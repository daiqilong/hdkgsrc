package com.whir.org.sys.actionsupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.SysUtils;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.ezoffice.security.log.bd.LogBD;
import com.whir.org.common.util.SysSetupReader;

/**
 * 系统设置Action
 * 
 * @author whir
 * 
 */
public class SysSetupAction extends BaseActionSupport {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(SysSetupAction.class
            .getName());

    // 附件上传 0-FTP 1-HTTP
    private String attach;
    // 手写意见
    private String sign;
    // 短信
    private String message;
    // 图形工作流
    private String potoflow;
    // 在线感知
    private String rtx;
    // WORD编辑
    private String word;
    // evo-WORD编辑 1开启  0关闭
    private String evoword;
    // 与ezSITE结合
    private String ezsite;
    // 电子签章
    private String signature;
    // 邮件群发
    private String emailSend;
    // 内部联系人查看
    private String innerPerson;
    // 附件加密存储 1-加密
    private String isEncrypt;
    // 同一用户同时登录
    private String userLoginType;
    // 信息未查看用户
    private String InfoNotView;
    // 移动办公信息推送
    private String isMobilePush;
    // 附件下载 0-FTP 1-HTTP
    private String downloadType;

    // Html编辑器默认字体
    private String fontType;
    private String wordSize;

    // 邮件数量
    private String mailNum;
    // 内部邮件容量
    private String mailBoxSize;
    // 我的文档容量
    private String netDiskSize;

    // 工作日志维护
    private String worklog;

    // 附件大小限制 0-不限制 1-限制
    private String attachLimit;
    // 附件大小
    private String attachLimitSize;

    // 历史邮件
    private String historyMail;
    private String saveDays;
    private String runHour;

    // 历史日志
    private String historyLog;
    private String logSaveDays;
    private String logRunHour;

    // 工作流程超期判断
    private String wfOverDate;

    // 邮件提醒方式
    private String mailRemindType;
    // SMTP服务器地址
    private String stmpAddr;
    // 电子邮件地址
    private String mailAddr;
    // 帐号
    private String mailAccount;
    // 密码
    private String mailPass;
    
    //邮件提醒发件人：0-系统账号 1-用户账号
    private String sendMailType;

    // 移动OA位置服务 0-不使用 1-使用
    private String isMobilePositionService;
    // 开启时间-时
    private String startTime_HH;
    // 开启时间-分
    private String startTime_mm;
    // 结束时间-时
    private String endTime_HH;
    // 结束时间-分
    private String endTime_mm;
    
    //登录验证码
    private String captcha;
    //账号大小写
    private String accountscase;
    //WORD编辑附件大小限制
    private String wordlimitsize;
    //工作流引擎
    private String workflowType;
    //使用邮件:0-外部邮件 1-内部邮件 2-全部使用
    private String usemail;

    //附件在线预览0-否 1-是
    private String attachPreview;
    
    //界面字体
    private String pageFontSize;
    
    //找回密码
    private String resetPassword;
    //客户端定位
    private String location;
    //微信定位
    private String wxlocation;
  //易播栏目
    private String yibo_flag;
  //信息列表排序
    private String infoorder;
  //论坛列表排序
    private String forumorder;
  //是否使用邮件提醒
    private String mailremind;
  //PDF批注
    private String oa_PDF;
    //20170505 -by jqq 新增evo端word编辑范围
    private String evoWordRangeIds;
    private String evoWordRangeNames;

