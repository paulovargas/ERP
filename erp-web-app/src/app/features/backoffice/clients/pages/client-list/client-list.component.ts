import { Component } from '@angular/core';

@Component({
  selector: 'app-client-list',
  template: `
    <main class="page">
      <h1>Clientes</h1>
    </main>
  `,
  styles: [
    `
      .page {
        font-family: Arial, Helvetica, sans-serif;
        padding: 32px;
      }
    `
  ]
})
export class ClientListComponent {}
