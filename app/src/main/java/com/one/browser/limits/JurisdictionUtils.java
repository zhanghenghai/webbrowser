package com.one.browser.limits;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * @author 18517
 */
public class JurisdictionUtils {

    private static final int CAMERA = 100;
    private static final int PHOTO = 200;
    private static final int BAR = 300;

    /**
     * 相机权限
     */
    public static boolean cameraPermissions(Context context) {
        // 有权限: PackageManager.PERMISSION_GRANTED
        // 无权限: PackageManager.PERMISSION_DENIED

        // WRITE_EXTERNAL_STORAGE 获取外部权限
        // CAMERA 摄像头
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA);
            return false;
        }
        return true;
    }

    /**
     * 相册权限
     */
    public static boolean photoPermissions(Context context) {
        // 检查是否已授予 READ_EXTERNAL_STORAGE 权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果未授予权限，则请求权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PHOTO);
            Log.i("TAG", "photoPermissions: 已开启权限");
            return false;
        }
        return true;
    }

    /**
     * 通知栏权限
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean checkPhotoPermissions(Context context) {
        // 检查是否已授予 READ_EXTERNAL_STORAGE 权限
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // 如果未授予权限，则请求权限
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, BAR);
            return false;
        }
        return true;
    }

}
