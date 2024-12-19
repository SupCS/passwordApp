package ua.asparian.frontend.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.asparian.frontend.api.PasswordStrengthRequest
import ua.asparian.frontend.api.RetrofitInstance

class CheckPasswordStrengthViewModel : ViewModel() {
    var password by mutableStateOf("")
    var strengthResult by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun checkStrength() {
        if (password.isEmpty()) {
            errorMessage = "Please enter a password"
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.checkPasswordStrength(PasswordStrengthRequest(password))
                strengthResult = """
                    Score: ${response.score}
                    Warning: ${response.warning}
                    Suggestions: ${response.suggestions.joinToString()}
                    Crack Time: ${response.crackTimeOfflineSlowHashingDisplay}
                """.trimIndent()
                errorMessage = null
            } catch (e: Exception) {
                strengthResult = null
                errorMessage = "Failed to check password strength: ${e.message}"
            }
        }
    }
}
