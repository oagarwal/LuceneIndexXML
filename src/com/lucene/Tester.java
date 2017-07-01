package com.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Tester {

	String indexDir;
	String dataDir;
	Indexer indexer;
	String indexName;
	Searcher searcher;
	   
	public static void main(String[] args){
		
		if(args.length<1)
			System.out.println("lucene-index.jar create <indexDirPath> <dataDirPath>"
					+ " \n lucene-index.jar search <indexDirPath> <indexName> <keyword>"
					+ "\n Index names: fulltext, leadpara, filename, filepath, headline, pubdate"
					+ "org, person, location, dsk, classifieridx, classifierongen, classifieronmat,"
					+ "classifierontax");
		
		Tester tester;
		try {
	      tester = new Tester();
	      if(args[0].contentEquals("create")){
	    	  tester.indexDir = args[1];
	    	  tester.dataDir = args[2];
	    	  tester.createIndex();
	      }
	      else if(args[0].contentEquals("search")){
	    	  tester.indexDir = args[1];
	    	  tester.indexName = args[2];
	    	  tester.search(args[3]);
	      }
	      else{
	    	  System.out.println("Invalid option");
	      }
		} catch (IOException | ParseException | ClassCastException | ClassNotFoundException | InterruptedException e ) {
	      e.printStackTrace();
		}
	}
	
	private void createIndex() throws IOException, ClassCastException, ClassNotFoundException, InterruptedException {
		indexer = new Indexer(indexDir);
	    int numIndexed;
	    long startTime = System.currentTimeMillis();	
	    numIndexed = indexer.createIndex(dataDir);
	    long endTime = System.currentTimeMillis();
	    indexer.close();
	    System.out.println(numIndexed+" File indexed, time taken: "+(endTime-startTime)+" ms");	
	}
	
	private void search(String searchQuery) throws IOException, ParseException {
	    searcher = new Searcher(indexDir,indexName);
	    long startTime = System.currentTimeMillis();
	    TopDocs hits = searcher.search(searchQuery);
	    long endTime = System.currentTimeMillis();
		   
	    System.out.println(hits.totalHits + " hits for the word. Time :" + (endTime - startTime));
	    for(ScoreDoc scoreDoc : hits.scoreDocs) {
	       Document doc = searcher.getDocument(scoreDoc);
	       System.out.println("File: "+ doc.get(LuceneConstants.FILE_PATH) 
	       						+ ", Score: " + scoreDoc.score
	       						+ ", Date: " + doc.get(LuceneConstants.PUB_DATE));
	    }
	    searcher.close();
	}
}

