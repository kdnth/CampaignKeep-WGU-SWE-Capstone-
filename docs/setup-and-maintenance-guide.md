# CampaignKeep: Setup & Maintenance Guide

Operational guide for running a local instance for development and maintenance. This is not an end-user product manual.

For how players and GMs use the app once it is running, see [`user-guide.md`](./user-guide.md).  
For architecture and OOP diagrams, see [`design-document.md`](./design-document.md).

---

## 1. What you are running

| Piece | Tech | Default URL |
|-------|------|-------------|
| Frontend SPA | Vue 3 + Vite | `http://localhost:5173` |
| Backend API | Spring Boot 4.1 (Java 17) | `http://localhost:8080` |
| Database | PostgreSQL | JDBC via `DB_URL` |
| External ref data | dnd5eapi.co `/api/2014` | Outbound HTTPS from backend only |

```
Browser → Vite (:5173) → Spring Boot (:8080/api) → PostgreSQL
                              ↓
                         dnd5eapi.co (spells / equipment enrich)
```

CORS is locked to `http://localhost:5173`. Change both CORS and `VITE_API_BASE_URL` together if you move ports or hosts.

---

## 2. Prerequisites

| Tool | Version / notes |
|------|-----------------|
| **JDK** | **17** (matches `backend/pom.xml` `java.version`) |
| **Maven** | 3.9+ (or IntelliJ’s bundled Maven) |
| **Node.js** | `^22.18.0` or `>=24.12.0` (see `frontend/package.json` `engines`) |
| **npm** | Comes with Node |
| **PostgreSQL** | 14+ recommended (local: Postgres.app, Homebrew, Docker, etc.) |
| **Git** | Clone the `campaignkeep` repository root |
| **IDE (optional)** | IntelliJ IDEA Ultimate is used for `.http` API smoke files |

Verify:

```bash
java -version          # 17.x
mvn -version
node -v                # 22.18+ or 24.12+
psql --version
```

---

## 3. One-time database setup

1. Start PostgreSQL.
2. Create an empty database (you choose name, `campaignkeep` is conventional):

```sql
CREATE DATABASE campaignkeep;
```

3. Confirm you can connect (example for local peer/trust auth):

```bash
psql -d campaignkeep -c '\conninfo'
```

Flyway runs automatically on Spring Boot startup and applies every migration under:

`backend/src/main/resources/db/migration/` (`V1` … `V15` today).

Hibernate never creates tables, as it is set to `ddl-auto=validate`. If entities and migrations disagree, the app **fails at startup**. This by design.

**Do not edit applied migration files** once they have been shared or deployed. This will result in a Flyway checksum error. Add a new `V<n>__description.sql` instead.
_(To resolve a Flyway checksum error, you must revert any edits made to already applied migration files.)_

---

## 4. Backend environment variables

Spring reads secrets from the environment (see `backend/src/main/resources/application.properties`). Nothing sensitive belongs in the properties file or git.

| Variable | Required | Purpose |
|----------|----------|---------|
| `DB_URL` | Yes | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/campaignkeep` |
| `DB_USERNAME` | Yes | DB user |
| `DB_PASSWORD` | No | Defaults to empty string if unset (`${DB_PASSWORD:}`) |
| `JWT_SECRET` | Yes | HMAC signing key **≥ 32 UTF-8 bytes** (JJWT HS256 minimum) |
| `JWT_EXP` | Yes | Token lifetime in **milliseconds** (e.g. `86400000` = 24h) |

Optional override:

| Property | Default |
|----------|---------|
| `dnd5eapi.base-url` | `https://www.dnd5eapi.co/api/2014` |

Use the versioned `/api/2014` path. The unversioned `/api/spells` redirects in a way that breaks JSON parsing.

### Example shell export (zsh/bash)

```bash
export DB_URL='jdbc:postgresql://localhost:5432/campaignkeep'
export DB_USERNAME='your_postgres_user'
export DB_PASSWORD='your_password'          # omit or empty if local auth allows it
export JWT_SECRET='replace-with-a-long-random-secret-at-least-32-chars'
export JWT_EXP='86400000'
```

