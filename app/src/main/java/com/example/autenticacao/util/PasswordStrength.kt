package com.example.autenticacao.util

/**
 * Avalia a força de uma senha a partir de critérios simples, usado para o
 * indicador visual em tempo real na tela de cadastro.
 */
data class PasswordStrength(
    val level: Level,
    val requirements: List<Requirement>
) {
    enum class Level(val label: String, val progress: Float) {
        EMPTY("", 0f),
        WEAK("Fraca", 0.25f),
        FAIR("Média", 0.5f),
        GOOD("Boa", 0.75f),
        STRONG("Forte", 1f)
    }

    data class Requirement(val description: String, val met: Boolean)

    companion object {
        const val RECOMMENDED_LENGTH = 8

        fun evaluate(password: String): PasswordStrength {
            val requirements = listOf(
                Requirement("Pelo menos $RECOMMENDED_LENGTH caracteres", password.length >= RECOMMENDED_LENGTH),
                Requirement("Uma letra maiúscula", password.any { it.isUpperCase() }),
                Requirement("Uma letra minúscula", password.any { it.isLowerCase() }),
                Requirement("Um número", password.any { it.isDigit() }),
                Requirement("Um caractere especial (!@#…)", password.any { !it.isLetterOrDigit() && !it.isWhitespace() })
            )

            if (password.isEmpty()) return PasswordStrength(Level.EMPTY, requirements)

            var score = requirements.count { it.met }
            // Senhas longas ganham um bônus; senhas abaixo do mínimo do Firebase são sempre fracas.
            if (password.length >= 12) score++
            val level = when {
                password.length < Validators.FIREBASE_MIN_PASSWORD_LENGTH -> Level.WEAK
                score <= 2 -> Level.WEAK
                score == 3 -> Level.FAIR
                score == 4 -> Level.GOOD
                else -> Level.STRONG
            }
            return PasswordStrength(level, requirements)
        }
    }
}
