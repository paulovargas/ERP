/* tslint:disable:no-unused-variable */
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuDropdownComponent } from './menu-dropdown.component';

describe('MenuDropdownComponent', () => {
  let component: MenuDropdownComponent;
  let fixture: ComponentFixture<MenuDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ MenuDropdownComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mock the menu list expected by the template', () => {
    expect(component.listaMenus.length).toBeGreaterThan(0);
    expect(component.listaMenus.some(menu => menu.endereco === null)).toBeTrue();
    expect(component.listaMenus.some(menu => menu.raiz === 3)).toBeTrue();
  });

  it('should transform a menu label into a route slug', () => {
    expect(component.transformaEmRota('Lançamentos Contábeis')).toBe('lancamentos-contabeis');
  });

  it('should store the selected mock route when opening a modal', () => {
    component.abreModal('pessoas');

    expect(component.rotaSelecionada).toBe('pessoas');
  });
});
