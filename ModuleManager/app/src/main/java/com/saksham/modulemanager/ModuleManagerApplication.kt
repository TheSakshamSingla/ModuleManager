package com.saksham.modulemanager

import android.app.Application
import com.saksham.modulemanager.data.repository.ModuleRepository
import com.saksham.modulemanager.data.repository.RepositoryRepository
import com.saksham.modulemanager.data.repository.SettingsRepository
import com.saksham.modulemanager.util.NotificationUtils
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application class for Module Manager
 */
class ModuleManagerApplication : Application() {
    
    // Application scope
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Repositories
    lateinit var moduleRepository: ModuleRepository
        private set
    
    lateinit var repositoryRepository: RepositoryRepository
        private set
    
    lateinit var settingsRepository: SettingsRepository
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Shell
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
        
        // Initialize repositories
        moduleRepository = ModuleRepository(this)
        repositoryRepository = RepositoryRepository(this)
        settingsRepository = SettingsRepository(this)
        
        // Create notification channel
        NotificationUtils.createNotificationChannel(this)
        
        // Initialize repositories in background
        applicationScope.launch {
            moduleRepository.refreshModules()
            repositoryRepository.refreshRepositories()
        }
    }
}
