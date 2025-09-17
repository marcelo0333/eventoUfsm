import { AfterViewInit, Component, OnInit } from '@angular/core';
import Swiper from 'swiper';
import { EventService } from '../../service/event.service';
import { Event } from '../../models/events.model';
import { Router } from "@angular/router";
import { TokenService } from "../../service/auth.token.service";
import { UserModel } from "../../models/auth.data.transfer.object";
import { FormControl } from "@angular/forms";
import { debounceTime } from "rxjs";

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
  categorys: any[] = [
    {
      imgAsset: '../../../assets/icon/concerts.png',
      CategoryName: 'Culturais',
      id: 5,

    },
    {
      imgAsset: '../../../assets/icon/light.png',
      CategoryName: 'Técnico-Científicos',
      id: 1,
    },
    {
      imgAsset: '../../../assets/icon/theater.png',
      CategoryName: 'Artísticos',
      id: 2,
    },
    {
      imgAsset: '../../../assets/icon/work-team.png',
      CategoryName: 'Profissionais',
      id: 7,
    },
    {
      imgAsset: '../../../assets/icon/training.png',
      CategoryName: 'Oficiais',
      id: 4,
    },
    {
      imgAsset: '../../../assets/icon/fair-trade.png',
      CategoryName: 'Sociais',
      id: 3,

    },
  ];
  private loading: boolean = false;

  constructor(
    private eventService: EventService,
    private router: Router,
    private tokenService: TokenService,
  ) { }

  ngOnInit(): void {
    const userValide = this.tokenService.sessionIsValid();

    if (userValide) {
      console.log("valido");
      this.loadEvents();
    } else {
      console.error("não é valido");
    }
    this.loadEvents();
    this.setupSearch();
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

  goToCategory(id: bigint) {
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
