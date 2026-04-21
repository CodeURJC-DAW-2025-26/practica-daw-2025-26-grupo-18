export interface EventDTO {
  id: number;
  title: string;
  description: string;
  price: number;
  priceCents: number;
  startDate?: string;
  endDate?: string;
  startDateStr?: string;
  startTimeStr?: string;
  endDateStr?: string;
  endTimeStr?: string;
  capacity: number;
  category: string;
  locationName: string;
  locationAddress: string;
  locationCity: string;
  locationCountry: string;
  locationLatitude: number;
  locationLongitude: number;
  status: 'DRAFT' | 'PENDING_REVIEW' | 'PUBLISHED' | 'ARCHIVED';

  // Campos que vienen del backend en la vista detalle (GET)
  speakers?: string[];
  sessions?: {
    id?: number;
    time: string;
    title: string;
    description: string;
  }[];
  alreadyPurchased?: boolean;
  canEdit?: boolean;
  canDelete?: boolean;
  formattedDate?: string;
  formattedTime?: string;
  attendeesCount?: number;
  priceEuros?: string;
  tags?: { name: string }[];

  // Campos para CREACIÓN/EDICIÓN (POST/PUT)
  speakerNames?: string[];
  sessionTimes?: string[];
  sessionTitles?: string[];
  sessionDescriptions?: string[];
}
