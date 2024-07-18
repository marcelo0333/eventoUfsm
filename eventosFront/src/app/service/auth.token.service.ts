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
        this.handleSessionExpired();
      }
    });
  }

  tokenValid(token: string): boolean {
    const decodeToken: JwtPayload | null = jwtDecode(token)

    if (decodeToken?.exp) {
      return (decodeToken.exp * 2000) > (new Date().getTime());
    }

    return false;
  }

  sessionIsValid(): boolean {

    const accessToken: (string | undefined) = this.getTokenParsed()?.accessToken;
    const refreshToken: (string | undefined) = this.getTokenParsed()?.refreshToken;

    let accessTokenValid: boolean = false;
    let refreshTokenValid: boolean = false;
    const sessionHasFinished = (!accessTokenValid && !refreshTokenValid);

    if (accessToken && refreshToken) {
      accessTokenValid = this.tokenValid(accessToken);
      refreshTokenValid = this.tokenValid(refreshToken);
    }

    if (accessTokenValid) {
      return true;
    }

    if (refreshTokenValid) {
      this.setRefreshedToken();
      return true;
    }

    if (sessionHasFinished) {
      this.router.navigate([`login`]);
      localStorage.removeItem(environment.tokenKey);
      this.showErrorToast(`Tokens have expired. Session is terminated`); //
      return false;
    }

    return false;
  }

  handleSessionExpired(): void {
    this.removeTokens();
    this.router.navigate(['login']);
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

  getAccessToken(): string | null {
    const tokenData = localStorage.getItem(environment.tokenKey);
    if (tokenData) {
      const parsedToken = JSON.parse(tokenData);
      return parsedToken.accessToken;
    }
    return null;
  }

  isAdmin(): boolean {
    const user = this.getUserFromToken();
    return user ? user.role === 'ADMIN' : false;
  }
}
