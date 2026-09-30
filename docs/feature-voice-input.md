# feature/voice-input — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Restore explicit normal/document modes, then add browser speech-to-text input.

# Branch Purpose
Restore explicit normal/document modes, then add browser speech-to-text input.

# Base Branch / Branch Lineage
PR #35 `920edf5` mode restore and PR #36 `5df9a34` voice.

# Git Commit History
- `920edf5` — fix: restore normal and document chat modes
- `5df9a34` — feat: add voice input to chat

# Files Changed
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 920edf5:M
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — 920edf5:M
- `frontend/src/App.css` — 5df9a34:M
- `frontend/src/ChatApp.jsx` — 920edf5:M
- `frontend/src/components/ChatModeToggle.jsx` — 920edf5:A
- `frontend/src/components/InputBox.jsx` — 5df9a34:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `InputBox.getRecognition`
- **Function name / class:** `InputBox.getRecognition`
- **Input:** browser environment
- **Output / side effect:** cached SpeechRecognition instance
- **Internal flow:** Resolve `window.SpeechRecognition || window.webkitSpeechRecognition`; set `lang = navigator.language || "en-US"`, `continuous = true`, `interimResults = false`; append only final transcripts and map error/end callbacks into UI state.
- **Dependencies, DB/API, errors, security:** Web Speech API and recognitionRef.
- **Before → After / impact:** New progressive enhancement.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `InputBox.handleMicClick`
- **Function name / class:** `InputBox.handleMicClick`
- **Input:** click
- **Output / side effect:** starts/stops recognition
- **Internal flow:** Guard unsupported/disabled/streaming; toggle listening and invoke start/stop with error handling.
- **Dependencies, DB/API, errors, security:** getRecognition/stopRecognition.
- **Before → After / impact:** Adds voice without backend audio changes.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 3. `InputBox.stopRecognition`
- **Function name / class:** `InputBox.stopRecognition`
- **Input:** none
- **Output / side effect:** cleanup side effect
- **Internal flow:** Stop existing recognizer safely and reset listening state.
- **Dependencies, DB/API, errors, security:** Recognition ref; called on send/unmount/mode changes as implemented.
- **Before → After / impact:** Prevents microphone lifecycle leaks.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 4. `applyChatMode / buildModePayloadFields`
- **Function name / class:** `applyChatMode / buildModePayloadFields`
- **Input:** requested mode, selected document
- **Output / side effect:** normalized mode/payload fields
- **Internal flow:** Prevent DOCUMENT without upload; send explicit NORMAL or DOCUMENT+documentId for new/edit/recovery.
- **Dependencies, DB/API, errors, security:** ChatModeToggle and STOMP payload.
- **Before → After / impact:** 920edf5 restores separation before voice commit.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`InputBox.getRecognition`** — New progressive enhancement.
- **`InputBox.handleMicClick`** — Adds voice without backend audio changes.
- **`InputBox.stopRecognition`** — Prevents microphone lifecycle leaks.
- **`applyChatMode / buildModePayloadFields`** — 920edf5 restores separation before voice commit.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java`: `previewQuery()`
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java`: `getChatMode()`, `setChatMode()`
- `frontend/src/ChatApp.jsx`: `applyChatMode()`, `buildModePayloadFields()`, changed context `export default function ChatApp() {`
- `frontend/src/components/ChatModeToggle.jsx`: `ChatModeToggle()`
- `frontend/src/components/InputBox.jsx`: `stopRecognition()`, `getRecognition()`, `handleMicClick()`, changed context `export default function InputBox({ onSend, onStop, isStreaming, disabled }) {`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java::previewQuery()` — Small deterministic transformation/helper hai used by the parent algorithm; side effects DB/API par nahi hain unless logs stated. Git evidence: `920edf5`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::getChatMode()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `920edf5`.
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java::setChatMode()` — DTO/entity/component property mutate karta hai; deserialization, JPA ya state transition me use hota hai. Git evidence: `920edf5`.
- `frontend/src/ChatApp.jsx::applyChatMode()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `920edf5`.
- `frontend/src/ChatApp.jsx::buildModePayloadFields()` — Inputs/configuration ko target DTO/prompt/chunk/bucket object me construct karta hai. Git evidence: `920edf5`.
- `frontend/src/components/ChatModeToggle.jsx::ChatModeToggle()` — Top-level entry/component function hai jo child state, handlers aur rendering/application bootstrap compose karta hai. Git evidence: `920edf5`.
- `frontend/src/components/InputBox.jsx::getRecognition()` — Stored/derived value read karke caller/serializer ko return karta hai. Git evidence: `5df9a34`.
- `frontend/src/components/InputBox.jsx::handleMicClick()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `5df9a34`.
- `frontend/src/components/InputBox.jsx::stopRecognition()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `5df9a34`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/src/main/java/com/chatbot/controller/ChatWebSocketController.java` — 920edf5:M
- `backend/src/main/java/com/chatbot/dto/ChatStompPayload.java` — 920edf5:M
- `frontend/src/App.css` — 5df9a34:M
- `frontend/src/ChatApp.jsx` — 920edf5:M
- `frontend/src/components/ChatModeToggle.jsx` — 920edf5:A
- `frontend/src/components/InputBox.jsx` — 5df9a34:M

# APIs Added/Modified
No audio API; transcript uses existing STOMP send.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
None

# Frontend Changes
InputBox stopRecognition/getRecognition/handleMicClick; mode toggle helpers.

# Backend Changes
Mode enum/controller routing modified by 920edf5.

# Security Changes
Application backend ko audio upload nahi hota; browser permission/security applies. Lekin some Web Speech implementations vendor cloud recognition use kar sakte hain, so “audio never leaves the device” repository evidence se claim nahi kiya ja sakta; privacy notice/browser policy production concern hai.

# Testing Changes
No voice or mode tests on main.

# Technical Topics Covered
Web Speech API, React refs/lifecycle, explicit mode payload

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Feature detection avoids requiring backend speech infrastructure.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Mode regression fixed; voice is progressive enhancement.

# Edge Cases
Unsupported browser, denied permission, recognition error, stream starts while listening, unmount.

# Production Considerations
## Current implementation
Feature detection avoids requiring backend speech infrastructure.

## Improvement, not currently implemented
Accessibility fallback, telemetry, browser E2E and privacy notice.

# Interview Topics From This Branch
- Function boundaries: InputBox.getRecognition, InputBox.handleMicClick, InputBox.stopRecognition, applyChatMode / buildModePayloadFields
- API contract: No audio API; transcript uses existing STOMP send.
- Persistence: None
- Security: Audio stays browser-side; browser permission/security applies.
- Failure/edge cases: Unsupported browser, denied permission, recognition error, stream starts while listening, unmount.
- Separate exhaustive Q&A: `Interview/feature-voice-input.txt`
