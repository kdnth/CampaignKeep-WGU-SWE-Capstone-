# Campaign Keep: Database Schema

PostgreSQL schema owned by Flyway (`backend/src/main/resources/db/migration/`). Hibernate runs with `ddl-auto=validate` (never creates or alters tables).

---

## Entity relationship diagram

Core domain relationships. Attribute lists show PKs, FKs, and columns that encode design constraints (ownership XOR, equipment slots, JOINED item subtypes). Reference-data lookup tables are included where characters/items depend on them.

```mermaid
erDiagram
    users ||--o{ account_tokens : has
    users ||--o{ campaign_members : "member of"
    users ||--o{ characters : "owns PC (player_id)"
    users ||--o{ characters : "creates NPC (created_by)"
    users ||--o{ items : "creates homebrew"

    campaigns ||--o{ campaign_members : has
    campaigns ||--o{ campaign_playable_characters : includes
    campaigns ||--o{ campaign_nonplayable_characters : includes
    campaigns ||--o{ campaign_master_notes : has
    campaigns ||--o{ session_logs : has
    campaigns ||--o{ items : "scopes homebrew"

    characters ||--o{ character_classes : has
    characters ||--o{ character_languages : knows
    characters ||--o| character_notes : "1:1 player note"
    characters ||--o{ character_spells : knows
    characters ||--o{ character_items : inventory
    characters ||--o{ campaign_playable_characters : attached
    characters ||--o{ campaign_nonplayable_characters : attached

    races ||--o{ characters : "is"
    races ||--o{ subraces : has
    races ||--o{ race_ability_point_bonuses : grants
    subraces ||--o{ characters : "optional"
    subraces ||--o{ subrace_ability_point_bonuses : grants
    abilities ||--o{ race_ability_point_bonuses : "bonus on"
    abilities ||--o{ subrace_ability_point_bonuses : "bonus on"
    abilities ||--o{ skills : governs
    backgrounds ||--o{ characters : "has"
    classes ||--o{ character_classes : "class of"
    classes ||--o{ class_starting_equipment_options : offers
    languages ||--o{ character_languages : "language of"

    notes ||--o| character_notes : "player note"
    notes ||--o{ campaign_master_notes : "master note"
    spells ||--o{ character_spells : "on spellbook"

    items ||--o| weapons : "JOINED"
    items ||--o| armor : "JOINED"
    items ||--o| gear_items : "JOINED"
    items ||--o| tool_items : "JOINED"
    items ||--o| jewelry_items : "JOINED"
    items ||--o| clothing_items : "JOINED"
    items ||--o{ character_items : "in inventory"
    items ||--o{ class_starting_equipment_options : "option item"
    weapon_categories ||--o{ weapons : categorizes
    armor_categories ||--o{ armor : categorizes

    users {
        bigint id PK
        varchar username UK
        varchar email UK
        varchar password_hash
        timestamp email_verified_at
        timestamp created_on
        timestamp updated_on
        timestamp last_activity
    }

    account_tokens {
        bigint id PK
        bigint user_id FK
        varchar type
        varchar token_hash UK
        timestamp expires_at
        timestamp consumed_at
    }

    campaigns {
        bigint id PK
        varchar title
        varchar description
        timestamp created_on
        timestamp updated_on
        timestamp finished_on "NULL = active"
    }

    campaign_members {
        bigint campaign_id PK_FK
        bigint user_id PK_FK
        campaign_role role "master | player"
    }

    characters {
        bigint id PK
        varchar character_type "PC | NPC"
        bigint player_id FK "PC only"
        bigint created_by_user_id FK "NPC only"
        varchar name
        bigint race_id FK
        bigint subrace_id FK
        bigint background_id FK
        character_status status
        smallint strength
        smallint dexterity
        smallint constitution
        smallint intelligence
        smallint wisdom
        smallint charisma
        smallint hit_points
        smallint armor_class
        integer gold
        boolean starting_equipment_chosen
    }

    campaign_playable_characters {
        bigint campaign_id PK_FK
        bigint character_id PK_FK
    }

    campaign_nonplayable_characters {
        bigint campaign_id PK_FK
        bigint character_id PK_FK
    }

    character_classes {
        bigint character_id PK_FK
        bigint class_id PK_FK
    }

    character_languages {
        bigint character_id PK_FK
        bigint language_id PK_FK
    }

    notes {
        bigint id PK
        varchar title
        varchar body
        timestamp created_on
        timestamp updated_on
    }

    character_notes {
        bigint character_id PK_FK
        bigint note_id UK_FK
    }

    campaign_master_notes {
        bigint campaign_id PK_FK
        bigint note_id PK_UK_FK
    }

    session_logs {
        bigint id PK
        bigint campaign_id FK
        varchar title
        varchar body
        timestamp created_on
    }

    spells {
        bigint id PK
        varchar api_index UK
        varchar name
        smallint level
        magic_school school
        spell_range_type range_type
        boolean concentration
        boolean ritual
        varchar duration
        text higher_level
    }

    character_spells {
        bigint character_id PK_FK
        bigint spell_id PK_FK
    }

    items {
        bigint id PK
        item_type item_type
        varchar name
        integer value
        smallint weight
        item_rarity rarity
        boolean is_magical
        varchar api_index UK
        bigint campaign_id FK "homebrew scope"
        bigint created_by_user_id FK
    }

    weapons {
        bigint item_id PK_FK
        bigint weapon_category_id FK
        varchar damage
        damage_type damage_type
        weapon_range weapon_range
        smallint range
    }

    armor {
        bigint item_id PK_FK
        bigint armor_category_id FK
        smallint base_ac
        smallint max_dex_bonus
        smallint str_minimum
    }

    gear_items {
        bigint item_id PK_FK
    }

    tool_items {
        bigint item_id PK_FK
    }

    jewelry_items {
        bigint item_id PK_FK
    }

    clothing_items {
        bigint item_id PK_FK
    }

    character_items {
        bigint id PK
        bigint character_id FK
        bigint item_id FK
        smallint quantity
        equipment_slot equipped_slot "unique per char when set"
    }

    class_starting_equipment_options {
        bigint id PK
        bigint class_id FK
        bigint item_id FK
        varchar option_group
        smallint sort_order
    }

    races {
        bigint id PK
        varchar name
        varchar index
        smallint speed
    }

    subraces {
        bigint id PK
        bigint race_id FK
        varchar name
    }

    race_ability_point_bonuses {
        bigint race_id PK_FK
        bigint ability_id PK_FK
        smallint value
    }

    subrace_ability_point_bonuses {
        bigint subrace_id PK_FK
        bigint ability_id PK_FK
        smallint value
    }

    abilities {
        bigint id PK
        varchar name
    }

    skills {
        bigint id PK
        bigint ability_id FK
        varchar name
    }

    backgrounds {
        bigint id PK
        varchar name
    }

    classes {
        bigint id PK
        varchar name
        smallint hit_dice
        varchar index
    }

    languages {
        bigint id PK
        varchar name
        varchar index
    }

    weapon_categories {
        bigint id PK
        varchar name
    }

    armor_categories {
        bigint id PK
        varchar name
    }
```

