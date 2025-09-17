import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {ExploreContainerComponentModule} from "../../explore-container/explore-container.module";
import { SlidesComponent } from './slides.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    IonicModule,
    ExploreContainerComponentModule,
],
  declarations: [SlidesComponent],
  exports: [SlidesComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],

})
export class SlidesModule {}
