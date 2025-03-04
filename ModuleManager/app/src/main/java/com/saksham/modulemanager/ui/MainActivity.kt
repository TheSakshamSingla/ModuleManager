package com.saksham.modulemanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.saksham.modulemanager.ModuleManagerApp
import com.saksham.modulemanager.ui.theme.ModuleManagerTheme
import com.saksham.modulemanager.util.ModuleInstaller
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val settingsRepository by lazy { (application as ModuleManagerApp).settingsRepository }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ModuleInstaller
        ModuleInstaller.init()
        
        // Check for root access
        lifecycleScope.launch {
            val hasRoot = ModuleInstaller.hasRootAccess()
            if (!hasRoot) {
                // Show root access required dialog
                // In a real app, we would show a dialog here
            }
        }
        
        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = null)
            
            settings?.let { appSettings ->
                ModuleManagerTheme(
                    appTheme = appSettings.theme,
                    enableDarkAmoled = appSettings.enableDarkAmoled
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ModuleManagerApp()
                    }
                }
            }
        }
    }
}
