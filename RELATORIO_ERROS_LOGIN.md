# Relatorio de erros nas alteracoes do login

Analise feita sobre as modificacoes atuais do projeto, sem alterar o codigo.

## Resultado do build

O comando abaixo falhou:

```cmd
cd C:\projetos\FULLSTACK\ERP\erp-web-app
npm run build
```

Erros reportados:

```text
src/app/core/auth.guard.ts:9:18 - Property 'isAuthenticated' does not exist on type 'AuthService'.
src/app/core/auth.interceptor.ts:6:36 - Property 'getToken' does not exist on type 'AuthService'.
```

## erp-web-app/src/app/core/auth.service.ts

1. O servico deixou de expor os metodos usados pelo restante da autenticacao.

   `auth.guard.ts` chama `authService.isAuthenticated()`, mas `AuthService` nao possui esse metodo.

   `auth.interceptor.ts` chama `authService.getToken()`, mas `AuthService` tambem nao possui esse metodo.

   Isso quebra a compilacao do Angular.

2. O token recebido no login nao e salvo dentro do proprio `AuthService`.

   O metodo `login()` apenas retorna o `POST`. Ele nao faz `tap()` para guardar `response.token`.

   Consequencia: mesmo quando a API responde com sucesso, o guard e o interceptor nao tem uma fonte confiavel para saber que o usuario esta autenticado.

3. `AccessToken` fica somente em memoria e nunca e preenchido no fluxo atual.

   O metodo `headers()` monta `Authorization: Bearer ` usando `this.AccessToken`, mas esse campo inicia vazio e nao recebe o token no `login()`.

   Consequencia: chamadas autenticadas feitas por `UsuarioService` saem com header invalido.

4. `logout()` navega para `/login`.

   Isso nao e necessariamente errado sozinho, mas no seu `LoginComponent` o `ngOnInit()` chama `this.authService.logout()`. Como `logout()` tambem navega, a tela de login dispara uma navegacao para ela mesma toda vez que abre.

5. Imports nao usados.

   `inject` e `tap` foram importados, mas nao sao usados. Isso nao quebra o build, mas deixa o arquivo inconsistente.

## erp-web-app/src/app/core/auth.guard.ts

1. O guard depende de um metodo inexistente.

   Na linha 9, ele chama `authService.isAuthenticated()`. Esse metodo foi removido ou nao foi implementado em `AuthService`.

   Consequencia: a aplicacao nao compila.

2. O guard nao consegue proteger rotas sem uma fonte persistente de token.

   Mesmo que o metodo existisse, o token hoje esta sendo salvo em `StorageService`, nao em `AuthService` nem em `localStorage`. Ao recarregar a pagina, o estado em memoria seria perdido.

## erp-web-app/src/app/core/auth.interceptor.ts

1. O interceptor depende de um metodo inexistente.

   Na linha 6, ele chama `inject(AuthService).getToken()`. Esse metodo nao existe em `AuthService`.

   Consequencia: a aplicacao nao compila.

2. O interceptor nao conversa com o lugar onde o login salva o token.

   O login salva o token em `StorageService.setToken()`, mas o interceptor tenta ler de `AuthService`.

   Consequencia: mesmo apos login bem-sucedido, o header `Authorization` nao seria adicionado nas requisicoes.

## erp-web-app/src/app/pages/login/login.component.ts

1. Mistura de Reactive Forms com Template-driven Forms.

   O componente cria `this.loginForm = this.fb.group(...)`, mas o HTML usa `[(ngModel)]` e `#f="ngForm"`.

   Consequencia: o objeto usado no TypeScript nao tem o mesmo formato esperado no template e no submit.

2. Leitura incorreta dos valores do formulario.

   Na linha 62, o codigo faz:

   ```ts
   const { login, password } = this.loginForm;
   ```

   Quando `loginForm` e um `FormGroup`, os valores nao ficam diretamente em `this.loginForm.login` e `this.loginForm.password`. O correto seria ler os valores do form, mas o codigo atual passa objetos/undefined para `authService.login()`.

   Consequencia: a API recebe usuario/senha errados ou vazios.

