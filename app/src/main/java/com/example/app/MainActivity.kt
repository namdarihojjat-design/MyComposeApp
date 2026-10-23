package com.example.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import com.google.androidbrowserhelper.trusted.TwaLauncher

class MainActivity : ComponentActivity() {

    private lateinit var twaLauncher: TwaLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        twaLauncher = TwaLauncher(this)
        val uri = Uri.parse("https://hojjat.timzo.ir/")

        try {
            // روش استاندارد و کاملاً ایمن در مستندات رسمی Google
            twaLauncher.launch(TrustedWebActivityIntentBuilder(uri))
            finish()
        } catch (t: Throwable) {
            // Fallback امن با Custom Tabs
            CustomTabsIntent.Builder().build().launchUrl(this, uri)
            finish()
        }
    }

    override fun onDestroy() {
        if (::twaLauncher.isInitialized) {
            twaLauncher.destroy()
        }
        super.onDestroy()
    }
}
