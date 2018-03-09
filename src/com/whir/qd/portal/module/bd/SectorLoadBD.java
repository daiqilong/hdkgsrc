package com.whir.qd.portal.module.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class SectorLoadBD extends HibernateBase {
	
	protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public  Map<String,String> getSectorLoad() throws Exception{
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    StringBuffer sectorName = new StringBuffer();
	    StringBuffer loadingValue = new StringBuffer();
	    StringBuffer realSafeguardFlight = new StringBuffer();
	    StringBuffer callSaturation = new StringBuffer();
	    StringBuffer airspaceComplexity = new StringBuffer();
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select SECTOR_NAME,LOADING_VALUE,REAL_SAFEGUARD_FLIGHTS,CALL_SATURATION,AIRSPACE_COMPLEXITY " +
	      		"from (select t.* from QD_SECTORLOAD t order by SECTOR_DATE desc) t1 where rownum<6";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	sectorName.append(rs.getString("SECTOR_NAME")).append(",");
	        	loadingValue.append(rs.getString("LOADING_VALUE")).append(",");
	        	realSafeguardFlight.append(rs.getString("REAL_SAFEGUARD_FLIGHTS")).append(",");
	        	callSaturation.append(rs.getString("CALL_SATURATION")).append(",");
	        	airspaceComplexity.append(rs.getString("AIRSPACE_COMPLEXITY")).append(",");
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2)
		      {
		      }
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
	   Map<String,String> map = new HashMap<String,String>();
	   if(!"".equals(sectorName.toString())){
		   map.put("SECTOR_NAME", sectorName.toString().substring(0, sectorName.toString().length()-1));
		   map.put("LOADING_VALUE", loadingValue.toString().substring(0, loadingValue.toString().length()-1));
		   map.put("REAL_SAFEGUARD_FLIGHTS", realSafeguardFlight.toString().substring(0, realSafeguardFlight.toString().length()-1));
		   map.put("CALL_SATURATION", callSaturation.toString().substring(0, callSaturation.toString().length()-1));
		   map.put("AIRSPACE_COMPLEXITY", airspaceComplexity.toString().substring(0, airspaceComplexity.toString().length()-1));
	   }
	   return map;
	}
}
