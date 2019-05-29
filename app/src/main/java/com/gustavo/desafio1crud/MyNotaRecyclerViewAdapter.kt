package com.gustavo.desafio1crud

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.gustavo.desafio1crud.NotaFragment.OnListFragmentInteractionListener
import com.gustavo.desafio1crud.databinding.FragmentNotaBinding

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
        return ViewHolder(view,FragmentNotaBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(ViewModelNota(mValues.get(position)),position)

    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val view: View,val binding: FragmentNotaBinding) : RecyclerView.ViewHolder(binding.notaCard) {

        private val details: MyAlunoRecyclerViewAdapter.Details = MyAlunoRecyclerViewAdapter.Details()
        private val mView: MaterialCardView = binding.notaCard
        override fun toString(): String {
            return super.toString() + ""
        }

        fun bind(viewModelNota: ViewModelNota, position: Int) {
            details.position = position.toLong()
            //binding.setViewModelNota(viewModelNota)
            binding.executePendingBindings()
            //inicializando o onClick para o Main, e o LongClick(pois se o mesmo não for configurado, será considerado um click normal)
            with(mView) {
                tag = mValues[position]
                setOnClickListener(mOnClickListener)
                //setOnLongClickListener(mOnLongClickListener)
            }
            /*if (mSelectionTracker != null) {
                if (mSelectionTracker!!.isSelected(details.selectionKey)) {
                    binding.getRoot().setActivated(true)
                } else {
                    binding.getRoot().setActivated(false)
                }
                mView.isChecked=binding.getRoot().isActivated
            }*/
        }


        internal fun getItemDetails(): MyAlunoRecyclerViewAdapter.Details {
            return details
        }
    }
}
