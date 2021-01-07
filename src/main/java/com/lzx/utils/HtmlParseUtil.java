package com.lzx.utils;

import com.lzx.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * [用一句话描述此类]
 *
 * @author: liuzx
 * @create: 2020-12-28 11:17
 **/
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("心理学").forEach(System.out::println);
    }

    public List<Content> parseJD(String keywords) throws IOException {
        //获取请求 https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword="+keywords;
        //解析网页
        Document document =  Jsoup.parse(new URL(url),30000);
        //所有js能操作的都可以用
        Element element = document.getElementById("J_goodsList");
        //获取li元素
        Elements elements= element.getElementsByTag("li");

        //对象
        List<Content> goodList= new ArrayList<>();
        //获取元素中的内容
        for (Element el : elements){
            //图片
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String name =  el.getElementsByClass("p-name").eq(0).text();
            Content centent = new Content();
            centent.setTitle(name);
            centent.setPrice(price);
            centent.setImg(img);
            goodList.add(centent);
        }

        return goodList;
    }

}
