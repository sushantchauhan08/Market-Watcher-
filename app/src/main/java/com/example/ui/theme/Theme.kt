package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryNavy,
    onPrimary = OnPrimaryNavy,
    primaryContainer = PrimaryNavyContainer,
    onPrimaryContainer = OnPrimaryNavyContainer,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGreenContainer,
    onSecondaryContainer = OnSecondaryGreenContainer,
    background = AmbientBackground,
    onBackground = OnAmbientBackground,
    surface = SurfaceLowest,
    onSurface = OnAmbientBackground,
    error = ColorError,
    errorContainer = ColorErrorContainer,
    onErrorContainer = OnColorErrorContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryNavy,
    onPrimary = OnPrimaryNavy,
    primaryContainer = PrimaryNavyContainer,
    onPrimaryContainer = OnPrimaryNavyContainer,
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGreenContainer,
    onSecondaryContainer = OnSecondaryGreenContainer,
    background = AmbientBackground,
    onBackground = OnAmbientBackground,
    surface = SurfaceLowest,
    onSurface = OnAmbientBackground,
    error = ColorError,
    errorContainer = ColorErrorContainer,
    onErrorContainer = OnColorErrorContainer
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Custom brand colours are calibrated, so we default dynamicColor off.
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
