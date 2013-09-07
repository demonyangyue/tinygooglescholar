/**
 * @file HDFSDirWriter.java
 * @brief write files from local directory to HDFS
 * @author yy
 * @version 1.0
 * @date 2013-03-19
 */
package com.nju.app.hdfs;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSDirWriter {

    public void copyDirToHDFS( String localDir, String HDFSDir ){
	try {
		Configuration conf = new Configuration();
		
		conf.addResource(new Path("/home/hduser/hadoop/conf/core-site.xml"));
    		conf.addResource(new Path("/home/hduser/hadoop/conf/hdfs-site.xml"));
		FileSystem fs = FileSystem.get(conf);

		File localFiles = new File(localDir);
		File[] localFileLists = localFiles.listFiles();

		Path[] srcs = new Path[localFileLists.length];
		for (int i = 0; i < srcs.length; i++) {
		    srcs[i] = new Path(localFileLists[i].getCanonicalPath());
		}

		Path dst = new Path(HDFSDir);
		if (!fs.exists(dst)) {
		    System.out.println("the path dosen't exist in HDFS");
		    return;
		}

		else {
		    fs.copyFromLocalFile(false, true, srcs, dst);
		    System.out.println("copy files to hdfs success");
		}
	}
	catch(IOException e){
		e.printStackTrace(System.out);
	}
    
    }
    
}

