export interface EventDTO {
  id: number;
  title: string;
  description: string;
  price: number;
  priceCents: number;
  startDate: string;
  endDate: string;
  startDateStr: string;
  startTimeStr: string;
  endDateStr: string;
  endTimeStr: string;
  capacity: number;
  category: string;
  locationName: string;
  locationAddress: string;
  locationCity: string;
  locationCountry: string;
  locationLatitude: number;
  locationLongitude: number;
  speakerNames: string[];
  sessionTimes: string[];
  sessionTitles: string[];
  sessionDescriptions: string[];
  status: 'DRAFT' | 'PENDING_REVIEW' | 'PUBLISHED' | 'ARCHIVED';
}
