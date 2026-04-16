export interface GlobalDataDTO {
  isUserLoggedIn: boolean;
  userId: number | null;
  userName: string | null;
  userProfileImage: string | null;
  canCreateEvent: boolean;
  canCreateCourse: boolean;
  isAdmin: boolean;
  isPublisher: boolean;
}
