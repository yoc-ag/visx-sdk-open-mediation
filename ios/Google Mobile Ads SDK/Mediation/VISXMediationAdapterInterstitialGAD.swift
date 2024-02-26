//
//  VISXMediationAdapterInterstitialGAD.swift
//  YOC Showcase
//
//  Created by Stefan Markovic on 17.8.21.
//  Copyright © 2021 YOC AG. All rights reserved.
//

import Foundation
import GoogleMobileAds
import VisxSDK

@objc(VISXMediationAdapterInterstitialGAD)
public class VISXMediationAdapterInterstitialGAD: NSObject, GADMediationAdapter, GADMediationInterstitialAd {

    private var adView: VisxAdView?
    private var interstitialCompletionHandler: GADMediationInterstitialLoadCompletionHandler?
    private weak var delegate: GADMediationInterstitialAdEventDelegate?
    
    required public override init() { }

    public func loadInterstitial(for adConfiguration: GADMediationInterstitialAdConfiguration,
                                 completionHandler: @escaping GADMediationInterstitialLoadCompletionHandler) {
        interstitialCompletionHandler = completionHandler
        guard let adId = VISXCustomEventUtils.adId(from: adConfiguration) else {
            return
        }
        let size = CGSize(width: 320, height: 480)
        
        adView = VisxAdView(adUnit: adId, adViewDelegate: self, adSize: size, interstitial: true, identifier: "")
        adView?.isMediationAdView = true
        adView?.load()
    }

    public func present(from viewController: UIViewController) {
        adView?.showInterstitialFromViewController(controller: viewController)
    }
    
    public static func adapterVersion() -> GADVersionNumber {
        VISXCustomEventUtils.adAdapterVersionNumber()
    }

    public static func adSDKVersion() -> GADVersionNumber {
        VISXCustomEventUtils.adSDKVersionNumber()
    }

    public static func networkExtrasClass() -> GADAdNetworkExtras.Type? {
        nil
    }
}

extension VISXMediationAdapterInterstitialGAD: VisxAdViewDelegate {
    public func viewControllerForPresentingVisxAdView() -> UIViewController {
        UIViewController()
    }

    public func visxAdViewDidInitialize(visxAdView: VisxAdView, effect: VisxPlacementEffect) {
        delegate = interstitialCompletionHandler?(self, nil)
    }

    public func visxAdFailedWithError(visxAdView: VisxAdView, message: String, code: Int) {
        let error = NSError(domain: "", code: code, userInfo: [NSLocalizedDescriptionKey: message])
        _ = interstitialCompletionHandler?(nil, error)
    }
}
