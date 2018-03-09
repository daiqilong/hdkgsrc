package com.whir.ezoffice.information.infomanager.actionsupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.hibernate.HibernateException;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.whir.common.util.CommonUtils;
import com.whir.common.util.UploadFile;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.security.crypto.EncryptUtil;
import com.whir.component.util.JacksonUtil;
import com.whir.ezoffice.bpm.actionsupport.BPMOpenFlowBaseAction;
import com.whir.ezoffice.bpm.bd.BPMInstanceBD;
import com.whir.ezoffice.bpm.po.BPMProcessInstancePO;
import com.whir.ezoffice.dossier.bd.DossierBD;
import com.whir.ezoffice.information.channelmanager.bd.ChannelBD;
import com.whir.ezoffice.information.channelmanager.po.InformationChannelPO;
import com.whir.ezoffice.information.common.util.InfoUtils;
import com.whir.ezoffice.information.infomanager.bd.InfoTagBD;
import com.whir.ezoffice.information.infomanager.bd.InformationAccessoryBD;
import com.whir.ezoffice.information.infomanager.bd.InformationBD;
import com.whir.ezoffice.information.infomanager.bd.NewInformationBD;
import com.whir.ezoffice.information.infomanager.ejb.InformationAccessoryEJBBean;
import com.whir.ezoffice.information.infomanager.po.InformationPO;
import com.whir.ezoffice.logon.bd.LogonBD;
import com.whir.ezoffice.relation.bd.RelationBD;
import com.whir.ezoffice.security.log.bd.LogBD;
import com.whir.govezoffice.documentmanager.bd.GovSendFileCheckWithWorkFlowBD;
import com.whir.govezoffice.documentmanager.bd.ReceiveFileBD;
import com.whir.govezoffice.documentmanager.bd.SendFileBD;
import com.whir.org.bd.organizationmanager.OrganizationBD;
import com.whir.org.manager.bd.ManagerBD;

public class InformationAction extends BPMOpenFlowBaseAction {

	/**
	 * 信息
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InformationAction.class.getName());
	private InformationBD informationBD = new InformationBD();
	private NewInformationBD newInformationBD = new NewInformationBD();
	private InformationPO information;
	private String informationId;
	private String channelId;
	private String channelName;
	private String channelType;
	private String userDefine;
	private String userChannelName;
	private String selectChannel;//新建信息时选择的栏目，格式为：channelId,channelName
	private String iso;
	private String destory;
	private String draft;
	//20161013 -by jqq 判断是否linux服务器
    private boolean isLinux = com.whir.component.util.SystemUtils.isLinux();
	
	public String getDraft() {
		return draft;
	}
	public void setDraft(String draft) {
		this.draft = draft;
	}
	public String getIso() {
		return iso;
	}
	public void setIso(String iso) {
		this.iso = iso;
	}
	public String getDestory() {
		return destory;
	}
	public void setDestory(String destory) {
		this.destory = destory;
	}
	public InformationPO getInformation() {
		return information;
	}
	public void setInformation(InformationPO information) {
		this.information = information;
	}
	public String getInformationId() {
		return informationId;
	}
	public void setInformationId(String informationId) {
		this.informationId = informationId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getUserDefine() {
		return userDefine;
	}
	public void setUserDefine(String userDefine) {
		this.userDefine = userDefine;
	}
	public String getUserChannelName() {
		return userChannelName;
	}
	public void setUserChannelName(String userChannelName) {
		this.userChannelName = userChannelName;
	}
	public String getSelectChannel() {
		return selectChannel;
	}
	public void setSelectChannel(String selectChannel) {
		this.selectChannel = selectChannel;
	}

	/**
	 * 新建信息页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String add(){
        HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
    	//取能发布的栏目
        List list = InfoUtils.getCanIssueChannel(userId, orgId, orgIdString,domainId);
        String columnJson = InfoUtils.columnListToJson(list, false);
        request.setAttribute("canIssueChannel", columnJson);
        //同时发布到
        List list1 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
        String columnJson1 = InfoUtils.columnListToJson(list1, true);
        request.setAttribute("otherChannel", columnJson1);
        //取模板
        List templateList = InfoUtils.getTemplate(userId, orgId, orgIdString, domainId);
        if (templateList != null) {
        	request.setAttribute("templateList", templateList);
        } else {
        	request.setAttribute("templateList",new java.util.ArrayList());
        }
        //收文（module：1） 发文（module：0） 工作流程（module：2）同步到信息管理
        String module = request.getParameter("module");//EncryptUtil.htmlcode(request,"module");//
        if(module!=null && !"".equals(module)){
        	request.setAttribute("fromGov", module);
        	String content = request.getParameter("_content");//EncryptUtil.htmlcode(request,"_content");//
        	request.setAttribute("content", content);
            String title = request.getParameter("_title");//EncryptUtil.htmlcode(request,"_title");//
            request.setAttribute("title", title);
            String govId = request.getParameter("_fileId");//EncryptUtil.htmlcode(request,"_fileId");//
            request.setAttribute("govId", govId);
            String author = request.getParameter("_author");//EncryptUtil.htmlcode(request,"_author");//
            request.setAttribute("author", author);
            String docNo = request.getParameter("_docNO");//EncryptUtil.htmlcode(request,"_docNO");//
            request.setAttribute("docNo", docNo);
            String accessoryName = request.getParameter("_accessName");//EncryptUtil.htmlcode(request,"_accessName");//
            request.setAttribute("infoAppendName", accessoryName);
            String infoAppendSaveName = request.getParameter("_accessSaveName");//EncryptUtil.htmlcode(request,"_accessSaveName");//
            request.setAttribute("infoAppendSaveName", infoAppendSaveName);
            if(module.equals("2")){
            	String moduleId = request.getParameter("_moduleId");//EncryptUtil.htmlcode(request,"_moduleId");//
            	request.setAttribute("moduleId", moduleId);
            	String informationType = request.getParameter("_type");//EncryptUtil.htmlcode(request,"_type");//
            	request.setAttribute("informationType", informationType);
            }
            channelType = request.getParameter("channelType");//EncryptUtil.htmlcode(request,"channelType");//
            userDefine = request.getParameter("userDefine");//EncryptUtil.htmlcode(request,"userDefine");//
            userChannelName = "信息管理";
        }
        //在需要审核的新建信息页面，选择了不需要审核的栏目时
        String channel = request.getParameter("channel");//EncryptUtil.htmlcode(request,"channel");
        String tempContent = request.getParameter("tempContent");//EncryptUtil.htmlcode(request,"tempContent");
        String reader = request.getParameter("reader");//EncryptUtil.htmlcode(request,"reader");
        String readerName = request.getParameter("readerName");//EncryptUtil.htmlcode(request,"readerName");
        String remindType = request.getParameter("remindType");//EncryptUtil.htmlcode(request,"remindType");
        String printer = request.getParameter("printer");//EncryptUtil.htmlcode(request,"printer");
        String printerName = request.getParameter("printerName");//EncryptUtil.htmlcode(request,"printerName");
        String printNum = EncryptUtil.htmlcode(request,"printNum_");//request.getParameter("printNum_");
        String downloader = request.getParameter("downloader");//EncryptUtil.htmlcode(request,"downloader");
        String downloaderName = request.getParameter("downloaderName");//EncryptUtil.htmlcode(request,"downloaderName");
        String downloadNum = EncryptUtil.htmlcode(request,"downloadNum_");//request.getParameter("downloadNum_");
        request.setAttribute("channel", channel);
        request.setAttribute("tempContent", tempContent);
        request.setAttribute("reader", reader);
        request.setAttribute("readerName", readerName);
        request.setAttribute("remindType", remindType);
        request.setAttribute("printer", printer);
        request.setAttribute("printerName", printerName);
        request.setAttribute("printNum", printNum);
        request.setAttribute("downloader", downloader);
        request.setAttribute("downloaderName", downloaderName);
        request.setAttribute("downloadNum", downloadNum);
        request.setAttribute("action", "add");
        //相关性中新建信息
        if(request.getParameter("relationNew")!=null && "1".equals(request.getParameter("relationNew"))){
        	request.setAttribute("relationNew", "1");
        }
        //是否是易播栏目
        String isyiboflag = request.getParameter("isyiboflag") != null ? request.getParameter("isyiboflag") : "0";
        request.setAttribute("isyiboflag", isyiboflag);
        /*信息公共标签查询*/
        List publicList =new ArrayList();
        InfoTagBD bd = new InfoTagBD();
        try {
            publicList = bd.getAllPublicTags();
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        request.setAttribute("publicList", publicList);
        /*信息公共标签查询*/
		return "add";
	}
	
