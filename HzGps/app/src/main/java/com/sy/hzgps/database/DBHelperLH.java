package com.sy.hzgps.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sy.hzgps.tool.lh.L;

/**
 * Created by Trust on 2017/4/8.
 */

public class DBHelperLH extends SQLiteOpenHelper {

    private static final String CREATE_IMG = "create table Img(" +
            "id integer primary key autoincrement," +
            "name text," +
            "img BLOB," +
            "time text)";

    private static final String CREATE_HISTORY = "create table history(" +
            "id integer primary key autoincrement," +
            "termId Integer," +
            "orderNumble text," +
            "startName text," +
            "endName text," +
            "qR BLOB," +
            "startTime text," +
            "endTime text," +
            "generatePictureTime text," +
            "status Integer,"+
            "orderPhotoBit text,"+
            "time text)";


    private Context mContext;

    public DBHelperLH(Context context) {
        super(context, "Img.db", null, 1);
        mContext = context;

    }


    public DBHelperLH(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMG);
        db.execSQL(CREATE_HISTORY);
        L.d("dataBase create susscess!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
