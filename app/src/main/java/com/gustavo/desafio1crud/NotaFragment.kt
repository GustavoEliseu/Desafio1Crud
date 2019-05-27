package com.gustavo.desafio1crud

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_add_aluno.view.*
import kotlinx.android.synthetic.main.dialog_add_nota.view.*
import kotlinx.android.synthetic.main.fragment_aluno_list.view.*
import kotlinx.android.synthetic.main.fragment_nota_list.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [NotaFragment.OnListFragmentInteractionListener] interface.
 */
class NotaFragment() : Fragment(),View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.FAB_add ->{
                val meuBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.addAlunoTitle)

                val v:View = layoutInflater.inflate(R.layout.dialog_add_nota,null,false)
                meuBuilder.setView(v)

                v.valor_edit_text.setFilters(arrayOf<InputFilter>(MinMaxFilter(0, 10)))


                meuBuilder.setCancelable(true)

                meuBuilder.setPositiveButton("add",
                    DialogInterface.OnClickListener { dialog,
                                                      which ->
                        var materia = v.materia_edit_text
                        var valor = v.valor_edit_text

                        val dbHandler = DBHelper(context!!, null)
                        val nota = Nota(aluno,materia.text.toString(),Integer.valueOf(valor.text.toString()))
                        try{

                        dbHandler.addNota(nota)
                            notas.add(nota)
                        Toast.makeText(context!!, materia.toString()+ " foi adicionado ao database", Toast.LENGTH_LONG).show()

                        mRecycler.adapter!!.notifyItemInserted(notas.size-1)
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

    // TODO: Customize parameters
    private var columnCount = 1
    lateinit var mRecycler:RecyclerView
    private lateinit var aluno: Aluno
    private var notas:ArrayList<Nota> = ArrayList<Nota>()

    private var listener: OnListFragmentInteractionListener? = null
    constructor(aluno:Aluno):this(){
        this.aluno=aluno
    }
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
        val view = inflater.inflate(R.layout.fragment_nota_list, container, false)
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


                    notas.add(0,Nota(
                        aluno,
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_MATERIA)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_NOTA))
                    )
                    )
                    while (cursor.moveToNext()) {
                        notas.add(cursor.position,Nota(
                            aluno,
                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_MATERIA)),
                            cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_NOTA))
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
                mRecycler.setAdapter(MyNotaRecyclerViewAdapter(notas, listener))
                mRecycler.adapter!!.notifyDataSetChanged()
            }
        (activity as MainActivity).FAB_add.setOnClickListener(this)
        return view
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
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Nota)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            NotaFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
