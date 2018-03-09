package com.whir.ezoffice.customize.customermenu.bd;


import net.sf.hibernate.HibernateException;

import org.apache.log4j.Logger;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.*;
import com.whir.ezoffice.customize.customermenu.vo.ListItem;
import com.whir.ezoffice.customize.common.util.CustomerMenuEJBProxy;
import com.whir.ezoffice.customize.customermenu.ejb.CustomMenuEJBBean;
import com.whir.ezoffice.customize.customermenu.ejb.CustomMenuEJBHome;
import com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO;
import com.whir.ezoffice.customize.customermenu.po.CustomerMenuQLCasePO;
import com.whir.ezoffice.customdb.customdb.bd.CustomDatabaseBD;
import javax.servlet.http.HttpServletRequest;
import com.whir.ezoffice.customForm.bd.CustomFormBD;
import com.whir.ezoffice.customdb.common.util.DbOpt;
import com.whir.ezoffice.ezform.util.DateUtil;
import com.whir.ezoffice.workflow.newEJB.WFChannelEJBBean;
import com.whir.ezoffice.workflow.po.WFChannelPO;
import com.whir.i18n.Resource;
import com.whir.org.manager.bd.ManagerBD;

import java.math.BigDecimal;
import java.sql.SQLException;
import com.whir.common.util.CommonUtils;
import com.whir.common.util.ConversionString;
import com.whir.common.util.ParameterGenerator;
import com.whir.common.util.EJBProxy;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.LocaleUtils;
import com.whir.component.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class CustomerMenuDB extends HibernateBase {
    private static Logger logger = Logger.getLogger(CustomerMenuDB.class.getName());

    public CustomerMenuDB() {}
    /**
     * 提取运营域中的所有自下定义菜单对象
     * @param domainId String
     * @return List
     */
    public List getAllCustomMenu(String domainId, String menuId, String show,
			String curUserId, String orgIdString) {
        List retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(5);
            pg.put(domainId, "String");
            pg.put(menuId, "String");
            pg.put(show, "String");
            pg.put(curUserId, "String");
            pg.put(orgIdString, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retList = (List) ejbProxy.invoke("getAllCustomMenu",pg.getParameters());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getAllCustomMenu information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
    /**
     * 获取模块的子模块
     * @param domainId
     * @param menuId
     * @param curUserId
     * @param orgId
     * @return
     * @throws HibernateException
     */
    public List getSubMenusById(String domainId, 
            String menuId,
            String curUserId, 
            String orgIdString) {
        List retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(domainId, "String");
            pg.put(menuId, "String");
            pg.put(curUserId, "String");
            pg.put(orgIdString, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retList = (List) ejbProxy.invoke("getSubMenusById",pg.getParameters());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getSubMenusById information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
    /**
     * 获取栏目菜单
     * @param userId
     * @param orgIdString
     * @param domainId
     * @return
     */
    public List getAllUserTopMenu(String userId, String orgIdString,String domainId) {
        List retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(userId, "String");
            pg.put(orgIdString, "String");
            pg.put(domainId, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retList = (List) ejbProxy.invoke("getAllUserTopMenu",pg.getParameters());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getAllUserTopMenu information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
   /**
    * 获取栏目下菜单option
    * @param list
    * @param menuId
    * @return
    */
    public String getToobarMenuByList(List list,String menuId){
		
    	String res = "";
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				if (menuId.equals(obj[2] + "")) {
				   int menuLevelSet=Integer.parseInt(obj[25]+"");
				   String spaceT="";
				   for(int j=0;j<menuLevelSet;j++){
					  spaceT+="　";
				   }
					res += "<option value=\""+obj[0]+"\">"+spaceT+obj[1]+"</option>";
					res += getToobarMenuByList(list,obj[0]+"");
				}
			}
		}
		return res;
	}
    
    /**
     * 获取栏目下菜单option new
     * @param list
     * @param menuId
     * @return
     */
     public String getToobarMenuByList_new(List list,String menuId,String id){
 		
     	String res = "";
 		if (list != null) {
 			for (int i = 0; i < list.size(); i++) {
 				Object[] obj = (Object[]) list.get(i);
 				if (menuId.equals(obj[2] + "")) {
 				   int menuLevelSet=Integer.parseInt(obj[25]+"");
 				   String spaceT="";
 				   for(int j=0;j<menuLevelSet;j++){
 					  spaceT+="　";
 				   }
 				    if(obj[0].toString().equals(id)){//11.5.0.0 修改的时候选择所属模块的时候 过滤掉本模块
 					res += "<option value=\""+obj[0]+"\" disabled='disabled' >"+spaceT+obj[1]+"</option>";
 				    }else{
 				    res += "<option value=\""+obj[0]+"\" >"+spaceT+obj[1]+"</option>";
 				    }
 					res += getToobarMenuByList_new(list,obj[0]+"",id);
 				}
 			}
 		}
 		return res;
 	}
    /**
     * 获取左侧2级菜单链接
     * @param menuId
     * @param request
     * @return
     */
    public String getLeftMenuAction(String menuId, 
    		                        HttpServletRequest request){
		
    	String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
		HttpSession session = request.getSession(true);
		
		String action = "";
		String compUrl = "";
		try {
			CustomerMenuConfigerPO po = new CustMenuWithOriginalBD()
					.loadMenuSetById(menuId, CommonUtils.getSessionDomainId(request)+ "");
			
			if (po.getMenuHtmlLink() != null && po.getMenuHtmlLink().toString().length() > 0) {         //附件

				String realName = po.getMenuHtmlLink().toString();
				String fileName = po.getMenuFileLink().toString();
				
				/*com.whir.component.security.crypto.EncryptUtil util = 
					      new com.whir.component.security.crypto.EncryptUtil();
				
				String dlcode = util.getSysEncoderKeyVlaue("FileName",realName,"dir");
				
				compUrl = rootPath
				+ "/public/download/download.jsp?verifyCode="
				+ dlcode
				+"&FileName="
				+ realName
				+ "&name="
				+ fileName + "&path=customize";*/
				
				/***修改显示上传方式 2016-01-22 start**/
				String fileServer = com.whir.component.config.ConfigReader.getFileServer(request.getRemoteAddr());
				Map sysMap = com.whir.org.common.util.SysSetupReader.getInstance()
				                  .getSysSetupMap(session.getAttribute("domainId") + "");
				
				com.whir.component.security.crypto.EncryptUtil util = 
				      new com.whir.component.security.crypto.EncryptUtil();
			
			     String dlcode = util.getSysEncoderKeyVlaue("FileName",realName,"dir");
			
				// 上传附件方式
				int smartInUse = 0;
				if (sysMap != null && sysMap.get("附件上传") != null) {
					smartInUse = Integer.parseInt(sysMap.get("附件上传").toString());
				}
				if (smartInUse == 1) {
					
					compUrl = rootPath
					+ "/public/download/download.jsp?verifyCode="
					+ dlcode
					+"&FileName="
					+ realName
					+ "&name="
					+ fileName + "&path=customize";
										 
				} else {
					compUrl = fileServer
					+ "/public/download/download.jsp?verifyCode="
					+ dlcode
					+"&FileName="
					+ realName
					+ "&name="
					+ fileName + "&path=customize";
					 
				}
				/***修改显示上传方式 2016-01-22 end**/			
				
				action = "menuJump('"+compUrl+"')";
			} else if (po.getMenuStartFlow()!= null && po.getMenuStartFlow().toString().length() > 0
					&& !"-1".equals(po.getMenuStartFlow().toString())) {                   //工作流程
                
				String[] menuStartFlows = po.getMenuStartFlow().toString().split("\\$");
				
				if(menuStartFlows.length==2&&
		        		"newFlow".equals(menuStartFlows[0])){
					compUrl = rootPath
					          +"/ezflowopen!startProcess.action?p_wf_processDefinitionKey="+menuStartFlows[1]
                              +"&openType=startOpen";
			        action = "openWin({url:\'"+compUrl+"\',isFull:'false',winName:\'"+po.getId()+"\'})";
			
				}else{
				
					int m = 0;
					StringTokenizer tk = new StringTokenizer(po.getMenuStartFlow().toString(), "$");
					String[] values = new String[tk.countTokens()];
					while (tk.hasMoreTokens()) {
						values[m] = tk.nextToken();
						m++;
					}
					compUrl = rootPath
							+ "/wfopenflow!startProcess.action?p_wf_processId="
							+ values[0];
					action = "openWin({url:\'"+compUrl+"\',isFull:'false',winName:\'"+po.getId()+"\'})";
				}
				
				
			} else if (po.getMenuListTableMap()!= null&& 
					   po.getMenuListTableMap().toString().length()>0&& 
					   !"0".equals(po.getMenuListTableMap().toString())) {                           //自定义表

				compUrl = rootPath+ "/custormerbiz!goRightMenu.action?menuId=" + po.getId();
				if ("0".equals(po.getMenuOpenStyle()+"")) {
					action = "menuJump('"+compUrl+"')";
				} else {
//					action = "window.open('"+compUrl+"')";
					action = "openWin({url:\'"+compUrl+"\',isFull:'false',winName:\'"+po.getId()+"\'})";
				}
			} else{        //自定义链接
				
				compUrl = rootPath+ "/custormerbiz!ssoLink.action?menuId=" + po.getId();
				if ("0".equals(po.getMenuOpenStyle()+"")) {
					action = "menuJump('"+compUrl+"')";
				} else {
					action = "openWin({url:\'"+compUrl+"\',isFull:'false',winName:\'"+po.getId()+"\'})";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return action;
	}
    /**
     * 获取左侧菜单2级以下菜单
     * @param list
     * @param menuId
     * @return
     * @throws Exception
     */
    public String getLeftMenuByList(List list, 
    		                        String menuId,
    		                        HttpServletRequest request) 
               throws Exception {
    	String res = "";
    	if(list!=null){
    		for(int i=0;i<list.size();i++){
    			Object[] obj = (Object[])list.get(i);
    			if(menuId.equals(obj[2]+"")){
    				res += confirmAction(obj[0]+"",request);
    				res += getLeftMenuByList(list,obj[0]+"",request);
    			}
    		}
    	}
    	return res;
    }
    /**
     * 获取菜单链接
     * @param mItem
     * @param request
     * @return
     */
    public String confirmAction(String menuId, 
                                HttpServletRequest request) {
    	String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
		HttpSession session = request.getSession(true);
		CustomerMenuConfigerPO po = new CustMenuWithOriginalBD()
		                                 .loadMenuSetById(menuId, CommonUtils.getSessionDomainId(request)+ "");
		String action = "";
		String compUrl = "";

		if (po.getMenuHtmlLink() != null && po.getMenuHtmlLink().toString().length() > 0) {         //附件
			String realName = po.getMenuHtmlLink().toString();
			String fileName = po.getMenuFileLink().toString();
			String fileServer = com.whir.component.config.ConfigReader.getFileServer(request.getRemoteAddr());
			Map sysMap = com.whir.org.common.util.SysSetupReader.getInstance()
			                  .getSysSetupMap(session.getAttribute("domainId") + "");
			
			com.whir.component.security.crypto.EncryptUtil util = 
			      new com.whir.component.security.crypto.EncryptUtil();
		
		     String dlcode = util.getSysEncoderKeyVlaue("FileName",realName,"dir");
		
			// 上传附件方式
			int smartInUse = 0;
			if (sysMap != null && sysMap.get("附件上传") != null) {
				smartInUse = Integer.parseInt(sysMap.get("附件上传").toString());
			}
			if (smartInUse == 1) {
				compUrl = rootPath
						+ "/public/download/download.jsp?verifyCode="+dlcode+"&FileName="
						+ realName
						+ "&name="
						+ fileName + "&path=customize";
			} else {
				compUrl = fileServer + "/public/download/download.jsp?verifyCode="+dlcode+
				        "&FileName=" + realName
						+ "&name=" + fileName + "&path=customize";
			}
			
			if ("0".equals(po.getMenuOpenStyle()+"")) {
				action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
						+ po.getMenuName() + "\", url:\"" + compUrl
						+ "\", target:'mainFrame',iconSkin:\"fa fa\"},";
			} else {
				action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
						+ po.getMenuName() + "\", click:\"openWin({url:'" + compUrl
						+ "',isFull:'true',winName: 'opencust" + po.getId()
						+ "' });\",target:'_blank',iconSkin:\"fa fa\"},";
			}

		} else if (po.getMenuStartFlow()!= null && po.getMenuStartFlow().toString().length() > 0
				&& !"-1".equals(po.getMenuStartFlow().toString())) {                   //工作流程

			String[] menuStartFlows = po.getMenuStartFlow().toString().split("\\$");
			
			if(menuStartFlows.length==2&&
	        		"newFlow".equals(menuStartFlows[0])){
				compUrl = rootPath
				          +"/ezflowopen!startProcess.action?p_wf_processDefinitionKey="+menuStartFlows[1]
                          +"&openType=startOpen";

		        action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
				+ po.getMenuName() + "\", click:\"openWin({url:'" + compUrl
				+ "',isFull:'true',winName: 'opencust" + po.getId()
				+ "' });\",target:'_blank',iconSkin:\"fa fa\"},";
		
			}else{
			
				int m = 0;
				StringTokenizer tk = new StringTokenizer(po.getMenuStartFlow().toString(), "$");
				String[] values = new String[tk.countTokens()];
				while (tk.hasMoreTokens()) {
					values[m] = tk.nextToken();
					m++;
				}
				compUrl = rootPath
						+ "/wfopenflow!startProcess.action?p_wf_processId="
						+ values[0];

				action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
				+ po.getMenuName() + "\", click:\"openWin({url:'" + compUrl
				+ "',isFull:'true',winName: 'opencust" + po.getId()
				+ "' });\",target:'_blank',iconSkin:\"fa fa\"},";
			}
			
			
		} else if (po.getMenuListTableMap()!= null && po.getMenuListTableMap().toString().length() > 0
				&& !"0".equals(po.getMenuListTableMap().toString())) {                           //自定义表
 
			compUrl = rootPath+ "/custormerbiz!goRightMenu.action?menuId=" + po.getId();
			if ("0".equals(po.getMenuOpenStyle()+"")) {
				action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
						+ po.getMenuName() + "\", url:\"" + compUrl
						+ "\", target:'mainFrame',iconSkin:\"fa fa\"},";
			} else {
				action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
						+ po.getMenuName()+ "\", click:\"openWin({url:'" + compUrl
						+ "',isFull:'true',winName: 'opencust" + po.getId()
						+ "' });\",target:'_blank',iconSkin:\"fa fa\"},";
			}
		} else {                                                                                //自定义链接
 
			compUrl = rootPath+ "/custormerbiz!ssoLink.action?menuId=" + po.getId();
			if ("0".equals(po.getMenuOpenStyle()+"")) {
				action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
						+ po.getMenuName()+ "\", url:\"" + compUrl
						+ "\", target:'mainFrame',iconSkin:\"fa fa\"},";
			} else {
				action = "{ id:" + po.getId() + ", pId:" + po.getMenuBlone() + ", name:\""
						+ po.getMenuName() + "\", click:\"openWin({url:'" + compUrl
						+ "',isFull:'true',winName: 'opencust" + po.getId()
						+ "' });\",target:'_blank',iconSkin:\"fa fa\"},";
			}
		}

		return action;
	}
    /**
     * 获取栏目下菜单Json
     * @param list
     * @param menuId
     * @return
     */
     public String getToobarMenuByListJson(List list,String menuId){
		
    	String json = "";
    	int k=0;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				if (menuId.equals(obj[2] + "")) {
				   json += "{\"id\":" + obj[0] + ", \"text\":\""+obj[1] + "\" "+(k==0?",\"selected\":true":"")+"},";
				   k++;
				}
			}
		}
		return json;
	}
    /**
     * 返回可以显示的菜单
     * @param menuCode String 菜单代码 workflow,information
     * @param domainId String 单位代码
     * @throws Exception
     * @return String
     */
    public String getShowMenu(String menuCode,String domainId) {
    	String retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(menuCode, "String");
            pg.put(domainId, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retList = (String) ejbProxy.invoke("getShowMenu",pg.getParameters());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getShowMenu information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
    /**
     * 设置菜单显示/隐藏
     * @param menuId
     * @param menuLevel
     * @param domainId
     * @param show
     * @return
     */
    public String setMenuDisplay(String menuId, 
                                 String menuLevel,
                                 String domainId, 
                                 Integer show){
    	
    	String retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(menuId, "String");
            pg.put(menuLevel, "String");
            pg.put(domainId, "String");
            pg.put(show, "Integer");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retList = (String) ejbProxy.invoke("setMenuDisplay",pg.getParameters());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to setMenuDisplay information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
    /**
     * 删除菜单以及相关信息
     * @param domainId
     * @param menuId
     * @param menuLevel
     * @return
     * @throws Exception 
     */
    public boolean delBatchCustmizeMenus(String domainId,
    		                             String menuId,
    		                             String menuLevel){
        boolean flag = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(domainId, "String");
            pg.put(menuId, "String");
            pg.put(menuLevel, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            flag = ((Boolean) ejbProxy.invoke("delBatchCustmizeMenus",
                                              pg.getParameters())).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to delBatchCustmizeMenus information :" +
                         e.getMessage());
            logger.error("*************************************");
            throw e;
        } finally {
            return flag;
        }

    }
    /**
     * GET USER WORK FLOW LIST FOR AJAX CHOICE
     * @param domainId String
     * @param moduleId String
     * @return List
     */
    public List getAllWFProcesses(String userId,String domainId) {
    	
//    	System.out.println("-----------getAllWFProcesses--------------------------");
    	
        List list = new ArrayList();
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(domainId, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            List tmpList = (List) ejbProxy.invoke("getWFProcesses",
                                                  pg.getParameters());
            //新流程
            com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService
                 ezFlowService = new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService();
            List list2 = ezFlowService.findProcessDefinitions_other(userId, 
         		                                   null,
                                                    domainId,
                                                    null,
                                                    null);
            
            if(tmpList != null||
            		list2 != null){
            	
            	ListItem item = new ListItem();
                item.setId("-1");
                item.setName("--请选择--");
                list.add(item);
                
                if (tmpList != null) {
                    for (int i = 0; i < tmpList.size(); i++) {
                        Object processObj[] = (Object[]) tmpList.get(i);
                        item = new ListItem();
                        item.setId(((processObj[2] != null ? processObj[2].toString():"")+"$"+
                                    (processObj[4] != null ? processObj[4].toString():"")+"$"+
                                    (processObj[3] != null ? processObj[3].toString():"")+"$"+
                                    (processObj[5] != null ? processObj[5].toString():"")+"$"+
                                    (processObj[6] != null ? processObj[6].toString():"")+""));
                        item.setName(processObj[3].toString());
                        list.add(item);
                    }
                }
                if (list2 != null) {
                    for (int i = 0; i < list2.size(); i++) {
                        Object processObj[] = (Object[]) list2.get(i);
                        item = new ListItem();
                        item.setId("newFlow$"+(processObj[3] != null ? processObj[3].toString():""));
                        item.setName(processObj[2].toString());
                        list.add(item);
                    }
                    
                }
            	
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getAllWFProcesses information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return list;
        }
    }
    /**
     * 判断用户是否有模块权限
     * @param menuCode String 模块编码
     * @param userId String  用户id
     * @param orgId String  用户所属部门
     * @return boolean
     * @throws HibernateException 
     */
    public boolean hasMenuAuth(String menuCode,
                                 String userId,
                                 String orgId) throws HibernateException {
    	     begin();
 		     Connection conn = null;
             String menuviewuser="";
             String menuvieworg="";
             String menuviewgroup="";
             try {
            	 conn = session.connection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("select menu_viewuser,menu_vieworg,menu_viewgroup from oa_custmenu t where menucodeset='"+menuCode+"'");

                 if (rs.next()) {
                     menuviewuser = rs.getString(1);
                     menuvieworg = rs.getString(2);
                     menuviewgroup = rs.getString(3);
                 }
                 rs.close();
                 stmt.close();

             } catch (Exception ex) {
                 ex.printStackTrace();
             } finally {
                 session.close();
                 session = null;
             }

             if(menuviewuser==null||"null".equals(menuviewuser)) menuviewuser="";
             if(menuvieworg==null||"null".equals(menuvieworg)) menuvieworg="";
             if(menuviewgroup==null||"null".equals(menuviewgroup)) menuviewgroup="";


             if(("".equals(menuviewuser)&&"".equals(menuvieworg)&&"".equals(menuviewgroup))){
                return true;
              }
              if(!"".equals(menuviewuser)&&hasMenuAuth_user(userId,menuviewuser)){
                return true;
              }
              if(!"".equals(menuvieworg)&&hasMenuAuth_org(orgId,menuvieworg)){
                return true;
              }
              System.out.println("---userId:"+userId);
              System.out.println("---menuviewgroup:"+menuviewgroup);
              if(!"".equals(menuviewgroup)&&hasMenuAuth_groupTmp(userId,menuviewgroup)){
                return true;
              }
             return false;
    }
    
    private boolean hasMenuAuth_user(String userId,
                                 String menuviewuser) {
        if(menuviewuser.indexOf("$"+userId+"$")>-1){
           return true;
        }else{
           return false;
        }

    }
    private boolean hasMenuAuth_org(String orgId,
                                 String menuvieworg){

          ConversionString conversionString = new ConversionString(menuvieworg);
          String orgIdString = conversionString.getOrgIdString();
          String[]  orgIds = orgIdString.split(",");
          boolean reault=false;
          Connection conn = null;
          try {
              DataSourceBase dsb = new DataSourceBase();
              conn = dsb.getDataSource().getConnection();
          } catch (Exception e) {
              e.printStackTrace();
          }
          try {

              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery("select orgidstring from org_organization where org_id="+orgId);
              String _tmp="";
              if(rs.next()) {
                 _tmp = rs.getString(1);
              }
              rs.close();
              conn.close();
              if(orgIds!=null){
                 for(int i=0;i<orgIds.length;i++){
                     if(_tmp.indexOf("$"+orgIds[i]+"$")>-1){
                        reault = true;
                        break;
                    }
                 }
              }

          } catch (Exception ex) {
              try {
                  if (conn != null) {
                      conn.close();
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
              ex.printStackTrace();
          }
          return reault;


    }
    private boolean hasMenuAuth_groupTmp(String userId, 
    		                             String menuvieworg) {
		Connection conn = null;
		boolean reault = false;
		try {
			DataSourceBase dsb = new DataSourceBase();
			conn = dsb.getDataSource().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("select t.group_id from org_group t,org_user_group tt "
							+ " where t.group_id=tt.group_id and tt.emp_id="
							+ userId);
			String _tmp = "";
			while (rs.next()) {
				_tmp = rs.getString(1);
				System.out.println("---_tmp:"+_tmp);
				if (menuvieworg.indexOf("*" + _tmp + "*") > -1) {
					reault = true;
					break;
				}
			}
			rs.close();
			conn.close();

		} catch (Exception ex) {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			ex.printStackTrace();
		}
		return reault;

	}
    private boolean hasMenuAuth_group(String userId,
                                      String menuvieworg) {
        Connection conn = null;
        boolean reault=false;
        try {
            DataSourceBase dsb = new DataSourceBase();
            conn = dsb.getDataSource().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select t.group_id from org_group t,org_user_group tt "+
                                             " where t.group_id=tt.group_id and tt.emp_id="+userId);
            String _tmp = "";
            while (rs.next()) {
                _tmp = rs.getString(1);
                if (menuvieworg.indexOf("@" + _tmp + "@") > -1) {
                    reault = true;
                    break;
                }
            }
            rs.close();
            conn.close();

        } catch (Exception ex) {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        }
        return reault;

    }
    /**
     * 获取一级菜单链接地址
     * @param menuId
     * @param request
     * @return
     */
    public String getTopMenuAction(String menuId, HttpServletRequest request) {
    	
    	String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
		String url = "";
		try {
			CustomerMenuConfigerPO po = new CustMenuWithOriginalBD()
					.loadMenuSetById(menuId, CommonUtils.getSessionDomainId(request)+ "");
			
			// 没有导航
			if (po.getMenuAction() != null && !"".equals(po.getMenuAction())) {

				url = getMenuActionString(po,request);
			} else if(po.getMenuURLSet()!=null&&
					!"".equals(po.getMenuURLSet())&&
					!"0".equals(po.getMenuURLSet())&&
					!"-1".equals(po.getMenuURLSet())){
				
				CustomerMenuConfigerPO navpo = new CustMenuWithOriginalBD()
				                       .loadMenuSetById(po.getMenuURLSet(), CommonUtils.getSessionDomainId(request)+ "");
				//自定义表单
				if(navpo.getMenuListTableMap()!=null&&
						!"".equals(navpo.getMenuListTableMap())){
					url = rootPath+"/custormerbiz!goRightMenu.action?menuId="+navpo.getId();
				}else if (navpo.getMenuAction() != null && !"".equals(navpo.getMenuAction())) {
					url = getMenuActionString(navpo,request);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}
    private String getMenuActionString(CustomerMenuConfigerPO po,
    		                           HttpServletRequest request){
    	HttpSession session = request.getSession(true);
    	String url="";
    	
    	String action = po.getMenuAction();
		String menuActionParams1 = po.getMenuActionParams1();
		String menuActionParams2 = po.getMenuActionParams2();
		String menuActionParams3 = po.getMenuActionParams3();
		String menuActionParams4 = po.getMenuActionParams4();
		String menuActionParams4Value = po.getMenuActionParams4Value();
		// 参数一
		String paraName = "";
		String paraType = "";
		String paraValue = "";
		String password = com.whir.common.util.CommonUtils.decryptPassword(session.getAttribute("userPassword")+"");
		if (menuActionParams1 != null && menuActionParams1.length() > 0) {
			paraName = menuActionParams1.substring(0, menuActionParams1.indexOf("|"));
			paraType = menuActionParams1.substring(menuActionParams1.indexOf("|") + 1, menuActionParams1.length());
			paraValue = "";
			if ("1".equals(paraType))
				paraValue = CommonUtils.getSessionUserAccount(request).toString();
			else if ("2".equals(paraType))
				paraValue = ToolBox.MD5Encode(password);
			else if ("3".equals(paraType))
				paraValue = CommonUtils.getSessionOrgName(request).toString();
			if (menuActionParams1.length() > 0) {
				url += "&" + paraName + "=" + paraValue;
			}
		}
		// 参数二
		if (menuActionParams2 != null && menuActionParams2.length() > 0) {
			paraName = menuActionParams2.substring(0, menuActionParams2.indexOf("|"));
			paraType = menuActionParams2.substring(menuActionParams2.indexOf("|") + 1, menuActionParams2.length());
			paraValue = "";
			if ("1".equals(paraType))
				paraValue = CommonUtils.getSessionUserAccount(request).toString();
			else if ("2".equals(paraType))
				paraValue = ToolBox.MD5Encode(password);
			else if ("3".equals(paraType))
				paraValue = CommonUtils.getSessionOrgName(request).toString();
			if (menuActionParams2.length() > 0) {
				url += "&" + paraName + "=" + paraValue;
			}
		}
		// 参数三
		if (menuActionParams3 != null && menuActionParams3.length() > 0) {
			paraName = menuActionParams3.substring(0, menuActionParams3.indexOf("|"));
			paraType = menuActionParams3.substring(menuActionParams3.indexOf("|") + 1, menuActionParams3.length());
			paraValue = "";
			if ("1".equals(paraType))
				paraValue = CommonUtils.getSessionUserAccount(request).toString();
			else if ("2".equals(paraType))
				paraValue = ToolBox.MD5Encode(password);
			else if ("3".equals(paraType))
				paraValue = CommonUtils.getSessionOrgName(request).toString();
			if (menuActionParams3.length() > 0) {
				url += "&" + paraName + "=" + paraValue;
			}
		}
		// 参数四
		if (menuActionParams4 != null && menuActionParams4.length() > 0) {
			paraName = menuActionParams4;
			paraValue = menuActionParams4Value;
			if (menuActionParams4.length() > 0) {
				url += "&" + paraName + "=" + paraValue;
			}
		}
		if (url != null && url.length() > 0) {
			if (action.indexOf("?") > -1) {
				url = action + url;
			} else {
				url = action + "?" + url.substring(1, url.length());
			}
		} else {
			url = action;
		}
		
    	return url;
    }
    /**
     * 检查是否有新增删除权限
     * @param menuViewUser
     * @param menuViewOrg
     * @param menuViewGroup
     * @param curUserId
     * @param orgId
     * @param domainId
     * @return
     */
    public boolean checkCustmenuAuth(String menuViewUser, String menuViewOrg,
			String menuViewGroup, String curUserId, String orgIds,
			String domainId) {
		boolean flag = false;
		if ((menuViewUser != null && menuViewUser
				.indexOf("$" + curUserId + "$") > -1)
				|| (menuViewOrg != null && existsOrg(menuViewOrg, orgIds))
				|| (menuViewGroup != null && existsGroup(menuViewGroup,
						curUserId, domainId))) {
			flag = true;
		}
		if (!flag) {
			DbOpt dbopt = null;
			try {

				dbopt = new DbOpt();
				String orgjz = dbopt
						.executeQueryToStr("select sidelineorg from org_employee where emp_id="
								+ curUserId);

				String[] orgjzs = null; // 得到兼职组织
				if (orgjz != null && !"".equals(orgjz)) {
					String orgIdString = new ConversionString(orgjz)
							.getOrgIdString();
					orgjzs = orgIdString.split(",");
				}
				if (orgjzs != null) {
					for (int i = 0; i < orgjzs.length; i++) {
						if ((orgjzs[i] != null && existsOrg(orgjzs[i], orgIds))) {
							flag = true;
							break;
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					dbopt.close();
				} catch (SQLException ex) {
				}
			}
		}
		return flag;
	}
    public boolean existsOrg(String scope, String orgIdString) {
		boolean retFlag = false;
		if (scope != null && scope.length() > 0 && orgIdString != null) {
			String[] orgIdStrings = ((orgIdString + "$").substring(1,
					(orgIdString + "$").length())).split("\\$\\$");
			for (int i = 0; i < orgIdStrings.length; i++) {
				if (scope.indexOf("*" + orgIdStrings[i] + "*") > -1) {
					retFlag = true;
				}
			}

		}
		return retFlag;
	}

	public boolean existsGroup(String scope, String userId, String domainId) {
		boolean retFlag = false;
		if (scope != null && scope.length() > 0) {
			String _scope = scope.replaceAll("\\*","@");
			List list = new CustomerMenuDB().getAllGroupsByUserId(userId,
					domainId);
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = (Object[])list.get(i);
					if (_scope.indexOf("@" + obj[0] + "@") > -1) {
						retFlag = true;
						break;
					}
				}
			}
		}
		return retFlag;
	}
	/**
	 * 获取用户所在群组id
	 * @param userId
	 * @param domainId
	 * @return
	 */
	public List getAllGroupsByUserId(String userId, String domainId) {
        List retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(userId, "String");
            pg.put(domainId, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);

            retList = (List) ejbProxy.invoke("getAllGroupsByUserId",
                                             pg.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getAllGroupsByUserId information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
	/**
	 * 获取方案
	 * @param listCaseId
	 * @param domainId
	 * @return
	 */
	public String[][] getListField(String listCaseId, String domainId) {
        String[][] retlist = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(listCaseId, "String");
            pg.put(domainId, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retlist = (String[][]) ejbProxy.invoke("getListField",
                    pg.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getListField information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retlist;
        }
    }
	/**
	 * 
	 * @param tblId
	 * @param orgIdString
	 * @param userId
	 * @param orgId
	 * @param menuId
	 * @return
	 */
	public String getViewScope2(String tblId, String orgIdString,
			String userId, String orgId, String menuId) {
		String list = null;
		try {
			ParameterGenerator pg = new ParameterGenerator(5);
			pg.put(tblId, "String");
			pg.put(orgIdString, "String");
			pg.put(userId, "String");
			pg.put(orgId, "String");
			pg.put(menuId, "String");
			EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
					"CustomMenuEJBLocal", CustomMenuEJBHome.class);
			list = (String) ejbProxy
					.invoke("getViewScope2", pg.getParameters());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*************************************");
			logger.error("error to getViewScope2 information :"
					+ e.getMessage());
			logger.error("*************************************");
		} finally {
			return list;
		}
	}
	/**
	 * 替换特殊标记符
	 * @param request
	 * @param defConstrain
	 * @return
	 */
	private String replaceDefConstrain(HttpServletRequest request,String defConstrain){

        String ret = defConstrain;
        ret = ret.replaceAll("&uid",CommonUtils.getSessionUserId(request)+"");
        ret = ret.replaceAll("&userAccounts",CommonUtils.getSessionUserAccount(request)+"");
        ret = ret.replaceAll("&userOrgID",CommonUtils.getSessionOrgId(request)+"");
        ret = ret.replaceAll("&number",request.getParameter("number")+"");
        ret = ret.replaceAll("&empidcard",getEmpIdCard(CommonUtils.getSessionUserId(request)+"")+"");
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ret = ret.replaceAll("&Currenttime",sdf.format(now));
        return ret;
    }
	/**
     * 取身份证号
     * @param userId String
     * @return String
     */
    private String getEmpIdCard(String userId) {

        DbOpt dbopt = null;
        String ret = "";
        try {
            dbopt = new DbOpt();
            java.sql.ResultSet rs = dbopt.executeQuery("select empidcard from org_employee where emp_id="+userId);
            if(rs.next()){
              ret = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch (SQLException ex) {}
        }
        return ret;
    }
    /**
     * 获取查询对象
     * @param po
     * @param request
     * @return
     * @throws Exception
     */
    public String getSelectPara(CustomerMenuConfigerPO po,
			                    HttpServletRequest request) throws Exception {

		StringBuffer sbf = new StringBuffer();
		CustomDatabaseBD dbBD = new CustomDatabaseBD();
		String tableId = po.getMenuListTableMap() + "";
		String tableName = dbBD.getSingleTableName(tableId);
		String[][] listFields = null;
		if (po.getMenuListDisplayElements() != null
				&& po.getMenuListDisplayElements().length() > 0) {
			listFields = getListField(po.getMenuListDisplayElements().toString(), 
					                  CommonUtils.getSessionDomainId(request) + "");
		} else {
			listFields = dbBD.getListField(tableId);
		}
		if (listFields != null && listFields.length > 0) {
			sbf.append(tableName + "_id,");
			for (int i = 0; i < listFields.length; i++) {
				sbf.append(listFields[i][2] + ",");
			}
			sbf = new StringBuffer(escapeString(sbf.toString()));
		}
		return sbf.toString();
	}
    
    /**
     * 获取查询对象  自定义门户接口  2016-05-05 11.5需求
     * @param po
     * @param request
     * @return
     * @throws Exception
     */
    public String getSelectPara_portal(CustomerMenuConfigerPO po,String showListFieldId,
			                    HttpServletRequest request) throws Exception {

		StringBuffer sbf = new StringBuffer();
		CustomDatabaseBD dbBD = new CustomDatabaseBD();
		String tableId = po.getMenuListTableMap() + "";
		String tableName = dbBD.getSingleTableName(tableId);
		String[][] listFields = null;
		CustomMenuEJBBean customMenuEJBBean=new CustomMenuEJBBean();
		if (showListFieldId!= null&& showListFieldId.length() > 0) {
			//listFields = getListField(showListFieldId,CommonUtils.getSessionDomainId(request) + "");
			listFields=customMenuEJBBean.getListField_portal(showListFieldId,CommonUtils.getSessionDomainId(request) + "");
		} else {
			listFields = dbBD.getListField(tableId);
		}
		if (listFields != null && listFields.length > 0) {
			sbf.append(tableName + "_id,");
			for (int i = 0; i < listFields.length; i++) {
				sbf.append(listFields[i][2] + ",");
			}
			sbf = new StringBuffer(escapeString(sbf.toString()));
		}
		return sbf.toString();
	}
    /**
     * 获取查询表条件
     * @param po
     * @param request
     * @return
     * @throws Exception
     */
    public String getWherePara(CustomerMenuConfigerPO po,
			HttpServletRequest request) throws Exception {

		StringBuffer sbf = new StringBuffer();
		String tableName = new CustomDatabaseBD().getSingleTableName(po.getMenuListTableMap()+"");
		
		boolean hasUserAccounts = false;
		// 判断 自定义模块是否设置查看全部数据  
		String menuSeeAuth = po.getMenuSeeAuth();
		if ("1".equals(menuSeeAuth)){
			hasUserAccounts = true;
		}
		sbf.append(" where 1=1");
		sbf.append(" and ("+tableName+"_WORKSTATUS is  null  or  "+tableName+"_WORKSTATUS <>-4   )");//11.5需求  办理查阅删除数据的同时删除（逻辑删除）自定义模块的数据 ，即过滤tableName_WORKSTATUS!=-4的数据
		// 取得列表默认显示约束
		String defConstrain = po.getMenuDefQueryCondition();
		if (defConstrain != null && defConstrain.length() > 0) {
			defConstrain = replaceDefConstrain(request,defConstrain);
			sbf.append("  and " + defConstrain);
		}

		String viewScopeWhere = "";
		// view scope
		if (!hasUserAccounts) {
			viewScopeWhere = getViewScope2(tableName, 
					CommonUtils.getSessionOrgIdString(request)+"", 
					CommonUtils.getSessionUserId(request)+"",
					CommonUtils.getSessionOrgId(request)+"",
					po.getId() + "");

			// 针对老数据升级情况 取出org是空值的数据
			viewScopeWhere = "(" + viewScopeWhere + " or " + tableName
					+ "_org is null or " + tableName + "_org='' )";
		}
		if (viewScopeWhere != null && viewScopeWhere.length() > 0) {
			sbf.append(" and " + viewScopeWhere);
		}
		String retstr = "";
		
		ManagerBD mBD = new ManagerBD();
		if (hasUserAccounts ||
	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-01") ||
//	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-02") ||
	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-03") ||
	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-04") ){
			retstr = sbf.toString()+" and "+getSearchPartSql(po,request);
		}else{
			retstr = " where 1<>1 ";
		}
		return retstr;
	}
    
    
    
    
    /**
     * 获取查询表条件(add by tianml 2015/11/16 用于设置导出的数据范围)
     * @param po
     * @param request
     * @return
     * @throws Exception
     */
    public String getWhereParaExport(CustomerMenuConfigerPO po,
			HttpServletRequest request) throws Exception {

		StringBuffer sbf = new StringBuffer();
		String tableName = new CustomDatabaseBD().getSingleTableName(po.getMenuListTableMap()+"");
		
		boolean hasUserAccounts = false;
		// 判断 自定义模块是否设置查看全部数据
		String menuSeeAuth = po.getMenuSeeAuth();
		if ("1".equals(menuSeeAuth)){
			hasUserAccounts = true;
		}
		sbf.append(" where 1=1 "); 
		sbf.append(" and ("+tableName+"_WORKSTATUS is  null  or  "+tableName+"_WORKSTATUS <>-4   )");//11.5需求  办理查阅删除数据的同时删除（逻辑删除）自定义模块的数据 ，即过滤tableName_WORKSTATUS!=-4的数据

		// 取得列表默认显示约束
		String defConstrain = po.getMenuDefQueryCondition();
		if (defConstrain != null && defConstrain.length() > 0) {
			defConstrain = replaceDefConstrain(request,defConstrain);
			sbf.append("  and " + defConstrain);
		}

		String viewScopeWhere = "";
		// view scope
		if (!hasUserAccounts) {
			viewScopeWhere = getViewScopeExport(tableName, 
					CommonUtils.getSessionOrgIdString(request)+"", 
					CommonUtils.getSessionUserId(request)+"",
					CommonUtils.getSessionOrgId(request)+"",
					po.getId() + "");

			// 针对老数据升级情况 取出org是空值的数据
			viewScopeWhere = "(" + viewScopeWhere + " or " + tableName
					+ "_org is null or " + tableName + "_org='' )";
		}
		if (viewScopeWhere != null && viewScopeWhere.length() > 0) {
			sbf.append(" and " + viewScopeWhere);
		}
		String retstr = "";
		
		ManagerBD mBD = new ManagerBD();
		if (hasUserAccounts ||
//	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-01") ||
//	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-02") ||
//	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-03") ||
	            mBD.hasRight(CommonUtils.getSessionUserId(request)+"", "99-" + po.getId() + "-04") ){
			retstr = sbf.toString()+" and "+getSearchPartSql(po,request);
		}else{
			retstr = " where 1<>1 ";
		}
		return retstr;
	}
    
    public String getViewScopeExport(String tblId, String orgIdString,String userId, String orgId, String menuId) {
		String list = null;
		CustomMenuEJBBean bean = new CustomMenuEJBBean();
        try {
        	list = bean.getViewScopeExport(tblId, orgIdString, userId, orgId, menuId);
		} catch (HibernateException e) {		 
			e.printStackTrace();
			logger.error("*************************************");
			logger.error("error to getViewScopeExport information :"+ e.getMessage());
			logger.error("*************************************");
		}
		return list;	 
		 
	}
    
    /**
     * 获取查询段 sql
     * @param po
     * @param request
     * @return
     */
    private String getSearchPartSql(CustomerMenuConfigerPO po,
			                        HttpServletRequest request){
    	String sql=" 1=1 ";
    	
    	String[][] queryFields = null;
        //优先自定义模块查询设置
        if(po.getMenuListQueryConditionElements()!=null&&
           !"null".equals(po.getMenuListQueryConditionElements())&&
           !"".equals(po.getMenuListQueryConditionElements())){
        	queryFields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(po.getMenuListQueryConditionElements(),
        			                                                            CommonUtils.getSessionDomainId(request)+"");
        }else{                                                                     //自定义数据库查询设置
        	queryFields = new CustomDatabaseBD().getQueryField(po.getMenuListTableMap().toString());
        }
        String field_name="";
        String field_HTMLtype="";
        String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();
        if (queryFields != null && queryFields.length > 0) {
            
            for (int i = 0; i < queryFields.length; i++) {
            	
                field_name=queryFields[i][2];
                field_HTMLtype = request.getParameter(field_name +"_search_type");

                if ("integer".equals(field_HTMLtype) &&
                    request.getParameter(field_name+"_search_begin").length() > 0 &&
                    request.getParameter(field_name+"_search_end").length()>0 ) {
                	
                	if(request.getParameter(field_name+"_search_begin")!=null && 
                			request.getParameter(field_name+"_search_begin").trim().length() > 0){
                    	sql += " and ("+field_name+" >= " + 
                    	       request.getParameter(field_name+"_search_begin")+")"; //默认等于
                    }
                    if(request.getParameter(field_name+"_search_end")!=null && 
                    		request.getParameter(field_name+"_search_end").trim().length() > 0){
                    	sql += " and ("+field_name+" <= " + 
                    	    request.getParameter(field_name+"_search_end")+")"; //默认等于
                    }
                    
                } else if ("varchar".equals(field_HTMLtype) &&
                           request.getParameter(field_name+"_search").length() > 0) {
                	sql += " and ("+field_name+" like N'%" +
                                  request.getParameter(field_name+"_search") +"%') ";
                } else if ("selectnumber".equals(field_HTMLtype) &&
                           request.getParameter(field_name+"_search").length() >0) {
                	sql += " and ("+field_name+" = "+request.getParameter(field_name+"_search")+")";
                } else if ("selectvarchar".equals(field_HTMLtype) &&
                           request.getParameter(field_name+"_search").length() >0) {
                	sql += " and ("+field_name+" = N'"+request.getParameter(field_name+"_search") +"')";
                } else if ("checkbox".equals(field_HTMLtype) &&
                           request.getParameter(field_name+"_search") != null &&
                           request.getParameter(field_name+"_search").length() > 0) {

                    String[] values = request.getParameterValues(field_name+"_search");
                    String _tmp = " ( ";
                    for (int j = 0; j < values.length; j++) {
                    	_tmp += field_name+" = N'"+values[j]+"' or " +
                    	        field_name+" like N'%"+values[j]+",%' or ";
                    }
                    _tmp = _tmp.substring(0,_tmp.lastIndexOf("or")) +") ";
                    
                    sql += " and "+_tmp;

                } else if ("radiovarchar".equals(field_HTMLtype) &&
                           request.getParameter(field_name+"_search") != null &&
                           request.getParameter(field_name+"_search").length() >0) {

                	sql += " and ("+field_name+" like N'%"+request.getParameter(field_name+"_search") +"%')";

                } else if ("radionumber".equals(field_HTMLtype) &&
                           request.getParameter(field_name+"_search") != null &&
                           request.getParameter(field_name+"_search").length() >0) {

                	sql += " and ("+field_name+"="+request.getParameter(field_name+"_search")+")";

                } else if ("date".equals(field_HTMLtype) ||
                           "time".equals(field_HTMLtype) ||
                           "datetime".equals(field_HTMLtype)) {
                    String start = request.getParameter(field_name+"_search_start");
                    String end = request.getParameter(field_name+"_search_end");
                	
                	if ("date".equals(field_HTMLtype)) {
                		if (databaseType.indexOf("mysql") >= 0||
                        		databaseType.indexOf("DM DBMS")>0) {
                        	if(start!=null&&
                        		!"".equals(start)){
                        		sql += " and "+field_name+" >= '"+start+" 00:00:00'";
                        	}
                        	if(end!=null&&
                        		!"".equals(end)){
                        		sql += " and "+field_name+" <= '"+end+" 23:59:59' ";
                        	}
                        } else{
                        	if(start!=null&&
                        		!"".equals(start)){
                        		sql += " and EZOFFICE.FN_STRTODATE("+field_name+",'') >= EZOFFICE.FN_STRTODATE('"+start+" 00:00:00','') ";
                        	}
                        	if(end!=null&&
                        		!"".equals(end)){
                        		sql += " and EZOFFICE.FN_STRTODATE("+field_name+",'') <= EZOFFICE.FN_STRTODATE('"+end+" 23:59:59','') ";
                        	}
                        }
                    } else if ("time".equals(field_HTMLtype)) {
                    	if ("oracle".equals(databaseType)) {
//                        	sql += " and (to_date("+ field_name+", 'HH:MM:SS') " +
//                                          " between to_date('"+start +"','HH:MI:SS') and to_date('" +end + 
//                                          "','HH:MI:SS')) ";
                        	if(start!=null&&
                        		!"".equals(start)){
                        		sql += " and (to_date("+ field_name+", 'HH:MM:SS') >= to_date('"+start +"','HH:MI:SS')";
                        	}
                        	if(end!=null&&
                        		!"".equals(end)){
                        		sql += " and (to_date("+ field_name+", 'HH:MM:SS') <= to_date('"+end +"','HH:MI:SS') ";
                        	}
                        } else {
                            
                            if(start!=null&&
                        		!"".equals(start)){
                            	start = getTimeString(start);
                                
                        		sql += " and "+field_name+" >= '"+start+"'";
                        	}
                        	if(end!=null&&
                        		!"".equals(end)){
                        		end = getTimeString(end);
                        		sql += " and "+field_name+" <= '"+end+"' ";
                        	}
                        }
                    } else if ("datetime".equals(field_HTMLtype)) {
                    	if (databaseType.indexOf("mysql") >= 0||
                        		databaseType.indexOf("DM DBMS")>0) {
                        	if(start!=null&&
                        		!"".equals(start)){
                        		sql += " and "+field_name+" >= '"+start+"'";
                        	}
                        	if(end!=null&&
                        		!"".equals(end)){
                        		sql += " and "+field_name+" <= '"+end+"' ";
                        	}
                    	}else if(databaseType.indexOf("oracle") >= 0){
                    		if(start!=null&&
                        		!"".equals(start)){
                        		sql += " and (EZOFFICE.FN_STRTODATE(substr("+field_name+",0,18),'L')>=EZOFFICE.FN_STRTODATE('"+start+"','L') ";
                        	}
                        	if(end!=null&&
                        		!"".equals(end)){
                        		sql += " and EZOFFICE.FN_STRTODATE(substr("+field_name+",0,18),'L')<=EZOFFICE.FN_STRTODATE('"+end+"','L')) ";
                        	}
                        } else{
                        	if(start!=null&&
                        		!"".equals(start)){
                        		sql += " and (EZOFFICE.FN_STRTODATE(substring("+field_name+",0,18),'L')>=EZOFFICE.FN_STRTODATE('"+start+"','L') ";
                        	}
                        	if(end!=null&&
                        		!"".equals(end)){
                        		sql += " and EZOFFICE.FN_STRTODATE(substring("+field_name+",0,18),'L')<=EZOFFICE.FN_STRTODATE('"+end+"','L')) ";
                        	}
                        }
                    }

                } else if ("number".equals(field_HTMLtype) &&
                           request.getParameter(queryFields[i][2]+"_search").length()>0) {
                	sql += " and ("+field_name+" = " +request.getParameter(queryFields[i][2]+"_search")+")"; //默认等于
                }else if ("float".equals(field_HTMLtype)) {
                	
                    if(request.getParameter(queryFields[i][2]+"_search_begin")!=null && 
                    		request.getParameter(queryFields[i][2]+"_search_begin").trim().length() > 0){
                    	sql += " and ("+field_name+" >= " + 
                    	       request.getParameter(field_name+"_search_begin")+")"; //默认等于
                    }
                    if(request.getParameter(queryFields[i][2]+"_search_end")!=null && 
                    		request.getParameter(queryFields[i][2]+"_search_end").trim().length() > 0){
                    	sql += " and ("+field_name+" <= " + 
                    	    request.getParameter(field_name+"_search_end")+")"; //默认等于
                    }
                }
            }
        }
    	
    	return "("+sql+")";
    }
    private String getTimeString(String datestr) {

        String str1 = datestr;
        if (datestr != null) {
            str1 = "";
            String[] strs = datestr.split(":");
            if (strs[0].length() == 1) {
                str1 += "0" + strs[0];
            } else {
                str1 += strs[0];
            }
            if (strs[1].length() == 1) {
                str1 += ":0" + strs[1];
            } else {
                str1 += ":" + strs[1];
            }

        }
        return str1;
    }
    private String getDateString(String datestr) {

        String str1 = datestr;
        if (datestr != null) {
            str1 = "";
            String[] strs = datestr.split("-");
            str1 = strs[0];
            if (strs[1].length() == 1) {
                str1 += "-0" + strs[1];
            } else {
                str1 += "-" + strs[1];
            }
            if (strs[2].length() == 1) {
                str1 += "-0" + strs[2];
            } else {
                str1 += "-" + strs[2];
            }

        }
        return str1;
    }
	/**
	 * 获取自定义模块查询数据
	 * @return
	 */
	public String getOrderByPara(CustomerMenuConfigerPO po,
			                         HttpServletRequest request)throws Exception {
		
		StringBuffer sbf = new StringBuffer();
		if(po.getMenuDefQueryOrder()!=null&&
		   !"null".equals(po.getMenuDefQueryOrder()+"")&&
		   !"".equals(po.getMenuDefQueryOrder())){
			sbf.append(po.getMenuDefQueryOrder());
			//如果页面传的排序参数类似的话，会造成分页的时候数据交叉显示，2016-09-06 11.5补丁 start
			String tableName = new CustomDatabaseBD().getSingleTableName(po.getMenuListTableMap()+"");
			sbf.append(","+tableName+"_id desc ");
			//如果页面传的排序参数类似的话，会造成分页的时候数据交叉显示，2016-09-06 11.5补丁 end
		}else{
			String tableName = new CustomDatabaseBD().getSingleTableName(po.getMenuListTableMap()+"");			 
			sbf.append(" order by "+tableName+"_id desc ");
		}
		return sbf.toString();
	}
	private String escapeString(String str) {
        if (!"".equals(str)) {
            while (str.endsWith(" ")) {
                str = str.substring(0, str.length() - 1);
            } while (str.endsWith(",")) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }
	/*
     * 判断是否有维护权限
     * @param
     * @return boolean:true 有
     */
    public boolean hasUpdAndDelRight(HttpServletRequest request,
                                       String tabName,
                                       String infoId,
                                       String rightType,
                                       String defineOrgs,
                                       String domainId) {

        boolean retFlag = false;
        String curUserId = request.getSession(true).getAttribute("userId") + "";
        String orgIds = request.getSession(true).getAttribute("orgIdString") + "";
        String orgId = request.getSession(true).getAttribute("orgId") + "";

        DbOpt dbopt = null;
        String infoOrg = null;
        String infoEmp = null;
        String infoOrgidString = null;
        try {

            dbopt = new DbOpt();
            String[][] _str = dbopt.executeQueryToStrArr2("select tt.org_id,tt.emp_id from "+tabName
                 +" t,org_organization_user tt where t."+tabName+"_owner = tt.emp_id "+
                 " and t."+tabName+"_id = "+infoId);

            infoOrg = _str[0][0];
            infoEmp = _str[0][1];
            
            infoOrgidString=dbopt.executeQueryToStr("select orgidstring from org_organization  where org_id="+infoOrg);

            dbopt.close();

        } catch (Exception e) {
            System.out.println("CustomBizAction error on hasUpdAndDelRight:" +
                               e.getMessage());
        } finally {
            try {
                dbopt.close();
            } catch (SQLException ex) {}
        }

           //0:全部 1:本人 2:本组织及下属组织 3:本组织 4:自定义

           if ("0".equals(rightType)) {

               return true;

           } else if ("1".equals(rightType)) {

               if(infoEmp != null && curUserId.equals(infoEmp))
                   retFlag = true;
               else
                   retFlag = false;

               return retFlag;

           } else if ("2".equals(rightType)) {

               //if(infoOrg != null && (orgIds.indexOf("$"+infoOrg+"$")>-1))
        	   if(infoOrgidString != null && (infoOrgidString.indexOf(orgId)>-1))//11.5bug 2016/06/28  维护的时候不能维护下属组织的数据
                   retFlag = true; 
        	   else
                   retFlag = false;

               return retFlag;
           } else if ("3".equals(rightType)) {

               if(infoOrg != null && orgId.equals(infoOrg))
                   retFlag = true;
               else
                   retFlag = false;

               return retFlag;
           } else if ("4".equals(rightType)) {

               if(infoOrg != null && infoOrgidString != null &&
            		   hasDefineRight(defineOrgs,infoOrgidString,infoEmp))
                   retFlag = true;
               else
                   retFlag = false;

               return retFlag;
           }


        return retFlag;

    }

	private boolean hasDefineRight(String defineOrgs, 
			                       String infoOrgidString,
			                       String infoEmp) {

		boolean hasright = false;

		if (defineOrgs.indexOf("$" + infoEmp + "$") > -1) {
			return true;
		}

		defineOrgs = "*" + defineOrgs + "*";
		String[] rangeArray = defineOrgs.split("\\*\\*");
		int i = 0;
		for (i = 0; i < rangeArray.length; i++) {
			if (infoOrgidString.indexOf("$" + rangeArray[i] + "$") > -1) {
				return true;
			}
		}

		return hasright;

	}
    /**
     * 判断按钮权限
     * @param request HttpServletRequest
     * @param menuViewUser String
     * @param menuViewOrg String
     * @param menuViewGroup String
     * @return boolean
     */
    public boolean checkCustmenuButtonAuth(
                                     HttpServletRequest request,
                                     String menuViewUser,
                                     String menuViewOrg,
                                     String menuViewGroup){
         boolean flag = false;

         String curUserId = request.getSession(true).getAttribute("userId") + "";
         String orgIdS = request.getSession(true).getAttribute("orgIdString") + "";
         String orgId = request.getSession(true).getAttribute("orgId") + "";
         String domainId = request.getSession(true).getAttribute("domainId") + "";

         if ((menuViewUser != null &&
          menuViewUser.indexOf("$"+curUserId+"$") > -1) ||
         (menuViewOrg != null &&
          existsOrg(menuViewOrg, orgIdS)) ||
         (menuViewGroup != null &&
          existsGroup(menuViewGroup, curUserId,
                          domainId+""))) {
             flag =true;
          }
          if(!flag){
              DbOpt dbopt = null;
              try {

                  dbopt = new DbOpt();
                  String orgjz = dbopt.executeQueryToStr(
                          "select sidelineorg from org_employee where emp_id=" +
                          curUserId);

                  String[] orgjzs = null; //得到兼职组织
                  if (orgjz != null && !"".equals(orgjz)) {
                      String orgIdString = new ConversionString(orgjz).
                                           getOrgIdString();
                      orgjzs = orgIdString.split(",");
                  }
                  if (orgjzs != null) {
                      for (int i = 0; i < orgjzs.length; i++) {
                          if ((orgjzs[i] != null &&
                               existsOrg(orgjzs[i], orgId))) {
                              flag = true;
                              break;
                          }
                      }
                  }

              } catch (Exception e) {
                  e.printStackTrace();
              } finally {
                  try {
                      dbopt.close();
                  } catch (SQLException ex) {}
              }
          }
          return flag;
    }
    /**
     * 保存自定义按钮
     * @param menuId String
     * @param alist List
     * @return Boolean
     */
    public Boolean setCustButtons(String menuId,List alist){
		boolean retFlg = false;
		try {
			ParameterGenerator pg = new ParameterGenerator(2);
			pg.put(menuId, String.class);
			pg.put(alist, List.class);

			EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
			retFlg = ((Boolean) ejbProxy.invoke("setCustButtons", pg.getParameters())).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*************************************");
			logger.error("error to setCustButtons information :"
					+ e.getMessage());
			logger.error("*************************************");
		} finally {
			return retFlg;
		}
	}
    /**
     * 获取自定义按钮信息
     * @param menuId String
     *
     * @return List
     */
    public Boolean delCustButtons(String menuIds){
		boolean retFlg = false;
		try {
			ParameterGenerator pg = new ParameterGenerator(1);
			pg.put(menuIds, String.class);

			EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
			retFlg = ((Boolean) ejbProxy.invoke("delCustButtons", pg.getParameters())).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*************************************");
			logger.error("error to delCustButtons information :"
					+ e.getMessage());
			logger.error("*************************************");
		} finally {
			return retFlg;
		}
	}
    /**
     * 获取自定义按钮信息
     * @param menuId String
     *
     * @return List
     */
    public List getCustButtons(String menuId){
        List retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(menuId, "String");
            
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);

            retList = (List) ejbProxy.invoke("getCustButtons",
                                             pg.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getCustButtons information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
    /**
     * 保存字段控制
     * @param menuId String
     * @param alist List
     * @return Boolean
     */
    public Boolean setFieldControl(String menuId,List alist){
		boolean retFlg = false;
		try {
			ParameterGenerator pg = new ParameterGenerator(2);
			pg.put(menuId, String.class);
			pg.put(alist, List.class);

			EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
			retFlg = ((Boolean) ejbProxy.invoke("setFieldControl", pg.getParameters())).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*************************************");
			logger.error("error to setFieldControl information :"
					+ e.getMessage());
			logger.error("*************************************");
		} finally {
			return retFlg;
		}
	}
    /**
     * 删除字段控制
     * @param menuId String
     * @param alist List
     * @return Boolean
     */
    public Boolean delFieldControls(String menuIds){
		boolean retFlg = false;
		try {
			ParameterGenerator pg = new ParameterGenerator(1);
			pg.put(menuIds, String.class);

			EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
			retFlg = ((Boolean) ejbProxy.invoke("delFieldControls", pg.getParameters())).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*************************************");
			logger.error("error to delFieldControls information :"
					+ e.getMessage());
			logger.error("*************************************");
		} finally {
			return retFlg;
		}
	}
    /**
     * 获取字段控制信息
     * @param menuId String
     * @param controlType String 字段控制类型：0:源字段 1：读字段 2：写字段 3：隐藏字段
     * @return List
     */
    public List getFieldControls(String menuId,String controlType){
        List retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(menuId, "String");
            pg.put(controlType, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);

            retList = (List) ejbProxy.invoke("getFieldControls",
                                             pg.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getFieldControls information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
    /*
     * 根据字段ID判断字段的对应的搜索控件HTML代码
     * @param fieldId:字段ID
     */
    public String getQueryFieldHTML(String fieldId) {
        String html = "";
        String temp = "";
        String[] tempArr = null;
        String[][] fieldTemp = null;
        String type = "";
        Calendar now = Calendar.getInstance();

        DbOpt dbopt = null;

        try {
            dbopt = new DbOpt();
            fieldTemp = dbopt.executeQueryToStrArr2(
                    "select a.field_id,b.show_id,a.field_type,a.field_name, a.field_value from TSHOW b,TFIELD a where FIELD_SHOW=SHOW_ID and FIELD_ID=" +
                    fieldId, 5);
            if (fieldTemp != null && fieldTemp.length > 0) {
                boolean isNum = false;
                if (fieldTemp[0][1] != null &&
                    fieldTemp[0][1].trim().length() > 0) {
                    //判断字段类型
                    if (fieldTemp[0][2].equals("1000000") ||
                        fieldTemp[0][2].equals("1000001")) {
                        type = "<input type='hidden' name='" + fieldTemp[0][3] +"_search"+
                               "_type' value=\"number\">";
                        isNum = true;
                    } else {
                        type = "<input type='hidden' name='" + fieldTemp[0][3] +"_search"+
                               "_type' value=\"varchar\">";
                    }
                    switch (Integer.parseInt(fieldTemp[0][1])) {
                    case 103:
                        if (!isNum) {
                            type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search"+
                                   "_type' name='" + fieldTemp[0][3] +"_search"+
                                   "_type' value=\"radiovarchar\">";
                        } else {
                            type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search"+
                                   "_type' name='" + fieldTemp[0][3] +"_search"+
                                   "_type' value=\"radionumber\">";
                        }
                        html = "";
                        temp = dbopt.executeQueryToStr(
                                "select field_value from tfield where field_id=" +
                                fieldId);
                        if (temp == null || temp.trim().length() < 1) {
                            break;
                        }
                        tempArr = temp.split(";");
                        for (int i = 0; i < tempArr.length; i++) {
                            if (tempArr[i] != null &&
                                tempArr[i].trim().length() > 0 &&
                                tempArr[i].indexOf("/") > 0 &&
                                tempArr[i].indexOf("/") <
                                tempArr[i].length() - 1) {
                            	
                                html += "<input type='radio' id='" +
                                        fieldTemp[0][3] + "_search' name='" +
                                        fieldTemp[0][3] + "_search' value=\"" +
                                        tempArr[i].split("/")[0] + "\">" +
                                        tempArr[i].split("/")[1] + "&nbsp;";
                            } else if (tempArr[i] != null &&
                                    tempArr[i].trim().length() > 0) {
                            	
                                    html += "<input type='radio' id='" +
                                                    fieldTemp[0][3] +"_search' name='" +
                                                    fieldTemp[0][3] +"_search' value=\"" +
                                                    tempArr[i] + "\">" +
                                                    tempArr[i] + "&nbsp;";
                            }

                        }
                        break;
                    case 104:
                        type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search"+
                               "_type' name='" + fieldTemp[0][3] +"_search"+
                               "_type' value=\"checkbox\">";
                        html = "";
                        temp = dbopt.executeQueryToStr(
                                "select field_value from tfield where field_id=" +
                                fieldId);
                        if (temp == null || temp.trim().length() < 1) {
                            break;
                        }
                        tempArr = temp.split(";");
                        for (int i = 0; i < tempArr.length; i++) {
                            if (tempArr[i] != null &&
                                tempArr[i].trim().length() > 0 &&
                                tempArr[i].indexOf("/") >= 0 &&
                                tempArr[i].indexOf("/") <
                                tempArr[i].length() - 1) {
                            	
                                html += "<input type='checkbox' id='" +
                                        fieldTemp[0][3] + "_search' name='" +
                                        fieldTemp[0][3] + "_search' value=\"" +
                                        tempArr[i].split("/")[0] + "\">" +
                                        tempArr[i].split("/")[1] + "&nbsp;";
                            } else if (tempArr[i] != null &&
                                    tempArr[i].trim().length() > 0) {
                            	
                                    html += "<input type=checkbox id='"+
                                                    fieldTemp[0][3] + "_search' name='" +
                                                    fieldTemp[0][3] + "_search' value=\"" +
                                                    tempArr[i] + "\">" +
                                                    tempArr[i] + "&nbsp;";
                            }

                        }
                        break;
                    case 105:
                        if (isNum) {
                            type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search"+
                                   "_type' name='" + fieldTemp[0][3] +"_search"+
                                   "_type' value=\"selectnumber\">";
                        } else {
                            type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search_type' "+
                            	   " name='" + fieldTemp[0][3] +"_search_type' "+
                            	   " value=\"selectvarchar\">";
                        }
                        html = "<select id='" + fieldTemp[0][3] + "_search' name='" +
                               fieldTemp[0][3] +"_search"+
                               "'><option value=\"\">--请选择--</option>";
                        temp = dbopt.executeQueryToStr(
                                "select field_value from tfield where field_id=" +
                                fieldId);
                        if (temp == null || temp.trim().length() < 1) {
                            html += "</select>";
                            break;
                        }
                        if (temp.startsWith("@")) {
                            //表示从数据库中选择单选项
                            String table = temp.substring(temp.indexOf("][") +
                                    2, temp.length() - 1);
                            String[][] data = null;
                            //从数据库中提取数据
                            try {
                                data = dbopt.executeQueryToStrArr2("select " +
                                        table.substring(0, table.indexOf(".")) +
                                        "_id," +
                                        table.substring(table.indexOf(".") + 1,
                                        table.length()) + " from " +
                                        table.substring(0, table.indexOf(".")));
                            } catch (Exception e3) {}
                            //根据提取的数据将数据以单选类型显示出来
                            if (data != null) {
                                for (int i = 0; i < data.length; i++) {
                                    html += "<option value=" + data[i][0] + ">" +
                                            data[i][1] + "</option>";
                                }
                            }
                        }  else if (temp.startsWith("$@")) {
                                //表示从数据库中选择单选项
                                String table = temp.substring(temp.indexOf("][") + 2, temp.length() - 1);
                                String[][] data = null;
                                //从数据库中提取数据
                                try {
                                        String col=table.substring(table.indexOf(".") + 1,  table.length());
                                        data = dbopt.executeQueryToStrArr2("select " + table.substring(0, table.indexOf(".")) + "_id," + col + " from " + table.substring(0, table.indexOf("."))+ " order by "+col);
                                } catch (Exception e3) {}
                                //根据提取的数据将数据以单选类型显示出来
                                if (data != null) {
                                        for (int i = 0; i < data.length; i++) {
                                                html += "<option value=\"" + data[i][0] + "\">" + data[i][1] + "</option>";
                                        }
                                }

                        } else {
                            tempArr = temp.split(";");
                            for (int i = 0; i < tempArr.length; i++) {
                                if (tempArr[i] != null &&
                                    tempArr[i].trim().length() > 0 &&
                                    tempArr[i].indexOf("/") >= 0 &&
                                    tempArr[i].indexOf("/") <
                                    tempArr[i].length() - 1) {
                                	
                                    html += "<option value=\"" +
                                            tempArr[i].split("/")[0] + "\">" +
                                            tempArr[i].split("/")[1] +
                                            "</option>";
                                } else if (tempArr[i] != null &&
                                        tempArr[i].trim().length() > 0) {
                                	
                                        html += "<option value=\"" +
                                                        tempArr[i] + "\">" +
                                                        tempArr[i] +
                                                        "</option>";
                                }

                            }
                        }
                        html += "</select>";
                        break;
                    case 107:
                    	//日期
                        type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search_type' "+
                               " name='" + fieldTemp[0][3] +"_search_type' value=\"date\">";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        html = "<input type=\"text\" class=\"Wdate whir_datebox\" "+
                               " name=\""+fieldTemp[0][3]+"_search_start\" "+
                               " id=\""+fieldTemp[0][3]+"_search_start\"  "+
                               " value=\"\" "+
                               " onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,maxDate:'#F{$dp.$D(\\\'"+fieldTemp[0][3]+"_search_end\\\',{d:0});}'})\"/> ";
                        html += "&nbsp;至&nbsp;";
                        html += "<input type=\"text\" class=\"Wdate whir_datebox\" "+
                                " name=\""+fieldTemp[0][3]+"_search_end\" "+
                                " id=\""+fieldTemp[0][3]+"_search_end\" "+
                                " value=\"\"  "+
                                " onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd',readOnly:true,minDate:'#F{$dp.$D(\\\'"+fieldTemp[0][3]+"_search_start\\\',{d:0});}'})\"/>";
                        
                        break;
                    case 108:
                    	//时间
                        type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search_type' "+
                               " name='" + fieldTemp[0][3] +"_search_type' value=\"time\">";
                        sdf = new SimpleDateFormat("hh:mm");
                         html = "<input type=\"text\" class=\"Wdate whir_datetimebox\" "+
		                        " name=\""+fieldTemp[0][3]+"_search_start\" "+
		                        " id=\""+fieldTemp[0][3]+"_search_start\"  "+
		                        " value=\"\" "+
		                        " onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'#F{$dp.$D(\\\'"+fieldTemp[0][3]+"_search_end\\\',{d:0});}'})\"/> ";
		                 html += "&nbsp;至&nbsp;";
		                 html += "<input type=\"text\" class=\"Wdate whir_datetimebox\" "+
		                         " name=\""+fieldTemp[0][3]+"_search_end\" "+
		                         " id=\""+fieldTemp[0][3]+"_search_end\" "+
		                         " value=\"\"  "+
		                         " onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,minDate:'#F{$dp.$D(\\\'"+fieldTemp[0][3]+"_search_start\\\',{d:0});}'})\"/>";
                        break;
                    case 109:
                    	//日期时间
                        type = "<input type='hidden' id='" + fieldTemp[0][3] +"_search_type' "+
                               " name='" + fieldTemp[0][3] +"_search_type' value=\"datetime\">";
                        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        
                         html = "<input type=\"text\" class=\"Wdate whir_datetimebox\" "+
		                        " name=\""+fieldTemp[0][3]+"_search_start\" "+
		                        " id=\""+fieldTemp[0][3]+"_search_start\"  "+
		                        " value=\"\" "+
		                        " onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,maxDate:'#F{$dp.$D(\\\'"+fieldTemp[0][3]+"_search_end\\\',{d:0});}'})\"/> ";
		                 html += "&nbsp;至&nbsp;";
		                 html += "<input type=\"text\" class=\"Wdate whir_datetimebox\" "+
		                         " name=\""+fieldTemp[0][3]+"_search_end\" "+
		                         " id=\""+fieldTemp[0][3]+"_search_end\" "+
		                         " value=\"\"  "+
		                         " onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm',readOnly:true,minDate:'#F{$dp.$D(\\\'"+fieldTemp[0][3]+"_search_start\\\',{d:0});}'})\"/>";

                        break;
                    case 301:
                    	//金额
                        type = "<input type='hidden' name='" + fieldTemp[0][3] +"_search_type' "+
                               " value=\"float\">";
                        html = "<input type='text' id='" + fieldTemp[0][3] +"_search_begin'" + 
                               " onkeydown='checkNum(this);' " +
                               " name='" + fieldTemp[0][3] +"_search_begin' "+
                               " style='width:115px;' class=\"inputText\"/>";
                        html += "&nbsp;至&nbsp;";
                        html += "<input type='text' id='" + fieldTemp[0][3] +"_search_end'" + 
                                " onkeydown='checkNum(this);' name='" + fieldTemp[0][3] +"_search_end' "+
                                " style='width:115px;' class=\"inputText\"/>";
                        
                        break;

                    case 404: //单选弹出选择
                        html = "<input type='text' style='width:115px;' name='" +
                                fieldTemp[0][3] + "_search' id='" + fieldTemp[0][3] + "_search' class='inputText' readonly>";
                        html += "<a href=\"#\" onclick=\"choice(0,'" +fieldTemp[0][0]+ "','" +
                                fieldTemp[0][4]+ "','" +fieldTemp[0][3]+"_search" +
                                "',event)\">选择</a>";

                        break;
                    case 405: //多选弹出选择
                        html = "<input type='text' style='width:115px;' name='" +
                                fieldTemp[0][3] + "_search' id='" + fieldTemp[0][3] + "_search' class='inputText' readonly>";
                        html += "<a href=\"#\" onclick=\"choice(1,'" +fieldTemp[0][0]+ "','" +
                                fieldTemp[0][4] + "','" + fieldTemp[0][3]+"_search" +
                                "',event)\">选择</a>";

                        break;

                    default:
                        html = "<input type=\"text\" id=\"" + fieldTemp[0][3] +"_search"+
                               "\"" +
                               (isNum ? "onkeydown=\"checkNum(this)\"" : "") +
                               " name=\"" + fieldTemp[0][3] +"_search"+
                               "\" class=\"inputText\"/>";
                        break;
                    }

                    //浮点型的 增加介值查询
                    if ( //fieldTemp[0][2].equals("1000000") ||
                            fieldTemp[0][2].equals("1000001")) {
                        type = "<input type='hidden' name='" + fieldTemp[0][3] +"_search"+
                               "_type' value=\"float\">";
                        html = "<input type='text' id='"+fieldTemp[0][3]+"_search_begin' "+
                               " onkeydown='checkNum(this);' " +
                               " name='" + fieldTemp[0][3] +"_search_begin' "+
                               " style='width:115px;' class=\"inputText\"/>";
                        html += "&nbsp;至&nbsp;";
                        html += "<input type='text' id='" + fieldTemp[0][3] +"_search_end' "+
                                " onkeydown='checkNum(this);' " +
                                " name='" + fieldTemp[0][3] +"_search_end' "+
                                " style='width:115px;' class=\"inputText\"/>";
                    }else if( fieldTemp[0][2].equals("1000000")) {
                        type = "<input type='hidden' name='"+fieldTemp[0][3] +"_search"+
                               "_type' value=\"integer\">";
                        html = "<input type='text' id='" + fieldTemp[0][3] +"_search_begin' "+
                               " onkeydown='checkNum(this);' " +
                               " name='" + fieldTemp[0][3] +"_search"+
                               "_begin' style='width:115px;' class=\"inputText\"/>";
                        html += "&nbsp;至&nbsp;";
                        html += "<input type='text' id='" + fieldTemp[0][3] +"_search_end' + " +
                        		" onkeydown='checkNum(this);' " +
                                " name='" + fieldTemp[0][3] +"_search_end' "+
                                " style='width:115px;' class=\"inputText\"/>";
                    }


                }
            }
        } catch (Exception e) {
            //System.out.print("------------------------------------------------");
            System.out.print(
                    "^^^^^^^^^^^^^^^^^^EJB:getQueryFieldHTML wrong^^^^^^^^^^^^^^^^^^^^^^^^");
            e.printStackTrace();
            //System.out.print("------------------------------------------------");
        } finally {
            try {
                dbopt.close();
            } catch (SQLException ex) {
            }

            //System.out.print("\n-----------------------\n"+type+html+"\n-------------------------\n");

        }
        return type + html;
    }
    /**
     * 频道增加自定义模块接口
     * @param channelName
     * @param menuscope
     * @param channelReader
     * @param menuvieworg
     * @param menuviewgroup
     * @param createdemp
     * @param createdorg
     * @param resultId
     * @param leftURL
     * @param rightURL
     * @param domainId
     * @param channelType
     * @return
     * @throws Exception
     */
    public Long addUserChannel(String channelName,
				            String menuscope,
				            String channelReader,
				            String menuvieworg,
				            String menuviewgroup,
				            String createdemp,
				            String createdorg,
				            String resultId,
				            String leftURL,
				            String rightURL,
				            String domainId,
				            String channelType){
        Long retFlg = new Long(0);
        try {
          ParameterGenerator pg = new ParameterGenerator(12);
          pg.put(channelName, String.class);
          pg.put(menuscope, String.class);
          pg.put(channelReader, String.class);
          pg.put(menuvieworg, String.class);
          pg.put(menuviewgroup, String.class);
          pg.put(createdemp, String.class);
          pg.put(createdorg, String.class);
          pg.put(resultId, String.class);
          pg.put(leftURL, String.class);
          pg.put(rightURL, String.class);
          pg.put(domainId, String.class);
          pg.put(channelType, String.class);

          EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                  "CustomMenuEJBLocal", CustomMenuEJBHome.class);
          retFlg = (Long) ejbProxy.invoke("addUserChannel",pg.getParameters());
        }
        catch (Exception e) {
          e.printStackTrace();
          logger.error("*************************************");
          logger.error("error to addUserChannel information :" + e.getMessage());
          logger.error("*************************************");
        }
        finally {
          return retFlg;
        }
      }
    /**
     * 频道修改自定义模块接口
     * @param channelId
     * @param channelName
     * @param menuScope
     * @param channelReader
     * @param channelReadOrg
     * @param channelReadGroup
     * @param channelType
     * @return
     * @throws Exception
     */
    public boolean updateUserChannel(String channelId, 
						            String channelName,
						            String menuScope, 
						            String channelReader, 
						            String channelReadOrg,
						            String channelReadGroup, 
						            String channelType){
        boolean flag = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(7);
            pg.put(channelId, String.class);
            pg.put(channelName, String.class);
            pg.put(menuScope, String.class);
            pg.put(channelReader, String.class);
            pg.put(channelReadOrg, String.class);
            pg.put(channelReadGroup, String.class);
            pg.put(channelType, String.class);
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            flag = ((Boolean) ejbProxy.invoke("updateUserChannel",
                                              pg.getParameters())).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to updateUserChannel information :" +e.getMessage());
            logger.error("*************************************");
            throw e;
        } finally {
            return flag;
        }

    }
    /**
     * 频道删除自定义模块接口
     * @param domainId
     * @param channelId
     * @param channelType
     * @return
     * @throws HibernateException
     */
    public boolean delChannelCustmizeMenus(String domainId, 
            String[] channelId,
            String[] channelType){
        boolean flag = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(domainId, String.class);
            pg.put(channelId, String[].class);
            pg.put(channelType, String[].class);
            
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            flag = ((Boolean) ejbProxy.invoke("delChannelCustmizeMenus",
                                              pg.getParameters())).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to delChannelCustmizeMenus information :" +e.getMessage());
            logger.error("*************************************");
            throw e;
        } finally {
            return flag;
        }

    }
    /**
     * 获取字段信息
     * @param fieldId
     * @return
     * @throws HibernateException
     */
    public List getFieldInfoById(String fieldId){
        List retList = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(fieldId, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retList = (List) ejbProxy.invoke("getFieldInfoById",pg.getParameters());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getFieldInfoById information :" +
                         e.getMessage());
            logger.error("*************************************");
        } finally {
            return retList;
        }
    }
    /**
     * 保存列表字段
     * @param menuId String
     * @param alist List
     * @return Boolean
     */
    public Boolean saveListFields(HttpServletRequest request) {
    	boolean flag = false;
//    	System.out.println("-------------------------saveListFields-----------------------------");
    	
        //获取列表保存字段
        List list2 = getFieldControls(request.getParameter("menuId")+"","4");
        String tableName = request.getParameter("tableName");
        String fieldname = "";
        String fieldType = "";
        String fieldShow = "";
        String[] infoIds = request.getParameterValues("infoId");
        String[] fieldvalues = null;
        if(list2 != null){
           for(int i=0;i<list2.size();i++){
                List _list = (List)list2.get(i);
                fieldname = (String)_list.get(1);
                fieldType = (String)_list.get(3);
                fieldShow = (String)_list.get(4);
                
//                System.out.println("fieldname:"+fieldname);
//                System.out.println("fieldType:"+fieldType);
//                System.out.println("fieldShow:"+fieldShow);

                if ((com.whir.component.util.Field.FIELD_SIMPLE_SELECTPERSON+"").equals(fieldShow)) {

                    fieldvalues = new String[infoIds.length];
                    for(int j=0;j<infoIds.length;j++){
                        if(request.getParameter(fieldname+"_Name"+j)==null || 
                        		"".equals(request.getParameter(fieldname+"_Name"+j))){
                            fieldvalues[j] = "";
                        }else{
                            fieldvalues[j] = request.getParameter(fieldname+"_Name"+j)+";"+
                                             request.getParameter(fieldname+"_Id"+j);
                        }
                    }
                }else if ((com.whir.component.util.Field.FIELD_DATE+"").equals(fieldShow)||
                		(com.whir.component.util.Field.FIELD_DATETIME+"").equals(fieldShow)) {
                	fieldvalues = new String[infoIds.length];
                    for(int j=0;j<infoIds.length;j++){
                        if(request.getParameter(fieldname+j)==null || 
                        		"".equals(request.getParameter(fieldname+j))){
                            fieldvalues[j] = "";
                        }else{
                            fieldvalues[j] = request.getParameter(fieldname+j);
                        }
                    }
                }else{
                    fieldvalues = request.getParameterValues(fieldname);
                }
//                System.out.println("fieldname:"+fieldname);
//                System.out.println("fieldvalues.length:"+fieldvalues.length);
//                for(int k=0;k<fieldvalues.length;k++){
//                	System.out.println("fieldvalues:"+fieldvalues[k]);
//                }
                saveListField(tableName,
	                          fieldname,
	                          fieldType,
	                          infoIds,
	                          fieldvalues);
           }
           flag = true;
        }
        
        return flag;
    }
    private Boolean saveListField(String tableName,
    		                      String fieldname,
    		                      String fieldType,
    		                      String[] infoIds,
    		                      String[] fieldvalues) {
    	
    	boolean flag = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(5);
            pg.put(tableName, String.class);
            pg.put(fieldname, String.class);
            pg.put(fieldType, String.class);
            pg.put(infoIds, String[].class);
            pg.put(fieldvalues, String[].class);
            
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            flag = ((Boolean) ejbProxy.invoke("saveListFields",
                                              pg.getParameters())).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to saveListFields information :" +e.getMessage());
            logger.error("*************************************");
            throw e;
        } finally {
            return flag;
        }
        
    }
    
    /**
     * 找出要删除的附件
     * @param tableName
     * @param delId
     * @return
     */
    public String[] getFiles(String tableName,String delId){

        DbOpt dbopt = null;
        String[] files = null;
        try {
            dbopt = new DbOpt();
            //删除附件
            String[] fname = dbopt.executeQueryToStrArr1(
                    "select t.field_name from tfield t,ttable tt " +
                    " where tt.table_name='" + tableName +
                    "' and t.field_table=tt.table_id and t.field_show=" +com.whir.component.util.Field.FIELD_UP_FILE);
            String files1 = "";
            if (fname != null) {
                for (int i = 0; i < fname.length; i++) {
                    String[] str1 = dbopt.executeQueryToStrArr1("select " +
                            fname[i] +" from " + tableName +
                            " where " + tableName + "_id in (" +delId+")");
                    for (int j = 0; j < str1.length; j++) {
                        String[] str2 = str1[j].split(";");
                        files1 = files1 + str2[0] + ",";
                    }
                }
            }
            if (!"".equals(files1)) {
                files = files1.split(",");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch (SQLException ex) {}
        }

        return files;

    }
    /**
     * 删除数据
     * @param tableName
     * @param recordId
     * @return
     */
    public boolean deleteBizDatas(String tableName, String recordId){

        boolean retFlg = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(tableName, "String");
            pg.put(recordId, "String");
            EJBProxy ejbProxy = new CustomerMenuEJBProxy("CustomMenuEJB",
                    "CustomMenuEJBLocal", CustomMenuEJBHome.class);
            retFlg = ((Boolean) ejbProxy.invoke("deleteBizDatas",
                                                pg.getParameters())).
                     booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to deleteBizDatas information :" + e.getMessage());
            logger.error("*************************************");
        } finally {
            return retFlg;
        }
    }
    /**
     * 获取导出字段设置
     * @param po
     * @param request
     * @return
     */
    public List getExpSelectPara(CustomerMenuConfigerPO po,
                                 HttpServletRequest request) throws Exception{
    	System.out.println("-----------------------------getExpSelectPara---------------------------");
    	System.out.println("po.getMenuSearchBound():"+po.getMenuSearchBound());
    	System.out.println("po.getMenuListExpDisplayElements():"+po.getMenuListExpDisplayElements());
		List res = new ArrayList();
		String qlFieldids = "";
		String searchBound = po.getMenuSearchBound()+"";
		String expelements = po.getMenuListExpDisplayElements();
		boolean hasNewForm=false;
		//新表单
		if(searchBound.indexOf("new$")!=-1){
			hasNewForm=true;
			searchBound = searchBound.replaceAll("new\\$","");
		}
		
		String[] qlFieldidss = null;

		DbOpt dbopt = null;
		try {

			dbopt = new DbOpt();

			if (expelements!=null&&
				!"".equals(expelements)&& 
				!"null".equals(expelements)&& 
				 !"default".equals(expelements)) {
				
				String Sql2 = "select ql_field from oa_custmenu_qlcase where id="+ expelements;
//				System.out.println("Sql2:"+Sql2);
				qlFieldids = dbopt.executeQueryToStr(Sql2);
			} else if (expelements==null||
					   "null".equals(expelements)||
					   "".equals(expelements)||
					   "default".equals(expelements)) {
				if(hasNewForm){
					com.whir.ezoffice.ezform.service.FormService service = 
						     new com.whir.ezoffice.ezform.service.FormService();
//					String[][] result1 = service.getFormAllFields(searchBound);
					String[][] result1 = service.getFormMainFields(searchBound);
//					System.out.println("searchBound:"+searchBound);
					if (result1 != null && result1.length > 0) {
						for (int i = 0; i < result1.length; i++) {
							qlFieldids += result1[i][0]+ ",";
						}
					}
//					System.out.println("qlFieldids:"+qlFieldids);
				}else{
					String Sql3 = "select t1.elt_name from telt t1,tArea t2 "+
					" where t1.area_id=t2.area_id and t2.area_name='form1' and t1.page_id="+searchBound;
//					System.out.println("Sql3:"+Sql3);
					String[][] result1 = dbopt.executeQueryToStrArr2(Sql3, 1);
					if (result1 != null && result1.length > 0) {
						for (int i = 0; i < result1.length; i++) {
							qlFieldids += result1[i][0].substring(0,result1[i][0].indexOf("-whir"))+ ",";
						}
					}
//					System.out.println("qlFieldids:"+qlFieldids);
				}
			}
			
			if(qlFieldids.endsWith(",")){
                qlFieldids = qlFieldids.substring(0, qlFieldids.length()-1);                
            }
            
            if(!"".equals(qlFieldids)){
                qlFieldidss = dbopt.executeQueryToStrArr1("select a.field_id from tfield a where a.field_id in ("+qlFieldids+") order by a.field_SEQUENCE asc, a.field_id");
            }
            
			dbopt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbopt.close();
			} catch (SQLException ex) {
			}
		}
		//String[] qlFieldidss = qlFieldids.split(",");
		if(qlFieldidss!=null){
			for(int i=0;i<qlFieldidss.length;i++){
				res.add(getFieldInfoById(qlFieldidss[i]));
			}
		}
		return res;
	}
    /**
     * 获取导出数据
     * @param expSelectPara
     * @param selectPara
     * @param fromPara
     * @param wherePara
     * @param orderByPara
     * @return
     */
    public List getExpDataList(List expSelectPara, 
    		                       String selectPara,
			                       String fromPara, 
			                       String wherePara, 
			                       String orderByPara) {

    	String[][] res =null;
		DbOpt dbopt = null;
		String Sql = "select "+selectPara+" from "+fromPara+" "+
		             wherePara+" "+orderByPara;
		try {
			dbopt = new DbOpt();
			res = dbopt.executeQueryToStrArr2(Sql,expSelectPara.size());
			dbopt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbopt.close();
			} catch (SQLException ex) {
			}
		}
		List list = new ArrayList();
		if(res!=null){
			for(int i=0;i<res.length;i++){
				String[] _d = new String[res[i].length];
				for(int j=0;j<res[i].length;j++){
					List _pama = (List) expSelectPara.get(j);
					_d[j] = getExpShowTEXT(res[i][j],
							       _pama.get(7)+"",
							       _pama.get(1)+"",
							       _pama.get(5)+"",
							       _pama.get(3)+"");
					
				}
				list.add(_d);
			}
		}

		return list;

	}
    /**
     * 获取导出数据显示
     * @param dataValue
     * @param fieldid
     * @param fieldname
     * @param fieldtype
     * @param fieldshow
     * @return
     */
    public String getExpShowTEXT(String dataValue,
    		                     String fieldid,
    		                     String fieldname,
    		                     String fieldtype,
    		                     String fieldshow) {
//    	logger.debug("---------------------getExpShowTEXT-------------------");
//    	logger.debug("dataValue:"+dataValue);
//    	logger.debug("fieldid:"+fieldid);
//    	logger.debug("fieldname:"+fieldname);
//    	logger.debug("fieldtype:"+fieldtype);
//    	logger.debug("fieldshow:"+fieldshow);
    	
		String json = dataValue;
		String value = (dataValue != null &&
		          dataValue.length() > 0 &&
		          dataValue.indexOf(";") > 0)?
		        		  dataValue.substring(0,dataValue.indexOf(";")):dataValue;
		value = (value==null||"null".equals(value)||"".equals(value))?"":value;
		if("1000001".equals(fieldtype)){
			value = new CustomerMenuDB().getFloatString(value);
		}

		if ( ((com.whir.component.util.Field.FIELD_RADIO+"").equals(fieldshow) ||
                (com.whir.component.util.Field.FIELD_CHECKBOX+"").equals(fieldshow) ||
                (com.whir.component.util.Field.FIELD_SELECT+"").equals(fieldshow))
               && dataValue != null
               && dataValue.length() > 0) { //类型RADIO, CHECKBOX SELECT
			
			 String linkurl = new CustomFormBD().getFieldShowValue(fieldname,
                                                                    fieldshow,
                                                                    dataValue,
                                                                    fieldid) + "";
			 json = linkurl;
          } else if ( (com.whir.component.util.Field.FIELD_PASSWORD+"").equals(fieldshow)&& 
        		  dataValue!= null && 
        		  dataValue.length() > 0) {                         //密码
              json = "******";
          } else if ( (com.whir.component.util.Field.FIELD_UP_FILE+"").equals(fieldshow) &&
        		        dataValue != null &&
        		        dataValue.length() > 0) {                   //附件
        	  
              String tempStr = ((dataValue!= null&&
            		             dataValue.length() > 0 && 
            		             dataValue.indexOf(";") > 0) ? dataValue.split(";")[1].replaceAll(",,",""):"");
              String fileName = ((dataValue != null && 
            		              dataValue.length() > 0 && 
            		              dataValue.indexOf(";") > 0) ? dataValue.split(";")[0].replaceAll(",",""):"");
              String path = "customform";
              while(tempStr.length()>0 && tempStr.endsWith(",")){
                  tempStr = tempStr.substring(0,tempStr.length()-1);
              }
              json = tempStr;
         } else if ( (com.whir.component.util.Field.FIELD_SIMPLE_SELECTPERSON+"").equals(fieldshow)) { //单选人(全部)
        	 
            String simpleName = "";
            String simpleId = "";
            if(dataValue != null &&
            	dataValue.length() > 0 &&
            	dataValue.indexOf(";") > 0){
                simpleName = dataValue.substring(0,dataValue.indexOf(";"));
                simpleId = dataValue.substring(dataValue.indexOf(";")+1,dataValue.length());
            }
            json =simpleName;
         } else {	 
        	 json = value;
         }

		return json;
	}
    
    /**
     * 获取合计字段
     * @param tableId
     * @param request
     * @return
     */
    public String getTotleFields(String tableId,
    		                   HttpServletRequest request,
    		                   String fromPara,
    		                   String wherePara){
//    	System.out.println("-------------------------getTotleFields-----------------------------");
        String i18N_sum = Resource.getValue(LocaleUtils.getLocale(request),"common","comm.sum");
        
        String list = "";
        DbOpt dbopt = null;
        try {
            dbopt = new DbOpt();
            java.sql.ResultSet rs = dbopt.executeQuery(
                "SELECT TABLE_NAME, TABLE_TOTFIELD FROM TTABLE WHERE TABLE_ID=" +
                tableId +
                " AND (TABLE_TOTFIELD IS NOT NULL OR TABLE_TOTFIELD<>'')");
            String tableName = "", totField = "";
            if(rs.next()) {
                tableName = rs.getString(1);
                totField = rs.getString(2);
            }
            rs.close();
            
            if(!tableName.equals("") && !totField.equals("")) {
                if(totField.endsWith(",")) {
                    totField = totField.substring(0, totField.length() - 1);
                }
                
                String[] tmp = {totField};
                if(totField.indexOf(",") >= 0) {
                    tmp = totField.split(",");
                }
                
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < tmp.length; i++) {
                	
                    String[][] fieldInfo = dbopt.executeQueryToStrArr2(
                        "SELECT FIELD_DESNAME, field_show ,FIELD_DECNUM FROM TFIELD WHERE FIELD_NAME='" +
                        tmp[i] + "' and field_table=" + tableId, 3);

                    sb.append(fieldInfo[0][0]);
                    sb.append(i18N_sum);
                    
                    String sumVal = dbopt.executeQueryToStr("SELECT SUM(" +
                        tmp[i] +") FROM " + fromPara + wherePara);

                    if(("301".equals(fieldInfo[0][1]))||(fieldInfo[0][2]!=null&&!"null".equals(fieldInfo[0][2]))) { //显示方式：金额，使用千位符
                        if(sumVal != null) {
                            try {
                                sumVal = java.text.DecimalFormat.getInstance().
                                    format(new java.math.BigDecimal(sumVal).setScale(2, BigDecimal.ROUND_HALF_UP));
                            } catch(Exception e) {
                            }
                        }
                    }
                  
//                    System.out.println("sumVal:" + sumVal);
                    sb.append(sumVal);
                    sb.append("&nbsp;&nbsp;");
                }
                list = sb.toString();
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }
    /**
     * 浮点型数据处理
     * @param val
     * @return
     */
    public String getFloatString(String val){
    	logger.debug("-----------------------getFloatString-------------------------");
    	logger.debug("val:"+val);
    	
    	String value = val;
    	try {
  	      if (!"".equals(value)&&value.startsWith(".")) {
  	    	  value="0"+value;
  	      }
  	      if ((!"".equals(value))&&(value.endsWith(".0"))&&(value.length()>=2)) {
		      value = value.substring(0,value.length()-2);
		  }
  	      if(!"".equals(value)&&value.indexOf(".")>-1){
            String ev = value.substring(value.indexOf("."),value.length());
            if(ev.length()==1){
              value = value+"0";
            }
            if(ev.length()==0){
              value = value+"00";
            }

          }else if(!"".equals(value)){
            value = value+".00";
          }
  	    
          if (!"".equals(value)&&
      	       value.indexOf("e")>-1) {
            value = value.toLowerCase();
            String value1 = value.substring(0,value.indexOf('e'));
            String value2 = value.substring(value.indexOf('e') + 1, value.length());
            double value11 = Double.parseDouble(value1);
            double value22 = Double.parseDouble(value2);

            double laterValue = value11 * Math.pow(10, value22);
            value = java.text.DecimalFormat.getInstance().format(laterValue).replaceAll(",","");
          }
          if (!"".equals(value)&&
          	       value.indexOf("E")>-1) {
                value = value.toLowerCase();
                String value1 = value.substring(0,value.indexOf('E'));
                String value2 = value.substring(value.indexOf('E') + 1, value.length());
                double value11 = Double.parseDouble(value1);
                double value22 = Double.parseDouble(value2);

                double laterValue = value11 * Math.pow(10, value22);
                value = java.text.DecimalFormat.getInstance().format(laterValue).replaceAll(",","");
          }

        } catch (Exception e) {
      	  e.printStackTrace();
        }
    	
    	return value;
    	
    }
    /**
     * 获取符合条件的id串   2016-01-26
     * @param name
     * @param menuLevel
     * @param menuLevelSet
     * @return
     */
    public String getMobileId(String name,String menuLevel,String menuLevelSet) {
    	String ids = "";
    	CustomMenuEJBBean customMenuEJBBean=new CustomMenuEJBBean();
        try {       	
        	//ids = customMenuEJBBean.getMobileId(name, menuLevel, menuLevelSet);
        	ids = customMenuEJBBean.getMobileId_new(name, menuLevel, menuLevelSet);//安全性修改的 2016/05/03
        } catch (Exception e) {
            e.printStackTrace();            
            logger.error("error to getMobileId information :" + e.getMessage());
        }  
        return ids;
    }
    /**
     * 修改自定义模块的同时修改自定义流程频道   2016-03-22 11.5.0.0需求
     * @param wFChannelPO
     * @param id
     * @return
     */
    public Long updateWFChannel(WFChannelPO wFChannelPO,Long id) {
    	Long result = new Long(-1L);
    	WFChannelEJBBean wFChannelEJBBean=new WFChannelEJBBean();
        try {       	
        	result = wFChannelEJBBean.updateWFChannelPO(wFChannelPO,id);
        } catch (Exception e) {
            e.printStackTrace();            
            logger.error("error to updateWFChannel information :" + e.getMessage());
        }  
        //logger.debug("---------------------result:"+result);
        return result;
    }
    
    public boolean deleteWorkLogByIds(String ids,String tableName) {
    	boolean result = false;
    	CustomMenuEJBBean customMenuEJBBean=new CustomMenuEJBBean();
        try {       	
        	result = customMenuEJBBean.deleteWorkLogByIds(ids,tableName);
        } catch (Exception e) {
            e.printStackTrace();            
            logger.error("error to deleteWorkLogByIds information :" + e.getMessage());
        }  
        //logger.debug("---------------------result:"+result);
        return result;
    }
    /**
     * 获取新自定义表单的字段联动列表  11.5需求  2016/04/11
     * @param formId
     * @return
     */
    public String[][] getRelaTrigByFormId(String formId){
    CustomMenuEJBBean customMenuEJBBean=new CustomMenuEJBBean();
    String[][] result=null;
	try {
		result = customMenuEJBBean.getRelaTrigByFormId(formId);
	} catch (Exception e) {
		logger.error("error to getRelaTrigByFormId information :" + e.getMessage());
		e.printStackTrace();
	}
      return result;
    }
    
    /**
     * 保存方案 add by tianml 11.5版本  新需求(门户保存方案时候调用)
     * @param domainId  域id
     * @param tblId  数据表id
     * @param qlFields 列表选择的字段
     * @param caseName 列表："portal_list",连接："portal_link",提醒："portal_remind"
     * @param casetype 列表：11,连接：12,提醒：13
     * @return
     */
	public Long saveQLCaseSetNew(String domainId,String tblId,String qlFields,String caseName,String casetype) {		 
		//tblId = tblId.split("\\|")[0];		 
		CustomerMenuQLCasePO po = new CustomerMenuQLCasePO();
		//po.setId(fieldId==null?null:new Long(fieldId));
		po.setMenuId(Long.valueOf(tblId));
		po.setQlFields(qlFields);
		po.setDomainId(Long.valueOf(domainId));
		po.setMenuCaseName(caseName);
		po.setCaseType(Integer.valueOf(casetype));
		Long caseId = new CustMenuWithOriginalBD().saveQLCaseSet(po);
		return caseId;
	}
	/**
	 * 删除方案add by tianml 11.5版本  新需求(门户删除方案时候调用)
	 * @param caseId
	 * @return
	 */
	public Boolean delQLCaseSet(String caseId) {
		Boolean res = new CustMenuWithOriginalBD().delQLCaseSet(caseId);
		return res;
	}
	/**
	 * 获取方案内容add by tianml 11.5版本  新需求(门户获取方案内容时候调用)
	 * @param caseId
	 * @param domainId
	 * @return
	 */
	public String getQueryShowFieldsByCase(String caseId,String domainId) {
	 
		String[][] fields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(caseId,domainId);
		String json = "";
		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				String[] obj = fields[i];
				json += "{\"id\":\"" + obj[0] + "\", \"text\":\"" +obj[1]+ "\"},";
			}
			json = "[" + escapeString(json) + "]";
		}
		//System.out.println("----json:" + json);
		return json;
	}
	
	 /**
     * 提取运营域中的所有自下定义菜单对象（门户使用的,获取启动信息项模块列表 2016-04-27 11.5需求）
     * @param domainId  域id
     * @param curUserId  当前用户id
     * @param orgIdString  当前用户组织id串
     * @return
     */
    public List getCustomMenuList_portal(String domainId,String curUserId, String orgIdString) {
        List retList = null;
        CustomMenuEJBBean customMenuEJBBean=new CustomMenuEJBBean();
        try {
        	retList=customMenuEJBBean.getCustomMenuList_portal(domainId, curUserId, orgIdString);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("*************************************");
            logger.error("error to getCustomMenuList_portal information :" +e.getMessage());
            logger.error("*************************************");
        } 
        return retList;
    }
   
    /**
     * 获取列表数据  自定义门户  11.5需求  2016-04-28
     * @param menuId  模块id
     * @param dominId 域id
     * @param isRefFlow  goRightMenu_portal map方法调用的值
     * @param isNewRefFlow goRightMenu_portal map方法调用的值
     * @param rightType goRightMenu_portal map方法调用的值
     * @param defineOrgs goRightMenu_portal map方法调用的值
     * @param days  提前提醒天数
     * @param counts 信息条数
     * @param reminFieldId 提醒字段名称
     * @param linkFieldId  连接字段名称
     * @param showListFieldId  列表方案id
     * @param request
     * @return
     * @throws Exception
     */
    public String getCustDataList_portal(String menuId,String dominId,String isRefFlow,String isNewRefFlow,String rightType,String defineOrgs,int days,int counts,String reminFieldId,String linkFieldId,String showListFieldId,HttpServletRequest request) throws Exception {
    	
    	logger.debug("------------------------getCustDataList_portal-------------------------");
    	//String portletSettingId=request.getParameter("portletSettingId");
    	CustomDatabaseBD dbBD = new CustomDatabaseBD();
    	CustomerMenuDB cDB = new CustomerMenuDB();
        CustomerMenuConfigerPO po = new CustMenuWithOriginalBD().loadMenuSetById(menuId,dominId);
        //String reminField=po.getPortalRemindField();//提醒字段
        String tableId = po.getMenuListTableMap() + "";//自定义数据表id
		String tableName = dbBD.getSingleTableName(tableId);         
        String formId=po.getMenuSearchBound();//自定义表单的id
        
        boolean hasNewForm = false;
        if((po.getMenuSearchBound()+"").indexOf("new$")!=-1){
        	hasNewForm = true;
        }
        String selectPara = cDB.getSelectPara_portal(po,showListFieldId,request);//自己重新写的
        String fromPara = " "+tableName+" ";
        String wherePara = cDB.getWherePara(po,request);
        String orderByPara = " " + cDB.getOrderByPara(po,request);
      
        //自定义数据表中设置A字段列表中不显示，自定义模块中设置A字段为排序字段，此自定义模块中不显示数据，后台报错。
        String sortField = "";
        if(!CommonUtils.isEmpty(orderByPara)){
            try{
                String temp_ = orderByPara.trim();
                temp_ = temp_.replaceAll("  ", " ");
                temp_ = temp_.substring(temp_.toLowerCase().indexOf("order by ") + 9);
                temp_ = temp_.trim();
                sortField = temp_.split(" ")[0];
            }catch(Exception e){
                e.printStackTrace();
            }
            if(!CommonUtils.isEmpty(sortField)){
                if(selectPara.toLowerCase().indexOf(sortField.toLowerCase()) == -1){
                    selectPara += "," + sortField;
                }
            }
        }
        
       /* if("true".equals(isRefFlow)){
        	if("true".equals(isNewRefFlow)){
        		logger.debug("-------------------isNewRefFlow----------------------"+isNewRefFlow); 
        		formId = formId.replaceAll("new\\$","");
        		selectPara += ",wfwork.workcurstep,wfwork.workstatus";
            	fromPara += " left join (select hip.whir_dealing_activity workcurstep,hip.whir_status workstatus,hip.business_key_ keyid "+
            	   " from ez_flow_hi_procinst hip,ez_form  f "+
            	   " where  f.form_code=hip.whir_formkey and f.form_id="+formId+") wfwork on "+tableName+"."+tableName+"_id=wfwork.keyid ";
            	wherePara += " and (wfwork.workstatus=1 or wfwork.workstatus=100) ";
        	}else{
        	  logger.debug("-------------------tableName----------------------"+tableName);
        	  selectPara += ",workcurstep,workstatus";
        	  fromPara += " left join wf_work wfwork on "+tableName+"."+tableName+"_id=wfwork.workrecord_id ";
        	  wherePara += " and (wfwork.workstatus=1 or wfwork.workstatus=100) ";
        	}
        }*/
        String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();
        if(StringUtils.isNotEmpty(reminFieldId)){
        	 Date currentd=new Date();
        	 //Date d=getDateBefore(currentd,days);
        	 Date d=getDateAfter(currentd,days);
        	 SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        	 String time=formatter.format(d);             
        	 wherePara += " and "+reminFieldId+" <= '"+time+"' ";
        	 
        	/* if (databaseType.indexOf("mysql") >= 0) {
             	wherePara += " and "+reminFieldId+" <= '"+time+"' ";
     		 } else {
     			wherePara += " and "+reminFieldId+" <= EZOFFICE.FN_STRTODATE('"+time+"','L') ";
     		}*/
          }
        
       
          logger.debug("--------------------------portal search---------------------------------");           
          logger.debug("-------------------selectPara- portal---------------------"+selectPara);
          logger.debug("-------------------fromPara----portal------------------"+fromPara);
          logger.debug("-------------------wherePara------portal----------------"+wherePara);
          logger.debug("-------------------orderByPara-----portal-----------------"+orderByPara);
 
        //String totFileds = cDB.getTotleFields(tableId,request, fromPara, wherePara);       
        //logger.debug("-------------------totFileds----------------------"+totFileds);
        Page page = PageFactory.getJdbcPage(selectPara, fromPara,wherePara,orderByPara);
        //page.setPageSize(counts);
		//page.setCurrentPage(0);
        List list = page.getResultList();
       
        logger.debug("-------------------list-----portal-----------------"+list);
        //---------设置每个字段属性
        String[][] listHeadFields = null;
        if (showListFieldId!= null &&showListFieldId.length() > 0) {
         	listHeadFields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(showListFieldId,dominId);
         } else {                                                         //自定义数据库查询设置
         	listHeadFields = dbBD.getListField(po.getMenuListTableMap().toString());
         }
		Map showJSData = new HashMap();
		if (listHeadFields != null) {
			for(int i=0;i<listHeadFields.length;i++){				
				Object[] obj = new Object[8];
			    obj[0] = "show_"+listHeadFields[i][2];
			    obj[1] = listHeadFields[i][0];
			    obj[2] = listHeadFields[i][1];
			    obj[3] = listHeadFields[i][2];
			    obj[4] = listHeadFields[i][3];
			    obj[5] = listHeadFields[i][4];
			    obj[6] = listHeadFields[i][5];
			    obj[7] = listHeadFields[i][6];
			    showJSData.put(obj[3], obj);
			}
		}
		//-----------设置每个字段属性
		
        
        /*
         * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
         */
        JacksonUtil util = new JacksonUtil();
        String[] fields = null;
        //String[] fields_new = null;
        if(selectPara!=null){
        	fields = selectPara.split(",");
        	fields[0]="id";
        	//有流程
        	/*if("true".equals(isRefFlow)){
        		fields[fields.length-2]="workcurstep";
        		fields[fields.length-1]="workstatus";
        	}*/
        	//判断数据权限
            /*fields_new = new String[fields.length+1];
            for(int i=0;i<fields.length;i++){
            	fields_new[i]=fields[i];
            }*/
            //fields_new[fields.length]="hasUpdAndDelRight";//给查询的字段取名为hasUpdAndDelRight 在前台判断checkbox是否显示权限
        }
        //获取列表保存字段
    	//List list2 = new CustomerMenuDB().getFieldControls(menuId,"4");
    	//可编辑字段
    	/*String custListFields ="";
    	if(list2 != null && list2.size()>0){
    		for(int i=0;i<list2.size();i++){
    			List _list = (List)list2.get(i);
    			custListFields += _list.get(1)+",";
    		}
    	}*/
        List list_new = new ArrayList();
        if(list!=null&&list.size()>0){
        	 if(list.size()<counts){//如果是大小小于信息条数 就获取信息条数
        		 counts=list.size();
             }
			for(int i=0;i<counts;i++){
				Object[] obj = (Object[])list.get(i);
				//Object[] obj1 = new Object[fields.length+1];
				//本条数据id
				//obj1[0] = obj[0];
				
				//本条数据其他显示值设定
				for(int j=1;j<fields.length;j++){
					//根据字段名称取字段属性
					Object[] obj_field = (Object[])showJSData.get(fields[j]);
					/*if(obj_field==null){
						obj1[j] =obj[j];
						continue;
					}*/ 
					if(obj_field!=null&&obj_field.length>0){
					String _fieldid = obj_field[1]+"";
					String _fielddesName = obj_field[2]+"";
					String _fieldname = obj_field[3]+"";
					String _fieldwidth = obj_field[4]+"";
					String _fieldshow = obj_field[5]+"";
					String _fieldvalue = obj_field[6]+"";
					String _fieldtype = obj_field[7]+"";
					String isLink = "0";
					if(_fieldname.equals(linkFieldId)){//是否是连接字段
					    isLink = "1";
					}
					String canModify="0";
					/*if (custListFields.indexOf(_fieldname) > -1) {//
						canModify = "1";
					}*/
					//可编辑或者链接字段不获取显示
					/*if(isLink.equals("1")||
					   canModify.equals("1")||
					   (com.whir.component.util.Field.FIELD_UP_FILE+"").equals(_fieldshow)){
					   obj1[j] =obj[j];
					}else{*/
					 obj[j] = getShowHTMLIN_portal(obj[0]+"",
							              obj[j]+"",
							              _fieldid,
							              _fielddesName,
							              _fieldname,
							              _fieldwidth,
							              _fieldshow,
							              _fieldvalue,
							              _fieldtype,
							              isLink,
							              canModify,
						                  i+"",
						                  formId,
						                  menuId,
						                  hasNewForm,
						                  request)+"";
					/*}*/
				  }
				}				
				//obj1[fields.length]=hasUpdAndDelRight(tableName,obj1[0]+"",rightType,defineOrgs,request);
				list_new.add(obj);
			}
		}
         
		String json = util.writeArrayJSON(fields,list_new);	
		logger.debug("-------------------json-----portal-----------------"+json);
		return json;
		 
	}
    
    /**
     * 获取列表每个字段显示内容   portal  11.5需求 门户自定义模块
     * @return
     */
    private String getShowHTMLIN_portal(String dataId,
                              String dataValue,
						      String fieldid,
						      String fielddesname,
						      String fieldname,
						      String fieldwidth,
						      String fieldshow,
						      String fieldvalue,
						      String fieldtype,
						      String isLink,
						      String canModify,
						      String thisrow,
						      String formId,
						      String menuId,
						      boolean hasNewForm,
						      HttpServletRequest request) {
    	String portletSettingId=request.getParameter("portletSettingId");
		String json = dataValue;
		String value = (dataValue != null &&
		          dataValue.length() > 0 &&
		          dataValue.indexOf(";") > 0)?
		        		  dataValue.substring(0,dataValue.indexOf(";")):dataValue;
		value = (value==null||"null".equals(value))?"":value;
		if("1000001".equals(fieldtype)){
			value = new CustomerMenuDB().getFloatString(value);
		}
        
		if ( ((com.whir.component.util.Field.FIELD_RADIO+"").equals(fieldshow)||
                (com.whir.component.util.Field.FIELD_CHECKBOX+"").equals(fieldshow)||
                (com.whir.component.util.Field.FIELD_SELECT+"").equals(fieldshow)||
                (com.whir.component.util.Field.FIELD_RELATION_SAVE+"").equals(fieldshow))
               && dataValue != null
               && dataValue.length() > 0) { //类型RADIO, CHECKBOX SELECT
			
			 String linkurl = new CustomFormBD().getFieldShowValue(fieldname,
                                                                    fieldshow,
                                                                    dataValue,
                                                                    fieldid) + "&nbsp;";
              if ("0".equals(isLink)) {
            	  json = linkurl;
              } else if ("1".equals(isLink)){
            	  json ="<a href=\"javascript:void(0)\" "+
            	        " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+linkurl+"</a>";
              }
          } else if (
                  ((com.whir.component.util.Field.FIELD_MORE_SELECTPERSON+"").equals(fieldshow) ||
                   (com.whir.component.util.Field.FIELD_SIMPLE_ORG+"").equals(fieldshow) ||
                   (com.whir.component.util.Field.FIELD_MORE_ORG+"").equals(fieldshow) )
                  && dataValue != null
                  && dataValue.length() > 0) {                                //多选人\单选组织\多选组织
        	  
        	  if ("0".equals(isLink)) {
            	  json = value;
              } else if ("1".equals(isLink)){
            	  json ="<a href=\"javascript:void(0)\" "+
            	        " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+value+"</a>";
              }
          } else if ( (com.whir.component.util.Field.FIELD_PASSWORD+"").equals(fieldshow)&& 
        		  dataValue!= null && 
        		  dataValue.length() > 0) {                         //密码
              json = "******";
          } else if ( (com.whir.component.util.Field.FIELD_UP_FILE+"").equals(fieldshow) &&
        		        dataValue != null &&
        		        dataValue.length() > 0) {                   //附件
 
              String tempStr = ((dataValue!= null&&
            		             dataValue.length() > 0 && 
            		             dataValue.indexOf(";") > 0) ? dataValue.split(";")[1].replaceAll(",,",""):"");
              String fileName = ((dataValue != null && 
            		              dataValue.length() > 0 && 
            		              dataValue.indexOf(";") > 0) ? dataValue.split(";")[0].replaceAll(",,",""):"");
              String path = "customform";
              while(tempStr.length()>0 && tempStr.endsWith(",")){
                  tempStr = tempStr.substring(0,tempStr.length()-1);
              }
              String enTempStr = "";
              try{
                 enTempStr = java.net.URLEncoder.encode(tempStr,"UTF-8");
              }catch(Exception e){
            	 e.printStackTrace();
              }
              
              com.whir.component.security.crypto.EncryptUtil util = 
			      new com.whir.component.security.crypto.EncryptUtil();
		
		      String dlcode = util.getSysEncoderKeyVlaue("FileName",fileName,"dir");
		
              String fileServer = com.whir.component.config.ConfigReader.getFileServer(request.getRemoteAddr());
              String tempLink = " '"+fileServer+"/public/download/download.jsp?verifyCode="+
				     dlcode+"&FileName="+
                     fileName+"&name="+enTempStr+"&path="+path+"',isFull:'true',winName: 'opencust" + dataId+ "' ";
              if ("0".equals(isLink)) {
            	  String[] tempStr0s =tempStr.split(",");
                  String[] fileName0s = fileName.split(",");
                  json = "";
            	  for(int k=0;k<tempStr0s.length;k++){
            		  try{
                          enTempStr = java.net.URLEncoder.encode(tempStr0s[k],"UTF-8");
                       }catch(Exception e){
                     	 e.printStackTrace();
                       }
                      dlcode = util.getSysEncoderKeyVlaue("FileName",fileName0s[k],"dir");
                       
                      tempLink = fileServer+"/public/download/download.jsp?verifyCode="+dlcode+"&FileName="+
                                 fileName0s[k]+"&name="+enTempStr+"&path="+path;
                      json += "<a href='"+tempLink+"' target=\"downloadIframe\" >" + tempStr0s[k]+"</a>,&nbsp;";
                  }
              } else if ("1".equals(isLink)){
            	  json ="<a href=\"javascript:void(0)\" "+
            	        " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+tempStr+"</a>";
              }

          } else if ( (com.whir.component.util.Field.FIELD_AUTONUM+"").equals(fieldshow) &&
        		       dataValue!= null &&
        		       dataValue.length() > 0) {                                           //多行文本
            		        		  
    		  if ("0".equals(isLink)) {
    			  /***
                  if (value.length() > 10)
                	  json = value.substring(0, 10) + "......";
                  else
                	  json = value + "&nbsp;";
                	  **/
    			  json = value + "&nbsp;";
              } else if ("1".equals(isLink)){
            	  String value0 = "";
            	  /**
                  if (value.length() > 10)
                      value0 = value.substring(0, 10) + "......";
                  else
                      value0 = value + "&nbsp;";
                      **/
            	  value0 = value + "&nbsp;";
                  
                  json ="<a href=\"javascript:void(0)\" "+
      	                " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+value0+"</a>";
              }

          } else if ( (com.whir.component.util.Field.FIELD_TEXT + "").equals(fieldshow)) { //单行文本

             if("1".equals(canModify)) {                           //可编辑

                  //获取列表编辑字段属性
                  List _list3 = new CustomerMenuDB().getFieldInfoById(fieldid);
                  String _fieldLen = "0";
                  if (_list3 != null) {
                      _fieldLen = _list3.get(6) + "";
                  }
                  if ("1000002".equals(fieldtype)) {
                	  json = "<input type=\"text\" name=\"" +fieldname+"\" id=\"" +fieldname +"\" "+
                	         " value=\"" + value + "\" size=\"10\" class=\"inputText\" "+
                	         " whir-options=\"vtype:[{'maxLength':"+_fieldLen+"}]\">";
                  } else {
                	  json = "<input type=\"text\" name=\"" +fieldname+"\" id=\"" +fieldname +"\" "+
         	                 " value=\"" + value + "\" size=\"10\" class=\"inputText\" "+
         	                 " whir-options=\"vtype:['notempty',{'maxLength':"+_fieldLen+"},'digit']\">";
                  }
              }else if ("1".equals(isLink)){
                  json ="<a href=\"javascript:void(0)\" "+
      	                " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+value+"</a>";
              }else{
            	  json =value;
              }

         } else if ( (com.whir.component.util.Field.FIELD_SIMPLE_SELECTPERSON+"").equals(fieldshow)) { //单选人(全部)
        	 
            String simpleName = "";
            String simpleId = "";
            if(dataValue != null &&
            	dataValue.length() > 0 &&
            	dataValue.indexOf(";") > 0){
                simpleName = dataValue.substring(0,dataValue.indexOf(";"));
                simpleId = dataValue.substring(dataValue.indexOf(";")+1,dataValue.length());
            }
            String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
           /* if ("1".equals(canModify)) {                           //可编辑
            	
            	json = "<input type=\"text\" name=\""+fieldname+"_Name"+thisrow+"\" id=\"" +fieldname+"_Name"+thisrow+"\" "+
   	                   " value=\"" + simpleName + "\" style=\"width:90%\" class=\"inputText\"  readonly>"+
   	                   "<a href=\"javascript:void(0);\" class=\"selectIco\" onClick=\"openSelect({allowId:'"+fieldname+"_Id"+thisrow+"', allowName:'"+fieldname+"_Name"+thisrow+"', "+
   	                   " select:'user', single:'yes', show:'user', range:'*0*',limited:'1'});\"></a>"+
   	                   "<input type='hidden' id='"+fieldname+"_Id"+thisrow+"' name='"+fieldname+"_Id"+thisrow+"' value='"+simpleId+"'>";
   	                   
            }else*/ if ("1".equals(isLink)){
                json ="<a href=\"javascript:void(0)\" "+
    	                " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+simpleName+"</a>";
            }else{
            	json =simpleName;
            }

        } else if ( (com.whir.component.util.Field.FIELD_DATE + "").equals(fieldshow)) { //日期
        	/*if ("1".equals(canModify)){                           //可编辑
        		
        		json ="<input type=\"text\" id=\""+fieldname+thisrow+"\" name=\""+fieldname+thisrow+"\" "+
        		      " class=\"Wdate whir_datebox\" onclick=\"WdatePicker({el:'"+fieldname+thisrow+"'})\" value=\""+value+"\"/>";
        	}else*/ if ("1".equals(isLink)){
        		json ="<a href=\"javascript:void(0)\" "+
                      " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+value+"</a>";
            }else{
            	json = value;
            }

        } else if ( (com.whir.component.util.Field.FIELD_DATETIME +"").equals(fieldshow)) { //日期时间

        	/*if ("1".equals(canModify)){                           //可编辑

        		json ="<input type=\"text\" id=\""+fieldname+thisrow+"\" name=\""+fieldname+thisrow+"\" "+
  		              " class=\"Wdate whir_datetimebox\" onclick=\"WdatePicker({el:'"+fieldname+thisrow+"',dateFmt:'yyyy-MM-dd HH:mm'})\" value=\""+value+"\"/>";
        	}else*/ if ("1".equals(isLink)){
        		
        		json ="<a href=\"javascript:void(0)\" "+
                      " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+value+"</a>";
        	}else{
            	 json = value;
            }
         } else {
        	 
        	 if("1".equals(isLink)){
        		 json ="<a href=\"javascript:void(0)\" "+
                        " onclick=\"view('"+dataId+"','"+portletSettingId+"','"+hasNewForm+"','"+formId+"','"+menuId+"');\">"+value+"</a>";
             }else{
            	 json = value;
              }
          }

//		printJsonResult(json);
		return json;
	}
    /**
     * 获取门户自定义模块的列表头部字段  11.5需求  2016-04-28
     * @param menuId
     * @param dominId
     * @param listFieldId
     * @param request
     * @return
     */
    public Map goRightMenu_portal(String menuId,String dominId,String listFieldId,HttpServletRequest request){
    	   
    	CustomerMenuConfigerPO po = new CustMenuWithOriginalBD().loadMenuSetById(menuId,dominId);
    	 
    	boolean hasCustmenuAuth = new CustomerMenuDB().checkCustmenuAuth(po.getMenuViewUser(),
                po.getMenuViewOrg(),
                po.getMenuViewGroup(),
                CommonUtils.getSessionUserId(request)+"",
                CommonUtils.getSessionOrgIdString(request)+"", 
                CommonUtils.getSessionDomainId(request)+"");
    	boolean isRefFlow = false;
    	boolean isNewRefFlow = false;
    	if (hasCustmenuAuth) {
            if (new ManagerBD().hasRight(CommonUtils.getSessionUserId(request)+"", 
            		                     "99-" + menuId + "-03")) {
                if (po.getMenuRefFlow() != null && 
                	po.getMenuRefFlow().length() > 0 &&
                    !"-1".equals(po.getMenuRefFlow())) {
                	
                	String[] menuRefFlows = po.getMenuRefFlow().toString().split("\\$");
                	if(menuRefFlows.length==2&&
    		        		"newFlow".equals(menuRefFlows[0])){
                		isNewRefFlow = true;
                	}
                    isRefFlow = true;
                }
            }
        }
    	
    	CustomDatabaseBD dbBD = new CustomDatabaseBD();
		//String tableId = po.getMenuListTableMap() + "";
		//String tableName=dbBD.getSingleTableName(tableId);
		 
        /*String[][] queryFields = null;
        //优先自定义模块查询设置
        if(po.getMenuListQueryConditionElements()!=null&&
           !"null".equals(po.getMenuListQueryConditionElements())&&
           !"".equals(po.getMenuListQueryConditionElements())){
        	queryFields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(po.getMenuListQueryConditionElements(),
        			                                                            CommonUtils.getSessionDomainId(request)+"");
        }else{                                                                     //自定义数据库查询设置
        	queryFields = dbBD.getQueryField(po.getMenuListTableMap().toString());
        }*/
        
        //String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
        
        //获取列表保存字段
    	List list22 = new CustomerMenuDB().getFieldControls(menuId,"4");
    	//可编辑字段
    	String custListFields ="";
    	if(list22 != null && list22.size()>0){
    		for(int i=0;i<list22.size();i++){
    			List _list = (List)list22.get(i);
    			custListFields += _list.get(1)+",";
    		}
    	} 
        
		//生成列表头
        String[][] listHeadFields = null;
        //优先自定义模块查询设置
        if (listFieldId != null &&listFieldId.length() > 0) {
        	listHeadFields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(listFieldId,CommonUtils.getSessionDomainId(request)+"");
        } else {                                                         //自定义数据库查询设置
        	listHeadFields = dbBD.getListField(po.getMenuListTableMap().toString());
        }
		String headerContainer = "";
		List showJSData = new ArrayList();
		if (listHeadFields != null) {
			headerContainer += "<tr class=\"wh-human-remind-tab-tit\">";			 
			for(int i=0;i<listHeadFields.length;i++){				
				/*Object[] obj = new Object[8];
			    obj[0] = "show_"+listHeadFields[i][2];
			    obj[1] = listHeadFields[i][0];
			    obj[2] = listHeadFields[i][1];
			    obj[3] = listHeadFields[i][2];
			    obj[4] = listHeadFields[i][3];
			    obj[5] = listHeadFields[i][4];
			    obj[6] = listHeadFields[i][5];
			    obj[7] = listHeadFields[i][6];*/
			    
				//附件或可编辑类型前台获取展示
				/*if(((obj[3]+"").equals(po.getMenuMaintenanceSubTableName())||custListFields.indexOf(obj[3]+"") > -1)||
					((com.whir.component.util.Field.FIELD_UP_FILE+"").equals(obj[5]))){
					showJSData.add(obj);
					headerContainer += "<td whir-options=\"field:'"+listHeadFields[i][2]+"',"+
	                   "width:'"+listHeadFields[i][3]+"%',renderer:"+obj[0]+"\">"+
	                   listHeadFields[i][1]+"</td>";
				}else{*/
					/*headerContainer += "<td whir-options=\"field:'"+listHeadFields[i][2]+"',"+
	                   "width:'"+listHeadFields[i][3]+"%' \">"+
	                   listHeadFields[i][1]+
	                   "<img src=\""+rootPath+"/images/blanksort.gif\" onclick=\"orderBy(this,'"+listHeadFields[i][2]+"');\"></td>";*/
					
					headerContainer += "<td>"+listHeadFields[i][1]+"</td>";
				
				/*}	*/		    
			}
			/*if(isRefFlow){
				headerContainer +="<td whir-options=\"field:'ban', width:'10%',renderer:showApp\">办理状态</td>";
			}*/
			//headerContainer +="<td whir-options=\"field:'opt', width:'8%',renderer:showoperate\">操作</td>";
			headerContainer +="</tr>";
		}
		
		logger.debug("--------------headerContainer: "+headerContainer);
		List _list = new ManagerBD().getRightScope(CommonUtils.getSessionUserId(request)+"","99-" + menuId + "-03");
        String rightType = "";
        String defineOrgs = "";
        if(_list != null ){

            if(_list.size() > 0){
                Object[] obj = (Object[]) _list.get(0);
                rightType = obj[0] + "";
                defineOrgs = obj[1] + "";
                if(obj[3]!=null&&!"null".equals(obj[3]+"")){
                	defineOrgs += obj[3];
                }
                if(obj[4]!=null&&!"null".equals(obj[4]+"")){
                	defineOrgs += obj[4];
                }
            }
        }
        boolean hasNewForm = false;
        if((po.getMenuSearchBound()+"").indexOf("new$")!=-1){
        	hasNewForm = true;
        }
         
    	Map map=new HashMap();
    	map.put("headerContainer", headerContainer);//显示列表的表头(拼接好的html,直接获取即可显示)
    	map.put("rightType", rightType);//权限类型
    	map.put("defineOrgs", defineOrgs);//定义组织
    	map.put("isRefFlow", isRefFlow);//是否有关联流程false/true
    	map.put("isNewRefFlow", isNewRefFlow);//是否有关联新流程false/true
    	map.put("hasNewForm", hasNewForm);//是否是新表单false/true
		return map;       
    }
    
 
    /**
     * 获取是否有修改删除数据权限
     * @param tableName
     * @param infoId
     * @param rightType
     * @param defineOrgs
     * @return 
     * 
     * @throws Exception
     */
    public boolean hasUpdAndDelRight(String tableName,
	                                String infoId,
	                                String rightType,
	                                String defineOrgs,HttpServletRequest request) throws Exception{
    	
    	boolean hasAuth = new CustomerMenuDB().
    	          hasUpdAndDelRight(request,
    	        		            tableName,
    	        		            infoId,
    	        		            rightType,
    	        		            defineOrgs, 
    	        		            CommonUtils.getSessionDomainId(request)+"");
		return hasAuth;
    }
  
    public static Date getDateBefore(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);  
        return now.getTime();  
    }    
    public static Date getDateAfter(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) +day);  
        return now.getTime();  
    }    
    
    /**
	  * 修改自定义模块名称的时候同步修改权限模块的自定义模块名称  2016/07/18
	  * @param menuName
	  * @param menuBlone
	  * @param menuId
	  * @return
	  * @throws Exception
	  */
	 public boolean updCustNameInRoleJsp(CustomerMenuConfigerPO po){
		 
		 CustomMenuEJBBean customMenuEJBBean=new CustomMenuEJBBean();
		 boolean flag=false;
		 try {
			 flag= customMenuEJBBean.updCustNameInRoleJsp(po);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	 }
}




