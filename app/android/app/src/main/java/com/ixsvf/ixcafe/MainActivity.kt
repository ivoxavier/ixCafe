package com.ixsvf.ixcafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.ixsvf.ixcafe.screens.AuthScreen
import com.ixsvf.ixcafe.screens.LoginScreen
import com.ixsvf.ixcafe.screens.OrderScreen // <-- IMPORT CORRETO (O REAL)
import com.ixsvf.ixcafe.screens.SettingsAuthScreen
import com.ixsvf.ixcafe.screens.SettingsScreen
import com.ixsvf.ixcafe.screens.TableManagementScreen
import com.ixsvf.ixcafe.screens.TablesScreen
import com.ixsvf.ixcafe.screens.demo.AuthScreenDemo
// import com.ixsvf.ixcafe.screens.demo.OrderScreenDemo <-- REMOVIDO
import com.ixsvf.ixcafe.services.repository.model.EmpregadosProfile
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme
import com.ixsvf.ixcafe.viewmodel.LoginViewModel
import com.ixsvf.ixcafe.viewmodel.SessionViewModel
import com.ixsvf.ixcafe.viewmodel.TableManagementViewModel
import com.ixsvf.ixcafe.viewmodel.TablesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IxCafeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()
                    val sessionViewModel: SessionViewModel = viewModel()
                    val shouldLogout by sessionViewModel.shouldLogout.collectAsState()

                    LaunchedEffect(shouldLogout) {
                        if (shouldLogout) {
                            navController.navigate(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                                popUpTo(0) { inclusive = true }
                            }
                            sessionViewModel.onLogoutCompleted()
                        }
                    }

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
                                        navController.navigate("authDemo/${profile.name}")
                                    } else {
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

                        // --- Rota 2: Auth Screen (Demo) ---
                        composable(
                            route = "authDemo/{profileName}",
                            arguments = listOf(navArgument("profileName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("profileName") ?: "Demonstração"
                            AuthScreenDemo(
                                profile = EmpregadosProfile(id = "demo", name = name, role = "Empregado"),
                                onCorrectPin = {
                                    navController.navigate(IxCafeConstants.NAVROUTES.TABLE_SCREEN) {
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // --- Rota 3: Auth Screen (Real) ---
                        composable("auth/{profileId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("profileId") ?: ""
                            AuthScreen(
                                profileId = id,
                                onLoginSuccess = {
                                    sessionViewModel.startSession(id)
                                    navController.navigate(IxCafeConstants.NAVROUTES.TABLE_SCREEN) {
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // --- Rota 4: Mesas ---
                        composable(IxCafeConstants.NAVROUTES.TABLE_SCREEN) {
                            val viewModel: TablesViewModel = viewModel()
                            val uiState by viewModel.uiState.collectAsState()

                            TablesScreen(
                                uiState = uiState,
                                onRefresh = { viewModel.forceRefresh() },
                                onTableClick = { mesa ->
                                    navController.navigate("order/${mesa.id}")
                                }
                            )
                        }

                        // --- Rota 5: Pedidos (AGORA O REAL) ---
                        composable(
                            route = "order/{tableId}",
                            arguments = listOf(navArgument("tableId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val tableId = backStackEntry.arguments?.getString("tableId") ?: "???"

                            // AQUI ESTAVA O ERRO: Usava OrderScreenDemo
                            // AGORA USA O REAL:
                            OrderScreen(
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
                                onLoginSuccess = { _, _ ->
                                    navController.navigate("settingsMain") {
                                        popUpTo("settingsAuth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Rota 7: Ecrã Principal de Definições ---
                        composable("settingsMain") {
                            SettingsScreen(
                                onNavigateBack = {
                                    navController.navigate(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateToProfileManagement = { navController.navigate("profileManagement") },
                                onNavigateToTableManagement = { navController.navigate("tableManagement") },
                                onNavigateToProductManagement = { navController.navigate("productManagement") }
                            )
                        }

                        // --- Rota 8: Gestão de Mesas ---
                        composable("tableManagement") {
                            val viewModel: TableManagementViewModel = viewModel()
                            val uiState by viewModel.uiState.collectAsState()

                            TableManagementScreen(
                                uiState = uiState,
                                onNavigateBack = { navController.popBackStack() },
                                onCreateMesa = { num, cap, loc -> viewModel.criarMesa(num, cap, loc) },
                                onClearMessages = { viewModel.clearMessages() }
                            )
                        }

                        // --- Rotas Futuras ---
                        composable("profileManagement") { Text("Gestão de Perfis") }
                        composable("productManagement") { Text("Gestão de Produtos") }
                    }
                }
            }
        }
    }
}