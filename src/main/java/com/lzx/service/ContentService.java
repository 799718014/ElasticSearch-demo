package com.lzx.service;

import com.alibaba.fastjson.JSON;
import com.lzx.pojo.Content;
import com.lzx.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * [业务编写]
 *
 * @author: liuzx
 * @create: 2021-01-06 15:38
 **/
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //解析数据放入es索引中
    public Boolean parseContent(String keywords) throws IOException {
        List<Content> contentList = new HtmlParseUtil().parseJD(keywords);
        //把查询到数据放入es中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        for (int i =0; i< contentList.size(); i++){
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contentList.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    //获取这些数据实现搜索功能
    public List<Map<String,Object>> searchPage(String keyword, int pageNo,int pageSize) throws IOException {
        if(pageNo <=1){
            pageNo = 1;
        }

        //条件搜索
        SearchRequest searchRequest= new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for(SearchHit documentFields : searchResponse.getHits().getHits()){
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }

    //高亮搜素
    public List<Map<String,Object>> searchHighlightPage(String keyword, int pageNo,int pageSize) throws IOException {
        if(pageNo <=1){
            pageNo = 1;
        }

        //条件搜索
        SearchRequest searchRequest= new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title",keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlight = new HighlightBuilder();
        highlight.field("title");
        highlight.requireFieldMatch(false);     //多个高亮显示
        highlight.preTags("<span style='color:red'>");
        highlight.postTags("</span>");
        searchSourceBuilder.highlighter(highlight);

        //执行搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for(SearchHit hit : searchResponse.getHits().getHits()){
            //解析高亮字段
            Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
            HighlightField title = highlightFieldMap.get("title");
            Map<String,Object> sourceAsMap = hit.getSourceAsMap();  //原来的结果
            //解析高亮字段,将原来的字段替换成我们高亮的字段即可
            if(title != null){
                Text[] fragments = title.fragments();
                String n_title = "";
                for(Text text : fragments){
                    n_title += text;
                }
                sourceAsMap.put("title",n_title);
            }

            list.add(sourceAsMap);
        }
        return list;
    }

}
