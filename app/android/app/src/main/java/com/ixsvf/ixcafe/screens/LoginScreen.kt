package com.ixsvf.ixcafe.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.ixsvf.ixcafe.services.repository.model.UserProfile
import com.ixsvf.ixcafe.viewmodel.LoginUiState



@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onProfileSelected: (UserProfile) -> Unit,
    onSettingsClicked: () -> Unit,
    onRetry: () -> Unit
) {

    val demoProfile = UserProfile(
        name = IxCafeConstants.APPSETTINGS.DEMO_KEY,
        role = "Empregado"
    )

    Box(modifier = modifier.fillMaxSize()) {


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            )
            {

                VerticalSpace(100)

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

                when{

                    // Estado 1: A carregar
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(top = 32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }


                    // Estado 2: Sucesso com perfis reais
                    uiState.profiles.isNotEmpty() -> {
                        ProfileList(
                            profiles = uiState.profiles,
                            onProfileSelected = onProfileSelected
                        )
                    }
                    // Estado 3: Modo DEBUG (Apanha 'error' ou 'isEmpty')
                    // Se não estamos a carregar e não temos perfis,
                    // mostramos o perfil DEMO em vez do erro.
                    BuildConfig.DEBUG -> {
                        ProfileList(
                            profiles = listOf(demoProfile),
                            onProfileSelected = onProfileSelected
                        )
                    }
                    // Estado 4: Modo RELEASE com Erro
                    uiState.error != null -> {
                        ErrorState(
                            message = uiState.error,
                            onRetry = onRetry
                        )
                    }

                    // Estado 5: Modo RELEASE com lista vazia
                    // (profiles.isEmpty() e error == null)
                    else -> {
                        ProfileList(
                            profiles = uiState.profiles,
                            onProfileSelected = onProfileSelected
                        )
                    }
                }
                AppVersion(Modifier.weight(1f))
            }

        }


        // (Como filho direto do Box, ele vai sobrepor-se)
        IconButton(
            onClick = onSettingsClicked,
            modifier = Modifier
                .align(Alignment.TopEnd) // Alinha ao canto superior direito
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.login_screen_settings),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Composable
private fun ProfileList(
    profiles: List<UserProfile>,
    onProfileSelected: (UserProfile) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
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
    profile: UserProfile,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Ícone de Pessoa ---
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)), // Fundo cinza escuro para o ícone
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
        // Botão simples para tentar novamente
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
fun AppVersion(modifier: Modifier)
{
    Spacer(modifier = modifier)
    Text(
        text =  stringResource(R.string.login_screen_app_version) + ": " + BuildConfig.VERSION_NAME,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 16.dp)
    )

}