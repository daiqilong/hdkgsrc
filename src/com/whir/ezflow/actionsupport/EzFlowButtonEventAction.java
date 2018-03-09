package com.whir.ezflow.actionsupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.component.security.crypto.EncryptUtil;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.ChoosedActivityVO;
import com.whir.ezflow.vo.IdentityVO;
import com.whir.ezflow.vo.TransactorInfoVO;
import com.whir.ezflow.vo.UserInfoVO;
import com.whir.ezoffice.bpm.bd.BPMFlowMessageBD;
import com.whir.ezoffice.bpm.bd.BPMJobStartBD;
import com.whir.ezoffice.bpm.po.BPMFlowMessagePO;
import com.whir.ezoffice.formhandler.EzFlow;
import com.whir.ezoffice.message.bd.MessageModelSend;
import com.whir.govezoffice.documentmanager.bd.SenddocumentBD;
import com.whir.i18n.Resource;
import com.whir.service.api.ezflowservice.EzFlowCommentService;
import com.whir.service.api.ezflowservice.EzFlowLogService;
import com.whir.service.api.ezflowservice.EzFlowMainService;
import com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService;
import com.whir.service.api.ezflowservice.EzFlowProcessDraftService;
import com.whir.service.api.ezflowservice.EzFlowTaskService;
import com.whir.service.api.ezflowservice.EzFlowTransactorService;

public class EzFlowButtonEventAction extends EzFlowBaseAttrAction {
	private static Logger logger = Logger.getLogger(EzFlowButtonEventAction.class.getName());
	
	//是否有短信权限
	private String showSmsRemind="false";
	//提醒标题
	private String  button_remindTitle="";
	
	//提醒内容
	private String  button_content="";
	
	//
	private String wf_button_dealType="";
	
	/**
	 * 
	 * 
	 * 退回查找哪些可以退回节点
	 */
	public String  back_init() throws Exception {
		logger.debug("开始跳转到 退回页面");
		String tag = "back_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		List choosedActivityList = ezFlowMainService.canback(p_wf_taskId, p_wf_processInstanceId);
		request.setAttribute("choosedActivityList", choosedActivityList);
		initSmsRight();
		logger.debug("结束到退回页面");
		return tag;
	}

	/**
	 * 执行退回
	 */
	@SuppressWarnings("unchecked")
	public String backProcess() throws Exception {
		logger.debug("开始执行退回操作");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}	
 
		String activityId = request.getParameter("back_activityId");
		String backNeedBackType = request.getParameter("backNeedBackType");
		
		logger.debug("activityId:"+activityId);
		logger.debug("backNeedBackType:"+backNeedBackType);
		
		logger.debug("sendNeedMailType:"+request.getParameter("sendNeedMailType"));

		EzFlowMainService ezFlowMainService = new EzFlowMainService();

		Map extendMap = new HashMap();
		// 1需要邮件提醒
		extendMap.put("sendNeedMailType", request.getParameter("sendNeedMailType"));
		
		// 0：退回环节经办人 1：所有经办人
		extendMap.put("backEmailRemindType", request.getParameter("backEmailRemindType"));
		
		// 1 需要短信提醒
		extendMap.put("backNeedNoteRemind", request.getParameter("backNeedNoteRemind"));
		
		// 退回原因
		extendMap.put("backReason", request.getParameter("backReason"));
		
		// 流程提醒模块
		extendMap.put("msgFrom", request.getParameter("p_wf_msgFrom"));
		
	     //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
 
		// 批示意见
		dealCommentMap(request, extendMap);

