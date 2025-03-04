package com.saksham.modulemanager.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.repository.ModuleRepository
import com.saksham.modulemanager.util.ModuleInstaller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen
 */
class HomeViewModel(
    private val moduleRepository: ModuleRepository
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val modules = moduleRepository.getInstalledModules()
    
    init {
        refreshModules()
    }
    
    /**
     * Refresh the list of installed modules
     */
    fun refreshModules() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                moduleRepository.scanForInstalledModules()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggle a module's enabled state
     */
    suspend fun toggleModule(module: Module, isEnabled: Boolean) {
        try {
            val result = ModuleInstaller.setModuleEnabled(module, isEnabled)
            if (result.isSuccess) {
                moduleRepository.setModuleEnabled(module.id, isEnabled)
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    /**
     * Check for updates for all modules
     */
    fun checkForUpdates() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val installedModules = moduleRepository.getInstalledModules().value ?: emptyList()
                for (module in installedModules) {
                    if (module.updateUrl != null) {
                        moduleRepository.checkForUpdates(module)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
