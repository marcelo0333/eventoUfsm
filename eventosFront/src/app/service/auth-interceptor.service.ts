import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';

// Endpoints públicos que não devem receber o header Authorization.
const PUBLIC_PATHS = ['/auth/login', '/auth/register', '/auth/refresh-token'];

// Lê o accessToken do blob JSON guardado sob environment.tokenKey.
// (Injetar TokenService aqui causaria dependência circular com HttpClient.)
function readAccessToken(): string | null {
  const raw = localStorage.getItem(environment.tokenKey);
  if (!raw) {
    return null;
  }
  try {
    return JSON.parse(raw).accessToken ?? null;
  } catch {
    return null;
  }
}

export const authInterceptor: HttpInterceptorFn = (request, next) => {

  let router = inject(Router);

  const isPublic = PUBLIC_PATHS.some((path) => request.url.includes(path));
  const token = readAccessToken();

  if (token && !isPublic) {
    request = request.clone({
      setHeaders: { Authorization: 'Bearer ' + token },
    });
  }

  return next(request).pipe(
    catchError((err: any) => {
      if (err instanceof HttpErrorResponse) {
        if (err.status === 401) {
            router.navigate(['/login']);
        } else if (err.status === 403) {
          router.navigate(['/login']);
        } else {

          console.error('HTTP error:', err);
        }


      } else {
        console.error('An error occurred:', err);
      }

      return throwError(() => err);
    })
  );
};
