package com.whir.integration.realtimemessage;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import org.apache.log4j.Logger;
import rtx.HttpRTX;
import rtx.RTXSvrApi;
import rtx.TransRTX;
import com.whir.component.cache.UserInfoCache;
import com.whir.component.config.ConfigXMLReader;
import com.whir.integration.realtimemessage.bigant.SendMessageWeb;
import com.whir.integration.realtimemessage.elink.ELinkApi;
import com.whir.integration.realtimemessage.lava.*;
import com.whir.integration.realtimemessage.ucStar.*;
import com.whir.integration.realtimemessage.cocall.*;
import com.whir.org.basedata.bd.LoginPageSetBD;

public class Realtimemessage {
	
 	private static String use;
  	private static String type;
   	private static String zoneID;
 	private static String serverDN;
   	private static String serverLocalDN;
 	private static String port;
 	private static String area;
 	//bigant
	private static String webservices;
 	private static String senderId;
 	private static String password;
 	
   	private String pushType = null;
	private static String usepush;
  	private static String pushurl;

 	private static String WebserviceServer;
 	private static Logger logger = Logger.getLogger(Realtimemessage.class.getName());
        
    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public Realtimemessage() {
    	if(this.use==null){
        	init();
     	}
 	}

	private void init(){
		this.use = com.whir.component.config.ConfigReader.REALTIMEMESSAGE_USE;
		this.type= com.whir.component.config.ConfigReader.REALTIMEMESSAGE_TYPE;
		this.usepush = com.whir.component.config.ConfigReader.FINGER_USEPUSHURL;
		this.pushurl = com.whir.component.config.ConfigReader.FINGER_PUSHURL;
		if("1".equals(use)){
			//系统中使用了即时通讯工具
			if("gk".equals(this.type)){
				this.serverDN= com.whir.component.config.ConfigReader.GKSERVER_SERVER;
				this.serverLocalDN= com.whir.component.config.ConfigReader.GKSERVER_LOCALSERVER;
				this.port=com.whir.component.config.ConfigReader.GKSERVER_PORT;
				this.zoneID=com.whir.component.config.ConfigReader.GKSERVER_ZONEID;
				GKUtilClass util= new GKUtilClass();
				util.setPort(Integer.parseInt(this.port));
				util.setServer(this.serverLocalDN);
			}else if("rtx".equals(type)||"rtx_http".equals(type)){
				this.serverDN=com.whir.component.config.ConfigReader.RTXSERVER_DOMAIN;
				this.serverLocalDN=com.whir.component.config.ConfigReader.RTXSERVER_LOCALDOMAIN;
			}else if("ucStar".equals(type)){
				this.WebserviceServer=com.whir.component.config.ConfigReader.UCSTARSERVER_WEBSERVICESERVER;
				this.serverDN=com.whir.component.config.ConfigReader.UCSTARSERVER_SERVER;
				this.port=com.whir.component.config.ConfigReader.UCSTARSERVER_PORT;
            }else if("eLink".equals(type)){
                this.serverDN=com.whir.component.config.ConfigReader.ELINK_SERVER;
                this.port=com.whir.component.config.ConfigReader.ELINK_PORT;
                this.area=com.whir.component.config.ConfigReader.ELINK_AREA;
            }else if("bigant".equals(type)){
                this.webservices=com.whir.component.config.ConfigReader.BIGANT_WEBSERVICES;
                this.serverDN=com.whir.component.config.ConfigReader.BIGANT_SERVER;
                this.port=com.whir.component.config.ConfigReader.BIGANT_PORT;
                this.senderId=com.whir.component.config.ConfigReader.BIGANT_SENDERID;
                this.password=com.whir.component.config.ConfigReader.BIGANT_PASSWORD;
            }
		}else{
			this.zoneID="5000";
			this.serverDN="127.0.0.1";
			this.serverLocalDN="127.0.0.1";
			this.port="8900";
		}
	}

