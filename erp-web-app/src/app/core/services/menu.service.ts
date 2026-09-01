import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { environment } from '../../../environments/environment';
import { MenuItem, MenuResponse } from '../models/menu.model';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  findMyMenus(): Observable<MenuItem[]> {
    return this.http.get<MenuResponse[]>(`${this.baseUrl}/menus/me`).pipe(
      map((menus) => menus.map((menu) => this.toMenuItem(menu)))
    );
  }

  private toMenuItem(menu: MenuResponse): MenuItem {
    return {
      id: menu.id,
      nome: menu.name,
      endereco: menu.address,
      posicao: menu.position,
      raiz: menu.root
    };
  }
}
