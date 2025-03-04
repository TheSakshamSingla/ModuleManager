package com.saksham.modulemanager.data.repository

import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.model.ModuleType
import com.saksham.modulemanager.data.model.Release
import com.saksham.modulemanager.data.source.local.ModuleDao
import com.saksham.modulemanager.data.source.remote.GithubApiService
import com.saksham.modulemanager.util.ModuleParser
import com.saksham.modulemanager.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

/**
 * Repository for module-related operations
 */
class ModuleRepository(
    private val moduleDao: ModuleDao,
    private val apiService: GithubApiService
) {
    
    /**
     * Get all modules as a flow
     */
    fun getAllModules(): Flow<List<Module>> {
        return moduleDao.getAllModules()
    }
    
    /**
     * Get installed modules as a flow
     */
    fun getInstalledModules(): Flow<List<Module>> {
        return moduleDao.getInstalledModules()
    }
    
    /**
     * Get modules by type as a flow
     */
    fun getModulesByType(type: ModuleType): Flow<List<Module>> {
        return moduleDao.getModulesByType(type)
    }
    
    /**
     * Get modules with updates as a flow
     */
    fun getModulesWithUpdates(): Flow<List<Module>> {
        return moduleDao.getModulesWithUpdates()
    }
    
    /**
     * Get module by ID
     */
    suspend fun getModuleById(id: String): Module? {
        return withContext(Dispatchers.IO) {
            moduleDao.getModuleById(id)
        }
    }
    
    /**
     * Insert or update a module
     */
    suspend fun saveModule(module: Module) {
        withContext(Dispatchers.IO) {
            moduleDao.insertModule(module)
        }
    }
    
    /**
     * Delete a module
     */
    suspend fun deleteModule(module: Module) {
        withContext(Dispatchers.IO) {
            moduleDao.deleteModule(module)
        }
    }
    
    /**
     * Set module enabled state
     */
    suspend fun setModuleEnabled(id: String, isEnabled: Boolean) {
        withContext(Dispatchers.IO) {
            moduleDao.setModuleEnabled(id, isEnabled)
        }
    }
    
    /**
     * Check for module updates
     */
    suspend fun checkForUpdates(module: Module): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (module.repositoryId == null || module.updateUrl == null) {
                    return@withContext Result.Error("No update source available")
                }
                
                val repoInfo = module.repositoryId.split("/")
                if (repoInfo.size < 2) {
                    return@withContext Result.Error("Invalid repository format")
                }
                
                val owner = repoInfo[0]
                val repo = repoInfo[1]
                
                val latestRelease = apiService.getLatestRelease(owner, repo)
                val versionName = latestRelease.tagName.removePrefix("v")
                val versionCode = versionName.replace(".", "").toIntOrNull() ?: 0
                
                val hasUpdate = versionCode > module.versionCode
                
                if (hasUpdate) {
                    moduleDao.updateModuleVersion(
                        module.id,
                        true,
                        versionName,
                        versionCode
                    )
                }
                
                Result.Success(hasUpdate)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Scan for installed modules
     */
    suspend fun scanForInstalledModules(): Result<List<Module>> {
        return withContext(Dispatchers.IO) {
            try {
                val modules = mutableListOf<Module>()
                
                // Scan for KernelSU modules
                val ksuModulesDir = File("/data/adb/modules")
                if (ksuModulesDir.exists() && ksuModulesDir.isDirectory) {
                    ksuModulesDir.listFiles()?.forEach { moduleDir ->
                        if (moduleDir.isDirectory) {
                            val propFile = File(moduleDir, "module.prop")
                            if (propFile.exists()) {
                                val module = ModuleParser.parseModuleProp(propFile, ModuleType.KERNELSU)
                                if (module != null) {
                                    modules.add(module)
                                }
                            }
                        }
                    }
                }
                
                // Scan for Magisk modules
                val magiskModulesDir = File("/data/adb/modules")
                if (magiskModulesDir.exists() && magiskModulesDir.isDirectory) {
                    magiskModulesDir.listFiles()?.forEach { moduleDir ->
                        if (moduleDir.isDirectory) {
                            val propFile = File(moduleDir, "module.prop")
                            if (propFile.exists()) {
                                val module = ModuleParser.parseModuleProp(propFile, ModuleType.MAGISK)
                                if (module != null) {
                                    modules.add(module)
                                }
                            }
                        }
                    }
                }
                
                // Save all found modules to database
                moduleDao.insertModules(modules)
                
                Result.Success(modules)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    /**
     * Get releases for a module
     */
    suspend fun getModuleReleases(module: Module): Result<List<Release>> {
        return withContext(Dispatchers.IO) {
            try {
                if (module.repositoryId == null) {
                    return@withContext Result.Error("No repository information available")
                }
                
                val repoInfo = module.repositoryId.split("/")
                if (repoInfo.size < 2) {
                    return@withContext Result.Error("Invalid repository format")
                }
                
                val owner = repoInfo[0]
                val repo = repoInfo[1]
                
                val releases = apiService.getReleases(owner, repo)
                    .map { it.toDomainModel(module.id, module.repositoryId) }
                
                Result.Success(releases)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error")
            }
        }
    }
}
