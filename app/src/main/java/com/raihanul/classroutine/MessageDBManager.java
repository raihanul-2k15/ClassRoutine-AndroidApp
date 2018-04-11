package com.raihanul.classroutine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MessageDBManager extends SQLiteOpenHelper{

    private static final String DB_NAME = "message_db";
    private static final String TABLE_NAME = "messages";
    private static final String COL_ID = "id";
    private static final String COL_DATETIME = "datetime";
    private static final String COL_MESSAGE = "message";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " integer primary key autoincrement, " + COL_DATETIME + " text not null, " + COL_MESSAGE +" text not null);";

    public MessageDBManager(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        this.onCreate(db);
    }

    public void addMessage(String datetime, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATETIME, datetime);
        values.put(COL_MESSAGE, message);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Pair<String, String>> getMessages(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, COL_ID + " DESC",  String.valueOf(limit));
        List<Pair<String, String>> messages = new ArrayList<Pair<String, String>>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String datetime = c.getString(c.getColumnIndex(COL_DATETIME));
            String message = c.getString(c.getColumnIndex(COL_MESSAGE));
            messages.add(new Pair<String, String>(datetime, message));
            c.moveToNext();
        }
        c.close();
        db.close();
        return messages;
    }

    public void clearMessageTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_NAME);
        db.execSQL(CREATE_TABLE);
        db.close();
    }
}
