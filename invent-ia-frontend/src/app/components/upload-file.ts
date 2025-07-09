import { Component, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-upload-file',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './upload-file.html',
  styleUrls: ['./upload-file.scss']
})
export class UploadFile {
  private rawData = signal<any[][]>([]);

  readonly previewData = computed(() => this.rawData().slice(0, 5));

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      const data = new Uint8Array(reader.result as ArrayBuffer);
      const workbook = XLSX.read(data, { type: 'array' });
      const firstSheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[firstSheetName];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 }) as unknown[];

      if (Array.isArray(jsonData) && jsonData.every(row => Array.isArray(row))) {
        this.rawData.set(jsonData as any[][]);
      } else {
        console.warn('El archivo no tiene un formato de tabla válido.');
        this.rawData.set([]);
      }

    };
    reader.readAsArrayBuffer(file);
  }
}
