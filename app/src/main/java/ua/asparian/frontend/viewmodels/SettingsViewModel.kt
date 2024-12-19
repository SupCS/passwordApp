package ua.asparian.frontend.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import ua.asparian.frontend.data.TokenManager

class SettingsViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var username by mutableStateOf(tokenManager.getUsername())
    var isLogoutDialogVisible by mutableStateOf(false)

    fun showLogoutDialog() {
        isLogoutDialogVisible = true
    }

    fun hideLogoutDialog() {
        isLogoutDialogVisible = false
    }

    fun logout(onLogout: () -> Unit) {
        tokenManager.clearToken()
        onLogout()
    }
}
