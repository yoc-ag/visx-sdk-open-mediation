//
//  VISXCustomEventUtils.swift
//  YOC Showcase
//
//  Created by Stefan Markovic on 20.4.22.
//  Copyright © 2022 YOC AG. All rights reserved.
//

import Foundation
import VisxSDK
import GoogleMobileAds

enum Parameters: String {
    case adId = "auid="
    case size = "size="
}

class VISXCustomEventUtils {

    private static let parameter = "parameter"

    static func adId(from adConfiguration: GADMediationAdConfiguration) -> String? {
        paramsFromConfiguration(adConfiguration: adConfiguration, param: .adId)
    }

    static func adSize(from adConfiguration: GADMediationAdConfiguration) -> CGSize? {
        guard let params = adConfiguration.credentials.settings[parameter] as? String else {
            return nil
        }
        let visxSize = CGSize.zero
        if let sizeSubstring = params.range(of: Parameters.size.rawValue)?.upperBound {
            let sizeString = String(params[sizeSubstring...])
            guard let widthSubstring = sizeString.range(of: "x")?.upperBound else {
                return visxSize
            }
            guard let heightSubstring = sizeString.range(of: "x")?.lowerBound else {
                return visxSize
            }
            let height = CGFloat((sizeString[widthSubstring...] as NSString).floatValue)
            let width = CGFloat((sizeString[..<heightSubstring] as NSString).floatValue)
            return CGSize(width: width, height: height)
        }
        return visxSize
    }

    static func adSDKVersionNumber() -> GADVersionNumber {
        let versionNumber = VisxSDKManager.sharedInstance().SDKVersion().components(separatedBy: ".")
        return GADVersionNumber(majorVersion: Int(versionNumber[0]) ?? 0,
                                minorVersion: Int(versionNumber[1]) ?? 0,
                                patchVersion: Int(versionNumber[2]) ?? 0)
    }

    static func adAdapterVersionNumber() -> GADVersionNumber {
        GADVersionNumber(majorVersion: 3,
                         minorVersion: 0,
                         patchVersion: 0)
    }
    // Helper methods
    fileprivate class func slice(param: String, from: String, toRange: String) -> String {
        guard let rangeFrom = param.range(of: from)?.upperBound else {
            return ""
        }
        guard let rangeTo = param[rangeFrom...].range(of: toRange)?.lowerBound else {
            return ""
        }
        return String(param[rangeFrom..<rangeTo])
    }

    fileprivate class func paramsFromConfiguration(adConfiguration: GADMediationAdConfiguration, param: Parameters) -> String? {
        guard let params = adConfiguration.credentials.settings[parameter] as? String else {
            return nil
        }
        return slice(param: params, from: param.rawValue, toRange: ";")
    }
}
