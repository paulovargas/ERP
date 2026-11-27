# Plano de evolução para SaaS multiempresa

## Escopo e premissas

Evoluir o ERP em entregas pequenas, revisáveis e com um commit independente por etapa. A arquitetura inicial será uma aplicação compartilhada com isolamento lógico por empresa (tenant) no mesmo banco PostgreSQL. O usuário autenticado pertencerá a uma empresa; cada domínio de negócio deverá filtrar e validar o tenant no servidor. Planos, cobrança e infraestrutura de produção ficam para etapas posteriores.

## Etapas e commits previstos

1. **Fundação de tenants** — entidade Empresa, vínculo obrigatório de usuários a empresas, configuração/default para bootstrap e documentação da compatibilidade com dados legados. Commit: `feat: add company tenant foundation`.
2. **Contexto de tenant na autenticação** — carregar empresa do usuário e disponibilizar contexto confiável por requisição. Commit: `feat: resolve tenant from authenticated user`.
3. **Isolamento dos endpoints** — escopar consultas e mutações de usuários, endereços e menus; impedir acesso cruzado; adicionar testes de autorização. Commit: `feat: enforce tenant data isolation`.
4. **Cadastro e administração de empresas** — fluxo seguro de provisionamento e gerenciamento de usuários da empresa. Commit: `feat: add company onboarding`.
5. **Operação SaaS** — avaliar planos, assinatura, cobrança, limites, observabilidade e deploy após definição do modelo comercial. Commits separados por capacidade.

## Entrega atual

Concluída a etapa 1: entidade `Company`, vínculo `Client.company` compatível com legado, bootstrap configurável da empresa padrão, associação dos usuários existentes e documentação. Não inclui planos, cobrança, interface ou isolamento de endpoints.

## Critérios da etapa 1

- Empresa persistida com identificador, nome, documento e timestamps.
- Usuários associados a uma empresa por chave estrangeira.
- Bootstrap cria ou reutiliza uma empresa padrão e associa o administrador inicial.
- Configuração/documentação explicam a empresa padrão e a estratégia de migração.
- Validar compilação da API; não alegar isolamento de tenant até a etapa 3.

## Próxima entrega

Etapa 2: incluir o identificador de empresa no contexto de autenticação confiável e disponibilizá-lo nas requisições autenticadas, antes de iniciar a restrição das consultas na etapa 3.
