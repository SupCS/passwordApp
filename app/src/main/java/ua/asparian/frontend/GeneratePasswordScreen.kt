package ua.asparian.frontend

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ua.asparian.frontend.api.PasswordRequest
import ua.asparian.frontend.api.RetrofitInstance
import ua.asparian.frontend.api.SavedPassword
import ua.asparian.frontend.data.TokenManager

@Composable
fun GeneratePasswordScreen(viewModel: MainViewModel) {
    var passwordLength by remember { mutableStateOf(TextFieldValue("12")) }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeLowercase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSpecialCharacters by remember { mutableStateOf(true) }

    var generatedPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showUnauthDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Generate Password",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordLength,
            onValueChange = { passwordLength = it },
            label = { Text("Password Length", color = Color.White) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        CheckboxWithLabel(
            isChecked = includeUppercase,
            label = "Include Uppercase Letters"
        ) { includeUppercase = it }

        CheckboxWithLabel(
            isChecked = includeLowercase,
            label = "Include Lowercase Letters"
        ) { includeLowercase = it }

        CheckboxWithLabel(
            isChecked = includeNumbers,
            label = "Include Numbers"
        ) { includeNumbers = it }

        CheckboxWithLabel(
            isChecked = includeSpecialCharacters,
            label = "Include Special Characters"
        ) { includeSpecialCharacters = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (passwordLength.text.toIntOrNull() == null || passwordLength.text.toInt() < 4) {
                    generatedPassword = "Invalid password length. Must be at least 4."
                } else {
                    scope.launch {
                        isLoading = true
                        try {
                            val response = RetrofitInstance.api.generatePassword(
                                PasswordRequest(
                                    length = passwordLength.text.toInt(),
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
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = if (isLoading) "Loading..." else "Generate", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = generatedPassword,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    if (generatedPassword.isNotEmpty()) {
                        copyToClipboard(context, generatedPassword)
                        Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No password to copy", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = generatedPassword.isNotEmpty()
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Password", modifier = Modifier.size(24.dp))
            }
            IconButton(
                onClick = {
                    if (tokenManager.getToken() != null) {
                        showSaveDialog = true
                    } else {
                        showUnauthDialog = true
                    }
                },
                enabled = generatedPassword.isNotEmpty()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Password", modifier = Modifier.size(24.dp))
            }
        }
    }

    if (showUnauthDialog) {
        UnauthenticatedDialog(
            onNavigateToLogin = {
                showUnauthDialog = false
                viewModel.triggerLoginNavigation()
            },
            onNavigateBack = { showUnauthDialog = false }
        )
    }

    if (showSaveDialog) {
        SavePasswordDialog(
            onSave = { title, username ->
                scope.launch {
                    try {
                        val token = tokenManager.getToken()
                        if (token != null) {
                            val response = RetrofitInstance.api.savePassword(
                                token = "Bearer $token",
                                request = SavedPassword(
                                    title = title,
                                    username = username.takeIf { !it.isNullOrEmpty() } ?: "",
                                    password = generatedPassword
                                )
                            )
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                            showSaveDialog = false
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to save password: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

            },
            onCancel = { showSaveDialog = false }
        )
    }
}

@Composable
fun SavePasswordDialog(onSave: (String, String?) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Save Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(title, if (username.isBlank()) null else username) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CheckboxWithLabel(isChecked: Boolean, label: String, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Copied Password", text)
    clipboard.setPrimaryClip(clip)
}