    /**
     * 初始化系统设置页面
     * 
     * @return
     * @throws Exception
     */
    public String init() throws Exception {
        logger.debug("初始化系统设置页面start");

        HttpSession session = request.getSession(true);
        String userAccount = session != null ? session
                .getAttribute("userAccount")
                + "" : "";

        // 安全管理员
        if ("security".equals(userAccount)) {
            return "security";
        }

        // 检查业务操作权限
        if (checkOperateRights() == false)
            return NO_RIGHTS;

        String domainId = CommonUtils.getSessionDomainId(request) + "";

        SysSetupReader sysRed = SysSetupReader.getInstance();
        String options = sysRed.getSystemOption(domainId);

        long limitSize = 0;

        // 移动oa位置服务相关参数
        String mobileOA_startTime = "";
        String mobileOA_endTime = "";

        String mobileOA_startTimeHH = "00";
        String mobileOA_startTimemm = "00";
        String mobileOA_endTimeHH = "00";
        String mobileOA_endTimemm = "00";

        // 是否使用位置服务
        if (sysRed.isMobilePositionService(domainId)) {
            String[] _mobileOAlist = sysRed.getMobileOA(domainId);
            mobileOA_startTime = _mobileOAlist[0];
            mobileOA_endTime = _mobileOAlist[1];

            SimpleDateFormat formatterHH = new SimpleDateFormat("HH");
            SimpleDateFormat formattermm = new SimpleDateFormat("mm");

            Date d = new Date();
            if (!CommonUtils.isEmpty(mobileOA_startTime)) {
                d.setTime(Long.parseLong(mobileOA_startTime));
                mobileOA_startTimeHH = formatterHH.format(d);
                mobileOA_startTimemm = formattermm.format(d);
            }
            if (!CommonUtils.isEmpty(mobileOA_endTime)) {
                d.setTime(Long.parseLong(mobileOA_endTime));
                mobileOA_endTimeHH = formatterHH.format(d);
                mobileOA_endTimemm = formattermm.format(d);
            }
        }

        String wfOverDate = sysRed.getStatus();// 取得工作流程超期判断

        String workLog = sysRed.getWorkLog(domainId);

        String mailBoxSize = sysRed.getMailBoxSize(domainId);
        String netDiskSize = sysRed.getNetDiskSize(domainId);
        String attachLimitSize = sysRed.getAttachLimit(domainId);
        String mailNum = sysRed.getInnerMailNum(domainId) + "";// 邮件封数，默认为10000

        String htmlFontType = sysRed.getHtmlFontType(domainId);// 返回""表示不设置html编辑器字体
        String htmlWordSize = sysRed.getHtmlWordSize(domainId);// 返回""表示不设置html编辑器字号
        if (CommonUtils.isEmpty(htmlFontType)) {
            htmlFontType = "仿宋_GB2312";
        }
        if (CommonUtils.isEmpty(htmlWordSize)) {
            htmlWordSize = "18px";
        }

        // 数组historyMailInfo[0]表示开关，0表示未启用，1表示启用；historyMailInfo[1]表示保存天数；historyMailInfo[2]表示运行时间
        String[] historyMailInfo = new String[3];
        historyMailInfo = sysRed.getHistoryMailSet();

        if (!CommonUtils.isEmpty(attachLimitSize)) {
            limitSize = Long.parseLong(attachLimitSize.substring(1))
                    / (1024 * 1024);
        }

        if (CommonUtils.isEmpty(workLog)) {
            workLog = "0";
        }

        // 历史日志
        String historyLog = sysRed.getSysValueByName("historyLog", domainId);
        String logSaveDays = sysRed.getSysValueByName("logSaveDays", domainId);
        String logRunHour = sysRed.getSysValueByName("logRunHour", domainId);
        
        String attachPreview = sysRed.getSysValueByName("attachPreview", domainId);
        String pageFontSize = sysRed.getSysValueByName("pageFontSize", domainId);

        // 判断是否使用即时通讯工具
        com.whir.integration.realtimemessage.Realtimemessage util = new com.whir.integration.realtimemessage.Realtimemessage();
        boolean used = util.getUsed();

        String[] mailRemindType = new SysUtils().getMailRemindType(domainId);

        Map sysSetupMap = new HashMap();
        sysSetupMap.put("options", options);
        sysSetupMap.put("attachLimitSize", attachLimitSize);
        sysSetupMap.put("limitSize", limitSize + "");

        sysSetupMap.put("wfOverDate", wfOverDate);
        sysSetupMap.put("workLog", workLog);
        sysSetupMap.put("historyMailInfo", historyMailInfo);
        sysSetupMap.put("historyLog", historyLog);
        sysSetupMap.put("logSaveDays", logSaveDays);
        sysSetupMap.put("logRunHour", logRunHour);

        sysSetupMap.put("htmlFontType", htmlFontType);
        sysSetupMap.put("htmlWordSize", htmlWordSize);
        sysSetupMap.put("mailRemindType", mailRemindType);

        sysSetupMap.put("mobileOA_startTimeHH", mobileOA_startTimeHH);
        sysSetupMap.put("mobileOA_startTimemm", mobileOA_startTimemm);
        sysSetupMap.put("mobileOA_endTimeHH", mobileOA_endTimeHH);
        sysSetupMap.put("mobileOA_endTimemm", mobileOA_endTimemm);

        sysSetupMap.put("mailBoxSize", mailBoxSize + "");
        sysSetupMap.put("mailNum", mailNum + "");
        sysSetupMap.put("netDiskSize", netDiskSize + "");

        sysSetupMap.put("used", Boolean.valueOf(used));
        
        Map setupMap = sysRed.getSysSetupMap(domainId);
        
        sysSetupMap.put("captcha", "" + setupMap.get("captcha"));
        sysSetupMap.put("accountscase", "" + setupMap.get("accountscase"));
        sysSetupMap.put("wordlimitsize", "" + setupMap.get("wordlimitsize"));
        sysSetupMap.put("workflowType", "" + setupMap.get("workflowType"));
        sysSetupMap.put("usemail", "" + setupMap.get("usemail"));
        
        sysSetupMap.put("attachPreview", attachPreview);
        sysSetupMap.put("pageFontSize", pageFontSize);
        sysSetupMap.put("resetPassword", setupMap.get("resetPassword"));
        sysSetupMap.put("location", setupMap.get("location"));
        sysSetupMap.put("wxlocation", setupMap.get("wxlocation"));
        sysSetupMap.put("yibo_flag", setupMap.get("yibo_flag"));
        sysSetupMap.put("oa_infoorder", setupMap.get("oa_infoorder"));
        sysSetupMap.put("oa_forumorder", setupMap.get("oa_forumorder"));
        sysSetupMap.put("mailremind", setupMap.get("oa_mailremind"));
        sysSetupMap.put("oa_PDF", setupMap.get("oa_PDF"));
        sysSetupMap.put("evoWordRangeIds", setupMap.get("evoWordRangeIds"));
        sysSetupMap.put("evoWordRangeNames", setupMap.get("evoWordRangeNames"));
        request.setAttribute("sysSetupMap", sysSetupMap);

        logger.debug("初始化系统设置页面end");

        return "init";
    }

