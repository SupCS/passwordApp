package ua.asparian.frontend.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object SavedPasswords : BottomNavItem("saved_passwords", Icons.Default.Save)
    object CheckPasswordStrength : BottomNavItem("check_password", Icons.Default.Security)
    object GeneratePassword : BottomNavItem("generate_password", Icons.Default.Add)
    object Info : BottomNavItem("info", Icons.Default.Info)
    object Settings : BottomNavItem("settings", Icons.Default.Settings)
}