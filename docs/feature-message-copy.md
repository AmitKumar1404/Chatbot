# feature/message-copy — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Add assistant copy action and expose edit controls in message UI.

# Branch Purpose
Add assistant copy action and expose edit controls in message UI.

# Base Branch / Branch Lineage
After stream-stop PRs; one commit merged by PR #3.

# Git Commit History
- `da9383e` — add copy & edit button with function

# Files Changed
- `frontend/package-lock.json` — da9383e:M
- `frontend/package.json` — da9383e:M
- `frontend/src/App.css` — da9383e:M
- `frontend/src/components/ChatWindow.jsx` — da9383e:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `ChatWindow.handleCopy`
- **Function name / class:** `ChatWindow.handleCopy`
- **Input:** message text, message id
- **Output / side effect:** clipboard write and copied UI state
- **Internal flow:** Call navigator.clipboard.writeText; set temporary copied indicator; handle rejection.
- **Dependencies, DB/API, errors, security:** Browser Clipboard API; no backend.
- **Before → After / impact:** Assistant bubble gained copy action; edit controls were surfaced alongside it.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`ChatWindow.handleCopy`** — Assistant bubble gained copy action; edit controls were surfaced alongside it.

## Exact diff-level function and hunk inventory
- `frontend/src/components/ChatWindow.jsx`: `AssistantBubble()`, `handleCopy()`, changed context `const UserBubble = memo(function UserBubble({`, changed context `export default function ChatWindow({ messages, isStreaming, onEditSave }) {`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `frontend/src/components/ChatWindow.jsx::AssistantBubble()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `da9383e`.
- `frontend/src/components/ChatWindow.jsx::handleCopy()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `da9383e`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `frontend/package-lock.json` — da9383e:M
- `frontend/package.json` — da9383e:M
- `frontend/src/App.css` — da9383e:M
- `frontend/src/components/ChatWindow.jsx` — da9383e:M

# APIs Added/Modified
No API change.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
None

# Frontend Changes
`ChatWindow.jsx` adds `handleCopy` and action rendering.

# Backend Changes
None

# Security Changes
No change.

# Testing Changes
No frontend tests.

# Technical Topics Covered
React memoization, Clipboard API, temporary copied-state timer, Lucide icons and UI action-state rendering. Is commit me Markdown renderer introduce nahi hua.

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Browser clipboard keeps operation client-side.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
UI lacked copy/edit affordance.

# Edge Cases
Clipboard rejection, copied-state timeout, editing during stream.

# Production Considerations
## Current implementation
Browser clipboard keeps operation client-side.

## Improvement, not currently implemented
Add component tests and accessible live feedback.

# Interview Topics From This Branch
- Function boundaries: ChatWindow.handleCopy
- API contract: No API change.
- Persistence: None
- Security: No change.
- Failure/edge cases: Clipboard rejection, copied-state timeout, editing during stream.
- Separate exhaustive Q&A: `Interview/feature-message-copy.txt`
