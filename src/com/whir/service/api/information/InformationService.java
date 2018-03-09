package com.whir.service.api.information;

import java.util.List;
import java.util.ArrayList;

import com.whir.ezlucene.webClient.LuceneClient;
import com.whir.ezoffice.assetManager.bd.CommentBD;
import com.whir.ezoffice.information.infomanager.bd.InformationBD;
import com.whir.ezoffice.information.infomanager.bd.InformationAccessoryBD;
import com.whir.ezoffice.information.channelmanager.bd.ChannelBD;
import com.whir.ezoffice.information.channelmanager.bd.NewChannelBD;
import com.whir.ezoffice.information.channelmanager.bd.YiBoChannelBD;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.common.util.Constants;
import com.whir.common.config.SystemCommon;
import com.whir.common.page.Page;
import java.util.Map;
import java.util.HashMap;
import com.whir.ezoffice.information.infomanager.po.InformationPO;
import com.whir.ezoffice.information.channelmanager.po.InformationChannelPO;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import net.sf.hibernate.HibernateException;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.ParameterGenerator;
import com.whir.common.util.EJBProxy;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.ezoffice.information.common.util.InformationEJBProxy;
import com.whir.ezoffice.information.channelmanager.ejb.ChannelEJBHome;
import com.whir.ezoffice.information.infomanager.bd.NewInformationBD;
import com.whir.ezoffice.portal.bd.PortletBD;

/**
 *
 * <p>
 * Title:信息管理大类API
 * </p>
 * <p>
 * Description: 信息管理 单位主页 信息 与栏目的相关操作
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: WanHu Internet Resource(Hefei) C0. Ltd
 * </p>
 *
 * @author wanggl
 * @version 1.0
 */
public class InformationService {
    private static Logger logger = Logger.getLogger(InformationService.class.getName());
	public InformationService() {
	}

	/**
    *
    * @param infomationXml
    *            String
    * @return String 返回的信息id
    */
   public String saveInformation(InformationPO tmppo, String channelId, String[] infoPicName,
           String[] infoPicSaveName, String[] infoAppendName, String[] infoAppendSaveName) {
       String infoId = "";
       InformationPO po = new InformationPO();
       // 栏目
       InformationChannelPO cpo = getSingleChannel(channelId);
       po.setInformationChannel(cpo);

       // 下面三句话是将栏目的查看权限放到信息中来
       po.setInformationReader(cpo.getChannelReader());
       po.setInformationReaderGroup(cpo.getChannelReaderGroup());
       po.setInformationReaderOrg(cpo.getChannelReaderOrg());
       po.setInformationReaderName(cpo.getChannelReaderName());
       po.setInformationTitle(tmppo.getInformationTitle());// 信息标题
       po.setInformationType("0");// 信息类别
       po.setInformationContent(tmppo.getInformationContent());// 信息内容

       po.setInformationIssuerId(tmppo.getInformationIssuerId());// 创建人Id
       po.setInformationIssuer(tmppo.getInformationIssuer());// 创建人姓名
       po.setInformationIssueOrgId(tmppo.getInformationIssueOrgId());
       po.setInformationIssueOrg(tmppo.getInformationIssueOrg());
       po.setInformationIssueTime(new Date());
       //po.setInformationModifyTime(new Date());

       po.setInformationValidType(0);
       po.setInformationKits(0);
       po.setInformationVersion("1.00");
       po.setInformationCommonNum(0);
       po.setInfoDepaFlag(0);

       po.setInfoDepaFlag2(0);
       po.setForbidCopy(0);
       po.setTransmitToEzsite(0);
       po.setDisplayTitle(1);
       po.setOtherChannel(",0,");

       po.setTitleColor(new Integer(0));
       po.setDossierStatus(new Integer(0));
       po.setMustRead(new Integer(0));
       po.setIsConf(new Integer(0));
       po.setDocumentNo("0");

       po.setDocumentType("0");
       po.setDomainId(tmppo.getDomainId());
       po.setIssueOrgIdString(tmppo.getIssueOrgIdString());// 需要修改
       po.setDisplayImage("1");
       // po.setfromgovdocument
       po.setInformationOrISODoc("0");
       po.setIsoDocStatus("0");
       po.setIsoSecretStatus("0");
       po.setOrderCode("1000");
       po.setInformationCanRemark(new Long(1));
       po.setInformationHead(0);//
       
       po.setDisplayTitle(tmppo.getDisplayTitle());
       po.setTitleColor(tmppo.getTitleColor());

       InformationBD ibd = new InformationBD();
       String[] para = new String[] {
               tmppo.getInformationIssuerId().toString(),
               tmppo.getInformationIssuer(), tmppo.getInformationIssueOrgId(),
               tmppo.getInformationIssueOrg(), tmppo.getIssueOrgIdString() };
       Long id = ibd.add(po, para, null, infoPicName, infoPicSaveName, infoAppendName, infoAppendSaveName,
               "0");
       return id.toString();
   }

	/**
	 * 取信息的所有栏目
	 *
	 * @param domainId
	 *            String
	 * @return List
	 */
	public List getInformationChannel(String domainId) {
		List list = new ArrayList();
		return list;
	}

	/**
	 * 取信息列表
	 *
	 * @param queryStr
	 *            String[] <br>
	 *            String domainId = queryStr[0] 域标识 =====*默认为：0* <br>
	 *            String userId = queryStr[1] 用户id <br>
	 *            String orgId = queryStr[2] 组织id <br>
	 *            String orgIdString = queryStr[3] 组织的orgIdString <br>
	 *            String type = queryStr[4] 当为all时取所有 此处暂固定为all <br>
	 *            String userDefine = queryStr[5] <br>
	 *            channelType =0 时 信息管理 <br>
	 *            当 channelType 不为0时 { <br>
	 *            userDefine=1 字定义频道 channelType为自定频道的id <br>
	 *            userDefine！=1时 单位主页 channelType为orgId <br> } =====*默认为：0* <br>
	 *            String informationId = queryStr[6] 信息的id =====*默认为空* <br>
	 *            String search = queryStr[7] 是否是查找 1：是查找 <br>
	 *            String userChannelName = queryStr[8] 频道名 =====*默认为：信息管理* <br>
	 *            String channelType = queryStr[9] 栏目的类型 =====*默认为：0* <br>
	 *            String depart = queryStr[10] 是否是单位主页 <br>
	 *            String searchDate = queryStr[11] 是否查询日期 1：是 <br>
	 *            String searchBeginDate = queryStr[12] 查询开始日期 <br>
	 *            String searchEndDate = queryStr[13] 查询结束日期 <br>
	 *            String searchIssuerName = queryStr[14] 查询的发布人 <br>
	 *            String searchKeywordType =queryStr[15] 查询的关键字类型 <br>
	 *            String searchKeyword = queryStr[16] 查询的关键字内容 <br>
	 *            String title = queryStr[17] 标题 <br>
	 *            String key = queryStr[18] 关键字 <br>
	 *            String subtitle = queryStr[19] 副标题 <br>
	 *            String append = queryStr[20] 附件名 <br>
	 *            String searchChannel =queryStr[21] 查询的栏目id =====*默认为：0* <br>
	 *            String pager_offset=queryStr[22] 翻页 的 第几页 =====*默认为：0*
	 * @return List 返回的List 第一层存两个List0 ,List1<br>
	 *         List0 存n条 List00至List0n 每一条List0n存的是每一条信息的具体内容<br>
	 *         List0n里存的先后顺序是： informationId 信息id <br>
	 *         informationTitle 信息标题<br>
	 *         informationKits 点击次数<br>
	 *         informationIssuer 发布人<br>
	 *         informationVersion 版本信息<br>
	 *         informationIssueTime发布日期 <br>
	 *         informationSummary 摘要 <br>
	 *         informationHead红头 <br>
	 *         informationType信息类型<br>
	 *         informationCommonNum 评论数 <br>
	 *         channelName 栏目名称 <br>
	 *         channelId 栏目id <br>
	 *         titleColor 标题颜色 <br>
	 *         isConf <br>
	 *         documentNo 编号 <br>
	 *         transmitToEzsite 是否发送到网站 <br>
	 *         informationModifyTime最后修改时间 <br>
	 *         orderCode 排序码 <br>
	 *         informationIssueOrg 发布人组织 <br>
	 *         informationIsCommend 是否推荐 <br>
	 *         channelType 栏目的类型 <br>
	 *         List1 存1条 List10 List10的 List10.get(0):pager_offset 翻页的页数
	 *         List10.get(1):recordCount 总格记录条数<br>
	 *
	 */
	public List getInformationList_Mobile(Object inobj[]) {
		/** @todo: complete the business logic here, this is just a skeleton. */

		String domainId = inobj[0] == null ? "" : inobj[0].toString(); // 域标识
		String userId = inobj[1] == null ? "488" : inobj[1].toString(); // 域标识
		String orgId = inobj[2] == null ? "123" : inobj[2].toString(); // 域标识
		String orgIdString = inobj[3] == null ? "$120$$123$" : inobj[3]
				.toString();
		String type = inobj[4] == null ? "all" : inobj[4].toString(); // all
		String userDefine = inobj[5] == null ? "0" : inobj[5].toString(); // 0
		String informationId = inobj[6] == null ? "" : inobj[6].toString();
		String search = inobj[7] == null ? "" : inobj[7].toString();
		String userChannelName = inobj[8] == null ? "" : inobj[8].toString();
		String channelType = inobj[9] == null ? "" : inobj[9].toString();
		String depart = inobj[10] == null ? "" : inobj[10].toString();
		String searchDate = inobj[11] == null ? "" : inobj[11].toString();
		String searchBeginDate = inobj[12] == null ? "" : inobj[12].toString();
		String searchEndDate = inobj[13] == null ? "" : inobj[13].toString();
		String searchIssuerName = inobj[14] == null ? "" : inobj[14].toString();

		// String keywordType = inobj[15] == null ? "" : inobj[15].toString();
		// String keyword = inobj[16] == null ? "" : inobj[16].toString();

		String searchKeywordType = inobj[15] == null ? "" : inobj[15]
				.toString();
		String searchKeyword = inobj[16] == null ? "" : inobj[16].toString();

		// String searchKeywordType ="";//
		// httpServletRequest.getParameter("keywordType");
		// String searchKeyword =
		// "";//httpServletRequest.getParameter("keyword");

		String title = inobj[17] == null ? "" : inobj[17].toString();
		String key = inobj[18] == null ? "" : inobj[18].toString();
		String subtitle = inobj[19] == null ? "" : inobj[19].toString();
		String append = inobj[20] == null ? "" : inobj[20].toString();
		String searchChannel = inobj[21] == null ? "0" : inobj[21].toString();
		String pager_offset = inobj[22] == null ? "" : inobj[22].toString();

		InformationBD informationBD = new InformationBD();

		ChannelBD channelBD = new ChannelBD();
		// 取用户可以查看的所有栏目,并返回到页面供搜索取栏目ID用
		List list = channelBD.getUserViewCh(userId, orgId, channelType,
				userDefine, domainId);

		java.util.Date now = new java.util.Date();
		String nowString = now.toLocaleString();
		nowString = nowString.substring(0, nowString.indexOf(" "));

		/**
		 * 取所有信息的条件为 1.信息的发布人 2.当前用户在信息的可维护人中 当前用户为栏目的可维护人 具有信息维护权限
		 * 信息的创建人或创建组织在维护范围内 3.当前用户在栏目的可查看范围内并且在信息的可查看范围内 4.栏目或信息的可查看人范围都没有选择
		 * 4.channelType=0
		 *
		 * where ((1) or (2) or (3)) and (4) (1): aaa.informationIssuerId=userId
		 * (2):
		 *
		 */
		String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, "
				+ " aaa.informationIssuer, aaa.informationVersion, "
				+ " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead,"
				+ " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId,"
				+ " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite,"
				+ " aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg,aaa.informationIsCommend,bbb.channelType";

		/*
		 * String viewSQL = " aaa.informationId, aaa.informationTitle,
		 * aaa.informationKits, " + " aaa.informationIssuer,
		 * aaa.informationVersion,aaa.informationIssueTime " ;
		 */
		String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa "
				+ " join aaa.informationChannel bbb ";
		String whereSQL = "";
		String databaseType = com.whir.common.config.SystemCommon
				.getDatabaseType();
		if (databaseType.indexOf("mysql") >= 0) {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or '"
					+ nowString
					+ "' between aaa.validBeginTime and aaa.validEndTime ) ";
		} else {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or EZOFFICE.FN_STRTODATE('"
					+ nowString
					+ "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
		}
		whereSQL += " and (  bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0'  ) ";
		
		ManagerBD managerBD = new ManagerBD();
		//用户的信息维护权限范围
        String rightWhere = managerBD.getRightFinalWhere(userId, orgId, "01*03*03", "aaa.informationIssueOrgId","aaa.informationIssuerId");
        //可维护的所有栏目
        String canReadAllInfoChannel = informationBD.getAllInfoChannel(userId, orgId, channelType, userDefine);
        //栏目可维护人
        String scopeWhere = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, "bbb.channelManager","bbb.channelManagerOrg", "bbb.channelManagerGroup");
        //栏目可查看人
        String viewscopeWhere = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, "bbb.channelReader","bbb.channelReaderOrg", "bbb.channelReaderGroup");
        String ttemp = "";
        //信息可查看人
        String readerWhere = informationBD.getInfoReader(userId, orgId, orgIdString, "aaa");
        if (readerWhere != null && !readerWhere.equals("")) {
            ttemp = ttemp + " and (" + readerWhere + ") ";
        }
        whereSQL += " and ((bbb.channelId in (" + canReadAllInfoChannel + ") or (" + scopeWhere + ") " 
				+ "or (" + rightWhere + ") or ((" + viewscopeWhere+ " or bbb.channelReaderName is null or bbb.channelReaderName='') "
				+ ttemp + ") or aaa.informationIssuerId=" + userId + ") ";
		String hSql = "";
		hSql = informationBD.getUserViewCh(userId, orgId, channelType, userDefine);
		
		List hsqlList = informationBD.getUserViewCh2(userId, orgId, channelType, userDefine);
		String whereTmp = "";
		if (hsqlList != null && hsqlList.size() != 0) {
            whereTmp = whereTmp + " and (( bbb.channelId in (" + hSql + ") ";
            if(!channelType.equals("-1")){
        		whereTmp += " and bbb.channelType = " + channelType;
        	}
            whereTmp += ")";
            whereTmp += ")";
        } else {
            whereTmp = whereTmp + " and bbb.channelId in (" + hSql + ") ";
        }
		whereSQL += whereTmp + ")";
		
		/*
		String twoCql[] = informationBD.getUserViewChAll(userId, orgId);
		List allchlist = informationBD.getUserViewChAll2(userId, orgId);

		boolean hasRight_info = false;
		boolean hasRight_depart = false;
		String rightCode_info = "01*03*03";
		String rightCode_depart = "01*01*02";
		List rightScopeList_depart = new com.whir.org.manager.bd.ManagerBD()
				.getRightScope(userId, rightCode_depart);
		if (rightScopeList_depart != null && rightScopeList_depart.size() > 0
				&& rightScopeList_depart.get(0) != null) {
			Object[] obj = (Object[]) rightScopeList_depart.get(0);
			if ("0".equals(obj[0].toString())) {
				hasRight_depart = true;
			}
		}

		List rightScopeList_info = new com.whir.org.manager.bd.ManagerBD()
				.getRightScope(userId, rightCode_info);
		if (rightScopeList_info != null && rightScopeList_info.size() > 0
				&& rightScopeList_info.get(0) != null) {
			Object[] obj = (Object[]) rightScopeList_info.get(0);
			if ("0".equals(obj[0].toString())) {
				hasRight_info = true;
			}
		}

		List userMenuChannelList = new ChannelBD().getAllUserMenuChannel(
				domainId, "");
		String ct = " ( 0 ";
		if (userMenuChannelList != null && userMenuChannelList.size() > 0) {
			for (int i = 0; i < userMenuChannelList.size(); i++) {
				ct += "," + userMenuChannelList.get(i);
			}
		}
		ct += " )";
		String whereTmp1 = "";
		String whereTmp2 = "";

		whereTmp1 += " and  (  bbb.channelType in " + ct + "  ";
		if (hasRight_info) {

		} else {
			whereTmp1 = " and  ((  bbb.channelType in " + ct + "  "
					+ " and bbb.channelId in (" + twoCql[0] + ")";
			whereTmp1 += " and bbb.channelType = " + channelType;
			InformationBD infoBD = new InformationBD();
			String readerWhere = infoBD.getInfoReader(userId, orgId,
					orgIdString, "aaa");

			if (readerWhere == null || readerWhere.equals("")) {

			} else {
				whereTmp1 = whereTmp1 + " and (" + readerWhere + ") )";
			}

		}

		whereTmp1 += " or bbb.channelId in (" + twoCql[1] + ") )";

		InformationBD infoBD = new InformationBD();
		String readerWhere = infoBD.getInfoReader(userId, orgId, orgIdString,
				"aaa");

		if (hasRight_info || hasRight_depart) {
		} else {
			if (readerWhere == null || readerWhere.equals("")) {
			} else {
				whereTmp1 = whereTmp1 + " and (" + readerWhere + ") ";
			}
		}

		whereSQL += whereTmp1;
		whereSQL += ")";
		*/
		if (search != null && !search.equals("") && search.equals("1")) {

			// 如果用户搜索是选择了栏目则从该栏目选择否则从所有可以查看的栏目中选择
			if (!searchChannel.equals("0") && !searchChannel.equals("")
					&& !searchChannel.equals("null")) {
				InformationChannelPO po = channelBD.loadChannel(searchChannel);
            	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
            		whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
            	}else{//不包含子栏目则只查该栏目中的信息
            		whereSQL = whereSQL + " and (bbb.channelId = " + searchChannel + " or aaa.otherChannel like '%," + searchChannel + ",%') ";
            	}
			}

			// 搜索时指定日期
			if (searchDate != null && !searchDate.equals("")
					&& !searchDate.equals("null")) {

				if (databaseType.indexOf("mysql") >= 0) {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between '"
							+ searchBeginDate + "' and '" + searchEndDate
							+ " 23:59:59' ";
				} else {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between EZOFFICE.FN_STRTODATE('"
							+ searchBeginDate
							+ "','S') and EZOFFICE.FN_STRTODATE('"
							+ searchEndDate + " 23:59:59','') ";
				}

			}

			// 搜索指定发布人

			if (!searchIssuerName.equals("")) {
				whereSQL = whereSQL + " and aaa.informationIssuer like '%"
						+ searchIssuerName + "%' ";
			}
			// 搜索关键字的类型及内容
			// String searchKeywordType ="";//
			// httpServletRequest.getParameter("keywordType");
			// String searchKeyword =
			// "";//httpServletRequest.getParameter("keyword");

			if (title != null && !title.equals("")) {
				// 标题
				whereSQL = whereSQL + " and aaa.informationTitle like '%"
						+ title + "%' ";
			}
			if (key != null && !key.equals("")) {
				// 关键字
				whereSQL = whereSQL + " and aaa.informationKey like '%" + key
						+ "%'";
			}
			if (subtitle != null && !subtitle.equals("")) {
				// 附标题
				whereSQL = whereSQL + " and aaa.informationSubTitle like '%"
						+ subtitle + "%'";
			}

			if (append != null && !append.equals("")) {
				// 附件名
				whereSQL = whereSQL
						+ " and ( select count(*) from "
						+ " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc "
						+ " where ccc.accessoryName like '%"
						+ append
						+ "%' and ccc.information.informationId = aaa.informationId) > 0 ";
			}

		}
		
