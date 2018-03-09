package com.whir.common.util.parse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class XmlDataParse implements IDataParse {

	public static final String encoding = "UTF-8";
	
	public List<List<Map<String,Object>>> parseXmlData(String data) throws Exception {
		List<List<Map<String,Object>>> listNum = new ArrayList<List<Map<String,Object>>>();
		SAXBuilder builder = new SAXBuilder();
	    byte[] b = data.getBytes(this.encoding);
	    InputStream is = new ByteArrayInputStream(b);
	    Document doc = builder.build(is);
	    Element root = doc.getRootElement();
	    List dataList = root.getChildren("data");
        if ((dataList != null) && (dataList.size() > 0))
          for (int i = 0; i < dataList.size(); i++) {
            Element dataElement = (Element)dataList.get(i);
            List methodList = dataElement.getChildren("list");
            if ((methodList != null) && (methodList.size() > 0))
              for (int j = 0; j < methodList.size(); j++) {
                Element methodElement = (Element)methodList.get(j);
                List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
                for(int m=0;m<methodElement.getContentSize();m++){
                	Map<String,Object> serviceMap = new HashMap<String,Object>();
                	String nodeName = methodElement.getContent(m).toString();
                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
                	serviceMap.put(keyName, methodElement.getContent(m).getValue());
                	list.add(serviceMap);
                }
                listNum.add(list);
              }
          }
        
		return listNum;
	}
	
	public List<Map<String,Object>> parseXmlDataToList(String data) throws Exception {
		List<Map<String,Object>> listNum = new ArrayList<Map<String,Object>>();
		SAXBuilder builder = new SAXBuilder();
	    byte[] b = data.getBytes(this.encoding);
	    InputStream is = new ByteArrayInputStream(b);
	    Document doc = builder.build(is);
	    Element root = doc.getRootElement();
	    List dataList = root.getChildren("data");
	    if ((dataList != null) && (dataList.size() > 0))
	        for (int i = 0; i < dataList.size(); i++) {
	          Element dataElement = (Element)dataList.get(i);
	          List methodList = dataElement.getChildren("list");
	          if ((methodList != null) && (methodList.size() > 0))
	            for (int j = 0; j < methodList.size(); j++) {
	              Element methodElement = (Element)methodList.get(j);
	              Map<String,Object> masterMap = new HashMap<String,Object>();
	              for(int m=0;m<methodElement.getContentSize();m++){
	              	String nodeName = methodElement.getContent(m).toString();
	              	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
	              	masterMap.put(keyName, methodElement.getContent(m).getValue());
	              }
	              listNum.add(masterMap);
	            }
	      }
        
		return listNum;
	}
	
	
	public String parseXmlRecord(String data) throws Exception {
		String recordNum = "0";
		SAXBuilder builder = new SAXBuilder();
	    byte[] b = data.getBytes(this.encoding);
	    InputStream is = new ByteArrayInputStream(b);
	    Document doc = builder.build(is);
	    Element root = doc.getRootElement();
	    List recordList = root.getChildren("record");
        if ((recordList != null) && (recordList.size() > 0))
          for (int i = 0; i < recordList.size(); i++) {
            Element recordElement = (Element)recordList.get(i);
            recordNum = recordElement.getValue();
          }
		return recordNum;
	}
	
	public List<Map<String, Object>> parseXmlData(File file) throws Exception {
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			SAXBuilder builder = new SAXBuilder();
			FileInputStream fis= new FileInputStream(file);
			Document doc = builder.build(fis);
			Element root = doc.getRootElement();
		    List dataList = root.getChildren("data");
		    if ((dataList != null) && (dataList.size() > 0))
		          for (int i = 0; i < dataList.size(); i++) {
		            Element dataElement = (Element)dataList.get(i);
		            for(int m=0;m<dataElement.getContentSize();m++){
	                	Map<String,Object> serviceMap = new HashMap<String,Object>();
	                	String nodeName = dataElement.getContent(m).toString();
	                	serviceMap.put(nodeName, dataElement.getContent(m).getValue());
	                	list.add(serviceMap);
		            }
		    }
		    return list;
	}
}
