-- New item categories for homebrew jewelry and clothing
ALTER TYPE item_type ADD VALUE IF NOT EXISTS 'jewelry';
ALTER TYPE item_type ADD VALUE IF NOT EXISTS 'clothing';

CREATE TYPE item_rarity AS ENUM (
    'common',
    'uncommon',
    'rare',
    'very_rare',
    'legendary',
    'artifact'
);

ALTER TABLE items
    ADD COLUMN rarity item_rarity NOT NULL DEFAULT 'common',
    ADD COLUMN is_magical BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN api_index VARCHAR(100);

CREATE UNIQUE INDEX uq_items_api_index ON items (api_index) WHERE api_index IS NOT NULL;

-- Rarity multipliers can push costs above SMALLINT; widen value column
ALTER TABLE items
    ALTER COLUMN value TYPE INTEGER USING value::INTEGER;

CREATE TABLE jewelry_items (
    item_id BIGINT PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE
);

CREATE TABLE clothing_items (
    item_id BIGINT PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE
);
