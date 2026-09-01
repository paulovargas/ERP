package com.paulovargas.erpapi.config;

import com.paulovargas.erpapi.entity.Client;
import com.paulovargas.erpapi.entity.Menu;
import com.paulovargas.erpapi.entity.UserType;
import com.paulovargas.erpapi.repository.ClientRepository;
import com.paulovargas.erpapi.repository.MenuRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DefaultMenuConfig {

    @Bean
    @Order(2)
    public CommandLineRunner createDefaultMenus(
            MenuRepository menuRepository,
            ClientRepository clientRepository,
            TransactionTemplate transactionTemplate
    ) {
        return args -> transactionTemplate.executeWithoutResult(status -> {
            List<MenuSeed> menuSeeds = List.of(
                    new MenuSeed("Sistema", null, 0L, null),
                    new MenuSeed("Cadastros", null, 1L, null),
                    new MenuSeed("Movimento", null, 2L, null),
                    new MenuSeed("Relatorios", null, 3L, null),
                    new MenuSeed("Integracao", null, 4L, null),
                    new MenuSeed("Dashboard", "/backoffice/dashboard", 0L, "Sistema"),
                    new MenuSeed("Usuarios", "/backoffice/usuarios", 1L, "Sistema"),
                    new MenuSeed("Perfis de Acesso", "/backoffice/perfis-acesso", 2L, "Sistema"),
                    new MenuSeed("Parametros Gerais", "/backoffice/parametros-gerais", 3L, "Sistema"),
                    new MenuSeed("Auditoria", "/backoffice/auditoria", 4L, "Sistema"),
                    new MenuSeed("Clientes", "/backoffice/clientes", 0L, "Cadastros"),
                    new MenuSeed("Fornecedores", "/backoffice/fornecedores", 1L, "Cadastros"),
                    new MenuSeed("Produtos", "/backoffice/produtos", 2L, "Cadastros"),
                    new MenuSeed("Servicos", "/backoffice/servicos", 3L, "Cadastros"),
                    new MenuSeed("Transportadoras", "/backoffice/transportadoras", 4L, "Cadastros"),
                    new MenuSeed("Condicoes de Pagamento", "/backoffice/condicoes-pagamento", 5L, "Cadastros"),
                    new MenuSeed("Centros de Custo", "/backoffice/centros-custo", 6L, "Cadastros"),
                    new MenuSeed("Pedidos de Venda", "/backoffice/pedidos-venda", 0L, "Movimento"),
                    new MenuSeed("Compras", "/backoffice/compras", 1L, "Movimento"),
                    new MenuSeed("Estoque", "/backoffice/estoque", 2L, "Movimento"),
                    new MenuSeed("Faturamento", "/backoffice/faturamento", 3L, "Movimento"),
                    new MenuSeed("Contas a Receber", "/backoffice/contas-receber", 4L, "Movimento"),
                    new MenuSeed("Contas a Pagar", "/backoffice/contas-pagar", 5L, "Movimento"),
                    new MenuSeed("Orcamentos", "/backoffice/orcamentos", 6L, "Movimento"),
                    new MenuSeed("Vendas por Periodo", "/backoffice/relatorios/vendas-periodo", 0L, "Relatorios"),
                    new MenuSeed("Fluxo de Caixa", "/backoffice/relatorios/fluxo-caixa", 1L, "Relatorios"),
                    new MenuSeed("Posicao de Estoque", "/backoffice/relatorios/posicao-estoque", 2L, "Relatorios"),
                    new MenuSeed("Inadimplencia", "/backoffice/relatorios/inadimplencia", 3L, "Relatorios"),
                    new MenuSeed("DRE Gerencial", "/backoffice/relatorios/dre-gerencial", 4L, "Relatorios"),
                    new MenuSeed("Importacao de Dados", "/backoffice/integracao/importacao-dados", 0L, "Integracao"),
                    new MenuSeed("Exportacao Fiscal", "/backoffice/integracao/exportacao-fiscal", 1L, "Integracao"),
                    new MenuSeed("Notas Fiscais", "/backoffice/integracao/notas-fiscais", 2L, "Integracao"),
                    new MenuSeed("Bancos", "/backoffice/integracao/bancos", 3L, "Integracao"),
                    new MenuSeed("APIs Externas", "/backoffice/integracao/apis-externas", 4L, "Integracao")
            );

            Map<String, Menu> rootMenus = menuSeeds.stream()
                    .filter(MenuSeed::isRoot)
                    .map(seed -> saveMenu(menuRepository, seed, null))
                    .collect(Collectors.toMap(Menu::getName, Function.identity()));

            Set<Menu> menus = new LinkedHashSet<>(rootMenus.values());
            menuSeeds.stream()
                    .filter(seed -> !seed.isRoot())
                    .map(seed -> saveMenu(menuRepository, seed, rootMenus.get(seed.rootName).getId()))
                    .forEach(menus::add);

            for (Client admin : clientRepository.findByUserType(UserType.ADMIN)) {
                admin.getMenus().addAll(menus);
                clientRepository.save(admin);
            }
        });
    }

    private Menu saveMenu(MenuRepository menuRepository, MenuSeed seed, Long root) {
        Menu menu = menuRepository.findByNameAndAddress(seed.name, seed.address)
                .orElseGet(Menu::new);

        menu.setName(seed.name);
        menu.setAddress(seed.address);
        menu.setPosition(seed.position);
        menu.setRoot(root);
        return menuRepository.save(menu);
    }

    private static class MenuSeed {
        private final String name;
        private final String address;
        private final Long position;
        private final String rootName;

        private MenuSeed(String name, String address, Long position, String rootName) {
            this.name = name;
            this.address = address;
            this.position = position;
            this.rootName = rootName;
        }

        private boolean isRoot() {
            return rootName == null;
        }
    }
}

