package com.whir.qd.portal.module.actionsupport;

import org.apache.log4j.Logger;

import com.whir.component.actionsupport.BaseActionSupport;

public class InformationReportAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(InformationReportAction.class.getName());
	/**
	 * 青岛信息报道详细页面
	 */
	public String view(){
	    return "view";
	}
}