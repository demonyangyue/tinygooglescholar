/**
 * @file KattaClient.java
 * @brief deploy indices to shards and serve for search request
 * @author yy
 * @version 1.0
 * @date 2013-03-20
 */
package com.nju.app.katta;

import net.sf.katta.util.ZkConfiguration;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkServer;
import java.util.ArrayList;
import java.util.List;

import net.sf.katta.protocol.InteractionProtocol;
import net.sf.katta.client.DeployClient;
import net.sf.katta.client.IDeployClient;
import net.sf.katta.client.IIndexDeployFuture;
import net.sf.katta.client.IndexState;
import net.sf.katta.lib.lucene.Hit;
import net.sf.katta.lib.lucene.Hits;
import net.sf.katta.lib.lucene.ILuceneClient;
import net.sf.katta.lib.lucene.LuceneClient;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;

import com.nju.app.search.SearchResultBean;
public class KattaClient {
    private ZkConfiguration _zkConf = new ZkConfiguration();
    private ZkClient _zkClient = new ZkClient(this._zkConf.getZKServers());

    public void addIndex(String name, String path, int replicationLevel) {
        InteractionProtocol protocol = new InteractionProtocol(this._zkClient, this._zkConf);
        //execute(zkConf, protocol);
        IDeployClient deployClient = new DeployClient(protocol);
        if (name.trim().equals("*")) {
            throw new IllegalArgumentException("Index with name " + name + " isn't allowed.");
        }
        if (deployClient.existsIndex(name)) {
            //throw new IllegalArgumentException("Index with name " + name + " already exists.");
            System.out.println("Index with name " + name + " already exists.");
        }

        try {
            long startTime = System.currentTimeMillis();
            IIndexDeployFuture deployFuture = deployClient.addIndex(name, path, replicationLevel);
            while (true) {
                long duration = System.currentTimeMillis() - startTime;
                if (deployFuture.getState() == IndexState.DEPLOYED) {
                    System.out.println("\ndeployed index '" + name + "' in " + duration + " ms");
                    break;
                } else if (deployFuture.getState() == IndexState.ERROR) {
                    System.err.println("\nfailed to deploy index '" + name + "' in " + duration + " ms");
                    break;
                }
                System.out.print(".");
                deployFuture.joinDeployment(1000);
            }
        } catch (final InterruptedException e) {
            System.out.println("interrupted wait on index deployment");
        }
  
        protocol.disconnect(); 
    }

    public List search(String[] indexNames, String queryString, int count) throws Exception {

        InteractionProtocol protocol = new InteractionProtocol(this._zkClient, this._zkConf);
        //execute(zkConf, protocol);
        final ILuceneClient client = new LuceneClient();
        final Query query = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30)).parse(queryString);
        final long start = System.currentTimeMillis();
        final Hits hits = client.search(query, indexNames, count);
        final long end = System.currentTimeMillis();
        System.out.println(hits.size() + " hits found in " + ((end - start) / 1000.0) + "sec.");
        //TODO convert to the resultBean
        //int index = 0;
       // final Table table = new Table(new String[] { "Hit", "Node", "Shard", "DocId", "Score" });
       // for (final Hit hit : hits.getHits()) {
       //     table.addRow(index, hit.getNode(), hit.getShard(), hit.getDocId(), hit.getScore());
       //     index++;
       // }
       // System.out.println(table.toString());

        List searchResult = new ArrayList();

        String[] fields = new String[]{"path", "title"};
        try {
            List<MapWritable> results = client.getDetails(hits.getHits(), fields);
            for (MapWritable result: results) {
                Text path = (Text)result.get(new Text("path"));
                Text title  = (Text)result.get(new Text("title"));
                SearchResultBean resultBean = new SearchResultBean();    
                resultBean.setPDFPath(path.toString());
                resultBean.setPDFTitle(title.toString());
                searchResult.add(resultBean);
            }

            protocol.disconnect();  
        }            
        catch(Exception e) {
            e.printStackTrace();
        }
        return searchResult;
    }
}
