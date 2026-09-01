import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { MenuItem } from '../../../core/models/menu.model';
import { MenuService } from '../../../core/services/menu.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
  imports: [CommonModule]
})
export class NavigationComponent implements OnInit {
  listaMenus: MenuItem[] = [];
  private readonly legacyRootByPosition = new Map<number, number>([
    [0, 1],
    [1, 3],
    [2, 7],
    [3, 16],
    [4, 13]
  ]);

  constructor(
    private readonly router: Router,
    private readonly menuService: MenuService
  ) { }

  ngOnInit() {
    this.menuService.findMyMenus().subscribe({
      next: (menus) => {
        this.listaMenus = menus;
      },
      error: () => {
        this.listaMenus = [];
      }
    });
  }

  abreModal(input: string | null): void {
    if (!input) {
      return;
    }

    void this.router.navigate(['/backoffice', { outlets: { main: [this.getOutletPath(input)] } }]);
  }

  menusRaiz(): MenuItem[] {
    return this.listaMenus.filter((menu) => menu.endereco === null);
  }

  submenus(menu: MenuItem): MenuItem[] {
    const legacyRoot = menu.posicao === null ? null : this.legacyRootByPosition.get(menu.posicao);

    return this.listaMenus.filter((submenu) =>
      submenu.endereco !== null &&
      (submenu.raiz === menu.id || submenu.raiz === legacyRoot)
    );
  }

  private getOutletPath(address: string): string {
    const backofficePrefix = '/backoffice/';

    return address.startsWith(backofficePrefix)
      ? address.substring(backofficePrefix.length)
      : address.replace(/^\//, '');
  }

}
