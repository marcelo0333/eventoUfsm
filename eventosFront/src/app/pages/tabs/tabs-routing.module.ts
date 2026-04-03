import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TabsPage } from './tabs.page';
import {IsAdminGuard} from "../../service/auth.guard";

export const routes: Routes = [
  {
    path: 'tabs',  canActivate: [IsAdminGuard],
    component: TabsPage,
    children: [
      {
        path: 'home',
        loadChildren: () => import('../home-page/home-page.module').then(m => m.HomePagePageModule)
      },
      {
        path: 'bookmarks',
        loadChildren: () => import('../filter-item/filter-item.module').then(m => m.FilterItemPageModule)
      },
      {
        path: 'reminders',
        loadChildren: () => import('../reminders/reminders.module').then(m => m.RemindersPageModule)
      },
      {
        path: 'user',
        loadChildren: () => import('../user/user.module').then(m => m.UserPageModule)
      },
      {
        path: 'category/:typeEvent',
        loadChildren: () => import('../filter-item/filter-item.module').then(m => m.FilterItemPageModule)
      },
      {
        path: 'bookmarks/:userId',
        loadChildren: () => import('../filter-item/filter-item.module').then(m => m.FilterItemPageModule)
      },
      {
        path: 'events-creator',
        loadChildren: () => import('../events-creator/events-creator.module').then( m => m.EventsCreatorPageModule)
      },
      {
        path: '',
        redirectTo: '/tabs/home',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    redirectTo: '/tabs/home',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TabsPageRoutingModule {}
