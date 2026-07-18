# CampaignKeep

D&D 5e campaign and character organizer for players and game masters. Vue 3 frontend, Spring Boot API, PostgreSQL.

---

## Documentation

| Guide | Audience | Description |
|-------|----------|-------------|
| [User guide](docs/user-guide.md) | Players and GMs | How to create accounts, campaigns, characters, and use table tools |
| [Setup and maintenance guide](docs/setup-and-maintenance-guide.md) | Developers / maintainers | Prerequisites, environment variables, local run, Flyway, troubleshooting |
| [Design document](docs/design-document.md) | Developers / reviewers | Class and sequence diagrams for core OOP design |
| [Software testing](docs/software-testing.md) | Evaluators / maintainers | Unit test plan, scripts, results placeholders, and changes from testing |

---

## Project layout

```
campaignkeep/
├── backend/     # Spring Boot REST API (default http://localhost:8080)
├── frontend/    # Vue 3 + Vite SPA (default http://localhost:5173)
└── docs/        # Guides
```

---

## Quick start

1. Follow the [setup and maintenance guide](docs/setup-and-maintenance-guide.md) to configure PostgreSQL, backend env vars, and the frontend `.env`.
2. Start the backend, then the frontend.
3. Open the app in a browser and use the [user guide](docs/user-guide.md).

---

## Capstone

WGU D424 Software Engineering Capstone project. Includes JWT auth, role-based campaign access (master/player), character management, inventory, spells, session logs, and a client-side dice roller.