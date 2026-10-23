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
                        val webView = WebView(context)
                        webViewRef = webView

                        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

                        webView.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false
                                view?.loadUrl(url)
                                return true
                            }
                        }

                        webView.webChromeClient = WebChromeClient()

                        webView.settings.apply {
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

                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setAcceptThirdPartyCookies(webView, true)

                        webView.loadUrl("https://hojjat.timzo.ir/")
                        webView
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
