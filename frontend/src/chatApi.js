import { API_BASE_URL } from "./apiConfig";

function authHeaders(token, json = false) {
  const h = { Authorization: `Bearer ${token}` };
  if (json) h["Content-Type"] = "application/json";
  return h;
}

export async function fetchChatSessions(token) {
  const res = await fetch(`${API_BASE_URL}/chat/sessions`, {
    headers: authHeaders(token),
  });
  if (!res.ok) throw new Error("Failed to load chat sessions");
  return res.json();
}

export async function createChatSession(token) {
  const res = await fetch(`${API_BASE_URL}/chat/sessions`, {
    method: "POST",
    headers: authHeaders(token),
  });
  if (!res.ok) throw new Error("Failed to create chat session");
  return res.json();
}

export async function uploadDocumentApi(token, file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${API_BASE_URL}/api/documents/upload`, {
    method: "POST",
    headers: authHeaders(token),
    body: formData,
  });
  if (!res.ok) throw new Error("Failed to upload document");
  return res.json();
}

export async function fetchSessionMessages(token, sessionId, options = {}) {
  const { signal } = options;
  const res = await fetch(
    `${API_BASE_URL}/chat/sessions/${sessionId}/messages`,
    { headers: authHeaders(token), signal }
  );
  if (!res.ok) throw new Error("Failed to load messages");
  return res.json();
}

/** Returns active stream metadata, or null when no stream is in flight. */
export async function fetchActiveStream(token, options = {}) {
  const { signal } = options;
  const res = await fetch(`${API_BASE_URL}/chat/stream/active`, {
    headers: authHeaders(token),
    signal,
  });
  if (res.status === 204 || res.status === 404) return null;
  if (!res.ok) throw new Error("Failed to load active stream status");
  return res.json();
}

export async function deleteChatSessionApi(token, sessionId) {
  const res = await fetch(`${API_BASE_URL}/chat/sessions/${sessionId}`, {
    method: "DELETE",
    headers: authHeaders(token),
  });
  if (!res.ok) throw new Error("Failed to delete chat session");
}

export async function updateChatSessionTitleApi(token, sessionId, title) {
  const res = await fetch(
    `${API_BASE_URL}/chat/sessions/${sessionId}/title`,
    {
      method: "PATCH",
      headers: authHeaders(token, true),
      body: JSON.stringify({ title }),
    }
  );
  if (!res.ok) throw new Error("Failed to rename chat session");
  return res.json();
}

export async function updateChatSessionPinnedApi(token, sessionId, pinned) {
  const res = await fetch(
    `${API_BASE_URL}/chat/sessions/${sessionId}/pin`,
    {
      method: "PATCH",
      headers: authHeaders(token, true),
      body: JSON.stringify({ pinned }),
    }
  );
  if (!res.ok) throw new Error("Failed to update chat session pin");
  return res.json();
}

export async function updateMessageFeedbackApi(
  token,
  messageId,
  feedbackType,
  feedbackReason
) {
  const body = { feedbackType };
  if (feedbackReason) body.feedbackReason = feedbackReason;

  const res = await fetch(`${API_BASE_URL}/chat/messages/${messageId}/feedback`, {
    method: "PUT",
    headers: authHeaders(token, true),
    body: JSON.stringify(body),
  });
  if (!res.ok) throw new Error("Failed to update message feedback");
  return res.json();
}

export async function searchChatsApi(token, query) {
  const trimmed = (query ?? "").trim();
  if (!trimmed) return [];

  const params = new URLSearchParams({ q: trimmed });
  const res = await fetch(`${API_BASE_URL}/search?${params.toString()}`, {
    headers: authHeaders(token),
  });
  if (!res.ok) throw new Error("Failed to search chats");
  return res.json();
}
