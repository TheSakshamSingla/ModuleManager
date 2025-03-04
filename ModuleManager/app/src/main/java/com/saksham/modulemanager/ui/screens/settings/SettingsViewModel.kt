package com.saksham.modulemanager.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.modulemanager.data.model.ModuleType
import com.saksham.modulemanager.data.model.Theme
import com.saksham.modulemanager.data.model.UpdateCheckInterval
import com.saksham.modulemanager.data.repository.SettingsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the settings screen
 */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    val settings = settingsRepository.settings
    
    /**
     * Update theme
     */
    suspend fun updateTheme(theme: Theme) {
        settingsRepository.updateTheme(theme)
    }
    
    /**
     * Update dark AMOLED theme
     */
    fun updateDarkAmoled(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value ?: return@launch
            val updatedSettings = currentSettings.copy(enableDarkAmoled = enabled)
            settingsRepository.updateSettings(updatedSettings)
        }
    }
    
    /**
     * Update automatic update check
     */
    suspend fun updateAutomaticUpdateCheck(enabled: Boolean) {
        settingsRepository.updateAutomaticUpdateCheck(enabled)
    }
    
    /**
     * Update update check interval
     */
    suspend fun updateUpdateCheckInterval(interval: UpdateCheckInterval) {
        settingsRepository.updateUpdateCheckInterval(interval)
    }
    
    /**
     * Update show notifications for updates
     */
    suspend fun updateShowNotificationsForUpdates(enabled: Boolean) {
        settingsRepository.updateShowNotificationsForUpdates(enabled)
    }
    
    /**
     * Update preferred module type
     */
    suspend fun updatePreferredModuleType(type: ModuleType?) {
        settingsRepository.updatePreferredModuleType(type)
    }
    
    /**
     * Update auto backup modules
     */
    fun updateAutoBackupModules(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value ?: return@launch
            val updatedSettings = currentSettings.copy(autoBackupModules = enabled)
            settingsRepository.updateSettings(updatedSettings)
        }
    }
    
    /**
     * Update show beta releases
     */
    fun updateShowBetaReleases(enabled: Boolean) {
        viewModelScope.launch {
            val currentSettings = settings.value ?: return@launch
            val updatedSettings = currentSettings.copy(showBetaReleases = enabled)
            settingsRepository.updateSettings(updatedSettings)
        }
    }
}
