/**
 * @file Indexer.java
 * @brief generate lucene index for the input file
 * @author yy
 * @version 1.0
 * @date 2013-03-13
 */

package com.nju.app.index;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class Indexer {

    private boolean create = true;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: java " + Indexer.class.getName()
                    + " <index dir> <data dir>");
        }
        String indexDir = args[0];         //1
        String dataDir = args[1];          //2


        Indexer indexer = new Indexer(indexDir);
        int numIndexed;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } finally {
            indexer.close();
        }

        System.out.println("Indexing " + numIndexed + "files");
    }

    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        Directory dir = FSDirectory.open(new File(indexDir));
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

        // Optional: for better indexing performance, if you
        // are indexing many documents, increase the RAM
        // buffer.  But if you do this, increase the max heap
        // size to the JVM (eg add -Xmx512m or -Xmx1g):
        //
        // iwc.setRAMBufferSizeMB(256.0);

        writer = new IndexWriter(dir, analyzer, create, IndexWriter.MaxFieldLength.UNLIMITED);

    }

    public void close() throws IOException {
        writer.close();                             //4
    }

    public int index(String dataDir, FileFilter filter)
        throws Exception {

        File[] files = new File(dataDir).listFiles();

        for (File f: files) {
            if (!f.isDirectory() &&
                    !f.isHidden() &&
                    f.exists() &&
                    f.canRead() &&
                    (filter == null || filter.accept(f))) {
                indexFile(f);
                    }
        }

        return writer.numDocs();                     //5
    }

    private static class TextFilesFilter implements FileFilter {
        public boolean accept(File path) {
            return path.getName().toLowerCase()        //6
                .endsWith(".txt");                  //6
        }
    }

    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new Field("contents", new FileReader(f)));      //7
        doc.add(new Field("filename", f.getName(),              //8
                    Field.Store.YES, Field.Index.NOT_ANALYZED));//8
        doc.add(new Field("fullpath", f.getCanonicalPath(),     //9
                    Field.Store.YES, Field.Index.NOT_ANALYZED));//9
        return doc;
    }

    private void indexFile(File file) throws Exception {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document doc = getDocument(file);
        if (create == true) {
            // New index, so we just add the document (no old document can be there):
            System.out.println("adding " + file);
            writer.addDocument(doc);
        } else {
            // Existing index (an old copy of this document may have been indexed) so 
            // we use updateDocument instead to replace the old one matching the exact 
            // path, if present:
            System.out.println("updating " + file);
            writer.updateDocument(new Term("path", file.getPath()), doc);
        }

    }
}

/*
#1 Create index in this directory
#2 Index *.txt files from this directory
#3 Create Lucene IndexWriter
#4 Close IndexWriter
#5 Return number of documents indexed
#6 Index .txt files only, using FileFilter
#7 Index file content
#8 Index file name
#9 Index file full path
#10 Add document to Lucene index
*/
