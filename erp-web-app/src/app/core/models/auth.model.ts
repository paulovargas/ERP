export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  clientId: number;
  name: string;
  email: string;
  userType: string;
}

export interface AuthUser {
  clientId: number;
  name: string;
  email: string;
  userType: string;
}
