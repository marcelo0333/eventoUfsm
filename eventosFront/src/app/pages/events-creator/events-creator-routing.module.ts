import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { EventsCreatorPage } from './events-creator.page';

const routes: Routes = [
  {
    path: '',
    component: EventsCreatorPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EventsCreatorPageRoutingModule {}
