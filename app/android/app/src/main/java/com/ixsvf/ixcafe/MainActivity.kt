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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ixsvf.ixcafe.screens.LoginScreen
import com.ixsvf.ixcafe.ui.theme.IxCafeTheme
import com.ixsvf.ixcafe.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IxCafeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val viewModel: LoginViewModel = viewModel()


                    val uiState by viewModel.uiState.collectAsState()


                    LoginScreen(
                        modifier = Modifier.padding(innerPadding),
                        uiState = uiState, // Passa o estado atual
                        onProfileSelected = { profile ->
                            println("Perfil selecionado: ${profile.name}")
                        },
                        onSettingsClicked = {
                            println("Botão de Definições clicado!")
                        },
                        onRetry = {
                            viewModel.fetchProfiles() // Chama a função de retry
                        }
                    )
                }
            }
        }
    }
}



