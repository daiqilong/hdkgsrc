package com.whir.org.bd.organizationmanager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

import org.apache.log4j.Logger;

import com.whir.common.db.Dbutil;
import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.CommonUtils;
import com.whir.common.util.EJBProxy;
import com.whir.common.util.ParameterGenerator;
import com.whir.common.util.StringSplit;
import com.whir.org.common.util.OrganizationEJBProxy;
import com.whir.org.ejb.organizationmanager.OrganizationEJBHome;
import com.whir.org.vo.organizationmanager.OrganizationVO;

/**
 * <p>Title: OrganizationBD</p>
 * <p>Description:
 * 包含对组织信息的添加，更新，删除，查找等一般性的操作</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: WanHu Internet Resource(Hefei) C0. Ltd.</p>
 * @author di
 * @version 1.0
 */
public class OrganizationBD extends HibernateBase {
    /**
     *用于记录日志
     */
    private static Logger logger = Logger.getLogger(OrganizationBD.class.
            getName());

    /**
     * 默认的构造函数
     */
    public OrganizationBD() {
    }

    /**
     * 向数据库中添加组织信息
     * @param organizationVO  组织值对象
     * @param position  添加该组织的基准位置
     * @param sort  判断添加到基准点的何处 0:向前 1:向后
     * @return 如果添加成功返回true
     */
    public long add(OrganizationVO organizationVO, String currentOrderCode,
                       String parentIdString, Integer sort) {
        long addResult = 0;
        ParameterGenerator pg = new ParameterGenerator(4);
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            pg.put(organizationVO, OrganizationVO.class);
            pg.put(currentOrderCode, "String");
            pg.put(parentIdString, "String");
            pg.put(sort, java.lang.Integer.class);
            addResult = ((Long)ejbProxy.invoke("add", pg.getParameters())).longValue();
        } catch (Exception e) {
            logger.error("Error to add Organization information:" +
                         e.getMessage());
        } finally {
            return addResult;
        }
    }

    /**
     * 删除组织信息持久对象
     * @param key 组织信息id
     * @return 如果删除成功返回true
     */
    public String delete(long key) {
        String result = "";
        Object[] parameters = new Object[1];
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            parameters[0] = new Long(key);
            result = (String)ejbProxy.invoke("delete", parameters);
        } catch (Exception e) {
            logger.error("Error to delete Organization information:" +
                         e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 修改组织信息
     * @param organizationVO OrganizationVO 要修改的组织持久对象
     * @param currentOrderCode String 相对排序组织的排序串
     * @param parentIdString String 父组织的idString
     * @param sort Integer 向选择的组织的前后位置
     * @return boolean 修改成功返回true
     */
    public boolean update(OrganizationVO organizationVO,
                          String currentOrderCode, String parentIdString,
                          Integer sort, String hasChanged) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(5);
            pg.put(organizationVO, OrganizationVO.class);
            pg.put(currentOrderCode, "String");
            pg.put(parentIdString, "String");
            pg.put(sort, java.lang.Integer.class);
            pg.put(hasChanged, "String");

            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            ejbProxy.invoke("update", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("Error to update Organization information:" +
                         e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 查询所有的组织机构信息
     * @return Iterator 迭代器,包含所有组织机构信息
     */
    public List getAllOrgs() {
        List allOrgArray = null;
        EJBProxy ejbProxy = null;
        try {
            ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                                                "OrganizationEJBLocal",
                                                OrganizationEJBHome.class);
            allOrgArray = (List)ejbProxy.invoke("getAllOrgs", null);
        } catch (Exception e) {
            logger.error("Can not get organization's info:" + e.getMessage());
        }
        return allOrgArray;
    }

    /**
     * 取得所有有效的组织（未被删除的）
     * @return List
     */
    public List getValidOrgs() {
        List validOrgs = null;
        EJBProxy ejbProxy = null;
        try {
            ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                                                "OrganizationEJBLocal",
                                                OrganizationEJBHome.class);
            validOrgs = (List)ejbProxy.invoke("getValidOrgs", null);
        } catch (Exception e) {
            logger.error("Can not get organization's info:" + e.getMessage());
        }
        return validOrgs;
    }

    /**
     * 取得某个组织的下级组织
     * @param orgId String
     * @return 组织列表
     */
    public List getSubOrgs(String orgId) {
        List subOrg = null;
        ParameterGenerator parameterGenerator = new ParameterGenerator(1);
        parameterGenerator.put(new Long(orgId), "Long");
        EJBProxy ejbProxy = null;
        try {
            ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                                                "OrganizationEJBLocal",
                                                OrganizationEJBHome.class);
            subOrg = (List)ejbProxy.invoke("getSubOrgs",
                                           parameterGenerator.getParameters());
        } catch (Exception e) {
            logger.error("Can not get organization's info:" + e.getMessage());
        }
        return subOrg;
    }
    
    public List getSubOrgs(String orgId, String domainId) {
        List subOrg = null;
        ParameterGenerator parameterGenerator = new ParameterGenerator(2);
        parameterGenerator.put(new Long(orgId), "Long");
        parameterGenerator.put(domainId, "String");
        EJBProxy ejbProxy = null;
        try {
            ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                                                "OrganizationEJBLocal",
                                                OrganizationEJBHome.class);
            subOrg = (List)ejbProxy.invoke("getSubOrgs",
                                           parameterGenerator.getParameters());
        } catch (Exception e) {
            logger.error("Can not get organization's info:" + e.getMessage());
        }
        return subOrg;
    }

    /**
     * 取得某个组织的用户
     * @param orgId String
     * @return 用户列表
     */
    public Set getSubUsers(String orgId) {
        Set subUser = null;
        ParameterGenerator parameterGenerator = new ParameterGenerator(1);
        parameterGenerator.put(new Long(orgId), "Long");
        EJBProxy ejbProxy = null;
        try {
            ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                                                "OrganizationEJBLocal",
                                                OrganizationEJBHome.class);
            subUser = (Set)ejbProxy.invoke("getSubUsers",
                                           parameterGenerator.getParameters());
        } catch (Exception e) {
            logger.error("Can not get organization's info:" + e.getMessage());
        }
        return subUser;
    }

    /**
     * 取得所有有组织频道的组织信息（包括其组织ID，完整组织名称，组织ID字符串）
     * @return List
     */
    public List getHasChannel() {
        List list = null;
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            list = (List)ejbProxy.invoke("getHasChannel", null);
        } catch (Exception e) {
            logger.error("errot to getHasChannel information :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 根据组织id，取组织名称及其上级组织名称串
     * @param orgId String
     * @return String
     */
    public String getOrgName(String orgId) {
        String orgFullName = "";
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(orgId, java.lang.String.class);
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            orgFullName = (String)ejbProxy.invoke("getOrgName",
                                                  pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getOrgName information :" + e.getMessage());
        } finally {
            return orgFullName;
        }
    }

    /**
     * 取一个组织的下级组织
     * @param orgId String
     * @return List
     */
    public List getSons(String orgId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(orgId, java.lang.String.class);
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            list = (List)ejbProxy.invoke("getSons", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getSons information :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 取一个组织的上级组织
     * @param orgId String
     * @return List
     */
    public List getSuperior(String orgId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(orgId, java.lang.String.class);
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            list = (List)ejbProxy.invoke("getSuperior", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getSuperior information :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 取得组织的组织Id，组织名称
     * @param orgIds String 一个包含一个或多个组织ID的串，如*101**187*
     * @return List
     */
    public List getNameAndId(String orgIds) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(orgIds, java.lang.String.class);
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            list = (List)ejbProxy.invoke("getNameAndId", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getOrgNameAndId :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 取得某个组织或多个组织下的所有子组织
     * @param orgIds String 一个包含一个或多个组织ID的串，如*101**187*
     * @return List 返回子组织的信息(组织ID，名称，级别，是否有下级组织等);
     */
    public List getValidOrgsByRange(String orgIds, String domainId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(orgIds, java.lang.String.class);
            pg.put(domainId, String.class);
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            list = (List)ejbProxy.invoke("getValidOrgsByRange",
                                         pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getValidOrgsByRange :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 取所有组织的orgId,orgName,orgHasChannel,orgLevel,orgParentOrgId
     * @return List
     */
    public List getSimpleOrg(String userId, String domainId) {
        List list = null;
        ParameterGenerator pg = new ParameterGenerator(2);
        pg.put(userId, "String");
        pg.put(domainId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            list = (List)ejbProxy.invoke("getSimpleOrg", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getSimpleOrg information :" + e.getMessage());
        } finally {
            return list;
        }
    }

    public List getAllChannelList(String userId) {
        List list = null;
        ParameterGenerator pg = new ParameterGenerator(1);
        pg.put(userId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            list = (List)ejbProxy.invoke("getAllChannelList", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getChannelOrg information :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 检查组织编码是否重复
     * @param orgId String
     * @param orgSerial String
     * @return Integer 0:不重复  1:重复
     */
    public Integer checkOrganizationSerial(String orgId, String orgSerial) {
        Integer result = null;
        ParameterGenerator pg = new ParameterGenerator(2);
        pg.put(orgId, "String");
        pg.put(orgSerial, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            result = (Integer)ejbProxy.invoke("checkOrganizationSerial",
                                              pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getChannelOrg information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    public com.whir.org.vo.organizationmanager.DomainVO loadDomain(String
            domainId) {
        com.whir.org.vo.organizationmanager.DomainVO result = null;
        ParameterGenerator pg = new ParameterGenerator(1);
        pg.put(domainId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            result = (com.whir.org.vo.organizationmanager.DomainVO)ejbProxy.
                    invoke("loadDomain", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to loadDomain information :" + e.getMessage());
        } finally {
            return result;
        }

    }

    /**
     * date:2009-03-31
     * 根据父组织id，本组织名称来获得本组织信息
     * @param parentOrgId String
     * @param newOrgName String
     * @param domainId String
     * @return OrganizationVO
     */
    public com.whir.org.vo.organizationmanager.OrganizationVO
            getOrgListByParentOrgIdAndOrgName(String parentOrgId,
                                              String newOrgName,
                                              String domainId) {
        com.whir.org.vo.organizationmanager.OrganizationVO returnOrg = null;
        ParameterGenerator pg = new ParameterGenerator(3);
        pg.put(parentOrgId, "String");
        pg.put(newOrgName, "String");
        pg.put(domainId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            returnOrg = (com.whir.org.vo.organizationmanager.OrganizationVO)
                    ejbProxy.invoke("getOrgListByParentOrgIdAndOrgName",
                                    pg.getParameters());
        } catch (Exception e) {
            logger.error(
                    "error to getOrgListByParentOrgIdAndOrgName information :" +
                    e.getMessage());
        } finally {
            return returnOrg;
        }
    }

    /**
     * date:2009-03-31
     * 根据组织id获得该组织的信息
     * @param orgId String
     * @return OrganizationVO
     */
    public com.whir.org.vo.organizationmanager.OrganizationVO getOrgByOrgId(
            String orgId) {
        com.whir.org.vo.organizationmanager.OrganizationVO returnOrg = null;
        ParameterGenerator pg = new ParameterGenerator(1);
        pg.put(orgId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            returnOrg = (com.whir.org.vo.organizationmanager.OrganizationVO)
                    ejbProxy.invoke("getOrgByOrgId", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getOrgByOrgId information :" + e.getMessage());
        } finally {
            return returnOrg;
        }
    }

    /**
     * TODO:根据用户id，获得该用户的兼职组织list
     * @param userId String
     * @return List
     */
    public List getSidelineOrgListByUserId(String userId) {
        List returnList = new ArrayList();
        ParameterGenerator pg = new ParameterGenerator(1);
        pg.put(userId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            returnList = (List)ejbProxy.invoke("getSidelineOrgListByUserId",
                                               pg.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    /**
     * TODO:根据用户id，获得该用户的组织list
     * @param userId String
     * @return List
     */
    public List getUserOrgListByUserId(String userId) {
        List returnList = new ArrayList();
        ParameterGenerator pg = new ParameterGenerator(1);
        pg.put(userId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            returnList = (List)ejbProxy.invoke("getUserOrgListByUserId",
                                               pg.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    /**
     * TODO:根据组织id，获得该组织的基本信息
     * @param orgId String
     * @return Object[]
     */
    public Object[] getOrgInfoByOrgId(String orgId) {
        Object[] orgObj = null;
        ParameterGenerator pg = new ParameterGenerator(1);
        pg.put(orgId, "String");
        try {
            EJBProxy ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                    "OrganizationEJBLocal", OrganizationEJBHome.class);
            orgObj = (Object[])ejbProxy.invoke("getOrgInfoByOrgId",
                                               pg.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return orgObj;
        }
    }

    /**
     * 根据orgId取得父级组织信息
     * @param orgId String
     * @return Object[]
     */
    public Object[] getParentOrgInfoByOrgId(String orgId) {
           Object[] orgObj = null;

           String selectHQL =" select org.orgParentOrgId ";
           selectHQL += " from com.whir.org.vo.organizationmanager.OrganizationVO org ";
           selectHQL += " where org.orgId="+orgId;

           try {
               begin();
               List orgList = session.createQuery(selectHQL).list();
               if(orgList!=null&&orgList.size()>0){
                   Object obj = (Object)orgList.get(0);

                   String selectHQL2 =" select org.orgId,org.orgName,org.orgIdString,org.orgSimpleName,org.orgEnglishName,org.orgNameString ";
                   selectHQL2 += " from com.whir.org.vo.organizationmanager.OrganizationVO org ";
                   selectHQL2 += " where org.orgId="+obj.toString();

                   List list = session.createQuery(selectHQL2).list();

                   if(list!=null&&list.size()>0){
                       orgObj = (Object[])list.get(0);
                       orgObj[2] = StringSplit.splitOrgIdString(orgObj[2].toString(),
                           "$", "_");
                   }
               }
           } catch (Exception ex) {
               ex.printStackTrace();
           } finally {
               try{
                   session.close();
               }catch(Exception e){
                   e.printStackTrace();
               }
               transaction = null;
               session = null;
               return orgObj;
           }
    }

    /**
     * 根据orgId取得该组织的下级组织信息列表（仅包含本组织下级）
     * @throws HibernateException
     * @return List
     */
    public List getSubOrgListByOrgId(String orgId) {
        List validOrgs = new ArrayList();

        try {
            begin();
            String sql = "SELECT org.orgId,org.orgName,org.orgParentOrgId,org.orgManagerEmpId,org.orgManagerEmpName,org.orgLevel,org.orgFoundDate,org.orgOrderCode,org.orgIdString,org.orgDescripte,org.orgHasJunior,org.orgStatus,org.orgHasChannel FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgStatus=0 and org.orgParentOrgId= " + orgId + " ORDER BY org.orgIdString";
            validOrgs = session.createQuery(sql).list();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                session.close();
            } catch(Exception e) {
                e.printStackTrace();
            }

            session = null;
            transaction = null;

        }
        return validOrgs;
    }

    public List getValidOrgsByRangeWithParentId(String range, String orgParentOrgId, String domainId) throws Exception{
        List list=null;
        try {
            String sqlWhere="";
            if(range.equals("*0*") ||range.equals("0")){
                sqlWhere="";
                if(CommonUtils.isEmpty(orgParentOrgId)){
                    orgParentOrgId = "0";
                }
            }else{
                              
            }
            
            if(!CommonUtils.isEmpty(orgParentOrgId)){
                sqlWhere += " and org.orgParentOrgId=:orgParentOrgId ";
            }else{                
                range = "*" + range + "*";
                String[] orgIdArray = range.split("\\*\\*");
                int i = 0,j=0;
                for (i = 0; i < orgIdArray.length; i++) {
                    /*if (!CommonUtils.isEmpty(orgIdArray[i])) {
                        if (j == 0) {
                            sqlWhere = " AND (org.orgIdString like '%$" +
                                       orgIdArray[i] + "$%'";
                        } else {
                            sqlWhere += " OR org.orgIdString like '%$" +
                                    orgIdArray[i] + "$%'";
                        }
                        j++;
                    }*/
                    
                    if (!CommonUtils.isEmpty(orgIdArray[i])) {
                    	long k = Long.parseLong(orgIdArray[i]);
                        if (j == 0) {
                            sqlWhere = " AND (org.orgId =" +
                                       orgIdArray[i];
                        } else {
                            sqlWhere += " OR org.orgId =" +
                                    orgIdArray[i];
                        }
                        j++;
                    }
                }
                if (j > 0) sqlWhere += ")";  
            }
            
            begin();
            
            logger.debug("sqlWhere:"+sqlWhere);
           
            String sql = "SELECT org.orgId,org.orgName,org.orgParentOrgId,org.orgManagerEmpId,org.orgManagerEmpName,org.orgLevel,org.orgFoundDate,org.orgOrderCode,org.orgIdString,org.orgDescripte,org.orgHasJunior,org.orgStatus,org.orgHasChannel,org.orgNameString,org.orgSimpleName,org.orgSerial,org.orgType,org.orgChannelType,org.orgChannelUrl,org.orgEnglishName,org.chargeLeaderIds,org.chargeLeaderNames, org.webserviceUrl FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgStatus=0 "+sqlWhere+" and org.domainId=" + domainId + " ORDER BY org.orgIdString";
            //                        0          1             2                   3                   4                  5             6                7                 8               9                10             11                12              13                14               15           16              17               18               19                  20                    21                    22
            if(!CommonUtils.isEmpty(orgParentOrgId)){
            	 list =session.createQuery(sql).setParameter("orgParentOrgId", orgParentOrgId).list();
            }else{
            	 list =session.createQuery(sql).list();
            }
           
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
           session.close();

        }
        return list;
    }
    
    /**
     * 获取同级的兄弟组织
     * @param range
     * @param orgId
     * @param orgParentOrgId
     * @param domainId
     * @return
     * @throws Exception
     */
    public List getBrotherOrg(String range, String orgId, String orgParentOrgId, String domainId) throws Exception{
        List list=null;
        Dbutil db = new Dbutil();
        HashMap vmap = new HashMap();
        try {
            String sqlWhere;
            if(range.equals("*0*") ||range.equals("0")){
                sqlWhere="";
            }else{
                sqlWhere="";
                range = "*" + range + "*";
                String[] orgIdArray = range.split("\\*\\*");
                int i = 0,j=0;
                for (i = 0; i < orgIdArray.length; i++) {
                    if (!orgIdArray[i].equals("")) {
                    	long k = Long.parseLong(orgIdArray[i]);
                        if (j == 0) {
                            sqlWhere = " AND (org.orgIdString like '%$" +
                                       orgIdArray[i] + "$%'";
                        } else {
                            sqlWhere += " OR org.orgIdString like '%$" +
                                    orgIdArray[i] + "$%'";
                        }
                        j++;
                    }
                }
                if (j > 0) sqlWhere += ")";
            }
            
            sqlWhere += " and org.orgParentOrgId=:orgParentOrgId ";
            vmap.put("orgParentOrgId", orgParentOrgId);
            if(!CommonUtils.isEmpty(orgId)){
                sqlWhere += " and org.orgId<>:orgId ";
                vmap.put("orgId", orgId);
            }
            
            logger.debug("sqlWhere:"+sqlWhere);
            
           // begin();

            String sql = "SELECT org.orgId, org.orgName, org.orgOrderCode FROM com.whir.org.vo.organizationmanager.OrganizationVO org WHERE org.orgStatus=0 "+sqlWhere+" and org.domainId=" + domainId + " ORDER BY org.orgIdString";
            
           // list =session.createQuery(sql).list();
            list = db.getDataListByHQL(sql, vmap);
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
           //session.close();

        }
        return list;
    }
    
    /**
     * 校验组织名称是否存在（不同组织下名称可以重复）
     * @param orgId
     * @param orgName
     * @return 1-存在 0-不存在
     * @throws Exception
     */
    public Integer checkOrgName(String orgId, String orgName, String parentOrgId) throws Exception {
        int result = 0;
        
        String temp_parentOrgId = parentOrgId;
        
        if(CommonUtils.isEmpty(parentOrgId)){
            temp_parentOrgId = "0";
        }
        
        begin();
        try {
            String sql;
            if (orgId == null || "null".equals(orgId) || "".equals(orgId)) {
                sql = "select count(org.orgId) from com.whir.org.vo.organizationmanager.OrganizationVO org where org.orgName=:orgName "
                        +"and org.orgName is not null and org.orgStatus=0 and org.orgParentOrgId=:temp_parentOrgId";
            } else {
                sql = "select count(org.orgId) from com.whir.org.vo.organizationmanager.OrganizationVO org where org.orgName=:orgName "
                        + " and org.orgName is not null and org.orgStatus=0 and org.orgId<>:orgId "
                        + " and org.orgParentOrgId=:temp_parentOrgId";
            }
            Query query = session.createQuery(sql);
            if(orgId == null || "null".equals(orgId) || "".equals(orgId)){
            	
            }else{
            	 query.setParameter("orgId", orgId);
            }
            
            query.setParameter("orgName", orgName);
            query.setParameter("temp_parentOrgId", temp_parentOrgId);
            int num = ((Integer) (query.iterate().next()))
                    .intValue();
            if (num > 0) {
                result = 1;
            }
        } catch (Exception ex) {
            System.out.println("checkOrgName error:" + ex);
            throw ex;
        } finally {
            session.close();
        }
        return (new Integer(result));
    }
    
    /**
     * 判断修改组织时，所属组织是否是该组织的下级子组织，如果是该组织的下级子组织则不可修改
     * @param parentOrgId
     * @param modifyOrgId
     * @return
     * @throws Exception
     */
    public String checkParentOrgIsChildOrg(String parentOrgId,
            String modifyOrgId) throws Exception {
        String result = "0";
        String selectSql = "select count(org.org_id)";
        String fromSql = " from org_organization org";

        String whereSql = " where org.orgstatus=0 ";

        if (!CommonUtils.isEmpty(parentOrgId)) {
            whereSql += " and org.org_id =" + parentOrgId;
        }
        if (!CommonUtils.isEmpty(modifyOrgId)) {
            whereSql += " and org.orgidstring like '%$" + modifyOrgId + "$%' ";
        }

        String sqlStr = selectSql + fromSql + whereSql;

        Connection conn = null;
        DataSource ds = null;
        try {
            ds = new com.whir.common.util.DataSourceBase().getDataSource();
            conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlStr);
            if (rs.next()) {
                result = rs.getString(1);
            }
            rs.close();
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
    
    /**
     * 更新单位主页样式
     * @param orgId
     * @param orgStyle
     * @return
     * @throws Exception
     */
    public String updateOrgStyle(String orgId, String orgStyle) throws Exception {
        String result = "-1";
        try {
            begin();

            // 取得修改前的组织信息
            OrganizationVO modifiedOrgVO = (OrganizationVO) session.load(OrganizationVO.class, new Long(orgId));
            modifiedOrgVO.setOrgStyle(orgStyle);
            session.update(modifiedOrgVO);
            session.flush();
            
            result = "1";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * 获取单位主页样式
     * @param orgId
     * @return
     * @throws Exception
     */
    public String getOrgStyle(String orgId) throws Exception {
        String result = "";
        try {
            begin();

            // 取得修改前的组织信息
            OrganizationVO modifiedOrgVO = (OrganizationVO) session.load(
                    OrganizationVO.class, new Long(orgId));
            String orgStyle = modifiedOrgVO.getOrgStyle();
            if (!CommonUtils.isEmpty(orgStyle)) {
                result = orgStyle;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
    
    /**
     * 更新单位主页Layout
     * 
     * @param orgId
     * @param orgLayoutId
     * @return
     * @throws Exception
     */
    public String updateOrgLayout(String orgId, Long orgLayoutId)
            throws Exception {
        String result = "-1";
        try {
            begin();

            // 取得修改前的组织信息
            OrganizationVO modifiedOrgVO = (OrganizationVO) session.load(
                    OrganizationVO.class, new Long(orgId));
            modifiedOrgVO.setOrgLayoutId(orgLayoutId);
            session.update(modifiedOrgVO);
            session.flush();

            result = "1";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * 获取单位主页Layout
     * 
     * @param orgId
     * @return
     * @throws Exception
     */
    public Long getOrgLayout(String orgId) throws Exception {
        try {
            begin();

            // 取得修改前的组织信息
            OrganizationVO modifiedOrgVO = (OrganizationVO) session.load(
                    OrganizationVO.class, new Long(orgId));
            return modifiedOrgVO.getOrgLayoutId();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }
    
    public boolean hasSubOrgs(String parentId, String domainId) throws Exception {
        List orgArray=new ArrayList();
        try {
            begin();
            String sql =
                    "SELECT organization.orgId " +
                    " FROM com.whir.org.vo.organizationmanager.OrganizationVO organization" +
                    " WHERE organization.orgParentOrgId=" + parentId +
                    " AND organization.domainId=" + domainId + " and organization.orgStatus=0 ";
            orgArray = session.createQuery(sql).list();
            
            if(orgArray != null && orgArray.size() > 0){
                return true;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return false;
    }
    
    /**
     * 获取组织logo信息
     * 
     * @param userId
     * @param orgId
     * @param orgIdString
     * @param domainId
     * @param isDefault
     * @return
     * @throws Exception
     */
    public Map getOrgLogoInfo(String userId, String orgId, String orgIdString, String domainId, boolean isDefault) throws Exception {
        Map result = new HashMap();
        
        String tempIdString = orgId;
        if(orgIdString != null && orgIdString.startsWith("$") && orgIdString.endsWith("$")){
            tempIdString = orgIdString.substring(1, orgIdString.length() - 1);
        }
        
        try {
            begin();
            String[] tempIdArr = tempIdString.split("\\$\\$");
            //从子开始
            for(int i=tempIdArr.length-1; i>=0; i--){
                String orgId_ = tempIdArr[i];
                OrganizationVO vo = (OrganizationVO)session.get(com.whir.org.vo.organizationmanager.OrganizationVO.class, new Long(orgId_));
                if(vo != null){
                    String winTitle = vo.getWinTitle();
                    String logoSaveFileName = vo.getLogoSaveFileName();
                    if(!CommonUtils.isEmpty(logoSaveFileName)){
                        result.put("winTitle", winTitle);
                        result.put("logoSaveFileName", logoSaveFileName);
                        break;
                    }
                } 
            }
        } catch (Exception e) {
            throw e; 
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        
        if(isDefault){
            if(result.get("logoSaveFileName") == null){
                com.whir.org.basedata.po.UnitInfoPO po = new com.whir.org.basedata.bd.UnitSetBD().getUnitInfo(domainId);
                if(po != null && po.getDomainId() != null){
                    result.put("winTitle", po.getUnitName());
                    result.put("logoSaveFileName", po.getUnitImgSaveName());
                }
            }
        }
        
        return result;
    }
    
    private String getOrgWithScope(String userId, String orgId, String orgIdString, String ownerGroupIdStr, String domainId, String parentOrgId, Map map) throws Exception {
        StringBuffer result = new StringBuffer();
        List list1 = (List)map.get(parentOrgId);
        if(list1 != null){
            for(int i=0, j=list1.size(); i<j; i++){
                Object[] obj = (Object[])list1.get(i);
                String orgId_ = obj[0].toString();
                //String orgParentOrgId_ = obj[1].toString();
                String orgIdString_ = obj[2].toString();
                
                String scopeUserIds = (String)obj[3];
                String scopeOrgIds = (String)obj[4];
                String scopeGroupIds = (String)obj[5];
                
                String tempIdStr = !CommonUtils.isEmpty(scopeUserIds) ?scopeUserIds:"";
                tempIdStr += !CommonUtils.isEmpty(scopeOrgIds) ?scopeOrgIds:"";
                tempIdStr += !CommonUtils.isEmpty(scopeGroupIds) ?scopeGroupIds:"";
                boolean isShow = CommonUtils.isContainsScope(tempIdStr, userId, orgId, orgIdString, ownerGroupIdStr, true);
                
                if(isShow){
                    result.append(StringSplit.splitOrgIdString(orgIdString_, "$", "_") + getOrgWithScope(userId, orgId, orgIdString, ownerGroupIdStr, domainId, orgId_, map));
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 获取可查看范围内的组织单位主页
     * @param userId
     * @param orgId
     * @param orgIdString
     * @param ownerGroupIdStr
     * @param domainId
     * @return
     * @throws Exception
     */
    public List getSimpleOrgWithScope(String userId, String orgId, String orgIdString, String ownerGroupIdStr, String domainId) throws Exception {
        List list = new ArrayList();
        begin();
        try{
            String sql = "";
            Query query = null;
                
            Map map = new HashMap();
            
            String sql_1 = " select aaa.orgId,aaa.orgParentOrgId,aaa.orgIdString,aaa.scopeUserIds,aaa.scopeOrgIds,aaa.scopeGroupIds from " +
                " com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                " where aaa.orgStatus=0 and aaa.domainId=" + domainId + " order by aaa.orgIdString ";
            query = session.createQuery(sql_1);                
            List list_1 = query.list();
            if(list_1 != null){
                for(int i=0, j=list_1.size(); i<j; i++){
                    Object[] obj = (Object[])list_1.get(i);
                    String orgParentOrgId_ = obj[1].toString();
                    List tmp = (List)map.get(orgParentOrgId_);
                    if(map.get(orgParentOrgId_) == null){
                        tmp = new ArrayList();
                        tmp.add(obj);
                        map.put(orgParentOrgId_, tmp);
                    }else{
                        tmp.add(obj);
                    }
                }
            }
            
            String showOrgIdStr = getOrgWithScope(userId, orgId, orgIdString, ownerGroupIdStr, domainId, "0", map);
            
            logger.debug("showOrgIdStr:"+showOrgIdStr);
            
            if(showOrgIdStr.length() > 0){
                String showOrgIdStrTemp = showOrgIdStr.substring(1, showOrgIdStr.length()-1);
                showOrgIdStrTemp = showOrgIdStrTemp.replaceAll("\\$\\$", ",");
                sql = " select aaa.orgId,aaa.orgName,aaa.orgHasChannel,aaa.orgLevel,aaa.orgParentOrgId,aaa.orgChannelType,aaa.orgChannelUrl,aaa.orgStyle,aaa.orgLayoutId,aaa.orgIdString,aaa.scopeUserIds,aaa.scopeOrgIds,aaa.scopeGroupIds from " +
                    " com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                    " where aaa.orgStatus=0 and aaa.domainId=" + domainId + " and (" + CommonUtils.convertStr(showOrgIdStrTemp, "aaa.orgId") +") order by aaa.orgIdString ";
                query = session.createQuery(sql);
                list = query.list();
            }
            
        }catch(Exception e){
            System.out.println(e);
            throw e;
        }finally{
            session.close();
            session = null;
            transaction = null;
        }
        
        return list;
    }
    
    /**
     * 根据父组织ID获取下级组织List
     * 
     * @param parentOrgId
     * @param domainId
     * @return
     * @throws Exception
     */
    public List getSubOrgListByParentOrgId(String parentOrgId, String domainId) {
        List subOrg = null;
        ParameterGenerator parameterGenerator = new ParameterGenerator(2);
        parameterGenerator.put(parentOrgId, "String");
        parameterGenerator.put(domainId, "String");
        EJBProxy ejbProxy = null;
        try {
            ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                                                "OrganizationEJBLocal",
                                                OrganizationEJBHome.class);
            subOrg = (List)ejbProxy.invoke("getSubOrgListByParentOrgId",
                                           parameterGenerator.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subOrg;
    }
    
    /**
     * 根据parentOrgId取得该组织的下级组织信息List（包含本组织下级及下级）
     * 
     * @param parentOrgId
     * @param domainId
     * @param isHasSelf
     * @return
     * @throws Exception
     */
    public List getAllSubOrgListByOrgId(String parentOrgId, String domainId, boolean isHasSelf) {
        List subOrg = null;
        ParameterGenerator parameterGenerator = new ParameterGenerator(3);
        parameterGenerator.put(parentOrgId, "String");
        parameterGenerator.put(domainId, "String");
        parameterGenerator.put(isHasSelf, "boolean");
        EJBProxy ejbProxy = null;
        try {
            ejbProxy = new OrganizationEJBProxy("OrganizationEJB",
                                                "OrganizationEJBLocal",
                                                OrganizationEJBHome.class);
            subOrg = (List)ejbProxy.invoke("getAllSubOrgListByOrgId",
                                           parameterGenerator.getParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subOrg;
    }
}
