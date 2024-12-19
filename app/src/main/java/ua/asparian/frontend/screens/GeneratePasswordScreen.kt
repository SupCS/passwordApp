package ua.asparian.frontend.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.asparian.frontend.MainViewModel
import ua.asparian.frontend.SavePasswordDialog
import ua.asparian.frontend.UnauthenticatedDialog
import ua.asparian.frontend.viewmodels.GeneratePasswordViewModel

@Composable
fun GeneratePasswordScreen(viewModel: GeneratePasswordViewModel, mainViewModel: MainViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок
        Text(
            text = "Generate Password",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле для довжини пароля
        OutlinedTextField(
            value = viewModel.passwordLength,
            onValueChange = {
                if ((it.toIntOrNull() ?: 0) <= 64) { // Максимальне значення — 64
                    viewModel.passwordLength = it
                }
            },
            label = { Text("Password Length") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Тогли для опцій пароля
        ToggleWithLabel(
            isChecked = viewModel.includeUppercase,
            label = "Include Uppercase Letters",
            onCheckedChange = { viewModel.includeUppercase = it }
        )
        ToggleWithLabel(
            isChecked = viewModel.includeLowercase,
            label = "Include Lowercase Letters",
            onCheckedChange = { viewModel.includeLowercase = it }
        )
        ToggleWithLabel(
            isChecked = viewModel.includeNumbers,
            label = "Include Numbers",
            onCheckedChange = { viewModel.includeNumbers = it }
        )
        ToggleWithLabel(
            isChecked = viewModel.includeSpecialCharacters,
            label = "Include Special Characters",
            onCheckedChange = { viewModel.includeSpecialCharacters = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка генерації пароля
        Button(
            onClick = { viewModel.generatePassword() },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (viewModel.isLoading) "Loading..." else "Generate",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Відображення згенерованого пароля
        Text(
            text = viewModel.generatedPassword,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Кнопки копіювання та збереження пароля
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    if (viewModel.generatedPassword.isNotEmpty()) {
                        copyToClipboard(context, viewModel.generatedPassword)
                        Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No password to copy", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = viewModel.generatedPassword.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Password",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = {
                    if (viewModel.tokenManager.getToken() != null) {
                        viewModel.showSaveDialog = true
                    } else {
                        viewModel.showUnauthDialog = true
                    }
                },
                enabled = viewModel.generatedPassword.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save Password",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }

    if (viewModel.showUnauthDialog) {
        UnauthenticatedDialog(
            onNavigateToLogin = {
                viewModel.showUnauthDialog = false
                mainViewModel.triggerLoginNavigation()
                                },
            onNavigateBack = { viewModel.showUnauthDialog = false }
        )
    }

    // Діалог збереження пароля
    if (viewModel.showSaveDialog) {
        SavePasswordDialog(
            onSave = { title, username -> viewModel.savePassword(title, username) },
            onCancel = { viewModel.showSaveDialog = false }
        )
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Copied Password", text)
    clipboard.setPrimaryClip(clip)
}


@Composable
fun ToggleWithLabel(
    isChecked: Boolean,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(48.dp, 32.dp)
                .background(
                    color = if (isChecked) Color(0xFFFFEA03) else Color(0xFF252525),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(
                    onClick = { onCheckedChange(!isChecked) },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 4.dp)
                    .align(if (isChecked) Alignment.CenterEnd else Alignment.CenterStart)
            )
        }
    }
}

