import { AfterViewInit, Component, OnInit } from '@angular/core';
import Swiper from 'swiper';
import { EventService } from '../../service/event.service';
import { Event } from '../../models/events.model';
import {Router} from "@angular/router";

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.page.html',
  styleUrls: ['./home-page.page.scss'],
})
export class HomePagePage implements AfterViewInit, OnInit {

  events: Event[] = [];

  constructor(
    private eventService: EventService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadEvents();
  }

  categorys: any[] = [
    {
      imgAsset:'../../../assets/icon/concerts.png',
      CategoryName:'Concertos',
    },
    {
      imgAsset:'../../../assets/icon/light.png',
      CategoryName:'Cientificos',
    },
    {
      imgAsset:'../../../assets/icon/theater.png',
      CategoryName:'Teatro',
    },
    {
      imgAsset:'../../../assets/icon/training.png',
      CategoryName:'WorkShop',
    },
  ];

  loadEvents(): void {
    this.eventService.getEvents().subscribe(
      (data: Event[]) => {
        this.events = data;
        console.log('Eventos carregados:', this.events);
        this.initializeSwiper();
      },
      (error) => {
        console.error('Erro ao carregar eventos:', error);
        // Trate o erro de maneira apropriada, como exibir uma mensagem de erro para o usuário
      }
    );
  }

  initializeSwiper(): void {
    const swiper = new Swiper('.swiper-container', {
      loop: true,
      initialSlide: 0,
      speed: 400,
      pagination: {
        el: '.swiper-pagination',
        clickable: true,
      },
    });
  }

  swiperSlideChanged(e: any): void {
    console.log('Slide alterado:', e);
  }

  ngAfterViewInit(): void {
    this.initializeSwiper();
  }

  goToEventDetails(eventsId: bigint) {
    console.log(eventsId)
    this.router.navigate(['/events', eventsId])
  }
}
