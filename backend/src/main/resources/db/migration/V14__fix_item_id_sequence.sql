-- V12 seeded items 41–42 with OVERRIDING SYSTEM VALUE but left item_id_seq at 40.
-- New inserts then collided with those rows (duplicate item_pkey).
SELECT setval(
    pg_get_serial_sequence('items', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM items),
    true
);
