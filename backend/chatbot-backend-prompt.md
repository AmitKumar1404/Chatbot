# ChatGPT-like AI Chatbot Backend — Production-Ready (100% Free)

You are a senior Java Spring Boot architect.
I already have a Spring Boot project (Java 17, Maven) with basic setup done.
Your task is to generate a complete production-ready backend for a ChatGPT-like AI chatbot.
Follow ALL instructions strictly.

---

## 🎯 GOAL

Build a scalable chatbot backend with:

- JWT Authentication
- Chat API
- AI integration (**Google Gemini FREE API** + Ollama switchable)
- H2 in-memory DB (dev) / PostgreSQL (prod) — switchable via config
- Clean architecture (Controller → Service → Repository)

---

## 📁 REQUIRED STRUCTURE

```
com.chatbot
├── config
├── controller
├── service
├── client
├── repository
├── model
├── dto
├── security
```

---

## ⚙️ REQUIREMENTS

### 1. Entities (JPA)

Create:

- `User` (id, username, password)
- `ChatSession` (id, userId, title, createdAt)
- `Message` (id, sessionId, userMessage, aiResponse, timestamp)

### 2. Repositories

- `UserRepository` (findByUsername)
- `ChatSessionRepository` (findByUserId)
- `MessageRepository` (findBySessionId, ordered by timestamp)

### 3. DTOs

- `ChatRequest` (message, sessionId — nullable for new session)
- `ChatResponse` (reply, sessionId)
- `LoginRequest` (username, password)
- `RegisterRequest` (username, password)
- `AuthResponse` (token, username)

### 4. Security (JWT-based)

Implement:

- `JwtUtil` (generate + validate token, 24h expiry)
- `JwtFilter` (intercept requests, extract user)
- `SecurityConfig`

Rules:

- `/auth/**` → public
- `/chat/**` → secured
- `/h2-console/**` → public (dev only)
- Disable CSRF
- Stateless session
- Allow H2 console frames

### 5. Authentication Flow

- `POST /auth/register` → save user, return `AuthResponse`
- `POST /auth/login` → validate credentials, return JWT token in `AuthResponse`
- Encrypt password using BCrypt
- Return proper HTTP 401 on bad credentials

### 6. AI Layer — FREE PROVIDERS ONLY

Create abstraction:

**`AIService` interface:**
```java
String chat(String userMessage);
```

**`AIServiceFactory`** — decides provider based on `app.ai.provider` config value:
- `gemini` → `GeminiClient`
- `ollama` → `OllamaClient`

**`GeminiClient`** (PRIMARY — FREE):
- Use Google Gemini API (free tier: 1500 requests/day)
- Endpoint: `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key={apiKey}`
- Use WebClient with POST
- Request body:
```json
{
  "contents": [{ "parts": [{ "text": "user message here" }] }]
}
```
- Extract from: `candidates[0].content.parts[0].text`
- Return only clean text

**`OllamaClient`** (SECONDARY — FREE, local):
- Endpoint: `http://localhost:11434/api/generate`
- Model: `llama3.2` (configurable)
- Request body:
```json
{ "model": "llama3.2", "prompt": "user message", "stream": false }
```
- Extract from: `response` field
- Return only clean text

**IMPORTANT:**
- Both clients use `WebClient`
- Return only clean AI text string, not raw JSON
- Handle API errors gracefully (return "AI service error. Please try again." on failure)

### 7. Chat Flow

**Controller:** `POST /chat`

Flow:
```
User Request → ChatController → ChatService → AIServiceFactory → Client → Response
```

Also:
- If `sessionId` is null → create new `ChatSession`, return new sessionId
- Save both userMessage and aiResponse in `Message` entity
- Return `ChatResponse` (reply + sessionId)

**Additional endpoints:**
- `GET /chat/sessions` → list all sessions for logged-in user
- `GET /chat/sessions/{sessionId}/messages` → get message history for a session

### 8. Configuration (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:chatbotdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false

