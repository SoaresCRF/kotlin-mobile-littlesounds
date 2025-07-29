# 📱 Little Sounds - App Infantil para Aprender Inglês
Little Sounds é um aplicativo educacional infantil desenvolvido em Kotlin para Android. Com uma abordagem lúdica, o app ajuda crianças a aprenderem palavras em inglês por meio de sons. Ao clicar nas imagens, um som correspondente é reproduzido, facilitando o aprendizado auditivo e visual.
> 💡 Este projeto utiliza o Supabase como banco de dados e está hospedado no Render.

## ✨ Funcionalidades principais
- Interface visual com imagens infantis coloridas e intuitivas.
- Reprodução de sons em inglês ao tocar nas imagens.
- Conteúdo educacional voltado para crianças.
- Design simples, seguro e de fácil navegação para crianças.

## 🛠️ Tecnologias utilizadas
- Android nativo
- Kotlin
- Supabase (PostgreSQL e Storage)
- API REST hospedada no Render (Node.js)
- MediaPlayer (player de áudio padrão do Android)

## 🔌 Como funciona a arquitetura
```plaintext
Android App
    ↓
API Backend (Node.js - Render)
    ↓
Supabase (Tabelas, URLs de áudio e imagem)
```
- O app busca as informações (imagens e sons) através de uma API intermediária que se comunica com o Supabase.
- O áudio é carregado via streaming a partir das URLs do Supabase Storage.

## 👶 Público-alvo
Crianças em fase de alfabetização que estão dando os primeiros passos no aprendizado do inglês.

## 📦 Download App
Baixe a versão mais recente do aplicativo para testar diretamente no seu dispositivo Android: [APK](https://github.com/SoaresCRF/kotlin-mobile-littlesounds/releases/download/v1.0.0/littlesounds-v1.0.0.apk "Download do APK") | [PlayStore](# "Ver na PlayStore")

## 📌 Melhorias futuras
- Publicação na Google Play Store.
- Adição de novas categorias de sons.

## 📄 Licença
Este projeto está sob a licença MIT.