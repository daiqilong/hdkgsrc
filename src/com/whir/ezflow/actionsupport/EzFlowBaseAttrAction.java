package com.whir.ezflow.actionsupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.activiti.engine.impl.persistence.entity.WhirEzFlowCommentEntity;
import org.apache.log4j.Logger;
import com.whir.common.util.CommonUtils;
import com.whir.component.security.crypto.EncryptUtil;
import com.whir.ezflow.util.EzFlowFinals;
import com.whir.ezflow.util.EzFlowOpenTypeFinals;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.UserInfoVO;
import com.whir.ezoffice.bpm.bd.BPMFormInfoBD;
import com.whir.ezoffice.bpm.bd.BPMProcessBD;
import com.whir.ezoffice.bpm.bd.BPMRelationBD;
import com.whir.ezoffice.bpm.po.BPMProcessPO;
import com.whir.ezoffice.information.infomanager.bd.InformationBD;
import com.whir.ezoffice.message.bd.messageSettingBD;
import com.whir.ezoffice.monitor.bd.WFDealSearchBD;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.service.api.ezflowservice.EzFlowCommentService;
import com.whir.service.api.ezflowservice.EzFlowMainService;
import com.whir.service.api.ezflowservice.EzFlowTaskService;

public class EzFlowBaseAttrAction  extends EzFlowBaseAction{
	
	private static Logger logger = Logger.getLogger(EzFlowBaseAttrAction.class.getName());
	
	//是否是定时发送
	protected String  p_wf_dealWithJob="0";
	
	//表单类型   2：默认表单    0：  自定义表单 
    protected String p_wf_pool_formType="0";  
	//流程定义id
	protected String p_wf_pool_processId;
	protected String p_wf_pool_processType;
	
	// 流程的定义id
	protected String p_wf_processId;  //p_wf_processDefinitionId    processDefinitionId
	
	protected String p_wf_processDefinitionKey;
	// 流程实例id
	protected String p_wf_processInstanceId;   //processInstanceId
	
	//父流程id
	protected String p_wf_superProcessInstanceId; //superProcessInstanceId
	
	//父流程的表单key
	protected String p_wf_superProcess_formKey;
	//父流程的主键id
	protected String p_wf_superProcess_recordId;
	
	//父流程的 parentFormId  有下面两个值 则继承主表数据
	protected String parentFormId="";
	protected String parentRecordId=""; 
	
	// 任务id
	protected String p_wf_taskId;
	
	// 流程定义名
	protected String p_wf_processName;//p_wf_processDefinitionName
	
	//  ropenType  
	
	//打开流程的操作类型
	//再次打开的类型    ropenType： fromDraft 打开草稿     startAgain 再次发起        reStart  重新提交
	/**
	 *  
		startOpen :   发起  
	    modifyView:   办理查阅打开  
	    reStart   :   重新发起  
	    startAgain:   再次发起  
	  	
	    waitingDeal:  待办打开  
	    waitingRead:  待阅打开  
	    readed     :  已阅打开  
	    dealed     :  已办打开  
	    myTask     :  我的文件打开  
	    fromDraft  :  从草稿箱打开
	    mailView   : 邮件地址打开
	 */
	protected String p_wf_openType;
	
	// 流程主键id
	protected String p_wf_recordId;
	
	// 流程使用的表单id
	protected String p_wf_formId;
	// 流程使用的表单id
	protected String p_wf_tableId;
	
	//表单code
	protected String p_wf_formKey;
	protected String p_wf_formKey_act;
 	
	//短信模块
	protected String  p_wf_msgFrom="工作流程";
	
	//流程按钮名
	protected String  p_wf_modiButton="Send,Relation,EzFlowSaveDraft,EzFlowJobStart";
	//退回按钮的范围
	protected String  p_wf_button_backRange="0";
	
	protected String  p_wf_extbuttonSetIds="";
	//自定义按钮的函数体
	protected String  p_wf_extButtonFunContent="";
	
	// 提醒字段
	protected String  p_wf_titleFieldName="";
	
	//提醒字段
	protected String p_wf_remindField; // whir_processRemindField
	
	//提交的表单名
	protected String  p_wf_dataForm="dataForm";
	//父页面查询表单名
	protected String  p_wf_queryForm="queryForm";
	//表单提交前的验证js函数
	protected String  p_wf_initPara="beforeSubmit";
	//当前能修改的字段
	protected String  p_wf_cur_ModifyField="";
	
	//隐藏字段
	protected String  p_wf_concealField="";
	
	//批示意见字段
	protected String  p_wf_curCommField="";
	//批示意见字段类型
	protected String  p_wf_commFieldType="";
	//阅件批示意见
	protected String  p_wf_curPassRoundCommField="";
	
	/*办理人设置审批意见查看范围  true/false */
	protected String  p_wf_commentRangeByDealUser="false";
	
	//批示意见不能为空   ：1 :不能为空    0：可以为空
	protected String  p_wf_commentNotNull;
	
	//批量办理的workIds;
	protected String p_wf_batchWorkIds;
	
    protected String p_wf_workStatus="0"; //whir_stauts
	//当前活动信息
	protected String  p_wf_cur_activityId;  //curActivityId
	protected String  p_wf_cur_activityName; //curActivityName
	
	//此活动的第几次办理
	protected String  p_wf_activityStep;
	
	////代理信息      0： 正常1： 代理其它办理的  
	protected String  p_wf_isProxy;
	protected String  p_wf_proxyAssignee;
	protected String  p_wf_proxyAssigneeName;
	////代理的任务id
	protected String  p_wf_proxyTaskId;
	
	//是否是转办     0： 正常      2： 从其它转办过来的     3： 从其它转办过来的，办理需要自动返回
    protected String  p_wf_isTransfer;
    //转办 来自哪个任务id
	protected String  p_wf_transferFromId;
	
	//是否是 自动活动任务     0： 正常      1:是
	protected String p_wf_isbacktrackAct;
	
	//自动返回活动 来自那个任务id
	protected String p_wf_backtrackFromTaskId;
	
	//退回后的活动是否需要 直接返回到退回点
	protected String p_wf_isneedSendtoBack;
	
	//从哪个活动任务退回过来的
	protected String p_wf_backFromTaskId;
	
	//-----------------------------------------------
	
	//流程变量字段
	protected String p_wf_processallVariables;
	//处理类名
	protected String p_wf_classname;
	//保存方法名
	protected String p_wf_saveData;
	//完成时
	protected String p_wf_completeData;
	//
	protected String p_wf_updateData;
	protected String p_wf_backData;
	
	protected String  whir_commentRangeId="*0*";
	protected String  whir_commentRangeUsers="";
	protected String  whir_commentRangeOrgs="";
	protected String  whir_commentRangeGroups="";
	
	//打开时执行的js方法名
	protected String p_wf_forminitJsFunName;
	//保存时执行的js方法名
	protected String p_wf_formsaveJsFunName;
	
	protected String p_wf_acti_forminitJsFunName;
	protected String p_wf_acti_formsaveJsFunName;
	
	//排序字段
	protected String p_wf_commentSortType;
	
	protected String p_wf_orgcommentSortType;
	//字段联动
	protected String p_wf_relationTrig;
	
	//发起人是否可以打印
	protected String p_wf_processNeedPrint;
	
	//保护字段
	protected String p_wf_protectedField;
	
	//保留退回意见
	protected String p_wf_processKeepBackComment;
	
	//保存退回发起人前的批示意见	
	protected String p_wf_processKeepReSubmitComment;

	//子表：允许新增行
	protected String p_wf_subTableDataControlAdd;
	
	//子表：新增默认空行
	protected String p_wf_subTableDataControlAddNull;
	
	//子表：允许修改已有行数据
	protected String p_wf_subTableDataControlModi;
	
	//子表：允许删除已有行数据
	protected String p_wf_subTableDataControlDel;
	
	//批示意见是否上传附件
	protected String p_wf_processCommentAcc;
	
	//是否需要归档
	protected String p_wf_processNeedDossier;
	
	//归档路径类型
	protected String p_wf_processNeedDossierType;
	
	//归档类目
	protected String p_wf_processNeedDossierPath;
	
	//删除原因
	protected String p_wf_deleteReason;
	//办理提醒
	protected String p_wf_dealTips;
	
	// 相当于任务id
	protected String p_wf_workId;
	
	// 流程类型 1':'业务流程','0':'随机流程
	protected String p_wf_processType;
	//
	protected String p_wf_url;
    //模块id
	protected String p_wf_moduleId="1";

	// '0':'自定义表单','1':'JSP文件'
	protected String p_wf_formType;
	
	//发起人姓名
	protected String  p_wf_submitPerson;   //whir_startUserName
	
	//发起人帐号
	protected String  p_wf_submitUserAccount;
	//发起人id
	protected String  p_wf_submitPersonId;
	//发起时间
	protected String  p_wf_submitTime;
	
	//发起组织id
	protected String  p_wf_startOrgId;
	//发起组织名
	protected String  p_wf_startOrgName;
	
	//当前步骤数
	protected String  p_wf_stepCount;
	
	//流程办理期限-
	protected String p_wf_processDeadlineDate;
	
	//是否从首页传进来
	protected String portletSettingId;
	//刷新父页面 是否是用openerReflush 方法 
	protected String flushWithFun;
	//是否需要刷新父页面
	protected String noNeedFlush="0";
 
	//打开流程连接的主地址
	protected String p_wf_mainLinkFile=com.whir.component.config.PropertiesUtil.getInstance().getRootPath()
			                           +"/ezflowopen!updateProcess.action";
	
	protected String p_wf_passLinkFile=com.whir.component.config.PropertiesUtil.getInstance().getRootPath()
            +"/ezflowopen!updateProcess.action";
	
	//新建子流程的连接
	protected String p_wf_subProcHref=com.whir.component.config.PropertiesUtil.getInstance().getRootPath()
            +"/ezflowopen!startProcess.action";
	
	//0 正常任务，  1： 空岗自动跳转的任务
	protected String p_wf_task_type="0";
	
	protected String p_wf_activityTip="0";// 活动提醒  0：没有  1：有
	protected String p_wf_activityTipTitle="";// 活动提醒标题
	protected String p_wf_activityTipCotent="";// 活动提醒内容 
	
	protected String p_wf_mailviewUrl="";
	
	// 意见态度设置：   0 不显示态度  1显示同意与不同意 2 显示已阅、同意与不同意 
	protected String  p_wf_commentAttitudeTypeSet; 
	
	//进过的网关 节点信息
	protected String p_wf_whir_dealedActInfo;
	
	//流程标题（待办标题）
	protected String p_wf_remindTitle;
	
	
	
	//子过程活动 
	protected String  p_wf_activityclassType;
	protected String  p_wf_subactivitytype;
	protected String  p_wf_extendMainTable;
	protected String  p_wf_subactivityIds; 
	
	
	public String getP_wf_remindTitle() {
		return p_wf_remindTitle;
	}
	public void setP_wf_remindTitle(String pWfRemindTitle) {
		p_wf_remindTitle = pWfRemindTitle;
	}
	
	public String getP_wf_processId() {
		return p_wf_processId;
	}
	public void setP_wf_processId(String p_wf_processId) {
		this.p_wf_processId = p_wf_processId;
	}
	
	public String getP_wf_processInstanceId() {
		return p_wf_processInstanceId;
	}
	public void setP_wf_processInstanceId(String p_wf_processInstanceId) {
		this.p_wf_processInstanceId = p_wf_processInstanceId;
	}
	
	public String getP_wf_superProcessInstanceId() {
		return p_wf_superProcessInstanceId;
	}
	public void setP_wf_superProcessInstanceId(String p_wf_superProcessInstanceId) {
		this.p_wf_superProcessInstanceId = p_wf_superProcessInstanceId;
	}
	
	public String getP_wf_taskId() {
		return p_wf_taskId;
	}
	public void setP_wf_taskId(String p_wf_taskId) {
		this.p_wf_taskId = p_wf_taskId;
	}
	
	public String getP_wf_processName() {
		return p_wf_processName;
	}
	public void setP_wf_processName(String p_wf_processName) {
		this.p_wf_processName = p_wf_processName;
	}
	
	public String getP_wf_openType() {
		return p_wf_openType;
	}
	public void setP_wf_openType(String p_wf_openType) {
		this.p_wf_openType = p_wf_openType;
	}
	
	public String getP_wf_recordId() {
		return p_wf_recordId;
	}
	public void setP_wf_recordId(String p_wf_recordId) {
		this.p_wf_recordId = p_wf_recordId;
	}
	
	public String getP_wf_formId() {
		return p_wf_formId;
	}
	public void setP_wf_formId(String p_wf_formId) {
		this.p_wf_formId = p_wf_formId;
	}
	
	public String getP_wf_tableId() {
		return p_wf_tableId;
	}
	public void setP_wf_tableId(String p_wf_tableId) {
		this.p_wf_tableId = p_wf_tableId;
	}
	