		// 退回发起人
		if (activityId.equals("-1")) {
			logger.debug("开始退回发起人");
			ezFlowMainService.backToSubmit(curUserInfoVO, p_wf_processInstanceId, p_wf_taskId, "backToSubmit", extendMap);

			EzFlow ezFlow = new EzFlow();
			// 退回
			Map businessMap = ezFlow.dealFlow("back", request, "");
		} else {
			logger.debug("开始退回到中间环节");
			// 退回到中间环节
			ChoosedActivityVO vo = dealBackUsers(request);
			if ((vo != null)
					&& (vo.getDealTransactorInfoVO() != null)
					&& (vo.getDealTransactorInfoVO().getIdentityVOList().size() > 0)) {
				logger.debug("开始执行退回到中间环节:");
				ezFlowMainService.back(p_wf_taskId, p_wf_processInstanceId, vo, backNeedBackType, extendMap);
			}
		}

		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束执行退回操作");
		return null;
	}

	/**
	 * 取退回的人
	 * 
	 * @param request
	 * @return
	 */
	private ChoosedActivityVO dealBackUsers(HttpServletRequest request) {
		String activityId = request.getParameter("back_activityId");
		ChoosedActivityVO vo = new ChoosedActivityVO();
		vo.setActivityId(activityId);

		String userAccounts = request.getParameter("back_userAccounts");
		String userNames = request.getParameter("back_userNames");
		String userIds = request.getParameter("back_userIds");
		
		logger.debug("userAccounts:"+userAccounts);
		logger.debug("userNames:"+userNames);
		logger.debug("userIds:"+userIds);
		

		if (EzFlowUtil.judgeNull(userAccounts)) {
			String[] userAccountArr = userAccounts.split(",");
			String[] userNameArr = userNames.split(",");
			//String[] UserIdArr = userIds.split(",");

			TransactorInfoVO dealTransactorInfoVO = new TransactorInfoVO("users");
			if ((userAccountArr != null) && (userAccountArr.length > 0)) {
				for (int userIndex = 0; userIndex < userAccountArr.length; userIndex++) {
					if (!EzFlowUtil.judgeNull(userAccountArr[userIndex]))
						continue;
					dealTransactorInfoVO.addIdentityVO(new UserInfoVO("", userNameArr[userIndex], userAccountArr[userIndex]));
				}
			}
			vo.setDealTransactorInfoVO(dealTransactorInfoVO);
		}
		return vo;
	}

	/**
	 * 加签
	 */
	public String addSign() throws Exception {
		logger.debug("开始执行加签操作");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		//
		EzFlowMainService ezFlowMainService = new EzFlowMainService();
    	Map extendMap = new HashMap();
		extendMap.put("addSignNeedNote", request.getParameter("addSignNeedNote"));
		extendMap.put("addSignNeedNoteContent", request.getParameter("addSignNeedNoteContent"));
		//办理提示
		extendMap.put("whir_dealTips", request.getParameter("whir_dealTips"));
		
		
	    // 是否需要邮件提醒
		extendMap.put("sendNeedMailType", request.getParameter("sendNeedMailType"));	
		
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);

		List userList = dealUserList("",request);
		if ((userList != null) && (userList.size() > 0)) {
			ezFlowMainService.addSign(p_wf_taskId, userList, curUserInfoVO, extendMap);
		}

		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束执行加签操作");
		return null;
	}

	/**
	 * 取加签的范围等
	 * 
	 * @throws Exception
	 */
	public String addSign_init() throws Exception {
		logger.debug("开始跳转到加签页面");
		String tag = "addSign_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		EzFlowProcessDefinitionService pdService = new EzFlowProcessDefinitionService();
		logger.debug("p_wf_cur_activityId:"+p_wf_cur_activityId);
		logger.debug("p_wf_processId:"+p_wf_processId);
		logger.debug("p_wf_processInstanceId:"+p_wf_processInstanceId);
		Map resultMap = pdService.getButtonScope("EzFlowAddSignTask",this.p_wf_cur_activityId,
				this.p_wf_processId,this.p_wf_processInstanceId, curUserInfoVO,p_wf_taskId);
		TransactorInfoVO transactorInfoVO = (TransactorInfoVO) resultMap.get("transactorInfoVO");
		request.setAttribute("transactorInfoVO", transactorInfoVO);
		
		//已经发送的任务人
		List haveSendTaskList=(List) resultMap.get("haveSendTaskList");
		
		String sendedUserAccounts="";
		if(haveSendTaskList!=null&&haveSendTaskList.size()>0){
			int s=haveSendTaskList.size();
		    for(int i=0;i<s;i++){
			   UserInfoVO vo=(UserInfoVO)haveSendTaskList.get(i);
			   sendedUserAccounts+=vo.getUserAccount()+","; 
		    }	
		}
	 
		request.setAttribute("haveSendTaskList", haveSendTaskList); 
		request.setAttribute("sendedUserAccounts", sendedUserAccounts); 
		
		initSmsRight();
		this.wf_button_dealType="addSign";
		logger.debug("结束跳转到加签页面");
		return tag;
	}
	 
	
	/**
	 * 补签
	 */
	public String edaddSign() throws Exception {
		logger.debug("开始执行补签操作");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		} 
		
		/**
		 * 
		 * 
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
		 * */
		EzFlowMainService ezFlowMainService = new EzFlowMainService(); 
		Map extendMap = new HashMap();
		//2 表示是补签  1表示是加签
		extendMap.put("addSignType", "2");
		extendMap.put("addSignNeedNote", request.getParameter("sendNeedNoteType"));
		extendMap.put("addSignNeedNoteContent", request.getParameter("whir_noteContent"));
		//办理提示
		extendMap.put("whir_dealTips", request.getParameter("whir_dealTips"));
		
		extendMap.put("sendNeedMailType", request.getParameter("sendNeedMailType"));
		
		extendMap.put("processInstanceId", p_wf_processInstanceId);
		
	    logger.debug("curUserInfoVO--edaddSign--->"+this.curUserInfoVO);
		extendMap.put("curUserInfoVO", this.curUserInfoVO);
		
		logger.debug("sendNeedMailType:"+ request.getParameter("sendNeedMailType"));
		
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
		
		//活动id
		String choosedActivityIds=request.getParameter("p_wf_choosedActivityId");
		logger.debug("p_wf_choosedActivityId:"+choosedActivityIds);
		if(!EzFlowUtil.judgeNull(choosedActivityIds)){
			return null;
		}
		String [] choosedActivityIdArr=choosedActivityIds.split(",");
 
		//活动id
		String chooosedActivityId=""; 
		if(choosedActivityIdArr!=null&&choosedActivityIdArr.length>0){  
			for(int i=0;i<choosedActivityIdArr.length;i++){ 
				chooosedActivityId=choosedActivityIdArr[i]; 
				logger.debug("chooosedActivityId:"+chooosedActivityId);  
				List userList = dealUserList(chooosedActivityId+"_",request);
				logger.debug("userListuserListuserList.size:"+userList.size());
				if ((userList != null) && (userList.size() > 0)) {
					extendMap.put("activityId", chooosedActivityId);   
					ezFlowMainService.addSign(null, userList, curUserInfoVO, extendMap);
				}   
			}
		}	 	
		//   
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束执行补签操作");
		return null;
	}

	
	
	/**
	 * 取补签的范围等
	 * 
	 * @throws Exception
	 */
	public String edAddSign_init() throws Exception {
		logger.debug("开始跳转到补签页面");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		EzFlowProcessDefinitionService pdService = new EzFlowProcessDefinitionService();
		logger.debug("p_wf_cur_activityId:"+p_wf_cur_activityId);
		logger.debug("p_wf_processId:"+p_wf_processId);
		logger.debug("p_wf_processInstanceId:"+p_wf_processInstanceId);
		Map resultMap = pdService.getEdAddSignButtonDealUserScope("EdAddSign",this.p_wf_cur_activityId,
				this.p_wf_processId,this.p_wf_processInstanceId, curUserInfoVO); 
		
		List nextActivitys=(List)resultMap.get("nextActivitys"); 
		logger.debug("nextActivitysnextActivitysnextActivitys.size:"+nextActivitys.size());
		request.setAttribute("nextActivitys", nextActivitys);   
		//initSmsRight(); 
		logger.debug("结束跳转到补签页面");
		return "showActivitys";
	}
	
	
	/**
	 * 取消流程
	 */
	public String  showCancel() throws Exception {
		logger.debug("开始跳转取消流程页面");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		logger.debug("结束跳转取消流程页面");
		return "showCancel";
	}

	/**
	 * 取消流程
	 */
	public String  cancelProcess() throws Exception {
		logger.debug("开始取消流程");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		EzFlow ezFlow = new EzFlow();
		// 取消
		Map businessMap = ezFlow.dealFlow("cancel", request, "");
		
		Map extendMap = new HashMap();
 
		//取消原因吧
		extendMap.put("cancelReason", request.getParameter("cancelReason"));
		
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
        
		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		ezFlowMainService.cancelProcess(curUserInfoVO, p_wf_processInstanceId, p_wf_taskId, "cancelProcess", extendMap);

		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束取消流程");
		return null;
	}

	/**
	 * 作废流程
	 */
	public String  abandonProcess() throws Exception {
		logger.debug("开始作废流程");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		Map extendMap = new HashMap();
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
        
		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		ezFlowMainService.abandonProcess(curUserInfoVO, p_wf_processInstanceId, p_wf_taskId, "abandonProcess", extendMap);
		
		EzFlow ezFlow = new EzFlow();
		// 取消
		Map businessMap = ezFlow.dealFlow("delete", request, "");
		
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束作废流程");
		return null;
	}
	
	/**
	 * 撤销转办
	 * @return
	 * @throws Exception
	 */
	public String cancelTran() throws Exception {
		logger.debug("开始撤销转办");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		Map extendMap = new HashMap();
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
        
        EzFlowTaskService  ezTaskService=new EzFlowTaskService();
        ezTaskService.cancelTran(p_wf_processInstanceId, p_wf_taskId, curUserInfoVO, extendMap);
	 
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束撤销转办");
		return null;
	}

	/**
	 * 转办流程
	 */
	public String  transferDeal() throws Exception {
		logger.debug("开始转办流程");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		
		//打开页面的转办，  不是批量转办  保存表单
		if(EzFlowUtil.judgeNull(this.p_wf_recordId)){  
			EzFlow ezFlow = new EzFlow();
			// 中间保存退出
			Map businessMap = ezFlow.dealFlow("updateClose", request, "");
		}
		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		String taskIds = request.getParameter("p_wf_taskId");
		// 取办理人
		List userList = dealUserList("",request);
		// 2转办 3：转办后自动返回
		int tranStatus = 2;
		if (EzFlowUtil.judgeNull(request.getParameter("tranStatus"))) {
			tranStatus = Integer.parseInt(request.getParameter("tranStatus"));
		}logger.debug("tranStatus："+tranStatus);
		
		Map extendMap = new HashMap();
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
        //邮件提醒
		logger.debug("tranNeedEmail："+request.getParameter("tranNeedEmail"));
		extendMap.put("tranNeedEmail", request.getParameter("tranNeedEmail")+ "");
		
		//短信提醒	
		logger.debug("tranNeedNote："+request.getParameter("tranNeedNote"));
		extendMap.put("tranNeedNote", request.getParameter("tranNeedNote")+ "");
		
		//即时通讯提醒
		logger.debug("tranNeedRtx:"+request.getParameter("tranNeedRtx"));
		extendMap.put("tranNeedRtx", request.getParameter("tranNeedRtx")+ "");
		
		extendMap.put("msgFrom", request.getParameter("p_wf_msgFrom")==null?"工作流程":request.getParameter("p_wf_msgFrom")+ ""); 
		
		extendMap.put("ezflow_randomSend", request.getParameter("ezflow_randomSend")+ ""); 
		
		//办理提示
		extendMap.put("whir_dealTips", request.getParameter("whir_dealTips")==null?"": request.getParameter("whir_dealTips").toString());
		
		dealCommentMap(request, extendMap);
		
		//清除默认的  不然都是默认的 ezflow!update
		extendMap.put("mainLinkFile","");
		ezFlowMainService.transferDeal(curUserInfoVO, taskIds, userList, tranStatus, extendMap);
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束转办流程");
		return null;
	}
	
	/**
	 * 
	 * 转办前找转办范围等
	 */
	public String  transferDeal_init() throws Exception {
		logger.debug("开始跳转到转办页面");
		String tag = "transferDeal_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		EzFlowProcessDefinitionService pdService = new EzFlowProcessDefinitionService();
		Map resultMap = pdService.getButtonScope("EzFlowTransfer",this.p_wf_cur_activityId,
				this.p_wf_processId,this.p_wf_processInstanceId, curUserInfoVO,p_wf_taskId);
		TransactorInfoVO transactorInfoVO = (TransactorInfoVO) resultMap.get("transactorInfoVO");
		request.setAttribute("transactorInfoVO", transactorInfoVO);

		// 当 为1 时 需要自动返回
		String autoTranReturn = resultMap.get("autoTranReturn") == null ? "0" : resultMap.get("autoTranReturn").toString();
		request.setAttribute("autoTranReturn", autoTranReturn);
		this.initSmsRight();
		this.wf_button_dealType="tran";
		logger.debug("结束跳转到转办页面");
		return  tag;
	}
	
	/**
	 * 开始跳转到批量转交页面
	 */
	public String trans_batch_init(){
		this.initSmsRight();
		logger.debug("开始跳转到批量转交页面");
		return "trans_batch_init";	
	}
	

	/**
	 * 此页面公用方法 取办理人
	 * 
	 * @param request
	 * @return
	 */
	private List dealUserList(String chooosedActivityId,HttpServletRequest request) {
		List userList = new ArrayList();
        
		logger.debug("dealUserList userAccounts str:"+chooosedActivityId+"deal_userAccounts");
		String userAccounts_str="deal_userAccounts";
		String userNames_str="deal_userNames";
		String userIds_str="deal_userIds";
		if(chooosedActivityId!=null&&!chooosedActivityId.equals("")){
			userAccounts_str=chooosedActivityId+"deal_userAccount";
			userNames_str=chooosedActivityId+"deal_userName";
			userIds_str=chooosedActivityId+"deal_userId";
		}
		
		String userAccounts = request.getParameter(userAccounts_str);
		String userNames = request.getParameter(userNames_str);
		String userIds = request.getParameter(userIds_str);
		logger.debug("dealUserList userAccounts0:"+userAccounts);
		logger.debug("dealUserList userNames0:"+userNames);
		logger.debug("dealUserList userIds0:"+userIds);
		if (EzFlowUtil.judgeNull(userAccounts)){
			if(userAccounts.indexOf("$")<0){
				userAccounts="$"+userAccounts+"$";
			}
		}
		if (EzFlowUtil.judgeNull(userIds)){
			if(userIds.indexOf("$")<0){
				userIds="$"+userIds+"$";
			}
		}
		logger.debug("dealUserList userAccounts:"+userAccounts);
		logger.debug("dealUserList userNames:"+userNames);
		logger.debug("dealUserList userIds:"+userIds);
		if (EzFlowUtil.judgeNull(userAccounts)) {
			logger.debug("dealUserList userAccounts----->"+EzFlowUtil.dealStrForIn(userAccounts, '$', null));
			String[] userAccountArr = EzFlowUtil.dealStrForIn(userAccounts, '$', null).split(",");
			logger.debug("dealUserList userAccountArr----->"+userAccountArr.length);
			String[] userNameArr = userNames.split(",");
			//String[] UserIdArr = userIds.split(",");
			if ((userAccountArr != null) && (userAccountArr.length > 0)) {
				for (int userIndex = 0; userIndex < userAccountArr.length; userIndex++) {
					if (!EzFlowUtil.judgeNull(userAccountArr[userIndex]))
						continue;
					if(userNameArr.length == userAccountArr.length){
						userList.add(new UserInfoVO("", userNameArr[userIndex], userAccountArr[userIndex]));
					}else{
						userList.add(new UserInfoVO("", "", userAccountArr[userIndex]));
					}
				}
			}
		}
		return userList;
	}

	

	/**
	 * 发送阅件
	 */
	public String sendRead( ) throws Exception {
		logger.debug("开始发送阅件");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
 
		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		
		Map extendMap = new HashMap();
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
 
		// 取人
		List userList = dealUserList("",request);
		//
		String sendReadType = request.getParameter("sendReadType") == null ? "add" : request.getParameter("sendReadType");
		
		//2016-1-4：是否发送邮件提醒
        logger.debug("tranNeedEmail："+request.getParameter("tranNeedEmail"));
		extendMap.put("tranNeedEmail", request.getParameter("tranNeedEmail")+ "");
		
		//短信提醒
		String needNote = request.getParameter("readNeedNote") == null ? "false" : request.getParameter("readNeedNote");
		extendMap.put("needNote", needNote);
		
		extendMap.put("msgFrom", request.getParameter("p_wf_msgFrom")==null?"工作流程":request.getParameter("p_wf_msgFrom")+ ""); 
		
		 //打开地址
		extendMap.put("mainLinkFile", this.p_wf_mainLinkFile);
		
		ezFlowMainService.sendRead(p_wf_taskId, userList, curUserInfoVO, sendReadType, extendMap);

		request.setAttribute("dealFlowResult", "1");
		logger.debug("结束发送阅件");
		this.printResult(this.SUCCESS);
		return null;
	}

	/**
	 * 打开发送阅件页面
	 */
	public String  sendRead_init() throws Exception {
		logger.debug("开始跳转到发送阅件页面");
		String tag = "sendRead_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
  
		EzFlowProcessDefinitionService pdService = new EzFlowProcessDefinitionService();
		Map resultMap = pdService.getButtonScope("EzFlowSendReadTask",this.p_wf_cur_activityId,
				this.p_wf_processId,this.p_wf_processInstanceId, curUserInfoVO,p_wf_taskId);
		TransactorInfoVO transactorInfoVO = (TransactorInfoVO) resultMap.get("transactorInfoVO");
		request.setAttribute("transactorInfoVO", transactorInfoVO);
		
		//已经发送的阅件
		List haveSendReadList=(List) resultMap.get("haveSendReadList");
		request.setAttribute("haveSendReadList", haveSendReadList);
		
		this.initSmsRight();
		this.wf_button_dealType="selfsend";
		logger.debug("结束跳转到发送阅件页面");
		return tag;
	}

	/**
	 * 转阅
	 */
	public String tranRead() throws Exception {
		logger.debug("开始转阅");
		String tag = "dealResult";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}

		EzFlowMainService ezFlowMainService = new EzFlowMainService();
 
		List userList = dealUserList("",request);
		
		Map extendMap = new HashMap();
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
        
        //2016-1-4：是否发送邮件提醒
        logger.debug("tranNeedEmail："+request.getParameter("tranNeedEmail"));
		extendMap.put("tranNeedEmail", request.getParameter("tranNeedEmail")+ "");
		
		//短信提醒
		String needNote = request.getParameter("readNeedNote") == null ? "false" : request.getParameter("readNeedNote");
		extendMap.put("needNote", needNote);

		// add 补发 cover 发送
		String sendReadType = request.getParameter("sendReadType") == null ? "add" : request.getParameter("sendReadType");
		ezFlowMainService.sendRead(p_wf_taskId, userList, curUserInfoVO, sendReadType, extendMap);
		
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束转阅");
		return null;
	}

	/**
	 * 
	 * 转阅页面
	 */
	public String  tranRead_init() throws Exception {
		logger.debug("开始跳转到转阅页面");
		String tag = "tranRead_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}

		EzFlowProcessDefinitionService pdService = new EzFlowProcessDefinitionService();
		Map resultMap = pdService.getButtonScope("EzFlowTranRead",this.p_wf_cur_activityId,
				this.p_wf_processId,this.p_wf_processInstanceId, curUserInfoVO,p_wf_taskId);
		TransactorInfoVO transactorInfoVO = (TransactorInfoVO) resultMap.get("transactorInfoVO");
		request.setAttribute("transactorInfoVO", transactorInfoVO);
		
		this.initSmsRight();
		this.wf_button_dealType="tranread";
		logger.debug("结束跳转到转阅页面");
		return tag;
	}

	/**
	 * 撤办
	 */
	public String recall() throws Exception {
		logger.debug("开始撤办");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}

		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		
		Map extendMap = new HashMap();
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
		ezFlowMainService.recall(curUserInfoVO, p_wf_processInstanceId, p_wf_taskId,extendMap);

		request.setAttribute("dealFlowResult", "1");
		logger.debug("结束撤办");
		this.printResult(this.SUCCESS);
		return null;
	}

	/**
	 * 收回 的弹出框 取 可以收回的 活动节点
	 */
	public String drawBack_init() throws Exception {
		logger.debug("开始跳转到收回页面");
		String tag = "drawBack_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}

		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		List list = ezFlowMainService.canDrawBackActivitys(curUserInfoVO.getUserAccount(), p_wf_processInstanceId);
		request.setAttribute("canDrawBackList", list);
		logger.debug("结束跳转到收回页面");
		return tag;
	}

	/**
	 * 收回
	 */
	public String drawBack() throws Exception {
		logger.debug("开始收回");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}

		EzFlowMainService ezFlowMainService = new EzFlowMainService();
		// 收回到活动节点id
		String toActivityId = request.getParameter("drawBacktoActivityId");

		Map extendMap = new HashMap();
	    //国际化语言类别
        String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
        extendMap.put("LOCALE", local);
		ezFlowMainService.drawBack(curUserInfoVO, p_wf_processInstanceId, toActivityId, extendMap);

		request.setAttribute("dealFlowResult", "1");
		logger.debug("结束收回");
		this.printResult(this.SUCCESS);
		return null;
	}

	/**
	 * 保存退出
	 */
	public String saveClose() throws Exception {
		logger.debug("开始保存退出");
 
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}

		EzFlowMainService ezFlowMainService = new EzFlowMainService();
	 
		EzFlow ezFlow = new EzFlow();
		// 中间保存退出
		Map businessMap = ezFlow.dealFlow("updateClose", request, "");

		Map commengMap = new HashMap();
		dealCommentMap(request, commengMap);
		commengMap.put("commentStatus", "0");
		EzFlowCommentService commentService = new EzFlowCommentService();
		commentService.insert(commengMap);

		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束保存退出");
		return null;
	}

	/**
	 * 保存草稿
	 */
	public String saveDraft() throws Exception {
		logger.debug("开始保存草稿");
		HttpSession session = request.getSession();
		/*-------------------从request取值部分-----------------------*/
		// 当前办理人帐号
		String curUserAccount = session.getAttribute("userAccount") + "";
		String curUserId = session.getAttribute("userId") + "";
		String curUserName = session.getAttribute("userName") + "";
		String curOrgId = session.getAttribute("orgId") + "";
		String curOrgName = session.getAttribute("orgName") + "";
		String domainId = "0";

		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
        logger.debug("p_wf_openType:"+p_wf_openType);
        logger.debug("p_wf_recordId:"+p_wf_recordId);
        String businessKey = (new java.util.Date().getTime()) + "";
		if (this.p_wf_openType.equals("fromDraft") && EzFlowUtil.judgeNull(p_wf_recordId)) {
			logger.debug("修改草稿");
			EzFlow ezFlow = new EzFlow();
			// 草稿箱修改
			Map businessMap = ezFlow.dealFlow("updateFromDraft", request, "");
			businessKey =p_wf_recordId;
			
			BPMJobStartBD  bd=new BPMJobStartBD();
			bd.updateJobProcessInfoFromDraft(businessKey,this.p_wf_processDefinitionKey, this.p_wf_processId);
		    
			// 取提醒字段
			Map remindMap = dealRemind(request);
			String whir_remindtitle = remindMap.get("remindTitle") == null ? "" : remindMap.get("remindTitle").toString();
			
			EzFlowProcessDraftService processDraftService = new EzFlowProcessDraftService();
			
			logger.debug("whir_remindtitle:"+whir_remindtitle);
			processDraftService.updateDraftTitle(this.p_wf_formKey, businessKey, whir_remindtitle);
		} else {
			logger.debug("新增草稿");
			// 新建业务数据 保存草稿记录
			
			EzFlow ezFlow = new EzFlow();
			// 草稿箱保存
			Map businessMap = ezFlow.dealFlow("saveFromDraft", request, "");
			businessKey = "" + businessMap.get("infoId");

			// 取提醒字段
			Map remindMap = dealRemind(request);
			String whir_remindtitle = remindMap.get("remindTitle") == null ? "" : remindMap.get("remindTitle").toString();

			//
			Map newMap = new HashMap();
			newMap.put("rev", "1");
			newMap.put("whir_formkey", this.p_wf_formKey);
			newMap.put("business_key", businessKey);
			newMap.put("whir_remindtitle", whir_remindtitle);
			newMap.put("proc_def_id",this.p_wf_processId);
			newMap.put("proc_def_name",this.p_wf_processName);
			newMap.put("proc_def_key",this.p_wf_processDefinitionKey);

			newMap.put("create_userid", curUserId);
			newMap.put("create_username", curUserName);
			newMap.put("create_useraccount", curUserAccount);
			newMap.put("create_orgid", curOrgId);
			newMap.put("create_orgname", curOrgName);

			EzFlowProcessDraftService processDraftService = new EzFlowProcessDraftService();
			String id = processDraftService.insert(newMap);
		}
		
		//保存相关流程  
	    startWithRelation(businessKey);

		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束保存草稿");
		return null;
	}

	/**
	 * 催办页面
	 */
	public String press_init() throws Exception {
		logger.debug("开始跳转到催办页面");
		logger.debug("button_remindTitle:"+button_remindTitle);
		String tag = "press_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		dealDealingActivity(request);
		this.initSmsRight();
		
		logger.debug("结束跳转到催办页面");
		return tag;
	}
	
	
	/***
	 * ajax 取催办标题
	 */
	public  String  ajaxInitPressTitle(){
		logger.debug("<---ajaxInitPressTitle--->");
		this.initPressTitle();
		this.printResult(this.button_remindTitle);
		return null;
	}

	/**
	 * 催办
	 */
	public String press() throws Exception {
		logger.debug("开始催办");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		dealpress(request);
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束催办");
		return null;
	}

	/**
	 * 反馈页面
	 */
	public String feedback_init() throws Exception {
		logger.debug("开始反馈页面");
		String tag = "feedback_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		String addedUserAccount=this.p_wf_submitUserAccount;
		dealDealedActivity(request,addedUserAccount);
		this.initSmsRight();
		button_content="该流程已办理完毕！请查阅！";  
		logger.debug("结束反馈页面");
		return tag;
	}
	/**
	 * ajax  获取反馈的标题
	 * @return
	 */
	public String ajaxInitFeedBackTitle(){
		logger.debug("开始取反馈标题");
		this.initFeedBackTitle();
		this.printResult(button_remindTitle);
		logger.debug("button_remindTitle："+button_remindTitle);
		logger.debug("结束取反馈标题");
		return null;
	}

	/**
	 * 反馈操作
	 */
	public String feedback() throws Exception {
		logger.debug("开始反馈");
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		dealFeedback(request);
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束反馈.......");
		return null;
	}

	/**
	 * 邮件转发页面
	 */
	public String tranWithMail_init() throws Exception {
		logger.debug("开始邮件转发页面");
		String tag = "tranWithMail_init";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		} 
		Map map = dealRemind(request);
		String remindTitle = map.get("remindTitle") == null ? "" : map.get("remindTitle").toString();
		request.setAttribute("remindTitle", remindTitle);
		initSmsRight();
		logger.debug("结束邮件转发页面");
		return tag;
	}

	/**
	 * 邮件转发
	 */
	public String tranWithMail() throws Exception {
		logger.debug("开始邮件转发");
		String tag = "dealResult";
		if(!EzFlowUtil.judgeNull(dealUserInfoVO().getUserAccount())){
			return this.NO_RIGHTS;
		}
		mialSend(request);
		request.setAttribute("dealFlowResult", "1");
		this.printResult(this.SUCCESS);
		logger.debug("结束邮件转发");
		return null;
	}

	/**
	 * 处理当前办理人
	 * 
	 * @param request
	 */
	private void dealDealingActivity(HttpServletRequest request) {
		// 经过处理的所有办理人
		List userInfoVOList = new ArrayList();

		EzFlowMainService mainService = new EzFlowMainService();
		EzFlowTransactorService transactorService = new EzFlowTransactorService();
		// 流程实例id
		String processInstanceId = this.p_wf_processInstanceId;
		// 取当前所在的活动 与 办理人
		List<ChoosedActivityVO> deallist = mainService.findDealingActivity(processInstanceId);
		StringBuffer userAccountsBuffer = new StringBuffer();
		if (deallist != null && deallist.size() > 0) {
			IdentityVO identityVo = null;
			ChoosedActivityVO avo = null;
			for (int ai = 0; ai < deallist.size(); ai++) {
				avo = (ChoosedActivityVO) deallist.get(ai);
				// 活动办理人
				TransactorInfoVO dealTransactorInfoVO = avo.getDealTransactorInfoVO();
				if (dealTransactorInfoVO == null) {
					continue;
				}
				List<IdentityVO> identityVOList = dealTransactorInfoVO.getIdentityVOList();
				if (identityVOList != null && identityVOList.size() > 0) {
					for (int i = 0; i < identityVOList.size(); i++) {
						identityVo = identityVOList.get(i);
						userAccountsBuffer.append("'").append(identityVo.getIdentityCode()).append("'").append(",");
					}
				}
			}
		}
		String userAccounts = userAccountsBuffer.toString();
		if (userAccounts != null) {
			if (userAccounts.endsWith(",")) {
				userAccounts = userAccounts.substring(0,userAccounts.length() - 1);
				userInfoVOList = transactorService.getUserInfoVOsByAccounts(userAccounts);
			}
		}
		request.setAttribute("userInfoVOList", userInfoVOList);
	}
	
	
	/**
	 * 处理已经办过的办理人
	 * 
	 * @param request
	 */
	private void dealDealedActivity(HttpServletRequest request,String addedUserAccount ) {
		// 经过处理的所有办理人
		List userInfoVOList = new ArrayList();

		EzFlowMainService mainService = new EzFlowMainService();
		EzFlowTransactorService transactorService = new EzFlowTransactorService();
		// 流程实例id
		String processInstanceId = this.p_wf_processInstanceId;
		// 取当前所在的活动 与 办理人
		List<ChoosedActivityVO> deallist = mainService.findDealedActivity(processInstanceId);
		StringBuffer userAccountsBuffer = new StringBuffer();
		if (deallist != null && deallist.size() > 0) {
			IdentityVO identityVo = null;
			ChoosedActivityVO avo = null;
			for (int ai = 0; ai < deallist.size(); ai++) {
				avo = (ChoosedActivityVO) deallist.get(ai);
				// 活动办理人
				TransactorInfoVO dealTransactorInfoVO = avo.getDealTransactorInfoVO();
				if (dealTransactorInfoVO == null) {
					continue;
				}
				List<IdentityVO> identityVOList = dealTransactorInfoVO.getIdentityVOList();
				if (identityVOList != null && identityVOList.size() > 0) {
					for (int i = 0; i < identityVOList.size(); i++) {
						identityVo = identityVOList.get(i);
						userAccountsBuffer.append("'").append(identityVo.getIdentityCode()).append("'").append(",");
					}
				}
			}
		}
		
		String userAccounts = userAccountsBuffer.toString();
		if(EzFlowUtil.judgeNull(addedUserAccount)){
			if (userAccounts != null) {
				userAccounts+="'"+addedUserAccount+"',";
			}else{
				userAccounts+="'"+addedUserAccount+"',";
			}
		}
		if (userAccounts != null) {
			if (userAccounts.endsWith(",")) {
				userAccounts = userAccounts.substring(0,userAccounts.length() - 1);
				userInfoVOList = transactorService.getUserInfoVOsByAccounts(userAccounts);
			}
		}
		request.setAttribute("userInfoVOList", userInfoVOList);
	}

	/**
	 * 催办
	 * 
	 * @param httpServletRequest
	 *            HttpServletRequest
	 */
	private void dealpress(HttpServletRequest request) {
	    logger.debug("开始催办1");
		HttpSession session = request.getSession();
		/*-------------------从request取值部分-----------------------*/
		// 当前办理人帐号
		String curUserAccount = session.getAttribute("userAccount") + "";
		String curUserId = session.getAttribute("userId") + "";
		String curUserName = session.getAttribute("userName") + "";
		String curOrgId = session.getAttribute("orgId") + "";
		String curOrgName = session.getAttribute("orgName") + "";
		String domainId = "0";
		
		// 流程定义名
		String processDefinitionName = request.getParameter("p_wf_processName");
		
		com.whir.ezoffice.pressdeal.bd.PersonalOAPressManageBD bd = new com.whir.ezoffice.pressdeal.bd.PersonalOAPressManageBD();
		boolean pressSMS = (request.getParameter("pressSMS") != null && request.getParameter("pressSMS").equals("true")) ? true : false;
		// 是否需要外部邮件催办
		boolean pressOutEmail = (request.getParameter("pressOutEmail") != null && request.getParameter("pressOutEmail").equals("true")) ? true : false;
		// 需要外部邮件提醒
		if (pressOutEmail) {
			com.whir.ezoffice.personalwork.innermailbox.bd.sendEmail se = new com.whir.ezoffice.personalwork.innermailbox.bd.sendEmail();
			String sendToId = request.getParameter("pressUserId");

			sendToId = sendToId.replace('$', ',');
			sendToId = sendToId.replaceAll(",,", ",");
			sendToId = sendToId.substring(1, sendToId.length() - 1);
			// 分割取收件人id
			String[] userIdArr = null;
			if (sendToId != null) {
				userIdArr = sendToId.split(",");
			}

			if (userIdArr != null && userIdArr.length > 0) {
				for (int i = 0; i < userIdArr.length; i++) {
					try {
						se.exeSendMail("", "" + userIdArr[i],
								request.getParameter("pressTitle") + "",
								request.getParameter("pressContent") + "");

					} catch (Exception ex1) {
						// ex1.printStackTrace();
					}
				}
			}
		}
		logger.debug("pressUserId："+request.getParameter("pressUserId"));
		logger.debug("pressUserName："+request.getParameter("pressUserName"));
		logger.debug("pressTitle："+request.getParameter("pressTitle"));
		logger.debug("msgFrom："+request.getParameter("p_wf_msgFrom"));
		logger.debug("processDefinitionName："+request.getParameter("p_wf_processName"));
		logger.debug("pressSMS："+request.getParameter("pressSMS"));
		try {
			String rslt = bd.sendNewPress(request.getParameter("pressUserId"), // 原始串（格式：*2*$12$@9@*123*）
					request.getParameter("pressUserName"), // 接收人串(格式: 接收人,接收人,)
					curUserId, // 发送人id
					curUserName, // 发送人名
					curOrgName, // 发送人部门名
					request.getParameter("pressTitle"), // 催办标题
					request.getParameter("pressContent"), // 催办内容
					request.getParameter("p_wf_msgFrom"), // 催办所属类别
					processDefinitionName, // 催办所属子类别
					domainId, pressSMS, request);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		logger.debug("结束催办1");
	}

	/**
	 * 反馈
	 * 
	 * @param httpServletRequest
	 *            HttpServletRequest
	 */
	private void dealFeedback(HttpServletRequest request) {
		HttpSession session = request.getSession();
		/*-------------------从request取值部分-----------------------*/
		// 当前办理人帐号
		String curUserAccount = session.getAttribute("userAccount") + "";
		String curUserId = session.getAttribute("userId") + "";
		String curUserName = session.getAttribute("userName") + "";
		String curOrgId = session.getAttribute("orgId") + "";
		String curOrgName = session.getAttribute("orgName") + "";
		String domainId = "0";

		sendInnerMail(
				request.getParameter("feedTitle"),
				request.getParameter("feedContent"),
				// "系统提示",Long.valueOf("0"),
				curUserName, Long.valueOf("0"),
				request.getParameter("feedUserId"),
				request.getParameter("feedUserName"),
				(request.getSession(true).getAttribute("domainId")).toString(),request);

		if (request.getParameter("feedUserId") != null
				&& request.getParameter("feedUserId").toString().length() > 1) {
			String empIds = request.getParameter("feedUserId").toString();
			String restlt = empIds.replaceAll("\\$", ",");
			restlt = restlt.replaceAll(",,", ",");
			restlt = restlt.substring(1, restlt.length() - 1);
			this.sendRTX(restlt, "反馈提醒", request.getParameter("feedTitle"), "-1");
		}
	}

	/**
	 * 
	 * @param userIds
	 *            String
	 * @param content
	 *            String
	 */
	private void sendRTX(String userIds, String title, String content, String moduleId) {
		javax.sql.DataSource ds = null;
		try {
			ds = new com.whir.common.util.DataSourceBase().getDataSource();
			java.sql.Connection conn = ds.getConnection();
			java.sql.Statement stmt = conn.createStatement();
			java.sql.ResultSet rs = stmt.executeQuery("select useraccounts from org_employee where emp_id in (" + userIds + ")");
			StringBuffer allUserIdBuffer = new StringBuffer();
			while (rs.next()) {
				allUserIdBuffer.append(rs.getString(1)).append(",");
			}
			rs.next();
			stmt.close();
			conn.close();
			if (allUserIdBuffer.length() > 1) {
				allUserIdBuffer = allUserIdBuffer.deleteCharAt(allUserIdBuffer.length() - 1);
			}
			// this.sendRTXNotify(allUserIdBuffer.toString(), content);
			com.whir.integration.realtimemessage.Realtimemessage util = new com.whir.integration.realtimemessage.Realtimemessage();
			// 1 2 4 3
			if (moduleId.equals("1") || moduleId.equals("2")
					|| moduleId.equals("3") || moduleId.equals("4")) {
				util.setPushType("1"); // type 0-最新邮件 1-待办文件 2-最新公文
			}

			util.sendNotify(allUserIdBuffer.toString(), title, content);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 邮件转发
	 * 
	 * @param request
	 */
	private void mialSend(HttpServletRequest request) {
		logger.debug("mialSend 开始 ");
		HttpSession session = request.getSession();
		/*-------------------从request取值部分-----------------------*/
		// 当前办理人帐号
		String curUserAccount = session.getAttribute("userAccount") + "";
		String curUserId = session.getAttribute("userId") + "";
		String curUserName = session.getAttribute("userName") + "";
		String curOrgId = session.getAttribute("orgId") + "";
		String curOrgName = session.getAttribute("orgName") + "";
		String domainId = "0";
 
		//String title = request.getParameter("tranMailTitle");
		
		String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		
		Map map = dealRemind(request);
		// 取 流程的提醒值 --------王国良
		String remindTitle = map.get("remindTitle") == null ? "" : map.get("remindTitle").toString();
	    //标题
		//String title= this.p_wf_submitPerson+this.p_wf_submitTime+Resource.getValue(local,"filetransact","file.turnto")+remindTitle;
        
		String title=this.p_wf_processName;
		// 接收人
		String toId = request.getParameter("tranMailtoId") == null ? "" : request.getParameter("tranMailtoId");
		String toName = request.getParameter("tranMailtoName") == null ? "" : request.getParameter("tranMailtoName");
		if (toName.endsWith(",")) {
			toName.substring(0, toName.length() - 1);
		}
		
		logger.debug("mialSend p_wf_mainLinkFile： "+p_wf_mainLinkFile);
		
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
		
		/*
		String  verifyCode=request.getParameter("verifyCode");
		if(EzFlowUtil.judgeNull(verifyCode)&&EzFlowUtil.judgeNull(this.p_wf_taskId)){
			if(verifyCode.indexOf("ezFlowTaskId")>0){
				flowurl = mainUrl+ "ezFlowTaskId="
						+ p_wf_taskId+ "&p_wf_recordId=" +p_wf_recordId
						+ "&p_wf_openType=mailView&verifyCode="+verifyCode; 
			}
		}else if(EzFlowUtil.judgeNull(verifyCode)&&EzFlowUtil.judgeNull(this.p_wf_processInstanceId)){
			if(verifyCode.indexOf("ezFlowProcessInstanceId")>0){
				flowurl =mainUrl
						+ "ezFlowProcessInstanceId="
						+ p_wf_processInstanceId+ "&p_wf_recordId=" +p_wf_recordId
						+ "&p_wf_openType=mailView&verifyCode="+verifyCode; 
			}
		}else{
			EncryptUtil encryptUtil=new EncryptUtil();
		    verifyCode= encryptUtil.getSysEncoderKeyVlaue("ezFlowProcessInstanceId", p_wf_processInstanceId, "WFDealWithAction");
			flowurl =mainUrl
			+ "ezFlowProcessInstanceId="
			+ p_wf_processInstanceId+ "&p_wf_recordId=" +p_wf_recordId
			+ "&p_wf_openType=mailView&verifyCode="+verifyCode; 
		}*/
		
		EncryptUtil encryptUtil=new EncryptUtil();
	    String verifyCode= encryptUtil.getSysEncoderKeyVlaue("ezFlowProcessInstanceId", p_wf_processInstanceId, "WFDealWithAction");
		flowurl =mainUrl
		+ "ezFlowProcessInstanceId="
		+ p_wf_processInstanceId+ "&p_wf_recordId=" +p_wf_recordId+"&ezFlowTaskId="+this.p_wf_taskId
		+ "&p_wf_openType=mailView&verifyCode="+verifyCode; 

		request.setAttribute("toName", toName);
		// 邮件内容
		String mailContent = request.getParameter("tranMailtoContent") == null ? ""
				: request.getParameter("tranMailtoContent");
		
		com.whir.common.util.SysUtils utils = new com.whir.common.util.SysUtils();
		String[] mailRemindType = utils.getMailRemindType(""+session.getAttribute("domainId"));
		String mailremind_type = mailRemindType[0];
        
		logger.debug("flowurl 2:"+flowurl);
		
		// 邮件连接内容
		String linkcontent = "转发人：" + curOrgName + " " + curUserName;
		if(!mailremind_type.equals("1")&&!mailremind_type.equals("2")){
			linkcontent = linkcontent + "</P>";
			//linkcontent += " <a href=\"#\"  onclick=\"openWin({url:'"+flowurl+"',isfull:true,winName:'view'});\" >查看表单</a>";
			
			String uniLink = com.whir.common.util.CommonUtils.getUniUrl( flowurl);   			
		    //linkcontent += " <a href=\"" + uniLink+ "\"   >查看表单</a>";	 		     
		     
		    linkcontent+="<a href='javascript:void(0);' onclick=\"javascript:openWin({url:'"+uniLink+"', width:800, height:600, isFull:true});\" class=\"uniLink\">查看表单</a>"; 
		    
		    linkcontent = linkcontent + "</P>";
		}
		linkcontent = linkcontent +"\n"+ mailContent;
		
		String rtxLinkContent="邮件转发提醒，转发人：" + curOrgName + " " + curUserName;
		
		//RTX提醒
		String sendNeedRTXType=request.getParameter("sendNeedRTXType")==null?"0":request.getParameter("sendNeedRTXType").toString();
        //短信提醒
		String sendNeedNoteType=request.getParameter("sendNeedNoteType")==null?"0":request.getParameter("sendNeedNoteType").toString();
		boolean result = true;
		String empIds = "";
		if ((toId != null) && (toId.length() > 0)) {
			result =sendInnerMail(remindTitle, linkcontent, "1", curUserName,
					Long.valueOf("0"), toId, toName, domainId,request);
			if(sendNeedRTXType.equals("1")){
			   sentRtx_(toId,flowurl,"邮件转发提醒",rtxLinkContent);
			}
			
			if(sendNeedNoteType.equals("1")){
				sendMsg(toId,rtxLinkContent);
			}
		} else {
			result = false;
		}
		if (result) {
			request.setAttribute("mailtSuccess", "1");
			// 流程日志
		} else {
			request.setAttribute("mailtSuccess", "0");
		}
		
		//日志处理
		EzFlowLogService   logService=new EzFlowLogService();
		Map logMap=new HashMap();			
		logMap.put("dealUserId",curUserId);
		logMap.put("dealUserName", curUserName);
		logMap.put("dealUserAccount", curUserAccount);
		logMap.put("domainId", domainId);
		logMap.put("recordId", p_wf_recordId);
		logMap.put("processInstanceId", p_wf_processInstanceId);
		logMap.put("nowTaskId", p_wf_taskId);	
		logMap.put("curActivityId", this.p_wf_cur_activityId);
		logMap.put("curActivityName", this.p_wf_cur_activityName);	
		logMap.put("curActivityStep", p_wf_activityStep);
		logMap.put("dealAction", "邮件转发");
		logMap.put("dealType", "MAILSEND");
		logMap.put("receiveIds", toId);
		logMap.put("receiveNames", toName); 
		logService.insertLog(logMap);
	}
	
	/**
	 * 
	 * @param toId 接收人
	 * @param linkurl
	 * @param fff
	 */
	private void  sentRtx_(String toId  ,String flowurl ,String title,String text ){  
	    SenddocumentBD bd = new SenddocumentBD();
	    String userIds=bd.getEmpIdsByTotal(toId); 
	    if(userIds!=null&&!userIds.equals("")&&!userIds.equals("null")){
	    	 String userIds_=EzFlowUtil.dealStrForIn(userIds, '$', "");  
	    	 String userIdArr[]=userIds_.split(",");    
	    	 com.whir.integration.realtimemessage.Realtimemessage util=new com.whir.integration.realtimemessage.Realtimemessage();
			 //util.sendNotify(receiverAccounts, title, text);
			 util.setPushType("1"); 
			 //util.sendFingerMessage(receiverAccounts, title, text); 
			 com.whir.component.config.ConfigXMLReader reader=new com.whir.component.config.ConfigXMLReader();
			 //系统ip地址获取  
		     String oaIP = reader.getAttribute("OAIP", "Server");  
		     //模块名称  
		     String modelName = "ezFLOW";  
		     com.whir.org.basedata.bd.LoginPageSetBD loginPageSetBD = new com.whir.org.basedata.bd.LoginPageSetBD();  
		     String domainAccount = com.whir.org.common.util.SysSetupReader.getInstance().isMultiDomain();  
		     
		     EzFlowMainService   service=new EzFlowMainService();
	 
			 for(int i=0;i<userIdArr.length;i++){
				 String userName="";
				 String userId=userIdArr[i];
				 
				 String  sql="select USERACCOUNTS,USERPASSWORD   from  org_employee  A "+
				          " where   EMP_ID="+userId+"  and A.USERISACTIVE=1 and  A.USERISDELETED<>1 ";
				 
				 Map inMap=new HashMap();
				 inMap.put("sql", sql);
				 
				 Map resultMap=service.searchBySql_out(inMap);
				 
				 List passwordList=(List)resultMap.get("resultList");
				 String realPassword="";
				 
				 if(passwordList!=null&&passwordList.size()>0){
					 Map map=(Map)passwordList.get(0);
					 realPassword=""+map.get("USERPASSWORD");
					 userName=""+map.get("USERACCOUNTS");
				 }
				  
				 //获得临时密码  
			     String password = loginPageSetBD.addTmpPasswordPO(modelName, realPassword);  
			     //跳转地址  
			     String toLink =  flowurl;    
			     //转码  
			     String  toLinktoLink = toLink.replaceAll("\\&", "%26");  
			  
			     String _url = oaIP+ "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount="  
			            + domainAccount + "&userName=" + userName  
			            + "&RealtimeMsgLogin=" + modelName + "&userPassword="+password  
			            + "&toLink=" + toLinktoLink;   
			     /* System.out.println("_url:"+_url);
			     System.out.println("userName:"+userName);
			     System.out.println("title:"+title);
			     System.out.println("text:"+text); */
			     //boolean  result= util.sendNotify(userName, title,text, title, _url);
			     
			     boolean  result= util.sendNotify(userName, title, text, title, modelName, userName, realPassword, toLink);
			     //System.out.println("result:"+result);
			 }  
	    }
	}
	
	/**
	 * 判断短信权限
	 */
	public void initSmsRight() {
		showSmsRemind = "false";
		HttpSession session = request.getSession();
		java.util.Map include_sysMap = com.whir.org.common.util.SysSetupReader.getInstance().getSysSetupMap(session.getAttribute("domainId").toString());
		if (include_sysMap != null && include_sysMap.get("短信开通") != null && "1".equals(include_sysMap.get("短信开通").toString())) {
			logger.debug("有系统短信权限");
			logger.debug("p_wf_msgFrom:"+p_wf_msgFrom);
			MessageModelSend  messageModelSend=new MessageModelSend(); 
			if (messageModelSend.judgePurviewMessage(p_wf_msgFrom,  
					session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString())) {
				logger.debug("有模块短信权限:"+p_wf_msgFrom);
				if (new com.whir.org.manager.bd.ManagerBD().hasRight(session.getAttribute("userId").toString(), "09*01*01")) {
					logger.debug("有人员短信权限:"+session.getAttribute("userId").toString());
					showSmsRemind = "true";
				}
			}
		}
	}
	
	/**
	 * 发送短信
	 * @param toId
	 * @param smsContent
	 */
	private void sendMsg(String toId,  String smsContent) { 
		SenddocumentBD bd = new SenddocumentBD();
		String userIds = bd.getEmpIdsByTotal(toId);
		if (userIds != null && !userIds.equals("") && !userIds.equals("null")) {
			String userIds_ = EzFlowUtil.dealStrForIn(userIds, '$', ""); 
			String msgFrom = this.p_wf_msgFrom;
			MessageModelSend  messageModelSend=new MessageModelSend(); 
			messageModelSend.sendSystemMessage(msgFrom, smsContent, userIds_, "", request, "1");
		} 
	}
    
	/**
	 * 反馈标题
	 */
	private void initFeedBackTitle() {
		String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		String Blank="";
		if(local.equals("en_US")){ 
			Blank=" ";
		} 
		Map map = dealRemind(request);
		// 取 流程的提醒值 --------王国良
		String remindTitle = map.get("remindTitle") == null ? "" : map.get("remindTitle").toString();
		if(remindTitle==null||remindTitle.equals("")){
			remindTitle=this.p_wf_processName;
		}
		// 标题
		this.button_remindTitle = Resource.getValue(local, "filetransact", "file.feedback")
				+ ":"
				+ this.p_wf_submitPerson
				+this.p_wf_submitTime
				+ Resource.getValue(local, "filetransact", "file.turnto")
				+ remindTitle
				+ Resource.getValue(local, "filetransact", "file.hascomplete");
	}
	
	/**
	 * 反馈标题
	 */
	private void initPressTitle() {
        //国际化的语言标记
		String local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		//英文状态下加空格
		String Blank="";
		if(local.equals("en_US")){ 
			Blank=" ";
		} 
		
		Map map = dealRemind(request);
		// 取 流程的提醒值 --------王国良
		String remindTitle = map.get("remindTitle") == null ? "" : map.get("remindTitle").toString();
		
		if(remindTitle==null||remindTitle.equals("")){
			remindTitle=p_wf_processName;
		}
		// 标题
		this.button_remindTitle = Resource.getValue(local, "filetransact", "file.remind")
				+ ":"
				+ p_wf_submitPerson
				+ p_wf_submitTime
				+ Resource.getValue(local, "filetransact", "file.turnto")
				+ remindTitle 
				+ Resource.getValue(local, "filetransact", "file.waityourdeal");
		logger.debug("this.button_remindTitle----->"+this.button_remindTitle);
	}
	
	/**
	 * 异步新增留言并获取新留言
	 * @return
	 */
	public String saveMessageAndGetMessage() throws Exception{
		String messageProcessId  =request.getParameter("processId");
		String messageProcinstId =request.getParameter("processInstanceId");
		String messageRecordId   =request.getParameter("recordId");
		String messageActivity   =request.getParameter("activityName");
		String messageTitle       =request.getParameter("remindTitle");
		logger.debug("messageProcessId----->"+messageProcessId);
		logger.debug("messageProcinstId----->"+messageProcinstId);
		logger.debug("messageRecordId----->"+messageRecordId);
		logger.debug("messageActivity----->"+messageActivity);
		logger.debug("messageTitle----->"+messageTitle);
		
		String messageContent =request.getParameter("message_Content");
		messageContent =messageContent.trim();
		logger.debug("messageContent----->"+messageContent);
		
		String empLivingPhoto ="";
		
		Long messageUserId     =CommonUtils.getSessionUserId(request);
		String messageUserName =CommonUtils.getSessionUserName(request);
		Long messageOrgId      =CommonUtils.getSessionOrgId(request);
		String messageOrgName  =CommonUtils.getSessionOrgName(request);
		
		String messageDealName =messageUserName;
		
		Date messageDate =new Date();
		
		BPMFlowMessageBD messageBD =new BPMFlowMessageBD();
		empLivingPhoto =messageBD.getEmpLivingPhoto(messageUserId);
		logger.debug("empLivingPhoto----->"+empLivingPhoto);
		
		BPMFlowMessagePO po =new BPMFlowMessagePO();
		
		po.setMessageTitle(messageTitle);
		po.setMessageContent(messageContent);
		po.setMessageDate(messageDate);
		po.setMessageUserId(messageUserId);
		po.setMessageUserName(messageUserName);
		po.setMessageOrgId(messageOrgId);
		po.setMessageOrgName(messageOrgName);
		po.setMessageProcessId(messageProcessId);
		po.setMessageProcinstId(messageProcinstId);
		po.setMessageRecordId(messageRecordId);
		po.setMessageIsNew("0");//新留言
		po.setMessageActivity(messageActivity);
		po.setMessageDealName(messageDealName);
		
		Long messageId =messageBD.saveFlowMessage(po);
		logger.debug("messageId----->"+messageId);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String json ="";
		if(messageId.longValue() >0){
			json ="{\"flag\":\"0\",\"empLivingPhoto\":\""+empLivingPhoto+"\",\"messageDate\":\""+sdf.format(messageDate)+"\"}";
		}else{
			json ="{\"flag\":\"1\"}";
		}
		printJsonResult(json);
		
		return null;
	}
    
	public String getShowSmsRemind() {
		return showSmsRemind;
	}

	public void setShowSmsRemind(String showSmsRemind) {
		this.showSmsRemind = showSmsRemind;
	}

	public String getWf_button_dealType() {
		return wf_button_dealType;
	}

	public void setWf_button_dealType(String wf_button_dealType) {
		this.wf_button_dealType = wf_button_dealType;
	}

	public String getButton_remindTitle() {
		return button_remindTitle;
	}

	public void setButton_remindTitle(String button_remindTitle) {
		this.button_remindTitle = button_remindTitle;
	}

	public String getButton_content() {
		return button_content;
	}

	public void setButton_content(String button_content) {
		this.button_content = button_content;
	}
}
