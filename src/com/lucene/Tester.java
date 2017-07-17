package com.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.commons.cli.*;

public class Tester {

	String indexDir;
	String dataDir;
	Indexer indexer;
	String indexName;
	Searcher searcher;
	   
	public static void main(String[] args) {
		
		Options options = new Options();
		Option option = new Option("o", "option", true, "Valid values are create and search");
		option.setRequired(true);
	    options.addOption(option);
	    
	    Option indexDir = new Option("i", "indexDir", true, "Index directory");
	    indexDir.setRequired(true);
	    options.addOption(indexDir);
	    
	    Option dataDir = new Option("d", "dataDir", true, "Data directory");
	    dataDir.setRequired(false);
	    options.addOption(dataDir);
	    
	    Option indexName = new Option("n", "indexName", true, "Valid values are fulltext, leadpara, filename, filepath, headline, pubdate"
	    		+ "org, person, location, dsk, classifieridx, classifierongen, classifieronmat, classifierontax, pubyear, pubmonth, pubday"
	    		+ "pubdayofweek, section, column, pagenum, onlinesection");
	    indexName.setRequired(false);
	    options.addOption(indexName);
	    
	    Option searchTerm = new Option("t", "searchTerm", true, "Term to search");
	    searchTerm.setRequired(false);
	    options.addOption(searchTerm);
	    
	    CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("lucene-index-nytimes", options);
            System.exit(1);
            return;
        } 

        String task = cmd.getOptionValue("option");
		
		Tester tester;
		try {
	      tester = new Tester();
	      if(task.contentEquals("create")){
	    	  tester.indexDir = cmd.getOptionValue("indexDir");
	    	  tester.dataDir = cmd.getOptionValue("dataDir");
	    	  tester.createIndex();
	      }
	      else if(task.contentEquals("search")){
	    	  tester.indexDir = cmd.getOptionValue("indexDir");
	    	  tester.indexName = cmd.getOptionValue("indexName");
	    	  tester.search(cmd.getOptionValue("searchTerm"));
	      }
	      else{
	    	  formatter.printHelp("lucene-index-nytimes", options);
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