	public String getP_wf_formKey() {
		return p_wf_formKey;
	}
	public void setP_wf_formKey(String p_wf_formKey) {
		this.p_wf_formKey = p_wf_formKey;
	}
	
	public String getP_wf_formKey_act() {
		return p_wf_formKey_act;
	}
	public void setP_wf_formKey_act(String p_wf_formKey_act) {
		this.p_wf_formKey_act = p_wf_formKey_act;
	}
	
	public String getP_wf_msgFrom() {
		return p_wf_msgFrom;
	}
	public void setP_wf_msgFrom(String p_wf_msgFrom) {
		this.p_wf_msgFrom = p_wf_msgFrom;
	}
	
	public String getP_wf_modiButton() {
		return p_wf_modiButton;
	}
	public void setP_wf_modiButton(String p_wf_modiButton) {
		this.p_wf_modiButton = p_wf_modiButton;
	}
	
	public String getP_wf_titleFieldName() {
		return p_wf_titleFieldName;
	}
	public void setP_wf_titleFieldName(String p_wf_titleFieldName) {
		this.p_wf_titleFieldName = p_wf_titleFieldName;
	}
	
	public String getP_wf_remindField() {
		return p_wf_remindField;
	}
	public void setP_wf_remindField(String p_wf_remindField) {
		this.p_wf_remindField = p_wf_remindField;
	}
	
	public String getP_wf_dataForm() {
		return p_wf_dataForm;
	}
	public void setP_wf_dataForm(String p_wf_dataForm) {
		this.p_wf_dataForm = p_wf_dataForm;
	}
	
	public String getP_wf_queryForm() {
		return p_wf_queryForm;
	}
	public void setP_wf_queryForm(String p_wf_queryForm) {
		this.p_wf_queryForm = p_wf_queryForm;
	}
	
	public String getP_wf_initPara() {
		return p_wf_initPara;
	}
	public void setP_wf_initPara(String p_wf_initPara) {
		this.p_wf_initPara = p_wf_initPara;
	}
	
	public String getP_wf_cur_ModifyField() {
		return p_wf_cur_ModifyField;
	}
	public void setP_wf_cur_ModifyField(String p_wf_cur_ModifyField) {
		this.p_wf_cur_ModifyField = p_wf_cur_ModifyField;
	}
	
	public String getP_wf_concealField() {
		return p_wf_concealField;
	}
	public void setP_wf_concealField(String p_wf_concealField) {
		this.p_wf_concealField = p_wf_concealField;
	}
	
	public String getP_wf_curCommField() {
		return p_wf_curCommField;
	}
	public void setP_wf_curCommField(String p_wf_curCommField) {
		this.p_wf_curCommField = p_wf_curCommField;
	}
	
	public String getP_wf_commFieldType() {
		return p_wf_commFieldType;
	}
	public void setP_wf_commFieldType(String p_wf_commFieldType) {
		this.p_wf_commFieldType = p_wf_commFieldType;
	}
	
	public String getP_wf_curPassRoundCommField() {
		return p_wf_curPassRoundCommField;
	}
	public void setP_wf_curPassRoundCommField(String p_wf_curPassRoundCommField) {
		this.p_wf_curPassRoundCommField = p_wf_curPassRoundCommField;
	}
	
	public String getP_wf_commentNotNull() {
		return p_wf_commentNotNull;
	}
	public void setP_wf_commentNotNull(String p_wf_commentNotNull) {
		this.p_wf_commentNotNull = p_wf_commentNotNull;
	}
	
	public String getP_wf_batchWorkIds() {
		return p_wf_batchWorkIds;
	}
	public void setP_wf_batchWorkIds(String p_wf_batchWorkIds) {
		this.p_wf_batchWorkIds = p_wf_batchWorkIds;
	}
	
	public String getP_wf_workStatus() {
		return p_wf_workStatus;
	}
	public void setP_wf_workStatus(String p_wf_workStatus) {
		this.p_wf_workStatus = p_wf_workStatus;
	}
	
	public String getP_wf_cur_activityId() {
		return p_wf_cur_activityId;
	}
	public void setP_wf_cur_activityId(String p_wf_cur_activityId) {
		this.p_wf_cur_activityId = p_wf_cur_activityId;
	}
	
	public String getP_wf_cur_activityName() {
		return p_wf_cur_activityName;
	}
	public void setP_wf_cur_activityName(String p_wf_cur_activityName) {
		this.p_wf_cur_activityName = p_wf_cur_activityName;
	}
	
	public String getP_wf_activityStep() {
		return p_wf_activityStep;
	}
	public void setP_wf_activityStep(String p_wf_activityStep) {
		this.p_wf_activityStep = p_wf_activityStep;
	}
	
	public String getP_wf_isProxy() {
		return p_wf_isProxy;
	}
	public void setP_wf_isProxy(String p_wf_isProxy) {
		this.p_wf_isProxy = p_wf_isProxy;
	}
	
	public String getP_wf_proxyAssignee() {
		return p_wf_proxyAssignee;
	}
	public void setP_wf_proxyAssignee(String p_wf_proxyAssignee) {
		this.p_wf_proxyAssignee = p_wf_proxyAssignee;
	}
	
	public String getP_wf_proxyAssigneeName() {
		return p_wf_proxyAssigneeName;
	}
	public void setP_wf_proxyAssigneeName(String p_wf_proxyAssigneeName) {
		this.p_wf_proxyAssigneeName = p_wf_proxyAssigneeName;
	}
	
	public String getP_wf_proxyTaskId() {
		return p_wf_proxyTaskId;
	}
	public void setP_wf_proxyTaskId(String p_wf_proxyTaskId) {
		this.p_wf_proxyTaskId = p_wf_proxyTaskId;
	}
	
	public String getP_wf_isTransfer() {
		return p_wf_isTransfer;
	}
	public void setP_wf_isTransfer(String p_wf_isTransfer) {
		this.p_wf_isTransfer = p_wf_isTransfer;
	}
	
	public String getP_wf_transferFromId() {
		return p_wf_transferFromId;
	}
	public void setP_wf_transferFromId(String p_wf_transferFromId) {
		this.p_wf_transferFromId = p_wf_transferFromId;
	}
	
	public String getP_wf_isbacktrackAct() {
		return p_wf_isbacktrackAct;
	}
	public void setP_wf_isbacktrackAct(String p_wf_isbacktrackAct) {
		this.p_wf_isbacktrackAct = p_wf_isbacktrackAct;
	}
	
	public String getP_wf_backtrackFromTaskId() {
		return p_wf_backtrackFromTaskId;
	}
	public void setP_wf_backtrackFromTaskId(String p_wf_backtrackFromTaskId) {
		this.p_wf_backtrackFromTaskId = p_wf_backtrackFromTaskId;
	}
	
	public String getP_wf_isneedSendtoBack() {
		return p_wf_isneedSendtoBack;
	}
	public void setP_wf_isneedSendtoBack(String p_wf_isneedSendtoBack) {
		this.p_wf_isneedSendtoBack = p_wf_isneedSendtoBack;
	}
	
	public String getP_wf_backFromTaskId() {
		return p_wf_backFromTaskId;
	}
	public void setP_wf_backFromTaskId(String p_wf_backFromTaskId) {
		this.p_wf_backFromTaskId = p_wf_backFromTaskId;
	}
	
	public String getP_wf_processallVariables() {
		return p_wf_processallVariables;
	}
	public void setP_wf_processallVariables(String p_wf_processallVariables) {
		this.p_wf_processallVariables = p_wf_processallVariables;
	}
	
	public String getP_wf_classname() {
		return p_wf_classname;
	}
	public void setP_wf_classname(String p_wf_classname) {
		this.p_wf_classname = p_wf_classname;
	}
	
	public String getP_wf_saveData() {
		return p_wf_saveData;
	}
	public void setP_wf_saveData(String p_wf_saveData) {
		this.p_wf_saveData = p_wf_saveData;
	}
	
	public String getP_wf_completeData() {
		return p_wf_completeData;
	}
	public void setP_wf_completeData(String p_wf_completeData) {
		this.p_wf_completeData = p_wf_completeData;
	}
	
	public String getP_wf_updateData() {
		return p_wf_updateData;
	}
	public void setP_wf_updateData(String p_wf_updateData) {
		this.p_wf_updateData = p_wf_updateData;
	}
	
	public String getP_wf_backData() {
		return p_wf_backData;
	}
	public void setP_wf_backData(String p_wf_backData) {
		this.p_wf_backData = p_wf_backData;
	}
	
	public String getP_wf_forminitJsFunName() {
		return p_wf_forminitJsFunName;
	}
	public void setP_wf_forminitJsFunName(String p_wf_forminitJsFunName) {
		this.p_wf_forminitJsFunName = p_wf_forminitJsFunName;
	}
	
	public String getP_wf_formsaveJsFunName() {
		return p_wf_formsaveJsFunName;
	}
	public void setP_wf_formsaveJsFunName(String p_wf_formsaveJsFunName) {
		this.p_wf_formsaveJsFunName = p_wf_formsaveJsFunName;
	}
	
	public String getP_wf_acti_forminitJsFunName() {
		return p_wf_acti_forminitJsFunName;
	}
	public void setP_wf_acti_forminitJsFunName(String p_wf_acti_forminitJsFunName) {
		this.p_wf_acti_forminitJsFunName = p_wf_acti_forminitJsFunName;
	}
	
	public String getP_wf_acti_formsaveJsFunName() {
		return p_wf_acti_formsaveJsFunName;
	}
	public void setP_wf_acti_formsaveJsFunName(String p_wf_acti_formsaveJsFunName) {
		this.p_wf_acti_formsaveJsFunName = p_wf_acti_formsaveJsFunName;
	}
	
	public String getP_wf_commentSortType() {
		return p_wf_commentSortType;
	}
	public void setP_wf_commentSortType(String p_wf_commentSortType) {
		this.p_wf_commentSortType = p_wf_commentSortType;
	}
	
	public String getP_wf_relationTrig() {
		return p_wf_relationTrig;
	}
	public void setP_wf_relationTrig(String p_wf_relationTrig) {
		this.p_wf_relationTrig = p_wf_relationTrig;
	}
	
	public String getP_wf_processNeedPrint() {
		return p_wf_processNeedPrint;
	}
	public void setP_wf_processNeedPrint(String p_wf_processNeedPrint) {
		this.p_wf_processNeedPrint = p_wf_processNeedPrint;
	}
	
	public String getP_wf_protectedField() {
		return p_wf_protectedField;
	}
	public void setP_wf_protectedField(String p_wf_protectedField) {
		this.p_wf_protectedField = p_wf_protectedField;
	}
	
	public String getP_wf_processKeepBackComment() {
		return p_wf_processKeepBackComment;
	}
	public void setP_wf_processKeepBackComment(String p_wf_processKeepBackComment) {
		this.p_wf_processKeepBackComment = p_wf_processKeepBackComment;
	}
	
	public String getP_wf_processCommentAcc() {
		return p_wf_processCommentAcc;
	}
	public void setP_wf_processCommentAcc(String p_wf_processCommentAcc) {
		this.p_wf_processCommentAcc = p_wf_processCommentAcc;
	}
	
	public String getP_wf_processNeedDossier() {
		return p_wf_processNeedDossier;
	}
	public void setP_wf_processNeedDossier(String p_wf_processNeedDossier) {
		this.p_wf_processNeedDossier = p_wf_processNeedDossier;
	}
	
	public String getP_wf_processNeedDossierType() {
		return p_wf_processNeedDossierType;
	}
	public void setP_wf_processNeedDossierType(String pWfProcessNeedDossierType) {
		p_wf_processNeedDossierType = pWfProcessNeedDossierType;
	}
	
	public String getP_wf_processNeedDossierPath() {
		return p_wf_processNeedDossierPath;
	}
	public void setP_wf_processNeedDossierPath(String pWfProcessNeedDossierPath) {
		p_wf_processNeedDossierPath = pWfProcessNeedDossierPath;
	}
	
	public String getP_wf_deleteReason() {
		return p_wf_deleteReason;
	}
	public void setP_wf_deleteReason(String p_wf_deleteReason) {
		this.p_wf_deleteReason = p_wf_deleteReason;
	}
	
	public String getP_wf_dealTips() {
		return p_wf_dealTips;
	}
	public void setP_wf_dealTips(String p_wf_dealTips) {
		this.p_wf_dealTips = p_wf_dealTips;
	}
	
	public String getP_wf_workId() {
		return p_wf_workId;
	}
	public void setP_wf_workId(String p_wf_workId) {
		this.p_wf_workId = p_wf_workId;
	}
	
	public String getP_wf_processType() {
		return p_wf_processType;
	}
	public void setP_wf_processType(String p_wf_processType) {
		this.p_wf_processType = p_wf_processType;
	}
	
	public String getP_wf_url() {
		return p_wf_url;
	}
	public void setP_wf_url(String p_wf_url) {
		this.p_wf_url = p_wf_url;
	}
	
