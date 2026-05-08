import { useState, useEffect, useRef, useCallback } from 'react';
import ChatWindow from './components/ChatWindow';
import InputBox from './components/InputBox';
import { connectWebSocket, sendMessage, sendStopSignal, disconnectWebSocket } from './websocket';
import './App.css';

let messageIdCounter = 0;
function nextId() {
  return ++messageIdCounter;
}

export default function App() {
  const [chats, setChats] = useState([
    {
      id: 1,
      title: 'New Chat',
      messages: []
    }
  ]);

  const [activeChatId, setActiveChatId] = useState(1);

  const [connected, setConnected] = useState(false);
  const [statusText, setStatusText] = useState('Connecting…');
  const [isTyping, setIsTyping] = useState(false);
  const [isStreaming, setIsStreaming] = useState(false);

  // Tracks whether the current assistant message bubble is actively appending chunks.
  const streamingRef = useRef(false);
  const stopRef = useRef(false);
  const isStreamingRef = useRef(false);
  const activeChatIdRef = useRef(activeChatId);
  const streamChatIdRef = useRef(null);

  useEffect(() => {
    activeChatIdRef.current = activeChatId;
  }, [activeChatId]);

  const setStreamingState = useCallback((value) => {
    isStreamingRef.current = value;
    setIsStreaming(value);
  }, []);

  const activeChat = chats.find(c => c.id === activeChatId);

  const finalizeStream = useCallback(() => {
    streamingRef.current = false;
    stopRef.current = false;
    streamChatIdRef.current = null;
    setStreamingState(false);
    setIsTyping(false);
  }, [setStreamingState]);

  const handleChunk = useCallback((chunk) => {
    if (chunk === '[DONE]') {
      if (isStreamingRef.current) {
          finalizeStream();
      }
      return;
  }

    // Ignore stale chunks after local stop click.
    if (stopRef.current) return;
    if (!isStreamingRef.current) return;

    // Hide typing indicator once the first chunk arrives.
    setIsTyping(false);

    const targetChatId = streamChatIdRef.current ?? activeChatIdRef.current;

    setChats(prev =>
      prev.map(chat => {
        if (chat.id !== targetChatId) return chat;

        const last = chat.messages[chat.messages.length - 1];

        if (last && last.role === 'assistant' && streamingRef.current) {
          return {
            ...chat,
            messages: [
              ...chat.messages.slice(0, -1),
              { ...last, content: last.content + chunk }
            ]
          };
        }

        streamingRef.current = true;

        return {
          ...chat,
          messages: [
            ...chat.messages,
            { id: nextId(), role: 'assistant', content: chunk }
          ]
        };
      })
    );
  }, [finalizeStream]);

  useEffect(() => {
    connectWebSocket({
      onMessage: handleChunk,
      onConnect: () => {
        setConnected(true);
        setStatusText('Connected');
      },
      onError: () => {
        setConnected(false);
        setStatusText('Disconnected — retrying…');
      },
    });

    return () => disconnectWebSocket();
  }, [handleChunk]);

  function handleSend(text) {
    if (isStreamingRef.current || !connected) {
      return;
    }

    streamingRef.current = false;
    stopRef.current = false;
    streamChatIdRef.current = activeChatIdRef.current;
    setStreamingState(true);
    setIsTyping(true);

    setChats(prev =>
      prev.map(chat => {
        if (chat.id !== activeChatId) return chat;

        const isFirstMessage = chat.messages.length === 0;

        return {
          ...chat,
          title: isFirstMessage
            ? text.slice(0, 20)
            : chat.title,
          messages: [
            ...chat.messages,
            { id: nextId(), role: 'user', content: text }
          ]
        };
      })
    );

    sendMessage(text);
  }

  function stopResponse() {
    if (!isStreamingRef.current) return;
    stopRef.current = true;
    sendStopSignal();
    finalizeStream();
  }

  function createNewChat() {
    const newChat = {
      id: Date.now(),
      title: 'New Chat',
      messages: []
    };

    setChats(prev => [newChat, ...prev]);
    setActiveChatId(newChat.id);
  }

  function deleteChat(chatId) {
    setChats(prev => prev.filter(c => c.id !== chatId));

    if (chatId === activeChatId) {
      setActiveChatId(null);
    }
  }

  function renameChat(chatId) {
    const newName = prompt('Enter new name');
    if (!newName) return;

    setChats(prev =>
      prev.map(c =>
        c.id === chatId ? { ...c, title: newName } : c
      )
    );
  }

  return (
    <div className="app-layout">
      <div className="sidebar">
        <button className="new-chat-btn" onClick={createNewChat}>
          + New Chat
        </button>

        <div className="chat-list">
          {chats.map(chat => (
            <div
              key={chat.id}
              className={`chat-item ${chat.id === activeChatId ? 'active' : ''}`}
            >
              <span onClick={() => setActiveChatId(chat.id)}>
                {chat.title}
              </span>

              <div className="chat-actions">
                <button onClick={() => renameChat(chat.id)}>✏️</button>
                <button onClick={() => deleteChat(chat.id)}>🗑</button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* CHAT AREA */}
      <div className="chat-section">

        <header className="app-header">
          <h1 className="app-title">Talk To Me</h1>
          <span className={`status-badge ${connected ? 'online' : 'offline'}`}>
            {statusText}
          </span>
        </header>

        <ChatWindow
          messages={activeChat?.messages || []}
          isTyping={isTyping}
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