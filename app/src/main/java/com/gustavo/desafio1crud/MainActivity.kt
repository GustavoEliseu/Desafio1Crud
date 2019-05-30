package com.gustavo.desafio1crud

import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.ActionMode
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gustavo.desafio1crud.MyDataClasses.Aluno
import com.gustavo.desafio1crud.MyDataClasses.Nota
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule
//TODO -- Corrigir comentários da aplicação
//TODO -- Implementar uma transição mais leve entre os fragments
class MainActivity() : AppCompatActivity(), AlunoFragment.OnListFragmentInteractionListener,
    NotaFragment.OnListFragmentInteractionListener {

    val meuFragAluno: Fragment = AlunoFragment()
    lateinit var meuFragNota: Fragment
    val myFragManager: FragmentManager= supportFragmentManager

    //FragmentAluno Interaction e cliques
    override fun onListFragmentInteraction(item: Aluno?) {
            //Verifica qual aluno foi clicado e se efetua a troca de atividades
            if (item != null) {
                meuFragNota = NotaFragment(item)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    meuFragNota.enterTransition = android.transition.Fade()
                    meuFragNota.returnTransition = null
                }
                val myFragTrans = myFragManager.beginTransaction()
                meuFragNota.setRetainInstance(true)
                myFragTrans.replace(R.id.myFrag, meuFragNota, "Nota")
                myFragTrans.commit()
            }
    }
    //FragmentNota interaction
    override fun onListFragmentInteraction(item: Nota) {

    }

        //implementação actionMode
    override fun onActionModeStarted(mode: ActionMode?) {
        super.onActionModeStarted(mode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart(){
        super.onStart()
        //val myDBHelper:DBHelper= DBHelper(this,null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            meuFragAluno.exitTransition = Explode()
        }
        Timer("SettingUp", false).schedule(500) {
            if(myFragManager.fragments.isEmpty()) myFragManager.beginTransaction().add(R.id.myFrag,meuFragAluno,"Aluno").commit()

        }

    }

    override fun onBackPressed() {
        var myFragment:Fragment? = null
        var controleBackPressed:Boolean = false
        //Caso nota não seja vazio, verifica se hà seleção ou não
        if(myFragManager.findFragmentByTag("Nota")!=null){
            if ((myFragManager.fragments[0] as NotaFragment).selectionTracker.hasSelection()) {
                (myFragManager.fragments[0] as NotaFragment).selectionTracker.clearSelection();
            } else {
            myFragment =  myFragManager.findFragmentByTag("Nota") as NotaFragment
                controleBackPressed= true
            }
        }
        //caso o fragment seja nota sem seleção, voltará para a tela anterior, caso não seja o fragment nota a aplicação será minimizada
        if (myFragment != null && myFragment.isVisible()) {
            myFragManager.beginTransaction().replace(R.id.myFrag,meuFragAluno,"Aluno").commit()
        }else{
            if(controleBackPressed==true) {
                super.onBackPressed();
            }
        }

    }

}

