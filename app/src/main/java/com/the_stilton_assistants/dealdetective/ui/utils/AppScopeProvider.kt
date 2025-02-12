package com.the_stilton_assistants.dealdetective.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.the_stilton_assistants.dealdetective.DealDetectiveApplication
import kotlinx.coroutines.CoroutineScope

@Composable
fun appScope() : CoroutineScope {
    val app = LocalContext.current.applicationContext as DealDetectiveApplication
    return app.applicationScope
}
