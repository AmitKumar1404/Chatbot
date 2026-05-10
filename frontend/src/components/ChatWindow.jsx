import { useEffect, useRef, useState, memo, useCallback } from "react";

const UserBubble = memo(function UserBubble({
  msg,
  isEditing,
  draft,
  onDraftChange,
  isStreaming,
  onBeginEdit,
  onCancelEdit,
  onCommitEdit,
}) {
  return (
    <div className="chat-bubble user user-bubble-wrap">
      {!isEditing && (
        <>
          {/* <span className="bubble-label">You</span> */}

          <p className="bubble-text">{msg.content}</p>

          <button
            type="button"
            className="msg-edit-btn"
            title="Edit message"
            disabled={isStreaming}
            onClick={() => onBeginEdit(msg)}
          >
            <span className="edit-icon">✎</span>
          </button>
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

const AssistantBubble = memo(function AssistantBubble({ msg }) {
  if (msg.streaming && !(msg.content ?? "").trim()) {
    return (
      <div className="chat-bubble assistant typing">
        {/* <span className="bubble-label">Assistant</span> */}
        <div className="typing-dots" aria-label="Assistant is typing">
          <span />
          <span />
          <span />
        </div>
      </div>
    );
  }

  return (
    <div className={`chat-bubble ${msg.role}`}>
      {/* <span className="bubble-label">Assistant</span> */}
      <p className="bubble-text">{msg.content}</p>
    </div>
  );
});

export default function ChatWindow({ messages, isStreaming, onEditSave }) {
  const bottomRef = useRef(null);
  const scrollRootRef = useRef(null);
  const stickToBottomRef = useRef(true);

  const [editingUserId, setEditingUserId] = useState(null);
  const [draft, setDraft] = useState("");

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
            />
          );
        }

        return <AssistantBubble key={msg.id} msg={msg} />;
      })}
      <div ref={bottomRef} />
    </div>
  );
}