	/**
	 * 从公文同步到信息管理
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String addOther(){
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
    	//取能发布的栏目
        List list = InfoUtils.getCanIssueChannel(userId, orgId, orgIdString,domainId);
        String columnJson = InfoUtils.columnListToJson(list, false);
        request.setAttribute("canIssueChannel", columnJson);
        //同时发布到
        List list1 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
        String columnJson1 = InfoUtils.columnListToJson(list1, true);
        request.setAttribute("otherChannel", columnJson1);
		return "addOther";
	}
	
	/**
	 * 保存信息
	 * @return
	 * @throws ParseException 
	 */
	public String save() throws ParseException{
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgName = session.getAttribute("orgName").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        String[] para = {userId, userName, orgId, orgName, orgIdString};
        //普通文本编辑方式，要处理换行
        if(information.getInformationType().equals("0")){
        	if(information.getInformationContent()!=null&&information.getInformationContent().indexOf("\n")>-1){
        		information.setInformationContent(information.getInformationContent().replaceAll("\n", "<br>"));
        	}
        }
        
        ChannelBD channelBD = new ChannelBD();
        information.setInformationChannel(channelBD.loadChannel(selectChannel.substring(0,selectChannel.indexOf(","))));
        
        information.setDomainId(Long.valueOf(domainId));
        information.setInformationIssuer(userName);
        information.setInformationIssuerId(Long.valueOf(userId));
        information.setInformationIssueOrg(orgName);
        information.setInformationIssueOrgId(orgId);
        information.setIssueOrgIdString(orgIdString);
        //information.setInformationIssueTime(new Date());
        String displayTitle = request.getParameter("information.displayTitle");
        if(displayTitle!=null && "1".equals(displayTitle)){
        	information.setDisplayTitle(0);
        }else{
        	information.setDisplayTitle(1);
        }
        String titleColor = request.getParameter("information.titleColor");
        if(titleColor!=null && "1".equals(titleColor)){
        	information.setTitleColor(1);
        }else{
        	information.setTitleColor(0);
        }
        /* 国产化支持
        if(information.getDisplayTitle()==1){
        	information.setDisplayTitle(0);
        }else{
        	information.setDisplayTitle(1);
        }*/
        if(information.getDisplayImage()==null||information.getDisplayImage().equals("")){
        	information.setDisplayImage("1");
        }else{
        	information.setDisplayImage("0");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String issueTime = request.getParameter("information.informationIssueTime");
        //20160308 -by jqq 页面发布时间默认为空，取服务器的时间
        if(request.getParameter("information.informationIssueTime")==null || "".equals(issueTime)){
        	information.setInformationIssueTime(new Date());
        }else{
        	information.setInformationIssueTime(sdf.parse(issueTime));
        }
        if(information.getInformationValidType()==1){
        	String validBeginTime = request.getParameter("information.validBeginTime") + " 00:00:00";
        	String validEndTime = request.getParameter("information.validEndTime") + " 23:59:59";
        	information.setValidBeginTime(sdf.parse(validBeginTime));
        	information.setValidEndTime(sdf.parse(validEndTime));
        }
        information.setOtherChannel(","+(information.getOtherChannel()!=null?information.getOtherChannel():"0")+",");
        /*设置信息的可查看人*/
        String informationReader = request.getParameter("informationReaderId");
        if(informationReader!=null){
        	com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationReader);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationReader(userIds);
            information.setInformationReaderOrg(orgIds);
            information.setInformationReaderGroup(groupIds);
        }else{
        	information.setInformationReaderName(information.getInformationChannel().getChannelReaderName());
        	information.setInformationReader(information.getInformationChannel().getChannelReader());
            information.setInformationReaderOrg(information.getInformationChannel().getChannelReaderOrg());
            information.setInformationReaderGroup(information.getInformationChannel().getChannelReaderGroup());
        }
        /*设置信息的可查看人*/
        
        /*提醒方式*/
        String remind_im = request.getParameter("remind_im")!=null?request.getParameter("remind_im"):"";
		String remind_sms = request.getParameter("remind_sms")!=null?request.getParameter("remind_sms"):"";
		String remind_mail = request.getParameter("remind_mail")!=null?request.getParameter("remind_mail"):"";
		String remindType = (remind_im.equals("im")?"im|":"")+(remind_sms.equals("sms")?"sms|":"")+(remind_mail.equals("mail")?"mail":"");
		remindType = remindType.endsWith("|") ? remindType.substring(0,remindType.length()-1) : remindType;
		information.setRemindType(remindType);
		/*提醒方式*/
        
		/*设置信息的可打印人*/
        String informationPrinterId = request.getParameter("informationPrinterId");
        if(informationPrinterId!=null){
        	com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationPrinterId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationPrinter(userIds);
            information.setInformationPrinterOrg(orgIds);
            information.setInformationPrinterGroup(groupIds);
        }else{
        	information.setInformationPrinterName(information.getInformationChannel().getChannelPrinterName());
        	information.setInformationPrinter(information.getInformationChannel().getChannelPrinter());
            information.setInformationPrinterOrg(information.getInformationChannel().getChannelPrinterOrg());
            information.setInformationPrinterGroup(information.getInformationChannel().getChannelPrinterGroup());
        }
        /*设置信息的可打印人*/
        
        /*设置信息的可下载人*/
        String informationDownLoaderId = request.getParameter("informationDownLoaderId");
        if(informationDownLoaderId!=null){
        	com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationDownLoaderId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationDownLoader(userIds);
            information.setInformationDownLoaderOrg(orgIds);
            information.setInformationDownLoaderGroup(groupIds);
        }else{
        	information.setInformationDownLoaderName(information.getInformationChannel().getChannelDownLoaderName());
        	information.setInformationDownLoader(information.getInformationChannel().getChannelDownLoader());
            information.setInformationDownLoaderOrg(information.getInformationChannel().getChannelDownLoaderOrg());
            information.setInformationDownLoaderGroup(information.getInformationChannel().getChannelDownLoaderGroup());
        }
        /*设置信息的可下载人*/
        
        /*信息的附件和图片*/
        String infoPicName = request.getParameter("infoPicName");
        String infoPicSaveName = request.getParameter("infoPicSaveName");
        String infoAppendName = request.getParameter("infoAppendName");
        String infoAppendSaveName = request.getParameter("infoAppendSaveName");
        String[] infoPicNames = null;
        String[] infoPicSaveNames = null;
        String[] infoAppendNames = null;
        String[] infoAppendSaveNames = null;
        if(infoPicName!=null && !infoPicName.equals("")){
        	infoPicNames = infoPicName.split("\\|");
        	infoPicSaveNames = infoPicSaveName.split("\\|");
        }
        if(infoAppendName!=null && !infoAppendName.equals("")){
        	infoAppendNames = infoAppendName.split("\\|");
        	infoAppendSaveNames = infoAppendSaveName.split("\\|");
        }
        /*信息的附件和图片*/
        //20160516 -by jqq 信息公共标签（原关键字字段）的保存
        String publicTagsId = request.getParameter("publicTagsId")==null ? "" : request.getParameter("publicTagsId");
        String publicTagsName = request.getParameter("information.informationKey")==null ? "" : request.getParameter("information.informationKey");
        //存储结构为：id1,id2,id3|name1,name2,name3
        information.setInformationKey(publicTagsId + "|" + publicTagsName);
        
        //保存信息
        Long infoId = informationBD.add(information,para, null,infoPicNames,infoPicSaveNames,infoAppendNames,infoAppendSaveNames, domainId);
        //保存标签-信息关系(公共标签)
        if(!"".equals(publicTagsId)){
            InfoTagBD bd = new InfoTagBD();
            String[] tagIdArr = publicTagsId.split(",");
            for(int i=0; i<tagIdArr.length; i++){
                try {
                    bd.saveTagRelation(Long.valueOf(tagIdArr[i]), infoId, "0");
                } catch (Exception tags_e) {
                    tags_e.printStackTrace();
                }
            }
        }
        //信息同步到EZSITE网站多站点方式
        informationBD.synchronizeToMoreSite(information, infoId, infoPicNames,infoPicSaveNames,infoAppendNames,infoAppendSaveNames,domainId, request);
        if(infoId != null){
	        //保存到全文检索
	        newInformationBD.saveInformationLucene(new String[]{""+infoId,"1","information"});
	        //保存到信息统计
	        newInformationBD.saveInfostat(infoId, information.getInformationChannel().getChannelId(),"new", new Long(orgId), orgName, new Long(userId), userName, new Long(domainId));
	        //信息推送
	        if(information.getInformationMailSendId()!=null && !"".equals(information.getInformationMailSendId())){
	        	String mail_account = com.whir.common.util.CommonUtils.getSessionUserAccount(request);
                String mail_pass = com.whir.common.util.CommonUtils.getSessionUserPassword(request);
                boolean isEncryptPass = true;
	        	try {
					informationBD.MailSendURL_Send(""+infoId, information.getInformationMailSendId(),information.getInformationMailSendName(), 
							userName, domainId, mail_account, mail_pass, isEncryptPass, remindType, userId, request);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        //相关性
	        RelationBD relationBD = new RelationBD();
            relationBD.saveRelationList(request,infoId+"");
            //公文同步
            if(information.getFromGOVDocument()!=null){
            	//从公文中发送过来的文件 需要将正文及附件拷贝至information 文件夹中
                String localPath = session.getServletContext().getRealPath("upload");//request.getRealPath("upload");
                this.copyFileFromDocument(infoAppendSaveNames, session, localPath, request, "1");
                String content = information.getInformationContent();
                if (content != null && !"null".equals(content) &&!"".equals(content)) {
                    this.copyFileFromDocument(new String[]{content + ".doc"}, session, localPath, request, "0");
                }
                //更新同步状态
                if (information.getFromGOV()!=null && information.getFromGOV()==4) {//文件送审签
                     GovSendFileCheckWithWorkFlowBD  cbd=new GovSendFileCheckWithWorkFlowBD();
                     cbd.updateSendFileCheckStatus(information.getFromGOVDocument().toString(), "1");
                } else {
                    if (information.getFromGOV()!=null && information.getFromGOV()==0) {//发文
                        SendFileBD bd = new SendFileBD();
                        bd.updateSendFileStatus(information.getFromGOVDocument().toString(), "1");
                    }else if(information.getFromGOV()!=null && information.getFromGOV()==1){//收文
                    	ReceiveFileBD bd = new ReceiveFileBD();
                    	bd.updateReceiveFileStatus(information.getFromGOVDocument().toString(), "1");
                    }
                }
            }
            //相关性中新建信息
            if(request.getParameter("relationNew")!=null && "1".equals(request.getParameter("relationNew"))){
            	printResult("success","{informationId:'"+infoId+"',informationTitle:'"+information.getInformationTitle()+"'," +
            			"informationType:'"+information.getInformationType()+"'," +
            			"channelId:'"+selectChannel.substring(0,selectChannel.indexOf(","))+"'," +
            			"userChannelName:'"+userChannelName+"',channelType:'"+channelType+"',userDefine:'"+userDefine+"'}");
            }else{
            	printResult("success");
            }
        }
        //记录日志
        LogBD logBD = new LogBD();
        logBD.log(userId, userName, orgName, "oa_information", "", new java.util.Date(), new java.util.Date(), "1",
        		information.getInformationTitle(), session.getAttribute("userIP").toString(), domainId);
		return null;
	}
	
	/**
	 * 查看信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String view(){
		String result = "";
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgName = session.getAttribute("orgName").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        //2013-09-10-----判断档案归档是否有归档模块-----start
        boolean dossierGD =false;
        DossierBD dossierBD = new DossierBD();
		List dossierGDlist = dossierBD.selectArchivesPigeonholeSet(String.valueOf(domainId));
		if(dossierGDlist !=null && dossierGDlist.size()>0){
			Object[] obj=(Object[])dossierGDlist.get(0);
			String pigeonholeSetType="";
		    if(obj[1] !=null){
		    	pigeonholeSetType = obj[1].toString();
		    }
		    //20160711 -by jqq 与信息的归档分开，单独一个标识控制，页面中在档案设置中有选项
		    String gdType = request.getParameter("gdType") != null ? request.getParameter("gdType") : "";
		    if("isoDoc".equals(gdType)){
		        if(pigeonholeSetType.indexOf("WDGL,")>=0){
	                dossierGD =true;
	            }
		    }else{
		        if(pigeonholeSetType.indexOf("ZSGL,")>=0){
	                dossierGD =true;
	            }
		    }
		}
		request.setAttribute("dossierGD", dossierGD);
        //2013-09-10-----判断档案归档是否有归档模块-----end
		if(informationId!=null && !"".equals(informationId) && !"null".equals(informationId)){
			information = informationBD.load(informationId);
		}
        
        if(information==null){
        	return "noInfo";
        }
        if(channelId==null || channelId.equals("") || "null".equals(channelId)){
        	channelId = information.getInformationChannel().getChannelId().toString();
        }
        /*取栏目名称串*/
        ChannelBD channelBD = new ChannelBD();
        String channelNameString = "";
        try {
        	channelNameString = informationBD.getChannelNameString(informationId);
		} catch (Exception e) {
			e.printStackTrace();
		}
        request.setAttribute("channelNameString", channelNameString);
        /*取栏目名称串*/
        
        //20151116 -by jqq 页面返回该信息点赞的次数用以展示
        /*取点赞总数*/
        String praiseNum = information.getPraiseNum() != null ? information.getPraiseNum() : "0";
        request.setAttribute("praiseNum", praiseNum);
        /*取点赞总数*/
        /*判断用户是否已点赞*/
        String haspraise = "0";
        try {
			haspraise = informationBD.hasPraise(userId, informationId);
		} catch (Exception praise_e) {
			//logger.debug("========判断用户是否已点赞异常:" + praise_e.getMessage());
			praise_e.printStackTrace();
		}
		request.setAttribute("haspraise", haspraise);
		/*判断用户是否已点赞*/
		
		/*统计信息阅读总数 20151215 -by jqq*/
        int num_tmp = information.getInformationKits() + 1;
        String viewnum =  Integer.toString(num_tmp);
        request.setAttribute("viewnum", viewnum);
		/*统计信息阅读总数*/
		
        /*评论次数*/
        List commentList = informationBD.getComment(informationId);
        request.setAttribute("commentNum", commentList!=null?commentList.size():"0");
        /*评论次数*/
        //评论使用头像
        com.whir.org.bd.usermanager.UserBD _ubd = new com.whir.org.bd.usermanager.UserBD();
        String userphoto = "";
        try {
            String fileServer = com.whir.component.config.ConfigReader.getFileServer(request.getRemoteAddr());
            userphoto = _ubd.getUserPhoto(userId);
            String empLivingImgUrl = userphoto == null ? "" : userphoto;
            userphoto = fileServer + ajustLivingImgUrl(empLivingImgUrl);
        } catch (Exception e_photo) {
            e_photo.printStackTrace();
        }
        request.setAttribute("userphoto", userphoto);
        /*信息个人标签查询*/
        List personalList =new ArrayList();
        List allPersonalList =new ArrayList();
        InfoTagBD bd = new InfoTagBD();
        try {
            allPersonalList = bd.getAllPersonalTags(userId);
            personalList = bd.getPersonalTagsByInfoId(informationId,userId);
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        //当前信息已经绑定的个人标签
        request.setAttribute("personalList", personalList);
        //当前用户所有自定义的个人标签，用于新增信息标签时候选择
        request.setAttribute("allPersonalList", allPersonalList);
        /*信息个人标签查询*/
        /*信息公共标签查询*/
        List publicList =new ArrayList();
        try {
            publicList = bd.getPublicTagsByInfoId(informationId);
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        request.setAttribute("publicList", publicList);
        /*信息公共标签查询*/
        
        /*判断用户是否有栏目的维护权限*/
        boolean canVindicate = channelBD.canVindicate(userId, orgId, channelId);
        //Boolean canVindicate = informationBD.vindicateInfo(userId, orgId, informationId);
        request.setAttribute("canVindicate", canVindicate);
        /*判断用户是否有栏目的维护权限*/
        
        /*判断用户是否有删除评论的权限*/
		if (!canVindicate) {
			com.whir.org.manager.bd.ManagerBD mbd = new com.whir.org.manager.bd.ManagerBD();
			List rightList = mbd.getRightScope(userId, "01*03*03");
			//用户可以修改的信息ID串
			String canModiIds = informationBD.getInformationModiIds(channelId, userId, orgId, orgIdString, informationId, rightList);
			if (canModiIds.indexOf("," + informationId + ",") >= 0) {
				canVindicate = true;
			}
		}
		request.setAttribute("delComment", canVindicate ? "1" : "0");
		/*判断用户是否有删除评论的权限*/
		
		/*取信息*/
		String informationType = request.getParameter("informationType");
		String content = informationBD.getContent(informationId);
		//content = content.replaceAll("\n", "<br>");
		//content = content.replaceAll("\r", "<br>");
		//2013-09-03-----start
		//if(informationType.equals("1")){
		//	content = content.replaceAll("\n", "<br>");
		//}
		//2013-09-03-----end
		if (informationType.equals("2")) {//地址链接
			if(content!=null && !content.equals("")){
				request.setAttribute("midType", "url");
				result = "url";
			}
        } else {
        	if (informationType.equals("3")){//文件链接
        		if(content!=null && !content.equals("")){
            		String[] tmp2 = content.toString().split(":");
                    request.setAttribute("fileLink", "fileLink");
                    String fileName = "";
                    String saveName = "";
                    if (tmp2.length == 2) {
                    	fileName = tmp2[0].toString();
                    	saveName = tmp2[1].toString();
                    	UploadFile uploadFile = new UploadFile();
                    	String isEncrypt = uploadFile.getFileEncrypt(saveName);
                    	try {
                    		if(isEncrypt.equals("1")){
    	                    	String localPath = session.getServletContext().getRealPath("upload");
    	                    	File file = new File(localPath+"/information/"+saveName);
    	                    	File decodefile = new File(localPath+"/information/"+saveName.substring(0,saveName.indexOf("."))
    	                    			+"-decode."+saveName.substring(saveName.indexOf(".")+1).toUpperCase());
    	                    	if(!file.exists()){
    	                    		file = new File(localPath+"/information/"+saveName.substring(0,6)+"/"+saveName);
    	                    		decodefile = new File(localPath+"/information/"+saveName.substring(0,6)+"/"+saveName.substring(0,saveName.indexOf("."))
    		                    			+"-decode."+saveName.substring(saveName.indexOf(".")+1).toUpperCase());
    	                    	}
    	                    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
    							BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(decodefile.getAbsolutePath()));
    				            byte[] buf = new byte[8192];
    				            int n = -1;
    			                while( -1 != (n = in.read(buf, 0, buf.length))) {
    			                    for(int i0 = 0; i0 < n; i0++) {
    			                        buf[i0] = (byte)(buf[i0] + 1);
    			                    }
    			                    out.write(buf, 0, n);
    			                }
    			                out.flush();
    			                saveName = saveName.substring(0,saveName.indexOf(".")) + "-decode." + saveName.substring(saveName.indexOf(".")+1).toUpperCase();
                        	}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("解密文件失败！");
						}
                        request.setAttribute("fileName", fileName);
                        request.setAttribute("saveName", saveName);
                        request.setAttribute("fileType",saveName.substring(saveName.indexOf(".")+1).toUpperCase());
                    }
            	}
        	} else {
	        	/*附件和图片*/
	            InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
	            List list = informationAccessoryBD.getAccessory(informationId);
	            String infoPicName = "";
	            String infoPicSaveName = "";
	            String infoAppendName = "";
	            String infoAppendSaveName = "";
	            Object[] tmp = null;
	            if (list != null) {
	                for (int i = 0; i < list.size(); i++) {
	                    tmp = (Object[]) list.get(i);
	                    if(tmp!=null && tmp[1]!=null){
		                    if (tmp[4].toString().equals("1")) {
		                    	infoPicName += tmp[1].toString()+"|";
		                    	infoPicSaveName += tmp[2].toString()+"|";
		                    } else {
	                    		infoAppendName += tmp[1].toString()+"|";
		                    	infoAppendSaveName += tmp[2].toString()+"|";
		                    }
	                    }
	                }
	            }
	            if(infoPicName.endsWith("|")){
	            	infoPicName = infoPicName.substring(0,infoPicName.length()-1);
	            	infoPicSaveName = infoPicSaveName.substring(0,infoPicSaveName.length()-1);
	            }
	            if(infoAppendName.endsWith("|")){
	            	infoAppendName = infoAppendName.substring(0,infoAppendName.length()-1);
	            	infoAppendSaveName = infoAppendSaveName.substring(0,infoAppendSaveName.length()-1);
	            }
	            request.setAttribute("infoPicName", infoPicName);
	            request.setAttribute("infoPicSaveName", infoPicSaveName);
	            request.setAttribute("infoAppendName", infoAppendName);
	            request.setAttribute("infoAppendSaveName", infoAppendSaveName);
	            //20151218 -by jqq word/excel/ppt编辑类型信息，展示上传的文件
	            request.setAttribute("infoPicName2", infoPicName);
	            request.setAttribute("infoPicSaveName2", infoPicSaveName);
	        	/*取附件和图片*/
        	}
        	result = "view";
        }
		request.setAttribute("content", content);
		/*文档管理查看页面-相关信息*/
		List assoicateInfoList = new ArrayList();
		//2013-09-03-----start
		//logger.debug("channelType----->"+channelType);
		//logger.debug("userDefine----->"+userDefine);
		if(CommonUtils.isEmpty(userDefine)){
			userDefine ="0";
		}
		if(CommonUtils.isEmpty(channelType)){
			channelType ="0";
		}
		//2013-09-03-----end
		assoicateInfoList = informationBD.getAssociateInfo(orgId, informationId, userId, orgIdString, channelType, userDefine, "2");
		if(assoicateInfoList!=null && assoicateInfoList.size()>0){
			request.setAttribute("assoicateInfo", assoicateInfoList);
		}
        /*文档管理查看页面-相关信息*/
		ManagerBD managerBD = new ManagerBD();
		/*判断用户是否有打印权限*/
		String canPrint = "";
		String printRight = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, 
				"aaa.informationPrinter","aaa.informationPrinterOrg", "aaa.informationPrinterGroup");
		//logger.debug("printRight----->"+printRight);
		canPrint = informationBD.hasRight(informationId, printRight + " or aaa.informationPrinterName is null or aaa.informationPrinterName = ''");
		//logger.debug("canPrint----->"+canPrint);
		request.setAttribute("canPrint", canPrint);
		/*判断用户是否有打印权限*/
        //20160718 -by jqq 有打印权限，判断打印次数是否用完，如果是，则无打印权限
		try {
            if("1".equals(canPrint)){
                /*判断用户打印次数，若可以打印则返回1,否则返回0*/
                String printNumFlag = informationBD.judgePrintNum(userId,informationId);
                if("0".equals(printNumFlag)){
                    request.setAttribute("canPrint", "0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		//20160520 -by jqq 打印预览 改造
		String printPreview = request.getParameter("printPreview")==null ? "0" :request.getParameter("printPreview");
		if("1".equals(printPreview)){
		    return "printPreview";
		}
		
		/*判断用户是否有下载权限
		String canDownload = "";
		String downloadRight = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, 
				"aaa.informationDownLoader","aaa.informationDownLoaderOrg", "aaa.informationDownLoaderGroup");
		canDownload = informationBD.hasRight(informationId, downloadRight + " or aaa.informationDownLoaderName is null or aaa.informationDownLoaderName = ''");
		request.setAttribute("canDownload", canDownload);
		判断用户是否有下载权限*/
		
		/*保存用户阅读情况（只保存用户的本身组织，不关注其兼职组织）*/
		String userAccount = session.getAttribute("userAccount").toString();
        HashMap userInfo = new LogonBD().logon(userAccount, "", request.getRemoteAddr(), "whir", "0");
        String userOrgId = userInfo.get("orgId")!=null?userInfo.get("orgId").toString():"";
        String userOrgName = userInfo.get("orgName")!=null?userInfo.get("orgName").toString():"";
        String userOrgIdString = userInfo.get("orgIdString")!=null?userInfo.get("orgIdString").toString():"";
        if(!userOrgId.equals("")){//超级管理员没有组织，不保存到阅读情况中
        	informationBD.setBrowser(userId, userName, userOrgId, userOrgName, informationId, userOrgIdString);
        	//20151215 -by jqq 信息查看信息保存流水记录
        	try {
				informationBD.saveViewRecord(userId, userName, userOrgId, userOrgName, informationId);
			} catch (Exception record_e) {
				//logger.debug("========信息查看保存流水记录saveViewRecord异常：" + record_e.getMessage());
				record_e.printStackTrace();
			}
        }
        /*保存用户阅读情况（只保存用户的本身组织，不关注其兼职组织）*/
        
        /*信息点击数+1*/
        if(request.getParameter("kits")==null || !"1".equals(request.getParameter("kits"))){
        	informationBD.setKits(informationId);
        }
        /*信息点击数+1*/
        
        /*插入信息统计数据*/
        new NewInformationBD().saveInfostat(new Long(informationId), new Long(channelId), "view", new Long(orgId), orgName,
                new Long(userId), userName, new Long(domainId));
        /*插入信息统计数据*/
        
		return result;
	}
	
	/**
	 * 修改信息页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String load(){
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        //取模板
        List templateList = InfoUtils.getTemplate(userId, orgId, orgIdString, domainId);
        if (templateList != null) {
        	request.setAttribute("templateList", templateList);
        } else {
        	request.setAttribute("templateList",new java.util.ArrayList());
        }
        //附件和图片
        InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
        List list = informationAccessoryBD.getAccessory(informationId);
        String infoPicName = "";
        String infoPicSaveName = "";
        String infoAppendName = "";
        String infoAppendSaveName = "";
        Object[] tmp = null;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                tmp = (Object[]) list.get(i);
                if (tmp[4].toString().equals("1")) {
                	infoPicName += tmp[1].toString()+"|";
                	infoPicSaveName += tmp[2].toString()+"|";
                } else {
                	infoAppendName += tmp[1].toString()+"|";
                	infoAppendSaveName += tmp[2].toString()+"|";
                }
            }
        }
        if(infoPicName.endsWith("|")){
        	infoPicName = infoPicName.substring(0,infoPicName.length()-1);
        	infoPicSaveName = infoPicSaveName.substring(0,infoPicSaveName.length()-1);
        }
        if(infoAppendName.endsWith("|")){
        	infoAppendName = infoAppendName.substring(0,infoAppendName.length()-1);
        	infoAppendSaveName = infoAppendSaveName.substring(0,infoAppendSaveName.length()-1);
        }
        request.setAttribute("infoPicName", infoPicName);
        request.setAttribute("infoPicSaveName", infoPicSaveName);
        request.setAttribute("infoAppendName", infoAppendName);
        request.setAttribute("infoAppendSaveName", infoAppendSaveName);
        information = informationBD.load(informationId);
        //修改信息时，给信息的正在编辑用户赋值，其他用户将不能编辑
        if(information.getEditUserName()==null || "".equals(information.getEditUserName())){
        	information.setEditUserId(userId);
        	information.setEditUserName(userName);
        	informationBD.save(information);
        }
        //提醒方式
        String remindType = information.getRemindType();
        request.setAttribute("remindType", remindType);
        //可查看人
        String informationReaderId = (information.getInformationReader()!=null ? information.getInformationReader() : "")
        	+ (information.getInformationReaderOrg()!=null ? information.getInformationReaderOrg() : "")
        	+ (information.getInformationReaderGroup()!=null ? information.getInformationReaderGroup() : "");
        request.setAttribute("informationReaderId", informationReaderId);
        
        //20160419 -by jqq 信息草稿，对应栏目可能为空
        String informationReaderId_ = "";
        String informationPrinterId_ = "";
        String informationDownLoaderId_ = "";
        if(information.getInformationChannel() != null){
            //信息可查看人选择范围
            informationReaderId_ = (information.getInformationChannel().getChannelReader()!=null ? information.getInformationChannel().getChannelReader() : "")
            + (information.getInformationChannel().getChannelReaderOrg()!=null ? information.getInformationChannel().getChannelReaderOrg() : "")
            + (information.getInformationChannel().getChannelReaderGroup()!=null ? information.getInformationChannel().getChannelReaderGroup() : "");
            //信息可打印人选择范围
            informationPrinterId_ = (information.getInformationChannel().getChannelPrinter()!=null ? information.getInformationChannel().getChannelPrinter() : "")
            + (information.getInformationChannel().getChannelPrinterOrg()!=null ? information.getInformationChannel().getChannelPrinterOrg() : "")
            + (information.getInformationChannel().getChannelPrinterGroup()!=null ? information.getInformationChannel().getChannelPrinterGroup() : "");
            //信息可下载人选择范围
            informationDownLoaderId_ = (information.getInformationChannel().getChannelDownLoader()!=null ? information.getInformationChannel().getChannelDownLoader() : "")
            + (information.getInformationChannel().getChannelDownLoaderOrg()!=null ? information.getInformationChannel().getChannelDownLoaderOrg() : "")
            + (information.getInformationChannel().getChannelDownLoaderGroup()!=null ? information.getInformationChannel().getChannelDownLoaderGroup() : "");
        }
        request.setAttribute("informationReaderId_", informationReaderId_);
        
        //信息可打印人
        String informationPrinterId = (information.getInformationPrinter()!=null ? information.getInformationPrinter() : "")
	    	+ (information.getInformationPrinterOrg()!=null ? information.getInformationPrinterOrg() : "")
	    	+ (information.getInformationPrinterGroup()!=null ? information.getInformationPrinterGroup() : "");
        request.setAttribute("informationPrinterId", informationPrinterId);
        request.setAttribute("informationPrinterId_", informationPrinterId_);
        
        //信息可下载人
        String informationDownLoaderId = (information.getInformationDownLoader()!=null ? information.getInformationDownLoader() : "")
			+ (information.getInformationDownLoaderOrg()!=null ? information.getInformationDownLoaderOrg() : "")
			+ (information.getInformationDownLoaderGroup()!=null ? information.getInformationDownLoaderGroup() : "");
		request.setAttribute("informationDownLoaderId", informationDownLoaderId);
		request.setAttribute("informationDownLoaderId_", informationDownLoaderId_);
        
        //细览不显示标题（兼容老数据）
        if(information.getDisplayTitle()==0){
    		information.setDisplayTitle(1);
    	}else{
    		information.setDisplayTitle(0);
    	}
        //细览不显示图片（兼容老数据）
        //20151216 -by jqq 修复当DisplayImage在数据库中为空时候报异常的问题
    	if(information.getDisplayImage() != null && information.getDisplayImage().equals("1")){
    		information.setDisplayImage("0");
    	}else{
    		information.setDisplayImage("1");
    	}
        //修改时取用户可新增的信息栏目+当前信息栏目,如果用户可新增的信息栏目包含当前信息栏目则不加
    	//20160419 -by jqq 信息草稿，对应栏目可能为空
    	String channel = "";
    	if(information.getInformationChannel() != null){
    	    channel = information.getInformationChannel().getChannelId()+","+information.getInformationChannel().getChannelName();
    	}
    	String otherChannel = information.getOtherChannel();
        List canIssueChannel = InfoUtils.getCanIssueChannel(userId, orgId, orgIdString,domainId);
        boolean include = false;
        for(int i=0;i<canIssueChannel.size();i++){
        	Object[] ch = (Object[]) canIssueChannel.get(i);
        	if(information.getInformationChannel() != null){
        	    if((information.getInformationChannel().getChannelId().toString()).equals(ch[0].toString())){
                    include = true;
                    break;
                }
            }
        }
    	if(!include){
    	    if(information.getInformationChannel() != null){
    	        Object[] obj = new Object[]{information.getInformationChannel().getChannelId(),
                        information.getInformationChannel().getChannelName(),
                        userChannelName+"."+information.getInformationChannel().getChannelNameString(),
                        information.getInformationChannel().getChannelType(),
                        information.getInformationChannel().getUserDefine(),
                        information.getInformationChannel().getChannelLevel()};
                canIssueChannel.add(obj);
            }
    	 }
        //修改时取用户可新增的信息栏目+当前信息栏目,如果用户可新增的信息栏目包含当前信息栏目则不加
        String columnJson = InfoUtils.columnListToJson(canIssueChannel, false);
        request.setAttribute("canIssueChannel", columnJson);
        //同时发布到
        List list1 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
        String columnJson1 = InfoUtils.columnListToJson(list1, true);
        request.setAttribute("otherChannel", columnJson1);
        //普通文本编辑的信息，要处理换行
        if(information.getInformationType().equals("0")){
	        if(information.getInformationContent()!=null&&information.getInformationContent().indexOf("<br>")>-1){
	        	information.setInformationContent(information.getInformationContent().replaceAll("<br>", "\n"));
	        }
        }
        if(information.getInformationType().equals("3")){//文件链接
        	String[] file = information.getInformationContent().split(":");
        	String fileLinkContent = file[0];
        	String fileLinkContentHidd = file[1];
        	request.setAttribute("fileLinkContent", fileLinkContent);
        	request.setAttribute("fileLinkContentHidd", fileLinkContentHidd);
        } else if (information.getInformationType().equals("2")){//地址链接
        	request.setAttribute("urlContent", information.getInformationContent());
        } else if (information.getInformationType().equals("0")){//普通编辑
        	request.setAttribute("textContent", information.getInformationContent());
        } else if (information.getInformationType().equals("4") || information.getInformationType().equals("5") || information.getInformationType().equals("6")){//word或excel或ppt编辑
        	/*信息重新生成金格文档及金格Id*/
            //201604113 -by jqq 11.4.0.8:信息用word编辑方式编辑保存的，再保存一次信息后，附件的大小显示异常
            /*String newContent = newInformationBD.copeJingeDocument(information.getInformationContent(), information.getInformationType());
            String fileType = ".doc";
            if (information.getInformationType().equals("5")) {
            	fileType = ".xls";
            } else if (information.getInformationType().equals("6")) {
            	fileType = ".ppt";
            }
            String oldFile = information.getInformationContent() + fileType;

            if (newContent != null && !newContent.equals("-1") && !newContent.equals("")) {
            	information.setInformationContent(newContent);
            }
            String localPath = session.getServletContext().getRealPath("upload");
            this.copyFileFromInformation(oldFile, newContent+fileType, session, localPath, request);*/
    		/*信息重新生成金格文档及金格Id*/
        	request.setAttribute("content", information.getInformationContent());
        }
        request.setAttribute("channel", channel);
        request.setAttribute("other", otherChannel);
        request.setAttribute("action", "load");
        //易播栏目改造，返回是否是易播栏目的标识
        String isYiBoChannel = "";
        if(information.getInformationChannel() != null){
            isYiBoChannel = information.getInformationChannel().getIsYiBoChannel();
        }
        request.setAttribute("isYiBoChannel",isYiBoChannel);
        /*信息公共标签查询*/
        List publicList =new ArrayList();
        List pubtagList =new ArrayList();
        InfoTagBD bd = new InfoTagBD();
        try {
            publicList = bd.getAllPublicTags();
            //20160516 -by jqq 该信息的公共标签（原关键字）
            pubtagList = bd.getPublicTagsByInfoId(informationId);
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        request.setAttribute("publicList", publicList);
        /*信息公共标签查询*/
        //20160531 -by jqq 每次load公共标签查询获取，不存储在信息表
        if(pubtagList != null && pubtagList.size() > 0){
            String ptagsId = "";
            String ptagsName = "";
            for(int k=0; k<pubtagList.size(); k++){
                Object[] obj = (Object[]) pubtagList.get(k);
                ptagsId += obj[0].toString() + ",";
                ptagsName += obj[1].toString() + ",";
            }
            request.setAttribute("publicTagsId",ptagsId.substring(0, ptagsId.length()-1));
            information.setInformationKey(ptagsName.substring(0, ptagsName.length()-1));
        }else{
            request.setAttribute("publicTagsId","");
            information.setInformationKey("");
        }
        /*String publictags = information.getInformationKey()==null ? "|" : information.getInformationKey();
        String[] ptags = publictags.split("\\|");
        if(ptags.length > 0){
         }else{
            request.setAttribute("publicTagsId","");
            information.setInformationKey("");
        }*/
        
		return "load";
	}
	
	/**
	 * 清空信息的正在编辑用户
	 * @return
	 */
	public String removeEditUser(){
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String editUserId = request.getParameter("editUserId");
        if(editUserId!=null && !"".equals(editUserId) && editUserId.equals(userId)){
        	information = informationBD.load(informationId);
        	information.setEditUserId("");
        	information.setEditUserName("");
        	informationBD.save(information);
        }
		return null;
	}
	
	/**
	 * 更新信息
	 * @return
	 * @throws ParseException 
	 */
	public String update() throws ParseException{
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        /*设置信息的可查看人*/
        String informationReader = request.getParameter("informationReaderId");
        if(informationReader!=null){
        	com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationReader);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationReader(userIds);
            information.setInformationReaderOrg(orgIds);
            information.setInformationReaderGroup(groupIds);
        }else{
        	information.setInformationReaderName(information.getInformationChannel().getChannelReaderName());
        	information.setInformationReader(information.getInformationChannel().getChannelReader());
            information.setInformationReaderOrg(information.getInformationChannel().getChannelReaderOrg());
            information.setInformationReaderGroup(information.getInformationChannel().getChannelReaderGroup());
        }
        /*设置信息的可查看人*/
        
        /*设置信息的可打印人*/
        String informationPrinterId = request.getParameter("informationPrinterId");
        if(informationPrinterId!=null){
        	com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationPrinterId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationPrinter(userIds);
            information.setInformationPrinterOrg(orgIds);
            information.setInformationPrinterGroup(groupIds);
        }else{
        	information.setInformationPrinterName(information.getInformationChannel().getChannelPrinterName());
        	information.setInformationPrinter(information.getInformationChannel().getChannelPrinter());
            information.setInformationPrinterOrg(information.getInformationChannel().getChannelPrinterOrg());
            information.setInformationPrinterGroup(information.getInformationChannel().getChannelPrinterGroup());
        }
        /*设置信息的可打印人*/
        
        /*设置信息的可下载人*/
        String informationDownLoaderId = request.getParameter("informationDownLoaderId");
        if(informationDownLoaderId!=null){
        	com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationDownLoaderId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationDownLoader(userIds);
            information.setInformationDownLoaderOrg(orgIds);
            information.setInformationDownLoaderGroup(groupIds);
        }else{
        	information.setInformationDownLoaderName(information.getInformationChannel().getChannelDownLoaderName());
        	information.setInformationDownLoader(information.getInformationChannel().getChannelDownLoader());
            information.setInformationDownLoaderOrg(information.getInformationChannel().getChannelDownLoaderOrg());
            information.setInformationDownLoaderGroup(information.getInformationChannel().getChannelDownLoaderGroup());
        }
        /*设置信息的可下载人*/
        
        /*提醒方式*/
        String remind_im = request.getParameter("remind_im")!=null?request.getParameter("remind_im"):"";
		String remind_sms = request.getParameter("remind_sms")!=null?request.getParameter("remind_sms"):"";
		String remind_mail = request.getParameter("remind_mail")!=null?request.getParameter("remind_mail"):"";
		String remindType = (remind_im.equals("im")?"im|":"")+(remind_sms.equals("sms")?"sms|":"")+(remind_mail.equals("mail")?"mail":"");
		remindType = remindType.endsWith("|") ? remindType.substring(0,remindType.length()-1) : remindType;
		information.setRemindType(remindType);
		/*提醒方式*/
		//20160516 -by jqq 信息公共标签
		String publicTagsId = request.getParameter("publicTagsId")==null ? "" : request.getParameter("publicTagsId");
        String publicTagsName = request.getParameter("information.informationKey")==null ? "" : request.getParameter("information.informationKey");
        //存储结构为：id1,id2,id3|name1,name2,name3
        information.setInformationKey(publicTagsId + "|" + publicTagsName);
        informationId = information.getInformationId().toString();
        //20160608 -by jqq 草稿页面发布信息，属于第一次信息发布，不保存历史版本
        if(information.getInformationStatus() != 9){
            //保存历史版本
            informationBD.saveHistory(informationId);
        }
        String channelId = request.getParameter("selectChannel").substring(0, request.getParameter("selectChannel").indexOf(","));
        if(information.getInformationType().equals("0")){
        	if(information.getInformationContent().indexOf("\n")>-1){
            	information.setInformationContent(information.getInformationContent().replaceAll("\n", "<br>"));
            }
        }
        String displayTitle = request.getParameter("information.displayTitle");
        if(displayTitle!=null && "1".equals(displayTitle)){
        	information.setDisplayTitle(0);
        }else{
        	information.setDisplayTitle(1);
        }
        String titleColor = request.getParameter("information.titleColor");
        if(titleColor!=null && "1".equals(titleColor)){
        	information.setTitleColor(1);
        }else{
        	information.setTitleColor(0);
        }
        if(information.getDisplayImage()==null||information.getDisplayImage().equals("")){
        	information.setDisplayImage("1");
        }else{
        	information.setDisplayImage("0");
        }
        String validBeginTime = request.getParameter("information.validBeginTime");
        String validEndTime = request.getParameter("information.validEndTime");
        if(validBeginTime!=null && !"".equals(validBeginTime)){
        	validBeginTime = validBeginTime + " 00:00:00";
        	validEndTime = validEndTime + " 23:59:59";
        }else{
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	validBeginTime = sdf.format(new Date()) + " 00:00:00";
        	validEndTime = sdf.format(new Date()) + " 23:59:59";
        }
        //信息草稿页面保存时，创建时间/修改时间/信息状态 修改
        String issueTime = request.getParameter("information.informationIssueTime");
        if(information.getInformationStatus() == 9){
            if(issueTime==null || "".equals(issueTime)){
                information.setInformationIssueTime(new Date());
            }else{
                //20160721 -by jqq 草稿直接保存发布信息，发布时间取页面值
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                information.setInformationIssueTime(sdf2.parse(issueTime));
            }
            information.setInformationModifyTime(null);
            information.setInformationStatus(0);
        }
        String[] parameters = {information.getInformationTitle(),
    		information.getInformationSubTitle(),
    		information.getInformationSummary(),
    		information.getInformationKey(),
    		information.getInformationContent(),
            userId,
            userName,
            orgName,
            information.getInformationReaderName(),
            information.getInformationReader(),
            information.getInformationReaderOrg(),
            information.getInformationReaderGroup(),
            information.getInformationValidType()+"",
            validBeginTime,//information.getValidBeginTime()!=null ? sdf.format(information.getValidBeginTime()) + " 00:00:00" : sdf.format(new Date()) + " 00:00:00",
            validEndTime,//information.getValidEndTime()!=null ? sdf.format(information.getValidEndTime()) + " 23:59:59" : sdf.format(new Date()) + " 23:59:59",
            information.getInformationHead()+"",
            information.getInformationHeadFile(),
            information.getInformationSeal(),
            information.getInformationMark(),
            information.getInfoRedIssueOrg(),
            information.getInfoRedIssueTime(),
            information.getInformationHeadId()!=null ? information.getInformationHeadId().toString() : "0",
            information.getInformationSealId()!=null ? information.getInformationSealId().toString() : "0",
            information.getTransmitToEzsite()+"",
            information.getForbidCopy()+"",
            information.getOrderCode(),
            information.getDisplayTitle()+"",
            ","+(information.getOtherChannel()!=null?information.getOtherChannel():"0")+",",
            information.getInformationAuthor(),
            request.getParameter("information.informationIssueTime"),
            information.getTitleColor()!=null ? information.getTitleColor()+"" : "0",
            channelId,
            information.getMustRead()==null || "".equals(information.getMustRead()) ? "0" : Integer.toString(information.getMustRead()),
            information.getComeFrom(),
            information.getIsConf()+"0",
            information.getDocumentNo(),
            information.getDocumentEditor(),
            information.getDocumentType(),
            information.getDisplayImage(),
            information.getWordDisplayType(),
            orgId,
            information.getInformationOrISODoc(),
            information.getIsoDocStatus(),
            information.getIsoOldInfoId(),
            information.getIsoSecretStatus(),
            information.getIsoDealCategory(),
            information.getIsoApplyName(),
            information.getIsoApplyId(),
            information.getIsoReceiveName(),
            information.getIsoReceiveId(),
            information.getIsoModifyReason(),
            information.getIsoAmendmentPage(),
            information.getIsoModifyVersion(),
            userName,
            orgName,
            information.getInformationCanRemark().toString(),
            information.getSiteChannelString(),
            information.getInformationMailSendId(),
            information.getInformationMailSendName(),
            information.getInformationMailSendUserId(),
            information.getInformationMailSendOrg(),
            information.getInformationMailSendGroup(),
            information.getRemindType(),
            information.getInformationPrinterName(),
            information.getInformationPrinter(),
            information.getInformationPrinterOrg(),
            information.getInformationPrinterGroup(),
            information.getPrintNum(),
            information.getInformationDownLoaderName(),
            information.getInformationDownLoader(),
            information.getInformationDownLoaderOrg(),
            information.getInformationDownLoaderGroup(),
            information.getDownLoadNum(),
            information.getInformationStatus()+""
        };
        /*信息的附件和图片*/
        String infoPicName = request.getParameter("infoPicName");
        String infoPicSaveName = request.getParameter("infoPicSaveName");
        String infoAppendName = request.getParameter("infoAppendName");
        String infoAppendSaveName = request.getParameter("infoAppendSaveName");
        String[] infoPicNames = null;
        String[] infoPicSaveNames = null;
        String[] infoAppendNames = null;
        String[] infoAppendSaveNames = null;
        if(infoPicName!=null && !infoPicName.equals("")){
        	infoPicNames = infoPicName.split("\\|");
            infoPicSaveNames = infoPicSaveName.split("\\|");
        }
        if(infoAppendName!=null && !infoAppendName.equals("")){
	        infoAppendNames = infoAppendName.split("\\|");
	        infoAppendSaveNames = infoAppendSaveName.split("\\|");
        }
        /*信息的附件和图片*/
        //更新信息
        boolean result = informationBD.update(informationId,parameters, null, infoAppendNames,
        		infoAppendSaveNames, infoPicNames, infoPicSaveNames);
        //先删除历史公共标签关系记录
        InfoTagBD bd = new InfoTagBD();
        try {
            bd.deletePublicTagRelation(Long.valueOf(informationId));
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        //保存标签-信息关系(公共标签)
        if(!"".equals(publicTagsId)){
            String[] tagIdArr = publicTagsId.split(",");
            for(int i=0; i<tagIdArr.length; i++){
                try {
                    //重新保存新的关联关系
                    bd.saveTagRelation(Long.valueOf(tagIdArr[i]), Long.valueOf(informationId), "0");
                } catch (Exception tags_e) {
                    tags_e.printStackTrace();
                }
            }
        }
        //信息同步到EZSITE网站多站点方式
        informationBD.synchronizeToMoreSite_update(informationId, parameters, null,infoAppendNames,
        		infoAppendSaveNames,infoPicNames,infoPicSaveNames,information.getInformationType(),"update", request);
        if (result) {
			//全文检索 插入临时表
	        newInformationBD.saveInformationLucene(new String[]{informationId,"2","information"});
			//修改时 插入统计数据
	        newInformationBD.saveInfostat(new Long(informationId),new Long(channelId), "update", new Long(orgId), orgName,
	        		new Long(userId), userName,new Long(domainId));
			//信息推送发邮件
	        if(information.getInformationMailSendId()!=null && !"".equals(information.getInformationMailSendId())){
				String mail_account = com.whir.common.util.CommonUtils.getSessionUserAccount(request);
	            String mail_pass = com.whir.common.util.CommonUtils.getSessionUserPassword(request);
	            boolean isEncryptPass = true;
	        	try {
					informationBD.MailSendURL_Send(informationId, information.getInformationMailSendId(), 
							information.getInformationMailSendName(), userName, domainId, mail_account, mail_pass, 
							isEncryptPass, remindType, userId, request);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
			//相关性
	        RelationBD relationBD = new RelationBD();
	        relationBD.saveRelationList(request,informationId);
	        printResult("success");
        }
      //记录日志
        LogBD logBD = new LogBD();
        logBD.log(userId, userName, orgName, "oa_information", "", new java.util.Date(), new java.util.Date(), "2",
        		information.getInformationTitle(), session.getAttribute("userIP").toString(), domainId);
		return null;
	}
	
	/**
	 * 单个删除信息，主要用于办理查阅的删除，删除信息的同时要删除该信息的相关流程信息 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String singleDelete(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		String[] batchId = new String[]{informationId};
		String delNames = "";
		/*从公文管理同步来的信息，在删除信息之前将该公文的同步状态还原*/
		List fromGOV = informationBD.getFromGOVInfo(batchId);
    	Object[] obj = null;
    	if(fromGOV!=null && fromGOV.size()>0){
    		SendFileBD bd = new SendFileBD();
    		GovSendFileCheckWithWorkFlowBD cbd = new GovSendFileCheckWithWorkFlowBD();
    		for(int i=0;i<fromGOV.size();i++){
    			obj = (Object[]) fromGOV.get(i);
    			if(obj[1].toString().equals("0")){
    				bd.updateSendFileStatus(obj[0].toString(), "0");
    			}else{
                    cbd.updateSendFileCheckStatus(obj[0].toString(), "0");
    			}
    		}
    	}
    	/*从公文管理同步来的信息，在删除信息之前将该公文的同步状态还原*/
    	com.whir.common.util.DeleteFile deleteFile = new com.whir.common.util.DeleteFile();
        deleteFile.setRequest(request);
        List list = informationBD.singleDelete(channelId, informationId);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size()-1; i++) {
                deleteFile.deleteFile("information", (String) list.get(i), domainId, new String[]{".swf", ".pdf"});
            }
            delNames = list.get(list.size()-1).toString();
        }
        /*办理查阅列表中删除办理查阅信息时，删除信息的流程数据*/
        String workflow = request.getParameter("workflow");
        //System.out.println("workflow:"+workflow);
        if(workflow!=null && workflow.equals("1")){
        	BPMInstanceBD bpmBD = new BPMInstanceBD();
        	bpmBD.deleteProcessInstance("4", informationId, userId);
        }
        /*办理查阅列表中删除办理查阅信息时，删除信息的流程数据*/
        //记录日志
        LogBD logBD = new LogBD();
        logBD.log(userId, userName, orgName, "oa_information", "", new java.util.Date(), new java.util.Date(), "3",
                  delNames, session.getAttribute("userIP").toString(), domainId);
        printResult("success");
		return null;
	}
	
	/**
	 * 批量删除信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String batchDelete(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		String[] batchId = informationId.split(",");
		String delNames = "";
		/*从公文管理同步来的信息，在删除信息之前将该公文的同步状态还原*/
		List fromGOV = informationBD.getFromGOVInfo(batchId);
    	Object[] obj = null;
    	if(fromGOV!=null && fromGOV.size()>0){
    		SendFileBD bd = new SendFileBD();
    		GovSendFileCheckWithWorkFlowBD cbd = new GovSendFileCheckWithWorkFlowBD();
    		for(int i=0;i<fromGOV.size();i++){
    			obj = (Object[]) fromGOV.get(i);
    			if(obj[1].toString().equals("0")){
    				bd.updateSendFileStatus(obj[0].toString(), "0");
    			}else{
                    cbd.updateSendFileCheckStatus(obj[0].toString(), "0");
    			}
    		}
    	}
    	/*从公文管理同步来的信息，在删除信息之前将该公文的同步状态还原*/
    	com.whir.common.util.DeleteFile deleteFile = new com.whir.common.util.DeleteFile();
        deleteFile.setRequest(request);
        List list = (List) informationBD.batchDelete(batchId);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size()-1; i++) {
                deleteFile.deleteFile("information", (String) list.get(i), domainId, new String[]{".swf", ".pdf"});
            }
            delNames = list.get(list.size()-1).toString();
        }
        //记录日志
        LogBD logBD = new LogBD();
        logBD.log(userId, userName, orgName, "oa_information", "", new java.util.Date(), new java.util.Date(), "3",
                  delNames, session.getAttribute("userIP").toString(), domainId);
        printResult("success");
		return null;
	}
	
