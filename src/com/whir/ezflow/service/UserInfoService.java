package com.whir.ezflow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.apache.log4j.Logger;
import com.whir.common.util.StringSplit;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.OrgInfoVO;
import com.whir.ezflow.vo.UserInfoVO;

/**
 * 
 * @author wanggl
 *
 */
public class UserInfoService extends ServiceBase implements UserInfoInterface {
	
	
	private static Logger logger = Logger.getLogger(UserInfoService.class.getName());
	
	private boolean isFilterDelete=true; 
	 
	public UserInfoService noNeedFilterDelete() {
		this.isFilterDelete  =false  ;
		return this;
	}
	public UserInfoService needFilterDelete() {
		this.isFilterDelete  =true  ;
		return this;
	} 


	/**
	 * 根据帐号取人员信息  包括人员的领导信息 拼成UserInfoVO
	 * @param inMap   key:useraccount  帐号
	 * @return  Map   key:userInfoVO   UserInfoVO对象
	 */
	public Map<String,Object>getUserInfoVOByAccount_out(Map<String,Object> inMap){
		String useraccount=inMap.get("useraccount").toString();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("userInfoVO", getUserInfoVOByAccount(useraccount));
		return resultMap;	
	}
	
	
	
	/**
	 * 根据帐号取人员信息  包括人员的领导信息 拼成UserInfoVO
	 */
	public UserInfoVO getUserInfoVOByAccount(String useraccount) {
		 UserInfoVO vo=null;
		 OrgInfoVO orgVO=new OrgInfoVO();
		 List<Map> list=new ArrayList<Map>();
		 //String sql=" select emp_id , empName ,useraccounts from  org_employee  useraccounts='"+useraccount+"'";	     
		 String sql="select emp.EMP_ID ,emp.EMPNAME ,emp.USERACCOUNTS,"+
		 "org.ORGSERIAL, org.ORG_ID, org.ORGNAME ,"+
		 "org.ORGNAMESTRING,org.ORGIDSTRING,org.ORGLEVEL,"+
		 "emp.DEPT_LEADER_IDS,emp.CHARGE_LEADER_IDS,emp.EMPLEADERID,  "+
		 "emp.SIDELINEORG,emp.EMPDUTY "+
 		 " FROM org_employee  emp ,ORG_ORGANIZATION_USER u,ORG_ORGANIZATION org"+
         " WHERE  emp.emp_id=u.emp_id and  org.org_id=u.org_id and emp.useraccounts='"+useraccount+"'";
		 if(this.isFilterDelete){
			 sql+=" and  emp.userIsDeleted=0 and emp.USERISACTIVE=1";
         }
		
		 list=new UtilService().searchBySql(sql);
		 //list=searchBySql(sql);
		 if(list!=null&&list.size()>0){
			 Map map=list.get(0);
			 vo=new UserInfoVO();
			 vo.setUserAccount(useraccount);
			 vo.setUserId(map.get("EMP_ID")==null?"":map.get("EMP_ID").toString());
			 vo.setUserName(map.get("EMPNAME")==null?"":map.get("EMPNAME").toString());
			 //上级领导串
			 vo.setLeaderIds(""+map.get("EMPLEADERID"));
			 //部门领导串
			 vo.setDepartLeaderIds(""+map.get("DEPT_LEADER_IDS"));
			 //分管领导串
			 vo.setChargeLeaderIds(""+map.get("CHARGE_LEADER_IDS"));
			 
			 vo.setDutyName(map.get("EMPDUTY")==null?"":map.get("EMPDUTY").toString());
			 
			//兼职组织串
			 vo.setSidelineorg(map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString()); 
			 orgVO.setOrgId(map.get("ORG_ID")==null?"":map.get("ORG_ID").toString());
			 orgVO.setOrgLayerName(""+map.get("ORGNAMESTRING"));
			 orgVO.setOrgName(""+map.get("ORGNAME"));
			 orgVO.setOrgSerial(""+map.get("ORGSERIAL")); 
			 orgVO.setOrgLevel(map.get("ORGLEVEL")==null?"":map.get("ORGLEVEL").toString());	
			 
			 //id 组织串
			 String orgIdString=map.get("ORGIDSTRING")==null?"":map.get("ORGIDSTRING").toString();
			 orgIdString=StringSplit.splitOrgIdString(orgIdString, "$", "_");
			 
			 orgVO.setOrgIdString(orgIdString);		 
			 vo.setOrgVO(orgVO);
			 
		 }
		 
		 /*else{//测试用的  万户测试用的
			 /*---------替代数据------------
			 vo=new UserInfoVO();
			 vo.setUserAccount(useraccount);
			 vo.setUserId("ezFlowUtil_"+useraccount);
			 vo.setUserName("ezFlowUtil_"+useraccount); 
			 orgVO.setOrgId("orgId");
			 orgVO.setOrgLayerName("orgnamestring");
			 orgVO.setOrgName("orgname");
			 orgVO.setOrgSerial("orgserial");
			 vo.setOrgVO(orgVO);
		 }*/
		 return vo;
	}
	
	
	/**
	 * 根据帐号取人员信息  包括人员的领导信息 拼成UserInfoVO
	 * @param inMap   key:useraccount  帐号
	 * @return  Map   key:userInfoVO   UserInfoVO对象
	 */
	public Map<String,Object>getUserInfoVOByUserId_out(Map<String,Object> inMap){
		String userId=inMap.get("userId").toString();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("userInfoVO", getUserInfoVOByUserId(userId));
		return resultMap;	
	}
	
	
	
