package com.whir.service.parse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.portal.basedata.bd.NoticeAnnouncementBD;
import com.whir.service.common.AbstractParse;

public class NotificationParse extends AbstractParse{
	
	public static NoticeAnnouncementBD noticeAnnouncementBD = new NoticeAnnouncementBD();
	public NotificationParse(Document doc){
	    super(doc);
	}
	public String getOaNotificationRecordCount() throws SQLException, HibernateException
	  {
			System.out.println("获取getOaNotificationRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userIdElement = rootElement.getChild("userId");
		    String userId = userIdElement.getValue();
		    NoticeAnnouncementBD noticeAnnouncementBD = new NoticeAnnouncementBD();
		    int recordCount = noticeAnnouncementBD.oaPageCount(userId);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	public String getAgNotificationRecordCount() throws SQLException, HibernateException
	  {
			System.out.println("获取getAgNotificationRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userIdElement = rootElement.getChild("userId");
		    String userId = userIdElement.getValue();
		    int recordCount = noticeAnnouncementBD.anguanPageCount(userId);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	
	public String getPxNotificationRecordCount() throws SQLException, HibernateException
	  {
			System.out.println("获取getPxNotificationRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userIdElement = rootElement.getChild("userId");
		    String userId = userIdElement.getValue();
		    int recordCount = noticeAnnouncementBD.peixunPageCount(userId);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	
	public String getYwNotificationRecordCount() throws SQLException, HibernateException
	  {
			System.out.println("获取getYwNotificationRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userIdElement = rootElement.getChild("userId");
		    String userId = userIdElement.getValue();
		    int recordCount = noticeAnnouncementBD.yunweiPageCount(userId);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	
	/**
	 * 获取OA通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getOaNotificationData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
    Element curPageElement = rootElement.getChild("curPage");
    String curPage = curPageElement.getValue();
    Element userIdElement = rootElement.getChild("userId");
    String userId = userIdElement.getValue();
    
	    List<Map<String,Object>> list = noticeAnnouncementBD.oaListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("OA通知数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
        for (int i = 0; i < list.size(); i++) {
        	Map<String,Object> map = list.get(i);
            result += "<result>";
            result += "<title><![CDATA[" + map.get("title") +"]]></title>";
            result += "<publishTime><![CDATA[" + map.get("publishTime") + "]]></publishTime>";
            result += "<status><![CDATA[" + map.get("status") + "]]></status>";
            result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
            result += "</result>";
        }
    }
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	/**
	 * 获取安管通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getAgNotificationData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
    Element curPageElement = rootElement.getChild("curPage");
    String curPage = curPageElement.getValue();
    Element userIdElement = rootElement.getChild("userId");
    String userId = userIdElement.getValue();
    
	    List<Map<String,Object>> list = noticeAnnouncementBD.anguanListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("安管通知数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
        for (int i = 0; i < list.size(); i++) {
        	Map<String,Object> map = list.get(i);
            result += "<result>";
            result += "<title><![CDATA[" + map.get("title") +"]]></title>";
            result += "<noticeType><![CDATA[" + map.get("noticeType") + "]]></noticeType>";
            result += "<publishDate><![CDATA[" + map.get("publishDate") + "]]></publishDate>";
            result += "<publisher><![CDATA[" + map.get("publisher") + "]]></publisher>";
            result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
            result += "</result>";
        }
    }
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	/**
	 * 获取培训通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getPxNotificationData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
    Element curPageElement = rootElement.getChild("curPage");
    String curPage = curPageElement.getValue();
    Element userIdElement = rootElement.getChild("userId");
    String userId = userIdElement.getValue();
    
	    List<Map<String,Object>> list = noticeAnnouncementBD.peixunListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("培训通知数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
        for (int i = 0; i < list.size(); i++) {
        	Map<String,Object> map = list.get(i);
            result += "<result>";
            result += "<title><![CDATA[" + map.get("title") +"]]></title>";
            result += "<receiver><![CDATA[" + map.get("receiver") + "]]></receiver>";
            result += "<startDate><![CDATA[" + map.get("startDate") + "]]></startDate>";
            result += "<endDate><![CDATA[" + map.get("endDate") + "]]></endDate>";
            result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
            result += "</result>";
        }
    }
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	/**
	 * 获取运维通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getYwNotificationData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
    Element curPageElement = rootElement.getChild("curPage");
    String curPage = curPageElement.getValue();
    Element userIdElement = rootElement.getChild("userId");
    String userId = userIdElement.getValue();
    
	    List<Map<String,Object>> list = noticeAnnouncementBD.yunweiListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("运维通知数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
        for (int i = 0; i < list.size(); i++) {
        	Map<String,Object> map = list.get(i);
            result += "<result>";
            result += "<informName><![CDATA[" + map.get("informName") +"]]></informName>";
            result += "<publisher><![CDATA[" + map.get("publisher") + "]]></publisher>";
            result += "<release><![CDATA[" + map.get("release") + "]]></release>";
            result += "<recipient><![CDATA[" + map.get("recipient") + "]]></recipient>";
            result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
            result += "</result>";
        }
    }
	    this.setMessage("1", message);
	    return result;
	 }
}