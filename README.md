# ERP Full Stack

Aplicação full stack de ERP com backend Java Spring Boot e frontend Angular, usando persistência relacional e API REST.

# DEMO

https://paulovargas.github.io/ERP

## Modulos

- `erp-api`: API REST em Java com Spring Boot, Spring Data JPA, PostgreSQL, Swagger/OpenAPI e empacotamento JAR executável.
- `erp-web-app`: aplicacao web Angular para a interface do ERP.

## Funcionalidades da API

- Autenticacao de usuarios e clientes com JWT.
- Cadastro, consulta, atualizacao, ativacao e remocao de clientes.
- Gerenciamento de enderecos e consulta de CEP.
- Gerenciamento de menus e acessos por usuario.

## Tecnologias

- Java 25 (versao alvo da API)
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven
- Angular 22.3.0
- TypeScript 6.0.3
- Node.js 24.15.0 (definido em `erp-web-app/.nvmrc`)
- RxJS

## Como executar o backend

Entre na pasta da API:

```bash
cd erp-api
```

O perfil local usa o banco configurado por `ERP_DB_URL`, `ERP_DB_USER` e `ERP_DB_PASSWORD`. Os padrões são `jdbc:postgresql://localhost:5432/erpdb`, usuário `postgres` e senha `postgres`.

Para abrir no IntelliJ, importe `erp-api/pom.xml`, selecione JDK 25 como Project SDK e JDK do Maven, habilite annotation processing e execute `ErpApiApplication`.

Para gerar o JAR executável:

```cmd
mvnw.cmd clean package
```

Inicie a API com:

```cmd
java -jar target/erp-api.jar
```

A aplicação mantém o contexto `/erp-api` para preservar as URLs que o frontend já utiliza.

## Como executar o frontend

Entre na pasta da aplicacao web:

```bash
cd erp-web-app
```

Instale as dependencias:

```bash
nvm install 24.15.0
nvm use 24.15.0
npm install
```

O Node.js instalado localmente precisa atender a matriz do Angular 22. O projeto registra Node.js 24.15.0 em `erp-web-app/.nvmrc`.

Execute em modo desenvolvimento:

```bash
npm start
```

A aplicacao Angular ficara disponivel em:

```text
http://localhost:4200
```

## Deploy do frontend no GitHub Pages pela pasta docs

Este deploy publica apenas o frontend Angular. A API Spring Boot (`erp-api`) precisa continuar hospedada em um servidor separado e acessivel pela aplicacao web.

O repositorio esta configurado para publicar o Angular em:

```text
https://paulovargas.github.io/ERP/
```

Para testar localmente o mesmo build usado no GitHub Pages:

```bash
cd erp-web-app
npm run build:github-pages
```

Esse comando gera a pasta `docs` na raiz do repositorio. No GitHub, configure o Pages em:

```text
Settings > Pages > Build and deployment > Source > Deploy from a branch
Branch: main
Folder: /docs
```

Se a API estiver hospedada fora do ambiente local, configure tambem o secret abaixo no repositorio:

```text
Settings > Secrets and variables > Actions > New repository secret
Name: ERP_API_URL
Value: https://seu-dominio-da-api/erp-api
```

Sem ajustar a URL da API, o build continuara usando `http://localhost:8080/erp-api/api`, o que serve para testes locais, mas nao para usuarios acessando pelo GitHub Pages.

## Login local

O frontend atual autentica em:

```text
POST http://localhost:8080/erp-api/api/auth/login
```

Corpo da requisicao:

```json
{
  "email": "admin@admin.com",
  "password": "sua_senha"
}
```

Por padrao, a API cria automaticamente este administrador no primeiro boot:

```text
E-mail: admin@admin.com
Senha: gerada no log do servidor
```

Para trocar a senha padrao sem alterar o codigo, defina:

```cmd
set DEFAULT_ADMIN_PASSWORD=sua_senha
```

Se voce ja usava `ERP_INITIAL_PASSWORD`, ela tambem sera aceita como fallback para a senha desse administrador. Se nenhuma senha for definida, a API gera uma senha temporaria e imprime no log do servidor quando criar o usuario.

Opcionalmente, tambem podem ser definidos `DEFAULT_ADMIN_EMAIL`, `DEFAULT_ADMIN_NAME`, `DEFAULT_ADMIN_DOCUMENT` e `DEFAULT_ADMIN_PHONE`. Se o e-mail ja existir no banco, a aplicacao nao altera a senha desse usuario.

Depois gere a pasta `docs`, faca commit e envie para a branch `main`:

```bash
cd erp-web-app
npm run build:github-pages
cd ..
git add docs erp-web-app/package.json erp-web-app/scripts/prepare-github-pages.mjs README.md
git commit -m "Build frontend for GitHub Pages docs"
git push origin main
```

## Documentacao da API

Com a API em execução, acesse:

```text
http://127.0.0.1:8080/erp-api/swagger-ui/index.html
```

## Compatibilidade do backend

O backend usa Java 25 e Spring Boot 3.5, empacotado como JAR executável com servidor embutido. O datasource local pode ser configurado pelas variáveis de ambiente documentadas em `erp-api/README.md`.

## Objetivo do projeto

Este repositorio centraliza backend e frontend em uma unica aplicacao para apresentar dominio de desenvolvimento full stack, modelagem de entidades, integracao com banco relacional, construcao de API REST, documentacao de endpoints e organizacao de projeto para publicacao no GitHub.
