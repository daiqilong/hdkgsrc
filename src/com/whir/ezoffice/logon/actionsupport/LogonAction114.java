package com.whir.ezoffice.logon.actionsupport;

import com.whir.common.init.DogManager;
import com.whir.common.util.CommonUtils;
import com.whir.common.util.Constants;
import com.whir.common.util.MD5;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.CookieParser;
import com.whir.component.util.LocaleUtils;
import com.whir.component.util.RSAUtils;
import com.whir.ezoffice.ldap.LDAP;
import com.whir.ezoffice.ldap.MSAD;
import com.whir.ezoffice.logon.bd.LogonBD;
import com.whir.ezoffice.personalwork.setup.bd.MyInfoBD;
import com.whir.ezoffice.security.log.bd.LogBD;
import com.whir.integration.realtimemessage.weixin.WeixinUtils;
import com.whir.org.basedata.bd.LoginPageSetBD;
import com.whir.org.basedata.po.ErrorPasswordPO;
import com.whir.org.bd.groupmanager.GroupBD;
import com.whir.org.bd.usermanager.UserBD;
import com.whir.org.common.util.SysSetupReader;
import com.whir.org.sys.bd.SysInterfaceBD;
import com.whir.plugins.sys.InterfaceUtils;
import com.whir.portal.service.Base64Helper;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import nl.captcha.Captcha;
import org.apache.log4j.Logger;

public class LogonAction114 extends BaseActionSupport
{
  private static Logger logger = Logger.getLogger(LogonAction114.class.getName());
  private static final long serialVersionUID = 1L;
  private static final String DEFAULT_SKIN = Constants.DEFAULT_THEME_SKIN;
  private String userAccount;
  private String userPassword;
  private String domainAccount;
  private String reurl;
  private String restType;
  private boolean isJson = false;
  private String captchaAnswer;

  public String getUserAccount()
  {
    return this.userAccount;
  }

  public void setUserAccount(String userAccount) {
    this.userAccount = userAccount;
  }

