package com.example.autenticacao.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.autenticacao.data.SessionUser
import com.example.autenticacao.ui.components.InitialsAvatar
import com.example.autenticacao.ui.theme.AutenticacaoTheme
import com.example.autenticacao.ui.theme.GradientEnd
import com.example.autenticacao.ui.theme.GradientStart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(user: SessionUser, onLogout: () -> Unit) {
    var confirmLogout by rememberSaveable { mutableStateOf(false) }
    val name = user.displayName ?: user.email.substringBefore("@")

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            InitialsAvatar(name = name)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${greeting()}, $name! 👋",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Que bom ter você por aqui.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            Card(
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(vertical = 8.dp)) {
                    Text(
                        "Sua conta",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                    InfoRow(Icons.Outlined.Email, "E-mail", user.email)
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                    InfoRow(
                        Icons.Outlined.VerifiedUser,
                        "Verificação do e-mail",
                        if (user.isEmailVerified) "Verificado" else "Pendente"
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                    InfoRow(Icons.Outlined.CalendarMonth, "Membro desde", formatDate(user.createdAt))
                    HorizontalDivider(Modifier.padding(horizontal = 20.dp))
                    InfoRow(Icons.Outlined.History, "Último acesso", formatDateTime(user.lastSignInAt))
                }
            }

            OutlinedButton(
                onClick = { confirmLogout = true },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sair da conta")
            }
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
            title = { Text("Encerrar sessão?") },
            text = { Text("Você precisará informar e-mail e senha para entrar novamente.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    onLogout()
                }) { Text("Sair") }
            },
            dismissButton = {
                TextButton(onClick = { confirmLogout = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

private val PtBr = Locale.forLanguageTag("pt-BR")

private fun greeting(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "Bom dia"
    in 12..17 -> "Boa tarde"
    else -> "Boa noite"
}

private fun formatDate(timestamp: Long?): String =
    timestamp?.let { SimpleDateFormat("dd 'de' MMMM 'de' yyyy", PtBr).format(Date(it)) } ?: "—"

private fun formatDateTime(timestamp: Long?): String =
    timestamp?.let { SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", PtBr).format(Date(it)) } ?: "—"

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    AutenticacaoTheme {
        DashboardScreen(
            user = SessionUser(
                uid = "123",
                email = "maria@email.com",
                displayName = "Maria Silva",
                isEmailVerified = false,
                createdAt = System.currentTimeMillis(),
                lastSignInAt = System.currentTimeMillis()
            ),
            onLogout = {}
        )
    }
}
