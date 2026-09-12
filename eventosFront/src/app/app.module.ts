import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouteReuseStrategy } from '@angular/router';

import { IonicModule, IonicRouteStrategy } from '@ionic/angular';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {HttpClientModule, HTTP_INTERCEPTORS, provideHttpClient, withInterceptors} from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Serviços
import { TokenService } from './service/auth.token.service';
import { authInterceptor } from './service/auth-interceptor.service';

// Componentes
import { ReminderModalComponent } from './pages/reminder-modal/reminder-modal.component';

import {DatePipe} from "@angular/common";

@NgModule({
  declarations: [
    AppComponent,
    ReminderModalComponent
  ],
  imports: [
    BrowserModule,
    IonicModule.forRoot(),
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    provideHttpClient(withInterceptors([authInterceptor]),),
    DatePipe
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
