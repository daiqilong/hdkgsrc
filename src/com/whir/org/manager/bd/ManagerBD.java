package com.whir.org.manager.bd;

import com.whir.common.util.EJBProxy;
import com.whir.common.util.ParameterGenerator;
import com.whir.org.bd.organizationmanager.OrganizationBD;
import com.whir.org.common.util.OrganizationEJBProxy;
import com.whir.org.ejb.organizationmanager.OrganizationEJBHome;
import com.whir.org.manager.ejb.ManagerEJBHome;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class ManagerBD
{
  private static Logger logger = Logger.getLogger(ManagerBD.class.getName());

  public List getSubOrgs(String orgId, String domainId)
  {
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
      logger.error("Can not getSubOrgs info:" + e.getMessage());
    }
    return subOrg;
  }

  public List getUserList(String para, String vo, String where)
  {
    List userList = null;
    ParameterGenerator pg = new ParameterGenerator(3);
    pg.put(para, String.class);
    pg.put(vo, "String");
    pg.put(where, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      userList = (List)ejbProxy.invoke("getUserList", pg.getParameters());
    } catch (Exception e) {
      logger.error("Error to getUserList information List:" + e.getMessage());
    } finally {
      return userList;
    }
  }

  public String getNameBYId(String idString)
  {
    String result = "";
    ParameterGenerator pg = new ParameterGenerator(1);
    try {
      pg.put(idString, "String");
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (String)ejbProxy.invoke("getNameById", pg.getParameters());
    }
    catch (Exception e) {
      logger.error("Error to getNameBYId:" + e.getMessage());
    }
    return result;
  }

  public boolean hasRightType(String userId, String rightType)
  {
    Boolean result = new Boolean(false);
    try {
      ParameterGenerator pg = new ParameterGenerator(2);
      pg.put(userId, String.class);
      pg.put(rightType, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (Boolean)ejbProxy.invoke("hasRightType", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to hasRightType information :" + e.getMessage());
    } finally {
      return result.booleanValue();
    }
  }

  public boolean hasRightTypeName(String userId, String rightType, String rightName)
  {
    Boolean result = new Boolean(false);
    try {
      ParameterGenerator pg = new ParameterGenerator(3);
      pg.put(userId, String.class);
      pg.put(rightType, String.class);
      pg.put(rightName, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (Boolean)ejbProxy.invoke("hasRightTypeName", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to hasRightTypeName information :" + 
        e.getMessage());
    } finally {
      return result.booleanValue();
    }
  }

  public List getRightScope(String userId, String rightType, String rightName)
  {
    List list = null;
    try {
      ParameterGenerator pg = new ParameterGenerator(3);
      pg.put(userId, String.class);
      pg.put(rightType, String.class);
      pg.put(rightName, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      list = (List)ejbProxy.invoke("getRightScope", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getRightScope information :" + e.getMessage());
    } finally {
      return list;
    }
  }

  public boolean hasRightTypeScope(String userId, String orgId, String rightType, String rightName, String channelType)
  {
    Boolean result = new Boolean(false);
    try {
      ParameterGenerator pg = new ParameterGenerator(5);
      pg.put(userId, String.class);
      pg.put(orgId, String.class);
      pg.put(rightType, String.class);
      pg.put(rightName, String.class);
      pg.put(channelType, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (Boolean)ejbProxy.invoke("hasRightTypeScope", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to hasRightTypeScope information :" + 
        e.getMessage());
    } finally {
      return result.booleanValue();
    }
  }

  public String getAllJuniorOrgIdByRange(String range)
  {
    String result = "";
    try {
      ParameterGenerator pg = new ParameterGenerator(1);
      pg.put(range, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (String)ejbProxy.invoke("getAllJuniorOrgIdByRange", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getAllJuniorOrgIdByRange information :" + e.getMessage());
    } finally {
      return result;
    }
  }

  public List getValidOrgsByRange(String range, String domainId)
  {
    return new OrganizationBD().getValidOrgsByRange(range, domainId);
  }

  public Map getOrgAndGroupByRange(String range, String group, String orgIdString, String empId, String currentOrg, String domainId)
  {
    Map map = null;
    try {
      ParameterGenerator pg = new ParameterGenerator(6);
      pg.put(range, "String");
      pg.put(group, "String");
      pg.put(orgIdString, "String");
      pg.put(empId, "String");
      pg.put(currentOrg, "String");
      pg.put(domainId, "String");
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      map = (Map)ejbProxy.invoke("getOrgAndGroupByRange", 
        pg.getParameters());
    }
    catch (Exception e) {
      logger.error("error to getOrgAndGroupByRange information :" + 
        e.getMessage());
    }
    return map;
  }

  public Map getSubOrgAndUsers(String orgId, String currentOrg, String domainId, String rootCorpId, String corpId, String departId, String otherDepart)
  {
    ParameterGenerator pg = new ParameterGenerator(7);
    pg.put(orgId, "String");
    pg.put(currentOrg, "String");
    pg.put(domainId, "String");
    pg.put(rootCorpId, "String");
    pg.put(corpId, "String");
    pg.put(departId, "String");
    pg.put(otherDepart, "String");
    Map map = null;
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      map = (Map)ejbProxy.invoke("getSubOrgAndUsers", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to g getSubOrgAndUsers information :" + 
        e.getMessage());
    }
    return map;
  }

  public Map getSelectedGroupAndOrgAndUsers(String selectedId)
  {
    ParameterGenerator pg = new ParameterGenerator(1);
    pg.put(selectedId, "String");
    Map map = null;
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      map = (Map)ejbProxy.invoke("getSelectedGroupAndOrgAndUsers", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error(
        "error to g getSelectedGroupAndOrgAndUsers information :" + 
        e.getMessage());
    }
    return map;
  }

  public String getRightWhere(String userId, String orgId, String orgIdString, String rightType, String rightName, String fieldOrg, String fieldEmp)
  {
    String where = "";
    ParameterGenerator pg = new ParameterGenerator(7);
    pg.put(userId, "String");
    pg.put(orgId, "String");
    pg.put(orgIdString, "String");
    pg.put(rightType, "String");
    pg.put(rightName, "String");
    pg.put(fieldOrg, "String");
    pg.put(fieldEmp, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      where = (String)ejbProxy.invoke("getRightWhere", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getRightWhere information :" + e.getMessage());
    }
    return where;
  }

  public String getRightFinalWhere(String userId, String orgId, String orgIdString, String rightType, String rightName, String fieldOrg, String fieldEmp)
  {
    String where = "";
    ParameterGenerator pg = new ParameterGenerator(7);
    pg.put(userId, "String");
    pg.put(orgId, "String");
    pg.put(orgIdString, "String");
    pg.put(rightType, "String");
    pg.put(rightName, "String");
    pg.put(fieldOrg, "String");
    pg.put(fieldEmp, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      where = (String)ejbProxy.invoke("getRightFinalWhere", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getRightFinalWhere information :" + e.getMessage());
    }
    return where;
  }

  public String getScopeWhere(String userId, String orgId, String orgIdString, String fieldUser, String fieldOrg, String fieldGroup, String fieldCreatedEmp)
  {
    String where = "";
    ParameterGenerator pg = new ParameterGenerator(7);
    pg.put(userId, "String");
    pg.put(orgId, "String");
    pg.put(orgIdString, "String");
    pg.put(fieldUser, "String");
    pg.put(fieldOrg, "String");
    pg.put(fieldGroup, "String");
    pg.put(fieldCreatedEmp, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      where = (String)ejbProxy.invoke("getScopeWhere", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getScopeWhere information :" + e.getMessage());
    }
    return where;
  }

  public String getScopeFinalWhere(String userId, String orgId, String orgIdString, String fieldUser, String fieldOrg, String fieldGroup)
  {
    String where = "";
    ParameterGenerator pg = new ParameterGenerator(6);
    pg.put(userId, "String");
    pg.put(orgId, "String");
    pg.put(orgIdString, "String");
    pg.put(fieldUser, "String");
    pg.put(fieldOrg, "String");
    pg.put(fieldGroup, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      where = (String)ejbProxy.invoke("getScopeFinalWhere", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getScopeFinalWhere information :" + e.getMessage());
    }
    return where;
  }

  public String getEmployeesAccounts(String empIds)
  {
    String result = "";
    ParameterGenerator pg = new ParameterGenerator(1);
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      pg.put(empIds, "String");
      result = (String)ejbProxy.invoke("getEmployeesAccounts", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getEmployeesAccounts information :" + e.getMessage());
    } finally {
      return result;
    }
  }

  public boolean hasRight(String userId, String rightCode)
  {
    Boolean result = new Boolean(false);
    try {
      ParameterGenerator pg = new ParameterGenerator(2);
      pg.put(userId, String.class);
      pg.put(rightCode, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (Boolean)ejbProxy.invoke("hasRight", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to hasRight information :" + 
        e.getMessage());
    } finally {
      return result.booleanValue();
    }
  }

  public boolean hasRightTypeScope(String userId, String orgId, String rightCode, String channelType)
  {
    Boolean result = new Boolean(false);
    try {
      ParameterGenerator pg = new ParameterGenerator(4);
      pg.put(userId, String.class);
      pg.put(orgId, String.class);
      pg.put(rightCode, String.class);
      pg.put(channelType, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (Boolean)ejbProxy.invoke("hasRightTypeScope", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to hasRightTypeScope information :" + 
        e.getMessage());
    } finally {
      return result.booleanValue();
    }
  }

  public List getRightScope(String userId, String rightCode)
  {
    List list = null;
    try {
      ParameterGenerator pg = new ParameterGenerator(2);
      pg.put(userId, String.class);
      pg.put(rightCode, String.class);
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      list = (List)ejbProxy.invoke("getRightScope", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getRightScope information :" + e.getMessage());
    } finally {
      return list;
    }
  }

  public String getRightWhere(String userId, String orgId, String rightCode, String fieldOrg, String fieldEmp)
  {
    String where = "";
    ParameterGenerator pg = new ParameterGenerator(5);
    pg.put(userId, "String");
    pg.put(orgId, "String");
    pg.put(rightCode, "String");
    pg.put(fieldOrg, "String");
    pg.put(fieldEmp, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      where = (String)ejbProxy.invoke("getRightWhere", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getRightWhere information :" + e.getMessage());
    }
    return where;
  }

  public String getRightFinalWhere(String userId, String orgId, String rightCode, String fieldOrg, String fieldEmp)
  {
    String where = "";
    ParameterGenerator pg = new ParameterGenerator(5);
    pg.put(userId, "String");
    pg.put(orgId, "String");
    pg.put(rightCode, "String");
    pg.put(fieldOrg, "String");
    pg.put(fieldEmp, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      where = (String)ejbProxy.invoke("getRightFinalWhere", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getRightFinalWhere information :" + e.getMessage());
    }
    return where;
  }

  /** @deprecated */
  public Map getLeft(String range, String group)
  {
    Map result = new HashMap();
    ParameterGenerator pg = new ParameterGenerator(2);
    pg.put(range, "String");
    pg.put(group, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (Map)ejbProxy.invoke("getLeft", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getLeft information :" + e.getMessage());
    }
    return result;
  }

  public List getAllDuty(String domainId)
  {
    List result = new ArrayList();
    ParameterGenerator pg = new ParameterGenerator(1);
    pg.put(domainId, "String");
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      result = (List)ejbProxy.invoke("getAllDuty", 
        pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getAllDuty :" + e.getMessage());
    }
    return result;
  }

  public Map getSubOrgAndAllUsers(String orgId, String currentOrg, String domainId, String rootCorpId, String corpId, String departId, String otherDepart)
  {
    ParameterGenerator pg = new ParameterGenerator(7);
    pg.put(orgId, "String");
    pg.put(currentOrg, "String");
    pg.put(domainId, "String");
    pg.put(rootCorpId, "String");
    pg.put(corpId, "String");
    pg.put(departId, "String");
    pg.put(otherDepart, "String");
    Map map = null;
    try {
      EJBProxy ejbProxy = new OrganizationEJBProxy("ManagerEJB", 
        "ManagerEJBLocal", ManagerEJBHome.class);
      map = (Map)ejbProxy.invoke("getSubOrgAndAllUsers", pg.getParameters());
    } catch (Exception e) {
      logger.error("error to getSubOrgAndAllUsers information :" + 
        e.getMessage());
    }
    return map;
  }

  public boolean isOwnerRangeOrg(String orgId, String range, String chr)
  {
    OrganizationBD orgBD = new OrganizationBD();
    Object[] orgObj = orgBD.getOrgInfoByOrgId(orgId);
    if (orgObj != null) {
      String orgIdStr = (String)orgObj[2];
      return isOwnerRangeOrgStr(orgIdStr, range, chr);
    }

    return false;
  }

  public boolean isOwnerRangeOrgStr(String orgIdStr, String range, String chr) {
    logger.debug("orgIdStr:" + orgIdStr);
    logger.debug("range:" + range);
    if (orgIdStr != null) {
      orgIdStr = orgIdStr.substring(1, orgIdStr.length() - 1);
      String[] orgIdArr = orgIdStr.split("\\$\\$");
      for (int i = 0; i < orgIdArr.length; i++) {
        if (range.indexOf(chr + orgIdArr[i] + chr) != -1) {
          return true;
        }
      }
    }

    return false;
  }
}