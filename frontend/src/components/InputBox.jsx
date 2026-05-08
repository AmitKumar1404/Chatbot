import { useState } from 'react';

export default function InputBox({ onSend, onStop, isStreaming, disabled }) {
  const [text, setText] = useState('');

  function handleSubmit(e) {
    e.preventDefault();
    if (isStreaming) return;
    const trimmed = text.trim();
    if (!trimmed) return;
    onSend(trimmed);
    setText('');
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
      if (isStreaming) {
        e.preventDefault();
        onStop();
        return;
      }
      handleSubmit(e);
    }
  }

  return (
    <form className="input-box" onSubmit={handleSubmit}>
      <textarea
        className="input-textarea"
        value={text}
        onChange={(e) => setText(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="Type a message…"
        rows={2}
        disabled={disabled}
      />
      {/* <button className="send-btn" type="submit" disabled={disabled || !text.trim()}>
        Send
      </button> */}
      {/* 🔥 BUTTON SWITCH */}
      <button
        className="send-btn"
        type={isStreaming ? 'button' : 'submit'}
        onClick={isStreaming ? onStop : undefined}
        disabled={disabled || (!isStreaming && !text.trim())}
      >
        {isStreaming ? 'Stop' : 'Send'}
      </button>
    </form>
  );
}
