# Mensagens do Professor
Prezados alunos,

O desenvolvimento do sistema deverá conter:
- Entrada de dados de acordo com o usuário que irá logar no sistema (Segurança de Acesso ao Sistema)
- Os dados de entrada pode ser manualmente ou automatizado
- Deverá demonstrar o resultado final dos dados podendo demonstrar um relatório 

No dia da apresentação o grupo deverá rodar o sistema demonstrando passo a passo seu funcionamento. Logo em seguida deverá abrir o código principal do projeto e fazer uma breve demonstração de seu funcionamento.

# Descrição do Projeto Escolhido

## 3º Modelo de Projeto - Jogo Investigativo

### Estrutura de Armazenamento:

Linked List e Árvore

### Descrição:

Um jogo de investigação em texto com um número fixo de cenas (3 a 5). O desfecho do caso é deduzido automaticamente pelo sistema com base na ordem exata das evidências que o jogador escolhe coletar. A Lista Encadeada atua como o histórico da investigação, registrando cronologicamente as coletas. A cada cena, o sistema cruza a lista do jogador com a Árvore (que funciona como o mapa hierárquico de dependência lógica das pistas) para determinar quais evidências ficam disponíveis para coleta nos textos. Se a sequência de pistas da lista levar a uma conclusão errada, o jogo reinicia e renderiza o caminho percorrido no terminal para demonstrar visualmente o caminho investigativo falho.

