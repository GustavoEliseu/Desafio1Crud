package com.gustavo.desafio1crud

import com.gustavo.desafio1crud.MyDataClasses.Aluno

class ViewModelAluno(aluno: Aluno) {

    val nome: String
    val data: String
    val matricula: String

    init {
        this.nome = aluno.nome
        this.data = aluno.data
        this.matricula = aluno.matricula.toString()
    }
}
