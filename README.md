# ERP Full Stack

Aplicacao full stack de ERP criada para demonstrar conhecimentos em backend Java, frontend Angular, persistencia relacional, APIs REST e deploy em servidor de aplicacoes.

# DEMO

https://paulovargas.github.io/ERP

## Modulos

- `erp-api`: API REST em Java com Spring Boot, Spring Data JPA, PostgreSQL, Swagger/OpenAPI e empacotamento WAR para WildFly.
- `erp-web-app`: aplicacao web Angular para a interface do ERP.

## Funcionalidades da API

- Cadastro e consulta de pessoas.
- Cadastro, consulta, atualizacao e remocao de contas.
- Lancamentos contabeis por pessoa e conta.
- Consulta de saldo por pessoa.
- Consulta de extrato por conta.

## Tecnologias

- Java 11
- Spring Boot 2.7.6
- Spring Web
- Spring Data JPA
- PostgreSQL
- WildFly
- Maven
- Angular 19
- TypeScript
- RxJS

## Como executar o backend

Entre na pasta da API:

```bash
cd erp-api
```

Configure no WildFly um datasource JNDI chamado:

```text
java:/jdbc/erpdb
```

Para buildar o WAR:

```bash
mvn clean package
```

No Windows:

```cmd
mvn clean package
```

O artefato sera gerado em:

```text
erp-api/target/erp-api.war
```

Para deploy via Maven, configure as variaveis de ambiente:

```bash
WILDFLY_USERNAME=seu_usuario
WILDFLY_PASSWORD=sua_senha
```

Depois execute:

```bash
mvn clean install
```

## Como executar o frontend

Entre na pasta da aplicacao web:

```bash
cd erp-web-app
```

Instale as dependencias:

```bash
npm install
```

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

Sem ajustar a URL da API, o build continuara usando `http://127.0.0.1:8080/erp-api`, o que serve para testes locais, mas nao para usuarios acessando pelo GitHub Pages.

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

Depois do deploy no WildFly, acesse:

```text
http://127.0.0.1:8080/erp-api/swagger-ui/index.html
```

## Objetivo do projeto

Este repositorio centraliza backend e frontend em uma unica aplicacao para apresentar dominio de desenvolvimento full stack, modelagem de entidades, integracao com banco relacional, construcao de API REST, documentacao de endpoints e organizacao de projeto para publicacao no GitHub.
