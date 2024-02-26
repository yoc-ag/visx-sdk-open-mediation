package com.yoc.visx.sdk.mediation

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.*
import com.yoc.visx.sdk.VisxAdManager
import com.yoc.visx.sdk.adview.tracker.VisxCallbacks
import com.yoc.visx.sdk.mediation.MediationUtil.auid
import com.yoc.visx.sdk.mediation.MediationUtil.getVersionInfo
import com.yoc.visx.sdk.mediation.MediationUtil.interstitialAdSize
import com.yoc.visx.sdk.mediation.MediationUtil.setParameterMap

@Keep
class VISXMediationAdapterInterstitialGAD : Adapter() {

    companion object {
        private val TAG = VISXMediationAdapterInterstitialGAD::class.java.simpleName
    }

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

    override fun getVersionInfo(): VersionInfo {
        return getVersionInfo(false)
    }

    override fun getSDKVersionInfo(): VersionInfo {
        Log.i(TAG, "getSDKVersionInfo()")
        return getVersionInfo(true)
    }

    override fun loadInterstitialAd(
        mediationInterstitialAdConfiguration: MediationInterstitialAdConfiguration,
        callback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>
    ) {
        setParameterMap(mediationInterstitialAdConfiguration.serverParameters[MediationUtil.PARAMETER_KEY].toString())
        VisxAdManager.Builder()
            .visxAdUnitID(auid)
            .adSize(interstitialAdSize)
            .context(mediationInterstitialAdConfiguration.context)
            .setMediation()
            .callback(object : VisxCallbacks() {
                override fun onAdRequestStarted(visxAdManager: VisxAdManager) {
                    Log.i(TAG, "onAdRequestStarted()")
                }

                override fun onAdResponseReceived(visxAdManager: VisxAdManager, price: Double, currency: String) {
                    Log.i(TAG, "onAdResponseReceived()")
                }

                override fun onAdLoadingFinished(visxAdManager: VisxAdManager, message: String) {
                    Log.i(TAG, "onAdLoadingFinished() Message: $message")
                    callback.onSuccess(MediationInterstitialAd { visxAdManager.showModalInterstitial() })
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

                override fun onInterstitialWillBeClosed() {
                    Log.i(TAG, "onInterstitialWillBeClosed()")
                }

                override fun onInterstitialClosed() {
                    Log.i(TAG, "onInterstitialClosed()")
                }

                override fun onLandingPageOpened(inExternalBrowser: Boolean) {
                    Log.i(TAG, "onLandingPageOpened()")
                }

                override fun onAdClosed() {
                    Log.i(TAG, "onAdClosed")
                }
            })
            .build()
    }
}