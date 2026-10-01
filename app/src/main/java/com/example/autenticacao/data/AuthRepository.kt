package com.example.autenticacao.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

/** Dados do usuário autenticado exibidos pela interface. */
data class SessionUser(
    val uid: String,
    val email: String,
    val displayName: String?,
    val isEmailVerified: Boolean,
    val createdAt: Long?,
    val lastSignInAt: Long?
)

/**
 * Camada de acesso ao Firebase Authentication. Converte as exceções do Firebase em
 * mensagens amigáveis e mantém o usuário da sessão atual em um [StateFlow].
 */
class AuthRepository(private val auth: FirebaseAuth) {

    private val _currentUser = MutableStateFlow(auth.currentUser?.toSessionUser())
    val currentUser: StateFlow<SessionUser?> = _currentUser.asStateFlow()

    init {
        // Garante que a interface volte ao login se a sessão for encerrada externamente
        // (token revogado, conta excluída etc.).
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) _currentUser.value = null
        }
    }

    suspend fun signIn(email: String, password: String): Result<SessionUser> = runCatchingAuth {
        val user = auth.signInWithEmailAndPassword(email.trim(), password).await().user
            ?: error("Usuário não retornado pelo Firebase")
        user.toSessionUser().also { _currentUser.value = it }
    }

    suspend fun register(name: String, email: String, password: String): Result<SessionUser> =
        runCatchingAuth {
            val user = auth.createUserWithEmailAndPassword(email.trim(), password).await().user
                ?: error("Usuário não retornado pelo Firebase")
            // O nome é salvo no perfil do Firebase para personalizar a mensagem de boas-vindas.
            user.updateProfile(userProfileChangeRequest { displayName = name.trim() }).await()
            user.toSessionUser().also { _currentUser.value = it }
        }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatchingAuth {
        auth.setLanguageCode("pt")
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    private suspend fun <T> runCatchingAuth(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(AuthException(e.toFriendlyMessage(), e))
        }

    companion object {
        val instance: AuthRepository by lazy { AuthRepository(FirebaseAuth.getInstance()) }
    }
}

/** Exceção com mensagem já pronta para ser exibida ao usuário. */
class AuthException(message: String, cause: Throwable? = null) : Exception(message, cause)

private fun FirebaseUser.toSessionUser() = SessionUser(
    uid = uid,
    email = email.orEmpty(),
    displayName = displayName?.takeIf { it.isNotBlank() },
    isEmailVerified = isEmailVerified,
    createdAt = metadata?.creationTimestamp,
    lastSignInAt = metadata?.lastSignInTimestamp
)

/**
 * Traduz os erros mais comuns do Firebase Authentication.
 * Os códigos estão documentados em https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuthException
 */
internal fun Throwable.toFriendlyMessage(): String = when (this) {
    is FirebaseAuthWeakPasswordException ->
        "Senha fraca. Use pelo menos 6 caracteres, combinando letras, números e símbolos."
    is FirebaseAuthUserCollisionException ->
        "Este e-mail já está cadastrado. Tente fazer login ou recuperar a senha."
    is FirebaseAuthInvalidUserException -> when (errorCode) {
        "ERROR_USER_DISABLED" -> "Esta conta foi desativada. Entre em contato com o suporte."
        else -> "Não encontramos uma conta com este e-mail."
    }
    is FirebaseAuthInvalidCredentialsException -> when (errorCode) {
        "ERROR_INVALID_EMAIL" -> "O e-mail informado é inválido."
        "ERROR_WRONG_PASSWORD" -> "Senha incorreta. Tente novamente."
        // Com a proteção contra enumeração de e-mails (padrão em projetos novos), o Firebase
        // não diferencia "usuário inexistente" de "senha errada": ambos chegam como INVALID_CREDENTIAL.
        else -> "E-mail ou senha incorretos."
    }
    is FirebaseTooManyRequestsException ->
        "Muitas tentativas seguidas. Aguarde alguns minutos e tente novamente."
    is FirebaseNetworkException ->
        "Sem conexão com a internet. Verifique a sua rede e tente novamente."
    is FirebaseAuthException -> when (errorCode) {
        "ERROR_OPERATION_NOT_ALLOWED" ->
            "O login por e-mail/senha não está habilitado no Firebase Console."
        else -> "Erro de autenticação ($errorCode)."
    }
    else -> "Algo deu errado. Tente novamente."
}
