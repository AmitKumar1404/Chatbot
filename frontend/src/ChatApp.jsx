import { useState, useEffect, useRef, useCallback } from "react";
import ChatWindow from "./components/ChatWindow";
import InputBox from "./components/InputBox";
import {
  connectWebSocket,
  sendMessage,
  sendStopSignal,
  disconnectWebSocket,
} from "./websocket";
import { useAuth } from "./context/AuthContext";
import {
  fetchChatSessions,
  createChatSession,
  fetchSessionMessages,
  deleteChatSessionApi,
  updateChatSessionTitleApi,
} from "./chatApi";
import "./App.css";

function buildPriorDtos(messages) {
  return messages
    .filter((m) => m && (m.role === "user" || m.role === "assistant"))
    .filter((m) => !(m.role === "assistant" && m.streaming))
    .map((m) => ({ role: m.role, content: m.content ?? "" }));
}

function mapDbMessageToUi(m) {
  const uid = m.userBubbleClientId || `legacy-u-${m.id}`;
  const aid = m.assistantBubbleClientId || `legacy-a-${m.id}`;
  return [
    {
      id: uid,
      role: "user",
      content: m.userMessage ?? "",
      responseTo: null,
      editing: false,
      streaming: false,
    },
    {
      id: aid,
      role: "assistant",
      content: m.aiResponse ?? "",
      responseTo: uid,
      editing: false,
      streaming: false,
    },
  ];
}

