package com.whir.ezflow.actionsupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.log4j.Logger;

import com.whir.common.db.Dbutil;
import com.whir.common.util.CommonUtils;
import com.whir.ezflow.util.EzFlowFinals;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.ChoosedActivityVO;
import com.whir.ezflow.vo.TransactorInfoVO;
import com.whir.ezflow.vo.UserInfoVO;
import com.whir.ezoffice.bpm.bd.BPMJobStartBD;
import com.whir.ezoffice.formhandler.EzFlow;
import com.whir.service.api.ezflowservice.EzFlowMainService;
import com.whir.service.api.ezflowservice.EzFlowProcessDraftService;
import com.whir.service.api.ezflowservice.EzFlowTransactorService;

/**
 * 流程发起 及 办理
 * @author wanggl
 *
 */
public class EzFlowTransactAction extends EzFlowBaseAttrAction {
	
	private static Logger logger = Logger.getLogger(EzFlowTransactAction.class.getName());
	
	protected String gdInfo="";
	
	protected boolean autoOpenDealFile =false;
	
	//判断下一活动是否结束活动2016-01-19
	protected String isEndActivity ="";
	
	private List<String> canNotBatchDealWithTaskIds=new ArrayList<String>(); 
	/**
	 * 发起流程的查找下一活动办理信息
	 * @param actionMapping
	 * @param actionForm
	 * @param request
	 * @param response
	 * @return  
	 * @throws Exception
	 */
	public String startProcess_init() throws Exception {
		logger.debug("开始发送时 选择活动");
		String tag="startOpen_init";
		//取不到session 中的当前人信息
		/**
		 * 
		 */
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
		
		//流程主服务
		EzFlowMainService mainService=new EzFlowMainService();
		//流程变量
		Map varMap=dealVarMap(request); 
		dealTableInfoMap(varMap);
		resetUserOrg();  
		Map resultMap=null;
		try{
		     resultMap=mainService.findNextActivityWithStart(null, this.p_wf_processId, varMap, curUserInfoVO);
		}catch(Exception e){
			logger.debug("startProcess_init findNextActivityWithStart  出现 异常");  
            String errormessage=EzFlowUtil.dealWhirException(e);
            request.setAttribute("errorInfo", errormessage);
            return "error_detail"; 
		}
		
		
		//给页面赋值 下一办理活动信息
		setNextActivityInfoToJsp(resultMap,request);
	    //设置短信内容
        setMessageContent(request);
		
		logger.debug("结束发送时 选择活动");
		return "showActivitys";
	} 
	
	
	
	/**
	 * 发起流程
	 * @param actionMapping
	 * @param actionForm
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String startProcess() throws Exception {
		logger.debug("开始发起流程");
		/**
		 * 
		 */
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
		
		if(!judgeAllFields()){
			logger.debug("非法访问嫌疑");
			return this.NO_ILLEGAL;
		}
		
		//流程主服务
		EzFlowMainService mainService=new EzFlowMainService();
 	
		//发起结果
		long  startResult=1;	
 
		//选择的办理活动
		List choosedNextActivitys=dealChoosedNextActivitys(request);
		
		//批量发起字段
		String batchSendUserIds=request.getParameter("batchSendUserIds")==null?"":request.getParameter("batchSendUserIds").toString();					
		
		boolean needDealBatchStart=false; 
		if(EzFlowUtil.judgeNull(batchSendUserIds)&&this.p_wf_openType.equals("startOpen")){
			needDealBatchStart=true; 
		}
		
