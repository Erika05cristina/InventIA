import { Component, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Auth } from '../../services/auth';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private authService = inject(Auth);
  private router = inject(Router);

  readonly isLogin = signal(true);

  // Registro
  signupName = '';
  signupEmail = '';
  signupPassword = '';
  acceptTerms = false;

  // Login
  loginEmail = '';
  loginPassword = '';

  // Estado
  readonly error = signal('');
  readonly success = signal('');

  toggleLogin(): void {
    this.isLogin.set(true);
    this.clearMessages();
  }

  toggleSignup(): void {
    this.isLogin.set(false);
    this.clearMessages();
  }

  onLogin(): void {
    if (!this.loginEmail || !this.loginPassword) {
      this.error.set('Por favor completa los campos.');
      return;
    }

    this.authService.login(this.loginEmail, this.loginPassword).subscribe({
      next: (res) => {
        this.authService.guardarUsuario(res);
        this.success.set('Inicio de sesión exitoso.');
        this.error.set('');
        this.router.navigate(['/home']);
      },
      error: () => {
        this.error.set('Credenciales incorrectas.');
        this.success.set('');
      }
    });
  }

  onSignup(): void {
    if (!this.signupName || !this.signupEmail || !this.signupPassword || !this.acceptTerms) {
      this.error.set('Completa todos los campos y acepta los términos.');
      return;
    }

    this.authService.register(this.signupName, this.signupEmail, this.signupPassword).subscribe({
      next: (res) => {
        this.success.set('Registro exitoso. Ahora puedes iniciar sesión.');
        this.error.set('');
        this.isLogin.set(true);
      },
      error: (err) => {
        this.error.set('Error al registrar usuario: ' + err.error);
        this.success.set('');
      }
    });
  }

  private clearMessages() {
    this.error.set('');
    this.success.set('');
  }
}
