package com.whir.ezflow.actionsupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.component.security.crypto.EncryptUtil;
import com.whir.ezflow.util.EzFlowFinals;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezoffice.bpm.po.BPMProcessPO;
import com.whir.service.api.ezflowservice.EzFlowDealQueryService;
import com.whir.service.api.ezflowservice.EzFlowLogService;
import com.whir.service.api.ezflowservice.EzFlowMainService;
import com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService;

public class EzFlowBaseOpenAction extends EzFlowBaseAttrAction {
	
	private static Logger logger = Logger.getLogger(EzFlowBaseOpenAction.class.getName());
	//模块类code
	public  static final  String  BASE_MODULE_CODE="WFDealWithAction";
	
	 /**
     * 发起流程时往页面赋值执行的方法
     */
	public void setStartInfo(Map map) {
		logger.debug("开始设置 流程发起时的信息");
		//
	    if(p_wf_openType==null||p_wf_openType.equals("")||p_wf_openType.equals("null")){
		    p_wf_openType="startOpen";
	    }
	    
	    if(!this.judgeInteger("rrecordId;rmoduleId;p_wf_recordId")){ 
	    	p_wf_pool_processId="";
	    	p_wf_tableId="";
	    	p_wf_moduleId="";
	    	p_wf_pool_processType="";
	    	p_wf_recordId="";
	    	p_wf_taskId="";
	    	p_wf_processInstanceId="";  
	    	return ;
	    }

		EzFlowProcessDefinitionService processService=new EzFlowProcessDefinitionService();
 
		/**
		 * reStart:重新发起   startAgain:再次发起  
		 * */	
	    Map processDefMap=new HashMap();
		if((p_wf_openType.equals("reStart")||p_wf_openType.equals("startAgain"))&&p_wf_processInstanceId!=null&&!p_wf_processInstanceId.equals("")&&!p_wf_processInstanceId.equals("null")){
			processDefMap=processService.findProcessInfoByProcessInstanceId(p_wf_processInstanceId);	
			this.p_wf_processId=""+processDefMap.get("processDefinitionId");
		}else{
			if ((p_wf_processId == null || p_wf_processId.equals("") || p_wf_processId.equals("null"))
					&& p_wf_processDefinitionKey != null
					&& !p_wf_processDefinitionKey.equals("")
					&& !p_wf_processDefinitionKey.equals("null")) { 
				//自定义模块 原理保存数据错了，  这里纠正
				if(p_wf_processDefinitionKey.indexOf(":")>0){
					p_wf_processDefinitionKey=p_wf_processDefinitionKey.substring(0,p_wf_processDefinitionKey.lastIndexOf(":"));
					p_wf_processDefinitionKey=p_wf_processDefinitionKey.substring(0,p_wf_processDefinitionKey.lastIndexOf(":"));
				}	
				processDefMap=processService.findProcessInfoByKey(p_wf_processDefinitionKey);
				//流程定义id
				this.p_wf_processId=""+processDefMap.get("processDefinitionId");
			}else{
				processDefMap=processService.findProcessInfoById(p_wf_processId);	
			}			
		}
 	
		//流程定义名
		p_wf_processName=processDefMap.get("processDefinitionName")==null?"":processDefMap.get("processDefinitionName").toString();
		
		//子表：允许新增行
	    p_wf_subTableDataControlAdd=processDefMap.get("whir_subTableDataControlAdd")==null?"":processDefMap.get("whir_subTableDataControlAdd").toString();
	    logger.debug("p_wf_subTableDataControlAdd----->"+p_wf_subTableDataControlAdd);
	    
	    //子表：新增默认空行
	    p_wf_subTableDataControlAddNull=processDefMap.get("whir_subTableDataControlAddNull")==null?"":processDefMap.get("whir_subTableDataControlAddNull").toString();
	    logger.debug("p_wf_subTableDataControlAddNull----->"+p_wf_subTableDataControlAddNull);
	    
	    //子表：允许修改已有行数据
	    p_wf_subTableDataControlModi=processDefMap.get("whir_subTableDataControlModi")==null?"":processDefMap.get("whir_subTableDataControlModi").toString();
	    logger.debug("p_wf_subTableDataControlModi----->"+p_wf_subTableDataControlModi);
	    
	    //子表：允许删除已有行数据
	    p_wf_subTableDataControlDel=processDefMap.get("whir_subTableDataControlDel")==null?"":processDefMap.get("whir_subTableDataControlDel").toString();
	    logger.debug("p_wf_subTableDataControlDel----->"+p_wf_subTableDataControlDel);
	    
		//不可写字段  ,fieldid,,fieldid,……
	    //String whir_nodeHiddenField=processDefMap.get("whir_nodeHiddenField")==null?"":processDefMap.get("whir_nodeHiddenField").toString();
	   
	    p_wf_concealField=""+processDefMap.get("whir_nodeHiddenField");
	    
	    //写字段 ,fieldid,,fieldid,……
	    p_wf_cur_ModifyField=processDefMap.get("whir_nodeWriteField")==null?"":processDefMap.get("whir_nodeWriteField").toString();
	    
	    // 表单code
	    if(!CommonUtils.isEmpty(processDefMap.get("whir_formKey"))){
	    	p_wf_formKey=com.whir.component.security.crypto.EncryptUtil.sqlcode(""+processDefMap.get("whir_formKey"));
	    }
		//表单提醒字段  ,fieldid,,fieldid,……
	    p_wf_remindField=""+processDefMap.get("whir_processRemindField");	 
	    
		//流程变量 ,fieldid,,fieldid,……
	    p_wf_processallVariables=""+processDefMap.get("whir_processallVariables");
	    //办理按钮
		List whir_taskButtons=(List)processDefMap.get("whir_taskButtons");
		//处理类名	
		this.p_wf_classname=""+processDefMap.get("whir_classname");
		this.p_wf_saveData=""+processDefMap.get("whir_saveData");
		this.p_wf_completeData=""+processDefMap.get("whir_completeData");
		
		this.p_wf_forminitJsFunName=""+processDefMap.get("whir_pro_forminitJsFunName");
		this.p_wf_formsaveJsFunName=""+processDefMap.get("whir_pro_formsaveJsFunName");
		
		//有业务记录 的打开   数据
		//主键id  发起 有主键id  一般 是重新发起   或者从草稿箱打开
		//this.p_wf_recordId=request.getParameter("ezflowBusinessKey")==null?"":request.getParameter("ezflowBusinessKey").toString();
		//批示意见排序方式
		this.p_wf_commentSortType=processDefMap.get("whir_commentSortType")==null?"time_desc":processDefMap.get("whir_commentSortType").toString();
	    
		this.p_wf_orgcommentSortType=processDefMap.get("whir_orgcommentSortType")==null?"":processDefMap.get("whir_orgcommentSortType").toString();
		
		logger.debug("p_wf_orgcommentSortType:"+p_wf_orgcommentSortType);
		
		//字段联动
		this.p_wf_relationTrig=processDefMap.get("whir_relationTrig")==null?"":processDefMap.get("whir_relationTrig").toString();
		request.setAttribute("whir_taskButtons", whir_taskButtons);
       
		this.p_wf_processNeedPrint=processDefMap.get("whir_processNeedPrint")==null?"":processDefMap.get("whir_processNeedPrint").toString();
		
		this.p_wf_processType=processDefMap.get("whir_processType")==null?"1":processDefMap.get("whir_processType").toString();
		//相关流程
		
		//是否从首页传进来
		if (request.getParameter("portletSettingId") != null) {
			 portletSettingId = request.getParameter("portletSettingId");
		}
		//刷新父页面 是否是用openerReflush 方法 
		if (request.getParameter("flushWithFun") != null) {
			 flushWithFun = request.getParameter("flushWithFun");
		}
		//是否需要刷新父页面
		if (request.getParameter("noNeedFlush") != null) {
			 noNeedFlush = request.getParameter("noNeedFlush");
		}
		// 流程类型  1：业务流程  0：随机流程    2：半自由流程    3：完全自由流程
		if(p_wf_processType.equals("2")||p_wf_processType.equals("3")){
			this.p_wf_modiButton+=",SetProcess";
		}
		
		//表单 新增url  
		this.p_wf_url=processDefMap.get("whir_formAddUrl")==null?"":processDefMap.get("whir_formAddUrl").toString();
		//this.p_wf_url=processDefMap.get("whir_formUpdateUrl")==null?"":processDefMap.get("whir_formUpdateUrl").toString();
		
		initInfo();		 
		logger.debug("结束设置 流程发起时的信息");   
	}
	
