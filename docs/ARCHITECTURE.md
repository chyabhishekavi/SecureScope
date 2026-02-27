# SecureScope Architecture

This document explains how SecureScope is put together at a practical level. It is meant to help someone understand the project without reading every class first.

## Overall Architecture

SecureScope has two main applications:

- Angular frontend in `securescope-frontend`
- Spring Boot backend in `securescope-backend/securescope-backend`

The frontend talks to the backend through REST APIs. After login, Angular stores the JWT in `localStorage` and sends it on protected API calls through the auth interceptor.

```text
Browser
  -> Angular pages and services
  -> JWT auth interceptor
  -> Spring Boot controllers
  -> Services and scanner modules
  -> Database through Spring Data JPA
```

The backend owns authentication, project data, scan execution, finding storage, dashboard summaries, and report generation. Local development uses H2. A PostgreSQL profile is kept for future use.

## Backend Modules

The backend uses a feature-based package layout under `com.securescope`.

- `auth`: registration, login, current user, JWT generation, user details
- `config`: Spring Security and web configuration
- `common`: shared API response, enums, exceptions, and masking utilities
- `dashboard`: user-scoped dashboard summary and chart data
- `finding`: finding list, detail, filters, and status update
- `health`: basic health API
- `persistence.entity`: JPA entities
- `persistence.repository`: Spring Data repositories
- `project`: project CRUD and project scan history
- `report`: HTML report generation and report metadata
- `scanner`: scanner rules, scan orchestration, ZIP scanning, GitHub scanning, scoring, and OWASP mapping

## Frontend Modules

The Angular app is split by feature.

- `features/home`: landing page
- `features/auth`: login and registration
- `features/dashboard`: dashboard cards, charts, and recent scans
- `features/projects`: project list, project form, project detail, ZIP scan, GitHub scan
- `features/quick-scan`: pasted code scan form and result table
- `features/findings`: finding filters, table, details, and status updates
- `features/reports`: report list, report preview, and download
- `layout`: header, sidebar, and main layout
- `core/services`: HTTP services for backend APIs
- `core/models`: TypeScript interfaces for API payloads
- `shared/components`: reusable UI components

## Scanner Engine Design

The scanner is intentionally simple and rule-based. It does not compile code or build a full AST.

Core scanner services:

- `SecretScannerService`
- `RiskyPatternScannerService`
- `DependencyScannerService`
- `OwaspMappingService`
- `SecurityScoringService`
- `ProjectArchiveScanService`

Quick scans pass pasted text into the scanner. ZIP and GitHub scans first extract supported files, then pass file contents through the same scanning services.

## Database Flow

The main entities are:

- `User`
- `Project`
- `Scan`
- `Finding`
- `Report`

Typical flow:

1. User registers or logs in.
2. A project may be created.
3. A scan is started from pasted code, ZIP upload, or GitHub repository.
4. The backend creates a `Scan` row.
5. Each detected issue becomes a `Finding` row linked to that scan.
6. Dashboard APIs aggregate scan and finding data for the logged-in user.
7. Report APIs create a `Report` row with generated HTML content.

## Scan Lifecycle

Most scans follow this shape:

```text
Request received
  -> validate input and owner
  -> prepare source files or code text
  -> run scanner rules
  -> calculate score and risk level
  -> save Scan
  -> save Findings
  -> return ScanResult DTO
```

Current scan statuses include values such as `PENDING`, `RUNNING`, `COMPLETED`, `FAILED`, and `CANCELLED`. The implemented scan flows currently save completed scans synchronously.

## How Findings Are Generated

Findings use a common DTO shape:

- title
- description
- severity
- category
- OWASP category
- file path
- line number
- masked evidence
- recommendation

Secret and risky-pattern rules scan line by line. Dependency rules parse supported manifest files and compare dependencies against the mock vulnerability database.

## How The Dashboard Gets Data

Dashboard APIs are scoped to the logged-in user. The dashboard service queries saved projects, scans, and findings to return:

- total projects
- total scans
- average security score
- critical finding count
- high finding count
- severity summary
- OWASP summary
- score trend
- recent scans

The Angular dashboard renders cards, charts, and the recent scan table from those endpoints.

## How Reports Are Generated

Report generation starts from a saved scan.

1. User clicks Generate Report.
2. Angular calls `POST /api/scans/{scanId}/reports`.
3. Backend confirms the scan belongs to the logged-in user.
4. `ReportHtmlBuilder` builds an HTML report from scan, project, and findings data.
5. Report metadata and HTML content are saved.
6. UI can preview the report or download it as an `.html` file.

PDF generation is not implemented yet.
