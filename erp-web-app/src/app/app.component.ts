import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  protected readonly navigation = ['Dashboard', 'Pessoas', 'Contas', 'Lancamentos', 'Extratos'];

  protected readonly metrics = [
    { label: 'Entidades', value: '7', detail: 'dominio financeiro' },
    { label: 'Endpoints', value: '15+', detail: 'CRUD e consultas' },
    { label: 'Frontend', value: 'Angular 19', detail: 'SPA responsiva' },
    { label: 'Deploy', value: 'WAR', detail: 'WildFly + PostgreSQL' }
  ];

  protected readonly workflow = [
    {
      index: '01',
      title: 'Cadastro de pessoas',
      description: 'Base de clientes ou usuarios vinculada a contas e lancamentos contabeis.'
    },
    {
      index: '02',
      title: 'Contas financeiras',
      description: 'Controle de contas por numero, titular e validacao de duplicidade na API.'
    },
    {
      index: '03',
      title: 'Lancamentos e saldo',
      description: 'Registro de operacoes e consulta consolidada de saldo por pessoa.'
    },
    {
      index: '04',
      title: 'Extrato por conta',
      description: 'Consulta de movimentacoes para leitura operacional e auditoria simples.'
    }
  ];

  protected readonly skills = [
    'Java',
    'Spring Boot',
    'REST API',
    'JPA',
    'PostgreSQL',
    'WildFly',
    'Angular',
    'TypeScript',
    'Swagger',
    'Git'
  ];
}
