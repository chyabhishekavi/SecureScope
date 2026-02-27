# SecureScope Future Backlog

These are realistic improvements I would consider next. Some are product features, and some are engineering hardening tasks.

## Scanner Improvements

- Integrate a real vulnerability database such as OSV or NVD.
- Add SARIF export so results can be consumed by code scanning tools.
- Add more advanced SAST rules.
- Add language-aware parsing for Java, JavaScript, and TypeScript.
- Improve false positive management with notes and rule suppression.
- Add rule IDs and rule documentation.
- Add severity overrides for ignored or accepted risk.

## GitHub And CI Improvements

- Add GitHub OAuth.
- Support private repository scanning.
- Add CI/CD pipeline scan triggers.
- Add webhook-based scan triggers for repository updates.
- Store repository metadata such as default branch and last scanned commit.

## Reporting Improvements

- Add PDF report export.
- Add report templates.
- Add executive summary and developer summary sections.
- Add SARIF and JSON export.
- Add report comparison between scans.

## Product Improvements

- Add organization or team workspaces.
- Add roles and permissions.
- Add project tags.
- Add scan scheduling.
- Add audit history for finding status changes.
- Add richer dashboard trends.

## Backend Hardening

- Move local JWT secret into environment variables.
- Add Flyway or Liquibase migrations.
- Add production database configuration.
- Add rate limiting on authentication and scan APIs.
- Add stronger validation around uploaded content.
- Add better error codes for frontend handling.
- Add structured logging.
- Add Docker deployment.

## Frontend Improvements

- Add route-level loading skeletons.
- Add richer empty states with actions.
- Add better mobile project workflows.
- Add pagination for large findings and scan lists.
- Add accessible chart summaries.
- Add end-to-end tests for the main demo flow.
