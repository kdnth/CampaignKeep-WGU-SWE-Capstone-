ALTER TABLE users
    ADD COLUMN email_verified_at TIMESTAMP;

CREATE TABLE account_tokens (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type         VARCHAR(32) NOT NULL,
    token_hash   VARCHAR(64) NOT NULL UNIQUE,
    expires_at   TIMESTAMP NOT NULL,
    consumed_at  TIMESTAMP,
    created_on   TIMESTAMP NOT NULL DEFAULT now(),
    updated_on   TIMESTAMP
);

CREATE INDEX idx_account_tokens_user_type ON account_tokens(user_id, type);
