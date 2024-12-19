package ua.asparian.frontend.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.asparian.frontend.api.RetrofitInstance
import ua.asparian.frontend.api.SavedPassword
import ua.asparian.frontend.data.TokenManager

class SavedPasswordsViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var savedPasswords = mutableStateOf<List<SavedPassword>?>(null)
    var errorMessage = mutableStateOf<String?>(null)

    fun loadSavedPasswords() {
        val token = tokenManager.getToken()
        if (token != null) {
            viewModelScope.launch {
                try {
                    val response = RetrofitInstance.api.getSavedPasswords("Bearer $token")
                    savedPasswords.value = response
                } catch (e: Exception) {
                    errorMessage.value = "Failed to load passwords: ${e.message}"
                }
            }
        } else {
            errorMessage.value = "You are not logged in."
        }
    }

    fun deletePassword(id: String) {
        val token = tokenManager.getToken()
        if (token != null) {
            viewModelScope.launch {
                try {
                    RetrofitInstance.api.deletePassword("Bearer $token", id)
                    savedPasswords.value = savedPasswords.value?.filterNot { it.id == id }
                } catch (e: Exception) {
                    errorMessage.value = "Failed to delete password: ${e.message}"
                }
            }
        }
    }
}