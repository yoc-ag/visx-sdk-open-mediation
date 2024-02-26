package com.yoc.visx.sdk.mediation

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.*
import com.yoc.visx.sdk.VisxAdManager
import com.yoc.visx.sdk.adview.tracker.VisxCallbacks
import com.yoc.visx.sdk.mediation.MediationUtil.auid
import com.yoc.visx.sdk.mediation.MediationUtil.bannerAdSize
import com.yoc.visx.sdk.mediation.MediationUtil.getVersionInfo
import com.yoc.visx.sdk.mediation.MediationUtil.setParameterMap

@Keep
class VISXMediationAdapterBannerGAD : Adapter() {
    override fun initialize(
        context: Context,
        initializationCompleteCallback: InitializationCompleteCallback,
        list: List<MediationConfiguration>
    ) {
        Log.i(
            TAG, "initialize()" +
                    " context: " + context +
                    " initializationCompleteCallback: " + initializationCompleteCallback +
                    " List<MediationConfiguration>: " + list
        )
    }

    override fun getSDKVersionInfo(): VersionInfo {
        return getVersionInfo(true)
    }

    override fun getVersionInfo(): VersionInfo {
        return getVersionInfo(false)
    }

    override fun loadBannerAd(
        mediationBannerAdConfiguration: MediationBannerAdConfiguration,
        callback: MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback>
    ) {
        setParameterMap(mediationBannerAdConfiguration.serverParameters[MediationUtil.PARAMETER_KEY].toString())
        VisxAdManager.Builder()
            .visxAdUnitID(auid)
            .adSize(bannerAdSize)
            .setMediation()
            .context(mediationBannerAdConfiguration.context)
            .callback(object : VisxCallbacks() {
                override fun onAdRequestStarted(visxAdManager: VisxAdManager) {
                    Log.i(TAG, "onAdRequestStarted()")
                }

                override fun onAdResponseReceived(visxAdManager: VisxAdManager, price: Double, currency: String) {
                    Log.i(TAG, "onAdResponseReceived()")
                    callback.onSuccess(visxAdManager::adContainer)
                }

                override fun onAdLoadingFinished(visxAdManager: VisxAdManager, message: String) {
                    Log.i(TAG, "onAdLoadingFinished() Message: $message")
                }

                override fun onAdLoadingFailed(visxAdManager: VisxAdManager, message: String, errorCode: Int, isFinal: Boolean) {
                    Log.i(
                        TAG,
                        "onAdLoadingFailed() ErrorCode: " + errorCode + "Message: " + message + " isFinal: " + isFinal
                    )
                    callback.onFailure(AdError(errorCode, message, TAG))
                }

                override fun onAdSizeChanged(width: Int, height: Int) {
                    Log.i(TAG, "onAdSizeChanged()")
                }

                override fun onAdClicked() {
                    Log.i(TAG, "onAdClicked()")
                }

                override fun onAdLeftApplication() {
                    Log.i(TAG, "onAdLeftApplication()")
                }

                override fun onVideoFinished() {
                    Log.i(TAG, "onAdLeftApplication()")
                }

                override fun onEffectChange(effect: String) {
                    Log.i(TAG, "onEffectChange() -> effect: $effect")
                }

                override fun onAdViewable() {
                    Log.i(TAG, "onAdViewable()")
                }

                override fun onAdResumeApplication() {
                    Log.i(TAG, "onAdResumeApplication()")
                }

                override fun onAdClosed() {
                    Log.i(TAG, "onAdClosed()")
                }

                override fun onInterstitialClosed() {
                    Log.i(TAG, "onInterstitialClosed()")
                }

                override fun onInterstitialWillBeClosed() {
                    Log.i(TAG, "onInterstitialWillBeClosed()")
                }

                override fun onLandingPageOpened(inExternalBrowser: Boolean) {
                    Log.i(TAG, "onLandingPageOpened()")
                }
            })
            .build()
    }

    companion object {
        private val TAG = VISXMediationAdapterBannerGAD::class.java.simpleName
    }
}