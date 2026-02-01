# SecureScope API Contract

All protected APIs require:

```http
Authorization: Bearer <jwt-token>
```

Most responses use the shared wrapper:

```json
{
  "success": true,
  "message": "Message",
  "data": {},
  "timestamp": "..."
}
```

## Auth APIs

### Register

- Method: `POST`
- Endpoint: `/api/auth/register`
- Purpose: create a user account and return a JWT.

Request:

```json
{
  "name": "SecureScope Developer",
  "email": "developer@example.com",
  "password": "password123"
}
```

Response data:

```json
{
  "token": "jwt-token",
  "tokenType": "Bearer",
  "user": {
    "id": "user-id",
    "name": "SecureScope Developer",
    "email": "developer@example.com"
  }
}
```

### Login

- Method: `POST`
- Endpoint: `/api/auth/login`
- Purpose: authenticate an existing user and return a JWT.

Request:

```json
{
  "email": "developer@example.com",
  "password": "password123"
}
```

### Current User

- Method: `GET`
- Endpoint: `/api/auth/me`
- Purpose: return the logged-in user.

## Project APIs

### Create Project

- Method: `POST`
- Endpoint: `/api/projects`
- Purpose: create a project owned by the logged-in user.

Request:

```json
{
  "name": "SecureScope Demo",
  "description": "Project used for scanning demos",
  "sourceType": "GITHUB_REPOSITORY",
  "technology": "Angular, Spring Boot",
  "githubUrl": "https://github.com/example/securescope"
}
```

### List Projects

- Method: `GET`
- Endpoint: `/api/projects`
- Purpose: list projects for the logged-in user.

### Get Project

- Method: `GET`
- Endpoint: `/api/projects/{projectId}`
- Purpose: load one owned project and its scan history.

### Update Project

- Method: `PUT`
- Endpoint: `/api/projects/{projectId}`
- Purpose: update an owned project.

### Delete Project

- Method: `DELETE`
- Endpoint: `/api/projects/{projectId}`
- Purpose: delete an owned project.

## Quick Scan APIs

### Run Quick Code Scan

- Method: `POST`
- Endpoint: `/api/scans/quick-code`
- Purpose: scan pasted code and save the result.

Request:

```json
{
  "snippetName": "Auth sample",
  "language": "JavaScript",
  "fileName": "auth.js",
  "codeContent": "const password = \"super-secret-password\";",
  "projectId": null
}
```

Response data:

```json
{
  "scanId": "scan-id",
  "status": "COMPLETED",
  "securityScore": 70,
  "riskLevel": "MODERATE",
  "totalFindings": 1,
  "findings": []
}
```

### Get Scan

- Method: `GET`
- Endpoint: `/api/scans/{scanId}`
- Purpose: load one owned scan.

### Get Scan Findings

- Method: `GET`
- Endpoint: `/api/scans/{scanId}/findings`
- Purpose: load findings for one owned scan.

### My Scans

- Method: `GET`
- Endpoint: `/api/scans/my-scans`
- Purpose: list scans for the logged-in user.

## ZIP Scan APIs

### Upload ZIP

- Method: `POST`
- Endpoint: `/api/projects/{projectId}/upload`
- Purpose: upload and validate a project ZIP.
- Body: multipart form data with field `file`.

Response data:

```json
{
  "uploadId": "upload-id",
  "projectId": "project-id",
  "fileName": "project.zip",
  "fileSizeBytes": 12345,
  "extractedFileCount": 8,
  "skippedEntryCount": 3,
  "uploadedAt": "..."
}
```

### Start ZIP Scan

- Method: `POST`
- Endpoint: `/api/projects/{projectId}/scans`
- Purpose: scan a previously uploaded ZIP.

Request:

```json
{
  "uploadId": "upload-id"
}
```

## GitHub Scan APIs

### Connect GitHub Repository

- Method: `POST`
- Endpoint: `/api/projects/{projectId}/github/connect`
- Purpose: validate and store a public GitHub repository URL on the project.

Request:

```json
{
  "repositoryUrl": "https://github.com/example/securescope"
}
```

Response data:

```json
{
  "projectId": "project-id",
  "repositoryUrl": "https://github.com/example/securescope",
  "repositoryName": "example/securescope"
}
```

### Scan GitHub Repository

- Method: `POST`
- Endpoint: `/api/projects/{projectId}/github/scan`
- Purpose: download a public repository ZIP and scan supported files.

Request:

```json
{
  "repositoryUrl": "https://github.com/example/securescope"
}
```

If `repositoryUrl` is omitted, the backend uses the URL saved on the project.

## Findings APIs

### List Findings

- Method: `GET`
- Endpoint: `/api/findings`
- Purpose: list findings owned by the logged-in user.

Supported filters:

- `severity`
- `category`
- `owaspCategory`
- `status`

Example:

```http
GET /api/findings?severity=HIGH&status=OPEN
```

### Get Finding

- Method: `GET`
- Endpoint: `/api/findings/{findingId}`
- Purpose: load one finding if it belongs to the user.

### Update Finding Status

- Method: `PATCH`
- Endpoint: `/api/findings/{findingId}/status`
- Purpose: update triage status.

Request:

```json
{
  "status": "FIXED"
}
```

Allowed statuses:

- `OPEN`
- `FIXED`
- `IGNORED`
- `FALSE_POSITIVE`

## Dashboard APIs

### Summary

- Method: `GET`
- Endpoint: `/api/dashboard/summary`
- Purpose: dashboard cards and recent scans.

### Severity Summary

- Method: `GET`
- Endpoint: `/api/dashboard/severity-summary`
- Purpose: severity chart data.

### OWASP Summary

- Method: `GET`
- Endpoint: `/api/dashboard/owasp-summary`
- Purpose: OWASP chart data.

### Score Trend

- Method: `GET`
- Endpoint: `/api/dashboard/score-trend`
- Purpose: score trend data.

## Report APIs

### Generate Report

- Method: `POST`
- Endpoint: `/api/scans/{scanId}/reports`
- Purpose: generate and save an HTML report for an owned scan.

### List Reports For Scan

- Method: `GET`
- Endpoint: `/api/scans/{scanId}/reports`
- Purpose: list reports generated for one scan.

### Preview Report

- Method: `GET`
- Endpoint: `/api/reports/{reportId}`
- Purpose: load report metadata and HTML content for preview.

### Download Report

- Method: `GET`
- Endpoint: `/api/reports/{reportId}/download`
- Purpose: download the generated HTML report.
