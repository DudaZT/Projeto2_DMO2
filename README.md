# 🏃 SprintTrack - Android

Projeto desenvolvido para a disciplina **Dispositivos Móveis 2 (ARQDMO2)** do curso de **Análise e Desenvolvimento de Sistemas - IFSP Araraquara**.

---

# 🎯 Sobre o Projeto

O **SprintTrack** é um aplicativo mobile focado em corredores de curta distância e atletas amadores. O objetivo do app é utilizar sensores do smartphone para registrar treinos de sprint de forma automática e inteligente.

O aplicativo utiliza os seguintes recursos do dispositivo:

- **Acelerômetro** para detectar movimento
- **Sensor de passos** para contagem de passos
- **Microfone** para reconhecimento de voz
- **Câmera** para foto de perfil

Além disso, o sistema utiliza o **Firebase Authentication** para autenticação de usuários e o **Firebase Firestore** para persistência dos dados.

---

# 🚀 Funcionalidades

## 🔐 Autenticação e Cadastro

- Cadastro de usuários com nome, e-mail e senha
- Login utilizando Firebase Authentication (e-mail/senha)
- Persistência de sessão (usuário permanece logado)
- Logout do usuário
- Armazenamento dos dados do perfil no Firestore (coleção `usuarios`)

---

## 🏠 Tela Home

A tela principal do aplicativo exibe:

- Melhor tempo de sprint do usuário
- Top 3 do ranking de eficiência (passos/segundo)
- Botão para iniciar um novo sprint
- Acesso à tela de Leaderboard completa (top 10)

---

## 🏃 Registro de Sprint

Durante o treino, o aplicativo:

- Executa uma contagem regressiva de 3 segundos com som de largada
- Cronometra o tempo do sprint (`Chronometer`)
- Detecta movimento via acelerômetro para identificar atividade intensa
- Conta a quantidade de passos com o sensor de passos
- Permite ditar uma observação por comando de voz (reconhecimento de fala)
- Ao finalizar, salva todos os dados no Firestore e emite notificação

---

## 🏆 Leaderboard

O ranking de eficiência ordena treinos de todos os usuários por passos por segundo, exibindo:

- Posição no pódio (medalhas para top 3)
- Nome e foto do usuário
- Eficiência (passos/segundo)
- Passos totais e tempo
- Data do treino

Disponível em:
- Card resumido na tela Home (top 3)
- Tela dedicada com lista dos 10 melhores (`LeaderboardActivity`)

---

## 📊 Histórico de Treinos

O sistema possui uma tela de histórico com:

- Lista de treinos do usuário logado
- Cada item exibe: tempo, passos e data
- Toque no item: abre tela de detalhes com observação e dados completos
- Botão de exclusão para remover um treino

Os dados são carregados do Firestore com `RecyclerView`.

---

## 👤 Perfil do Usuário

A tela de perfil possui:

- Nome do usuário
- E-mail
- Foto de perfil (tirada com a câmera e salva em Base64)
- Botão de logout

---

# 📱 Recursos do Dispositivo Utilizados

### 1. Sensores de Movimento

- **Acelerômetro:** detecta movimento intenso do usuário (`MotionSensorManager`)
- **Sensor de Passos:** conta os passos totais durante o sprint (`StepCounterManager`)

---

### 2. Microfone (Reconhecimento de Voz)

- Implementado com `SpeechRecognizer` via `ReconhecimentoHelper`
- Permite ditar a observação do treino por voz
- Funciona em português (pt-BR)
- Tratamento de permissão `RECORD_AUDIO`

---

### 3. Câmera

- Usada exclusivamente para foto de perfil no `PerfilFragment`
- Imagem capturada com `MediaStore.ACTION_IMAGE_CAPTURE`
- Convertida para Base64 com redimensionamento (`ImageUtils`)
- Salva no Firestore como `fotoBase64`
- Tratamento de permissão `CAMERA`

---

# 🔥 Integração com Firebase

## Firebase Authentication

- Cadastro de usuários
- Login e logout
- Persistência automática de sessão

## Firebase Firestore

Estrutura de coleções:

**`usuarios`**
| Campo | Tipo |
|-------|------|
| uid | String |
| nome | String |
| email | String |
| fotoBase64 | String |

**`treinos`**
| Campo | Tipo |
|-------|------|
| uid | String |
| tempo | Double |
| passos | Int |
| data | String |
| observacao | String |
| fotoUrl | String |
| timestamp | Long |
| nome | String |
| foto | String |

---

# 🔔 Feedback Visual e Sonoro

O aplicativo oferece feedback ao usuário por meio de:

- **Toasts** para confirmações e erros
- **Notificação** ao finalizar e salvar um treino com sucesso
- **Som de largada** (`race_start`) na contagem regressiva
- **Som de chegada** (`finish`) ao finalizar o sprint

---

# 🛠️ Tecnologias Utilizadas

- **Kotlin**
- **Android Studio**
- **SDK mínimo 33 (Android 13.0)**
- **Firebase Authentication**
- **Firebase Firestore**
- **RecyclerView**
- **ViewBinding**
- **SensorManager** (Accelerometer + Step Counter)
- **SpeechRecognizer** (reconhecimento de voz)
- **MediaPlayer** (efeitos sonoros)
- **NotificationCompat** (notificações locais)
- **ActivityResultContracts** (permissões e câmera)

---

# 📁 Organização do Código

O código está estruturado nos seguintes pacotes:

| Pacote | Conteúdo |
|--------|----------|
| `adapter` | `LeaderboardAdapter`, `TreinoAdapter` |
| `auth` | `LoginActivity`, `RegisterActivity` |
| `firebase` | `FirebaseConfig` |
| `helper` | `ReconhecimentoHelper` |
| `model` | `User`, `Treino`, `LeaderboardItem` |
| `sensor` | `MotionSensorManager`, `StepCounterManager` |
| `ui` | `MainActivity`, `SprintActivity`, `DetalheTreinoActivity`, `LeaderboardActivity`, `HomeFragment`, `HistoricoFragment`, `PerfilFragment` |
| `util` | `ImageUtils` |

---

# ▶️ Demonstração

📌 Vídeo demonstrativo:

👉 [Download](video/videodemonstracao.mp4)

---

# 👨‍🏫 Informações Acadêmicas

- **Disciplina:** Dispositivos Móveis 2 (ARQDMO2)
- **Professor:** Henrique Galati
- **Instituição:** IFSP - Campus Araraquara
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Período:** 2º Bimestre

---

# 📌 Proposta do Projeto

Este projeto foi desenvolvido como parte de uma atividade prática simulando um cenário real de desenvolvimento mobile. O SprintTrack é um **MVP (Minimum Viable Product)** de um aplicativo que utiliza sensores do dispositivo e Firebase para oferecer uma experiência rica e personalizada ao usuário.

---

# 👩‍💻 Desenvolvido por

- Maria Eduarda Zanetti Moro
- Christian Amancio