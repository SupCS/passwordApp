package ua.asparian.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ua.asparian.frontend.data.TokenManager

@Composable
fun SettingsScreen(
    onLogout: () -> Unit, // Callback для виходу
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)

    val username = tokenManager.getUsername()
    var confirmationVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Відображення логіну
        Text(text = "Logged in as: $username", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка виходу
        Button(
            onClick = { confirmationVisible = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }

// Діалог підтвердження виходу
        if (confirmationVisible) {
            AlertDialog(
                onDismissRequest = { confirmationVisible = false },
                title = {
                    Text(
                        text = "Log Out",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmationVisible = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { confirmationVisible = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 6.dp
            )
        }

    }
}
