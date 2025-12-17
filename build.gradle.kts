// 최상위 (Top-level) build.gradle.kts 파일 전체 내용

plugins {
    // 안드로이드 앱 빌드 플러그인
    alias(libs.plugins.android.application) apply false

    // 🔥 Firebase 플러그인 정의 (이전에 지워졌던 코드 복구)
    id("com.google.gms.google-services") version "4.4.0" apply false
}