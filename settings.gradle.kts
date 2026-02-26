pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "GDSEMR_ver_1.1003"

// 서브 프로젝트 등록
include("app", "server", "core")
