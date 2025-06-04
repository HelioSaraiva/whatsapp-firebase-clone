# 📱 Clone WhatsApp com Firebase (Kotlin)

Aplicativo Android completo inspirado no WhatsApp, desenvolvido em **Kotlin**, com integração em tempo real via **Firebase Authentication** e **Cloud Firestore**. O app permite **mensagens entre usuários autenticados**, exibição de conversas recentes, gerenciamento de perfil e navegação fluida com `Fragments` e `ViewPager`.

---

## ✨ Funcionalidades

- 🔐 **Autenticação com Firebase Auth**
- 💬 **Envio e recebimento de mensagens em tempo real**
- 🧾 **Lista de conversas atualizada automaticamente**
- 👥 **Listagem de contatos com RecyclerView**
- 🔄 **Atualização automática com Firestore SnapshotListener**
- 📷 **Upload e exibição de fotos com Picasso**
- 🛠️ **Binding de layouts com ViewBinding**

---

## 🛠️ Tecnologias e Bibliotecas

- Kotlin + Android SDK
- Firebase Authentication
- Cloud Firestore (Realtime DB)
- Firebase Storage
- Picasso (carregamento de imagem)
- ViewBinding
- RecyclerView + Adapter personalizado
- TabLayout + ViewPager2
- Material Design Components

---

## 🧩 Estrutura Principal

```
├── activities
│   ├── LoginActivity.kt
│   ├── CadastroActivity.kt
│   ├── MainActivity.kt
│   ├── PerfilActivity.kt
│   └── MensagemsActivity.kt
│
├── fragments
│   ├── ConversasFragment.kt
│   └── ContatosFragment.kt
│
├── adapters
│   ├── ConversasAdapter.kt
│   ├── ContatosAdapter.kt
│   ├── MensagensAdapter.kt
│   └── ViewPageAdapter.kt
│
├── model
│   ├── Conversa.kt
│   └── Usuario.kt
│
├── utils
│   ├── Constantes.kt
│   ├── Extensoes.kt
│   └── Mensagem.kt
```

---

## 🚀 Como executar o projeto

1. Clone o repositório:
```bash
git clone https://github.com/HelioSaraiva/whatsapp-firebase-clone.git
```

2. Abra no Android Studio

3. Crie um projeto no [Firebase Console](https://console.firebase.google.com/) com:
   - Autenticação por e-mail/senha
   - Cloud Firestore
   - Storage (para fotos)

4. Configure o arquivo `google-services.json` no módulo `app/`

5. Execute no emululador ou dispositivo real

---

## 💼 Destaques para Recrutadores

Este projeto demonstra domínio de:

- Integração real com **Firebase Auth + Firestore**
- Uso de **arquitetura modular** com `Activities`, `Fragments` e `Adapters`
- Manipulação de listas e atualizações em tempo real com **SnapshotListeners**
- Boas práticas de UI/UX com Material Design
- Escrita limpa e clara de código, pronta para escalar com MVVM ou Jetpack Compose

🎯 Ideal para demonstrar habilidades em apps reais de chat, autenticação e manipulação de dados em tempo real.

---

## 🔧 Futuras melhorias

- [ ] Login com Google
- [ ] Notificações Push com Firebase Messaging
- [ ] Adição de status (estilo Stories)
- [ ] Chamadas de voz e vídeo (WebRTC)
- [ ] Arquitetura MVVM com ViewModel

---

## 📄 Licença

MIT License — sinta-se à vontade para usar e adaptar este projeto.

---

## 🙋 Sobre o autor

Desenvolvido por **Helio Saraiva Buzato**  
📧 buzato@hotmail.com  
🔗 [LinkedIn](https://linkedin.com/in/heliosaraivabuzato)  
🔗 [GitHub](https://github.com/HelioSaraiva)
