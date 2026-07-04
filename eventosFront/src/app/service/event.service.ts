import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {CommentsDTO, Event, Local, Locals, UserComments} from '../models/events.model';
import { Page } from '../models/events.model';
import { environment } from '../../environments/environment';
import {Bookmark, BookmarkDTO} from "../models/auth.data.transfer.object";
import {TokenService} from "./auth.token.service";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private apiUrl = `${environment.api}`;
  private recommendationApi = `${environment.recommendationApi}`;
  constructor(private http: HttpClient, private tokenService: TokenService) { }

  registerInteraction(userId: number, eventId: number, type: string): Observable<any> {
    const token = this.tokenService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  return this.http.post(`${this.apiUrl}/interactions/${userId}/${eventId}/${type}`, {headers});
  }

  getEventsRecommended(): Observable<Event[]> {
    const user = this.tokenService.getUserFromToken();
    const userId = user ? user.userId : null;

    return this.http.get<Page<Event>>(`${this.recommendationApi}/${userId}`)
    .pipe(
      map(page => page.content)
    );
  }

  getEvents(): Observable<Event[]> {
    return this.http.get<Page<Event>>(`${this.apiUrl}/events/date` )
      .pipe(
        map(page => page.content)
      );
  }
  getEventsBookmarks(): Observable<Event[]> {
    return this.http.get<Page<Event>>(`${this.apiUrl}/events/bookmarks` )
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
  getLocal(): Observable<Local[]>{
    return this.http.get<Local[]>(`${this.apiUrl}/local`);
  }

  register(eventData: any, imgEvent: File): Observable<any> {
    const token = this.tokenService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const formData: FormData = new FormData();
    formData.append('eventName', eventData.eventName);
    formData.append('imgEvent', imgEvent);
    formData.append('typeEvent', eventData.typeEvent);
    formData.append('description', eventData.description);
    formData.append('dateInitial', eventData.dateInitial);
    formData.append('dateFinal', eventData.dateFinal);
    formData.append('centerName', eventData.centerName);
    formData.append('contact', eventData.contact);
    formData.append('userId', eventData.userId.toString());
    formData.append('localIds', JSON.stringify(eventData.localIds));

    console.log(formData.get(eventData.userId));
    return this.http.post(`${this.apiUrl}/events/save`, formData, { headers });
  }
  saveEventBookmarked(bookmark: BookmarkDTO): Observable<any> {
    const token = this.tokenService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.post(`${this.apiUrl}/bookmarks/save`, bookmark, { headers });
  }

  getUserBookmarks(userId: string): Observable<Event[]> {
    const token = this.tokenService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<Event[]>(`${this.apiUrl}/bookmarks/${userId}`, { headers });
  }
  wipeBookmark(userId: number | undefined, eventId: bigint): Observable<any> {
    const token = this.tokenService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.delete(`${this.apiUrl}/bookmarks/delete/${userId}/${eventId}`, { headers });
  }
  wipeEvent(eventId: bigint): Observable<any> {
    const token = this.tokenService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const params = new HttpParams().set('id', eventId?.toString())
    return this.http.delete(`${this.apiUrl}/events/delete`, { headers, params });
  }
  getUserHasBookmarked(userId: number | undefined, eventId: bigint): Observable<boolean> {
    const token = this.tokenService.getAccessToken()
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<boolean>(`${this.apiUrl}/bookmarks/${userId}/${eventId}`, { headers });
  }
  getUserHasCreated(userId: number | undefined, eventId: bigint): Observable<boolean> {
    const token = this.tokenService.getAccessToken()
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const params = new HttpParams().set('userId', userId?.toString() || '').set('eventId', eventId?.toString());
    return this.http.get<boolean>(`${this.apiUrl}/events/check-event`, {  headers, params });
  }
  searchEvents(query: string):Observable<Event[]>{
      return this.http.get<Event[]>(`${this.apiUrl}/events/search`, {params: {query}})
  }
  getEventsByType(typeEvent: string): Observable<Event[]> {
    const params = new HttpParams().set('type', typeEvent);
    return this.http.get<Event[]>(`${this.apiUrl}/events/type`, { params });
  }

  editEvent(eventId: bigint,eventData: any, imgEvent: File): Observable<any> {
    const token = this.tokenService.getAccessToken();
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    const formData: FormData = new FormData();
    formData.append('eventId', eventId?.toString());
    formData.append('eventName', eventData.eventName);
    formData.append('imgEvent', imgEvent);
    formData.append('typeEvent', eventData.typeEvent);
    formData.append('description', eventData.description);
    formData.append('dateInitial', eventData.dateInitial);
    formData.append('dateFinal', eventData.dateFinal);
    formData.append('centerName', eventData.centerName);
    formData.append('contact', eventData.contact);
    formData.append('userId', eventData.userId.toString());
    formData.append('localIds', JSON.stringify(eventData.localIds));

    console.log(formData.get(eventData.userId));
    return this.http.put(`${this.apiUrl}/events/edit`, formData, { headers });
  }


}
