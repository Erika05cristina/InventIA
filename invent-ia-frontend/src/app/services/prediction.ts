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

export interface PredictionExplanation {
  status: string;
  explicacion_simple: string;
  explicacionOpenAi?: string; // ✅ Agregado aquí
  prediccion: {
    product_id: number;
    fecha_prediccion: string;
    prediccion: number;
  };
  explicacion_avanzada: {
    producto_id: number;
    variables_importantes: [string, number][];
    grafica_explicabilidad_base64: string;
  };
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

  // Predicción para un solo producto
  predictSingle(productId: number, fecha: string): Observable<PredictionExplanation[]> {
    return this.http.get<PredictionExplanation[]>(`${this.predictionUrl}/single`, {
      params: { product_id: productId, fecha_prediccion: fecha },
    });
  }

// Predicción para todos los productos
  predictGroup(fecha: string): Observable<PredictionResponseGroup[]> {
    return this.http.get<PredictionResponseGroup[]>(`${this.predictionUrl}/group`, {
      params: { fecha_prediccion: fecha },
    });
  }

}
