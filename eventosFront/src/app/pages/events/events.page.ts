import { AfterViewInit, Component, OnInit } from '@angular/core';
import { Event, Local } from "../../models/events.model";
import { ActivatedRoute } from "@angular/router";
import { EventService } from "../../service/event.service";
import * as L from 'leaflet';

@Component({
  selector: 'app-events',
  templateUrl: './events.page.html',
  styleUrls: ['./events.page.scss'],
})
export class EventsPage implements OnInit, AfterViewInit {

  event!: Event;
  local!: Local;

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
  ) { }

  ngOnInit() {
    this.getFullEvent();
    this.getLocal();
  }

  getFullEvent() {
    const eventId = this.route.snapshot.paramMap.get('id');
    if (eventId) {
      this.eventService.getEventById(eventId).subscribe(
        (data: Event) => {
          this.event = data;
        },
        (error) => {
          console.error('Erro ao carregar evento:', error);
        }
      );
    }
  }

  getLocal() {
    const eventId = this.route.snapshot.paramMap.get('id');
    if (eventId) {
      this.eventService.getEventAndLocal(eventId).subscribe(
        (res: Local) => {
          this.local = res;
          if (this.local && this.local.latitude && this.local.longitude) {
            this.initMap(); // Inicialize o mapa após carregar o local e verificar as coordenadas
          } else {
            console.error('Localização inválida:', this.local);
          }
        },
        (error) => {
          console.error('Erro ao carregar local:', error);
        }
      );
    }
  }

  ngAfterViewInit() {
    this.breakTitleIntoChunks();
  }

  private breakTitleIntoChunks() {
    const titleElement = document.getElementById('eventTitle');
    if (titleElement && this.event) {
      const words = this.event.eventName.split(' ');
      let formattedTitle = '';
      for (let i = 0; i < words.length; i++) {
        formattedTitle += words[i] + ' ';
        if ((i + 1) % 3 === 0) {
          formattedTitle += '<br>';
        }
      }
      titleElement.innerHTML = formattedTitle.trim();
    }
  }

  private initMap() {
    const mapElement = document.getElementById('map');
    if (mapElement && this.local) {
      const lat = parseFloat(this.local.latitude);
      const lng = parseFloat(this.local.longitude);

      if (isNaN(lat) || isNaN(lng)) {
        console.error('Coordenadas inválidas:', lat, lng);
        return;
      }

      const map = L.map(mapElement).setView([lat, lng], 15);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
      }).addTo(map);

      L.marker([lat, lng])
        .addTo(map)
        .bindPopup(this.local.nameLocal)
        .openPopup();
    }
  }
}