3. O campo enviado para a API nao corresponde diretamente ao backend.

   O backend espera:

   ```json
   {
     "username": "...",
     "password": "..."
   }
   ```

   O componente usa o nome `login`. Isso pode funcionar somente se, antes de chamar a API, o valor for mapeado para `username`. No codigo atual, essa conversao ficou confusa pela leitura incorreta do `FormGroup`.

4. `ngOnInit()` chama `this.authService.logout()`.

   Isso apaga o estado de autenticacao sempre que a tela de login abre. Pode ser aceitavel para uma tela publica, mas combinado com `logout()` navegando para `/login`, cria uma responsabilidade duplicada e comportamento circular.

5. Redirecionamento para rota inexistente.

   No `complete`, o codigo navega para:

   ```ts
   this.router.navigate(['/entregas-em-aberto']);
   ```

   Essa rota nao existe em `app.routes.ts`. As rotas atuais sao `/login` e `/dashboard`.

   Consequencia: apos login, o usuario cai no wildcard e volta para `/dashboard`, ou pode ter comportamento inesperado dependendo da configuracao.

6. Uso incorreto de `complete` para navegacao de sucesso.

   A navegacao apos login deveria acontecer no `next`, depois de receber o token e salvar o estado. O callback `complete` nao e o melhor lugar para fluxo de sucesso de autenticacao.

7. `isLoggedIn` inicia como `true`.

   Para uma tela de login, esse estado inicial deveria refletir a autenticacao real. Comecar como `true` pode mascarar validacoes ou mensagens.

8. Imports e dependencias sem uso ou incompletos.

   `Component, inject`, `FormBuilder`, `ReactiveFormsModule`, `finalize`, `UsuarioService` e varias partes comentadas nao estao coerentes com o fluxo atual.

9. `NotificationService` e injetado, mas a classe nao esta decorada com `@Injectable`.

   Isso pode falhar em runtime quando o Angular tentar resolver a dependencia.

## erp-web-app/src/app/pages/login/login.component.html

1. O template usa template-driven forms enquanto o TypeScript cria um reactive form.

   O HTML usa `#f="ngForm"` e `[(ngModel)]`, enquanto o TS cria `FormGroup` com `FormBuilder`.

   Consequencia: os dados preenchidos podem nao ser os mesmos que o `onSubmit()` tenta ler.

2. Campo `login` nao reflete o contrato da API.

   O input se chama `login`, mas a API espera `username`. Isso exige uma conversao clara no componente antes da chamada HTTP.

3. Ha um bloco `<script>` dentro do template Angular.

   Angular nao deve usar scripts imperativos dentro do template do componente. Esse trecho referencia variaveis globais nao declaradas, como `chrome`, `safari`, `opera`, `epiphany`, `InternetExplorer`, `flag_beforeunload` e `jQuery`.

   Consequencia: mesmo que o Angular compile, esse codigo e fragil e pode quebrar no navegador.

4. O script tenta acessar `document.getElementById('browser')`, mas nao existe elemento com id `browser` no template.

5. Uso de classes Bootstrap sem Bootstrap configurado.

   Classes como `row`, `g-3`, `col-12`, `form-control` e `invalid-feedback` so terao efeito se o CSS do Bootstrap estiver importado. O `angular.json` nao inclui Bootstrap em `styles`.

## erp-web-app/src/app/core/services/storage.service.ts

1. O token e salvo apenas em um signal em memoria.

   Ao atualizar a pagina, o token desaparece.

   Consequencia: o usuario perde a autenticacao no refresh.

2. O interceptor nao le esse servico.

   O login salva em `StorageService`, mas `auth.interceptor.ts` tenta buscar token em `AuthService`.

   Consequencia: o token salvo nao e usado para montar o header `Authorization`.

