package ua.asparian.frontend.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.DELETE
import retrofit2.http.Path

data class PasswordRequest(
    val length: Int,
    val includeUppercase: Boolean,
    val includeLowercase: Boolean,
    val includeNumbers: Boolean,
    val includeSpecialCharacters: Boolean
)

data class PasswordResponse(
    val password: String
)

data class PasswordStrengthRequest(val password: String)

data class PasswordStrengthResponse(
    val score: Int,
    val warning: String,
    val suggestions: List<String>,
    val crackTimeOfflineSlowHashingDisplay: String
)


data class RegisterRequest(val username: String, val password: String)
data class RegisterResponse(val message: String)
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class SavedPassword(
    val id: String? = null,
    val title: String,
    val username: String,
    val password: String
)
data class SavePasswordResponse(val message: String)

interface ApiService {
    @POST("/generate-password")
    suspend fun generatePassword(@Body request: PasswordRequest): PasswordResponse

    @POST("/password-strength")
    suspend fun checkPasswordStrength(@Body request: PasswordStrengthRequest): PasswordStrengthResponse

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("/saved-passwords")
    suspend fun getSavedPasswords(
        @Header("Authorization") token: String
    ): List<SavedPassword>

    @POST("/save-password")
    suspend fun savePassword(
        @Header("Authorization") token: String,
        @Body request: SavedPassword
    ): SavePasswordResponse

    @DELETE("/delete-password/{id}")
    suspend fun deletePassword(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): SavePasswordResponse
}