		//没有选择下一活动  或者选择的下一活动里有 某个没有选择办理人
		if(choosedNextActivitys==null||choosedNextActivitys.size()<=0){
			startResult=-1;
		}else{
			//流程变量
			Map varMap=dealVarMap(request);
			
			//流程附加信息  比如 提醒字段  批示意见等等
			Map extendMap=dealRemind(request,needDealBatchStart);
			//tips  短信提醒  邮件提醒 等
			dealTipsExtendMap(extendMap,request);
			//处理相关流程Map
			dealRealtionMap(extendMap,request);
			
			//加入 此流程发起 是哪个task 发送过来 的 
			extendMap.put("whir_startProcessFromTaskId", request.getParameter("whir_startProcessFromTaskId"));
			
			 //此次发送经过几个网关
		    int gateNum=0;
		    String gateNumStr=request.getParameter("gateNum");	    
		    if(!EzFlowUtil.judgeNull(gateNumStr)){
		    	startResult=-2;
		    }else{
		        gateNum=Integer.parseInt(gateNumStr);
		    }
 
			////<input type="hidden" name="batchSendUserIds" value="字段名称">
			//$userId$$userID$
			if(startResult>=0&EzFlowUtil.judgeNull(batchSendUserIds)&&this.p_wf_openType.equals("startOpen")){
				//
				System.out.println("重新发送1");
				String 	batchSendUserIds_value=request.getParameter(batchSendUserIds);
				if(EzFlowUtil.judgeNull(batchSendUserIds_value)){
					//改为 userId,userId的形式
				    batchSendUserIds_value=EzFlowUtil.dealStrForIn(batchSendUserIds_value, '$', null);
				    logger.debug("batchSendUserIds_value:"+batchSendUserIds_value);
				    EzFlowTransactorService tranService=new EzFlowTransactorService();
				    List userList=tranService.getUserInfoVOsByEmpIds(batchSendUserIds_value);
				    String batchSendUserIds_valueArr[]=batchSendUserIds_value.split(",");
				     
				    // varMap.put("remindTitle", remindTitle);     
				    String all_remindTitle=extendMap.get("remindTitle")==null?"":extendMap.get("remindTitle").toString();
				    
				    if(userList!=null&&userList.size()>0){
				    	UserInfoVO eachVo=null;
				    	for(int i=0;i<userList.size();i++){
				    		eachVo=(UserInfoVO)userList.get(i);
				    		logger.debug("id:"+eachVo.getId());
				    		logger.debug("name:"+eachVo.getName());
				    		logger.debug("userAccount:"+eachVo.getUserAccount());
				    		//业务主键id		
							EzFlow ezFlow=new EzFlow();
							Map  businessMap=ezFlow.dealFlow("batchSave", request,eachVo.getUserId());
							String businessKey=""+businessMap.get("infoId"); 
							
							//批量提交处理提醒字段  
							if(true){ 
						        Map fieldMap=new HashMap();   
					            fieldMap.put("condi_start_userName", new String[]{eachVo.getUserName()});
								String _all_remindTitle=all_remindTitle;
								List <String> remindFiledList=new ArrayList<String>();
								remindFiledList.add("{condi_start_userName}");  
								_all_remindTitle=EzFlowUtil.returnRemindValue(_all_remindTitle,remindFiledList,fieldMap);
								extendMap.put("remindTitle",_all_remindTitle);
							} 
							
							if (EzFlowUtil.judgeNull(businessKey)&& !businessKey.equals("-1")) {
								mainService.startProcessInstanceById(p_wf_processId, businessKey, varMap, choosedNextActivitys, gateNum, eachVo,extendMap);
								//保存相关流程
							    startWithRelation(businessKey);
							}else{
								startResult=-3;
								break;
							}			
				    	}
 				    }
				}else{
					startResult=-4;
				}
			//单个发起	 
			}else if(startResult>=0){	
				System.out.println("重新发送2");
				//业务主键id
				String businessKey="";	
				String draftID="";
				EzFlow ezFlow=new EzFlow();
				//定时发送
			    if(this.p_wf_dealWithJob.equals("1")){
			    	System.out.println("重新发送3");
			    	
			    	if (this.p_wf_openType.equals("fromDraft") && EzFlowUtil.judgeNull(p_wf_recordId)) {
						logger.debug("修改草稿");
						System.out.println("重新发送4");
						// 草稿箱修改
						Map businessMap = ezFlow.dealFlow("updateFromDraft", request, "");
						businessKey =p_wf_recordId;  
					} else{
						System.out.println("重新发送5");
				    	Map  businessMap=ezFlow.dealFlow("saveFromDraft", request, "");
						businessKey=""+businessMap.get("infoId");  
						//
						Map newMap = new HashMap();
						newMap.put("rev", "1");
						newMap.put("whir_formkey", this.p_wf_formKey);
						newMap.put("business_key", businessKey);
						String  whir_remindtitle=extendMap.get("remindTitle")==null?"":extendMap.get("remindTitle").toString();
						newMap.put("whir_remindtitle", whir_remindtitle);
						newMap.put("proc_def_id",this.p_wf_processId);
						newMap.put("proc_def_name",this.p_wf_processName);
						newMap.put("proc_def_key",this.p_wf_processDefinitionKey);
	
						newMap.put("create_userid", curUserInfoVO.getUserId());
						newMap.put("create_username", curUserInfoVO.getUserName());
						newMap.put("create_useraccount", curUserInfoVO.getUserAccount());
						newMap.put("create_orgid", curUserInfoVO.getOrgVO().getOrgId());
						newMap.put("create_orgname", curUserInfoVO.getOrgVO().getOrgLayerName());
	
						EzFlowProcessDraftService processDraftService = new EzFlowProcessDraftService();
						draftID = processDraftService.insert(newMap);
					}
			    	 
			    	BPMJobStartBD bd=new BPMJobStartBD();
			        Date jobStartTime=new Date(); 
			        String jobStartTime_str=request.getParameter("jobStartTime_str");
			        if(jobStartTime_str!=null&&!jobStartTime_str.equals("")&&!jobStartTime_str.equals("null")){
			        	// 当前时间格式
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    jobStartTime=simpleDateFormat.parse(""+request.getParameter("jobStartTime_str")); 
			        } 
			        bd.saveJob(this.p_wf_processId, businessKey, this.p_wf_processallVariables, choosedNextActivitys, gateNum, curUserInfoVO, extendMap, this.p_wf_formKey, this.p_wf_moduleId, jobStartTime,draftID);
			    	
				}else{
					System.out.println("重新发送6");
					 if(this.p_wf_openType.equals("fromDraft")){
					    	businessKey=p_wf_recordId; 
							//草稿箱发起
							Map  businessMap=ezFlow.dealFlow("update", request, "");				 	    	
					    	//删除草稿
					    	EzFlowProcessDraftService  draftService=new EzFlowProcessDraftService();
					    	Map deleteMap=new HashMap();
					    	deleteMap.put("business_key", p_wf_recordId);
					    	deleteMap.put("whir_formkey", p_wf_formKey);
					    	draftService.deleteByCriteria(deleteMap); 
					    	
		                    //删除定时器
							BPMJobStartBD  bd=new BPMJobStartBD();
							int moduleId=1;
							if(EzFlowUtil.judgeNull(p_wf_moduleId)){
								moduleId=Integer.parseInt(p_wf_moduleId);
							}
							bd.deleteJobStart(new Long(p_wf_recordId),moduleId);
		  	
					  }else{
						    Map  businessMap=ezFlow.dealFlow("save", request, "");
							businessKey=""+businessMap.get("infoId");
							System.out.println("businessKey1111:"+businessKey);
					  } 
				}
			    
			    logger.debug("businessKey:"+businessKey);
				if (EzFlowUtil.judgeNull(businessKey)&& !businessKey.equals("-1")) {
					System.out.println("重新发送7");
					resetUserOrg();
					 System.out.println("businessKey:"+businessKey);
				    System.out.println("p_wf_processId:"+p_wf_processId);
				    System.out.println("gateNum:"+gateNum);
				    System.out.println("choosedNextActivitys.size():"+choosedNextActivitys.size());
				    if(this.p_wf_dealWithJob.equals("1")){
				       
				    }else{
				    	//
				    	ProcessInstance processInstance=mainService.startProcessInstanceById(this.p_wf_processId, businessKey, varMap, choosedNextActivitys, gateNum, curUserInfoVO,extendMap);
						//if(this.p_wf_openType.equals("reStart")&&(this.p_wf_moduleId.equals("2")||this.p_wf_moduleId.equals("3")||this.p_wf_moduleId.equals("34"))){					 
				    	System.out.println("重新发送8");
				    	logger.debug("p_wf_processKeepReSubmitComment:"+p_wf_processKeepReSubmitComment);
				    	logger.debug("p_wf_openType:"+p_wf_openType);
				    	//保留重新发起前的批示意见
				    	//父流程实例：
				    	//有父流程 并且继承主表相同数据 
				    	String  p_wf_parentProcessInstanceId=request.getParameter("p_wf_parentProcessInstanceId")+""; 
				    	String  p_wf_extendMainTable_nowNeed=request.getParameter("p_wf_extendMainTable_nowNeed")+""; 
						if ((!p_wf_parentProcessInstanceId.equals("")&&!p_wf_parentProcessInstanceId.equals("null")&&p_wf_extendMainTable_nowNeed.equals("1"))||
								((p_wf_processKeepReSubmitComment != null && p_wf_processKeepReSubmitComment.equals("true"))&& this.p_wf_openType.equals("reStart"))) {
							
							if(p_wf_parentProcessInstanceId!=null&&!p_wf_parentProcessInstanceId.equals("")&&!p_wf_parentProcessInstanceId.equals("")){
								//mainService.changerLogToNew(p_wf_parentProcessInstanceId,processInstance.getId());
							}else{ 
								mainService.changerLogToNew(this.p_wf_processInstanceId,processInstance.getId());
							}
						}	
				    }
				    
				    //保存相关流程
				    startWithRelation(businessKey);
				}else{
					startResult=-3;
				}			
			}	
		} 
		
