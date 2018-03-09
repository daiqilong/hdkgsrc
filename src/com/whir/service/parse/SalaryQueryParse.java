package com.whir.service.parse;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.ezoffice.customize.customercontent.actionsupport.CustormerContentAction;
import com.whir.service.common.AbstractParse;

public class SalaryQueryParse extends AbstractParse{
	public SalaryQueryParse(Document doc){
	    super(doc);
	}
	public String getSalaryQueryData() throws SQLException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        Element queryDateElement = rootElement.getChild("queryDate");
        String queryDate = queryDateElement.getValue();
        
	    CustormerContentAction custormerContentAction = new CustormerContentAction();
	    Map<String, Object> map = custormerContentAction.getSalaryQueryData(queryDate,userId);
	    if(!map.isEmpty()){
	    	Set<Entry<String, Object>> entrySet = map.entrySet();
	    	result += "<result>";
	    	for(Entry<String, Object> entry : entrySet){
	            result += "<"+entry.getKey()+"><![CDATA[" + entry.getValue() +"]]></"+entry.getKey()+">";
	    	}
	    	result += "</result>";
	    }
	    this.setMessage("1", message);
        return result;
	 }
}