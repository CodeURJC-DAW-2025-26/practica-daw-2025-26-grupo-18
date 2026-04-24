export interface LessonDTO {
  id: number;
  title: string;
  videoUrl: string;
  description: string;
  orderIndex: number;
  completed?: boolean;
}

export interface ModuleDTO {
  id: number;
  title: string;
  description: string;
  orderIndex: number;
  lessons: LessonDTO[];
  first?: boolean;
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
  status: 'DRAFT' | 'PENDING_REVIEW' | 'PUBLISHED' | 'ARCHIVED';
  language: string;
  updatedAt?: string;
  subscribersNumber?: number;
  creator?: {
    id: number;
    username: string;
    shortDescription: string;
    currentGoal: string;
    comunity: string;
    otherCourses: { id: number; title: string }[];
  };
  image?: {
    url: string;
  };
  tags?: { id: number; name: string }[];
  tagNames?: string[];
  modules?: ModuleDTO[];
}

export interface CourseDetailDTO {
  course: CourseDTO;
  modules: ModuleDTO[];
  reviews: any[];
  priceInEuros: string;
  averageRating: string;
  ratingCount: number;
  reviewsNumber: number;
  averageRatingStars: string;
  hasSubscribers: boolean;
  courseProgressPercentage: number;
  isSubscribedToCourse: boolean;
  canEdit: boolean;
}
