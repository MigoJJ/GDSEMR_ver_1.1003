# Databases (SQLite) Guide

This project uses multiple SQLite files for feature-specific storage.

## 1) DB locations and placement rules

### Runtime writable DB folder (primary)
- `app/db/*.db`
- This is the main location used by most repositories via project-root path resolution.

### Seed/resource DB folder (reference assets)
- `app/src/main/resources/database/*.db`
- Includes seed/reference databases (for example KCD and baseline DB assets).

### Special case: KCD DB
- Current KCD manager uses a fixed path:
  - `app/src/main/resources/database/kcd_database.db`
- This means KCD edits can modify a DB under `src/main/resources`.

## 2) Database inventory (purpose + schema source)

| DB file | Primary path | Purpose | Schema location |
|---|---|---|---|
| `abbreviations.db` | `app/db/abbreviations.db` | Abbreviation short/full pairs | Created in code: `SqliteAbbreviationRepository.ensureTable()` |
| `prolist.db` | `app/db/prolist.db` | Problem list entries | Created in code: `SqliteProblemRepository.init()` |
| `plan_history.db` | `app/db/plan_history.db` | Plan follow-up history | Created in code: `SqlitePlanHistoryRepository.init()` |
| `history.db` | `app/db/history.db` | Family history conditions by category | Created in code: `JdbcHistoryRepository.initializeDatabase()` |
| `references.db` | `app/db/references.db` | Reference metadata (category/content/path) | Created in code: `SqliteReferenceRepository.createTable()` |
| `emr_templates.db` | `app/db/emr_templates.db` | EMR templates (name/content) | Created in code: `TemplateRepository.createTableIfNotExists()` |
| `med_data.db` | `app/db/med_data.db` | Medication categories/groups/items | Created in code: `features.medication.db.DatabaseManager.initializeDatabase()` |
| `ClinicalLabItemsSqlite3.db` | `app/db/ClinicalLabItemsSqlite3.db` | Clinical lab catalog and ranges | Prebuilt DB file; used by `ClinicalLabDatabase` |
| `kcd_database.db` | `app/src/main/resources/database/kcd_database.db` | KCD codes table | Prebuilt DB + importer `CsvToSqliteImporter` |

Also present:
- `app/med_data.db` (legacy/duplicate location; keep `app/db/med_data.db` as source of truth)

## 3) Current table schemas (quick reference)

- `abbreviations.db`: `abbreviations(short PRIMARY KEY, full)`
- `prolist.db`: `problems(id, problem_text UNIQUE)`
- `plan_history.db`: `plan_history(id, created_at, section, content, patient_id, encounter_date)`
- `history.db`: `conditions(id, category, name, UNIQUE(category,name))`
- `references.db`: `references(id, category, contents, directory_path)` + category/contents indexes
- `emr_templates.db`: `templates(id, name, content)`
- `med_data.db`: `categories`, `medication_groups`, `medication_items`
- `ClinicalLabItemsSqlite3.db`: `clinical_lab_items(...)`
- `kcd_database.db`: `kcd_codes(classification, disease_code, check_field, note, korean_name, english_name)`

To inspect live schema:
```bash
sqlite3 app/db/med_data.db ".schema"
sqlite3 app/db/references.db ".schema"
sqlite3 app/src/main/resources/database/kcd_database.db ".schema"
```

## 4) Initialize databases

### Automatic initialization (preferred)
Run app and open related features; many DBs auto-create tables on first access.

```bash
./gradlew :app:run
```

Auto-created by code when accessed:
- `abbreviations.db`, `prolist.db`, `plan_history.db`, `history.db`, `references.db`, `emr_templates.db`, `med_data.db`

Not fully auto-created from empty file:
- `ClinicalLabItemsSqlite3.db` (expects existing table/data)
- `kcd_database.db` (expects existing `kcd_codes`, or importer/manual SQL)

### Manual initialization (if needed)
Create empty runtime DB files:
```bash
mkdir -p app/db
sqlite3 app/db/abbreviations.db ".databases"
sqlite3 app/db/prolist.db ".databases"
```

For KCD from CSV, use importer logic in:
- `app/src/main/java/com/emr/gds/features/kcd/db/CsvToSqliteImporter.java`

## 5) Migrate / update strategy

There is no centralized migration framework (e.g., Flyway/Liquibase) yet.
Use safe manual SQL migrations per DB.

Recommended workflow:
1. Backup DB first.
2. Apply idempotent SQL (`ALTER TABLE`, `CREATE INDEX IF NOT EXISTS`, backfill).
3. Verify with `.schema` and smoke test in app.

Example migration run:
```bash
cp app/db/references.db app/db/references.db.bak
sqlite3 app/db/references.db < migrations/references/2026-02-26_add_index.sql
sqlite3 app/db/references.db ".schema"
```

For prebuilt datasets (`ClinicalLabItemsSqlite3.db`, `kcd_database.db`):
- Update source DB in controlled branch.
- Validate row counts and key queries.
- Replace file atomically and keep backup copy outside Git.

## 6) Update/seed operations by DB type

- User-editable runtime DBs (`app/db/*.db`): updated by app features at runtime.
- Resource/reference DBs (`app/src/main/resources/database/*.db`): treat as seed/reference assets.
- KCD CSV source:
  - `app/src/main/resources/database/KCD-9master_4digit.csv`
  - Keep CSV and DB in sync if regenerating KCD DB.

## 7) Safety notes for Git commits

SQLite files are binary and can easily cause merge conflicts or accidental PHI/data leakage.

Safety rules:
- Do not commit patient-generated runtime DB changes by default.
- Commit DBs only when intentionally shipping curated seed/reference data.
- Always review `git diff --name-only` before commit.
- Keep backups outside repo when doing data migrations.

Suggested `.gitignore` patterns:
```gitignore
# Runtime/local SQLite files
app/db/*.db
app/db/*.db-*
app/db/*.sqlite
app/db/*.sqlite3

# SQLite temp/WAL files
*.db-wal
*.db-shm
*.db-journal

# Optional: ignore local DB exports/backups
*.sqlitedump
*.bak
```

If you must version selected seed DBs, use allowlist exceptions (example):
```gitignore
app/src/main/resources/database/*.db
!app/src/main/resources/database/abbreviations.db
!app/src/main/resources/database/emr_templates.db
!app/src/main/resources/database/prolist.db
!app/src/main/resources/database/kcd_database.db
```

## 8) Verification checklist

```bash
# 1) Build and run
./gradlew clean :app:run

# 2) Confirm DB files exist
find app/db -maxdepth 1 -name '*.db' | sort

# 3) Confirm schemas
sqlite3 app/db/history.db ".schema"
sqlite3 app/db/plan_history.db ".schema"
```
