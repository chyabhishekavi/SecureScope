# SecureScope Demo Testing Checklist

This checklist is for a local demo run using the Spring Boot backend, Angular frontend, and the local H2 database profile.

## Backend Run Command

```powershell
cd securescope-backend/securescope-backend
.\mvnw.cmd spring-boot:run
```

Expected result:

- Backend starts on `http://localhost:8080`
- Health check works at `http://localhost:8080/api/health`
- Local H2 console is available at `http://localhost:8080/h2-console`

## Frontend Run Command

```powershell
cd securescope-frontend
npm start
```

Expected result:

- Angular starts on `http://localhost:4200`
- The frontend can call the backend at `http://localhost:8080`

## Demo User

```text
Name: Abhishek Kumar Choudhary
Email: abhishek.demo@securescope.dev
Password: Demo@123
```

## Testing Steps

| Step | Action | Expected Result |
| --- | --- | --- |
| 1 | Start the backend | Spring Boot starts without PostgreSQL by using the local H2 profile. |
| 2 | Open `http://localhost:8080/api/health` | Response shows `status: UP` and the backend running message. |
| 3 | Start the frontend | Angular starts on port `4200`. |
| 4 | Open `http://localhost:4200/register` | Register page loads. |
| 5 | Register the demo user | Account is created, or a clear duplicate-email message appears if the user already exists. |
| 6 | Log in with the demo user | User is redirected to `/dashboard`. |
| 7 | Open Dashboard | Summary cards, charts area, and recent scans section load. |
| 8 | Open Quick Code Scan | Form fields and Run Scan button are visible. |
| 9 | Run a scan with sample risky code | Scan result appears with security score, risk level, saved scan ID, and findings. |
| 10 | Open Findings | Findings list loads and shows severity, title, OWASP category, evidence/recommendation details where available. |
| 11 | Test logout | User is redirected to `/login` and protected pages require authentication again. |
| 12 | Check desktop layout | Dashboard, Quick Scan, and Findings pages fit a desktop viewport without broken layout. |
| 13 | Check mobile layout | Dashboard, Quick Scan, and Findings pages load on a mobile viewport without obvious layout breakage. |

## Sample Quick Scan Input

```javascript
const password = "super-secret-password";
const apiKey = "sk_live_1234567890abcdef";
const token = "ghp_1234567890abcdef1234567890abcdef1234";
const crypto = require('crypto');
const hash = crypto.createHash('md5').update(password).digest('hex');
const query = "SELECT * FROM users WHERE email = '" + email + "'";
```

Expected result:

- Secret findings are detected.
- Weak hash usage is detected.
- SQL string concatenation is detected when the rule matches the input.
- Sensitive evidence is masked.
- Findings are mapped to OWASP-style categories.

## Screenshot Checklist

Use real screenshots captured from the running local app. Final screenshots are stored under `docs/screenshots`.

| Screenshot | Final file | Expected content |
| --- | --- | --- |
| Home page | `docs/screenshots/01-home-page.png` | SecureScope home page with the hero section and scan option cards. |
| Registration page with Abhishek demo user | `docs/screenshots/02-registration-abhishek.png` | Register form filled with the Abhishek demo user details. |
| Login page | `docs/screenshots/03-login-page.png` | Missing for now. Capture this later if needed. |
| Dashboard after scans | `docs/screenshots/04-dashboard-overview.png` | Dashboard cards, severity chart, OWASP chart, and recent scans. |
| Quick Code Scan input | `docs/screenshots/05-quick-code-scan-input.png` | Quick scan form with sample insecure code before running the scan. |
| Quick Code Scan result | `docs/screenshots/06-quick-code-scan-result.png` | Security score, risk level, saved scan ID, and finding table after a scan. |
| Findings table | `docs/screenshots/07-findings-table.png` | Missing as a standalone findings-table screenshot. The scan result screenshot currently shows the findings table. |
| Report page | `docs/screenshots/08-report-page.png` | Reports page before selecting a scan. |

Optional extra screenshot:

- Projects empty state: `docs/screenshots/09-projects-empty-state.png`
- Findings empty state: `docs/screenshots/10-findings-empty-state.png`

## Local Notes

- The local profile uses the file-based H2 database at `jdbc:h2:file:./data/securescope-db`.
- If the demo user already exists, skip registration and log in with the same credentials.
- If the frontend shows a backend connection error, confirm that Spring Boot is running on port `8080`.
- If port `4200` is already in use, stop the existing Angular process or run Angular on another port.
