-- file_hash is SHA-256 hex of the original PDF bytes (see DocumentServiceImpl.calculateSha256).
-- It cannot be derived from existing documents columns (file_name, stored_file_name, etc.).
-- Do not invent placeholder hashes.

ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS file_hash VARCHAR(64);

-- PostgreSQL UNIQUE allows multiple NULLs, so this is safe before NOT NULL.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_documents_file_hash'
    ) THEN
        ALTER TABLE documents
            ADD CONSTRAINT uk_documents_file_hash UNIQUE (file_hash);
    END IF;
END $$;

-- Enforce NOT NULL only when every row already has a real hash (true for empty CI DB).
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM documents WHERE file_hash IS NULL) THEN
        RAISE EXCEPTION
            'V12 cannot set documents.file_hash NOT NULL: existing row(s) have NULL file_hash and SHA-256 cannot be recovered from database columns. Backfill file_hash from the original PDF bytes on disk, or remove those rows, then re-run migrations.';
    END IF;
END $$;

ALTER TABLE documents
    ALTER COLUMN file_hash SET NOT NULL;
