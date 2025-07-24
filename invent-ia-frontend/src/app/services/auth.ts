import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { environment } from '../../environmets/environment';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private readonly http = inject(HttpClient);
  //private readonly baseUrl = 'http://localhost:8080/user';
  private readonly baseUrl = environment.backendBaseUrl;

  login(email: string, password: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/login`, { email, password }, {
      responseType: 'text',
    });
  }

  register(name: string, email: string, password: string): Observable<string> {
    return this.http.post(`${this.baseUrl}/register`, {
      name,
      email,
      password
    }, { responseType: 'text' });
  }

  guardarUsuario(user: string): void {
    localStorage.setItem('usuario', user);
  }

  logout(): void {
    localStorage.removeItem('usuario');
  }

  isAuthenticated(): boolean {
    return localStorage.getItem('usuario') !== null;
  }

}
