import { AfterViewInit, Component, OnInit } from '@angular/core';
import {CommentsDTO, Event, Local, UserComments} from "../../models/events.model";
import {ActivatedRoute, Router} from "@angular/router";
import { EventService } from "../../service/event.service";
import * as L from 'leaflet';
import {BookmarkDTO, TokensResponse, UserModel} from "../../models/auth.data.transfer.object";
import { TokenService } from "../../service/auth.token.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ModalController, ToastController} from "@ionic/angular";
import {HttpErrorResponse} from "@angular/common/http";
import {ReminderModalComponent} from "../reminder-modal/reminder-modal.component";
import { MapsService } from 'src/app/service/maps.service';

@Component({
  selector: 'app-events',
  templateUrl: './events.page.html',
  styleUrls: ['./events.page.scss'],
})
export class EventsPage implements OnInit, AfterViewInit {

  form!: FormGroup;
  public locals: Local[] = [] ;
  event!: Event;
  local!: Local;
  user!: UserModel | null;
  isBookmarked: boolean = false;
  comments!: CommentsDTO;
  commentsList!: CommentsDTO[];
  isCreatedByUser: boolean = false;
  public userId!: number | undefined;
  eventId!: number;
  public isModalOpen: boolean = false;
  public imgEvent!: File;
  public imgEventError = false;

  constructor(
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private router: Router,
    private toastController: ToastController,
    private eventService: EventService,
    private tokenService: TokenService,
    private modalController: ModalController,
  ) {

   }

  ngOnInit() {
    this.userId = this.tokenService.getUserFromToken()?.userId;
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.tokenService.sessionIsValid()) {
      this.user = this.tokenService.getUserFromToken();
      if (this.user) {
        this.getFullEvent();
        this.getLocal();
        this.initializeForm();
      } else {
        console.error('Usuário não encontrado no token');
      }
        this.eventService.registerInteraction(this.userId!, this.eventId, 'VIEW')
        .subscribe({ error: e => console.error(e) });
    } else {
      this.tokenService.handleSessionExpired();
    }
  }
  initializeForm(){
    this.event = {} as Event;
    this.form = this.formBuilder.group({
      eventName: ['', Validators.required],
      typeEvent: ['', Validators.required],
      description: ['', Validators.required],
      dateInitial: ['', Validators.required],
      dateFinal: ['', Validators.required],
      centerName: ['', Validators.required],
      contact: ['', Validators.required],
      userId: [this.userId],
      localIds: [[]]
    });
  }
  getFullEvent() {
    const eventId = this.route.snapshot.paramMap.get('id');
    if (eventId) {
      this.eventService.getEventById(eventId).subscribe(
        (data: Event) => {
          this.event = data;
          this.checkBookmarked();
          this.checkIfUserCreatedEvent();

        },
        (error) => {
          console.error('Erro ao carregar evento:', error);
        }
      );
    }
  }



  ngAfterViewInit() {
    this.breakTitleIntoChunks();
  }

  private breakTitleIntoChunks() {
    const titleElement = document.getElementById('eventTitle');
    if (titleElement && this.event?.eventName) {
      const words = this.event.eventName.split(' ');
      let formattedTitle = '';
      for (let i = 0; i < words.length; i++) {
        formattedTitle += words[i] + ' ';
        if ((i + 1) % 3 === 0) {
          formattedTitle += '<br>';
        }
      }
      titleElement.innerHTML = formattedTitle.trim();
    }
  }
  getLocal() {
    const eventId = this.route.snapshot.paramMap.get('id');
    if (eventId) {
      this.eventService.getEventAndLocal(eventId).subscribe(
        (res: Local) => {
          this.local = res;
          console.log(res)
          const mapsService = new MapsService(this.local);
          mapsService.initMap();
        },
        (error) => {
          console.error('Erro ao carregar local:', error);
        }
      );
    }
  }
  loadLocals(): void {
    this.eventService.getLocal().subscribe(
      (data: Local[]) => {
        this.locals = data;
        console.log("local",this.locals)
      },
      () => {
        console.error();
      }
    );
  }
  saveBookmark() {
    if (this.user && this.event) {
      const bookmark: BookmarkDTO = {
        userId: this.user.userId,
        eventId: this.event.eventsId
      };
      this.eventService.saveEventBookmarked(bookmark).subscribe(
        (res) => {
          console.log('Evento salvo como favorito:', res);
          this.isBookmarked = true;
          this.eventService.registerInteraction(this.userId!, this.eventId, 'BOOKMARK')
          .subscribe({ error: e => console.error(e) });
        },
        (error) => {
          console.error('Erro ao salvar evento como favorito:', error);
          this.isBookmarked = true;
        }
      );
    } else {
      console.error('Usuário ou evento não definidos');
    }
  }

  deleteBookmark() {
    if (this.user && this.event) {
      this.eventService.wipeBookmark(this.user.userId, this.event.eventsId).subscribe(
        (res) => {
          console.log('Evento removido dos favoritos:', res);
          this.isBookmarked = false;
        },
        (error) => {
          console.error('Erro ao remover evento dos favoritos:', error);
        }
      );
    } else {
      console.error('Usuário ou evento não definidos');
    }
  }


  checkBookmarked() {
    if (this.user && this.event) {
      this.eventService.getUserHasBookmarked(this.user.userId, this.event.eventsId).subscribe(
        (res) => {
          console.log(res);
          this.isBookmarked = res;
        },
        (error) => {
          console.error(error);
          this.isBookmarked = false;
        }
      );
    }
  }

  checkIfUserCreatedEvent() {
    if (this.user && this.event) {
      this.eventService.getUserHasCreated(this.user.userId, this.event.eventsId).subscribe(
        (res) => {
          this.isCreatedByUser = res;
        },
        (error) => {
          console.error(error);
          this.isCreatedByUser = false;
        }
      );
    }
  }
  deleteEvent() {
    if (this.isCreatedByUser) {
      this.eventService.wipeEvent(this.event.eventsId).subscribe(
        (res) => {
          console.log('Evento removido:', res);
          this.router.navigate(['tabs/home'])
        },
        (error) => {
          console.error('Erro ao remover evento', error);
        }
      );
    } else {
      console.error('Usuário ou evento não definidos');
    }
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
  editEvent() {
    const eventId = this.event.eventsId;
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
    console.log(eventId)
    this.eventService.editEvent(eventId, eventData, this.imgEvent).subscribe({
      next: (response: TokensResponse) => {
        this.closeModal();
        this.showSuccessToast('Informações atualizadas com sucesso!');
      },
      error: (error: HttpErrorResponse) => {
        console.error('Erro ao atualizar:', error);
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
  openEditModal() {
    this.loadLocals();
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  async openReminderModal() {
    this.eventService.registerInteraction(this.userId!, this.eventId, 'REMINDER')
    .subscribe({ error: e => console.error(e) });

    const modal = await this.modalController.create({
      component: ReminderModalComponent,
      componentProps: {
        userId: this.userId,
        eventId: this.eventId,
        dateFinal: this.event.dateFinal
      }
    });
    await modal.present();
  }
}
