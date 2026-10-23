import pathlib

# 1. تنظیمات ریشه پروژه (Root build.gradle.kts)
root_gradle = """plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}
"""
pathlib.Path('build.gradle.kts').write_text(root_gradle, encoding='utf-8')

# 2. تنظیمات ماژول برنامه (app/build.gradle.kts)
app_gradle = """plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.0")
}
"""

app_path = pathlib.Path('app/build.gradle.kts')
app_path.parent.mkdir(parents=True, exist_ok=True)
app_path.write_text(app_gradle, encoding='utf-8')

print('[SUCCESS] update_compose.py executed successfully: Gradle files updated for Kotlin 2.0 & Compose Compiler plugin.')
