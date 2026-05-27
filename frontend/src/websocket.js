import { Client } from '@stomp/stompjs';
import { wsChatUrl } from './apiConfig';

let stompClient = null;
let manualDisconnectInProgress = false;
let reconnectAttempts = 0;
let reconnectLimitReached = false;

const RECONNECT_DELAY_MS = 5000;
const MAX_RECONNECT_ATTEMPTS = 12;
const HEARTBEAT_INTERVAL_MS = 20000;

function resetReconnectGuards() {
  reconnectAttempts = 0;
  reconnectLimitReached = false;
}

export function connectWebSocket({ accessToken, onMessage, onConnect, onError }) {
  if (!accessToken) {
    console.warn('[WS] connectWebSocket called without accessToken');
    return;
  }

  disconnectWebSocket();
  manualDisconnectInProgress = false;
  resetReconnectGuards();

  stompClient = new Client({
    brokerURL: wsChatUrl(),

    reconnectDelay: RECONNECT_DELAY_MS,
    heartbeatIncoming: HEARTBEAT_INTERVAL_MS,
    heartbeatOutgoing: HEARTBEAT_INTERVAL_MS,

    connectHeaders: {
      Authorization: `Bearer ${accessToken}`,
    },

    debug: (msg) => console.debug('[STOMP debug]', msg),

    onConnect: (frame) => {
      console.log('[WS] Connected. Frame:', frame);
      resetReconnectGuards();

      const sub = stompClient.subscribe('/user/queue/messages', (message) => {
        console.debug('[WS] Message received on /user/queue/messages:', message.body);
        onMessage(message.body);
      });
      console.log('[WS] Subscribed to /user/queue/messages — id:', sub.id);

      if (onConnect) onConnect();
    },

    // onDisconnect: () => {
    //   console.warn('[WS] Disconnected');
    // },
    onDisconnect: () => {
      console.warn('[WS] Disconnected');
    
      if (onError) {
        onError();
      }
    },

    onStompError: (frame) => {
      console.error('[WS] STOMP broker error:', frame.headers['message'], frame);
      if (onError) onError(frame);
    },

    // onWebSocketError: (error) => {
    //   console.error('[WS] WebSocket error:', error);
    //   if (onError) onError(error);
    // },
    onWebSocketError: () => {

      // backend restart/offline ke time normal behavior
    
      if (onError) {
        onError();
      }
    },

  // onWebSocketClose: (event) => {
  //   console.warn('[WS] WebSocket closed:', event);
  
  //   if (onError) {
  //     onError();
  //   }
  // },
  onWebSocketClose: (event) => {

    // 1000 = normal close
    // 1001 = server restart/navigation
    // 1006 = backend temporarily unavailable
  
    if (!manualDisconnectInProgress && !reconnectLimitReached) {
      reconnectAttempts += 1;
      if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
        reconnectLimitReached = true;
        if (stompClient) {
          stompClient.reconnectDelay = 0;
        }
        console.error('[WS] Reconnect attempt limit reached; auto-reconnect paused.');
      }
    }

    const expected =
      event.code === 1000 ||
      event.code === 1001 ||
      event.code === 1006;
  
    if (!expected) {
      console.warn('[WS] WebSocket closed:', event);
    }
  
    if (onError) {
      onError();
    }
  },
});
  console.log('[WS] Activating STOMP client…');
  stompClient.activate();
}

const JSON_CT = { 'content-type': 'application/json' };

export function sendMessage(payloadObject) {
  if (!stompClient || !stompClient.connected) {
    console.warn('WebSocket not connected');
    return;
  }

  stompClient.publish({
    destination: '/app/chat',
    body: JSON.stringify(payloadObject),
    headers: JSON_CT,
  });
}

export function sendStopSignal() {
  if (!stompClient || !stompClient.connected) {
    console.warn('WebSocket not connected');
    return;
  }

  stompClient.publish({
    destination: '/app/chat/stop',
    body: JSON.stringify('STOP'),
    headers: JSON_CT,
  });
}

export function disconnectWebSocket() {
  if (stompClient) {
    manualDisconnectInProgress = true;
    stompClient.deactivate();
    stompClient = null;
  }
  resetReconnectGuards();
}
