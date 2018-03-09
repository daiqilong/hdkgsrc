package com.whir.portal.module.actionsupport;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.AxisLabel;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Pie;
import com.github.abel533.echarts.style.ItemStyle;
import com.whir.common.util.DataSourceBase;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.PropertiesUtils;

public class StatisticsMapAction extends BaseActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3930568095233307445L;
	/*
	 * 干扰统计
	 */
	public String disturbPage() {
	    return "disturbPage";
	}
	/*
	 * 停机统计
	 */
	public String haltPage() {
	    return "haltPage";
	}
	/*
	 * 故障统计
	 */
	public String faultPage() {
	    return "faultPage";
	}
	/**
	 * 干扰统计图数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public void disturbMapData() {
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String statisDirection = request.getParameter("statisDirection");
  	    
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("jam");
		Element parameter = output.addElement("parameter");
		
		Element disturbStartDateElement = parameter.addElement("disturbStartDate");
		if(startDate==null || startDate.equals("")){
			disturbStartDateElement.setText("");
		}else{
			disturbStartDateElement.setText(startDate);
		}
		Element disturbEndDateElement = parameter.addElement("disturbEndDate");
		if(endDate==null || endDate.equals("")){
			disturbEndDateElement.setText("");
		}else{
			disturbEndDateElement.setText(endDate);
		}
		
		Element registerUnitElement = parameter.addElement("registerUnit");
		registerUnitElement.setText("");
		
		Element registerDepartmentElement = parameter.addElement("registerDepartment");
		registerDepartmentElement.setText("");
		
		Element frequencyElement = parameter.addElement("frequency");
		frequencyElement.setText("");
		
		Element disturbTypeElement = parameter.addElement("disturbType");
		disturbTypeElement.setText("");
		
		Element disturbCharcaterElement = parameter.addElement("disturbCharcater");
		disturbCharcaterElement.setText("");
		
		Element disturbInfuenceElement = parameter.addElement("disturbInfuence");
		disturbInfuenceElement.setText("");
		
		Element disturbPurposeElement = parameter.addElement("disturbPurpose");
		disturbPurposeElement.setText("");
		
		Element coupleUnitElement = parameter.addElement("coupleUnit");
		coupleUnitElement.setText("");
		
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(1000000));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(1));
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		List<String> listFrequency = new ArrayList<String>();
		String res = null;
		try {
			  Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      list = xmlDataParse.parseXmlDataToList(res);
		      }
		      int data1=0;
		      int data2=0;
		      int data3=0;
		      int data4=0;
		      int data5=0;
		      int data6=0;
		      int data7=0;
		      int data8=0;
		      int data9=0;
		      Object[] valueArr = null;
		      Object[] keyArr = null;
		      for(Map<String,Object> map :list){
		    	  if("1".equals(statisDirection) && null!=map.get("frequency") && !"".equals(map.get("frequency")) && !listFrequency.contains(map.get("frequency").toString())){
		    		  listFrequency.add(map.get("frequency").toString());
		    	  }
		    	  if("2".equals(statisDirection) && null!=map.get("disturbCharcater") && !"".equals(map.get("disturbCharcater"))){   
			    	  if("话音".equals(map.get("disturbCharcater").toString())){
			    		  data1++;
			    	  }
			    	  if("广播".equals(map.get("disturbCharcater").toString())){
			    		  data2++;
			    	  }
			    	  if("长划".equals(map.get("disturbCharcater").toString())){
			    		  data3++;
			    	  }
			    	  if("噪音".equals(map.get("disturbCharcater").toString())){
			    		  data4++;
			    	  }
			    	  if("电流声".equals(map.get("disturbCharcater").toString())){
			    		  data5++;
			    	  }
			    	  if("其它".equals(map.get("disturbCharcater").toString())){
			    		  data6++;
			    	  }
		    	  }
		    	  if("3".equals(statisDirection) && null!=map.get("disturbType") && !"".equals(map.get("disturbType"))){   
			    	  if("地面".equals(map.get("disturbType").toString())){
			    		  data7++;
			    	  }
			    	  if("空中".equals(map.get("disturbType").toString())){
			    		  data8++;
			    	  }
			    	  if("地面与空中".equals(map.get("disturbType").toString())){
			    		  data9++;
			    	  }
		    	  }
		      }
		      valueArr = new Object[listFrequency.size()]; 
		      keyArr = new Object[listFrequency.size()];
		      for(int i=0;i<listFrequency.size();i++){
		    	  keyArr[i]=listFrequency.get(i);
		    	  int count=0;
		    	  for(Map<String,Object> map :list){
		    		  if("1".equals(statisDirection) && listFrequency.get(i).toString().equals(map.get("frequency").toString())){
		    			  count++;
			    	  }
		    	  }
		    	  valueArr[i]=count;
		      }
			Option option = new GsonOption();  
			option.title().text("干扰统计图").setX("bottom");
	        option.tooltip().trigger(Trigger.axis);
	        AxisLabel axisLabel = new AxisLabel();
	        axisLabel.setInterval(0);
	        axisLabel.setRotate(60);
	        if("1".equals(statisDirection)){
	        	option.legend("频率");
	        	option.legend("");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

//		        Bar bar = new Bar("频率");
		        Bar bar = new Bar("");
		        
		        bar.data(valueArr);
	        	option.xAxis(new CategoryAxis().data(keyArr).axisLabel(axisLabel));
		        option.yAxis(new ValueAxis());
		        option.color("#2aa8ed");
		        option.series(bar);
	        } else  if("2".equals(statisDirection)){
//	        	option.legend("干扰特征");
	        	option.legend("");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

//		        Bar bar = new Bar("干扰特征");
		        Bar bar = new Bar("");
		        bar.data(data1, data2, data3, data4, data5, data6);
//		        bar.markPoint().data(new PointData().type(MarkType.max).name("最大值"), new PointData().type(MarkType.min).name("最小值"));
//		        bar.markLine().data(new PointData().type(MarkType.average).name("平均值"));
	        	option.xAxis(new CategoryAxis().data("话音", "广播", "长划", "噪音", "电流声", "其它").axisLabel(axisLabel));
		        option.yAxis(new ValueAxis());
		        option.color("#2aa8ed");
		        option.series(bar);
	        } else {
	        	option.legend("干扰类型");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

		        Bar bar = new Bar("干扰类型");
		        bar.data(data7, data8, data9);
//		        bar.markPoint().data(new PointData().type(MarkType.max).name("最大值"), new PointData().type(MarkType.min).name("最小值"));
//		        bar.markLine().data(new PointData().type(MarkType.average).name("平均值"));
	        	option.xAxis(new CategoryAxis().data("地面", "空中", "地面与空中").axisLabel(axisLabel));
		        option.yAxis(new ValueAxis());
		        option.color("#2aa8ed");
		        option.series(bar);
	        }
	        //类型转换
	        JSONObject jsonObj = JSONObject.fromObject(option.toString());
	        response.setContentType("application/json;charset=UTF-8");  
	        response.setHeader("Cache-Control", "no-cache"); 
	        //输出到页面
	        response.getWriter().print(jsonObj); 
		} catch (Exception e) {  
			e.printStackTrace();
	    } 
	}
	
	/**
	 * 停机统计图数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public void haltMapData() {
		String startDate1 = request.getParameter("startDate1");
		String endDate1 = request.getParameter("endDate1");
		String statisDirection1 = request.getParameter("statisDirection1");
  	    
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("stop");
		Element parameter = output.addElement("parameter");
		
		Element applyDateStartElement = parameter.addElement("applyDateStart");
		applyDateStartElement.setText("");
		Element applyDateEndElement = parameter.addElement("applyDateEnd");
		applyDateEndElement.setText("");
		
		Element applicatElement = parameter.addElement("applicat");
		applicatElement.setText("");
		
		Element haltStartTimeElement = parameter.addElement("haltStartTime");
		if(startDate1==null || startDate1.equals("")){
			haltStartTimeElement.setText("");
		}else{
			haltStartTimeElement.setText(startDate1);
		}
		Element haltEndTimeElement = parameter.addElement("haltEndTime");
		if(endDate1==null || endDate1.equals("")){
			haltEndTimeElement.setText("");
		}else{
			haltEndTimeElement.setText(endDate1);
		}
		Element haltReasonElement = parameter.addElement("haltReason");
		haltReasonElement.setText("");
		
		Element applyUnitElement = parameter.addElement("applyUnit");
		applyUnitElement.setText("");
		
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		applyDepartmentElement.setText("");
		
		Element systemNameElement = parameter.addElement("systemName");
		systemNameElement.setText("");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(1000000));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(1));
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String res = null;
		try {
			  Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      list = xmlDataParse.parseXmlDataToList(res);
		      }
		      int data1=0;int data2=0;int data3=0;
		      int data4=0;int data5=0;int data6=0;int data7=0;int data8=0;
		      int data9=0;int data10=0;int data11=0;int data12=0;int data13=0;
		      int data14=0;int data15=0;int data16=0;int data17=0;int data18=0;
		      int data19=0;int data20=0;int data21=0;int data22=0;int data23=0;
		      int data24=0;int data25=0;int data26=0;int data27=0;int data28=0;
		      int data29=0;int data30=0;int data31=0;int data32=0;int data33=0;
		      int data34=0;int data35=0;int data36=0;int data37=0;int data38=0;
		      for(Map<String,Object> map :list){
		    	  if(null!=map.get("applyUnit") && !"".equals(map.get("applyUnit"))){   
//			    	  if("局机关".equals(map.get("applyUnit").toString())){
//			    		  data1++;
//			    	  }
//			    	  if("监控中心".equals(map.get("applyUnit").toString())){
//			    		  data2++;
//			    	  }
			    	  if("维修中心".equals(map.get("applyUnit").toString())){
			    		  data3++;
			    	  }
			    	  if("技保中心".equals(map.get("applyUnit").toString())){
			    		  data4++;
			    	  }
			    	  if("网络中心".equals(map.get("applyUnit").toString())){
			    		  data5++;
			    	  }
//			    	  if("区管中心".equals(map.get("applyUnit").toString())){
//			    		  data6++;
//			    	  }
//			    	  if("终端中心".equals(map.get("applyUnit").toString())){
//			    		  data7++;
//			    	  }
//			    	  if("飞服中心".equals(map.get("applyUnit").toString())){
//			    		  data8++;
//			    	  }
//			    	  if("运管中心".equals(map.get("applyUnit").toString())){
//			    		  data9++;
//			    	  }
			    	  if("运管中心".equals(map.get("applyUnit").toString())){
			    		  data10++;
			    	  }
			    	  if("山东".equals(map.get("applyUnit").toString())){
			    		  data11++;
			    	  }
			    	  if("安徽".equals(map.get("applyUnit").toString())){
			    		  data12++;
			    	  }
			    	  if("江苏".equals(map.get("applyUnit").toString())){
			    		  data13++;
			    	  }
			    	  if("浙江".equals(map.get("applyUnit").toString())){
			    		  data14++;
			    	  }
			    	  if("江西".equals(map.get("applyUnit").toString())){
			    		  data15++;
			    	  }
			    	  if("福建".equals(map.get("applyUnit").toString())){
			    		  data16++;
			    	  }
			    	  if("青岛".equals(map.get("applyUnit").toString())){
			    		  data17++;
			    	  }
			    	  if("温州".equals(map.get("applyUnit").toString())){
			    		  data18++;
			    	  }
			    	  if("宁波".equals(map.get("applyUnit").toString())){
			    		  data19++;
			    	  }
			    	  if("厦门".equals(map.get("applyUnit").toString())){
			    		  data20++;
			    	  }
		    	}
		    	if(null!=map.get("haltReason") && !"".equals(map.get("haltReason"))){   
		    	  if("切换".equals(map.get("haltReason").toString())){
		    		  data21++;
		    	  }
		    	  if("维护".equals(map.get("haltReason").toString())){
		    		  data22++;
		    	  }
		    	  if("检修".equals(map.get("haltReason").toString())){
		    		  data23++;
		    	  }
		    	  if("更新改造".equals(map.get("haltReason").toString())){
		    		  data24++;
		    	  }
		    	  if("升级".equals(map.get("haltReason").toString())){
		    		  data25++;
		    	  }
		    	  if("飞行校验".equals(map.get("haltReason").toString())){
		    		  data26++;
		    	  }
		    	  if("设备搬迁".equals(map.get("haltReason").toString())){
		    		  data27++;
		    	  }
		    	  if("测试".equals(map.get("haltReason").toString())){
		    		  data28++;
		    	  }
		    	  if("数据发布".equals(map.get("haltReason").toString())){
		    		  data29++;
		    	  }
		    	  if("更换".equals(map.get("haltReason").toString())){
		    		  data30++;
		    	  }
		    	  if("割接".equals(map.get("haltReason").toString())){
		    		  data31++;
		    	  }
		    	  if("天气原因".equals(map.get("haltReason").toString())){
		    		  data32++;
		    	  }
		    	  if("运营商".equals(map.get("haltReason").toString())){
		    		  data33++;
		    	  }
		    	  if("市电".equals(map.get("haltReason").toString())){
		    		  data34++;
		    	  }
		    	  if("其他".equals(map.get("haltReason").toString())){
		    		  data35++;
		    	  }
		      } 
		    	  if(null!=map.get("planType") && !"".equals(map.get("planType"))){ 
			    	  if("计划".equals(map.get("planType").toString())){
			    		  data36++;
			    	  }
			    	  if("临时".equals(map.get("planType").toString())){
			    		  data37++;
			    	  }
			    	  if("紧急".equals(map.get("planType").toString())){
			    		  data38++;
			    	  }
		    	  }
		      }
			Option option = new GsonOption();  
			option.title().text("停机统计图").setX("bottom");
	        option.tooltip().trigger(Trigger.axis);
	        AxisLabel axisLabel = new AxisLabel();
	        axisLabel.setInterval(0);
	        axisLabel.setRotate(60);
	        if("1".equals(statisDirection1)){
//	        	option.legend("单位");
	        	option.legend("");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

		        Bar bar = new Bar("");
//		        Bar bar = new Bar("单位");
//		        bar.data(data1, data2, data3, data4, data5, data6,data7, data8, data9, data10, data11, data12,data13, data14, data15, data16, data17
//		        		, data18,data19, data20);
		        bar.data(data3, data4, data5,data11, data12,data13, data14, data15, data16, data17
		        		, data18,data19, data20);
//		        bar.markPoint().data(new PointData().type(MarkType.max).name("最大值"), new PointData().type(MarkType.min).name("最小值"));
//		        bar.markLine().data(new PointData().type(MarkType.average).name("平均值"));
//	        	option.xAxis(new CategoryAxis().data("局机关", "监控中心", "维修中心", "技保中心", "网络中心", "区管中心","终端中心","飞服中心"
//	        			,"运管中心","山东","安徽","江苏","浙江","江西","福建","青岛","温州","宁波","厦门"));
	        	option.xAxis(new CategoryAxis().data("维修", "技保", "网络","山东","安徽","江苏","浙江","江西","福建","青岛","温州","宁波","厦门").axisLabel(axisLabel));
		        option.yAxis(new ValueAxis());
		        option.color("#2aa8ed");
		        option.series(bar);
	        } else if("2".equals(statisDirection1)){
//	        	option.legend("停机原因");
	        	option.legend("");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

//		        Bar bar = new Bar("停机原因");
		        Bar bar = new Bar("");
		        bar.data(data21, data22, data23,data24, data25, data26,data27, data28, data29,data30, data31, data32,data33,data34,data35);
//		        bar.markPoint().data(new PointData().type(MarkType.max).name("最大值"), new PointData().type(MarkType.min).name("最小值"));
//		        bar.markLine().data(new PointData().type(MarkType.average).name("平均值"));
	        	option.xAxis(new CategoryAxis().data("切换", "维护", "检修","更新改造","升级","飞行校验","设备搬迁","测试","数据发布","更换",
	        			"割接","天气原因","运营商","市电","其他").axisLabel(axisLabel));
		        option.yAxis(new ValueAxis());
		        option.color("#2aa8ed");
		        option.series(bar);
	        } else {
//	        	option.legend("停机种类");
	        	option.legend("");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

//		        Bar bar = new Bar("停机种类");
		        Bar bar = new Bar("");
		        bar.data(data36,data37,data38);
//		        bar.markPoint().data(new PointData().type(MarkType.max).name("最大值"), new PointData().type(MarkType.min).name("最小值"));
//		        bar.markLine().data(new PointData().type(MarkType.average).name("平均值"));
	        	option.xAxis(new CategoryAxis().data("计划", "临时", "紧急").axisLabel(axisLabel));
		        option.yAxis(new ValueAxis());
		        option.color("#2aa8ed");
		        option.series(bar);
	        }
	        //类型转换
	        JSONObject jsonObj = JSONObject.fromObject(option.toString());
	        response.setContentType("application/json;charset=UTF-8");  
	        response.setHeader("Cache-Control", "no-cache"); 
	        //输出到页面
	        response.getWriter().print(jsonObj); 
		} catch (Exception e) {  
			e.printStackTrace();
	    } 
	}
	
	/**
	 * 故障统计图数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public void faultMapData() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = format.format(new Date());
		String startDate2 = request.getParameter("startDate2");
		String endDate2 = request.getParameter("endDate2");
		String statisDirection2 = request.getParameter("statisDirection2");
  	    
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("fault");
		Element parameter = output.addElement("parameter");
		
		Element fillUnitElement = parameter.addElement("fillUnit");
		fillUnitElement.setText("");
		Element startDateElement = parameter.addElement("startDate");
		if(startDate2==null || startDate2.equals("")){
			startDateElement.setText("");
		}else{
			startDateElement.setText(startDate2);
		}
		Element endDateElement = parameter.addElement("endDate");
		if(endDate2==null || endDate2.equals("")){
			endDateElement.setText("");
		}else{
			endDateElement.setText(endDate2);
		}
		Element majorElement = parameter.addElement("major");
		majorElement.setText("");
		Element gradeElement = parameter.addElement("grade");
		gradeElement.setText("");
		Element systemElement = parameter.addElement("system");
		systemElement.setText("");
		
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(1000000));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(1));
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String res = null;
		try {
			  Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      list = xmlDataParse.parseXmlDataToList(res);
		      }
		      int data1=0;int data2=0;int data3=0;int data03=0;int data04=0;int data05=0;
		      int data4=0;int data5=0;int data6=0;int data7=0;int data8=0;
		      int data9=0;int data10=0;int data11=0;int data011=0;int data12=0;int data012=0;
		      int data13=0;int data013=0;int data014=0;int data015=0;int data016=0;
		      int data14=0;int data15=0;int data16=0;int data17=0;int data017=0;int data18=0;
		      int data018=0;int data019=0;int data020=0;int data021=0;int data022=0;
		      int data19=0;int data20=0;int data21=0;int data22=0;int data23=0;int data023=0;
		      int data024=0;int data025=0;int data026=0;
		      int data24=0;int data25=0;int data26=0;int data27=0;int data28=0;
		      int data29=0;int data30=0;int data31=0;int data32=0;int data33=0;
		      int data031=0;int data032=0;int data033=0;
		      int data035=0;int data036=0;int data037=0;
		      int data34=0;int data35=0;int data36=0;int data37=0;int data38=0;
		      for(Map<String,Object> map :list){
		    	  if(null!=map.get("fillUnit") && !"".equals(map.get("fillUnit"))){
//		    		  if("局机关".equals(map.get("fillUnit").toString())){
//			    		  data1++;
//			    	  }
//			    	  if("监控中心".equals(map.get("fillUnit").toString())){
//			    		  data2++;
//			    	  }
			    	  if("维修中心".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data03++;
			    	  } else if("维修中心".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data3++; 
			    	  }
			    	  if("技保中心".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data04++;
			    	  } else if("技保中心".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data4++; 
			    	  }
			    	  if("网络中心".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data05++;
			    	  } else if("网络中心".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data5++; 
			    	  }
//			    	  if("区管中心".equals(map.get("fillUnit").toString())){
//			    		  data6++;
//			    	  }
//			    	  if("终端中心".equals(map.get("fillUnit").toString())){
//			    		  data7++;
//			    	  }
//			    	  if("飞服中心".equals(map.get("fillUnit").toString())){
//			    		  data8++;
//			    	  }
//			    	  if("运管中心".equals(map.get("fillUnit").toString())){
//			    		  data9++;
//			    	  }
//			    	  if("运管中心".equals(map.get("fillUnit").toString())){
//			    		  data10++;
//			    	  }
			    	  if("山东".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data011++;
			    	  } else if("山东".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data11++; 
			    	  }
			    	  if("安徽".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data012++;
			    	  } else if("安徽".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data12++; 
			    	  }
			    	  if("江苏".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data013++;
			    	  } else if("江苏".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data13++; 
			    	  }
			    	  if("浙江".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data014++;
			    	  } else if("浙江".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data14++; 
			    	  }
			    	  if("江西".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data015++;
			    	  } else if("江西".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data15++; 
			    	  }
			    	  if("福建".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data016++;
			    	  } else if("福建".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data16++; 
			    	  }
			    	  if("青岛".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data017++;
			    	  } else if("青岛".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data17++; 
			    	  }
			    	  if("温州".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data018++;
			    	  } else if("温州".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data18++; 
			    	  }
			    	  if("宁波".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data019++;
			    	  } else if("宁波".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data19++; 
			    	  }
			    	  if("厦门".equals(map.get("fillUnit").toString()) && "".equals(map.get("endDate"))){
			    		  data020++;
			    	  } else if("厦门".equals(map.get("fillUnit").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data20++; 
			    	  }
		    	  }
			      if(null!=map.get("major") && !"".equals(map.get("major"))){ 
			    	  if("地空通信".equals(map.get("major").toString()) && "".equals(map.get("endDate"))){
			    		  data021++;
			    	  } else if("地空通信".equals(map.get("major").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data21++; 
			    	  }
			    	  if("平面通信".equals(map.get("major").toString()) && "".equals(map.get("endDate"))){
			    		  data022++;
			    	  } else if("平面通信".equals(map.get("major").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data22++; 
			    	  }
			    	  if("导航".equals(map.get("major").toString()) && "".equals(map.get("endDate"))){
			    		  data023++;
			    	  } else if("导航".equals(map.get("major").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data23++; 
			    	  }
			    	  if("监视".equals(map.get("major").toString()) && "".equals(map.get("endDate"))){
			    		  data024++;
			    	  } else if("监视".equals(map.get("major").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data24++; 
			    	  }
			    	  if("动力".equals(map.get("major").toString()) && "".equals(map.get("endDate"))){
			    		  data025++;
			    	  } else if("动力".equals(map.get("major").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data25++; 
			    	  }
			    	  if("信息系统".equals(map.get("major").toString()) && "".equals(map.get("endDate"))){
			    		  data026++;
			    	  } else if("信息系统".equals(map.get("major").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data26++; 
			    	  }
//			    	  if("气象".equals(map.get("major").toString())){
//			    		  data27++;
//			    	  }
//			    	  if("情报".equals(map.get("major").toString())){
//			    		  data28++;
//			    	  }
//			    	  if("其他".equals(map.get("major").toString())){
//			    		  data29++;
//			    	  }
//			    	  if("空管外部专业".equals(map.get("major").toString())){
//			    		  data30++;
//			    	  }
		    	  }
		    	  
		    	  if(null!=map.get("grade") && !"".equals(map.get("grade"))){
			    	  if("I类故障".equals(map.get("grade").toString()) && "".equals(map.get("endDate"))){
			    		  data031++;
			    	  } else if("I类故障".equals(map.get("grade").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data31++; 
			    	  }
			    	  if("II类故障".equals(map.get("grade").toString()) && "".equals(map.get("endDate"))){
			    		  data032++;
			    	  } else if("II类故障".equals(map.get("grade").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data32++; 
			    	  }
			    	  if("III类故障".equals(map.get("grade").toString()) && "".equals(map.get("endDate"))){
			    		  data033++;
			    	  } else if("III类故障".equals(map.get("grade").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data33++; 
			    	  }
//			    	  if("其它".equals(map.get("grade").toString())){
//			    		  data34++;
//			    	  }
		    	  }
		    	  
		    	  if(null!=map.get("classify") && !"".equals(map.get("classify"))){
			    	  if("软件".equals(map.get("classify").toString()) && "".equals(map.get("endDate"))){
			    		  data035++;
			    	  } else if("软件".equals(map.get("classify").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data35++; 
			    	  }
			    	  if("硬件".equals(map.get("classify").toString()) && "".equals(map.get("endDate"))){
			    		  data036++;
			    	  } else if("硬件".equals(map.get("classify").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data36++; 
			    	  }
			    	  if("外部原因".equals(map.get("classify").toString()) && "".equals(map.get("endDate"))){
			    		  data037++;
			    	  }else if("外部原因".equals(map.get("classify").toString()) && !"".equals(map.get("endDate")) && nowDate.equals(map.get("endDate").toString().substring(0, 10))){
			    		  data37++; 
			    	  }
		    	  }
		      }
			Option option = new GsonOption();  
			option.title().text("故障统计图").setX("bottom");
	        option.tooltip().trigger(Trigger.axis);
	        AxisLabel axisLabel = new AxisLabel();
	        axisLabel.setInterval(0);
	        axisLabel.setRotate(60);
	        if("1".equals(statisDirection2)){
	        	option.legend("当天已解决故障", "截止当目前未解决故障");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);
		        Bar bar = new Bar("当天已解决故障");
		        bar.data(data3, data4, data5,data11, data12,data13, data14, data15, data16, data17,data18,data19, data20);
		        Bar bar1 = new Bar("截止当目前未解决故障");
		        bar1.data(data03, data04, data05,data011, data012,data013, data014, data015, data016, data017,data018,data019, data020);
	        	option.xAxis(new CategoryAxis().data("维修中心", "技保中心", "网络中心","山东","安徽","江苏","浙江","江西","福建","青岛","温州","宁波","厦门").axisLabel(axisLabel));
	        	option.color("#2aa8ed","green");
	            option.yAxis(new ValueAxis());
	            option.series(bar,bar1);
		        
	        } else if("2".equals(statisDirection2)){
	        	option.legend("当天已解决故障", "截止当目前未解决故障");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

		        Bar bar = new Bar("当天已解决故障");
		        bar.data(data21, data22, data23,data24, data25, data26);
		        Bar bar1 = new Bar("截止当目前未解决故障");
		        bar1.data(data021, data022, data023,data024, data025, data026);
	        	option.xAxis(new CategoryAxis().data("地空通信", "平面通信", "导航","监视","动力","信息系统").axisLabel(axisLabel));
	        	option.color("#2aa8ed","green");
	            option.yAxis(new ValueAxis());
	            option.series(bar,bar1);
	        } else if("3".equals(statisDirection2)){
	        	option.legend("当天已解决故障", "截止当目前未解决故障");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

		        Bar bar = new Bar("当天已解决故障");
		        bar.data(data31,data32,data33);
		        Bar bar1 = new Bar("截止当目前未解决故障");
		        bar1.data(data031,data032,data033);
	        	option.xAxis(new CategoryAxis().data("I类故障", "II类故障", "III类故障").axisLabel(axisLabel));
	        	option.color("#2aa8ed","green");
	            option.yAxis(new ValueAxis());
	            option.series(bar,bar1);
	        } else {
	        	option.legend("当天已解决故障", "截止当目前未解决故障");
	        	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
		        option.calculable(true);

		        Bar bar = new Bar("当天已解决故障");
		        bar.data(data35,data36,data37);
		        Bar bar1 = new Bar("截止当目前未解决故障");
		        bar1.data(data035,data036,data037);
	        	option.xAxis(new CategoryAxis().data("软件", "硬件", "外部原因").axisLabel(axisLabel));
	        	option.color("#2aa8ed","green");
	            option.yAxis(new ValueAxis());
	            option.series(bar,bar1);
	        }
	        //类型转换
	        JSONObject jsonObj = JSONObject.fromObject(option.toString());
	        response.setContentType("application/json;charset=UTF-8");  
	        response.setHeader("Cache-Control", "no-cache"); 
	        //输出到页面
	        response.getWriter().print(jsonObj); 
		} catch (Exception e) {  
			e.printStackTrace();
	    } 
	}
	
	
	/**
	 * 计划起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void planUpDownZSSSFlight() throws IOException {
		int  planZSSSNum = getPlanUpDownFlight("ZSSS");
//		Option option = new GsonOption(); 
//		option.legend("虹桥计划起降航班统计图");
//		option.tooltip().formatter("{a} <br/>{b} : {c}");
//    	option.toolbox().show(true).feature(Tool.mark, Tool.saveAsImage);
//      option.calculable(true);
//    	option.series(new Gauge("虹桥计划起降航班统计图").max(900).detail(new Detail().formatter("{value}")).data(new Data("", planZSSSNum)));
//        //类型转换
//        JSONObject jsonObj = JSONObject.fromObject(option.toString());
        response.setContentType("application/json;charset=UTF-8");  
        response.setHeader("Cache-Control", "no-cache"); 
        //输出到页面
//        response.getWriter().print(jsonObj); 
        response.getWriter().print(planZSSSNum); 
	}
	/**
	 * 计划起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void planUpDownZSPDFlight() throws IOException {
		int  planZSPDNum = getPlanUpDownFlight("ZSPD");
//		Option option = new GsonOption(); 
//		option.legend("浦东计划起降航班统计图");
//		option.tooltip().formatter("{a} <br/>{b} : {c}");
//    	option.toolbox().show(true).feature(Tool.mark, Tool.saveAsImage);
//        option.calculable(true);
//    	option.series(new Gauge("浦东计划起降航班统计图").max(900).detail(new Detail().formatter("{value}")).data(new Data("", planZSPDNum)));
//        //类型转换
//        JSONObject jsonObj = JSONObject.fromObject(option.toString());
//        response.setContentType("application/json;charset=UTF-8");  
//        response.setHeader("Cache-Control", "no-cache"); 
        //输出到页面
		response.getWriter().print(planZSPDNum);
//        response.getWriter().print(jsonObj); 
	}
	/**
	 * 实际起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void actualUpDownZSSSFlight() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
		int  actualZSSSNum = getActualUpDownFlight(dateStr,"ZSSS");
//		Option option = new GsonOption(); 
//		option.legend("虹桥实际起降航班统计图");
//		option.tooltip().formatter("{a} <br/>{b} : {c}");
//    	option.toolbox().show(true).feature(Tool.mark, Tool.saveAsImage);
//        option.calculable(true);
//    	option.series(new Gauge().max(900).name("虹桥实际起降航班统计图")
//    			.detail(new Detail().formatter("{value}")).axisLine().data(new Data("", actualZSSSNum)));
//        //类型转换
//        JSONObject jsonObj = JSONObject.fromObject(option.toString());
//        response.setContentType("application/json;charset=UTF-8");  
//        response.setHeader("Cache-Control", "no-cache"); 
        //输出到页面
//        response.getWriter().print(jsonObj); 
        response.getWriter().print(actualZSSSNum); 
	}
	/**
	 * 实际起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public Integer getActualUpDownZSSSFlight() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
		int  actualZSSSNum = getActualUpDownFlight("2017-12-08","ZSSS");
        return actualZSSSNum;
	}
	
	/**
	 * 实际起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void actualUpDownZSPDFlight() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
		int  actualZSPDNum = getActualUpDownFlight(dateStr,"ZSPD");
//		Option option = new GsonOption(); 
//		option.legend("浦东实际起降航班统计图");
//		option.tooltip().formatter("{a} <br/>{b} : {c}");
//    	option.toolbox().show(true).feature(Tool.mark, Tool.saveAsImage);
//        option.calculable(true);
//    	option.series(new Gauge("浦东实际起降航班统计图").max(900).detail(new Detail().formatter("{value}")).data(new Data("", actualZSPDNum)));
//        //类型转换
//        JSONObject jsonObj = JSONObject.fromObject(option.toString());
//        response.setContentType("application/json;charset=UTF-8");  
//        response.setHeader("Cache-Control", "no-cache"); 
//        //输出到页面
//        response.getWriter().print(jsonObj); 
		response.getWriter().print(actualZSPDNum); 
	}
	
	/**
	 * 实际起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public Integer getActualUpDownZSPDFlight() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
		int  actualZSPDNum = getActualUpDownFlight("2017-12-08","ZSPD");
		return actualZSPDNum;
	}
	
	public int getPlanUpDownFlight(String airPort){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int num=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
    	  sql = "SELECT COUNT(*) AS COUNT FROM CDMINFOR A WHERE A.ADEP IN ('"+airPort+"') AND " +
      		"A.STOD LIKE '%"+dateStr+"%' " +
      		"OR A.ADES IN ('"+airPort+"') AND A.STOA LIKE '%"+dateStr+"%'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	num = Integer.parseInt(rs.getString("COUNT"));
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return num;
	}
	
	public int getActualUpDownFlight(String dateStr,String airPort){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("ActualUpDown开始时间："+format.format(new Date()));
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int num=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
    	  sql = "SELECT COUNT(*) AS COUNT FROM CDMINFOR A WHERE A.ADEP IN ('"+airPort+"') AND " +
    	  		"A.ATOT LIKE '%"+dateStr+"%' OR A.ADES IN ('"+airPort+"') AND A.ALDT LIKE '%"+dateStr+"%'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	num = Integer.parseInt(rs.getString("COUNT"));
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   System.out.println("ActualUpDown结束时间："+format.format(new Date()));
	   return num;
	}
	
	/**
	 * 七日实际起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void sevenActualUpDownFlight() throws IOException {
		int data1=0;int data2=0;int data3=0;
	    int data4=0;int data5=0;int data6=0;
	    int data7=0;
	    int data11=0;int data22=0;int data33=0;
	    int data44=0;int data55=0;int data66=0;
	    int data77=0;
	    String date1="";String date2="";String date3="";
	    String date4="";String date5="";String date6="";
	    String date7="";
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    for(int i=1;i<=7;i++){
	    	if(i==1){
	    		Date date= new Date();
	    		date1 = format.format(date);
	    	    data1 = getActualUpDownFlight(date1,"ZSSS");
	    	    data11 = getActualUpDownFlight(date1,"ZSPD");
	    	}
	    	if(i==2){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 1);  
	            Date monday = c.getTime();
	            date2 = format.format(monday);
	            data2 = getActualUpDownFlight(date2,"ZSSS");
	            data22 = getActualUpDownFlight(date2,"ZSPD");
	    	}
	    	if(i==3){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 2);  
	            Date monday = c.getTime();
	            date3 = format.format(monday);
	            data3 = getActualUpDownFlight(date3,"ZSSS");
	            data33 = getActualUpDownFlight(date3,"ZSPD");
	    	}
	    	if(i==4){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 3);  
	            Date monday = c.getTime();
	            date4 = format.format(monday);
	            data4 = getActualUpDownFlight(date4,"ZSSS");
	            data44 = getActualUpDownFlight(date4,"ZSPD");
	    	}
	    	if(i==5){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 4);  
	            Date monday = c.getTime();
	            date5 = format.format(monday);
	            data5 = getActualUpDownFlight(date5,"ZSSS");
	            data55 = getActualUpDownFlight(date5,"ZSPD");
	    	}
	    	if(i==6){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 5);  
	            Date monday = c.getTime();
	            date6 = format.format(monday);
	            data6 = getActualUpDownFlight(date6,"ZSSS");
	            data66 = getActualUpDownFlight(date6,"ZSPD");
	    	}
	    	if(i==7){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 6);  
	            Date monday = c.getTime();
	            date7 = format.format(monday);
	            data7 = getActualUpDownFlight(date7,"ZSSS");
	            data77 = getActualUpDownFlight(date7,"ZSPD");
	    	}
	    }
		Option option = new GsonOption();  
		option.title().text("七日内实际起降航班统计图").setX("bottom");
        option.tooltip().trigger(Trigger.axis);
        AxisLabel axisLabel = new AxisLabel();
        axisLabel.setInterval(0);
        axisLabel.setRotate(30);
        option.legend("虹桥", "浦东");
    	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
        option.calculable(true);
        Bar bar = new Bar("虹桥");
        bar.data(data7, data6, data5,data4,data3,data2, data1);
        Bar bar1 = new Bar("浦东");
        bar1.data(data77, data66, data55,data44,data33,data22, data11);
    	option.xAxis(new CategoryAxis().data(date7, date6, date5,date4,date3,date2,date1).axisLabel(axisLabel));
    	option.color("#2aa8ed","green");
        option.yAxis(new ValueAxis());
        option.series(bar,bar1);
        //类型转换
        JSONObject jsonObj = JSONObject.fromObject(option.toString());
        response.setContentType("application/json;charset=UTF-8");  
        response.setHeader("Cache-Control", "no-cache"); 
        //输出到页面
        response.getWriter().print(jsonObj); 
	}
	
	
	/**
	 * 七日实际起降航班统计图
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public String getSevenActualUpDownFlight() throws IOException {
		int data1=0;int data2=0;int data3=0;
	    int data4=0;int data5=0;int data6=0;
	    int data7=0;
	    int data11=0;int data22=0;int data33=0;
	    int data44=0;int data55=0;int data66=0;
	    int data77=0;
	    String date1="";String date2="";String date3="";
	    String date4="";String date5="";String date6="";
	    String date7="";
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    for(int i=1;i<=7;i++){
	    	if(i==1){
	    		Date date= new Date();
	    		date1 = format.format(date);
	    	    data1 = getActualUpDownFlight(date1,"ZSSS");
	    	    data11 = getActualUpDownFlight(date1,"ZSPD");
	    	}
	    	if(i==2){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 1);  
	            Date monday = c.getTime();
	            date2 = format.format(monday);
	            data2 = getActualUpDownFlight(date2,"ZSSS");
	            data22 = getActualUpDownFlight(date2,"ZSPD");
	    	}
	    	if(i==3){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 2);  
	            Date monday = c.getTime();
	            date3 = format.format(monday);
	            data3 = getActualUpDownFlight(date3,"ZSSS");
	            data33 = getActualUpDownFlight(date3,"ZSPD");
	    	}
	    	if(i==4){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 3);  
	            Date monday = c.getTime();
	            date4 = format.format(monday);
	            data4 = getActualUpDownFlight(date4,"ZSSS");
	            data44 = getActualUpDownFlight(date4,"ZSPD");
	    	}
	    	if(i==5){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 4);  
	            Date monday = c.getTime();
	            date5 = format.format(monday);
	            data5 = getActualUpDownFlight(date5,"ZSSS");
	            data55 = getActualUpDownFlight(date5,"ZSPD");
	    	}
	    	if(i==6){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 5);  
	            Date monday = c.getTime();
	            date6 = format.format(monday);
	            data6 = getActualUpDownFlight(date6,"ZSSS");
	            data66 = getActualUpDownFlight(date6,"ZSPD");
	    	}
	    	if(i==7){
	    		Calendar c = Calendar.getInstance(); 
	    		c.add(Calendar.DATE, - 6);  
	            Date monday = c.getTime();
	            date7 = format.format(monday);
	            data7 = getActualUpDownFlight(date7,"ZSSS");
	            data77 = getActualUpDownFlight(date7,"ZSPD");
	    	}
	    }
		Option option = new GsonOption();  
		option.title().text("七日内实际起降航班统计图").setX("bottom");
        option.tooltip().trigger(Trigger.axis);
        AxisLabel axisLabel = new AxisLabel();
        axisLabel.setInterval(0);
        axisLabel.setRotate(30);
        option.legend("虹桥", "浦东");
    	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
        option.calculable(true);
        Bar bar = new Bar("虹桥");
        bar.data(data7, data6, data5,data4,data3,data2, data1);
        Bar bar1 = new Bar("浦东");
        bar1.data(data77, data66, data55,data44,data33,data22, data11);
    	option.xAxis(new CategoryAxis().data(date7, date6, date5,date4,date3,date2,date1).axisLabel(axisLabel));
    	option.color("#2aa8ed","green");
        option.yAxis(new ValueAxis());
        option.series(bar,bar1);
        //类型转换
        JSONObject jsonObj = JSONObject.fromObject(option.toString());
        return jsonObj.toString();
	}
	/**
	 * 间隔二小时的实时流量
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void realTimeFlow() throws IOException {
		int data1=0;int data2=0;int data3=0;
	    int data4=0;int data5=0;int data6=0;
	    int data7=0;int data8=0;int data9=0;
	    int data10=0;int data11=0;int data12=0;
	    int data01=0;int data02=0;int data03=0;
	    int data04=0;int data05=0;int data06=0;
	    int data07=0;int data08=0;int data09=0;
	    int data010=0;int data011=0;int data012=0;
		List<Map<String,String>> listMap = getRealTimeFlow();
		for(Map<String,String> map:listMap){
			if(map.get("adep")!=null && !"".equals(map.get("adep"))){
	        	if("ZSSS".equals(map.get("adep").toString())){
	        		if(map.get("atot")!=null && !"".equals(map.get("atot"))){
	        			String[] atotArry = map.get("atot").toString().split(" ");
	        			String[] atotTime = atotArry[1].split(":");
	        			int atot = Integer.parseInt(atotTime[0]);
	        			if(atot>=0 && atot<2){
	        				data1++;
	        			}
	        			if(atot>=2 && atot<4){
	        				data2++;
	        			}
	        			if(atot>=4 && atot<6){
	        				data3++;
	        			}
	        			if(atot>=6 && atot<8){
	        				data4++;
	        			}
	        			if(atot>=8 && atot<10){
	        				data5++;
	        			}
	        			if(atot>=10 && atot<12){
	        				data6++;
	        			}
	        			if(atot>=12 && atot<14){
	        				data7++;
	        			}
	        			if(atot>=14 && atot<16){
	        				data8++;
	        			}
	        			if(atot>=16 && atot<18){
	        				data9++;
	        			}
	        			if(atot>=18 && atot<20){
	        				data10++;
	        			}
	        			if(atot>=20 && atot<22){
	        				data11++;
	        			}
	        			if(atot>=22 && atot<24){
	        				data12++;
	        			}
	        		}
	        	} 
	        	if("ZSPD".equals(map.get("adep").toString())){
	        		if(map.get("atot")!=null && !"".equals(map.get("atot"))){
	        			String[] atotArry = map.get("atot").toString().split(" ");
	        			String[] atotTime = atotArry[1].split(":");
	        			int atot = Integer.parseInt(atotTime[0]);
	        			if(atot>=0 && atot<2){
	        				data01++;
	        			}
	        			if(atot>=2 && atot<4){
	        				data02++;
	        			}
	        			if(atot>=4 && atot<6){
	        				data03++;
	        			}
	        			if(atot>=6 && atot<8){
	        				data04++;
	        			}
	        			if(atot>=8 && atot<10){
	        				data05++;
	        			}
	        			if(atot>=10 && atot<12){
	        				data06++;
	        			}
	        			if(atot>=12 && atot<14){
	        				data07++;
	        			}
	        			if(atot>=14 && atot<16){
	        				data08++;
	        			}
	        			if(atot>=16 && atot<18){
	        				data09++;
	        			}
	        			if(atot>=18 && atot<20){
	        				data010++;
	        			}
	        			if(atot>=20 && atot<22){
	        				data011++;
	        			}
	        			if(atot>=22 && atot<24){
	        				data012++;
	        			}
	        		}
	        	}
	        }
			if(map.get("ades")!=null && !"".equals(map.get("ades"))){
	        	if("ZSSS".equals(map.get("ades").toString())){
	        		if(map.get("aldt")!=null && !"".equals(map.get("aldt"))){
	        			String[] aldtArry = map.get("aldt").toString().split(" ");
	        			String[] aldtTime = aldtArry[1].split(":");
	        			int aldt = Integer.parseInt(aldtTime[0]);
	        			if(aldt>=0 && aldt<2){
	        				data1++;
	        			}
	        			if(aldt>=2 && aldt<4){
	        				data2++;
	        			}
	        			if(aldt>=4 && aldt<6){
	        				data3++;
	        			}
	        			if(aldt>=6 && aldt<8){
	        				data4++;
	        			}
	        			if(aldt>=8 && aldt<10){
	        				data5++;
	        			}
	        			if(aldt>=10 && aldt<12){
	        				data6++;
	        			}
	        			if(aldt>=12 && aldt<14){
	        				data7++;
	        			}
	        			if(aldt>=14 && aldt<16){
	        				data8++;
	        			}
	        			if(aldt>=16 && aldt<18){
	        				data9++;
	        			}
	        			if(aldt>=18 && aldt<20){
	        				data10++;
	        			}
	        			if(aldt>=20 && aldt<22){
	        				data11++;
	        			}
	        			if(aldt>=22 && aldt<24){
	        				data12++;
	        			}
	        		}
	        	} 
	        	if("ZSPD".equals(map.get("ades").toString())){
	        		if(map.get("aldt")!=null && !"".equals(map.get("aldt"))){
	        			String[] aldtArry = map.get("aldt").toString().split(" ");
	        			String[] aldtTime = aldtArry[1].split(":");
	        			int aldt = Integer.parseInt(aldtTime[0]);
	        			if(aldt>=0 && aldt<2){
	        				data01++;
	        			}
	        			if(aldt>=2 && aldt<4){
	        				data02++;
	        			}
	        			if(aldt>=4 && aldt<6){
	        				data03++;
	        			}
	        			if(aldt>=6 && aldt<8){
	        				data04++;
	        			}
	        			if(aldt>=8 && aldt<10){
	        				data05++;
	        			}
	        			if(aldt>=10 && aldt<12){
	        				data06++;
	        			}
	        			if(aldt>=12 && aldt<14){
	        				data07++;
	        			}
	        			if(aldt>=14 && aldt<16){
	        				data08++;
	        			}
	        			if(aldt>=16 && aldt<18){
	        				data09++;
	        			}
	        			if(aldt>=18 && aldt<20){
	        				data010++;
	        			}
	        			if(aldt>=20 && aldt<22){
	        				data011++;
	        			}
	        			if(aldt>=22 && aldt<24){
	        				data012++;
	        			}
	        		}
	        	}
	        }
		}
		Option option = new GsonOption();  
		option.title().text("实时流量统计图").setX("bottom");
        option.tooltip().trigger(Trigger.axis);
        AxisLabel axisLabel = new AxisLabel();
        axisLabel.setInterval(0);
        axisLabel.setRotate(60);
        option.legend("虹桥", "浦东");
    	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
        option.calculable(true);
        Bar bar = new Bar("虹桥");
        bar.data(data1, data2, data3,data4,data5,data6, data7, data8, data9, data10,data11,data12);
        Bar bar1 = new Bar("浦东");
        bar1.data(data01, data02, data03,data04,data05,data06, data07, data08, data09, data010,data011,data012);
    	option.xAxis(new CategoryAxis().data("0-2点", "2-4点", "4-6点","6-8点","8-10点","10-12点","12-14点","14-16点","16-18点","18-20点","20-22点","22-24点").axisLabel(axisLabel));
    	option.color("#2aa8ed","green");
        option.yAxis(new ValueAxis());
        option.series(bar,bar1);
        //类型转换
        JSONObject jsonObj = JSONObject.fromObject(option.toString());
        response.setContentType("application/json;charset=UTF-8");  
        response.setHeader("Cache-Control", "no-cache"); 
        //输出到页面
        response.getWriter().print(jsonObj); 
	}
	
	
	/**
	 * 间隔二小时的实时流量
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public String getWxRealTimeFlow() throws IOException {
		int data1=0;int data2=0;int data3=0;
	    int data4=0;int data5=0;int data6=0;
	    int data7=0;int data8=0;int data9=0;
	    int data10=0;int data11=0;int data12=0;
	    int data01=0;int data02=0;int data03=0;
	    int data04=0;int data05=0;int data06=0;
	    int data07=0;int data08=0;int data09=0;
	    int data010=0;int data011=0;int data012=0;
		List<Map<String,String>> listMap = getRealTimeFlow();
		for(Map<String,String> map:listMap){
			if(map.get("adep")!=null && !"".equals(map.get("adep"))){
	        	if("ZSSS".equals(map.get("adep").toString())){
	        		if(map.get("atot")!=null && !"".equals(map.get("atot"))){
	        			String[] atotArry = map.get("atot").toString().split(" ");
	        			String[] atotTime = atotArry[1].split(":");
	        			int atot = Integer.parseInt(atotTime[0]);
	        			if(atot>=0 && atot<2){
	        				data1++;
	        			}
	        			if(atot>=2 && atot<4){
	        				data2++;
	        			}
	        			if(atot>=4 && atot<6){
	        				data3++;
	        			}
	        			if(atot>=6 && atot<8){
	        				data4++;
	        			}
	        			if(atot>=8 && atot<10){
	        				data5++;
	        			}
	        			if(atot>=10 && atot<12){
	        				data6++;
	        			}
	        			if(atot>=12 && atot<14){
	        				data7++;
	        			}
	        			if(atot>=14 && atot<16){
	        				data8++;
	        			}
	        			if(atot>=16 && atot<18){
	        				data9++;
	        			}
	        			if(atot>=18 && atot<20){
	        				data10++;
	        			}
	        			if(atot>=20 && atot<22){
	        				data11++;
	        			}
	        			if(atot>=22 && atot<24){
	        				data12++;
	        			}
	        		}
	        	} 
	        	if("ZSPD".equals(map.get("adep").toString())){
	        		if(map.get("atot")!=null && !"".equals(map.get("atot"))){
	        			String[] atotArry = map.get("atot").toString().split(" ");
	        			String[] atotTime = atotArry[1].split(":");
	        			int atot = Integer.parseInt(atotTime[0]);
	        			if(atot>=0 && atot<2){
	        				data01++;
	        			}
	        			if(atot>=2 && atot<4){
	        				data02++;
	        			}
	        			if(atot>=4 && atot<6){
	        				data03++;
	        			}
	        			if(atot>=6 && atot<8){
	        				data04++;
	        			}
	        			if(atot>=8 && atot<10){
	        				data05++;
	        			}
	        			if(atot>=10 && atot<12){
	        				data06++;
	        			}
	        			if(atot>=12 && atot<14){
	        				data07++;
	        			}
	        			if(atot>=14 && atot<16){
	        				data08++;
	        			}
	        			if(atot>=16 && atot<18){
	        				data09++;
	        			}
	        			if(atot>=18 && atot<20){
	        				data010++;
	        			}
	        			if(atot>=20 && atot<22){
	        				data011++;
	        			}
	        			if(atot>=22 && atot<24){
	        				data012++;
	        			}
	        		}
	        	}
	        }
			if(map.get("ades")!=null && !"".equals(map.get("ades"))){
	        	if("ZSSS".equals(map.get("ades").toString())){
	        		if(map.get("aldt")!=null && !"".equals(map.get("aldt"))){
	        			String[] aldtArry = map.get("aldt").toString().split(" ");
	        			String[] aldtTime = aldtArry[1].split(":");
	        			int aldt = Integer.parseInt(aldtTime[0]);
	        			if(aldt>=0 && aldt<2){
	        				data1++;
	        			}
	        			if(aldt>=2 && aldt<4){
	        				data2++;
	        			}
	        			if(aldt>=4 && aldt<6){
	        				data3++;
	        			}
	        			if(aldt>=6 && aldt<8){
	        				data4++;
	        			}
	        			if(aldt>=8 && aldt<10){
	        				data5++;
	        			}
	        			if(aldt>=10 && aldt<12){
	        				data6++;
	        			}
	        			if(aldt>=12 && aldt<14){
	        				data7++;
	        			}
	        			if(aldt>=14 && aldt<16){
	        				data8++;
	        			}
	        			if(aldt>=16 && aldt<18){
	        				data9++;
	        			}
	        			if(aldt>=18 && aldt<20){
	        				data10++;
	        			}
	        			if(aldt>=20 && aldt<22){
	        				data11++;
	        			}
	        			if(aldt>=22 && aldt<24){
	        				data12++;
	        			}
	        		}
	        	} 
	        	if("ZSPD".equals(map.get("ades").toString())){
	        		if(map.get("aldt")!=null && !"".equals(map.get("aldt"))){
	        			String[] aldtArry = map.get("aldt").toString().split(" ");
	        			String[] aldtTime = aldtArry[1].split(":");
	        			int aldt = Integer.parseInt(aldtTime[0]);
	        			if(aldt>=0 && aldt<2){
	        				data01++;
	        			}
	        			if(aldt>=2 && aldt<4){
	        				data02++;
	        			}
	        			if(aldt>=4 && aldt<6){
	        				data03++;
	        			}
	        			if(aldt>=6 && aldt<8){
	        				data04++;
	        			}
	        			if(aldt>=8 && aldt<10){
	        				data05++;
	        			}
	        			if(aldt>=10 && aldt<12){
	        				data06++;
	        			}
	        			if(aldt>=12 && aldt<14){
	        				data07++;
	        			}
	        			if(aldt>=14 && aldt<16){
	        				data08++;
	        			}
	        			if(aldt>=16 && aldt<18){
	        				data09++;
	        			}
	        			if(aldt>=18 && aldt<20){
	        				data010++;
	        			}
	        			if(aldt>=20 && aldt<22){
	        				data011++;
	        			}
	        			if(aldt>=22 && aldt<24){
	        				data012++;
	        			}
	        		}
	        	}
	        }
		}
		Option option = new GsonOption();  
		option.title().text("实时流量统计图").setX("bottom");
        option.tooltip().trigger(Trigger.axis);
        AxisLabel axisLabel = new AxisLabel();
        axisLabel.setInterval(0);
        axisLabel.setRotate(60);
        option.legend("虹桥", "浦东");
    	option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.saveAsImage);
        option.calculable(true);
        Bar bar = new Bar("虹桥");
        bar.data(data1, data2, data3,data4,data5,data6, data7, data8, data9, data10,data11,data12);
        Bar bar1 = new Bar("浦东");
        bar1.data(data01, data02, data03,data04,data05,data06, data07, data08, data09, data010,data011,data012);
    	option.xAxis(new CategoryAxis().data("0-2点", "2-4点", "4-6点","6-8点","8-10点","10-12点","12-14点","14-16点","16-18点","18-20点","20-22点","22-24点").axisLabel(axisLabel));
    	option.color("#2aa8ed","green");
        option.yAxis(new ValueAxis());
        option.series(bar,bar1);
        //类型转换
        JSONObject jsonObj = JSONObject.fromObject(option.toString());
        return jsonObj.toString();
	}
	
	public List<Map<String,String>> getRealTimeFlow(){
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "SELECT A.ADEP,A.ADES,A.ATOT,A.ALDT FROM CDMINFOR A WHERE A.ADEP IN ('ZSSS','ZSPD') AND A.ATOT LIKE '%"+dateStr+"%' OR A.ADES IN ('ZSSS','ZSPD') AND A.ALDT LIKE '%"+dateStr+"%'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	Map<String,String> map = new HashMap<String,String>();
	        	map.put("adep", rs.getString(1));
	        	map.put("ades", rs.getString(2));
	        	map.put("atot", rs.getString(3));
	        	map.put("aldt", rs.getString(4));
	        	listMap.add(map);
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return listMap;
	}
	
	
	/**
	 * 班机延误统计
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void flightDelay() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int data1=0;int data2=0;int data3=0;
	    int data4=0;int data5=0;
		List<Map<String,String>> listMap = getFlightDelay();
		int num = getFlightDelayNum();
		for(Map<String,String> map:listMap){
			String stod = map.get("stod").toString();
			String atot = map.get("atot").toString();
			try {
				Date d1 = format.parse(atot);
				Date d2 = format.parse(stod);
				long diff = d1.getTime() - d2.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
				if(hours<=0){
					data1++;
				}
				if(hours<=1 && hours>0){
					data2++;
				}
				if(hours<=2 && hours>1){
					data3++;
				}
				if(hours<=3 && hours>2){
					data4++;
				}
				if(hours>3){
					data5++;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数  
		String value1="0";
		String value2="0";
		String value3="0";
		String value4="0";
		String value5="0";
		if(num!=0){
			value1 = df.format((float)data1/num*100);
			value2 = df.format((float)data2/num*100);
			value3 = df.format((float)data3/num*100);
			value4 = df.format((float)data4/num*100);
			value5 = df.format((float)data5/num*100);
		}
		ItemStyle dataStyle = new ItemStyle();
        dataStyle.normal().label(new Label().show(false)).labelLine().show(false);

        ItemStyle placeHolderStyle = new ItemStyle();
        placeHolderStyle.normal().color("rgba(0,0,0,0)").label(new Label().show(false)).labelLine().show(false);
        placeHolderStyle.emphasis().color("rgba(0,0,0,0)");
        
        Option option = new GsonOption();  
        option.title().text("")
        .x(80)
        .y(80)
        .itemGap(10)
        .textStyle().color("rgba(30,144,255,0.8)")
        .fontFamily("微软雅黑")
        .fontSize(25)
        .fontWeight("bolder");
		option.tooltip().show(true).formatter("{b} </br>所占比例: {c}%");
		option.legend("正常", "延误1小时", "延误2小时", "延误3小时", "延误3小时以上");
		option.toolbox().show(true).feature(Tool.mark, Tool.saveAsImage);
		
		Pie p1 = new Pie();
		p1.name("班机延误率").data(
                new PieData("正常:"+data1+"架次", value1),
                new PieData("延误1小时:"+data2+"架次", value2),
                new PieData("延误2小时:"+data3+"架次", value3),
                new PieData("延误3小时:"+data4+"架次", value4),
                new PieData("延误3小时以上:"+data5+"架次", value5));
		option.series(p1);
        //类型转换
        JSONObject jsonObj = JSONObject.fromObject(option.toString());
        response.setContentType("application/json;charset=UTF-8");  
        response.setHeader("Cache-Control", "no-cache"); 
        //输出到页面
        response.getWriter().print(jsonObj); 
        System.out.println("结束时间："+format.format(new Date()));
	}
	
	
	/**
	 * 班机延误统计
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public String getWxFlightDelay() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("开始时间："+format.format(new Date()));
		int data1=0;int data2=0;int data3=0;
	    int data4=0;int data5=0;
		List<Map<String,String>> listMap = getFlightDelay();
		System.out.println("获取listMap时间："+format.format(new Date()));
		int num = getFlightDelayNum();
		for(Map<String,String> map:listMap){
			String stod = map.get("stod").toString();
			String atot = map.get("atot").toString();
			try {
				Date d1 = format.parse(atot);
				Date d2 = format.parse(stod);
				long diff = d1.getTime() - d2.getTime();
				long days = diff / (1000 * 60 * 60 * 24);
				long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
				if(hours<=0){
					data1++;
				}
				if(hours<=1 && hours>0){
					data2++;
				}
				if(hours<=2 && hours>1){
					data3++;
				}
				if(hours<=3 && hours>2){
					data4++;
				}
				if(hours>3){
					data5++;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		System.out.println("获取各类data时间："+format.format(new Date()));
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数  
		String value1="0";
		String value2="0";
		String value3="0";
		String value4="0";
		String value5="0";
		if(num!=0){
			value1 = df.format((float)data1/num*100);
			value2 = df.format((float)data2/num*100);
			value3 = df.format((float)data3/num*100);
			value4 = df.format((float)data4/num*100);
			value5 = df.format((float)data5/num*100);
		}
		System.out.println(value1+"aaa"+value2+"bbb"+value3+"ccc"+value4+"ddd"+value5);
		ItemStyle dataStyle = new ItemStyle();
        dataStyle.normal().label(new Label().show(false)).labelLine().show(false);

        ItemStyle placeHolderStyle = new ItemStyle();
        placeHolderStyle.normal().color("rgba(0,0,0,0)").label(new Label().show(false)).labelLine().show(false);
        placeHolderStyle.emphasis().color("rgba(0,0,0,0)");
        
        Option option = new GsonOption();  
        option.title().text("")
//        .x(X.center)
//        .y(Y.top)
        .itemGap(10)
        .textStyle().color("rgba(30,144,255,0.8)")
        .fontFamily("微软雅黑")
        .fontSize(25)
        .fontWeight("bolder");
		option.tooltip().show(true).formatter("{b} </br>所占比例: {c}%");
		option.legend("正常", "延误1小时", "延误2小时", "延误3小时", "延误3小时以上");
		option.toolbox().show(true).feature(Tool.mark, Tool.saveAsImage);
		
		Pie p1 = new Pie();
		p1.name("班机延误率").data(
                new PieData("正常:"+data1+"架次", value1),
                new PieData("延误1小时:"+data2+"架次", value2),
                new PieData("延误2小时:"+data3+"架次", value3),
                new PieData("延误3小时:"+data4+"架次", value4),
                new PieData("延误3小时以上:"+data5+"架次", value5));
		option.series(p1);
        //类型转换
        JSONObject jsonObj = JSONObject.fromObject(option.toString());
        return jsonObj.toString();
	}
	
	public List<Map<String,String>> getFlightDelay(){
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "SELECT A.STOD,A.ATOT FROM CDMINFOR A WHERE A.ADEP IN ('ZSSS','ZSPD') AND A.ATOT LIKE '%"+dateStr+"%'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	Map<String,String> map = new HashMap<String,String>();
	        	map.put("stod", rs.getString(1));
	        	map.put("atot", rs.getString(2));
	        	listMap.add(map);
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return listMap;
	}
	
	public int getFlightDelayNum(){
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int num=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "SELECT COUNT(*) AS COUNT FROM CDMINFOR A WHERE A.ADEP IN ('ZSSS','ZSPD') AND A.ATOT LIKE '%"+dateStr+"%'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	num = Integer.parseInt(rs.getString(1));
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return num;
	}
}
