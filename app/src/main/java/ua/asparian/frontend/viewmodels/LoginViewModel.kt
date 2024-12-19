package ua.asparian.frontend.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import ua.asparian.frontend.api.LoginRequest
import ua.asparian.frontend.api.RetrofitInstance
import ua.asparian.frontend.data.TokenManager

class LoginViewModel(private val tokenManager: TokenManager) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun login(onSuccess: () -> Unit) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage = "Please fill in all fields"
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(LoginRequest(username, password))
                tokenManager.saveToken(response.token)
                errorMessage = null
                onSuccess()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val json = JSONObject(errorBody ?: "{}")
                errorMessage = json.optString("error", "Failed to login")
            } catch (e: Exception) {
                errorMessage = "An unexpected error occurred: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
