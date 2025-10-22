# SecureScope

SecureScope is a flagship full-stack security scanning dashboard for developers who want fast, understandable security feedback before code reaches production.

The project will be built feature by feature with a clean Angular frontend, a Spring Boot backend, PostgreSQL persistence, JWT authentication, and developer-friendly scan reports.

## Project Overview

SecureScope will allow users to scan source code and project artifacts for common application security risks. The dashboard will focus on clear findings, masked sensitive values, OWASP Top 10 mapping, practical remediation guidance, and exportable reports.

## SecureScope Features

- Quick Code Scan for pasted snippets
- Project ZIP upload and scan
- GitHub repository connection and scan
- Hardcoded secret detection with masked output
- Vulnerable dependency detection
- Risky code pattern detection
- Missing security best practice checks
- OWASP Top 10 mapped findings
- Security score calculation
- Developer-friendly fix recommendations
- HTML and PDF security reports
- Authenticated user dashboard
- Scan history and report management

## Tech Stack

### Frontend

- Angular
- TypeScript
- Angular Material
- RxJS
- SCSS
- Chart.js

### Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- JWT authentication

## Backend Setup

The Spring Boot backend project is located at:

```text
securescope-backend/securescope-backend
```

### Prerequisites

- Java 17 or newer
- Maven wrapper included with the backend project

### Run the Backend

From the backend project folder:

```powershell
cd securescope-backend/securescope-backend
./mvnw spring-boot:run
```

On Windows PowerShell, you can also run:

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd spring-boot:run
```

### Validate the Backend Build

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd clean test
```

### Database Setup

SecureScope backend is configured for PostgreSQL.

Default local database settings:

```text
Database: securescope_db
Host: localhost
Port: 5432
Username: postgres
Password: postgres
```

Create the local database before running persistence-backed features:

```sql
CREATE DATABASE securescope_db;
```

The backend datasource is configured in:

```text
securescope-backend/securescope-backend/src/main/resources/application.yml
```

Run PostgreSQL locally, then start the backend:

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd spring-boot:run
```

Hibernate is currently set to `ddl-auto: update` for local development so the first tables can be created from the JPA entities. Add a migration tool before production deployment.

### Health Check API

```http
GET /api/health
```

Expected response:

```json
{
  "status": "UP",
  "message": "SecureScope backend is running"
}
```

### Authentication API

SecureScope uses Spring Security with BCrypt password hashing and JWT bearer tokens.

Public endpoints:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/health`

Protected endpoint:

- `GET /api/auth/me`

All other API routes require an `Authorization: Bearer <token>` header.

Register request:

```json
{
  "name": "SecureScope Developer",
  "email": "developer@example.com",
  "password": "password123"
}
```

Login request:

```json
{
  "email": "developer@example.com",
  "password": "password123"
}
```

Successful register and login responses include a JWT token and user details:

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "jwt-token",
    "tokenType": "Bearer",
    "user": {
      "id": "user-id",
      "name": "SecureScope Developer",
      "email": "developer@example.com"
    }
  },
  "timestamp": "..."
}
```

### Quick Code Scan API

```http
POST /api/scans/quick-code
```

This endpoint requires a JWT bearer token. Each quick scan is saved to PostgreSQL with the logged-in user as the owner. If `projectId` is provided, the scan is linked to that project only when the project belongs to the same user. If `projectId` is omitted or `null`, SecureScope saves the scan as a standalone quick scan.

Sample request:

```json
{
  "snippetName": "Auth sample",
  "language": "JavaScript",
  "fileName": "auth.js",
  "codeContent": "const password = \"super-secret-password\";\nMessageDigest.getInstance(\"MD5\");",
  "projectId": null
}
```

The scanner detects hardcoded passwords, tokens, API keys, JWT secrets, private key blocks, weak hash usage, disabled CSRF, permissive CORS, and SQL string concatenation. Sensitive evidence is masked before results are returned or saved.

Sample response data:

```json
{
  "scanId": "0f7b5ea7-2f09-4a5b-9858-0e2e35f0b71b",
  "status": "COMPLETED",
  "securityScore": 50,
  "riskLevel": "MODERATE",
  "totalFindings": 2,
  "findings": []
}
```

Saved scan APIs:

```http
GET /api/scans/{scanId}
GET /api/scans/{scanId}/findings
GET /api/scans/my-scans
```

Only the user who owns a scan can view the saved scan or its findings.

### Projects API

Project APIs require a JWT bearer token and are scoped to the logged-in user. Users can only create, view, update, or delete their own projects.

```http
POST /api/projects
GET /api/projects
GET /api/projects/{projectId}
PUT /api/projects/{projectId}
DELETE /api/projects/{projectId}
```

Project request:

```json
{
  "name": "SecureScope Web App",
  "description": "Angular and Spring Boot security scanning workspace",
  "sourceType": "GITHUB_REPOSITORY",
  "technology": "Angular, Spring Boot, PostgreSQL",
  "githubUrl": "https://github.com/example/securescope"
}
```

Project responses include scan history for scans linked to the project. Quick Code Scan can link to a project by sending the optional `projectId` field in the scan request.

### Dashboard API

Dashboard APIs require a JWT bearer token and return metrics only for the logged-in user.

```http
GET /api/dashboard/summary
GET /api/dashboard/severity-summary
GET /api/dashboard/owasp-summary
GET /api/dashboard/score-trend
```

The summary endpoint returns total projects, total scans, average security score, critical findings, high findings, and recent scans. The summary endpoints power the Angular security dashboard cards, severity chart, OWASP category chart, and recent scans table.

### Quick Code Scan UI

The Angular frontend includes a `/quick-scan` page that connects to:

```text
http://localhost:8080/api/scans/quick-code
```

The page includes fields for snippet name, language, file name, and code content. Results show the security score, risk level, total findings, and a findings table with severity, title, OWASP category, file path, and recommendation.

After a successful scan, the page also shows the saved PostgreSQL scan ID returned by the backend.

Start the backend before running the frontend scan page:

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd spring-boot:run
```

