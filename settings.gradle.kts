// settings.gradle.kts 파일

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // ✅ 원래 있던 것들 (유지)
        google()
        mavenCentral()

        // 🔥 네이버 지도 SDK 저장소 (수정된 부분)
        maven {
            url = uri("https://naver.jfrog.io/artifactory/maven/")
            metadataSources {
                mavenPom()
                artifact()
            }
        }
    }
}

rootProject.name = "Beautyinside"
include(":app")
