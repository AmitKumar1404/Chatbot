import { useState } from "react";

export default function InputBox({ onSend, onStop, isStreaming, disabled }) {
  const [text, setText] = useState("");

  // function handleSubmit(e) {
  //   e.preventDefault();
  //   if (isStreaming) return;
  //   const trimmed = text.trim();
  //   if (!trimmed) return;
  //   onSend(trimmed);
  //   setText("");
  // }

  // function handleKeyDown(e) {
  //   if (e.key === 'Enter' && !e.shiftKey) {
  //     if (isStreaming) {
  //       e.preventDefault();
  //       onStop();
  //       return;
  //     }
  //     handleSubmit(e);
  //   }
  // }
  function handleKeyDown(e) {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();

      // ✅ DO NOTHING DURING STREAMING
      if (isStreaming) {
        return;
      }

      const trimmed = text.trim();

      if (!trimmed) return;

      onSend(trimmed);
      setText("");
    }
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
      {/* <button className="send-btn" type="submit" disabled={disabled || !text.trim()}>
        Send
      </button> */}
      {/* 🔥 BUTTON SWITCH */}
      {/* <button
        className="send-btn"
        type={isStreaming ? 'button' : 'submit'}
        onClick={isStreaming ? onStop : undefined}
        disabled={disabled || (!isStreaming && !text.trim())}
      >
        {isStreaming ? 'Stop' : 'Send'}
      </button> */}
      {/* <button
        className="send-btn"
        type="submit"
        disabled={disabled || isStreaming || !text.trim()}
      >
        {isStreaming ? "Stop" : "Send"}
      </button> */}
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
