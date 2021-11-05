package com.smunyeke.jetpacksqlite.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME="sqlite.db"
private const val TABLE_NAME="tbItem"
private const val ID="id"
private const val ITEM_NAME="name"
private const val CREATE_TABLE= "CREATE TABLE $TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT, $ITEM_NAME TEXT)"
class DatabaseHelper(var context : Context): SQLiteOpenHelper(context,DATABASE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun  insertData(item: TodoItem): Boolean {
        val db = this.writableDatabase
         val cv = ContentValues()
         cv.put(ITEM_NAME,item.itemName)
        return db.insert(TABLE_NAME,null,cv) != (-1).toLong()
    }
    fun getData(): MutableList<TodoItem> {
        val todoItems = mutableListOf<TodoItem>()
        val db = this.readableDatabase
        val query= "SELECT * FROM $TABLE_NAME"
        val result= db.rawQuery(query,null)
        if (result.moveToFirst()){
            do {
                var item=TodoItem()
                item.itemId = result.getLong(0)
                item.itemName = result.getString(1)
                todoItems.add(item)
            }while (result.moveToNext())
        }
        return todoItems
    }
    fun deleteData(item: TodoItem){
        val db = this.writableDatabase
        db.delete(TABLE_NAME,"$ID=?", arrayOf(item.itemId.toString()))
    }
    fun updateData(item: TodoItem){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(ITEM_NAME,item.itemName)
        db.update(TABLE_NAME,cv,"$ID=?", arrayOf(item.itemId.toString()))
    }
}