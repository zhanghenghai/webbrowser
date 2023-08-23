package com.one.browser.utils;

import android.util.Log;

import com.one.browser.sqlite.Bookmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 18517
 */
public class DataUtil {
    public static String generateHomepageHtml(String filePath, String stylesheetUrl) {
        if (!new File(filePath).exists()) {
            Log.i("TAG", " 创建成功 ");
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
                // 移动端设置
                bufferedWriter.write("<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                // CSS
                bufferedWriter.write("<link rel=\"stylesheet\" href=\"" + stylesheetUrl + "\"><style></style>");
                bufferedWriter.write(" body {font-family: Arial, sans-serif;margin: 0;padding: 0;}");
                bufferedWriter.write(".bookmark-list {display: flex;flex-direction: column;padding: 0;}");
                bufferedWriter.write(".bookmark-list li {display: flex;align-items: center;padding: 10px;border-bottom: 1px solid #ccc;}");
                bufferedWriter.write(".bookmark-list li:last-child {border-bottom: none;}");
                bufferedWriter.write(".bookmark-list li .index {font-weight: bold;margin-right: 20px;}");
                bufferedWriter.write(".bookmark-list li .info {display: flex;flex-direction: column;}");
                bufferedWriter.write(".bookmark-list li a {color: #000;text-decoration: none;}/style></head>");

                // HTML开始
                bufferedWriter.write("<body><ul class=\"bookmark-list\" id=\"book\">");
                // 获取数据
                List<Bookmark> list = new LinkedList<>();
                // 内容循环
                for (Bookmark bookmark : list){
                    bufferedWriter.write("<li><span class=\"index\">"+ bookmark.getIcon() + "</span><div class=\"info\"><a href="+bookmark.getUrl()+">"+ bookmark.getTitle() +"</a><span>"+bookmark.getUrl()+"</span></li>");
                }
                // JS
                bufferedWriter.write("<script type=\"text/javascript\"></script>");
                // HTML结束
                bufferedWriter.write("</ul></body></html>");
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        Log.i("TAG", "generateHomepageHtml:  >>>>> "+filePath);
        return filePath;
    }
}