### IntelliJ Run Configuration

For `CampaignkeepApplication`:

1. Run → Edit Configurations → your Spring Boot run config.
2. Environment variables: paste the same `KEY=value` pairs (semicolon- or semicolon-separated depending on IDE version; IntelliJ uses `;` between entries on many setups).
3. Working directory: `backend/` (module root with `pom.xml`).

Generate a secret (example):

```bash
openssl rand -base64 48
```

---

## 5. Start the backend

From `campaignkeep/backend`:

```bash
# Ensure env vars from §4 are set in this shell
./mvnw spring-boot:run
# or, if Maven is on PATH:
mvn spring-boot:run
```

Or run `com.kdnth.campaignkeep.CampaignkeepApplication` from IntelliJ with the env vars attached.

**Success signals:**

- Log shows Flyway migrations applied (or “up to date”).
- Tomcat / app listening on port **8080**.
- No schema-validation errors from Hibernate.

**Smoke probe (unauthenticated):**

```bash
curl -s -o /dev/null -w '%{http_code}\n' \
  -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"identifier":"x","password":"y"}'
```

Expect `400` or `401` with a JSON body, not connection refused. A raw connection error means the process is not up.

### Unit tests

```bash
cd backend
mvn test
```

Current service-level suites include notes, session logs, spells, and inventory (plus `CampaignkeepApplicationTests`).

---

## 6. Frontend environment

`frontend/.env` is gitignored (`*.env`). Create it locally:

```bash
cd frontend
cat > .env <<'EOF'
VITE_API_BASE_URL=http://localhost:8080/api
EOF
```

| Variable | Purpose |
|----------|---------|
| `VITE_API_BASE_URL` | Axios base URL. Must include the `/api` suffix. Stores call paths like `/auth/login`. |

Vite only exposes variables prefixed with `VITE_`. Restart `npm run dev` after changing `.env`.

---

## 7. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

| Script | Use |
|--------|-----|
| `npm run dev` | Hot-reload development server |
| `npm run build` | Type-check + production bundle |
| `npm run type-check` | `vue-tsc` only |
| `npm run lint` | ESLint + oxlint |
| `npm run preview` | Serve the production build locally |

---

## 8. First-run sanity checklist

Do this after both processes are up:

1. **Register** at `/register` (username ≤ 16 chars, unique email).
2. Confirm redirect to home and NavBar shows your username.
3. **Create a campaign** → you become `master`.
4. Open campaign detail → master tabs (PCs, NPCs, Spells, Notes, Session Logs, Members, Campaign Items, Roll).
5. Create a playable character (wizard) and attach it to the campaign.
6. As that character’s player context, open Equipment / Spells / Roll tabs.
7. Optional: add a second user, invite them as `player`, verify they cannot finish/delete the campaign.

Auth tokens live in browser `localStorage` under key `token`. Clearing site data logs you out.

---

## 9. API smoke tests (IntelliJ HTTP client)

Files live in `backend/http/`:

| File | Coverage |
|------|----------|
| `auth.http` | Register / login / `/api/users/me` |
| `campaigns.http` | Campaign CRUD, membership, roles |
| `notes.http` | Player + master notes |
| `session-logs.http` | Session logs |
| `spells.http` | Spell browser / character spells |
| `inventory.http` | Inventory, starting gear, grants |

`http-client.env.json` is **gitignored**. Recreate it beside the `.http` files:

```json
{
  "dev": {
    "baseUrl": "http://localhost:8080"
  }
}
```

Optional secrets go in `http-client.private.env.json` (also gitignored).

In the IDE, select the `dev` environment, run register/login first (scripts stash `{{token}}`), then protected requests.

---

## 10. Routine maintenance

### Restart after dependency or env changes

1. Stop Spring Boot and Vite.
2. Update env / `.env` as needed.
3. Backend: `mvn spring-boot:run` (or IDE rebuild/run).
4. Frontend: `npm install` if `package-lock.json` changed, then `npm run dev`.

### Apply a new Flyway migration

