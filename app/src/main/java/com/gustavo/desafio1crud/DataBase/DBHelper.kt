@file:Suppress("UNUSED_PARAMETER")

package com.gustavo.desafio1crud.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.gustavo.desafio1crud.MyDataClasses.Aluno
import com.gustavo.desafio1crud.MyDataClasses.Nota
import com.gustavo.desafio1crud.R

//TODO - CORRIGIR UPDATE PARA FUNCIONAR CORRETMENTE
class DBHelper (val context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {
    private val CREATE_TABLE_ALUNOS =
        "CREATE TABLE IF NOT EXISTS $TABLE_ALUNOS ($COLUNA_MATRICULA INTEGER PRIMARY KEY AUTOINCREMENT, $COLUNA_NOME TEXT NOT NULL, $COLUNA_DATA TEXT , UNIQUE ($COLUNA_NOME , $COLUNA_DATA));"
    private val CREATE_TABLE_NOTAS =
        "CREATE TABLE IF NOT EXISTS $TABLE_NOTAS ($COLUNA_MATRICULA INTEGER , $COLUNA_NOTA INTEGER CHECK ($COLUNA_NOTA >= 0 AND $COLUNA_NOTA <11), $COLUNA_MATERIA TEXT NOT NULL);"

    //onCreate com auto-inicialização para testes
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ALUNOS)
        db.execSQL(CREATE_TABLE_NOTAS)

        val cursor:Cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_ALUNOS",null)
        cursor.moveToFirst()
        if( cursor.getInt(0)<=0){
            startAlunoNota(db)
        }
        cursor.close()

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        onCreate(db)
    }

    //FUNÇÃO TESTE - para adicionar os alunos e as notas disponibilizadas nos string-arrays no string.xml
    fun startAlunoNota(db: SQLiteDatabase){
        val mArrayNome= context.resources.getStringArray(R.array.alunos_nomes)
        val mArrayData= context.resources.getStringArray(R.array.alunos_datas)
        val mArrayMateria= context.resources.getStringArray(R.array.notas_materia)
        
        for(x in 0 until mArrayNome.size){
            val aluno: Aluno =
                Aluno(mArrayNome[x], mArrayData[x], x)
            //checa se o usuário foi adicionado corretamente e adiciona as respectivas ntoas dele
            if(addAluno(aluno,db)>-1L){
                for (y in 0 until mArrayMateria.size){
                    addNota(
                        Nota(
                            aluno,
                            mArrayMateria[y],
                            (0..10).random().toInt()
                        ),db)
                }
            }
        }
    }

    //FUNÇÃO TESTE- para adicionar alunos do onCreateDatabase
    fun addAluno(aluno: Aluno, db: SQLiteDatabase):Long {
        val values = ContentValues()
        values.put(COLUNA_NOME, aluno.nome)
        values.put(COLUNA_DATA, aluno.data)
        var myLong: Long
        try {
             myLong= db.insertOrThrow(TABLE_ALUNOS, null, values)
            //precaução para caso de algum erro
        }catch(t:Throwable){
            t.printStackTrace()
            myLong=-1
        }
        return myLong
    }

    //Função para se adicionar um aluno ao banco de dados
    fun addAluno(aluno: Aluno):Long {
        val values = ContentValues()
        values.put(COLUNA_NOME, aluno.nome)
        values.put(COLUNA_DATA, aluno.data)
        var myLong: Long
        val db = this.writableDatabase
        try {
            myLong= db.insertOrThrow(TABLE_ALUNOS, null, values)
            //precaução para caso de algum erro
        }catch(t:Throwable){
            t.printStackTrace()
            myLong=-1
        }
        db.close()
        return myLong
    }

    fun updateAluno(nome:String,data:String, matricula:Int):Boolean{
        val db = this.writableDatabase
        val cv:ContentValues = ContentValues()
        cv.put(COLUNA_NOME,nome)
        cv.put(COLUNA_DATA,data)
        var result: Boolean
        try{
               result =db.update(TABLE_ALUNOS,cv, COLUNA_MATRICULA+" = ? ",arrayOf(matricula.toString()))>0
        }catch(e:Exception){
            result = false
        }
        return result
    }

    //Seleciona o ultimo AUTOINCREMENT adicionado na tabela Alunos
    fun getLastInsert():Cursor?{
        val db = this.readableDatabase
        return db.query(TABLE_ALUNOS,  arrayOf(COLUNA_MATRICULA), null, null, null, null, COLUNA_MATRICULA+" DESC");
        //return db.rawQuery("SELECT $COLUNA_MATRICULA FROM $TABLE_ALUNOS ORDER BY DESC LIMIT 1 ",null)
    }

    //FUNÇÃO TESTE, adiciona notas para os alunos gerados automaticamente
    fun addNota(nota: Nota, db: SQLiteDatabase) {
        val values = ContentValues()
        values.put(COLUNA_MATRICULA,nota.aluno.matricula)
        values.put(COLUNA_NOTA, nota.valor)
        values.put(COLUNA_MATERIA, nota.materia)
        db.insertOrThrow(TABLE_NOTAS, null, values)

    }

    //Função para adicionar a nota de um aluno
    fun addNota(nota: Nota) {
        val values = ContentValues()
        values.put(COLUNA_MATRICULA,nota.aluno.matricula)
        values.put(COLUNA_NOTA, nota.valor)
        values.put(COLUNA_MATERIA, nota.materia)
        val db = this.writableDatabase
        db.insertOrThrow(TABLE_NOTAS, null, values)
        db.close()

    }

    fun deleteNota(id:Long, matricula: Int):Boolean{
        val db= this.writableDatabase
        val args:Array<String> = arrayOf(id.toString(),matricula.toString())
        return db.delete(TABLE_NOTAS,"ROWID=? and $COLUNA_MATRICULA =? ",args )>0
    }

    fun deleteAllNotas(matricula:Int){
        val db= this.writableDatabase
        val args:Array<String> = arrayOf(matricula.toString())
        db.delete(TABLE_NOTAS,"$COLUNA_MATRICULA =? ",args )>0
    }

    fun deleteAluno(matricula:Int):Boolean{
        val db= this.writableDatabase
        val args:Array<String> = arrayOf(matricula.toString())
        val alunoDeletado= db.delete(TABLE_ALUNOS,"$COLUNA_MATRICULA =? ",args)>0
        if(alunoDeletado==true){
            deleteAllNotas(matricula)
        }
        return alunoDeletado
    }

    //função que seleciona todos os launos para preencher a lista
    fun getAllAlunos(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_ALUNOS", null)
    }

    //criada para caso seja solicitado
    fun getAllNotas(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT  * FROM $TABLE_NOTAS", null)
    }

    //função para receber todas as notas de um aluno
    fun getAllNotasAluno(matricula:Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT ROWID as rowid,* FROM $TABLE_NOTAS WHERE $COLUNA_MATRICULA  = "+matricula, null)
    }


    companion object {
        private val DATABASE_NAME = "Crud.db"
        private val DATABASE_VERSION = 1
        val COLUNA_MATRICULA ="MATRICULA"
        val COLUNA_NOME= "NOME"
        val COLUNA_DATA= "DATA"
        val COLUNA_MATERIA= "MATERIA"
        val COLUNA_NOTA = "NOTA"
        val TABLE_ALUNOS = "ALUNOS"
        val TABLE_NOTAS = "NOTAS"

    }
}