	/**
	 * 根据帐号取人员信息  包括人员的领导信息 拼成UserInfoVO
	 */
	public UserInfoVO getUserInfoVOByUserId(String userId) {
		 UserInfoVO vo=null;
		 OrgInfoVO orgVO=new OrgInfoVO();
		 List<Map> list=new ArrayList<Map>();
		 //String sql=" select emp_id , empName ,useraccounts from  org_employee  useraccounts='"+useraccount+"'";	     
		 String sql="select emp.EMP_ID ,emp.EMPNAME ,emp.USERACCOUNTS,"+
		 "org.ORGSERIAL, org.ORG_ID, org.ORGNAME ,"+
		 "org.ORGNAMESTRING,org.ORGIDSTRING,org.ORGLEVEL,"+
		 "emp.DEPT_LEADER_IDS,emp.CHARGE_LEADER_IDS,emp.EMPLEADERID,  "+
		 "emp.SIDELINEORG,emp.EMPDUTY "+
 		 " FROM org_employee  emp ,ORG_ORGANIZATION_USER u,ORG_ORGANIZATION org"+
         " WHERE  emp.emp_id=u.emp_id and  org.org_id=u.org_id and emp.EMP_ID="+userId+" ";
		 //+" and  emp.userIsDeleted=0 and emp.USERISACTIVE=1";
		
		 list=new UtilService().searchBySql(sql);
		 //list=searchBySql(sql);
		 if(list!=null&&list.size()>0){
			 Map map=list.get(0);
			 vo=new UserInfoVO();
			 vo.setUserAccount(map.get("USERACCOUNTS")==null?"":map.get("USERACCOUNTS").toString());
			 vo.setUserId(map.get("EMP_ID")==null?"":map.get("EMP_ID").toString());
			 vo.setUserName(map.get("EMPNAME")==null?"":map.get("EMPNAME").toString());
			 //上级领导串
			 vo.setLeaderIds(""+map.get("EMPLEADERID"));
			 //部门领导串
			 vo.setDepartLeaderIds(""+map.get("DEPT_LEADER_IDS"));
			 //分管领导串
			 vo.setChargeLeaderIds(""+map.get("CHARGE_LEADER_IDS"));
			 
			 vo.setDutyName(map.get("EMPDUTY")==null?"":map.get("EMPDUTY").toString());
			 
			//兼职组织串
			 vo.setSidelineorg(map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString()); 
			 orgVO.setOrgId(map.get("ORG_ID")==null?"":map.get("ORG_ID").toString());
			 orgVO.setOrgLayerName(""+map.get("ORGNAMESTRING"));
			 orgVO.setOrgName(""+map.get("ORGNAME"));
			 orgVO.setOrgSerial(""+map.get("ORGSERIAL")); 
			 orgVO.setOrgLevel(map.get("ORGLEVEL")==null?"":map.get("ORGLEVEL").toString());	
			 
			 //id 组织串
			 String orgIdString=map.get("ORGIDSTRING")==null?"":map.get("ORGIDSTRING").toString();
			 orgIdString=StringSplit.splitOrgIdString(orgIdString, "$", "_");
			 
			 orgVO.setOrgIdString(orgIdString);		 
			 vo.setOrgVO(orgVO);
			 
		 }
		 /*
		 else{//测试用的  万户测试用的
			 /*---------替代数据------------ 
			 vo=new UserInfoVO();
			 vo.setUserAccount("ezFlowUtil_"+userId);
			 vo.setUserId(userId);
			 vo.setUserName("ezFlowUtil_"+userId); 
			 orgVO.setOrgId("orgId");
			 orgVO.setOrgLayerName("orgnamestring");
			 orgVO.setOrgName("orgname");
			 orgVO.setOrgSerial("orgserial");
			 vo.setOrgVO(orgVO);
		 }*/
		 return vo;
	}
   
 
	/**
	 * 根据多个帐号取多个人员信息   包括人员的领导信息 拼成UserInfoVO
	 */
	public List<UserInfoVO> getUserInfoVOsByAccounts(String accounts) {
		List<UserInfoVO> userInfos=new ArrayList<UserInfoVO>();
		 UserInfoVO vo=null;
		 OrgInfoVO orgVO=null;
		 List<Map> list=new ArrayList<Map>();
		 //String sql=" select emp_id , empName ,useraccounts from  org_employee  useraccounts='"+useraccount+"'";	     
		 String sql="select emp.EMP_ID,emp.EMPNAME,emp.USERACCOUNTS ,"+
		            "org.ORGSERIAL , org.ORG_ID ,org.ORGNAME  ,"+
		            "org.ORGNAMESTRING , emp.DEPT_LEADER_IDS,emp.CHARGE_LEADER_IDS,"+
		            "EMPLEADERID, emp.SIDELINEORG ,org.ORGLEVEL, "+
		            "org.ORGIDSTRING ,emp.EMPDUTY "+
 		            " FROM org_employee  emp ,ORG_ORGANIZATION_USER u,ORG_ORGANIZATION org"+
                    " WHERE  emp.emp_id=u.emp_id and  org.org_id=u.org_id and emp.useraccounts in("+accounts+")"+
 		            " and  emp.userIsDeleted=0 and emp.USERISACTIVE=1";
		 list=new UtilService().searchBySql(sql);
		 //list=searchBySql(sql);
		 if(list!=null&&list.size()>0){
			 for(Map map:list){
				 vo=new UserInfoVO();
				 vo.setUserAccount(map.get("USERACCOUNTS").toString());
				 vo.setUserId(map.get("EMP_ID")==null?"":map.get("EMP_ID").toString());
				 vo.setUserName(""+map.get("EMPNAME"));
				 //上级领导串
				 vo.setLeaderIds(map.get("EMPLEADERID")==null?"":map.get("EMPLEADERID").toString());
				 //部门领导串
				 vo.setDepartLeaderIds(""+map.get("DEPT_LEADER_IDS"));
				 //分管领导串
				 vo.setChargeLeaderIds(""+map.get("CHARGE_LEADER_IDS"));
				 //兼职组织串
				 vo.setSidelineorg(map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString());
				 //职务名称
				 vo.setDutyName(map.get("EMPDUTY")==null?"":map.get("EMPDUTY").toString());
			 
				 orgVO=new OrgInfoVO();
				 orgVO.setOrgId(map.get("ORG_ID")==null?"":map.get("ORG_ID").toString());
				 orgVO.setOrgLayerName(""+map.get("ORGNAMESTRING"));
				 orgVO.setOrgName(""+map.get("ORGNAME"));
				 orgVO.setOrgSerial(""+map.get("ORGSERIAL")); 
				 orgVO.setOrgLevel(map.get("ORGLEVEL")==null?"":map.get("ORGLEVEL").toString());
				 orgVO.setOrgIdString(map.get("ORGIDSTRING")==null?"":map.get("ORGIDSTRING").toString());
				 vo.setOrgVO(orgVO);
				 userInfos.add(vo);
			 }
		 }
		 return userInfos;
	}
	
	
	/**
	 * 根据多个帐号取多个人员信息   包括人员的领导信息 拼成UserInfoVO
	 */
	public List<UserInfoVO> getUserInfoVOsByAccounts_order(String accounts) {
		List<UserInfoVO> userInfos=new ArrayList<UserInfoVO>();
		 UserInfoVO vo=null;
		 OrgInfoVO orgVO=null;
		 List<Map> list=new ArrayList<Map>();
		 //String sql=" select emp_id , empName ,useraccounts from  org_employee  useraccounts='"+useraccount+"'";	     
		 String sql="select emp.EMP_ID,emp.EMPNAME,emp.USERACCOUNTS ,"+
		            "org.ORGSERIAL , org.ORG_ID ,org.ORGNAME  ,"+
		            "org.ORGNAMESTRING , emp.DEPT_LEADER_IDS,emp.CHARGE_LEADER_IDS,"+
		            "EMPLEADERID, emp.SIDELINEORG ,org.ORGLEVEL, "+
		            "org.ORGIDSTRING ,emp.EMPDUTY "+
 		            " FROM org_employee  emp ,ORG_ORGANIZATION_USER u,ORG_ORGANIZATION org"+
                    " WHERE  emp.emp_id=u.emp_id and  org.org_id=u.org_id and emp.useraccounts in("+accounts+")"+
 		            " and  emp.userIsDeleted=0 and emp.USERISACTIVE=1";
		 
		 String orderby = ""; 
	 
		 String participantUserArr[] = accounts.split(",");
		 orderby = " order   by   case   emp.useraccounts ";
		 for (int k = 0; k < participantUserArr.length; k++) {
		  	 orderby += " when " + participantUserArr[k] + " then " + (k + 1)+ " ";
		 }
		 orderby += " end ";
		
		 sql=sql+orderby; 
		 list=new UtilService().searchBySql(sql);
		 //list=searchBySql(sql);
		 if(list!=null&&list.size()>0){
			 for(Map map:list){
				 vo=new UserInfoVO();
				 vo.setUserAccount(map.get("USERACCOUNTS").toString());
				 vo.setUserId(map.get("EMP_ID")==null?"":map.get("EMP_ID").toString());
				 vo.setUserName(""+map.get("EMPNAME"));
				 //上级领导串
				 vo.setLeaderIds(map.get("EMPLEADERID")==null?"":map.get("EMPLEADERID").toString());
				 //部门领导串
				 vo.setDepartLeaderIds(""+map.get("DEPT_LEADER_IDS"));
				 //分管领导串
				 vo.setChargeLeaderIds(""+map.get("CHARGE_LEADER_IDS"));
				 //兼职组织串
				 vo.setSidelineorg(map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString());
				 //职务名称
				 vo.setDutyName(map.get("EMPDUTY")==null?"":map.get("EMPDUTY").toString());
			 
				 orgVO=new OrgInfoVO();
				 orgVO.setOrgId(map.get("ORG_ID")==null?"":map.get("ORG_ID").toString());
				 orgVO.setOrgLayerName(""+map.get("ORGNAMESTRING"));
				 orgVO.setOrgName(""+map.get("ORGNAME"));
				 orgVO.setOrgSerial(""+map.get("ORGSERIAL")); 
				 orgVO.setOrgLevel(map.get("ORGLEVEL")==null?"":map.get("ORGLEVEL").toString());
				 orgVO.setOrgIdString(map.get("ORGIDSTRING")==null?"":map.get("ORGIDSTRING").toString());
				 vo.setOrgVO(orgVO);
				 userInfos.add(vo);
			 }
		 }
		 return userInfos;
	}
	
	
  
	
	
