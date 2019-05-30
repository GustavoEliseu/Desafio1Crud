@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package com.gustavo.desafio1crud



import android.app.Activity
import android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
import android.app.DatePickerDialog
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gustavo.desafio1crud.Adapters.MyAlunoRecyclerViewAdapter
import com.gustavo.desafio1crud.DataBase.DBHelper
import com.gustavo.desafio1crud.MyDataClasses.Aluno
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_add_aluno.view.*
import kotlinx.android.synthetic.main.fragment_aluno_list.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [AlunoFragment.OnListFragmentInteractionListener] interface.
 */
//TODO - Implementar uma Busca complexa por nome de aluno (com animações da lista)


class AlunoFragment : androidx.fragment.app.Fragment(),View.OnClickListener {

    private var listener: OnListFragmentInteractionListener? = null
    private var columnCount = 1

    lateinit var mAdapter: MyAlunoRecyclerViewAdapter
    lateinit var mRecycler:RecyclerView
    lateinit var alunos:ArrayList<Aluno>

    var actionMode:ActionMode? = null
    var toolbar: ActionBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }


    override fun onPause() {
        super.onPause()
        actionMode?.finish()
        actionMode=null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aluno_list, container, false)
        mRecycler =view.mRVList
        alunos = ArrayList<Aluno>()
        toolbar= (activity as AppCompatActivity).supportActionBar
        //Inicio configuração recycler
            with(mRecycler) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                val dbHandler = DBHelper(activity!!, null)
                val cursor = dbHandler.getAllAlunos()
                //Inicio tentativa ler Banco de Dados
                try{

                    cursor!!.moveToFirst()


                    alunos.add(0, Aluno(
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_NOME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_DATA)),
                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_MATRICULA))
                    )
                    )

                    while (cursor.moveToNext()) {
                        alunos.add(cursor.position, Aluno(
                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_NOME)),
                            cursor.getString(cursor.getColumnIndex(DBHelper.COLUNA_DATA)),
                            cursor.getInt(cursor.getColumnIndex(DBHelper.COLUNA_MATRICULA))
                        )
                        )
                    }
                    cursor.close()

                }catch(t:Throwable){
                    t.printStackTrace()

                    Toast.makeText(context,"Banco de dados Vazio",Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    e.printStackTrace()
                }finally {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }// Fim Tentativa ler Banco de Dados
                dbHandler.close()
                mAdapter = MyAlunoRecyclerViewAdapter(alunos, listener, context)
                mRecycler.setAdapter(mAdapter)
                mAdapter.notifyDataSetChanged()
            } //fim da configuração do Recycler


        (activity as MainActivity).FAB_add.setOnClickListener(this)

        return view
        }

    fun updateLabel(v:View,myCalendar:Calendar) {
        val myFormat:String = "dd/MM/yyyy";//In which you need put here
        val sdf:SimpleDateFormat = SimpleDateFormat(myFormat, Locale.ENGLISH)

        v.date_edit_text.setText(sdf.format(myCalendar.getTime()))
    }


    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        if (actionMode != null) {
            actionMode!!.finish()
            actionMode = null
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.FAB_add ->{

                val myCalendar:Calendar = Calendar.getInstance();

                val meuBuilder:MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context)

                val myDialogAddView:View = layoutInflater.inflate(R.layout.dialog_add_aluno,null,false)
                meuBuilder.setView(myDialogAddView)
                //se impede a digitação no date_edit
                myDialogAddView.date_edit_text.setKeyListener(null);

                //Inicia o DatePicker com a data atual
                val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, monthOfYear)
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateLabel(myDialogAddView,myCalendar)
                }

              //Se esconde o teclado e inicia o DatePicker
                myDialogAddView.date_edit_text.setOnClickListener(object: View.OnClickListener{
                    override fun onClick(v: View) {

                        try{
                            hideKeyboard(activity!!,v)
                        }catch(t:Throwable){
                            t.printStackTrace()
                        }catch(e:Exception){
                            e.printStackTrace()}

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val datePickerDialog =DatePickerDialog(context!!, R.style.TimePickerTheme,date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH))
                            datePickerDialog.create()
                            //modifica a cor dos botões do dialog
                            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(resources.getColor(R.color.colorAccent,null))
                            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(resources.getColor(R.color.colorAccent,null))
                            datePickerDialog.show()
                        }

                    }

                })


                //caso edit_text receba focus, se executa o onClick para criar o datepicker
                myDialogAddView.date_edit_text.setOnFocusChangeListener(View.OnFocusChangeListener { innerView, hasFocus ->
                    if(innerView.isFocused==true){
                       myDialogAddView.date_edit_text.performClick()
                    }
                })


                meuBuilder.setCancelable(true)

                meuBuilder.setPositiveButton("add",null

                )
                val meuAlert = meuBuilder.create()

                meuAlert.show()
                //caso os dados sejam válidos, se adiciona o aluno no banco de dados e a lista do recycler
                meuAlert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
                    var nome = myDialogAddView.nome_edit_text
                    var data = myDialogAddView.date_edit_text
                    if(!nome.text!!.isEmpty()||!data.text!!.isEmpty()) {
                        val dbHandler = DBHelper(context!!, null)
                        val aluno =
                            Aluno(nome.text.toString(), data.text.toString())
                        //Tenta inserir o aluno novo ao database e informa em caso de sucesso ou erro
                        try {
                            val myLong: Long = dbHandler.addAluno(aluno)
                            if (myLong != -1L) {
                                val cursor: Cursor? = dbHandler.getLastInsert()
                                cursor!!.moveToFirst()
                                val mat:Long =cursor.getLong(cursor.getColumnIndex(DBHelper.COLUNA_MATRICULA))
                                aluno.matricula = mat.toInt()
                                alunos.add(aluno)
                                //informa que o usuário foi adicionado corretamente
                                Toast.makeText(
                                    context!!,
                                    nome.text.toString() + " foi adicionado ao database com a matricula: "+ aluno.matricula.toString(),
                                    Toast.LENGTH_LONG
                                ).show()
                                //atualiza o recyclerview
                                mRecycler.adapter!!.notifyItemInserted(alunos.size - 1)

                            } else {
                                mRecycler.adapter!!.notifyItemInserted(alunos.size)
                                Toast.makeText(context!!, "Aluno já existente!", Toast.LENGTH_SHORT).show()
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
        }
    }


    //função para esconder o teclado na hora de selecionar a data
    fun hideKeyboard( context:Context,v: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.getRootView()!!.getWindowToken(), 0)
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
        fun onListFragmentInteraction(item: Aluno?)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AlunoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}


