package com.whir.service.parse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.portal.backlog.actionsupport.CapitalConstructionAction;
import com.whir.portal.backlog.actionsupport.ContractManageAction;
import com.whir.portal.backlog.actionsupport.OnlineReimburseAction;
import com.whir.portal.backlog.actionsupport.OperationSystemAction;
import com.whir.portal.backlog.actionsupport.SecureManageAction;
import com.whir.portal.backlog.actionsupport.TrainManageAction;
import com.whir.portal.basedata.bd.BackLogBD;
import com.whir.service.common.AbstractParse;

public class BackLogParse extends AbstractParse{
	public BackLogParse(Document doc){
	    super(doc);
	}
	public String getBacklogDocData() throws SQLException, NumberFormatException, HibernateException
	  {
		System.out.println("获取getBacklogDocData::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element curPageElement = rootElement.getChild("curPage");
        String curPage = curPageElement.getValue();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        System.out.println("userId:::"+userId);
        System.out.println("curPage:::"+curPage);
	    BackLogBD backLogBD = new BackLogBD();
	    List<Map<String,Object>> list = backLogBD.backlogDocListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("待办公文数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
            	Map<String,Object> map = list.get(i);
                result += "<result>";
                result += "<title><![CDATA[" + map.get("title") +"]]></title>";
                result += "<draftDepartment><![CDATA[" + map.get("draftDepartment") + "]]></draftDepartment>";
                result += "<documentType><![CDATA[" + map.get("documentType") +"]]></documentType>";
                result += "<currentNode><![CDATA[" + map.get("currentNode") +"]]></currentNode>";
                result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
                result += "</result>";
            }
        }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getBacklogDocRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getBacklogDocRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
	    BackLogBD backLogBD = new BackLogBD();
	    int recordCount = backLogBD.backlogDocPageCount(userId);
	    
	    result += "<result>";
        result += "<recordCount1><![CDATA[" + recordCount +"]]></recordCount1>";
        result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getAdminAffairData() throws NumberFormatException, Exception
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element curPageElement = rootElement.getChild("curPage");
        String curPage = curPageElement.getValue();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
	    BackLogBD backLogBD = new BackLogBD();
	    List<Map<String,Object>> list = backLogBD.adminAffairListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("行政事务数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
            	Map<String,Object> map = list.get(i);
                result += "<result>";
                result += "<title><![CDATA[" + map.get("title") +"]]></title>";
                result += "<draftDepartment><![CDATA[" + map.get("draftDepartment") + "]]></draftDepartment>";
                result += "<affairType><![CDATA[" + map.get("affairType") +"]]></affairType>";
                result += "<currentNode><![CDATA[" + map.get("currentNode") +"]]></currentNode>";
                result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
                result += "</result>";
            }
        }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getAdminAffairRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getAdminAffairRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
    	Element userIdElement = rootElement.getChild("userId");
    	String userId = userIdElement.getValue();
    	BackLogBD backLogBD = new BackLogBD();
    	int recordCount = backLogBD.adminAffairPageCount(userId);
	    
