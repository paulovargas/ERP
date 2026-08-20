import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ERP_API } from '../../environments/environment.development';
import { Router } from '@angular/router';
import { StorageService } from './services/storage.service';
import { catchError, Observable, of, throwError } from 'rxjs';
import { Response } from './models/Response';
import { AuthResponse } from './models/AuthResponse';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly mockUsername = 'admin';
  private readonly mockPassword = 'admin';
  private readonly mockToken = 'mock-token-github-pages';

  AccessToken: string = "";
  logado: boolean = false;

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly storage: StorageService
  ) {
    this.AccessToken = this.storage.getToken();
    this.logado = this.AccessToken !== "";
  }

  logout() {
    this.clearSession();
    this.router.navigate(["/login"]);
  }

  clearSession() {
    this.logado = false;
    this.AccessToken = "";
    this.storage.clearToken();
  }

  headers(){
    this.AccessToken = this.storage.getToken();

    const headers = new HttpHeaders().set(
      'Authorization',
      'Bearer ' + this.AccessToken
    );

    return headers;
  }

  getToken(): string {
    this.AccessToken = this.storage.getToken();
    return this.AccessToken;
  }

  isAuthenticated(): boolean {
    return this.getToken() !== "";
  }

  login(username: string, password: string): Observable<Response<AuthResponse>>{
    return this.http.post<Response<AuthResponse>>(ERP_API + `/auth/login`, { username, password}).pipe(
      catchError((error: HttpErrorResponse) => {
        if (this.isApiConnectionError(error) && this.isDefaultMockLogin(username, password)) {
          return of({
            sucesso: true,
            mensagem: 'Login mockado por indisponibilidade da API.',
            dados: {
              token: this.mockToken,
              tokenType: 'Bearer'
            }
          });
        }

        return throwError(() => error);
      })
    );
  }

  private isApiConnectionError(error: HttpErrorResponse): boolean {
    return error.status === 0;
  }

  private isDefaultMockLogin(username: string, password: string): boolean {
    return username === this.mockUsername && password === this.mockPassword;
  }
}
