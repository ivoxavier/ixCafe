package com.ixsvf.ixcafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.ixsvf.ixcafe.screens.SettingsScreen // <-- NOVO IMPORT
import com.ixsvf.ixcafe.screens.demo.AuthScreenDemo
import com.ixsvf.ixcafe.screens.demo.OrderScreenDemo
import com.ixsvf.ixcafe.screens.demo.TablesScreenDemo
import com.ixsvf.ixcafe.services.repository.model.UserProfile
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme
import com.ixsvf.ixcafe.viewmodel.LoginViewModel

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
                                        navController.navigate("authDemo/${profile.name}")
                                    } else {
                                        navController.navigate("auth/${profile.name}")
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
                            arguments = listOf(navArgument("profileName") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("profileName") ?: "Demonstração"

                            AuthScreenDemo(
                                profile = UserProfile(name = name, role = "Empregado"),
                                onCorrectPin = {
                                    navController.navigate(IxCafeConstants.NAVROUTES.TABLE_SCREEN) {
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- Rota 3: Auth Screen (Real - Placeholder) ---
                        composable(
                            route = "auth/{profileId}",
                            arguments = listOf(navArgument("profileId") {
                                type = NavType.StringType
                            })
                        ) {
                            val id = it.arguments?.getString("profileId")
                            Text("Ecrã de Auth Real para o ID: $id")
                        }

                        // --- Rota 4: Mesas (Demo) ---
                        composable(IxCafeConstants.NAVROUTES.TABLE_SCREEN) {
                            TablesScreenDemo(
                                onTableClick = { table ->
                                    navController.navigate("order/${table.id}")
                                }
                            )
                        }

                        // --- Rota 5: Ecrã de Pedidos (Demo) ---
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

                        // --- Rota 6: Autenticação das Definições ---
                        composable("settingsAuth") {
                            SettingsAuthScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onLoginSuccess = { user, pass ->
                                    navController.navigate("settingsMain") {
                                        popUpTo("settingsAuth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Rota 7: Ecrã Principal de Definições (ALTERADO) ---
                        composable("settingsMain") {
                            SettingsScreen(
                                onNavigateBack = {
                                    // Volta ao ecrã de login
                                    navController.navigate(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) {
                                        // Limpa a pilha de navegação até ao login
                                        popUpTo(IxCafeConstants.NAVROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateToProfileManagement = {
                                    navController.navigate("profileManagement")
                                },
                                onNavigateToTableManagement = {
                                    navController.navigate("tableManagement")
                                }
                            )
                        }

                        // --- NOVAS ROTAS 8 e 9 (Placeholders) ---
                        composable("profileManagement") {
                            // TODO: Criar o ecrã de gestão de perfis
                            Text("Ecrã de Gestão de Perfis")
                        }

                        composable("tableManagement") {
                            // TODO: Criar o ecrã de gestão de mesas
                            Text("Ecrã de Gestão de Mesas")
                        }
                    }
                }
            }
        }
    }
}