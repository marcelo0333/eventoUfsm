import { Component, OnInit } from '@angular/core';
import { RegisterDTO, TokensResponse } from "../../models/auth.data.transfer.object";
import { Router } from "@angular/router";
import { RegisterService } from "../../service/register.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { HttpErrorResponse } from "@angular/common/http";
import { ToastController } from '@ionic/angular/standalone';

@Component({
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
})
export class RegisterPage implements OnInit {
  public form!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private registerService: RegisterService,
    private toastController: ToastController
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

    this.registerService.register(registerDTO).subscribe({
      next: (response: TokensResponse) => {
        this.router.navigate(['']);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erro ao registrar:', error);
        this.showErrorToast('Erro ao registrar. Tente novamente mais tarde.');
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
