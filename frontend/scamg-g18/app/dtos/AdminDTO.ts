export type AdminUserDTO = {
  id: number;
  username: string;
  email: string;
  isActive: boolean;
  isSubscribed: boolean;
};

export type AdminCourseDTO = {
  id: number;
  title: string;
  shortDescription: string;
  status: "DRAFT" | "PENDING_REVIEW" | "PUBLISHED" | "ARCHIVED";
};

export type AdminEventDTO = {
  id: number;
  title: string;
  category: string;
  status: "DRAFT" | "PENDING_REVIEW" | "PUBLISHED" | "ARCHIVED";
};

export type AdminOrderItemDTO = {
  id: number;
  resourceType?: string;
  resourceId?: number;
  title?: string;
  amountCents?: number;
};

export type AdminOrderDTO = {
  id: number;
  userId: number;
  totalAmountCents: number;
  totalAmountEuros: string;
  status: string;
  createdAt: string;
  items: AdminOrderItemDTO[];
};

export type AdminReviewDTO = {
  id: number;
  courseId: number;
  userId: number;
  content: string;
  rating: number;
  createdAt: string;
};