	/**
	 * 流程发起
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String start(){
		//map参数备用
		setStartInfo(new HashMap()); 
		//设置打开流程的主地址
		this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/Information!updateProcess.action";
		//表单提交前的验证函数
		this.p_wf_initPara = "initPara";
		//没有父页面  
		this.p_wf_queryForm = "";
		//流程提醒信息标题
		this.p_wf_titleFieldName = "information.informationTitle";
		
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        String isIso = request.getParameter("iso")!=null ? request.getParameter("iso") : "";
        //20160617 -by jqq 保存草稿改造
        if("1".equals(isIso)){
            //文档新建没有保存草稿
            this.p_wf_modiButton = "Send";
            //20170302 -by jqq 文档流程审批名称改造
            this.p_wf_titleFieldName = "";
        }else{
            //设置流程按钮
            this.p_wf_modiButton = "Send,SaveInfoDraft";
        }
        //20161214 -by jqq 自由流程改造
        // 流程类型  1：业务流程  0：随机流程    2：半自由流程    3：完全自由流程
        if(p_wf_processType.equals("2")||p_wf_processType.equals("3")){
            this.p_wf_modiButton+=",SetProcess";
        }
        //文档管理目录
        if (isIso.equals("1")) {
            List list = InfoUtils.getIsoCanIssueChannel(userId, orgId, orgIdString,domainId);
            request.setAttribute("canIssueChannel", list);
            this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/Information!updateProcess.action?iso=1";
        } else {//信息管理栏目
        	//取能发布的栏目
            List list = InfoUtils.getCanIssueChannel(userId, orgId, orgIdString,domainId);
            String columnJson = InfoUtils.columnListToJson(list, false);
            request.setAttribute("canIssueChannel", columnJson);
            //同时发布到
            List list1 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
            String columnJson1 = InfoUtils.columnListToJson(list1, true);
            request.setAttribute("otherChannel", columnJson1);
        }
        //取模板
        List templateList = InfoUtils.getTemplate(userId, orgId, orgIdString, domainId);
        if (templateList != null) {
        	request.setAttribute("templateList", templateList);
        } else {
        	request.setAttribute("templateList",new java.util.ArrayList());
        }
        //收文（module：1） 发文（module：0） 工作流程（module：2）同步到信息管理
        String module = request.getParameter("module");//EncryptUtil.htmlcode(request,"module");
        if(module!=null && !"".equals(module)){
        	request.setAttribute("fromGov", module);
        	String content = request.getParameter("_content");//EncryptUtil.htmlcode(request,"_content");//
        	request.setAttribute("content", content);
            String title = request.getParameter("_title");//EncryptUtil.htmlcode(request,"_title");//
            request.setAttribute("title", title);
            String govId = request.getParameter("_fileId");//EncryptUtil.htmlcode(request,"_fileId");//
            request.setAttribute("govId", govId);
            String author = request.getParameter("_author");//EncryptUtil.htmlcode(request,"_author");//
            request.setAttribute("author", author);
            String docNo = request.getParameter("_docNO");//EncryptUtil.htmlcode(request,"_docNO");//
            request.setAttribute("docNo", docNo);
            String accessoryName = request.getParameter("_accessName");//EncryptUtil.htmlcode(request,"_accessName");//
            request.setAttribute("infoAppendName", accessoryName);
            String infoAppendSaveName = request.getParameter("_accessSaveName");//EncryptUtil.htmlcode(request,"_accessSaveName");//
            request.setAttribute("infoAppendSaveName", infoAppendSaveName);
            if(module.equals("2")){
            	String moduleId = request.getParameter("_moduleId");//EncryptUtil.htmlcode(request,"_moduleId");//
            	request.setAttribute("moduleId", moduleId);
            	String informationType = request.getParameter("_type");//EncryptUtil.htmlcode(request,"_type");//
            	request.setAttribute("informationType", informationType);
            }
            channelType = request.getParameter("channelType");//EncryptUtil.htmlcode(request,"channelType");//
            userDefine = request.getParameter("userDefine");//EncryptUtil.htmlcode(request,"userDefine");//
            userChannelName = "信息管理";
        }
        //新建信息时，在不需要审核的页面选择了需要审核的栏目
        /*String channel = request.getParameter("channel");//EncryptUtil.htmlcode(request,"channel");
        String tempContent = request.getParameter("tempContent");//EncryptUtil.htmlcode(request,"tempContent");
        String reader = request.getParameter("reader");//EncryptUtil.htmlcode(request,"reader");
        String readerName = request.getParameter("readerName");//EncryptUtil.htmlcode(request,"readerName");
        String remindType = request.getParameter("remindType");//EncryptUtil.htmlcode(request,"remindType");
        String printer = request.getParameter("printer");//EncryptUtil.htmlcode(request,"printer");
        String printerName = request.getParameter("printerName");//EncryptUtil.htmlcode(request,"printerName");
        String printNum = EncryptUtil.htmlcode(request,"printNum_");//request.getParameter("printNum_");
        String downloader = request.getParameter("downloader");//EncryptUtil.htmlcode(request,"downloader");
        String downloaderName = request.getParameter("downloaderName");//EncryptUtil.htmlcode(request,"downloaderName");
        String downloadNum = EncryptUtil.htmlcode(request,"downloadNum_");//request.getParameter("downloadNum_");
*/      
        String channel = EncryptUtil.replaceHtmlcode(request, "channel");
        String tempContent = EncryptUtil.replaceHtmlcode(request, "tempContent");
        String reader = EncryptUtil.replaceHtmlcode(request, "reader");
        String readerName = EncryptUtil.replaceHtmlcode(request, "readerName");
        String remindType = EncryptUtil.replaceHtmlcode(request, "remindType");
        String printer = EncryptUtil.replaceHtmlcode(request, "printer");
        String printerName = EncryptUtil.replaceHtmlcode(request, "printerName");
        String printNum = EncryptUtil.replaceHtmlcode(request, "printNum_");
        String downloader = EncryptUtil.replaceHtmlcode(request, "downloader");
        String downloaderName = EncryptUtil.replaceHtmlcode(request, "downloaderName");
        String downloadNum = EncryptUtil.replaceHtmlcode(request, "downloadNum_");
        
