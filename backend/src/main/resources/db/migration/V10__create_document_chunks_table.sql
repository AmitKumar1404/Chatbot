CREATE TABLE document_chunks (

    id BIGSERIAL PRIMARY KEY,

    document_id BIGINT NOT NULL,

    chunk_index INT NOT NULL,

    content TEXT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document_chunks_document
        FOREIGN KEY (document_id)
        REFERENCES documents(id)
        ON DELETE CASCADE
);