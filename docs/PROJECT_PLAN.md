# SecureScope Project Plan

## Goal

Build SecureScope as a professional full-stack portfolio project that demonstrates secure backend development, modern Angular UI design, structured security scanning workflows, and clear reporting.

## Core Product Flow

1. A user signs in.
2. The user starts a scan from pasted code, a ZIP upload, or a GitHub repository.
3. The backend analyzes the submitted source for secrets, dependency risks, risky code patterns, and missing best practices.
4. Findings are mapped to severity levels and OWASP Top 10 categories where applicable.
5. The system calculates a security score.
6. The dashboard presents masked findings, recommendations, and exportable reports.

## Feature Build Order

1. Workspace documentation and project structure
2. Backend dependencies and base configuration
3. Authentication and JWT security
4. Quick Code Scan backend API
5. Secret detection service
6. Finding model, DTOs, and security score
7. Angular workspace setup
8. Quick Code Scan frontend
9. Scan history and report details
10. ZIP upload scanner
11. Dependency vulnerability checks
12. GitHub repository scanning
13. Report export
14. Dashboard analytics and charts
15. Final validation and deployment readiness

## Engineering Rules

- Build feature by feature.
- Keep services and DTOs small and maintainable.
- Update README.md after each feature.
- Add or update tests where useful.
- Run build validation before committing.
- Run git status before reporting completion.
- Use clear feature-based commit messages.
- Do not expose full secrets in UI, logs, API responses, or reports.

## Documentation Plan

- README.md: project overview, setup, features, roadmap, and usage notes
- docs/PROJECT_PLAN.md: build sequence and architectural notes
- docs/screenshots: screenshots for completed UI milestones

## Security Notes

SecureScope findings should be treated as developer assistance, not as a guarantee of security. The application must avoid storing or displaying raw secrets unless a future secure storage policy explicitly requires it.
