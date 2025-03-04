package com.saksham.modulemanager.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.saksham.modulemanager.R
import com.saksham.modulemanager.ui.MainActivity

/**
 * Utility class for notifications
 */
object NotificationUtils {
    private const val CHANNEL_ID = "module_manager_channel"
    private const val CHANNEL_NAME = "Module Manager"
    private const val CHANNEL_DESCRIPTION = "Notifications for module updates and installations"
    
    /**
     * Create the notification channel
     *
     * @param context The application context
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Show a notification for a module update
     *
     * @param context The application context
     * @param moduleId The ID of the module
     * @param moduleName The name of the module
     * @param newVersion The new version of the module
     */
    fun showModuleUpdateNotification(
        context: Context,
        moduleId: String,
        moduleName: String,
        newVersion: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("moduleId", moduleId)
            putExtra("action", "update")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Module Update Available")
            .setContentText("$moduleName v$newVersion is available for update")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            notify(moduleId.hashCode(), builder.build())
        }
    }
    
    /**
     * Show a notification for a module installation
     *
     * @param context The application context
     * @param moduleId The ID of the module
     * @param moduleName The name of the module
     * @param success Whether the installation was successful
     */
    fun showModuleInstallationNotification(
        context: Context,
        moduleId: String,
        moduleName: String,
        success: Boolean
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("moduleId", moduleId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val title = if (success) "Module Installed" else "Installation Failed"
        val text = if (success) "$moduleName was successfully installed" else "Failed to install $moduleName"
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            notify(moduleId.hashCode(), builder.build())
        }
    }
}
