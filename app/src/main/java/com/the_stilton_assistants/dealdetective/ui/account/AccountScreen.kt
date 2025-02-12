package com.the_stilton_assistants.dealdetective.ui.account

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.R
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.navigation.AccountRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.EditAccountRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.SettingsRoute
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
import com.the_stilton_assistants.dealdetective.viewmodel.AccountDatabaseViewModel
import com.the_stilton_assistants.dealdetective.viewmodel.AccountUiState
import com.the_stilton_assistants.dealdetective.viewmodel.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    modifier: Modifier,
    navLambda: (ScreenRoute) -> Unit,
    accountViewModel: AccountViewModel,
    accountDBViewModel: AccountDatabaseViewModel = viewModel(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity,
        factory = AccountDatabaseViewModel.Factory,
    ),
) {
    var signingOut by rememberSaveable { mutableStateOf(false) }
    val enabledAcc = handleOperationState(
        viewModel = accountViewModel,
        onSuccess = {
            if (signingOut) {
                signingOut = false
                navLambda(AccountRoute)
            }
            accountViewModel.resetOperation()
        }
    )
    val enabledAccDB = handleOperationState(
        viewModel = accountDBViewModel,
    )
    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Account",
                navLambdaRoute = SettingsRoute,
                navLambda = navLambda,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        val columnModifier = getColumnModifier(modifier, innerPadding)
        val appUiState by accountViewModel.accountUiState.collectAsStateWithLifecycle()
        if (appUiState !is AccountUiState.User) {
            LoadingComponent(
                modifier = modifier.padding(innerPadding),
            )
            return@Scaffold
        }
        Column(
            modifier = columnModifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val user = (appUiState as AccountUiState.User).user
            val defaultPainter = painterResource(id = R.drawable.user_box)
            val errorPainter = rememberVectorPainter(Icons.Default.Clear)
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = modifier
                    .size(164.dp)
                    .clip(CircleShape)
                    .padding(16.dp),
                error = errorPainter,
                fallback = defaultPainter,
            )

            val displayName = user.displayName ?: "Non specificato"

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "Nome: $displayName",
                    style = MaterialTheme.typography.titleMedium,
                )
                IconButton(
                    modifier = Modifier,
                    onClick = {
                        navLambda(EditAccountRoute)
                    },
                    enabled = enabledAcc && enabledAccDB,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Account",
                    )
                }
            }

            Text(
                modifier = modifier.padding(8.dp),
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.titleMedium,
            )

            HorizontalDivider(
                thickness = 2.dp,
                modifier = modifier.padding(8.dp),
            )
            Text(
                modifier = modifier.padding(4.dp),
                text = "Dati sul Cloud",
                style = MaterialTheme.typography.titleMedium,
            )
            HorizontalDivider(
                thickness = 2.dp,
                modifier = modifier.padding(8.dp),
            )

            Button(
                modifier = modifier.padding(8.dp),
                onClick = {
                    accountDBViewModel.uploadUserData()
                },
                enabled = enabledAcc && enabledAccDB,
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Send Data to Cloud",
                )
                Spacer(modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    modifier = modifier,
                    text = "Invia Dati sul Cloud",
                )
            }

            Button(
                modifier = modifier.padding(8.dp),
                onClick = {
                    accountDBViewModel.retrieveUserData()
                },
                enabled = enabledAcc && enabledAccDB,
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Retrieve Data from Cloud",
                )
                Spacer(modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    modifier = modifier,
                    text = "Scarica Dati dal Cloud",
                )
            }

            HorizontalDivider(
                thickness = 2.dp,
                modifier = modifier.padding(8.dp),
            )
            Text(
                modifier = modifier.padding(4.dp),
                text = "Gestione Account",
                style = MaterialTheme.typography.titleMedium,
            )
            HorizontalDivider(
                thickness = 2.dp,
                modifier = modifier.padding(8.dp),
            )

            if (!user.isEmailVerified) {
                Button(
                    modifier = modifier
                        .padding(8.dp),
                    onClick = {
                        accountViewModel.sendEmailVerification()
                    },
                    enabled = enabledAcc && enabledAccDB,
                ) {
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verify Email",
                    )
                    Spacer(modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        modifier = modifier,
                        text = "Verifica Email",
                    )
                }
            }

            Button(
                modifier = modifier.padding(8.dp),
                onClick = {
                    accountViewModel.sendPasswordResetEmail(user.email!!)
                },
                enabled = enabledAcc && enabledAccDB,
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.Default.Build,
                    contentDescription = "Account",
                )
                Spacer(modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    modifier = modifier,
                    text = "Reimposta Password",
                )
            }

            Button(
                modifier = modifier.padding(8.dp),
                onClick = {
                    signingOut = true
                    accountViewModel.signOut()
                },
                enabled = enabledAcc && enabledAccDB,
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = Icons.AutoMirrored.Default.ExitToApp,
                    contentDescription = "Exit Account",
                )
                Spacer(modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    modifier = modifier,
                    text = "Disconnettiti",
                )
            }
        }
    }
}
