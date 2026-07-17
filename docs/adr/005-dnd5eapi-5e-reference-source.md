# ADR-005: dnd5eapi.co as the 5e reference data source

### Status

Accepted

### Context

The application requires static 5e reference data for spells, classes, races, equipment, etc. This data is extensive, well-defined, and doesn't change. The question is whether to store it entirely in the local database, fetch it from an external source on demand, or use a hybrid of both. The full SRD spell list contains over 300 spells. Seeding all of them locally with complete data would be a significant one-time effort with ongoing maintenance burden if the SRD is updated.

### Alternatives Considered

**Full local seeding** : All reference data seeded into PostgreSQL at startup. Would allow for fast queries and offline use. However, the seeding effort for 300+ spells is substantial and the data is static and unlikely to change.

**Open5e API**: An alternative 5e REST API evaluated directly against dnd5eapi.co. Rejected because it mixes third-party sourcebook content alongside SRD data with no clean filter and provides less structured responses, (components as a comma-separated string, rather than an array, school as a plain string rather than structured object). Verified by direct API comparison during design phase.

### Decision

dnd5eapi.co will serve as the external reference data source for spell data. 

Equipment, classes, and races are seeded locally because they're small datasets directly referenced by database foreign keys during character creation. Spells are fetched from the API and cached locally on first use via the `spell` table, which stores the API index alongside the spell record.

### Rationale

Dnd5e.co returns SRD-only content with no third-party noise, well-structured JSON responses that map cleanly to the local schema, and requires no authentication or API key. The caching strategy means the app will progressively build a local spell dataset over time.

### Consequences

##### Positive

- Eliminates seeding burding for 300+ spells while keeping character creation local
- Clean structured responses make parsing and mapping to the local schema straightforward
- Progressive caching means the app's external API dependency diminishes over time as spells are accessed
- API is free with no rate limiting

##### Negative

- Because dnd5eapi.co is an external dependency, the spell browser cannot fetch uncached spells if the API is unavailable.
- First-time spell fetches will incur a network latency due to round trip through Spring Boot backend
- API requests must be proxied through the backend
- If dnd5eapi.co changes response structure, the backend's mapping layer will break silently or throw parsing errors. 