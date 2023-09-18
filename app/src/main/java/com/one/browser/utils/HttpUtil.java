package com.one.browser.utils;

/**
 * @author 18517
 */
public class HttpUtil {

    /**
     * 获取证件列表
     */
    public static final String HTTP = "http://www.howhh.cn/xiaoai";
    /**
     * 证件生成接口
     */
    public static final String MATTING = "http://www.howhh.cn:8866/predict/modnet_hrnet18_matting";
    /**
     * 翻译接口
     */
    public static final String TRANSLATE = "http://192.168.8.100:8080/api/translate?";

    /**
     * 成语接口
     */
    public static final String IDIOM = "http://192.168.8.100:8080/api/idiom?";
    /**
     * 汉字词典
     */
    public static final String CHINESE = "http://192.168.8.100:8080/api/getSysChineseList?";
    /**
     * 图片高清
     */
    public static final String RESTORATION = "http://192.168.8.210:8088/sys_ImageRepair/getSysImageRepair";
    /*图片转BASE64*/
    public static final String BASE64Image = "http://192.168.8.210:8088/sys_ImageBase64/getBase64";

}
