package com.whir.rd.util;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import com.whir.component.config.ConfigXMLReader;
import com.whir.component.security.crypto.EncryptUtil;
import com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD;
import com.whir.org.common.util.SysSetupReader;

public class SendOutMail {
	/*
	 * 发送邮件 daiql
	 * httpServletRequest
	 * title 标题
	 * link 要单点的地址
	 * content 内容
	 * empIds 发送至  例$11$$22$
	 * recordId 流程记录id
	 * type 哪个模块调用
	 */
 public void sendOutMail(HttpServletRequest httpServletRequest,String title,String link,String content,String empIds,String recordId,String types){
	 	ConfigXMLReader reader = new ConfigXMLReader();
	    String oaIP = reader.getAttribute("OAIP", "Server");
		InnerMailBD mail = new InnerMailBD();
		String mail_account = com.whir.common.util.CommonUtils.getSessionUserAccount(httpServletRequest);
		String mail_pass = com.whir.common.util.CommonUtils.getSessionUserPassword(httpServletRequest);
		String userName =  com.whir.common.util.CommonUtils.getSessionUserName(httpServletRequest);
		Long userId =  com.whir.common.util.CommonUtils.getSessionUserId(httpServletRequest);
		String domainId = com.whir.common.util.CommonUtils.getSessionDomainId(httpServletRequest).toString();
		boolean isEncryptPass = true;
		String linkcontent="";
		try {
			//只有邮件提醒 没有反向登录
			if(link.equals("")) {
				linkcontent = content;
			}else{
				String realPassword="";
				String sql = "select USERPASSWORD  from  org_employee  A  where   EMP_ID='" + 
						userId + "'  and A.USERISACTIVE=1 and  A.USERISDELETED<>1 ";
				realPassword = searchBySql(sql);
				String domainAccount = SysSetupReader.getInstance().isMultiDomain();
				EncryptUtil eutil = new EncryptUtil();
				String verifyCode ="";
				if(types.equals("asset")) {
					verifyCode=eutil.getSysEncoderKeyVlaue("applyId", recordId, "AssetApplyAction");
					link = link+"&verifyCode="+verifyCode;
				}
				link = link.replaceAll("\\&", "%26");
				String mail_url=oaIP + 
	            "/defaultroot/ReverseLogin!msLoginPage.action?domainAccount=" + 
	            domainAccount + "&userName=" + URLEncoder.encode(userName, "utf-8") + 
	            "&RealtimeMsgLogin=" + URLEncoder.encode("ezFLOW", "utf-8") + "&userPassword=" + 
	            realPassword + 
	            "&msgType=sendOutMail&WORKACTIVITY=-1&recordId="+ recordId + 
	            "&toLink=" + link;
			   //邮件里的 连接
			   linkcontent += "<P> "+ content;
			   linkcontent += " <a href=\"" + mail_url + "\">点击查看</a>";
			   linkcontent += "</P>";		  
				    	  
			}
		    mail.sendInnerMail(title, linkcontent, "1", userName,
						userId, empIds,
						userName,
						domainId, mail_account, mail_pass, isEncryptPass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 }
 public String searchBySql(String sql)
 {
	    String result="";
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try{
			conn = new com.whir.common.util.DataSourceBase().getDataSource()
						.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()){
				result=rs.getString(1);
			}
		   }catch(Exception eee){
		    eee.printStackTrace();
		}finally{
			try {
				if (null != rs) rs.close();
				if (null != st) st.close();
				if (null != conn)conn.close();
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
 }
}
