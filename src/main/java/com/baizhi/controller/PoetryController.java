package com.baizhi.controller;

import com.baizhi.entity.Poet;
import com.baizhi.entity.Poetry;
import com.baizhi.service.PoetryService;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/poetry")
public class PoetryController {

    @Autowired
    private PoetryService poetryService;

    @RequestMapping("/toSearch")
    public String toSearch() {
        return "tosearch";
    }

    @RequestMapping("/search")
    public String search(HttpSession session, String search, String[] indexs) {
        if (indexs == null || indexs.length == 0) {
            indexs = new String[]{"content", "title", "poet"};
        }
        if (search == null || search.isEmpty()) {
            search = "默认";
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("search", search);
        parameters.put("indexs", indexs);
        session.setAttribute("parameters", parameters);
        return "redirect:/poetry/showSearch";
    }

    @RequestMapping("/showSearch")
    public String showSearch(HttpSession session, HttpServletRequest request, Integer nowPage, Integer pageSize) throws IOException, ParseException, InvalidTokenOffsetsException {
        Map<String, Object> parameters = (Map<String, Object>) session.getAttribute("parameters");
        String search = (String) parameters.get("search");
        String[] indexs = (String[]) parameters.get("indexs");
        if (nowPage == null) {
            nowPage = 1;
        }

        FSDirectory fsDirectory = FSDirectory.open(Paths.get("D:\\TestLucene\\luceneIndex\\poetry"));
        IndexReader indexReader = DirectoryReader.open(fsDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser queryParser = new QueryParser("content", new IKAnalyzer());

        Query query = new MultiFieldQueryParser(indexs, new IKAnalyzer()).parse(search);

        // 声明分页相关请求参数信息
        if (pageSize == null) {
            pageSize = 10;
        }

        // n 表示 保留符合条件前N条记录
        //TopDocs topDocs = indexSearcher.search(query, 10);
        // 分页数据
        TopDocs topDocs = null;
        if (nowPage <= 1) {
            // 假如说: 查第一页 每页2条
            topDocs = indexSearcher.search(query, pageSize);
        } else if (nowPage > 1) {
            // 假如说: 不是第一页 必须先获取上一页的最后一条记录的ScoreDoc
            topDocs = indexSearcher.search(query, (nowPage - 1) * pageSize);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            ScoreDoc sd = scoreDocs[scoreDocs.length - 1];
            // 参数一: 当前页的上一页的最后的文档的ScoreDoc对象
            topDocs = indexSearcher.searchAfter(sd, query, pageSize);
        }

        // 总记录数
        int totalHits = topDocs.totalHits;

        //====================================================
        // 创建高亮器对象
        Scorer scorer = new QueryScorer(query);
        // 默认高亮样式 加粗
        // 使用自定义的高亮样式
        Formatter formatter = new SimpleHTMLFormatter("<font color=\"red\">", "</font>");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        //====================================================

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        List<Poetry> poetries = new ArrayList<>();
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document document = indexReader.document(docID);

            String poetFragment = highlighter.getBestFragment(new IKAnalyzer(), "poet", document.get("poet"));
            String titleFragment = highlighter.getBestFragment(new IKAnalyzer(), "title", document.get("title"));
            String contentFragment = highlighter.getBestFragment(new IKAnalyzer(), "content", document.get("content"));
            poetries.add(new Poetry(Integer.parseInt(document.get("id")), null, contentFragment ==
                    null ? document.get("content") : contentFragment,
                    titleFragment == null ? document.get("title") : titleFragment, new Poet(null,
                    poetFragment == null ? document.get("poet") : poetFragment)));

        }

        indexReader.close();

        request.setAttribute("poetries", poetries);
        request.setAttribute("nowPage", nowPage);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("total", totalHits);
        request.setAttribute("maxPage", totalHits % pageSize == 0 ? totalHits / pageSize : totalHits / pageSize + 1);

        return "search";
    }

    @RequestMapping("/outSearch")
    public String outSearch(HttpSession session) {
        session.removeAttribute("parameters");
        return "redirect:/poetry/toSearch";
    }
}
