package com.gustavo.desafio1crud

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_aluno_list.view.*
import android.app.DatePickerDialog
import android.graphics.Rect
import android.os.Build
import android.transition.Transition
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.transition.Explode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_add_aluno.view.*
import kotlinx.android.synthetic.main.fragment_aluno.view.*
import kotlinx.android.synthetic.main.fragment_nota_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [AlunoFragment.OnListFragmentInteractionListener] interface.
 */
class AlunoFragment : androidx.fragment.app.Fragment(),View.OnClickListener, View.OnLongClickListener {



    // TODO: Customize parameters
    private var columnCount = 1
    val myCalendar:Calendar = Calendar.getInstance();

    lateinit var mRecycler:RecyclerView
    lateinit var alunos:ArrayList<Aluno>

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aluno_list, container, false)
        mRecycler =view.mRVList
        alunos = ArrayList<Aluno>()

            with(mRecycler) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val dbHandler = DBHelper(activity!!, null)
                val cursor = dbHandler.getAllAlunos()
                try{

                    cursor!!.moveToFirst()


                    alunos.add(0,Aluno(
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_NOME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_DATA)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_MATRICULA))
                        )
                    )

                    while (cursor.moveToNext()) {
                        alunos.add(cursor.position,Aluno(
                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_NOME)),
                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_DATA)),
                            cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_MATRICULA))
                        )
                        )
                    }
                    cursor.close()

                }catch(t:Throwable){
                    t.printStackTrace()

                    Toast.makeText(context,"falhou",Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    e.printStackTrace()
                }finally {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }
                dbHandler.close()
                mRecycler.setAdapter(MyAlunoRecyclerViewAdapter(alunos, listener))
                mRecycler.adapter!!.notifyDataSetChanged()

            }

        (activity as MainActivity).FAB_add.setOnClickListener(this)

        return view
        }


    fun updateLabel(v:View) {
        val myFormat:String = "dd/MM/yy"; //In which you need put here
        val sdf:SimpleDateFormat = SimpleDateFormat(myFormat, Locale.ENGLISH);

        v.date_edit_text.setText(sdf.format(myCalendar.getTime()));
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.FAB_add ->{
                val meuBuilder:MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)

                val v:View = layoutInflater.inflate(R.layout.dialog_add_aluno,null,false)
                meuBuilder.setView(v)
                v.date_edit_text.setKeyListener(null);
                val teste= DatePicker(context)
                teste.setMaxDate(Date().time)
                teste.minDate=Date(1900,1,0).time


                val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateLabel(v)
                }

                v.date_edit_text.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
                    if(v.isFocused==true){
                        DatePickerDialog(context!!, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show()
                        try{
                            hideKeyboard(activity!!,v)
                        }catch(t:Throwable){
                            t.printStackTrace()
                        }catch(e:Exception){
                            e.printStackTrace()}
                    }
                })


                meuBuilder.setCancelable(true)

                meuBuilder.setPositiveButton("add",
                    DialogInterface.OnClickListener { dialog,
                                                      which ->
                        var nome = v.nome_edit_text.text
                        var data = v.date_edit_text.text

                        val dbHandler = DBHelper(context!!, null)
                        val aluno = Aluno(nome.toString(),data.toString())
                        try{
                            val myLong:Long=dbHandler.addAluno(aluno)
                            if(myLong!=-1L){
                            alunos.add(aluno)
                            Toast.makeText(context!!, nome.toString()+ " foi adicionado ao database", Toast.LENGTH_LONG).show()
                            mRecycler.adapter!!.notifyItemInserted(alunos.size-1)
                            }else{
                                mRecycler.adapter!!.notifyItemInserted(alunos.size)
                                Toast.makeText(context!!,"Aluno já existente!",Toast.LENGTH_SHORT).show()

                            }

                        }catch(e:Exception){
                            e.printStackTrace()
                        }catch(t:Throwable){
                            t.printStackTrace()
                        }finally {
                            dbHandler.close()
                        }



                    }

                )

                meuBuilder.create().show();
            }
        }
    }
    //função para esconder o teclado na hora de selecionar a data
    fun hideKeyboard( context:Context,v: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.getRootView()!!.getWindowToken(), 0)
    }


    override fun onLongClick(v: View?): Boolean {
        v?.isSelected=true
        return false
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Aluno?,v:View)
        fun onListFragmentInteraction(v: View)
    }

    interface OnLongClickListener{
        abstract fun onLongClick(v: View,position:Int): Boolean
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AlunoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }




    inner class MyAlunoRecyclerViewAdapter(
        private val mValues: ArrayList<Aluno>,
        private val mListener: AlunoFragment.OnListFragmentInteractionListener?
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<MyAlunoRecyclerViewAdapter.ViewHolder>() {

        private val mOnClickListener: View.OnClickListener
        private val mOnLongClickListener: View.OnLongClickListener


        init {
            mOnClickListener = View.OnClickListener { v ->
                val item = v.tag as Aluno
                // Notify the active callbacks interface (the activity, if the fragment is attached to
                // one) that an item has been selected.
                mListener?.onListFragmentInteraction(item,v)
            }
            mOnLongClickListener = View.OnLongClickListener { v ->
                val item = v.tag as Aluno
                // Notify the active callbacks interface (the activity, if the fragment is attached to
                // one) that an item has been selected.
                mListener?.onListFragmentInteraction(v)
                true
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_aluno, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mValues[position]
            holder.myNome.text = item.nome
            holder.myMatricula.text = item.matricula.toString()
            holder.myData.text = item.data

            with(holder.mView) {
                tag = item
                setOnClickListener(mOnClickListener)
                setOnLongClickListener(mOnLongClickListener)
            }

        }

        override fun getItemCount(): Int = mValues.size

        inner class ViewHolder(val mView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
            val myMatricula: TextView = mView.aluno_matricula
            val myNome: TextView = mView.materia_nome
            val myData: TextView = mView.materia_nota

            override fun toString(): String {
                return super.toString() + " '" + myNome.text + ", "+myData.text+"'"
            }
        }
    }
}