        request.setAttribute("channel", channel);
        request.setAttribute("tempContent", tempContent);
        request.setAttribute("reader", reader);
        request.setAttribute("readerName", readerName);
        request.setAttribute("remindType", remindType);
        request.setAttribute("printer", printer);
        request.setAttribute("printerName", printerName);
        request.setAttribute("printNum", printNum);
        request.setAttribute("downloader", downloader);
        request.setAttribute("downloaderName", downloaderName);
        request.setAttribute("downloadNum", downloadNum);
        //是否是易播栏目
        String isyiboflag = request.getParameter("isyiboflag") != null ? request.getParameter("isyiboflag") : "0";
        isyiboflag = EncryptUtil.replaceHtmlcode(isyiboflag);
        request.setAttribute("isyiboflag", isyiboflag);
        /*信息公共标签查询*/
        List publicList =new ArrayList();
        InfoTagBD bd = new InfoTagBD();
        try {
            publicList = bd.getAllPublicTags();
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        request.setAttribute("publicList", publicList);
        /*信息公共标签查询*/
        if(isIso.equals("1")){
        	return "isoaddprocess";
        }else{
        	return "addprocess";
        }
	}
	
	/**
	 * 流程流转
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String updateProcess(){
		//logger.debug("========进入流程流转方法 start ========");
		informationId = p_wf_recordId;
		//map参数备用   
        if(!setUpdateInfo(new HashMap())){   
        	if(request.getParameter("loadDeal")==null || !request.getParameter("loadDeal").equals("1")){
        		return BaseActionSupport.NO_RIGHTS;   
        	}
        }
        //设置打开流程的主地址
        this.p_wf_mainLinkFile= com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/Information!updateProcess.action";
        //表单提交前的验证函数
		this.p_wf_initPara = "initPara";
		//流程提醒信息标题
		this.p_wf_titleFieldName = "information.informationTitle";
		//流程活动设置中的可写字段
        request.setAttribute("curModifyField", p_wf_cur_ModifyField);
        //20160107 -by jqq 办理查阅，所有字段都不可以编辑
        //logger.debug("========流程活动设置中的可写字段curModifyField:"+(p_wf_cur_ModifyField!=null?p_wf_cur_ModifyField:"null"));
        if("modifyView".equals(request.getParameter("p_wf_openType"))){
        	logger.debug("========进入办理查阅，设置可编辑字段为\"\"，空字符串（非null）========");
        	request.setAttribute("curModifyField", "");
        }
        //办理查阅列表打开信息
        if(request.getParameter("loadDeal")!=null && request.getParameter("loadDeal").equals("1")){
        	this.p_wf_modiButton = "Print";
        }
        HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        information = informationBD.load(informationId);
        //走流程的信息修改时不能更改所属栏目，所以不需要取当前用户的可新建栏目，直接在list中放入当前信息的所属栏目即可
        String userChannelName = "信息管理";
        if(information.getInformationChannel().getUserDefine().equals("1")){
        	userChannelName = new ChannelBD().getUserChannelName(information.getInformationChannel().getChannelId().toString());
        }else if(information.getInformationChannel().getChannelType()>0){
        	userChannelName = new OrganizationBD().getOrgName(information.getInformationChannel().getChannelType()+"");
        }else if (iso!=null && iso.equals("1")) {
        	userChannelName = "文档管理";
        }
        String[] channelArray = new String[]{information.getInformationChannel().getChannelId().toString(),
        		information.getInformationChannel().getChannelName(),
        		userChannelName+"."+information.getInformationChannel().getChannelName(),
        		information.getInformationChannel().getChannelType()+"",
        		information.getInformationChannel().getUserDefine()};
        List channelList = new ArrayList();//InfoUtils.getCanIssueChannel(userId, orgId, orgIdString,domainId);
        channelList.add(channelArray);
        //走流程的信息修改时不能更改所属栏目，所以不需要取当前用户的可新建栏目，直接在list中放入当前信息的所属栏目即可
        if (iso!=null && iso.equals("1")) {
        	request.setAttribute("canIssueChannel", channelList);
            this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+ "/Information!updateProcess.action?iso=1";
            if(destory!=null && "1".equals(destory)){
            	this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+ "/Information!updateProcess.action?iso=1&destory=1";
            }
            //20170302 -by jqq 文档流程审批名称改造
            this.p_wf_titleFieldName = "";
        } else {
        	String columnJson = InfoUtils.columnListToJson(channelList, false);
            request.setAttribute("canIssueChannel", columnJson);
            //同时发布到,11.1.0.0需求，同时发布到根据流程设置是否可写，取用户可发布的栏目+该信息当前同时发布到的栏目，
        	//如果用户可发布的栏目不包含该信息当前同时发布到的栏目，则将该信息当前同时发布到的栏目加入到用户可发布的栏目
            List list1 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
            String otherchannel = information.getOtherChannel();
            if(otherchannel!=null && !"".equals(otherchannel) && !",0,".equals(otherchannel)){
	            boolean include = false;
	            for(int i=0;i<list1.size();i++){
	            	Object[] ch = (Object[]) list1.get(i);
	            	if((otherchannel.replaceAll(",", "")).equals(ch[0].toString())){
	            		include = true;
	            		break;
	            	}
	            }
	            String otherChannelName = "信息管理";
	            if(!include){
	            	otherchannel = otherchannel.replaceAll(",", "");
	            	List ch = new ChannelBD().getSingleChannel(otherchannel);
	            	Object[] obj = (Object[]) ch.get(0);
	            	String channelName = obj[0].toString();
	            	String channelType = obj[1].toString();
	            	String userDefine = obj[30].toString();
	            	String channelNameString = obj[29].toString();
	            	if(userDefine.equals("1")){
	            		otherChannelName = new ChannelBD().getUserChannelName(otherchannel);
	            	}else if(Integer.parseInt(channelType)>0){
	            		otherChannelName = new OrganizationBD().getOrgName(channelType);
	            	}
	            	String[] otherChannelArray = new String[]{otherchannel,channelName,
	            			otherChannelName+"."+channelNameString,channelType,userDefine};
	            	list1.add(otherChannelArray);
	            }
            }
            String columnJson1 = InfoUtils.columnListToJson(list1, true);
            request.setAttribute("otherChannel",columnJson1);
        }
        //取模板
        List templateList = InfoUtils.getTemplate(userId, orgId, orgIdString, domainId);
        if (templateList != null) {
        	request.setAttribute("templateList", templateList);
        } else {
        	request.setAttribute("templateList",new java.util.ArrayList());
        }
        //附件和图片
        InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
        List list = informationAccessoryBD.getAccessory(informationId);
        String infoPicName = "";
        String infoPicSaveName = "";
        String infoAppendName = "";
        String infoAppendSaveName = "";
        Object[] tmp = null;
        if (list != null && list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                tmp = (Object[]) list.get(i);
                if(tmp!=null && tmp[1]!=null){
                	if (tmp[4].toString().equals("1")) {
                    	infoPicName += tmp[1].toString()+"|";
                    	infoPicSaveName += tmp[2].toString()+"|";
                    } else {
                    	infoAppendName += tmp[1].toString()+"|";
                    	infoAppendSaveName += tmp[2].toString()+"|";
                    }
                }
            }
        }
        if(infoPicName.endsWith("|")){
        	infoPicName = infoPicName.substring(0,infoPicName.length()-1);
        	infoPicSaveName = infoPicSaveName.substring(0,infoPicSaveName.length()-1);
        }
        if(infoAppendName.endsWith("|")){
        	infoAppendName = infoAppendName.substring(0,infoAppendName.length()-1);
        	infoAppendSaveName = infoAppendSaveName.substring(0,infoAppendSaveName.length()-1);
        }
        request.setAttribute("infoPicName", infoPicName);
        request.setAttribute("infoPicSaveName", infoPicSaveName);
        request.setAttribute("infoAppendName", infoAppendName);
        request.setAttribute("infoAppendSaveName", infoAppendSaveName);
        
        //可查看人
        String informationReaderId = (information.getInformationReader()!=null ? information.getInformationReader() : "")
        	+ (information.getInformationReaderOrg()!=null ? information.getInformationReaderOrg() : "")
        	+ (information.getInformationReaderGroup()!=null ? information.getInformationReaderGroup() : "");
        request.setAttribute("informationReaderId", informationReaderId);
        //信息可查看人选择范围
        String informationReaderId_ = (information.getInformationChannel().getChannelReader()!=null ? information.getInformationChannel().getChannelReader() : "")
        	+ (information.getInformationChannel().getChannelReaderOrg()!=null ? information.getInformationChannel().getChannelReaderOrg() : "")
        	+ (information.getInformationChannel().getChannelReaderGroup()!=null ? information.getInformationChannel().getChannelReaderGroup() : "");
        request.setAttribute("informationReaderId_", informationReaderId_);
        
        //信息可打印人
        String informationPrinterId = (information.getInformationPrinter()!=null ? information.getInformationPrinter() : "")
	    	+ (information.getInformationPrinterOrg()!=null ? information.getInformationPrinterOrg() : "")
	    	+ (information.getInformationPrinterGroup()!=null ? information.getInformationPrinterGroup() : "");
        request.setAttribute("informationPrinterId", informationPrinterId);
        //信息可打印人选择范围
        String informationPrinterId_ = (information.getInformationChannel().getChannelPrinter()!=null ? information.getInformationChannel().getChannelPrinter() : "")
        	+ (information.getInformationChannel().getChannelPrinterOrg()!=null ? information.getInformationChannel().getChannelPrinterOrg() : "")
        	+ (information.getInformationChannel().getChannelPrinterGroup()!=null ? information.getInformationChannel().getChannelPrinterGroup() : "");
        request.setAttribute("informationPrinterId_", informationPrinterId_);
        
        //信息可下载人
        String informationDownLoaderId = (information.getInformationDownLoader()!=null ? information.getInformationDownLoader() : "")
			+ (information.getInformationDownLoaderOrg()!=null ? information.getInformationDownLoaderOrg() : "")
			+ (information.getInformationDownLoaderGroup()!=null ? information.getInformationDownLoaderGroup() : "");
		request.setAttribute("informationDownLoaderId", informationDownLoaderId);
		//信息可下载人选择范围
        String informationDownLoaderId_ = (information.getInformationChannel().getChannelDownLoader()!=null ? information.getInformationChannel().getChannelDownLoader() : "")
        	+ (information.getInformationChannel().getChannelDownLoaderOrg()!=null ? information.getInformationChannel().getChannelDownLoaderOrg() : "")
        	+ (information.getInformationChannel().getChannelDownLoaderGroup()!=null ? information.getInformationChannel().getChannelDownLoaderGroup() : "");
        request.setAttribute("informationDownLoaderId_", informationDownLoaderId_);
        
        if(information.getDisplayTitle()==0){
    		information.setDisplayTitle(1);
    	}else{
    		information.setDisplayTitle(0);
    	}
    	if(!CommonUtils.isEmpty(information.getDisplayImage()) && information.getDisplayImage().equals("1")){
    		information.setDisplayImage("0");
    	}else{
    		information.setDisplayImage("1");
    	}
        if(information.getInformationType().equals("3")){//文件链接
        	String[] file = information.getInformationContent().split(":");
        	String fileLinkContent = file[0];
        	String fileLinkContentHidd = file[1];
        	request.setAttribute("fileLinkContent", fileLinkContent);
        	request.setAttribute("fileLinkContentHidd", fileLinkContentHidd);
        } else if (information.getInformationType().equals("2")){//地址链接
        	request.setAttribute("urlContent", information.getInformationContent());
        } else if (information.getInformationType().equals("0")){//普通编辑
        	request.setAttribute("textContent", information.getInformationContent());
        } else if (information.getInformationType().equals("4") || information.getInformationType().equals("5") || information.getInformationType().equals("6")){//word或excel或ppt编辑
        	request.setAttribute("content", information.getInformationContent());
        }
        String channel = information.getInformationChannel().getChannelId()+","+information.getInformationChannel().getChannelName();
        String otherChannel = information.getOtherChannel();
        request.setAttribute("channel", channel);
        request.setAttribute("other", otherChannel);
        
        //提醒方式
        String remindType = information.getRemindType()!=null ? information.getRemindType() : "";
        request.setAttribute("remindType", remindType);
        
        //2013-09-14-----获取关联列表-----start
        List assoicateInfoList = new ArrayList();
		//logger.debug("channelType----->"+channelType);
		//logger.debug("userDefine----->"+userDefine);
		if(CommonUtils.isEmpty(channelType)){
			channelType ="0";
		}
		if(CommonUtils.isEmpty(userDefine)){
			userDefine ="0";
		}
        assoicateInfoList = informationBD.getAssociateInfo(orgId, informationId, userId, orgIdString, channelType, userDefine, "2");
		if(assoicateInfoList!=null && assoicateInfoList.size()>0){
			request.setAttribute("assoicateInfo", assoicateInfoList);
		}
		//2013-09-14-----获取关联列表-----end
		
		//易播栏目改造，增加是否是易播栏目标识字段
        String isYiBoChannel =  information.getInformationChannel().getIsYiBoChannel();
        request.setAttribute("isYiBoChannel", isYiBoChannel);
        /*信息公共标签查询*/
        List publicList =new ArrayList();
        List pubtagList =new ArrayList();
        InfoTagBD bd = new InfoTagBD();
        try {
            publicList = bd.getAllPublicTags();
            pubtagList = bd.getPublicTagsByInfoId(informationId);
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        request.setAttribute("publicList", publicList);
        /*信息公共标签查询*/
        //20160531 -by jqq 每次load公共标签查询获取，不存储在信息表
        if(pubtagList != null && pubtagList.size() > 0){
            String ptagsId = "";
            String ptagsName = "";
            for(int k=0; k<pubtagList.size(); k++){
                Object[] obj = (Object[]) pubtagList.get(k);
                ptagsId += obj[0].toString() + ",";
                ptagsName += obj[1].toString() + ",";
            }
            request.setAttribute("publicTagsId",ptagsId.substring(0, ptagsId.length()-1));
            information.setInformationKey(ptagsName.substring(0, ptagsName.length()-1));
        }else{
            request.setAttribute("publicTagsId","");
            information.setInformationKey("");
        }
        if(iso!=null && iso.equals("1")){
        	if(request.getParameter("loadDeal")!=null && request.getParameter("loadDeal").equals("1")){
        		request.setAttribute("oldinformationVersion", information.getInformationVersion());
        		request.setAttribute("oldisoDealCategory", information.getIsoDealCategory());
        		return "isoprintprocess";
        	}else{
        		return "isoupdateprocess";
        	}
        }else{
        	return "updateprocess";
        }
	}
	
