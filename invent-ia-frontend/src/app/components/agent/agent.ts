import { Component, signal, inject } from '@angular/core';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Agent as AgentService } from '../../services/agent'; // ajusta la ruta si es necesario

@Component({
  selector: 'app-agent',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './agent.html',
  styleUrl: './agent.scss'
})
export class Agent {
  private readonly agentService = inject(AgentService);

  readonly inputControl = new FormControl<string>('');
  readonly messages = signal<{ role: 'user' | 'ai'; content: string }[]>([
    { role: 'ai', content: '¡Hola! Soy tu agente IA. ¿En qué puedo ayudarte hoy?' }
  ]);

  sendMessage(): void {
    const input = this.inputControl.value?.trim();
    if (!input) return;

    // Mostrar el mensaje del usuario
    this.messages.update(messages => [...messages, { role: 'user', content: input }]);

    // Mostrar mensaje de carga
    this.messages.update(messages => [...messages, { role: 'ai', content: 'Procesando...' }]);

    // Llamar al agente
    this.agentService.enviarPregunta(input).subscribe({
      next: (res) => {
        this.replaceLastMessage({ role: 'ai', content: res.respuesta });
      },
      error: () => {
        this.replaceLastMessage({ role: 'ai', content: 'Error al contactar al agente. Intenta más tarde.' });
      }
    });

    this.inputControl.reset();
  }

  private replaceLastMessage(newMessage: { role: 'user' | 'ai'; content: string }) {
    this.messages.update(messages => {
      const updated = [...messages];
      updated[updated.length - 1] = newMessage;
      return updated;
    });
  }
}
