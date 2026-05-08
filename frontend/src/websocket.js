import { Client } from '@stomp/stompjs';

let stompClient = null;

export function connectWebSocket({ onMessage, onConnect, onError }) {
  stompClient = new Client({
    brokerURL: 'ws://localhost:9999/api/v1/ws-chat',

    reconnectDelay: 5000,

    debug: (msg) => console.debug('[STOMP debug]', msg),

    onConnect: (frame) => {
      console.log('[WS] Connected. Frame:', frame);

      const sub = stompClient.subscribe('/user/queue/messages', (message) => {
        console.debug('[WS] Message received on /user/queue/messages:', message.body);
        const body = message.body;
        if (body === '[DONE]') {
          onMessage('[DONE]');
          return;
        }
        onMessage(body);
      });
      console.log('[WS] Subscribed to /user/queue/messages — id:', sub.id);

      if (onConnect) onConnect();
    },

    onDisconnect: () => {
      console.warn('[WS] Disconnected');
    },

    onStompError: (frame) => {
      console.error('[WS] STOMP broker error:', frame.headers['message'], frame);
      if (onError) onError(frame);
    },

    onWebSocketError: (error) => {
      console.error('[WS] WebSocket error:', error);
      if (onError) onError(error);
    },

    onWebSocketClose: (event) => {
      console.warn('[WS] WebSocket closed:', event);
    },
  });

  console.log('[WS] Activating STOMP client…');
  stompClient.activate();
}

export function sendMessage(content) {
  if (!stompClient || !stompClient.connected) {
    console.warn('WebSocket not connected');
    return;
  }

  stompClient.publish({
    destination: '/app/chat',
    body: content,
  });
}

export function sendStopSignal() {
  if (!stompClient || !stompClient.connected) {
    console.warn('WebSocket not connected');
    return;
  }

  stompClient.publish({
    destination: '/app/chat/stop',
    body: 'STOP',
  });
}

export function disconnectWebSocket() {
  if (stompClient) {
    stompClient.deactivate();
  }
}