	public String getP_wf_moduleId() {
		return p_wf_moduleId;
	}
	public void setP_wf_moduleId(String p_wf_moduleId) {
		this.p_wf_moduleId = p_wf_moduleId;
	}
	
	public String getP_wf_formType() {
		return p_wf_formType;
	}
	public void setP_wf_formType(String p_wf_formType) {
		this.p_wf_formType = p_wf_formType;
	}
	
	public String getP_wf_submitPerson() {
		return p_wf_submitPerson;
	}
	public void setP_wf_submitPerson(String p_wf_submitPerson) {
		this.p_wf_submitPerson = p_wf_submitPerson;
	}
	
	public String getP_wf_submitUserAccount() {
		return p_wf_submitUserAccount;
	}
	public void setP_wf_submitUserAccount(String p_wf_submitUserAccount) {
		this.p_wf_submitUserAccount = p_wf_submitUserAccount;
	}
	
	public String getP_wf_submitPersonId() {
		return p_wf_submitPersonId;
	}
	public void setP_wf_submitPersonId(String p_wf_submitPersonId) {
		this.p_wf_submitPersonId = p_wf_submitPersonId;
	}
	
	public String getP_wf_submitTime() {
		return p_wf_submitTime;
	}
	public void setP_wf_submitTime(String p_wf_submitTime) {
		this.p_wf_submitTime = p_wf_submitTime;
	}
	
	public String getP_wf_startOrgId() {
		return p_wf_startOrgId;
	}
	public void setP_wf_startOrgId(String p_wf_startOrgId) {
		this.p_wf_startOrgId = p_wf_startOrgId;
	}
	
	public String getP_wf_startOrgName() {
		return p_wf_startOrgName;
	}
	public void setP_wf_startOrgName(String p_wf_startOrgName) {
		this.p_wf_startOrgName = p_wf_startOrgName;
	}
	
	public String getP_wf_stepCount() {
		return p_wf_stepCount;
	}
	public void setP_wf_stepCount(String p_wf_stepCount) {
		this.p_wf_stepCount = p_wf_stepCount;
	}
	
	public String getPortletSettingId() {
		return portletSettingId;
	}
	public void setPortletSettingId(String portletSettingId) {
		this.portletSettingId = portletSettingId;
	}
	
	public String getFlushWithFun() {
		return flushWithFun;
	}
	public void setFlushWithFun(String flushWithFun) {
		this.flushWithFun = flushWithFun;
	}
	
	public String getNoNeedFlush() {
		return noNeedFlush;
	}
	public void setNoNeedFlush(String noNeedFlush) {
		this.noNeedFlush = noNeedFlush;
	}
	
	public String getP_wf_mainLinkFile() {
		return p_wf_mainLinkFile;
	}
	public void setP_wf_mainLinkFile(String p_wf_mainLinkFile) {
		this.p_wf_mainLinkFile = p_wf_mainLinkFile;
	}
	
	public String getP_wf_passLinkFile() {
		return p_wf_passLinkFile;
	}
	public void setP_wf_passLinkFile(String p_wf_passLinkFile) {
		this.p_wf_passLinkFile = p_wf_passLinkFile;
	}
	
	public String getP_wf_subProcHref() {
		return p_wf_subProcHref;
	}
	public void setP_wf_subProcHref(String p_wf_subProcHref) {
		this.p_wf_subProcHref = p_wf_subProcHref;
	}
	
	public String getP_wf_processDefinitionKey() {
		return p_wf_processDefinitionKey;
	}
	public void setP_wf_processDefinitionKey(String p_wf_processDefinitionKey) {
		this.p_wf_processDefinitionKey = p_wf_processDefinitionKey;
	}
	
	public String getP_wf_extbuttonSetIds() {
		return p_wf_extbuttonSetIds;
	}
	public void setP_wf_extbuttonSetIds(String p_wf_extbuttonSetIds) {
		this.p_wf_extbuttonSetIds = p_wf_extbuttonSetIds;
	}
	
    public String getP_wf_extButtonFunContent() {
		return p_wf_extButtonFunContent;
	}
	public void setP_wf_extButtonFunContent(String p_wf_extButtonFunContent) {
		this.p_wf_extButtonFunContent = p_wf_extButtonFunContent;
	}
    
	public String getP_wf_superProcess_formKey() {
		return p_wf_superProcess_formKey;
	}
	public void setP_wf_superProcess_formKey(String p_wf_superProcess_formKey) {
		this.p_wf_superProcess_formKey = p_wf_superProcess_formKey;
	}
	
	public String getP_wf_superProcess_recordId() {
		return p_wf_superProcess_recordId;
	}
	public void setP_wf_superProcess_recordId(String p_wf_superProcess_recordId) {
		this.p_wf_superProcess_recordId = p_wf_superProcess_recordId;
	}
    
	public String getParentFormId() {
		return parentFormId;
	}
	public void setParentFormId(String parentFormId) {
		this.parentFormId = parentFormId;
	}
	
	public String getParentRecordId() {
		return parentRecordId;
	}
	public void setParentRecordId(String parentRecordId) {
		this.parentRecordId = parentRecordId;
	}
	
	public String getP_wf_button_backRange() {
		return p_wf_button_backRange;
	}
	public void setP_wf_button_backRange(String p_wf_button_backRange) {
		this.p_wf_button_backRange = p_wf_button_backRange;
	}
	
	public String getWhir_commentRangeUsers() {
		return whir_commentRangeUsers;
	}
	public void setWhir_commentRangeUsers(String whir_commentRangeUsers) {
		this.whir_commentRangeUsers = whir_commentRangeUsers;
	}
	
	public String getWhir_commentRangeOrgs() {
		return whir_commentRangeOrgs;
	}
	public void setWhir_commentRangeOrgs(String whir_commentRangeOrgs) {
		this.whir_commentRangeOrgs = whir_commentRangeOrgs;
	}
	
	public String getWhir_commentRangeGroups() {
		return whir_commentRangeGroups;
	}
	public void setWhir_commentRangeGroups(String whir_commentRangeGroups) {
		this.whir_commentRangeGroups = whir_commentRangeGroups;
	}
    
	public String getP_wf_commentRangeByDealUser() {
		return p_wf_commentRangeByDealUser;
	}
	public void setP_wf_commentRangeByDealUser(String p_wf_commentRangeByDealUser) {
		this.p_wf_commentRangeByDealUser = p_wf_commentRangeByDealUser;
	}
	
	public String getWhir_commentRangeId() {
		return whir_commentRangeId;
	}
	public void setWhir_commentRangeId(String whir_commentRangeId) {
		this.whir_commentRangeId = whir_commentRangeId;
	}
	
	public String getP_wf_task_type() {
		return p_wf_task_type;
	}
	public void setP_wf_task_type(String p_wf_task_type) {
		this.p_wf_task_type = p_wf_task_type;
	}
    
	public String getP_wf_dealWithJob() {
		return p_wf_dealWithJob;
	}
	public void setP_wf_dealWithJob(String p_wf_dealWithJob) {
		this.p_wf_dealWithJob = p_wf_dealWithJob;
	}
    
	public String getP_wf_activityTip() {
		return p_wf_activityTip;
	}
	public void setP_wf_activityTip(String p_wf_activityTip) {
		this.p_wf_activityTip = p_wf_activityTip;
	}
	
	public String getP_wf_activityTipTitle() {
		return p_wf_activityTipTitle;
	}
	public void setP_wf_activityTipTitle(String p_wf_activityTipTitle) {
		this.p_wf_activityTipTitle = p_wf_activityTipTitle;
	}
	
	public String getP_wf_activityTipCotent() {
		return p_wf_activityTipCotent;
	}
	public void setP_wf_activityTipCotent(String p_wf_activityTipCotent) {
		this.p_wf_activityTipCotent = p_wf_activityTipCotent;
	}
	
	public String getP_wf_pool_formType() {
		return p_wf_pool_formType;
	}
	public void setP_wf_pool_formType(String p_wf_pool_formType) {
		this.p_wf_pool_formType = p_wf_pool_formType;
	}
	
	public String getP_wf_pool_processId() {
		return p_wf_pool_processId;
	}
	public void setP_wf_pool_processId(String p_wf_pool_processId) {
		this.p_wf_pool_processId = p_wf_pool_processId;
	}
	
	public String getP_wf_pool_processType() {
		return p_wf_pool_processType;
	}
	public void setP_wf_pool_processType(String p_wf_pool_processType) {
		this.p_wf_pool_processType = p_wf_pool_processType;
	}
	
	public String getP_wf_orgcommentSortType() {
		return p_wf_orgcommentSortType;
	}
	public void setP_wf_orgcommentSortType(String pWfOrgcommentSortType) {
		p_wf_orgcommentSortType = pWfOrgcommentSortType;
	}
	
	public String getP_wf_processKeepReSubmitComment() {
		return p_wf_processKeepReSubmitComment;
	}
	public void setP_wf_processKeepReSubmitComment(String pWfProcessKeepReSubmitComment) {
		p_wf_processKeepReSubmitComment = pWfProcessKeepReSubmitComment;
	}
	
	public String getP_wf_subTableDataControlAdd() {
		return p_wf_subTableDataControlAdd;
	}
	public void setP_wf_subTableDataControlAdd(String pWfSubTableDataControlAdd) {
		p_wf_subTableDataControlAdd = pWfSubTableDataControlAdd;
	}
	
	public String getP_wf_subTableDataControlAddNull() {
		return p_wf_subTableDataControlAddNull;
	}
	public void setP_wf_subTableDataControlAddNull(
			String pWfSubTableDataControlAddNull) {
		p_wf_subTableDataControlAddNull = pWfSubTableDataControlAddNull;
	}
	
	public String getP_wf_subTableDataControlModi() {
		return p_wf_subTableDataControlModi;
	}
	public void setP_wf_subTableDataControlModi(String pWfSubTableDataControlModi) {
		p_wf_subTableDataControlModi = pWfSubTableDataControlModi;
	}
	
	public String getP_wf_subTableDataControlDel() {
		return p_wf_subTableDataControlDel;
	}
	public void setP_wf_subTableDataControlDel(String pWfSubTableDataControlDel) {
		p_wf_subTableDataControlDel = pWfSubTableDataControlDel;
	}
	
	/**
     * 初始化流程定义key    
     */
	/*protected void initProcessDefinitionKey(){	
		logger.debug("p_wf_processId:"+p_wf_processId);
		//流程定义key
		String processDefinitionKey="";
		if(p_wf_processId!=null&&!p_wf_processId.equals("")){
		     processDefinitionKey=p_wf_processId.substring(0,p_wf_processId.lastIndexOf(":"));
			 processDefinitionKey=processDefinitionKey.substring(0,processDefinitionKey.lastIndexOf(":"));
		}	
        this.p_wf_processDefinitionKey=processDefinitionKey;
        
        
        logger.debug("in  initProcessDefinitionKey   p_wf_processDefinitionKey:"+p_wf_processDefinitionKey);
        
        logger.debug("in  initProcessDefinitionKey   p_wf_pool_processId1:"+p_wf_pool_processId);
        if(p_wf_pool_processId==null||p_wf_pool_processId.equals("null")||p_wf_pool_processId.equals("")){
	        BPMProcessBD bd=new BPMProcessBD();
	        BPMProcessPO poolPO=bd.loadBPMProcessPOByEzFlowKey(p_wf_processDefinitionKey);
	        this.p_wf_moduleId=""+poolPO.getPoolModuleId();
	        this.p_wf_pool_formType=""+poolPO.getPoolFormType();
	        this.p_wf_pool_processId=""+poolPO.getPoolProcessId();
	        this.p_wf_pool_processType=""+poolPO.getPoolProcessType();
	        
	        this.p_wf_tableId=poolPO.getPoolProcessFormId()==null?"":poolPO.getPoolProcessFormId().toString();
        }
        
        logger.debug("in  initProcessDefinitionKey   p_wf_pool_processId:"+p_wf_pool_processId);
        logger.debug("in  initProcessDefinitionKey   p_wf_moduleId:"+p_wf_moduleId);
        logger.debug("in  initProcessDefinitionKey   p_wf_pool_formType:"+p_wf_pool_formType);
        
        logger.debug("in  initProcessDefinitionKey   p_wf_pool_processId:"+p_wf_pool_processId);
        logger.debug("in  initProcessDefinitionKey   p_wf_pool_processType:"+p_wf_pool_processType);
	}*/
	
	public String getP_wf_mailviewUrl() {
		return p_wf_mailviewUrl;
	}
	public void setP_wf_mailviewUrl(String p_wf_mailviewUrl) {
		this.p_wf_mailviewUrl = p_wf_mailviewUrl;
	}
	
	public String getP_wf_commentAttitudeTypeSet() {
		return p_wf_commentAttitudeTypeSet;
	}
	public void setP_wf_commentAttitudeTypeSet(String pWfCommentAttitudeTypeSet) {
		p_wf_commentAttitudeTypeSet = pWfCommentAttitudeTypeSet;
	}
	
