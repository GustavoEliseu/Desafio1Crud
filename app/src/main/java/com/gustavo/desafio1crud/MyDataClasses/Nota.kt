package com.gustavo.desafio1crud.MyDataClasses



class Nota(val aluno: Aluno, val materia: String, var valor: Int, var id: Long, var position:Int) {

    constructor(aluno: Aluno, materia: String, valor: Int):this(aluno,materia,valor,0L, -1){

    }
    constructor(aluno: Aluno, materia: String, valor: Int,id:Long):this(aluno,materia,valor,id, -1){

    }
}