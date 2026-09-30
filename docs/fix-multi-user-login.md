# fix/multi-user-login — Exhaustive Technical Documentation

> Generated from actual refs, owned commits, parent diffs and source snapshots. Baseline HEAD: `a62003a`. Secret values intentionally omitted.

# Branch Overview
Use tab-scoped auth storage so different users can log in in separate tabs.

# Branch Purpose
Use tab-scoped auth storage so different users can log in in separate tabs.

# Base Branch / Branch Lineage
Branch contains inherited search-dialog commit; actual fix is `ba1f78a`; PR #19.

# Git Commit History
- `ba1f78a` — fix(auth): replace localStorage with sessionStorage for tab-specific login

# Files Changed
- `frontend/src/context/AuthContext.jsx` — ba1f78a:M

# Functions Implemented
The following dossiers cover branch-critical executable boundaries. Exact diff symbol inventory follows and includes smaller helpers/accessors.

### 1. `AuthContext storage operations`
- **Function name / class:** `AuthContext storage operations`
- **Input:** access JWT
- **Output / side effect:** tab-scoped persisted auth
- **Internal flow:** Read/write/remove same token key in sessionStorage instead of localStorage.
- **Dependencies, DB/API, errors, security:** Browser sessionStorage.
- **Before → After / impact:** Before origin-wide tabs shared token; after each tab isolates user.
- **Why it matters:** Yeh branch ke user-visible ya correctness invariant ko executable boundary par implement karta hai; unsupported behavior isse infer nahi kiya gaya.

# Functions Modified
## Before / After evolution
- **`AuthContext storage operations`** — Before origin-wide tabs shared token; after each tab isolates user.

## Exact diff-level function and hunk inventory
- `frontend/src/context/AuthContext.jsx`: changed context `export function AuthProvider({ children }) {`

## Smaller Helper, Mapper, Accessor and Event-Handler Analysis
- No executable helper symbol introduced; change is documentation/configuration/CSS-only.
`A` file-status methods are newly introduced with their file; `M` status/hunk contexts indicate existing functions modified. Getter/setter boilerplate is retained in inventory where Git introduced it but grouped in detailed explanation.

# Classes Added/Modified
- `frontend/src/context/AuthContext.jsx` — ba1f78a:M

# APIs Added/Modified
No API change.

Lifecycle: client/frontend caller → Spring Security/filter or STOMP interceptor → controller/message handler → DTO/guard validation → service/transaction → repository/database or external dependency → response/event. Branch-specific deviations are in function dossiers.

# Database Changes
None

# Frontend Changes
AuthContext storage reads/writes/removes changed.

# Backend Changes
None

# Security Changes
Token remains JS-readable but becomes tab-scoped.

# Testing Changes
No multi-tab test.

# Technical Topics Covered
sessionStorage vs localStorage, auth bootstrap, tab isolation

# Detailed Implementation Flow
1. Input enters through the branch API/component/event named above.
2. Validation and ownership checks run at the exact controller/service functions in the dossiers.
3. Business/state transition delegates to listed dependencies.
4. Persistence or external service work executes with the documented transaction/failure boundary.
5. DTO/entity/event/UI state is returned or updated.
6. Errors follow actual exception/event/UI rollback behavior; undocumented recovery is not claimed.

# Important Technical Decisions
sessionStorage solves tab isolation, not XSS.

**Repository evidence:** commits, files and symbols listed above. **Reasonable engineering explanation:** only trade-offs inferable from implementation are stated; undocumented historical intent is explicitly not asserted.

# Bugs/Fixes
Origin-wide localStorage caused cross-tab user sharing.

# Edge Cases
Tab duplication, XSS, expiry, logout in one tab.

# Production Considerations
## Current implementation
sessionStorage solves tab isolation, not XSS.

## Improvement, not currently implemented
Consider HttpOnly cookies/BroadcastChannel policy and security tests.

# Interview Topics From This Branch
- Function boundaries: AuthContext storage operations
- API contract: No API change.
- Persistence: None
- Security: Token remains JS-readable but becomes tab-scoped.
- Failure/edge cases: Tab duplication, XSS, expiry, logout in one tab.
- Separate exhaustive Q&A: `Interview/fix-multi-user-login.txt`
