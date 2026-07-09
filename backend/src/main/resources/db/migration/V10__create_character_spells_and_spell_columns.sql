ALTER TABLE spells
    ADD COLUMN duration VARCHAR(100),
    ADD COLUMN higher_level TEXT;

CREATE TABLE character_spells (
    character_id BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    spell_id     BIGINT NOT NULL REFERENCES spells(id) ON DELETE CASCADE,
    PRIMARY KEY (character_id, spell_id)
);

CREATE INDEX idx_character_spells_character ON character_spells(character_id);
