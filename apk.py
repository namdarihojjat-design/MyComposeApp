import pathlib

def update_project():
    print("[INFO] Updating MainActivity.kt for persistent WebView session & internal browser rendering...")

    main_code = """package com.example.app

import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebSettings
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
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webView = this
                            
                            // Ensure it stays inside the app and doesn't launch external browser
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                    // Load all links inside this same WebView
                                    return false
                                }
                            }
                            
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                // Enable saving form data
                                setSaveFormData(true)
                            }
                            
                            // Force-enable cookies and storage persistence like Chrome
                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)
                            
                            loadUrl("https://hojjat.timzo.ir/")
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

    override fun onStop() {
        super.onStop()
        // Ensure cookies are saved immediately when app goes to background
        CookieManager.getInstance().flush()
    }
}
"""
    main_path = pathlib.Path('app/src/main/java/com/example/app/MainActivity.kt')
    main_path.parent.mkdir(parents=True, exist_ok=True)
    main_path.write_text(main_code, encoding='utf-8')
    print("[SUCCESS] MainActivity.kt updated successfully.")

if __name__ == '__main__':
    update_project()
