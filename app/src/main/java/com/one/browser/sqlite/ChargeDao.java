package com.one.browser.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 18517
 */
public class ChargeDao {

    /**
     * 字段
     */
    private final String[] COLUMNS = new String[]{"ICON", "TITLE", "MONEY", "TIME", "REMARK"};
    /**
     * 时间条件
     */
    private final String TIME = "TIME desc";
    /**
     * ChargeDbHelper对象
     */
    private ChargeDbHelper dbHelper;

    /**
     * 构造函数
     */
    public ChargeDao(Context mContext) {
        dbHelper = new ChargeDbHelper(mContext);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(ChargeDbHelper.TABLE_NAME, new String[]{"COUNT(TITLE)"}, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 查询所有数据
     */
    @SuppressLint("Range")
    public List<Charge> getAll() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Charge> list = new ArrayList<>();
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(ChargeDbHelper.TABLE_NAME, COLUMNS, null, null, null, null, TIME);
            while (cursor.moveToNext()) {
                Charge charge = new Charge();
                charge.setIcon(cursor.getString(cursor.getColumnIndex("ICON")));
                charge.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));
                charge.setMoney(cursor.getString(cursor.getColumnIndex("MONEY")));
                charge.setTime(cursor.getString(cursor.getColumnIndex("TIME")));
                charge.setRemark(cursor.getString(cursor.getColumnIndex("REMARK")));
                list.add(charge);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

    /**
     * 插入数据
     */
    public void insert(Charge charge) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL("insert into " + ChargeDbHelper.TABLE_NAME + "(ICON,TITLE,MONEY,TIME,REMARK) values(?,?,?,?,?)", new Object[]{charge.getIcon(), charge.getTitle(), charge.getMoney(), charge.getTime(), charge.getRemark()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 删除数据
     */
    public void delete(String title) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL("delete from " + ChargeDbHelper.TABLE_NAME + " where TITLE=?", new Object[]{title});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 更新数据
     */
    public void update(Charge charge) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL("update " + ChargeDbHelper.TABLE_NAME + " set ICON=?,MONEY=?,TIME=?,REMARK=? where TITLE=? and TIME=?" , new Object[]{charge.getIcon(), charge.getMoney(), charge.getTime(), charge.getRemark(), charge.getTitle(), charge.getTime()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}
