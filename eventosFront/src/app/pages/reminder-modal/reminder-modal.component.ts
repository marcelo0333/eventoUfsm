import { Component, OnInit, Input } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
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
  @Input() dateFinal!: Date;
  minDateTime!: string;
  maxDateTime!: string;

  constructor(
    private modalController: ModalController,
    private formBuilder: FormBuilder,
    private reminderService: ReminderService
  ) {}

  ngOnInit() {
    this.reminderForm = this.formBuilder.group({
      reminderDateTime: ['', Validators.required]
    });

    this.minDateTime = new Date().toISOString();
    this.maxDateTime = this.dateFinal.toISOString();
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

      this.reminderService.saveReminder(reminder)
        .subscribe(response =>  {
          console.log('Lembrete salvo:', reminderDateTime);
          this.modalController.dismiss({ reminderDateTime });
        });
    }
  }
}
