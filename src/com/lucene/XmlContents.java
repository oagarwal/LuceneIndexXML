package com.lucene;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlContents {
	
	private String leadPara;
	private String fullText;
	private String headline;
	private String pubDate;
	
	public void readFileContents(File file){
		
		this.leadPara="";
		this.fullText="";
		this.pubDate="";
		this.headline="";
		
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			
			NodeList blockList = doc.getElementsByTagName("block");
			for (int temp = 0; temp < blockList.getLength(); temp++) {
				
				Node blockNode = blockList.item(temp);
				if (blockNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) blockNode;
					if(eElement.getAttribute("class").contentEquals("lead_paragraph")){
						NodeList textList = eElement.getElementsByTagName("p");
						for(int i=0;i< textList.getLength();i++){
							this.leadPara += textList.item(i).getTextContent();
						}
					}
					else if(eElement.getAttribute("class").contentEquals("full_text")){
						NodeList textList = eElement.getElementsByTagName("p");
						for(int i=0;i< textList.getLength();i++){
							this.fullText += textList.item(i).getTextContent();
						}
					}
				}
			}
			
			String month="",day="",year="";
			NodeList metaList = doc.getElementsByTagName("meta");
			for (int temp = 0; temp < metaList.getLength(); temp++) {
				Node metaNode = metaList.item(temp);
				if (metaNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) metaNode;
					if(eElement.getAttribute("name").contentEquals("publication_year")){
						year = eElement.getAttribute("content");
					}
					else if(eElement.getAttribute("name").contentEquals("publication_month")){
						month = eElement.getAttribute("content");
					}
					else if(eElement.getAttribute("name").contentEquals("publication_day_of_month")){
						day = eElement.getAttribute("content");
					}
				}
			}
			this.pubDate = year + "-" + month + "-" + day;
			
			this.headline = doc.getElementsByTagName("hedline").item(0).getTextContent();
			
			
	    } catch (Exception e) {
	       e.printStackTrace();
	    }
	}
	
	public String getLeadPara() {
		return leadPara;
	}
	public void setLeadPara(String leadPara) {
		this.leadPara = leadPara;
	}
	public String getFullText() {
		return fullText;
	}
	public void setFullText(String fullText) {
		this.fullText = fullText;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String year) {
		this.pubDate = year;
	}
}
