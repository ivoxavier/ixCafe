package com.ixsvf.ixcafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.screens.LoginScreen
import com.ixsvf.ixcafe.screens.SettingsAuthScreen
import com.ixsvf.ixcafe.screens.SettingsScreen
import com.ixsvf.ixcafe.screens.demo.AuthScreenDemo // O Demo
// import com.ixsvf.ixcafe.screens.AuthScreen // <-- O REAL (Descomente quando criar o ficheiro do Passo 4)
import com.ixsvf.ixcafe.screens.demo.OrderScreenDemo
import com.ixsvf.ixcafe.screens.demo.TablesScreenDemo
import com.ixsvf.ixcafe.services.repository.EmpregadosProfile
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme
import com.ixsvf.ixcafe.viewmodel.LoginViewModel
// import com.ixsvf.ixcafe.viewmodel.AuthViewModel // <-- Import do novo VM

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IxCafeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = IxCafeConstants.NAVROUTES.LOGIN_SCREEN,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // --- Rota 1: Login Screen ---
                        composable(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                            val viewModel: LoginViewModel = viewModel()
                            val uiState by viewModel.uiState.collectAsState()

                            LoginScreen(
                                uiState = uiState,
                                onProfileSelected = { profile ->
                                    if (profile.name == IxCafeConstants.APPSETTINGS.DEMO_KEY) {
                                        // Rota Demo
                                        navController.navigate("authDemo/${profile.name}")
                                    } else {
                                        // Rota Real (Passamos o ID)
                                        navController.navigate("auth/${profile.id}")
                                    }
                                },
                                onSettingsClicked = {
                                    navController.navigate("settingsAuth")
                                },
                                onRetry = {
                                    viewModel.fetchProfiles()
                                }
                            )
                        }

                        // --- Rota 2: Auth Screen (Demo - PIN 1234) ---
                        composable(
                            route = "authDemo/{profileName}",
                            arguments = listOf(navArgument("profileName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("profileName") ?: "Demonstração"

                            // Para demo, criamos um perfil fictício
                            AuthScreenDemo(
                                profile = EmpregadosProfile(id = "demo", name = name, role = "Empregado"),
                                onCorrectPin = {
                                    navController.navigate(IxCafeConstants.NAVROUTES.TABLE_SCREEN) {
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- Rota 3: Auth Screen (REAL - Firebase/Room) ---
                        composable(
                            route = "auth/{profileId}",
                            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("profileId") ?: ""

                            // AQUI ENTRA O AuthScreen REAL
                            // Como ainda não tem o ficheiro criado (Passo 4), deixo comentado a lógica correta
                            // e um placeholder para não dar erro de compilação agora.

                            /*
                            AuthScreen(
                                profileId = id,
                                onLoginSuccess = {
                                    navController.navigate(IxCafeConstants.NAV_ROUTES.TABLE_SCREEN) {
                                        popUpTo(IxCafeConstants.NAV_ROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                            */

                            // Placeholder temporário até criar o AuthScreen.kt
                            Text("Autenticação REAL para ID: $id. \n(Crie o AuthScreen.kt e AuthViewModel.kt para funcionar)")
                        }

                        // --- Rota 4: Mesas ---
                        composable(IxCafeConstants.NAVROUTES.TABLE_SCREEN) {
                            TablesScreenDemo(
                                onTableClick = { table ->
                                    navController.navigate("order/${table.id}")
                                }
                            )
                        }

                        // --- Rota 5: Pedidos ---
                        composable(
                            route = "order/{tableId}",
                            arguments = listOf(navArgument("tableId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val tableId = backStackEntry.arguments?.getString("tableId") ?: "???"

                            OrderScreenDemo(
                                tableId = tableId,
                                onNavigateBackToTables = {
                                    navController.popBackStack()
                                },
                                onCloseAccount = {
                                    println("Conta da mesa $tableId fechada!")
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- Rota 6: Auth Definições ---
                        composable("settingsAuth") {
                            SettingsAuthScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onLoginSuccess = { user, pass ->
                                    navController.navigate("settingsMain") {
                                        popUpTo("settingsAuth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Rota 7: Definições Principal ---
                        composable("settingsMain") {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.navigate(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateToProfileManagement = { navController.navigate("profileManagement") },
                                onNavigateToTableManagement = { navController.navigate("tableManagement") }
                            )
                        }

                        // --- Rotas Futuras ---
                        composable("profileManagement") { Text("Gestão de Perfis") }
                        composable("tableManagement") { Text("Gestão de Mesas") }
                    }
                }
            }
        }
    }
}