1. Add `backend/src/main/resources/db/migration/V<n>__snake_case_description.sql`.
2. Update JPA entities to match.
3. Restart the backend. Flyway applies pending versions before the app accepts traffic.
4. If validation fails, fix the entity/migration mismatch; do not force Hibernate to rewrite the schema.

### Reset the local database (destructive)

Only for disposable local DBs:

```sql
DROP DATABASE campaignkeep;
CREATE DATABASE campaignkeep;
```

Restart the backend so Flyway replays V1→current on an empty database. All users, campaigns, and characters are gone.

### Clear a stuck JWT on the client

- Log out in the UI, or
- DevTools → Application → Local Storage → delete `token`, refresh.

### Inspect Flyway history

```sql
SELECT version, description, success, installed_on
FROM flyway_schema_history
ORDER BY installed_rank;
```

---

## 11. Troubleshooting

| Symptom | Likely cause | What to do |
|---------|--------------|------------|
| Backend exits on start: datasource error | Bad/missing `DB_*` or Postgres down | Check process, URL, credentials, DB exists |
| Schema-validation / mapping error | Entity ≠ Flyway schema | Diff last migration vs entity; add a new migration if needed |
| Flyway checksum mismatch | Edited an already-applied `V*.sql` | Restore original file or (local only) repair/rebuild DB carefully |
| `WeakKeyException` / JWT key too short | `JWT_SECRET` &lt; 32 bytes | Use a longer secret |
| Frontend loads but API calls fail CORS | Origin ≠ `http://localhost:5173` or API on wrong host | Align Vite URL with `SecurityConfig` allowed origin |
| `Network Error` / 404 on `/auth/login` | Missing `/api` in `VITE_API_BASE_URL` | Set `http://localhost:8080/api` |
| 401 on every request after refresh | Expired `JWT_EXP` or secret rotated | Log in again; keep `JWT_SECRET` stable across restarts in a given env |
| Spell sync fails | Outbound HTTPS blocked or wrong dnd5e base URL | Confirm network; keep `/api/2014` base |
| Port 8080 / 5173 in use | Another process bound | Stop the other process or change ports (and CORS / `.env` together) |
| Node engine warning / install fails | Node too old | Upgrade to Node 22.18+ or 24.12+ |

Useful log filters on the backend: Spring Security (`logging.level.org.springframework.security`), Flyway, and Hibernate SQL.

---

## 12. Security & hygiene for maintainers

- Never commit `.env`, real `JWT_SECRET`, or production DB passwords. Root `.gitignore` already ignores `*.env` and HTTP private env files.
- Prefer a unique `JWT_SECRET` per environment. Rotating it invalidates all outstanding tokens (users must log in again). Acceptable for local resets; plan for production.
- Production will need: externalized CORS origins, non-TRACE security logging, configured `VITE_API_BASE_URL`, and HTTPS termination. Tracked as deployment work, not covered by local defaults here.
- Dice rolls are **client-only** (`Math.random` in the SPA). Do not expect server logs or DB rows for rolls.

---

## 13. Quick command reference

```bash
# Terminal A (Postgres must already be running)
export DB_URL='jdbc:postgresql://localhost:5432/campaignkeep'
export DB_USERNAME='your_user'
export DB_PASSWORD='your_password'
export JWT_SECRET='replace-with-a-long-random-secret-at-least-32-chars'
export JWT_EXP='86400000'
cd backend && mvn spring-boot:run

# Terminal B
cd frontend
# ensure .env has VITE_API_BASE_URL=http://localhost:8080/api
npm install
npm run dev
```

Then browse `http://localhost:5173`.

---

## 14. Related paths

| Path | Contents |
|------|----------|
| `backend/src/main/resources/application.properties` | Datasource, JWT, dnd5eapi base URL bindings |
| `backend/src/main/resources/db/migration/` | Flyway SQL (source of truth for schema) |
| `backend/http/` | Manual API sequences |
| `frontend/src/api/axios.ts` | Axios base URL + Bearer adapter |
| `frontend/src/router/index.ts` | Routes and auth guards |
| `docs/design-document.md` | Class / sequence design diagrams |
