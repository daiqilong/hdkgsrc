package com.whir.ezflow.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.UserInfoVO;
import com.whir.ezoffice.message.bd.MessageBD;
import com.whir.ezoffice.message.bd.messageSettingBD;
import com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD;
import com.whir.ezoffice.workmanager.personstatus.service.OutMailService;
import com.whir.i18n.Resource;

/**
 * 工具类服务
 * @author wanggl
 *
 */
public class UtilService {
	private static Logger logger = Logger.getLogger(UtilService.class.getName());

	//语言类别
	private String local="";

	//发邮件
	private String mail_pass="";
	private String mail_account="";


	public UtilService() {

	}
	public UtilService(String local) {
		this.local = local;
	}

	public void setLocal(String local){
		this.local=local;
	}

	public String getMail_pass() {
		return mail_pass;
	}
	public void setMail_pass(String mail_pass) {
		this.mail_pass = mail_pass;
	}
	public String getMail_account() {
		return mail_account;
	}
	public void setMail_account(String mail_account) {
		this.mail_account = mail_account;
	}
	/**
	 * 传入sql 查询
	 * @param sql
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public   List<Map> searchBySql(String sql){
		List<Map>list=new ArrayList<Map>();
		CommandContext _commandContext=Context.getCommandContext();
		if(_commandContext!=null){
		 DbSqlSession session=_commandContext.getSession(DbSqlSession.class);

	     String query = "whir_selectBySql";
	     Map<String,Object>queryMap=new HashMap<String,Object>();
	     queryMap.put("sql", sql);
	     list=session.selectList(query, queryMap, null);
	  }

      return list;
     }

	 /**
	 *传sql 查结果
	 * @param inMap
	 * @return
	 */
	public   Map<String,Object> searchBySql_out(Map<String,Object>inMap){
		String sql=""+inMap.get("sql");
		Map<String,Object>resultMap=new HashMap<String,Object>();

		List<Map>list=new ArrayList<Map>();
		CommandContext _commandContext=Context.getCommandContext();
		if(_commandContext!=null){
		 DbSqlSession session=_commandContext.getSession(DbSqlSession.class);

	     String query = "whir_selectBySql";
	     Map<String,Object>queryMap=new HashMap<String,Object>();
	     queryMap.put("sql", sql);
	     list=session.selectList(query, queryMap, null);
	   }
		resultMap.put("resultList", list);
        return resultMap;
     }


	/**
	 * 删除
	 * @param sql
	 * @return
	 */
	public int deleteBySql(String sql){
		DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
		SqlSession sqlSession=session.getSqlSession();
		Map<String,Object>queryMap=new HashMap<String,Object>();
	    queryMap.put("sql", sql);
		int result=sqlSession.delete("whir_deleteBySql", queryMap);
        return result;
	}


	/**
	 * 新增
	 * @param sql
	 * @return
	 */
	public int insertBySql(String sql){
		DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
		SqlSession sqlSession=session.getSqlSession();
		Map<String,Object>queryMap=new HashMap<String,Object>();
	    queryMap.put("sql", sql);
		int result=sqlSession.insert("whir_insertBySql", queryMap);
        return result;
	}

	/**
	 * 根据sql 新增
	 * @param inMap
	 * @return
	 */
	public  Map<String,Object>  insertBySql_out(Map<String,Object>inMap){
		DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
		SqlSession sqlSession=session.getSqlSession();
		int result=sqlSession.insert("whir_insertBySql", inMap);
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("result", result);
        return resultMap;
     }


	/**
	 * 根据sql 批量修改
	 * @param sql
	 * @return
	 */
	public  int  updateBySql(String sql){
		DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
		SqlSession sqlSession=session.getSqlSession();
		Map<String,Object>queryMap=new HashMap<String,Object>();
	    queryMap.put("sql", sql);
		int result=sqlSession.update("whir_updateBySql", queryMap);
        return result;
     }


	/**
	 * 根据sql 批量修改
	 * @param inMap
	 * @return
	 */
	public  Map<String,Object>  updateBySql_out(Map<String,Object>inMap){
		DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
		SqlSession sqlSession=session.getSqlSession();
		int result=sqlSession.update("whir_updateBySql", inMap);
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("result", result);
        return resultMap;
     }


	 /**
	  * 发送提醒
	  * @param inMap
	  */
	 /*public  void  sendRemindMessage(Map<String ,Object> inMap){
		 //什么操作的提醒
		 String typeAction=inMap.get("typeAction").toString();
		 //提醒的类型： RTX ,  MAIL , NOTE
		 String remindType=inMap.get("remindType").toString();

		 //流程信息
		 String processInstanceId=inMap.get("processInstanceId")==null?"":inMap.get("processInstanceId").toString();
		 String processDefinitionName=inMap.get("processDefinitionName")==null?"":inMap.get("processDefinitionName").toString();
		 String ezflowBusinessKey=inMap.get("ezflowBusinessKey")==null?"":inMap.get("ezflowBusinessKey").toString();
		 String whir_formKey=inMap.get("whir_formKey")==null?"":inMap.get("whir_formKey").toString();
		 String whir_processRemindField=inMap.get("whir_processRemindField")==null?"":inMap.get("whir_processRemindField").toString();

		 String msgFrom=inMap.get("msgFrom")==null?"工作流程":inMap.get("msgFrom").toString();

		 //当前环节
		 String curActivityName=inMap.get("curActivityName")==null?"":inMap.get("curActivityName").toString();
		 //下一环节
		 String nextAcitivityName=inMap.get("nextAcitivityName")==null?"":inMap.get("nextAcitivityName").toString();

		 //发起人信息
		 String whir_startUserName=inMap.get("whir_startUserName")==null?"":inMap.get("whir_startUserName").toString();
		 String whir_startOrgName=inMap.get("whir_startOrgName")==null?"":inMap.get("whir_startOrgName").toString();
		 String ezFlow_startTime=inMap.get("ezFlow_startTime")==null?"":inMap.get("ezFlow_startTime").toString();
		 String whir_startUserId=inMap.get("whir_startUserId")==null?"":inMap.get("whir_startUserId").toString();
		 String whir_startUserAccount=inMap.get("whir_startUserAccount")==null?"":inMap.get("whir_startUserAccount").toString();

		 //接收人信息  都是以  ,分割
		 String receiveIds=inMap.get("receiveIds")==null?"":inMap.get("receiveIds").toString();
		 String receiveNames=inMap.get("receiveNames")==null?"":inMap.get("receiveNames").toString();
		 String receiveAccounts=inMap.get("receiveAccounts")==null?"":inMap.get("receiveAccounts").toString();

		 //当前办理人
		 UserInfoVO curUserInfoVO=(UserInfoVO)inMap.get("curUserInfoVO");

		 String title="";
		 String content="";

		 //办理任务
		 if(typeAction.equals("completeTask")){
			 title="文件办理提醒";
			 content="新待批文件提醒\n "+whir_startOrgName+" "+whir_startUserName+ ezFlow_startTime+"提交的"+processDefinitionName;
			 content+="处于"+nextAcitivityName;
			 content+="请您处理！\n";

			 //邮件提醒
			 if(remindType.indexOf(",MAIL,")>=0){
				 sendMail(title,content,receiveNames,receiveAccounts);
			 }

			 //RTX提醒
			 if(remindType.indexOf(",RTX,")>=0){
				 sendRTX(receiveAccounts,title,content);
			 }

			 //短信提醒
			 if(remindType.indexOf(",RTX,")>=0){
				 sendSystemMessage(curUserInfoVO,msgFrom,title,content,receiveIds,"1");
			 }
		 }

		 //结束流程
		 if(typeAction.equals("endProcess")){
			 title="您的"+processDefinitionName+"已经办理完毕.";
			 content="您的"+processDefinitionName+"已经办理完毕.";

		     //邮件提醒
			 if(remindType.indexOf(",MAIL,")>=0){
				 sendMail(title,content,receiveNames,receiveAccounts);
			 }

			 //RTX提醒
			 if(remindType.indexOf(",RTX,")>=0){
				 sendRTX(receiveAccounts,title,content);
			 }

			//短信提醒
			 if(remindType.indexOf(",RTX,")>=0){
				 sendSystemMessage(curUserInfoVO,msgFrom,title,content,receiveIds,"1");
			 }
		 }

		 //退回流程
		 if(typeAction.equals("backProcess")){

		 }
	 }*/