	/**
	 * 打开待办等流程时 往页面执行的方法
	 */
	public boolean setUpdateInfo(Map map) {
		logger.debug("开始 setUpdateInfo");
		
		//处理状态  是成功还是失败
		boolean dealStatus=true;
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return false;
		}

		if(!judgeNotNull(p_wf_taskId)){
			if(judgeNotNull(request.getParameter("ezFlowTaskId"))){
				this.p_wf_taskId= request.getParameter("ezFlowTaskId");
			} 
		}
		
		if(!judgeNotNull(p_wf_processInstanceId)){
			if(judgeNotNull(request.getParameter("ezFlowProcessInstanceId"))){
				this.p_wf_processInstanceId= request.getParameter("ezFlowProcessInstanceId");
			} 
		}
		
		logger.debug("p_wf_openType:"+p_wf_openType);
		logger.debug("p_wf_processId:"+p_wf_processId);
		logger.debug("p_wf_taskId:"+p_wf_taskId);
		logger.debug("p_wf_processInstanceId:"+p_wf_processInstanceId);
		
		String curUserAccount=this.curUserInfoVO.getUserAccount();
		
		if(p_wf_openType!=null&&p_wf_openType.equals("print")){
			
		}else{
			/**
			 * 检验验证码
			 */
			if (!this.judgeCallRight(this.BASE_MODULE_CODE, "")) {
				logger.debug("检验验证码 失败");
				dealStatus = false;
			}
			
			//"我退回的文件"2016/5/26
			if(p_wf_openType.equals(EzFlowFinals.OPENTYPE_IBACKED)){
				String submitBackUser =request.getParameter("submitBackUser");
				logger.debug("submitBackUser----->"+submitBackUser);
				if(!submitBackUser.equals(curUserAccount)){
					return false;
				}
			}
		}
		
