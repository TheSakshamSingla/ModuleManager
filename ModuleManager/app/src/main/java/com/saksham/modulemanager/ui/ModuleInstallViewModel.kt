package com.saksham.modulemanager.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.model.ModuleType
import com.saksham.modulemanager.data.repository.ModuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * States for module installation
 */
enum class InstallState {
    IDLE,
    DOWNLOADING,
    INSTALLING,
    COMPLETED,
    FAILED
}

/**
 * ViewModel for the module installation screen
 */
class ModuleInstallViewModel(
    private val moduleRepository: ModuleRepository,
    private val moduleId: String
) : ViewModel() {
    
    private val _module = mutableStateOf<Module?>(null)
    val module: State<Module?> = _module
    
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    
    private val _installProgress = MutableStateFlow(0f)
    val installProgress: StateFlow<Float> = _installProgress.asStateFlow()
    
    private val _installState = MutableStateFlow(InstallState.IDLE)
    val installState: StateFlow<InstallState> = _installState.asStateFlow()
    
    private val _changelog = mutableStateOf<String?>(null)
    val changelog: String? get() = _changelog.value
    
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value
    
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
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Install the module
     */
    suspend fun installModule(moduleType: ModuleType) {
        _module.value?.let { module ->
            _installState.value = InstallState.DOWNLOADING
            _installProgress.value = 0f
            _errorMessage.value = null
            
            try {
                // Download module
                moduleRepository.downloadModule(
                    module = module,
                    moduleType = moduleType,
                    onProgressUpdate = { progress ->
                        _installProgress.value = progress * 0.8f // Download is 80% of the process
                    }
                ).onSuccess {
                    // Install module
                    _installState.value = InstallState.INSTALLING
                    
                    moduleRepository.installModule(
                        module = module,
                        moduleType = moduleType
                    ).onSuccess {
                        _installProgress.value = 1f
                        _installState.value = InstallState.COMPLETED
                    }.onFailure { error ->
                        _errorMessage.value = error.message
                        _installState.value = InstallState.FAILED
                    }
                }.onFailure { error ->
                    _errorMessage.value = error.message
                    _installState.value = InstallState.FAILED
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _installState.value = InstallState.FAILED
            }
        }
    }
}
