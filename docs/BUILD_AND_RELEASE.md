# Build and Release Guide

This document explains how to build and run the project from CLI and IDE, and where distributable artifacts are generated.

## 1) Multi-module structure
This repository is a Gradle multi-module build:
- `app`: JavaFX desktop client
- `server`: Spring Boot REST API
- `core`: shared library used by `app`

Defined in `settings.gradle.kts`:
- `include("app", "server", "core")`

## 2) Gradle tasks used in this repo

### Root convenience tasks
- `./gradlew run`
  - Delegates to `:app:run`
- `./gradlew runServer`
  - Delegates to `:server:bootRun`

### Common build/test tasks
- `./gradlew clean`
- `./gradlew build`
- `./gradlew test`
- `./gradlew :app:test :core:test :server:test`

### Module-specific tasks
- `./gradlew :app:run`
- `./gradlew :server:bootRun`
- `./gradlew :core:jar`
- `./gradlew :app:distZip`
- `./gradlew :server:bootJar`

### Discover all tasks
- `./gradlew tasks --all`

Example expected output (trimmed):
```text
Tasks runnable from root project 'GDSEMR_ver_1.1001'
...
Application tasks
app:run
server:bootRun
...
Distribution tasks
app:distZip
...
Build tasks
server:bootJar
core:jar
...
BUILD SUCCESSFUL
```

## 3) Build a distributable

### Recommended release build
```bash
./gradlew clean :core:jar :app:distZip :server:bootJar
```

### What this produces
- `core` reusable jar
- `app` zipped distribution (scripts + app jar)
- `server` executable Spring Boot jar

Expected output (trimmed):
```text
> Task :app:distZip
> Task :server:bootJar
BUILD SUCCESSFUL
```

## 4) Output artifact locations
After build, artifacts are in:
- `app/build/distributions/app.zip`
- `app/build/libs/app.jar`
- `core/build/libs/core.jar`
- `server/build/libs/server.jar`

Quick check:
```bash
ls -lh app/build/distributions app/build/libs core/build/libs server/build/libs
```

## 5) Run from CLI

### Desktop app
```bash
./gradlew run
# or
./gradlew :app:run
```
Expected result: JavaFX window opens (`com.emr.gds.IttiaApp`).

### API server
```bash
./gradlew runServer
# or
./gradlew :server:bootRun
```
Expected result: server starts on port `8080`.

Health check:
```bash
curl http://localhost:8080/api/v1/health
```
Expected JSON includes `"status":"ok"`.

## 6) Run from IDE (IntelliJ IDEA)

### Import
1. Open project root.
2. Import as Gradle project.
3. Use Gradle JVM/JDK 25.

### Option A: Run via Gradle tool window
- App: `app > Tasks > application > run`
- Server: `server > Tasks > application > bootRun`

### Option B: Run as Java/Spring configuration
- App run config:
  - Main class: `com.emr.gds.IttiaApp`
  - Module classpath: `app`
- Server run config:
  - Main class: `com.emr.gds.server.GdsEmrServerApplication`
  - Module classpath: `server`

Note: CLI and IDE should use the same JDK major version (`25`) to avoid class/version mismatches.

## 7) Minimal release checklist
1. `./gradlew clean test`
2. `./gradlew :core:jar :app:distZip :server:bootJar`
3. Verify artifacts in each `*/build/libs` and `app/build/distributions`
4. Smoke test:
   - run app (`./gradlew :app:run`)
   - run server + health check (`./gradlew :server:bootRun` + `curl`)
