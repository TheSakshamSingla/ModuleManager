package com.saksham.modulemanager.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.repository.ModuleRepository
import com.saksham.modulemanager.util.ModuleInstaller
import kotlinx.coroutines.launch

/**
 * ViewModel for the module detail screen
 */
class ModuleDetailViewModel(
    private val moduleRepository: ModuleRepository,
    private val moduleId: String
) : ViewModel() {
    
    private val _module = mutableStateOf<Module?>(null)
    val module: State<Module?> = _module
    
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    
    private val _changelog = mutableStateOf<String?>(null)
    val changelog: String? get() = _changelog.value
    
    /**
     * Load module data
     */
    fun loadModule() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedModule = moduleRepository.getModuleById(moduleId)
                _module.value = loadedModule
                
                // Load changelog if update URL is available
                loadedModule?.updateUrl?.let { url ->
                    _changelog.value = moduleRepository.getModuleChangelog(loadedModule)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggle module enabled state
     */
    suspend fun toggleModule(isEnabled: Boolean) {
        _module.value?.let { module ->
            try {
                val result = ModuleInstaller.setModuleEnabled(module, isEnabled)
                if (result.isSuccess) {
                    moduleRepository.setModuleEnabled(module.id, isEnabled)
                    _module.value = _module.value?.copy(isEnabled = isEnabled)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    /**
     * Update module to latest version
     */
    suspend fun updateModule() {
        _module.value?.let { module ->
            _isLoading.value = true
            try {
                val result = moduleRepository.updateModule(module)
                if (result.isSuccess) {
                    loadModule()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Uninstall module
     */
    suspend fun uninstallModule() {
        _module.value?.let { module ->
            _isLoading.value = true
            try {
                val result = moduleRepository.uninstallModule(module)
                // No need to update UI as we'll navigate back
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
