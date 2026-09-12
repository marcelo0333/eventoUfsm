import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Capacitor } from '@capacitor/core';
import { Network } from '@capacitor/network';

/**
 * Observa a conectividade do dispositivo. Funciona tanto no navegador
 * (fallback via window.navigator.onLine) quanto no app nativo (@capacitor/network).
 */
@Injectable({ providedIn: 'root' })
export class NetworkService {

  private readonly onlineSubject = new BehaviorSubject<boolean>(true);
  readonly online$: Observable<boolean> = this.onlineSubject.asObservable();

  constructor() {
    this.initialize();
  }

  get isOnline(): boolean {
    return this.onlineSubject.value;
  }

  private async initialize(): Promise<void> {
    if (Capacitor.isNativePlatform()) {
      const status = await Network.getStatus();
      this.onlineSubject.next(status.connected);
      Network.addListener('networkStatusChange', (s) => this.onlineSubject.next(s.connected));
    } else {
      this.onlineSubject.next(navigator.onLine);
      window.addEventListener('online', () => this.onlineSubject.next(true));
      window.addEventListener('offline', () => this.onlineSubject.next(false));
    }
  }
}
