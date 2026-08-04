import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private readonly tokenKey = 'erp_token';
  token = signal<string>(localStorage.getItem(this.tokenKey) || '');

  setToken(token: string){
    localStorage.setItem(this.tokenKey, token);
    this.token.set(token);
  }

  getToken(): string {
    return this.token() || localStorage.getItem(this.tokenKey) || '';
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
    this.token.set('');
  }
}
