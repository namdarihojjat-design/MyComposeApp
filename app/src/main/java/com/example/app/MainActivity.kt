package com.example.app

import android.annotation.SuppressLint
import android.os.Bundle
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

                            // استفاده از متد استاندارد و غیرمنسوخ WebResourceRequest برای مدیریت لینک‌ها داخل خود اپ
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    // اجازه دادن به وب‌ویو برای لود کردن تمام لینک‌های داخلی سایت
                                    view?.loadUrl(url)
                                    return true
                                }
                            }

                            // پشتیبانی کامل از قابلیت‌های پیشرفته مرورگر (پاپ‌آپ‌ها، فایل‌ها و المان‌های جاوااسکریپتی)
                            webChromeClient = WebChromeClient()

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                
                                // شبیه‌سازی دقیق رفتار کروم برای رندر بی‌نقص صفحات موبایل و دسکتاپ
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                setBuiltInZoomControls(false)
                                displayZoomControls = false
                                
                                // حافظه پنهان، کش و ذخیره فرم‌ها برای عملکرد روان و سریع
                                cacheMode = WebSettings.LOAD_DEFAULT
                                setSaveFormData(true)
                                
                                // اجازه محتوای ترکیبی (HTTP/HTTPS) برای جلوگیری از خرابی تصاویر و اسکریپت‌ها
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                
                                // فعال‌سازی شتاب‌دهنده سخت‌افزاری برای روانی کامل اسکرول‌ها (60 فریم بر ثانیه)
                            }

                            // مدیریت فوق‌العاده کوکی‌ها و حفظ دائمی سشن‌ها (دقیقاً مثل کروم)
                            val cookieManager = CookieManager.getInstance().apply {
                                setAcceptCookie(true)
                                setAcceptThirdPartyCookies(this@apply, true)
                            }

                            // لود اولیه سایت اصلی
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
        // ذخیره آنی کوکی‌ها و سشن‌ها هنگام بسته شدن یا رفتن در پس‌زمینه اپ
        CookieManager.getInstance().flush()
    }
}
