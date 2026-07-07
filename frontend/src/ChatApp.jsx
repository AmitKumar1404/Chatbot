import { useState, useEffect, useRef, useCallback } from "react";
import ChatWindow from "./components/ChatWindow";
import InputBox from "./components/InputBox";
import SearchBar from "./components/SearchBar";
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
  fetchActiveStream,
  deleteChatSessionApi,
  updateChatSessionTitleApi,
  updateChatSessionPinnedApi,
  searchChatsApi,
} from "./chatApi";
import "./App.css";
import { useNetworkStatus } from "./hooks/useNetworkStatus";
// import { PanelLeftClose, PanelLeftOpen, Menu } from "lucide-react";
import {
  PanelLeftClose,
  PanelLeftOpen,
  Menu,
  Search,
  MoreHorizontal,
  Pencil,
  Pin,
  PinOff,
  Trash2,
} from "lucide-react";

const SIDEBAR_DESKTOP_STORAGE_KEY = "chatbot.sidebar.desktop.collapsed";
const STREAM_OWNERSHIP_STORAGE_KEY = "chatbot.stream.ownership";
const DESKTOP_MEDIA_QUERY = "(min-width: 768px)";

function getInitialDesktopSidebarCollapsed() {
  if (typeof window === "undefined") return false;
  try {
    return window.localStorage.getItem(SIDEBAR_DESKTOP_STORAGE_KEY) === "1";
  } catch {
    return false;
  }
}

function getInitialIsDesktopViewport() {
  if (typeof window === "undefined") return true;
  return window.matchMedia(DESKTOP_MEDIA_QUERY).matches;
}

