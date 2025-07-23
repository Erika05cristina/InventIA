import { Injectable, computed, signal } from '@angular/core';
import { PredictionResponseSingle } from '../services/prediction';

@Injectable({
  providedIn: 'root'
})
export class UploadState {
  private rawData = signal<string[][]>([]);
  readonly previewData = computed(() => this.rawData().slice(0, 5));

  readonly predictionResult = signal<PredictionResponseSingle[] | null>(null);
  readonly isUploading = signal(false);
  readonly isPredicting = signal(false);
  readonly isTraining = signal(false);
  readonly wasGroupPredictionDownloaded = signal(false);
  readonly trainingCompleted = signal(false);
  readonly fechaUltimaPrediccionGrupal = signal<string>('');

  readonly manualProductId = signal<number | null>(null);
  readonly manualFecha = signal('');
  readonly groupFecha = signal('');

  setRawData(data: string[][]) {
    this.rawData.set(data);
  }

  clearState() {
    this.rawData.set([]);
    this.predictionResult.set(null);
    this.isUploading.set(false);
    this.isPredicting.set(false);
    this.isTraining.set(false);
    this.wasGroupPredictionDownloaded.set(false);
    this.trainingCompleted.set(false);
    this.manualProductId.set(null);
    this.manualFecha.set('');
    this.groupFecha.set('');
  }
}
