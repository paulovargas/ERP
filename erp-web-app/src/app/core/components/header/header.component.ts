import { Component, OnInit } from '@angular/core';
import { MenuDropdownComponent } from '../menu-dropdown/menu-dropdown.component';

@Component({
  selector: 'app-header',
  imports: [MenuDropdownComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
