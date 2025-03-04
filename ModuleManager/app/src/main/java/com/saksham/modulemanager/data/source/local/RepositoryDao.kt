package com.saksham.modulemanager.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saksham.modulemanager.data.model.Repository
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface RepositoryDao {
    
    @Query("SELECT * FROM repositories")
    fun getAllRepositories(): Flow<List<Repository>>
    
    @Query("SELECT * FROM repositories WHERE isOfficial = 1")
    fun getOfficialRepositories(): Flow<List<Repository>>
    
    @Query("SELECT * FROM repositories WHERE id = :id")
    suspend fun getRepositoryById(id: String): Repository?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepository(repository: Repository)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repositories: List<Repository>)
    
    @Update
    suspend fun updateRepository(repository: Repository)
    
    @Delete
    suspend fun deleteRepository(repository: Repository)
    
    @Query("DELETE FROM repositories WHERE id = :id")
    suspend fun deleteRepositoryById(id: String)
    
    @Query("UPDATE repositories SET lastFetched = :lastFetched, moduleCount = :moduleCount WHERE id = :id")
    suspend fun updateRepositoryFetchInfo(id: String, lastFetched: Date, moduleCount: Int)
}
