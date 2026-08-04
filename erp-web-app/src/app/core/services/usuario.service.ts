import { Injectable } from '@angular/core';
import { StorageService } from './storage.service';
import { HttpClient } from '@angular/common/http';
import { Usuario } from '../models/Usuario';
import { Observable } from 'rxjs';
import { AuthService } from '../auth.service';
import { ERP_API } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

constructor(
    private auth: AuthService,
    private storage: StorageService,
    private http: HttpClient
  ) { }

  consultaDadosUsuario(login: string): Observable<Usuario> {
    const headers = this.auth.headers();
    return this.http.get<Usuario>(ERP_API + `/usuario/${login}` , { headers });
  }
}
