//
//  VisxInterstitialGAD.swift
//  YOC Showcase
//
//  Created by Stefan Markovic on 20.8.21.
//  Copyright © 2021 YOC AG. All rights reserved.
//

import GoogleMobileAds
import VisxSDK

@objc(VISXInterstitialGAD)
public class VISXInterstitialGAD: NSObject, GADFullScreenContentDelegate, LoadMediationDelegate {

    private var visxAdView: VisxAdView?

    public func loadMediationAd(_ mediation: Mediation, adView: VisxAdView, customTargeting: [String : String]?) {
        guard let controller = VisxMediationUtils.topMostController() else { return }
        guard let adId = mediation.adunit else { return }
        visxAdView = adView
        let request = GAMRequest()
        if let params = customTargeting {
            request.customTargeting = params
        }
        GADInterstitialAd.load(withAdUnitID: adId, request: request) { interstitialAd, error in
            if let interstitial = interstitialAd {
                interstitial.fullScreenContentDelegate = self
                interstitial.present(fromRootViewController: controller)
            }
        }
        
        GADInterstitialAd.load(withAdUnitID: adId, request: request) { [self] ad, error in
            if let error {
                print("Failed to load interstitial ad with error: \(error.localizedDescription)")
                return
            }
            if let interstitial = ad {
                interstitial.fullScreenContentDelegate = self
                interstitial.present(fromRootViewController: controller)
            }
        }
    }

    public func ad(_ ad: GADFullScreenPresentingAd, didFailToPresentFullScreenContentWithError error: Error) {
        print("interstitial:didFailToReceiveAdWithError")
    }
}
