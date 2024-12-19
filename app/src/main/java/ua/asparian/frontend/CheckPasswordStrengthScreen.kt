package ua.asparian.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.asparian.frontend.api.PasswordStrengthRequest
import ua.asparian.frontend.api.RetrofitInstance

@Composable
fun CheckPasswordStrengthScreen(modifier: Modifier = Modifier) {
    var password by remember { mutableStateOf("") }
    var strengthResult by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = "CHECK PASSWORD STRENGTH",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Поле вводу пароля
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter Password", color = MaterialTheme.colorScheme.onSurface) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка перевірки
        Button(
            onClick = {
                if (password.isNotEmpty()) {
                    checkPasswordStrength(password) { result, error ->
                        strengthResult = result
                        errorMessage = error
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Check Strength")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Відображення результату
        strengthResult?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Відображення помилки
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// Функція для виклику API
private fun checkPasswordStrength(password: String, callback: (String?, String?) -> Unit) {
    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.checkPasswordStrength(PasswordStrengthRequest(password))
            val result = """
                Score: ${response.score}
                Warning: ${response.warning}
                Suggestions: ${response.suggestions.joinToString()}
                Crack Time: ${response.crackTimeOfflineSlowHashingDisplay}
            """.trimIndent()
            callback(result, null)
        } catch (e: Exception) {
            callback(null, "Failed to check password strength: ${e.message}")
        }
    }
}
