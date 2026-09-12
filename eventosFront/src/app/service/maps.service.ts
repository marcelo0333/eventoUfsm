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

    // 2. Tenta geocodificar pelo endereço (busca estruturada e fallbacks)
    const coords = await this.geocode();
    if (coords) return coords;

    // 3. Fallback para coordenadas padrão
    console.warn('Não foi possível resolver coordenadas. Usando padrão.');
    return DEFAULT;
  }

  // Separa "Recife - Pernambuco" em cidade e estado.
  private parseCityState(raw: string | undefined | null): { city: string; state: string } {
    if (!raw) return { city: '', state: '' };
    const parts = raw.split(/\s*[-–/]\s*/).map(p => p.trim()).filter(Boolean);
    return { city: parts[0] ?? '', state: parts[1] ?? '' };
  }

  // Extrai o nome principal da via, descartando lixo tipo "- 0 - Armazen 10".
  // Ex.: "Av. Alfredo Lisboa - 0 - Armazen 10" -> "Av. Alfredo Lisboa".
  private cleanStreet(raw: string | undefined | null): string {
    if (!raw) return '';
    const firstSegment = raw.split(/\s*[-–]\s*/)[0]?.trim() ?? '';
    // Remove número de porta solto no fim (ex.: "Rua X 0" -> "Rua X").
    return firstSegment.replace(/\s+\d+\s*$/, '').trim() || firstSegment;
  }

  // Tenta geocodificar do mais específico ao mais genérico.
  // A busca em texto livre (q) é bem mais tolerante a dados sujos do que a
  // busca estruturada, então ela vem primeiro.
  private async geocode(): Promise<[number, number] | null> {
    const rawAddress = this.local?.address?.trim();
    const cep = this.local?.cep?.trim();
    const nameLocal = this.local?.nameLocal?.trim();
    const { city, state } = this.parseCityState(this.local?.city);
    const street = this.cleanStreet(rawAddress);

    const attempts: Array<Record<string, string>> = [];

    // 2a. Texto livre com a via limpa + cidade + estado (mais robusto).
    if (street) {
      attempts.push({ q: [street, city, state, 'Brasil'].filter(Boolean).join(', ') });
    }
    // 2b. Nome do local (prédios/campi conhecidos costumam existir no OSM).
    if (nameLocal) {
      attempts.push({ q: [nameLocal, city, state, 'Brasil'].filter(Boolean).join(', ') });
    }
    // 2c. Endereço bruto em texto livre (caso a limpeza tenha tirado algo útil).
    if (rawAddress && rawAddress !== street) {
      attempts.push({ q: [rawAddress, city, state, 'Brasil'].filter(Boolean).join(', ') });
    }
    // 2d. Busca estruturada com a via limpa.
    if (street) {
      const params: Record<string, string> = { street, country: 'Brasil' };
      if (city) params['city'] = city;
      if (state) params['state'] = state;
      if (cep) params['postalcode'] = cep;
      attempts.push(params);
    }
    // 2e. Só o CEP.
    if (cep) {
      attempts.push({ postalcode: cep, country: 'Brasil' });
    }
    // 2f. Ao menos centraliza na cidade/estado.
    if (city) {
      attempts.push({ q: [city, state, 'Brasil'].filter(Boolean).join(', ') });
    }

    for (const params of attempts) {
      const coords = await this.searchNominatim(params);
      if (coords) return coords;
    }
    return null;
  }

  // Chama a API Nominatim
  private async searchNominatim(params: Record<string, string>): Promise<[number, number] | null> {
    try {
      const query = new URLSearchParams({ format: 'json', limit: '1', ...params });
      const url = `https://nominatim.openstreetmap.org/search?${query.toString()}`;

      // Obs.: não definir 'User-Agent' aqui — é um header proibido em fetch
      // de navegador; o Nominatim identifica a aplicação pelo Referer.
      const response = await fetch(url, {
        headers: { 'Accept-Language': 'pt-BR' }
      });

      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      const results = await response.json();

      if (!Array.isArray(results) || results.length === 0) {
        console.warn('Nenhum resultado encontrado para:', params);
        return null;
      }

      const { lat, lon } = results[0];
      console.log('Geocodificação bem-sucedida:', lat, lon, 'para', params);
      return [parseFloat(lat), parseFloat(lon)];

    } catch (error) {
      console.error('Erro ao geocodificar endereço:', error, params);
      return null;
    }
  }

}
