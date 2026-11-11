package com.sundarpatro.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebViewAssetLoader

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    /** Set when the user was sent to system settings to allow notifications; checked on return. */
    private var enableOnReturn = false

    private val notifPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        Reminders.markAsked(this)
        setRemindersOn(granted && Reminders.canPost(this), blocked = !granted)
    }

    private fun setRemindersOn(on: Boolean, blocked: Boolean = false) {
        Reminders.setEnabled(this, on)
        webView.evaluateJavascript("window.onRemindersChanged&&onRemindersChanged($on,$blocked)", null)
    }

    private fun openNotificationSettings() {
        enableOnReturn = true
        val i = if (Build.VERSION.SDK_INT >= 26)
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        else Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        try { startActivity(i) } catch (_: Exception) { enableOnReturn = false }
    }

    /** Bridge for the bundled page only (the WebView never navigates off appassets). */
    inner class ReminderBridge {
        @JavascriptInterface fun remindersEnabled(): Boolean = Reminders.isEnabled(this@MainActivity)
        @JavascriptInterface fun setReminders(json: String) {
            try { Reminders.save(this@MainActivity, json) } catch (_: Exception) {}
        }
        @JavascriptInterface fun enableReminders() = runOnUiThread {
            when {
                Reminders.canPost(this@MainActivity) -> setRemindersOn(true)
                Build.VERSION.SDK_INT >= 33 && !(Reminders.wasAsked(this@MainActivity) &&
                    !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) ->
                    notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                // Permission permanently denied, or notifications switched off for the app: only settings can fix it.
                else -> openNotificationSettings()
            }
        }
        @JavascriptInterface fun disableReminders() = runOnUiThread { setRemindersOn(false) }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Install Splash Screen before super.onCreate()
        installSplashScreen()

        // 2. Enable modern Edge-to-Edge display
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootContainer = findViewById<android.view.View>(R.id.rootContainer)
        webView = findViewById(R.id.webView)

        // 3. Handle System Bar Insets (Status bar & Navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(rootContainer) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            rootContainer.setPadding(0, statusBars.top, 0, navBars.bottom)
            insets
        }

        // Configure system bar contrast based on current night mode
        val isNight = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = !isNight
        windowInsetsController.isAppearanceLightNavigationBars = !isNight

        // 4. Secure WebViewAssetLoader configuration
        val assetLoader = WebViewAssetLoader.Builder()
            .setDomain("appassets.androidplatform.net")
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        // 5. Configure WebSettings
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            allowFileAccess = false
            allowContentAccess = false
            displayZoomControls = false
            builtInZoomControls = false
            setSupportZoom(false)
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        webView.clearCache(true)

        webView.setBackgroundColor(if (isNight) Color.parseColor("#151515") else Color.WHITE)

        // 6. Custom WebViewClient for internal asset routing & external link interception
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val url = request.url.toString()
                return if (url.startsWith("https://appassets.androidplatform.net/")) {
                    false
                } else {
                    // Open any external web links or mailto in device browser
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (_: Exception) {}
                    true
                }
            }
        }

        webView.addJavascriptInterface(ReminderBridge(), "SundarNative")

        // 7. Load local assets bundle via secure virtual HTTPS domain
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")

        // 8. Intuitive Back Gesture handling for sub-screens & cards
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                webView.evaluateJavascript(
                    """
                    (function() {
                        // 1. Close open day details card if open
                        var dc = document.getElementById('daycard');
                        if (dc && !dc.hidden && document.getElementById('dc-close')) {
                            document.getElementById('dc-close').click();
                            return true;
                        }
                        // 2. Return from sub-views (Auspicious, Date Converter, Clocks, About, Privacy, Support) back to 'More'
                        var activeView = document.querySelector('.view:not([hidden])');
                        if (activeView && (activeView.id === 'view-about' || activeView.id === 'view-convert' || activeView.id === 'view-auspicious' || activeView.id === 'view-clocks' || activeView.id === 'view-privacy' || activeView.id === 'view-support')) {
                            var backBtn = activeView.querySelector('.back') || document.querySelector('.tb[data-view="more"]');
                            if (backBtn) { backBtn.click(); return true; }
                        }
                        // 3. Return to 'Today' home screen if on Month/Festivals/Holidays/More
                        if (activeView && activeView.id !== 'view-today') {
                            var todayBtn = document.querySelector('.tb[data-view="today"]');
                            if (todayBtn) { todayBtn.click(); return true; }
                        }
                        return false;
                    })();
                    """.trimIndent()
                ) { handled ->
                    if (handled != "true") {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!::webView.isInitialized) return
        if (enableOnReturn) {
            enableOnReturn = false
            setRemindersOn(Reminders.canPost(this), blocked = !Reminders.canPost(this))
        } else if (Reminders.isEnabled(this) && !Reminders.canPost(this)) {
            // Notifications were turned off in system settings: reflect that instead of failing silently.
            setRemindersOn(false, blocked = true)
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}

