package com.gustavo.desafio1crud

import com.gustavo.desafio1crud.MyDataClasses.Nota

class ViewModelNota(val nota: Nota) {

    val materia: String
    val valor: String

    init {
        this.materia = nota.materia
        this.valor = nota.valor.toString()
    }
}