	public String getP_wf_whir_dealedActInfo() {
		return p_wf_whir_dealedActInfo;
	}
	public void setP_wf_whir_dealedActInfo(String pWfWhirDealedActInfo) {
		p_wf_whir_dealedActInfo = pWfWhirDealedActInfo;
	}
	
	
	public String getP_wf_activityclassType() {
		return p_wf_activityclassType;
	}
	public void setP_wf_activityclassType(String pWfActivityclassType) {
		p_wf_activityclassType = pWfActivityclassType;
	}
	public String getP_wf_subactivitytype() {
		return p_wf_subactivitytype;
	}
	public void setP_wf_subactivitytype(String pWfSubactivitytype) {
		p_wf_subactivitytype = pWfSubactivitytype;
	}
	public String getP_wf_extendMainTable() {
		return p_wf_extendMainTable;
	}
	public void setP_wf_extendMainTable(String pWfExtendMainTable) {
		p_wf_extendMainTable = pWfExtendMainTable;
	}
	public String getP_wf_subactivityIds() {
		return p_wf_subactivityIds;
	}
	public void setP_wf_subactivityIds(String pWfSubactivityIds) {
		p_wf_subactivityIds = pWfSubactivityIds;
	}
	/**
    * 初始化流程定义key    
    */
	protected void initProcessDefinitionKey(){	
		logger.debug("p_wf_processId:"+p_wf_processId);
		//流程定义key
		String processDefinitionKey="";
		if(p_wf_processId!=null&&!p_wf_processId.equals("")){
		     processDefinitionKey=p_wf_processId.substring(0,p_wf_processId.lastIndexOf(":"));
			 processDefinitionKey=processDefinitionKey.substring(0,processDefinitionKey.lastIndexOf(":"));
		}	
        this.p_wf_processDefinitionKey=processDefinitionKey;
        
        //
        if(p_wf_pool_processId==null||p_wf_pool_processId.equals("null")||p_wf_pool_processId.equals("")){
	        BPMProcessBD bd=new BPMProcessBD();
	        BPMProcessPO poolPO=bd.loadBPMProcessPOByEzFlowKey(p_wf_processDefinitionKey);
	        this.p_wf_moduleId=""+poolPO.getPoolModuleId();
	        this.p_wf_pool_formType=""+poolPO.getPoolFormType();
	        this.p_wf_pool_processId=""+poolPO.getPoolProcessId();
	        this.p_wf_pool_processType=""+poolPO.getPoolProcessType();
	        //
	        this.p_wf_tableId=poolPO.getPoolProcessFormId()==null?"":poolPO.getPoolProcessFormId().toString();
        }else if(p_wf_tableId==null||p_wf_tableId.equals("null")){
        	BPMProcessBD bd=new BPMProcessBD();
			BPMProcessPO po=bd.loadBPMProcessPO(new Long(p_wf_pool_processId));
		    this.p_wf_pool_processType=""+po.getPoolProcessType(); 
		    this.p_wf_pool_formType=""+po.getPoolFormType(); 
		    //ezFLOW
		    if(this.p_wf_pool_processType.equals("1")){
		    	this.p_wf_processDefinitionKey=po.getPoolEzFlowProcessKey(); 
		    	this.p_wf_tableId=""+po.getPoolProcessFormId();
		    }else{//老流程  wuyong 
		    	this.p_wf_processId=""+po.getPoolOldProcessId();
				this.p_wf_tableId=""+po.getPoolOldFormId();			 
		    }
        }
	}

	protected void initActFromKey(){
		//活动表单code
		if(p_wf_formKey_act==null||p_wf_formKey_act.equals("null")||p_wf_formKey_act.equals("")){
			p_wf_formKey_act=p_wf_formKey;
		}
	}
	
	protected void initField() {
        //---------------------处理批示意见字段------
		if (p_wf_openType.equals("waitingDeal")) {
		} else if (p_wf_openType.equals("waitingRead")) {
			p_wf_curCommField = p_wf_curPassRoundCommField;
		} else {
			p_wf_curCommField = "nullCommentField";
		}
		if (p_wf_curCommField == null) {
			p_wf_curCommField = "nullCommentField";
		}

		if (p_wf_curCommField.endsWith(",")) {
			p_wf_curCommField = p_wf_curCommField.substring(0, p_wf_curCommField.length() - 1);
		}
		if (p_wf_curCommField.startsWith(",")) {
			p_wf_curCommField = p_wf_curCommField.substring(1);
		}

		if (p_wf_curCommField.equals("")) {
			p_wf_curCommField = "nullCommentField";
		} 
		if (true||(!this.p_wf_openType.equals("reStart")&& !this.p_wf_openType.equals("startAgain"))) {
			// ---------------------处理保护字段------------
			// 保护字段
			String protectField_w = ""; // protectField_w += objP[1].toString()+"="+objP[2].toString()+";";
			String whir_protectedField = p_wf_protectedField;
			if (whir_protectedField != null && !whir_protectedField.equals("")
					&& !whir_protectedField.equals("null")) {
				whir_protectedField = whir_protectedField.substring(1,whir_protectedField.length() - 1);
				whir_protectedField = whir_protectedField.replaceAll(",,", ",");
				String whir_protectedFieldArr[] = whir_protectedField.split(",");
				for (int pi = 0; pi < whir_protectedFieldArr.length; pi++) {
					protectField_w += whir_protectedFieldArr[pi] + "=" + whir_protectedFieldArr[pi] + ";";
				}
			} 
			if ("".equals(protectField_w)) {
				protectField_w = "recordId=标识;";
			}
			p_wf_protectedField = protectField_w;
			
			// --------------取批示意见-----------------------------------
			// 批示意见排序方式
			/*
			 * 批示意见排序方式 time_asc 按时间顺序 time_desc按时间倒序 dute_asc按职务顺序
			 * dute_desc按职务倒序
			 */
			String whir_commentSortType = p_wf_commentSortType;
			if (whir_commentSortType == null 
					|| whir_commentSortType.equals("")
					|| whir_commentSortType.equals("null")) {
				whir_commentSortType = "time_desc";
			}

			EzFlowCommentService commentService = new EzFlowCommentService();
			Map map = new HashMap();
			HttpSession session = request.getSession();
			map.put("userId", session.getAttribute("userId") + "");
			map.put("orgIdString", session.getAttribute("orgIdString") + "");
			map.put("processInstanceId", p_wf_processInstanceId);

			String whir_commentSortType_temp = whir_commentSortType;
			if (whir_commentSortType_temp.equals("dute_asc")) {
				whir_commentSortType = "dute_desc";
			}
			if (whir_commentSortType_temp.equals("dute_desc")) {
				whir_commentSortType = "dute_asc";
			}

			map.put("orderBy", whir_commentSortType);
			
			map.put("orgorderBy", p_wf_orgcommentSortType);

			// 批示意见 内容列表
			List commentList_w =new ArrayList();
			if(EzFlowUtil.judgeNull(p_wf_processInstanceId)){
				commentList_w=commentService.list(map);
			}
			request.setAttribute("commentList_w", commentList_w);

			// -----------------处理草稿-----------------------------------

			// 当前办理人 当前任务的草稿批示意见
			String curDraftContent = "";
			String curDraftAccName = "";
			String curDraftAccSName = "";
			if (commentList_w != null && commentList_w.size() > 0) {
				for (int k = 0; k < commentList_w.size(); k++) {
					WhirEzFlowCommentEntity cEntity = (WhirEzFlowCommentEntity) commentList_w.get(k);
					String now_activityId = cEntity.getActivityId();
					String dealUserId = cEntity.getDealUserId();
					String dealUserName = cEntity.getDealUserName();
					String dealContent = cEntity.getDealContent() == null ? "": cEntity.getDealContent();
					Date dealTime = cEntity.getDealTime();

					String commentField = cEntity.getCommentField();

					// 批示意见类型。 0： 普通，1：手写签名 2：电子签章 3 附件
					String commentType = cEntity.getCommentType();
					int isStandFor = 0;
					String standForUserId = "";
					String standForUserName = "";
					String recordId = cEntity.getRecordId();

					// 处理类型 是办件 还是阅件
					String commentDealType = "";
					// //默认1 表示正常 0：表示是草稿
					int commentStatus = cEntity.getCommentStatus();

					// 附件
					String commentAccDisName = cEntity.getAccDisName();
					String commentAccSaveName = cEntity.getAccSaveName();
					// 如果是草稿 并且是本人的 并且 是当前活动的 并且批示意见字段相同 并且是普通类型
					if (now_activityId.equals(this.p_wf_cur_activityId)
							&& commentStatus == 0
							&& dealUserId.equals(curUserInfoVO.getUserId())
							&& commentField.equals(p_wf_curCommField)
							&& commentType.equals("0")) {
						curDraftContent = dealContent;
						if (commentAccSaveName != null&& !commentAccSaveName.equals("null")) {
							curDraftAccName = commentAccDisName;
							curDraftAccSName = commentAccSaveName;
//							if (curDraftAccName != null) {
//								try {
//									curDraftAccName = java.net.URLEncoder.encode(curDraftAccName, "UTF-8");
//								} catch (UnsupportedEncodingException e) {
//									e.printStackTrace();
//								}
//							}
						}
						break;
					}
					// 如果不属于此td 活动的批示意见 以及 是无批示意见字段 以及草稿
					/*
					 * if(!activityId.equals(now_activityId) ||
					 * "nullCommentField"
					 * .equals(commentField)||commentStatus==0){ continue; }
					 */
				}
			}
			request.setAttribute("curDraftContent", curDraftContent);
			request.setAttribute("curDraftAccName", curDraftAccName);
			request.setAttribute("curDraftAccSName", curDraftAccSName);
		}
	}
	
	/**
	 * 转化 字段格式
	 */
	protected void  transformField(){
		if(this.p_wf_pool_formType.equals("2")){
			dealFieldFormType2();
		}else{
			//写字段
			if(p_wf_cur_ModifyField!=null){
				p_wf_cur_ModifyField=p_wf_cur_ModifyField.replaceAll(",", "\\$");
			}
			//隐藏字段
			if(p_wf_concealField!=null){
				p_wf_concealField=p_wf_concealField.replaceAll(",", "\\$");
			}
		}
	}
	
	
    protected void  dealFieldFormType2(){
    	BPMFormInfoBD   bpmFormInfoBD=new BPMFormInfoBD();
	    List list=bpmFormInfoBD.getFixedFieldByFormCode(p_wf_formKey); 
    	if(p_wf_cur_ModifyField!=null&&p_wf_cur_ModifyField.length()>2){
    		 int listsize=list.size();
    		 Object []obj=null;
    		 String p_wf_cur_ModifyField_real="";
    		 String  p_wf_cur_ModifyField_=p_wf_cur_ModifyField.substring(1,p_wf_cur_ModifyField.length()-1);
    		 p_wf_cur_ModifyField_=p_wf_cur_ModifyField_.replaceAll(",,", ",");
    		 String p_wf_cur_ModifyFieldArr[]=p_wf_cur_ModifyField_.split(",");
    	     for(String str:p_wf_cur_ModifyFieldArr){
    	    	 for(int i=0;i<listsize;i++){
    	    		 obj=(Object[])list.get(i);
    	    		 if(str.equals(""+obj[0])){
    	    			 p_wf_cur_ModifyField_real+="$"+obj[5]+"$";
    	    		 }
    	    	 }
    	     }
    	     p_wf_cur_ModifyField= p_wf_cur_ModifyField_real;
    	}
    	
		if (p_wf_concealField != null && p_wf_concealField.length() > 2) {
			int listsize = list.size();
			Object[] obj = null;
			String p_wf_cur_ModifyField_real = "";
			String p_wf_cur_ModifyField_ = p_wf_concealField.substring(1,p_wf_concealField.length() - 1);
			p_wf_cur_ModifyField_ = p_wf_cur_ModifyField_.replaceAll(",,", ",");
			String p_wf_cur_ModifyFieldArr[] = p_wf_cur_ModifyField_.split(",");
			for (String str : p_wf_cur_ModifyFieldArr) {
				for (int i = 0; i < listsize; i++) {
					obj = (Object[]) list.get(i);
					if (str.equals("" + obj[0])) {
						p_wf_cur_ModifyField_real += "$" + obj[5] + "$";
					}
				}
			}
			p_wf_concealField = p_wf_cur_ModifyField_real;
		}
    }
	
