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
  loading = false;
  isBookmarks = false;
  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private router: Router,
    private tokenService: TokenService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      if (params.get('typeEvent')) {
        this.isBookmarks = false;
        this.categoryType = params.get('typeEvent')!;
        this.category = CATEGORIES.find(cat => cat.id.toString() === this.categoryType);
        this.loadEventsByCategory();
      } else {
        this.isBookmarks = true;
        const userId = this.tokenService.getUserFromToken()?.userId;
        if (userId) {
          this.loadUserBookmarks(userId.toString());
        }
      }
    });
  }

  loadEventsByCategory() {
    this.loading = true;
    this.eventService.getEventsByType(this.categoryType).subscribe({
      next: (data: Event[]) => { this.events = data ?? []; this.loading = false; },
      error: (error) => { this.loading = false; console.error('Erro ao carregar eventos por categoria:', error); }
    });
  }

  loadUserBookmarks(userId: string) {
    this.loading = true;
    this.eventService.getUserBookmarks(userId).subscribe({
      next: (data: Event[]) => { this.events = data ?? []; this.loading = false; },
      error: (error) => { this.loading = false; console.error('Erro ao carregar favoritos do usuário:', error); }
    });
  }

  get emptyMessage(): string {
    return this.isBookmarks ? 'Você ainda não favoritou eventos.' : 'Nenhum evento nesta categoria.';
  }

  goToEventDetails(eventsId: bigint | undefined) {
    this.router.navigate(['/events', eventsId]);
  }
}
