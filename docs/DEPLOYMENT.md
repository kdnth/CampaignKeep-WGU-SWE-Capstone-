# Task 4: Deployment Justification

## Cloud Hosting Provider Justification

Campaign Keep is deployed across three services: **Railway** for the containerized Spring Boot backend, **Neon** for managed PostgreSQL, and **Netlify**. This was a deliberate architectural decision. I evaluated several alternatives before settling on this combination.

**Why compute and database are hosted separately.** Coupling application compute and database storage on a single platform creates a dependency between two distinct scaling concerns: an application server benefits from restart flexibility and horizontal scaling, while a database needs durable storage, backups, and point-in-time recovery. 
By hosting the database independently on Neon, the application layer can be redeployed, rebuilt, or migrated to a different compute provider without any risk to persisted data. Neon's free tier also includes automated backups and point-in-time recovery, which most bundled platforms don't offer, since those free database instances are typically ephemeral or time-limited. For example, Render's free Postgres, which is deleted 30 days after creation.

**Why Railway for compute, evaluated against alternatives.** I considered several categories of hosting before choosing Railway:

- *Render* was a strong free-tier candidate, but its free web services spin down after 15 minutes of inactivity. This would introduce a 30-60 second cold start and create a poor experience for anyone reviewing the live deployment.
- *Fly.io* offered competitive pricing and has a more Docker-native deployment model, but its per-second metered billing is less predictable than a flat subscription, and given a realistic memory requirement for this application (~512MB–1GB), Fly's always-on pricing (~$6-7/month) exceeded Railway's flat Hobby-tier cost.
- *Google Cloud Run, AWS App Runner, and Azure App Service* would have provided stronger resume/industry-recognition value, but each requires meaningfully more setup overhead (IAM configuration, container registries, service accounts, VPC basics) than I could justify for a solo capstone project on a fixed timeline. Google Cloud Run in particular is architecturally well-suited to this kind of low-traffic application, but pairing it with Cloud SQL would have introduced more cost than I was willing to spend.

Railway was ultimately chosen because of its flat, predictable $5/month cost, full Docker support, and Railway CLI. Railway CLI was used for deployment as this project started as my WGU Capstone project (GitLab required), so I was unable to use the push-to-deploy feature due to lack of GitLab support.

**Why the frontend is hosted separately from the backend.** The Vue 3 frontend is a static single-page application after build, so it doesn’t require the same container-based compute Railway provides from the backend. It is hosted on Netlify, which serves static assets directly from a CDN with no cold-start behavior, and it supports GitLab-based continuous deployment, so frontend changes deploy automatically on push. Separating the frontend from the backend host also decouples static asset failure and application server failure incidents.

---

## Container Usage Justification

The backend is packaged as a Docker container using a multi-stage build. Spring Boot applications require a specific Java runtime version, a compiled JAR built via Maven, and consistent behavior regardless of the underlying OS/architecture.

**Implementation — multi-stage build.** The Dockerfile uses two distinct stages. The first stage uses a full Maven + JDK image to compile the application and package it into an executable JAR. The second stage starts from a minimal image containing only a Java Runtime Environment and copies in just the compiled JAR from the first stage. 

This is to keep the final production image smaller than a single-stage build would, as none of the build-time tooling is included in the deployed application.

**Configuration is externalized, not baked into the image.** The container itself contains no environment-specific configuration. Connection strings, JWT secrets, and CORS allowed origins are all supplied at runtime via environment variables (`SPRING_DATASOURCE_URL`, `JWT_SECRET`, `CORS_ALLOWED_ORIGIN`, etc.), rather than hardcoded into `application.properties` at build time. This means allows the identical image can be deployed to any environment for local Docker testing, Railway staging, or a future production environment with only the environment variables changing.