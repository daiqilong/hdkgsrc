package com.whir.ezflow.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList; 
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException; 
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.HistoricTaskInstanceQueryImpl;
import org.activiti.engine.impl.TaskQueryImpl;
import org.activiti.engine.impl.bpmn.behavior.EventBasedGatewayActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.IntermediateCatchEventActivitiBehaviour;
 
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.el.UelExpressionCondition;
 
import org.activiti.engine.impl.jobexecutor.TimerDeclarationImpl;
import org.activiti.engine.impl.jobexecutor.TimerDeclarationType;
import org.activiti.engine.impl.jobexecutor.Whir_RemindTimerExecuteJobHandler;
import org.activiti.engine.impl.jobexecutor.Whir_TimerExecuteJobHandler;
import org.activiti.engine.impl.jobexecutor.Whir_autoTranTimerExecuteJobHandler;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
 
import org.activiti.engine.task.Task;
 

import com.whir.common.db.Dbutil;
import com.whir.ezflow.util.EzFlowFinals;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.ChoosedActivityVO;
import com.whir.ezflow.vo.TransactorInfoVO;
import com.whir.ezflow.vo.UserInfoVO;

public class EzFlowTaskService extends ServiceBase{
	
   
	
	/**
	 * 找当前正在 办理的活动
	 * @param varMap
	 * @return
	 */
	public Map<String,Object> findDealedActivity_out(Map<String,Object> varMap){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String processInstanceId=varMap.get("processInstanceId")==null?"":varMap.get("processInstanceId").toString();
		List<ChoosedActivityVO> list=findDealedActivity(processInstanceId);
		resultMap.put("dealedActivitys", list);
		return resultMap;	
	}
	
	
	/**
	 * 找当前正在 办理的活动
	 * @param varMap
	 * @return
	 */
	public Map<String,Object> findDealedActivity_real_out(Map<String,Object> varMap){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String processInstanceId=varMap.get("processInstanceId")==null?"":varMap.get("processInstanceId").toString();
		List<ChoosedActivityVO> list=findDealedActivity_real(processInstanceId);
		resultMap.put("dealedActivitys", list);
		return resultMap;	
	}
	
	
	/**
	 * 找已经办理的活动
	 * @param processInstanceId
	 * @return
	 */
	public List<ChoosedActivityVO> findDealedActivity(String processInstanceId){
		
		List<ChoosedActivityVO>  resultList=new ArrayList<ChoosedActivityVO> ();
		HistoricTaskInstanceQueryImpl  query=new HistoricTaskInstanceQueryImpl(commandContext);	
		//查询出当前在办所有任务
		List<HistoricTaskInstance> list=query.processInstanceId(processInstanceId).finished().whir_isForRead(0).list();
		Map<String,ChoosedActivityVO> infoMap=new HashMap<String,ChoosedActivityVO>();
	    ChoosedActivityVO vo=null;
		for(HistoricTaskInstance task:list){
		    //已经设置了
			if(infoMap.get(task.getTaskDefinitionKey())!=null){
				vo=infoMap.get(task.getTaskDefinitionKey());
				UserInfoVO userInfoVo=new UserInfoVO(null,task.getWhir_assigneeName(),task.getAssignee());
				if(task.getWhir_isProxy()==1){
					userInfoVo.setIsStandFor(1);
					userInfoVo.setMyProxyUserAccount(task.getWhir_proxyAssignee());
					userInfoVo.setMyProxyUserName(task.getWhir_proxyAssigneeName());
				}	
				vo.getDealTransactorInfoVO().addIdentityVO(userInfoVo);		    		   		    
			}else{
				//本活动第一次设置
				vo=new ChoosedActivityVO();
				TransactorInfoVO   dealUserVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_USERS);		    
				vo.setActivityId(task.getTaskDefinitionKey());
				vo.setActivityName(task.getName());
				UserInfoVO userInfoVo=new UserInfoVO(null,task.getWhir_assigneeName(),task.getAssignee());
				if(task.getWhir_isProxy()==1){
					userInfoVo.setIsStandFor(1);
					userInfoVo.setMyProxyUserAccount(task.getWhir_proxyAssignee());
					userInfoVo.setMyProxyUserName(task.getWhir_proxyAssigneeName());
				}	
				dealUserVO.addIdentityVO(userInfoVo);	
				vo.setDealTransactorInfoVO(dealUserVO);
			    infoMap.put(task.getTaskDefinitionKey(), vo);	    
			}
		}	
		//把map 转化为list
		 Iterator<?> it = infoMap.entrySet().iterator();
         while (it.hasNext()) {      	
        	 Map.Entry entry = (Map.Entry) it.next();
        	 ChoosedActivityVO  value = (ChoosedActivityVO)entry.getValue();
        	 resultList.add(value);
         }	
		return resultList;
	}
	
	
	
	/**
	 * 找已经办理的活动  剔除 退回收回等 
	 * @param processInstanceId
	 * @return
	 */
	public List<ChoosedActivityVO> findDealedActivity_real(String processInstanceId){
		
		List<ChoosedActivityVO>  resultList=new ArrayList<ChoosedActivityVO> ();
		HistoricTaskInstanceQueryImpl  query=new HistoricTaskInstanceQueryImpl(commandContext);	
		//查询出当前在办所有任务
		List<HistoricTaskInstance> list=query.processInstanceId(processInstanceId).finished().taskDeleteReason("completed").whir_isForRead(0).list();
		Map<String,ChoosedActivityVO> infoMap=new HashMap<String,ChoosedActivityVO>();
	    ChoosedActivityVO vo=null;
		for(HistoricTaskInstance task:list){
		    //已经设置了
			if(infoMap.get(task.getTaskDefinitionKey())!=null){
				vo=infoMap.get(task.getTaskDefinitionKey());
				UserInfoVO userInfoVo=new UserInfoVO(null,task.getWhir_assigneeName(),task.getAssignee());
				if(task.getWhir_isProxy()==1){
					userInfoVo.setIsStandFor(1);
					userInfoVo.setMyProxyUserAccount(task.getWhir_proxyAssignee());
					userInfoVo.setMyProxyUserName(task.getWhir_proxyAssigneeName());
				}	
				vo.getDealTransactorInfoVO().addIdentityVO(userInfoVo);		    		   		    
			}else{
				//本活动第一次设置
				vo=new ChoosedActivityVO();
				TransactorInfoVO   dealUserVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_USERS);		    
				vo.setActivityId(task.getTaskDefinitionKey());
				vo.setActivityName(task.getName());
				UserInfoVO userInfoVo=new UserInfoVO(null,task.getWhir_assigneeName(),task.getAssignee());
				if(task.getWhir_isProxy()==1){
					userInfoVo.setIsStandFor(1);
					userInfoVo.setMyProxyUserAccount(task.getWhir_proxyAssignee());
					userInfoVo.setMyProxyUserName(task.getWhir_proxyAssigneeName());
				}	
				dealUserVO.addIdentityVO(userInfoVo);	
				vo.setDealTransactorInfoVO(dealUserVO);
			    infoMap.put(task.getTaskDefinitionKey(), vo);	    
			}
		}	
		//把map 转化为list
		 Iterator<?> it = infoMap.entrySet().iterator();
         while (it.hasNext()) {      	
        	 Map.Entry entry = (Map.Entry) it.next();
        	 ChoosedActivityVO  value = (ChoosedActivityVO)entry.getValue();
        	 resultList.add(value);
         }	
		return resultList;
	} 
 
 
	
	/**
	 * 找当前正在 办理的活动
	 * @param varMap
	 * @return
	 */
	public Map<String,Object> findDealingActivity_out(Map<String,Object> varMap){
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String processInstanceId=varMap.get("processInstanceId")==null?"":varMap.get("processInstanceId").toString();
		List<ChoosedActivityVO> list=findDealingActivity(processInstanceId);
		resultMap.put("dealingActivitys", list);
		return resultMap;	
	}
	
	
	/**
	 * 找当前正在 办理的活动
	 * @param processInstanceId
	 * @return
	 */
	public List<ChoosedActivityVO> findDealingActivity(String processInstanceId){
		
		List<ChoosedActivityVO>  resultList=new ArrayList<ChoosedActivityVO> ();
		TaskQueryImpl  query=new TaskQueryImpl(commandContext);	
		//查询出当前在办所有任务
		List<Task> list=query.processInstanceId(processInstanceId).whir_isForRead(0).list();
		Map<String,ChoosedActivityVO> infoMap=new HashMap<String,ChoosedActivityVO>();
	    TaskEntity taskEntity=null;
	    ChoosedActivityVO vo=null;
		for(Task task:list){
		    taskEntity=(TaskEntity)task;
		    //已经设置了
			if(infoMap.get(taskEntity.getTaskDefinitionKey())!=null){
				vo=infoMap.get(taskEntity.getTaskDefinitionKey());
				UserInfoVO userInfoVo=new UserInfoVO(null,taskEntity.getWhir_assigneeName(),taskEntity.getAssignee());
				if(taskEntity.getWhir_isProxy()==1){
					userInfoVo.setIsStandFor(1);
					userInfoVo.setMyProxyUserAccount(taskEntity.getWhir_proxyAssignee());
					userInfoVo.setMyProxyUserName(taskEntity.getWhir_proxyAssigneeName());
				}	
				vo.getDealTransactorInfoVO().addIdentityVO(userInfoVo);		    		   		    
			}else{
				//本活动第一次设置
				vo=new ChoosedActivityVO();
				TransactorInfoVO   dealUserVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_USERS);		    
				vo.setActivityId(taskEntity.getTaskDefinitionKey());
				vo.setActivityName(taskEntity.getName());
				UserInfoVO userInfoVo=new UserInfoVO(null,taskEntity.getWhir_assigneeName(),taskEntity.getAssignee());
				if(taskEntity.getWhir_isProxy()==1){
					userInfoVo.setIsStandFor(1);
					userInfoVo.setMyProxyUserAccount(taskEntity.getWhir_proxyAssignee());
					userInfoVo.setMyProxyUserName(taskEntity.getWhir_proxyAssigneeName());
				}	
				dealUserVO.addIdentityVO(userInfoVo);	
				vo.setDealTransactorInfoVO(dealUserVO);
			    infoMap.put(taskEntity.getTaskDefinitionKey(), vo);	    
			}
		}	
		//把map 转化为list
		 Iterator<?> it = infoMap.entrySet().iterator();
         while (it.hasNext()) {      	
        	 Map.Entry entry = (Map.Entry) it.next();
        	 ChoosedActivityVO  value = (ChoosedActivityVO)entry.getValue();
        	 resultList.add(value);
         }	
		return resultList;
	}
	
	
	/**
	 * 自动返回活动 自动返回
	 * @param executionEntity  
	 * @param nowActivityId  自动返回活动id
	 * @param backToTaskId   自动返回到的 活动的任务id
	 */
   public void  backtrackActivity(ActivityExecution executionEntity,String nowActivityId,String backToTaskId){
	   
	   /*    
                  线里条件表变量名	varName	activitityId+”_XOR_B_VAR”
                  连线里条件表达式		${ varName =='”+ toActivityId +”'}";
       */
	   if(!EzFlowUtil.judgeNull(backToTaskId)){
		   throw new ActivitiException("backtrackActivity  backToTaskId  is null" );

	   }
	   //自动返回活动id+"_XOR_B_VAR";   
	   String varName=nowActivityId+"_XOR_B_VAR";
	   HistoricTaskInstanceEntity  histTask=commandContext.getHistoricTaskInstanceManager().findHistoricTaskInstanceById(backToTaskId);
	   //给条件表达式中变量赋值 ， 使表达式成立。
	   executionEntity.setVariable(varName, histTask.getTaskDefinitionKey());
	   
	   //取 活动的上一次的办理人
	   HistoricTaskInstanceQueryImpl hiTaskQuery=new HistoricTaskInstanceQueryImpl(commandContext);
	   List<HistoricTaskInstance> htasklist = hiTaskQuery.processInstanceId(histTask.getProcessInstanceId())
				           .whir_isForRead(new Integer(0))// 办件
				           .whir_activityFrequency(histTask.getWhir_activityFrequency()) //活动办理的批次
				           .taskDefinitionKey(histTask.getTaskDefinitionKey()).list_whir(); 
	   
	   List<String> assigneList=new ArrayList<String>();
	   for(HistoricTaskInstance task:htasklist){
		   //不要加重复的
		   if(!assigneList.contains(task.getAssignee())){
		      commandContext.addDealUserInfo(new UserInfoVO("",task.getWhir_assigneeName(),task.getAssignee())); 
		      assigneList.add(task.getAssignee());
		   }
	   }
	   
	   //统一设置办理人的代理人
	   commandContext.dealUserProxy();
	   
	   
	   //设置办理人变量
	   String assigneVarName=histTask.getTaskDefinitionKey()+"_assigneeList";
	   executionEntity.setVariable(assigneVarName, assigneList);
	    
   }
   
   
   
   /**
    * 处理 已办任务   当历史任务里 一个办理人有多个办理任务时  把前面的做删除标记
    * @param processInstanceId
    */
   public  void  dealedInfo(String processInstanceId){
 
	  //记录 此次办理 结束的 历史任务
 	  List<HistoricTaskInstanceEntity> markEndedHistoricTaskInstanceList=new ArrayList<HistoricTaskInstanceEntity>();
 	  if(commandContext.getDealingProperty("markEndedHistoricTaskInstance")!=null){
 		  markEndedHistoricTaskInstanceList=(List<HistoricTaskInstanceEntity>)commandContext.getDealingProperty("markEndedHistoricTaskInstance");
 	  } 
 	 
 	  List<String> whir_taskAssignees=new ArrayList<String>();
 	  for(HistoricTaskInstanceEntity task:markEndedHistoricTaskInstanceList){
 		  //不要重复的    并且是办件任务 不是阅件任务
 		  if(!whir_taskAssignees.contains(task.getAssignee())&&task.getWhir_isForRead()==0){
 			 whir_taskAssignees.add(task.getAssignee());
 		  }
 	  }
     
 	 //-----处理wfWorkFlow
 	 String inTasksql="";
 	 Dbutil dbuitl=new Dbutil();
 	 
 	 if(whir_taskAssignees!=null&&whir_taskAssignees.size()>0){
	 	 String sql="select HTI.ID_,HTI.ASSIGNEE_,HTI.END_TIME_,HTI.WHIR_ISPROXY,HTI.whir_isForRead,HTI.WHIR_ISDELETED "+
	                " from EZ_FLOW_HI_TASKINST HTI   where   HTI.PROC_INST_ID_=:pid "; 
	 	 Map sMap=new HashMap();
	 	 sMap.put("pid", processInstanceId);
	 	 try {
			List tlist=dbuitl.getDataListBySQL(sql, sMap);
			if(tlist!=null&&tlist.size()>0){ 
				Object obj[]=null; 
				for(int i=0;i<tlist.size();i++){
					obj=(Object [])tlist.get(i); 
					String t_Assignees=obj[1].toString();
					String  t_ftime=obj[2]==null?"":obj[2].toString();
					if(t_ftime.equals("null")){
						t_ftime="";
					}
					String t_isproxy=obj[3]==null?"":obj[3].toString();
					String t_isRead=obj[4]==null?"":obj[4].toString();
					String  t_delete=obj[5]==null?"":obj[5].toString();
					if(t_delete.equals("0")&&t_isRead.equals("0")&&t_isproxy.equals("0")&&!t_ftime.equals("")&&whir_taskAssignees.contains(t_Assignees)) {
						inTasksql="'"+obj[0]+"',";
					}
				} 
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
 	 
 	 /* 国产化 性能问题作废 换为上面的写法
 	 if(whir_taskAssignees!=null&&whir_taskAssignees.size()>0){
	 		HistoricTaskInstanceQueryImpl hisQuery=new HistoricTaskInstanceQueryImpl(Context.getCommandContext());
			List<HistoricTaskInstance> hisTasklist = hisQuery
					.processInstanceId(processInstanceId)
	                .whir_taskAssignees(whir_taskAssignees)//办理人串
					.finished()//已经办理完毕的
					.whir_isForRead(new Integer("0"))//办件
					.whir_isDeleted(new Integer("0"))// 没有被删除的
					.list_whir();
	        for(HistoricTaskInstance hisTask:hisTasklist){
				HistoricTaskInstanceEntity hisTaskEntity=(HistoricTaskInstanceEntity)hisTask;
				hisTaskEntity.setWhir_isDeleted(1);
				inTasksql="'"+hisTaskEntity.getId()+"',";
			}
 	  }*/  
 	  if(EzFlowUtil.judgeNull(inTasksql)){
 		 inTasksql=inTasksql.substring(0,inTasksql.length()-1);
 		 String  updateSql=" update  WF_WORK  SET WORKDELETE=1 WHERE  ISEZFLOW=1  AND EZFLOWTASKID IN ("+inTasksql+")";	 
 		 String  updateEzFLOWSql=" update  EZ_FLOW_HI_TASKINST  SET WHIR_ISDELETED=1 WHERE   ID_ IN ("+inTasksql+")";	 
 		 try {
 			dbuitl.excuteBySQLWithVarMap(updateEzFLOWSql, null);
			dbuitl.excuteBySQLWithVarMap(updateSql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		 //UtilService utilService=new UtilService();
 		 //utilService.updateBySql(updateSql);		  
 	  }
 
   }
   
   
   /**
    * 处理 已办任务   当历史任务里 一个办理人有多个办理任务时  把前面的做删除标记
    * 
    * 【新工作流】已办文件中，收回流程后，办理过该流程的办理人的已办文件中该流程记录应该不显示，参照老工作流效果。
    * @param processInstanceId
    */
   public  void  dealedInfo(UserInfoVO curUserInfoVO,String type,String processInstanceId){

		  //记录 此次办理 结束的 历史任务
	 	  List<HistoricTaskInstanceEntity> markEndedHistoricTaskInstanceList=new ArrayList<HistoricTaskInstanceEntity>();
	 	  if(commandContext.getDealingProperty("markEndedHistoricTaskInstance")!=null){
	 		  markEndedHistoricTaskInstanceList=(List<HistoricTaskInstanceEntity>)commandContext.getDealingProperty("markEndedHistoricTaskInstance");
	 	  } 
	 	  
	 	  
	 	 //-----处理wfWorkFlow
	     String inTasksql="";
	 	 
	 	  List<String> whir_taskAssignees=new ArrayList<String>();
	 	  for(HistoricTaskInstanceEntity task:markEndedHistoricTaskInstanceList){
	 		  //不要重复的    并且是办件任务 不是阅件任务
	 		  if(!whir_taskAssignees.contains(task.getAssignee())&&task.getWhir_isForRead()==0){
	 			 whir_taskAssignees.add(task.getAssignee());
	 			inTasksql="'"+task.getId()+"',";
	 		  }
	 	  }
	    
	 	  if(whir_taskAssignees!=null&&whir_taskAssignees.size()>0){
		 		HistoricTaskInstanceQueryImpl hisQuery=new HistoricTaskInstanceQueryImpl(Context.getCommandContext());
				List<HistoricTaskInstance> hisTasklist = hisQuery
						.processInstanceId(processInstanceId)
		                .whir_taskAssignees(whir_taskAssignees)//办理人串
						.finished()//已经办理完毕的
						.whir_isForRead(new Integer("0"))//办件
						.whir_isDeleted(new Integer("0"))// 没有被删除的
						.list_whir();
		        for(HistoricTaskInstance hisTask:hisTasklist){
					HistoricTaskInstanceEntity hisTaskEntity=(HistoricTaskInstanceEntity)hisTask;
					hisTaskEntity.setWhir_isDeleted(1);
					if(inTasksql.indexOf("'"+hisTaskEntity.getId()+"'")<=0){
				    	inTasksql+="'"+hisTaskEntity.getId()+"',";
					}
				}
	 	  }
	  
	 	 
	 	  if(EzFlowUtil.judgeNull(inTasksql)){
	 		 inTasksql=inTasksql.substring(0,inTasksql.length()-1);
	 		 String  updateSql=" update  WF_WORK  SET WORKDELETE=1 WHERE  ISEZFLOW=1  AND EZFLOWTASKID IN ("+inTasksql+")";	 
	 		 Dbutil dbuitl=new Dbutil();
	 		 try {
				dbuitl.excuteBySQLWithVarMap(updateSql, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		 //UtilService utilService=new UtilService();
	 		 //utilService.updateBySql(updateSql);		  
	 	  }
   }
    
   
   
   //--------------------------处理办理期限-----------start------------------------
   /**
    * 处理办理期限
    * @param executionEntity
    */
   public void  dealDueDate(TaskEntity task,ExecutionEntity executionEntity){
	   //当前所在的活动
	   ActivityImpl activity=executionEntity.getActivity();
	   String  whir_overdueType=activity.getProperty("whir_overdueType")+"";
	   
	   //固定办理期限
	   if(whir_overdueType.equals("1")){
		   dealDueDateFinal(task,executionEntity);   
	   }
	   
	   //自定义办理期限
	   if(whir_overdueType.equals("2")){
		   dueDateCustom(task,executionEntity);   
	   }
   }
   
   
   
   /**
    * 处理固定办理期限
    * @param task
    * @param executionEntity
    */
   private void  dealDueDateFinal(TaskEntity task,ExecutionEntity executionEntity){
	   //2012-06-19T15:59:14
	   //当前所在的活动
	   ActivityImpl activity=executionEntity.getActivity();
	   //过期后执行的事件
	   /* 系统中提醒   ,system, 即时通讯提醒 ,IM, 催办    ,press, 自动跳转,autoDeal,  */
	   String overdueDealType=activity.getProperty("whir_overdueDealType")+"";
	   
	   List<Map<String,String>> whir_dueDateFinalList=new ArrayList<Map<String,String>>();
	   
	   if(activity.getProperty("whir_dueDateFinal")!=null){
		   whir_dueDateFinalList=(List<Map<String,String>>)activity.getProperty("whir_dueDateFinal");
	   }
	   if(whir_dueDateFinalList!=null&&whir_dueDateFinalList.size()>0){
		   Map<String,String> whir_dueDateMap=(Map<String,String>)whir_dueDateFinalList.get(0);
		   dealTaskTimerWithMap(task,whir_dueDateMap,overdueDealType,executionEntity);
	   } 
   }
   
   
   /**
    * 处理自定义办理期限
    * @param task
    * @param executionEntity
    */
   private void  dueDateCustom(TaskEntity task,ExecutionEntity executionEntity){
	   //2012-06-19T15:59:14
	   //当前所在的活动
	   ActivityImpl activity=executionEntity.getActivity();
	   //过期后执行的事件
	   /* 系统中提醒   ,system, 即时通讯提醒 ,IM, 催办    ,press, 自动跳转,autoDeal,  */
	   String overdueDealType=activity.getProperty("whir_overdueDealType")+"";
	   
	   //从活动设置里取   办理期限信息   因为 自定义 可能有多个
	   List<Map<String,String>> whir_dueDateCustomList=new ArrayList<Map<String,String>>();
	   if(activity.getProperty("whir_dueDateCustom")!=null){
		   whir_dueDateCustomList=(List<Map<String,String>>)activity.getProperty("whir_dueDateCustom");
	   }
	   
	   if(whir_dueDateCustomList!=null&&whir_dueDateCustomList.size()>0){
		   for(Map<String,String> whir_dueDateMap:whir_dueDateCustomList){
			    
			    String fieldName=whir_dueDateMap.get("fieldName");//字段名   就是变量名
			    String operateStr=whir_dueDateMap.get("operateStr");//比较符
			    String fieldValue=whir_dueDateMap.get("fieldValueStr");//比较值  fieldValueStr
			    
			    
			    String jobUserIds=whir_dueDateMap.get("customPressUserIds");// 提醒人 
   
			    String expression = "${"+fieldName+""+operateStr+""+fieldValue+"}"; 
			    
			    String type = "tFormalExpression";
				ExpressionManager expressionManager = Context
						.getProcessEngineConfiguration().getExpressionManager();
			    Condition expressionCondition = new UelExpressionCondition(expressionManager.createExpression(expression));
			    
			    //如果条件满足    执行第一满足的
			    if( expressionCondition.evaluate(executionEntity)){
			    	dealTaskTimerWithMap(task,whir_dueDateMap,overdueDealType,executionEntity,jobUserIds);
			    	break;
			    }
 		  
		   }
	   } 
   }
   
   /**
    * 
    * @param task
    * @param whir_dueDateMap
    * @param overdueDealType
    * @param executionEntity
    */
   private void dealTaskTimerWithMap(TaskEntity task,Map<String,String> whir_dueDateMap,String overdueDealType,ExecutionEntity executionEntity   ){
	   dealTaskTimerWithMap(  task, whir_dueDateMap,  overdueDealType,  executionEntity,null);
   }
   /**
    * 根据Map里信息  设置 办理期限 提醒时间  以及 创建 job
    * @param task
    * @param whir_dueDateMap
    * @param overdueDealType
    * @param executionEntity
    */
   private void dealTaskTimerWithMap(TaskEntity task,Map<String,String> whir_dueDateMap,String overdueDealType,ExecutionEntity executionEntity ,String jobUserIds ){
	   String timeNum=whir_dueDateMap.get("timeNum");//"1";  时间的数量
	   
	   /**  <OPTION selected value="1">小时</OPTION>
          <OPTION value="0">天</OPTION>   时间类型： 小时：hour   天：date*/
	   String timeType=whir_dueDateMap.get("timeType");//"1"; 
	   String isPreRemind=whir_dueDateMap.get("isPreRemind");//"false"; 是否提前提醒
	   String preRemindTime=whir_dueDateMap.get("preRemindTime");//"900"; //15分钟		      
	 
	   Date  dueDate=new java.util.Date();
	   Date  preRemindDate=dueDate;
	   
	   UserInfoVO  userInfoVO = Context.getCommandContext().getUserInfo(task.getAssignee());	   
	   com.whir.common.util.WfUtils  wfUtils = new com.whir.common.util.WfUtils(userInfoVO.getUserId());
   
       // now 当前时间     deadLineTime 过期时间      pressMotionTimeType 0：天  1：小时
       dueDate = wfUtils.getOverDate(nowCalendar.getTime(), Integer.parseInt(timeNum), new Integer(timeType).intValue());
      
       if(isPreRemind.equals("true")){
    	   preRemindDate=new java.util.Date(dueDate.getTime()-Long.parseLong(preRemindTime)*1000);
       }else{
    	   preRemindDate=dueDate;
       }

        /* 
	   if(timeType.equals("1")){
		   Calendar nowCal = Calendar.getInstance(); 
		   nowCal.add(Calendar.HOUR, new Integer(timeNum).intValue());//增加小时
		   dueDate=nowCal.getTime();
	   }
	   
	   if(timeType.equals("0")){
		   Calendar nowCal = Calendar.getInstance(); 
		   nowCal.add(Calendar.DATE, new Integer(timeNum).intValue());//增加一天    
		   dueDate=nowCal.getTime();
	   }
	   */
	   /*
	    * 
	   Calendar preRemindDateCal = Calendar.getInstance();
	   preRemindDateCal.setTime(dueDate);
	   int  preRemindTime_int=Integer.parseInt(preRemindTime)*-1;
	   System.out.println("preRemindTime_int:"+preRemindTime_int);
	   preRemindDateCal.add(Calendar.SECOND, preRemindTime_int);
       Date  preRemindDate=preRemindDateCal.getTime();
	    * */
     
       //需要提前提醒     在期限时间基础上减去 提前时间 
      
	   task.setDueDate(dueDate);
	   
	   createJob(overdueDealType,dueDate,preRemindDate,executionEntity,jobUserIds);	   
   }
   
   /**
    * 生成job
    * @param overdueDealType    过期后执行的事件  系统中提醒   ,system, 即时通讯提醒 ,IM, 催办    ,press, 自动跳转,autoDeal,
    * @param dueDate      到期时间 
    * @param preRemindDate   提醒时间
    * @param executionEntity
    * @param jobUserIds job的操作对象
    */
   private void  createJob(String overdueDealType , Date  dueDate,Date preRemindDate,ExecutionEntity executionEntity,String jobUserIds ){
	   //自动跳转
	   if(overdueDealType.indexOf(",autoDeal,")>=0){
		   TimerEntity timer = dealTaskTimerDeclaration(dueDate,executionEntity.getActivityId(),Whir_TimerExecuteJobHandler.TYPE)
					.prepareTimerEntity(executionEntity);
			Context.getCommandContext().getJobManager().schedule(timer);
	   }else if(overdueDealType.indexOf(",autoTran,")>=0){
		   //自动转交  
		   TimerEntity timer = dealTaskTimerDeclaration(dueDate,executionEntity.getActivityId(),Whir_autoTranTimerExecuteJobHandler.TYPE)
					.prepareTimerEntity(executionEntity);
			Context.getCommandContext().getJobManager().schedule(timer);
		   
	   }
	   // 系统 提醒，  RTX提醒 ， 催办，短信提醒
	   if (overdueDealType.indexOf(",system,") >= 0
				|| overdueDealType.indexOf(",IM,") >= 0
				|| overdueDealType.indexOf(",press,") >= 0
				|| overdueDealType.indexOf(",note,") >= 0) {
			// 提醒
		   TimerEntity timer = dealTaskTimerDeclaration(preRemindDate,executionEntity.getActivityId(),Whir_RemindTimerExecuteJobHandler.TYPE)
					.prepareTimerEntity(executionEntity);
		   //设置参与人
		   timer.setJobUserIds(jobUserIds);
		   Context.getCommandContext().getJobManager().schedule(timer);
	   }
	   
   }
   
    /**
     * 
     * 返回 TimerDeclarationImpl 对象 
     * @param dueDate     到期时间 或者 提醒时间      作为job的触发时间
     * @param activityId  所在活动id
     * @param timeType    job类型
     * @return
     */
	private TimerDeclarationImpl dealTaskTimerDeclaration(Date  dueDate,String activityId,String timeType) {
		// 参照 parseBoundaryTimerEventDefinition
		// 过期执行类型
		// TimeDate
		TimerDeclarationType type = TimerDeclarationType.DATE;
		 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		/*--------------------测试数据--------------------*/
		//String timeDate = "2012-06-19T15:59:14";
		String timeDate = simpleDateFormat.format(dueDate);
		ExpressionManager expressionManager = Context
				.getProcessEngineConfiguration().getExpressionManager();
		// String timeDate=overdueType;
		Expression expression = expressionManager.createExpression(timeDate);
		// TimeCycle
		if (expression == null) {
			type = TimerDeclarationType.CYCLE;
			String timeCycle = "";
			expression = expressionManager.createExpression(timeCycle);
		}
		// TimeDuration
		if (expression == null) {
			type = TimerDeclarationType.DURATION;
			String timeDuration = "";
			expression = expressionManager.createExpression(timeDuration);
		}
		// neither date, cycle or duration configured!
		if (expression == null) {
		}

		// Parse the timer declaration
		// TODO move the timer declaration into the bpmn activity or next to the
		// TimerSession
		TimerDeclarationImpl timerDeclaration = new TimerDeclarationImpl(
				expression, type, timeType);
		
		timerDeclaration.setJobHandlerConfiguration(activityId);
		// 默认
		// timerDeclaration.setExclusive("true".equals(timerEventDefinition.attributeNS(BpmnParser.ACTIVITI_BPMN_EXTENSIONS_NS,
		// "exclusive", String.valueOf(JobEntity.DEFAULT_EXCLUSIVE))));
        return timerDeclaration;
	}
	
	//--------------------------处理办理期限-----------end ------------------------
	
	
	
	
	
	
	/**
	 * 在当前环境下  是退回的任务 ，  直接发送到从哪退回的
	 * @param executionEntity
	 * @param joinedExecutions
	 * @return
	 */
	public  int  dealSendToBack(ActivityExecution  executionEntity,List<ActivityExecution> joinedExecutions){		
		int result=0;
		
		//从哪个任务退回过来的
		String  whir_backFromTaskId=""+commandContext.getDealingProperty("cur_whir_backFromTaskId");
		//是否是退回的任务 并且需要  返回退回过来的活动
		String  whir_isneedSendtoBack=""+commandContext.getDealingProperty("cur_whir_isneedSendtoBack");
		if(EzFlowUtil.judgeNull(whir_backFromTaskId)||EzFlowUtil.judgeNull(whir_isneedSendtoBack)){
			//从历史任务里找
		    HistoricTaskInstanceEntity  histBackFromTask=commandContext.getHistoricTaskInstanceManager().findHistoricTaskInstanceById(whir_backFromTaskId);
	        //取 活动的上一次的办理人
			HistoricTaskInstanceQueryImpl hiTaskQuery=new HistoricTaskInstanceQueryImpl(commandContext);
			List<HistoricTaskInstance> htasklist = hiTaskQuery.processInstanceId(histBackFromTask.getProcessInstanceId())
						           .whir_isForRead(new Integer(0))// 办件
						           .whir_activityFrequency(histBackFromTask.getWhir_activityFrequency()) //活动办理的批次
						           .taskDefinitionKey(histBackFromTask.getTaskDefinitionKey()).list_whir(); 
			//办理人  
			List<String> assigneList=new ArrayList();
			for(HistoricTaskInstance task:htasklist){
			    //不要加重复的
			    if(!assigneList.contains(task.getAssignee())){
			       commandContext.addDealUserInfo(new UserInfoVO("",task.getWhir_assigneeName(),task.getAssignee())); 
			       assigneList.add(task.getAssignee());
			    }
			} 
			
			
		   //统一设置办理人的代理人
		   commandContext.dealUserProxy();
			   
			
			 //设置办理人变量
		   String assigneVarName=histBackFromTask.getTaskDefinitionKey()+"_assigneeList";
		   executionEntity.setVariable(assigneVarName, assigneList);
		   
		   
			//获取流程定义
			ProcessDefinitionEntity processDefinition  = Context
						.getProcessEngineConfiguration()
						.getDeploymentCache()
						.findDeployedProcessDefinitionById(histBackFromTask.getProcessDefinitionId());
			
			ActivityImpl destinationActivity = processDefinition.findActivity(histBackFromTask.getTaskDefinitionKey());		
			ActivityImpl cur_activity=(ActivityImpl)executionEntity.getActivity();
			PvmTransition dynamic_transition = createSequenceFlow(cur_activity,destinationActivity);
			
			List<PvmTransition> outgoingTransitions=new ArrayList<PvmTransition>();
			outgoingTransitions.add(dynamic_transition);
			executionEntity.takeAll(outgoingTransitions, joinedExecutions);
			
			 /*办理结束后 需要删除缓存里的 动态连线  还原*/
			//删除
			if (cur_activity != null && dynamic_transition != null) {
	            if(cur_activity.getOutgoingTransitions()!=null){
	            	cur_activity.getOutgoingTransitions().remove(dynamic_transition);
	            }
			}
			if (destinationActivity != null && dynamic_transition != null) {
				if(destinationActivity.getIncomingTransitions()!=null){
					destinationActivity.getIncomingTransitions().remove(dynamic_transition);
				}    
			}
			
			result=1; 
		}else{
			throw new ActivitiException(" whir_backFromTaskId:" + whir_backFromTaskId+"whir_isneedSendtoBack:"+whir_isneedSendtoBack); 
		}
		return  result;
	}
	
	
	
	/**
	 * 创建动态  连线    dealSendToBack 需要
	 * @param cur_activity  当前的活动的定义
	 * @param destinationActivity  目标的活动定义
	 * @return
	 */
	private PvmTransition createSequenceFlow(ActivityImpl cur_activity,ActivityImpl destinationActivity) {
		TransitionImpl dynamic_transition=null;
		String cur_activityId=cur_activity.getId();
		String destinationRef=destinationActivity.getId();
		//动态连线 名称
		String name = "sendTobackName";
		//动态连线id
		String id = "sendToBack_"+cur_activityId+"_"+destinationRef+(new java.util.Date().getTime());		 
		
		if (cur_activity == null) {
			// error
		} else if (destinationActivity == null) {
			// error
		} else if (cur_activity.getActivityBehavior() instanceof EventBasedGatewayActivityBehavior) {
			// ignore
		} else if (destinationActivity.getActivityBehavior() instanceof IntermediateCatchEventActivitiBehaviour
				&& (destinationActivity.getParentActivity() != null)
				&& (destinationActivity.getParentActivity()
						.getActivityBehavior() instanceof EventBasedGatewayActivityBehavior)) {
			// "Invalid incoming sequenceflow for intermediateCatchEvent with id '"+
			// destinationActivity.getId()+
			// "' connected to an event-based gateway.";
		} else {
			dynamic_transition = cur_activity.createOutgoingTransition(id);		
			//dynamic_transition = new TransitionImpl(id, processDefinition);
			dynamic_transition.setProperty("name", name);
			dynamic_transition.setDestination(destinationActivity);
		}
		return dynamic_transition;
	}
   
	
	
	
	/**
	 * 求能 批量办理的任务id  流程的定义 相同    所在的活动也相同
	 * @param map
	 * @return
	 */
	public  Map<String,Object>  getBatchUpateIds_out(Map<String,Object> map){
		Map<String,Object> resultMap=new HashMap<String,Object>();	
		//第一个 taskId
		String firstTaskId=map.get("firstTaskId")==null?"":map.get("firstTaskId").toString(); 
		//首个任务
		TaskEntity firstTask = commandContext.getTaskManager().findTaskById(firstTaskId); 
		boolean canbatch=true;
		String actId=firstTask.getTaskDefinitionKey();
		String processDefinitionId=firstTask.getProcessDefinitionId();	 
		ProcessDefinitionEntity deployedProcessDefinition = Context
		.getProcessEngineConfiguration().getDeploymentCache()
		.findDeployedProcessDefinitionById(processDefinitionId);
		List<ActivityImpl> actlist =deployedProcessDefinition.getActivities();
		ActivityImpl  nowAct=null;
		for(ActivityImpl a:actlist){
			if(actId.equals(a.getId())){
				nowAct=a;
			}
		} 
		if(nowAct!=null){
			List<PvmTransition> nextActList=nowAct.getOutgoingTransitions();
			if(nextActList!=null&&nextActList.size()>0){
				for(PvmTransition t:nextActList){
					PvmActivity  nact= t.getDestination();
					String aid=nact.getId();
					if(aid.startsWith("inclusivegateway")||aid.startsWith("exclusivegateway")||aid.startsWith("subprocess")){
						canbatch=false;
					}
				}
			} 
		}
  
		//不能批量办理
		if(!canbatch){
			resultMap.put("restultTaskIds","");
			return resultMap;
		}
		
		//其它taskId
		String otherTaskId=map.get("otherTaskId")==null?"":map.get("otherTaskId").toString();
		String otherTaskIdArr[]=otherTaskId.split(",");	
		List<String>list=new ArrayList<String>();	
		if(otherTaskIdArr!=null&&otherTaskIdArr.length>0){
			for(String str:otherTaskIdArr){
				list.add(str);
			}
		}
		
		//其它任务 
		List <Task>taskList=new TaskQueryImpl(commandContext).whir_taskIds(list).list_whir();
		String   restultTaskIds="";
		for(Task task:taskList){
			  //流程的定义 相同    所在的活动也相同
			 if(task.getProcessDefinitionId().equals(firstTask.getProcessDefinitionId())
					 &&task.getTaskDefinitionKey().equals(firstTask.getTaskDefinitionKey())){
				 restultTaskIds+=task.getId()+",";
			 }
		} 
		resultMap.put("restultTaskIds", restultTaskIds);
		return  resultMap;
	}
 
}
