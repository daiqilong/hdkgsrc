package com.whir.org.manager.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

import org.apache.log4j.Logger;

import com.whir.common.db.Dbutil;
import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.StringSplit;
import com.whir.org.common.util.ConvertIdAndName;
import com.whir.org.common.util.EndowVO;
import com.whir.org.vo.organizationmanager.OrganizationVO;
import com.whir.org.vo.usermanager.UserPO;

public class ManagerEJBBean extends HibernateBase implements SessionBean {
    
    private static Logger logger = Logger.getLogger(ManagerEJBBean.class
            .getName());
    
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

    public List getUserList(String para, String vo, String where) throws
            Exception {
        List userList = null;
        try {
            begin();
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT ");
            sqlBuffer.append(para);
            sqlBuffer.append(" FROM ");
            sqlBuffer.append(vo);
            sqlBuffer.append(where);
            Query query = session.createQuery(sqlBuffer.toString());
            userList = query.list();
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return userList;
    }

    /**
     * 根据所选择的人员的id串取出对应的组织名、员工名、组名。
     * @param idString String
     * @throws Exception
     * @return String
     */

    public String getNameById(String idString) throws Exception {

        ConvertIdAndName cIdAndName = new ConvertIdAndName();
        EndowVO endowVO = cIdAndName.splitId(idString);
        String orgId = endowVO.getOrgIdArray();
        String empId = endowVO.getEmpIdArray();
        String groupId = endowVO.getGroupIdArray();

        StringBuffer names = new StringBuffer();
        try {
            begin();
            
            if (orgId != null && !orgId.equals("")) {
                Query query = session.createQuery("SELECT org.orgName FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgId IN (" +
                                                  orgId + ")");
                List orgList = query.list();
                for (int i = 0, k=orgList.size(); i < k; i++) {
                    names.append(orgList.get(i));
                    names.append(",");
                }
            }
            if (groupId != null && !"".equals(groupId)) {
                Query query = session.createQuery("SELECT g.groupName FROM com.whir.org.vo.groupmanager.GroupVO g WHERE g.groupId IN (" +
                                                  groupId + ")");
                List groupList = query.list();
                for (int i = 0, k=groupList.size(); i < k; i++) {
                    names.append(groupList.get(i));
                    names.append(",");
                }
            }
            if (empId != null && !"".equals(empId)) {
                Query query = session.createQuery("SELECT emp.empName FROM com.whir.org.vo.usermanager.UserPO emp WHERE emp.empId IN (" +
                                                  empId + ")");
                List empList = query.list();
                for (int i = 0, k=empList.size(); i < k; i++) {
                    names.append(empList.get(i));
                    names.append(",");
                }
            }
        
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
        }
        
        return names.toString();
    }

    /**
     * 判断一个用户有没有一个权限类型的权限
     * @param userId String
     * @param rightType String
     * @return boolean
     */
    public Boolean hasRightType(String userId, String rightType) throws
            Exception {
        Boolean result = new Boolean(false);
        begin();
        try {
            String domainId = this.getUserDomainId(userId);
            Query query = session.createQuery("select count(aaa.rightScopeId) from com.whir.org.vo.rolemanager.RightScopeVO aaa join aaa.employee bbb join aaa.right ccc where bbb.empId = :userId and ccc.rightType = :rightType and ccc.domainId=:domainId");
            query.setParameter("userId", userId);
            query.setParameter("rightType", rightType);
            query.setParameter("domainId", domainId);
            
            int count = ((Integer)query.iterate().next()).intValue();
            if (count > 0) {
                result = Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("##############################################");
            System.out.println(e.getMessage());
            System.out.println("##############################################");
            throw e;
        } finally {
            session.close();

        }
        return result;
    }

    /**
     * 判断一个用户有没有一个权限类型的权限名称的权限
     * @param userId String
     * @param rightType String
     * @param rightName String
     * @throws Exception
     * @return Boolean
     */
    public Boolean hasRightTypeName(String userId, String rightType,
                                    String rightName) throws Exception {
        Boolean result = new Boolean(false);
        begin();
        try {
            String domainId = this.getUserDomainId(userId);
            Query query = session.createQuery("select count(aaa.rightScopeId) from com.whir.org.vo.rolemanager.RightScopeVO aaa join aaa.employee bbb join aaa.right ccc where bbb.empId = :userId and ccc.rightType = :rightType and ccc.rightName = :rightName and ccc.domainId=:domainId");
            query.setParameter("userId", userId);
            query.setParameter("rightType", rightType);
            query.setParameter("rightName", rightName);
            query.setParameter("domainId", domainId);
            
            int count = ((Integer)query.iterate().next()).intValue();
            if (count > 0) {
                result = Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println(
                    "####################################################");
            System.out.println(e.getMessage());
            System.out.println(
                    "####################################################");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }

    /**
     * 取某一用户的权限类型权限名称的权限范围类型和权限范围
     * @param userId String
     * @param rightType String
     * @param rightName String
     * @throws Exception
     * @return List
     */
    public List getRightScope(String userId, String rightType, String rightName) throws
            Exception {
        List list = null;
        begin();
        try {
            String domainId = this.getUserDomainId(userId);
            Query query = session.createQuery("select aaa.rightScopeType,aaa.rightScopeScope,aaa.rightScope,aaa.rightScopeUser,aaa.rightScopeGroup from com.whir.org.vo.rolemanager.RightScopeVO aaa join aaa.employee bbb join aaa.right ccc where bbb.empId = :userId and ccc.rightType = :rightType and ccc.rightName = :rightName and ccc.domainId=:domainId");
            query.setParameter("userId", userId);
            query.setParameter("rightType", rightType);
            query.setParameter("rightName", rightName);
            query.setParameter("domainId", domainId);
            
            list = query.list();
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }

    /**
     *
     * @param userId String
     * @param orgId String
     * @param rightType String
     * @throws Exception
     * @return Boolean
     */
    public Boolean hasRightTypeScope(String userId, String orgId,
                                     String rightType, String rightName,
                                     String channelType) throws Exception {
        Boolean result = new Boolean(false);
        begin();
        try {
            String domainId = this.getUserDomainId(userId);
            Query query = session.createQuery(
                    " select aaa.rightScopeType,aaa.rightScopeScope from " +
                    " com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                    " join aaa.employee bbb join aaa.right ccc where " +
                    " bbb.empId = :userId and ccc.rightType = :rightType " +
                    " and ccc.rightName = :rightName and ccc.domainId=:domainId");
            query.setParameter("userId", userId);
            query.setParameter("rightType", rightType);
            query.setParameter("rightName", rightName);
            query.setParameter("domainId", domainId);
            
            List list = query.list();
            if (list != null && list.size() > 0 && list.get(0) != null) {
                Object[] obj = (Object[])list.get(0);
                String rightScopeType = obj[0].toString();
//                String rightScopeScope = "";
//                if (obj[1] != null) {
//                    rightScopeScope = obj[1].toString();
//                    rightScopeScope = rightScopeScope.substring(1,
//                            rightScopeScope.length() - 1);
//                }
                if (rightScopeType.equals("0")) {
                    result = Boolean.TRUE;
                } else if (rightScopeType.equals("2")) {
                    query = session.createQuery(" select aaa.orgId from " +
                                                " com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                                                " where aaa.orgIdString like :orgId");
                    query.setParameter("orgId", "%$"+orgId+"$%");
                    
                    list = query.list();
                    for (int i = 0; i < list.size(); i++) {
                        if ((list.get(i).toString()).equals(channelType)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (rightScopeType.equals("3")) {
                    //权限范围是本组织
                    if (orgId.equals(channelType)) {
                        result = Boolean.TRUE;
                    }
                } else if (rightScopeType.equals("4")) {
                    String rightScopeScope = "";
                    String hql = " select aaa.orgId from " +
                        " com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where 1<>1 ";
                    if (obj[1] != null) {
                        rightScopeScope = obj[1].toString();
                        rightScopeScope = rightScopeScope.substring(1, rightScopeScope.length() - 1);
                        String[] rightScopeScopeArr = rightScopeScope.split("\\*\\*");
                        for(int i=0; i<rightScopeScopeArr.length; i++){
                            hql += " or aaa.orgIdString like '%$'" + rightScopeScopeArr[i] + "'$%'";
                        }
                    }
                    
                    query = session.createQuery(hql);
                    list = query.list();
                    if(list != null){
                        for (int i = 0, k=list.size(); i < k; i++) {
                            if ((list.get(i).toString()).equals(channelType)) {
                                result = Boolean.TRUE;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }

    /**
     * 取得某个组织范围的所有下级组织
     * @param range String 一个组织范围的串 如: *101**120*
     * @throws Exception
     * @return String  返回所有下级组织的ID 如: 122,124,200
     */
    public String getAllJuniorOrgIdByRange(String range) throws Exception {
        if (range == null || "".equals(range)) {
            return "-1";
        }
        String result = "-1,";
        try {
            StringBuffer where = new StringBuffer(" WHERE ");
            range = "*" + range + "*";
            String[] rangeArray = range.split("\\*\\*");
            int i = 0;
            for (i = 1; i < rangeArray.length; i++) {
                if (i > 1)where.append(" or ");
                where.append(" org.orgIdString like '%$");
                where.append(rangeArray[i]);
                where.append("$%' ");
            }
            begin();

            List list = session.createQuery(
                    "SELECT org.orgId FROM com.whir.org.vo.organizationmanager.OrganizationVO org" +
                    where).list();

            int j = 900;
            StringBuffer tmp = new StringBuffer();
            for (i = 0; i < list.size(); i++) {
                tmp.append(list.get(i));
                if (i > j) {
                    tmp.append("a");
                    j = j + 900;
                } else {
                    tmp.append(",");
                }
            }
            result = tmp.toString();
            if (result.length() > 0)
                result = result.substring(0, result.length() - 1);
        } catch (Exception e) {
            System.out.println("error!" + e.getMessage());
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * 赋权时根据给定的范围选择组织,选择自己有权限的组
     * @param range String 选择组织的范围
     * @param group String  是否选择组
     * @param orgId String  用户的组织ID
     * @param empId String  用户ID
     * @return HashMap  返回包含组织信息和组信息两个List的Map
     */
    public Map getOrgAndGroupByRange2(String range, String group,
                                     String orgIdString,
                                     String empId, String currentOrg,
                                     String domainId) throws Exception {
        StringBuffer sqlWhere = new StringBuffer();
        HashMap map = new HashMap(2);
        begin();
        try {
            StringBuffer tmp = new StringBuffer();
            if (range.equals("*0*") || range.equals("0")) {
                tmp.append(" 1=1 ");
            } else {
                String tmpSql = "";
                String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();
                if(databaseType.indexOf("mysql") >= 0) {
                    tmpSql = "select org.orgIdString,org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where '" +
                            range + "' like concat('%*', org.orgId, '*%')) and org.domainId=" +domainId + " ";
                }else if (databaseType.indexOf("db2") >= 0) {
                    tmpSql = "select org.orgIdString,org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where locate(EZOFFICE.FN_LINKCHAR('*', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'*')),'" +
                            range + "')>0  and org.domainId=" + domainId + " ";
                }else {
                    tmpSql = "select org.orgIdString,org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where '" +
                            range + "' like EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'*%')) and org.domainId=" + domainId + " ";
                }
                Iterator fIter = session.iterate(tmpSql);
                Object[] obj = null;
                while (fIter.hasNext()) {
                    obj = (Object[])fIter.next();
                    if (databaseType.indexOf("mysql") >= 0) {
                        tmp.append(" '" + obj[0] +"' like concat('%$', org.orgId, '$%')) or orgIdString like '%$" +obj[1] + "$%' or ");
                    } else if (databaseType.indexOf("db2") >= 0) {
                        tmp.append(" locate( EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'$')),'" +
                                   obj[0] + "')>0 or orgIdString like '%$" + obj[1] + "$%' or ");
                    } else {
                        tmp.append(" '" + obj[0] + "' like EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'$%')) or orgIdString like '%$" +
                                   obj[1] + "$%' or ");
                    }
                }

                tmp.append(" 1>1");
            }
            String sql = "SELECT org.orgId,org.orgName,org.orgParentOrgId,org.orgLevel,org.orgHasJunior,org.orgIdString FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgStatus=0 " +
                         sqlWhere.toString() + " and (" + tmp.toString() +") and org.domainId=" + domainId + " ORDER BY org.orgIdString";
            map.put("org", session.createQuery(sql).list());
            
            //判断是否选取组
            if (group.indexOf("group") >= 0) {
                //取得用户的所有上级组织,计算一个串如:12,23,300
                String idStringTemp;
                String idStringArray = "";
                int index = orgIdString.indexOf("$");
                int i = 0;
                while (index > 0) {
                    i++;
                    idStringTemp = orgIdString.substring(index + 1);
                    index = idStringTemp.indexOf("$");
                    if (i == 1) {
                        idStringArray = idStringTemp.substring(0, index);
                    } else {
                        idStringArray += "," + idStringTemp.substring(0, index);
                    }
                    if (index < idStringTemp.length()) {
                        orgIdString = idStringTemp.substring(index + 1);
                        index = orgIdString.indexOf("$");
                    }
                }
                if (i == 0)idStringArray = "0";

                sql = "SELECT g.groupId,g.groupName FROM com.whir.org.vo.groupmanager.GroupVO g";
                map.put("group", session.createQuery(sql).list());
            }
        } catch (Exception e) {
            System.out.println("The error at MangerEJBBean:" + e.getMessage());
            throw e;
        } finally {
            session.close();

        }
        return map;
    }

    /**
    * 赋权时根据给定的范围选择组织,选择自己有权限的组
    * @param range String 选择组织的范围
    * @param group String  是否选择组
    * @param orgId String  用户的组织ID
    * @param empId String  用户ID
    * @return HashMap  返回包含组织信息和组信息两个List的Map
    */
   public Map getOrgAndGroupByRange(String range, String group,
                                    String orgIdString,
                                    String empId, String currentOrg,
                                    String domainId) throws Exception {
       StringBuffer sqlWhere = new StringBuffer();
       HashMap map = new HashMap(2);
       if (!"".equals(currentOrg)) {
           sqlWhere.append(" and (org.orgIdString not like '%$")
                   .append(currentOrg).append("$%')");
       }
       begin();
       try {
           StringBuffer tmp = new StringBuffer();
           if (range.equals("*0*") || range.equals("0")) {
               tmp.append(" 1=1 ");
           } else {
               String tmpSql = "";
               String databaseType = com.whir.common.config.SystemCommon.
                       getDatabaseType();
               if (databaseType.indexOf("mysql") >= 0) {
                   tmpSql = "select org.orgIdString,org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where '" +
                           range +
                           "' like concat('%*', org.orgId, '*%')) and org.domainId=" +
                           domainId + " ";
               } else if (databaseType.indexOf("db2") >= 0) {
                   tmpSql = "select org.orgIdString,org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where locate(EZOFFICE.FN_LINKCHAR('*', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'*')),'" +
                           range + "')>0  and org.domainId=" + domainId + " ";
               } else {
                   tmpSql = "select org.orgIdString,org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where '" +
                           range + "' like EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'*%')) and org.domainId=" +
                           domainId + " ";
               }

               Iterator fIter = session.iterate(tmpSql);
               Object[] obj = null;
               while (fIter.hasNext()) {
                   obj = (Object[])fIter.next();
                   if (databaseType.indexOf("mysql") >= 0) {
                       tmp.append(" '" + obj[0] +
                               "' like concat('%$', org.orgId, '$%')) or orgIdString like '%$" +
                                  obj[1] + "$%' or ");
                   } else if (databaseType.indexOf("db2") >= 0) {
                       tmp.append(" locate( EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'$')),'" +
                                  obj[0] + "')>0 or orgIdString like '%$" +
                                  obj[1] + "$%' or ");
                   } else {
                       tmp.append(" '" + obj[0] + "' like EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_INTTOSTR(org.orgId),'$%')) or orgIdString like '%$" +
                                  obj[1] + "$%' or ");
                   }
               }

               tmp.append(" 1>1");
           }
           String sql = "SELECT org.orgId,org.orgName,org.orgParentOrgId,org.orgLevel,org.orgHasJunior,org.orgIdString FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgStatus=0 " +
                   sqlWhere.toString() + " and (" + tmp.toString() +
                   ") and org.domainId=" + domainId +
                   " ORDER BY org.orgIdString";
           map.put("org", session.createQuery(sql).list());
           //判断是否选取组
           if (group.indexOf("group") >= 0) {
               //取得用户的所有上级组织,计算一个串如:12,23,300
               String idStringTemp;
               String idStringArray = "";
               int index = orgIdString.indexOf("$");
               int i = 0;
               while (index > 0) {
                   i++;
                   idStringTemp = orgIdString.substring(index + 1);
                   index = idStringTemp.indexOf("$");
                   if (i == 1) {
                       idStringArray = idStringTemp.substring(0, index);
                   } else {
                       idStringArray += "," + idStringTemp.substring(0, index);
                   }
                   if (index < idStringTemp.length()) {
                       orgIdString = idStringTemp.substring(index + 1);
                       index = orgIdString.indexOf("$");
                   }
               }
               if (i == 0)idStringArray = "0";

               sql =
                       "SELECT g.groupId,g.groupName FROM com.whir.org.vo.groupmanager.GroupVO g";
               map.put("group", session.createQuery(sql).list());
           }

           //判断是否选取公共联系人
           if (group.indexOf("publicPerson") >= 0) {
               //取得用户的所有上级组织,计算一个串如:12,23,300
               String idStringTemp;
               String idStringArray = "";
               int index = orgIdString.indexOf("$");
               int i = 0;
               while (index > 0) {
                   i++;
                   idStringTemp = orgIdString.substring(index + 1);
                   index = idStringTemp.indexOf("$");
                   if (i == 1) {
                       idStringArray = idStringTemp.substring(0, index);
                   } else {
                       idStringArray += "," + idStringTemp.substring(0, index);
                   }
                   if (index < idStringTemp.length()) {
                       orgIdString = idStringTemp.substring(index + 1);
                       index = orgIdString.indexOf("$");
                   }
               }
               if (i == 0)idStringArray = "0";

               sql = "SELECT personClassPO.id,personClassPO.className FROM com.whir.ezoffice.personalwork.person.po.PersonClassPO personClassPO where personClassPO.classType=1 and personClassPO.domainId=" +
                       domainId;
               map.put("publicPerson", session.createQuery(sql).list());
           }

           //判断是否选取个人联系人
           if (group.indexOf("privatePerson") >= 0) {
               //取得用户的所有上级组织,计算一个串如:12,23,300
               String idStringTemp;
               String idStringArray = "";
               int index = orgIdString.indexOf("$");
               int i = 0;
               while (index > 0) {
                   i++;
                   idStringTemp = orgIdString.substring(index + 1);
                   index = idStringTemp.indexOf("$");
                   if (i == 1) {
                       idStringArray = idStringTemp.substring(0, index);
                   } else {
                       idStringArray += "," + idStringTemp.substring(0, index);
                   }
                   if (index < idStringTemp.length()) {
                       orgIdString = idStringTemp.substring(index + 1);
                       index = orgIdString.indexOf("$");
                   }
               }
               if (i == 0)idStringArray = "0";

               sql = "SELECT personClassPO.id,personClassPO.className FROM com.whir.ezoffice.personalwork.person.po.PersonClassPO personClassPO where personClassPO.classType=0 and personClassPO.empId=" +
                       empId + " and personClassPO.domainId=" + domainId;
               map.put("privatePerson", session.createQuery(sql).list());
           }
       } catch (Exception e) {
           System.out.println("The error at MangerEJBBean:" + e.getMessage());
           throw e;
       } finally {
           session.close();

       }
       return map;
   }


    /**
     * 取得某个组织下的下一级子组织 和用户
     * @param orgId String 组织ID
     * @param type String  选择的类型(包含"user","org"的字符串 如:"user","userorg"等
     * @throws Exception
     * @return Map 返回包含组织List 和用户List 的Map
     */
    public Map getSubOrgAndUsers(String orgId, String currentOrg,
                                 String domainId, String rootCorpId,
                                 String corpId, String departId,
                                 String otherDepart) throws Exception {
        HashMap map = new HashMap(2);
        String databaseType = com.whir.common.config.SystemCommon.
                getDatabaseType();

        Dbutil db = new Dbutil();
        HashMap varmap = new HashMap();
        
        begin();
        try {
            //取得下级组织
            String sql = "SELECT org.orgId,org.orgName,org.orgSerial,org.orgIdString FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgParentOrgId="
                    + ":orgId  AND org.orgStatus=0 and org.domainId=:domainId";
            ;
            varmap.put("orgId", orgId);
            varmap.put("domainId", domainId);
            if ("0".equals(orgId)) {
                //取一级组织
                if (!"".equals(currentOrg)) {
                    if ("-1".equals(otherDepart)) {
                        //取全部下级组织
                    } else if ("1".equals(otherDepart)) {
                        //取外单位的所有组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql += " and org.orgIdString not like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"%$");
                        } else {
                            sql +=
                                    " and org.orgIdString not like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId ),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }

                    } else {
                        //本单位的组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql +=
                                    " and org.orgIdString like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"%$");

                        } else {
                            sql +=
                                    " and org.orgIdString like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId ),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }
                    }
                    if (databaseType.indexOf("db2") >= 0) {
                        sql += " and org.orgIdString like :rootCorpId ";
                        varmap.put("rootCorpId", "%$"+rootCorpId+"%$");
                    } else {
                        sql +=
                                " and org.orgIdString like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                        varmap.put("rootCorpId", rootCorpId);
                    }
                } else {
                    if ("-1".equals(otherDepart)) {
                        //取全部下级组织
                    } else if ("1".equals(otherDepart)) {
                        //取外单位的所有组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql += " and org.orgIdString not like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"%$");
                        } else {
                            sql +=" and org.orgIdString not like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }
                    } else {
                        //本单位的组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql += " and org.orgIdString like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"%$");
                        } else {
                            sql +=" and org.orgIdString like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }
                    }
                }
               
            } else {
                //取下级组织
                if (!"".equals(currentOrg)) {
                    sql += " and org.orgId<>:currentOrg ";
                    varmap.put("currentOrg", currentOrg);
                }
            }
            sql += " ORDER BY org.orgOrderCode";
            //System.out.println("Sql:"+sql);
            List list = db.getDataListByHQL(sql, varmap);
            map.put("org", list);
            
            Query query = session.createQuery("SELECT emp.empId,emp.empName,emp.userAccounts,emp.curStatus,emp.imId, emp.empSex, org.orgName, org.orgNameString FROM com.whir.org.vo.usermanager.UserPO emp join emp.organizations org WHERE (org.orgId=:orgId or emp.sidelineOrg like :sidelineOrg) AND emp.userIsActive=1 and emp.userIsDeleted=0 and emp.userAccounts is not null ORDER BY emp.empDutyLevel,emp.userOrderCode,emp.empName");
            query.setParameter("orgId", orgId);
            query.setParameter("sidelineOrg", "%*" + orgId + "*%");
            
            map.put("user", query.list());
        } catch (Exception e) {
            System.out.println("error ManagerEJB :" + e.getMessage());
            throw e;
        } finally {
            session.close();

        }
        return map;
    }

    /**
     * 取得某个组织下的下一级子组织 和用户
     * @param orgId String 组织ID
     * @param type String  选择的类型(包含"user","org"的字符串 如:"user","userorg"等
     * @throws Exception
     * @return Map 返回包含组织List 和用户List 的Map
     */
    public Map getSelectedGroupAndOrgAndUsers(String selected) throws Exception {
        HashMap map = new HashMap(3);
        String databaseType = com.whir.common.config.SystemCommon.
                getDatabaseType();
        begin();
        try {
            //取得下级组织
            String sql;
            if (databaseType.indexOf("db2") >= 0) {
                sql = "SELECT org.orgId,org.orgName,org.orgSerial FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE locate(ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%*',org.orgId),'*%'),'"
                        + selected + "')>0  ORDER BY org.orgIdString";
            } else {
                sql = "SELECT org.orgId,org.orgName,org.orgSerial FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE '"
                        + selected + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%*',org.orgId),'*%') ORDER BY org.orgIdString";
            }
            map.put("org", session.createQuery(sql).list());

            if (databaseType.indexOf("db2") >= 0) {
                sql = "SELECT emp.empId,emp.empName,emp.userAccounts,emp.curStatus,emp.imId, emp.empSex FROM com.whir.org.vo.usermanager.UserPO emp join emp.organizations org WHERE emp.userIsActive=1 and emp.userIsDeleted=0 and emp.userAccounts is not null and locate(ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',emp.empId),'$%'),'"
                        + selected +
                        "')>0  ORDER BY emp.empDutyLevel,emp.userOrderCode,emp.empName";
            } else {
                sql = "SELECT emp.empId,emp.empName,emp.userAccounts,emp.curStatus,emp.imId, emp.empSex FROM com.whir.org.vo.usermanager.UserPO emp join emp.organizations org WHERE emp.userIsActive=1 and emp.userIsDeleted=0 and emp.userAccounts is not null and '"
                        + selected + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',emp.empId),'$%') ORDER BY emp.empDutyLevel,emp.userOrderCode,emp.empName";
            }

            map.put("user", session.createQuery(sql).list());

            if (databaseType.indexOf("db2") >= 0) {
                sql = "SELECT po.groupId,po.groupName,po.groupCode FROM com.whir.org.vo.groupmanager.GroupVO po WHERE locate(ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',po.groupId),'@%'),'"
                        + selected + "')>0  order by po.groupOrder";
            } else {
                sql = "SELECT po.groupId,po.groupName,po.groupCode FROM com.whir.org.vo.groupmanager.GroupVO po WHERE '"
                        + selected + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',po.groupId),'@%') order by po.groupOrder";
            }

            map.put("group", session.createQuery(sql).list());
        } catch (Exception e) {
            System.out.println("error ManagerEJB :" + e.getMessage());
            throw e;
        } finally {
            session.close();
        }
        return map;
    }

    /**
     *
     * @param userId String
     * @param orgId String
     * @param orgIdString String
     * @param rightType String
     * @param rightName String
     * @param fieldOrg String
     * @param fieldEmp String
     * @throws Exception
     * @return String
     */
    public String getRightWhere(String userId,
                                String orgId,
                                String orgIdString,
                                String rightType,
                                String rightName,
                                String fieldOrg,
                                String fieldEmp
                                ) throws Exception {
        String where = "";
        List list = getRightScope(userId, rightType, rightName);

        //----------------------------------------------------------------------
        //兼职组织
        UserPO upo = getSidelineOrgByUserId(userId, false);
        String sidelineOrg = upo.getSidelineOrg();
        
        logger.debug("userId:"+userId);
        logger.debug("orgId:"+orgId);
        logger.debug("sidelineOrg:"+sidelineOrg);

        String[] sidelineOrgArr = null;
        String sidelineOrg_sql = "";
        String sidelineOrg_str = "";//兼职组织 *123**456*
        if(sidelineOrg!=null && !"".equals(sidelineOrg) && !"null".equals(sidelineOrg)){
            //切换兼职组织登录
            if(sidelineOrg.indexOf("*"+orgId+"*")!=-1){
                logger.debug("兼职组织");
                //所属组织
                orgId = getOrgIdByUserId(userId);//所属组织ID
                
                logger.debug("after orgId:"+orgId);
            }
            
            if(sidelineOrg.startsWith("*") && sidelineOrg.endsWith("*")){
                sidelineOrg_str = sidelineOrg;
                sidelineOrg = sidelineOrg.substring(1, sidelineOrg.length()-1);
                sidelineOrgArr = sidelineOrg.split("\\*\\*");
                for(int i=0; i<sidelineOrgArr.length; i++){
                    sidelineOrg_sql += " or " + fieldOrg + "=" + sidelineOrgArr[i];
                }
            }
        }
        //----------------------------------------------------------------------

        //System.out.println(list);
        if (list != null && list.size() > 0) {
            Object[] obj = (Object[])list.get(0);
            String scopeType = obj[0].toString();
            logger.debug("scopeType:"+scopeType);
            if ("0".equals(scopeType)) {
                //可以维护全部数据
                where = " 1=1 ";
            } else if ("1".equals(scopeType)) {
                //可以维护本人的数据
                where = "".equals(fieldEmp) ? " 1=1 " : fieldEmp + "=" + userId;
            } else if ("2".equals(scopeType)) {
                //可以维护本组织及下级组织的数据
                String orgRange = getAllJuniorOrgIdByRange("*" + orgId + "*" + sidelineOrg_str);//包含兼职组织
                if (orgRange.indexOf("a") > 0) {
                    String[] tmp = orgRange.split("a");
                    for (int k = 0; k < tmp.length; k++) {
                        where += "(" + fieldOrg + " in(" + tmp[k] + ")) or ";
                    }
                    if (where.endsWith("or ")) {
                        where = where.substring(0, where.length() - 3);
                    }
                } else {
                    where = fieldOrg + " in(" + orgRange + ") ";
                }

                //where = fieldOrg + " in(" + orgRange + ") ";
            } else if ("3".equals(scopeType)) {
                //可以维护本组织的数据
                where = fieldOrg + "=" + orgId + sidelineOrg_sql;//包含兼职组织
            } else {
                //scopeType==4 维护定义的范围
                if (obj[1] != null && !"".equals(obj[1].toString())) {
                    String orgRange = getAllJuniorOrgIdByRange((String)obj[1]);
                    if ("".equals(orgRange)) {
                        where = "1>2";
                    } else {
                        if (orgRange.indexOf("a") > 0) {
                            String[] tmp = orgRange.split("a");
                            for (int k = 0; k < tmp.length; k++) {
                                where += "(" + fieldOrg + " in(" + tmp[k] +
                                        ")) or ";
                            }
                            if (where.endsWith("or ")) {
                                where = where.substring(0, where.length() - 3);
                            }
                        } else {
                            where = fieldOrg + " in(" + orgRange + ") ";
                        }
                    }
                }

                String dbType = com.whir.common.config.SystemCommon.
                        getDatabaseType();
                //判断定义的范围内的用户
                //obj[3] 为选择的用户，obj[4]为选择的群组
                if (fieldEmp != null && !"".equals(fieldEmp) && obj[3] != null &&
                    !"".equals(obj[3].toString())) {
                    if (!"".equals(where)) {
                        where += " or ";
                    }
                    if ("oracle".equals(dbType) || "mysql".equals(fieldEmp)) {
                        where += "'" + obj[3].toString() + "' like '%$'||" +
                                fieldEmp + "||'$%'";
                    } else if (dbType.indexOf("sqlserver") != -1) {
                        where += "'" + obj[3].toString() +
                                "' like '%$'+convert(varchar," + fieldEmp +
                                ")+'$%'";
                    } else if ("db2".equals(fieldEmp)) {
                        where += "'" + obj[3].toString() + "' like '%$'+" +
                                fieldEmp + "+'$%'";
                    }
                }
                if ("".equals(where)) {
                    where = "1>2";
                }

            }
        } else {
            where = ("1>2");
        }
        
        logger.debug("where:"+where);
        
        return "(" + where + ")";
    }

    /**
     * @param userId String
     * @param orgId String
     * @param orgIdString String
     * @param rightType String
     * @param rightName String
     * @param fieldOrg String
     * @param fieldEmp String
     * @throws Exception
     * @return String
     */
    public String getRightFinalWhere(String userId,
                                     String orgId,
                                     String orgIdString,
                                     String rightType,
                                     String rightName,
                                     String fieldOrg,
                                     String fieldEmp
                                     ) throws Exception {
        String where;
        boolean bln = hasRightTypeName(userId, rightType, rightName).
                booleanValue();
        if (!bln)where = " 1<>1 ";
        else where = getRightWhere(userId, orgId, orgIdString, rightType,
                                   rightName, fieldOrg, fieldEmp);
        return where;
    }

    /**
     * 构造sql条件(用于控制用户范围)
     * @param userId String 用户ID
     * @param orgId String 组织ID
     * @param orgIdString String 组织ID串
     * @param fieldUser String PO中存储用户ID的字段
     * @param fieldOrg String PO中存储组织ID的字段
     * @param fieldGroup String PO中存储组ID的字段
     * @param fieldCreatedEmp String PO中创建人ID字段
     * @throws Exception
     * @return String
     */
    public String getScopeWhere(String userId,
                                String orgId,
                                String orgIdString,
                                String fieldUser,
                                String fieldOrg,
                                String fieldGroup,
                                String fieldCreatedEmp) throws Exception {
        StringBuffer where = new StringBuffer();
        //用户条件
        where.append(fieldUser).append(" like '%$").append(userId).append("$%'").
                append(" or ");
        //组织条件
        where.append("(").append(whereOrg(fieldOrg, orgIdString, userId)).append(
                ") or ");
        //组条件
        where.append("(").append(whereGroup(fieldGroup, userId)).append(") or ");
        where.append(fieldCreatedEmp).append("=").append(userId);
        return where.toString();
    }

    /**
     * 构造sql条件(用于控制用户范围)
     * @param userId String 用户ID
     * @param orgId String 组织ID
     * @param orgIdString String 组织ID串
     * @param fieldUser String PO中存储用户ID的字段
     * @param fieldOrg String PO中存储组织ID的字段
     * @param fieldGroup String PO中存储组ID的字段
     * @param fieldCreatedEmp String PO中创建人ID字段
     * @throws Exception
     * @return String
     */
    public String getScopeFinalWhere(String userId,
                                     String orgId,
                                     String orgIdString,
                                     String fieldUser,
                                     String fieldOrg,
                                     String fieldGroup) throws Exception {
        StringBuffer where = new StringBuffer();
        //用户条件
        where.append(fieldUser).append(" like '%$").append(userId).append("$%'").
                append(" or ");
        //组织条件
        where.append("(").append(whereOrg(fieldOrg, orgIdString, userId)).append(
                ") or ");
        //组条件
        where.append("(").append(whereGroup(fieldGroup, userId)).append(") ");
        return where.toString();
    }

    /**
     * 构造getScopeWhere的SQLwhere
     * @param orgIdString String
     * @throws Exception
     * @return String
     */
    private String whereOrg(String fieldOrg, String orgIdString, String userId) throws
            Exception {
        StringBuffer retString = new StringBuffer(" 1<>1 ");
        try {
            //----------------------------------------------------------------------
            //兼职组织
            UserPO upo = getSidelineOrgByUserId(userId, true);
            String sidelineOrg = upo.getSidelineOrg();            
            logger.debug("userId:"+userId);
            logger.debug("sidelineOrg:"+sidelineOrg);
            if(sidelineOrg!=null && !"".equals(sidelineOrg) && !"null".equals(sidelineOrg)){
                Set org = upo.getOrganizations();
                Iterator itor = org.iterator();
                OrganizationVO orgVO = (OrganizationVO)itor.next();
                orgIdString = orgVO.getOrgIdString();
            }
            
            String str = StringSplit.splitOrgIdString(orgIdString, "$", "_"); //$$1$$2$$
            str = "$" + str + "$";
            String[] arr = str.split("\\$\\$");
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != null && !arr[i].equals("") && !arr[i].equals(" ")) {
                    retString.append(" or ").append(fieldOrg).append(
                            " like '%*").append(arr[i]).append("*%'");
                }
            }
            
            //----------------------------------------------------------------------
            //兼职组织
            if(sidelineOrg!=null && !"".equals(sidelineOrg) && !"null".equals(sidelineOrg)){
                String[] arr2 = ("*"+sidelineOrg+"*").split("\\*\\*");
                for (int i = 0; i < arr2.length; i++) {
                    if (arr2[i] != null && !arr2[i].equals("") && !arr2[i].equals(" ")) {
                        retString.append(" or ").append(fieldOrg).append(
                                " like '%*").append(arr2[i]).append("*%'");
                    }
                }
            }
            //----------------------------------------------------------------------
        } catch (Exception e) {
            retString = new StringBuffer(" 1<>1 ");
            e.printStackTrace();
        }
        return retString.toString();
    }

    /**
     * 构造getScopeWhere的SQLwhere
     * @param userId long
     * @throws Exception
     * @return String
     */
    private String whereGroup(String fieldGroup, String userId) throws
            Exception {
        StringBuffer retString = new StringBuffer(" 1<>1 ");
        try {
            begin();
            Query query = session.createQuery("select groups.groupId from com.whir.org.vo.usermanager.UserPO user join user.groups groups where user.empId = :userId");
            query.setParameter("userId", userId);
            
            List list = query.list();
            for (int i = 0; i < list.size(); i++) {
                retString.append(" or ").append(fieldGroup).append(" like '%@").
                        append(list.get(i)).append("@%'");
            }
        } catch (Exception e) {
            retString = new StringBuffer(" 1<>1 ");
            e.printStackTrace();
        } finally {
            session.close();

        }
        return retString.toString();
    }

    public String getEmployeesAccounts(String empIds) {
        StringBuffer result = new StringBuffer();
        try {
            begin();

            Query query;
            if (empIds.indexOf("$") >= 0) {
                String tmpSql = "";
                String databaseType = com.whir.common.config.SystemCommon.
                        getDatabaseType();
                if (databaseType.indexOf("mysql") >= 0) {
                    tmpSql = "select distinct emp.userAccounts from com.whir.org.vo.usermanager.UserPO emp where :empIds like concat('%$', emp.empId, '$%')";
                } else if (databaseType.indexOf("db2") >= 0) {
                    tmpSql = "select distinct emp.userAccounts from com.whir.org.vo.usermanager.UserPO emp where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(emp.empId)), '$'),:empIds )>0 ";
                } else {
                    tmpSql = "select distinct emp.userAccounts from com.whir.org.vo.usermanager.UserPO emp where :empIds  like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp.empId)), '$%')";
                }
                query = session.createQuery(tmpSql);
                query.setParameter("empIds", empIds);
            } else {
                query = session.createQuery("select distinct emp.userAccounts from com.whir.org.vo.usermanager.UserPO emp where emp.empId in (:empIds )");
                String[] empIdarr = empIds.split(",");
                query.setParameterList("empIds", empIdarr);
            }
          
            Iterator iterator = query.iterate();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    result.append(iterator.next()).append(",");
                }
            }
        } catch (HibernateException ex) {
            throw new EJBException("Hibernate Exception:", ex);
        } finally {
            try {
                session.close();
            } catch (HibernateException ex1) {
                throw new EJBException("Close session Exception:", ex1);
            }
        }
        if (result.length() > 0)return result.substring(0, result.length() - 1);
        else return "";
    }

    /**
     * 通过权限编码判断用户有无权限
     * @param userId String 用户ID
     * @param rightCode String 权限编码
     * @throws Exception
     * @return Boolean
     */
    public Boolean hasRight(String userId, String rightCode) throws Exception {
        Boolean result = new Boolean(false);
        begin();
        try {
            String domainId = this.getUserDomainId(userId);
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                    getDatabaseType();
//            if (databaseType.indexOf("mysql") >= 0) {
//                tmpSql = "select count(aaa.rightScopeId) from com.whir.org.vo.rolemanager.RightScopeVO aaa join aaa.employee bbb join aaa.right ccc where bbb.empId = " +
//                        userId + " and ccc.rightCode like concat('" + rightCode +
//                        "', '%') and ccc.domainId=" + domainId;
//            } else {
                tmpSql = "select count(aaa.rightScopeId) from com.whir.org.vo.rolemanager.RightScopeVO aaa join aaa.employee bbb join aaa.right ccc where bbb.empId = :userId and ccc.rightCode like :rightCode and ccc.domainId= :domainId";
//            }

            Query query = session.createQuery(tmpSql);
            query.setParameter("userId", userId);
            query.setParameter("rightCode", rightCode + "%");
            query.setParameter("domainId", domainId);
            
            int count = ((Integer)query.iterate().next()).intValue();
            if (count > 0) {
                result = Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
            session.close();

        }
        return result;
    }

    /**
     * 通过rightCode判断权限
     * @param userId String
     * @param orgId String
     * @param rightCode String 权限编码
     * @param channelType String
     * @throws Exception
     * @return Boolean
     */
    public Boolean hasRightTypeScope(String userId, String orgId,
                                     String rightCode, String channelType) throws
            Exception {
        Boolean result = new Boolean(false);
        begin();
        try {
            String domainId = this.getUserDomainId(userId);
            Query query = session.createQuery(
                    " select aaa.rightScopeType,aaa.rightScopeScope from " +
                    " com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                    " join aaa.employee bbb join aaa.right ccc where " +
                    " bbb.empId = :userId and ccc.rightCode = :rightCode and ccc.domainId=:domainId");
            query.setParameter("userId", userId);
            query.setParameter("rightCode", rightCode);
            query.setParameter("domainId", domainId);
            
            List list = query.list();
            if (list != null && list.size() > 0 && list.get(0) != null) {
                Object[] obj = (Object[])list.get(0);
                String rightScopeType = obj[0].toString();
//                String rightScopeScope = "";
//                if (obj[1] != null) {
//                    rightScopeScope = obj[1].toString();
//                    rightScopeScope = rightScopeScope.substring(1,
//                            rightScopeScope.length() - 1);
//                }
                if (rightScopeType.equals("0")) {
                    result = Boolean.TRUE;
                } else if (rightScopeType.equals("2")) {
                    query = session.createQuery(" select aaa.orgId from " +
                                                " com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                                                " where aaa.orgIdString like :orgId");
                    query.setParameter("orgId", "%$"+orgId+"$%");
                    
                    list = query.list();
                    for (int i = 0; i < list.size(); i++) {
                        if ((list.get(i).toString()).equals(channelType)) {
                            result = Boolean.TRUE;
                            break;
                        }
                    }
                } else if (rightScopeType.equals("3")) {
                    //权限范围是本组织
                    if (orgId.equals(channelType)) {
                        result = Boolean.TRUE;
                    }
                } else if (rightScopeType.equals("4")) {
                    String rightScopeScope = "";
                    String hql = " select aaa.orgId from " +
                        " com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where 1<>1 ";
                    if (obj[1] != null) {
                        rightScopeScope = obj[1].toString();
                        rightScopeScope = rightScopeScope.substring(1, rightScopeScope.length() - 1);
                        String[] rightScopeScopeArr = rightScopeScope.split("\\*\\*");
                        for(int i=0; i<rightScopeScopeArr.length; i++){
                            hql += " or aaa.orgIdString like '%$'" + rightScopeScopeArr[i] + "'$%'";
                        }
                    }
                    
                    query = session.createQuery(hql);
                    
//                    query = session.createQuery(" select aaa.orgId from " +
//                                                " com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
//                                                " where aaa.orgIdString like '%$" +
//                                                rightScopeScope + "$%'");
                    list = query.list();
                    if(list != null){
                        for (int i = 0, k=list.size(); i < k; i++) {
                            if ((list.get(i).toString()).equals(channelType)) {
                                result = Boolean.TRUE;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }

    /**
     * 通过rightCode取某一用户的权限类型权限名称的权限范围类型和权限范围
     * @param userId String
     * @param rightType String
     * @param rightName String
     * @throws Exception
     * @return List
     */
    public List getRightScope(String userId, String rightCode) throws Exception {
        List list = null;
        begin();
        try {
            String domainId = this.getUserDomainId(userId);
            Query query = session.createQuery("select aaa.rightScopeType,aaa.rightScopeScope,aaa.rightScope,aaa.rightScopeUser,aaa.rightScopeGroup from com.whir.org.vo.rolemanager.RightScopeVO aaa join aaa.employee bbb join aaa.right ccc where bbb.empId = :userId and ccc.rightCode = :rightCode and ccc.domainId=:domainId");
            query.setParameter("userId", userId);
            query.setParameter("rightCode", rightCode);
            query.setParameter("domainId", domainId);
            
            list = query.list();
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }

    /**
     * 通过rightCode取得权限条件
     * @param userId String
     * @param orgId String
     * @param rightCode String 权限编码
     * @param fieldOrg String
     * @param fieldEmp String
     * @throws Exception
     * @return String
     */
    public String getRightWhere(String userId, String orgId, String rightCode,
                                String fieldOrg, String fieldEmp) throws
            Exception {

        String where = "";
        List list = getRightScope(userId, rightCode);

        //----------------------------------------------------------------------
        //兼职组织
        UserPO upo = getSidelineOrgByUserId(userId, false);
        String sidelineOrg = upo.getSidelineOrg();
        
        logger.debug("userId:"+userId);
        logger.debug("orgId:"+orgId);
        logger.debug("sidelineOrg:"+sidelineOrg);

        String[] sidelineOrgArr = null;
        String sidelineOrg_sql = "";        
        String sidelineOrg_str = "";//兼职组织 *123**456*
        if(sidelineOrg!=null && !"".equals(sidelineOrg) && !"null".equals(sidelineOrg)){
            //切换兼职组织登录
            if(sidelineOrg.indexOf("*"+orgId+"*")!=-1){
                logger.debug("兼职组织");
                //所属组织
                orgId = getOrgIdByUserId(userId);//所属组织ID
                
                logger.debug("after orgId:"+orgId);
            }
            
            if(sidelineOrg.startsWith("*") && sidelineOrg.endsWith("*")){
                sidelineOrg_str = sidelineOrg;
                sidelineOrg = sidelineOrg.substring(1, sidelineOrg.length()-1);
                sidelineOrgArr = sidelineOrg.split("\\*\\*");
                for(int i=0; i<sidelineOrgArr.length; i++){
                    sidelineOrg_sql += " or " + fieldOrg + "=" + sidelineOrgArr[i];
                }
            }            
        }
        //----------------------------------------------------------------------

        if (list != null && list.size() > 0) {
            Object[] obj = (Object[])list.get(0);
            String scopeType = obj[0].toString();
            logger.debug("scopeType:"+scopeType);
            if ("0".equals(scopeType)) {
                //可以维护全部数据
                where = " 1=1 ";
            } else if ("1".equals(scopeType)) {
                //可以维护本人的数据
                where = "".equals(fieldEmp) ? "1>1" : fieldEmp + "=" + userId;
            } else if ("2".equals(scopeType)) {
                //可以维护本组织及下级组织的数据
                String orgRange = getAllJuniorOrgIdByRange("*" + orgId + "*" + sidelineOrg_str);//包含兼职组织
                if (orgRange.indexOf("a") > 0) {
                    String[] tmp = orgRange.split("a");

                    for (int k = 0; k < tmp.length; k++) {
                        where += "(" + fieldOrg + " in(" + tmp[k] + ")) or ";
                    }
                    if (where.endsWith("or ")) {
                        where = where.substring(0, where.length() - 3);
                    }
                } else {
                    where = fieldOrg + " in(" + orgRange + ") ";
                }

            } else if ("3".equals(scopeType)) {
                //可以维护本组织的数据
                where = fieldOrg + "=" + orgId + sidelineOrg_sql;//包含兼职组织
            } else {
                //scopeType==4 维护定义的范围
                if (obj[1] != null && !"".equals(obj[1].toString())) {
                    String orgRange = getAllJuniorOrgIdByRange((String)obj[1]);
                    if ("".equals(orgRange)) {
                        where = "1>2";
                    } else {
                        if (orgRange.indexOf("a") > 0) {
                            String[] tmp = orgRange.split("a");
                            for (int k = 0; k < tmp.length; k++) {
                                where += "(" + fieldOrg + " in(" + tmp[k] +
                                        ")) or ";
                            }
                            if (where.endsWith("or ")) {
                                where = where.substring(0, where.length() - 3);
                            }
                        } else {
                            where = fieldOrg + " in(" + orgRange + ") ";
                        }
                    }
                }
                //where = "".equals(orgRange) ? "1>2" :" " + fieldOrg + " in(" + orgRange + ") ";
                String dbType = com.whir.common.config.SystemCommon.
                        getDatabaseType();
                //判断定义的范围内的用户
                //obj[3] 为选择的用户，obj[4]为选择的群组
                if (fieldEmp != null && !"".equals(fieldEmp) && obj[3] != null &&
                    !"".equals(obj[3].toString())) {
                    if (!"".equals(where)) {
                        where += " or ";
                    }
                    if ("oracle".equals(dbType) || "mysql".equals(fieldEmp)) {
                        where += "'" + obj[3].toString() + "' like '%$'||" +
                                fieldEmp + "||'$%'";
                    } else if (dbType.indexOf("sqlserver") != -1) {
                        where += "'" + obj[3].toString() +
                                "' like '%$'+convert(varchar," + fieldEmp +
                                ")+'$%'";
                    } else if ("db2".equals(fieldEmp)) {
                        where += "'" + obj[3].toString() + "' like '%$'+" +
                                fieldEmp + "+'$%'";
                    }
                }
                if ("".equals(where)) {
                    where = "1>2";
                }

            }
        } else {
            where = "1>2";
        }
        
        logger.debug("where:"+where);
        
        return "(" + where + ")";
    }

    /**
     *
     * @param userId String
     * @param orgId String
     * @param rightCode String 权限编码
     * @param fieldOrg String
     * @param fieldEmp String
     * @throws Exception
     * @return String
     */
    public String getRightFinalWhere(String userId,
                                     String orgId,
                                     String rightCode,
                                     String fieldOrg,
                                     String fieldEmp) throws Exception {
        String where;

        boolean bln = hasRight(userId, rightCode).booleanValue();
        if (!bln)
            where = " 1<>1 ";
        else
            where = getRightWhere(userId, orgId, rightCode, fieldOrg, fieldEmp);

        return where;
    }

    private String getUserDomainId(String userId) {
        String domainId = "0";
        try {
            Query query = session.createQuery("select emp.domainId from com.whir.org.vo.usermanager.UserPO emp WHERE emp.empId=:userId");
            query.setParameter("userId", userId);
            
            domainId = query.iterate().next().toString();
        } catch (Exception ex) {
            domainId = "0";
        } finally {

        }
        return domainId;
    }

    public List getAllDuty(String domainId) throws Exception {
        List list = new ArrayList();
        try {
            begin();
            Query query = session.createQuery("select po.id,po.dutyName,po.dutyLevel from com.whir.ezoffice.officemanager.po.DutyPO po where po.domainId=:domainId order by po.dutyLevel");
            query.setParameter("domainId", domainId);
            
            list = query.list();
            
            session.close();
        } catch (Exception ex) {
            try {
                session.close();
            } catch (Exception err) {

            }
            throw ex;
        } finally {
            return list;
        }
    }

    /**
     * date: 2009-01-07 new add---
     * 取得某个组织下的下一级子组织 和用户(适用于人事管理选人)
     * 不过滤帐号是空的情况
     * @param orgId String 组织ID
     * @param type String  选择的类型(包含"user","org"的字符串 如:"user","userorg"等
     * @throws Exception
     * @return Map 返回包含组织List 和用户List 的Map
     */
    public Map getSubOrgAndAllUsers(String orgId, String currentOrg,
                                    String domainId, String rootCorpId,
                                    String corpId, String departId,
                                    String otherDepart) throws Exception {
        HashMap map = new HashMap(2);
        String databaseType = com.whir.common.config.SystemCommon.
                getDatabaseType();
        Dbutil db = new Dbutil();
        HashMap varmap = new HashMap();
        begin();
        try {
            //取得下级组织
            String sql = "SELECT org.orgId,org.orgName,org.orgSerial FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgParentOrgId=:orgId"
                  +  " AND org.orgStatus=0 and org.domainId=:domainId" ;
           // Query qu = session.createQuery(sql);
            if ("0".equals(orgId)) {
                //取一级组织
                if (!"".equals(currentOrg)) {
                    if ("-1".equals(otherDepart)) {
                        //取全部下级组织
                    } else if ("1".equals(otherDepart)) {
                        //取外单位的所有组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql += " and org.orgIdString not like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"$%");
                        } else {
                            sql +=
                                    " and org.orgIdString not like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }

                    } else {
                        //本单位的组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql +=
                                    " and org.orgIdString like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"$%");

                        } else {
                            sql +=" and org.orgIdString like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }
                    }
                    if (databaseType.indexOf("db2") >= 0) {
                        sql += " and org.orgIdString like :rootCorpId ";
                        varmap.put("rootCorpId", "%$"+rootCorpId+"$%");
                    } else {
                        sql +=" and org.orgIdString like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                        varmap.put("rootCorpId", rootCorpId);
                    }
                } else {
                    if ("-1".equals(otherDepart)) {
                        //取全部下级组织
                    } else if ("1".equals(otherDepart)) {
                        //取外单位的所有组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql += " and org.orgIdString not like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"$%");
                        } else {
                            sql +=" and org.orgIdString not like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }
                    } else {
                        //本单位的组织
                        if (databaseType.indexOf("db2") >= 0) {
                            sql += " and org.orgIdString like :rootCorpId ";
                            varmap.put("rootCorpId", "%$"+rootCorpId+"$%");
                        } else {
                            sql +=" and org.orgIdString like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',:rootCorpId),'$%')";
                            varmap.put("rootCorpId", rootCorpId);
                        }
                    }
                }
//                qu = session.createQuery(sql);
//                qu.setParameter("rootCorpId", rootCorpId);
               
            } else {
                //取下级组织
                if (!"".equals(currentOrg)) {
                    sql += " and org.orgId<>:currentOrg ";
                }
//                qu = session.createQuery(sql);
//                qu.setParameter("currentOrg", currentOrg);
                varmap.put("currentOrg", currentOrg);
            }
           
          
//            qu.setParameter("orgId", orgId);
//            qu.setParameter("domainId", domainId);
            varmap.put("orgId", orgId);
            varmap.put("domainId", domainId);
           
            sql += " ORDER BY org.orgOrderCode";
            
            
            //map.put("org", qu.list());
            map.put("org", db.getDataListByHQL(sql, varmap));
            
            Query query = session.createQuery("SELECT emp.empId,emp.empName,emp.userAccounts,emp.curStatus,emp.imId, emp.empSex FROM com.whir.org.vo.usermanager.UserPO emp join emp.organizations org WHERE (org.orgId=:orgId or emp.sidelineOrg like :sidelineOrg) AND emp.userIsActive=1 and emp.userIsDeleted=0  ORDER BY emp.empDutyLevel,emp.userOrderCode,emp.empName");
            query.setParameter("orgId", orgId);
            query.setParameter("sidelineOrg", "%*" + orgId + "*%");
            
            map.put("user", query.list());
        } catch (Exception e) {
            System.out.println("error ManagerEJB :" + e.getMessage());
            throw e;
        } finally {
            session.close();

        }
        return map;
    }

    /**
     * 根据userId获取兼职组织
     * @param userId String
     * @throws Exception
     * @return UserPO
     */
    private UserPO getSidelineOrgByUserId(String userId, boolean lazy) throws Exception {
        UserPO userPO = null;
        begin();
        try {
            userPO = (UserPO)session.load(UserPO.class, new Long(userId));
            if(lazy){
                Hibernate.initialize(userPO.getOrganizations());
            }
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return userPO;
    }
    
    private String getOrgIdByUserId(String empId) throws Exception {
        try {
            begin();
            Query query = session
                    .createQuery(
                            "SELECT org.orgId FROM com.whir.org.vo.usermanager.UserPO emp join emp.organizations org WHERE emp.empId=:empId");
            query.setParameter("empId", empId);
            
            List list = query.list();
            if(list != null){
                Object obj = (Object)list.get(0);
                
                return obj.toString();
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            try{
                session.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return null;
    }
}
