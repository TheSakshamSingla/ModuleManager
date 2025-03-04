package com.saksham.modulemanager

import android.app.Application
import com.saksham.modulemanager.data.repository.ModuleRepository
import com.saksham.modulemanager.data.repository.RepositoryRepository
import com.saksham.modulemanager.data.repository.SettingsRepository
import com.saksham.modulemanager.data.source.local.AppDatabase
import com.saksham.modulemanager.data.source.remote.GithubApiService
import com.saksham.modulemanager.data.source.remote.RetrofitClient

class ModuleManagerApp : Application() {
    
    // Database instance
    private val database by lazy { AppDatabase.getDatabase(this) }
    
    // API service
    private val apiService by lazy { RetrofitClient.createGithubApiService() }
    
    // Repositories
    val moduleRepository by lazy { ModuleRepository(database.moduleDao(), apiService) }
    val repositoryRepository by lazy { RepositoryRepository(database.repositoryDao(), apiService) }
    val settingsRepository by lazy { SettingsRepository(this) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: ModuleManagerApp
            private set
    }
}
