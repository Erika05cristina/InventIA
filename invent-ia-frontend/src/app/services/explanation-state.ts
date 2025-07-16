import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ExplanationState {

readonly explicacionSimple = signal('');
readonly variablesImportantes = signal<[string, number][]>([]);
readonly graficaBase64 = signal('');
  
}
