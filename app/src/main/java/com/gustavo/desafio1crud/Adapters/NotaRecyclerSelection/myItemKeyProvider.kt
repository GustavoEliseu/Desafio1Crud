package com.gustavo.desafio1crud.Adapters.NotaRecyclerSelection

import androidx.recyclerview.selection.ItemKeyProvider
import com.gustavo.desafio1crud.MyDataClasses.Nota


class myItemKeyProvider( val notas: List<Nota> ):
    ItemKeyProvider<Long>( ItemKeyProvider.SCOPE_MAPPED ) {

    /*
     * Retorna a chave de seleção na posição do adaptador fornecida ou
     * então retorna null.
     * */
    override fun getKey( position: Int )
            = notas[position].id

    /*
     * Retorna a posição correspondente à chave de seleção, ou
     * RecyclerView.NO_POSITION em caso de null em getKey().
     * */
    override fun getPosition( key: Long )
            = notas.indexOf(
        notas.filter{
                nota -> nota.id == key
        }.firstOrNull()
    )
}