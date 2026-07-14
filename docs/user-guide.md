# CampaignKeep — User Guide

How to use CampaignKeep as a player or game master (GM). This guide assumes the app is already running at **http://localhost:5173** (or whatever URL your maintainer gave you).

For installing and starting the servers, see [`setup-and-maintenance-guide.md`](./setup-and-maintenance-guide.md).

---

## 1. What CampaignKeep is

CampaignKeep is a Dungeons & Dragons 5e **campaign organizer**. You can:

- Create an account and log in securely
- Join or run campaigns (as **player** or **master** / GM)
- Build level-1 **playable characters** and **NPCs**
- Track stats, equipment, attacks, spells, and notes at the table
- Write **session logs** (meeting notes for the campaign)
- Roll dice during a session (rolls stay on your screen; they are not shared automatically)

The app supports the core races (Human, Dwarf, Elf) and classes (Fighter, Wizard, Rogue) included in the seed data.

---

## 2. Account basics

### Create an account

1. Open CampaignKeep in your browser.
2. Click **Sign Up** (or go to Register).
3. Enter a **username** (max 16 characters), **email**, and **password**.
4. Confirm your password and submit.

Successful registration logs you in automatically and takes you to **Home**.

### Log in

1. Click **Login**.
2. Enter either your **username or email**, plus password.
3. You land on Home.

### Log out

Open the username menu (top right) → **Logout**.

Your session is stored in the browser. If you clear site data or your session expires, log in again.

---

## 3. Finding your way around

When logged in, the top navigation has:

| Link | What it does |
|------|----------------|
| **CampaignKeep** (logo) | Home — welcome, plus short lists of your campaigns and characters |
| **Home** | Same as logo |
| **My Campaigns** | Full campaign list (search, filters, create) |
| **My Characters** | Your playable characters and NPCs |
| **Spells** | Browse the spell library and look up details |

Use **Back** links on detail pages to return to lists.

---

## 4. Roles: master vs player

Every campaign membership has a role:

| Role | Typical jobs |
|------|----------------|
| **Master** (GM) | Create/finish/delete campaign, manage members, attach NPCs, grant items, write master notes & session logs, browse spells for prep |
| **Player** | Attach a playable character, manage that PC’s stats/gear/spells/notes, read session logs, roll dice |

You can be a master in one campaign and a player in another. Opening a campaign shows the view that matches **your role in that campaign**.

**Tip:** The person who creates a campaign is automatically its first master.

---

## 5. Campaigns

### Browse and find campaigns

**My Campaigns** shows cards for every campaign you belong to. You can:

- **Search** by title
- Filter by **status** (active vs finished) and **your role** (master / player)
- **Sort** by created date (newest or oldest)

### Create a campaign

1. My Campaigns → **Create New** (or equivalent create button).
2. Enter a **title** (required) and optional **description**.
3. Submit — you open the new campaign as **master**.

### Open a campaign

Click a campaign card. You see either the **master** or **player** campaign desk (tabs below).

### Finish or delete (master only)

On the master campaign view:

- **Finish** marks the campaign done (you can still open it; it is no longer “active”).
- **Delete** removes the campaign and its memberships permanently — confirm carefully.

### Manage members (master)

Open the **Campaign Members** tab:

- **Add member** by username or email and choose role (`master` or `player`)
- **Promote / demote** (other members)
- **Remove** a member, or **Leave** yourself (you cannot leave or demote if you are the **only** remaining master)

---

## 6. Characters

### View your characters

**My Characters** lists your playable characters (PCs) and NPCs. Open a card for the character sheet (stats, campaigns, inventory/spells when available, and delete if you own it).

Home also shows a compact character list for quick access.

### Create a playable character (PC)

From My Characters (or from a campaign’s “add character” flow):

1. Choose **create playable**.
2. Walk through the wizard:

| Step | What you choose |
|------|------------------|
| Name, race, class | Character identity (Fighter / Wizard / Rogue) |
| Subrace | Shown when the race has options (e.g. Hill Dwarf, High Elf); skipped for Human |
| Ability scores | Randomized scores you can reroll |
| Background & languages | Background (or create a custom one) and languages |
| Spells | Appears for **Wizard** or **High Elf** |
| Starting equipment | Weapons/armor/shield from your class options (armor may require enough Strength) |
| Review | Confirm and create |

Optional: if you started from a campaign, the new PC can be **attached to that campaign** automatically.

### Create an NPC

Similar wizard, oriented for GM use:

- Name and race (no player class step)
- Subrace, abilities, background/languages
- **Starting pack** (standard adventuring gear pack)
- Review and create

NPCs are owned by the user who created them (usually the master).

### Attach a character to a campaign

- **Player:** on the player campaign view, use the panel to attach one of your PCs (or create a new one).
- **Master:** on **Player Characters** / **NPCs** tabs, add existing characters or create new ones for the table.

A PC must be attached before most player tabs (Stats, Notes, Spells, Equipment, Attacks) unlock.

---

## 7. At the table — player view

After opening a campaign as a **player** and attaching your PC, use these tabs:

### Stats

Ability scores, derived stats, and **hit points**.

- Adjust current HP as you take damage or heal (tracked for this campaign/character in your browser).
- Status can move between **alive**, **down**, and **dead** based on HP / actions available in the UI.
- Update **gold** when your wealth changes.

**Note:** Current HP is saved in your browser for this campaign, not as a shared live tracker for the whole table. Refresh on the same browser keeps it; a different device may not see the same current HP until you re-enter it.

