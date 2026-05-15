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
import "./App.css";

function buildPriorDtos(messages) {
  return messages
    .filter((m) => m && (m.role === "user" || m.role === "assistant"))
    .filter((m) => !(m.role === "assistant" && m.streaming))
    .map((m) => ({ role: m.role, content: m.content ?? "" }));
}

export default function ChatApp() {
  const { token, username, logout } = useAuth();

  const [chats, setChats] = useState([
    {
      id: 1,
      title: "New Chat",
      messages: [],
    },
  ]);

  const [activeChatId, setActiveChatId] = useState(1);

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

  const setStreamingState = useCallback((value) => {
    isStreamingRef.current = value;
    setIsStreaming(value);
  }, []);

  const activeChat = chats.find((c) => c.id === activeChatId);
  const userInitial = username?.charAt(0)?.toUpperCase() || "?";
  // const firstName = username?.split(" ")[0] || "";

  const finalizeStream = useCallback(() => {
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
  }, [setStreamingState]);

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
      clientStreamId,
      messageId: assistantMessageId,
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

  function createNewChat() {
    const newChat = {
      id: Date.now(),
      title: "New Chat",
      messages: [],
    };

    setChats((prev) => [newChat, ...prev]);
    setActiveChatId(newChat.id);
  }

  function deleteChat(chatId) {
    setChats((prev) => prev.filter((c) => c.id !== chatId));

    if (chatId === activeChatId) {
      setActiveChatId(null);
    }
  }

  function renameChat(chatId) {
    const newName = prompt("Enter new name");
    if (!newName) return;

    setChats((prev) =>
      prev.map((c) => (c.id === chatId ? { ...c, title: newName } : c))
    );
  }

  return (
    <div className="app-layout">
      <div className="sidebar">
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
              <span onClick={() => setActiveChatId(chat.id)}>{chat.title}</span>

              <div className="chat-actions">
                <button type="button" onClick={() => renameChat(chat.id)}>
                  ✏️
                </button>
                <button type="button" onClick={() => deleteChat(chat.id)}>
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
            {/* <div className="profile-circle">{userInitial}</div>

            <span className="profile-name">{firstName}</span> */}

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
          {/* <div className="header-actions">
            <span className="header-user" title={username || ""}>
              {username ? `Signed in as ${username}` : ""}
            </span>
            <span
              className={`status-badge ${connected ? "online" : "offline"}`}
            >
              {statusText}
            </span>
            {/* <button type="button" className="logout-btn" onClick={() => logout()}>
              Log out
            </button>*/}
          {/* <button
              type="button"
              className="logout-btn"
              onClick={() => {
                const confirmed = window.confirm(
                  "Are you sure you want to log out?"
                );

                if (confirmed) {
                  logout();
                }
              }}
            >
              Log out
            </button> */}
          {/* <button
              type="button"
              className="logout-btn"
              onClick={() => setShowLogoutModal(true)}
            >
              Log out
            </button>
          </div> */}
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
          disabled={!connected}
          isStreaming={isStreaming}
        />
      </div>
    </div>
  );
}
