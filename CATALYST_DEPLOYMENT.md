# Deploying PTube on Zoho Catalyst AppSail

PTube currently uses the Invidious server codebase, which is written in Crystal and requires PostgreSQL.

Catalyst AppSail supports OCI/Docker custom runtimes, so the existing PTube Docker image can be deployed without rewriting the server into Node.js or Python.

## Architecture

```text
Android APK
    |
    | HTTPS
    v
Zoho Catalyst AppSail
    |
    | PTube / Invidious container
    v
PostgreSQL
```

## AppSail service

Use the repository's existing `docker/Dockerfile` to build the PTube image.

Configure the AppSail service to listen on port:

```text
3000
```

The application must be reachable publicly over HTTPS before entering its URL in the Android app.

## Required server configuration

Do not commit passwords or keys to GitHub.

Configure `INVIDIOUS_CONFIG` as an AppSail environment variable. A starting template is:

```yaml
db:
  dbname: ptube
  user: YOUR_DB_USER
  password: YOUR_DB_PASSWORD
  host: YOUR_DB_HOST
  port: 5432

check_tables: true
https_only: true
hmac_key: "GENERATE_A_LONG_RANDOM_SECRET"
```

The PostgreSQL database must be initialized with the schema expected by the PTube/Invidious version in this repository.

## Catalyst deployment paths

### Option A: local Docker image through Catalyst CLI

After building the image locally:

```bash
docker build -f docker/Dockerfile -t ptube:latest .
```

Deploy the image from the Catalyst project directory using AppSail custom-runtime deployment and configure port 3000.

### Option B: container registry

Push the image to a supported registry and deploy it from the Catalyst AppSail console.

Keep database credentials and `hmac_key` in AppSail environment variables rather than in the image.

## Android connection

When the Android APK opens for the first time, enter the final HTTPS AppSail endpoint, for example:

```text
https://your-ptube-service.example
```

The app stores the server URL locally, so the server endpoint does not need to be hard-coded into the APK.
