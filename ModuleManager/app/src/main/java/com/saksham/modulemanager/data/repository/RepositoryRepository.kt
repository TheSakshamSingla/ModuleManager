package com.saksham.modulemanager.data.repository

import com.saksham.modulemanager.data.model.Repository
import com.saksham.modulemanager.data.source.local.RepositoryDao
import com.saksham.modulemanager.data.source.remote.GithubApiService
import com.saksham.modulemanager.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repository for GitHub repository operations
 */
class RepositoryRepository(
    private val repositoryDao: RepositoryDao,
    private val apiService: GithubApiService
) {
    
    /**
     * Get all repositories as a flow
     */
    fun getAllRepositories(): Flow<List<Repository>> {
        return repositoryDao.getAllRepositories()
    }
    
    /**
     * Get official repositories as a flow
     */
    fun getOfficialRepositories(): Flow<List<Repository>> {
        return repositoryDao.getOfficialRepositories()
    }
    
    /**
     * Get repository by ID
     */
    suspend fun getRepositoryById(id: String): Repository? {
        return withContext(Dispatchers.IO) {
            repositoryDao.getRepositoryById(id)
        }
    }
    
    /**
     * Add a repository by URL
     */
    suspend fun addRepositoryByUrl(url: String): Result<Repository> {
        return withContext(Dispatchers.IO) {
            try {
                // Extract owner and repo from GitHub URL
                val regex = "github.com/([^/]+)/([^/]+)".toRegex()
                val matchResult = regex.find(url)
                
                if (matchResult == null || matchResult.groupValues.size < 3) {
                    return@withContext Result.Error("Invalid GitHub URL")
                }
                
                val owner = matchResult.groupValues[1]
                val repo = matchResult.groupValues[2]
                
                // Get repository info from GitHub API
                val githubRepo = apiService.getRepository(owner, repo)
                val repository = githubRepo.toDomainModel()
                
                // Save to database
                repositoryDao.insertRepository(repository)
                
                Result.Success(repository)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Search for repositories
     */
    suspend fun searchRepositories(query: String): Result<List<Repository>> {
        return withContext(Dispatchers.IO) {
            try {
                val searchQuery = if (query.contains("module") || query.contains("magisk") || query.contains("kernelsu")) {
                    query
                } else {
                    "$query module magisk kernelsu"
                }
                
                val response = apiService.searchRepositories(searchQuery)
                val repositories = response.items.map { it.toDomainModel() }
                
                Result.Success(repositories)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Refresh repository data
     */
    suspend fun refreshRepository(repository: Repository): Result<Repository> {
        return withContext(Dispatchers.IO) {
            try {
                val repoInfo = repository.id.split("/")
                if (repoInfo.size < 2) {
                    return@withContext Result.Error("Invalid repository format")
                }
                
                val owner = repoInfo[0]
                val repo = repoInfo[1]
                
                // Get updated repository info
                val githubRepo = apiService.getRepository(owner, repo)
                val updatedRepo = githubRepo.toDomainModel().copy(
                    isOfficial = repository.isOfficial,
                    moduleCount = repository.moduleCount,
                    supportedModuleTypes = repository.supportedModuleTypes
                )
                
                // Save to database
                repositoryDao.updateRepository(updatedRepo)
                
                Result.Success(updatedRepo)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Delete a repository
     */
    suspend fun deleteRepository(repository: Repository) {
        withContext(Dispatchers.IO) {
            repositoryDao.deleteRepository(repository)
        }
    }
    
    /**
     * Update repository fetch information
     */
    suspend fun updateRepositoryFetchInfo(id: String, moduleCount: Int) {
        withContext(Dispatchers.IO) {
            repositoryDao.updateRepositoryFetchInfo(id, Date(), moduleCount)
        }
    }
}
