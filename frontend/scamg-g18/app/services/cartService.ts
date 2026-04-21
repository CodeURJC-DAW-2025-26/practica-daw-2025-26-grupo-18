import type { CartDTO, CheckoutRequestDTO } from "../dtos/CartDTO";

const BASE_URL = "/api/v1/cart";

/**
 * GET /api/v1/cart — Get the current user's pending order
 */
export async function getCart(): Promise<CartDTO> {
  const res = await fetch(BASE_URL);
  if (!res.ok) throw new Error(`Error fetching cart: ${res.status}`);
  return res.json();
}

/**
 * POST /api/v1/cart/add/course/:id — Add a course to the cart
 */
export async function addCourseToCart(id: number): Promise<CartDTO> {
  const res = await fetch(`${BASE_URL}/add/course/${id}`, { method: "POST" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error adding course to cart: ${res.status}`);
  }
  return res.json();
}

/**
 * POST /api/v1/cart/add/event/:id — Add an event to the cart
 */
export async function addEventToCart(id: number): Promise<CartDTO> {
  const res = await fetch(`${BASE_URL}/add/event/${id}`, { method: "POST" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error adding event to cart: ${res.status}`);
  }
  return res.json();
}

/**
 * POST /api/v1/cart/add/subscription — Add the premium subscription to the cart
 */
export async function addSubscriptionToCart(): Promise<CartDTO> {
  const res = await fetch(`${BASE_URL}/add/subscription`, { method: "POST" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error adding subscription to cart: ${res.status}`);
  }
  return res.json();
}

/**
 * POST /api/v1/cart/remove/:itemId — Remove an item from the cart
 */
export async function removeItemFromCart(itemId: number): Promise<CartDTO> {
  const res = await fetch(`${BASE_URL}/remove/${itemId}`, { method: "POST" });
  if (!res.ok) {
    const errorText = await res.text().catch(() => "");
    throw new Error(errorText || `Error removing item from cart: ${res.status}`);
  }
  return res.json();
}

/**
 * POST /api/v1/cart/checkout — Process payment and complete purchase
 */
export async function checkout(request: CheckoutRequestDTO): Promise<void> {
  const res = await fetch(`${BASE_URL}/checkout`, {
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
