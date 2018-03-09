package com.whir.service.parse;

import java.sql.SQLException;
import java.util.Map;

import org.jdom.Document;

import com.whir.portal.basedata.bd.WainingInformationBD;
import com.whir.service.common.AbstractParse;

public class EarlyWaringInfoParse extends AbstractParse{
	public EarlyWaringInfoParse(Document doc){
	    super(doc);
	}
	public String getEarlyWaringInfoData() throws SQLException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    WainingInformationBD wainingInformationBD = new WainingInformationBD();
	    Map<String,Object> mdrsMap = wainingInformationBD.getWainingInforList();
	    if(mdrsMap!=null && !mdrsMap.isEmpty()){
	    	result += "<result>";
	        result += "<datetime><![CDATA[" + mdrsMap.get("datetime").toString() +"]]></datetime>";
	        result += "<content><![CDATA[" + mdrsMap.get("content").toString() + "]]></content>";
	        result += "<colour><![CDATA[" + mdrsMap.get("colour").toString() +"]]></colour>";
	        result += "<preenddate><![CDATA[" + mdrsMap.get("preenddate").toString() +"]]></preenddate>";
	        result += "</result>";
	    }
	    this.setMessage("1", message);
        return result;
	 }
}