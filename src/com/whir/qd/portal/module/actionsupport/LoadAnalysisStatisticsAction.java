package com.whir.qd.portal.module.actionsupport;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.AxisLabel;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.google.gson.Gson;
import com.whir.common.util.DataSourceBase;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.PropertiesUtils;
import com.whir.qd.portal.module.po.LoadAnalysisPO;

public class LoadAnalysisStatisticsAction extends BaseActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3930568095233307445L;
	
	
	/**
	 * 负荷分析统计
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public void getLoadAnalysis() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
	  //将list中的对象转换为Json格式的数组
	    List<LoadAnalysisPO> records = new ArrayList<LoadAnalysisPO>(); 
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
    	  sql = "select  a.sector_date,a.sector_name,a.loading_value,b.sector_name,b.loading_value," +
    	  		"c.sector_name,c.loading_value,d.sector_name,d.loading_value,e.sector_name,e.loading_value " +
    	  		"from (select sector_date,sector_name,loading_value from qd_sectorload where sector_name='ACC1' and sector_date like '%2018-02-02%') " +
    	  		"a inner join (select sector_date,sector_name,loading_value from qd_sectorload where sector_name='ACC3' and sector_date like '%2018-02-02%') " +
    	  		"b on a.sector_date=b.sector_date inner join (select sector_date,sector_name,loading_value from qd_sectorload where sector_name='ACC6' and sector_date like '%2018-02-02%') " +
    	  		"c on a.sector_date=c.sector_date inner join (select sector_date,sector_name,loading_value from qd_sectorload where sector_name='APP1' and sector_date like '%2018-02-02%') " +
    	  		"d on a.sector_date=d.sector_date inner join(select sector_date,sector_name,loading_value from qd_sectorload where sector_name='APP1' and sector_date like '%2018-02-02%') " +
    	  		"e on a.sector_date=e.sector_date order by a.sector_date asc";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	LoadAnalysisPO r = new LoadAnalysisPO();
	        	r.setDates(rs.getString(1));
	        	r.setSectorNameAcc1(rs.getString(2));
	        	r.setSectorNameAcc3(rs.getString(3));
	        	r.setSectorNameAcc6(rs.getString(4));
	        	r.setSectorNameApp1(rs.getString(5));
	        	r.setSectorNameApp2(rs.getString(6));
	        	r.setLoadingValueAcc1(rs.getString(7));
	        	r.setLoadingValueAcc3(rs.getString(8));
	        	r.setLoadingValueAcc6(rs.getString(9));
	        	r.setLoadingValueApp1(rs.getString(10));
	        	r.setLoadingValueApp2(rs.getString(11));
	        	records.add(r);
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
        Gson gson = new Gson();                    
        String json = gson.toJson(records); 
        System.out.println("json::"+json);
        //将json数据返回给客户端
        response.setContentType("text/html; charset=utf-8");
        response.getWriter().write(json);
	}
	
	
	/**
	 * 运行态势统计图数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public void getOperationSituation() {
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
}
