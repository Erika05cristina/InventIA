import { Component, signal } from '@angular/core';
import { ReactiveFormsModule, FormControl } from '@angular/forms';

@Component({
  selector: 'app-agent',
  imports: [ReactiveFormsModule],
  templateUrl: './agent.html',
  styleUrl: './agent.scss'
})
export class Agent {

  readonly inputControl = new FormControl<string>('');
  readonly messages = signal<{ role: 'user' | 'ai'; content: string }[]>([
    { role: 'ai', content: '¡Hola! Soy tu agente IA. ¿En qué puedo ayudarte hoy?' }
  ]);

  sendMessage(): void {
    const input = this.inputControl.value?.trim();
    if (!input) return;

    this.messages.update(messages => [...messages, { role: 'user', content: input }]);
    this.messages.update(messages => [...messages, { role: 'ai', content: 'Gracias por tu mensaje. Estoy procesando tu solicitud...' }]);

    this.inputControl.reset();
  }

}
