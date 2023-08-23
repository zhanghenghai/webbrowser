package com.one.browser.utils;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * @author 18517
 */
public class AlphaUtil {

    public static Bitmap alphaBitmap(Bitmap front, Bitmap background, Bitmap alpha) {
        int width = front.getWidth();
        int height = front.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] frontPixels = new int[width * height];
        int[] backgroundPixels = new int[width * height];
        int[] alphaPixels;
        try {
            alphaPixels = new int[width * height];
        } catch (Exception e) {
            Log.i("TAG", "synthetizeBitmap: 报错了 >>>>>");
            e.getMessage();
            return null;
        }

        front.getPixels(frontPixels, 0, width, 0, 0, width, height);
        background.getPixels(backgroundPixels, 0, width, 0, 0, width, height);
        alpha.getPixels(alphaPixels, 0, width, 0, 0, width, height);
        float frontA = 0, frontR = 0, frontG = 0, frontB = 0;
        float backgroundR = 0, backgroundG = 0, backgroundB = 0;
        float alphaR = 0, alphaG = 0, alphaB = 0;
        int index = 0;

        //逐个像素赋值（这种写法比较耗时，后续可以优化）
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = width * row + col;

                //取出前景图像像素值
                frontA = (frontPixels[index] >> 24) & 0xff;
                frontR = (frontPixels[index] >> 16) & 0xff;
                frontG = (frontPixels[index] >> 8) & 0xff;
                frontB = frontPixels[index] & 0xff;

                //取出alpha像素值
                alphaR = (alphaPixels[index] >> 16) & 0xff;
                alphaG = (alphaPixels[index] >> 8) & 0xff;
                alphaB = alphaPixels[index] & 0xff;

                //取出背景图像像素值
                backgroundR = (backgroundPixels[index] >> 16) & 0xff;
                backgroundG = (backgroundPixels[index] >> 8) & 0xff;
                backgroundB = backgroundPixels[index] & 0xff;

                //重新合成  ImgOut = F * alpha/255 + BG * ( 1 - alpha/255 )
                frontR = frontR * alphaR / 255 + backgroundR * (1 - alphaR / 255);
                frontG = frontG * alphaG / 255 + backgroundG * (1 - alphaG / 255);
                frontB = frontB * alphaB / 255 + backgroundB * (1 - alphaB / 255);
                frontPixels[index] = (int) frontA << 24 | ((int) frontR << 16) | ((int) frontG << 8) | (int) frontB;
            }
        }
        result.setPixels(frontPixels, 0, width, 0, 0, width, height);
        return result;
    }
}
