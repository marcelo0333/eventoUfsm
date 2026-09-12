import { AfterViewInit, Component, OnInit } from '@angular/core';
import Swiper from 'swiper';
import { EventService } from '../../service/event.service';
import { Event } from '../../models/events.model';
import { Router } from "@angular/router";
import { TokenService } from "../../service/auth.token.service";
import { UserModel } from "../../models/auth.data.transfer.object";
import { FormControl } from "@angular/forms";
import { debounceTime, forkJoin, of } from "rxjs";
import { catchError, finalize } from "rxjs/operators";
import { CATEGORIES } from 'src/app/constants/categories.constant';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.page.html',
  styleUrls: ['./home-page.page.scss'],
})
export class HomePagePage implements AfterViewInit, OnInit {

  events: Event[] = [];
  eventsBookmarks: Event[] = [];
  eventsRecommended: Event[] = [];

  user!: UserModel;
  searchControl: FormControl = new FormControl('');
  searchResults: Event[] = [];
  categorys = CATEGORIES;

  loadingEvents = false;
  loadingRecommended = false;
  loadingBookmarks = false;

  constructor(
    private eventService: EventService,
    private router: Router,
    private tokenService: TokenService,

  ) { }

  ngOnInit(): void {
    if (this.tokenService.sessionIsValid()) {
      this.loadEvents();
      this.setupSearch();
    } else {
      console.error('Sessão inválida');
      this.router.navigate(['/login']);
    }
  }

  setupSearch() {
    this.searchControl.valueChanges.pipe(
      debounceTime(300)
    ).subscribe(query => {
      if (query) {
        this.eventService.searchEvents(query).subscribe(
          res => {
            this.searchResults = res;
          }, error => {
            console.error('erro ao buscar', error);
          }
        );
      } else {
        this.searchResults = [];
      }
    });
  }

  loadEvents(refresher?: any): void {
    this.loadingEvents = true;
    this.loadingRecommended = true;
    this.loadingBookmarks = true;

    // Cada chamada é resiliente: uma falha (ex.: API de recomendação fora)
    // não derruba as demais seções.
    const events$ = this.eventService.getEvents().pipe(
      catchError((err) => { console.error('Erro ao carregar eventos:', err); return of([] as Event[]); }),
      finalize(() => (this.loadingEvents = false))
    );
    const bookmarks$ = this.eventService.getEventsBookmarks().pipe(
      catchError((err) => { console.error('Erro ao carregar favoritos:', err); return of([] as Event[]); }),
      finalize(() => (this.loadingBookmarks = false))
    );
    const recommended$ = this.eventService.getEventsRecommended().pipe(
      catchError((err) => { console.error('Erro ao carregar recomendados:', err); return of([] as Event[]); }),
      finalize(() => (this.loadingRecommended = false))
    );

    forkJoin({ events: events$, bookmarks: bookmarks$, recommended: recommended$ }).subscribe({
      next: ({ events, bookmarks, recommended }) => {
        this.events = events;
        this.eventsBookmarks = bookmarks;
        this.eventsRecommended = recommended;
      },
      // Completa o pull-to-refresh apenas quando as respostas reais chegam.
      complete: () => refresher?.target?.complete(),
    });
  }

  ngAfterViewInit(): void {
  }

  goToEventDetails(eventsId: bigint) {
    this.router.navigate(['/events', eventsId]);
  }

  goToCategory(id: number) {
    this.router.navigate(['tabs/category', id]);
  }

  doRefresh(event: any): void {
    this.loadEvents(event);
  }
}
