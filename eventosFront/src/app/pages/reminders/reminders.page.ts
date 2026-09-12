import { Component, OnInit } from '@angular/core';
import { ReminderService } from '../../service/reminder.service';
import { Router } from '@angular/router';
import { TokenService } from '../../service/auth.token.service';
import { Capacitor } from '@capacitor/core';
import { LocalNotifications } from '@capacitor/local-notifications';
import { ReminderEventsDTO } from "../../models/auth.data.transfer.object";

@Component({
  selector: 'app-reminders',
  templateUrl: './reminders.page.html',
  styleUrls: ['./reminders.page.scss'],
})
export class RemindersPage implements OnInit {

  reminders: ReminderEventsDTO[] = [];
  userId!: number | undefined;
  loading = false;

  private notificationsEnabled = false;

  constructor(
    private reminderService: ReminderService,
    private router: Router,
    private tokenService: TokenService,
  ) { }

  async ngOnInit(): Promise<void> {
    this.userId = this.tokenService.getUserFromToken()?.userId;
    await this.ensureNotificationPermission();
    this.getEventsReminder();
  }

  private async ensureNotificationPermission(): Promise<void> {
    if (!Capacitor.isNativePlatform()) {
      return;
    }
    try {
      let status = await LocalNotifications.checkPermissions();
      if (status.display !== 'granted') {
        status = await LocalNotifications.requestPermissions();
      }
      this.notificationsEnabled = status.display === 'granted';
    } catch (err) {
      console.error('Falha ao solicitar permissão de notificações', err);
      this.notificationsEnabled = false;
    }
  }

  getEventsReminder(): void {
    this.loading = true;
    this.reminderService.getRemindersByUser(this.userId).subscribe({
      next: (data: ReminderEventsDTO[]) => {
        this.reminders = data ?? [];
        this.loading = false;
        this.scheduleNotifications(this.reminders);
      },
      error: (error) => {
        this.loading = false;
        console.error('Erro ao carregar lembretes:', error);
      }
    });
  }

  private async scheduleNotifications(reminders: ReminderEventsDTO[]): Promise<void> {
    if (!this.notificationsEnabled) {
      return;
    }
    const now = Date.now();
    const notifications = reminders
      .filter(r => r.reminderId != null && new Date(r.reminderTime).getTime() > now)
      .map(r => ({
        id: Number(r.reminderId),
        title: 'Lembrete de evento',
        body: `Não perca: ${r.events?.eventName ?? 'seu evento'}`,
        schedule: { at: new Date(r.reminderTime) },
      }));

    if (notifications.length) {
      try {
        await LocalNotifications.schedule({ notifications });
      } catch (err) {
        console.error('Falha ao agendar notificações', err);
      }
    }
  }

  goToEventDetails(eventsId: bigint | undefined): void {
    this.router.navigate(['/events', eventsId]);
  }

  deleteReminder(reminderId: number | undefined): void {
    if (reminderId == null) {
      return;
    }
    this.reminderService.deleteReminder(reminderId).subscribe({
      next: async () => {
        this.reminders = this.reminders.filter(r => r.reminderId !== reminderId);
        if (Capacitor.isNativePlatform()) {
          try {
            await LocalNotifications.cancel({ notifications: [{ id: Number(reminderId) }] });
          } catch { /* já cancelada / inexistente */ }
        }
      },
      error: (error) => console.error('Erro ao excluir lembrete:', error)
    });
  }
}
