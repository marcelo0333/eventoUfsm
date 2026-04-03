import { Component, OnInit } from '@angular/core';
import { EventService } from "../../service/event.service";
import { ActivatedRoute, Router } from "@angular/router";
import { Event } from "../../models/events.model";
import { TokenService } from "../../service/auth.token.service";
import {Bookmark} from "../../models/auth.data.transfer.object";
import { CATEGORIES } from 'src/app/constants/categories.constant';

@Component({
  selector: 'app-filter-item',
  templateUrl: './filter-item.page.html',
  styleUrls: ['./filter-item.page.scss'],
})
export class FilterItemPage implements OnInit {

  categoryType!: string;
  events: (Event | undefined)[] = [];
  userBookmarks: Bookmark[] = [];
  category: any;
  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private router: Router,
    private tokenService: TokenService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      if (params.get('typeEvent')) {
        this.categoryType = params.get('typeEvent')!;
        this.category = CATEGORIES.find(cat => cat.id.toString() === this.categoryType);
        this.loadEventsByCategory();
      } else {
        const userId = this.tokenService.getUserFromToken()?.userId;
        if (userId) {
          this.loadUserBookmarks(userId.toString());
        }
      }
    });
  }

  loadEventsByCategory() {
    this.eventService.getEventsByType(this.categoryType).subscribe(
      (data: Event[]) => {
        this.events = data;
        console.log('Eventos carregados por categoria:', this.events);
      },
      (error) => {
        console.error('Erro ao carregar eventos por categoria:', error);
      }
    );
  }

  loadUserBookmarks(userId: string) {
    this.eventService.getUserBookmarks(userId).subscribe(
      (data: Event[]) => {
        this.events = data;
        console.log('Favoritos carregados:', this.events);
      },
      (error) => {
        console.error('Erro ao carregar favoritos do usuário:', error);
      }
    );
  }

  goToEventDetails(eventsId: bigint | undefined) {
    console.log(eventsId);
    this.router.navigate(['/events', eventsId]);
  }
}
