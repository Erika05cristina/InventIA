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

  public productId: number | null = null;
  public fecha: string = '';

  private selectedFile: File | null = null;

  private predictionService = inject(Prediction);

  getPrediction(): void {
    const fechaFormateada = this.formatFecha(this.fecha);
    if (!fechaFormateada) {
      console.warn('Fecha inválida o vacía');
      return;
    }

    if (this.productId === null || isNaN(this.productId)) {
      console.warn('ID de producto inválido o vacío');
      return;
    }

    this.isPredicting.set(true);

    this.predictionService.predictSingle(this.productId, fechaFormateada).subscribe({
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
      console.warn('Fecha inválida o vacía');
      return;
    }

    this.isPredicting.set(true);

    this.predictionService.predictGroup(fechaFormateada).subscribe({
      next: (res) => this.predictionResult.set(res as any),
      error: (err) => {
        console.error('Error al predecir por grupo:', err);
        this.predictionResult.set(null);
      },
      complete: () => this.isPredicting.set(false)
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.selectedFile = file;
    input.value = ''; // limpia input del campo file

    this.isUploading.set(true);
    this.isTraining.set(false); // por si ya había uno en curso

    this.predictionService.uploadCsv(file).subscribe({
      next: (msg) => {
        console.log('✅ Backend procesó CSV:', msg);

        // Mostrar vista previa
        this.processFilePreview(file);

        // Inicia estado de entrenamiento (manual, sin detenerlo aún)
        this.isTraining.set(true);
      },
      error: (err) => {
        console.error('❌ Error al subir el archivo:', err);
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

          // ✅ solo actualiza vista previa y campos
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
}