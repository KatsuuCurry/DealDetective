package com.the_stilton_assistants.dealdetective.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.the_stilton_assistants.dealdetective.ui.navigation.LoginRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.SettingsRoute
import com.the_stilton_assistants.dealdetective.ui.utils.appContainer
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
import com.the_stilton_assistants.dealdetective.ui.utils.isWifiAvailable
import com.the_stilton_assistants.dealdetective.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier,
    navLambda: (ScreenRoute) -> Unit,
    viewModel: AccountViewModel,
) {
    val wifiStatusState by appContainer().wifiStatusState.collectAsStateWithLifecycle()
    val enabled = handleOperationState(
        viewModel = viewModel,
        onSuccess = {
            navLambda(AccountRoute)
            viewModel.resetOperation()
        }
    )
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Registrati",
                navLambdaRoute = SettingsRoute,
                navLambda = navLambda,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        val columnModifier = getColumnModifier(modifier, innerPadding)
        Column(
            modifier = columnModifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = modifier
                    .size(182.dp)
                    .padding(16.dp),
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
            )

            Text(
                modifier = modifier
                    .padding(16.dp),
                text = "Registrati",
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
                        text = "Email",
                    )
                },
                placeholder = {
                    Text(
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
                        text = "Password",
                    )
                },
                placeholder = {
                    Text(
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

            var confirmPassword by rememberSaveable { mutableStateOf("") }
            var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
            TextField(
                modifier = modifier.padding(16.dp),
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = {
                    Text(
                        text = "Conferma Password",
                    )
                },
                placeholder = {
                    Text(
                        text = "Conferma Password",
                    )
                },
                trailingIcon = {
                    val image = if (confirmPasswordVisible) R.drawable.visibility else R.drawable.visibility_off
                    IconButton(
                        modifier = modifier,
                        onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                    ) {
                        Icon(
                            modifier = modifier,
                            painter = painterResource(id = image),
                            contentDescription = "Password Visibility",
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, autoCorrectEnabled = false),
                singleLine = true,
                enabled = enabled,
            )

            if (
                password.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                password != confirmPassword
            ) {
                Text(
                    modifier = modifier.padding(16.dp),
                    text = "Le password non corrispondono",
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Button(
                modifier = modifier.padding(16.dp),
                onClick = {
                    passwordVisible = false
                    confirmPasswordVisible = false
                    viewModel.signUp(email, password)
                },
                enabled = enabled && email.isNotEmpty() && password.isNotEmpty()
                        && password == confirmPassword && isWifiAvailable(wifiStatusState),
            ) {
                Text(
                    text = "Registrati",
                )
            }

            Text(
                modifier = modifier
                    .padding(16.dp)
                    .clickable(
                        enabled = enabled,
                        onClick = {
                            passwordVisible = false
                            confirmPasswordVisible = false
                            navLambda(LoginRoute)
                        }),
                text = buildAnnotatedString {
                    append("Hai un account? ")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("Accedi Ora")
                    }
                },
            )

            if (!enabled) {
                Text(
                    text = "Caricamento...",
                )
                CircularProgressIndicator(
                    modifier = modifier.size(32.dp),
                )
            }
        }
    }
}
