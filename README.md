# 🏃 SprintTrack - Android

Projeto desenvolvido para a disciplina **Dispositivos Móveis 2 (ARQDMO2)** do curso de **Análise e Desenvolvimento de Sistemas - IFSP Araraquara**.

---

# 🎯 Sobre o Projeto

O **SprintTrack** é um aplicativo mobile focado em corredores de curta distância e atletas amadores. O objetivo do app é utilizar sensores do smartphone para registrar treinos de sprint de forma automática e inteligente.

O aplicativo utiliza recursos do dispositivo como:

* Sensores de movimento
* Contador de passos
* Microfone
* Câmera

Além disso, o sistema utiliza o **Firebase Authentication** para autenticação de usuários e o **Firebase Firestore** para armazenamento dos dados dos treinos.

---

# 🚀 Funcionalidades

## 🔐 Autenticação e Cadastro

* Cadastro de usuários com e-mail e senha
* Login utilizando Firebase Authentication
* Persistência de sessão
* Logout do usuário
* Armazenamento de dados do usuário no Firestore

---

## 🏠 Tela Home

A tela principal do aplicativo exibe:

* Melhor tempo do usuário
* Último treino realizado
* Botão para iniciar novo sprint
* Informações gerais de desempenho

---

## 🏃 Registro de Sprint

O aplicativo permite:

* Iniciar treinos de corrida curta
* Detectar movimento automaticamente
* Cronometrar o tempo do sprint
* Registrar quantidade de passos
* Detectar intensidade do movimento

---

## 📱 Sensores Utilizados

### 📌 Sensores de Movimento

Uso de:

* Acelerômetro
* Contador de passos

Funcionalidades:

* Detectar início da corrida
* Medir movimentação do usuário
* Contabilizar passos
* Identificar intensidade do sprint

---

### 🎤 Microfone

O aplicativo utilizará recursos de áudio para:

* Comandos de voz
* Feedback sonoro
* Contagem regressiva
* Notas de voz do treino

Exemplos:

* “Iniciar treino”
* “Parar treino”
* “Salvar resultado”

---

### 📷 Câmera

O usuário poderá:

* Tirar fotos do treino
* Registrar pista/local
* Adicionar imagem ao treino
* Definir foto de perfil

---

## 📊 Histórico de Treinos

O sistema possui uma tela de histórico contendo:

* Data do treino
* Tempo realizado
* Quantidade de passos
* Observações
* Evolução do usuário

Os dados são carregados diretamente do Firebase Firestore utilizando RecyclerView.

---

## 👤 Perfil do Usuário

A tela de perfil possui:

* Nome do usuário
* E-mail
* Logout

---

# 🔥 Integração com Firebase

O aplicativo utiliza:

## Firebase Authentication

Responsável por:

* Cadastro
* Login
* Persistência de sessão
* Logout

---

## Firebase Firestore

Responsável por armazenar:

### Usuários

### Treinos

---

# 🔔 Feedback Visual e Sonoro

O aplicativo utiliza:

* Toasts
* Vibração
* Feedback visual
* Sons de largada
* Notificações
* Destaque para novos recordes

---

# 🛠️ Tecnologias Utilizadas

* **Kotlin**
* **Android Studio**
* **SDK 33 (Android 13)**
* **Firebase Authentication**
* **Firebase Firestore**
* **RecyclerView**
* **ViewBinding**
* **SensorManager**
* **Accelerometer Sensor**
* **Step Counter Sensor**
* **Media APIs**
* **Material Design**

---

# 📱 Responsividade

O aplicativo foi desenvolvido utilizando boas práticas de UI/UX:

* Layouts responsivos
* Uso de `ScrollView`
* `ConstraintLayout`
* `LinearLayout`
* Utilização de `dp` e `sp`
* Hierarquia visual organizada
* Compatibilidade com diferentes tamanhos de tela

---

# ▶️ Demonstração

📌 Vídeo curto (30s):

👉 [Download](video/videodemonstracao.mp4)

---

# 📚 Aprendizados

Durante o desenvolvimento do projeto foram aplicados conceitos como:

* Desenvolvimento Android em Kotlin
* Integração com Firebase
* Autenticação de usuários
* Persistência de dados
* Uso de sensores físicos
* RecyclerView
* Navegação entre telas
* Arquitetura em camadas
* Manipulação de permissões
* Interface responsiva
* MVP para aplicativos mobile

---

# 👨‍🏫 Informações Acadêmicas

* **Disciplina:** Dispositivos Móveis 2
* **Instituição:** IFSP - Campus Araraquara

---

# 👩‍💻 Desenvolvido por

* Maria Eduarda Zanetti Moro e Christian Amancio

---

# 📌 Proposta do Projeto

Este projeto foi desenvolvido como parte de uma atividade prática simulando um cenário real de desenvolvimento mobile.

A proposta da disciplina consiste em criar um aplicativo Android funcional utilizando:

* Firebase ou Room
* Sensores do dispositivo
* Recursos multimídia
* Persistência de dados
* Autenticação
* Interface responsiva
