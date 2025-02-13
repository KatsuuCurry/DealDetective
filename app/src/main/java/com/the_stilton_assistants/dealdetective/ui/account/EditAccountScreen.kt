package com.the_stilton_assistants.dealdetective.ui.account

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.navigation.AccountRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.utils.appContainer
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
import com.the_stilton_assistants.dealdetective.ui.utils.isWifiAvailable
import com.the_stilton_assistants.dealdetective.util.IImagePicker.Companion.ImagePickerError
import com.the_stilton_assistants.dealdetective.util.rememberImagePicker
import com.the_stilton_assistants.dealdetective.util.rememberNotificationBubbleHandler
import com.the_stilton_assistants.dealdetective.viewmodel.AccountUiState
import com.the_stilton_assistants.dealdetective.viewmodel.AccountViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    viewModel: AccountViewModel,
) {
    val notificationBubbleHandler = rememberNotificationBubbleHandler()
    val enabled = handleOperationState(
        viewModel = viewModel,
        onSuccess = {
            navLambda(AccountRoute)
            viewModel.resetOperation()
        },
        notificationBubbleHandler = notificationBubbleHandler,
    )

    val accountUiState by viewModel.accountUiState.collectAsStateWithLifecycle()
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Modifica Account",
                navLambdaRoute = AccountRoute,
                navLambda = navLambda,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        val columnModifier = getColumnModifier(modifier, innerPadding)
        if (accountUiState is AccountUiState.Loading) {
            LoadingComponent(
                modifier = modifier.padding(innerPadding),
            )
            return@Scaffold
        }
        if (accountUiState is AccountUiState.Error) {
            ErrorText(
                modifier = modifier.padding(innerPadding),
                text = (accountUiState as AccountUiState.Error).message,
            )
            return@Scaffold
        }

        val account = (accountUiState as AccountUiState.User).user

        Column(
            modifier = columnModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val wifiStatusState by appContainer().wifiStatusState.collectAsStateWithLifecycle()
            var key by rememberSaveable { mutableStateOf(false) }
            var image: Uri? by rememberSaveable { mutableStateOf(account.photoUrl) }
            val imagePicker = rememberImagePicker { uri, error ->
                if (error == ImagePickerError.NoError) {
                    if (uri == null) {
                        return@rememberImagePicker
                    }
                    image = uri
                    key = !key
                    notificationBubbleHandler.displayBubble(
                        message = "Immagine selezionata",
                    )
                } else {
                    notificationBubbleHandler.displayBubble(
                        message = "Errore nel selezionare l'immagine: $error",
                    )
                }
            }

            key(key) {
                val defaultPainter = rememberVectorPainter(Icons.Default.AccountCircle)
                val errorPainter = rememberVectorPainter(Icons.Default.Clear)
                val loadingPainter = rememberVectorPainter(Icons.Default.Refresh)
                var isSuccess by rememberSaveable { mutableStateOf(false) }
                AsyncImage(
                    modifier = modifier
                        .size(182.dp)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .clickable {
                            imagePicker.pickImage()
                        },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = "Account Image",
                    placeholder = loadingPainter,
                    error = errorPainter,
                    fallback = defaultPainter,
                    colorFilter = if (isSuccess)
                        null
                    else
                        ColorFilter.tint(LocalContentColor.current),
                    onError = {
                        isSuccess = false
                    },
                    onSuccess = {
                        isSuccess = true
                    },
                    onLoading = {
                        isSuccess = false
                    },
                )
            }

            Text(
                modifier = modifier.padding(16.dp),
                text = "Clicca sull'immagine per cambiarla",
            )

            if (image != null) {
                Button(
                    modifier = modifier.padding(16.dp),
                    onClick = {
                        image = null
                    },
                    enabled = enabled,
                ) {
                    Text(
                        modifier = modifier,
                        text = "Rimuovi Immagine",
                    )
                }
            }

            Text(
                modifier = modifier.padding(16.dp),
                text = "Modifica Nome Utente",
            )

            var displayName by rememberSaveable { mutableStateOf(account.displayName ?: "") }
            TextField(
                modifier = modifier.padding(16.dp),
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Nome Utente") },
                singleLine = true,
                enabled = enabled,
                placeholder = { Text("Nome Utente") },
            )

            Button(
                modifier = modifier.padding(16.dp),
                onClick = {
                    if (image == null) {
                        imagePicker.clearImage()
                    }
                    viewModel.updateAccount(
                        displayName = displayName,
                        image = image
                    )
                },
                enabled = enabled && isWifiAvailable(wifiStatusState),
            ) {
                Text(
                    modifier = modifier,
                    text = "Aggiorna i Miei Dati",
                )
            }
        }
    }
}
