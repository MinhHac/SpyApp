package com.example.hac.notebook;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hac on 20/03/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context,"thongtin.sqlite", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public Cursor layData(String sql){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor=db.rawQuery(sql,null);
        return cursor;
    }

    public void xuliData(String sql){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL(sql);
    }


    public void deleteSms(String address,String time){
        this.getWritableDatabase().delete("sms_table","address='"+address+"' AND time='"+time+"'",null);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
