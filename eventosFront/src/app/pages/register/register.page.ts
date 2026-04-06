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
  public imgUser: File | null = null;
  public isPrivilege: boolean = false;

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


  const onSuccess = (response: any, userId: number) => {
    // Salva preferências se preenchidas
    const course         = this.form.value.course;
    const preferredTypes = this.form.value.preferredTypes;

    if (course || preferredTypes?.length > 0) {
      this.registerService.savePreferences(userId, {
        course,
        preferredTypes: preferredTypes.join(',')
      }).subscribe();
    }

    this.router.navigate(['/login']);
  };

  const register$ = this.isPrivilege
    ? this.registerService.privilege(registerDTO)
    : this.registerService.register(registerDTO);

  register$.subscribe({
    next: (response: TokensResponse) => onSuccess(response, response.userId),
    error: (error: HttpErrorResponse) => console.error('Erro no registro', error)
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
