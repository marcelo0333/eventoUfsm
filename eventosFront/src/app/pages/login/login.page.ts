import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { ToastController } from "@ionic/angular";
import { LoginService } from "../../service/login.service";
import { LoginDTO, SignInResponse } from "../../models/auth.data.transfer.object";
import { TokenService } from "../../service/auth.token.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage implements OnInit {

  form!: FormGroup;
  showPassword = false;

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private loginService: LoginService,
    private toastController: ToastController,
    private tokenService: TokenService,
  ) { }

  ngOnInit() {
    this.initializeForm();
  }

  initializeForm(): void {
    this.form = this.createLoginForm();
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  login(): void {
    if (this.form.invalid) {
      return;
    }

    const credentials: LoginDTO = {
      email: this.form.value.email,
      password: this.form.value.password,
    };

    this.loginService.login(credentials).subscribe({
      next: (response: SignInResponse) => {
        this.tokenService.setTokens(response.tokens);
        this.tokenService.setUser(response.user);
        this.router.navigate(['']);
      },
      error: (error: any) => {
        this.showErrorToast(error.message);
        console.error('Erro ao efetuar login:', error);
      }
    });
  }

  private showErrorToast(message: string): void {
    this.toastController.create({
      message: message,
      duration: 4000,
      buttons: [{
        role: 'cancel',
        text: 'Dismiss'
      }],
      color: 'danger'
    }).then(toast => toast.present());
  }

  cancel(): void {
    this.router.navigate(['register']);
  }

  private createLoginForm(): FormGroup {
    return this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

}
