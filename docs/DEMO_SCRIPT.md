# SecureScope Demo Script

This is a simple script I can use when walking through the project in an interview.

## 1. Start The Backend

For a local demo, the backend uses H2 by default. PostgreSQL is not required.

Start Spring Boot:

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

## 2. Start The Frontend

```powershell
cd securescope-frontend
npm start
```

Frontend URL:

```text
http://localhost:4200
```

## 3. Register And Login

Open the frontend and create a user account.

Talking point:

> The backend uses Spring Security, BCrypt password hashing, and JWT tokens. The Angular app stores the session in localStorage and attaches the token through an HTTP interceptor.

## 4. Run A Quick Code Scan

Open Quick Code Scan and paste something like:

```javascript
const password = "super-secret-password";
MessageDigest.getInstance("MD5");
```

Run the scan.

Talking point:

> This flow scans pasted text directly. The backend runs secret and risky-pattern rules, masks sensitive evidence, calculates a score, saves the scan, and returns the result to the UI.

## 5. Create A Project And Upload ZIP

Create a project from the Projects page.

On the project detail page:

1. Choose a ZIP file.
2. Upload it.
3. Start the scan.

Talking point:

> ZIP files are extracted into a temporary folder. The extractor prevents Zip Slip by normalizing paths and making sure each extracted file stays inside the target folder. Large files and unsupported folders are skipped.

## 6. Scan A GitHub Repository

On the project detail page:

1. Enter a public GitHub repository URL.
2. Connect it.
3. Start a GitHub scan.

Talking point:

> GitHub scanning validates the URL, downloads the public repository ZIP through GitHub's zipball API, and reuses the same safe extraction and scanner engine used by ZIP uploads.

## 7. View Findings

Open Findings.

Try filters:

- severity
- category
- OWASP category
- status

Update a finding status.

Talking point:

> Findings are normalized so the UI can filter and display different scanner results in one table. Evidence stays masked.

## 8. View Dashboard

Open Dashboard.

Talking point:

> The dashboard reads saved scans and findings for the logged-in user. It shows project count, scan count, average score, high and critical findings, severity distribution, OWASP distribution, and recent scans.

## 9. Generate Report

Open Reports.

1. Pick a scan.
2. Generate report.
3. Preview the HTML report.
4. Download the report.

Talking point:

> Reports are generated from saved scan data. The current implementation stores HTML report content and report metadata in the configured database.

## 10. Architecture Explanation

Short version:

> SecureScope is an Angular and Spring Boot app. Angular handles the authenticated UI and calls REST APIs. Spring Boot owns authentication, projects, scans, findings, dashboard summaries, and reports. The scanner is rule-based and split into small services for secrets, risky code patterns, dependencies, OWASP mapping, and scoring.

Good things to mention:

- JWT authentication
- owner-scoped data access
- safe ZIP extraction
- masked secret evidence
- scan persistence
- dashboard aggregation
- HTML report generation
- tests for scanner services and Angular services
