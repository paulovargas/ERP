import { Component, OnInit } from '@angular/core';
import { NavigationComponent } from '../../layout/components/navigation/navigation.component';
import { MainComponent } from '../../layout/components/main/main.component';
import { FooterComponent } from '../../layout/components/footer/footer.component';

@Component({
  selector: 'app-backoffice',
  templateUrl: './backoffice.component.html',
  styleUrls: ['./backoffice.component.css'],
  imports: [NavigationComponent, MainComponent, FooterComponent]
})
export class BackofficeComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
