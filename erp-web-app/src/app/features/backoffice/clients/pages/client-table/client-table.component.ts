import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

import { ClientResponse, UserStatus, UserType } from '../../../../../core/models/client.model';
import { ClientService } from '../../../../../core/services/client.service';
import { ClientDeleteDialogComponent } from './client-delete-dialog.component';
import { ClientFormDialogComponent, ClientFormDialogResult } from './client-form-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-client-table',
  templateUrl: './client-table.component.html',
  styleUrls: ['./client-table.component.css'],
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatIconModule, MatInputModule, MatTableModule]
})
export class ClientTableComponent implements OnInit {
  private readonly clientService = inject(ClientService);
  private readonly dialog = inject(MatDialog);

  displayedColumns: string[] = ['id', 'name', 'email', 'document', 'phone', 'userType', 'status', 'action'];
  dataSource = new MatTableDataSource<ClientResponse>([]);
  isLoading = true;
  isSaving = false;
  errorMessage = '';
  emptyMessage = 'Nenhum cliente cadastrado.';

  ngOnInit(): void {
    this.dataSource.filterPredicate = (client, filter) => this.getFilterText(client).includes(filter);
    this.loadClients();
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openCreateDialog(): void {
    this.openFormDialog();
  }

  openEditDialog(client: ClientResponse): void {
    this.openFormDialog(client);
  }

  openDeleteDialog(client: ClientResponse): void {
    const dialogRef = this.dialog.open(ClientDeleteDialogComponent, {
      data: client,
      disableClose: true
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }

      this.isSaving = true;
      this.errorMessage = '';

      this.clientService.delete(client.id).subscribe({
        next: () => this.loadClients(),
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

  private openFormDialog(client?: ClientResponse): void {
    const dialogRef = this.dialog.open(ClientFormDialogComponent, {
      data: { client },
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
        ? this.clientService.create(result.payload)
        : this.clientService.update(result.id, result.payload);

      request$.subscribe({
        next: () => this.loadClients(),
        error: (error: unknown) => {
          this.isSaving = false;
          this.errorMessage = this.getErrorMessage(error, result.mode);
        }
      });
    });
  }

  private loadClients(): void {
    this.isLoading = true;
    this.clientService.findAll().subscribe({
      next: (clients) => {
        this.dataSource.data = clients;
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
      return 'Endpoint de clientes nao encontrado no backend.';
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
      create: 'Nao foi possivel adicionar o cliente.',
      edit: 'Nao foi possivel editar o cliente.',
      delete: 'Nao foi possivel excluir o cliente.',
      list: 'Nao foi possivel carregar os clientes.'
    };

    return messages[action];
  }

  private getFilterText(client: ClientResponse): string {
    return [
      client.id,
      client.name,
      client.tradeName,
      client.email,
      client.document,
      client.phone,
      this.userTypeLabel(client.userType),
      this.statusLabel(client.status)
    ]
      .filter((value) => value !== null && value !== undefined)
      .join(' ')
      .toLowerCase();
  }
}