export default function ChatApp() {
  const { token, username, logout } = useAuth();

  const [chats, setChats] = useState([]);
  const [activeChatId, setActiveChatId] = useState(null);

  const [connected, setConnected] = useState(false);
  const [statusText, setStatusText] = useState("Connecting…");

  const [isStreaming, setIsStreaming] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const stopRef = useRef(false);
  const isStreamingRef = useRef(false);
  const activeChatIdRef = useRef(activeChatId);
  const chatsRef = useRef(chats);
  const streamChatIdRef = useRef(null);
  const activeClientStreamIdRef = useRef(null);
  const streamAssistantMessageIdRef = useRef(null);

  useEffect(() => {
    activeChatIdRef.current = activeChatId;
  }, [activeChatId]);

  useEffect(() => {
    chatsRef.current = chats;
  }, [chats]);

  const loadSessions = useCallback(async () => {
    if (!token) return;
    try {
      const rows = await fetchChatSessions(token);
      const mapped = rows.map((s) => ({
        id: s.id,
        title: s.title || "New chat",
        messages: [],
        messagesLoaded: false,
      }));
      setChats(mapped);
      // if (mapped.length === 0) {
      //   setActiveChatId(null);
      //   return;
      // }
      if (mapped.length === 0) {
        const newSession = await createChatSession(token);

        const newChat = {
          id: newSession.id,
          title: newSession.title || "New chat",
          messages: [],
          messagesLoaded: true,
        };

        setChats([newChat]);
        setActiveChatId(newChat.id);
        return;
      }
      const firstId = mapped[0].id;
      setActiveChatId(firstId);
      const firstRows = await fetchSessionMessages(token, firstId);
      const messages = firstRows.flatMap(mapDbMessageToUi);
      setChats((prev) =>
        prev.map((c) =>
          c.id === firstId ? { ...c, messages, messagesLoaded: true } : c
        )
      );
    } catch (e) {
      console.error(e);
      setChats([]);
      setActiveChatId(null);
    }
  }, [token]);

  useEffect(() => {
    if (!token) {
      setChats([]);
      setActiveChatId(null);
      return;
    }
    loadSessions();
  }, [token, loadSessions]);

  const setStreamingState = useCallback((value) => {
    isStreamingRef.current = value;
    setIsStreaming(value);
  }, []);

  const activeChat = chats.find((c) => c.id === activeChatId);
  const userInitial = username?.charAt(0)?.toUpperCase() || "?";

  const selectChat = useCallback(
    async (chatId) => {
      setActiveChatId(chatId);
      if (!token) return;
      const snap = chatsRef.current.find((c) => c.id === chatId);
      if (!snap || snap.messagesLoaded) return;
      try {
        const rows = await fetchSessionMessages(token, chatId);
        const messages = rows.flatMap(mapDbMessageToUi);
        setChats((prev) =>
          prev.map((c) =>
            c.id === chatId ? { ...c, messages, messagesLoaded: true } : c
          )
        );
      } catch (e) {
        console.error(e);
      }
    },
    [token]
  );

  const finalizeStream = useCallback(() => {
    const skipRefetch = stopRef.current;
    const targetChatId = streamChatIdRef.current ?? activeChatIdRef.current;
    const assistantId = streamAssistantMessageIdRef.current;

    if (targetChatId != null && assistantId != null) {
      setChats((prev) =>
        prev.map((chat) => {
          if (chat.id !== targetChatId) return chat;
          return {
            ...chat,
            messages: chat.messages.map((m) =>
              m.id === assistantId && m.role === "assistant"
                ? { ...m, streaming: false }
                : m
            ),
          };
        })
      );
    }

    activeClientStreamIdRef.current = null;
    streamAssistantMessageIdRef.current = null;
    stopRef.current = false;
    streamChatIdRef.current = null;
    setStreamingState(false);

    if (!skipRefetch && token && targetChatId != null) {
      fetchSessionMessages(token, targetChatId)
        .then((rows) => {
          const messages = rows.flatMap(mapDbMessageToUi);
          setChats((prev) =>
            prev.map((c) =>
              c.id === targetChatId
                ? { ...c, messages, messagesLoaded: true }
                : c
            )
          );
        })
        .catch((e) => console.error(e));
    }
  }, [setStreamingState, token]);

  const appendAssistantChunk = useCallback((chunk) => {
    if (stopRef.current) return;
    if (!isStreamingRef.current) return;

    const targetChatId = streamChatIdRef.current ?? activeChatIdRef.current;
    const assistantId = streamAssistantMessageIdRef.current;
    if (!assistantId) return;

    setChats((prev) =>
      prev.map((chat) => {
        if (chat.id !== targetChatId) return chat;

        const idx = chat.messages.findIndex(
          (m) => m.id === assistantId && m.role === "assistant"
        );
        if (idx === -1) {
          return chat;
        }

        const row = chat.messages[idx];
        return {
          ...chat,
          messages: chat.messages.map((m, i) =>
            i === idx
              ? { ...m, content: (row.content ?? "") + chunk, streaming: true }
              : m
          ),
        };
      })
    );
  }, []);

  const handleStreamBody = useCallback(
    (raw) => {
      if (raw == null || raw === "") return;

      let event;
      try {
        event = typeof raw === "string" ? JSON.parse(raw) : raw;
      } catch {
        return;
      }

      const expectedId = activeClientStreamIdRef.current;
      if (!expectedId || event.clientStreamId !== expectedId) return;

      const expectedAssistant = streamAssistantMessageIdRef.current;
      if (
        event.assistantMessageId &&
        expectedAssistant &&
        event.assistantMessageId !== expectedAssistant
      ) {
        return;
      }

      switch (event.type) {
        case "chunk":
          appendAssistantChunk(event.chunk ?? "");
          break;
        case "error":
          appendAssistantChunk(event.message ?? "Error");
          finalizeStream();
          break;
        case "done":
          if (isStreamingRef.current) finalizeStream();
          break;
        default:
          break;
      }
    },
    [appendAssistantChunk, finalizeStream]
  );

  useEffect(() => {
    if (!token) {
      disconnectWebSocket();
      setConnected(false);
      setStatusText("Not signed in");
      return;
    }

    connectWebSocket({
      accessToken: token,
      onMessage: handleStreamBody,
      onConnect: () => {
        setConnected(true);
        setStatusText("Connected");
      },
      onError: () => {
        setConnected(false);
        setStatusText("Disconnected — retrying…");
      },
    });

    return () => disconnectWebSocket();
  }, [handleStreamBody, token]);

  function handleSend(text) {
    if (isStreamingRef.current || !connected) {
      return;
    }
    if (activeChatIdRef.current == null) {
      return;
    }

    const clientStreamId = crypto.randomUUID();
    const userMessageId = crypto.randomUUID();
    const assistantMessageId = crypto.randomUUID();

    const chatId = activeChatIdRef.current;
    const snapshot = chatsRef.current.find((c) => c.id === chatId);
    const priorMessages = buildPriorDtos(snapshot?.messages || []);

    stopRef.current = false;
    streamChatIdRef.current = chatId;
    activeClientStreamIdRef.current = clientStreamId;
    streamAssistantMessageIdRef.current = assistantMessageId;
    setStreamingState(true);

    setChats((prev) =>
      prev.map((chat) => {
        if (chat.id !== chatId) return chat;

        const isFirstCompleteTurn = buildPriorDtos(chat.messages).length === 0;

        return {
          ...chat,
          title: isFirstCompleteTurn ? text.slice(0, 20) : chat.title,
          messages: [
            ...chat.messages,
            {
              id: userMessageId,
              role: "user",
              content: text,
              responseTo: null,
              editing: false,
              streaming: false,
            },
            {
              id: assistantMessageId,
              role: "assistant",
              content: "",
              responseTo: userMessageId,
              editing: false,
              streaming: true,
            },
          ],
        };
      })
    );

    sendMessage({
      type: "NEW",
      sessionId: chatId,
      clientStreamId,
      messageId: assistantMessageId,
      userMessageId,
      content: text,
      priorMessages,
    });
  }

  const handleEditSave = useCallback(
    (userMessageId, newText) => {
      if (isStreamingRef.current || !connected) return false;

      const trimmed = (newText ?? "").trim();
      if (!trimmed) return false;

      const chatId = activeChatIdRef.current;
      if (chatId == null) return false;

      const chatSnapshot = chatsRef.current.find((c) => c.id === chatId);
      if (!chatSnapshot) return false;

      const msgs = chatSnapshot.messages;
      const userIdx = msgs.findIndex(
        (m) => m.id === userMessageId && m.role === "user"
      );
      if (userIdx === -1) return false;

      const paired = msgs[userIdx + 1];
      if (
        !paired ||
        paired.role !== "assistant" ||
        paired.responseTo !== userMessageId
      ) {
        return false;
      }

      const clientStreamId = crypto.randomUUID();
      const assistantMessageId = paired.id;
      const priorMessages = buildPriorDtos(msgs.slice(0, userIdx));

      stopRef.current = false;
      streamChatIdRef.current = chatId;
      activeClientStreamIdRef.current = clientStreamId;
      streamAssistantMessageIdRef.current = assistantMessageId;
      setStreamingState(true);

      setChats((prev) =>
        prev.map((chat) => {
          if (chat.id !== chatId) return chat;
          return {
            ...chat,
            messages: chat.messages.map((m) => {
              if (m.id === userMessageId && m.role === "user") {
                return { ...m, content: trimmed, editing: false };
              }
              if (m.id === assistantMessageId && m.role === "assistant") {
                return { ...m, content: "", streaming: true };
              }
              return m;
            }),
          };
        })
      );

      sendMessage({
        type: "EDIT",
        sessionId: chatId,
        clientStreamId,
        messageId: assistantMessageId,
        content: trimmed,
        editTargetMessageId: userMessageId,
        priorMessages,
      });
      return true;
    },
    [connected, setStreamingState]
  );

  function stopResponse() {
    if (!isStreamingRef.current) return;
    stopRef.current = true;
    sendStopSignal();
    finalizeStream();
  }

  async function createNewChat() {
    if (!token || isStreamingRef.current) return;
    try {
      const s = await createChatSession(token);
      const newChat = {
        id: s.id,
        title: s.title || "New chat",
        messages: [],
        messagesLoaded: true,
      };
      setChats((prev) => [newChat, ...prev]);
      setActiveChatId(newChat.id);
    } catch (e) {
      console.error(e);
    }
  }

  async function deleteChat(chatId) {
    if (!token || isStreamingRef.current) return;
    try {
      await deleteChatSessionApi(token, chatId);
    } catch (e) {
      console.error(e);
      return;
    }
    setChats((prev) => {
      const next = prev.filter((c) => c.id !== chatId);
      if (activeChatIdRef.current === chatId) {
        setActiveChatId(next.length ? next[0].id : null);
      }
      return next;
    });
  }

  async function renameChat(chatId) {
    const newName = prompt("Enter new name");
    if (!newName || !token) return;
    const trimmed = newName.trim();
    if (!trimmed) return;
    try {
      const updated = await updateChatSessionTitleApi(token, chatId, trimmed);
      setChats((prev) =>
        prev.map((c) =>
          c.id === chatId ? { ...c, title: updated.title ?? trimmed } : c
        )
      );
    } catch (e) {
      console.error(e);
    }
  }

  return (
    <div className="app-layout">
      <div className={`sidebar ${isStreaming ? "sidebar-busy" : ""}`}>
        <button className="new-chat-btn" onClick={createNewChat}>
          + New Chat
        </button>

        <div className="chat-list">
          {chats.map((chat) => (
            <div
              key={chat.id}
              className={`chat-item ${
                chat.id === activeChatId ? "active" : ""
              }`}
            >
              <span onClick={() => selectChat(chat.id)}>{chat.title}</span>

              {/* <div className="chat-actions">
                <button type="button" onClick={() => renameChat(chat.id)}>
                  ✏️
                </button>
                <button type="button" onClick={() => deleteChat(chat.id)}>
                  🗑
                </button>
              </div> */}
              <div className="chat-actions">
                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    renameChat(chat.id);
                  }}
                >
                  ✏️
                </button>

                <button
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    deleteChat(chat.id);
                  }}
                >
                  🗑
                </button>
              </div>
            </div>
          ))}
        </div>
        <div className="sidebar-footer">
          <div
            className="profile-wrapper"
            onClick={() => setShowProfileMenu((prev) => !prev)}
          >
            <div className="profile-circle">{userInitial}</div>

            {showProfileMenu && (
              <div className="profile-dropdown">
                <button
                  type="button"
                  className="profile-logout-btn"
                  onClick={() => {
                    setShowProfileMenu(false);
                    setShowLogoutModal(true);
                  }}
                >
                  Log out
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="chat-section">
        <header className="app-header">
          <h1 className="app-title">Talk To Me</h1>
          <div className="header-actions">
            <span
              className={`status-badge ${connected ? "online" : "offline"}`}
            >
              {statusText}
            </span>
          </div>
        </header>
        {showLogoutModal && (
          <div className="logout-modal-overlay">
            <div className="logout-modal">
              <h2 className="logout-modal-title">Log out?</h2>

              <p className="logout-modal-text">
                Are you sure you want to log out from your account?
              </p>

              <div className="logout-modal-actions">
                <button
                  type="button"
                  className="logout-cancel-btn"
                  onClick={() => setShowLogoutModal(false)}
                >
                  Cancel
                </button>

                <button
                  type="button"
                  className="logout-confirm-btn"
                  onClick={() => {
                    setShowLogoutModal(false);
                    logout();
                  }}
                >
                  Log out
                </button>
              </div>
            </div>
          </div>
        )}
        <ChatWindow
          messages={activeChat?.messages || []}
          isStreaming={isStreaming}
          onEditSave={handleEditSave}
        />

        <InputBox
          onSend={handleSend}
          onStop={stopResponse}
          disabled={!connected || !activeChat}
          isStreaming={isStreaming}
        />
      </div>
    </div>
  );
}
