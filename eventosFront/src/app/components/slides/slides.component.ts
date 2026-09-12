import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Event } from 'src/app/models/events.model';

@Component({
  selector: 'app-slides',
  templateUrl: './slides.component.html',
  styleUrls: ['./slides.component.scss'],
})
export class SlidesComponent {

  @Input() events: Event[] = [];
  @Input() eventsBookmarks: Event[] = [];
  @Input() eventsRecommended: Event[] = [];
  @Input() title: string = '';
  @Input() loading = false;

  constructor(private router: Router) {}

  /** Retorna a lista correta conforme o tipo do carrossel. */
  get items(): Event[] {
    switch (this.title) {
      case 'bookmarks': return this.eventsBookmarks ?? [];
      case 'recommended': return this.eventsRecommended ?? [];
      default: return this.events ?? [];
    }
  }

  get emptyMessage(): string {
    switch (this.title) {
      case 'bookmarks': return 'Nenhum evento favoritado ainda.';
      case 'recommended': return 'Sem recomendações no momento.';
      default: return 'Nenhum evento disponível.';
    }
  }

  goToEventDetails(eventsId: bigint | undefined): void {
    if (eventsId != null) {
      this.router.navigate(['/events', eventsId]);
    }
  }
}
