import type { CartDTO, CheckoutRequestDTO } from "../dtos/CartDTO";
import { apiFetch } from "./apiClient";

const BASE_URL = "/api/v1/cart";

/**
 * GET /api/v1/cart — Get the current user's pending order
 */
export async function getCart(): Promise<CartDTO> {
  const res = await apiFetch(BASE_URL);
  if (!res.ok) throw new Error(`Error fetching cart: ${res.status}`);
  return res.json();
}

/**
 * POST /api/v1/cart/courses/:id — Add a course to the cart
 */
export async function addCourseToCart(id: number): Promise<CartDTO> {
  const res = await apiFetch(`${BASE_URL}/courses/${id}`, { method: "POST" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error adding course to cart: ${res.status}`);
  }
  return res.json();
}

/**
 * POST /api/v1/cart/events/:id — Add an event to the cart
 */
export async function addEventToCart(id: number): Promise<CartDTO> {
  const res = await apiFetch(`${BASE_URL}/events/${id}`, { method: "POST" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error adding event to cart: ${res.status}`);
  }
  return res.json();
}

/**
 * POST /api/v1/cart/subscriptions — Add the premium subscription to the cart
 */
export async function addSubscriptionToCart(): Promise<CartDTO> {
  const res = await apiFetch(`${BASE_URL}/subscriptions`, { method: "POST" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error adding subscription to cart: ${res.status}`);
  }
  return res.json();
}

/**
 * DELETE /api/v1/cart/items/:itemId — Remove an item from the cart
 */
export async function removeItemFromCart(itemId: number): Promise<CartDTO> {
  const res = await apiFetch(`${BASE_URL}/items/${itemId}`, { method: "DELETE" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error removing item from cart: ${res.status}`);
  }
  return res.json();
}

/**
 * POST /api/v1/cart/payments — Process payment and complete purchase
 */
export async function checkout(request: CheckoutRequestDTO): Promise<void> {
  const res = await apiFetch(`${BASE_URL}/payments`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });
  
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error processing checkout: ${res.status}`);
  }
}
