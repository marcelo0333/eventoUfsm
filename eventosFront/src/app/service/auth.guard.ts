import {inject, Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot} from '@angular/router';
import { TokenService } from './auth.token.service';

@Injectable({
  providedIn: 'root'
  })
class AdminGuard {
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
    console.log("AuthGuard is being called");

    return true;
  }
}
  export const IsAdminGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean =>{
  return inject(AdminGuard).canActivate(route, state)
}

