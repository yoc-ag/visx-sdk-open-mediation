//
//  VISXMediationAdapterBannerGAD.swift
//  YOC Showcase
//
//  Created by Stefan Markovic on 17.8.21.
//  Copyright © 2021 YOC AG. All rights reserved.
//

import Foundation
import GoogleMobileAds
import VisxSDK

@objc(VISXMediationAdapterBannerGAD)
public class VISXMediationAdapterBannerGAD: NSObject, GADMediationAdapter, GADMediationBannerAd {

    public var view: UIView
    private var adView: VisxAdView?
    private var size: CGSize = .zero
    private var bannerCompletionHandler: GADMediationBannerLoadCompletionHandler?
    private weak var delegate: GADMediationBannerAdEventDelegate?

    public required override init() {
        view = UIView()
    }

    public func loadBanner(for adConfiguration: GADMediationBannerAdConfiguration,
                           completionHandler: @escaping GADMediationBannerLoadCompletionHandler) {
        bannerCompletionHandler = completionHandler
        guard let adId = VISXCustomEventUtils.adId(from: adConfiguration),
              let adSize = VISXCustomEventUtils.adSize(from: adConfiguration) else {
                  return
              }
        adView = VisxAdView(adUnit: adId, adViewDelegate: self, adSize: adSize, fixedSize: true, identifier: "")
        size = adView?.frame.size ?? .zero
        adView?.isMediationAdView = true
        adView?.load()
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

extension VISXMediationAdapterBannerGAD: VisxAdViewDelegate {
    public func viewControllerForPresentingVisxAdView() -> UIViewController {
        UIViewController()
    }

    public func visxAdViewDidInitialize(visxAdView: VisxAdView, effect: VisxPlacementEffect) {
        sendAdView(visxAdView)
    }

    public func visxAdViewEffectChange(visxAdView: VisxAdView, effect: VisxPlacementEffect) {
        sendAdView(visxAdView)
    }

    public func visxAdViewSizeChange(visxAdView: VisxAdView, width: CGFloat, height: CGFloat) {
        sendAdView(visxAdView)
    }

    public func visxAdFailedWithError(visxAdView: VisxAdView, message: String, code: Int) {
        let error = NSError(domain: "", code: code, userInfo: [NSLocalizedDescriptionKey: message])
        _ = bannerCompletionHandler?(nil, error)
    }

    fileprivate func sendAdView(_ visxAdView: VisxAdView) {
        if !size.equalTo(visxAdView.frame.size) {
            size = visxAdView.frame.size
            view = visxAdView
            delegate = bannerCompletionHandler?(self, nil)
            delegate?.reportImpression()
        }
    }
}
