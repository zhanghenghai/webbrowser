package com.one.browser.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import java.io.OutputStream;

/**
 * @author 18517
 */
public class SaveFileUtil {


    /**
     * @param content  对象
     * @param path     保存路径
     * @param name     文件名称
     * @param type     文件类型
     * @param fileType 文件保存类型
     * @param content  保存内容
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void save(Context context, String path, String name, String type, Uri fileType, byte[] content) {

        ContentValues values = new ContentValues();
        // 存储位置
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, path);
        // 文件名称
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        // 文件类型
        values.put(MediaStore.MediaColumns.MIME_TYPE, type);
        ContentResolver localContentResolver = context.getContentResolver();
        Uri uri = localContentResolver.insert(fileType, values);
        // 输出流
        try (OutputStream outputStream = localContentResolver.openOutputStream(uri)) {
            outputStream.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
