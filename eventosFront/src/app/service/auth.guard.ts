import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from './auth.token.service';

/**
 * Exige uma sessão válida (token não expirado). Redireciona para /login caso contrário.
 * Aplicado às rotas protegidas do app (/tabs e afins).
 */
export const authGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  const tokens = tokenService.getTokenParsed();
  if (tokens?.accessToken && tokenService.tokenValid(tokens.accessToken)) {
    return true;
  }
  router.navigate(['/login']);
  return false;
};

/**
 * Exige que o usuário autenticado tenha papel ADMIN. Aplicado a rotas administrativas
 * (ex.: criação/edição de eventos). Defesa em profundidade — a autorização real é no backend.
 */
export const adminGuard: CanActivateFn = () => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  const tokens = tokenService.getTokenParsed();
  const hasValidSession = tokens?.accessToken && tokenService.tokenValid(tokens.accessToken);

  if (hasValidSession && tokenService.isAdmin()) {
    return true;
  }
  router.navigate(hasValidSession ? ['/'] : ['/login']);
  return false;
};

// Compatibilidade com imports existentes.
export const IsAdminGuard: CanActivateFn = authGuard;
