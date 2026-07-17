# Campaign Keep: Architecture

Vue 3 SPA + Spring Boot API for D&D 5e campaign management. Monorepo: `frontend/` (Vite, Pinia, Tailwind) and `backend/` (Spring Security JWT, JPA, Flyway, PostgreSQL).

This document is meant to record **why** the system is shaped the way it is.

---

## Packaging

### Backend: feature packages, not layers

Domain code lives under `com.kdnth.campaignkeep` by feature (`user`, `campaign`, `character`, `item`, `spell`, `note`, ...). Cross-cutting pieces (auditing, exception types, global handler) sit in `base/` and `config/`.

**`security/` is separate from `user/`.** Classes there exist to satisfy Spring Security’s contracts (`UserDetailsService`, JWT filter, entry point). If the auth framework changed, those classes would be rewritten, but `User` / `UserRepository` could remain untouched.

**Auth stays in `user/`.** Register and login are thin REST operations over `User` because auth is not a distinct domain concept large enough to own a package at this scope.

### Frontend: layer-based under `src/`

Views are route-bound pages; components are reusable UI; Pinia stores hold client state; `api/` owns the Axios client. Auth lives in a store (not component-local state) because the navbar, router guards, and multiple views all need the same token/user data.

---

## Auth & security

### JWT subject is user ID, not username

The `sub` claim is the user’s `Long` ID (stringified). IDs are immutable and map directly to FK columns; usernames could change. Controllers extract identity via `(Long) authentication.getPrincipal()`. The filter stores the ID as principal, not a `UserDetails` object, so that authenticated requests stay stateless with no DB hit per request.

`UserDetailsServiceImpl` runs only at login (`AuthenticationManager.authenticate()`). Deliberately not wired into `JwtAuthFilter`.

### Roles are not in the token

Campaign role (`master` / `player`) is scoped per campaign in `campaign_members`, not global to the account. Master/player roles are scoped to campaigns, not users. Authorization is a service-layer DB lookup; Spring Security’s only authority is `ROLE_USER` (“authenticated account”) for URL-level gates.

### Filter chain choices

- CSRF disabled and sessions `STATELESS`. CSRF protects cookie session auth; this API is Bearer JWT.
- `permitAll` path matchers must exactly match controller `@RequestMapping` prefixes; a mismatch fails closed as 401.
- CORS uses an injected `CorsConfigurationSource` bean. Calling the `@Bean` method from the security chain re-enters bean init instead of using the managed instance.

### DTOs and validation

Request/response types are Java `record`s. Entities are never returned. `User` carries `passwordHash`. `@Valid` is required on controller parameters; without it, Bean Validation annotations are silently ignored. Frontend validation is UX; backend validation is the trust boundary.

### Login identifier

`loadUserByUsername` resolves username first, then email via `Optional.or()`. The request field is named `identifier` to match that contract. Register returns a token so the client does not need a second round-trip.

Login does an intentional second repository fetch after `authenticate()`: Spring’s `Authentication` does not hand back the domain entity. This could be collapsed later by authenticating with a pre-loaded `UserDetails`.

### Exception mapping

`@RestControllerAdvice` maps domain failures to HTTP status:

| Exception | Status | Typical cause |
| --- | --- | --- |
| `IllegalArgumentException` | 400 | Duplicate username/email, bad input |
| `MethodArgumentNotValidException` | 400 | `@Valid` failures |
| Custom `AccessDeniedException` | 403 | Campaign membership/role |
| `NoSuchElementException` | 404 | Missing resource |
| Custom `ConflictException` | 409 | Duplicate member, last-master guard |

Custom `AccessDeniedException` / `ConflictException` live in `base/`, not Spring Security’s `AccessDeniedException`. AuthZ is enforced in services, so the handler owns those mappings. Response shape (`timestamp`, `status`, `error`, `message`) matches `JwtAuthEntryPoint` for consistency.

---

## Authorization model (campaigns)

All campaign authorization lives in `CampaignServiceImpl`, not method-level Spring Security annotations. Per-campaign roles do not fit cleanly into a global authority model; service helpers keep the rules in one place:

- `requireMembership`: campaign exists and caller is a member
- `requireMaster`: membership plus `master` role
- `guardAgainstLastMasterLoss`: demote/remove of the sole master creates `409 Conflict`

Creator of a campaign is auto-added as master. List endpoints return only campaigns the caller belongs to; `callerRole` is embedded in `CampaignResponse` so the UI can branch without a second membership fetch.

Frontend mirrors last-master rules (`isOnlyMaster`) to hide destructive actions early. The server enforces regardless. The UI never trusts itself for AuthZ.