  public String getUserPassword() {
    return this.userPassword;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getDomainAccount() {
    return this.domainAccount;
  }

  public void setDomainAccount(String domainAccount) {
    this.domainAccount = domainAccount;
  }

  public String getCaptchaAnswer() {
    return this.captchaAnswer;
  }

  public void setCaptchaAnswer(String captchaAnswer) {
    this.captchaAnswer = captchaAnswer;
  }

  private boolean isUseCaptcha(String useCaptcha) {
    if ("1".equals(useCaptcha))
      return true;
    if ("2".equals(useCaptcha)) {
      HttpSession session = this.request.getSession();
      if (session.getAttribute("inputErrorNum") != null) {
        Integer inputErrorNum = (Integer)session.getAttribute("inputErrorNum");
        logger.debug("isUseCaptcha inputErrorNum:" + inputErrorNum);
        if (inputErrorNum.intValue() >= 2) {
          return true;
        }
      }
    }
    logger.debug("isUseCaptcha false");
    return false;
  }

  private void clearSessionValue(HttpSession session)
  {
    if (session.getAttribute("inputErrorNum") != null)
      session.removeAttribute("inputErrorNum");
  }

  public String login()
  {
    return "login";
  }

  public String logon()
    throws Exception
  {
    long startTime = System.currentTimeMillis();
    HttpSession session = this.request.getSession();

    CommonUtils.checkAccessRequest(this.request, this.response);
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
    logger.debug("userAccount:" + this.userAccount);
    logger.debug("domainAccount:" + this.domainAccount);

    String result = "error";
    String PORTAL_ERROR = "portalError";
    String CONSOLE_ERROR = "console_noright";

    if (!CommonUtils.checkValidCharacter(this.domainAccount)) {
      return result;
    }

    if (CommonUtils.isEmpty(this.userAccount)) {
      return result;
    }

    this.isJson = false;
    if ("json".equals(this.restType)) {
      this.isJson = true;
    }

    boolean isConsoleSubmit = false;
    if ("Console!init.action".equals(this.reurl)) {
      if (!"admin".equals(this.userAccount)) {
        logger.debug("console_noright");
        this.request.setAttribute("errorType", "user");
        return CONSOLE_ERROR;
      }
      isConsoleSubmit = true;
    }

    String _portal_loginUsed = this.request.getParameter("_portal_loginUsed");
    if (_portal_loginUsed != null) {
      result = PORTAL_ERROR;
    }

    logger.debug("captchaAnswer:" + this.captchaAnswer);

    String useCaptcha = SysSetupReader.getInstance().getSysValueByName("captcha", "0");
    if ((!isConsoleSubmit) && (isUseCaptcha(useCaptcha))) {
      if ((this.captchaAnswer != null) && (!"".equals(this.captchaAnswer))) {
        Captcha captcha = (Captcha)session.getAttribute("simpleCaptcha");
        if (captcha != null) {
          if (!captcha.getAnswer().equalsIgnoreCase(this.captchaAnswer)) {
            this.request.setAttribute("errorType", "captchaWrong");
            if (this.isJson) {
              writeResponse2Page("failure", "captchaWrong");
              return null;
            }
            return result;
          }
        } else {
          this.request.setAttribute("errorType", "captchaWrong");
          if (this.isJson) {
            writeResponse2Page("failure", "captchaWrong");
            return null;
          }
          return result;
        }
      } else {
        this.request.setAttribute("errorType", "captchaWrongNull");
        if (this.isJson) {
          writeResponse2Page("failure", "captchaWrong");
          return null;
        }
        return result;
      }
    }

    session.removeAttribute("userIP");

    if ((this.userPassword != null) && (this.userPassword.length() > 80)) {
      this.userPassword = RSAUtils.decryptStringByJs(this.userPassword, true);
      long decrTime = System.currentTimeMillis();
      logger.debug("decr time's:" + (decrTime - startTime));
    }

    String userIP = CommonUtils.getIpAddr(this.request);

    String serverIP = "";
    try {
      serverIP = InetAddress.getLocalHost().getHostAddress();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    boolean isRtxLoginPage = false;
    if (this.request.getParameter("RealtimeMsgLogin") != null) {
      LoginPageSetBD lpsbd = new LoginPageSetBD();
      this.userPassword = lpsbd.checkTmpPassword(this.userPassword, this.request
        .getParameter("RealtimeMsgLogin"));
      isRtxLoginPage = true;
    }

    if ("Console!init.action".equals(this.reurl)) {
      result = CONSOLE_ERROR;
    }

    HashMap userInfo = new HashMap(10, 1.0F);
    UserBD userBD = new UserBD();

    boolean hasDog = false;
    DogManager dm = DogManager.getInstance();
    Integer userNum = userBD.getUserNum();
    hasDog = dm.getDogValidate(userNum.intValue(), "logon");

    if (!hasDog) {
      this.request.setAttribute("errorType", "noDog");
      if (this.isJson) {
        writeResponse2Page("failure", "noDog");
        return null;
      }
      return result;
    }

    LogonBD logonBD = new LogonBD();

    int useLDAP = new LDAP().getUseLDAP();

    boolean isAdCheckFlag = false;
    if (useLDAP == 1)
    {
      int isCheckFlag = new LDAP().getIsCheckFlag();
      if (isCheckFlag == 1) {
        String isAdCheck = userBD.getIsAdCheckByAccount(this.userAccount);
        if ("1".equals(isAdCheck)) {
          isAdCheckFlag = true;
        }
      }

    }

    String userAccount_lc = this.userAccount.trim().toLowerCase();
    if ((str != null) && (!"".equals(str))) {
      userInfo = logonBD.logon(userAccount_lc, this.userPassword, 
        userIP, this.domainAccount, "0");
    }
    else if ((useLDAP == 0) || (!isAdCheckFlag) || (userAccount_lc.equals("admin")) || 
      (userAccount_lc.equals("security")))
    {
      String openId = this.request.getParameter("openId");
      if (!CommonUtils.isEmpty(openId)) {
        String secret = this.request.getParameter("secret");
        String newSecret = new MD5().toMD5(this.userAccount + openId);
        if (newSecret.equals(secret))
          userInfo = logonBD.logon(this.userAccount, this.userPassword, 
            userIP, this.domainAccount, "0");
        else
          this.reurl = ("evo/weixin/login.jsp?openId=" + openId);
      }
      else
      {
        if (CommonUtils.isEmpty(this.userPassword)) {
          logger.debug("非LDAP登陆，密码为空。");
          this.request.setAttribute("errorType", "password");
          if (this.isJson) {
            writeResponse2Page("failure", "password");
            return null;
          }
          return result;
        }

        if (!"1".equals(this.request.getParameter("pkexit"))) {
          MD5 md5 = new MD5();
          this.userPassword = md5.toMD5(this.userPassword);
        }

        String accountscase = SysSetupReader.getInstance().getSysValueByName("accountscase", "0");

        userInfo = logonBD.logon(this.userAccount, this.userPassword, userIP, 
          this.domainAccount, "1" + ("0".equals(accountscase) ? "-0" : ""));
      }
    } else {
      if (CommonUtils.isEmpty(this.userPassword)) {
        logger.debug("密码为空。");
        this.request.setAttribute("errorType", "password");
        if (this.isJson) {
          writeResponse2Page("failure", "password");
          return null;
        }
        return result;
      }

      LDAP ldap = new MSAD();
      String rs = ldap.Authenticate(this.userAccount, this.userPassword);
      if ("0".equals(rs)) {
        userInfo = new LogonBD().logon(this.userAccount, this.userPassword, 
          userIP, this.domainAccount, "0"); } else {
        if ("-1".equals(rs)) {
          this.request.setAttribute("errorType", "user");
          if (this.isJson) {
            writeResponse2Page("failure", "user");
            return null;
          }
          return result;
        }
        if ("533".equals(rs)) {
          logger.debug("LDAP登陆验证，账号被不可用。");
          this.request.setAttribute("errorType", "active");

          Map umap = userBD.getUserInfoByAccount(this.userAccount);
          if ((umap != null) && (umap.get("userId") != null)) {
            String err_uid = (String)umap.get("userId");
            userInfo.put("error", "active");
            userInfo.put("userId", err_uid);
          } else {
            if (this.isJson) {
              writeResponse2Page("failure", "active");
              return null;
            }
            return result;
          }
        } else { if ("525".equals(rs)) {
            logger.debug("LDAP登陆验证，user not found。");
            this.request.setAttribute("errorType", "user");
            if (this.isJson) {
              writeResponse2Page("failure", "user");
              return null;
            }
            return result;
          }if ("773".equals(rs)) {
            logger.debug("LDAP登陆验证，user must reset password。");
            this.request.setAttribute("errorType", "resetpassword");
            if (this.isJson) {
              writeResponse2Page("failure", "resetpassword");
              return null;
            }
            return result;
          }
          logger.debug("LDAP登陆验证，密码错误。");
          this.request.setAttribute("errorType", "password");

          Map umap = userBD.getUserInfoByAccount(this.userAccount);
          if ((umap != null) && (umap.get("userId") != null)) {
            String err_uid = (String)umap.get("userId");
            userInfo.put("error", "password");
            userInfo.put("userId", err_uid);
          } else {
            if (this.isJson) {
              writeResponse2Page("failure", "password");
              return null;
            }
            return result;
          }
        }
      }
    }

    if (userInfo == null) {
      logger.debug("登录失败:userInfo:" + userInfo);
      this.request.setAttribute("errorType", "user");
      if (this.isJson) {
        writeResponse2Page("failure", "user");
        return null;
      }

      Integer inputErrorNum = (Integer)session.getAttribute("inputErrorNum");
      if (inputErrorNum == null) {
        inputErrorNum = new Integer(1);
        session.setAttribute("inputErrorNum", inputErrorNum);
        this.request.setAttribute("inputErrorNum", inputErrorNum);
      } else {
        inputErrorNum = new Integer(inputErrorNum.intValue() + 1);
        session.setAttribute("inputErrorNum", inputErrorNum);
        this.request.setAttribute("inputErrorNum", inputErrorNum);
      }

      return result;
    }
    if (userInfo.get("error") != null) {
      String errorType = (String)userInfo.get("error");

      logger.debug("登录失败:errorType:" + errorType);

      if (("password".equals(errorType)) && 
        (this.userAccount != null) && (!this.userAccount.equals("admin")) && 
        (!this.userAccount.equals("security")))
      {
        int maxErrorNum = logonBD.getPassMaxErrNum("0");

        int inputPwdErrorNum = logonBD.getPassErrnum(this.userAccount);

        inputPwdErrorNum++;

        if (inputPwdErrorNum >= maxErrorNum) {
          String[] ids = new String[1];
          ids[0] = userInfo.get("userId").toString();
          userBD.sleepUser(ids, "连续输入密码错误");

          errorType = "sleep";

          ErrorPasswordPO epo = new ErrorPasswordPO();
          epo.setErrorNum(0);
          epo.setUserAccount(this.userAccount);
          epo.setLoginIp(userIP);
          logonBD.updatePassErrnum(epo);
        }
        else if ((inputPwdErrorNum > 0) && (inputPwdErrorNum < maxErrorNum)) {
          ErrorPasswordPO epo = new ErrorPasswordPO();
          epo.setErrorNum(inputPwdErrorNum);
          epo.setUserAccount(this.userAccount);
          epo.setLoginIp(userIP);
          logonBD.updatePassErrnum(epo);
        }

        logger.debug("inputPwdErrorNum:" + inputPwdErrorNum);
        logger.debug("maxErrorNum:" + maxErrorNum);

        this.request.setAttribute("inputPwdErrorNum", Integer.valueOf(inputPwdErrorNum)+"");
        this.request.setAttribute("inputPwdErrorNumMax", Integer.valueOf(maxErrorNum)+"");

        Integer inputErrorNum = (Integer)session.getAttribute("inputErrorNum");
        if (inputErrorNum == null) {
          inputErrorNum = new Integer(1);
          session.setAttribute("inputErrorNum", inputErrorNum+"");
          this.request.setAttribute("inputErrorNum", inputErrorNum+"");
        } else {
          inputErrorNum = new Integer(inputErrorNum.intValue() + 1);
          session.setAttribute("inputErrorNum", inputErrorNum+"");
          this.request.setAttribute("inputErrorNum", inputErrorNum+"");
        }
      }

      Map umap = userBD.getUserInfoByAccount(this.userAccount);
      if (umap != null) {
        String err_uid = (String)umap.get("userId");
        if (err_uid != null) {
          LogBD bd = new LogBD();
          bd.log((String)umap.get("userId"), (String)umap.get("userName"), (String)umap.get("orgName"), "oa_index", "登录", startDate, new Date(), "0", "登录失败", userIP, (String)umap.get("domainId"));
        }
      }

      this.request.setAttribute("errorType", errorType);

      if (this.isJson) {
        writeResponse2Page("failure", errorType);
        return null;
      }

      return result;
    }

    if (userInfo.get("userName") != null) {
      session.setAttribute("keySerial", !CommonUtils.isEmpty(userInfo.get("keySerial")) ? userInfo.get("keySerial") : null);
      String domainId = userInfo.get("domainId") != null ? (String)userInfo.get("domainId") : "0";
      session.setAttribute("domainId", domainId);

      String tempUserAccount = (String)userInfo.get("userAccount");
      if (tempUserAccount != null)
      {
        session.setAttribute("userName", "系统管理员");
        session.setAttribute("orgName", "");
        session.setAttribute("orgId", "0");
        session.setAttribute("orgIdString", "");
        session.setAttribute("browseRange", "0");

        String sysAcc = tempUserAccount;
        if (sysAcc.equals("admin")) {
          session.setAttribute("userId", "0");
          session.setAttribute("userAccount", "admin");
        } else if (sysAcc.equals("security")) {
          session.setAttribute("userId", userInfo.get("userId"));
          session.setAttribute("userAccount", "security");
        }

        session.setAttribute("sysManager", "1");
        session.setAttribute("skin", !CommonUtils.isEmpty(userInfo.get("skin")) ? userInfo.get("skin") : DEFAULT_SKIN);
        session.setAttribute("rootCorpId", "0");
        session.setAttribute("corpId", "0");
        session.setAttribute("departId", "0");
        session.setAttribute("pageFontsize", userInfo.get("pageFontsize") != null ? userInfo.get("pageFontsize") : "14");

        session.setAttribute("orgEnglishName", !CommonUtils.isEmpty(userInfo.get("orgEnglishName")) ? userInfo.get("orgEnglishName") : "");

        session.setAttribute("empNumber", !CommonUtils.isEmpty(userInfo.get("empNumber")) ? userInfo.get("empNumber") : "");

        session.setAttribute("empBusinessPhone", !CommonUtils.isEmpty(userInfo.get("empBusinessPhone")) ? userInfo.get("empBusinessPhone") : "");

        session.setAttribute("orgSelfName", !CommonUtils.isEmpty(userInfo.get("orgSelfName")) ? userInfo.get("orgSelfName") : "");

        String empIdCard = userInfo.get("empIdCard") != null ? userInfo.get("empIdCard").toString() : "";
        session.setAttribute("empIdCard", empIdCard);

        String userPageSize = userInfo.get("userPageSize") != null ? userInfo.get("userPageSize").toString() : "";
        session.setAttribute("userPageSize", !CommonUtils.isEmpty(userPageSize) ? userPageSize : "15");

        String userIdentityNo = empIdCard;
        session.setAttribute("userIdentityNo", userIdentityNo);
      }
      else {
        String curuserId = userInfo.get("userId").toString();
        session.setAttribute("userName", userInfo.get("userName"));
        session.setAttribute("userId", curuserId);
        session.setAttribute("orgName", userInfo.get("orgName"));
        session.setAttribute("orgId", userInfo.get("orgId"));
        session.setAttribute("orgIdString", userInfo.get("orgIdString"));
        session.setAttribute("skin", !CommonUtils.isEmpty(userInfo.get("skin")) ? userInfo.get("skin") : DEFAULT_SKIN);
        session.setAttribute("rootCorpId", userInfo.get("rootCorpId"));
        session.setAttribute("corpId", userInfo.get("corpId"));
        session.setAttribute("departId", userInfo.get("departId"));
        session.setAttribute("pageFontsize", userInfo.get("pageFontsize") != null ? userInfo.get("pageFontsize") : "14");

        session.setAttribute("orgEnglishName", !CommonUtils.isEmpty(userInfo.get("orgEnglishName")) ? userInfo.get("orgEnglishName") : "");

        session.setAttribute("empNumber", !CommonUtils.isEmpty(userInfo.get("empNumber")) ? userInfo.get("empNumber") : "");

        session.setAttribute("empBusinessPhone", !CommonUtils.isEmpty(userInfo.get("empBusinessPhone")) ? userInfo.get("empBusinessPhone") : "");

        session.setAttribute("orgSelfName", !CommonUtils.isEmpty(userInfo.get("orgSelfName")) ? userInfo.get("orgSelfName") : "");

        String empIdCard = userInfo.get("empIdCard") != null ? userInfo.get("empIdCard").toString() : "";
        session.setAttribute("empIdCard", empIdCard);

        String userIdentityNo = empIdCard;
        session.setAttribute("userIdentityNo", userIdentityNo);

        session.setAttribute("browseRange", !CommonUtils.isEmpty(userInfo.get("browseRange")) ? userInfo.get("browseRange") : "0");

        session.setAttribute("userAccount", userInfo.get("realUserAccount"));
        session.setAttribute("sysManager", userInfo.get("sysManager"));

        session.setAttribute("userSimpleName", !CommonUtils.isEmpty(userInfo.get("userSimpleName")) ? userInfo.get("userSimpleName") : "");
        session.setAttribute("orgSerial", !CommonUtils.isEmpty(userInfo.get("orgSerial")) ? userInfo.get("orgSerial") : "");
        session.setAttribute("orgSimpleName", !CommonUtils.isEmpty(userInfo.get("orgSimpleName")) ? userInfo.get("orgSimpleName") : "");
        session.setAttribute("dutyName", !CommonUtils.isEmpty(userInfo.get("dutyName")) ? userInfo.get("dutyName") : "");
        session.setAttribute("dutyLevel", !CommonUtils.isEmpty(userInfo.get("dutyLevel")) ? userInfo.get("dutyLevel") : "0");
        session.setAttribute("imID", !CommonUtils.isEmpty(userInfo.get("imID")) ? userInfo.get("imID") : "0");
        session.setAttribute("sidelineOrg", !CommonUtils.isEmpty(userInfo.get("sidelineOrg")) ? userInfo.get("sidelineOrg") : "");
        session.setAttribute("sidelineOrgName", !CommonUtils.isEmpty(userInfo.get("sidelineOrgName")) ? userInfo.get("sidelineOrgName") : "");

        if (!CommonUtils.isEmpty(curuserId)) {
          String isInitPassword = userInfo.get("isChangePwd") != null ? userInfo.get("isChangePwd").toString() : "";

          if ("1".equals(isInitPassword)) {
            String isPasswordRule = userInfo.get("isPasswordRule") != null ? userInfo.get("isPasswordRule").toString() : "";

            if ("1".equals(isPasswordRule)) {
              SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
              String currentDateStr = sdf.format(new Date());
              String latestModPasswordDateStr = userBD.getLatestModiPasswordDate(curuserId);
              long subTime = 0L;
              try {
                long currentDateTime = sdf.parse(currentDateStr)
                  .getTime();
                long latestModPwdTime = sdf.parse(
                  latestModPasswordDateStr).getTime();
                subTime = currentDateTime - latestModPwdTime;
                long subDays = subTime / 86400000L;
                if (subDays > 76L) {
                  long subMsgDay = 90L - subDays;
                  if (subMsgDay > 0L) {
                    this.request.setAttribute("modifyPwdMsg", 
                      "modifyPwd");

                    this.request.setAttribute("subMsgDay", Long.valueOf(subMsgDay));
                  }
                  else {
                    this.request.setAttribute("errorType", 
                      "forbidUser");
                    String[] ids = new String[1];
                    ids[0] = curuserId;
                    userBD.sleepUser(ids, "密码到期");
                    return result;
                  }
                }
              }
              catch (ParseException localParseException)
              {
              }
            }
          }
        }
        String userPageSize = userInfo.get("userPageSize") != null ? userInfo.get("userPageSize").toString() : "";
        session.setAttribute("userPageSize", !CommonUtils.isEmpty(userPageSize) ? userPageSize : "15");
      }

      session.setAttribute("hasLoged", null);
      String userId = session.getAttribute("userId").toString();

      session.setAttribute("serverIP", serverIP);

      if (!isRtxLoginPage)
      {
        session.setAttribute("userIP", userIP);
      }

      session.setAttribute("empEnglishName", !CommonUtils.isEmpty(userInfo.get("empEnglishName")) ? userInfo.get("empEnglishName") : "");

      session.setAttribute("empPosition", !CommonUtils.isEmpty(userInfo.get("empPosition")) ? userInfo.get("empPosition") : "");

      session.setAttribute("ownerGroupIdStr", new GroupBD().getOwnerGroupIdStrByUserId(userId));

      MyInfoBD myInfoBD = new MyInfoBD();
      myInfoBD.updateUserLatestLogonTime(userId);
      try
      {
        SysInterfaceBD sibd = new SysInterfaceBD();
        String[] interfaceInfos = sibd.getInterfaceInfo(
          "com.whir.plugins.sys.impl.LoginImpl", "execute", "0", 
          domainId);
        if (interfaceInfos != null) {
          Class[] paramsType = { String.class, 
            String.class, HttpServletRequest.class };
          Object[] paramsValue = { userId, 
            session.getAttribute("orgId").toString(), this.request };
          new InterfaceUtils().execute(
            interfaceInfos, paramsType, paramsValue, "0");
        }
      } catch (Exception e) {
        logger.error("--调用登录接口出错--\n" + e.getMessage());
        e.printStackTrace();
      }

      saveOrUpdateCookies(userInfo);

      String _portal_goUrl = this.request.getParameter("_portal_goUrl");
      String _portal_flag = this.request.getParameter("_portal_flag");
      session.setAttribute("_portal_flag", _portal_flag);
      if ("1".equals(_portal_loginUsed)) {
        String localeCode = this.request.getParameter("localeCode");
        LocaleUtils.setLocale(localeCode, this.request);
        this.request.getRequestDispatcher(_portal_goUrl).forward(
          this.request, this.response);
        return null;
      }

      result = "success";

      ErrorPasswordPO epo = new ErrorPasswordPO();
      epo.setErrorNum(0);
      epo.setUserAccount(this.userAccount);
      epo.setLoginIp(userIP);
      logonBD.updatePassErrnum(epo);

      if (this.isJson) {
        writeResponse2Page("success", "success");
        return null;
      }
    }
    else {
      this.request.setAttribute("errorType", "user");
      if (this.isJson) {
        writeResponse2Page("failure", "user");
        return null;
      }

    }

    logger.debug("reurl:" + this.reurl);
    if (!CommonUtils.isEmpty(this.reurl)) {
      this.reurl = URLDecoder.decode(this.reurl, "utf-8");
      logger.debug("decode reurl:" + this.reurl);
      if ("Console!init.action".equals(this.reurl)) {
        logger.debug("checking userAccount");
        if ("admin".equals(this.userAccount)) {
          logger.debug("Dispatcher reurl:" + this.reurl);

          this.response.sendRedirect(this.reurl);
          return null;
        }
        return "console_noright";
      }

      this.request.getRequestDispatcher(this.reurl).forward(this.request, this.response);
      return null;
    }

    clearSessionValue(session);

    long endTime = System.currentTimeMillis();

    logger.debug("登录消耗时间:" + (endTime - startTime));

    return result;
  }

  public String getJsonData() throws Exception
  {
    String json = "";

    String userAccount = "";

    userAccount = WeixinUtils.getUserAccountByWeixinId(this.request.getParameter("openId"));

    json = "{\"userAccount\":\"" + userAccount + "\"}";

    printJsonResult(json);

    return null;
  }

  public String getUserToken() throws Exception
  {
    String dataJson = "";

    String[] userInfo = new CookieParser().getUserFromRememberToken(
      this.request, this.response);

    String pwd = "";
    if (userInfo != null) {
      pwd = userInfo[1];
    }

    dataJson = "{\"userToken\":\"" + pwd + "\", \"security\":1}";

    this.response.setContentType("text/plain;charSet=UTF-8");
    this.response.setCharacterEncoding("UTF-8");
    this.response.setHeader("Pragma", "no-cache");
    this.response.addHeader("Cache-Control", "no-cache");
    this.response.addHeader("Cache-Control", "no-store");
    this.response.setDateHeader("Expires", 0L);
    try {
      PrintWriter pw = this.response.getWriter();
      pw.print(dataJson);
      pw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void writeResponse2Page(String result, String errorType) {
    if ("success".equals(result))
      printJsonResult("{\"result\":\"" + result + "\"}");
    else
      printJsonResult("{\"result\":\"" + result + "\",\"errorType\":\"" + errorType + "\"}");
  }

  public String getReurl()
  {
    return this.reurl;
  }

  public void setReurl(String reurl) {
    this.reurl = reurl;
  }

  public String getRestType() {
    return this.restType;
  }

  public void setRestType(String restType) {
    this.restType = restType;
  }

  private void saveOrUpdateCookies(Map userInfo) {
    CookieParser cookieparser = new CookieParser();

    cookieparser.addCookie(this.response, "ezofficeDomainAccount", this.request.getParameter("domainAccount"), 31536000, null, "/", false);
    if ("1".equals(this.request.getParameter("isRemember"))) {
      String pwd = this.request.getParameter("userPassword");
      cookieparser.setRememberCookie(this.request, this.response, this.userAccount, pwd, 5184000, null, "/");

      cookieparser.addCookie(this.response, "ezofficeIsRemember", this.request.getParameter("isRemember"), 5184000, null, "/", false);

      String empLivingPhoto = (String)userInfo.get("empLivingPhoto");
      cookieparser.addCookie(this.response, "empLivingPhoto", empLivingPhoto, 5184000, null, "/", false);
    } else {
      cookieparser.addCookie(this.response, "rwhirtoken", "", 0, null, "/", false);

      cookieparser.addCookie(this.response, "ezofficeIsRemember", null, 0, null, "/", false);
      cookieparser.addCookie(this.response, "empLivingPhoto", null, 0, null, "/", false);
    }
  }
}