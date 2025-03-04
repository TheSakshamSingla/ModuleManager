package com.saksham.modulemanager.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.saksham.modulemanager.ModuleManagerApp
import com.saksham.modulemanager.data.model.ModuleType
import com.saksham.modulemanager.data.model.Theme
import com.saksham.modulemanager.data.model.UpdateCheckInterval
import com.saksham.modulemanager.ui.components.SettingsSwitchItem
import com.saksham.modulemanager.ui.components.SettingsSelectItem
import kotlinx.coroutines.launch

/**
 * Screen for app settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel {
        val context = LocalContext.current
        val app = context.applicationContext as ModuleManagerApp
        SettingsViewModel(app.settingsRepository)
    }
) {
    val settings by viewModel.settings.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { paddingValues ->
        settings?.let { appSettings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                SettingsSection(title = "Appearance") {
                    SettingsSelectItem(
                        title = "Theme",
                        selectedOption = appSettings.theme.name,
                        options = Theme.values().map { it.name },
                        onOptionSelected = { selected ->
                            val theme = Theme.valueOf(selected)
                            coroutineScope.launch {
                                viewModel.updateTheme(theme)
                            }
                        }
                    )
                    
                    SettingsSwitchItem(
                        title = "Dark AMOLED Theme",
                        description = "Use pure black background in dark mode",
                        checked = appSettings.enableDarkAmoled,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                viewModel.updateDarkAmoled(checked)
                            }
                        }
                    )
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsSection(title = "Updates") {
                    SettingsSwitchItem(
                        title = "Check for Updates Automatically",
                        description = "Periodically check for module updates",
                        checked = appSettings.checkForUpdatesAutomatically,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                viewModel.updateAutomaticUpdateCheck(checked)
                            }
                        }
                    )
                    
                    if (appSettings.checkForUpdatesAutomatically) {
                        SettingsSelectItem(
                            title = "Update Check Interval",
                            selectedOption = appSettings.updateCheckInterval.name,
                            options = UpdateCheckInterval.values().map { it.name },
                            onOptionSelected = { selected ->
                                val interval = UpdateCheckInterval.valueOf(selected)
                                coroutineScope.launch {
                                    viewModel.updateUpdateCheckInterval(interval)
                                }
                            }
                        )
                    }
                    
                    SettingsSwitchItem(
                        title = "Show Notifications for Updates",
                        description = "Get notified when updates are available",
                        checked = appSettings.showNotificationsForUpdates,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                viewModel.updateShowNotificationsForUpdates(checked)
                            }
                        }
                    )
                    
                    SettingsSwitchItem(
                        title = "Show Beta Releases",
                        description = "Include pre-release versions in updates",
                        checked = appSettings.showBetaReleases,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                viewModel.updateShowBetaReleases(checked)
                            }
                        }
                    )
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsSection(title = "Modules") {
                    SettingsSelectItem(
                        title = "Preferred Module Type",
                        selectedOption = appSettings.preferredModuleType?.name ?: "None",
                        options = listOf("None") + ModuleType.values().map { it.name },
                        onOptionSelected = { selected ->
                            val type = if (selected == "None") null else ModuleType.valueOf(selected)
                            coroutineScope.launch {
                                viewModel.updatePreferredModuleType(type)
                            }
                        }
                    )
                    
                    SettingsSwitchItem(
                        title = "Auto Backup Modules",
                        description = "Automatically backup modules before updating",
                        checked = appSettings.autoBackupModules,
                        onCheckedChange = { checked ->
                            coroutineScope.launch {
                                viewModel.updateAutoBackupModules(checked)
                            }
                        }
                    )
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsSection(title = "About") {
                    SettingsItem(
                        title = "Version",
                        description = "1.0.0"
                    )
                    
                    SettingsItem(
                        title = "Developer",
                        description = "Saksham Singla"
                    )
                    
                    SettingsItem(
                        title = "License",
                        description = "Open Source - MIT License"
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = androidx.compose.material3.MaterialTheme.typography.titleSmall
        )
        Text(
            text = description,
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}
