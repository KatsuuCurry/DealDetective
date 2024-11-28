package com.example.dealdetective.ui

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.dealdetective.AppContainer
import com.example.dealdetective.repository.DataStoreOperation
import com.example.dealdetective.repository.EsselungaHandler
import kotlinx.coroutines.CoroutineScope

class EsselungaWebView(private val appScope: CoroutineScope, private val appContainer: AppContainer,
                       private val callback: (DataStoreOperation) -> Unit,
                       private val loadingCallback: () -> Unit) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
        println(request.url)
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
            loadingCallback()
            EsselungaHandler.saveNewStore(appScope, appContainer, url, callback)
        } else
            super.onPageFinished(view, url)
    }
}