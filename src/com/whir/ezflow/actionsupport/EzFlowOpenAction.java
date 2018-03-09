package com.whir.ezflow.actionsupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whir.common.db.Dbutil;

public class EzFlowOpenAction extends EzFlowBaseOpenAction {
	
	private static Logger logger = Logger.getLogger(EzFlowOpenAction.class.getName());
	
	/***
	 * 打开流程发起页面
	 * @return
	 */
	public String  startProcess(){
		logger.debug("开始打开流程发起页面");
		logger.debug("p_wf_recordId1:"+p_wf_recordId);
		//判断可是整数
	    if(!this.judgeInteger("rrecordId;rmoduleId;p_wf_pool_processId;p_wf_tableId;p_wf_moduleId;p_wf_pool_processType;p_wf_recordId;p_wf_taskId;p_wf_processInstanceId")){
		    logger.debug("sql注入风险！");
	    	return  this.NO_RIGHTS ;
	    } 	
	    
		//设置流程信息
		setStartInfo(new HashMap());
		logger.debug("p_wf_recordId:"+p_wf_recordId);
		logger.debug("结束打开流程发起页面");
		return "startOpen";
	}
	
	/***
	 * 打开待办等流程页面
	 * @return
	 */
	public String updateProcess(){
		logger.debug("开始待办等流程页面");
		
		//判断可是整数
	    if(!this.judgeInteger("rrecordId;rmoduleId;p_wf_pool_processId;p_wf_tableId;p_wf_moduleId;p_wf_pool_processType;p_wf_recordId;p_wf_taskId;p_wf_processInstanceId")){
		    logger.debug("sql注入风险！");
	    	return  this.NO_RIGHTS ;
	    } 
		if(!setUpdateInfo(new HashMap())){
			return this.NO_RIGHTS;
		}
		return "updateProcess";
	}
	/**
	 * 更改打印信息
	 * @return
	 */
	public String updatePrint(){
	    logger.debug("开始更新打印信息");	
		
		String databaseType=com.whir.common.config.SystemCommon.getDatabaseType();
		 
		
		String selectOracle="select MAX(nvl(PRINTNUM,0)) from  WF_WORK  where EZFLOWPROCESSINSTANCEID=:p_wf_processInstanceId ";
		String selectMssql=" select MAX(isnull(PRINTNUM,0)) from  WF_WORK  where EZFLOWPROCESSINSTANCEID=:p_wf_processInstanceId  ";
		
		String selectMaxSql="";
		
		int maxNum=0;

		Dbutil dbutil = new Dbutil();
		if (databaseType.indexOf("oracle") >= 0||databaseType.indexOf("mysql") >= 0) {
			selectMaxSql = selectOracle;
		} else {
			selectMaxSql = selectMssql;
		}

		Map varMap = new HashMap();
		varMap.put("p_wf_processInstanceId", this.p_wf_processInstanceId);
		List list=new ArrayList();
		try {
			list = dbutil.getDataListBySQL(selectMaxSql, varMap);
			
			if(list!=null&&list.size()>0){
				Object [] maxnumObj=(Object [])list.get(0);
				maxNum=Integer.parseInt(""+maxnumObj[0]);
			} 
			String type=request.getParameter("type");
			if(type.equals("update")){
				maxNum=maxNum+1;	
				String updateSql=" update  WF_WORK  set  PRINTNUM=:v_PRINTNUM   where   EZFLOWPROCESSINSTANCEID=:p_wf_processInstanceId  ";
				varMap.put("v_PRINTNUM", maxNum+"");
				dbutil.excuteBySQLWithVarMap(updateSql, varMap);
		    } 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//返回打印次数
		this.printResult(""+maxNum);
		logger.debug("结束更新打印信息");	
		return null;
	} 
}
