package com.lucene;

import java.io.File;
import java.util.ArrayList;

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
	private String dsk;
	private ArrayList<String> org;
	private ArrayList<String> person;
	private ArrayList<String> location;
	private ArrayList<String> classifier_online_producer_general;
	private ArrayList<String> classifier_online_producer_taxonomic;
	private ArrayList<String> classifier_online_producer_material;
	private ArrayList<String> classifier_indexing_service;
	
	public XmlContents() {
		this.leadPara="";
		this.fullText="";
		this.pubDate="";
		this.headline="";
		this.dsk ="";
		this.org = new ArrayList<String>();
		this.person = new ArrayList<String>();
		this.location = new ArrayList<String>();
		this.classifier_indexing_service = new ArrayList<String>();
		this.classifier_online_producer_general = new ArrayList<String>();
		this.classifier_online_producer_taxonomic = new ArrayList<String>();
		this.classifier_online_producer_material = new ArrayList<String>();
	}
	
	public void readFileContents(File file){
		
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
					else if(eElement.getAttribute("name").contentEquals("dsk")){
						this.dsk = eElement.getAttribute("content");
					}
				}
			}
			this.pubDate = year + "-" + month + "-" + day;
			
			NodeList classifierList = doc.getElementsByTagName("classifier");
			for (int temp = 0; temp < classifierList.getLength(); temp++) {
				Node classifierNode = classifierList.item(temp);
				if (classifierNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) classifierNode;
					if(eElement.getAttribute("class").contentEquals("indexing_service")){
						this.classifier_indexing_service.add(classifierNode.getTextContent());
					}
					else if(eElement.getAttribute("class").contentEquals("online_producer")){
						if(eElement.getAttribute("type").contentEquals("types_of_material")){
							this.classifier_online_producer_material.add(classifierNode.getTextContent());
						}
						else if(eElement.getAttribute("type").contentEquals("taxonomic_classifier")){
							this.classifier_online_producer_taxonomic.add(classifierNode.getTextContent());
						}
						else if(eElement.getAttribute("type").contentEquals("general_descriptor")){
							this.classifier_online_producer_general.add(classifierNode.getTextContent());
						}
					}
				}
			}
					
			this.headline = doc.getElementsByTagName("title").item(0).getTextContent();
			
			NodeList tempList = doc.getElementsByTagName("org");
			for (int temp = 0; temp < tempList.getLength(); temp++)				
				this.org.add(tempList.item(temp).getTextContent());
				
			tempList = doc.getElementsByTagName("person");
			for (int temp = 0; temp < tempList.getLength(); temp++)				
				this.person.add(tempList.item(temp).getTextContent());
			
			tempList = doc.getElementsByTagName("location");
			for (int temp = 0; temp < tempList.getLength(); temp++)				
				this.location.add(tempList.item(temp).getTextContent());
			
			
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
	
	public String getDsk() {
		return dsk;
	}

	public void setDsk(String dsk) {
		this.dsk = dsk;
	}

	public ArrayList<String> getOrg() {
		return org;
	}

	public void setOrg(ArrayList<String> org) {
		this.org = org;
	}

	public ArrayList<String> getPerson() {
		return person;
	}

	public void setPerson(ArrayList<String> person) {
		this.person = person;
	}

	public ArrayList<String> getLocation() {
		return location;
	}

	public void setLocation(ArrayList<String> location) {
		this.location = location;
	}

	public ArrayList<String> getClassifier_online_producer_general() {
		return classifier_online_producer_general;
	}

	public void setClassifier_online_producer_general(ArrayList<String> classifier_online_producer_general) {
		this.classifier_online_producer_general = classifier_online_producer_general;
	}

	public ArrayList<String> getClassifier_online_producer_taxonomic() {
		return classifier_online_producer_taxonomic;
	}

	public void setClassifier_online_producer_taxonomic(ArrayList<String> classifier_online_producer_taxonomic) {
		this.classifier_online_producer_taxonomic = classifier_online_producer_taxonomic;
	}

	public ArrayList<String> getClassifier_online_producer_material() {
		return classifier_online_producer_material;
	}

	public void setClassifier_online_producer_material(ArrayList<String> classifier_online_producer_material) {
		this.classifier_online_producer_material = classifier_online_producer_material;
	}

	public ArrayList<String> getClassifier_indexing_service() {
		return classifier_indexing_service;
	}

	public void setClassifier_indexing_service(ArrayList<String> classifier_indexing_service) {
		this.classifier_indexing_service = classifier_indexing_service;
	}
}