app:
  jwt:
    secret: your-256-bit-secret-key-change-this-in-production-must-be-long
    expiration: 86400000  # 24 hours in ms
  ai:
    provider: gemini  # Options: gemini, ollama
    gemini:
      api-key: YOUR_GEMINI_API_KEY_HERE   # Get free at: https://aistudio.google.com/
      model: gemini-1.5-flash
    ollama:
      base-url: http://localhost:11434
      model: llama3.2
```

**Also create `application-prod.yml`** for PostgreSQL override:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/chatbotdb
    username: postgres
    password: yourpassword
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
```

### 9. WebClient Config

Create reusable `WebClientConfig` bean with:
- Default codecs with max in-memory size 10MB
- No base URL (each client sets its own)

### 10. CORS Config

Create `CorsConfig`:
- Allow all origins (`*`) for dev
- Allow methods: GET, POST, PUT, DELETE, OPTIONS
- Allow all headers
- Allow credentials: false

### 11. Exception Handling

Create `GlobalExceptionHandler` with `@RestControllerAdvice`:
- Handle `UsernameNotFoundException` → 404
- Handle `BadCredentialsException` → 401
- Handle `RuntimeException` → 400
- Generic fallback → 500
- Return consistent `ErrorResponse` DTO (message, status, timestamp)

### 12. `pom.xml` Dependencies

Include:
- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-webflux` (for WebClient)
- `h2` (runtime, scope: runtime)
- `postgresql` (runtime, scope: runtime)
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (version 0.12.3)
- `lombok`
- `spring-boot-starter-validation`

---

## ⚠️ IMPORTANT RULES

- Do NOT skip any file
- Do NOT leave TODOs
- Code must be runnable out of the box (H2 dev mode, no external DB needed)
- Use proper imports
- Follow Spring Boot 3.x standards
- Use `io.jsonwebtoken` (JJWT 0.12.x) — NOT deprecated methods
- Use `SecurityContextHolder` for getting current user
- Use constructor injection (no `@Autowired` on fields)
- Never hardcode secrets

---

## 🚀 OUTPUT FORMAT

Generate ALL of the following files with complete working code:

**Config:**
- `WebClientConfig.java`
- `CorsConfig.java`

**Security:**
- `JwtUtil.java`
- `JwtFilter.java`
- `SecurityConfig.java`

**Model:**
- `User.java`
- `ChatSession.java`
- `Message.java`

**Repository:**
- `UserRepository.java`
- `ChatSessionRepository.java`
- `MessageRepository.java`

**DTO:**
- `ChatRequest.java`
- `ChatResponse.java`
- `LoginRequest.java`
- `RegisterRequest.java`
- `AuthResponse.java`
- `ErrorResponse.java`

**Service:**
- `AIService.java` (interface)
- `AIServiceFactory.java`
- `ChatService.java`
- `UserService.java`

**Client:**
- `GeminiClient.java`
- `OllamaClient.java`

**Controller:**
- `AuthController.java`
- `ChatController.java`

**Exception:**
- `GlobalExceptionHandler.java`

**Resources:**
- `application.yml`
- `application-prod.yml`

**Maven:**
- `pom.xml` (complete)

---

## 💡 FREE SETUP GUIDE (include as comments in application.yml)

```
# HOW TO GET FREE GEMINI API KEY:
# 1. Go to https://aistudio.google.com/
# 2. Sign in with Google account
# 3. Click "Get API Key" → "Create API Key"
# 4. Copy key and paste above in app.ai.gemini.api-key
# 5. Free tier: 1500 requests/day, 1M tokens/day — enough for personal use
#
# HOW TO USE OLLAMA (100% local, no API key needed):
# 1. Install: https://ollama.com/download
# 2. Run: ollama pull llama3.2
# 3. Start: ollama serve
# 4. Change app.ai.provider to: ollama
```

---

Now generate the full backend code. No explanations. No comments outside code. All files complete.
