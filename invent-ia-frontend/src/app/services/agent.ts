import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AgentResponse {
  respuesta: string;
}

@Injectable({
  providedIn: 'root'
})
export class Agent {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8000/agent/chat';

  enviarPregunta(pregunta: string): Observable<AgentResponse> {
    const userId = localStorage.getItem('usuario');

    const data = {
      user_id: userId || 'anonimo',
      pregunta: pregunta
    };

    return this.http.post<AgentResponse>(this.baseUrl, data);
  }
  
}
