package com.example.autenticacao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.autenticacao.data.AuthRepository
import com.example.autenticacao.ui.auth.AuthRoute
import com.example.autenticacao.ui.dashboard.DashboardScreen
import com.example.autenticacao.ui.theme.AutenticacaoTheme

class MainActivity : ComponentActivity() {
    private val repository = AuthRepository.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutenticacaoTheme {
                // A sessão do Firebase é persistida: se já houver usuário logado, abre direto no Dashboard.
                val user by repository.currentUser.collectAsStateWithLifecycle()
                AnimatedContent(
                    targetState = user,
                    contentKey = { it?.uid },
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "authNavigation"
                ) { currentUser ->
                    if (currentUser == null) {
                        AuthRoute()
                    } else {
                        DashboardScreen(user = currentUser, onLogout = repository::signOut)
                    }
                }
            }
        }
    }
}
