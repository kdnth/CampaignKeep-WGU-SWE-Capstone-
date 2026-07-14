## 1. Class Diagram

Scoped to the `Character` and `Item` hierarchies plus a representative service pair, since these carry the OOP story.

```mermaid
classDiagram
    class Auditable {
        <<abstract>>
        -LocalDateTime createdOn
        -LocalDateTime updatedOn
    }

    class Character {
        <<abstract>>
        -Long id
        -String name
        -CharacterStatus status
        -short strength
        -short dexterity
        -short constitution
        -short intelligence
        -short wisdom
        -short charisma
        -short hitPoints
        -short armorClass
        -short initiativeBonus
        -short speed
        -Race race
        -Subrace subrace
        -Background background
        +canBeEditedBy(Long userId) boolean*
    }

    class PlayableCharacter {
        -User player
        +canBeEditedBy(Long userId) boolean
    }

    class NonplayableCharacter {
        -User createdBy
        +canBeEditedBy(Long userId) boolean
    }

    Auditable <|-- Character
    Character <|-- PlayableCharacter
    Character <|-- NonplayableCharacter
    Character "1" --> "1" Race : has
    Character "1" --> "0..1" Subrace : has
    Character "1" --> "0..1" Background : has
    PlayableCharacter "1" --> "1" User : owned by
    NonplayableCharacter "1" --> "1" User : created by

    class Item {
        <<abstract>>
        -Long id
        -ItemType itemType
        -String name
        -String description
        -int value
        -short weight
        -ItemRarity rarity
        -boolean isMagical
        -String apiIndex
    }

    class Weapon {
        -WeaponCategory weaponCategory
        -String damageDice
        -DamageType damageType
        -short range
        -WeaponRange weaponRange
    }

    class Armor {
        -ArmorCategory armorCategory
        -short baseAc
        -short maxDexBonus
        -short strMinimum
    }

    class GearItem
    class ToolItem
    class JewelryItem
    class ClothingItem

    Item <|-- Weapon
    Item <|-- Armor
    Item <|-- GearItem
    Item <|-- ToolItem
    Item <|-- JewelryItem
    Item <|-- ClothingItem

    class CharacterService {
        <<interface>>
        +createPlayableCharacter(CreatePlayableCharacterRequest, Long callerId) Character
        +createNonplayableCharacter(CreateNonplayableCharacterRequest, Long callerId) Character
        +getCharacter(Long id, Long callerId) Character
        +deleteCharacter(Long id, Long callerId) void
    }
    class CharacterServiceImpl
    CharacterService <|.. CharacterServiceImpl
    CharacterServiceImpl ..> Character : manages

    class ItemService {
        <<interface>>
        +createCampaignItem(CreateItemRequest, Long callerId) Item
        +getItem(Long id) Item
    }
    class ItemServiceImpl
    ItemService <|.. ItemServiceImpl
    ItemServiceImpl ..> Item : manages

    note for CharacterService "CampaignService, SpellService, NoteService,\nand SessionLogService follow the same\ninterface + impl pattern (omitted here for clarity)."
```

**Inheritance**: `PlayableCharacter`/`NonplayableCharacter` extend `Character`; six item subtypes extend `Item`; `Character` extends `Auditable`.

**Polymorphism**: `canBeEditedBy(Long userId)` is abstract on `Character`, overridden differently per subclass. Callers invoke it without knowing the concrete type. The interface/impl split on every service is a second, structural form of polymorphism.

**Encapsulation**: Private fields with Lombok accessors, no `@Data`. Business rules live in the service layer only, so controllers can't bypass validation.

**Mapping to persistence:** `Character` uses single-table inheritance (`character_type` discriminator, two subtypes with mostly-overlapping shape). `Item` uses joined-table inheritance (6+ growing subtypes with different columns each) to avoid a wide null-heavy table.

## 2. Design Diagram (System Architecture)

```mermaid
flowchart TB
    subgraph Client["Browser — Vue 3 SPA"]
        Vue["Views & Components<br/>(Composition API)"]
        Pinia["Pinia Stores<br/>(auth, campaign, character, item, spell, ...)"]
        Axios["Axios Client<br/>+ JWT request interceptor"]
        Vue --> Pinia --> Axios
    end

    subgraph Backend["Spring Boot API — localhost:8080"]
        Filter["JwtAuthFilter<br/>(Spring Security filter chain)"]
        Controllers["REST Controllers<br/>Campaign / Character / Item / Spell / Note / SessionLog"]
        Services["Service Layer<br/>interface + impl — authZ, business rules"]
        Repos["Spring Data JPA Repositories"]
        Filter --> Controllers --> Services --> Repos
    end

    DB[("PostgreSQL<br/>schema managed by Flyway (V1–V15)")]
    External["dnd5eapi.co<br/>external SRD REST API"]

    Axios -- "HTTPS + JSON<br/>Authorization: Bearer &lt;JWT&gt;" --> Filter
    Repos --> DB
    Services -- "server-side only\ncached into spells table" --> External
```

#### Justification

- Everything's proxied through Spring Boot, so Vue never talks to Postgres or dnd5eapi directly. Secrets stay server-side and the dnd5eapi cache is enforced in exactly one place.
- `JwtAuthFilter` resolves authentication once per request before any controller runs.
- Authorization (i.e., membership, role, ownership) lives in services. `requireMembership()`/`requireMaster()` is reusable across `CampaignService`, `CharacterService`, `NoteService`, etc, and controllers stay thin.
- Services depend on repository interfaces instead of JPA specifics, and Pinia stores are the only frontend code that knows API response shapes. Either layer could change underneath without having to rewrite business logic.