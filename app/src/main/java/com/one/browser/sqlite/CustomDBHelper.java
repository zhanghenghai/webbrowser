package com.one.browser.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 18517
 */
public class CustomDBHelper extends SQLiteOpenHelper {
    //数据库版本号
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "custom.db";
    //数据表名称
    static final String TABLE_NAME = "Certificate";

    public CustomDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    /*创建数据表*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + TABLE_NAME + " (Title TEXT primary key, W INTEGER,H INTEGER,WASH_W INTEGER,WASH_H INTEGER)";
        db.execSQL(sql);
    }

    /*修改数据*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }


}
