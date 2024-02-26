package com.yoc.visx.sdk.adapter.googleads

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.yoc.visx.sdk.mediation.VISXMediationEventListener
import com.yoc.visx.sdk.mediation.adapter.VisxMediationAdapter
import java.util.*
import kotlin.collections.HashMap

class VISXBannerGAD : VisxMediationAdapter {

    companion object {
        private const val AD_UNIT = "adunit"
        private const val AD_SIZES = "sizes"
    }

    private lateinit var adView: AdManagerAdView

    override fun loadAd(
        targetingParams: Map<String?, String?>?, context: Context,
        eventListener: VISXMediationEventListener?
    ) {
        adView = AdManagerAdView(context)
        adView.adUnitId = (targetingParams as HashMap)[AD_UNIT] ?: ""
        adView.setAdSizes(*getSizes(targetingParams[AD_SIZES]))
        adView.adListener = object : AdListener() {
            override fun onAdClosed() {}
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
                eventListener?.onAdLoadingFailed(errorCodeMessage, "GAD Banner")
                destroy()
            }

            override fun onAdLoaded() {
                eventListener?.onAdLoaded()
                // Add the adView to the VisxAdViewContainer
                eventListener?.showAd(adView)
            }

            override fun onAdOpened() {}
        }

        // Request and load an Ad with custom parameters
        val adRequestBuilder = AdManagerAdRequest.Builder()
        if (targetingParams != null) {
            targetingParams.remove(AD_SIZES)
            targetingParams.remove(AD_UNIT)
            for ((key, value) in targetingParams) {
                adRequestBuilder.addCustomTargeting(key ?: "", value ?: "")
            }
        }
        adView.loadAd(adRequestBuilder.build())
    }

    override fun show() {
        // method used for interstitial
    }

    override fun destroy() {
        adView.destroy()
    }

    private fun getSizes(sizes: String?): Array<AdSize?> {
        val sizesTrimmed = sizes?.replace("[\\[ \\] ,]".toRegex(), " ")
        val sizeList: MutableList<Int> = ArrayList()
        val scanner = Scanner(sizesTrimmed)
        while (scanner.hasNextInt()) {
            sizeList.add(scanner.nextInt())
        }
        val adSizesList = arrayOfNulls<AdSize>(sizeList.size / 2)
        var position = 0
        var i = 0
        while (i < sizeList.size) {
            adSizesList[position] = AdSize(sizeList[i], sizeList[i + 1])
            position++
            i += 2
        }
        return adSizesList
    }
}