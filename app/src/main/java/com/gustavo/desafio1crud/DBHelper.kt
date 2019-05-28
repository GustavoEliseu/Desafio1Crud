package com.gustavo.desafio1crud

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (context: Context, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val CREATE_TABLE_ALUNOS =
        "CREATE TABLE IF NOT EXISTS $TABLE_ALUNOS ($COLUNA_MATRICULA INTEGER PRIMARY KEY AUTOINCREMENT, $COLUNA_NOME TEXT NOT NULL, $COLUNA_DATA TEXT , UNIQUE ($COLUNA_NOME , $COLUNA_DATA));"
    private val CREATE_TABLE_NOTAS =
        "CREATE TABLE IF NOT EXISTS $TABLE_NOTAS ($COLUNA_MATRICULA INTEGER , $COLUNA_NOTA INTEGER CHECK ($COLUNA_NOTA > 0 AND $COLUNA_NOTA <11), $COLUNA_MATERIA TEXT NOT NULL);"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ALUNOS)
        db.execSQL(CREATE_TABLE_NOTAS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        onCreate(db)
    }

    fun addAluno(aluno: Aluno):Long {
        val values = ContentValues()
        values.put(COLUNA_NOME, aluno.nome)
        values.put(COLUNA_DATA, aluno.data)
        val db = this.writableDatabase
        var myLong: Long
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

    fun getLastInsert():Cursor?{
        val db = this.writableDatabase
        return db.rawQuery("SELECT seq  from sqlite_sequence  where name=\"$TABLE_ALUNOS\"",null)
    }

    //TODO - verificar se está correto e efetivar no clique da actionbar
    fun deleteAluno(matricula:Int){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_ALUNOS WHERE $COLUNA_MATRICULA = "+matricula+";")
    }

    fun addNota(nota: Nota) {
        val values = ContentValues()
        values.put(COLUNA_MATRICULA,nota.aluno.matricula)
        values.put(COLUNA_NOTA, nota.valor)
        values.put(COLUNA_MATERIA, nota.materia)
        val db = this.writableDatabase
        db.insertOrThrow(TABLE_NOTAS, null, values)
        db.close()

    }

    fun getAllAlunos(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_ALUNOS", null)
    }

    fun getAllNotas(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NOTAS", null)
    }
    fun removeDuplicates(){
        val db = this.readableDatabase
        val remove:String ="DELETE FROM $TABLE_ALUNOS WHERE $COLUNA_MATRICULA NOT IN(SELECT MIN($COLUNA_MATRICULA) FROM $TABLE_ALUNOS GROUP BY $COLUNA_NOME)"
        db.execSQL(remove)
    }

    fun getAllNotasAluno(matricula:Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NOTAS WHERE $COLUNA_MATRICULA  = "+matricula, null)
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