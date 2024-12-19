package ua.asparian.frontend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import ua.asparian.frontend.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Відображення логіну
        Text(
            text = "Logged in as: ${viewModel.username}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка виходу
        Button(
            onClick = { viewModel.showLogoutDialog() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }

        // Діалог підтвердження виходу
        if (viewModel.isLogoutDialogVisible) {
            AlertDialog(
                onDismissRequest = { viewModel.hideLogoutDialog() },
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
                            viewModel.hideLogoutDialog()
                            viewModel.logout(onLogout)
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
                        onClick = { viewModel.hideLogoutDialog() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
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