		if (inobj.length > 23) {
            String retrievalKey = "" + inobj[23];
            if(!retrievalKey.equals("")&&!retrievalKey.equals("null")){
                LuceneClient luceneClient = new LuceneClient();
                String lids = luceneClient.getIdByLucenceRestult("",
                        "information",
                        retrievalKey);
                if (lids != null && !lids.equals("null") && lids.length() > 1) {
                    whereSQL += " and aaa.informationId in(" + lids + ") ";
                } else {
                    whereSQL += " and (1>2) ";
                }

            }
        }

		whereSQL = whereSQL
				+ " order by aaa.orderCode desc,case when aaa.informationModifyTime is null then aaa.informationIssueTime else aaa.informationModifyTime end desc";
		String pageSize = "";
		if(inobj.length > 24){
			pageSize = inobj[24] == null ? "" : inobj[24].toString();
		}
		List resultlist = new ArrayList();
		resultlist = list(pager_offset, viewSQL, fromSQL, whereSQL, pageSize);
		return resultlist;
	}
	
	/**
	 * 取信息列表,只根据信息的可查看人范围来判断
	 *
	 * @param queryStr
	 *            String[] <br>
	 *            String domainId = queryStr[0] 域标识 =====*默认为：0* <br>
	 *            String userId = queryStr[1] 用户id <br>
	 *            String orgId = queryStr[2] 组织id <br>
	 *            String orgIdString = queryStr[3] 组织的orgIdString <br>
	 *            String type = queryStr[4] 当为all时取所有 此处暂固定为all <br>
	 *            String userDefine = queryStr[5] <br>
	 *            channelType =0 时 信息管理 <br>
	 *            当 channelType 不为0时 { <br>
	 *            userDefine=1 字定义频道 channelType为自定频道的id <br>
	 *            userDefine！=1时 单位主页 channelType为orgId <br> } =====*默认为：0* <br>
	 *            String informationId = queryStr[6] 信息的id =====*默认为空* <br>
	 *            String search = queryStr[7] 是否是查找 1：是查找 <br>
	 *            String userChannelName = queryStr[8] 频道名 =====*默认为：信息管理* <br>
	 *            String channelType = queryStr[9] 栏目的类型 =====*默认为：0* <br>
	 *            String depart = queryStr[10] 是否是单位主页 <br>
	 *            String searchDate = queryStr[11] 是否查询日期 1：是 <br>
	 *            String searchBeginDate = queryStr[12] 查询开始日期 <br>
	 *            String searchEndDate = queryStr[13] 查询结束日期 <br>
	 *            String searchIssuerName = queryStr[14] 查询的发布人 <br>
	 *            String searchKeywordType =queryStr[15] 查询的关键字类型 <br>
	 *            String searchKeyword = queryStr[16] 查询的关键字内容 <br>
	 *            String title = queryStr[17] 标题 <br>
	 *            String key = queryStr[18] 关键字 <br>
	 *            String subtitle = queryStr[19] 副标题 <br>
	 *            String append = queryStr[20] 附件名 <br>
	 *            String searchChannel =queryStr[21] 查询的栏目id =====*默认为：0* <br>
	 *            String pager_offset=queryStr[22] 翻页 的 第几页 =====*默认为：0*
	 * @return List 返回的List 第一层存两个List0 ,List1<br>
	 *         List0 存n条 List00至List0n 每一条List0n存的是每一条信息的具体内容<br>
	 *         List0n里存的先后顺序是： informationId 信息id <br>
	 *         informationTitle 信息标题<br>
	 *         informationKits 点击次数<br>
	 *         informationIssuer 发布人<br>
	 *         informationVersion 版本信息<br>
	 *         informationIssueTime发布日期 <br>
	 *         informationSummary 摘要 <br>
	 *         informationHead红头 <br>
	 *         informationType信息类型<br>
	 *         informationCommonNum 评论数 <br>
	 *         channelName 栏目名称 <br>
	 *         channelId 栏目id <br>
	 *         titleColor 标题颜色 <br>
	 *         isConf <br>
	 *         documentNo 编号 <br>
	 *         transmitToEzsite 是否发送到网站 <br>
	 *         informationModifyTime最后修改时间 <br>
	 *         orderCode 排序码 <br>
	 *         informationIssueOrg 发布人组织 <br>
	 *         informationIsCommend 是否推荐 <br>
	 *         channelType 栏目的类型 <br>
	 *         List1 存1条 List10 List10的 List10.get(0):pager_offset 翻页的页数
	 *         List10.get(1):recordCount 总格记录条数<br>
	 * @throws Exception 
	 *
	 */
	public List getInformationListMobile(Object inobj[]) {
        /** @todo: complete the business logic here, this is just a skeleton. */
        logger.debug("========InformationService[getInformationListMobile]  start:");
        Long starttime = System.currentTimeMillis();
        String domainId = inobj[0] == null ? "" : inobj[0].toString(); // 域标识
        String userId = inobj[1] == null ? "488" : inobj[1].toString(); // 域标识
        String orgId = inobj[2] == null ? "123" : inobj[2].toString(); // 域标识
        String orgIdString = inobj[3] == null ? "$120$$123$" : inobj[3].toString();
        String type = inobj[4] == null ? "all" : inobj[4].toString(); // all
        String userDefine = inobj[5] == null ? "0" : inobj[5].toString(); // 0
        String informationId = inobj[6] == null ? "" : inobj[6].toString();
        String search = inobj[7] == null ? "" : inobj[7].toString();
        String userChannelName = inobj[8] == null ? "" : inobj[8].toString();
        String channelType = inobj[9] == null ? "" : inobj[9].toString();
        String depart = inobj[10] == null ? "" : inobj[10].toString();
        String searchDate = inobj[11] == null ? "" : inobj[11].toString();
        String searchBeginDate = inobj[12] == null ? "" : inobj[12].toString();
        String searchEndDate = inobj[13] == null ? "" : inobj[13].toString();
        String searchIssuerName = inobj[14] == null ? "" : inobj[14].toString();
        // String keywordType = inobj[15] == null ? "" : inobj[15].toString();
        // String keyword = inobj[16] == null ? "" : inobj[16].toString();
        String searchKeywordType = inobj[15] == null ? "" : inobj[15].toString();
        String searchKeyword = inobj[16] == null ? "" : inobj[16].toString();
        // String searchKeywordType ="";//
        // httpServletRequest.getParameter("keywordType");
        // String searchKeyword =
        // "";//httpServletRequest.getParameter("keyword");
        String title = inobj[17] == null ? "" : inobj[17].toString();
        String key = inobj[18] == null ? "" : inobj[18].toString();
        String subtitle = inobj[19] == null ? "" : inobj[19].toString();
        String append = inobj[20] == null ? "" : inobj[20].toString();
        String searchChannel = inobj[21] == null ? "0" : inobj[21].toString();
        String pager_offset = inobj[22] == null ? "" : inobj[22].toString();
        String pageSize = "";
        String userChannelId = "0";
        if(inobj.length > 24){
            pageSize = inobj[23] == null ? "" : inobj[23].toString();
            //20151112 -by jqq 自定义信息频道id
            userChannelId = inobj[24] == null || "".equals(inobj[24].toString()) ? "0" : inobj[24].toString();
        }
        
        java.util.Date now = new java.util.Date();
        String nowString = now.toLocaleString();
        nowString = nowString.substring(0, nowString.indexOf(" "));

        /*String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, "
            + " aaa.informationIssuer, aaa.informationVersion, "
            + " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead,"
            + " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId,"
            + " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite,"
            + " aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg,aaa.informationIsCommend,bbb.channelType";*/
        String viewSQL = " inf.information_id, inf.informationTitle, inf.informationKits, inf.informationIssuer, " +
        " inf.informationVersion, inf.informationIssueTime, inf.informationSummary, inf.informationHead," +
        " inf.informationType, inf.informationCommonNum, ch.channelName, ch.channel_id, inf.titleColor," +
        " inf.isConf, inf.documentNo, inf.transmitToEzsite, inf.informationModifyTime, inf.orderCode," +
        " inf.informationIssueOrg, inf.informationIsCommend, ch.channelType ";

        /*String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa "
                + " join aaa.informationChannel bbb ";*/
        String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO inf join inf.informationChannel ch ";
        String whereSQL = "";
        String databaseType = com.whir.common.config.SystemCommon
                .getDatabaseType();
        CustomizeService cs = new CustomizeService();
        boolean departInfo = false;
        try {
            departInfo = cs.hasDapartAuth(userId, orgIdString, domainId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (databaseType.indexOf("mysql") >= 0) {
            whereSQL = " where inf.domain_Id="
                    + domainId
                    + " and inf.informationStatus=0 and ( inf.informationValidType = 0 or '"
                    + nowString
                    + "' between inf.validBeginTime and inf.validEndTime ) ";
        } else {
            whereSQL = " where inf.domain_Id="
                    + domainId
                    + " and inf.informationStatus=0 and ( inf.informationValidType = 0 or EZOFFICE.FN_STRTODATE('"
                    + nowString
                    + "','S') between inf.validBeginTime and inf.validEndTime ) ";
        }
        if(!departInfo){
            //whereSQL += "and ( bbb.channelType = 0 or bbb.userDefine = 1 ) ";
        }
        
        if(channelType.equals("0") && userDefine.equals("0")){
            //信息管理
            whereSQL += "and ( ch.channelType = 0 and ch.userDefine = 0 ) ";
        }else if(channelType.equals("1") && userDefine.equals("0")){
            //单位主页
            whereSQL += "and ( ch.channelType > 0 and ch.userDefine = 0 ) ";
        }else if(channelType.equals("1") && userDefine.equals("1")){
            //20151112 -by jqq 用户自定义频道改造：从全部自定义信息变成对应频道下信息
            //whereSQL += "and ( bbb.channelType > 0 and bbb.userDefine = 1 ) ";
            if("0".equals(userChannelId)){
                //如果没有传值，返回空值
                whereSQL += "and 1=2 ";
            }else{
                //如果传递了自定义频道id值，则只查询该频道下的信息
                whereSQL += "and ( ch.channelType ="+ userChannelId +" and ch.userDefine = 1 ) ";
            }
        }else{
            //查询全部数据：信息+单位主页+自定义频道
            //20160707 -by jqq 过滤掉没有查看权限的自定义信息频道
            String noReadStr = "-1,";
            try {
                NewChannelBD newChannelBD = new NewChannelBD();
                List noreadUserChanList = newChannelBD.getNoReadUserChannels(domainId, userId, orgId, orgIdString);
                if(noreadUserChanList!=null && noreadUserChanList.size() > 0){
                    for(int xx=0; xx < noreadUserChanList.size(); xx++){
                        noReadStr += noreadUserChanList.get(xx).toString() + ",";
                    }
                }
                if(noReadStr.lastIndexOf(",") > -1){
                    noReadStr = noReadStr.substring(0, noReadStr.length() - 1);
                }
            } catch (Exception e) {
                logger.debug("========过滤掉没有查看权限的自定义信息频道异常:"+e.getMessage());
                e.printStackTrace();
            }
            //自定义信息频道id对应栏目表中channelType字段
            whereSQL += " and ch.channelType not in (" + noReadStr + ") ";
            Long endtime1 = System.currentTimeMillis();
            logger.debug("========time1:" + (endtime1 - starttime) + "ms");
        }
        
        whereSQL += " and ( ch.afficheChannelStatus is null or ch.afficheChannelStatus='0' ) ";
        //InformationBD informationBD = new InformationBD();
        ChannelBD channelBD = new ChannelBD();
        /*ManagerBD managerBD = new ManagerBD();
        ChannelBD channelBD = new ChannelBD();
        //可维护的所有栏目
        String canReadAllInfoChannel = informationBD.getAllInfoChannel(userId, orgId, channelType, userDefine);
        //栏目可新建人
        //20160706 -by jqq 经产品确认，信息查看与栏目可新建人无关
        //String scopeWhere1 = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, "bbb.channelIssuer","bbb.channelIssuerOrg", "bbb.channelIssuerGroup");
        //栏目可维护人
        String scopeWhere2 = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, "bbb.channelManager","bbb.channelManagerOrg", "bbb.channelManagerGroup");
        String whereSQL1 = informationBD.getUserViewInfo(userId,orgId);
        //信息可查看人
        String ttemp = " and (" + whereSQL1 + " aaa.informationReader like '%"+ userId +"%' " +
        "or aaa.informationReaderName is null or aaa.informationReaderName = '' ) " ;
        PortletBD portletBD = new PortletBD();
        String canViewChannel = portletBD.getInformationCategoryWhereSQL(userId, orgId, orgIdString, domainId);
        //栏目可查看人
        canViewChannel = canViewChannel.replaceAll("aaa.", "bbb.");
        //20160316 -by jqq 栏目下信息列表查询改造（查看的权限范围）
        whereSQL += " and (bbb.channelId in (" + canReadAllInfoChannel + ") or (" + scopeWhere2 + ") "
        + "or (" + canViewChannel + ttemp + ") or aaa.informationIssuerId=" + userId + ") ";*/
        
        /***********************************************************************************************/
        StringBuffer sqlBuf = new StringBuffer();
        StringBuffer frombuffer1 = new StringBuffer("");
        StringBuffer frombuffer2 = new StringBuffer("");
        StringBuffer frombuffer3 = new StringBuffer("");
        StringBuffer wherebuffer1 = new StringBuffer("");
        StringBuffer wherebuffer2 = new StringBuffer("");
        StringBuffer wherebuffer3 = new StringBuffer("");
        StringBuffer buffer = new StringBuffer();
        // 取当前用户的所有上级组织ID
        orgIdString = (buffer.append("$").append(orgIdString).append("$")).toString();
        String[] orgIdArray = orgIdString.split("\\$\\$");
        StringBuffer orgIdStringdgnx = new StringBuffer("0");
        // 判断用户是否在可查看组织范围内(单条信息)
        for (int i = 0; i < orgIdArray.length; i++) {
                if (!"".equals(orgIdArray[i])) {
                        orgIdStringdgnx.append(",").append(orgIdArray[i]);
                }
        }
        //String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();

           if(databaseType.indexOf("oracle")>=0){
               frombuffer1.append(" OA_INFORMATION inf,OA_INFORMATIONCHANNEL ch , (")
               .append(" select bbb.information_id as info_id, bbb.channel_id as ch_id ")
               .append(" from oa_information bbb ")
               .append(" where bbb.informationstatus=0")
               .append(" and (bbb.INFORMATIONVALIDTYPE = 0 OR (sysdate BETWEEN bbb.VALIDBEGINTIME AND bbb.VALIDENDTIME)) ")
               .append(" and bbb.ispublic=1")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_user_info where user_id='").append(userId).append("'")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_grp_info, org_user_group ")
               .append(" where group_id=grp_id and emp_id='").append(userId).append("'")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_org_info ")
               .append(" where org_id in (").append(orgIdStringdgnx).append(")")
               .append(") TT ");
               
               fromSQL = frombuffer1.toString();
               
               wherebuffer1.append(" and ch.channel_id=inf.channel_id ")
               .append(" and inf.information_id=TT.info_id ");
               
               whereSQL += wherebuffer1.toString();
           }
           if(databaseType.indexOf("mssqlserver")>=0){
               frombuffer2.append(" OA_INFORMATION inf,OA_INFORMATIONCHANNEL ch , (")
               .append(" select bbb.information_id as info_id, bbb.channel_id as ch_id ")
               .append(" from oa_information bbb ")
               .append(" where bbb.informationstatus=0")
               .append(" and (bbb.INFORMATIONVALIDTYPE = 0 OR (getdate() BETWEEN bbb.VALIDBEGINTIME AND bbb.VALIDENDTIME)) ")
               .append(" and bbb.ispublic=1")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_user_info where user_id='").append(userId).append("'")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_grp_info, org_user_group ")
               .append(" where group_id=grp_id and emp_id='").append(userId).append("'")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_org_info ")
               .append(" where org_id in (").append(orgIdStringdgnx).append(")")
               .append(") TT ");
               
               fromSQL = frombuffer2.toString();
               
               wherebuffer2.append(" and ch.channel_id=inf.channel_id ")
               .append(" and inf.information_id=TT.info_id ");
               
               whereSQL += wherebuffer2.toString();
           }
           if(databaseType.indexOf("mysql")>=0){
               frombuffer3.append(" OA_INFORMATION inf,OA_INFORMATIONCHANNEL ch , (")
               .append(" select bbb.information_id as info_id, bbb.channel_id as ch_id ")
               .append(" from oa_information bbb ")
               .append(" where bbb.informationstatus=0")
               .append(" and (bbb.INFORMATIONVALIDTYPE = 0 OR ( now() BETWEEN bbb.VALIDBEGINTIME AND bbb.VALIDENDTIME)) ")
               .append(" and bbb.ispublic=1")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_user_info where user_id='").append(userId).append("'")
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_grp_info, org_user_group ")
               .append(" where group_id=grp_id and emp_id='").append(userId)
               .append(" union select info_id as info_id,channel_id as ch_id ")
               .append(" from zl_org_info ")
               .append(" where org_id in (").append(orgIdStringdgnx).append(")")
               .append(") TT ");
               
               fromSQL = frombuffer3.toString();
               
               wherebuffer3.append(" and ch.channel_id=inf.channel_id ")
               .append(" and inf.information_id=TT.info_id ");
               
               whereSQL += wherebuffer3.toString();
           }
           /***********************************************************************************************/
        
        if (search != null && !search.equals("") && search.equals("1")) {
            // 如果用户搜索是选择了栏目则从该栏目选择否则从所有可以查看的栏目中选择
            if (!searchChannel.equals("0") && !searchChannel.equals("")
                    && !searchChannel.equals("null")) {
                InformationChannelPO po = channelBD.loadChannel(searchChannel);
                if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
                    whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
                }else{//不包含子栏目则只查该栏目中的信息
                    whereSQL = whereSQL + " and (ch.channel_Id = " + searchChannel + " or inf.otherChannel like '%," + searchChannel + ",%') ";
                }
            }
            // 搜索时指定日期
            if (searchDate != null && !searchDate.equals("")
                    && !searchDate.equals("null")) {
                if (databaseType.indexOf("mysql") >= 0) {
                    whereSQL = whereSQL
                            + " and inf.informationIssueTime between '"
                            + searchBeginDate + "' and '" + searchEndDate
                            + " 23:59:59' ";
                } else {
                    whereSQL = whereSQL
                            + " and inf.informationIssueTime between EZOFFICE.FN_STRTODATE('"
                            + searchBeginDate
                            + "','S') and EZOFFICE.FN_STRTODATE('"
                            + searchEndDate + " 23:59:59','') ";
                }
            }
            // 搜索指定发布人
            if (!searchIssuerName.equals("")) {
                whereSQL = whereSQL + " and inf.informationIssuer like '%"
                        + searchIssuerName + "%' ";
            }
            // 搜索关键字的类型及内容
            // String searchKeywordType ="";//
            // httpServletRequest.getParameter("keywordType");
            // String searchKeyword =
            // "";//httpServletRequest.getParameter("keyword");
            if (title != null && !title.equals("")) {
                // 标题
                whereSQL = whereSQL + " and inf.informationTitle like '%"
                        + title + "%' ";
            }
            if (key != null && !key.equals("")) {
                // 关键字
                whereSQL = whereSQL + " and inf.informationKey like '%" + key
                        + "%'";
            }
            if (subtitle != null && !subtitle.equals("")) {
                // 附标题
                whereSQL = whereSQL + " and inf.informationSubTitle like '%"
                        + subtitle + "%'";
            }
            if (append != null && !append.equals("")) {
                // 附件名
                whereSQL = whereSQL
                        + " and ( select count(*) from "
                        + " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc "
                        + " where ccc.accessoryName like '%"
                        + append
                        + "%' and ccc.information.informationId = inf.information_Id) > 0 ";
            }
        }
        Long endtime2 = System.currentTimeMillis();
        logger.debug("========time2:" + (endtime2 - starttime) + "ms");
        whereSQL = whereSQL
                + " order by inf.orderCode desc, case when inf.informationModifyTime is null then inf.informationIssueTime else inf.informationModifyTime end desc " ;
        logger.debug("========whereSQL:"+whereSQL);
        List resultlist = new ArrayList();
        resultlist = mobiledeallist(pager_offset, viewSQL, fromSQL, whereSQL, pageSize);
        Long endtime = System.currentTimeMillis();
        logger.debug("========end time:" + (endtime - starttime) + "ms");
        return resultlist;
    }
	
	private List list(String pager_offset, String viewSQL, String fromSQL,
			String whereSQL, String pagesize) {

		List resultList = new ArrayList();
		List relist = new ArrayList();

		int pageSize = Constants.MOBILE_DEFAULT_PAGE_SIZE;
		if(pagesize!=null && !"".equals(pagesize)){
			pageSize = Integer.valueOf(pagesize);
		}
		int offset = 0;
		if (pager_offset != null && !pager_offset.equals("")
				&& !pager_offset.equals("null")) {
			offset = Integer.parseInt(pager_offset);
		}
		int currentPage = (offset / pageSize) + 1;
		Page page = new Page(viewSQL, fromSQL, whereSQL);
		page.setPageSize(pageSize);
		page.setcurrentPage(currentPage);
		List list = page.getResultList();
		String recordCount = String.valueOf(page.getRecordCount());

		List rList0 = new ArrayList();
		List rList1 = new ArrayList();
		List rList10 = new ArrayList();

		if (list != null && list.size() > 0) {
			for (int jj = 0; jj < list.size(); jj++) {
				Object[] oobj = (Object[]) list.get(jj);
				List listjj = new ArrayList();
				listjj = java.util.Arrays.asList(oobj);
				rList0.add(listjj);
			}
		}

		rList10.add(pager_offset); // 1_0
		rList10.add(recordCount); // 1_1
		rList1.add(rList10);

		resultList.add(rList0); // 0
		resultList.add(rList1); // 1
		return resultList;

	}
	//20170314 -by jqq 信息列表接口jdpcpage查询方法
	private List mobiledeallist(String pager_offset, String viewSQL, String fromSQL,
            String whereSQL, String pagesize) {

        List resultList = new ArrayList();
        List relist = new ArrayList();

        int pageSize = Constants.MOBILE_DEFAULT_PAGE_SIZE;
        if(pagesize!=null && !"".equals(pagesize)){
            pageSize = Integer.valueOf(pagesize);
        }
        int offset = 0;
        if (pager_offset != null && !pager_offset.equals("")
                && !pager_offset.equals("null")) {
            offset = Integer.parseInt(pager_offset);
        }
        int currentPage = (offset / pageSize) + 1;
        /*Page page = new Page(viewSQL, fromSQL, whereSQL);
        page.setPageSize(pageSize);
        page.setcurrentPage(currentPage);
        List list = page.getResultList();*/
        com.whir.component.page.Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL, "");
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        page.setVarMap(new HashMap());
        List list = page.getResultList();
        String recordCount = String.valueOf(page.getRecordCount());

        List rList0 = new ArrayList();
        List rList1 = new ArrayList();
        List rList10 = new ArrayList();

        if (list != null && list.size() > 0) {
            for (int jj = 0; jj < list.size(); jj++) {
                Object[] oobj = (Object[]) list.get(jj);
                List listjj = new ArrayList();
                listjj = java.util.Arrays.asList(oobj);
                rList0.add(listjj);
            }
        }

        rList10.add(pager_offset); // 1_0
        rList10.add(recordCount); // 1_1
        rList1.add(rList10);

        resultList.add(rList0); // 0
        resultList.add(rList1); // 1
        return resultList;

    }

	/**
	 * 取所有信息栏目的一级栏目分页列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getChannelPageList(Object inobj[]){
		String domainId = inobj[0]!=null ? inobj[0].toString() :"";
		String userId = inobj[1]!=null ? inobj[1].toString() :"";
		String orgId = inobj[2]!=null ? inobj[2].toString() :"";
		String orgIdString = inobj[3]!=null ? inobj[3].toString() :"";
		String channelId = inobj[4]!=null ? inobj[4].toString() :"";
		String channelType = inobj[5]!=null ? inobj[5].toString() :"";
		String pager_offset = inobj[6]!=null ? inobj[6].toString() :"";
		String pageSize = inobj[7]!=null ? inobj[7].toString() :"";
		PortletBD portletBD = new PortletBD();
		String canViewChannel = portletBD.getInformationCategoryWhereSQL(userId, orgId, orgIdString, domainId);
		String viewHql = "aaa.channelId, aaa.channelName, aaa.channelParentId, aaa.channelIdString, aaa.channelNameString, " +
				"aaa.channelType, aaa.userDefine, aaa.channelNeedCheckup " ;
		String fromHql = "com.whir.ezoffice.information.channelmanager.po.InformationChannelPO aaa" ;
		String whereHql = " where (aaa.afficheChannelStatus is null or aaa.afficheChannelStatus='0') " +
        	" and aaa.domainId = " + domainId + " and " + canViewChannel + " and aaa.channelParentId = " + channelId ;
		if(channelType!=null && !"".equals(channelType)){
			whereHql += " and aaa.channelType = " + channelType;
		}
		whereHql += " order by aaa.channelType,aaa.channelIdString ";
		List resultlist = new ArrayList();
		resultlist = list(pager_offset, viewHql, fromHql, whereHql, pageSize);
		return resultlist;
	}
	
	/**
	 * 根据信息id取此条信息的内容
	 *
	 * @param setObj
	 *            String[]<br>
	 *            <br>
	 *            String informationId = setObj[0] 信息ID <br>
	 *            String userId = setObj[1] 用户的id <br>
	 *            String orgId = setObj[2] 组织的Id <br>
	 *            String orgId<br>
	 *            String = setObj[3] 组织orgIdString <br>
	 *            String domainId = setObj[4] 域id <br>
	 *            String userName = setObj[5] 用户名 <br>
	 *            String orgName = setObj[6] 组织名 <br>
	 *            String userChannelName = setObj[7] 频道名 <br>
	 *            return_informationType = setObj[8] 信息的类型 0 : 普通 1 : html 2 :
	 *            地址连接 3 : 文件连接 4 : word 5 : excel 6：ppt <br>
	 *            String channelType = setObj[9] 频道的类型
	 * @return List<br>
	 *         返回的List 第一层存8条List List0,List1,Lst2,List3,List4,List5,List6,List7<br>
	 *         List0 存1条List List00每一项依次存的是此条信息的详细信息<br>
	 *         List1 存n条List List10 至List1n 每条List1n 的第一项存的是图片附件名 第二项 存的是图片附件存储名<br>
	 *         List2 存n条List List20 至List2n 每条List2n 的第一项存的是附件名 第二项 存的是附件存储名<br>
	 *         List3 存1条List List30只有一项 存的是此信息的评论条数<br>
	 *         List4 存1条List List40只有一项 存的是此信息的类型<br>
	 *         List5 存1条List List50只有一项 存的是此信息所属频道名<br>
	 *         List6 存1条List List60只有一项 存的是此信息所属栏目名<br>
	 *         List7 存1条List List70只有一项 存的是地址<br>
	 */
	public List loadInformation_Mobile(String setObj[]) {

		List resultList = new ArrayList();
		List return_commentList = new ArrayList();
		List return_assoicateInfo = new ArrayList();
		List return_accList = new ArrayList();
		java.util.ArrayList picList = new java.util.ArrayList();
		java.util.ArrayList appList = new java.util.ArrayList();

		String return_informationType = "";
		String return_userChannelName = "";
		String return_channelName = "";
		String return_address = "";

		String r_informationTitle = "", r_informationSubTitle = "", r_informationContent = "", r_informationIssuer = "", r_informationIssueOrg = "", r_informationIssueTime = "", r_informationModifyTime = "", r_informationVersion = "", r_informationAuthor = "", r_informationSummary = "", r_informationKey = "", r_informationKits = "",

		r_documentNo = "", r_comeFrom = "", r_documentEditor = "", r_documentType = "", rCan_readers = "", r_forbidCopy = "", r_informationCanRemark="", r_informationDisplayTitle="", r_informationDisplayImage="";

		try {
			String informationId = setObj[0] == null ? "" : setObj[0]
					.toString();
			String userId = setObj[1] == null ? "" : setObj[1].toString();
			String orgId = setObj[2] == null ? "" : setObj[2].toString();
			String orgIdString = setObj[3] == null ? "" : setObj[3].toString();
			String domainId = setObj[4] == null ? "" : setObj[4].toString();
			String userName = setObj[5] == null ? "" : setObj[5].toString();
			String orgName = setObj[6] == null ? "" : setObj[6].toString();
			String userChannelName = setObj[7] == null ? "信息管理" : setObj[7]
					.toString(); // "信息管理";
			return_informationType = setObj[8] == null ? "" : setObj[8]
					.toString();
			String channelType = setObj[9] == null ? "-1" : setObj[9]
					.toString();

			String informationType = return_informationType;

			InformationBD informationBD = new InformationBD();
			ChannelBD channelBD = new ChannelBD();

			String userDefine1 = informationBD.getInfoUserdefine(informationId);

			String channelIds = "";
			String channelNames = "";
			List channelstringids = informationBD.getchannleinfo(informationId);
			if (channelstringids != null && channelstringids.size() > 0) {
				Object[] objid = null;
				objid = (Object[]) channelstringids.get(0);
				channelIds = objid[0].toString();
				channelNames = objid[1].toString();
			}

			/* START----------------NEW[何炜]20051102[NUM:5]------------------- */
			String returnValue = "";
			try {
				returnValue = new com.whir.ezoffice.archives.bd.ArchivesBD()
						.archivesPigeonholeSet("ZSGL", domainId);
			} catch (Exception e) {
			}

			if (returnValue.equals("")) {
				returnValue = "1,1,0";
			}
			Boolean dossier = Boolean.FALSE;
			String isSendMessage = "0";
			if (!returnValue.equals("-1")) {
				String[] tmp = returnValue.split(",");
				if (tmp[0].equals("1") || tmp[0].equals("01")) {
					dossier = Boolean.TRUE;
				}
				isSendMessage = tmp[2];
			}

			String channelId = channelIds; // --------------------------------------------------------
			String checkdepart = "";
			String channelName = channelNames;
			String OrgName = "";

			return_channelName = channelName;

			String channelidstring = "";
			List channelstringid = channelBD.getSingleChannel(channelId);
			if (channelstringid != null && channelstringid.size() > 0) {
				Object[] objid = null;
				objid = (Object[]) channelstringid.get(0);
				channelidstring = objid[13].toString();
			}
			if (channelidstring != "") {
				String address = "";
				String[] temp_channelidstring = channelidstring.split("_");
				for (int i = 0; i < temp_channelidstring.length; i++) {

					String channelidstring_new = "";
					channelidstring_new = temp_channelidstring[i];
					String[] temp_channelidstring_new = channelidstring_new
							.split("\\$");
					String channelidstring_new_now = "";
					channelidstring_new_now = temp_channelidstring_new[1];
					channelId = channelidstring_new_now;
					List channelname = channelBD.getSingleChannel(channelId);
					if (channelname != null && channelname.size() > 0) {
						Object[] objid = null;
						objid = (Object[]) channelname.get(0);
						channelidstring = objid[0].toString();
					}
					address = address + "." + channelidstring;
				}

				if (checkdepart.equals("1")) {
					List getOrgName = informationBD.getOrgName(channelId);
					if (getOrgName != null && getOrgName.size() > 0) {
						Object[] objorgname = null;
						objorgname = (Object[]) getOrgName.get(0);
						OrgName = objorgname[0].toString();
						channelName = OrgName;
					}
				}

				if (checkdepart.equals("1")) {
					return_userChannelName = channelName;
				} else {
					return_userChannelName = userChannelName;
				}

				return_channelName = channelidstring;
				return_address = address;

			}

			return_commentList = informationBD.getComment(informationId);

			if ("-1".equals(channelType) && "0".equals(userDefine1)) {
				channelType = "2";
			}

			/*
			 * return_assoicateInfo = new InformationBD().
			 * getAssociateInfo(orgId, informationId, userId, orgIdString,
			 * channelType, userDefine1, "0");
			 */

			if (informationType.equals("2")) {
				String content = informationBD.getContent(informationId);

				// 地址链接
				if (!content.startsWith("http://")) {
					content = "http://" + content;
				}
				// httpServletRequest.setAttribute("midType",
				// "url");---------------------------------------
				// // httpServletRequest.setAttribute("content",
				// content);---------------------------------------

				r_informationContent = content;

			} else {

				List list = informationBD.getSingleInfo(informationId,
						channelId);

				String content = "";
				if (list != null && list.size() > 0) {
					Object[] obj = (Object[]) list.get(0);
					content = obj[2] == null ? "" : obj[2].toString();
					r_informationContent = obj[2] == null ? "" : obj[2]
							.toString();

					/*
					if (informationType.equals("3")) {
						// 文件连接
						if (content != null && content.toString().length() > 1) {
							String[] tmp2 = content.toString().split(":");
							if (tmp2.length == 2) {
								// infolist = new Object[] {tmp2[0], tmp2[1]};
							}
						}

					} else {
					*/
						r_informationTitle = "" + obj[0];
						r_informationSubTitle = "" + obj[1];
						r_informationContent = "" + obj[2];
						r_informationIssuer = "" + obj[3];
						r_informationIssueOrg = "" + obj[4];
						r_informationIssueTime = "" + obj[5];
						r_informationModifyTime = "" + obj[6];
						r_informationVersion = "" + obj[7];
						r_informationAuthor = "" + obj[8];
						r_informationSummary = "" + obj[14];
						r_informationKey = "" + obj[15];
						r_informationKits = "" + obj[64];
						r_informationCanRemark = "" + obj[67];
						//20160302 -by jqq evo改造接口返回增加字段displaytitle
						r_informationDisplayTitle = "" + obj[31];
						//20160310 -by jqq evo改造接口返回增加字段displayimage
                        r_informationDisplayImage = "" + obj[44];

						if (obj[41] != null) {
							r_documentNo = obj[41].toString();
						}
						// 作者单位：
						if (obj[42] != null) {
							r_documentEditor = obj[42].toString();
						}

						r_documentType = "撰写"; // 文章类型： 撰写
						if (obj[43] != null) {
							if (obj[43].toString().equals("0")) {
								r_documentType = "撰写";
							} else if (obj[43].toString().equals("1")) {
								r_documentType = "编辑";
							} else if (obj[43].toString().equals("2")) {
								r_documentType = "摘录";
							}
						}

						// 来源：
						if (obj[39] != null) {
							r_comeFrom = obj[39].toString();
						}

						// 可查看人：质量部,项目部,研发部,
						if (obj[16] != null) {
							rCan_readers = obj[16].toString();
						}

						r_forbidCopy = "否"; // 禁止打印拷贝： 否
						if (obj[26] != null && obj[26].toString().equals("1")) {
							r_forbidCopy = "是";
						}

						// 取附件和图片
						InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();

						List accclist = informationAccessoryBD
								.getAccessory(informationId);

						Object[] tmp = null;
						if (accclist != null) {
							for (int i = 0; i < accclist.size(); i++) {
								tmp = (Object[]) accclist.get(i);
								if (tmp[4].toString().equals("1")) {
									picList.add(accclist.get(i));
								} else {
									appList.add(accclist.get(i));
								}
							}
						}

				//	}

				} else {

					return_informationType = "00";
				}
			}

			informationBD.setBrowser(userId, userName, orgId, orgName,
					informationId, orgIdString);
			informationBD.setKits(informationId);

			List rList0 = new ArrayList();
			List rList1 = new ArrayList();
			List rList2 = new ArrayList();
			List rList3 = new ArrayList();
			List rList4 = new ArrayList();
			List rList5 = new ArrayList();
			List rList6 = new ArrayList();
			List rList7 = new ArrayList();

			// resultList.add(infolist);//0

			List rList00 = new ArrayList();
			rList00.add(r_informationTitle);
			rList00.add(r_informationSubTitle);
			rList00.add(r_informationContent);
			rList00.add(r_informationIssuer);
			rList00.add(r_informationIssueOrg);
			rList00.add(r_informationIssueTime);
			rList00.add(r_informationModifyTime);
			rList00.add(r_informationVersion);
			rList00.add(r_informationAuthor);
			rList00.add(r_informationSummary);
			rList00.add(r_informationKey);
			rList00.add(r_informationKits);

			rList00.add(r_documentNo);
			rList00.add(r_comeFrom);
			rList00.add(r_documentEditor);
			rList00.add(r_documentType);
			rList00.add(rCan_readers);
			rList00.add(r_forbidCopy);
			rList00.add(r_informationCanRemark);
			//20160302 -by jqq evo改造接口返回增加字段displaytitle
			rList00.add(r_informationDisplayTitle);
			//20160310 -by jqq evo改造接口返回增加字段displayimage
            rList00.add(r_informationDisplayImage);
			rList0.add(rList00);
			resultList.add(rList0);

			// resultList.add(picList);//1
			if (picList != null && picList.size() > 0) {
				for (int jj = 0; jj < picList.size(); jj++) {
					Object[] objjj = (Object[]) picList.get(jj);
					List listjj = java.util.Arrays.asList(objjj);
					rList1.add(listjj);
				}
			}
			resultList.add(rList1);

			// resultList.add(appList);//2
			if (appList != null && appList.size() > 0) {
				for (int jj = 0; jj < appList.size(); jj++) {
					Object[] objjj = (Object[]) appList.get(jj);
					List listjj = java.util.Arrays.asList(objjj);
					rList2.add(listjj);
				}
			}
			resultList.add(rList2);

			// resultList.add(return_commentList == null ? "0" :
			// "" + return_commentList.size());//3
			List rList30 = new ArrayList();
			rList30.add(return_commentList == null ? "0" : ""
					+ return_commentList.size());
			rList3.add(rList30);
			resultList.add(rList3); // 3

			// resultList.add(return_informationType);//4
			List rList40 = new ArrayList();
			rList40.add(return_informationType);
			rList4.add(rList40); // 4
			resultList.add(rList4);

			// resultList.add(return_userChannelName);//5
			List rList50 = new ArrayList();
			rList50.add(return_userChannelName);
			rList5.add(rList50); // 5
			resultList.add(rList5);

			// resultList.add(return_channelName);//6
			List rList60 = new ArrayList();
			rList60.add(return_channelName);
			rList6.add(rList60); // 5
			resultList.add(rList6);

			// resultList.add(return_address);//7
			List rList70 = new ArrayList();
			rList70.add(return_address);
			rList7.add(rList70); // 5
			resultList.add(rList7);
			
			//20160504 -by jqq 增加手机浏览信息的记录
			try {
			    informationBD.saveViewRecord(userId, userName, orgId, orgName, informationId);
            } catch (Exception record_e) {
                record_e.printStackTrace();
            }
		} catch (Exception e) {
			e.printStackTrace();

		}

		return resultList;

	}

	/**
	 *
	 * @param arg
	 *            String[]
	 * @return List
	 */
	public List loadInformation_MobileByWorkId(String argStr[]) {
		/*
		 * 标题 所属栏目： 信息管理.审(权限) 副标题： 文档编号：0 同时发布到： 关键字： 作者： 作者单位： 文章类型： 撰写 来源：
		 * 可查看人：质量部,项目部,研发部, 摘要： 禁止打印拷贝： 否 内容：
		 *
		 * new String []{"标 题","所属栏目"," 副标题","文档编号","同时发布"," 关键字","作
		 * 者","作者单位","文章类型","来 源","可查看人","摘 要","禁止拷贝"}
		 */

		String workId = argStr[0].toString();
		String channelId = "";
		String informationId = "";
		String processId = "";
		String tableId = "";

		// 去取流程信息
		Map _workMap = new HashMap(0);
		com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
		String sql = "select ";
		sql += " WORKFILETYPE, "; // 0
		sql += " WORKCURSTEP, "; // 1
		sql += " WORKTITLE, "; // 2
		sql += " WORKDEADLINE, "; // 3
		sql += " WORKSUBMITPERSON, "; // 4
		sql += " WORKSUBMITTIME, "; // 5
		sql += " WORKTYPE, "; // 6
		sql += " WORKACTIVITY, "; // 7
		sql += " WORKTABLE_ID, "; // 8
		sql += " WORKRECORD_ID, "; // 9
		sql += " WF_WORK_ID, "; // 10
		sql += " WORKSUBMITPERSON, "; // 11
		sql += " WF_SUBMITEMPLOYEE_ID, "; // 12
		sql += " WORKALLOWCANCEL, "; // 13
		sql += " WORKPROCESS_ID, "; // 14
		sql += " WORKSTEPCOUNT, "; // 15
		sql += " WORKMAINLINKFILE, "; // 16
		sql += " WORKSUBMITTIME, "; // 17
		sql += " WORKCURSTEP, "; // 18
		sql += " CREATORCANCELLINK, "; // 19
		sql += " ISSTANDFORWORK, "; // 20
		sql += " STANDFORUSERID, "; // 21
		sql += " STANDFORUSERNAME, "; // 22
		sql += " WORKCREATEDATE, "; // 23
		sql += " SUBMITORG, "; // 24
		sql += " WORKDONEWITHDATE, "; // 25
		sql += " EMERGENCE, "; // 26
		sql += " INITACTIVITY, "; // 27
		sql += " INITACTIVITYNAME, "; // 28
		sql += " TRANTYPE, "; // 29
		sql += " TRANFROMPERSONID, "; // 30
		sql += " PROCESSDEADLINEDATE, "; // 31
		sql += " WF_CUREMPLOYEE_ID "; // 32
		sql += "  from WF_WORK ";
		sql += " where WF_WORK_ID = " + workId;
		try {
			dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
			_workMap = dbopt.executeQueryToMap(sql);
			dbopt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				dbopt.close();
			} catch (Exception ex1) {
			}
		}

		String creatorcancellink = (String) _workMap.get("creatorcancellink");
		String workstepcount = (String) _workMap.get("workstepcount");
		String processdeadlinedate = (String) _workMap
				.get("processdeadlinedate");
		String workallowcancel = (String) _workMap.get("workallowcancel");
		String workdonewithdate = (String) _workMap.get("workdonewithdate");
		String workcreatedate = (String) _workMap.get("workcreatedate");
		String wf_curemployee_id = (String) _workMap.get("wf_curemployee_id");
		String initactivity = (String) _workMap.get("initactivity");
		String tranfrompersonid = (String) _workMap.get("tranfrompersonid");
		String workcurstep = (String) _workMap.get("workcurstep");
		String workmainlinkfile = (String) _workMap.get("workmainlinkfile");
		String worksubmitperson = (String) _workMap.get("worksubmitperson");
		String initactivityname = (String) _workMap.get("initactivityname");
		String worktable_id = (String) _workMap.get("worktable_id");
		String standforuserid = (String) _workMap.get("standforuserid");
		String wf_work_id = (String) _workMap.get("wf_work_id");
		String worksubmittime = (String) _workMap.get("worksubmittime");
		String wf_submitemployee_id = (String) _workMap
				.get("wf_submitemployee_id");
		String worktype = (String) _workMap.get("worktype");
		String workrecord_id = (String) _workMap.get("workrecord_id");
		String workprocess_id = (String) _workMap.get("workprocess_id");
		String workactivity = (String) _workMap.get("workactivity");
		String submitorg = (String) _workMap.get("submitorg");
		String isstandforwork = (String) _workMap.get("isstandforwork");
		String standforusername = (String) _workMap.get("standforusername");
		String workdeadline = (String) _workMap.get("workdeadline");
		String trantype = (String) _workMap.get("trantype");
		String emergence = (String) _workMap.get("emergence");
		String workfiletype = (String) _workMap.get("workfiletype");
		String worktitle = (String) _workMap.get("worktitle");
