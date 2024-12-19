package ua.asparian.frontend.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle( // Для головного заголовка
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 44.sp
    ),
    bodyLarge = TextStyle( // Основний текст
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    )
)
