package com.the_stilton_assistants.dealdetective.ui.stores

import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.viewmodel.OperationUiState
import com.the_stilton_assistants.dealdetective.viewmodel.StoresViewModel
import com.the_stilton_assistants.dealdetective.webviews.EsselungaWebView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsselungaScreen(
    modifier: Modifier = Modifier,
    navBackLambda: () -> Boolean,
    viewModel: StoresViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current!! as ComponentActivity,
        factory = StoresViewModel.Factory
    ),
) {
    var alertOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        alertOpen = true
    }

    val operationUiState by viewModel.operationUiState.collectAsStateWithLifecycle()
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)

    if (alertOpen) {
        BasicAlertDialog(
            modifier = modifier,
            onDismissRequest = { alertOpen = false },
        ) {
            Surface(
                modifier = modifier.wrapContentWidth().wrapContentHeight(),
                shape = AlertDialogDefaults.shape,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = modifier.padding(vertical = 8.dp),
                        text = "Seleziona il volantino del tuo negozio",
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        modifier = modifier.padding(vertical = 8.dp),
                        onClick = {
                            alertOpen = false
                        }
                    ) {
                        Text(
                            modifier = modifier,
                            text = "OK",
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Seleziona il tuo negozio",
                navBackLambda = navBackLambda,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val columnModifier = getColumnModifier(modifier, innerPadding)
        when (operationUiState) {
            is OperationUiState.Loading -> {
                LoadingComponent(
                    modifier = columnModifier,
                )
                return@Scaffold
            }
            is OperationUiState.Error -> {
                ErrorText(
                    modifier = columnModifier,
                    text = (operationUiState as OperationUiState.Error).message
                )
            }
            is OperationUiState.Success -> {
                Text(
                    modifier = columnModifier
                        .padding(16.dp)
                        .fillMaxSize(),
                    text = (operationUiState as OperationUiState.Success).message,
                    textAlign = TextAlign.Center,
                )
            }
            else -> {
                AndroidView(
                    modifier = columnModifier
                        .fillMaxSize(),
                    factory = { context ->
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
                                successCallback = { storesId, url ->
                                    viewModel.insertStore(
                                        storeId = storesId,
                                        url = url,
                                    )
                                },
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
        }
    }
}
