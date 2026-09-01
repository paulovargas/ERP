import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface MenuItem {
  nome: string;
  endereco: string | null;
  posicao?: number;
  raiz?: number;
}

@Component({
  selector: 'modal-content',
  template: ''
})
export class ModalContentComponent {}

@Component({
  selector: 'app-menu-dropdown',
  imports: [CommonModule, ModalContentComponent],
  templateUrl: './menu-dropdown.component.html',
  styleUrls: ['./menu-dropdown.component.css']
})
export class MenuDropdownComponent implements OnInit {

  listaMenus: MenuItem[] = [
    { nome: 'Sistema', endereco: null, posicao: 0 },
    { nome: 'Cadastros', endereco: null, posicao: 1 },
    { nome: 'Movimento', endereco: null, posicao: 2 },
    { nome: 'Relatorios', endereco: null, posicao: 3 },
    { nome: 'Integracao', endereco: null, posicao: 4 },
    { nome: 'Usuarios', endereco: '/usuarios', raiz: 1 },
    { nome: 'Permissoes', endereco: '/permissoes', raiz: 1 },
    { nome: 'Pessoas', endereco: '/pessoas', raiz: 3 },
    { nome: 'Contas', endereco: '/contas', raiz: 3 },
    { nome: 'Lancamentos', endereco: '/lancamentos', raiz: 7 },
    { nome: 'Extrato', endereco: '/extrato', raiz: 16 },
    { nome: 'Saldos', endereco: '/saldos', raiz: 16 },
    { nome: 'API ERP', endereco: '/api-erp', raiz: 13 }
  ];

  rotaSelecionada = '';

  constructor() { }

  ngOnInit() {
  }

  transformaEmRota(nome: string): string {
    return nome
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .trim()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '');
  }

  abreModal(rota: string): void {
    this.rotaSelecionada = rota;
  }
}
