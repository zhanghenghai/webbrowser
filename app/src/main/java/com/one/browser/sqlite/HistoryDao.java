package com.one.browser.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 18517
 */
public class HistoryDao {
    private final String[] HISTORY_COLUMNS = new String[]{"Icon", "Title", "Url", "Time"};
    private final String TIME = "Time desc";

    private HistoryDbHelper dbHelper;

    public HistoryDao(Context mContext) {
        dbHelper = new HistoryDbHelper(mContext);
    }

    /**
     * 判断表中是否有数据
     */
    public boolean isDataExist() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(HistoryDbHelper.TABLE_NAME, new String[]{"COUNT(Url)"}, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            if (count > 0) {
                return true;
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
     * 查询数据
     */
    public History getOne(String title) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String selection = "Title = ?";
        String[] selectArgs = {title};
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(HistoryDbHelper.TABLE_NAME, HISTORY_COLUMNS, selection, selectArgs, null, null, null);
            if (cursor.getCount() > 0) {
                Log.i("TAG", "历史数据查到了");
                while (cursor.moveToNext()) {
                    return parseHistory(cursor);
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
        return null;

    }

    /**
     * 获取所有数据
     */
    public List<History> getAllDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<History> historyList = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(HistoryDbHelper.TABLE_NAME, HISTORY_COLUMNS, null, null, null, null, TIME, null);
            historyList = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    historyList.add(parseHistory(cursor));
                }
                return historyList;
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

        return historyList;
    }

    /**
     * 将数据转换为History类
     */
    @SuppressLint("Range")
    private History parseHistory(Cursor cursor) {
        History history = new History();
        history.setIcon(cursor.getString(cursor.getColumnIndex("Icon")));
        history.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
        history.setUrl(cursor.getString(cursor.getColumnIndex("Url")));
        history.setTime(cursor.getString(cursor.getColumnIndex("Time")));
        return history;
    }

    /**
     * 插入一条数据
     */
    public void insertDate(History history) {
        SQLiteDatabase db = null;
        String icon = history.getIcon();
        String title = history.getTitle();
        String url = history.getUrl();
        String time = history.getTime();
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Icon", icon);
            contentValues.put("Title", title);
            contentValues.put("Url", url);
            contentValues.put("Time", time);
            db.replaceOrThrow(HistoryDbHelper.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
            Log.i("TAG", "insertDate: 插入成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 删除一条数据
     */
    public void deleteHistory(String url) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(HistoryDbHelper.TABLE_NAME, "Title = ?", new String[]{url});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 更新数据
     *
     * @param path  图片文件
     * @param title 主键
     */
    public void Update(String path, String title) {
        Log.i("TAG", "Update: 获取图标数据 " + path);
        Log.i("TAG", "Update: 获取网址数据 " + title);
        SQLiteDatabase db = null;
        ContentValues values = new ContentValues();
        values.put("Icon", path);
        String whereClause = "Title = ?";
        String[] whereArgs = {title};
        try {
            Log.i("TAG", "Update: 历史图片更新成功 >>>> ");
            db = dbHelper.getReadableDatabase();
            db.update(HistoryDbHelper.TABLE_NAME, values, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