function readStreamOwnership() {
  if (typeof window === "undefined") return null;
  try {
    const raw = window.sessionStorage.getItem(STREAM_OWNERSHIP_STORAGE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    if (!parsed || typeof parsed !== "object") return null;
    if (!parsed.clientStreamId || !parsed.assistantMessageId) return null;
    return parsed;
  } catch {
    return null;
  }
}

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

function buildPriorDtos(messages) {
  return messages
    .filter((m) => m && (m.role === "user" || m.role === "assistant"))
    .filter((m) => !(m.role === "assistant" && m.streaming))
    .map((m) => ({ role: m.role, content: m.content ?? "" }));
}

function mapDbMessageToUi(m, options = {}) {
  const uid = m.userBubbleClientId || `legacy-u-${m.id}`;
  const aid = m.assistantBubbleClientId || `legacy-a-${m.id}`;
  const allowIncompleteAssistant = options?.allowIncompleteAssistant !== false;
  const assistantStreaming =
    m.generationComplete === false && allowIncompleteAssistant;
  const userMessage = {
    id: uid,
    role: "user",
    content: m.userMessage ?? "",
    responseTo: null,
    editing: false,
    streaming: false,
  };

  if (m.generationComplete === false && !allowIncompleteAssistant) {
    return [userMessage];
  }

  return [
    {
      id: uid,
      role: "user",
      content: m.userMessage ?? "",
      responseTo: null,
      editing: false,
      streaming: false,
      sourceMessageId: m.id,
    },
    {
      id: aid,
      role: "assistant",
      content: m.aiResponse ?? "",
      responseTo: uid,
      editing: false,
      streaming: assistantStreaming,
      sourceMessageId: m.id,
      feedbackType: m.feedbackType ?? null,
    },
  ];
}

function shouldAllowIncompleteAssistant({
  row,
  chatId,
  ownedStream,
  username,
}) {
  if (row?.generationComplete !== false) return false;
  if (!ownedStream?.assistantMessageId) return false;
  if (!row?.assistantBubbleClientId) return false;
  if (ownedStream.username && username && ownedStream.username !== username) {
    return false;
  }
  if (ownedStream.assistantMessageId !== row.assistantBubbleClientId) {
    return false;
  }
  if (ownedStream.sessionId != null && chatId != null) {
    return Number(ownedStream.sessionId) === Number(chatId);
  }
  return true;
}

function mapDbRowsToUi(rows, { chatId, ownedStream, username }) {
  return rows.flatMap((row) =>
    mapDbMessageToUi(row, {
      allowIncompleteAssistant: shouldAllowIncompleteAssistant({
        row,
        chatId,
        ownedStream,
        username,
      }),
    })
  );
}

function hasSuppressedIncompleteAssistant(
  rows,
  { chatId, ownedStream, username }
) {
  return rows.some(
    (row) =>
      row?.generationComplete === false &&
      !shouldAllowIncompleteAssistant({
        row,
        chatId,
        ownedStream,
        username,
      })
  );
}

function chooseRecoveredAssistantContent(
  localContent,
  dbContent,
  preferLocalWhileRestarting
) {
  if (!localContent) return dbContent;
  if (!dbContent) return localContent;

  if (preferLocalWhileRestarting) {
    return localContent;
  }

  // Keep whichever side is ahead only when one is a clean prefix of the other.
  // If they diverge, trust DB as authoritative to avoid mixed/corrupted UI text.
  if (dbContent.startsWith(localContent)) return dbContent;
  if (localContent.startsWith(dbContent)) return localContent;
  return dbContent;
}

function mergeDbMessagesWithActiveStream(
  localMessages,
  dbRows,
  assistantId,
  options = {}
) {
  const dbUi = mapDbRowsToUi(dbRows, {
    chatId: options.chatId,
    ownedStream: options.ownedStream,
    username: options.username,
  });
  if (!assistantId) return dbUi;
  const preferLocalWhileRestarting =
    options.preferLocalWhileRestarting === true;
  const allowIncompleteAssistant = options.allowIncompleteAssistant === true;

  return dbUi.map((m) => {
    if (m.id !== assistantId || m.role !== "assistant") return m;
    const local = localMessages.find((x) => x.id === assistantId);
    const localContent = local?.content ?? "";
    const dbContent = m.content ?? "";
    const merged = chooseRecoveredAssistantContent(
      localContent,
      dbContent,
      preferLocalWhileRestarting
    );
    return {
      ...m,
      content: merged,
      streaming: allowIncompleteAssistant && m.streaming,
    };
  });
}

function sanitizeAssistantChunkForUi(chunk, currentContent) {
  const raw = typeof chunk === "string" ? chunk : String(chunk ?? "");
  if (!raw) return "";
  if ((currentContent ?? "").trim().length > 0) return raw;
  return raw.replace(/^\s*(assistant|user|system)\s*:\s*/i, "");
}

export default function ChatApp() {
  const { token, username, logout } = useAuth();

  const [chats, setChats] = useState([]);
  const [activeChatId, setActiveChatId] = useState(null);

  const [connected, setConnected] = useState(false);
  const [statusText, setStatusText] = useState("Connecting…");
  const { isOnline: isBrowserOnline, isOffline: isBrowserOffline } =
    useNetworkStatus();

  const [isStreaming, setIsStreaming] = useState(false);
  const [isRecovering, setIsRecovering] = useState(false);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [openChatMenuId, setOpenChatMenuId] = useState(null);
  const [showCollapsedSearchDialog, setShowCollapsedSearchDialog] =
    useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);
  const [searchedQuery, setSearchedQuery] = useState("");
  const [isSearching, setIsSearching] = useState(false);
  const [openedSearchMatch, setOpenedSearchMatch] = useState(null);

  const [isDesktopViewport, setIsDesktopViewport] = useState(
    getInitialIsDesktopViewport
  );
  const [isDesktopSidebarCollapsed, setIsDesktopSidebarCollapsed] = useState(
    getInitialDesktopSidebarCollapsed
  );
  const [isMobileSidebarOpen, setIsMobileSidebarOpen] = useState(false);

  const stopRef = useRef(false);
  const recoveryPollRef = useRef(null);
  const recoveryPollAbortRef = useRef(null);
  const recoveryAbortRef = useRef(null);
  const recoveryRunIdRef = useRef(0);
  const recoveryInFlightRef = useRef(false);
  const connectedRef = useRef(false);
  const latestWsConnectionIdRef = useRef(null);
  const isStreamingRef = useRef(false);
  const activeChatIdRef = useRef(activeChatId);
  const chatsRef = useRef(chats);
  const streamChatIdRef = useRef(null);
  const activeClientStreamIdRef = useRef(null);
  const streamAssistantMessageIdRef = useRef(null);
  const streamTypeRef = useRef("NEW");
  const streamUserMessageIdRef = useRef(null);
  const streamEditTargetRef = useRef(null);
  const resumeAttemptedForStreamIdRef = useRef(null);
  const streamReplayPrefixRef = useRef(null);
  const streamReplayCursorRef = useRef(0);
  const activeStreamBootstrapAttemptedRef = useRef(false);

  useEffect(() => {
    activeChatIdRef.current = activeChatId;
  }, [activeChatId]);

  useEffect(() => {
    chatsRef.current = chats;
  }, [chats]);

  useEffect(() => {
    if (typeof window === "undefined") return undefined;

    const media = window.matchMedia(DESKTOP_MEDIA_QUERY);
    const updateViewport = (event) => {
      setIsDesktopViewport(event.matches);
      if (event.matches) {
        setIsMobileSidebarOpen(false);
      }
    };

    updateViewport(media);

    if (media.addEventListener) {
      media.addEventListener("change", updateViewport);
      return () => media.removeEventListener("change", updateViewport);
    }

    media.addListener(updateViewport);
    return () => media.removeListener(updateViewport);
  }, []);

  useEffect(() => {
    if (typeof window === "undefined") return;
    try {
      window.localStorage.setItem(
        SIDEBAR_DESKTOP_STORAGE_KEY,
        isDesktopSidebarCollapsed ? "1" : "0"
      );
    } catch {
      // Ignore write failures in private browsing environments.
    }
  }, [isDesktopSidebarCollapsed]);

  const isSidebarCollapsed = isDesktopViewport && isDesktopSidebarCollapsed;
  const isMobileDrawerOpen = !isDesktopViewport && isMobileSidebarOpen;

  useEffect(() => {
    if (!isMobileDrawerOpen) return undefined;
    const onKeyDown = (event) => {
      if (event.key === "Escape") {
        setIsMobileSidebarOpen(false);
      }
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [isMobileDrawerOpen]);

  useEffect(() => {
    if (!isMobileDrawerOpen) return undefined;
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = previousOverflow;
    };
  }, [isMobileDrawerOpen]);

  useEffect(() => {
    if (!showCollapsedSearchDialog) return undefined;
    const onKeyDown = (event) => {
      if (event.key === "Escape") {
        setShowCollapsedSearchDialog(false);
      }
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [showCollapsedSearchDialog]);

  useEffect(() => {
    if (isSidebarCollapsed) {
      setShowProfileMenu(false);
    }
  }, [isSidebarCollapsed]);

  useEffect(() => {
    if (!isSidebarCollapsed) {
      setShowCollapsedSearchDialog(false);
    }
  }, [isSidebarCollapsed]);

  useEffect(() => {
    function handleOutsideClick(event) {
      if (
        event.target instanceof Element &&
        event.target.closest(".chat-actions")
      ) {
        return;
      }
      if (
        event.target instanceof Element &&
        event.target.closest(".chat-dropdown-menu")
      ) {
        return;
      }
      if (openChatMenuId !== null) {
        setOpenChatMenuId(null);
      }
    }

    document.addEventListener("mousedown", handleOutsideClick);

    return () => {
      document.removeEventListener("mousedown", handleOutsideClick);
    };
  }, [openChatMenuId]);

  const loadSessions = useCallback(async () => {
    if (!token) return;
    try {
      const rows = await fetchChatSessions(token);
      const mapped = rows.map((s) => ({
        id: s.id,
        title: s.title || "New chat",
        pinned: Boolean(s.pinned),
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
          pinned: Boolean(newSession.pinned),
          messages: [],
          messagesLoaded: true,
        };

        setChats([newChat]);
        setActiveChatId(newChat.id);
        return;
      }

      setChats(mapped);

      // ✅ LOGIN ke baad NEW blank screen open hogi
      setActiveChatId(null);
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

  const setConnectedState = useCallback((value) => {
    connectedRef.current = value;
    setConnected(value);
  }, []);

  const abortRecoveryRequests = useCallback(() => {
    if (recoveryAbortRef.current) {
      recoveryAbortRef.current.abort();
      recoveryAbortRef.current = null;
    }
  }, []);

  const setOwnedStream = useCallback(
    ({ clientStreamId, assistantMessageId, sessionId }) => {
      if (typeof window === "undefined") return;
      if (!clientStreamId || !assistantMessageId) return;
      try {
        window.sessionStorage.setItem(
          STREAM_OWNERSHIP_STORAGE_KEY,
          JSON.stringify({
            username,
            clientStreamId,
            assistantMessageId,
            sessionId: sessionId ?? null,
          })
        );
      } catch {
        // Ignore private mode/sessionStorage write failures.
      }
    },
    [username]
  );

  const clearOwnedStream = useCallback(() => {
    if (typeof window === "undefined") return;
    try {
      window.sessionStorage.removeItem(STREAM_OWNERSHIP_STORAGE_KEY);
    } catch {
      // Ignore private mode/sessionStorage write failures.
    }
  }, []);

  const isActiveStreamOwnedByThisTab = useCallback(
    (active) => {
      if (!active?.clientStreamId || !active?.assistantMessageId) return false;
      const owned = readStreamOwnership();
      if (!owned) return false;
      if (owned.username && username && owned.username !== username)
        return false;
      if (owned.clientStreamId !== active.clientStreamId) return false;
      if (owned.assistantMessageId !== active.assistantMessageId) return false;
      if (owned.sessionId != null && active.sessionId != null) {
        return Number(owned.sessionId) === Number(active.sessionId);
      }
      return true;
    },
    [username]
  );

  const clearRecoveryPoll = useCallback(() => {
    if (recoveryPollRef.current != null) {
      clearInterval(recoveryPollRef.current);
      recoveryPollRef.current = null;
    }
    if (recoveryPollAbortRef.current) {
      recoveryPollAbortRef.current.abort();
      recoveryPollAbortRef.current = null;
    }
  }, []);

  const markDisconnected = useCallback(
    (reason) => {
      setConnectedState(false);
      setIsRecovering(false);
      setStatusText("Disconnected");
      clearRecoveryPoll();
      abortRecoveryRequests();
      console.warn("[Stream] Connection lost", {
        reason,
        isStreaming: isStreamingRef.current,
        activeClientStreamId: activeClientStreamIdRef.current,
      });
      if (isStreamingRef.current && activeClientStreamIdRef.current) {
        console.info("[Stream] Stream paused due to disconnect", {
          reason,
          clientStreamId: activeClientStreamIdRef.current,
        });
      }
    },
    [abortRecoveryRequests, clearRecoveryPoll, setConnectedState]
  );

  const startReplayDedup = useCallback((chatId, assistantId) => {
    const chat = chatsRef.current.find((c) => c.id === chatId);
    const assistant = chat?.messages?.find(
      (m) => m.id === assistantId && m.role === "assistant"
    );
    const prefix = assistant?.content ?? "";
    streamReplayPrefixRef.current = prefix.length > 0 ? prefix : null;
    streamReplayCursorRef.current = 0;
  }, []);

  useEffect(() => () => clearRecoveryPoll(), [clearRecoveryPoll]);

  const activeChat = chats.find((c) => c.id === activeChatId);
  const userInitial = username?.charAt(0)?.toUpperCase() || "?";
  const isSearchView =
    searchQuery.trim().length > 0 &&
    hasSearched &&
    searchQuery.trim() === searchedQuery;

  useEffect(() => {
    if (!openedSearchMatch) return;
    if (activeChatId == null || activeChatId !== openedSearchMatch.sessionId) {
      setOpenedSearchMatch(null);
    }
  }, [activeChatId, openedSearchMatch]);

  const selectChat = useCallback(
    async (chatId, options = {}) => {
      const { preserveSearchMatch = false } = options;
      if (!preserveSearchMatch) {
        setOpenedSearchMatch(null);
      }
      setActiveChatId(chatId);
      if (!isDesktopViewport) {
        setIsMobileSidebarOpen(false);
      }
      if (!token) return;
      const snap = chatsRef.current.find((c) => c.id === chatId);
      if (!snap || snap.messagesLoaded) return;
      try {
        const rows = await fetchSessionMessages(token, chatId);
        const ownedStream = readStreamOwnership();
        const messages = mapDbRowsToUi(rows, {
          chatId,
          ownedStream,
          username,
        });
        const containsHiddenIncomplete = hasSuppressedIncompleteAssistant(
          rows,
          {
            chatId,
            ownedStream,
            username,
          }
        );
        setChats((prev) =>
          prev.map((c) =>
            c.id === chatId
              ? {
                  ...c,
                  messages,
                  // Keep this chat "not fully loaded" while another tab owns an incomplete assistant row.
                  // This allows a future select to refetch and pick up the completed DB response.
                  messagesLoaded: !containsHiddenIncomplete,
                }
              : c
          )
        );
      } catch (e) {
        console.error(e);
      }
    },
    [isDesktopViewport, token, username]
  );

  const handleSearch = useCallback(async () => {
    setOpenedSearchMatch(null);
    const trimmed = searchQuery.trim();
    if (!trimmed || !token) {
      setHasSearched(false);
      setSearchedQuery("");
      setSearchResults([]);
      return;
    }

    setIsSearching(true);
    try {
      const rows = await searchChatsApi(token, trimmed);
      setSearchResults(Array.isArray(rows) ? rows : []);
      setSearchedQuery(trimmed);
      setHasSearched(true);
    } catch (error) {
      console.error(error);
      setSearchResults([]);
      setSearchedQuery(trimmed);
      setHasSearched(true);
    } finally {
      setIsSearching(false);
    }
  }, [searchQuery, token]);
  function handleClearSearch() {
    setSearchQuery("");
    setSearchResults([]);
    setHasSearched(false);
    setSearchedQuery("");
    setOpenedSearchMatch(null);
  }
  function handleSearchQueryChange(value) {
    setSearchQuery(value);
    if (!value.trim()) {
      setHasSearched(false);
      setSearchedQuery("");
      setSearchResults([]);
      return;
    }

    if (value.trim() !== searchedQuery) {
      setHasSearched(false);
    }
  }

  async function handleSearchResultClick(result) {
    const keyword = (searchedQuery || searchQuery).trim();
    setOpenedSearchMatch({
      sessionId: result.sessionId,
      messageId: result.messageId ?? null,
      keyword,
    });
    setOpenChatMenuId(null);
    await selectChat(result.sessionId, { preserveSearchMatch: true });
    setShowCollapsedSearchDialog(false);
  }

  function getSearchResultSnippet(result) {
    const snippet = (result?.content ?? "").trim();
    if (snippet) return snippet;
    if (result?.matchType === "TITLE") return "Title match";
    return result?.matchType ?? "Match";
  }

  function renderSearchSnippetWithHighlight(snippet) {
    const keyword = searchQuery.trim();
    if (!keyword || !snippet) return snippet;

    const pattern = new RegExp(`(${escapeRegExp(keyword)})`, "ig");
    return snippet.split(pattern).map((part, index) => {
      if (part.toLowerCase() === keyword.toLowerCase()) {
        return (
          <mark key={`match-${index}`} className="chat-search-highlight">
            {part}
          </mark>
        );
      }

      return (
        <span key={`text-${index}`} className="chat-search-snippet-part">
          {part}
        </span>
      );
    });
  }

  const finalizeStream = useCallback(() => {
    recoveryRunIdRef.current += 1;
    clearRecoveryPoll();
    abortRecoveryRequests();
    setIsRecovering(false);
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
    streamTypeRef.current = "NEW";
    streamUserMessageIdRef.current = null;
    streamEditTargetRef.current = null;
    resumeAttemptedForStreamIdRef.current = null;
    streamReplayPrefixRef.current = null;
    streamReplayCursorRef.current = 0;
    stopRef.current = false;
    streamChatIdRef.current = null;
    clearOwnedStream();
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
  }, [clearOwnedStream, clearRecoveryPoll, setStreamingState, token]);

  const syncMessagesFromServer = useCallback(
    async (chatId, assistantId, options = {}) => {
      const rows = await fetchSessionMessages(token, chatId, options);
      const chat = chatsRef.current.find((c) => c.id === chatId);
      const localMessages = chat?.messages ?? [];
      const ownedStream = readStreamOwnership();
      const allowIncompleteAssistant = rows.some((row) =>
        shouldAllowIncompleteAssistant({
          row,
          chatId,
          ownedStream,
          username,
        })
      );
      const preferLocalWhileRestarting =
        Boolean(resumeAttemptedForStreamIdRef.current) &&
        assistantId === streamAssistantMessageIdRef.current;
      const messages = mergeDbMessagesWithActiveStream(
        localMessages,
        rows,
        assistantId,
        {
          preferLocalWhileRestarting,
          allowIncompleteAssistant,
          chatId,
          ownedStream,
          username,
        }
      );
      setChats((prev) =>
        prev.map((c) =>
          c.id === chatId ? { ...c, messages, messagesLoaded: true } : c
        )
      );
      const row = rows.find((r) => r.assistantBubbleClientId === assistantId);
      return row == null || row.generationComplete !== false;
    },
    [token, username]
  );

  const buildStreamResumePayload = useCallback(
    (chatId, assistantId, clientStreamId) => {
      const chat = chatsRef.current.find((c) => c.id === chatId);
      const msgs = chat?.messages ?? [];
      const assistant = msgs.find(
        (m) => m.id === assistantId && m.role === "assistant"
      );
      if (!assistant?.responseTo) return null;

      const user = msgs.find(
        (m) => m.id === assistant.responseTo && m.role === "user"
      );
      if (!user) return null;

      const userIdx = msgs.findIndex((m) => m.id === user.id);
      const priorMessages = buildPriorDtos(msgs.slice(0, userIdx));
      const type = streamTypeRef.current ?? "NEW";
      const payload = {
        type,
        sessionId: chatId,
        clientStreamId,
        messageId: assistantId,
        content: user.content ?? "",
        priorMessages,
      };

      if (type === "EDIT") {
        payload.editTargetMessageId = streamEditTargetRef.current ?? user.id;
      } else {
        payload.userMessageId = streamUserMessageIdRef.current ?? user.id;
      }

      return payload;
    },
    []
  );

  const restartInterruptedGeneration = useCallback(
    (chatId, assistantId, clientStreamId) => {
      if (stopRef.current || !isStreamingRef.current) return false;
      if (resumeAttemptedForStreamIdRef.current === clientStreamId) {
        return false;
      }

      const payload = buildStreamResumePayload(
        chatId,
        assistantId,
        clientStreamId
      );
      if (!payload?.content?.trim()) return false;

      resumeAttemptedForStreamIdRef.current = clientStreamId;
      startReplayDedup(chatId, assistantId);
      console.info(
        "[Stream] Active stream missing after reconnect, restarting generation",
        {
          chatId,
          assistantId,
          clientStreamId,
        }
      );

      sendMessage(payload);
      return true;
    },
    [buildStreamResumePayload, startReplayDedup]
  );

  const recoverInterruptedStream = useCallback(
    async (connectionId) => {
      if (!token || !isStreamingRef.current || stopRef.current) return;
      if (recoveryInFlightRef.current) return;
      recoveryInFlightRef.current = true;
      const recoveryRunId = ++recoveryRunIdRef.current;
      abortRecoveryRequests();
      const recoveryAbort = new AbortController();
      recoveryAbortRef.current = recoveryAbort;

      setIsRecovering(true);
      setStatusText("Resuming…");
      console.info("[Stream] Recovery attempt started", {
        recoveryRunId,
        connectionId,
        clientStreamId: activeClientStreamIdRef.current,
      });

      try {
        let assistantId = streamAssistantMessageIdRef.current;
        let clientStreamId = activeClientStreamIdRef.current;

        const active = await fetchActiveStream(token, {
          signal: recoveryAbort.signal,
        });
        if (latestWsConnectionIdRef.current !== connectionId) return;

        if (active) {
          clientStreamId = active.clientStreamId;
          assistantId = active.assistantMessageId;
          activeClientStreamIdRef.current = clientStreamId;
          streamAssistantMessageIdRef.current = assistantId;
          if (active.sessionId != null) {
            streamChatIdRef.current = active.sessionId;
          }
        }

        const targetChatId = streamChatIdRef.current ?? activeChatIdRef.current;
        if (targetChatId == null || !assistantId || !clientStreamId) return;

        const alreadyComplete = await syncMessagesFromServer(
          targetChatId,
          assistantId,
          { signal: recoveryAbort.signal }
        );
        if (latestWsConnectionIdRef.current !== connectionId) return;

        if (alreadyComplete) {
          console.info("[Stream] Recovery found stream already completed", {
            recoveryRunId,
            clientStreamId,
          });
          finalizeStream();
          return;
        }

        if (!active) {
          const restarted = restartInterruptedGeneration(
            targetChatId,
            assistantId,
            clientStreamId
          );
          if (restarted) {
            console.info("[Stream] Recovery restart dispatched", {
              recoveryRunId,
              clientStreamId,
            });
          }
        } else {
          streamReplayPrefixRef.current = null;
          streamReplayCursorRef.current = 0;
          console.info("[Stream] Recovery bound to backend active stream", {
            recoveryRunId,
            clientStreamId,
          });
        }

        clearRecoveryPoll();
        recoveryPollRef.current = setInterval(async () => {
          if (recoveryRunIdRef.current !== recoveryRunId) {
            clearRecoveryPoll();
            return;
          }
          if (!isStreamingRef.current || stopRef.current) {
            clearRecoveryPoll();
            return;
          }
          if (latestWsConnectionIdRef.current !== connectionId) {
            clearRecoveryPoll();
            return;
          }
          try {
            const tid = streamChatIdRef.current ?? activeChatIdRef.current;
            const aid = streamAssistantMessageIdRef.current;
            if (tid == null || !aid) return;
            if (recoveryPollAbortRef.current) {
              recoveryPollAbortRef.current.abort();
            }
            const pollAbort = new AbortController();
            recoveryPollAbortRef.current = pollAbort;
            const complete = await syncMessagesFromServer(tid, aid, {
              signal: pollAbort.signal,
            });
            if (complete) {
              clearRecoveryPoll();
              finalizeStream();
            }
          } catch (e) {
            if (e?.name === "AbortError") return;
            console.error("[Stream] Recovery polling failed", e);
          }
        }, 1500);
      } catch (e) {
        if (e?.name === "AbortError") {
          console.debug("[Stream] Recovery request aborted");
          return;
        }
        console.error("[Stream] Recovery failed", e);
      } finally {
        recoveryInFlightRef.current = false;
        if (recoveryAbortRef.current === recoveryAbort) {
          recoveryAbortRef.current = null;
        }
        if (
          recoveryRunIdRef.current === recoveryRunId &&
          latestWsConnectionIdRef.current === connectionId
        ) {
          setIsRecovering(false);
        }
      }
    },
    [
      abortRecoveryRequests,
      clearRecoveryPoll,
      finalizeStream,
      restartInterruptedGeneration,
      syncMessagesFromServer,
      token,
    ]
  );

  const appendAssistantChunk = useCallback((chunk) => {
    if (stopRef.current) return;
    if (!isStreamingRef.current) return;
    if (!connectedRef.current) return;

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
        const safeChunk = sanitizeAssistantChunkForUi(chunk, row.content ?? "");
        if (!safeChunk) {
          return {
            ...chat,
            messages: chat.messages.map((m, i) =>
              i === idx ? { ...m, streaming: true } : m
            ),
          };
        }
        return {
          ...chat,
          messages: chat.messages.map((m, i) =>
            i === idx
              ? {
                  ...m,
                  content: (row.content ?? "") + safeChunk,
                  streaming: true,
                }
              : m
          ),
        };
      })
    );
  }, []);

  useEffect(() => {
    if (!token || !connected) return;
    if (isStreamingRef.current) return;
    if (activeStreamBootstrapAttemptedRef.current) return;
    if (chats.length === 0) return;

    activeStreamBootstrapAttemptedRef.current = true;
    let cancelled = false;

    (async () => {
      try {
        const active = await fetchActiveStream(token);
        if (cancelled) return;
        if (!active?.clientStreamId || !active?.assistantMessageId) return;
        if (!isActiveStreamOwnedByThisTab(active)) return;

        stopRef.current = false;
        activeClientStreamIdRef.current = active.clientStreamId;
        streamAssistantMessageIdRef.current = active.assistantMessageId;
        if (active.sessionId != null) {
          streamChatIdRef.current = active.sessionId;
          setActiveChatId((prev) => (prev == null ? active.sessionId : prev));
        }
        streamTypeRef.current = "NEW";
        streamUserMessageIdRef.current = null;
        streamEditTargetRef.current = null;
        resumeAttemptedForStreamIdRef.current = null;
        setStreamingState(true);

        recoverInterruptedStream();
      } catch (e) {
        console.error(e);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [
    chats.length,
    connected,
    isActiveStreamOwnedByThisTab,
    recoverInterruptedStream,
    setStreamingState,
    token,
  ]);

  const handleStreamBody = useCallback(
    (raw, wsMeta) => {
      if (
        wsMeta?.connectionId != null &&
        wsMeta.connectionId !== latestWsConnectionIdRef.current
      ) {
        return;
      }
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
          setIsRecovering(false);
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
      latestWsConnectionIdRef.current = null;
      setConnectedState(false);
      setStatusText("Not signed in");
      clearRecoveryPoll();
      abortRecoveryRequests();
      return;
    }

    setConnectedState(false);
    setStatusText("Connecting…");
    const connectionId = connectWebSocket({
      accessToken: token,
      onMessage: handleStreamBody,
      onConnect: (meta) => {
        setConnectedState(true);
        activeStreamBootstrapAttemptedRef.current = false;
        if (isStreamingRef.current && activeClientStreamIdRef.current) {
          console.info("[Stream] Reconnected, attempting stream recovery", {
            connectionId: meta?.connectionId,
            clientStreamId: activeClientStreamIdRef.current,
          });
          recoverInterruptedStream(meta?.connectionId);
        } else {
          setStatusText("Connected");
        }
      },
      onError: (meta) => {
        setConnected(false);
        activeStreamBootstrapAttemptedRef.current = false;
        if (isStreamingRef.current) {
          setStatusText("Reconnecting…");
        } else {
          setStatusText("Disconnected");
        }
        markDisconnected(meta?.reason ?? "unknown");
      },
    });
    latestWsConnectionIdRef.current = connectionId;
    console.info("[WS] Connection attempt started", { connectionId });

    return () => {
      latestWsConnectionIdRef.current = null;
      clearRecoveryPoll();
      abortRecoveryRequests();
      disconnectWebSocket();
    };
  }, [
    abortRecoveryRequests,
    clearRecoveryPoll,
    handleStreamBody,
    markDisconnected,
    recoverInterruptedStream,
    setConnectedState,
    token,
  ]);

  useEffect(() => {
    if (!token) return undefined;

    const cleanupSocketOnPageExit = () => {
      disconnectWebSocket();
    };

    window.addEventListener("beforeunload", cleanupSocketOnPageExit);
    window.addEventListener("pagehide", cleanupSocketOnPageExit);

    return () => {
      window.removeEventListener("beforeunload", cleanupSocketOnPageExit);
      window.removeEventListener("pagehide", cleanupSocketOnPageExit);
    };
  }, [token]);

  const displayStatusText = !token
    ? "Not signed in"
    : isRecovering
    ? "Resuming…"
    : isBrowserOffline
    ? "No internet"
    : connected
    ? "Connected"
    : statusText;

  function handleSend(text) {
    if (isStreamingRef.current || !connected || isBrowserOffline) {
      return;
    }

    const currentChatId = activeChatIdRef.current;

    const currentChat = chatsRef.current.find((c) => c.id === currentChatId);

    // ✅ Draft chat ko real DB chat me convert karo
    if (!currentChatId || currentChat?.isDraft) {
      createRealChatAndSend(text);
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
    streamTypeRef.current = "NEW";
    streamUserMessageIdRef.current = userMessageId;
    streamEditTargetRef.current = null;
    resumeAttemptedForStreamIdRef.current = null;
    setOwnedStream({
      clientStreamId,
      assistantMessageId,
      sessionId: chatId,
    });
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
      if (isStreamingRef.current || !connected || isBrowserOffline)
        return false;

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
      streamTypeRef.current = "EDIT";
      streamUserMessageIdRef.current = userMessageId;
      streamEditTargetRef.current = userMessageId;
      resumeAttemptedForStreamIdRef.current = null;
      setOwnedStream({
        clientStreamId,
        assistantMessageId,
        sessionId: chatId,
      });
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
    [connected, isBrowserOffline, setOwnedStream, setStreamingState]
  );

  function stopResponse() {
    if (!isStreamingRef.current) return;
    stopRef.current = true;
    sendStopSignal();
    finalizeStream();
  }

  function createNewChat() {
    if (isStreamingRef.current) return;
    if (!isDesktopViewport) {
      setIsMobileSidebarOpen(false);
    }

    // ✅ already blank chat open hai
    const existingBlankChat = chatsRef.current.find(
      (c) => c.isDraft && c.messages.length === 0
    );

    if (existingBlankChat) {
      setActiveChatId(existingBlankChat.id);
      activeChatIdRef.current = existingBlankChat.id;
      return;
    }

    // ✅ frontend-only temporary blank chat
    const tempId = `draft-${Date.now()}`;

    const draftChat = {
      id: tempId,
      title: "New chat",
      messages: [],
      messagesLoaded: true,
      isDraft: true,
    };

    setChats((prev) => [draftChat, ...prev]);

    setActiveChatId(tempId);

    activeChatIdRef.current = tempId;
  }

  // function handleSidebarToggle() {
  //   if (isDesktopViewport) {
  //     setIsDesktopSidebarCollapsed((prev) => !prev);
  //     return;
  //   }
  //   setIsMobileSidebarOpen((prev) => !prev);
  // }
  function handleSidebarToggle() {
    setOpenChatMenuId(null);

    if (isDesktopViewport) {
      setIsDesktopSidebarCollapsed((prev) => !prev);
      return;
    }

    setIsMobileSidebarOpen((prev) => !prev);
  }

  function handleCollapsedSidebarSearchClick() {
    setShowCollapsedSearchDialog(true);
    requestAnimationFrame(() => {
      const input = document.querySelector(".sidebar-search-input");
      if (input instanceof HTMLInputElement) {
        input.focus();
      }
    });
  }

  const sidebarToggleAriaLabel = isDesktopViewport
    ? isSidebarCollapsed
      ? "Expand sidebar"
      : "Collapse sidebar"
    : isMobileDrawerOpen
    ? "Close sidebar"
    : "Open sidebar";

  async function createRealChatAndSend(text) {
    if (!token || isStreamingRef.current) return;

    try {
      // current draft id
      const oldDraftId = activeChatIdRef.current;

      // backend me REAL session create
      const s = await createChatSession(token);

      const realChat = {
        id: s.id,
        title: s.title || "New chat",
        pinned: Boolean(s.pinned),
        messages: [],
        messagesLoaded: true,
      };

      // draft remove + real chat add
      setChats((prev) => {
        const withoutDraft = prev.filter((c) => c.id !== oldDraftId);

        return [realChat, ...withoutDraft];
      });

      // active chat update
      setActiveChatId(realChat.id);

      // IMPORTANT
      activeChatIdRef.current = realChat.id;

      // actual message send
      setTimeout(() => {
        handleSend(text);
      }, 0);
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

  async function handleTogglePinned(chatId, pinned) {
    if (!token) return;
    try {
      const updated = await updateChatSessionPinnedApi(token, chatId, pinned);
      setChats((prev) =>
        prev.map((c) =>
          c.id === chatId ? { ...c, pinned: Boolean(updated.pinned) } : c
        )
      );
    } catch (e) {
      console.error(e);
    }
  }

  const pinnedChats = chats.filter((chat) => chat.pinned);
  const unpinnedChats = chats.filter((chat) => !chat.pinned);

  return (
    <div className="app-layout">
      {isMobileDrawerOpen && (
        <button
          type="button"
          className="sidebar-backdrop"
          onClick={() => setIsMobileSidebarOpen(false)}
          aria-label="Close sidebar"
        />
      )}
      <div
        className={`sidebar ${isStreaming ? "sidebar-busy" : ""} ${
          isSidebarCollapsed ? "sidebar-collapsed" : "sidebar-expanded"
        } ${!isDesktopViewport ? "sidebar-mobile" : ""} ${
          isMobileDrawerOpen ? "sidebar-mobile-open" : "sidebar-mobile-closed"
        }`}
      >
        {isDesktopViewport && (
          <button
            type="button"
            className="sidebar-menu-btn sidebar-desktop-toggle-btn"
            onClick={handleSidebarToggle}
            aria-label={sidebarToggleAriaLabel}
          >
            {isSidebarCollapsed ? (
              <PanelLeftOpen size={20} />
            ) : (
              <PanelLeftClose size={20} />
            )}
          </button>
        )}
        <div className="new-chat-tooltip-wrapper">
          <button
            className={`new-chat-btn ${isSidebarCollapsed ? "icon-only" : ""}`}
            onClick={createNewChat}
            aria-label="New Chat"
          >
            {isSidebarCollapsed ? (
              <span aria-hidden="true">＋</span>
            ) : (
              "+ New Chat"
            )}
          </button>

          {isSidebarCollapsed && (
            <div className="sidebar-tooltip">New Chat</div>
          )}
        </div>

        {isSidebarCollapsed && (
          <div className="new-chat-tooltip-wrapper sidebar-search-tooltip-wrapper">
            <button
              className="new-chat-btn icon-only"
              onClick={handleCollapsedSidebarSearchClick}
              aria-label="Search"
            >
              <Search size={20} />
            </button>
            <div className="sidebar-tooltip">Search</div>
          </div>
        )}

        {!isSidebarCollapsed && (
          <>
            <SearchBar
              value={searchQuery}
              onChange={handleSearchQueryChange}
              onSearch={handleSearch}
              onClear={handleClearSearch}
              isLoading={isSearching}
            />
            {isSearching && (
              <div className="sidebar-search-status">Searching...</div>
            )}
          </>
        )}

        {!isSidebarCollapsed && !isSearchView && (
          <div className="chat-list">
            {pinnedChats.length > 0 && (
              <div className="sidebar-search-status">Pinned</div>
            )}
            {pinnedChats.map((chat) => (
              <div
                key={chat.id}
                className={`chat-item ${
                  chat.id === activeChatId ? "active" : ""
                }`}
              >
                <span onClick={() => selectChat(chat.id)}>{chat.title}</span>

                {/* <div className="chat-actions">
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
                </div> */}
                {/* <div className="chat-actions"> */}
                <div className="chat-actions">
                  <button
                    type="button"
                    className="chat-menu-trigger"
                    onClick={(e) => {
                      e.stopPropagation();

                      setOpenChatMenuId((prev) =>
                        prev === chat.id ? null : chat.id
                      );
                    }}
                  >
                    <MoreHorizontal size={18} />
                  </button>

                  {openChatMenuId === chat.id && (
                    <div className="chat-dropdown-menu">
                      {!String(chat.id).startsWith("draft-") && (
                        <button
                          type="button"
                          className="chat-dropdown-item"
                          onClick={(e) => {
                            e.stopPropagation();
                            setOpenChatMenuId(null);
                            handleTogglePinned(chat.id, !chat.pinned);
                          }}
                        >
                          <Pin size={15} />
                          {chat.pinned ? "Unpin" : "Pin"}
                        </button>
                      )}

                      <button
                        type="button"
                        className="chat-dropdown-item"
                        onClick={(e) => {
                          e.stopPropagation();
                          setOpenChatMenuId(null);
                          renameChat(chat.id);
                        }}
                      >
                        <Pencil size={15} />
                        Edit
                      </button>

                      <button
                        type="button"
                        className="chat-dropdown-item delete"
                        onClick={(e) => {
                          e.stopPropagation();
                          setOpenChatMenuId(null);
                          deleteChat(chat.id);
                        }}
                      >
                        <Trash2 size={15} />
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
            {pinnedChats.length > 0 && (
              <div className="sidebar-search-status">Chats</div>
            )}
            {unpinnedChats.map((chat) => (
              <div
                key={chat.id}
                className={`chat-item ${
                  chat.id === activeChatId ? "active" : ""
                }`}
              >
                <span onClick={() => selectChat(chat.id)}>{chat.title}</span>

                {/* <div className="chat-actions">
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
                </div> */}
                {/* <div className="chat-actions"> */}
                <div className="chat-actions">
                  <button
                    type="button"
                    className="chat-menu-trigger"
                    onClick={(e) => {
                      e.stopPropagation();

                      setOpenChatMenuId((prev) =>
                        prev === chat.id ? null : chat.id
                      );
                    }}
                  >
                    <MoreHorizontal size={18} />
                  </button>

                  {openChatMenuId === chat.id && (
                    <div className="chat-dropdown-menu">
                      {!String(chat.id).startsWith("draft-") && (
                        <button
                          type="button"
                          className="chat-dropdown-item"
                          onClick={(e) => {
                            e.stopPropagation();
                            setOpenChatMenuId(null);
                            handleTogglePinned(chat.id, !chat.pinned);
                          }}
                        >
                          <Pin size={15} />
                          {chat.pinned ? "Unpin" : "Pin"}
                        </button>
                      )}

                      <button
                        type="button"
                        className="chat-dropdown-item"
                        onClick={(e) => {
                          e.stopPropagation();
                          setOpenChatMenuId(null);
                          renameChat(chat.id);
                        }}
                      >
                        <Pencil size={15} />
                        Edit
                      </button>

                      <button
                        type="button"
                        className="chat-dropdown-item delete"
                        onClick={(e) => {
                          e.stopPropagation();
                          setOpenChatMenuId(null);
                          deleteChat(chat.id);
                        }}
                      >
                        <Trash2 size={15} />
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {!isSidebarCollapsed && isSearchView && (
          <div className="chat-list search-results-list">
            {searchResults.length === 0 ? (
              <p className="sidebar-search-status">
                No matching chats or messages found.
              </p>
            ) : (
              searchResults.map((result, index) => (
                <button
                  key={`${result.sessionId}-${
                    result.messageId ?? "session"
                  }-${index}`}
                  type="button"
                  className={`chat-item chat-search-item ${
                    result.sessionId === activeChatId ? "active" : ""
                  }`}
                  onClick={() => handleSearchResultClick(result)}
                >
                  <span className="chat-search-title">
                    {result.sessionTitle || "New chat"}
                  </span>
                  <span className="chat-search-snippet">
                    {renderSearchSnippetWithHighlight(
                      getSearchResultSnippet(result)
                    )}
                  </span>
                </button>
              ))
            )}
          </div>
        )}
        <div className="sidebar-footer">
          <div
            className="profile-wrapper"
            onClick={() => setShowProfileMenu((prev) => !prev)}
          >
            <div className="profile-circle">{userInitial}</div>

            {!isSidebarCollapsed && (
              <div className="profile-name">{username?.split(" ")[0]}</div>
            )}

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

      <div
        className={`chat-section ${
          isSidebarCollapsed ? "chat-section-collapsed" : ""
        }`}
      >
        <header className="app-header">
          <div className="header-title-wrap">
            {!isDesktopViewport && (
              <button
                type="button"
                className="sidebar-menu-btn"
                onClick={handleSidebarToggle}
                aria-label={sidebarToggleAriaLabel}
              >
                {isMobileDrawerOpen ? (
                  <PanelLeftClose size={20} />
                ) : (
                  <Menu size={20} />
                )}
              </button>
            )}
            <h1 className="app-title">Talk To Me</h1>
          </div>
          <div className="header-actions">
            <span
              className={`status-badge ${
                connected && isBrowserOnline ? "online" : "offline"
              }`}
              title={
                isBrowserOffline
                  ? "No internet connection detected"
                  : connected
                  ? "WebSocket connected"
                  : "WebSocket disconnected"
              }
            >
              {displayStatusText}
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
        {showCollapsedSearchDialog && (
          <div
            className="collapsed-search-modal-overlay"
            onClick={() => setShowCollapsedSearchDialog(false)}
          >
            <div
              className="collapsed-search-modal"
              role="dialog"
              aria-modal="true"
              aria-label="Search chats"
              onClick={(event) => event.stopPropagation()}
            >
              <SearchBar
                value={searchQuery}
                onChange={handleSearchQueryChange}
                onSearch={handleSearch}
                onClear={handleClearSearch}
                isLoading={isSearching}
              />
              {isSearching && (
                <div className="sidebar-search-status">Searching...</div>
              )}
              {isSearchView && (
                <div className="chat-list search-results-list collapsed-search-results-list">
                  {searchResults.length === 0 ? (
                    <p className="sidebar-search-status">
                      No matching chats or messages found.
                    </p>
                  ) : (
                    searchResults.map((result, index) => (
                      <button
                        key={`${result.sessionId}-${
                          result.messageId ?? "session"
                        }-${index}`}
                        type="button"
                        className={`chat-item chat-search-item ${
                          result.sessionId === activeChatId ? "active" : ""
                        }`}
                        onClick={() => handleSearchResultClick(result)}
                      >
                        <span className="chat-search-title">
                          {result.sessionTitle || "New chat"}
                        </span>
                        <span className="chat-search-snippet">
                          {renderSearchSnippetWithHighlight(
                            getSearchResultSnippet(result)
                          )}
                        </span>
                      </button>
                    ))
                  )}
                </div>
              )}
            </div>
          </div>
        )}
        {isBrowserOffline && (
          <div
            className="network-offline-banner"
            role="status"
            aria-live="polite"
          >
            You are offline. Messages cannot be sent until your connection is
            restored.
          </div>
        )}
        {isRecovering && !isBrowserOffline && (
          <div
            className="network-offline-banner stream-recovery-banner"
            role="status"
            aria-live="polite"
          >
            Connection restored — resuming your response…
          </div>
        )}

        <ChatWindow
          messages={activeChat?.messages || []}
          isStreaming={isStreaming}
          onEditSave={handleEditSave}
          searchMatch={
            openedSearchMatch?.sessionId === activeChatId
              ? openedSearchMatch
              : null
          }
        />

        <InputBox
          onSend={handleSend}
          onStop={stopResponse}
          disabled={!connected || isBrowserOffline}
          isStreaming={isStreaming}
        />
      </div>
    </div>
  );
}
