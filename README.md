# 🔐 Autenticação — Firebase + Jetpack Compose

Aplicativo Android de autenticação de usuários com **e-mail e senha**, desenvolvido em **Kotlin** e **Jetpack Compose** (Material 3), integrado ao **Firebase Authentication**.

## ✨ Funcionalidades

| Recurso | Descrição |
|---|---|
| **Login e cadastro** | Uma única tela com seletor *Entrar / Cadastrar* (segmented button) e link para alternar entre os modos. |
| **Dashboard** | Após autenticar, o usuário vê uma saudação personalizada (“Bom dia, Maria! 👋”), avatar com iniciais e dados da conta (e-mail, verificação, data de cadastro e último acesso). |
| **Logout** | Botão “Sair da conta” com diálogo de confirmação. |
| **Sessão persistente** | Se o usuário já estiver logado, o app abre direto no Dashboard. |
| **Validação dos campos** | Nome, formato de e-mail, senha e confirmação de senha são validados antes de qualquer requisição, com mensagens abaixo de cada campo. |
| **Tratamento de erros do Firebase** | E-mail já cadastrado, credenciais inválidas, senha fraca, conta desativada, excesso de tentativas, falta de internet e provedor desabilitado são traduzidos em mensagens amigáveis em português. |
| **Estados de carregamento** | Durante as requisições o botão mostra um indicador de progresso e os campos ficam desabilitados (evitando envios duplicados). |
| ⭐ **Desafio extra 1 — “Esqueci minha senha”** | Diálogo que envia o e-mail de redefinição com `sendPasswordResetEmail` do Firebase (em português). |
| ⭐ **Desafio extra 2 — Força da senha em tempo real** | No cadastro, barra colorida animada (Fraca → Média → Boa → Forte) e checklist de requisitos atualizados a cada tecla. |
| **UI/UX** | Paleta própria índigo/ciano com tema claro e escuro, cabeçalho em gradiente, cantos arredondados, animações de transição, mostrar/ocultar senha, layout rolável e responsivo (largura máxima de 480 dp em tablets, ajuste ao teclado). |

## 🧱 Estrutura do projeto

```
app/src/main/java/com/example/autenticacao/
├── MainActivity.kt                 # Alterna entre Login e Dashboard conforme a sessão
├── data/
│   └── AuthRepository.kt           # Chamadas ao Firebase Auth + tradução das exceções
├── ui/
│   ├── auth/
│   │   ├── AuthScreen.kt           # Tela de login/cadastro + diálogo de recuperação
│   │   └── AuthViewModel.kt        # Estado do formulário, validação e loading
│   ├── dashboard/
│   │   └── DashboardScreen.kt      # Boas-vindas, dados da conta e logout
│   ├── components/Components.kt    # Campos, botão com loading, banner de erro, medidor de senha
│   └── theme/                      # Cores, tipografia e formas
└── util/
    ├── Validators.kt               # Regras de validação (testadas)
    └── PasswordStrength.kt         # Cálculo da força da senha (testado)
```

Arquitetura: **MVVM** — a interface Compose observa um `StateFlow` do `AuthViewModel`; o `AuthRepository` encapsula o Firebase e expõe o usuário atual como `StateFlow`.

## 🛠️ Pré-requisitos

