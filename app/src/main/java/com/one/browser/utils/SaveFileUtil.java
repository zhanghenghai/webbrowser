package com.one.browser.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    public static void saveQUp(Context context, String path, String name, String type, Uri fileType, byte[] content) {

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


    // Android Q 以下
    public static void saveQNext(Bitmap image, Context context, String fileName, int quality) {
        try {
            String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "艾跳跳";
            //如果不存在，那就建立这个文件夹
            mkdir(storePath);
            File dirFile = new File(storePath, fileName + ".jpg");
            FileOutputStream fos = new FileOutputStream(dirFile.getAbsoluteFile());
            // 1、宽高比例压缩
            // 2、压缩参数
            image.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            Log.i("TAG", "保存文件: >>>  " + dirFile.getPath());
            // 通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + dirFile.getAbsolutePath())));
            Log.i("TAG", "存储大小 >>> " + dirFile.length());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mkdir(String path) {
        String[] dirArray = path.split("/");
        StringBuilder pathTemp = new StringBuilder();
        for (int i = 0; i < dirArray.length; i++) {
            pathTemp.append("/").append(dirArray[i]);
            File file = new File(dirArray[0] + pathTemp);
            if (!file.exists()) {
                file.mkdir();
            }
        }
    }


}