	 /**
	  * 发送RTX等等
	  * @param receiverAccounts
	  * @param title
	  * @param text
	  */
	 public void  sendRTX(String receiverAccounts, String title,String text){
//		 com.whir.integration.realtimemessage.Realtimemessage util=new com.whir.integration.realtimemessage.Realtimemessage();
//		 util.sendNotify(receiverAccounts, title, text);
//		 util.setPushType("1");
//		 util.sendFingerMessage(receiverAccounts, title, text);

		 Map infoMap=new HashMap();
		 infoMap.put("receiverAccounts", receiverAccounts);
		 infoMap.put("title", title);
		 infoMap.put("text", text);
		 addRemindInCache("rtx1",infoMap);
		 addRemindInCache("sendFingerMessage",infoMap);
	 }

	 public void  sendRTX_(String receiverAccounts,String taskIds, String title,String text,String flowType){
		 sendRTX_( receiverAccounts, taskIds,  title, text, flowType,null,null,"0","/ezflowopen!updateProcess.action","");
	 }

	 public void  sendRTX_(String receiverAccounts,String taskIds, String title,String text,String flowType,String remindTitle,String ucText,String synPhone,String mainUrl,String recordId){
		 logger.debug("  sendRTX_  开始 ");

		 logger.debug("receiverAccounts:"+receiverAccounts);
		 logger.debug("taskIds:"+taskIds);
		 logger.debug("title:"+title);
		 logger.debug("text:"+text);
		 logger.debug("flowType:"+flowType);
		 logger.debug("remindTitle:"+remindTitle);
		 logger.debug("ucText:"+ucText);
		 logger.debug("synPhone:"+synPhone);
		 logger.debug("mainUrl:"+mainUrl);
		 logger.debug("recordId:"+recordId);

		 //ezcard名片订购流程，发起、办理不发送EVO、微信提醒
		 boolean ezCardSend =true;
		 if(mainUrl !=null && !mainUrl.equals("") && mainUrl.indexOf("ezcardAction!modify.action") > -1){
			 ezCardSend =false;
		 }
		 logger.debug("ezCardSend----->"+ezCardSend);

//		 com.whir.integration.realtimemessage.Realtimemessage util=new com.whir.integration.realtimemessage.Realtimemessage();
//		 util.sendNotify(receiverAccounts, title, text);
//		 util.setPushType("1");
//		 util.sendFingerMessage(receiverAccounts, title, text);

		 String remindPre ="";
		 if(flowType.equals("read")){
			 remindPre ="新待阅：";
		 }else{
			 remindPre ="新待批：";
		 }

		 if(synPhone!=null && synPhone.equals("1") && ezCardSend){
			 Map infoMap = new HashMap();
			 infoMap.put("receiverAccounts", receiverAccounts);
			 infoMap.put("title", remindPre + remindTitle + "等待您的办理！");
			 infoMap.put("text", text);
			 addRemindInCache("sendFingerMessage", infoMap);
		 }

		 com.whir.component.config.ConfigXMLReader reader=new com.whir.component.config.ConfigXMLReader();
		 //系统ip地址获取
	     String oaIP = reader.getAttribute("OAIP", "Server");
	     //模块名称
         String modelName = "ezFLOW";
         com.whir.org.basedata.bd.LoginPageSetBD loginPageSetBD = new com.whir.org.basedata.bd.LoginPageSetBD();
         String domainAccount = com.whir.org.common.util.SysSetupReader.getInstance().isMultiDomain();
		 String receiverAccountsArr[]=receiverAccounts.split(",");
		 String taskIdsArr[]=taskIds.split(",");
		 com.whir.component.security.crypto.EncryptUtil  eutil=new  com.whir.component.security.crypto.EncryptUtil ();
		 //com.whir.evo.weixin.bd.WeiXinBD  weixinbd=new  com.whir.evo.weixin.bd.WeiXinBD();
		 for(int i=0;i<receiverAccountsArr.length;i++){
			 String userName=receiverAccountsArr[i];
			 String userId="";
			 String  sql="select USERPASSWORD,EMP_ID   from  org_employee  A "+
			          " where   useraccounts='"+userName+"'  and A.USERISACTIVE=1 and  A.USERISDELETED<>1 ";
			 List passwordList=this.searchBySql(sql);
			 String realPassword="";
			 if(passwordList!=null&&passwordList.size()>0){
				 Map map=(Map)passwordList.get(0);
				 realPassword=""+map.get("USERPASSWORD");
				 userId=""+map.get("EMP_ID");
			 }
			 //获得临时密码
		     String password = loginPageSetBD.addTmpPasswordPO(modelName, realPassword);
		     //跳转地址
		     /* String toLink = "/defaultroot/EzFlowOpenAction.do?action=updateOpen"+"&taskId="+taskIdsArr[i];
		          if(flowType.equals("read")){
		    	      toLink+="&openType=waitingRead";
			      }else{
			    	 toLink+="&openType=waitingDeal";
			      }
		     */
		     String  verifyCode=eutil.getSysEncoderKeyVlaue("ezFlowTaskId", taskIdsArr[i], "WFDealWithAction");
		     //verifyCode
		     //跳转地址
		     String toLink = "";

		     if(mainUrl==null||mainUrl.equals("")||mainUrl.equals("null")){
		    	 mainUrl="/ezflowopen!updateProcess.action";
		     }
		     String rootPath= com.whir.component.config.PropertiesUtil.getInstance().getRootPath();

		     if(!mainUrl.startsWith(rootPath)){
		    	 mainUrl= rootPath+mainUrl;
		     }

		     toLink= mainUrl+"?ezFlowTaskId="+ taskIdsArr[i]+ "&verifyCode="+verifyCode;

		     if(recordId!=null&&!recordId.equals("")&&!recordId.equals("null")){
		    	  toLink+="&p_wf_recordId="+recordId;
		     }
		     //&p_wf_openType=mailView
			 if (flowType.equals("read")) {
				toLink += "&p_wf_openType=waitingRead";
			 } else {
				toLink += "&p_wf_openType=waitingDeal";
			 }
		    //转码
		    String  toLinktoLink = toLink.replaceAll("\\&", "%26");

		    /* String _url = oaIP+ "/defaultroot/LoginPageSetAction.do?action=MsLoginPage&domainAccount="
		            + domainAccount + "&userName=" + userName
		            + "&RealtimeMsgLogin=" + modelName + "&userPassword="+password
		            + "&toLink=" + toLinktoLink;   */
		    String _url = oaIP+ "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount="
		             + domainAccount + "&userName=" + userName
		             + "&RealtimeMsgLogin=" + modelName + "&userPassword="+password
		             + "&toLink=" + toLinktoLink;
		     /* System.out.println("_url:"+_url);
		     System.out.println("userName:"+userName);
		     System.out.println("title:"+title);
		     System.out.println("text:"+text); */
		     //boolean  result= util.sendNotify(userName, title,text, title, _url);
		     if(text.endsWith("!\n")){
		    	 text=text.substring(0,text.length()-2);
			 }
		     if(text.endsWith("！\n")){
		    	 text=text.substring(0,text.length()-2);
			 }
		     if(ucText==null||ucText.equals("")||ucText.equals("null")){
		    	 ucText=text;
		     }
		     if(ucText.endsWith("!\n")){
		    	 ucText=ucText.substring(0,ucText.length()-2);
			 }
		     if(ucText.endsWith("！\n")){
		    	 ucText=ucText.substring(0,ucText.length()-2);
			 }


		     if(remindTitle==null||remindTitle.equals("")||remindTitle.equals("null")){
		    	 remindTitle=title;
		     }

//		     logger.debug(" each sendNotify  开始 ");
//		     boolean  result= util.sendNotify(userName, title, ucText, title, modelName, userName, realPassword, toLink);
//		     logger.debug(" each sendNotify  结束");

		     Map _infoMap1=new HashMap();
             _infoMap1.put("userName", userName);
             _infoMap1.put("title", title);
             _infoMap1.put("ucText", ucText);
             _infoMap1.put("modelName", modelName);
             _infoMap1.put("realPassword", realPassword);
             _infoMap1.put("toLink", toLink);
             addRemindInCache("rtx2",_infoMap1);


		     if(synPhone!=null && synPhone.equals("1") && ezCardSend){
 	             Map params  =new HashMap();
//	             params.put("ezflow_taskId",taskIdsArr[i]);
//	             params.put("isezFlow","1");
//
//	             logger.debug("weixing remindTitle :"+remindTitle);
//	             logger.debug("weixing text :"+text);
//	             boolean weixinsuccess = weixinbd.sendMsg(userId,remindTitle,text,null,null,"workflow",params);

	             Map _infoMap2=new HashMap();
                 _infoMap2.put("ezflow_taskId", taskIdsArr[i]);
                 _infoMap2.put("isezFlow", 1);
                 _infoMap2.put("userId", userId);
                 _infoMap2.put("remindTitle", remindTitle);
                 _infoMap2.put("text", text);
                 _infoMap2.put("params", params);
                 _infoMap2.put("flowType", flowType);
    			 addRemindInCache("weixin",_infoMap2);
             }
             logger.debug(" each weixinbd.sendMsg  结束");
		 }
		 logger.debug("  sendRTX_  结束 ");
	 }