### Notes

Your private **player note** for this character in the campaign (one note you can edit and update).

### Spells

Your **spellbook** for this character — browse and add/remove spells you know or prepare as your table agrees.

### Session Logs

Read session write-ups the master has posted (titles, dates, body text). Players do not create or delete these logs.

### Equipment

- See inventory, equip/unequip (armor, shield, main hand, off hand)
- Adjust quantity and gold as needed
- Armor **Strength requirements** are enforced when equipping
- Your **armor class** updates when armor/shield changes

If starting equipment was not chosen during character creation, a prompt may ask you to pick it when you enter the campaign.

### Attacks

Attack options derived from **equipped** weapons. You can roll damage dice for those attacks from this tab (local roll on your machine).

### Roll

Full dice roller (see [§9](#9-dice-roller)).

---

## 8. At the table — master (GM) view

Opening a campaign as **master** shows:

| Tab | Use it to |
|-----|-----------|
| **Player Characters** | See attached PCs, add more, **grant items** to a PC |
| **NPCs** | Add/manage NPCs, grant items, track NPCs as needed |
| **Spells** | Browse the spell library while prepping or running |
| **Notes** | Create, edit, and delete **GM notes** (many per campaign) |
| **Session Logs** | Create session reports (title + body + timestamp) — good for after-session notes; members can read them |
| **Campaign Members** | Invite players, change roles, remove members; finish/delete campaign |
| **Campaign Items** | Create **homebrew / campaign-specific** items, then grant them from PC/NPC tabs |
| **Roll** | Dice roller with a **custom modifier** (GM-friendly) |

### Granting an item

1. Ensure the item exists (seeded gear, or create it under **Campaign Items**).
2. Open the PC or NPC section and use **Grant item**.
3. The character’s inventory updates for their owner next time they load equipment.

### Session logs as your “report”

Each log has a **title**, **body**, and **created time**. Create one after (or during) a session so the table has a lasting record. Players read them under their Session Logs tab.

---

## 9. Dice roller

Available on the campaign **Roll** tab (master and player) and via damage rolls on Attacks.

**How to roll**

1. Choose die type: **d4, d6, d8, d10, d12, d20, d100**.
2. Set **quantity** (how many dice), unless using d20 advantage/disadvantage.
3. Optional:
   - **Player:** pick a **skill** — the roller adds the related ability modifier (proficiency is not applied automatically).
   - **Master:** set a **custom modifier**.
4. For d20 only: choose **Normal**, **Advantage**, or **Disadvantage**.
5. Click roll — faces animate, then the total is shown.

**Important:** Rolls are calculated in your browser only. Other users do not see your roll unless you tell them (chat, voice, etc.).

---

## 10. Spell browser (global)

**Spells** in the main nav opens the full library:

- Search by name
- Filter by level, school, or class as offered in the UI
- Flip/open cards for casting time, range, components, description, and higher-level text

From a campaign or character spellbook you can typically add spells to a character when that UI is available. The library is shared reference data (including spells loaded from the official SRD API cache on first use).

---

## 11. Typical first night (suggested path)

**GM**

1. Register / log in.
2. Create a campaign.
3. Add player accounts as members (role: player).
4. Create NPCs if needed; attach them under **NPCs**.
5. Create a session log after play; keep prep in **Notes** or **Spells**.

**Player**

1. Register / log in (or use the account the GM invited).
2. Create a playable character (or create one when attaching to the campaign).
3. Open the campaign → confirm your PC is attached.
4. Finish **starting equipment** if prompted.
5. Use **Stats**, **Equipment**, **Spells**, and **Roll** during play; read **Session Logs** between sessions.

---

## 12. Tips and limits (so nothing surprises you)

- Characters are **level 1** in this version — no XP or leveling UI yet.
- **Multiclassing** is not offered at create time.
- Status effects beyond **alive / down / dead** are not modeled.
- Damage to *other* characters is not applied automatically — you adjust HP yourself.
- Dice and current HP are **not live multiplayer sync**.
- Only attached characters drive most player tabs; attach a PC first if tabs are disabled.
- Custom **backgrounds** can be added during creation when the form allows it.
- Campaign **homebrew items** are visible in that campaign’s item tooling; grant them so they appear in a character’s inventory.

---

## 13. Quick troubleshooting (user-facing)

| Problem | What to try |
|---------|-------------|
| Sent back to Login | Session expired or logged out — log in again |
| Campaign tabs disabled (player) | Attach a playable character first |
| Cannot leave / demote | You are the last master — promote someone else first |
| Cannot equip heavy armor | Raise Strength or choose lighter armor |
| “Something went wrong” on forms | Check required fields and that your username/email is unique |
| Spells list empty or slow first load | Wait for the first sync; try refresh (needs network once) |
| HP looks wrong on another computer | Current HP is local to that browser — re-enter it there |

If nothing loads at all, contact whoever maintains the app (servers or database may be down). Maintainers use the [setup and maintenance guide](./setup-and-maintenance-guide.md).

---

## 14. Glossary

| Term | Meaning in CampaignKeep |
|------|-------------------------|
| **Master** | Game master / GM for a campaign |
| **Player** | Member who contributes a playable character |
| **PC** | Playable character |
| **NPC** | Non-playable character (usually GM-created) |
| **Session log** | Timestamped campaign report / session write-up |
| **Grant** | Master adds an item to a character’s inventory |
| **Campaign item** | Item created for one campaign (often homebrew) |
