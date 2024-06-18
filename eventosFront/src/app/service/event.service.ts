import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {Event, Local} from '../models/events.model';
import { Page } from '../models/events.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private apiUrl = `${environment.api}`;

  constructor(private http: HttpClient) { }

  getEvents(): Observable<Event[]> {
    return this.http.get<Page<Event>>(`${this.apiUrl}/events/date` )
      .pipe(
        map(page => page.content)
      );
  }
  getEventById(id: string): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/events/${id}`);
  }

  getEventAndLocal(id: string): Observable<Local> {
    return this.http.get<Local>(`${this.apiUrl}/local/events/${id}`);
  }
}
