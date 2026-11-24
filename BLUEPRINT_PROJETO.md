# Blueprint de Programacao do Projeto TMS

Este blueprint descreve o jeito de programar usado neste projeto Angular para reutilizar em outro sistema: arquitetura, componentes standalone, rotas, modal, subrotas, services, estilos e padroes de tela.

## 1. Base tecnica

- Framework: Angular 22.3.0 com componentes standalone.
- UI: Angular Material + Bootstrap 5.
- Estado simples: services singleton com `providedIn: 'root'`.
- HTTP: `HttpClient` com headers vindos do `AuthenticationService`.
- Rotas: `provideRouter(routes)` em `app.config.ts`.
- Locale: `pt-BR` registrado globalmente.
- Estilos globais: `src/custom-theme.scss`, Bootstrap e `src/styles.css`, nessa ordem em `angular.json`.

Dependencias principais para outro projeto:

```json
{
  "@angular/core": "^22.3.0",
  "@angular/router": "^22.3.0",
  "@angular/forms": "^22.3.0",
  "@angular/material": "^22.3.0",
  "bootstrap": "^5.3.3",
  "rxjs": "~7.8.0",
  "zone.js": "~0.16.3",
  "typescript": "~6.0.3"
}
```

Use Node.js 24.15.0, registrado no arquivo `.nvmrc` do frontend.

## 2. Estrutura de pastas

Padrao usado:

```text
src/
  app/
    app.config.ts
    app.routes.ts
    auth.interceptor.ts
    core/
      components/
        header/
        main/
        footer/
        modal/
        componentes-reutilizaveis/
      models/
      resolvers/
      services/
      shared/
        directives/
        pipes/
        messages/
        shared.imports.ts
    modules/
      nome-do-modulo/
        nome-do-modulo.component.ts
        nome-do-modulo.component.html
        nome-do-modulo.component.css
        datagrid-.../
        form-.../
```

Regra pratica:

- `core/components`: componentes reutilizaveis ou estruturais.
- `core/services`: comunicacao HTTP, estado compartilhado e utilitarios.
- `core/models`: interfaces/classes de dominio.
- `core/shared`: pipes, directives e imports comuns.
- `modules`: telas e fluxos de negocio.

## 3. Padrao de componente standalone

Os componentes declaram seus imports localmente:

```ts
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-minha-tela',
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './minha-tela.component.html',
  styleUrl: './minha-tela.component.css'
})
export class MinhaTelaComponent {}
```

Quando houver pipes/directives recorrentes, usar o array compartilhado:

```ts
import { sharedImports } from '../../core/shared/shared.imports';

@Component({
  selector: 'app-exemplo',
  imports: [
    CommonModule,
    RouterModule,
    ...sharedImports
  ],
  templateUrl: './exemplo.component.html',
  styleUrl: './exemplo.component.css'
})
export class ExemploComponent {}
```

## 4. Rotas principais

O projeto centraliza as rotas em `src/app/app.routes.ts`.

Padrao para tela comum:

```ts
{
  path: 'entregas-em-aberto',
  component: EntregasEmAbertoComponent,
  children: [
    {
      path: '',
      component: SemOcorrenciaComponent,
      outlet: 'principal'
    },
    {
      path: 'com-ocorrencia',
      component: ComOcorrenciaComponent,
      outlet: 'principal'
    }
  ]
}
```

No HTML do componente pai:

```html
<ul class="nav nav-tabs container mx-3 mt-2">
  <li class="nav-item" role="presentation">
    <button
      [routerLink]="[{ outlets: { principal: ['sem-ocorrencia'] }}]"
      routerLinkActive="active"
      class="nav-link"
      type="button">
      Sem Ocorrencia
    </button>
  </li>
</ul>

<router-outlet name="principal" class="justify-content-center scrollable-div" />
```

Usar este padrao quando a tela principal tem abas internas sem abrir modal.

## 5. Modal por rota nomeada

O modal do projeto usa Angular Material `MatDialog` como container visual e o Angular Router para renderizar a rota dentro do outlet nomeado `modal`.

Rota de modal:

```ts
{
  path: 'consulta-cte',
  outlet: 'modal',
  component: ConsultaCteComponent
}
```

Abertura pelo service:

