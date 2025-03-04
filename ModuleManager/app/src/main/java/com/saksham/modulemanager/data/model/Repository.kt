package com.saksham.modulemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a GitHub repository that hosts modules
 */
@Entity(tableName = "repositories")
data class Repository(
    @PrimaryKey
    val id: String,
    val name: String,
    val owner: String,
    val description: String?,
    val url: String,
    val website: String?,
    val stars: Int = 0,
    val forks: Int = 0,
    val lastUpdated: Date = Date(),
    val lastFetched: Date = Date(),
    val moduleCount: Int = 0,
    val isOfficial: Boolean = false,
    val avatarUrl: String? = null,
    val supportedModuleTypes: List<ModuleType> = listOf(ModuleType.KERNELSU, ModuleType.MAGISK)
)
