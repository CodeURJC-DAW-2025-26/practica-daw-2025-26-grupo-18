import { useEffect, useState } from "react";
import type { ChartDataDTO } from "../dtos/ChartDTO";
import * as ChartService from "../services/chartService";
import * as C from "../constants/constants";
import { PieChart, BarChart } from '@mui/x-charts';


function RenderBarChart({ data }: { data: ChartDataDTO }) {
  if (!data || !data.chartLabels || !data.chartValues) {
    return <div>Cargando gráfico...</div>;
  }
  const alternatingColors = ['#D58B66', '#907963'];
  const series = data.chartValues.map((value, i) => ({
    data: data.chartValues.map((_, j) => (i === j ? value : null)),
    stack: 'total',
    color: alternatingColors[i % 2],
    label: i === 0 ? data.chartTitle : undefined,
  }));
  return (
    <BarChart
      xAxis={[{ scaleType: 'band', data: data.chartLabels }]}
      series={series}
      height={270}
    />
  );
}

function RenderPieChart({ data, isDonut }: { data: ChartDataDTO, isDonut: boolean }) {
  const formattedData = data.chartLabels.map((name, i) => {
    return {
      id: i,
      value: data.chartValues[i] || 0,
      label: name,
    };
  });

  return (
    <PieChart
      colors={['#D58B66', '#907963', '#4e79a7', '#f28e2c', '#e15759']}
      series={[
        {
          data: formattedData,
          outerRadius: 90,
          ...(isDonut && {
            innerRadius: 30,
            paddingAngle: 5,
            cornerRadius: 5,
          }),
        },
      ]}
      width={313}
      height={250}
      margin={{ top: 5, bottom: 5, left: 5, right: 5 }} // Ajusta los márgenes para acercar el gráfico y la leyenda
      slotProps={{
        legend: {
          position: { vertical: 'bottom', horizontal: 'center' },
          direction: 'horizontal',
        },
      }}
    />
  );
}


export default function Chart({ info, infoUser, infoCourse }: { 
  info: string, 
  infoUser: number, 
  infoCourse: number 
}) {
  const [data, setData] = useState<ChartDataDTO | null>(null);
  const [error, setError] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        setLoading(true);
        let dtoResult: ChartDataDTO;
        switch (info) {
          case C.GET_COURSE_PROGRESS: dtoResult = await ChartService.getCourseProgress(infoUser); break;
          case C.GET_LESSONS_LEARNED: dtoResult = await ChartService.getLessonsLearned(infoUser); break;
          case C.GET_COURSE_GENDERS: dtoResult = await ChartService.getCourseGenders(infoCourse); break;
          case C.GET_COURSE_AGES: dtoResult = await ChartService.getCourseAges(infoCourse); break;
          case C.GET_COURSE_TAGS: dtoResult = await ChartService.getCourseTags(infoUser, infoCourse); break;
          case C.GET_COURSE_USER_PROGRESS: dtoResult = await ChartService.getCourseUserProgress(infoCourse, infoUser); break;
          case C.GET_CREATED_COURSE_STATUS: dtoResult = await ChartService.getCreatedCourseStatus(infoCourse); break;
          default: throw new Error("Tipo de info no soportado");
        }
        
        setData(dtoResult);
        setError(false);
      } catch (e) {
        console.error("Error cargando gráfico:", e);
        setError(true);
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, [info, infoUser, infoCourse]);

  if (loading) return <>Cargando estadísticas...</>;
  if (error || !data) return <>Error al cargar el contenido</>;
  if (!data.chartLabels || data.chartLabels.length === 0 || !data.chartValues || data.chartValues.length === 0) {
    return <span className="text-muted small">Sin datos disponibles aún</span>;
  }

  switch (data.chartType) { 
    case C.GRAPHIC_BAR:
      return <RenderBarChart data={data} />;
    
    case C.GRAPHIC_PIE:
      return <RenderPieChart data={data} isDonut={false} />;  
    case C.GRAPHIC_DOUGHTNUT:
      return <RenderPieChart data={data} isDonut={true} />;    
    default:
      return <>Tipo de gráfico no reconocido: {data.chartType}</>;
  }
}