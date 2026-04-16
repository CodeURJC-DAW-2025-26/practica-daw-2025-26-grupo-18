export interface LessonDTO {
  id: number;
  title: string;
  videoUrl: string;
  description: string;
  orderIndex: number;
}

export interface ModuleDTO {
  id: number;
  title: string;
  description: string;
  orderIndex: number;
  lessons: LessonDTO[];
}

export interface CourseDTO {
  id: number;
  title: string;
  shortDescription: string;
  longDescription: string;
  price: number;
  priceCents: number;
  videoHours: number;
  downloadableResources: number;
  learningPoints: string[];
  prerequisites: string[];
  modules: ModuleDTO[];
  status: 'DRAFT' | 'PENDING_REVIEW' | 'PUBLISHED' | 'ARCHIVED';
  language: string;
}
