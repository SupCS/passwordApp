package ua.asparian.frontend

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ua.asparian.frontend.api.RetrofitInstance
import ua.asparian.frontend.api.SavedPassword
import ua.asparian.frontend.data.TokenManager
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material.icons.filled.Delete



@Composable
fun SavedPasswordsScreen() {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token = tokenManager.getToken()
    var savedPasswords by remember { mutableStateOf<List<SavedPassword>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Завантаження паролів
    LaunchedEffect(Unit) {
        if (token != null) {
            scope.launch {
                try {
                    val response = RetrofitInstance.api.getSavedPasswords("Bearer $token")
                    savedPasswords = response
                } catch (e: Exception) {
                    errorMessage = "Failed to load passwords: ${e.message}"
                }
            }
        } else {
            errorMessage = "You are not logged in."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Saved Passwords", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            savedPasswords != null -> {
                LazyColumn {
                    items(savedPasswords!!) { password ->
                        PasswordTile(
                            id = password.id ?: "",
                            title = password.title,
                            username = password.username,
                            password = password.password,
                            onDelete = { id ->
                                scope.launch {
                                    try {
                                        if (token != null) {
                                            RetrofitInstance.api.deletePassword("Bearer $token", id)
                                            savedPasswords = savedPasswords?.filterNot { it.id == id }
                                            Toast.makeText(context, "Password deleted", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
            errorMessage != null -> {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            else -> {
                Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}



@Composable
fun PasswordTile(id: String, title: String, username: String, password: String, onDelete: (String) -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF383838))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { onDelete(id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Password",
                        tint = Color.Red
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            SelectionContainer {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(username))
                    },
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SelectionContainer {
                    Text(
                        text = if (isPasswordVisible) password else "*".repeat(password.length),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier.clickable {
                            clipboardManager.setText(AnnotatedString(password))
                        }
                    )
                }
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
