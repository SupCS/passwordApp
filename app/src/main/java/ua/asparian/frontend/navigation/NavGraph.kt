package ua.asparian.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ua.asparian.frontend.*
import ua.asparian.frontend.data.TokenManager
import ua.asparian.frontend.screens.LoginScreen
import ua.asparian.frontend.screens.RegisterScreen
import ua.asparian.frontend.viewmodels.LoginViewModel
import ua.asparian.frontend.viewmodels.RegisterViewModel

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier, mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val startDestination = if (tokenManager.getToken() != null) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Логін та реєстрація
        composable("login") {
            val viewModel = remember {
                LoginViewModel(tokenManager)
            }

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onGuestContinue = { navController.navigate("main") },
                onRegisterClick = { navController.navigate("register") }
            )
        }


        composable("register") {
            val viewModel = remember { RegisterViewModel() }
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackToLoginClick = { navController.popBackStack() }
            )
        }

        // Основний екран додатку
        composable("main") {
            MainScreen(
                mainViewModel = mainViewModel,
                onLogout = {
                    tokenManager.clearToken()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}
