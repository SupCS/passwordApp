package ua.asparian.frontend.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.asparian.frontend.api.PasswordRequest
import ua.asparian.frontend.api.RetrofitInstance
import ua.asparian.frontend.api.SavedPassword
import ua.asparian.frontend.data.TokenManager

class GeneratePasswordViewModel(val tokenManager: TokenManager) : ViewModel() {
    var passwordLength by mutableStateOf("12")
    var includeUppercase by mutableStateOf(true)
    var includeLowercase by mutableStateOf(true)
    var includeNumbers by mutableStateOf(true)
    var includeSpecialCharacters by mutableStateOf(true)

    var generatedPassword by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var showSaveDialog by mutableStateOf(false)
    var saveErrorMessage by mutableStateOf<String?>(null)
    var showUnauthDialog by mutableStateOf(false)

    fun generatePassword() {
        if (passwordLength.toIntOrNull() == null || passwordLength.toInt() < 4) {
            generatedPassword = "Invalid password length. Must be at least 4."
            return
        }
        if (!includeUppercase && !includeLowercase && !includeNumbers && !includeSpecialCharacters) {
            generatedPassword = "At least one character type must be selected."
            return
        }
        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.generatePassword(
                    PasswordRequest(
                        length = passwordLength.toInt(),
                        includeUppercase = includeUppercase,
                        includeLowercase = includeLowercase,
                        includeNumbers = includeNumbers,
                        includeSpecialCharacters = includeSpecialCharacters
                    )
                )
                generatedPassword = response.password
            } catch (e: Exception) {
                generatedPassword = "Failed to generate password: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun savePassword(title: String, username: String?) {
        val token = tokenManager.getToken()
        if (token == null) {
            saveErrorMessage = "You need to be logged in to save a password."
            showUnauthDialog = true
            return
        }
        viewModelScope.launch {
            try {
                RetrofitInstance.api.savePassword(
                    token = "Bearer $token",
                    request = SavedPassword(
                        title = title,
                        username = username ?: "",
                        password = generatedPassword
                    )
                )
                saveErrorMessage = null
                showSaveDialog = false
            } catch (e: Exception) {
                saveErrorMessage = "Failed to save password: ${e.message}"
            }
        }
    }
}
