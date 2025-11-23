package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState // <-- IMPORTANTE
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // <-- IMPORTANTE
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ixsvf.ixcafe.BuildConfig
import com.ixsvf.ixcafe.R
import com.ixsvf.ixcafe.constants.IxCafeConstants
import com.ixsvf.ixcafe.screens.components.HorizontalSpace
import com.ixsvf.ixcafe.screens.components.LargeTitleText
import com.ixsvf.ixcafe.screens.components.MediumBodyText
import com.ixsvf.ixcafe.screens.components.VerticalSpace
import com.ixsvf.ixcafe.services.repository.model.EmpregadosProfile
import com.ixsvf.ixcafe.viewmodel.LoginUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onProfileSelected: (EmpregadosProfile) -> Unit,
    onSettingsClicked: () -> Unit,
    onRetry: () -> Unit
) {

    val demoProfile = EmpregadosProfile(
        id = "1",
        name = IxCafeConstants.APPSETTINGS.DEMO_KEY,
        role = "Empregado"
    )

    Box(modifier = modifier.fillMaxSize()) {

        // --- 1. O CONTEÚDO COM SWIPE ---
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { onRetry() },
            modifier = Modifier.fillMaxSize()
        ) {
            // Adicionamos um Box aqui para garantir que o conteudo ocupa o ecra todo
            // e permite o scroll funcionar corretamente
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // <--- O SCROLL É AQUI (no pai)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa a largura, a altura é definida pelo conteudo
                        .padding(horizontal = 16.dp, vertical = 16.dp), // Padding geral
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {

                    VerticalSpace(80) // Espaço do topo

                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )

                    VerticalSpace(12)

                    LargeTitleText(stringResource(R.string.app_name) + ": " + BuildConfig.CLIENT_NAME)

                    VerticalSpace(8)

                    MediumBodyText(stringResource(R.string.login_screen_select_profile))

                    VerticalSpace(48)

                    when {
                        uiState.isLoading && uiState.profiles.isEmpty() -> {
                            // Loading inicial sem dados
                        }

                        uiState.profiles.isNotEmpty() -> {
                            ProfileList(
                                profiles = uiState.profiles,
                                onProfileSelected = onProfileSelected
                            )
                        }

                        // Se quiser reativar o Demo, descomente aqui
                        /*
                        BuildConfig.DEBUG -> {
                            ProfileList(
                                profiles = listOf(demoProfile),
                                onProfileSelected = onProfileSelected
                            )
                        }
                        */

                        uiState.error != null -> {
                            ErrorState(
                                message = uiState.error,
                                onRetry = onRetry
                            )
                        }

                        else -> {
                            EmptyState(message = stringResource(R.string.login_screen_no_users))
                        }
                    }

                    // Adiciona espaço extra no fundo para não ficar colado à versão
                    VerticalSpace(100)
                }
            }
        }

        // --- 2. ELEMENTOS FIXOS (FORA DO SCROLL) ---

        // Botão de Settings (Topo Direito)
        IconButton(
            onClick = onSettingsClicked,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.login_screen_settings),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Versão da App (Fundo Centro)
        // Colocamos aqui para ficar sempre no fundo, independentemente do scroll
        AppVersion(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun ProfileList(
    profiles: List<EmpregadosProfile>,
    onProfileSelected: (EmpregadosProfile) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            // .verticalScroll(rememberScrollState()) <-- REMOVIDO DAQUI (está no pai agora)
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            profiles.forEachIndexed { index, profile ->
                ProfileListItem(
                    profile = profile,
                    onClick = { onProfileSelected(profile) }
                )
                if (index < profiles.lastIndex) {
                    HorizontalDivider(
                        color = Color(0xFF2A2A2A),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileListItem(
    profile: EmpregadosProfile,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.lbl_profile),
                tint = Color.LightGray,
                modifier = Modifier.size(24.dp)
            )
        }

        HorizontalSpace(16)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = profile.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            MediumBodyText(profile.role)
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBox,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(48.dp)
        )
        VerticalSpace(16)
        MediumBodyText(text = message)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        VerticalSpace(16)
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        VerticalSpace(16)
        Text(
            text = stringResource(R.string.login_screen_retry),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onRetry() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun AppVersion(modifier: Modifier) {
    // O Spacer foi removido porque o posicionamento é feito pelo Box pai
    Text(
        text = stringResource(R.string.login_screen_app_version) + ": " + BuildConfig.VERSION_NAME,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray,
        modifier = modifier // Usa o modifier passado (com o align)
    )
}