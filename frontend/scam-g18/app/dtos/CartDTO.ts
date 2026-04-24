import type { CourseDTO } from "./CourseDTO";
import type { EventDTO } from "./EventDTO";

export type OrderStatus = "PENDING" | "PAID" | "FAILED" | "REFUNDED";

export interface CartItemDTO {
    id: number;
    course: CourseDTO | null;
    event: EventDTO | null;
    priceAtPurchaseCents: number;
    priceInEuros: string;
    subscription: boolean;
}

export interface CartDTO {
    id: number;
    userId: number;
    totalAmountCents: number;
    totalAmountEuros: string;
    status: OrderStatus;
    createdAt: string;
    items: CartItemDTO[];
}

export interface CheckoutRequestDTO {
    cardName: string;
    billingEmail: string;
    cardNumber: string;
    cardExpiry: string;
    cardCvv: string;
}

