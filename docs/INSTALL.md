# Installation Guide (Ubuntu) | 설치 가이드 (우분투)

This guide is for first-time setup on Ubuntu for the GDS EMR multi-module project (`app`, `core`, `server`).

## 1) System Packages | 필수 패키지
```bash
sudo apt update
sudo apt install -y git curl unzip ca-certificates fontconfig
```

## 2) Install Java 25 (JDK) | Java 25 설치
Project toolchain is set to Java 25 (`javaVersion=25`).

```bash
sudo apt install -y openjdk-25-jdk
java -version
javac -version
```

Expected major version: `25`.

## 3) Set `JAVA_HOME` | 환경변수 설정
Check JDK path:
```bash
readlink -f "$(which java)"
```

Typical Ubuntu path:
- `/usr/lib/jvm/java-25-openjdk-amd64`

Set for current shell:
```bash
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
```

Persist in `~/.bashrc`:
```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```

Verify:
```bash
echo "$JAVA_HOME"
java -version
```

## 4) Gradle Wrapper Check | Gradle Wrapper 확인
This project uses Gradle Wrapper (`./gradlew`), so no separate Gradle install is required.

```bash
cd /home/ittia/git/GDSEMR_ver_1.1003
chmod +x gradlew
./gradlew -v
```

Wrapper target version is Gradle `9.3.0` (from `gradle/wrapper/gradle-wrapper.properties`).

## 5) JavaFX SDK Path Notes | JavaFX SDK 경로 참고
For normal Gradle runs, JavaFX modules are downloaded automatically via the OpenJFX plugin.  
You usually do **not** need a manual JavaFX SDK path.

Use a JavaFX SDK path only when:
- running Java directly (not via Gradle), or
- configuring some IDE launchers manually.

Example manual run arguments (if needed):
```bash
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml,javafx.swing
```

## 6) First Run Verification | 첫 실행 검증
### A. Build and tests
```bash
./gradlew clean test
```

### B. Run desktop app
```bash
./gradlew run
```
Expected: JavaFX app window launches (`com.emr.gds.IttiaApp`).

### C. Run API server and health check
In terminal 1:
```bash
./gradlew runServer
```
In terminal 2:
```bash
curl http://localhost:8080/api/v1/health
```
Expected JSON contains `"status":"ok"`.

## Common Errors and Fixes | 자주 발생하는 오류와 해결

### 1) Java version mismatch
Symptoms:
- `Unsupported class file major version`
- toolchain cannot find Java 25

Fix:
```bash
java -version
echo "$JAVA_HOME"
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
./gradlew --stop
./gradlew clean build
```

If still failing, confirm `gradle.properties` Java paths exist on your machine.

### 2) Gradle wrapper issues
Symptoms:
- `./gradlew: Permission denied`
- `Could not find or load main class org.gradle.wrapper.GradleWrapperMain`
- wrapper download/network errors

Fix:
```bash
chmod +x gradlew
ls -l gradle/wrapper/gradle-wrapper.jar
./gradlew --version
```

If `gradle-wrapper.jar` is missing/corrupted, restore it from repository and retry.  
If network is restricted, allow access to `services.gradle.org` and `repo.maven.apache.org`.

### 3) Missing JavaFX modules
Symptoms:
- `Module javafx.controls not found`
- JavaFX runtime component errors

Fix:
```bash
./gradlew --refresh-dependencies
./gradlew :app:dependencies
./gradlew run
```

If running outside Gradle (IDE/manual), set JavaFX SDK module path and required modules explicitly.

## Optional: Useful Commands | 참고 명령어
```bash
./gradlew tasks
./gradlew :app:run
./gradlew :server:bootRun
./gradlew :core:test
```
