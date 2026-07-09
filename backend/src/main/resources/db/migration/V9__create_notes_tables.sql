CREATE TABLE notes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(100),
    body VARCHAR(10000) NOT NULL DEFAULT '',
    created_on TIMESTAMP NOT NULL DEFAULT now(),
    updated_on TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE character_notes (
    character_id BIGINT NOT NULL PRIMARY KEY
        REFERENCES characters(id) ON DELETE CASCADE,
    note_id BIGINT NOT NULL UNIQUE
        REFERENCES notes(id) ON DELETE CASCADE
);

CREATE TABLE campaign_master_notes (
    campaign_id BIGINT NOT NULL REFERENCES campaigns(id) ON DELETE CASCADE,
    note_id BIGINT NOT NULL UNIQUE REFERENCES notes(id) ON DELETE CASCADE,
    PRIMARY KEY (campaign_id, note_id)
);

CREATE INDEX idx_campaign_master_notes_campaign
    ON campaign_master_notes(campaign_id);
CREATE INDEX idx_notes_updated_on
    ON notes(updated_on DESC);
