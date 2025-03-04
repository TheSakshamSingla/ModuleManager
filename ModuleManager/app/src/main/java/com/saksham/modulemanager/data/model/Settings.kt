package com.saksham.modulemanager.data.model

/**
 * Represents user settings for the app
 */
data class Settings(
    val theme: Theme = Theme.SYSTEM,
    val checkForUpdatesAutomatically: Boolean = true,
    val updateCheckInterval: UpdateCheckInterval = UpdateCheckInterval.DAILY,
    val showNotificationsForUpdates: Boolean = true,
    val autoBackupModules: Boolean = true,
    val backupLocation: String? = null,
    val preferredModuleType: ModuleType? = null,
    val useSystemWebView: Boolean = false,
    val enableDarkAmoled: Boolean = false,
    val enableAnalytics: Boolean = true,
    val showBetaReleases: Boolean = false
)

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM
}

enum class UpdateCheckInterval(val hours: Int) {
    HOURLY(1),
    DAILY(24),
    WEEKLY(168),
    MONTHLY(720)
}
