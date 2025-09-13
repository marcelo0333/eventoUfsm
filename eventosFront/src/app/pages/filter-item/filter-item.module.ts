import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { FilterItemPageRoutingModule } from './filter-item-routing.module';

import { FilterItemPage } from './filter-item.page';
import {ExploreContainerComponentModule} from "../../explore-container/explore-container.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    FilterItemPageRoutingModule,
    ExploreContainerComponentModule,
    ReactiveFormsModule
  ],
  declarations: [FilterItemPage],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class FilterItemPageModule {}
