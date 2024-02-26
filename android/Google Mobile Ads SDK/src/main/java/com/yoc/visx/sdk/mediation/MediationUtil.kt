package com.yoc.visx.sdk.mediation

import android.text.TextUtils
import android.util.Size
import com.google.android.gms.ads.mediation.VersionInfo
import com.yoc.visx.sdk.BuildConfig
import com.yoc.visx.sdk.logger.VISXLog.w
import com.yoc.visx.sdk.util.ad.AdSize
import com.yoc.visx.sdk.util.ad.PlacementType

internal object MediationUtil {
    const val PARAMETER_KEY = "parameter"
    private val paramsMap: MutableMap<String, String> = HashMap()
    private var visxAdSize: AdSize? = null
    private const val AUID_KEY = "auid"
    private const val SIZE_KEY = "size"

    /**
     * Getting the SDK Version from
     *
     * @return VersionInfo
     * @see BuildConfig.VERSION_NAME
     * defined in SDK build.gradle and processing for populating
     *
     * @see VersionInfo
     */
    @JvmStatic
    fun getVersionInfo(isSDKVersion: Boolean): VersionInfo {
        val versionString = BuildConfig.VERSION_NAME
        val splits = versionString.split("\\.".toRegex()).toTypedArray()
        if (splits.size >= 4) {
            val major = splits[0].toInt()
            val minor = splits[1].toInt()
            val micro =
                if (isSDKVersion) splits[2].toInt() else splits[2].toInt() * 100 + splits[3].toInt()
            return VersionInfo(major, minor, micro)
        }
        return VersionInfo(0, 0, 0)
    }

    /**
     * Creating and setting parameter map from server response from Google Mediation Adapters
     *
     * @param parameters by splitting the response first by ";" for separating different key <> value pair value
     * and then by "=" for splitting key and value strings
     * (correct string parameters example: auid=910570;size=300x250)
     * @see VISXMediationAdapterBannerGAD.loadBannerAd
     * @see VISXMediationAdapterInterstitialGAD.loadInterstitialAd
     */
    @JvmStatic
    fun setParameterMap(parameters: String) {
        if (!TextUtils.isEmpty(parameters)) {
            val keyValuePairs = parameters.split(";".toRegex()).toTypedArray()
            for (keyValuePair in keyValuePairs) {
                if (!TextUtils.isEmpty(keyValuePair)) {
                    val tokens = keyValuePair.split("=".toRegex()).toTypedArray()
                    if (!TextUtils.isEmpty(tokens[0]) && !TextUtils.isEmpty(
                            tokens[1]
                        )
                    ) {
                        paramsMap[tokens[0]] = tokens[1]
                    }
                }
            }
        } else {
            w(MediationUtil::class.java.simpleName, "Mediation parameter response null or empty")
        }
    }

    /**
     * Method for getting the auid value from
     * serverParameters response that are stored inside paramsMap
     *
     * @return value of auid
     * @see MediationUtil.setParameterMap
     */
    @JvmStatic
    val auid: String?
        get() {
            var auid: String? = ""
            if (!TextUtils.isEmpty(paramsMap[AUID_KEY])) {
                auid = paramsMap[AUID_KEY]
            }
            return auid
        }

    /**
     * Method for getting the size value from
     * serverParameters response that are stored inside paramsMap and store in
     *
     * @return AdSize
     * @see AdSize object, ready for setting as adSize inside the
     *
     * see VisxAdManager.Builder.adSize
     * @see MediationUtil.setParameterMap
     */
    @JvmStatic
    val bannerAdSize: AdSize?
        get() {
            visxAdSize = if (!TextUtils.isEmpty(paramsMap[SIZE_KEY])) {
                val sizeList: Array<String?> = paramsMap[SIZE_KEY]!!
                    .split("x".toRegex()).toTypedArray()
                if (sizeList[0] != null && sizeList[1] != null) {
                    AdSize(
                        Size(
                            sizeList[0]?.toInt() ?: 0, sizeList[1]?.toInt() ?: 0
                        ), PlacementType.INLINE
                    )
                } else {
                    AdSize.SMARTPHONE_320x50
                }
            } else {
                AdSize.SMARTPHONE_320x50
            }
            return visxAdSize
        }

    /**
     * Method for getting the size value from
     * serverParameters response that are stored inside paramsMap and store in
     *
     * @return AdSize
     * @see AdSize object, ready for setting as adSize inside the
     *
     * see VisxAdManager.Builder.adSize
     * @see MediationUtil.setParameterMap
     */
    @JvmStatic
    val interstitialAdSize: AdSize?
        get() {
            visxAdSize = if (!TextUtils.isEmpty(paramsMap[SIZE_KEY])) {
                val sizeList: Array<String?> = paramsMap[SIZE_KEY]!!
                    .split("x".toRegex()).toTypedArray()
                if (sizeList[0] != null && sizeList[1] != null) {
                    AdSize(
                        Size(
                            sizeList[0]?.toInt() ?: 0, sizeList[1]?.toInt() ?: 0
                        ), PlacementType.INTERSTITIAL
                    )
                } else {
                    AdSize.INTERSTITIAL_320x480
                }
            } else {
                AdSize.INTERSTITIAL_320x480
            }
            return visxAdSize
        }
}