```ts
openModal(route: string, codUF: string, id: string): void {
  this.dialog.open(ModalContentDialog);

  if (codUF === '') {
    this.router.navigate([{ outlets: { modal: [route] } }]);
  } else {
    this.router.navigate([{ outlets: { modal: [route, codUF, id] } }]);
  }
}
```

Chamada a partir de qualquer componente:

```ts
constructor(private modalService: ModalService) {}

abrirConsulta() {
  this.modalService.openModal('consulta-cte', '', '');
}

abrirCte(codUF: string, id: string) {
  this.modalService.openModal('cte', codUF, id);
}
```

No container do modal:

```html
<div class="modal-dialog align-items-center" [ngStyle]="{ 'min-width': modalStyle ? modalStyle : '170vh' }">
  <div class="modal-content">
    <div class="titulo mx-2 my-2">
      <h5>{{ data?.title ? data?.title : titulo }}</h5>
    </div>

    <div class="modal-body">
      @if (data?.component) {
        <ng-container #dynamicContainer></ng-container>
      } @else {
        <router-outlet name="modal"></router-outlet>
      }
    </div>
  </div>
</div>
```

Regra de uso:

- Usar `outlet: 'modal'` para telas abertas sobre a tela atual.
- Usar `modalService.openModal('rota', '', '')` para abrir por nome de rota.
- Usar parametros quando a rota for algo como `cte/:codUF/:id`.
- Fechar com `modalService.fechar()` quando quiser limpar o modal e voltar para a rota base.

## 6. Modal por componente dinamico

Tambem existe abertura direta por componente, usada quando o menu precisa passar titulo, tamanho e dados:

```ts
this.modalService.open(
  ConsultaCteComponent,
  '',
  '160vh',
  'Consulta CTe',
  '',
  '',
  ''
);
```

Contrato atual:

```ts
open(
  component: any,
  object: any,
  modalStyle: string,
  title: string,
  codUF: string,
  id: string,
  chave: string
): void
```

O container cria o componente em runtime:

```ts
this.container.clear();
this.componentRef = this.container.createComponent(this.data.component);

if (this.data.object && this.componentRef.instance) {
  this.componentRef.instance.dados = this.data.object;
}
```

Regra de uso:

- Usar componente dinamico quando precisar controlar `title`, `modalStyle` ou passar um objeto diretamente.
- O componente aberto deve aceitar `dados` se receber payload.

## 7. Submodal e abas dentro de modal

Modais complexos usam um segundo outlet nomeado: `submodal`.

Rota:

```ts
{
  path: 'tabelas-de-precos',
  outlet: 'modal',
  component: TabelasDePrecosComponent,
  children: [
    {
      path: '',
      outlet: 'submodal',
      redirectTo: 'datagrid-precificadores',
      pathMatch: 'full'
    },
    {
      path: 'datagrid-precificadores',
      outlet: 'submodal',
      component: DatagridPrecificadoresComponent
    },
    {
      path: 'datagrid-tabelas-preco-genericas',
      outlet: 'submodal',
      component: DatagridTabelasPrecoGenericasComponent
    }
  ]
}
```

HTML do modal pai:

```html
<ul class="nav nav-tabs container mx-3 mt-2">
  <li class="nav-item" role="presentation">
    <button
      [routerLink]="[{ outlets: { submodal: ['datagrid-precificadores'] }}]"
      routerLinkActive="active"
      class="nav-link"
      type="button">
      Precificadores
    </button>
  </li>
</ul>

<div class="justify-content-center">
  <router-outlet name="submodal" />
</div>
```

Regra de uso:

- `modal`: janela principal.
- `submodal`: abas internas da janela.
- Outlets mais especificos podem existir quando o fluxo precisar de terceiro nivel, exemplo `dadosSimulacao` ou `ocorrencias`.

## 8. App shell

O shell da aplicacao separa login, layout principal, loading e mensagens:

```html
<div *ngIf="logado">
  <app-header></app-header>
  <app-main></app-main>
  <app-footer></app-footer>
</div>

<app-loading-spinner *ngIf="isLoading"></app-loading-spinner>
<app-snackbar></app-snackbar>
<app-login *ngIf="!logado"></app-login>
```