		EzFlowDealQueryService  dealQueryService=new EzFlowDealQueryService();
		
		//判断可是整数
	    if(!this.judgeInteger("rrecordId;rmoduleId;p_wf_recordId;p_wf_taskId;p_wf_processInstanceId")){
		    return  false ;
	    } 	 
		 
		/**
		 * 必须： p_wf_openType,userAccount,p_wf_recordId 
		 * p_wf_taskId  与 p_wf_processInstanceId 可选
		 * 
		 */
		Map processDefMap=dealQueryService.getEzFlowOpenInfo(p_wf_processId, p_wf_processInstanceId, p_wf_openType, p_wf_taskId, curUserAccount, p_wf_recordId);
        
		//程标题（待办标题）
		this.p_wf_remindTitle =processDefMap.get("whir_remindTitle")+"";
		
		String openwithnotself=processDefMap.get("openwithnotself")+"";
		
		if(openwithnotself.equals("1")){
			return false;
		}
		
		String openErrorInfo=processDefMap.get("openErrorInfo")+"";
		if(openErrorInfo.equals("NOwaitingTask")){
			return false;
		}
		
		if(openErrorInfo.equals("NOprocessInstanceId")){
			return false;
		}
		
		String assignee=""+processDefMap.get("assignee");
		if(curUserAccount==null||curUserAccount.equals("null")||curUserAccount.equals("")){
			return false;
		}
		//判断待办打开的人 可是本人
		if(assignee!=null&&!assignee.equals("null")&&!assignee.equals("")&&p_wf_openType!=null&&p_wf_openType.equals(EzFlowFinals.OPENTYPE_WATING_READ)){
            if(!assignee.equals(curUserAccount)){
            	return false;
            }
		}
		
		logger.debug("assignee----->"+assignee);
		logger.debug("curUserAccount----->"+curUserAccount);
		
		//隐藏字段  ,fieldid,,fieldid,……
		this.p_wf_concealField=""+processDefMap.get("whir_nodeHiddenField");
		logger.debug("p_wf_concealField----->"+p_wf_concealField);
		//写字段
		this.p_wf_cur_ModifyField=""+processDefMap.get("whir_nodeWriteField");
		
		//如果是阅件 不可写  只有办件才可以修改 waitingDeal
		if (p_wf_openType.equals("waitingRead") 
				|| p_wf_openType.equals("dealed")
				|| p_wf_openType.equals("readed")
				|| p_wf_openType.equals("ibacked")
				|| p_wf_openType.equals("myUnderWaitingDeal")
				|| p_wf_openType.equals("relation")
				|| p_wf_openType.equals("ibacked")
				|| p_wf_openType.equals("mailView")
				|| p_wf_openType.equals("myTask")) {
			p_wf_cur_ModifyField = "";
		}
		 
	    p_wf_task_type=processDefMap.get("whir_type")+"";
		//保护字段
		p_wf_protectedField=""+processDefMap.get("whir_protectedField");
		//批示意见字段 
		p_wf_curCommField=processDefMap.get("whir_nodeCommentField")==null?"":processDefMap.get("whir_nodeCommentField").toString();
		logger.debug("p_wf_curCommField--setUpdateInfo--->"+p_wf_curCommField);
		//阅件批示意见字段 
		p_wf_curPassRoundCommField=processDefMap.get("whir_passNodeCommentField")==null?"":processDefMap.get("whir_passNodeCommentField").toString();
		logger.debug("p_wf_curPassRoundCommField--setUpdateInfo--->"+p_wf_curPassRoundCommField);
	    if(p_wf_task_type.equals("1")){	
			p_wf_curCommField="nullCommentField";
			p_wf_curPassRoundCommField="nullCommentField";
		}
		
		// 表单code
	    if(!CommonUtils.isEmpty(processDefMap.get("whir_formKey"))){
	    	this.p_wf_formKey=com.whir.component.security.crypto.EncryptUtil.sqlcode(""+processDefMap.get("whir_formKey"));
	    }
		//活动 表单code
		this.p_wf_formKey_act=processDefMap.get("whir_formKey_act")==null?"":processDefMap.get("whir_formKey_act").toString();
		
        logger.debug("processDefinitionId:"+processDefMap.get("processDefinitionId"));
		//流程定义id
		this.p_wf_processId=""+processDefMap.get("processDefinitionId");
		this.p_wf_processInstanceId=""+processDefMap.get("processInstanceId");
		