	/**
	 * 流程重新发起（修改需要审核的信息）
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String restart(){
		informationId = p_wf_recordId;
		information = informationBD.load(informationId);
		if(destory!=null && "1".equals(destory)){
			p_wf_pool_processId = request.getParameter("processId");
		}else {
			if(request.getParameter("p_wf_pool_processId")!=null && request.getParameter("channelId")!=null){
				p_wf_pool_processId = request.getParameter("p_wf_pool_processId");
				channelId = request.getParameter("channelId");
				information.setInformationChannel(new ChannelBD().loadChannel(channelId));
			}else{
				channelId = information.getInformationChannel().getChannelId().toString();
				ChannelBD bd = new ChannelBD();
				Object processInfo[] = bd.getChannelProcessInfoByChannelId(channelId);
				p_wf_pool_processId = processInfo[0].toString();
			}
		}
		p_wf_openType = request.getParameter("p_wf_openType");
		//map参数备用
		setStartInfo(new HashMap());
		//设置打开流程的主地址
		this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+   
				"/Information!updateProcess.action";
		//如果要自己控制按钮 可改变按钮属性  
		//20151217 -by jqq 审核修改页面去掉保存草稿的按钮
		//this.p_wf_modiButton = "Send,EzFlowSaveDraft";
		//20160601 -by jqq 流程审核保存草稿改造
		String draftflag = request.getParameter("draftflag")!=null ? request.getParameter("draftflag") : "";
		if("1".equals(draftflag)){
		    this.p_wf_modiButton = "Send,SaveInfoDraft";
		    //保存草稿页面的发布时间显示为修改时间
		    if(information.getInformationModifyTime() != null){
		        information.setInformationIssueTime(information.getInformationModifyTime());
		    }
		}else{
		    this.p_wf_modiButton = "Send";
		}
		if (iso!=null && iso.equals("1")) {
			this.p_wf_modiButton = "Send,Saveclose,Print";  
		}
		//20161214 -by jqq 自由流程改造
        // 流程类型  1：业务流程  0：随机流程    2：半自由流程    3：完全自由流程
        if(p_wf_processType.equals("2")||p_wf_processType.equals("3")){
            this.p_wf_modiButton+=",SetProcess";
        }
		this.p_wf_queryForm = "queryForm";
		//流程提醒信息标题
		this.p_wf_titleFieldName = "information.informationTitle";
		//信息或文档从不审核栏目修改成审核栏目时，不要删除流程记录
		String modifyToProcess = request.getParameter("modifyToProcess")!=null?request.getParameter("modifyToProcess"):"";
		request.setAttribute("modifyToProcess", modifyToProcess);
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        //草稿
        String draft = request.getParameter("draft")!=null?request.getParameter("draft"):"";
        request.setAttribute("draft", draft);
        //走流程的信息修改时不能更改所属栏目，所以不需要取当前用户的可新建栏目，直接在list中放入当前信息的所属栏目即可
        String userChannelName = "信息管理";
        if(information.getInformationChannel().getUserDefine().equals("1")){
        	userChannelName = new ChannelBD().getUserChannelName(information.getInformationChannel().getChannelId().toString());
        }else if(information.getInformationChannel().getChannelType()>0){
        	userChannelName = new OrganizationBD().getOrgName(information.getInformationChannel().getChannelType()+"");
        }else if (iso!=null && iso.equals("1")) {
        	userChannelName = "文档管理";
        }
        String[] channelArray = new String[]{information.getInformationChannel().getChannelId().toString(),
        		information.getInformationChannel().getChannelName(),
        		userChannelName+"."+information.getInformationChannel().getChannelName(),
        		information.getInformationChannel().getChannelType()+"",
        		information.getInformationChannel().getUserDefine()};
        List channelList = new ArrayList();//InfoUtils.getCanIssueChannel(userId, orgId, orgIdString,domainId);
        channelList.add(channelArray);
        //走流程的信息修改时不能更改所属栏目，所以不需要取当前用户的可新建栏目，直接在list中放入当前信息的所属栏目即可
        if (iso!=null && iso.equals("1")) {
        	request.setAttribute("canIssueChannel", channelList);
        	List assoicateInfoList = new ArrayList();
    		//2013-09-03-----start
    		//logger.debug("channelType----->"+channelType);
    		//logger.debug("userDefine----->"+userDefine);
    		if(CommonUtils.isEmpty(userDefine)){
    			userDefine ="0";
    		}
    		if(CommonUtils.isEmpty(channelType)){
    			channelType ="0";
    		}
    		//2013-09-03-----end
    		assoicateInfoList = informationBD.getAssociateInfo(orgId, informationId, userId, orgIdString, channelType, userDefine, "2");
    		if(assoicateInfoList!=null && assoicateInfoList.size()>0){
    			request.setAttribute("assoicateInfo", assoicateInfoList);
    		}
            this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+   
			"/Information!updateProcess.action?iso=1";
            if(destory!=null && "1".equals(destory)){
            	this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+   
    			"/Information!updateProcess.action?iso=1&destory=1";
            }
            //20170302 -by jqq 文档流程审批名称改造
            this.p_wf_titleFieldName = "";
        } else {
        	String columnJson = InfoUtils.columnListToJson(channelList, false);
            request.setAttribute("canIssueChannel", columnJson);
        	//同时发布到,11.1.0.0需求，同时发布到根据流程设置是否可写，取用户可发布的栏目+该信息当前同时发布到的栏目，
        	//如果用户可发布的栏目不包含该信息当前同时发布到的栏目，则将该信息当前同时发布到的栏目加入到用户可发布的栏目
        	List list1 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
            String otherchannel = information.getOtherChannel();
            if(otherchannel!=null && !"".equals(otherchannel) && !",0,".equals(otherchannel)){
	            boolean include = false;
	            for(int i=0;i<list1.size();i++){
	            	Object[] ch = (Object[]) list1.get(i);
	            	if((otherchannel.replaceAll(",", "")).equals(ch[0].toString())){
	            		include = true;
	            		break;
	            	}
	            }
	            String otherChannelName = "信息管理";
	            if(!include){
	            	otherchannel = otherchannel.replaceAll(",", "");
	            	List ch = new ChannelBD().getSingleChannel(otherchannel);
	            	Object[] obj = (Object[]) ch.get(0);
	            	String channelName = obj[0].toString();
	            	String channelType = obj[1].toString();
	            	String userDefine = obj[30].toString();
	            	String channelNameString = obj[29].toString();
	            	if(userDefine.equals("1")){
	            		otherChannelName = new ChannelBD().getUserChannelName(otherchannel);
	            	}else if(Integer.parseInt(channelType)>0){
	            		otherChannelName = new OrganizationBD().getOrgName(channelType);
	            	}
	            	String[] otherChannelArray = new String[]{otherchannel,channelName,
	            			otherChannelName+"."+channelNameString,channelType,userDefine};
	            	list1.add(otherChannelArray);
	            }
            }
            String columnJson1 = InfoUtils.columnListToJson(list1, true);
            request.setAttribute("otherChannel",columnJson1);
        }
        //取模板
        List templateList = InfoUtils.getTemplate(userId, orgId, orgIdString, domainId);
        if (templateList != null) {
        	request.setAttribute("templateList", templateList);
        } else {
        	request.setAttribute("templateList",new java.util.ArrayList());
        }
        //附件和图片
        InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
        List list = informationAccessoryBD.getAccessory(informationId);
        String infoPicName = "";
        String infoPicSaveName = "";
        String infoAppendName = "";
        String infoAppendSaveName = "";
        Object[] tmp = null;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                tmp = (Object[]) list.get(i);
                if (tmp[4].toString().equals("1")) {
                	infoPicName += tmp[1].toString()+"|";
                	infoPicSaveName += tmp[2].toString()+"|";
                } else {
                	infoAppendName += tmp[1].toString()+"|";
                	infoAppendSaveName += tmp[2].toString()+"|";
                }
            }
        }
        if(infoPicName.endsWith("|")){
        	infoPicName = infoPicName.substring(0,infoPicName.length()-1);
        	infoPicSaveName = infoPicSaveName.substring(0,infoPicSaveName.length()-1);
        }
        if(infoAppendName.endsWith("|")){
        	infoAppendName = infoAppendName.substring(0,infoAppendName.length()-1);
        	infoAppendSaveName = infoAppendSaveName.substring(0,infoAppendSaveName.length()-1);
        }
        request.setAttribute("infoPicName", infoPicName);
        request.setAttribute("infoPicSaveName", infoPicSaveName);
        request.setAttribute("infoAppendName", infoAppendName);
        request.setAttribute("infoAppendSaveName", infoAppendSaveName);
        //可查看人
        String informationReaderId = (information.getInformationReader()!=null ? information.getInformationReader() : "")
        	+ (information.getInformationReaderOrg()!=null ? information.getInformationReaderOrg() : "")
        	+ (information.getInformationReaderGroup()!=null ? information.getInformationReaderGroup() : "");
        request.setAttribute("informationReaderId", informationReaderId);
        //信息可查看人选择范围
        String informationReaderId_ = (information.getInformationChannel().getChannelReader()!=null ? information.getInformationChannel().getChannelReader() : "")
        	+ (information.getInformationChannel().getChannelReaderOrg()!=null ? information.getInformationChannel().getChannelReaderOrg() : "")
        	+ (information.getInformationChannel().getChannelReaderGroup()!=null ? information.getInformationChannel().getChannelReaderGroup() : "");
        request.setAttribute("informationReaderId_", informationReaderId_);
        
        //信息可打印人
        String informationPrinterId = (information.getInformationPrinter()!=null ? information.getInformationPrinter() : "")
	    	+ (information.getInformationPrinterOrg()!=null ? information.getInformationPrinterOrg() : "")
	    	+ (information.getInformationPrinterGroup()!=null ? information.getInformationPrinterGroup() : "");
        request.setAttribute("informationPrinterId", informationPrinterId);
        //信息可打印人选择范围
        String informationPrinterId_ = (information.getInformationChannel().getChannelPrinter()!=null ? information.getInformationChannel().getChannelPrinter() : "")
        	+ (information.getInformationChannel().getChannelPrinterOrg()!=null ? information.getInformationChannel().getChannelPrinterOrg() : "")
        	+ (information.getInformationChannel().getChannelPrinterGroup()!=null ? information.getInformationChannel().getChannelPrinterGroup() : "");
        request.setAttribute("informationPrinterId_", informationPrinterId_);
        
        //信息可下载人
        String informationDownLoaderId = (information.getInformationDownLoader()!=null ? information.getInformationDownLoader() : "")
			+ (information.getInformationDownLoaderOrg()!=null ? information.getInformationDownLoaderOrg() : "")
			+ (information.getInformationDownLoaderGroup()!=null ? information.getInformationDownLoaderGroup() : "");
		request.setAttribute("informationDownLoaderId", informationDownLoaderId);
		//信息可下载人选择范围
        String informationDownLoaderId_ = (information.getInformationChannel().getChannelDownLoader()!=null ? information.getInformationChannel().getChannelDownLoader() : "")
        	+ (information.getInformationChannel().getChannelDownLoaderOrg()!=null ? information.getInformationChannel().getChannelDownLoaderOrg() : "")
        	+ (information.getInformationChannel().getChannelDownLoaderGroup()!=null ? information.getInformationChannel().getChannelDownLoaderGroup() : "");
        request.setAttribute("informationDownLoaderId_", informationDownLoaderId_);
        
        if(information.getDisplayTitle()==0){
    		information.setDisplayTitle(1);
    	}else{
    		information.setDisplayTitle(0);
    	}
    	if(!CommonUtils.isEmpty(information.getDisplayImage()) && information.getDisplayImage().equals("1")){
    		information.setDisplayImage("0");
    	}else{
    		information.setDisplayImage("1");
    	}
        if(information.getInformationType().equals("3")){//文件链接
        	String[] file = information.getInformationContent().split(":");
        	String fileLinkContent = file[0];
        	String fileLinkContentHidd = file[1];
        	request.setAttribute("fileLinkContent", fileLinkContent);
        	request.setAttribute("fileLinkContentHidd", fileLinkContentHidd);
        } else if (information.getInformationType().equals("2")){//地址链接
        	request.setAttribute("urlContent", information.getInformationContent());
        } else if (information.getInformationType().equals("0")){//普通编辑
        	request.setAttribute("textContent", information.getInformationContent());
        } else if (information.getInformationType().equals("4") || information.getInformationType().equals("5") 
        		|| information.getInformationType().equals("6")){//word或excel或ppt编辑
        	/*信息重新生成金格文档及金格Id*/
            /*String newContent = newInformationBD.copeJingeDocument(information.getInformationContent(), information.getInformationType());
            String fileType = ".doc";
            if (information.getInformationType().equals("5")) {
            	fileType = ".xls";
            } else if (information.getInformationType().equals("6")) {
            	fileType = ".ppt";
            }
            String oldFile = information.getInformationContent() + fileType;
            if (newContent != null && !newContent.equals("-1") && !newContent.equals("")) {
            	information.setInformationContent(newContent);
            }
            String localPath = session.getServletContext().getRealPath("upload");
            this.copyFileFromInformation(oldFile, newContent+fileType, session, localPath, request);*/
    		/*信息重新生成金格文档及金格Id*/
        	request.setAttribute("content", information.getInformationContent());
        }
        String channel = information.getInformationChannel().getChannelId()+","+information.getInformationChannel().getChannelName();
        String otherChannel = information.getOtherChannel();
        request.setAttribute("channel", channel);
        request.setAttribute("other", otherChannel);
        request.setAttribute("oldisoDealCategory", information.getIsoDealCategory()==null?"0":information.getIsoDealCategory());
        //易播栏目改造，增加是否是易播栏目标识字段
        String isYiBoChannel =  information.getInformationChannel().getIsYiBoChannel();
        request.setAttribute("isYiBoChannel", isYiBoChannel);
        /*信息公共标签查询*/
        List publicList =new ArrayList();
        List pubtagList =new ArrayList();
        InfoTagBD bd = new InfoTagBD();
        try {
            publicList = bd.getAllPublicTags();
            //20160516 -by jqq 该信息的公共标签（原关键字）
            pubtagList = bd.getPublicTagsByInfoId(informationId);
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        request.setAttribute("publicList", publicList);
        /*信息公共标签查询*/
        //20160531 -by jqq 每次load公共标签查询获取，不存储在信息表
        if(pubtagList != null && pubtagList.size() > 0){
            String ptagsId = "";
            String ptagsName = "";
            for(int k=0; k<pubtagList.size(); k++){
                Object[] obj = (Object[]) pubtagList.get(k);
                ptagsId += obj[0].toString() + ",";
                ptagsName += obj[1].toString() + ",";
            }
            request.setAttribute("publicTagsId",ptagsId.substring(0, ptagsId.length()-1));
            information.setInformationKey(ptagsName.substring(0, ptagsName.length()-1));
        }else{
            request.setAttribute("publicTagsId","");
            information.setInformationKey("");
        }
        if (iso!=null && iso.equals("1")) {
        	return "isoupdateprocess";
        }else{
        	return "updateprocess";
        }
	}
	
	/**
	 * 选择栏目
	 * @return
	 */
	public String changeChannel(){
		if(channelId!=null && !"".equals(channelId)){
			String json = "";
			ChannelBD bd = new ChannelBD();
			Object processInfo[] = bd.getChannelProcessInfoByChannelId(channelId);
			String processId = "-1";
			processId = processInfo[0].toString();
			InformationChannelPO po = bd.loadChannel(channelId);
			String reader = "";
			String canReader = "";
			String canReaderName = "";
			String remindType = po.getRemindType()!=null ? po.getRemindType() : "";
	        if(po.getChannelReader() != null){
	            reader += po.getChannelReader();
	        }
	        if(po.getChannelReaderGroup() != null){
	            reader += po.getChannelReaderGroup();
	        }
	        if(po.getChannelReaderOrg() != null){
	            reader += po.getChannelReaderOrg();
	        }
	        
	        /*打印权限*/
	        String printer = "";
			String printerName = "";
			String printNum = "";
	        if(po.getChannelPrinter() != null){
	        	printer += po.getChannelPrinter();
	        }
	        if(po.getChannelPrinterGroup() != null){
	        	printer += po.getChannelPrinterGroup();
	        }
	        if(po.getChannelPrinterOrg() != null){
	        	printer += po.getChannelPrinterOrg();
	        }
	        if(po.getChannelPrinterName() != null){
	        	printerName = po.getChannelPrinterName();
	        }
	        if(po.getPrintNum() != null){
	        	printNum = po.getPrintNum();
	        }
	        
	        /*打印权限*/
	        /*下载权限*/
	        String downloader = "";
			String downloaderName = "";
			String downloadNum = "";
	        if(po.getChannelDownLoader() != null){
	        	downloader += po.getChannelDownLoader();
	        }
	        if(po.getChannelDownLoaderGroup() != null){
	        	downloader += po.getChannelDownLoaderGroup();
	        }
	        if(po.getChannelDownLoaderOrg() != null){
	        	downloader += po.getChannelDownLoaderOrg();
	        }
	        if(po.getChannelDownLoaderName() != null){
	        	downloaderName = po.getChannelDownLoaderName();
	        }
	        if(po.getDownLoadNum() != null){
	        	downloadNum = po.getDownLoadNum();
	        }
	        /*下载权限*/
	        //20151120 -by jqq 11.3：增加是否易播栏目标志
	        String isYiBoChannel = "";
	        if(po.getIsYiBoChannel() != null){
	        	isYiBoChannel = po.getIsYiBoChannel();
	        }
	        //logger.debug("===========是否易播栏目isYiBoChannel:" + isYiBoChannel);
	        
	        HttpSession session = request.getSession();
	        String userId = session.getAttribute("userId").toString();
	        String orgId = session.getAttribute("orgId").toString();
	        String [] rs = informationBD.getTwoOrgRight(userId, orgId, reader, 
	        		po.getChannelReaderName() == null ? "" : po.getChannelReaderName());
	        if(rs!=null && rs.length>1){
	        	canReader = rs[0].toString();
	        	canReaderName = rs[1] == null ? "" : rs[1].toString();
	        }else{
	        	canReader = reader;
	        	canReaderName = po.getChannelReaderName() == null ? "" : po.getChannelReaderName();
	        }
	        json += "{\"processId\":\""+processId+"\",\"canReader\":\""+canReader+"\",\"canReaderName\":\""+canReaderName+"\"," +
    			"\"printer\":\""+printer+"\",\"printerName\":\""+printerName+"\",\"printNum\":\""+printNum+"\"," +
    			"\"downloader\":\""+downloader+"\",\"downloaderName\":\""+downloaderName+"\",\"downloadNum\":\""+downloadNum+"\",\"remindType\":\""+remindType +"\",\"isYiBoChannel\":\"" + isYiBoChannel +"\"}";
	        printJsonResult(json);
		}
		return null;
	}
	
	/**
	 * 打开信息排序页面
	 * @return
	 */
	public String sort(){
		request.setAttribute("orderCode", request.getParameter("orderCode"));
		return "sort";
	}
	
	/**
	 * 保存排序码
	 * @return
	 */
	public String setOrder(){
		Integer result = informationBD.setOrderCode(informationId, channelType, request.getParameter("orderCode"));
		if(result != -1){
			printResult("success");
		}
		return null;
	}
	
	/**
	 * 设置精华
	 * @return
	 */
	public String setCommend(){
		if(informationId!=null && !informationId.equals("")){
			String[] ids = informationId.split(",");
			boolean result = informationBD.commend(ids);
			if(result)
				printResult("success");
		}
		return null;
	}
	
	/**
	 * 取消精华设置
	 * @return
	 */
	public String removeCommend(){
        boolean result = informationBD.removeCommend(informationId);
        if(result){
        	printResult("success");
        }
		return null;
	}
	
	/**
	 * 取信息中的评论列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String commentList(){
		List commentList = informationBD.getComment(information.getInformationId().toString());
		String viewSQL = "aaa.commentIssuerOrg,aaa.commentIssuerName,aaa.commentIssueTime,aaa.commentContent," +
				"aaa.commentId,aaa.commentIssuerId,evo.empLivingPhoto";
		/* 头像 处理[BEGIN] */
        String fileServer = com.whir.component.config.ConfigReader.getFileServer(request.getRemoteAddr());
        List<String[]> resultList = new ArrayList<String[]>();
        for (int i = 0; i < commentList.size(); i++) {
            Object[] obj = (Object[]) commentList.get(i);
            String[] _obj = new String[7];
            _obj[0] = obj[0] == null ? "" : obj[0].toString();
            _obj[1] = obj[1] == null ? "" : obj[1].toString();
            if(obj[2] != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                _obj[2] = sdf.format(obj[2]);
            }else{
                _obj[2] = "";
            }
            _obj[3] = obj[3] == null ? "" : obj[3].toString().replace("\\", "\\\\");
            _obj[4] = obj[4] == null ? "" : obj[4].toString();
            _obj[5] = obj[5] == null ? "" : obj[5].toString();
            String empLivingImgUrl = obj[6] == null ? "" : obj[6].toString();
            empLivingImgUrl = fileServer + ajustLivingImgUrl(empLivingImgUrl);
            _obj[6] = empLivingImgUrl;
            resultList.add(_obj);
        }
        /* 头像 处理[END] */
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, resultList);
		json = "{data:"+json+"}";
		printResult(G_SUCCESS,json);
		return null;
	}
	
	/**
     * 【私有方法】矫正生活照URL
     * */
    private String ajustLivingImgUrl(String fileName) {
        if (!"".equals(fileName)) {
            ServletContext application = (ServletContext) request.getSession()
                    .getServletContext();
            java.io.File file = new java.io.File(application.getRealPath("/")
                    + "/upload/peopleinfo/" + fileName);
            if (file.exists()) {
                return "/upload/peopleinfo/" + fileName;
            } else {
                String date = fileName.substring(0, 6);
                file = new java.io.File(application.getRealPath("/")
                        + "/upload/peopleinfo/" + date + "/" + fileName);
                if (file.exists()) {
                    return "/upload/peopleinfo/" + date + "/" + fileName;
                }
            }
        }
        return "/images/noliving.gif";
    }
	
	/**
	 * 评论
	 * @return
	 */
	public String setComment(){
		boolean result = false;
		String updateCommentId = request.getParameter("updateCommentId");
		String commentContent = request.getParameter("commentContent");
		if(updateCommentId!=null&&!updateCommentId.equals("")) {//修改评论
			result = informationBD.updateComment(commentContent,updateCommentId);
		} else {//新增评论
			HttpSession session = request.getSession(true);
            String userId = session.getAttribute("userId").toString();
            String userName = session.getAttribute("userName").toString();
            String orgName = session.getAttribute("orgName").toString();
            Long orgId = new Long(session.getAttribute("orgId").toString());
            Long domainId = new Long(CommonUtils.getSessionDomainId(request).toString());
            result = informationBD.setComment(userId,userName,orgName,commentContent,information.getInformationId().toString());
            new NewInformationBD().saveInfostat(information.getInformationId(),new Long(channelId),"comment",
            		orgId,orgName,new Long(userId),userName,domainId);
		}
		if(result){
			printResult("success");
		}
		return null;
	}
	
	/**
	 * 删除信息中的某评论
	 * @return
	 */
	public String deleteComment(){
		String commentId = request.getParameter("commentId");
		informationBD.delComment(commentId);
		printResult("success");
		return null;
	}
	/**
	 * 删除信息中的某评论
	 * -20151106 by jqq 同时将信息表中评论数量减一
	 * @return
	 */
	public String deleteComment2(){
		String commentId = request.getParameter("commentId");
		String informationId = request.getParameter("informationId");
		informationBD.delComment2(commentId,informationId);
		printResult("success");
		return null;
	}
	
	/**
	 * 取信息的历史版本
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String historyList(){
		List list = informationBD.getHistoryVersion(information.getInformationId().toString());
		String viewSQL = "aaa.historyVersion,aaa.historyIssueOrg,aaa.historyIssuerName,aaa.historyTime,aaa.historyId," +
				"aaa.historyHead,ccc.channelId,ccc.channelType,aaa.isoDealCategory,aaa.isoAmendmentPage,aaa.isoModifyReason," +
				"aaa.historyMark, po.historyIssuerId ";
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, list);
		json = "{data:"+json+"}";
		printResult(G_SUCCESS,json);
		return null;
	}
	
	/**
	 * 历史版本修改备注
	 * @return
	 */
	public String modifyHistory(){
		String historyId = request.getParameter("historyId");
    	String historyMark = request.getParameter("historyMark");
    	String tempHistoryMark = request.getParameter("tempHistoryMark");
    	if(historyId!=null && !historyId.equals("")){
    		if(!historyMark.equals(tempHistoryMark)){
    			informationBD.updateHistory(historyId, historyMark);
    		}
    	}
    	printResult("success");
		return null;
	}
	
	/**
	 * 删除历史版本
	 * @return
	 */
	public String deleteHistory(){
		String historyId = request.getParameter("historyId");
        String result = informationBD.deleteHistory(historyId,"");//此方法只需要historyId参数即可
        if (result.equals("1")){
        	printResult("success");
        }
		return null;
	}
	
	/**
	 * 归档
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String file(){
		String pageContent = request.getParameter("pageContent");
		String gdType      = request.getParameter("gdType");
		HttpSession session = request.getSession(true);
        //String isIso = request.getParameter("isIso")==null ? "0" : request.getParameter("isIso").toString();
        String dossierCode = "ZSGL";
        if("isoDoc".equals(gdType)){
        	dossierCode ="WDGL";
        }
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        java.util.Date gdDate = new java.util.Date();
        String tmp = "ZS_" + request.getParameter("informationId") + "_" + now.get(Calendar.YEAR) +(now.get(Calendar.MONTH) + 1) + now.get(Calendar.DATE) + ".htm";
        ActionContext ac = ActionContext.getContext();   
        ServletContext sc = (ServletContext) ac.get(ServletActionContext.SERVLET_CONTEXT);   
        String path = sc.getRealPath("/");  
        String fileName = path + "/archivesfile/" + tmp;
        informationBD.setDossierStatus(request.getParameter("informationId"), "1");

        com.whir.ezoffice.dossier.bd.DossierBD dossierBD = new com.whir.ezoffice.dossier.bd.DossierBD();
        dossierBD.gdDossier(request.getParameter("informationId"),
                dossierCode,
                request.getParameter("information.informationTitle"),
                null,
                null,
                null,
                session.getAttribute("orgName") + "",
                now.get(Calendar.YEAR)+"",
                null,
                null,
                "",
                null,
                session.getAttribute("userId").toString(),
                session.getAttribute("orgId").toString(),
                sdf.format(gdDate),
                session.getAttribute("domainId").toString(),
                "htm",
                tmp,
                null,
                null,
                null);
        File file = new File(fileName);
        String enc = "UTF-8";
        OutputStreamWriter os = null;
        
        String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
        try {
            if (file.isFile()) {
                file.deleteOnExit();
                file = new File(file.getAbsolutePath());
            }
            os = new OutputStreamWriter(new FileOutputStream(file), enc);
            String pageStyle_common =request.getParameter("pageStyle_common")==null?"":request.getParameter("pageStyle_common");
            String pageStyle_style  =request.getParameter("pageStyle_style")==null?"":request.getParameter("pageStyle_style");
            String pageStyle ="<link href=\""+pageStyle_common+"\" rel=\"stylesheet\" type=\"text/css\"/>\n<link href=\""+pageStyle_style+"\" rel=\"stylesheet\" type=\"text/css\"/>";
            os.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
            		+ "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><script type=\"text/javascript\">\n"
            		+ "var whirRootPath = \"/defaultroot\";\n"
            		+ "var preUrl = \"/defaultroot\";\n"
            		+ "var whir_browser = \"msie\";\n"
            		+ "var whir_agent = \"\";\n"
            		+ "var whir_locale = \"zh_cn\";\n"
            		+ "</script>\n<title>信息查看</title>\n" + pageStyle + "\n" 
            		+"<LINK rel=stylesheet href=\""+rootPath+"/template/css/template.bootstrap.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_system/template.reset.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_system/template.fa.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_default/template.media.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_default/themes/2016/template.theme.media.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_default/template.detail.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_default/themes/2016/template.theme.detail.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_default/template.detail.size.min.css\"><LINK rel=stylesheet href=\""+rootPath+"/template/css/template_system/template.print.min.css\">"
            		+"<LINK rel=stylesheet type=text/css href=\""+rootPath+"/themes/common/style_ie7.css\" media=screen>"
            		+ "<script src=\""+rootPath+"/scripts/jquery-1.8.0.min.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/i18n/zh_CN/CommonResource.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/plugins/lhgdialog/lhgdialog.js?skin=idialog\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/plugins/form/jquery.form.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/main/whir.validation.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/main/whir.application.js\" type=\"text/javascript\"></script>\n"
            		
            		+ "<script src=\""+rootPath+"/scripts/main/whir.util.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/static/template.min.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/static/template.extend.min.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/static/template.custom.min.js\" type=\"text/javascript\"></script>\n"
            		+ "<script src=\""+rootPath+"/scripts/main/whir.application_2016.js\" type=\"text/javascript\"></script>\n"
            		+ "</head>\n<body>\n" + pageContent + "\n</body></html>");
            //2013-09-11-----上传ftp-----start
            String domainId = CommonUtils.getSessionDomainId(request).toString();
            java.util.Map sysMap = com.whir.org.common.util.SysSetupReader.getInstance().getSysSetupMap(domainId);
            if (sysMap != null && sysMap.get("附件上传") != null && sysMap.get("附件上传").toString().equals("0")) {
            	String fileServer = com.whir.component.config.ConfigReader.getFileServer(request.getRemoteAddr());
            	logger.debug("fileServer----->"+fileServer);
            	java.util.Map ftpMap = com.whir.component.config.ConfigReader.getUploadMap(request.getRemoteAddr(), domainId);
            	logger.debug("server----->"+ftpMap.get("server").toString());
            	logger.debug("port----->"+ftpMap.get("port").toString());
            	logger.debug("user----->"+ftpMap.get("user").toString());
            	logger.debug("oriPass----->"+ftpMap.get("oriPass") + "whir?!");
            	com.whir.govezoffice.documentmanager.common.util.NewFtpClient ftpClient1 = new com.whir.govezoffice.documentmanager.common.util.NewFtpClient(
            			ftpMap.get("server").toString(),
            			ftpMap.get("port").toString(),
            			ftpMap.get("user").toString(),
            			ftpMap.get("oriPass") + "whir?!",
            			fileName,
            			tmp,
            			"/archivesfile/");
            	ftpClient1.upload2();
            }
            //2013-09-11-----上传ftp-----end
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
		return "file";
	}
	
	/**
	 * 转移页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String toTransfer(){
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        //20151209 -by jqq 易播栏目改造：易播转移只能到易播栏目，反之非易播只能转移非易播
        InformationChannelPO channel = new ChannelBD().loadChannel(channelId);
        String isyibochannel = channel.getIsYiBoChannel() == null || "".equals(channel.getIsYiBoChannel()) ? "0" : channel.getIsYiBoChannel();
        //logger.debug("========isyibochannel:" + isyibochannel);
        List list = new ArrayList();
        if(iso!=null && iso.equals("1")){
        	list = InfoUtils.getIsoCanIssueChannel_YiBo(userId, orgId, orgIdString, domainId, isyibochannel);
        	request.setAttribute("canIssueChannel", list);
        }else {
        	list = InfoUtils.getCanIssueChannel_YiBo(userId, orgId, orgIdString,domainId,isyibochannel);
            String columnJson = InfoUtils.columnListToJson(list, false);
            request.setAttribute("canIssueChannel", columnJson);
        }
        //InformationChannelPO channel = new ChannelBD().loadChannel(channelId);
        List list1 = new ArrayList();
        String userChannelName_temp = "";
        //logger.debug("========转移userChannelName：" + userChannelName);
        try {
        	//20151224 -by jqq 直接从portal的栏目进入到信息列表，点击转移进入页面乱码显示改造
        	userChannelName_temp = java.net.URLDecoder.decode(userChannelName,"utf-8");
        	//logger.debug("========转移userChannelName解码：" + java.net.URLDecoder.decode(userChannelName,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}; 
        //logger.debug("========转移channel.getChannelNameString()：" + channel.getChannelNameString());
        //logger.debug("========转移request：" + request.getParameter("userChannelName"));
        Object[] obj = new Object[]{channel.getChannelId(),userChannelName_temp + "." + channel.getChannelNameString()};
        list1.add(obj);
        request.setAttribute("Channel", list1);
		return "transfer";
	}
	
	/**
	 * 转移
	 * @return
	 */
	public String transfer(){
		if(!informationId.equals("") && !channelId.equals("") && !selectChannel.equals("")){
			String[] ids = informationId.split(",");
			String toChannelId = selectChannel.indexOf(",")>-1 ? selectChannel.substring(0, selectChannel.indexOf(",")) : selectChannel;
			boolean result = informationBD.transfer(ids, toChannelId, channelId);
			if(result){
				printResult("success");
			}
		}
		return null;
	}
	
	/**
	 * 信息查看页面打印
	 * @return
	 */
	public String print(){
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        /*判断用户打印次数，若可以打印则返回1，并在信息打印表（OA_INFORMATION_PRINT）中更新数据,否则返回0*/
        String result = informationBD.userPrintNum(userId,informationId);
        //logger.debug("print:"+result);
        if(result.equals("1")){
        	/*信息积分统计*/
            NewInformationBD newInformationBD = new NewInformationBD();
            newInformationBD.saveInfostat(new Long(informationId),new Long(channelId),"print",new Long(orgId),orgName,
            		new Long(userId),userName,new Long(domainId));
            /*信息积分统计*/
            printJsonResult(result);
        } else {
        	printJsonResult(result);
        }
		return null;
	}
	
	/**
	 * 信息查看页面下载附件
	 * @return
	 */
	public String download(){
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String fileName = request.getParameter("fileName");
        String canDownload = "";
        ManagerBD managerBD = new ManagerBD();
		String downloadRight = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, 
				"aaa.informationDownLoader","aaa.informationDownLoaderOrg", "aaa.informationDownLoaderGroup");
		canDownload = informationBD.hasRight(informationId, downloadRight + " or aaa.informationDownLoaderName is null or aaa.informationDownLoaderName = ''");
		String result = "";
		if(canDownload.equals("1")){
			result = informationBD.updateDownloadNum(userId,informationId,fileName);
		}else{
			result = canDownload;
		}
        logger.debug("print:"+result);
        printJsonResult(result);
		return null;
	}
	
	/**
	 * 取信息中的附件类型
	 * @return
	 */
	public String getAccessoryType(){
		String result = informationBD.getAccessoryType(informationId);
		response.setContentType("text/plain;charSet=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        try {
            PrintWriter pw = response.getWriter();
            pw.print(result);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	/**
	 * 取信息中的一张图片
	 * @return
	 */
	public String getOneInfoPic(){
		InformationAccessoryBD accessoryBD = new InformationAccessoryBD();
		String result = accessoryBD.getOneInfoPic(informationId);
		response.setContentType("text/plain;charSet=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        try {
            PrintWriter pw = response.getWriter();
            pw.print(result);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
	
	/**
	 * 取信息的评论数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getCommentNum(){
        List commentList = informationBD.getComment(informationId);
        printJsonResult("{\"num\":\""+commentList.size()+"\"}");
        return null;
	}
	
	/**
	 * 历史版本查看页面
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String historyView() throws ParseException{
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
		String historyId = request.getParameter("historyId");
		String informationType = request.getParameter("informationType");
		information = informationBD.load(informationId);
		List list = informationBD.getSingleHistInfo(historyId);
		Object[] obj = (Object[]) list.get(0);
		//标题
		information.setInformationTitle(obj[0].toString());
		//副标题
		information.setInformationSubTitle(obj[1]!=null?obj[1].toString():"");
		//内容
		String content = "";
		if(list.get(1) == null){
            information.setInformationContent("");
        }else{
            content = list.get(1).toString();
            information.setInformationContent(content);
        }
		//修改人
		if (obj[3] != null && !obj[3].toString().equals("")) {
			information.setModifyEmp(obj[4] + "." + obj[3]);
        } else {
        	information.setModifyEmp("");
        }
		//修改时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String modifyTime = obj[6].toString();
        if(modifyTime.indexOf(".") > 0){
        	//information.setInformationModifyTime(sdf.parse(modifyTime.substring(0,modifyTime.indexOf("."))));
        }else{
        	
        }
        //取信息版本
        if(obj[7] != null){
        	//20160512 -by jqq改造历史版本查看，返回最新版本
            //information.setInformationVersion(obj[7].toString());
        }else{
        	information.setInformationVersion("");
        }
        //取信息作者
        if(obj[8] == null){
            information.setInformationAuthor("");
        }else{
        	information.setInformationAuthor(obj[8].toString());
        }
        //取附件和图片
        InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
        list = informationAccessoryBD.getHistAccessory(historyId);
        String infoPicName = "";
        String infoPicSaveName = "";
        String infoAppendName = "";
        String infoAppendSaveName = "";
        Object[] tmp = null;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                tmp = (Object[]) list.get(i);
                if(tmp!=null && tmp[1]!=null){
                    if (tmp[4].toString().equals("1")) {
                    	infoPicName += tmp[1].toString()+"|";
                    	infoPicSaveName += tmp[2].toString()+"|";
                    } else {
                    	infoAppendName += tmp[1].toString()+"|";
                    	infoAppendSaveName += tmp[2].toString()+"|";
                    }
                }
            }
        }
        if(infoPicName.endsWith("|")){
        	infoPicName = infoPicName.substring(0,infoPicName.length()-1);
        	infoPicSaveName = infoPicSaveName.substring(0,infoPicSaveName.length()-1);
        }
        if(infoAppendName.endsWith("|")){
        	infoAppendName = infoAppendName.substring(0,infoAppendName.length()-1);
        	infoAppendSaveName = infoAppendSaveName.substring(0,infoAppendSaveName.length()-1);
        }
        request.setAttribute("infoPicName", infoPicName);
        request.setAttribute("infoPicSaveName", infoPicSaveName);
        request.setAttribute("infoAppendName", infoAppendName);
        request.setAttribute("infoAppendSaveName", infoAppendSaveName);
        //是否拷贝打印
        information.setForbidCopy(Integer.valueOf(obj[16].toString()).intValue());
        //细览显示标题
        information.setDisplayTitle(Integer.valueOf(obj[19].toString()).intValue());
        //细览显示图片
        if(obj[20]!=null){
        	information.setDisplayImage(obj[20].toString());
        }
        //关键字
        if (obj[15] == null) {
        	information.setInformationKey("");
        } else {
        	information.setInformationKey(obj[15].toString());
        }
        //摘要
        if (obj[14] == null) {
            information.setInformationSummary("");
        } else {
        	information.setInformationSummary(obj[14].toString());
        }
        //信息类型-文件链接
        if("3".equals(informationType)){
        	String fileName = "";
            String saveName = "";
            if(content != null && !"".equals(content)){
            	request.setAttribute("fileLink", "fileLink");
                String[] file = content.split(":");
                fileName = file[0].toString();
                saveName = file[1].toString();
                request.setAttribute("fileName", fileName);
                request.setAttribute("saveName", saveName);
                request.setAttribute("fileType",saveName.substring(saveName.indexOf(".")+1).toUpperCase());
            }
        }
        request.setAttribute("content", content);
        //取栏目名称串
        ChannelBD channelBD = new ChannelBD();
        String checkdepart = "";
        if (request.getParameter("checkdepart") != null) {
            checkdepart = request.getParameter("checkdepart");
        }
        String OrgName = "";
        String channelNameString = "";
        List channel = channelBD.getSingleChannel(channelId);
        if (channel != null && channel.size() > 0) {
            Object[] objid = null;
            objid = (Object[]) channel.get(0);
            channelNameString = objid[29].toString();
        }
        if (checkdepart.equals("1")) {
            List getOrgName = informationBD.getOrgName(channelId);
            if (getOrgName != null && getOrgName.size() > 0) {
                Object[] objorgname = null;
                objorgname = (Object[]) getOrgName.get(0);
                OrgName = objorgname[0].toString();
                userChannelName = OrgName;
            }
        }
        channelNameString = userChannelName + "." + channelNameString;
        request.setAttribute("channelNameString", channelNameString);
        //评论次数
        List commentList = informationBD.getComment(informationId);
        request.setAttribute("commentNum", commentList.size());
        /*判断用户是否有栏目的维护权限*/
        boolean canVindicate = channelBD.canVindicate(userId, orgId, channelId);
        request.setAttribute("canVindicate", canVindicate);
        /*判断用户是否有栏目的维护权限*/
        
        /*文档管理查看页面-相关信息*/
		List assoicateInfoList = new ArrayList();
		//2013-09-03-----start
		//logger.debug("channelType----->"+channelType);
		//logger.debug("userDefine----->"+userDefine);
		if(CommonUtils.isEmpty(userDefine)){
			userDefine ="0";
		}
		if(CommonUtils.isEmpty(channelType)){
			channelType ="0";
		}
		//2013-09-03-----end
		assoicateInfoList = informationBD.getAssociateInfo(orgId, informationId, userId, orgIdString, channelType, userDefine, "2");
		if(assoicateInfoList!=null && assoicateInfoList.size()>0){
			request.setAttribute("assoicateInfo", assoicateInfoList);
		}
		//20160627 -by jqq 文档管理查看历史版本时，图片路径不使用信息，而应该为文档的上传目录
		if (obj[21] == null || "".equals(obj[21].toString()) || "null".equals(obj[21].toString())) {
		    //说明是信息类型，该字段iso处理类别
		    request.setAttribute("isoFlag", "0");
        } else {
            //说明是文档类型
            request.setAttribute("isoFlag", "1");
        }
        /*文档管理查看页面-相关信息*/
        
        /*判断用户是否有删除评论的权限*/
		if (!canVindicate) {
			com.whir.org.manager.bd.ManagerBD mbd = new com.whir.org.manager.bd.ManagerBD();
			List rightList = mbd.getRightScope(userId, "01*03*03");
			//用户可以修改的信息ID串
			String canModiIds = informationBD.getInformationModiIds(channelId, userId, orgId, orgIdString, 
					informationId, rightList);
			if (canModiIds.indexOf("," + informationId + ",") >= 0) {
				canVindicate = true;
			}
		}
		request.setAttribute("delComment", canVindicate ? "1" : "0");
		/*判断用户是否有删除评论的权限*/
		//20160711 -by jqq 增加标识，历史版本的查看页面，用于控制查看页标签/评论/点赞等功能不展示
		request.setAttribute("hisFlag", "1");
		return "view";
	}
	
	/**
	 * 
	 * @param fileNames
	 * @param session
	 * @param localPath
	 * @param httpServletRequest
	 * @param fileType 1-附件  0-金格正文
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean copyFileFromDocument(String[] fileNames,
			HttpSession session, String localPath,
			HttpServletRequest httpServletRequest, String fileType) {
		// 判断文件是否在本地
		if (fileNames != null && fileNames.length > 0) {
			try {
				java.util.Map sysMap = com.whir.org.common.util.SysSetupReader.getInstance().getSysSetupMap(session.getAttribute("domainId").toString());
				if (sysMap != null && sysMap.get("附件上传") != null && sysMap.get("附件上传").toString().equals("0")) {
					java.util.Map ftpMap = com.whir.component.config.ConfigReader.getUploadMap(httpServletRequest.getRemoteAddr(),session.getAttribute("domainId").toString());
					com.whir.common.util.NewFtpClient ftpClient = new com.whir.common.util.NewFtpClient(
                            ftpMap.get("server").toString(),ftpMap.get("port").toString(),ftpMap.get("user").toString(),ftpMap.get("oriPass") + "whir?!","govdocumentmanager","information", "");
					for (int i = 0; i < fileNames.length; i++) {
						String localFile = "";
						if(isLinux){//Linux
							localFile = localPath + "/govdocumentmanager/" ;
						}else{
							localFile = localPath + "\\govdocumentmanager\\";
						}
						if(fileType.equals("1")){
							localFile += fileNames[i].substring(0, 6) + (isLinux?"/":"\\");
						}
						// 下载
						boolean download = ftpClient.downloadFile("govdocumentmanager\\"+ (fileType.equals("1")?fileNames[i].substring(0, 6)+"\\":"") + fileNames[i], 
								localFile + fileNames[i]);
						//System.out.println("copyFileFromDocument-download:"+i+download);
						// 上传
						ftpClient = new com.whir.common.util.NewFtpClient(
	                            ftpMap.get("server").toString(),ftpMap.get("port").toString(),ftpMap.get("user").toString(),ftpMap.get("oriPass") + "whir?!", 
								localFile + fileNames[i], fileNames[i],"information");
						ftpClient.upload2();
					}
				} else {
					//本地拷贝
					for (int i = 0; i < fileNames.length; i++) {
						if(isLinux){//Linux
							copyFile(localPath + "/govdocumentmanager/",fileNames[i], localPath + "/information/"+ fileNames[i]);
						}else{
							copyFile(localPath + "\\govdocumentmanager\\",fileNames[i], localPath + "\\information\\"+ fileNames[i]);
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean copyFileFromInformation(String sourceName, String targetName, HttpSession session, String localPath, HttpServletRequest httpServletRequest) {
		// 判断文件是否在本地
		try {
			java.util.Map sysMap = com.whir.org.common.util.SysSetupReader.getInstance().getSysSetupMap(session.getAttribute("domainId").toString());
			if (sysMap != null && sysMap.get("附件上传") != null && sysMap.get("附件上传").toString().equals("0")) {
				java.util.Map ftpMap = com.whir.component.config.ConfigReader.getUploadMap(httpServletRequest.getRemoteAddr(),session.getAttribute("domainId").toString());
				com.whir.common.util.NewFtpClient ftpClient = new com.whir.common.util.NewFtpClient(
                        ftpMap.get("server").toString(),ftpMap.get("port").toString(),ftpMap.get("user").toString(),ftpMap.get("oriPass") + "whir?!","information","information", "");
				String localFile = "";
				if(isLinux){//Linux
					localFile = localPath + "/information/" ;
				}else{
					localFile = localPath + "\\information\\";
				}
				// 下载
				ftpClient.downloadFile("information\\" + sourceName, localFile + sourceName);
				// 上传
				ftpClient = new com.whir.common.util.NewFtpClient(
						ftpMap.get("server").toString(), ftpMap.get("port").toString(),ftpMap.get("user").toString(), ftpMap.get("oriPass") + "whir?!", 
						localFile+ targetName, targetName, "information");
				ftpClient.upload();
			} else {
				//本地拷贝
				if(isLinux){//Linux
					copyFile(localPath + "/information/",sourceName, localPath + "/information/"+ targetName);
				}else{
					copyFile(localPath + "\\information\\",sourceName, localPath + "\\information\\"+ targetName);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}
	
	public boolean copyFile(String path, String fileName, String aimName) {
		FileInputStream from = null;
		FileOutputStream to = null;
		String srcName = path + fileName;
		try {
			File file = new File(srcName);
			if (!file.exists()) {
				if(isLinux){//Linux
					srcName = path + fileName.substring(0, 6) + "/"+ fileName;
				}else{
					srcName = path + fileName.substring(0, 6) + "\\"+ fileName;
				}
			}
			from = new FileInputStream(srcName);
			to = new FileOutputStream(aimName);
			byte[] buffer = new byte[4096];
			int pos;
			while ((pos = from.read(buffer)) != -1) {
				to.write(buffer, 0, pos);
			}
			if (from != null) {
				from.close();
			}
			if (to != null) {
				to.close();
			}
			return true;
		} catch (Exception ex) {
			System.out.println("复制文件出错！" + ex);
			return false;
		}
	}
	
	/**
     * 信息点赞功能
     * @throws Exception 
     */
	public String praiseInfo() throws Exception {
	    HttpSession session = this.request.getSession(true);
	    String praiseUserId = session.getAttribute("userId").toString();
	    String praiseInformationId = this.request.getParameter("praiseInformationId");
	    String praiseType = this.request.getParameter("praiseType");
	
	    String flag = "0";
	
	    if (!CommonUtils.isEmpty(praiseInformationId)) {
	      InformationBD bd = new InformationBD();
	      flag = bd.updateInfoPraise(praiseUserId, praiseInformationId, praiseType);
	    }
	
	    if ("1".equals(flag))
	      printResult("success");
	    else if ("2".equals(flag))
	      printResult("已赞同！");
	    else {
	      printResult("点赞失败！");
	    }
	    return null;
	}
	
	/**
     * 信息查看详细记录
     * @throws Exception 
     */
	public String viewRecords() throws Exception {
		//logger.debug("========查看信息的详细记录Action查询开始========");
		HttpSession session = this.request.getSession(true);
	    String userId = session.getAttribute("userId").toString();
	    String orgId = session.getAttribute("orgId").toString();
	    String searchuserName = this.request.getParameter("searchuserName");
	    String searchOrgName = this.request.getParameter("searchOrg");
	    //logger.debug("========searchuserName:" + searchuserName);
	    //logger.debug("========searchOrgName:" + searchOrgName);
		
	    NewInformationBD newifnobd = new NewInformationBD();
		String json = newifnobd.viewDetailInfo(searchuserName, searchOrgName, request);

		//logger.debug("========列表数据:\n"+json);
		printResult(G_SUCCESS,json);
		//logger.debug("========查询列表Action结束========");
		return null;
	}
	/**
     * infodetaillist页面
     * @throws Exception 
     */
	public String infodetail() throws Exception {
		//logger.debug("========infodetail页面开始========");
		//logger.debug("========infodetail页面结束========");
		return "infodetaillist";
	}
	
	/**
     * infodetaillist页面
     * @throws Exception 
     */
	public String judgeProcess() throws Exception {
		String result = "0";
		//logger.debug("========judgeProcess开始========");
		String infoId = request.getParameter("informationId");
		String moduleId = request.getParameter("moduleId");
		
		BPMInstanceBD bpmbd = new BPMInstanceBD();
		BPMProcessInstancePO procpo = bpmbd.loadInstanceInfo(moduleId,infoId);
		if(procpo != null){
			result = "1";
		}
		try {
            PrintWriter pw = response.getWriter();
            //logger.debug("========judgeProcess:result 值为：" + result);
            pw.print(result);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
            //logger.error("[InformationAction][method=judgeProcess]异常:" + e);
        }
        //logger.debug("========judgeProcess结束========");
		return null;
	}
	
	/**
	 * 信息查看页面打印次数判断：0-无；1-有
	 * @return result String
	 * @throws Exception 
	 */
	public String judgePrintNum() throws Exception{
		HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        /*判断用户打印次数，若可以打印则返回1,否则返回0*/
        String result = informationBD.judgePrintNum(userId,informationId);
        //logger.debug("信息查看页面打印次数判断[judgePrintNum]result:"+result);
        printJsonResult(result);
        
        return null;
	}
	
	/**
     * 保存信息草稿
     * @return
     * @throws ParseException 
     */
    public String saveDraft() throws ParseException{
        HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgName = session.getAttribute("orgName").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        String[] para = {userId, userName, orgId, orgName, orgIdString};
        //普通文本编辑方式，要处理换行
        if(information.getInformationType().equals("0")){
            if(information.getInformationContent()!=null&&information.getInformationContent().indexOf("\n")>-1){
                information.setInformationContent(information.getInformationContent().replaceAll("\n", "<br>"));
            }
        }
        //设置信息栏目
        if(selectChannel != null && !"".equals(selectChannel)){
            ChannelBD channelBD = new ChannelBD();
            information.setInformationChannel(channelBD.loadChannel(selectChannel.substring(0,selectChannel.indexOf(","))));
        }
        
        information.setDomainId(Long.valueOf(domainId));
        information.setInformationIssuer(userName);
        information.setInformationIssuerId(Long.valueOf(userId));
        information.setInformationIssueOrg(orgName);
        information.setInformationIssueOrgId(orgId);
        information.setIssueOrgIdString(orgIdString);
        //information.setInformationIssueTime(new Date());
        String displayTitle = request.getParameter("information.displayTitle");
        if(displayTitle!=null && "1".equals(displayTitle)){
            information.setDisplayTitle(0);
        }else{
            information.setDisplayTitle(1);
        }
        String titleColor = request.getParameter("information.titleColor");
        if(titleColor!=null && "1".equals(titleColor)){
            information.setTitleColor(1);
        }else{
            information.setTitleColor(0);
        }
        if(information.getDisplayImage()==null||information.getDisplayImage().equals("")){
            information.setDisplayImage("1");
        }else{
            information.setDisplayImage("0");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String issueTime = request.getParameter("information.informationIssueTime");
        //草稿的创建时间默认为服务器的时间
        information.setInformationIssueTime(new Date());
        if(information.getInformationValidType()==1){
            String validBeginTime = request.getParameter("information.validBeginTime") + " 00:00:00";
            String validEndTime = request.getParameter("information.validEndTime") + " 23:59:59";
            information.setValidBeginTime(sdf.parse(validBeginTime));
            information.setValidEndTime(sdf.parse(validEndTime));
        }
        information.setOtherChannel(","+(information.getOtherChannel()!=null?information.getOtherChannel():"0")+",");
        /*设置信息的可查看人*/
        String informationReader = request.getParameter("informationReaderId");
        if(informationReader!=null){
            com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationReader);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationReader(userIds);
            information.setInformationReaderOrg(orgIds);
            information.setInformationReaderGroup(groupIds);
        }else{
            information.setInformationReaderName(information.getInformationChannel().getChannelReaderName());
            information.setInformationReader(information.getInformationChannel().getChannelReader());
            information.setInformationReaderOrg(information.getInformationChannel().getChannelReaderOrg());
            information.setInformationReaderGroup(information.getInformationChannel().getChannelReaderGroup());
        }
        /*设置信息的可查看人*/
        
        /*提醒方式*/
        String remind_im = request.getParameter("remind_im")!=null?request.getParameter("remind_im"):"";
        String remind_sms = request.getParameter("remind_sms")!=null?request.getParameter("remind_sms"):"";
        String remind_mail = request.getParameter("remind_mail")!=null?request.getParameter("remind_mail"):"";
        String remindType = (remind_im.equals("im")?"im|":"")+(remind_sms.equals("sms")?"sms|":"")+(remind_mail.equals("mail")?"mail":"");
        remindType = remindType.endsWith("|") ? remindType.substring(0,remindType.length()-1) : remindType;
        information.setRemindType(remindType);
        /*提醒方式*/
        
        /*设置信息的可打印人*/
        String informationPrinterId = request.getParameter("informationPrinterId");
        if(informationPrinterId!=null){
            com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationPrinterId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationPrinter(userIds);
            information.setInformationPrinterOrg(orgIds);
            information.setInformationPrinterGroup(groupIds);
        }else{
            information.setInformationPrinterName(information.getInformationChannel().getChannelPrinterName());
            information.setInformationPrinter(information.getInformationChannel().getChannelPrinter());
            information.setInformationPrinterOrg(information.getInformationChannel().getChannelPrinterOrg());
            information.setInformationPrinterGroup(information.getInformationChannel().getChannelPrinterGroup());
        }
        /*设置信息的可打印人*/
        
        /*设置信息的可下载人*/
        String informationDownLoaderId = request.getParameter("informationDownLoaderId");
        if(informationDownLoaderId!=null){
            com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationDownLoaderId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationDownLoader(userIds);
            information.setInformationDownLoaderOrg(orgIds);
            information.setInformationDownLoaderGroup(groupIds);
        }else{
            information.setInformationDownLoaderName(information.getInformationChannel().getChannelDownLoaderName());
            information.setInformationDownLoader(information.getInformationChannel().getChannelDownLoader());
            information.setInformationDownLoaderOrg(information.getInformationChannel().getChannelDownLoaderOrg());
            information.setInformationDownLoaderGroup(information.getInformationChannel().getChannelDownLoaderGroup());
        }
        /*设置信息的可下载人*/
        
        /*信息的附件和图片*/
        String infoPicName = request.getParameter("infoPicName");
        String infoPicSaveName = request.getParameter("infoPicSaveName");
        String infoAppendName = request.getParameter("infoAppendName");
        String infoAppendSaveName = request.getParameter("infoAppendSaveName");
        String[] infoPicNames = null;
        String[] infoPicSaveNames = null;
        String[] infoAppendNames = null;
        String[] infoAppendSaveNames = null;
        if(infoPicName!=null && !infoPicName.equals("")){
            infoPicNames = infoPicName.split("\\|");
            infoPicSaveNames = infoPicSaveName.split("\\|");
        }
        if(infoAppendName!=null && !infoAppendName.equals("")){
            infoAppendNames = infoAppendName.split("\\|");
            infoAppendSaveNames = infoAppendSaveName.split("\\|");
        }
        //信息保存为草稿,对应状态informationstatus :9-草稿
        information.setInformationStatus(new Integer(9));
        //20160516 -by jqq 信息公共标签（原关键字字段）的保存
        String publicTagsId = request.getParameter("publicTagsId")==null ? "" : request.getParameter("publicTagsId");
        String publicTagsName = request.getParameter("information.informationKey")==null ? "" : request.getParameter("information.informationKey");
        //存储结构为：id1,id2,id3|name1,name2,name3
        information.setInformationKey(publicTagsId + "|" + publicTagsName);
        
        //保存信息
        Long infoId = informationBD.add(information,para, null,infoPicNames,infoPicSaveNames,infoAppendNames,infoAppendSaveNames, domainId);
        //保存标签-信息关系(公共标签)
        if(!"".equals(publicTagsId)){
            InfoTagBD bd = new InfoTagBD();
            String[] tagIdArr = publicTagsId.split(",");
            for(int i=0; i<tagIdArr.length; i++){
                try {
                    bd.saveTagRelation(Long.valueOf(tagIdArr[i]), infoId, "0");
                } catch (Exception tags_e) {
                    tags_e.printStackTrace();
                }
            }
        }
        //信息同步到EZSITE网站多站点方式
        //informationBD.synchronizeToMoreSite(information, infoId, infoPicNames,infoPicSaveNames,infoAppendNames,infoAppendSaveNames,domainId, request);
        if(infoId != null){
            //相关性
            RelationBD relationBD = new RelationBD();
            relationBD.saveRelationList(request,infoId+"");
            //相关性中新建信息
            if(request.getParameter("relationNew")!=null && "1".equals(request.getParameter("relationNew"))){
                printResult("success","{informationId:'"+infoId+"',informationTitle:'"+information.getInformationTitle()+"'," +
                        "informationType:'"+information.getInformationType()+"'," +
                        "channelId:'"+selectChannel.substring(0,selectChannel.indexOf(","))+"'," +
                        "userChannelName:'"+userChannelName+"',channelType:'"+channelType+"',userDefine:'"+userDefine+"'}");
            }else{
                printResult("success");
            }
        }
        
        return null;
    }
    
    public String loadDraft() throws Exception {
        HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        //是否审核标识
        String channelNeedCheckup = request.getParameter("channelNeedCheckup") == null ? "0" : request.getParameter("channelNeedCheckup");
        //取模板
        List templateList = InfoUtils.getTemplate(userId, orgId, orgIdString, domainId);
        if (templateList != null) {
            request.setAttribute("templateList", templateList);
        } else {
            request.setAttribute("templateList",new java.util.ArrayList());
        }
        //附件和图片
        InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
        List list = informationAccessoryBD.getAccessory(informationId);
        String infoPicName = "";
        String infoPicSaveName = "";
        String infoAppendName = "";
        String infoAppendSaveName = "";
        Object[] tmp = null;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                tmp = (Object[]) list.get(i);
                if (tmp[4].toString().equals("1")) {
                    infoPicName += tmp[1].toString()+"|";
                    infoPicSaveName += tmp[2].toString()+"|";
                } else {
                    infoAppendName += tmp[1].toString()+"|";
                    infoAppendSaveName += tmp[2].toString()+"|";
                }
            }
        }
        if(infoPicName.endsWith("|")){
            infoPicName = infoPicName.substring(0,infoPicName.length()-1);
            infoPicSaveName = infoPicSaveName.substring(0,infoPicSaveName.length()-1);
        }
        if(infoAppendName.endsWith("|")){
            infoAppendName = infoAppendName.substring(0,infoAppendName.length()-1);
            infoAppendSaveName = infoAppendSaveName.substring(0,infoAppendSaveName.length()-1);
        }
        request.setAttribute("infoPicName", infoPicName);
        request.setAttribute("infoPicSaveName", infoPicSaveName);
        request.setAttribute("infoAppendName", infoAppendName);
        request.setAttribute("infoAppendSaveName", infoAppendSaveName);
        information = informationBD.load(informationId);
        //修改信息时，给信息的正在编辑用户赋值，其他用户将不能编辑
        if(information.getEditUserName()==null || "".equals(information.getEditUserName())){
            information.setEditUserId(userId);
            information.setEditUserName(userName);
            informationBD.save(information);
        }
        //提醒方式
        String remindType = information.getRemindType();
        request.setAttribute("remindType", remindType);
        //草稿的修改页面，发布时间取修改时间
        if(information.getInformationModifyTime() != null){
            information.setInformationIssueTime(information.getInformationModifyTime());
        }
        //可查看人
        String informationReaderId = (information.getInformationReader()!=null ? information.getInformationReader() : "")
            + (information.getInformationReaderOrg()!=null ? information.getInformationReaderOrg() : "")
            + (information.getInformationReaderGroup()!=null ? information.getInformationReaderGroup() : "");
        request.setAttribute("informationReaderId", informationReaderId);
        //信息可打印人
        String informationPrinterId = (information.getInformationPrinter()!=null ? information.getInformationPrinter() : "")
            + (information.getInformationPrinterOrg()!=null ? information.getInformationPrinterOrg() : "")
            + (information.getInformationPrinterGroup()!=null ? information.getInformationPrinterGroup() : "");
        request.setAttribute("informationPrinterId", informationPrinterId);
        //信息可下载人
        String informationDownLoaderId = (information.getInformationDownLoader()!=null ? information.getInformationDownLoader() : "")
            + (information.getInformationDownLoaderOrg()!=null ? information.getInformationDownLoaderOrg() : "")
            + (information.getInformationDownLoaderGroup()!=null ? information.getInformationDownLoaderGroup() : "");
        request.setAttribute("informationDownLoaderId", informationDownLoaderId);
        //20160419 -by jqq 信息草稿，对应栏目可能为空
        String isYiBoChannel = "";
        String informationReaderId_ = "";
        String informationPrinterId_ = "";
        String informationDownLoaderId_ = "";
        if(information.getInformationChannel() != null){
            //易播栏目改造，返回是否是易播栏目的标识
            isYiBoChannel = information.getInformationChannel().getIsYiBoChannel();
            //信息可查看人选择范围
            informationReaderId_ = (information.getInformationChannel().getChannelReader()!=null ? information.getInformationChannel().getChannelReader() : "")
            + (information.getInformationChannel().getChannelReaderOrg()!=null ? information.getInformationChannel().getChannelReaderOrg() : "")
            + (information.getInformationChannel().getChannelReaderGroup()!=null ? information.getInformationChannel().getChannelReaderGroup() : "");
            //信息可打印人选择范围
            informationPrinterId_ = (information.getInformationChannel().getChannelPrinter()!=null ? information.getInformationChannel().getChannelPrinter() : "")
            + (information.getInformationChannel().getChannelPrinterOrg()!=null ? information.getInformationChannel().getChannelPrinterOrg() : "")
            + (information.getInformationChannel().getChannelPrinterGroup()!=null ? information.getInformationChannel().getChannelPrinterGroup() : "");
            //信息可下载人选择范围
            informationDownLoaderId_ = (information.getInformationChannel().getChannelDownLoader()!=null ? information.getInformationChannel().getChannelDownLoader() : "")
            + (information.getInformationChannel().getChannelDownLoaderOrg()!=null ? information.getInformationChannel().getChannelDownLoaderOrg() : "")
            + (information.getInformationChannel().getChannelDownLoaderGroup()!=null ? information.getInformationChannel().getChannelDownLoaderGroup() : "");
        }
        request.setAttribute("isYiBoChannel",isYiBoChannel);
        request.setAttribute("informationDownLoaderId_", informationDownLoaderId_);
        request.setAttribute("informationReaderId_", informationReaderId_);
        request.setAttribute("informationPrinterId_", informationPrinterId_);
        
        //细览不显示标题（兼容老数据）
        if(information.getDisplayTitle()==0){
            information.setDisplayTitle(1);
        }else{
            information.setDisplayTitle(0);
        }
        //细览不显示图片（兼容老数据）
        //20151216 -by jqq 修复当DisplayImage在数据库中为空时候报异常的问题
        if(information.getDisplayImage() != null && information.getDisplayImage().equals("1")){
            information.setDisplayImage("0");
        }else{
            information.setDisplayImage("1");
        }
        /*信息公共标签查询*/
        List publicList =new ArrayList();
        List pubtagList =new ArrayList();
        InfoTagBD bd = new InfoTagBD();
        try {
            publicList = bd.getAllPublicTags();
            //20160516 -by jqq 该信息的公共标签（原关键字）
            pubtagList = bd.getPublicTagsByInfoId(informationId);
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        request.setAttribute("publicList", publicList);
        /*信息公共标签查询*/
        //20160531 -by jqq 每次load公共标签查询获取，不存储在信息表
        if(pubtagList != null && pubtagList.size() > 0){
            String ptagsId = "";
            String ptagsName = "";
            for(int k=0; k<pubtagList.size(); k++){
                Object[] obj = (Object[]) pubtagList.get(k);
                ptagsId += obj[0].toString() + ",";
                ptagsName += obj[1].toString() + ",";
            }
            request.setAttribute("publicTagsId",ptagsId.substring(0, ptagsId.length()-1));
            information.setInformationKey(ptagsName.substring(0, ptagsName.length()-1));
        }else{
            request.setAttribute("publicTagsId","");
            information.setInformationKey("");
        }
        //普通文本编辑的信息，要处理换行
        if(information.getInformationType().equals("0")){
            if(information.getInformationContent()!=null&&information.getInformationContent().indexOf("<br>")>-1){
                information.setInformationContent(information.getInformationContent().replaceAll("<br>", "\n"));
            }
        }
        if(information.getInformationType().equals("3")){//文件链接
            //20160630 -by jqq 文件链接保存草稿有可能内容为空的
            if(information.getInformationContent() != null && !"".equals(information.getInformationContent())){
                String[] file = information.getInformationContent().split(":");
                String fileLinkContent = file[0];
                String fileLinkContentHidd = file[1];
                request.setAttribute("fileLinkContent", fileLinkContent);
                request.setAttribute("fileLinkContentHidd", fileLinkContentHidd);
            }else{
                request.setAttribute("fileLinkContent", "");
                request.setAttribute("fileLinkContentHidd", "");
            }
        } else if (information.getInformationType().equals("2")){//地址链接
            request.setAttribute("urlContent", information.getInformationContent());
        } else if (information.getInformationType().equals("0")){//普通编辑
            request.setAttribute("textContent", information.getInformationContent());
        } else if (information.getInformationType().equals("4") || information.getInformationType().equals("5") || information.getInformationType().equals("6")){//word或excel或ppt编辑
            request.setAttribute("content", information.getInformationContent());
        }
        String channel = "";
        if(information.getInformationChannel() != null){
            channel = information.getInformationChannel().getChannelId()+","+information.getInformationChannel().getChannelName();
        }
        String otherChannel = information.getOtherChannel();
        request.setAttribute("channel", channel);
        request.setAttribute("other", otherChannel);
        /**-----------------------------------------   区分普通信息草稿与流程审核信息草稿   start  ------------------------------------------**/
        //对于需要流程审核的信息，页面加载时候区分处理
        if("1".equals(channelNeedCheckup)){
            channelId = information.getInformationChannel().getChannelId().toString();
            p_wf_openType = "reStart";
            //map参数备用
            setStartInfo(new HashMap());
            //设置打开流程的主地址
            this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+   
                    "/Information!updateProcess.action";
            //如果要自己控制按钮 可改变按钮属性  
            this.p_wf_modiButton = "Send,SaveInfoDraft";
            if (iso!=null && iso.equals("1")) {
                this.p_wf_modiButton = "Send,Saveclose,Print";  
            }
            this.p_wf_queryForm = "queryForm";
            //流程提醒信息标题
            this.p_wf_titleFieldName = "information.informationTitle";
            //信息或文档从不审核栏目修改成审核栏目时，不要删除流程记录
            String modifyToProcess = request.getParameter("modifyToProcess")!=null?request.getParameter("modifyToProcess"):"";
            request.setAttribute("modifyToProcess", modifyToProcess);
            //草稿
            String draft = request.getParameter("draft")!=null?request.getParameter("draft"):"";
            request.setAttribute("draft", draft);
            //走流程的信息修改时不能更改所属栏目，所以不需要取当前用户的可新建栏目，直接在list中放入当前信息的所属栏目即可
            String userChannelName = "信息管理";
            if(information.getInformationChannel().getUserDefine().equals("1")){
                userChannelName = new ChannelBD().getUserChannelName(information.getInformationChannel().getChannelId().toString());
            }else if(information.getInformationChannel().getChannelType()>0){
                userChannelName = new OrganizationBD().getOrgName(information.getInformationChannel().getChannelType()+"");
            }else if (iso!=null && iso.equals("1")) {
                userChannelName = "文档管理";
            }
            String[] channelArray = new String[]{information.getInformationChannel().getChannelId().toString(),
                    information.getInformationChannel().getChannelName(),
                    userChannelName+"."+information.getInformationChannel().getChannelName(),
                    information.getInformationChannel().getChannelType()+"",
                    information.getInformationChannel().getUserDefine()};
            List channelList = new ArrayList();
            channelList.add(channelArray);
            //走流程的信息修改时不能更改所属栏目，所以不需要取当前用户的可新建栏目，直接在list中放入当前信息的所属栏目即可
            if (iso!=null && iso.equals("1")) {
                request.setAttribute("canIssueChannel", channelList);
                List assoicateInfoList = new ArrayList();
                if(CommonUtils.isEmpty(userDefine)){
                    userDefine ="0";
                }
                if(CommonUtils.isEmpty(channelType)){
                    channelType ="0";
                }
                assoicateInfoList = informationBD.getAssociateInfo(orgId, informationId, userId, orgIdString, channelType, userDefine, "2");
                if(assoicateInfoList!=null && assoicateInfoList.size()>0){
                    request.setAttribute("assoicateInfo", assoicateInfoList);
                }
                this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+   
                "/Information!updateProcess.action?iso=1";
                if(destory!=null && "1".equals(destory)){
                    this.p_wf_mainLinkFile = com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+   
                    "/Information!updateProcess.action?iso=1&destory=1";
                }
            } else {
                String columnJson = InfoUtils.columnListToJson(channelList, false);
                request.setAttribute("canIssueChannel", columnJson);
                //同时发布到,11.1.0.0需求，同时发布到根据流程设置是否可写，取用户可发布的栏目+该信息当前同时发布到的栏目，
                //如果用户可发布的栏目不包含该信息当前同时发布到的栏目，则将该信息当前同时发布到的栏目加入到用户可发布的栏目
                List list2 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
                String otherchannel = information.getOtherChannel();
                if(otherchannel!=null && !"".equals(otherchannel) && !",0,".equals(otherchannel)){
                    boolean include = false;
                    for(int i=0;i<list2.size();i++){
                        Object[] ch = (Object[]) list2.get(i);
                        if((otherchannel.replaceAll(",", "")).equals(ch[0].toString())){
                            include = true;
                            break;
                        }
                    }
                    String otherChannelName = "信息管理";
                    if(!include){
                        otherchannel = otherchannel.replaceAll(",", "");
                        List ch = new ChannelBD().getSingleChannel(otherchannel);
                        Object[] obj = (Object[]) ch.get(0);
                        String channelName = obj[0].toString();
                        String channelType = obj[1].toString();
                        String userDefine = obj[30].toString();
                        String channelNameString = obj[29].toString();
                        if(userDefine.equals("1")){
                            otherChannelName = new ChannelBD().getUserChannelName(otherchannel);
                        }else if(Integer.parseInt(channelType)>0){
                            otherChannelName = new OrganizationBD().getOrgName(channelType);
                        }
                        String[] otherChannelArray = new String[]{otherchannel,channelName,
                                otherChannelName+"."+channelNameString,channelType,userDefine};
                        list2.add(otherChannelArray);
                    }
                }
                String columnJson2 = InfoUtils.columnListToJson(list2, true);
                request.setAttribute("otherChannel",columnJson2);
            }
            request.setAttribute("oldisoDealCategory", information.getIsoDealCategory()==null?"0":information.getIsoDealCategory());
            if (iso!=null && iso.equals("1")) {
                //return "isoupdateprocess";
            }else{
                return "updateprocessdraft";
            }
        }else{
            //修改时取用户可新增的信息栏目+当前信息栏目,如果用户可新增的信息栏目包含当前信息栏目则不加
            List canIssueChannel = InfoUtils.getCanIssueChannel(userId, orgId, orgIdString,domainId);
            boolean include = false;
            for(int i=0;i<canIssueChannel.size();i++){
                Object[] ch = (Object[]) canIssueChannel.get(i);
                if(information.getInformationChannel() != null){
                    if((information.getInformationChannel().getChannelId().toString()).equals(ch[0].toString())){
                        include = true;
                        break;
                    }
                }
            }
            if(!include){
                if(information.getInformationChannel() != null){
                    Object[] obj = new Object[]{information.getInformationChannel().getChannelId(),
                            information.getInformationChannel().getChannelName(),
                            userChannelName+"."+information.getInformationChannel().getChannelNameString(),
                            information.getInformationChannel().getChannelType(),
                            information.getInformationChannel().getUserDefine(),
                            information.getInformationChannel().getChannelLevel()};
                    canIssueChannel.add(obj);
                }
             }
            //修改时取用户可新增的信息栏目+当前信息栏目,如果用户可新增的信息栏目包含当前信息栏目则不加
            String columnJson = InfoUtils.columnListToJson(canIssueChannel, false);
            request.setAttribute("canIssueChannel", columnJson);
            //同时发布到
            List list1 = InfoUtils.getAllCanIssueWithoutCheck(userId, orgId, domainId);
            String columnJson1 = InfoUtils.columnListToJson(list1, true);
            request.setAttribute("otherChannel", columnJson1);
            request.setAttribute("action", "loaddraft");
            
            return "loaddraft";
        }
        return "loaddraft";
    }
    
    public String updateDraft() throws ParseException{
        HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        /*设置信息的可查看人*/
        String informationReader = request.getParameter("informationReaderId");
        if(informationReader!=null){
            com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationReader);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationReader(userIds);
            information.setInformationReaderOrg(orgIds);
            information.setInformationReaderGroup(groupIds);
        }else{
            information.setInformationReaderName(information.getInformationChannel().getChannelReaderName());
            information.setInformationReader(information.getInformationChannel().getChannelReader());
            information.setInformationReaderOrg(information.getInformationChannel().getChannelReaderOrg());
            information.setInformationReaderGroup(information.getInformationChannel().getChannelReaderGroup());
        }
        /*设置信息的可查看人*/
        
        /*设置信息的可打印人*/
        String informationPrinterId = request.getParameter("informationPrinterId");
        if(informationPrinterId!=null){
            com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationPrinterId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationPrinter(userIds);
            information.setInformationPrinterOrg(orgIds);
            information.setInformationPrinterGroup(groupIds);
        }else{
            information.setInformationPrinterName(information.getInformationChannel().getChannelPrinterName());
            information.setInformationPrinter(information.getInformationChannel().getChannelPrinter());
            information.setInformationPrinterOrg(information.getInformationChannel().getChannelPrinterOrg());
            information.setInformationPrinterGroup(information.getInformationChannel().getChannelPrinterGroup());
        }
        /*设置信息的可打印人*/
        
        /*设置信息的可下载人*/
        String informationDownLoaderId = request.getParameter("informationDownLoaderId");
        if(informationDownLoaderId!=null){
            com.whir.common.util.ConversionString conversionString = new com.whir.common.util.ConversionString(informationDownLoaderId);
            String userIds = conversionString.getUserString();
            String orgIds = conversionString.getOrgString();
            String groupIds = conversionString.getGroupString();
            information.setInformationDownLoader(userIds);
            information.setInformationDownLoaderOrg(orgIds);
            information.setInformationDownLoaderGroup(groupIds);
        }else{
            information.setInformationDownLoaderName(information.getInformationChannel().getChannelDownLoaderName());
            information.setInformationDownLoader(information.getInformationChannel().getChannelDownLoader());
            information.setInformationDownLoaderOrg(information.getInformationChannel().getChannelDownLoaderOrg());
            information.setInformationDownLoaderGroup(information.getInformationChannel().getChannelDownLoaderGroup());
        }
        /*设置信息的可下载人*/
        
        /*提醒方式*/
        String remind_im = request.getParameter("remind_im")!=null?request.getParameter("remind_im"):"";
        String remind_sms = request.getParameter("remind_sms")!=null?request.getParameter("remind_sms"):"";
        String remind_mail = request.getParameter("remind_mail")!=null?request.getParameter("remind_mail"):"";
        String remindType = (remind_im.equals("im")?"im|":"")+(remind_sms.equals("sms")?"sms|":"")+(remind_mail.equals("mail")?"mail":"");
        remindType = remindType.endsWith("|") ? remindType.substring(0,remindType.length()-1) : remindType;
        information.setRemindType(remindType);
        /*提醒方式*/
        
        //保存历史版本
        /*informationId = information.getInformationId().toString();
        informationBD.saveHistory(informationId);*/
        String channelId = "";
        if(request.getParameter("selectChannel") != null && !"".equals(request.getParameter("selectChannel"))){
            channelId = request.getParameter("selectChannel").substring(0, request.getParameter("selectChannel").indexOf(","));
        }
        if(information.getInformationType().equals("0")){
            if(information.getInformationContent().indexOf("\n")>-1){
                information.setInformationContent(information.getInformationContent().replaceAll("\n", "<br>"));
            }
        }
        String displayTitle = request.getParameter("information.displayTitle");
        if(displayTitle!=null && "1".equals(displayTitle)){
            information.setDisplayTitle(0);
        }else{
            information.setDisplayTitle(1);
        }
        String titleColor = request.getParameter("information.titleColor");
        if(titleColor!=null && "1".equals(titleColor)){
            information.setTitleColor(1);
        }else{
            information.setTitleColor(0);
        }
        if(information.getDisplayImage()==null||information.getDisplayImage().equals("")){
            information.setDisplayImage("1");
        }else{
            information.setDisplayImage("0");
        }
        String validBeginTime = request.getParameter("information.validBeginTime");
        String validEndTime = request.getParameter("information.validEndTime");
        if(validBeginTime!=null && !"".equals(validBeginTime)){
            validBeginTime = validBeginTime + " 00:00:00";
            validEndTime = validEndTime + " 23:59:59";
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            validBeginTime = sdf.format(new Date()) + " 00:00:00";
            validEndTime = sdf.format(new Date()) + " 23:59:59";
        }
        //20160516 -by jqq 信息公共标签
        String publicTagsId = request.getParameter("publicTagsId")==null ? "" : request.getParameter("publicTagsId");
        String publicTagsName = request.getParameter("information.informationKey")==null ? "" : request.getParameter("information.informationKey");
        //存储结构为：id1,id2,id3|name1,name2,name3
        information.setInformationKey(publicTagsId + "|" + publicTagsName);
        String[] parameters = {information.getInformationTitle(),
            information.getInformationSubTitle(),
            information.getInformationSummary(),
            information.getInformationKey(),
            information.getInformationContent(),
            userId,
            userName,
            orgName,
            information.getInformationReaderName(),
            information.getInformationReader(),
            information.getInformationReaderOrg(),
            information.getInformationReaderGroup(),
            information.getInformationValidType()+"",
            validBeginTime,//information.getValidBeginTime()!=null ? sdf.format(information.getValidBeginTime()) + " 00:00:00" : sdf.format(new Date()) + " 00:00:00",
            validEndTime,//information.getValidEndTime()!=null ? sdf.format(information.getValidEndTime()) + " 23:59:59" : sdf.format(new Date()) + " 23:59:59",
            information.getInformationHead()+"",
            information.getInformationHeadFile(),
            information.getInformationSeal(),
            information.getInformationMark(),
            information.getInfoRedIssueOrg(),
            information.getInfoRedIssueTime(),
            information.getInformationHeadId()!=null ? information.getInformationHeadId().toString() : "0",
            information.getInformationSealId()!=null ? information.getInformationSealId().toString() : "0",
            information.getTransmitToEzsite()+"",
            information.getForbidCopy()+"",
            information.getOrderCode(),
            information.getDisplayTitle()+"",
            ","+(information.getOtherChannel()!=null?information.getOtherChannel():"0")+",",
            information.getInformationAuthor(),
            request.getParameter("information.informationIssueTime"),
            information.getTitleColor()!=null ? information.getTitleColor()+"" : "0",
            channelId,
            information.getMustRead()==null || "".equals(information.getMustRead()) ? "0" : Integer.toString(information.getMustRead()),
            information.getComeFrom(),
            information.getIsConf()+"0",
            information.getDocumentNo(),
            information.getDocumentEditor(),
            information.getDocumentType(),
            information.getDisplayImage(),
            information.getWordDisplayType(),
            orgId,
            information.getInformationOrISODoc(),
            information.getIsoDocStatus(),
            information.getIsoOldInfoId(),
            information.getIsoSecretStatus(),
            information.getIsoDealCategory(),
            information.getIsoApplyName(),
            information.getIsoApplyId(),
            information.getIsoReceiveName(),
            information.getIsoReceiveId(),
            information.getIsoModifyReason(),
            information.getIsoAmendmentPage(),
            information.getIsoModifyVersion(),
            userName,
            orgName,
            information.getInformationCanRemark().toString(),
            information.getSiteChannelString(),
            information.getInformationMailSendId(),
            information.getInformationMailSendName(),
            information.getInformationMailSendUserId(),
            information.getInformationMailSendOrg(),
            information.getInformationMailSendGroup(),
            information.getRemindType(),
            information.getInformationPrinterName(),
            information.getInformationPrinter(),
            information.getInformationPrinterOrg(),
            information.getInformationPrinterGroup(),
            information.getPrintNum(),
            information.getInformationDownLoaderName(),
            information.getInformationDownLoader(),
            information.getInformationDownLoaderOrg(),
            information.getInformationDownLoaderGroup(),
            information.getDownLoadNum(),
            information.getInformationStatus()+""
        };
        /*信息的附件和图片*/
        String infoPicName = request.getParameter("infoPicName");
        String infoPicSaveName = request.getParameter("infoPicSaveName");
        String infoAppendName = request.getParameter("infoAppendName");
        String infoAppendSaveName = request.getParameter("infoAppendSaveName");
        String[] infoPicNames = null;
        String[] infoPicSaveNames = null;
        String[] infoAppendNames = null;
        String[] infoAppendSaveNames = null;
        if(infoPicName!=null && !infoPicName.equals("")){
            infoPicNames = infoPicName.split("\\|");
            infoPicSaveNames = infoPicSaveName.split("\\|");
        }
        if(infoAppendName!=null && !infoAppendName.equals("")){
            infoAppendNames = infoAppendName.split("\\|");
            infoAppendSaveNames = infoAppendSaveName.split("\\|");
        }
        /*信息的附件和图片*/
        //更新信息
        boolean result = informationBD.update(information.getInformationId().toString(),parameters, null, infoAppendNames,
                infoAppendSaveNames, infoPicNames, infoPicSaveNames);
        //先删除历史公共标签关系记录
        InfoTagBD bd = new InfoTagBD();
        try {
            bd.deletePublicTagRelation(information.getInformationId());
            //保存标签-信息关系(公共标签)
            if(!"".equals(publicTagsId)){
                String[] tagIdArr = publicTagsId.split(",");
                for(int i=0; i<tagIdArr.length; i++){
                    bd.saveTagRelation(Long.valueOf(tagIdArr[i]), information.getInformationId(), "0");
                }
            }
        } catch (Exception tags_e) {
            tags_e.printStackTrace();
        }
        
        if (result) {
            //相关性
            RelationBD relationBD = new RelationBD();
            relationBD.saveRelationList(request,information.getInformationId().toString());
            printResult("success");
        }
        return null;
    }
    /**
     * 查询每个信息的每张图片信息
     * @return
     */
    public String getInfoPicById(){
        InformationAccessoryEJBBean bean = new InformationAccessoryEJBBean();
        String result="";
        String accessoryId =request.getParameter("accessoryId")==null?"-1":request.getParameter("accessoryId");
        try {
            result = bean.getInfoPicById(informationId,accessoryId);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        response.setContentType("text/plain;charSet=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        try {
            PrintWriter pw = response.getWriter();
            pw.print(result);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 页面预览（信息html编辑方式）
     * @return
     * @throws ParseException
     */
    public String infoPreview() throws ParseException{
        information = new InformationPO();
        HttpSession session = request.getSession(true);
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgName = session.getAttribute("orgName").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        
        //预览页面传入参数
        String informationTitle =request.getParameter("informationTitle")==null?"":request.getParameter("informationTitle");
        String informationSubTitle =request.getParameter("informationSubTitle")==null?"":request.getParameter("informationSubTitle");
        String displayTitle = request.getParameter("displayTitle");
        String titleColor = request.getParameter("titleColor");
        String userDefine =request.getParameter("userDefine")==null?"":request.getParameter("userDefine");
        String userChannelName =request.getParameter("userChannelName")==null?"":request.getParameter("userChannelName");
        String informationType =request.getParameter("informationType")==null?"1":request.getParameter("informationType");
        String informationIssueTime =request.getParameter("informationIssueTime")==null?"":request.getParameter("informationIssueTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String content =request.getParameter("content")==null?"":request.getParameter("content");
        /*if(information.getDisplayImage()==null||information.getDisplayImage().equals("")){
            information.setDisplayImage("1");
        }else{
            information.setDisplayImage("0");
        }*/
        //预览页面参数赋值
        information.setDomainId(Long.valueOf(domainId));
        information.setInformationIssuer(userName);
        information.setInformationIssuerId(Long.valueOf(userId));
        information.setInformationIssueOrg(orgName);
        information.setInformationIssueOrgId(orgId);
        information.setIssueOrgIdString(orgIdString);
        information.setInformationTitle(informationTitle);
        information.setInformationSubTitle(informationSubTitle);
        information.setInformationType(informationType);
        if(displayTitle!=null && "0".equals(displayTitle)){
            information.setDisplayTitle(0);
        }else{
            information.setDisplayTitle(1);
        }
        if(titleColor!=null && "1".equals(titleColor)){
            information.setTitleColor(1);
        }else{
            information.setTitleColor(0);
        }
        if(informationIssueTime==null || "".equals(informationIssueTime)){
            information.setInformationIssueTime(new Date());
        }else{
            information.setInformationIssueTime(sdf.parse(informationIssueTime));
        }
        /*request.setAttribute("domainId", domainId);
        request.setAttribute("userName", userName);
        request.setAttribute("orgId", orgId);
        request.setAttribute("orgName", orgName);
        request.setAttribute("informationTitle", informationTitle);
        request.setAttribute("informationSubTitle", informationSubTitle);
        request.setAttribute("informationType", informationType);*/
        request.setAttribute("userDefine", userDefine);
        request.setAttribute("userChannelName", userChannelName);
        request.setAttribute("content", content);
        /*if(displayTitle!=null && "1".equals(displayTitle)){
            request.setAttribute("displayTitle", 0);
        }else{
            request.setAttribute("displayTitle", 1);
        }
        if(informationIssueTime==null || "".equals(informationIssueTime)){
            request.setAttribute("informationIssueTime", new Date());
        }else{
            request.setAttribute("informationIssueTime", sdf.parse(informationIssueTime));
        }*/
        //printResult("success");
        return "preview";
    }

    

}
