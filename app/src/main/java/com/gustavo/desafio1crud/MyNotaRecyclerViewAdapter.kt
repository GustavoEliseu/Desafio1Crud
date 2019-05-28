package com.gustavo.desafio1crud

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.gustavo.desafio1crud.NotaFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_nota.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Implementar a seguir exemplo de MyAlunoRecyclerViewAdapter.
 */
class MyNotaRecyclerViewAdapter(
    private val mValues: ArrayList<Nota>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyNotaRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Nota
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_nota, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mMateriaTxt.text = item.materia
        holder.mNotaTxt.text = item.valor.toString()

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mMateriaTxt: TextView = mView.materia_nome
        val mNotaTxt: TextView = mView.materia_nota

        override fun toString(): String {
            return super.toString() + " '" + mMateriaTxt.text+","+mNotaTxt.text + "'"
        }
    }
}
