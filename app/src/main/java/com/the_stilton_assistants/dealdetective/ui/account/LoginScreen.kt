package com.the_stilton_assistants.dealdetective.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.navigation.AccountRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.RegisterRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.SettingsRoute
import com.the_stilton_assistants.dealdetective.ui.utils.appContainer
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
import com.the_stilton_assistants.dealdetective.ui.utils.isWifiAvailable
import com.the_stilton_assistants.dealdetective.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier,
    navLambda: (ScreenRoute) -> Unit,
    viewModel: AccountViewModel,
) {
    var sendingPasswordRequest by rememberSaveable { mutableStateOf(false) }
    var sendPasswordReset by rememberSaveable { mutableStateOf(false) }
    val enabled = handleOperationState(
        viewModel = viewModel,
        onSuccess = {
            if (sendingPasswordRequest) {
                sendingPasswordRequest = false
                sendPasswordReset = false
            } else {
                navLambda(AccountRoute)
            }
            viewModel.resetOperation()

        }
    )
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Accedi",
                navLambdaRoute = SettingsRoute,
                navLambda = navLambda,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        val wifiStatusState by appContainer().wifiStatusState.collectAsStateWithLifecycle()
        val columnModifier = getColumnModifier(modifier, innerPadding)
        Column(
            modifier = columnModifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = modifier.size(182.dp).padding(16.dp),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
            )

            Text(
                modifier = modifier
                    .padding(16.dp),
                text = "Accedi",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            )

            var email by rememberSaveable { mutableStateOf("") }
            TextField(
                modifier = modifier.padding(16.dp),
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        modifier = modifier,
                        text = "Email",
                    )
                },
                placeholder = {
                    Text(
                        modifier = modifier,
                        text = "Email",
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false),
                singleLine = true,
                enabled = enabled,
            )
            var password by rememberSaveable { mutableStateOf("") }
            var passwordVisible by rememberSaveable { mutableStateOf(false) }
            TextField(
                modifier = modifier.padding(16.dp),
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        modifier = modifier,
                        text = "Password",
                    )
                },
                placeholder = {
                    Text(
                        modifier = modifier,
                        text = "Password",
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
                    IconButton(
                        modifier = modifier,
                        onClick = { passwordVisible = !passwordVisible },
                    ) {
                        Icon(
                            modifier = modifier,
                            painter = painterResource(id = image),
                            contentDescription = "Password Visibility",
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                singleLine = true,
                enabled = enabled,
            )

            Text(
                modifier = modifier
                    .padding(16.dp)
                    .clickable(
                        enabled = enabled,
                        onClick = {
                            passwordVisible = false
                            sendPasswordReset = true
                    }),
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("Hai dimenticato la password?")
                    }
                },
            )

            Button(
                modifier = modifier.padding(16.dp),
                onClick = {
                    passwordVisible = false
                    viewModel.signIn(email, password)
                },
                enabled = enabled && email.isNotEmpty() && password.isNotEmpty()
                        && isWifiAvailable(wifiStatusState),
            ) {
                Text(
                    text = "Accedi",
                )
            }

            Text(
                modifier = modifier
                    .padding(16.dp)
                    .clickable(
                        enabled = enabled,
                        onClick = {
                            passwordVisible = false
                            navLambda(RegisterRoute)
                    }),
                text = buildAnnotatedString {
                    append("Non hai un account? ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("Registrati Ora")
                    }
                },
            )

            if (!enabled) {
                Text(
                    modifier = modifier.padding(16.dp),
                    text = "Caricamento...",
                )
                CircularProgressIndicator(
                    modifier = modifier.padding(16.dp).size(32.dp),
                )
            }
            if (sendPasswordReset) {
                BasicAlertDialog (
                    modifier = modifier,
                    onDismissRequest = { sendPasswordReset = false },
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
                                text = "Inserisci la tua email",
                            )
                            var emailReset by rememberSaveable { mutableStateOf("") }
                            TextField(
                                modifier = modifier.padding(vertical = 8.dp),
                                value = emailReset,
                                onValueChange = { emailReset = it },
                                label = {
                                    Text(
                                        modifier = modifier,
                                        text = "Email",
                                    )
                                },
                                placeholder = {
                                    Text(
                                        modifier = modifier,
                                        text = "Email",
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, autoCorrectEnabled = false),
                                singleLine = true,
                                enabled = enabled,
                            )
                            Button(
                                modifier = modifier.padding(vertical = 8.dp),
                                onClick = {
                                    sendingPasswordRequest = true
                                    viewModel.sendPasswordResetEmail(emailReset)
                                },
                                enabled = enabled && emailReset.isNotBlank() && isWifiAvailable(wifiStatusState),
                            ) {
                                Text(
                                    modifier = modifier,
                                    text = "Resettare la password",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