    /**
     * 更新系统设置
     * 
     * @return  
     * @throws Exception
     */
    public String update() throws Exception {
        logger.debug("更新系统设置start");

		if(!"1".equals(word)){
			word = "0";
		}
		if(!"1".equals(evoword)){
			evoword = "0";
		}
		if(!"1".equals(location)){
			location = "0";
		}
		if(!"1".equals(wxlocation)){
			wxlocation = "0";
		}
		if("-1".equals(mailRemindType)){
			mailremind = "0";//邮件提醒方式：“无”
		}else{
			mailremind = "1";//邮件提醒方式：不为“无”
		}
		if(!"1".equals(oa_PDF)){
			oa_PDF = "0";
		}
        HttpSession session = request.getSession(true);
        String userAccount = session != null ? session
                .getAttribute("userAccount")
                + "" : "";

        // 安全管理员
        if ("security".equals(userAccount)) {
            return "security";
        }

        // 检查业务操作权限
        if (checkOperateRights() == false)
            return NO_RIGHTS;

        Date startDate = new Date();

        String domainId = CommonUtils.getSessionDomainId(request) + "";

        StringBuffer optionBuffer = new StringBuffer();
        optionBuffer.append(attach)// 1附件上传
                .append(sign)// 2手写意见
                .append(message)// 3短信
                .append(potoflow)// 4图形工作流
                .append(rtx)// 5在线感知
                .append(word)// 6WORD编辑
                .append(ezsite)// 7与ezSITE结合
                .append(signature)// 8电子签章
                .append(emailSend)// 9邮件群发
                .append(innerPerson)// 10内部联系人查看
                .append(isEncrypt == null ? "0" : "1")// 11是否加密存储
                .append(userLoginType)// 12是否允许同一用户同时登录，0允许，1不允许
                .append(InfoNotView)// 13信息未查看，0不允许，1允许
                .append(isMobilePush)// 14移动办公信息推送
                .append(downloadType)// 15附件下载方式
                .append(isMobilePositionService)// 16移动OA位置服务标志
        		.append(evoword);// 17EVO-WORD编辑
        String options = optionBuffer.toString();

        long limitSize = 0;
        if (!CommonUtils.isEmpty(attachLimitSize)) {
            limitSize = Integer.parseInt(attachLimitSize.trim()) * 1024 * 1024;
        }

        String[] setupValues = new String[23];// setupValues[0]Html编辑器字体,setupValues[1]Html编辑器字号,setupValues[2]邮件数量
        setupValues[0] = fontType != null ? fontType : "";// Html编辑器字体
        setupValues[1] = wordSize != null ? wordSize : "";// Html编辑器字号
        if (!CommonUtils.isEmpty(mailNum)) {
            setupValues[2] = Integer.parseInt(mailNum) + "";
        } else {
            setupValues[2] = "10000";// 邮件数量(默认)
        }

        // 历史日志
        setupValues[3] = historyLog;
        setupValues[4] = logSaveDays;
        setupValues[5] = logRunHour;
        
        //登录验证码/账号大小写/WORD编辑附件大小限制
        setupValues[6] = this.captcha;
        setupValues[7] = this.accountscase;
        setupValues[8] = this.wordlimitsize;
        setupValues[9] = this.workflowType;
        setupValues[10] = this.usemail;
        setupValues[11] = attachPreview;
        setupValues[12] = this.pageFontSize;
        setupValues[13] = this.resetPassword;
        setupValues[14] = this.location;
        setupValues[15] = this.wxlocation;
        setupValues[16] = this.yibo_flag;
        setupValues[17] = this.infoorder;
        setupValues[18] = this.forumorder;
        setupValues[19] = this.mailremind;
        setupValues[20] = this.oa_PDF;
        //20170505 -by jqq 新增evo端word编辑范围
        if("0".equals(evoword)){
            setupValues[21] = "";
            setupValues[22] = "";
        }else{
            setupValues[21] = this.evoWordRangeIds;
            setupValues[22] = this.evoWordRangeNames;
        }

        logger.debug("参数:");
        logger.debug("options:" + options);
        logger.debug("worklog:" + worklog);
        logger.debug("mailBoxSize:" + mailBoxSize);
        logger.debug("netDiskSize:" + netDiskSize);
        logger.debug("attachLimit:" + attachLimit);
        logger.debug("limitSize:" + limitSize);
        logger.debug("mailNum:" + mailNum);
        logger.debug("fontType:" + fontType);
        logger.debug("wordSize:" + wordSize);
        logger.debug("captcha:" + captcha);
        logger.debug("accountscase:" +accountscase);
        logger.debug("wordlimitsize:" + wordlimitsize);
        logger.debug("workflowType:" + workflowType);
        logger.debug("usemail:" + usemail);
        logger.debug("resetPassword:" + resetPassword);
        logger.debug("location:" + location);
        logger.debug("wxlocation:" + wxlocation);
        logger.debug("yibo_flag:" + yibo_flag);
        logger.debug("infoorder:" + infoorder);
        logger.debug("forumorder:" + forumorder);
        logger.debug("mailremind:" + mailremind);
        logger.debug("oa_PDF:" + oa_PDF);
        logger.debug("========evoWordRangeIds:" + evoWordRangeIds);
        logger.debug("========evoWordRangeNames:" + evoWordRangeNames);
        SysSetupReader sysRed = SysSetupReader.getInstance();
        // sysRed.setRequest(request);
        sysRed.updateSystemOption(domainId, options, worklog, mailBoxSize,
                netDiskSize, attachLimit, String.valueOf(limitSize),
                setupValues);

        // 保存历史邮件设置---start
        String[] historyMailValues = new String[3];
        historyMailValues[0] = historyMail != null ? historyMail : "0";
        historyMailValues[1] = saveDays != null ? saveDays : "60";
        historyMailValues[2] = runHour != null ? runHour : "4";
        sysRed.updateHistoryMailSet(historyMailValues);
        // 保存历史邮件设置---end

        sysRed.saveData(wfOverDate);// 保存工作流程超期判断

        // 邮件提醒方式
        if ("0".equals(mailRemindType)) {//0内部邮件
            new SysUtils().saveSysMailRemindType(new String[] { mailRemindType,
                    "", "", "", "", "", "" }, domainId);
        } else if(!"-1".equals(mailRemindType)){//mailRemindType 1外部邮件,-1为不提醒
            new SysUtils().saveSysMailRemindType(new String[] { mailRemindType,
                    stmpAddr, "", mailAddr, mailAccount, mailPass, sendMailType}, domainId);
        }

        if ("1".equals(isMobilePositionService)) {
            if (CommonUtils.isEmpty(startTime_HH))
                startTime_HH = "00";
            if (CommonUtils.isEmpty(startTime_mm))
                startTime_mm = "00";
            if (CommonUtils.isEmpty(endTime_HH))
                endTime_HH = "00";
            if (CommonUtils.isEmpty(endTime_mm))
                endTime_mm = "00";
//            sysRed.updateMobileOASet(startTime_HH + startTime_mm, endTime_HH
//                    + endTime_mm);
        }

        LogBD logBD = new LogBD();
        Date endDate = new Date();
        logBD.log(session.getAttribute("userId").toString(), session
                .getAttribute("userName").toString(), session.getAttribute(
                "orgName").toString(), "system_sys", "系统管理", startDate,
                endDate, "2", "系统设置",
                session.getAttribute("userIP").toString(), domainId);

        logger.debug("更新系统设置end");

        printResult("success");

        // return "update";
        return null;
    }