	    result += "<result>";
	    result += "<recordCount3><![CDATA[" + recordCount +"]]></recordCount3>";
	    result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	public String getEntrustData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element curPageElement = rootElement.getChild("curPage");
        String curPage = curPageElement.getValue();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        
	    BackLogBD backLogBD = new BackLogBD();
	    List<Map<String,Object>> list = backLogBD.entrustListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("委托事项数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
            	Map<String,Object> map = list.get(i);
                result += "<result>";
                result += "<title><![CDATA[" + map.get("title") +"]]></title>";
                result += "<draftDepartment><![CDATA[" + map.get("draftDepartment") + "]]></draftDepartment>";
                result += "<documentType><![CDATA[" + map.get("documentType") +"]]></documentType>";
                result += "<currentNode><![CDATA[" + map.get("currentNode") +"]]></currentNode>";
                result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
                result += "<byBackLog><![CDATA[" + map.get("byBackLog") +"]]></byBackLog>";
                result += "</result>";
            }
        }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getEntrustRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getEntrustRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
    Element userIdElement = rootElement.getChild("userId");
    String userId = userIdElement.getValue();
    BackLogBD backLogBD = new BackLogBD();
    int recordCount = backLogBD.entrustPageCount(userId);
	    
	    result += "<result>";
    result += "<recordCount4><![CDATA[" + recordCount +"]]></recordCount4>";
    result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	public String getReadDocData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element curPageElement = rootElement.getChild("curPage");
        String curPage = curPageElement.getValue();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        
	    BackLogBD backLogBD = new BackLogBD();
	    List<Map<String,Object>> list = backLogBD.readDocListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("待阅公文数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
            	Map<String,Object> map = list.get(i);
                result += "<result>";
                result += "<title><![CDATA[" + map.get("title") +"]]></title>";
                result += "<fileType><![CDATA[" + map.get("fileType") + "]]></fileType>";
                result += "<source><![CDATA[" + map.get("source") +"]]></source>";
                result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
                result += "</result>";
            }
        }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getReadDocRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getReadDocRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      BackLogBD backLogBD = new BackLogBD();
      int recordCount = backLogBD.readDocPageCount(userId);
	    
	    result += "<result>";
      result += "<recordCount2><![CDATA[" + recordCount +"]]></recordCount2>";
      result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	
	/**
	 * 获取网报待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getWbBackLogData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element curPageElement = rootElement.getChild("curPage");
      String curPage = curPageElement.getValue();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      
      OnlineReimburseAction onlineReimburseAction = new OnlineReimburseAction();
	    List<Map<String,Object>> list = onlineReimburseAction.onlineReimburseListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("网报待办数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<title><![CDATA[" + map.get("title") +"]]></title>";
              result += "<dmessage><![CDATA[" + map.get("dmessage") + "]]></dmessage>";
              result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getWbBackLogRecordCount() throws SQLException
	  {
		System.out.println("获取getWbBackLogRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        OnlineReimburseAction onlineReimburseAction = new OnlineReimburseAction();
        int recordCount = onlineReimburseAction.onlineReimbursePageCount(userId);
	    
	    result += "<result>";
        result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
        result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	/**
	 * 获取合同待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getHtBackLogData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element curPageElement = rootElement.getChild("curPage");
      String curPage = curPageElement.getValue();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      
      ContractManageAction contractManageAction = new ContractManageAction();
	    List<Map<String,Object>> list = contractManageAction.contractManageListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("合同待办数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<title><![CDATA[" + map.get("title") +"]]></title>";
              result += "<draftDepartment><![CDATA[" + map.get("draftDepartment") + "]]></draftDepartment>";
              result += "<fileType><![CDATA[" + map.get("fileType") + "]]></fileType>";
              result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getHtBackLogRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getHtBackLogRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        ContractManageAction contractManageAction = new ContractManageAction();
        int recordCount = contractManageAction.contractManagePageCount(userId);
	    
	    result += "<result>";
        result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
        result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	/**
	 * 获取固定资产申请待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getZcDisPosalData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element curPageElement = rootElement.getChild("curPage");
      String curPage = curPageElement.getValue();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      
      BackLogBD backLogBD = new BackLogBD();
	    List<Map<String,Object>> list = backLogBD.disposeListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("固定资产申请数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<receiptNum><![CDATA[" + map.get("receiptNum") +"]]></receiptNum>";
              result += "<receiptDate><![CDATA[" + map.get("receiptDate") + "]]></receiptDate>";
              result += "<agent><![CDATA[" + map.get("agent") + "]]></agent>";
              result += "<agentPhone><![CDATA[" + map.get("agentPhone") +"]]></agentPhone>";
              result += "<dealShape><![CDATA[" + map.get("dealShape") +"]]></dealShape>";
              result += "<dealDepartment><![CDATA[" + map.get("dealDepartment") +"]]></dealDepartment>";
              result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	public String getZcDisPosalRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getZcDisPosalRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element userIdElement = rootElement.getChild("userId");
	    String userId = userIdElement.getValue();
	    BackLogBD backLogBD = new BackLogBD();
	    int recordCount = backLogBD.disPosalPageCount(userId);
	    
	    result += "<result>";
	    result += "<recordCount1><![CDATA[" + recordCount +"]]></recordCount1>";
	    result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	/**
	 * 获取固定资产申购待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getZcPurchaseData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element curPageElement = rootElement.getChild("curPage");
      String curPage = curPageElement.getValue();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      
      BackLogBD backLogBD = new BackLogBD();
	    List<Map<String,Object>> list = backLogBD.purchaseListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("固定资产申购数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<receiptNum><![CDATA[" + map.get("receiptNum") +"]]></receiptNum>";
              result += "<receiptDate><![CDATA[" + map.get("receiptDate") + "]]></receiptDate>";
              result += "<agent><![CDATA[" + map.get("agent") + "]]></agent>";
              result += "<applicatDepartment><![CDATA[" + map.get("applicatDepartment") +"]]></applicatDepartment>";
              result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	public String getZcPurchaseRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getZcPurchaseRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element userIdElement = rootElement.getChild("userId");
	    String userId = userIdElement.getValue();
	    BackLogBD backLogBD = new BackLogBD();
	    int recordCount = backLogBD.purchasePageCount(userId);
	    
	    result += "<result>";
	    result += "<recordCount2><![CDATA[" + recordCount +"]]></recordCount2>";
	    result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	/**
	 * 获取固定资产变动待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getZcAlterData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element curPageElement = rootElement.getChild("curPage");
      String curPage = curPageElement.getValue();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      
      BackLogBD backLogBD = new BackLogBD();
	    List<Map<String,Object>> list = backLogBD.alterListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("固定资产申请数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<receiptNum><![CDATA[" + map.get("receiptNum") +"]]></receiptNum>";
              result += "<receiptDate><![CDATA[" + map.get("receiptDate") + "]]></receiptDate>";
              result += "<agent><![CDATA[" + map.get("agent") + "]]></agent>";
              result += "<applicatDepartment><![CDATA[" + map.get("applicatDepartment") +"]]></applicatDepartment>";
              result += "<dataLink><![CDATA[" + map.get("dataLink") +"]]></dataLink>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getZcAlterRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getZcAlterRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element userIdElement = rootElement.getChild("userId");
	    String userId = userIdElement.getValue();
	    BackLogBD backLogBD = new BackLogBD();
	    int recordCount = backLogBD.changePageCount(userId);
	    
	    result += "<result>";
	    result += "<recordCount3><![CDATA[" + recordCount +"]]></recordCount3>";
	    result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	/**
	 * 获取运维待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getYwBackLogData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element curPageElement = rootElement.getChild("curPage");
      String curPage = curPageElement.getValue();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      
      OperationSystemAction operationSystemAction = new OperationSystemAction();
	    List<Map<String,Object>> list = operationSystemAction.operationSystemListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("运维待办数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<backlogCategory><![CDATA[" + map.get("backlogCategory") +"]]></backlogCategory>";
              result += "<backlogContent><![CDATA[" + map.get("backlogContent") + "]]></backlogContent>";
              result += "<backlogNode><![CDATA[" + map.get("backlogNode") + "]]></backlogNode>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	public String getYwBackLogRecordCount() throws SQLException, HibernateException
	  {
		System.out.println("获取getYwBackLogRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      OperationSystemAction operationSystemAction = new OperationSystemAction();
      int recordCount = operationSystemAction.operationSystemPageCount(userId);
	    
	    result += "<result>";
      result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
      result += "</result>";
	    this.setMessage("1", message);
	    return result;
	 }
	/**
	 * 获取培训待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getPxBackLogData() throws SQLException, NumberFormatException, HibernateException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element curPageElement = rootElement.getChild("curPage");
      String curPage = curPageElement.getValue();
      Element userIdElement = rootElement.getChild("userId");
      String userId = userIdElement.getValue();
      
      TrainManageAction trainManageAction = new TrainManageAction();
	    List<Map<String,Object>> list = trainManageAction.trainManageListData(userId,Integer.parseInt(curPage),15);
	    
	    System.out.println("培训待办数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<trainName><![CDATA[" + map.get("trainName") +"]]></trainName>";
              result += "<auditState><![CDATA[" + map.get("auditState") + "]]></auditState>";
              result += "<trainAddress><![CDATA[" + map.get("trainAddress") + "]]></trainAddress>";
              result += "<dataLink><![CDATA[" + map.get("dataLink") + "]]></dataLink>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getPxBackLogRecordCount() throws SQLException, HibernateException {
		System.out.println("获取getPxBackLogRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        TrainManageAction trainManageAction = new TrainManageAction();
        int recordCount = trainManageAction.trainManagePageCount(userId);
	    
	    result += "<result>";
        result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
        result += "</result>";
	    this.setMessage("1", message);
	    return result;
	}
	/**
	 * 获取安管待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getAgBackLogData() throws SQLException, NumberFormatException, HibernateException
	  {
		  String result = "";
	      String message = "数据传输成功。";
	      Element rootElement = doc.getRootElement();
	      Element curPageElement = rootElement.getChild("curPage");
	      String curPage = curPageElement.getValue();
	      Element userIdElement = rootElement.getChild("userId");
	      String userId = userIdElement.getValue();
	      
	      SecureManageAction secureManageAction = new SecureManageAction();
		  List<Map<String,Object>> list = secureManageAction.secureManageListData(userId,Integer.parseInt(curPage),15);
		    
		  System.out.println("安管待办数据list:::"+list.size());
		  if (list != null && list.size() > 0) {
	          for (int i = 0; i < list.size(); i++) {
	          	Map<String,Object> map = list.get(i);
	              result += "<result>";
	              result += "<title><![CDATA[" + map.get("title") +"]]></title>";
	              result += "<demandFinishDate><![CDATA[" + map.get("demandFinishDate") + "]]></demandFinishDate>";
	              result += "<establishDate><![CDATA[" + map.get("establishDate") + "]]></establishDate>";
	              result += "<backlogURL><![CDATA[" + map.get("backlogURL") + "]]></backlogURL>";
	              result += "<backlogType><![CDATA[" + map.get("backlogType") + "]]></backlogType>";
	              result += "</result>";
	          }
	      }
	      this.setMessage("1", message);
	      return result;
	 }
	
	public String getAgBackLogRecordCount() throws SQLException, HibernateException {
		System.out.println("获取getAgBackLogRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        SecureManageAction secureManageAction = new SecureManageAction();
        int recordCount = secureManageAction.secureManagePageCount(userId);
	    
	    result += "<result>";
        result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
        result += "</result>";
	    this.setMessage("1", message);
	    return result;
	}
	/**
	 * 获取基建待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws HibernateException 
	 * @throws NumberFormatException 
	 */
	public String getJjBackLogData() throws SQLException, NumberFormatException, HibernateException
	  {
		  String result = "";
	      String message = "数据传输成功。";
	      Element rootElement = doc.getRootElement();
	      Element curPageElement = rootElement.getChild("curPage");
	      String curPage = curPageElement.getValue();
	      Element userIdElement = rootElement.getChild("userId");
	      String userId = userIdElement.getValue();
	      
	      CapitalConstructionAction capitalConstructionAction = new CapitalConstructionAction();
		  List<Map<String,Object>> list = capitalConstructionAction.capitalConstructionData(userId,Integer.parseInt(curPage),15);
		    
		  System.out.println("基建待办数据list:::"+list.size());
		  if (list != null && list.size() > 0) {
	          for (int i = 0; i < list.size(); i++) {
	          	Map<String,Object> map = list.get(i);
	              result += "<result>";
	              result += "<filetype><![CDATA[" + map.get("filetype") +"]]></filetype>";
	              result += "<dataLink><![CDATA[" + map.get("dataLink") + "]]></dataLink>";
	              result += "<sum><![CDATA[" + map.get("sum") + "]]></sum>";
	              result += "<dateTime><![CDATA[" + map.get("dateTime") + "]]></dateTime>";
	              result += "<sendPeople><![CDATA[" + map.get("sendPeople") + "]]></sendPeople>";
	              result += "<status><![CDATA[" + map.get("status") + "]]></status>";
	              result += "<colour><![CDATA[" + map.get("colour") + "]]></colour>";
	              result += "</result>";
	          }
	      }
	      this.setMessage("1", message);
	      return result;
	 }
	
	public String getJjBackLogRecordCount() throws SQLException, HibernateException {
		System.out.println("获取getJjBackLogRecordCount::");
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        CapitalConstructionAction capitalConstructionAction = new CapitalConstructionAction();
        int recordCount = capitalConstructionAction.capitalConstructionPageCount(userId);
	    
	    result += "<result>";
        result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
        result += "</result>";
	    this.setMessage("1", message);
	    return result;
	}
}