import os

project_dir = r"C:\Users\p\Desktop\apk"

def check_file(path, name):
    if os.path.exists(path):
        print(f"[✓] {name} پیدا شد: {path}")
        return True
    else:
        print(f"[❌] {name} یافت نشد! مسیر مورد انتظار: {path}")
        return False

print("=== شروع بررسی سلامت پروژه TWA ===")
print(f"مسیر پروژه: {project_dir}\n")

# ۱. بررسی فایل‌های اصلی
manifest_path = os.path.join(project_dir, "app", "src", "main", "AndroidManifest.xml")
main_activity_path = os.path.join(project_dir, "app", "src", "main", "java", "com", "example", "app", "MainActivity.kt")
build_gradle_path = os.path.join(project_dir, "app", "build.gradle.kts")

check_file(manifest_path, "AndroidManifest.xml")
check_file(main_activity_path, "MainActivity.kt")
check_file(build_gradle_path, "App build.gradle.kts")

# ۲. بررسی محتوای MainActivity.kt
if os.path.exists(main_activity_path):
    with open(main_activity_path, "r", encoding="utf-8") as f:
        content = f.read()
        print("\n--- بررسی کد MainActivity.kt ---")
        if "TwaLauncher" in content:
            print("[✓] کلاس TwaLauncher به درستی استفاده شده است.")
        else:
            print("[❌] TwaLauncher در کد دیده نشد.")

# ۳. بررسی مانیفست
if os.path.exists(manifest_path):
    with open(manifest_path, "r", encoding="utf-8") as f:
        m_content = f.read()
        print("\n--- بررسی AndroidManifest.xml ---")
        if 'android.permission.INTERNET' in m_content:
            print("[✓] دسترسی INTERNET وجود دارد.")
        else:
            print("[❌] دسترسی INTERNET در Manifest گم شده است!")

print("\n=== پایان بررسی ===")