		//-------------------------流程信息  start------------------------------
		
		//表单提醒字段
		this.p_wf_remindField=""+processDefMap.get("whir_processRemindField");
        
		this.p_wf_processName=processDefMap.get("processDefinitionName")==null?"":processDefMap.get("processDefinitionName").toString();
	
		//流程变量 ,fieldid,,fieldid,……
		this.p_wf_processallVariables=""+processDefMap.get("whir_processallVariables");
		
		//批示意见排序方式
		p_wf_commentSortType=processDefMap.get("whir_commentSortType")==null?"time_desc":processDefMap.get("whir_commentSortType").toString();
		
		this.p_wf_orgcommentSortType=processDefMap.get("whir_orgcommentSortType")==null?"":processDefMap.get("whir_orgcommentSortType").toString();
	    
		//字段联动
		p_wf_relationTrig=processDefMap.get("whir_relationTrig")==null?"":processDefMap.get("whir_relationTrig").toString();
		//保留退回意见
		p_wf_processKeepBackComment=processDefMap.get("whir_processKeepBackComment")==null?"":processDefMap.get("whir_processKeepBackComment").toString();

		//批示意见上传附件
		p_wf_processCommentAcc=processDefMap.get("whir_processCommentAcc")==null?"":processDefMap.get("whir_processCommentAcc").toString();
      
		//是否需要归档
		p_wf_processNeedDossier=processDefMap.get("whir_processNeedDossier")==null?"":processDefMap.get("whir_processNeedDossier").toString();
        
		//归档路径类型
		p_wf_processNeedDossierType=processDefMap.get("whir_processNeedDossierType")==null?"":processDefMap.get("whir_processNeedDossierType").toString();
		
		//归档类目
		p_wf_processNeedDossierPath=processDefMap.get("whir_processNeedDossierPath")==null?"":processDefMap.get("whir_processNeedDossierPath").toString();
		
		//发起人是否可以打印
		p_wf_processNeedPrint=processDefMap.get("whir_processNeedPrint")==null?"":processDefMap.get("whir_processNeedPrint").toString();
       
		//批示意见是否可以为空
		p_wf_commentNotNull=processDefMap.get("whir_processCommentIsNull")==null?"":processDefMap.get("whir_processCommentIsNull").toString();
        
		//办理人设置审批意见查看范围  true/false
		p_wf_commentRangeByDealUser=processDefMap.get("whir_commentRangeByDealUser")==null?"false":processDefMap.get("whir_commentRangeByDealUser").toString();
        
		//处理类名	
		this.p_wf_classname=""+processDefMap.get("whir_classname");
		this.p_wf_saveData=""+processDefMap.get("whir_saveData");
		this.p_wf_completeData=""+processDefMap.get("whir_completeData");
		
		this.p_wf_forminitJsFunName=""+processDefMap.get("whir_pro_forminitJsFunName");
		this.p_wf_formsaveJsFunName=""+processDefMap.get("whir_pro_formsaveJsFunName");
		
		this.p_wf_processType=processDefMap.get("whir_processType")==null?"1":processDefMap.get("whir_processType").toString();
		
		//批示意见范围id
	    String whir_commentRangeEmpId=processDefMap.get("whir_commentRangeEmpId")==null?"":processDefMap.get("whir_commentRangeEmpId").toString();
 
	    //审批意见范围
        whir_commentRangeUsers="";
        whir_commentRangeOrgs="";
        whir_commentRangeGroups="";
	    if(EzFlowUtil.judgeNull(whir_commentRangeEmpId)){
	    	whir_commentRangeId=whir_commentRangeEmpId;
	    	for( int  i = 0; i < whir_commentRangeEmpId.length(); i ++ ){
	            char flagCode = whir_commentRangeEmpId.charAt(i);
	            int nextPos = whir_commentRangeEmpId.indexOf(flagCode,i + 1);
	            String str = whir_commentRangeEmpId.substring(i,nextPos+1);
	            if(flagCode=='$'){
	            	whir_commentRangeUsers = whir_commentRangeUsers + str;
	            }else if(flagCode == '*'){
	            	whir_commentRangeOrgs = whir_commentRangeOrgs + str;
	            }else{
	            	whir_commentRangeGroups = whir_commentRangeGroups + str;
	            }
	            i = nextPos;
	        }
	    }else{
	    	whir_commentRangeUsers="-1";
	    	whir_commentRangeOrgs="-1";
	    	whir_commentRangeGroups="-1";
	    	whir_commentRangeId="*0*";
	    }  
	    
		//批示意见 态度类型设置
	    p_wf_commentAttitudeTypeSet=processDefMap.get("whir_commentAttitudeTypeSet")==null?"":processDefMap.get("whir_commentAttitudeTypeSet").toString();
	    //保存退回发起人前的批示意见
	    p_wf_processKeepReSubmitComment=processDefMap.get("whir_processKeepReSubmitComment")==null?"":processDefMap.get("whir_processKeepReSubmitComment").toString();
	    