	/**
	 * 获取即时通讯配置
	 *
	 * @return String
	 * 0 没有配置即时通讯工具
	 *  rtx 配置rtx
     *  gk 配置gke
     *  bigant 配置 bigant
     */
	public String getRealTimeMessageType(){
        if(!"1".equals(use)){
            return "0";
        }else{
            return type;
        }
	}

	/**
	 * 同步所有组织用户
	 *
	 * @return boolean
	 */
	public boolean sync() throws Exception {
        if ("1".equals(use)) {
            if ("gk".equals(type)) {
                GKSync sync = new GKSync();
                return sync.Sync();
            } else if ("rtx".equals(type)) {
                TransRTX rtx = new TransRTX();
                return rtx.SyncDepsAndUsers();
            } else if ("rtx_http".equals(type)) {
            	HttpRTX httprtx = new HttpRTX();
                return httprtx.SyncDepsAndUsers();
            } else if ("ucStar".equals(type)) {
                ucStarSync ucSync = new ucStarSync();
                return ucSync.Sync(WebserviceServer);
            } else if ("eLink".equals(type)) {
                ELinkApi eLinkApi = new ELinkApi();
                boolean ret = false;
                try {
                    ret = eLinkApi.Sync(this.serverDN, this.port, this.area);
                } catch (MalformedURLException ex) {
                    System.out.println("error in RealTimeUtil sync()" + ex);
                }
                return ret;
            }else if("bigant".equals(type)){
            	SendMessageWeb antSvr = new SendMessageWeb();
                antSvr.Sync(webservices,serverDN);
            }else if("cocall".equals(type)){
            	CocallSendMessage cocall = new CocallSendMessage();
            	cocall.Sync();
            }
        }
        return false;
	}

	/**
	 * 同步所有组织用户
	 * @param syncUsers String  \uFFFDC要同步的用户id 组(格式：<用户id>,<用户id >)
	 * @param syncOrgs   String \uFFFDC要同步的组织id 组(格式：<组织id>,<组织id >)
	 *
	 * @return boolean
	 */
	public boolean syncPart(String syncUsers, String syncOrgs) {
        if ("1".equals(use)) {
            if ("gk".equals(type)) {
                GKSync sync = new GKSync();
                if (!"".equals(syncOrgs)) {
                    sync.SyncDept(syncOrgs);
                }
                if (!"".equals(syncUsers)) {
                    sync.SyncUser(syncUsers);
                }
                return true;
            } else if ("rtx".equals(type)) {
                TransRTX rtx = new TransRTX();
                if (!"".equals(syncOrgs)) {
                    rtx.SyncDeps(syncOrgs);
                }
                if (!"".equals(syncUsers)) {
                    rtx.SyncUsers(syncUsers);
                }
                return true;
            } else if ("rtx_http".equals(type)) {
            	HttpRTX httprtx = new HttpRTX();
            	if (!"".equals(syncOrgs)) {
            		httprtx.SyncDeps(syncOrgs);
                }
                if (!"".equals(syncUsers)) {
                	httprtx.SyncUsers(syncUsers);
                }
            } else if ("ucStar".equals(type)) {
                ucStarSync ucSync = new ucStarSync();
                //组织也要同步
                if (!"".equals(syncOrgs)) {
                    ucSync.SyncDept(WebserviceServer, syncOrgs);
                }
                if (!"".equals(syncUsers)) {
                    ucSync.SyncUser(WebserviceServer, syncUsers);
                }
                return true;
            } else if ("eLink".equals(type)) {
                ELinkApi eLinkApi = new ELinkApi();
                boolean ret = false;
                try {
                    //组织也要同步
                    if (!"".equals(syncOrgs)) {
                        ret = eLinkApi.SyncDept(this.serverDN, this.port, this.area, syncOrgs);
                    }
                    if (!"".equals(syncUsers)) {
                        ret = eLinkApi.SyncUser(this.serverDN, this.port, this.area, syncUsers);
                    }
                } catch (Exception ex) {
                    System.out.println("error in RealTimeUtil syncPart()" + ex);
                }
                return ret;
            }else if("bigant".equals(type)){
            	SendMessageWeb antSvr = new SendMessageWeb();
                try {
                    antSvr.syncPart(webservices,serverDN, syncUsers, syncOrgs);
                } catch (Exception ex1) {
                    System.out.println("error in RealTimeUtil syncPart()" + ex1);
                }
            }else if("cocall".equals(type)){
            	CocallSendMessage cocall = new CocallSendMessage();
            	boolean ret = false;
                try {
                    //组织也要同步
                    if (!"".equals(syncOrgs)) {
                        ret = cocall.SyncDept(syncOrgs);
                    }
                    if (!"".equals(syncUsers)) {
                        ret = cocall.SyncUser(syncUsers);
                    }
                } catch (Exception ex) {
                    System.out.println("error in RealTimeUtil syncPart()" + ex);
                }
                return ret;
            }
        }
        return false;
	}

