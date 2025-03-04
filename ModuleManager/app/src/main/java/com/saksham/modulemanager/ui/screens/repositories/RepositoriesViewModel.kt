package com.saksham.modulemanager.ui.screens.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saksham.modulemanager.data.repository.RepositoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the repositories screen
 */
class RepositoriesViewModel(
    private val repositoryRepository: RepositoryRepository
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val repositories = repositoryRepository.getAllRepositories()
    
    /**
     * Add a repository by URL
     */
    suspend fun addRepository(url: String) {
        _isLoading.value = true
        try {
            repositoryRepository.addRepositoryByUrl(url)
        } catch (e: Exception) {
            // Handle error
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * Search for repositories
     */
    fun searchRepositories(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repositoryRepository.searchRepositories(query)
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
    fun refreshRepositories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val repos = repositories.value ?: emptyList()
                for (repo in repos) {
                    repositoryRepository.refreshRepository(repo)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
