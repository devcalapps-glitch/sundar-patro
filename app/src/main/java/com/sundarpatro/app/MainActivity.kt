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
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.Locale
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebViewAssetLoader
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var adView: AdView? = null
    private var isAdLoaded = false
    private var isAdSlotVisible = false
    private var adSlotTopPx = 0f
    private var adSlotOffsetXPx = 0f
    private var lastAdWidthDp = 0
    private val isMobileAdsInitialized = AtomicBoolean(false)

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
        private fun speakSentences(text: String) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "sundar_today")
        }
        @JavascriptInterface fun speak(text: String) = runOnUiThread {
            if (tts == null) {
                tts = TextToSpeech(this@MainActivity) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        tts?.language = Locale("ne", "NP")
                        tts?.setSpeechRate(0.95f)
                        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {}
                            override fun onDone(utteranceId: String?) {
                                if (utteranceId == "sundar_today") {
                                    webView.post { webView.evaluateJavascript("window._onNarrationDone&&window._onNarrationDone()", null) }
                                }
                            }
                            @Deprecated("Deprecated in Java")
                            override fun onError(utteranceId: String?) {
                                webView.post { webView.evaluateJavascript("window._onNarrationDone&&window._onNarrationDone()", null) }
                            }
                        })
                        ttsReady = true
                        speakSentences(text)
                    }
                }
            } else if (ttsReady) {
                speakSentences(text)
            }
        }
        @JavascriptInterface fun stopSpeak() = runOnUiThread { tts?.stop() }
        @JavascriptInterface fun syncAdSlot(visible: Boolean, topCssPx: Int, offsetXCssPx: Int) = runOnUiThread {
            isAdSlotVisible = visible
            val density = resources.displayMetrics.density
            adSlotTopPx = topCssPx * density
            adSlotOffsetXPx = offsetXCssPx * density
            updateAdVisibility()
        }
        @JavascriptInterface fun setThemeMode(dark: Boolean) = runOnUiThread {
            applySystemBarTheme(dark)
        }
    }

    private fun applySystemBarTheme(isDark: Boolean) {
        val bg = if (isDark) Color.parseColor("#111110") else Color.WHITE
        findViewById<android.view.View>(R.id.rootContainer)?.setBackgroundColor(bg)
        if (::webView.isInitialized) webView.setBackgroundColor(bg)
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = !isDark
        windowInsetsController.isAppearanceLightNavigationBars = !isDark
    }

    private fun updateAdVisibility() {
        val adContainer = findViewById<android.widget.FrameLayout>(R.id.adContainer) ?: return
        if (isAdLoaded && isAdSlotVisible) {
            adContainer.translationX = adSlotOffsetXPx
            adContainer.translationY = adSlotTopPx
            adContainer.visibility = android.view.View.VISIBLE
        } else {
            adContainer.visibility = android.view.View.GONE
        }
    }

    private fun currentWindowWidthDp(): Int {
        val displayMetrics = resources.displayMetrics
        val adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                android.view.WindowInsets.Type.systemBars() or android.view.WindowInsets.Type.displayCutout()
            )
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            @Suppress("DEPRECATION")
            displayMetrics.widthPixels
        }
        val density = displayMetrics.density
        var widthDp = (adWidthPixels / density).toInt()
        if (widthDp <= 0) widthDp = (displayMetrics.widthPixels / density).toInt()
        if (widthDp <= 0) widthDp = 320
        return widthDp
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Override system dark mode: always run in Light Mode by default unless toggled inside the app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // 1. Install Splash Screen before super.onCreate()
        installSplashScreen()

        // 2. Enable modern Edge-to-Edge display
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rootContainer = findViewById<android.view.View>(R.id.rootContainer)
        webView = findViewById(R.id.webView)

        // 3. Handle System Bar & Display Cutout Insets (all 4 edges for tablets, foldables & landscape)
        ViewCompat.setOnApplyWindowInsetsListener(rootContainer) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            rootContainer.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.Builder(insets)
                .setInsets(WindowInsetsCompat.Type.systemBars(), androidx.core.graphics.Insets.NONE)
                .setInsets(WindowInsetsCompat.Type.displayCutout(), androidx.core.graphics.Insets.NONE)
                .build()
        }

        // Default to Light Mode system bars (overriding phone's dark mode setting)
        applySystemBarTheme(isDark = false)

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
            builtInZoomControls = true
            setSupportZoom(true)
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        webView.clearCache(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            webView.settings.forceDark = WebSettings.FORCE_DARK_OFF
        }
        webView.setBackgroundColor(Color.WHITE)

        // 6. Custom WebViewClient for internal asset routing & external link interception
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val h = adView?.adSize?.height ?: 0
                if (isAdLoaded && h > 0) {
                    webView.evaluateJavascript("window.onAdLoaded&&window.onAdLoaded(true,$h)", null)
                }
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

        // 8. Initialize Mobile Ads with UMP consent
        setupAds()

        // 9. Intuitive Back Gesture handling for sub-screens & cards
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
                        // 2. Return from sub-views (Holidays, Auspicious, Date Converter, About, Privacy, Support) back to 'More'
                        var activeView = document.querySelector('.view:not([hidden])');
                        if (activeView && (activeView.id === 'view-holidays' || activeView.id === 'view-about' || activeView.id === 'view-convert' || activeView.id === 'view-auspicious' || activeView.id === 'view-privacy' || activeView.id === 'view-support')) {
                            var backBtn = activeView.querySelector('.back') || document.querySelector('.tb[data-view="more"]');
                            if (backBtn) { backBtn.click(); return true; }
                        }
                        // 3. Return to 'Today' home screen if on Month/Festivals/Clocks/More
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

    private fun setupAds() {
        // 1. Immediately initialize Mobile Ads and request banner
        initializeMobileAdsSdk()

        // 2. Request consent information in parallel (for EEA / UK users)
        try {
            val params = ConsentRequestParameters.Builder().build()
            val consentInformation = UserMessagingPlatform.getConsentInformation(this)
            consentInformation.requestConsentInfoUpdate(
                this,
                params,
                {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { _ ->
                        // Consent form dismissed or not required
                    }
                },
                { error ->
                    android.util.Log.d("SundarAdMob", "Consent update info: ${error.message}")
                }
            )
        } catch (e: Exception) {
            android.util.Log.e("SundarAdMob", "Consent error", e)
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitialized.getAndSet(true)) return
        android.util.Log.d("SundarAdMob", "Initializing MobileAds SDK...")
        MobileAds.initialize(this) { initStatus ->
            android.util.Log.d("SundarAdMob", "MobileAds initialized: $initStatus")
        }
        runOnUiThread {
            loadAdaptiveBanner()
        }
    }

    private fun getAdSize(): AdSize {
        val widthDp = currentWindowWidthDp()
        // Account for 92dp left rail on wide tablets and cap at 728dp leaderboard width
        val availableDp = if (widthDp >= 960) widthDp - 124 else widthDp - 28
        val adWidth = minOf(maxOf(availableDp, 320), 728)
        lastAdWidthDp = adWidth
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isAdSlotVisible = false
        updateAdVisibility()
        if (isMobileAdsInitialized.get()) {
            val widthDp = currentWindowWidthDp()
            val availableDp = if (widthDp >= 960) widthDp - 124 else widthDp - 28
            val newAdWidth = minOf(maxOf(availableDp, 320), 728)
            if (kotlin.math.abs(newAdWidth - lastAdWidthDp) >= 24) {
                adView?.destroy()
                adView = null
                isAdLoaded = false
                loadAdaptiveBanner()
            }
        }
        if (::webView.isInitialized) {
            webView.postDelayed({
                webView.evaluateJavascript("window._checkBottomAd&&window._checkBottomAd()", null)
            }, 220)
        }
    }

    private fun loadAdaptiveBanner() {
        val adContainer = findViewById<android.widget.FrameLayout>(R.id.adContainer) ?: return
        val isDebuggable = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val targetAdUnitId = if (isDebuggable) {
            "ca-app-pub-3940256099942544/9214589741"
        } else {
            getString(R.string.admob_banner_ad_unit_id)
        }
        android.util.Log.d("SundarAdMob", "Loading adaptive banner with ID: $targetAdUnitId (debuggable=$isDebuggable)")

        val adSize = this@MainActivity.getAdSize()
        val ad = AdView(this).apply {
            adUnitId = targetAdUnitId
            setAdSize(adSize)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    android.util.Log.d("SundarAdMob", "Banner Ad loaded successfully!")
                    isAdLoaded = true
                    updateAdVisibility()
                    webView.evaluateJavascript("window.onAdLoaded&&window.onAdLoaded(true,${adSize.height})", null)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    android.util.Log.e("SundarAdMob", "Banner Ad failed to load: code ${error.code}, message: ${error.message}, responseInfo: ${error.responseInfo}")
                    isAdLoaded = false
                    updateAdVisibility()
                    webView.evaluateJavascript("window.onAdLoaded&&window.onAdLoaded(false,0)", null)
                }
            }
        }
        adView = ad
        adContainer.removeAllViews()
        val lp = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
            android.view.Gravity.CENTER_HORIZONTAL
        )
        adContainer.addView(ad, lp)
        ad.loadAd(AdRequest.Builder().build())
    }

    override fun onPause() {
        adView?.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
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
        tts?.stop()
        tts?.shutdown()
        tts = null
        adView?.destroy()
        adView = null
        webView.destroy()
        super.onDestroy()
    }
}

