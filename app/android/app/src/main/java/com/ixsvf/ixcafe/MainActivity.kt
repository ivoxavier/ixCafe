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
import com.ixsvf.ixcafe.screens.SettingsAuthScreen
import com.ixsvf.ixcafe.screens.SettingsScreen
import com.ixsvf.ixcafe.screens.TableManagementScreen
import com.ixsvf.ixcafe.screens.TablesScreen
import com.ixsvf.ixcafe.screens.demo.AuthScreenDemo
import com.ixsvf.ixcafe.screens.demo.OrderScreenDemo
import com.ixsvf.ixcafe.screens.demo.TablesScreenDemo
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

                    // 1. Criar o SessionViewModel ao nível da Activity
                    val sessionViewModel: SessionViewModel = viewModel()

                    // 2. Observar se devemos fazer logout forçado
                    val shouldLogout by sessionViewModel.shouldLogout.collectAsState()

                    // 3. Reação ao Logout Forçado (A magia acontece aqui!)
                    LaunchedEffect(shouldLogout) {
                        if (shouldLogout) {
                            // Limpa a pilha e vai para o login
                            navController.navigate(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                                popUpTo(0) { inclusive = true } // Limpa TUDO
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

                        composable("auth/{profileId}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("profileId") ?: ""

                            AuthScreen(
                                profileId = id,
                                onLoginSuccess = {
                                    // --- AQUI: AVISAR A SESSÃO QUE O LOGIN OCORREU ---
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
                            // Injetar ViewModel das Mesas
                            val viewModel: TablesViewModel = viewModel()
                            val uiState by viewModel.uiState.collectAsState()

                            TablesScreen(
                                uiState = uiState,
                                onRefresh = { viewModel.forceRefresh() }, // Liga ao pull-to-refresh
                                onTableClick = { mesa ->
                                    // Passar ID da mesa para o ecrã de pedidos
                                    // IMPORTANTE: O OrderScreen ainda é 'demo', mas o ID é real
                                    navController.navigate("order/${mesa.id}")
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
                                    // Esta rota TEM de existir no NavHost
                                    navController.navigate("settingsMain") {
                                        popUpTo("settingsAuth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- ADICIONE ESTA ROTA SE FALTAR ---
                        composable("settingsMain") {
                            SettingsScreen(
                                onNavigateBack = {
                                    // Volta ao login
                                    navController.navigate(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateToProfileManagement = {
                                    navController.navigate("profileManagement")
                                },
                                onNavigateToTableManagement = {
                                    navController.navigate("tableManagement")
                                },
                                onNavigateToProductManagement = {
                                    navController.navigate("productManagement")
                                }
                            )
                        }

                        // --- Rota 7: Definições Principal ---
                        // --- Rota 7: Ecrã Principal de Definições ---
                        // --- NOVA ROTA: Gestão de Mesas (O destino) ---
                        composable("tableManagement") {
                            // 1. Criar o ViewModel
                            val viewModel: TableManagementViewModel = viewModel()
                            val uiState by viewModel.uiState.collectAsState()

                            // 2. Mostrar o Ecrã
                            TableManagementScreen(
                                uiState = uiState,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onCreateMesa = { num, cap, loc ->
                                    viewModel.criarMesa(num, cap, loc)
                                },
                                onClearMessages = {
                                    viewModel.clearMessages()
                                }
                            )
                        }

                        // --- NOVA ROTA: Gestão de Mesas (O destino) ---
                        composable("tableManagement") {
                            // 1. Criar o ViewModel
                            val viewModel: TableManagementViewModel = viewModel()
                            val uiState by viewModel.uiState.collectAsState()

                            // 2. Mostrar o Ecrã
                            TableManagementScreen(
                                uiState = uiState,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onCreateMesa = { num, cap, loc ->
                                    viewModel.criarMesa(num, cap, loc)
                                },
                                onClearMessages = {
                                    viewModel.clearMessages()
                                }
                            )
                        }

                        // --- Rotas Futuras ---
                        composable("profileManagement") { Text("Gestão de Perfis") }

                    }
                }
            }
        }
    }
}