	/**
	 * 修改即时通讯系统密码
	 * @param account String \uFFFDC用户帐号
	 * @param password   String \uFFFDC密码
	 * @param md5Pwd    String - Md5密码
	 *
	 * @return boolean
	 */
	public boolean modifyPassword(String account, String password, String md5Pwd) {
        if ("1".equals(use)) {
            if ("gk".equals(type)) {
                GKUserManager gkUser = new GKUserManager();
                gkUser.modPwd(account, password, md5Pwd);
                gkUser.close();
            } else if ("eLink".equals(type)) {
                ELinkApi eLinkApi = new ELinkApi();
                try {
                    eLinkApi.modPwd(this.serverDN, this.port, this.area, account, password);
                } catch (MalformedURLException ex) {
                }
            }
        }
        return false;
	}

    /**
     * 修改用户密码 ucStar
     * @param account String 用户帐号
     * @param password String 用户新密码
     * String oldpassword 用户旧密码
     * @return boolean
     */
	public boolean modifyUcStarPassword(String account, String password, String oldpassword) {
        boolean flag = false;
        if ("1".equals(use)) {
            if ("ucStar".equals(type)) {
                ucStarSync ucSync = new ucStarSync();
                flag = ucSync.modPwd(WebserviceServer, account, password, oldpassword);
            }
        }
        return flag;
	}

    /**
     * 修改用户
     * @param userId String  同步的用户id
     *
     * @return boolean
     */
	public boolean updateUser(String userId) {
        if ("1".equals(use)) {
            if ("gk".equals(type)) {
            	
            } else if ("rtx".equals(type)) {
                TransRTX rtx = new TransRTX();
                String[] _user = new String[1];
                _user[0]=userId;
                //rtx.deleteUser(_user);
                //rtx.SyncUsers(userId);
                rtx.updateUser(userId);
            } else if ("rtx_http".equals(type)) {
            	HttpRTX rtx = new HttpRTX();
                String[] _user = new String[1];
                _user[0]=userId;
                //rtx.deleteUser(_user);
                //rtx.SyncUsers(userId);
                rtx.updateUser(userId);
            } else if ("ucStar".equals(type)) {
                ucStarSync ucSync = new ucStarSync();
                //组织也要同步
                if (!"".equals(userId)) {
                    ucSync.updateUser(WebserviceServer, userId);
                }
                return true;
            } else if ("eLink".equals(type)) {
                ELinkApi eLinkApi = new ELinkApi();
                boolean ret = false;
                try {
                    ret = eLinkApi.updateUser(this.serverDN,
                                              this.port,
                                              this.area,
                                              userId);
                } catch (MalformedURLException ex) {
                }
                return ret;
            }else if("cocall".equals(type)){
            	CocallSendMessage cocall = new CocallSendMessage();
            	boolean ret = false;
                try {
                    ret = cocall.updateUser(userId);
                } catch (Exception ex) {
                }
                return ret;
            }
        }
        return false;
	}
	
