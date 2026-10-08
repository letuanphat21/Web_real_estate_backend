# Web_real_estate_backend

## Run locally

1. Create local env file:
   ```bash
   cp .env_dev.example .env_dev
   ```
2. Update `.env_dev` values for your machine (especially PostgreSQL).
   - Default fallback if not set: `jdbc:postgresql://localhost:5432/postgres`, user `postgres`, password `postgres`.
   - If PostgreSQL is not running, app will fail with `Connection refused`.
3. Start app:
   ```bash
   ./mvnw spring-boot:run
   ```