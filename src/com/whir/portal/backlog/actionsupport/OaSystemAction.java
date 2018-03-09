package com.whir.portal.backlog.actionsupport;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.whir.component.actionsupport.BaseActionSupport;

public class OaSystemAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(OaSystemAction.class.getName());
	/*
	 * OA系统待办
	 */
	public String oaSystemPage(){
		String currentTab = this.request.getParameter("currentTab");
		HttpSession session = this.request.getSession(true);
	    session.setAttribute("currentTab", currentTab);
	    return "oaSystemPage";
	}
}
