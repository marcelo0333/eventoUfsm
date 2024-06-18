import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { TokensResponse, UserModel } from "../models/auth.data.transfer.object";
import { Injectable } from "@angular/core";
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) { }

  private apiUrl = `${environment.api}`;

  register(user: UserModel): Observable<TokensResponse> {
    return this.http.post<TokensResponse>(`${this.apiUrl}/auth/register`, user);
  }

}
