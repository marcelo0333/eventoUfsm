import { HttpClient } from "@angular/common/http";
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
    // O token é anexado automaticamente pelo authInterceptor.
    return this.http.put<any>(`${this.API}/edit`, userModel);
  }

  anyComparator(a: any, b: any): boolean {
    return a.id === b.id; // true if two objects are equal
  }

  jsonComparator(a: any, b: any): boolean {
    return JSON.stringify(a) === JSON.stringify(b); // true if two objects are equal
  }

}
