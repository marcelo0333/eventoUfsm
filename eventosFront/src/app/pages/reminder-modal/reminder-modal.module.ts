import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';

import { ReminderModalComponent } from './reminder-modal.component';

@NgModule({
  declarations: [ReminderModalComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    IonicModule
  ],
  exports: [ReminderModalComponent]
})
export class ReminderModalModule {}
