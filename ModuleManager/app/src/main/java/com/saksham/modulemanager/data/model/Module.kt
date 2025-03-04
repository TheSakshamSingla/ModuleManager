package com.saksham.modulemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Represents a KernelSU or Magisk module
 */
@Entity(tableName = "modules")
data class Module(
    @PrimaryKey
    val id: String,
    val name: String,
    val version: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val updateUrl: String? = null,
    val changelogUrl: String? = null,
    val installDate: Date = Date(),
    val lastUpdateCheck: Date? = null,
    val isEnabled: Boolean = true,
    val isInstalled: Boolean = true,
    val type: ModuleType = ModuleType.UNKNOWN,
    val repositoryId: String? = null,
    val hasUpdate: Boolean = false,
    val newVersion: String? = null,
    val newVersionCode: Int? = null,
    val size: Long = 0,
    val localPath: String? = null
)

enum class ModuleType {
    KERNELSU,
    MAGISK,
    UNKNOWN
}
