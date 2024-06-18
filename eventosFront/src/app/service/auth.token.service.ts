import { Injectable } from "@angular/core";
import { environment } from '../../environments/environment';
import { JwtPayload, jwtDecode } from 'jwt-decode';
import { HttpClient, HttpErrorResponse } from "@angular/common/http";
import { Observable } from "rxjs";
import { Router } from "@angular/router";
import { ToastController } from '@ionic/angular/standalone';
import { TokensResponse, UserModel } from "../models/auth.data.transfer.object";

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private API = `${environment.api}`;

  constructor(
    private http: HttpClient,
    private router: Router,
    private toastController: ToastController
  ) { }

  /// User related functions

  getUserFromToken(): UserModel | null {
    const user = this.getUser();
    return user ? JSON.parse(user) : null;
  }

  getUser(): string | null {
    return localStorage.getItem(environment.userKey);
  }

  setUser(user: UserModel): void {
    localStorage.setItem(environment.userKey, JSON.stringify(user));
  }

  removeUser(): void {
    localStorage.removeItem(environment.userKey);
  }

  /// Token related functions

  getTokens(): string | null {
    return localStorage.getItem(environment.tokenKey);
  }

  setTokens(tokens: TokensResponse): void {
    localStorage.setItem(environment.tokenKey, JSON.stringify(tokens));
  }

  removeTokens(): void {
    localStorage.removeItem(environment.tokenKey);
  }

  getTokenParsed(): TokensResponse | null {
    const token = this.getTokens();
    return token ? JSON.parse(token) : null;
  }

  getTokenDecoded(token: string): JwtPayload | null {
    return token ? jwtDecode(token) : null;
  }

  getRefreshedToken(): Observable<TokensResponse> {
    return this.http.post<TokensResponse>(`${this.API}/auth/refresh-token`, { responseType: "json" });
  }

  setRefreshedToken(): void {
    this.getRefreshedToken().subscribe({
      next: (tokensResponse: TokensResponse) => {
        this.removeTokens();
        this.setTokens(tokensResponse);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error refreshing token:', error);
      }
    });
  }

  tokenValid(token: string): boolean {
    const decodedToken = this.getTokenDecoded(token);
    return decodedToken?.exp ? (decodedToken.exp * 1000) > Date.now() : false;
  }

  sessionIsValid(): boolean {
    const tokens = this.getTokenParsed();
    const accessToken = tokens?.accessToken;
    const refreshToken = tokens?.refreshToken;

    const accessTokenValid = accessToken ? this.tokenValid(accessToken) : false;
    const refreshTokenValid = refreshToken ? this.tokenValid(refreshToken) : false;

    if (accessTokenValid) {
      return true;
    }

    if (refreshTokenValid) {
      this.setRefreshedToken();
      return true;
    }

    this.handleSessionExpired();
    return false;
  }

  handleSessionExpired(): void {
    this.router.navigate(['login']);
    this.removeTokens();
    this.showErrorToast('Tokens have expired. Session is terminated.');
  }

  storageClear(): void {
    localStorage.clear();
  }

  private showErrorToast(message: string): void {
    const toastOptions = {
      animated: true,
      message,
      duration: 4000,
      buttons: [{ role: 'cancel', text: 'Dismiss' }],
      color: 'danger'
    };

    this.toastController.create(toastOptions).then(toast => toast.present());
  }

}
