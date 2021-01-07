package com.lzx.controller;

import com.lzx.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * [用一句话描述此类]
 *
 * @author: liuzx
 * @create: 2021-01-06 15:38
 **/
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("") String keyword) throws IOException {
        return contentService.parseContent(keyword);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchPage(@PathVariable("keyword") String keyword,@PathVariable("pageNo") int pageNo,@PathVariable("pageSize") int pageSize) throws IOException {
        return contentService.searchPage(keyword,pageNo,pageSize);
    }


    @GetMapping("/searchHightLight/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchHightLight(@PathVariable("keyword") String keyword,@PathVariable("pageNo") int pageNo,@PathVariable("pageSize") int pageSize) throws IOException {
        return contentService.searchHighlightPage(keyword,pageNo,pageSize);
    }

}
