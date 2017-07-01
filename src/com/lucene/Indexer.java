package com.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class Indexer {

	private IndexWriter writer;
	AbstractSequenceClassifier<CoreLabel>  classifier;
	
	@SuppressWarnings("deprecation")
	public Indexer(String indexDirPath) throws IOException, ClassCastException, ClassNotFoundException {
		Directory indexDirectory = FSDirectory.open(new File(indexDirPath));
		
		writer = new IndexWriter(indexDirectory, 
		         new KeywordAnalyzer(),true, 
		         IndexWriter.MaxFieldLength.UNLIMITED);
		
		classifier = CRFClassifier.getClassifier("classifiers/english.all.3class.distsim.crf.ser.gz");

	}
	
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

   private Document getDocument(File file) throws IOException {
	   
	  XmlContents contents = new XmlContents();
	  contents.readFileContents(file);
	  
      Document document = new Document();

      /*for(String segment: getNERSegments(contents.getFullText())){
    	  document.add(new Field(LuceneConstants.FULL_TEXT,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }

      for(String segment: getNERSegments(contents.getLeadPara())){
    	  document.add(new Field(LuceneConstants.LEAD_PARA,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }*/
      
      for(String segment: getNERSegments(contents.getHeadline())){
    	  document.add(new Field(LuceneConstants.HEADLINE,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }
      
      Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field pubDateField = new Field(LuceneConstants.PUB_DATE, contents.getPubDate(),Field.Store.YES,Field.Index.NOT_ANALYZED);

      document.add(fileNameField);
      document.add(filePathField);
      document.add(pubDateField);
      
      return document;
   }   	

   private void indexFile(File file) throws IOException, ClassCastException, ClassNotFoundException {
	  System.out.println("Indexing "+file.getCanonicalPath());
	  Document document = getDocument(file);
	  writer.addDocument(document);
   }   
   
   public int createIndex(String dataDirPath) throws ClassCastException, ClassNotFoundException, IOException {
		
		ArrayList<File> allFiles = new ArrayList<File>();
		listXMLFiles(dataDirPath,allFiles);
		for (File file : allFiles){
			indexFile(file);
		}
		return writer.numDocs();
	}
	
	public void listXMLFiles(String dataDirPath,ArrayList<File> allFiles){
		File[] files = new File(dataDirPath).listFiles();
		for (File file: files){
			if(file.isFile() && file.getName().toLowerCase().endsWith(".xml")){
				allFiles.add(file);
			}
			if(file.isDirectory()){
				listXMLFiles(dataDirPath+"/"+file.getName(),allFiles);
			}
		}
	}
	
	public ArrayList<String> getNERSegments(String text) {

	    String[] output = classifier.classifyToString(text, "tsv", false).split("\\n");
	    ArrayList<String> segments = new ArrayList<String>();
	    String token="";
	    for(String val: output){
	    	if(!val.isEmpty()){
	    		String[] tokens = val.split("\\t");
	    		if(tokens[1].contentEquals("O")){
	    			if(!token.isEmpty()){
	    				segments.add(token);
	    				token = "";
	    			}
    				segments.add(tokens[0]);
	    		} else if (token.isEmpty()){
	    			token = tokens[0];
	    		} else {
	    			token = token + "_" + tokens[0];
	    		}
	    	  }
	      }
	      
	    if(!token.isEmpty()){
	    	segments.add(token);
	    }
	      
	    return segments;
	}
}

