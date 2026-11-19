package com.arrowsecho.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    // TODO: Replace Google Test Ad Unit IDs with your real AdMob Ad Unit IDs before release!
    private val INTERSTITIAL_AD_UNIT = "ca-app-pub-3940256099942544/1033173712"
    private val REWARDED_AD_UNIT     = "ca-app-pub-3940256099942544/5224354917"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {
            loadInterstitial()
            loadRewarded()
        }

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            addJavascriptInterface(AdBridge(), "AndroidAds")
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    return assetLoader.shouldInterceptRequest(request.url)
                }
            }
            loadUrl("https://appassets.androidplatform.net/assets/index.html")
        }
        setContentView(webView)
    }

    private fun loadInterstitial() {
        InterstitialAd.load(
            this,
            INTERSTITIAL_AD_UNIT,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad }
                override fun onAdFailedToLoad(err: LoadAdError) { interstitialAd = null }
            }
        )
    }

    private fun loadRewarded() {
        RewardedAd.load(
            this,
            REWARDED_AD_UNIT,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
                override fun onAdFailedToLoad(err: LoadAdError) { rewardedAd = null }
            }
        )
    }

    inner class AdBridge {
        @JavascriptInterface
        fun showInterstitial() {
            runOnUiThread {
                interstitialAd?.show(this@MainActivity)
                interstitialAd = null
                loadInterstitial()
            }
        }

        @JavascriptInterface
        fun showRewardedHint() {
            runOnUiThread {
                val ad = rewardedAd
                if (ad != null) {
                    ad.show(this@MainActivity) {
                        webView.evaluateJavascript("window.onAdRewarded('hint')", null)
                    }
                    rewardedAd = null
                    loadRewarded()
                } else {
                    // Fallback if ad is still loading
                    webView.evaluateJavascript("window.onAdRewarded('hint')", null)
                    loadRewarded()
                }
            }
        }
    }
}
