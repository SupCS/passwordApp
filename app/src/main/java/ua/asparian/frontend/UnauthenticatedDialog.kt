package ua.asparian.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UnauthenticatedDialog(
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onNavigateBack,
        title = {
            Text(
                text = "Access Denied",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        },
        text = {
            Text(
                text = "You need to log in to access this page.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        },
        confirmButton = {
            Button(
                onClick = onNavigateToLogin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Go to Login")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onNavigateBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Back")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(16.dp)
    )
}