	/**
	 * 根据多个帐号取多个人员信息   包括人员的领导信息 拼成UserInfoVO
	 */
	public List<UserInfoVO> getUserInfoVOsByEmpIds(String empIds) {
		List<UserInfoVO> userInfos=new ArrayList<UserInfoVO>();
		if(empIds==null||empIds.equals("")||empIds.equals("null")){
			return userInfos;
		}
		 UserInfoVO vo=null;
		 OrgInfoVO orgVO=null;
		 List<Map> list=new ArrayList<Map>();
		 //String sql=" select emp_id , empName ,useraccounts from  org_employee  useraccounts='"+useraccount+"'";	     
		 String sql="select emp.EMP_ID,emp.EMPNAME,emp.USERACCOUNTS ,"+
		            "org.ORGSERIAL , org.ORG_ID ,org.ORGNAME  ,"+
		            "org.ORGNAMESTRING , emp.DEPT_LEADER_IDS,emp.CHARGE_LEADER_IDS,"+
		            "EMPLEADERID, emp.SIDELINEORG ,org.ORGLEVEL, "+
		            "org.ORGIDSTRING ,emp.EMPDUTY "+
 		            " FROM org_employee  emp ,ORG_ORGANIZATION_USER u,ORG_ORGANIZATION org"+
                    " WHERE  emp.emp_id=u.emp_id and  org.org_id=u.org_id and emp.EMP_ID  in("+empIds+")"+
 		            " and  emp.userIsDeleted=0 and emp.USERISACTIVE=1 ";
		 sql+=" order   by   case   emp.EMP_ID ";
		 String tmp[]=empIds.split(",");
         for(int k=0;k<tmp.length;k++){
             sql += " when "+tmp[k]+ " then "+(k+1)+ " ";
         }
         sql += " end ";
         
         logger.debug("getUserInfoVOsByEmpIds sql:"+sql);
		 
		 
		 list=new UtilService().searchBySql(sql);
		 //list=searchBySql(sql);
		 if(list!=null&&list.size()>0){
			 for(Map map:list){
				 vo=new UserInfoVO();
				 vo.setUserAccount(map.get("USERACCOUNTS").toString());
				 vo.setUserId(map.get("EMP_ID")==null?"":map.get("EMP_ID").toString());
				 vo.setUserName(""+map.get("EMPNAME"));
				 //上级领导串
				 vo.setLeaderIds(map.get("EMPLEADERID")==null?"":map.get("EMPLEADERID").toString());
				 //部门领导串
				 vo.setDepartLeaderIds(""+map.get("DEPT_LEADER_IDS"));
				 //分管领导串
				 vo.setChargeLeaderIds(""+map.get("CHARGE_LEADER_IDS"));
				 //兼职组织串
				 vo.setSidelineorg(map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString());
				 //职务名称
				 vo.setDutyName(map.get("EMPDUTY")==null?"":map.get("EMPDUTY").toString());
			 
				 orgVO=new OrgInfoVO();
				 orgVO.setOrgId(map.get("ORG_ID")==null?"":map.get("ORG_ID").toString());
				 orgVO.setOrgLayerName(""+map.get("ORGNAMESTRING"));
				 orgVO.setOrgName(""+map.get("ORGNAME"));
				 orgVO.setOrgSerial(""+map.get("ORGSERIAL")); 
				 orgVO.setOrgLevel(map.get("ORGLEVEL")==null?"":map.get("ORGLEVEL").toString());
				 orgVO.setOrgIdString(map.get("ORGIDSTRING")==null?"":map.get("ORGIDSTRING").toString());
				 vo.setOrgVO(orgVO);
				 userInfos.add(vo);
			 }
		 }
		 return userInfos;
	}
	
	
	/**
	 * 根据职务取职务级别
	 * @param inMap   key:dutyName 职务名
	 * @return  Map   dutyLevel  职务级别
	 */
	public Map<String,Object>getDutyLevelBydutyName_out(Map<String,Object> inMap){
		String dutyName=inMap.get("dutyName").toString();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("dutyLevel", getDutyLevelBydutyName(dutyName));
		return resultMap;	
	}
	
