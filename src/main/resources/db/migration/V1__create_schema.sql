--
-- Name: damage_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.damage_type AS ENUM (
    'slashing',
    'piercing',
    'bludgeoning',
    'cold',
    'acid',
    'fire',
    'force',
    'lightning',
    'necrotic',
    'poison',
    'psychic',
    'radiant',
    'thunder'
);


--
-- Name: item_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.item_type AS ENUM (
    'weapon',
    'armor',
    'tool',
    'gear'
);


--
-- Name: magic_school; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.magic_school AS ENUM (
    'abjuration',
    'conjuration',
    'divination',
    'enchantment',
    'evocation',
    'illusion',
    'necromancy',
    'transmutation'
);


--
-- Name: spell_range_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.spell_range_type AS ENUM (
    'self',
    'touch',
    'ranged',
    'special',
    'sight',
    'unlimited'
);


--
-- Name: weapon_range; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.weapon_range AS ENUM (
    'melee',
    'ranged'
);

--
-- Name: ability; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ability (
    id bigint NOT NULL,
    name character varying(25)
);


--
-- Name: ability_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.ability ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.ability_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: armor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.armor (
    item_id bigint NOT NULL,
    armor_category_id bigint NOT NULL,
    base_ac smallint NOT NULL,
    max_dex_bonus smallint,
    str_minimum smallint
);


--
-- Name: armor_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.armor_category (
    id bigint NOT NULL,
    name character varying(50)
);


--
-- Name: armor_category_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.armor_category ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.armor_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: background; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.background (
    id bigint NOT NULL,
    name character varying(50),
    description character varying(1000)
);


--
-- Name: background_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.background ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.background_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: class; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.class (
    id bigint NOT NULL,
    name character varying(25),
    hit_dice smallint,
    description character varying(1000),
    index character varying(50)
);


--
-- Name: class_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.class ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.class_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.item (
    id bigint NOT NULL,
    item_type public.item_type NOT NULL,
    name character varying(75),
    description character varying(500),
    value smallint,
    weight smallint
);


--
-- Name: item_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.item ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: language; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.language (
    id bigint NOT NULL,
    name character varying(25),
    index character varying(25) NOT NULL
);


--
-- Name: language_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.language ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.language_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: race; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.race (
    id bigint NOT NULL,
    name character varying(25),
    age_desc character varying(1000),
    alignment_desc character varying(1000),
    language_desc character varying(1000),
    size character varying(25),
    size_desc character varying(1000),
    speed smallint,
    index character varying(25)
);


--
-- Name: race_ability_point_bonus; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.race_ability_point_bonus (
    race_id bigint NOT NULL,
    ability_id bigint NOT NULL,
    value smallint NOT NULL
);


--
-- Name: race_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.race ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.race_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: skill; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.skill (
    id bigint NOT NULL,
    ability_id bigint,
    name character varying(50)
);


--
-- Name: skill_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.skill ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.skill_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: spell; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.spell (
    id bigint NOT NULL,
    api_index character varying(100),
    name character varying(100) NOT NULL,
    level smallint NOT NULL,
    casting_time character varying(50),
    range_type public.spell_range_type NOT NULL,
    range_feet smallint,
    components character varying(10),
    description text,
    school public.magic_school NOT NULL,
    concentration boolean DEFAULT false NOT NULL,
    has_verbal boolean DEFAULT false NOT NULL,
    has_somatic boolean DEFAULT false NOT NULL,
    has_material boolean DEFAULT false NOT NULL,
    material_desc text,
    ritual boolean DEFAULT false NOT NULL
);


--
-- Name: spell_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.spell ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.spell_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: subrace; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subrace (
    id bigint NOT NULL,
    race_id bigint,
    name character varying(50),
    description character varying(1000)
);


--
-- Name: subrace_ability_point_bonus; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.subrace_ability_point_bonus (
    subrace_id bigint NOT NULL,
    ability_id bigint NOT NULL,
    value smallint NOT NULL
);


--
-- Name: subrace_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.subrace ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.subrace_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: weapon; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.weapon (
    item_id bigint NOT NULL,
    weapon_category_id bigint,
    damage character varying(10),
    damage_type public.damage_type,
    range smallint,
    weapon_range public.weapon_range
);


--
-- Name: weapon_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.weapon_category (
    id bigint NOT NULL,
    name character varying(50)
);


