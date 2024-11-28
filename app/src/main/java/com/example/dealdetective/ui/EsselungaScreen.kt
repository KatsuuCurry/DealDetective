package com.example.dealdetective.ui

import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.dealdetective.DealDetectiveApplication
import com.example.dealdetective.repository.DataStoreOperation

enum class ScreenState {
    NONE,
    LOADING,
    SUCCESS,
    ERROR,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsselungaScreen(
    modifier: Modifier = Modifier,
    navBackLambda: () -> Boolean,
) {
    var status by rememberSaveable { mutableStateOf(ScreenState.NONE) }

    val callback = { state: DataStoreOperation ->
        status = when (state) {
            DataStoreOperation.SUCCESS -> {
                ScreenState.SUCCESS
            }

            else -> {
                ScreenState.ERROR
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("Esselunga")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navBackLambda()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        when (status) {
            ScreenState.NONE -> {
                AndroidView(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    factory = { context ->
                        val appScope =
                            (context.applicationContext as DealDetectiveApplication).applicationScope
                        val appContainer =
                            (context.applicationContext as DealDetectiveApplication).container
                        WebStorage.getInstance().deleteAllData()

                        CookieManager.getInstance().removeAllCookies(null)
                        CookieManager.getInstance().flush()
                        WebView(context).apply {
                            clearCache(true)
                            clearFormData()
                            clearHistory()
                            clearSslPreferences()
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            webViewClient = EsselungaWebView(
                                appScope = appScope,
                                appContainer = appContainer,
                                callback = callback,
                                loadingCallback = {
                                    status = ScreenState.LOADING
                                }
                            )
                            loadUrl("https://www.esselunga.it/it-it/negozi.html")
                        }
                    },
                    onRelease = { webView ->
                        // Perform cleanup
                        webView.clearCache(true)
                        webView.clearFormData()
                        webView.clearHistory()
                        webView.clearSslPreferences()
                        webView.destroy()
                    }
                )
            }

            else -> {
                Text(
                    modifier = modifier.padding(innerPadding),
                    text = "Loading...",
                )
            }
        }
    }
}