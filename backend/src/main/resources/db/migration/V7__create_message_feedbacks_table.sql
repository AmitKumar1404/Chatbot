CREATE TABLE message_feedbacks (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    feedback_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_message_feedbacks_message
        FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_message_feedbacks_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT uk_message_feedbacks_message_user
        UNIQUE (message_id, user_id),
    CONSTRAINT ck_message_feedbacks_type
        CHECK (feedback_type IN ('HELPFUL', 'NOT_HELPFUL'))
);

CREATE INDEX idx_message_feedbacks_user_id
    ON message_feedbacks(user_id);
