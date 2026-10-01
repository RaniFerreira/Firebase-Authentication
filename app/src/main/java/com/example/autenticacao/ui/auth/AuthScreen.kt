package com.example.autenticacao.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.autenticacao.ui.components.AppTextField
import com.example.autenticacao.ui.components.ErrorBanner
import com.example.autenticacao.ui.components.LoadingButton
import com.example.autenticacao.ui.components.PasswordStrengthMeter
import com.example.autenticacao.ui.components.PasswordTextField
import com.example.autenticacao.ui.theme.AutenticacaoTheme
import com.example.autenticacao.ui.theme.GradientEnd
import com.example.autenticacao.ui.theme.GradientStart

@Composable
fun AuthRoute(viewModel: AuthViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AuthScreen(
        state = state,
        onModeChange = viewModel::onModeChange,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSubmit = viewModel::submit,
        onDismissError = viewModel::dismissError,
        onForgotPassword = viewModel::openResetDialog
    )

    if (state.reset.visible) {
        ResetPasswordDialog(
            state = state.reset,
            onEmailChange = viewModel::onResetEmailChange,
            onSend = viewModel::sendPasswordReset,
            onDismiss = viewModel::closeResetDialog
        )
    }
}

@Composable
fun AuthScreen(
    state: AuthUiState,
    onModeChange: (AuthMode) -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismissError: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val submit = {
        focusManager.clearFocus()
        onSubmit()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fundo em gradiente na parte superior
        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Header(isRegister = state.isRegister)
            Spacer(Modifier.height(24.dp))

            Card(
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .padding(24.dp)
                        .animateContentSize()
                ) {
                    ModeSelector(mode = state.mode, enabled = !state.isLoading, onModeChange = onModeChange)
                    Spacer(Modifier.height(4.dp))

                    AnimatedVisibility(
                        visible = state.isRegister,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        AppTextField(
                            value = state.name,
                            onValueChange = onNameChange,
                            label = "Nome",
                            leadingIcon = Icons.Outlined.Person,
                            error = state.nameError,
                            enabled = !state.isLoading
                        )
                    }

                    AppTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        label = "E-mail",
                        leadingIcon = Icons.Outlined.Email,
                        error = state.emailError,
                        enabled = !state.isLoading,
                        keyboardType = KeyboardType.Email
                    )

                    PasswordTextField(
                        value = state.password,
                        onValueChange = onPasswordChange,
                        label = "Senha",
                        leadingIcon = Icons.Outlined.Lock,
                        error = state.passwordError,
                        enabled = !state.isLoading,
                        imeAction = if (state.isRegister) ImeAction.Next else ImeAction.Done,
                        onImeAction = if (state.isRegister) null else submit
                    )

                    AnimatedVisibility(
                        visible = state.isRegister,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            PasswordStrengthMeter(state.passwordStrength)
                            PasswordTextField(
                                value = state.confirmPassword,
                                onValueChange = onConfirmPasswordChange,
                                label = "Confirmar senha",
                                leadingIcon = Icons.Outlined.Badge,
                                error = state.confirmPasswordError,
                                enabled = !state.isLoading,
                                onImeAction = submit
                            )
                        }
                    }

                    AnimatedVisibility(visible = !state.isRegister) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(onClick = onForgotPassword, enabled = !state.isLoading) {
                                Text("Esqueci minha senha")
                            }
                        }
                    }

                    ErrorBanner(message = state.errorMessage, onDismiss = onDismissError)

                    LoadingButton(
                        text = if (state.isRegister) "Criar conta" else "Entrar",
                        isLoading = state.isLoading,
                        onClick = submit
                    )

                    SwitchModeHint(
                        isRegister = state.isRegister,
                        enabled = !state.isLoading,
                        onModeChange = onModeChange
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(isRegister: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            contentColor = Color.White,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Shield, contentDescription = null, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (isRegister) "Crie sua conta" else "Bem-vindo(a)!",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (isRegister) "Leva menos de um minuto" else "Entre para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun ModeSelector(mode: AuthMode, enabled: Boolean, onModeChange: (AuthMode) -> Unit) {
    val options = listOf(AuthMode.LOGIN to "Entrar", AuthMode.REGISTER to "Cadastrar")
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, (option, label) ->
            SegmentedButton(
                selected = mode == option,
                onClick = { onModeChange(option) },
                enabled = enabled,
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun SwitchModeHint(isRegister: Boolean, enabled: Boolean, onModeChange: (AuthMode) -> Unit) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(
            onClick = { onModeChange(if (isRegister) AuthMode.LOGIN else AuthMode.REGISTER) },
            enabled = enabled
        ) {
            Text(if (isRegister) "Já tem uma conta? Entrar" else "Ainda não tem conta? Cadastre-se")
        }
    }
}

@Composable
private fun ResetPasswordDialog(
    state: ResetPasswordState,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    val sent = state.sentTo != null
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                if (sent) Icons.Outlined.MarkEmailRead else Icons.Outlined.LockReset,
                contentDescription = null
            )
        },
        title = { Text(if (sent) "Verifique seu e-mail" else "Recuperar senha", textAlign = TextAlign.Center) },
        text = {
            if (sent) {
                Text(
                    "Se existir uma conta para ${state.sentTo}, você receberá um link para criar uma nova senha. " +
                        "Confira também a caixa de spam."
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Informe o e-mail da sua conta e enviaremos um link para redefinir a senha.")
                    AppTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        label = "E-mail",
                        leadingIcon = Icons.Outlined.Email,
                        error = state.emailError ?: state.errorMessage,
                        enabled = !state.isLoading,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Send,
                        onImeAction = onSend
                    )
                }
            }
        },
        confirmButton = {
            if (sent) {
                TextButton(onClick = onDismiss) { Text("Voltar ao login") }
            } else {
                TextButton(onClick = onSend, enabled = !state.isLoading) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Enviar link")
                    }
                }
            }
        },
        dismissButton = {
            if (!sent) {
                TextButton(onClick = onDismiss, enabled = !state.isLoading) { Text("Cancelar") }
            }
        }
    )
}

@Preview(showBackground = true, name = "Login")
@Composable
private fun AuthScreenLoginPreview() {
    AutenticacaoTheme {
        AuthScreen(AuthUiState(), {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, name = "Cadastro", heightDp = 1000)
@Composable
private fun AuthScreenRegisterPreview() {
    AutenticacaoTheme {
        AuthScreen(
            AuthUiState(mode = AuthMode.REGISTER, name = "Maria", email = "maria@email.com", password = "Senha12"),
            {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}
