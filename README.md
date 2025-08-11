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
- Chart.js or ApexCharts

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

### Quick Code Scan API

```http
POST /api/scans/quick-code
```

Sample request:

```json
{
  "snippetName": "Auth sample",
  "language": "JavaScript",
  "fileName": "auth.js",
  "codeContent": "const password = \"super-secret-password\";\nMessageDigest.getInstance(\"MD5\");"
}
```

The first scanner engine version runs in memory and does not use a database. It detects hardcoded passwords, tokens, API keys, JWT secrets, private key blocks, weak hash usage, disabled CSRF, permissive CORS, and SQL string concatenation. Sensitive evidence is masked before returning results.

### Backend Architecture

The backend follows a feature-first package structure under `com.securescope`.

Current backend packages:

- `config`: application and web configuration
- `common.response`: shared API response models
- `common.dto`: shared DTOs used across backend layers
- `common.exception`: custom exceptions and global exception handling
- `common.enums`: scan, finding, severity, and risk enums
- `common.util`: reusable utility helpers such as sensitive value masking
- `health`: health check API
- `scanner`: quick code scanner orchestration, secret rules, risky pattern rules, OWASP mapping, scoring, and scanner DTOs

Common response and exception handling are intentionally database-free. Persistence, JPA, PostgreSQL, and security will be added in later features.

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
- `/findings`
- `/reports`

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
