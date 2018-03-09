package com.whir.service.api.ezflowservice;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.WhirEzFlowDesignerEntity;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezflow.vo.TransactorInfoVO;
import com.whir.ezflow.vo.UserInfoVO;
import com.whir.ezoffice.assetManager.bd.CommentBD;
import com.whir.ezoffice.bpm.util.SynPoolProcessSet;

public class EzFlowProcessDefinitionService extends EzFlowServiceBase
{
  private static Logger logger = Logger.getLogger(EzFlowProcessDefinitionService.class.getName());
  private static int offset_x = 500;
  private static int offset_y = 0;
  private static int add_offset_y = 300;
  private static int add_offset_x = 0;

  public Map<String, Object> findProcessDefinitions_withMap(Map paraMap, String domainId, String firstResult, String maxResults)
  {
    logger.debug("开始查找可以发起的流程 findProcessDefinitions_withMap:");
    long processDefinitionCount = 0L;
    List processDefinitionList = new ArrayList();
    RepositoryService repositoryService = this.processEngine.getRepositoryService();

    String userAccount = paraMap.get("userAccount")+"";

    String userId = paraMap.get("userId")+"";

    UserInfoVO curUserInfoVO = null;

    if (paraMap.get("curUserInfoVO") != null) {
      curUserInfoVO = (UserInfoVO)paraMap.get("curUserInfoVO");
    }
    else {
      curUserInfoVO = new UserInfoVO();
      if ((userId != null) && (!userId.equals("null")) && (!userId.equals(""))) {
        curUserInfoVO.setUserId(userId);
      } else if ((userAccount != null) && (!userAccount.equals("null")) && (!userAccount.equals(""))) {
        curUserInfoVO.setUserAccount(userAccount);
      } else {
        logger.error("curUserInfoVO  is  null");
        return null;
      }
      userAccount = curUserInfoVO.getUserAccount();
    }

    List scopeOrgs = new ArrayList();
    List scopeGroups = new ArrayList();
    if ((paraMap.get("scopeOrgs") == null) || (paraMap.get("scopeGroups") == null)) {
      Map resultMap = new HashMap();
      Map inMap = new HashMap();
      inMap.put("orgIdString", curUserInfoVO.getOrgVO().getOrgIdString());
      inMap.put("userAccount", curUserInfoVO.getUserAccount());
      inMap.put("className", "UserInfoService");
      inMap.put("method_code", "getOrgsAndGroupsById");
      RuntimeService runtimeService = this.processEngine.getRuntimeService();
      resultMap = runtimeService.Whir_AllService(inMap);
      scopeOrgs = (List)resultMap.get("myOrgList");
      scopeGroups = (List)resultMap.get("myGroupList");
    } else {
      scopeOrgs = (List)paraMap.get("myOrgList");
      scopeGroups = (List)paraMap.get("myGroupList");
    }

    List userList = new ArrayList();
    if (EzFlowUtil.judgeNull(userAccount)) {
      userList.add("%$" + userAccount + "$%");
    }

    String processPackage = paraMap.get("processPackage")+"";
    String processPackageName = paraMap.get("processPackageName")+"";

    String processName = paraMap.get("processName")+"";

    String mobileType = paraMap.get("mobileType")+"";

    String moduleId = paraMap.get("moduleId")+"";

    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion().whir_type(new Integer("0"));

    if ((userList != null) && (userList.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeUsers(userList);
    }

    if ((scopeOrgs != null) && (scopeOrgs.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeOrgs(scopeOrgs);
    }

    if ((scopeGroups != null) && (scopeGroups.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeGroups(scopeGroups);
    }

    if (EzFlowUtil.judgeNull(processPackage)) {
      processDefinitionQuery = processDefinitionQuery.whir_processPackage(processPackage);
    }
    if (EzFlowUtil.judgeNull(processPackageName)) {
      processDefinitionQuery = processDefinitionQuery.whir_processPackageName(processPackageName);
    }

    if (EzFlowUtil.judgeNull(mobileType)) {
      if (mobileType.equals("mobilePhone")) {
        processDefinitionQuery = processDefinitionQuery.whir_mobilePhoneStatus("1");
      }
      else {
        processDefinitionQuery = processDefinitionQuery.whir_mobileStatus("1");
      }

    }

    if (EzFlowUtil.judgeNull(moduleId)) {
      processDefinitionQuery = processDefinitionQuery.whir_moduleId(moduleId);
    }

    logger.debug("moduleId:" + moduleId);

    if (EzFlowUtil.judgeNull(processName)) {
      processDefinitionQuery = processDefinitionQuery.processDefinitionNameLike("%" + processName + "%");
    }

    processDefinitionCount = processDefinitionQuery.count_whir();

    if ((EzFlowUtil.judgeNull(firstResult)) && (EzFlowUtil.judgeNull(maxResults)))
      processDefinitionList = processDefinitionQuery.listPage_whir(Integer.parseInt(firstResult), Integer.parseInt(maxResults));
    else {
      processDefinitionList = processDefinitionQuery.noNeedCash().list_whir();
    }

    Map resultMap = new HashMap();
    resultMap.put("resultCount", Long.valueOf(processDefinitionCount));
    resultMap.put("resultList", processDefinitionList);
    return resultMap;
  }

  public Map<String, Object> findProcessDefinitions(String userAccount, List<String> scopeOrgs, List<String> scopeGroups, String processPackage, String domainId, String firstResult, String maxResults)
  {
    long processDefinitionCount = 0L;
    List processDefinitionList = new ArrayList();
    RepositoryService repositoryService = this.processEngine.getRepositoryService();

    List userList = new ArrayList();
    if (EzFlowUtil.judgeNull(userAccount)) {
      userList.add("%$" + userAccount + "$%");
    }

    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion().whir_type(new Integer("0"));

    if ((userList != null) && (userList.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeUsers(userList);
    }

    if ((scopeOrgs != null) && (scopeOrgs.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeOrgs(scopeOrgs);
    }

    if ((scopeGroups != null) && (scopeGroups.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeGroups(scopeGroups);
    }

    if (EzFlowUtil.judgeNull(processPackage)) {
      processDefinitionQuery = processDefinitionQuery.whir_processPackage(processPackage);
    }

    processDefinitionCount = processDefinitionQuery.count_whir();

    if ((EzFlowUtil.judgeNull(firstResult)) && (EzFlowUtil.judgeNull(maxResults)))
      processDefinitionList = processDefinitionQuery.listPage_whir(Integer.parseInt(firstResult), Integer.parseInt(maxResults));
    else {
      processDefinitionList = processDefinitionQuery.noNeedCash().list_whir();
    }

    Map resultMap = new HashMap();
    resultMap.put("resultCount", Long.valueOf(processDefinitionCount));
    resultMap.put("resultList", processDefinitionList);
    return resultMap;
  }

  public Map<String, Object> findProcessDefinitions(UserInfoVO curUserInfoVO, String processPackage, String domainId, String firstResult, String maxResults)
  {
    logger.debug("开始取发起范围的流程");
    List scopeOrgs = new ArrayList();
    List scopeGroups = new ArrayList();
    String userAccount = curUserInfoVO.getUserAccount();
    Map resultMap = new HashMap();

    Map inMap = new HashMap();
    inMap.put("orgIdString", curUserInfoVO.getOrgVO().getOrgIdString());
    inMap.put("userAccount", curUserInfoVO.getUserAccount());
    inMap.put("className", "UserInfoService");
    inMap.put("method_code", "getOrgsAndGroupsById");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);
    scopeOrgs = (List)resultMap.get("myOrgList");

    scopeGroups = (List)resultMap.get("myGroupList");

    logger.debug("开始调用findProcessDefinitions");
    return findProcessDefinitions(userAccount, scopeOrgs, scopeGroups, processPackage, domainId, firstResult, maxResults);
  }

  public Map<String, Object> findProcessDefinitions_mobile(UserInfoVO curUserInfoVO, String processPackage, String domainId, String mobileType, String firstResult, String maxResults)
  {
    logger.debug("开始取发起范围的流程");
    List scopeOrgs = new ArrayList();
    List scopeGroups = new ArrayList();
    String userAccount = curUserInfoVO.getUserAccount();
    Map resultMap = new HashMap();

    Map inMap = new HashMap();
    inMap.put("orgIdString", curUserInfoVO.getOrgVO().getOrgIdString());
    inMap.put("userAccount", curUserInfoVO.getUserAccount());
    inMap.put("className", "UserInfoService");
    inMap.put("method_code", "getOrgsAndGroupsById");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);
    scopeOrgs = (List)resultMap.get("myOrgList");

    scopeGroups = (List)resultMap.get("myGroupList");

    logger.debug("开始调用findProcessDefinitions");
    return findProcessDefinitions_mobile(userAccount, scopeOrgs, scopeGroups, processPackage, domainId, firstResult, maxResults, mobileType);
  }

  public Map<String, Object> findProcessDefinitions_mobile(String userAccount, List<String> scopeOrgs, List<String> scopeGroups, String processPackage, String domainId, String firstResult, String maxResults, String mobileType)
  {
    long processDefinitionCount = 0L;
    List processDefinitionList = new ArrayList();
    RepositoryService repositoryService = this.processEngine.getRepositoryService();

    List userList = new ArrayList();
    if (EzFlowUtil.judgeNull(userAccount)) {
      userList.add("%$" + userAccount + "$%");
    }

    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
      .latestVersion().whir_type(new Integer("0"));

    if (mobileType.equals("mobilePhone")) {
      processDefinitionQuery = processDefinitionQuery.whir_mobilePhoneStatus("1");
    }
    else {
      processDefinitionQuery = processDefinitionQuery.whir_mobileStatus("1");
    }

    if ((userList != null) && (userList.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeUsers(userList);
    }

    if ((scopeOrgs != null) && (scopeOrgs.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeOrgs(scopeOrgs);
    }

    if ((scopeGroups != null) && (scopeGroups.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processScopeGroups(scopeGroups);
    }

    if (EzFlowUtil.judgeNull(processPackage)) {
      processDefinitionQuery = processDefinitionQuery.whir_processPackage(processPackage);
    }

    processDefinitionCount = processDefinitionQuery.count_whir();

    if ((EzFlowUtil.judgeNull(firstResult)) && (EzFlowUtil.judgeNull(maxResults)))
      processDefinitionList = processDefinitionQuery.listPage_whir(Integer.parseInt(firstResult), Integer.parseInt(maxResults));
    else {
      processDefinitionList = processDefinitionQuery.noNeedCash().list_whir();
    }

    Map resultMap = new HashMap();
    resultMap.put("resultCount", Long.valueOf(processDefinitionCount));
    resultMap.put("resultList", processDefinitionList);
    return resultMap;
  }

  public Map<String, Object> findViewProcessDefinitions(UserInfoVO curUserInfoVO, String processPackage, String domainId, String firstResult, String maxResults)
  {
    return findViewProcessDefinitions(curUserInfoVO, processPackage, "", "", domainId, firstResult, maxResults);
  }

  public Map<String, Object> findViewProcessDefinitions(UserInfoVO curUserInfoVO, String processPackage, String searchProcessName, String searchPackageName, String domainId, String firstResult, String maxResults)
  {
    Map scopeResultMap = new HashMap();

    logger.debug("service后台取数据开始");

    List userList = new ArrayList();

    List scopeOrgs = new ArrayList();

    List scopeGroups = new ArrayList();
    Map inMap = new HashMap();
    inMap.put("orgIdString", curUserInfoVO.getOrgVO().getOrgIdString());
    inMap.put("userAccount", curUserInfoVO.getUserAccount());
    inMap.put("className", "UserInfoService");
    inMap.put("method_code", "getScopeWithId");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    scopeResultMap = runtimeService.Whir_AllService(inMap);
    scopeOrgs = (List)scopeResultMap.get("myOrgList");
    scopeGroups = (List)scopeResultMap.get("myGroupList");

    long processDefinitionCount = 0L;
    List processDefinitionList = new ArrayList();

    RepositoryService repositoryService = this.processEngine.getRepositoryService();
    if (EzFlowUtil.judgeNull(curUserInfoVO.getUserId())) {
      userList.add("%$" + curUserInfoVO.getUserId() + "$%");
    }

    ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion().whir_type(new Integer("0"));
    if ((userList != null) && (userList.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processCanReadUsers(userList);
      processDefinitionQuery = processDefinitionQuery.whir_processCanModifyUsers(userList);
      processDefinitionQuery = processDefinitionQuery.whir_processAdminUsers(userList);
    }

    if ((scopeOrgs != null) && (scopeOrgs.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processCanReadOrgs(scopeOrgs);
      processDefinitionQuery = processDefinitionQuery.whir_processCanModifyOrgs(scopeOrgs);
      processDefinitionQuery = processDefinitionQuery.whir_processAdminOrgs(scopeOrgs);
    }

    if ((scopeGroups != null) && (scopeGroups.size() > 0)) {
      processDefinitionQuery = processDefinitionQuery.whir_processAdminGroups(scopeGroups);
      processDefinitionQuery = processDefinitionQuery.whir_processCanReadGroups(scopeGroups);
      processDefinitionQuery = processDefinitionQuery.whir_processCanModifyGroups(scopeGroups);
    }

    if (EzFlowUtil.judgeNull(processPackage)) {
      processDefinitionQuery = processDefinitionQuery.whir_processPackage(processPackage);
    }

    logger.debug("searchPackageName:" + searchPackageName);

    if (EzFlowUtil.judgeNull(searchPackageName)) {
      processDefinitionQuery = processDefinitionQuery.whir_processPackageName("%" + searchPackageName + "%");
    }
    logger.debug("searchProcessName:" + searchProcessName);

    if (EzFlowUtil.judgeNull(searchProcessName)) {
      processDefinitionQuery = processDefinitionQuery.processDefinitionNameLike("%" + searchProcessName + "%");
    }

    processDefinitionCount = 0L;

    if ((EzFlowUtil.judgeNull(firstResult)) && (EzFlowUtil.judgeNull(maxResults))) {
      processDefinitionCount = processDefinitionQuery.count_whir();
      processDefinitionList = processDefinitionQuery.listPage_whir(Integer.parseInt(firstResult), Integer.parseInt(maxResults));
    } else {
      processDefinitionList = processDefinitionQuery.noNeedCash().list_whir();
    }

    Map totalresultMap = new HashMap();
    totalresultMap.put("resultCount", Long.valueOf(processDefinitionCount));
    totalresultMap.put("resultList", processDefinitionList);
    return totalresultMap;
  }

  public Map<String, Object> findProcessInfoById(String processDefinitionId)
  {
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    Map parameterMap = new HashMap();
    parameterMap.put("method_code", "findProcessInfoById");
    parameterMap.put("className", "ProcessDefinitionService");
    parameterMap.put("processDefinitionId", processDefinitionId);
    Map resultMap = runtimeService.Whir_AllService(parameterMap);
    return resultMap;
  }

  public Map<String, Object> findProcessInfoByProcessInstanceId(String processInstanceId)
  {
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    Map parameterMap = new HashMap();
    parameterMap.put("method_code", "findProcessInfoByProcessInstanceId");
    parameterMap.put("className", "ProcessDefinitionService");
    parameterMap.put("processInstanceId", processInstanceId);
    Map resultMap = runtimeService.Whir_AllService(parameterMap);
    return resultMap;
  }

  public Map<String, Object> findProcessInfoByKey(String key)
  {
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    Map parameterMap = new HashMap();
    parameterMap.put("method_code", "findProcessInfoByKey");
    parameterMap.put("className", "ProcessDefinitionService");
    parameterMap.put("key", key);
    Map resultMap = runtimeService.Whir_AllService(parameterMap);
    return resultMap;
  }

  public Map<String, Object> deploy(String processDesignerId)
  {
    Map map = new HashMap();

    map.put("id", processDesignerId);
    map.put("className", "DesignerService");
    map.put("method_code", "get");

    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    map = runtimeService.Whir_AllService(map);
    WhirEzFlowDesignerEntity whirEzFlowDesignerEntity = (WhirEzFlowDesignerEntity)map.get("whirEzFlowDesignerEntity");

    return deployByEntity(whirEzFlowDesignerEntity);
  }

  public Map<String, Object> deployByEntity(WhirEzFlowDesignerEntity whirEzFlowDesignerEntity)
  {
    Map map = new HashMap();
    map.put("id", whirEzFlowDesignerEntity.getId());

    RepositoryService repositoryService = this.processEngine.getRepositoryService();
    String resourceName = whirEzFlowDesignerEntity.getProcessId() + ".bpmn20.xml";

    String xml = whirEzFlowDesignerEntity.getDesignerXml();
    xml = imbarkSubProcess(xml);
    if (xml.equals("-3")) {
      Map resultMap = new HashMap();
      resultMap.put("deploymentId", "-3");
      resultMap.put("error", "");
      return resultMap;
    }

    InputStream imputStram = new StringInputStream(xml, "UTF-8");
    Deployment deployment = repositoryService.createDeployment().addInputStream(resourceName, imputStram).deploy();

    Map updateMap = new HashMap();
    updateMap.put("id", whirEzFlowDesignerEntity.getId());
    updateMap.put("designerXml", whirEzFlowDesignerEntity.getDesignerXml());
    updateMap.put("isdeployed", "1");
    updateMap.put("processId", whirEzFlowDesignerEntity.getProcessId());
    updateMap.put("processName", whirEzFlowDesignerEntity.getProcessName());
    updateMap.put("processScopeIds", whirEzFlowDesignerEntity.getProcessScopeIds());
    updateMap.put("processScopeNames", whirEzFlowDesignerEntity.getProcessScopeNames());
    updateMap.put("sort", whirEzFlowDesignerEntity.getSort());
    updateMap.put("mobileStatus", whirEzFlowDesignerEntity.getMobileStatus());

    updateMap.put("mobilePhoneStatus", whirEzFlowDesignerEntity.getMobilePhoneStatus());
    updateMap.put("processType", whirEzFlowDesignerEntity.getProcessType());
    EzFlowDesignerService dService = new EzFlowDesignerService();
    dService.updateDesigner(updateMap);

    String deploymentId = deployment.getId();
    if (EzFlowUtil.judgeNull(deploymentId))
    {
      setProcessDefSort(whirEzFlowDesignerEntity.getProcessId());

      SynPoolProcessSet syn = new SynPoolProcessSet();
      syn.SynEzFLOWProcess(whirEzFlowDesignerEntity.getProcessId(), false);
    }
    Map resultMap = new HashMap();
    resultMap.put("deploymentId", deploymentId);
    resultMap.put("error", "");
    return resultMap;
  }

  public Map<String, Object> deployByXML(String processId, String xml)
  {
    RepositoryService repositoryService = this.processEngine.getRepositoryService();
    String resourceName = processId + ".bpmn20.xml";

    xml = imbarkSubProcess(xml);
    if (xml.equals("-3")) {
      Map resultMap = new HashMap();
      resultMap.put("deploymentId", "-3");
      resultMap.put("error", "");
      return resultMap;
    }

    InputStream imputStram = new StringInputStream(xml, "UTF-8");
    Deployment deployment = repositoryService.createDeployment().addInputStream(resourceName, imputStram).deploy();

    String deploymentId = deployment.getId();

    Map resultMap = new HashMap();
    resultMap.put("deploymentId", deploymentId);

    List pdeflist = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).list();
    if ((pdeflist != null) && (pdeflist.size() > 0)) {
      ProcessDefinition p = (ProcessDefinition)pdeflist.get(0);
      resultMap.put("processDefinitionId", p.getId());

      setProcessDefInfo(deploymentId);
    }
    resultMap.put("error", "");
    return resultMap;
  }

  public Map<String, Object> deploySubProcess(String subProcessDesignerId)
  {
    String sql = " select  psub.PROCESSKEY from EZ_FLOW_RE_PROCESS_SUB  psub ,ez_flow_de_designer de  where psub.sub_processkey=de.processid  and  de.id=" + 
      subProcessDesignerId;

    logger.debug("deploySubProcess sql:" + sql);
    Map map = new HashMap();
    map.put("className", "UtilService");
    map.put("method_code", "searchBySql_out");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    map.put("sql", sql);
    Map resultMap = runtimeService.Whir_AllService(map);

    sql = " UPDATE  EZ_FLOW_DE_DESIGNER  SET IS_DEPLOYED=1  WHERE  ID='" + subProcessDesignerId + "'";
    map.put("method_code", "updateBySql_out");
    map.put("sql", sql);
    Map resultMap2 = runtimeService.Whir_AllService(map);

    Map inmap = new HashMap();
    inmap.put("className", "DesignerService");
    inmap.put("method_code", "findDesignerByProcessId");

    List mainProcessList = (List)resultMap.get("resultList");

    logger.debug("deploySubProcess  mainProcessList.size:" + (mainProcessList == null ? "0" : Integer.valueOf(mainProcessList.size())));

    String mainProcessKey = "";
    WhirEzFlowDesignerEntity whirEzFlowDesignerEntity = null;
    Map entitymap = null;
    if ((mainProcessList != null) && (mainProcessList.size() > 0)) {
      for (int i = 0; i < mainProcessList.size(); i++) {
        Map mainmap = (Map)mainProcessList.get(i);
        mainProcessKey = mainmap.get("PROCESSKEY")+"";
        logger.debug("mainProcessKey：" + mainProcessKey);
        inmap.put("processId", mainProcessKey);

        entitymap = runtimeService.Whir_AllService(inmap);
        whirEzFlowDesignerEntity = (WhirEzFlowDesignerEntity)entitymap.get("whirEzFlowDesignerEntity");
        deployByEntity(whirEzFlowDesignerEntity);
      }
    }
    return null;
  }

  public String imbarkSubProcess(String mainProcess)
  {
    String resultXml = mainProcess;
    boolean result = true;
    Document doc = null;
    try {
      doc = new SAXBuilder().build(new StringReader(resultXml));
    } catch (Exception e) {
      result = false;
    }
    if (!result) {
      return "导入的xml文件内容不正确，请重新导入！";
    }
    Element defineElement = doc.getRootElement();
    Element processElement = defineElement.getChild("process", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
    List subProcessList = processElement.getChildren(
      "subProcess", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    String processType = processElement.getAttributeValue("processType", 
      Namespace.getNamespace("whir", "http://whir.com/ezFlow"));

    if ((processType == null) || (processType.equals("")) || (processType.equals("null"))) {
      processType = "1";
    }

    logger.debug("subProcessList .size:" + subProcessList.size());
    if ((subProcessList != null) && (subProcessList.size() > 0))
    {
      Element BPMNDiagramElement = defineElement.getChild("BPMNDiagram", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));
      Element mainBPMNPlaneElement = BPMNDiagramElement.getChild("BPMNPlane", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));

      Element subProcessElement = null;
      for (int i = 0; i < subProcessList.size(); i++) {
        subProcessElement = (Element)subProcessList.get(i);

        String subProcessKey = subProcessElement.getAttributeValue("subProcessKey", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
        String subResult = getRealSubProcessXml(subProcessElement, subProcessKey, mainBPMNPlaneElement, i);

        if (subResult.equals("-3")) {
          return "-3";
        }
      }

      logger.debug("main sub resultXml2:\n" + toxml(doc));
      resultXml = toxml(doc);
    }

    if (processType.equals("0")) {
      resultXml = dealRandomProcess(doc);
    }

    return resultXml;
  }

  public String getRealSubProcessXml(Element mainSubProcessElement, String subProcessKey, Element mainBPMNPlaneElement, int index)
  {
    String subProcessXml = "";
    Map map = new HashMap();
    map.put("processId", subProcessKey);
    map.put("className", "DesignerService");
    map.put("method_code", "findDesignerByProcessId");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    map = runtimeService.Whir_AllService(map);
    WhirEzFlowDesignerEntity whirEzFlowDesignerEntity = (WhirEzFlowDesignerEntity)map.get("whirEzFlowDesignerEntity");

    if (whirEzFlowDesignerEntity == null) {
      mainSubProcessElement = null;

      return "-3";
    }

    String y_sbuProcessXml = whirEzFlowDesignerEntity.getDesignerXml();
    boolean result = true;
    Document doc = null;
    try {
      doc = new SAXBuilder().build(new StringReader(y_sbuProcessXml));
    } catch (Exception e) {
      result = false;
    }
    if (!result) {
      return "导入的xml文件内容不正确，请重新导入！";
    }
    Element defineElement = doc.getRootElement();
    Element processElement = defineElement.getChild("process", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    List userTaskElementList = processElement
      .getChildren(
      "userTask", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
    logger.debug("userTaskElementList size:" + userTaskElementList.size());

    List sequenceFlowElementList = processElement
      .getChildren(
      "sequenceFlow", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
    logger.debug("sequenceFlowElementList size:" + sequenceFlowElementList.size());

    List parallelGatewayElementList = processElement
      .getChildren(
      "parallelGateway", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    List exclusiveGatewayElementList = processElement
      .getChildren(
      "exclusiveGateway", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    List inclusiveGatewayElementList = processElement
      .getChildren(
      "inclusiveGateway", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    List callActivityElementList = processElement
      .getChildren(
      "callActivity", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    Element startElement = processElement
      .getChild(
      "startEvent", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    Element endElement = processElement
      .getChild(
      "endEvent", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    Parent p = startElement.getParent();
    p.removeContent(startElement);
    p.removeContent(endElement);

    mainSubProcessElement.addContent(startElement);
    mainSubProcessElement.addContent(endElement);

    if (userTaskElementList.size() > 0) {
      for (int i = 0; i < userTaskElementList.size(); i++) {
        Element userElement = (Element)userTaskElementList.get(i);

        mainSubProcessElement.addContent((Element)userElement.clone());
      }
    }
    if (parallelGatewayElementList.size() > 0) {
      for (int i = 0; i < parallelGatewayElementList.size(); i++) {
        Element userElement = (Element)parallelGatewayElementList.get(i);
        mainSubProcessElement.addContent((Element)userElement.clone());
      }
    }

    if (exclusiveGatewayElementList.size() > 0) {
      for (int i = 0; i < exclusiveGatewayElementList.size(); i++) {
        Element userElement = (Element)exclusiveGatewayElementList.get(i);
        mainSubProcessElement.addContent((Element)userElement.clone());
      }
    }

    if (inclusiveGatewayElementList.size() > 0) {
      for (int i = 0; i < inclusiveGatewayElementList.size(); i++) {
        Element userElement = (Element)inclusiveGatewayElementList.get(i);
        mainSubProcessElement.addContent((Element)userElement.clone());
      }
    }

    if (callActivityElementList.size() > 0) {
      for (int i = 0; i < callActivityElementList.size(); i++) {
        Element userElement = (Element)callActivityElementList.get(i);
        mainSubProcessElement.addContent((Element)userElement.clone());
      }
    }

    if (sequenceFlowElementList.size() > 0)
    {
      for (int i = 0; i < sequenceFlowElementList.size(); i++) {
        Element seqElement = (Element)sequenceFlowElementList.get(i);

        mainSubProcessElement.addContent((Element)seqElement.clone());
      }

    }

    Element BPMNDiagramElement = defineElement.getChild("BPMNDiagram", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));
    Element BPMNPlaneElement = BPMNDiagramElement.getChild("BPMNPlane", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));
    logger.debug("流程图：\n" + toxml(BPMNPlaneElement));

    List grapementList = BPMNPlaneElement.getChildren();
    logger.debug("grapementList.size:" + grapementList.size());
    if ((grapementList != null) && (grapementList.size() > 0)) {
      Element gElement = null;
      Element gElement_clone = null;
      Element xyElement = null;
      List xyElementList = new ArrayList();
      for (int i = 0; i < grapementList.size(); i++) {
        gElement = (Element)grapementList.get(i);
        gElement_clone = (Element)gElement.clone();

        logger.debug("gElement xml" + toxml(gElement));
        logger.debug("gElement_clone xml" + toxml(gElement_clone));

        xyElementList = gElement_clone.getChildren("Bounds", Namespace.getNamespace("http://www.omg.org/spec/DD/20100524/DC"));
        logger.debug("xyElementList.size:" + xyElementList.size());

        if (xyElementList != null) {
          for (int iii = 0; iii < xyElementList.size(); iii++) {
            xyElement = (Element)xyElementList.get(iii);
            if (xyElement != null)
              logger.debug("x,y:1");
            else {
              logger.debug("x,y:2");
            }
            String x = xyElement.getAttribute("x").getValue();
            String y = xyElement.getAttribute("y").getValue();
            logger.debug("x,y:" + x + "," + y);
            int x_int = Integer.parseInt(x) + offset_x + add_offset_x * index;
            int y_int = Integer.parseInt(y) + offset_y + add_offset_y * index;
            xyElement.setAttribute("x", x_int+"");
            xyElement.setAttribute("y", y_int+"");
          }

        }

        mainBPMNPlaneElement.addContent(gElement_clone);
      }
    }
    return subProcessXml;
  }

  public Map<String, Object> getButtonScope(String buttonId, String activityId, String processDefinitionId, String processInstanceId, UserInfoVO curUserInfoVO)
  {
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("buttonCode", buttonId);
    inMap.put("activityId", activityId);
    inMap.put("processDefId", processDefinitionId);
    inMap.put("processInstanceId", processInstanceId);
    inMap.put("curUserInfoVO", curUserInfoVO);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "getButtonDealUserScope");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);

    return resultMap;
  }

  public Map<String, Object> getButtonScope(String buttonId, String activityId, String processDefinitionId, String processInstanceId, UserInfoVO curUserInfoVO, String cur_taskId)
  {
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("buttonCode", buttonId);
    inMap.put("activityId", activityId);
    inMap.put("processDefId", processDefinitionId);
    inMap.put("processInstanceId", processInstanceId);
    inMap.put("curUserInfoVO", curUserInfoVO);
    inMap.put("curTaskId", cur_taskId);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "getButtonDealUserScope");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);

    return resultMap;
  }

  public Map<String, Object> getProcessXmlByProcessDefinitionId(String processDefinitionId)
  {
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("processDefinitionId", processDefinitionId);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "getProcessXmlByProcessDefinitionId");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);

    return resultMap;
  }

  public void setProcessDefSort(String processId, String sort)
  {
    String sql = " update ez_flow_re_procdef set WHIR_SORT=" + sort + " where  key_='" + processId + "' ";
    CommentBD bd = new CommentBD();
    bd.excuteBySQL(sql);
  }

  public void setProcessDefSort(String processId)
  {
    String sql = "update EZ_FLOW_RE_PROCDEF   set WHIR_SORT=(select  sort  from ez_flow_de_designer   where  processid='" + 
      processId + "')" + 
      " where   key_='" + processId + "' ";

    CommentBD bd = new CommentBD();
    bd.excuteBySQL(sql);
  }

  public void setProcessDefInfo(String deployId)
  {
    String sql = "update EZ_FLOW_RE_PROCDEF   set WHIR_SORT=2  ,  WHIR_TYPE=1  where   DEPLOYMENT_ID_='" + 
      deployId + "' ";
    System.out.println("setProcessDefInfo  sql:" + sql);
    CommentBD bd = new CommentBD();
    bd.excuteBySQL(sql);
  }

  public List findProcessDefinitions_other(String userId, String processPackage, String domainId, String firstResult, String maxResults)
  {
    logger.debug("开始取发起范围的流程");
    UserInfoVO curUserInfoVO = new UserInfoVO();
    curUserInfoVO.setUserId(userId);
    List scopeOrgs = new ArrayList();
    List scopeGroups = new ArrayList();
    String userAccount = curUserInfoVO.getUserAccount();
    Map resultMap = new HashMap();

    Map inMap = new HashMap();
    inMap.put("orgIdString", curUserInfoVO.getOrgVO().getOrgIdString());
    inMap.put("userAccount", curUserInfoVO.getUserAccount());
    inMap.put("className", "UserInfoService");
    inMap.put("method_code", "getOrgsAndGroupsById");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);
    scopeOrgs = (List)resultMap.get("myOrgList");
    scopeGroups = (List)resultMap.get("myGroupList");

    logger.debug("开始调用findProcessDefinitions");
    Map allresultMap = findProcessDefinitions(userAccount, scopeOrgs, scopeGroups, processPackage, domainId, firstResult, maxResults);

    List processDefList = (List)allresultMap.get("resultList");

    List processDefinitionList = new ArrayList();

    List packageList = new ArrayList();

    if ((processDefList != null) && (processDefList.size() > 0)) {
      String upPackageId = "";
      ProcessDefinitionEntity processDef = null;
      for (int i = 0; i < processDefList.size(); i++) {
        processDef = (ProcessDefinitionEntity)processDefList.get(i);

        if (!upPackageId.equals(processDef.getWhir_processPackage())) {
          upPackageId = processDef.getWhir_processPackage();

          String[] packageObj = { processDef.getWhir_processPackage(), processDef.getWhir_processPackageName() };
          packageList.add(packageObj);
        }

        processDefinitionList.add(new String[] { processDef.getWhir_processPackage(), processDef.getId(), processDef.getName(), processDef.getKey(), processDef.getWhir_formKey() });
      }
    }
    return processDefinitionList;
  }

  public String toxml(Document doc)
  {
    Format format = Format.getPrettyFormat();
    format.setEncoding("UTF-8");
    format.setIndent(null);
    String xml = "";
    XMLOutputter xmloutputter = new XMLOutputter(format);
    try
    {
      xml = xmloutputter.outputString(doc);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return xml;
  }

  public String toxml(Element doc)
  {
    String xml = "";
    XMLOutputter xmloutputter = new XMLOutputter(Format.getPrettyFormat().setEncoding("UTF-8"));
    try
    {
      xml = xmloutputter.outputString(doc);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return xml;
  }

  public String getInheritParentInfo(String superProcessId, String processDefinitonKey)
  {
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("superProcessId", superProcessId);
    inMap.put("processDefinitonKey", processDefinitonKey);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "getInheritParentInfo");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);

    String whir_inheritParent = resultMap.get("whir_inheritParent") == null ? "false" : 
      resultMap.get("whir_inheritParent").toString();
    return whir_inheritParent;
  }

  public Map<String, Object> getInheritParentInfo2(String superProcessId, String processDefinitonKey)
  {
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("superProcessId", superProcessId);
    inMap.put("processDefinitonKey", processDefinitonKey);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "getInheritParentInfo");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);

    return resultMap;
  }

  public String dealRandomProcess(Document doc)
  {
    Element defineElement = doc.getRootElement();
    Element processElement = defineElement.getChild("process", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    Element BPMNDiagramElement = defineElement.getChild("BPMNDiagram", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));
    Element BPMNPlaneElement = BPMNDiagramElement.getChild("BPMNPlane", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));

    String formKey = processElement.getAttributeValue("formKey", 
      Namespace.getNamespace("whir", "http://whir.com/ezFlow"));

    String formType = processElement.getAttributeValue("formType", 
      Namespace.getNamespace("whir", "http://whir.com/ezFlow"));

    String nodeWriteField = "";

    Element userTask = new Element("userTask", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    processElement.addContent(userTask);
    userTask.setAttribute("taskSequenceType", "parataxis", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("priority", "10", Namespace.getNamespace("activiti", "http://activiti.org/bpmn"));
    userTask.setAttribute("formKey", formKey, Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("formType", formType, Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("overdueType", "0", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("nodeCommentField", "autoCommentField", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("taskNeedRead", "0", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("commentRangeEmpId", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("commentRangeEmpName", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("passNodeCommentField", "nullCommentField", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("nodeWriteField", nodeWriteField, Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("nodeHiddenField", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("protectedField", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("id", "usertask1");
    userTask.setAttribute("name", "随机活动");

    Element documentation = new Element("documentation", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    userTask.addContent(documentation);

    Element uextensionElements = new Element("extensionElements", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    userTask.addContent(uextensionElements);

    Element whirextension_taskParticipantType = addwhirextension("taskParticipantType");
    addwhirextension_field(whirextension_taskParticipantType, "code", "allUser");
    uextensionElements.addContent(whirextension_taskParticipantType);

    Element whirextension_taskDealWithClass = addwhirextension("taskDealWithClass");
    addwhirextension_field(whirextension_taskDealWithClass, "updateData", "update");
    addwhirextension_field(whirextension_taskDealWithClass, "updateStatus", "update_norequest");
    addwhirextension_field(whirextension_taskDealWithClass, "backData", "back");
    addwhirextension_field(whirextension_taskDealWithClass, "backStauts", "back_norequest");
    addwhirextension_field(whirextension_taskDealWithClass, "forminitJsFunName", "");
    addwhirextension_field(whirextension_taskDealWithClass, "formsaveJsFunName", "");
    uextensionElements.addContent(whirextension_taskDealWithClass);

    Element whirextension_activityTip = addwhirextension("activityTip");
    addwhirextension_field(whirextension_activityTip, "enable", "0");
    uextensionElements.addContent(whirextension_activityTip);

    Element whirextension_taskReadParticipantType = addwhirextension("taskReadParticipantType");
    uextensionElements.addContent(whirextension_taskReadParticipantType);

    addButton(uextensionElements, "EzFlowRelationProcess", "关联流程");
    addButton(uextensionElements, "EzFlowFeedback", "反馈");
    addButton(uextensionElements, "EzFlowCancel", "取消");
    addButton(uextensionElements, "EzFlowNewProcess", "新建流程");
    addButton(uextensionElements, "EzFlowPress", "催办");
    addButton(uextensionElements, "EzFlowTranWithMail", "邮件转发");
    addButton(uextensionElements, "EzFlowPrint", "打印");

    Element sequenceFlow1 = new Element("sequenceFlow", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    processElement.addContent(sequenceFlow1);
    sequenceFlow1.setAttribute("id", "sequenceflow1");
    sequenceFlow1.setAttribute("name", "");
    sequenceFlow1.setAttribute("sourceRef", "startevent1");
    sequenceFlow1.setAttribute("targetRef", "usertask1");

    Element sequenceFlow2 = new Element("sequenceFlow", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    processElement.addContent(sequenceFlow2);
    sequenceFlow2.setAttribute("id", "sequenceflow2");
    sequenceFlow2.setAttribute("name", "");
    sequenceFlow2.setAttribute("sourceRef", "usertask1");
    sequenceFlow2.setAttribute("targetRef", "endevent1");

    Element BPMNShape_usertask1 = new Element("BPMNShape", Namespace.getNamespace("bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI"));
    BPMNShape_usertask1.setAttribute("bpmnElement", "usertask1");
    BPMNShape_usertask1.setAttribute("id", "BPMNShape_usertask1");
    BPMNPlaneElement.addContent(BPMNShape_usertask1);

    Element Bounds = new Element("Bounds", Namespace.getNamespace("omgdc", "http://www.omg.org/spec/DD/20100524/DC"));
    BPMNShape_usertask1.addContent(Bounds);
    Bounds.setAttribute("x", "311");
    Bounds.setAttribute("y", "118");
    Bounds.setAttribute("width", "80");
    Bounds.setAttribute("height", "30");

    String resultXml = toxml(doc);
    return resultXml;
  }

  private Element addwhirextension(String whirextension_name)
  {
    Element whirextension = new Element("whirextension", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    whirextension.setAttribute("name", whirextension_name);
    return whirextension;
  }

  private void addwhirextension_field(Element whirextension, String fiedlName, String value)
  {
    Element field = new Element("field", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    field.setAttribute("name", fiedlName);

    Element field_value = new Element("value", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    field_value.addContent(value);
    field.addContent(field_value);

    whirextension.addContent(field);
  }

  private void addButton(Element uextensionElements, String id, String name)
  {
    Element whirextension_taskButtons = addwhirextension("taskButtons");
    addwhirextension_field(whirextension_taskButtons, "id", id);
    addwhirextension_field(whirextension_taskButtons, "name", name);
    uextensionElements.addContent(whirextension_taskButtons);
  }

  public Map<String, Object> getEdAddSignButtonDealUserScope(String buttonId, String activityId, String processDefinitionId, String processInstanceId, UserInfoVO curUserInfoVO)
  {
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("buttonCode", buttonId);
    inMap.put("activityId", activityId);
    inMap.put("processDefId", processDefinitionId);
    inMap.put("processInstanceId", processInstanceId);
    inMap.put("curUserInfoVO", curUserInfoVO);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "getEdAddSignButtonDealUserScope");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);

    return resultMap;
  }

  public Map<String, Object> getFenfaButtonMap(String activityId, String processDefinitionId, String processInstanceId, String cur_taskId, String userId)
  {
    logger.debug("开始  getFenfaButtonMap");

    logger.debug("activityId：" + activityId);
    logger.debug("processDefinitionId：" + processDefinitionId);
    logger.debug("processDefinitionId：" + processDefinitionId);
    logger.debug("cur_taskId：" + cur_taskId);
    logger.debug("userId：" + userId);

    UserInfoVO curUserInfoVO = new UserInfoVO(userId, "", "");
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("buttonCode", "Sendclose");
    inMap.put("activityId", activityId);
    inMap.put("processDefId", processDefinitionId);
    inMap.put("processInstanceId", processInstanceId);
    inMap.put("curUserInfoVO", curUserInfoVO);
    inMap.put("curTaskId", cur_taskId);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "getButtonDealUserScope");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);

    TransactorInfoVO transactorInfoVO = (TransactorInfoVO)resultMap.get("transactorInfoVO");

    String scopeType = transactorInfoVO.getType();
    String scopeIds = transactorInfoVO.getScopeIds();
    String scopeName = transactorInfoVO.getScopeNames();

    logger.debug("scopeType：" + scopeType);
    logger.debug("scopeIds：" + scopeIds);
    logger.debug("scopeName：" + scopeName);

    Map buttonMap = new HashMap();

    String whir_processCanReadUsers = "";
    String whir_processCanReadOrgs = "";
    String whir_processCanReadGroups = "";

    String rangScope = scopeIds;

    if ((EzFlowUtil.judgeNull(scopeIds)) && (!scopeIds.equals("*0*"))) {
      for (int ii = 0; ii < scopeIds.length(); ii++) {
        char flagCode = scopeIds.charAt(ii);
        int nextPos = scopeIds.indexOf(flagCode, ii + 1);
        String str = scopeIds.substring(ii, nextPos + 1);
        if (flagCode == '$')
          whir_processCanReadUsers = whir_processCanReadUsers + str;
        else if (flagCode == '*')
          whir_processCanReadOrgs = whir_processCanReadOrgs + str;
        else {
          whir_processCanReadGroups = whir_processCanReadGroups + str;
        }
        ii = nextPos;
      }

      EzFlowTransactorService tranService = new EzFlowTransactorService();
      rangScope = tranService.dealUserAccountToUserId(whir_processCanReadUsers) + 
        tranService.dealOrgserialtoOrgId(whir_processCanReadOrgs) + 
        tranService.dealGroupCodeTogGroupId(whir_processCanReadGroups);
    }

    logger.debug("scopeId2：" + rangScope);
    logger.debug("scopeType2：" + scopeType);
    logger.debug("scopeName2：" + scopeName);

    buttonMap.put("scopeId", rangScope);
    buttonMap.put("scopeType", scopeType);
    buttonMap.put("scopeName", scopeName);

    return buttonMap;
  }

  public ProcessDefinitionImpl getProcessDefinitionInfoById(String processDefinitionId)
  {
    Map resultMap = new HashMap();
    Map inMap = new HashMap();
    inMap.put("processDefinitionId", processDefinitionId);
    inMap.put("className", "ProcessDefinitionService");
    inMap.put("method_code", "findProcessDefinitionInfoById");
    RuntimeService runtimeService = this.processEngine.getRuntimeService();
    resultMap = runtimeService.Whir_AllService(inMap);
    ProcessDefinitionImpl processDefinition = (ProcessDefinitionImpl)resultMap.get("processDefinition");

    return processDefinition;
  }

  public String addOtherRealSubProcessXml(String processDefId, List<Map> actList)
  {
    Map map1 = getProcessXmlByProcessDefinitionId(processDefId);
    String y_sbuProcessXml = (String)map1.get("processXml");
    int offset_x = 500;
    int offset_y = 0;
    int add_offset_y = 80;
    int add_offset_x = 25;

    boolean result = true;
    Document doc = null;
    try {
      doc = new SAXBuilder().build(new StringReader(y_sbuProcessXml));
    } catch (Exception e) {
      result = false;
    }

    Element defineElement = doc.getRootElement();
    Element processElement = defineElement.getChild("process", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    String formKey = processElement.getAttributeValue("formKey", 
      Namespace.getNamespace("whir", "http://whir.com/ezFlow"));

    String formType = processElement.getAttributeValue("formType", 
      Namespace.getNamespace("whir", "http://whir.com/ezFlow"));

    Element BPMNDiagramElement = defineElement.getChild("BPMNDiagram", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));
    Element BPMNPlaneElement = BPMNDiagramElement.getChild("BPMNPlane", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/DI"));

    String taskElementStr = "<userTask whir:taskSequenceType=\"monopolise\" activiti:priority=\"10\" whir:formKey=\"222_2\" whir:formType=\"0\" whir:overdueType=\"0\" whir:nodeCommentField=\"nullCommentField\" whir:taskNeedRead=\"0\" whir:nodeNeedAgent=\"true\" whir:commentRangeEmpId=\"\" whir:commentRangeEmpName=\"\" whir:passNodeCommentField=\"nullCommentField\" whir:nodeWriteField=\"\" whir:nodeHiddenField=\"\" whir:protectedField=\"\" whir:commentAttitudeTypeSet=\"0\" id=\"usertask1\" name=\"用户任务1\"><documentation></documentation><extensionElements><whir:whirextension name=\"taskParticipantType\"><whir:field name=\"code\"><whir:value>someUsers</whir:value></whir:field><whir:field name=\"candidateId\"><whir:value>$xiehd_2$</whir:value></whir:field><whir:field name=\"candidate\"><whir:value>谢怀栋,</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskDealWithClass\"><whir:field name=\"updateData\"><whir:value>update</whir:value></whir:field><whir:field name=\"updateStatus\"><whir:value>update_norequest</whir:value></whir:field><whir:field name=\"backData\"><whir:value>back</whir:value></whir:field><whir:field name=\"backStauts\"><whir:value>back_norequest</whir:value></whir:field><whir:field name=\"forminitJsFunName\"><whir:value></whir:value></whir:field><whir:field name=\"formsaveJsFunName\"><whir:value></whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskReadParticipantType\"/><whir:whirextension name=\"activityTip\"><whir:field name=\"enable\"><whir:value>0</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskButtons\"><whir:field name=\"id\"><whir:value>EzFlowBackTask</whir:value></whir:field><whir:field name=\"name\"><whir:value>退回</whir:value></whir:field><whir:field name=\"range\"><whir:value>0</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskButtons\"><whir:field name=\"id\"><whir:value>EzFlowAddSignTask</whir:value></whir:field><whir:field name=\"name\"><whir:value>加签</whir:value></whir:field><whir:field name=\"range\"><whir:value>3</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskButtons\"><whir:field name=\"id\"><whir:value>EzFlowRelationProcess</whir:value></whir:field><whir:field name=\"name\"><whir:value>关联流程</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskButtons\"><whir:field name=\"id\"><whir:value>EzFlowReCall</whir:value></whir:field><whir:field name=\"name\"><whir:value>撤办</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskButtons\"><whir:field name=\"id\"><whir:value>EzFlowNewProcess</whir:value></whir:field><whir:field name=\"name\"><whir:value>新建流程</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskButtons\"><whir:field name=\"id\"><whir:value>EzFlowPress</whir:value></whir:field><whir:field name=\"name\"><whir:value>催办</whir:value></whir:field></whir:whirextension><whir:whirextension name=\"taskButtons\"><whir:field name=\"id\"><whir:value>EzFlowTranWithMail</whir:value></whir:field><whir:field name=\"name\"><whir:value>邮件转发</whir:value></whir:field></whir:whirextension></extensionElements></userTask>";
    String sequenceFlowStr = "<sequenceFlow id=\"sequenceflow1\" name=\"\" sourceRef=\"startevent1\" targetRef=\"usertask1\" whir:tempsetforbidmodify=\"0\"></sequenceFlow>";

    Element sequenceFlowElement = null;

    Element sequenceFlow1 = new Element("sequenceFlow", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    processElement.addContent(sequenceFlow1);
    sequenceFlow1.setAttribute("id", "sequenceflow1");
    sequenceFlow1.setAttribute("name", "");
    sequenceFlow1.setAttribute("sourceRef", "startevent1");
    sequenceFlow1.setAttribute("targetRef", "usertask1");

    for (int i = 0; i < actList.size(); i++) {
      Map varMap = (Map)actList.get(i);
      Element taskElement = creatTask(formKey, formType, varMap, i + 1);
      processElement.addContent(taskElement);

      Element sequenceFlow = new Element("sequenceFlow", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
      processElement.addContent(sequenceFlow);
      sequenceFlow.setAttribute("id", "sequenceflow" + (i + 2));
      sequenceFlow.setAttribute("name", "");
      sequenceFlow.setAttribute("sourceRef", "usertask" + (i + 1));
      if (i == actList.size() - 1)
        sequenceFlow.setAttribute("targetRef", "endevent1");
      else {
        sequenceFlow.setAttribute("targetRef", "usertask" + (i + 2));
      }

      Element BPMNShape_usertask1 = new Element("BPMNShape", Namespace.getNamespace("bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI"));
      BPMNShape_usertask1.setAttribute("bpmnElement", "usertask" + (i + 1));
      BPMNShape_usertask1.setAttribute("id", "BPMNShape_usertask" + (i + 1));
      BPMNPlaneElement.addContent(BPMNShape_usertask1);
      Element Bounds = new Element("Bounds", Namespace.getNamespace("omgdc", "http://www.omg.org/spec/DD/20100524/DC"));
      BPMNShape_usertask1.addContent(Bounds);
      int x_int = 70 + add_offset_x * (i + 1);
      int y_int = 20 + add_offset_y * (i + 1);
      Bounds.setAttribute("x", x_int+"");
      Bounds.setAttribute("y", y_int+"");
      Bounds.setAttribute("width", "120");
      Bounds.setAttribute("height", "35");
    }

    Element startElement = processElement
      .getChild(
      "startEvent", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));

    Element endElement = processElement
      .getChild(
      "endEvent", 
      Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
    String fileContent = toxml(doc);

    return fileContent;
  }

  public Element creatTask(String formKey, String formType, Map varMap, int index) {
    String userName = varMap.get("userTaskname") == null ? "用户任务" + index : varMap.get("userTaskname").toString();

    String taskSequenceType = varMap.get("taskSequenceType") == null ? "parataxis" : varMap.get("taskSequenceType").toString();
    String priority = varMap.get("priority") == null ? "10" : varMap.get("priority").toString();
    String participantType = varMap.get("participantType") == null ? "" : varMap.get("participantType").toString();
    String candidateId = varMap.get("passRound_candidateId") == null ? "" : varMap.get("passRound_candidateId").toString();
    String candidate = varMap.get("passRound_candidate") == null ? "" : varMap.get("passRound_candidate").toString();

    String nodeWriteField = "";
    Element userTask = new Element("userTask", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    userTask.setAttribute("taskSequenceType", taskSequenceType, Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("priority", priority, Namespace.getNamespace("activiti", "http://activiti.org/bpmn"));
    userTask.setAttribute("formKey", formKey, Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("formType", formType, Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("overdueType", "0", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("nodeCommentField", "autoCommentField", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("taskNeedRead", "0", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("commentRangeEmpId", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("commentRangeEmpName", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("passNodeCommentField", "nullCommentField", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("nodeWriteField", nodeWriteField, Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("nodeHiddenField", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("protectedField", "", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
    userTask.setAttribute("id", "usertask" + index);
    userTask.setAttribute("name", userName);

    Element documentation = new Element("documentation", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    userTask.addContent(documentation);

    Element uextensionElements = new Element("extensionElements", Namespace.getNamespace("", "http://www.omg.org/spec/BPMN/20100524/MODEL"));
    userTask.addContent(uextensionElements);

    Element whirextension_taskParticipantType = addwhirextension("taskParticipantType");
    addwhirextension_field(whirextension_taskParticipantType, "code", participantType);

    if (participantType.equals("someUsers")) {
      addwhirextension_field(whirextension_taskParticipantType, "candidateId", candidateId);
      addwhirextension_field(whirextension_taskParticipantType, "candidate", candidate);
    }
    if (participantType.equals("prevTransactorLeader")) {
      addwhirextension_field(whirextension_taskParticipantType, "dutyLevelOperateAnd", "");
      addwhirextension_field(whirextension_taskParticipantType, "dutyLevelOperate", "");
      addwhirextension_field(whirextension_taskParticipantType, "dutyLevel", "-1");
    }
    if (participantType.equals("initiator")) {
      addwhirextension_field(whirextension_taskParticipantType, "type", "Initiator");
    }
    uextensionElements.addContent(whirextension_taskParticipantType);

    Element whirextension_taskDealWithClass = addwhirextension("taskDealWithClass");
    addwhirextension_field(whirextension_taskDealWithClass, "updateData", "update");
    addwhirextension_field(whirextension_taskDealWithClass, "updateStatus", "update_norequest");
    addwhirextension_field(whirextension_taskDealWithClass, "backData", "back");
    addwhirextension_field(whirextension_taskDealWithClass, "backStauts", "back_norequest");
    addwhirextension_field(whirextension_taskDealWithClass, "forminitJsFunName", "");
    addwhirextension_field(whirextension_taskDealWithClass, "formsaveJsFunName", "");
    uextensionElements.addContent(whirextension_taskDealWithClass);

    Element whirextension_activityTip = addwhirextension("activityTip");
    addwhirextension_field(whirextension_activityTip, "enable", "0");
    uextensionElements.addContent(whirextension_activityTip);

    Element whirextension_taskReadParticipantType = addwhirextension("taskReadParticipantType");
    uextensionElements.addContent(whirextension_taskReadParticipantType);

    addButton(uextensionElements, "EzFlowBackTask", "退回", "0");
    addButton(uextensionElements, "EzFlowAddSignTask", "加签", "3");

    addButton(uextensionElements, "EzFlowRelationProcess", "关联流程");
    addButton(uextensionElements, "EzFlowFeedback", "反馈");
    addButton(uextensionElements, "EzFlowCancel", "取消");
    addButton(uextensionElements, "EzFlowNewProcess", "新建流程");
    addButton(uextensionElements, "EzFlowPress", "催办");
    addButton(uextensionElements, "EzFlowTranWithMail", "邮件转发");
    addButton(uextensionElements, "EzFlowPrint", "打印");

    return userTask;
  }

  private void addButton(Element uextensionElements, String id, String name, String range) {
    Element whirextension_taskButtons = addwhirextension("taskButtons");
    addwhirextension_field(whirextension_taskButtons, "id", id);
    addwhirextension_field(whirextension_taskButtons, "name", name);
    addwhirextension_field(whirextension_taskButtons, "range", range);
    uextensionElements.addContent(whirextension_taskButtons);
  }
}