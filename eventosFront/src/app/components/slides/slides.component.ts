import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Event } from 'src/app/models/events.model';

@Component({
  selector: 'app-slides',
  templateUrl: './slides.component.html',
  styleUrls: ['./slides.component.scss'],
})
export class SlidesComponent  implements OnInit {

  @Input() events: Event[] = [] ;
  @Input() eventsBookmarks: Event[] = [] ;
  @Input() eventsRecommended: Event[] = [] ;
  @Input() title: string = ''
  constructor(
    private router: Router
  ) { }

ngOnInit() {  console.log('events', this.events); 
  console.log('eventsBookmarks', this.eventsBookmarks);
  console.log('eventsRecommended', this.eventsRecommended);
  console.log('title', this.title);}
  swiperSlideChanged(e: any): void {
    console.log('Slide alterado:', e);
  }
    goToEventDetails(eventsId: bigint) {
    console.log(eventsId);
    this.router.navigate(['/events', eventsId]);
  }

}
