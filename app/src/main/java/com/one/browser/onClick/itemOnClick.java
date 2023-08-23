package com.one.browser.onClick;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;


import com.one.browser.R;
import com.one.browser.utils.FileUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author 18517
 */
public class itemOnClick {

    public static Context context;
    public static String edit;
    public static String shellcode;
    public static String savedFile;
    public static AlertDialog loadDialog;
    public static HashMap<String, Object> map = new HashMap<>();
    public static Vibrator vibrator;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private static int time = 0;
    private static Configuration mconfig = new Configuration();

    private itemOnClick() {
    }





    public static void setRipple(Context context, View view, int bgColor, int rippleColor, int left_top, int right_top, int left_bottom, int right_bottom) {
        GradientDrawable GD = new GradientDrawable();
        GD.setColor(bgColor);
        GD.setCornerRadii(new float[]{dp2px(context, left_top), dp2px(context, left_top), dp2px(context, right_top), dp2px(context, right_top), dp2px(context, right_bottom), dp2px(context, right_bottom), dp2px(context, left_bottom), dp2px(context, left_bottom)});
        RippleDrawable RE = new RippleDrawable(new ColorStateList(new int[][]{new int[]{}}, new int[]{rippleColor}), GD, null);
        view.setBackground(RE);
    }

    public static void deleteNullFile(Context context, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        File[] fileArr = file.listFiles();

        if (fileArr != null) {
            for (File subFile : fileArr) {
                if (subFile.isDirectory() && subFile.list().length == 0) {
                    time++;
                    subFile.delete();
                } else if (subFile.isDirectory() && subFile.list().length != 0){
                    deleteNullFile(context, subFile.getAbsolutePath());
                }
                //if (subFile.isFile()) {
                    //subFile.delete();
                //}
            }
        }
        //file.delete();
    }

