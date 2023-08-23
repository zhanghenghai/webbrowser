package com.one.browser.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 18517
 */
public class BitMapUtil {

    public static String saveBitmap(Bitmap bitmap, String filePath, String name) {
        File file = new File(filePath);
        Log.i("TAG", "获取形参地址 >>>>> : " + file.getPath());
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            File outputFile = new File(file, name);
            fos = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Log.i("TAG", "保存文件成功: ");
            // 图片保存成功
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "保存文件失败: ");
            // 图片保存失败
            return null;
        }
    }


}
