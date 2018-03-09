package com.whir.ezflow.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
import org.activiti.engine.impl.bpmn.behavior.EventBasedGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.IntermediateCatchEventActivitiBehaviour;
import org.activiti.engine.impl.context.Context; 
import org.activiti.engine.impl.persistence.deploy.DeploymentCache;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.ResourceEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.apache.log4j.Logger;
import com.whir.ezflow.util.EzFlowFinals;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.ChoosedActivityVO;
import com.whir.ezflow.vo.IdentityVO;
import com.whir.ezflow.vo.TransactorInfoVO;
import com.whir.ezflow.vo.UserInfoVO; 

public class ProcessDefinitionService extends ServiceBase {
	
	private static Logger logger = Logger.getLogger(ProcessDefinitionService.class.getName());
	/**
	 * 根据流程编码找流程信息 (最新的)
	 * @param infp
	 * @return
	 */
	public Map<String, Object> findProcessInfoByKey(Map<String, Object> infp) {
		String key= infp.get("key").toString();
		DeploymentCache deploymentCache = Context.getProcessEngineConfiguration().getDeploymentCache();

		if (key != null) {
			processDefinition = deploymentCache.findDeployedLatestProcessDefinitionByKey_whir(key);
			this.processDefinitionId=processDefinition.getId();
			if (processDefinition == null) {
				throw new ActivitiException( "No process definition found for key = '" + key + "'");
			}
		}
		return dressUpProDefMap();
	} 
	
	
	
	/**
	 * 根据流程定义 id找流程
	 * @param processDefinitionId
	 * @return
	 */
	public Map<String, Object> findProcessInfoById(Map<String, Object> infp) {
		String processDefinitionId = infp.get("processDefinitionId").toString();
		DeploymentCache deploymentCache = Context.getProcessEngineConfiguration().getDeploymentCache();
		if (processDefinitionId != null) {
			processDefinition = deploymentCache.findDeployedProcessDefinitionById(processDefinitionId);		
			if (processDefinition == null) {
				throw new ActivitiException("No process definition found for id = '" + processDefinitionId + "'");
			}
			this.processDefinitionId=processDefinitionId;
		}
		/*
		 * else if (processDefinitionKey != null) { processDefinition =
		 * deploymentCache
		 * .findDeployedLatestProcessDefinitionByKey(processDefinitionKey); if
		 * (processDefinition == null) { throw new ActivitiException(
		 * "No process definition found for key '" + processDefinitionKey +
		 * "'"); } } else { throw new ActivitiException(
		 * "processDefinitionKey and processDefinitionId are null"); }
		 */
        return dressUpProDefMap();
	}
	
