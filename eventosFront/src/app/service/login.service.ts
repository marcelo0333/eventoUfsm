import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { LoginDTO, SignInResponse, UserModel } from "../../../../../projeto/mobile-app/src/app/shared/auth.data.transfer.object";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private API = `${environment.api}`;

  constructor(private http: HttpClient) { }

  login(req: LoginDTO): Observable<SignInResponse> {
    return this.http.post<SignInResponse>(`${this.API}/auth/login`, req);
  }

  // getUser(options: HttpHeaders): Observable<UserModel> {
  //   const httpOptions = { headers: options };
  //   return this.http.get<UserModel>(`${this.API}/user`, httpOptions);
  // }

}
