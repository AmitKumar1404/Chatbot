const raw = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:9999/api/v1';

export const API_BASE_URL = raw.replace(/\/$/, '');

export function wsChatUrl() {
  try {
    const u = new URL(API_BASE_URL);
    u.protocol = u.protocol === 'https:' ? 'wss:' : 'ws:';
    let path = u.pathname.replace(/\/$/, '');
    // REST lives under server.servlet.context-path (e.g. /api/v1). If env omits the path, match backend default.
    if (!path || path === '/') {
      path = '/api/v1';
    }
    return `${u.origin}${path}/ws-chat`;
  } catch {
    return 'ws://localhost:9999/api/v1/ws-chat';
  }
}
