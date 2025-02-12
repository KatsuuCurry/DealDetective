package com.koalas.trackmybudget.ui.utils

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getScrollBehaviorAndModifier(
    modifier: Modifier = Modifier,
) : Pair<TopAppBarScrollBehavior?, Modifier> {
    val scrollBehavior: TopAppBarScrollBehavior? =
        if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) {
        TopAppBarDefaults.enterAlwaysScrollBehavior()
    } else {
        null
    }
    val scaffoldModifier = if (scrollBehavior != null) {
        modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    } else {
        modifier
    }
    return Pair(scrollBehavior, scaffoldModifier)
}

@Composable
fun getColumnModifier(
    modifier: Modifier,
    innerPadding: PaddingValues,
): Modifier {
    return if (LocalConfiguration.current.orientation == ORIENTATION_LANDSCAPE) {
        modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
    } else {
        modifier.padding(innerPadding)
    }
}
