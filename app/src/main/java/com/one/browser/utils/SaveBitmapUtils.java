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
 * Bitmap 帮助类之一
 *
 * @author 18517
 */
public class SaveBitmapUtils {
    /**
     * 保存图片 Android Q 以上
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void saveQUp(Bitmap image, Context context, String fileName) {
        // 文件夹路径
        String imageSaveFilePath = Environment.DIRECTORY_DCIM + File.separator + "一小只";
        Log.i("TAG", "文件夹目录 >>> " + imageSaveFilePath);
        mkdir(imageSaveFilePath);
        // 文件名字
        Log.d("TAG", "文件名字 >>> " + fileName);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.TITLE, fileName);
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, fileName);
        //该媒体项在存储设备中的相对路径，该媒体项将在其中保留
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, imageSaveFilePath);
        Uri uri = null;
        OutputStream outputStream = null;
        ContentResolver localContentResolver = context.getContentResolver();
        try {
            uri = localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            outputStream = localContentResolver.openOutputStream(uri);
            // Bitmap图片保存
            // 1、宽高比例压缩
            // 2、压缩参数
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (uri != null) {
                localContentResolver.delete(uri, null, null);
            }
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  Android Q 以下
     * 这种方式不用创建文件，适用所有的Android设备 原因是数据都存在于data/包里面
     * */
    public void saveQNext(Bitmap image, Context context, String fileName) {
        // 判断保存权限

        try {
            String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "艾跳跳";
            //如果不存在，那就建立这个文件夹
            mkdir(storePath);
            File dirFile = new File(storePath, fileName + ".jpg");
            FileOutputStream fos = new FileOutputStream(dirFile.getAbsoluteFile());
            // 1、宽高比例压缩
            // 2、压缩参数
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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

    public void mkdir(String path) {
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