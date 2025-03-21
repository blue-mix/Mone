package com.example.money.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

//private val DarkColorScheme = darkColorScheme(
//    primary = Primary,
//    background = Surface,
//    surface = Surface,
//    error = Destructive,
//    onPrimary = TextPrimary,
//    onSecondary = TextPrimary,
//    onBackground = TextPrimary,
//    onSurface = TextPrimary,
//    onError = TextPrimary,
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

//private fun getColorScheme(
//    themeState: ThemeMode,
//    materialYouState: Boolean,
//    amoledTheme: Boolean,
//    darkTheme: Boolean,
//    context: Context
//): ColorScheme {
//    val initialColorScheme = when (themeState) {
//        ThemeMode.Light -> if (materialYouState && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            dynamicLightColorScheme(context)
//        } else {
//            lightColors
//        }
//
//        ThemeMode.Dark -> if (materialYouState && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            dynamicDarkColorScheme(context)
//        } else {
//            darkColors
//        }
//
//        ThemeMode.Auto -> if (materialYouState && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        } else {
//            if (darkTheme) darkColors else lightColors
//        }
//    }
//
//    return if (amoledTheme && // Check if AMOLED theme is enabled
//        (themeState == ThemeMode.Dark || themeState == ThemeMode.Auto && darkTheme)
//    ) {
//        initialColorScheme.copy(surface = Color.Black, background = Color.Black)
//    } else {
//        initialColorScheme
//    }
//}
//
//@Composable
//fun AdjustEdgeToEdge(activity: AppCompatActivity, themeState: ThemeMode) {
//    LaunchedEffect(themeState) {
//        if (themeState == ThemeMode.Dark) {
//            activity.enableEdgeToEdge(
//                statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
//                navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
//            )
//        } else {
//            activity.enableEdgeToEdge()
//        }
//    }
//}
@Composable
fun MoneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
//@Composable
//fun GreenStashTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    settingsViewModel: SettingsViewModel,
//    content: @Composable () -> Unit
//) {
//    val context = LocalContext.current
//    val themeState = settingsViewModel.theme.observeAsState(initial = ThemeMode.Auto)
//    val amoledTheme = settingsViewModel.amoledTheme.observeAsState(initial = false)
//    val materialYouState = settingsViewModel.materialYou.observeAsState(
//        initial = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
//    )
//
//    val colorScheme = getColorScheme(
//        themeState = themeState.value,
//        materialYouState = materialYouState.value,
//        amoledTheme = amoledTheme.value,
//        darkTheme = darkTheme,
//        context = context
//    )
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}

