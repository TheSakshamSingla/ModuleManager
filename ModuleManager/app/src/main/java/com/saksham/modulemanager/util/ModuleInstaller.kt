package com.saksham.modulemanager.util

import android.content.Context
import com.saksham.modulemanager.data.model.Module
import com.saksham.modulemanager.data.model.ModuleType
import com.saksham.modulemanager.data.model.Release
import com.saksham.modulemanager.data.model.ReleaseAsset
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream

/**
 * Utility class for installing and managing modules
 */
object ModuleInstaller {
    
    /**
     * Initialize Shell
     */
    fun init() {
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(Shell.Builder.create()
            .setFlags(Shell.FLAG_MOUNT_MASTER)
            .setTimeout(10)
        )
    }
    
    /**
     * Check if root access is available
     */
    suspend fun hasRootAccess(): Boolean {
        return withContext(Dispatchers.IO) {
            Shell.getShell().isRoot
        }
    }
    
    /**
     * Install a module from a URL
     */
    suspend fun installModuleFromUrl(
        context: Context,
        url: String,
        type: ModuleType
    ): Result<Module> {
        return withContext(Dispatchers.IO) {
            try {
                // Create temporary directory
                val cacheDir = context.cacheDir
                val tempDir = File(cacheDir, "module_temp_${System.currentTimeMillis()}")
                tempDir.mkdirs()
                
                // Download the module zip
                val zipFile = File(tempDir, "module.zip")
                downloadFile(url, zipFile)
                
                // Extract the zip
                extractZip(zipFile, tempDir)
                
                // Check if module.prop exists
                val propFile = File(tempDir, "module.prop")
                if (!propFile.exists()) {
                    return@withContext Result.Error("Invalid module: module.prop not found")
                }
                
                // Parse module.prop
                val module = ModuleParser.parseModuleProp(propFile, type)
                    ?: return@withContext Result.Error("Failed to parse module.prop")
                
                // Install the module
                val result = when (type) {
                    ModuleType.KERNELSU -> installKernelSUModule(tempDir, module.id)
                    ModuleType.MAGISK -> installMagiskModule(tempDir, module.id)
                    else -> return@withContext Result.Error("Unsupported module type")
                }
                
                // Clean up
                tempDir.deleteRecursively()
                
                if (result) {
                    Result.Success(module)
                } else {
                    Result.Error("Failed to install module")
                }
            } catch (e: Exception) {
                Result.Error("Installation failed: ${e.message}")
            }
        }
    }
    
    /**
     * Install a module from a release asset
     */
    suspend fun installModuleFromRelease(
        context: Context,
        release: Release,
        asset: ReleaseAsset,
        type: ModuleType
    ): Result<Module> {
        return installModuleFromUrl(context, asset.downloadUrl, type)
    }
    
    /**
     * Enable or disable a module
     */
    suspend fun setModuleEnabled(module: Module, enabled: Boolean): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val result = when (module.type) {
                    ModuleType.KERNELSU -> setKernelSUModuleEnabled(module.id, enabled)
                    ModuleType.MAGISK -> setMagiskModuleEnabled(module.id, enabled)
                    else -> return@withContext Result.Error("Unsupported module type")
                }
                
                if (result) {
                    Result.Success(true)
                } else {
                    Result.Error("Failed to ${if (enabled) "enable" else "disable"} module")
                }
            } catch (e: Exception) {
                Result.Error("Operation failed: ${e.message}")
            }
        }
    }
    
    /**
     * Uninstall a module
     */
    suspend fun uninstallModule(module: Module): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val result = when (module.type) {
                    ModuleType.KERNELSU -> uninstallKernelSUModule(module.id)
                    ModuleType.MAGISK -> uninstallMagiskModule(module.id)
                    else -> return@withContext Result.Error("Unsupported module type")
                }
                
                if (result) {
                    Result.Success(true)
                } else {
                    Result.Error("Failed to uninstall module")
                }
            } catch (e: Exception) {
                Result.Error("Uninstallation failed: ${e.message}")
            }
        }
    }
    
    /**
     * Download a file from a URL
     */
    private suspend fun downloadFile(url: String, destination: File) {
        withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection()
            connection.connect()
            
            val inputStream = connection.getInputStream()
            val outputStream = FileOutputStream(destination)
            
            val buffer = ByteArray(1024)
            var bytesRead: Int
            
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            
            outputStream.close()
            inputStream.close()
        }
    }
    
    /**
     * Extract a zip file
     */
    private suspend fun extractZip(zipFile: File, destination: File) {
        withContext(Dispatchers.IO) {
            val zipInputStream = ZipInputStream(zipFile.inputStream())
            var entry = zipInputStream.nextEntry
            
            while (entry != null) {
                val file = File(destination, entry.name)
                
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile?.mkdirs()
                    
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    
                    while (zipInputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    
                    outputStream.close()
                }
                
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
            
            zipInputStream.close()
        }
    }
    
    /**
     * Install a KernelSU module
     */
    private suspend fun installKernelSUModule(moduleDir: File, moduleId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val targetDir = "/data/adb/modules/$moduleId"
            
            val result = Shell.cmd(
                "rm -rf $targetDir",
                "mkdir -p $targetDir",
                "cp -af ${moduleDir.absolutePath}/* $targetDir/",
                "chmod -R 755 $targetDir"
            ).exec()
            
            result.isSuccess
        }
    }
    
    /**
     * Install a Magisk module
     */
    private suspend fun installMagiskModule(moduleDir: File, moduleId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val targetDir = "/data/adb/modules/$moduleId"
            
            val result = Shell.cmd(
                "rm -rf $targetDir",
                "mkdir -p $targetDir",
                "cp -af ${moduleDir.absolutePath}/* $targetDir/",
                "chmod -R 755 $targetDir"
            ).exec()
            
            result.isSuccess
        }
    }
    
    /**
     * Enable or disable a KernelSU module
     */
    private suspend fun setKernelSUModuleEnabled(moduleId: String, enabled: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            val disableFile = "/data/adb/modules/$moduleId/disable"
            
            val result = if (enabled) {
                Shell.cmd("rm -f $disableFile").exec()
            } else {
                Shell.cmd("touch $disableFile").exec()
            }
            
            result.isSuccess
        }
    }
    
    /**
     * Enable or disable a Magisk module
     */
    private suspend fun setMagiskModuleEnabled(moduleId: String, enabled: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            val disableFile = "/data/adb/modules/$moduleId/disable"
            
            val result = if (enabled) {
                Shell.cmd("rm -f $disableFile").exec()
            } else {
                Shell.cmd("touch $disableFile").exec()
            }
            
            result.isSuccess
        }
    }
    
    /**
     * Uninstall a KernelSU module
     */
    private suspend fun uninstallKernelSUModule(moduleId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val result = Shell.cmd("rm -rf /data/adb/modules/$moduleId").exec()
            result.isSuccess
        }
    }
    
    /**
     * Uninstall a Magisk module
     */
    private suspend fun uninstallMagiskModule(moduleId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val result = Shell.cmd("rm -rf /data/adb/modules/$moduleId").exec()
            result.isSuccess
        }
    }
}
