# ERP API

Projeto Java com Spring Boot para gestão de operações de ERP, empacotado como JAR executável com servidor web embutido.

## Tecnologias e dependencias

- **Java 25** (versao alvo do projeto)
- **Spring Boot 3.5.16** (linha Jakarta, compatível com Java 25)
  - Spring Web
  - Spring Data JPA
  - JDBC
- **Banco de dados**
  - PostgreSQL configurado por variáveis de ambiente ou endereço local
- **Swagger/OpenAPI** com `springdoc-openapi-starter-webmvc-ui`
- **Maven**

O projeto usa Java 25 e inicia com o servidor Tomcat embutido pelo Spring Boot. Não é necessário instalar ou configurar servidor de aplicações externo.

## Estrutura do projeto

```text
src/
|-- main/
|   |-- java/
|   |   `-- com/paulovargas/erpapi/...
|   `-- resources/
|       `-- application.properties
`-- test/
    `-- java/...
```

## Abrir e executar pelo IntelliJ IDEA

1. Instale um JDK 25 e o plugin Lombok do IntelliJ IDEA.
2. Abra `pom.xml` no IntelliJ e importe como projeto Maven.
3. Defina o Project SDK e o JDK do Maven Importer como Java 25.
4. Em `Settings > Build, Execution, Deployment > Compiler > Annotation Processors`, habilite annotation processing.
5. Inicie o PostgreSQL local e abra a classe `ErpApiApplication` para executar com a configuracao padrao.

O perfil padrão usa o datasource configurado por `ERP_DB_URL`, `ERP_DB_USER` e `ERP_DB_PASSWORD`. Sem essas variáveis, a API usa `jdbc:postgresql://localhost:5432/erpdb`, usuário `postgres` e senha `postgres`.

No PowerShell do IntelliJ, para usar Java 25 apenas na sessão atual sem alterar variáveis permanentes do Windows:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.jdks\temurin-25.0.4.1"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
java -version
.\mvnw.cmd spring-boot:run
```

Para os comandos do painel Maven, selecione `temurin-25` em **Settings > Build Tools > Maven > Runner > JRE**.

## Configuração do PostgreSQL

O datasource usa as variáveis `ERP_DB_URL`, `ERP_DB_USER` e `ERP_DB_PASSWORD`. Sem elas, a API usa `jdbc:postgresql://localhost:5432/erpdb` e o usuário `postgres`.


## Autenticacao JWT

A API usa autenticacao stateless com JWT. O endpoint publico usado pelo frontend atual e:

```text
POST /erp-api/api/auth/login
```

Corpo da requisicao:

```json
{
  "email": "admin@admin.com",
  "password": "sua_senha"
}
```

Resposta:

```json
{
  "token": "jwt_gerado",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "clientId": 1,
  "name": "Administrador",
  "email": "admin@admin.com",
  "userType": "ADMIN"
}
```

As demais rotas devem receber o token no header:

```text
Authorization: Bearer jwt_gerado
```

Configure a chave de assinatura por `JWT_SECRET`. O valor padrão em `application.properties` serve apenas para desenvolvimento local; defina uma chave própria nos demais ambientes.

Por padrao, a API cria automaticamente este administrador no primeiro boot:

```text
E-mail: admin@admin.com
Senha: gerada no log do servidor
```

Para definir uma senha fixa no seu ambiente, use:

```cmd
set DEFAULT_ADMIN_PASSWORD=sua_senha
```

Se voce ja usava `ERP_INITIAL_PASSWORD`, ela tambem sera aceita como fallback para a senha desse administrador. Se nenhuma senha for definida, a API gera uma senha temporaria e imprime no log do servidor quando criar o usuario.

Opcionalmente, defina `DEFAULT_ADMIN_EMAIL`, `DEFAULT_ADMIN_NAME`, `DEFAULT_ADMIN_DOCUMENT` e `DEFAULT_ADMIN_PHONE`. Se o e-mail ja existir no banco, a aplicacao nao altera a senha desse usuario.

## Documentacao da API

A documentação da API fica disponível após iniciar a aplicação:

```text
http://127.0.0.1:8080/erp-api/swagger-ui/index.html
```

## Pre-requisitos

- Java 25 para desenvolvimento no IntelliJ
- Maven Wrapper (`mvnw.cmd` no Windows ou `./mvnw` no Linux/macOS)
- PostgreSQL Server rodando e banco `erpdb` criado

## Build e execução manual

```bash
./mvnw clean package
```

No Windows:

```cmd
mvnw.cmd clean package
```

Para iniciar o JAR gerado:

```cmd
java -jar target/erp-api.jar
```

O arquivo JAR executável será gerado em:

```text
target/erp-api.jar
```

## Autor

**Paulo Vargas**
