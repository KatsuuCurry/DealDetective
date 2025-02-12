package com.the_stilton_assistants.dealdetective.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koalas.trackmybudget.ui.utils.getColumnModifier
import com.koalas.trackmybudget.ui.utils.getScrollBehaviorAndModifier
import com.the_stilton_assistants.dealdetective.model.ImagesSize
import com.the_stilton_assistants.dealdetective.model.NotificationFilter
import com.the_stilton_assistants.dealdetective.ui.common.ErrorText
import com.the_stilton_assistants.dealdetective.ui.common.LoadingComponent
import com.the_stilton_assistants.dealdetective.ui.common.NavigationBottomBar
import com.the_stilton_assistants.dealdetective.ui.common.TopBar
import com.the_stilton_assistants.dealdetective.ui.navigation.AccountRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.ScreenRoute
import com.the_stilton_assistants.dealdetective.ui.navigation.SettingsRoute
import com.the_stilton_assistants.dealdetective.ui.utils.handleOperationState
import com.the_stilton_assistants.dealdetective.viewmodel.AccountViewModel
import com.the_stilton_assistants.dealdetective.viewmodel.SettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navLambda: (ScreenRoute) -> Unit,
    viewModel: AccountViewModel,
) {
    val enabled = handleOperationState(
        viewModel = viewModel,
    )

    val (scrollBehavior, scaffoldModifier) = getScrollBehaviorAndModifier(modifier)
    Scaffold(
        modifier = scaffoldModifier,
        topBar = {
            TopBar(
                modifier = modifier,
                title = "Impostazioni",
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            NavigationBottomBar(
                modifier = modifier,
                navLambda = navLambda,
                currentDestination = SettingsRoute,
            )
        },
    ) { innerPadding ->
        val columnModifier = getColumnModifier(modifier, innerPadding)
        Column(
            modifier = columnModifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable(
                        enabled = enabled,
                        onClick = { navLambda(AccountRoute) }
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = modifier.size(48.dp),
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Account",
                )
                Text(
                    modifier = modifier.padding(start = 16.dp),
                    text = "Account",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

            if (settingsUiState is SettingsUiState.Loading) {
                LoadingComponent(
                    modifier = modifier.padding(16.dp),
                )
                return@Column
            }

            if (settingsUiState is SettingsUiState.Error) {
                ErrorText(
                    modifier = modifier.padding(16.dp),
                    text = (settingsUiState as SettingsUiState.Error).message,
                )
                return@Column
            }

            val settings = (settingsUiState as SettingsUiState.Display).settings

            HorizontalDivider(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                thickness = 2.dp,
            )
            Text(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                text = "Grandezza Immagini",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
            )

            val options = listOf(
                "Piccole" to ImagesSize.SMALL,
                "Medie" to ImagesSize.MEDIUM,
                "Grandi" to ImagesSize.LARGE,
            )

            SingleChoiceSegmentedButtonRow(
                modifier = modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                options.forEachIndexed { index, (label, type) ->
                    SegmentedButton(
                        modifier = modifier,
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {
                            viewModel.updateSettings(imagesSize = type)
                        },
                        selected = settings.imagesSize == type,
                        label = { Text(label) }
                    )
                }
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = modifier.padding(16.dp),
                    text = "Nome Prodotti in Grassetto",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
                Checkbox(
                    checked = settings.boldText,
                    onCheckedChange = {
                        viewModel.updateSettings(boldText = it)
                    },
                    modifier = modifier.padding(16.dp),
                    enabled = enabled,
                )
            }

            val notificationFilterOptions = mapOf<NotificationFilter, String>(
                NotificationFilter.ALL to "Tutti",
                NotificationFilter.IMPORTANT to "Importanti",
                NotificationFilter.ERROR_ONLY to "Solo Errori",
            )

            Text(
                modifier = modifier.fillMaxWidth().padding(16.dp),
                text = "Filtro Notifiche",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
            )
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = modifier.padding(16.dp),
                expanded = expanded,
                onExpandedChange = {
                    expanded = if (enabled) {
                        !expanded
                    } else {
                        false
                    }
                }
            ) {
                var value = notificationFilterOptions[settings.notificationFilter]
                if (value == null) {
                    value = "Errore!"
                }
                TextField(
                    value = value,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Selezione un'Opzione") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    modifier = modifier,
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    notificationFilterOptions.forEach { (type, label) ->
                        DropdownMenuItem(
                            modifier = modifier,
                            text = { Text(label) },
                            onClick = {
                                viewModel.updateSettings(notificationFilter = type)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
