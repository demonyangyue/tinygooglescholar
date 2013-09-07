package com.nju.app.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.nju.app.index.IndexManager;
import com.nju.app.katta.KattaClient;

/**
 * This class is used to search the 
 * Lucene index and return search results
 */
public class SearchManager {
	
    private String searchWord;
    
    private IndexManager indexManager;
    
    private Analyzer analyzer;

    private KattaClient client;
    
    public SearchManager(String searchWord){
    	this.searchWord   =  searchWord;
    	this.indexManager =  new IndexManager();
    	this.analyzer     =  new StandardAnalyzer(Version.LUCENE_30);
        this.client = new KattaClient();
    }
    
    /**
     * do search
     */
    public List search(){
    	List searchResult = new ArrayList();
    	if(false == indexManager.ifIndexExist()){
    		try {
				if(false == indexManager.createIndex()){
					return searchResult;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return searchResult;
			}
    	}

        String[] indexNames = {"demoIndex"};
        try {
            searchResult = client.search(indexNames, searchWord, 5);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return searchResult;
    }
    	
//    	IndexSearcher indexSearcher = null;
//    	
//    	try{
//            String indexDir = indexManager.getIndexDir();
//            indexSearcher = new IndexSearcher(FSDirectory.open(new File(indexDir)));
//    	}catch(IOException ioe){
//    		ioe.printStackTrace();
//    	}
//        
//    	QueryParser queryParser = new QueryParser(Version.LUCENE_30,"contents",analyzer);
//    	Query query = null;
//    	try {
//			query = queryParser.parse(searchWord);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		if(null != query && null != indexSearcher){			
//			try {
//				TopDocs results = indexSearcher.search(query, null, 5);
//                ScoreDoc[ ] hits = results.scoreDocs;
//				for(int i = 0; i < hits.length; i ++){
//					SearchResultBean resultBean = new SearchResultBean();
//					resultBean.setPDFPath(indexSearcher.doc(hits[i].doc).get("path"));
//					resultBean.setPDFTitle(indexSearcher.doc(hits[i].doc).get("title"));
//                    searchResult.add(resultBean);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//    	return searchResult;
//    }

}
