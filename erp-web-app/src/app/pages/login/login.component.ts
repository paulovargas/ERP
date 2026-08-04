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
    private fb: FormBuilder,
    private notification: NotificationService,
    private router: Router,
    private usuario: UsuarioService,
    private authService: AuthService, // private alertService: AlertService
    private storage: StorageService
  ) {
    this.loginForm = this.fb.group({
      login: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  ngOnInit() : void {
    this.authService.clearSession();

    //this.returnUrl = this.activatedRoute.snapshot.queryParams['returnUrl'] || '/inicio';
  }

  ngOnChanges() {
    //this.authService.logout();
  }

  get login() {
    return this.loginForm.controls['login'];
  }

  get password() {
    return this.loginForm.controls['password'];
  }

  onSubmit(): void {

    //this.storage.setState("isLoading", true);

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
          this.storage.setToken(response.token)
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
          //this.notificationService.notify('Erro ao logar, tente novamente.');
          this.errorMessage = err.error?.message || 'Usuario ou senha invalidos.';
          this.isLoginFailed = true;
          //this.notificationService.notify('Erro ao logar, tente novamente.');
        },
        complete: () => {
          //this.usuario.consultaDadosUsuario().subscribe((usuario) => ( console.log("login - usuario : " , usuario)));
        //  this.storage.setState("isLoading", false);

        }
      })
    }
    else {
    //this.storage.setState("isLoading", false);
      this.notification.notify("Acesso não autorizado !");
    }
  }
}
