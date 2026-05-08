import { useEffect, useRef } from 'react';

export default function ChatWindow({ messages, isTyping }) {
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isTyping]);

  return (
    <div className="chat-window">
      {messages.length === 0 && !isTyping &&(
        <p className="chat-empty">Start a conversation…</p>
      )}
      {messages.map((msg) => (
        <div key={msg.id} className={`chat-bubble ${msg.role}`}>
          <span className="bubble-label">{msg.role === 'user' ? 'You' : 'Assistant'}</span>
          <p className="bubble-text">{msg.content}</p>
        </div>
      ))}
      {isTyping && (
        <div className="chat-bubble assistant typing">
          <span className="bubble-label">Assistant</span>

          {/* 👇 WhatsApp style dots */}
          <div className="typing-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      )}
      <div ref={bottomRef} />
    </div>
  );
}