	/**
	 * 根据职务取职务级别
	 * @param dutyName 职务名
	 * @return
	 */
	public String getDutyLevelBydutyName(String dutyName){
		String dutyLevel="0";
		//为空
		if(!EzFlowUtil.judgeNull(dutyName)){
			return dutyLevel;
		}
		
		String sql="SELECT   DUTYLEVEL FROM   OA_DUTY   WHERE  DUTYNAME='"+dutyName+"'";
		List<Map> list=new UtilService().searchBySql(sql);
		if(list!=null&&list.size()>0){
			Map map=list.get(0);
			dutyLevel=map.get("DUTYLEVEL")==null?"0":map.get("DUTYLEVEL").toString();
		}	
		return dutyLevel;
	}
 
   
	/**
	 * 根据多个帐号取多个人员信息   包括人员的领导信息 拼成UserInfoVO  过滤了不存在的用户等
	 * @param varMap
	 * @return
	 */
	public Map<String,Object> getUserInfoVOsByAccounts_out(Map<String,Object> varMap) {
		Map<String,Object> resultMap=new HashMap<String,Object>();
		String userAccounts=varMap.get("userAccounts")==null?"":varMap.get("userAccounts").toString();
		List<UserInfoVO> list=getUserInfoVOsByAccounts(userAccounts);
		resultMap.put("userList", list);
		return resultMap;
	}
	
	/**
	 * 根据多个empId 取多个人员信息   包括人员的领导信息 拼成UserInfoVO  过滤了不存在的用户等
	 */
	public Map<String,Object>getUserInfoVOsByEmpIds_out(Map<String,Object> inMap){
		String empIds=inMap.get("empIds").toString();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("userList", getUserInfoVOsByEmpIds(empIds));
		return resultMap;	
	}
	
	
	
	/**
	 * TransactorService 用到  
	 * 根据用户Account查询其相关信息
	 * @param map
	 * @return
	 */
	public  Map getEmployeeInfoByUserAccount(String useraccounts){
		 DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
	     String query = "whir_selectBySql";
	     Map<String,Object>  queryMap = new HashMap<String,Object>();
	     String sql = " select DEPT_LEADER_IDS,CHARGE_LEADER_IDS,EMPLEADERID from org_employee where useraccounts='"+useraccounts+"'" ;
	     queryMap.put("sql", sql);
	     @SuppressWarnings("unused")
		 List<Map> list = session.selectList(query, queryMap);
	     Map userInfoMap = list.get(0);
	     return userInfoMap;
	}
	
	
	