--
-- Name: weapon_category_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.weapon_category ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.weapon_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: ability ability_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ability
    ADD CONSTRAINT ability_pkey PRIMARY KEY (id);


--
-- Name: armor_category armor_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.armor_category
    ADD CONSTRAINT armor_category_pkey PRIMARY KEY (id);


--
-- Name: armor armor_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.armor
    ADD CONSTRAINT armor_pkey PRIMARY KEY (item_id);


--
-- Name: background background_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.background
    ADD CONSTRAINT background_pkey PRIMARY KEY (id);


--
-- Name: class class_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.class
    ADD CONSTRAINT class_pkey PRIMARY KEY (id);


--
-- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- Name: language language_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.language
    ADD CONSTRAINT language_pkey PRIMARY KEY (id);


--
-- Name: race_ability_point_bonus race_ability_point_bonus_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.race_ability_point_bonus
    ADD CONSTRAINT race_ability_point_bonus_pkey PRIMARY KEY (race_id, ability_id, value);


--
-- Name: race race_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.race
    ADD CONSTRAINT race_pkey PRIMARY KEY (id);


--
-- Name: skill skill_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill
    ADD CONSTRAINT skill_pkey PRIMARY KEY (id);


--
-- Name: spell spell_api_index_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spell
    ADD CONSTRAINT spell_api_index_key UNIQUE (api_index);


--
-- Name: spell spell_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.spell
    ADD CONSTRAINT spell_pkey PRIMARY KEY (id);


--
-- Name: subrace_ability_point_bonus subrace_ability_point_bonus_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subrace_ability_point_bonus
    ADD CONSTRAINT subrace_ability_point_bonus_pkey PRIMARY KEY (subrace_id, ability_id, value);


--
-- Name: subrace subrace_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subrace
    ADD CONSTRAINT subrace_pkey PRIMARY KEY (id);


--
-- Name: weapon_category weapon_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.weapon_category
    ADD CONSTRAINT weapon_category_pkey PRIMARY KEY (id);


--
-- Name: weapon weapon_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.weapon
    ADD CONSTRAINT weapon_pkey PRIMARY KEY (item_id);


--
-- Name: armor armor_armor_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.armor
    ADD CONSTRAINT armor_armor_category_id_fkey FOREIGN KEY (armor_category_id) REFERENCES public.armor_category(id);


--
-- Name: armor armor_item_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.armor
    ADD CONSTRAINT armor_item_id_fkey FOREIGN KEY (item_id) REFERENCES public.item(id);


--
-- Name: race_ability_point_bonus race_ability_point_bonus_ability_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.race_ability_point_bonus
    ADD CONSTRAINT race_ability_point_bonus_ability_id_fkey FOREIGN KEY (ability_id) REFERENCES public.ability(id);


--
-- Name: race_ability_point_bonus race_ability_point_bonus_race_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.race_ability_point_bonus
    ADD CONSTRAINT race_ability_point_bonus_race_id_fkey FOREIGN KEY (race_id) REFERENCES public.race(id);


--
-- Name: skill skill_ability_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.skill
    ADD CONSTRAINT skill_ability_id_fkey FOREIGN KEY (ability_id) REFERENCES public.ability(id);


--
-- Name: subrace_ability_point_bonus subrace_ability_point_bonus_ability_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subrace_ability_point_bonus
    ADD CONSTRAINT subrace_ability_point_bonus_ability_id_fkey FOREIGN KEY (ability_id) REFERENCES public.ability(id);


--
-- Name: subrace_ability_point_bonus subrace_ability_point_bonus_subrace_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subrace_ability_point_bonus
    ADD CONSTRAINT subrace_ability_point_bonus_subrace_id_fkey FOREIGN KEY (subrace_id) REFERENCES public.subrace(id);


--
-- Name: subrace subrace_race_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.subrace
    ADD CONSTRAINT subrace_race_id_fkey FOREIGN KEY (race_id) REFERENCES public.race(id);


--
-- Name: weapon weapon_item_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.weapon
    ADD CONSTRAINT weapon_item_id_fkey FOREIGN KEY (item_id) REFERENCES public.item(id);


--
-- Name: weapon weapon_weapon_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.weapon
    ADD CONSTRAINT weapon_weapon_category_id_fkey FOREIGN KEY (weapon_category_id) REFERENCES public.weapon_category(id);