	private Map<String, Object>  dressUpProDefMap(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 写字段 ,fieldid,,fieldid,……
		resultMap.put("whir_nodeWriteField",processDefinition.getProperty("whir_nodeWriteField"));
		// 隐藏字段
		//resultMap.put("whir_nodeHiddenField",processDefinition.getProperty("whir_nodeHiddenField"));
		
		resultMap.put("whir_nodeHiddenField",processDefinition.getProperty("whir_nodeHiddenForStartUserField"));
		
		// 办理按钮
		resultMap.put("whir_taskButtons",processDefinition.getProperty("whir_taskButtons"));
		//表单id
		resultMap.put("whir_formKey",processDefinition.getProperty("whir_formKey"));
		
		//表单 新增url
		resultMap.put("whir_formAddUrl",processDefinition.getProperty("whir_formAddUrl"));
		resultMap.put("whir_formUpdateUrl",processDefinition.getProperty("whir_formUpdateUrl"));
		
		logger.debug("whir_formAddUrl:"+processDefinition.getProperty("whir_formAddUrl"));
		logger.debug("whir_formUpdateUrl:"+processDefinition.getProperty("whir_formUpdateUrl"));
		
        //表单提醒字段
		resultMap.put("whir_processRemindField",processDefinition.getProperty("whir_processRemindField"));
        //流程定义id
		resultMap.put("processDefinitionId", processDefinitionId);	
		//流程定义名
	    resultMap.put("processDefinitionName", processDefinition.getName());
		//流程变量
        resultMap.put("whir_processallVariables", processDefinition.getProperty("whir_processallVariables"));
        
        resultMap.put("whir_relationTrig", processDefinition.getProperty("whir_relationTrig"));
        
        resultMap.put("whir_processType", processDefinition.getProperty("whir_processType")==null?"1":processDefinition.getProperty("whir_processType").toString());
        
        //子表：允许新增行
	    resultMap.put("whir_subTableDataControlAdd", processDefinition.getProperty("whir_subTableDataControlAdd"));
	    
	    //子表：新增默认空行
	    resultMap.put("whir_subTableDataControlAddNull", processDefinition.getProperty("whir_subTableDataControlAddNull"));
	    
	    //子表：允许修改已有行数据
	    resultMap.put("whir_subTableDataControlModi", processDefinition.getProperty("whir_subTableDataControlModi"));
	    
	    //子表：允许删除已有行数据
	    resultMap.put("whir_subTableDataControlDel", processDefinition.getProperty("whir_subTableDataControlDel"));
	    
        //处理类信息
        if(processDefinition.getProperty("whir_taskDealWithClass")!=null){
        	List<Map<String,String>>   classList=(List<Map<String,String>>)processDefinition.getProperty("whir_taskDealWithClass");
		    if(classList!=null&&classList.size()>0){
 
		    	Map<String,String> classMap=classList.get(0);
		    	
		    	//处理类名
		    	resultMap.put("whir_classname", classMap.get("classname"));
		    	//保存方法名
		    	resultMap.put("whir_saveData", classMap.get("saveData"));
		    	//接口保存方法名
		    	resultMap.put("whir_saveStauts", classMap.get("saveStauts"));		    	
		    	//完成时
		    	resultMap.put("whir_completeData", classMap.get("completeData"));
		    	//接口完成时
		    	resultMap.put("whir_completeStauts", classMap.get("completeStauts"));
		    	
		       	//表单打开时执行方法
		    	resultMap.put("whir_pro_forminitJsFunName", classMap.get("forminitJsFunName"));
		    	//表单保存是执行方法
		    	resultMap.put("whir_pro_formsaveJsFunName", classMap.get("formsaveJsFunName"));
		   }
		}
		
        //流程描述
        resultMap.put("documentation", processDefinition.getProperty("documentation"));
		
        resultMap.put("description", processDefinition.getDescription());	
        
        return resultMap;	
	}
	
    
	/**
	 * 根据流程实例 id找流程
	 * @param processDefinitionId
	 * @return
	 */
	public Map<String, Object> findProcessInfoByProcessInstanceId(Map<String, Object> infp) {
		String processInstanceId = infp.get("processInstanceId").toString();
		DeploymentCache deploymentCache = Context.getProcessEngineConfiguration().getDeploymentCache();
	    
		 //找流程设计表的id 
	    String sql=" SELECT  DE.KEY_  FROM  EZ_FLOW_RE_PROCDEF DE ,EZ_FLOW_HI_PROCINST HIP"+
		           "  WHERE HIP.PROC_DEF_ID_=DE.ID_  and  HIP.PROC_INST_ID_='"+processInstanceId+"'";
	    UtilService  utilService=new UtilService();
	    List<Map>list=utilService.searchBySql(sql);
	    String processDefinitionKey="";
	    if(list!=null&&list.size()>0){
	    	processDefinitionKey=""+list.get(0).get("KEY_");
	    }
 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (processDefinitionKey != null) {
			Map<String, Object> inMap = new HashMap<String, Object>();
			inMap.put("key", processDefinitionKey);
			resultMap=findProcessInfoByKey(inMap); 
		} 
        return resultMap;
	}
 
	
	/**
	 * 取流程定义xml
	 * @param infp
	 * @return
	 */
	public Map<String, Object> getProcessXmlByProcessDefinitionId(Map<String, Object> infp) {
		
		logger.debug("开始     getProcessXmlByProcessDefinitionId"); 
		java.util.Date  bDate1=new java.util.Date(); 
		
		String processDefinitionId = infp.get("processDefinitionId").toString();
		DeploymentCache deploymentCache = Context.getProcessEngineConfiguration().getDeploymentCache();
		logger.debug("开始     getProcessXmlByProcessDefinitionId2"); 
		ProcessDefinitionEntity processDefinition = null;
		if (processDefinitionId != null) {
			processDefinition = deploymentCache.findDeployedProcessDefinitionById(processDefinitionId);
			if (processDefinition == null) {
				throw new ActivitiException( "No process definition found for id = '" + processDefinitionId + "'");
			}
		}
		logger.debug("开始     getProcessXmlByProcessDefinitionId3 processDefinition.getDeploymentId():"+processDefinition.getDeploymentId()+"   ||"+processDefinition.getKey()); 
	    java.util.Date  bDate2=new java.util.Date(); 
	    ResourceEntity resource = commandContext.getResourceManager()
				      .findResourceByDeploymentIdAndResourceName(processDefinition.getDeploymentId(), processDefinition.getKey()+".bpmn20.xml");
	   
	    if(resource == null) {
	       throw new ActivitiException("no resource found with name '" + processDefinition.getKey()+".bpmn20.xml" + "' in deployment '" + processDefinition.getDeploymentId() + "'");
	    }
	    
	    java.util.Date  eDate2=new java.util.Date(); 
		logger.debug("findResourceByDeploymentIdAndResourceName  时间："+(eDate2.getTime()-bDate2.getTime())); 
 
		logger.debug("开始     getProcessXmlByProcessDefinitionId4"); 
	    String processXml="";
		try {
			processXml = new String(resource.getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 写字段 ,fieldid,,fieldid,……
		resultMap.put("processXml", processXml);
		resultMap.put("processKey", processDefinition.getKey());
		resultMap.put("processDefId", processDefinition.getId());
		resultMap.put("processName", processDefinition.getName());
		
		java.util.Date  eDate1=new java.util.Date(); 
		logger.debug("getProcessXmlByProcessDefinitionId  时间："+(eDate1.getTime()-bDate1.getTime())); 
        return resultMap;
	}
 
	
	/**
	 * 取按钮范围
	 * @param inMap
	 * @return
	 */
	public  Map<String ,Object> getButtonDealUserScope(Map<String ,Object> inMap){
		
		logger.debug("开始 getButtonDealUserScope");
		
		//按钮的
		String buttonCode=inMap.get("buttonCode")==null?"":inMap.get("buttonCode").toString();
		//当前的活动id
		String activityId=inMap.get("activityId")==null?"":inMap.get("activityId").toString();
		String processInstanceId=inMap.get("processInstanceId")==null?"":inMap.get("processInstanceId").toString();
		String processDefId=inMap.get("processDefId")==null?"":inMap.get("processDefId").toString();
		UserInfoVO curUserInfoVO=(UserInfoVO)inMap.get("curUserInfoVO");
		String curUserAccount=inMap.get("curUserAccount")==null?"":inMap.get("curUserAccount").toString();
	    String curTaskId=inMap.get("curTaskId")==null?"":inMap.get("curTaskId").toString();
	    HistoricTaskInstanceEntity  histTask=null;
	    if(curTaskId!= null&&!curTaskId.equals("")&&!curTaskId.equals("null")){
	    	 histTask=commandContext.getHistoricTaskInstanceManager().findHistoricTaskInstanceById(curTaskId);     	  
	         if(processDefId.equals("")||processDefId.equals("null")){
	        	 processDefId=histTask.getProcessDefinitionId();
	  	     }
	  	     if(activityId.equals("")||activityId.equals("")){
	    		  activityId=histTask.getTaskDefinitionKey();
	         }
	    } 
		logger.debug("开始 getButtonDealUserScope buttonCode："+buttonCode); 
		Context.getCommandContext().setCurUserInfoVO(curUserInfoVO); 
		if(EzFlowUtil.judgeNull(processDefId)){
			this.processDefinitionId=processDefId;
		}
		ensureProcessDefinitionInitialized();
		
		ActivityImpl activityImpl=this.processDefinition.findActivity(activityId);
		List<Map<String,String>> taskButtonList=new ArrayList<Map<String,String>>();
		if(activityImpl.getProperty("whir_taskButtons")!=null){
		    taskButtonList=(List<Map<String,String>>)activityImpl.getProperty("whir_taskButtons");
		}
		
		Map<String,String> curButtonMap=null;
		for(Map<String,String> buttonMap:taskButtonList){
			logger.debug("buttonMap.get ："+buttonMap.get("id")); 	
			if(buttonMap.get("id")!=null&&(buttonMap.get("id").toString().equals(buttonCode)||buttonMap.get("id").toString().equals("cmd"+buttonCode))){
				curButtonMap=buttonMap;
				break;
			}
		}
		
		Map<String ,Object> resultMap=new HashMap<String ,Object>();		
		if(curButtonMap!=null){
		     if(true){
		    	 TransactorInfoVO transactorInfoVO=null;
				 String range=""+curButtonMap.get("range");
				 
			     logger.debug("开始 getButtonDealUserScope range："+range);
				 //比如 阅件 按钮没有 范围
				 if(!EzFlowUtil.judgeNull(range)){
					 range="3";
				 }  
				 //全部
				 if(range.equals("0")){
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds("*0*");
					 transactorInfoVO.setScopeNames("全部办理人");
				 }
				 //本部门
				 if(range.equals("1")){
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds("*"+curUserInfoVO.getOrgVO().getOrgSerial()+"*");
					 transactorInfoVO.setScopeNames(curUserInfoVO.getOrgVO().getOrgName());	
					 transactorInfoVO.setRealtype("selfOrg");
				 }
				 
				 //本组织及下属组织
				 if(range.equals("5")){
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds("*"+curUserInfoVO.getOrgVO().getOrgSerial()+"*");
					 transactorInfoVO.setScopeNames(curUserInfoVO.getOrgVO().getOrgName());	
					 transactorInfoVO.setRealtype("selfOrgAndSub");
				 }
				 
				 //本活动办理人
				 if(range.equals("3")){
					 if(processInstanceId!=null&&!processInstanceId.equals("null")){
						 Context.getCommandContext().setCurProcessInstanceId(processInstanceId);
					 }
			         TransactorService transactorService=new TransactorService();	
			         ActivityExecution execution=Context.getCommandContext().getExecutionManager().findExecutionById(processInstanceId);   
			         if(curTaskId!=null&&!curTaskId.equals("")&&!curTaskId.equals("null")){  
			        	 if(histTask.getWhir_fromTaskId()!=null&&!histTask.getWhir_fromTaskId().equals("")&&!histTask.getWhir_fromTaskId().equals("null")){
			      	    	HistoricTaskInstanceEntity  histfromTask=commandContext.getHistoricTaskInstanceManager().findHistoricTaskInstanceById(histTask.getWhir_fromTaskId()); 
			      	    	 UserInfoVO  vo=new UserInfoVO(histfromTask.getWhir_assigneeId(),histfromTask.getWhir_assigneeName(),histfromTask.getAssignee());
			      	    	 Context.getCommandContext().setCurUserInfoVO(vo);
			      	     }
			         } 
			         String  gettype="onlyDeal";
			         //转阅
			         if(buttonCode.equals("EzFlowTranRead")||buttonCode.equals("EzFlowSendReadTask")){
			              gettype="onlyRead"; 
			         }
			         ChoosedActivityVO  avo=transactorService.getChoosedActivityVOWithTransactor(activityImpl,execution,gettype,null); 
                     
			         if(gettype.equals("onlyRead")){
			        	 transactorInfoVO=avo.getReadTransactorInfoVO();
			         }else{
			        	 transactorInfoVO=avo.getDealTransactorInfoVO();
			         }	
				 }
				 
				 //指定范围
				 if(range.equals("4")){
					 String customIds=curButtonMap.get("customIds");
					 String customNames=curButtonMap.get("customNames");
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds(customIds);
					 transactorInfoVO.setScopeNames(customNames);	
					 logger.debug("开始 getButtonDealUserScope customIds："+customIds);
					 logger.debug("开始 getButtonDealUserScope customNames："+customNames);
					 logger.debug("开始 getButtonDealUserScope range："+range);
				 }
				 
				 if(!buttonCode.equals("EzFlowTranRead")&&!buttonCode.equals("EzFlowSendReadTask")){  
					String isSingle="false"; 
					if(activityImpl.getProperty("whir_taskSequenceType")!=null&&activityImpl.getProperty("whir_taskSequenceType").toString().equals("monopolise_single")){
						isSingle="true";
						if(transactorInfoVO!=null){
							transactorInfoVO.setIsSingle(isSingle);
						}
			        }  
				 }
	 	 
				 resultMap.put("transactorInfoVO", transactorInfoVO);
				 
				 //自动返回    1   需要自动返回
				 String autoTranReturn=curButtonMap.get("autoTranReturn");
				 if(autoTranReturn!=null){
				     resultMap.put("autoTranReturn", autoTranReturn);
				 }
			 }else{
				 //
			 }
		}
		
	    if(buttonCode.equals("EzFlowTranRead")||buttonCode.equals("EzFlowSendReadTask")){ 
	    	
	    	List<IdentityVO>resultList=new ArrayList<IdentityVO>();
			HistoricTaskInstanceQuery  query=new HistoricTaskInstanceQueryImpl(commandContext);
			//按    待办     活动key   流程实例id 查询
			query = query.whir_isForRead(new Integer("1")).taskDefinitionKey(activityId).processInstanceId(processInstanceId);
			
			String haveIds="";
			List<HistoricTaskInstance> tasklist = query.list();
			if(tasklist!=null&&tasklist.size()>0){
				IdentityVO vo=null;	  
			    for(HistoricTaskInstance task:tasklist){
			    	if(haveIds.indexOf("$"+task.getAssignee()+"$")<0){
			    		haveIds+="$"+task.getAssignee()+"$";
				    	vo=new UserInfoVO(null,task.getWhir_assigneeName(),task.getAssignee());
				    	resultList.add(vo);
			    	}
			    }
			}  
			resultMap.put("haveSendReadList", resultList);
        }else{
        	List<IdentityVO>resultList=new ArrayList<IdentityVO>();
			HistoricTaskInstanceQuery  query=new HistoricTaskInstanceQueryImpl(commandContext);
			//按    待办     活动key   流程实例id 查询
			query = query.whir_isForRead(new Integer("0")).taskDefinitionKey(activityId).processInstanceId(processInstanceId); 
		
			String haveIds="";
			List<HistoricTaskInstance> tasklist = query.list();
			if(tasklist!=null&&tasklist.size()>0){
				IdentityVO vo=null;	  
			    for(HistoricTaskInstance task:tasklist){
			    	if(haveIds.indexOf("$"+task.getAssignee()+"$")<0){
			    		haveIds+="$"+task.getAssignee()+"$";
				    	vo=new UserInfoVO(null,task.getWhir_assigneeName(),task.getAssignee());
				    	resultList.add(vo);
			    	}
			    }
			}  
			resultMap.put("haveSendTaskList", resultList);      	
        }
		return  resultMap;
	}
	
	
	
	/**
	 * 取按钮范围
	 * @param inMap
	 * @return
	 */
	public  Map<String ,Object> getEdAddSignButtonDealUserScope(Map<String ,Object> inMap){
		Map<String ,Object> resultMap=new HashMap<String ,Object>(); 
		
		//按钮的
		String buttonCode="EdAddSign";
		//当前的活动id
		String activityId=inMap.get("activityId")==null?"":inMap.get("activityId").toString();
		String processInstanceId=inMap.get("processInstanceId")==null?"":inMap.get("processInstanceId").toString();
		String processDefId=inMap.get("processDefId")==null?"":inMap.get("processDefId").toString();
		UserInfoVO curUserInfoVO=(UserInfoVO)inMap.get("curUserInfoVO");
		String curUserAccount=inMap.get("curUserAccount")==null?"":inMap.get("curUserAccount").toString();
		
		//找当前正在办理的活动
		com.whir.ezflow.service.EzFlowTaskService  ezFlowTaskService=new com.whir.ezflow.service.EzFlowTaskService();
		List<ChoosedActivityVO> nowActivityList=ezFlowTaskService.findDealingActivity(processInstanceId);
		if(nowActivityList!=null&&nowActivityList.size()>0){
			
		}else{
			 return resultMap;
		}
		
		Context.getCommandContext().setCurUserInfoVO(curUserInfoVO);
 
		if(EzFlowUtil.judgeNull(processDefId)){
			this.processDefinitionId=processDefId;
		}
		ensureProcessDefinitionInitialized();
		
		ActivityImpl activityImpl=this.processDefinition.findActivity(activityId);
		List<Map<String,String>> taskButtonList=new ArrayList<Map<String,String>>();
		if(activityImpl.getProperty("whir_taskButtons")!=null){
		    taskButtonList=(List<Map<String,String>>)activityImpl.getProperty("whir_taskButtons");
		}
		
		Map<String,String> curButtonMap=null;
		for(Map<String,String> buttonMap:taskButtonList){
			if(buttonMap.get("id")!=null&&buttonMap.get("id").toString().equals(buttonCode)){
				curButtonMap=buttonMap;
				break;
			}
		}
		
	    String range="-1";
	    TransactorInfoVO transactorInfoVO=null;
				
		if(curButtonMap!=null){
		     if(true){ 
				 range=""+curButtonMap.get("range");
				 //比如 阅件 按钮没有 范围
				 if(!EzFlowUtil.judgeNull(range)){
					 range="3";
				 }
				 
				 //全部
				 if(range.equals("0")){
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds("*0*");
					 transactorInfoVO.setScopeNames("全部办理人");
				 }
				 
				 //本部门
				 if(range.equals("1")){
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds("*"+curUserInfoVO.getOrgVO().getOrgSerial()+"*");
					 transactorInfoVO.setScopeNames(curUserInfoVO.getOrgVO().getOrgName());	
					 transactorInfoVO.setRealtype("selfOrg");
				 }
				 
				 //本组织及下属组织
				 if(range.equals("5")){
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds("*"+curUserInfoVO.getOrgVO().getOrgSerial()+"*");
					 transactorInfoVO.setScopeNames(curUserInfoVO.getOrgVO().getOrgName());	
					 transactorInfoVO.setRealtype("selfOrgAndSub");
				 }
				 
				 //本活动办理人
				 if(range.equals("3")){
			         
				 }
				 
				 //指定范围
				 if(range.equals("4")){
					 String customIds=curButtonMap.get("customIds");
					 String customNames=curButtonMap.get("customNames");
					 transactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_SCOPE);
					 transactorInfoVO.setScopeIds(customIds);
					 transactorInfoVO.setScopeNames(customNames);				 
				 }   
			 }else{
				 
			 } 
		}	 
		
		if(processInstanceId!=null&&!processInstanceId.equals("null")){
			 Context.getCommandContext().setCurProcessInstanceId(processInstanceId);
		}
	    TransactorService transactorService=new TransactorService();
	    ActivityExecution execution=Context.getCommandContext().getExecutionManager().findExecutionById(processInstanceId);
	    
	    ActivityImpl activityIngImpl=null; 
		for(ChoosedActivityVO vo:nowActivityList){ 
			vo.setDealTransactorInfoVO(null);
			vo.setReadTransactorInfoVO(null);
			if(range.equals("3")){  
			  	activityIngImpl=this.processDefinition.findActivity(vo.getActivityId());
		        String  gettype="onlyDeal"; 
		        ChoosedActivityVO  avo=transactorService.getChoosedActivityVOWithTransactor(activityIngImpl,execution,gettype,null);  
		        if(gettype.equals("onlyRead")){
		            transactorInfoVO=avo.getReadTransactorInfoVO();
		        }else{
		            transactorInfoVO=avo.getDealTransactorInfoVO();
		        }	 
		        vo.setDealTransactorInfoVO(transactorInfoVO); 
			}else{
				vo.setDealTransactorInfoVO(transactorInfoVO); 
			} 
		}
		resultMap.put("nextActivitys", nowActivityList);
		
		return  resultMap;
	}
	
	
	
	/**
	 * 判断是否需要继承父流程数据
	 * 
	 * @param superProcessId
	 *            父流程id
	 * @param processDefinitonKey
	 *            子流程的key
	 * @return
	 */
	public Map<String, Object> getInheritParentInfo(Map<String, Object> initMap) {
		String superProcessId=initMap.get("superProcessId").toString();
		String processDefinitonKey=initMap.get("processDefinitonKey").toString(); 
		//
		HistoricProcessInstanceEntity superHipEntity = (HistoricProcessInstanceEntity) Context
				.getCommandContext().getHistoricProcessInstanceManager()
				.findHistoricProcessInstance(superProcessId); 
		
		// 父流程定义
		ProcessDefinitionImpl superProcessDefinition = Context
				.getProcessEngineConfiguration()
				.getDeploymentCache()
				.findDeployedProcessDefinitionById(superHipEntity.getProcessDefinitionId());

		List<ActivityImpl> superActList = superProcessDefinition.getActivities();
		String isInheritParent = ""; 
		Map<String, Object> resultMap=new HashMap(); 
		for (ActivityImpl act : superActList) {
			String whir_act_subprocessKey = ""+ act.getProperty("whir_act_subprocessKey");			 
			if (whir_act_subprocessKey.equals(processDefinitonKey)) {
				isInheritParent = "" + act.getProperty("whir_inheritParent");
				List<Map<String,String>>  whir_callFieldJICHENs  =(List<Map<String,String>>)act.getProperty("whir_callFieldJICHEN");
				resultMap.put("whir_callFieldJICHEN",whir_callFieldJICHENs);
				break;
			}
		} 
		resultMap.put("whir_inheritParent", isInheritParent); 
		return resultMap;
	}
	
	
	/**
	 * 动态创建 连线
	 * @param processDefinition
	 * @return
	 */
	public TransitionImpl createSequenceFlow( ProcessDefinitionEntity processDefinition,String formId,String toId) {
		//动态连线 名称
		String name = "jumpName";
	 
		String id = "jump_"+formId+"_"+toId+(new java.util.Date().getTime());		 
		// Implicit check: sequence flow cannot cross (sub) process
		// boundaries: we
		// don't do a processDefinition.findActivity here
		ActivityImpl destinationActivity = processDefinition.findActivity(toId);
		ActivityImpl fromActivity = processDefinition.findActivity(formId);
		
		TransitionImpl dynamic_transition=null;
		
		if (formId == null) {
			// error
		} else if (destinationActivity == null) {
			// error
		} else if (fromActivity.getActivityBehavior() instanceof EventBasedGatewayActivityBehavior) {
			// ignore
		} else if (destinationActivity.getActivityBehavior() instanceof IntermediateCatchEventActivitiBehaviour
				&& (destinationActivity.getParentActivity() != null)
				&& (destinationActivity.getParentActivity()
						.getActivityBehavior() instanceof EventBasedGatewayActivityBehavior)) {
			// "Invalid incoming sequenceflow for intermediateCatchEvent with id '"+
			// destinationActivity.getId()+
			// "' connected to an event-based gateway.";
		} else {
			dynamic_transition = fromActivity.createOutgoingTransition(id);		
			//dynamic_transition = new TransitionImpl(id, processDefinition);
			dynamic_transition.setProperty("name", name);
			dynamic_transition.setDestination(destinationActivity);
		}
		return dynamic_transition;
	}
	
	/**
	 * 删除动态连线
	 * @param processDefinition
	 * @param dynamic_transition
	 * @param formId
	 * @param toId
	 */
	public  void  removeDynamicTransition(ProcessDefinitionEntity processDefinition,TransitionImpl dynamic_transition,String formId,String toId){
		logger.debug("开始 删除动态连线   removeDynamicTransition   formId： "+formId+"  toId:"+toId);
		logger.debug("开始 删除动态连线   removeDynamicTransition  dynamic_transition is null:"+(dynamic_transition==null));
		ActivityImpl destinationActivity = processDefinition.findActivity(toId);
		ActivityImpl fromActivity = processDefinition.findActivity(formId);
		if (fromActivity != null && dynamic_transition != null) {
            if(fromActivity.getOutgoingTransitions()!=null){
            	fromActivity.getOutgoingTransitions().remove(dynamic_transition);
            	logger.debug("removeDynamicTransition from  出口删除OK   ");
            }
		}
		if (destinationActivity != null && dynamic_transition != null) {
			if(destinationActivity.getIncomingTransitions()!=null){
				destinationActivity.getIncomingTransitions().remove(dynamic_transition);
				logger.debug("removeDynamicTransition to  进口删除OK   ");
			}    
		}
	}
	
	/**
	 * 根据流程定义 id找流程
	 * @param processDefinitionId
	 * @return
	 */
	public Map<String, Object> findProcessDefinitionInfoById(Map<String, Object> infp) {
		String processDefinitionId = infp.get("processDefinitionId").toString();
		DeploymentCache deploymentCache = Context.getProcessEngineConfiguration().getDeploymentCache();
		if (processDefinitionId != null) {
			processDefinition = deploymentCache.findDeployedProcessDefinitionById(processDefinitionId);		
			if (processDefinition == null) {
				throw new ActivitiException("No process definition found for id = '" + processDefinitionId + "'");
			}
			this.processDefinitionId=processDefinitionId;
		}
		/*
		 * else if (processDefinitionKey != null) { processDefinition =
		 * deploymentCache
		 * .findDeployedLatestProcessDefinitionByKey(processDefinitionKey); if
		 * (processDefinition == null) { throw new ActivitiException(
		 * "No process definition found for key '" + processDefinitionKey +
		 * "'"); } } else { throw new ActivitiException(
		 * "processDefinitionKey and processDefinitionId are null"); }
		 */
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("processDefinition", processDefinition);
        return resultMap;
	}
}
