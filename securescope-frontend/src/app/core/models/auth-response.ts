import { AuthUser } from './auth-user';

export interface AuthResponse {
  token: string;
  tokenType: 'Bearer';
  user: AuthUser;
}
