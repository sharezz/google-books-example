package com.sharezzorama.example.books.data.tables;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

abstract public class BaseTable {
    /*public long insert(@NonNull SQLiteDatabase db, @NonNull ContentValues values) {
        return db.insert(getTableName(), null, values);
    }*/

    abstract public void createTable(SQLiteDatabase db);

    abstract public String getTableName();
}
