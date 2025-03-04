package com.saksham.modulemanager.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.model.ModuleType
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {
    
    @Query("SELECT * FROM modules")
    fun getAllModules(): Flow<List<Module>>
    
    @Query("SELECT * FROM modules WHERE isInstalled = 1")
    fun getInstalledModules(): Flow<List<Module>>
    
    @Query("SELECT * FROM modules WHERE type = :type")
    fun getModulesByType(type: ModuleType): Flow<List<Module>>
    
    @Query("SELECT * FROM modules WHERE id = :id")
    suspend fun getModuleById(id: String): Module?
    
    @Query("SELECT * FROM modules WHERE repositoryId = :repositoryId")
    fun getModulesByRepository(repositoryId: String): Flow<List<Module>>
    
    @Query("SELECT * FROM modules WHERE hasUpdate = 1")
    fun getModulesWithUpdates(): Flow<List<Module>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: Module)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<Module>)
    
    @Update
    suspend fun updateModule(module: Module)
    
    @Delete
    suspend fun deleteModule(module: Module)
    
    @Query("DELETE FROM modules WHERE id = :id")
    suspend fun deleteModuleById(id: String)
    
    @Query("UPDATE modules SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setModuleEnabled(id: String, isEnabled: Boolean)
    
    @Query("UPDATE modules SET hasUpdate = :hasUpdate, newVersion = :newVersion, newVersionCode = :newVersionCode WHERE id = :id")
    suspend fun updateModuleVersion(id: String, hasUpdate: Boolean, newVersion: String?, newVersionCode: Int?)
}
