import type { GlobalDataDTO } from "~/dtos/GlobalDataDTO";

const BASE_URL = "/api/v1";

/**
 * GET /api/v1/global — Returns global session and permissions data
 */
export async function getGlobalData(): Promise<GlobalDataDTO> {
  const res = await fetch(`${BASE_URL}/global`);
  if (!res.ok) throw new Error(`Error fetching global data: ${res.status}`);
  return res.json();
}
