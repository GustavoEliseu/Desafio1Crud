# Desafio1Crud
Aplicativo passado como desafio.
Resolvi utilizar SQLITE e Kotlin além de não utilizar bibliotecas de terceiros para revisar Kotlin.

Na primeira tela(FragmentAluno), temos a lista de alunos clicaveis e botões para deletar os alunos( não coloquei confimação para agilizar os testes)
Temos também um Floating Action Button para adicionar um novo aluno. Quando este é clicado, abre-se o dialog solicitando o nome e a data de nascimento(datePicker).

<img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/FragmentAluno.jpg" height="480" width="240"><img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/addAluno.png" height="480" width="240">


Quando ambos os valores forem verificados o aluno é adicionado ao banco de dados
(não quis limitar data de nascimento, porém para implementar, é necessário apenas verificar a data atual com Calendar.time)

<img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/addAluno.png?raw=true" height="480" width="240">

Ao selecionar um aluno, os dados do mesmo são repassados para a próxima atividade. Nela utiliza-se a matricula do aluno para pesquisar as notas dele e gerar a lista.

<img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/onClickAluno.png?raw=true" height="380" width="1000">



Nesta tela o fab continua, agora é utilizado para se adicionar notas, de forma semelhante.
Quando as notas são selecionadas, o actionMode é ativado permitindo que o usuário delete as notas. Isto foi feito utilizando a biblioteca RecyclerView-Selection da google. Nunca havia utilizado ela, por isso reso  lvi experimentar.

<img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/fragmentNota.png?raw=true" height="480" width="240"><img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/actionMode.png?raw=true" height="480" width="240"><img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/addNota.png?raw=true" height="480" width="240">

Também nesta página o botão delete_aluno foi substituido pelo edit_aluno, que gera um dialog igual ao anterior mas previamente preenchido com os dados atuais do aluno.

A seguir seguem alguns Snippet da Main Activity( controle de back pressed e onStart" )

<img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/onBackPressed.png?raw=true" height="300" width="1000">

<img src="https://github.com/GustavoEliseu/imagensDesafio1Crud/blob/master/onStartMain.png?raw=true" height="240" width="1000">


Deveria ter implementado o onBackPressed nos fragments em sí, neste caso especifico. Porém resolvi deixar desta maneira para caso eu resolvesse aprimorar o código mais a frente.


TODO - Se eu conseguir temmpo ainda hoje, acrescentarei uma busca utilizando o arrayList de alunos, e colocarei o botão de busca na actionBar
