import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { EventsCreatorPageRoutingModule } from './events-creator-routing.module';

import { EventsCreatorPage } from './events-creator.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    EventsCreatorPageRoutingModule,
    ReactiveFormsModule
  ],
  declarations: [EventsCreatorPage]
})
export class EventsCreatorPageModule {}
