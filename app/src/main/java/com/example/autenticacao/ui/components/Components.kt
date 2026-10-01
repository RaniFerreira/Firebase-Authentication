package com.example.autenticacao.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.autenticacao.ui.theme.StrengthFair
import com.example.autenticacao.ui.theme.StrengthGood
import com.example.autenticacao.ui.theme.StrengthStrong
import com.example.autenticacao.ui.theme.StrengthWeak
import com.example.autenticacao.util.PasswordStrength

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        enabled = enabled,
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onImeAction?.let { action -> KeyboardActions(onAny = { action() }) }
            ?: KeyboardActions.Default,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (visible) "Ocultar senha" else "Mostrar senha"
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        enabled = enabled,
        singleLine = true,
        shape = MaterialTheme.shapes.small,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = onImeAction?.let { action -> KeyboardActions(onAny = { action() }) }
            ?: KeyboardActions.Default,
        modifier = modifier.fillMaxWidth()
    )
}

/** Botão principal que troca o texto por um indicador de progresso durante requisições. */
@Composable
fun LoadingButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Text("Aguarde…")
        } else {
            Text(text)
        }
    }
}

/** Faixa de erro animada exibida acima do botão principal. */
@Composable
fun ErrorBanner(message: String?, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 14.dp, top = 6.dp, bottom = 6.dp)
            ) {
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = message.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Fechar aviso")
                }
            }
        }
    }
}

/** Indicador de força de senha atualizado em tempo real enquanto o usuário digita. */
@Composable
fun PasswordStrengthMeter(strength: PasswordStrength, modifier: Modifier = Modifier) {
    val targetColor = when (strength.level) {
        PasswordStrength.Level.EMPTY -> MaterialTheme.colorScheme.outlineVariant
        PasswordStrength.Level.WEAK -> StrengthWeak
        PasswordStrength.Level.FAIR -> StrengthFair
        PasswordStrength.Level.GOOD -> StrengthGood
        PasswordStrength.Level.STRONG -> StrengthStrong
    }
    val color by animateColorAsState(targetColor, label = "strengthColor")
    val progress by animateFloatAsState(strength.level.progress, label = "strengthProgress")

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Força da senha",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(strength.level.label, style = MaterialTheme.typography.labelMedium, color = color)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
        ) {
            LinearProgressIndicator(
                progress = { progress },
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
        strength.requirements.forEach { requirement ->
            RequirementRow(requirement.description, requirement.met)
        }
    }
}

@Composable
private fun RequirementRow(text: String, met: Boolean) {
    val color by animateColorAsState(
        if (met) StrengthGood else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "requirementColor"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (met) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = if (met) "Atendido" else "Não atendido",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

/** Avatar circular com as iniciais do usuário. */
@Composable
fun InitialsAvatar(name: String, modifier: Modifier = Modifier, size: Int = 72) {
    val initials = name.split(" ", "@", ".")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }
    Surface(
        shape = androidx.compose.foundation.shape.CircleShape,
        color = Color.White.copy(alpha = 0.22f),
        contentColor = Color.White,
        modifier = modifier.size(size.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(initials, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
