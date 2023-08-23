package com.one.browser.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author 18517
 */
public class JurisdictionUtil {

    private static final int CAMERA = 100;
    private static final int PHOTO = 200;

    /**
     * 存储权限
     */
    public static boolean photoPermissions(Context context) {
        // 有权限: PackageManager.PERMISSION_GRANTED
        // 无权限: PackageManager.PERMISSION_DENIED
        // WRITE_EXTERNAL_STORAGE 获取外部权限
        // CAMERA 摄像头
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, PHOTO);
            return false;
        }
        return true;
    }

    /**
     * 相册权限
     */
    public static boolean cameraPermissions(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA);
            return false;
        }
        return true;
    }

}
