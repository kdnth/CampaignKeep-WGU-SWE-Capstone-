--
-- Data for Name: abilities; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.abilities OVERRIDING SYSTEM VALUE VALUES (1, 'CHA');
INSERT INTO public.abilities OVERRIDING SYSTEM VALUE VALUES (2, 'CON');
INSERT INTO public.abilities OVERRIDING SYSTEM VALUE VALUES (3, 'DEX');
INSERT INTO public.abilities OVERRIDING SYSTEM VALUE VALUES (4, 'INT');
INSERT INTO public.abilities OVERRIDING SYSTEM VALUE VALUES (5, 'STR');
INSERT INTO public.abilities OVERRIDING SYSTEM VALUE VALUES (6, 'WIS');


--
-- Data for Name: armor_categories; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.armor_categories OVERRIDING SYSTEM VALUE VALUES (1, 'light');
INSERT INTO public.armor_categories OVERRIDING SYSTEM VALUE VALUES (2, 'medium');
INSERT INTO public.armor_categories OVERRIDING SYSTEM VALUE VALUES (3, 'heavy');
INSERT INTO public.armor_categories OVERRIDING SYSTEM VALUE VALUES (4, 'shield');


--
-- Data for Name: items; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (1, 'weapon', 'Dagger', NULL, 2, 1);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (2, 'weapon', 'Quarterstaff', NULL, 0, 4);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (3, 'weapon', 'Light Crossbow', NULL, 25, 5);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (4, 'weapon', 'Longsword', NULL, 15, 3);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (5, 'weapon', 'Shortsword', NULL, 10, 2);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (6, 'weapon', 'Rapier', NULL, 25, 2);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (7, 'weapon', 'Hand Crossbow', NULL, 75, 3);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (8, 'armor', 'Padded Armor', NULL, 5, 8);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (9, 'armor', 'Leather Armor', NULL, 10, 10);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (10, 'armor', 'Studded Leather Armor', NULL, 45, 13);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (11, 'armor', 'Hide Armor', NULL, 10, 12);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (12, 'armor', 'Chain Shirt', NULL, 50, 20);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (13, 'armor', 'Scale Mail', NULL, 50, 45);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (14, 'armor', 'Breastplate', NULL, 400, 20);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (15, 'armor', 'Half Plate Armor', NULL, 750, 40);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (16, 'armor', 'Ring Mail', NULL, 30, 40);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (17, 'armor', 'Chain Mail', NULL, 75, 55);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (18, 'armor', 'Splint Armor', NULL, 200, 60);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (19, 'armor', 'Plate Armor', NULL, 1500, 65);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (20, 'armor', 'Shield', NULL, 10, 6);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (21, 'weapon', 'Dagger', NULL, 2, 1);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (22, 'weapon', 'Quarterstaff', NULL, 0, 4);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (23, 'weapon', 'Light Crossbow', NULL, 25, 5);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (24, 'weapon', 'Longsword', NULL, 15, 3);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (25, 'weapon', 'Shortsword', NULL, 10, 2);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (26, 'weapon', 'Rapier', NULL, 25, 2);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (27, 'weapon', 'Hand Crossbow', NULL, 75, 3);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (28, 'armor', 'Padded Armor', NULL, 5, 8);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (29, 'armor', 'Leather Armor', NULL, 10, 10);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (30, 'armor', 'Studded Leather Armor', NULL, 45, 13);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (31, 'armor', 'Hide Armor', NULL, 10, 12);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (32, 'armor', 'Chain Shirt', NULL, 50, 20);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (33, 'armor', 'Scale Mail', NULL, 50, 45);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (34, 'armor', 'Breastplate', NULL, 400, 20);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (35, 'armor', 'Half Plate Armor', NULL, 750, 40);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (36, 'armor', 'Ring Mail', NULL, 30, 40);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (37, 'armor', 'Chain Mail', NULL, 75, 55);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (38, 'armor', 'Splint Armor', NULL, 200, 60);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (39, 'armor', 'Plate Armor', NULL, 1500, 65);
INSERT INTO public.items OVERRIDING SYSTEM VALUE VALUES (40, 'armor', 'Shield', NULL, 10, 6);