    /**
     * 判断是否具有业务操作权限
     * 
     * @return true-是 false-否
     * @throws Exception
     */
    public boolean checkOperateRights() throws Exception {
        HttpSession session = request.getSession(true);

        String sysManager = session != null ? session
                .getAttribute("sysManager")
                + "" : "";
        String userAccount = session != null ? session
                .getAttribute("userAccount")
                + "" : "";

        boolean sysRole = false;

        if (sysManager.indexOf("1") >= 0) {
            sysRole = true;
            return true;
        }

        if (!"admin".equals(userAccount) && !sysRole) {
            return false;
        }

        return true;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPotoflow() {
        return potoflow;
    }

    public void setPotoflow(String potoflow) {
        this.potoflow = potoflow;
    }

    public String getRtx() {
        return rtx;
    }

    public void setRtx(String rtx) {
        this.rtx = rtx;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getEzsite() {
        return ezsite;
    }

    public void setEzsite(String ezsite) {
        this.ezsite = ezsite;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEmailSend() {
        return emailSend;
    }

    public void setEmailSend(String emailSend) {
        this.emailSend = emailSend;
    }

    public String getInnerPerson() {
        return innerPerson;
    }

    public void setInnerPerson(String innerPerson) {
        this.innerPerson = innerPerson;
    }

    public String getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(String isEncrypt) {
        this.isEncrypt = isEncrypt;
    }

    public String getUserLoginType() {
        return userLoginType;
    }

    public void setUserLoginType(String userLoginType) {
        this.userLoginType = userLoginType;
    }

    public String getInfoNotView() {
        return InfoNotView;
    }

    public void setInfoNotView(String infoNotView) {
        InfoNotView = infoNotView;
    }

    public String getIsMobilePush() {
        return isMobilePush;
    }

    public void setIsMobilePush(String isMobilePush) {
        this.isMobilePush = isMobilePush;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getFontType() {
        return fontType;
    }

    public void setFontType(String fontType) {
        this.fontType = fontType;
    }

    public String getWordSize() {
        return wordSize;
    }

    public void setWordSize(String wordSize) {
        this.wordSize = wordSize;
    }

    public String getMailNum() {
        return mailNum;
    }

    public void setMailNum(String mailNum) {
        this.mailNum = mailNum;
    }

    public String getMailBoxSize() {
        return mailBoxSize;
    }

    public void setMailBoxSize(String mailBoxSize) {
        this.mailBoxSize = mailBoxSize;
    }

    public String getNetDiskSize() {
        return netDiskSize;
    }

    public void setNetDiskSize(String netDiskSize) {
        this.netDiskSize = netDiskSize;
    }

    public String getWorklog() {
        return worklog;
    }

    public void setWorklog(String worklog) {
        this.worklog = worklog;
    }

    public String getAttachLimit() {
        return attachLimit;
    }

    public void setAttachLimit(String attachLimit) {
        this.attachLimit = attachLimit;
    }

    public String getAttachLimitSize() {
        return attachLimitSize;
    }

    public void setAttachLimitSize(String attachLimitSize) {
        this.attachLimitSize = attachLimitSize;
    }

    public String getHistoryMail() {
        return historyMail;
    }

    public void setHistoryMail(String historyMail) {
        this.historyMail = historyMail;
    }

    public String getSaveDays() {
        return saveDays;
    }

    public void setSaveDays(String saveDays) {
        this.saveDays = saveDays;
    }

    public String getRunHour() {
        return runHour;
    }

    public void setRunHour(String runHour) {
        this.runHour = runHour;
    }

    public String getHistoryLog() {
        return historyLog;
    }

    public void setHistoryLog(String historyLog) {
        this.historyLog = historyLog;
    }

    public String getLogSaveDays() {
        return logSaveDays;
    }

    public void setLogSaveDays(String logSaveDays) {
        this.logSaveDays = logSaveDays;
    }

    public String getLogRunHour() {
        return logRunHour;
    }

    public void setLogRunHour(String logRunHour) {
        this.logRunHour = logRunHour;
    }

    public String getWfOverDate() {
        return wfOverDate;
    }

    public void setWfOverDate(String wfOverDate) {
        this.wfOverDate = wfOverDate;
    }

    public String getMailRemindType() {
        return mailRemindType;
    }

    public void setMailRemindType(String mailRemindType) {
        this.mailRemindType = mailRemindType;
    }

    public String getStmpAddr() {
        return stmpAddr;
    }

    public void setStmpAddr(String stmpAddr) {
        this.stmpAddr = stmpAddr;
    }

    public String getMailAddr() {
        return mailAddr;
    }

    public void setMailAddr(String mailAddr) {
        this.mailAddr = mailAddr;
    }

    public String getMailAccount() {
        return mailAccount;
    }

    public void setMailAccount(String mailAccount) {
        this.mailAccount = mailAccount;
    }

    public String getMailPass() {
        return mailPass;
    }

    public void setMailPass(String mailPass) {
        this.mailPass = mailPass;
    }

    public String getIsMobilePositionService() {
        return isMobilePositionService;
    }

    public void setIsMobilePositionService(String isMobilePositionService) {
        this.isMobilePositionService = isMobilePositionService;
    }

    public String getStartTime_HH() {
        return startTime_HH;
    }

    public void setStartTime_HH(String startTimeHH) {
        startTime_HH = startTimeHH;
    }

    public String getStartTime_mm() {
        return startTime_mm;
    }

    public void setStartTime_mm(String startTimeMm) {
        startTime_mm = startTimeMm;
    }

    public String getEndTime_HH() {
        return endTime_HH;
    }

    public void setEndTime_HH(String endTimeHH) {
        endTime_HH = endTimeHH;
    }

    public String getEndTime_mm() {
        return endTime_mm;
    }

    public void setEndTime_mm(String endTimeMm) {
        endTime_mm = endTimeMm;
    }

    public String getSendMailType() {
        return sendMailType;
    }

    public void setSendMailType(String sendMailType) {
        this.sendMailType = sendMailType;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getAccountscase() {
        return accountscase;
    }

    public void setAccountscase(String accountscase) {
        this.accountscase = accountscase;
    }

    public String getWordlimitsize() {
        return wordlimitsize;
    }

    public void setWordlimitsize(String wordlimitsize) {
        this.wordlimitsize = wordlimitsize;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public String getUsemail() {
        return usemail;
    }

    public void setUsemail(String usemail) {
        this.usemail = usemail;
    }

    public String getAttachPreview() {
        return attachPreview;
    }

    public void setAttachPreview(String attachPreview) {
        this.attachPreview = attachPreview;
    }

	public String getPageFontSize() {
		return pageFontSize;
	}

	public void setPageFontSize(String pageFontSize) {
		this.pageFontSize = pageFontSize;
	}

	public String getResetPassword() {
		return resetPassword;
	}

	public void setResetPassword(String resetPassword) {
		this.resetPassword = resetPassword;
	}

	public String getEvoword() {
		return evoword;
	}

	public void setEvoword(String evoword) {
		this.evoword = evoword;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getWxlocation() {
		return wxlocation;
	}

	public void setWxlocation(String wxlocation) {
		this.wxlocation = wxlocation;
	}

	public String getYibo_flag() {
		return yibo_flag;
	}

	public void setYibo_flag(String yiboFlag) {
		yibo_flag = yiboFlag;
	}

	public String getInfoorder() {
		return infoorder;
	}

	public void setInfoorder(String infoorder) {
		this.infoorder = infoorder;
	}

	public String getForumorder() {
		return forumorder;
	}

	public void setForumorder(String forumorder) {
		this.forumorder = forumorder;
	}

	public String getMailremind() {
		return mailremind;
	}

	public void setMailremind(String mailremind) {
		this.mailremind = mailremind;
	}

	public String getOa_PDF() {
		return oa_PDF;
	}

	public void setOa_PDF(String oaPDF) {
		oa_PDF = oaPDF;
	}
    public String getEvoWordRangeIds() {
        return evoWordRangeIds;
    }

    public void setEvoWordRangeIds(String evoWordRangeIds) {
        this.evoWordRangeIds = evoWordRangeIds;
    }

    public String getEvoWordRangeNames() {
        return evoWordRangeNames;
    }

    public void setEvoWordRangeNames(String evoWordRangeNames) {
        this.evoWordRangeNames = evoWordRangeNames;
    }

	
}
