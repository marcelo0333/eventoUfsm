import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { IsAdminGuard} from "./service/auth.guard";

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./pages/tabs/tabs.module').then(m => m.TabsPageModule)

  },
  {
    path: 'loader',
    loadChildren: () => import('./pages/loader/loader.module').then( m => m.LoaderPageModule)
  },
  {
    path: 'login',
    loadChildren: () => import('./pages/login/login.module').then( m => m.LoginPageModule)

  },
  {
    path: 'register',
    loadChildren: () => import('./pages/register/register.module').then( m => m.RegisterPageModule)
  },
  // {
  //   path: 'home',
  //   loadChildren: () => import('./pages/home-page/home-page.module').then( m => m.HomePagePageModule)
  // },
  {
    path: 'events/:id',
    loadChildren: () => import('./pages/events/events.module').then(m => m.EventsPageModule)
  },
  {
    path: 'user',
    loadChildren: () => import('./pages/user/user.module').then( m => m.UserPageModule)
  },
  {
    path: 'category/:typeEvent',
    loadChildren: () => import('./pages/filter-item/filter-item.module').then( m => m.FilterItemPageModule)
  },
  {
    path: 'reminders',
    loadChildren: () => import('./pages/reminders/reminders.module').then( m => m.RemindersPageModule)
  },





];
@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
