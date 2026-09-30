# feature/qwen2.5-model-upgrade — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Change default Ollama model to Qwen 2.5 7B and preserve whitespace-only streamed fragments for Markdown.

# Branch Purpose
Change default Ollama model to Qwen 2.5 7B and preserve whitespace-only streamed fragments for Markdown.

# Base Branch / Branch Lineage
Commit `dd49b14`; PR #22.

# Git Commit History
- `dd49b14` — update ollama version from phi3 to qwen2.5:7b,preserve markdown structure by retaining whitespace chunks

# Files Changed
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — dd49b14:M
- `backend/src/main/resources/application-prod.yml` — dd49b14:M
- `backend/src/main/resources/application.yml` — dd49b14:M
- `frontend/package-lock.json` — dd49b14:M
- `frontend/package.json` — dd49b14:M
- `frontend/src/App.css` — dd49b14:M
- `frontend/src/components/ChatWindow.jsx` — dd49b14:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `OllamaStreamingServiceImpl.mapOllamaLineToTextFlux`
- **Function name / class:** `OllamaStreamingServiceImpl.mapOllamaLineToTextFlux`
- **Input:** one NDJSON chunk
- **Output / side effect:** zero/one text Flux
- **Internal flow:** Parse JSON response/done; preserve whitespace; suppress only terminal empty; malformed lines log and drop.
- **Dependencies, DB/API, errors, security:** ObjectMapper/Reactor.
- **Before → After / impact:** Before blank filtering damaged Markdown; after whitespace carries formatting.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`OllamaStreamingServiceImpl.mapOllamaLineToTextFlux`** — Before blank filtering damaged Markdown; after whitespace carries formatting.

## Exact diff-level function and hunk inventory
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java`: `visualize()`
- `frontend/src/components/ChatWindow.jsx`: changed context `const AssistantBubble = memo(function AssistantBubble({`, changed context `export default function ChatWindow({`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java::visualize()` — Diff me introduced/modified executable symbol; exact callers aur branch flow detailed dossiers/file context me documented hain. Git evidence: `dd49b14`.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `backend/src/main/java/com/chatbot/impl/OllamaStreamingServiceImpl.java` — dd49b14:M
- `backend/src/main/resources/application-prod.yml` — dd49b14:M
- `backend/src/main/resources/application.yml` — dd49b14:M
- `frontend/package-lock.json` — dd49b14:M
- `frontend/package.json` — dd49b14:M
- `frontend/src/App.css` — dd49b14:M
- `frontend/src/components/ChatWindow.jsx` — dd49b14:M

# APIs Added/Modified
No endpoint change.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
None

# Frontend Changes
ChatWindow ReactMarkdown/GFM and CSS formatting.

# Backend Changes
OllamaStreamingServiceImpl parser/model config.

# Security Changes
No change.

# Testing Changes
No parser/Markdown tests.

# Technical Topics Covered
Ollama NDJSON, React Markdown/GFM, whitespace semantics

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
Drop only terminal done+empty response; retain other whitespace.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Blank filtering previously collapsed tables/lists/code formatting.

# Edge Cases
Malformed NDJSON, terminal empty frame, whitespace flood, model unavailable.

# Production Considerations
## Current implementation
Drop only terminal done+empty response; retain other whitespace.

## Improvement, not currently implemented
Parser fixtures, token/latency metrics and model compatibility tests.

# Interview Topics From This Branch
- Function boundaries: OllamaStreamingServiceImpl.mapOllamaLineToTextFlux
- API contract: No endpoint change.
- Persistence: None
- Security: No change.
- Failure/edge cases: Malformed NDJSON, terminal empty frame, whitespace flood, model unavailable.
- Separate exhaustive Q&A: `Interview/feature-qwen2.5-model-upgrade.txt`
