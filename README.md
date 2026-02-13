# SecureScope

SecureScope is a full-stack security scanning dashboard built with Angular, Spring Boot, PostgreSQL, and JWT authentication.

The project lets a user scan pasted code, uploaded ZIP files, or public GitHub repositories. It looks for common security issues such as hardcoded secrets, risky code patterns, and known vulnerable dependency versions from a small mock vulnerability database. Results are saved, mapped to OWASP-style categories, scored, shown in a dashboard, and exported as HTML reports.

I built this project to understand how security scanning tools work at a basic level: request handling, archive extraction, rule-based scanning, finding storage, score calculation, and developer-friendly reporting.

## Short Project Summary

SecureScope is not a production SAST platform. It is a portfolio project that demonstrates how a security scanning workflow can be designed end to end:

- Angular frontend for authentication, scan forms, dashboards, findings, projects, and reports
- Spring Boot backend for APIs, authentication, persistence, scanning, and reports
- PostgreSQL for users, projects, scans, findings, and report metadata
- Rule-based scanner for secrets, risky patterns, and dependency checks
- HTML report generation with masked evidence

## Why I Built This Project

Security tools can feel like a black box. I wanted to build a smaller version of the workflow myself so I could understand the moving parts:

- how code or project files enter the system
- how scan rules are organized
- how findings are normalized and stored
- how severity and security score can be calculated
- how a frontend can make security results easier to read
- how reports can be generated without exposing full secrets

It also gave me a practical full-stack project that combines backend APIs, frontend state, authentication, database design, testing, and security-focused business logic.

## Problem It Solves

Developers often need quick feedback before code reaches a formal review or CI pipeline. SecureScope helps with early checks for:

- secrets accidentally committed into code
- risky patterns such as weak hashing, disabled CSRF, permissive CORS, and SQL string concatenation
- dependency versions that match a small built-in mock vulnerability database
- project-level security trends across scans

The goal is simple feedback, not complete coverage.

## Main Features

- Register and login with JWT authentication
- Run a Quick Code Scan from pasted code
- Create and manage projects
- Upload a project ZIP and scan supported files
- Connect a public GitHub repository URL and scan its downloaded ZIP archive
- Detect hardcoded passwords, tokens, API keys, JWT secrets, and private key headers
- Detect risky code patterns such as MD5, SHA1, disabled CSRF, permissive CORS, and SQL concatenation
- Parse Maven `pom.xml` and npm `package.json`
- Flag example vulnerable dependencies from a mock vulnerability database
- Map findings to OWASP-style categories
- Calculate a security score and risk level
- View and update finding status
- View dashboard metrics and charts
- Generate and download HTML security reports
- Mask sensitive evidence in API responses, UI, and reports

## Tech Stack

### Frontend

- Angular
- TypeScript
- Angular Material
- RxJS
- SCSS
- Chart.js

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- JWT authentication

## Architecture Overview

SecureScope uses a standard frontend/backend split.

```text
Angular UI
  -> Auth interceptor adds JWT
  -> Spring Boot REST APIs
  -> Scanner services run rule checks
  -> PostgreSQL stores users, projects, scans, findings, and reports
```

The scanner is intentionally rule-based. It reads code as text, splits it into lines, applies regex and parser-based checks, creates normalized findings, calculates a score, and stores the scan result.

More detail is available in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Backend Overview

The backend lives in:

```text
securescope-backend/securescope-backend
```

Important packages:

- `auth`: register, login, current user, JWT service, security user details
- `config`: Spring Security and web configuration
- `dashboard`: user-scoped summary and chart data
- `finding`: finding list, details, filtering, and status updates
- `project`: project CRUD and project scan history
- `report`: HTML report generation, metadata, preview, and download
- `scanner`: quick scan, ZIP scan, GitHub scan, rules, dependency checks, OWASP mapping, scoring
- `persistence.entity`: JPA entities
- `persistence.repository`: Spring Data repositories
- `common`: shared responses, enums, exceptions, and masking utility

## Frontend Overview

The frontend lives in:

```text
securescope-frontend
```

Main feature areas:

- `features/home`: landing page and scan entry points
- `features/auth`: login and register pages
- `features/dashboard`: security overview cards, charts, and recent scans
- `features/projects`: project CRUD, ZIP upload scan, GitHub scan
- `features/quick-scan`: pasted code scan flow
- `features/findings`: finding filters, details, and status updates
- `features/reports`: report list, preview, and download
- `layout`: header, sidebar, main layout
- `shared/components`: reusable UI pieces such as empty states, severity chips, and score badges