	/**
	 * 根据 orgIdString  userId  取 范围内的 组织 与群组
	 * @param inMap    resultMap.put("myGroupList", myGroupList);    每项存@群组code@ 
                       resultMap.put("myOrgList", myOrgList);        每项存*组织编码*
	 * @return
	 */
	public  Map   getOrgsAndGroupsById(Map inMap){
	     logger.debug("开始取 范围信息:");
		 Map resultMap=new HashMap();
		 UtilService utilService=new UtilService();
		
		 List<String>myOrgList=new ArrayList();
		
		 String orgIdString=inMap.get("orgIdString")==null?"":inMap.get("orgIdString").toString();	
		 String userId=inMap.get("userId")==null?"":inMap.get("userId").toString();
		 String userAccount=inMap.get("userAccount")==null?"":inMap.get("userAccount").toString();
		
		 DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
		
		 String databaseType=session.getDbSqlSessionFactory().getDatabaseType();
		 
		 //取所有组织
		 String orgSql=" select aaa.ORGSERIAL  from  org_organization  aaa where ";		
         if(databaseType.indexOf("mysql")>=0){
        	 orgSql += " '" + orgIdString + "' like concat('%$', aaa.org_Id, '$%')";
         }
         else if(databaseType.indexOf("db2")>=0){
        	 orgSql +=  " locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$'),'" +
                       orgIdString + "')>0";
         }else{
        	 orgSql +=   " '" + orgIdString + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$%')";
         }
 
         List<Map> orgList=utilService.searchBySql(orgSql);
         if(orgList!=null){
        	 for(Map map:orgList){
        		 myOrgList.add("%*"+map.get("ORGSERIAL").toString()+"*%");
        	 } 	 
         } 
         //判断兼职部门范围
         String sidelineOrgSql= "select  SIDELINEORG  from org_employee aaa ";
         if(EzFlowUtil.judgeNull(userId)){
        	 sidelineOrgSql+="where aaa.emp_Id="+userId;
         }else if(EzFlowUtil.judgeNull(userAccount)){
        	 sidelineOrgSql+="where aaa.useraccounts='"+userAccount+"'";
         }else{
        	 
         }
         
         logger.debug("sidelineOrgSql:"+sidelineOrgSql);
         
         String inOrg="";
         List<Map> sidelineList=utilService.searchBySql(sidelineOrgSql);
         if(sidelineList!=null){
        	 String sidelineOrg="";
        	 for(Map map:sidelineList){ 
        		 if(map!=null){
        			 logger.debug("map is not null");
        		     sidelineOrg=map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString();
        		     logger.debug("sidelineOrg每个："+sidelineOrg);
        		 }else{
        			 logger.debug("map is null");
                 }
        	 }
        	 
        	 logger.debug("sidelineOrg总个:"+sidelineOrg);
        	 if(EzFlowUtil.judgeNull(sidelineOrg)){ 
	             String[] sidelineOrgArr = sidelineOrg.split("\\*\\*");
	             for(int i = 0; i < sidelineOrgArr.length; i ++){
	                 sidelineOrgArr[i] = sidelineOrgArr[i].replaceAll("\\*","");
	               	 logger.debug(" sidelineOrgArr[i]:"+ sidelineOrgArr[i]);
	                 inOrg+=sidelineOrgArr[i]+",";
	             }
	             if(inOrg.endsWith(",")){
	            	 inOrg=inOrg.substring(0,inOrg.length()-1);
	             }
	             logger.debug("inOrg:"+inOrg);
	             
	             //取兼职组织的 code 以及 orgidString 
	             String  ssql=" select aaa.ORGSERIAL,aaa.ORGIDSTRING  "+
	                          " from  org_organization  aaa where aaa.org_id in ("+inOrg+")";
	             
	             logger.debug("ssql:"+ssql);
	             List<Map> sList=utilService.searchBySql(ssql);
	             if(sList!=null){
	            	 String ORGSERIAL="";
	            	 String sideorgIdstring="";
	            	 for(Map map:sList){
	            		 ORGSERIAL=map.get("ORGSERIAL").toString();
	            		 if(!myOrgList.contains("%*"+ORGSERIAL+"*%")){
	            		 //if(true){
	            			 myOrgList.add("%*"+ORGSERIAL+"*%");           			 
	            			 //------------------------     根据兼职组织 找上级   start-------------------------------
	            			 sideorgIdstring=map.get("ORGIDSTRING").toString();
		            		 
		            		 //取所有组织
		            		 String sideorgSql=" select aaa.ORGSERIAL  from  org_organization  aaa where ";		
		                     if(databaseType.indexOf("mysql")>=0){
		                    	 sideorgSql += " '" + sideorgIdstring + "' like concat('%$', aaa.org_Id, '$%')";
		                     }else if(databaseType.indexOf("db2")>=0){
		                    	 sideorgSql +=  " locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$'),'" +
		                    			 sideorgIdstring + "')>0";
		                     }else{
		                    	 sideorgSql +=   " '" + sideorgIdstring + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$%')";
		                     }
		             
		                     List<Map> sideorgList=utilService.searchBySql(sideorgSql);
		                     if(sideorgList!=null){
		                    	 for(Map sidemap:sideorgList){
		                    		 if(!myOrgList.contains("%*"+sidemap.get("ORGSERIAL")+"*%")){
		                    		      myOrgList.add("%*"+sidemap.get("ORGSERIAL").toString()+"*%");
		                    		 }
		                    	 } 	 
		                     }	                     
		                     //-------------------------   根据兼职组织 找上级   end -----------------------------------
	            		 }else{
	            			 logger.debug("已经包含:"+ORGSERIAL);
	            		 }	            		
	            	 } 	 
	             }
             }
         }else{     	 
        	 logger.debug("sidelineOrg is null ");
         }
 
  
         List<String> myGroupList=new ArrayList<String>();
         String groupSql=" select aaa.GROUP_CODE "+
                         " from   org_group aaa ,  org_user_group ab, org_employee  bbb  where "+
        		         " aaa.group_id=ab.group_id  and  ab.emp_id =bbb.emp_id  and group_code is not null ";
         
         if(EzFlowUtil.judgeNull(userId)){
        	 groupSql+=" and  bbb.emp_Id="+userId;
         }else if(EzFlowUtil.judgeNull(userAccount)){
        	 groupSql+=" and  bbb.useraccounts='"+userAccount+"'";
         }else{    	 
         }
         
         List<Map> groupList=utilService.searchBySql(groupSql);
         if(groupList!=null){
        	 for(Map map:groupList){
        		 myGroupList.add("%@"+map.get("GROUP_CODE").toString()+"@%");
        	 } 	 
         }
 
         resultMap.put("myGroupList", myGroupList);
         resultMap.put("myOrgList", myOrgList);
		return resultMap;	
	}
	
 
	