--
-- Data for Name: armor; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.armor VALUES (30, 1, 12, NULL, NULL);
INSERT INTO public.armor VALUES (29, 1, 11, NULL, NULL);
INSERT INTO public.armor VALUES (28, 1, 11, NULL, NULL);
INSERT INTO public.armor VALUES (10, 1, 12, NULL, NULL);
INSERT INTO public.armor VALUES (9, 1, 11, NULL, NULL);
INSERT INTO public.armor VALUES (8, 1, 11, NULL, NULL);
INSERT INTO public.armor VALUES (35, 2, 15, 2, NULL);
INSERT INTO public.armor VALUES (34, 2, 14, 2, NULL);
INSERT INTO public.armor VALUES (33, 2, 14, 2, NULL);
INSERT INTO public.armor VALUES (32, 2, 13, 2, NULL);
INSERT INTO public.armor VALUES (31, 2, 12, 2, NULL);
INSERT INTO public.armor VALUES (15, 2, 15, 2, NULL);
INSERT INTO public.armor VALUES (14, 2, 14, 2, NULL);
INSERT INTO public.armor VALUES (13, 2, 14, 2, NULL);
INSERT INTO public.armor VALUES (12, 2, 13, 2, NULL);
INSERT INTO public.armor VALUES (11, 2, 12, 2, NULL);
INSERT INTO public.armor VALUES (39, 3, 18, 0, 15);
INSERT INTO public.armor VALUES (38, 3, 17, 0, 15);
INSERT INTO public.armor VALUES (37, 3, 16, 0, 13);
INSERT INTO public.armor VALUES (36, 3, 14, 0, NULL);
INSERT INTO public.armor VALUES (19, 3, 18, 0, 15);
INSERT INTO public.armor VALUES (18, 3, 17, 0, 15);
INSERT INTO public.armor VALUES (17, 3, 16, 0, 13);
INSERT INTO public.armor VALUES (16, 3, 14, 0, NULL);
INSERT INTO public.armor VALUES (40, 4, 2, 0, NULL);
INSERT INTO public.armor VALUES (20, 4, 2, 0, NULL);


--
-- Data for Name: backgrounds; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.backgrounds OVERRIDING SYSTEM VALUE VALUES (1, 'Acolyte', 'You have spent your life in the service of a temple to a specific god or pantheon of gods. You act as an intermediary between the realm of the holy and the mortal world, performing sacred rites and offering sacrifices in order to conduct worshipers into the presence of the divine. You are not necessarily a cleric—performing sacred rites is not the same thing as channeling divine power.');


--
-- Data for Name: classes; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.classes OVERRIDING SYSTEM VALUE VALUES (1, 'Fighter', 10, 'fighters all share an unparalleled prowess with weapons and armor, and are well acquaintied with death, both meting it out and defying it', 'fighter');
INSERT INTO public.classes OVERRIDING SYSTEM VALUE VALUES (2, 'Wizard', 6, 'Wizards cast spells of explosive fire, arcing lightning, subtle deception, and spectacular transformations', 'wizard');
INSERT INTO public.classes OVERRIDING SYSTEM VALUE VALUES (3, 'Rogue', 8, 'Rogues have a knack for finding the solution to just about any problem, prioritizing subtle strikes over brute strength', 'rogue');


--
-- Data for Name: languages; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (1, 'Abyssal', 'abyssal');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (2, 'Celestial', 'celestial');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (3, 'Common', 'common');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (4, 'Deep Speech', 'deep-speech');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (5, 'Dwarvish', 'dwarvish');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (6, 'Elvish', 'elvish');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (7, 'Gnomish', 'gnomish');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (8, 'Goblin', 'goblin');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (9, 'Halfling', 'halfling');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (10, 'Infernal', 'infernal');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (11, 'Orc', 'orc');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (12, 'Primordial', 'primordial');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (13, 'Sylvan', 'sylvan');
INSERT INTO public.languages OVERRIDING SYSTEM VALUE VALUES (14, 'Undercommon', 'undercommon');


--
-- Data for Name: races; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.races OVERRIDING SYSTEM VALUE VALUES (1, 'Dwarf', 'Dwarves mature at the same rate as humans, but they''re considered young until they reach the age of 50. On average, they live about 350 years.', 'Most dwarves are lawful, believing firmly in the benefits of a well-ordered society. They tend toward good as well, with a strong sense of fair play and a belief that everyone deserves to share in the benefits of a just order.', 'You can speak, read, and write Common and Dwarvish. Dwarvish is full of hard consonants and guttural sounds, and those characteristics spill over into whatever other language a dwarf might speak.', 'Medium', 'Dwarves stand between 4 and 5 feet tall and average about 150 pounds. Your size is Medium.', 25, 'dwarf');
INSERT INTO public.races OVERRIDING SYSTEM VALUE VALUES (2, 'Elf', 'Although elves reach physical maturity at about the same age as humans, the elven understanding of adulthood goes beyond physical growth to encompass worldly experience. An elf typically claims adulthood and an adult name around the age of 100 and can live to be 750 years old.', 'Elves love freedom, variety, and self-expression, so they lean strongly toward the gentler aspects of chaos. They value and protect others'' freedom as well as their own, and they are more often good than not.', 'You can speak, read, and write Common and Elvish. Elvish is fluid, with subtle intonations and intricate grammar. Elven literature is rich and varied, and their songs and poems are famous among other races. Many bards learn their language so they can add Elvish ballads to their repertoires.', 'Medium', 'Elves range from under 5 to over 6 feet tall and have slender builds. Your size is Medium.', 30, 'elf');
INSERT INTO public.races OVERRIDING SYSTEM VALUE VALUES (3, 'Human', 'Humans reach adulthood in their late teens and live less than a century.', 'Humans tend toward no particular alignment. The best and the worst are found among them.', 'You can speak, read, and write Common and one extra language of your choice. Humans typically learn the languages of other peoples they deal with, including obscure dialects. They are fond of sprinkling their speech with words borrowed from other tongues: Orc curses, Elvish musical expressions, Dwarvish military phrases, and so on.', 'Medium', 'Humans vary widely in height and build, from barely 5 feet to well over 6 feet tall. Regardless of your position in that range, your size is Medium.', 30, 'human');


