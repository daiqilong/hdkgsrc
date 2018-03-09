package com.whir.plugins.job;

import java.io.File;
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
import java.util.Properties;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.whir.component.util.PropertiesUtils;
import com.whir.ezoffice.customdb.common.util.DbOpt;

public class TimingDeleteJob implements Job{
	private static Logger logger = Logger.getLogger(TimingDeleteJob.class.getName());
	private static final String ACTIONPATH = "ftppath.properties";  
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Properties properties = PropertiesUtils.getProperties(ACTIONPATH);
		//String qxzxftp_tq = properties.getProperty("qxzxftp_tq");
		String qxzxftp_cac = properties.getProperty("qxzxftp_cac");
		String qxzxftp_pm = properties.getProperty("qxzxftp_pm");
		
	    //deleteTqFiles(qxzxftp_tq);
	    deleteCacFiles(qxzxftp_cac);
	    deletePmFiles(qxzxftp_pm);
	    deleteCdmData();
	}
	
	public void deleteTqFiles(String filePath){
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		Date da = new Date();
		String fileName = format1.format(da);
		  File root = new File(filePath);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(!file.isDirectory()){
			    	 if("YB".equals(file.getName().substring(0, 2)) && !fileName.equals(file.getName().substring(2, 10))){
	    	            file.delete();
			    	 }
			     }     
		    }
	}
	
	public void deleteCacFiles(String filePath){
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		Date da = new Date();
		String fileName = format1.format(da);
		  File root = new File(filePath);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(!file.isDirectory()){
			    	 if("RVR".equals(file.getName().substring(0, 3)) && !fileName.equals(file.getName().substring(3, 11))){
	    	            file.delete();
			    	 }
			     }     
		    }
	}
	public void deletePmFiles(String filePath){
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		Date da = new Date();
		String fileName = format1.format(da);
		  File root = new File(filePath);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(!file.isDirectory()){
			    	 if(!fileName.equals(file.getName().substring(0, 8))){
	    	            file.delete();
			    	 }
			     }     
		    }
	}
	
	public void deleteCdmData(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar nowDate = Calendar.getInstance();
		nowDate.roll(Calendar.DATE, -8);
		String str = sdf.format(nowDate.getTime());
		
		DbOpt opt=new DbOpt();
		try {
			opt.executeUpdate("delete from CDMINFOR where SUBSTR(sendtime,1,10)<'"+str+"'");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				opt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
