package com.whir.service.parse;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.evo.weixin.bd.KgNewBD;
import com.whir.service.common.AbstractParse;

public class KgNewParse extends AbstractParse{
	
	public KgNewParse(Document doc){
	    super(doc);
	}
	public String getKgNewRecordCount() throws SQLException, HibernateException, ParseException, IOException
	  {
			System.out.println("获取getKgNewRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element queryTitleElement = rootElement.getChild("queryTitle");
		    String queryTitle = queryTitleElement.getValue();
		    Element channelIdElement = rootElement.getChild("channelId");
		    String channelId = channelIdElement.getValue();
		    KgNewBD kgNewBD = new KgNewBD();
		    System.out.println("获取queryTitle::"+queryTitle);
		    int recordCount = kgNewBD.getKgNewRecordCount(queryTitle,channelId);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	/**
	 * 获取空管新聞数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getKgNewInfoList() throws SQLException, NumberFormatException, HibernateException {
		System.out.println("获取getKgNewInfoList::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element curPageElement = rootElement.getChild("curPage");
	    String curPage = curPageElement.getValue();
	    System.out.println("获取curPage::"+curPage);
	    Element queryTitleElement = rootElement.getChild("queryTitle");
	    String queryTitle = queryTitleElement.getValue();
	    System.out.println("获取queryTitle::"+queryTitle);
	    Element channelIdElement = rootElement.getChild("channelId");
	    String channelId = channelIdElement.getValue();
	    KgNewBD kgNewBD = new KgNewBD();
	    List<Map<String,Object>> list = kgNewBD.getKgNewInfoList(queryTitle,channelId,Integer.parseInt(curPage),5);
		    
	    if (list != null && list.size() > 0) {
	        for (int i = 0; i < list.size(); i++) {
	        	Map<String,Object> map = list.get(i);
	            result += "<result>";
	            result += "<informationId><![CDATA[" + map.get("informationId") +"]]></informationId>";
	            result += "<channelId><![CDATA[" + map.get("channelId") + "]]></channelId>";
	            result += "<informationTitle><![CDATA[" + map.get("informationTitle") + "]]></informationTitle>";
	            result += "<informationIssueTime><![CDATA[" + map.get("informationIssueTime") + "]]></informationIssueTime>";
	            result += "<informationType><![CDATA[" + map.get("informationType") +"]]></informationType>";
	            result += "</result>";
	        }
	    }
	    this.setMessage("1", message);
	    return result;
	 }
	
	/**
	 * 获取空管新聞图片数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getPhotoImage() throws SQLException, NumberFormatException, HibernateException {
		System.out.println("获取getPhotoImage::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element channelIdElement = rootElement.getChild("channelId");
	    String channelId = channelIdElement.getValue();
	    System.out.println("channelId::"+channelId);
	    KgNewBD kgNewBD = new KgNewBD();
	    List<Map<String,Object>> list = kgNewBD.getPhotoImage(channelId);
	    System.out.println("list::"+list.size()); 
	    if (list != null && list.size() > 0) {
	        for (int i = 0; i < list.size(); i++) {
	        	Map<String,Object> map = list.get(i);
	            result += "<result>";
	            result += "<informationId><![CDATA[" + map.get("informationId") +"]]></informationId>";
	            result += "<channelId><![CDATA[" + map.get("channelId") + "]]></channelId>";
	            result += "<informationTitle><![CDATA[" + map.get("informationTitle") + "]]></informationTitle>";
	            result += "<accessorySaveName><![CDATA[" + map.get("accessorySaveName") + "]]></accessorySaveName>";
	            result += "<filePath><![CDATA[" + map.get("filePath") + "]]></filePath>";
	            result += "<informationType><![CDATA[" + map.get("informationType") +"]]></informationType>";
	            result += "</result>";
	        }
	    }
	    this.setMessage("1", message);
	    return result;
	 }
}