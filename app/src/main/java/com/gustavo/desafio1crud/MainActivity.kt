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
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity() : AppCompatActivity(), AlunoFragment.OnListFragmentInteractionListener,NotaFragment.OnListFragmentInteractionListener {
    override fun onListFragmentInteraction(item: Nota) {

    }

    val meuFragAluno: Fragment = AlunoFragment()
    lateinit var meuFragNota: Fragment
    var selecionado:Boolean=false;
    var contadorSelected:Int= 0;
    val myFragManager: FragmentManager= supportFragmentManager

    override fun onListFragmentInteraction(item: Aluno?,v:View) {
        if(item != null){
            if(selecionado==false){
            meuFragNota = NotaFragment(item)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                meuFragNota.enterTransition = Slide()
                meuFragNota.returnTransition = null
            }
            val myFragTrans=myFragManager.beginTransaction()

            meuFragNota.setRetainInstance(true)

            myFragTrans.replace(R.id.myFrag, meuFragNota, "Nota")

            myFragTrans.commit()}
            else{
                (v as MaterialCardView).isChecked=!(v as MaterialCardView).isChecked
                if((v as MaterialCardView).isChecked==true){
                    contadorSelected++
                }else {
                    contadorSelected--
                    if(contadorSelected==0) selecionado=false
                }
            }
        }
    }

    override fun onListFragmentInteraction(v: View){

        selecionado = true;
        contadorSelected++
    }

    private lateinit var bancodeDados: SQLiteDatabase

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
        if (myFragment != null && myFragment!!.isVisible()) {
            myFragManager.beginTransaction().replace(R.id.myFrag,meuFragAluno,"Aluno").commit()
        }else{
            super.onBackPressed()
        }

    }

}

