package com.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Indexer {

	private IndexWriter writer;
	
	@SuppressWarnings("deprecation")
	public Indexer(String indexDirPath) throws IOException {
		Directory indexDirectory = FSDirectory.open(new File(indexDirPath));
		
		writer = new IndexWriter(indexDirectory, 
		         new StandardAnalyzer(Version.LUCENE_36),true, 
		         IndexWriter.MaxFieldLength.UNLIMITED);
	}
	
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

   private Document getDocument(File file) throws IOException {
	   
	  XmlContents contents = new XmlContents();
	  contents.readFileContents(file);
	  
      Document document = new Document();

      Field contentField = new Field(LuceneConstants.FULL_TEXT, contents.getFullText(),Field.Store.NO,Field.Index.ANALYZED);
      Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(),Field.Store.YES,Field.Index.NOT_ANALYZED);
      Field leadParaField = new Field(LuceneConstants.LEAD_PARA, contents.getLeadPara(),Field.Store.NO,Field.Index.ANALYZED);
      Field headLineField = new Field(LuceneConstants.HEADLINE, contents.getHeadline(),Field.Store.NO,Field.Index.ANALYZED);
      Field pubDateField = new Field(LuceneConstants.PUB_DATE, contents.getPubDate(),Field.Store.YES,Field.Index.NOT_ANALYZED);

      document.add(contentField);
      document.add(fileNameField);
      document.add(filePathField);
      document.add(leadParaField);
      document.add(headLineField);
      document.add(pubDateField);
      
      return document;
   }   	

   private void indexFile(File file) throws IOException {
	  System.out.println("Indexing "+file.getCanonicalPath());
	  Document document = getDocument(file);
	  writer.addDocument(document);
   }   
   
   public int createIndex(String dataDirPath) throws IOException{
		
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
}
