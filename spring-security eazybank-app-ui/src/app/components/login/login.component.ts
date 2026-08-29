import { Component, OnInit } from '@angular/core';
import { User } from "src/app/model/user.model";
import { NgForm } from '@angular/forms';
import { LoginService } from 'src/app/services/login/login.service';
import { Router } from '@angular/router';
import { getCookie } from 'typescript-cookie';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  authStatus: string = "";
  model = new User();

  constructor(private loginService: LoginService, private router: Router) {

   }

  ngOnInit(): void {

  }

validateUser(loginForm: NgForm) {
    const plainPassword = this.model.password; // capture the real plaintext password BEFORE it gets overwritten

    this.loginService.validateLoginDetails(this.model).subscribe(
      responseData => {
        this.model = <any> responseData.body;
        this.model.password = plainPassword; // restore the correct plaintext password into the new model
        this.model.authStatus = 'AUTH';
        window.sessionStorage.setItem("userdetails", JSON.stringify(this.model));
        let csrf = getCookie("XSRF-TOKEN");
        window.sessionStorage.setItem("XSRF-TOKEN", csrf!);
        this.router.navigate(['dashboard']);
      });
}

}
