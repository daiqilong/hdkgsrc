package com.whir.ezoffice.information.infomanager.bd;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.CommonUtils;
import com.whir.common.util.EJBProxy;
import com.whir.ezoffice.information.channelmanager.bd.ChannelBD;
import com.whir.ezoffice.information.common.util.InformationEJBProxy;
import com.whir.common.util.ParameterGenerator;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.ezoffice.information.infomanager.ejb.InformationEJBBean;
import com.whir.ezoffice.information.infomanager.ejb.InformationEJBHome;
import com.whir.ezoffice.information.infomanager.po.InformationBrowserPO;
import com.whir.ezoffice.information.infomanager.po.InformationPO;
import com.whir.ezoffice.information.infomanager.po.InformationPrintPO;
import com.whir.ezoffice.information.infomanager.po.InformationViewRecordPO;

import java.util.ArrayList;
import com.whir.ezoffice.information.infomanager.po.InforViewScopePO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.whir.ezoffice.information.infomanager.wangzha.WangzhaInfoVO;
import com.whir.ezoffice.information.infomanager.wangzha.WangzhaTools;
import com.whir.ezoffice.message.action.ModelSendMsg;
import com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD;
import com.whir.govezoffice.documentmanager.bd.SenddocumentBD;
import com.whir.integration.realtimemessage.Realtimemessage;
import com.whir.org.bd.usermanager.UserBD;

public class InformationBD extends HibernateBase {

    private static Logger logger = Logger.getLogger(InformationBD.class.getName());

    public InformationBD() {
    }

	/**
	 * 是否同步OA信息
	 * @return boolean
	 */
	public boolean isSynchOAInfo() {
		boolean flag = false;
//		String filename = "/ezsite.properties";
//		Properties prop = new Properties();
//		InputStream in;
		try {
//			in = this.getClass().getResourceAsStream(filename);
//			prop.load(in);
//			in.close();
//			//读取SynchOAInfo属性
//			if (prop != null &&
//				prop.get("SynchOAInfo") != null &&
//				"true".equals(((String)prop.get("SynchOAInfo")).trim().toLowerCase())) {
//				flag = true;
//				System.out.println("\n*******************OA信息同步配置属性(SynchOAInfo)读取成功！******************\n");
//			}
			//------------------------------------------------------------------
			int i = 0;
			String domainId = "0";
			javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase().getDataSource();
			java.sql.Connection conn = null;
			java.sql.Statement stmt = null;
			java.sql.ResultSet rs = null;
			try {
				conn = ds.getConnection();
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT DOMAIN_ID FROM ORG_DOMAIN");
				if (rs.next()) {
					i++;
					domainId = rs.getString(1);
				}
			} catch (Exception ex) {
				if (conn != null) {
					try {
						conn.close();
					} catch (Exception e) {}
				}
			} finally {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}

			String options=com.whir.org.common.util.SysSetupReader.getInstance().getSystemOption(domainId);
			//System.out.println("options----->"+options);
			//是否与ezSITE结合
			if (options.charAt(6) == '1') {
				flag = true;
			}
			//------------------------------------------------------------------
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			logger.error("error to isSynchOAInfo :" + e.getMessage());
		}
		//System.out.println("flag----->"+flag);
		return flag;
	}


    /**
     * 新增信息
     * @param informationPO InformationPO
     * @return boolean
     */
    public Long add(InformationPO informationPO,
                    String[] para,
                    String[] assoInfo,
                    String[] infoPicName,
                    String[] infoPicSaveName,
                    String[] infoAppendName,
                    String[] infoAppendSaveName,
                    String domainId) {
        Long result = new Long(0);
        try {
            ParameterGenerator pg = new ParameterGenerator(8);
            pg.put(informationPO, InformationPO.class);
            pg.put(para, java.lang.String[].class);
            pg.put(assoInfo, java.lang.String[].class);
            pg.put(infoPicName, String[].class);
            pg.put(infoPicSaveName, String[].class);
            pg.put(infoAppendName, String[].class);
            pg.put(infoAppendSaveName, String[].class);
            pg.put(domainId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (Long) ejbProxy.invoke("save", pg.getParameters());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("error to add information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    public Long add(InformationPO informationPO,
                   String[] para,
                   String[] assoInfo,
                   String[] infoPicName,
                   String[] infoPicSaveName,
                   String[] infoAppendName,
                   String[] infoAppendSaveName,
                   String domainId,HttpServletRequest request) {
       Long result = new Long(0);
       try {
           ParameterGenerator pg = new ParameterGenerator(8);
           pg.put(informationPO, InformationPO.class);
           pg.put(para, java.lang.String[].class);
           pg.put(assoInfo, java.lang.String[].class);
           pg.put(infoPicName, String[].class);
           pg.put(infoPicSaveName, String[].class);
           pg.put(infoAppendName, String[].class);
           pg.put(infoAppendSaveName, String[].class);
           pg.put(domainId, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           result = (Long) ejbProxy.invoke("save", pg.getParameters());

       } catch (Exception e) {
           e.printStackTrace();
           logger.error("error to add information :" + e.getMessage());
       } finally {
           return result;
       }
   }


    /**
     * 当流程成为写字段是UPDATE
     * @param informationPO InformationPO
     *
     */

    /**
     * 取单条信息（标题、副标题、内容、发布人、发布组织、发布时间、最后修改时间、版本、作者）
     * @param informationId String
     * @return List
     */
    public List getSingleInfo(String informationId,String channelId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(informationId, java.lang.String.class);
            pg.put(channelId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getSingleInfo", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getSingleInfo information :" + e.getMessage());
        } finally {
            return list;
        }
    }


    public List getOrgName(String channelId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(channelId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getOrgName", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getOrgName information :" + e.getMessage());
        } finally {
            return list;
        }
    }


    public List getAllOrgName(String flag) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(flag, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getAllOrgName", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getAllOrgName information :" + e.getMessage());
        } finally {
            return list;
        }
    }


