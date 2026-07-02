CREATE TYPE campaign_role AS ENUM ('master', 'player');

CREATE TABLE campaigns (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(75) NOT NULL,
    description VARCHAR(3000),
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    updated_on TIMESTAMP NOT NULL DEFAULT now(),
    finished_on TIMESTAMP
);

CREATE TABLE campaign_members (
    campaign_id BIGINT NOT NULL REFERENCES campaigns(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    role campaign_role NOT NULL,
    PRIMARY KEY (campaign_id, user_id)
);