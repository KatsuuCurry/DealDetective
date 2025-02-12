package com.the_stilton_assistants.dealdetective.ui.account

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.navigation.AccountRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
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
                title = "Modifica Dati Account",
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
            var image: Uri? by rememberSaveable { mutableStateOf(account.photoUrl) }
            val imagePicker = rememberImagePicker { uri, error ->
                if (error == ImagePickerError.NoError) {
                    if (uri == null) {
                        return@rememberImagePicker
                    }
                    image = uri
                    notificationBubbleHandler.displayBubble(
                        message = "Immagine selezionata",
                    )
                } else {
                    notificationBubbleHandler.displayBubble(
                        message = "Errore nel selezionare l'immagine: $error",
                    )
                }
            }

            val painter = rememberAsyncImagePainter(image)
            val imgState by painter.state.collectAsStateWithLifecycle()
            if (imgState is AsyncImagePainter.State.Success) {
                Image(
                    painter = imgState.painter!!,
                    contentDescription = null,
                    modifier = modifier
                        .size(164.dp)
                        .clip(CircleShape)
                        .padding(16.dp)
                        .clickable(enabled = enabled) {
                            imagePicker.pickImage()
                        },
                )
            } else if (imgState is AsyncImagePainter.State.Loading) {
                Icon(
                    painter = painterResource(id = R.drawable.user_box),
                    contentDescription = null,
                    modifier = modifier
                        .size(164.dp)
                        .clip(CircleShape)
                        .padding(16.dp)
                        .clickable(enabled = enabled) {
                            imagePicker.pickImage()
                        },
                )
            } else if (imgState is AsyncImagePainter.State.Error) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = modifier
                        .size(164.dp)
                        .clip(CircleShape)
                        .padding(16.dp)
                        .clickable(enabled = enabled) {
                            imagePicker.pickImage()
                        },
                )
            } else {
                CircularProgressIndicator(
                    modifier = modifier
                        .size(164.dp)
                        .padding(16.dp),
                )
            }

            Text(
                modifier = modifier.padding(16.dp),
                text = "Clicca sull'immagine per cambiarla",
            )

            Button(
                modifier = modifier.padding(16.dp),
                onClick = {
                    image = null
                    imagePicker.clearImage()
                },
                enabled = enabled,
            ) {
                Text(
                    modifier = modifier,
                    text = "Rimuovi Immagine",
                )
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
                    viewModel.updateAccount(
                        displayName = displayName,
                        image = image
                    )
                },
                enabled = enabled,
            ) {
                Text(
                    modifier = modifier,
                    text = "Aggiorna i Miei Dati",
                )
            }
        }
    }
}
