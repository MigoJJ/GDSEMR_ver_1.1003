# GDS EMR Prototype (GDSEMR_ver_1.1003)

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-25-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.x-brightgreen.svg)
![SQLite](https://img.shields.io/badge/SQLite-Local_DB-lightgrey.svg)

간단한 진료 문서화와 임상 의사결정을 돕기 위한 Java 기반 EMR 프로토타입입니다.  
*A Java-based EMR prototype designed to support outpatient documentation and quick clinical workflows.*

---

## 📑 목차 | Table of Contents
1. [프로젝트 개요 | Project Overview](#-프로젝트-개요--project-overview)
2. [주요 기능 | Features](#-주요-기능--features)
3. [기술 스택 | Tech Stack](#-기술-스택--tech-stack)
4. [사전 준비 | Prerequisites](#-사전-준비--prerequisites)
5. [빠른 시작 | Quick Start](#-빠른-시작--quick-start)
6. [빌드/실행 명령어 | Build & Run Commands](#-빌드실행-명령어--build--run-commands)
7. [폴더 구조 | Folder Structure](#-폴더-구조--folder-structure)
8. [아키텍처 | Architecture](#-아키텍처--architecture)
9. [문제 해결 | Troubleshooting](#-문제-해결--troubleshooting)
10. [참고 | Notes](#-참고--notes)

---

## 📌 프로젝트 개요 | Project Overview
- **멀티모듈 프로젝트**: JavaFX 데스크톱 앱(`app`)과 Spring Boot API(`server`)를 포함합니다.
- **공통 로직 재사용**: Shared domain/util 모듈(`core`)을 통해 중복 코드를 방지합니다.
- **빠른 개발/테스트**: 로컬 SQLite 데이터 저장소를 중심으로 구축되어 설정이 간편합니다.

## ✨ 주요 기능 | Features
- **임상 문서화 UI**: SOAP 및 병력 입력 중심의 직관적인 사용자 인터페이스.
- **임상 도구 화면**: 약물, 검사, 알레르기, 백신, EKG 등 다양한 임상 도구 제공.
- **데이터 관리**: 템플릿, 자주 쓰는 문구 및 참조 데이터 관리 기능.
- **API 확장성**: 환자 및 템플릿 API 확장을 위한 서버 스켈레톤 포함.

## 🛠 기술 스택 | Tech Stack
- **Language**: Java 25 (toolchain)
- **UI**: JavaFX 25 (`javafx.controls`, `javafx.fxml`, `javafx.swing`)
- **Backend**: Spring Boot 3.3.x (`server`)
- **DB**: SQLite (`sqlite-jdbc`)
- **Build**: Gradle Wrapper (`./gradlew`)
- **Test**: JUnit 5

## ⚙️ 사전 준비 | Prerequisites
- JDK 25 설치가 필수입니다.
- Gradle은 별도로 설치할 필요가 없습니다. (Wrapper 사용)
- JavaFX는 Gradle 의존성에 의해 자동으로 다운로드됩니다.

**권장 확인 명령어:**
```bash
java -version
./gradlew -v
```

## 🚀 빠른 시작 | Quick Start

저장소를 클론하고 프로젝트 폴더로 이동합니다:
```bash
git clone <repo-url>
cd GDSEMR_ver_1.1003
./gradlew clean
```

**앱 실행 (App):**
```bash
./gradlew run
# 또는
./gradlew :app:run
```

**API 서버 실행 (기본 포트 8080):**
```bash
./gradlew runServer
# 또는
./gradlew :server:bootRun
```

**테스트:**
```bash
./gradlew test
```

## ⌨️ 빌드/실행 명령어 | Build & Run Commands
- **전체 빌드**: `./gradlew build`
- **앱 실행**: `./gradlew run` (또는 `:app:run`)
- **서버 실행**: `./gradlew runServer` (또는 `:server:bootRun`)
- **모듈별 테스트**:
  - 앱 모듈: `./gradlew :app:test`
  - 코어 모듈: `./gradlew :core:test`
  - 서버 모듈: `./gradlew :server:test`

## 📁 폴더 구조 | Folder Structure
```text
.
├── app/        # JavaFX desktop EMR client
├── core/       # Shared domain/util library
├── server/     # Spring Boot REST API
├── docs/       # Project docs/notes
├── gradle/     # Wrapper and version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🏗 아키텍처 | Architecture
- **모듈/레이어 구조 문서**: [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)
- **포함 내용**: 각 subproject의 역할(`app`, `server`, `core`), 코드 위치(UI/Controller/Service/Repository), 간단한 다이어그램.

## 🚑 문제 해결 | Troubleshooting
- **`JAVA_HOME` / toolchain 오류**:
  - JDK 25가 설치되어 있는지 확인하고 `java -version`으로 검증하세요.
- **JavaFX 관련 실행 오류**:
  - 네트워크가 Maven Central에 접근 가능한지 확인 후 `./gradlew --refresh-dependencies`를 실행하세요.
- **포트 충돌 (서버 8080)**:
  - 다른 프로세스 종료 후 재실행하거나 `server/src/main/resources/application.yml`에서 서버 포트 설정을 변경하세요.
- **Gradle 캐시/빌드 이상**:
  - 문제가 지속될 경우, 캐시를 지우고 새로 빌드하세요: `./gradlew clean build --no-daemon`

## 📝 참고 | Notes
- 버전 및 툴체인 설정은 `gradle.properties`에서 관리됩니다.
- 루트 태스크 `run`, `runServer`는 각각 `app`, `server` 모듈의 실행을 위임받아 처리합니다.