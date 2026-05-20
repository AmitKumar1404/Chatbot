import { useEffect, useRef, useState, memo, useCallback } from "react";
import { Copy, Check } from "lucide-react";

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
}) {
  return (
    <div className="chat-bubble user user-bubble-wrap">
      {!isEditing && (
        <>
          {/* MESSAGE TEXT */}
          <p className="bubble-text">{msg.content}</p>

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
    <div className="chat-bubble assistant">
      {/* MESSAGE TEXT */}
      <p className="bubble-text">{msg.content}</p>

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
        </div>
      )}
    </div>
  );
});

export default function ChatWindow({ messages, isStreaming, onEditSave }) {
  const bottomRef = useRef(null);
  const scrollRootRef = useRef(null);
  const stickToBottomRef = useRef(true);

  const [editingUserId, setEditingUserId] = useState(null);
  const [draft, setDraft] = useState("");
  const [copiedId, setCopiedId] = useState(null);

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

  return (
    <div ref={scrollRootRef} className="chat-window">
      {messages.length === 0 && (
        <p className="chat-empty">Start a conversation…</p>
      )}
      {messages.map((msg) => {
        if (msg.role === "user") {
          return (
            <UserBubble
              key={msg.id}
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
            />
          );
        }

        return (
          <AssistantBubble
            key={msg.id}
            msg={msg}
            onCopy={handleCopy} // ✅ ADDED
            copiedId={copiedId}
          />
        );
      })}
      <div ref={bottomRef} />
    </div>
  );
}
