create table if not exists menus (
    id bigserial primary key,
    name varchar(120) not null,
    address varchar(160),
    position bigint,
    root bigint
);

create table if not exists client_menus (
    client_id bigint not null,
    menu_id bigint not null,
    primary key (client_id, menu_id),
    constraint fk_client_menus_client foreign key (client_id) references clients (id) on delete cascade,
    constraint fk_client_menus_menu foreign key (menu_id) references menus (id) on delete cascade
);

create unique index if not exists uk_menus_name_address
    on menus (name, coalesce(address, ''));

insert into menus (name, address, position, root) values
    ('Sistema', null, 0, null),
    ('Cadastros', null, 1, null),
    ('Movimento', null, 2, null),
    ('Relatorios', null, 3, null),
    ('Integracao', null, 4, null),
    ('Dashboard', '/backoffice/dashboard', 0, null),
    ('Usuarios', '/backoffice/usuarios', 1, null),
    ('Perfis de Acesso', '/backoffice/perfis-acesso', 2, null),
    ('Parametros Gerais', '/backoffice/parametros-gerais', 3, null),
    ('Auditoria', '/backoffice/auditoria', 4, null),
    ('Clientes', '/backoffice/clientes', 0, null),
    ('Fornecedores', '/backoffice/fornecedores', 1, null),
    ('Produtos', '/backoffice/produtos', 2, null),
    ('Servicos', '/backoffice/servicos', 3, null),
    ('Transportadoras', '/backoffice/transportadoras', 4, null),
    ('Condicoes de Pagamento', '/backoffice/condicoes-pagamento', 5, null),
    ('Centros de Custo', '/backoffice/centros-custo', 6, null),
    ('Pedidos de Venda', '/backoffice/pedidos-venda', 0, null),
    ('Compras', '/backoffice/compras', 1, null),
    ('Estoque', '/backoffice/estoque', 2, null),
    ('Faturamento', '/backoffice/faturamento', 3, null),
    ('Contas a Receber', '/backoffice/contas-receber', 4, null),
    ('Contas a Pagar', '/backoffice/contas-pagar', 5, null),
    ('Orcamentos', '/backoffice/orcamentos', 6, null),
    ('Vendas por Periodo', '/backoffice/relatorios/vendas-periodo', 0, null),
    ('Fluxo de Caixa', '/backoffice/relatorios/fluxo-caixa', 1, null),
    ('Posicao de Estoque', '/backoffice/relatorios/posicao-estoque', 2, null),
    ('Inadimplencia', '/backoffice/relatorios/inadimplencia', 3, null),
    ('DRE Gerencial', '/backoffice/relatorios/dre-gerencial', 4, null),
    ('Importacao de Dados', '/backoffice/integracao/importacao-dados', 0, null),
    ('Exportacao Fiscal', '/backoffice/integracao/exportacao-fiscal', 1, null),
    ('Notas Fiscais', '/backoffice/integracao/notas-fiscais', 2, null),
    ('Bancos', '/backoffice/integracao/bancos', 3, null),
    ('APIs Externas', '/backoffice/integracao/apis-externas', 4, null)
on conflict (name, (coalesce(address, ''))) do update set
    position = excluded.position,
    root = excluded.root;

with menu_parents(name, address, parent_name) as (
    values
        ('Dashboard', '/backoffice/dashboard', 'Sistema'),
        ('Usuarios', '/backoffice/usuarios', 'Sistema'),
        ('Perfis de Acesso', '/backoffice/perfis-acesso', 'Sistema'),
        ('Parametros Gerais', '/backoffice/parametros-gerais', 'Sistema'),
        ('Auditoria', '/backoffice/auditoria', 'Sistema'),
        ('Clientes', '/backoffice/clientes', 'Cadastros'),
        ('Fornecedores', '/backoffice/fornecedores', 'Cadastros'),
        ('Produtos', '/backoffice/produtos', 'Cadastros'),
        ('Servicos', '/backoffice/servicos', 'Cadastros'),
        ('Transportadoras', '/backoffice/transportadoras', 'Cadastros'),
        ('Condicoes de Pagamento', '/backoffice/condicoes-pagamento', 'Cadastros'),
        ('Centros de Custo', '/backoffice/centros-custo', 'Cadastros'),
        ('Pedidos de Venda', '/backoffice/pedidos-venda', 'Movimento'),
        ('Compras', '/backoffice/compras', 'Movimento'),
        ('Estoque', '/backoffice/estoque', 'Movimento'),
        ('Faturamento', '/backoffice/faturamento', 'Movimento'),
        ('Contas a Receber', '/backoffice/contas-receber', 'Movimento'),
        ('Contas a Pagar', '/backoffice/contas-pagar', 'Movimento'),
        ('Orcamentos', '/backoffice/orcamentos', 'Movimento'),
        ('Vendas por Periodo', '/backoffice/relatorios/vendas-periodo', 'Relatorios'),
        ('Fluxo de Caixa', '/backoffice/relatorios/fluxo-caixa', 'Relatorios'),
        ('Posicao de Estoque', '/backoffice/relatorios/posicao-estoque', 'Relatorios'),
        ('Inadimplencia', '/backoffice/relatorios/inadimplencia', 'Relatorios'),
        ('DRE Gerencial', '/backoffice/relatorios/dre-gerencial', 'Relatorios'),
        ('Importacao de Dados', '/backoffice/integracao/importacao-dados', 'Integracao'),
        ('Exportacao Fiscal', '/backoffice/integracao/exportacao-fiscal', 'Integracao'),
        ('Notas Fiscais', '/backoffice/integracao/notas-fiscais', 'Integracao'),
        ('Bancos', '/backoffice/integracao/bancos', 'Integracao'),
        ('APIs Externas', '/backoffice/integracao/apis-externas', 'Integracao')
)
update menus child
set root = parent.id
from menu_parents mp
join menus parent on parent.name = mp.parent_name and parent.address is null
where child.name = mp.name
  and child.address = mp.address;

insert into client_menus (client_id, menu_id)
select c.id, m.id
from clients c
cross join menus m
where c.user_type = 'ADMIN'
on conflict do nothing;
