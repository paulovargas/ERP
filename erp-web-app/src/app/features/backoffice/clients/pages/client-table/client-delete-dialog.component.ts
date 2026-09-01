import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';

import { ClientResponse } from '../../../../../core/models/client.model';

export interface ClientDeleteDialogData {
  client: ClientResponse;
  entityLabel?: string;
}

@Component({
  selector: 'app-client-delete-dialog',
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatIconModule],
  templateUrl: './client-delete-dialog.component.html',
  styleUrls: ['./client-delete-dialog.component.css']
})
export class ClientDeleteDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<ClientDeleteDialogComponent, boolean>);
  private readonly data = inject<ClientDeleteDialogData | ClientResponse>(MAT_DIALOG_DATA);
  readonly client = 'client' in this.data ? this.data.client : this.data;
  readonly entityLabel = 'client' in this.data ? this.data.entityLabel ?? 'cliente' : 'cliente';

  confirm(): void {
    this.dialogRef.close(true);
  }

  close(): void {
    this.dialogRef.close(false);
  }
}
