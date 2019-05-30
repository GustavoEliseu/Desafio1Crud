package com.gustavo.desafio1crud

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gustavo.desafio1crud.Adapters.MyNotaRecyclerViewAdapter
import com.gustavo.desafio1crud.Adapters.NotaRecyclerSelection.DetailsLookup
import com.gustavo.desafio1crud.Adapters.NotaRecyclerSelection.myItemKeyProvider
import com.gustavo.desafio1crud.DataBase.DBHelper
import com.gustavo.desafio1crud.MyDataClasses.Aluno
import com.gustavo.desafio1crud.MyDataClasses.Nota
import com.gustavo.desafio1crud.TextFilter.MinMaxFilter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add_aluno.view.*
import kotlinx.android.synthetic.main.dialog_add_nota.view.*
import kotlinx.android.synthetic.main.fragment_aluno.view.*
import kotlinx.android.synthetic.main.fragment_nota_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [NotaFragment.OnListFragmentInteractionListener] interface.
 */


class NotaFragment() : Fragment(),View.OnClickListener,ActionMode.Callback {

    val myCalendar:Calendar = Calendar.getInstance();

    override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        val inflater = actionMode.menuInflater
        inflater.inflate(R.menu.selected_menu, menu)
        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean {
        val DBHandler:DBHelper=  DBHelper(context!!, null)
        val iterator:Iterator<Long> =selectionTracker.selection.iterator()
        when(menuItem.itemId){
            R.id.delete_item->

            if(context!=null){
                val mSelection: Selection<Long> = selectionTracker.selection
                val dbHandler = DBHelper(context!!,null)
                val arrayRmv = IntArray(selectionTracker.selection.size())
                var i:Int = 0
                while(i<mSelection.size()){
                    arrayRmv[i] = notas.indexOf(
                        notas.filter{
                                nota -> nota.id == mSelection.elementAt(i)
                        }.single())
                    val x:Boolean = dbHandler.deleteNota(notas.get(arrayRmv[i]).id,notas.get(arrayRmv[i]).aluno.matricula)
                    if(x==true) {
                        i++
                    }
                }
                selectionTracker.clearSelection()
                while(i>0) {
                    i--
                    notas.remove(notas.get( arrayRmv[i]))

                    //Necessário, pois NotifyItemRemoved estava causando crashs quando selecionava outra nota, provavelmente devido a como o ID é gerado. Verificar!!
                    mAdapter.notifyDataSetChanged()

                }
                dbHandler.close()

            }

        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode) {
        selectionTracker.clearSelection()
        isInActionMode=false
    }

    lateinit var selectionTracker: SelectionTracker<Long>
    var actionMode: ActionMode? = null


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.FAB_add ->{
                selectionTracker.clearSelection()
                val meuBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)

                val view:View = layoutInflater.inflate(R.layout.dialog_add_nota,null,false)
                meuBuilder.setView(view)

                view.valor_edit_text.setFilters(arrayOf<InputFilter>(
                    MinMaxFilter(
                        0,
                        10
                    )
                ))


                meuBuilder.setCancelable(true)

                meuBuilder.setPositiveButton("add",
                    DialogInterface.OnClickListener { _,
                                                      which ->
                        var materia = view.materia_edit_text
                        var valor = view.valor_edit_text

                        val dbHandler = DBHelper(context!!, null)
                        val nota = Nota(
                            aluno,
                            materia.text.toString(),
                            Integer.valueOf(valor.text.toString())
                        )
                        try{

                        dbHandler.addNota(nota)
                            notas.add(nota)
                        Toast.makeText(context!!, materia.text.toString()+ " foi adicionado ao database", Toast.LENGTH_LONG).show()

                            mAdapter!!.notifyItemInserted(notas.size-1)
                        }catch(e:Exception){
                            e.printStackTrace()
                        }
                        catch(t:Throwable){
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


    private var columnCount = 1
    lateinit var mRecycler:RecyclerView
    lateinit var mAdapter:MyNotaRecyclerViewAdapter
    private lateinit var aluno: Aluno
    private var notas:ArrayList<Nota> = ArrayList<Nota>()
    private var isInActionMode:Boolean = false

    private var listener: OnListFragmentInteractionListener? = null
    constructor(aluno: Aluno):this(){
        this.aluno=aluno
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }


    }
    override fun onActivityCreated( savedInstanceState: Bundle? ) {
        super.onActivityCreated( savedInstanceState )

        initSelectionTracker(savedInstanceState)
    }

    override fun onSaveInstanceState( outState: Bundle ) {
        super.onSaveInstanceState(outState)
        selectionTracker.onSaveInstanceState(outState)

        outState.putBoolean("ActionMode", isInActionMode);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nota_list, container, false)
        view.alunoCard.txt_nome.text = aluno.nome
        view.alunoCard.txt_data.text = aluno.data
        view.alunoCard.aluno_matricula.text = aluno.matricula.toString()

        view.alunoCard.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View){
                val meuBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                    .setTitle(getString(R.string.mudar_nome))
                    .setCancelable(true)
                    .setMessage(getString(R.string.deseja_edit))
                meuBuilder.setNegativeButton(getString(R.string.cancelar),null)

                meuBuilder.setPositiveButton(getString(R.string.sim),object: DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val meuBuilder2:MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)

                        val v2:View = layoutInflater.inflate(R.layout.dialog_add_aluno,null,false)
                        meuBuilder2.setView(v2)
                        v2.date_edit_text.setKeyListener(null);
                        val teste= DatePicker(context)
                        teste.setMaxDate(Date().time)
                        teste.minDate= Date(1900,1,0).time


                        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            myCalendar.set(Calendar.YEAR, year)
                            myCalendar.set(Calendar.MONTH, monthOfYear)
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            updateLabel(v2)
                        }

                        v2.date_edit_text.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
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


                        meuBuilder2.setCancelable(true)

                        meuBuilder2.setPositiveButton("Modificar",null

                        )
                        val meuAlert = meuBuilder2.create()


                        meuAlert.show()

                        meuAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
                            var nome = v2.nome_edit_text
                            var data = v2.date_edit_text
                            if(!nome.text!!.isEmpty()||!data.text!!.isEmpty()) {
                                val dbHandler = DBHelper(context!!, null)
                                val mAluno =
                                    Aluno(nome.text.toString(), data.text.toString())
                                try {
                                    val updateSucessfull: Boolean = dbHandler.updateAluno(nome.text.toString(), data.text.toString(),aluno.matricula)
                                    if (updateSucessfull == true) {

                                        //informa que o usuário foi adicionado corretamente
                                        Toast.makeText(
                                            context!!,
                                            nome.text.toString() + " foi modificado no database",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        aluno.data = mAluno.data
                                        aluno.nome = mAluno.nome
                                        //atualiza o texto do aluno
                                        view.alunoCard.txt_nome.text = mAluno.nome
                                        view.alunoCard.txt_data.text = mAluno.data
                                    } else {
                                        Toast.makeText(context!!, "Valor invalido ou Aluno já existente!", Toast.LENGTH_SHORT).show()
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } catch (t: Throwable) {
                                    t.printStackTrace()
                                } finally {
                                    dbHandler.close()
                                    meuAlert.dismiss()
                                }
                            }else{
                                Toast.makeText(context,"Valores invalidos",Toast.LENGTH_SHORT).show()
                            }
                        })


                    }

                })
                meuBuilder.create().show()
            }
        })






        mRecycler= view.myNotaRecycler
        // Set the adapter
            with(mRecycler) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                val dbHandler = DBHelper(activity!!, null)
                if(notas.size<=0){
                val cursor = dbHandler.getAllNotasAluno(aluno.matricula)
                try {

                    cursor!!.moveToFirst()


                    notas.add(
                        0, Nota(
                            aluno,
                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_MATERIA)),
                            cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_NOTA)),
                            cursor.getLong(cursor.getColumnIndex("rowid"))
                        )
                    )


                    while (cursor.moveToNext()) {
                        notas.add(
                            cursor.position, Nota(
                                aluno,
                                cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_MATERIA)),
                                cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_NOTA)),
                                cursor.getLong(cursor.getColumnIndex("rowid"))
                            )
                        )
                    }
                    cursor.close()

                }catch(t:Throwable){
                    t.printStackTrace()


                }catch (e:Exception){
                    e.printStackTrace()
                }finally {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }
                }
                dbHandler.close()
                mAdapter=MyNotaRecyclerViewAdapter(notas, listener)
                mAdapter.setHasStableIds(true)
                mRecycler.setAdapter(mAdapter)
                mAdapter!!.notifyDataSetChanged()
            }
        (activity as MainActivity).FAB_add.setOnClickListener(this)
        retainInstance = true
        return view
    }

    //função para edição do editText Calendário
    fun updateLabel(v:View) {
        val myFormat:String = "dd/MM/yy";//In which you need put here
        val sdf: SimpleDateFormat = SimpleDateFormat(myFormat, Locale.ENGLISH)

        v.date_edit_text.setText(sdf.format(myCalendar.getTime()))
    }
    //função para esconder o teclado na hora de selecionar a data
    fun hideKeyboard( context:Context,v: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.getRootView()!!.getWindowToken(), 0)
    }


    fun initSelectionTracker(savedInstanceState: Bundle?) {


        selectionTracker = SelectionTracker.Builder<Long>(
            "my-selection-tracker-id",
            mRecycler,
            myItemKeyProvider(notas),
            DetailsLookup(mRecycler),
            StorageStrategy.createLongStorage()
        )
            .build()

        selectionTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {

                override fun onItemStateChanged(
                    key: Long,
                    selected: Boolean
                ) {
                    super.onItemStateChanged(key, selected)

                }

                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    if (selectionTracker.hasSelection() && actionMode == null) {
                        actionMode = (activity as AppCompatActivity).startSupportActionMode(
                            this@NotaFragment
                        )
                        isInActionMode = true
                    } else if (!selectionTracker.hasSelection() && actionMode != null) {
                        actionMode!!.finish()
                        actionMode = null
                        isInActionMode = false
                    } else {
                    }
                }

                override fun onSelectionRefresh() {
                    super.onSelectionRefresh()
                }

                override fun onSelectionRestored() {
                    super.onSelectionRestored()
                }

            }
        )


        if (savedInstanceState != null) {
            if(savedInstanceState.getBoolean("ActionMode", false)){
                actionMode = (activity as AppCompatActivity).startSupportActionMode(
                this@NotaFragment
                )
            }
            selectionTracker.onRestoreInstanceState( savedInstanceState )
        }
        (mAdapter as MyNotaRecyclerViewAdapter).selectionTracker = selectionTracker
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

        fun onListFragmentInteraction(item: Nota)
    }

    companion object {


        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            NotaFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
