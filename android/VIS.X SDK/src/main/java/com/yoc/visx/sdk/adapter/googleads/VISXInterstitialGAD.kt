package com.yoc.visx.sdk.adapter.googleads

import com.yoc.visx.sdk.logger.VISXLog.w
import com.yoc.visx.sdk.logger.VISXLog.i
import com.yoc.visx.sdk.mediation.adapter.VisxMediationAdapter
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.yoc.visx.sdk.mediation.VISXMediationEventListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.AdError
import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest

class VISXInterstitialGAD : VisxMediationAdapter {

    private var publisherInterstitialAd: AdManagerInterstitialAd? = null
    private lateinit var context: Context

    override fun loadAd(
        targetingParams: Map<String?, String?>?, context: Context,
        eventListener: VISXMediationEventListener?
    ) {
        this.context = context
        var adUnit = ""
        if (targetingParams!= null && targetingParams[AD_UNIT] != null) {
            adUnit = (targetingParams as HashMap).remove(AD_UNIT) ?: ""
        }
        val adRequestBuilder = AdManagerAdRequest.Builder()
        if (targetingParams != null) {
            for ((key, value) in targetingParams) {
                adRequestBuilder.addCustomTargeting(key ?: "", value ?: "")
            }
        }
        AdManagerInterstitialAd.load(context,
            adUnit,
            adRequestBuilder.build(),
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdLoaded(adManagerInterstitialAd: AdManagerInterstitialAd) {
                    super.onAdLoaded(adManagerInterstitialAd)
                    eventListener?.onAdLoaded()
                    publisherInterstitialAd = adManagerInterstitialAd
                    interstitialCallbackInit(publisherInterstitialAd!!)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    if (loadAdError.code == AdRequest.ERROR_CODE_NO_FILL) {
                        eventListener?.mediationFailWithNoAd()
                        return
                    }
                    val errorCodeMessage: String = when (loadAdError.code) {
                        AdRequest.ERROR_CODE_INTERNAL_ERROR -> "ERROR_CODE_INTERNAL_ERROR"
                        AdRequest.ERROR_CODE_INVALID_REQUEST -> "ERROR_CODE_INVALID_REQUEST"
                        AdRequest.ERROR_CODE_NETWORK_ERROR -> "ERROR_CODE_NETWORK_ERROR"
                        else -> "AD FAILED TO LOAD - UNKNOWN ERROR"
                    }
                    eventListener?.onAdLoadingFailed(errorCodeMessage, "GAD Interstitial")
                    destroy()
                }
            })
    }

    private fun interstitialCallbackInit(interstitialAd: AdManagerInterstitialAd) {
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                w("The ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                w("The ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                // Make sure to set your reference to null so you don't
                // show it a second time.
                publisherInterstitialAd = null
                i("TAG", "The ad was shown.")
            }
        }
    }

    override fun show() {
        if (publisherInterstitialAd != null && context is Activity) {
            (context as Activity?)?.let { publisherInterstitialAd?.show(it) }
        } else {
            w("Google interstitial Ad, in Google as Primary SDK in Mediation, wasn't ready yet.")
        }
    }

    override fun destroy() {
        if (publisherInterstitialAd != null) {
            publisherInterstitialAd = null
        }
    }

    companion object {
        private const val AD_UNIT = "adunit"
    }
}