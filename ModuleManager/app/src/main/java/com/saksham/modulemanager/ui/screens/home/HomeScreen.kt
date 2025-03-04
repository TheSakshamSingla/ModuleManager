package com.saksham.modulemanager.ui.screens.home

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.model.ModuleType
import com.saksham.modulemanager.ui.components.ModuleCard
import com.saksham.modulemanager.ui.screens.home.HomeViewModel
import kotlinx.coroutines.launch

/**
 * Home screen showing installed modules
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel {
        val context = LocalContext.current
        val app = context.applicationContext as ModuleManagerApp
        HomeViewModel(app.moduleRepository)
    }
) {
    val modules by viewModel.modules.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    val tabs = listOf("All", "KernelSU", "Magisk")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Module Manager") },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.padding(16.dp).clickable {
                            coroutineScope.launch {
                                viewModel.refreshModules()
                            }
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Navigate to module installation screen */ },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Install") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val filteredModules = when (selectedTabIndex) {
                    1 -> modules.filter { it.type == ModuleType.KERNELSU }
                    2 -> modules.filter { it.type == ModuleType.MAGISK }
                    else -> modules
                }
                
                if (filteredModules.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No modules found",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(filteredModules) { module ->
                            ModuleCard(
                                module = module,
                                onModuleClick = { selectedModule ->
                                    navController.navigate("module_detail/${selectedModule.id}")
                                },
                                onModuleToggle = { selectedModule, isEnabled ->
                                    coroutineScope.launch {
                                        viewModel.toggleModule(selectedModule, isEnabled)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
