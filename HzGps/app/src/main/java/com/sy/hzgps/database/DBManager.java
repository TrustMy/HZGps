package com.sy.hzgps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sy.hzgps.tool.lh.L;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jiayang on 2015/9/15.
 */
public class DBManager {

    private static Logger logger = LoggerFactory.getLogger(DBManager.class);

    private DBHelper helper;
    private SQLiteDatabase db;

    public class Record {

        public short serialNo;
        public byte[] data;


        Record(short serialNo, byte[] data) {
            this.serialNo = serialNo;
            this.data = data;
        }
    }

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }

    /**
     * 打开数据库
     */
    public void openDB() {
        db = helper.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        db.close();
    }

    /**
     * 添加一行记录
     */
    public void add(long timeStamp, short serialNo, byte[] data) {

        String table = helper.TABLE_NAME;
        String nullColumnHack = null;

        ContentValues values = new ContentValues();

        values.put(helper.TIMESTAMP, timeStamp);

        values.put(helper.SERIALNO, serialNo);
        values.put(helper.DATA, data);

        db.insert(table, nullColumnHack, values);

        L.d("DBManager add  time:"+timeStamp+"|data:"+data.toString());

    }

    /**
     * 通过ID删除一行记录
     */
    public void delete(short serialNo) {
        db.execSQL("delete from " + helper.TABLE_NAME + " where serialNo = " + String.valueOf(serialNo));
    }

    /**
     * 读取一行记录
     */
    public Record getOneTask() {

        String table = helper.TABLE_NAME;
        String[] columns = new String[] { helper.SERIALNO, helper.DATA };
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = helper.TIMESTAMP + " desc";
        String limit = "1";

        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

        if ( c.getCount() == 0 ) {
            c.close();
            return null;
        }

        c.moveToFirst();


        short serialNo = c.getShort(0);
        byte[] data = c.getBlob(1);

        Record result = new Record(serialNo, data);

        c.close();

        return result;
    }


    /**
     * 清空记录
     */
    public void clear(String dbName) {
        db.execSQL("delete * from " + dbName);
    }






}
