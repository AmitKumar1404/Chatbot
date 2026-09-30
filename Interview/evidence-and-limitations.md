# Evidence and Limitations

## Verified sources
- `git log --all`, first-parent main history, merge parents, unique commit ranges and changed-file diffs
- All local branches and locally available `origin/*` refs
- Current Java/React implementation at `a62003a`
- Flyway `V1`–`V12`, Maven/npm manifests, GitHub Actions, Postman collection and existing README
- Current tracked tests and tip-only tests visible through branch history

## Important limits
- Current main has one Spring context-load test; frontend automated tests are absent.
- Historical intent is not assumed from branch names. Example: `fix/reconnect-contract-restore` diff is rate limiting.
- A commit message proves change intent/symptom, but not an undocumented debugging session. Such details are marked unverifiable.
- Local branch tips `feature/document-chat` (`75fed10`) and `feature/feedback-system` (`4185ea6`) contain work not reachable from main.
- Current README contains stale sections (for example earlier Redis absence) alongside newer additions. Source/config/history take priority.
- Servlet context `/api/v1` ke saath document and feedback-analytics controller mappings double-prefix effective paths banate hain: `/api/v1/api/documents/...` and `/api/v1/api/v1/admin/feedback/...`.
- Backend refresh-token rotation implemented hai, lekin frontend `/auth/refresh` use nahi karta.
- Current Postman collection later pin/feedback/analytics/document APIs se drift karti hai; Springdoc STOMP destinations describe nahi karta.
- `StreamingMessageRenderer.jsx`, `useSidebarState.js` aur recovery replay refs current live flow me unused/incomplete evidence dikhate hain; unhe implemented runtime behavior nahi maana gaya.
- No benchmark, load test or production telemetry exists, so performance/scalability numbers are not claimed.
- No secret value is reproduced. Only configuration variable purposes are discussed.

## Current test reality
- `backend/src/test/java/com/chatbot/ChatbotApplicationTests.java`: Spring context load with `local` profile.
- `feature/document-chat` tip adds two tests (`ChatStompPayloadChatModeTest`, `PromptBuilderServiceImplTest`), but these are absent on main.
- No MockMvc, repository, Redis integration, RAG quality, concurrency, frontend unit or E2E suite is tracked on current main.
