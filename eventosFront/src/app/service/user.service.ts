import {HttpClient, HttpHeaders} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserModel } from "../models/auth.data.transfer.object";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UserDetailService {

  private API = `${environment.api}/auth`;

  constructor(private http: HttpClient) { }

  public editUser(userModel: UserModel): Observable<any> {
    const token = localStorage.getItem('token_key');
    const accessToken = token ? JSON.parse(token).accessToken : null;
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${accessToken}`
    });
    return this.http.put<any>(`${this.API}/edit`, userModel, { headers });
  }

  anyComparator(a: any, b: any): boolean {
    return a.id === b.id; // true if two objects are equal
  }

  jsonComparator(a: any, b: any): boolean {
    return JSON.stringify(a) === JSON.stringify(b); // true if two objects are equal
  }

}