## Scanner Engine Overview

The scanner engine is split into small services:

- `SecretScannerService`: detects hardcoded secrets
- `RiskyPatternScannerService`: detects simple risky code patterns
- `DependencyScannerService`: parses dependency files and checks mock vulnerability data
- `OwaspMappingService`: maps scanner concepts to OWASP-style labels
- `SecurityScoringService`: turns findings into a score and risk level
- `ProjectArchiveScanService`: shared scanning and persistence for ZIP and GitHub archives

The scanner does static checks only. It does not execute code, run data-flow analysis, or inspect runtime behavior.

## Quick Code Scan Flow

1. User opens `/quick-scan`.
2. User enters snippet name, language, file name, and code content.
3. Angular sends `POST /api/scans/quick-code`.
4. Backend runs secret and risky-pattern rules.
5. Backend calculates score and risk level.
6. Scan and findings are saved to PostgreSQL.
7. UI displays score, risk, total findings, and a findings table.

## ZIP Upload Scan Flow

1. User opens a project detail page.
2. User uploads a `.zip` file with `POST /api/projects/{projectId}/upload`.
3. Backend validates file type and file size.
4. Backend extracts the ZIP into a temporary folder.
5. Zip Slip protection rejects unsafe paths.
6. Ignored folders such as `node_modules`, `target`, `dist`, and `.git` are skipped.
7. Supported source/config files are scanned.
8. Scan and findings are saved and linked to the project.

Supported files:

- `.java`
- `.ts`
- `.js`
- `.json`
- `.yml`
- `.yaml`
- `.properties`
- `.xml`
- `.env`

## GitHub Repository Scan Flow

1. User enters a public GitHub repository URL on a project detail page.
2. Backend validates that the URL points to `github.com/{owner}/{repo}` and uses HTTPS.
3. The repository URL is saved on the project.
4. User starts a GitHub scan.
5. Backend downloads the public repository ZIP using GitHub's zipball API.
6. The same safe ZIP extraction logic is used.
7. Supported files are scanned.
8. Scan and findings are saved and linked to the project.

Current GitHub scanning works for public repositories only. GitHub OAuth and private repository access are future work.

## Secret Scanning Logic

The secret scanner checks each line for:

- hardcoded password names such as `password`, `passwd`, and `pwd`
- hardcoded token names such as `token`, `access_token`, and `auth_token`
- API key names such as `api_key`, `apikey`, `apiKey`, and `x-api-key`
- JWT secret names such as `jwt_secret`, `jwtSecret`, and `jwt.secret`
- private key block headers

Sensitive values are masked before they are returned or saved.

## Dependency Vulnerability Scanning Logic

Dependency scanning currently supports:

- Maven `pom.xml`
- npm `package.json`

The implementation extracts dependency name and version, then compares them with a small mock vulnerability database. Example checked packages include:

- `log4j-core`
- `spring-core`
- `jackson-databind`
- `lodash`
- `axios`

This is not connected to a real CVE feed yet.

## OWASP Top 10 Mapping Logic

Findings are mapped to OWASP-style categories based on the rule type:

- secrets and weak crypto: `A02:2021 - Cryptographic Failures`
- SQL string concatenation: `A03:2021 - Injection`
- disabled CSRF and permissive CORS: `A05:2021 - Security Misconfiguration`
- vulnerable dependencies: `A06:2021 - Vulnerable and Outdated Components`

The mapping is intentionally simple and lives in `OwaspMappingService`.

## Security Scoring Logic

Scores start at `100`. Findings subtract points by severity:

- `CRITICAL`: 30
- `HIGH`: 20
- `MEDIUM`: 10
- `LOW`: 5
- `INFO`: 1

The score is never lower than `0`.

Risk levels:

- `90-100`: `SAFE`
- `75-89`: `LOW`
- `50-74`: `MODERATE`
- `25-49`: `HIGH`
- `0-24`: `CRITICAL`

## Report Generation Logic

Reports are generated from saved scans.

Implemented report format:

- HTML

Report content includes:

- project name
- scan date
- scan source
- security score
- risk level
- severity summary
- OWASP summary
- findings table
- dependency findings
- secret findings
- recommendations
- security disclaimer

PDF export is not implemented yet. It is listed as a future improvement.

## Database Tables Overview

The main JPA entities are:

- `User`: account details and authentication identity
- `Project`: project metadata, source type, technology, and optional GitHub URL
- `Scan`: saved scan metadata, source type, status, score, risk, and owner
- `Finding`: normalized security finding linked to a scan
- `Report`: saved report metadata and generated HTML content

