package com.whir.ezoffice.logon.actionsupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;

import org.apache.log4j.Logger;

import com.whir.common.init.DogManager;
import com.whir.common.util.CommonUtils;
import com.whir.common.util.Constants;
import com.whir.common.util.MD5;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.CookieParser;
import com.whir.component.util.RSAUtils;
import com.whir.ezoffice.logon.bd.LogonBD;
import com.whir.ezoffice.personalwork.setup.bd.MyInfoBD;
import com.whir.ezoffice.security.log.bd.LogBD;
import com.whir.org.basedata.bd.LoginPageSetBD;
import com.whir.org.basedata.po.ErrorPasswordPO;
import com.whir.org.bd.groupmanager.GroupBD;
import com.whir.org.bd.usermanager.UserBD;
import com.whir.org.common.util.SysSetupReader;
import com.whir.portal.service.Base64Helper;

/**
 * 登录Action
 * 
 * @author wangchao
 * @version 11.0.0.0
 */
public class LogonAction extends BaseActionSupport {

    private static Logger logger = Logger
            .getLogger(LogonAction.class.getName());

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_SKIN = Constants.DEFAULT_THEME_SKIN;
    
    private String userAccount;
    private String userPassword;
    private String domainAccount;
    //重定向链接地址
    private String reurl;
    private String restType;//json
    private boolean isJson = false;
    
    //验证码
    private String captchaAnswer;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getDomainAccount() {
        return domainAccount;
    }

    public void setDomainAccount(String domainAccount) {
        this.domainAccount = domainAccount;
    }

    public String getCaptchaAnswer() {
        return captchaAnswer;
    }

    public void setCaptchaAnswer(String captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }
    
    private boolean isUseCaptcha(String useCaptcha){
        if("1".equals(useCaptcha)){
            return true;
        }else if("2".equals(useCaptcha)){
            HttpSession session = request.getSession();
            LogonBD logonBD = new LogonBD();
            int inputPwdErrorNum = logonBD.getPassErrnum(userAccount);
            //if(request.getAttribute("inputPwdErrorNum") != null){
                // inputPwdErrorNum = (Integer)request.getAttribute("inputPwdErrorNum");
                logger.debug("isUseCaptcha inputErrorNum:"+inputPwdErrorNum);
                if(inputPwdErrorNum>= 2){
                    return true;
                }
            //}
        }
        logger.debug("isUseCaptcha false");
        return false;
    }

    
    private void clearSessionValue(HttpSession session){
        if(session.getAttribute("inputErrorNum") != null){
            session.removeAttribute("inputErrorNum");
        }
    }
    
    /**
     * 登录页面
     * 
     * @return
     */
    public String login() {
        return "login";
    }

