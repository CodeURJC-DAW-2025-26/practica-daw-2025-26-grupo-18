export type ProfileDTO = {
  id: number;
  username: string;
  email: string;
  country: string | null;
  shortDescription: string | null;
  currentGoal: string | null;
  weeklyRoutine: string | null;
  comunity: string | null;
  profileImage: string | null;
  profileOwner: boolean;
  userType: string;
  completedCourses: number;
  completedCourseNames: string[];
  inProgressCount: number;
  userTags: string[];
  subscribedCourses: Array<Record<string, unknown>>;
  userEvents: Array<Record<string, unknown>>;
  averageProgress: number;
  totalEnrollments: number;
  totalLessonsCompleted: number;
  completedLessonsThisMonth: number;
  averageLessonsPerMonth: string;
  createdCourses: Array<Record<string, unknown>>;
  hasMultipleCourses: boolean;
};

export type ProfileUpdateDTO = {
  username: string;
  email: string;
  country?: string | null;
  shortDescription?: string | null;
  currentGoal?: string | null;
  weeklyRoutine?: string | null;
  comunity?: string | null;
};
