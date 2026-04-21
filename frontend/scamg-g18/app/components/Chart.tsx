import { useEffect, useState } from "react";
import type { ChartDataDTO } from "../dtos/ChartDTO";
import * as ChartService from "../services/chartService";
import * as C from "../constants/constants";
import { PieChart, BarChart } from '@mui/x-charts';

function RenderBarChart({ data }: { data: ChartDataDTO }) {
  return (
    <BarChart
      xAxis={[{ data: data.chartLabels, scaleType: 'band' }]}
      series={[{ data: data.chartValues, label: data.chartTitle }]}
      height={300}
    />
  );
}

function RenderPieChart({ data, isDonut }: { data: ChartDataDTO, isDonut: boolean }) {
  const formattedData = data.chartLabels.map((name, i) => ({
    id: i,
    value: data.chartValues[i],
    label: name
  }));

  return (
    <PieChart
      series={[
        {
          data: formattedData,
          ...(isDonut && {
            innerRadius: 30,
            outerRadius: 100,
            paddingAngle: 5,
            cornerRadius: 5,
          }),
        },
      ]}
      width={400}
      height={200}
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
          case C.GET_COURSE_GENDERS: dtoResult = await ChartService.getCourseGenders(infoUser); break;
          case C.GET_COURSE_AGES: dtoResult = await ChartService.getCourseAges(infoUser); break;
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