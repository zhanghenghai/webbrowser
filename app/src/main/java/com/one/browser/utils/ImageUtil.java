package com.one.browser.utils;



import java.util.Base64;

/**
 * @author 18517
 */
public class ImageUtil {

    public static String getByte(byte[] bytes){

        Base64.Encoder encoder = null;
        encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }


}
