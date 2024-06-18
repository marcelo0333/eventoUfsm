import { Event } from "./events.model";

export interface TokensResponse {
  accessToken: string,
  refreshToken: string
}

export interface SignInResponse {
  tokens: TokensResponse;
  user: UserModel;
}

export interface LoginDTO {
  email: string,
  password: string,
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
}


enum ErrorToken {
  ACCESS_DENIED = 'Invalid Token',
  EXPIRED_TOKEN = 'Expired Token',
  INVALID_TOKEN = 'Access Denied',
}

