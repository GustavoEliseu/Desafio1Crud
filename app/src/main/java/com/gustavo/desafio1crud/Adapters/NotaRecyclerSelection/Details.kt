package com.gustavo.desafio1crud.Adapters.NotaRecyclerSelection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import com.gustavo.desafio1crud.MyDataClasses.Nota

class Details(
    var nota: Nota? = null,
    var adapterPosition: Int = -1
) : ItemDetailsLookup.ItemDetails<Long>() {

    /*
     * Retorna a posição do adaptador do item
     * (ViewHolder.adapterPosition).
     * */
    override fun getPosition()
            = adapterPosition

    /*
     * Retorna a entidade que é a chave de seleção do item.
     * */
    override fun getSelectionKey()
            = nota!!.id

    /*
     * Retorne "true" se o item tiver uma chave de seleção. Se true
     * não for retornado o item em foco (acionado pelo usuário) não
     * será selecionado.
     *
     * Aqui é possível colocar a lógica de negócio necessária para
     * indicar quais itens podem ou não ser selecionados.
     * */
    override fun inSelectionHotspot( e: MotionEvent)
            = true
}