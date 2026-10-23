import os
import pathlib
import base64

def fix_everything():
    base_dir = pathlib.Path.cwd()
    print("[INFO] Setting up foolproof project structure...")

    # ۱. ساختار پوشه‌ها
    (base_dir / "gradle" / "wrapper").mkdir(parents=True, exist_ok=True)
    (base_dir / ".github" / "workflows").mkdir(parents=True, exist_ok=True)

    # ۲. قرار دادن فایل حیاتی gradle-wrapper.jar به صورت بایت‌های استاندارد (برای جلوگیری از خطای 127)
    # این فایل باینری استاندارد گرادل است
    jar_path = base_dir / "gradle" / "wrapper" / "gradle-wrapper.jar"
    if not jar_path.exists():
        # محتوای باینری استاندارد یک فایل gradle-wrapper.jar سبک
        # برای اطمینان از صحت، از روش دانلود خودکار گرادل در CI استفاده می‌کنیم تا نیاز به jar هم نباشد!
        pass

    # ۳. اصلاح نهایی CI Workflow برای استفاده از Gradle مستقیم بدون نیاز به wrapper پیچیده
    workflow_path = base_dir / ".github" / "workflows" / "android-ci.yml"
    ci_content = """name: Android CI

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
        uses: actions/setup-java@v5
        with:
          distribution: temurin
          java-version: "17"

      - name: Download Gradle Distribution Manually
        run: |
          wget https://services.gradle.org/distributions/gradle-8.7-bin.zip -O gradle.zip
          unzip -q gradle.zip
          echo "$PWD/gradle-8.7/bin" >> $GITHUB_PATH

      - name: Build Debug APK
        run: gradle :app:assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
"""
    workflow_path.write_text(ci_content.replace('\r\n', '\n'), encoding='utf-8')
    print("[SUCCESS] Workflow updated to download and use Gradle directly!")

if __name__ == "__main__":
    fix_everything()
