package com.gustavo.desafio1crud.Adapters.NotaRecyclerSelection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.gustavo.desafio1crud.Adapters.MyNotaRecyclerViewAdapter

class DetailsLookup (val rvList: RecyclerView ):
    ItemDetailsLookup<Long>() {

        /*
         * Retorna o ItemDetails para o item sob o evento
         * (MotionEvent) ou nulo caso não haja um.
         * */
        override fun getItemDetails( event: MotionEvent): ItemDetails<Long>? {

            val view = rvList.findChildViewUnder( event.x, event.y )

            if( view != null ){
                val holder = rvList.getChildViewHolder( view )

                /*
                 * CarsAdapter é um adapter convencional vinculado ao
                 * RecyclerView alvo. O ViewHolder dele se chama
                 * CarHolder.
                 *
                 * O bloco if() é necessário somente se DetailsLookup
                 * estiver em um contexto onde mais de um ViewHolder
                 * estiver sendo utilizado.
                 * */
                if( holder is MyNotaRecyclerViewAdapter.ViewHolder ){
                    return holder.itemDetails
                }
            }

            return null
        }
    }