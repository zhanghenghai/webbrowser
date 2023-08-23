package com.one.browser.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 18517
 */
public class WebsiteDao {

    private static final String[] WEBSITE_COLUMNS = new String[]{"Url", "Path"};

    private WebsiteDbHelper websiteDbHelper;

    public WebsiteDao(Context context) {
        this.websiteDbHelper = new WebsiteDbHelper(context);
    }


    public void installData(Website website) {
        SQLiteDatabase db = null;
        String url = website.getUrl();
        String path = website.getPath();
        try {
            db = websiteDbHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("URL", url);
            contentValues.put("PATH", path);
            db.replaceOrThrow(WebsiteDbHelper.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
            Log.i("TAG", "insertDate: 插入成功");
        } catch (Exception e) {
            Log.i("TAG", "installData: " + e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    /**
     * 判断是否存在数据
     */
    public boolean isDataExist() {
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = websiteDbHelper.getReadableDatabase();
            cursor = db.query(BookmarkDbHelper.TABLE_NAME, new String[]{"COUNT(Path)"}, null, null, null, null, null);

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
    public Website getOne(String url) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        // 选择条件，例如：column1 = 'value'
        String selection = "Url = ?";
        // 选择条件的参数，与选择条件中的?一一对应
        String[] selectionArgs = {url};
        try {
            db = websiteDbHelper.getReadableDatabase();
            cursor = db.query(WebsiteDbHelper.TABLE_NAME, WEBSITE_COLUMNS, selection, selectionArgs, null, null, null);
            Log.i("TAG", "SQL >>>" + cursor);
            if (cursor.getCount() > 0) {
                Log.i("TAG", "getOne: 查询到数据了 >>>");
                while (cursor.moveToNext()) {
                    return parseWebSize(cursor);
                }
            }
        } catch (Exception e) {
            Log.i("TAG", "getOne: " + e.getMessage());
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
    public List<Website> getAllDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Website> websiteList = null;
        try {
            db = websiteDbHelper.getReadableDatabase();
            cursor = db.query(WebsiteDbHelper.TABLE_NAME, WEBSITE_COLUMNS, null, null, null, null, null, null);
            websiteList = new ArrayList<>(cursor.getCount());
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    websiteList.add(parseWebSize(cursor));
                }
                return websiteList;
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

        return websiteList;
    }

    @SuppressLint("Range")
    private Website parseWebSize(Cursor cursor) {
        Website website = new Website();
        website.setUrl(cursor.getString(cursor.getColumnIndex("Url")));
        website.setPath(cursor.getString(cursor.getColumnIndex("Path")));
        return website;
    }

}