- Android Studio (versão recente, com suporte ao AGP 9)
- Android SDK **37** instalado (o projeto compila com `compileSdk 37`; `minSdk 24`)
- Uma conta Google para acessar o [Firebase Console](https://console.firebase.google.com/)
- Emulador ou dispositivo com **Google Play Services** e acesso à internet

## 🔥 Configuração do Firebase (passo a passo)

> O arquivo `app/google-services.json` é específico de cada projeto Firebase. Siga os passos abaixo para gerar o seu.

1. **Crie o projeto**: acesse o [Firebase Console](https://console.firebase.google.com/), clique em **Adicionar projeto**, dê um nome (ex.: `autenticacao-app`) e conclua o assistente (o Google Analytics é opcional).
2. **Registre o app Android**: na página do projeto, clique no ícone **Android**.
   - **Nome do pacote**: `com.example.autenticacao` *(precisa ser exatamente este)*
   - Apelido e SHA-1 são opcionais para login com e-mail/senha.
   - Clique em **Registrar app**.
3. **Baixe o `google-services.json`** e copie-o para a pasta **`app/`** do projeto:
   ```
   Autenticacao/
   └── app/
       ├── build.gradle.kts
       └── google-services.json   ← aqui
   ```
   As etapas seguintes do assistente (adicionar SDK/plugin) **já estão feitas** neste projeto — basta avançar até o fim.
4. **Ative o provedor E-mail/senha**: no menu lateral, vá em **Build → Authentication → Primeiros passos → Sign-in method**, selecione **E-mail/senha**, ative a primeira chave e clique em **Salvar**.
5. *(Opcional)* **Personalize o e-mail de redefinição de senha** em **Authentication → Templates → Redefinição de senha**.

### Como o Firebase foi integrado (referência)

- `gradle/libs.versions.toml`: plugin `com.google.gms.google-services` e Firebase BoM.
- `build.gradle.kts` (raiz): `alias(libs.plugins.google.services) apply false`
- `app/build.gradle.kts`: plugin `google-services` + dependências
  ```kotlin
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.auth)
  implementation(libs.kotlinx.coroutines.play.services) // permite usar await() nas Tasks do Firebase
  ```

## ▶️ Compilação e execução

**Pelo Android Studio**
1. `File → Open` e selecione a pasta `Autenticacao`.
2. Aguarde o *Gradle Sync* terminar.
3. Confirme que o `app/google-services.json` está no lugar.
4. Selecione um emulador/dispositivo e clique em **Run ▶**.

**Pela linha de comando**
```bash
# Windows
gradlew.bat assembleDebug          # gera app/build/outputs/apk/debug/app-debug.apk
gradlew.bat installDebug           # instala no dispositivo/emulador conectado
gradlew.bat testDebugUnitTest      # executa os testes unitários

# macOS / Linux
./gradlew assembleDebug
```

> Se aparecer o erro `File google-services.json is missing`, revise o passo 3 da configuração do Firebase.

## 🧪 Como testar os cenários

| Cenário | O que fazer | Resultado esperado |
|---|---|---|
| Campos vazios / e-mail mal formatado | Toque em **Entrar** sem preencher | Mensagens de erro abaixo dos campos, sem chamada à rede |
| Senha fraca | No cadastro, digite `abcdef` | Medidor vermelho “Fraca” e cadastro bloqueado |
| Senhas diferentes | Confirmação diferente da senha | “As senhas não coincidem” |
| Cadastro com sucesso | Nome, e-mail válido e senha `Senha123` | Abre o Dashboard com “Bom dia / Boa tarde / Boa noite, Nome!” |
| E-mail já cadastrado | Cadastre novamente o mesmo e-mail | “Este e-mail já está cadastrado…” |
| Credenciais inválidas | Login com senha errada | “E-mail ou senha incorretos.” |
| Sem internet | Ative o modo avião e tente entrar | “Sem conexão com a internet…” |
| Esqueci minha senha | Toque no link e informe o e-mail | Confirmação de envio; e-mail chega na caixa de entrada/spam |
| Logout | **Sair da conta → Sair** | Volta para a tela de login |

> **Observação:** projetos Firebase novos ativam a *proteção contra enumeração de e-mails*. Com ela, o Firebase não informa se o e-mail existe: login com usuário inexistente ou senha errada retornam o mesmo erro (“E-mail ou senha incorretos”), e a recuperação de senha sempre responde com sucesso. Esse comportamento é intencional e foi tratado no app.

## 📦 Tecnologias

Kotlin · Jetpack Compose · Material 3 · ViewModel + StateFlow · Coroutines · Firebase Authentication (BoM 34.19.0) · Android Gradle Plugin 9
