package com.whir.portal.backlog.actionsupport;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.component.actionsupport.BaseActionSupport;

public class AssetsManageAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(AssetsManageAction.class.getName());
	/**
	 * 资产管理待办
	 */
	public String assetsManagePage() {
		String currentTab = this.request.getParameter("currentTab");
		HttpSession session = this.request.getSession(true);
	    session.setAttribute("currentTab", currentTab);
	    return "assetsManagePage";
	}
}