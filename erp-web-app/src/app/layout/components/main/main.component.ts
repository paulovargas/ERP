import { Component, OnInit } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  imports: [RouterModule, RouterOutlet]
})
export class MainComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
