package com.whir.portal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.nankang.framework.lang.ByteArray;
import com.nankang.framework.util.StringUtil;
import com.nankang.system.interfaces.oi.Organization;
import com.nankang.system.services.synchro.AbstractImportClass;
import com.nankang.system.vo.HumanVO;
import com.nankang.system.vo.OrganizationVO;
import com.nankang.system.vo.SysUserVO;

public class PlatformInToPortal extends AbstractImportClass
{
  private Connection conn = null;

  private String domainId = "1020";

  private String ftppwd = "";

  private String ftpserver = "";
  private String ftpuser = "";
  private static Properties props;
  private ResultSet rs = null;
  private Statement stmt = null;

  private boolean delEZOrgUser(String orgID)
  {
    boolean rs = true;
    try {
      this.stmt.execute("delete from ezoffice.ORG_ORGANIZATION_USER where ORG_ID='" + orgID + "'");
    }
    catch (SQLException e) {
      rs = false;
      e.printStackTrace();
    }
    return rs;
  }

  private void destroyDatabaseConnection()
  {
    try
    {
      this.conn.close();
    }
    catch (Exception ex) {
      if (this.stmt != null) {
        try {
          this.stmt.close();
        }
        catch (SQLException ex1) {
          ex1.printStackTrace();
        }
      }
      if (this.conn != null) {
        try {
          this.conn.close();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      ex.printStackTrace();
    }
  }

  protected int execute()
  {
	System.out.println("------------门户网站组织用户同步------------");
    initDatabaseConnection();
    try
    {
      this.stmt = this.conn.createStatement();
      this.rs = this.stmt.executeQuery("select domain_id from ezoffice.org_domain");
      if (this.rs.next()) {
        this.domainId = this.rs.getString(1);
      }
      this.rs.close();
      if (props == null) {
        props = new Properties();
        try {
          props.load(getClass().getResourceAsStream("kgportal.properties"));
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      this.ftpserver = props.getProperty("ftpserver");
      this.ftpuser = props.getProperty("ftpuser");
      this.ftppwd = props.getProperty("ftppwd");
      
      insertOrganizationInfo(0, "0", "", "");
      System.out.println("------------门户网站组织用户同步结束！------------");
      return AbstractImportClass.BM_SUCCESS;
    }
    catch (Exception e) {
      e.printStackTrace();
      return AbstractImportClass.BM_FAILURE;
    }
    finally
    {
      destroyDatabaseConnection();
    }
  }

  private void initDatabaseConnection()
  {
    String dbDriver = this.interfaceConfigVO.getDBDriver();
    String dbURL = this.interfaceConfigVO.getDBURL();
    String dbUser = this.interfaceConfigVO.getDBUserName();
    String dbPassword = this.interfaceConfigVO.getDBPassword();
    System.out.println("-------"+dbPassword);
    System.out.println("-------"+dbUser);
    System.out.println("-------"+dbURL);
    try {
      Class.forName(dbDriver).getInterfaces();
      if ((this.conn == null) || (this.conn.isClosed()))
        this.conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  private void insertHumansOfOrganization(int parentOrgID, String WebOrgID)
  {
    try
    {
      List listHumans = this.importManager.getHumanListOnOrganization(parentOrgID);
      int i = 0;
      do { 
    	HumanVO humanVO = (HumanVO)listHumans.get(i);
        insertUsersOfHuman(humanVO, WebOrgID, i + 1);
        i++; 
        if (listHumans == null) break;  
      } while (i < listHumans.size());
    }
    catch (Exception err)
    {
      err.printStackTrace();
    }
  }

  private void insertOrganizationInfo(int parentPlatformOrgId, String parentOrgId, String parentIdString, String parentNameString)
  {
    String orgName = "";
    String orgSimpleName = "";
    String orgSerial = "";
    int platformId = 0;
    String orgId = "0";
    int orgLevel = 0;
    int orgStatus = 0;
    int orderCode = 500000;
    long modifyTime = 0L;
    StringBuffer sqlBuffer = new StringBuffer();
    try
    {
      List listRootOrgs;
      if ("0".equals(parentOrgId)) {
        listRootOrgs = this.importManager.getRootOrganizationList();
      }
      else {
        listRootOrgs = this.importManager.getSubOrganizationList(parentPlatformOrgId);
        if ((listRootOrgs != null) && (listRootOrgs.size() > 0)) {
          this.stmt.executeUpdate("update ezoffice.org_organization set orgHasJunior=1 where org_id=" + parentOrgId);
        }
      }

      for (int i = 0; i < listRootOrgs.size(); i++) {
        OrganizationVO orgVO = (OrganizationVO)listRootOrgs.get(i);
        orgName = StringUtil.format(orgVO.getName());
        orgSimpleName = orgName;
        orgSerial = StringUtil.format(orgVO.getCode());
        platformId = orgVO.getOrgID();
        orgLevel = orgVO.getTreeID().length() / 4 - 1;
        orgStatus = orgVO.isValid() ? 0 : 1;

        if (this.stmt == null) {
          this.stmt = this.conn.createStatement();
        }
        int num = 0;
        this.rs = this.stmt.executeQuery("select org_id,modifyTime from ezoffice.org_organization where thirdId=" + platformId);
        if (this.rs.next()) {
          num = 1;
          orgId = this.rs.getString(1);
          modifyTime = this.rs.getLong(2);
        }
        this.rs.close();
        if (num == 0) {
          this.rs = this.stmt.executeQuery("select hibernate_sequence.nextval from dual");
          if (this.rs.next()) {
            orgId = this.rs.getString(1);
          }
          this.rs.close();
        }
        String orgIdString;
        String orgNameString;
        if ("0".equals(parentOrgId)) {
          orgIdString = "_" + orderCode + "$" + orgId + "$";
          orgNameString = orgName;
        } else {
          orgIdString = parentIdString + "_" + orderCode + "$" + orgId + "$";
          orgNameString = parentNameString + "." + orgName;
        }
        long platformModifyTime = orgVO.getPlatformModifiedTime().getTime();
        sqlBuffer.delete(0, sqlBuffer.length());

        this.conn.setAutoCommit(true);
        if (num == 0)
        {
          sqlBuffer.append("insert into ezoffice.org_organization (").append("org_id,orgParentOrgId,orgName,orgSimpleName,orgSerial,orgLevel,orgHasJunior,orgIdString,orgNameString,orgStatus,orgHasChannel,thirdId,modifyTime,domain_id, orgordercode) values(").append(orgId).append(",").append(parentOrgId).append(",'").append(orgName).append("','").append(orgSimpleName).append("','").append(orgSerial).append("',").append(orgLevel).append(",0,'").append(orgIdString).append("','").append(orgNameString).append("',").append(orgStatus).append(",0,").append(platformId).append(",").append(platformModifyTime).append(",").append(this.domainId).append(", " + orderCode + ")");

          int icount = this.stmt.executeUpdate(sqlBuffer.toString());
          if (icount > 0)
        	  this.logList.addLog(AbstractImportClass.OPERATION_TYPE_ADD_ONE, AbstractImportClass.BM_SUCCESS, "组织表", "sm_org_t", "OrgID", String.valueOf(platformId), orgName);
          else
        	  this.logList.addLog(AbstractImportClass.OPERATION_TYPE_ADD_ONE, AbstractImportClass.BM_FAILURE, "组织表", "sm_org_t", "OrgID", String.valueOf(platformId), orgName);
        }
        else
        {
          if (platformModifyTime > modifyTime) {
            sqlBuffer.delete(0, sqlBuffer.length());
            sqlBuffer.append("update ezoffice.org_organization set ").append("orgName='").append(orgName).append("',orgSimpleName='").append(orgSimpleName).append("',orgSerial='").append(orgSerial).append("',orgLevel=").append(orgLevel).append(",orgHasJunior=").append(0).append(",orgIdString='").append(orgIdString).append("',orgNameString='").append(orgNameString).append("',orgStatus=").append(orgStatus).append(",orgordercode=").append(orderCode).append(",modifyTime=").append(platformModifyTime).append(" where thirdId=").append(platformId);

            int icount = this.stmt.executeUpdate(sqlBuffer.toString());

            if (icount >= 0)
            	this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_SUCCESS, "组织表", "sm_org_t", "OrgID", String.valueOf(platformId), orgName);
            else {
            	this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_FAILURE, "组织表", "sm_org_t", "OrgID", String.valueOf(platformId), orgName);
            }
          }

          this.stmt.executeUpdate("update ezoffice.org_organization set orgordercode =" + orderCode + ",orgIdString='" + orgIdString + "',orgLevel=" + orgLevel + " where thirdId=" + platformId);
        }
        this.conn.commit();

        orderCode += 10000 + i + 1;

        boolean delOrgUserRslt = delEZOrgUser(orgId);

        insertHumansOfOrganization(orgVO.getOrgID(), orgId);

        if ((orgId != null) && (orgIdString != null))
          insertOrganizationInfo(platformId, orgId, orgIdString, orgNameString);
      }
    }
    catch (Exception err)
    {
    	this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_FAILURE, "组织表", "sm_org_t", "OrgID", "", orgName);
      err.printStackTrace();
    }
  }

  private void insertUsersOfHuman(HumanVO humanVO, String WebOrgID, int orderID)
  {
    int empPlatformId = 0;

    String empId = "0";

    String empName = humanVO.getName();
    String empSex = humanVO.getSex();

    if (("1".equals(empSex)) || ("女".equals(empSex)) || ((null != empSex) && (empSex.indexOf("女") > -1)))
      empSex = "1";
    else {
      empSex = "0";
    }
    Date empBirth = humanVO.getBirthDate();
    int certType = humanVO.getCertificateType();
    String certCode = humanVO.getCertificateCode();
    String certAddr = humanVO.getCertificateAddr();
    String nation = humanVO.getNationality();
    String province = humanVO.getProvince();
    String address = humanVO.getAddress();
    String zip = humanVO.getZip();
    String mobile = humanVO.getMobile();
    String telephone = humanVO.getTelephone();
    String fax = humanVO.getFax();
    String email = humanVO.getEmail();
    String description = humanVO.getDescription();
    String photoFileName = "";
    StringBuffer buffer = new StringBuffer();
    long modifyTime = 0L;

    if ((telephone == null) || ("null".equals(telephone))) {
      telephone = "";
    }
    if ((mobile == null) || ("null".equals(mobile))) {
      mobile = "";
    }
    if ((nation == null) || ("null".equals(nation))) {
      nation = "";
    }
    if ((province == null) || ("null".equals(province))) {
      province = "";
    }
    
    if ((zip == null) || ("null".equals(zip))) {
      zip = "";
    }
    if ((address == null) || ("null".equals(address))) {
      address = "";
    }

    if ((description == null) || ("null".equals(description))) {
      description = "";
    }

    if ((email == null) || ("null".equals(email))) {
      email = "";
    }

    if ((fax == null) || ("null".equals(fax))) {
      fax = "";
    }

    if ((telephone == null) || ("null".equals(telephone))) {
      telephone = "";
    }

    int userIsActive = 0;
    try
    {
      List relationOrgId = new ArrayList();
      String sidelineorg = "";
      String sidelineorgname = "";
      int orgthirdId = 0;
      if (humanVO != null) {
        List relationOrg = this.importManager.getOrganizationListOnHuman(humanVO.getHumanID());
        Organization defaultOrganization = this.importManager.getDefaultOrganization(humanVO.getHumanID());
        if ((relationOrg != null) && (defaultOrganization != null)) {
          if (relationOrg.size() > 1) {
            for (int k = 0; k < relationOrg.size(); k++) {
              OrganizationVO organizationVO = (OrganizationVO)relationOrg.get(k);
              if (organizationVO.getOrgID() != defaultOrganization.getOrgID()) {
                relationOrgId.add(organizationVO);
              }
            }
          }
          orgthirdId = defaultOrganization.getOrgID();
        }
        if (orgthirdId != 0) {
          this.rs = this.stmt.executeQuery("select org_id from ezoffice.org_organization where thirdId =" + orgthirdId);
          if (this.rs.next()) {
            WebOrgID = this.rs.getString(1);
          }
          this.rs.close();
        }
        String relationOrgIds = "";
        for (int k = 0; (relationOrgId != null) && (k < relationOrgId.size()); k++) {
          OrganizationVO organizationVO = (OrganizationVO)relationOrgId.get(k);
          relationOrgIds = relationOrgIds + organizationVO.getOrgID();
          if (k != relationOrgId.size() - 1) {
            relationOrgIds = relationOrgIds + ",";
          }
        }

        if (!"".equals(relationOrgIds)) {
          this.rs = this.stmt.executeQuery("select org_id,orgname from ezoffice.org_organization where thirdId in(" + relationOrgIds + ")");
          while (this.rs.next()) {
            sidelineorg = sidelineorg + "*" + this.rs.getString(1) + "*";
            sidelineorgname = sidelineorgname + this.rs.getString(2) + ",";
          }
          this.rs.close();
        }
      }
      List listUsers = this.importManager.getUserListOnHuman(humanVO.getHumanID());
      if ((listUsers == null) || (listUsers.size() == 0)) {
        return;
      }

      int i = 0;
      do { 
    	SysUserVO userVO = (SysUserVO)listUsers.get(i);
        String userAccount = userVO.getLoginName();
        userIsActive = (userVO.isValid()) && (humanVO.getValid()) ? 1 : 0;
        String password = userVO.getPassword();
        empName = StringUtil.format(userVO.getUserName());
        System.out.println("----- empName : " + empName);
        int userIsDeleted = (userVO.isValid()) && (humanVO.getValid()) ? 0 : 1;
        int imark = (userVO.getUser_IMark()) && (userVO.getUser_IMark()) ? 0 : 1;
        if (this.stmt == null) {
          this.stmt = this.conn.createStatement();
        }
        if (userIsDeleted == 1) {
          userAccount = "";
        }

        int num = 0;
        empId = "0";
        empPlatformId = userVO.getUserID();

        long platformModifyTime = humanVO.getPlatformModifiedTime().after(userVO.getPlatformModifiedTime()) ? humanVO.getPlatformModifiedTime().getTime() : userVO.getPlatformModifiedTime().getTime();

        this.rs = this.stmt.executeQuery("select emp_id,modifyTime from ezoffice.org_employee where thirdId=" + empPlatformId);
        if (this.rs.next()) {
          num = 1;
          empId = this.rs.getString(1);
          modifyTime = this.rs.getLong(2);
        }
        this.rs.close();
        this.conn.setAutoCommit(true);
        if (num == 0)
        {
          this.rs = this.stmt.executeQuery("select hibernate_sequence.nextval from dual");
          if (this.rs.next()) {
            empId = this.rs.getString(1);
          }
          this.rs.close();
          ByteArray photoByteArr = this.importManager.getPictureOfHuman(String.valueOf(humanVO.getHumanID()));
          System.out.println("人员照片数据==="+photoByteArr);
          if (photoByteArr != null) {
            byte[] photoBytes = photoByteArr.getBytes();
            if (photoBytes != null)
            {
              photoFileName = uploadUserPhoto(photoBytes, userAccount.toLowerCase());
              System.out.println("人员照片名==="+photoFileName);
              photoByteArr = null;
            }
          }

          buffer.delete(0, buffer.length());
          buffer.append("insert into ezoffice.org_employee ");
          buffer.append("(emp_id,empName,empSex,empBirth,empIdCard,empCountry,empState,empAddress,empPhone,empMobilePhone,");
          buffer.append("empZipCode,userIsDeleted,thirdId,modifyTime,domain_id,empIsMarriage,empHeight,empWeight,");
          buffer.append("empStatus,userIsFormalUser,userIsActive,userIsSuper,keyvalidate,userAccounts,userpassword, EMPDESCRIBE, EMPEMAIL, EMPBUSINESSFAX, EMPBUSINESSPHONE,createdorg, usersimplename,EMPDUTYLEVEL, EMPPHOTO,emplivingphoto,imark) values(");
          buffer.append(empId).append(",'").append(empName).append("',").append(empSex).append(",to_date('");

          if (empBirth != null) {
            buffer.append(empBirth.getYear() + 1900).append("-");
            buffer.append(empBirth.getMonth() + 1).append("-");
            buffer.append(empBirth.getDate());
          }
          else {
            buffer.append("1900-01-01");
          }
          buffer.append("','YYYY-MM-DD'),").append("'" + certCode + "','").append(nation).append("','").append(province);

          buffer.append("','").append(address).append("','");
          buffer.append(telephone).append("','").append(mobile);
          buffer.append("','").append(zip).append("',").append(userIsDeleted).append(",").append(empPlatformId).append(",");

          buffer.append(platformModifyTime).append(",").append(this.domainId).append(",0,0,0,0,1," + userIsActive + ",0,0,'" + userAccount.toLowerCase() + "','" + password + "', '" + description + "', '" + email + "', '" + fax + "', '" + telephone + "', 0, '" + userAccount.toLowerCase() + "',1000,'" + photoFileName + "','" + photoFileName + "',"+imark+")");

          int icount = this.stmt.executeUpdate(buffer.toString());

          if (icount >= 0)
            this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_SUCCESS, "用户表", "sm_human_t", "EmpID", String.valueOf(empPlatformId), empName);
          else {
            this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_FAILURE, "用户表", "sm_human_t", "OrgID", String.valueOf(empPlatformId), empName);
          }
        }
        else if (modifyTime < platformModifyTime)
        {
          ByteArray photoByteArr = this.importManager.getPictureOfHuman(String.valueOf(humanVO.getHumanID()));
          System.out.println("人员照片数据==="+photoByteArr);
          if (photoByteArr != null) {
            byte[] photoBytes = photoByteArr.getBytes();
            if (photoBytes != null)
            {
              photoFileName = uploadUserPhoto(photoBytes, userAccount.toLowerCase());
              System.out.println("人员照片名==="+photoFileName);
              photoByteArr = null;
            }
          }

          buffer.delete(0, buffer.length());
          buffer.append("update ezoffice.org_employee set ").append("empName='").append(empName).append("',empSex=").append(empSex).append(",empBirth=to_date('");

          if (empBirth != null) {
            buffer.append(empBirth.getYear() + 1900).append("-").append(empBirth.getMonth() + 1).append("-").append(empBirth.getDate());
          }
          else
          {
            buffer.append("1900-01-01");
          }
          buffer.append("','YYYY-MM-DD')").append(", empCountry='").append(nation).append("',empState='").append(province).append("',empAddress='").append(address).append("',empPhone='").append(telephone).append("',empMobilePhone='").append(mobile).append("',empZipCode='").append(zip).append("',userIsDeleted=").append(userIsDeleted).append(", modifyTime=").append(platformModifyTime).append(", userAccounts='").append(userAccount.toLowerCase()).append("',userpassword='").append(password).append("',userIsActive=" + userIsActive).append(", EMPDESCRIBE='").append(description).append("', EMPEMAIL='").append(email).append("', EMPBUSINESSFAX='").append(fax).append("', EMPBUSINESSPHONE='").append(telephone).append("', empIdCard='").append(certCode).append("', usersimplename='").append(userAccount.toLowerCase()).append("', empphoto='").append(photoFileName).append("', emplivingphoto='").append(photoFileName).append("', imark=").append(imark).append(" where emp_Id=").append(empId);

          int icount = this.stmt.executeUpdate(buffer.toString());

          if (icount >= 0)
        	  this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_SUCCESS, "用户表", "sm_human_t", "EmpID", String.valueOf(empPlatformId), empName);
          else {
        	  this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_FAILURE, "用户表", "sm_human_t", "OrgID", String.valueOf(empPlatformId), empName);
          }
        }

        this.conn.commit();

        if ((!"".equals(sidelineorg)) && (!"".equals(sidelineorgname)))
          this.stmt.executeUpdate("update ezoffice.org_employee set USERORDERCODE =" + orderID + ",SIDELINEORG='" + sidelineorg + "',SIDELINEORGNAME='" + sidelineorgname + "' where emp_Id = " + empId);
        else {
          this.stmt.executeUpdate("update ezoffice.org_employee set USERORDERCODE =" + orderID + " where emp_Id = " + empId);
        }

        this.rs = this.stmt.executeQuery("select count(*) from ezoffice.ORG_ORGANIZATION_USER where emp_Id = " + empId);
        boolean isNew = true;
        if (this.rs.next()) {
          if (this.rs.getInt(1) > 0) {
            isNew = false;
          }
        }
        this.rs.close();
        if (isNew)
          this.stmt.execute("insert into ezoffice.ORG_ORGANIZATION_USER (ORG_ID,EMP_ID) values('" + StringUtil.format(WebOrgID) + "','" + empId + "')");
        else
          this.stmt.execute("UPDATE ezoffice.ORG_ORGANIZATION_USER set ORG_ID ='" + StringUtil.format(WebOrgID) + "' where emp_Id='" + empId + "'");
        i++; 
        if (listUsers == null) break;  
       } 
       while (i < listUsers.size());
    }
    catch (Exception err)
    {
    	this.logList.addLog(AbstractImportClass.OPERATION_TYPE_UPDATE_ONE, AbstractImportClass.BM_FAILURE, "用户表", "sm_human_t", "OrgID", String.valueOf(empPlatformId), empName);
      err.printStackTrace();
      System.err.println("------------------------ 同步人员失败 ------paullmc------------");
    }
  }
  
  private String uploadUserPhoto(byte[] bArr, String userAccount) throws IOException {
	  String fileName = userAccount + ".jpg";
	  FTPClient ftpClient = new FTPClient();
	  try {
			ftpClient.connect(this.ftpserver);
			int reply=ftpClient.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				boolean b_=ftpClient.login(this.ftpuser, this.ftppwd);
				System.out.println("------------图片上传1------------"+b_);
				if(b_){
					ftpClient.setDefaultTimeout(30000);
					ftpClient.setDataTimeout(10000);
					ftpClient.setBufferSize(1024);//设置上传缓存大小
					ftpClient.setControlEncoding("UTF-8");//设置编码
					ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置文件类型
					ftpClient.enterLocalPassiveMode();
					InputStream is = new ByteArrayInputStream(bArr);
					ftpClient.storeFile(fileName, is);
					ftpClient.logout();
					ftpClient.disconnect();
					is.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    return fileName;
  }
}