package com.whir.ezoffice.customize.customercontent.actionsupport;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.DataSourceBase;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
public class CustormerContentAction extends BaseActionSupport{
	
	private static Logger logger = Logger.getLogger(CustormerContentAction.class.getName());
	private static final long serialVersionUID = 562835666947159208L;
	
	private String sname;
	private String mobilePhone;
	private String orgName;
	/*
	 * 人员信息查询
	 */
	public String personalInforList()
	  {
	    return "personalInforList";
	  }
	/*
	 * 薪资查询
	 */
	public String salaryQueryList()
	  {
	    return "salaryQueryList";
	  }
	/*
	 * 培训年度计划
	 */
	public String trainYearPlanList()
	  {
	    return "trainYearPlanList";
	  }
	/*
	 * 协会管理
	 */
	public String associationManageList()
	  {
	    return "associationManageList";
	  }
	
	/**
	 * 查找培训年度计划数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String trainYearPlanListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("px_course_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
		String trainName = this.request.getParameter("trainName");
		String address = this.request.getParameter("address");
		String reportDayStart = this.request.getParameter("reportDayStart");
		String reportDayEnd = this.request.getParameter("reportDayEnd");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("coursedetail");
		Element parameter = input.addElement("parameter");
		Element trainNameElement = parameter.addElement("trainName");
		if(trainName==null || trainName.equals("")){
			trainNameElement.setText("");
		}else{
			trainNameElement.setText(trainName);
		}
		Element addressElement = parameter.addElement("address");
		if(address==null || address.equals("")){
			addressElement.setText("");
		}else{
			addressElement.setText(address);
		}
		Element reportDayStartElement = parameter.addElement("reportDayStart");
		if(reportDayStart==null || reportDayStart.equals("")){
			reportDayStartElement.setText("");
		}else{
			reportDayStartElement.setText(reportDayStart);
		}
		Element reportDayEndElement = parameter.addElement("reportDayEnd");
		if(reportDayEnd==null || reportDayEnd.equals("")){
			reportDayEndElement.setText("");
		}else{
			reportDayEndElement.setText(reportDayEnd);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		System.out.println("--------input---------"+rec.asXML());
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      System.out.println("--------input1---------"+send);
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] { "trainName", "major",
									"address", "reportDay", "trackDay",
									"studentNumber", "totalPeriod", "stateIdentify" }, list);
					int recordCount = Integer.parseInt(listStr);
					int pageCount = (recordCount / pageSize);
				      int mod = recordCount % pageSize;
				      if (mod != 0)
				    	 pageCount += 1;
					json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
							+ "},data:" + json + "}";
					printResult(this.G_SUCCESS, json);
		       }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	/**
	 * 查找人员信息数据
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String personalInforListData() throws ParseException{
		
		logger.debug("查询列表开始");

	    Long domainId = CommonUtils.getSessionDomainId(this.request);

	    int pageSize = 
	      CommonUtils.getUserPageSize(this.request);

	    int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }

	    String viewSQL = "organization.orgName,user.empDuty,user.empLeaderName,user.empId,user.empName,user.empSex,user.canSendMail,user.empMobilePhone,organization.orgLevel,user.userAccounts,user.empBusinessPhone";
	    String fromSQL = "com.whir.org.vo.usermanager.UserPO as user join user.organizations organization";
	    String whereSQL = "where user.domainId=" + domainId;

	    whereSQL = whereSQL + " and user.userIsActive=1 and user.userIsDeleted=0 and user.userIsSleep=0 and user.userAccounts is not null and user.userIsFormalUser=1 ";
	    Map varMap = new HashMap();
	    if (!CommonUtils.isEmpty(this.orgName)) {
    		whereSQL = whereSQL + " and organization.orgName = '"+this.orgName+"'";
	    }
	    if (!CommonUtils.isEmpty(this.sname)) {
    		whereSQL = whereSQL + " and user.empName like '%"+this.sname+"%'";
	    }
	    if (!CommonUtils.isEmpty(this.mobilePhone)) {
    		whereSQL = whereSQL + " and user.empMobilePhone like '%"+this.mobilePhone+"%'";
	    }
	    
	    logger.debug("WHERE语句：" + whereSQL);

	    String orderBy = " order by ";
	    orderBy = orderBy + "organization.orgIdString,user.empDutyLevel,user.userOrderCode,user.empName,user.empId";

	    Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, 
	      orderBy);
	    page.setPageSize(pageSize);
	    page.setCurrentPage(currentPage);

	    page.setVarMap(varMap);

	    List list = page.getResultList();

	    int pageCount = page.getPageCount();

	    int recordCount = page.getRecordCount();

	    String[] arr = { "orgNameString", "empDuty",  "empLeaderName", "empId","empName", "empSex", 
	    		"canSendMail",  "empMobilePhone","orgLevel","userAccounts","empBusinessPhone"};
	    JacksonUtil util = new JacksonUtil();
	    String json = util.writeArrayJSON(arr, list);
	    json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount + 
	      "},data:" + json + "}";
	    printResult("success", json);

	    logger.debug("查询列表结束");

	    return null;
	}
	
	/**
	 * 查找人员信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String secndPersonalInforListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("hr_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
		String empIdCard = this.request.getParameter("empIdCard");
		String userName = this.request.getParameter("userName");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("1qazse4!@#$");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element empIdCardElement = parameter.addElement("empIdCard");
		if(empIdCard==null || empIdCard.equals("")){
			empIdCardElement.setText("");
		}else{
			empIdCardElement.setText(empIdCard);
		}
		Element interfaceTypeElement = parameter.addElement("interfaceType");
		interfaceTypeElement.setText("staffInfor");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		String res = null;
		try {
			  Service service = new Service(); 
			  Call call = (Call) service.createCall();  
			  call.setTargetEndpointAddress(wsdl_address);  
			  call.setOperationName("getdata1");// WSDL里面描述的接口名称  
			  call.setSOAPActionURI("urn:getdata1");  
			  call.addParameter("string", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数  
			  call.setReturnType(XMLType.XSD_STRING);// 设置返回类型  
			  String string = rec.asXML();
			  res = (String) call.invoke(new Object[] { string });
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 展示字段：姓名、性别、政治面貌、专业技术职务、手机、电子邮件、学位、办公电话、学历、出生日期、参加工作日期、入党(团)日期、部门、岗位、职务、职级
					String json = util.writeNewArrayJSON(
							new String[] {"birthdate", "email", "edu",
									"titletechpost", "polity", "mobile", 
									"name", "officephone", "pk_degree", "sex",
									"joinworkdate","joinpolitydate","pk_dept","pk_post","pk_job","pk_jobgrade" }, list);
					int recordCount = Integer.parseInt(listStr);
					int pageCount = (recordCount / pageSize);
				      int mod = recordCount % pageSize;
				      if (mod != 0)
				    	 pageCount += 1;
					json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
							+ "},data:" + json + "}";
					printResult(this.G_SUCCESS, json);
		       }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	
	/**
	 * 查找薪资信息
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 */
	public Map<String,Object> getSalaryQueryData(String queryDate,String userId) throws SQLException{
		String empIdCard = "";
		StringBuffer sql = new StringBuffer();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try {
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        sql.append("SELECT EMPIDCARD FROM ORG_EMPLOYEE WHERE EMP_ID=?");
	        pstmt = conn.prepareStatement(sql.toString());
	        pstmt.setString(1, userId);
	        rs = pstmt.executeQuery();
	        while(rs.next()){
		    	empIdCard = rs.getString("EMPIDCARD");
		    }
	    }catch (Exception e) {
		      conn.rollback();
		      System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	}
	    	}
	    	if(pstmt != null){
	    	try
	    	{
	    	pstmt.close();
	    	}catch(Exception e)
	    	{
	    	}
	    	}
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("hr_wsdl");
		String keyString = "f_1,f_2,f_3,f_4,f_5,f_6,f_7,f_8,f_9,f_10,f_11,f_12,f_13,f_14,f_15,f_16,f_17,f_18,f_19,f_20,f_21,f_22," +
				"f_23,f_24,f_25,f_26,f_27,f_28,f_29,f_30," +
				"f_31,f_32,f_33,f_34,f_35,f_36,f_37,f_38,f_39,f_40,f_41,f_42,f_43,f_44,f_45,f_46,f_47,f_48,f_49,f_50,f_51,f_52," +
				"f_53,f_54,f_55,f_56,f_57,f_58,f_59,f_60," +
				"f_61,f_62,f_63,f_64,f_65,f_66,f_67,f_68,f_69,f_70,f_71,f_72,f_73,f_74,f_75,f_76,f_77,f_78,f_79,f_80," +
				"f_81,f_82,f_83,f_84,f_85,f_86,f_87,f_88,f_89,f_90,f_91,f_92,f_93,f_94,f_95,f_96,f_97,f_98,f_99,f_100," +
				"f_101,f_102,f_103,f_104,f_105,f_106,f_107,f_108,f_109,f_110,f_111,f_112,f_113,f_114,f_115,f_116,f_117,f_118,f_119,f_120," +
				"f_121,f_122,f_123,f_124,f_125,f_126,f_127,f_128,f_129,f_130,f_131,f_132,f_133,f_134,f_135" +
				",c_1,c_2,c_3,c_4,c_5,c_6,c_7";
		String[] keys = keyString.split(","); 
		Map<String,Object> map1 = new HashMap<String,Object>();
		String res = null;
		try {
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
			key.addText("1qazse4!@#$");
			Element parameter = input.addElement("parameter");
			Element empIdCardElement = parameter.addElement("empIdCard");
			if(empIdCard==null || empIdCard.equals("")){
				empIdCardElement.setText("");
			}else{
				empIdCardElement.setText(empIdCard);
			}
			Element queryDateElement = parameter.addElement("queryDate");
			if(queryDate==null || queryDate.equals("")){
				queryDateElement.setText("");
			}else{
				queryDateElement.setText(queryDate);
			}
			Element interfaceTypeElement = parameter.addElement("interfaceType");
			interfaceTypeElement.setText("querySalary");
			
			  Service service = new Service(); 
			  Call call = (Call) service.createCall();  
			  call.setTargetEndpointAddress(wsdl_address);  
			  call.setOperationName("getdata1");// WSDL里面描述的接口名称  
			  call.setSOAPActionURI("urn:getdata1");  
			  call.addParameter("string", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数  
			  call.setReturnType(XMLType.XSD_STRING);// 设置返回类型  
			  String string = rec.asXML();
	          res = (String) call.invoke(new Object[] { string });
		      if(null != res && !"".equals(res)){ 
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<Map<String,Object>> list = xmlDataParse.parseXmlDataToList(res);
			      for(Map<String,Object> map:list){
			    	  for(String str:keys){
			    		  map1.put(str, map.get(str));
			    	  }
			      }
		      } else {
		    	  System.out.println("--------获取薪资信息数据---------"+0);
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    map1.put("keys",keys);
		    return map1;
		}
	
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
}
