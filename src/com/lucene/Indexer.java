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

      ArrayList<String> all = new ArrayList<String>();
      ArrayList<String> persons = new ArrayList<String>();
      ArrayList<String> orgs = new ArrayList<String>();
      ArrayList<String> locs = new ArrayList<String>();
      
      getNERSegments(contents.getFullText(),all,persons,orgs,locs);
      for(String segment: all){
    	  document.add(new Field(LuceneConstants.FULL_TEXT,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }

      all = new ArrayList<String>();
      getNERSegments(contents.getLeadPara(),all,persons,orgs,locs);
      for(String segment: all){
    	  document.add(new Field(LuceneConstants.LEAD_PARA,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }
      
      all = new ArrayList<String>();
      getNERSegments(contents.getHeadline1(),all,persons,orgs,locs);
      for(String segment: all){
    	  document.add(new Field(LuceneConstants.HEADLINE1,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }
      
      all = new ArrayList<String>();
      getNERSegments(contents.getHeadline2(),all,persons,orgs,locs);
      for(String segment: all){
    	  document.add(new Field(LuceneConstants.HEADLINE2,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: persons){
    	  document.add(new Field(LuceneConstants.NE_PERSON,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: orgs){
    	  document.add(new Field(LuceneConstants.NE_ORG,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: locs){
    	  document.add(new Field(LuceneConstants.NE_LOCATION,segment,Field.Store.NO,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: contents.getLocation()){
    	  document.add(new Field(LuceneConstants.LOCATION,segment,Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: contents.getPerson()){
    	  document.add(new Field(LuceneConstants.PERSON,segment,Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: contents.getOrg()){
    	  document.add(new Field(LuceneConstants.ORG,segment,Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: contents.getClassifier_indexing_service()){
    	  document.add(new Field(LuceneConstants.CLASSIFIER_IDX,segment,Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: contents.getClassifier_online_producer_general()){
    	  document.add(new Field(LuceneConstants.CLASSIFIER_ON_GEN,segment,Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: contents.getClassifier_online_producer_material()){
    	  document.add(new Field(LuceneConstants.CLASSIFIER_ON_MAT,segment,Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      for(String segment: contents.getClassifier_online_producer_taxonomic()){
    	  document.add(new Field(LuceneConstants.CLASSIFIER_ON_TAX,segment,Field.Store.YES,Field.Index.NOT_ANALYZED));
      }
      
      Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field pubDateField = new Field(LuceneConstants.PUB_DATE, contents.getPubDate(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field dskField = new Field(LuceneConstants.DSK, contents.getDsk(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field pubYearField  = new Field(LuceneConstants.PUB_YEAR,contents.getPubYear(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field pubMonthField = new Field(LuceneConstants.PUB_MONTH,contents.getPubMonth(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field pubDayField = new Field(LuceneConstants.PUB_DAY,contents.getPubDay(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field pubDayOfWeekField = new Field(LuceneConstants.PUB_DAY_OF_WEEK,contents.getPubDayOfWeek(),Field.Store.YES,Field.Index.NOT_ANALYZED);;
      Field pageNumField = new Field(LuceneConstants.PAGE_NUM,contents.getPageNum(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field sectionField = new Field(LuceneConstants.SECTION,contents.getSection(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field columnField = new Field(LuceneConstants.COLUMN,contents.getColumn(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field onlineSectionField = new Field(LuceneConstants.ONLINE_SECTION,contents.getOnlineSection(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      
      document.add(fileNameField);
      document.add(filePathField);
      document.add(pubDateField);
      document.add(dskField);
      document.add(pubYearField);
      document.add(pubMonthField);
      document.add(pubDayField);
      document.add(pubDayOfWeekField);
      document.add(pageNumField);
      document.add(sectionField);
      document.add(columnField);
      document.add(onlineSectionField);
      
      return document;
   }   	

   private void indexFile(File file) throws IOException, ClassCastException, ClassNotFoundException {
	  //System.out.println("Indexing "+file.getCanonicalPath());
	  Document document = getDocument(file);
	  writer.addDocument(document);
   }   
   
   public int createIndex(String dataDirPath) throws ClassCastException, ClassNotFoundException, IOException, InterruptedException {
		
		ArrayList<File> allFiles = new ArrayList<File>();
		listXMLFiles(dataDirPath,allFiles);
		int numFiles = allFiles.size();
		int filesPerThread = numFiles/20;
		ArrayList<Thread> allThreads = new ArrayList<Thread>();
		for (int threadNum = 0; threadNum < 20; threadNum++){
			final int innerThreadNum = threadNum;
			Thread thread = new Thread(){
				public void run(){
					int startIdx = innerThreadNum*filesPerThread;
					int endIdx = innerThreadNum==19?numFiles:(innerThreadNum+1)*filesPerThread;
					for(int fileNum=startIdx;fileNum<endIdx;fileNum++){
						try {
							indexFile(allFiles.get(fileNum));
						} catch (ClassCastException | ClassNotFoundException | IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			allThreads.add(thread);
			thread.start();
		}
		
		for(Thread thread: allThreads){
			thread.join();
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
	
    public void getNERSegments(String text, ArrayList<String> all, ArrayList<String> persons, ArrayList<String> orgs, ArrayList<String> locs) {

	    String[] output = classifier.classifyToString(text, "tsv", false).split("\\n");
	    String token="",tag="";
	    for(String val: output){
	    	if(!val.isEmpty()){
	    		String[] tokens = val.split("\\t");
	    		if(tokens[1].contentEquals("O")){
	    			if(!token.isEmpty()) {
	    				if(tag.contentEquals("PERSON")){
	    					persons.add(token.toLowerCase());
	    				}
	    				else if(tag.contentEquals("ORGANIZATION")){
	    					orgs.add(token.toLowerCase());
	    				}
	    				else if(tag.contentEquals("LOCATION")){
	    					locs.add(token.toLowerCase());
	    				}
	    				all.add(token.toLowerCase());
	    				token = "";
	    				tag = "";
	    			}
    				all.add(tokens[0].toLowerCase());
	    		} else if (token.isEmpty()){
	    			token = tokens[0];
	    			tag = tokens[1];
	    		} else {
	    			token = token + "_" + tokens[0];
	    		}
	    	  }
	      }
	      
	    if(!token.isEmpty()){
	    	all.add(token.toLowerCase());
	    	if(tag.contentEquals("PERSON")){
				persons.add(token.toLowerCase());
			}
			else if(tag.contentEquals("ORGANIZATION")){
				orgs.add(token.toLowerCase());
			}
			else if(tag.contentEquals("LOCATION")){
				locs.add(token.toLowerCase());
			}
	    }
	      
	}
}

