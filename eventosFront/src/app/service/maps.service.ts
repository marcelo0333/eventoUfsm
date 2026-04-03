import { async } from "rxjs";
import { Local } from "../models/events.model";
import L from "leaflet";

export class MapsService {
  private local: Local;

  constructor(local: Local) {
    this.local = local;
  }
private fixLeafletIcons() {
  const iconDefault = L.icon({
    iconUrl: '/assets/leaflet/marker-icon.png',         // Adicionada a / no início
    iconRetinaUrl: '/assets/leaflet/marker-icon-2x.png',
    shadowUrl: '/assets/leaflet/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });
  L.Marker.prototype.options.icon = iconDefault;
}
  public async initMap() {
    this.fixLeafletIcons();
    const mapElement = document.getElementById('map');
    if (!mapElement) return;

    // Evita duplicar o mapa ao navegar
    if ((mapElement as any)._leaflet_id) {
      (mapElement as any)._leaflet_id = null;
      mapElement.innerHTML = '';
    }

    const [lat, lng] = await this.resolveCoordinates();

    const map = L.map(mapElement).setView([lat, lng], 15);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    L.marker([lat, lng])
      .addTo(map)
      .bindPopup(this.local?.nameLocal || 'Local')
      .openPopup();
  }

  private async resolveCoordinates(): Promise<[number, number]> {
    const DEFAULT: [number, number] = [-29.7199611, -53.7151194];

    const sanitize = (value: string | undefined | null): number => {
      if (!value) return NaN;
      return parseFloat(value.toString().replace(',', '.').trim());
    };

    // 1. Tenta usar lat/lng do backend
    const lat = sanitize(this.local?.latitude);
    const lng = sanitize(this.local?.longitude);

    if (!isNaN(lat) && !isNaN(lng)) {
      console.log('Usando coordenadas do backend:', lat, lng);
      return [lat, lng];
    }

    // 2. Tenta geocodificar pelo endereço
    const address = this.buildAddress();
    if (address) {
      console.log('Geocodificando endereço:', address);
      const coords = await this.geocodeAddress(address);
      if (coords) return coords;
    }

    // 3. Fallback para coordenadas padrão
    console.warn('Não foi possível resolver coordenadas. Usando padrão.');
    return DEFAULT;
  }

  // Monta o endereço completo a partir dos campos do Local
  private buildAddress(): string | null {
    const parts = [
      this.local?.address,       // rua
      this.local?.city,         // cidade
      'Brasil'
    ].filter(Boolean);

    return parts.length > 0 ? parts.join(', ') : null;
  }

  // Chama a API Nominatim
  private async geocodeAddress(address: string): Promise<[number, number] | null> {
    try {
      const url = `https://nominatim.openstreetmap.org/search?format=json&limit=1&q=${encodeURIComponent(address)}`;

      const response = await fetch(url, {
        headers: {
          // Nominatim exige um User-Agent identificando sua aplicação
          'Accept-Language': 'pt-BR',
          'User-Agent': 'EventosUFSM/1.0'
        }
      });

      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      const results = await response.json();

      if (results.length === 0) {
        console.warn('Nenhum resultado encontrado para:', address);
        return null;
      }

      const { lat, lon } = results[0];
      console.log('Geocodificação bem-sucedida:', lat, lon);
      return [parseFloat(lat), parseFloat(lon)];

    } catch (error) {
      console.error('Erro ao geocodificar endereço:', error);
      return null;
    }
  }

}
