import { Event } from "./events.model";

export interface TokensResponse {
  userId: number,
  accessToken: string,
  refreshToken: string
}

export interface SignInResponse {
  tokens: TokensResponse;
  user: UserModel;
}
export interface BookmarkDTO{
  userId?: number;
  eventId?: bigint;
}
export interface Bookmark{
  id: number;
  userId?: number;
  event?: Event;
}
export interface LoginDTO {
  email: string,
  password: string,
}
export interface ReminderDTO{
  userId?: number;
  eventId?: number;
  reminderTime: Date;
}

export interface ReminderEventsDTO{
  reminderId?: number;
  events?: Event;
  reminderTime: Date;
}
export interface RegisterDTO {
  firstName: string,
  lastName: string,
  email: string,
  password: string
}

export interface ErrorDTO {
  error: string,
  message: string,
}

export class UserModel {
  public userId?: number;
  public firstName?: string;
  public lastName?: string;
  public email?: string;
  public role?: string;
}


enum ErrorToken {
  ACCESS_DENIED = 'Invalid Token',
  EXPIRED_TOKEN = 'Expired Token',
  INVALID_TOKEN = 'Access Denied',
}

