package com.example.autenticacao.util

/**
 * Validações puras (sem dependência do Android) para que possam ser cobertas por testes unitários.
 */
object Validators {

    /** Tamanho mínimo exigido pelo Firebase Authentication. */
    const val FIREBASE_MIN_PASSWORD_LENGTH = 6

    private val EMAIL_REGEX =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    fun validateEmail(email: String): String? {
        val value = email.trim()
        return when {
            value.isEmpty() -> "Informe o seu e-mail"
            !EMAIL_REGEX.matches(value) -> "Formato de e-mail inválido"
            else -> null
        }
    }

    fun validateLoginPassword(password: String): String? = when {
        password.isEmpty() -> "Informe a sua senha"
        else -> null
    }

    fun validateRegisterPassword(password: String): String? {
        val strength = PasswordStrength.evaluate(password)
        return when {
            password.isEmpty() -> "Crie uma senha"
            password.length < FIREBASE_MIN_PASSWORD_LENGTH ->
                "A senha deve ter pelo menos $FIREBASE_MIN_PASSWORD_LENGTH caracteres"
            strength.level < PasswordStrength.Level.FAIR ->
                "Senha muito fraca. Siga os requisitos abaixo"
            else -> null
        }
    }

    fun validateConfirmation(password: String, confirmation: String): String? = when {
        confirmation.isEmpty() -> "Confirme a sua senha"
        confirmation != password -> "As senhas não coincidem"
        else -> null
    }

    fun validateName(name: String): String? {
        val value = name.trim()
        return when {
            value.isEmpty() -> "Informe como devemos chamar você"
            value.length < 2 -> "Nome muito curto"
            else -> null
        }
    }
}
