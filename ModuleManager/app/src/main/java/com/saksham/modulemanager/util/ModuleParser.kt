package com.saksham.modulemanager.util

import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.model.ModuleType
import java.io.File
import java.util.Date
import java.util.Properties

/**
 * Utility class for parsing module properties
 */
object ModuleParser {
    
    /**
     * Parse module.prop file and create a Module object
     */
    fun parseModuleProp(file: File, type: ModuleType): Module? {
        return try {
            val properties = Properties()
            file.inputStream().use { properties.load(it) }
            
            val id = properties.getProperty("id") ?: return null
            val name = properties.getProperty("name") ?: id
            val version = properties.getProperty("version") ?: "1.0"
            val versionCode = properties.getProperty("versionCode")?.toIntOrNull() ?: 1
            val author = properties.getProperty("author") ?: "Unknown"
            val description = properties.getProperty("description") ?: ""
            val updateUrl = properties.getProperty("updateUrl")
            
            // Check if module is enabled
            val isEnabled = when (type) {
                ModuleType.KERNELSU -> {
                    val disableFile = File(file.parentFile, "disable")
                    !disableFile.exists()
                }
                ModuleType.MAGISK -> {
                    val disableFile = File(file.parentFile, "disable")
                    !disableFile.exists()
                }
                else -> true
            }
            
            Module(
                id = id,
                name = name,
                version = version,
                versionCode = versionCode,
                author = author,
                description = description,
                updateUrl = updateUrl,
                isEnabled = isEnabled,
                type = type,
                installDate = Date(file.lastModified()),
                localPath = file.parentFile.absolutePath
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Create module.prop content from Module object
     */
    fun createModuleProp(module: Module): String {
        return buildString {
            appendLine("id=${module.id}")
            appendLine("name=${module.name}")
            appendLine("version=${module.version}")
            appendLine("versionCode=${module.versionCode}")
            appendLine("author=${module.author}")
            appendLine("description=${module.description}")
            if (module.updateUrl != null) {
                appendLine("updateUrl=${module.updateUrl}")
            }
        }
    }
}