--
-- Data for Name: race_ability_point_bonuses; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.race_ability_point_bonuses VALUES (2, 3, 2);
INSERT INTO public.race_ability_point_bonuses VALUES (1, 2, 2);
INSERT INTO public.race_ability_point_bonuses VALUES (3, 1, 1);
INSERT INTO public.race_ability_point_bonuses VALUES (3, 2, 1);
INSERT INTO public.race_ability_point_bonuses VALUES (3, 3, 1);
INSERT INTO public.race_ability_point_bonuses VALUES (3, 4, 1);
INSERT INTO public.race_ability_point_bonuses VALUES (3, 5, 1);
INSERT INTO public.race_ability_point_bonuses VALUES (3, 6, 1);


--
-- Data for Name: skills; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (1, 5, 'Athletics');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (2, 3, 'Acrobatics');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (3, 3, 'Sleight of Hand');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (4, 3, 'Stealth');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (5, 4, 'Arcana');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (6, 4, 'History');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (7, 4, 'Investigation');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (8, 4, 'Nature');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (9, 4, 'Religion');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (10, 6, 'Animal Handling');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (11, 6, 'Insight');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (12, 6, 'Medicine');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (13, 6, 'Perception');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (14, 6, 'Survival');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (15, 1, 'Deception');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (16, 1, 'Intimidation');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (17, 1, 'Performace');
INSERT INTO public.skills OVERRIDING SYSTEM VALUE VALUES (18, 1, 'Persuasion');


--
-- Data for Name: spells; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: subraces; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.subraces OVERRIDING SYSTEM VALUE VALUES (1, 2, 'High Elf', 'As a high elf, you have a keen mind and a mastery of at least the basics of magic. In many fantasy gaming worlds, there are two kinds of high elves. One type is haughty and reclusive, believing themselves to be superior to non-elves and even other elves. The other type is more common and more friendly, and often encountered among humans and other races.');
INSERT INTO public.subraces OVERRIDING SYSTEM VALUE VALUES (2, 1, 'Hill Dwarf', 'As a hill dwarf, you have keen senses, deep intuition, and remarkable resilience.');


--
-- Data for Name: subrace_ability_point_bonuses; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.subrace_ability_point_bonuses VALUES (1, 4, 1);
INSERT INTO public.subrace_ability_point_bonuses VALUES (2, 6, 1);


--
-- Data for Name: weapon_categories; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.weapon_categories OVERRIDING SYSTEM VALUE VALUES (1, 'simple');
INSERT INTO public.weapon_categories OVERRIDING SYSTEM VALUE VALUES (2, 'martial');


--
-- Data for Name: weapons; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.weapons VALUES (23, 1, '1d8', 'piercing', 80, 'ranged');
INSERT INTO public.weapons VALUES (22, 1, '1d6', 'bludgeoning', 5, 'melee');
INSERT INTO public.weapons VALUES (21, 1, '1d4', 'piercing', 5, 'melee');
INSERT INTO public.weapons VALUES (3, 1, '1d8', 'piercing', 80, 'ranged');
INSERT INTO public.weapons VALUES (2, 1, '1d6', 'bludgeoning', 5, 'melee');
INSERT INTO public.weapons VALUES (1, 1, '1d4', 'piercing', 5, 'melee');
INSERT INTO public.weapons VALUES (27, 2, '1d6', 'piercing', 30, 'ranged');
INSERT INTO public.weapons VALUES (26, 2, '1d8', 'piercing', 5, 'melee');
INSERT INTO public.weapons VALUES (25, 2, '1d6', 'piercing', 5, 'melee');
INSERT INTO public.weapons VALUES (24, 2, '1d8', 'slashing', 5, 'melee');
INSERT INTO public.weapons VALUES (7, 2, '1d6', 'piercing', 30, 'ranged');
INSERT INTO public.weapons VALUES (6, 2, '1d8', 'piercing', 5, 'melee');
INSERT INTO public.weapons VALUES (5, 2, '1d6', 'piercing', 5, 'melee');
INSERT INTO public.weapons VALUES (4, 2, '1d8', 'slashing', 5, 'melee');


--
-- Name: ability_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.ability_id_seq', 6, true);


--
-- Name: armor_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.armor_category_id_seq', 4, true);


--
-- Name: background_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.background_id_seq', 6, true);


--
-- Name: class_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.class_id_seq', 3, true);


--
-- Name: item_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.item_id_seq', 40, true);


--
-- Name: language_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.language_id_seq', 14, true);


--
-- Name: race_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.race_id_seq', 3, true);


--
-- Name: skill_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.skill_id_seq', 18, true);


--
-- Name: spell_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.spell_id_seq', 1, false);


--
-- Name: subrace_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.subrace_id_seq', 2, true);


--
-- Name: weapon_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.weapon_category_id_seq', 2, true);
