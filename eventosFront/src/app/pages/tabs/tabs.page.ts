import { Component } from '@angular/core';
import {TokenService} from "../../service/auth.token.service";
import {UserDetailService} from "../../service/user.service";
import {UserModel} from "../../models/auth.data.transfer.object";

@Component({
  selector: 'app-tabs',
  templateUrl: 'tabs.page.html',
  styleUrls: ['tabs.page.scss']
})
export class TabsPage {

  userId!: number | undefined;
  user!: UserModel | null;
  constructor(private authService: TokenService) {

    this.user = this.authService.getUserFromToken();
    this.userId = this.user?.userId;
  }

}
