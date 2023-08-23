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
public class ScriptDao {

    private final String[] SCRIPT_COLUMNS = new String[]{"Title", "Script", "State", "Time"};

    private ScriptDBHelper dbScript;

    private final String TIME = "Time desc";

    private Context context;

    public ScriptDao(Context context) {
        dbScript = new ScriptDBHelper(context);
    }

    public void setContext(Context context) {
        this.context = context;
    }


    //=====判断表中是否有数据=====

    public boolean isDataExist() {
        int count = 0;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbScript.getReadableDatabase();
            cursor = db.query(ScriptDBHelper.TABLE_NAME, new String[]{"COUNT(Script)"}, null, null, null, null, TIME, null);
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

    //=====获取所有数据=====

    public List<Script> getAllDate() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Script> scriptList = null;
        try {
            db = dbScript.getReadableDatabase();
            cursor = db.query(ScriptDBHelper.TABLE_NAME, SCRIPT_COLUMNS, null, null, null, null, TIME);
            scriptList = new ArrayList<>(cursor.getCount());
            // 如果条数大于0
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    scriptList.add(parseScript(cursor));
                }
                return scriptList;
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
        return scriptList;
    }

    // ================ 运行脚本 ======================================

    public List<Script> RunDate() {
        try (SQLiteDatabase db = dbScript.getReadableDatabase(); Cursor cursor = db.query(ScriptDBHelper.TABLE_NAME, SCRIPT_COLUMNS, "State = ?", new String[]{"1"}, null, null, null)) {
            if (cursor.getCount() > 0) {
                ArrayList<Script> scriptList = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    scriptList.add(parseScript(cursor));
                }
                return scriptList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //=====将数据转换为Script类=====

    @SuppressLint("Range")
    private Script parseScript(Cursor cursor) {
        Script script = new Script();
        script.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
        script.setScript(cursor.getString(cursor.getColumnIndex("Script")));
        script.setState(cursor.getString(cursor.getColumnIndex("State")));
        script.setTime(cursor.getString(cursor.getColumnIndex("Time")));
        return script;
    }


    //=====插入一条数据=====

    public void insertDate(Script scripts) {
        SQLiteDatabase db = null;

        String title = scripts.getTitle();
        String script = scripts.getScript();
        String state = scripts.getState();
        String time = scripts.getTime();
        try {
            db = dbScript.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("Title", title);
            contentValues.put("Script", script);
            contentValues.put("State", state);
            contentValues.put("Time", time);
            db.replaceOrThrow(ScriptDBHelper.TABLE_NAME, null, contentValues);
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

    //=====查询一条数据======

    public boolean selectScript(Script script) {
        String count = null;
        String title = script.getTitle();
        System.out.println("数据层数据：" + title);
        try (
                SQLiteDatabase db = dbScript.getReadableDatabase();
                Cursor cursor = db.query(ScriptDBHelper.TABLE_NAME, SCRIPT_COLUMNS, "Title like ?", new String[]{title}, null, null, null)) {
            if (cursor.moveToFirst()) {
                count = cursor.getString(1);
            }
            if (count == null) {
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //=====删除一条数据=====

    public void deleteScript(Script scripts) {
        System.out.println("要删除了！！！");
        SQLiteDatabase db = null;
        String title = scripts.getTitle();
        try {
            db = dbScript.getWritableDatabase();
            db.beginTransaction();
            db.delete(ScriptDBHelper.TABLE_NAME, "Title = ?", new String[]{title});
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


    //=====修改数据=====

    public void updateState(Script oScript, Script nScript) {
        SQLiteDatabase db = null;
        String title = oScript.getTitle();
        String state = nScript.getState();
        try {
            db = dbScript.getReadableDatabase();
            db.beginTransaction();
            ContentValues cvState = new ContentValues();
            cvState.put("State", state);
            db.update(ScriptDBHelper.TABLE_NAME, cvState, "Title = ?", new String[]{title});
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

    public void updateScript(Script script, Script newscript) {
        SQLiteDatabase db = null;

        Log.i("TAG", "脚本状态 >>> "+newscript.getState());

        String oldTitle = script.getTitle();
        String oldScript = script.getScript();
        String newTitle = newscript.getTitle();
        String newScript = newscript.getScript();
        String newState = newscript.getState();
        try {
            db = dbScript.getWritableDatabase();
            db.beginTransaction();

            ContentValues cvTitle = new ContentValues();
            cvTitle.put("Title", newTitle);
            ContentValues cvScript = new ContentValues();
            cvScript.put("Script", newScript);
            ContentValues cvState = new ContentValues();
            cvState.put("State",newState);
            db.update(ScriptDBHelper.TABLE_NAME, cvTitle, "Title = ?", new String[]{oldTitle});
            db.update(ScriptDBHelper.TABLE_NAME, cvScript, "Script = ?", new String[]{oldScript});
            db.update(ScriptDBHelper.TABLE_NAME, cvState, "Title = ?", new String[]{newTitle});
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