	    //子表：允许新增行
	    p_wf_subTableDataControlAdd=processDefMap.get("whir_subTableDataControlAdd")==null?"":processDefMap.get("whir_subTableDataControlAdd").toString();
	    logger.debug("p_wf_subTableDataControlAdd--setUpdateInfo--->"+p_wf_subTableDataControlAdd);
	    
	    //子表：新增默认空行
	    p_wf_subTableDataControlAddNull=processDefMap.get("whir_subTableDataControlAddNull")==null?"":processDefMap.get("whir_subTableDataControlAddNull").toString();
	    logger.debug("p_wf_subTableDataControlAddNull--setUpdateInfo--->"+p_wf_subTableDataControlAddNull);
	    
	    //子表：允许修改已有行数据
	    p_wf_subTableDataControlModi=processDefMap.get("whir_subTableDataControlModi")==null?"":processDefMap.get("whir_subTableDataControlModi").toString();
	    logger.debug("p_wf_subTableDataControlModi--setUpdateInfo--->"+p_wf_subTableDataControlModi);
	    
	    //子表：允许删除已有行数据
	    p_wf_subTableDataControlDel=processDefMap.get("whir_subTableDataControlDel")==null?"":processDefMap.get("whir_subTableDataControlDel").toString();
	    logger.debug("p_wf_subTableDataControlDel--setUpdateInfo--->"+p_wf_subTableDataControlDel);
		//---------------------------当前活动信息  start ---------------------------
	    // 当前活动id
		p_wf_cur_activityId=processDefMap.get("curActivityId")==null?"0":processDefMap.get("curActivityId").toString();
		// 当前活动名
		p_wf_cur_activityName=processDefMap.get("curActivityName")==null?"0":processDefMap.get("curActivityName").toString();	
		//此活动的第几次办理
		p_wf_activityStep=processDefMap.get("whir_activityStep")==null?"0":processDefMap.get("whir_activityStep").toString();
		
		//代理信息      0： 正常1： 代理其它办理的     
		p_wf_isProxy=processDefMap.get("whir_isProxy")==null?"0":processDefMap.get("whir_isProxy").toString();
		p_wf_proxyAssignee=processDefMap.get("whir_proxyAssignee")==null?"":processDefMap.get("whir_proxyAssignee").toString();
		p_wf_proxyAssigneeName=processDefMap.get("whir_proxyAssigneeName")==null?"":processDefMap.get("whir_proxyAssigneeName").toString();
 
		//代理的任务id
		p_wf_proxyTaskId=processDefMap.get("whir_proxyTaskId")==null?"":processDefMap.get("whir_proxyTaskId").toString();
				
		//是否是转办     0： 正常      2： 从其它转办过来的     3： 从其它转办过来的，办理需要自动返回
		p_wf_isTransfer=processDefMap.get("whir_isTransfer")==null?"0":processDefMap.get("whir_isTransfer").toString();
		//转办 来自哪个任务id
		p_wf_transferFromId=processDefMap.get("whir_transferFromId")==null?"":processDefMap.get("whir_transferFromId").toString();
		//是否是 自动活动任务     0： 正常      1:是
		p_wf_isbacktrackAct=processDefMap.get("whir_isbacktrackAct")==null?"0":processDefMap.get("whir_isbacktrackAct").toString();
		//自动返回活动 来自那个任务id
		p_wf_backtrackFromTaskId=processDefMap.get("whir_backtrackFromTaskId")==null?"":processDefMap.get("whir_backtrackFromTaskId").toString();
        
		//退回后的活动是否需要 直接返回到退回点
		p_wf_isneedSendtoBack=processDefMap.get("whir_isneedSendtoBack")==null?"":processDefMap.get("whir_isneedSendtoBack").toString();
		
	    //从哪个活动任务退回过来的
		p_wf_backFromTaskId=processDefMap.get("whir_backFromTaskId")==null?"":processDefMap.get("whir_backFromTaskId").toString();
	 		
		//任务所在活动的id
		//String  activityId=processDefMap.get("activityId")==null?"":processDefMap.get("activityId").toString();
 
		p_wf_taskId=""+processDefMap.get("taskId");
						
		//办理按钮
		List whir_taskButtons=(List)processDefMap.get("whir_taskButtons");
		
		//主键id
		p_wf_recordId=processDefMap.get("ezflowBusinessKey")==null?p_wf_recordId:processDefMap.get("ezflowBusinessKey").toString();
        
		if(p_wf_recordId!=null&&p_wf_recordId.equals("-1")){
			p_wf_recordId="";
		}

    	//修改方法名
		p_wf_updateData=""+processDefMap.get("whir_updateData");
 
    	//退回时
		p_wf_backData=processDefMap.get("whir_backData")+"";
		
		p_wf_acti_forminitJsFunName=""+processDefMap.get("whir_acti_forminitJsFunName");
		p_wf_acti_formsaveJsFunName=""+ processDefMap.get("whir_acti_formsaveJsFunName");
		
