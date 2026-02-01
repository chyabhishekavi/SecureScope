# SecureScope Scanner Rules

SecureScope uses basic static checks. The scanner reads source files or pasted code as text and applies a small set of rules. It does not build a full AST, run code, or do data-flow analysis.

## Secret Scanning Rules

The secret scanner checks line by line for assignment patterns.

Current rules:

- hardcoded password names: `password`, `passwd`, `pwd`
- hardcoded token names: `token`, `access_token`, `auth_token`
- API key names: `api_key`, `apikey`, `apiKey`, `x-api-key`
- JWT secret names: `jwt_secret`, `jwtSecret`, `jwt.secret`
- private key block headers such as `-----BEGIN PRIVATE KEY-----`

These findings are categorized as `HARDCODED_SECRET` and currently use `CRITICAL` severity.

## Risky Pattern Rules

Current risky pattern checks:

- MD5 usage
- SHA1 or SHA-1 usage
- disabled CSRF
- permissive CORS with wildcard origin
- SQL query strings built with concatenation

These findings are categorized as `RISKY_CODE_PATTERN`.

## Dependency Scanning Rules

Dependency scanning supports:

- Maven `pom.xml`
- npm `package.json`

The scanner extracts package names and versions, then checks them against a mock vulnerability database.

Example vulnerable packages in the mock data:

- old `log4j-core`
- old `spring-core`
- old `jackson-databind`
- old `lodash`
- old `axios`

These findings are categorized as `VULNERABLE_DEPENDENCY`.

## OWASP Mapping

The scanner maps rule groups to OWASP-style categories:

- secrets: `A02:2021 - Cryptographic Failures`
- weak cryptography: `A02:2021 - Cryptographic Failures`
- SQL concatenation: `A03:2021 - Injection`
- disabled CSRF and permissive CORS: `A05:2021 - Security Misconfiguration`
- vulnerable dependencies: `A06:2021 - Vulnerable and Outdated Components`

## Severity Assignment

Current severity choices are simple:

- hardcoded secrets: `CRITICAL`
- private key headers: `CRITICAL`
- weak hashes: `HIGH`
- SQL string concatenation: `HIGH`
- disabled CSRF: `MEDIUM`
- permissive CORS: `MEDIUM`
- dependency vulnerabilities: based on mock vulnerability data

## Scoring Formula

Each scan starts at `100`.

Penalties:

- `CRITICAL`: 30
- `HIGH`: 20
- `MEDIUM`: 10
- `LOW`: 5
- `INFO`: 1

Risk level:

- `90-100`: `SAFE`
- `75-89`: `LOW`
- `50-74`: `MODERATE`
- `25-49`: `HIGH`
- `0-24`: `CRITICAL`

## Masking Approach

Secrets are masked before findings are returned or saved. The scanner keeps enough context to help a developer identify the line, but it should not expose the full secret value.

Example:

```text
password = "super-secret-password"
```

becomes a masked evidence string that hides the sensitive middle portion.

## Current Limitations

- Regex rules can miss real issues.
- Regex rules can also create false positives.
- There is no AST parsing.
- There is no taint analysis or data-flow tracking.
- Dependency vulnerability data is mocked.
- File content is treated as UTF-8 text.
- The scanner does not inspect compiled artifacts or running applications.
- A clean scan does not prove that an application is secure.
