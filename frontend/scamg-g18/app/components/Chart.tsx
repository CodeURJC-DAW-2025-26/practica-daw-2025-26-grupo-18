import { useEffect, useState } from "react";
import { Link, NavLink } from "react-router";
import type { ChartDataDTO } from "../dtos/ChartDTO";
import * as ChartService from "../services/chartService";
import * as C from "../constants/constants";
import { PieChart, BarChart } from '@mui/x-charts';


function getInfo(info: string, infoUser: number, infoCourse: number): Promise<ChartDataDTO>{
  switch (info) {
      case C.GET_COURSE_PROGRESS:
        return ChartService.getCourseProgress(infoUser);
      case C.GET_LESSONS_LEARNED:
        return ChartService.getLessonsLearned(infoUser);
      case C.GET_COURSE_GENDERS:
        return ChartService.getCourseGenders(infoUser);
      case C.GET_COURSE_AGES:
        return ChartService.getCourseAges(infoUser);
      case C.GET_COURSE_TAGS:
        return ChartService.getCourseTags(infoUser, infoCourse);
      case C.GET_COURSE_USER_PROGRESS:
        return ChartService.getCourseUserProgress(infoCourse, infoUser);
      case C.GET_CREATED_COURSE_STATUS:
        return ChartService.getCreatedCourseStatus(infoCourse);
      default:
        return Promise.resolve(null!);
  }
}
function barChart(info: ChartDataDTO){
  const { chartLabels, chartValues, chartTitle } = info;
  return (
    <BarChart
      xAxis={[{data: chartLabels, 
        scaleType: 'band'}]}
      series={[{
        data: chartValues,
        label: chartTitle}]}
      height={300}
    />
  );
}

function pieChart(info: ChartDataDTO){
  const { chartLabels, chartValues, chartTitle } = info;
  const formatedData = chartLabels.map((name, i) => ({
    id: i, 
    value: chartValues[i],
    label: name
  }));
  return (
    <PieChart
      series={[
        {
          data: formatedData
        },
      ]}
      width={400} 
      height={200}
    />
  );
}


//info = getCourseProgress, getLesson...
export default function Chart({ chartType, info, infoUser, infoCourse }: { 
  chartType: string, 
  info: string, 
  infoUser: number, 
  infoCourse: number 
}){
  const [data, setData] = useState<ChartDataDTO | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    async function loadData() {
      try {
        const dtoResult = await getInfo(info, infoUser, infoCourse);
        setData(dtoResult);
      } catch (e) {
        setError(true);
      }
    }
    loadData();
  }, [info, infoUser, infoCourse]); 
  if (error) return <>Fallo con la carga de contenido</>;
  if (!data) return <>Movida de carga de Alberto</>; 

  switch(chartType){
    case C.GRAPHIC_BAR:
      return barChart(data);
    case C.GRAPHIC_PIE:
      return pieChart(data);
    default:
      return <>Tipo de gráfico no reconocido</>;
  }
}