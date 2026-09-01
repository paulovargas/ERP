import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { ClientResponse, UserStatus, UserType } from '../../../core/models/client.model';
import { UsuarioService } from '../../../core/services/usuario.service';
import { ClientDeleteDialogComponent } from '../clients/pages/client-table/client-delete-dialog.component';
import { ClientFormDialogComponent, ClientFormDialogResult } from '../clients/pages/client-table/client-form-dialog.component';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatIconModule, MatInputModule, MatTableModule]
})
export class UsersComponent implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  private readonly dialog = inject(MatDialog);

  displayedColumns: string[] = ['id', 'name', 'email', 'userType', 'status', 'sellerApproved', 'updatedAt', 'action'];
  dataSource = new MatTableDataSource<ClientResponse>([]);
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  emptyMessage = 'Nenhum usuario cadastrado.';

  ngOnInit(): void {
    this.dataSource.filterPredicate = (user, filter) => this.getFilterText(user).includes(filter);
    this.loadUsers();
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openCreateDialog(): void {
    this.openFormDialog();
  }

  openEditDialog(user: ClientResponse): void {
    this.openFormDialog(user);
  }

  openDeleteDialog(user: ClientResponse): void {
    const dialogRef = this.dialog.open(ClientDeleteDialogComponent, {
      data: { client: user, entityLabel: 'usuario' },
      disableClose: true
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }

      this.isSaving = true;
      this.errorMessage = '';

      this.usuarioService.delete(user.id).subscribe({
        next: () => this.loadUsers(),
        error: (error: unknown) => {
          this.isSaving = false;
          this.errorMessage = this.getErrorMessage(error, 'delete');
        }
      });
    });
  }

  userTypeLabel(userType: UserType): string {
    const labels: Record<UserType, string> = {
      BUYER: 'Comprador',
      SELLER: 'Vendedor',
      BUYER_SELLER: 'Comprador/Vendedor',
      ADMIN: 'Administrador'
    };

    return labels[userType];
  }

  statusLabel(status: UserStatus): string {
    const labels: Record<UserStatus, string> = {
      ACTIVE: 'Ativo',
      INACTIVE: 'Inativo',
      BLOCKED: 'Bloqueado'
    };

    return labels[status];
  }

  approvalLabel(user: ClientResponse): string {
    if (user.userType === 'BUYER') {
      return '-';
    }

    return user.sellerApproved ? 'Aprovado' : 'Pendente';
  }

  private openFormDialog(user?: ClientResponse): void {
    const dialogRef = this.dialog.open(ClientFormDialogComponent, {
      data: { client: user, entityLabel: 'usuario' },
      disableClose: true,
      maxWidth: 'none',
      width: '760px'
    });

    dialogRef.afterClosed().subscribe((result?: ClientFormDialogResult) => {
      if (!result) {
        return;
      }

      this.isSaving = true;
      this.errorMessage = '';

      const request$ = result.mode === 'create'
        ? this.usuarioService.create(result.payload)
        : this.usuarioService.update(result.id, result.payload);

      request$.subscribe({
        next: () => this.loadUsers(),
        error: (error: unknown) => {
          this.isSaving = false;
          this.errorMessage = this.getErrorMessage(error, result.mode);
        }
      });
    });
  }

  private loadUsers(): void {
    this.isLoading = true;
    this.usuarioService.findAll().subscribe({
      next: (users) => {
        this.dataSource.data = users;
        this.isLoading = false;
        this.isSaving = false;
        this.errorMessage = '';
      },
      error: (error: unknown) => {
        this.dataSource.data = [];
        this.isLoading = false;
        this.isSaving = false;
        this.errorMessage = this.getErrorMessage(error, 'list');
      }
    });
  }

  private getErrorMessage(error: unknown, action: 'create' | 'edit' | 'delete' | 'list'): string {
    if (!(error instanceof HttpErrorResponse)) {
      return this.fallbackErrorMessage(action);
    }

    const apiMessage = this.getApiErrorMessage(error);
    if (apiMessage) {
      return apiMessage;
    }

    if (error.status === 401) {
      return 'Sessao expirada. Faca login novamente.';
    }

    if (error.status === 403) {
      return 'Seu usuario nao tem permissao para executar esta operacao.';
    }

    if (error.status === 404) {
      return 'Endpoint de usuarios nao encontrado no backend.';
    }

    if (error.status === 400 || error.status === 409) {
      return 'Verifique os dados informados e tente novamente.';
    }

    return this.fallbackErrorMessage(action);
  }

  private getApiErrorMessage(error: HttpErrorResponse): string {
    const body = error.error as { message?: unknown; fields?: Record<string, string> } | null;

    if (!body || typeof body !== 'object') {
      return '';
    }

    if (body.fields && Object.keys(body.fields).length > 0) {
      return Object.entries(body.fields)
        .map(([field, message]) => `${field}: ${message}`)
        .join(' ');
    }

    return typeof body.message === 'string' ? body.message : '';
  }

  private fallbackErrorMessage(action: 'create' | 'edit' | 'delete' | 'list'): string {
    const messages = {
      create: 'Nao foi possivel adicionar o usuario.',
      edit: 'Nao foi possivel editar o usuario.',
      delete: 'Nao foi possivel excluir o usuario.',
      list: 'Nao foi possivel carregar os usuarios.'
    };

    return messages[action];
  }

  private getFilterText(user: ClientResponse): string {
    return [
      user.id,
      user.name,
      user.tradeName,
      user.email,
      user.document,
      user.phone,
      this.userTypeLabel(user.userType),
      this.statusLabel(user.status),
      this.approvalLabel(user)
    ]
      .filter((value) => value !== null && value !== undefined)
      .join(' ')
      .toLowerCase();
  }
}