//		int cK = 0;
//		int tK = 0;
//		if (workmainlinkfile != null) {
//			tK = workmainlinkfile.indexOf("&channelType=");
//			cK = workmainlinkfile.indexOf("&channelId=");
//			// channelId在最后
//			if (tK < cK) {
//				channelId = workmainlinkfile.substring(cK + 11);
//			} else { // channelType在最后
//				channelId = workmainlinkfile.substring(cK + 11, tK);
//			}
//		}
		InformationPO po = new InformationBD().load(workrecord_id);
        channelId = po.getInformationChannel().getChannelId()+"";
		informationId = workrecord_id;
		processId = workprocess_id;
		tableId = worktable_id;

		List paramList = new ArrayList();
		paramList.add(creatorcancellink != null ? creatorcancellink : "");
		paramList.add(workstepcount != null ? workstepcount : "");
		paramList.add(processdeadlinedate != null ? processdeadlinedate : "");
		paramList.add(workallowcancel != null ? workallowcancel : "");
		paramList.add(workdonewithdate != null ? workdonewithdate : "");
		paramList.add(workcreatedate != null ? workcreatedate : "");
		paramList.add(wf_curemployee_id != null ? wf_curemployee_id : "");
		paramList.add(initactivity != null ? initactivity : "");
		paramList.add(tranfrompersonid != null ? tranfrompersonid : "");
		paramList.add(workcurstep != null ? workcurstep : "");
		paramList.add(workmainlinkfile != null ? workmainlinkfile : "");
		paramList.add(worksubmitperson != null ? worksubmitperson : "");
		paramList.add(initactivityname != null ? initactivityname : "");
		paramList.add(worktable_id != null ? worktable_id : ""); // 13
		paramList.add(standforuserid != null ? standforuserid : "");
		paramList.add(wf_work_id != null ? wf_work_id : "");
		paramList.add(worksubmittime != null ? worksubmittime : "");
		paramList.add(wf_submitemployee_id != null ? wf_submitemployee_id : "");
		paramList.add(worktype != null ? worktype : "");
		paramList.add(workrecord_id != null ? workrecord_id : ""); // 19
		paramList.add(workprocess_id != null ? workprocess_id : ""); // 20
		paramList.add(workactivity != null ? workactivity : ""); // 21
		paramList.add(submitorg != null ? submitorg : ""); // 22
		paramList.add(isstandforwork != null ? isstandforwork : "");
		paramList.add(standforusername != null ? standforusername : "");
		paramList.add(workdeadline != null ? workdeadline : "");
		paramList.add(trantype != null ? trantype : "");
		paramList.add(emergence != null ? emergence : "");
		paramList.add(workfiletype != null ? workfiletype : "");
		paramList.add(worktitle != null ? worktitle : "");

		// activityClass 当是3时 是自动返回活动
		// String[] activityInfo =new
		// com.whir.ezoffice.workflow.newBD.WorkFlowBD().getActivityClass(worktable_id,workrecord_id,
		// workactivity);
		if(!workactivity.toLowerCase().equals("null") && !workactivity.equals("")){
			List activityInfoList = new com.whir.ezoffice.workflow.newBD.WorkFlowBD().getActivityClass_multi(worktable_id, workrecord_id, workactivity);
			String[] activityInfoListObj = (String[]) activityInfoList.get(0);
			String activityClassclass = activityInfoListObj[0];
			paramList.add(activityInfoListObj[0] != null ? activityInfoListObj[0]: "");
		}
		String informationType = "0";
		InformationBD informationBD = new InformationBD();
		List infortype = new ArrayList();
		infortype = informationBD.getinformation(informationId);
		if (infortype != null && infortype.size() > 0) {
			Object[] objlistype = null;
			objlistype = (Object[]) infortype.get(0);
			informationType = objlistype[0].toString();
		}

		String str14 = informationType;
		// httpServletRequest.setAttribute("informationType", informationType);

		ChannelBD channelBD = new ChannelBD();

		String str1 = ""; // 栏目

		if (channelId != null) {
			List channelList = channelBD.getSingleChannel(channelId);
			if (channelList != null && channelList.size() > 0) {
				Object[] obj = null;
				obj = (Object[]) channelList.get(0);
				str1 = obj[0] + "";
			}
		}

		List list = informationBD.getSingleInfo(informationId, channelId);
		if (list.size() > 1) {
			str1 = list.get(1).toString();
		}

		String r_informationTitle = "", r_informationSubTitle = "", r_informationContent = "", r_informationIssuer = "", r_informationIssueOrg = "", 
			r_informationIssueTime = "", r_informationModifyTime = "", r_informationVersion = "", r_informationAuthor = "", r_informationSummary = "",
			r_informationKey = "", r_informationKits = "",r_documentNo = "", r_comeFrom = "", r_documentEditor = "", r_documentType = "", 
			rCan_readers = "", r_forbidCopy = "", r_otherChannel = "";

		Object[] obj = (Object[]) list.get(0);

		r_informationTitle = "" + obj[0];
		r_informationSubTitle = obj[1]!=null?obj[1].toString():"";
		r_informationContent = obj[2]!=null?obj[2].toString():"";
		r_informationIssuer = obj[3]!=null?obj[3].toString():"";
		r_informationIssueOrg = obj[4]!=null?obj[4].toString():"";
		r_informationIssueTime = obj[5]!=null?obj[5].toString():"";
		r_informationModifyTime = obj[6]!=null?obj[6].toString():"";
		r_informationVersion = obj[7]!=null?obj[7].toString():"";
		r_informationAuthor = obj[8]!=null?obj[8].toString():"";
		r_informationSummary = obj[14]!=null?obj[14].toString():"";
		r_informationKey = obj[15]!=null?obj[15].toString():"";
		r_informationKits = obj[64]!=null?obj[64].toString():"";
		
		if (obj[32] != null) {
	      String otherChannel = obj[32].toString().substring(1, obj[32].toString().length() - 1);
	      if (!otherChannel.equals("0")) {
	        String[] channel = otherChannel.split(",");
	        for (int i = 0; i < channel.length; i++) {
	          List l = informationBD.getSingleInfo(informationId, channel[i]);
	          String channelName = "";
	          if (l.size() > 1) {
	            channelName = l.get(1).toString();
	          }
	          r_otherChannel = r_otherChannel + channelName + ",";
	        }
	        r_otherChannel = r_otherChannel.substring(0, r_otherChannel.length() - 1);
	      }
	    }
		
		if (obj[41] != null) {
			r_documentNo = obj[41].toString();
		}
		// 作者单位：
		if (obj[42] != null) {
			r_documentEditor = obj[42].toString();
		}

		r_documentType = "撰写"; // 文章类型： 撰写
		if (obj[43] != null) {
			if (obj[43].toString().equals("0")) {
				r_documentType = "撰写";
			} else if (obj[43].toString().equals("1")) {
				r_documentType = "编辑";
			} else if (obj[43].toString().equals("2")) {
				r_documentType = "摘录";
			}
		}

		// 来源：
		if (obj[39] != null) {
			r_comeFrom = obj[39].toString();
		}

		// 可查看人：质量部,项目部,研发部,
		if (obj[16] != null) {
			rCan_readers = obj[16].toString();
		}

		String str11 = ""; // 摘要：
		if (obj[14] != null) {
			str11 = obj[14].toString();
		}

		r_forbidCopy = "否"; // 禁止打印拷贝： 否
		if (obj[26] != null && obj[26].toString().equals("1")) {
			r_forbidCopy = "是";
		}

		// 取附件和图片
		InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
		list = informationAccessoryBD.getAccessory(informationId);
		java.util.ArrayList picList = new java.util.ArrayList();
		java.util.ArrayList appList = new java.util.ArrayList();
		Object[] tmp = null;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				tmp = (Object[]) list.get(i);
				if (tmp[4].toString().equals("1")) {
					picList.add(list.get(i));
				} else {
					appList.add(list.get(i));
				}
			}
		}

		List rList0 = new ArrayList();
		List rList1 = new ArrayList();
		List rList2 = new ArrayList();
		List rList3 = new ArrayList();
		List rList4 = new ArrayList();
		List rList5 = new ArrayList();
		List rList6 = new ArrayList();

		List rList00 = new ArrayList();
		rList00.add(r_informationTitle);//0
		rList00.add(str1);//1
		rList00.add(r_informationSubTitle);//2
		rList00.add(r_informationContent);//3
		rList00.add(r_informationIssuer);//4
		rList00.add(r_informationIssueOrg);//5
		rList00.add(r_informationIssueTime);//6
		rList00.add(r_informationModifyTime);//7
		rList00.add(r_informationVersion);//8
		rList00.add(r_informationAuthor);//9
		rList00.add(r_informationSummary);//10
		rList00.add(r_informationKey);//11
		rList00.add(r_informationKits);//12

		rList00.add(r_documentNo);//13
		rList00.add(r_comeFrom);//14
		rList00.add(r_documentEditor);//15
		rList00.add(r_documentType);//16
		rList00.add(rCan_readers);//17
		rList00.add(r_forbidCopy);//18
		
        //信息类型   信息的类型 0 :  普通  1 :  html  2 :  地址连接  3 :  文件连接  4 :   word  5 :   excel  6：ppt
        rList00.add(informationType);//19
        rList00.add(r_otherChannel);//20
		rList0.add(rList00);

		List resultList = new ArrayList();

		// resultList.add(infoArr);//0
		resultList.add(rList0);

		// resultList.add(picList);//1
		if (picList != null && picList.size() > 0) {
			for (int jj = 0; jj < picList.size(); jj++) {
				Object[] objjj = (Object[]) picList.get(jj);
				List listjj = java.util.Arrays.asList(objjj);
				rList1.add(listjj);
			}
		}
		resultList.add(rList1);

		// resultList.add(appList);//2
		if (appList != null && appList.size() > 0) {
			for (int jj = 0; jj < appList.size(); jj++) {
				Object[] objjj = (Object[]) appList.get(jj);
				List listjj = java.util.Arrays.asList(objjj);
				rList2.add(listjj);
			}
		}
		resultList.add(rList2);

		// 取得审批意见
		List commentList = new com.whir.ezoffice.workflow.newBD.WorkFlowBD()
				.getDealWithCommentNotBack(tableId, informationId, "1");
		if (commentList != null && commentList.size() > 0) {
			for (int jj = 0; jj < commentList.size(); jj++) {
				Object[] objjj = (Object[]) commentList.get(jj);
				List listjj = java.util.Arrays.asList(objjj);
				rList3.add(listjj);
			}
		}
		resultList.add(rList3); // 3

		// 取得退回意见
		String backCommentStr = new com.whir.ezoffice.workflow.newBD.WorkFlowButtonBD()
				.getBackComment(processId, tableId, informationId);
		List rList40 = new ArrayList();
		rList40.add(backCommentStr);
		rList4.add(rList40);
		resultList.add(rList4); // 4

		rList5.add(paramList);
		resultList.add(rList5); // 5

		return resultList;
	}

	/**
	 * 根据id取单个栏目
	 *
	 * @param channelId
	 *            String
	 * @return List
	 */
	public InformationChannelPO getSingleChannel(String channelId) {
		InformationChannelPO informationChannelPO = null;
		try {
			ParameterGenerator pg = new ParameterGenerator(1);
			pg.put(channelId, java.lang.String.class);
			EJBProxy ejbProxy = new InformationEJBProxy("ChannelEJB",
					"ChannelEJBLocal", ChannelEJBHome.class);
			informationChannelPO = (InformationChannelPO) ejbProxy.invoke(
					"loadChannel", pg.getParameters());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return informationChannelPO;
		}
	}

	/**
	 *
	 * @param userId
	 *            String
	 * @param orgId
	 *            String
	 * @param orgIdString
	 *            String
	 * @param domainId
	 *            String
	 * @param containFlow
	 *            String 是否包含流程栏目 0： 不包含 1： 包含
	 * @return List
	 */
	public List getCanIssueChannel(String userId, String orgId,
			String orgIdString, String domainId, String containFlow,
			String searchChannelName) {
		ChannelBD channelBD = new ChannelBD();
		ManagerBD managerBD = new ManagerBD();
		String where = managerBD.getScopeFinalWhere(userId, orgId, orgIdString,
				"aaa.channelIssuer", "aaa.channelIssuerOrg",
				"aaa.channelIssuerGroup");
		// 兼职组织
		NewInformationBD newInformationBD = new NewInformationBD();
		String sideLineOrg = newInformationBD.getSidelineOrgId(userId);
		if (sideLineOrg != null && !sideLineOrg.equals("")
				&& sideLineOrg.length() > 2) {
			sideLineOrg = sideLineOrg.substring(1, sideLineOrg.length() - 1);
			sideLineOrg.replaceAll("\\*", ",");
			sideLineOrg.replaceAll(",,", ",");
			String[] sarr = sideLineOrg.split(",");
			if (sarr != null && sarr.length > 0) {
				for (int i = 0; i < sarr.length; i++) {
					where += " or aaa.channelIssuerOrg like '%*" + sarr[i]
							+ "*%' ";
				}
			}
		}
		/**
		 * 不包含流程栏目
		 */
		if (containFlow.equals("0")) {
			where = "(" + where + ") and  aaa.channelNeedCheckup=0";
		}
		if (searchChannelName != null && !searchChannelName.equals("")
				&& !searchChannelName.equals("null")) {
			where += "  and  aaa.channelName like '%" + searchChannelName
					+ "%'";
		}
		return channelBD.getCanIssue(where, domainId);
	}

	/**
	 * 取全文检索标签 以“|”分割
	 *
	 * @date 2012-2-11 ipad版
	 * @return
	 */
	public String getInformationTag() {
		String result = "";
		String sql = "select tagContent from  OA_informationTag ";
		CommentBD bd = new CommentBD();
		List list = bd.getDataBySQL(sql);
		if (list != null && list.size() > 0) {
			Object obj[] = (Object[]) list.get(0);
			result = "" + obj[0];
		}
		return result;
	}

	/**
	 * 取最近更新信息
	 *
	 * @date 2012-2-11 ipad版
	 * @return
	 */
	public List getLastUpdateInfo1(Object inobj[]) {
		/** @todo: complete the business logic here, this is just a skeleton. */
		String domainId = inobj[0] == null ? "" : inobj[0].toString(); // 域标识
		String userId = inobj[1] == null ? "488" : inobj[1].toString(); // 域标识
		String orgId = inobj[2] == null ? "123" : inobj[2].toString(); // 域标识
		String orgIdString = inobj[3] == null ? "$120$$123$" : inobj[3]
				.toString();
		String type = inobj[4] == null ? "all" : inobj[4].toString();// all
		String userDefine = inobj[5] == null ? "0" : inobj[5].toString();// 0
		String informationId = inobj[6] == null ? "" : inobj[6].toString();
		String search = inobj[7] == null ? "" : inobj[7].toString();
		String userChannelName = inobj[8] == null ? "" : inobj[8].toString();
		String channelType = inobj[9] == null ? "" : inobj[9].toString();
		String depart = inobj[10] == null ? "" : inobj[10].toString();
		String searchDate = inobj[11] == null ? "" : inobj[11].toString();
		String searchBeginDate = inobj[12] == null ? "" : inobj[12].toString();
		String searchEndDate = inobj[13] == null ? "" : inobj[13].toString();
		String searchIssuerName = inobj[14] == null ? "" : inobj[14].toString();
		String searchKeywordType = inobj[15] == null ? "" : inobj[15]
				.toString();
		String searchKeyword = inobj[16] == null ? "" : inobj[16].toString();
		String title = inobj[17] == null ? "" : inobj[17].toString();
		String key = inobj[18] == null ? "" : inobj[18].toString();
		String subtitle = inobj[19] == null ? "" : inobj[19].toString();
		String append = inobj[20] == null ? "" : inobj[20].toString();
		String searchChannel = inobj[21] == null ? "0" : inobj[21].toString();
		String pager_offset = inobj[22] == null ? "" : inobj[22].toString();

		InformationBD informationBD = new InformationBD();
//		ChannelBD channelBD = new ChannelBD();
		// 取用户可以查看的所有栏目,并返回到页面供搜索取栏目ID用
//		List list = channelBD.getUserViewCh(userId, orgId, channelType,
//				userDefine, domainId);

		java.util.Date now = new java.util.Date();
		String nowString = now.toLocaleString();
		nowString = nowString.substring(0, nowString.indexOf(" "));

		/**
		 * 取所有信息的条件为 1.信息的发布人 2.当前用户在信息的可维护人中 当前用户为栏目的可维护人 具有信息维护权限
		 * 信息的创建人或创建组织在维护范围内 3.当前用户在栏目的可查看范围内并且在信息的可查看范围内 4.栏目或信息的可查看人范围都没有选择
		 * 4.channelType=0
		 *
		 * where ((1) or (2) or (3)) and (4) (1): aaa.informationIssuerId=userId
		 * (2):
		 *
		 */

		String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, "
				+ " aaa.informationIssuer, aaa.informationVersion, aaa.informationContent,"
				+ " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead,"
				+ " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId,"
				+ " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite,"
				+ " aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg,aaa.informationIsCommend,bbb.channelType";
		String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa "
				+ " join aaa.informationChannel bbb ";
		String whereSQL = "";
		String databaseType = com.whir.common.config.SystemCommon
				.getDatabaseType();
		if (databaseType.indexOf("mysql") >= 0) {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or '"
					+ nowString
					+ "' between aaa.validBeginTime and aaa.validEndTime ) ";
		} else {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or EZOFFICE.FN_STRTODATE('"
					+ nowString
					+ "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
		}

		whereSQL += " and (  bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0'  ) ";

		// 用户可是栏目的创建人或者有栏目的维护权限则可以查看栏目的全部信息
		// 用户在栏目的维护人里则可以查看信息维护权限范围内的信息
			String rightWhere = new ManagerBD().getRightFinalWhere(userId, orgId,
					"01*03*03", "aaa.informationIssueOrgId",
					"aaa.informationIssuerId");
			String canReadAllInfoChannel = informationBD.getAllInfoChannel(userId,
					orgId, channelType, userDefine);
//			String managedChannel = informationBD.getManagedChannel(userId, orgId,
//					channelType, userDefine);
			String scopeWhere = new ManagerBD().getScopeFinalWhere(userId, orgId,
					orgIdString, "bbb.channelManager", "bbb.channelManagerOrg",
					"bbb.channelManagerGroup");

			whereSQL += " and ( ( (bbb.channelId in ("
					+ canReadAllInfoChannel
					+ ") or(("
					+ scopeWhere
					+ ") and ("
					+ rightWhere
					+ ")) or aaa.informationIssuerId="
					+ userId
					+ ") and (bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0') ) or 1=1 ";

		String twoCql[] = informationBD.getUserViewChAll(userId, orgId);
//		List allchlist = informationBD.getUserViewChAll2(userId, orgId);

		boolean hasRight_info = false;
		boolean hasRight_depart = false;
		String rightCode_info = "01*03*03";
		String rightCode_depart = "01*01*02";
		List rightScopeList_depart = new com.whir.org.manager.bd.ManagerBD()
				.getRightScope(userId, rightCode_depart);
		if (rightScopeList_depart != null && rightScopeList_depart.size() > 0
				&& rightScopeList_depart.get(0) != null) {
			Object[] obj = (Object[]) rightScopeList_depart.get(0);
			if ("0".equals(obj[0].toString())) {
				hasRight_depart = true;
			}
		}

		List rightScopeList_info = new com.whir.org.manager.bd.ManagerBD()
				.getRightScope(userId, rightCode_info);
		if (rightScopeList_info != null && rightScopeList_info.size() > 0
				&& rightScopeList_info.get(0) != null) {
			Object[] obj = (Object[]) rightScopeList_info.get(0);
			if ("0".equals(obj[0].toString())) {
				hasRight_info = true;
			}
		}

		List userMenuChannelList = new ChannelBD().getAllUserMenuChannel(
				domainId, "");
		String ct = " ( 0 ";
		if (userMenuChannelList != null && userMenuChannelList.size() > 0) {
			for (int i = 0; i < userMenuChannelList.size(); i++) {
				ct += "," + userMenuChannelList.get(i);
			}
		}
		ct += " )";
		String whereTmp1 = "";
//		String whereTmp2 = "";

		whereTmp1 += " and  (  bbb.channelType in " + ct + "  ";
		if (hasRight_info) {

		} else {
			whereTmp1 = " and  ((  bbb.channelType in " + ct + "  "
					+ " and bbb.channelId in (" + twoCql[0] + ")";
			whereTmp1 += " and bbb.channelType = " + channelType;
			InformationBD infoBD = new InformationBD();
			String readerWhere = infoBD.getInfoReader(userId, orgId,
					orgIdString, "aaa");

			if (readerWhere == null || readerWhere.equals("")) {

			} else {
				whereTmp1 = whereTmp1 + " and (" + readerWhere + ") )";
			}

		}

		whereTmp1 += " or bbb.channelId in (" + twoCql[1] + ") )";

		InformationBD infoBD = new InformationBD();
		String readerWhere = infoBD.getInfoReader(userId, orgId, orgIdString,
				"aaa");

		if (hasRight_info || hasRight_depart) {
		} else {
			if (readerWhere == null || readerWhere.equals("")) {
			} else {
				whereTmp1 = whereTmp1 + " and (" + readerWhere + ") ";
			}
		}

		whereSQL += whereTmp1;
		whereSQL += ")";

		if (search != null && !search.equals("")) {

			// 如果用户搜索是选择了栏目则从该栏目选择否则从所有可以查看的栏目中选择
			if (!searchChannel.equals("0")) {

				whereSQL = whereSQL + " and ( bbb.channelId = " + searchChannel
						+ " or aaa.otherChannel='," + searchChannel + ",')";
			}

			// 搜索时指定日期
			if (searchDate != null && !searchDate.equals("")
					&& !searchDate.equals("null")) {

				if (databaseType.indexOf("mysql") >= 0) {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between '"
							+ searchBeginDate + "' and '" + searchEndDate
							+ " 23:59:59' ";
				} else {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between EZOFFICE.FN_STRTODATE('"
							+ searchBeginDate
							+ "','S') and EZOFFICE.FN_STRTODATE('"
							+ searchEndDate + " 23:59:59','') ";
				}

			}

			// 搜索指定发布人

			if (!searchIssuerName.equals("")) {
				whereSQL = whereSQL + " and aaa.informationIssuer like '%"
						+ searchIssuerName + "%' ";
			}
			// 搜索关键字的类型及内容
			// String searchKeywordType ="";//
			// httpServletRequest.getParameter("keywordType");
			// String searchKeyword =
			// "";//httpServletRequest.getParameter("keyword");

			if (searchKeywordType != null && !searchKeywordType.equals("")
					&& !searchKeywordType.equals("null")
					&& searchKeyword != null && !searchKeyword.equals("")
					&& !searchKeyword.equals("null")) {

				// 判断何种关键字类型及关键字的内容
				if (searchKeywordType.equals("title")) {
					// 标题
					whereSQL = whereSQL + " and aaa.informationTitle like '%"
							+ searchKeyword + "%' ";
				}
				if (searchKeywordType.equals("key")) {
					// 关键字
					whereSQL = whereSQL + " and aaa.informationKey like '%"
							+ searchKeyword + "%'";
				}
				if (searchKeywordType.equals("append")) {
					// 附标题
					whereSQL = whereSQL
							+ " and ( select count(*) from "
							+ " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc "
							+ " where ccc.accessoryName like '%"
							+ searchKeyword
							+ "%' and ccc.information.informationId = aaa.informationId) > 0 ";
				}

			} else {
				// 判断何种关键字类型及关键字的内容

				if (title != null && !title.equals("")) {
					// 标题
					whereSQL = whereSQL + " and aaa.informationTitle like '%"
							+ title + "%' ";
				}
				if (key != null && !key.equals("")) {
					// 关键字
					whereSQL = whereSQL + " and aaa.informationKey like '%"
							+ key + "%'";
				}
				if (subtitle != null && !subtitle.equals("")) {
					// 附标题
					whereSQL = whereSQL
							+ " and aaa.informationSubTitle like '%" + subtitle
							+ "%'";
				}

				if (append != null && !append.equals("")) {
					// 附件名
					whereSQL = whereSQL
							+ " and ( select count(*) from "
							+ " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc "
							+ " where ccc.accessoryName like '%"
							+ append
							+ "%' and ccc.information.informationId = aaa.informationId) > 0 ";
				}

			}

		}
		whereSQL = whereSQL
				+ " order by aaa.informationModifyTime desc,aaa.informationId desc";
		List resultlist = new ArrayList();
		resultlist = list1(pager_offset, viewSQL, fromSQL, whereSQL);
		return resultlist;
	}

	/**
	 * 最近更新分页
	 * @param pager_offset
	 * @param viewSQL
	 * @param fromSQL
	 * @param whereSQL
	 * @date 2012-2-11 ipad版
	 * @return
	 */
	private List list1(String pager_offset, String viewSQL, String fromSQL,
			String whereSQL) {

		List resultList = new ArrayList();
		List relist = new ArrayList();

		int pageSize = com.whir.common.util.Constants.MOBILE_DEFAULT_PAGE_SIZE;
		int offset = 0;

		if (pager_offset != null && !pager_offset.equals("")
				&& !pager_offset.equals("null")) {
			offset = Integer.parseInt(pager_offset);
		}
		int currentPage = (offset / pageSize) + 1;
		Page page = new Page(viewSQL, fromSQL, whereSQL);
		page.setPageSize(pageSize);
		page.setcurrentPage(currentPage);
		List list = page.getResultList();
		String recordCount = String.valueOf(page.getRecordCount());

		List rList0 = new ArrayList();
		List rList1 = new ArrayList();
		List rList10 = new ArrayList();

		if (list != null && list.size() > 0) {
			for (int jj = 0; jj < list.size(); jj++) {
				Object[] oobj = (Object[]) list.get(jj);
				List listjj = new ArrayList();
				listjj = java.util.Arrays.asList(oobj);
				rList0.add(listjj);
			}
		}

		rList10.add(pager_offset);// 1_0
		rList10.add(recordCount);// 1_1
		rList1.add(rList10);

		resultList.add(rList0);// 0
		resultList.add(rList1);// 1
		return resultList;

	}

	/**
	 * 取头条预览图片 若无图片返回空字符串
	 * @param informationId
	 * @date 2012-2-11 ipad版
	 * @return
	 */
	public String getHeadInfoImage(String informationId){
		String result = "";
		String sql = "select accessoryname,accessorysavename from oa_informationaccessory t where accessoryisimage = 1 "
				+" and accessorytype in ('jpg','bmp','gif','png','JPG','BMP','GIF','PNG') and information_id = " + informationId
				+" order by accessory_id ";
		CommentBD bd = new CommentBD();
		List list = bd.getDataBySQL(sql);
		if(list!=null && list.size()>0){
			Object[] obj = (Object[])list.get(0);
			result = obj[0].toString()+"|"+obj[1].toString();
		}
		return result;
	}

	/**
	 * 取图片新闻前4条记录
	 * @date 2012-2-11 ipad版
	 * @return
	 */
	public List getImageInfo(){
		List list = new ArrayList();
		String dbType = SystemCommon.getDatabaseType();
		String sql = "";
		if("oracle".equalsIgnoreCase(dbType)){
			sql = "select * from (select t.information_id,t.informationmodifytime,t.channel_id,t.informationtype,c.channeltype"
				+ " from oa_information t,oa_informationaccessory a,oa_informationchannel c "
				+ " where accessoryisimage = 1 and t.channel_id = c.channel_id and a.information_id = t.information_id"
				+ " and t.informationstatus = 0 order by t.informationmodifytime desc )ta where rownum < 5";
		}else if("mssqlserver".equalsIgnoreCase(dbType)){
			sql = "select top 4 t.information_id,t.informationmodifytime,t.channel_id,t.informationtype,c.channeltype "
				 +" from oa_information t,oa_informationaccessory a,oa_informationchannel c "
				 +" where accessoryisimage = 1 and t.channel_id = c.channel_id and a.information_id = t.information_id "
				 +" and t.informationstatus = 0 order by t.informationmodifytime desc ";
		}

		CommentBD bd = new CommentBD();
		List list1 = bd.getDataBySQL(sql);
		if(list1!=null && list1.size()>0){
			list = list1;
		}
		return list;
	}

	/**
	 * 用于去除信息内容中的html代码
	 * @param inputString
	 * @date 2012-2-11 ipad版
	 * @return
	 */
	public String HtmlToText(String inputString) {
        String htmlStr = inputString; //含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
            String regEx_script =
                "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
            String regEx_style =
                "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            //定义HTML标签的正则表达式
            String regEx_html = "<[^>]+>";

            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); //过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); //过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); //过滤html标签

            textStr = htmlStr;
        } catch(Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }
        return textStr.replaceAll("&nbsp;", " "); //返回文本字符串
    }

	/**
	 * 新建信息时取栏目列表
	 * @date 2012-2-20 ipad版
	 * @return
	 */
	public List getChannelList(Object[] obj){
		String userId = obj[0]==null ? "" : obj[0].toString();
		String orgId = obj[1]==null ? "" : obj[1].toString();
		String orgIdString = obj[2]==null ? "" : obj[2].toString();
		String domainId = obj[3]==null ? "" : obj[3].toString();
		String channelName = obj[4]==null || "请输入栏目名称".equals(obj[4].toString()) ? "" :obj[4].toString();
		String pager_offset = obj[5]==null ? "" :obj[5].toString();
        ManagerBD managerBD = new ManagerBD();
        String viewSQL = " aaa.channelId,aaa.channelName,aaa.channelIdString,aaa.channelType,aaa.userDefine,aaa.channelNameString ";
        String fromSQL = " com.whir.ezoffice.information.channelmanager.po.InformationChannelPO aaa ";
        String whereSQL = managerBD.getScopeFinalWhere(userId, orgId, orgIdString,"aaa.channelIssuer",
        		"aaa.channelIssuerOrg","aaa.channelIssuerGroup");
        //兼职组织
        NewInformationBD newInformationBD = new NewInformationBD();
        String sideLineOrg = newInformationBD.getSidelineOrgId(userId);
        if (sideLineOrg != null && !sideLineOrg.equals("") &&
            sideLineOrg.length() > 2) {
            sideLineOrg = sideLineOrg.substring(1, sideLineOrg.length() - 1);
            sideLineOrg.replaceAll("\\*", ",");
            sideLineOrg.replaceAll(",,", ",");
            String[] sarr = sideLineOrg.split(",");
            if (sarr != null && sarr.length > 0) {
                for (int i = 0; i < sarr.length; i++) {
                	whereSQL += " or aaa.channelIssuerOrg like '%*" + sarr[i] +
                            "*%' ";
                }
            }
        }
        whereSQL = " where ("+ whereSQL +") and aaa.channelNeedCheckup = 0 and aaa.domainId = " + domainId + " and aaa.channelName like '%"+channelName+"%'"
        	+" and ( aaa.afficheChannelStatus is null or aaa.afficheChannelStatus='0' )"
        	+" order by aaa.channelType,aaa.channelIdString ";
//        System.out.println("where==="+whereSQL);
		return list1(pager_offset, viewSQL, fromSQL, whereSQL);
	}

	/**
	 * 取自定义频道名称
	 * @date 2012-2-20 ipad版
	 * @param channelId
	 * @return
	 */
	public String getUserChannelName(String channelId){
		String result = "";
		String sql = "select userchannelname from oa_userchannel t where userchannel_id = "+channelId;
		CommentBD bd = new CommentBD();
		List list = bd.getDataBySQL(sql);
		if(list!=null && list.size()>0){
			Object[] obj = (Object[])list.get(0);
			result = obj[0].toString();
		}
		return result;
	}

	/**
	 * 取单位主页部门名称
	 * @date 2012-2-20 ipad版
	 * @param orgId
	 * @return
	 */
	public String getDeptName(String orgId){
		String result = "";
		String sql = "select orgname from org_organization where org_id ="+orgId;
		CommentBD bd = new CommentBD();
		List list = bd.getDataBySQL(sql);
		if(list!=null && list.size()>0){
			Object[] obj = (Object[])list.get(0);
			result = obj[0].toString();
		}
		return result;
	}

	/**
	 * 信息积分榜
	 * @date 2012-2-20 ipad版
	 * @return
	 */
	public List viewUserStat() {
		String viewSQL = " po.dealUserId,po.dealUserName, org.orgname,count(case when po.dealType='new' then po.activeScore end ) dealNew ,count(case when po.dealType='view' then po.activeScore end ) dealView ,"
				+ "sum(case when po.dealSort='1' then po.activeScore end ) as s1,sum(case when po.dealSort='2' then po.activeScore end ) as s2,sum(case when po.dealSort='3' then po.activeScore end ) as s3"
				+ ",sum(po.activeScore) s4, org.org_Id ,org.orgIdstring as dealOrgIdString";
		String fromSQL = " oa_informationStatistics po, org_organization org,org_organization_user orgUser, org_employee empuser";
		String orderBySQL = " order by s4 desc  ";
		String whereSQL = "where org.org_id=orguser.org_id and orguser.emp_id=po.dealUserId and  po.dealUserId=empuser.emp_id  ";
		whereSQL += " group by po.dealUserId,po.dealUserName,org.orgname,org.org_Id,org.orgIdstring ";
		int pageSize = 10;
		int offset = 0;

		int currentPage = (offset / pageSize) + 1;
		NewInformationBD newinformationBD = new NewInformationBD();
		Map map = newinformationBD.useSqlPageMap(viewSQL, fromSQL, whereSQL,
				orderBySQL, new Integer(currentPage), new Integer(pageSize));

		List list = (List) map.get("recordlist");
		if (list.size() == 0 && offset >= 15) {
			offset = offset - 15;
			currentPage = (offset / pageSize) + 1;
			map = newinformationBD
					.useSqlPageMap(viewSQL, fromSQL, whereSQL, orderBySQL,
							new Integer(currentPage), new Integer(pageSize));
			list = (List) map.get("recordlist");
		}
		return list;
	}

	/**
	 * 取信息
	 * 
	 * @param inobj
	 * @return
	 */
	public List getLastUpdateInfo(Object inobj[]) {
		/** @todo: complete the business logic here, this is just a skeleton. */
		String domainId = inobj[0] == null ? "" : inobj[0].toString(); // 域标识
		String userId = inobj[1] == null ? "" : inobj[1].toString(); // 域标识
		String orgId = inobj[2] == null ? "" : inobj[2].toString(); // 域标识
		String orgIdString = inobj[3] == null ? "" : inobj[3]
				.toString();//$120$$123$
		String type = inobj[4] == null ? "" : inobj[4].toString();// all
		String userDefine = inobj[5] == null ? "0" : inobj[5].toString();// 0
		String informationId = inobj[6] == null ? "" : inobj[6].toString();
		String search = inobj[7] == null ? "" : inobj[7].toString();
		String userChannelName = inobj[8] == null ? "" : inobj[8].toString();
		String channelType = inobj[9] == null ? "" : inobj[9].toString();
		String depart = inobj[10] == null ? "" : inobj[10].toString();
		String searchDate = inobj[11] == null ? "" : inobj[11].toString();
		String searchBeginDate = inobj[12] == null ? "" : inobj[12].toString();
		String searchEndDate = inobj[13] == null ? "" : inobj[13].toString();
		String searchIssuerName = inobj[14] == null ? "" : inobj[14].toString();
		String searchKeywordType = inobj[15] == null ? "" : inobj[15]
				.toString();
		String searchKeyword = inobj[16] == null ? "" : inobj[16].toString();
		String title = inobj[17] == null ? "" : inobj[17].toString();
		String key = inobj[18] == null ? "" : inobj[18].toString();
		String subtitle = inobj[19] == null ? "" : inobj[19].toString();
		String append = inobj[20] == null ? "" : inobj[20].toString();
		String searchChannel = inobj[21] == null ? "0" : inobj[21].toString();
		String pager_offset = inobj[22] == null ? "" : inobj[22].toString();
		String channelId = "";
		if(inobj.length > 23){
			channelId = inobj[23] == null ? "" : inobj[23].toString();
		}
		InformationBD informationBD = new InformationBD();
		ChannelBD channelBD = new ChannelBD();
		// 取用户可以查看的所有栏目,并返回到页面供搜索取栏目ID用
		List list = channelBD.getUserViewCh(userId, orgId, channelType,
				userDefine, domainId);

		java.util.Date now = new java.util.Date();
		String nowString = now.toLocaleString();
		nowString = nowString.substring(0, nowString.indexOf(" "));

		/**
		 * 取所有信息的条件为 1.信息的发布人 2.当前用户在信息的可维护人中 当前用户为栏目的可维护人 具有信息维护权限
		 * 信息的创建人或创建组织在维护范围内 3.当前用户在栏目的可查看范围内并且在信息的可查看范围内 4.栏目或信息的可查看人范围都没有选择
		 * 4.channelType=0
		 *
		 * where ((1) or (2) or (3)) and (4) (1): aaa.informationIssuerId=userId
		 * (2):
		 *
		 */

		String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, "
				+ " aaa.informationIssuer, aaa.informationVersion, aaa.informationContent,"
				+ " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead,"
				+ " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId,"
				+ " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite,"
				+ " aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg,aaa.informationIsCommend,bbb.channelType";
		String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa "
				+ " join aaa.informationChannel bbb ";
		String whereSQL = "";
		String databaseType = com.whir.common.config.SystemCommon
				.getDatabaseType();
		if (databaseType.indexOf("mysql") >= 0) {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or '"
					+ nowString
					+ "' between aaa.validBeginTime and aaa.validEndTime ) ";
		} else {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or EZOFFICE.FN_STRTODATE('"
					+ nowString
					+ "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
		}

		whereSQL += " and (  bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0'  ) ";

		// 用户可是栏目的创建人或者有栏目的维护权限则可以查看栏目的全部信息
		// 用户在栏目的维护人里则可以查看信息维护权限范围内的信息

		String rightWhere = new ManagerBD().getRightFinalWhere(userId, orgId,
				"01*03*03", "aaa.informationIssueOrgId",
				"aaa.informationIssuerId");
		String canReadAllInfoChannel = informationBD.getAllInfoChannel(userId,
				orgId, channelType, userDefine);
		String scopeWhere = new ManagerBD().getScopeFinalWhere(userId, orgId,
				orgIdString, "bbb.channelManager", "bbb.channelManagerOrg",
				"bbb.channelManagerGroup");

		/*whereSQL += " and ( ( (bbb.channelId in ("
				+ canReadAllInfoChannel
				+ ") or(("
				+ scopeWhere
				+ ") and ("
				+ rightWhere
				+ ")) or aaa.informationIssuerId="
				+ userId
				+ ") and (bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0') ) ";*/

		String twoCql[] = informationBD.getUserViewChAll(userId, orgId);
		boolean hasRight_info = false;
		boolean hasRight_depart = false;
		String rightCode_info = "01*03*03";
		String rightCode_depart = "01*01*02";
		List rightScopeList_depart = new com.whir.org.manager.bd.ManagerBD()
				.getRightScope(userId, rightCode_depart);
		if (rightScopeList_depart != null && rightScopeList_depart.size() > 0
				&& rightScopeList_depart.get(0) != null) {
			Object[] obj = (Object[]) rightScopeList_depart.get(0);
			if ("0".equals(obj[0].toString())) {
				hasRight_depart = true;
			}
		}

		List rightScopeList_info = new com.whir.org.manager.bd.ManagerBD()
				.getRightScope(userId, rightCode_info);
		if (rightScopeList_info != null && rightScopeList_info.size() > 0
				&& rightScopeList_info.get(0) != null) {
			Object[] obj = (Object[]) rightScopeList_info.get(0);
			if ("0".equals(obj[0].toString())) {
				hasRight_info = true;
			}
		}
		List userMenuChannelList = new ChannelBD().getAllUserMenuChannel(
				domainId, " and ("+new ManagerBD().getScopeFinalWhere(userId, orgId,
				orgIdString, "aaa.channelReader", "aaa.channelReadOrg",
				"aaa.channelReadGroup") +" or (aaa.channelReader is null or aaa.channelReader='') " +
				"or (aaa.channelReadOrg is null or aaa.channelReadOrg='') " +
				"or (aaa.channelReadGroup is null or aaa.channelReadGroup=''))");
		String ct = " ( 0 ";
		if (userMenuChannelList != null && userMenuChannelList.size() > 0) {
			for (int i = 0; i < userMenuChannelList.size(); i++) {
				ct += "," + userMenuChannelList.get(i);
			}
		}
		ct += " )";
		String whereTmp1 = "";

		whereTmp1 += " and  (  bbb.channelType in " + ct + "  ";
		if (hasRight_info) {
			whereTmp1 += " and ( ( (bbb.channelId in ("
				+ canReadAllInfoChannel
				+ ") or(("
				+ scopeWhere
				+ ") and ("
				+ rightWhere
				+ ")) or aaa.informationIssuerId="
				+ userId
				+ ") and (bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0') ) ";
		} else {
			whereTmp1 = " and ((((bbb.channelId in ("
				+ canReadAllInfoChannel
				+ ") or(("
				+ scopeWhere
				+ ") and ("
				+ rightWhere
				+ ")) or aaa.informationIssuerId="
				+ userId
				+ ") and (bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0') ) or (  bbb.channelType in " + ct + "  "
					+ " and bbb.channelId in (" + twoCql[0] + ")";
			whereTmp1 += " and bbb.channelType = " + channelType;
			InformationBD infoBD = new InformationBD();
			String readerWhere = infoBD.getInfoReader(userId, orgId,
					orgIdString, "aaa");

			if (readerWhere == null || readerWhere.equals("")) {

			} else {
				whereTmp1 = whereTmp1 + " and (" + readerWhere + ") )";
			}

		}
		whereTmp1 += " or bbb.channelId in (" + twoCql[1] + ") )";

		InformationBD infoBD = new InformationBD();
		String readerWhere = infoBD.getInfoReader(userId, orgId, orgIdString,
				"aaa");
		if (hasRight_info || hasRight_depart) {
		} else {
			if (readerWhere == null || readerWhere.equals("")) {
			} else {
				whereTmp1 = whereTmp1 + " and (" + readerWhere + ") ";
			}
		}

		whereSQL += whereTmp1;
		//whereSQL += ")";
		if(depart.equals("") && !userDefine.equals("1")) {//信息
			whereSQL += ") and bbb.channelType = 0 ";
		}else if(depart.equals("1")) {//单位主页
			if(!hasRight_info){
				whereSQL += ") and bbb.channelType > 0 and bbb.userDefine <> 1 ";
			}else {
				whereSQL += "or 1=1) and bbb.channelType > 0 and bbb.userDefine <> 1 ";
			}
		}else if(userDefine.equals("1")){//自定义信息频道
			whereSQL += "or 1=1) and bbb.channelType > 0 and bbb.userDefine = 1 and ( bbb.channelType in " + ct +")";
		}else{
			//depart=='0' userDefine <>1时 取上述3种所有信息
			whereSQL += ")";
		}
		//System.out.println("whereSQL:\n"+whereSQL);
		if (search != null && !search.equals("")) {

			// 如果用户搜索是选择了栏目则从该栏目选择否则从所有可以查看的栏目中选择
			if (!searchChannel.equals("0")) {
				//选择栏目查询或者点击具体栏目信息列表
	            InformationChannelPO po = channelBD.loadChannel(searchChannel);
	        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
	        		whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
	        	}else{//不包含子栏目则只查该栏目中的信息
	        		whereSQL = whereSQL + " and ( bbb.channelId = " + searchChannel
					+ " or aaa.otherChannel='," + searchChannel + ",')";
	        	}
			}

			// 搜索时指定日期
			if (searchDate != null && !searchDate.equals("")
					&& !searchDate.equals("null")) {

				if (databaseType.indexOf("mysql") >= 0) {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between '"
							+ searchBeginDate + "' and '" + searchEndDate
							+ " 23:59:59' ";
				} else {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between EZOFFICE.FN_STRTODATE('"
							+ searchBeginDate
							+ "','S') and EZOFFICE.FN_STRTODATE('"
							+ searchEndDate + " 23:59:59','') ";
				}

			}

			// 搜索指定发布人

			if (!searchIssuerName.equals("")) {
				whereSQL = whereSQL + " and aaa.informationIssuer like '%"
						+ searchIssuerName + "%' ";
			}
			// 搜索关键字的类型及内容
			// String searchKeywordType ="";//
			// httpServletRequest.getParameter("keywordType");
			// String searchKeyword =
			// "";//httpServletRequest.getParameter("keyword");

			if (searchKeywordType != null && !searchKeywordType.equals("")
					&& !searchKeywordType.equals("null")
					&& searchKeyword != null && !searchKeyword.equals("")
					&& !searchKeyword.equals("null")) {

				// 判断何种关键字类型及关键字的内容
				if (searchKeywordType.equals("title")) {
					// 标题
					whereSQL = whereSQL + " and aaa.informationTitle like '%"
							+ searchKeyword + "%' ";
				}
				if (searchKeywordType.equals("key")) {
					// 关键字
					whereSQL = whereSQL + " and aaa.informationKey like '%"
							+ searchKeyword + "%'";
				}
				if (searchKeywordType.equals("append")) {
					// 附标题
					whereSQL = whereSQL
							+ " and ( select count(*) from "
							+ " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc "
							+ " where ccc.accessoryName like '%"
							+ searchKeyword
							+ "%' and ccc.information.informationId = aaa.informationId) > 0 ";
				}

			} else {
				// 判断何种关键字类型及关键字的内容

				if (title != null && !title.equals("")) {
					// 标题
					whereSQL = whereSQL + " and aaa.informationTitle like '%"
							+ title + "%' ";
				}
				if (key != null && !key.equals("")) {
					// 关键字
					whereSQL = whereSQL + " and aaa.informationKey like '%"
							+ key + "%'";
				}
				if (subtitle != null && !subtitle.equals("")) {
					// 附标题
					whereSQL = whereSQL
							+ " and aaa.informationSubTitle like '%" + subtitle
							+ "%'";
				}

				if (append != null && !append.equals("")) {
					// 附件名
					whereSQL = whereSQL
							+ " and ( select count(*) from "
							+ " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc "
							+ " where ccc.accessoryName like '%"
							+ append
							+ "%' and ccc.information.informationId = aaa.informationId) > 0 ";
				}
			}
		}
		if(!channelId.equals("") && channelId.length() > 0){
			whereSQL += " and bbb.channelId = "+channelId;
		}
		System.out.println("whereSQL:"+whereSQL);
		whereSQL = whereSQL
				+ " order by aaa.orderCode desc, case when aaa.informationModifyTime is null then aaa.informationIssueTime else aaa.informationModifyTime end desc ";
		List resultlist = new ArrayList();
		resultlist = list1(pager_offset, viewSQL, fromSQL, whereSQL);
		return resultlist;
	}
	
	public  List  LoadModiInfo(String argStr[]){
	      /*
	         标题
	       所属栏目： 信息管理.审(权限)
	       副标题：
	       文档编号：0
	       同时发布到：
	       关键字：
	       作者：
	       作者单位：
	       文章类型： 撰写
	       来源：
	       可查看人：质量部,项目部,研发部,
	       摘要：
	       禁止打印拷贝： 否
	       内容：

	       new String []{"标　　题","所属栏目","　副标题","文档编号","同时发布","　关键字","作　　者","作者单位","文章类型","来　　源","可查看人","摘　　要","禁止拷贝"}
	       */

	      String workId = argStr[0].toString();
	      
	      String channelId = "";
	      String informationId = "";
	      String processId = "";
	      String tableId = "";


	      //去取流程信息
	        Map _workMap = new HashMap(0);
	        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
	        String sql = "select ";
	        sql += " WORKFILETYPE, ";//0
	        sql += " WORKCURSTEP, ";//1
	        sql += " WORKTITLE, ";//2
	        sql += " WORKDEADLINE, ";//3
	        sql += " WORKSUBMITPERSON, ";//4
	        sql += " WORKSUBMITTIME, ";//5
	        sql += " WORKTYPE, ";//6
	        sql += " WORKACTIVITY, ";//7
	        sql += " WORKTABLE_ID, ";//8
	        sql += " WORKRECORD_ID, ";//9
	        sql += " WF_WORK_ID, ";//10
	        sql += " WORKSUBMITPERSON, ";//11
	        sql += " WF_SUBMITEMPLOYEE_ID, ";//12
	        sql += " WORKALLOWCANCEL, ";//13
	        sql += " WORKPROCESS_ID, ";//14
	        sql += " WORKSTEPCOUNT, ";//15
	        sql += " WORKMAINLINKFILE, ";//16
	        sql += " WORKSUBMITTIME, ";//17
	        sql += " WORKCURSTEP, ";//18
	        sql += " CREATORCANCELLINK, ";//19
	        sql += " ISSTANDFORWORK, ";//20
	        sql += " STANDFORUSERID, ";//21
	        sql += " STANDFORUSERNAME, ";//22
	        sql += " WORKCREATEDATE, ";//23
	        sql += " SUBMITORG, ";//24
	        sql += " WORKDONEWITHDATE, ";//25
	        sql += " EMERGENCE, ";//26
	        sql += " INITACTIVITY, ";//27
	        sql += " INITACTIVITYNAME, ";//28
	        sql += " TRANTYPE, ";//29
	        sql += " TRANFROMPERSONID, ";//30
	        sql += " PROCESSDEADLINEDATE, ";//31
	        sql += " WF_CUREMPLOYEE_ID ";//32
	        sql += "  from WF_WORK ";
	        sql += " where WF_WORK_ID = " + workId;
	        try {
	                dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

	        _workMap = dbopt.executeQueryToMap(sql);

	                dbopt.close();
	        } catch (Exception ex) {
	                ex.printStackTrace();
	        } finally {
	                try {
	                        dbopt.close();
	                } catch (Exception ex1) {}
	        }
	        //System.out.println("--------------"+_workMap);

	        String creatorcancellink = (String)_workMap.get("creatorcancellink");
	        String workstepcount = (String)_workMap.get("workstepcount");
	        String processdeadlinedate = (String)_workMap.get("processdeadlinedate");
	        String workallowcancel = (String)_workMap.get("workallowcancel");
	        String workdonewithdate = (String)_workMap.get("workdonewithdate");
	        String workcreatedate = (String)_workMap.get("workcreatedate");
	        String wf_curemployee_id = (String)_workMap.get("wf_curemployee_id");
	        String initactivity = (String)_workMap.get("initactivity");
	        String tranfrompersonid = (String)_workMap.get("tranfrompersonid");
	        String workcurstep = (String)_workMap.get("workcurstep");
	        String workmainlinkfile = (String)_workMap.get("workmainlinkfile");
	        String worksubmitperson = (String)_workMap.get("worksubmitperson");
	        String initactivityname = (String)_workMap.get("initactivityname");
	        String worktable_id = (String)_workMap.get("worktable_id");
	        String standforuserid = (String)_workMap.get("standforuserid");
	        String wf_work_id = (String)_workMap.get("wf_work_id");
	        String worksubmittime = (String)_workMap.get("worksubmittime");
	        String wf_submitemployee_id = (String)_workMap.get("wf_submitemployee_id");
	        String worktype = (String)_workMap.get("worktype");
	        String workrecord_id = (String)_workMap.get("workrecord_id");
	        String workprocess_id = (String)_workMap.get("workprocess_id");
	        String workactivity = (String)_workMap.get("workactivity");
	        String submitorg = (String)_workMap.get("submitorg");
	        String isstandforwork = (String)_workMap.get("isstandforwork");
	        String standforusername = (String)_workMap.get("standforusername");
	        String workdeadline = (String)_workMap.get("workdeadline");
	        String trantype = (String)_workMap.get("trantype");
	        String emergence = (String)_workMap.get("emergence");
	        String workfiletype = (String)_workMap.get("workfiletype");
	        String worktitle = (String)_workMap.get("worktitle");
	        
	        InformationPO po = new InformationBD().load(workrecord_id);
	        channelId = po.getInformationChannel().getChannelId()+"";
//	        int cK=0;
//	        int tK=0;
//	        if(workmainlinkfile!=null){
//	             tK=workmainlinkfile.indexOf("&channelType=");
//	             cK=workmainlinkfile.indexOf("&channelId=");
//	             //channelId在最后
//	             if (tK < cK) {
//	                 channelId = workmainlinkfile.substring(cK + 11);
//
//	             } else { //channelType在最后
//	                 channelId = workmainlinkfile.substring(cK + 11, tK);
//	             }
//	        }

	        informationId = workrecord_id;
	        processId = workprocess_id;
	        tableId = worktable_id;


	        List paramList = new ArrayList();
	        paramList.add(creatorcancellink!=null?creatorcancellink:"");
	        paramList.add(workstepcount!=null?workstepcount:"");
	        paramList.add(processdeadlinedate!=null?processdeadlinedate:"");
	        paramList.add(workallowcancel!=null?workallowcancel:"");
	        paramList.add(workdonewithdate!=null?workdonewithdate:"");
	        paramList.add(workcreatedate!=null?workcreatedate:"");
	        paramList.add(wf_curemployee_id!=null?wf_curemployee_id:"");
	        paramList.add(initactivity!=null?initactivity:"");
	        paramList.add(tranfrompersonid!=null?tranfrompersonid:"");
	        paramList.add(workcurstep!=null?workcurstep:"");
	        paramList.add(workmainlinkfile!=null?workmainlinkfile:"");
	        paramList.add(worksubmitperson!=null?worksubmitperson:"");
	        paramList.add(initactivityname!=null?initactivityname:"");
	        paramList.add(worktable_id!=null?worktable_id:"");//13
	        paramList.add(standforuserid!=null?standforuserid:"");
	        paramList.add(wf_work_id!=null?wf_work_id:"");
	        paramList.add(worksubmittime!=null?worksubmittime:"");
	        paramList.add(wf_submitemployee_id!=null?wf_submitemployee_id:"");
	        paramList.add(worktype!=null?worktype:"");
	        paramList.add(workrecord_id!=null?workrecord_id:"");//19
	        paramList.add(workprocess_id!=null?workprocess_id:"");//20
	        paramList.add(workactivity!=null?workactivity:"");//21
	        paramList.add(submitorg!=null?submitorg:"");//22
	        paramList.add(isstandforwork!=null?isstandforwork:"");
	        paramList.add(standforusername!=null?standforusername:"");
	        paramList.add(workdeadline!=null?workdeadline:"");
	        paramList.add(trantype!=null?trantype:"");
	        paramList.add(emergence!=null?emergence:"");
	        paramList.add(workfiletype!=null?workfiletype:"");
	        paramList.add(worktitle!=null?worktitle:"");

	        //activityClass  当是3时 是自动返回活动
		//String[] activityInfo =new com.whir.ezoffice.workflow.newBD.WorkFlowBD().getActivityClass(worktable_id,workrecord_id, workactivity);

	        List activityInfoList= new com.whir.ezoffice.workflow.newBD.WorkFlowBD().getActivityClass_multi(worktable_id,workrecord_id, workactivity);
	        String[] activityInfoListObj=(String[])activityInfoList.get(0);
	        String  activityClassclass=activityInfoListObj[0];

	        paramList.add(activityInfoListObj[0]!=null?activityInfoListObj[0]:"");

	      String informationType = "";
	      InformationBD informationBD = new InformationBD();
	      List infortype = new ArrayList();
	      infortype = informationBD.getinformation(informationId);
	      if (infortype != null && infortype.size() > 0) {
	          Object[] objlistype = null;
	          objlistype = (Object[]) infortype.get(0);
	          informationType = objlistype[0].toString();
	      }

	      String str14 = informationType;

	      //   httpServletRequest.setAttribute("informationType", informationType);

	      ChannelBD channelBD = new ChannelBD();

	      String str1 = ""; //栏目

	      if (channelId != null) {

	          List channelList = channelBD.getSingleChannel(channelId);
	          if (channelList != null && channelList.size() > 0) {
	              Object[] obj = null;
	              obj = (Object[]) channelList.get(0);
	              str1 = obj[0] + "";
	          }

	      }

	      List list = informationBD.getSingleInfo(informationId, channelId);

	      if (list.size() > 1) {
	          str1 = list.get(1).toString();
	      }

	      Object[] obj = (Object[]) list.get(0);

	      String tmpTitle = obj[0].toString();
	      if (tmpTitle.startsWith("<font color=red>")) {
	          tmpTitle = tmpTitle.replaceAll("<font color=red>", "");
	          tmpTitle = tmpTitle.replaceAll("</font>", "");
	      }
	      String str0 = tmpTitle;

	      //-------
	      String str2 = ""; ////副标题：
	      if (obj[1] != null) { //副标题：
	          str2 = obj[1].toString();
	      }

	      String str3 = ""; //文档编号：0
	      if (obj[41] != null) {
	          str3 = obj[41].toString();
	      }

	      String str4 = ""; //同时发布到：---------------------------------------
	      if (obj[32] != null) {
	          String otherChannel = obj[32].toString().substring(1,obj[32].toString().length()-1);
	          if(!otherChannel.equals("0")){
	        	  String[] channel = otherChannel.split(",");
	        	  for(int i=0;i<channel.length;i++){
	        		  List l = informationBD.getSingleInfo(informationId, channel[i]);
	        		  String channelName = "";
	        		  if (l.size() > 1) {
	        			  channelName = l.get(1).toString();
	        	      }
	        		  str4 += channelName +",";
	        	  }
	        	  str4 = str4.substring(0,str4.length()-1);
	          }
	      }
	      
	      String str5 = ""; //关键字：
	      if (obj[15] != null) {
	          str5 = obj[15].toString();
	      }

	      String str6 = ""; //作者：
	      if (obj[8] != null) {
	          str6 = obj[8].toString();
	      }

	      String str7 = ""; //作者单位：
	      if (obj[42] != null) {
	          str7 = obj[42].toString();
	      }

	      String str8 = "撰写"; //文章类型： 撰写
	      if (obj[43] != null) {
	          if (obj[43].toString().equals("0")) {
	              str8 = "撰写";
	          } else if (obj[43].toString().equals("1")) {
	              str8 = "编辑";
	          } else if (obj[43].toString().equals("2")) {
	              str8 = "摘录";
	          }
	      }

	      String str9 = ""; //来源：
	      if (obj[39] != null) {
	          str9 = obj[39].toString();
	      }

	      String str10 = ""; //可查看人：质量部,项目部,研发部,
	      if (obj[16] != null) {
	          str10 = obj[16].toString();
	      }

	      String str11 = ""; //摘要：
	      if (obj[14] != null) {
	          str11 = obj[14].toString();
	      }

	      String str12 = "否"; //禁止打印拷贝： 否
	      if (obj[26] != null && obj[26].toString().equals("1")) {
	          str12 = "是";
	      }

	      String str13 = ""; //内容;
	      if (obj[2] != null) {
	          str13 = obj[2].toString();
	      }

	      //取附件和图片
	      InformationAccessoryBD informationAccessoryBD = new
	              InformationAccessoryBD();
	      list = informationAccessoryBD.getAccessory(informationId);
	      java.util.ArrayList picList = new java.util.ArrayList();
	      java.util.ArrayList appList = new java.util.ArrayList();
	      Object[] tmp = null;
	      if (list != null) {
	          for (int i = 0; i < list.size(); i++) {
	              tmp = (Object[]) list.get(i);
	              if (tmp[4].toString().equals("1")) {
	                  picList.add(list.get(i));
	              } else {
	                  appList.add(list.get(i));
	              }
	          }
	      }

	      List rList0 = new ArrayList();
	      List rList1 = new ArrayList();
	      List rList2 = new ArrayList();
	      List rList3 = new ArrayList();
	      List rList4 = new ArrayList();
	      List rList5 = new ArrayList();
	      List rList6=new ArrayList();

	      String infoArr[] = new String[] {str0, str1, str2, str3, str4, str5, str6,
	                         str7, str8, str9, str10, str11, str12, str13, str14};
	      List rList00=new ArrayList();
	      rList00=java.util.Arrays.asList(infoArr);
	      rList0.add(rList00);

	      List resultList = new ArrayList();

	      //resultList.add(infoArr);//0
	      resultList.add(rList0);

	     // resultList.add(picList);//1
	     if (picList != null && picList.size() > 0) {
	         for (int jj = 0; jj < picList.size(); jj++) {
	             Object[] objjj = (Object[]) picList.get(jj);
	             List listjj = java.util.Arrays.asList(objjj);
	             rList1.add(listjj);
	         }
	     }
	     resultList.add(rList1);



	      //resultList.add(appList);//2
	      if (appList != null && appList.size() > 0) {
	          for (int jj = 0; jj < appList.size(); jj++) {
	              Object[] objjj = (Object[]) appList.get(jj);
	              List listjj = java.util.Arrays.asList(objjj);
	              rList2.add(listjj);
	          }
	      }
	      resultList.add(rList2);


	      //取得审批意见
	      List commentList = new com.whir.ezoffice.workflow.newBD.WorkFlowBD().
	                         getDealWithCommentNotBack(tableId, informationId, "1");
	      if (commentList != null && commentList.size() > 0) {
	          for (int jj = 0; jj < commentList.size(); jj++) {
	              Object[] objjj = (Object[]) commentList.get(jj);
	              List listjj = java.util.Arrays.asList(objjj);
	              rList3.add(listjj);
	          }
	      }
	      resultList.add(rList3); //3


	      //取得退回意见
	      String backCommentStr = new com.whir.ezoffice.workflow.newBD.
	                              WorkFlowButtonBD().getBackComment(
	                                      processId, tableId, informationId);
	      List rList40 = new ArrayList();
	      rList40.add(backCommentStr);
	      rList4.add(rList40);
	      resultList.add(rList4); //4


	      rList5.add(paramList);
	      resultList.add(rList5); //5

	     return resultList;
	  }

	/**
	 * 图片新闻列表
	 * @param inobj
	 * @return
	 */
	public List getImageInformationListMobile(Object inobj[]) {
		String domainId = inobj[0] == null ? "" : inobj[0].toString(); // 域标识
		String userId = inobj[1] == null ? "" : inobj[1].toString(); // 域标识
		String orgId = inobj[2] == null ? "" : inobj[2].toString(); // 域标识
		String orgIdString = inobj[3] == null ? "" : inobj[3].toString();
		String type = inobj[4] == null ? "all" : inobj[4].toString(); // all
		String userDefine = inobj[5] == null ? "0" : inobj[5].toString(); // 0
		String informationId = inobj[6] == null ? "" : inobj[6].toString();
		String search = inobj[7] == null ? "" : inobj[7].toString();
		String userChannelName = inobj[8] == null ? "" : inobj[8].toString();
		String channelType = inobj[9] == null ? "" : inobj[9].toString();
		String depart = inobj[10] == null ? "" : inobj[10].toString();
		String searchDate = inobj[11] == null ? "" : inobj[11].toString();
		String searchBeginDate = inobj[12] == null ? "" : inobj[12].toString();
		String searchEndDate = inobj[13] == null ? "" : inobj[13].toString();
		String searchIssuerName = inobj[14] == null ? "" : inobj[14].toString();
		String searchKeywordType = inobj[15] == null ? "" : inobj[15].toString();
		String searchKeyword = inobj[16] == null ? "" : inobj[16].toString();
		String title = inobj[17] == null ? "" : inobj[17].toString();
		String key = inobj[18] == null ? "" : inobj[18].toString();
		String subtitle = inobj[19] == null ? "" : inobj[19].toString();
		String append = inobj[20] == null ? "" : inobj[20].toString();
		String searchChannel = inobj[21] == null ? "0" : inobj[21].toString();
		String pager_offset = inobj[22] == null ? "" : inobj[22].toString();
		String pageSize = "";
		if(inobj.length > 24){
			pageSize = inobj[24] == null ? "" : inobj[24].toString();
		}
		java.util.Date now = new java.util.Date();
		String nowString = now.toLocaleString();
		nowString = nowString.substring(0, nowString.indexOf(" "));

		String viewSQL = " distinct aaa.informationId, aaa.informationTitle, aaa.informationKits, "
			+ " aaa.informationIssuer, aaa.informationVersion, "
			+ " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead,"
			+ " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId,"
			+ " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite,"
			+ " aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg,aaa.informationIsCommend,bbb.channelType";

		String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa "
				+ " join aaa.informationChannel bbb ,com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc ";
		String whereSQL = "";
		String databaseType = com.whir.common.config.SystemCommon
				.getDatabaseType();
		CustomizeService cs = new CustomizeService();
		boolean departInfo = false;
		try {
			departInfo = cs.hasDapartAuth(userId, orgIdString, domainId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (databaseType.indexOf("mysql") >= 0) {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or '"
					+ nowString
					+ "' between aaa.validBeginTime and aaa.validEndTime ) ";
		} else {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or EZOFFICE.FN_STRTODATE('"
					+ nowString
					+ "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
		}
		if(!departInfo){
			whereSQL += "and ( bbb.channelType = 0 or bbb.userDefine = 1 ) ";
		}

		whereSQL += " and ( bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0' ) ";
		
		InformationBD informationBD = new InformationBD();
		String whereSQL1 = informationBD.getUserViewInfo(userId,orgId);
		whereSQL += " and ((" + whereSQL1 + " aaa.informationReader like '%"+ userId +"%' " +
				"or aaa.informationReaderName is null or aaa.informationReaderName = '' ) and " ;

		PortletBD portletBD = new PortletBD();
		String canViewChannel = portletBD.getInformationCategoryWhereSQL(userId, orgId, orgIdString, domainId);
		canViewChannel = canViewChannel.replaceAll("aaa.", "bbb.");
		whereSQL += canViewChannel+") ";
		ChannelBD channelBD = new ChannelBD();
		/*List channelList = channelBD.getUserViewCh(userId,orgId,channelType,userDefine,domainId);
		if(channelList!=null && channelList.size()>0){
			String channels = "";
			for(int i=0;i<channelList.size();i++){
				Object[] obj = (Object[]) channelList.get(i);
				channels += obj[0].toString()+",";
			}
			channels = channels.substring(0,channels.length()-1);
			whereSQL += " and bbb.channelId in ("+channels+") ";
		}*/
		
		if (search != null && !search.equals("") && search.equals("1")) {
			// 如果用户搜索是选择了栏目则从该栏目选择否则从所有可以查看的栏目中选择
			if (!searchChannel.equals("0") && !searchChannel.equals("")
					&& !searchChannel.equals("null")) {
				InformationChannelPO po = channelBD.loadChannel(searchChannel);
            	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
            		whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
            	}else{//不包含子栏目则只查该栏目中的信息
            		whereSQL = whereSQL + " and (bbb.channelId = " + searchChannel + " or aaa.otherChannel like '%," + searchChannel + ",%') ";
            	}
			}
			// 搜索时指定日期
			if (searchDate != null && !searchDate.equals("")
					&& !searchDate.equals("null")) {
				if (databaseType.indexOf("mysql") >= 0) {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between '"
							+ searchBeginDate + "' and '" + searchEndDate
							+ " 23:59:59' ";
				} else {
					whereSQL = whereSQL
							+ " and aaa.informationIssueTime between EZOFFICE.FN_STRTODATE('"
							+ searchBeginDate
							+ "','S') and EZOFFICE.FN_STRTODATE('"
							+ searchEndDate + " 23:59:59','') ";
				}
			}
			// 搜索指定发布人
			if (!searchIssuerName.equals("")) {
				whereSQL = whereSQL + " and aaa.informationIssuer like '%"
						+ searchIssuerName + "%' ";
			}
			// 搜索关键字的类型及内容
			// String searchKeywordType ="";//
			// httpServletRequest.getParameter("keywordType");
			// String searchKeyword =
			// "";//httpServletRequest.getParameter("keyword");
			if (title != null && !title.equals("")) {
				// 标题
				whereSQL = whereSQL + " and aaa.informationTitle like '%"
						+ title + "%' ";
			}
			if (key != null && !key.equals("")) {
				// 关键字
				whereSQL = whereSQL + " and aaa.informationKey like '%" + key
						+ "%'";
			}
			if (subtitle != null && !subtitle.equals("")) {
				// 附标题
				whereSQL = whereSQL + " and aaa.informationSubTitle like '%"
						+ subtitle + "%'";
			}
			if (append != null && !append.equals("")) {
				// 附件名
				whereSQL = whereSQL
						+ " and ( select count(*) from "
						+ " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc "
						+ " where ccc.accessoryName like '%"
						+ append
						+ "%' and ccc.information.informationId = aaa.informationId) > 0 ";
			}
		}
		
		if (inobj.length > 23) {
            String retrievalKey = "" + inobj[23];
            if(!retrievalKey.equals("")&&!retrievalKey.equals("null")){
                LuceneClient luceneClient = new LuceneClient();
                String lids = luceneClient.getIdByLucenceRestult("",
                        "information",
                        retrievalKey);
                if (lids != null && !lids.equals("null") && lids.length() > 1) {
                    whereSQL += " and aaa.informationId in(" + lids + ") ";
                } else {
                    whereSQL += " and (1>2) ";
                }

            }
        }

		whereSQL = whereSQL + "and ccc.accessoryIsImage = 1 and ccc.information.informationId = aaa.informationId " +
				"order by aaa.orderCode desc,aaa.informationModifyTime desc,aaa.informationId desc";
		List resultlist = new ArrayList();
		resultlist = list(pager_offset, viewSQL, fromSQL, whereSQL, pageSize);
		return resultlist;
	}
	
	/**
	 * 根据父栏目Id取子栏目Id
	 * @return
	 */
	public List getChildChannelIdByChannelId(String channelId){
		List result = new ChannelBD().getChannelByParent(channelId,"0","0","0");
		return result;
	}
	
	/**
	 * 根据AppId取栏目Id
	 * @param appId
	 * @return
	 * @throws Exception 
	 */
	public String getChannelIdByAppId(String appId) {
		String channelId = "";
		try {
			channelId = new ChannelBD().getChannelIdByAppId(appId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return channelId;
	}
	
	/**
	 * 获取易播频道数据
	 * @return
	 */
	public List getYiBoInfoList(){
		System.out.println("========获取易播频道接口方法开始========");
		String domainId = "0";
		YiBoChannelBD bd = new YiBoChannelBD();
        
        List list  = new ArrayList();
        List yiboChannelList = new ArrayList();
		try {
			list = bd.getAllYiBoChannel(domainId);
			if(list != null && list.size() > 0){
	        	for(int i = 0; i < list.size(); i ++){
	        		Object[] obj = (Object[]) list.get(i);
	        		Object[] result = new Object[3];
	        		for(int j=0; j<11; j++){
	        			//易播频道id和名称
	        			if(j==0 || j==1){
	        				result[j] = obj[j];
	        			}else if(j == 9){
	        				//易播频道播放条数
	        				result[2] = obj[j];
	        			}else{
	        				
	        			}
	    			}
	        		yiboChannelList.add(result);
	        	}
	        }
		} catch (Exception e) {
			System.out.println("========获取易播频道接口方法异常:" + e.getMessage());
			e.printStackTrace();
		}
        
        return yiboChannelList;
	}
	
	/**
	 * 获取易播频道下详细的信息数据
	 * @return
	 */
	public List getYiBoInfoMsgList(String channelId){
		System.out.println("========获取易播频道信息列表接口方法开始========");
		YiBoChannelBD bd = new YiBoChannelBD();
        
        List list  = new ArrayList();
        List yiboChannelList = new ArrayList();
        List msgAppendList = new ArrayList();
        //读取配置文件中企业信息
		com.whir.component.config.ConfigXMLReader  reader=new com.whir.component.config.ConfigXMLReader();
		String  filepath = reader.getAttribute("orginfo", "fileurl");
        try {
			list = bd.getYiBoChannelMsgList(channelId);
			if(list != null && list.size() > 0){
	        	for(int i = 0; i < list.size(); i ++){
	        		//取到每条易播栏目信息，处理成规范的返回数据
	        		//aaa.informationId,aaa.informationTitle,aaa.informationIssueTime,
	        		//aaa.informationAuthor,aaa.informationContent,aaa.informationType
	        		Object[] obj = (Object[]) list.get(i);
	        		//易播信息类型：0（普通）或3（文件），其他情况不考虑
	        		if( !( "0".equals(obj[5].toString()) || "3".equals(obj[5].toString()) ) ){
	        			continue;
	        		}
	        		//根据信息id：obj[0]获取附件信息
	        		//aaa.accessoryIsImage,aaa.accessoryType,aaa.accessoryName,aaa.accessorySaveName,aaa.domainId
	        		msgAppendList = bd.getMsgAppend(obj[0].toString());
	        		int appendSize = msgAppendList.size();
	        		//result:"msgId,msgType,title,date,author,content,videoUrl,imgUrl"
	        		
	        		//根据附件数目，如果多图，拆分成多条信息，出图片附件url其他参数相同
	        		if(appendSize > 0){
	        			for(int x=0 ; x < appendSize ;x ++){
	        				Object[] result = new Object[8];
	    	        		result[0] = obj[0]; //信息id
	            			result[2] = obj[1]; //信息标题
	            			result[3] = obj[2]; //发布时间
	            			result[4] = obj[3]; //作者
	        				//取每一条附件的信息
	        				Object[] obj_append = (Object[]) msgAppendList.get(x);
	        				//信息类型是3-文件链接时，对应易播信息0-视频消息
		        			if( "3".equals(obj[5].toString()) ){
		        				result[1] = "0"; //0-视频消息
		        				result[5] = ""; //文字内容
		        				//获取视频附件url（文件名）
		        				int start = obj[4].toString().indexOf(":");
		        				String videoname = obj[4].toString().substring(start + 1);
		        				result[6] = filepath + videoname.substring(0, 6) + "/" + videoname;
		        				result[7] = ""; //图片
		        			}else if("0".equals(obj[5].toString())){
		        				//信息类型0-普通时，对应易播信息分为1-纯图，（2-纯文，不含该情况），3-图文
		        				//信息内容obj[4]为空，则对应1-纯图
		        				if(obj[4] == null || "".equals(obj[4].toString())){
		        					result[1] = "1"; //1-纯图
		        					result[5] = ""; //文字内容
		    	        			result[6] = ""; //视频文件url
		    	        			result[7] = filepath + obj_append[3].toString().substring(0, 6) + "/" + obj_append[3].toString(); //图片
		        				}else{
		        					result[1] = "3"; //3-图文
		        					result[5] = obj[4]; //文字内容
		    	        			result[6] = ""; //视频文件url
		    	        			result[7] = filepath + obj_append[3].toString().substring(0, 6) + "/" + obj_append[3].toString(); //图片
		        				}
		        				
		        			}else{
		        				//易播只含0-普通，3-文件（视频） 类型的信息，其他情况暂不考虑
		        			}
		        			//添加到返回的易播信息列表中
		        			//System.out.println("=========result[7]图片：" + result[7].toString());
		        			yiboChannelList.add(result);
	        			}
	        		}else{
	        			Object[] result = new Object[8];
		        		result[0] = obj[0]; //信息id
	        			result[2] = obj[1]; //信息标题
	        			result[3] = obj[2]; //发布时间
	        			result[4] = obj[3]; //作者
	        			//信息类型是3-文件链接时，对应易播信息0-视频消息
	        			if( "3".equals(obj[5].toString()) ){
	        				result[1] = "0"; //0-视频消息
	        				result[5] = ""; //文字内容
	        				//获取视频附件url（文件名）
	        				int start = obj[4].toString().indexOf(":");
	        				String videoname = obj[4].toString().substring(start + 1);
	        				result[6] = filepath + videoname.substring(0, 6) + "/" + videoname;
	        				result[7] = ""; //图片
	        			}else if("0".equals(obj[5].toString())){
	        				//没有图片附件则返回一条数据
		        			result[1] = "2"; //2-纯文
	    					result[5] = obj[4]; //文字内容
	        				result[6] = ""; //视频文件url
	        				result[7] = ""; //图片
	        			}else{
	        				//易播只含0-普通，3-文件（视频） 类型的信息，其他情况暂不考虑
	        			}
	        			//System.out.println("=========result[7]图片：" + result[7].toString());
	        			yiboChannelList.add(result);
	        		}
	        	}// for 循环信息列表
	        }// if 信息list不为空
		} catch (Exception e) {
			System.out.println("========获取易播频道信息列表接口方法异常:" + e.getMessage());
			e.printStackTrace();
		}
		
        return yiboChannelList;
	}
	
	/**
	 *取所有能新增的且不走流程的栏目
	 * @param userId String
	 * @param orgId String
	 * @param orgIdString String
	 * @param domainId String
	 * @param searchChannelName String 
	 * @return List
	 */
	public List getCanIssueChannel_noProcess(String userId, String orgId,
			String orgIdString, String domainId, String searchChannelName) {
		List resultList = new ArrayList();
		NewChannelBD newChannelBD = new NewChannelBD();
		ManagerBD managerBD = new ManagerBD();
		String where = managerBD.getScopeFinalWhere(userId, orgId, orgIdString,
				"aaa.channelIssuer", "aaa.channelIssuerOrg",
				"aaa.channelIssuerGroup");
		// 兼职组织
		NewInformationBD newInformationBD = new NewInformationBD();
		String sideLineOrg = newInformationBD.getSidelineOrgId(userId);
		if (sideLineOrg != null && !sideLineOrg.equals("")
				&& sideLineOrg.length() > 2) {
			sideLineOrg = sideLineOrg.substring(1, sideLineOrg.length() - 1);
			sideLineOrg.replaceAll("\\*", ",");
			sideLineOrg.replaceAll(",,", ",");
			String[] sarr = sideLineOrg.split(",");
			if (sarr != null && sarr.length > 0) {
				for (int i = 0; i < sarr.length; i++) {
					where += " or aaa.channelIssuerOrg like '%*" + sarr[i]
							+ "*%' ";
				}
			}
		}
		if (searchChannelName != null && !searchChannelName.equals("")
				&& !searchChannelName.equals("null")) {
			searchChannelName = searchChannelName;
		}else{
		    searchChannelName = "";
		}
		try {
			resultList = newChannelBD.getCanIssue_noProcess(where, domainId, searchChannelName);
		} catch (Exception e) {
			System.out.print("接口异常[getCanIssueChannel_noProcess]:" + e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}
	
	/**
     *取所有能新增的栏目信息（包含是否走流程审核标识）
     * @param userId String
     * @param orgId String
     * @param orgIdString String
     * @param domainId String
     * @param searchChannelName String
     * @return List
     */
    public List getCanIssueChannel_All(String userId, String orgId,
            String orgIdString, String domainId, String searchChannelName) {
        List resultList = new ArrayList();
        NewChannelBD newChannelBD = new NewChannelBD();
        ManagerBD managerBD = new ManagerBD();
        String where = managerBD.getScopeFinalWhere(userId, orgId, orgIdString,
                "aaa.channelIssuer", "aaa.channelIssuerOrg",
                "aaa.channelIssuerGroup");
        // 兼职组织
        NewInformationBD newInformationBD = new NewInformationBD();
        String sideLineOrg = newInformationBD.getSidelineOrgId(userId);
        if (sideLineOrg != null && !sideLineOrg.equals("")
                && sideLineOrg.length() > 2) {
            sideLineOrg = sideLineOrg.substring(1, sideLineOrg.length() - 1);
            sideLineOrg.replaceAll("\\*", ",");
            sideLineOrg.replaceAll(",,", ",");
            String[] sarr = sideLineOrg.split(",");
            if (sarr != null && sarr.length > 0) {
                for (int i = 0; i < sarr.length; i++) {
                    where += " or aaa.channelIssuerOrg like '%*" + sarr[i]
                            + "*%' ";
                }
            }
        }
        if (searchChannelName != null && !searchChannelName.equals("")
                && !searchChannelName.equals("null")) {
            searchChannelName = searchChannelName;
        }else{
            searchChannelName = "";
        }
        try {
            resultList = newChannelBD.getCanIssue_All(where, domainId, searchChannelName);
        } catch (Exception e) {
            System.out.print("接口异常[getCanIssueChannel_All]:" + e.getMessage());
            e.printStackTrace();
        }
        return resultList;
    }
    
    //20161017 -by jqq 信息门户列表查询
    public List getPortalInfoList(Map map) {
        List resultList = new ArrayList();
        InformationBD informationBD = new InformationBD();
		ManagerBD managerBD = new ManagerBD();
		ChannelBD channelBD = new ChannelBD();
        //获取传递的参数准备
        String domainId = map.get("domainId").toString();
        String userId = map.get("userId").toString();
        String orgId = map.get("orgId").toString();
        String orgIdString = map.get("orgIdString").toString();
        String channelType = map.get("channelType").toString();
        String userDefine = map.get("userDefine").toString();
        String searchChannelId = map.get("searchChannelId").toString();
        String pageSize = map.get("pageSize").toString();
        String pager_offset = map.get("pager_offset").toString();
        
        String ismobileTop= map.get("ezmobileType")==null?"0":map.get("ezmobileType").toString();
        //20170104 -by jqq 增加标题模糊查询
        String title = map.get("title")==null ? "" : map.get("title").toString();
        
        java.util.Date now = new java.util.Date();
		String nowString = now.toLocaleString();
		nowString = nowString.substring(0, nowString.indexOf(" "));
		//查询的信息字段
		String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, "
			+ " aaa.informationIssuer, aaa.informationVersion, "
			+ " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead,"
			+ " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId,"
			+ " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite,"
			+ " aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg,aaa.informationIsCommend,bbb.channelType";

		String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa "
				+ " join aaa.informationChannel bbb ";
		String whereSQL = "";
		String databaseType = com.whir.common.config.SystemCommon
				.getDatabaseType();
		
		if (databaseType.indexOf("mysql") >= 0) {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or '"
					+ nowString
					+ "' between aaa.validBeginTime and aaa.validEndTime ) ";
		} else {
			whereSQL = " where aaa.domainId="
					+ domainId
					+ " and aaa.informationStatus=0 and ( aaa.informationValidType = 0 or EZOFFICE.FN_STRTODATE('"
					+ nowString
					+ "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
		}
		
		//加入移动头条过滤
		if(ismobileTop.equals("1")){
			whereSQL += " and  aaa.mobileTop=1 ";
			//移动头条 只取四条 
			pageSize="4";
		}
		logger.debug("========[InformationService] title:" + title);
		if(!"".equals(title)){
		    whereSQL += " and  aaa.informationTitle like '%"+ title +"%' ";
		}
		
		logger.debug("========[InformationService] searchChannelId:" + searchChannelId);
		if(searchChannelId==null || "".equals(searchChannelId)){
			//1.接口传入栏目配置为空，分为单位主页、信息管理栏目的组合情况查询
			if("0".equals(channelType)){
			    //信息管理
				whereSQL += "and ( bbb.channelType = 0 and bbb.userDefine = 0 ) ";
			}else if("1".equals(channelType)){
				//单位主页
			    whereSQL += "and ( bbb.channelType > 0 and bbb.userDefine = 0 ) ";
			}else{
				//信息+单位主页
				whereSQL += "and bbb.userDefine != 1 ";
			}
		}else{
			//2.接口传入栏目配置不为空，查询栏目下所属的信息，可能传入多个栏目Id:111,222,333
			String[] channelIdArr = searchChannelId.split(",");
			if(channelIdArr!=null && channelIdArr.length>0){
				whereSQL += " and bbb.channelId in (" + searchChannelId + ") ";
				/*for(int k=0;k<channelIdArr.length;k++){
					String channelIdtmp = channelIdArr[k];
					InformationChannelPO po = channelBD.loadChannel(channelIdtmp);
		        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
		        		whereSQL = whereSQL + channelBD.getChannelById(channelIdtmp);
		        	}else{//不包含子栏目则只查该栏目中的信息
		        		whereSQL = whereSQL + " and (bbb.channelId = " + channelIdtmp + " or aaa.otherChannel like '%," + channelIdtmp + ",%') ";
		        	}
				}*/
				//过滤掉没有查看权限的自定义信息频道
		        String noReadStr = "-1,";
		        try {
		            NewChannelBD newChannelBD = new NewChannelBD();
		            List noreadUserChanList = newChannelBD.getNoReadUserChannels(domainId, userId, orgId, orgIdString);
		            if(noreadUserChanList!=null && noreadUserChanList.size() > 0){
		                for(int xx=0; xx < noreadUserChanList.size(); xx++){
		                    noReadStr += noreadUserChanList.get(xx).toString() + ",";
		                }
		            }
		            if(noReadStr.lastIndexOf(",") > -1){
	                    noReadStr = noReadStr.substring(0, noReadStr.length() - 1);
	                }
		        } catch (Exception e) {
		            logger.debug("========过滤掉没有查看权限的自定义信息频道异常:"+e.getMessage());
		            e.printStackTrace();
		        }
		        //自定义信息频道id对应栏目表中channelType字段
		        whereSQL += " and bbb.channelType not in (" + noReadStr + ") ";
			}else{
				//传入参数有误则返回空记录
				whereSQL += " and 1=2 ";
			}
		}
		//正常状态的栏目
		whereSQL += " and ( bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0' ) ";
		//可维护的所有栏目
        String canReadAllInfoChannel = informationBD.getAllInfoChannel(userId, orgId, channelType, userDefine);
        //栏目可维护人
        String scopeWhere2 = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, "bbb.channelManager","bbb.channelManagerOrg", "bbb.channelManagerGroup");
        String whereSQL1 = informationBD.getUserViewInfo(userId,orgId);
		//信息可查看人
		String ttemp = " and (" + whereSQL1 + " aaa.informationReader like '%"+ userId +"%' " +
        "or aaa.informationReaderName is null or aaa.informationReaderName = '' ) " ;
		PortletBD portletBD = new PortletBD();
		String canViewChannel = portletBD.getInformationCategoryWhereSQL(userId, orgId, orgIdString, domainId);
		//栏目可查看人
		canViewChannel = canViewChannel.replaceAll("aaa.", "bbb.");
		//20160316 -by jqq 栏目下信息列表查询改造（查看的权限范围）
		whereSQL += " and (bbb.channelId in (" + canReadAllInfoChannel + ") or (" + scopeWhere2 + ") "
        + "or (" + canViewChannel + ttemp + ") or aaa.informationIssuerId=" + userId + ") ";
		//排序：排序码 > 发布时间
		whereSQL = whereSQL + " order by aaa.orderCode desc, aaa.informationIssueTime desc " ;
 
		logger.debug("========whereSQL:"+whereSQL);
		List resultlist = new ArrayList();
		resultlist = infoPicList(pager_offset, viewSQL, fromSQL, whereSQL, pageSize);
		return resultlist;
    }
    //20161020 -by jqq 信息portal接口查询
    private List infoPicList(String pager_offset, String viewSQL, String fromSQL,
			String whereSQL, String pagesize) { 
   
		List resultList = new ArrayList();

		int pageSize = Constants.MOBILE_DEFAULT_PAGE_SIZE;
		if(pagesize!=null && !"".equals(pagesize)){
			pageSize = Integer.valueOf(pagesize);
		}
		int offset = 0;
		if (pager_offset != null && !pager_offset.equals("")
				&& !pager_offset.equals("null")) {
			offset = Integer.parseInt(pager_offset);
		}
		int currentPage = (offset / pageSize) + 1;
		Page page = new Page(viewSQL, fromSQL, whereSQL);
		page.setPageSize(pageSize);
		page.setcurrentPage(currentPage);
		List list = page.getResultList();
		String recordCount = String.valueOf(page.getRecordCount());
		logger.debug("========Portal Info List Query Over========");
		List list0 = new ArrayList();
		List list1 = new ArrayList();
		if (list != null && list.size() > 0) {
			//循环每条信息，获取对应附件中图片数据
			for (int jj = 0; jj < list.size(); jj++) {
				//每次循环初始化tmpPicInfoList，存放infoList & picList
				List tmpPicInfoList = new ArrayList();
				
				Object[] oobj = (Object[]) list.get(jj);
				List infoList = new ArrayList();
				infoList = java.util.Arrays.asList(oobj);
				//放入当前这条信息List
				tmpPicInfoList.add(infoList);
				//获取信息图片数据
				InformationAccessoryBD informationAccessoryBD = new InformationAccessoryBD();
				String infoId = oobj[0].toString();
				//查询信息附件
				List accclist = informationAccessoryBD.getAccessory(infoId);
				logger.debug("========Portal InfoPics[" + jj +"] List Query Over========");
				Object[] tmp = null;
				List picList = new ArrayList();
				if (accclist != null) {
					logger.debug("========Portal InfoPics[" + jj +"] Accessory Is Not Null========");
					for (int i = 0; i < accclist.size(); i++) {
						tmp = (Object[]) accclist.get(i);
						//判断是否是上传的图片
						if (tmp[4].toString().equals("1")) {
							logger.debug("========Portal InfoPics[" + jj +"] Pic Is Not Null========");
							picList.add(accclist.get(i));
						}
					}
				}
				//放入当前信息图片结果List
				tmpPicInfoList.add(picList);
				list0.add(tmpPicInfoList);
			}
		}

		list1.add(pager_offset);
		list1.add(recordCount);

		resultList.add(list0); //信息与图片集合 
		
		resultList.add(list1); //其他查询结果数据
		return resultList;

	}
    
    //20161220 -by jqq 移动门户信息接口参数改造
    public String[] getInfoInputField(){
        String[] result = new String[3];
        String searchChannelId ="";
        String userDefine = "";
        String channelType = "";
        //是否选择具体栏目标识
        boolean selectChannelFlag = false;
        //是否信息管理标识
        boolean infoFlag = false;
        //是否单位主页标识
        boolean departFlag = false;
        //查询选择了具体的栏目信息allChannelIds = '1'
        String sql = "select channelId, channelName, allChannelIds from ezmobile_infoDataSource where allChannelIds = '1' ";
        CommentBD bd = new CommentBD();
        List list = bd.getDataBySQL(sql);
        if(list!=null && list.size()>0){
            selectChannelFlag = true;
            userDefine = "0";
            channelType = "-1";
            //获取所有栏目id，使用逗号隔开
            String selectChannelId = "";
            for(int i=0; i<list.size(); i++){
                Object[] obj = (Object[]) list.get(i);
                if(obj[2]!=null && "1".equals( obj[2].toString())){
                    selectChannelId = (obj[0]!=null && !"".equals(obj[0].toString()) && !"null".equals(obj[0].toString())) ? obj[0].toString() : "";
                    searchChannelId += selectChannelId + ",";
                }
            }
            if(searchChannelId.length() > 0 && searchChannelId.endsWith(",")){
                searchChannelId = searchChannelId.substring(0, searchChannelId.length() - 1);
            }
            result[0] = searchChannelId;
            result[1] = userDefine;
            result[2] = channelType;
        }else{
            //查询没有具体的栏目信息allChannelIds != '1'
            String sql2 = "select channelId, channelName, allChannelIds from ezmobile_infoDataSource where allChannelIds != '1' ";
            List list2 = bd.getDataBySQL(sql2);
            //没有选择具体栏目，信息与单位主页的情况要分别判断
            for(int i=0; i<list2.size(); i++){
                Object[] obj = (Object[]) list2.get(i);
                if(obj[1]!=null && "信息管理".equals( obj[1].toString())){
                    if(obj[2]!=null && "infomanage".equals( obj[2].toString())){
                        infoFlag = true;
                    }
                }else if(obj[1]!=null && "单位主页".equals( obj[1].toString())){
                    if(obj[2]!=null && "unit".equals( obj[2].toString())){
                        departFlag = true;
                    }
                }
            }
            //根据信息标识infoFlag 和 单位主页标识departFlag 返回参数
            if(infoFlag && !departFlag){
                //仅信息管理的情况
                userDefine = "0";
                channelType = "0";
                searchChannelId = "";
            }else if(!infoFlag && departFlag){
                //仅单位主页的情况
                userDefine = "0";
                channelType = "1";
                searchChannelId = "";
            }else {
                //信息 管理&单位主页的情况
                userDefine = "0";
                channelType = "-1";
                searchChannelId = "";
            }
            result[0] = searchChannelId;
            result[1] = userDefine;
            result[2] = channelType;
        }
        return result;
    }
}