Role-based campaign UI uses **one route** (`/campaigns/:id`) and swaps master vs player detail views from `callerRole`, rather than duplicating URLs.

---

## Persistence

**Flyway owns schema.** Hibernate `ddl-auto=validate`. Startup fails if entities disagree with migrations. Schema drift is caught before traffic instead of mid-request.

**Naming:** plural tables (`users`, `campaign_members`). `armor` stays singular as `armors` is unnatural.

**Auditing:** `@MappedSuperclass` `Auditable` with `@CreatedDate` / `@LastModifiedDate`. Entities that need both timestamps extend it.

**Lombok on entities:** `@Getter` / `@Setter` / `@NoArgsConstructor`. Never `@Data`. `@Data`’s `equals`/`hashCode` over all fields recurses through JPA associations and breaks lazy proxies. `@NoArgsConstructor` is required for Hibernate’s reflective construction.

**IDs:** `GenerationType.IDENTITY` aligned with Postgres `GENERATED ALWAYS AS IDENTITY`.

**Enums:** Java enum labels match Postgres native enum labels verbatim (lowercase), mapped with `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`.

---

## Domain design highlights

### Characters use single-table inheritance

Abstract `Character` with `PlayableCharacter` (PC) and `NonplayableCharacter` (NPC) discriminators. Owner CHECK in SQL: `player_id` XOR `created_by_user_id`. Both subtypes implement `canBeEditedBy(userId)`.

Ability scores are six columns on `characters`. Class is a junction (`character_classes`) with one class at creation time. MVP does not support leveling, but it is a planned feature.

`character_type` is `VARCHAR` for JPA `@DiscriminatorColumn`. Enum discriminators fight Hibernate’s string discriminator values.

### Asymmetric Notes model

Player notes are 1:1 with a PC in a campaign (upsert). Master notes are many-per-campaign. AuthZ delegates membership/role checks to `CampaignService`. Player notes also require ownership and campaign attachment.

### Spells prefer local cache over external API

`Dnd5eApiClient` fetches from the public dnd5e API on cache miss and persists into local `spells`. The Vue app never calls the external API.

### Inventory JOINED item hierarchy

Items use JOINED inheritance (`gear`, `tools`, weapons/armor subtypes, etc.). Attacks are **derived at read time** from equipped weapons. There currently is no `attacks` table. This will need to change if combat log is implemented in the future. Leveling implementation may also create this need.

Starting equipment applies a STR gate on armor. AC recalculates server-side when equipment changes.

### Dice (client-only)

Rolls are session-table UX, not authoritative game state. Persisting or syncing them would need real-time infra without satisfying core CRUD/security goals. Damage rolls on the attacks panel use the same local helpers.

### Current HP client shortcut

`useCurrentHp` stores HP in `localStorage` keyed by `campaignId:characterId`. Status (alive/down/dead) syncs to the API when thresholds cross. Server-persisted HP was deferred: it needs concurrent-edit rules and is not required for the MVP authorization/report surface. Documented so it is not mistaken for an oversight.

---

## Frontend auth session

Token in `localStorage`. Profile (`userId`, `username`) re-fetched via `GET /users/me` on mount / after login. Axios request interceptor attaches `Authorization: Bearer` from `localStorage` (same key as the store). A global `401` response interceptor would centralize mid-session expiry handling beyond the mount/guard path.

Router `beforeEach` is async: if a token exists but `userId` is null (refresh restored the token only), it awaits `fetchCurrentUser()` before applying `requiresAuth` / `requiresGuest` meta.

Logout is client-side only. JWT is stateless; there is no server revoke list.

---

## Deliberate tradeoffs

| Choice | Why | Cost |
| --- | --- | --- |
| Service-layer AuthZ, not `@PreAuthorize` | Per-campaign roles; one place for last-master and membership rules | Controllers must remember to call services that enforce |
| JWT without role claims | Roles change without re-issue | Every protected campaign op hits DB for membership |
| `Long` principal, no per-request user load | Stateless, cheap | Controllers cast principal; need `/users/me` for display fields |
| Flyway + validate | Explicit schema history; fail-fast mapping bugs | Every entity change needs a migration |
| Client HP / client dice | MVP scope; no realtime | No cross-device HP or shared rolls |
| Spell cache from dnd5eapi | Offline-friendly local catalog; Vue never depends on third party | Sync freshness is pull-on-miss, not push |
| No `UserService` for `/users/me` | Single lookup, no business rules yet | Extract when ownership/joins appear |

---

## Related docs

- [DATABASE.md](./DATABASE.md) (schema and migration detail)
- [DEPLOYMENT.md](./DEPLOYMENT.md) (runtime and environment configuration)
