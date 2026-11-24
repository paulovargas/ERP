# Plano: remover WildFly da ERP API

## Escopo concluído
- Java 25 como versão de compilação.
- API empacotada como JAR Spring Boot com Tomcat embutido.
- PostgreSQL local configurável por `ERP_DB_URL`, `ERP_DB_USER` e `ERP_DB_PASSWORD`.
- Configurações de servidor externo e JNDI removidas do runtime e da documentação.
- Spring Boot 3.5.16; APIs de Persistence, Validation e Servlet migradas para Jakarta.
- Spring Security atualizado para `requestMatchers`.
- Context path `/erp-api` mantido para preservar as URLs usadas pelo frontend.

## Etapas concluídas
- [x] Atualizar POM e remover plugin/configuração de deploy externo.
- [x] Migrar código para Jakarta e atualizar segurança.
- [x] Retirar datasource JNDI e documentar execução local como JAR.
- [x] Preservar o prefixo `/erp-api` das rotas do frontend.

## Validação
- POM revisado: Spring Boot 3.5.16, empacotamento JAR e plugin de deploy externo ausente.
- Build Maven não executado: o executor de comandos falhou ao iniciar o processo no projeto.
