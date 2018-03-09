package com.whir.ezflow.actionsupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.ezflow.util.EzFlowFinals;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.OrgInfoVO;
import com.whir.ezflow.vo.UserInfoVO;
import com.whir.ezoffice.assetManager.bd.CommentBD;
import com.whir.ezoffice.message.bd.messageSettingBD;
import com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD;
import com.whir.org.manager.bd.ManagerBD;

public class EzFlowBaseAction  extends BaseActionSupport {
	
	protected UserInfoVO  curUserInfoVO=null; 
	
	protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static Logger logger = Logger.getLogger(EzFlowBaseAction.class.getName());
 
	
	/**
	 * 装载当前人信息
	 * @param request
	 * @return
	 */
	protected UserInfoVO dealUserInfoVO(){
		
		if(curUserInfoVO!=null&&EzFlowUtil.judgeNull(curUserInfoVO.getUserAccount())){
			return curUserInfoVO;
		}
	    UserInfoVO userInfoVO=null;
		HttpSession session=request.getSession();
		String userAccount=session.getAttribute("userAccount")==null?"":session.getAttribute("userAccount").toString();
 
		if(EzFlowUtil.judgeNull(userAccount)){
			//人员帐号信息
			String userName=session.getAttribute("userName")==null?"":""+session.getAttribute("userName");
			String userId=session.getAttribute("userId")==null?"":""+session.getAttribute("userId");
			//人员所属组织信息
			String orgId=session.getAttribute("orgId")==null?"":session.getAttribute("orgId").toString();
			String orgName=session.getAttribute("orgName")==null?"":session.getAttribute("orgName").toString();
			String orgIdString=session.getAttribute("orgIdString")==null?"":session.getAttribute("orgIdString").toString();
			/*----------测试数据----------------*/
			String orgSerial=session.getAttribute("orgSerial")==null?"":session.getAttribute("orgSerial").toString();
			String orgLayerName="";
			//
			userInfoVO=new UserInfoVO(userId,userName,userAccount);

			userInfoVO.setDomainId(session.getAttribute("domainId")+"");
			
			/*
			 //组织信息
			OrgInfoVO orgvo=new OrgInfoVO();
			orgvo.setOrgId(orgId);
			orgvo.setOrgName(orgName);
			orgvo.setOrgSerial(orgSerial);
			// session 取的  orgName 就是   OrgLayerName
			orgvo.setOrgLayerName(orgName);
			orgvo.setOrgIdString(orgIdString);	
			curUserInfoVO.setOrgVO(orgvo);*/
 
		}else{
			userInfoVO=new UserInfoVO();
		}
		this.curUserInfoVO=userInfoVO;
		return curUserInfoVO;
	}
	
	/**
	 * 单独设置组织信息， 这里
	 * @param session
	 */
	public  void resetUserOrg(){	
		HttpSession session=request.getSession();
		//人员所属组织信息
		String orgId=session.getAttribute("orgId")==null?"":session.getAttribute("orgId").toString();
		String orgName=session.getAttribute("orgName")==null?"":session.getAttribute("orgName").toString();
		String ORGNAMESTRING=session.getAttribute("orgName")==null?"":session.getAttribute("orgName").toString();
		 
		int ld=orgName.lastIndexOf("."); 
		if(ld>0){
			orgName=orgName.substring(ld+1);
		}
		String orgIdString=session.getAttribute("orgIdString")==null?"":session.getAttribute("orgIdString").toString();

		/*----------测试数据----------------*/
		String orgSerial=session.getAttribute("orgSerial")==null?"":session.getAttribute("orgSerial").toString();
		String orgLayerName="";
	    //组织信息
		OrgInfoVO orgvo=new OrgInfoVO();
		orgvo.setOrgId(orgId);
		orgvo.setOrgName(orgName);
		orgvo.setOrgSerial(orgSerial);
		// session 取的  orgName 就是   OrgLayerName
		orgvo.setOrgLayerName(ORGNAMESTRING);
		orgvo.setOrgIdString(orgIdString);	
		OrgInfoVO iorgvo=curUserInfoVO.getOrgVO();
		if(iorgvo!=null){
			orgvo.setOrgLevel(iorgvo.getOrgLevel());
		}
		curUserInfoVO.setOrgVO(orgvo);	
	}
	
	 
	
 
	
