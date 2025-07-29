  import {
    Component,
    ChangeDetectionStrategy,
    inject,
    computed,
    signal,
    OnInit
  } from '@angular/core';
  import { CommonModule } from '@angular/common';
  import { FormsModule } from '@angular/forms';
  import { HttpClient } from '@angular/common/http';
  import {
    Prediction,
    PredictionResponseSingle,
    PredictionResponseGroup
  } from '../../services/prediction';
  import { UploadState } from '../../services/upload-state';
  import { ExplanationState } from '../../services/explanation-state';

  interface ProductoCSV {
    id: number;
    nombre: string;
    precio: number;
    categoria: string;
  }

  @Component({
    selector: 'app-predict',
    standalone: true,
    imports: [CommonModule, FormsModule],
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './predict.html',
    styleUrl: './predict.scss'
  })
  export class Predict implements OnInit {
    private predictionService = inject(Prediction);
    private state = inject(UploadState);
    private explanationState = inject(ExplanationState);
    private http = inject(HttpClient);

    readonly predictionResult = signal<PredictionResponseSingle[] | null>(null);
    readonly isPredictingIndividual = signal(false);
    readonly isPredictingGroup = signal(false);
    readonly wasGroupPredictionDownloaded = this.state.wasGroupPredictionDownloaded;

    public fecha: string = '';

    // Datos CSV cargados
    readonly productosDisponibles = signal<ProductoCSV[]>([]);
    readonly selectedCategoriaCSV = signal<string | null>(null);
    readonly selectedProductoCSV = signal<ProductoCSV | null>(null);

    readonly categoriasCSV = computed(() => {
      const productos = this.productosDisponibles();
      return [...new Set(productos.map(p => p.categoria))];
    });

    readonly productosFiltradosCSV = computed(() => {
      const cat = this.selectedCategoriaCSV();
      if (!cat) return [];
      return this.productosDisponibles().filter(p => p.categoria === cat);
    });

    get selectedCategoriaCSVValue(): string | null {
      return this.selectedCategoriaCSV();
    }
    set selectedCategoriaCSVValue(val: string | null) {
      this.selectedCategoriaCSV.set(val === '' ? null : val);
      this.selectedProductoCSV.set(null); // Reset producto al cambiar categoría
    }

    get productoSeleccionado(): ProductoCSV | null {
      return this.selectedProductoCSV();
    }

    // Datos de predicción grupal
    readonly groupPrediction = signal<PredictionResponseGroup | null>(null);
    readonly _pageSize = signal(10);
    readonly currentPage = signal(1);

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
      this.currentPage.set(1);
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
      return [
        ...new Set(
          group.predicciones
            .map(p => p.categoria)
            .filter(cat => cat !== 'Categoría Desconocida')
        )
      ];
    });

    readonly productosFiltrados = computed(() => {
      const group = this.groupPrediction();
      const categoria = this.selectedCategoriaValue;
      if (!group || !categoria) return [];
      return [...new Set(group.predicciones.filter(p => p.categoria === categoria).map(p => p.name))];
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

    readonly totalPages = computed(() => {
      const total = this.prediccionesFiltradas().length;
      const size = this._pageSize();
      return Math.max(1, Math.ceil(total / size));
    });

    readonly paginatedPredictions = computed(() => {
      const all = this.prediccionesFiltradas();
      const page = this.currentPage();
      const size = this._pageSize();
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
      const producto = this.selectedProductoCSV();
      const productId = producto?.id ?? null;

      if (!fechaFormateada || productId === null) {
        console.warn('Predicción individual: datos inválidos');
        return;
      }

      this.isPredictingIndividual.set(true);
      this.predictionService.predictSingle(productId, fechaFormateada).subscribe({
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
        const filtrado = {
          ...res[0],
          predicciones: res[0].predicciones.filter(
            p =>
              p.categoria &&
              p.categoria !== 'Categoría Desconocida' &&
              p.name &&
              p.name !== 'Nombre Desconocido' &&
              p.precio > 0
          )
        };  
          this.groupPrediction.set(filtrado);
          this.currentPage.set(1);
        },
        error: (err) => {
          console.error('Error al predecir por grupo:', err);
        },
        complete: () => this.isPredictingGroup.set(false)
      });
    }

    getNombreProductoPorId(id: number): string {
      const producto = this.productosDisponibles().find(p => p.id === id);
      return producto ? producto.nombre : `#${id}`;
    }

    ngOnInit(): void {
      this.http.get('assets/productos_250.csv', { responseType: 'text' }).subscribe(csv => {
        const lines = csv.trim().split('\n');
        const data: ProductoCSV[] = lines.slice(1).map(line => {
          const [id, categoria, nombre, precio] = line.split(',');
          return {
            id: parseInt(id.trim(), 10),
            nombre: nombre.trim(),
            precio: parseFloat(precio.trim()),
            categoria: categoria.trim()
          };
        }).filter(p => p.categoria !== 'Categoría Desconocida');
        this.productosDisponibles.set(data);
      });
    }

  }