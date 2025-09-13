import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import { environment } from '../../environments/environment';
import {ReminderDTO, ReminderEventsDTO} from "../models/auth.data.transfer.object";



@Injectable({
  providedIn: 'root'
})
export class ReminderService {

  private apiUrl = `${environment.api}/reminders`;

  constructor(private http: HttpClient) { }

  saveReminder(reminder: ReminderDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/save`, reminder)
      .pipe(
        catchError(this.handleError)
      );
  }
  private handleError(error: HttpErrorResponse) {
    console.error('Ocorreu um erro:', error);
    return throwError('Algo deu errado; por favor, tente novamente mais tarde.');
  }

  getRemindersByUser(userId: number | undefined): Observable<ReminderEventsDTO[]> {
    return this.http.get<ReminderEventsDTO[]>(`${this.apiUrl}/user/${userId}`);
  }

  deleteReminder(reminderId: number | undefined): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${reminderId}`);
  }
}
