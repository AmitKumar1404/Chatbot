import { Client } from '@stomp/stompjs';
import { wsChatUrl } from './apiConfig';

let stompClient = null;
let activeConnectionId = 0;
let activeSubscription = null;

export function connectWebSocket({ accessToken, onMessage, onConnect, onError }) {
  if (!accessToken) {
    console.warn('[WS] connectWebSocket called without accessToken');
    return null;
  }

  disconnectWebSocket();

  const connectionId = ++activeConnectionId;
  let connectAttempt = 0;
  const client = new Client({
    brokerURL: wsChatUrl(),

    reconnectDelay: 5000,

    connectHeaders: {
      Authorization: `Bearer ${accessToken}`,
    },

    debug: (msg) => console.debug('[STOMP debug]', msg),

    beforeConnect: () => {
      connectAttempt += 1;
      console.info("[WS] STOMP connect attempt", { connectionId, connectAttempt });
    },

    onConnect: (frame) => {
      if (client !== stompClient) {
        console.debug('[WS] Ignoring stale onConnect callback', { connectionId });
        return;
      }
      console.info('[WS] Connected', { connectionId, frame });

      if (activeSubscription) {
        activeSubscription.unsubscribe();
        activeSubscription = null;
      }

      activeSubscription = client.subscribe('/user/queue/messages', (message) => {
        if (client !== stompClient) {
          console.debug('[WS] Ignoring message from stale client', { connectionId });
          return;
        }
        console.debug('[WS] Message received on /user/queue/messages:', message.body);
        onMessage(message.body, { connectionId });
      });
      console.info('[WS] Subscribed to /user/queue/messages', {
        connectionId,
        subscriptionId: activeSubscription.id,
      });

      if (onConnect) onConnect({ connectionId, frame });
    },

    onDisconnect: () => {
      if (client !== stompClient) {
        return;
      }
      console.warn('[WS] Disconnected', { connectionId });
      if (activeSubscription) {
        activeSubscription.unsubscribe();
        activeSubscription = null;
      }
      if (onError) onError({ connectionId, reason: 'disconnect' });
    },

    onStompError: (frame) => {
      if (client !== stompClient) {
        return;
      }
      console.error('[WS] STOMP broker error:', frame.headers['message'], frame);
      if (onError) onError({ connectionId, reason: 'stomp-error', frame });
    },

    onWebSocketError: (error) => {
      if (client !== stompClient) {
        return;
      }
      console.warn('[WS] WebSocket error', { connectionId, error });
      if (activeSubscription) {
        activeSubscription.unsubscribe();
        activeSubscription = null;
      }
      if (onError) onError({ connectionId, reason: 'ws-error', error });
    },

    onWebSocketClose: (event) => {
      if (client !== stompClient) {
        return;
      }
      console.warn('[WS] WebSocket closed', {
        connectionId,
        code: event.code,
        reason: event.reason,
        wasClean: event.wasClean,
      });
      if (activeSubscription) {
        activeSubscription.unsubscribe();
        activeSubscription = null;
      }
      if (onError) onError({ connectionId, reason: 'ws-close', event });
    },
  });

  stompClient = client;
  console.info('[WS] Activating STOMP client', { connectionId });
  client.activate();
  return connectionId;
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
    if (activeSubscription) {
      activeSubscription.unsubscribe();
      activeSubscription = null;
    }
    stompClient.deactivate();
    stompClient = null;
    activeConnectionId += 1;
  }
}
