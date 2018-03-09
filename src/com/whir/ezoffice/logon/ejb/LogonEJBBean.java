package com.whir.ezoffice.logon.ejb;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.log4j.Logger;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.StringSplit;

/**
 *
 * <p>Title: 用户登陆时的身份认证类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: WanHu Internet Resource(Hefei) C0. Ltd</p>
 * @version 1.0
 */
public class LogonEJBBean extends HibernateBase implements SessionBean {
    
    private static Logger logger = Logger.getLogger(LogonEJBBean.class.getName());
    
    SessionContext sessionContext;
    public void ejbCreate() throws CreateException {
    }

    public void ejbRemove() {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
    /**
     * 根据用户帐户、密码判断用户权限、记录用户登陆信息
     * @param userName String 用户名
     * @param userPassword String 用户密码
     * @param userIP String 用户IP
     * @param domainAccount 单位帐号
     * @param flag 标识  0:无需要验证密码 1:需要密码验证
     * @throws Exception
     * @return HashMap
     */
    public HashMap logon(String userName, String userPassword, String userIP, String domainAccount,String needPass) throws Exception {
        HashMap userInfo=new HashMap();
        //MD5 md5 = new MD5();
        Object[] obj;
        List list = null;
        
        //---------------------------------------------------------------
        //账号是否区分大小写，注意：needPass 包含 - 时，则表示是否区分大小
        String accountscase = "1";
        if(needPass != null && needPass.indexOf("-") != -1){
            accountscase = needPass.substring(needPass.indexOf("-") + 1);
            needPass = needPass.substring(0, needPass.indexOf("-"));
        }
        //---------------------------------------------------------------

        this.begin();
        try {
            String databaseType=com.whir.common.config.SystemCommon.getDatabaseType();
            String domainId = "-1";
            String userNum = "0";
        	domainAccount = "whir";
            System.out.println("domainAccount::::"+domainAccount);
            if(domainAccount != null && !"".equals(domainAccount) && !"null".equals(domainAccount)){
                Iterator iter = session.createQuery("select a.id,a.userNum,a.domainType,a.noLog,a.inUse,a.domainEndDate from com.whir.org.vo.organizationmanager.DomainVO a where a.domainAccount=:domainAccount and a.inUse=1").setString("domainAccount", domainAccount).iterate();
                if(iter.hasNext()){
                    Object[] tmpObj = (Object[]) iter.next();
                    domainId = tmpObj[0].toString();
                    userNum = tmpObj[1]==null?"0":tmpObj[1].toString();
                    userInfo.put("domainType",tmpObj[2]==null?"":tmpObj[2].toString());
                    userInfo.put("noLog",tmpObj[3]==null?"":tmpObj[3].toString());
                    String domainInUse=tmpObj[4]==null?"0":tmpObj[4].toString();
                    //System.out.println("date1:"+tmpObj[5]);
                    java.util.Date domainEndDate=(java.util.Date)tmpObj[5];
                    //System.out.println("date2:"+domainEndDate);
                    if("0".equals(domainInUse)){
                        userInfo.put("error","domainInUseError");
                        return userInfo;
                    }
                    if(domainEndDate!=null){
                        domainEndDate.setHours(23);
                        domainEndDate.setMinutes(59);
                        domainEndDate.setSeconds(59);
                        if((new java.util.Date()).after(domainEndDate)){
                            userInfo.put("error", "domainEndDateError");
                            return userInfo;
                        }
                    }
                }
            }
            System.out.println("domainId::::"+domainId);
            if("-1".equals(domainId)){
                userInfo.put("error", "domainError");
                return userInfo;
            }
            System.out.println("userNum::::"+userNum);
            if("0".equals(userNum)){
                userInfo.put("error", "userNumError");
                return userInfo;
            }

            Iterator tmpIter = session.iterate("select count(user.empId) from com.whir.org.vo.usermanager.UserPO user where user.userIsDeleted=0 and (user.userAccounts is not null and user.userAccounts<>'admin' and user.userAccounts<>'security') and user.userIsFormalUser=1 and user.domainId=" + domainId);
            String tmpUserNum = "0";
            if(tmpIter.hasNext()){
                tmpUserNum = tmpIter.next().toString();
            }
            
            if(Integer.parseInt(tmpUserNum) > Integer.parseInt(userNum)){
                if(userName.toLowerCase().equals("admin") || userName.toLowerCase().equals("system") || userName.toLowerCase().equals("security")){
                    userInfo.put("userNumOver","1");
                }else{
                    userInfo.put("error", "userNumError");
                    return userInfo;
                }
            }
            
            String collateSql = "";
            if(databaseType.indexOf("sqlserver") >= 0){
                collateSql = " COLLATE Chinese_PRC_CS_AS ";
            }
            userName = userName.replaceAll("'", "''");//过滤单引号
            
            String userAccount = userName;
            String upper_l_parenthesis = "";
            String upper_r_parenthesis = "";
            if("0".equals(accountscase)){//不区分大小写
                upper_l_parenthesis = "upper(";
                upper_r_parenthesis = ")";
                userAccount = userAccount.toUpperCase();
            }
            
            if(userName.toLowerCase().equals("admin") || userName.toLowerCase().equals("security")){
                list=session.createQuery("SELECT emp.userPassword,emp.keyValidate,emp.keySerial,emp.skin,emp.empId,emp.empName,emp.empNumber,emp.empBusinessPhone,emp.empPosition, emp.empIdCard, emp.userPageSize, emp.isChangePwd, emp.isPasswordRule, emp.empLivingPhoto, emp.pageFontsize,emp.userIsSleep FROM com.whir.org.vo.usermanager.UserPO emp WHERE "+upper_l_parenthesis+"emp.userAccounts"+upper_r_parenthesis+"=:userAccount"
                                          +collateSql+" and emp.domainId=:domainId").setString("userAccount", userAccount).setLong("domainId", Long.parseLong(domainId)).list();
                //                                     0                 1              2            3        4            5          6              7                 8                 9                10              11                 12                  13                14             15
                obj=(Object[])list.get(0);
              //判断用户是否休眠,否：0，是：1
                if("1".equals(obj[15].toString())){
                    userInfo.put("error", "sleep");
                    return userInfo;
                }
                
                //userPassword = md5.toMD5(userPassword);
                if (!(obj[0].toString()).equals(userPassword)) {
                    userInfo.put("error", "password");
                    
                }
                if("1".equals(obj[1].toString())){
                    userInfo.put("keySerial", obj[2]);
                }
                userInfo.put("userAccount", userName.toLowerCase());
                userInfo.put("userName", "");
                userInfo.put("skin", obj[3]);
                userInfo.put("userId",obj[4]);
                userInfo.put("userName",obj[5]);
                userInfo.put("domainId", domainId);
                userInfo.put("orgEnglishName", "");//组织英文名称
                userInfo.put("empNumber", obj[6]!=null?obj[6]:"");//工号
                userInfo.put("empBusinessPhone", obj[7]!=null?obj[7]:"");//商务电话
                userInfo.put("empPosition", obj[8]!=null?obj[8]:"");//岗位
                userInfo.put("empIdCard", obj[9]!=null&&!"null".equalsIgnoreCase(obj[9]+"")?obj[9]:"");//身份证号
                userInfo.put("userPageSize", obj[10]!=null?obj[10]+"":"");
                userInfo.put("isChangePwd", obj[11]!=null?obj[11]+"":"");
                userInfo.put("isPasswordRule", obj[12]!=null?obj[12]+"":"");
                userInfo.put("empLivingPhoto", obj[13]!=null?obj[13]+"":"");
                userInfo.put("pageFontsize", obj[14]!=null?obj[14]+"":"");

            }else{
                /*String collateSql = "";
                if(databaseType.indexOf("sqlserver") >= 0){
                    collateSql = " COLLATE Chinese_PRC_CS_AS ";
                }
                userName = userName.replaceAll("'", "");//过滤单引号
                
                String userAccount = userName;
                String upper_l_parenthesis = "";
                String upper_r_parenthesis = "";
                if("0".equals(accountscase)){//不区分大小写
                    upper_l_parenthesis = "upper(";
                    upper_r_parenthesis = ")";
                    userAccount = userAccount.toUpperCase();
                }*/
                list= session.createQuery("SELECT employee.userIsActive,employee.userIsSuper,employee.userPassword,employee.empId,employee.empName,employee.browseRange,org.orgId,org.orgIdString,employee.keyValidate,employee.keySerial,employee.userSuperBegin,employee.userSuperEnd,employee.userSimpleName,org.orgSerial,org.orgSimpleName,employee.skin,employee.empDuty,employee.empEnglishName,employee.userIsSleep,org.orgEnglishName,employee.empNumber,employee.empBusinessPhone,org.orgName,employee.empPosition, employee.mobileUserFlag, employee.empIdCard, employee.userAccounts, employee.userPageSize, employee.isChangePwd, employee.isPasswordRule, org.orgNameString, employee.imId, employee.empDutyLevel, employee.sidelineOrg, employee.sidelineOrgName, employee.empLivingPhoto, employee.pageFontsize,employee.enterprisenumber " +
                                          //                  0                    1                       2                  3             4                   5              6           7                    8                   9                  10                         11              12                    13             14              15             16                  17                    18                  19                 20                    21                   22             23                   24                        25                  26                      27                  28                 29                          30                 31                32                    33                        34                   35                             36                37
                                          " FROM com.whir.org.vo.usermanager.UserPO employee join employee.organizations org WHERE "+upper_l_parenthesis+"employee.userAccounts"+upper_r_parenthesis+"=:userAccount "+collateSql+" and employee.userIsDeleted=0 and employee.domainId=:domainId").setString("userAccount", userAccount).setLong("domainId", Long.parseLong(domainId)).list();
                
                if(list!=null && list.size()>0){
                    obj = (Object[]) list.get(0);
                    
                    //判断用户是否被禁用
                    if ("0".equals(obj[0].toString())) {
                        userInfo.put("error", "active");
                        return userInfo;
                    }
                    //判断用户是否休眠,否：0，是：1
                    if("1".equals(obj[18].toString())){
                        userInfo.put("error", "sleep");
                        return userInfo;
                    }

                    //判断用户密码是否正确
                    if("1".equals(needPass) || "9".equals(needPass)){//needPass="9" 移动办公 不判断特权用户
                        if (!(obj[2].toString()).equals(userPassword)) {
                            userInfo.put("error", "password");
                            userInfo.put("userId",obj[3]);
                            return userInfo;
                        }
                    }

                    //判断用户是否是特权用户 needPass=9的时候不判断特权用户
                    if(!"9".equals(needPass)){
                        java.util.Calendar date = java.util.Calendar.
                                                  getInstance();

                        if ("1".equals(obj[1].toString())) {
                            if (obj[10] != null) {
                                Date superBegin = (Date) obj[10];
                                Date superEnd = (Date) obj[11];
                                if (date.getTimeInMillis() > superEnd.getTime() ||
                                    date.getTimeInMillis() < superBegin.getTime()) {
                                    obj[1] = "0";
                                }
                            }
                        }

                        if(userIP != null &&
                           (userIP.toLowerCase().equals("localhost") ||
                            userIP.toLowerCase().startsWith("0:0:0:0"))) {
                            userIP = "127.0.0.1";
                        }

                        if("0".equals(obj[1].toString())) {
                            String today = (date.get(Calendar.YEAR)) + "/" +
                                           (date.get(Calendar.MONTH) + 1) + "/" +
                                           date.get(Calendar.DATE);
                            //格式化ip地址
                            String[] ipAddr = userIP.split("\\.");
                            StringBuffer ip = new StringBuffer(16);
                            int i, len;
                            for (i = 0; i < 4; i++) {
                                len = 3 - ipAddr[i].length();
                                while (len > 0) {
                                    ip.append("0");
                                    len--;
                                }
                                ip.append(ipAddr[i]).append(".");
                            }
                            userIP = ip.toString().substring(0, 15);
                            
                            logger.debug("userIP:"+userIP);
                            
                            String queryString = "";
                            if (databaseType.indexOf("mysql") >= 0) {
                                queryString = "SELECT COUNT(ip.id) FROM com.whir.ezoffice.security.ip.po.IPPO ip WHERE ip.domainId=:domainId and ip.ipIsOpen=1 AND (:today1>=ip.ipOpenBeginTime AND :today2<=ip.ipOpenEndTime AND (ip.ipAddressBegin=:userIP1 OR :userIP2 BETWEEN ip.ipAddressBegin AND ip.ipAddressEnd))";

                            } else {
                                queryString = "SELECT COUNT(ip.id) FROM com.whir.ezoffice.security.ip.po.IPPO ip WHERE ip.domainId=:domainId and ip.ipIsOpen=1 AND (EZOFFICE.FN_STRTODATE(:today1,'S')>=ip.ipOpenBeginTime AND EZOFFICE.FN_STRTODATE(:today2,'S')<=ip.ipOpenEndTime AND (ip.ipAddressBegin=:userIP1 OR :userIP2 BETWEEN ip.ipAddressBegin AND ip.ipAddressEnd))";
                            }
                            
                            logger.debug(queryString);

                            int count = ((Integer) session.createQuery(queryString).setLong("domainId", Long.parseLong(domainId)).setString("today1", today).setString("today2", today).setString("userIP1", userIP).setString("userIP2", userIP).iterate().
                                         next()).intValue();
                            if (count < 1) {
                                userInfo.put("error", "ip");
                                return userInfo;
                            }
                        }
                    }

                    if ("1".equals(obj[8].toString())) {
                        /*标准产品begin*/
                        userInfo.put("keySerial", obj[9]);
                        /*标准产品end*/
                    }

                    //记录用户信息
                    userInfo.put("domainId", domainId);
                    userInfo.put("userId", obj[3]);
                    userInfo.put("userName", obj[4]);
                    userInfo.put("browseRange", obj[5]);
                    userInfo.put("orgId", obj[6]);
                    userInfo.put("userSimpleName", obj[12]);
                    userInfo.put("orgSerial", obj[13]);
                    userInfo.put("orgSimpleName", obj[14]);
                    userInfo.put("skin", obj[15]);
                    userInfo.put("orgEnglishName", obj[19]!=null?obj[19]:"");//组织英文名称
                    userInfo.put("empNumber", obj[20]!=null?obj[20]:"");//工号
                    userInfo.put("empBusinessPhone", obj[21]!=null?obj[21]:"");//商务电话
                    userInfo.put("orgSelfName", obj[22]!=null?obj[22]:"");//组织名称
                    userInfo.put("empPosition", obj[23]!=null?obj[23]:"");//岗位
                    userInfo.put("mobileUserFlag", obj[24]!=null?obj[24]+"":"");//是否开通移动办公 1-是
                    userInfo.put("empIdCard", obj[25]!=null&&!"null".equalsIgnoreCase(obj[25]+"")?obj[25]:"");//身份证号
                    userInfo.put("realUserAccount", obj[26]!=null?obj[26]+"":"");//帐号 
                    userInfo.put("userPageSize", obj[27]!=null?obj[27]+"":"");
                    //isChangePwd, isPasswordRule
                    userInfo.put("isChangePwd", obj[28]!=null?obj[28]+"":"");
                    userInfo.put("isPasswordRule", obj[29]!=null?obj[29]+"":"");
                    
                    //英文名称，用于快客邮件帐号
                    userInfo.put("empEnglishName", obj[17]!=null?obj[17]:"");

                    String orgIdString = (String) obj[7];
                    userInfo.put("orgIdString", StringSplit.splitOrgIdString(orgIdString, "$", "_"));

                    //得到用户所属组织信息
                    /*String tmpSql = "";
                    if(databaseType.indexOf("mysql")>=0){
                        tmpSql = "SELECT org.orgName,org.orgId,org.orgType FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE '"
                                 + orgIdString +
                                "' LIKE concat('%$', org.orgId, '$%') ORDER BY org.orgLevel";
                    }else if(databaseType.indexOf("db2")>=0){
                        tmpSql="SELECT org.orgName,org.orgId,org.orgType FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(org.orgId)), '$%'),'"
                                 + orgIdString +

                                 "')>0 ORDER BY org.orgLevel";

                    }else{
                        tmpSql = "SELECT org.orgName,org.orgId,org.orgType FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE '"
                                 + orgIdString +
                                 "' LIKE EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(org.orgId)), '$%') ORDER BY org.orgLevel";
                    }*/

                    String rootCorpId="";
                    String corpId="";
                    String departId="";
                    /*Object[] orgObj;

                    list = session.createQuery(tmpSql).list();
                    orgIdString = "";
                    for (int i = 0; i < list.size(); i++) {
                        orgObj=(Object[])list.get(i);
                        orgIdString += orgObj[0].toString();
                        orgIdString += ".";
                        if(rootCorpId.equals("") && "0".equals(orgObj[2]+"")){
                            rootCorpId=orgObj[1].toString();
                        }
                        if(corpId.equals("") && "3".equals(orgObj[2]+"")){
                            corpId=orgObj[1].toString();
                        }
                        if(departId.equals("") && "2".equals(orgObj[2]+"")){
                            departId=orgObj[1].toString();
                        }
                    }
                    if (orgIdString.endsWith(".")) {
                        orgIdString = orgIdString.substring(0, orgIdString.length() - 1);
                    }
                    
                    userInfo.put("rootCorpId",rootCorpId);
                    userInfo.put("corpId",corpId);
                    userInfo.put("departId",departId);*/
                    
                    userInfo.put("rootCorpId",rootCorpId);
                    userInfo.put("corpId",corpId);
                    userInfo.put("departId",departId);

                    //userInfo.put("orgName", orgIdString);
                    userInfo.put("orgName", obj[30]!=null?obj[30]+"":"");
                    
                    //查看用户是否有系统管理的权限
                    userInfo.put("sysManager", "0");
                    /*
                    list = session.createQuery("SELECT r.rightId FROM com.whir.org.vo.rolemanager.RightScopeVO rightScope join rightScope.employee emp join rightScope.right r WHERE (r.rightCode='00*01*01' OR r.rightCode='00*01*02') AND emp.empId=" +
                                               obj[3] + " ORDER BY rightScope.rightScopeId DESC").list();
                    for (int i = 0; i < list.size(); i++) {
                        userInfo.put("sysManager", list.get(i));
                    }
                    */
                    
                    String sysManager = "0";
                    List rightsList = session.createQuery("select r.rightCode FROM com.whir.org.vo.rolemanager.RightScopeVO rightScope join rightScope.employee emp join rightScope.right r WHERE (r.rightCode='00*01*01' or r.rightCode='00*01*02' or r.rightCode='00*01*03') AND emp.empId=:empId order by r.rightCode").setLong("empId", Long.parseLong(obj[3]+"")).list();
                    if(rightsList != null){
                        for(int i=0; i<rightsList.size(); i++){
                            String rightCode = "" + rightsList.get(i);
                            if("00*01*01".equals(rightCode)){
                                sysManager = "1";
                                break;
                            }else if("00*01*02".equals(rightCode)){
                                sysManager = "2";
                            }else if("00*01*03".equals(rightCode)){
                                sysManager += "3";
                            }
                        }
                    }
                    
                    userInfo.put("sysManager", sysManager);
                    
                   /*Iterator iter = session.createQuery("select r.rightId FROM com.whir.org.vo.rolemanager.RightScopeVO rightScope join rightScope.employee emp join rightScope.right r WHERE r.rightCode='00*01*01' AND emp.empId=:empId").setLong("empId", Long.parseLong(obj[3]+"")).iterate();
                   if(iter.hasNext()){
                       userInfo.put("sysManager", "1");
                   }else{
                       boolean flag=false;
                       iter = session.createQuery("select r.rightId FROM com.whir.org.vo.rolemanager.RightScopeVO rightScope join rightScope.employee emp join rightScope.right r WHERE r.rightCode='00*01*02' AND emp.empId=:empId").setLong("empId", Long.parseLong(obj[3]+"")).iterate();
                       if(iter.hasNext()){
                           userInfo.put("sysManager", "2");
                           flag=true;
                       }
                       iter = session.createQuery("select r.rightId FROM com.whir.org.vo.rolemanager.RightScopeVO rightScope join rightScope.employee emp join rightScope.right r WHERE r.rightCode='00*01*03' AND emp.empId=:empId").setLong("empId", Long.parseLong(obj[3]+"")).iterate();
                       if(iter.hasNext()){
                           if(flag){
                               userInfo.put("sysManager","23");
                           }else{
                               userInfo.put("sysManager","3");
                           }
                       }
                   }*/
                   
                   //判断用户职务级别
                   userInfo.put("dutyName", obj[16]+"");
                   userInfo.put("dutyLevel", obj[32]!=null?obj[32]+"":"");
                   
                   /*userInfo.put("dutyLevel", "0");
                   if(obj[16]!=null){
                        iter=session.createQuery("select d.dutyLevel from com.whir.ezoffice.officemanager.po.DutyPO d where d.dutyName=:dutyName").setString("dutyName", obj[16].toString()).iterate();
                        if(iter.hasNext()){
                            userInfo.put("dutyLevel",iter.next());
                        }
                    }*/
                    
                    userInfo.put("imID", obj[31]!=null?obj[31]+"":"");
                    userInfo.put("sidelineOrg", obj[33]!=null?obj[33]+"":"");
                    userInfo.put("sidelineOrgName", obj[34]!=null?obj[34]+"":"");
                    userInfo.put("empLivingPhoto", obj[35]!=null?obj[35]+"":"");
                    userInfo.put("pageFontsize", obj[36]!=null?obj[36]+"":"");
                    userInfo.put("enterprisenumber", obj[37]!=null?obj[37]+"":"");
                    //记录用户IMID即时通讯工具ID
                    /*java.sql.Connection conn=session.connection();
                    java.sql.Statement stmt=conn.createStatement();
                    java.sql.ResultSet rs=stmt.executeQuery("select imid from org_employee where emp_id="+obj[3]);
                    if(rs.next()){
                        userInfo.put("imID",rs.getString(1));
                    }
                    rs.close();*/
                    
                    //stmt.close();
                   // conn.close();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.debug("error-userIP:"+userIP);
            throw e;
        }finally{
            try{
                session.close();
            }catch(Exception e){
                e.printStackTrace();
                logger.debug(e.getMessage());
            }
            return userInfo;
        }
    }

}
