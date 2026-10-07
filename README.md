# Espresso — integração entre Activities e intents

[English version](README.en.md)

Cinco testes Java sobre o [IntentsBasicSample oficial do Android](https://github.com/android/testing-samples/tree/8c9df3a534ef99e44d481d96c00a5fc1970f7c70/ui/espresso/IntentsBasicSample), usando Espresso, Espresso Intents e ActivityScenario. O código da aplicação é baixado no commit fixado; somente o source set dos testes é configurado para esta suíte.

## Cenários e limites da integração

- O número digitado chega exatamente ao `ACTION_CALL`.
- A Activity de contato real do exemplo retorna seu número de demonstração.
- Um contato selecionado substitui o número anterior.
- Cancelar a seleção preserva o número digitado.
- Recriar a Activity preserva o campo ainda não enviado.

As chamadas telefônicas são sempre interceptadas pelo Espresso Intents; nenhuma ligação é feita. Dois cenários controlam o resultado da Activity de contato com stubs para verificar sucesso/cancelamento. Um terceiro executa a Activity real do exemplo. Ela é uma demo, não a agenda do dispositivo. O README distingue essas provas de uma integração real com contatos/telefonia.

## Execução

Linux, Java 17, Python 3.13 e Android SDK com emulador API 34. O Gradle Wrapper vem da fonte oficial fixada.

```bash
cp .env.example .env
python -m pip install -r requirements.txt
python prepare_app.py
chmod +x .upstream/ui/espresso/IntentsBasicSample/gradlew
.upstream/ui/espresso/IntentsBasicSample/gradlew -p .upstream/ui/espresso/IntentsBasicSample connectedDebugAndroidTest
```

O prepare confere o SHA e injeta `src/androidTest/java/ContactIntentsTest.java` em um source set isolado. Não altera a lógica do aplicativo. O checkout baixado, builds e resultados ficam ignorados; testes upstream não são somados como autoria deste portfólio.

## Resultados e triagem

[Actions](https://github.com/brunobaccari/espresso-android-intents/actions) compila app e instrumentação, executa os cinco testes e publica summary por caso. O artifact `android-results` guarda XML JUnit e o relatório HTML de instrumentação por 14 dias, inclusive saídas disponíveis quando há falha.

Skip, quantidade diferente de cinco, ausência de relatório ou falha reprova o gate. Diferença de intent/payload é investigada separadamente de erro de instalação ou emulador. Sem sleeps, reruns até verde ou dependência de contato real. Escopo Android; sem iOS, aparelho físico ou dados de clientes.

O summary do Actions lista cada cenário, duração, totais e motivo de bloqueio. O gate exige a quantidade prevista no workflow, sem falhas ou skips; JUnit ausente ou inválido reprova. O resumo também acompanha o artifact.

Husky: com Node 24 e as dependências da stack instalados, rode `npm ci` para ativar o pre-commit. `npm run check:local` verifica o diff, o gate dos relatórios e os checks de tipos/lint existentes. O hook também bloqueia arquivos ignorados no índice. Testes que usam navegador, emulador ou API continuam no CI.
