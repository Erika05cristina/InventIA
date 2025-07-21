import {
  Component,
  ChangeDetectionStrategy,
  computed,
  inject,
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
  selector: 'app-upload-file',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './upload-file.html',
  styleUrls: ['./upload-file.scss']
})
export class UploadFile {

  private explanationState = inject(ExplanationState);
  private state = inject(UploadState);
  private predictionService = inject(Prediction);

  private rawData = signal<string[][]>([]);
  readonly previewData = this.state.previewData;
  readonly predictionResult = signal<PredictionResponseSingle[] | null>(null);

  // 🔄 Señales separadas
  readonly isUploading = this.state.isUploading;
  readonly isPredicting = this.state.isPredicting;
  readonly isTraining = this.state.isTraining;
  readonly wasGroupPredictionDownloaded = this.state.wasGroupPredictionDownloaded;
  readonly trainingCompleted = this.state.trainingCompleted;

  public productId: number | null = null;
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
    const fechaFormateada = this.formatFecha(this.manualFechaValue);
    if (!fechaFormateada || this.manualProductIdValue === null || isNaN(this.manualProductIdValue)) {
      console.warn('Predicción individual: datos inválidos');
      return;
    }

    this.isPredicting.set(true);
    this.predictionService.predictSingle(this.manualProductIdValue, fechaFormateada).subscribe({
      next: (res) => {
        // ✅ Transformamos PredictionExplanation[] a PredictionResponseSingle[]
        const preds = res.map(({ prediccion }) => ({
          productId: prediccion.product_id,
          date: prediccion.fecha_prediccion,
          prediction: prediccion.prediccion
        }));
        this.predictionResult.set(preds);

        // ✅ Guardar explicación en ExplanationState (sólo si hay respuesta)
        if (res.length > 0) {
          const explicacion = res[0];

          this.explanationState.explicacionSimple.set(explicacion.explicacion_simple ?? '');
          this.explanationState.variablesImportantes.set(explicacion.explicacion_avanzada?.variables_importantes ?? []);
          this.explanationState.graficaBase64.set(explicacion.explicacion_avanzada?.grafica_explicabilidad_base64 ?? '');
        }
      },
      error: (err) => {
        console.error('Error al predecir individual:', err);
        this.predictionResult.set(null);
      },
      complete: () => this.isPredicting.set(false)
    });

  }

  getGroupPrediction(): void {
    const fechaFormateada = this.fecha; // <<<< ya viene en formato correcto YYYY-MM-DD
    if (!fechaFormateada) {
      console.warn('Predicción grupal: fecha inválida');
      return;
    }

    this.isPredicting.set(true);
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
      complete: () => {
        this.isPredicting.set(false);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    input.value = ''; // limpia input del campo file

    this.isUploading.set(true);
    this.isTraining.set(false);

    this.predictionService.uploadCsv(file).subscribe({
      next: (msg) => {
        console.log(' Backend procesó CSV:', msg);

        // Mostrar vista previa
        this.processFilePreview(file);

        // Inicia estado de entrenamiento (manual, sin detenerlo aún)
        this.isTraining.set(true);

        setTimeout(() => {
          this.isTraining.set(false);
          this.trainingCompleted.set(true); 
          console.log(' Entrenamiento finalizado automáticamente');
        }, 5 * 60 * 1000); // 5 minutos
      },
      error: (err) => {
        console.error(' Error al subir el archivo:', err);
        this.isUploading.set(false);
        this.isTraining.set(false);
      }
    });
  }


  private processFilePreview(file: File): void {
    const reader = new FileReader();

    reader.onload = () => {
      try {
        const text = reader.result as string;
        const lines = text.split('\n').filter((l) => l.trim().length > 0);
        const data = lines.map((line) => line.split(','));

        if (data.length > 1) {
          // Limpiar estado anterior
          this.productId = null;
          this.fecha = '';
          this.predictionResult.set(null);
          this.wasGroupPredictionDownloaded.set(false);

          this.state.setRawData(data); // ✅ este es el cambio importante

          const extractedProductId = parseInt((data[1][0] ?? '').toString(), 10);
          const extractedFecha = (data[1][1] ?? '').toString();
          const fechaFormateada = this.formatFecha(extractedFecha);

          if (!isNaN(extractedProductId)) {
            this.productId = extractedProductId;
          }

          if (fechaFormateada) {
            this.fecha = fechaFormateada;
          }

          this.isUploading.set(false);
        } else {
          console.warn('Formato inválido');
          this.state.setRawData([]);
          this.isUploading.set(false);
        }

      } catch (err) {
        console.error('Error al leer archivo:', err);
        this.state.setRawData([]);
        this.isUploading.set(false);
      }
    };

    reader.readAsText(file);
  }

  private formatFecha(fecha: string): string | null {
    const partes = fecha.split('-');
    if (partes.length !== 3) return null;

    const [dia, mes, anio] = partes.map(Number);
    if (isNaN(dia) || isNaN(mes) || isNaN(anio)) return null;

    // ⚠️ ¡Cuidado! new Date(año, mesIndex, día)
    const d = new Date(anio, mes - 1, dia);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }

  downloadJson(data: any, filename: string = 'predicciones.json'): void {
    const jsonStr = JSON.stringify(data, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();

    URL.revokeObjectURL(url);
  }

}