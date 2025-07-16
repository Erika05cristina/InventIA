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

@Component({
  selector: 'app-upload-file',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './upload-file.html',
  styleUrls: ['./upload-file.scss']
})
export class UploadFile {
  private rawData = signal<string[][]>([]);
  readonly previewData = computed(() => this.rawData().slice(0, 5));
  readonly predictionResult = signal<PredictionResponseSingle[] | null>(null);

  // 🔄 Señales separadas
  readonly isUploading = signal(false);
  readonly isPredicting = signal(false);
  readonly isTraining = signal(false);
  readonly wasGroupPredictionDownloaded = signal(false);
  readonly trainingCompleted = signal(false);

  public productId: number | null = null;
  public fecha: string = '';

  public manualProductId: number | null = null;
  public manualFecha: string = '';

  private selectedFile: File | null = null;

  private predictionService = inject(Prediction);

  getPrediction(): void {
    const fechaFormateada = this.formatFecha(this.manualFecha);
    if (!fechaFormateada || this.manualProductId === null || isNaN(this.manualProductId)) {
      console.warn('Predicción individual: datos inválidos');
      return;
    }

    this.isPredicting.set(true);
    this.predictionService.predictSingle(this.manualProductId, fechaFormateada).subscribe({
      next: (res) => this.predictionResult.set(res),
      error: (err) => {
        console.error('Error al predecir individual:', err);
        this.predictionResult.set(null);
      },
      complete: () => this.isPredicting.set(false)
    });
  }

  getGroupPrediction(): void {
    const fechaFormateada = this.formatFecha(this.fecha);
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

    this.selectedFile = file;
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
          // 🔄 Limpiar campos antes de mostrar nueva vista previa
          this.productId = null;
          this.fecha = '';
          this.predictionResult.set(null);
          this.wasGroupPredictionDownloaded.set(false);

          this.rawData.set(data);

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
          this.rawData.set([]);
          this.isUploading.set(false);
        }

      } catch (err) {
        console.error('Error al leer archivo:', err);
        this.rawData.set([]);
        this.isUploading.set(false);
      }
    };

    reader.readAsText(file);
  }

  private formatFecha(fecha: string): string | null {
    const d = new Date(fecha);
    if (isNaN(d.getTime())) return null;
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