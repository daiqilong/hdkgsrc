package com.whir.plugins.job;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.whir.evo.weixin.bd.InformationBD;

public class InformationJob implements Job{
	  private static Logger logger = Logger.getLogger(InformationJob.class.getName());

	  public void execute(JobExecutionContext arg0) throws JobExecutionException
	  {
		  boolean result = false;
		  InformationBD informationBD = new InformationBD();
		List<Map<String,String>> list = informationBD.getInformation();
		if(!list.isEmpty() && list!=null){
			try {
				result = informationBD.putDateToInformation(list);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if(result){
			try {
				informationBD.updateInformationData(list);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    System.out.println("结束执行 InformationJob"+result);
	  }

}
