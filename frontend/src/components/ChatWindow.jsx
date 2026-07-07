import { useEffect, useRef, useState, memo, useCallback, useMemo } from "react";
import { Copy, Check } from "lucide-react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { updateMessageFeedbackApi } from "../chatApi";
import { useAuth } from "../context/AuthContext";
import FeedbackButtons from "./FeedbackButtons";
import FeedbackReasonModal from "./FeedbackReasonModal";

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

const UserBubble = memo(function UserBubble({
  msg,
  isEditing,
  draft,
  onDraftChange,
  isStreaming,
  onBeginEdit,
  onCancelEdit,
  onCommitEdit,
  onCopy, // ✅ ADDED: copy handler
  copiedId,
  renderedContent,
  isSearchMatch,
}) {
  return (
    <div
      className={`chat-bubble user user-bubble-wrap ${
        isSearchMatch ? "search-target-bubble" : ""
      }`}
    >
      {!isEditing && (
        <>
          {/* MESSAGE TEXT */}
          <p className="bubble-text">{renderedContent ?? msg.content}</p>

          <div className="msg-actions-row">
            {/* ✏️ EDIT BUTTON */}
            <button
              type="button"
              className="msg-edit-btn"
              disabled={isStreaming}
              onClick={() => onBeginEdit(msg)}
            >
              <span className="tooltip-text">Edit</span>
              <span className="edit-icon">✎</span>
            </button>
            <button
              type="button"
              className="msg-copy-btn"
              onClick={() => onCopy(msg.content, msg.id)}
            >
              {/* <span className="tooltip-text">Copy</span> */}
              <span className="tooltip-text">
                {copiedId === msg.id ? "Copied" : "Copy"}
              </span>
              {/* <Copy size={16} strokeWidth={2} /> */}
              {copiedId === msg.id && window.innerWidth <= 768 ? (
                <Check
                  className="copied-check-icon"
                  size={16}
                  strokeWidth={2.5}
                />
              ) : (
                <Copy size={16} strokeWidth={2} />
              )}
            </button>
          </div>
        </>
      )}

      {isEditing && (
        <div className="user-edit-panel">
          <textarea
            className="user-edit-textarea"
            value={draft}
            onChange={(e) => onDraftChange(e.target.value)}
            rows={3}
            disabled={isStreaming}
          />
          <div className="user-edit-actions">
            <button
              type="button"
              className="user-edit-save"
              disabled={isStreaming || !draft.trim()}
              onClick={() => onCommitEdit(msg.id)}
            >
              Send
            </button>
            <button
              type="button"
              className="user-edit-cancel"
              disabled={isStreaming}
              onClick={onCancelEdit}
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
});

const AssistantBubble = memo(function AssistantBubble({
  msg,
  onCopy,
  copiedId,
  renderedContent,
  shouldRenderPlainText,
  isSearchMatch,
  selectedFeedback,
  feedbackLoading,
  onFeedback,
}) {
  if (msg.streaming && !(msg.content ?? "").trim()) {
    return (
      <div className="chat-bubble assistant typing">
        <div className="typing-dots" aria-label="Assistant is typing">
          <span />
          <span />
          <span />
        </div>
      </div>
    );
  }

  return (
    <div
      className={`chat-bubble assistant ${
        isSearchMatch ? "search-target-bubble" : ""
      }`}
    >
      {shouldRenderPlainText ? (
        // Keep current text+mark rendering for active search target.
        <p className="bubble-text">{renderedContent ?? msg.content}</p>
      ) : (
        // Render assistant content as Markdown (GFM), while preserving raw text for copy.
        <div className="bubble-text bubble-markdown">
          <ReactMarkdown remarkPlugins={[remarkGfm]}>
            {msg.content ?? ""}
          </ReactMarkdown>
        </div>
      )}

      {/* 📋 COPY BUTTON */}
      {msg.content?.trim() && !msg.streaming && (
        <div className="msg-actions-row">
          <button
            type="button"
            className="msg-copy-btn"
            onClick={() => onCopy(msg.content, msg.id)}
          >
            <span className="tooltip-text">
              {copiedId === msg.id ? "Copied" : "Copy"}
            </span>
            {/* <Copy size={16} strokeWidth={2} /> */}
            {copiedId === msg.id && window.innerWidth <= 768 ? (
              <Check
                className="copied-check-icon"
                size={16}
                strokeWidth={2.5}
              />
            ) : (
              <Copy size={16} strokeWidth={2} />
            )}
          </button>
          {msg.sourceMessageId && (
            <FeedbackButtons
              selectedFeedback={selectedFeedback}
              loading={feedbackLoading}
              onFeedback={onFeedback}
            />
          )}
        </div>
      )}
    </div>
  );
});

export default function ChatWindow({
  messages,
  isStreaming,
  onEditSave,
  searchMatch = null,
}) {
  const { token } = useAuth();
  const bottomRef = useRef(null);
  const scrollRootRef = useRef(null);
  const stickToBottomRef = useRef(true);
  const messageRefs = useRef(new Map());

  const [editingUserId, setEditingUserId] = useState(null);
  const [draft, setDraft] = useState("");
  const [copiedId, setCopiedId] = useState(null);
  const [feedbackByMessageId, setFeedbackByMessageId] = useState({});
  const [feedbackReasonByMessageId, setFeedbackReasonByMessageId] = useState(
    {}
  );
  const [feedbackLoadingByMessageId, setFeedbackLoadingByMessageId] = useState(
    {}
  );
  const [isFeedbackReasonModalOpen, setIsFeedbackReasonModalOpen] =
    useState(false);
  const [pendingFeedbackMessageId, setPendingFeedbackMessageId] =
    useState(null);
  const feedbackLoadingRef = useRef({});
  const historyFeedbackByMessageId = useMemo(() => {
    const feedback = {};
    messages.forEach((msg) => {
      if (msg.role === "assistant" && msg.sourceMessageId && msg.feedbackType) {
        feedback[msg.sourceMessageId] = msg.feedbackType;
      }
    });
    return feedback;
  }, [messages]);
  const historyFeedbackReasonByMessageId = useMemo(() => {
    const feedbackReasons = {};
    messages.forEach((msg) => {
      if (msg.role === "assistant" && msg.sourceMessageId && msg.feedbackReason) {
        feedbackReasons[msg.sourceMessageId] = msg.feedbackReason;
      }
    });
    return feedbackReasons;
  }, [messages]);

  // ===============================
  // 📋 COPY FUNCTION (NEW ADDITION)
  // ===============================
  const handleCopy = useCallback(async (text, id) => {
    try {
      await navigator.clipboard.writeText(text);
      setCopiedId(id);

      setTimeout(() => {
        setCopiedId(null);
      }, 1500);
    } catch (err) {
      console.error("Copy failed", err);
    }
  }, []);

  useEffect(() => {
    const el = scrollRootRef.current;
    if (!el) return;
    const threshold = 80;
    const onScroll = () => {
      stickToBottomRef.current =
        el.scrollHeight - el.scrollTop - el.clientHeight < threshold;
    };
    el.addEventListener("scroll", onScroll, { passive: true });
    return () => el.removeEventListener("scroll", onScroll);
  }, []);

  useEffect(() => {
    if (!stickToBottomRef.current) return;
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isStreaming]);

  const registerMessageRef = useCallback((messageId, node) => {
    if (!messageId) return;
    if (!node) {
      messageRefs.current.delete(messageId);
      return;
    }
    messageRefs.current.set(messageId, node);
  }, []);

  useEffect(() => {
    const messageId = searchMatch?.messageId;
    if (!messageId) return;

    const keyword = (searchMatch?.keyword ?? "").trim().toLowerCase();
    const target =
      messages.find(
        (m) =>
          m.sourceMessageId === messageId &&
          keyword &&
          (m.content ?? "").toLowerCase().includes(keyword)
      ) ?? messages.find((m) => m.sourceMessageId === messageId);
    if (!target) return;

    const node = messageRefs.current.get(target.id);
    if (!node) return;

    node.scrollIntoView({ behavior: "smooth", block: "center" });
  }, [messages, searchMatch]);

  const renderContentWithSearchHighlight = useCallback(
    (msg) => {
      const keyword = (searchMatch?.keyword ?? "").trim();
      if (!keyword) return msg.content ?? "";
      if (searchMatch?.messageId == null) return msg.content ?? "";
      if (msg.sourceMessageId !== searchMatch.messageId) return msg.content ?? "";

      const text = msg.content ?? "";
      const pattern = new RegExp(`(${escapeRegExp(keyword)})`, "ig");
      let sawMatch = false;
      const parts = text.split(pattern).map((part, index) => {
        if (part.toLowerCase() === keyword.toLowerCase()) {
          sawMatch = true;
          return (
            <mark key={`msg-hit-${msg.id}-${index}`} className="message-keyword-highlight">
              {part}
            </mark>
          );
        }
        return part;
      });
      return sawMatch ? parts : text;
    },
    [searchMatch]
  );

  const beginEdit = useCallback(
    (msg) => {
      if (isStreaming) return;
      setEditingUserId(msg.id);
      setDraft(msg.content ?? "");
    },
    [isStreaming]
  );

  const cancelEdit = useCallback(() => {
    setEditingUserId(null);
    setDraft("");
  }, []);

  const commitEdit = useCallback(
    (userMessageId) => {
      const trimmed = draft.trim();
      if (!trimmed) return;
      const ok = onEditSave(userMessageId, trimmed);
      if (ok) {
        setEditingUserId(null);
        setDraft("");
      }
    },
    [draft, onEditSave]
  );

  const getSelectedFeedback = useCallback(
    (messageId) =>
      feedbackByMessageId[messageId] ??
      historyFeedbackByMessageId[messageId] ??
      null,
    [feedbackByMessageId, historyFeedbackByMessageId]
  );

  const submitFeedback = useCallback(
    async (messageId, feedbackType, feedbackReason) => {
      if (!token || !messageId || feedbackLoadingRef.current[messageId]) return;
      const selectedFeedback = getSelectedFeedback(messageId);
      const selectedFeedbackReason =
        feedbackReasonByMessageId[messageId] ??
        historyFeedbackReasonByMessageId[messageId] ??
        null;
      if (selectedFeedback === feedbackType) return;
      feedbackLoadingRef.current = {
        ...feedbackLoadingRef.current,
        [messageId]: true,
      };
      setFeedbackLoadingByMessageId((prev) => ({
        ...prev,
        [messageId]: true,
      }));
      setFeedbackByMessageId((prev) => ({
        ...prev,
        [messageId]: feedbackType,
      }));
      setFeedbackReasonByMessageId((prev) => ({
        ...prev,
        [messageId]: feedbackType === "NOT_HELPFUL" ? feedbackReason : null,
      }));
      try {
        await updateMessageFeedbackApi(
          token,
          messageId,
          feedbackType,
          feedbackReason
        );
      } catch (err) {
        setFeedbackByMessageId((prev) => ({
          ...prev,
          [messageId]: selectedFeedback,
        }));
        setFeedbackReasonByMessageId((prev) => ({
          ...prev,
          [messageId]: selectedFeedbackReason,
        }));
        console.error("Feedback failed", err);
      } finally {
        feedbackLoadingRef.current = {
          ...feedbackLoadingRef.current,
          [messageId]: false,
        };
        setFeedbackLoadingByMessageId((prev) => ({
          ...prev,
          [messageId]: false,
        }));
      }
    },
    [feedbackReasonByMessageId, getSelectedFeedback, historyFeedbackReasonByMessageId, token]
  );

  const closeFeedbackReasonModal = useCallback(() => {
    setIsFeedbackReasonModalOpen(false);
    setPendingFeedbackMessageId(null);
  }, []);

  const handleFeedbackReasonSubmit = useCallback(
    async (feedbackReason) => {
      if (!pendingFeedbackMessageId) return;
      const messageId = pendingFeedbackMessageId;
      setIsFeedbackReasonModalOpen(false);
      setPendingFeedbackMessageId(null);
      await submitFeedback(messageId, "NOT_HELPFUL", feedbackReason);
    },
    [pendingFeedbackMessageId, submitFeedback]
  );

  const handleFeedback = useCallback(
    (messageId, feedbackType) => {
      if (!token || !messageId || feedbackLoadingRef.current[messageId]) return;
      if (getSelectedFeedback(messageId) === feedbackType) return;

      if (feedbackType === "NOT_HELPFUL") {
        setPendingFeedbackMessageId(messageId);
        setIsFeedbackReasonModalOpen(true);
        return;
      }

      submitFeedback(messageId, feedbackType);
    },
    [getSelectedFeedback, submitFeedback, token]
  );

  return (
    <div ref={scrollRootRef} className="chat-window">
      {messages.length === 0 && (
        <p className="chat-empty">Start a conversation…</p>
      )}
      {messages.map((msg) => {
        const keyword = (searchMatch?.keyword ?? "").trim().toLowerCase();
        const isSearchMatch =
          searchMatch?.messageId != null &&
          msg.sourceMessageId === searchMatch.messageId &&
          keyword.length > 0 &&
          (msg.content ?? "").toLowerCase().includes(keyword);
        const shouldRenderAssistantAsPlainText =
          msg.role === "assistant" && isSearchMatch;
        const renderedContent = renderContentWithSearchHighlight(msg);

        if (msg.role === "user") {
          return (
            <div key={msg.id} ref={(node) => registerMessageRef(msg.id, node)}>
              <UserBubble
                msg={msg}
                isEditing={editingUserId === msg.id}
                draft={draft}
                onDraftChange={setDraft}
                isStreaming={isStreaming}
                onBeginEdit={beginEdit}
                onCancelEdit={cancelEdit}
                onCommitEdit={commitEdit}
                onCopy={handleCopy} // ✅ ADDED
                copiedId={copiedId}
                renderedContent={renderedContent}
                isSearchMatch={isSearchMatch}
              />
            </div>
          );
        }

        return (
          <div key={msg.id} ref={(node) => registerMessageRef(msg.id, node)}>
            <AssistantBubble
              msg={msg}
              onCopy={handleCopy} // ✅ ADDED
              copiedId={copiedId}
              renderedContent={renderedContent}
              shouldRenderPlainText={shouldRenderAssistantAsPlainText}
              isSearchMatch={isSearchMatch}
              selectedFeedback={
                feedbackByMessageId[msg.sourceMessageId] ??
                historyFeedbackByMessageId[msg.sourceMessageId] ??
                null
              }
              feedbackLoading={
                feedbackLoadingByMessageId[msg.sourceMessageId] === true
              }
              onFeedback={(feedbackType) =>
                handleFeedback(msg.sourceMessageId, feedbackType)
              }
            />
          </div>
        );
      })}
      {isFeedbackReasonModalOpen && (
        <FeedbackReasonModal
          isOpen={isFeedbackReasonModalOpen}
          onClose={closeFeedbackReasonModal}
          onSubmit={handleFeedbackReasonSubmit}
        />
      )}
      <div ref={bottomRef} />
    </div>
  );
}