    /**
     * 删除用户
     * @param userId String  同步的用户id
     *
     * @return boolean
     */
	public boolean deleteUser(String[] userId){
        String userIds = "";
        if(userId!= null){
        	for(int i=0;i<userId.length;i++){
        		userIds+=userId[i]+",";
        	}
        }
        if(!"".equals(userIds)){
            userIds = userIds.substring(0,userIds.length() - 1);
        }
        if ("1".equals(use)) {
            if ("gk".equals(type)) {
            	
            } else if ("rtx".equals(type)) {
                TransRTX rtx = new TransRTX();
                rtx.deleteUser(userId);
            } else if ("rtx_http".equals(type)) {
            	HttpRTX rtx = new HttpRTX();
                rtx.deleteUser(userId);
            } else if ("ucStar".equals(type)) {
                ucStarSync ucSync = new ucStarSync();
                //组织也要同步
                if (!"".equals(userId)) {
                    ucSync.deleteUser(WebserviceServer, userId);
                }
                return true;
            } else if ("eLink".equals(type)) {
                ELinkApi eLinkApi = new ELinkApi();
                boolean ret = false;
                try {
                    ret = eLinkApi.deleteUser(this.serverDN,
                                              this.port,
                                              this.area,
                                              userIds);
                } catch (MalformedURLException ex) {
                }
                return ret;
            }else if("cocall".equals(type)){
            	CocallSendMessage cocall = new CocallSendMessage();
            	boolean ret = false;
                try {
                    ret = cocall.deleteUser(userId);
                } catch (Exception ex) {
                }
                return ret;
            }
        }
        return false;
	}

    /**
     * 修改部门
     * @param orgId String  修改部门id
     *
     * @return boolean
     */
	public boolean updateDept(String orgId) {
        if ("1".equals(use)) {
            if ("gk".equals(type)) {
            	
            } else if ("rtx".equals(type)) {
                TransRTX rtx = new TransRTX();
                rtx.updateDept(orgId);
            } else if ("rtx_http".equals(type)) {
            	HttpRTX rtx = new HttpRTX();
                rtx.updateDept(orgId);
            } else if ("ucStar".equals(type)) {
                ucStarSync ucSync = new ucStarSync();
                //组织也要同步
                if (!"".equals(orgId)) {
                    ucSync.updateDept(WebserviceServer,orgId);
                }
                return true;
            } else if ("eLink".equals(type)) {
                ELinkApi eLinkApi = new ELinkApi();
                boolean ret = false;
                try {
                    ret = eLinkApi.updateDept(this.serverDN,
                                              this.port,
                                              this.area,
                                              orgId);
                } catch (MalformedURLException ex) {
                }
                return ret;
            }else if("cocall".equals(type)){
            	CocallSendMessage cocall = new CocallSendMessage();
            	boolean ret = false;
                try {
                    ret = cocall.updateDept(orgId);
                } catch (Exception ex) {
                }
                return ret;
            }
        }
        return false;
	}
	
    /**
     * 删除部门
     * @param orgIds String[]  部门id
     *
     * @return boolean
     */
	public boolean deleteDept(String[] orgIds){
        if ("1".equals(use)) {
            if ("gk".equals(type)) {

            } else if ("rtx".equals(type)) {
                TransRTX rtx = new TransRTX();
                rtx.deleteDept(orgIds);
            } else if ("rtx_http".equals(type)) {
            	HttpRTX rtx = new HttpRTX();
                rtx.deleteDept(orgIds);
            } else if ("ucStar".equals(type)) {
                ucStarSync ucSync = new ucStarSync();
                //组织也要同步
                if (!"".equals(orgIds)) {
                    ucSync.deleteDept(WebserviceServer, orgIds);
                }
                return true;
            } else if ("eLink".equals(type)) {
                ELinkApi eLinkApi = new ELinkApi();
                boolean ret = false;
                try {
                    ret = eLinkApi.deleteDept(this.serverDN,
                                              this.port,
                                              this.area,
                                              orgIds);
                } catch (MalformedURLException ex) {
                }
                return ret;
            }else if("cocall".equals(type)){
            	CocallSendMessage cocall = new CocallSendMessage();
            	boolean ret = false;
                try {
                    ret = cocall.deleteDept(orgIds);
                } catch (Exception ex) {
                }
                return ret;
            }
        }
        return false;
	}

