import { Component, OnInit } from '@angular/core';
import { ReminderService } from '../../service/reminder.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TokenService } from '../../service/auth.token.service';
import { LocalNotifications } from '@awesome-cordova-plugins/local-notifications/ngx';
import { AndroidPermissions } from '@awesome-cordova-plugins/android-permissions/ngx';
import { Platform } from '@ionic/angular';
import {ReminderEventsDTO} from "../../models/auth.data.transfer.object";

@Component({
  selector: 'app-reminders',
  templateUrl: './reminders.page.html',
  styleUrls: ['./reminders.page.scss'],
})
export class RemindersPage implements OnInit {

  reminders: ReminderEventsDTO[] = [];
  userId!: number | undefined;

  constructor(
    private reminderService: ReminderService,
    private route: ActivatedRoute,
    private router: Router,
    private tokenService: TokenService,
    private localNotifications: LocalNotifications,
    private androidPermissions: AndroidPermissions,
    private platform: Platform
  ) { }

  ngOnInit() {
    this.userId = this.tokenService.getUserFromToken()?.userId;
    this.platform.ready().then(() => {
      if (this.platform.is('cordova')) {
        this.androidPermissions.checkPermission(this.androidPermissions.PERMISSION.RECEIVE_BOOT_COMPLETED).then(
          result => {
            if (!result.hasPermission) {
              this.androidPermissions.requestPermission(this.androidPermissions.PERMISSION.RECEIVE_BOOT_COMPLETED);
            }
          },
          err => this.androidPermissions.requestPermission(this.androidPermissions.PERMISSION.RECEIVE_BOOT_COMPLETED)
        );

        this.localNotifications.requestPermission().then(permission => {
          if (permission) {
            this.getEventsReminder();
          } else {
            console.error('Permissão para notificações negada');
          }
        });
      } else {
        console.warn('Cordova não está disponível - algumas funcionalidades podem não funcionar.');
        this.getEventsReminder();
      }
    });
  }

  getEventsReminder() {
    this.reminderService.getRemindersByUser(this.userId)
      .subscribe((data: ReminderEventsDTO[]) => {
          this.reminders = data;
          if (this.platform.is('cordova')) {
            this.scheduleNotifications(data);
          }
          console.log('Eventos carregados por reminders:', this.reminders);
        },
        (error) => {
          console.error('Erro ao carregar eventos por categoria:', error);
        });
  }

  scheduleNotifications(reminders: ReminderEventsDTO[]) {
    reminders.forEach(reminder => {
      const reminderTime = new Date(reminder.reminderTime).getTime();
      this.localNotifications.schedule({
        id: reminder.reminderId,
        title: 'Reminder',
        text: `Lembrete para o evento: ${reminder.events?.eventName}`,
        trigger: { at: new Date(reminderTime) },
        smallIcon: 'res://icon',
        icon: 'https://example.com/icon.png'
      });
    });
  }

  goToEventDetails(eventsId: bigint | undefined) {
    console.log(eventsId);
    this.router.navigate(['/events', eventsId]);
  }

  deleteReminder(reminderId: number | undefined) {
    this.reminderService.deleteReminder(reminderId)
      .subscribe(() => {
          this.reminders = this.reminders.filter(reminder => reminder.reminderId !== reminderId);
          console.log('Lembrete excluído com sucesso');
          if (this.platform.is('cordova')) {
            this.localNotifications.cancel(reminderId);  // Cancelar a notificação também
          }
        },
        (error) => {
          console.error('Erro ao excluir lembrete:', error);
        });
  }
}
