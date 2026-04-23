export interface ChartDataDTO {
  chartTitle: string;
  chartType: string;
  chartLabels: string[];
  chartValues: number[];
}

// Re-exportamos ChartOptions de chart.js por si lo estás importando desde aquí
export type { ChartOptions } from 'chart.js';
