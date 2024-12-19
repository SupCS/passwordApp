package ua.asparian.frontend.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove("jwt_token").apply()
    }

    fun getUsername(): String? {
        val token = getToken() ?: return "Guest"
        val payload = token.split(".")[1] // Отримуємо payload токена
        val decodedPayload = String(android.util.Base64.decode(payload, android.util.Base64.DEFAULT))
        return JSONObject(decodedPayload).optString("username", "Unknown User")
    }

    fun logToken() {
        val token = getToken()
        android.util.Log.d("TokenManager", "Current Token: $token")
    }

}
