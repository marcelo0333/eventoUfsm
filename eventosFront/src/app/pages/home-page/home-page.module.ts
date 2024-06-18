import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { HomePagePageRoutingModule } from './home-page-routing.module';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HomePagePage } from './home-page.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HomePagePageRoutingModule,
  ],
  declarations: [HomePagePage],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],

})
export class HomePagePageModule {}
