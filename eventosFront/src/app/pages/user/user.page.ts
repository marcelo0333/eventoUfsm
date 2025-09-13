import { Component, OnInit } from '@angular/core';
import { TokenService } from "../../service/auth.token.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { UserModel, TokensResponse } from "../../models/auth.data.transfer.object";
import { Router } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import { ToastController } from "@ionic/angular";
import { UserDetailService } from "../../service/user.service";

@Component({
  selector: 'app-user',
  templateUrl: './user.page.html',
  styleUrls: ['./user.page.scss'],
})
export class UserPage implements OnInit {
  public userModel!: UserModel | null;
  public form!: FormGroup;
  public isModalOpen: boolean = false;
  public isAdmin: boolean = false;

  constructor(
    private tokenService: TokenService,
    private formBuilder: FormBuilder,
    private router: Router,
    private toastController: ToastController,
    private userService: UserDetailService,
  ) { }

  ngOnInit() {
    this.userModel = this.tokenService.getUserFromToken();
    this.isAdmin = this.userModel?.role === 'ADMIN';
    this.initializeForm();
  }

  initializeForm(): void {
    this.form = this.formBuilder.group({
      firstName: [this.userModel?.firstName || '', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      lastName: [this.userModel?.lastName || '', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: [this.userModel?.email || '', [Validators.required, Validators.email]],
    });
  }

  openEditModal() {
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  edit(): void {
    if (this.form.invalid) {
      this.showErrorToast('Por favor, preencha todos os campos corretamente.');
      return;
    }

    const editDTO: UserModel = {
      firstName: this.form.value.firstName,
      lastName: this.form.value.lastName,
      email: this.form.value.email,
    };

    this.userService.editUser(editDTO).subscribe({
      next: (response: TokensResponse) => {
        this.closeModal();
        this.showSuccessToast('Informações atualizadas com sucesso!');
        this.userModel = this.tokenService.getUserFromToken();
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erro ao atualizar:', error);
        this.closeModal();
        this.showErrorToast('Erro ao atualizar. Tente novamente mais tarde.');
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

  private showSuccessToast(message: string): void {
    this.toastController.create({
      message,
      duration: 4000,
      buttons: [{ role: 'cancel', text: 'OK' }],
      color: 'success'
    }).then(toast => toast.present());
  }

  logout(): void {
    this.tokenService.storageClear();
    this.router.navigate(['login']);
  }

  openCreateEventModal() {
    this.router.navigate(['tabs/events-creator']);
  }
}
