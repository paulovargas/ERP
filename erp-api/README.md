# ERP API

Projeto Java com Spring Boot para gestao de operacoes de ERP, empacotado como WAR e preparado para execucao no servidor de aplicacoes **WildFly**.

## Tecnologias e dependencias

- **Spring Boot 2.7.6**
  - Spring Web
  - Spring Data JPA
  - JDBC
- **Banco de dados**
  - PostgreSQL via datasource JNDI configurado no WildFly
- **Swagger/OpenAPI** com `springdoc-openapi-ui`
- **WildFly 21**
- **Java 11**
- **Maven**

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

## Deploy no WildFly

O projeto e empacotado como `.war` e o plugin `wildfly-maven-plugin` esta configurado para fazer o deploy automatico via management API do WildFly durante o build:

```bash
mvn clean install
```

Antes de rodar o deploy, configure o datasource PostgreSQL no WildFly e defina as variaveis de ambiente `WILDFLY_USERNAME` e `WILDFLY_PASSWORD`.

## Configuracao do PostgreSQL no WildFly

1. Instale o driver PostgreSQL no WildFly via modulo ou console admin.
2. Configure um datasource JNDI com o nome `java:/jdbc/erpdb`.
3. Exemplo de configuracao no `standalone.xml`:

```xml
<datasource jndi-name="java:/jdbc/erpdb" pool-name="ErpApiDS" enabled="true" use-java-context="true">
  <connection-url>jdbc:postgresql://localhost:5432/erpdb</connection-url>
  <driver>postgresql</driver>
  <security>
    <user-name>postgres</user-name>
    <password>sua_senha</password>
  </security>
</datasource>

<drivers>
  <driver name="postgresql" module="org.postgresql">
    <driver-class>org.postgresql.Driver</driver-class>
  </driver>
</drivers>
```

## Configuracao do Spring Boot

O `application.properties` esta configurado para usar o datasource JNDI do WildFly:

```properties
spring.datasource.jndi-name=java:/jdbc/erpdb
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true
```

## Documentacao da API

A documentacao da API fica disponivel apos o deploy:

```text
http://127.0.0.1:8080/erp-api/swagger-ui/index.html
```

## Como iniciar o WildFly localmente

1. Navegue ate o diretorio da instalacao do WildFly:

```bash
cd C:/wildfly-21.0.0.Final/bin
```

2. Execute o script para iniciar o servidor:

```cmd
standalone.bat
```

3. Aguarde o servidor iniciar na porta 8080.

4. Console de administracao:

```text
http://localhost:9990/
```

## Pre-requisitos

- Java 11
- Maven 3.6+
- WildFly 21 ou superior
- PostgreSQL Server rodando e banco `erpdb` criado

## Build e deploy manual

```bash
mvn clean install
```

O arquivo `.war` sera gerado em:

```text
target/erp-api.war
```

## Autor

**Paulo Vargas**
