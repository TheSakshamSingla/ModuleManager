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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.saksham.modulemanager.data.model.ModuleType
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

/**
 * Screen for installing a module
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleInstallScreen(
    navController: NavController,
    moduleId: String,
    viewModel: ModuleInstallViewModel = viewModel {
        val context = LocalContext.current
        val app = context.applicationContext as ModuleManagerApp
        ModuleInstallViewModel(app.moduleRepository, moduleId)
    }
) {
    val module by viewModel.module
    val isLoading by viewModel.isLoading
    val installProgress by viewModel.installProgress.collectAsState()
    val installState by viewModel.installState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    var selectedModuleType by remember { mutableStateOf<ModuleType?>(null) }
    
    LaunchedEffect(moduleId) {
        viewModel.loadModule()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Install Module") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
                        Text(
                            text = module!!.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "v${module!!.version} by ${module!!.author}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = module!!.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
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
                            label = "Version Code",
                            value = module!!.versionCode.toString()
                        )
                        
                        module!!.updateUrl?.let { url ->
                            DetailItem(
                                label = "Source",
                                value = url
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Module type selection
                if (module!!.supportedTypes.size > 1) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Select Module Type",
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            module!!.supportedTypes.forEach { type ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable { selectedModuleType = type },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedModuleType == type,
                                        onClick = { selectedModuleType = type }
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = type.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                } else if (module!!.supportedTypes.isNotEmpty()) {
                    // Auto-select if only one type is supported
                    LaunchedEffect(Unit) {
                        selectedModuleType = module!!.supportedTypes.first()
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
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Install progress
                if (installState != InstallState.IDLE) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (installState) {
                                InstallState.DOWNLOADING -> "Downloading..."
                                InstallState.INSTALLING -> "Installing..."
                                InstallState.COMPLETED -> "Installation completed!"
                                InstallState.FAILED -> "Installation failed"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { installProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        if (installState == InstallState.COMPLETED) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Done")
                            }
                        } else if (installState == InstallState.FAILED) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = viewModel.errorMessage ?: "Unknown error occurred",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.installModule(selectedModuleType!!)
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                } else {
                    // Install button
                    Button(
                        onClick = {
                            if (selectedModuleType != null) {
                                coroutineScope.launch {
                                    viewModel.installModule(selectedModuleType!!)
                                }
                            }
                        },
                        enabled = selectedModuleType != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Install"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Install Module")
                    }
                }
            }
        }
    }
}
