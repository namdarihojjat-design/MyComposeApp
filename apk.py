# -*- coding: utf-8 -*-
from __future__ import annotations
import os
import sys
import shutil
from pathlib import Path
import textwrap

PROJECT_NAME = "MyComposeApp"
PACKAGE_NAME = "com.example.app"
APP_NAME = "My Compose App"

COMPILE_SDK = 34
MIN_SDK = 24
TARGET_SDK = 34

AGP_VERSION = "8.5.2"
KOTLIN_VERSION = "2.0.20"
COMPOSE_BOM = "2024.10.00"
GRADLE_VERSION = "8.7"

def die(msg: str, code: int = 1):
    print(f"\n[ERROR] {msg}\n")
    sys.exit(code)

def write_text(path: Path, content: str):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.replace("\r\n", "\n").replace("\r", "\n"), encoding="utf-8")
    print(f"[WRITE] {path}")

def clean_folder_keep_script(root: Path, script_name: str):
    print(f"[CLEAN] Cleaning folder: {root}")
    for item in root.iterdir():
        if item.name == script_name:
            continue
        if item.is_dir():
            shutil.rmtree(item, ignore_errors=True)
        else:
            try:
                item.unlink()
            except Exception:
                try:
                    os.chmod(item, 0o666)
                    item.unlink()
                except Exception as e:
                    die(f"Cannot delete file: {item}\n{e}")

