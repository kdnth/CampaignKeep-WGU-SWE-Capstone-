CREATE TYPE character_status AS ENUM ('alive', 'down', 'dead');

CREATE TYPE character_type AS ENUM ('NPC', 'PC');

CREATE TABLE characters (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    character_type character_type NOT NULL,
    player_id BIGINT REFERENCES users(id),           -- set for PCs, NULL for NPCs
    created_by_user_id BIGINT REFERENCES users(id),  -- set for NPCs, NULL for PCs
    name VARCHAR(50) NOT NULL,
    race_id BIGINT NOT NULL REFERENCES races(id),
    subrace_id BIGINT REFERENCES subraces(id),
    background_id BIGINT REFERENCES backgrounds(id),
    status character_status NOT NULL DEFAULT 'alive',
    strength SMALLINT NOT NULL,
    dexterity SMALLINT NOT NULL,
    constitution SMALLINT NOT NULL,
    intelligence SMALLINT NOT NULL,
    wisdom SMALLINT NOT NULL,
    charisma SMALLINT NOT NULL,
    hit_points SMALLINT NOT NULL,
    armor_class SMALLINT NOT NULL,
    initiative_bonus SMALLINT NOT NULL,
    speed SMALLINT NOT NULL,
    created_on TIMESTAMP NOT NULL,
    updated_on TIMESTAMP NOT NULL,
    CONSTRAINT chk_character_owner CHECK (
        (player_id IS NOT NULL AND created_by_user_id IS NULL) OR
        (player_id IS NULL AND created_by_user_id IS NOT NULL)
        )
);

CREATE TABLE character_classes (
   character_id BIGINT NOT NULL REFERENCES characters(id),
   class_id BIGINT NOT NULL REFERENCES classes(id),
   PRIMARY KEY (character_id, class_id)
);

CREATE TABLE character_languages (
     character_id BIGINT NOT NULL REFERENCES characters(id),
     language_id BIGINT NOT NULL REFERENCES languages(id),
     PRIMARY KEY (character_id, language_id)
);

CREATE TABLE campaign_nonplayable_characters (
     campaign_id BIGINT NOT NULL REFERENCES campaigns(id),
     character_id BIGINT NOT NULL REFERENCES characters(id),
     PRIMARY KEY (campaign_id, character_id)
);

CREATE TABLE campaign_playable_characters (
      campaign_id BIGINT NOT NULL REFERENCES campaigns(id),
      character_id BIGINT NOT NULL REFERENCES characters(id),
      PRIMARY KEY (campaign_id, character_id)
);