Then run the frontend:

```powershell
cd securescope-frontend
npm start
```

### Backend Architecture

The backend follows a feature-first package structure under `com.securescope`.

Current backend packages:

- `auth`: registration, login, current-user API, JWT service, and authentication filter
- `config`: application and web configuration
- `common.response`: shared API response models
- `common.dto`: shared DTOs used across backend layers
- `common.exception`: custom exceptions and global exception handling
- `common.enums`: scan, finding, severity, and risk enums
- `common.util`: reusable utility helpers such as sensitive value masking
- `dashboard`: user-scoped security summary, severity, OWASP, and score trend APIs
- `health`: health check API
- `persistence.entity`: JPA entities for users, projects, scans, and findings
- `persistence.repository`: Spring Data JPA repositories for persistence access
- `project`: owner-scoped project CRUD APIs, DTOs, and project scan history responses
- `scanner`: quick code scanner orchestration, secret rules, risky pattern rules, OWASP mapping, scoring, and scanner DTOs

## Frontend Setup

The Angular frontend project is located at:

```text
securescope-frontend
```

### Prerequisites

- Node.js 20 or newer
- npm

### Install Dependencies

From the frontend project folder:

```powershell
cd securescope-frontend
npm install
```

On Windows PowerShell, if script execution blocks `npm`, use:

```powershell
npm.cmd install
```

### Run the Frontend

```powershell
cd securescope-frontend
npm start
```

The Angular development server runs at:

```text
http://localhost:4200
```

### Validate the Frontend Build

```powershell
cd securescope-frontend
npm run build
```

Current routes:

- `/`
- `/login`
- `/register`
- `/dashboard`
- `/quick-scan`
- `/projects`
- `/projects/new`
- `/projects/:projectId`
- `/projects/:projectId/edit`
- `/findings`
- `/reports`

### Frontend Authentication Flow

The Angular frontend includes login and register pages that call the Spring Boot authentication API:

- `POST http://localhost:8080/api/auth/register`
- `POST http://localhost:8080/api/auth/login`

After successful login or registration, the JWT token and user details are stored in `localStorage`. The frontend auth interceptor attaches the token to outgoing API requests with:

```text
Authorization: Bearer <token>
```

Protected frontend routes redirect unauthenticated users to `/login`:

- `/dashboard`
- `/projects`
- `/projects/new`
- `/projects/:projectId`
- `/projects/:projectId/edit`
- `/quick-scan`
- `/findings`
- `/reports`

Logged-in users who open `/login` or `/register` are redirected to `/dashboard`. Header and sidebar logout actions clear the stored session.

### Security Dashboard UI

The Angular `/dashboard` page calls the dashboard APIs and displays:

- Total Projects
- Total Scans
- Average Security Score
- Critical Findings
- High Findings
- Severity breakdown chart
- OWASP category breakdown chart
- Recent scans table

## Planned Modules

- Authentication and user accounts
- Scan request management
- Quick code scanner
- ZIP project scanner
- GitHub repository scanner
- Secret detection engine
- Dependency vulnerability analyzer
- Risk pattern analyzer
- OWASP mapping service
- Security scoring service
- Report generation service
- Dashboard analytics
- Admin and audit views

## Development Roadmap

1. Workspace initialization and project documentation
2. Backend foundation with Spring Boot dependencies and configuration
3. Authentication with JWT and role-based access
4. Quick Code Scan API
5. Secret detection rules and masked findings
6. Security score and OWASP mapping
7. Angular application foundation
8. Quick Code Scan UI
9. Scan history and report details
10. ZIP upload scanner
11. GitHub repository scanner
12. HTML/PDF report generation
13. Dashboard charts and analytics
14. Testing, validation, and deployment preparation

## Security Disclaimer

SecureScope is intended to support developer security reviews and learning. It should not be treated as a complete replacement for professional penetration testing, secure code review, dependency auditing, threat modeling, or compliance assessment.

Sensitive values must be masked in the UI, API responses, logs, and reports. The project should never intentionally expose full secrets discovered during scans.
