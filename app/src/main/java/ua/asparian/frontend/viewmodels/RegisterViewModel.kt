package ua.asparian.frontend.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import ua.asparian.frontend.api.RegisterRequest
import ua.asparian.frontend.api.RetrofitInstance

class RegisterViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun register(onSuccess: () -> Unit) {
        if (username.isEmpty() || password.isEmpty()) {
            errorMessage = "Please fill in all fields"
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.register(RegisterRequest(username, password))
                if (response.message == "User registered successfully") {
                    errorMessage = null
                    onSuccess()
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                errorMessage = try {
                    val json = JSONObject(errorBody ?: "")
                    json.getString("error")
                } catch (ex: Exception) {
                    "Registration failed with code ${e.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Registration failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
