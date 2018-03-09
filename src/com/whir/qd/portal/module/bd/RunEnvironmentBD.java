package com.whir.qd.portal.module.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class RunEnvironmentBD extends HibernateBase {
	
	public  Map<String,String> getRunEnvironment(){
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    Map<String,String> map = new HashMap<String,String>();
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select t.weather_date,t.weather_before,t.weather_later,t.wind_direction_speed,t.temperature," +
	      		"t.visibility,t.airport_alarm,t.wind_shear_alarm from qd_weather t where " +
	      		"not exists (select 1 from qd_weather where weather_date>t.weather_date)";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	map.put("weatherDate", rs.getString("weather_date"));
	        	map.put("weatherBefore", rs.getString("weather_before"));
	        	map.put("weatherLater", rs.getString("weather_later"));
	        	map.put("windDirectionSpeed", rs.getString("wind_direction_speed"));
	        	map.put("temperature", rs.getString("temperature"));
	        	map.put("visibility", rs.getString("visibility"));
	        	map.put("airportAlarm", rs.getString("airport_alarm"));
	        	map.put("windShearAlarm", rs.getString("wind_shear_alarm"));
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
	   return map;
	}
}
