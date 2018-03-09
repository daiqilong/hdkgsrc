package com.whir.service.parse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.portal.module.actionsupport.CDMMoudleAction;
import com.whir.service.common.AbstractParse;

public class FlightLineSearchParse extends AbstractParse{
	public FlightLineSearchParse(Document doc){
	    super(doc);
	}
	public String getFlightLineSearchRecordCount() throws SQLException
	  {
			System.out.println("获取getFlightLineSearchRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    
		    CDMMoudleAction cDMMoudleAction = new CDMMoudleAction();
		    int recordCount = cDMMoudleAction.getFlightLineSearchRecordCount();
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	/**
	 * 获取CDM数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public String getFlightLineSearchData() throws SQLException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element queryDateElement = rootElement.getChild("queryDate");
	    String queryDate = queryDateElement.getValue();
	    Element curPageElement = rootElement.getChild("curPage");
	    String curPage = curPageElement.getValue();
	    Element flnoElement = rootElement.getChild("flno");
	    String flno = flnoElement.getValue();
      
	    CDMMoudleAction cDMMoudleAction = new CDMMoudleAction();
	    List<Map<String,String>> list = cDMMoudleAction.getFlightLineSearchData(flno,queryDate,curPage);
	    System.out.println("list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
        	  Map<String,String> map = list.get(i);
              result += "<result>";
              result += "<flno><![CDATA[" + map.get("flno") +"]]></flno>";
              result += "<adep><![CDATA[" + map.get("adep") + "]]></adep>";
              result += "<ades><![CDATA[" + map.get("ades") + "]]></ades>";
              result += "<stod><![CDATA[" + map.get("stod") +"]]></stod>";
              result += "<stoa><![CDATA[" + map.get("stoa") +"]]></stoa>";
              result += "<rwy><![CDATA[" + map.get("rwy") +"]]></rwy>";
              result += "<pbay><![CDATA[" + map.get("pbay") +"]]></pbay>";
              result += "<act><![CDATA[" + map.get("act") +"]]></act>";
              result += "<atot><![CDATA[" + map.get("atot") +"]]></atot>";
              result += "<aldt><![CDATA[" + map.get("aldt") +"]]></aldt>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
}