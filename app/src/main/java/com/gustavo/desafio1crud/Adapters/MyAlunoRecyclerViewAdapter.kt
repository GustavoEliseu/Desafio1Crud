package com.gustavo.desafio1crud.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import com.google.android.material.card.MaterialCardView
import com.gustavo.desafio1crud.AlunoFragment
import com.gustavo.desafio1crud.DataBase.DBHelper
import com.gustavo.desafio1crud.MyDataClasses.Aluno
import com.gustavo.desafio1crud.R
import com.gustavo.desafio1crud.ViewModelAluno
import com.gustavo.desafio1crud.databinding.FragmentAlunoBinding
import kotlinx.android.synthetic.main.fragment_aluno.view.*
import java.util.*


class MyAlunoRecyclerViewAdapter(
    private val mValues: ArrayList<Aluno>,
    private val mListener: AlunoFragment.OnListFragmentInteractionListener?,
    private val context:Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<MyAlunoRecyclerViewAdapter.ViewHolder>() {


    var mSelectionTracker: SelectionTracker<Long>? = null
    private val mOnClickListener: View.OnClickListener



    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Aluno
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_aluno, parent, false)
        return ViewHolder(view,FragmentAlunoBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ViewModelAluno(mValues.get(position)),position)
    }

    fun deleteAluno(matricula:Int, position:Int){
        val DBHandler: DBHelper= DBHelper(context, null)
        val controle =DBHandler.deleteAluno(matricula);
        if(controle == true) {
            mValues.remove(mValues.get(position))
            this.notifyItemRemoved(position)
        }
    }




    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val view:View,val binding: FragmentAlunoBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.myCardView),View.OnClickListener {
        override fun onClick(v: View?) {
            deleteAluno(mValues[mPosition].matricula,mPosition)
        }

        private val mView:MaterialCardView = binding.myCardView
        private val mDeleteButton = view.myCardView.delete_aluno_btn
        private var mPosition = -1

        fun bind(viewModelAluno: ViewModelAluno, position: Int) {
            binding.setViewModelAluno(viewModelAluno)
            binding.executePendingBindings()
            mPosition = position;

            with(mView) {
                tag = mValues[position]
                setOnClickListener(mOnClickListener)
            }
            with(mDeleteButton){
                setOnClickListener(this@ViewHolder)
            }
        }

    }

    //id estatico e unico para cada viewholder para caso seja nescessário alguma interação com o database
    override fun getItemId(position: Int): Long {
        return mValues.get(position).matricula.toLong()
    }
}