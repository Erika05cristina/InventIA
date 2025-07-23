import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from './components/sidebar/sidebar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,Sidebar], 
  templateUrl: './app.html',
  styleUrls: ['./app.scss'] 
})
export class App {
  protected title = 'invent-ia-frontend';
}
