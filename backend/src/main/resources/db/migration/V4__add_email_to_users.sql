ALTER TABLE users ADD COLUMN email VARCHAR(255) NULL;

CREATE UNIQUE INDEX idx_users_email
ON users(email)
WHERE email IS NOT NULL;