	 /**
	  * 发送邮件
	  * @param mailsubject
	  * @param mailcontent
	  * @param posterName
	  * @param accounts
	  */
	 public void  sendMail_(String mailsubject,String mailcontent,String posterName,String accounts){

		 logger.debug("开始  发内部邮件方法 accounts:"+accounts);
		 //MailService  mailService=new MailService();
		 try {
			/* //String   flowurl="http://oa.anji.com";
			 String   flowurl=" http://192.168.0.26:7178/defaultroot/login.jsp";
			 mailcontent = mailcontent + "</P>";
			 mailcontent += " <a href=\"" + flowurl + "\" target=\"_blank\" >请点击此处登入OA办理</a>";
			 mailcontent = mailcontent + "</P>";
		     java.util.Map ftpMap = com.whir.component.config.ConfigReader.getUploadMap(null, "0");
			 mailService.sendSysMail(mailsubject, mailcontent, "系统", accounts);
			 */
			 logger.debug("accounts:"+accounts);
			 logger.debug("mailsubject:"+mailsubject);
			 logger.debug("mailcontent:"+mailcontent);
			 //mailService.sendSysMail(mailsubject, mailcontent, "系统", accounts);

//			 com.whir.component.mail.Mail  mail=new  com.whir.component.mail.Mail();
//			 mail.sendInnerMail(mailsubject, mailcontent, getRealMessage(local,"系统"), accounts);


			 Map _infoMap2 = new HashMap();
			 _infoMap2.put("mailsubject", mailsubject);
			 _infoMap2.put("mailcontent", mailcontent);
			 _infoMap2.put("accounts", accounts);
			 _infoMap2.put("title", getRealMessage(local, "系统"));
			 addRemindInCache("mail1", _infoMap2);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	 }


	 /**
	  * 发送短信
	  * @param model
	  * @param title
	  * @param ids
	  * @param isNotModel
	  * @return
	  */
	 public boolean sendSystemMessage(UserInfoVO curUserInfoVO,String model, String title,String content, String ids,  String isNotModel) {
		logger.debug("curUserInfoVO--sendSystemMessage--->"+curUserInfoVO);
		logger.debug("sendSystemMessage 开始发短信");
		StringBuffer  loginfo=new StringBuffer();
		loginfo.append("model:").append(model).append("\ntitle:").append(title).append("\nisNotModel:").append(isNotModel).append("\nids:").append(ids);
		logger.debug(loginfo.toString());
		// 首先判断是否有发生短信得权限 dierzhang
		String userId=curUserInfoVO.getUserId();
		String userName=curUserInfoVO.getUserName();
		String orgName=curUserInfoVO.getOrgVO().getOrgName();
		String domainId=curUserInfoVO.getDomainId()==null?"0":curUserInfoVO.getDomainId();

		if (!new com.whir.org.manager.bd.ManagerBD().hasRight(userId, "09*01*01")) {
			logger.debug("人员没有发短信的权限");
			return false;
		}

		String sendMen = userName; // 取当前用户中文名
		String department =orgName; // 取当前用户中文名

		messageSettingBD messageSetting = new messageSettingBD();

		if (messageSetting.judgePurviewMessage(model, domainId)) { // 模块可以发短信

			// String contents = messageSetting.getModleContents(model,
			// title,sendMen, department,domainId); //返回短信内容
			// S Upd by sj
			String contents = "";
			if (isNotModel.equals("1")) {
				//contents = title;
				contents=content;
			} else {
				contents = messageSetting.getModleContents(model, title, sendMen, department, domainId); // 返回短信内容
			}
			// E Upd by sj
			if (ids == null || ids.equals(""))
				ids = null;

			if (contents == null || contents.equals(""))
				contents = null;
			MessageBD messageSend = new MessageBD();
			// boolean sendSuccess = messageSend.modelSendMsg(ids, contents,
			// telephone,domainId);
			logger.debug(" 短信的userId"+userId);
			boolean sendSuccess = messageSend.modelSendMsg(ids, contents, null, domainId, userId.toString());
			// System.out.println("========模块发短信方法===是否成功==="+sendSuccess) ;
			return sendSuccess;
		} else {
			logger.debug("模块没有发短信的权限");
			return false;
		}
	}


	/**
	 * 发送提醒
	 */
	public void sendCompleteTaskRemindMessage(String typeAction,String remindType,String msgFrom,String whir_noteContent) {

		logger.debug("开始  sendCompleteTaskRemindMessage");
		logger.debug("开始  mail_account:"+this.mail_account);
		logger.debug("开始  mail_pass:"+this.mail_pass);
		//
	    if(!EzFlowUtil.judgeNull(typeAction)){
	    	return ;
	    }
	    if(!EzFlowUtil.judgeNull(remindType)){
	    	return ;
	    }
        CommandContext nowcommandContext = Context.getCommandContext();

		//取流程定义
		ProcessDefinitionEntity  curProcessDefinition = nowcommandContext.getCurProcessDefinition();

		logger.debug("sendCompleteTaskRemindMessage 结束取  ProcessDefinitionEntity  ");
		//流程定义名
		String processDefinitionName=curProcessDefinition.getName();

		// 流程信息
		String processInstanceId = "";
		String ezflowBusinessKey = "";

		//打开地址
		String mainUrl="";

		// 发起人信息
		String whir_startUserName = "";
		String whir_startOrgName = "";
		String ezFlow_startTime = null;
		String whir_startUserId = "";
		String whir_startUserAccount = "";

		String synphone="";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		//当前办理人
		UserInfoVO curUserInfoVO=nowcommandContext.getCurUserInfoVO();
		logger.debug("curUserInfoVO--sendCompleteTaskRemindMessage--->"+curUserInfoVO);

		logger.debug("sendCompleteTaskRemindMessage 结束取  curUserInfoVO  ");
		String remindTitle="";
		//取当前的流程实例
		HistoricProcessInstanceEntity curProcessInstance=nowcommandContext.getCurProcessInstance();
		if (curProcessInstance != null && curProcessInstance.getId() != null) {
			ezflowBusinessKey= curProcessInstance.getBusinessKey();
			processInstanceId=curProcessInstance.getId();
			whir_startUserName=curProcessInstance.getWhir_startUserName();
			whir_startOrgName=curProcessInstance.getWhir_startOrgName();
			ezFlow_startTime=simpleDateFormat.format(curProcessInstance.getStartTime());
			whir_startUserId=""+curProcessInstance.getWhir_startUserId();
			whir_startUserAccount=curProcessInstance.getStartUserId();
			remindTitle=curProcessInstance.getWhir_remindTitle();

			mainUrl=curProcessInstance.getWhir_updateURL();
			synphone=curProcessInstance.getWhir_mobilePhoneStatus();
		}else{
			//流程发起时 等 没有流程实例id
			ezflowBusinessKey= curProcessInstance.getBusinessKey();
			synphone=curProcessInstance.getWhir_mobilePhoneStatus();
			mainUrl=curProcessInstance.getWhir_updateURL();
			whir_startUserName=curUserInfoVO.getUserName();
			whir_startOrgName=curUserInfoVO.getOrgVO().getOrgName();
			ezFlow_startTime=simpleDateFormat.format(new java.util.Date());
			whir_startUserId=curUserInfoVO.getUserId();
			whir_startUserAccount=curUserInfoVO.getUserAccount();
			if(nowcommandContext.getDealingProperty("remindTitle")!=null){
				remindTitle=""+nowcommandContext.getDealingProperty("remindTitle");
			}
		}

		if(remindTitle==null||remindTitle.equals("")||remindTitle.equals("null")){
			remindTitle=processDefinitionName;
		}

		String title="";
	  	String content="";
	  	String ucContent="";

	  	String read_title="";
	  	String read_content="";

	  	String read_ucContent="";

	  	logger.debug("sendCompleteTaskRemindMessage 开始取  dealNewTaskAssigneeInfos");

		 //取 新加的待办 待阅
	  	Map newTaskAssigneeMap=dealNewTaskAssigneeInfos();

	 	logger.debug("sendCompleteTaskRemindMessage 结束取dealNewTaskAssigneeInfos  ");


	 	String dealReceiveNames= newTaskAssigneeMap.get("dealReceiveNames").toString();
	  	String dealReceiveAccounts=newTaskAssigneeMap.get("dealReceiveAccounts").toString();
	  	String dealReceiveIds=newTaskAssigneeMap.get("dealReceiveIds").toString();

	  	String readReceiveNames=newTaskAssigneeMap.get("readReceiveNames").toString();
	  	String readReceiveAccounts=newTaskAssigneeMap.get("readReceiveAccounts").toString();
	  	String readReceiveIds=newTaskAssigneeMap.get("readReceiveIds").toString();

	  	String curActivityNames=newTaskAssigneeMap.get("curActivityNames").toString();

	  	String dealTaskIds=newTaskAssigneeMap.get("dealTaskIds").toString();
	  	String dealReceiveAccounts_=newTaskAssigneeMap.get("dealReceiveAccounts_").toString();

	 	String readTaskIds=newTaskAssigneeMap.get("readTaskIds").toString();
	  	String readReceiveAccounts_=newTaskAssigneeMap.get("readReceiveAccounts_").toString();

		title=getRealMessage(local,"文件办理提醒");

		read_title=getRealMessage(local,"文件办理提醒");

		content=getRealMessage(local,"新待批文件提醒")+"\n "+whir_startOrgName+" "+whir_startUserName+ ezFlow_startTime+getRealMessage(local,"提交的")+remindTitle;
	    content+=getRealMessage(local,"处于")+curActivityNames;
	    content+=getRealMessage(local,"请您处理")+"\n";


	    ucContent=whir_startOrgName+" "+whir_startUserName+ ezFlow_startTime+getRealMessage(local,"提交的")+remindTitle;
	    ucContent+=getRealMessage(local,"处于")+curActivityNames;
	    ucContent+=getRealMessage(local,"请您处理")+"\n";




	    read_content=getRealMessage(local,"新待批文件提醒")+"\n "+whir_startOrgName+" "+whir_startUserName+ ezFlow_startTime+getRealMessage(local,"提交的")+remindTitle;
	    read_content+=getRealMessage(local,"处于")+curActivityNames;
	    read_content+=getRealMessage(local,"请您审阅!")+"\n";

	    read_ucContent=whir_startOrgName+" "+whir_startUserName+ ezFlow_startTime+getRealMessage(local,"提交的")+remindTitle;
	    read_ucContent+=getRealMessage(local,"处于")+curActivityNames;
	    read_ucContent+=getRealMessage(local,"请您审阅!")+"\n";

	    if(!EzFlowUtil.judgeNull(whir_noteContent)){
	    	whir_noteContent=content;
	    	if(true){
	    		messageSettingBD messageSetting = new messageSettingBD();
		    	String domainId="0";
	    	    whir_noteContent= messageSetting.getModleContents(msgFrom, remindTitle, whir_startUserName, whir_startOrgName,domainId);
	    	}
	    }


	 	logger.debug("sendCompleteTaskRemindMessage  开始 一系列 发送   ");
	 	logger.debug("typeAction----->"+typeAction);
		if(typeAction.equals("SEND")||typeAction.equals("END")){
			 if(EzFlowUtil.judgeNull(dealReceiveAccounts)){
				 //邮件提醒
				 if(remindType.indexOf(",MAIL,")>=0){
					 //sendMail(title,content,dealReceiveNames,dealReceiveAccounts);
					 logger.debug("开始  发 邮件方法 :"+mail_account);

					 if ((this.mail_account == null || this.mail_account.equals("") || this.mail_account.equals("null"))&&false) {
						sendMail_(title, content, dealReceiveNames, dealReceiveAccounts);
					 } else {
						 String toId="";
						 String dealReceiveIdsArr[]=dealReceiveIds.split(",");
						 for(int ii=0;ii<dealReceiveIdsArr.length;ii++){
							 toId+="$"+dealReceiveIdsArr[ii]+"$";
						 }
						 logger.debug("title----->"+title);
						 logger.debug("content----->"+content);
						 logger.debug("curUserInfoVO.getUserName()----->"+curUserInfoVO.getUserName());
						 logger.debug("curUserInfoVO.getUserId()----->"+curUserInfoVO.getUserId());
						 logger.debug("toId----->"+toId);
						 logger.debug("dealReceiveNames----->"+dealReceiveNames);
						 logger.debug("mail_pass----->"+mail_pass);
						 logger.debug("mail_account----->"+mail_account);
                        /* this.sendMailNew(title, content, curUserInfoVO.getUserName(), curUserInfoVO.getUserId(),
                        		 toId, dealReceiveNames, "0",  mail_pass,  mail_account);*/
						 System.out.println("-------张飞的方法---------");
						 System.out.println("title：：："+title);
						 System.out.println("content：：："+content);
						 System.out.println("toId：：："+toId);
						 /*String mailtoIds[] = (new StringBuilder(String.valueOf(toId.substring(1)))).append("$").toString().split("\\$\\$");
						 OutMailService outService=new OutMailService();
						 for(int i = 0; i < mailtoIds.length; i++){

							 outService.insertOutMail(title,content,mailtoIds[i]);
						 }*/
						 OutMailService outService=new OutMailService();
						 System.out.println("dealReceiveAccounts_:::"+dealReceiveAccounts_);
						 if(EzFlowUtil.judgeNull(dealReceiveAccounts_)){
							 outService.getOutMailUrlAndSend(dealReceiveAccounts_,dealTaskIds,title,content,"deal",remindTitle,ucContent,synphone,mainUrl,ezflowBusinessKey);
						 }
						 System.out.println("dealReceiveAccounts_:::"+dealReceiveAccounts_);
                         System.out.println("-------张飞的方法end---------");
					 }
				 }

				/* //RTX提醒
				 if(remindType.indexOf(",RTX,")>=0){
					sendRTX(dealReceiveAccounts,title,content);
				 } */

				 //短信提醒
				 if(remindType.indexOf(",NOTE,")>=0){
					 logger.debug("  sendCompleteTaskRemindMessage  send deal  短信提醒    开始 ");
					 sendSystemMessage(curUserInfoVO,msgFrom,title,whir_noteContent,dealReceiveIds,"1");
					 logger.debug("  sendCompleteTaskRemindMessage  send deal  短信提醒    结束 ");
				 }
			 }

			 /*System.out.println("dealReceiveAccounts_:"+dealReceiveAccounts_);
			 System.out.println("remindType:"+remindType);*/

			 //反向登入改的
			 if(EzFlowUtil.judgeNull(dealReceiveAccounts_)){
				 //RTX提醒
				 if(remindType.indexOf(",RTX,")>=0){
					logger.debug("  sendCompleteTaskRemindMessage  send  deal RTX    开始 ");
					sendRTX_(dealReceiveAccounts_,dealTaskIds,title,content,"deal",remindTitle,ucContent,synphone,mainUrl,ezflowBusinessKey);
					logger.debug("  sendCompleteTaskRemindMessage  send   deal  RTX    结束 ");
				 }
			 }
			 System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>readReceiveAccounts="+readReceiveAccounts);
			 System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>EzFlowUtil.judgeNull(readReceiveAccounts)="+EzFlowUtil.judgeNull(readReceiveAccounts));
			 System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>readTaskIds="+readTaskIds);
			 if(EzFlowUtil.judgeNull(readReceiveAccounts)){
				 OutMailService outService=new OutMailService();
				 outService.getOutMailUrlAndSend(readReceiveAccounts,readTaskIds,title,read_content,"read",remindTitle,ucContent,synphone,mainUrl,ezflowBusinessKey);
				 //邮件提醒
				/* if(remindType.indexOf(",MAIL,")>=0){
					 //sendMail(title,content,readReceiveNames,readReceiveAccounts);
					 if ((this.mail_account == null
							|| this.mail_account.equals("")
							|| this.mail_account.equals("null"))&&false) {
						sendMail_(read_title, read_content, readReceiveNames, readReceiveAccounts);
					  } else {
						String toId="";
					    String dealReceiveIdsArr[]=readReceiveIds.split(",");
					    for(int ii=0;ii<dealReceiveIdsArr.length;ii++){
						   toId+="$"+dealReceiveIdsArr[ii]+"$";
					    }
                        this.sendMailNew(read_title, read_content, curUserInfoVO.getUserName(), curUserInfoVO.getUserId(),
                    		toId, readReceiveNames, "0",  mail_pass,  mail_account);
					  }
				 }*/

				/* //RTX提醒
				 if(remindType.indexOf(",RTX,")>=0){
					 sendRTX(readReceiveAccounts,title,content);
				 } */

				 //短信提醒
				 if(remindType.indexOf(",NOTE,")>=0){
					 sendSystemMessage(curUserInfoVO,msgFrom,title,whir_noteContent,readReceiveIds,"1");
				 }
			 }

			 //反向登入改的
			 if(EzFlowUtil.judgeNull(readReceiveAccounts_)){
				 //RTX提醒
				 if(remindType.indexOf(",RTX,")>=0){
					 logger.debug("  sendCompleteTaskRemindMessage  send  read RTX    开始 ");
					 sendRTX_(readReceiveAccounts_,readTaskIds,title,content,"read",remindTitle,read_ucContent,synphone,mainUrl,ezflowBusinessKey);
					 logger.debug("  sendCompleteTaskRemindMessage  send  read RTX    结束 ");
				 }
			 }
		 }

		 if(typeAction.equals("COMPLETE")){
			 if(EzFlowUtil.judgeNull(dealReceiveAccounts)){
				 title=getRealMessage(local,"您的")+remindTitle+getRealMessage(local,"已经办理完毕");
				 content=getRealMessage(local,"您的")+remindTitle+getRealMessage(local,"已经办理完毕");

				 //邮件提醒
				 if(remindType.indexOf(",MAIL,")>=0){
					  //sendMail(title,content,dealReceiveNames,dealReceiveAccounts);
					  if ((this.mail_account == null
								|| this.mail_account.equals("")
								|| this.mail_account.equals("null"))&&false) {
						  sendMail_(title,content,dealReceiveNames,dealReceiveAccounts);
						} else {
							String toId="";
						    String dealReceiveIdsArr[]=dealReceiveIds.split(",");
						    for(int ii=0;ii<dealReceiveIdsArr.length;ii++){
							   toId+="$"+dealReceiveIdsArr[ii]+"$";
						    }
	                        this.sendMailNew(title, content, curUserInfoVO.getUserName(), curUserInfoVO.getUserId(),
	                    		toId, dealReceiveNames, "0",  mail_pass,  mail_account);
						}
				 }

				 //RTX提醒
				 if(remindType.indexOf(",RTX,")>=0){
					  sendRTX(dealReceiveAccounts,title,content);
				 }

				 //短信提醒
				 if(remindType.indexOf(",NOTE,")>=0){
					  sendSystemMessage(curUserInfoVO,msgFrom,title,content,dealReceiveIds,"1");
				 }
			 }
			 //结束邮件提醒
			 if(remindType.indexOf(",COMPLETE_MAIL,")>=0){
				 title=getRealMessage(local,"您发起的")+remindTitle+getRealMessage(local,"已经办理完毕");
				 content=getRealMessage(local,"您发起的")+remindTitle+getRealMessage(local,"已经办理完毕");
				 com.whir.component.security.crypto.EncryptUtil  eutil=new  com.whir.component.security.crypto.EncryptUtil ();

			     //跳转地址
		         String p_wf_mainLinkFile=com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/ezflowopen!updateProcess.action?";
		         if(mainUrl!=null&&!mainUrl.equals("")&&!mainUrl.equals("null")){
		        	 p_wf_mainLinkFile=mainUrl;
		         }
				 if(p_wf_mainLinkFile!=null&&!p_wf_mainLinkFile.equals("")){
					if(p_wf_mainLinkFile.startsWith(com.whir.component.config.PropertiesUtil.getInstance().getRootPath())){
					}else{
						p_wf_mainLinkFile=com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+p_wf_mainLinkFile;
					}
					if(p_wf_mainLinkFile.indexOf("?")>=0){
						p_wf_mainLinkFile= p_wf_mainLinkFile+"&";
					}else{
						p_wf_mainLinkFile= p_wf_mainLinkFile+"?";
					}
				 }
				 logger.debug("mialSend p_wf_mainLinkFile： "+p_wf_mainLinkFile);
				 String  verifyCode=eutil.getSysEncoderKeyVlaue("p_wf_processInstanceId", processInstanceId, "WFDealWithAction");
				 // 表单打开地址
				 String flowurl =p_wf_mainLinkFile
							+ "p_wf_processInstanceId="
							+ processInstanceId
							+ "&p_wf_recordId=" +ezflowBusinessKey
							+ "&p_wf_openType=mailView&verifyCode="+verifyCode;

				 com.whir.common.util.SysUtils utils = new com.whir.common.util.SysUtils();
				 String[] mailRemindType = utils.getMailRemindType("0");
				 String mailremind_type = mailRemindType[0];
				 // 邮件连接内容
				 String linkcontent = content;
				 if(!mailremind_type.equals("1")&&!mailremind_type.equals("2")){
					 linkcontent = linkcontent + "</P>";
					 String uniLink = com.whir.common.util.CommonUtils.getUniUrl( flowurl);
				     linkcontent+="<a href='javascript:void(0);' onclick=\"javascript:openWin({url:'"+uniLink+"', width:800, height:600, isFull:true});\" class=\"uniLink\">查看表单</a>";
					 linkcontent = linkcontent + "</P>";
				}
				linkcontent = linkcontent +"\n";

				 logger.debug("mialSend flowurl： "+flowurl);

				 //sendMail(title,content,getRealMessage(local,"系统"),whir_startUserAccount);
				 if ((this.mail_account == null
							|| this.mail_account.equals("")
							|| this.mail_account.equals("null"))&&false) {
					  sendMail_(title,linkcontent,getRealMessage(local,"系统"),whir_startUserAccount);
				  } else {
                      this.sendMailNew(title, linkcontent, curUserInfoVO.getUserName(), curUserInfoVO.getUserId(),
              		  "$"+whir_startUserId+"$", whir_startUserName, "0",  mail_pass,  mail_account);
				  }
			 }
		 }
		 logger.debug("结束  sendCompleteTaskRemindMessage");
	}


	/**
	 * 取操作后新加的 待办  待阅
	 * @return
	 */
	public Map<String ,String> dealNewTaskAssigneeInfos(){
		Map<String ,String> resultMap=new HashMap<String ,String>();
	  	String dealReceiveNames="";
	  	String dealReceiveAccounts="";
	  	String dealReceiveIds="";

	  	String dealTaskIds="";
	  	//与dealReceiveAccounts 的区别就是   : dealReceiveAccounts是重新查一次的 顺序跟dealTaskIds不一致。
	  	String dealReceiveAccounts_="";

	  	String readTaskIds="";
	  	String readReceiveAccounts_="";

	  	String readReceiveNames="";
	  	String readReceiveAccounts="";
	  	String readReceiveIds="";

	  	String curActivityNames="";

	  	String curActivityNames_="";
		//办件
		String needDealUserAccounts="";
		//阅件
		String needReadUserAccounts="";
		// 记录  本次 新增 或者 删除的 任务的id
	    Map<String ,Object>map=Context.getCommandContext().getDbSqlSession().getTaskSessoinIds();
	  	if(map!=null){
	  		List<TaskEntity> insertList=null;
	  		if(map.get("insertObj")!=null){
	  			//新增的id 串
	  			insertList=(List<TaskEntity>)map.get("insertObj");
	  			for(TaskEntity taskEntity:insertList){
	  				//办件
	  				if(taskEntity.getWhir_isForRead()==0){
	  					needDealUserAccounts+="'"+taskEntity.getAssignee()+"',";
	  					dealReceiveAccounts_+=taskEntity.getAssignee()+",";
	  					dealTaskIds+=""+taskEntity.getId()+",";
	  					//防止一个活动显示多次
	  					if(curActivityNames_.indexOf(","+taskEntity.getName()+",")>=0){

	  					}else{
	  						curActivityNames+=taskEntity.getName()+",";
	  						curActivityNames_+=","+taskEntity.getName()+",";
	  					}

	  				}else{
	  				   //阅件
	  					System.out.println(">>>>>>>>>>2>>>>>>>>>>taskEntity.getAssignee()="+taskEntity.getAssignee());
	  					System.out.println(">>>>>>>>>>2>>>>>>>>>>getId()="+taskEntity.getId());
	  					needReadUserAccounts+="'"+taskEntity.getAssignee()+"',";
	  					readTaskIds+=""+taskEntity.getId()+",";
	  					readReceiveAccounts_+=taskEntity.getAssignee()+",";
	  					System.out.println(">>>>>>>>>>1>>>>>>>>>>readTaskIds="+readTaskIds);
	  				  	System.out.println(">>>>>>>>>>2>>>>>>>>>>readReceiveAccounts_="+readReceiveAccounts_);
	  				}
	  			}
	  		}
	  	}
	  	System.out.println(">>>>>>>>>>>>>>>>>>>>readTaskIds="+readTaskIds);
	  	System.out.println(">>>>>>>>>>>>>>>>>>>>readReceiveAccounts_="+readReceiveAccounts_);
	  	if(curActivityNames.endsWith(",")){
	  		curActivityNames=curActivityNames.substring(0,curActivityNames.length()-1);
	  	}



	  	//去掉末尾的 , 号
		if (EzFlowUtil.judgeNull(needDealUserAccounts)) {
			needDealUserAccounts=needDealUserAccounts.substring(0,needDealUserAccounts.length()-1);
		}
		if (EzFlowUtil.judgeNull(needReadUserAccounts)) {
			needReadUserAccounts=needReadUserAccounts.substring(0,needReadUserAccounts.length()-1);
		}


	  	UserInfoInterface userInfoService=Context.getCommandContext().getUserInfoInterface();
	  	List<UserInfoVO> deallist=new ArrayList<UserInfoVO>();
	  	if(EzFlowUtil.judgeNull(needDealUserAccounts)){
	  	    deallist=userInfoService.getUserInfoVOsByAccounts(needDealUserAccounts);
	  	}


	  	List<UserInfoVO> readlist=new ArrayList<UserInfoVO>();
	  	if(EzFlowUtil.judgeNull(needReadUserAccounts)){
	  	    readlist=userInfoService.getUserInfoVOsByAccounts(needReadUserAccounts);
	  	}


	  	if(deallist!=null&&deallist.size()>0){
	  		for(UserInfoVO userInfoVo:deallist){
	  			dealReceiveNames+=userInfoVo.getUserName()+",";
			  	dealReceiveAccounts+=userInfoVo.getUserAccount()+",";
			  	dealReceiveIds+=userInfoVo.getUserId()+",";
	  		}
	  		if(readReceiveNames.endsWith(",")){
	  			dealReceiveNames=dealReceiveNames.substring(0,dealReceiveNames.length()-1);
	  			dealReceiveAccounts=dealReceiveAccounts.substring(0,dealReceiveAccounts.length()-1);
	  			dealReceiveIds=dealReceiveIds.substring(0,dealReceiveIds.length()-1);
	  		}
	  	}

	  	if(readlist!=null&&readlist.size()>0){
	  		for(UserInfoVO userInfoVo:readlist){
	  			readReceiveNames+=userInfoVo.getUserName()+",";
	  			readReceiveAccounts+=userInfoVo.getUserAccount()+",";
	  			readReceiveIds+=userInfoVo.getUserId()+",";
	  		}
	  		if(readReceiveNames.endsWith(",")){
	  			readReceiveNames=readReceiveNames.substring(0,readReceiveNames.length()-1);
	  			readReceiveAccounts=readReceiveAccounts.substring(0,readReceiveAccounts.length()-1);
	  			readReceiveIds=readReceiveIds.substring(0,readReceiveIds.length()-1);
	  		}
	  	}


	  	if(dealTaskIds!=null&&dealTaskIds.endsWith(",")){
	  		dealTaskIds=dealTaskIds.substring(0,dealTaskIds.length()-1);
	  	}

	  	if(dealReceiveAccounts_!=null&&dealReceiveAccounts_.endsWith(",")){
	  		dealReceiveAccounts_=dealReceiveAccounts_.substring(0,dealReceiveAccounts_.length()-1);
	  	}


		if(readTaskIds!=null&&readTaskIds.endsWith(",")){
			readTaskIds=readTaskIds.substring(0,readTaskIds.length()-1);
	  	}

	  	if(readReceiveAccounts_!=null&&readReceiveAccounts_.endsWith(",")){
	  		readReceiveAccounts_=readReceiveAccounts_.substring(0,readReceiveAccounts_.length()-1);
	  	}

	  	resultMap.put("dealReceiveNames", dealReceiveNames);
	  	resultMap.put("dealReceiveAccounts", dealReceiveAccounts);
	  	resultMap.put("dealReceiveIds", dealReceiveIds);
	  	resultMap.put("readReceiveNames", readReceiveNames);
	  	resultMap.put("readReceiveAccounts", readReceiveAccounts);
	  	resultMap.put("readReceiveIds", readReceiveIds);
	  	resultMap.put("curActivityNames", curActivityNames);

	  	resultMap.put("dealTaskIds", dealTaskIds);
	  	resultMap.put("dealReceiveAccounts_", dealReceiveAccounts_);



	  	resultMap.put("readTaskIds", readTaskIds);
	  	resultMap.put("readReceiveAccounts_", readReceiveAccounts_);

	  	return  resultMap;
	}


	/**
	 * 取提醒的国际化 ，兼容老的版本
	 * @param key
	 * @return
	 */
	public String getRealMessage(String key){
		return getRealMessage(local,key);
	}
	/**
	 * 取提醒的国际化 ，兼容老的版本
	 * @param local
	 * @param key
	 * @return
	 */
	public String getRealMessage(String local,String key){
	    String message=key;
		if(local!=null&&!local.equals("")&&!local.equals("null")&&!local.equals("NULL")){
			if(key.equals("文件办理提醒")){
				message=Resource.getValue(local,"workflow","workflow.remind_waitingdocument");
			}
			if(key.equals("新待批文件提醒")){
				message=Resource.getValue(local,"workflow","workflow.remind_newwaitingdocument");
			}
			if(key.equals("提交的")||key.equals("发起的")){
				message=Resource.getValue(local,"workflow","workflowAnalysis.initiated");
			}
			if(key.equals("处于")){
				message=Resource.getValue(local,"workflow","workflow.remind_in");
			}
			if(key.equals("请您处理")||key.equals("请您处理")){
				message=Resource.getValue(local,"workflow","workflow.remind_pleasedeal");
			}
			if(key.equals("系统")){
				message=Resource.getValue(local,"workflow","workflow.remind_system");
			}
			if(key.equals("已经办理完毕")){
				message=Resource.getValue(local,"workflow","workflow.iscomplete");
			}
			if(key.equals("您的")){
				message=Resource.getValue(local,"workflow","workflow.you");
			}
			if(key.equals("您发起的")){
				message=Resource.getValue(local,"workflow","workflow.youstart");
			}
			if(key.equals("被")){
				message=Resource.getValue(local,"workflow","workflow.remind_backby");
			}
			if(key.equals("退回提醒")){
				message=Resource.getValue(local,"workflow","workflow.remind_backtips");
			}

			if(key.equals("退回到发起人")){
				message=Resource.getValue(local,"workflow","workflow.remind_backtoinit");
			}
			if(key.equals("退回到")){
				message=Resource.getValue(local,"workflow","workflow.remind_backto");
			}
			if(key.equals("退回")){
				message=Resource.getValue(local,"workflow","workflow.remind_back");
			}
			if(key.equals("处查阅该文件")){
				message=Resource.getValue(local,"workflow","workflow.remind_viewin");
			}
			if(key.equals("可以在")){
				message=Resource.getValue(local,"workflow","workflow.remind_canview");
			}
			if(key.equals("我的文件")){
				message=Resource.getValue(local,"filetransact","file.mydocument");
			}
			if(key.equals("待办文件")){
				message=Resource.getValue(local,"filetransact","file.underdo");
			}
			if(key.equals("处")){
				message=Resource.getValue(local,"workflow","workflow.remind_back_local");
			}
			if(key.equals("退回原因")){
				message=Resource.getValue(local,"workflow","workflow.remind_backReason");
			}
			if(key.equals("已经取消")){
				message=Resource.getValue(local,"workflow","workflow.isbecancel");
			}
			if(key.equals("取消原因")){
				message=Resource.getValue(local,"workflow","workflow.CancelReason");
			}
			if(key.equals("取消原因")){
				message=Resource.getValue(local,"workflow","workflow.CancelReason");
			}
			if(key.equals("待阅提醒")){
				message=Resource.getValue(local,"workflow","workflow.remind_viewdocument");
			}

			if(key.equals("新待阅文件提醒")){
				message=Resource.getValue(local,"workflow","workflow.remind_newviewdocument");
			}
		}
		return message;
	}


	 /**
	  * 发邮件
	  * @param title
	  * @param linkcontent
	  * @param userName
	  * @param userId
	  * @param toId
	  * @param toName
	  * @param domainId
	  * @param mail_pass
	  * @param mail_account
	  */
	public void sendMailNew(String title, String linkcontent, String userName,
			String userId, String toId, String toName, String domainId,
			String mail_pass, String mail_account) {
		logger.debug("开始  发外部邮件方法 sendMailNew   mail_account:"+mail_account);
		logger.debug("开始  发外部邮件方法 sendMailNew   mail_pass:"+mail_pass);
		logger.debug("开始  发外部邮件方法 sendMailNew   toId:"+toId);
		logger.debug("开始  发外部邮件方法 sendMailNew   toName:"+toName);
		logger.debug("开始  发外部邮件方法 sendMailNew   domainId:"+domainId);
		logger.debug("开始  发外部邮件方法 sendMailNew   userName:"+userName);
		logger.debug("开始  发外部邮件方法 sendMailNew   userId:"+userId);
		logger.debug("开始  发外部邮件方法 sendMailNew   title:"+title);
		logger.debug("开始  发外部邮件方法 sendMailNew   linkcontent:"+linkcontent);
		//InnerMailBD ebd = new InnerMailBD();
		try {
//			ebd.sendInnerMail(title, linkcontent, "1", userName, new Long(""
//					+ userId), toId, toName, domainId, mail_account, mail_pass, false);
//
//			ebd.sendInnerMail(title, linkcontent, "1", userName, new Long(""
//					+ userId), toId, toName, domainId, mail_account, mail_pass, true);

			Map _infoMap2 = new HashMap();
			_infoMap2.put("title", title);
			_infoMap2.put("linkcontent", linkcontent);
			_infoMap2.put("userName", userName);
			_infoMap2.put("userId", userId);
			_infoMap2.put("toId", toId);
			_infoMap2.put("toName", toName);
			_infoMap2.put("domainId", domainId);
			_infoMap2.put("mail_account", mail_account);
			_infoMap2.put("mail_pass", mail_pass);
			addRemindInCache("mail2", _infoMap2);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	/**
	 *
	 * 把需要发的提醒 放到MAP 里
	 * @param type
	 * @param info
	 */
	private void addRemindInCache(String type, Map info){
		CommandContext _commandContext=Context.getCommandContext();
		List<Map>  extRemindInCacheList=(List<Map>)_commandContext.getDealingProperty("remindInCache_"+type);
		if(extRemindInCacheList==null){
			extRemindInCacheList=new ArrayList();
		}
		extRemindInCacheList.add(info);
		_commandContext.addDealingProperty("remindInCache_"+type, extRemindInCacheList);
	}




	 /**
     * 从内存里 读取 需要发送的提醒
     */
	public void dealRemindInCache(CommandContext _commandContext){
		//发送 RTX
		List<Map>  rtx1List=(List<Map>)_commandContext.getDealingProperty("remindInCache_rtx1");
		if(rtx1List!=null&&rtx1List.size()>0){
			for(Map map:rtx1List){
				String receiverAccounts=map.get("receiverAccounts")==null?"":map.get("receiverAccounts").toString();
				String title=map.get("title")==null?"":map.get("title").toString();
				String text=map.get("text")==null?"":map.get("text").toString();
			    com.whir.integration.realtimemessage.Realtimemessage util=new com.whir.integration.realtimemessage.Realtimemessage();
			    util.sendNotify(receiverAccounts, title, text);
			}
		}

		//发送微信
		List<Map> sendFingerMessageList=(List<Map>)_commandContext.getDealingProperty("remindInCache_sendFingerMessage");
		if(sendFingerMessageList!=null&&sendFingerMessageList.size()>0){
			for(Map map:sendFingerMessageList){
				String receiverAccounts=map.get("receiverAccounts")==null?"":map.get("receiverAccounts").toString();
				String title=map.get("title")==null?"":map.get("title").toString();
				String text=map.get("text")==null?"":map.get("text").toString();
			    com.whir.integration.realtimemessage.Realtimemessage util=new com.whir.integration.realtimemessage.Realtimemessage();
			    util.setPushType("1");
			    util.sendFingerMessage(receiverAccounts, title, text);
			}
		}


		//
		List<Map> rtx2List=(List<Map>)_commandContext.getDealingProperty("remindInCache_rtx2");
		if(rtx2List!=null&&rtx2List.size()>0){
			for(Map map:rtx2List){
				String userName=map.get("userName")==null?"":map.get("userName").toString();
				String title=map.get("title")==null?"":map.get("title").toString();
				String ucText=map.get("ucText")==null?"":map.get("ucText").toString();
				String modelName=map.get("modelName")==null?"":map.get("modelName").toString();
				String realPassword=map.get("realPassword")==null?"":map.get("realPassword").toString();
				String toLink=map.get("toLink")==null?"":map.get("toLink").toString();
			    com.whir.integration.realtimemessage.Realtimemessage util=new com.whir.integration.realtimemessage.Realtimemessage();
			    boolean result = util.sendNotify(userName, title, ucText, title, modelName, userName, realPassword, toLink);
			}
		}


		List<Map> weixinList=(List<Map>)_commandContext.getDealingProperty("remindInCache_weixin");
		if(weixinList!=null&&weixinList.size()>0){
			for(Map map:weixinList){
				String ezflow_taskId=map.get("ezflow_taskId")==null?"":map.get("ezflow_taskId").toString();
				String userId=map.get("userId")==null?"":map.get("userId").toString();
				String remindTitle=map.get("remindTitle")==null?"":map.get("remindTitle").toString();
				//String text=map.get("text")==null?"":map.get("text").toString();
				String flowType=map.get("flowType")==null?"":map.get("flowType").toString();
                Map<String,String> params = new HashMap<String,String>();
                params.put("ezflow_taskId", ezflow_taskId);
                params.put("isezFlow", "1");
                // String flag = weixinUtil.sendMsg(userId+"", remindTitle+"", text, null, null, "workflow", params);
       		    com.whir.evo.weixin.bd.WeiXinBD  weixinbd=new  com.whir.evo.weixin.bd.WeiXinBD();
       		    String remindPre ="";
       		    logger.debug("flowType--dealRemindInCache--->"+flowType);
       		    if(flowType.equals("read")){
       		    	remindPre ="新待阅：";
       		    }else{
       		    	remindPre ="新待批：";
       		    }
   		        boolean weixinsuccess = weixinbd.sendMsg(userId,remindPre + remindTitle+"等待您的办理！","",null,null,"workflow",params);
   		        logger.debug("weixinsuccess--dealRemindInCache--->"+weixinsuccess);
			}
		}


		List<Map> mail1List = (List<Map>) _commandContext.getDealingProperty("remindInCache_mail1");
		if (mail1List != null && mail1List.size() > 0) {
			for (Map map : mail1List) {
				String mailsubject = map.get("mailsubject") == null ? "" : map.get("mailsubject").toString();
				String mailcontent = map.get("mailcontent") == null ? "" : map.get("mailcontent").toString();
				String title = map.get("title") == null ? "" : map.get("title").toString();
				String accounts = map.get("accounts") == null ? "" : map.get("accounts").toString();
				com.whir.component.mail.Mail mail = new com.whir.component.mail.Mail();
				try {
					mail.sendInnerMail(mailsubject, mailcontent, title, accounts);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		List<Map> mail2List = (List<Map>) _commandContext.getDealingProperty("remindInCache_mail2");
		if (mail2List != null && mail2List.size() > 0) {
			for (Map map : mail2List) {
				String title = map.get("title") == null ? "" : map.get("title").toString();
				String linkcontent = map.get("linkcontent") == null ? "" : map.get("linkcontent").toString();
				String userName = map.get("userName") == null ? "" : map.get("userName").toString();
				String userId = map.get("userId") == null ? "" : map.get("userId").toString();
				String toId = map.get("toId") == null ? "" : map.get("toId").toString();
				String toName = map.get("toName") == null ? "" : map.get("toName").toString();
				String domainId = map.get("domainId") == null ? "" : map.get("domainId").toString();
				String mail_account = map.get("mail_account") == null ? "" : map.get("mail_account").toString();
				String mail_pass = map.get("mail_pass") == null ? "" : map.get("mail_pass").toString();
				InnerMailBD ebd = new InnerMailBD();
				try {
					ebd.sendInnerMail(title, linkcontent, "1", userName, new Long(""+ userId), toId, toName, domainId, mail_account, mail_pass, true);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
