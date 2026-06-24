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
    primary = SpruceGreen,
    secondary = LimeAccent,
    tertiary = GoldVeggie,
    background = DeepForestDark,
    surface = MildOliveSurface,
    onPrimary = DeepForestDark,
    onSecondary = DeepForestDark,
    onTertiary = DeepForestDark,
    onBackground = DarkWhiteText,
    onSurface = DarkWhiteText
  )

private val LightColorScheme =
  lightColorScheme(
    primary = FreshGreenPrimary,
    secondary = FreshGreenSecondary,
    tertiary = WarmVeggieOrange,
    background = CleanOrganicCream,
    surface = WhitePure,
    onPrimary = WhitePure,
    onSecondary = WhitePure,
    onTertiary = WhitePure,
    onBackground = Color(0xFF1B221C),
    onSurface = Color(0xFF1B221C)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors by default so branding is consistent
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
