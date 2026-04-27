const PUBLIC_BASE = import.meta.env.BASE_URL;

export function publicAsset(path: string): string {
  const normalizedPath = path.startsWith("/") ? path.slice(1) : path;
  return `${PUBLIC_BASE}${normalizedPath}`;
}