    //保存图片
    public static String SaveImage(Context context, Bitmap bitmap, String path, String name) {
        if (bitmap == null) {
            return null;
        }
        final String time = new SimpleDateFormat("HH-mm-ss").format(new Date());
        if (!FileUtil.isExistFile(FileUtil.getExternalStorageDir().concat(path))) {
            FileUtil.makeDir(FileUtil.getExternalStorageDir().concat(path));
        }
        File appDir = new File(FileUtil.getExternalStorageDir().concat(path));
        //File pathfile = Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES);
        File file = new File(appDir, name);
        java.io.FileOutputStream fos = null;
        try {
            fos = new java.io.FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //加载弹窗
    public static void LoadingDialog(Context context) {
        try {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch(Exception e) {
        }
        loadDialog = new AlertDialog.Builder(context)
                .create();
        loadDialog.show();
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        WindowManager.LayoutParams layoutParams = loadDialog.getWindow().getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 5 * 4;
        layoutParams.height = context.getResources().getDisplayMetrics().widthPixels / 5 * 4;
        loadDialog.getWindow().setAttributes(layoutParams);
    }

    //复制弹窗
    public static void CopyDialog(Context context, String little, String content) {
        final AlertDialog mDialog = new AlertDialog.Builder(context, R.style.AppTheme)
                .setPositiveButton("复制", null)
                .setNegativeButton("取消", null)
                .create();
        mDialog.setTitle(little);
        mDialog.setMessage(content);
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        ((ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("clipboard", content));
//                        Alerter.create((Activity) context)
//                                .setTitle("复制成功")
//                                .setText("已将内容复制到剪切板")
//                                .setBackgroundColorInt(0xFF4CAF50)
//                                .show();
                    }
                });
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
            }
        });
        mDialog.show();
        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels / 5 * 4;
        mDialog.getWindow().setAttributes(layoutParams);
    }

    //截取
    public static String JieQu(Context context, String str1, String str2, String str3) {
        if (!str1.contains(str2) || !str1.contains(str3)) {
            return "";
        }
        String substring = str1.substring(str1.indexOf(str2) + str2.length());
        return substring.substring(0, substring.indexOf(str3));
    }

    //手机壁纸
    public static Bitmap getWallpaper_1(Context context) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        }
        ParcelFileDescriptor mParcelFileDescriptor = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mParcelFileDescriptor = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM);
        }
        FileDescriptor fileDescriptor = mParcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        try {
            mParcelFileDescriptor.close();
        } catch(Exception e) {
        }
        return image;
    }

    //锁屏壁纸
    public static Bitmap getWallpaper_2(Context context) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        }
        ParcelFileDescriptor mParcelFileDescriptor = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mParcelFileDescriptor = wallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK);
        }
        FileDescriptor fileDescriptor = mParcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        try {
            mParcelFileDescriptor.close();
        } catch(Exception e) {
        }
        return image;
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static String getPhoneInfo(Context context){
        StringBuffer sb = new StringBuffer();
        sb.append("主板： "+ Build.BOARD + "\n\n");
        sb.append("系统启动程序版本号： "+ Build.BOOTLOADER + "\n\n");
        sb.append("系统定制商： "+ Build.BRAND + "\n\n");

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            sb.append("cpu指令集：" + Build.CPU_ABI + "\n\n");
            sb.append("cpu指令集2:" + Build.CPU_ABI2 + "\n\n");
        }else {

            if(Build.SUPPORTED_32_BIT_ABIS.length != 0){
                sb.append("cpu指令集:");
                sb.append(" [ 32位 ] ");
                sb.append("[ ");
                for (int i = 0; i < Build.SUPPORTED_32_BIT_ABIS.length; i++) {

                    if (i == Build.SUPPORTED_32_BIT_ABIS.length - 1) {
                        sb.append(Build.SUPPORTED_32_BIT_ABIS[i]);
                    } else {
                        sb.append(Build.SUPPORTED_32_BIT_ABIS[i] + " , ");
                    }

                }
                sb.append(" ]");
                sb.append("\n\n");
            }

            if(Build.SUPPORTED_64_BIT_ABIS.length != 0){
                sb.append("cpu指令集:");
                sb.append(" [ 64位 ] ");
                sb.append("[ ");
                for(int i=0;i<Build.SUPPORTED_64_BIT_ABIS.length;i++){

                    if(i == Build.SUPPORTED_64_BIT_ABIS.length - 1){
                        sb.append(Build.SUPPORTED_64_BIT_ABIS[i]);
                    }else{
                        sb.append(Build.SUPPORTED_64_BIT_ABIS[i] + " , ");
                    }

                }
                sb.append(" ]");
                sb.append("\n\n");
            }

        }
        sb.append("设置参数： "+ Build.DEVICE + "\n\n");
        sb.append("显示屏参数： "+ Build.DISPLAY + "\n\n");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            sb.append("无线电固件版本： "+ Build.getRadioVersion() + "\n\n");
        }
        sb.append("硬件识别码： "+ Build.FINGERPRINT + "\n\n");
        sb.append("硬件名称： "+ Build.HARDWARE + "\n\n");
        sb.append("HOST: "+ Build.HOST + "\n\n");
        sb.append("修订版本列表： "+ Build.ID + "\n\n");
        sb.append("硬件制造商： "+ Build.MANUFACTURER + "\n\n");
        sb.append("版本： "+ Build.MODEL + "\n\n");
        sb.append("硬件序列号：" + Build.SERIAL + "\n\n");
        sb.append("手机制造商：" + Build.PRODUCT + "\n\n");
        sb.append("描述Build的标签：" + Build.TAGS + "\n\n");
        sb.append("TIME: "+ Build.TIME + "\n\n");
        sb.append("builder类型：" + Build.TYPE + "\n\n");
        sb.append("USER: "+ Build.USER );

        return sb.toString();
    }
    public static boolean isVPNConnected(Context context) {
        List<String> networkList = new ArrayList<>();
        try {
            for (java.net.NetworkInterface networkInterface : java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp()) {networkList.add(networkInterface.getName());}
            }
        } catch (Exception ex) {
        }
        return networkList.contains("tun0") || networkList.contains("ppp0");
    }
    public static Drawable circularImage(Context context, Bitmap bitmap){
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(null, bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }



}
