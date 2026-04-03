import { AfterViewInit, Component, OnInit } from '@angular/core';
import Swiper from 'swiper';
import { EventService } from '../../service/event.service';
import { Event } from '../../models/events.model';
import { Router } from "@angular/router";
import { TokenService } from "../../service/auth.token.service";
import { UserModel } from "../../models/auth.data.transfer.object";
import { FormControl } from "@angular/forms";
import { debounceTime } from "rxjs";
import { CATEGORIES } from 'src/app/constants/categories.constant';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.page.html',
  styleUrls: ['./home-page.page.scss'],
})
export class HomePagePage implements AfterViewInit, OnInit {

  events: Event[] = [];
  eventsBookmarks: Event[] = [];

  user!: UserModel;
  searchControl: FormControl = new FormControl('');
  searchResults: Event[] = [];
  categorys = CATEGORIES;

  private loading: boolean = false;

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

  loadEvents(): void {
    this.loading = true;
    this.eventService.getEvents().subscribe(
      (data: Event[]) => {
        this.events = data;
        console.log('Eventos carregados:', this.events);
        this.loading = false;
      },
      (error) => {
        this.loading = false;
        console.error('Erro ao carregar eventos:', error);
      }
    );
    this.eventService.getEventsBookmarks().subscribe(
      (data: Event[]) => {
        this.eventsBookmarks = data;
        console.log('Eventos carregados:', this.eventsBookmarks);
        this.loading = false;
      },
      (error) => {
        this.loading = false;
        console.error('Erro ao carregar eventos:', error);
      }
    );
  }


  swiperSlideChanged(e: any): void {
    console.log('Slide alterado:', e);
  }

  ngAfterViewInit(): void {
  }
    goToEventDetails(eventsId: bigint) {
    console.log(eventsId);
    this.router.navigate(['/events', eventsId]);
  }

  goToCategory(id: number) {
    console.log(id);
    this.router.navigate(['tabs/category', id]);
  }
  doRefresh(event: any): void {
    this.loadEvents();
    setTimeout(() => {
      event.target.complete();
    }, 3000);
  }
}
