import { Component } from '@angular/core';

@Component({
  selector: 'app-explanation',
  imports: [],
  templateUrl: './explanation.html',
  styleUrl: './explanation.scss'
})
export class Explanation {

  keyFeatures = [
    { name: 'Precio', description: 'El precio influye directamente en la demanda.' },
    { name: 'Estacionalidad', description: 'Factores de temporada afectan el consumo.' },
    { name: 'Promociones', description: 'Descuentos aumentan la probabilidad de compra.' },
  ];

  featureImportance = [
    { name: 'Precio', importance: 40 },
    { name: 'Estacionalidad', importance: 35 },
    { name: 'Promociones', importance: 25 },
  ];

}
