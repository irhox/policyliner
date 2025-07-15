import { Component } from '@angular/core';
import {MatButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-side-bar',
  imports: [
    MatButton,
    RouterLink
  ],
  templateUrl: './side-bar.html',
  styleUrl: './side-bar.scss'
})
export class SideBar {

  protected readonly console = console;
}
