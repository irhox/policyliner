import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {
  MatSidenav,
  MatSidenavContainer,
  MatSidenavContent
} from '@angular/material/sidenav';
import {MatFabButton} from '@angular/material/button';
import {SideBar} from './components/side-bar/side-bar';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-root',
  imports: [MatSidenavContainer, MatSidenav, MatSidenavContent, RouterOutlet, SideBar, MatFabButton, MatIcon],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected title = 'policyliner-demo-ui';
  protected opened: boolean = false;
}
