package com.saksham.modulemanager.ui.screens.repositories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.saksham.modulemanager.ui.components.RepositoryCard
import kotlinx.coroutines.launch

/**
 * Screen for managing repositories
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepositoriesScreen(
    navController: NavController,
    viewModel: RepositoriesViewModel = viewModel {
        val context = LocalContext.current
        val app = context.applicationContext as ModuleManagerApp
        RepositoriesViewModel(app.repositoryRepository)
    }
) {
    val repositories by viewModel.repositories.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var repositoryUrl by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repositories") },
                actions = {
                    IconButton(onClick = { /* Open search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search repositories"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Repo") }
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
        } else {
            if (repositories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No repositories found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.padding(paddingValues)
                ) {
                    items(repositories) { repository ->
                        RepositoryCard(
                            repository = repository,
                            onRepositoryClick = { selectedRepo ->
                                navController.navigate("repository_detail/${selectedRepo.id}")
                            }
                        )
                    }
                }
            }
        }
        
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Repository") },
                text = {
                    Column {
                        Text(
                            text = "Enter the GitHub repository URL",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = repositoryUrl,
                            onValueChange = { repositoryUrl = it },
                            label = { Text("Repository URL") },
                            placeholder = { Text("https://github.com/username/repo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (repositoryUrl.isNotEmpty()) {
                                coroutineScope.launch {
                                    viewModel.addRepository(repositoryUrl)
                                    showAddDialog = false
                                    repositoryUrl = ""
                                }
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showAddDialog = false
                            repositoryUrl = ""
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
