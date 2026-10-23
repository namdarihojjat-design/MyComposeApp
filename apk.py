import pathlib

def update_ci_workflow():
    workflow_path = pathlib.Path(".github/workflows/android-ci.yml")
    workflow_path.parent.mkdir(parents=True, exist_ok=True)

    content = """name: Android CI

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

      - name: Install Gradle
        run: sudo apt-get update && sudo apt-get install -y gradle

      - name: Build Debug APK
        run: gradle :app:assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
"""

    # نوشتن با انتهای خط LF لینوکسی
    workflow_path.write_text(content.replace('\r\n', '\n'), encoding='utf-8')
    print(f"[SUCCESS] Workflow updated successfully at: {workflow_path}")
    
    # بررسی نهایی محتوا
    print("[INFO] Verifying file content and line endings...")
    raw_bytes = workflow_path.read_bytes()
    if b'\r\n' in raw_bytes:
        print("[WARNING] Found CRLF, forcing LF conversion.")
        workflow_path.write_bytes(raw_bytes.replace(b'\r\n', b'\n'))
    else:
        print("[OK] File format is strictly Linux LF.")

if __name__ == "__main__":
    update_ci_workflow()
