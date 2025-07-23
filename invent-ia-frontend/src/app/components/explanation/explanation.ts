import { Component, inject,computed } from '@angular/core';
import { ExplanationState } from '../../services/explanation-state';

@Component({
  selector: 'app-explanation',
  imports: [],
  templateUrl: './explanation.html',
  styleUrl: './explanation.scss'
})
export class Explanation {

  private state = inject(ExplanationState);

    readonly explicacionSimple = computed(() => this.state.explicacionSimple());
    readonly variables = computed(() => this.state.variablesImportantes());
    readonly grafica = computed(() => this.state.graficaBase64());

}
