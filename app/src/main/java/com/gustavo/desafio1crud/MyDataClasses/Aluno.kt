package com.gustavo.desafio1crud.MyDataClasses

class Aluno( var nome: String, var data:String,var matricula: Int ) {

    constructor(nome:String, data: String) : this(nome,data,-1) {

    }

}