3. Falta metodo para limpar token.

   Existe `setToken()`, mas nao ha `clearToken()` ou equivalente. O `logout()` limpa somente campos internos do `AuthService`, nao o signal do `StorageService`.

## erp-web-app/src/app/core/services/usuario.service.ts

1. Chama endpoint inexistente na API atual.

   O metodo usa:

   ```ts
   GET /usuario/{login}
   ```

   A API ERP atual nao tem esse endpoint. Os controllers existentes usam rotas como `/peoples`, `/accounts` e `/accountingEntries`.

2. Usa headers manuais baseados em `AuthService.headers()`.

   Como ja existe `auth.interceptor.ts`, o ideal e centralizar o header `Authorization` no interceptor. Do jeito atual, uma parte do app depende do interceptor e outra parte depende de `auth.headers()`.

3. O tipo de retorno nao combina com o codigo comentado no login.

   `consultaDadosUsuario()` retorna `Observable<Usuario>`, mas o trecho comentado em `login.component.ts` usa `usuario.dados`, como se a API retornasse um wrapper `{ dados: ... }`.

## erp-web-app/src/app/core/shared/messages/notification.service.ts

1. Falta `@Injectable({ providedIn: 'root' })`.

   O servico e injetado no `LoginComponent`, mas a classe nao esta marcada como injectable nem aparece em `providers`.

   Consequencia: pode ocorrer erro de injecao de dependencia em runtime.

## erp-web-app/src/environments/environment.ts e environment.development.ts

1. A URL esta fixa com contexto de WildFly:

   ```ts
   http://127.0.0.1:8080/erp-api
   ```

   Isso funciona quando a API roda no WildFly com contexto `/erp-api`. Se rodar com `mvn spring-boot:run`, o contexto geralmente sera sem `/erp-api`, e o login deveria apontar para `http://127.0.0.1:8080`.

2. Os dois arquivos estao iguais.

   Nao e erro fatal, mas perde a utilidade de separar ambiente de desenvolvimento e producao.

## erp-web-app/src/app/app.routes.ts

1. A rota de sucesso usada pelo login nao existe.

   O login navega para `/entregas-em-aberto`, mas `app.routes.ts` define apenas `/login`, `/dashboard`, redirect vazio e wildcard.

   Consequencia: o fluxo de pos-login nao vai para uma rota real do sistema.

## erp-web-app/angular.json

1. O template usa classes Bootstrap, mas o Bootstrap nao foi adicionado em `styles`.

   O arquivo `angular.json` inclui somente:

   ```json
   "styles": [
     "src/styles.css"
   ]
   ```

   Consequencia: a tela pode renderizar sem o layout visual esperado.

## erp-api/src/main/java/com/paulovargas/erpapi/controllers/AuthController.java

1. O CORS foi ajustado para `localhost` e `127.0.0.1`, o que esta correto para o teste local.

   Nao encontrei erro funcional direto aqui nas alteracoes atuais.

## erp-api/src/main/java/com/paulovargas/erpapi/security/SecurityConfig.java

1. O CORS global tambem foi ajustado para `localhost` e `127.0.0.1`, o que esta correto para o teste local.

2. Observacao: ha CORS global em `SecurityConfig` e `@CrossOrigin` em controllers. Isso funciona, mas deixa a configuracao distribuida. Para manutencao, o ideal seria centralizar em um lugar so.

## Resumo dos principais motivos para o login nao funcionar

1. A aplicacao Angular nao compila porque `AuthService` nao tem `isAuthenticated()` e `getToken()`.
2. O login mistura `FormGroup` com `ngModel`, e o `onSubmit()` le os valores do formulario de forma incorreta.
3. O token e salvo em `StorageService`, mas o interceptor tenta le-lo de `AuthService`.
4. Apos o login, a navegacao aponta para `/entregas-em-aberto`, rota que nao existe.
5. O endpoint `/usuario/{login}` usado em `UsuarioService` nao existe na API ERP atual.
