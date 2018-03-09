package com.whir.portal.module.actionsupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
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

public class StrategyStatisticalAction extends BaseActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8100466905892136929L;
	private static Logger logger = Logger.getLogger(StrategyStatisticalAction.class.getName());
	
	/**
	 *  航空情报业务统计流程
	 *  @param request
	 *            HttpServletRequest
	 */
	public String aviationInforStatistical() {
	    return "aviationInforStatistical";
	}
	
	/**
	 *  飞行报告工作统计流程
	 *  @param request
	 *            HttpServletRequest
	 */
	public String flightReportStatistical() {
	    return "flightReportStatistical";
	}
	
	/**
	 *  塔台保障起降架次统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String towerSecFlightStatistical() {
		List<String> sectorName = getSectorName("whir$ttbzjctjb_f3616","whir$ttbzjctjb");
	    if (sectorName != null){
	      this.request.setAttribute("sectorNameList", sectorName);
	    } else {
	      this.request.setAttribute("sectorNameList", new ArrayList());
	    }
	    return "towerSecFlightStatistical";
	}
	
	/**
	 *  区域保障架次统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String areaSecFlightStatistical() {
		List<String> sectorName = getSectorName("whir$jjbzjctjzb_f4045","whir$jjbzjctjzb");
	    if (sectorName != null){
	      this.request.setAttribute("sectorNameList", sectorName);
	    } else {
	      this.request.setAttribute("sectorNameList", new ArrayList());
	    }
	    return "areaSecFlightStatistical";
	}
	
	/**
	 *  进近（终端）保障架次统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String terminalSecFlightStatistical() {
		List<String> sectorName = getSectorName("whir$jjbzjctjzb_f4045","whir$jjbzjctjzb");
	    if (sectorName != null){
	      this.request.setAttribute("sectorNameList", sectorName);
	    } else {
	      this.request.setAttribute("sectorNameList", new ArrayList());
	    }
	    return "terminalSecFlightStatistical";
	}
	
	public List<String> getSectorName(String field,String table) {
        List<String> list = new ArrayList<String>();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String str = "select distinct "+field+" from "+table+" where "+table+"_workstatus='100'";
	    System.out.println("str1"+str);
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
	 *  塔台数据统计统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String towerDataStatistical() {
		List<String> yearList = getAllYear("whir$ttbzjctjd_f3945","whir$ttbzjctjd");
	    if (yearList != null){
	      this.request.setAttribute("yearList", yearList);
	    } else {
	      this.request.setAttribute("yearList", new ArrayList());
	    }
	    
	    List<String> sectorName = getSectorName("whir$ttbzjctjb_f3616","whir$ttbzjctjb");
	    if (sectorName != null){
	      this.request.setAttribute("sectorNameList", sectorName);
	    } else {
	      this.request.setAttribute("sectorNameList", new ArrayList());
	    }
	    
	    return "towerDataStatistical";
	}
	
	/**
	 *  塔台数据统计统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String areaDataStatistical() {
		List<String> yearList = getAllYear("whir$jjbzjctjb_f4039","whir$jjbzjctjb");
	    if (yearList != null){
	      this.request.setAttribute("yearList", yearList);
	    } else {
	      this.request.setAttribute("yearList", new ArrayList());
	    }
	    
	    List<String> sectorName = getSectorName("whir$jjbzjctjzb_f4045","whir$jjbzjctjzb");
	    if (sectorName != null){
	      this.request.setAttribute("sectorNameList", sectorName);
	    } else {
	      this.request.setAttribute("sectorNameList", new ArrayList());
	    }
	    
	    return "areaDataStatistical";
	}
	
	public List<String> getAllYear(String field,String table) {
        List<String> list = new ArrayList<String>();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String str = "select distinct substr("+field+",0,4) from "+table+" where "+table+"_workstatus='100'";
	    System.out.println("str"+str);
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(str);
    		while (rs.next()) {
  	    	  String yearName = rs.getString(1);
  	    	  list.add(yearName);
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
	
	/*
	 * 航空情报业务统计流程数据
	 */
	public List<Map<String,String>> aviationInforStatisticalData(String queryDate) throws IOException, ParseException{
		System.out.println("queryDate::"+queryDate);
		if("".equals(queryDate) || queryDate==null){
			return null;
		}
		String[] queryDateArray = queryDate.split("-");
		int year = Integer.parseInt(queryDateArray[0]);
		int beforeYear = year-1;
		int mouth = Integer.parseInt(queryDateArray[1]);
		
		List<Map<String, String>> list = getData1(year,queryDate);
		System.out.println("list::"+list.size());
		List<String> yearMouthList = getYearMouthList(list);
		
		Map<String, String> dataMap = getData2(beforeYear,mouth);
	    
    	int simpCSeries = 0;
		int simpDSeries = 0;
		int simpDealWith = 0;
		int simpDataRevision = 0;
		int simpAirportCount = 0;
		int simpRevisionItem = 0;
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			if(entry.getKey().equals("cSeries")){
				simpCSeries = Integer.parseInt(entry.getValue());
			}
			if(entry.getKey().equals("dSeries")){
				simpDSeries = Integer.parseInt(entry.getValue());
			}
			if(entry.getKey().equals("dealWith")){
				simpDealWith = Integer.parseInt(entry.getValue());
			}
			if(entry.getKey().equals("dataRevision")){
				simpDataRevision = Integer.parseInt(entry.getValue());
			}
			if(entry.getKey().equals("airportCount")){
				simpAirportCount = Integer.parseInt(entry.getValue());
			}
			if(entry.getKey().equals("revisionItem")){
				simpRevisionItem = Integer.parseInt(entry.getValue());
			}
  		}
		
		for(int i=list.size()+1;i<=mouth;i++){
			Map<String, String> map1 = new HashMap<String, String>();
			if(i<10 && !yearMouthList.contains(year+"-0"+i)){
				map1.put("yearMouth", year+"-0"+i);
				map1.put("cSeries", "0");
				map1.put("dSeries", "0");
				map1.put("dealWith", "0");
				map1.put("dataRevision", "0");
				map1.put("airportCount", "0");
				map1.put("revisionItem", "0");
	    	  	list.add(map1);
			} else if(i>=10 && !yearMouthList.contains(year+"-0"+i)){
				map1.put("yearMouth", year+"-"+i);
				map1.put("cSeries", "0");
				map1.put("dSeries", "0");
				map1.put("dealWith", "0");
				map1.put("dataRevision", "0");
				map1.put("airportCount", "0");
				map1.put("revisionItem", "0");
	    	  	list.add(map1);
			}
		}
		
		Collections.sort(list, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> o1, Map<String, String> o2) {
            	String map1value = o1.get("yearMouth");
                String map2value = o2.get("yearMouth");
                return map1value.compareTo(map2value);    
            }
		});
		
		int cSeriesSum=0;
		int dSeriesSum=0;
		int dealWithSum=0;
		int dataRevisionSum=0;
		int airportCountSum=0;
		int revisionItemSum=0;
		
		int currCSeries=0;
		int currDSeries=0;
		int currDealWith=0;
		int currDataRevision=0;
		int currAirportCount=0;
		int currRevisionItem=0;
		
		for(Map<String, String> map1:list){
			cSeriesSum+=Integer.parseInt(map1.get("cSeries"));
			dSeriesSum+=Integer.parseInt(map1.get("dSeries"));
			dealWithSum+=Integer.parseInt(map1.get("dealWith"));
			dataRevisionSum+=Integer.parseInt(map1.get("dataRevision"));
			airportCountSum+=Integer.parseInt(map1.get("airportCount"));
			revisionItemSum+=Integer.parseInt(map1.get("revisionItem"));
			
			if(queryDate.equals(map1.get("yearMouth"))){
				currCSeries = Integer.parseInt(map1.get("cSeries"));
				currDSeries = Integer.parseInt(map1.get("dSeries"));
				currDealWith = Integer.parseInt(map1.get("dealWith"));
				currDataRevision = Integer.parseInt(map1.get("dataRevision"));
				currAirportCount = Integer.parseInt(map1.get("airportCount"));
				currRevisionItem = Integer.parseInt(map1.get("revisionItem"));
			}
		}
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("yearMouth", "本年累计");
		map2.put("cSeries", cSeriesSum+"");
		map2.put("dSeries", dSeriesSum+"");
		map2.put("dealWith", dealWithSum+"");
		map2.put("dataRevision", dataRevisionSum+"");
		map2.put("airportCount", airportCountSum+"");
		map2.put("revisionItem", revisionItemSum+"");
		list.add(map2);
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("yearMouth", "同比增长%");
		if(simpCSeries!=0){
			map3.put("cSeries", (currCSeries-simpCSeries)/simpCSeries+"%");
		} else {
			map3.put("cSeries", "0%");
		}
		if(simpDSeries!=0){
			map3.put("dSeries", (currDSeries-simpDSeries)/simpDSeries+"%");
		} else {
			map3.put("dSeries", "0%");
		}
		if(simpDealWith!=0){
			map3.put("dealWith", (currDealWith-simpDealWith)/simpDealWith+"%");
		} else {
			map3.put("dealWith", "0%");
		}
		if(simpDataRevision!=0){
			map3.put("dataRevision", (currDataRevision-simpDataRevision)/simpDataRevision+"%");
		} else {
			map3.put("dataRevision", "0%");
		}
		if(simpAirportCount!=0){
			map3.put("airportCount", (currAirportCount-simpAirportCount)/simpAirportCount+"%");
		} else {
			map3.put("airportCount", "0%");
		}
		if(simpRevisionItem!=0){
			map3.put("revisionItem", (currRevisionItem-simpRevisionItem)/simpRevisionItem+"%");
		} else {
			map3.put("revisionItem", "0%");
		}
	  	list.add(map3);
	  	return list;
	}
	
	
	public List<Map<String,String>> getData1(int year,String queryDate) throws IOException, ParseException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select substr(whir$hkqbtjb_f4119, 0, 7),whir$hkqbtjb_f4121,whir$hkqbtjb_f4124,whir$hkqbtjb_f4127, ")
	    .append("whir$hkqbtjb_f4130,whir$hkqbtjb_f4133,whir$hkqbtjb_f4139 from whir$hkqbtjb ")
	    .append("where substr(whir$hkqbtjb_f4119, 0, 7)>='"+year+"-01' and substr(whir$hkqbtjb_f4119, 0, 7)<='"+queryDate+"' order by whir$hkqbtjb_f4119");
	   
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
    		  Map<String, String> map = new HashMap<String, String>();
	    	  String yearMouth = rs.getString(1);
	    	  String cSeries = rs.getString(2);
	    	  String dSeries = rs.getString(3);
	    	  String dealWith = rs.getString(4);
	    	  String dataRevision = rs.getString(5);
	    	  String airportCount = rs.getString(6);
	    	  String revisionItem = rs.getString(7);
	    	  
	    	  map.put("yearMouth", yearMouth);
	    	  map.put("cSeries", cSeries);
	    	  map.put("dSeries", dSeries);
	    	  map.put("dealWith", dealWith);
	    	  map.put("dataRevision", dataRevision);
	    	  map.put("airportCount", airportCount);
	    	  map.put("revisionItem", revisionItem);
	    	  list.add(map);
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
	
	
	public Map<String,String> getData2(int year,int mouth) throws IOException, ParseException{
		Map<String, String> map = new HashMap<String, String>();
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select whir$hkqbtjb_f4121,whir$hkqbtjb_f4124,whir$hkqbtjb_f4127, ")
	    .append("whir$hkqbtjb_f4130,whir$hkqbtjb_f4133,whir$hkqbtjb_f4139 from whir$hkqbtjb ")
	    .append("where substr(whir$hkqbtjb_f4119, 0, 7)='"+year+"-"+mouth+"'");
	    
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
	    	  String yearMouth = rs.getString(1);
	    	  String cSeries = rs.getString(2);
	    	  String dSeries = rs.getString(3);
	    	  String dealWith = rs.getString(4);
	    	  String dataRevision = rs.getString(5);
	    	  String airportCount = rs.getString(6);
	    	  String revisionItem = rs.getString(7);
	    	  
	    	  map.put("yearMouth", yearMouth);
	    	  map.put("cSeries", cSeries);
	    	  map.put("dSeries", dSeries);
	    	  map.put("dealWith", dealWith);
	    	  map.put("dataRevision", dataRevision);
	    	  map.put("airportCount", airportCount);
	    	  map.put("revisionItem", revisionItem);
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
		return map;
	}
	
	
	
	/*
	 * 飞行报告数据统计
	 */
	public List<Map<String,String>> flightReportStatisticalData(String queryDate) throws IOException, ParseException{
		System.out.println("queryDate::"+queryDate);
		if("".equals(queryDate) || queryDate==null){
			return null;
		}
		String[] queryDateArray = queryDate.split("-");
		int year = Integer.parseInt(queryDateArray[0]);
		int beforeYear = year-1;
		int mouth = Integer.parseInt(queryDateArray[1]);
		
		List<Map<String, String>> list = getData3(year,queryDate);
		System.out.println("list::"+list.size());
		
		List<String> yearMouthList = getYearMouthList(list);
		Map<String, String> dataMap = getData4(beforeYear,mouth);
    	int simpSpeakTelegraph = 0;
		int simpDealWithTelegraph = 0;
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			if(entry.getKey().equals("speakTelegraph")){
				simpSpeakTelegraph = Integer.parseInt(entry.getValue());
			}
			if(entry.getKey().equals("dealWithTelegraph")){
				simpDealWithTelegraph = Integer.parseInt(entry.getValue());
			}
  		}
		System.out.println("simpSpeakTelegraph::::"+simpSpeakTelegraph);
		System.out.println("simpDealWithTelegraph::::"+simpDealWithTelegraph);
		
		for(int i=1;i<=mouth;i++){
			Map<String, String> map1 = new HashMap<String, String>();
			if(i<10 && !yearMouthList.contains(year+"-0"+i)){
				map1.put("yearMouth", year+"-0"+i);
				map1.put("speakTelegraph", "0");
				map1.put("dealWithTelegraph", "0");
	    	  	list.add(map1);
			} else if(i>=10 && !yearMouthList.contains(year+"-0"+i)){
				map1.put("yearMouth", year+"-"+i);
				map1.put("speakTelegraph", "0");
				map1.put("dealWithTelegraph", "0");
	    	  	list.add(map1);
			}
		}
		Collections.sort(list, new Comparator<Map<String, String>>() {
            public int compare(Map<String, String> o1, Map<String, String> o2) {
            	String map1value = o1.get("yearMouth");
                String map2value = o2.get("yearMouth");
                return map1value.compareTo(map2value);    
            }
		});
		
		int speakTelegraphSum=0;
		int dealWithTelegraphSum=0;
		
		int currSpeakTelegraph=0;
		int currDealWithTelegraph=0;
		
		for(Map<String, String> map1:list){
			System.out.println("speakTelegraph::::"+map1.get("speakTelegraph"));
			System.out.println("dealWithTelegraph::::"+map1.get("dealWithTelegraph"));
			
			speakTelegraphSum+=Integer.parseInt(map1.get("speakTelegraph"));
			dealWithTelegraphSum+=Integer.parseInt(map1.get("dealWithTelegraph"));
			
			if(queryDate.equals(map1.get("yearMouth"))){
				currSpeakTelegraph = Integer.parseInt(map1.get("speakTelegraph"));
				currDealWithTelegraph = Integer.parseInt(map1.get("dealWithTelegraph"));
			}
		}
		System.out.println("currSpeakTelegraph::::"+currSpeakTelegraph);
		System.out.println("currDealWithTelegraph::::"+currDealWithTelegraph);
		
		
		System.out.println("speakTelegraphSum::::"+speakTelegraphSum);
		System.out.println("dealWithTelegraphSum::::"+dealWithTelegraphSum);
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("yearMouth", "本年累计");
		map2.put("speakTelegraph", speakTelegraphSum+"");
		map2.put("dealWithTelegraph", dealWithTelegraphSum+"");
		list.add(map2);
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("yearMouth", "同比增长%");
		if(simpSpeakTelegraph!=0){
			map3.put("speakTelegraph", (currSpeakTelegraph-simpSpeakTelegraph)/simpSpeakTelegraph+"%");
		} else {
			map3.put("speakTelegraph", "0%");
		}
		if(simpDealWithTelegraph!=0){
			map3.put("dealWithTelegraph", (currDealWithTelegraph-simpDealWithTelegraph)/simpDealWithTelegraph+"%");
		} else {
			map3.put("dealWithTelegraph", "0%");
		}
	  	list.add(map3);
	  	return list;
	}
	
	
	public List<Map<String,String>> getData3(int year,String queryDate) throws IOException, ParseException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select substr(whir$fxbgtjb_f4142, 0, 7),whir$fxbgtjb_f4144,whir$fxbgtjb_f4147 from whir$fxbgtjb ")
	    .append("where substr(whir$fxbgtjb_f4142, 0, 7)>='"+year+"-01' and substr(whir$fxbgtjb_f4142, 0, 7)<='"+queryDate+"' order by whir$fxbgtjb_f4142");
	   
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
    		  Map<String, String> map = new HashMap<String, String>();
	    	  String yearMouth = rs.getString(1);
	    	  String speakTelegraph = rs.getString(2);
	    	  String dealWithTelegraph = rs.getString(3);
	    	  
	    	  map.put("yearMouth", yearMouth);
	    	  map.put("speakTelegraph", speakTelegraph);
	    	  map.put("dealWithTelegraph", dealWithTelegraph);
	    	  list.add(map);
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
	
	
	public Map<String,String> getData4(int year,int mouth) throws IOException, ParseException{
		Map<String, String> map = new HashMap<String, String>();
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select whir$fxbgtjb_f4144,whir$fxbgtjb_f4147 from whir$fxbgtjb ")
	    .append("where substr(whir$fxbgtjb_f4142, 0, 7)='"+year+"-"+mouth+"'");
	    
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
	    	  String speakTelegraph = rs.getString(1);
	    	  String dealWithTelegraph = rs.getString(2);
	    	  
	    	  map.put("speakTelegraph", speakTelegraph);
	    	  map.put("dealWithTelegraph", dealWithTelegraph);
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
		return map;
	}
	
	public List<String> getYearMouthList(List<Map<String, String>> list){
		List<String> result = new ArrayList<String>();
		for(Map<String, String> map:list){
			result.add(map.get("yearMouth"));
		}
		return result;
	}
	
	/*
	 * 飞行报告数据统计Excel数据导出
	 */
	public String toExcel1() throws Exception {
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   String queryDate = request.getParameter("queryDate");
	        
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = "飞行报告数据统计";
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
		        lab = new Label(0, 0, "时间",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "拍发电报数量",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "处理电报数量",wcfTitle); 
				ws.addCell(lab);
				
				List<Map<String, String>> result = flightReportStatisticalData(queryDate);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
		    	    lab = new Label(0,j+1,bodymap.get("yearMouth"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("speakTelegraph"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("dealWithTelegraph"));
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
	
	
	/*
	 * 航空情报业务统计Excel数据导出
	 */
	public String toExcel2() throws Exception {
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   String queryDate = request.getParameter("queryDate");
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = "航空情报业务统计";
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
		        lab = new Label(0, 0, "时间",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "C系列航行通告发布数量",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "D系列航行通告发布数量",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(3, 0, "航行通告处理数量",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(4, 0, "航空资料修订换页数",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(5, 0, "资料上报机场数",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(6, 0, "资料上报修订数据项",wcfTitle); 
				ws.addCell(lab);
				
				List<Map<String, String>> result = aviationInforStatisticalData(queryDate);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
		    	    lab = new Label(0,j+1,bodymap.get("yearMouth"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("cSeries"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("dSeries"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(3,j+1,bodymap.get("dealWith"));
		    	    ws.addCell(lab);
		    	    lab = new Label(4,j+1,bodymap.get("dataRevision"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(5,j+1,bodymap.get("airportCount"));
		    	    ws.addCell(lab);
		    	    lab = new Label(6,j+1,bodymap.get("revisionItem"));
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
	
	/**
	 * 塔台保障起降架次统计表
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception 
	 */
	public String towerSecFlightTableData() throws Exception {
		String sectorName = request.getParameter("sectorName");
		
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if(!"".equals(sectorName)){
        	list = getBodyData1(sectorName,pageSize,currentPage);
        }
		String json = "";
		JacksonUtil util = new JacksonUtil();
   			// 这里需要修改ejb,加个字段
   		json = util.writeListToJSON(
   				new String[] {"seqNum","airPort","dayPeak", "hoursPeak"}, list);
   		int recordCount = list.size();
   		int pageCount = (recordCount / pageSize);
   	    int mod = recordCount % pageSize;
   	    if (mod != 0)
   	    	 pageCount += 1;
   	    	json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
			+ "},data:" + json + "}";
   	    printResult(this.G_SUCCESS, json);
		return null;
	}
	
	
	/**
	 * 区域保障架次统计表
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception 
	 */
	public String areaSecFlightTableData() throws Exception {
		String queryDate = request.getParameter("queryDate");
		String sectorName = request.getParameter("sectorName");
		
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if(!"".equals(sectorName)){
        	list = getBodyData2(queryDate,sectorName,pageSize,currentPage);
        }
		String json = "";
			 JacksonUtil util = new JacksonUtil();
	   			// 这里需要修改ejb,加个字段
	   		 json = util.writeListToJSON(
	   				new String[] {"seqNum","controlUnit","mouthPeak", "historyPeak"}, list);
	   		 int recordCount = list.size();
	   		 int pageCount = (recordCount / pageSize);
	   	     int mod = recordCount % pageSize;
	   	     if (mod != 0)
	   	    	 pageCount += 1;
	   	    	json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
   				+ "},data:" + json + "}";
   		printResult(this.G_SUCCESS, json);
		return null;
	}
	
	
	/**
	 * 进近（终端）保障架次统计表
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception 
	 */
	public String terminalSecFlightTableData() throws Exception {
		String queryDate = request.getParameter("queryDate");
		String sectorName = request.getParameter("sectorName");
		
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if(!"".equals(sectorName)){
        	list = getBodyData3(queryDate,sectorName,pageSize,currentPage);
        }
		String json = "";
			 JacksonUtil util = new JacksonUtil();
	   			// 这里需要修改ejb,加个字段
	   		 json = util.writeListToJSON(
	   				new String[] {"seqNum","controlUnit","mouthPeak", "historyPeak"}, list);
	   		 int recordCount = list.size();
	   		 int pageCount = (recordCount / pageSize);
	   	     int mod = recordCount % pageSize;
	   	     if (mod != 0)
	   	    	 pageCount += 1;
	   	    	json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
   				+ "},data:" + json + "}";
   		printResult(this.G_SUCCESS, json);
		return null;
	}
	
	/*
	 * 塔台保障起降架次统计Excel数据导出
	 */
	public String toExcel3() throws Exception {
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
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
				String sheetName = "塔台保障起降架次统计";
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
		        lab = new Label(0, 0, "机场",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "日高峰",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "小时高峰",wcfTitle); 
				ws.addCell(lab);
				List<Map<String, String>> result = getBodyData1(sectorName,pageSize,currentPage);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
		    	    lab = new Label(0,j+1,bodymap.get("airPort"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("dayPeak"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("hoursPeak"));
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
	
	public List<Map<String, String>> getBodyData1(String sectorName,int pageSize,int currentPage)throws SQLException{
	    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select distinct(whir$ttbzjctjb_f3616),(select max(whir$ttbzjctjb_f3636) from whir$ttbzjctjb where whir$ttbzjctjb_f3616=c.whir$ttbzjctjb_f3616), ")
	    .append(" (select max(whir$ttbzjctjb_f3638) from whir$ttbzjctjb where whir$ttbzjctjb_f3616=c.whir$ttbzjctjb_f3616) from ")
	    .append(" whir$ttbzjctjb c where whir$ttbzjctjb_f3616 in ("+sectorName+") and c.whir$ttbzjctjb_workstatus='100'");
	    
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
  	    	  Map<String, String> map = new HashMap<String, String>();
  	    	  String airPort = rs.getString(1);
  	    	  String dayPeak = rs.getString(2);
  	    	  String hoursPeak = rs.getString(3);
  	    	  
  	    	  map.put("airPort", airPort);
  	    	  map.put("dayPeak", dayPeak);
  	    	  map.put("hoursPeak", hoursPeak);
  	    	  list.add(map);
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
	
	/*
	 * 区域保障架次统计Excel数据导出
	 */
	public String toExcel4() throws Exception {
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
				String sheetName = "区域保障架次统计";
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
		        lab = new Label(0, 0, "管制单位",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "本月日高峰",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "历史日高峰",wcfTitle); 
				ws.addCell(lab);
				
				List<Map<String, String>> result = getBodyData2(queryDate,sectorName,pageSize,currentPage);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
		    	    lab = new Label(0,j+1,bodymap.get("controlUnit"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("mouthPeak"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("historyPeak"));
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
	
	public List<Map<String, String>> getBodyData2(String queryDate, String sectorName,int pageSize,int currentPage)throws SQLException{
	    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select distinct(whir$jjbzjctjzb_f4045),(select max(whir$jjbzjctjzb_f4052) from whir$jjbzjctjb a, ")
	    .append(" whir$jjbzjctjzb b where b.whir$jjbzjctjzb_f4045=c.whir$jjbzjctjzb_f4045 and b.whir$jjbzjctjzb_foreignkey=a.whir$jjbzjctjb_id ")
	    .append(" and a.whir$jjbzjctjb_f4039 like '%"+queryDate+"%'),(select max(whir$jjbzjctjzb_f4052) from whir$jjbzjctjzb where whir$jjbzjctjzb_f4045=c.whir$jjbzjctjzb_f4045) from ")
	    .append("whir$jjbzjctjzb c where whir$jjbzjctjzb_f4045 in ("+sectorName+") and whir$jjbzjctjzb_workstatus='100'");
	    
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
    		  Map<String, String> map = new HashMap<String, String>();
	    	  String controlUnit = rs.getString(1);
	    	  String mouthPeak = rs.getString(2);
	    	  String historyPeak = rs.getString(3);
	    	  
	    	  map.put("controlUnit", controlUnit);
	    	  map.put("mouthPeak", mouthPeak);
	    	  map.put("historyPeak", historyPeak);
	    	  list.add(map);
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
	
	/*
	 * 进近（终端）保障架次统计Excel数据导出
	 */
	public String toExcel5() throws Exception {
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
				String sheetName = "进近（终端）保障架次统计";
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
		        lab = new Label(0, 0, "管制单位",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 0, "本月日高峰",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 0, "历史日高峰",wcfTitle); 
				ws.addCell(lab);
				
				List<Map<String, String>> result = getBodyData3(queryDate,sectorName,pageSize,currentPage);
				for(int j=0;j<result.size();j++){
					Map<String, String> bodymap = result.get(j);
		    	    lab = new Label(0,j+1,bodymap.get("controlUnit"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+1,bodymap.get("mouthPeak"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+1,bodymap.get("historyPeak"));
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
	
	public List<Map<String, String>> getBodyData3(String queryDate, String sectorName,int pageSize,int currentPage)throws SQLException{
	    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select distinct(whir$jjbzjctjzb_f4045),(select max(whir$jjbzjctjzb_f4048) from whir$jjbzjctjb a, ")
	    .append(" whir$jjbzjctjzb b where b.whir$jjbzjctjzb_f4045=c.whir$jjbzjctjzb_f4045 and b.whir$jjbzjctjzb_foreignkey=a.whir$jjbzjctjb_id ")
	    .append(" and a.whir$jjbzjctjb_f4039 like '%"+queryDate+"%'),(select max(whir$jjbzjctjzb_f4048) from whir$jjbzjctjzb where whir$jjbzjctjzb_f4045=c.whir$jjbzjctjzb_f4045) from ")
	    .append("whir$jjbzjctjzb c where whir$jjbzjctjzb_f4045 in ("+sectorName+") and whir$jjbzjctjzb_workstatus='100'");
	    
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
    		  Map<String, String> map = new HashMap<String, String>();
	    	  String controlUnit = rs.getString(1);
	    	  String mouthPeak = rs.getString(2);
	    	  String historyPeak = rs.getString(3);
	    	  
	    	  map.put("controlUnit", controlUnit);
	    	  map.put("mouthPeak", mouthPeak);
	    	  map.put("historyPeak", historyPeak);
	    	  list.add(map);
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
	
	/*
	 * 塔台数据统计统计表
	 */
	public List<Map<String,String>> towerDataStatisticalData(String years,String mouths,String sectorNames) throws SQLException, UnsupportedEncodingException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String[] yearArray  = years.split(",");
		String[] mouthArray  = mouths.split(",");
		StringBuffer sb = new StringBuffer();
		for(String strYear:yearArray){
			for(String strMouth:mouthArray){
				if(strMouth.length()==1){
					sb.append(strYear).append("-").append("0"+strMouth).append(",");
				} else {
					sb.append(strYear).append("-").append(strMouth).append(",");
				}
				
			}
		}
		String queryDates = sb.toString().substring(0, sb.toString().length()-1);
		System.out.println("queryDates:"+queryDates);
		if(!"".equals(sectorNames) && sectorNames!=null){
			String[] sectorNameArray = sectorNames.split(",");
			for(String sectorName:sectorNameArray){
				Map<String, String> map = getBodyData4(queryDates,sectorName);
				list.add(map);
			}
		}
		return list;
	}
	
	
	public Map<String, String> getBodyData4(String queryDates, String sectorName)throws SQLException, UnsupportedEncodingException{
		String[] queryDateArray = queryDates.split(",");
		Map<String, String> map = new HashMap<String, String>();
		map.put("controlUnit", sectorName.substring(1, sectorName.length()-1));
		
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select sum(whir$ttbzjctjb_f3617),sum(whir$ttbzjctjb_f3618),sum(whir$ttbzjctjb_f3619),sum(whir$ttbzjctjb_f3620),sum(whir$ttbzjctjb_f3621),sum(whir$ttbzjctjb_f3622)")
	    .append(",sum(whir$ttbzjctjb_f3623),sum(whir$ttbzjctjb_f3624),sum(whir$ttbzjctjb_f3625),sum(whir$ttbzjctjb_f3626),sum(whir$ttbzjctjb_f3627),sum(whir$ttbzjctjb_f3628)")
	    .append(",sum(whir$ttbzjctjb_f3629),sum(whir$ttbzjctjb_f3630),sum(whir$ttbzjctjb_f3631),sum(whir$ttbzjctjb_f3632),sum(whir$ttbzjctjb_f3633),sum(whir$ttbzjctjb_f3634)")
	    .append(",sum(whir$ttbzjctjb_f3635),sum(whir$ttbzjctjb_f3636),sum(whir$ttbzjctjb_f3638) from whir$ttbzjctjd a,whir$ttbzjctjb b where b.whir$ttbzjctjb_foreignkey=a.whir$ttbzjctjd_id ")
	    .append("and b.whir$ttbzjctjb_f3616="+sectorName+" and (");
	    for(int i=0;i<queryDateArray.length;i++){
	    	if(i==0){
	    		buffer.append("a.whir$ttbzjctjd_f3945 like '%"+queryDateArray[i]+"%'");
	    	} else {
	    		buffer.append(" or a.whir$ttbzjctjd_f3945 like '%"+queryDateArray[i]+"%'");
	    	}
	    }
	    buffer.append(" ) and whir$ttbzjctjd_workstatus='100' and whir$ttbzjctjb_workstatus='100'");
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
	    	  String bj = rs.getString(1);
	    	  String zhj = rs.getString(2);
	    	  String jb = rs.getString(3);
	    	  String baoj = rs.getString(4);
	    	  String gsxl = rs.getString(5);
	    	  String hxxl = rs.getString(6);
	    	  String dj = rs.getString(7);
	    	  String gw = rs.getString(8);
	    	  String jzjj = rs.getString(9);
	    	  String jh = rs.getString(10);
	    	  String wh = rs.getString(11);
	    	  String qt = rs.getString(12);
	    	  String hj = rs.getString(13);
	    	  String bshntq = rs.getString(14);
	    	  String mh = rs.getString(15);
	    	  String jh1 = rs.getString(16);
	    	  String wh1 = rs.getString(17);
	    	  String hj1 = rs.getString(18);
	    	  String zj1 = rs.getString(19);
	    	  String rgfqjjc = rs.getString(20);
	    	  String xshgfqjjc = rs.getString(21);
	    	  map.put("bj", bj);
	    	  map.put("zhj", zhj);
	    	  map.put("jb", jb);
	    	  map.put("baoj", baoj);
	    	  map.put("gsxl", gsxl);
	    	  map.put("hxxl", hxxl);
	    	  map.put("dj", dj);
	    	  map.put("gw", gw);
	    	  map.put("jzjj", jzjj);
	    	  map.put("jh", jh);
	    	  map.put("wh", wh);
	    	  map.put("qt", qt);
	    	  map.put("hj", hj);
	    	  map.put("bshntq", bshntq);
	    	  map.put("mh", mh);
	    	  map.put("jh1", jh1);
	    	  map.put("wh1", wh1);
	    	  map.put("hj1", hj1);
	    	  map.put("zj1", zj1);
	    	  map.put("rgfqjjc", rgfqjjc);
	    	  map.put("xshgfqjjc", xshgfqjjc);
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
		return map;
	}
	
	
	/*
	 * 进近（区域）数据统计表
	 */
	public List<Map<String,String>> areaDataStatisticalData(String years,String mouths,String sectorNames) throws SQLException, UnsupportedEncodingException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		String[] yearArray  = years.split(",");
		String[] mouthArray  = mouths.split(",");
		StringBuffer sb = new StringBuffer();
		for(String strYear:yearArray){
			for(String strMouth:mouthArray){
				if(strMouth.length()==1){
					sb.append(strYear).append("-").append("0"+strMouth).append(",");
				} else {
					sb.append(strYear).append("-").append(strMouth).append(",");
				}
				
			}
		}
		String queryDates = sb.toString().substring(0, sb.toString().length()-1);
		System.out.println("queryDates:"+queryDates);
		if(!"".equals(sectorNames) && sectorNames!=null){
			String[] sectorNameArray = sectorNames.split(",");
			for(String sectorName:sectorNameArray){
				Map<String, String> map = getBodyData5(queryDates,sectorName);
				list.add(map);
			}
		}
		return list;
	}
	
	
	public Map<String, String> getBodyData5(String queryDates, String sectorName)throws SQLException, UnsupportedEncodingException{
		String[] queryDateArray = queryDates.split(",");
		Map<String, String> map = new HashMap<String, String>();
		map.put("controlUnit", sectorName.substring(1, sectorName.length()-1));
		
	    StringBuffer buffer = new StringBuffer();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    buffer.append("select sum(whir$jjbzjctjzb_f4046),sum(whir$jjbzjctjzb_f4047),sum(whir$jjbzjctjzb_f4048),sum(whir$jjbzjctjzb_f4049),")
	    .append("sum(whir$jjbzjctjzb_f4050),sum(whir$jjbzjctjzb_f4051),sum(whir$jjbzjctjzb_f4052),")
	    .append("sum(whir$jjbzjctjzb_f4053),sum(whir$jjbzjctjzb_f4054)")
	    .append(" from whir$jjbzjctjb a,whir$jjbzjctjzb b where b.whir$jjbzjctjzb_foreignkey=a.whir$jjbzjctjb_id and b.whir$jjbzjctjzb_f4045 ="+sectorName+" and (");
	    for(int i=0;i<queryDateArray.length;i++){
	    	if(i==0){
	    		buffer.append("a.whir$jjbzjctjb_f4039 like '%"+queryDateArray[i]+"%'");
	    	} else {
	    		buffer.append(" or a.whir$jjbzjctjb_f4039 like '%"+queryDateArray[i]+"%'");
	    	}
	    }
	    buffer.append(" ) and whir$jjbzjctjb_workstatus='100' and whir$jjbzjctjzb_workstatus='100'");
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(buffer.toString());
    		while (rs.next()) {
	    	  String jjmh = rs.getString(1);
	    	  String jjjh = rs.getString(2);
	    	  String jjrgf = rs.getString(3);
	    	  String jjxj = rs.getString(4);
	    	  String qymh = rs.getString(5);
	    	  String qujh = rs.getString(6);
	    	  String qyrgf = rs.getString(7);
	    	  String qyxj = rs.getString(8);
	    	  String zj = rs.getString(9);
	    	  map.put("jjmh", jjmh);
	    	  map.put("jjjh", jjjh);
	    	  map.put("jjrgf", jjrgf);
	    	  map.put("jjxj", jjxj);
	    	  map.put("qymh", qymh);
	    	  map.put("qujh", qujh);
	    	  map.put("qyrgf", qyrgf);
	    	  map.put("qyxj", qyxj);
	    	  map.put("zj", zj);
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
		return map;
	}
	
	
	public String toExcel6() throws Exception {
	    //response.reset();
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	    
		request.setCharacterEncoding("UTF-8");
		String years = request.getParameter("years")==null?"":request.getParameter("years").toString();
		String mouths = request.getParameter("mouths")==null?"":request.getParameter("mouths").toString();
		String sectorName = request.getParameter("sectorName").toString();
	   
	   InputStream in = null;
	   OutputStream out = null;
	   OutputStream os = null;
	   File tempFile = null;
	    
	   Date date = new Date();
	   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
	   String strDate = format.format(date);
		// 输出到EXCEL
		WritableWorkbook wwb = null;
		try {
		    os = response.getOutputStream();
			String sheetName = "塔台数据统计表";
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
			Label lab = null; 
			WritableFont   wf2   =   new   WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
	        WritableCellFormat wcfTitle = new WritableCellFormat(wf2);
	        wcfTitle.setBackground(jxl.format.Colour.IVORY);  //象牙白
	        wcfTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
	        wcfTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐
	        CellView navCellView = new CellView();  
	        navCellView.setAutosize(true); //设置自动大小
	        navCellView.setSize(18);
	        /*===========================表头设置===============================*/
			
			lab = new Label(0, 0, "管制单位",wcfTitle); 
			ws.mergeCells(0,0,0,1);
			ws.addCell(lab);
			
			lab = new Label(1, 0, "起降架次",wcfTitle); 
			ws.mergeCells(1,0,14,0);
			ws.addCell(lab);
			
			lab = new Label(15, 0, "塔台飞越架次",wcfTitle); 
			ws.mergeCells(15,0,18,0);
			ws.addCell(lab);
			
			lab = new Label(19, 0, "总计",wcfTitle); 
			ws.mergeCells(19,0,19,1);
			ws.addCell(lab);
			lab = new Label(20, 0, "日高峰起降架次",wcfTitle); 
			ws.mergeCells(20,0,20,1);
			ws.addCell(lab);
			Label label111 = new Label(21, 0, "小时高峰起降架次",wcfTitle); 
			ws.mergeCells(21,0,21,1);
			ws.addCell(label111);
		
			lab = new Label(1, 1, "班机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(2, 1, "专机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(3, 1, "加班",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(4, 1, "包机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(5, 1, "公司训练",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(6, 1, "航校训练",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(7, 1, "调机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(8, 1, "公务",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(9, 1, "救灾急救",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(10, 1, "军航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(11, 1, "外航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(12, 1, "其他",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(13, 1, "合计",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(14, 1, "比上年同期%",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(15, 1, "民航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(16, 1, "军航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(17, 1, "外航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(18, 1, "合计",wcfTitle); 
			ws.addCell(lab);
			
			List<Map<String, String>> list = towerDataStatisticalData(years,mouths,sectorName);
			for(int j=0;j<list.size();j++){
				Map<String, String> bodymap = list.get(j);
	    	    lab = new Label(0,j+2,bodymap.get("controlUnit"));
	    	    ws.addCell(lab);
	    	    lab = new Label(1,j+2,bodymap.get("bj"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(2,j+2,bodymap.get("zhj"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(3,j+2,bodymap.get("jb"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(4,j+2,bodymap.get("baoj"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(5,j+2,bodymap.get("gsxl"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(6,j+2,bodymap.get("hxxl"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(7,j+2,bodymap.get("dj"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(8,j+2,bodymap.get("gw"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(9,j+2,bodymap.get("jzjj"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(10,j+2,bodymap.get("jh"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(11,j+2,bodymap.get("wh"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(12,j+2,bodymap.get("qt"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(13,j+2,bodymap.get("hj"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(14,j+2,bodymap.get("bshntq"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(15,j+2,bodymap.get("mh"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(16,j+2,bodymap.get("jh1"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(17,j+2,bodymap.get("wh1"));
	    	    ws.addCell(lab);  
	    	    lab = new Label(18,j+2,bodymap.get("hj1"));
	    	    ws.addCell(lab);
	    	    lab = new Label(19,j+2,bodymap.get("zj1"));
	    	    ws.addCell(lab); 
	    	    lab = new Label(20,j+2,bodymap.get("rgfqjjc"));
	    	    ws.addCell(lab); 
	    	    lab = new Label(21,j+2,bodymap.get("xshgfqjjc"));
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
	        catch (Exception e2)
	        {
	        	e2.printStackTrace();
	        }
	        try {
	          if (in != null)
	            in.close();
	        }
	        catch (Exception e1)
	        {
	        	e1.printStackTrace();
	        }
	        if ((tempFile != null) && 
	            (tempFile.exists())) {
	            tempFile.delete();
            }
       }
		return null;
	}
	
	
	public String toExcel7() throws Exception {
		   //response.reset();
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		    
		   request.setCharacterEncoding("UTF-8");
		   String years = request.getParameter("years")==null?"":request.getParameter("years").toString();
		   String mouths = request.getParameter("mouths")==null?"":request.getParameter("mouths").toString();
		   String sectorName = request.getParameter("sectorName").toString();
		   
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = "进近(区域)数据统计";
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
				lab = new Label(0, 0, "进近（终端）、区域保障架次统计表",wcfTitle); 
				ws.mergeCells(0,0,9,0);
				ws.addCell(lab);
				lab = new Label(0, 1, "管制单位",wcfTitle); 
				ws.mergeCells(0,1,0,2);
				ws.addCell(lab);
				lab = new Label(1, 1, "进近（终端）保障架次",wcfTitle); 
				ws.mergeCells(1,1,4,1);
				ws.addCell(lab);
				lab = new Label(5, 1, "区域保障架次",wcfTitle); 
				ws.mergeCells(5,1,8,1);
				ws.addCell(lab);
				lab = new Label(9, 1, "",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 2, "民航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 2, "军航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(3, 2, "日高峰",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(4, 2, "小计",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(5, 2, "民航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(6, 2, "军航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(7, 2, "日高峰",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(8, 2, "小计",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(9, 2, "总计",wcfTitle); 
				ws.addCell(lab);
				List<Map<String, String>> list = areaDataStatisticalData(years,mouths,sectorName);
				for(int j=0;j<list.size();j++){
					Map<String, String> bodymap = list.get(j);
		    	    lab = new Label(0,j+3,bodymap.get("controlUnit"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+3,bodymap.get("jjmh"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+3,bodymap.get("jjjh"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(3,j+3,bodymap.get("jjrgf"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(4,j+3,bodymap.get("jjxj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(5,j+3,bodymap.get("qymh"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(6,j+3,bodymap.get("qyjh"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(7,j+3,bodymap.get("qyrgf"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(8,j+3,bodymap.get("qyxj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(9,j+3,bodymap.get("zj"));
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
}