		//设置办理状态
		request.setAttribute("dealFlowResult", startResult);
		logger.debug("startResult:"+startResult);
		this.printResult(this.SUCCESS);
		logger.debug("结束发起流程");
		return null;
	}
	 
	
	/**
	 * 完成办理任务选择下一办理节点
	 * @param actionMapping
	 * @param actionForm
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String completeTask_init() throws Exception {
		logger.debug("开始 completeTask_init");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
		//流程主服务
		EzFlowMainService mainService=new EzFlowMainService();
		
	 
		/*代理信息      0： 正常
		 * 1： 代理其它办理的     
		 *          
		 */
		/*String  whir_isProxy=request.getParameter("whir_isProxy");
		//代理谁  转办谁 的任务
		String proxyTaskId=request.getParameter("proxyTaskId");
		
		//  0： 正常   2： 从其它转办过来的     3： 从其它转办过来的，办理需要自动返回
		String  whir_isTransfer=request.getParameter("whir_isTransfer")==null?"":request.getParameter("whir_isTransfer").toString();
		//代理谁  转办谁 的任务
		String whir_transferFromId=request.getParameter("whir_transferFromId");
		
		
		// //是否是 自动活动任务     0： 正常      1:是
		String  whir_isbacktrackAct=request.getParameter("whir_isbacktrackAct")==null?"":request.getParameter("whir_isbacktrackAct").toString();
		//自动返回活动 来自那个任务id
		String whir_backtrackFromTaskId=request.getParameter("whir_backtrackFromTaskId")==null?"":request.getParameter("whir_backtrackFromTaskId")+"";
		
		//退回后的活动是否需要 直接返回到退回点  1:需要
		String whir_isneedSendtoBack=request.getParameter("whir_isneedSendtoBack")==null?"":request.getParameter("whir_isneedSendtoBack")+"";
		//从哪个活动任务退回过来的
		String whir_backFromTaskId=request.getParameter("whir_backFromTaskId")==null?"":request.getParameter("whir_backFromTaskId")+"";
		*/
		
		
		
		//设置短信内容
        setMessageContent(request);
 
		
		logger.debug("开始 mainService.findNextActivityByTaskId0 ："+curUserInfoVO.getOrgVO().getOrgName());
		//流程变量
		Map varMap=dealVarMap(request); 
		
		logger.debug("开始 mainService.findNextActivityByTaskId1 ："+curUserInfoVO.getOrgVO().getOrgName());
		dealTableInfoMap(varMap);
		
		EzFlow ezFlow=new EzFlow();
		
		logger.debug("开始 mainService.findNextActivityByTaskId2 ："+curUserInfoVO.getOrgVO().getOrgName());
		//批示意见等扩展信息
		//流程附加信息  比如 提醒字段  批示意见等等
		Map extendMap=dealRemind(request);	
		
		
		logger.debug("开始 mainService.findNextActivityByTaskId2 ："+curUserInfoVO.getOrgVO().getOrgName());
		
		
		if (p_wf_recordId == null || p_wf_recordId.equals("null") || p_wf_recordId.equals("")) {
			//保存业务数据
			Map  businessMap=ezFlow.dealFlow("update", request, "");
			String ezflowBusinessKey=businessMap.get("infoId")+"";
			extendMap.put("ezflowBusinessKey", ezflowBusinessKey); 
			request.setAttribute("ezflowBusinessKey", ezflowBusinessKey);
			if(this.p_wf_superProcessInstanceId!=null){
				this.saveRelation(this.p_wf_processInstanceId, p_wf_superProcessInstanceId);			
			}
		}
		//表明是PC访问调用
		extendMap.put("fromWhere", "PC");
		
		logger.debug("开始 mainService.findNextActivityByTaskId ："+curUserInfoVO.getOrgVO().getOrgName());
	 
		Map resultMap=null;
		try{
		    resultMap=mainService.findNextActivityByTaskId(p_wf_taskId, varMap, curUserInfoVO,extendMap);
		}catch(Exception e){
			logger.debug("mainService.findNextActivityByTaskId 出现 异常");  
            String errormessage=EzFlowUtil.dealWhirException(e);
            request.setAttribute("errorInfo", errormessage);
            return "error_detail"; 
		}
		
		//给页面赋值 下一办理活动信息
		String gateType=setNextActivityInfoToJsp(resultMap,request);
		logger.debug("gateType----->"+gateType);
		if(gateType.equals(EzFlowFinals.DEALTYPE_GATEWAIT)//发送到网关节点等待
				||gateType.equals(EzFlowFinals.DEALTYPE_MULWATI)//多实例 办理完毕
				||gateType.equals(EzFlowFinals.DEALTYPE_BACKTRACK_ACT_SEND)//自动返回活动的发送办理
				||gateType.equals(EzFlowFinals.DEALTYPE_BACKTOBACK_SEND)//退回的任务 直接返送从哪退回的节点
				//||gateType.equals(EzFlowFinals.DEALTYPE_READED)//阅件办理完毕
				||gateType.equals(EzFlowFinals.DEALTYPE_MULWATI_TRAN) //转办办理完毕
				||gateType.equals(EzFlowFinals.DEALTYPE_TRAN_BACKTRACK_SEND)){//转办自动返回	
		    
			//保存业务数据
			Map  businessMap=ezFlow.dealFlow("update", request, "");	
			logger.debug("直接办理完成");
			//多实例 办理完毕 还要判断下批量办理
			if (!gateType.equals(EzFlowFinals.DEALTYPE_MULWATI) && !gateType.equals(EzFlowFinals.DEALTYPE_GATEWAIT)) {
				return "dealResult";
			}
		}
		
		//阅件办理完毕
		if(gateType.equals(EzFlowFinals.DEALTYPE_READED) || gateType.equals(EzFlowFinals.DEALTYPE_MULWATI) || 
		   gateType.equals(EzFlowFinals.DEALTYPE_GATEWAIT)){
			//批量办理
			String batchTaskIds =request.getParameter("otherTaskId")==null?"":request.getParameter("otherTaskId").toString();
			logger.debug("batchTaskIds:"+batchTaskIds);
			if(EzFlowUtil.judgeNull(batchTaskIds)){
	        	if(batchTaskIds.endsWith(",")){
	        		batchTaskIds=batchTaskIds.substring(0,batchTaskIds.length()-1); 
	        	}
	        	String  batchTaskIdArr[]=batchTaskIds.split(",");
        		//批量办理的要清空属于某个表单的数据 ，保留共用的数据
        		Map newVarMap=new HashMap();
        		/*//上一活动办理人职务级别   
        		varMap.put(EzFlowFinals.CONDI_PRETRANSACTOR_DUTYLEVEL, new Float(curUserInfoVO.getDutyLevel()).floatValue());
        		//上一活动办理人上级领导职务级别
        		varMap.put(EzFlowFinals.CONDI_PRETRANSACTOR_LEADERDUTYLEVEL, leadLeaderLevel.floatValue());
           		*/
        		
        		extendMap.put("remindTitle", "");
        		Map newextendMap=new HashMap();
        		for(String str:batchTaskIdArr){
        			logger.debug("str banl:"+str);
        			mainService.findNextActivityByTaskId(str, newVarMap, curUserInfoVO,extendMap); 			   
        		}
	        }
			//解锁 2016-04-11
			if(true){
				logger.debug("p_wf_recordId--解锁--->"+p_wf_recordId);
				logger.debug("p_wf_formKey--解锁--->"+p_wf_formKey);
				logger.debug("p_wf_processInstanceId--解锁--->"+p_wf_processInstanceId);
				logger.debug("p_wf_taskId--解锁--->"+p_wf_taskId);
				Map onlieMap=new HashMap();
				onlieMap.put("businessKey", p_wf_recordId);
				onlieMap.put("formKey", p_wf_formKey);
				onlieMap.put("processInstanceId", p_wf_processInstanceId);
				onlieMap.put("taskId", p_wf_taskId);				
				EzFlowMainService  mainSercice=new EzFlowMainService();
				mainSercice.delEzFlowOnlineUser(onlieMap);			 	
			}
			//this.printResult(this.SUCCESS);
			logger.debug("直接办理完成");
			return "dealResult";
		}
	    
		if(this.p_wf_processType.equals("0")){
			return "showRandom";
		} 
		/*
		// 当是发送操作 并且 又是退回的活动节点 需直接返回
		if((gateType!=null&&(!gateType.equals(EzFlowFinals.DEALTYPE_MULWATI)))&&whir_isneedSendtoBack.equals("1")&&EzFlowUtil.judgeNull(whir_isneedSendtoBack)){
			completeTask_deal(request, gateType);
            tag="dealResult";
		}*/		 
		logger.debug("结束 completeTask_init");
		return "showActivitys";
	}

	
	/**
	 * 办理任务
	 * @param actionMapping
	 * @param actionForm
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String completeTask() throws Exception {
		String tag="dealResult";
		/**
		 * 
		 */
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
		if(!judgeAllFields()){
			logger.debug("非法访问嫌疑");
			return this.NO_ILLEGAL;
		}
		completeTask_deal(request,null);
		//this.printResult(this.SUCCESS);
		String href="";
		if(!gdInfo.equals("")&&!gdInfo.equals("null")){ 
			String  verifyCode=request.getParameter("verifyCode");
			// 表单打开地址 
			if(p_wf_mainLinkFile!=null&&p_wf_mainLinkFile.indexOf("?")>=0){
				href = p_wf_mainLinkFile+"&ezFlowTaskId="
						+ p_wf_taskId+ "&p_wf_recordId=" +p_wf_recordId
						+ "&p_wf_openType=mailView&verifyCode="+verifyCode+""; 
			}else{
				href = p_wf_mainLinkFile+"?ezFlowTaskId="
						+ p_wf_taskId+ "&p_wf_recordId=" +p_wf_recordId
						+ "&p_wf_openType=mailView&verifyCode="+verifyCode+""; 
			}
		}
		//this.printResult(this.SUCCESS, "{href:'"+href+"', gdInfo:'"+gdInfo+"'}");
		
		String openHref ="";
		if(autoOpenDealFile){
			String userId =CommonUtils.getSessionUserId(request)==null?"":CommonUtils.getSessionUserId(request).toString();
			String next_taskId ="";
			String hql ="select aaa.ezFlowTaskId,aaa.ezFlowProcessInstanceId from com.whir.ezoffice.workflow.po.WFWorkPO aaa ";
			       hql+="where aaa.isezFlow =1 and aaa.wfCurEmployeeId =:userId and aaa.workStatus=0 and aaa.workRecordId =:recordId ";
			Dbutil  dbutil=new Dbutil();
			Map varMap=new HashMap();
			varMap.put("userId", userId);
			varMap.put("recordId", p_wf_recordId);
			List list=dbutil.getDataListByHQL(hql, varMap);
			if(list!=null&&list.size()>0){
				Object obj[]=null;
				for(int i=0;i<list.size();i++){
					obj=(Object [])list.get(0);
					next_taskId =obj[0]==null?"":obj[0].toString();
				}
			}
			logger.debug("next_taskId----->"+next_taskId);
			if(!CommonUtils.isEmpty(next_taskId)){
				com.whir.component.security.crypto.EncryptUtil  eutil=new  com.whir.component.security.crypto.EncryptUtil ();
				String  real_verifyCode=eutil.getSysEncoderKeyVlaue("ezFlowTaskId", next_taskId, "WFDealWithAction");
				// 表单打开地址 
				if(p_wf_mainLinkFile!=null&&p_wf_mainLinkFile.indexOf("?")>=0){
					openHref = p_wf_mainLinkFile+"&ezFlowTaskId="+next_taskId+"&verifyCode="+real_verifyCode
							+"&p_wf_openType=waitingDeal&otherTaskId=&p_wf_pool_processType=1&p_wf_recordId="+p_wf_recordId +"&from=workflow"; 
				}else{
					openHref = p_wf_mainLinkFile+"?ezFlowTaskId="+next_taskId+"&verifyCode="+real_verifyCode
							+"&p_wf_openType=waitingDeal&otherTaskId=&p_wf_pool_processType=1&p_wf_recordId="+p_wf_recordId+ "&from=workflow"; 
				}
			}
		}
		logger.debug("openHref----->"+openHref);
		
		this.printResult(this.SUCCESS, "{href:'"+href+"', gdInfo:'"+gdInfo+"', openHref:'"+openHref+"'}");
		
		return  null;
	}
	
	/**
	 * 
	 * @param request
	 * @param type   不是从页面取的 gate_dealType
	 */
	private void  completeTask_deal(HttpServletRequest request,String type){
		//办理结果
		String dealResult="1";	
		//流程主服务
		EzFlowMainService mainService=new EzFlowMainService();
 	
		//选择的办理活动
		List choosedNextActivitys=null;
		//网关类型，and  动作类型
		String gate_dealType=""; 
		if(type!=null){
			gate_dealType=type;
		}else{
			gate_dealType=request.getParameter("gate_dealType"); 
		}
		
		//当是 到达 XAND 网关等待， 或者  多实例不是最后一步   或者是阅件办理完毕
		if (gate_dealType != null
				&& (gate_dealType.equals(EzFlowFinals.DEALTYPE_GATEWAIT)||  //发送到网关节点等待
					gate_dealType.equals(EzFlowFinals.DEALTYPE_MULWATI) ||  // 多实例 办理完毕
					gate_dealType.equals(EzFlowFinals.DEALTYPE_READED)||    //阅件办理完毕
					gate_dealType.equals(EzFlowFinals.DEALTYPE_MULWATI_TRAN)||   //转办的 办理完毕( 转给多人办理    不是最后一个办理的话  都是办理完毕)
					gate_dealType.equals(EzFlowFinals.DEALTYPE_BACKTRACK_ACT_SEND))) { //自动返回活动的发送办理

		}else{
			//需要 找下一办理活动
			choosedNextActivitys=dealChoosedNextActivitys(request);
			//没有选择下一活动  或者选择的下一活动里有 某个没有选择办理人
			if(choosedNextActivitys==null||choosedNextActivitys.size()<=0){
				dealResult="-1";
			}	 
		}
		
		logger.debug("dealResult 一:"+dealResult);
		
		//出现错误不保存
		if(dealResult.equals("-1")){		
		}else{
			//流程变量
			Map varMap=dealVarMap(request);			
			//流程附加信息  比如 提醒字段
			Map extendMap=dealRemind(request);
			
			//tips  短信提醒  邮件提醒 等
			dealTipsExtendMap(extendMap,request);
			
			//管理员自由跳转
			String isFreeJump=request.getParameter("isFreeJump")==null?"0":request.getParameter("isFreeJump").toString();
			if(isFreeJump.equals("1")){
				extendMap.put("ext_isFreeJump", "1");
				extendMap.put("ext_processInstatnceId", this.p_wf_processInstanceId);  
			}
 		
			//此次发送经过几个网关
		    int gateNum=0;
		    String gateNumStr=request.getParameter("gateNum");
		        
            // //自动返回活动的发送办理
			if(gate_dealType.equals(EzFlowFinals.DEALTYPE_BACKTRACK_ACT_SEND)){
				gateNum=1;	
			}
			
			logger.debug("gateNumStr 一:"+gateNumStr);
		    if(EzFlowUtil.judgeNull(gateNumStr)){
		    	EzFlow ezFlow=new EzFlow();
				//保存业务数据
				Map  businessMap=ezFlow.dealFlow("update", request, "");	
				
		    	gateNum=Integer.parseInt(gateNumStr);
		    	
		    	//批量办理的任务ids
		    	String  batchTaskIds=null;//dealBatchComplete(request); 
		    	if((gdInfo==null || gdInfo.equals("") || gdInfo.equals("null")) && 
		    			(isEndActivity==null || isEndActivity.equals("") || isEndActivity.equals("null"))){ 
		    		batchTaskIds=dealBatchComplete(request);
		    	}
		    	//必须放在下面
		    	//保存流程
		    	mainService.completeTaskById(p_wf_taskId,p_wf_processInstanceId, varMap, choosedNextActivitys, gateNum, gate_dealType, curUserInfoVO, extendMap);				   
		       
		    	if(EzFlowUtil.judgeNull(batchTaskIds)){
		        	if(batchTaskIds.endsWith(",")){
		        		batchTaskIds=batchTaskIds.substring(0,batchTaskIds.length()-1); 
		        	}
		        	if(EzFlowUtil.judgeNull(batchTaskIds)){
		        		dealbatchSend( batchTaskIds, gateNum, gate_dealType, varMap, extendMap , choosedNextActivitys);
	        		} 
		        }
		    	//
		    	if(canNotBatchDealWithTaskIds!=null&&canNotBatchDealWithTaskIds.size()>0){
	        		logger.debug("canNotBatchDealWithTaskIds 有值  需要 自动批量办理 ");
	        		dealCanAutoBatchTask(canNotBatchDealWithTaskIds, varMap, extendMap);
	        	}
		    	
				// 解锁
				if(true){
					Map onlieMap=new HashMap();
					onlieMap.put("businessKey", p_wf_recordId);
					onlieMap.put("formKey", p_wf_formKey);
					onlieMap.put("processInstanceId", p_wf_processInstanceId);
					onlieMap.put("taskId", p_wf_taskId);				
					EzFlowMainService  mainSercice=new EzFlowMainService();
					mainSercice.delEzFlowOnlineUser(onlieMap);			 	
				}    
		    }else{
		    	//
		    	dealResult="-2";
		    }		
		}	
		
		logger.debug("dealResult:"+dealResult);
		request.setAttribute("dealFlowResult", dealResult);
		
	}
	
	/***
	 * 处理批量办理， 
	 * 主要是批量办理 选择的活动是 流程启动人等方式， 没个流程应该取不一样的人
	 */
	private  void  dealbatchSend(String batchTaskIds,int gateNum,String gate_dealType,Map varMap,Map extendMap ,List choosedNextActivitys){
		EzFlowMainService mainService=new EzFlowMainService();
		if(batchTaskIds.endsWith(",")){
    		batchTaskIds=batchTaskIds.substring(0,batchTaskIds.length()-1); 
    	}
		
	    //打开页面时的 taskId  
        String oldTaskId=extendMap.get("oldTaskId")==null?"":extendMap.get("oldTaskId").toString();
        //批量办理其他任务id不为空， 并且不是空港自动跳转   ， 空港自动跳转暂时不支持
    	if(EzFlowUtil.judgeNull(batchTaskIds)&&(oldTaskId.equals("")||oldTaskId.equals("")||oldTaskId.equals(this.p_wf_taskId))){
        	String  batchTaskIdArr[]=batchTaskIds.split(",");
    		//批量办理的要清空属于某个表单的数据 ，保留共用的数据 
    		extendMap.put("remindTitle", "");
    		Map newextendMap=new HashMap();
    		//第一次为空   当经过judgeChoosedNextActivitys  后  会边为：noNeedJudge 不在需要判断，   needJudge ：需要每个判断
    		String needJudgeType="";
    		for(String str:batchTaskIdArr){
    			judgeChoosedNextActivitys(str,needJudgeType,choosedNextActivitys,varMap,extendMap);
    			extendMap.put("oldTaskId", str);
    			logger.debug(" needJudgeType:"+needJudgeType);
		    	mainService.completeTaskById(str, varMap, choosedNextActivitys, gateNum, gate_dealType, curUserInfoVO, extendMap);				   
    		}
		} 	
	}
	
	
	/**
	 * 
	 * @param list
	 * @param varMap
	 * @param extendMap
	 */
	private void dealCanAutoBatchTask(List<String> list,Map varMap,Map extendMap){  
		
		logger.debug("canNotBatchDealWithTaskIds 开始  ");
		ChoosedActivityVO cVO=null; 
		TransactorInfoVO dTVO=null;
		for(String taskId:list){
			EzFlowMainService mainService=new EzFlowMainService();
			Map resultMap=null;
			extendMap.put("remindTitle", "");
			try{
				//
			    resultMap=mainService.findNextActivityByTaskId(taskId, varMap, curUserInfoVO,extendMap);
			}catch(Exception e){
				logger.debug("自动批量办理  出错， 可能 不是条件不足  ");
				e.printStackTrace();
				continue;
			}   
			
			List nextActivitys=new ArrayList();
			nextActivitys=(List)resultMap.get("nextActivitys");
			//默认活动id
			String defaultActivity=""+resultMap.get("defaultActivity");
			//此次发送的网关类型    XOR XAND   XX
			String gateType=""+resultMap.get("gateType");
			//此次发送经过几个网关
			String gateNum=""+resultMap.get("gateNum");	
			 
			int gateNum_int=0;
			gateNum_int=Integer.parseInt(gateNum);
			
			
			logger.debug("canNotBatchDealWithTaskIds  gateType： "+gateType);
			logger.debug("canNotBatchDealWithTaskIds  gateNum_int： "+gateNum_int);
			
			
			if(gateType.equals(EzFlowFinals.DEALTYPE_GATEWAIT)//发送到网关节点等待
					||gateType.equals(EzFlowFinals.DEALTYPE_MULWATI)//多实例 办理完毕
					||gateType.equals(EzFlowFinals.DEALTYPE_BACKTRACK_ACT_SEND)//自动返回活动的发送办理
					||gateType.equals(EzFlowFinals.DEALTYPE_BACKTOBACK_SEND)//退回的任务 直接返送从哪退回的节点
					//||gateType.equals(EzFlowFinals.DEALTYPE_READED)//阅件办理完毕
					||gateType.equals(EzFlowFinals.DEALTYPE_MULWATI_TRAN) //转办办理完毕
					||gateType.equals(EzFlowFinals.DEALTYPE_TRAN_BACKTRACK_SEND)){//转办自动返回	 
			}else{ 
				//
				if(nextActivitys!=null&&nextActivitys.size()==1){ 
					cVO=(ChoosedActivityVO)nextActivitys.get(0);
					if(cVO!=null){
						dTVO=cVO.getDealTransactorInfoVO(); 
						if (dTVO != null
								&& (dTVO.getRealtype().equals(EzFlowFinals.TRANSACTORTYPE_INITIATOR) 
									|| dTVO.getRealtype().equals(EzFlowFinals.TRANSACTORTYPE_SET_ALLTRANSACTORS)
									|| dTVO.getRealtype().equals(EzFlowFinals.TRANSACTORTYPE_SET_ALLTRANSACTORS))) {
							logger.debug("canNotBatchDealWithTaskIds dTVO.getRealtype()： "+dTVO.getRealtype());
							
							//cVO.setDealTransactorInfoVO(dTVO);
							List choosedNextActivitys = new ArrayList();
							ChoosedActivityVO cvo=new ChoosedActivityVO();
							choosedNextActivitys.add(cVO); 
							try{
							   mainService.completeTaskById(taskId, varMap, choosedNextActivitys, gateNum_int, gateType, curUserInfoVO, extendMap);
							}catch(Exception e){ 
								e.printStackTrace();
								continue;
							}				   
						}
					}
				}
			} 
		}
		
		logger.debug("canNotBatchDealWithTaskIds结束 ");
		
	}
	
	
	/**
	 * 
	 * @param taskId
	 * @param type  noNeedJudge 不在需要判断，   needJudge ：需要每个判断  第一次为空
	 * @param choosedNextActivitys  
	 * @param varMap
	 * @param extendMap
	 */
	private void  judgeChoosedNextActivitys(String taskId,String type,List choosedNextActivitys,Map varMap,Map extendMap){
		
		logger.debug("judgeChoosedNextActivitys  taskId:"+taskId);
		logger.debug(" type:"+type);
		//表明进行过判断  可直接返回choosedNextActivitys
		if(type.equals("noNeedJudge")){
			//return choosedNextActivitys; 
			//保持choosedNextActivitys 不变。
			return ;
		}else {
			EzFlowMainService mainService=new EzFlowMainService();
			Map resultMap=mainService.findNextActivityByTaskId(taskId, varMap, curUserInfoVO,extendMap); 
			List nextActivitys=(List)resultMap.get("nextActivitys");
			ChoosedActivityVO cVO=null; 
			
			ChoosedActivityVO dVO=null;
			TransactorInfoVO  dTVO=null;
			for(int i=0;i<choosedNextActivitys.size();i++){
				cVO=(ChoosedActivityVO)choosedNextActivitys.get(i);
				for(int j=0;j<nextActivitys.size();j++){
					dVO=(ChoosedActivityVO)nextActivitys.get(j);
					logger.debug("cVO.getActivityId():"+cVO.getActivityId());
					logger.debug("dVO.getActivityId():"+dVO.getActivityId());
					if(cVO.getActivityId().equals(dVO.getActivityId())){
						dTVO=dVO.getDealTransactorInfoVO();
						//流程启动人
						if(dTVO!=null && 
								(dTVO.getRealtype().equals(EzFlowFinals.TRANSACTORTYPE_INITIATOR) ||
								 dTVO.getRealtype().equals(EzFlowFinals.TRANSACTORTYPE_ACTIVITY_TRANSACTOR))){
							type="needJudge";
							cVO.setDealTransactorInfoVO(dTVO);
							logger.debug(" needJudge:");
							continue;
						}
					}
				} 
			}
			
			//当type还是为空 就表明 没有活动选择的是流程启动人  类型
			if(type.equals("")){
				type="noNeedJudge";
			}
			return ;
		}
	}
	
	
	
	/**
	 * 找能一起批量办理的任务
	 * @param request
	 * @return
	 */
	private  String dealBatchComplete(HttpServletRequest request){
		logger.debug("开始判断 能批量办理的任务");
		logger.debug("p_wf_taskId:"+p_wf_taskId);		
		String resultTaskIds="";
		String  otherTaskId =request.getParameter("otherTaskId")==null?"":request.getParameter("otherTaskId").toString();
		logger.debug("otherTaskId:"+otherTaskId);
		if(EzFlowUtil.judgeNull(otherTaskId)){
			EzFlowMainService mainService=new EzFlowMainService();
			resultTaskIds=mainService.getBatchUpateIds(p_wf_taskId, otherTaskId);
			String []otherTaskIdArr=otherTaskId.split(",");
			
			for(String str:otherTaskIdArr){
				if(EzFlowUtil.judgeNull(str)){
					canNotBatchDealWithTaskIds.add(str);
				}
			}
			
			logger.debug("canNotBatchDealWithTaskIds size:"+canNotBatchDealWithTaskIds.size());
			if(EzFlowUtil.judgeNull(resultTaskIds)){
				String []resultTaskIdsArr=resultTaskIds.split(",");
				String _otherTaskId=","+otherTaskId+",";
				for(String str:resultTaskIdsArr){
					 //在可批量办理内的  ，就不能在 不批量办理内
					 //if(_otherTaskId.indexOf(","+str+",")>=0){
						 canNotBatchDealWithTaskIds.remove(str);
					// }
				}
			}else{  
			}
			
		}
		
		logger.debug("canNotBatchDealWithTaskIds size2:"+canNotBatchDealWithTaskIds.size());
		logger.debug("resultTaskIds:"+resultTaskIds);
		
		return resultTaskIds;	
	}
	

	
 
	/**
	 * 
	 * 处理业务信息
	 * @param request
	 * @return
	 */
	private String saveBusiness(HttpServletRequest request){
		//业务主键id
		String businessKey=(new java.util.Date().getTime())+"";	
		com.whir.ezoffice.formhandler.EzFormFlow  ezFormFlow=new com.whir.ezoffice.formhandler.EzFormFlow();
		Map  resultMap=ezFormFlow.save(request,1);
		businessKey=""+resultMap.get("infoId");
		return businessKey;
	}
 
	/**
	 * 
	 * @param request
	 * @return
	 */
	private String updateBusiness(HttpServletRequest request){
		//业务主键id
		String businessKey=(new java.util.Date().getTime())+"";	
		com.whir.ezoffice.formhandler.EzFormFlow  ezFormFlow=new com.whir.ezoffice.formhandler.EzFormFlow();
		Map  resultMap=ezFormFlow.update(request,1);
		businessKey=""+resultMap.get("infoId");
		return businessKey;
	}
 
	
	
	
	/**
	 * 处理下一办理活动信息
	 * @param request
	 * @return
	 */
	private List dealChoosedNextActivitys(HttpServletRequest request){
		logger.debug("开始处理下一办理活动信息");
		//选择的活动信息
		List choosedActivityVOList=null;		
		//活动id
		String choosedActivityIds=request.getParameter("p_wf_choosedActivityId");
		logger.debug("p_wf_choosedActivityId:"+choosedActivityIds);
		if(!EzFlowUtil.judgeNull(choosedActivityIds)){
			return null;
		}
		String [] choosedActivityIdArr=choosedActivityIds.split(",");
		//活动名
		String choosedActivityName="";
		//活动类型   主要是判断是否是结束活动
		String choosedActivityType="";
 
	
		//是否包含结束活动
        boolean haveEndActivity=false;
        
		if(choosedActivityIdArr!=null&&choosedActivityIdArr.length>0){
			choosedActivityVOList=new ArrayList();
			//选择的活动
			ChoosedActivityVO activityVo=null;
			//活动的办理人信息
			TransactorInfoVO  dealTransactorInfoVO=null;
			//活动 阅件人
			TransactorInfoVO  readTransactorInfoVO=null;
			for(int i=0;i<choosedActivityIdArr.length;i++){
				choosedActivityName=request.getParameter(choosedActivityIdArr[i]+"_choosedActivityName");
				choosedActivityType=request.getParameter(choosedActivityIdArr[i]+"_choosedActivityType");
				
				logger.debug("choosedActivityName:"+choosedActivityName);
				logger.debug("choosedActivityType:"+choosedActivityType);
				/*---------------活动信息------------------------------*/
				activityVo=new ChoosedActivityVO();
				activityVo.setActivityId(choosedActivityIdArr[i]);
				activityVo.setActivityName(choosedActivityName);
				activityVo.setActivityType(choosedActivityType);
				logger.debug("_choosedProcessInstanceId:"+request.getParameter(choosedActivityIdArr[i]+"_choosedProcessInstanceId"));
 
				activityVo.setProcessInstanceId(request.getParameter(choosedActivityIdArr[i]+"_choosedProcessInstanceId"));
				//判断是结束活动
				if(choosedActivityType!=null&&choosedActivityType.equals("end")){
					haveEndActivity=true;
					this.isEndActivity ="true";
				}
				
				/*-------------活动办理人 阅件办理人信息-----------------*/
				
				dealTransactorInfoVO=dealTransactorInfo(choosedActivityIdArr[i]+"_deal",request);
				readTransactorInfoVO=dealTransactorInfo(choosedActivityIdArr[i]+"_read",request);
				//如果办理人为空
				if (dealTransactorInfoVO == null
						|| dealTransactorInfoVO.getIdentityVOList() == null
						|| dealTransactorInfoVO.getIdentityVOList().size() < 0) {
					//当办理人为空  ，并且是结束活动
					if(choosedActivityType!=null&&choosedActivityType.equals("end")){	
						choosedActivityVOList.add(activityVo);
					}else{
	                   choosedActivityVOList=null;
					   break;
					}
				}else{
					String userAccounts ="$"+CommonUtils.getSessionUserAccount(request)+"$";
					//dealTransactorInfoVO.getScopeIds()----->$wanggl$
					logger.debug("userAccounts----->"+userAccounts);
					logger.debug("dealTransactorInfoVO.getScopeIds()----->"+dealTransactorInfoVO.getScopeIds());
					if(userAccounts.equals(dealTransactorInfoVO.getScopeIds())){
						autoOpenDealFile =true;
					}
					logger.debug("autoOpenDealFile----->"+autoOpenDealFile);
					activityVo.setDealTransactorInfoVO(dealTransactorInfoVO);
					activityVo.setReadTransactorInfoVO(readTransactorInfoVO);
					choosedActivityVOList.add(activityVo);
				}
			}
		}	
		
		
		//判断是否有结束活动， 判断 归档
		/***/ 
		// true 表示归档
	    String whir_processNeedDossier=p_wf_processNeedDossier==null?"":p_wf_processNeedDossier; 
	    
	    logger.debug("whir_processNeedDossierwhir_processNeedDossier:"+whir_processNeedDossier);
	    logger.debug("p_wf_moduleIdp_wf_moduleIdp_wf_moduleId:"+p_wf_moduleId);
	    if(haveEndActivity&&whir_processNeedDossier.equals("true")){ 
	    	 if("1".equals(p_wf_moduleId)||"2".equals(p_wf_moduleId) || "3".equals(p_wf_moduleId) || "34".equals(p_wf_moduleId)){ 
		    	 String returnValue = new com.whir.ezoffice.archives.bd.ArchivesBD().archivesPigeonholeSet("GZLC",request.getSession(true).getAttribute("domainId")==null?"-1":request.getSession(true).getAttribute("domainId").toString());
		            //if(",23,24,25,".indexOf(tableId)>=0){
	             if("2".equals(p_wf_moduleId) || "3".equals(p_wf_moduleId) || "34".equals(p_wf_moduleId)){
	                 returnValue = new com.whir.ezoffice.archives.bd.ArchivesBD().archivesPigeonholeSet("GWGL",request.getSession(true).getAttribute("domainId")==null?"-1":request.getSession(true).getAttribute("domainId").toString());
	                //System.out.print("\n----GWGL-returnValue----\n"+returnValue+"\n-------\n");
	             } 
	             
	             logger.debug("returnValuereturnValuereturnValuereturnValue:"+returnValue);
	             if(!"".equals(returnValue) && !"-1".equals(returnValue)){
		    	      this.gdInfo="true";
		    	 }
	    	 }
	    }
	    
	    logger.debug("gdInfogdInfogdInfogdInfo:"+gdInfo);
	    /*   考虑子过程情况  下面代码作废，  判断在页面上处理
		//当多个活动并行  并且有一个是结束活动时 不 允许 办理 
		if(haveEndActivity&&choosedActivityVOList!=null&&choosedActivityVOList.size()>1){
			choosedActivityVOList=null;
			//标记失败 状态
			request.setAttribute("MulAndEnd", "1");
		}*/
		logger.debug("结束处理下一办理活动信息");
		return choosedActivityVOList;
	}
	
	/**
	 *处理活动的办理热办理人
	 * @param activityId_type   活动id_type   type:  办件：deal    阅件： read
	 * @param request
	 * @return
	 */
	private  TransactorInfoVO  dealTransactorInfo(String activityId_type,HttpServletRequest request){
		logger.debug("开始处理活动的办理人信息");
		logger.debug("activityId_type:"+activityId_type);
		TransactorInfoVO  dealTransactorInfoVO=null;
		//办理人帐号
		String  userAccounts=request.getParameter(activityId_type+"_userAccount");
		logger.debug("userAccounts:"+userAccounts);
 
		//办理人帐号不为空时 
		if(EzFlowUtil.judgeNull(userAccounts)){		
			String [] userAccountArr=EzFlowUtil.dealStrForIn(userAccounts, '$', null).split(",");
			//办理人名
			String  userNames=""+request.getParameter(activityId_type+"_userName");
			
			logger.debug("userNames:"+userNames);
			String [] userNameArr=userNames.split(",");
			//办理人id 暂不需要
			String  userIds=""+request.getParameter(activityId_type+"_userId");
			
			logger.debug("userIds:"+userIds);
			
			String [] UserIdArr=userIds.split(",");
			//办理人信息
			dealTransactorInfoVO=new TransactorInfoVO(EzFlowFinals.TRANSACTOY_INFO_USERS);
			if(userAccountArr!=null&&userAccountArr.length>0){
				 for(int userIndex=0;userIndex<userAccountArr.length;userIndex++){
					 if(EzFlowUtil.judgeNull(userAccountArr[userIndex])){
						 //加多个办理人 ，            userId,  userName, userAccount
						 logger.debug("userAccountArr[userIndex]:"+userAccountArr[userIndex]);
						 //dealTransactorInfoVO.addIdentityVO(new UserInfoVO("",userNameArr[userIndex],userAccountArr[userIndex]) );						 
						 dealTransactorInfoVO.addIdentityVO(new UserInfoVO("","",userAccountArr[userIndex]) );
					 }
				 }
			}
		}
		logger.debug("结束处理办理人信息");
		return dealTransactorInfoVO;
	}
 
	
	
	/**
	 * 给页面上传 下一办理活动的信息
	 */
	private String   setNextActivityInfoToJsp(Map resultMap,HttpServletRequest request){
		//List<ChoosedActivityVO>nextActivitys
		List nextActivitys=new ArrayList();
		nextActivitys=(List)resultMap.get("nextActivitys");
		//默认活动id
		String defaultActivity=""+resultMap.get("defaultActivity");
		//此次发送的网关类型    XOR XAND   XX
		String gateType=""+resultMap.get("gateType");
		//此次发送经过几个网关
		String gateNum=""+resultMap.get("gateNum");	
		
		
		//默认活动id  一般情况下跟当前的taskId 是一致 的 ，但如果是空岗问题 的话， 会变为想新的taskId
		String afterInsertTaskIds=resultMap.get("afterInsertTaskIds")==null?"":resultMap.get("afterInsertTaskIds").toString();
		request.setAttribute("nextActivitys", nextActivitys);
		request.setAttribute("defaultActivity", defaultActivity);
		request.setAttribute("gateType", gateType);
		request.setAttribute("gateNum", gateNum);
		request.setAttribute("afterInsertTaskIds", afterInsertTaskIds);
        return gateType;
	}
  
	
	
	/**
	 * 处理 tips  短信提醒控制等
	 * @param map
	 */
	private  void  dealTipsExtendMap(Map map,HttpServletRequest request){
		//是否需要短信提醒
		map.put("sendNeedNoteType", request.getParameter("sendNeedNoteType"));
	    // 是否需要邮件提醒
		map.put("sendNeedMailType", request.getParameter("sendNeedMailType"));		
		// 办理提示
	    map.put("whir_dealTips", request.getParameter("whir_dealTips"));    
	    // 短信内容提示
	    map.put("whir_noteContent", request.getParameter("whir_noteContent"));
	    
	    //流程提醒模块
	    map.put("msgFrom", this.p_wf_msgFrom);
	    
	    String whir_priority=request.getParameter("whir_priority")==null?"10":request.getParameter("whir_priority");
	    logger.debug("whir_priority："+whir_priority);
	    // 
	    map.put("whir_priority", whir_priority); 
	    dealWhir_priority(map,request);
	    
	} 
	
	
	/**
	 * 缓急发送每一个环节分开赋值
	 * @param map
	 * @param request
	 */
	private  void  dealWhir_priority(Map map,HttpServletRequest request){
		String choosedActivityIds=request.getParameter("p_wf_choosedActivityId");
		logger.debug("p_wf_choosedActivityId:"+choosedActivityIds);
		if(!EzFlowUtil.judgeNull(choosedActivityIds)){
			return ;
		}
		String [] choosedActivityIdArr=choosedActivityIds.split(","); 
		if(choosedActivityIdArr!=null&&choosedActivityIdArr.length>0){
			for(String id:choosedActivityIdArr){
				 String whir_priority=request.getParameter("whir_priority"+id)==null?"10":request.getParameter("whir_priority"+id);
				 map.put("whir_priority"+id, whir_priority);  			
			}
		}
	}
	
	
	/**
	 * 处理 相关流程数据
	 * @param map
	 * @param request
	 */
	private  void  dealRealtionMap(Map map,HttpServletRequest request){
		
		//发起流程  需要关联的流程实例id
		String relationRProcessInstanceIdArr[]=request.getParameterValues("relationRProcessInstanceId");
		String relationRProcessInstanceIds="";
		
		if(relationRProcessInstanceIdArr!=null&&relationRProcessInstanceIdArr.length>0){
			for(int i=0;i<relationRProcessInstanceIdArr.length;i++){
				relationRProcessInstanceIds+=relationRProcessInstanceIdArr[i]+",";
			}
			relationRProcessInstanceIds=relationRProcessInstanceIds.substring(0,relationRProcessInstanceIds.length()-1);
		}
		
		if(EzFlowUtil.judgeNull(relationRProcessInstanceIds)){
			//处理sql注入
			relationRProcessInstanceIds =com.whir.component.security.crypto.EncryptUtil.sqlcode(relationRProcessInstanceIds);
			logger.debug("relationRProcessInstanceIds----->"+relationRProcessInstanceIds);
			map.put("relationRProcessInstanceIds", relationRProcessInstanceIds);
		}
		
		logger.debug("relationRProcessInstanceIds:"+relationRProcessInstanceIds);
		
		//别的流程  关联当前新发起流程 
		String relationProcessInstanceId=request.getParameter("relationProcessInstanceId")==null?"":request.getParameter("relationProcessInstanceId").toString();
		if(EzFlowUtil.judgeNull(relationProcessInstanceId)){
			//处理sql注入
			relationProcessInstanceId =com.whir.component.security.crypto.EncryptUtil.sqlcode(relationProcessInstanceId);
			logger.debug("relationProcessInstanceId----->"+relationProcessInstanceId);
			map.put("relationProcessInstanceId", relationProcessInstanceId);
		}
		
		logger.debug("relationProcessInstanceId:"+relationProcessInstanceId);
	}
	
	
	/***
	 * 中间环节 新增关联流程
	 * @return
	 */
	public String addRelation(){
		String processInstanceId = request.getParameter("processInstanceId") == null ? "": request.getParameter("processInstanceId").toString();
		String rProcessInstanceIds = request.getParameter("rProcessInstanceIds") == null ? "" : request.getParameter("rProcessInstanceIds").toString();	
		saveRelation(processInstanceId,rProcessInstanceIds);
		this.printResult(this.SUCCESS);
		return null;		
	}
	
	/**
	 *  保存相关流程，  给addRelation 调用，或者子流程关联父流程用
	 */
	private String saveRelation(String  processInstanceId ,String rProcessInstanceIds) {
	   // 新增关联
		com.whir.service.api.ezflowservice.EzFlowRelationProcessService mainSercice = new com.whir.service.api.ezflowservice.EzFlowRelationProcessService();
		return mainSercice.insertRelation(processInstanceId, rProcessInstanceIds);
	}
	
	public String deleteRelation() {
		logger.debug("开始deleteRelation");
		String pr_pId = request.getParameter("pr_pId") == null ? "" : request.getParameter("pr_pId").toString();
		String pr_r_pId = request.getParameter("pr_r_pId") == null ? "": request.getParameter("pr_r_pId").toString();
		logger.debug("pr_pId:"+pr_pId);
		logger.debug("pr_r_pId:"+pr_r_pId);
		com.whir.service.api.ezflowservice.EzFlowRelationProcessService mainSercice = new com.whir.service.api.ezflowservice.EzFlowRelationProcessService();
		mainSercice.deleteRelation(pr_pId, pr_r_pId);
		this.printResult(this.SUCCESS);
		logger.debug("结束deleteRelation");
		return null;
	}
	
	public  String showJumpActs(){
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
		com.whir.service.api.ezflowservice.EzFlowMainService  mainService=new com.whir.service.api.ezflowservice.EzFlowMainService();
 
        Map varMap=dealVarMap(request);  
		dealTableInfoMap(varMap); 
		EzFlow ezFlow=new EzFlow(); 
	 
		//批示意见等扩展信息
		//流程附加信息  比如 提醒字段  批示意见等等
		Map extendMap=dealRemind(request);	 
		List<ChoosedActivityVO> list=mainService.findCanFreeJumpActs(this.p_wf_processInstanceId, this.curUserInfoVO, extendMap, varMap);
		request.setAttribute("nextActivitys", list); 
		return "showJumpActs";
	}
	
	public  String showJumpActUser(){
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
		com.whir.service.api.ezflowservice.EzFlowMainService  mainService=new com.whir.service.api.ezflowservice.EzFlowMainService();
        Map varMap=dealVarMap(request);  
		dealTableInfoMap(varMap); 
		EzFlow ezFlow=new EzFlow(); 
		Map extendMap=dealRemind(request);	 
		String  choosedActId=request.getParameter("choosedActId");
		Map <String,Object> map=mainService.getActivityUsers(this.p_wf_processInstanceId, choosedActId, curUserInfoVO, extendMap, varMap);
		ChoosedActivityVO vo=(ChoosedActivityVO)map.get(choosedActId);
		request.setAttribute("choosedActivityVO", vo); 
		return "showJumpActUser";
	}
 
}
