# GDS EMR Prototype (GDSEMR_ver_1.1003)

![Java](https://img.shields.io/badge/Java-25-orange.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-25-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-brightgreen.svg)
![Gradle](https://img.shields.io/badge/Gradle-Wrapper-02303A.svg)
![SQLite](https://img.shields.io/badge/SQLite-3.45.3.0-lightgrey.svg)

진료 문서화와 임상 의사결정을 지원하는 Java 기반 EMR 프로토타입입니다.

## 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [기술 스택](#기술-스택)
3. [버전 정보](#버전-정보)
4. [프로젝트 구조](#프로젝트-구조)
5. [빠른 시작](#빠른-시작)
6. [주요 명령어](#주요-명령어)
7. [문서](#문서)
8. [문제 해결](#문제-해결)

## 프로젝트 개요
- 멀티모듈 Gradle 프로젝트 (`app`, `core`, `server`)
- JavaFX 기반 데스크톱 클라이언트와 Spring Boot API 서버 포함
- 로컬 SQLite DB 중심의 경량 개발/테스트 환경

## 기술 스택
- Language: Java 25 (toolchain)
- UI: JavaFX 25.0.1
- Backend: Spring Boot 3.3.5
- Database: SQLite (`sqlite-jdbc` 3.45.3.0)
- Build: Gradle Wrapper
- Test: JUnit 5

## 버전 정보
- Repository Directory: `GDSEMR_ver_1.1003`
- Gradle Root Project Name: `GDSEMR_ver_1.1001` (현재 `settings.gradle.kts` 기준)
- Toolchain/Dependency Version Source: `gradle.properties`

## 프로젝트 구조
```text
.
├── app/                 # JavaFX desktop EMR client
│   ├── db/              # SQLite DB 및 reference 자료
│   └── src/main/        # Java/FXML/CSS/resources
├── core/                # Shared domain/util library
├── server/              # Spring Boot REST API
├── docs/                # 프로젝트 문서
├── templates/           # 템플릿 리소스
├── gradle/              # Gradle wrapper / version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 빠른 시작
```bash
git clone <repo-url>
cd GDSEMR_ver_1.1003
./gradlew clean
./gradlew run
```

서버 실행:
```bash
./gradlew runServer
```

## 주요 명령어
- 전체 빌드: `./gradlew build`
- 전체 테스트: `./gradlew test`
- 앱 실행: `./gradlew run` 또는 `./gradlew :app:run`
- 서버 실행: `./gradlew runServer` 또는 `./gradlew :server:bootRun`
- 모듈 테스트: `./gradlew :app:test :core:test :server:test`

## 문서
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- [docs/INSTALL.md](docs/INSTALL.md)
- [docs/BUILD_AND_RELEASE.md](docs/BUILD_AND_RELEASE.md)
- [docs/DATABASES.md](docs/DATABASES.md)

## 문제 해결
- Java/toolchain 확인: `java -version`, `./gradlew -v`
- 의존성 재동기화: `./gradlew --refresh-dependencies`
- 빌드 초기화: `./gradlew clean build --no-daemon`
