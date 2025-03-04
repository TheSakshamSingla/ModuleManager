package com.saksham.modulemanager.util

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class for root operations
 */
object RootUtils {
    
    init {
        // Set flags
        Shell.enableVerboseLogging = false
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }
    
    /**
     * Check if the device is rooted
     *
     * @return true if the device is rooted, false otherwise
     */
    suspend fun isRooted(): Boolean = withContext(Dispatchers.IO) {
        Shell.getShell().isRoot
    }
    
    /**
     * Execute a command as root
     *
     * @param command The command to execute
     * @return The result of the command execution
     */
    suspend fun executeCommand(command: String): Shell.Result = withContext(Dispatchers.IO) {
        Shell.cmd(command).exec()
    }
    
    /**
     * Check if KernelSU is installed
     *
     * @return true if KernelSU is installed, false otherwise
     */
    suspend fun isKernelSUInstalled(): Boolean = withContext(Dispatchers.IO) {
        val result = Shell.cmd("ksuctl -V").exec()
        result.isSuccess
    }
    
    /**
     * Check if Magisk is installed
     *
     * @return true if Magisk is installed, false otherwise
     */
    suspend fun isMagiskInstalled(): Boolean = withContext(Dispatchers.IO) {
        val result = Shell.cmd("magisk -v").exec()
        result.isSuccess
    }
    
    /**
     * Get the KernelSU version
     *
     * @return The KernelSU version or null if not installed
     */
    suspend fun getKernelSUVersion(): String? = withContext(Dispatchers.IO) {
        val result = Shell.cmd("ksuctl -V").exec()
        if (result.isSuccess) {
            result.out.firstOrNull()
        } else {
            null
        }
    }
    
    /**
     * Get the Magisk version
     *
     * @return The Magisk version or null if not installed
     */
    suspend fun getMagiskVersion(): String? = withContext(Dispatchers.IO) {
        val result = Shell.cmd("magisk -v").exec()
        if (result.isSuccess) {
            result.out.firstOrNull()
        } else {
            null
        }
    }
}
