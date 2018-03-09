/**
 * 
 */
package com.whir.evo.weixin.util;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;

import com.whir.common.util.MD5;
import com.whir.service.common.CallApi;
import com.whir.evo.weixin.util.Xml2JsonUtil;
import com.whir.evo.weixin.util.PropertyUtil;
import com.whir.evo.weixin.util.XmlHelper;
import com.whir.component.config.ConfigXMLReader;

/**
 * Title: WebServiceUtils.java<br>
 * Description: <br>
 * Project: ezOffice <br>
 * Company: Finalist IT Group <br>
 * Copyright: 2011 www.finalist.cn Inc. All rights reserved. <br>
 * Date: 2011-3-22
 * 
 * @author Terry
 * @version 1.0
 */
public class WebServiceUtils {

	private final static Log log = LogFactory.getLog(WebServiceUtils.class);
	private static Logger logger = Logger.getLogger(WebServiceUtils.class);
	// WSDL
	private static String wsdl_address = "xfservices/GeneralWeb?wsdl";

	private static String key = "dffd512f3c274ec11af53753fc82b483";
	private static String vkey = "";

	public static String getWebServiceData(Map<String, String> params,
								            HttpServletRequest request,
								            String userKey) {
		logger.debug("log!!!@@@@inin");
		if (params == null || params.isEmpty()) {
			return null;
		}
		
        HttpSession session = request.getSession();
		
		//没有oa令牌
		if(session.getAttribute("OaToken")==null || "".equals(session.getAttribute("OaToken").toString())){
			String oaToken = getEzOfficeToken(userKey);
			session.setAttribute("OaToken",oaToken);
		}
		log.debug("----OaToken:"+session.getAttribute("OaToken"));
		ConfigXMLReader reader = new ConfigXMLReader();
		params.put("ServiceToken", session.getAttribute("OaToken")+"");
//		params.put("serviceKey", PropertyUtil.getPropertyByKey("serviceKey"));
		params.put("serviceKey", reader.getAttribute("Weixin","serviceKey"));
		params.put("userKey", userKey);
//		params.put("userKeyType", PropertyUtil.getPropertyByKey("userKeyType"));
		params.put("userKeyType", reader.getAttribute("Weixin","userKeyType"));
		
		// construct the request
		StringBuilder sb = new StringBuilder("<input>");
		if (StringUtils.isNotEmpty(reader.getAttribute("Weixin","key"))) {
			vkey = reader.getAttribute("Weixin","key");
		}
		//String domainId = params.get("domain") == null ? "0" : params.get("domain").toString();
		//com.whir.org.common.util.SysSetupReader sysRed= com.whir.org.common.util.SysSetupReader.getInstance();
		//key = sysRed.getVkey(domainId);
		//log.debug("!!!~~~key="+key);
		logger.debug("log!!!~~~key="+key);
		MD5 md5 = new MD5();
		key = md5.toMD5(vkey);
		//log.debug("!!!---key="+key);
		logger.debug("log!!!~~~key="+key);
		sb.append("<key>" + key + "</key>");
		Set<Entry<String, String>> set = params.entrySet();
		for (Entry<String, String> entry : set) {
			if(entry.getKey() != ""){
				sb.append("<" + entry.getKey() + ">");
				sb.append("" + entry.getValue() + "");
				sb.append("</" + entry.getKey() + ">");
			}else{
				sb.append("" + entry.getValue() + "");
			}
		}
		sb.append("</input>");

		try {
			if (log.isDebugEnabled()) {
				log.info(sb.toString());
			}
			
			CallApi api = new CallApi();
			log.debug("!!!@@@sb="+sb.toString());
			return api.getResult(sb.toString());

			/*
			Client client = new Client(new URL(PropertyUtil.getPropertyByKey("webserviceUrl") + wsdl_address));

//			Object[] results = client.invoke("OAManager", new Object[] { sb.toString() });
			
			
	        String sbStr = sb.toString().replaceAll("&(?!lt;|gt;|amp;|#|nbsp;|quot;)", "&amp;");
	        if(log.isDebugEnabled() ){
	        	log.info("WebServiceUtils getWebServiceData sbStr:" + sbStr);
	        }
	        Object[] results = client.invoke("OAManager", new Object[] {sbStr});
	      
	        
			if (log.isDebugEnabled()) {
				log.info((String) results[0]);
			}
			// 输出xml格式
			return (String) results[0];
			*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	public static String saveDisplayChannel(Map<String, String> params, List<Map> param2) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		// construct the request
		StringBuilder sb = new StringBuilder("<input>");

		/*if (StringUtils.isNotEmpty(PropertyUtil.getPropertyByKey("key"))) {
			key = PropertyUtil.getPropertyByKey("key");
		}*/
		String domainId = params.get("domain") == null ? "0" : params.get("domain").toString();
		com.whir.org.common.util.SysSetupReader sysRed= com.whir.org.common.util.SysSetupReader.getInstance();
		key = sysRed.getVkey(domainId);
		MD5 md5 = new MD5();
		key = md5.toMD5(key);
		sb.append("<key>" + key + "</key>");
		sb.append("<cmd>");
		sb.append(params.get("cmd"));
		sb.append("</cmd>");
		sb.append("<domain>");
		sb.append(params.get("domain"));
		sb.append("</domain>");
		sb.append("<userId>");
		sb.append(params.get("userId"));
		sb.append("</userId>");
		if (param2 != null && param2.size() > 0) {
			for (Map map : param2) {
				sb.append("<myDisplayChannel><channelId>");
				sb.append(map.get("channelId"));
				sb.append("</channelId>");
				sb.append("<channelIdString>");
				sb.append(map.get("channelIdString"));
				sb.append("</channelIdString>");
				sb.append("<channelName>");
				sb.append(map.get("channelName"));
				sb.append("</channelName></myDisplayChannel>");
			}
		}

		sb.append("</input>");

		try {
			Client client = new Client(new URL(PropertyUtil.getPropertyByKey("webserviceUrl") + wsdl_address));
			if (log.isDebugEnabled()) {
				log.info(sb.toString());
			}
//			Object[] results = client.invoke("OAManager", new Object[] { sb.toString() });
			
			/*Modify by dingpc 2013-03-04 for & Error [START]*/
	        String sbStr = sb.toString().replaceAll("&(?!lt;|gt;|amp;|#|nbsp;|quot;)", "&amp;");
	        if(log.isDebugEnabled() ){
	        	log.info("WebServiceUtils saveDisplayChannel sbStr:" + sbStr);
	        }
	        Object[] results = client.invoke("OAManager", new Object[] {sbStr});
	        /*Modify by dingpc 2013-03-04 for & Error [END]*/
	        
			if (log.isDebugEnabled()) {
				log.info((String) results[0]);
			}
			// 输出xml格式
			return (String) results[0];

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getMultiWebServiceData(Map<String, Object> params,HttpServletRequest request,String userKey) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		
		HttpSession session = request.getSession();
		
		//没有oa令牌
		if(session.getAttribute("OaToken")==null || "".equals(session.getAttribute("OaToken").toString())){
			String oaToken = getEzOfficeToken(userKey);
			session.setAttribute("OaToken",oaToken);
		}
		log.debug("----OaToken:"+session.getAttribute("OaToken"));
		ConfigXMLReader reader = new ConfigXMLReader();
		params.put("ServiceToken", session.getAttribute("OaToken")+"");
		params.put("serviceKey", reader.getAttribute("Weixin","serviceKey"));
		params.put("userKey", userKey);
		params.put("userKeyType", reader.getAttribute("Weixin","userKeyType"));	
		
		// construct the request
		StringBuilder sb = new StringBuilder("<input>");
		if (StringUtils.isNotEmpty(reader.getAttribute("Weixin","key"))) {
			vkey = reader.getAttribute("Weixin","key");
		}
		MD5 md5 = new MD5();
		key = md5.toMD5(vkey);
		sb.append("<key>" + key + "</key>");
		Set<Entry<String, Object>> set = params.entrySet();
		for (Entry<String, Object> entry : set) {
			if (entry.getValue() instanceof Map) {
				sb.append("<" + entry.getKey() + ">");
				HashMap<String, String> chileMap = (HashMap) entry.getValue();
				Set<Entry<String, String>> childSet = chileMap.entrySet();

				for (Entry<String, String> chileEntry : childSet) {
					sb.append("<" + chileEntry.getKey() + ">");
					sb.append("" + chileEntry.getValue() + "");
					sb.append("</" + chileEntry.getKey() + ">");
				}
				sb.append("</" + entry.getKey() + ">");
			}else if (entry.getValue() instanceof List) {
				List list = (List) entry.getValue();
				for(int i=0;i<list.size();i++){
					sb.append("<" + entry.getKey() + ">");
					HashMap<String, String> chileMap = (HashMap) list.get(i);
					Set<Entry<String, String>> childSet = chileMap.entrySet();

					for (Entry<String, String> chileEntry : childSet) {
						sb.append("<" + chileEntry.getKey() + ">");
						sb.append("" + chileEntry.getValue() + "");
						sb.append("</" + chileEntry.getKey() + ">");
					}
					sb.append("</" + entry.getKey() + ">");
				}
			}else {
			
				sb.append("<" + entry.getKey() + ">");
				sb.append("" + entry.getValue() + "");
				sb.append("</" + entry.getKey() + ">");
			}
		}
		sb.append("</input>");
		log.debug("!!!@@@sb="+sb.toString());
		try {
			if (log.isDebugEnabled()) {
				log.info(sb.toString());
			}
			CallApi api = new CallApi();
			
			String result = api.getResult(sb.toString());
			if (log.isDebugEnabled()) {
				log.info((String) result);
			}
			return result;
			
			/*
			Client client = new Client(new URL(PropertyUtil.getPropertyByKey("webserviceUrl") + wsdl_address));
			if (log.isDebugEnabled()) {
				log.info(sb.toString());
			}
//			Object[] results = client.invoke("OAManager", new Object[] { sb.toString() });
			
		
	        String sbStr = sb.toString().replaceAll("&(?!lt;|gt;|amp;|#|nbsp;|quot;)", "&amp;");
	        if(log.isDebugEnabled() ){
	        	log.info("WebServiceUtils getMultiWebServiceData sbStr:" + sbStr);
	        }
	        Object[] results = client.invoke("OAManager", new Object[] {sbStr});
	      
	        
			if (log.isDebugEnabled()) {
				log.info((String) results[0]);
			}
			
			// 输出xml格式
			return (String) results[0];
			*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static String getWebServiceDataNoReplace(Map<String,Object> params, HttpServletRequest request, String userKey) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		HttpSession session = request.getSession();
		
		//没有oa令牌
		if(session.getAttribute("OaToken")==null || "".equals(session.getAttribute("OaToken").toString())){
			String oaToken = getEzOfficeToken(userKey);
			session.setAttribute("OaToken",oaToken);
		}
		log.debug("----OaToken:"+session.getAttribute("OaToken"));
		ConfigXMLReader reader = new ConfigXMLReader();
		params.put("ServiceToken", session.getAttribute("OaToken")+"");
		params.put("serviceKey", reader.getAttribute("Weixin","serviceKey"));
		params.put("userKey", userKey);
		params.put("userKeyType", reader.getAttribute("Weixin","userKeyType"));	
		// construct the request
		StringBuilder sb = new StringBuilder("<input>");
		if (StringUtils.isNotEmpty(reader.getAttribute("Weixin","key"))) {
			vkey = reader.getAttribute("Weixin","key");
		}
		MD5 md5 = new MD5();
		key = md5.toMD5(vkey);
		sb.append("<key>" + key + "</key>");
		Set<Entry<String, Object>> set = params.entrySet();
		for (Entry<String, Object> entry : set) {
			
			if(entry.getValue() instanceof List){
				List al = (List) entry.getValue();
				for(int i =0;i<al.size(); i ++){
					sb.append("<" + entry.getKey() + ">");
					Map sm = (Map) al.get(i);
					Set<Entry<String, Object>> sset = sm.entrySet();
					for (Entry<String, Object> sentry : sset) {
						sb.append("<" + sentry.getKey() + ">");
						sb.append("" + sentry.getValue() + "");
						sb.append("</" + sentry.getKey() + ">");
					}
					sb.append("</" + entry.getKey() + ">");
				}
			}else{
				sb.append("<" + entry.getKey() + ">");
				sb.append("" + entry.getValue() + "");
				sb.append("</" + entry.getKey() + ">");
			}
			
		}
		sb.append("</input>");
		log.debug("---->csinput="+sb.toString());
		try {
			if (log.isDebugEnabled()) {
				log.info(sb.toString());
			}
			
			/*
			Client client = new Client(new URL(PropertyUtil.getPropertyByKey("webserviceUrl") + wsdl_address));

//			Object[] results = client.invoke("OAManager", new Object[] { sb.toString() });
			
		
	       // String sbStr = sb.toString().replaceAll("&(?!lt;|gt;|amp;|#|nbsp;|quot;)", "&amp;");
	        if(log.isDebugEnabled() ){
	        	log.info("WebServiceUtils getWebServiceData sbStr:" + sb);
	        }
	        Object[] results = client.invoke("OAManager", new Object[] {sb.toString()});
	      
	        
		
			// 输出xml格式
			return (String) results[0];
			*/
			
			CallApi api = new CallApi();
			return api.getResult(sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * 调用接口公用
	 * 返回接口数据json
	 * @param params
	 * @param request
	 * @param userKey
	 * @return
	 */
	public static String getJsonDataNoReplace(Map<String,Object> params,
									            HttpServletRequest request,
									            String userKey) {
		
		HttpSession session = request.getSession();
		
		//没有oa令牌
		if(session.getAttribute("OaToken")==null||
		   "".equals(session.getAttribute("OaToken").toString())){
			
			String oaToken = getEzOfficeToken(userKey);
			session.setAttribute("OaToken",oaToken);
		}
		log.debug("----OaToken:"+session.getAttribute("OaToken"));		
		ConfigXMLReader reader = new ConfigXMLReader();
		params.put("ServiceToken", session.getAttribute("OaToken")+"");
		params.put("serviceKey", reader.getAttribute("Weixin","serviceKey"));
		params.put("userKey", userKey);
		params.put("userKeyType", reader.getAttribute("Weixin","userKeyType"));	
		String xmlstr = getWebServiceDataNoReplace(params,request,userKey);
		String description = "";
		description = XmlHelper.getElement(xmlstr, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			xmlstr = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		xmlstr=xmlstr.replace("&", "&amp;");
		log.debug("----xmlstr:"+xmlstr);
		return Xml2JsonUtil.xml2JSON(xmlstr);
	}
	
	/**
	 * 获取oa访问令牌
	 * @param userKey
	 * @return
	 */
	public static String getEzOfficeToken(String userKey) {
		if (userKey == null || userKey.isEmpty()) {
			return null;
		}
		String domain = "0";
		ConfigXMLReader reader = new ConfigXMLReader();
		StringBuilder sb = new StringBuilder("<input>");
		if (StringUtils.isNotEmpty(reader.getAttribute("Weixin", "key"))) {
			vkey = reader.getAttribute("Weixin", "key");
		}
		key = new MD5().toMD5(vkey);  //加密
		sb.append("<key>" + key + "</key>");
		sb.append("<cmd>getServiceToken</cmd>");
		sb.append("<domain>"+domain+"</domain>");

		//外服务key			
		String serviceKey = reader.getAttribute("Weixin", "serviceKey");
		sb.append("<serviceKey>"+serviceKey+"</serviceKey>");

		//验证类型，0-不依赖OA帐号登入验证的系统，1-依赖OA认证的登入系统
		String verificationType = "0";
		sb.append("<verificationType>"+verificationType+"</verificationType>");

		//OA里帐号或者OA里 userId或者 OA里身份证号
		sb.append("<userKey>"+userKey+"</userKey>");
		
		//0：帐号  1：userId  2：身份证号 3：用户简码
		String userKeyType = reader.getAttribute("Weixin", "userKeyType");
		sb.append("<userKeyType>"+userKeyType+"</userKeyType>");
		
		Date d = new Date();
		String dtime = d.getTime()+"";
		//当前的时间毫秒数（用于生产key和校验，不做token过期校验）
		sb.append("<time>"+dtime+"</time>");
		
		String fixedStr = reader.getAttribute("Weixin", "fixedStr");
		String vkey=serviceKey+verificationType+userKey+userKeyType+dtime+fixedStr;
		String md5key = new MD5().toMD5(vkey);
		//通过为serviceKey等生成的加密串 md5(systemKey+ verificationType +userKey+userKeyTyp+time+fixedStr) +password
		sb.append("<md5key>"+md5key+"</md5key>");
		
		sb.append("</input>");
		log.debug("-------sb："+sb);
        
		String  xmlstr = "";
		try {
			if (log.isDebugEnabled()) {
				log.info(sb.toString());
			}						
			String webserviceUrl=reader.getAttribute("Weixin", "webserviceUrl") + wsdl_address;
			log.debug("webserviceUrl------>>"+webserviceUrl);
			Client client = new Client(new URL(webserviceUrl));
			
	        if(log.isDebugEnabled() ){
	        	log.info("WebServiceUtils getWebServiceData sbStr:" + sb);
	        }
	        Object[] results = client.invoke("OAManager", new Object[] {sb.toString()});
	        
			if (log.isDebugEnabled()) {
				log.info((String) results[0]);
			}
			// 输出xml格式
			xmlstr = results[0]+"";

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("-----!@xmlstr="+xmlstr);
		String result = XmlHelper.getElement(xmlstr, "//result");
		String OaToken = "";
		if("1".equals(result)){
			OaToken = XmlHelper.getElement(xmlstr, "//ServiceToken");
		}
		return OaToken;

	}
	
	/**
	 * @see http://www.w3.org/TR/2004/REC-xml-20040204/#charsets
	 * 		All supported characters
	 * @param data 
	 * 			content in each field 
	 * @return 
	 * 			regular content is filtered from illegal XML char
	 */
	public static String checkXmlChar(String data) {
		StringBuffer appender = new StringBuffer("");
		
	    if (StringUtils.isNotBlank(data)) {
	    	appender = new StringBuffer(data.length());
	    	
	    	for (int i = 0; i < data.length(); i++) {
		        char ch = data.charAt(i);
		        if ((ch == 0x9) || (ch == 0xA) || (ch == 0xD)
		                || ((ch >= 0x20) && (ch <= 0xD7FF))
		                || ((ch >= 0xE000) && (ch <= 0xFFFD))
		                || ((ch >= 0x10000) && (ch <= 0x10FFFF)))
		        	appender.append(ch);
		    }
	    }
	    
	    String result = appender.toString();
	    
	    return result.replaceAll("]]>", "");
	}

	public static void main(String[] argsd) {
		log.debug(PropertyUtil.getPropertyByKey("key"));

		String aaa = "哈1:---->46哈2:---><P><FONT style=\"FONT-FAMILY: 幼圆; FONT-SIZE: 12pt\">新的选项</FONT></P>哈3:--->0哈4:---->[46, <P><FONT style=\"FONT-FAMILY: 幼圆; FONT-SIZE: 12pt\">新的选项</FONT></P>, 0]";
		log.debug(aaa.replaceAll("<.*?>", ""));
	}
	
	
}
