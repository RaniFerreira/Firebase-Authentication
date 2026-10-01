package com.example.autenticacao.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autenticacao.data.AuthRepository
import com.example.autenticacao.util.PasswordStrength
import com.example.autenticacao.util.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthMode { LOGIN, REGISTER }

data class ResetPasswordState(
    val visible: Boolean = false,
    val email: String = "",
    val showErrors: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val sentTo: String? = null
) {
    val emailError: String? get() = if (showErrors) Validators.validateEmail(email) else null
}

data class AuthUiState(
    val mode: AuthMode = AuthMode.LOGIN,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    /** Os erros dos campos só aparecem após a primeira tentativa de envio. */
    val showErrors: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val reset: ResetPasswordState = ResetPasswordState()
) {
    val isRegister: Boolean get() = mode == AuthMode.REGISTER

    val passwordStrength: PasswordStrength get() = PasswordStrength.evaluate(password)

    val nameError: String?
        get() = if (showErrors && isRegister) Validators.validateName(name) else null
    val emailError: String?
        get() = if (showErrors) Validators.validateEmail(email) else null
    val passwordError: String?
        get() = if (!showErrors) null
        else if (isRegister) Validators.validateRegisterPassword(password)
        else Validators.validateLoginPassword(password)
    val confirmPasswordError: String?
        get() = if (showErrors && isRegister) {
            Validators.validateConfirmation(password, confirmPassword)
        } else null

    val isFormValid: Boolean
        get() = copy(showErrors = true).run {
            listOf(nameError, emailError, passwordError, confirmPasswordError).all { it == null }
        }
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onModeChange(mode: AuthMode) = _uiState.update {
        if (it.isLoading) it
        else it.copy(mode = mode, showErrors = false, errorMessage = null, password = "", confirmPassword = "")
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, errorMessage = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value.trim(), errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun dismissError() = _uiState.update { it.copy(errorMessage = null) }

    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return
        if (!state.isFormValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }

        _uiState.update { it.copy(showErrors = true, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = when (state.mode) {
                AuthMode.LOGIN -> repository.signIn(state.email, state.password)
                AuthMode.REGISTER -> repository.register(state.name, state.email, state.password)
            }
            result
                // Em caso de sucesso o MainActivity navega para o Dashboard; limpamos o formulário.
                .onSuccess { _uiState.value = AuthUiState() }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    // ----- Recuperação de senha -----

    fun openResetDialog() = _uiState.update {
        it.copy(reset = ResetPasswordState(visible = true, email = it.email))
    }

    fun closeResetDialog() = _uiState.update {
        if (it.reset.isLoading) it else it.copy(reset = ResetPasswordState())
    }

    fun onResetEmailChange(value: String) = _uiState.update {
        it.copy(reset = it.reset.copy(email = value.trim(), errorMessage = null))
    }

    fun sendPasswordReset() {
        val reset = _uiState.value.reset
        if (reset.isLoading) return
        if (Validators.validateEmail(reset.email) != null) {
            _uiState.update { it.copy(reset = it.reset.copy(showErrors = true)) }
            return
        }

        _uiState.update { it.copy(reset = it.reset.copy(showErrors = true, isLoading = true, errorMessage = null)) }
        viewModelScope.launch {
            repository.sendPasswordReset(reset.email)
                .onSuccess {
                    _uiState.update { it.copy(reset = it.reset.copy(isLoading = false, sentTo = reset.email)) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(reset = it.reset.copy(isLoading = false, errorMessage = error.message))
                    }
                }
        }
    }
}
