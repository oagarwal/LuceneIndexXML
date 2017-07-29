package com.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import jdk.nashorn.internal.parser.Token;

import org.apache.commons.cli.*;

public class Tester {

	String indexDir;
	String dataDir;
	Indexer indexer;
	String indexName;
	Searcher searcher;
	   
	public static void main(String[] args) {
		
		Options options = new Options();
		Option option = new Option("p", "option", true, "Valid values are create and search");
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
	    		+ "pubdayofweek, section, column, pagenum, onlinesection, persons, organizations, locations");
	    indexName.setRequired(false);
	    options.addOption(indexName);
	    
	    Option searchTerm = new Option("t", "searchTerm", true, "Term to search");
	    searchTerm.setRequired(false);
	    options.addOption(searchTerm);
	    
	    Option isFile = new Option("y", "isFile", false, "Should file be used for search terms");
	    isFile.setRequired(false);
	    options.addOption(isFile);
	    
	    Option fileName = new Option("f", "fileName", true, "Name of the file containing the search terms. A new term in every line.");
	    fileName.setRequired(false);
	    options.addOption(fileName);
	    
	    Option outputFile = new Option("o", "outputFile", true, "Output file name");
	    outputFile.setRequired(false);
	    options.addOption(outputFile);
	    
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
	    	  if(cmd.hasOption("isFile")){
	    		tester.search(cmd.getOptionValue("fileName"),true,cmd.getOptionValue("outputFile"));  
	    	  }
	    	  else {
	    		  tester.search(cmd.getOptionValue("searchTerm"),false,cmd.getOptionValue("outputFile"));
	    	  }
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
	
	private void search(String searchQuery, boolean isFile, String outputFile) throws IOException, ParseException {
	    searcher = new Searcher(indexDir,indexName);
	    if(isFile){
	    	BufferedReader br = new BufferedReader(new FileReader(searchQuery));
	    	FileWriter fw = new FileWriter(new File(outputFile));
	    	String line;
	    	while ((line = br.readLine()) != null) {
	    		fw.write(line);
	    		line = line.replaceAll(" ", "_").toLowerCase();
	    		
	    		HashMap<Integer,Integer> hs = new HashMap<Integer,Integer>();
	    		for(int i=1987; i<=2007; i++){
	    			hs.put(i, 0);
	    		}
	    		
	    		TopDocs hits = searcher.search(line);
	    		for(ScoreDoc scoreDoc : hits.scoreDocs) {
	    			Document doc = searcher.getDocument(scoreDoc);
	    			String[] tokens = doc.get(LuceneConstants.FILE_PATH).split("/");
	    			int year = Integer.parseInt(tokens[tokens.length-4]);
	    			hs.put(year,hs.get(year)+1);
	    		}
	    		
	    		for(int i=1987; i<=2007; i++){
	    			fw.write("," + hs.get(i).toString());	
	    		}
	    		fw.write("\n");
	    	}
	    	br.close();
	    	fw.close();
	    }
	    else {
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
	    }
	    searcher.close();
	}
}

