package com.sy.hzgps.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sy.hzgps.tool.lh.L;

/**
 * Created by jiayang on 2015/9/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OBDRecords.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "HistoryRecords";
    public static final String TIMESTAMP = "timeStamp";
    public static final String SERIALNO = "serialNo";
    public static final String DATA = "data";
    //private static final String SQL = "create table if not exists " + TABLE_NAME +
    //        " (" + TASKTIME + " timestamp not null default (datetime('now', '+5 minute', 'localtime')), " + CMDID + " integer, " + SERIALNO + " integer, " + DATA + " blob)";

    private static final String SQL = "create table if not exists " + TABLE_NAME +
              " (" + TIMESTAMP + " timestamp not null default (strftime('%s', datetime('now'))), " + SERIALNO + " integer, " + DATA + " text)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL);
        L.d("DBHelper create success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
