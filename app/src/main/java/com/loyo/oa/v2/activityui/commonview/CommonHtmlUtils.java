package com.loyo.oa.v2.activityui.commonview;

import android.text.TextUtils;

import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.tool.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理 html 数据
 * Created by xeq on 16/11/4.
 */

public class CommonHtmlUtils {
    private static CommonHtmlUtils instance;

    public static CommonHtmlUtils Instance() {
        if (null == instance) {
            instance = new CommonHtmlUtils();
        }
        return instance;
    }

    public String checkContent(String content) {
        String textContent = "";
        Document jsoup = Jsoup.parse(content);
        Elements imgs = jsoup.select("img,p");
        for (Element img : imgs) {
            String image = img.attr("src");
            if (!TextUtils.isEmpty(image)) {
                textContent = textContent + "  [图片]  ";
            } else {
                textContent = textContent + img.text().trim();
            }
        }
        return textContent;
    }

    public List<ImgAndText> checkContentList(String content) {
        List<ImgAndText> list = new ArrayList<>();
        Document jsoup = Jsoup.parse(content);
        Elements imgs = jsoup.select("img,p");
        for (Element img : imgs) {
            String image = img.attr("src");
            if (!TextUtils.isEmpty(image)) {
                list.add(new ImgAndText("img", image));
            } else {
                list.add(new ImgAndText("text", img.text().trim()));
            }
            LogUtil.d(img.text().trim() + "---过滤的图片:" + img.attr("src"));
        }
        return list;
    }
}
