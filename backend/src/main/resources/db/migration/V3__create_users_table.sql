CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(16) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    last_activity TIMESTAMP,
    updated_on TIMESTAMP
);