package com.gustavo.desafio1crud

import java.sql.Date

class Aluno( var nome: String, var data:String,var matricula: Int ) {

    constructor(nome:String, data: String) : this(nome,data,-1) {
        this.nome = nome
        this.data = data
    }

}