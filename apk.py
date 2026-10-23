import pathlib

def update_project():
    print("[INFO] Updating MainActivity.kt and AndroidManifest.xml...")

    # 1. MainActivity.kt (Jetpack Compose + WebView for hojjat.timzo.ir)
    main_code = """package com.example.app

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            loadUrl("https://hojjat.timzo.ir/")
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
"""
    main_path = pathlib.Path('app/src/main/java/com/example/app/MainActivity.kt')
    main_path.parent.mkdir(parents=True, exist_ok=True)
    main_path.write_text(main_code, encoding='utf-8')
    print("[OK] MainActivity.kt updated.")

    # 2. AndroidManifest.xml (Internet permission & icon settings)
    manifest_code = """<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Timzo Hojjat"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.NoTitleBar">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|keyboardHidden">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
"""
    manifest_path = pathlib.Path('app/src/main/AndroidManifest.xml')
    manifest_path.write_text(manifest_code, encoding='utf-8')
    print("[OK] AndroidManifest.xml updated.")
    print("[SUCCESS] All files are ready! Now run: git add . ; git commit -m \"Update app\" ; git push")

if __name__ == '__main__':
    update_project()
