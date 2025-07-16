import {
  Component,
  ChangeDetectionStrategy,
  computed,
  inject,
  signal
} from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as XLSX from 'xlsx';
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
  // Señales
  private rawData = signal<any[][]>([]);
  readonly previewData = computed(() => this.rawData().slice(0, 5));
  readonly predictionResult = signal<PredictionResponseSingle[] | null>(null);

  // Inputs manuales
  public productId: number | null = null;
  public fecha: string = '';

  // Servicio HTTP
  private predictionService = inject(Prediction);

  getPrediction(): void {
    if (this.productId === null || this.fecha.trim() === '') {
      console.warn('Faltan datos para la predicción');
      return;
    }

    this.predictionService.predictSingle(this.productId, this.fecha).subscribe({
      next: (res) => this.predictionResult.set(res),
      error: (err) => {
        console.error('Error al predecir desde inputs:', err);
        this.predictionResult.set(null);
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    // 🔹 Primero, subimos al backend
    this.predictionService.uploadCsv(file).subscribe({
      next: (msg) => {
        console.log('✅ Backend procesó CSV:', msg);
        // Luego, mostramos vista previa en frontend
        this.processFilePreview(file);
      },
      error: (err) => {
        console.error(' Error al subir el archivo:', err);
      },
    });
  }

  private processFilePreview(file: File): void {
  const reader = new FileReader();

  reader.onload = () => {
    let workbook: XLSX.WorkBook;

    try {
      if (file.name.endsWith('.csv')) {
        workbook = XLSX.read(reader.result as string, { type: 'string' });
      } else {
        const data = new Uint8Array(reader.result as ArrayBuffer);
        workbook = XLSX.read(data, { type: 'array' });
      }

      const sheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(sheet, { header: 1 }) as unknown[];

      if (Array.isArray(jsonData) && jsonData.length > 1 && Array.isArray(jsonData[1])) {
        this.rawData.set(jsonData as any[][]);

        // Prueba automática con el primer producto
        const productId = parseInt((jsonData[1][0] ?? '').toString(), 10);
        const fecha = (jsonData[1][1] ?? '').toString();

        if (!isNaN(productId) && fecha) {
          this.predictionService.predictSingle(productId, fecha).subscribe({
            next: (response) => this.predictionResult.set(response),
            error: (err) => {
              console.error('Error en la predicción:', err);
              this.predictionResult.set(null);
            },
          });
        }
      } else {
        console.warn('Formato inválido');
        this.rawData.set([]);
      }
    } catch (err) {
      console.error('Error al leer el archivo:', err);
      this.rawData.set([]);
    }
  };

  if (file.name.endsWith('.csv')) {
    reader.readAsText(file);
  } else {
    reader.readAsArrayBuffer(file);
  }
}


}
