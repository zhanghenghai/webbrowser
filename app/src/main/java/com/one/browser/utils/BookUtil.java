package com.one.browser.utils;

import android.annotation.SuppressLint;

import com.one.browser.sqlite.Bookmark;
import com.one.browser.sqlite.BookmarkDao;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 18517
 */
public class BookUtil {
    public void input(String path, BookmarkDao bookmarkDao) {
        String title = null;
        String atitle = null;
        String url = null;
        String aurl = null;

        String start = "yes";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String time = sDateFormat.format(new Date());
        int num = 0;
        File file = new File(path);
        try {
            Document document = Jsoup.parse(file, "UTF-8");
            // 获取主DL
            Element dl = document.getElementsByTag("DL").get(0);
            Elements h3 = dl.select("DT H3");
            // 获取标题
            for (int i = 0; i < h3.size(); i++) {
                Element dls = h3.get(i);
                // 将标题存入数据库中
                title = dls.text();
                url = dls.text();
                System.out.println(title);
                System.out.println("时间输出 >>>>>> " + time);
                Bookmark bookmark = new Bookmark(null, title, url, time, start);
                long id = bookmarkDao.insertDate(bookmark);
                System.out.println("插入后的ID >>>" + id);
                // 查看 DL
                Element dlss = dl.select("DT DL").get(i);
                // 循环 A 标签
                Elements s = dlss.getElementsByTag("A");
                for (Element t : s) {
                    num++;
                    atitle = t.text();
                    aurl = t.attr("href");
                    System.out.println(" >>> " + atitle);
                    System.out.println(" >>> " + aurl);
                    System.out.println("时间 >>>> " + time);
                    Bookmark bookmarka = new Bookmark(null, atitle, aurl, time, url);
                    long ids = bookmarkDao.insertDate(bookmarka);
                    System.out.println("插入后的ID >>>" + ids);
                }
            }
            // 获取 所有A
            Elements s = dl.select("A");
            String aatitle = null;
            String aaurl = null;
            for (int i = num; i < s.size(); i++) {
                aatitle = s.get(i).text();
                System.out.println("aatitle >>> " + aatitle);
                aaurl = s.get(i).attr("href");
                System.out.println("aaurl >>> " + aaurl);
                Bookmark bookmarkaa = new Bookmark(null,aatitle, aaurl, time, start);
                long ids = bookmarkDao.insertDate(bookmarkaa);
                System.out.println(ids);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
