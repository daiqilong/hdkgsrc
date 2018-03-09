package com.whir.plugins.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.hibernate.HibernateException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.whir.portal.basedata.bd.CdmServerBD;

public class CdmServerData implements Job{
	  private static Logger logger = Logger.getLogger(CdmServerData.class.getName());

	  public void execute(JobExecutionContext arg0) throws JobExecutionException
	  {
	    System.out.println("开始CdmServerData :" + new Date().toString());
	    CdmServerBD cdmServerBD = new CdmServerBD();
	    StringBuffer sb = new StringBuffer();
	    List<String> listXml = new ArrayList<String>();
	    try {
			List<Map<String,String>> list = cdmServerBD.getCdmServerData();
			System.out.println("list大小:" + list.size());
			for(Map<String,String> map:list){
				sb.append(map.get("id").toString()).append(",");
				listXml.add(map.get("id").toString()+"####"+map.get("xmlStructure").toString());
			}
			cdmServerBD.updateCdmServerData(sb.toString());
			cdmServerBD.putCdmServerBD(listXml);
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    System.out.println("结束执行 CdmServerData");
	  }

}
