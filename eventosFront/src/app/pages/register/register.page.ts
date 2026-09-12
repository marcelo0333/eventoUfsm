import { Component, OnInit } from '@angular/core';
import { RegisterDTO, TokensResponse } from "../../models/auth.data.transfer.object";
import { Router } from "@angular/router";
import { RegisterService } from "../../service/register.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { HttpErrorResponse } from "@angular/common/http";
import { ToastController } from '@ionic/angular/standalone';
import { TokenService } from "../../service/auth.token.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
})
export class RegisterPage implements OnInit {
  public form!: FormGroup;
  public imgUser: File | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private registerService: RegisterService,
    private toastController: ToastController,
    private tokenService: TokenService
  ) { }

  ngOnInit() {
    this.initializeForm();
  }

  initializeForm(): void {
    this.form = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
      course:         [''],
      preferredTypes: [[]]
    });
  }



  register(): void {
    if (this.form.invalid) {
      this.showErrorToast('Por favor, preencha todos os campos corretamente.');
      return;
    }

    const registerDTO: RegisterDTO = {
      firstName: this.form.value.firstName,
      lastName: this.form.value.lastName,
      email: this.form.value.email,
      password: this.form.value.password,
    };


  const goToLogin = () => this.router.navigate(['/login']);

  const onSuccess = (tokens: TokensResponse) => {
    const course = this.form.value.course;
    const preferredTypes = this.form.value.preferredTypes;
    const hasPrefs = course || preferredTypes?.length > 0;

    if (!hasPrefs) {
      goToLogin();
      return;
    }

    // Guarda o token temporariamente para que o interceptor autentique a
    // chamada de preferências (o endpoint agora exige autenticação).
    this.tokenService.setTokens(tokens);
    this.registerService.savePreferences(tokens.userId, {
      course,
      preferredTypes: preferredTypes.join(',')
    }).subscribe({
      next: () => { this.tokenService.removeTokens(); goToLogin(); },
      error: () => { this.tokenService.removeTokens(); goToLogin(); }
    });
  };

  this.registerService.register(registerDTO).subscribe({
    next: (tokens: TokensResponse) => onSuccess(tokens),
    error: (error: HttpErrorResponse) => {
      this.showErrorToast('Não foi possível concluir o cadastro. Tente novamente.');
      console.error('Erro no registro', error);
    }
  });
}
  private showErrorToast(message: string): void {
    this.toastController.create({
      message,
      duration: 4000,
      buttons: [{ role: 'cancel', text: 'Dismiss' }],
      color: 'danger'
    }).then(toast => toast.present());
  }
}