	/**
	 * 
	 */
	protected void debuglog(){
		logger.debug("开始debug信息");
		StringBuffer  logBuffer=new StringBuffer();
		logBuffer.append("p_wf_processId:").append(p_wf_processId).append("\n");// 流程的定义id
		logBuffer.append("p_wf_processDefinitionKey:").append(p_wf_processDefinitionKey).append("\n");
	 
		logBuffer.append("p_wf_processInstanceId:").append(p_wf_processInstanceId).append("\n");
		logBuffer.append("p_wf_superProcessInstanceId:").append(p_wf_superProcessInstanceId).append("\n");

		logBuffer.append("p_wf_taskId:").append(p_wf_taskId).append("\n");
		logBuffer.append("p_wf_processName:").append(p_wf_processName).append("\n");
		logBuffer.append("p_wf_openType:").append(p_wf_openType).append("\n");
		
		logBuffer.append("p_wf_formId:").append(p_wf_formId).append("\n");
		logBuffer.append("p_wf_tableId:").append(p_wf_tableId).append("\n");
		logBuffer.append("p_wf_formKey:").append(p_wf_formKey).append("\n");
		logBuffer.append("p_wf_formKey_act:").append(p_wf_formKey_act).append("\n");
		logBuffer.append("p_wf_msgFrom:").append(p_wf_msgFrom).append("\n");
		logBuffer.append("p_wf_modiButton:").append(p_wf_modiButton).append("\n");
		
		logBuffer.append("p_wf_titleFieldName:").append(p_wf_titleFieldName).append("\n");
		logBuffer.append("p_wf_remindField:").append(p_wf_remindField).append("\n");
		logBuffer.append("p_wf_dataForm:").append(p_wf_dataForm).append("\n");
	 
		
		logBuffer.append("p_wf_queryForm:").append(p_wf_queryForm).append("\n");
		logBuffer.append("p_wf_initPara:").append(p_wf_initPara).append("\n");
		logBuffer.append("p_wf_cur_ModifyField:").append(p_wf_cur_ModifyField).append("\n");
		logBuffer.append("p_wf_concealField:").append(p_wf_concealField).append("\n");
		logBuffer.append("p_wf_curCommField:").append(p_wf_curCommField).append("\n");
		
		logBuffer.append("p_wf_commFieldType:").append(p_wf_commFieldType).append("\n");
		logBuffer.append("p_wf_curPassRoundCommField:").append(p_wf_curPassRoundCommField).append("\n");
		logBuffer.append("p_wf_commentNotNull:").append(p_wf_commentNotNull).append("\n");
		logBuffer.append("p_wf_batchWorkIds:").append(p_wf_batchWorkIds).append("\n");
		
		logBuffer.append("p_wf_workStatus:").append(p_wf_workStatus).append("\n");
		logBuffer.append("p_wf_cur_activityId:").append(p_wf_cur_activityId).append("\n");
		logBuffer.append("p_wf_cur_activityName:").append(p_wf_cur_activityName).append("\n");
		logBuffer.append("p_wf_activityStep:").append(p_wf_activityStep).append("\n");
		
		logBuffer.append("p_wf_isProxy:").append(p_wf_isProxy).append("\n");
		logBuffer.append("p_wf_proxyAssignee:").append(p_wf_proxyAssignee).append("\n");
		logBuffer.append("p_wf_proxyAssigneeName:").append(p_wf_proxyAssigneeName).append("\n");
		logBuffer.append("p_wf_proxyTaskId:").append(p_wf_proxyTaskId).append("\n");
		logBuffer.append("p_wf_isTransfer:").append(p_wf_isTransfer).append("\n");
		logBuffer.append("p_wf_transferFromId:").append(p_wf_transferFromId).append("\n");
		logBuffer.append("p_wf_isbacktrackAct:").append(p_wf_isbacktrackAct).append("\n");
		
		logBuffer.append("p_wf_backtrackFromTaskId:").append(p_wf_backtrackFromTaskId).append("\n");
		
		logBuffer.append("p_wf_isneedSendtoBack:").append(p_wf_isneedSendtoBack).append("\n");
		
		logBuffer.append("p_wf_backFromTaskId:").append(p_wf_backFromTaskId).append("\n");
		logBuffer.append("p_wf_processallVariables:").append(p_wf_processallVariables).append("\n");
		logBuffer.append("p_wf_classname:").append(p_wf_classname).append("\n");
		logBuffer.append("p_wf_saveData:").append(p_wf_saveData).append("\n");
		logBuffer.append("p_wf_completeData:").append(p_wf_completeData).append("\n");
		logBuffer.append("p_wf_updateData:").append(p_wf_updateData).append("\n");
		logBuffer.append("p_wf_backData:").append(p_wf_backData).append("\n");
		logBuffer.append("p_wf_forminitJsFunName:").append(p_wf_forminitJsFunName).append("\n");
		
		logBuffer.append("p_wf_formsaveJsFunName:").append(p_wf_formsaveJsFunName).append("\n");
		logBuffer.append("p_wf_acti_forminitJsFunName:").append(p_wf_acti_forminitJsFunName).append("\n");
		logBuffer.append("p_wf_acti_formsaveJsFunName:").append(p_wf_acti_formsaveJsFunName).append("\n");
		logBuffer.append("p_wf_commentSortType:").append(p_wf_commentSortType).append("\n");
		
		logBuffer.append("p_wf_relationTrig:").append(p_wf_relationTrig).append("\n");
		logBuffer.append("p_wf_processNeedPrint:").append(p_wf_processNeedPrint).append("\n");
		logBuffer.append("p_wf_protectedField:").append(p_wf_protectedField).append("\n");
		logBuffer.append("p_wf_processKeepBackComment:").append(p_wf_processKeepBackComment).append("\n");
		
		logBuffer.append("p_wf_processCommentAcc:").append(p_wf_processCommentAcc).append("\n");
		logBuffer.append("p_wf_processNeedDossier:").append(p_wf_processNeedDossier).append("\n");
		logBuffer.append("p_wf_deleteReason:").append(p_wf_deleteReason).append("\n");
		logBuffer.append("p_wf_dealTips:").append(p_wf_dealTips).append("\n");
		logger.debug(logBuffer.toString());
  
		logger.debug("结束debug信息");
	}
	
