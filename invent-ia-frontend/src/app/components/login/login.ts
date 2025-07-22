import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  readonly isLogin = signal(false); // false = signup, true = login

  signupName = '';
  signupEmail = '';
  signupPassword = '';
  acceptTerms = false;

  loginEmail = '';
  loginPassword = '';

  toggleLogin(): void {
    this.isLogin.set(true);
  }

  toggleSignup(): void {
    this.isLogin.set(false);
  }

  onLogin(): void {
    console.log('Logging in:', this.loginEmail, this.loginPassword);
  }

  onSignup(): void {
    console.log('Signing up:', this.signupName, this.signupEmail);
  }
}
