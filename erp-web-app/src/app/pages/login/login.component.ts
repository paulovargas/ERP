import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { CommonModule } from '@angular/common';
import { Usuario } from '../../core/models/Usuario';
import { NotificationService } from '../../core/shared/messages/notification.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { StorageService } from '../../core/services/storage.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule,
        ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  dadosUsuario: Usuario = new Usuario();

  loginForm: FormGroup;

  isLoggedIn = true;
  isLoginFailed = false;
  errorMessage = '';

  navigateTo: string = "";
  returnUrl: string = "";

  constructor(
    private readonly fb: FormBuilder,
    private readonly notification: NotificationService,
    private readonly router: Router,
    private readonly usuario: UsuarioService,
    private readonly authService: AuthService, 
    private readonly storage: StorageService
  ) {
    this.loginForm = this.fb.group({
      login: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit() : void {
    this.authService.clearSession();
  }

  get login() {
    return this.loginForm.controls['login'];
  }

  get password() {
    return this.loginForm.controls['password'];
  }

  onSubmit(): void {

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { login, password } = this.loginForm.getRawValue();

    if (login && password) {
      this.errorMessage = "";
      this.isLoginFailed = false;

      this.authService.login(login.trim(), password).subscribe({
        next: response => {
          this.authService.AccessToken = response.dados.token;
          this.authService.logado = true;
          this.storage.setToken(response.dados.token);
          this.router.navigate(['/dashboard']);
          /* this.usuario.consultaDadosUsuario(login).subscribe({
            next: usuario => {
              this.dadosUsuario = usuario.dados,
              this.usuario.setUsuarioLogado(this.dadosUsuario),
              this.storage.setState("idUsuarios" , this.dadosUsuario?.idUsuarios),
              this.storage.setState("dadosUsuario" ,usuario.dados),
              this.agencia.consultaGaragem(this.dadosUsuario?.idGaragem.toString()).subscribe(data => {
                this.usuario.setGaragemUsuarioLogado(data.dados);
              });
            },
            error: error => { console.log('Error ' + error)},
          }) */

        },
        error: err => {
          this.errorMessage = err.error?.message || 'Usuario ou senha invalidos.';
          this.isLoginFailed = true;
        },
        complete: () => {
        }
      })
    }
    else {
      this.notification.notify("Acesso não autorizado !");
    }
  }
}
