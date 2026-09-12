import { Component, OnInit } from '@angular/core';
import { register } from 'swiper/element/bundle';
import { Capacitor } from '@capacitor/core';
import { App } from '@capacitor/app';
import { StatusBar, Style } from '@capacitor/status-bar';
import { SplashScreen } from '@capacitor/splash-screen';
import { Router } from '@angular/router';
import { NavController } from '@ionic/angular';
import { Observable } from 'rxjs';
import { NetworkService } from './service/network.service';

register();

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
})
export class AppComponent implements OnInit {

  readonly online$: Observable<boolean>;

  constructor(
    private router: Router,
    private navController: NavController,
    private networkService: NetworkService,
  ) {
    this.online$ = this.networkService.online$;
  }

  async ngOnInit(): Promise<void> {
    if (!Capacitor.isNativePlatform()) {
      return;
    }
    await this.initializeNative();
  }

  private async initializeNative(): Promise<void> {
    // Barra de status alinhada ao tema (azul institucional).
    try {
      await StatusBar.setStyle({ style: Style.Dark });
      if (Capacitor.getPlatform() === 'android') {
        await StatusBar.setBackgroundColor({ color: '#1565c0' });
      }
    } catch { /* StatusBar indisponível em alguns dispositivos */ }

    // Botão físico de voltar do Android: volta na pilha ou minimiza o app na raiz.
    App.addListener('backButton', ({ canGoBack }) => {
      const atRoot = this.router.url === '/tabs/home' || this.router.url === '/';
      if (canGoBack && !atRoot) {
        this.navController.back();
      } else {
        App.exitApp();
      }
    });

    // Esconde a splash depois que a UI está pronta.
    try {
      await SplashScreen.hide();
    } catch { /* sem splash em ambiente sem plugin */ }
  }
}
