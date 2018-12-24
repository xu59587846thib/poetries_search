package com.baizhi;

import com.baizhi.entity.Poet;
import com.baizhi.entity.Poetry;
import com.baizhi.service.PoetryService;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PoetriesSearchApplicationTests {

    @Autowired
    private PoetryService poetryService;

    @Test
    public void te1() {
        List<Poetry> poetries = poetryService.findAllPoetry();
        poetries.forEach(poetry -> {
            System.out.println(poetry);
        });
    }

    @Test
    public void poetrySearch() throws IOException {
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("D:\\TestLucene\\luceneIndex\\poetry"));
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, config);
        List<Poetry> poetries = poetryService.findAllPoetry();
        Document document = null;
        for (Poetry poetry : poetries) {
            document = new Document();
            document.add(new IntField("id", poetry.getId(), Field.Store.YES));
            document.add(new StringField("poet", poetry.getPoet().getName(), Field.Store.YES));
            document.add(new TextField("title", poetry.getTitle(), Field.Store.YES));
            document.add(new TextField("content", poetry.getContent(), Field.Store.YES));
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
        indexWriter.close();
    }

    @Test
    public void poetrySearch2() throws IOException, ParseException, InvalidTokenOffsetsException {
        FSDirectory fsDirectory = FSDirectory.open(Paths.get("D:\\TestLucene\\luceneIndex\\poetry"));
        IndexReader indexReader = DirectoryReader.open(fsDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser queryParser = new QueryParser("content", new IKAnalyzer());

        Query query = null;
        query = queryParser.parse("龙城");

        // 声明分页相关请求参数信息
        int nowPage = 2;
        int pageSize = 10;

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
        Formatter formatter = new SimpleHTMLFormatter("<span coler='red'>", "</span>");
        Highlighter highlighter = new Highlighter(formatter, scorer);
        //====================================================

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        List<Poetry> poetries = new ArrayList<>();
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document document = indexReader.document(docID);
            String highlighterBestFragment = highlighter.getBestFragment(new IKAnalyzer(), "content", document.get("content"));
            poetries.add(new Poetry(Integer.parseInt(document.get("id")), null, highlighterBestFragment, document.get("title"), new Poet(null, document.get("poet"))));
        }

        indexReader.close();

        poetries.forEach(poetry -> {
            System.out.println(poetry);
        });
    }

    @Test
    public void te11(){
        String[] a = {};
        System.out.println("=============");
        System.out.println(a==null||a.length==0);
        System.out.println("=============");

    }

}

