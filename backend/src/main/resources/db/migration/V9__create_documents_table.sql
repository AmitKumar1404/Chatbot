CREATE TABLE documents (

    id BIGSERIAL PRIMARY KEY,

    file_name VARCHAR(255) NOT NULL,

    stored_file_name VARCHAR(255) NOT NULL,

    content_type VARCHAR(100) NOT NULL,

    file_size BIGINT NOT NULL,

    uploaded_by BIGINT NOT NULL,

    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    status VARCHAR(50) NOT NULL DEFAULT 'UPLOADED',

    CONSTRAINT fk_document_user
        FOREIGN KEY (uploaded_by)
        REFERENCES users(id)

);