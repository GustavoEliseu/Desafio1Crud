package com.gustavo.desafio1crud

import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.gustavo.desafio1crud.databinding.FragmentAlunoBinding

import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import java.util.*
import android.view.*
import android.view.MotionEvent


class MyAlunoRecyclerViewAdapter(
    private val mValues: ArrayList<Aluno>,
    private val mListener: AlunoFragment.OnListFragmentInteractionListener?,
    private val context:Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<MyAlunoRecyclerViewAdapter.ViewHolder>() {


    var mSelectionTracker: SelectionTracker<Long>? = null
    private val mOnClickListener: View.OnClickListener
    private val mOnLongClickListener: View.OnLongClickListener



    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Aluno
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item,v,mSelectionTracker!!.selection.size())
        }
        mOnLongClickListener = View.OnLongClickListener { v ->
            val item = v.tag as Aluno
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(v,mSelectionTracker!!.selection.size())
            true
        }
    }

    fun setSelectionTracker(mSelectionTracker: SelectionTracker<Long>) {
        this.mSelectionTracker = mSelectionTracker
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_aluno, parent, false)
        return ViewHolder(view,FragmentAlunoBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ViewModelAluno(mValues.get(position)),position)
    }


    class Details : ItemDetailsLookup.ItemDetails<Long>() {

        var position: Long = 0

        override fun getPosition(): Int {
            return position.toInt()
        }

        @Nullable
        override fun getSelectionKey(): Long? {
            return position
        }

        override fun inSelectionHotspot(@NonNull e: MotionEvent): Boolean {

            return true
        }
    }

    internal class KeyProvider(adapter: RecyclerView.Adapter<ViewHolder>) :
        ItemKeyProvider<Long>(ItemKeyProvider.SCOPE_MAPPED) {

        @Nullable
        override fun getKey(position: Int): Long? {
            return position.toLong()
        }


        override fun getPosition(@NonNull key: Long): Int {
            return key.toInt()
        }
    }

    internal class DetailsLookup(private val recyclerView: RecyclerView, val context: Context) : ItemDetailsLookup<Long>() {

        var view:View? = null
        var isLong:Boolean=false
        var itemD:Details? = null

        fun teste(){
            val viewHolder = recyclerView.getChildViewHolder(view!!)
            if (viewHolder is ViewHolder) {
                itemD= viewHolder.getItemDetails()
            }
        }


        @Nullable
        override fun getItemDetails(@NonNull e: MotionEvent): ItemDetailsLookup.ItemDetails<Long>? {
            view=  recyclerView.findChildViewUnder(e.getX(), e.getY());

            if(view!=null&& (recyclerView.adapter as MyAlunoRecyclerViewAdapter).mSelectionTracker != null)/*
                &&(e.action==MotionEvent.ACTION_UP)
                ||e.action==MotionEvent.ACTION_DOWN)*/ {
                teste()
            }
            return itemD
        }
    }

    internal class Predicate : SelectionTracker.SelectionPredicate<Long>() {

        override fun canSetStateForKey(@NonNull key: Long, nextState: Boolean): Boolean {
            return true
        }

        override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
            return true
        }

        override fun canSelectMultiple(): Boolean {
            return true
        }

    }


    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val view:View,val binding:FragmentAlunoBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.myCardView) {

        private val details: Details = Details()
        private val mView:MaterialCardView = binding.myCardView



        fun bind(viewModelAluno: ViewModelAluno, position: Int) {
            details.position = position.toLong()
            binding.setViewModelAluno(viewModelAluno)
            binding.executePendingBindings()
            //inicializando o onClick para o Main, e o LongClick(pois se o mesmo não for configurado, será considerado um click normal)
            with(mView) {
                tag = mValues[position]
                setOnClickListener(mOnClickListener)
                setOnLongClickListener(mOnLongClickListener)
            }
            if (mSelectionTracker != null) {
                if (mSelectionTracker!!.isSelected(details.selectionKey)) {
                    binding.getRoot().setActivated(true)
                } else {
                    binding.getRoot().setActivated(false)
                }
                mView.isChecked=binding.getRoot().isActivated
            }
        }


        internal fun getItemDetails(): Details {
            return details
        }
    }


    override fun getItemId(position: Int): Long {
        return mValues.get(position).matricula.toLong()
    }
}