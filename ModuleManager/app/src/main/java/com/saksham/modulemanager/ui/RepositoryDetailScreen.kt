package com.saksham.modulemanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import coil.compose.AsyncImage
import com.saksham.modulemanager.ModuleManagerApp
import com.saksham.modulemanager.data.model.Repository
import com.saksham.modulemanager.ui.components.ModuleCard
import kotlinx.coroutines.launch

/**
 * Screen for repository details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoryDetailScreen(
    navController: NavController,
    repositoryId: String,
    viewModel: RepositoryDetailViewModel = viewModel {
        val context = LocalContext.current
        val app = context.applicationContext as ModuleManagerApp
        RepositoryDetailViewModel(app.repositoryRepository, repositoryId)
    }
) {
    val repository by viewModel.repository
    val modules by viewModel.modules
    val isLoading by viewModel.isLoading
    val coroutineScope = rememberCoroutineScope()
    
    var showRemoveDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(repositoryId) {
        viewModel.loadRepository()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repository?.name ?: "Repository Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.refreshRepository()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    
                    IconButton(onClick = { showRemoveDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove"
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
        } else if (repository == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Repository not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Repository header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repository?.avatarUrl?.let { avatarUrl ->
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Repository avatar",
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            
                            Column {
                                Text(
                                    text = repository!!.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = repository!!.owner,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Stars",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${repository!!.stars}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        repository?.description?.let { description ->
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Text(
                            text = "Last updated: ${repository!!.lastUpdated}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                
                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                
                // Modules list
                Text(
                    text = "Available Modules (${modules.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                
                if (modules.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No modules found in this repository",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(modules) { module ->
                            ModuleCard(
                                module = module,
                                onModuleClick = { selectedModule ->
                                    // If module is installed, navigate to detail
                                    // Otherwise, show install dialog
                                    if (selectedModule.isInstalled) {
                                        navController.navigate("module_detail/${selectedModule.id}")
                                    } else {
                                        // Navigate to install screen
                                        navController.navigate("module_install/${selectedModule.id}")
                                    }
                                },
                                onModuleToggle = { _, _ -> /* Not applicable for repository modules */ }
                            )
                        }
                    }
                }
            }
        }
        
        if (showRemoveDialog) {
            AlertDialog(
                onDismissRequest = { showRemoveDialog = false },
                title = { Text("Remove Repository") },
                text = { Text("Are you sure you want to remove this repository? This will not uninstall any modules from this repository.") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.removeRepository()
                                showRemoveDialog = false
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Remove")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showRemoveDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
