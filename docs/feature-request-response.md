# feature/request-response — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Make `InputBox` controlled by streaming state and improve responsive send/stop behavior.

# Branch Purpose
Make `InputBox` controlled by streaming state and improve responsive send/stop behavior.

# Base Branch / Branch Lineage
After message-copy; PR #4.

# Git Commit History
- `c1bf727` — Add controlled streaming behavior and responsive assistant chat layout

# Files Changed
- `frontend/src/components/InputBox.jsx` — c1bf727:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `InputBox.handleSubmit`
- **Function name / class:** `InputBox.handleSubmit`
- **Input:** form event/current text
- **Output / side effect:** calls onSend and clears input when allowed
- **Internal flow:** Prevent default; trim/guard disabled or streaming state; delegate message.
- **Dependencies, DB/API, errors, security:** React state and parent callback.
- **Before → After / impact:** Changed from less controlled send behavior to stream-aware controlled input.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

### 2. `InputBox.handleKeyDown`
- **Function name / class:** `InputBox.handleKeyDown`
- **Input:** keyboard event
- **Output / side effect:** submit or newline
- **Internal flow:** Enter without Shift triggers submit; Shift+Enter preserves multiline.
- **Dependencies, DB/API, errors, security:** handleSubmit.
- **Before → After / impact:** Keyboard behavior aligned with request state.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`InputBox.handleSubmit`** — Changed from less controlled send behavior to stream-aware controlled input.
- **`InputBox.handleKeyDown`** — Keyboard behavior aligned with request state.

## Exact diff-level function and hunk inventory
- `frontend/src/components/InputBox.jsx`: `handleSubmit()`, `handleKeyDown()`, changed context `export default function InputBox({ onSend, onStop, isStreaming, disabled }) {`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `frontend/src/components/InputBox.jsx::handleKeyDown()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `c1bf727`.
- `frontend/src/components/InputBox.jsx::handleSubmit()` — Event/use-case handler hai: input guard karta hai, dependent state/service call karta hai aur UI/DB/event side effect coordinate karta hai. Git evidence: `c1bf727`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `frontend/src/components/InputBox.jsx` — c1bf727:M

# APIs Added/Modified
No API change; consumes existing STOMP functions.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
None

# Frontend Changes
`InputBox.handleSubmit` and `handleKeyDown` changed.

# Backend Changes
None

# Security Changes
No change.

# Testing Changes
No tests.

# Technical Topics Covered
Controlled React input, keyboard submission, streaming disable/stop state

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Single controlled source prevents UI drift.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Input submission and stream state could diverge.

# Edge Cases
Enter vs Shift+Enter, empty input, disabled/offline/streaming state.

# Production Considerations
## Current implementation
Single controlled source prevents UI drift.

## Improvement, not currently implemented
Add keyboard/accessibility and state-transition tests.

# Interview Topics From This Branch
- Function boundaries: InputBox.handleSubmit, InputBox.handleKeyDown
- API contract: No API change; consumes existing STOMP functions.
- Persistence: None
- Security: No change.
- Failure/edge cases: Enter vs Shift+Enter, empty input, disabled/offline/streaming state.
- Separate exhaustive Q&A: `Interview/feature-request-response.txt`
