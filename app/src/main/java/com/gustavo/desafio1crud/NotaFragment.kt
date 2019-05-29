package com.gustavo.desafio1crud

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.Toast
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
import kotlinx.android.synthetic.main.dialog_add_nota.view.*
import kotlinx.android.synthetic.main.fragment_aluno.view.*
import kotlinx.android.synthetic.main.fragment_nota_list.view.*


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [NotaFragment.OnListFragmentInteractionListener] interface.
 */
//TODO -- IMPLEMENTAR ONCLICK E DIALOG PARA EDITAR NOME DO ALUNO
//TODO -- Implementar corretamente o savedStates do NotaFragment
class NotaFragment() : Fragment(),View.OnClickListener,ActionMode.Callback {

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
                    Log.e("Deleted: ",notas.get( arrayRmv[i]).materia)
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
    }

    lateinit var selectionTracker: SelectionTracker<Long>
    var actionMode: ActionMode? = null


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.FAB_add ->{
                selectionTracker.clearSelection()
                val meuBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.addAlunoTitle)

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
        super.onSaveInstanceState( outState )
        selectionTracker.onSaveInstanceState( outState )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nota_list, container, false)
        view.alunoCard.txt_nome.text = aluno.nome
        view.alunoCard.txt_data.text = aluno.data
        view.alunoCard.aluno_matricula.text = aluno.matricula.toString()
        mRecycler= view.myNotaRecycler
        // Set the adapter
            with(mRecycler) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val dbHandler = DBHelper(activity!!, null)
                val cursor = dbHandler.getAllNotasAluno(aluno.matricula)
                try{

                    cursor!!.moveToFirst()


                    notas.add(0, Nota(
                        aluno,
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_MATERIA)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_NOTA)),
                        cursor.getLong(cursor.getColumnIndex("rowid"))
                    ))


                    while (cursor.moveToNext()) {
                        notas.add(cursor.position, Nota(
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


    fun initSelectionTracker(savedInstanceState: Bundle?){

        selectionTracker = SelectionTracker.Builder<Long>(
            "id-unico-do-objeto-de-selecao",
            mRecycler,
            myItemKeyProvider(notas),
            DetailsLookup( mRecycler ),
            StorageStrategy.createLongStorage()
        )
            .build()

        selectionTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>(){

                override fun onItemStateChanged(
                    key: Long,
                    selected: Boolean ) {
                    super.onItemStateChanged( key, selected )

                }

                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    if (selectionTracker.hasSelection() && actionMode == null) {
                        actionMode = (activity as AppCompatActivity).startSupportActionMode(
                            this@NotaFragment
                        )

                    } else if (!selectionTracker.hasSelection() && actionMode != null) {
                        actionMode!!.finish()
                        actionMode = null
                    } else {
                    }
                    val itemIterable: Iterator<Long> = selectionTracker.getSelection().iterator();
                    while (itemIterable.hasNext()) {
                        Log.i("Item Iterable: ", itemIterable.next().toString());
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
        (mAdapter as MyNotaRecyclerViewAdapter).selectionTracker = selectionTracker

        if( savedInstanceState != null ){
            selectionTracker.onRestoreInstanceState( savedInstanceState )
        }
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
