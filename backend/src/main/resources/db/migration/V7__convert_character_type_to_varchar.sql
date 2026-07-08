ALTER TABLE characters
    ALTER COLUMN character_type TYPE VARCHAR(3)
        USING character_type::text;

DROP TYPE character_type;