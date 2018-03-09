package com.whir.service.parse;

import java.util.List;
import java.util.Map;

import org.jdom.Document;

import com.whir.portal.basedata.bd.WeatherForecastBD;
import com.whir.service.common.AbstractParse;

public class WeatherForecastParse extends AbstractParse{
	public WeatherForecastParse(Document doc){
	    super(doc);
	}
	public String getWeatherInfoData()
	  {
		String result = "";
		int code = 0;
	    String message = "数据传输成功。";
	    WeatherForecastBD weatherForecastBD = new WeatherForecastBD();
	    List<Map<String,Object>> list = weatherForecastBD.getWeiXingInfo();
	    System.out.println("list.size():::::"+list.size());
	    if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
            	Map<String,Object> map = list.get(i);
                result += "<result>";
                result += "<address><![CDATA[" + map.get("0").toString() +"]]></address>";
                result += "<wind><![CDATA[" + map.get("2").toString() + "]]></wind>";
                result += "<temperature><![CDATA[" + map.get("3").toString() +"]]></temperature>";
                result += "<visibility><![CDATA[" + map.get("4").toString() +"]]></visibility>";
                result += "</result>";
            }
        }
	    this.setMessage("1", "正常");
        return result;
	 }
}