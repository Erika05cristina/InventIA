import {
  Component,
  ChangeDetectionStrategy,
  inject,
  signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UploadState } from '../../services/upload-state';
import { Prediction } from '../../services/prediction';

@Component({
  selector: 'app-upload-file',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './upload-file.html',
  styleUrls: ['./upload-file.scss']
})
export class UploadFile {

  private state = inject(UploadState);
  private predictionService = inject(Prediction);

  readonly previewData = this.state.previewData;
  readonly isUploading = this.state.isUploading;
  readonly isTraining = this.state.isTraining;
  readonly trainingCompleted = this.state.trainingCompleted;

  public fecha: string = '';
  public productId: number | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    input.value = '';

    this.isUploading.set(true);
    this.isTraining.set(false);

    this.predictionService.uploadCsv(file).subscribe({
      next: (msg) => {
        console.log(' Backend procesó CSV:', msg);

        // Mostrar vista previa
        this.processFilePreview(file);

        this.isTraining.set(true);

        setTimeout(() => {
          this.isTraining.set(false);
          this.trainingCompleted.set(true); 
          console.log(' Entrenamiento finalizado automáticamente');
        }, 1 * 60 * 1000); // 5 minutos
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
          this.productId = null;
          this.fecha = '';
          this.state.setRawData(data);
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
}