	protected void setUpdateButton(List whir_taskButtons) {
		String buttonIds = "";
		String openType = this.p_wf_openType;
		EzFlowMainService  ezFlowMainService=new EzFlowMainService();

		// 总个可以有的按钮
		String buttonSetIds = "";
		// 总个可以有的自定义按钮
		String extbuttonSetIds = "";
		if (whir_taskButtons != null) {
			Map curButtonMap = null;
			String tempbuttonId = "";
			for (int i = 0; i < whir_taskButtons.size(); i++) {
				curButtonMap = (Map) whir_taskButtons.get(i);
				tempbuttonId = "" + curButtonMap.get("id");				
	            //退回按钮的范围
			    if(tempbuttonId.equals("EzFlowBackTask")){
			    	this.p_wf_button_backRange=""+curButtonMap.get("range");
			    } 
				if (tempbuttonId.startsWith("EXT_")) {
					extbuttonSetIds += tempbuttonId + ",";
				} else {
					buttonSetIds += "," + tempbuttonId + ",";
				}
			}
		}
		
		if(extbuttonSetIds!=null&&extbuttonSetIds.endsWith(",")){
			extbuttonSetIds = extbuttonSetIds.substring(0, extbuttonSetIds.length() - 1);
		}
		this.p_wf_extbuttonSetIds=extbuttonSetIds; 
		
		logger.debug("extbuttonSetIds:"+extbuttonSetIds);
		logger.debug("buttonSetIds:"+buttonSetIds);
		logger.debug("openType:"+openType);
		
		// 待办文件
		if (openType.equals("waitingDeal")) {
			buttonIds += "CompleteTask,";
			if (buttonSetIds.indexOf("EzFlowBackTask") >= 0) {
				//buttonIds += "EzFlowBackTask,";
				buttonIds += "Back,";
			}
			if (buttonSetIds.indexOf("EzFlowAddSignTask") >= 0) {
				//buttonIds += "EzFlowAddSignTask,";
				buttonIds += "AddSign,";
			}
			if (buttonSetIds.indexOf("EzFlowSendReadTask") >= 0) {
				//buttonIds += "EzFlowSendReadTask,";
				buttonIds += "Selfsend,";
			}
			if (buttonSetIds.indexOf("EzFlowAbandon") >= 0) {
				//buttonIds += "EzFlowAbandon,";
				buttonIds += "Delete,";
			}
			if (buttonSetIds.indexOf("EzFlowTransfer") >= 0) {
				//buttonIds += "EzFlowTransfer,";
				buttonIds += "Tran,";
			}
			// 打印
			if (buttonSetIds.indexOf("EzFlowPrint") >= 0) {
				//buttonIds += "EzFlowPrint,";
				buttonIds += "Print,";
			}
			// 新建流程
			if (buttonSetIds.indexOf("EzFlowNewProcess") >= 0) {
				//buttonIds += "EzFlowNewProcess,";
				buttonIds += "AddNew,";
			}
			// 相关流程
			// if(true){
			if (buttonSetIds.indexOf("EzFlowRelationProcess") >= 0) {
				//buttonIds += "EzFlowRelationProcess,";
				buttonIds += "Relation,";
			}
			
			//公文按钮---------------------
			//查看正文
    		if(buttonSetIds.indexOf("Viewtext")!=-1){
    			buttonIds += "Viewtext,";
    		}
    		//查看附件
    		if(buttonSetIds.indexOf("Viewacc")!=-1){
    			buttonIds += "Viewacc,";
    		}
    		//打印阅办单
    		if(buttonSetIds.indexOf("Printcomm")!=-1){
    			buttonIds += "Printcomm,";
    		}
    		//打印正文
    		if(buttonSetIds.indexOf("Printtext")!=-1){
    			buttonIds += "Printtext,";
    		}
    		//附件管理
    		if(buttonSetIds.indexOf("Manageracc")!=-1){
    			//modiButton += ",Manageracc";
    			buttonIds = buttonIds.replaceAll("Viewacc,","");
    		}
    		//归档
    		if(buttonSetIds.indexOf("Document")!=-1){
    			buttonIds += "Document,";
    		}
    		//正文管理
    		if(buttonSetIds.indexOf("Textmanager")!=-1){
    			buttonIds += "Textmanager,";
    		}
    		//批阅正文
    		if(buttonSetIds.indexOf("Readtext")!=-1){
    			buttonIds += "Readtext,";
    			buttonIds = buttonIds.replaceAll("Viewtext,","");
    		}
    		//生成正式文件
    		if(buttonSetIds.indexOf("Savefile")!=-1){
    			buttonIds += "Savefile,";
    			buttonIds = buttonIds.replaceAll("Viewtext,","");
    			//modiButton = modiButton.replaceAll(",Readtext","");
    		}
    		//编号
    		if(buttonSetIds.indexOf("Code")!=-1){
    			buttonIds += "Code,";
    		}
    		//转本部门
    		if(buttonSetIds.indexOf("Toselfdept")!=-1){
    			buttonIds += "Toselfdept,";
    		}
    		//分发
    		if(buttonSetIds.indexOf("Sendclose")!=-1){
    			buttonIds += "Sendclose,";
    		}
    		
    		//督办任务
    		if(buttonSetIds.indexOf("GovUnionTask")!=-1){
    			buttonIds += "GovUnionTask,";
    		}
    		
    		//再次发送
    		if(buttonSetIds.indexOf("ReSavefile")!=-1){
    			buttonIds += "ReSavefile,";
    		}
    		
    		//公文按钮---------------------end
    		
			// 保存退出
			// if(buttonSetIds.indexOf("EzFlowSaveClose")>=0){
			if (true) {
				//buttonIds += "EzFlowSaveClose,";
				buttonIds += "Saveclose,";
			}
			
			//强制结束
			if(buttonSetIds.indexOf("EzFlowCompulsoryEnd")!=-1){
				buttonIds += "CompulsoryEnd,";
			}
		}

		// 已办文件
		if (openType.equals("dealed")) {
			/*
			 * //打印 if(buttonSetIds.indexOf("EzFlowPrint")>=0){
			 * buttonIds+="EzFlowPrint,"; }
			 */
			// 发起人可以打印
			if (p_wf_processNeedPrint.equals("true")||buttonSetIds.indexOf("EzFlowPrint") >= 0) {
				//buttonIds += "EzFlowPrint,";
				buttonIds += "Print,";
			}
			// 新建流程
			if (buttonSetIds.indexOf("EzFlowNewProcess") >= 0) {
				//buttonIds += "EzFlowNewProcess,";
				buttonIds += "AddNew,";
			}
 		    
			//办理中才能收回
			if (p_wf_workStatus.equals("1")) {
				// 收回
				if (buttonSetIds.indexOf("EzFlowDrwaBack") >= 0) {
					//buttonIds += "EzFlowDrwaBack,";
					buttonIds += "Return,";
				}
			}
			
			//发文
			if("2".equals(p_wf_moduleId)){
				//modiButton +=  ",Viewtext,Viewacc,Printcomm,Printtext,Viewread";
				buttonIds +=  "Viewtext,Viewacc,Printcomm,Printtext,";
			}
			//收文
			if("3".equals(p_wf_moduleId)){
				//modiButton +=  ",Viewtext,Viewacc,Printcomm,Viewread";
				buttonIds +=  "Viewacc,Printcomm,";
			}
			//文件送审签
			if("34".equals(p_wf_moduleId)){
				buttonIds +=  "Viewtext,Viewacc,";
			}	

			// 在办才显示
			if (p_wf_workStatus.equals("1")) {
				if (buttonSetIds.indexOf("EzFlowReCall") >= 0) {
					logger.debug("p_wf_processInstanceId:"+p_wf_processInstanceId);
					logger.debug("p_wf_taskId:"+p_wf_taskId);
					// 判断是否能撤办
					String judgeRecallResult = ezFlowMainService.judgeRecall(
							new UserInfoVO("", "" + this.curUserInfoVO.getUserName(), "" + this.curUserInfoVO.getUserAccount()),
							p_wf_processInstanceId, p_wf_taskId);
					// 撤办
					if (judgeRecallResult != null && judgeRecallResult.equals("true")) {
						//buttonIds += "EzFlowReCall,";
						buttonIds += "Undo,";
					}
				}
				// 反馈
				if (buttonSetIds.indexOf("EzFlowFeedback") >= 0) {
					//buttonIds += "EzFlowFeedback,";
					buttonIds += "Feedback,";
				}
				// 催办
				if (buttonSetIds.indexOf("EzFlowPress") >= 0) {
					//buttonIds += "EzFlowPress,";
					buttonIds += "Wait,";
				}
				
				// 补签
				if (p_wf_workStatus.equals("1")&&buttonSetIds.indexOf("EdAddSign") >= 0) {
					//buttonIds += "EzFlowPress,";
					buttonIds += "EdAddSign,";
				}
				
				//转办撤办
				if (p_wf_workStatus.equals("1")&&buttonSetIds.indexOf("CancelTran") >= 0) {
					EzFlowTaskService  ezTService=new EzFlowTaskService();
					//buttonIds += "EzFlowPress,";
					if(ezTService.judgeCanCancelTran(this.p_wf_taskId)){
				      	buttonIds += "CancelTran,";
					}
				}
			}
		}

		// 我的文件
		if (openType.equals("myTask")) {
			// 取消
			if (buttonSetIds.indexOf("EzFlowCancel") >= 0) {
				//buttonIds += "EzFlowCancel,";
				buttonIds += "Cancel,";
			}
			// 发起人可以打印
			if (p_wf_processNeedPrint.equals("true")||buttonSetIds.indexOf("EzFlowPrint") >= 0) {
				//buttonIds += "EzFlowPrint,";
				buttonIds += "Print,";
			}
			// 催办
			if (buttonSetIds.indexOf("EzFlowPress") >= 0) {
				//buttonIds += "EzFlowPress,";
				buttonIds += "Wait,";
			}
			
			// 新建流程
			if (buttonSetIds.indexOf("EzFlowNewProcess") >= 0) {
				//buttonIds += "EzFlowNewProcess,";
				buttonIds += "AddNew,";
			}
			
			if("2".equals(p_wf_moduleId)){
				buttonIds +=  "Viewtext,Viewacc,Printcomm,Printtext,";
			}
			//收文
			if( "3".equals(p_wf_moduleId)){
				buttonIds +=  "Viewacc,Printcomm,";
			}
			
			//文件送审签
			if("34".equals(p_wf_moduleId)){
				buttonIds +=  "Viewtext,Viewacc,";
			}
		}
		// 待阅文件
		if (openType.equals("waitingRead")) {
			// 办理任务
			buttonIds += "CompleteRead,";
			// 转阅
			if (buttonSetIds.indexOf("EzFlowTranRead") >= 0) {
				//buttonIds += "EzFlowTranRead,";
				buttonIds += "TranRead,";
			}
			// 打印
			/*
			 * //打印 if(buttonSetIds.indexOf("EzFlowPrint")>=0){
			 * buttonIds+="EzFlowPrint,"; }
			 */

			// 发起人可以打印
			if (p_wf_processNeedPrint.equals("true")||buttonSetIds.indexOf("EzFlowPrint") >= 0) {
				//buttonIds += "EzFlowPrint,";
				buttonIds += "Print,";
			}
		}
		// 已阅文件
		if (openType.equals("readed")) {
			
			//发文
    		if("2".equals(p_wf_moduleId)){
    			buttonIds +=  "Viewtext,Viewacc,Printcomm,Printtext,";
    		}
    		//收文
    		if( "3".equals(p_wf_moduleId)){
    			buttonIds +=  "Viewacc,Printcomm,Printtext,";
    		}
    		//文件送审签
    		if("34".equals(p_wf_moduleId)){
    			buttonIds +=  "Viewtext,Viewacc,";
    		}	 
    		
			// 发起人可以打印
			if (p_wf_processNeedPrint.equals("true")) {
				//buttonIds += "EzFlowPrint,";
				buttonIds += "Print,";
			}
		}
        
		// 通用按钮 发起 关联不显示
		if (!openType.equals("startOpen") && !openType.equals("relation") && !openType.equals("") && !openType.equals("null")) {
			com.whir.org.common.util.SysSetupReader sysRed = com.whir.org.common.util.SysSetupReader.getInstance();
			//内部邮件提醒
			String haveMail ="0";
			String domainId =CommonUtils.getSessionDomainId(request)==null?"0":CommonUtils.getSessionDomainId(request).toString();
			haveMail =sysRed.getOa_mailremind(domainId);
			logger.debug("haveMail----->"+haveMail);
			
			if("1".equals(haveMail)){
				// 邮件转发
			    if(buttonSetIds.indexOf("EzFlowTranWithMail")>=0){
					buttonIds += "EmailSend,";	
				}else{
					if(this.p_wf_workStatus.equals("100")){
						buttonIds += "EmailSend,";	
					}else{
						if(openType.equals("mailView")){
							buttonIds+="EmailSend,";
						}
					}
				}
			}
			// 打印
		    /*if(buttonSetIds.indexOf("EzFlowPrint")>=0){
				buttonIds += "Print,";
		    }*/ 
		    if(!openType.equals("modifyView")){
			    //同步到信息管理
				if(buttonSetIds.indexOf("SynToInfo")!=-1){
					buttonIds += "SynToInfo,";
				}  
		    } 
		}
		
		if(openType.equals("mailView")){
			buttonIds+="Print,";
		}
		
		//办理查阅打开
		if(openType.equals("modifyView")){
			buttonIds += "Print,";  
		    if (true) { 
		    	WFDealSearchBD wfBD = new WFDealSearchBD();
		    	HttpSession session = request.getSession();
		    	String userId=session.getAttribute("userId").toString();
		    	String orgId=session.getAttribute("orgId").toString();
		    	String orgIdString=session.getAttribute("orgIdString").toString();
		    	String domainId=session.getAttribute("domainId").toString(); 
		    	//ManagerBD managerBD = new ManagerBD();
		    	//boolean   analyRight=managerBD.hasRight(session.getAttribute("userId").toString(), "gzlc*01*01");  
		    	boolean  dosssierRight=wfBD.hasModifyRights( userId , orgId, orgIdString, domainId, p_wf_processDefinitionKey); 
		    	if(dosssierRight){
		    		// 在办才显示
		    		if(p_wf_workStatus.equals("1")){
		    			buttonIds += "Back,";
		    		}
		    	    buttonIds += "SynToInfo,";
		    	}
		    	
	    		// 在办才显示
	    		if(p_wf_workStatus.equals("1")){
		    	    buttonIds += "Wait,";
		    	}
		    	//办理中的才出现 按钮
				if(this.p_wf_workStatus.equals("1")){
					// 流程类型  1：业务流程  0：随机流程    2：半自由流程    3：完全自由流程
					if(p_wf_processType.equals("1")&&dosssierRight){
						buttonIds += "FreeJump,";
					}
				} 
		    }    
		}
		
		// 发起fromDraft 打开草稿     startAgain 再次发起        reStart  重新提交
		if (openType.equals("startOpen")|| openType.equals("startAgain")||openType.equals("reStart")|| openType.equals("")
				|| openType.equals("null")) {
			// 发送
			//buttonIds += "EzFlowStartProcess,";
			buttonIds += "Send,";
			// 相关流程
			//buttonIds += "EzFlowRelationProcess,";
			buttonIds += "Relation,";
			if (!openType.equals("startAgain") || openType.equals("reStart")) {
				// 保存草稿
				buttonIds += "EzFlowSaveDraft,";
			} 
			
		    //通用公文按钮
			//转收文
			if(buttonSetIds.indexOf("Toreceive")!=-1){
				buttonIds += "Toreceive,";
			}
			//转发文
			if(buttonSetIds.indexOf("Tosend")!=-1){
				buttonIds += "Tosend,";
			}
			//转文件按送审签
			if(buttonSetIds.indexOf("Tocheck")!=-1){
				buttonIds += "Tocheck,";
			}
			//公文交换
			if(buttonSetIds.indexOf("GovExchange")!=-1){
				buttonIds += "GovExchange,";
	        }
			
			// 流程类型  1：业务流程  0：随机流程    2：半自由流程    3：完全自由流程
			if(p_wf_processType.equals("2")||p_wf_processType.equals("3")){
				buttonIds+="SetProcess,"; 
			}
		}
		
		logger.debug("buttonIds1:"+buttonIds);
	    if(buttonIds.indexOf("SynToInfo,")>0){
	    	InformationBD informationBD=new InformationBD();
	    	try {
				if(informationBD.getInfoFromWorkFlow(this.p_wf_recordId,this.p_wf_moduleId)){
					logger.debug("已经有按钮:"+buttonIds);
					buttonIds=buttonIds.replaceAll("SynToInfo,", "");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    } 
		logger.debug("buttonIds2:"+buttonIds);
	    
		initExtendButton(this.p_wf_openType,this.p_wf_workStatus);

		//buttonIds += extbuttonSetIds;
		if (buttonIds.endsWith(",")) {
			buttonIds = buttonIds.substring(0, buttonIds.length() - 1);
		}
        logger.debug("结束时buttonIds："+buttonIds);
        this.p_wf_modiButton=buttonIds;
           
	}
	
	protected void initExtendButton(String openType,String status) {
        logger.debug("开始初始化自定义按钮信息");
		if (p_wf_extbuttonSetIds != null && !p_wf_extbuttonSetIds.equals("")) {
			com.whir.service.api.ezflowservice.EzFlowButtonService buttonService = new com.whir.service.api.ezflowservice.EzFlowButtonService();
			List allbuttonList = buttonService.getAllButtonList("0", "");
			String _buttonId = "";
			String _buttonName = "";
			String _buttonTips = "";
			String _buttonImage = "";
			String _buttonContent = "";
			String _buttonClass="";
			String[] buttonobj = null;
			Map extendButtonMap = new HashMap();
			if (allbuttonList != null && allbuttonList.size() > 0) {
				StringBuffer keyInfoBuffer = new StringBuffer();
				for (int b = 0; b < allbuttonList.size(); b++) {
					buttonobj = (String[]) allbuttonList.get(b);
					_buttonId = buttonobj[0];
					_buttonName = buttonobj[1];
					_buttonTips = buttonobj[2];
					_buttonImage = buttonobj[3];
					_buttonContent =buttonobj[4];
					_buttonClass=  ""+buttonobj[5]; 
					boolean canAdd=false;
					if(_buttonClass.equals("0")&&!openType.equals("myUnderWaitingDeal")){
						canAdd=true;
					}
					//待办
					if(_buttonClass.equals("1")&&openType.equals(EzFlowOpenTypeFinals.WAITINGDEAL)){
						canAdd=true;
					}
					
					//待阅件
					if(_buttonClass.equals("2")&&openType.equals(EzFlowOpenTypeFinals.WAITINGREAD)){
						canAdd=true;
					}
					
					//在办/办结按钮
					if(_buttonClass.equals("3")&&openType.equals(EzFlowOpenTypeFinals.DEALED)){
						canAdd=true;
					}
					
					//在办按钮
					if(_buttonClass.equals("4")&&openType.equals(EzFlowOpenTypeFinals.DEALED)&&"1".equals(status+"")){
						canAdd=true;
					}
					
					//我的在办按钮
					if(_buttonClass.equals("5")&&openType.equals(EzFlowOpenTypeFinals.MYTASK)&&"1".equals(status+"")){
						canAdd=true;
					}
					
					if(!canAdd){
						continue;
					}
					
					if (p_wf_extbuttonSetIds.indexOf(_buttonId) >= 0) {

						keyInfoBuffer = new StringBuffer("{");
						keyInfoBuffer.append("id:").append("'").append(_buttonId).append("'")
						             .append(",name:").append("'").append(_buttonName).append("'")
								     .append(",tips:").append("'").append(_buttonTips).append("'")
								     .append(",img:").append("'").append(_buttonImage).append("'")
								     .append(",width:'10'}");
						logger.debug("keyInfoBuffer:" + keyInfoBuffer);
						extendButtonMap.put(_buttonId, keyInfoBuffer.toString());
						// 按钮调用的函数体
						p_wf_extButtonFunContent += " function  cmd"+ _buttonId + "(){" + _buttonContent + "} ";
					}
				}
				request.setAttribute("extendButtonMap", extendButtonMap);
			}
		}
	}
	
	
	/**
	 * 初始化信息
	 */
	protected void initInfo(){
		//初始化流程定义key
		initProcessDefinitionKey();
		//初始化表单key
		initActFromKey();
		//初始化字段信息
		initField();
		//把, 转化为$
		transformField();
		//【综合办公】会务管理，单位收文员进行会议转批，要求转批的过程中：表单字段不可写（即要求审批设置可写的字段不起作用），要求和老工作流一样；
		if(this.p_wf_moduleId.equals("16")){
			p_wf_curCommField="";
		}
	}
	

	/**
	 * 处理流程变量
	 * @param request
	 * @return
	 */
	protected Map dealVarMap(HttpServletRequest request){
		
		logger.debug("开始dealVarMap");
		//流程变量
		Map  varMap=new HashMap();
		//流程变量字段
		String whir_processallVariables=p_wf_processallVariables;
			
		//分隔符
		String splitStr=",";
		//不为空  ，并且  开始 结束 都以  分隔符结束
		if(EzFlowUtil.judgeNull(whir_processallVariables)&&whir_processallVariables.endsWith(splitStr)&&whir_processallVariables.startsWith(splitStr)){
			whir_processallVariables=whir_processallVariables.substring(1,whir_processallVariables.length()-1);
			whir_processallVariables=whir_processallVariables.replaceAll(splitStr+splitStr, splitStr);
			
			String whir_processallVariableArr[]=whir_processallVariables.split(splitStr);
			
			//一个是 活动动选择的表单
			String formCode=this.p_wf_formKey_act;
			if(!EzFlowUtil.judgeNull(formCode)){
				//流程选择的表单
				formCode=p_wf_formKey;
			}
			logger.debug("p_wf_pool_formType:"+p_wf_pool_formType);
			logger.debug("p_wf_formKey:"+p_wf_formKey);
			//固定表单
			if(p_wf_pool_formType.equals("2")){ 
				com.whir.ezoffice.bpm.bd.BPMFormInfoBD  formBD=new com.whir.ezoffice.bpm.bd.BPMFormInfoBD();
				Map fieldMap=formBD.getFixedFieldValue(formCode, whir_processallVariableArr, request); 
				Iterator it = fieldMap.entrySet().iterator();
	            while (it.hasNext()) {
		            Map.Entry entry = (Map.Entry) it.next();
		            String key = (String)entry.getKey();
		            String  values =(String  ) entry.getValue();	
		            logger.debug("固定  key："+key+"|values:"+values);
		            
		            //公文机关代字 为空 时候 也为;
		            if(key!=null&&key.equals("sendFileDepartWord")){
		            	if(values!=null&&values.equals(";")){
		            		values=null;
		            	}
		            }
		            varMap.put(key, values);                  
	            } 	
			}else{ 
				/*返回:Map包含
				 * key-系统字段名
				 * value-String[3]一维数组
				 * String[0]-字段值
				 * String[1]-字段类型（如：字符型、整型、浮点型。。。）
				 *           1000000-整型
				 *           1000001-浮点型
				 *           1000002-字符型
				 *           1000003-文本型
				 * String[2]-字段显示方式（如：单行文本、多行文本。。。）
				 * 
				 * */
				com.whir.ezoffice.ezform.service.FormService  formService=new com.whir.ezoffice.ezform.service.FormService();
				Map fieldMap=formService.getFieldsInfoWithRequest(formCode, whir_processallVariableArr, curUserInfoVO.getDomainId(), request);
					
				Iterator it = fieldMap.entrySet().iterator();
	            while (it.hasNext()) {
		            Map.Entry entry = (Map.Entry) it.next();
		            String key = (String)entry.getKey();
		            String []values =(String []) entry.getValue();	
		            //显示值
		            String fvalue=values[0];
		            //字段类型
		            String ftype=values[1];
		            //字段显示方式
		            String showType=values[2];
		            //字段的影藏值
		            String fhiddenValue=fvalue;
		            //字段保存到数据库的
		            String fAllValue=fvalue;
		            
		            String nowValue=fvalue;
		            
		            logger.debug("dealVarMap   key:"+key+"value:"+values[0]+"| fhiddenValue:"+values[3]+"|fAllValue:"+values[4]+"  ");
		            
		            //单选人     全部  ||本组织
		            if(showType!=null&&(showType.equals("210")||showType.equals("704"))){  
		            	if(values.length>3){
		            		fhiddenValue=values[3]; 
		            		fAllValue=values[4];
		            	}
		            	//单选的人 没有在前后加$ 这里统一加上
		            	if(fhiddenValue==null||fhiddenValue.equals("")){
		            		nowValue=fhiddenValue;
		            	}else{
		            		nowValue="$"+fhiddenValue+"$";
		            	} 
		            }
		            //多选人    全部||本组织
		            if(showType!=null&&(showType.equals("211")||showType.equals("705"))){
		            	if(values!=null&&values.length>3){
		            		fhiddenValue=values[3]; 
		            		fAllValue=values[4];
		            	}
		            	nowValue=fhiddenValue;
		            }  
		            
		            //单选组织
		            /* if(showType!=null&&showType.equals("212")){ 
		            	if(values!=null&&values.length>3){
		            		fhiddenValue=values[3]; 
		            		fAllValue=values[4];
		            	}
		            	//单选的组织 没有在前后加* 这里统一加上
		            	if(fhiddenValue==null||fhiddenValue.equals("")){
		            		nowValue=fhiddenValue;
		            	}else{
		            		nowValue="*"+fhiddenValue+"*";
		            	}	            	
		            }*/
		            
		            
		            //公文机关代字 为空 时候 也为;
		            if(key!=null&&key.equals("sendFileDepartWord")){
		            	if(values!=null&&values.equals(";")){
		            		values=null;
		            	}
		            }
		            
		            logger.debug("dealVarMap   key:"+key+"| value:"+nowValue+"|"+"  ");
		            
		            if(ftype!=null&&ftype.equals("1000000")&&EzFlowUtil.judgeNull(nowValue)){
		            	nowValue=nowValue.replaceAll(",", "");
		            	varMap.put(key, Integer.parseInt(nowValue));       	
		            }else if(ftype!=null&&ftype.equals("1000001")&&EzFlowUtil.judgeNull(nowValue)){
		            	// 500,000改为 500000
		            	nowValue=nowValue.replaceAll(",", "");
		            	varMap.put(key, Float.parseFloat(nowValue));       	
		            }else if(ftype!=null){
		            	if(nowValue==null){	
		            		varMap.put(key, null);    
		            	}else if(nowValue.equals("$null$")){            		
		            	}else{
		            		varMap.put(key, nowValue);             		
		            	}            	 	
		            }            
	            }
			}
		}
	 
		//上一活动办理人上级领导职务级别
		Float leadLeaderLevel=new Float("0");
		Double  leadLeaderLevel_d=new Double("0");
		List leadlist=curUserInfoVO.getLeaders();	
		if(leadlist!=null&&leadlist.size()>0){
			UserInfoVO leadVo=(UserInfoVO)leadlist.get(0);
			leadLeaderLevel=new Float(leadVo.getDutyLevel());
			leadLeaderLevel_d=new Double(leadVo.getDutyLevel());
		}  
		//上一活动办理人职务级别   
		varMap.put(EzFlowFinals.CONDI_PRETRANSACTOR_DUTYLEVEL, new Double(curUserInfoVO.getDutyLevel()).doubleValue());
		//上一活动办理人上级领导职务级别
		varMap.put(EzFlowFinals.CONDI_PRETRANSACTOR_LEADERDUTYLEVEL, leadLeaderLevel_d.doubleValue());
		resetUserOrg();
		logger.debug("结束dealVarMap");
		return varMap;	
	}
	
	
	/**
	 * 取 提醒字段 内容 
	 * @param request
	 * @return
	 */
	protected Map dealRemind(HttpServletRequest request){
		return dealRemind(request,false);
	}
	
	
    /**
     * 取提醒字段内容 
     * @param request
     * @param needDealBatchStart  需要处理 批量发起 判断  
     * @return
     */
	protected Map dealRemind(HttpServletRequest request,boolean needDealBatchStart){
		
		//国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString(); 
		String domainId="0";
		String Blank="";
		if(local.equals("en_US")){ 
			Blank=" ";
		}
		
		//流程变量
		Map  varMap=new HashMap();
		
		String remindTitle="";
		
		if(EzFlowUtil.judgeNull(p_wf_titleFieldName)&&EzFlowUtil.judgeNull(request.getParameter(p_wf_titleFieldName))){
			remindTitle=request.getParameter(p_wf_titleFieldName);
		}else{
			varMap.put("remindTitle", request.getParameter("remindTitle"));
			/*varMap.put("whir_proxyTaskId", request.getParameter("whir_proxyTaskId"));
			varMap.put("whir_transferFromId", request.getParameter("whir_transferFromId"));
			varMap.put("whir_backtrackFromTaskId", request.getParameter("whir_backtrackFromTaskId"));*/	
			//流程变量字段
			String whir_processRemindField=this.p_wf_remindField;  
			//分隔符
			String splitStr=",";
			
			logger.debug("whir_processRemindField:"+whir_processRemindField);
			
			//不为空  ，并且  开始 结束 都以  分隔符结束
			if (this.p_wf_moduleId.equals("1")&&EzFlowUtil.judgeNull(whir_processRemindField)&&whir_processRemindField.length()>1) {
//			if (this.p_wf_moduleId.equals("1")&&EzFlowUtil.judgeNull(whir_processRemindField)
//						&& whir_processRemindField.endsWith(splitStr)
//						&& whir_processRemindField.startsWith(splitStr)&&whir_processRemindField.length()>1) {
				
				logger.debug("whir_processRemindField2:"+whir_processRemindField);
				//兼容老的格式  换 为 新的格式 
				whir_processRemindField=EzFlowUtil.returnNewRemindField(whir_processRemindField);
				
				logger.debug("whir_processRemindField new :"+whir_processRemindField);
				//返回变量名
				List <String> remindFiledList=EzFlowUtil.dealRemindField(whir_processRemindField);
				
				//临时删掉   是防止 自定义表单 没有这字段 查询报错。
				boolean haveStartUser=false;
				boolean haveProcessName=false;
				if(remindFiledList.contains("{condi_start_userName}")){
					haveStartUser=true;
					remindFiledList.remove("{condi_start_userName}");
				}
	            if(remindFiledList.contains("{condi_processName}")){
	            	haveProcessName=true;
	            	remindFiledList.remove("{condi_processName}");
				}
	            //流程启动人  与 流程名 默认加  
	            if(!haveStartUser){
	            	whir_processRemindField="{condi_start_userName}"+whir_processRemindField;
	            }
	            if(!haveProcessName){
	            	whir_processRemindField=whir_processRemindField+"{condi_processName}";
	            }
	            
	            Map fieldMap=new HashMap();           
	            if(remindFiledList!=null&&remindFiledList.size()>0){
	            	
					//一个是 活动动选择的表单
					String formCode=this.p_wf_formKey_act;
					if(!EzFlowUtil.judgeNull(formCode)){
						//流程选择的表单
						formCode=p_wf_formKey;
					} 
					int size=remindFiledList.size(); 
					String whir_processRemindFieldArr[]=new String[size];//whir_processRemindField.split(splitStr); 
					for(int index=0;index<size;index++){
						String str=remindFiledList.get(index);
						if(str!=null&&str.length()>2){
							str=str.substring(1,str.length()-1);
						}
						whir_processRemindFieldArr[index]=str;
					}
					/*返回:Map包含
					 * key-系统字段名
					 * value-String[3]一维数组
					 * String[0]-字段值
					 * String[1]-字段类型（如：字符型、整型、浮点型。。。）
					 *           1000000-整型
					 *           1000001-浮点型
					 *           1000002-字符型
					 *           1000003-文本型
					 * String[2]-字段显示方式（如：单行文本、多行文本。。。）*/
					com.whir.ezoffice.ezform.service.FormService  formService=new com.whir.ezoffice.ezform.service.FormService();
					fieldMap=formService.getFieldsInfoWithRequest(formCode, whir_processRemindFieldArr,domainId, request);
					Iterator it = fieldMap.entrySet().iterator();
		             while (it.hasNext()) {
			            Map.Entry entry = (Map.Entry) it.next();
			            String key = (String)entry.getKey();
			            String []values =(String []) entry.getValue();
			        	logger.debug("dealRemind key  :"+key+" value:"+values[0]);
		            }
					//按顺序取
		            
					/*for(int i=0;i<whir_processRemindFieldArr.length;i++){
		            	if(fieldMap.get(whir_processRemindFieldArr[i])!=null){
		            	     String []values =(String [])fieldMap.get(whir_processRemindFieldArr[i]);
		            	     remindTitle+=values[0];
		            	}         	
		            }*/
				}
	            
	            if(fieldMap==null){
	            	fieldMap=new HashMap();
	            }  
	            String startUser=this.p_wf_submitPerson;
	            if(startUser==null||startUser.equals("")||startUser.equals("null")){
	            	startUser=this.curUserInfoVO.getUserName();
	            }
	            
	            fieldMap.put("condi_start_userName", new String[]{startUser});
	            fieldMap.put("condi_processName", new String[]{this.p_wf_processName}); 
	            
	            //删除后 又 加上 为了取值 ---------------------
	            //批量发起 留下 {condi_start_userName}  作为变量，发起没一行时 再替换
	            if(!needDealBatchStart){
	        	   remindFiledList.add("{condi_start_userName}"); 
	        	}
            	remindFiledList.add("{condi_processName}"); 
	            remindTitle=EzFlowUtil.returnRemindValue(whir_processRemindField,remindFiledList,fieldMap);
			} 
			
			//为空自动加流程名称
			if(remindTitle==null||remindTitle.equals("")){ 
			     remindTitle=remindTitle+Blank+this.p_wf_processName;
			}
		}
		
		//如果为空
        /*if(!EzFlowUtil.judgeNull(remindTitle)){
        	remindTitle=request.getParameter("processDefinitionName");
        }*/
        
		if(remindTitle!=null&&remindTitle.length()>150){
			remindTitle=remindTitle.substring(0,150)+"......";
		}
        varMap.put("remindTitle", remindTitle);    
         
        varMap.put("LOCALE", local);
        
        //打开页面时的 taskId
        varMap.put("oldTaskId", request.getParameter("p_wf_oldTaskId"));
        
        String mail_pass = com.whir.common.util.CommonUtils.getSessionUserPassword(request);
        String mail_account = com.whir.common.util.CommonUtils.getSessionUserAccount(request);
        
        varMap.put("mail_account", mail_account);
        varMap.put("mail_pass", mail_pass); 
        
        //打开地址
        varMap.put("mainLinkFile", this.p_wf_mainLinkFile);
        
        //批示意见信息
        dealCommentMap(request,varMap);       
		return varMap;		
	}
	
	/**
	 * 处理批示意见 等 扩展信息
	 * @param resultMap
	 * @param request
	 */
	protected Map  dealCommentMap(HttpServletRequest request,Map<String, Object> resultMap){
		logger.debug("开始处理批示意见");
		
		UserInfoVO curUserInfoVO=dealUserInfoVO(); 
		
		//Map<String, Object> resultMap=new HashMap();
		//批示意见对应字段
		String  ezFlow_commentField= this.p_wf_curCommField;
		logger.debug("ezFlow_commentField:"+ezFlow_commentField);
		
		//如果批示意见对应字段不为空
		if(EzFlowUtil.judgeNull(ezFlow_commentField)){
			//内容
			String  ezFlow_commentDealContent=request.getParameter("ezFlow_commentDealContent")==null?"":request.getParameter("ezFlow_commentDealContent").toString();		
			//0: 文字   1：手写   2：电子签章     3     //批示意见类型。  0： 普通，1：手写签名  2：电子签章   3 附件
			String  ezFlow_commentType=request.getParameter("ezFlow_commentType")==null?"0":request.getParameter("ezFlow_commentType").toString();          
            
			logger.debug("ezFlow_commentDealContent:"+ezFlow_commentDealContent);
			logger.debug("ezFlow_commentType:"+ezFlow_commentType);
            resultMap.put("activityId", this.p_wf_cur_activityId);
            resultMap.put("activityName", this.p_wf_cur_activityName);
            resultMap.put("dealUserId", curUserInfoVO.getUserId());
            resultMap.put("dealUserName", curUserInfoVO.getUserName());
            resultMap.put("processInstanceId", p_wf_processInstanceId);
            
            String dutyLevel="0";
 			if(curUserInfoVO.getDutyLevel()!=null&&!curUserInfoVO.getDutyLevel().equals("null")&&!curUserInfoVO.getDutyLevel().equals("")){
 				dutyLevel=curUserInfoVO.getDutyLevel();
 			}
 			logger.debug("dutyLevel:"+dutyLevel);
            resultMap.put("dealUserDutyLevel", dutyLevel);
            if(this.p_wf_isProxy.equals("")||p_wf_isProxy.equals("null")){
            	p_wf_isProxy="0";
            }
            resultMap.put("revision", "");
            resultMap.put("whir_proxyAssignee", this.p_wf_proxyAssignee);
            resultMap.put("whir_proxyAssigneeName", p_wf_proxyAssigneeName);
            resultMap.put("processInstanceId", p_wf_processInstanceId);
            //是否代理办理
            resultMap.put("whir_isProxy", p_wf_isProxy);  
            
			resultMap.put("commentField", ezFlow_commentField);
			resultMap.put("commentType", ezFlow_commentType);
			resultMap.put("dealContent", ezFlow_commentDealContent);
			
			String commentAttitudeType=request.getParameter("commentAttitudeType");
			if(commentAttitudeType==null||commentAttitudeType.equals("")||commentAttitudeType.equals("")){
				commentAttitudeType="0";
			}
			resultMap.put("commentAttitudeType", commentAttitudeType);
			
			 //附件
			 /*StringBuffer attachName = new StringBuffer();
		     String[] attachNameArr = request.getParameterValues("ezFlow_CommentAccessoryName");
		     StringBuffer attachSaveName = new StringBuffer();
		     String[] attachSaveNameArr = request.getParameterValues("ezFlow_CommentAccessorySaveName");
		     int k = 0;
		     for (int i = 0; attachSaveNameArr != null && i < attachSaveNameArr.length;  i++) {
		         if (attachSaveNameArr[i] != null && !"".equals(attachSaveNameArr[i])) {
		                k++;
		                if (k != 1) { //第一个元素的前面不加"|"
		                    attachName.append("|");
		                    attachSaveName.append("|");
		                }
		                attachName.append(attachNameArr[i]);
		                attachSaveName.append(attachSaveNameArr[i]);
		            }
		      }*/
			  
		     String attachName=request.getParameter("ezFlow_CommentAccessoryName")==null?"":request.getParameter("ezFlow_CommentAccessoryName").toString();
		     String attachSaveName=request.getParameter("ezFlow_CommentAccessorySaveName")==null?"":request.getParameter("ezFlow_CommentAccessorySaveName").toString();
		     logger.debug("attachName:"+attachName);
		     logger.debug("attachSaveName:"+attachSaveName);
		     resultMap.put("accDisName", attachName.toString());
		     resultMap.put("accSaveName", attachSaveName.toString());
		     
		     resultMap.put("recordId", this.p_wf_recordId);
		     resultMap.put("ezflowBusinessKey", p_wf_recordId);
		     
		     //当前人决定的范围
		     String ru_commentRangeEmpId=request.getParameter("ru_commentRangeEmpId"); 
		     //审批意见范围由当前人决定
		     if(ru_commentRangeEmpId!=null&&!ru_commentRangeEmpId.equals("")&&!ru_commentRangeEmpId.equals("null")){
		    	// 审批意见范围
				String whir_commentRangeUsers = "";
				String whir_commentRangeOrgs = "";
				String whir_commentRangeGroups = "";
				if (EzFlowUtil.judgeNull(ru_commentRangeEmpId)) {
					for (int i = 0; i < ru_commentRangeEmpId.length(); i++) {
						char flagCode = ru_commentRangeEmpId.charAt(i);
						int nextPos = ru_commentRangeEmpId.indexOf(flagCode, i + 1);
						String str = ru_commentRangeEmpId.substring(i, nextPos + 1);
						if (flagCode == '$') {
							whir_commentRangeUsers = whir_commentRangeUsers + str;
						} else if (flagCode == '*') {
							whir_commentRangeOrgs = whir_commentRangeOrgs + str;
						} else {
							whir_commentRangeGroups = whir_commentRangeGroups + str;
						}
						i = nextPos;
					}
				} else {
					whir_commentRangeUsers = "-1";
					whir_commentRangeOrgs = "-1";
					whir_commentRangeGroups = "-1";
				} 
				resultMap.put("whir_commentRangeUsers",whir_commentRangeUsers);
			    resultMap.put("whir_commentRangeOrgs", whir_commentRangeOrgs);
			    resultMap.put("whir_commentRangeGroups",whir_commentRangeGroups);
		    	
		     }else{ 
		    	//审批意见范围由活动设置里定
		    	//批示意见的范围	
			    resultMap.put("whir_commentRangeUsers", request.getParameter("whir_commentRangeUsers"));
			    resultMap.put("whir_commentRangeOrgs", request.getParameter("whir_commentRangeOrgs"));
			    resultMap.put("whir_commentRangeGroups", request.getParameter("whir_commentRangeGroups")); 	
			}
		    logger.debug("结束处理批示意见");  
		}
		//打开地址
		resultMap.put("mainLinkFile", this.p_wf_mainLinkFile);
		return resultMap;		
	}
	
	
	/**
     *  设置短信信息
     * @param httpServletRequest
     */
    public  void setMessageContent(HttpServletRequest httpServletRequest) {
        messageSettingBD messageSetting = new messageSettingBD();
        javax.servlet.http.HttpSession session = httpServletRequest.getSession(true);
        String sendMen = session.getAttribute("userName").toString(); //取当前用户中文名
        String department = session.getAttribute("orgName").toString(); //取当前用户中文名
        String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
        String title = null;
        if (this.p_wf_titleFieldName==null || "".equals(p_wf_titleFieldName)) {
        	title = p_wf_processName;
        } else {
        	//title = httpServletRequest.getParameter(httpServletRequest.getParameter("processName"));
        	title = httpServletRequest.getParameter(p_wf_titleFieldName);
     	}
        
        if(title==null||title.equals("null")||title.equals("")){
        	title=p_wf_processName;
        }
        title =com.whir.component.security.crypto.EncryptUtil.sqlcode(title);
        //title=p_wf_processName;
        
    	String contents = messageSetting.getModleContents(this.p_wf_msgFrom, title, sendMen, department,domainId); //返回短信内容
          
    	httpServletRequest.setAttribute("smsContent",contents);
    }
    
    
    /**
	 *  保存相关流程
	 * @param recordId
	 */
	protected  void startWithRelation(String recordId){
		logger.debug("开始 保存相关流程");
		Long userId =CommonUtils.getSessionUserId(request);
		Long orgId =CommonUtils.getSessionOrgId(request);
		BPMRelationBD brelationBD=new BPMRelationBD();
		// 添加新建流程相关流程
		if (request.getParameter("rrecordId") != null
				&& !"".equals(request.getParameter("rrecordId"))
				&& !"null".equals(request.getParameter("rrecordId"))) { 
			logger.debug("1添加新建流程相关流程");
			logger.debug("1添加新建流程相关流程rrecordId："+request.getParameter("rrecordId"));
			logger.debug("1添加新建流程相关流程rmoduleId："+request.getParameter("rmoduleId"));
			logger.debug("1添加新建流程相关流程recordId："+recordId);
			logger.debug("1添加新建流程相关流程p_wf_moduleId："+p_wf_moduleId);
			brelationBD.addRelation(request.getParameter("rmoduleId"), request.getParameter("rrecordId"), p_wf_moduleId, recordId, userId, orgId);
		}

		// 添加关联流程   草稿箱 已经是通过ajax 直接关联， 这里要排除 ，不然重复
		if (request.getParameterValues("relationIdStr") != null&&!this.p_wf_openType.equals("fromDraft")) {
			String[] relationIdStr = request.getParameterValues("relationIdStr");
			
			logger.debug("2添加关联流程 recordId："+recordId);
			logger.debug("2添加关联流程p_wf_moduleId："+p_wf_moduleId);
			
			brelationBD.addRelation(p_wf_moduleId, recordId, relationIdStr, userId, orgId); 
		}
		
		logger.debug("结束 保存相关流程");
	}
	
	

    /**
     * 
     * @return
     */
    public boolean judgeAllFields(){
    	boolean result=true;
    	EncryptUtil  util=new EncryptUtil(request);
		String fields = "rrecordId;rmoduleId;p_wf_pool_processId;"+
		                "p_wf_tableId;p_wf_moduleId;p_wf_pool_processType;p_wf_recordId;p_wf_taskId;"+
		                "p_wf_processInstanceId;p_wf_processType;"; 
		
		if(!judgeAllFieldsIsNumber(fields)){
			logger.debug("judgeAllFieldsIsNumber is false  ");
			result=false;
		}
		//dealTips
		String sqlFields="p_wf_processName;p_wf_next_activityName;dealTips;p_wf_cur_activityName;p_wf_activityClass;"+
		                 "p_wf_allowCancel;p_wf_standForUserName"; 
		//p_wf_processName=util.sqlcode(p_wf_processName);  
    	return result;
    }
}
