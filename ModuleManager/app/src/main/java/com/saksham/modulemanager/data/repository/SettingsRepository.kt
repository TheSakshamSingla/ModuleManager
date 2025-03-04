package com.saksham.modulemanager.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.saksham.modulemanager.data.model.ModuleType
import com.saksham.modulemanager.data.model.Settings
import com.saksham.modulemanager.data.model.Theme
import com.saksham.modulemanager.data.model.UpdateCheckInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Repository for app settings
 */
class SettingsRepository(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val _settings = MutableStateFlow(loadSettings())
    val settings: Flow<Settings> = _settings.asStateFlow()
    
    /**
     * Update settings
     */
    suspend fun updateSettings(settings: Settings) {
        withContext(Dispatchers.IO) {
            val json = gson.toJson(settings)
            prefs.edit().putString(KEY_SETTINGS, json).apply()
            _settings.value = settings
        }
    }
    
    /**
     * Update theme
     */
    suspend fun updateTheme(theme: Theme) {
        withContext(Dispatchers.IO) {
            val currentSettings = _settings.value
            val updatedSettings = currentSettings.copy(theme = theme)
            updateSettings(updatedSettings)
        }
    }
    
    /**
     * Update update check interval
     */
    suspend fun updateUpdateCheckInterval(interval: UpdateCheckInterval) {
        withContext(Dispatchers.IO) {
            val currentSettings = _settings.value
            val updatedSettings = currentSettings.copy(updateCheckInterval = interval)
            updateSettings(updatedSettings)
        }
    }
    
    /**
     * Update preferred module type
     */
    suspend fun updatePreferredModuleType(type: ModuleType?) {
        withContext(Dispatchers.IO) {
            val currentSettings = _settings.value
            val updatedSettings = currentSettings.copy(preferredModuleType = type)
            updateSettings(updatedSettings)
        }
    }
    
    /**
     * Update automatic update check
     */
    suspend fun updateAutomaticUpdateCheck(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            val currentSettings = _settings.value
            val updatedSettings = currentSettings.copy(checkForUpdatesAutomatically = enabled)
            updateSettings(updatedSettings)
        }
    }
    
    /**
     * Update show notifications for updates
     */
    suspend fun updateShowNotificationsForUpdates(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            val currentSettings = _settings.value
            val updatedSettings = currentSettings.copy(showNotificationsForUpdates = enabled)
            updateSettings(updatedSettings)
        }
    }
    
    /**
     * Load settings from SharedPreferences
     */
    private fun loadSettings(): Settings {
        val json = prefs.getString(KEY_SETTINGS, null)
        return if (json != null) {
            try {
                gson.fromJson(json, Settings::class.java)
            } catch (e: Exception) {
                Settings()
            }
        } else {
            Settings()
        }
    }
    
    companion object {
        private const val PREFS_NAME = "module_manager_prefs"
        private const val KEY_SETTINGS = "settings"
    }
}
