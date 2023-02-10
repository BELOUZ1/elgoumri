package com.app.elgoumri.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.app.elgoumri.bean.Transaction;

public class DataHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "transactions.db";

    public DataHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String req = "create table tTransactions (idTransaction text primary key, idUser1 text, idUser2 text)";
        sqLiteDatabase.execSQL(req);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tTransactions");
        onCreate(sqLiteDatabase);
    }

    public boolean insertTransaction (Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("idTransaction", transaction.getId());
        contentValues.put("idUser1", transaction.getIdSender());
        contentValues.put("idUser2", transaction.getIdReceiver());
        db.insert("tTransactions", null, contentValues);
        return true;
    }

    public boolean existeTransaction(String idTransaction){
        String[] selectionArgs = {idTransaction};
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM tTransactions WHERE idTransaction = ?", selectionArgs );
        if (res.moveToLast()) {
            return true;
        }
        return false;
    }
}