---

## Design notes worth reading on the diagram

**Character ownership XOR**: `characters` enforces `(player_id XOR created_by_user_id)` so a row is either a PC or an NPC at the DB level, not only in Java.

**Campaign attachment is typed**: PCs and NPCs use separate junction tables (`campaign_playable_characters` / `campaign_nonplayable_characters`) rather than a polymorphic attach with a type flag.

**Notes are asymmetric**: `character_notes` is 1:1 (PK on `character_id`); `campaign_master_notes` is many-per-campaign. Both point at a shared `notes` row.

**Items use JOINED inheritance**: `items` is the root; subtype tables (`weapons`, `armor`, `gear_items`, …) share `item_id` as PK/FK. `item_type` discriminates. Homebrew rows may set `campaign_id`.

**Equipped slots**: partial unique index on `(character_id, equipped_slot) WHERE equipped_slot IS NOT NULL` so only one item occupies a slot.

**Roles stay off the user**: `campaign_role` lives only on `campaign_members`.

---

## Enums

| Type | Values |
| --- | --- |
| `campaign_role` | `master`, `player` |
| `character_status` | `alive`, `down`, `dead` |
| `item_type` | `weapon`, `armor`, `tool`, `gear`, `jewelry`, `clothing` |
| `item_rarity` | `common` → `artifact` |
| `equipment_slot` | `armor`, `shield`, `main_hand`, `off_hand` |
| `magic_school` | abjuration … transmutation |
| `spell_range_type` | `self`, `touch`, `ranged`, `special`, `sight`, `unlimited` |
| `damage_type` | slashing, piercing, … thunder |
| `weapon_range` | `melee`, `ranged` |

`character_type` is `VARCHAR(3)` (`PC` / `NPC`) and not a Postgres enum. This is required for JPA `@DiscriminatorColumn`.

---

## Migrations

Versioned under `V1`–`V16`. Source of truth for column types and constraints. This diagram is a map, not a substitute for the SQL.
