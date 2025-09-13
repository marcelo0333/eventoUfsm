import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastController } from '@ionic/angular';
import {EventService} from "../../service/event.service";
import {Local, Locals} from "../../models/events.model";
import {TokenService} from "../../service/auth.token.service";

@Component({
  selector: 'app-events-creator',
  templateUrl: './events-creator.page.html',
  styleUrls: ['./events-creator.page.scss'],
})
export class EventsCreatorPage implements OnInit {
  public form!: FormGroup;
  public imgEvent!: File;
  public imgEventError = false;
  public locals: Local[] = [] ;
  public userId!: number | undefined;
  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private eventService: EventService,
    private toastController: ToastController,
    private tokenService: TokenService
  ) {}

  ngOnInit() {
    this.userId = this.tokenService.getUserFromToken()?.userId;
    this.initializeForm();
    this.loadLocals();
  }

  initializeForm(): void {
    this.form = this.formBuilder.group({
      eventName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      typeEvent: ['', [Validators.required]],
      description: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
      dateInitial: ['', [Validators.required]],
      dateFinal: ['', [Validators.required]],
      centerName: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
      contact: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
      userId: [this.userId],
      localIds: [[], Validators.required] // Add form control for local IDs
    });
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.imgEvent = file;
      this.imgEventError = false;
    } else {
      this.imgEventError = true;
    }
  }

  register(): void {
    if (this.form.invalid || !this.imgEvent) {
      this.showErrorToast('Por favor, preencha todos os campos corretamente.');
      this.imgEventError = !this.imgEvent;
      return;
    }


    const eventData = {
      eventName: this.form.value.eventName,
      typeEvent: this.form.value.typeEvent,
      description: this.form.value.description,
      dateInitial: this.form.value.dateInitial,
      dateFinal: this.form.value.dateFinal,
      centerName: this.form.value.centerName,
      contact: this.form.value.contact,
      userId: this.form.value.userId,
      localIds: this.form.value.localIds
    };

    this.eventService.register(eventData, this.imgEvent).subscribe({
      next: (response) => {
        this.router.navigate(['']);
      },
      error: (error) => {
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

   loadLocals(): void {
    this.eventService.getLocal().subscribe(
      (data: Local[]) => {
        this.locals = data;
      },
      () => {
        console.error();
      }
    );
  }
}
