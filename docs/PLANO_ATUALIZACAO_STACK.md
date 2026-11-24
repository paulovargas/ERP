# Atualizacao da stack

## Objetivo

Levar o ERP para Java 25 e para a versao estavel atual do Angular, mantendo PostgreSQL e a instalacao/configuracao existente do WildFly 21.

## Estado confirmado

- API: `pom.xml` declara Java 11 e Spring Boot 2.7.6; o WAR usa o plugin `wildfly-maven-plugin` 3.0.2.Final.
- Documentacao do projeto aponta WildFly 21 e Servlet 4.0.
- Frontend atualizado para Angular, CLI, Material e CDK 22.3.0; TypeScript 6.0.3; Zone.js 0.16.3.
- Frontend registra Node.js 24.15.0 em `.nvmrc` e aceita somente linhas Node suportadas pelo Angular 22.
- Ambiente desta maquina: JDK 11, Maven 3.8.8 e Node.js 22.15.1. Nenhum arquivo versionado com Java 25 foi encontrado.

## Compatibilidade do backend

O requisito de Java 25 conflita com a preservacao do WildFly 21 para deploy do WAR. O WildFly 21 fornece Servlet 4.0. Spring Boot 4, que suporta Java 25, requer Servlet 6.1 em containers externos. Spring Boot 2.7.18 suporta containers Servlet 4.0, mas declara compatibilidade apenas ate Java 21. Portanto, nao ha combinacao oficialmente suportada que mantenha ao mesmo tempo Java 25, Spring Boot e deploy nesse WildFly.

Por instrucao do usuario, a versao/configuracao do WildFly permanece inalterada. O POM do backend tambem permanece sem mudancas ate ser definida uma combinacao de runtime suportada.

## Etapas e progresso

1. [x] Atualizar as versoes do frontend para Angular 22.3.0 e dependencias compativeis.
2. [x] Atualizar o lockfile, o requisito de Node e a documentacao do frontend.
3. [x] Atualizar o README e o blueprint para refletir os recursos e versoes atuais.
4. [ ] Atualizar a API para Java 25 depois de resolver a compatibilidade com o WildFly preservado.
5. [ ] Validar os builds com JDK 25 e Node.js suportado.

## Validacao prevista

- API: `mvn -f erp-api/pom.xml -DskipTests package` com a versao de Java aprovada e o runtime escolhido.
- Frontend: `npm ci` e `npm run build` com Node.js 24.15.0 ou outra versao aceita pelo Angular 22.