	    p_wf_activityTip=""+ processDefMap.get("whir_activityTip");
	    p_wf_activityTipCotent=""+ processDefMap.get("whir_activityTipCotent");
	    p_wf_activityTipTitle=""+ processDefMap.get("whir_activityTipTitle");
		
		//---------------------------当前活动信息  end ---------------------------
		
		//---------------------------流程实例信息  start--------------------------
		//发起人名
		p_wf_submitPerson=processDefMap.get("whir_startUserName")==null?"":processDefMap.get("whir_startUserName").toString();
		//发起组织id
		p_wf_startOrgId=processDefMap.get("whir_startOrgId")==null?"":processDefMap.get("whir_startOrgId").toString();
		//发起组织名
	    p_wf_startOrgName=processDefMap.get("whir_startOrgName")==null?"":processDefMap.get("whir_startOrgName").toString();
		
	    //发起时间
	    Date  ezFlow_startTime=new Date();  
	    String ezFlow_startTime_str="";
	    if(processDefMap.get("ezFlow_startTime")!=null){
	    	ezFlow_startTime=(Date)processDefMap.get("ezFlow_startTime");
	    }
	    p_wf_submitTime=simpleDateFormat.format(ezFlow_startTime);
	    
	    //发起人帐号
	    p_wf_submitUserAccount=processDefMap.get("ezFlow_startUser")==null?"":processDefMap.get("ezFlow_startUser").toString();    
	    //默认是在办
	    p_wf_workStatus=processDefMap.get("whir_stauts")==null?"1":processDefMap.get("whir_stauts").toString();
        //父流程id
	    p_wf_superProcessInstanceId=processDefMap.get("superProcessInstanceId")==null?"":processDefMap.get("superProcessInstanceId").toString();
	    
		p_wf_superProcess_formKey=processDefMap.get("superProcessInstance_formKey")==null?"":processDefMap.get("superProcessInstance_formKey").toString();
		p_wf_superProcess_recordId=processDefMap.get("superProcessInstance_businesKey")==null?"":processDefMap.get("superProcessInstance_businesKey").toString();
		
	    p_wf_deleteReason=processDefMap.get("deleteReason")==null?"":processDefMap.get("deleteReason").toString();
 	
		//办理提醒
	    p_wf_dealTips=processDefMap.get("whir_dealTips")==null?"":processDefMap.get("whir_dealTips").toString();
	    
		//表单 新增url  
		//this.p_wf_url=processDefMap.get("whir_formAddUrl")==null?"":processDefMap.get("whir_formAddUrl").toString();
		this.p_wf_url=processDefMap.get("whir_formUpdateUrl")==null?"":processDefMap.get("whir_formUpdateUrl").toString();
	    
		if(processDefMap.get("whir_updateURL")!=null&&!processDefMap.get("whir_updateURL").toString().equals("")&&!processDefMap.get("whir_updateURL").toString().equals("null")){
		      this.p_wf_mainLinkFile= processDefMap.get("whir_updateURL").toString();
		}
 
	    logger.debug(" update p_wf_dealTips: "+p_wf_dealTips);
	    logger.debug(" update p_wf_deleteReason: "+p_wf_deleteReason);
	    //退回发起人 等的提示
		//if ((p_wf_workStatus.equals("-1")||p_wf_workStatus.equals("-2")) && p_wf_openType.equals("myTask")) {
	    if ((p_wf_workStatus.equals("-1")||p_wf_workStatus.equals("-2"))&&(p_wf_openType.equals("myTask")||p_wf_openType.equals("reStart"))) {
			p_wf_dealTips = p_wf_deleteReason;
			if (p_wf_dealTips.equals("backToSubmit")||p_wf_dealTips.equals("cancelProcess")) {
				p_wf_dealTips = "";
			}
		} 
	    if (this.p_wf_openType.equals("ibacked")) {
			p_wf_dealTips = p_wf_deleteReason;
			if (p_wf_dealTips.equals("backToSubmit")||p_wf_dealTips.equals("cancelProcess")) {
				p_wf_dealTips = "";
			}
		}
	    
		//当前经过的网关节点名
		this.p_wf_whir_dealedActInfo=processDefMap.get("whir_dealedActInfo")==null?"":processDefMap.get("whir_dealedActInfo").toString();
		
	    //---------------------------流程实例信息   end --------------------------
		// 打开的几种组合
		/**
		 * 知道： 1) p_wf_workId , p_wf_openType 2) p_wf_recordId , p_wf_tableId
		 * p_wf_openType 3) p_wf_recordId , p_wf_tableId p_wf_openType 4)
		 * p_wf_recordId , p_wf_moduleId p_wf_openType
		 * */
		if (judgeNotNull(p_wf_taskId) && judgeNotNull(p_wf_openType)) {
			logger.debug("p_wf_taskId   p_wf_openType 类型 1");
			/**
			 * 检验验证码
			 */
			if (!this.judgeCallRight(this.BASE_MODULE_CODE, "")) {
				dealStatus = false;
			}
		}