	public boolean sendFingerMessage(String receiverAccounts,String title,String text) {
		logger.debug("usepush--sendFingerMessage--->"+usepush);
		logger.debug("receiverAccounts--sendFingerMessage--->"+receiverAccounts);
		logger.debug("title--sendFingerMessage--->"+title);
		logger.debug("text--sendFingerMessage--->"+text);
		if("1".equals(usepush)){
			String _receiverAccounts=UserInfoCache.getInstance().getCanSendFingerByUserAccount(receiverAccounts, this.getPushType());
			if(_receiverAccounts!=null&&!_receiverAccounts.equals("")&&!_receiverAccounts.equals("null")){
				com.whir.integration.realtimemessage.finger.ThreadFinger th = new com.whir.integration.realtimemessage.finger.ThreadFinger(title,text,_receiverAccounts, this.getPushType());
            	th.start();
			}
//                   FingerSendMessage fingerSendMessage = new FingerSendMessage();
//                   fingerSendMessage.push(title,text,receiverAccounts, this.getPushType());
//                   res = true;
		}
    	return true;
	}
	
	/**
	 * 发送即时通讯提醒
	 * @param receiverAccounts String-用户帐号组(格式：帐号,帐号)
	 * @param title    String--标题 (rtx,ELink 使用 ，gke,bigant，ucStar 不使用)
	 * @param text     String--正文 (不能为空)
	 * @return boolean
	 */
	public boolean sendNotify(String receiverAccounts,String title, String text) {
		return this.sendNotify(receiverAccounts, title, text, "");
	}
	
