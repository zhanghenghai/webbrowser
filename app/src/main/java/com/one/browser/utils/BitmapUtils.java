package com.one.browser.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;


/**
 * Bitmap 帮助类之一
 *
 * @author 18517
 */
public class BitmapUtils {
    public static void getKb(Context context, int w, int h, PhotoEditor photoEditor, int quality,BitmapSize bitmapSize) {
        photoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(@Nullable Bitmap bitmap) {
                //宽高
                int src_w = bitmap.getWidth();
                int src_h = bitmap.getHeight();
                float scale_w = ((float) w) / src_w;
                float scale_h = ((float) h) / src_h;
                Matrix matrix = new Matrix();
                matrix.postScale(scale_w, scale_h);

                // 单图片处理
                Bitmap image = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);


                File outputImageFile = new File(context.getExternalFilesDir(null), "output_image.JPEG");
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(outputImageFile);
                    image.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
                    fileOutputStream.close();
                    long fileSizeInBytes = outputImageFile.length();

                    // 将文件大小转换为 KB
                    double fileSizeInKB = fileSizeInBytes / 1000.0;
                    Log.i("TAG", "保存后文件大小（字节数）: " + fileSizeInBytes);
                    Log.i("TAG", "保存后文件大小（KB）: " + fileSizeInKB);
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    String format = decimalFormat.format(fileSizeInKB);
                    Log.i("TAG", "保留小数点后两位: "+format);
                    bitmapSize.getBitmap(Double.parseDouble(format));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@Nullable Exception e) {

            }
        });


    }

    /**
     * 保存指纹图片
     */
    public static void saveBitmap(Context context, int w, int h, int quality, PhotoEditor photoEditor) {
        photoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(@Nullable Bitmap bitmap) {
                //宽高
                int src_w = bitmap.getWidth();
                int src_h = bitmap.getHeight();
                float scale_w = ((float) w) / src_w;
                float scale_h = ((float) h) / src_h;
                Matrix matrix = new Matrix();
                matrix.postScale(scale_w, scale_h);

                // 单图片处理
                Bitmap image = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);
                Bitmap images = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix, true);
                // 图片名字
                String sdCardDir = System.currentTimeMillis() + "";
                Log.i("TAG", "saveBitmap: " + sdCardDir);

                // 保存单张图片
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android Q 以上
                    // 保存单照片
                    saveQUp(image, context, sdCardDir, quality);
                } else {
                    // Android Q 以下
                    // 保存单照片
                    saveQNext(image, context, sdCardDir, quality);
                }
                int width = images.getWidth();
                Log.i("TAG", "打印宽度 " + width);
                int width_size = width + 50;
                Log.i("TAG", "计算加间距之后的宽度" + width_size);
                int width_sum = width_size * 4 + 50;
                Log.i("TAG", "画布总长度 " + width_sum);
                int height = images.getHeight();
                Log.i("TAG", "打印高度: " + height);
                int height_size = height + 50;
                Log.i("TAG", "计算加间距之后的高度" + height_size);
                int height_sum = height_size * 2 + 50;
                // 多图片处理
                // 创建布局
                Bitmap bitmaps = Bitmap.createBitmap(width_sum, height_sum, Bitmap.Config.ARGB_8888);
                bitmaps.eraseColor(Color.parseColor("#FFFFFF"));
                // 开始绘制
                Canvas canvas = new Canvas(bitmaps);
                // 第一页
                canvas.drawBitmap(images, 50, 50, null);
                canvas.drawBitmap(images, 50 + width + 50, 50, null);
                canvas.drawBitmap(images, 50 + width + 50 + width + 50, 50, null);
                canvas.drawBitmap(images, 50 + width + 50 + width + 50 + width + 50, 50, null);
                // 第二页
                canvas.drawBitmap(images, 50, 50 + height + 50, null);
                canvas.drawBitmap(images, 50 + width + 50, 50 + height + 50, null);
                canvas.drawBitmap(images, 50 + width + 50 + width + 50, 50 + height + 50, null);
                canvas.drawBitmap(images, 50 + width + 50 + width + 50 + width + 50, 50 + height + 50, null);
                // 绘制完成
                String fileName = System.currentTimeMillis() + "s";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveQUp(bitmaps, context, fileName);
                } else {
                    saveQNext(bitmaps, context, fileName);
                }
            }


            @RequiresApi(api = Build.VERSION_CODES.Q)
            private void saveQUp(Bitmap image, Context context, String fileName) {
                // 文件夹路径
                String imageSaveFilePath = Environment.DIRECTORY_DCIM + File.separator + "艾跳跳";
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


            // Android Q 以上
            @RequiresApi(api = Build.VERSION_CODES.Q)
            public void saveQUp(Bitmap image, Context context, String fileName, int quality) {
                // 文件夹路径
                String imageSaveFilePath = Environment.DIRECTORY_DCIM + File.separator + "艾跳跳";
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
                    image.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (uri != null) {
                        localContentResolver.delete(uri, null, null);
                    }
                }
            }

            /*
             * 这种方式不用创建文件，适用所有的Android设备 原因是数据都存在于data/包里面
             * */
            // Android Q 以下
            public void saveQNext(Bitmap image, Context context, String fileName, int quality) {
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


            // Android Q 以下
            public void saveQNext(Bitmap image, Context context, String fileName) {
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

            @Override
            public void onFailure(@Nullable Exception e) {

            }
        });
    }

    /**
     * 返回图片大小
     */
    public interface BitmapSize {
        /**
         * 获取图片
         *
         * @param imageSize 图片大小
         */
        void getBitmap(double imageSize);
    }

}