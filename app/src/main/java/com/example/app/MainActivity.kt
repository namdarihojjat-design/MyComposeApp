package com.example.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    private var webViewRef: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // مدیریت حرفه‌ای دکمه‌ی Back گوشی:
            // اگر سایت تاریخچه عقب‌گرد داشت، برمی‌گردد صفحه قبل؛ وگرنه از اپ خارج می‌شود.
            BackHandler(enabled = true) {
                val currentWebView = webViewRef
                if (currentWebView?.canGoBack() == true) {
                    currentWebView.goBack()
                } else {
                    finish()
                }
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewRef = this

                            // فعال‌سازی شتاب‌دهنده سخت‌افزاری برای روانی کامل و بدون لگ اسکرول‌ها
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)

                            // استفاده از متد استاندارد و غیرمنسوخ WebResourceRequest برای مدیریت لینک‌ها داخل خود اپ
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    view?.loadUrl(url)
                                    return true
                                }
                            }

                            webChromeClient = WebChromeClient()

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                setBuiltInZoomControls(false)
                                displayZoomControls = false
                                
                                cacheMode = WebSettings.LOAD_DEFAULT
                                setSaveFormData(true)
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }

                            // مدیریت فوق‌العاده کوکی‌ها و حفظ دائمی سشن‌ها
                            CookieManager.getInstance().apply {
                                setAcceptCookie(true)
                                setAcceptThirdPartyCookies(this@WebView, true)
                            }

                            // لود اولیه سایت اصلی با سرعت بالا
                            loadUrl("https://hojjat.timzo.ir/")
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        CookieManager.getInstance().flush()
    }
}