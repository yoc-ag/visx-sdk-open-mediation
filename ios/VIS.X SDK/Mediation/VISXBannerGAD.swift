//
//  VisxBannerGAD.swift
//  YOC Showcase
//
//  Created by Stefan Markovic on 20.8.21.
//  Copyright © 2021 YOC AG. All rights reserved.
//

import Foundation
import GoogleMobileAds
import VisxSDK

@objc(VISXBannerGAD)
public class VISXBannerGAD: NSObject, GADBannerViewDelegate, LoadMediationDelegate {

    private var visxAdView: VisxAdView?

    public func loadMediationAd(_ mediation: Mediation, adView: VisxAdView, customTargeting: [String : String]?) {
        let bannerView = GAMBannerView()
        visxAdView = adView
        guard let controller = VisxMediationUtils.topMostController() else { return }
        if let size = mediation.sizes {
            bannerView.validAdSizes = getValidSizes(sizeArray: size)
        }
        visxAdView?.addSubview(bannerView)
        bannerView.adUnitID = mediation.adunit
        bannerView.rootViewController = controller
        bannerView.delegate = self
        let request = GAMRequest()
        if let params = customTargeting {
            request.customTargeting = params
        }
        bannerView.load(request)
    }

    public func bannerViewDidReceiveAd(_ bannerView: GADBannerView) {
        visxAdView?.adViewDidReceiveAd(bannerView: bannerView)
    }

    public func bannerView(_ bannerView: GADBannerView, didFailToReceiveAdWithError error: Error) {
        visxAdView?.didFailToReceiveAdWithError(bannerView: bannerView, error: error as NSError)
    }

    private func getValidSizes(sizeArray: [[Int]]) -> [NSValue] {
        var gadSize = GADAdSize()
        var validSizes = [NSValue]()
        for array in sizeArray {
            if array.count == 2 {
                let width = array[0]
                let height = array[1]
                switch height {
                case 50:
                    gadSize = GADAdSizeFromCGSize(CGSize(width: 320, height: 50))
                case 100:
                    gadSize = GADAdSizeFromCGSize(CGSize(width: 320, height: 100))
                case 250:
                    gadSize = GADAdSizeFromCGSize(CGSize(width: 300, height: 250))
                case 60:
                    gadSize = GADAdSizeFromCGSize(CGSize(width: 468, height: 60))
                case 90:
                    gadSize = GADAdSizeFromCGSize(CGSize(width: 728, height: 90))
                case 600:
                    gadSize = GADAdSizeFromCGSize(CGSize(width: 120, height: 600))
                default:
                    gadSize = GADAdSizeFromCGSize(CGSize(width: width, height: height))
                }
                validSizes.append(NSValueFromGADAdSize(gadSize))
            }
        }
        return validSizes
    }
}
