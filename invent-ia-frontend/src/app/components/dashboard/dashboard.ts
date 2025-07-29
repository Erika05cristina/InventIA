import { Component } from '@angular/core';
import { ChartModule } from 'primeng/chart';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ChartModule, CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
})
export class Dashboard {
  totalProductos = 250;
  totalInvertido = 7804;
  totalCategorias = 6;
  productosExitosos = 135;
  promedioInversion = Math.round(this.totalInvertido / this.totalProductos);

  // Gráfico de pastel: Inversión por categoría
  chartDataPie = {
    labels: ['Tecnología', 'Alimentos', 'Ropa', 'Salud', 'Hogar', 'Juguetes'],
    datasets: [
      {
        data: [20000, 18000, 15000, 13000, 17000, 15500],
        backgroundColor: ['#42A5F5', '#66BB6A', '#FFA726', '#AB47BC', '#FF7043', '#26C6DA'],
        hoverBackgroundColor: ['#64B5F6', '#81C784', '#FFB74D', '#CE93D8', '#FF8A65', '#4DD0E1'],
      },
    ],
  };

  chartOptionsPie = {
    responsive: true,
    maintainAspectRatio: true,
    aspectRatio: 1.4,
    plugins: {
      tooltip: {
        callbacks: {
          label: function (context: any) {
            const label = context.label || '';
            const value = context.parsed;
            return `${label}: $${value.toLocaleString()}`;
          },
        },
      },
      legend: {
        position: 'bottom',
      },
    },
  };

  // Gráfico de barras: Rentabilidad por categoría
  chartDataBar = {
    labels: ['Tecnología', 'Alimentos', 'Ropa', 'Salud', 'Hogar', 'Juguetes'],
    datasets: [
      {
        label: 'Rentabilidad (%)',
        data: [32, 28, 21, 25, 30, 27],
        backgroundColor: '#4CAF50',
      },
    ],
  };

  chartOptionsBar = {
    responsive: true,
    maintainAspectRatio: true,
    aspectRatio: 1.6,
    plugins: {
      legend: { display: false },
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 40,
      },
    },
  };

  // Gráfico de línea: Inversión mensual
  chartDataLine = {
    labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio'],
    datasets: [
      {
        label: 'Inversión Mensual ($)',
        data: [10000, 12000, 15000, 14000, 18000, 19500],
        fill: false,
        tension: 0.4,
        borderColor: '#42A5F5',
        backgroundColor: '#42A5F5',
      },
    ],
  };

  chartOptionsLine = {
    responsive: true,
    maintainAspectRatio: true,
    aspectRatio: 1.8,
    plugins: {
      legend: {
        position: 'top',
      },
    },
  };

  // Top 5 productos con mayor inversión
  ranking = [
    { nombre: 'Smartphone Z', inversion: 8500 },
    { nombre: 'Refrigeradora X', inversion: 7900 },
    { nombre: 'Laptop Pro', inversion: 7400 },
    { nombre: 'Bicicleta Urbana', inversion: 6900 },
    { nombre: 'TV 4K 55"', inversion: 6400 },
  ];

  // Radar: Stock disponible por categoría
  chartDataRadar = {
    labels: ['Tecnología', 'Alimentos', 'Ropa', 'Salud', 'Hogar', 'Juguetes'],
    datasets: [
      {
        label: 'Stock disponible',
        data: [120, 200, 170, 90, 160, 130],
        backgroundColor: 'rgba(66, 165, 245, 0.2)',
        borderColor: '#42A5F5',
        pointBackgroundColor: '#42A5F5',
      },
    ],
  };

  chartOptionsRadar = {
    responsive: true,
    maintainAspectRatio: true,
    aspectRatio: 1.4,
    plugins: {
      legend: { position: 'top' },
    },
  };

  // Barra horizontal: Precisión por categoría
  chartDataHorizontalBar = {
    labels: ['Tecnología', 'Alimentos', 'Ropa', 'Salud', 'Hogar', 'Juguetes'],
    datasets: [
      {
        label: 'Precisión (%)',
        data: [91, 87, 78, 82, 88, 85],
        backgroundColor: '#9CCC65',
      },
    ],
  };

  chartOptionsHorizontalBar = {
    responsive: true,
    indexAxis: 'y' as const,
    maintainAspectRatio: true,
    aspectRatio: 1.4,
    scales: {
      x: {
        min: 0,
        max: 100,
        ticks: {
          stepSize: 10,
        },
      },
    },
    plugins: {
      legend: { display: false },
    },
  };

  // Dispersión: Demanda de productos no rentables
  chartDataScatter = {
    datasets: [
      {
        label: 'Productos no rentables',
        data: [
          { x: 5, y: 12, nombre: 'Impresora Básica' },
          { x: 10, y: 9, nombre: 'Reloj Inteligente Lite' },
          { x: 15, y: 6, nombre: 'Zapatillas Retro' },
          { x: 20, y: 4, nombre: 'Purificador de Aire Mini' },
          { x: 25, y: 2, nombre: 'Set de Ollas Tradicional' },
        ],
        backgroundColor: '#EF5350',
      },
    ],
  };

  chartOptionsScatter = {
    responsive: true,
    maintainAspectRatio: true,
    aspectRatio: 1.6,
    scales: {
      x: {
        title: { display: true, text: 'Tiempo (semanas)' },
      },
      y: {
        title: { display: true, text: 'Ventas (unidades)' },
      },
    },
    plugins: {
      tooltip: {
        callbacks: {
          label: function (context: any) {
            const point = context.raw;
            return `${point.nombre}: (${point.x}, ${point.y} ventas)`;
          },
        },
      },
    },
  };

}
