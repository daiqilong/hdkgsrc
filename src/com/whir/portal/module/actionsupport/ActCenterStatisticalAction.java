package com.whir.portal.module.actionsupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;

public class ActCenterStatisticalAction extends BaseActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8100466905892136929L;
	private static Logger logger = Logger.getLogger(ActCenterStatisticalAction.class.getName());
	
	/**
	 *  扇区上报表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String sectorReportTable() {
		List<String> sectorName = getSectorName();
	    if (sectorName != null){
	      this.request.setAttribute("sectorNameList", sectorName);
	    } else {
	      this.request.setAttribute("sectorNameList", new ArrayList());
	    }
	    return "sectorReportTable";
	}
	
	public List<String> getSectorName() {
        List<String> list = new ArrayList<String>();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String str = "select distinct whir$sqtjb_f4076 from whir$sqtjb";
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(str);
    		while (rs.next()) {
  	    	  String sectorName = rs.getString(1);
  	    	  list.add(sectorName);
    		}
		}catch (SQLException ex) {
			ex.printStackTrace();
			try{
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null){
		        	conn.close();
		        }
		     }catch (SQLException localSQLException1){
		     }
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
		 return list;
	}
	
	/**
	 * 扇区上报表统计数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception 
	 */
	public String sectorReportTableData() throws Exception {
		String queryDate = request.getParameter("queryDate");
		String sectorName = request.getParameter("sectorName");
		
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select whir$sqtjb_f4075,whir$sqtjb_f4156,whir$sqtjb_f4157,whir$sqtjb_f4076 ")
	    .append(" from whir$sqtjb where whir$sqtjb_f4076 in ("+sectorName+") and whir$sqtjb_f4075 like '%"+queryDate+"%'");
	    
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
  	    	  Map<String, String> map = new HashMap<String, String>();
  	    	  String statisDate = rs.getString(1);
  	    	  String sectorDayTraffic = rs.getString(2);
  	    	  String sectorHoursTraffic = rs.getString(3);
  	    	  String sectorNameStr = rs.getString(4);
  	    	  
  	    	  map.put("sectorName", sectorNameStr);
  	    	  map.put("statisDate", statisDate);
  	    	  map.put("sectorDayTraffic", sectorDayTraffic);
  	    	  map.put("sectorHoursTraffic", sectorHoursTraffic);
  	    	  list.add(map);
    		}
    		System.out.println("list.size():::::"+list.size());
    		String json = "";
 	   		if(list.size()!=0){
	    		 List<Map<String,String>> result = new ArrayList<Map<String,String>>();
	    		 String[] sectorNameArray = sectorName.split(",");
	    		 for(String str:sectorNameArray){
	    			 int day=0;
    				 int daySumTraffic=0;
    				 int hoursSumTraffic=0;
    				 String key = str.substring(1, str.length()-1);
			   		 for(Map<String, String> map1 :list){
			   			 if(key.equals(map1.get("sectorName"))){
			   				 day++;
			   				 daySumTraffic=daySumTraffic+Integer.parseInt(map1.get("sectorDayTraffic"));
				   			 hoursSumTraffic=hoursSumTraffic+Integer.parseInt(map1.get("sectorHoursTraffic"));
			   			 }
			   		 }
			   		 
			   		 Map<String,String> map2 = new HashMap<String,String>();
			   		 map2.put("statisDate", queryDate);
			   		 map2.put("sectorName", key);
			   		 map2.put("dayAverageTraffic", String.valueOf(daySumTraffic/day));
			   		 map2.put("hoursAverageTraffic",  String.valueOf(hoursSumTraffic/day));
			   		 result.add(map2);
	    		 }
	    		 
	    		 Collections.sort(result, new Comparator<Map<String, String>>() {
	    	            public int compare(Map<String, String> o1, Map<String, String> o2) {
	    	            	String map1value = o1.get("dayAverageTraffic");
	    	                String map2value = o2.get("dayAverageTraffic");
	    	                return map2value.compareTo(map1value);    
	    	            }
	    		 });
    			 JacksonUtil util = new JacksonUtil();
		   			// 这里需要修改ejb,加个字段
		   		 json = util.writeListToJSON(
		   				new String[] {"seqNum","statisDate","sectorName", "dayAverageTraffic","hoursAverageTraffic"}, result);
		   		 int recordCount = result.size();
		   		 int pageCount = (recordCount / pageSize);
		   	     int mod = recordCount % pageSize;
		   	     if (mod != 0)
		   	    	 pageCount += 1;
		   	    	json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
	   				+ "},data:" + json + "}";
	   	    } else {
	   	    	json = "{pager:{pageCount:0,recordCount:0},data:[]}";
	   	    }
	   		printResult(this.G_SUCCESS, json);
		}catch (SQLException ex) {
			ex.printStackTrace();
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
		return null;
	}
	
	public String toExcel1() throws Exception {
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   String queryDate = request.getParameter("queryDate");
		   String sectorName = request.getParameter("sectorName");
		   int pageSize = CommonUtils.getUserPageSize(request);
		   int currentPage = 0;
	       if (request.getParameter("startPage") != null) {
	        	currentPage = Integer.parseInt(request.getParameter("startPage"));
	       }
	        
	        
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = "扇区数据统计";
				sheetName = sheetName.replaceAll(":", "").replaceAll("[)]", "")
				.replaceAll("[(]", "");
				// 这里解释一下
				// attachment; 这个代表要下载的，如果去掉就编程直接打开了
				// filename是文件名，另存为或者下载时，为默认的文件名
				response.setHeader("Content-Disposition", "attachment; filename="
				+ new String(sheetName.getBytes("GBK"), "ISO-8859-1")+strDate
				+ ".xls");
				String tempDir = System.getProperty("java.io.tmpdir");

		        WorkbookSettings wbSetting = new WorkbookSettings();
		        wbSetting.setGCDisabled(true);
		        wbSetting.setUseTemporaryFileDuringWrite(true);
		        wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempDir));

		        String tempfilename = System.currentTimeMillis() + "_export.xls";

		        tempFile = new File(tempDir + "/" + tempfilename);
				// 创建工作薄(Workbook)对象
				wwb = Workbook.createWorkbook(tempFile, wbSetting);
				// 创建一个可写入的工作表
				WritableSheet ws = wwb.createSheet(sheetName, 0);
				WritableFont   wf2   =   new   WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
		        WritableCellFormat wcfTitle = new WritableCellFormat(wf2);
		        wcfTitle.setBackground(jxl.format.Colour.IVORY);  //象牙白
		        wcfTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
		        wcfTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐
		        CellView navCellView = new CellView();  
		        navCellView.setAutosize(true); //设置自动大小
		        navCellView.setSize(18);
		        
		        Label lab = null;
		        lab = new Label(0, 0, "统计日期",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "扇区名称",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "日均流量",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(3, 0, "扇区高峰日均小时流量",wcfTitle); 
				ws.addCell(lab);
				List<Map<String, String>> result = getBodyData1(queryDate,sectorName,pageSize,currentPage);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
					System.out.println("bodymap::::"+bodymap.get("statisDate"));
					System.out.println("bodymap::::"+bodymap.get("sectorName"));
					System.out.println("bodymap::::"+bodymap.get("dayAverageTraffic"));
					System.out.println("bodymap::::"+bodymap.get("hoursAverageTraffic"));
		    	    lab = new Label(0,j+1,bodymap.get("statisDate"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("sectorName"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("dayAverageTraffic"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(3,j+1,bodymap.get("hoursAverageTraffic"));
		    	    ws.addCell(lab);  
		        }
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
				in = new BufferedInputStream(new FileInputStream(tempDir + "/" + tempfilename));
		        out = new BufferedOutputStream(os);

	            byte[] buf = new byte[8192];
		        int n = -1;
		        while (-1 != (n = in.read(buf, 0, buf.length))) {
			        out.write(buf, 0, n);
		        }
		        out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}finally {
		      try {
		          if (out != null)
		            out.close();
		        }
		        catch (Exception localException3)
		        {
		        }
		        try {
		          if (in != null)
		            in.close();
		        }
		        catch (Exception localException4)
		        {
		        }
		        if ((tempFile != null) && 
		          (tempFile.exists())) {
		          tempFile.delete();
		        }

	       }
			return null;
		}
	
	
	public List<Map<String, String>> getBodyData1(String queryDate,String sectorName,int pageSize,int currentPage)throws SQLException{
		    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		    List<Map<String,String>> result = null;
		    StringBuffer buffer = new StringBuffer();
		    DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    Statement stmt = null;
		    ResultSet rs = null;
		    buffer.append("select whir$sqtjb_f4075,whir$sqtjb_f4156,whir$sqtjb_f4157,whir$sqtjb_f4076 ")
		    .append(" from whir$sqtjb where whir$sqtjb_f4076 in ("+sectorName+") and whir$sqtjb_f4075 like '%"+queryDate+"%'");
		    
			try{
				conn = dsb.getDataSource().getConnection();
		    	stmt = conn.createStatement();
		    	rs = stmt.executeQuery(buffer.toString());
	    		while (rs.next()) {
	  	    	  Map<String, String> map = new HashMap<String, String>();
	  	    	  String statisDate = rs.getString(1);
	  	    	  String sectorDayTraffic = rs.getString(2);
	  	    	  String sectorHoursTraffic = rs.getString(3);
	  	    	  String sectorNameStr = rs.getString(4);
	  	    	  
	  	    	  map.put("sectorName", sectorNameStr);
	  	    	  map.put("statisDate", statisDate);
	  	    	  map.put("sectorDayTraffic", sectorDayTraffic);
	  	    	  map.put("sectorHoursTraffic", sectorHoursTraffic);
	  	    	  list.add(map);
	    		}
	    		System.out.println("list.size():::::"+list.size());
	 	   		if(list.size()!=0){
	 	   			 result = new ArrayList<Map<String,String>>();
		    		 String[] sectorNameArray = sectorName.split(",");
		    		 for(String str:sectorNameArray){
		    			 int day=0;
	    				 int daySumTraffic=0;
	    				 int hoursSumTraffic=0;
	    				 String key = str.substring(1, str.length()-1);
				   		 for(Map<String, String> map1 :list){
				   			 if(key.equals(map1.get("sectorName"))){
				   				 day++;
				   				 daySumTraffic=daySumTraffic+Integer.parseInt(map1.get("sectorDayTraffic"));
					   			 hoursSumTraffic=hoursSumTraffic+Integer.parseInt(map1.get("sectorHoursTraffic"));
				   			 }
				   		 }
				   		 
				   		 Map<String,String> map2 = new HashMap<String,String>();
				   		 map2.put("statisDate", queryDate);
				   		 map2.put("sectorName", key);
				   		 map2.put("dayAverageTraffic", String.valueOf(daySumTraffic/day));
				   		 map2.put("hoursAverageTraffic",  String.valueOf(hoursSumTraffic/day));
				   		 result.add(map2);
		    		 }
		    		 
		    		 Collections.sort(result, new Comparator<Map<String, String>>() {
		    	            public int compare(Map<String, String> o1, Map<String, String> o2) {
		    	            	String map1value = o1.get("dayAverageTraffic");
		    	                String map2value = o2.get("dayAverageTraffic");
		    	                return map2value.compareTo(map1value);    
		    	            }
		    		 });
		   	    }
			}catch (SQLException ex) {
				ex.printStackTrace();
			 }finally{
		    	try{
		    		if (stmt != null){
		    			stmt.close();
		    			}
		    		if (conn != null){
		    			conn.close();
		    		}
		    	}catch (SQLException localSQLException2) {
		    	}
			 }
			return result;
	}
	/**
	 *  航路段上报表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String routePeriodReportTable() {
		List<String> unitName = getUnitName("whir$kgzxhldtjb","whir$kgzxhldtjb_f4099");
	    if (unitName != null){
	      this.request.setAttribute("unitNameList", unitName);
	    } else {
	      this.request.setAttribute("unitNameList", new ArrayList());
	    }
	    List<String> routePeriodName = getRoutePeriodOrPoint("whir$kgzxhldtjb","whir$kgzxhldtjb_f4095");
	    if (routePeriodName != null){
	      this.request.setAttribute("routePeriodList", routePeriodName);
	    } else {
	      this.request.setAttribute("routePeriodList", new ArrayList());
	    }
	    return "routePeriodReportTable";
	}
	
	
	public List<String> getUnitName(String tableName,String fieldName) {
        List<String> list = new ArrayList<String>();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String str = "select distinct "+fieldName+" from "+tableName;
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(str);
    		while (rs.next()) {
  	    	  String unitName = rs.getString(1);
  	    	  list.add(unitName);
    		}
		}catch (SQLException ex) {
			ex.printStackTrace();
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
		 return list;
	}
	
	public List<String> getRoutePeriodOrPoint(String tableName,String fieldName) {
        List<String> list = new ArrayList<String>();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String str = "select distinct "+fieldName+" from "+tableName;
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(str);
    		while (rs.next()) {
  	    	  String sectorName = rs.getString(1);
  	    	  list.add(sectorName);
    		}
		}catch (SQLException ex) {
			ex.printStackTrace();
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
		 return list;
	}
	
	/**
	 * 航路段上报表数据统计
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String routePeriodReportTableData() {
		String queryDate = request.getParameter("queryDate");
		String routePeriodName = request.getParameter("routePeriodName");
		String units = request.getParameter("unit");
		
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String sql  ="select whir$kgzxhldtjb_f4094,whir$kgzxhldtjb_f4096,whir$kgzxhldtjb_f4097,whir$kgzxhldtjb_f4095,whir$kgzxhldtjb_f4099  from " +
	   		"whir$kgzxhldtjb where whir$kgzxhldtjb_f4095 in ("+routePeriodName+") and whir$kgzxhldtjb_f4094 like '%"+queryDate+"%' "+
	   		"and whir$kgzxhldtjb_f4099 in ("+units+")";
	    System.out.println("sql::::::"+sql);
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(sql);
    		while (rs.next()) {
  	    	  Map<String, String> map = new HashMap<String, String>();
  	    	  String statisDate = rs.getString(1);
  	    	  String routePeriodDayTraffic = rs.getString(2);
  	    	  String routePeriodHoursTraffic = rs.getString(3);
  	    	  String routePeriodNameStr = rs.getString(4);
  	    	  String unitNameStr = rs.getString(5);
  	    	  
  	    	  map.put("unitName", unitNameStr);
  	    	  map.put("statisDate", statisDate);
  	    	  map.put("routePeriodName", routePeriodNameStr);
  	    	  map.put("routePeriodDayTraffic", routePeriodDayTraffic);
  	    	  map.put("routePeriodHoursTraffic", routePeriodHoursTraffic);
  	    	  list.add(map);
    		}
    		System.out.println("list.size()::::"+list.size());
    		String json = "";
 	   		if(list.size()!=0){
	    		 List<Map<String,String>> result = new ArrayList<Map<String,String>>();
	    		 String[] routePeriodNameArray = routePeriodName.split(",");
	    		 String[] unitsArray = units.split(",");
	    		 for(String unitStr:unitsArray){
	    			 String unitKey = unitStr.substring(1, unitStr.length()-1);
    				 System.out.println("unitKey:::::"+unitKey);
    				 
    				 for(String routePeriodNameStr:routePeriodNameArray){
	    				 int day=0;
	    				 int daySumTraffic=0;
	    				 int hoursSumTraffic=0;
	    				 String routePeriodNameKey = routePeriodNameStr.substring(1, routePeriodNameStr.length()-1);
	    				 for(Map<String, String> map1 :list){
	    		   			 if(unitKey.equals(map1.get("unitName")) && routePeriodNameKey.equals(map1.get("routePeriodName"))){
	    		   				 day++;
	    		   				 daySumTraffic=daySumTraffic+Integer.parseInt(map1.get("routePeriodDayTraffic"));
	       		   			     hoursSumTraffic=hoursSumTraffic+Integer.parseInt(map1.get("routePeriodHoursTraffic"));
	    		   			 }
	    		   		 }
	    				 if(day!=0){
	    					 Map<String,String> map2 = new HashMap<String,String>();
					   		 map2.put("statisDate", queryDate);
					   		 map2.put("unit", unitKey);
					   		 map2.put("routePeriodName", routePeriodNameKey);
					   		 map2.put("dayAverageTraffic", String.valueOf(daySumTraffic/day));
					   		 map2.put("hoursAverageTraffic",  String.valueOf(hoursSumTraffic/day));
					   		 result.add(map2);
	    				 }
	    			 }
	    		 }
	    		 Collections.sort(result, new Comparator<Map<String, String>>() {
    	            public int compare(Map<String, String> o1, Map<String, String> o2) {
    	            	String map1value = o1.get("dayAverageTraffic");
    	                String map2value = o2.get("dayAverageTraffic");
    	                return map2value.compareTo(map1value);    
    	            }
    	         });
	    		 System.out.println("result.size()::::"+result.size());
	    		 
		   		 JacksonUtil util = new JacksonUtil();
		   			// 这里需要修改ejb,加个字段
		   		 json = util.writeListToJSON(
		   				new String[] {"seqNum","statisDate","unit","routePeriodName", "dayAverageTraffic","hoursAverageTraffic"}, result);
		   		 int recordCount = result.size();
		   		 int pageCount = (recordCount / pageSize);
		   	     int mod = recordCount % pageSize;
		   	     if (mod != 0)
		   	    	 pageCount += 1;
		   	    	json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
	   				+ "},data:" + json + "}";
	   	    } else {
	   	    	json = "{pager:{pageCount:0,recordCount:0},data:[]}";
	   	    }
	   		printResult(this.G_SUCCESS, json);
		}catch (SQLException ex) {
			ex.printStackTrace();
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
		 return null;
	}
	
	
	public String toExcel2() throws Exception {
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   String queryDate = request.getParameter("queryDate");
		   String routePeriodName = request.getParameter("routePeriodName");
		   String units = request.getParameter("unit");
			
		   int pageSize = CommonUtils.getUserPageSize(request);
		   int currentPage = 0;
	       if (request.getParameter("startPage") != null) {
	        	currentPage = Integer.parseInt(request.getParameter("startPage"));
	       }
	        
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = "航路段上报表数据统计";
				sheetName = sheetName.replaceAll(":", "").replaceAll("[)]", "")
				.replaceAll("[(]", "");
				// 这里解释一下
				// attachment; 这个代表要下载的，如果去掉就编程直接打开了
				// filename是文件名，另存为或者下载时，为默认的文件名
				response.setHeader("Content-Disposition", "attachment; filename="
				+ new String(sheetName.getBytes("GBK"), "ISO-8859-1")+strDate
				+ ".xls");
				String tempDir = System.getProperty("java.io.tmpdir");

		        WorkbookSettings wbSetting = new WorkbookSettings();
		        wbSetting.setGCDisabled(true);
		        wbSetting.setUseTemporaryFileDuringWrite(true);
		        wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempDir));

		        String tempfilename = System.currentTimeMillis() + "_export.xls";

		        tempFile = new File(tempDir + "/" + tempfilename);
				// 创建工作薄(Workbook)对象
				wwb = Workbook.createWorkbook(tempFile, wbSetting);
				// 创建一个可写入的工作表
				WritableSheet ws = wwb.createSheet(sheetName, 0);
				WritableFont   wf2   =   new   WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
		        WritableCellFormat wcfTitle = new WritableCellFormat(wf2);
		        wcfTitle.setBackground(jxl.format.Colour.IVORY);  //象牙白
		        wcfTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
		        wcfTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐
		        CellView navCellView = new CellView();  
		        navCellView.setAutosize(true); //设置自动大小
		        navCellView.setSize(18);
		        
		        Label lab = null;
		        lab = new Label(0, 0, "统计日期",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "单位",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "航路段名称",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(3, 0, "航路段日均流量",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(4, 0, "航路段高峰日均小时流量",wcfTitle); 
				ws.addCell(lab);
				
				List<Map<String, String>> result = getBodyData2(queryDate,routePeriodName,units,pageSize,currentPage);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
		    	    lab = new Label(0,j+1,bodymap.get("statisDate"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("unit"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("routePeriodName"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(3,j+1,bodymap.get("dayAverageTraffic"));
		    	    ws.addCell(lab);
		    	    lab = new Label(4,j+1,bodymap.get("hoursAverageTraffic"));
		    	    ws.addCell(lab);  
		        }
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
				in = new BufferedInputStream(new FileInputStream(tempDir + "/" + tempfilename));
		        out = new BufferedOutputStream(os);

	            byte[] buf = new byte[8192];
		        int n = -1;
		        while (-1 != (n = in.read(buf, 0, buf.length))) {
			        out.write(buf, 0, n);
		        }
		        out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}finally {
		      try {
		          if (out != null)
		            out.close();
		        }
		        catch (Exception localException3)
		        {
		        }
		        try {
		          if (in != null)
		            in.close();
		        }
		        catch (Exception localException4)
		        {
		        }
		        if ((tempFile != null) && 
		          (tempFile.exists())) {
		          tempFile.delete();
		        }

	       }
			return null;
		}
	
	
	public List<Map<String, String>> getBodyData2(String queryDate,String routePeriodName,String units,int pageSize,int currentPage)throws SQLException{
		    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		    List<Map<String,String>> result = null;
		    DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    Statement stmt = null;
		    ResultSet rs = null;
		    String sql  ="select whir$kgzxhldtjb_f4094,whir$kgzxhldtjb_f4096,whir$kgzxhldtjb_f4097,whir$kgzxhldtjb_f4095,whir$kgzxhldtjb_f4099  from " +
		   		"whir$kgzxhldtjb where whir$kgzxhldtjb_f4095 in ("+routePeriodName+") and whir$kgzxhldtjb_f4094 like '%"+queryDate+"%' "+
		   		"and whir$kgzxhldtjb_f4099 in ("+units+")";
			try{
				conn = dsb.getDataSource().getConnection();
		    	stmt = conn.createStatement();
		    	rs = stmt.executeQuery(sql);
	    		while (rs.next()) {
	  	    	  Map<String, String> map = new HashMap<String, String>();
	  	    	  String statisDate = rs.getString(1);
	  	    	  String routePeriodDayTraffic = rs.getString(2);
	  	    	  String routePeriodHoursTraffic = rs.getString(3);
	  	    	  String routePeriodNameStr = rs.getString(4);
	  	    	  String unitNameStr = rs.getString(5);
	  	    	  
	  	    	  map.put("unitName", unitNameStr);
	  	    	  map.put("statisDate", statisDate);
	  	    	  map.put("routePeriodName", routePeriodNameStr);
	  	    	  map.put("routePeriodDayTraffic", routePeriodDayTraffic);
	  	    	  map.put("routePeriodHoursTraffic", routePeriodHoursTraffic);
	  	    	  list.add(map);
	    		}
	    		System.out.println("list.size()::::"+list.size());
	 	   		if(list.size()!=0){
		    		 result = new ArrayList<Map<String,String>>();
		    		 String[] routePeriodNameArray = routePeriodName.split(",");
		    		 String[] unitsArray = units.split(",");
		    		 for(String unitStr:unitsArray){
		    			 String unitKey = unitStr.substring(1, unitStr.length()-1);
	    				 System.out.println("unitKey:::::"+unitKey);
	    				 
	    				 for(String routePeriodNameStr:routePeriodNameArray){
		    				 int day=0;
		    				 int daySumTraffic=0;
		    				 int hoursSumTraffic=0;
		    				 String routePeriodNameKey = routePeriodNameStr.substring(1, routePeriodNameStr.length()-1);
		    				 for(Map<String, String> map1 :list){
		    		   			 if(unitKey.equals(map1.get("unitName")) && routePeriodNameKey.equals(map1.get("routePeriodName"))){
		    		   				 day++;
		    		   				 daySumTraffic=daySumTraffic+Integer.parseInt(map1.get("routePeriodDayTraffic"));
		       		   			     hoursSumTraffic=hoursSumTraffic+Integer.parseInt(map1.get("routePeriodHoursTraffic"));
		    		   			 }
		    		   		 }
		    				 if(day!=0){
		    					 Map<String,String> map2 = new HashMap<String,String>();
						   		 map2.put("statisDate", queryDate);
						   		 map2.put("unit", unitKey);
						   		 map2.put("routePeriodName", routePeriodNameKey);
						   		 map2.put("dayAverageTraffic", String.valueOf(daySumTraffic/day));
						   		 map2.put("hoursAverageTraffic",  String.valueOf(hoursSumTraffic/day));
						   		 result.add(map2);
		    				 }
		    			 }
		    		 }
		    		 Collections.sort(result, new Comparator<Map<String, String>>() {
	    	            public int compare(Map<String, String> o1, Map<String, String> o2) {
	    	            	String map1value = o1.get("dayAverageTraffic");
	    	                String map2value = o2.get("dayAverageTraffic");
	    	                return map2value.compareTo(map1value);    
	    	            }
	    	         });
		   	    }
			}catch (SQLException ex) {
				ex.printStackTrace();
			 }finally{
		    	try{
		    		if (stmt != null){
		    			stmt.close();
		    			}
		    		if (conn != null){
		    			conn.close();
		    		}
		    	}catch (SQLException localSQLException2) {
		    	}
			 }
			return result;
	}
	
	/**
	 *  航路点上报表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String routePointReportTable()
	  {
		List<String> unitName = getUnitName("whir$hangludian","whir$hangludian_f4074");
	    if (unitName != null){
	      this.request.setAttribute("unitNameList", unitName);
	    } else {
	      this.request.setAttribute("unitNameList", new ArrayList());
	    }
	    List<String> routePointName = getRoutePeriodOrPoint("whir$hangludian","whir$hangludian_f4070");
	    if (routePointName != null){
	      this.request.setAttribute("routePointList", routePointName);
	    } else {
	      this.request.setAttribute("routePointList", new ArrayList());
	    }
	    return "routePointReportTable";
	  }
	
	/**
	 * 航路点上报表数据统计
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String routePointReportTableData() {
		String queryDate = request.getParameter("queryDate");
		String routePointName = request.getParameter("routePointName");
		String units = request.getParameter("unit");
		
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String sql  ="select whir$hangludian_f4069,whir$hangludian_f4071,whir$hangludian_f4072,whir$hangludian_f4070,whir$hangludian_f4074  from " +
	    		"whir$hangludian where whir$hangludian_f4070 in ("+routePointName+") and " +
	    		"whir$hangludian_f4069 like '%"+queryDate+"%' and whir$hangludian_f4074 in ("+units+")";
	    
	    System.out.println("sql:::::"+sql);
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(sql);
    		while (rs.next()) {
  	    	  Map<String, String> map = new HashMap<String, String>();
  	    	  String statisDate = rs.getString(1);
  	    	  String routePointDayTraffic = rs.getString(2);
  	    	  String routePointHoursTraffic = rs.getString(3);
  	    	  String routePointNameStr = rs.getString(4);
  	    	  String unitNameStr = rs.getString(5);
  	    	  
  	    	  map.put("unitName", unitNameStr);
  	    	  map.put("statisDate", statisDate);
  	    	  map.put("routePointName", routePointNameStr);
  	    	  map.put("routePointDayTraffic", routePointDayTraffic);
  	    	  map.put("routePointHoursTraffic", routePointHoursTraffic);
  	    	  list.add(map);
    		}
    		System.out.println("list:::::"+list.size());
    		
    		String json = "";
 	   		if(list.size()!=0){
	    		 List<Map<String,String>> result = new ArrayList<Map<String,String>>();
	    		 String[] routePointNameArray = routePointName.split(",");
	    		 String[] unitsArray = units.split(",");
	    		 for(String unitStr:unitsArray){
	    			 String unitKey = unitStr.substring(1, unitStr.length()-1);
    				 System.out.println("unitKey:::::"+unitKey);
    				 
    				 for(String routePointNameStr:routePointNameArray){
	    				 int day=0;
	    				 int daySumTraffic=0;
	    				 int hoursSumTraffic=0;
	    				 String routePointNameKey = routePointNameStr.substring(1, routePointNameStr.length()-1);
	    				 for(Map<String, String> map1 :list){
	    		   			 if(unitKey.equals(map1.get("unitName")) && routePointNameKey.equals(map1.get("routePointName"))){
	    		   				 day++;
	    		   				 daySumTraffic=daySumTraffic+Integer.parseInt(map1.get("routePointDayTraffic"));
	       		   			     hoursSumTraffic=hoursSumTraffic+Integer.parseInt(map1.get("routePointHoursTraffic"));
	    		   			 }
	    		   		 }
	    				 if(day!=0){
	    					 Map<String,String> map2 = new HashMap<String,String>();
	        		   		 map2.put("statisDate", queryDate);
	        		   		 map2.put("unit", unitKey);
	        		   		 map2.put("routePointName", routePointNameKey);
	        		   		 map2.put("dayAverageTraffic", String.valueOf(daySumTraffic/day));
	        		   		 map2.put("hoursAverageTraffic",  String.valueOf(hoursSumTraffic/day));
	        		   		 result.add(map2);	 
	    				 }
	    			 }
	    		 }
	    		 Collections.sort(result, new Comparator<Map<String, String>>() {
	    	            public int compare(Map<String, String> o1, Map<String, String> o2) {
	    	            	String map1value = o1.get("dayAverageTraffic");
	    	                String map2value = o2.get("dayAverageTraffic");
	    	                return map2value.compareTo(map1value);    
	    	            }
    	         });
	    		 System.out.println("result.size()::::"+result.size());
		   		  JacksonUtil util = new JacksonUtil();
		   			// 这里需要修改ejb,加个字段
		   		  json = util.writeListToJSON(
		   				new String[] {"seqNum","statisDate","unit","routePointName", "dayAverageTraffic","hoursAverageTraffic"}, result);
		   		  int recordCount = result.size();
		   		  int pageCount = (recordCount / pageSize);
		   	      int mod = recordCount % pageSize;
		   	      if (mod != 0)
		   	    	 pageCount += 1;
		   	    	json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
	   				+ "},data:" + json + "}";
	   	    } else {
	   	    	json = "{pager:{pageCount:0,recordCount:0},data:[]}";
	   	    }
	   		printResult(this.G_SUCCESS, json);
		}catch (SQLException ex) {
			ex.printStackTrace();
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
		 return null;
	}
	
	
	public String toExcel3() throws Exception {
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   String queryDate = request.getParameter("queryDate");
		   String routePointName = request.getParameter("routePointName");
		   String units = request.getParameter("unit");
			
		   int pageSize = CommonUtils.getUserPageSize(request);
		   int currentPage = 0;
	       if (request.getParameter("startPage") != null) {
	        	currentPage = Integer.parseInt(request.getParameter("startPage"));
	       }
	        
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = "航路点上报表数据统计";
				sheetName = sheetName.replaceAll(":", "").replaceAll("[)]", "")
				.replaceAll("[(]", "");
				// 这里解释一下
				// attachment; 这个代表要下载的，如果去掉就编程直接打开了
				// filename是文件名，另存为或者下载时，为默认的文件名
				response.setHeader("Content-Disposition", "attachment; filename="
				+ new String(sheetName.getBytes("GBK"), "ISO-8859-1")+strDate
				+ ".xls");
				String tempDir = System.getProperty("java.io.tmpdir");

		        WorkbookSettings wbSetting = new WorkbookSettings();
		        wbSetting.setGCDisabled(true);
		        wbSetting.setUseTemporaryFileDuringWrite(true);
		        wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempDir));

		        String tempfilename = System.currentTimeMillis() + "_export.xls";

		        tempFile = new File(tempDir + "/" + tempfilename);
				// 创建工作薄(Workbook)对象
				wwb = Workbook.createWorkbook(tempFile, wbSetting);
				// 创建一个可写入的工作表
				WritableSheet ws = wwb.createSheet(sheetName, 0);
				WritableFont   wf2   =   new   WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
		        WritableCellFormat wcfTitle = new WritableCellFormat(wf2);
		        wcfTitle.setBackground(jxl.format.Colour.IVORY);  //象牙白
		        wcfTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
		        wcfTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐
		        CellView navCellView = new CellView();  
		        navCellView.setAutosize(true); //设置自动大小
		        navCellView.setSize(18);
		        
		        Label lab = null;
		        lab = new Label(0, 0, "统计日期",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "单位",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "航路点名称",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(3, 0, "航路段日均流量",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(4, 0, "航路段高峰日均小时流量",wcfTitle); 
				ws.addCell(lab);
				
				List<Map<String, String>> result = getBodyData3(queryDate,routePointName,units,pageSize,currentPage);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
		    	    lab = new Label(0,j+1,bodymap.get("statisDate"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("unit"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("routePointName"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(3,j+1,bodymap.get("dayAverageTraffic"));
		    	    ws.addCell(lab);
		    	    lab = new Label(4,j+1,bodymap.get("hoursAverageTraffic"));
		    	    ws.addCell(lab);  
		        }
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
				in = new BufferedInputStream(new FileInputStream(tempDir + "/" + tempfilename));
		        out = new BufferedOutputStream(os);

	            byte[] buf = new byte[8192];
		        int n = -1;
		        while (-1 != (n = in.read(buf, 0, buf.length))) {
			        out.write(buf, 0, n);
		        }
		        out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}finally {
		      try {
		          if (out != null)
		            out.close();
		        }
		        catch (Exception localException3)
		        {
		        }
		        try {
		          if (in != null)
		            in.close();
		        }
		        catch (Exception localException4)
		        {
		        }
		        if ((tempFile != null) && 
		          (tempFile.exists())) {
		          tempFile.delete();
		        }

	       }
			return null;
		}
	
	
	public List<Map<String, String>> getBodyData3(String queryDate,String routePointName,String units,int pageSize,int currentPage)throws SQLException{
		    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		    List<Map<String,String>> result = null;
		    DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    Statement stmt = null;
		    ResultSet rs = null;
		    String sql  ="select whir$hangludian_f4069,whir$hangludian_f4071,whir$hangludian_f4072,whir$hangludian_f4070,whir$hangludian_f4074  from " +
		    		"whir$hangludian where whir$hangludian_f4070 in ("+routePointName+") and " +
		    		"whir$hangludian_f4069 like '%"+queryDate+"%' and whir$hangludian_f4074 in ("+units+")";
		    
		    System.out.println("sql:::::"+sql);
			try{
				conn = dsb.getDataSource().getConnection();
		    	stmt = conn.createStatement();
		    	rs = stmt.executeQuery(sql);
	    		while (rs.next()) {
	  	    	  Map<String, String> map = new HashMap<String, String>();
	  	    	  String statisDate = rs.getString(1);
	  	    	  String routePointDayTraffic = rs.getString(2);
	  	    	  String routePointHoursTraffic = rs.getString(3);
	  	    	  String routePointNameStr = rs.getString(4);
	  	    	  String unitNameStr = rs.getString(5);
	  	    	  
	  	    	  map.put("unitName", unitNameStr);
	  	    	  map.put("statisDate", statisDate);
	  	    	  map.put("routePointName", routePointNameStr);
	  	    	  map.put("routePointDayTraffic", routePointDayTraffic);
	  	    	  map.put("routePointHoursTraffic", routePointHoursTraffic);
	  	    	  list.add(map);
	    		}
	    		System.out.println("list:::::"+list.size());
	    		
	    		String json = "";
	 	   		if(list.size()!=0){
		    		 result = new ArrayList<Map<String,String>>();
		    		 String[] routePointNameArray = routePointName.split(",");
		    		 String[] unitsArray = units.split(",");
		    		 for(String unitStr:unitsArray){
		    			 String unitKey = unitStr.substring(1, unitStr.length()-1);
	    				 System.out.println("unitKey:::::"+unitKey);
	    				 
	    				 for(String routePointNameStr:routePointNameArray){
		    				 int day=0;
		    				 int daySumTraffic=0;
		    				 int hoursSumTraffic=0;
		    				 String routePointNameKey = routePointNameStr.substring(1, routePointNameStr.length()-1);
		    				 for(Map<String, String> map1 :list){
		    		   			 if(unitKey.equals(map1.get("unitName")) && routePointNameKey.equals(map1.get("routePointName"))){
		    		   				 day++;
		    		   				 daySumTraffic=daySumTraffic+Integer.parseInt(map1.get("routePointDayTraffic"));
		       		   			     hoursSumTraffic=hoursSumTraffic+Integer.parseInt(map1.get("routePointHoursTraffic"));
		    		   			 }
		    		   		 }
		    				 if(day!=0){
		    					 Map<String,String> map2 = new HashMap<String,String>();
		        		   		 map2.put("statisDate", queryDate);
		        		   		 map2.put("unit", unitKey);
		        		   		 map2.put("routePointName", routePointNameKey);
		        		   		 map2.put("dayAverageTraffic", String.valueOf(daySumTraffic/day));
		        		   		 map2.put("hoursAverageTraffic",  String.valueOf(hoursSumTraffic/day));
		        		   		 result.add(map2);	 
		    				 }
		    			 }
		    		 }
		    		 Collections.sort(result, new Comparator<Map<String, String>>() {
		    	            public int compare(Map<String, String> o1, Map<String, String> o2) {
		    	            	String map1value = o1.get("dayAverageTraffic");
		    	                String map2value = o2.get("dayAverageTraffic");
		    	                return map2value.compareTo(map1value);    
		    	            }
	    	         });
		   	    }
			}catch (SQLException ex) {
				ex.printStackTrace();
			 }finally{
		    	try{
		    		if (stmt != null){
		    			stmt.close();
		    			}
		    		if (conn != null){
		    			conn.close();
		    		}
		    	}catch (SQLException localSQLException2) {
		    	}
			 }
			return result;
	}
}