		// 设置按钮
		if (dealStatus) {
			//这里面流程key   setUpdateButton 会用到 ，所以放到之前
			initInfo();
			setUpdateButton(whir_taskButtons); 
			//流程加锁
			addLock();
		}
		//父流程信息 ，主要用来处理 子表继承老数据
		if(this.p_wf_openType!=null&&p_wf_openType.equals("waitingDeal")){
			dealSuperProcess();
			p_wf_workStatus="0";
		}
		
		//处理 子过程活动
		dealSubActivity(processDefMap);
		
		setMailviewUrl();
		debuglog();
	
		logger.debug("dealStatus：" + dealStatus);
		logger.debug("结束 setUpdateInfo");
		return dealStatus;
	}
 	
	/**
	 * 设置打开流程的地址
	 */
	private void setMailviewUrl(){
		String mainUrl=com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/ezflowopen!updateProcess.action?";
		if(this.p_wf_mainLinkFile!=null&&!this.p_wf_mainLinkFile.equals("")){
			if(p_wf_mainLinkFile.startsWith(com.whir.component.config.PropertiesUtil.getInstance().getRootPath())){ 
			}else{
				p_wf_mainLinkFile=com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+p_wf_mainLinkFile;
			}
			
			if(p_wf_mainLinkFile.indexOf("?")>=0){
				mainUrl= p_wf_mainLinkFile+"&";
			}else{
				mainUrl= p_wf_mainLinkFile+"?";
			}
		} 
		
		logger.debug("mialSend mainUrl： "+mainUrl); 

		// 表单打开地址
		String flowurl =mainUrl
				+ "p_wf_processInstanceId="
				+ p_wf_processInstanceId+"&ezFlowProcessInstanceId="+p_wf_processInstanceId
				+ "&p_wf_recordId=" +p_wf_recordId
				+ "&p_wf_openType=mailView"; 

		logger.debug("mialSend flowurl： "+flowurl);
		 EncryptUtil  encryptUtil=new EncryptUtil(request);
		String verifyCode= encryptUtil.getSysEncoderKeyVlaue("ezFlowProcessInstanceId",p_wf_processInstanceId,"WFDealWithAction");
		
		flowurl =mainUrl
				+ "ezFlowProcessInstanceId="
				+ p_wf_processInstanceId+ "&p_wf_recordId=" +p_wf_recordId
				+ "&p_wf_openType=mailView&verifyCode="+verifyCode; 
		this.p_wf_mailviewUrl=flowurl;
	}
	
	/**
	 * 处理父流程的信息
	 */
	private void dealSuperProcess() {
		com.whir.ezoffice.ezform.service.FormService _formService = new com.whir.ezoffice.ezform.service.FormService();
		if (true||(this.p_wf_recordId == null || p_wf_recordId.equals("")|| p_wf_recordId.equals("null")|| p_wf_recordId.equals("-1"))) {
			if (this.p_wf_superProcess_recordId != null
					&& !p_wf_superProcess_recordId.equals("")
					&& !p_wf_superProcess_recordId.equals("null")) {  
				com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService  defService=new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService();			 
				String inheritInfo="";
				
				Map<String, Object> inheritMapInfo=defService.getInheritParentInfo2(this.p_wf_superProcessInstanceId, this.p_wf_processDefinitionKey);		 
				
				inheritInfo=inheritMapInfo.get("whir_inheritParent") == null ? "false" : inheritMapInfo.get("whir_inheritParent").toString();
				if(inheritInfo.equals("true")){
					parentFormId = _formService.getFormIdByFormCode(this.p_wf_superProcess_formKey, "0");
					parentRecordId = p_wf_superProcess_recordId;
					request.setAttribute("inheritInfo", "1");
				}else{
					//不继承 主表相同数据  就查看 有没有对应字段设置  。
					List<Map<String,String>> whir_callFieldJICHENList=(List<Map<String,String>>)inheritMapInfo.get("whir_callFieldJICHEN");	
					
					/*
					if (whir_callFieldJICHENList != null) {
						Map curButtonMap = null;
						String callmainFieldName = "";
						String callsubFieldName="";
						for (int i = 0; i < whir_callFieldJICHENList.size(); i++) {
							 curButtonMap = (Map) whir_callFieldJICHENList.get(i); 
							 //主表字段 
							 callmainFieldName = "" + curButtonMap.get("callmainFieldName");  a
							 //子表字段 
							 callsubFieldName = "" + curButtonMap.get("callsubFieldName");  a  
						}
					}*/
					if(whir_callFieldJICHENList!=null&&whir_callFieldJICHENList.size()>0){
						parentFormId = _formService.getFormIdByFormCode(this.p_wf_superProcess_formKey, "0");
						parentRecordId = p_wf_superProcess_recordId; 
						request.setAttribute("whir_callFieldJICHENList", whir_callFieldJICHENList);
					}
				}
			}
		}
	}
	
	
	/**
	 * 流程加锁
	 */
    private void addLock(){
    	//待办打开 、待阅打开、我的文件打开 判断流程锁
		if(this.p_wf_openType.equals(EzFlowFinals.OPENTYPE_WATING_DEAL) || this.p_wf_openType.equals(EzFlowFinals.OPENTYPE_WATING_READ) ||
			this.p_wf_openType.equals(EzFlowFinals.OPENTYPE_MYTASK)){
		//if(true){
			Map onlieMap=new HashMap();
			onlieMap.put("businessKey", this.p_wf_recordId);
			onlieMap.put("formKey", this.p_wf_formKey);
			onlieMap.put("processInstanceId", p_wf_processInstanceId);
			onlieMap.put("activityId", this.p_wf_cur_activityId);
			
			EzFlowMainService  mainSercice=new EzFlowMainService();
			Map onlineResultMap=mainSercice.getEzFlowOnlineUser(onlieMap);
			request.setAttribute("onlineResultMap", onlineResultMap);	
			String onlineUserAccount=onlineResultMap.get("userAccount")==null?"":onlineResultMap.get("userAccount").toString();
			String onlineUserName=onlineResultMap.get("userName")==null?"":onlineResultMap.get("userName").toString();
	 	
			//不为空 说明有人加锁了
			if(EzFlowUtil.judgeNull(onlineUserAccount)&&!onlineUserAccount.equals(this.curUserInfoVO.getUserAccount())){
				request.setAttribute("onlineUserName", onlineUserName);
				this.p_wf_modiButton="";
				if(this.p_wf_moduleId.equals("2")||this.p_wf_moduleId.equals("3")||this.p_wf_moduleId.equals("34")){
					this.p_wf_modiButton="Viewtext";
				}
				
				p_wf_extbuttonSetIds="";  
			    request.setAttribute("extendButtonMap", new HashMap());
		    }else{
		    	//为空  就自己加锁
		    	Map inputVarMap=new HashMap();
		    	inputVarMap.put("activityId", p_wf_cur_activityId);
		    	inputVarMap.put("processInstanceId", p_wf_processInstanceId);
		    	inputVarMap.put("businessKey", this.p_wf_recordId);
		    	inputVarMap.put("formKey", p_wf_formKey);
		    	inputVarMap.put("pageKey", "pageKey");
		    	inputVarMap.put("taskId", p_wf_taskId);
		    	inputVarMap.put("userAccount", this.curUserInfoVO.getUserAccount());
		    	inputVarMap.put("userName", this.curUserInfoVO.getUserName());
		    	mainSercice.setEzFlowOnlineUser(inputVarMap);
		    }	 
            if(this.p_wf_openType.equals("waitingDeal")||this.p_wf_openType.equals("waitingRead")){				
				//签收日志
				EzFlowLogService   logService=new EzFlowLogService();
				Map logMap=new HashMap();			
				logMap.put("dealUserId",curUserInfoVO.getUserId());
				logMap.put("dealUserName", curUserInfoVO.getUserName());
				logMap.put("dealUserAccount", curUserInfoVO.getUserAccount());
				logMap.put("domainId", curUserInfoVO.getDomainId());
				logMap.put("recordId", p_wf_recordId);
				logMap.put("processInstanceId", p_wf_processInstanceId);
				logMap.put("nowTaskId", p_wf_taskId);	
				logMap.put("curActivityId", this.p_wf_cur_activityId);
				logMap.put("curActivityName", this.p_wf_cur_activityName);	
				logMap.put("curActivityStep", p_wf_activityStep);
				logService.dealViewLog(logMap);
			}
		}
    }
    
    //处理子过程活动
    private  void  dealSubActivity(Map processDefMap){ 
    	p_wf_activityclassType=""+processDefMap.get("whir_activityclassType");
    	if(p_wf_activityclassType!=null&&p_wf_activityclassType.equals("1")){
	    	p_wf_subactivitytype=""+processDefMap.get("whir_subactivitytype");
	        p_wf_extendMainTable=""+processDefMap.get("whir_extendMainTable");
	    	p_wf_subactivityIds=""+processDefMap.get("whir_subactivityIds");  
	    	com.whir.ezoffice.bpm.bd.BPMProcessBD  bpmbd=new com.whir.ezoffice.bpm.bd.BPMProcessBD();
	    	List<BPMProcessPO> subalist=bpmbd.loadBPMProcessPOByEzFlowKeys(p_wf_subactivityIds); 
	    	List p_wf_subActivityProList=new ArrayList();
	    	for(BPMProcessPO po:subalist){
	    		p_wf_subActivityProList.add(new String []{po.getPoolEzFlowProcessKey(),po.getPoolProcessName(),po.getPoolProcessId()+"",po.getPoolStartUrl()});
	    	} 
	    	request.setAttribute("p_wf_subActivityProList", p_wf_subActivityProList);
    	}
    }
}
