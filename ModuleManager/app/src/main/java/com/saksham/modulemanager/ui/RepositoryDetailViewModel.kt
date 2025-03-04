package com.saksham.modulemanager.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.model.Repository
import com.saksham.modulemanager.data.repository.RepositoryRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the repository detail screen
 */
class RepositoryDetailViewModel(
    private val repositoryRepository: RepositoryRepository,
    private val repositoryId: String
) : ViewModel() {
    
    private val _repository = mutableStateOf<Repository?>(null)
    val repository: State<Repository?> = _repository
    
    private val _modules = mutableStateOf<List<Module>>(emptyList())
    val modules: State<List<Module>> = _modules
    
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    
    /**
     * Load repository data
     */
    fun loadRepository() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val loadedRepository = repositoryRepository.getRepositoryById(repositoryId)
                _repository.value = loadedRepository
                
                // Load modules from repository
                loadedRepository?.let { repo ->
                    _modules.value = repositoryRepository.getModulesFromRepository(repo)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresh repository data
     */
    suspend fun refreshRepository() {
        _repository.value?.let { repo ->
            _isLoading.value = true
            try {
                val refreshedRepo = repositoryRepository.refreshRepository(repo)
                _repository.value = refreshedRepo
                _modules.value = repositoryRepository.getModulesFromRepository(refreshedRepo)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Remove repository
     */
    suspend fun removeRepository() {
        _repository.value?.let { repo ->
            try {
                repositoryRepository.removeRepository(repo)
                // No need to update UI as we'll navigate back
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
