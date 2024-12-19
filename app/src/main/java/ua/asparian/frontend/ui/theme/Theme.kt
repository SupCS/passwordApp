package ua.asparian.frontend.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = YellowFFEA03,            // Жовтий для кнопок
    background = DarkGray383838,       // Темний фон
    surface = LightGrayD9D9D9,         // Фон інпутів
    onSurface = PlaceholderGray050505, // Плейсхолдери в інпутах
)

@Composable
fun FrontendTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