`AuditableEntity` provides common ID and timestamp fields.

Hibernate is currently configured with `ddl-auto: update` for local development.

## API Overview

More detail is available in [docs/API_CONTRACT.md](docs/API_CONTRACT.md).

Main API groups:

- Auth: register, login, current user
- Projects: CRUD and scan history
- Quick Scan: pasted code scan and saved scan access
- ZIP Scan: upload archive and start project scan
- GitHub Scan: connect repository and scan public repository ZIP
- Findings: filter, detail, and status update
- Dashboard: summary, severity, OWASP, and score trend
- Reports: generate, list, preview, and download HTML reports

## How To Run Backend

Prerequisites:

- Java 17 or newer
- Maven wrapper included in the backend project

The backend uses the `local` profile by default. That profile runs with a file-based H2 database, so PostgreSQL is not required for local development.

Default local H2 settings:

```text
JDBC URL: jdbc:h2:file:./data/securescope-db
Username: sa
Password: empty
Console: http://localhost:8080/h2-console
```

Run the backend with H2:

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

H2 stores local database files under:

```text
securescope-backend/securescope-backend/data
```

PostgreSQL support is still available through the `postgres` profile for future use.

PostgreSQL settings:

```text
Host: localhost
Port: 5432
Database: securescope_db
Username: postgres
Password: postgres
```

To run with PostgreSQL later, create the database:

```sql
CREATE DATABASE securescope_db;
```

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

## How To Run Frontend

Install dependencies:

```powershell
cd securescope-frontend
npm install
```

Run the frontend:

```powershell
cd securescope-frontend
npm start
```

Frontend runs at:

```text
http://localhost:4200
```

## How To Build Backend

Run tests:

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd test
```

Package backend:

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd package -DskipTests
```

## How To Build Frontend

Run tests:

```powershell
cd securescope-frontend
npm test -- --watch=false --browsers=ChromeHeadless
```

Build frontend:

```powershell
cd securescope-frontend
npm run build
```

Note: the Angular build currently passes with a bundle budget warning because the warning threshold is still set to `500 kB`.

## Screenshots

Screenshots are not checked into the repository yet. Placeholder locations:

- Home Page: `docs/screenshots/home.png`
- Quick Code Scan Page: `docs/screenshots/quick-scan.png`
- Dashboard Page: `docs/screenshots/dashboard.png`
- Findings Page: `docs/screenshots/findings.png`
- Report Page: `docs/screenshots/reports.png`

The `docs/screenshots` folder is present with a `.gitkeep` file.

## Demo Flow

A simple demo path:

1. Start PostgreSQL and create `securescope_db`.
2. Start the Spring Boot backend.
3. Start the Angular frontend.
4. Register a user.
5. Log in.
6. Run a Quick Code Scan with a hardcoded password and weak hash.
7. Create a project.
8. Upload a ZIP or connect a public GitHub repository.
9. Run a project scan.
10. Open Findings and update a finding status.
11. Open Dashboard and explain the metrics.
12. Generate an HTML report and download it.

More interview notes are in [docs/DEMO_SCRIPT.md](docs/DEMO_SCRIPT.md).

## Known Limitations

- The scanner is rule-based and does not perform full static analysis.
- Dependency vulnerability data is mocked, not pulled from a real advisory feed.
- GitHub scanning supports public repositories only.
- ZIP scans read supported files as UTF-8 text.
- PDF report generation is not implemented yet.
- No SARIF export yet.
- No CI/CD scan trigger yet.
- No team or organization workspace yet.
- Database migrations are not configured; Hibernate `ddl-auto: update` is used for local development.

## Future Improvements

- Use a real vulnerability database such as OSV or NVD
- Add GitHub OAuth for private repositories
- Add CI/CD scan triggers
- Export SARIF
- Add deeper SAST rules
- Add team workspaces
- Improve false positive management
- Add AI-assisted fix explanations
- Add Docker deployment
- Harden backend security configuration for production
- Add Flyway or Liquibase migrations

More detail is in [docs/FUTURE_BACKLOG.md](docs/FUTURE_BACKLOG.md).

## Security Disclaimer

SecureScope performs static checks and basic rule-based scanning. It does not replace professional penetration testing, manual secure code review, enterprise SAST/SCA tools, threat modeling, compliance assessment, or runtime security testing.

Findings should be treated as early feedback. A clean SecureScope scan does not prove that an application is secure.

Sensitive values should stay masked in API responses, UI, logs, and reports.
