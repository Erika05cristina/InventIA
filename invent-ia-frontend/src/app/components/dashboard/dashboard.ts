import { Component, OnInit, inject, effect } from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { Dashboard as DashboardService, DashboardResponse } from '../../services/dashboard';
import { UploadState } from '../../services/upload-state';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ChartModule, CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard implements OnInit {

  private dashboardService = inject(DashboardService);
  private state = inject(UploadState);

  stats: DashboardResponse | null = null;
  chartData: any;

  ngOnInit(): void {
    effect(() => {
      const fecha = this.state.fechaUltimaPrediccionGrupal();
      if (!fecha) return;

      this.dashboardService.getDashboardStatistics(fecha, 'grupo').subscribe((res) => {
        this.stats = res;
        this.chartData = {
          labels: ['Alta', 'Media', 'Baja'],
          datasets: [
            {
              data: [res.cantidadAltaDemanda, res.cantidadMediaDemanda, res.cantidadBajaDemanda],
              backgroundColor: ['#FF6384', '#FFCE56', '#36A2EB'],
            },
          ],
        };
      });
    });
  }
}
