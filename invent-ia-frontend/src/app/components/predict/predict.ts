import {
  Component,
  ChangeDetectionStrategy,
  inject,
  computed,
  signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Prediction,
  PredictionResponseSingle,
  PredictionResponseGroup,
  GroupPrediction
} from '../../services/prediction';
import { UploadState } from '../../services/upload-state';
import { ExplanationState } from '../../services/explanation-state';

@Component({
  selector: 'app-predict',
  standalone: true,
  imports: [CommonModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './predict.html',
  styleUrl: './predict.scss'
})
export class Predict {

  private predictionService = inject(Prediction);
  private state = inject(UploadState);
  private explanationState = inject(ExplanationState);

  readonly predictionResult = signal<PredictionResponseSingle[] | null>(null);
  readonly isPredictingIndividual = signal(false);
  readonly isPredictingGroup = signal(false);
  readonly wasGroupPredictionDownloaded = this.state.wasGroupPredictionDownloaded;

  public fecha: string = '';

  // Datos de predicción grupal
  readonly groupPrediction = signal<PredictionResponseGroup | null>(null);

  readonly _pageSize = signal(10);

  // Filtros
  private _selectedCategoria = signal<string | null>(null);
  private _selectedProducto = signal<string | null>(null);

  get selectedCategoriaValue(): string | null {
    return this._selectedCategoria();
  }
  set selectedCategoriaValue(val: string | null) {
    this._selectedCategoria.set(val === '' ? null : val);
    this.currentPage.set(1);
  }

  get selectedProductoValue(): string | null {
    return this._selectedProducto();
  }
  set selectedProductoValue(val: string | null) {
    this._selectedProducto.set(val);
    this.currentPage.set(1); // reiniciar a página 1 si cambia filtro
  }

  get pageSizeValue(): number {
    return this._pageSize();
  }
  set pageSizeValue(val: number | string) {
    const parsed = typeof val === 'string' ? parseInt(val, 10) : val;
    this._pageSize.set(parsed);
    this.currentPage.set(1);
  }

  readonly categorias = computed(() => {
    const group = this.groupPrediction();
    if (!group) return [];
    return [...new Set(group.predicciones.map(p => p.categoria))];
  });

  readonly productosFiltrados = computed(() => {
    const group = this.groupPrediction();
    const categoria = this.selectedCategoriaValue;
    if (!group || !categoria) return [];
    return [...new Set(
      group.predicciones
        .filter(p => p.categoria === categoria)
        .map(p => p.name)
    )];
  });

  readonly prediccionesFiltradas = computed(() => {
    const pred = this.groupPrediction()?.predicciones ?? [];
    const cat = this.selectedCategoriaValue;
    const prod = this.selectedProductoValue;

    return pred.filter(p =>
      (!cat || p.categoria === cat) &&
      (!prod || p.name === prod)
    );
  });

  // Paginación
  readonly currentPage = signal(1);

  readonly totalPages = computed(() => {
    const total = this.prediccionesFiltradas().length;
    const size = this._pageSize(); // ← usa el correcto
    return Math.max(1, Math.ceil(total / size));
  });

  readonly paginatedPredictions = computed(() => {
    const all = this.prediccionesFiltradas();
    const page = this.currentPage();
    const size = this._pageSize(); // ← usa el correcto
    const start = (page - 1) * size;
    return all.slice(start, start + size);
  });

  nextPage(): void {
    if (this.currentPage() < this.totalPages()) {
      this.currentPage.set(this.currentPage() + 1);
    }
  }

  prevPage(): void {
    if (this.currentPage() > 1) {
      this.currentPage.set(this.currentPage() - 1);
    }
  }

  // Inputs predicción individual
  get manualProductIdValue(): number | null {
    return this.state.manualProductId();
  }
  set manualProductIdValue(val: number | null) {
    this.state.manualProductId.set(val);
  }

  get manualFechaValue(): string {
    return this.state.manualFecha();
  }
  set manualFechaValue(val: string) {
    this.state.manualFecha.set(val);
  }

  getPrediction(): void {
    const fechaFormateada = this.manualFechaValue;
    if (!fechaFormateada || this.manualProductIdValue === null || isNaN(this.manualProductIdValue)) {
      console.warn('Predicción individual: datos inválidos');
      return;
    }

    this.isPredictingIndividual.set(true);
    this.predictionService.predictSingle(this.manualProductIdValue, fechaFormateada).subscribe({
      next: (res) => {
        const preds = res.map(({ prediccion }) => ({
          productId: prediccion.product_id,
          date: prediccion.fecha_prediccion,
          prediction: prediccion.prediccion
        }));
        this.predictionResult.set(preds);

        if (res.length > 0) {
          const explicacion = res[0];
          this.explanationState.explicacionSimple.set(explicacion.explicacion_simple ?? '');
          this.explanationState.variablesImportantes.set(explicacion.explicacion_avanzada?.variables_importantes ?? []);
          this.explanationState.graficaBase64.set(explicacion.explicacion_avanzada?.grafica_explicabilidad_base64 ?? '');
          this.explanationState.explicacionCompleta.set(explicacion.explicacionOpenAi ?? '');
        }
      },
      error: (err) => {
        console.error('Error al predecir individual:', err);
        this.predictionResult.set(null);
      },
      complete: () => this.isPredictingIndividual.set(false)
    });
  }

  getGroupPrediction(): void {
    const fechaFormateada = this.fecha;
    if (!fechaFormateada) {
      console.warn('Predicción grupal: fecha inválida');
      return;
    }

    this.state.fechaUltimaPrediccionGrupal.set(fechaFormateada);
    this.isPredictingGroup.set(true);

    this.predictionService.predictGroup(fechaFormateada).subscribe({
      next: (res) => {
        this.groupPrediction.set(res[0]);
        this.currentPage.set(1); // Reiniciar paginación
      },
      error: (err) => {
        console.error('Error al predecir por grupo:', err);
      },
      complete: () => this.isPredictingGroup.set(false)
    });
  }
}