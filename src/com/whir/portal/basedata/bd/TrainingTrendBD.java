package com.whir.portal.basedata.bd;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.xfire.client.Client;

import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;

public class TrainingTrendBD {
	public List<Map<String, Object>> getTrainingTrendList(){
		
//		Properties properties = PropertiesUtils.getProperties();
//		String wsdl_address = properties.getProperty("px_course_wsdl");
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String nowDate = format.format(new Date());
//		String res = null;
//		try {
//		      Client client = new Client(new URL(wsdl_address));
//		      String send = "<input><key>coursedetail</key><parameter><pageSize>100000</pageSize><currentPage>1</currentPage><trainName></trainName><address></address><trainCategory></trainCategory><reportDayStart>"+nowDate+"</reportDayStart><reportDayEnd></reportDayEnd></parameter></input>";
//		      Object[] results = client.invoke("service", new Object[] { send });
//		      res = (String)results[0];
//		      XmlDataParse xmlDataParse = new XmlDataParse();
//		      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
//		      Collections.reverse(list);
//		      JacksonUtil util = new JacksonUtil();
//			  list2 = util.createResultList(new String[] { "trainName", "address","reportDay","trackDay","stateIdentify"}, list);
//			  for(Iterator it = list2.iterator(); it.hasNext();){
//				  Map<String,Object> map = (Map<String,Object>)it.next();
//				  if(!map.get("stateIdentify").toString().equals("已批复")){
//					  it.remove();
//				  } else {
//					 boolean result = compareDate(map.get("trackDay").toString()); 
//					 if(result){
//						 it.remove(); 
//					 }
//				  }
//			  }
//		    }
//		    catch (Exception e) {
//		      e.printStackTrace();
//		      res = e.getMessage();
//		    }
		return list2;
	}
	
	public Boolean compareDate(String trackDay){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt2 = df.parse(trackDay);
			Date nowDate = new Date();
			if(nowDate.getTime() > dt2.getTime()){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
}
