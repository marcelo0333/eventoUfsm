import {UserModel} from "./auth.data.transfer.object";

export class Event {
  eventsId!: bigint;
  eventName!: string;
  imgEvent!: string;
  centerName!: string;
  contact!: string;
  link!: string;
  description!: string;
  typeEvent!: string;
  dateInitial!: Date;
  dateFinal!: Date;
  averageRating!: number;
  totalBookmarks!: bigint;
}
export class CommentsDTO{
  eventName!: string;
  firstName!: string;
  lastName!: string;
  commentContent!: string;
}

export class UserComments{
  id!: bigint;
  user?: UserModel | null;
  events!: Event | null;
  content!: string;
  datePublished?: Date;

}
export interface Comment {
  id?: number,
  user?: UserModel | null,
  event?: Event | null,
  content?: string,
  timestamp?: Date,
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
export interface Locals {
  id: number;
  name: string;
}
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
}
export class Category{
  typeEvent!: string;

}
