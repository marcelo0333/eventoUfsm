import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { HomePagePageRoutingModule } from './home-page-routing.module';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HomePagePage } from './home-page.page';
import {ExploreContainerComponentModule} from "../../explore-container/explore-container.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    IonicModule,
    HomePagePageRoutingModule,
    ExploreContainerComponentModule,
  ],
  declarations: [HomePagePage],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],

})
export class HomePagePageModule {}
