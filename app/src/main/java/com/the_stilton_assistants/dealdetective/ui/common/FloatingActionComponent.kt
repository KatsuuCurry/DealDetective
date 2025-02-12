package com.the_stilton_assistants.dealdetective.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun FloatingActionComponent(
    modifier: Modifier = Modifier,
    lazyStaggeredGridState: LazyStaggeredGridState,
) {
    val isAtTop by remember {
        derivedStateOf { lazyStaggeredGridState.firstVisibleItemIndex == 0 &&
                lazyStaggeredGridState.firstVisibleItemScrollOffset == 0 }
    }
    if (!isAtTop) {
        val coroutineScope = rememberCoroutineScope()
        FloatingActionButton(
            modifier = modifier.padding(start = 4.dp),
            onClick = {
                coroutineScope.launch {
                    lazyStaggeredGridState.animateScrollToItem(0)
                }
            },
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Go to top")
        }
    }
}
