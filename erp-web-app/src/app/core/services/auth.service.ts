import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map, tap } from 'rxjs';

import { AuthRequest, AuthResponse, AuthUser } from '../models/auth.model';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = 'auth_token';
const USER_KEY = 'auth_user';
const EXPIRES_AT_KEY = 'auth_expires_at';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  login(credentials: AuthRequest): Observable<AuthUser> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`, credentials).pipe(
      tap((response) => this.saveSession(response)),
      map((response) => this.toUser(response))
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(EXPIRES_AT_KEY);
  }

  getToken(): string | null {
    if (this.isTokenExpired()) {
      this.logout();
      return null;
    }

    return localStorage.getItem(TOKEN_KEY);
  }

  getUser(): AuthUser | null {
    const user = localStorage.getItem(USER_KEY);

    if (!user) {
      return null;
    }

    try {
      return JSON.parse(user) as AuthUser;
    } catch {
      this.logout();
      return null;
    }
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }

  private saveSession(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, `${response.tokenType} ${response.token}`);
    localStorage.setItem(USER_KEY, JSON.stringify(this.toUser(response)));
    localStorage.setItem(EXPIRES_AT_KEY, String(Date.now() + response.expiresIn));
  }

  private toUser(response: AuthResponse): AuthUser {
    return {
      clientId: response.clientId,
      name: response.name,
      email: response.email,
      userType: response.userType
    };
  }

  private isTokenExpired(): boolean {
    const expiresAt = Number(localStorage.getItem(EXPIRES_AT_KEY));

    if (!expiresAt) {
      return false;
    }

    return Date.now() >= expiresAt;
  }
}
