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

export async function fetchSessionMessages(token, sessionId) {
  const res = await fetch(
    `${API_BASE_URL}/chat/sessions/${sessionId}/messages`,
    { headers: authHeaders(token) }
  );
  if (!res.ok) throw new Error("Failed to load messages");
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
