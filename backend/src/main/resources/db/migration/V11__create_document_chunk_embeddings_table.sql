CREATE TABLE document_chunk_embeddings (

    id BIGSERIAL PRIMARY KEY,

    chunk_id BIGINT NOT NULL UNIQUE,

    embedding JSONB NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document_chunk_embedding
        FOREIGN KEY (chunk_id)
        REFERENCES document_chunks(id)
        ON DELETE CASCADE
);