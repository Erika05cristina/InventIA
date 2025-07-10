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
  private readonly apiUrl = 'http://localhost:8080/predict';

  // Predicción para un solo producto
  predictSingle(productId: number, fecha: string): Observable<PredictionResponseSingle[]> {
    return this.http.get<PredictionResponseSingle[]>(`${this.apiUrl}/single`, {
      params: { product_id: productId, fecha },
    });
  }

  // Predicción para todos los productos en una fecha
  predictGroup(fecha: string): Observable<PredictionResponseGroup[]> {
    return this.http.get<PredictionResponseGroup[]>(`${this.apiUrl}/group`, {
      params: { fecha },
    });
  }
}
