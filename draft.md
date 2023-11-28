# Troca de chaves para autenticação de mensagens

1. Começamos a conexão entre cada par de cliente e servidor com a troca de chaves [Diffie-Hellman](https://en.wikipedia.org/wiki/Diffie–Hellman_key_exchange)

2. Depois deste passo o servidor e os nodes podem comunicar em segurança.

3. Quando dois nodes querem estabelecer uma ligação entre si, recorrem ao servidor através das suas conexões e obtém uma chave partilhada que podem utilizar para o [HMAC](https://en.wikipedia.org/wiki/HMAC#Details).
Este algoritmo pode ser usado tanto para a autenticação da mensagem (autor) como para a verificação da sua integridade.
