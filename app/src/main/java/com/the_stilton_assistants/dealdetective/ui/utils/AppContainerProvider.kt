package com.the_stilton_assistants.dealdetective.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.the_stilton_assistants.dealdetective.DealDetectiveApplication
import com.the_stilton_assistants.dealdetective.IAppContainer

@Composable
fun appContainer() : IAppContainer {
    val app = LocalContext.current.applicationContext as DealDetectiveApplication
    return app.appContainer
}
