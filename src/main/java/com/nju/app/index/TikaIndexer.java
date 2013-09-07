package com.nju.app.index;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.config.TikaConfig;

import org.xml.sax.ContentHandler;


public class TikaIndexer extends Indexer {

  private boolean DEBUG = false;                     //1

  static Set<String> textualMetadataFields           //2
        = new HashSet<String>();                     //2
  static {                                           //2
    textualMetadataFields.add(Metadata.TITLE);       //2
    textualMetadataFields.add(Metadata.AUTHOR);      //2
    textualMetadataFields.add(Metadata.COMMENTS);    //2
    textualMetadataFields.add(Metadata.KEYWORDS);    //2
    textualMetadataFields.add(Metadata.DESCRIPTION); //2
    textualMetadataFields.add(Metadata.SUBJECT);     //2
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      throw new IllegalArgumentException("Usage: java " + TikaIndexer.class.getName()
        + " <index dir> <data dir>");
    }

    TikaConfig config = TikaConfig.getDefaultConfig();  //3
    
    String indexDir = args[0];
    String dataDir = args[1];

    TikaIndexer indexer = new TikaIndexer(indexDir);
    int numIndexed = indexer.index(dataDir, null);
    indexer.close();

    System.out.println("Indexing " + numIndexed + " files ");
  }

  public TikaIndexer(String indexDir) throws IOException {
    super(indexDir);
  }

  protected Document getDocument(File f) throws Exception {

    Metadata metadata = new Metadata();
    metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName());   // 4

    // If you know content type (eg because this document
    // was loaded from an HTTP server), then you should also
    // set Metadata.CONTENT_TYPE

    // If you know content encoding (eg because this
    // document was loaded from an HTTP server), then you
    // should also set Metadata.CONTENT_ENCODING

    InputStream is = new FileInputStream(f);      // 5
    Parser parser = new AutoDetectParser();       // 6
    ContentHandler handler = new BodyContentHandler(-1); // 7
    ParseContext context = new ParseContext();   // 8
    context.set(Parser.class, parser);           // 8

    try {
      parser.parse(is, handler, metadata,      // 9
                   new ParseContext());        // 9
    } finally {
      is.close();
    }

    Document doc = new Document();

    doc.add(new Field("contents", handler.toString(),           // 10
                      Field.Store.NO, Field.Index.ANALYZED));   // 10

    if (DEBUG) {
      System.out.println("  all text: " + handler.toString());
    }
    
    for(String name : metadata.names()) {         //11
      String value = metadata.get(name);

      if (textualMetadataFields.contains(name)) {
        doc.add(new Field("contents", value,      //12
                          Field.Store.NO, Field.Index.ANALYZED));
      }

      doc.add(new Field(name, value, Field.Store.YES, Field.Index.NO)); //13

      if (DEBUG) {
        System.out.println("  " + name + ": " + value);
      }
    }

    if (DEBUG) {
      System.out.println();
    }

    doc.add(new Field("title", f.getName(),              
            Field.Store.YES, Field.Index.ANALYZED));
    doc.add(new Field("path", f.getCanonicalPath(),     
            Field.Store.YES, Field.Index.NOT_ANALYZED));

    return doc;
  }
}

/*
  #1 Change to true to see all text
  #2 Which metadata fields are textual
  #3 List all mime types handled by Tika
  #4 Create Metadata for the file
  #5 Open the file
  #6 Automatically determines file type
  #7 Extracts metadata and body text
  #8 Setup ParseContext
  #9 Does all the work!
  #10 Index body content
  #11 Index metadata fields
  #12 Append to contents field
  #13 Separately store metadata fields
  #14 Index file path
*/
