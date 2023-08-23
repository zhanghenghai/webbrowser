package com.one.browser.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import com.one.browser.entity.Resource;

import java.util.ArrayList;
import java.util.List;

public class CommonDao {

    private final String[] COLUMNS = new String[]{"Title", "W", "H","WASH_W","WASH_H"};
    private CommonDBHelper dbHelper;


    public CommonDao(Context context) {
        dbHelper = new CommonDBHelper(context);
    }



    // 数据插入
    public void inserData(Resource resource) {
        SQLiteDatabase db = null;
        String title = resource.getTitle();
        int w = resource.getW();
        int h = resource.getH();
        int wash_w = resource.getWash_w();
        int wash_h = resource.getWash_y();
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Title", title);
            contentValues.put("W", w);
            contentValues.put("H", h);
            contentValues.put("WASH_W",wash_w);
            contentValues.put("WASH_H",wash_h);
            db.replaceOrThrow(CommonDBHelper.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /*获取表中数据*/
    public List<Resource> getAll() {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase(); Cursor cursor = db.query(CommonDBHelper.TABLE_NAME, COLUMNS, null, null, null, null, null)) {
            if (cursor.getCount() > 0) {
                ArrayList<Resource> resources = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    String Title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
                    int W = cursor.getInt(cursor.getColumnIndexOrThrow("W"));
                    int H = cursor.getInt(cursor.getColumnIndexOrThrow("H"));
                    int WASH_W = cursor.getInt(cursor.getColumnIndexOrThrow("WASH_H"));
                    int WASH_H = cursor.getInt(cursor.getColumnIndexOrThrow("WASH_H"));
                    resources.add(new Resource(Title,W,H,WASH_W,WASH_H));
                }
                return resources;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    /*删除表中中数据*/
    public void deleteData(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("delete from Certificate");
    }

}
