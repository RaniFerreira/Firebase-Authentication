package com.example.autenticacao

import com.example.autenticacao.util.PasswordStrength
import com.example.autenticacao.util.Validators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun email_valid() {
        assertNull(Validators.validateEmail("maria@email.com"))
        assertNull(Validators.validateEmail("  joao.silva+teste@empresa.com.br "))
    }

    @Test
    fun email_invalid() {
        assertNotNull(Validators.validateEmail(""))
        assertNotNull(Validators.validateEmail("maria"))
        assertNotNull(Validators.validateEmail("maria@"))
        assertNotNull(Validators.validateEmail("maria@email"))
        assertNotNull(Validators.validateEmail("maria @email.com"))
    }

    @Test
    fun registerPassword_respectsFirebaseMinimumAndStrength() {
        assertNotNull(Validators.validateRegisterPassword(""))
        assertNotNull(Validators.validateRegisterPassword("Ab1!"))      // menos de 6 caracteres
        assertNotNull(Validators.validateRegisterPassword("abcdef"))    // fraca
        assertNull(Validators.validateRegisterPassword("Senha123"))
    }

    @Test
    fun confirmation_mustMatch() {
        assertNotNull(Validators.validateConfirmation("Senha123", "Senha124"))
        assertNull(Validators.validateConfirmation("Senha123", "Senha123"))
    }
}

class PasswordStrengthTest {

    @Test
    fun levels() {
        assertEquals(PasswordStrength.Level.EMPTY, PasswordStrength.evaluate("").level)
        assertEquals(PasswordStrength.Level.WEAK, PasswordStrength.evaluate("abc").level)
        assertEquals(PasswordStrength.Level.WEAK, PasswordStrength.evaluate("abcdefgh").level)
        assertEquals(PasswordStrength.Level.FAIR, PasswordStrength.evaluate("abcdefg1").level)
        assertEquals(PasswordStrength.Level.GOOD, PasswordStrength.evaluate("Abcdefg1").level)
        assertEquals(PasswordStrength.Level.STRONG, PasswordStrength.evaluate("Abcdefg1!").level)
    }
}
