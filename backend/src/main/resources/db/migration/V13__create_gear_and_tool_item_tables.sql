-- JOINED inheritance requires a subtype table even when the subclass has no extra columns.
-- Weapon/Armor already have weapons/armor; gear and tool were only inserted into items.

CREATE TABLE public.gear_items (
    item_id BIGINT PRIMARY KEY REFERENCES public.items(id) ON DELETE CASCADE
);

CREATE TABLE public.tool_items (
    item_id BIGINT PRIMARY KEY REFERENCES public.items(id) ON DELETE CASCADE
);

INSERT INTO public.gear_items (item_id)
SELECT id FROM public.items WHERE item_type = 'gear';

INSERT INTO public.tool_items (item_id)
SELECT id FROM public.items WHERE item_type = 'tool';
