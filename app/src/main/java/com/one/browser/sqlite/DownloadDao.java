package com.one.browser.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 18517
 */
public class DownloadDao {
    private final String[] DOWNLOAD_COLUMNS = new String[]{"Title", "Path", "Mime", "Size", "Time"};
    private DownloadDBHelper dbHelper;

    private final String TIME = "Time desc";

    public DownloadDao(Context mContext) {
        dbHelper = new DownloadDBHelper(mContext);
    }


    /**
     * 将数据转换为Bookmark类
     */
    @SuppressLint("Range")
    private Download parseBookmark(Cursor cursor) {
        Download bookmark = new Download();
        bookmark.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
        bookmark.setPath(cursor.getString(cursor.getColumnIndex("Path")));
        bookmark.setMime(cursor.getString(cursor.getColumnIndex("Mime")));
        bookmark.setSize(cursor.getString(cursor.getColumnIndex("Size")));
        bookmark.setTime(cursor.getString(cursor.getColumnIndex("Time")));
        return bookmark;
    }

    public boolean isDataExist() {
        int count = 0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DownloadDBHelper.TABLE_NAME, new String[]{"COUNT(Title)"}, null, null, null, null, null);
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


    public void deleteData(String title) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            db.beginTransaction();
            db.delete(DownloadDBHelper.TABLE_NAME, "Title = ?", new String[]{title});
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
     * 插入一条数据
     */
    public long insertDate(Download download) {
        long id = 0;
        SQLiteDatabase db = null;
        String title = download.getTitle();
        String path = download.getPath();
        String mime = download.getMime();
        String size = download.getSize();
        String time = download.getTime();
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Title", title);
            contentValues.put("Path", path);
            contentValues.put("Mime", mime);
            contentValues.put("Size", size);
            contentValues.put("Time", time);
            id = db.replaceOrThrow(DownloadDBHelper.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return id;
    }

    /**
     * 获取所有数据
     */
    public List<Download> getAllDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Download> downloadList = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DownloadDBHelper.TABLE_NAME, DOWNLOAD_COLUMNS, null, null, null, null, TIME, null);
            downloadList = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    downloadList.add(parseBookmark(cursor));
                }
                return downloadList;
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

        return downloadList;
    }
}