	protected Map  getRightScope(HttpServletRequest request){
		
		HttpSession session=request.getSession();
		/*-------------------从request取值部分-----------------------*/
		// 当前办理人帐号
		String curUserAccount = session.getAttribute("userAccount")+"";
		String curUserId= session.getAttribute("userId")+"";
		String curUserName= session.getAttribute("userName")+"";
		String curOrgId=session.getAttribute("orgId")+"";
		String domainId="0";
		
		
		Map resultMap=new HashMap();
		String rightCode="02*03*02";
		ManagerBD  managerBD =new ManagerBD();
 
		String where = "";
		List list=managerBD.getRightScope(curUserId,rightCode);
		
		String userId=curUserId;
		String orgId=curOrgId;
     
        List orgList=new ArrayList();
        List sideOrgList=new ArrayList();
        List userList=new ArrayList();
	 
        //----------------------------------------------------------------------
        //兼职组织
        String sidelineOrg ="";
        
        String sql="select emp.sidelineorg  from org_employee emp "+
		           " where emp.useraccounts='"+curUserAccount+"'";	
		CommentBD bd=new CommentBD();
		List sideList=bd.getDataBySQL(sql);
		if(sideList!=null&&sideList.size()>0){
			Object obj[]=(Object[])sideList.get(0);	
			sidelineOrg=""+obj[0];
		}	
        String[] sidelineOrgArr = null;
        String sidelineOrg_sql = "";
        String sidelineOrg_str = "";//兼职组织 *123**456*
        if(sidelineOrg!=null && !"".equals(sidelineOrg) && !"null".equals(sidelineOrg)){
            if(sidelineOrg.startsWith("*") && sidelineOrg.endsWith("*")){
                sidelineOrg_str = sidelineOrg;
                sidelineOrg = sidelineOrg.substring(1, sidelineOrg.length()-1);
                sidelineOrgArr = sidelineOrg.split("\\*\\*");
                for(int i=0; i<sidelineOrgArr.length; i++){
                	sideOrgList.add(sidelineOrgArr[i]);
                }
            }
        }  
        //sidelineOrg_str-------------
        //----------------------------------------------------------------------
 
        if (list != null && list.size() > 0) {
            Object[] obj = (Object[])list.get(0);
            String scopeType = obj[0].toString();
 
            if ("0".equals(scopeType)) {
                //可以维护全部数据
                where = " 1=1 ";
                //这两者赋值 为null 就不加条件   就能查所有的
                orgList=null;
                userList=null;        
            } else if ("1".equals(scopeType)) {
                //可以维护本人的数据
            	userList.add(curUserId);
                
            } else if ("2".equals(scopeType)) {
                //可以维护本组织及下级组织的数据
                String orgRange = managerBD.getAllJuniorOrgIdByRange("*" + orgId + "*" + sidelineOrg_str);//包含兼职组织
                if (orgRange.indexOf("a") > 0) {
                    String[] tmp = orgRange.split("a");
                    for (int k = 0; k < tmp.length; k++) {
                    	String aorgIds=tmp[k];
                    	String aorgIdArr[]=aorgIds.split(",");
                    	for(int kk=0;kk<aorgIdArr.length;kk++){
                    	   orgList.add( aorgIdArr[kk]);
                    	}                   
                    }                  
                } else {
                	String aorgIdArr[]=orgRange.split(",");
                	for(int kk=0;kk<aorgIdArr.length;kk++){
                	   orgList.add( aorgIdArr[kk]);
                	}
                }
            } else if ("3".equals(scopeType)) {
 
                //可以维护本组织的数据
                orgList.add(orgId);
                orgList.addAll(sideOrgList);//包含兼职组织             
            } else {
                //scopeType==4 维护定义的范围
                if (obj[1] != null && !"".equals(obj[1].toString())) {
                    String orgRange = managerBD.getAllJuniorOrgIdByRange((String)obj[1]);
                    if ("".equals(orgRange)) {
                         
                    } else {
                    	if (orgRange.indexOf("a") > 0) {
                            String[] tmp = orgRange.split("a");
                            for (int k = 0; k < tmp.length; k++) {
                            	String aorgIds=tmp[k];
                            	String aorgIdArr[]=aorgIds.split(",");
                            	for(int kk=0;kk<aorgIdArr.length;kk++){
                            	   orgList.add( aorgIdArr[kk]);
                            	}                   
                            }                  
                        } else {
                        	String aorgIdArr[]=orgRange.split(",");
                        	for(int kk=0;kk<aorgIdArr.length;kk++){
                        	   orgList.add( aorgIdArr[kk]);
                        	}
                        }
                    }
                }

                String dbType = com.whir.common.config.SystemCommon.getDatabaseType();
                //判断定义的范围内的用户
                //obj[3] 为选择的用户，obj[4]为选择的群组
                String  scopeUserIds=obj[3]+"";
                if(EzFlowUtil.judgeNull(scopeUserIds)){
                	scopeUserIds=EzFlowUtil.dealStrForIn(scopeUserIds, '$', null);
                	String scopeUserIdArr[]=scopeUserIds.split(",");
                	if(scopeUserIdArr!=null&&scopeUserIdArr.length>0){
                		for(int i=0;i<scopeUserIdArr.length;i++){
                			userList.add(new Long(scopeUserIdArr[i]));
                		}
                	}	
                }
            }
        } else {
          
        }
 
        //如果 size 为 0 表明没有范围可维护
        if(orgList!=null&&orgList.size()<=0){
        	orgList.add("-1");
        }
        if(userList!=null&&userList.size()<=0){
        	userList.add("-1");
        }
        
        resultMap.put("orgList", orgList);
        resultMap.put("userList", userList);
        return resultMap;
	}
 
	
	/***
	 * 判断是否不为空
	 * @param str
	 * @return
	 */
	public boolean judgeNotNull(String str){
		  if(str!=null&&!str.equals("")&&!str.equals("null")){
			  return true;
		  }else{
			  return false;
		  }
	}
	

  
	
