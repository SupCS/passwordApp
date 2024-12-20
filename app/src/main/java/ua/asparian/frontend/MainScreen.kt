package ua.asparian.frontend

import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ua.asparian.frontend.navigation.BottomNavItem
import ua.asparian.frontend.data.TokenManager
import ua.asparian.frontend.screens.CheckPasswordStrengthScreen
import ua.asparian.frontend.screens.GeneratePasswordScreen
import ua.asparian.frontend.screens.InfoScreen
import ua.asparian.frontend.screens.SavedPasswordsScreen
import ua.asparian.frontend.screens.SettingsScreen
import ua.asparian.frontend.viewmodels.CheckPasswordStrengthViewModel
import ua.asparian.frontend.viewmodels.GeneratePasswordViewModel
import ua.asparian.frontend.viewmodels.SavedPasswordsViewModel
import ua.asparian.frontend.viewmodels.SettingsViewModel

@Composable
fun MainScreen(onLogout: () -> Unit, mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    var showUnauthDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                tokenManager = tokenManager,
                onUnauthorizedAccess = { showUnauthDialog = true }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.GeneratePassword.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.GeneratePassword.route) {
                val generatePasswordViewModel = remember { GeneratePasswordViewModel(tokenManager) }
                GeneratePasswordScreen(viewModel = generatePasswordViewModel, mainViewModel=mainViewModel)
            }

            composable(BottomNavItem.CheckPasswordStrength.route) {
                val checkPasswordStrengthViewModel = remember { CheckPasswordStrengthViewModel() }
                CheckPasswordStrengthScreen(viewModel = checkPasswordStrengthViewModel)
            }
            composable(BottomNavItem.SavedPasswords.route) {
                if (tokenManager.getToken() == null) {
                    showUnauthDialog = true
                } else {
                    val savedPasswordsViewModel = remember { SavedPasswordsViewModel(tokenManager) }
                    SavedPasswordsScreen(viewModel = savedPasswordsViewModel)
                }
            }

            composable(BottomNavItem.Info.route) {
                InfoScreen()
            }
            composable(BottomNavItem.Settings.route) {
                val settingsViewModel = remember { SettingsViewModel(TokenManager(context)) }

                SettingsScreen(
                    viewModel = settingsViewModel,
                    onLogout = onLogout
                )
            }

        }
    }

    if (showUnauthDialog) {
        UnauthenticatedDialog(
            onNavigateToLogin = {
                showUnauthDialog = false
                onLogout()
            },
            onNavigateBack = {
                showUnauthDialog = false
                navController.navigate(BottomNavItem.GeneratePassword.route) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                }
            }
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    tokenManager: TokenManager,
    onUnauthorizedAccess: () -> Unit
) {
    val items = listOf(
        BottomNavItem.SavedPasswords,
        BottomNavItem.CheckPasswordStrength,
        BottomNavItem.GeneratePassword,
        BottomNavItem.Info,
        BottomNavItem.Settings
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF252525)) // Темний фон для навбару
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEachIndexed { index, item ->
            val isSelected = currentRoute == item.route

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(if (isSelected) Color(0xFFFFEA03) else Color(0xFF252525))
                    .clickable {
                        if (item == BottomNavItem.SavedPasswords && tokenManager.getToken() == null) {
                            onUnauthorizedAccess()
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp) // Розмір іконок
                        .align(Alignment.Center)
                )
            }

            if (index < items.size - 1) {
                Divider(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}
