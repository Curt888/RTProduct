package com.example.rtproduct

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

val DATABASE_NAME = "MY DATABASE"
val TABLE_NAME = "Users"
val COL_TRUCKCOMP = "truckComp"
val COL_TRUCKTYPE = "truckType"
val COL_TRUCKNO = "TruckNo"
val COL_MQTTHOST = "MqttHost"
val COL_DIALOGPASSWORD = "DialogPassword"
val COL_ID = "id"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTERGER PRIMARY KEY, " + COL_TRUCKCOMP + " TEXT," + COL_TRUCKTYPE + " TEXT," + COL_TRUCKNO + " TEXT," + COL_MQTTHOST + " TEXT," + COL_DIALOGPASSWORD + " TEXT" + ")"
        db?.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }
    fun insertData(user: User) {
        val database = this.writableDatabase
        var contentValues = ContentValues()
        contentValues.put(COL_TRUCKCOMP, user.truckComp)
        contentValues.put(COL_TRUCKTYPE, user.truckType)
        contentValues.put(COL_TRUCKNO, user.TruckNo)
        contentValues.put(COL_MQTTHOST, user.MqttHost)
        contentValues.put(COL_DIALOGPASSWORD, user.DialogPassword)
        val result = database.insert(TABLE_NAME, null, contentValues)
        if (result == (-1).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun readData(): MutableList<User> {
        val list: MutableList<User> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLE_NAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val user = User(truckComp = toString(), truckType = toString(), TruckNo = toString(), MqttHost = toString(), DialogPassword = toString())
                user.truckComp = result.getString(result.getColumnIndex(COL_TRUCKCOMP))
                user.truckType = result.getString(result.getColumnIndex(COL_TRUCKTYPE))
                user.TruckNo = result.getString(result.getColumnIndex(COL_TRUCKNO))
                user.MqttHost = result.getString(result.getColumnIndex(COL_MQTTHOST))
                user.DialogPassword = result.getString(result.getColumnIndex(COL_DIALOGPASSWORD))
                list.add(user)
            }
            while (result.moveToNext())
        }
        return list
    }
}