    public boolean informationStatus(String informationId) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("informationStatus", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("error to getSingleInfo information :" + e.getMessage());
        } finally {
            return result;
        }
    }


    public boolean  setInformationStatus(String informationId,String status) {
       boolean result = false;
       try {
           ParameterGenerator pg = new ParameterGenerator(2);
           pg.put(informationId, java.lang.String.class);
           pg.put(status, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           ejbProxy.invoke("setInformationStatus", pg.getParameters());
           result = true;
       } catch (Exception e) {
           logger.error("error to setInformationStatus information :" + e.getMessage());
       } finally {
           return result;
       }
   }


    public List getchannleinfo(String informationId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getchannleinfo", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getchannleinfo information :" + e.getMessage());
        } finally {
            return list;
        }
    }


    /**
     * 设置信息已浏览人
     * @param userId String
     * @param userName String
     * @param orgName String
     * @param informationId String
     */
    public boolean setBrowser(String userId, String userName, String orgName,
                              String informationId, String orgIdString) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(5);
            pg.put(userId, java.lang.String.class);
            pg.put(userName, java.lang.String.class);
            pg.put(orgName, java.lang.String.class);
            pg.put(informationId, java.lang.String.class);
            pg.put(orgIdString, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("setBrowser", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("error to setBrowser information :" + e.getMessage());
        } finally {
            return result;
        }
    }


    /**
     * 设置信息已浏览人 (加了 orgId)
     * @param userId String
     * @param userName String
     * @param orgId String
     * @param orgName String
     * @param informationId String
     * @param orgIdString String
     * @return boolean
     */
    public boolean setBrowser(String userId, String userName,String orgId, String orgName,
                            String informationId, String orgIdString) {
      boolean result = false;
      try {
          ParameterGenerator pg = new ParameterGenerator(6);
          pg.put(userId, java.lang.String.class);
          pg.put(userName, java.lang.String.class);
          pg.put(orgId,java.lang.String.class);
          pg.put(orgName, java.lang.String.class);
          pg.put(informationId, java.lang.String.class);
          pg.put(orgIdString, java.lang.String.class);
          EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                  "InformationEJBLocal", InformationEJBHome.class);
          ejbProxy.invoke("setBrowser", pg.getParameters());
          result = true;
      } catch (Exception e) {
          logger.error("error to setBrowser information :" + e.getMessage());
      } finally {
          return result;
      }
  }


    /**
     * 取信息的已浏览用户
     * @param informationId String
     * @return List
     */
    public List getBrowser(String informationId, String searchName) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(informationId, java.lang.String.class);
            pg.put(searchName, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getBrowser", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getBrowser information :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 取信息的历史版本
     * @param informationId String
     * @return List
     */
    public List getHistoryVersion(String informationId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getHistoryVersion", pg.getParameters());
        } catch (Exception e) {
            logger.error("errot to getHistoryVersion information :" +
                         e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 取信息评论
     * @param informationId String
     * @return List
     */
    public List getComment(String informationId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getComment", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getComment information :" + e.getMessage());
        } finally {
            return list;
        }
    }

    /**
     * 添加信息评论
     * @param userId String
     * @param userName String
     * @param orgName String
     * @param content String
     * @param informationId String
     */
    public boolean setComment(String userId, String userName, String orgName,
                              String content, String informationId) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(5);
            pg.put(userId, java.lang.String.class);
            pg.put(userName, java.lang.String.class);
            pg.put(orgName, java.lang.String.class);
            pg.put(content, java.lang.String.class);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("setComment", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("error to setComment information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 修改评论信息
     * @param content String
     * @param commentId String
     * @return boolean
     */
    public boolean updateComment(String content,String commentId)  {
      boolean result = false;
      try {
          ParameterGenerator pg = new ParameterGenerator(2);
          pg.put(content, java.lang.String.class);
          pg.put(commentId, java.lang.String.class);
          EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                  "InformationEJBLocal", InformationEJBHome.class);
          ejbProxy.invoke("updateComment", pg.getParameters());
          result = true;
      } catch (Exception e) {
          logger.error("error to updateComment information :" + e.getMessage());
      } finally {
          return result;
      }
  }


    /**
     * 点击次数加1
     * @param informationId String
     * @return boolean
     */
    public boolean setKits(String informationId) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("setKits", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("error to setKits information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 修改信息时首先保存修改前的信息
     * @param informationId String
     * @return boolean
     */
    public boolean saveHistory(String informationId) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("saveHistory", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("error to saveHistory information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 更新信息
     * @param informationId String
     * @param parameters String[]
     * @param accessoryList ArrayList
     * @return boolean
     */
    public boolean update(String informationId,
                          String[] parameters,
                          String[] assoInfo,
                          String[] infoAppendName,
                          String[] infoAppendSaveName,
                          String[] infoPicName,
                          String[] infoPicSaveName) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(7);
            pg.put(informationId, java.lang.String.class);
            pg.put(parameters, java.lang.String[].class);
            pg.put(assoInfo, java.lang.String[].class);
            pg.put(infoAppendName, java.lang.String[].class);
            pg.put(infoAppendSaveName, java.lang.String[].class);
            pg.put(infoPicName, java.lang.String[].class);
            pg.put(infoPicSaveName, java.lang.String[].class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (Boolean) ejbProxy.invoke("update", pg.getParameters());
            return result;
        } catch (Exception e) {
            logger.error("error to update information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 删除附件信息
     * @param informationId String
     * @param accessory String
     * @return boolean
     */
    public boolean deleteAccessory(String informationId, String accessory) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(informationId, java.lang.String.class);
            pg.put(accessory, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("deleteAccessory", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("error to deleteAccessory information :" +
                         e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 取单条历史信息用于浏览
     * @param historyId String
     * @return List
     */
    public List getSingleHistInfo(String historyId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(historyId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getSingleHistInfo", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getSingleHistInfo information :" +
                         e.getMessage());
        } finally {
            return list;
        }

    }

    /**
     * 推荐信息
     * @param batchId String[]
     * @return boolean
     */
    public boolean commend(String[] batchId) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(batchId, java.lang.String[].class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("commend", pg.getParameters());
            result = true;
        } catch (Exception e) {
            logger.error("error to commend information :" + e.getMessage());
        } finally {
            return result;
        }
    }
    /**
   * 取消推荐
   * @param id String
   * @return boolean
   */
  public boolean removeCommend(String  id) {
         boolean result = false;
         try {
             ParameterGenerator pg = new ParameterGenerator(1);
             pg.put(id, java.lang.String.class);
             EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                     "InformationEJBLocal", InformationEJBHome.class);
             ejbProxy.invoke("removeCommend", pg.getParameters());
             result=true;
         } catch (Exception e) {
             logger.error("error to commend information :" + e.getMessage());
         } finally {
             return result;
         }
     }


    /**
     * 批量删除信息，返回信息附件（包括历史信息附件）名称数组
     * @param batchId String[]
     * @return String[]
     */
    public List batchDelete(String[] batchId) {
        List result = null;
        try {
			if (batchId != null && batchId.length > 0) {
				/*//同步OA信息
				if (isSynchOAInfo()) {
					List channel = getchannleinfo(batchId[0]);
					if (channel != null && channel.size() > 0) {
						Object[] obj = (Object[]) channel.get(0);
						Long channelId = new Long(obj[0].toString());
						for (int i = 0; i < batchId.length; i++) {
							//System.out.println("-------------batch----delete-----------------channelId="+channelId+",articleId="+batchId[i]);
							List infoList = getSingleInfo(batchId[i], channelId.toString());
							if (infoList != null && infoList.size() > 0) {
								String transmitToEzsite = ((Object[]) infoList.
										get(0))[27].toString();

								com.whir.cms.synchoamanager.vo.SynchOAInfoVO vo = new
										com.whir.cms.synchoamanager.vo.
										SynchOAInfoVO();
								vo.setOaArticleId(new Long(batchId[i]));
								vo.setOaChannelId(channelId);
								vo.setTransmitToEzsite(Integer.parseInt(
										transmitToEzsite));

								//删除信息
								com.whir.cms.synchoamanager.bd.SynchOAInfoBD bd = new
										com.whir.cms.synchoamanager.bd.
										SynchOAInfoBD();
								bd.synchronizationOArticleInfoToCmsSite(vo,
										"delete");
							}
						}
					}
					//System.out.println("\n*******************OA信息同步成功！******************\n");
				} else {
					//System.out.println("\n*******************未设置OA信息同步！******************\n");
				}*/
			}

            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(batchId, java.lang.String[].class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (List) ejbProxy.invoke("batchDelete", pg.getParameters());

        } catch (Exception e) {
            logger.error("error to commend information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 清空一个频道下的所有的信息,返回信息附件（包括历史信息附件）名称集合
     * @param channelId String
     * @return List
     */
    public List allDelete(String channelId) {
        List result = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(channelId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (List) ejbProxy.invoke("allDelete", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to commend information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 删除单条信息,返回信息附件（包括历史信息附件）名称集合
     * @param informationId String
     * @return List
     */
    public List singleDelete(String informationId) {

            return this.singleDelete("",informationId);


    }


    /**
     * 删除单条信息,返回信息附件（包括历史信息附件）名称集合
     * @param informationId String
     * @return List
     */
    public List singleDelete(String channelId,String informationId) {
        List result = null;
        try {
			/*//同步OA信息
			if (isSynchOAInfo()) {
				String oaChannelId = null;
				if (channelId != null &&
					!"".equals(channelId) &&
					!"null".equals(channelId)) {
					oaChannelId = channelId;
				} else {
					List channel = getchannleinfo(informationId);
					if (channel != null && channel.size() > 0) {
						Object[] obj = (Object[]) channel.get(0);
						oaChannelId = obj[0].toString();
					}
				}
				//System.out.println("-----------------delete-----------------channelId="+oaChannelId+",articleId="+informationId);
				List infoList = getSingleInfo(informationId, oaChannelId.toString());
				if (infoList != null && infoList.size() > 0) {
					String transmitToEzsite = ((Object[]) infoList.get(0))[27].
											  toString();

					com.whir.cms.synchoamanager.vo.SynchOAInfoVO vo = new
							com.whir.cms.synchoamanager.vo.SynchOAInfoVO();
					vo.setOaArticleId(new Long(informationId));
					vo.setOaChannelId(new Long(oaChannelId));
					vo.setTransmitToEzsite(Integer.parseInt(transmitToEzsite));

					//删除信息
					com.whir.cms.synchoamanager.bd.SynchOAInfoBD bd = new
							com.whir.cms.synchoamanager.bd.SynchOAInfoBD();
					bd.synchronizationOArticleInfoToCmsSite(vo, "delete");
				}
				//System.out.println("\n*******************OA信息同步成功！******************\n");
			} else {
				//System.out.println("\n*******************未设置OA信息同步！******************\n");
			}*/
                        //全文检索 插入临时表
            new  NewInformationBD().saveInformationLucene(new String[]{informationId,"3"});
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(channelId,java.lang.String.class);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (List) ejbProxy.invoke("singleDelete", pg.getParameters());

        } catch (Exception e) {
            logger.error("error to commend information :" + e.getMessage());
        } finally {
            return result;
        }

    }

    /**
     * 信息转移
     * @param infoId String[]
     * @param channelId String
     * @return boolean
     */
    public boolean transfer(String[] infoId, String channelId,
                            String orchannelId) {
        boolean result = false;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(infoId, java.lang.String[].class);
            pg.put(channelId, java.lang.String.class);
            pg.put(orchannelId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("transfer", pg.getParameters());

			if (infoId != null && infoId.length > 0) {
				/*//同步OA信息
				if (isSynchOAInfo()) {
					for (int i = 0; i < infoId.length; i++) {
						List infoList = getSingleInfo(infoId[i],
													  channelId);
						if (infoList != null && infoList.size() > 0) {
							String transmitToEzsite = ((Object[]) infoList.get(
									0))[27].
									toString();

							com.whir.cms.synchoamanager.vo.SynchOAInfoVO vo = new
									com.
									whir.cms.synchoamanager.vo.SynchOAInfoVO();
							//OA信息的ID
							vo.setOaArticleId(new Long(infoId[i]));
							//OA信息的栏目ID
							vo.setOaChannelId(new Long(channelId));
							vo.setTransmitToEzsite(Integer.parseInt(transmitToEzsite));

							//修改信息
							com.whir.cms.synchoamanager.bd.SynchOAInfoBD bd = new
									com.
									whir.
									cms.synchoamanager.bd.SynchOAInfoBD();
							bd.synchronizationOArticleInfoToCmsSite(vo,
									"update");
						}
					}
					//System.out.println("\n*******************OA信息同步成功！******************\n");
				} else {
					//System.out.println("\n*******************未设置OA信息同步！******************\n");
				}*/
			}

            result = true;
        } catch (Exception e) {
            logger.error("error to transfer information :" + e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 取最近更新15条
     * @param userId String
     * @param orgId String
     * @return List
     */
    public List getNew(String userId, String orgId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getNew", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getNew information : " + e.getMessage());
        }
        return list;
    }

    /**
     * 取信息内容
     * @param informationId String
     * @return String
     */
    public String getContent(String informationId) {
        String content = "";
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            content = (String) ejbProxy.invoke("getContent", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getContent information :" + e.getMessage());
        }
        return content;
    }


    public String getUserViewCh(String userId, String orgId, String channelType,
                                String userDefine) {
        String hSql = "";
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(userDefine, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            hSql = (String) ejbProxy.invoke("getUserViewCh", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getUserViewCh information :" + e.getMessage());
        }
        return hSql;
    }



    public List getUserViewCh2(String userId, String orgId, String channelType,
                                String userDefine) {
     List  list=new ArrayList();
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(userDefine, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getUserViewCh2", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getUserViewCh2 information :" + e.getMessage());
        }
        return list;
    }

    /**
     * 增加对栏目维护人的判断
     * @param userId String
     * @param orgId String
     * @param channelType String
     * @param userDefine String
     * @return String
     */
    public String getUserViewCh3(String userId, String orgId, String channelType,
                                String userDefine) {
        String hSql = "";
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(userDefine, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            hSql = (String) ejbProxy.invoke("getUserViewCh3", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getUserViewCh information :" + e.getMessage());
        }
        return hSql;
    }



    public String getManagedChannel(String userId, String orgId, String channelType,
                                String userDefine) {
        String hSql = "";
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(userDefine, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            hSql = (String) ejbProxy.invoke("getManagedChannel", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getUserViewCh information :" + e.getMessage());
        }
        return hSql;
    }

    public String getAllInfoChannel(String userId, String orgId, String channelType,
                               String userDefine) {
       String hSql = "";
       try {
           ParameterGenerator pg = new ParameterGenerator(4);
           pg.put(userId, java.lang.String.class);
           pg.put(orgId, java.lang.String.class);
           pg.put(channelType, java.lang.String.class);
           pg.put(userDefine, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           hSql = (String) ejbProxy.invoke("getManagedChannel", pg.getParameters());
       } catch (Exception e) {
           logger.error("error to getUserViewCh information :" + e.getMessage());
       }
       return hSql;
   }





    /**
     * 取当前信息的关键字
     * @param informationId String
     * @return String
     */
    public List getinformation(String informationId) {
        List contentID = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            contentID = (List) ejbProxy.invoke("getinformation",
                                               pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getContent information :" + e.getMessage());
        }
        return contentID;
    }

    /**
     * 取关键字相同的ID
     * @param informationId String
     * @return String
     */
    public List getinformationID(String informationId_same,
                                 String informationId,String domainId) {
        List contentID_same = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(informationId_same, java.lang.String.class);
            pg.put(informationId, java.lang.String.class);
            pg.put(domainId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            contentID_same = (List) ejbProxy.invoke("getinformationID",
                    pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getContent information :" + e.getMessage());
        }
        return contentID_same;
    }


    /**
     * 信息可查看人的条件语句
     * @param userId String
     * @param orgId String
     * @param orgIdString String
     * @param alias String
     * @return String
     */
    public String getInfoReader(String userId, String orgId, String orgIdString,
                                String alias) {
        String reader = "";
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(orgIdString, java.lang.String.class);
            pg.put(alias, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            reader = (String) ejbProxy.invoke("getInfoReader", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getContent information :" + e.getMessage());
        }
        return reader;
    }

    public List getAssociateInfo(String orgId, String infoId, String userId,
                                 String orgIdString, String channelType,
                                 String userDefine) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(6);
            pg.put(orgId, java.lang.String.class);
            pg.put(infoId, java.lang.String.class);
            pg.put(userId, java.lang.String.class);
            pg.put(orgIdString, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(userDefine, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getAssociateInfo", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getAssociateInfo information :" +
                         e.getMessage());
        }
        return list;
    }

    public void updateProcInfo(String infoId, List fieldValueList) {
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(infoId, java.lang.String.class);
            pg.put(fieldValueList, java.util.List.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("updateProcInfo", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to updateProcInfo information :" + e.getMessage());
        }
    }

    public String getAccessoryType(String infoId) {
        String aType = "";
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(infoId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            aType = (String) ejbProxy.invoke("getAccessoryType",
                                             pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getAccessoryType information :" +
                         e.getMessage());
        }
        return aType;
    }

    public void save(InformationPO informationPO) {
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationPO, InformationPO.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            ejbProxy.invoke("save", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to save information :" + e.getMessage());
        }
    }

    public List getNotBrowser(String informationId, String searchName,
                              String domainId) {
        List list = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(informationId, java.lang.String.class);
            pg.put(searchName, java.lang.String.class);
            pg.put(domainId, String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getNotBrowser", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getNotBrowser information :" + e.getMessage());
        }
        return list;
    }

    /**
     * 取得某条信息的所有可查看人，组，组织等
     * @param informationId String
     * @return Object[]
     */
    public Object[] getAllBrowser(String informationId) {
       Object[] obj = null;
       try {
           ParameterGenerator pg = new ParameterGenerator(1);
           pg.put(informationId, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           obj = (Object[]) ejbProxy.invoke("getAllBrowser", pg.getParameters());
       } catch (Exception e) {
           logger.error("error to getAllBrowser information :" + e.getMessage());
       }
       return obj;
   }


    public Integer getUserIssueInfoCount(String userId) {
        Integer count = Integer.valueOf("0");
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(userId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            count = (Integer) ejbProxy.invoke("getUserIssueInfoCount",
                                              pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getNotBrowser information :" + e.getMessage());
        }
        return count;
    }

    public Integer setDossierStatus(String informationId, String dossierStatus) {
        Integer result = Integer.valueOf("0");
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(informationId, java.lang.String.class);
            pg.put(dossierStatus, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (Integer) ejbProxy.invoke("setDossierStatus",
                                               pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getNotBrowser information :" + e.getMessage());
        }
        return result;
    }

    public Boolean vindicateInfo(String userId, String orgId,
                                 String informationId) {
        Boolean result = Boolean.FALSE;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (Boolean) ejbProxy.invoke("vindicateInfo",
                                               pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getNotBrowser information :" + e.getMessage());
        }
        return result;
    }

    public String getInfoUserdefine(String informationId) {
        String userDefine = "0";
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            userDefine = (String) ejbProxy.invoke("getInfoUserdefine",
                                                  pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getInfoUserdefine information :" +
                         e.getMessage());
        }
        return userDefine;
    }

    public java.util.Map getMustReadCount(String userIds) {
        java.util.Map result = new java.util.HashMap();
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(userIds, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (java.util.Map) ejbProxy.invoke("getMustReadCount",
                    pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getMustReadCount information :" +
                         e.getMessage());
        }
        return result;
    }

    public java.util.List getMustReadInfo(String userIds, String domainId) {
        java.util.List result = new java.util.ArrayList();
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(userIds, java.lang.String.class);
            pg.put(domainId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (java.util.List) ejbProxy.invoke("getMustReadInfo",
                    pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getMustReadInfo information :" +
                         e.getMessage());
        }
        return result;
    }

    public Integer setOrderCode(String informationId, String channelType,
                                String orderNum) {
        Integer result = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(3);
            pg.put(informationId, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(orderNum, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = (Integer) ejbProxy.invoke("setOrderCode", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to setOrderCode information :" + e.getMessage());
        }
        return result;

    }


    public String[] getSingleEditor(String documentEditor,
                                    String informationIssuer, String year,
                                    String month) {
        String[] maplist = null;
        try {
            ParameterGenerator pg = new ParameterGenerator(4);
            pg.put(documentEditor, java.lang.String.class);
            pg.put(informationIssuer, java.lang.String.class);
            pg.put(year, java.lang.String.class);
            pg.put(month, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            maplist = (String[]) ejbProxy.invoke("getSingleEditor",
                                                 pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getNotBrowser information :" + e.getMessage());
        }
        return maplist;
    }


    public boolean channelCanView(String userId, String orgId, String channelType,
                                String userDefine,String channelId) {
        boolean result=false;
        try {
            ParameterGenerator pg = new ParameterGenerator(5);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(userDefine, java.lang.String.class);
            pg.put(channelId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = ((Boolean) ejbProxy.invoke("channelCanView", pg.getParameters())).booleanValue();
        } catch (Exception e) {
            logger.error("error to channelCanView information :" + e.getMessage());
        }
        return result;
    }

    /**
     * 增加对栏目维护人的判断
     * @param userId String
     * @param orgId String
     * @param channelType String
     * @param userDefine String
     * @param channelId String
     * @return boolean
     */
    public boolean channelCanView2(String userId, String orgId, String channelType,
                                String userDefine,String channelId) {
        boolean result=false;
        try {
            ParameterGenerator pg = new ParameterGenerator(5);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            pg.put(channelType, java.lang.String.class);
            pg.put(userDefine, java.lang.String.class);
            pg.put(channelId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result = ((Boolean) ejbProxy.invoke("channelCanView2", pg.getParameters())).booleanValue();
        } catch (Exception e) {
            logger.error("error to channelCanView information :" + e.getMessage());
        }
        return result;
    }


    public Integer delComment(String commentId) {
        Integer result = Integer.valueOf("0");
        try{
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(commentId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
            result = (Integer) ejbProxy.invoke("delComment", pg.getParameters());

        }catch(Exception e){
            logger.error("error to delComment information :" + e.getMessage());
        }
        return result;
    }
    /**
	 * 删除信息中的某评论
	 * -20151106 by jqq 同时将信息表中评论数量减一
	 * @return
	*/
    public Integer delComment2(String commentId,String informationId) {
        Integer result = Integer.valueOf("0");
        try{
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(commentId, java.lang.String.class);
            pg.put(informationId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
            result = (Integer) ejbProxy.invoke("delComment2", pg.getParameters());

        }catch(Exception e){
            logger.error("error to delComment2 information :" + e.getMessage());
        }
        return result;
    }

    public String getInformationModiIds(String channelId,String userId,String orgId,String orgIdString,String infoIds,List rightList) {
       String result=null;
       try{
           ParameterGenerator pg = new ParameterGenerator(6);
           pg.put(channelId, java.lang.String.class);
           pg.put(userId, java.lang.String.class);
           pg.put(orgId, java.lang.String.class);
           pg.put(orgIdString, java.lang.String.class);
           pg.put(infoIds, java.lang.String.class);
           pg.put(rightList,java.util.List.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           result = (String) ejbProxy.invoke("getInformationModiIds", pg.getParameters());

       }catch(Exception e){
           logger.error("error to delComment information :" + e.getMessage());
       }
       return result;
   }

   public String deleteHistory(String historyId,String informationId){
       String result="";
       try{
           ParameterGenerator pg=new ParameterGenerator(2);
           pg.put(historyId,String.class);
           pg.put(informationId,String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           result = (String) ejbProxy.invoke("deleteHistory", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to deleteHistory information :" + ex.getMessage());
       }
       return result;
   }


   public java.util.List getAfficheList(String domainId,String userId,String orgId,String orgIdString ){

       String  hasRight="-1";
    String rightCode="01*03*03";

    List rightScopeList=new com.whir.org.manager.bd.ManagerBD().getRightScope(userId,rightCode);
        if(rightScopeList!=null && rightScopeList.size()>0 && rightScopeList.get(0)!=null){
          Object[] obj=(Object[])rightScopeList.get(0);
          if("0".equals(obj[0].toString())){
            hasRight="1";
          }
         }

       java.util.List result = new java.util.ArrayList();
            try {
                ParameterGenerator pg = new ParameterGenerator(5);
                pg.put(domainId, java.lang.String.class);
                pg.put(userId, java.lang.String.class);
                pg.put(orgId,java.lang.String.class);
                pg.put(orgIdString,java.lang.String.class);
                pg.put(hasRight,String.class);
                EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                        "InformationEJBLocal", InformationEJBHome.class);
                result = (java.util.List) ejbProxy.invoke("getAfficheList",
                        pg.getParameters());
            } catch (Exception e) {
                logger.error("error to getAfficheList information :" +
                             e.getMessage());
            }
            return result;


   }


   /**
    *
    * @param userId String
    * @return List
    */
   public java.util.List getAllGroupByUserId(String userId) {
       java.util.List result = new java.util.ArrayList();
       try {
           ParameterGenerator pg = new ParameterGenerator(1);
           pg.put(userId, java.lang.String.class);

           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           result = (java.util.List) ejbProxy.invoke("getAllGroupByUserId",
                   pg.getParameters());
       } catch (Exception e) {
           logger.error("error to getAllGroupByUserId information :" +
                        e.getMessage());
       }
       return result;
   }


   /**
    *
    * @param orgId String
    * @param infoId String
    * @param userId String
    * @param orgIdString String
    * @param channelType String
    * @param userDefine String
    * @param channelStatusType String
    * @return List
    */
   public List getAssociateInfo(String orgId, String infoId, String userId,String orgIdString,String channelType,String userDefine,String channelStatusType){
      List list = null;
      try {
          ParameterGenerator pg = new ParameterGenerator(7);
          pg.put(orgId, java.lang.String.class);
          pg.put(infoId, java.lang.String.class);
          pg.put(userId, java.lang.String.class);
          pg.put(orgIdString, java.lang.String.class);
          pg.put(channelType, java.lang.String.class);
          pg.put(userDefine, java.lang.String.class);
          pg.put(channelStatusType, java.lang.String.class);
          EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                  "InformationEJBLocal", InformationEJBHome.class);
          list = (List) ejbProxy.invoke("getAssociateInfo", pg.getParameters());
      } catch (Exception e) {
          logger.error("error to getAssociateInfo information :" +
                       e.getMessage());
      }
      return list;
  }


  public boolean  checkReaded(String informationId,String userId)  {
       boolean result=false;
       List list=new ArrayList();
       try {
           ParameterGenerator pg = new ParameterGenerator(2);
           pg.put(informationId, java.lang.String.class);
           pg.put(userId, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           list = (List) ejbProxy.invoke("getBrowByEmpAndIfoId", pg.getParameters());
           if(list!=null&&list.size()>0){
               result=true;
           }
       } catch (Exception e) {
           logger.error("error to channelCanView information :" + e.getMessage());
       }
       return result;
   }

   /**
    * 得到某个组织发布信息的数量
    * @param orgId String
    * @return int
    */
   public int  getIssueNumOrg(String orgId)  {
       int result=0;
       try {
           ParameterGenerator pg = new ParameterGenerator(1);
           pg.put(orgId, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           result = ((Integer) ejbProxy.invoke("getIssueNumOrg", pg.getParameters())).intValue();

       } catch (Exception e) {
           logger.error("error to channelCanView information :" + e.getMessage());
       }
       return result;
   }

   public int  getIssueNumPerson(String userId)  {
       int result=0;
       try {
           ParameterGenerator pg = new ParameterGenerator(1);
           pg.put(userId, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           result = ((Integer) ejbProxy.invoke("getIssueNumPerson", pg.getParameters())).intValue();

       } catch (Exception e) {
           logger.error("error to channelCanView information :" + e.getMessage());
       }
       return result;
   }


   /**
    *
    * @param userId String
    * @param orgId String
    * @return String[]
    */
   public String [] getUserViewChAll(String userId, String orgId) {
        String hSql [] =null;
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);

            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            hSql = (String []) ejbProxy.invoke("getUserViewChAll", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getUserViewChAll information :" + e.getMessage());
        }
        return hSql;
    }


    /**
     *
     * @param userId String
     * @param orgId String
     * @return List
     */
    public List getUserViewChAll2(String userId, String orgId) {
        List list=new ArrayList();
        try {
            ParameterGenerator pg = new ParameterGenerator(2);
            pg.put(userId, java.lang.String.class);
            pg.put(orgId, java.lang.String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            list = (List) ejbProxy.invoke("getUserViewChAll2", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to getUserViewChAll2 information :" + e.getMessage());
        }
        return list;
    }


    /**
     *
     * @param po InforViewScopePO
     * @return Long
     */
    public Long saveInforViewScopePO(InforViewScopePO po) {
        Long result=null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(po, InforViewScopePO.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result=(Long)ejbProxy.invoke("saveInforViewScopePO", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to saveInforViewScopePO information :" + e.getMessage());
        }finally{
         return result;
        }

    }


    public String deleteInforViewScopePO(String ids)  {
        String result="-1";
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(ids, String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
           result=(String) ejbProxy.invoke("deleteInforViewScopePO", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to deleteInforViewScopePO information :" + e.getMessage());
        } finally{
         return result;
        }

    }

    /**
     *
     * @param po InforViewScopePO
     * @return Long
     */
    public Long updateInforViewScopePO(InforViewScopePO po)  {
        Long result=null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(po, InforViewScopePO.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
            result=(Long)ejbProxy.invoke("updateInforViewScopePO", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to updateInforViewScopePO information :" + e.getMessage());
        }finally{
        return result;
        }
    }


    /**
     *
     * @param id String
     * @return InforViewScopePO
     */
    public InforViewScopePO loadInforViewScopePO(String id) {
        InforViewScopePO po=null;
        try {
            ParameterGenerator pg = new ParameterGenerator(1);
            pg.put(id, String.class);
            EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                    "InformationEJBLocal", InformationEJBHome.class);
           po=(InforViewScopePO)ejbProxy.invoke("loadInforViewScopePO", pg.getParameters());
        } catch (Exception e) {
            logger.error("error to loadInforViewScopePO information :" + e.getMessage());
        }finally{
         return po;
        }
    }


    /**
   *
   * @param rightCode1 String
   * @param rightCode2 String
   * @return String
   */
  public String [] getTwoOrgRight(String userId, String orgId,String readerIds,String readerNames)  {
      String result []=null;
      try {
          ParameterGenerator pg = new ParameterGenerator(4);
          pg.put(userId, String.class);
          pg.put(orgId, String.class);
          pg.put(readerIds, String.class);
          pg.put(readerNames, String.class);
          EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                  "InformationEJBLocal", InformationEJBHome.class);
         result=(String [])ejbProxy.invoke("getTwoOrgRight", pg.getParameters());
      } catch (Exception e) {
          logger.error("error to save information :" + e.getMessage());
      }finally{
       return result;
      }
  }

  public  String synchronizeTOSite(InformationPO informationPO ,Long result,
                    String[] infoPicName,
                    String[] infoPicSaveName,
                    String[] infoAppendName,
                    String[] infoAppendSaveName,
                    String domainId, HttpServletRequest request){
          String resulst="false";
          String  siteChannelString=informationPO.getSiteChannelString();
          try{
                  //同步OA信息
            if (isSynchOAInfo()) {
                   WangzhaInfoVO vo = new  WangzhaInfoVO();
                    //OA信息的ID
                    vo.setOaArticleId(result);
                    //OA信息的栏目ID
                    vo.setOaChannelId(informationPO.getInformationChannel().
                                                      getChannelId());
                    //OA文章的信息标题
                    vo.setInformationTitle(informationPO.getInformationTitle());
                    //OA文章的副标题
                    vo.setInformationSubTitle(informationPO.getInformationSubTitle());
                    //OA文章的关键字
                    vo.setInformationKey(informationPO.getInformationKey());
                    //OA文章的信息摘要
                    vo.setInformationSummary(informationPO.getInformationSummary());
                    //OA文章的内容
                    vo.setInformationContent(informationPO.getInformationContent());
                    //OA文章的图片名称数组
                    vo.setOaArticlePhotoFileName(infoPicName);
                    //OA文章的图片保存名称(随机数文件名)数组
                    vo.setOaArticlePhotoSaveName(infoPicSaveName);
                    //OA文章的图片的大小(字节)
                    vo.setOaArticlePhotoSize(null);
                    //OA文章的物理图片保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/  路径以/开始和结束
                    vo.setOaArticlePhotoPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/upload/information/");
                    //OA文章的附件名称数组
                    vo.setOaArticleAccessoryFileName(infoAppendName);
                    //OA文章的附件保存名称(随机数文件名)数组
                    vo.setOaArticleAccessorySaveName(infoAppendSaveName);
                    //OA文章的附件的大小(字节)
                    vo.setOaArticleAccessorySize(null);
                    //OA文章的物理附件保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/ 路径以/开始和结束
                    vo.setOaArticleAccessoryPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/upload/information/");
                    //是否发布到网站-transmitToEzsite 1-是 0-否
                    //informationPO.getTransmitToEzsite();
                     vo.setTransmitToEzsite(informationPO.getTransmitToEzsite());

                    //是否永久-informationVaildType 1-否 0-是
                    //informationPO.getInformationValidType();
                    vo.setInformationVaildType(informationPO.getInformationValidType());

                    //OA文章的有效期开始时间
                    vo.setValidBeginTime(informationPO.getValidBeginTime());
                    //OA文章的有效期结束时间
                    vo.setValidEndTime(informationPO.getValidEndTime());
                    //OA文章的作者
                    vo.setInformationAuthor(informationPO.getInformationAuthor());
                    //OA文章的发布人ID
                    vo.setInformationIssuerId(informationPO.getInformationIssuerId() +"");
                    //OA文章的发布人的组织ID
                    vo.setInformationIssueOrgId(informationPO.getInformationIssueOrgId());
                    //OA文章的发布时间
                    vo.setInformationIssueTime(informationPO.getInformationIssueTime());
                    //OA域Id
                    vo.setDomainId(new Long(domainId));
                    //信息发布类型
                    vo.setInformationType(informationPO.getInformationType());
                    //来源
                    vo.setComefrom(informationPO.getComeFrom());

                    String siteId = ""; //站点id
                    String siteChannelIds = ""; //站点的栏目id
                    if (siteChannelString != null && !siteChannelString.equals("") &&
                        !siteChannelString.equals("null")) { //外网
                        String allsiteInforArr[] = siteChannelString.split(",");
                        if (allsiteInforArr != null && allsiteInforArr.length > 0) {
                            for (int ii = 0; ii < allsiteInforArr.length; ii++) {
                                String siteInforArr[] = allsiteInforArr[ii].
                                        toString().split("\\|");
                                if (siteInforArr.length > 2) {
                                    siteId = siteInforArr[0];
                                    siteChannelIds += siteInforArr[2] + ",";
                                }
                            }

                            siteChannelIds = siteChannelIds.substring(0, siteChannelIds.length() - 1);
                            WangzhaTools tools = new WangzhaTools();
                            vo.setSiteId(siteId);
                            vo.setSiteChannelId(siteChannelIds);
                            tools.saveInfoContent(vo, "add", request);
                        }
                    }
           }
       }catch(Exception e){
            e.printStackTrace();
            logger.error("error to add information :" + e.getMessage());
       }
       return resulst;
    }


    public  String synchronizeToMoreSite(InformationPO informationPO ,Long result,
                    String[] infoPicName,
                    String[] infoPicSaveName,
                    String[] infoAppendName,
                    String[] infoAppendSaveName,
                    String domainId, HttpServletRequest request){
          String resulst="false";
          try{
            //同步OA信息
            if (isSynchOAInfo()) {
                   InformationSynBD inforSynBD=new InformationSynBD();
                   List synList=inforSynBD.saveInfoSiteInfo(request,""+result);
                   //System.out.println("synList----->"+synList);
                   if(synList!=null&&synList.size()>0){
                	   //System.out.println("synList----->"+synList.size());
                	   //System.out.println("result---00000-->"+result);
                       WangzhaInfoVO vo = new WangzhaInfoVO();
                       //OA信息的ID
                       vo.setOaArticleId(result);
                       //OA信息的栏目ID
                       vo.setOaChannelId(informationPO.getInformationChannel().getChannelId());
                       //OA文章的信息标题
                       vo.setInformationTitle(informationPO.getInformationTitle());
                       //OA文章的副标题
                       vo.setInformationSubTitle(informationPO.getInformationSubTitle());
                       //OA文章的关键字
                       vo.setInformationKey(informationPO.getInformationKey());
                       //OA文章的信息摘要
                       vo.setInformationSummary(informationPO.getInformationSummary());
                       //OA文章的内容
                       vo.setInformationContent(informationPO.getInformationContent());
                       //OA文章的图片名称数组
                       vo.setOaArticlePhotoFileName(infoPicName);
                       //OA文章的图片保存名称(随机数文件名)数组
                       vo.setOaArticlePhotoSaveName(infoPicSaveName);
                       //OA文章的图片的大小(字节)
                       vo.setOaArticlePhotoSize(null);
                       //OA文章的物理图片保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/  路径以/开始和结束
                       vo.setOaArticlePhotoPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath() +"/upload/information/");
                       //OA文章的附件名称数组
                       vo.setOaArticleAccessoryFileName(infoAppendName);
                       //OA文章的附件保存名称(随机数文件名)数组
                       vo.setOaArticleAccessorySaveName(infoAppendSaveName);
                       //OA文章的附件的大小(字节)
                       vo.setOaArticleAccessorySize(null);
                       //OA文章的物理附件保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/ 路径以/开始和结束
                       vo.setOaArticleAccessoryPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath() +"/upload/information/");
                       //是否发布到网站-transmitToEzsite 1-是 0-否
                       //informationPO.getTransmitToEzsite();
                       vo.setTransmitToEzsite(informationPO.getTransmitToEzsite());

                       //是否永久-informationVaildType 1-否 0-是
                       //informationPO.getInformationValidType();
                       vo.setInformationVaildType(informationPO.getInformationValidType());

                       //OA文章的有效期开始时间
                       vo.setValidBeginTime(informationPO.getValidBeginTime());
                       //OA文章的有效期结束时间
                       vo.setValidEndTime(informationPO.getValidEndTime());
                       //OA文章的作者
                       vo.setInformationAuthor(informationPO.getInformationAuthor());
                       //OA文章的发布人ID
                       vo.setInformationIssuerId(informationPO.getInformationIssuerId() + "");
                       //OA文章的发布人的组织ID
                       vo.setInformationIssueOrgId(informationPO.getInformationIssueOrgId());
                       //OA文章的发布时间
                       vo.setInformationIssueTime(informationPO.getInformationIssueTime());
                       //OA域Id
                       vo.setDomainId(new Long(domainId));
                       //信息发布类型
                       vo.setInformationType(informationPO.getInformationType());
                       //来源
                       vo.setComefrom(informationPO.getComeFrom());

                       inforSynBD.saveInfoContent(vo, "add", request,synList);
                       resulst="true";
                }
           }
       }catch(Exception e){
            e.printStackTrace();
            logger.error("error to add information :" + e.getMessage());
       }
       return resulst;
    }



    public void synchronizeToMoreSite_update(String informationId,
                                       String[] parameters,
                                       String[] assoInfo,
                                       String[] infoAppendName,
                                       String[] infoAppendSaveName,
                                       String[] infoPicName,
                                       String[] infoPicSaveName,
                                       String InformationType,
                                       String dealType,HttpServletRequest request
                                       ) {
      try{
          InformationSynBD inforSynBD=new InformationSynBD();
          List synList=inforSynBD.saveInfoSiteInfo(request,informationId);
          //同步OA信息
          if (isSynchOAInfo()&&synList!=null&&synList.size()>0) {
              WangzhaInfoVO vo = new  WangzhaInfoVO();
              //OA信息的ID
              vo.setOaArticleId(new Long(informationId));
              //OA信息的栏目ID
              vo.setOaChannelId(new Long(parameters[31]));
              //OA文章的信息标题
              vo.setInformationTitle(parameters[0]);
              //OA文章的副标题
              vo.setInformationSubTitle(parameters[1]);
              //OA文章的关键字
              vo.setInformationKey(parameters[3]);
              //OA文章的信息摘要
              vo.setInformationSummary(parameters[2]);
              //OA文章的内容
              vo.setInformationContent(parameters[4]);
              //OA文章的图片名称数组
              vo.setOaArticlePhotoFileName(infoPicName);
              //OA文章的图片保存名称(随机数文件名)数组
              vo.setOaArticlePhotoSaveName(infoPicSaveName);
              //OA文章的图片的大小(字节)
              vo.setOaArticlePhotoSize(null);
              //OA文章的物理图片保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/  路径以/开始和结束
              vo.setOaArticlePhotoPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/upload/information/");
              //OA文章的附件名称数组
              vo.setOaArticleAccessoryFileName(infoAppendName);
              //OA文章的附件保存名称(随机数文件名)数组
              vo.setOaArticleAccessorySaveName(infoAppendSaveName);
              //OA文章的附件的大小(字节)
              vo.setOaArticleAccessorySize(null);
              //OA文章的物理附件保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/ 路径以/开始和结束
              vo.setOaArticleAccessoryPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+
                      "/upload/information/");
              //是否发布到网站-transmitToEzsite 1-是 0-否
              //parameters[23]
              vo.setTransmitToEzsite(Integer.parseInt(parameters[23]));


              //是否永久-informationVaildType 1-否 0-是
              //parameters[12]
              vo.setInformationVaildType(Integer.parseInt(parameters[12]));

              //OA文章的有效期开始时间
              vo.setValidBeginTime(new java.util.Date(parameters[13]));
              //OA文章的有效期结束时间
              vo.setValidEndTime(new java.util.Date(parameters[14]));
              //OA文章的作者
              vo.setInformationAuthor(parameters[28]);
              //OA文章的发布人ID
              //vo.setInformationIssuerId(informationPO.getInformationIssuerId() + "");
              //OA文章的发布人的组织ID
              //vo.setInformationIssueOrgId(informationPO.getInformationIssueOrgId());
              //OA文章的发布时间


              if (parameters[29] != null) {
                  java.util.Calendar IssueTime = java.util.Calendar.getInstance();
                  String[] issueTime = parameters[29].split("/");
                  IssueTime.set(Integer.parseInt(issueTime[0]),
                                Integer.parseInt(issueTime[1]) - 1,
                                Integer.parseInt(issueTime[2]));
                  vo.setInformationIssueTime(IssueTime.getTime());
              }
              //OA域Id
              //vo.setDomainId(new Long(domainId));

              //OA域Id
                  vo.setDomainId(new Long("0"));
                  //信息发布类型
                  vo.setInformationType(InformationType);

                  //来源
                  vo.setComefrom(""+parameters[33]);

                  inforSynBD.saveInfoContent(vo, dealType, request,synList);

          }
      }catch(Exception e){
      }
  }



    public void synchronizeTOSite_update(String informationId,
                                       String[] parameters,
                                       String[] assoInfo,
                                       String[] infoAppendName,
                                       String[] infoAppendSaveName,
                                       String[] infoPicName,
                                       String[] infoPicSaveName,
                                       String InformationType,
                                       String dealType,HttpServletRequest request
                                       ) {
      String siteChannelString=""+parameters[56];

      try{

          //同步OA信息
          if (isSynchOAInfo()) {
              WangzhaInfoVO vo = new  WangzhaInfoVO();
              //OA信息的ID
              vo.setOaArticleId(new Long(informationId));
              //OA信息的栏目ID
              vo.setOaChannelId(new Long(parameters[31]));
              //OA文章的信息标题
              vo.setInformationTitle(parameters[0]);
              //OA文章的副标题
              vo.setInformationSubTitle(parameters[1]);
              //OA文章的关键字
              vo.setInformationKey(parameters[3]);
              //OA文章的信息摘要
              vo.setInformationSummary(parameters[2]);
              //OA文章的内容
              vo.setInformationContent(parameters[4]);
              //OA文章的图片名称数组
              vo.setOaArticlePhotoFileName(infoPicName);
              //OA文章的图片保存名称(随机数文件名)数组
              vo.setOaArticlePhotoSaveName(infoPicSaveName);
              //OA文章的图片的大小(字节)
              vo.setOaArticlePhotoSize(null);
              //OA文章的物理图片保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/  路径以/开始和结束
              vo.setOaArticlePhotoPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/upload/information/");
              //OA文章的附件名称数组
              vo.setOaArticleAccessoryFileName(infoAppendName);
              //OA文章的附件保存名称(随机数文件名)数组
              vo.setOaArticleAccessorySaveName(infoAppendSaveName);
              //OA文章的附件的大小(字节)
              vo.setOaArticleAccessorySize(null);
              //OA文章的物理附件保存路径,如:com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+/upload/information/ 路径以/开始和结束
              vo.setOaArticleAccessoryPath(com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/upload/information/");
              //是否发布到网站-transmitToEzsite 1-是 0-否
              //parameters[23]
              vo.setTransmitToEzsite(Integer.parseInt(parameters[23]));

              //是否永久-informationVaildType 1-否 0-是
              //parameters[12]
              vo.setInformationVaildType(Integer.parseInt(parameters[12]));

              //OA文章的有效期开始时间
              vo.setValidBeginTime(new java.util.Date(parameters[13]));
              //OA文章的有效期结束时间
              vo.setValidEndTime(new java.util.Date(parameters[14]));
              //OA文章的作者
              vo.setInformationAuthor(parameters[28]);
              //OA文章的发布人ID
              //vo.setInformationIssuerId(informationPO.getInformationIssuerId() + "");
              //OA文章的发布人的组织ID
              //vo.setInformationIssueOrgId(informationPO.getInformationIssueOrgId());
              //OA文章的发布时间


              if (parameters[29] != null) {
                  java.util.Calendar IssueTime = java.util.Calendar.getInstance();
                  String[] issueTime = parameters[29].split("/");
                  IssueTime.set(Integer.parseInt(issueTime[0]),
                                Integer.parseInt(issueTime[1]) - 1,
                                Integer.parseInt(issueTime[2]));
                  vo.setInformationIssueTime(IssueTime.getTime());
              }
              //OA域Id
              //vo.setDomainId(new Long(domainId));

              //OA域Id
                  vo.setDomainId(new Long("0"));
                  //信息发布类型
                  vo.setInformationType(InformationType);

                  //来源
                  vo.setComefrom(""+parameters[33]);

                       String  siteId="";//站点id
                       String  siteChannelIds=""; //站点的栏目id
                       if (siteChannelString!=null&&!siteChannelString.equals("")&&!siteChannelString.equals("null")) { //外网
                           String allsiteInforArr[] = siteChannelString.split(",");
                           if (allsiteInforArr != null && allsiteInforArr.length > 0) {
                               for (int ii = 0; ii < allsiteInforArr.length; ii++) {
                                   String siteInforArr[] = allsiteInforArr[ii].toString().split("\\|");
                                   if (siteInforArr.length > 2) {
                                       siteId=siteInforArr[0];
                                       siteChannelIds+=siteInforArr[2]+",";
                                   }
                               }
                               siteChannelIds=siteChannelIds.substring(0,siteChannelIds.length()-1);
                               WangzhaTools tools = new WangzhaTools();
                               vo.setSiteId(siteId);
                               vo.setSiteChannelId(siteChannelIds);
                               tools.saveInfoContent(vo, dealType, request);

                           }
                       }
          }
      }catch(Exception e){
      }
  }



  /**
   * 取可以维护的id
   * @param userId String
   * @param orgId String
   * @param orgIdString String
   * @param inforIds String
   * @param rightList List
   * @return String
   */
  public String getInformationModiIds_notChannel( String userId,
                              String orgId, String orgIdString,
                              String inforIds, List rightList){
      String result="";

      try {

          ParameterGenerator pg = new ParameterGenerator(5);
          pg.put(userId, java.lang.String.class);
          pg.put(orgId, java.lang.String.class);
          pg.put(orgIdString, java.lang.String.class);
          pg.put(inforIds, java.lang.String.class);
          pg.put(rightList, java.util.List.class);
          EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                  "InformationEJBLocal", InformationEJBHome.class);
         result=(String)ejbProxy.invoke("getInforModiIdsNotChannel", pg.getParameters());
      } catch (Exception e) {
          logger.error("error to save getInforModiIdsNotChannel :" + e.getMessage());
      }finally{
         return result;
      }
  }

  /**
   * 测试方法
   * @param domainId String
   * @return List
   */
  public List listInformationClass(String domainId){
       List list=new ArrayList();
       try {
           ParameterGenerator pg = new ParameterGenerator(1);
           pg.put(domainId, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           list = (List) ejbProxy.invoke("listInformationClass", pg.getParameters());
       } catch (Exception e) {
           logger.error("error to listInformationClass information :" + e.getMessage());
       }
       return list;
   }


   /**
    *
    * @param userIds String
    * @return String
    */
   public String  getUserIdsForEmailByThree(String userIds){
       String result="";
       try {
           ParameterGenerator pg = new ParameterGenerator(1);
           pg.put(userIds, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           result = ""+ejbProxy.invoke("getUserIdsForEmailByThree", pg.getParameters());
       } catch (Exception e) {
           logger.error("error to getUserIdsForEmailByThree information :" + e.getMessage());
       }
       return result;
   }


   /**
    * 根据人员id串+组织id串+群组id串  发送邮件
    * @param title String
    * @param content String
    * @param sendName String
    * @param userIds String
    * @param userNames String
    * @return boolean
    */
   public boolean sendEmailByThreeIds(String title, String content,
                                      String sendName, String userIds,
                                      String userNames){
       boolean result = false;
       try {
           com.whir.component.mail.Mail mail = new com.whir.component.mail.
                                               Mail();
           result = mail.sendInnerMail(title, content, sendName,
                                       getUserIdsForEmailByThree(userIds),
                                       userNames);
      } catch (Exception e) {
          logger.error("error to getUserIdsForEmailByThree information :" + e.getMessage());
      }
       return result;
   }

   /**
    *根据信息id取 栏目等信息
    * @param informationId String
    * @return String[]
    */
   public  String []  getInfor_channel_type(String informationId){
       String result []=null;
       try {
           ParameterGenerator pg = new ParameterGenerator(1);
           pg.put(informationId, java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB",
                   "InformationEJBLocal", InformationEJBHome.class);
           result =(String[])ejbProxy.invoke("getInfor_channel_type", pg.getParameters());
       } catch (Exception e) {
           logger.error("error to getInfor_channel_type information :" + e.getMessage());
       }
       return result;
   }

   /**
    * 信息推送 发邮件
    * @param informationId String
    * @param informationMailSendId String
    * @param informationMailSendName String
    * @param userName String
    * @param domainId String
    * @return String
 * @throws UnsupportedEncodingException 
    */
   public String MailSendURL_Send(String informationId,
                                  String informationMailSendId,
                                  String informationMailSendName,
                                  String userName, String domainId, String mail_account, 
                                  String mail_pass, boolean isEncryptPass, String remindType, String userId,HttpServletRequest request) throws UnsupportedEncodingException {
	   
	   logger.debug("----------------------MailSendURL_Send-------------------------");
	   logger.debug("informationId:"+informationId);
	   
       String[] infors = getInfor_channel_type(informationId);
       String channelId = "" + infors[0];
       String channelName = "" + infors[1];
       String informationType = "" + infors[2];
       String channelType = "" + infors[3];
       String userDefine = "" + infors[4];
       String content = "" + infors[5];
       String userChannelName = "" + infors[6];
       String informationtitle = "" + infors[7];
       
       logger.debug("channelId:"+channelId);
       logger.debug("channelName:"+channelName);

       String orgId = "";
       String checkdepart = "";
       if ("0".equals(userDefine) && Long.valueOf(channelType)>0) {
           //单位主页
           orgId = channelType;
           checkdepart = "1";
       }
       
       com.whir.common.util.SysUtils sysUtils = new com.whir.common.util.SysUtils();
       String[] str = sysUtils.getMailRemindType(domainId);
       String isWebMail = str[0];
       String url = "";
       String linkcontent = "";
//       if(isWebMail.equals("1")){//外部邮件
//    	   linkcontent += informationtitle;
//       }else{
    	   url =
               com.whir.component.config.PropertiesUtil.getInstance().getRootPath()+"/Information!view.action?channelId=" + channelId
               + "&informationId=" + informationId + "&orgId=" + orgId + "&userDefine=" + userDefine + "&checkdepart=" +
               checkdepart + "&informationType=" + informationType + "&channelType=" + channelType;
    	   //邮件里的 连接
    	   linkcontent += "<P>";
    	   linkcontent += " <a href=\"" + url + "\">" + informationtitle + "</a>";
           linkcontent += "</P>";
//       }

       SenddocumentBD bd = new SenddocumentBD();
       InnerMailBD mail = new InnerMailBD();

       boolean result = true;
       //把 组织id,群组id转话为 人员id
       String empIds = "";
       String empIds_mail = "";
       if (informationMailSendId != null && informationMailSendId.length() > 0) {
           // empIds = bd.getEmpIdsByTotal(informationMailSendId);
           empIds = getUserIdsForEmailByThree(informationMailSendId);
           //logger.debug("========empIds:" + empIds);
    	   //20151215 -by jqq 改造信息推送，直接传递用户组织字符串，不做拆分人员id的处理
           empIds_mail = informationMailSendId;
       }
       
       //不为空的话 发 邮件
       if (empIds_mail != null && empIds_mail.length() > 0) {
           try {
        	   //邮件提醒
        	   if(remindType.indexOf("mail")>-1){
        		   //logger.debug("========informationtitle:" + informationtitle);
        		   //logger.debug("========linkcontent:" + linkcontent);
        		   //logger.debug("========userName:" + userName);
        		   //logger.debug("========empIds_mail:" + empIds_mail);
        		   //logger.debug("========informationMailSendName:" + informationMailSendName);
        		   //logger.debug("========mail_account:" + mail_account);
        		   result = mail.sendInnerMail(informationtitle+"（信息推送）", linkcontent, "1", userName,
        				   Long.valueOf(userId), empIds_mail,
	                       informationMailSendName,
	                       domainId, mail_account, mail_pass, isEncryptPass);
        	   }
           } catch (Exception e) {
        	   e.printStackTrace();
           }
           String userAccounts = "";
           UserBD userBD = new UserBD();
           String[] empIdArray = (empIds.substring(1) + "$").split("\\$\\$");
           //即时通讯提醒
           if(remindType.indexOf("im")>-1){
        	   new SendRealTimeMessage(empIdArray, informationtitle ,url, userName).start();
	           /*Realtimemessage rm = new Realtimemessage();
	           for(int i=0;i<empIdArray.length;i++){
	        	   String empId = empIdArray[i];
	        	   if(!empId.equals("-1")&&!empId.equals("0")){
		        	   List userInfo = userBD.getUserInfo(Long.valueOf(empId));
		        	   Object[] obj = (Object[]) userInfo.get(0);
		        	   if(obj[2]!=null){
		        		   String userAccount = obj[2].toString();
		        		   String userPassword = obj[4].toString();
		        		   rm.sendNotify(userAccount, "信息推送", informationtitle, "点击查看", "information", userAccount, userPassword, url);
		        	   }
	        	   }
	           }*/
           }
           //短信提醒
           if(remindType.indexOf("sms")>-1){
        	   String userIdStr = "";
        	   for(int i=0;i<empIdArray.length;i++){
	        	   String empId = empIdArray[i];
	        	   userIdStr += empId+",";
        	   }
        	   if(!userIdStr.equals("")){
        		   userIdStr = userIdStr.endsWith(",")?userIdStr.substring(0,userIdStr.lastIndexOf(",")):userIdStr;
        	   }
        	   //20160109 -by jqq 短信发送改造:使用（格式处理前）短信发送方法
        	   //com.whir.ezoffice.message.bd.MessageBD messageSend = new com.whir.ezoffice.message.bd.MessageBD();   
        	   ModelSendMsg sendMsg = new ModelSendMsg();
        	   /*boolean sendSuccess = messageSend.modelSendMsg(userIdStr, 
        			   informationtitle+"（信息推送）", "", domainId, userId); */
        	   boolean sendSuccess = sendMsg.sendSystemMessage("信息管理", 
        			   informationtitle+"（信息推送）", userIdStr, "", request);
           }
           //微信提醒
           String userIdStr = "";
    	   for(int i=0;i<empIdArray.length;i++){
        	   String empId = empIdArray[i];
        	   userIdStr += empId+"|";
    	   }
    	   if(!userIdStr.equals("")){
    		   userIdStr = userIdStr.endsWith("|")?userIdStr.substring(0,userIdStr.lastIndexOf("|")):userIdStr;
    	   }
    	   java.util.Map param  = new HashMap();
    	   param.put("informationId", informationId);
    	   param.put("informationType", informationType);
    	   param.put("channelId", channelId);
    	   
    	   logger.debug("channelId:"+channelId);
    	   
    	   //20151211 -by jqq 手机端推送的标题改造
    	   String result_title = "新推送：【";
    	   result_title += channelName + "】" + informationtitle;
    	   //logger.debug("========result_title:" + result_title);
    	   logger.debug("========信息推送手机userIdStr:" + userIdStr);
    	   boolean success = new com.whir.evo.weixin.bd.WeiXinBD().sendMsg(userIdStr,result_title,null,null,null,"information",param);
       } else {
           result = false;
       }

       return url;

   }
   
   /**
    * 信息推送 发邮件
    * @param informationId String
    * @param informationMailSendId String
    * @param informationMailSendName String
    * @param userName String
    * @param domainId String
    * @return String
 * @throws UnsupportedEncodingException 
    */
   public String MailSendURL_Send2(String informationId,
                                  String informationMailSendId,
                                  String informationMailSendName,
                                  String userName, String domainId , boolean isEncryptPass, String remindType, String userId) throws UnsupportedEncodingException {
       
       logger.debug("----------------------MailSendURL_Send2-------------------------");
       logger.debug("informationId:"+informationId);
       
       String[] infors = getInfor_channel_type(informationId);
       String channelId = "" + infors[0];
       String channelName = "" + infors[1];
       String informationType = "" + infors[2];
       String channelType = "" + infors[3];
       String userDefine = "" + infors[4];
       String content = "" + infors[5];
       String userChannelName = "" + infors[6];
       String informationtitle = "" + infors[7];
       
       logger.debug("channelId:"+channelId);
       logger.debug("channelName:"+channelName);

       String orgId = "";
       String checkdepart = "";
       if ("0".equals(userDefine) && Long.valueOf(channelType)>0) {
           //单位主页
           orgId = channelType;
           checkdepart = "1";
       }
       
       com.whir.common.util.SysUtils sysUtils = new com.whir.common.util.SysUtils();
       String[] str = sysUtils.getMailRemindType(domainId);
       String isWebMail = str[0];
       String url = "";
       String linkcontent = "";
        url = com.whir.component.config.PropertiesUtil.getInstance()
                .getRootPath()
                + "/Information!view.action?channelId="
                + channelId
                + "&informationId="
                + informationId
                + "&orgId="
                + orgId
                + "&userDefine="
                + userDefine
                + "&checkdepart="
                + checkdepart
                + "&informationType="
                + informationType
                + "&channelType="
                + channelType;
        //邮件里的 连接
        linkcontent += "<P>";
        linkcontent += " <a href=\"" + url + "\">" + informationtitle + "</a>";
        linkcontent += "</P>";

       SenddocumentBD bd = new SenddocumentBD();
       InnerMailBD mail = new InnerMailBD();

       boolean result = true;
       //把 组织id,群组id转话为 人员id
       String empIds = "";
       String empIds_mail = "";
       if (informationMailSendId != null && informationMailSendId.length() > 0) {
           // empIds = bd.getEmpIdsByTotal(informationMailSendId);
           empIds = getUserIdsForEmailByThree(informationMailSendId);
           //logger.debug("========empIds:" + empIds);
           //20151215 -by jqq 改造信息推送，直接传递用户组织字符串，不做拆分人员id的处理
           empIds_mail = informationMailSendId;
       }
       
       //不为空的话 发 邮件
       if (empIds_mail != null && empIds_mail.length() > 0) {
           try {
               //邮件提醒
               if(remindType.indexOf("mail")>-1){
                   /*result = mail.sendInnerMail(informationtitle+"（信息推送）", linkcontent, "1", userName,
                           Long.valueOf(userId), empIds_mail,
                           informationMailSendName,
                           domainId, mail_account, mail_pass, isEncryptPass);*/
                   logger.debug("========邮件提醒mail start========");
                   mail.sendInnerMail(informationtitle+"（信息推送）", linkcontent, "0", userName, Long.valueOf(userId), empIds_mail, informationMailSendName, domainId);
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
           String userAccounts = "";
           UserBD userBD = new UserBD();
           String[] empIdArray = (empIds.substring(1) + "$").split("\\$\\$");
           //即时通讯提醒
           if(remindType.indexOf("im")>-1){
               logger.debug("========即时通讯IM start========");
               new SendRealTimeMessage(empIdArray, informationtitle ,url, userName).start();
               /*Realtimemessage rm = new Realtimemessage();
               for(int i=0;i<empIdArray.length;i++){
                   String empId = empIdArray[i];
                   if(!empId.equals("-1")&&!empId.equals("0")){
                       List userInfo = userBD.getUserInfo(Long.valueOf(empId));
                       Object[] obj = (Object[]) userInfo.get(0);
                       if(obj[2]!=null){
                           String userAccount = obj[2].toString();
                           String userPassword = obj[4].toString();
                           rm.sendNotify(userAccount, "信息推送", informationtitle, "点击查看", "information", userAccount, userPassword, url);
                       }
                   }
               }*/
           }
           //短信提醒
           if(remindType.indexOf("sms")>-1){
               String userIdStr = "";
               for(int i=0;i<empIdArray.length;i++){
                   String empId = empIdArray[i];
                   userIdStr += empId+",";
               }
               if(!userIdStr.equals("")){
                   userIdStr = userIdStr.endsWith(",")?userIdStr.substring(0,userIdStr.lastIndexOf(",")):userIdStr;
               }
               //20160109 -by jqq 短信发送改造:使用（格式处理前）短信发送方法
               com.whir.ezoffice.message.bd.MessageBD messageSend = new com.whir.ezoffice.message.bd.MessageBD();   
               //ModelSendMsg sendMsg = new ModelSendMsg();
               logger.debug("========短信提醒message start========");
               boolean sendSuccess = messageSend.modelSendMsg(userIdStr, 
                       informationtitle+"（信息推送）", "", domainId, userId);
               /*boolean sendSuccess = sendMsg.sendSystemMessage("信息管理", 
                       informationtitle+"（信息推送）", userIdStr, "", request);*/
           }
           //微信提醒
           String userIdStr = "";
           for(int i=0;i<empIdArray.length;i++){
               String empId = empIdArray[i];
               userIdStr += empId+"|";
           }
           if(!userIdStr.equals("")){
               userIdStr = userIdStr.endsWith("|")?userIdStr.substring(0,userIdStr.lastIndexOf("|")):userIdStr;
           }
           java.util.Map param  = new HashMap();
           param.put("informationId", informationId);
           param.put("informationType", informationType);
           param.put("channelId", channelId);
           
           logger.debug("channelId:"+channelId);
           
           //20151211 -by jqq 手机端推送的标题改造
           String result_title = "新推送：【";
           result_title += channelName + "】" + informationtitle;
           logger.debug("========信息推送手机userIdStr:" + userIdStr);
           boolean success = new com.whir.evo.weixin.bd.WeiXinBD().sendMsg(userIdStr,result_title,null,null,null,"information",param);
       } else {
           result = false;
       }

       return url;

   }
   
   private class SendRealTimeMessage extends Thread {
	   private String[] empIdArray;
	   private String informationtitle;
	   private String url;
	   private String userName;
	   
	   public SendRealTimeMessage(String[] empIdArray, String informationtitle, String url, String userName) {
		   super();
		   this.empIdArray = empIdArray;
		   this.informationtitle = informationtitle;
		   this.url = url;
		   this.userName = userName;
	   }

	   public void run() {
		   UserBD userBD = new UserBD();
		   Date date = new Date();
		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		   String dateStr = sdf.format(date);
		   Realtimemessage rm = new Realtimemessage();
	       for(int i=0;i<empIdArray.length;i++){
	    	   String empId = empIdArray[i];
	    	   if(!empId.equals("-1")&&!empId.equals("0")){
	        	   List userInfo = userBD.getUserInfo(Long.valueOf(empId));
	        	   Object[] obj = (Object[]) userInfo.get(0);
	        	   if(obj[2]!=null){
	        		   String userAccount = obj[2].toString();
	        		   String userPassword = obj[4].toString();
        			   rm.sendNotify(userAccount, "信息推送", userName+" "+dateStr, informationtitle, "information", userAccount, userPassword, url);
	        	   }
	    	   }
	       }
	   }
   }

   /**
    * 取公文同步到信息的信息对象数组
    * @param infoId
    * @return
    */
   public List getFromGOVInfo(String[] infoId){
	   List list = new ArrayList();
	   try{
           ParameterGenerator pg=new ParameterGenerator(1);
           pg.put(infoId,java.lang.String[].class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           list = (List) ejbProxy.invoke("getFromGOVInfo", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to getFromGOVInfo information :" + ex.getMessage());
       }
	   return list;
   }

   /**
    * 更新信息历史版本修改备注
    * @param historyId
    * @param historyMark
    */
   public void updateHistory(String historyId,String historyMark){
	   try{
           ParameterGenerator pg=new ParameterGenerator(2);
           pg.put(historyId,java.lang.String.class);
           pg.put(historyMark,java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           ejbProxy.invoke("updateHistory", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to updateHistory information :" + ex.getMessage());
       }
   }
   
   /**
    * 取用户查看信息的范围HQL
    * @param userId
    * @param orgId
    * @return
    */
   public String getUserViewInfo(String userId,String orgId) {
	   String hql = "";
	   try{
           ParameterGenerator pg=new ParameterGenerator(2);
           pg.put(userId,java.lang.String.class);
           pg.put(orgId,java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           hql = (String) ejbProxy.invoke("getUserViewInfo", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to getUserViewInfo information :" + ex.getMessage());
       }
       return hql;
   }
   
   /**
    * 取信息PO
    * @param informationId
    * @return
    */
   public InformationPO load(String informationId){
	   InformationPO po = new InformationPO();
	   try{
           ParameterGenerator pg=new ParameterGenerator(1);
           pg.put(informationId,java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           po = (InformationPO) ejbProxy.invoke("load", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to getUserViewInfo information :" + ex.getMessage());
       }
       return po;
   }
   
   /**
    * 根据信息Id取其栏目全称
    * @return
    * @throws Exception 
    */
   public String getChannelNameString(String infoId) throws Exception{
	   String result = "";
	   begin();
	    try {
	    	String channelType = "";
	    	String userDefine = "";
	    	String channelNameString = "";
	    	String hql = "select aaa.channelNameString, aaa.channelType, aaa.userDefine from com.whir.ezoffice.information.infomanager.po.InformationPO bbb " +
	    			"join bbb.informationChannel aaa where bbb.informationId = " + infoId;
	    	List list = session.createQuery(hql).list();
	    	if(list!=null && list.size()>0){
	    		Object[] obj = (Object[]) list.get(0);
	    		channelNameString = obj[0].toString();
	    		channelType = obj[1].toString();
	    		userDefine = obj[2].toString();
	    		if (channelType.equals("0")) {// 信息管理
	    			result = "信息管理." + channelNameString;
				} else if (userDefine != null && "1".equals(userDefine)) {// 自定义频道
					List list1 = session.createQuery("select po.userChannelName from com.whir.ezoffice.information.channelmanager.po.UserChannelPO po " +
							"where po.userChannelId=" + channelType).list();
					if(list1!=null && list1.size()>0){
						result = (String)list1.get(0) + "." + channelNameString;
					}
				} else {// 单位主页
					List list2 = session.createQuery("select po.orgName from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgId="
							+ channelType).list();
					if(list2!=null && list2.size()>0){
						result = (String)list2.get(0) + "." + channelNameString;
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
	   return result;
   }

   /**
    * 判断用户对某条信息中的附件的下载次数是否已达上限
    * @param userId
    * @param informationId
    * @return
    */
   public String userDownloadNum(String userId, String informationId, String accessorySaveName){
	   String result = "";
	   try{
           ParameterGenerator pg=new ParameterGenerator(3);
           pg.put(userId,java.lang.String.class);
           pg.put(informationId,java.lang.String.class);
           pg.put(accessorySaveName,java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           result = (String) ejbProxy.invoke("userDownloadNum", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to userDownloadNum information :" + ex.getMessage());
       }
	   return result;
   }

   /**
    * 判断用户对某条信息的打印次数，若扔可打印，则直接插入数据（更新打印次数）
    * @param userId
    * @param informationId
    * @return
    */
   public String userPrintNum(String userId, String informationId){
	   String result = "";
	   try{
           ParameterGenerator pg=new ParameterGenerator(2);
           pg.put(userId,java.lang.String.class);
           pg.put(informationId,java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           result = (String) ejbProxy.invoke("userPrintNum", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to userPrintNum information :" + ex.getMessage());
       }
	   return result;
   }

   /**
    * 更新用户下载附件的次数
    * @param userId
    * @param informationId
    * @param accessorySaveName 1-N个附件保存名
    * @return
    * @throws Exception 
    */
   public String updateDownloadNum(String userId, String informationId, String accessorySaveName){
	   String result = "";
	   try{
           ParameterGenerator pg=new ParameterGenerator(3);
           pg.put(userId,java.lang.String.class);
           pg.put(informationId,java.lang.String.class);
           pg.put(accessorySaveName,java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           result = (String) ejbProxy.invoke("updateDownloadNum", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to updateDownloadNum information :" + ex.getMessage());
       }
	   return result;
   }

   /**
    * 根据信息ID和权限sql判断用户权限
    * @param informationId
    * @param scope
    * @return
    */
   public String hasRight(String informationId, String rightScope){
	   String result = "";
	   try{
           ParameterGenerator pg=new ParameterGenerator(2);
           pg.put(informationId,java.lang.String.class);
           pg.put(rightScope,java.lang.String.class);
           EJBProxy ejbProxy = new InformationEJBProxy("InformationEJB", "InformationEJBLocal", InformationEJBHome.class);
           result = (String) ejbProxy.invoke("hasRight", pg.getParameters());
       }catch(Exception ex){
           logger.error("error to hasRight information :" + ex.getMessage());
       }
	   return result;
   }
   
   /**
    * 取某个栏目下的信息list
    * @param channelId
    * @return List {informationId,otherChannel}
    * @throws HibernateException 
    */
    public List getInformationByChannelId(String channelId) throws Exception{
    	List list = null;
	   	try {
	   		begin();
	   		String hql = "select bbb.informationId, bbb.otherChannel from com.whir.ezoffice.information.infomanager.po.InformationPO bbb " +
	   			"join bbb.informationChannel aaa where aaa.channelId = " + channelId;
	   		list = session.createQuery(hql).list();
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
     * 判断某流程是否已同步至信息管理
     * @param recordId 流程实例Id
     * @param moduleId 流程模块Id
     * @throws HibernateException 
     */
    public boolean getInfoFromWorkFlow(String recordId, String moduleId) throws Exception{
    	boolean result = false;
    	try {
	   		begin();
	   		String hql = "select bbb.informationId from com.whir.ezoffice.information.infomanager.po.InformationPO bbb " +
	   			"where bbb.fromGOV = 2 and bbb.fromGOVDocument = " + recordId + " and bbb.wfModuleId = '" + moduleId + "'";
	   		List list = session.createQuery(hql).list();
	   		if(list!=null && list.size()>0){
	   			result = true;
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
    	return result;
    }
    
    /**
     * 信息点赞功能
     * @param praiseUserId 点赞用户Id
     * @param praiseInformationId 点赞信息Id
     * @param praiseType 点赞类型
     * @throws HibernateException 
     */
    public String updateInfoPraise(String praiseUserId, String praiseInformationId, String praiseType) throws Exception{
    	InformationEJBBean info = new InformationEJBBean();
        String flag = "0";
        flag = info.updateInfoPraise(praiseUserId, praiseInformationId, praiseType);
        return flag;
    }
    
    /**
     * 判断用户是否对信息已点赞
     * @param praiseUserId 点赞用户Id
     * @param praiseInformationId 点赞信息Id
     * @throws HibernateException 
     */
    public String hasPraise(String praiseUserId, String praiseInformationId) throws Exception{
    	String flag = "0";
    	try {
    		//判空
    		if (CommonUtils.isEmpty(praiseUserId) || CommonUtils.isEmpty(praiseInformationId)){
    			return flag;
    		}
    		//查询该用户对该信息的点赞记录
    		begin();
    		StringBuffer sql_praise = new StringBuffer();
    		sql_praise.append("select po.praiseId from com.whir.ezoffice.information.infomanager.po.InformationPraisePO po ")
    		.append("where po.praiseUserId=" + praiseUserId + " and po.praiseType = 1 and po.praiseInformationId=" + praiseInformationId);
        
        	Query query_praise = this.session.createQuery(sql_praise.toString());
            List list_praise = query_praise.list();
            //点赞有记录，返回true表明该用户已经对该信息点赞
            if ((list_praise != null) && (list_praise.size() > 0)) {
              flag = "1";
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
        
        return flag;
    }
    
    /**
     * 信息查看详细记录
     * @throws Exception 
     */
	public List viewRecords(String userId, String informationId) throws Exception {
		
		List list = null;
        begin();
		StringBuffer sql = new StringBuffer("");
		sql.append("select aaa.browserId,aaa.browserName,aaa.browserOrgName,aaa.browseTime ")
		.append("from  com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb ");
	    if (!CommonUtils.isEmpty(userId)) {
	    	sql.append("where aaa.empId=" + userId + " and bbb.informationId =" + informationId );
	    }
	    if (!CommonUtils.isEmpty(informationId)) {
	    	sql.append("where aaa.empId=" + userId + " and bbb.informationId =" + informationId );
	    }
	    
        try {
            Query query = session.createQuery(sql.toString());
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
     * 信息查看阅读情况数量
     * 
     * **/
    public String viewDetailNum(String informationId) throws Exception {
    	//logger.debug("========信息查看阅读情况数量viewDetailNum开始========");
    	begin();
        String viewnum = "0";
        try {
            //20160328 -by jqq 安全性漏洞sql注入改造
            Query query = session
                    .createQuery("select count(aaa.browserId) from com.whir.ezoffice.information.infomanager.po.InformationBrowserPO aaa join aaa.information bbb where bbb.informationId =:informationId ");
            viewnum = query.setParameter("informationId", informationId)
                    .iterate().next().toString();
        } catch (Exception e) {
            System.out.println("---------------------------------------------");
            e.printStackTrace();
            System.out.println("---------------------------------------------");
        } finally {
            session.close();
            session = null;
            transaction = null;
        }
        return viewnum;
    }
    
    /**
     * 信息查看保存阅读流水记录
     * 
     * **/
    public void saveViewRecord(String userId, String userName, String orgId,String orgName, String informationId) throws Exception {
		begin();
		try {
			InformationViewRecordPO informationViewRecordPO = new InformationViewRecordPO();
			informationViewRecordPO.setEmpId(new Long(userId));
			informationViewRecordPO.setViewerName(userName);
			informationViewRecordPO.setViewerOrgName(orgName);
			informationViewRecordPO.setViewTime(new Date());
			informationViewRecordPO.setOrgId(new Long(orgId));
			informationViewRecordPO.setDomainId(new Long(0));
			informationViewRecordPO.setInformationId(Long.valueOf(informationId));
			Long RecordId = (Long) session.save(informationViewRecordPO);
			session.flush();
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
     * 判断用户对某条信息的打印次数，若扔可打印，则直接插入数据（更新打印次数）
     * @param userId
     * @param informationId
     * @return
     */
    public String judgePrintNum(String userId, String informationId) throws Exception{
    	String result = "";
    	begin();
    	InformationPrintPO po = null;
    	try {
			//20160615 -by jqq 判断打印次数逻辑改造：如果信息设置中次数为0直接返回1-没有打印权限
            String hql2 = "select aaa.printNum from com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
                    "where aaa.informationId =:informationId ";
            String num = (String) session.createQuery(hql2).setParameter(
                "informationId",
                informationId).iterate().next();
            //不设置下载次数（不限制）则取值为空的
            if(num==null || "".equals(num) ){
                num = "0";
            }else if("0".equals(num)){
                result = "0";
                return result;
            }else{
                num = num;
            }
            //20160328 -by jqq 安全性漏洞sql注入改造
            String hql = "select aaa from com.whir.ezoffice.information.infomanager.po.InformationPrintPO aaa " +
                    "where aaa.informationId =:informationId and aaa.userId =:userId ";
            List list = session.createQuery(hql).setParameter(
                "informationId",
                informationId).setParameter("userId", userId).list();
            if(list!=null && list.size()>0){
				po = (InformationPrintPO) list.get(0);
				long userNum = po.getPrintNum();
				if("0".equals(num) || userNum < Long.valueOf(num)){
					result = "1";
				}else{
					result = "0";
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
}
