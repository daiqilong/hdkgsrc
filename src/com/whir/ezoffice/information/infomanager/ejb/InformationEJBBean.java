package com.whir.ezoffice.information.infomanager.ejb;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;
import net.sf.hibernate.*;

import java.util.*;

import com.whir.ezoffice.information.channelmanager.po.InformationChannelPO;
import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.CommonUtils;
import com.whir.ezoffice.information.infomanager.po.*;

import java.sql.Connection;
import java.sql.Statement;
import com.whir.ezoffice.information.isodoc.po.IsoPaperPO;
import com.whir.ezoffice.information.isodoc.po.IsoBorrowUserPO;
import com.whir.ezoffice.information.isodoc.po.IsoCommentPO;
import com.whir.ezoffice.information.isodoc.po.IsoDeallogPO;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import com.whir.ezoffice.information.common.util.SaveInfoReader;



/**
 * <p>Title: InformationEJBBean</p>
 * <p>Description: 处理知识管理</p>
 * <p>Copyright: Copyright (c) 2003</p>transfer
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class InformationEJBBean extends HibernateBase implements SessionBean {
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
     * 取信息PO
     * @param informationId
     * @return
     * @throws Exception 
     */
    public InformationPO load(String informationId) throws Exception{
    	InformationPO po = null;
    	try {
    		begin();
    		po = (InformationPO) session.load(InformationPO.class, Long.valueOf(informationId));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
			session = null;
            transaction = null;
		}
    	return po;
    }
    
    /**
     * 取单条信息（标题、副标题、内容、发布人、发布组织、发布时间、最后修改时间、版本、作者、文号、红头文件、印章文件、红头文件发布部门、红头文件发布日期）
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List getSingleInfo(String informationId,String channelId) throws Exception {
        List list = null;
        begin();
        try {
            /*START----------------OLD[何炜]20051025[NUM:2]-------------------*/
            /*
                         Query query = session.createQuery("select aaa.informationTitle, aaa.informationSubTitle, aaa.informationContent, " +
             "aaa.informationIssuer, aaa.informationIssueOrg, aaa.informationIssueTime, " +
             "aaa.informationModifyTime, aaa.informationVersion, aaa.informationAuthor, " +
             "aaa.informationMark, aaa.informationHeadFile, aaa.informationSeal, " +
             "aaa.infoRedIssueOrg, aaa.infoRedIssueTime, aaa.informationSummary, " +
             "aaa.informationKey, aaa.informationReaderName, aaa.informationReader, " +
                                              "aaa.informationReaderOrg, aaa.informationReaderGroup, aaa.informationValidType, " +
                                              "aaa.validBeginTime, aaa.validEndTime, aaa.informationHead, aaa.informationHeadId, " +
                                              "aaa.informationSealId, aaa.forbidCopy, aaa.transmitToEzsite, aaa.infoDepaFlag, " +
                                              "aaa.infoDepaFlag2, aaa.orderCode, aaa.displayTitle, aaa.otherChannel,aaa.titleColor," +
             "aaa.showSign,aaa.showSignName from " +
             "com.whir.ezoffice.information.infomanager.po.InformationPO aaa where " +
             "aaa.informationId = " + informationId );*/
            /*END------------------OLD[何炜]20051025[NUM:2]-------------------*/
            /*START----------------NEW[何炜]20051025[NUM:3]-------------------*/


            Query query = session.createQuery(
                    "select aaa.informationTitle, aaa.informationSubTitle, aaa.informationContent, " + //1
                    "aaa.informationIssuer, aaa.informationIssueOrg, aaa.informationIssueTime, " + //2
                    "aaa.informationModifyTime, aaa.informationVersion, aaa.informationAuthor, " + //3
                    "aaa.informationMark, aaa.informationHeadFile, aaa.informationSeal, " + //4
                    "aaa.infoRedIssueOrg, aaa.infoRedIssueTime, aaa.informationSummary, " + //5
                    "aaa.informationKey, aaa.informationReaderName, aaa.informationReader, " + //6
                    "aaa.informationReaderOrg, aaa.informationReaderGroup, aaa.informationValidType," + //7
                    "aaa.validBeginTime, aaa.validEndTime, aaa.informationHead," + //8
                    "aaa.informationHeadId, aaa.informationSealId, aaa.forbidCopy," + //9
                    "aaa.transmitToEzsite, aaa.infoDepaFlag,aaa.infoDepaFlag2, " + //10
                    "aaa.orderCode, aaa.displayTitle, aaa.otherChannel," + //11
                    "aaa.titleColor,aaa.showSign,aaa.showSignName, " + //12
                    "aaa.modifyEmp, aaa.dossierStatus, aaa.mustRead," + //13
                    "aaa.comeFrom,aaa.isConf,aaa.documentNo," + //14
                    "aaa.documentEditor,aaa.documentType, aaa.displayImage ," + //15
                    "aaa.afficeHistoryDate,aaa.wordDisplayType,bbb.channelReader," + //16
                    "bbb.channelReaderOrg,bbb.channelReaderGroup, aaa.informationOrISODoc," + //17
                    "aaa.isoDocStatus,aaa.isoOldInfoId,aaa.isoSecretStatus, " + //18
                    "aaa.isoDealCategory,aaa.isoApplyName,aaa.isoApplyId, " + //19
                    "aaa.isoReceiveName,aaa.isoReceiveId,aaa.isoModifyReason ," + //20
                    "aaa.isoAmendmentPage,aaa.isoModifyVersion,aaa.inforModifyMen," + //21
                    "aaa.inforModifyOrg,aaa.informationKits,bbb.userDefine, " +//22
                    "bbb.channelType,aaa.informationCanRemark,aaa.siteChannelString,"+//23
                    "aaa.twoUserId,aaa.isoOldInfoId,aaa.informationMailSendId,  "+//24
                    "aaa.informationMailSendName,aaa.informationMailSendOrg,aaa.informationMailSendGroup,"+//25
                    "aaa.informationMailSendUserId "+// 信息推送"
                    " from com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb where " +
                    "aaa.informationId = " + informationId);

            /*END------------------NEW[何炜]20051025[NUM:3]-------------------*/

            list = query.list();
            if(list.size()>0 && channelId!=null){
                String channelName="信息管理";
                List array=new ArrayList();
                array=session.createQuery("select ch.channelType,ch.channelIdString,ch.userDefine from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO ch where ch.channelId="+channelId).list();
                if(array!=null && array.size()>0){
                    Object[] obj=(Object[])array.get(0);
                    String chType=obj[0].toString();
                    String chIdString=obj[1].toString();
                    String userDefine=obj[2].toString();
                    if("1".equals(userDefine)){
                        //用户自定义频道
                        channelName=session.createQuery("select po.userChannelName from com.whir.ezoffice.information.channelmanager.po.UserChannelPO po where po.userChannelId="+chType).iterate().next().toString();
                    }else if(Integer.parseInt(chType)>0){
                        //单位主页
                        channelName=session.createQuery("select po.orgName from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgId="+chType).iterate().next().toString();

                    }
                    String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
                    Iterator it=null;
                    if (databaseType.indexOf("mysql") >= 0) {
                        it = session.createQuery("select po.channelName from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po where '"+chIdString+"' like concat('%$', po.channelId, '$%') order by po.channelLevel").
                             iterate();
                    }else{
                        it = session.createQuery("select po.channelName from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po where '"+chIdString+"' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', po.channelId), '$%') order by po.channelLevel").
                             iterate();
                    }
                    if(it!=null){
                        while(it.hasNext()){
                            channelName+="."+it.next().toString();
                        }
                    }
                }
                list.add(channelName);
            }

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }

    /**
     * 设置信息已浏览人
     * @param userId String 用户ID
     * @param userName String 用户姓名
     * @param orgName String 组织名称
     * @param informationId String 信息ID
     * @throws Exception
     */
    public void setKits(String userId, String userName, String orgName,
                        String informationId, String orgIdString) throws
            Exception {
        begin();
        String dd = "0";
        try {
            Query query = session.createQuery("select count(aaa.browserId) from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb where aaa.empId = " +
                                              userId +
                                              " and bbb.informationId = " +
                                              informationId);

            int count = ((Integer) (query.iterate().next())).intValue();
            if (count == 0) {
                //InformationPO informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId),LockMode.UPGRADE);
                InformationBrowserPO informationBrowserPO = new
                        InformationBrowserPO();
                informationBrowserPO.setEmpId(new Long(userId));
                informationBrowserPO.setBrowserName(userName);
                informationBrowserPO.setBrowserOrgName(orgName);
                informationBrowserPO.setBrowserOrgIdStr(orgIdString);
                //informationBrowserPO.setInformation(informationPO);
                informationBrowserPO.setBrowseTime(new Date());
                Long browserId = (Long) session.save(informationBrowserPO);
                dd = "" + browserId;
                session.flush();
                java.sql.Connection conn = session.connection();
                java.sql.Statement stmt = conn.createStatement();
                stmt.execute(
                        "update EZOFFICE.oa_informationBrowser set information_id=" +
                        informationId + " where browser_id=" + browserId);
                stmt.close();
                conn.close();
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
    }

    /**
     * 设置信息已浏览人
     * @param userId String 用户ID
     * @param userName String 用户姓名
     * @param orgName String 组织名称
     * @param informationId String 信息ID
     * @throws Exception
     */
    public void setBrowser(String userId, String userName, String orgName,
                           String informationId, String orgIdString) throws
            Exception {
        begin();
        try {
            Query query = session.createQuery("select count(aaa.browserId) from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb where aaa.empId = " +
                                              userId +
                                              " and bbb.informationId = " +
                                              informationId);

            int count = ((Integer) (query.iterate().next())).intValue();
            if (count == 0) {
                //InformationPO informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId),LockMode.UPGRADE);
                InformationBrowserPO informationBrowserPO = new
                        InformationBrowserPO();
                informationBrowserPO.setEmpId(new Long(userId));
                informationBrowserPO.setBrowserName(userName);
                informationBrowserPO.setBrowserOrgName(orgName);
                informationBrowserPO.setBrowserOrgIdStr(orgIdString);
                //informationBrowserPO.setInformation(informationPO);
                informationBrowserPO.setBrowseTime(new Date());
                Long browserId = (Long) session.save(informationBrowserPO);
                session.flush();
                java.sql.Connection conn = session.connection();
                java.sql.Statement stmt = conn.createStatement();
                stmt.execute(
                        "update EZOFFICE.oa_informationBrowser set information_id=" +
                        informationId + " where browser_id=" + browserId);
                stmt.close();
                conn.close();
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
    }


    public void setBrowser(String userId, String userName, String orgId,String orgName,
                         String informationId, String orgIdString) throws
          Exception {
      begin();
      try {
          Query query = session.createQuery("select count(aaa.browserId) from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb where aaa.empId = " +
                                            userId +
                                            " and bbb.informationId = " +
                                            informationId);

          int count = ((Integer) (query.iterate().next())).intValue();
          if (count == 0) {
              //InformationPO informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId),LockMode.UPGRADE);
              InformationBrowserPO informationBrowserPO = new
                      InformationBrowserPO();
              informationBrowserPO.setEmpId(new Long(userId));
              informationBrowserPO.setBrowserName(userName);
              informationBrowserPO.setBrowserOrgName(orgName);
              informationBrowserPO.setBrowserOrgIdStr(orgIdString);
              //informationBrowserPO.setInformation(informationPO);
              informationBrowserPO.setBrowseTime(new Date());
              informationBrowserPO.setOrgId(new Long(orgId));
              Long browserId = (Long) session.save(informationBrowserPO);
              session.flush();
              java.sql.Connection conn = session.connection();
              java.sql.Statement stmt = conn.createStatement();
              stmt.execute(
                      "update EZOFFICE.oa_informationBrowser set information_id=" +
                      informationId + " where browser_id=" + browserId);
              stmt.close();
              conn.close();
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
  }


    /**
     * 取信息的已浏览用户
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List getBrowser(String informationId, String searchName) throws
            Exception {
        List list = null;
        begin();
        try {
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql = "select aaa.browserName,aaa.browserOrgName,emp.userAccounts from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb,com.whir.org.vo.usermanager.EmployeeVO emp where emp.empId=aaa.empId and bbb.informationId = " +
                         informationId +
                         " and aaa.browserName like concat('%', '" + searchName +
                         "', '%') order by aaa.browserOrgIdStr";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql = "select aaa.browserName,aaa.browserOrgName,emp.userAccounts from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb,com.whir.org.vo.usermanager.EmployeeVO emp where emp.empId=aaa.empId and bbb.informationId = " +
                         informationId +
                        " and aaa.browserName like '%" +
                         searchName + "%' order by aaa.browserOrgIdStr";

            } else {
                tmpSql = "select aaa.browserName,aaa.browserOrgName,emp.userAccounts from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb,com.whir.org.vo.usermanager.EmployeeVO emp where emp.empId=aaa.empId and bbb.informationId = " +
                         informationId +
                        " and aaa.browserName like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%', '" +
                         searchName + "'), '%') order by aaa.browserOrgIdStr";
            }

            Query query = session.createQuery(tmpSql);
            list = query.list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }


    public List getchannleinfo(String informationId) throws Exception {
        List list = null;
        begin();
        try {
            Query query = session.createQuery("select aaa.channelId,aaa.channelName from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO aaa join aaa.information bbb where bbb.informationId = " +
                                              informationId);
            list = query.list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }


    public List getOrgName(String channelId) throws Exception {
        List list = null;
        begin();
        try {
            Query query = session.createQuery("select org.orgName, ch.channelName from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO ch,com.whir.org.vo.organizationmanager.OrganizationVO org where ch.channelType=org.orgId and ch.channelType>0 and ch.userDefine=0 and ch.channelId=" +
                                              channelId);
            list = query.list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }

    public List getAllOrgName(String flag) throws Exception {
        List list = null;
        begin();
        try {
            Query query = session.createQuery("select org.orgName, ch.channelName from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO ch,com.whir.org.vo.organizationmanager.OrganizationVO org where ch.channelType=org.orgId and ch.channelType>0 and ch.userDefine=0 order by org.orgName");
            list = query.list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }


    public boolean informationStatus(String informationId) throws Exception {
        boolean result = false;
        begin();
        try {

            InformationPO informationPO = (InformationPO) session.load(
                    InformationPO.class, new Long(informationId));
            informationPO.setInformationStatus(0);
            informationPO.setInformationModifyTime(new java.util.Date());
            session.flush();
            //informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId));
            result = true;
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }


    /**
     * 取信息的历史版本
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List getHistoryVersion(String informationId) throws Exception {
        List list = null;
        begin();
        try {
            Query query = session.createQuery("select aaa.historyVersion,aaa.historyIssueOrg,aaa.historyIssuerName,aaa.historyTime," +
                                              "aaa.historyId, aaa.historyHead,ccc.channelId,ccc.channelType,aaa.isoDealCategory,aaa.isoAmendmentPage,aaa.isoModifyReason,aaa.historyMark,aaa.historyIssuerId from " +
                                              "com.whir.ezoffice.information.infomanager.po.InformationHistoryPO aaa join aaa.information bbb " +
                                              "join bbb.informationChannel ccc where bbb.informationId = " +
                                              informationId +
                                              " order by aaa.historyTime desc, aaa.historyVersion desc ");//20160713 -by jqq 增加了排序historyVersion desc（oracle数据库环境出现过0.02版本在0.01之前了，时间相同）
            list = query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }

    /**
     * 取信息评论
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List getComment(String informationId) throws Exception {
        List list = null;
        begin();
        try {
        	//20151228 -by jqq 接口调用该方法，增加返回字段：用户头像empLivingPhoto
            Query query = session.createQuery("select aaa.commentIssuerOrg,aaa.commentIssuerName,aaa.commentIssueTime,aaa.commentContent,aaa.commentId,aaa.commentIssuerId,evo.empLivingPhoto  from com.whir.ezoffice.information.infomanager.po.InformationCommentPO aaa join aaa.information bbb, com.whir.org.vo.usermanager.EmployeeVO evo where bbb.informationId = " +
                                              informationId +
                                              " and aaa.commentIssuerId=evo.empId order by aaa.commentId desc ");
            list = query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }


    /**
     * 取当前信息的KEY
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List getinformation(String informationId) throws Exception {
        List contentID = null;
        begin();
        try {
            Query query = session.createQuery("select aaa.informationType,aaa.informationKey from com.whir.ezoffice.information.infomanager.po.InformationPO aaa where aaa.informationId = " +
                                              informationId);
            contentID = query.list();
        } catch (Exception e) {
            e.getMessage();
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return contentID;
    }

    /**
     * 取与当前信息的KEY相同信息的ID
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List getinformationID(String informationId_same,
                                 String informationId, String domainId) throws
            Exception {
        List contentID_same = null;
        begin();
        try {
            Query query = session.createQuery("select info.informationTitle,info.informationId,info.informationHead,ch.channelId,ch.channelName ,info.informationType from com.whir.ezoffice.information.infomanager.po.InformationPO info join info.informationChannel ch where info.domainId=" +
                                              domainId +
                                              " and info.informationKey = '" +
                                              informationId_same +
                                              "' and info.informationId <> " +
                                              informationId);
            contentID_same = query.list();
        } catch (Exception e) {
            e.getMessage();
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return contentID_same;
    }


    /**
     * 添加信息评论
     * @param userId String 用户ID
     * @param userName String 用户名称
     * @param orgName String 组织名称
     * @param content String 评论内容
     * @param informationId String 信息ID
     * @throws Exception
     */
    public void setComment(String userId, String userName, String orgName,
                           String content, String informationId) throws
            Exception {
        begin();
        try {
//            InformationPO informationPO = (InformationPO) session.load(
//                    InformationPO.class, new Long(informationId),
//                    LockMode.UPGRADE);
            InformationPO informationPO = (InformationPO) session.load(InformationPO.class, new Long(informationId));

            informationPO.setInformationCommonNum(informationPO.
                                                  getInformationCommonNum() + 1);
            InformationCommentPO informationCommentPO = new
                    InformationCommentPO();
            informationCommentPO.setCommentIssuerId(new Long(userId));
            informationCommentPO.setCommentIssuerName(userName);
            informationCommentPO.setCommentIssuerOrg(orgName);
            informationCommentPO.setCommentContent(content);
            //informationCommentPO.setInformation(informationPO);
            informationCommentPO.setCommentIssueTime(new java.util.Date());
            Long commentId = (Long) session.save(informationCommentPO);
            session.flush();
            java.sql.Connection conn = session.connection();
            java.sql.Statement stmt = conn.createStatement();
            stmt.execute(
                    "update EZOFFICE.oa_informationcomment set information_id = " +
                    informationId + " where comment_id = " + commentId);
            stmt.close();
            conn.close();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
    }

    /**
     * 修改信息评论
     * @param content String 评论内容
     * @param commentId String 评论Id
     * @throws Exception
     */
    public void updateComment(String content,String commentId) throws
           Exception {
       begin();
       try {
//           InformationCommentPO informationCommentPO=(InformationCommentPO) session.load(
//                    InformationCommentPO.class, new Long(commentId),
//                    LockMode.UPGRADE);
           InformationCommentPO informationCommentPO=(InformationCommentPO) session.load(
                    InformationCommentPO.class, new Long(commentId));

           informationCommentPO.setCommentContent(content);
           //informationCommentPO.setInformation(informationPO);
           informationCommentPO.setCommentIssueTime(new java.util.Date());
           session.update(informationCommentPO);
           session.flush();
       } catch (Exception e) {
           throw e;
       } finally {
           session.close();
           session = null;
           transaction = null;
       }
   }


    /**
     * 点击次数加1
     * @param informationId String 信息ID
     * @throws Exception
     */
    public void setKits(String informationId) throws Exception {
        begin();
        InformationPO informationPO = new InformationPO();
        try {
//            informationPO = (InformationPO) session.load(InformationPO.class,
//                    new Long(informationId), LockMode.UPGRADE);
            informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId));
            informationPO.setInformationKits((int) informationPO.
                                             getInformationKits() + 1);
            session.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
//            System.out.println("////////////////////ejb/////////////////");
//            System.out.println(informationPO.getInformationKits());
//            System.out.println((int)informationPO.getInformationKits() + 1);
//            System.out.println("////////////////////ejb/////////////////");
        }

    }

    /**
     * 修改信息时首先保存修改前的信息
     * @param informationId String 信息ID
     * @throws Exception
     */
    public void saveHistory(String informationId) throws Exception {
        begin();
        try {
//            InformationPO informationPO = (InformationPO) session.load(
//                    InformationPO.class, new Long(informationId),
//                    LockMode.UPGRADE);

            InformationPO informationPO = (InformationPO) session.load(InformationPO.class, new Long(informationId) );


            InformationHistoryPO informationHistoryPO = new
                    InformationHistoryPO();
            informationHistoryPO.setHistoryTitle(informationPO.
                                                 getInformationTitle());
            informationHistoryPO.setHistorySubTitle(informationPO.
                    getInformationSubTitle());
            informationHistoryPO.setHistorySummary(informationPO.
                    getInformationSummary());
            informationHistoryPO.setHistoryContent(informationPO.
                    getInformationContent());
            informationHistoryPO.setHistoryIssuerId(informationPO.
                    getInformationIssuerId());
            informationHistoryPO.setHistoryIssuerName(informationPO.
                    getInformationIssuer());
            informationHistoryPO.setHistoryIssueOrg(informationPO.
                    getInformationIssueOrg());
            //20160816 -by jqq 历史版本时间改造：第一次版本应该取发布时间
            informationHistoryPO.setHistoryTime(informationPO
                    .getInformationModifyTime() != null ? informationPO
                    .getInformationModifyTime() : (informationPO
                    .getInformationIssueTime() != null ? informationPO
                    .getInformationIssueTime() : new Date()));
            informationHistoryPO.setHistoryVersion(informationPO.
                    getInformationVersion());
            informationHistoryPO.setHistoryKey(informationPO.getInformationKey());
            informationHistoryPO.setHistoryHead(informationPO.
                                                getInformationHead());
            informationHistoryPO.setHistoryHeadFile(informationPO.
                    getInformationHeadFile());
            informationHistoryPO.setHistoryRedIssueTime(informationPO.
                    getInfoRedIssueTime());
            informationHistoryPO.setHistoryRedIssueOrg(informationPO.
                    getInfoRedIssueOrg());
            informationHistoryPO.setHistorySeal(informationPO.
                                                getInformationSeal());
            informationHistoryPO.setHistoryMark(" ");//修改信息时将历史版本的修改备注默认置空
            informationHistoryPO.setHistoryAuthor(informationPO.
                                                  getInformationAuthor());
            informationHistoryPO.setDomainId(informationPO.getDomainId());
            informationHistoryPO.setInformation(informationPO);
            informationHistoryPO.setHisDisplayImage(
            		informationPO.getDisplayImage()!=null&&!"".equals(informationPO.getDisplayImage()) ? 
            				informationPO.getDisplayImage() : "0");
            /*
             alter table oa_informationhistory add  isoDealCategory varchar2(100);
             alter table oa_informationhistory add  isoAmendmentPage number(20);
             alter table oa_informationhistory add  isoModifyReason  varchar2(1000);

             */
            if (informationPO.getIsoDealCategory() != null &&
                !informationPO.getIsoDealCategory().toString().equals("null")) {
                informationHistoryPO.setIsoDealCategory(informationPO.
                        getIsoDealCategory());
            } else {
                informationHistoryPO.setIsoDealCategory("");
            }
            if (informationPO.getIsoAmendmentPage() != null) {
                informationHistoryPO.setIsoAmendmentPage(informationPO.
                        getIsoAmendmentPage());
            } else {
                informationHistoryPO.setIsoAmendmentPage("");
            }
            if (informationPO.getIsoModifyReason() != null &&
                !informationPO.getIsoModifyReason().toString().equals("null")) {
                informationHistoryPO.setIsoModifyReason(informationPO.
                        getIsoModifyReason());
            } else {
                informationHistoryPO.setIsoModifyReason("");
            }
            if(informationPO.getInforModifyMen()!=null && !"".equals(informationPO.getInforModifyMen())){
            	informationHistoryPO.setHistoryIssuerName(informationPO.
                    getInforModifyMen());
            	informationHistoryPO.setHistoryIssueOrg(informationPO.
                    getInforModifyOrg());
            }
//            System.out.println(informationHistoryPO.getHistoryTitle());
//            System.out.println(informationHistoryPO.getHistorySubTitle());
//            System.out.println(informationHistoryPO.getHistorySummary());
//            System.out.println(informationHistoryPO.getHistoryContent());
//            System.out.println(informationHistoryPO.getHistoryIssuerId());
//            System.out.println(informationHistoryPO.getHistoryIssuerName());
//            System.out.println(informationHistoryPO.getHistoryIssueOrg());
//            System.out.println(informationHistoryPO.getHistoryVersion());
//            System.out.println(informationHistoryPO.getHistoryKey());
//            System.out.println(informationHistoryPO.getHistoryHead());
//            System.out.println(informationHistoryPO.getHistoryHeadFile());
//            System.out.println(informationHistoryPO.getHistoryRedIssueTime());
//            System.out.println(informationHistoryPO.getHistoryRedIssueOrg());
//            System.out.println(informationHistoryPO.getHistoryMark());
//            System.out.println(informationHistoryPO.getHistoryAuthor());
//            System.out.println(informationHistoryPO.getHistoryTime());
//            System.out.println(informationHistoryPO.getHistorySeal());
//            System.out.println(informationHistoryPO.getHisDisplayImage());
//            System.out.println(informationPO.
//                    getDisplayImage());

            session.save(informationHistoryPO);

            Query query = session.createQuery(" select aaa.accessoryIsImage,aaa.accessoryType,aaa.accessoryName,aaa.accessorySaveName,aaa.domainId " +
                                              " from com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO aaa " +
                                              " join aaa.information bbb where bbb.informationId = " +
                                              informationId);
            List list = query.list();
            for (int i = 0; i < list.size(); i++) {
                Object[] obj = (Object[]) list.get(i);
                InforHistoryAccessoryPO inforHistoryAccessoryPO = new
                        InforHistoryAccessoryPO();
                inforHistoryAccessoryPO.setAccessoryIsImage(Integer.parseInt(
                        obj[0].toString()));
                inforHistoryAccessoryPO.setAccessoryType(obj[1].toString());
                inforHistoryAccessoryPO.setAccessoryName(obj[2].toString());
                inforHistoryAccessoryPO.setAccessorySaveName(obj[3].toString());

                inforHistoryAccessoryPO.setDomainId(informationPO.getDomainId());
                inforHistoryAccessoryPO.setInformationHistory(
                        informationHistoryPO);
                session.save(inforHistoryAccessoryPO);
            }
            session.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
    }

    /**
     * 更新信息
     * @param informationId String 信息ID
     * @param parameters String[] 修改参数
     * @param accessoryList ArrayList 附件
     * @throws Exception
     */
    public boolean update(String informationId,
                       String[] parameters,
                       String[] assoInfo,
                       String[] infoAppendName,
                       String[] infoAppendSaveName,
                       String[] infoPicName,
                       String[] infoPicSaveName) throws Exception {
    	boolean result = false;
        begin();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            InformationPO informationPO = (InformationPO) session.load(
                    InformationPO.class, new Long(informationId));
            //信息草稿修改保存后，可能存在栏目为空的情况
            InformationChannelPO channelPO = null;
            if(parameters[31] != null && !"".equals(parameters[31])){
                channelPO = (InformationChannelPO) session.
                load(InformationChannelPO.class,
                     new Long(parameters[31]));
            }
            String version = informationPO.getInformationVersion();
            //iso
            if (parameters[41] != null && parameters[41].toString().equals("1")) {
                  //iso特定状态才改版本号
                if("3".equals(parameters[45]+"")||"4".equals(parameters[45]+"")){

                }else{
                int a = Integer.parseInt(version.substring(0,
                        version.indexOf(".")));
                version = (a + 1) + ".00";
             }
            } else {
                //信息每版本必改
                /*int a = Integer.parseInt(version.substring(0,
                        version.indexOf(".")));
                int b = Integer.parseInt(version.substring(version.indexOf(".") +
                        1, version.length()));
                if (b + 1 == 0) {
                    version = (a + 1) + ".0";
                } else {
                    version = (a) + "." + (b + 1);
                }*/
            	if(parameters[73].toString().equals("0")){
	                int a = Integer.parseInt(version.substring(0,
	                        version.indexOf(".")));
	                int b = Integer.parseInt(version.substring(version.indexOf(".") +
	                        1, version.length()));
	                 version = (a + 1) + ".00";
            	}
            }
            informationPO.setInformationChannel(channelPO);
            informationPO.setInformationVersion(version);
            informationPO.setInformationTitle(parameters[0]);
            informationPO.setInformationSubTitle(parameters[1]);
            informationPO.setInformationSummary(parameters[2]);
            informationPO.setInformationKey(parameters[3]);
            //informationPO.setInformationIssuerId(new Long(parameters[5]));
            //informationPO.setInformationIssuer(parameters[6]);
            //informationPO.setInformationIssueOrg(parameters[7]);
            /*START----------------NEW[何炜]20051025[NUM:1]-------------------*/
            informationPO.setModifyEmp(parameters[7] + "." + parameters[6]);
            /*END------------------NEW[何炜]20051025[NUM:1]-------------------*/
            informationPO.setInformationModifyTime(new java.util.Date());
            informationPO.setInformationReaderName(parameters[8]);
            informationPO.setInformationReader(parameters[9]);
            informationPO.setInformationReaderOrg(parameters[10]);
            informationPO.setInformationReaderGroup(parameters[11]);
            informationPO.setInformationValidType(Integer.parseInt(parameters[
                    12]));
            informationPO.setValidBeginTime(sdf.parse(parameters[13]));
            informationPO.setValidEndTime(sdf.parse(parameters[14]));
            if (parameters[15] != null && !parameters[15].equals("")) {
                informationPO.setInformationHead(Integer.parseInt(parameters[15]));
            }
            informationPO.setInformationHeadFile(parameters[16]);
            informationPO.setInformationSeal(parameters[17]);
            informationPO.setInformationMark(parameters[18]);
            informationPO.setInfoRedIssueOrg(parameters[19]);
            informationPO.setInfoRedIssueTime(parameters[20]);
            informationPO.setInformationHeadId(new Long(parameters[21]));
            informationPO.setInformationSealId(new Long(parameters[22]));
            informationPO.setTransmitToEzsite(Integer.parseInt(parameters[23]));
            informationPO.setForbidCopy(Integer.parseInt(parameters[24]));
            informationPO.setInformationAuthor(parameters[28]);
            if (parameters[29] != null && !"".equals(parameters[29])) {
                //草稿的修改页面，进行保存草稿，发布时间不变，修改时间取服务器时间，与页面发布时间无关
                //草稿的修改页面，进行保存发布信息，发布时间取页面值
                if(informationPO.getInformationStatus() != 9 || 
                        (informationPO.getInformationStatus() == 9 && parameters[73] != null && "0".equals(parameters[73]))){
                    informationPO.setInformationIssueTime(sdf.parse(parameters[29]));
                }
//                java.util.Calendar IssueTime = java.util.Calendar.getInstance();
//                String[] issueTime = parameters[29].split("/");
//                IssueTime.set(Integer.parseInt(issueTime[0]),
//                              Integer.parseInt(issueTime[1]) - 1,
//                              Integer.parseInt(issueTime[2]));
//                informationPO.setInformationIssueTime(IssueTime.getTime());//发布日期
                //               informationPO.setInformationIssueTime(new Date(parameters[29]));
//                java.util.Calendar IssueTime = java.util.Calendar.getInstance();
//                IssueTime.setTime(informationPO.getInformationIssueTime());
//                String[] issueTime = parameters[29].split("/");
//                IssueTime.set(Integer.parseInt(issueTime[0]),
//                              Integer.parseInt(issueTime[1]) - 1,
//                              Integer.parseInt(issueTime[2]));
//                informationPO.setInformationIssueTime(IssueTime.getTime()); //发布日期
                /*if(IssueTime.get(Calendar.YEAR) != Integer.parseInt(issueTime[0])
                 && IssueTime.get(Calendar.MONTH) != Integer.parseInt(issueTime[1]) - 1
                 && IssueTime.get(Calendar.DATE) != Integer.parseInt(issueTime[2])){
                    IssueTime.set(Integer.parseInt(issueTime[0]),
                                  Integer.parseInt(issueTime[1]) - 1,
                                  Integer.parseInt(issueTime[2]));
                 informationPO.setInformationIssueTime(IssueTime.getTime());//发布日期
                                 }*/
            }else{
                //页面的发布时间是空的情况:除保存草稿外，取服务器时间；草稿保存发布时间与页面的发布时间无关，只跟第一次保存草稿时间有关
                if(informationPO.getInformationStatus() != 9 || 
                        (informationPO.getInformationStatus() == 9 && parameters[73] != null && "0".equals(parameters[73]))){
                    informationPO.setInformationIssueTime(new Date());
                }
            }
            //informationPO.setInformationIssueTime(new java.util.Date(parameters[29]));
            informationPO.setTitleColor(new Integer(parameters[30]));
            if (parameters[25].equals("")) {
                informationPO.setOrderCode("1000");
            } else {
                informationPO.setOrderCode(parameters[25]);
            }
            informationPO.setDisplayTitle(Integer.parseInt(parameters[26]));
            informationPO.setOtherChannel(parameters[27]);
            informationPO.setInformationContent(parameters[4]);
            //20151216 -by jqq 修改因mustread字段为空导致报错的问题
            if(parameters[32] == null || "".equals(parameters[32])){
            	parameters[32] = "0";
            }
            //System.out.println("========MustRead:parameters[32]:" + parameters[32]);
            informationPO.setMustRead(Integer.valueOf(parameters[32]));
            informationPO.setComeFrom(parameters[33]);
            //20150524 -by jqq 信息草稿改造
            //信息草稿页面保存时，创建时间/修改时间/信息状态 修改(数据库未更新状态仍为9，且传参状态为0)
            if(informationPO.getInformationStatus() == 9 && parameters[73] != null && "0".equals(parameters[73])){
                //此时是在信息草稿的修改页面，进行的保存发布，使用页面的发布时间
                //informationPO.setInformationIssueTime(new Date());
                informationPO.setInformationModifyTime(null);
                informationPO.setInformationStatus(0);
                //保存草稿页面进行发布，首次发布的信息，版本还是1.00
                informationPO.setInformationVersion("1.00");
            }
            if (parameters.length > 34) {
                if (parameters[34] != null) {
                    informationPO.setIsConf(Integer.valueOf("1"));
                } else {
                    informationPO.setIsConf(Integer.valueOf("0"));
                }
            }
            if (parameters.length > 35) {
                if (parameters[35] != null) {
                    informationPO.setDocumentNo(parameters[35]);
                } else {
                    informationPO.setDocumentNo("0");
                }
            }
            if (parameters.length > 36) {

                informationPO.setDocumentEditor(parameters[36]);
            }
            if (parameters.length > 37) {
                informationPO.setDocumentType(parameters[37]);
            }

            if (parameters.length > 38) {
                informationPO.setDisplayImage(parameters[38]);
            }
            if (parameters.length > 39) {
                informationPO.setWordDisplayType(parameters[39]);
            }

            if (parameters.length > 41) {
                informationPO.setInformationOrISODoc(parameters[41]);
            }
            if (parameters.length > 42) {
                informationPO.setIsoDocStatus(parameters[42]);
            }
            if (parameters.length > 43) {
                informationPO.setIsoOldInfoId(parameters[43]);
            }
            if (parameters.length > 44) {
                informationPO.setIsoSecretStatus(parameters[44]);
            }
            if (parameters.length > 45) {
                informationPO.setIsoDealCategory(parameters[45]);
            }
            if (parameters.length > 46) {
                informationPO.setIsoApplyName(parameters[46]);
            }
            if (parameters.length > 47) {
                informationPO.setIsoApplyId(parameters[47]);
            }
            if (parameters.length > 48) {
                informationPO.setIsoReceiveName(parameters[48]);
            }
            if (parameters.length > 49) {
                informationPO.setIsoReceiveId(parameters[49]);
            }
            if (parameters.length > 50) {
                informationPO.setIsoModifyReason(parameters[50]);
            }
            if (parameters.length > 51) {
                if (parameters[51] != null &&
                    !parameters[51].toString().equals("null")) {
                    informationPO.setIsoAmendmentPage("" + parameters[51]);
                } else {
                    informationPO.setIsoAmendmentPage("");
                }
            }
            if (parameters.length > 52) {
                informationPO.setIsoModifyVersion(parameters[52]);
            }
            if (parameters.length > 53) {
                informationPO.setInforModifyMen(parameters[53]);
            }
            if (parameters.length > 54) {
                informationPO.setInforModifyOrg(parameters[54]);
            }

            if (parameters.length > 55) {
                if(parameters[55]!=null&&!parameters[55].toString().equals(""))
                 informationPO.setInformationCanRemark(new Long(parameters[55]));
           }

           // 同步到网站的栏目
           if (parameters.length > 56) {
               if (parameters[56] != null &&
                   !parameters[56].toString().equals(""))
                   informationPO.setSiteChannelString(parameters[56]);
           }

          //信息推送
          if (parameters.length > 57) {
              informationPO.setInformationMailSendId(parameters[57]);
              informationPO.setInformationMailSendName(parameters[58]);
              informationPO.setInformationMailSendUserId(parameters[59]);
              informationPO.setInformationMailSendOrg(parameters[60]);
              informationPO.setInformationMailSendGroup(parameters[61]);
          }
          informationPO.setRemindType(parameters[62]);
          
          //信息打印与下载权限
          if (parameters.length > 63) {
        	  informationPO.setInformationPrinterName(parameters[63]);
        	  informationPO.setInformationPrinter(parameters[64]);
        	  informationPO.setInformationPrinterOrg(parameters[65]);
        	  informationPO.setInformationPrinterGroup(parameters[66]);
        	  informationPO.setPrintNum(parameters[67]);
        	  informationPO.setInformationDownLoaderName(parameters[68]);
        	  informationPO.setInformationDownLoader(parameters[69]);
        	  informationPO.setInformationDownLoaderOrg(parameters[70]);
        	  informationPO.setInformationDownLoaderGroup(parameters[71]);
        	  informationPO.setDownLoadNum(parameters[72]);
          }

          if(parameters.length > 73){
        	  informationPO.setInformationStatus(Integer.parseInt(parameters[73]));
          }

            java.util.Set accessory = informationPO.getInformationAccessory();
            informationPO.setInformationAccessory(null);
            java.util.Iterator iter = accessory.iterator();
            while (iter.hasNext()) {
                session.delete((InformationAccessoryPO) iter.next());
            }
            java.util.HashSet acceSet = new java.util.HashSet();
            if (infoAppendName != null) {
                for (int i = 0; i < infoAppendName.length; i++) {
                    InformationAccessoryPO accePO = new InformationAccessoryPO();
                    accePO.setAccessoryIsImage(0);
                    accePO.setAccessoryName(infoAppendName[i]);
                    accePO.setAccessorySaveName(infoAppendSaveName[i]);
                    if (infoAppendSaveName[i].indexOf(".") > 0) {
                        accePO.setAccessoryType(infoAppendSaveName[i].substring(
                                infoAppendSaveName[i].indexOf(".") + 1));
                    } else {
                        accePO.setAccessoryType("");
                    }
                    accePO.setInformation(informationPO);
                    acceSet.add(accePO);
                    session.save(accePO);
                }
            }
            if (infoPicName != null) {
                for (int i = 0; i < infoPicName.length; i++) {
                    InformationAccessoryPO accePO = new InformationAccessoryPO();
                    accePO.setAccessoryIsImage(1);
                    accePO.setAccessoryName(infoPicName[i]);
                    accePO.setAccessorySaveName(infoPicSaveName[i]);
                    if (infoPicSaveName[i].indexOf(".") > 0) {
                        accePO.setAccessoryType(infoPicSaveName[i].substring(
                                infoPicSaveName[i].indexOf(".") + 1));
                    } else {
                        accePO.setAccessoryType("");
                    }
                    accePO.setInformation(informationPO);
                    acceSet.add(accePO);
                    session.save(accePO);
                }
            }
            informationPO.setInformationAccessory(acceSet);
            informationPO.setEditUserId("");
            informationPO.setEditUserName("");//修改保存时将正在编辑用户置空
            session.update(informationPO);
            session.flush();

            session.delete(
                    "from com.whir.ezoffice.information.infomanager.po.AssociateInfoPO aaa  " +
                    "where aaa.masterInfo = " + informationId);

            if (assoInfo != null) {
                for (int j = 0; j < assoInfo.length; j++) {
                    AssociateInfoPO assoPO = new AssociateInfoPO();
                    assoPO.setAssociateInfo(new Long(assoInfo[j]));
                    assoPO.setMasterInfo(new Long(informationId));
                    session.save(assoPO);
                }
            }
            session.flush();
            result = true;
        } catch (Exception e) {
            System.out.println(
                    "-------------------------------------------------");
            e.printStackTrace();
            System.out.println(
                    "-------------------------------------------------");

        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        //信息草稿，栏目可能为空
        if(parameters[31] != null && !"".equals(parameters[31])){
          //修改信息查看权限
            SaveInfoReader saveInfoReader = SaveInfoReader.getInstance();
            saveInfoReader.update(parameters[31], parameters[27], informationId,
                                  parameters[9], parameters[10].toString().replaceAll("null", ""), parameters[11]);
        }
        
        return result;

    }


    /**
     * 删除附件信息
     * @param informationId String 信息ID
     * @param accessory String 附件ID
     * @throws Exception
     */
    public void deleteAccessory(String informationId, String accessory) throws
            Exception {
        begin();
        try {
//            InformationPO informationPO = (InformationPO) session.load(
//                    InformationPO.class, new Long(informationId),
//                    LockMode.UPGRADE);
            InformationPO informationPO = (InformationPO) session.load(InformationPO.class, new Long(informationId) );

//           String version = informationPO.getInformationVersion();
//           int a = Integer.parseInt(version.substring(0,version.indexOf(".")));
//           int b = Integer.parseInt(version.substring(version.indexOf(".") + 1,version.length()));
//           if( b + 1 == 0){
//               version = (a + 1) + ".0";
//           }else{
//               version = (a) + "." + (b + 1);
//           }
//           informationPO.setInformationVersion(version);
//           informationPO.setInformationAccessory(null);
            java.util.Set acceSet = informationPO.getInformationAccessory();
//            InformationAccessoryPO accessoryPO = (InformationAccessoryPO)
//                                                 session.load(
//                    InformationAccessoryPO.class, new Long(accessory),
//                    LockMode.UPGRADE);
            InformationAccessoryPO accessoryPO = (InformationAccessoryPO)
                                            session.load(InformationAccessoryPO.class, new Long(accessory));

            acceSet.remove(accessoryPO);
            informationPO.setInformationAccessory(acceSet);
            session.delete(accessoryPO);
            session.flush();
        } catch (Exception e) {
            System.out.println(
                    "-------------------------------------------------");
            e.printStackTrace();
            System.out.println(
                    "-------------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
    }

    /**
     * 取单条历史信息用于浏览
     * @param historyId String 历史ID
     * @throws Exception
     * @return List
     */
    public List getSingleHistInfo(String historyId) throws Exception {
        List list = null;
        begin();
        try {
            /**
             Query query = session.createQuery(" select aaa.historyTitle,aaa.historySubTitle,aaa.historyTitle, " +
             " aaa.historyIssuerName,aaa.historyIssueOrg,aaa.historyTime, " +
             " bbb.informationModifyTime,aaa.historyVersion,aaa.historyAuthor, " +
             " aaa.historyMark,aaa.historyHeadFile,aaa.historySeal, " +
             " aaa.historyRedIssueOrg,aaa.historyRedIssueTime,aaa.historySummary, " +
             " aaa.historyKey, bbb.forbidCopy, bbb.infoDepaFlag,"+
                                              " bbb.infoDepaFlag2, bbb.displayTitle,aaa.documentNo,"+                                       //14
             " aaa.documentEditor,aaa.documentType "+
             " from com.whir.ezoffice.information.infomanager.po.InformationHistoryPO aaa " +
             " join aaa.information bbb where aaa.historyId = " + historyId );

             */
            Query query = session.createQuery(
                    " select aaa.historyTitle,aaa.historySubTitle,aaa.historyTitle, " +
                    " aaa.historyIssuerName,aaa.historyIssueOrg,aaa.historyTime, " +
                    " bbb.informationModifyTime,aaa.historyVersion,aaa.historyAuthor, " +
                    " aaa.historyMark,aaa.historyHeadFile,aaa.historySeal, " +
                    " aaa.historyRedIssueOrg,aaa.historyRedIssueTime,aaa.historySummary, " +
                    " aaa.historyKey, bbb.forbidCopy, bbb.infoDepaFlag, bbb.infoDepaFlag2, bbb.displayTitle,aaa.hisDisplayImage,aaa.isoDealCategory from " +
                    " com.whir.ezoffice.information.infomanager.po.InformationHistoryPO aaa " +
                    " join aaa.information bbb where aaa.historyId = " +
                    historyId);
            list = query.list();
            query = session.createQuery("select aaa.historyContent from com.whir.ezoffice.information.infomanager.po.InformationHistoryPO aaa " +
                                        "where aaa.historyId = " + historyId);
            // java.sql.Clob clob = (Clob)query.iterate().next();
            String content = (String) query.iterate().next();
            //if(clob!=null){
            //    content=clob.getSubString(1, (int) clob.length());
            //}
            list.add(content);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return list;
    }

    /**
     * 推荐信息
     * @param batchId String[] 信息ID数组
     * @throws Exception
     */
    public void commend(String[] batchId) throws Exception {
        begin();
        try {
            InformationPO informationPO = new InformationPO();
            for (int i = 0; i < batchId.length; i++) {
//                informationPO = (InformationPO) session.load(InformationPO.class,
//                        new Long(batchId[i]), LockMode.UPGRADE);
                informationPO = (InformationPO) session.load(InformationPO.class,new Long(batchId[i]));
                informationPO.setInformationIsCommend(new Long("1"));
            }
            session.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
    }

    /**
     *  取消推荐
     *
     * @param id String
     * @throws Exception
     */
    public void removeCommend(String id) throws Exception {
        begin();
        try {
            InformationPO informationPO = new InformationPO();

//            informationPO = (InformationPO) session.load(InformationPO.class,
//                    new Long(id), LockMode.UPGRADE);

            informationPO = (InformationPO) session.load(InformationPO.class, new Long(id) );
            informationPO.setInformationIsCommend(new Long("0"));

            session.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
    }


    /**
     * 批量删除信息，返回信息附件（包括历史信息附件）名称数组
     * @param batchId String[] 信息ID数组
     * @throws Exception
     * @return String[]
     */
    public List batchDelete(String[] batchId) throws Exception {
        List result = null;
        StringBuffer sb = new StringBuffer(batchId.length);
        for (int i = 0; i < batchId.length; i++) {
            sb.append(batchId[i] + ",");
        }
        String tmp = sb.substring(0, sb.length() - 1).toString();
        begin();
        try {
            Query query = session.createQuery(
                    " select aaa.accessorySaveName from " +
                    " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO aaa " +
                    " join aaa.information bbb where bbb.informationId in (" +
                    tmp + ")");
            result = query.list();
            query = session.createQuery(" select aaa.accessorySaveName from " +
                                        " com.whir.ezoffice.information.infomanager.po.InforHistoryAccessoryPO aaa join " +
                                        " aaa.informationHistory bbb join bbb.information ccc where " +
                                        " ccc.informationId in (" + tmp + ")");
            List list2 = query.list();
            for (int i = 0; i < list2.size(); i++) {
                result.add(list2.get(i));
            }

            StringBuffer buffer=new StringBuffer();
            Iterator it=session.createQuery("select info.informationTitle from com.whir.ezoffice.information.infomanager.po.InformationPO info where info.informationId in("+tmp+")").iterate();
            while(it.hasNext()){
                buffer.append(it.next()).append(",");
            }
            result.add(buffer.toString());
            
            //删除oa_infomrationhistory表的历史记录
            java.sql.Connection conn = session.connection();
            java.sql.Statement stmt = conn.createStatement();
            stmt.execute("DELETE from EZOFFICE.OA_INFORMATIONHISTORY WHERE INFORMATION_ID IN (" +tmp + ")");
            //删除oa_informationbrowser表的记录
            stmt.execute("DELETE from EZOFFICE.OA_INFORMATIONBROWSER WHERE INFORMATION_ID IN (" +tmp + ")");
            
            stmt.execute("DELETE from EZOFFICE.OA_INFORMATION WHERE INFORMATION_ID IN (" +tmp + ")");
            //20160524 -by jqq 删除信息关联标签的关联记录
            stmt.execute("DELETE from EZOFFICE.OA_INFOTAGRELATION WHERE INFORMATION_ID IN (" +tmp + ")");
            stmt.close();
            conn.close();
            
            //session.delete("from com.whir.ezoffice.information.infomanager.po.InformationPO info where info.informationId in("+tmp+")");
            /*
                        InformationPO infoPO = null;
                        java.util.Set histInfo = null;
                        java.util.Set infoAcce = null;
                        java.util.Iterator iter = null;
                        for(int i = 0; i < batchId.length; i ++){
                infoPO = (InformationPO) session.load(InformationPO.class, new Long(batchId[i]), LockMode.UPGRADE);
                histInfo = infoPO.getInformationHistory();
                if(histInfo != null){
                    iter = histInfo.iterator();
                    while(iter.hasNext()){
                        session.delete((InformationAccessoryPO) iter.next());
                    }
                }
                infoAcce = infoPO.getInformationAccessory();
                if(infoAcce != null){
                    iter = infoAcce.iterator();
                    while(iter.hasNext()){
                        session.delete((InformationAccessoryPO) iter.next());
                    }
                }
                infoPO.setInformationHistory(null);
                infoPO.setInformationAccessory(null);
                session.delete(infoPO);
                        }
                        session.flush();*/

        } catch (Exception e) {
            System.out.println(
                    "-----------------------------------------------");
            e.printStackTrace();
            System.out.println(
                    "-----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }

    /**
     * 清空一个频道下的所有的信息,返回信息附件（包括历史信息附件）名称集合
     * @param channelId String 频道ID
     * @throws Exception
     * @return List
     */
    public List allDelete(String channelId) throws Exception {

        List result = null;
        begin();
        try {
            Query query = session.createQuery(" select aaa.informationId from " +
                                              " com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
                                              " join aaa.informationChannel bbb where bbb.channelId = " +
                                              channelId);
            List list = query.list();
            StringBuffer sb = new StringBuffer(list.size());
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i) + ",");
            }
            String tmp = sb.substring(0, sb.length() - 1).toString();

            query = session.createQuery(" select aaa.accessorySaveName from " +
                                        " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO aaa " +
                                        " join aaa.information bbb where bbb.informationId in (" +
                                        tmp + ")");
            result = query.list();
            query = session.createQuery(" select aaa.accessorySaveName from " +
                                        " com.whir.ezoffice.information.infomanager.po.InforHistoryAccessoryPO aaa join " +
                                        " aaa.informationHistory bbb join bbb.information ccc where " +
                                        " ccc.informationId in (" + tmp + ")");
            list = query.list();

            for (int i = 0; i < list.size(); i++) {
                result.add(list.get(i));
            }
            InformationPO infoPO = null;
            java.util.Set histInfo = null;
            java.util.Set infoAcce = null;
            java.util.Iterator iter = null;
            for (int i = 0; i < list.size(); i++) {
//                infoPO = (InformationPO) session.load(InformationPO.class,
//                        new Long(list.get(i).toString()), LockMode.UPGRADE);
                infoPO = (InformationPO) session.load(InformationPO.class,new Long(list.get(i).toString()));
                histInfo = infoPO.getInformationHistory();
                if (histInfo != null) {
                    iter = histInfo.iterator();
                    while (iter.hasNext()) {
                        session.delete((InformationAccessoryPO) iter.next());
                    }
                }
                infoAcce = infoPO.getInformationAccessory();
                if (infoAcce != null) {
                    iter = infoAcce.iterator();
                    while (iter.hasNext()) {
                        session.delete((InformationAccessoryPO) iter.next());
                    }
                }
                infoPO.setInformationHistory(null);
                infoPO.setInformationAccessory(null);
                session.delete(infoPO);
            }
            session.flush();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;

    }

    /**
     * 删除单条信息,返回信息附件（包括历史信息附件）名称集合
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List singleDelete(String channelId, String informationId) throws
            Exception {
        List result = null;
        begin();
        try {
            java.sql.Connection conn = session.connection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs1 = stmt.executeQuery(
                    "SELECT WF_IMMOFORM_ID FROM EZOFFICE.WF_IMMOBILITYFORM WHERE WF_MODULE_ID=21");
            String tableId = "0";
            if (rs1.next()) {
                tableId = rs1.getString(1);
            }
            rs1.close();
            stmt.execute("DELETE from EZOFFICE.WF_WORK WHERE WORKTABLE_ID=" +
                         tableId + " AND WORKRECORD_ID=" + informationId);

            Query query = session.createQuery(
                    " select aaa.accessorySaveName from " +
                    " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO aaa " +
                    " join aaa.information bbb where bbb.informationId = " +
                    informationId);
            result = query.list();
            stmt.execute(" DELETE from oa_informationaccessory  WHERE information_id=" + informationId);
            query = session.createQuery(" select aaa.accessorySaveName from " +
                                        " com.whir.ezoffice.information.infomanager.po.InforHistoryAccessoryPO aaa join " +
                                        " aaa.informationHistory bbb join bbb.information ccc where " +
                                        " ccc.informationId = " + informationId);
            stmt.execute(" DELETE from oa_inforhistoryaccessory  where   history_id  in (select  history_id  from OA_INFORMATIONHISTORY  where information_id= "+ informationId+")");
            List list = query.list();
            for (int i = 0; i < list.size(); i++) {
                result.add(list.get(i));
            }

            String name="";
            Iterator it=session.createQuery("select info.informationTitle from com.whir.ezoffice.information.infomanager.po.InformationPO info where info.informationId ="+informationId).iterate();
            while(it.hasNext()){
               name=it.next().toString();
            }
            result.add(name);


            //如果是被同时发布到该栏目的信息则只将信息从该栏目下移除，信息仍然在主栏目中,否则将删除信息
            String infoChannelId = "";
            String otherChannel = "";
            java.sql.ResultSet rs = stmt.executeQuery("select channel_id,otherchannel from ezoffice.oa_information where information_id=" +
                    informationId);
            if (rs.next()) {
                infoChannelId = rs.getString(1);
                otherChannel = rs.getString(2);
            }
            rs.close();
            if (channelId.equals(infoChannelId)) {
                stmt.execute(
                        "delete from EZOFFICE.oa_information where information_id=" +
                        informationId);
                stmt.execute("delete from EZOFFICE.wf_work where worktable_id in(select wf_immoform_id from EZOFFICE.wf_immobilityform where immoform_displayname='信息内容表') and workrecord_id=" +
                             informationId);
            } else {
                otherChannel = otherChannel.replaceAll("," + channelId + ",",
                        "");
                stmt.executeUpdate(
                        "update ezoffice.oa_information set otherchannel='" +
                        otherChannel + "' where information_id=" +
                        informationId);
            }
            stmt.close();
            conn.close();
            /*
                         InformationPO infoPO = null;
                         java.util.Set histInfo = null;
                         java.util.Set infoAcce = null;
                         java.util.Iterator iter = null;

                         infoPO = (InformationPO) session.load(InformationPO.class, new Long(informationId));
                         infoPO.setInformationHistory(null);

                         histInfo = infoPO.getInformationHistory();
                         if(histInfo != null){
                iter = histInfo.iterator();
                while(iter.hasNext()){
             InformationHistoryPO histPO = (InformationHistoryPO) iter.next();
                    histPO.setInformation(null);
                    session.delete((InformationHistoryPO) iter.next());
                }
                         }
                         infoAcce = infoPO.getInformationAccessory();
                         if(infoAcce != null){
                iter = infoAcce.iterator();
                while(iter.hasNext()){
                    session.delete((InformationAccessoryPO) iter.next());
                }
                         }
                         infoPO.setInformationHistory(null);
                         infoPO.setInformationAccessory(null);

                         session.delete(infoPO);
                         session.flush();*/

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }

    /**
     * 信息转移
     * @param infoId String[] 信息ID数组
     * @param channelId String 要转移到的目标栏目
     * @param orchannelId String 信息的当前栏目
     */
    public void transfer(String[] infoId, String channelId, String orchannelId) throws
            Exception {
        begin();
        try {
            InformationChannelPO informationChannelPO = (InformationChannelPO) session.load(InformationChannelPO.class, new Long(channelId));
            for (int i = 0; i < infoId.length; i++) {
                InformationPO informationPO = (InformationPO) session.load(InformationPO.class, new Long(infoId[i]));
                String otherChannel = informationPO.getOtherChannel();
                //如果信息原来是同时发布到目标栏目，则将信息的同时发布到取消
                if(otherChannel.indexOf(","+channelId+",")>-1){
                	informationPO.setOtherChannel(",0,");
                }
                informationPO.setInformationChannel(informationChannelPO);
//                String curchannel = informationPO.getInformationChannel().getChannelId().toString();
//                //取每条信息的原栏目ID
//                orchannelId = informationPO.getInformationChannel().getChannelId().toString();
//                if (curchannel.equals(orchannelId)) {
//                    //若信息的是属于当前栏目下的信息，则转移并将同时发布到其它栏目的信息取消
//                    informationPO.setInformationChannel(informationChannelPO);
//                    informationPO.setOtherChannel("");
//                } else {
//                    //若信息是其它栏目下的主信息仅是同步发布到本栏目的信息，则信息仍保留在主栏目
//                    //将原来的otherChannel中的原栏目ID用新的栏目ID替代
//                    String orOtherChannel = informationPO.getOtherChannel();
//                    if (orOtherChannel == null)
//                        orOtherChannel = "";
//                    orOtherChannel = orOtherChannel.replaceAll("," + orchannelId + ",", "," + channelId + ",");
//                    informationPO.setOtherChannel(orOtherChannel);
//                }
                informationPO.setInformationReader(informationChannelPO.getChannelReader());
                informationPO.setInformationReaderGroup(informationChannelPO.getChannelReaderGroup());
                informationPO.setInformationReaderOrg(informationChannelPO.getChannelReaderOrg());
                informationPO.setInformationReaderName(informationChannelPO.getChannelReaderName());
                session.update(informationPO);
                
                SaveInfoReader saveInfoReader = SaveInfoReader.getInstance();
                saveInfoReader.update(channelId, otherChannel, infoId[i],
                		informationPO.getInformationReader(), informationPO.getInformationReaderOrg(), informationPO.getInformationReaderGroup());
            }
            session.flush();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
    }

    /**
     * 取最近更新15条
     * @param userId String 用户ID
     * @param orgId String 组织ID
     * @return List
     */
    public List getNew(String userId, String orgId) throws Exception {
        begin();
        List list = null;
        try {
            String hSql =
                    "select top 15 aaa.channelName, bbb.informationId, bbb.informationTitle, " +
                    " bbb.informationKits, bbb.informationIssueTime, bb.informationHead " +
                    " com.whir.ezoffice.information.infomanager.po.InformationPO bbb join " +
                    " aaa.informationChannel aaa ";

            //取用户组织的所有下级组织(包括自身)
            Query query = session.createQuery(
                    "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                    " where aaa.orgIdString like '%$" + orgId + "$%'");
            List orgList = query.list();

            //取用户信息分类维护的权限范围 由信息管理设置代替

            query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                        " join aaa.right bbb join aaa.employee ccc where bbb.rightType = '01*02*01' and " +
                                        " and ccc.empId = " + userId);
            List tmpList = query.list();
            boolean allScope = false;
            String scopeString = "";
            if (tmpList != null && tmpList.size() > 0) {
                Object[] obj = (Object[]) tmpList.get(0);
                String scopeType = obj[0].toString();
                String scopeScope = "";
                if (obj[1] != null) {
                    scopeScope = obj[1].toString().substring(1,
                            obj[1].toString().length() - 1);
                }
                if (!scopeType.equals("0")) {
                    if (scopeType.equals("1")) {
                        scopeString = " aaa.createdEmp = " + userId + " or ";
                    } else if (scopeType.equals("2")) {
                        for (int i = 0; i < orgList.size(); i++) {
                            scopeString = scopeString + "aaa.createdOrg = " +
                                          orgList.get(i) + " or ";
                        }
                    } else if (scopeType.equals("3")) {
                        scopeString = "aaa.createdOrg = " + orgId + " or ";
                    } else if (scopeType.equals("4")) {
                        if (!scopeScope.equals("")) {

                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                            Query tmpQuery = session.createQuery(orgsql.toString());
                            List tmpOrgList = tmpQuery.list();
                            for (int i = 0; i < tmpOrgList.size(); i++) {
                                scopeString = scopeString + "aaa.createdOrg = " +
                                              tmpOrgList.get(i) + " or ";
                            }
                         }
                        }
                    }
                } else {
                    allScope = true;
                }
            }

            if (!allScope) {
                String orgString = ""; //下级组织ID串
                for (int i = 0; i < orgList.size(); i++) {
                    orgString = orgString + " aaa.channelReaderOrg like '%*" +
                                orgList.get(i) + "*%' or ";
                }

                //取用户所属的组
                String groupString = ""; //组ID串
                query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                            userId);
                tmpList = query.list();
                for (int i = 0; i < tmpList.size(); i++) {
                    groupString = groupString +
                                  " aaa.channelReaderGroup like '%@" +
                                  tmpList.get(i) + "@%' or ";
                }
                hSql = hSql + " where (" + scopeString + orgString +
                       groupString +
                       " aaa.channelReader like '%$" + userId + "$%')  ";
            }
            hSql = " order by bbb.informationIssueTime desc ";

            query = session.createQuery(hSql);
            list = query.list();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return list;
    }

    /**
     * 取信息内容
     * @param informationId String 信息ID
     * @return String
     */
    public String getContent(String informationId) throws Exception {
        String content = "";
        begin();
        try {
            Query query = session.createQuery(
                    " select aaa.informationContent from " +
                    " com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
                    " where aaa.informationId = " + informationId);
            Iterator iter = query.iterate();
            if (iter.hasNext()) {
                Object tmp = iter.next();
                content = tmp == null ? "" : tmp.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return content;
    }

    /**
     * 信息可查看人的条件语句
     * @param userId String 用户ID
     * @param orgId String 组织ID
     * @param orgIdString String 组织ID串
     * @param alias String 别名
     * @throws Exception
     * @return String
     */
    public String getInfoReader(String userId, String orgId, String orgIdString,
                                String alias) throws Exception {
        StringBuffer infoReader = new StringBuffer();
        begin();
        try {
            infoReader.append("((").append(alias).append(
                    ".informationReader is null or ").append(alias).append(
                    ".informationReader='') and ");
            infoReader.append("(").append(alias).append(
                    ".informationReaderOrg is null or ").append(alias).append(
                    ".informationReaderOrg='') and ");
            infoReader.append("(").append(alias).append(
                    ".informationReaderGroup is null or ").append(alias).append(
                    ".informationReaderGroup=''))");

            //用户id在informationReader中
            infoReader.append(" or ").append(alias).append(
                    ".informationReader like '%$").append(userId).append("$%' ");

            //当前用户为发布人
            infoReader.append(" or ").append(alias).append(
                    ".informationIssuerId=").append(userId);

            //取用户所在组织的上级组织（包括本组织）
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where '" + orgIdString +
                        "' like concat('%$', aaa.orgId, '$%')";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),'" + orgIdString + "')>0";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where '" + orgIdString + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%')";
            }

            Query query = session.createQuery(tmpSql);
            List list = query.list();
            //取用户所在组织的上级组织（包括本组织）id在informationReaderOrg中
            for (int i = 0; i < list.size(); i++) {
                infoReader.append(" or " + alias +
                                  ".informationReaderOrg like '%*" +
                                  list.get(i).toString() + "*%'");
            }

            //兼职组织 王国良 2009-10-28
            List sideOrgList = session.createQuery("select eee.sidelineOrg from com.whir.org.vo.usermanager.EmployeeVO eee where eee.empId=" +userId).list();
            if(sideOrgList!=null&&sideOrgList.size()>0){
            	String sideLineOrg = (String) sideOrgList.get(0);
            	if (sideLineOrg != null && !sideLineOrg.equals("") && sideLineOrg.length() > 2) {
            		sideLineOrg = sideLineOrg.substring(1,sideLineOrg.length() - 1);
            		String[] sarr = sideLineOrg.split("\\*\\*");
            		if (sarr != null && sarr.length > 0) {
            			for (int i = 0; i < sarr.length; i++) {
            				infoReader.append(" or " + alias + ".informationReaderOrg like '%*" + sarr[i] + "*%'");
            			}
            		}
            	}
            }

            //取用户所在的组id
            query = session.createQuery(
                    "select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa " +
                    " join aaa.employees bbb where bbb.empId = " + userId);
            list = query.list();
            for (int i = 0; i < list.size(); i++) {
                infoReader.append(" or " + alias +
                                  ".informationReaderGroup like '%@" +
                                  list.get(i).toString() + "@%'");
            }
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return infoReader.toString();
    }

    /**
     * 取相关新闻
     * @param viewCh String 可浏览频道串
     * @param infoId String 信息ID
     * @return List
     */
    public List getAssociateInfo(String orgId, String infoId, String userId,
                                 String orgIdString, String channelType,
                                 String userDefine) throws Exception {
        List list = null;
        String readWhere = getInfoReader(userId, "", orgIdString, "aaa");

        String hSql = getUserViewCh(userId, orgId, channelType, userDefine);
        begin();

        try {
            /*String sql = "select distinct infocc.channelName, inforss.informationId, inforss.informationTitle, inforss.informationSummary, " +
                           "inforss.informationModifyTime, inforss.informationHead, inforss.informationType,infocc.channelId from com.whir.ezoffice.information.infomanager.po.InformationPO " +
                           "inforss join inforss.informationChannel infocc where ((" + readWhere + ") or infocc.channelId in ("+hSql+")) " +
                           "and (inforss.informationKey<>'' and inforss.informationKey = (select ccc.informationKey from com.whir.ezoffice.information.infomanager.po.InformationPO ccc " +
                           "where ccc.informationId = " + infoId + " and ccc.informationStatus=0) or inforss.informationId in (select ddd.associateInfo from " +
                           "com.whir.ezoffice.information.infomanager.po.AssociateInfoPO ddd where ddd.masterInfo = " + infoId + ")) and inforss.informationId <> " +
                           infoId + " order by inforss.informationId desc";*/
            //取domainId
            String domainId = session.createQuery("select po.domainId from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgId=" +
                                                  orgId).iterate().next().
                              toString();

            //取本信息的关键字
            String key = "";
            Iterator it = session.createQuery("select aaa.informationKey from com.whir.ezoffice.information.infomanager.po.InformationPO aaa where aaa.informationId = " +
                                              infoId).iterate();
            if (it.hasNext()) {
                Object obj = it.next();
                if (obj != null) {
                    key = obj.toString();
                }
            }
            StringBuffer buffer = new StringBuffer("select distinct bbb.channelName, aaa.informationId, aaa.informationTitle, aaa.informationSummary,aaa.informationModifyTime, aaa.informationHead, aaa.informationType,bbb.channelId ");
            buffer.append("from com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb ")
                    .append("where aaa.domainId=").append(domainId)
                    .append(" and ((").append(readWhere).append(
                    ") and bbb.channelId in (").append(hSql).append(")) ")
                    .append("and (");
            if (!"".equals(key)) {
                buffer.append("(aaa.informationKey='").append(key).append(
                        "') or");
            }
            buffer.append(" aaa.informationId in (select ddd.associateInfo from com.whir.ezoffice.information.infomanager.po.AssociateInfoPO ddd where ddd.masterInfo = ").
                    append(infoId).append(")")
                    .append(") and aaa.informationId <> ").append(infoId).
                    append(" order by aaa.informationId desc");
            list = session.createQuery(buffer.toString()).list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return list;
    }


    /**
     * 信息发布审核时更新信息数据
     * @param infoId String 信息ID
     * @param field String[] 字段数组
     * @param value String[] 值数组
     */
    public void updateProcInfo(String infoId, List fieldValueList) throws
            Exception {
        begin();
        try {
//            InformationPO infoPO = (InformationPO) session.load(InformationPO.class,
//                    new Long(infoId), LockMode.UPGRADE);
            InformationPO infoPO = (InformationPO) session.load(InformationPO.class,new Long(infoId));
            String[] str = null;
            for (int i = 0; i < fieldValueList.size(); i++) {
                str = (String[]) fieldValueList.get(i);
                if (str[0].equals("informationValidType")) {
                    infoPO.setInformationValidType(Integer.parseInt(str[1]));
                    if (str[1].equals("1")) {
                        String[] str2 = null;
                        for (int j = 0; j < fieldValueList.size(); j++) {
                            str2 = (String[]) fieldValueList.get(j);
                            if (str2[0].equals("validBeginTime"))
                                infoPO.setValidBeginTime(new java.util.Date(
                                        str2[1]));
                            if (str2[0].equals("validEndTime"))
                                infoPO.setValidEndTime(new java.util.Date(str2[
                                        1]));
                        }
                    }
                } else if (str[0].equals("informationTitle")) {
                    infoPO.setInformationTitle(str[1]);
                } else if (str[0].equals("informationSubTitle")) {
                    infoPO.setInformationSubTitle(str[1]);
                } else if (str[0].equals("informationSummary")) {
                    infoPO.setInformationSummary(str[1]);
                } else if (str[0].equals("informationKey")) {
                    infoPO.setInformationKey(str[1]);
                } else if (str[0].equals("informationHead")) {
                    infoPO.setInformationHead(Integer.parseInt(str[1]));
                    String[] str2 = null;
                    for (int j = 0; j < fieldValueList.size(); j++) {
                        str2 = (String[]) fieldValueList.get(j);
                        if (str2[0].equals("informationHeadId"))
                            infoPO.setInformationHeadId(new Long(str2[1]));
                        else if (str2[0].equals("informationSealId"))
                            infoPO.setInformationSealId(new Long(str2[1]));
                        else if (str2[0].equals("informationHeadFile"))
                            infoPO.setInformationHeadFile(str2[1]);
                        else if (str2[0].equals("informationSeal"))
                            infoPO.setInformationSeal(str2[1]);
                        else if (str[0].equals("informationMark"))
                            infoPO.setInformationMark(str[1]);
                        else if (str[0].equals("infoRedIssueTime")) {
                            String[] infoRedIssueTime = str[1].split("/");
                            infoPO.setInfoRedIssueTime(infoRedIssueTime[0] +
                                    "年" + infoRedIssueTime[1] + "月" +
                                    infoRedIssueTime[2] + "日");
                        } else if (str[0].equals("infoRedIssueOrg"))
                            infoPO.setInfoRedIssueOrg(str[1]);
                    }
                } else if (str[0].equals("informationAuthor")) {
                    infoPO.setInformationAuthor(str[1]);
                } else if (str[0].equals("infoAppendName")) {
                    String infoAppendName = str[1], infoAppendSaveName = "",
                            infoPicName = "", infoPicSaveName = "";
                    String[] str2 = null;
                    for (int j = 0; j < fieldValueList.size(); j++) {
                        str2 = (String[]) fieldValueList.get(j);
                        if (str2[0].equals("infoAppendSaveName"))
                            infoAppendSaveName = str2[1];
                        if (str2[0].equals("infoPicName"))
                            infoPicName = str2[1];
                        if (str2[0].equals("infoPicSaveName"))
                            infoPicSaveName = str2[1];
                    }
                    java.util.Set accessorySet = new java.util.HashSet();
                    if (infoAppendName != null && !"".equals(infoAppendName)) {
                        infoAppendName = infoAppendName.substring(1,
                                infoAppendName.length() - 1);
                        infoAppendSaveName = infoAppendSaveName.substring(1,
                                infoAppendSaveName.length() - 1);
                        String[] infoAppendNameArray = {""};
                        String[] infoAppendSaveNameArray = {""};
                        if (infoAppendName.indexOf(",,") > 0) {
                            infoAppendNameArray = infoAppendName.split(",,");
                            infoAppendSaveNameArray = infoAppendSaveName.split(
                                    ",,");
                        } else {
                            infoAppendNameArray[0] = infoAppendName;
                            infoAppendSaveNameArray[0] = infoAppendSaveName;
                        }
                        for (int k = 0; k < infoAppendNameArray.length; k++) {
                            InformationAccessoryPO accePO = new
                                    InformationAccessoryPO();
                            accePO.setAccessoryIsImage(0);
                            accePO.setAccessoryName(infoAppendNameArray[k]);
                            accePO.setAccessorySaveName(infoAppendSaveNameArray[
                                    k]);
                            accePO.setAccessoryType(infoAppendSaveNameArray[k].
                                    substring(infoAppendSaveNameArray[k].
                                              indexOf(".") + 1));
                            accePO.setInformation(infoPO);
                            session.save(accePO);
                            accessorySet.add(accePO);
                        }
                    }
                    if (infoPicName != null && !"".equals(infoPicName)) {
                        infoPicName = infoPicName.substring(1,
                                infoPicName.length() - 1);
                        infoPicSaveName = infoPicSaveName.substring(1,
                                infoPicSaveName.length() - 1);
                        String[] infoPicNameArray = {""};
                        String[] infoPicSaveNameArray = {""};
                        if (infoPicName.indexOf(",,") > 0) {
                            infoPicNameArray = infoPicName.split(",,");
                            infoPicSaveNameArray = infoPicSaveName.split(",,");
                        } else {
                            infoPicNameArray[0] = infoPicName;
                            infoPicSaveNameArray[0] = infoPicSaveName;
                        }

                        for (int k = 0; k < infoPicNameArray.length; k++) {
                            InformationAccessoryPO accePO = new
                                    InformationAccessoryPO();
                            accePO.setAccessoryIsImage(1);
                            accePO.setAccessoryName(infoPicNameArray[k]);
                            accePO.setAccessorySaveName(infoPicSaveNameArray[k]);
                            accePO.setAccessoryType(infoPicSaveNameArray[k].
                                    substring(infoPicSaveNameArray[k].indexOf(
                                    ".") + 1));
                            accePO.setInformation(infoPO);
                            session.save(accePO);
                            accessorySet.add(accePO);
                        }
                    }
                    infoPO.setInformationAccessory(accessorySet);

                } else if (str[0].equals("informationContent")) {
                    infoPO.setInformationContent(str[1]);
                } else if (str[0].equals("informationReaderName")) {
                    infoPO.setInformationReaderName(str[1]);
                    String[] str2 = null;
                    for (int j = 0; j < fieldValueList.size(); j++) {
                        str2 = (String[]) fieldValueList.get(j);
                        if (str2[0].equals("informationReader"))
                            infoPO.setInformationReader(str2[1]);
                        if (str2[0].equals("informationReaderOrg"))
                            infoPO.setInformationReaderOrg(str2[1]);
                        if (str2[0].equals("informationReaderGroup"))
                            infoPO.setInformationReaderGroup(str2[1]);
                    }
                } else if (str[0].equals("forbidCopy")) {
                    infoPO.setForbidCopy(Integer.parseInt(str[1]));
                } else if (str[0].equals("transmitToEzsite")) {
                    infoPO.setTransmitToEzsite(Integer.parseInt(str[1]));
                } else if (str[0].equals("orderCode")) {
                    infoPO.setOrderCode(str[1]);
                } else if (str[0].equals("associateInfo")) {
                    /*String associateInfo = str[1];
                     if(associateInfo != null && !associateInfo.equals("")){
                        String[] assoInfo = {""};
                     associateInfo = associateInfo.substring(1, associateInfo.length() - 1);
                        if(associateInfo.indexOf(",") >= 0){
                            assoInfo = associateInfo.split(",,");
                        }else{
                            assoInfo[0] = associateInfo;
                        }
                        session.delete("from com.whir.ezoffice.information.infomanager.po.AssociateInfoPO aaa  " +
                                       "where aaa.masterInfo = " + infoId);
                        for(int j = 0; j < assoInfo.length; j ++){
                            AssociateInfoPO assoPO = new AssociateInfoPO();
                            assoPO.setAssociateInfo(new Long(assoInfo[j]));
                            assoPO.setMasterInfo(new Long(infoId));
                            session.save(assoPO);
                        }
                                         }*/
                }
            }
            session.flush();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
        } finally {
            session.close();
            session = null;
            transaction = null;
        }

    }

    /**
     * 取信息的附件类型
     * @param infoId String
     * @throws Exception
     * @return String
     */
    public String getAccessoryType(String infoId) throws Exception {
        String aType = "";
        begin();
        try {
            Query query = session.createQuery("select aaa.accessoryType from com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO aaa join aaa.information bbb where bbb.informationId=" +
                                              infoId + " order by accessory_id");
            java.util.Iterator itor = query.iterate();
            if (itor.hasNext()) {
                aType = (String) itor.next();
            }
        } catch (Exception e) {
            System.out.println(
                    "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println(e.getMessage());
            System.out.println(
                    "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return aType;
    }

    public Long save(InformationPO informationPO,
                     String[] para,
                     String[] assoInfo,
                     String[] infoPicName,
                     String[] infoPicSaveName,
                     String[] infoAppendName,
                     String[] infoAppendSaveName, String domainId) throws
            Exception {
        begin();
        Long informationId = null;
        try {
            //保存信息数据
            informationId = (Long) session.save(informationPO);
            if (infoPicName != null) {
                for (int i = 0; i < infoPicName.length; i++) {
                    InformationAccessoryPO informationAccessoryPO = new
                            InformationAccessoryPO();
                    informationAccessoryPO.setAccessoryName(infoPicName[i]);
                    informationAccessoryPO.setAccessorySaveName(infoPicSaveName[
                            i]);
                    informationAccessoryPO.setAccessoryIsImage(1);
                    if (infoPicSaveName[i].indexOf(".") > 0) {
                        informationAccessoryPO.setAccessoryType(infoPicSaveName[
                                i].substring(infoPicSaveName[i].indexOf(".") +
                                             1, infoPicSaveName[i].length()));
                    } else {
                        informationAccessoryPO.setAccessoryType("");
                    }
                    informationAccessoryPO.setInformation(informationPO);
                    informationAccessoryPO.setDomainId(Long.valueOf(domainId));
                    session.save(informationAccessoryPO);
                }
            }
            if (infoAppendName != null) {
                for (int i = 0; i < infoAppendName.length; i++) {
                    InformationAccessoryPO informationAccessoryPO = new
                            InformationAccessoryPO();
                    informationAccessoryPO.setAccessoryName(infoAppendName[i]);
                    informationAccessoryPO.setAccessorySaveName(
                            infoAppendSaveName[i]);
                    informationAccessoryPO.setAccessoryIsImage(0);

                    if (infoAppendSaveName[i].indexOf(".") > 0) {
                        informationAccessoryPO.setAccessoryType(
                                infoAppendSaveName[i].substring(
                                infoAppendSaveName[i].indexOf(".") + 1,
                                infoAppendSaveName[i].length()));
                    } else {
                        informationAccessoryPO.setAccessoryType("");
                    }
                    informationAccessoryPO.setInformation(informationPO);
                    informationAccessoryPO.setDomainId(Long.valueOf(domainId));
                    session.save(informationAccessoryPO);
                }
            }
            //相关信息
            if (assoInfo != null) {
                for (int j = 0; j < assoInfo.length; j++) {
                    AssociateInfoPO assoPO = new AssociateInfoPO();
                    assoPO.setMasterInfo(informationId);
                    assoPO.setAssociateInfo(new Long(assoInfo[j]));
                    assoPO.setDomainId(Long.valueOf(domainId));
                    session.save(assoPO);
                }
            }
            //保存统计信息
            String userId = para[0];
            String userName = para[1];
            String orgId = para[2];
            String orgName = para[3];
            String orgIdString = para[4];
            java.util.Date now = new java.util.Date();
            String afficheHisDate = "-1";
            String informationOrISODoc = "0";
            if (informationPO.getInformationOrISODoc() != null) {
                informationOrISODoc = "" + informationPO.getInformationOrISODoc();
            }
            if (informationPO.getAfficeHistoryDate() != null) {
                afficheHisDate = "" + informationPO.getAfficeHistoryDate();
            }
            if (afficheHisDate.equals("-1") && informationOrISODoc.equals("0")) { // 是信息才统计  是公告不统计 是 iso 文档也不统计
                //保存个人统计
                long maxCount = 0;
                Query query = session.createQuery(
                        " select max(aaa.accumulateNum) from " +
                        " com.whir.ezoffice.information.infomanager.po.InforPersonalStatPO aaa " +
                        " where aaa.empId = " + userId);

                List list = query.list();
                if (list != null && list.size() > 0 && list.get(0) != null) {
                    maxCount = ((Long) list.get(0)).longValue();
                }

                query = session.createQuery(" select aaa from com.whir.ezoffice.information.infomanager.po.InforPersonalStatPO aaa " +
                                            " where aaa.empId = " + userId +
                                            " and aaa.statYear = " +
                                            (now.getYear() + 1900) +
                                            " and statMonth = " +
                                            (now.getMonth() + 1));
                list = query.list();
                InforPersonalStatPO inforPersonalStatPO = new InforPersonalStatPO();
                if (list != null && list.size() > 0 && list.get(0) != null) {
                    inforPersonalStatPO = (InforPersonalStatPO) list.get(0);
                    int monthIssueNum = inforPersonalStatPO.getMonthIssueNum();
                    inforPersonalStatPO.setAccumulateNum(new Long(maxCount + 1));
                    inforPersonalStatPO.setMonthIssueNum(monthIssueNum + 1);
                    inforPersonalStatPO.setEmpName(userName);
                    inforPersonalStatPO.setOrgId(new Long(orgId));
                    inforPersonalStatPO.setOrgIdString(orgIdString);
                    inforPersonalStatPO.setOrgName(orgName);
                    inforPersonalStatPO.setDomainId(Long.valueOf(domainId));
                } else {
                    inforPersonalStatPO.setAccumulateNum(new Long(maxCount + 1));
                    inforPersonalStatPO.setEmpId(new Long(userId));
                    inforPersonalStatPO.setEmpName(userName);
                    inforPersonalStatPO.setMonthIssueNum(1);
                    inforPersonalStatPO.setOrgId(new Long(orgId));
                    inforPersonalStatPO.setOrgIdString(orgIdString);
                    inforPersonalStatPO.setOrgName(orgName);
                    inforPersonalStatPO.setStatMonth(now.getMonth() + 1);
                    inforPersonalStatPO.setStatYear(now.getYear() + 1900);
                    inforPersonalStatPO.setDomainId(Long.valueOf(domainId));
                    session.save(inforPersonalStatPO);
                }
                //保存组织统计
                String tmpSql = "";
                String databaseType = com.whir.common.config.SystemCommon.
                                      getDatabaseType();
                if (databaseType.indexOf("mysql") >= 0) {
                    tmpSql =
                            " select aaa.orgId,aaa.orgName,aaa.orgIdString,aaa.orgLevel,aaa.orgNameString " +
                            " from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                            " where '" + orgIdString +
                            "' like concat('%$', aaa.orgId, '$%') ";
                }else if (databaseType.indexOf("db2") >= 0) {
                    tmpSql =
                           " select aaa.orgId,aaa.orgName,aaa.orgIdString,aaa.orgLevel,aaa.orgNameString " +
                           " from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                           " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),'" + orgIdString + "')>0";

                } else {
                    tmpSql =
                            " select aaa.orgId,aaa.orgName,aaa.orgIdString,aaa.orgLevel,aaa.orgNameString " +
                            " from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                            " where '" + orgIdString + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
                }

                query = session.createQuery(tmpSql);
                list = query.list();

                for (int i = 0; i < list.size(); i++) {

                    Object[] obj = (Object[]) list.get(i);

                    query = session.createQuery(
                            " select max(aaa.accumulateNum) from " +
                            " com.whir.ezoffice.information.infomanager.po.InforOrgStatPO aaa " +
                            " where aaa.orgId = " + obj[0]);

                    List tmpList = query.list();

                    maxCount = 0;
                    if (tmpList != null && tmpList.size() > 0 && tmpList.get(0) != null) {
                        maxCount = ((Long) tmpList.get(0)).longValue();
                    }
                    query = session.createQuery(" select aaa from " +
                                                " com.whir.ezoffice.information.infomanager.po.InforOrgStatPO aaa " +
                                                " where aaa.orgId = " + obj[0] +
                                                " and aaa.statYear = " +
                                                (now.getYear() + 1900) +
                                                " and aaa.statMonth = " +
                                                (now.getMonth() + 1));
                    tmpList = query.list();
                    if (tmpList != null && tmpList.size() > 0 && tmpList.get(0) != null) {
                        InforOrgStatPO inforOrgStatPO = (InforOrgStatPO)
                                tmpList.get(0);
                        inforOrgStatPO.setOrgIdString(obj[2].toString());
                        inforOrgStatPO.setOrgName(obj[4].toString());
                        inforOrgStatPO.setOrgLevel(Integer.parseInt(obj[3].
                                toString()));
                        inforOrgStatPO.setMonthIssueNum(inforOrgStatPO.
                                getMonthIssueNum() + 1);
                        inforOrgStatPO.setAccumulateNum(new Long(maxCount + 1));
                        inforOrgStatPO.setDomainId(Long.valueOf(domainId));
                    } else {
                        InforOrgStatPO inforOrgStatPO = new InforOrgStatPO();
                        inforOrgStatPO.setOrgId(new Long(obj[0].toString()));
                        inforOrgStatPO.setOrgIdString(obj[2].toString());
                        inforOrgStatPO.setOrgName(obj[4].toString());
                        inforOrgStatPO.setStatMonth(now.getMonth() + 1);
                        inforOrgStatPO.setStatYear(now.getYear() + 1900);
                        inforOrgStatPO.setOrgLevel(Integer.parseInt(obj[3].
                                toString()));
                        inforOrgStatPO.setMonthIssueNum(1);
                        inforOrgStatPO.setAccumulateNum(new Long(maxCount + 1));
                        inforOrgStatPO.setDomainId(Long.valueOf(domainId));
                        session.save(inforOrgStatPO);
                    }
                }
            }
            //informationPO.setOrderCode((informationId.longValue() * 100) + "");
            informationPO.setOrderCode("1000");
            session.flush();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        //--处理信息查看权限
        //保存信息草稿(类型9-草稿)不处理，栏目可能为空
        if(!(informationPO.getInformationStatus() == new Integer(9))){
            SaveInfoReader saveInfoReader = SaveInfoReader.getInstance();
            saveInfoReader.save(informationPO.getInformationChannel().getChannelId().
                                toString(), informationPO.getOtherChannel(),
                                informationId.toString(),
                                informationPO.getInformationReader(),
                                informationPO.getInformationReaderOrg(),
                                informationPO.getInformationReaderGroup());
        }
        return informationId;
    }


    /**
     * 取信息的未浏览用户
     * @param informationId String
     * @throws Exception
     * @return List
     */
    public List getNotBrowser(String informationId, String searchName,
                              String domainId) throws Exception {
        begin();
        List list = null;
        try {
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            String informationReader = "", informationReaderOrg = "",
                    informationReaderGroup = "", channelReader = "",
                    channelReaderOrg = "", channelReaderGroup = "";
            Iterator iter = session.iterate("select info.informationReader, info.informationReaderOrg, info.informationReaderGroup, " +
                                            "ch.channelReader, ch.channelReaderOrg, ch.channelReaderGroup from " +
                                            "com.whir.ezoffice.information.infomanager.po.InformationPO info join " +
                                            "info.informationChannel ch where info.informationId = " +
                                            informationId);
            Object[] obj = null;
            if (iter.hasNext()) {
                obj = (Object[]) iter.next();
                informationReader = obj[0] == null ? "" : obj[0].toString();
                informationReaderOrg = obj[1] == null ? "" : obj[1].toString();
                informationReaderGroup = obj[2] == null ? "" : obj[2].toString();
                channelReader = obj[3] == null ? "" : obj[3].toString();
                channelReaderOrg = obj[4] == null ? "" : obj[4].toString();
                channelReaderGroup = obj[5] == null ? "" : obj[5].toString();
            }

            //栏目的可查看人
            String chWhere = "select emp.empId from com.whir.org.vo.usermanager.EmployeeVO emp join emp.organizations org where 1=1 and org.domainId=" +
                             domainId + " ";
            if (channelReader.equals("") && channelReaderOrg.equals("") &&
                channelReaderGroup.equals("")) {
                //栏目未指定可查看人
                chWhere += " and 1=1 ";
            } else {
                chWhere += " and ( 1<>1 ";
                //栏目指定可查看人
                if (!channelReader.equals("")) {
                    //可查看用户
//                    System.out.println("栏目可查看人");
//                    System.out.println(" or EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp.empId)), '$%') like '" + channelReader + "' ");
                    if (databaseType.indexOf("mysql") >= 0) {
                        chWhere += " or '" + channelReader +
                                "' like concat('%$', emp.empId, '$%') ";
                    }else if (databaseType.indexOf("db2") >= 0) {
                        chWhere += " or locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(emp.empId)), '$'),'" + channelReader + "')>0";
                    } else {
                        chWhere += " or '" + channelReader + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp.empId)), '$%') ";
                    }

                }
                if (!channelReaderOrg.equals("")) {
                    //可查看组织
//                    System.out.println("栏目可查看组织");
//                    System.out.println(" or org.orgId in (select a.orgId from com.whir.org.vo.organizationmanager.OrganizationVO a, " +
//                                       "com.whir.org.vo.organizationmanager.OrganizationVO b where a.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(b.orgId)), '$%') and '" + channelReaderOrg + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(b.orgId)), '*%'))");
                    if (databaseType.indexOf("mysql") >= 0) {
                        chWhere += " or org.orgId in (select a.orgId from com.whir.org.vo.organizationmanager.OrganizationVO a, " +
                                "com.whir.org.vo.organizationmanager.OrganizationVO b where a.orgIdString like concat('%$', b.orgId, '$%') and '" +
                                channelReaderOrg +
                                "' like concat('%*', b.orgId, '*%'))";
                    }else  if (databaseType.indexOf("db2") >= 0) {
                        chWhere += " or org.orgId in (select a.orgId from com.whir.org.vo.organizationmanager.OrganizationVO a, " +
                                "com.whir.org.vo.organizationmanager.OrganizationVO b where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(b.orgId)), '$'),a.orgIdString)>0 and locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('*', EZOFFICE.FN_INTTOSTR(b.orgId)), '*'),'" +
                                channelReaderOrg + "')>0)";

                    } else {
                        chWhere += " or org.orgId in (select a.orgId from com.whir.org.vo.organizationmanager.OrganizationVO a, " +
                                "com.whir.org.vo.organizationmanager.OrganizationVO b where a.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(b.orgId)), '$%') and '" +
                                channelReaderOrg + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(b.orgId)), '*%'))";
                    }

                }
                if (!channelReaderGroup.equals("")) {
                    //可查看组
//                    System.out.println("栏目可查看组");
//                    System.out.println(" or emp.empId in (select groupEmp.empId from com.whir.org.vo.usermanager.EmployeeVO groupEmp join groupEmp.groups group2 where '" + channelReaderGroup + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%@', EZOFFICE.FN_INTTOSTR(group2.groupId)), '@%') )");
                    if (databaseType.indexOf("mysql") >= 0) {
                        chWhere += " or emp.empId in (select groupEmp.empId from com.whir.org.vo.usermanager.EmployeeVO groupEmp join groupEmp.groups group2 where '" +
                                channelReaderGroup +
                                "' like concat('%@', group2.groupId, '@%') )";
                    }else if (databaseType.indexOf("db2") >= 0) {
                        chWhere += " or emp.empId in (select groupEmp.empId from com.whir.org.vo.usermanager.EmployeeVO groupEmp join groupEmp.groups group2 where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('@', EZOFFICE.FN_INTTOSTR(group2.groupId)), '@'),'" +
                                channelReaderGroup + "')>0 )";
                    } else {
                        chWhere += " or emp.empId in (select groupEmp.empId from com.whir.org.vo.usermanager.EmployeeVO groupEmp join groupEmp.groups group2 where '" +
                                channelReaderGroup + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%@', EZOFFICE.FN_INTTOSTR(group2.groupId)), '@%') )";
                    }

                }
                chWhere += " ) ";
            }

            //信息查看人
            String infoWhere = "select emp.empId from com.whir.org.vo.usermanager.EmployeeVO emp join emp.organizations org where 1=1 ";
            if (informationReader.equals("") && informationReaderOrg.equals("") &&
                informationReaderGroup.equals("")) {
                //栏目未指定可查看人
                infoWhere += " and 1=1 ";
            } else {
                infoWhere += " and ( 1<>1 ";
                //栏目指定可查看人
                if (!informationReader.equals("")) {
                    //可查看用户
                    if (databaseType.indexOf("mysql") >= 0) {
                        infoWhere += " or '" + informationReader +
                                "' like concat('%$', emp.empId, '$%') ";
                    }else if (databaseType.indexOf("db2") >= 0) {
                        infoWhere += " or locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(emp.empId)), '$'),'" + informationReader + "')>0 ";
                    } else {
                        infoWhere += " or '" + informationReader + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp.empId)), '$%') ";
                    }

                }
                if (!informationReaderOrg.equals("")) {
                    //可查看组织
                    if (databaseType.indexOf("mysql") >= 0) {
                        infoWhere += " or org.orgId in (select a.orgId from com.whir.org.vo.organizationmanager.OrganizationVO a, " +
                                "com.whir.org.vo.organizationmanager.OrganizationVO b where a.orgIdString like concat('%$', b.orgId, '$%') and '" +
                                informationReaderOrg +
                                "' like concat('%*', b.orgId, '*%'))";
                    }else if (databaseType.indexOf("db2") >= 0) {
                        infoWhere += " or org.orgId in (select a.orgId from com.whir.org.vo.organizationmanager.OrganizationVO a, " +
                                "com.whir.org.vo.organizationmanager.OrganizationVO b where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(b.orgId)), '$'),a.orgIdString)>0 and locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(b.orgId)), '*%'),'" +
                                informationReaderOrg + "')>0)";

                    } else {
                        infoWhere += " or org.orgId in (select a.orgId from com.whir.org.vo.organizationmanager.OrganizationVO a, " +
                                "com.whir.org.vo.organizationmanager.OrganizationVO b where a.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(b.orgId)), '$%') and '" +
                                informationReaderOrg + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(b.orgId)), '*%'))";
                    }

                }
                if (!informationReaderGroup.equals("")) {
                    //可查看组
                    if (databaseType.indexOf("mysql") >= 0) {
                        infoWhere += " or emp.empId in (select groupEmp.empId from com.whir.org.vo.usermanager.EmployeeVO groupEmp join groupEmp.groups group2 where '" +
                                informationReaderGroup +
                                "' like concat('%@', group2.groupId, '@%') )";
                    }else if (databaseType.indexOf("db2") >= 0) {
                        infoWhere += " or emp.empId in (select groupEmp.empId from com.whir.org.vo.usermanager.EmployeeVO groupEmp join groupEmp.groups group2 where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('@', EZOFFICE.FN_INTTOSTR(group2.groupId)), '@'),'" +
                                informationReaderGroup + "')>0)";
                    } else {
                        infoWhere += " or emp.empId in (select groupEmp.empId from com.whir.org.vo.usermanager.EmployeeVO groupEmp join groupEmp.groups group2 where '" +
                                informationReaderGroup + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%@', EZOFFICE.FN_INTTOSTR(group2.groupId)), '@%') )";
                    }

                }
                infoWhere += " ) ";
            }
            String sql = "";
            if (databaseType.indexOf("mysql") >= 0) {
                sql = "select empVO.empName, orgVO.orgNameString, empVO.userAccounts from com.whir.org.vo.usermanager.EmployeeVO empVO join empVO.organizations orgVO where empVO.empId in (" +
                      chWhere + ") " +
                      "and empVO.empId in (" + infoWhere + ") and " +
                      "empVO.empId not in (select ib.empId from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO ib join ib.information info where info.informationId=" +
                      informationId + ") and " +
                      "empVO.empName like concat('%', '" + searchName +
                      "', '%') and " +
                      "empVO.userIsDeleted=0 and empVO.userIsActive=1 order by orgVO.orgIdString";
            }else if (databaseType.indexOf("db2") >= 0) {
                sql = "select empVO.empName, orgVO.orgNameString, empVO.userAccounts from com.whir.org.vo.usermanager.EmployeeVO empVO join empVO.organizations orgVO where empVO.empId in (" +
                      chWhere + ") " +
                      "and empVO.empId in (" + infoWhere + ") and " +
                      "empVO.empId not in (select ib.empId from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO ib join ib.information info where info.informationId=" +
                      informationId + ") and " +
                      "empVO.empName like '%" +
                       searchName + "%') and " +
                       "empVO.userIsDeleted=0 and empVO.userIsActive=1 order by orgVO.orgIdString";


            } else {
                sql = "select empVO.empName, orgVO.orgNameString, empVO.userAccounts from com.whir.org.vo.usermanager.EmployeeVO empVO join empVO.organizations orgVO where empVO.empId in (" +
                      chWhere + ") " +
                      "and empVO.empId in (" + infoWhere + ") and " +
                      "empVO.empId not in (select ib.empId from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO ib join ib.information info where info.informationId=" +
                      informationId + ") and " +
                      "empVO.empName like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%', '" +
                      searchName + "'), '%') and " +
                      "empVO.userIsDeleted=0 and empVO.userIsActive=1 order by orgVO.orgIdString";

            }

            list = session.createQuery(sql).list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return list;
    }

    /**
     * 取一个用户发布信息的数量
     * @param userId String
     * @throws Exception
     * @return Integer
     */
    public Integer getUserIssueInfoCount(String userId) throws Exception {
        begin();
        Integer count = Integer.valueOf("0");
        try {
            Iterator iter = session.iterate("select count(info.informationId) from com.whir.ezoffice.information.infomanager.po.InformationPO info where info.informationIssuerId=" +
                                            userId);
            if (iter.hasNext()) {
                count = (Integer) iter.next();
            }
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return count;
    }

    public Integer setDossierStatus(String informationId, String dossierStatus) throws
            Exception {
        begin();
        Integer result = Integer.valueOf("0");
        try {
            java.sql.Connection conn = session.connection();
            java.sql.Statement stat = conn.createStatement();
            stat.execute("update EZOFFICE.OA_INFORMATION SET DOSSIERSTATUS=" +
                         dossierStatus + " WHERE INFORMATION_ID = " +
                         informationId);
            stat.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return result;
    }

    public Boolean vindicateInfo(String userId, String orgId,
                                 String informationId) throws Exception {
        Boolean result = new Boolean(false);
        begin();
        try {
            //取用户权限范围
            String rightCode = "", channelId = "";
            java.util.Iterator iter = session.iterate("select a.channelType, a.userDefine, a.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO a join a.information b where b.informationId = " +
                    informationId);
            if (iter.hasNext()) {
                Object[] obj = (Object[]) iter.next();
                if (obj[0].toString().equals("0") ||
                    obj[1].toString().equals("1")) {
                    //栏目维护权限由信息管理设置代替
                    //rightCode = "01*03*02";
                    rightCode = "01*02*01";
                } else {
                    rightCode = "01*01*02";
                }
                channelId = obj[2].toString();

                Query query = session.createQuery(
                        " select aaa.rightScopeType,aaa.rightScopeScope from " +
                        " com.whir.org.vo.rolemanager.RightScopeVO aaa join " +
                        " aaa.right bbb join aaa.employee ccc where bbb.rightCode='" +
                        rightCode + "' and ccc.empId = " + userId);
                List tmpList = query.list();
                boolean hasAllScope = false;
                String scopeWhereSql = "";
                if (tmpList != null && tmpList.size() > 0 && tmpList.get(0) != null) {
                    obj = (Object[]) tmpList.get(0);
                    String scopeType = obj[0].toString();
                    String scopeScope = "";
                    if (obj[1] != null) {
                        scopeScope = obj[1].toString();
                    }
                    if (scopeType.equals("0")) {
                        //权限范围是全部
                        hasAllScope = true;
                    } else if (scopeType.equals("1")) {
                        //权限范围是本人
                        scopeWhereSql = " aaa.createdEmp = " + userId + " or ";
                    } else if (scopeType.equals("2")) {
                        //权限范围是本组织及下属组织
                        query = session.createQuery(
                                " select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                                " where aaa.orgIdString like '%$" + orgId + "$%'");
                        tmpList = query.list();
                        for (int i = 0; i < tmpList.size(); i++) {
                            scopeWhereSql = scopeWhereSql + " aaa.createdOrg = " +
                                            tmpList.get(i) + " or ";
                        }
                    } else if (scopeType.equals("3")) {
                        //权限范围是本组织
                        scopeWhereSql = "aaa.createdOrg = " + orgId + " or ";
                    } else if (scopeType.equals("4")) {
                        if (scopeScope != null && !scopeScope.equals("")) {
                            scopeScope = scopeScope.substring(1,
                                    scopeScope.length() - 1);

                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");


                            query = session.createQuery(orgsql.toString());
                            tmpList = query.list();
                            for (int i = 0; i < tmpList.size(); i++) {
                                scopeWhereSql = scopeWhereSql +
                                                " aaa.createdOrg = " +
                                                tmpList.get(i) + " or ";
                            }
                        }
                     }
                    }
                }

                if (hasAllScope) {
                    result = Boolean.TRUE;
                } else {
                    if (scopeWhereSql.endsWith("or ")) {
                        scopeWhereSql = scopeWhereSql.substring(0,
                                scopeWhereSql.length() - 3);
                    }
                    if(scopeWhereSql==null||scopeWhereSql.equals("")||scopeWhereSql.trim().equals("")){
                     result = Boolean.TRUE;
                    }else{
                    query = session.createQuery(
                            " select count(aaa.channelId) from " +
                            " com.whir.ezoffice.information.channelmanager.po.InformationChannelPO aaa where " +
                            scopeWhereSql + " and aaa.channelId=" + channelId);
                    int count = ((Integer) query.iterate().next()).intValue();
                    if (count > 0) {
                        result = Boolean.TRUE;
                    }
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
        return result;
    }

    public String getInfoUserdefine(String informationId) throws Exception {
        String userDefine = "";
        begin();
        try {
            java.util.Iterator iter = session.iterate("select a.userDefine from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO a join a.information b where b.informationId=" +
                    informationId);
            if (iter.hasNext()) {
                userDefine = iter.next().toString();
            }
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return userDefine;
    }

    /**
     * 取未看过的必读信息数量
     * @param userIds String
     * @throws Exception
     * @return Map
     */
    public Map getMustReadCount(String userIds) throws Exception {
        Map mustReadMap = new HashMap();
        begin();
        try {
            StringBuffer sql = new StringBuffer("select emp1.empId, count(distinct info1.informationId) from com.whir.ezoffice.information.infomanager.po.InformationPO info1 join info1.informationChannel ch1, com.whir.org.vo.usermanager.EmployeeVO emp1 join emp1.organizations org1 left join emp1.groups gp1, com.whir.org.vo.organizationmanager.OrganizationVO org2 where org1.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(org2.orgId)), '$%') and info1.mustRead=1 and emp1.empId in (" +
                                                userIds + ") and ( ");
            //1.栏目的可查看人、可查看组织、可查看组、信息的可查看人、可查看组织、可查看组全部为空
            sql.append("((info1.informationReader is null or info1.informationReader='') and (info1.informationReaderOrg is null or info1.informationReaderOrg='') and (info1.informationReaderGroup is null or info1.informationReaderGroup='') and (ch1.channelReader is null or ch1.channelReader='') and (ch1.channelReaderOrg is null or ch1.channelReaderOrg='') and (ch1.channelReaderGroup is null or ch1.channelReaderGroup=''))");
            sql.append(" or ");
            //2.栏目的可查看人
            sql.append("(ch1.channelReader like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp1.empId)), '$%'))");
            sql.append(" or ");
            //3.栏目的可查看组织
            sql.append("(ch1.channelReaderOrg like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(org2.orgId)), '*%'))");
            sql.append(" or ");
            //4.栏目的可查看组
            sql.append("(ch1.channelReaderGroup like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%@', EZOFFICE.FN_INTTOSTR(gp1.groupId)), '@%'))");
            sql.append(" or ");
            //5.信息的可查看人
            sql.append("(info1.informationReader like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp1.empId)), '$%'))");
            sql.append(" or ");
            //6.信息的可查看组织
            sql.append("(info1.informationReaderOrg like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(org2.orgId)), '*%'))");
            sql.append(" or ");
            //7.信息的可查看组
            sql.append("(info1.informationReaderGroup like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%@', EZOFFICE.FN_INTTOSTR(gp1.groupId)), '@%'))");
            sql.append(") ");
            sql.append(" and info1.informationId not in (select info2.informationId from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO ib join ib.information info2 where ib.empId in (" +
                       userIds + "))");
            sql.append(" and info1.informationStatus=0 group by emp1.empId");
            Iterator iter = session.iterate(sql.toString());
            Object[] obj = null;
            while (iter.hasNext()) {
                obj = (Object[]) iter.next();
                mustReadMap.put(obj[0].toString(), obj[1]);
            }
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
        }
        return mustReadMap;
    }

    /**
     * 取未看过的必读信息
     * @param userIds String
     * @throws Exception
     * @return List
     */
    public List getMustReadInfo(String userIds, String domainId) throws
            Exception {
        List mustReadInfo = new ArrayList();
        begin();
        try {
            StringBuffer sql = new StringBuffer("select distinct ch1.userDefine,ch1.channelType,ch1.channelIdString,info1.informationId,info1.informationTitle,info1.titleColor,info1.informationKits,info1.informationIssueOrg, info1.informationIssuer, info1.informationVersion,info1.informationIssueTime,info1.informationCommonNum,info1.informationHead,info1.informationType from com.whir.ezoffice.information.infomanager.po.InformationPO info1 join info1.informationChannel ch1, com.whir.org.vo.usermanager.EmployeeVO emp1 join emp1.organizations org1 left join emp1.groups gp1, com.whir.org.vo.organizationmanager.OrganizationVO org2 where org1.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(org2.orgId)), '$%') and info1.mustRead=1 and info1.domainId=" +
                                                domainId +
                                                " and emp1.empId in (" +
                                                userIds + ") and ( ");
            //1.栏目的可查看人、可查看组织、可查看组、信息的可查看人、可查看组织、可查看组全部为空
            sql.append("((info1.informationReader is null or info1.informationReader='') and (info1.informationReaderOrg is null or info1.informationReaderOrg='') and (info1.informationReaderGroup is null or info1.informationReaderGroup='') and (ch1.channelReader is null or ch1.channelReader='') and (ch1.channelReaderOrg is null or ch1.channelReaderOrg='') and (ch1.channelReaderGroup is null or ch1.channelReaderGroup=''))");
            sql.append(" or ");
            //2.栏目的可查看人
            sql.append("(ch1.channelReader like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp1.empId)), '$%'))");
            sql.append(" or ");
            //3.栏目的可查看组织
            sql.append("(ch1.channelReaderOrg like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(org2.orgId)), '*%'))");
            sql.append(" or ");
            //4.栏目的可查看组
            sql.append("(ch1.channelReaderGroup like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%@', EZOFFICE.FN_INTTOSTR(gp1.groupId)), '@%'))");
            sql.append(" or ");
            //5.信息的可查看人
            sql.append("(info1.informationReader like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(emp1.empId)), '$%'))");
            sql.append(" or ");
            //6.信息的可查看组织
            sql.append("(info1.informationReaderOrg like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*', EZOFFICE.FN_INTTOSTR(org2.orgId)), '*%'))");
            sql.append(" or ");
            //7.信息的可查看组
            sql.append("(info1.informationReaderGroup like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%@', EZOFFICE.FN_INTTOSTR(gp1.groupId)), '@%'))");
            sql.append(") ");
            sql.append(" and info1.informationStatus=0 and info1.informationId not in (select info2.informationId from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO ib join ib.information info2 where ib.empId=emp1.empId) order by info1.informationIssueTime");

            Iterator iter = session.iterate(sql.toString());
            Iterator iter2 = null;
            Object[] obj = null;
            String userDefine = "0", channelType = "0", channelIdString = "",
                    informationTitle = "", titleColor = "";
            StringBuffer title = new StringBuffer();
            while (iter.hasNext()) {
                title.append("[");
                obj = (Object[]) iter.next();
                userDefine = obj[0] == null ? "0" : obj[0].toString();
                channelType = obj[1] == null ? "0" : obj[1].toString();
                if (userDefine.equals("0")) {
                    //知识管理或部门主页
                    if (channelType.equals("0")) {
                        //知识管理
                        iter2 = session.iterate("select menu.menuName from com.whir.org.menu.po.MenuSetPO menu where menu.menuCode='information'");
                    } else {
                        //部门主页
                        iter2 = session.iterate("select org.orgName from com.whir.org.vo.organizationmanager.OrganizationVO org where org.orgId=" +
                                                channelType);
                    }
                } else {
                    //自定义内容
                    iter2 = session.iterate("select a.userChannelName from com.whir.ezoffice.information.channelmanager.po.UserChannelPO a where a.userChannelId=" +
                                            channelType);
                }
                if (iter2.hasNext()) {
                    title.append(iter2.next() + ".");
                }
                channelIdString = obj[2] == null ? "" : obj[2].toString();
                iter2 = session.iterate("select ch.channelName from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO ch where '" +
                                        channelIdString + "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(ch.channelId)), '$%')");
                while (iter2.hasNext()) {
                    title.append(iter2.next() + ".");
                }
                title.deleteCharAt(title.length() - 1);
                title.append("]");
                titleColor = obj[5] == null ? "0" : obj[5].toString();
                if (titleColor.equals("1")) {
                    title.append("<font color=red>");
                }
                title.append(obj[4]);
                if (titleColor.equals("1")) {
                    title.append("</font>");
                }
                Object[] newObj = new Object[10];
                newObj[0] = obj[3]; //信息ID
                newObj[1] = title.toString(); //显示的标题
                newObj[2] = obj[6]; //点击次数
                newObj[3] = obj[7] + "." + obj[8]; //发布人
                newObj[4] = obj[9]; //版本
                newObj[5] = obj[10]; //发布日期
                newObj[6] = obj[11]; //评论数
                newObj[7] = obj[12]; //是否红头
                newObj[8] = obj[13]; //信息类型
                newObj[9] = obj[1]; //channelType
                mustReadInfo.add(newObj);
                title.delete(0, title.length());
            }
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
        }
        return mustReadInfo;
    }

    /**
     * 设置信息排序
     * @param informationId String
     * @param orderNum String
     * @throws Exception
     * @return Integer
     */
    public Integer setOrderCode(String informationId, String channelType,
                                String orderNum) throws Exception {
        Integer result = Integer.valueOf("0");
        begin();
        try {
            /*
             int num = Integer.parseInt(orderNum)<1?1:Integer.parseInt(orderNum);
                        int totalNum = 0;//总记录数
                        Iterator iter = session.iterate("select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info join info.informationChannel ch where ch.channelType=" + channelType);
                        if(iter.hasNext()){
                            totalNum = ((Integer) iter.next()).intValue();
                        }
                        float orderCode = 0;
                        if(num == 1){
                            //排到第一条
                            iter = session.iterate("select max(info.orderCode) from com.whir.ezoffice.information.infomanager.po.InformationPO info join info.informationChannel ch where ch.channelType=" + channelType);
                            if(iter.hasNext()){
             orderCode = Float.parseFloat(iter.next() + "") + 100;
                            }
                        }else if(num >= totalNum){
                            //排序到最后一条
                            iter = session.iterate("select min(info.orderCode) from com.whir.ezoffice.information.infomanager.po.InformationPO info join info.informationChannel ch where ch.channelType=" + channelType);
                            if(iter.hasNext()){
             orderCode = Float.parseFloat(iter.next() + "") - 100;
                            }
                        }else{
                            //排序到num条
                            //得到当前信息ID的排序码
                            float orOrderCode=0;

                            iter=session.iterate("select po.orderCode from com.whir.ezoffice.information.infomanager.po.InformationPO po where po.informationId="+informationId);
                            if(iter.hasNext()){
             orOrderCode=Float.parseFloat(iter.next().toString());
                            }
                            Query query = session.createQuery("select info.orderCode from com.whir.ezoffice.information.infomanager.po.InformationPO info join info.informationChannel ch where ch.channelType=" + channelType + " order by info.orderCode desc");
                            query.setFirstResult(num - 2);
                            query.setMaxResults(2);
                            List tmp = query.list();
                            if(tmp != null && tmp.size() == 2){
             float first = Float.parseFloat(tmp.get(0) + "");
                                float next = Float.parseFloat(tmp.get(1) + "");
                                //System.out.println("orOrderCode:"+orOrderCode);
                                if(first==orOrderCode){
                                    //System.out.println("orOrderCode==first");
                                    //如果要移动的信息就是第一条信息，那么first,next 需向后移一条
                                    first=next;
                                    query=session.createQuery("select info.orderCode from com.whir.ezoffice.information.infomanager.po.InformationPO info join info.informationChannel ch where ch.channelType=" + channelType +" and info.orderCode<"+first+" order by info.orderCode desc");
                                    query.setFirstResult(0);
                                    query.setMaxResults(1);
                                    tmp=query.list();
                                    if(tmp!=null){
             next=Float.parseFloat(tmp.get(0).toString());
                                    }else{
                                        next+=100;
                                    }
                                }else if(next==orOrderCode){
                                    //System.out.println("orOrderCode==next");
                                    //如果要移动的信息就是第二条信息，那么first,next 需向前移一条
                                    next=first;
                                    query=session.createQuery("select info.orderCode from com.whir.ezoffice.information.infomanager.po.InformationPO info join info.informationChannel ch where ch.channelType=" + channelType +" and info.orderCode>"+next+" order by info.orderCode");
                                    query.setFirstResult(0);
                                    query.setMaxResults(1);
                                    tmp=query.list();
                                    if(tmp!=null){
             first=Float.parseFloat(tmp.get(0).toString());
                                    }else{
                                        first-=100;
                                    }
                                }
                                //System.out.println("first:"+first);
                                //System.out.println("next:"+next);
                                orderCode = (first + next) / 2;
                                //System.out.println("orderCode:"+orderCode);
                            }
                        }
                        NumberFormat nf1 = NumberFormat.getInstance();


                        //System.out.println("^^^^^^^^^^^^^^^^^^^^^^orderCode^^^^^^^^^^^^^^^^^^^^^^^^^^^"+nf1.format(orderCode));

                        String tmpCode = nf1.format(orderCode) + "";
                        if(tmpCode.indexOf(".") >= 0){
                            if(tmpCode.length() - tmpCode.indexOf(".") > 4 ){
             tmpCode = tmpCode.substring(0, tmpCode.indexOf(".") + 4);
                            }
                        }
                        String temp1="";
                        //System.out.println("^^^^^^^^^^^^^^^^^^^^^tmpCode^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+tmpCode);
                        String[] tmpCodenow = tmpCode.toString().split(",");
                        for(int i=0;i<tmpCodenow.length;i++){

                          temp1=temp1+tmpCodenow[i];
                        }

                        //System.out.println("^^^^^^^^^^^^^^^^^^^^^temp1temp1^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+temp1);
             */
            InformationPO po = (InformationPO) session.load(InformationPO.class,
                    Long.valueOf(informationId));
            po.setOrderCode(orderNum);
            session.flush();
        } catch (Exception e) {
            result = Integer.valueOf("-1");
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
        }
        return result;
    }


    public String getUserViewCh(String userId, String orgId, String channelType,
                                String userDefine) throws Exception {
        String hSql = "";
        begin();
        try {
            userDefine = userDefine == null ? "0" : userDefine;
            String rightCode = "";

//            System.out.println("^^^^^^^^^^^^^^channeltype^^^^^^^^^^^^^^^^^"+channelType);
//            System.out.println("^^^^^^^^^^^^^^userDefine^^^^^^^^^^^^^^^^^"+userDefine);
            if (channelType.equals("0") || userDefine.equals("1")) {
                //知识维护权限
                //栏目维护权限由信息管理设置代替
                //rightCode = "01*03*02";
                rightCode = "01*02*01";
            } else {
                //部门主页维护
                rightCode = "01*01*02";
            }

            //           System.out.println("^^^^^^^^^^^^^^^^^rightCode^^^^^^^^^^^^^^^^^^^^^"+rightCode);

            hSql = "select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";

            //取用户组织的所有上级组织(包括自身)
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like concat('%$', aaa.orgId, '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + "))>0 ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
            }

            Query query = session.createQuery(tmpSql);
            List orgList = query.list();

            //取用户的组织及所有下级组织
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like concat('%$'," + orgId +
                        ", '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate('$" +
                        orgId + "$',aaa.orgIdString)>0 ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(" +
                        orgId + ")), '$%') ";
            }

            query = session.createQuery(tmpSql);
            List orgChildList = query.list();

            //取用户信息分类维护的权限范围
            query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                        " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                        rightCode + "' and ccc.empId = " +
                                        userId);
            List tmpList = query.list();
            boolean allScope = false;
            String scopeString = "";
            if (tmpList != null && tmpList.size() > 0) {
                Object[] obj = (Object[]) tmpList.get(0);
                String scopeType = obj[0].toString();
                String scopeScope = "";
                if (obj[1] != null && !obj[1].toString().equals("")) {
                    scopeScope = obj[1].toString().substring(1,
                            obj[1].toString().length() - 1);
                }
                //0:全部1:本人2:本组织及下属组织3:本组织4:自定义
                //不是全部
                if (!scopeType.equals("0")) {
                    //本人时
                    if (scopeType.equals("1")) {
                        scopeString = " icpo.createdEmp = " + userId + " or icpo.channelManager like '%$" + userId + "$%' or ";
                    //本组织即下属组织
                    } else if (scopeType.equals("2")) {
                        for (int i = 0; i < orgChildList.size(); i++) {
                            scopeString = scopeString + "icpo.createdOrg = " +
                                          orgChildList.get(i) + " or icpo.channelManagerOrg like '%*"+
                                          orgChildList.get(i) + "*%' or ";
                        }
                    //本组织
                    } else if (scopeType.equals("3")) {
                        scopeString = "icpo.createdOrg = " + orgId + " or icpo.channelManagerOrg like '%*" + orgId + "*%' or ";
                    } else if (scopeType.equals("4")) {
                        if (!scopeScope.equals("")) {
                            //如果有多个组织 原来的写法导致 查不出一个组织  9.2.1.0 王国良 2010.3.11;
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                           if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                            for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                              orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                            }
                             orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");
                             Query tmpQuery = session.createQuery(orgsql.toString());
                             List tmpOrgList = tmpQuery.list();
                             for (int i = 0; i < tmpOrgList.size(); i++) {
                            	 scopeString = scopeString +
                                            "icpo.createdOrg = " +
                                            tmpOrgList.get(i) + " or icpo.channelManagerOrg like '%*" + tmpOrgList.get(i) + "*%' or ";
                             }

                           }
                        }
                    }
                } else {//全部
                    allScope = true;
                }
            }

            if (!allScope) {
                String orgString = ""; //下级组织ID串
                for (int i = 0; i < orgList.size(); i++) {
                    orgString = orgString + " icpo.channelReaderOrg like '%*" +
                                orgList.get(i) + "*%' or icpo.channelManagerOrg like '%*" + orgList.get(i) + "*%' or ";
                }

                //取用户所属的组
                String groupString = ""; //组ID串
                query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                            userId);
                tmpList = query.list();
                for (int i = 0; i < tmpList.size(); i++) {
                    groupString = groupString +
                                  " icpo.channelReaderGroup like '%@" +
                                  tmpList.get(i) + "@%' or icpo.channelManagerGroup like '%@" + tmpList.get(i) + "@%' or ";
                }

                //取用户的兼职组织
                query = session.createQuery("select po.sidelineOrg from com.whir.org.vo.usermanager.UserPO po where po.empId=" + userId);
                List sideList = query.list();
                String sidelineOrg = "";
                String sideLineOrgWhere = "";
                if(sideList!=null && sideList.size()>0){
                	sidelineOrg = (String) sideList.get(0);
                	if(sidelineOrg!=null && !sidelineOrg.equals("")){
	                	sidelineOrg = sidelineOrg.substring(1, sidelineOrg.length() - 1);
	                	String[] sarr = sidelineOrg.split("\\*\\*");
	                    if (sarr != null && sarr.length > 0) {
	                        for (int i = 0; i < sarr.length; i++) {
	                        	sideLineOrgWhere += " or icpo.channelReaderOrg like '%*"+sarr[i]+"*%'";
	                        }
	                    }
                	}

                }
                if (channelType.equals("-1")) {
                    hSql = "select icpo.channelId" +
                           " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                           ",com.whir.org.vo.organizationmanager.OrganizationVO org where (" +
                           scopeString + orgString + groupString +
                           " icpo.channelReader like '%$" + userId + "$%' or icpo.channelManager like '%$" + userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup=''))"+(sideLineOrgWhere.equals("")?"":sideLineOrgWhere)+")  and (icpo.channelType > 0) and (icpo.userDefine=0) and (org.orgId=icpo.channelType) ";
                } else {
                    hSql = hSql + " where (" + scopeString + orgString +
                           groupString + " icpo.channelReader like '%$" +
                           userId + "$%' or icpo.channelManager like '%$" + userId + "$%'or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup=''))"+(sideLineOrgWhere.equals("")?"":sideLineOrgWhere)+" ) and icpo.channelType = " +
                           channelType;
                }

            } else {
                if (channelType.equals("-1")) {
                    hSql = "select icpo.channelId" +
                           " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                           ",com.whir.org.vo.organizationmanager.OrganizationVO org " +
                           "where icpo.channelType >0 and icpo.userDefine=0 and (org.orgId=icpo.channelType)";
                } else {
                    hSql = hSql + " where icpo.channelType = " + channelType;
                }
            }

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return hSql;
    }


    public List getUserViewCh2(String userId, String orgId, String channelType,
                               String userDefine) throws Exception {
        String hSql = "";
        List rList = new ArrayList();
        begin();
        try {
            userDefine = userDefine == null ? "0" : userDefine;
            String rightCode = "";

//            System.out.println("^^^^^^^^^^^^^^channeltype^^^^^^^^^^^^^^^^^"+channelType);
//            System.out.println("^^^^^^^^^^^^^^userDefine^^^^^^^^^^^^^^^^^"+userDefine);
            if (channelType.equals("0") || userDefine.equals("1")) {
                //知识维护权限
                //栏目维护权限由信息管理设置代替
                //rightCode = "01*03*02";
                rightCode = "01*02*01";
            } else {
                //部门主页维护
                rightCode = "01*01*02";
            }

            //           System.out.println("^^^^^^^^^^^^^^^^^rightCode^^^^^^^^^^^^^^^^^^^^^"+rightCode);

            hSql = "select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";

            //取用户组织的所有上级组织(包括自身)
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like concat('%$', aaa.orgId, '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") )>0  ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
            }

            Query query = session.createQuery(tmpSql);
            List orgList = query.list();

            //取用户的组织及所有下级组织
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like concat('%$'," + orgId +
                        ", '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate('$" +
                        orgId + "$',aaa.orgIdString)>0 ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(" +
                        orgId + ")), '$%') ";
            }

            query = session.createQuery(tmpSql);
            List orgChildList = query.list();

            //取用户信息分类维护的权限范围
            query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                        " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                        rightCode + "' and ccc.empId = " +
                                        userId);
            List tmpList = query.list();
            boolean allScope = false;
            String scopeString = "";
            if (tmpList != null && tmpList.size() > 0) {
                Object[] obj = (Object[]) tmpList.get(0);
                String scopeType = obj[0].toString();

                String scopeScope = "";
                if (obj[1] != null && !obj[1].toString().equals("")) {
                    scopeScope = obj[1].toString().substring(1,
                            obj[1].toString().length() - 1);
                }

                if (!scopeType.equals("0")) {
                    if (scopeType.equals("1")) {
                        scopeString = " icpo.createdEmp = " + userId + " or ";
                    } else if (scopeType.equals("2")) {
                        StringBuffer scopeBuffer=new StringBuffer();
                        for (int i = 0; i < orgChildList.size(); i++) {
                             scopeBuffer.append("icpo.createdOrg = ").append(orgChildList.get(i)).append(" or ");
                        }
                        scopeString+=scopeBuffer.toString();
                    } else if (scopeType.equals("3")) {
                        scopeString = "icpo.createdOrg = " + orgId + " or ";
                    } else if (scopeType.equals("4")) {
                        if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                               Query tmpQuery = session.createQuery(orgsql.toString());
                               List tmpOrgList = tmpQuery.list();
                               StringBuffer scopeBuffer=new StringBuffer();
                               for (int i = 0; i < tmpOrgList.size(); i++) {
                                 scopeBuffer.append("icpo.createdOrg = ").append(tmpOrgList.get(i)).append(" or ");
                               }
                               scopeString+=scopeBuffer.toString();
                           }

                        }
                    }
                } else {
                    allScope = true;
                }
            }

            if (!allScope) {
                String orgString = ""; //下级组织ID串
                StringBuffer scopeBuffer=new StringBuffer();
                for (int i = 0; i < orgList.size(); i++) {
                    //scopeBuffer.append(" icpo.channelReaderOrg like '%*").append(orgList.get(i)).append("' or ");
                    // 王国良 2009-10-28 以上改动
                     scopeBuffer.append(" icpo.channelReaderOrg like '%*").append(orgList.get(i)).append("*%' or ");
                }

                //兼职组织 王国良 2009-10-28
                List sideOrgList = session.createQuery("select eee.sidelineOrg from com.whir.org.vo.usermanager.EmployeeVO eee where eee.empId=" +
                        userId).list();
                if (sideOrgList != null && sideOrgList.size() > 0) {
                    String sideLineOrg = (String) sideOrgList.get(0);
                    if (sideLineOrg != null && !sideLineOrg.equals("") &&
                        sideLineOrg.length() > 2) {
                        sideLineOrg = sideLineOrg.substring(1,
                                sideLineOrg.length() - 1);
                        String[] sarr = sideLineOrg.split("\\*\\*");
                        if (sarr != null && sarr.length > 0) {
                            for (int i = 0; i < sarr.length; i++) {
                                 scopeBuffer.append(" icpo.channelReaderOrg like '%*").append(sarr[i]).append("*%' or ");
                            }
                        }
                    }
                }


                orgString+=scopeBuffer.toString();

                //取用户所属的组
                String groupString = ""; //组ID串
                query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                            userId);
                tmpList = query.list();
                for (int i = 0; i < tmpList.size(); i++) {
                    groupString = groupString +
                                  " icpo.channelReaderGroup like '%@" +
                                  tmpList.get(i) + "@%' or ";
                }
                if (channelType.equals("-1")) {
                    hSql = "select icpo.channelId" +
                           " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                           ",com.whir.org.vo.organizationmanager.OrganizationVO org where (" +
                           scopeString + orgString + groupString +
                           " icpo.channelReader like '%$" + userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')))  and (icpo.channelType > 0) and (icpo.userDefine=0) and (org.orgId=icpo.channelType) ";
                } else {
                    hSql = hSql + " where (" + scopeString + orgString +
                           groupString + " icpo.channelReader like '%$" +
                           userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')) ) and icpo.channelType = " +
                           channelType;
                }

            } else {
                if (channelType.equals("-1")) {
                    hSql = "select icpo.channelId" +
                           " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                           ",com.whir.org.vo.organizationmanager.OrganizationVO org " +
                           "where icpo.channelType >0 and icpo.userDefine=0 and (org.orgId=icpo.channelType)";
                } else {
                    hSql = hSql + " where icpo.channelType = " + channelType;
                }
            }

            query = session.createQuery(hSql);
            rList = query.list();

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return rList;
    }


    public String[] getSingleEditor(String documentEditor,
                                    String informationIssuer, String year,
                                    String month) throws Exception {
        begin();
//      System.out.println("^^^^^^^^^^^^^ejb^^^^^^^^^^^^^^^^^^^^^^^^"+year);
//      System.out.println("^^^^^^^^^^^^^ejb^^^^^^^^^^^^^^^^^^^^^^^^"+month);
        String[] result = {"", "", "", "", "", ""};
        try {
            String viewSQL1 = "", viewSQL2 = "", viewSQL3 = "", viewSQL4 = "",
                    viewSQL5 = "", viewSQL6 = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                viewSQL1 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='0' and info2.informationAuthor='" +
                           documentEditor + "' and  info2.documentEditor='" +
                           informationIssuer +
                           "' and YEAR(info2.informationIssueTime)=" + year +
                           " and MONTH(info2.informationIssueTime)=" + month + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL2 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='1' and info2.informationAuthor='" +
                           documentEditor + "' and  info2.documentEditor='" +
                           informationIssuer +
                           "' and YEAR(info2.informationIssueTime)=" + year +
                           " and MONTH(info2.informationIssueTime)=" + month + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL3 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='2' and info2.informationAuthor='" +
                           documentEditor + "' and  info2.documentEditor='" +
                           informationIssuer +
                           "' and YEAR(info2.informationIssueTime)=" + year +
                           " and MONTH(info2.informationIssueTime)=" + month + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";

                viewSQL4 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='0' and info2.informationAuthor='" +
                           documentEditor + "'" + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL5 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='1' and info2.informationAuthor='" +
                           documentEditor + "'" + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL6 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='2' and info2.informationAuthor='" +
                           documentEditor + "'" + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";

            } else {
                viewSQL1 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='0' and info2.informationAuthor='" +
                           documentEditor + "' and  info2.documentEditor='" +
                           informationIssuer +
                        "' and EZOFFICE.FN_DATENAME('YEAR', info2.informationIssueTime)=" +
                           year +
                        " and EZOFFICE.FN_DATENAME('MONTH', info2.informationIssueTime)=" +
                           month + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL2 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='1' and info2.informationAuthor='" +
                           documentEditor + "' and  info2.documentEditor='" +
                           informationIssuer +
                        "' and EZOFFICE.FN_DATENAME('YEAR', info2.informationIssueTime)=" +
                           year +
                        " and EZOFFICE.FN_DATENAME('MONTH', info2.informationIssueTime)=" +
                           month + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL3 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='2' and info2.informationAuthor='" +
                           documentEditor + "' and  info2.documentEditor='" +
                           informationIssuer +
                        "' and EZOFFICE.FN_DATENAME('YEAR', info2.informationIssueTime)=" +
                           year +
                        " and EZOFFICE.FN_DATENAME('MONTH', info2.informationIssueTime)=" +
                           month + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";

                viewSQL4 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='0' and info2.informationAuthor='" +
                           documentEditor + "'" + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL5 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='1' and info2.informationAuthor='" +
                           documentEditor + "'" + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";
                viewSQL6 = "select count(*) from com.whir.ezoffice.information.infomanager.po.InformationPO info2 where info2.documentType='2' and info2.informationAuthor='" +
                           documentEditor + "'" + " and (info2.afficeHistoryDate is null or info2.afficeHistoryDate =-1) and (info2.informationOrISODoc is null or info2.informationOrISODoc='0' )";

            }

            Iterator iter = session.iterate(viewSQL1);
            if (iter.hasNext()) {
                result[0] = iter.next() + "";
            }

            iter = session.iterate(viewSQL2);
            if (iter.hasNext()) {
                result[1] = iter.next() + "";
            }

            iter = session.iterate(viewSQL3);
            if (iter.hasNext()) {
                result[2] = iter.next() + "";
            }

            iter = session.iterate(viewSQL4);
            if (iter.hasNext()) {
                result[3] = iter.next() + "";
            }

            iter = session.iterate(viewSQL5);
            if (iter.hasNext()) {
                result[4] = iter.next() + "";
            }

            iter = session.iterate(viewSQL6);
            if (iter.hasNext()) {
                result[5] = iter.next() + "";
            }

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;

        }
        return result;
    }

    /**
     * 取得某条信息的可查看人
     * @param informationId String 信息标识
     * @return Object[]
     */
    public Object[] getAllBrowser(String informationId) throws Exception {
        Object[] obj = null;
        begin();
        try {
            Iterator iter = session.iterate("select info.informationReader, info.informationReaderOrg, info.informationReaderGroup, " +
                                            "ch.channelReader, ch.channelReaderOrg, ch.channelReaderGroup from " +
                                            "com.whir.ezoffice.information.infomanager.po.InformationPO info join " +
                                            "info.informationChannel ch where info.informationId = " +
                                            informationId);
            if (iter.hasNext()) {
                obj = (Object[]) iter.next();
            }
        } catch (Exception ex) {
            throw ex;
        }finally{
            session.close();
            session = null;
        }
        return obj;
    }

    /**
     * 检查某个栏目是否可以被该用户浏览
     * @param userId String 用户ID
     * @param orgId String 组织ID
     * @param channelType String 栏目类型
     * @param userDefine String 是否用户定义
     * @param channelId String 栏目ID
     * @return Boolean 可以浏览返回true
     * @throws Exception
     */
    public Boolean channelCanView(String userId, String orgId,
                                  String channelType,
                                  String userDefine, String channelId) throws
            Exception {
        Boolean result = Boolean.FALSE;
        String sql = this.getUserViewCh(userId, orgId, channelType, userDefine);
        try {
            begin();
            sql += " and icpo.channelId=" + channelId;
            Iterator it = session.iterate(sql);
            if (it.hasNext()) {
                result = Boolean.TRUE;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * 删除信息评论
     * @param commentId String
     * @throws Exception
     * @return Integer
     */
    public Integer delComment(String commentId) throws Exception {
        Integer result = Integer.valueOf("0");
        begin();
        try {
            //InformationCommentPO po = (InformationCommentPO) session.load(InformationCommentPO.class, Long.valueOf(commentId));
            //session.delete(po);
            session.delete("from com.whir.ezoffice.information.infomanager.po.InformationCommentPO po where po.commentId=" +
                           commentId);
            session.flush();
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
     * 删除信息评论2
     * -20151106 by jqq 删除评论的同时，将信息表中评论数量减一
     */
    public Integer delComment2(String commentId,String informationId) throws Exception {
        Integer result = Integer.valueOf("0");
        begin();
        try {
            session.delete("from com.whir.ezoffice.information.infomanager.po.InformationCommentPO po where po.commentId=" +
                           commentId);
            InformationPO informationPO = (InformationPO) session.load(
                    InformationPO.class, new Long(informationId));
            //评论数量减去一
            //System.out.println("=========================处理前评论数量：" + informationPO.getInformationCommonNum());
            informationPO.setInformationCommonNum(informationPO.getInformationCommonNum() - 1);
            session.update(informationPO);
            session.flush();
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
     * 返回用户可以修改的ID串 ,12,34,56,
     * @param channelId String
     * @param userId String
     * @param orgId String
     * @param orgIdString String
     * @param inforIds String
     * @param rightList List
     * @throws Exception
     * @return String
     */
    public String getInformationModiIds(String channelId, String userId,
                                        String orgId, String orgIdString,
                                        String inforIds, List rightList) throws
            Exception {
        StringBuffer buffer = new StringBuffer();
        begin();
        try {
            orgIdString = (buffer.append("$").append(orgIdString).append("$")).
                          toString();
            String[] orgIdArray = orgIdString.split("\\$\\$");

            //判段用户所在的组是否在维护人组里
            //取用户所在的组ID
            List list = session.createQuery("select po.groupId from com.whir.org.vo.groupmanager.GroupVO po join po.employees emp where emp.empId=" +
                                            userId).list();
            int i;
            buffer = new StringBuffer(" where po.channelId=");
            buffer.append(channelId).append(" and (");

            for (i = 0; list != null && i < list.size(); i++) {
                buffer.append(" po.channelManagerGroup like '%@").append(
                        list.get(i)).append("%@' or ");
            }
            for (i = 0; i < orgIdArray.length; i++) {
                if (!"".equals(orgIdArray[i]))
                    buffer.append(" po.channelManagerOrg like '%*").append(
                            orgIdArray[i]).append("*%' or ");
            }

            //兼职组织 王国良 2009-10-28
             List sideOrgList = session.createQuery("select eee.sidelineOrg from com.whir.org.vo.usermanager.EmployeeVO eee where eee.empId=" +
                     userId).list();
             if (sideOrgList != null && sideOrgList.size() > 0) {
                 String sideLineOrg = (String) sideOrgList.get(0);
                 if (sideLineOrg != null && !sideLineOrg.equals("") &&
                     sideLineOrg.length() > 2) {
                     sideLineOrg = sideLineOrg.substring(1,
                             sideLineOrg.length() - 1);
                     String[] sarr = sideLineOrg.split("\\*\\*");
                     if (sarr != null && sarr.length > 0) {
                         for (int ii = 0; ii < sarr.length; ii++) {
                             buffer.append(" po.channelManagerOrg like '%*").append(
                           sarr[ii]).append("*%' or ");
                         }
                     }
                 }
             }

            buffer.append(" po.channelManager like '%$").append(userId).append(
                    "$%')");
            int num = ((Integer) session.createQuery("select count(po.channelId) from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po " +
                    buffer.toString()).iterate().next()).intValue();
            if (num > 0) {
                //用户在维护人范围内，检索用户可以修改的信息ID
                String where = "";
                if(rightList!=null && rightList.size()>0){
                    Object[] obj = (Object[]) rightList.get(0);
                    String scopeType = obj[0].toString();
                    if ("0".equals(scopeType)) {
                        //可以维护全部数据
                        where = " 1=1 ";
                    } else if ("1".equals(scopeType)) {
                        //可以维护本人的数据
                        where = "po.informationIssuerId=" + userId;
                    } else if ("2".equals(scopeType)) {
                        //可以维护本组织及下级组织的数据
                        String orgRange = getAllJuniorOrgIdByRange("*" + orgId +
                                "*");
                        if (orgRange.indexOf("a") > 0) {
                            String[] tmp = orgRange.split("a");
                            for (int k = 0; k < tmp.length; k++) {
                                where += "po.informationIssueOrgId in(" + tmp[k] +
                                        ") or ";
                            }
                            if (where.endsWith("or ")) {
                                where = where.substring(0, where.length() - 3);
                            }
                        } else {
                            where = "po.informationIssueOrgId in(" + orgRange +
                                    ") ";
                        }
                        //silensColin 2009-11-2
                        if (sideOrgList != null && sideOrgList.size() > 0) {
                            String sideLineOrg = (String) sideOrgList.get(0);
                            if (sideLineOrg != null && !sideLineOrg.equals("") &&
                                sideLineOrg.length() > 2) {
                                sideLineOrg = sideLineOrg.substring(1,
                                        sideLineOrg.length() - 1);
                                sideLineOrg.replaceAll("\\*", ",");
                                sideLineOrg.replaceAll(",,", ",");
                                where += " or po.informationIssueOrgId in(" + sideLineOrg +
                                    ") ";

                            }
                        }


                        //where = fieldOrg + " in(" + orgRange + ") ";
                    } else if ("3".equals(scopeType)) {
                        //可以维护本组织的数据
                        where = " po.informationIssueOrgId=" + orgId;
                    } else {
                        //scopeType==4 维护定义的范围
                        String orgRange = getAllJuniorOrgIdByRange((String) obj[1]);
                        if ("".equals(orgRange)) {
                            where = "1>2";
                        } else {
                            if (orgRange.indexOf("a") > 0) {
                                String[] tmp = orgRange.split("a");
                                for (int k = 0; k < tmp.length; k++) {
                                    where += "po.informationIssueOrgId in(" + tmp[k] +
                                            ") or ";
                                }
                                if (where.endsWith("or ")) {
                                    where = where.substring(0, where.length() - 3);
                                }
                            } else {
                                where = "po.informationIssueOrgId in(" + orgRange +
                                        ") ";
                            }
                        }

                    }

                    if(!"".equals(where)){

                        where =
                                "select po.informationId from com.whir.ezoffice.information.infomanager.po.InformationPO po where po.informationId in (" +
                                inforIds + ") and (" + where + ")";

                        list = session.createQuery(where).list();
                        buffer = new StringBuffer();
                        if (list != null) {
                            for (i = 0; i < list.size(); i++) {
                                buffer.append(",").append(list.get(i));
                            }
                            buffer.append(",");
                        }
                    }
                }
            }
            session.close();
        } catch (Exception ex) {
            buffer = new StringBuffer();
            session.close();
            ex.printStackTrace();
            throw ex;
        } finally {
            return buffer.toString();
        }

    }

    /**
     * 取得某个组织范围的所有下级组织
     * @param range String 一个组织范围的串 如: *101**120*
     * @throws Exception
     * @return String  返回所有下级组织的ID 如: 122,124,200
     */
    public String getAllJuniorOrgIdByRange(String range) throws Exception {
        String result = "-1,";
        try {
            StringBuffer where = new StringBuffer(" WHERE ");
            range = "*" + range + "*";
            String[] rangeArray = range.split("\\*\\*");
            int i = 0;
            for (i = 1; i < rangeArray.length; i++) {
                if (i > 1)
                    where.append(" or ");
                where.append(" org.orgIdString like '%$");
                where.append(rangeArray[i]);
                where.append("$%' ");
            }

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

            //i=range.length();
            //if(i>0) range=range.substring(0,i-1);
        } catch (Exception e) {
            System.out.println("error!" + e.getMessage());
            throw e;
        }
        return result;
    }

    /**
     * 删除历史信息
     * @param historyId String
     * @param informationId String
     * @throws Exception
     * @return String
     */
    public String deleteHistory(String historyId, String informationId) throws
            Exception {
        begin();
        Connection conn = null;
        try {
            conn = session.connection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(
                    "delete from oa_inforhistoryaccessory where history_id=" +
                    historyId);
            stmt.executeUpdate(
                    "delete from oa_informationhistory where history_id=" +
                    historyId);
            stmt.close();
            conn.close();
            session.close();
        } catch (Exception ex) {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            session.close();
            throw ex;
        }
        return "1";
    }


    public java.util.List getAfficheList(String domainId, String userId,
                                         String orgId, String orgIdString,
                                         String hasRightStr) throws Exception {
        String channelType = "0";
        String userDefine = "0";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        // 当前时间
        Calendar nowCalendar = Calendar.getInstance();
        String nowToLocaleString = simpleDateFormat.format(
                nowCalendar.getTime());

        List list = new ArrayList();
        boolean hasRight = false;
        if (hasRightStr != null && hasRightStr.equals("1")) {
            hasRight = true;
        }
        try {

            begin();
            String viewSQL =
                    " aaa.informationId, aaa.informationTitle, aaa.informationKits, " +
                    " aaa.informationIssuer, aaa.informationVersion, " +
                    " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead," +
                    " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId," +
                    " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite,aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg";
            String fromSQL =
                    " com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
                    " join aaa.informationChannel bbb ";
            String whereSQL = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
//*******************************************wanggl_start*****************************************************************//
            whereSQL = " where aaa.domainId=" + domainId +
                    " and ( bbb.afficheChannelStatus='1' ) and aaa.informationStatus=0 ";
            if (databaseType.indexOf("mysql") >= 0) {
                whereSQL += " and ( '" + nowToLocaleString +
                        "' between aaa.validBeginTime and aaa.validEndTime ) ";
            } else {
                whereSQL += " and (  EZOFFICE.FN_STRTODATE('" + nowToLocaleString +
                        "','L') between aaa.validBeginTime and aaa.validEndTime ) ";
            }

            //取用户所在组织

            Query query1 = session.createQuery("select po.groupId from com.whir.org.vo.groupmanager.GroupVO po join po.employees emp where emp.empId=" +
                                               userId);
            List groupList = query1.list();
            String readerWhere1 = " and (";
            readerWhere1 += "((aaa.informationReader is null or aaa.informationReader ='') and (aaa.informationReaderOrg is null or aaa.informationReaderOrg='') and ( aaa.informationReaderGroup is null or aaa.informationReaderGroup='') ) ";
            if (orgIdString != null && orgIdString.length() > 3) {
                String cStr = orgIdString.substring(1, orgIdString.length() - 1);
                cStr = cStr.replaceAll("\\$", ",");
                cStr = cStr.replaceAll(",,", ",");
                String[] gg1 = cStr.split(",");
                if (gg1 != null && gg1.length > 0) {
                    for (int i = 0; i < gg1.length; i++) {
                        readerWhere1 += " or aaa.informationReaderOrg like '%*" +
                                gg1[i] + "*%' ";
                    }
                }
            }
            readerWhere1 += " or aaa.informationReader like '%$" + userId +
                    "$%' ";
            readerWhere1 += " or aaa.informationIssuerId = " + userId;
            if (groupList != null && groupList.size() > 0) {
                for (int i = 0; i < groupList.size(); i++) {
                    String groupId = "" + groupList.get(i);
                    readerWhere1 += "  or  aaa.informationReaderGroup like '%@" +
                            groupId + "@%' ";

                }
            }
            readerWhere1 += " ) ";
            whereSQL += readerWhere1;

//********************************************wanggl_end*******************************************************************//
            /************  String hSql = "";
              //取用户可以浏览全部信息的栏目(有栏目的维护权限、在栏目的可查看人中、栏目的可查看人为空)
              hSql = getUserViewCh(userId, orgId, channelType,userDefine);
              String  lhsql="";
             List hsqlList=getUserViewCh2(userId, orgId, channelType,userDefine);
              if(hsqlList!=null&&hsqlList.size()!=0){
                  for(int ii=0;ii<hsqlList.size()-1;ii++){
                      lhsql=lhsql+"',"+hsqlList.get(ii)+",',";
                  }
                  lhsql=lhsql+"',"+hsqlList.get(hsqlList.size()-1)+",'";
              }
              //判断当前用户是否有信息维护－排序的权限
              //具有信息维护的权限并且权限范围为全部的用户具有排序的权限
              //boolean hasRight = new com.whir.org.manager.bd.ManagerBD().hasRight(userId, "01*03*03");
                 // whereSQL = whereSQL + " and bbb.channelId in (" + hSql + ") ";
             whereSQL = whereSQL + " and ( bbb.channelId in (" + hSql + ")  ";
                 if(!lhsql.equals("")){
                     whereSQL += "or aaa.otherChannel in (" + lhsql + " ) ";
                 }
                 whereSQL += ") ";
                 /* 2.当前用户在信息的可维护人中
              *   当前用户为栏目的可维护人
              *   具有信息维护权限
              *   信息的创建人或创建组织在维护范围内
              */
             /* ******************** String readerWhere = getInfoReader(userId,orgId,orgIdString,"aaa");
                   whereSQL += " and bbb.channelType = " + channelType;
               if (hasRight) {

               } else {
                   if (readerWhere == null || readerWhere.equals("")) {
                   } else {
                      whereSQL = whereSQL + " and (" + readerWhere + ") ";
                   }
               }*/
             whereSQL += " and bbb.channelType = " + channelType;
            whereSQL = whereSQL + " order by aaa.orderCode desc,aaa.informationModifyTime desc,aaa.informationId desc";
            String sql = " select " + viewSQL + " from " + fromSQL + whereSQL;

            Query query = session.createQuery(sql);
            list = query.list();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            session.close();
            session = null;

        }
        return list;
    }


    /**
     *
     * @param informationId String
     * @param status String
     * @throws Exception
     * @return boolean
     */
    public boolean setInformationStatus(String informationId, String status) throws
            Exception {
        boolean result = false;
        int intStatus = Integer.parseInt(status);
        begin();
        try {

            InformationPO informationPO = (InformationPO) session.load(
                    InformationPO.class, new Long(informationId));
            informationPO.setInformationStatus(intStatus);
           // informationPO.setInformationIssueTime(new java.util.Date());
            session.flush();
            //informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId));
            result = true;
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }


    /**
     * 取得用户可以维护的栏目（可以查看该栏目下的所有的信息）
     * @param userId String
     * @param orgId String
     * @param channelType String
     * @param userDefine String
     * @throws Exception
     * @return String
     */
    public String getManagedChannel(String userId, String orgId,
                                    String channelType, String userDefine) throws
            Exception {
        String hSql = "";
        begin();
        try {
            userDefine = userDefine == null ? "0" : userDefine;
            String rightCode = "01*03*03";

//            System.out.println("^^^^^^^^^^^^^^channeltype^^^^^^^^^^^^^^^^^"+channelType);
//            System.out.println("^^^^^^^^^^^^^^userDefine^^^^^^^^^^^^^^^^^"+userDefine);
//            if (channelType.equals("0") || userDefine.equals("1")) {
//                //知识维护权限
//                //栏目维护权限由信息管理设置代替
//                //rightCode = "01*03*02";
//                rightCode = "01*02*01";
//            } else {
//                //部门主页维护
//                rightCode = "01*01*02";
//            }

//           System.out.println("^^^^^^^^^^^^^^^^^rightCode^^^^^^^^^^^^^^^^^^^^^"+rightCode);

            hSql = "select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";

            //取用户组织的所有上级组织(包括自身)
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like concat('%$', aaa.orgId, '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ")) >0 ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
            }

            Query query = session.createQuery(tmpSql);
            List orgList = query.list();

            //取用户的组织及所有下级组织
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like concat('%$'," + orgId +
                        ", '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like '%$" +
                        orgId + "$%' ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(" +
                        orgId + ")), '$%') ";
            }

            query = session.createQuery(tmpSql);
            List orgChildList = query.list();

            //取用户信息分类维护的权限范围
            query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                        " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                        rightCode + "' and ccc.empId = " +
                                        userId);
            List tmpList = query.list();
            boolean allScope = false;
            String scopeString = "";
            if (tmpList != null && tmpList.size() > 0) {
                Object[] obj = (Object[]) tmpList.get(0);
                String scopeType = obj[0].toString();
                String scopeScope = "";
                if (obj[1] != null && !obj[1].toString().equals("")) {
                    scopeScope = obj[1].toString().substring(1,
                            obj[1].toString().length() - 1);
                }

                int i = 0;

                if (!scopeType.equals("0")) {
                    if (scopeType.equals("1")) {
                        scopeString = " icpo.createdEmp = " + userId;
                    } else if (scopeType.equals("2")) {
                        for (i = 0; i < orgChildList.size(); i++) {
                            if (i == 0) {
                                scopeString = "icpo.createdOrg = " +
                                              orgChildList.get(i);
                            } else {
                                scopeString += " or icpo.createdOrg = " +
                                        orgChildList.get(i);
                            }
                        }
                    } else if (scopeType.equals("3")) {
                        scopeString = "icpo.createdOrg = " + orgId;
                    } else if (scopeType.equals("4")) {
                        if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                            Query tmpQuery = session.createQuery(orgsql.toString());
                            List tmpOrgList = tmpQuery.list();
                            for (i = 0; i < tmpOrgList.size(); i++) {
                                if (i == 0) {
                                    scopeString = "icpo.createdOrg = " +
                                                  tmpOrgList.get(i);
                                } else {
                                    scopeString += " or icpo.createdOrg = " +
                                            tmpOrgList.get(i);
                                }
                            }
                        }
                     }
                    }
                    if ("".equals(scopeString)) {
                        scopeString = " 1=1 ";
                    }
                    if (channelType.equals("-1")) {
                        hSql = "select icpo.channelId" +
                               " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                               ",com.whir.org.vo.organizationmanager.OrganizationVO org where (" +
                               scopeString + ") and  (icpo.channelType > 0) and (icpo.userDefine=0) and (org.orgId=icpo.channelType) ";
                    } else {
                        hSql = hSql + " where (" + scopeString +
                               ") and icpo.channelType = " + channelType;
                    }

                } else {
                    allScope = true;
                    if (channelType.equals("-1")) {
                        hSql = "select icpo.channelId" +
                               " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                               ",com.whir.org.vo.organizationmanager.OrganizationVO org " +
                               "where icpo.channelType >0 and icpo.userDefine=0 and (org.orgId=icpo.channelType)";
                    } else {
                        hSql = hSql + " where icpo.channelType = " +
                               channelType;
                    }

                }
            } else {
                //该用户没有栏目维护的权限
                hSql = "-1";
            }

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return hSql;
    }


    /**
     *
     * @param userId String
     * @throws Exception
     * @return List
     */
    public List getAllGroupByUserId(String userId) throws Exception {
        List list = null;
        begin();
        try {

            Query query = session.createQuery("select po.groupId from com.whir.org.vo.groupmanager.GroupVO po join po.employees emp where emp.empId=" +
                                              userId);
            list = query.list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }

    /**
     * 检查某个栏目是否可以被该用户浏览,增加栏目维护人的判断
     * @param userId String 用户ID
     * @param orgId String 组织ID
     * @param channelType String 栏目类型
     * @param userDefine String 是否用户定义
     * @param channelId String 栏目ID
     * @return Boolean 可以浏览返回true
     * @throws Exception
     */
    public Boolean channelCanView2(String userId, String orgId,
                                   String channelType,
                                   String userDefine, String channelId) throws
            Exception {
        Boolean result = Boolean.FALSE;
        String sql = this.getUserViewCh3(userId, orgId, channelType, userDefine);

        try {
            begin();
            sql += " and icpo.channelId=" + channelId;
            Iterator it = session.iterate(sql);
            if (it.hasNext()) {
                result = Boolean.TRUE;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            session.close();
        }
        return result;
    }

    /**
     * 组织用户可以查看的栏目的hsql，增加对栏目维护人的判断
     * @param userId String
     * @param orgId String
     * @param channelType String
     * @param userDefine String
     * @throws Exception
     * @return String
     */
    public String getUserViewCh3(String userId, String orgId,
                                 String channelType, String userDefine) throws
            Exception {
        String hSql = "";
        begin();
        try {
            userDefine = userDefine == null ? "0" : userDefine;
            String rightCode = "";

//            System.out.println("^^^^^^^^^^^^^^channeltype^^^^^^^^^^^^^^^^^"+channelType);
//            System.out.println("^^^^^^^^^^^^^^userDefine^^^^^^^^^^^^^^^^^"+userDefine);
            if (channelType.equals("0") || userDefine.equals("1")) {
                //知识维护权限
                //栏目维护权限由信息管理设置代替
                //rightCode = "01*03*02";
                rightCode = "01*02*01";
            } else {
                //部门主页维护
                rightCode = "01*01*02";
            }

            //           System.out.println("^^^^^^^^^^^^^^^^^rightCode^^^^^^^^^^^^^^^^^^^^^"+rightCode);

            hSql = "select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";

            //取用户组织的所有上级组织(包括自身)
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like concat('%$', aaa.orgId, '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") )>0  ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
            }

            Query query = session.createQuery(tmpSql);
            List orgList = query.list();

            //取用户的组织及所有下级组织
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like concat('%$'," + orgId +
                        ", '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like '%$" +
                        orgId + "$%' ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(" +
                        orgId + ")), '$%') ";
            }

            query = session.createQuery(tmpSql);
            List orgChildList = query.list();

            //取用户信息分类维护的权限范围
            query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                        " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                        rightCode + "' and ccc.empId = " +
                                        userId);
            List tmpList = query.list();
            boolean allScope = false;
            String scopeString = "";
            if (tmpList != null && tmpList.size() > 0) {
                Object[] obj = (Object[]) tmpList.get(0);
                String scopeType = obj[0].toString();
                String scopeScope = "";
                if (obj[1] != null && !obj[1].toString().equals("")) {
                    scopeScope = obj[1].toString().substring(1,
                            obj[1].toString().length() - 1);
                }

                if (!scopeType.equals("0")) {
                    if (scopeType.equals("1")) {
                        scopeString = " icpo.createdEmp = " + userId + " or ";
                    } else if (scopeType.equals("2")) {
                        for (int i = 0; i < orgChildList.size(); i++) {
                            scopeString = scopeString + "icpo.createdOrg = " +
                                          orgChildList.get(i) + " or ";
                        }
                    } else if (scopeType.equals("3")) {
                        scopeString = "icpo.createdOrg = " + orgId + " or ";
                    } else if (scopeType.equals("4")) {
                        if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");
                               Query tmpQuery = session.createQuery(orgsql.toString());
                               List tmpOrgList = tmpQuery.list();
                               for (int i = 0; i < tmpOrgList.size(); i++) {
                                 scopeString = scopeString +
                                              "icpo.createdOrg = " +
                                              tmpOrgList.get(i) + " or ";
                            }
                        }
                     }
                    }
                } else {
                    allScope = true;
                }
            }

            if (!allScope) {
                //增加对栏目维护人的判断
                String whOrgString = "";
                String whGroupString = "";
                String orgString = ""; //下级组织ID串

                for (int i = 0; i < orgList.size(); i++) {
                    orgString = orgString + " icpo.channelReaderOrg like '%*" +
                                orgList.get(i) + "*%' or ";
                    whOrgString = whOrgString +
                                  " icpo.channelManagerOrg like '%*" +
                                  orgList.get(i) + "*%' or ";
                }

                //兼职组织 王国良 2009-10-28
                List sideOrgList = session.createQuery("select eee.sidelineOrg from com.whir.org.vo.usermanager.EmployeeVO eee where eee.empId=" +
                        userId).list();
                if (sideOrgList != null && sideOrgList.size() > 0) {
                    String sideLineOrg = (String) sideOrgList.get(0);
                    if (sideLineOrg != null && !sideLineOrg.equals("") &&
                        sideLineOrg.length() > 2) {
                        sideLineOrg = sideLineOrg.substring(1,
                                sideLineOrg.length() - 1);
                        String[] sarr = sideLineOrg.split("\\*\\*");
                        if (sarr != null && sarr.length > 0) {
                            for (int i = 0; i < sarr.length; i++) {
                                orgString = orgString + " icpo.channelReaderOrg like '%*" +
                                sarr[i] + "*%' or ";
                            }
                        }
                    }
                }


                //取用户所属的组
                String groupString = ""; //组ID串
                query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                            userId);
                tmpList = query.list();
                for (int i = 0; i < tmpList.size(); i++) {
                    groupString = groupString +
                                  " icpo.channelReaderGroup like '%@" +
                                  tmpList.get(i) + "@%' or ";
                    whGroupString = whGroupString +
                                    " icpo.channelManagerGroup like '%@" +
                                    tmpList.get(i) + "@%' or ";
                }
                if (channelType.equals("-1")) {
                    hSql = "select icpo.channelId" +
                           " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                           ",com.whir.org.vo.organizationmanager.OrganizationVO org where (" +
                           scopeString + orgString + groupString + whOrgString +
                           whGroupString + " icpo.channelReader like '%$" +
                           userId + "$%' or icpo.channelManager like '%$" +
                           userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')))  and (icpo.channelType > 0) and (icpo.userDefine=0) and (org.orgId=icpo.channelType) ";
                } else {
                    hSql = hSql + " where (" + scopeString + orgString +
                           groupString + whOrgString + whGroupString +
                           " icpo.channelReader like '%$" + userId +
                           "$%' or icpo.channelManager like '%$" + userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')) ) and icpo.channelType = " +
                           channelType;
                }

            } else {
                if (channelType.equals("-1")) {
                    hSql = "select icpo.channelId" +
                           " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                           ",com.whir.org.vo.organizationmanager.OrganizationVO org " +
                           "where icpo.channelType >0 and icpo.userDefine=0 and (org.orgId=icpo.channelType)";
                } else {
                    hSql = hSql + " where icpo.channelType = " + channelType;
                }
            }

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return hSql;
    }

    public String getAllInfoChannel(String userId, String orgId,
                                    String channelType, String userDefine) throws
            Exception {
        String hSql = "";
        begin();
        try {
            userDefine = userDefine == null ? "0" : userDefine;
            String rightCode = "";

//            System.out.println("^^^^^^^^^^^^^^channeltype^^^^^^^^^^^^^^^^^"+channelType);
//            System.out.println("^^^^^^^^^^^^^^userDefine^^^^^^^^^^^^^^^^^"+userDefine);
            if (channelType.equals("0") || userDefine.equals("1")) {
                //知识维护权限
                //栏目维护权限由信息管理设置代替
                //rightCode = "01*03*02";
                rightCode = "01*02*01";
            } else {
                //部门主页维护
                rightCode = "01*01*02";
            }

//           System.out.println("^^^^^^^^^^^^^^^^^rightCode^^^^^^^^^^^^^^^^^^^^^"+rightCode);

            hSql = "select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";

            //取用户组织的所有上级组织(包括自身)
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like concat('%$', aaa.orgId, '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") )>0  ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                        orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
            }

            Query query = session.createQuery(tmpSql);
            List orgList = query.list();

            //取用户的组织及所有下级组织
            if (databaseType.indexOf("mysql") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like concat('%$'," + orgId +
                        ", '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like '%$" +
                        orgId + "$%' ";

            } else {
                tmpSql =
                        "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                        " where aaa.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(" +
                        orgId + ")), '$%') ";
            }

            query = session.createQuery(tmpSql);
            List orgChildList = query.list();

            //取用户信息分类维护的权限范围
            query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                        " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                        rightCode + "' and ccc.empId = " +
                                        userId);
            List tmpList = query.list();
            boolean allScope = false;
            String scopeString = "";
            if (tmpList != null && tmpList.size() > 0) {
                Object[] obj = (Object[]) tmpList.get(0);
                String scopeType = obj[0].toString();
                String scopeScope = "";
                if (obj[1] != null && !obj[1].toString().equals("")) {
                    scopeScope = obj[1].toString().substring(1,
                            obj[1].toString().length() - 1);
                }

                int i = 0;

                if (!scopeType.equals("0")) {
                    if (scopeType.equals("1")) {
                        scopeString = " icpo.createdEmp = " + userId;
                    } else if (scopeType.equals("2")) {

                        /*for (i = 0; i < orgChildList.size(); i++) {
                            if (i == 0) {
                                scopeString = "icpo.createdOrg = " +
                                              orgChildList.get(i);
                            } else {
                                scopeString += " or icpo.createdOrg = " +
                                        orgChildList.get(i);
                            }
                        }*/

                       if(orgChildList!=null&&orgChildList.size()>0){
                           scopeString="  icpo.createdOrg  in (";
                           for (i = 0; i < orgChildList.size(); i++) {
                              if (i == 0) {
                                  scopeString += orgChildList.get(i);
                              } else {
                                  scopeString += ","+orgChildList.get(i)+"";
                              }
                          }
                          scopeString+=")";
                       }


                    } else if (scopeType.equals("3")) {
                        scopeString = "icpo.createdOrg = " + orgId;
                    } else if (scopeType.equals("4")) {
                        if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                            Query tmpQuery = session.createQuery(orgsql.toString());
                            List tmpOrgList = tmpQuery.list();
                            if(tmpOrgList!=null&&tmpOrgList.size()>0){
                                String tempStr="(";
                                for (i = 0; i < tmpOrgList.size(); i++) {
                                    tempStr+=""+tmpOrgList.get(i);
                                  if (i == 0) {
                                      tempStr+=tmpOrgList.get(i);
                                  } else {
                                     tempStr+=","+tmpOrgList.get(i);
                                  }
                                  tempStr+=")";
                                  scopeString=" icpo.createdOrg   in"+tempStr;
                              }

                            }
                           /* for (i = 0; i < tmpOrgList.size(); i++) {
                                if (i == 0) {
                                    scopeString = "icpo.createdOrg = " +
                                                  tmpOrgList.get(i);
                                } else {
                                    scopeString += " or icpo.createdOrg = " +
                                            tmpOrgList.get(i);
                                }
                            }*/
                         }
                        }
                    }
                    if ("".equals(scopeString)) {
                        scopeString = " 1=1 ";
                    }
                    if (channelType.equals("-1")) {
                        hSql = "select icpo.channelId" +
                               " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                               ",com.whir.org.vo.organizationmanager.OrganizationVO org where (" +
                               scopeString + ") and  (icpo.channelType > 0) and (icpo.userDefine=0) and (org.orgId=icpo.channelType) ";
                    } else {
                        hSql = hSql + " where (" + scopeString +
                               ") and icpo.channelType = " + channelType;
                    }

                } else {
                    allScope = true;
                    if (channelType.equals("-1")) {
                        hSql = "select icpo.channelId" +
                               " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                               ",com.whir.org.vo.organizationmanager.OrganizationVO org " +
                               "where icpo.channelType >0 and icpo.userDefine=0 and (org.orgId=icpo.channelType)";
                    } else {
                        hSql = hSql + " where icpo.channelType = " +
                               channelType;
                    }

                }
            } else {
                //该用户没有栏目维护的权限
                hSql = "-1";
            }

        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return hSql;
    }


    /**
     *   取相关新闻
     * @param orgId String
     * @param infoId String
     * @param userId String
     * @param orgIdString String
     * @param channelType String
     * @param userDefine String
     * @param channelStatusType String  当是 2:  是iso文档
     * @return List
     * @throws Exception
     */
    public List getAssociateInfo(String orgId, String infoId, String userId,
                                 String orgIdString, String channelType,
                                 String userDefine, String channelStatusType) throws
            Exception {
        List list = null;
        String readWhere = getInfoReader(userId, "", orgIdString, "aaa");

        String hSql = getUserViewCh(userId, orgId, channelType, userDefine);
        begin();
        try {
            /*String sql = "select distinct infocc.channelName, inforss.informationId, inforss.informationTitle, inforss.informationSummary, " +
                           "inforss.informationModifyTime, inforss.informationHead, inforss.informationType,infocc.channelId from com.whir.ezoffice.information.infomanager.po.InformationPO " +
                           "inforss join inforss.informationChannel infocc where ((" + readWhere + ") or infocc.channelId in ("+hSql+")) " +
                           "and (inforss.informationKey<>'' and inforss.informationKey = (select ccc.informationKey from com.whir.ezoffice.information.infomanager.po.InformationPO ccc " +
                           "where ccc.informationId = " + infoId + " and ccc.informationStatus=0) or inforss.informationId in (select ddd.associateInfo from " +
                           "com.whir.ezoffice.information.infomanager.po.AssociateInfoPO ddd where ddd.masterInfo = " + infoId + ")) and inforss.informationId <> " +
                           infoId + " order by inforss.informationId desc";*/
            //取domainId

            String domainId = session.createQuery("select po.domainId from com.whir.org.vo.usermanager.EmployeeVO po where po.empId=" +
                                                  userId).iterate().next().
                              toString();

            //取本信息的关键字
            String key = "";
            Iterator it = session.createQuery("select aaa.informationKey from com.whir.ezoffice.information.infomanager.po.InformationPO aaa where aaa.informationId = " +
                                              infoId).iterate();
            if (it.hasNext()) {
                Object obj = it.next();
                if (obj != null) {
                    key = obj.toString();
                }
            }
            StringBuffer buffer = new StringBuffer("select distinct bbb.channelName, aaa.informationId, aaa.informationTitle, aaa.informationSummary,aaa.informationModifyTime, aaa.informationHead, aaa.informationType,bbb.channelId ");
            buffer.append("from com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb ")
                    .append("where aaa.domainId=").append(domainId);

            if (channelStatusType.equals("2")) { //iso文档
                buffer.append(" and ( bbb.afficheChannelStatus='" +
                              channelStatusType + "' )")
                        .append("and (");
            } else {
                buffer.append(" and ((").append(readWhere).append(
                        ") and bbb.channelId in (").append(hSql).append(")) ")
                        .append(" and ( bbb.afficheChannelStatus='" +
                                channelStatusType + "' )")
                        .append("and (");
            }

            if (!"".equals(key)) {
                buffer.append("(aaa.informationKey='").append(key).append(
                        "') or");
            }

            buffer.append(" aaa.informationId in (select ddd.associateInfo from com.whir.ezoffice.information.infomanager.po.AssociateInfoPO ddd where ddd.masterInfo = ").
                    append(infoId).append(")")
                    .append(") and aaa.informationId <> ").append(infoId).
                    append(" order by aaa.informationId desc");

            list = session.createQuery(buffer.toString()).list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return list;
    }


    //******************************************************************************

     /**
      * 保存发放单信息
      * @param po IsoPaperPO
      * @return Long
      * @throws Exception
      */
     public Long saveIsoPaperPO(IsoPaperPO po) throws Exception {
         Long result = new Long( -1);
         begin();
         try {
             result = (Long) session.save(po);
             session.flush();
         } catch (Exception e) {
             System.out.println(
                     "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
             System.out.println(e.getMessage());
             System.out.println(
                     "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
         } finally {
             session.close();
             session = null;
             transaction = null;
         }
         return result;
     }


    /**
     * 修改 发放 单状态
     * @param id String
     * @param status String
     * @return String
     * @throws Exception
     */
    public String setPaperPOStatus(String id, String status) throws Exception {
        begin();
        String result = "0";
        try {
            java.sql.Connection conn = session.connection();
            java.sql.Statement stmt = conn.createStatement();
            stmt.execute("update EZOFFICE.OA_ISO_Paper set paperStatus= '" +
                         status + "' where isoPaperId=" + id);
        } catch (Exception e) {
            result = "-1";
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();

        }
        return result;
    }

    /**
     * 修改 发放 单
     * @param id String
     * @param arg String[]
     * @return String
     * @throws Exception
     */
    public String updatePaperPO(String id, String arg[]) throws Exception {
        begin();
        String result = "0";
        try {
            IsoPaperPO isoPaperPO = (IsoPaperPO) session.load(IsoPaperPO.class,
                    new Long(id));
            if (arg.length > 0) { //修改状态
                isoPaperPO.setPaperStatus("" + arg[0]);
            }
            if (arg.length > 1) {
                isoPaperPO.setBackUserId(new Long("" + arg[1]));
            }
            if (arg.length > 2) {
                isoPaperPO.setBackUserName("" + arg[2]);
            }
            isoPaperPO.setBackTime(new Date());
            session.update(isoPaperPO);
            session.flush();
        } catch (Exception e) {
            result = "-1";
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
        }
        return result;
    }


    /**
     *删除 发放 单
     * @param id String
     * @return String
     * @throws Exception
     */
    public String deletePaperPO(String id) throws Exception {
        begin();
        String result = "0";
        try {
            java.sql.Connection conn = session.connection();
            java.sql.Statement stmt = conn.createStatement();
            stmt.execute(
                    "delete  from  EZOFFICE.OA_ISO_Paper  where isoPaperId in ( " +
                    id + " )");
        } catch (Exception e) {
            result = "-1";
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();

        }
        return result;
    }


    /** 保存 借阅记录
     *
     * @param id String
     * @return String
     * @throws Exception
     */
    public Long saveBorrowPO(IsoBorrowUserPO po) throws Exception {
        begin();
        Long result = new Long( -1);
        try {
            result = (Long) session.save(po);
            session.flush();
        } catch (Exception e) {
            result = new Long( -1);
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();

        }
        return result;
    }


    /**
     *修改借阅信息
     * @param id String
     * @return String
     * @throws Exception
     */
    public Long updateBorrowPO(IsoBorrowUserPO po) throws Exception {
        begin();
        Long result = new Long( -1);
        try {
            session.update(po);
            session.flush();
        } catch (Exception e) {
            result = new Long( -1);
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();

        }
        return result;
    }


    /**
     *  修改 借阅状态
     * @param id String
     * @return String
     * @throws Exception
     */
    public String setBorrowStatus(String id, String status) throws Exception {
        begin();
        String result = "0";
        try {
            java.sql.Connection conn = session.connection();
            java.sql.Statement stmt = conn.createStatement();
            stmt.execute(
                    "update EZOFFICE.OA_ISO_BorrowUser set  borrowStatus= '" +
                    status + "' where isoBorrowUserId=" + id);
        } catch (Exception e) {
            result = "-1";
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();

        }
        return result;
    }


    /**
     *  load 借阅单信息
     * @param id String
     * @return IsoBorrowUserPO
     * @throws Exception
     */
    public IsoBorrowUserPO loadBorrowUserPO(String id) throws Exception {
        begin();
        IsoBorrowUserPO po = null;
        try {
            po = (IsoBorrowUserPO) session.load(IsoBorrowUserPO.class,
                                                new Long(id));
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
        } finally {
            session.close();
        }
        return po;
    }


    /**
     * 删除借阅 单
     * @param ids String
     * @return String
     * @throws Exception
     */
    public String deleteBorrow(String ids) throws Exception {
        begin();
        String bl = "0";
        try {
            if (ids != null && !ids.equals("")) {
                session.delete(
                        " from com.whir.ezoffice.information.isodoc.po.IsoBorrowUserPO  po where po.cardOrderId in (" +
                        ids + ")");
            }
            session.flush();

            //删除流程数据
            java.sql.Connection conn = session.connection();
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(
                    "SELECT WF_IMMOFORM_ID FROM EZOFFICE.WF_IMMOBILITYFORM WHERE WF_MODULE_ID=36");
            String tableId = "0";
            if (rs.next()) {
                tableId = rs.getString(1);
            }
            rs.close();
            stmt.execute("DELETE from EZOFFICE.WF_WORK WHERE WORKTABLE_ID=" +
                         tableId + " AND WORKRECORD_ID in (" +
                         ids.substring(0, ids.length() - 1) + ")");
            stmt.execute("DELETE from EZOFFICE.WF_WORK WHERE WORKTABLE_ID=" +
                         tableId + " AND WORKRECORD_ID in (" +
                         ids.substring(0, ids.length() - 1) + ")");
            stmt.execute("DELETE from EZOFFICE.WF_WORK WHERE WORKTABLE_ID=" +
                         tableId + " AND WORKRECORD_ID in (" +
                         ids.substring(0, ids.length() - 1) + ")");

            bl = "1";
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return bl;
    }
    /**
     * 从 借阅单 中 查找 是否 所传 文档
     * @param informationId String       文档id
     * @param informationOldId String    文档老的相关的 文档id
     * @param userId    借阅人id
     * @return List
     * @throws Exception
     */
    public List findIdsFromBorrow(String informationId, String informationOldId,
                                  String userId) throws Exception {
        begin();
        List list = new ArrayList();
        try {
            String sql = "";

            SimpleDateFormat simpleDateFormat_short = new SimpleDateFormat(
                    "yyyy-MM-dd");
            // 当前时间
            Calendar nowCalendar = Calendar.getInstance();
            String nowToLocaleString_short = simpleDateFormat_short.format(
                    nowCalendar.getTime());


            String nowB = nowToLocaleString_short + " 23:59:59";
            String nowE = nowToLocaleString_short + " 00:00:00";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            //informationOldId 现在 都是为空!
            if (databaseType.indexOf("mysql") >= 0) {
                sql = " select po  from  com.whir.ezoffice.information.isodoc.po.IsoBorrowUserPO po where ( po.informationId=" +
                      informationId + " ) and po.userId= " + userId + " ";

                if (informationOldId != null && !informationOldId.equals("")) {
                    sql = " select po  from  com.whir.ezoffice.information.isodoc.po.IsoBorrowUserPO po where ( po.informationId=" +
                          informationId + " or " + informationOldId +
                            " like concat('%$', po.informationId, '$%') ) and po.userId= " +
                          userId + " ";
                }
                sql += " and '" + nowB + "' > =po.borrowBeginTime and '" + nowE +
                        "' < =po.borrowEndTime"; //在有效期限内

            } else {
                sql = " select po from  com.whir.ezoffice.information.isodoc.po.IsoBorrowUserPO po where ( po.informationId=" +
                      informationId + ") and po.userId= " + userId + " ";

                if (informationOldId != null && !informationOldId.equals("")) {
                    if (databaseType.indexOf("db2") >= 0) {
                     sql = " select po from  com.whir.ezoffice.information.isodoc.po.IsoBorrowUserPO po where ( po.informationId=" +
                         informationId + " or locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR( po.informationId)), '$'),'" + informationOldId + "')>0)  and po.userId= " +
                         userId + " ";
                  }else{
                      sql = " select po from  com.whir.ezoffice.information.isodoc.po.IsoBorrowUserPO po where ( po.informationId=" +
                           informationId + " or " + informationOldId + " like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR( po.informationId)), '$%')) and po.userId= " +
                           userId + " ";
                  }
                }
                sql += " and  EZOFFICE.FN_STRTODATE('" + nowB +
                        "','L') >=po.borrowBeginTime and EZOFFICE.FN_STRTODATE('" +
                        nowE + "','L') < =po.borrowEndTime"; //在借阅的有效期内

            }
            sql += " and po.borrowStatus='1' ";
            list = session.find(sql);
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();

        }
        return list;
    }


    /**
     * 当是走 销毁流程时 修改 文档 状态
     * @param informationId String       文档id
     * @param informationStatus int      文档状态
     * @param isoDocStatus String        销毁流程状态
     * @return boolean
     * @throws Exception
     */
    public boolean setInformationStatus(String informationId,
                                        String informationStatus,
                                        String isoDocStatus) throws Exception {
        boolean result = false;
        begin();
        try {

            InformationPO informationPO = (InformationPO) session.load(
                    InformationPO.class, new Long(informationId));
            informationPO.setInformationStatus(Integer.parseInt(
                    informationStatus));
            informationPO.setIsoDocStatus(isoDocStatus);
            informationPO.setIsoDealCategory("2");
            //informationPO.setInformationIssueTime(new java.util.Date());
            informationPO.setInformationModifyTime(new java.util.Date());
            session.flush();
            //informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId));
            result = true;
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return result;
    }



    /**
        * 当是走 销毁流程时 修改 文档 状态   并 修改 修改人
        * @param informationId String       文档id
        * @param informationStatus int      文档状态
        * @param isoDocStatus String        销毁流程状态
        * @return boolean
        * @throws Exception
        */
       public boolean setInformationStatus(String informationId,
                                           String informationStatus,
                                           String isoDocStatus,String orgName,String userName) throws Exception {
           boolean result = false;
           begin();
           try {

               InformationPO informationPO = (InformationPO) session.load(
                       InformationPO.class, new Long(informationId));
               informationPO.setInformationStatus(Integer.parseInt(
                       informationStatus));
               informationPO.setIsoDocStatus(isoDocStatus);
               informationPO.setIsoDealCategory("2");
               informationPO.setInforModifyMen(userName);
               informationPO.setInforModifyOrg(orgName);
               informationPO.setModifyEmp(orgName+"."+userName);
               //informationPO.setInformationIssueTime(new java.util.Date());
               informationPO.setInformationModifyTime(new java.util.Date());
               session.flush();
               //informationPO = (InformationPO) session.load(InformationPO.class,new Long(informationId));
               result = true;
           } catch (Exception e) {
               System.out.println("----------------------------------------------");
               e.printStackTrace();
               System.out.println("----------------------------------------------");
               throw e;
           } finally {
               session.close();
               session = null;
               transaction = null;

           }
           return result;
       }


    /**
     * 当办理结束时， 修改大的版本信息
     * @param informationId String
     * @throws Exception
     */
    public void updateBigVersion(String informationId) throws Exception {
        begin();
        try {
            InformationPO informationPO = (InformationPO) session.load(
                    InformationPO.class, new Long(informationId));
            String version = informationPO.getInformationVersion();
            int middle = 1;
            int a = Integer.parseInt(version.substring(0,
                    version.indexOf(".")));
            String b = version.substring(version.indexOf(".") + 1,
                                         version.length());
            /* middle =  Integer.parseInt(b.substring(0, 1));
             if(middle==9){//进一
              a++;
              middle=0;
             }else{
              middle++;
             }
                            version = "" + a + "." + middle;*/
            version = "" + (a + 1) + "." + "00";
            informationPO.setInformationVersion(version);
            session.update(informationPO);
            session.flush();
        } catch (Exception e) {
            System.out.println(
                    "-------------------------------------------------");
            e.printStackTrace();
            System.out.println(
                    "-------------------------------------------------");

        } finally {
            session.close();
            session = null;
            transaction = null;
        }
    }


    /**
     * 根据 版本号 取 信息
     * @param informationId String 信息ID
     * @throws Exception
     * @return List
     */
    public List getInforByVersion(String informationId, String version) throws
            Exception {
        List list = null;
        begin();
        try {
            Query query = session.createQuery("select aaa.historyVersion,aaa.historyIssueOrg,aaa.historyIssuerName,aaa.historyTime," +
                                              "aaa.historyId, aaa.historyHead,ccc.channelId,ccc.channelType,aaa.historyTitle," +
                                              " aaa.historySubTitle,aaa.historyContent,aaa.historyIssuerId," +
                                              " aaa.historyKey,aaa.historyHead,aaa.historyHeadFile,aaa.historySummary," +
                                              " aaa.historyRedIssueTime,aaa.historyRedIssueOrg,aaa.historyAuthor from " +
                                              "com.whir.ezoffice.information.infomanager.po.InformationHistoryPO aaa join aaa.information bbb " +
                                              "join bbb.informationChannel ccc where bbb.informationId = " +
                                              informationId +
                                              " and aaa.historyVersion= '" +
                                              version +
                                              "' and aaa.isoDealCategory<>'2' and aaa.isoDealCategory<>'3' and aaa.isoDealCategory<>'4'  order by aaa.historyTime");
            list = query.list();
        } catch (Exception e) {
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;

        }
        return list;
    }

    /**
     *取历史 修改次数
     * @param informationId String
     * @return String
     * @throws Exception
     */
    public String getHisModiNum(String informationId) throws Exception {
        String result = "1";
        try {
            begin();
            //收件箱
            Query query =
                    session.createQuery(
                            "select count( aaa.historyId ) from  com.whir.ezoffice.information.infomanager.po.InformationHistoryPO aaa join aaa.information bbb where bbb.informationId=" +
                            informationId);
            Integer rr = (Integer) query.iterate().next();
            int irr = rr.intValue();
            irr++;
            result = "" + irr;
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        } finally {
            return result;
        }

    }


    /**
     * 用户可增加的所有的频道
     * @param where String   留接口， 为空
     * @param userId String
     * @param orgId String
     * @param domainId StringgetSingleEditor
     * @return List
     * @throws Exception
     */
    public List getCanVindicate_ISO(String where, String userId, String orgId,
                                    String domainId) throws Exception {

        begin();
        ArrayList alist = new ArrayList();
        try {
            String mySql = "select aaa.channelId, aaa.channelName, aaa.channelType, aaa.channelIdString, aaa.channelLevel,aaa.afficheChannelStatus from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO aaa " +
                           "where (" + where + ") and (aaa.channelType=0 or aaa.userDefine=1) and  aaa.afficheChannelStatus='2' and aaa.domainId=" +
                           domainId +
                           " order by aaa.channelIdString, aaa.channelType";
            Query query = session.createQuery(mySql);
            Iterator iter = query.iterate();
            Iterator iter2 = null;
            StringBuffer channelNameBuf = new StringBuffer();
            while (iter.hasNext()) {
                Object[] obj = (Object[]) iter.next();
                if (obj[4].toString().equals("1")) {
                    if (obj[2].toString().equals("0")) {
                        obj[1] = "文档管理." + obj[1];
                    } else {
                        query = session.createQuery("select aaa.userChannelName from com.whir.ezoffice.information.channelmanager.po.UserChannelPO aaa " +
                                "where aaa.userChannelId=" + obj[2]);
                        iter2 = query.iterate();
                        if (iter2.hasNext()) {
                            obj[1] = iter2.next() + "." + obj[1];
                        }
                    }
                }

                alist.add(obj);
            }
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            System.out.println(e.getMessage());
            System.out.println("---------------------------------------------");
            throw e;
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return alist;
    }


    /**
     *  发放单
     * @param id String
     * @return IsoPaperPO
     * @throws Exception
     */
    public IsoPaperPO loadIsoPaperPO(String id) throws Exception {
        begin();
        IsoPaperPO po = null;
        try {
            po = (IsoPaperPO) session.load(IsoPaperPO.class, new Long(id));
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
        } finally {
            session.close();
        }
        return po;
    }


    /**
     * 检查 是否 查看 了 信息
     * @param informationId String
     * @param userId String
     * @return List
     * @throws Exception
     */
    public List  getBrowByEmpAndIfoId(String informationId,String userId) throws Exception {
        List list = null;
        begin();
        try {
            Query query = session.createQuery("select aaa.browserId from  com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb where  aaa.empId="+userId+" and  bbb.informationId ="+informationId);
            list = query.list();
        } catch (Exception e) {
            System.out.println("----------------------------------------------");
            e.printStackTrace();
            System.out.println("----------------------------------------------");
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
     * @param po IsoCommentPO
     * @return Long
     * @throws Exception
     */
    public Long saveIsoCommentPO(IsoCommentPO po) throws Exception {
       begin();
       Long result = new Long( -1);
       try {
           result = (Long) session.save(po);
           session.flush();
       } catch (Exception e) {
           result = new Long( -1);
           System.out.println("----------------------------------------------");
           e.printStackTrace();
           System.out.println("----------------------------------------------");
           throw e;
       } finally {
           session.close();

       }
       return result;
   }
   /**
    *
    * @param informationId String
    * @param userId String
    * @return List
    * @throws Exception
    */
   public List  getIsoCommentList(String informationId) throws Exception {
       List list = null;
       begin();
       try {
           Query query = session.createQuery("select aaa.dealComment,aaa.acName,aaa.dealDate,aaa.dealEmpName,aaa.inforversion, aaa.infodealType from  com.whir.ezoffice.information.isodoc.po.IsoCommentPO aaa  where  aaa.informationId ="+informationId +" order by aaa.id ");
           list = query.list();
       } catch (Exception e) {
           System.out.println("----------------------------------------------");
           e.printStackTrace();
           System.out.println("----------------------------------------------");
           throw e;
       } finally {
           session.close();
           session = null;
           transaction = null;

       }
       return list;
   }


   /**
    * 取 流程中的 批示意见
    * @param recordId String
    * @param tableId String
    * @param processId String
    * @return List
    * @throws Exception
    */
   public List  getCommentList(String recordId,String tableId,String processId) throws Exception {
       String comment="", empName="", dealwithDate="",isStandForComm="", standForUserName="",activityName="";
       List list = new ArrayList();
             begin();
             try {
                 java.sql.Connection conn = session.connection();
                 java.sql.Statement stmt = conn.createStatement();
               //  System.out.println("commetSql:"+"SELECT B.DEALWITHEMPLOYEECOMMENT,C.EMPNAME,B.DEALWITHTIME,B.ISSTANDFORCOMM,B.STANDFORUSERNAME, A.activityname from  WF_DEALWITHCOMMENT B  join WF_DEALWITH A  on  A.WF_DEALWITH_ID=B.WF_DEALWITH_ID  join  ORG_EMPLOYEE C on  B.DEALWITHEMPLOYEE_ID=C.EMP_ID  where A.WF_DEALWITH_ID in (SELECT WF_DEALWITH_ID FROM EZOFFICE.WF_DEALWITH WHERE DATABASETABLE_ID="+tableId+" AND DATABASERECORD_ID="+recordId+") order by A.curstepcount ");
                 java.sql.ResultSet rs = stmt.executeQuery("SELECT B.DEALWITHEMPLOYEECOMMENT,C.EMPNAME,B.DEALWITHTIME,B.ISSTANDFORCOMM,B.STANDFORUSERNAME, A.activityname from  WF_DEALWITHCOMMENT B  join WF_DEALWITH A  on  A.WF_DEALWITH_ID=B.WF_DEALWITH_ID  join  ORG_EMPLOYEE C on  B.DEALWITHEMPLOYEE_ID=C.EMP_ID  where A.WF_DEALWITH_ID in (SELECT WF_DEALWITH_ID FROM EZOFFICE.WF_DEALWITH WHERE DATABASETABLE_ID="+tableId+" AND DATABASERECORD_ID="+recordId+") order by A.curstepcount ");
                 while (rs.next()) {
                     comment = rs.getString(1);
                     empName = rs.getString(2);
                     dealwithDate = rs.getString(3);
                     isStandForComm = rs.getString(4);
                     standForUserName = rs.getString(5);
                     activityName=rs.getString(6);
                     Object[] obj = new Object[] {comment, empName, dealwithDate,
                                    isStandForComm, standForUserName,activityName};
                     list.add(obj);
                 }
                 rs.close();

    } catch (Exception e) {
        System.out.println("----------------------------------------------");
        e.printStackTrace();
        System.out.println("----------------------------------------------");
        throw e;
    } finally {
        session.close();
        session = null;
        transaction = null;

    }
    return list;
}

   /**
    * 移交发送人
    */
   public void TransferUserId(String informationId,String userId) throws Exception {
       begin();
       try {
           InformationPO informationPO = (InformationPO) session.load(
                   InformationPO.class, new Long(informationId));
           informationPO.setTwoUserId(new Long(userId));
           session.update(informationPO);
           session.flush();
       } catch (Exception e) {
           System.out.println(
                   "-------------------------------------------------");
           e.printStackTrace();
           System.out.println(
                   "-------------------------------------------------");

       } finally {
           session.close();
           session = null;
           transaction = null;
       }
   }




/**
  *  批量移交发送人
  */
 public void BatchTransferUserId(List userList) throws Exception {
     begin();
     try {
         if(userList!=null&&userList.size()>0){
             for(int i=0;i<userList.size();i++){
             String tstr[]=(String [])userList.get(i);
             InformationPO informationPO = (InformationPO) session.load(
                  InformationPO.class, new Long(tstr[0]));
                 informationPO.setTwoUserId(new Long(tstr[1]));
                 informationPO.setTwoUserName(tstr[2]);
                 session.update(informationPO);
           }
         }
         session.flush();
     } catch (Exception e) {
         System.out.println(
                 "-------------------------------------------------");
         e.printStackTrace();
         System.out.println(
                 "-------------------------------------------------");
     } finally {
         session.close();
         session = null;
         transaction = null;
     }
 }




   /**
     *
     * @param po IsoCommentPO
     * @return Long
     * @throws Exception
     */
    public Long saveIsoDeallogPO(IsoDeallogPO po) throws Exception {
       begin();
       Long result = new Long( -1);
       try {
           result = (Long) session.save(po);
           session.flush();
       } catch (Exception e) {
           result = new Long( -1);
           System.out.println("----------------------------------------------");
           e.printStackTrace();
           System.out.println("----------------------------------------------");
           throw e;
       } finally {
           session.close();

       }
       return result;
   }




   /**
  *
  * @param informationId String
  * @param userId String
  * @return List
  * @throws Exception
  */
 public List  getIsoDeallogList(String informationId) throws Exception {
     List list = null;
     begin();
     try {
         Query query = session.createQuery("select aaa.startUser,aaa.dealDate,aaa.dealType,aaa.endUser,aaa.inforversion, aaa.infodealType from  com.whir.ezoffice.information.isodoc.po.IsoDeallogPO aaa  where  aaa.informationId ="+informationId +" order by aaa.id ");
         list = query.list();


     } catch (Exception e) {
         System.out.println("----------------------------------------------");
         e.printStackTrace();
         System.out.println("----------------------------------------------");
         throw e;
     } finally {
         session.close();
         session = null;
         transaction = null;

     }
     return list;
 }


 public Integer getIssueNumOrg(String orgId) throws Exception{
     Integer rs=Integer.valueOf("0");
     begin();
     try{
         //String orgIdString=session.createQuery("select po.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgId="+orgId).iterate().next().toString();
         Iterator it=session.createQuery("select count(po.informationId) from com.whir.ezoffice.information.infomanager.po.InformationPO po join po.informationChannel ch where po.informationIssueOrgId="+orgId+" and ch.channelType=0 and ( po.afficeHistoryDate  is null or  po.afficeHistoryDate = -1)  and (po.informationOrISODoc is null or po.informationOrISODoc='0' )").iterate();
         if(it!=null && it.hasNext()){
             rs=Integer.valueOf(it.next().toString());
         }
         session.close();
     }catch(Exception ex){
         session.close();
         throw ex;
     }
     return rs;
 }




 public Integer getIssueNumPerson(String userId) throws Exception{
     Integer rs=Integer.valueOf("0");
     begin();
     try{
         //String orgIdString=session.createQuery("select po.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgId="+orgId).iterate().next().toString();
         Iterator it=session.createQuery("select count(po.informationId) from com.whir.ezoffice.information.infomanager.po.InformationPO po join po.informationChannel ch where po.informationStatus=0 and po.informationIssuerId="+userId+" and ch.channelType=0 and ( po.afficeHistoryDate  is null or  po.afficeHistoryDate = -1)  and (po.informationOrISODoc is null or po.informationOrISODoc='0' )").iterate();
         if(it!=null && it.hasNext()){
             rs=Integer.valueOf(it.next().toString());
         }

         session.close();
     }catch(Exception ex){
         session.close();
         throw ex;
     }
     return rs;
 }



 /**
  *
  *  取能看到信息的栏目
  * 只列出sql 语句 ，不查出
  * @param userId String
  * @param orgId String
  * @return String[]
  * @throws Exception
  */
 public String [] getUserViewChAll(String userId, String orgId) throws Exception {
       String hSql = "";
       String retutnObj []=null;
       begin();
        try {


          String rightCode_info = "01*02*01";// 信息管理 ，自定义频道
          String rightCode_depart = "01*01*02";//单位主页


          hSql = "select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";
          String userChannelSql= " select aaa.userChannelId from com.whir.ezoffice.information.channelmanager.po.UserChannelPO aaa";

          //取用户组织的所有上级组织(包括自身)
          String tmpSql = "";
          String databaseType = com.whir.common.config.SystemCommon.
                                getDatabaseType();
          if (databaseType.indexOf("mysql") >= 0) {
              tmpSql =
                      "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                      " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                      orgId + ") like concat('%$', aaa.orgId, '$%') ";
          }else if (databaseType.indexOf("db2") >= 0) {
              tmpSql =
                      "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                      " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                      orgId + "))>0 ";

          } else {
              tmpSql =
                      "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                      " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                      orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
          }



          Query query = session.createQuery(tmpSql);
          List orgList = query.list();

          query = session.createQuery(userChannelSql);
          List userChannelSqlList = query.list();
          String userChannelStr=" ( 0";
          if(userChannelSqlList!=null&&userChannelSqlList.size()>0){
              for(int i=0;i<userChannelSqlList.size();i++){
                 userChannelStr+=","+userChannelSqlList.get(i);
              }
          }
        userChannelStr+=" ) ";


          //取用户的组织及所有下级组织
          if (databaseType.indexOf("mysql") >= 0) {
              tmpSql =
                      "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                      " where aaa.orgIdString like concat('%$'," + orgId +
                      ", '$%') ";
          }else if (databaseType.indexOf("db2") >= 0) {
              tmpSql =
                      "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                      " where locate('$" +
                      orgId + "$',aaa.orgIdString)>0 ";

          } else {
              tmpSql =
                      "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                      " where aaa.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(" +
                      orgId + ")), '$%') ";
          }

          query = session.createQuery(tmpSql);
          List orgChildList = query.list();

          //取用户信息分类维护的权限范围
          query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                      " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                      rightCode_info + "' and ccc.empId = " +
                                      userId);
          List tmpList_info = query.list();
          boolean allScope_info = false;
          String scopeString_info = "";
          if (tmpList_info != null && tmpList_info.size() > 0) {
              Object[] obj = (Object[]) tmpList_info.get(0);
              String scopeType = obj[0].toString();
              String scopeScope = "";
              if (obj[1] != null && !obj[1].toString().equals("")) {
                  scopeScope = obj[1].toString().substring(1,
                          obj[1].toString().length() - 1);
              }

              if (!scopeType.equals("0")) {
                  if (scopeType.equals("1")) {
                      scopeString_info = " icpo.createdEmp = " + userId + " or ";
                  } else if (scopeType.equals("2")) {
                      for (int i = 0; i < orgChildList.size(); i++) {
                          scopeString_info = scopeString_info + "icpo.createdOrg = " +
                                        orgChildList.get(i) + " or ";
                      }
                  } else if (scopeType.equals("3")) {
                      scopeString_info = "icpo.createdOrg = " + orgId + " or ";
                  } else if (scopeType.equals("4")) {
                      if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                            Query tmpQuery = session.createQuery(orgsql.toString());
                            List tmpOrgList = tmpQuery.list();
                            for (int i = 0; i < tmpOrgList.size(); i++) {
                               scopeString_info = scopeString_info +
                                            "icpo.createdOrg = " +
                                            tmpOrgList.get(i) + " or ";
                          }
                      }
                   }
                  }
              } else {
                  allScope_info = true;
              }
          }

          //取用户信息分类维护的权限范围
       query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                   " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                   rightCode_depart + "' and ccc.empId = " +
                                   userId);
       List tmpList_depart = query.list();
       boolean allScope_depart = false;
       String scopeString_depart = "";
       if (tmpList_depart != null && tmpList_depart.size() > 0) {
           Object[] obj = (Object[]) tmpList_depart.get(0);
           String scopeType = obj[0].toString();
           String scopeScope = "";
           if (obj[1] != null && !obj[1].toString().equals("")) {
               scopeScope = obj[1].toString().substring(1,
                       obj[1].toString().length() - 1);
           }

           if (!scopeType.equals("0")) {
               if (scopeType.equals("1")) {
                   scopeString_depart = " icpo.createdEmp = " + userId + " or ";
               } else if (scopeType.equals("2")) {
                   for (int i = 0; i < orgChildList.size(); i++) {
                       scopeString_depart = scopeString_depart + "icpo.createdOrg = " +
                                     orgChildList.get(i) + " or ";
                   }
               } else if (scopeType.equals("3")) {
                   scopeString_depart = "icpo.createdOrg = " + orgId + " or ";
               } else if (scopeType.equals("4")) {
                   if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                               Query tmpQuery = session.createQuery(orgsql.
                                       toString());
                               List tmpOrgList = tmpQuery.list();
                               for (int i = 0; i < tmpOrgList.size(); i++) {
                                   scopeString_depart = scopeString_depart +
                                           "icpo.createdOrg = " +
                                           tmpOrgList.get(i) + " or ";
                               }
                   }
                }
               }
           } else {
               allScope_depart = true;
           }
       }

          if (!allScope_info) {

              String orgString = ""; //下级组织ID串
              for (int i = 0; i < orgList.size(); i++) {
                  orgString = orgString + " icpo.channelReaderOrg like '%*" +
                              orgList.get(i) + "*%' or ";
              }

              //取用户所属的组
              String groupString = ""; //组ID串
              query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                          userId);
               List tmpList1 = query.list();
              for (int i = 0; i < tmpList1.size(); i++) {
                  groupString = groupString +
                                " icpo.channelReaderGroup like '%@" +
                                tmpList1.get(i) + "@%' or ";
              }

                  hSql = hSql + " where (" + scopeString_info + orgString +
                         groupString + " icpo.channelReader like '%$" +
                         userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')) )  and icpo.channelType in "+userChannelStr+" ";//去掉了channelType

          } else {
                  hSql = hSql + " where icpo.channelType  in  " + userChannelStr+" ";

          }

           String  hsql2="select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";

          if (!allScope_depart) {
              String orgString = ""; //下级组织ID串
              for (int i = 0; i < orgList.size(); i++) {
                  orgString = orgString + " icpo.channelReaderOrg like '%*" +
                              orgList.get(i) + "*%' or ";
              }

              //取用户所属的组
              String groupString = ""; //组ID串
              query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                          userId);
              List tmpList2 = query.list();
              for (int i = 0; i < tmpList2.size(); i++) {
                  groupString = groupString +
                                " icpo.channelReaderGroup like '%@" +
                                tmpList2.get(i) + "@%' or ";
              }
              hsql2 = "select icpo.channelId" +
                      " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                      ",com.whir.org.vo.organizationmanager.OrganizationVO org where (" +
                      scopeString_depart + orgString + groupString +
                      " icpo.channelReader like '%$" + userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')))  and (icpo.channelType > 0) and (icpo.userDefine=0) and (org.orgId=icpo.channelType) ";
           } else {
                  hsql2 = "select icpo.channelId" +
                         " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                         ",com.whir.org.vo.organizationmanager.OrganizationVO org " +
                         "where icpo.channelType >0 and icpo.userDefine=0 and (org.orgId=icpo.channelType)";
           }
        retutnObj=new String [] {hSql,hsql2};
      } catch (Exception e) {
          System.out.println("----------------------------------------------");
          e.printStackTrace();
          System.out.println("----------------------------------------------");
          throw e;
      } finally {
          session.close();
          session = null;
          transaction = null;

      }
      return retutnObj;
  }


  /**
   * 列出能看到信息的 栏目
   * @param userId String
   * @param orgId String
   * @return List
   * @throws Exception
   */
  public List getUserViewChAll2(String userId, String orgId) throws Exception {
        String hSql = "";
        List returnList=new ArrayList();
        begin();
         try {

           String rightCode_info = "01*02*01";// 信息管理 ，自定义频道
           String rightCode_depart = "01*01*02";//单位主页


           hSql = "select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";



           String userChannelSql= " select aaa.userChannelId from com.whir.ezoffice.information.channelmanager.po.UserChannelPO aaa";


           //取用户组织的所有上级组织(包括自身)
           String tmpSql = "";
           String databaseType = com.whir.common.config.SystemCommon.
                                 getDatabaseType();
           if (databaseType.indexOf("mysql") >= 0) {
               tmpSql =
                       "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                       " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                       orgId + ") like concat('%$', aaa.orgId, '$%') ";
           }else if (databaseType.indexOf("db2") >= 0) {
               tmpSql =
                       "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                       " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                       orgId + "))>0 ";

           } else {
               tmpSql =
                       "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                       " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                       orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
           }



           Query query = session.createQuery(tmpSql);
           List orgList = query.list();

           query = session.createQuery(userChannelSql);
           List userChannelSqlList = query.list();
           String userChannelStr=" ( 0";
           if(userChannelSqlList!=null&&userChannelSqlList.size()>0){
               for(int i=0;i<userChannelSqlList.size();i++){
                 userChannelStr+=","+userChannelSqlList.get(i);
               }
           }
         userChannelStr+=" ) ";


           //取用户的组织及所有下级组织
           if (databaseType.indexOf("mysql") >= 0) {
               tmpSql =
                       "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                       " where aaa.orgIdString like concat('%$'," + orgId +
                       ", '$%') ";
           }else if (databaseType.indexOf("db2") >= 0) {
               tmpSql =
                       "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                       " where locate('$" +
                       orgId + "$',aaa.orgIdString)>0 ";

           } else {
               tmpSql =
                       "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                       " where aaa.orgIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(" +
                       orgId + ")), '$%') ";
           }

           query = session.createQuery(tmpSql);
           List orgChildList = query.list();

           //取用户信息分类维护的权限范围
           query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                       " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                       rightCode_info + "' and ccc.empId = " +
                                       userId);
           List tmpList_info = query.list();
           boolean allScope_info = false;
           String scopeString_info = "";
           if (tmpList_info != null && tmpList_info.size() > 0) {
               Object[] obj = (Object[]) tmpList_info.get(0);
               String scopeType = obj[0].toString();
               String scopeScope = "";
               if (obj[1] != null && !obj[1].toString().equals("")) {
                   scopeScope = obj[1].toString().substring(1,
                           obj[1].toString().length() - 1);
               }

               if (!scopeType.equals("0")) {
                   if (scopeType.equals("1")) {
                       scopeString_info = " icpo.createdEmp = " + userId + " or ";
                   } else if (scopeType.equals("2")) {
                       for (int i = 0; i < orgChildList.size(); i++) {
                           scopeString_info = scopeString_info + "icpo.createdOrg = " +
                                         orgChildList.get(i) + " or ";
                       }
                   } else if (scopeType.equals("3")) {
                       scopeString_info = "icpo.createdOrg = " + orgId + " or ";
                   } else if (scopeType.equals("4")) {
                       if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                           Query tmpQuery = session.createQuery(orgsql.toString());
                           List tmpOrgList = tmpQuery.list();
                           for (int i = 0; i < tmpOrgList.size(); i++) {
                               scopeString_info = scopeString_info +
                                             "icpo.createdOrg = " +
                                             tmpOrgList.get(i) + " or ";
                           }
                        }
                       }
                   }
               } else {
                   allScope_info = true;
               }
           }


           //取用户信息分类维护的权限范围
        query = session.createQuery(" select aaa.rightScopeType,aaa.rightScopeScope from com.whir.org.vo.rolemanager.RightScopeVO aaa " +
                                    " join aaa.right bbb join aaa.employee ccc where bbb.rightCode = '" +
                                    rightCode_depart + "' and ccc.empId = " +
                                    userId);
        List tmpList_depart = query.list();
        boolean allScope_depart = false;
        String scopeString_depart = "";
        if (tmpList_depart != null && tmpList_depart.size() > 0) {
            Object[] obj = (Object[]) tmpList_depart.get(0);
            String scopeType = obj[0].toString();
            String scopeScope = "";
            if (obj[1] != null && !obj[1].toString().equals("")) {
                scopeScope = obj[1].toString().substring(1,
                        obj[1].toString().length() - 1);
            }

            if (!scopeType.equals("0")) {
                if (scopeType.equals("1")) {
                    scopeString_depart = " icpo.createdEmp = " + userId + " or ";
                } else if (scopeType.equals("2")) {
                    for (int i = 0; i < orgChildList.size(); i++) {
                        scopeString_depart = scopeString_depart + "icpo.createdOrg = " +
                                      orgChildList.get(i) + " or ";
                    }
                } else if (scopeType.equals("3")) {
                    scopeString_depart = "icpo.createdOrg = " + orgId + " or ";
                } else if (scopeType.equals("4")) {
                    if (!scopeScope.equals("")) {
                            //9.2.1.0  王国良 2010.3.11
                            String scopeScope_deal1= scopeScope.replace('*',',');
                            scopeScope_deal1=scopeScope_deal1.replaceAll(",,",",");
                            String  scopeScopeaArr[]=scopeScope_deal1.split(",");
                            StringBuffer orgsql= new StringBuffer(" select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa  where ");
                            if(scopeScopeaArr!=null&&scopeScopeaArr.length>0){
                               for(int ii=0;ii<scopeScopeaArr.length-1;ii++){
                                 orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[ii]).append("$%' or ");
                               }
                               orgsql.append(" aaa.orgIdString like '%$").append(scopeScopeaArr[scopeScopeaArr.length-1]).append("$%' ");

                               Query tmpQuery = session.createQuery(orgsql.
                                       toString());
                               List tmpOrgList = tmpQuery.list();
                               for (int i = 0; i < tmpOrgList.size(); i++) {
                                   scopeString_depart = scopeString_depart +
                                           "icpo.createdOrg = " +
                                           tmpOrgList.get(i) + " or ";
                               }
                    }
                  }
                }
            } else {
                allScope_depart = true;
            }
          }

           if (!allScope_info) {
               String orgString = ""; //下级组织ID串
               for (int i = 0; i < orgList.size(); i++) {
                   orgString = orgString + " icpo.channelReaderOrg like '%*" +
                               orgList.get(i) + "*%' or ";
               }

               //取用户所属的组
               String groupString = ""; //组ID串
               query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                           userId);
                List tmpList1 = query.list();
               for (int i = 0; i < tmpList1.size(); i++) {
                   groupString = groupString +
                                 " icpo.channelReaderGroup like '%@" +
                                 tmpList1.get(i) + "@%' or ";
               }

                   hSql = hSql + " where (" + scopeString_info + orgString +
                          groupString + " icpo.channelReader like '%$" +
                          userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')) )  and icpo.channelType in "+userChannelStr+" ";//去掉了channelType

           } else {

                   hSql = hSql + " where icpo.channelType  in  " + userChannelStr+" ";

           }

            String  hsql2="select icpo.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo ";

           if (!allScope_depart) {
               String orgString = ""; //下级组织ID串
               for (int i = 0; i < orgList.size(); i++) {
                   orgString = orgString + " icpo.channelReaderOrg like '%*" +
                               orgList.get(i) + "*%' or ";
               }

               //取用户所属的组
               String groupString = ""; //组ID串
               query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                           userId);
               List tmpList2 = query.list();
               for (int i = 0; i < tmpList2.size(); i++) {
                   groupString = groupString +
                                 " icpo.channelReaderGroup like '%@" +
                                 tmpList2.get(i) + "@%' or ";
               }
               hsql2 = "select icpo.channelId" +
                       " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                       ",com.whir.org.vo.organizationmanager.OrganizationVO org where (" +
                       scopeString_depart + orgString + groupString +
                       " icpo.channelReader like '%$" + userId + "$%' or ((icpo.channelReader is null or icpo.channelReader='') and (icpo.channelReaderOrg is null or icpo.channelReaderOrg='') and (icpo.channelReaderGroup is null or icpo.channelReaderGroup='')))  and (icpo.channelType > 0) and (icpo.userDefine=0) and (org.orgId=icpo.channelType) ";
            } else {
                   hsql2 = "select icpo.channelId" +
                          " from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO icpo " +
                          ",com.whir.org.vo.organizationmanager.OrganizationVO org " +
                          "where icpo.channelType >0 and icpo.userDefine=0 and (org.orgId=icpo.channelType)";
            }



          query = session.createQuery(hSql);
          returnList = query.list();
          query = session.createQuery(hsql2);
          List  departList = query.list();
          if(departList!=null&&departList.size()>0){
           returnList.addAll(departList);
          }
       } catch (Exception e) {
           System.out.println("----------------------------------------------");
           e.printStackTrace();
           System.out.println("----------------------------------------------");
           throw e;
       } finally {
           session.close();
           session = null;
           transaction = null;

       }
       return returnList;
   }



       /** 保存 信息浏览范围
       *
       * @param id String
       * @return String
       * @throws Exception
       */
    public Long saveInforViewScopePO(InforViewScopePO po) throws Exception {
          begin();
          Long result = new Long( -1);
          try {
              result = (Long) session.save(po);
              java.sql.Connection conn = session.connection();

          java.sql.Statement stmt = conn.createStatement();
          java.sql.ResultSet rs = null;

          String userIds=po.getScopeIds();
          //System.out.println("userIds:"+userIds);
          char flagCode = '0';
          int nextPos = 0;
          String str = "";
          String userId = "", orgId = "", groupId = "";
          for (int i = 0; i < userIds.length(); i++) {
              flagCode = userIds.charAt(i);
              nextPos = userIds.indexOf(flagCode, i + 1);
              str = userIds.substring(i, nextPos + 1);
              if (flagCode == '$') {
                  userId = userId + str;
              } else if (flagCode == '*') {
                  orgId = orgId + str;
              } else {
                  groupId = groupId + str;
              }
              i = nextPos;
          }
          if (!userId.equals("")) {
              if (userId.indexOf("$") >= 0) {
                  userId = userId.replace('$', ',');
                  userId = userId.replaceAll(",,", ",");
              }
          } else {
              userId = ",-1,";
          }

          StringBuffer toUserId = new StringBuffer(userId);
      /*  ---------------------if (!groupId.equals("")) {
              if (groupId.indexOf("@") >= 0) {
                  groupId = groupId.substring(1, groupId.length() - 1);
              }
              if (groupId.indexOf("@") >= 0) {
                  groupId = groupId.replace('@', ',');
                  groupId = groupId.replaceAll(",,", ",");
              }
              rs = stmt.executeQuery(
                      "SELECT EMP_ID FROM ORG_USER_GROUP WHERE GROUP_ID IN (" +
                      groupId + ")");
              String tmp = "";
              while (rs.next()) {
                  tmp = rs.getString(1);
                  if (toUserId.indexOf("," + tmp + ",") < 0) {
                      toUserId.append(tmp + ",");
                  }
              }
              rs.close();
          }---------------------*/
          if (!orgId.equals("")) {
              if (orgId.indexOf("*") >= 0) {
                  orgId = orgId.substring(1, orgId.length() - 1);
              }
              if (orgId.indexOf("*") >= 0) {
                  orgId = orgId.replace('*', ',');
                  orgId = orgId.replaceAll(",,", ",");
              }
              String[] tmpStr = {orgId};

              if (orgId.indexOf(",") > 0) {
                  tmpStr = orgId.split(",");
              }
              String whereSql = "";
              String whereSql2 = "";
              for (int i = 0; i < tmpStr.length; i++) {
                  whereSql += "A.ORGIDSTRING LIKE '%$" + tmpStr[i] +
                          "$%' OR ";
                  whereSql2 += " C.SIDELINEORG  like '%*" + tmpStr[i] +
                          "*%' OR ";
              }
              whereSql2 += " 1>1 ";
              whereSql += " 1 > 1 ";
              rs = stmt.executeQuery("SELECT B.emp_id FROM ORG_ORGANIZATION A, ORG_ORGANIZATION_USER B  WHERE   A.ORG_ID=B.ORG_ID   AND (" +
                                     whereSql + ")");
              String tmp = "";
              while (rs.next()) {
                  tmp = rs.getString(1);
                  if (toUserId.indexOf("," + tmp + ",") < 0) {
                      toUserId.append(tmp + ",");
                  }
              }

     /*----------- String tmp2 = "";
              rs = stmt.executeQuery(
                      "SELECT C.emp_id FROM  org_employee C  WHERE " +
                      whereSql2);
              while (rs.next()) {
                  tmp2 = rs.getString(1);
                  if (toUserId.indexOf("," + tmp2 + ",") < 0) {
                      toUserId.append(tmp2 + ",");
                  }
              }
              rs.close(); --------------*/
          }

          //System.out.println("to infro UserId:"+toUserId);
          stmt.execute("DELETE FROM oa_infoViewRightScope_user  WHERE infouserId  IN (-1" + toUserId.toString() + "-1)");
          stmt.execute("INSERT INTO oa_infoViewRightScope_user (id, rightScopeUserId, rightScopeOrgId, rightScopeGroupId, rightScopeName, rightScopeIds,infouserId,domainId) SELECT "+result+", '"+po.getRightScopeUserId()+"' , '"+po.getRightScopeOrgId()+"' , '"+po.getRightScopeGroupId()+"' , '"+po.getRightScopeName()+"' , '"+po.getRightScopeIds()+"' , EMP.EMP_ID," + po.getDomainId() + "  FROM ORG_EMPLOYEE EMP  WHERE EMP.EMP_ID IN (-1" + toUserId + "-1)");

          stmt.close();
          conn.close();
           session.flush();
          } catch (Exception e) {
              result = new Long( -1);
              System.out.println("----------------------------------------------");
              e.printStackTrace();
              System.out.println("----------------------------------------------");
              throw e;
          } finally {
              session.close();
            session = null;
          }
          return result;
      }


       /**
         *删除  信息浏览范围
         * @param id String
         * @return String
         * @throws Exception
         */
       public String deleteInforViewScopePO(String ids) throws Exception {
            begin();
            String result = "0";
            try {
                if (ids != null && !ids.equals("")) {
                    session.delete(
                            " from com.whir.ezoffice.information.infomanager.po.InforViewScopePO  po where po.id in (" +
                            ids + ")");
                    java.sql.Connection conn = session.connection();
                    java.sql.Statement stmt = conn.createStatement();
                    stmt.execute("DELETE FROM oa_infoViewRightScope_user  WHERE id in ("+ids+")");
                    stmt.close();
                    conn.close();
                }
                session.flush();
            } catch (Exception e) {
                result = "-1";
                System.out.println("----------------------------------------------");
                e.printStackTrace();
                System.out.println("----------------------------------------------");
                throw e;
            } finally {
                session.close();

            }
            return result;
        }


        /**
         *修改信息浏览范围
         * @param id String
         * @return String
         * @throws Exception
         */
      public Long updateInforViewScopePO(InforViewScopePO po) throws Exception {
            begin();
            Long result = new Long( -1);
            try {
                session.update(po);
                session.flush();
            } catch (Exception e) {
                result = new Long( -1);
                System.out.println("----------------------------------------------");
                e.printStackTrace();
                System.out.println("----------------------------------------------");
                throw e;
            } finally {
                session.close();
            }
            return result;
        }


        /**
         *  load 信息浏览范围
         * @param id String
         * @return IsoBorrowUserPO
         * @throws Exception
         */
    public InforViewScopePO loadInforViewScopePO(String id) throws Exception {
            begin();
            InforViewScopePO po = null;
            try {
                po = (InforViewScopePO) session.load(InforViewScopePO.class,
                                                     new Long(id));
            } catch (Exception e) {
                System.out.println("----------------------------------------------");
                e.printStackTrace();
                System.out.println("----------------------------------------------");
            } finally {
                session.close();
            }
            return po;
        }



      public String [] getTwoOrgRight(String userId, String orgId,String readerIds,String readerNames) throws Exception {
             String resultOrg="";//"*"+orgId+"*";
             String resultName="";
             String result[]= new String[]{readerIds,readerNames};
             begin();
            try {
                 java.sql.Connection conn = session.connection();
                 java.sql.Statement stmt = conn.createStatement();
                 java.sql.ResultSet rs = null;

                 rs=stmt.executeQuery(" select po.id,po.rightScopeUserId,po.rightScopeOrgId,po.rightScopeName,po.rightScopeGroupId,po.rightScopeIds  from oa_infoViewRightScope_user po where po.infouserId = "+userId);
                Object [] obj=null;
                 while(rs.next()){
                 obj = new Object[]{""+rs.getLong(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)};
                 }
                 rs.close();
                if(obj==null||obj[5]==null||obj[5].toString().equals("")){
                   result[0]=readerIds;
                   result[1]=readerNames;
                }else if (readerIds.equals("")){
                   result[0]=obj[5].toString();
                   result[1]=obj[3].toString();
                }else{
                      List list1=getsigleString(obj[5].toString());
                      List list2=getsigleString(readerIds);
                      String obj1[]=(String [])list1.get(1);
                      String obj2[]=(String [])list2.get(1);
                    if(obj1.length<0){
                           result[0]=readerIds;
                           result[1]=readerNames;
                    }else if(obj2.length<0){
                          result[0]=obj[5].toString();
                          result[1]=obj[3].toString();
                    }else{
                        List othList=new ArrayList();
                        for(int i=0;i<obj1.length;i++){
                            boolean tb=true;
                          for(int j=0;j<obj2.length;j++){
                              rs=stmt.executeQuery("select po.orgname from org_Organization po where po.org_Id="+obj1[i]+" and po.orgIdString like '%$"+obj2[j]+"$%'");
                              boolean bb=false;
                              if(rs.next()){
                               resultOrg+="*"+obj1[i]+"*";
                               resultName+=rs.getString(1)+",";
                               bb=true;
                               tb=false;
                              }
                              rs.close();
                              if(bb){
                               break;
                              }


                          }

                          if(tb){
                              othList.add(obj1[i]);
                          }
                        }
                        if(othList!=null&&othList.size()>0){
                        for(int i=0;i<obj2.length;i++){
                           for(int j=0;j<othList.size();j++){
                             rs=stmt.executeQuery("select po.orgname from org_Organization po where po.org_Id="+obj2[i]+" and po.orgIdString like '%$"+othList.get(j)+"$%'");
                             boolean bb=false;
                             if(rs.next()){
                             resultOrg+="*"+obj2[i]+"*";
                             resultName+=rs.getString(1)+",";
                             bb=true;
                             }
                             rs.close();
                             if(bb){
                              break;
                             }

                           }
                        }
                        }

                        if(resultOrg.equals("")){
                            resultOrg="*"+orgId+"*";
                            rs=stmt.executeQuery("select po.orgname from org_Organization po where po.org_Id="+orgId);
                            if(rs.next()){
                                resultName=rs.getString(1);
                            }
                            rs.close();
                        }
                        result[0]=resultOrg;
                        result[1]=resultName;
                    }

                }
                stmt.close();
                conn.close();
            } catch (Exception e) {
                System.out.println("----------------------------------------------");
                e.printStackTrace();
                System.out.println("----------------------------------------------");
            } finally {
                session.close();
                 return result;
            }

      }

      private List  getsigleString(String rightCode1){
             List list=new ArrayList();
             String  userIdArr1[]=new String[]{};
             String  orgIdArr1[]=new String[]{};
             String  userId1 = "";
             String  orgId1 = "";
             String  group1 = "";
             char flagCode = '0';
             int nextPos = 0;
             String str = "";
             if(rightCode1 != ""){
                     for( int  i = 0; i <rightCode1.length(); i ++ ){
                             flagCode = rightCode1.charAt(i);
                             nextPos = rightCode1.indexOf(flagCode,i + 1);
                             str = rightCode1.substring(i,nextPos+1);
                             if(flagCode == '$'){
                                     userId1 = userId1 + str;
                             }else if(flagCode == '*'){
                                     orgId1 = orgId1 + str;
                             }else{
                                     group1 = group1 + str;
                             }
                             i = nextPos;
                     }
             }

           if (!userId1.equals("")) {
               if (userId1.indexOf("$") >= 0) {
                   userId1 = userId1.substring(1, userId1.length() - 1);
               }
               if (userId1.indexOf("$") >= 0) {
                   userId1 = userId1.replace('$', ',');
                   userId1 = userId1.replaceAll(",,", ",");
               }
               userIdArr1=userId1.split(",");

           }
           if (!orgId1.equals("")) {
               if (orgId1.indexOf("*") >= 0) {
                   orgId1 = orgId1.substring(1, orgId1.length() - 1);
               }
               if (orgId1.indexOf("*") >= 0) {
                   orgId1 = orgId1.replace('*', ',');
                   orgId1 = orgId1.replaceAll(",,", ",");
               }
               orgIdArr1=orgId1.split(",");
           }
           list.add(userIdArr1);
           list.add(orgIdArr1);
           return list;
      }









      /**
       *
       * @param informationId String
       * @throws Exception
       * @return List
       */
      public List getSingleInfo(String informationId) throws Exception {
          List list;
          list = null;
          begin();
          try {

              String sql="select aaa.informationTitle, aaa.informationSubTitle, aaa.informationContent, aaa.informationIssuer, aaa.informationIssueOrg, aaa.informationIssueTime,"+//0-5
                       " aaa.informationModifyTime, aaa.informationVersion, aaa.informationAuthor, aaa.informationMark, aaa.informationHeadFile, "+//6-10
                       "aaa.informationSeal, aaa.infoRedIssueOrg, aaa.infoRedIssueTime, aaa.informationSummary, aaa.informationKey,"+//11-15
                       " aaa.informationReaderName, aaa.informationReader, aaa.informationReaderOrg, aaa.informationReaderGroup, aaa.informationValidType,"+//16-20
                       "aaa.validBeginTime, aaa.validEndTime, aaa.informationHead,aaa.informationHeadId, aaa.informationSealId, "+//21-25
                       "aaa.forbidCopy,aaa.transmitToEzsite, aaa.infoDepaFlag,aaa.infoDepaFlag2, aaa.orderCode,"+//26-30
                       " aaa.displayTitle, aaa.otherChannel,aaa.titleColor,aaa.showSign,aaa.showSignName, "+//31-35
                       "aaa.modifyEmp, aaa.dossierStatus, aaa.mustRead,aaa.comeFrom,aaa.isConf,"+//36-40
                       "aaa.documentNo,aaa.documentEditor,aaa.documentType, aaa.displayImage ,aaa.afficeHistoryDate,"+//41-45
                       "aaa.wordDisplayType,bbb.channelReader,bbb.channelReaderOrg,bbb.channelReaderGroup, aaa.informationOrISODoc,"+//46-50
                       "aaa.isoDocStatus,aaa.isoOldInfoId,aaa.isoSecretStatus, aaa.isoDealCategory,aaa.isoApplyName,"+//51-55
                       "aaa.isoApplyId, aaa.isoReceiveName,aaa.isoReceiveId,aaa.isoModifyReason ,aaa.isoAmendmentPage,"+//56-60
                       "aaa.isoModifyVersion,aaa.inforModifyMen,aaa.inforModifyOrg,aaa.informationCanRemark, "+//61-64
                       "aaa.informationMailSendId,aaa.informationMailSendName,aaa.informationMailSendOrg,aaa.informationMailSendGroup,aaa.informationMailSendUserId "+//65-69 信息推送
                       " from com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb where aaa.informationId = "
                                              + informationId;

              /*Query query = session.createQuery("select aaa.informationTitle, aaa.informationSubTitle, aaa.informationContent, aaa.informationIssuer, aaa.informationIssueOrg, aaa.informationIssueTime, aaa.informationModifyTime, aaa.informationVersion, aaa.informationAuthor, aaa.informationMark, aaa.informationHeadFile, aaa.informationSeal, aaa.infoRedIssueOrg, aaa.infoRedIssueTime, aaa.informationSummary, aaa.informationKey, aaa.informationReaderName, aaa.informationReader, aaa.informationReaderOrg, aaa.informationReaderGroup, aaa.informationValidType,aaa.validBeginTime, aaa.validEndTime, aaa.informationHead,aaa.informationHeadId, aaa.informationSealId, aaa.forbidCopy,aaa.transmitToEzsite, aaa.infoDepaFlag,aaa.infoDepaFlag2, aaa.orderCode, aaa.displayTitle, aaa.otherChannel,aaa.titleColor,aaa.showSign,aaa.showSignName, aaa.modifyEmp, aaa.dossierStatus, aaa.mustRead,aaa.comeFrom,aaa.isConf,aaa.documentNo,aaa.documentEditor,aaa.documentType, aaa.displayImage ,aaa.afficeHistoryDate,aaa.wordDisplayType,bbb.channelReader,bbb.channelReaderOrg,bbb.channelReaderGroup, aaa.informationOrISODoc,aaa.isoDocStatus,aaa.isoOldInfoId,aaa.isoSecretStatus, aaa.isoDealCategory,aaa.isoApplyName,aaa.isoApplyId, aaa.isoReceiveName,aaa.isoReceiveId,aaa.isoModifyReason ,aaa.isoAmendmentPage,aaa.isoModifyVersion,aaa.inforModifyMen,aaa.inforModifyOrg  from com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb where aaa.informationId = "
                                                + informationId);*/

              Query query = session.createQuery(sql);
              list = query.list();
          } catch (Exception e) {
              System.out.println("----------------------------------------------");
              e.printStackTrace();
              System.out.println("----------------------------------------------");
              throw e;
          } finally {
              session.close();
              session = null;
              transaction = null;

          }
          return list;
      }



      /**
     * 返回用户可以修改的ID串 ,12,34,56,
     * @param channelId String
     * @param userId String
     * @param orgId String
     * @param orgIdString String
     * @param inforIds String
     * @param rightList List
     * @throws Exception
     * @return StringgetInformationModiIds_notChannel
     */
    public String getInforModiIdsNotChannel( String userId,
                                        String orgId, String orgIdString,
                                        String inforIds, List rightList) throws Exception {
        StringBuffer buffer = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        String where = "";
        begin();
        try {
            orgIdString = (buffer.append("$").append(orgIdString).append("$")).
                          toString();
            String[] orgIdArray = orgIdString.split("\\$\\$");

            //判段用户所在的组是否在维护人组里
            //取用户所在的组ID
            List list = session.createQuery("select po.groupId from com.whir.org.vo.groupmanager.GroupVO po join po.employees emp where emp.empId=" +
                                            userId).list();
            int i;
            buffer = new StringBuffer(" where (");
            for (i = 0; list != null && i < list.size(); i++) {
                buffer.append(" po.channelManagerGroup like '%@").append(
                        list.get(i)).append("%@' or ");
            }
            for (i = 0; i < orgIdArray.length; i++) {
                if (!"".equals(orgIdArray[i]))
                    buffer.append(" po.channelManagerOrg like '%*").append(
                            orgIdArray[i]).append("*%' or ");
            }

            //兼职组织 王国良 2009-10-28
             List sideOrgList = session.createQuery("select eee.sidelineOrg from com.whir.org.vo.usermanager.EmployeeVO eee where eee.empId=" +
                     userId).list();
             if (sideOrgList != null && sideOrgList.size() > 0) {
                 String sideLineOrg = (String) sideOrgList.get(0);
                 if (sideLineOrg != null && !sideLineOrg.equals("") &&
                     sideLineOrg.length() > 2) {
                     sideLineOrg = sideLineOrg.substring(1,
                             sideLineOrg.length() - 1);
                     String[] sarr = sideLineOrg.split("\\*\\*");
                     if (sarr != null && sarr.length > 0) {
                         for (int ii = 0; ii < sarr.length; ii++) {
                             buffer.append(" po.channelManagerOrg like '%*").append(
                           sarr[ii]).append("*%' or ");
                         }
                     }
                 }
             }

            buffer.append(" po.channelManager like '%$").append(userId).append(
                    "$%')");
            int num = ((Integer) session.createQuery("select count(po.channelId) from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po " +
                    buffer.toString()).iterate().next()).intValue();
            if (num > 0) {
                //用户在维护人范围内，检索用户可以修改的信息ID

                if(rightList!=null && rightList.size()>0){
                    Object[] obj = (Object[]) rightList.get(0);
                    String scopeType = obj[0].toString();
                    if ("0".equals(scopeType)) {
                        //可以维护全部数据
                        where = " 1=1 ";
                    } else if ("1".equals(scopeType)) {
                        //可以维护本人的数据
                        where = "po.informationIssuerId=" + userId;
                    } else if ("2".equals(scopeType)) {
                        //可以维护本组织及下级组织的数据
                        String orgRange = getAllJuniorOrgIdByRange("*" + orgId +
                                "*");
                        if (orgRange.indexOf("a") > 0) {
                            String[] tmp = orgRange.split("a");
                            for (int k = 0; k < tmp.length; k++) {
                                where += "po.informationIssueOrgId in(" + tmp[k] +
                                        ") or ";
                            }
                            if (where.endsWith("or ")) {
                                where = where.substring(0, where.length() - 3);
                            }
                        } else {
                            where = "po.informationIssueOrgId in(" + orgRange +
                                    ") ";
                        }
                        //silensColin 2009-11-2
                        if (sideOrgList != null && sideOrgList.size() > 0) {
                            String sideLineOrg = (String) sideOrgList.get(0);
                            if (sideLineOrg != null && !sideLineOrg.equals("") &&
                                sideLineOrg.length() > 2) {
                                sideLineOrg = sideLineOrg.substring(1,
                                        sideLineOrg.length() - 1);
                                sideLineOrg.replaceAll("\\*", ",");
                                sideLineOrg.replaceAll(",,", ",");
                                where += " or po.informationIssueOrgId in(" + sideLineOrg +
                                    ") ";

                            }
                        }


                        //where = fieldOrg + " in(" + orgRange + ") ";
                    } else if ("3".equals(scopeType)) {
                        //可以维护本组织的数据
                        where = " po.informationIssueOrgId=" + orgId;
                    } else {
                        //scopeType==4 维护定义的范围
                        String orgRange = getAllJuniorOrgIdByRange((String) obj[1]);
                        if ("".equals(orgRange)) {
                            where = "1>2";
                        } else {
                            if (orgRange.indexOf("a") > 0) {
                                String[] tmp = orgRange.split("a");
                                for (int k = 0; k < tmp.length; k++) {
                                    where += "po.informationIssueOrgId in(" + tmp[k] +
                                            ") or ";
                                }
                                if (where.endsWith("or ")) {
                                    where = where.substring(0, where.length() - 3);
                                }
                            } else {
                                where = "po.informationIssueOrgId in(" + orgRange +
                                        ") ";
                            }
                        }

                    }

                    if(!"".equals(where)){

                        where =
                                "select po.informationId from com.whir.ezoffice.information.infomanager.po.InformationPO po where po.informationId in (" +
                                inforIds + ") and (" + where + ")";

                        //System.out.println(" informationEjb where:"+where);
                        list = session.createQuery(where).list();
                        buffer2 = new StringBuffer();
                        if (list != null) {
                            for (i = 0; i < list.size(); i++) {
                                buffer2.append(",").append(list.get(i));
                            }
                            buffer2.append(",");
                        }
                    }
                }
            }
            session.close();
        } catch (Exception ex) {
            buffer2 = new StringBuffer();
            session.close();
            System.out.println("where:"+where);
            ex.printStackTrace();
            throw ex;
        } finally {
            return buffer2.toString();
        }

    }




    public List listInformationClass(String domainId) throws Exception {
        List list = new ArrayList();
        begin();
        String userchannelSql="";
        String orgSql="";
        try {
            String hSql = "select aaa.channelId,aaa.channelName,aaa.channelIdString,aaa.channelType,aaa.userDefine,aaa.channelShowType,aaa.channelNameString from " +
                          "com.whir.ezoffice.information.channelmanager.po.InformationChannelPO aaa" +
                          " where (aaa.afficheChannelStatus is null or aaa.afficheChannelStatus='0') and aaa.domainId=" + domainId +
                          " order by aaa.channelType,aaa.channelIdString";

            Query query = session.createQuery(hSql);
            list = query.list();
            Iterator orgIter = null;
            //频道ID
            StringBuffer userChannelNameBuffer=new StringBuffer();
            StringBuffer    userChannelName_temp=new StringBuffer();
            //组织id
            StringBuffer orgNameBuffer=new StringBuffer();
            StringBuffer   orgName_temp=new StringBuffer();
            //查ID
            for (int i = 0; i < list.size(); i++) {
             Object[] obj = (Object[]) list.get(i);
             if (obj[3].toString().equals("0")) {//信息管理

              } else if (obj[4] != null && "1".equals(obj[4].toString())) {//自定义频道
                  //之前没有的才加进来
                  if(userChannelName_temp.toString().indexOf(","+obj[3]+",")<0){
                      userChannelName_temp.append(",").append(obj[3]).append(",");
                      userChannelNameBuffer.append(obj[3]).append(",");
                  }

              } else {//单位主页
                  //之前没有的才加进来
                  if(orgName_temp.toString().indexOf(","+obj[3]+",")<0){
                      orgName_temp.append(",").append(obj[3]).append(",");
                      orgNameBuffer.append(obj[3]).append(",");
                  }
              }
           }

           // 自定义频道
           Map userChannelMap=new HashMap();
           Map orgMap=new HashMap();
           String userChannelNameBuffer_toString=userChannelNameBuffer.toString();

            Object tempobj[]=null;
           if(userChannelNameBuffer_toString.length()>1){
               userchannelSql="select aaa.userChannelId,aaa.userChannelName from com.whir.ezoffice.information.channelmanager.po.UserChannelPO aaa where aaa.userChannelId in (" +
                                              userChannelNameBuffer_toString.substring(0,userChannelNameBuffer_toString.length()-1)+")";
                query = session.createQuery(userchannelSql);
               orgIter = query.iterate();
               while(orgIter.hasNext()){
                 tempobj= (Object [])orgIter.next();
                 userChannelMap.put(tempobj[0].toString(),tempobj[1]);
               }
           }
           //组织
           String orgNameBuffer_toString=orgNameBuffer.toString();
           if(orgNameBuffer_toString.length()>1){
               orgSql="select aaa.orgId,aaa.orgNameString from com.whir.org.vo.organizationmanager.OrganizationVO aaa where aaa.orgId in (" +
                                               orgNameBuffer_toString.substring(0,orgNameBuffer_toString.length()-1)+")";
                query = session.createQuery(orgSql);
               orgIter = query.iterate();
               while (orgIter.hasNext()) {
                   tempobj = (Object[]) orgIter.next();
                   orgMap.put(tempobj[0].toString(), tempobj[1]);
               }

          }

          //改obj[2]值
            for (int i = 0; i < list.size(); i++) {
                String channelNameString = "";
                Object[] obj = (Object[]) list.get(i);
                if (obj[3].toString().equals("0")) {
                    channelNameString = "信息管理.";
                } else if (obj[4] != null && "1".equals(obj[4].toString())) {
                         channelNameString=userChannelMap.get(obj[3].toString())+"";
                } else {
                         channelNameString=orgMap.get(obj[3].toString())+"";
                }
                obj[2] = channelNameString+=obj[6];
                list.set(i, obj);
            }

        } catch (HibernateException e) {
            e.printStackTrace();
            System.out.println("userchannelSql:"+userchannelSql);
            System.out.println("orgSql:"+orgSql);
            throw e;
        } finally {
            session.close();
            session = null;

        }
        return list;
    }


    /**
     * 根据人员id串+组织id串+群组id串 取出  $userId$$userId$$userId$$userId$$userId$
     * @param userIds String
     * @throws Exception
     * @return String
     */
    public String  getUserIdsForEmailByThree(String userIds) throws Exception {
     String resultStr="";
      begin();
      try {
          java.sql.Connection conn = session.connection();
          java.sql.Statement stmt = conn.createStatement();
          java.sql.ResultSet rs = null;
          char flagCode = '0';
          int nextPos = 0;
          String str = "";
          String userId = "", orgId = "", groupId = "";
          String outOrgId = "";
          for (int i = 0; i < userIds.length(); i++) {
              flagCode = userIds.charAt(i);
              nextPos = userIds.indexOf(flagCode, i + 1);
              str = userIds.substring(i, nextPos + 1);
              if (flagCode == '$') {
                  userId = userId + str;
              }
              else if (flagCode == '*') {
                  orgId = orgId + str;
              }
              else {
                  groupId = groupId + str;
              }
              i = nextPos;
          }
          if (!userId.equals("")) {
        	  if (userId.indexOf("$") >= 0) {
                  userId = userId.replace('$', ',');
                  userId = userId.replaceAll(",,", ",");
              }
        	  /***-----------20151109 -by jqq 去掉禁用用户       start 
        	   	验证发现：禁用用户在选择组织用户时候，不在查询结果中，因此用户这部分可以不处理，只对组织/群组情况下处理
        	  String[] userIdArray_tmp = userId.substring(1, userId.length()).split(",");
	       	  StringBuffer idBuffer = new StringBuffer("");
	       	  for(int x = 0; x < userIdArray_tmp.length; x ++){
	       		  idBuffer.append("'" + userIdArray_tmp[x] +"',");
	       	  }
	       	  System.out.println("===================userId:" + idBuffer.toString());
        	  String strtmp = "";
    		  StringBuffer userIdtmp = new StringBuffer("");
    		  rs = stmt
              .executeQuery("SELECT C.emp_id FROM  org_employee C  WHERE C.userisactive != '0' AND C.emp_id IN ("
                            +idBuffer.toString().substring(0, userId.length()) +")");
              while (rs.next()) {
            	  strtmp = rs.getString(1);
                  if (userIdtmp.indexOf("," + strtmp + ",") < 0) {
                	  userIdtmp.append(strtmp + ",");
                  }
              }
        	  userId = "," + userIdtmp.toString();
        	  /***-----------20151109 -by jqq 去掉禁用用户      end---------------***/
          } else {
              userId = ",-1,";
          }


          StringBuffer toUserId = new StringBuffer(userId);
          if (!groupId.equals("")) {
              if (groupId.indexOf("@") >= 0) {
                  groupId = groupId.substring(1, groupId.length() - 1);
              }
              if (groupId.indexOf("@") >= 0) {
                  groupId = groupId.replace('@', ',');
                  groupId = groupId.replaceAll(",,", ",");
              }
              rs = stmt
                  .executeQuery("SELECT EMP_ID FROM ORG_USER_GROUP WHERE GROUP_ID IN ("
                                + groupId + ")");
              String tmp = "";
              while (rs.next()) {
                  tmp = rs.getString(1);
                  if (toUserId.indexOf("," + tmp + ",") < 0) {
                      toUserId.append(tmp + ",");
                  }
              }
              rs.close();
          }

          if (!orgId.equals("")) {
              if (orgId.indexOf("*") >= 0) {
                  orgId = orgId.substring(1, orgId.length() - 1);
              }
              if (orgId.indexOf("*") >= 0) {
                  orgId = orgId.replace('*', ',');
                  orgId = orgId.replaceAll(",,", ",");
              }
              String[] tmpStr = {orgId};
              // System.out.println("colin orgId: "+orgId);
              if (orgId.indexOf(",") > 0) {
                  tmpStr = orgId.split(",");
              }
              String whereSql = "";
              String whereSql2 = "";
              for (int i = 0; i < tmpStr.length; i++) {
                  whereSql += "A.ORGIDSTRING LIKE '%$" + tmpStr[i]
                      + "$%' OR ";
                  whereSql2 += " C.SIDELINEORG  like '%*" + tmpStr[i]
                      + "*%' OR ";
              }
              whereSql2 += " 1>1 ";
              whereSql += " 1 > 1 ";
              rs = stmt
                  .executeQuery("SELECT B.emp_id FROM ORG_ORGANIZATION A, ORG_ORGANIZATION_USER B  WHERE   A.ORG_ID=B.ORG_ID   AND ("
                                + whereSql + ")");
              String tmp = "";
              while (rs.next()) {
                  tmp = rs.getString(1);
                  if (toUserId.indexOf("," + tmp + ",") < 0) {
                      toUserId.append(tmp + ",");
                  }
              }
              rs.close();

              String tmp2 = "";
              rs = stmt
                  .executeQuery("SELECT C.emp_id FROM  org_employee C  WHERE "
                                + whereSql2);
              while (rs.next()) {
                  tmp2 = rs.getString(1);
                  if (toUserId.indexOf("," + tmp2 + ",") < 0) {
                      toUserId.append(tmp2 + ",");
                  }
              }
              rs.close();
          }



           if(toUserId!=null&&!toUserId.toString().equals("")){
        	   resultStr=toUserId.toString().replaceAll(",",",,");
               resultStr=resultStr.replace(',', '$');
               resultStr=resultStr.substring(1,resultStr.length()-1);
           }
          stmt.close();
          conn.close();

      }
      catch (Exception e) {
          System.out
              .println("----------------------------------------------");
          e.printStackTrace();
          System.out
              .println("----------------------------------------------");

          throw e;
      }
      finally {
          session.close();
          session = null;

      }
      return resultStr;
  }


  /**
   * 根据信息id  查找信息的栏目等
   * @param informationId String
   * @throws Exception
   * @return String[]
   */
  public String[] getInfor_channel_type(String informationId)
			throws Exception {
		List list;
		list = null;
		begin();
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		String sql = "";
		ResultSet rs = null;
		String[] resutlobj = new String[] { "" };
		try {
			String channelId = "";
			String channelName = "";
			String informationType = "";
			String channelType = "";
			String userDefine = "";
			String content = "";
			String userChannelName = "";
			String informationtitle = "";
			conn = session.connection();
			stmt = conn.createStatement();

			sql = "  SELECT aa.channel_id ,bb.channelname,aa.informationtype ,bb.Channeltype,bb.userDefine,case when aa.informationtype=2 or  aa.informationtype=3 then  aa.informationcontent end,  aa.informationtitle  from   OA_INFORMATION aa ,OA_INFORMATIONCHANNEL bb"
					+ "  WHERE  aa.Channel_Id=bb.Channel_Id and  aa.information_id="
					+ informationId;

			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				channelId = rs.getString(1) + "";
				channelName = rs.getString(2) + "";
				informationType = rs.getString(3) + "";
				channelType = rs.getString(4) + "";
				userDefine = rs.getString(5) + "";
				content = rs.getString(6) + "";
				informationtitle = rs.getString(7) + "";
			}
			if (channelType.equals("0")) {// 信息管理
				userChannelName = "信息管理";
			} else if (userDefine != null && "1".equals(userDefine)) {// 自定义频道
				userChannelName = session.createQuery(
								"select po.userChannelName from com.whir.ezoffice.information.channelmanager.po.UserChannelPO po where po.userChannelId="
										+ channelType).iterate().next().toString();

			} else {// 单位主页
				userChannelName = session.createQuery(
								"select po.orgName from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgId="
										+ channelType).iterate().next().toString();
			}

			resutlobj = new String[] { channelId, channelName, informationType,
					channelType, userDefine, content, userChannelName,
					informationtitle };

		} catch (Exception e) {
			System.out
					.println("----------------------------------------------");
			e.printStackTrace();
			System.out.println("error sql :" + sql);
			System.out
					.println("----------------------------------------------");
			throw e;
		} finally {
			session.close();
			session = null;
			transaction = null;

		}
		return resutlobj;
	}

  /**
	 * 取公文同步到信息的信息对象数组
	 * 
	 * @param infoId
	 * @return
	 * @throws Exception
	 */
    public List getFromGOVInfo(String[] infoId) throws Exception {
	    List list = new ArrayList();
	    begin();
	    try {
	    	StringBuffer stringBuffer = new StringBuffer();
	    	for(int i=0;i<infoId.length;i++){
	    		stringBuffer.append(infoId[i]+",");
	    	}
	    	String infoIds = stringBuffer.substring(0, stringBuffer.length()-1);
	    	list = (List) session.createQuery("select po.fromGOVDocument,po.fromGOV from com.whir.ezoffice.information.infomanager.po.InformationPO po where po.fromGOVDocument > 0 and po.fromGOVDocument is not null and po.informationId in ("+infoIds+")").list();
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
	    return list;
    }

    /**
     * 更新信息历史版本修改备注
     * @param historyId
     * @param historyMark
     * @throws Exception
     */
    public void updateHistory(String historyId,String historyMark) throws Exception {
    	begin();
    	try {
			InformationHistoryPO po = (InformationHistoryPO) session.load(InformationHistoryPO.class, new Long(historyId));
			po.setHistoryMark(historyMark);
			session.update(po);
			session.flush();
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
    }

    /**
     * 取用户可查看信息的hql
     * @param userId
     * @param orgId
     * @return
     * @throws Exception
     */
    public String getUserViewInfo(String userId,  String orgId) throws Exception {
    	begin();
    	String hql = "";
    	try {
    		//取用户组织的所有上级组织(包括自身)
            String orgSql = "";
            String databaseType = com.whir.common.config.SystemCommon.
                                  getDatabaseType();
            if (databaseType.indexOf("mysql") >= 0) {
            	orgSql = "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                         " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                         orgId + ") like concat('%$', aaa.orgId, '$%') ";
            }else if (databaseType.indexOf("db2") >= 0) {
            	orgSql = "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                         " where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$'),(select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                         orgId + "))>0 ";
            } else {
            	orgSql = "select aaa.orgId from com.whir.org.vo.organizationmanager.OrganizationVO aaa " +
                         " where (select bbb.orgIdString from com.whir.org.vo.organizationmanager.OrganizationVO bbb where bbb.orgId = " +
                         orgId + ") like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(aaa.orgId)), '$%') ";
            }
            Query query = session.createQuery(orgSql);
            List orgList = query.list();
            String orgString = ""; //上级组织ID串(包括自身)
            for (int i = 0; i < orgList.size(); i++) {
                orgString = orgString + " aaa.informationReaderOrg like '%*" + orgList.get(i) + "*%' or ";
            }
            String groupString = ""; //组ID串
            query = session.createQuery("select aaa.groupId from com.whir.org.vo.groupmanager.GroupVO aaa join aaa.employees bbb where bbb.empId = " +
                                        userId);
            List groupList = query.list();
            for (int i = 0; i < groupList.size(); i++) {
                groupString = groupString + " aaa.informationReaderGroup like '%@" + groupList.get(i) + "@%' or ";
            }
            hql = orgString + groupString;
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
		return hql;
    }
    
    /**
     * 修改页面保存信息PO的正在编辑用户时调用
     * @param po
     */
    public void save(InformationPO po) throws Exception {
    	begin();
    	try {
			session.update(po);
			session.flush();
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
    }
    
    /**
     * 根据信息ID和权限sql判断用户权限
     * @param informationId
     * @param scope
     * @return
     * @throws Exception 
     */
    public String hasRight(String informationId, String rightScope) throws Exception{
    	String result = "0";
    	begin();
    	try {
			String hql = "select aaa.informationId from com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
					"where aaa.informationId = " + informationId + " and (" + rightScope + ")";
			//System.out.println("-------------hql:"+hql);
			List list = session.createQuery(hql).list();
			if(list!=null && list.size()>0){
				result = "1";
			}
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
    	return result ;
    }
    
    /**
     * 判断用户对某条信息的打印次数，若扔可打印，则直接插入数据（更新打印次数）
     * @param userId
     * @param informationId
     * @return
     */
    public String userPrintNum(String userId, String informationId) throws Exception{
    	String result = "";
    	begin();
    	InformationPrintPO po = null;
    	try {
			String hql = "select aaa from com.whir.ezoffice.information.infomanager.po.InformationPrintPO aaa " +
					"where aaa.informationId = " + informationId + " and aaa.userId = " + userId;
			List list = session.createQuery(hql).list();
			Date date = new Date();
			if(list!=null && list.size()>0){
				po = (InformationPrintPO) list.get(0);
				long userNum = po.getPrintNum();
				hql = "select aaa.printNum from com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
						"where aaa.informationId = " + informationId ;
				String num = (String) session.createQuery(hql).iterate().next();
				num = (num==null || num.equals(""))?"0":num;
				if(num.equals("0") || userNum < Long.valueOf(num)){
					po.setPrintNum(++userNum);
					po.setPrintTime(date);
					session.update(po);
					session.flush();
					result = "1";
				}else{
					result = "0";
				}
			}else{
				po = new InformationPrintPO();
				po.setInformationId(Long.valueOf(informationId));
				po.setUserId(Long.valueOf(userId));
				po.setPrintNum(1l);
				po.setPrintTime(date);
				po.setDomainId(0l);
				session.save(po);
				session.flush();
				result = "1";
			}
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
    	return result ;
    }
    
    /**
     * 判断用户对某条信息中的附件的下载次数是否已达上限
     * @param userId
     * @param informationId
     * @return
     */
    public String userDownloadNum(String userId, String informationId, String accessorySaveName) throws Exception {
    	String result = "0";
    	begin();
    	InformationDownloadPO po = null;
    	try {
			String hql = "select aaa from com.whir.ezoffice.information.infomanager.po.InformationDownloadPO aaa " +
					"where aaa.informationId = " + informationId + " and aaa.userId = " + userId + " and aaa.accessorySaveName = '" + accessorySaveName + "'";
			List list = session.createQuery(hql).list();
			if(list!=null && list.size()>0){
				po = (InformationDownloadPO) list.get(0);
				long userNum = po.getDownloadNum();
				hql = "select aaa.downLoadNum from com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
						"where aaa.informationId = " + informationId ;
				String num = (String) session.createQuery(hql).iterate().next();
				num = (num==null || num.equals(""))?"0":num;
				if(num.equals("0") || userNum < Long.valueOf(num)){
					result = "1";
				}
			}else{
				result = "1";
			}
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
    	return result ;
    }
    
    /**
     * 更新用户下载附件的次数
     * @param userId
     * @param informationId
     * @param accessorySaveName 1-N个附件保存名
     * @return
     * @throws Exception 
     */
    public String updateDownloadNum(String userId, String informationId, String accessorySaveName) throws Exception{
    	String result = "0";
    	begin();
    	try {
    		String[] array = accessorySaveName.split("\\|");
    		Date date = new Date();
    		for(int i=0; i<array.length; i++){
    			String fileName = array[i];
    			if(fileName!=null && !"".equals(fileName)){
    				if(informationId==null||"".equals(informationId)){
    					String sql = "select aaa.information.informationId from com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO aaa " +
    						"where aaa.accessorySaveName = '" + fileName + "'";
    					informationId = (String) session.createQuery(sql).iterate().next();
    				}
    				InformationDownloadPO po = null;
    				String hql = "select aaa from com.whir.ezoffice.information.infomanager.po.InformationDownloadPO aaa " +
						"where aaa.informationId = " + informationId + " and aaa.userId = " + userId + " and aaa.accessorySaveName = '" + fileName + "'";
					List list = session.createQuery(hql).list();
					//取信息的可下载次数
					hql = "select aaa.downLoadNum from com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
						"where aaa.informationId = " + informationId;
					List list1 = session.createQuery(hql).list();
					String downLoadNum = "";
					if(list1!=null && list1.size()>0){
						downLoadNum = (list1.get(0)!=null && !((String)list1.get(0)).equals(""))?(String)list1.get(0):"";
					}
					if(!downLoadNum.equals("")){
						long hasDownloadNum = 0l;//已下载次数
						if(list!=null && list.size()>0){
							po = (InformationDownloadPO) list.get(0);
							hasDownloadNum = po.getDownloadNum();
							if(hasDownloadNum >= Long.valueOf(downLoadNum)){
								result = "-1";
							}else{
								po.setDownloadNum(hasDownloadNum+1);
								po.setDownloadTime(date);
								session.update(po);
								session.flush();
								result = "1";
							}
						}else{
							po = new InformationDownloadPO();
							po.setUserId(Long.valueOf(userId));
							po.setInformationId(Long.valueOf(informationId));
							po.setAccessorySaveName(fileName);
							po.setDownloadNum(1l);
							po.setDownloadTime(date);
							po.setDomainId(0l);
							session.save(po);
							session.flush();
							result = "1";
						}
					}else{
						result = "1";
					}
    			}
    		}
		} catch (Exception e) {
			System.out.println("----------------------------------------------");
	        e.printStackTrace();
	        System.out.println("----------------------------------------------");
	        throw e;
		} finally {
			session.close();
	        session = null;
	        transaction = null;
		}
    	return result ;
    }
    
    /**
     * 信息点赞功能
     * @param praiseUserId 点赞用户Id
     * @param praiseInformationId 点赞信息Id
     * @param praiseType 点赞类型
     * @throws HibernateException 
     */
    public String updateInfoPraise(String praiseUserId, String praiseInformationId, String praiseType) throws Exception {
    	begin();
        String flag = "0";
        //20160511 -by jqq 信息点赞改造，增加取消点赞功能
        if("1".equals(praiseType)){
            StringBuffer sql_praise = new StringBuffer();
            sql_praise.append("select po.praiseId from com.whir.ezoffice.information.infomanager.po.InformationPraisePO po ")
              .append("where po.praiseUserId=:praiseUserId and po.praiseType =:praiseType and po.praiseInformationId=:praiseInformationId");
            Query query_praise = this.session.createQuery(sql_praise.toString());
            query_praise.setParameter("praiseUserId", praiseUserId);
            query_praise.setParameter("praiseType", praiseType);
            query_praise.setParameter("praiseInformationId", praiseInformationId);
            List list_praise = query_praise.list();
            //点赞时，如果已经存在点赞记录，说明操作异常
            if ((list_praise != null) && (list_praise.size() > 0)) {
              flag = "3";
              return flag;
            }
            InformationPraisePO praisePO = new InformationPraisePO();
            praisePO.setPraiseUserId(Long.valueOf(praiseUserId));
            praisePO.setPraiseInformationId(Long.valueOf(praiseInformationId));
            praisePO.setPraiseType(Long.valueOf(praiseType));
            try
            {
              this.session.save(praisePO);

              InformationPO po = (InformationPO)this.session.load(InformationPO.class, Long.valueOf(praiseInformationId));
              String num = po.getPraiseNum() == null || "".equals(po.getPraiseNum()) ? "0" : po.getPraiseNum();
              //点赞，对应信息总赞次数加1
              int praiseNum = Integer.parseInt(num) + 1;
              po.setPraiseNum(String.valueOf(praiseNum));    
              this.session.update(po);
              this.session.flush();
              flag = "1";
            } catch (Exception e) {
              System.out.println("修改点赞次数异常！");
              flag = "0";
              e.printStackTrace();
            } finally {
              this.session.close();
              this.session = null;
              this.transaction = null;
            }
        }else if("0".equals(praiseType)){
            StringBuffer sql_praise = new StringBuffer();
            sql_praise.append("select po.praiseId from com.whir.ezoffice.information.infomanager.po.InformationPraisePO po ")
              .append("where po.praiseUserId=:praiseUserId and po.praiseType =:praiseType and po.praiseInformationId=:praiseInformationId");
            Query query_praise = this.session.createQuery(sql_praise.toString());
            query_praise.setParameter("praiseUserId", praiseUserId);
            query_praise.setParameter("praiseType", "1");
            query_praise.setParameter("praiseInformationId", praiseInformationId);
            List list_praise = query_praise.list();
            //如果点击取消赞，而当前用户对该信息点赞的记录，则表明操作异常
            if ((list_praise == null) || (list_praise.size() == 0)) {
              flag = "3";
              return flag;
            }
            try {
                String praiseId = list_praise.get(0).toString();
                InformationPraisePO praise_po = (InformationPraisePO) this.session.load(
                    InformationPraisePO.class,
                    Long.valueOf(praiseId));
                this.session.delete(praise_po);
                InformationPO po = (InformationPO) this.session.load(
                    InformationPO.class,
                    Long.valueOf(praiseInformationId));
                String num = po.getPraiseNum() == null || "".equals(po.getPraiseNum()) ? "0" : po.getPraiseNum();
                //取消赞，对于信息的点赞总数减去1
                int praiseNum = Integer.parseInt(num) - 1;
                po.setPraiseNum(String.valueOf(praiseNum));
                this.session.update(po);
                this.session.flush();
                flag = "1";
            } catch (Exception e) {
                System.out.println("修改点赞次数异常！");
                flag = "0";
                e.printStackTrace();
            } finally {
                this.session.close();
                this.session = null;
                this.transaction = null;
            }
        }
        
        return flag;
    }
}


