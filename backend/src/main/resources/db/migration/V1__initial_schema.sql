CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE chat_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_chat_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    user_message VARCHAR(10000) NOT NULL,
    ai_response VARCHAR(10000) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    user_bubble_client_id VARCHAR(128),
    assistant_bubble_client_id VARCHAR(128),
    generation_complete BOOLEAN NOT NULL,
    CONSTRAINT fk_messages_session
        FOREIGN KEY (session_id)
        REFERENCES chat_sessions(id)
);