	/**
	 * 发送邮件
	 * @param mailsubject
	 * @param mailcontent
	 * @param posterName
	 * @param posterId
	 * @param mailtoId
	 * @param mailtoName
	 * @param domainId
	 * @return
	 */
	public boolean sendInnerMail(String mailsubject, String mailcontent,
			String posterName, Long posterId, String mailtoId,
			String mailtoName, String domainId,HttpServletRequest request) {
		if(mailtoId==null||mailtoId.equals("")||mailtoId.equals("null")){
			logger.warn("邮件接收人为空");
		}
		return sendInnerMail(mailsubject, mailcontent, "0", posterName,
				posterId, mailtoId, mailtoName, domainId,request);
	}
	
	
    /**
     * 发送邮件
     * @param mailsubject
     * @param mailcontent
     * @param mailcontenttype
     * @param posterName
     * @param posterId
     * @param mailtoId
     * @param mailtoName
     * @param domainId
     * @return
     */
	public boolean sendInnerMail(String mailsubject, String mailcontent,
			String mailcontenttype, String posterName, Long posterId,
			String mailtoId, String mailtoName, String domainId,HttpServletRequest request) {
		
	
		logger.debug("mailsubject:" + mailsubject + "\n mailcontent:"
				+ mailcontent + "\n mailcontenttype:" + mailcontenttype
				+ "\n posterName:" + posterName + "\n posterId:" + posterId
				+ "\n mailtoId:" + mailtoId + "\n mailtoName:" + mailtoName
				+ "\n domainId:" + domainId); 
		/*return bd.sendInnerMail(mailsubject, mailcontent, mailcontenttype,
				posterName, posterId, mailtoId, mailtoName, domainId);*/
		HttpSession session=request.getSession();
		InnerMailBD bd = new InnerMailBD();
		String mail_pass=com.whir.common.util.CommonUtils.getSessionUserPassword(request);
		String mail_account=com.whir.common.util.CommonUtils.getSessionUserAccount(request);
		boolean result=true;
		try {
			mailcontenttype="1";
			result= bd.sendInnerMail(mailsubject, mailcontent, mailcontenttype, 
					""+session.getAttribute("userName"), new Long(""+session.getAttribute("userId")), 
					mailtoId, mailtoName,
					domainId, mail_account,
					mail_pass, true);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  result;
	}
	
	public void  dealTableInfoMap(Map  map){ 
		if(map!=null){  
		}else{
			map=new HashMap();
		}  
		Map infomap = request.getParameterMap();
		Map newMap = turnToMapString(infomap); 
		map.put("p_wf_tableValueMap",newMap);
	}
	
	
	/**
	 * 转换map里的数组
	 * @param map
	 * @return
	 */
	private Map   turnToMapString( Map<String, Object> map) {
		  Iterator it = map.entrySet().iterator();
		  Map newMap = new HashMap();
		  while (it.hasNext()) {
		        Map.Entry entry = (Map.Entry) it.next();
		        String key = entry.getKey().toString();
		        Object value = entry.getValue();
		    	if (value instanceof String[]){
		    	   String[] strs = (String[])value;
		    	   if(strs.length==1){
		    		   newMap.put(key, strs[0]);
		    	   }else{
		    		   newMap.put(key, value);
		    	   }
		    	}else{
		    	   newMap.put(key, value);
		    	}
		 }
		 return newMap;
   }
    
	/**
	 * 判断是否是整数
	 * @param str
	 * @return
	 */
	public  boolean isInteger(String str) {
		// Pattern pattern = Pattern.compile("[0-9]*");
		if(str==null||str.equals("")||str.equals("null")){
			return true;
		}
		Pattern pattern = Pattern.compile("^[+-]?[0-9]+$");
		return pattern.matcher(str).matches();
	}
	
	 
	/**
	 * 
	 * @param fields  字段名 如果有多个 以 ;分割
	 * @return
	 */
    public  boolean judgeInteger(String fields){
    	boolean result=true;
    	String gg=request.getParameter("fff")+"";
    	if(fields!=null&&!fields.equals("null")&&!fields.equals("")){
    		String fieldsArr[]=fields.split(";");
    		for(String str:fieldsArr){ 
    			if(!isInteger(request.getParameter(str))){
    				result=false;
    				break;
    			}
    		}
    	}
    	return result; 	
     }

}
