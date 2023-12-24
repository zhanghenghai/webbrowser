package com.one.browser.utils;

import android.graphics.Bitmap;

public class ScaleBitmap {

    public static Bitmap centerCrop4To3(Bitmap srcBitmap) {
        int desWidth = srcBitmap.getWidth();
        int desHeight = srcBitmap.getHeight();
        float desRate = (float) desWidth / desHeight;
        float rate = 4f / 3f;
        if (desRate > rate) {//宽有多余
            desWidth = (int) (desHeight * 4 / 3);
        } else {//宽有不够，裁剪高度
            desHeight = (int) (desWidth * 3 / 4);
        }
        return centerCrop(srcBitmap, desWidth, desHeight);
    }

    public static Bitmap centerCrop16To9(Bitmap srcBitmap) {
        int desWidth = srcBitmap.getWidth();
        int desHeight = srcBitmap.getHeight();
        float desRate = (float) desWidth / desHeight;
        float rate = 16f / 9f;
        if (desRate > rate) {//宽有多余
            desWidth = (int) (desHeight * 16 / 9);
        } else {//宽有不够，裁剪高度
            desHeight = (int) (desWidth * 9 / 16);
        }
        return centerCrop(srcBitmap, desWidth, desHeight);
    }

    public static Bitmap centerCrop(Bitmap srcBitmap, int desWidth, int desHeight) {
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        int newWidth = srcWidth;
        int newHeight = srcHeight;
        float srcRate = (float) srcWidth / srcHeight;
        float desRate = (float) desWidth / desHeight;
        int dx = 0, dy = 0;
        if (srcRate == desRate) {
            return srcBitmap;
        } else if (srcRate > desRate) {
            newWidth = (int) (srcHeight * desRate);
            dx = (srcWidth - newWidth) / 2;
        } else {
            newHeight = (int) (srcWidth / desRate);
            dy = (srcHeight - newHeight) / 2;
        }
        //创建目标Bitmap，并用选取的区域来绘制
        Bitmap desBitmap = Bitmap.createBitmap(srcBitmap, dx, dy, newWidth, newHeight);
        return desBitmap;
    }

}
