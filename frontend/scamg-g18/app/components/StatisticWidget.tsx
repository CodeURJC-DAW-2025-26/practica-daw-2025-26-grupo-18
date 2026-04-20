import React, { useEffect, useState } from "react";
import GenericChart from "../routes/chart";
import { 
  getCourseProgress, 
  getLessonsLearned, 
  getCourseGenders, 
  getCourseAges, 
  getCourseTags, 
  getCourseUserProgress, 
  getCreatedCourseStatus 
} from "../services/chartService";
import type { ChartDataDTO } from "../dtos/ChartDTO";

interface StatisticWidgetProps {
  type: "courseProgress" | "lessonsLearned" | "courseGenders" | "courseAges" | "courseTags" | "courseUserProgress" | "createdCourseStatus";
  userId?: number;
  courseId?: number;
}

export default function StatisticWidget({ type, userId, courseId }: StatisticWidgetProps) {
  const [data, setData] = useState<ChartDataDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    setLoading(true);
    let fetchPromise: Promise<ChartDataDTO>;

    // Según lo que pida la pantalla, llamamos a un endpoint u otro
    switch (type) {
      case "courseProgress":
        fetchPromise = getCourseProgress(userId!);
        break;
      case "lessonsLearned":
        fetchPromise = getLessonsLearned(userId!);
        break;
      case "courseGenders":
        fetchPromise = getCourseGenders(courseId!);
        break;
      case "courseAges":
        fetchPromise = getCourseAges(courseId!);
        break;
      case "courseTags":
        fetchPromise = getCourseTags(userId!, courseId!);
        break;
      case "courseUserProgress":
        fetchPromise = getCourseUserProgress(courseId!, userId!);
        break;
      case "createdCourseStatus":
        fetchPromise = getCreatedCourseStatus(courseId!);
        break;
      default:
        return;
    }

    fetchPromise
      .then((res) => {
        setData(res);
        setError(false);
      })
      .catch(() => setError(true))
      .finally(() => setLoading(false));

  }, [type, userId, courseId]);

  if (loading) return <div>Cargando gráfico...</div>;
  if (error || !data) return <div>Error al cargar el gráfico</div>;

  // Una vez cargados los datos, le pasamos la "pelota" a tu GenericChart
  return <GenericChart {...data} />;
}
