# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NutriSearch is a CMU Distributed Systems Project 4 — a full-stack nutrition lookup app consisting of:
- **Android app** (Java) — three-screen UI: search → results list → nutrition detail
- **Web service** (Java/Jakarta Servlet/Tomcat) — REST API + analytics dashboard
- **MongoDB Atlas** — logs every search request; powers the dashboard

## Build & Deploy

### Web Service

```bash
# Build WAR
cd web-service && ./mvnw clean package

# Copy WAR and run via Docker
cp web-service/target/ROOT.war ROOT.war
docker build -t nutrisearch .
docker run -d -p 8080:8080 nutrisearch
# Dashboard: http://localhost:8080/dashboard
# API:       http://localhost:8080/api/search?q=oat+milk
```

The `build-and-run.sh` script does the Docker steps only (Maven step is commented out).

### Android App

Build via Android Studio or:
```bash
cd android-app && ./gradlew build
```

### Tests

```bash
# Web service unit tests
cd web-service && ./mvnw test
```

## Architecture

### Web Service (`web-service/src/main/java/`)

All Java classes are in the **default package** (no package declaration). Two servlets, mapped in `WEB-INF/web.xml`:

- `FoodSearchServlet` → `GET /api/search?q=<term>` — validates query, delegates to `OpenFoodFactsClient`, logs to MongoDB via `MongoDBClient`, returns JSON
- `DashboardServlet` → `GET /dashboard` — reads analytics from MongoDB, forwards to `index.jsp`
- `OpenFoodFactsClient` — calls `world.openfoodfacts.org`, trims to six nutriment fields, retries up to 5× with linear back-off on HTTP 503
- `MongoDBClient` — lazy singleton wrapping the MongoDB sync driver; contains aggregation queries for top queries, avg duration, and error rate
- `LogEntry` — plain value object for one request log document

### Android App (`android-app/app/src/main/java/ds/edu/cmu/nutrisearch/`)

- `MainActivity` → search input, launches `ResultsActivity` with the query string
- `ResultsActivity` → fires an `ExecutorService` thread to call the web service, populates a `RecyclerView` via `FoodAdapter`; Glide handles image loading
- `DetailActivity` → receives a `FoodItem` via Intent extras, displays macros in a table
- `FoodItem` — data class holding all fields from the API response

## Important Hardcoded Values to Know

**`ResultsActivity.java:67-71`** — The web service hostname is hardcoded to a GitHub Codespaces URL (`scaling-waddle-q75vg74q9wwr3x4ww-8080.app.github.dev`). This **must be updated** whenever the Codespaces instance changes or when deploying elsewhere.

**`MongoDBClient.java:18`** — The MongoDB Atlas connection string (including credentials) is hardcoded. The database is `food_search`, collection is `logs`.

## Deployment Notes

The Dockerfile is a single-stage Tomcat 10.1 image — it just copies `ROOT.war` into `/usr/local/tomcat/webapps/`. Maven outputs the WAR as `ROOT` (configured via `<finalName>ROOT</finalName>` in `pom.xml`) so it deploys at the context root.

The project was designed to run on GitHub Codespaces; port 8080 is forwarded publicly and that URL is what the Android app targets.
