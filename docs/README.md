# Exhaustive Branch Documentation Index

Baseline: `docs/project-documentation` @ `a62003a`.

## Counts
- Meaningful documented branches/lines: 24 (23 development lines plus integrated `main`).
- Branch Markdown files: 24.
- Per-branch interview TXT files: 24.

## Documented branches
- `main` → [`docs/main.md`](main.md), `Interview/main.txt`
- `feature/stream-stop-fix` → [`docs/feature-stream-stop-fix.md`](feature-stream-stop-fix.md), `Interview/feature-stream-stop-fix.txt`
- `feature/message-copy` → [`docs/feature-message-copy.md`](feature-message-copy.md), `Interview/feature-message-copy.txt`
- `feature/request-response` → [`docs/feature-request-response.md`](feature-request-response.md), `Interview/feature-request-response.txt`
- `feature/login-page` → [`docs/feature-login-page.md`](feature-login-page.md), `Interview/feature-login-page.txt`
- `feature/user-chat-history` → [`docs/feature-user-chat-history.md`](feature-user-chat-history.md), `Interview/feature-user-chat-history.txt`
- `feature/reconnect-recovery` → [`docs/feature-reconnect-recovery.md`](feature-reconnect-recovery.md), `Interview/feature-reconnect-recovery.txt`
- `feature/responsive-collapsible-sidebar` → [`docs/feature-responsive-collapsible-sidebar.md`](feature-responsive-collapsible-sidebar.md), `Interview/feature-responsive-collapsible-sidebar.txt`
- `feature/chat-search` → [`docs/feature-chat-search.md`](feature-chat-search.md), `Interview/feature-chat-search.txt`
- `feature/streaming-ux-polish` → [`docs/feature-streaming-ux-polish.md`](feature-streaming-ux-polish.md), `Interview/feature-streaming-ux-polish.txt`
- `feature/redis-stream-state` → [`docs/feature-redis-stream-state.md`](feature-redis-stream-state.md), `Interview/feature-redis-stream-state.txt`
- `feature/search-dialog-collapsed-sidebar` → [`docs/feature-search-dialog-collapsed-sidebar.md`](feature-search-dialog-collapsed-sidebar.md), `Interview/feature-search-dialog-collapsed-sidebar.txt`
- `fix/multi-user-login` → [`docs/fix-multi-user-login.md`](fix-multi-user-login.md), `Interview/fix-multi-user-login.txt`
- `chore/postman-collection-sync` → [`docs/chore-postman-collection-sync.md`](chore-postman-collection-sync.md), `Interview/chore-postman-collection-sync.txt`
- `feature/swagger-integration` → [`docs/feature-swagger-integration.md`](feature-swagger-integration.md), `Interview/feature-swagger-integration.txt`
- `feature/qwen2.5-model-upgrade` → [`docs/feature-qwen2.5-model-upgrade.md`](feature-qwen2.5-model-upgrade.md), `Interview/feature-qwen2.5-model-upgrade.txt`
- `feature/production-readiness-hardening` → [`docs/feature-production-readiness-hardening.md`](feature-production-readiness-hardening.md), `Interview/feature-production-readiness-hardening.txt`
- `fix/reconnect-contract-restore` → [`docs/fix-reconnect-contract-restore.md`](fix-reconnect-contract-restore.md), `Interview/fix-reconnect-contract-restore.txt`
- `feature/refresh-token-clean` → [`docs/feature-refresh-token-clean.md`](feature-refresh-token-clean.md), `Interview/feature-refresh-token-clean.txt`
- `bugfix/ui-fixes` → [`docs/bugfix-ui-fixes.md`](bugfix-ui-fixes.md), `Interview/bugfix-ui-fixes.txt`
- `feature/pinned-chats` → [`docs/feature-pinned-chats.md`](feature-pinned-chats.md), `Interview/feature-pinned-chats.txt`
- `feature/feedback-system` → [`docs/feature-feedback-system.md`](feature-feedback-system.md), `Interview/feature-feedback-system.txt`
- `feature/document-chat` → [`docs/feature-document-chat.md`](feature-document-chat.md), `Interview/feature-document-chat.txt`
- `feature/voice-input` → [`docs/feature-voice-input.md`](feature-voice-input.md), `Interview/feature-voice-input.txt`

## Intentionally excluded as duplicate/temporary
- `docs/project-documentation`: documentation worktree branch, no development commits beyond `main`.
- `feature/redis-active-stream-registry`: exact alias of `feature/streaming-ux-polish` at `f3fb373`; no separate implementation.
- `feature/refresh-token`: stale local branch at `2bc5f87` that shares earlier hardening lineage; refresh implementation used for branch-specific documentation is the clean integrated line `feature/refresh-token-clean` (`3e9de42`), so inherited hardening work is not duplicated.
- Remote tips that only merge `main` into a feature are lineage sync commits, not separate implementation branches.

## Ownership corrections
- `22b0d98` belongs to user-chat-history feature work but entered main through reconnect PR #9.
- `51ad3cc` search-dialog work entered through multi-user-login PR #19; `ba1f78a` is actual login fix.
- `f3fb373` streaming polish is inherited by Redis PR #16; `e844265` is Redis-specific.
- `fix/reconnect-contract-restore` implements rate limiting, not reconnect logic.
- `75fed10` and `4185ea6` are tip-only commits not reachable from main.
