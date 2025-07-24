import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environmets/environment';

export interface DashboardResponse {
  totalUnidades: number;
  promedioPorProducto: number;
  productoMayorDemandaId: number;
  productoMayorDemandaStock: number;
  productoMenorDemandaId: number;
  productoMenorDemandaStock: number;
  cantidadAltaDemanda: number;
  cantidadMediaDemanda: number;
  cantidadBajaDemanda: number;
  fecha: string;
}

@Injectable({
  providedIn: 'root'
})
export class Dashboard {

  private readonly http = inject(HttpClient);
  //private readonly baseUrl = 'http://localhost:8080/dashboard';
  private readonly baseUrl = environment.backendBaseUrl;

  getDashboardStatistics(fecha: string, tipo: string = 'grupo'): Observable<DashboardResponse> {
    const url = `${this.baseUrl}/statistics?fecha=${fecha}&tipo=${tipo}`;
    return this.http.get<DashboardResponse>(url);
  }

}
