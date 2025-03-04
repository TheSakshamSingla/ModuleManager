package com.saksham.modulemanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.saksham.modulemanager.ModuleManagerApp
import com.saksham.modulemanager.data.model.Module
import kotlinx.coroutines.launch
import dev.jeziellago.compose.markdowntext.MarkdownText

/**
 * Screen for module details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    navController: NavController,
    moduleId: String,
    viewModel: ModuleDetailViewModel = viewModel {
        val context = LocalContext.current
        val app = context.applicationContext as ModuleManagerApp
        ModuleDetailViewModel(app.moduleRepository, moduleId)
    }
) {
    val module by viewModel.module
    val isLoading by viewModel.isLoading
    val coroutineScope = rememberCoroutineScope()
    
    var showUninstallDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(moduleId) {
        viewModel.loadModule()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(module?.name ?: "Module Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (module != null) {
                        IconButton(onClick = { showUninstallDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Uninstall"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (module == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Module not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Module header
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = module!!.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "v${module!!.version} by ${module!!.author}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            Switch(
                                checked = module!!.isEnabled,
                                onCheckedChange = { isEnabled ->
                                    coroutineScope.launch {
                                        viewModel.toggleModule(isEnabled)
                                    }
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = module!!.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        if (module!!.hasUpdate) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Update,
                                    contentDescription = "Update available",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Update available: v${module!!.newVersion}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            viewModel.updateModule()
                                        }
                                    }
                                ) {
                                    Text("Update")
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Module details
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DetailItem(
                            label = "Module ID",
                            value = module!!.id
                        )
                        
                        DetailItem(
                            label = "Type",
                            value = module!!.type.name
                        )
                        
                        DetailItem(
                            label = "Version Code",
                            value = module!!.versionCode.toString()
                        )
                        
                        DetailItem(
                            label = "Install Date",
                            value = module!!.installDate.toString()
                        )
                        
                        module!!.updateUrl?.let { url ->
                            DetailItem(
                                label = "Update URL",
                                value = url
                            )
                        }
                        
                        module!!.localPath?.let { path ->
                            DetailItem(
                                label = "Local Path",
                                value = path
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Changelog
                viewModel.changelog?.let { changelog ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Changelog",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            MarkdownText(
                                markdown = changelog,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
        
        if (showUninstallDialog) {
            AlertDialog(
                onDismissRequest = { showUninstallDialog = false },
                title = { Text("Uninstall Module") },
                text = { Text("Are you sure you want to uninstall this module? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.uninstallModule()
                                showUninstallDialog = false
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Uninstall")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showUninstallDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