	/**
	 * 根据 orgIdString  userId  取 范围内的 组织 与群组 以供拼接 范围sql   此方法返回是 ID
	 * @param inMap    resultMap.put("myGroupList", myGroupList);     每项存@群组ID@ 
                       resultMap.put("myOrgList", myOrgList);         每项存*组织ID*
	 * @return
	 */
	public  Map   getScopeWithId(Map inMap){
		 Map resultMap=new HashMap();
		 UtilService utilService=new UtilService();		
		 List<String>myOrgList=new ArrayList();
		
		 String orgIdString=inMap.get("orgIdString")==null?"":inMap.get("orgIdString").toString();	
		 String userId=inMap.get("userId")==null?"":inMap.get("userId").toString();
		 String userAccount=inMap.get("userAccount")==null?"":inMap.get("userAccount").toString();
		 
		 logger.debug(" getScopeWithId orgIdString:"+orgIdString);
		 logger.debug(" getScopeWithId  userId:"+userId);
		 logger.debug("getScopeWithId  userAccount:"+userAccount); 
		
		 DbSqlSession session=Context.getCommandContext().getSession(DbSqlSession.class);
		
		 String databaseType=session.getDbSqlSessionFactory().getDatabaseType();
		 
		 //取所有组织
		 String orgSql=" select aaa.ORG_ID  from  org_organization  aaa where ";		
         if(databaseType.indexOf("mysql")>=0){
        	 orgSql += " '" + orgIdString + "' like concat('%$', aaa.org_Id, '$%')";
         }
         else if(databaseType.indexOf("db2")>=0){
        	 orgSql +=  " locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$'),'" +
                       orgIdString + "')>0";
         }else{
        	 orgSql +=   " '" + orgIdString + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$%')";
         }
         
         List<Map> orgList=utilService.searchBySql(orgSql);
         if(orgList!=null){
        	 for(Map map:orgList){
        		 myOrgList.add("%*"+map.get("ORG_ID").toString()+"*%");
        	 } 	 
         }
     
         //判断兼职部门范围
         String sidelineOrgSql= "select aaa.SIDELINEORG  from org_employee aaa ";
         if(EzFlowUtil.judgeNull(userId)){
        	 sidelineOrgSql+="where aaa.emp_Id="+userId;
         }else if(EzFlowUtil.judgeNull(userAccount)){
        	 sidelineOrgSql+="where aaa.useraccounts='"+userAccount+"'";
         }else{
        	 
         }
         
         String inOrg="";
         List<Map> sidelineList=utilService.searchBySql(sidelineOrgSql);
         if(sidelineList!=null){
        	 String sidelineOrg="";
        	 for(Map map:sidelineList){
        		 if(map!=null){
        		     sidelineOrg=map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString();
        		 }else{
        			 System.out.println("  map is null");
        		 }
        	 }
        	 if(EzFlowUtil.judgeNull(sidelineOrg)){
	             String[] sidelineOrgArr = sidelineOrg.split("\\*\\*");
	             for(int i = 0; i < sidelineOrgArr.length; i ++){
	                 sidelineOrgArr[i] = sidelineOrgArr[i].replaceAll("\\*","");
	                 inOrg+=sidelineOrgArr[i]+",";
	             }
	             if(inOrg.endsWith(",")){
	            	 inOrg=inOrg.substring(0,inOrg.length()-1);
	             }
	             String  ssql=" select aaa.ORG_ID,aaa.ORGIDSTRING  from  org_organization  aaa where aaa.org_id in ("+inOrg+")";
	             List<Map> sList=utilService.searchBySql(ssql);
	             if(sList!=null){
	            	 String ORGSERIAL="";
	            	 String sideorgIdstring="";
	            	 for(Map map:sList){
	            		 ORGSERIAL=map.get("ORG_ID").toString();
	            		 if(!myOrgList.contains("%*"+ORGSERIAL+"*%")){
	            			 myOrgList.add("%*"+ORGSERIAL+"*%");
	            			 
	            			 //------------------------     根据兼职组织 找上级   start-------------------------------
	            			 sideorgIdstring=map.get("ORGIDSTRING").toString();
		            		 
		            		 //取所有组织
		            		 String sideorgSql=" select aaa.ORG_ID  from  org_organization  aaa where ";		
		                     if(databaseType.indexOf("mysql")>=0){
		                    	 sideorgSql += " '" + sideorgIdstring + "' like concat('%$', aaa.org_Id, '$%')";
		                     }else if(databaseType.indexOf("db2")>=0){
		                    	 sideorgSql +=  " locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$'),'" +
		                    			 sideorgIdstring + "')>0";
		                     }else{
		                    	 sideorgSql +=   " '" + sideorgIdstring + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.org_Id)), '$%')";
		                     }
		             
		                     List<Map> sideorgList=utilService.searchBySql(sideorgSql);
		                     if(sideorgList!=null){
		                    	 for(Map sidemap:sideorgList){
		                    		 if(!myOrgList.contains("%*"+sidemap.get("ORG_ID")+"*%")){
		                    		      myOrgList.add("%*"+sidemap.get("ORG_ID").toString()+"*%");
		                    		 }
		                    	 } 	 
		                     }	                     
		                     //-------------------------   根据兼职组织 找上级   end -----------------------------------
	            		 }
	            	 } 	 
	             }
             }
         }    
          
         List<String> myGroupList=new ArrayList<String>();
         String groupSql=" select aaa.GROUP_ID "+
                         " from   org_group aaa ,  org_user_group ab, org_employee  bbb  where "+
        		         " aaa.group_id=ab.group_id  and  ab.emp_id =bbb.emp_id  and group_code is not null ";
         
         if(EzFlowUtil.judgeNull(userId)){
        	 groupSql+=" and  bbb.emp_Id="+userId;
         }else if(EzFlowUtil.judgeNull(userAccount)){
        	 groupSql+=" and  bbb.useraccounts='"+userAccount+"'";
         }else{    	 
         }
         
         List<Map> groupList=utilService.searchBySql(groupSql);
         if(groupList!=null){
        	 for(Map map:groupList){
        		 myGroupList.add("%@"+map.get("GROUP_ID").toString()+"@%");
        	 } 	 
         }      
         resultMap.put("myGroupList", myGroupList);
         resultMap.put("myOrgList", myOrgList);
         
         /*if(myOrgList!=null){
        	  for(int i=0;i<myOrgList.size();i++){  
        			 System.out.println("org each:"+myOrgList.get(i));  
        	  }  
         }*/
		return resultMap;	
	}
    
    
	
	/**
	 * 把 组织编码换为 orgId
	 * @param inMap
	 * @return
	 */
	public Map<String,Object>dealOrgserialtoOrgId_out(Map<String,Object> inMap){
		String orgSerials=inMap.get("orgSerials").toString();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("result", dealOrgserialtoOrgId(orgSerials));
		return resultMap;	
	}
	
	
	/**
	 * 把 组织编码换为 orgId
	 * @param orgSerials
	 * @return
	 */
	public String dealOrgserialtoOrgId(String orgSerials ){
		String orgIdstrs="";
		if(EzFlowUtil.judgeNull(orgSerials)){	
	      	String inorg=EzFlowUtil.dealStrForIn(orgSerials, '*', "'");
	      	String orgSql="select ORG_ID  from  org_organization  where  orgserial in ("+inorg+")";
	      	List<Map> orgList=new UtilService().searchBySql(orgSql);
	        if(orgList!=null){
	            for(Map map:orgList){
	        	   orgIdstrs+="*"+ map.get("ORG_ID")+"*";
	        	} 	 
	        }
		}
		return orgIdstrs;
	}
	
	/**
	 * 把帐号转化为 id
	 * @param inMap
	 * @return
	 */
	public Map<String,Object>dealGroupCodeTogGroupId_out(Map<String,Object> inMap){
		String groupCodes=inMap.get("groupCodes").toString();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("result", dealGroupCodeTogGroupId(groupCodes));
		return resultMap;	
	}
	
	/**
	 * 把群组cod转化为id
	 * @param groupCodes
	 * @return
	 */
	public String dealGroupCodeTogGroupId(String groupCodes){
		String groupIds="";
		if(EzFlowUtil.judgeNull(groupCodes)){	
	      	String incodes=EzFlowUtil.dealStrForIn(groupCodes, '@', "'");
	    	String gropuSql="select GROUP_ID  from   org_group    where  group_code in ("+incodes+")";
	      	List<Map> groupList=new UtilService().searchBySql(gropuSql);
	        if(groupList!=null){
	            for(Map map:groupList){
	            	groupIds+="@"+ map.get("GROUP_ID")+"@";
	        	} 	 
	        }
		}
		return groupIds;
	}
	
	
	/**
	 * 把帐号转化为 id
	 * @param inMap
	 * @return
	 */
	public Map<String,Object>dealUserAccountToUserId_out(Map<String,Object> inMap){
		String userAccountStrs=inMap.get("userAccountStrs").toString();
		Map<String,Object> resultMap=new HashMap<String,Object>();
		resultMap.put("result", dealUserAccountToUserId(userAccountStrs));
		return resultMap;	
	}
	/**
	 * 把帐号转化为 id
	 * @param userAccountStrs
	 * @return
	 */
	public String dealUserAccountToUserId(String userAccountStrs){
		String userIds="";
		if(EzFlowUtil.judgeNull(userAccountStrs)){	
	      	String incodes=EzFlowUtil.dealStrForIn(userAccountStrs, '$', "'");
	      	String userSql=" select EMP_ID   from org_employee  where  useraccounts in ("+incodes+")  and userIsDeleted=0 and USERISACTIVE=1 ";
	      	List<Map> userList=new UtilService().searchBySql(userSql);
	        if(userList!=null){
	            for(Map map:userList){
	            	userIds+="$"+ map.get("EMP_ID")+"$";
	        	} 	 
	        }
		}	
		return userIds;
	}
	
	/**
	 * 判断用户帐号是否禁用
	 * @param userId
	 * @return boolean true：正常用户 false：禁用用户
	 */
	public boolean isActiveUser(String userId){
		boolean ret =false;
		if(EzFlowUtil.judgeNull(userId)){
			String userSql="select EMP_ID,useraccounts from org_employee  where  EMP_ID ="+userId+" and userIsDeleted=0 and USERISACTIVE=1";
			List<Map> userList=new UtilService().searchBySql(userSql);
			if(userList!=null && userList.size() >0){
				ret =true;
			}
		}
		return ret;
	}
	
	
	public  String dealSidelineorgToCodes(String sidelineorg){
		String orgCodes="";
		if(EzFlowUtil.judgeNull(sidelineorg)){	
	      	String incodes=EzFlowUtil.dealStrForIn(sidelineorg, '*', null);
	      	String orgSql=" select ORGSERIAL  from  org_organization where org_id  in ("+incodes+")";
	      	List<Map> orgList=new UtilService().searchBySql(orgSql);
	        if(orgList!=null){
	            for(Map map:orgList){
	            	orgCodes+="*"+ map.get("ORGSERIAL")+"*";
	        	} 	 
	        }
		}	
		return orgCodes;
	}
	 
	
	public  Map test(Map map){
        //判断兼职部门范围
        String sidelineOrgSql= "select aaa.SIDELINEORG   from org_employee aaa ";       
       	sidelineOrgSql+="where aaa.useraccounts='gyb'";         
        List<Map> sidelineList=new UtilService().searchBySql(sidelineOrgSql);
        
        for(Map mmap:sidelineList){
   		   String  sidelineOrg=mmap.get("SIDELINEORG")==null?"":mmap.get("SIDELINEORG").toString();
        }
	    return  new HashMap();
	}
 
	
	/**
	 * 根据岗位取人
	 * @param useraccount
	 * @return
	 */
	public List<UserInfoVO>  getUserInfoVOByStationInfo(List <String []>stationList) {
		List<UserInfoVO>  ulist=new ArrayList<UserInfoVO> (); 
		 UserInfoVO vo=null;
		 OrgInfoVO orgVO=new OrgInfoVO();
		 List<Map> list=new ArrayList<Map>();	       
		 String sql="select emp.EMP_ID ,emp.EMPNAME ,emp.USERACCOUNTS,"+  
		 " emp.DEPT_LEADER_IDS,emp.CHARGE_LEADER_IDS,emp.EMPLEADERID,  "+
		 " emp.SIDELINEORG,emp.EMPDUTY "+
		 " FROM org_employee  emp ,ORG_ORGANIZATION_USER u,gj_station station"+  
         " WHERE  emp.emp_id=u.emp_id   and emp.empposition=station.STATION_NAME "+
         " and  emp.userIsDeleted=0 and emp.USERISACTIVE=1";
		 if(stationList!=null&&stationList.size()>0){
			 sql+=" and ( 1>2 ";
			 for(String[] str:stationList){
				 sql+="  or ( u.org_id= "+str[1]+"  and   station.id ="+str[0]+" )";
			 }
			 sql+=" )";
		 } 
		 list=new UtilService().searchBySql(sql);
		 //list=searchBySql(sql);
		 if(list!=null&&list.size()>0){
			 for(Map map:list){ 
				 vo=new UserInfoVO();
				 vo.setUserAccount(map.get("USERACCOUNTS")==null?"":map.get("USERACCOUNTS").toString());
				 vo.setUserId(map.get("EMP_ID")==null?"":map.get("EMP_ID").toString());
				 vo.setUserName(map.get("EMPNAME")==null?"":map.get("EMPNAME").toString());
				 //上级领导串
				 vo.setLeaderIds(""+map.get("EMPLEADERID"));
				 //部门领导串
				 vo.setDepartLeaderIds(""+map.get("DEPT_LEADER_IDS"));
				 //分管领导串
				 vo.setChargeLeaderIds(""+map.get("CHARGE_LEADER_IDS"));		 
				 vo.setDutyName(map.get("EMPDUTY")==null?"":map.get("EMPDUTY").toString());		 
				//兼职组织串
				 vo.setSidelineorg(map.get("SIDELINEORG")==null?"":map.get("SIDELINEORG").toString()); 	
				 ulist.add(vo); 
			 } 
		 } 
		 return ulist;
	}
	
	
	/**
	 * 
	 * @param orgId
	 * @param stationList
	 * @return
	 */
	public List<UserInfoVO>  getUserInfoVOByStationInfoAndOrgId(String orgId,List <String []>stationList) {
		 List<UserInfoVO>  ulist=new ArrayList<UserInfoVO> (); 
		 UserInfoVO vo=null;
		 OrgInfoVO orgVO=new OrgInfoVO();
		 List<Map> list=new ArrayList<Map>(); 
		 String stationSql=" "; 
		 if(stationList!=null&&stationList.size()>0){
			 stationSql+=" and ( 1>2 ";
			 for(String[] str:stationList){
				 stationSql+="  or ( C.ORG_ID= "+str[1]+"  and   B.id ="+str[0]+" )";
			 }
			 stationSql+=" )";
		 } 
		  
		  String sql = "SELECT DISTINCT A.EMP_ID  ,A.EMPNAME , A.USERACCOUNTS  ,A.USERORDERCODE ,A.EMPDUTYLEVEL"+
          " FROM EZOFFICE.ORG_EMPLOYEE A left join org_sideline D on A.EMP_ID=D.EMP_ID, gj_station B, ORG_ORGANIZATION_USER C"+
          " WHERE A.EMPPOSITION=B.STATION_NAME AND A.EMP_ID=C.EMP_ID  " + stationSql + 
          " AND A.userIsDeleted=0 and A.USERISACTIVE=1 "+
          " AND (C.ORG_ID IN (SELECT ORG_ID FROM EZOFFICE.ORG_ORGANIZATION WHERE ORGIDSTRING LIKE '%$" + orgId + "$%')" +
          " or D.ORG_ID IN (SELECT ORG_ID FROM EZOFFICE.ORG_ORGANIZATION WHERE ORGIDSTRING LIKE '%$" + orgId + "$%'))" +
          " order by A.EMPDUTYLEVEL,A.USERORDERCODE,A.EMPNAME ";  
		  
		 list=new UtilService().searchBySql(sql);
		 //list=searchBySql(sql);
		 if(list!=null&&list.size()>0){
			 for(Map map:list){ 
				 vo=new UserInfoVO(map.get("EMP_ID")+"",map.get("EMPNAME")+"",map.get("USERACCOUNTS")+""); 
				 ulist.add(vo); 
			 } 
		 } 
		 return ulist;
	}
	
	
 
}
