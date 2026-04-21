import React from 'react';
import type { ChartDataDTO } from "../dtos/ChartDTO";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';
import { Bar, Line, Pie, Doughnut } from 'react-chartjs-2';

// Registramos los componentes de Chart.js necesarios
ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

const GenericChart: React.FC<ChartDataDTO> = ({ 
  chartType, 
  chartTitle, 
  chartLabels, 
  chartValues 
}) => {

  const data = {
    labels: chartLabels,
    datasets: [
      {
        label: chartTitle,
        data: chartValues,
        backgroundColor: [
          'rgba(53, 162, 235, 0.5)',
          'rgba(255, 99, 132, 0.5)',
          'rgba(75, 192, 192, 0.5)',
          'rgba(255, 206, 86, 0.5)',
          'rgba(153, 102, 255, 0.5)',
          'rgba(255, 159, 64, 0.5)'
        ],
        borderColor: [
          'rgb(53, 162, 235)',
          'rgb(255, 99, 132)',
          'rgb(75, 192, 192)',
          'rgb(255, 206, 86)',
          'rgb(153, 102, 255)',
          'rgb(255, 159, 64)'
        ],
        borderWidth: 1,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top' as const },
      title: { display: true, text: chartTitle },
    },
  };

  const renderChart = () => {
    switch(chartType) {
        case 'bar':
            return <Bar data={data} options={options} />;
        case 'line':
            return <Line data={data} options={options} />;
        case 'pie':
            return <Pie data={data} options={options} />;
        case 'doughnut':
            return <Doughnut data={data} options={options} />;
        default:
            return <Bar data={data} options={options} />;
    }
  }

  return (
    <div style={{ width: '100%', height: '400px', padding: '20px' }}>
      {renderChart()}
    </div>
  );
};

export default GenericChart;
