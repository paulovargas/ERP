import { CommonModule } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import {
  ClientCreateRequest,
  ClientResponse,
  ClientUpdateRequest,
  UserStatus,
  UserType
} from '../../../../../core/models/client.model';

export interface ClientFormDialogData {
  client?: ClientResponse;
  entityLabel?: string;
}

export type ClientFormDialogResult =
  | { mode: 'create'; payload: ClientCreateRequest }
  | { mode: 'edit'; id: number; payload: ClientUpdateRequest };

@Component({
  selector: 'app-client-form-dialog',
  imports: [
    CommonModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule
  ],
  templateUrl: './client-form-dialog.component.html',
  styleUrls: ['./client-form-dialog.component.css']
})
export class ClientFormDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<ClientFormDialogComponent, ClientFormDialogResult>);
  private readonly formBuilder = inject(FormBuilder);
  readonly data = inject<ClientFormDialogData>(MAT_DIALOG_DATA);

  readonly isEdit = Boolean(this.data.client);
  readonly entityLabel = this.data.entityLabel ?? 'cliente';
  readonly title = this.isEdit ? `Editar ${this.entityLabel}` : `Adicionar ${this.entityLabel}`;
  readonly passwordLabel = this.isEdit ? 'Nova senha' : 'Senha';
  readonly passwordHint = this.isEdit ? 'Preencha apenas para alterar a senha.' : 'Minimo de 8 caracteres.';

  readonly userTypes = this.isEdit ? this.allUserTypes() : this.allUserTypes().filter((userType) => userType.value !== 'ADMIN');

  private allUserTypes(): Array<{ value: UserType; label: string }> {
    return [
    { value: 'BUYER', label: 'Comprador' },
    { value: 'SELLER', label: 'Vendedor' },
    { value: 'BUYER_SELLER', label: 'Comprador/Vendedor' },
    { value: 'ADMIN', label: 'Administrador' }
    ];
  }

  readonly statuses: Array<{ value: UserStatus; label: string }> = [
    { value: 'ACTIVE', label: 'Ativo' },
    { value: 'INACTIVE', label: 'Inativo' },
    { value: 'BLOCKED', label: 'Bloqueado' }
  ];

  readonly submitLabel = computed(() => (this.isEdit ? 'Salvar' : 'Adicionar'));

  readonly form = this.formBuilder.nonNullable.group({
    name: [this.data.client?.name ?? '', [Validators.required, Validators.maxLength(120)]],
    tradeName: [this.data.client?.tradeName ?? '', [Validators.maxLength(120)]],
    email: [this.data.client?.email ?? '', [Validators.required, Validators.email, Validators.maxLength(160)]],
    password: ['', this.isEdit ? [Validators.minLength(8), Validators.maxLength(72)] : [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
    document: [this.data.client?.document ?? '', [Validators.required, Validators.pattern(/^[0-9.\-/]{11,18}$/)]],
    phone: [this.data.client?.phone ?? '', [Validators.pattern(/^[0-9()+\-\s]{10,20}$/)]],
    userType: [this.data.client?.userType ?? 'BUYER' as UserType, [Validators.required]],
    status: [this.data.client?.status ?? 'ACTIVE' as UserStatus, [Validators.required]],
    sellerApproved: [this.data.client?.sellerApproved ?? false]
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const basePayload = {
      name: value.name.trim(),
      tradeName: this.optionalValue(value.tradeName),
      email: value.email.trim().toLowerCase(),
      document: value.document.trim(),
      phone: this.optionalValue(value.phone),
      userType: value.userType
    };

    if (!this.data.client) {
      this.dialogRef.close({
        mode: 'create',
        payload: {
          ...basePayload,
          password: value.password
        }
      });
      return;
    }

    const payload: ClientUpdateRequest = {
      ...basePayload,
      status: value.status,
      sellerApproved: value.sellerApproved
    };

    if (value.password.trim()) {
      payload.password = value.password;
    }

    this.dialogRef.close({
      mode: 'edit',
      id: this.data.client.id,
      payload
    });
  }

  close(): void {
    this.dialogRef.close();
  }

  hasError(controlName: keyof typeof this.form.controls, error: string): boolean {
    const control = this.form.controls[controlName];
    return control.touched && control.hasError(error);
  }

  private optionalValue(value: string): string | null {
    const trimmed = value.trim();
    return trimmed ? trimmed : null;
  }
}