	public boolean sendNotify(String receiverAccounts,String title, String text, String remindTitle) {
    	logger.debug("-------------------sendNotify------------------");
        boolean res = false;
        try {
        	logger.debug("use:"+use);
        	logger.debug("type:"+type);
            if ("1".equals(use)) {
                if ("gk".equals(type)) {
                    GKSendMessage gkSM = new GKSendMessage();
                    gkSM.sendNotify(receiverAccounts, title, text);
                    gkSM.close();
                } else if ("rtx".equals(type)) {
                    RTXSvrApi rtxSvr = new RTXSvrApi();
                    rtxSvr.sendNotify(receiverAccounts,title,text,"0","0");
                } else if ("rtx_http".equals(type)) {
                	HttpRTX rtx = new HttpRTX();
                	rtx.sendNotify(receiverAccounts,title,text);
                } else if ("bigant".equals(type)) {
                	SendMessageWeb antSvr = new SendMessageWeb();
                	receiverAccounts = receiverAccounts.replaceAll(",",";");
                	antSvr.sendNotify(webservices,
                			serverDN,
                			port,
                			senderId,
                			password,
                			receiverAccounts,
                			title,
                			text);
                } else if ("ucStar".equals(type)) {
                    ucStarSendMessage uc = new ucStarSendMessage();
                    uc.sendNotify(WebserviceServer, receiverAccounts,title,text);
                } else if ("eLink".equals(type)) {
                    ELinkApi eLinkApi = new ELinkApi();
                    eLinkApi.sendNotify(this.serverDN, this.port, this.area, receiverAccounts, title, text);
                }else if("cocall".equals(type)){
                	CocallSendMessage cocall = new CocallSendMessage();
                	cocall.sendNotify(receiverAccounts, title, text);
                }
            }
            
            String fingerTitle =title;
            if(remindTitle !=null && !remindTitle.equals("null") && !remindTitle.equals("")){
            	fingerTitle =remindTitle;
            }
            logger.debug("fingerTitle----->"+fingerTitle);
            sendFingerMessage(receiverAccounts,fingerTitle,text);
            res = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
	}
        
    /**
     * 带链接的消息提醒
     * @param receiverAccounts rtx帐号
     * @param title  标题
     * @param msg  内容
     * @param urlName  连接标题
     * @param modelName 模块
     * @param userAccounts 登录oa帐号
     * @param userPassword 登录oa密码
     * @param toLink  跳转地址
     * @return
     */
	public boolean sendNotify(String receiverAccounts, 
        		                  String title,
			                      String msg, 
			                      String urlName,
			                      String modelName,
			                      String userAccounts,
			                      String userPassword,
			                      String toLink) {
	    	logger.debug("-------------------sendNotify------------------");
			boolean res = false;
			try {
				logger.debug("use:"+use);
            	logger.debug("type:"+type);
				if ("1".equals(use)) {
					ConfigXMLReader reader = new ConfigXMLReader();
	    			String oaIP = reader.getAttribute("OAIP", "Server");
	    			String domainAccount = com.whir.org.common.util.SysSetupReader.getInstance().isMultiDomain();
	    			LoginPageSetBD loginPageSetBD = new LoginPageSetBD();
	    			
					String url= "";
					
					if ("gk".equals(type)) {
	
					} else if ("rtx".equals(type)) {
						toLink = toLink.replaceAll("\\&", "%26");
						url= oaIP
						+ "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount="
						+ domainAccount + "&userName=" + URLEncoder.encode(userAccounts,"utf-8")
						+ "&RealtimeMsgLogin=" + URLEncoder.encode(modelName,"utf-8") + "&userPassword="
						+ loginPageSetBD.addTmpPasswordPO(modelName,userPassword)
						+ "&toLink=" + toLink;

						RTXSvrApi rtxSvr = new RTXSvrApi();
						String text = msg + ",[" + urlName + "|" + url + "]";
						rtxSvr.sendNotify(receiverAccounts, title, text, "0", "0");
					} else if ("rtx_http".equals(type)) {
						
						url = oaIP
						+ "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount="
						+ domainAccount + "&userName=" + URLEncoder.encode(userAccounts,"utf-8")
						+ "&RealtimeMsgLogin=" +URLEncoder.encode(modelName,"utf-8") + "&userPassword="
						+ loginPageSetBD.addTmpPasswordPO(modelName,userPassword);
//						System.out.println("url:"+url);
//						System.out.println("msg:"+msg);
//						System.out.println("urlName:"+urlName);
						HttpRTX rtx = new HttpRTX();
						String text = URLEncoder.encode(msg,"GBK") + ",[" + URLEncoder.encode(urlName,"GBK") + "|" + url;
						
						rtx.sendNotifyUrl(receiverAccounts, title, text,toLink);
					} else if ("bigant".equals(type)) {
						SendMessageWeb antSvr = new SendMessageWeb();
                    	receiverAccounts = receiverAccounts.replaceAll(",",";");
                    	antSvr.sendNotify(webservices,
                    			serverDN,
                    			port,
                    			senderId,
                    			password,
                    			receiverAccounts,
                    			title,
                    			msg+" "+urlName);
					} else if ("ucStar".equals(type)) {
						
						toLink = toLink.replaceAll("\\&", "%26");
						url= oaIP
						+ "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount="
						+ domainAccount + "&userName=" + userAccounts
						+ "&RealtimeMsgLogin=" + modelName + "&userPassword="
						+ loginPageSetBD.addTmpPasswordPO(modelName,userPassword)
						+ "&toLink=" + toLink;
						
						ucStarSendMessage uc = new ucStarSendMessage();
//						String text = msg
//								+ "<a style=\"cursor:hand\" href=\"#\" onclick=\"window.open('"
//								+ url + "')\" >" + urlName + "</a> ";
						uc.sendNotifyUrl(WebserviceServer, 
								      receiverAccounts, 
								      title+"\n"+msg+":"+urlName, 
								      url);
	
					} else if ("eLink".equals(type)) {
						
						toLink = toLink.replaceAll("\\&", "%26");
						
						url= oaIP
						+ "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount="
						+ domainAccount + "&userName=" + userAccounts
						+ "&RealtimeMsgLogin=" + modelName + "&userPassword="
						+ loginPageSetBD.addTmpPasswordPO(modelName,userPassword)
						+ "&toLink=" + toLink;
						ELinkApi eLinkApi = new ELinkApi();
						String text = msg
								+ "<a style=\"cursor:hand\" href=\"#\" onclick=\"window.open('"
								+ url + "')\" >" + urlName + "</a> ";
						eLinkApi.sendNotify(this.serverDN, 
								            this.port, 
								            this.area,
								            receiverAccounts, 
								            title, 
								            text);
                    } else if ("cocall".equals(type)) {
                    	logger.debug("----------------cocall---------------");
                        toLink = toLink.replaceAll("\\&", "%26");
                        logger.debug("----------------userAccounts---------------"+userAccounts);
                        userAccounts =java.net.URLEncoder.encode(userAccounts,"utf-8");
                     
                        logger.debug("----------------userAccounts---------------"+userAccounts);
						url= oaIP
						+ "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount="
						+ domainAccount + "&userName=" + userAccounts
						+ "&RealtimeMsgLogin=" + modelName + "&userPassword="
						+ loginPageSetBD.addTmpPasswordPO(modelName,userPassword)
						+ "&toLink=" + toLink;
						
                    	CocallSendMessage cocall = new CocallSendMessage();
                    	cocall.sendNotify(receiverAccounts, title, urlName, url); 
                    }
					res = true;
				}
	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return res;
	}
	    
    /**
     * 注销用户
     * @param Server String
     * @param port String
     * @param domainName String
     * @param userName String
     * @param disable boolean
     * @return boolean
     */
    public boolean disableEmployee(String account,boolean disable){
         boolean result = false;
         if ("eLink".equals(type)) {
        	 ELinkApi eLinkApi = new ELinkApi();
            try {
                result = eLinkApi.disableEmployee(this.serverDN, this.port, this.area, account, disable);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
         }
        return result;
    }

    /**
     * 返回LocalServerDN
     * @return String
     */
    public String getLocalServerDN(){
        return this.serverLocalDN;
    }
        
    /**
     * 返回ServerDN
     * @return String
     */
    public String getServerDN(){
        return this.serverDN;
    }

    /**
     * 取得用户单点登陆的sessionKey
     * @param account String 帐号
     * @return String
     */
	public String getSessionKey(String account){
        String res ="";
        if ("rtx".equals(type)) {
        	RTXSvrApi rtxSvr=new RTXSvrApi();
        	res = rtxSvr.getSessionKey(account);
        } else if ("rtx_http".equals(type)) {
        	HttpRTX rtxSvr=new HttpRTX();
        	try {
				res = rtxSvr.getSessionKey(account);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return res;
	}

    /**
     * 取得用户单点登陆的URL串
     * @param account String 帐号
     * @param pwd String 密码
     * @return String
     */
    public String getLogURL(String account,String pwd){
        return new GKLogin().getLoginURL(account,pwd);
    }
        
    /**
     * elink判断是否在线
     * @param account String 帐号
     * @return boolean 在线 true 不在线 false
     */
    public boolean isLoginInElink(String accounts)
            throws MalformedURLException {

        ELinkApi elinkapi = new ELinkApi();
        return elinkapi.isLogin(serverDN,port,area,accounts);
    }
        
    /**
     * 返回Used
     * @return boolean   使用 true 不使用 false
     */
    public boolean getUsed(){
        if("1".equals(this.use)){
            return true;
        }else{
            return false;
        }
    }
        
    /**
     * 返回Type
     * @return String  使用即时通讯： 1 不使用： 0
     */
    public String getType(){
        return this.type;
    }
        
    /**
     * 返回ZoneID
     * @return String
     */
    public String getZoneID(){
        return this.zoneID;
    }
        
	/**
 	 * 返回端口
     * @return String
     */
	public String getPort(){
		return this.port;
	}
        
	/**
	 * 返回域名(elink)
	 * @return String
	 */
	public String getArea(){
		return this.area;
	}

    public String getPushType() {
        return pushType;
    }
}
