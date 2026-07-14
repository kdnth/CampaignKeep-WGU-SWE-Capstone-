CREATE TYPE equipment_slot AS ENUM ('armor', 'shield', 'main_hand', 'off_hand');

ALTER TABLE characters
    ADD COLUMN gold INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN starting_equipment_chosen BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE items
    ADD COLUMN campaign_id BIGINT REFERENCES campaigns(id) ON DELETE CASCADE,
    ADD COLUMN created_by_user_id BIGINT REFERENCES users(id);

CREATE TABLE character_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    character_id BIGINT NOT NULL REFERENCES characters(id) ON DELETE CASCADE,
    item_id BIGINT NOT NULL REFERENCES items(id),
    quantity SMALLINT NOT NULL DEFAULT 1 CHECK (quantity >= 1),
    equipped_slot equipment_slot NULL
);

CREATE UNIQUE INDEX uq_character_equipped_slot
    ON character_items (character_id, equipped_slot)
    WHERE equipped_slot IS NOT NULL;

CREATE TABLE class_starting_equipment_options (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id),
    item_id BIGINT NOT NULL REFERENCES items(id),
    option_group VARCHAR(30) NOT NULL,
    sort_order SMALLINT NOT NULL DEFAULT 0
);

-- Gear / tool items for grants
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (41, 'gear', 'Arrows (20)', 'Twenty arrows for a quiver.', 1, 1);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (42, 'tool', 'Thieves'' Tools', 'A set of tools for picking locks and disarming traps.', 25, 1);

-- Fighter (class 1): pool IDs 1-20
INSERT INTO class_starting_equipment_options (class_id, item_id, option_group, sort_order) VALUES
    (1, 4, 'weapon', 1),
    (1, 5, 'weapon', 2),
    (1, 6, 'weapon', 3),
    (1, 3, 'weapon', 4),
    (1, 17, 'armor', 1),
    (1, 9, 'armor', 2),
    (1, 11, 'armor', 3),
    (1, 20, 'shield', 1);

-- Wizard (class 2): pool IDs 21-40
INSERT INTO class_starting_equipment_options (class_id, item_id, option_group, sort_order) VALUES
    (2, 22, 'weapon', 1),
    (2, 21, 'weapon', 2);

-- Rogue (class 3): pool IDs 21-40
INSERT INTO class_starting_equipment_options (class_id, item_id, option_group, sort_order) VALUES
    (3, 26, 'weapon', 1),
    (3, 25, 'weapon', 2),
    (3, 27, 'weapon', 3),
    (3, 29, 'armor', 1),
    (3, 30, 'armor', 2);
