package com.gustavo.desafio1crud.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.gustavo.desafio1crud.Adapters.NotaRecyclerSelection.Details
import com.gustavo.desafio1crud.MyDataClasses.Nota
import com.gustavo.desafio1crud.NotaFragment.OnListFragmentInteractionListener
import com.gustavo.desafio1crud.R
import com.gustavo.desafio1crud.ViewModelNota

import com.gustavo.desafio1crud.databinding.FragmentNotaBinding

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyNotaRecyclerViewAdapter(
    private val mValues: ArrayList<Nota>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyNotaRecyclerViewAdapter.ViewHolder>() {


    lateinit var selectionTracker: SelectionTracker<Long>
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
        return ViewHolder(view, FragmentNotaBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(ViewModelNota(mValues.get(position)),position)

    }

    override fun getItemCount(): Int = mValues.size

    override fun getItemId(position: Int): Long{
        return mValues.get(position).id}

    inner class ViewHolder(val view: View,val binding: FragmentNotaBinding) : RecyclerView.ViewHolder(binding.notaCard) {


        val itemDetails: Details
        private val mView: MaterialCardView = binding.notaCard

        init {
            itemDetails = Details()
        }

        fun bind(viewModelNota: ViewModelNota, position: Int) {
            binding.setViewModelNota(viewModelNota)
            binding.executePendingBindings()
            //inicializando o onClick para o Main

            with(mView) {
                tag = mValues[position]
                setOnClickListener(mOnClickListener)
            }
            itemDetails.nota = mValues[position]
            itemDetails.nota?.position=position
            itemDetails.adapterPosition= adapterPosition

            if(selectionTracker.isSelected(itemDetails.selectionKey)){
                mView.isSelected = true
            }else{
                mView.isSelected = false
            }
            mView.isChecked = mView.isSelected

        }


    }
}
