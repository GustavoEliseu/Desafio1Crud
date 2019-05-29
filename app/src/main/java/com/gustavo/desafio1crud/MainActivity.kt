package com.gustavo.desafio1crud

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.transition.Explode
import android.transition.Slide
import android.transition.Transition
import android.view.ActionMode
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.selection.SelectionTracker
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity() : AppCompatActivity(), AlunoFragment.OnListFragmentInteractionListener,NotaFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: Nota) {

    }

    val meuFragAluno: Fragment = AlunoFragment()
    lateinit var meuFragNota: Fragment
    var controle:Boolean = true
    val myFragManager: FragmentManager= supportFragmentManager

    override fun onListFragmentInteraction(item: Aluno?,v:View,selecionados:Int) {
        if(myFragManager.fragments.contains(meuFragAluno)){
            if(item != null){
                if(selecionados==0){
                    controle=true
                }
                else if(selecionados==1){
                    if(controle==true){
                        (myFragManager.fragments[0] as AlunoFragment).mSelectionTracker?.clearSelection()
                meuFragNota = NotaFragment(item)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    meuFragNota.enterTransition = android.transition.Fade()
                    meuFragNota.returnTransition = null
                }
                val myFragTrans=myFragManager.beginTransaction()

                meuFragNota.setRetainInstance(true)

                myFragTrans.replace(R.id.myFrag, meuFragNota, "Nota")

                myFragTrans.commit()
                    }
                }else{
                    controle=false
                }
            }
        }
    }

    override fun onListFragmentInteraction(v: View,selecionados:Int) {
        Toast.makeText(this,"teste",Toast.LENGTH_SHORT).show()
        if (selecionados == 1||selecionados ==0) {
            if (controle == true) {
                controle = false
            } else {
                controle = true
            }
        }
    }

    private lateinit var bancodeDados: SQLiteDatabase

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
        if(myFragManager.findFragmentByTag("Nota")!=null){
            myFragment =  myFragManager.findFragmentByTag("Nota") as NotaFragment
        }
        if (myFragment != null && myFragment.isVisible()) {
            myFragManager.beginTransaction().replace(R.id.myFrag,meuFragAluno,"Aluno").commit()
        }else{
            if ((myFragManager.fragments[0] as AlunoFragment).mSelectionTracker!=null&&(myFragManager.fragments[0] as AlunoFragment).mSelectionTracker!!.hasSelection()) {
                (myFragManager.fragments[0] as AlunoFragment).mSelectionTracker!!.clearSelection();
            } else {
                super.onBackPressed();
            }
        }

    }

}

