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
public class BookmarkDao {
    private final String[] BOOKMARK_COLUMNS = new String[]{"Icon", "Title", "Url", "Time", "State"};

    private BookmarkDbHelper dbHelper;

    private final String TIME = "Time desc";

    public BookmarkDao(Context mContext) {
        dbHelper = new BookmarkDbHelper(mContext);
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
            cursor = db.query(BookmarkDbHelper.TABLE_NAME, new String[]{"COUNT(Url)"}, null, null, null, null, null);

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
     * 获取所有数据
     */
    public List<Bookmark> getAllDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Bookmark> bookmarkList = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(BookmarkDbHelper.TABLE_NAME, BOOKMARK_COLUMNS, null, null, null, null, TIME, null);
            bookmarkList = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    bookmarkList.add(parseBookmark(cursor));
                }
                return bookmarkList;
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

        return bookmarkList;
    }




    /**
     * 查询目录
     */
    public List<Bookmark> superior(String superior) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(BookmarkDbHelper.TABLE_NAME, BOOKMARK_COLUMNS, "State = ?", new String[]{superior}, null, null, TIME, null);

            if (cursor.getCount() > 0) {
                List<Bookmark> bookmarkList = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    bookmarkList.add(parseBookmark(cursor));
                }
                return bookmarkList;
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
     * 将数据转换为Bookmark类
     */
    @SuppressLint("Range")
    private Bookmark parseBookmark(Cursor cursor) {
        Bookmark bookmark = new Bookmark();
        bookmark.setIcon(cursor.getString(cursor.getColumnIndex("Icon")));
        bookmark.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
        bookmark.setUrl(cursor.getString(cursor.getColumnIndex("Url")));
        bookmark.setTime(cursor.getString(cursor.getColumnIndex("Time")));
        return bookmark;
    }

    /**
     * 插入一条数据
     */
    public long insertDate(Bookmark bookmark) {
        long id = 0;
        SQLiteDatabase db = null;
        String icon = bookmark.getIcon();
        String title = bookmark.getTitle();
        String url = bookmark.getUrl();
        String state = bookmark.getState();
        String time = bookmark.getTime();
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put("Icon", icon);
            contentValues.put("Time", time);
            contentValues.put("Url", url);
            contentValues.put("Title", title);
            contentValues.put("State", state);
            id = db.replaceOrThrow(BookmarkDbHelper.TABLE_NAME, null, contentValues);
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
     * 删除一条数据
     */
    public void deleteBookmark(Bookmark bookmark) {
        SQLiteDatabase db = null;
        String title = bookmark.getTitle();
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(BookmarkDbHelper.TABLE_NAME, "Title = ?", new String[]{title});
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
     * 修改数据
     */
    public void updateBookmark(Bookmark oldBookmark, Bookmark newBookmark) {
        SQLiteDatabase db = null;

        String oldTitle = oldBookmark.getTitle();
        String oldUrl = oldBookmark.getUrl();
        String newTitle = newBookmark.getTitle();
        String newUrl = newBookmark.getUrl();
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues cvTitle = new ContentValues();
            cvTitle.put("Title", newTitle);
            ContentValues cvUrl = new ContentValues();
            cvUrl.put("Url", newUrl);
            db.update(BookmarkDbHelper.TABLE_NAME, cvTitle, "Title = ?", new String[]{oldTitle});
            db.update(BookmarkDbHelper.TABLE_NAME, cvUrl, "Url = ?", new String[]{oldUrl});
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
}
