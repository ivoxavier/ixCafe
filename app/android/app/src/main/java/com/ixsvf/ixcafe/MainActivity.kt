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

                    // 2. O NavHost "ouve" o navController e desenha o ecrã certo
                    NavHost(
                        navController = navController,
                        startDestination = IxCafeConstants.NAV_ROUTES.LOGIN_SCREEN, // O ecrã inicial
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // --- Rota 1: Login Screen ---
                        composable(IxCafeConstants.NAV_ROUTES.LOGIN_SCREEN) {
                            // O ViewModel agora é criado aqui,
                            // pois pertence a este ecrã
                            val viewModel: LoginViewModel = viewModel()
                            val uiState by viewModel.uiState.collectAsState()

                            LoginScreen(
                                uiState = uiState,
                                onProfileSelected = { profile ->
                                    if (profile.name == IxCafeConstants.APP_SETTINGS.DEMO_KEY) {
                                        // Navega para o ecrã DEMO
                                        navController.navigate("authDemo/${profile.name}")
                                    } else {
                                        // Navega para o ecrã REAL
                                        navController.navigate("auth/${profile.name}")
                                    }
                                },
                                onSettingsClicked = {
                                    println("Settings Clicado!")
                                    // navController.navigate("settings")
                                },
                                onRetry = {
                                    viewModel.fetchProfiles()
                                }
                            )
                        }

                        // --- Rota 2: Auth Screen (Demo) ---
                        composable(
                            route = "authDemo/{profileName}", // Argumento na rota
                            arguments = listOf(navArgument("profileName") { type = NavType.StringType })
                        ) { backStackEntry ->

                            val name = backStackEntry.arguments?.getString("profileName") ?: "Demonstração"

                            AuthScreenDemo(
                                profile = UserProfile(name = name, role = "Empregado"),
                                onCorrectPin = {
                                    // SUCESSO: Vai para as mesas
                                    navController.navigate(IxCafeConstants.NAV_ROUTES.TABLE_SCREEN) {
                                        popUpTo(IxCafeConstants.NAV_ROUTES.LOGIN_SCREEN) { inclusive = true }
                                    }
                                },
                                onNavigateBack = {
                                    navController.popBackStack() // Volta para o login
                                }
                            )
                        }

                        // --- Rota 3: Auth Screen (Real - Placeholder) ---
                        composable(
                            route = "auth/{profileId}",
                            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
                        ) {
                            val id = it.arguments?.getString("profileId")
                            Text("Ecrã de Auth Real para o ID: $id")
                            // ... aqui chamaria o seu AuthScreen real
                        }

                        // --- Rota 4: Mesas (Placeholder) ---
                        composable(IxCafeConstants.NAV_ROUTES.TABLE_SCREEN) {
                            TablesScreenDemo(
                                onTableClick = { table ->
                                    // Navega para o ecrã de pedidos, passando o ID da mesa
                                    navController.navigate("order/${table.id}")
                                }
                            )
                        }

                        // --- Rota 5: Ecrã de Pedidos (Nova) ---
                        composable(
                            route = "order/{tableId}",
                            arguments = listOf(navArgument("tableId") { type = NavType.StringType })
                        ) { backStackEntry ->

                            val tableId = backStackEntry.arguments?.getString("tableId") ?: "???"

                            OrderScreenDemo(
                                tableId = tableId,
                                // O parâmetro agora é este, e deve voltar atrás
                                onNavigateBackToTables = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}