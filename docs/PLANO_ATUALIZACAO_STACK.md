# Atualização da stack

## Objetivo

Manter o projeto ERP executável com Java 25, Spring Boot e Angular 22.3.0, usando JAR com servidor web embutido e preservando as URLs que o frontend já consome.

## Alteracoes

- [x] Definir Java 25 e Maven Compiler Plugin 3.14.1.
- [x] Atualizar Spring Boot para 3.5.16 e migrar Servlet, Validation e Persistence para Jakarta.
- [x] Empacotar a API como JAR executável com Tomcat embutido; remover o plugin de deploy externo.
- [x] Atualizar Springdoc para a linha compatível com Spring Boot 3.5.
- [x] Preservar PostgreSQL local pelas variáveis `ERP_DB_URL`, `ERP_DB_USER` e `ERP_DB_PASSWORD`.
- [x] Manter o contexto `/erp-api` para não alterar as URLs usadas pelo frontend.
- [x] Documentar a execução com Maven Wrapper, IntelliJ e JDK 25.

## Compatibilidade a confirmar

O backend usa Spring Boot 3.5.16, que suporta Java 25. O projeto não depende mais de WAR, WildFly nem datasource JNDI. O frontend permanece em Angular 22.3.0.

## Build

```cmd
cd erp-api
mvnw.cmd clean package
```

Para iniciar localmente, execute `ErpApiApplication` no IntelliJ com JDK 25 e PostgreSQL ativo, ou gere o JAR com o comando acima e execute `java -jar target/erp-api.jar`. O build do Maven ainda precisa ser validado neste ambiente.
