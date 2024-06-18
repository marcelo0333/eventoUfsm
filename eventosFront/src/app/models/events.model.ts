export class Event {
  eventsId!: bigint;
  eventName!: string;
  imgEvent!: string;
  centerName!: string;
  contact!: string;
  link!: string;
  description!: string;
  dateInitial!: Date;
  dateFinal!: Date;
  averageRating!: number;
}
export class Local{
  id!: bigint;
  nameLocal!: string;
  address!: string;
  city!: string;
  cep!: string;
  latitude!: string;
  longitude!: string;
}
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
}