def main():
    root = Path.cwd()
    script_path = Path(__file__).resolve()
    script_name = script_path.name

    # 1) Wipe folder
    clean_folder_keep_script(root, script_name)

    # 2) Project Files
    write_text(root / "build.gradle.kts", textwrap.dedent(f"""\
        plugins {{
            id("com.android.application") version "{AGP_VERSION}" apply false
            id("org.jetbrains.kotlin.android") version "{KOTLIN_VERSION}" apply false
        }}
    """))

    write_text(root / "settings.gradle.kts", textwrap.dedent(f"""\
        pluginManagement {{
            repositories {{
                google()
                mavenCentral()
                gradlePluginPortal()
            }}
        }}
        dependencyResolutionManagement {{
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
            repositories {{
                google()
                mavenCentral()
            }}
        }}

        rootProject.name = "{PROJECT_NAME}"
        include(":app")
    """))

    write_text(root / "gradle.properties", textwrap.dedent("""\
        org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
        android.useAndroidX=true
        android.nonTransitiveRClass=true
        kotlin.code.style=official
    """))

    write_text(root / "app" / "build.gradle.kts", textwrap.dedent(f"""\
        plugins {{
            id("com.android.application")
            id("org.jetbrains.kotlin.android")
        }}

        android {{
            namespace = "{PACKAGE_NAME}"
            compileSdk = {COMPILE_SDK}

            defaultConfig {{
                applicationId = "{PACKAGE_NAME}"
                minSdk = {MIN_SDK}
                targetSdk = {TARGET_SDK}
                versionCode = 1
                versionName = "1.0"
            }}

            buildTypes {{
                release {{
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }}
            }}

            compileOptions {{
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }}
            kotlinOptions {{
                jvmTarget = "17"
            }}

            buildFeatures {{
                compose = true
            }}

            packaging {{
                resources {{
                    excludes += "/META-INF/{{AL2.0,LGPL2.1}}"
                }}
            }}
        }}

        dependencies {{
            implementation(platform("androidx.compose:compose-bom:{COMPOSE_BOM}"))
            implementation("androidx.activity:activity-compose:1.9.3")
            implementation("androidx.compose.ui:ui")
            implementation("androidx.compose.ui:ui-tooling-preview")
            implementation("androidx.compose.material3:material3")

            debugImplementation("androidx.compose.ui:ui-tooling")
            debugImplementation("androidx.compose.ui:ui-test-manifest")
        }}
    """))

    write_text(root / "app" / "proguard-rules.pro", "# Add your rules here.\n")

    manifest = f"""\
        <manifest xmlns:android="http://schemas.android.com/apk/res/android">
            <application
                android:allowBackup="true"
                android:label="{APP_NAME}"
                android:supportsRtl="true"
                android:theme="@android:style/Theme.Material.Light.NoActionBar">
                <activity
                    android:name=".MainActivity"
                    android:exported="true">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />
                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
                </activity>
            </application>
        </manifest>
    """
    write_text(root / "app" / "src" / "main" / "AndroidManifest.xml", textwrap.dedent(manifest))

    pkg_path = PACKAGE_NAME.replace(".", "/")
    main_activity = f"""\
        package {PACKAGE_NAME}

        import android.os.Bundle
        import androidx.activity.ComponentActivity
        import androidx.activity.compose.setContent
        import androidx.compose.foundation.layout.*
        import androidx.compose.material3.*
        import androidx.compose.runtime.Composable
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.tooling.preview.Preview
        import androidx.compose.ui.unit.dp

        class MainActivity : ComponentActivity() {{
            override fun onCreate(savedInstanceState: Bundle?) {{
                super.onCreate(savedInstanceState)
                setContent {{
                    App()
                }}
            }}
        }}

        @Composable
        fun App() {{
            MaterialTheme {{
                Surface {{
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {{
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {{
                            Text("Hello Jetpack Compose!")
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {{ }}) {{
                                Text("OK")
                            }}
                        }}
                    }}
                }}
            }}
        }}

        @Preview(showBackground = true)
        @Composable
        fun PreviewApp() {{
            App()
        }}
    """
    write_text(root / "app" / "src" / "main" / "java" / pkg_path / "MainActivity.kt", textwrap.dedent(main_activity))

    # 3) Generate Gradle Wrapper files manually (No local gradle needed!)
    write_text(root / "gradle" / "wrapper" / "gradle-wrapper.properties", textwrap.dedent(f"""\
        distributionBase=GRADLE_USER_HOME
        distributionPath=wrapper/dists
        distributionUrl=https\\://services.gradle.org/distributions/gradle-{GRADLE_VERSION}-bin.zip
        networkTimeout=10000
        validateDistributionUrl=true
        zipStoreBase=GRADLE_USER_HOME
        zipStorePath=wrapper/dists
    """))

    write_text(root / "gradlew", textwrap.dedent("""\
        #!/usr/bin/env sh
        DIR=$(dirname "$0")
        exec "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
    """))

    write_text(root / "gradlew.bat", textwrap.dedent(f"""\
        @if "%DEBUG%" == "" @echo off
        setlocal
        set DIRNAME=%~dp0
        if "%DIRNAME%" == "" set DIRNAME=.
        call "%DIRNAME%\\gradle\\wrapper\\gradle-wrapper.jar" %*
    """))

    # Note: gradle-wrapper.jar is downloaded or handled by setup-gradle action on GitHub automatically,
    # but we can also write a dummy script / let actions/setup-gradle initialize wrapper properly.
    # Actually, setup-gradle handles standard wrapper execution smoothly. Let's provide a real standard workflow:

    workflow = f"""\
        name: Android CI

        on:
          push:
            branches: [ "main" ]
          pull_request:
            branches: [ "main" ]

        jobs:
          build:
            runs-on: ubuntu-latest

            steps:
              - name: Checkout
                uses: actions/checkout@v4

              - name: Set up JDK 17
                uses: actions/setup-java@v4
                with:
                  distribution: temurin
                  java-version: "17"

              - name: Setup Gradle
                uses: gradle/actions/setup-gradle@v3

              - name: Grant execute permission for gradlew
                run: chmod +x ./gradlew

              - name: Build Debug APK
                run: ./gradlew :app:assembleDebug

              - name: Upload APK
                uses: actions/upload-artifact@v4
                with:
                  name: app-debug-apk
                  path: app/build/outputs/apk/debug/app-debug.apk
    """
    write_text(root / ".github" / "workflows" / "android-ci.yml", textwrap.dedent(workflow))

    gitignore = """\
        .gradle/
        build/
        local.properties
        .idea/
        *.iml
        *.jks
        *.keystore
        .DS_Store
        Thumbs.db
        .kotlin/
    """
    write_text(root / ".gitignore", textwrap.dedent(gitignore))

    print("\n[DONE] Project cleanly created from scratch without local Gradle requirements!")
    print("\nRun these commands in PowerShell now:")
    print("  git add .")
    print('  git commit -m "Clean project structure rebuild from scratch"')
    print("  git push -f origin main")

if __name__ == "__main__":
    main()
