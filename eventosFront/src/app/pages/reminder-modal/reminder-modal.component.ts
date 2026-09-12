import { Component, OnInit, Input } from '@angular/core';
import { ModalController, ToastController } from '@ionic/angular';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Capacitor } from '@capacitor/core';
import { LocalNotifications } from '@capacitor/local-notifications';
import { ReminderService } from '../../service/reminder.service';
import { ReminderDTO } from '../../models/auth.data.transfer.object';

@Component({
  selector: 'app-reminder-modal',
  templateUrl: './reminder-modal.component.html',
  styleUrls: ['./reminder-modal.component.scss'],
})
export class ReminderModalComponent implements OnInit {
  reminderForm!: FormGroup;
  @Input() userId!: number;
  @Input() eventId!: number;
  @Input() eventName?: string;
  @Input() dateFinal!: Date;
  minDateTime!: string;
  maxDateTime!: string;

  constructor(
    private modalController: ModalController,
    private formBuilder: FormBuilder,
    private reminderService: ReminderService,
    private toastController: ToastController
  ) {}

  ngOnInit() {
    // dateFinal chega do backend como string (não uma instância real de Date),
    // por isso passa por `new Date(...)` antes de converter pra ISO.
    this.minDateTime = new Date().toISOString();
    this.maxDateTime = new Date(this.dateFinal).toISOString();

    // O valor inicial precisa ser uma data válida dentro do range (hoje) — com
    // '' o ion-datetime não consegue interpretar o valor e abre num mês arbitrário
    // em vez de partir do dia atual.
    this.reminderForm = this.formBuilder.group({
      reminderDateTime: [this.minDateTime, Validators.required]
    });
  }

  dismiss() {
    this.modalController.dismiss();
  }

  setReminder() {
    if (this.reminderForm.valid) {
      const reminderDateTime = this.reminderForm.value.reminderDateTime;

      const reminder: ReminderDTO = {
        userId: this.userId,
        eventId: this.eventId,
        reminderTime: reminderDateTime,
      };

      this.reminderService.saveReminder(reminder).subscribe({
        next: async (response) => {
          await this.scheduleNotification(response?.reminderId, reminderDateTime);
          this.modalController.dismiss({ reminderDateTime });
        },
        error: () => this.showErrorToast('Não foi possível salvar o lembrete. Tente novamente.')
      });
    }
  }

  // Agenda a notificação local já no momento da criação — sem isso, o lembrete só
  // dispararia se o usuário passasse pela aba "Lembretes" depois (onde também é
  // reagendado, com o mesmo id, então não duplica).
  private async scheduleNotification(reminderId: number | undefined, reminderDateTime: string): Promise<void> {
    if (reminderId == null || !Capacitor.isNativePlatform()) {
      return;
    }
    try {
      let status = await LocalNotifications.checkPermissions();
      if (status.display !== 'granted') {
        status = await LocalNotifications.requestPermissions();
      }
      if (status.display !== 'granted') {
        return;
      }
      await LocalNotifications.schedule({
        notifications: [{
          id: reminderId,
          title: 'Lembrete de evento',
          body: `Não perca: ${this.eventName ?? 'seu evento'}`,
          schedule: { at: new Date(reminderDateTime) },
        }]
      });
    } catch (err) {
      console.error('Falha ao agendar notificação local', err);
    }
  }

  private showErrorToast(message: string): void {
    this.toastController.create({
      message,
      duration: 4000,
      buttons: [{ role: 'cancel', text: 'OK' }],
      color: 'danger'
    }).then(toast => toast.present());
  }
}
