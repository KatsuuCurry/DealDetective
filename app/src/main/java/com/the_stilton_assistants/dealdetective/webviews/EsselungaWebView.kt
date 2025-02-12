package com.the_stilton_assistants.dealdetective.webviews

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.the_stilton_assistants.dealdetective.model.StoreId

class EsselungaWebView(
    private val successCallback: (StoreId, String) -> Unit,
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
        return !(request.url.toString().startsWith("https://www.esselunga.it/it-it/promozioni")
                || request.url.toString().startsWith("https://www.esselunga.it/it-it/negozi")
                || request.url.toString().startsWith("https://www.esselunga.it/content/istituzionale35/it/it/promozioni"))
    }

    override fun onPageFinished(view: WebView, url: String) {
        if (url.startsWith("https://www.esselunga.it/it-it/promozioni/volantini")) {
            val check = url.removePrefix("https://www.esselunga.it/it-it/promozioni/volantini")
                .removeSuffix(".html")
            if (check.length == 3) {
                super.onPageFinished(view, url)
                return
            }

            successCallback(StoreId.ESSELUNGA, url)
        } else
            super.onPageFinished(view, url)
    }
}
