package com.whir.portal.util;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.client.Client;

public class WebServiceUtils {
	 private static final Log log = LogFactory.getLog(WebServiceUtils.class);

	  private static String wsdl_address = "xfservices/GeneralWeb?wsdl";

	  private static String key = "dffd512f3c274ec11af53753fc82b483";

	  public static String getWebServiceData(Map<String, String> params) {
	    if ((params == null) || (params.isEmpty())) {
	      return null;
	    }
	    StringBuilder sb = new StringBuilder("<input>");
	    sb.append("<key>" + key + "</key>");
	    Set set = params.entrySet();
	    sb.append("<parameter>");
	    java.util.Iterator it = params.entrySet().iterator();
	    while(it.hasNext()){
	    	java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
	    	sb.append("<" + (String)entry.getKey() + ">");
		    sb.append((String)entry.getValue());
		    sb.append("</" + (String)entry.getKey() + ">");
	    }
	    sb.append("</parameter>");
	    sb.append("</input>");
	    try
	    {
	      
	      Client client = new Client(new URL("http://localhost:7001/defaultroot/" + wsdl_address));

	      String sbStr = sb.toString().replaceAll("&(?!lt;|gt;|amp;|#|nbsp;|quot;)", "&amp;");
	      if (log.isDebugEnabled()) {
	        log.info("WebServiceUtils getWebServiceData sbStr:" + sbStr);
	      }
	      Object[] results = client.invoke("OAManager", new Object[] { sbStr });

	      if (log.isDebugEnabled()) {
	        log.info((String)results[0]);
	      }

	      return (String)results[0];
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    return null;
	  }
	}