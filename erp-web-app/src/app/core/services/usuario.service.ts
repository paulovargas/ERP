import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ClientCreateRequest, ClientResponse, ClientUpdateRequest, UserStatus, UserType } from '../models/client.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  findAll(filters: { status?: UserStatus; userType?: UserType } = {}): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(`${this.baseUrl}/users`, { params: this.cleanParams(filters) });
  }

  create(request: ClientCreateRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(`${this.baseUrl}/users`, request);
  }

  update(id: number, request: ClientUpdateRequest): Observable<ClientResponse> {
    return this.http.put<ClientResponse>(`${this.baseUrl}/users/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/users/${id}`);
  }

  private cleanParams(filters: { status?: UserStatus; userType?: UserType }): Record<string, string> {
    return Object.entries(filters).reduce<Record<string, string>>((params, [key, value]) => {
      if (value) {
        params[key] = value;
      }

      return params;
    }, {});
  }
}
