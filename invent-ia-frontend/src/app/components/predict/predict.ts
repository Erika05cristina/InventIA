import {
  Component,
  ChangeDetectionStrategy,
  inject,
  computed,
  signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Prediction,
  PredictionResponseSingle
} from '../../services/prediction';
import { UploadState } from '../../services/upload-state';
import { ExplanationState } from '../../services/explanation-state';

@Component({
  selector: 'app-predict',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './predict.html',
  styleUrl: './predict.scss'
})
export class Predict {

  private predictionService = inject(Prediction);
  private state = inject(UploadState);
  private explanationState = inject(ExplanationState);

  readonly predictionResult = signal<PredictionResponseSingle[] | null>(null);

  readonly isPredictingIndividual = signal(false);
  readonly isPredictingGroup = signal(false);
  readonly wasGroupPredictionDownloaded = this.state.wasGroupPredictionDownloaded;

  public fecha: string = '';

  get manualProductIdValue(): number | null {
    return this.state.manualProductId();
  }
  set manualProductIdValue(val: number | null) {
    this.state.manualProductId.set(val);
  }

  get manualFechaValue(): string {
    return this.state.manualFecha();
  }
  set manualFechaValue(val: string) {
    this.state.manualFecha.set(val);
  }

  getPrediction(): void {
    const fechaFormateada = this.manualFechaValue;
    if (!fechaFormateada || this.manualProductIdValue === null || isNaN(this.manualProductIdValue)) {
      console.warn('Predicción individual: datos inválidos');
      return;
    }

    this.isPredictingIndividual.set(true);
    this.predictionService.predictSingle(this.manualProductIdValue, fechaFormateada).subscribe({
      next: (res) => {
        const preds = res.map(({ prediccion }) => ({
          productId: prediccion.product_id,
          date: prediccion.fecha_prediccion,
          prediction: prediccion.prediccion
        }));
        this.predictionResult.set(preds);

        if (res.length > 0) {
          const explicacion = res[0];
          this.explanationState.explicacionSimple.set(explicacion.explicacion_simple ?? '');
          this.explanationState.variablesImportantes.set(explicacion.explicacion_avanzada?.variables_importantes ?? []);
          this.explanationState.graficaBase64.set(explicacion.explicacion_avanzada?.grafica_explicabilidad_base64 ?? '');
          this.explanationState.explicacionCompleta.set(explicacion.explicacionOpenAi ?? '');
        }
      },
      error: (err) => {
        console.error('Error al predecir individual:', err);
        this.predictionResult.set(null);
      },
      complete: () => this.isPredictingIndividual.set(false)
    });
  }


  getGroupPrediction(): void {
    const fechaFormateada = this.fecha;
    if (!fechaFormateada) {
      console.warn('Predicción grupal: fecha inválida');
      return;
    }

    this.isPredictingGroup.set(true);
    this.wasGroupPredictionDownloaded.set(false);

    this.predictionService.predictGroup(fechaFormateada).subscribe({
      next: (res) => {
        const blob = new Blob([JSON.stringify(res, null, 2)], {
          type: 'application/json',
        });

        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `prediccion_grupo_${fechaFormateada}.json`;
        document.body.appendChild(link);
        link.click();
        link.remove();

        this.wasGroupPredictionDownloaded.set(true);
      },
      error: (err) => {
        console.error('Error al predecir por grupo:', err);
      },
      complete: () => this.isPredictingGroup.set(false)
    });
  }

}
