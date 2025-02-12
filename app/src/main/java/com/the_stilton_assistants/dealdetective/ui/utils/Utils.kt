package com.the_stilton_assistants.dealdetective.ui.utils

import com.the_stilton_assistants.dealdetective.util.WifiStatus

fun isWifiAvailable(wifiStatus: WifiStatus.Status): Boolean {
    return wifiStatus == WifiStatus.Status.Available
}