O `MainComponent` concentra os botoes de navegacao principal e o outlet padrao:

```html
<main class="base-router-outlet mb-2">
  <div class="d-flex flex-wrap mx-5">
    <button [routerLink]="['romaneio']" routerLinkActive="active" class="btn btn-outline-success me-2 my-2">
      Romaneio
    </button>
  </div>

  <router-outlet class="base-router-outlet" />
  <modal-content></modal-content>
  <app-notificacao></app-notificacao>
</main>
```

## 9. Services HTTP

Padrao dos services:

```ts
@Injectable({
  providedIn: 'root'
})
export class ClientesService {
  constructor(
    private authService: AuthenticationService,
    private http: HttpClient
  ) {}

  consultaCliente(idCliente: string): Observable<any> {
    const headers = this.authService.headers();
    return this.http.get<any>(`${SJ_API}/cliente/${idCliente}`, { headers });
  }
}
```

Regra de uso:

- Um service por dominio: `clientes`, `motoristas`, `cte`, `menu`.
- Retornar `Observable<T>`.
- Tipar com modelos de `core/models` quando houver contrato claro.
- Montar URL com a constante de ambiente `SJ_API`.
- Repetir headers via `AuthenticationService.headers()`.

## 10. Interceptor

O projeto usa interceptor funcional:

```ts
export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 403) {
        window.location.reload();
      }

      if (error.status === 500) {
        console.error('Erro interno no servidor !');
      }

      return throwError(() => new Error('Ocorreu um erro; por favor, tente novamente mais tarde.'));
    })
  );
};
```

Registro em `app.config.ts`:

```ts
provideHttpClient(withInterceptors([AuthInterceptor]))
```

## 11. Estilo visual global

O projeto mistura Bootstrap para layout/botoes/navs e Angular Material para form fields, dialogs, tabelas e inputs.

Ordem de estilos em `angular.json`:

```json
[
  "src/custom-theme.scss",
  "node_modules/bootstrap/dist/css/bootstrap.min.css",
  "src/styles.css"
]
```

Tema Material:

```scss
@use '@angular/material' as mat;

html {
  color-scheme: light;
  @include mat.theme((
    color: mat.$green-palette,
    typography: Roboto,
    density: 0
  ));
}
```

Reset e ajustes globais:

```css
* {
  padding: 0;
  margin: 0;
  border: 0;
  box-sizing: border-box;
}

html, body {
  font-size: 14px;
}

button, input, select, textarea {
  font-size: 14px;
}
```

Padrao de tabs:

```css
.nav-item .active {
  color: white !important;
  background-color: #098b09 !important;
}

.nav-item:not(.active) .nav-link {
  color: black;
}
```

Padrao de tabela:

```css
.table-container {
  max-height: 70vh;
  overflow-x: auto;
}

th,
ul,
.mat-header-row {
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 1;
}

.row-is-clicked {
  font-weight: bold;
  cursor: pointer;
  background-color: rgb(163, 245, 163);
}
```

Padrao Material:

```css
.mat-mdc-form-field {
  display: flex !important;
}

.mat-mdc-text-field-wrapper {
  width: 85% !important;
}

.mat-mdc-button,
.mat-mdc-raised-button,
.mat-mdc-outlined-button {
  font-size: 14px;
  padding: 6px 16px;
}
```

## 12. Padrao de datagrid

Use um componente dedicado para cada grid:

```text
modules/
  modulo/
    modulo.component.*
    datagrid-entidade/
      datagrid-entidade.component.ts
      datagrid-entidade.component.html
      datagrid-entidade.component.css
    form-entidade/
      form-entidade.component.ts
      form-entidade.component.html
      form-entidade.component.css
```

Regras:

- O componente pai monta as abas e outlets.
- O datagrid lista, seleciona e dispara edicao/abertura.
- O form concentra validacao e persistencia.
- Linhas selecionadas usam `.row-is-clicked`.
- Tabelas ficam dentro de `.table-container`.

## 13. Padrao para criar novo modulo com modal

Checklist:

1. Criar pasta em `src/app/modules/nome-do-modulo`.
2. Criar componente standalone pai.
3. Criar filhos como `datagrid-*`, `form-*` ou etapas do fluxo.
4. Importar componentes no `app.routes.ts`.
5. Adicionar rota com `outlet: 'modal'`.
6. Se houver abas, adicionar `children` com `outlet: 'submodal'`.
7. No HTML do pai, criar nav tabs Bootstrap e `<router-outlet name="submodal" />`.
8. Abrir pelo menu com `modalService.openModal('nome-do-modulo', '', '')`.
9. Se precisar titulo/tamanho/payload, abrir com `modalService.open(Component, dados, '160vh', 'Titulo', '', '', '')`.

Template de rota:

```ts
{
  path: 'meu-modulo',
  outlet: 'modal',
  component: MeuModuloComponent,
  children: [
    {
      path: '',
      outlet: 'submodal',
      redirectTo: 'datagrid-meu-modulo',
      pathMatch: 'full'
    },
    {
      path: 'datagrid-meu-modulo',
      outlet: 'submodal',
      component: DatagridMeuModuloComponent
    },
    {
      path: 'form-meu-modulo',
      outlet: 'submodal',
      component: FormMeuModuloComponent
    }
  ]
}
```

Template de componente pai:

```html
<ul class="nav nav-tabs container mx-3 mt-2">
  <li class="nav-item" role="presentation">
    <button
      [routerLink]="[{ outlets: { submodal: ['datagrid-meu-modulo'] }}]"
      routerLinkActive="active"
      class="nav-link"
      type="button">
      Consulta
    </button>
  </li>

  <li class="nav-item" role="presentation">
    <button
      [routerLink]="[{ outlets: { submodal: ['form-meu-modulo'] }}]"
      routerLinkActive="active"
      class="nav-link"
      type="button">
      Cadastro
    </button>
  </li>
</ul>

<div class="justify-content-center">
  <router-outlet name="submodal" />
</div>
```

## 14. Convencoes de nomes

- Pastas e rotas em kebab-case: `cadastro-de-usuarios`.
- Components com sufixo `Component`.
- Services com sufixo `Service`.
- Models em `core/models`, normalmente com nome de dominio: `ClienteCte`, `Motorista`, `Response`.
- Rotas de relatorios seguem prefixo `relatorio-*`.
- Formularios seguem prefixo `form-*`.
- Grids seguem prefixo `datagrid-*`.

## 15. Pontos para melhorar ao replicar

Ao aplicar em outro projeto, manter o padrao, mas vale corrigir estes pontos:

- Evitar acentos em nomes de arquivos/classes para reduzir problemas de encoding.
- Preferir `styleUrls` ou `styleUrl` de forma consistente.
- Remover logs de debug antes de producao.
- Evitar `any` quando houver modelo conhecido.
- Revisar `provideHttpClient`: hoje ha duas chamadas, uma com interceptor e outra com `withFetch`; em projeto novo prefira uma unica chamada combinada.
- Padronizar fechamento de modal limpando explicitamente o outlet `modal`.
- Criar constantes para nomes de outlets: `modal`, `submodal`, `principal`.

Exemplo melhorado para providers:

```ts
provideHttpClient(
  withFetch(),
  withInterceptors([AuthInterceptor])
)
```

Exemplo melhorado para fechar modal:

```ts
fechar(): void {
  this.dialog.closeAll();
  this.router.navigate([{ outlets: { modal: null } }]);
}
```

## 16. Blueprint minimo para outro projeto

Copiar estes arquivos/conceitos primeiro:

- `app.config.ts`: providers, router, HTTP, locale e animations.
- `app.routes.ts`: estrutura com outlet padrao, `modal`, `submodal` e `principal`.
- `core/components/modal`: container `MatDialog` + `<router-outlet name="modal">`.
- `core/services/modal.service.ts`: API unica para abrir/fechar modal.
- `core/shared/shared.imports.ts`: pipes/directives reutilizaveis.
- `custom-theme.scss`: tema Material.
- `styles.css`: reset, tabs, tables, Material overrides.
- Estrutura `modules/<modulo>/<pai|datagrid|form>`.

Com isso, o outro projeto consegue repetir a mesma forma de programar: telas normais por rota padrao, janelas por rota nomeada `modal`, abas internas por outlet nomeado, services singleton para regra de negocio/HTTP e Bootstrap + Material para UI.
