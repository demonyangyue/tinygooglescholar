package com.nju.app.index; 

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.config.TikaConfig;

import com.nju.app.hdfs.HDFSDirWriter;
import com.nju.app.katta.KattaClient;


/**
 * This class is used to create index for html files
 *
 */
public class IndexManager {
	
	//the directory that stores html files
    private final String dataDir  = "/home/yy/programme/java/pdfSamples";
    
    //the directory that is used to store lucene index
    private final String indexDir = "/home/yy/programme/java/index";
    
    private final String hdfsDir = "hdfs://localhost:54310/index/aIndex";

    private HDFSDirWriter hdfsWriter = new HDFSDirWriter();

    private KattaClient client = new KattaClient();
    /**
     * create index
     */
    public boolean createIndex() throws IOException{

        if(ifIndexExist() == false) {
            TikaConfig config = TikaConfig.getDefaultConfig();	   
            TikaIndexer indexer = new TikaIndexer(indexDir);
            int numIndexed = 0;
            try {
                numIndexed = indexer.index(dataDir, null);
            }
            catch (Exception e ) {
                e.printStackTrace(System.out);
            }
            indexer.close();
            if (numIndexed == 0) return false;
             
        }
            hdfsWriter.copyDirToHDFS(indexDir, hdfsDir);
            client.addIndex("demoIndex", "hdfs://localhost:54310/index/", 2);
            return true;
        	
    }
    
    
    /**
     * judge if the index is already exist
     */
    public boolean ifIndexExist(){
        File directory = new File(indexDir);
        if(0 < directory.listFiles().length){
        	return true;
        }else{
        	return false;
        }
    }
    
    public String getDataDir(){
    	return this.dataDir;
    }
    
    public String getIndexDir(){
    	return this.indexDir;
    }
        
}
