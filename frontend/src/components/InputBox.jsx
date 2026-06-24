import { useState } from "react";

export default function InputBox({ onSend, onStop, isStreaming, disabled }) {
  const [text, setText] = useState("");

  function handleKeyDown(e) {
    if (e.key !== "Enter" || e.shiftKey) {
      return;
    }

    // ✅ STREAMING CHAL RAHI HAI
    // Enter completely ignore
    if (isStreaming) {
      return;
    }

    e.preventDefault();

    const trimmed = text.trim();

    if (!trimmed) return;

    onSend(trimmed);

    setText("");
  }
  return (
    <div className="input-box">
      <textarea
        className="input-textarea"
        value={text}
        onChange={(e) => setText(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="Type a message…"
        rows={2}
        disabled={disabled}
        // readOnly={isStreaming}
      />

      <button
        className="send-btn"
        type="button"
        disabled={disabled || (!isStreaming && !text.trim())}
        onClick={() => {
          // ✅ stop current response
          if (isStreaming) {
            onStop();
            return;
          }

          const trimmed = text.trim();

          if (!trimmed) return;

          onSend(trimmed);
          setText("");
        }}
      >
        {isStreaming ? "Stop" : "Send"}
      </button>
    </div>
  );
}
