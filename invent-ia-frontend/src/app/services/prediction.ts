import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PredictionResponseSingle {
  productId: number;
  date: string;
  prediction: number;
}

export interface PredictionResponseGroup {
  date: string;
  predictions: { productId: number; prediction: number }[];
}

@Injectable({
  providedIn: 'root',
})
export class Prediction {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080'; 
  private readonly predictionUrl = `${this.baseUrl}/predict`;

  uploadCsv(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${this.baseUrl}/data/upload`, formData, {
      responseType: 'text',
    });
  }

  predictSingle(productId: number, fecha: string): Observable<PredictionResponseSingle[]> {
    return this.http.get<PredictionResponseSingle[]>(`${this.predictionUrl}/single`, {
      params: { product_id: productId, fecha },
    });
  }

  predictGroup(fecha: string): Observable<PredictionResponseGroup[]> {
    return this.http.get<PredictionResponseGroup[]>(`${this.predictionUrl}/group`, {
      params: { fecha },
    });
  }
}
