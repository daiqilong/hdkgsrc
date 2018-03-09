package com.whir.portal.basedata.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whir.common.util.DataSourceBase;
import com.whir.rd.util.WatchArrangeUtils;

public class WatchInfosOnHomePage {
	//获取当日值班领导信息
	public List getWatchLeaders(String postClass){
		Map<String,String> map1 = new HashMap<String,String>();
		if(map1!=null || map1.isEmpty()){
			
		}
		List empList = new ArrayList();
		List empExcelList = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date da = new Date();
		String today = sdf.format(da);
		String excelSql = "select distinct whir$dutyroster_dutystaff,whir$dutyroster_f3537 from whir$dutyroster " +
				"where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) = '"+today+"' order by whir$dutyroster_f3537 asc";
		System.out.println("excelSql:::"+excelSql);
		String sql = "select p.whir$posttab_pname,w.whir$watchtab_mpa,w.whir$watchtab_mpname "+
			"from whir$watchtab w join whir$posttab p on p.whir$posttab_id=w.whir$watchtab_postno "+
			"where p.whir$posttab_class='"+postClass+"' and w.whir$watchtab_dates='"+today+"' "+
			"order by p.whir$posttab_dno";
		List empInfoList = new ArrayList();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	rs = stmt.executeQuery(excelSql);
	    	if(rs!=null){
	    		while(rs.next()){
		    		Object[] obj1 = new Object[1];
		    		obj1[0] = rs.getString(1);
		    		empExcelList.add(obj1);
		    	}
	    		for(int i=0;i<empExcelList.size();i++){
		    		Object[] objs = (Object[])empExcelList.get(i);
		    		String[] excelObj = new String[2];
		    		//用户名
		    		excelObj[0] = objs[0].toString();
	    			sql = "select concat(empname,empmobilephone) infos from org_employee where empname='"+excelObj[0]+"'";
					rs = stmt.executeQuery(sql);
			    	while (rs.next()){
			    		excelObj[1]=rs.getString(1)+",";
			    	}
		    		empInfoList.add(excelObj);
		    	}
	    		rs.close();
	    	} else {
	    		rs = stmt.executeQuery(sql);
		    	while(rs.next()){
		    		Object[] obj1 = new Object[3];
		    		obj1[0] = rs.getString(1);
		    		obj1[1] = rs.getString(2);
		    		obj1[2] = rs.getString(3);
		    		empList.add(obj1);
		    	}
		    	
		    	for(int i=0;i<empList.size();i++){
		    		Object[] objs = (Object[])empList.get(i);
		    		String[] empObj = new String[3];
		    		empObj[0] = objs[0].toString();
		    		empObj[1] = "";
		    		empObj[2] = "";
		    		String mpa = (String)objs[1];
	        		if(mpa.startsWith("$")){
	        			String[] mailtoIds = (mpa.substring(1) + "$").split("\\$\\$");
	        			for(int j=0;j<mailtoIds.length;j++){
	        				sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+mailtoIds[j]+"'";
	        				rs = stmt.executeQuery(sql);
	        		    	while (rs.next()){
	        		    		empObj[1]+=rs.getString(1)+",";
	        		    		empObj[2]+=rs.getString(2)+",";
	        		    	}
		        		}
	        		} else{
	        			sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+mpa+"'";
	    				rs = stmt.executeQuery(sql);
	    		    	while (rs.next()){
	    		    		empObj[1]=rs.getString(1)+",";
	    		    		empObj[2]=rs.getString(2)+",";
	    		    	}
	        		}
		    		empInfoList.add(empObj);
		    		
		    	}
		    	rs.close();
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
		return empInfoList;
	}
	
	//获取当日值班领导信息
	public List getWatchCenterLeader(String postClass){
		List empList = new ArrayList();
		List empExcelList = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date da = new Date();
		String today = sdf.format(da);
		String excelSql = "select distinct whir$centreduty_dutystaff, whir$centreduty_department " +
		"from whir$centreduty where concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) = '"+today+"'";

		String sql = "select p.whir$posttab_pname,w.whir$watchtab_mpa,w.whir$watchtab_mpname "+
			"from whir$watchtab w join whir$posttab p on p.whir$posttab_id=w.whir$watchtab_postno "+
			"where p.whir$posttab_class='"+postClass+"' and w.whir$watchtab_dates='"+today+"' "+
			"order by p.whir$posttab_dno";
		List excelEmpInfoList = new ArrayList();
		List empInfoList = new ArrayList();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	rs = stmt.executeQuery(excelSql);
	    	if(rs!=null){
	    		while(rs.next()){
		    		Object[] obj1 = new Object[2];
		    		obj1[0] = rs.getString(1);
		    		obj1[1] = rs.getString(2);
		    		empExcelList.add(obj1);
		    	}
	    		for(int i=0;i<empExcelList.size();i++){
		    		Object[] objs = (Object[])empExcelList.get(i);
		    		String[] excelObj = new String[2];
		    		//部门
		    		excelObj[0] = objs[1].toString();
	    			sql = "select concat(empname,empmobilephone) infos from org_employee where empname='"+objs[0].toString()+"'";
					rs = stmt.executeQuery(sql);
			    	while (rs.next()){
			    		excelObj[1]=rs.getString(1)+",";
			    	}
			    	excelEmpInfoList.add(excelObj);
		    	}
	    		rs.close();
	    	} else {
		    	while(rs.next()){
		    		Object[] obj1 = new Object[3];
		    		obj1[0] = rs.getString(1);
		    		obj1[1] = rs.getString(2);
		    		obj1[2] = rs.getString(3);
		    		empList.add(obj1);
		    	}
		    	
		    	for(int i=0;i<empList.size();i++){
		    		Object[] objs = (Object[])empList.get(i);
		    		String[] empObj = new String[3];
		    		empObj[0] = objs[0].toString();
		    		empObj[1] = "";
		    		empObj[2] = "";
		    		String mpa = (String)objs[1];
	        		if(mpa.startsWith("$")){
	        			String[] mailtoIds = (mpa.substring(1) + "$").split("\\$\\$");
	        			for(int j=0;j<mailtoIds.length;j++){
	        				sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+mailtoIds[j]+"'";
	        				rs = stmt.executeQuery(sql);
	        		    	while (rs.next()){
	        		    		empObj[1]+=rs.getString(1)+",";
	        		    		empObj[2]+=rs.getString(2)+",";
	        		    	}
		        		}
	        		} else{
	        			sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+mpa+"'";
	    				rs = stmt.executeQuery(sql);
	    		    	while (rs.next()){
	    		    		empObj[1]=rs.getString(1)+",";
	    		    		empObj[2]=rs.getString(2)+",";
	    		    	}
	        		}
		    		empInfoList.add(empObj);
		    		
		    	}
		    	rs.close();
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
		 if(excelEmpInfoList!=null){
			List sortlist = new ArrayList();
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("空管中心")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("运行管理中心")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("飞服中心")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("飞行计划处理中心")){
					sortlist.add(obj);
				}
			}
			
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("设备监控中心")){
					sortlist.add(obj);
				}
			}
			
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("技术保障中心")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("设备维修中心")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("网络公司")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("气象中心")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("山东分局")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("安徽分局")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("江苏分局")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("浙江分局")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("江西分局")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("福建分局")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("厦门空管站")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("青岛空管站")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("宁波空管站")){
					sortlist.add(obj);
				}
			}
			for(int i=0;i<excelEmpInfoList.size();i++){
				Object[] obj = (Object[])excelEmpInfoList.get(i);
				if(obj[0].toString().equals("温州空管站")){
					sortlist.add(obj);
				}
			}
			empInfoList =sortlist;
		}
		return empInfoList;
	}
	//获取omss值班信息
	public List getOMSSDutyInfos(){
		List dutyList = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date da = new Date();
		String today = sdf.format(da);
		String sql = "select distinct(type),name from DUTY_INFORMATION where substr(datetime,0,10)='"+today+"'";
		List list = new WatchArrangeUtils().getList(sql);
		
		for(int i=0;i<list.size();i++){
			Object[] objs = (Object[]) list.get(i);
			Object[] obj = new Object[2];
			obj[0] = "";
			obj[1] = "";
			if(objs[0] !=null && !"".equals(objs[0])){
				String type = objs[0].toString();
				if(type.equals("centerDuty")){
					obj[0] = "OMSS中心值班";
				}else if(type.equals("moveDirector1")){
					obj[0] = "运行总主任1";
				}else if(type.equals("moveDirector2")){
					obj[0] = "运行总主任2";
				}else{
					obj[0] = "塔台值班";
				}
				obj[1] = objs[1];
			}
			dutyList.add(obj);
		}
		return dutyList;
	}
	
}
