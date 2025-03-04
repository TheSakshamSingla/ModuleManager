package com.saksham.modulemanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.saksham.modulemanager.data.model.Theme as AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF1565C0),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFF42A5F5),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF1976D2),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFFFF),
)

private val DarkAmoledColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF0D47A1),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF64B5F6),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF1565C0),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFF42A5F5),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF1976D2),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFFFFFFF),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFF1565C0),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = Color(0xFF000000),
    tertiary = Color(0xFF0D47A1),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE3F2FD),
    onTertiaryContainer = Color(0xFF000000),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
)

@Composable
fun ModuleManagerTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    enableDarkAmoled: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    
    val colorScheme = when {
        darkTheme && enableDarkAmoled -> DarkAmoledColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
