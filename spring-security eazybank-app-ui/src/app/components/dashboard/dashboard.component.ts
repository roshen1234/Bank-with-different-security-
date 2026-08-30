import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/user.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  user = new User();
  roleNames: string = '';

  constructor() {
    
  }

  ngOnInit() {
    if(sessionStorage.getItem('userdetails')){
      this.user = JSON.parse(sessionStorage.getItem('userdetails') || "");
       if (this.user.roles && this.user.roles.length > 0) {
        this.roleNames = this.user.roles.map(r => r.name).join(', ');
      }
    }
  }

}