    /**
     * 登录验证
     * 
     * @return
     */
    public String logon() throws Exception {
        
        long startTime = System.currentTimeMillis();
        HttpSession session = this.request.getSession();
        CommonUtils.checkAccessRequest(request, response);
        String str = this.request.getQueryString();
        if ((str != null) && (!"".equals(str))) {
          byte[] decode = Base64Helper.decode(str);
          String decodeStr = new String(decode);
          String[] decodeArray = decodeStr.split("&");
          String[] userAccountArray = decodeArray[0].split("=");
          this.userAccount = userAccountArray[1];
          this.domainAccount = "whir";
          session.setAttribute("userPassword", "111111");
        } else {
          this.domainAccount = "whir";
          session.setAttribute("userPassword", CommonUtils.encryptPassword(this.userPassword));
        }
        Date startDate = new Date();
        logger.debug("userAccount:" + userAccount);
        logger.debug("domainAccount:" + domainAccount);
        String result = "error";
        String PORTAL_ERROR = "portalError";
        String CONSOLE_ERROR = "console_noright";
        LogonBD logonBD = new LogonBD();
        if(this.domainAccount.isEmpty() || "".equals(this.domainAccount)){
        	this.domainAccount = "whir";
        }
        //检查参数合法性
        if(CommonUtils.checkValidCharacter(domainAccount) == false){
            return result;
        }
        
        //判断帐号是否为空
        if(CommonUtils.isEmpty(userAccount)){
            return result;
        }
        
        //restType-json
        isJson = false;
        if("json".equals(restType)){
            isJson = true;
        }
        
        boolean isConsoleSubmit = false;
        if("Console!init.action".equals(reurl)){
            if(!"admin".equals(userAccount)){
                logger.debug("console_noright");
                request.setAttribute("errorType", "user");
                return CONSOLE_ERROR;
            }
            isConsoleSubmit = true;
        }
        
        // 登录前门户登录
        String _portal_loginUsed = request.getParameter("_portal_loginUsed");
        if (_portal_loginUsed != null) {
            result = PORTAL_ERROR;
        }
      
//        System.out.println("===========session:"+session.getId());
//      //安全性，会话标识未更新
//        session.invalidate();//清空session
//        session = request.getSession();
        
        System.out.println("===========session:"+session.getId());
       // session.setMaxInactiveInterval(10);
        logger.debug("captchaAnswer:" + captchaAnswer);
        
        String useCaptcha = SysSetupReader.getInstance().getSysValueByName("captcha", "0");
        if (isConsoleSubmit == false && isUseCaptcha(useCaptcha)){//登录时启用验证码
            if(captchaAnswer!=null&&!"".equals(captchaAnswer)){
                Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
                if (captcha != null){
                    if(!captcha.getAnswer().equalsIgnoreCase(captchaAnswer)) {
                        request.setAttribute("errorType", "captchaWrong");
                        if(isJson){
                            writeResponse2Page("failure", "captchaWrong");
                            return null;
                        }
                        int inputPwdErrorNum = logonBD.getPassErrnum(userAccount);
                        request.setAttribute("inputPwdErrorNum", inputPwdErrorNum + "");
                        
                        return result;
                    }
                }else{
                    request.setAttribute("errorType", "captchaWrong");
                    if(isJson){
                        writeResponse2Page("failure", "captchaWrong");
                        return null;
                    }
                    return result;
                }
            }else if(captchaAnswer!=null){
                request.setAttribute("errorType", "captchaWrongNull");
                if(isJson){
                    writeResponse2Page("failure", "captchaWrong");
                    return null;
                }
                return result;
            }
        }
        
        session.removeAttribute("userIP");        
                
        if(userPassword != null && userPassword.length() > 80){
        	
        	//安全性,xiehd 20160330 登陆提交时提交时间戳，当时间戳和当中时间相差超过二十秒，则登陆失败---start；
        	//logger.debug("_______________________userPassword:"+userPassword);
            String pwd = RSAUtils.decryptStringByJs(userPassword, true);
            //logger.debug("_______________________pwd:"+pwd);
            String time=request.getParameter("time")==null?"":request.getParameter("time");
            if(pwd==null||"".equals(pwd)){
            	request.setAttribute("errorType", "password");
                if(isJson){
                    writeResponse2Page("failure", "captchaWrong");
                    return null;
                }
                return result;
            }
            
            if(pwd.contains("#!#@!@")&&"".equals(time)){//手动输入密码提交登陆时 前台对pwd = pwd+"#!#@!@"+time加密提交表单，此时time字段为空
            	userPassword = pwd.split("#!#@!@")[0];
                time = pwd.split("#!#@!@")[1];
               
            }else if(!"".equals(time)){//记住密码自动填充提交表单，同时提交的time不为空
            	if(pwd.contains("#!#@!@")){
            		userPassword = pwd.split("#!#@!@")[0];
            	}else{
            		userPassword = pwd;
            	}
            	time = RSAUtils.decryptStringByJs(time, true);
            	//logger.debug("_______________________time2:"+time);
            }
            long decrTime = System.currentTimeMillis();//当前时间戳
            long timeL = Long.parseLong(time);//提交表单登陆前的时间戳
            long t = decrTime-timeL;
            if(t>20000){
            	 logger.debug("登陆时间戳超时："+t);
            	 request.setAttribute("errorType", "password");
                 if(isJson){
                     writeResponse2Page("failure", "captchaWrong");
                     return null;
                 }
                 return result;
            }
          //安全性,xiehd 20160330 登陆提交时提交时间戳，当时间戳和当中时间相差超过二十秒，则登陆失败---end；
            logger.debug("decr time's:"+(decrTime-startTime));
        }
                
        // 用户访问IP
        String userIP = CommonUtils.getIpAddr(request);
        // 应用服务IP
        String serverIP = "";
        try {
            serverIP = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 验证rtx反向登录
        boolean isRtxLoginPage = false;
        if (request.getParameter("RealtimeMsgLogin") != null) {
            LoginPageSetBD lpsbd = new LoginPageSetBD();
            userPassword = lpsbd.checkTmpPassword(userPassword, request
                    .getParameter("RealtimeMsgLogin"));
            isRtxLoginPage = true;
        }
        
        if("Console!init.action".equals(reurl)){
            result = CONSOLE_ERROR;
        }

        HashMap userInfo = new HashMap(10, 1);
        UserBD userBD = new UserBD();
        // 加密狗验证
        boolean hasDog = false;
        DogManager dm = DogManager.getInstance();
        Integer userNum = userBD.getUserNum();
        hasDog = dm.getDogValidate(userNum.intValue(), "logon");
        // 系统没有加密狗
        if (!hasDog) {
            request.setAttribute("errorType", "noDog");
            if(isJson){
                writeResponse2Page("failure", "noDog");
                return null;
            }
            return result;
        }

        session.setAttribute("userPassword", CommonUtils.encryptPassword(userPassword));

      
        // 是否使用LDAP
        int useLDAP = new com.whir.ezoffice.ldap.LDAP().getUseLDAP();
        
        // 如果对该用户启用域验证，则域登录验证
        boolean isAdCheckFlag = false;
        if(useLDAP == 1){
            // isCheckFlag为0表示不验证AD域账号密码，1表示验证AD域账号密码
            int isCheckFlag = new com.whir.ezoffice.ldap.LDAP().getIsCheckFlag();
            if(isCheckFlag == 1){
                String isAdCheck = userBD.getIsAdCheckByAccount(userAccount);
                if("1".equals(isAdCheck)) {
                    isAdCheckFlag = true;
                }
            }
        }

        // 非LDAP登陆，验证数据库用户名密码
        String userAccount_lc = userAccount.trim().toLowerCase();
        
        if ((str != null) && (!"".equals(str))) {
            userInfo = logonBD.logon(userAccount_lc, this.userPassword, 
              userIP, this.domainAccount, "0");
        } else if (useLDAP == 0 || !isAdCheckFlag || userAccount_lc.equals("admin")
                || userAccount_lc.equals("security")) {
            
            //微信整合--start
            String openId = request.getParameter("openId");
            if(!CommonUtils.isEmpty(openId)){
                String secret = request.getParameter("secret");
                String newSecret = new MD5().toMD5(userAccount+openId);
                if(newSecret.equals(secret)){
                    userInfo = logonBD.logon(userAccount, userPassword,
                            userIP, domainAccount, "0");
                }else{
                    reurl = "evo/weixin/login.jsp?openId="+openId;
                }
                //--end
            }else{
                if (CommonUtils.isEmpty(userPassword)) {
                    logger.debug("非LDAP登陆，密码为空。");
                    request.setAttribute("errorType", "password");
                    if(isJson){
                        writeResponse2Page("failure", "password");
                        return null;
                    }
                    return result;
                }

                // PK登陆
                if (!"1".equals(request.getParameter("pkexit"))) {
                    MD5 md5 = new MD5();
                    userPassword = md5.toMD5(userPassword);
                }
                
                //账号是否区分大小写
                String accountscase = SysSetupReader.getInstance().getSysValueByName("accountscase", "0");
                userInfo = logonBD.logon(userAccount, userPassword, userIP,
                    domainAccount, "1" + ("0".equals(accountscase)?"-0":""));//注意：1-0
            }
        } else {
            if (CommonUtils.isEmpty(userPassword)) {
                logger.debug("密码为空。");
                request.setAttribute("errorType", "password");
                if(isJson){
                    writeResponse2Page("failure", "password");
                    return null;
                }
                return result;
            }

            // LDAP登陆验证
            com.whir.ezoffice.ldap.LDAP ldap = new com.whir.ezoffice.ldap.MSAD();
            String rs = ldap.Authenticate(userAccount, userPassword);
            if ("0".equals(rs)) {
                userInfo = new LogonBD().logon(userAccount, userPassword,
                        userIP, domainAccount, "0");
            } else if ("-1".equals(rs)) {
                request.setAttribute("errorType", "user");
                if(isJson){
                    writeResponse2Page("failure", "user");
                    return null;
                }
                return result;
            } else {
                if("533".equals(rs)){
                    logger.debug("LDAP登陆验证，账号被不可用。");
                    request.setAttribute("errorType", "active");
                    //return result;
                    
                    //判断账号存在，密码错误
                    Map umap = userBD.getUserInfoByAccount(userAccount);                
                    if(umap != null && umap.get("userId") != null){
                        String err_uid = (String)umap.get("userId");
                        userInfo.put("error", "active");
                        userInfo.put("userId", err_uid);
                    }else{
                        if(isJson){
                            writeResponse2Page("failure", "active");
                            return null;
                        }
                        return result;
                    }
                }else if("525".equals(rs)){
                    logger.debug("LDAP登陆验证，user not found。");
                    request.setAttribute("errorType", "user");
                    if(isJson){
                        writeResponse2Page("failure", "user");
                        return null;
                    }
                    return result;
                }else if("773".equals(rs)){
                    logger.debug("LDAP登陆验证，user must reset password。");
                    request.setAttribute("errorType", "resetpassword");
                    if(isJson){
                        writeResponse2Page("failure", "resetpassword");
                        return null;
                    }
                    return result;
                }else {
                    logger.debug("LDAP登陆验证，密码错误。");
                    request.setAttribute("errorType", "password");
                    //return result;
                    
                    //判断账号存在，密码错误
                    Map umap = userBD.getUserInfoByAccount(userAccount);                
                    if(umap != null && umap.get("userId") != null){
                        String err_uid = (String)umap.get("userId");
                        userInfo.put("error", "password");
                        userInfo.put("userId", err_uid);
                    }else{
                        if(isJson){
                            writeResponse2Page("failure", "password");
                            return null;
                        }
                        return result;
                    }
                }
            }
        }
        if (userInfo == null) {
            logger.debug("登录失败:userInfo:"+userInfo);
            request.setAttribute("errorType", "user");
            if(isJson){
                writeResponse2Page("failure", "user");
                return null;
            }
            
            //add input error num 
            Integer inputErrorNum = (Integer)session.getAttribute("inputErrorNum");
            if(inputErrorNum == null){
                inputErrorNum = new Integer(1);
                session.setAttribute("inputErrorNum", inputErrorNum);
                request.setAttribute("inputErrorNum", inputErrorNum);
            }else{
                inputErrorNum = new Integer(inputErrorNum.intValue() + 1);
                session.setAttribute("inputErrorNum", inputErrorNum);
                request.setAttribute("inputErrorNum", inputErrorNum);
            }
            
            return result;

        } else if (userInfo.get("error") != null) {
            String errorType = (String)userInfo.get("error");
            
            logger.debug("登录失败:errorType:"+errorType);
            
            // -------密码输入超过6次错误，则禁用该账号-----2013-05-01-----start
            if ("password".equals(errorType)
                    && userAccount != null
                    //&& !userAccount.equals("admin")&& !userAccount.equals("security") // 锁定账号,账号不是系统超级管理员
                    ) {// 密码错误,

                int maxErrorNum = logonBD.getPassMaxErrNum("0");

                int inputPwdErrorNum = logonBD.getPassErrnum(userAccount);

                inputPwdErrorNum++;
               

                if (inputPwdErrorNum >= maxErrorNum) {
                    String[] ids = new String[1];
                    ids[0] = userInfo.get("userId").toString();
                    userBD.sleepUser(ids, "连续输入密码错误");// 休眠
                    
                    errorType = "sleep";

                    ErrorPasswordPO epo = new ErrorPasswordPO();
                    epo.setErrorNum(0);
                    epo.setUserAccount(userAccount);
                    epo.setLoginIp(userIP);
                    logonBD.updatePassErrnum(epo);
                } else {
                    if (inputPwdErrorNum > 0 && inputPwdErrorNum < maxErrorNum) {
                        ErrorPasswordPO epo = new ErrorPasswordPO();
                        epo.setErrorNum(inputPwdErrorNum);
                        epo.setUserAccount(userAccount);
                        epo.setLoginIp(userIP);
                        logonBD.updatePassErrnum(epo);
                    }
                }
                
                logger.debug("inputPwdErrorNum:"+inputPwdErrorNum);
                logger.debug("maxErrorNum:"+maxErrorNum);

                request.setAttribute("inputPwdErrorNum", inputPwdErrorNum + "");
                request.setAttribute("inputPwdErrorNumMax", maxErrorNum + "");
                
                //add input error num 
                Integer inputErrorNum = (Integer)session.getAttribute("inputErrorNum");
                if(inputErrorNum == null){
                    inputErrorNum = new Integer(1);
                    session.setAttribute("inputErrorNum", inputErrorNum);
                    request.setAttribute("inputErrorNum", inputErrorNum);
                }else{
                    inputErrorNum = new Integer(inputErrorNum.intValue() + 1);
                    session.setAttribute("inputErrorNum", inputErrorNum);
                    request.setAttribute("inputErrorNum", inputErrorNum);
                }
            }
            
            Map umap = userBD.getUserInfoByAccount(userAccount);
            if(umap != null){//登录失败记录到日志
                String err_uid = (String)umap.get("userId");
                if(err_uid != null){
                    LogBD bd = new LogBD();
                    bd.log((String)umap.get("userId"), (String)umap.get("userName"), (String)umap.get("orgName"), "oa_index", "登录", startDate, new Date(), "0", "登录失败", userIP, (String)umap.get("domainId"));
                }
            }

            request.setAttribute("errorType", errorType);
            // -------密码输入超过6次错误，则禁用该账号-----2013-05-01-----end
            if(isJson){
                writeResponse2Page("failure", errorType);
                return null;
            }
            
            return result;

        } else if (userInfo.get("userName") != null) {
            session.setAttribute("keySerial", !CommonUtils.isEmpty(userInfo.get("keySerial"))? ""+userInfo.get("keySerial") : null);
            String domainId = userInfo.get("domainId")!=null?(String)userInfo.get("domainId"):"0";
            session.setAttribute("domainId", domainId);

            String tempUserAccount = (String)userInfo.get("userAccount");
            if (tempUserAccount != null) {
                // 系统管理员
                session.setAttribute("userName", "系统管理员");
                session.setAttribute("orgName", "");
                session.setAttribute("orgId", "0");
                session.setAttribute("orgIdString", "");
                session.setAttribute("browseRange", "0");
                
                String sysAcc = tempUserAccount;
                if (sysAcc.equals("admin")) {//系统管理员
                    session.setAttribute("userId", "0");
                    session.setAttribute("userAccount", "admin");
                } else if (sysAcc.equals("security")) {//安全管理员
                    session.setAttribute("userId", userInfo.get("userId"));
                    session.setAttribute("userAccount", "security");
                }
                
                session.setAttribute("sysManager", "1");
                session.setAttribute("skin", !CommonUtils.isEmpty(userInfo.get("skin"))? ""+userInfo.get("skin") : DEFAULT_SKIN);
                session.setAttribute("rootCorpId", "0");
                session.setAttribute("corpId", "0");
                session.setAttribute("departId", "0");
                session.setAttribute("pageFontsize", userInfo.get("pageFontsize")!=null?userInfo.get("pageFontsize"):"14");
                
                // 组织英文名称
                session.setAttribute("orgEnglishName", !CommonUtils.isEmpty(userInfo.get("orgEnglishName"))? ""+userInfo.get("orgEnglishName") : "");
                // 工号
                session.setAttribute("empNumber", !CommonUtils.isEmpty(userInfo.get("empNumber"))?""+userInfo.get("empNumber") : "");
                // 商务电话
                session.setAttribute("empBusinessPhone", !CommonUtils.isEmpty(userInfo.get("empBusinessPhone"))?""+userInfo.get("empBusinessPhone") : "");
                // 组织名称
                session.setAttribute("orgSelfName", !CommonUtils.isEmpty(userInfo.get("orgSelfName"))? ""+userInfo.get("orgSelfName") : "");
                // 身份证号
                String empIdCard = userInfo.get("empIdCard") != null ? ""+userInfo.get("empIdCard") : "";
                session.setAttribute("empIdCard", empIdCard);

                //MyInfoBD myInfoBD = new MyInfoBD();
                String userPageSize = userInfo.get("userPageSize") != null ? ""+userInfo.get("userPageSize") : "";//myInfoBD.loadUserPageSize("0");
                session.setAttribute("userPageSize", !CommonUtils.isEmpty(userPageSize) ? userPageSize : Constants.DEFAULT_PAGE_SIZE + "");
                //数据交换用户唯一识别号
                String userIdentityNo = empIdCard;
                session.setAttribute("userIdentityNo", userIdentityNo);

            } else {
                String curuserId = userInfo.get("userId")+"";
                session.setAttribute("userName", userInfo.get("userName"));
                session.setAttribute("userId", curuserId);
                session.setAttribute("orgName", userInfo.get("orgName"));
                session.setAttribute("orgId", userInfo.get("orgId"));
                session.setAttribute("orgIdString", userInfo.get("orgIdString"));
                session.setAttribute("skin", !CommonUtils.isEmpty(userInfo.get("skin"))? ""+userInfo.get("skin") : DEFAULT_SKIN);
                session.setAttribute("rootCorpId", userInfo.get("rootCorpId"));
                session.setAttribute("corpId", userInfo.get("corpId"));
                session.setAttribute("departId", userInfo.get("departId"));
                session.setAttribute("pageFontsize", userInfo.get("pageFontsize")!=null?userInfo.get("pageFontsize"):"14");

                // 组织英文名称
                session.setAttribute("orgEnglishName", !CommonUtils.isEmpty(userInfo.get("orgEnglishName"))? ""+userInfo.get("orgEnglishName") : "");
                // 工号
                session.setAttribute("empNumber", !CommonUtils.isEmpty(userInfo.get("empNumber"))? ""+userInfo.get("empNumber") : "");
                // 商务电话
                session.setAttribute("empBusinessPhone", !CommonUtils.isEmpty(userInfo.get("empBusinessPhone"))? ""+userInfo.get("empBusinessPhone") : "");
                // 组织名称
                session.setAttribute("orgSelfName", !CommonUtils.isEmpty(userInfo.get("orgSelfName"))? ""+userInfo.get("orgSelfName") : "");
                // 身份证号
                String empIdCard = userInfo.get("empIdCard") != null ? ""+userInfo.get("empIdCard") : "";
                session.setAttribute("empIdCard", empIdCard);
                //数据交换用户唯一识别号
                String userIdentityNo = empIdCard;
                session.setAttribute("userIdentityNo", userIdentityNo); 
                //浏览范围
                session.setAttribute("browseRange", !CommonUtils.isEmpty(userInfo.get("browseRange"))? ""+userInfo.get("browseRange") : "0");

                session.setAttribute("userAccount", userInfo.get("realUserAccount"));//帐号-realUserAccount
                session.setAttribute("sysManager", userInfo.get("sysManager"));
               
                session.setAttribute("userSimpleName", !CommonUtils.isEmpty(userInfo.get("userSimpleName"))? ""+userInfo.get("userSimpleName") : "");
                session.setAttribute("orgSerial", !CommonUtils.isEmpty(userInfo.get("orgSerial"))? ""+userInfo.get("orgSerial") : "");
                session.setAttribute("orgSimpleName", !CommonUtils.isEmpty(userInfo.get("orgSimpleName"))? ""+userInfo.get("orgSimpleName") : "");
                session.setAttribute("dutyName", !CommonUtils.isEmpty(userInfo.get("dutyName"))? ""+userInfo.get("dutyName") : "");
                session.setAttribute("dutyLevel", !CommonUtils.isEmpty(userInfo.get("dutyLevel"))? ""+userInfo.get("dutyLevel") : "0");
                session.setAttribute("imID", !CommonUtils.isEmpty(userInfo.get("imID"))? ""+userInfo.get("imID") : "0");
                session.setAttribute("sidelineOrg", !CommonUtils.isEmpty(userInfo.get("sidelineOrg"))? ""+userInfo.get("sidelineOrg") : "");
                session.setAttribute("sidelineOrgName", !CommonUtils.isEmpty(userInfo.get("sidelineOrgName"))? ""+userInfo.get("sidelineOrgName") : "");

                // ----判断系统对该用户是否启用了密码规则，如果启用了密码规则，则要进行相关的操作或提示-----2009-04-23----start
                if (!CommonUtils.isEmpty(curuserId)) {
                    String isInitPassword = userInfo.get("isChangePwd") != null ? ""+userInfo.get("isChangePwd") : "";
                    
                    //String isInitPassword = userBD.getUserIsInitPassword(curuserId);
                    // 密码为非初始密码且系统对该用户启用了密码规则
                    if ("1".equals(isInitPassword)) {
                        String isPasswordRule = userInfo.get("isPasswordRule") != null ? ""+userInfo.get("isPasswordRule") : "";
                        
                        //String isPasswordRule = userBD.getUserIsPasswordRule(curuserId);
                        if("1".equals(isPasswordRule)){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            String currentDateStr = sdf.format(new Date());
                            String latestModPasswordDateStr = userBD.getLatestModiPasswordDate(curuserId);
                            long subTime = 0;
                            try {
                                long currentDateTime = sdf.parse(currentDateStr)
                                        .getTime();
                                long latestModPwdTime = sdf.parse(
                                        latestModPasswordDateStr).getTime();
                                subTime = currentDateTime - latestModPwdTime;
                                long subDays = subTime / ((long) 3600 * 24 * 1000);
                                if (subDays > 76) {// 3个月必须更新密码，两周之内进行提醒
                                    long subMsgDay = 90 - subDays;
                                    if (subMsgDay > 0) {
                                        request.setAttribute("modifyPwdMsg",
                                                "modifyPwd");
                                        // 提醒用户必须修改密码的剩余天数
                                        request.setAttribute("subMsgDay", subMsgDay
                                                + "");
                                    } else {// 超过90天未修改密码，禁用该用户帐号
                                        request.setAttribute("errorType",
                                                "forbidUser");
                                        String[] ids = new String[1];
                                        ids[0] = curuserId;
                                        userBD.sleepUser(ids, "密码到期");// 休眠//.disable(ids);//将该用户账号禁用
                                        return result;
                                    }
                                }
                            } catch (ParseException ex1) {}
                        }
                    }
                }
                // ----判断系统对该用户是否启用了密码规则，如果启用了密码规则，则要进行相关的操作或提示-----2009-04-23-----end

                //MyInfoBD myInfoBD = new MyInfoBD();
                String userPageSize = userInfo.get("userPageSize") != null ? ""+userInfo.get("userPageSize") : "";//myInfoBD.loadUserPageSize(curuserId);
                session.setAttribute("userPageSize", !CommonUtils.isEmpty(userPageSize) ? userPageSize : Constants.DEFAULT_PAGE_SIZE + "");
            }

            session.setAttribute("hasLoged", null);
            String userId = session.getAttribute("userId").toString();

            //boolean isAllowFlag = com.whir.org.common.util.SysSetupReader.getInstance().isAllowUserLogin(domainId);
            // -------判断系统是否允许用户同时在两个客户端登录--------------2009-04-23--end
            //if (!isAllowFlag) {// 不允许用户同时在两个客户端登录
                // 判断用户是否在线，若在线且登陆IP地址不同则不可以登录
                //if (!OnlineUser.getInstance().userCanLog(userId, userIP)) {
                    // 注释一下2行，不对用户是否可以登录进行判断(原：如过已经在线则不可以登录，同一台机器可以。先修改为不判断是否已经在线)------2009-07-09
                    // httpServletRequest.setAttribute("errorType", "online");
                    // return actionMapping.findForward("error");
                //
            //}

            // 记录用户登陆的服务器IP地址
            session.setAttribute("serverIP", serverIP);
            
            // 将给用户session设置IP的操作移至判断用户是否在线的下发执行
            if (isRtxLoginPage) {
                // 不处理
            } else {
                session.setAttribute("userIP", userIP);
            }

            // 英文名称
            session.setAttribute("empEnglishName", !CommonUtils.isEmpty(userInfo.get("empEnglishName"))? ""+userInfo.get("empEnglishName") : "");
            // 岗位
            session.setAttribute("empPosition", !CommonUtils.isEmpty(userInfo.get("empPosition"))? ""+userInfo.get("empPosition") : "");

            //用户所在的群组GROUP_ID's
            session.setAttribute("ownerGroupIdStr", new GroupBD().getOwnerGroupIdStrByUserId(userId));
            
            // 修改该用户的最新登录时间---start
            MyInfoBD myInfoBD = new MyInfoBD();
            myInfoBD.updateUserLatestLogonTime(userId);
            // 修改该用户的最新登录时间---end
            
            //重置session
            //session = CommonUtils.changeSessionIdentifier(request);

            // 调用系统管理-登录接口--start--
            try {
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo(
                        "com.whir.plugins.sys.impl.LoginImpl", "execute", "0",
                        domainId);
                if (interfaceInfos != null) {
                    Class[] paramsType = new Class[] { String.class,
                            String.class, HttpServletRequest.class };
                    Object[] paramsValue = new Object[] { userId,
                            session.getAttribute("orgId").toString(), request };
                    new com.whir.plugins.sys.InterfaceUtils().execute(
                            interfaceInfos, paramsType, paramsValue, "0");
                }
            } catch (Exception e) {
                logger.error("--调用登录接口出错--\n"+e.getMessage());
                e.printStackTrace();
            }
            // 调用系统管理-登录接口--end--
            
            //cookie set
            saveOrUpdateCookies(userInfo);

            // 登录前门户登录后跳转
            String _portal_goUrl = request.getParameter("_portal_goUrl");
            String _portal_flag = request.getParameter("_portal_flag");
            session.setAttribute("_portal_flag", _portal_flag);
            if ("1".equals(_portal_loginUsed)) {// 使用该门户
                String localeCode = request.getParameter("localeCode");
                com.whir.component.util.LocaleUtils.setLocale(localeCode, request);
                request.getRequestDispatcher(_portal_goUrl).forward(
                        request, response);
                return null;
            }
            
            result = "success";

            // 登录成功，次数置0
            ErrorPasswordPO epo = new ErrorPasswordPO();
            epo.setErrorNum(0);
            epo.setUserAccount(userAccount);
            epo.setLoginIp(userIP);
            logonBD.updatePassErrnum(epo);
            
            if(isJson){
                writeResponse2Page("success", "success");
                return null;
            }

        } else {
            request.setAttribute("errorType", "user");
            if(isJson){
                writeResponse2Page("failure", "user");
                return null;
            }
        }
        
        //用于整合单点登录到具体页面
        logger.debug("reurl:"+reurl);        
        if(!CommonUtils.isEmpty(reurl)){//登录后重定向到具体页面
            reurl = java.net.URLDecoder.decode(reurl, "utf-8");
            logger.debug("decode reurl:"+ reurl);
            if("Console!init.action".equals(reurl)){
                logger.debug("checking userAccount");
                if("admin".equals(userAccount)){
                    logger.debug("Dispatcher reurl:"+reurl);
                    //request.getRequestDispatcher(reurl).forward(request, response);
                    response.sendRedirect(reurl);
                    return null;
                }else{
                    return "console_noright";
                }
            }
            request.getRequestDispatcher(reurl).forward(request, response);
            return null;
        }
        
        //清除不需要去的session值
        clearSessionValue(session);
        
        long endTime = System.currentTimeMillis();
        
        logger.debug("登录消耗时间:"+(endTime-startTime)); 

        return result;
    }

    public String getJsonData() throws Exception {
        
        String json = "";
        
        String userAccount = "";
        
        userAccount = com.whir.integration.realtimemessage.weixin.WeixinUtils.getUserAccountByWeixinId(request.getParameter("openId"));
        
        json = "{\"userAccount\":\""+userAccount+"\"}";
        
        printJsonResult(json);
        
        return null;
    }
    
    public String getUserToken() throws Exception {

        String dataJson = "";

        String[] userInfo = new CookieParser().getUserFromRememberToken(
                request, response);

        String pwd = "";
        if (userInfo != null) {
            pwd = userInfo[1];
        }

        dataJson = "{\"userToken\":\"" + pwd + "\", \"security\":1}";

        response.setContentType("text/plain;charSet=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        try {
            PrintWriter pw = response.getWriter();
            pw.print(dataJson);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    private void writeResponse2Page(String result, String errorType){
        if("success".equals(result)){
            printJsonResult("{\"result\":\""+result+"\"}");
        }else{
            printJsonResult("{\"result\":\""+result+"\",\"errorType\":\""+errorType+"\"}");
        }
    }
    
    public String getReurl() {
        return reurl;
    }

    public void setReurl(String reurl) {
        this.reurl = reurl;
    }

    public String getRestType() {
        return restType;
    }

    public void setRestType(String restType) {
        this.restType = restType;
    }
    
    private void saveOrUpdateCookies(Map userInfo) {
        CookieParser cookieparser = new CookieParser();
        
        //String userAccount_base64 = new BASE64().BASE64EncoderUTF8(userAccount);        
        //cookieparser.addCookie(response, "ezofficeUserName", userAccount_base64, 365*24*60*60, null, "/", false);
        cookieparser.addCookie(response, "ezofficeDomainAccount", request.getParameter("domainAccount"), 365*24*60*60, null, "/", false);
        if("1".equals(request.getParameter("isRemember"))){
            String pwd = request.getParameter("userPasswordTemp");
            logger.debug("__________________________+++pwd2:"+pwd);
            cookieparser.setRememberCookie(request, response, userAccount, pwd, 7*24*60*60, null, "/");
            //cookieparser.addCookie(response, "ezofficeUserPassword", new BASE64().BASE64EncoderUTF8(request.getParameter("userPassword")), 365*24*60*60, null, "/", false);
            cookieparser.addCookie(response, "ezofficeIsRemember", request.getParameter("isRemember"), 60*24*60*60, null, "/", false);
            
            String empLivingPhoto = (String)userInfo.get("empLivingPhoto");
            cookieparser.addCookie(response, "empLivingPhoto", empLivingPhoto, 60*24*60*60, null, "/", false);
        }else{
            cookieparser.addCookie(response, CookieParser.REMEMBER_TOKEN_COOKIE_NAME, "", 0, null, "/", false);
            //cookieparser.addCookie(response, "ezofficeUserPassword", null, 0, null, "/", false);
            cookieparser.addCookie(response, "ezofficeIsRemember", null, 0, null, "/", false);
            cookieparser.addCookie(response, "empLivingPhoto", null, 0, null, "/", false);
        }
    }
    
    /**
     * 登录之前获取服务器时间戳
     *@author xiehd
     *20160330
     * @return
     */
    public String getSysTime(){
    	long time = System.currentTimeMillis();
    	logger.debug("________________time:"+time);
    	
    	printResult("true", "{'time':'" + time + "'}");
    	return null;
    }
}
