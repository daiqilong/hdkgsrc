package com.whir.ezoffice.information.infomanager.actionsupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.ezlucene.webClient.LuceneClient;
import com.whir.ezoffice.dossier.bd.DossierBD;
import com.whir.ezoffice.information.channelmanager.bd.ChannelBD;
import com.whir.ezoffice.information.channelmanager.ejb.ChannelEJBBean;
import com.whir.ezoffice.information.channelmanager.po.InformationChannelPO;
import com.whir.ezoffice.information.common.util.InfoUtils;
import com.whir.ezoffice.information.infomanager.bd.InformationBD;
import com.whir.org.bd.groupmanager.GroupBD;
import com.whir.org.manager.bd.ManagerBD;

public class InfoListAction extends BaseActionSupport {

	/**
	 * 信息列表类
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoListAction.class.getName());
	private String type;
	private String channelType;
	private String userDefine;
	private String userChannelName;
	private String searchChannel;
	private String title;
	private String subtitle;
	private String key;
	private String searchIssuerName;
	private String searchOrgId;
	private String searchOrgName;
	private String append;
	private String startDate;
	private String endDate;
	private String retrievalKey;
	private String channelId;
	private String dataType;//相关性中区分 信息管理 单位主页 自定义频道的字段
	private String userId;
	private String from;//在人事管理中点击 信息
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getUserDefine() {
		return userDefine;
	}
	public void setUserDefine(String userDefine) {
		this.userDefine = userDefine;
	}
	public String getUserChannelName() {
		return userChannelName;
	}
	public void setUserChannelName(String userChannelName) {
		this.userChannelName = userChannelName;
	}
	public String getSearchChannel() {
		return searchChannel;
	}
	public void setSearchChannel(String searchChannel) {
		this.searchChannel = searchChannel;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSearchIssuerName() {
		return searchIssuerName;
	}
	public void setSearchIssuerName(String searchIssuerName) {
		this.searchIssuerName = searchIssuerName;
	}
	public String getSearchOrgId() {
		return searchOrgId;
	}
	public void setSearchOrgId(String searchOrgId) {
		this.searchOrgId = searchOrgId;
	}
	public String getSearchOrgName() {
		return searchOrgName;
	}
	public void setSearchOrgName(String searchOrgName) {
		this.searchOrgName = searchOrgName;
	}
	public String getAppend() {
		return append;
	}
	public void setAppend(String append) {
		this.append = append;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getRetrievalKey() {
		return retrievalKey;
	}
	public void setRetrievalKey(String retrievalKey) {
		this.retrievalKey = retrievalKey;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	/**
	 * 所有信息 详细页签 列表页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String allList(){
		String onlyRetrievalAll = request.getParameter("onlyRetrievalAll");
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString(); 
		String domainId = CommonUtils.getSessionDomainId(request).toString();
        /* 判断当前用户是否有信息维护－排序的权限*/
        boolean allRight = false;//全部权限
        boolean hasRight = false;//维护权限
        String rightCode ="";
        /*if (request.getParameter("checkdepart") != null && "1".equals(request.getParameter("checkdepart"))) {
            rightCode = "01*01*02"; //单位主页维护权限
            List rightScopeList = new com.whir.org.manager.bd.ManagerBD().getRightScope(userId, rightCode);
	        if (rightScopeList!=null && rightScopeList.size()>0 && rightScopeList.get(0)!=null) {
	        	Object[] obj = (Object[]) rightScopeList.get(0);
	            if ("0".equals(obj[0].toString())) {
	                hasRight = true;
	            }
	        }
        }else{*/
        rightCode = "01*03*03";//信息管理维护权限
        List rightScopeList = new com.whir.org.manager.bd.ManagerBD().getRightScope(userId, rightCode);
        if (rightScopeList!=null && rightScopeList.size()>0 && rightScopeList.get(0)!=null) {
        	hasRight = true;
            Object[] obj = (Object[]) rightScopeList.get(0);
            if ("0".equals(obj[0].toString())) {
            	allRight = true;
            }
        }
        //}
        //相关性中打开信息列表
        if(request.getParameter("relationModule") != null && "information".equals(request.getParameter("relationModule"))){
        	request.setAttribute("relationModule","information");
        }
        request.setAttribute("hasRight",Boolean.valueOf(hasRight));
        request.setAttribute("allRight",Boolean.valueOf(allRight));
        /* 判断当前用户是否有信息维护－排序的权限*/
        ChannelEJBBean channelBean = new ChannelEJBBean();
        /*判断某一用户是否有某一频道的维护权限*/
        if(channelId!=null && !channelId.equals("")){
        	boolean channelManager = false;
			try {
				channelManager = channelBean.canModifyChannel(userId, orgId, channelId);
			} catch (Exception e) {
				//logger.debug("========[allList]判断用户是否有栏目下信息的维护权限异常：" + e.getMessage());
				e.printStackTrace();
			}
        	request.setAttribute("channelManager", channelManager);
        }
        /*判断某一用户是否有某一频道的维护权限*/
        request.setAttribute("onlyRetrievalAll", onlyRetrievalAll);
        if(dataType!=null && !"".equals(dataType)){
        	request.setAttribute("dataType", dataType);
        }
        
        //2013-09-10-----判断档案归档是否有归档模块-----start
        boolean dossierGD =false;
        DossierBD dossierBD = new DossierBD();
		List dossierGDlist = dossierBD.selectArchivesPigeonholeSet(String.valueOf(domainId));
		if(dossierGDlist !=null && dossierGDlist.size()>0){
			Object[] obj=(Object[])dossierGDlist.get(0);
			String pigeonholeSetType="";
		    if(obj[1] !=null){
		    	pigeonholeSetType = obj[1].toString();
		    }
		    if(pigeonholeSetType.indexOf("ZSGL,")>=0){
		    	dossierGD =true;
		    }
		}
		request.setAttribute("dossierGD", dossierGD);
        //2013-09-10-----判断档案归档是否有归档模块-----end
		
		//2013-09-13-----判断是否有信息管理维护权限-----start
		boolean weihuInformation =false;
		if(this.judgeCallRight("", "01*03*03")){
			weihuInformation =true;
		}
		request.setAttribute("weihuInformation", String.valueOf(weihuInformation));
		//2013-09-13-----判断是否有信息管理维护权限-----end
        return "allList";
	}
	
	/**
	 * 取列表页查询下拉框中的 信息栏目 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String channelList(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString(); 
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		ChannelBD channelBD = new ChannelBD();
		/* 取用户可以查看的所有栏目,并返回到页面供查询时取栏目ID用 */
        List list = channelBD.getUserViewCh(userId, orgId, channelType, userDefine, domainId);
        request.setAttribute("channelList", InfoUtils.dealChannelName(list, 2));
        /* 取用户可以查看的所有栏目,并返回到页面供查询时取栏目ID用 */
		String json = "[";
		for(int i=0;i<list.size();i++){
			json = json+"{";
			Object[] obj = (Object[])list.get(i);
			json += "\"channelId\":\""+ obj[0]+"\",\"channelName\":\""+obj[1].toString()+"\"";
			if(i!=list.size()-1){
				json += "},";
			}else{
				json += "}";
			}
		}
		json += "]";
		printJsonResult(json);
		return null;
	}
	
	/**
	 * 所有信息 列表页签 列表页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String allInfo(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString(); 
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		ChannelBD channelBD = new ChannelBD();
		/* 取用户可以查看的所有栏目,并返回到页面供查询时取栏目ID用 */
        List list = channelBD.getUserViewCh(userId, orgId, channelType, userDefine, domainId);
        request.setAttribute("channelList", InfoUtils.dealChannelName(list, 2));
        /* 取用户可以查看的所有栏目,并返回到页面供查询时取栏目ID用 */
        String listType = request.getParameter("listType");
        request.setAttribute("listType", listType);
		return "allInfo";
	}
	
	/**
	 * 信息 缩略图页签 列表页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String allThumb(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString(); 
		String domainId = CommonUtils.getSessionDomainId(request).toString();
        /* 判断当前用户是否有信息维护－排序的权限（信息维护的权限并且权限范围为全部）*/
        boolean hasRight = false;
        String rightCode = "01*03*03";//信息管理维护权限
        if (request.getParameter("checkdepart") != null && "1".equals(request.getParameter("checkdepart"))) {
            rightCode = "01*01*02"; //单位主页维护权限
        }
        List rightScopeList = new com.whir.org.manager.bd.ManagerBD().getRightScope(userId, rightCode);
        if (rightScopeList!=null && rightScopeList.size()>0 && rightScopeList.get(0)!=null) {
            Object[] obj = (Object[]) rightScopeList.get(0);
            if ("0".equals(obj[0].toString())) {
                hasRight = true;
            }
        }
        request.setAttribute("hasRight",Boolean.valueOf(hasRight));
        /* 判断当前用户是否有信息维护－排序的权限（信息维护的权限并且权限范围为全部）*/
		ChannelBD channelBD = new ChannelBD();
		/* 取用户可以查看的所有栏目,并返回到页面供查询时取栏目ID用 */
        List list = channelBD.getUserViewCh(userId, orgId, channelType, userDefine, domainId);
        request.setAttribute("channelList", InfoUtils.dealChannelName(list, 2));
        /* 取用户可以查看的所有栏目,并返回到页面供查询时取栏目ID用 */
		return "allThumb";
	}
	
	/**
	 * 所有信息 详细页签 列表数据
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String list() throws ParseException{
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString(); //用户组织
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        ChannelBD channelBD = new ChannelBD();
        InformationBD informationBD = new InformationBD();
        ManagerBD managerBD = new ManagerBD();
        /* 判断当前用户是否有信息维护－排序的权限*/
        boolean hasRight = false;
        String rightCode = "01*03*03";//信息管理维护权限
        List rightScopeList = new com.whir.org.manager.bd.ManagerBD().getRightScope(userId, rightCode);
        if (rightScopeList!=null && rightScopeList.size()>0 && rightScopeList.get(0)!=null) {
            Object[] obj = (Object[]) rightScopeList.get(0);
            if ("0".equals(obj[0].toString())) {
                hasRight = true;
            }
        }
        /* 判断当前用户是否有信息维护－排序的权限*/
        String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, aaa.informationIssuer, " +
            " aaa.informationVersion, aaa.informationIssueTime, aaa.informationSummary, aaa.informationHead," +
            " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId, aaa.titleColor," +
            " aaa.isConf, aaa.documentNo, aaa.transmitToEzsite, aaa.informationModifyTime, aaa.orderCode," +
            " aaa.informationIssueOrg, aaa.informationIsCommend, bbb.channelType, aaa.informationIssuerId," +
            " bbb.channelNeedCheckup,aaa.dossierStatus,bbb.channelNeedCheckupForModi,aaa.informationValidType,"+
            " aaa.validBeginTime,aaa.validEndTime,bbb.newInfoDeadLine,aaa.informationIssuerId,aaa.informationIssueOrgId ";
        if(request.getParameter("retrievalAction")!=null && request.getParameter("retrievalAction").toString().equals("1")){
            viewSQL+=", aaa.informationContent ";
        }
        String relation =request.getParameter("relation")==null?"":request.getParameter("relation");
        String fromSQL = "";
        if("1".equals(relation)){
        	fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb , com.whir.ezoffice.relation.po.RelationDataPO rrr ";
        }else{
        	fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb ";
        }
        String whereSQL = "";
        String mysqlnowString = new SimpleDateFormat("yyyy-MM-dd ").format(Calendar.getInstance().getTime());
        //2013-09-03-----start
        if(CommonUtils.isEmpty(channelType)){
        	channelType ="0";
        }
        if(CommonUtils.isEmpty(userDefine)){
        	userDefine ="0";
        }
        //2013-09-03-----end
        whereSQL = " where aaa.domainId=" + domainId + " and aaa.informationStatus=0 and (bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0')";
        
        /*if ("-1".equals(channelType)) {//单位主页所有信息
            whereSQL += " and bbb.channelType >0 and bbb.userDefine=0 ";
        } else {//信息管理或自定义信息频道
            whereSQL += " and bbb.channelType=" + channelType;
        }
        if("1".equals(userDefine)){
        	whereSQL += " and bbb.channelType=" + channelType;
        }*/
        Map varMap = new HashMap();
        if(channelId!=null && !"".equals(channelId)){
        	//选择栏目查询或者点击具体栏目信息列表
            InformationChannelPO po = channelBD.loadChannel(channelId);
        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
        		whereSQL = whereSQL + channelBD.getChannelById(channelId);
        	}else{//不包含子栏目则只查该栏目中的信息
        		whereSQL = whereSQL + " and (bbb.channelId = :channelId or aaa.otherChannel like :channelId2) ";
        		varMap.put("channelId", channelId);
        		varMap.put("channelId2", "%,"+channelId+",%");
        	}
        } 
        /*没有信息维护的权限并且权限范围为全部的用户看不到过期的信息*/
        if(!hasRight){
        	whereSQL += " and ( aaa.informationValidType = 0 or EZOFFICE.FN_STRTODATE('" + mysqlnowString + "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
        }
        /*没有信息维护的权限并且权限范围为全部的用户看不到过期的信息*/
        
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
        //logger.debug("========rightWhere:" + rightWhere);
        whereSQL += " and ((bbb.channelId in (" + canReadAllInfoChannel + ") or (" + scopeWhere + ") " 
        		+ "or (" + rightWhere + ") or ((" + viewscopeWhere+ " or bbb.channelReaderName is null or bbb.channelReaderName='') "
        		+ ttemp + ") or aaa.informationIssuerId=" + userId + ") ";
        String hSql = "";
        hSql = informationBD.getUserViewCh(userId, orgId, channelType, userDefine);
        
        List hsqlList = informationBD.getUserViewCh2(userId, orgId, channelType, userDefine);
        String whereTmp = "";
        if (request.getParameter("checkdepart") == null || request.getParameter("checkdepart").equals("") 
        		|| request.getParameter("checkdepart").equals("null")) {
            //if (!hasRight) {
                if (hsqlList != null && hsqlList.size() != 0) {
                    whereTmp = whereTmp + " and (( bbb.channelId in (" + hSql + ") ";
                    if(!channelType.equals("-1")){
	            		whereTmp += " and bbb.channelType = " + channelType;
	            	}
                    whereTmp += ")";
                    //取单个栏目下的信息时，取同时发布到该栏目下的信息
                    if(channelId != null && !"".equals(channelId)){
	                    if (hsqlList.size() > 1000) {
	                        whereTmp += " or ( aaa.otherChannel in('-1', ";
	                        for (int i = 0; i < hsqlList.size(); i++) {
	                            if (i % 998 == 1) {
	                                whereTmp += "'-1') or aaa.otherChannel in( '-1','," + hsqlList.get(i) + ",',";
	                            } else {
	                                whereTmp += "',"+hsqlList.get(i)+",'" + ",";
	                            }
	                        }
	                        whereTmp += "'-1'))";
	                    } else {
	                        whereTmp += " or aaa.otherChannel in (";
	                        for (int i = 0; i < hsqlList.size() - 1; i++) {
	                            whereTmp += "'," + hsqlList.get(i) + ",',";
	                        }
	                        whereTmp += "'," + hsqlList.get(hsqlList.size() - 1) + ",')";
	                    }
                    }
                    whereTmp += ")";
                } else {
                    whereTmp = whereTmp + " and bbb.channelId in (" + hSql + ") ";
                }

                /*if (readerWhere != null && !readerWhere.equals("")) {
                    whereTmp = whereTmp + " and (" + readerWhere + ") ";
                }*/
            //} else {
            	//if(!channelType.equals("-1")){
            	//	whereTmp += " and bbb.channelType = " + channelType;
            	//}
            //}
        } else {
            if (hsqlList != null && hsqlList.size() != 0) {
                whereTmp = whereTmp + " and (( bbb.channelId in (" + hSql + ") ";
                if(!channelType.equals("-1")){
                    whereTmp += " and bbb.channelType = " + channelType;
                }
                whereTmp += ")"; 
                //取单个栏目下的信息时，取同时发布到该栏目下的信息
                if(channelId != null && !"".equals(channelId)){
	                if (hsqlList.size() > 1000) {
	                    whereTmp += " or ( aaa.otherChannel in('-1', ";
	                    for (int i = 0; i < hsqlList.size(); i++) {
	                        if (i % 998 == 1) {
	                            whereTmp += "'-1') or aaa.otherChannel in( '-1','," + hsqlList.get(i) + ",',";
	                        } else {
	                            whereTmp += "',"+hsqlList.get(i) + ",',";
	                        }
	                    }
	                    whereTmp += "'-1'))";
	                } else {
	                    whereTmp += " or aaa.otherChannel in (";
	                    for (int i = 0; i < hsqlList.size() - 1; i++) {
	                        whereTmp += "'," + hsqlList.get(i) + ",',";
	                    }
	                    whereTmp += "'," + hsqlList.get(hsqlList.size() - 1) + ",')";
	                }
                }
                whereTmp += ")";
            } else {
                whereTmp = whereTmp + " and bbb.channelId in (" + hSql + ") ";
                if(!channelType.equals("-1")){
                    whereTmp += " and bbb.channelType = " + channelType;
                }
            }

            /*if (!hasRight) {
                if (readerWhere != null && !readerWhere.equals("")) {
                    whereTmp = whereTmp + " and (" + readerWhere + ") ";
                }
            }*/
        }
        whereSQL += whereTmp + ")";
    
        //判断是否精华信息
        if (request.getParameter("type").equals("good")) {
            whereSQL += " and aaa.informationIsCommend=1 ";
        }
        if (searchChannel!=null&&!searchChannel.equals("0")&&!searchChannel.equals("")) {
        	//选择栏目查询
            InformationChannelPO po = channelBD.loadChannel(searchChannel);
        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
        		whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
        	}else{//不包含子栏目则只查该栏目中的信息
        		whereSQL = whereSQL + " and (bbb.channelId = :searchChannel or aaa.otherChannel like :searchChannel2) ";
        		varMap.put("searchChannel", searchChannel);
        		varMap.put("searchChannel2", "%,"+searchChannel+",%");
        	}
        }
        if (title != null && !title.equals("")) {
            //标题
            whereSQL = whereSQL + " and aaa.informationTitle like :title " ;
            varMap.put("title", "%"+title+"%");
        }
        if (key != null && !key.equals("")) {
            //关键字
            whereSQL = whereSQL + " and aaa.informationKey like :key "; 
            varMap.put("key", "%"+key+"%");
        }
        if (subtitle != null && !subtitle.equals("")) {
            //副标题
            whereSQL = whereSQL + " and aaa.informationSubTitle like :subtitle "; 
            varMap.put("subtitle", "%"+subtitle+"%");
        }
        if (searchIssuerName!=null && !searchIssuerName.equals("")) {
        	//发布人
            whereSQL = whereSQL + " and aaa.informationIssuer like :searchIssuerName " ;
            varMap.put("searchIssuerName", "%"+searchIssuerName+"%");
        }
        if (searchOrgName != null && !searchOrgName.equals("")) {
            //部门
            whereSQL += " and aaa.issueOrgIdString like :searchOrgId "; 
            varMap.put("searchOrgId", "%$"+searchOrgId+"$%");
        }
        if (append != null && !append.equals("")) {
            //附件名
            whereSQL = whereSQL + " and ( select count(*) from " +
                       " com.whir.ezoffice.information.infomanager.po.InformationAccessoryPO ccc " +
                       " where ccc.accessoryName like :append " +
                       " and ccc.information.informationId = aaa.informationId) > 0 ";
            varMap.put("append", "%"+append+"%");
        }
        if(startDate != null && !startDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime >= :startDate";
        	varMap.put("startDate", sdf.parse(startDate+" 00:00:00"));
        }
        if(endDate != null && !endDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime <= :endDate";
        	varMap.put("endDate", sdf.parse(endDate+" 23:59:59"));
        }
        if(retrievalKey!=null && !retrievalKey.equals("")){
        	retrieval_new();
        }
        String orderSQL = " order by aaa.orderCode desc, case when aaa.informationModifyTime is null then aaa.informationIssueTime else aaa.informationModifyTime end desc " ;
        String orderByFieldName = request.getParameter("orderByFieldName") != null ? request.getParameter("orderByFieldName") : "";
        String orderByType = request.getParameter("orderByType") != null ? request.getParameter("orderByType") : "";
		if(orderByFieldName!=null && !"".equals(orderByFieldName)){
			orderSQL = " order by aaa."+orderByFieldName+" "+orderByType;
        }
//		String listType = request.getParameter("listType");
		int pageSize = CommonUtils.getUserPageSize(request);
//		if(listType!=null && listType.equals("1")){
//			pageSize = 30;
//		}
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        //2013-09-03-----start
        if("1".equals(relation)){
        	String informationId  = request.getParameter("informationId");
    		String moduleType     = request.getParameter("moduleType");
    		String relationModule = request.getParameter("relationModule");
        	whereSQL = " where rrr.moduleType= '"+moduleType+"' and rrr.infoId="+informationId+" and aaa.informationId=rrr.relationInfoId and rrr.relationObjectType='"+relationModule+"' "; 
        }
        //2013-09-03-----end
        logger.debug("信息列表数据SQL:\n"+viewSQL+fromSQL+whereSQL+orderSQL);
        Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, orderSQL);
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        page.setVarMap(varMap);
        List reslutList = page.getResultList();
        List result = InfoUtils.allInfoList(reslutList, rightScopeList, userId, orgId);
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL+",infoManager", result);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		logger.debug("信息列表数据:\n"+json);
		printResult(G_SUCCESS,json);
		logger.debug("查询信息列表结束");
		return null;
	}
	
	/**
	 * 草稿箱列表页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String myDraft(){
		HttpSession session = request.getSession(true);
		if(userId==null || "".equals(userId)){
			userId = session.getAttribute("userId").toString();
		}
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		/*取当前用户有新建权限的栏目*/
		List list = InfoUtils.getCanIssueChannel(userId, orgId, orgIdString, domainId);
		request.setAttribute("channelList", InfoUtils.dealChannelName(list, 5));
		/*取当前用户有新建权限的栏目*/
		//2013-09-09-----start
		boolean hasRight = false;
		String rightCode = "01*01*02"; //单位主页维护权限
		if(this.judgeCallRight("", rightCode)){  
			hasRight =true;  
        }
        request.setAttribute("hasRight",Boolean.valueOf(hasRight));
        //2013-09-09-----end
		return "myDraft";
	}
	
	/**
	 * 草稿箱列表数据
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String myDraftList() throws ParseException{
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		HttpSession session = request.getSession(true);
		if(userId==null || "".equals(userId)){
			userId = session.getAttribute("userId").toString();
		}
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		ChannelBD channelBD = new ChannelBD();
		String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, aaa.informationAuthor, " +
				"aaa.informationIssueTime, aaa.informationType, aaa.informationCommonNum, bbb.channelId, bbb.channelName, " +
				"bbb.channelNeedCheckup, aaa.informationModifyTime, aaa.orderCode, bbb.channelNeedCheckupForModi, bbb.deleteCtrl, " +
				"bbb.channelPrivileger, bbb.channelPrivilegerOrg, bbb.channelPrivilegerGroup, bbb.newInfoDeadLine ";
		String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb ";
		String whereSQL = " where informationIssuerId = " + userId + 
                      " and aaa.informationStatus=-1 and aaa.domainId=" + domainId + 
                      " and (aaa.afficeHistoryDate is null or aaa.afficeHistoryDate= -1) " +
                      " and (aaa.informationOrISODoc is null or aaa.informationOrISODoc='0')";
		
		Map varMap = new HashMap();
        if (!searchChannel.equals("0")&&!"".equals(searchChannel)) {
        	//选择栏目查询
            InformationChannelPO po = channelBD.loadChannel(searchChannel);
        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
        		whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
        	}else{//不包含子栏目则只查该栏目中的信息
        		whereSQL = whereSQL + " and (bbb.channelId = :searchChannel or aaa.otherChannel like :searchChannel2) ";
        		varMap.put("searchChannel", searchChannel);
        		varMap.put("searchChannel2", "%,"+searchChannel+",%");
        	}
        }
        if (title != null && !title.equals("")) {
            //标题
            whereSQL = whereSQL + " and aaa.informationTitle like :title " ;
            varMap.put("title", "%"+title+"%");
        }
        if (key != null && !key.equals("")) {
            //关键字
            whereSQL = whereSQL + " and aaa.informationKey like :key "; 
            varMap.put("key", "%"+key+"%");
        }
        if (subtitle != null && !subtitle.equals("")) {
            //副标题
            whereSQL = whereSQL + " and aaa.informationSubTitle like :subtitle "; 
            varMap.put("subtitle", "%"+subtitle+"%");
        }
        if(startDate != null && !startDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime >= :startDate";
        	varMap.put("startDate", sdf.parse(startDate+" 00:00:00"));
        }
        if(endDate != null && !endDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime <= :endDate";
        	varMap.put("endDate", sdf.parse(endDate+" 23:59:59"));
        }
        String orderSQL = " order by aaa.orderCode desc, case when aaa.informationModifyTime is null then aaa.informationIssueTime else aaa.informationModifyTime end desc" ;
		String orderByFieldName = request.getParameter("orderByFieldName") != null
				? request.getParameter("orderByFieldName") : "";
		String orderByType = request.getParameter("orderByType") != null 
				? request.getParameter("orderByType") : "";
		if(orderByFieldName!=null && !"".equals(orderByFieldName)){
			orderSQL = " order by aaa."+orderByFieldName+" "+orderByType;
		}
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, orderSQL);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		page.setVarMap(varMap);
		List reslutList = page.getResultList();
		/*判断用户是否可以删除信息*/
		GroupBD groupBD = new GroupBD();
		String groupId = groupBD.getOwnerGroupIdStrByUserId(userId);
		List list = InfoUtils.myIssueList(reslutList, userId, orgId, orgIdString, groupId);
		/*判断用户是否可以删除信息*/
		int pageCount = page.getPageCount();
		int recordCount = page.getRecordCount();
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL+",canDelete", list);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		logger.debug("草稿箱信息列表数据:\n"+json);
		printResult(G_SUCCESS,json);
		return null;
	}
	
	/**
	 * 我的发布列表页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String myIssue(){
		HttpSession session = request.getSession(true);
		if(userId==null || "".equals(userId)){
			userId = session.getAttribute("userId").toString();
		}
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		/*取当前用户有新建权限的栏目*/
		List list = InfoUtils.getCanIssueChannel(userId, orgId, orgIdString, domainId);
		request.setAttribute("channelList", InfoUtils.dealChannelName(list, 5));
		/*取当前用户有新建权限的栏目*/
		//2013-09-09-----start
		boolean hasRight = false;
		String rightCode = "01*01*02"; //单位主页维护权限
		if(this.judgeCallRight("", rightCode)){  
			hasRight =true;  
        }
        request.setAttribute("hasRight",Boolean.valueOf(hasRight));
        //2013-09-09-----end
		return "myIssue";
	}
	
	/**
	 * 我的发布列表数据
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String myIssueList() throws ParseException{
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		HttpSession session = request.getSession(true);
		if(userId==null || "".equals(userId)){
			userId = session.getAttribute("userId").toString();
		}
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		ChannelBD channelBD = new ChannelBD();
		String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, aaa.informationAuthor, " +
				"aaa.informationIssueTime, aaa.informationType, aaa.informationCommonNum, bbb.channelId, bbb.channelName, " +
				"bbb.channelNeedCheckup, aaa.informationModifyTime, aaa.orderCode, bbb.channelNeedCheckupForModi, bbb.deleteCtrl, " +
				"bbb.channelPrivileger, bbb.channelPrivilegerOrg, bbb.channelPrivilegerGroup, bbb.newInfoDeadLine ";
		String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb ";
		String whereSQL = " where informationIssuerId = " + userId + " and bbb.channelType = " + channelType +
                      " and aaa.informationStatus=0 and aaa.domainId=" + domainId + 
                      " and (aaa.afficeHistoryDate is null or aaa.afficeHistoryDate= -1) " +
                      " and (aaa.informationOrISODoc is null or aaa.informationOrISODoc='0')";
		
		Map varMap = new HashMap();
        if (!searchChannel.equals("0")&&!"".equals(searchChannel)) {
        	//选择栏目查询
            InformationChannelPO po = channelBD.loadChannel(searchChannel);
        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
        		whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
        	}else{//不包含子栏目则只查该栏目中的信息
        		whereSQL = whereSQL + " and (bbb.channelId = :searchChannel or aaa.otherChannel like :searchChannel2) ";
        		varMap.put("searchChannel", searchChannel);
        		varMap.put("searchChannel2", "%,"+searchChannel+",%");
        	}
        }
        if (title != null && !title.equals("")) {
            //标题
            whereSQL = whereSQL + " and aaa.informationTitle like :title " ;
            varMap.put("title", "%"+title+"%");
        }
        if (key != null && !key.equals("")) {
            //关键字
            whereSQL = whereSQL + " and aaa.informationKey like :key "; 
            varMap.put("key", "%"+key+"%");
        }
        if (subtitle != null && !subtitle.equals("")) {
            //副标题
            whereSQL = whereSQL + " and aaa.informationSubTitle like :subtitle "; 
            varMap.put("subtitle", "%"+subtitle+"%");
        }
        if(startDate != null && !startDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime >= :startDate";
        	varMap.put("startDate", sdf.parse(startDate+" 00:00:00"));
        }
        if(endDate != null && !endDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime <= :endDate";
        	varMap.put("endDate", sdf.parse(endDate+" 23:59:59"));
        }
        String orderSQL = " order by aaa.orderCode desc, case when aaa.informationModifyTime is null then aaa.informationIssueTime else aaa.informationModifyTime end desc" ;
		String orderByFieldName = request.getParameter("orderByFieldName") != null
				? request.getParameter("orderByFieldName") : "";
		String orderByType = request.getParameter("orderByType") != null 
				? request.getParameter("orderByType") : "";
		if(orderByFieldName!=null && !"".equals(orderByFieldName)){
			orderSQL = " order by aaa."+orderByFieldName+" "+orderByType;
		}
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, orderSQL);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		page.setVarMap(varMap);
		List reslutList = page.getResultList();
		/*判断用户是否可以删除信息*/
		GroupBD groupBD = new GroupBD();
		String groupId = groupBD.getOwnerGroupIdStrByUserId(userId);
		List list = InfoUtils.myIssueList(reslutList, userId, orgId, orgIdString, groupId);
		/*判断用户是否可以删除信息*/
		int pageCount = page.getPageCount();
		int recordCount = page.getRecordCount();
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL+",canDelete", list);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		logger.debug("我发布的信息列表数据:\n"+json);
		printResult(G_SUCCESS,json);
		return null;
	}
	
	/**
	 * 办理查阅列表页面
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String allDeal(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		ChannelBD channelBD = new ChannelBD();
        //取用户可以查看的所有栏目,并返回到页面供搜索取栏目ID用
		//信息管理栏目
        List list = channelBD.getUserViewCh(userId, orgId, channelType,userDefine, domainId);
        request.setAttribute("channelList", InfoUtils.dealChannelName(list, 2));
        //单位主页栏目
        List list1 = channelBD.getUserViewChDepart(userId, orgId, channelType,userDefine, domainId);
        request.setAttribute("channelListDepart", InfoUtils.dealChannelName(list1, 2));
		return "alldeal";
	}
	
	/**
	 * 办理查阅列表数据
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String dealList() throws ParseException{
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String domainId = CommonUtils.getSessionDomainId(request).toString();
		String viewSQL =
            " aaa.informationId, aaa.informationTitle, aaa.informationKits, aaa.informationIssuer, aaa.informationVersion," +
            " aaa.informationIssueTime, aaa.informationSummary, aaa.informationHead, aaa.informationType, aaa.titleColor," +
            " bbb.channelName, bbb.channelId, aaa.isConf, aaa.informationCommonNum, aaa.documentNo, aaa.informationStatus," +
            " aaa.transmitToEzsite, aaa.informationModifyTime, aaa.orderCode, aaa.informationIssueOrg, aaa.informationIsCommend," +
            " bpm.poolWorkStatus, bpm.poolNowAcitivityNames, bbb.newInfoDeadLine ";
        String fromSQL =
            " com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb ,com.whir.ezoffice.bpm.po.BPMProcessInstancePO bpm ";
        String whereSQL = "";
        String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();
        String mysqlnowString = new SimpleDateFormat("yyyy-MM-dd ").format(Calendar.getInstance().getTime());
        if (databaseType.indexOf("mysql") >= 0) {
            whereSQL = " where aaa.domainId=" + domainId + " and ( aaa.informationValidType = 0 " +
            	"or ('" + mysqlnowString + "' >=aaa.validBeginTime and '" + mysqlnowString + "' <= aaa.validEndTime )) ";
        } else {
            whereSQL = " where aaa.domainId=" + domainId + " and ( aaa.informationValidType = 0 " +
            	"or EZOFFICE.FN_STRTODATE('" + mysqlnowString + "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
        }
        whereSQL += " and (bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0') and bbb.channelNeedCheckup = 1 ";
        		
        if ("-1".equals(channelType)) {//单位主页所有信息
            whereSQL += " and bbb.channelType >0 and bbb.userDefine=0 ";
        } else {//信息管理或自定义信息频道
            whereSQL += " and bbb.channelType=" + channelType;
        }
        //查看范围判断
        String rightWhere = new ManagerBD().getRightFinalWhere(userId, orgId, "01*03*03", 
        		"aaa.informationIssueOrgId", "aaa.informationIssuerId");
        whereSQL += " and (("+rightWhere+") or aaa.informationIssuerId="+userId+") ";
        
        Map varMap = new HashMap();
        ChannelBD channelBD = new ChannelBD();
        if (!searchChannel.equals("0")&&!searchChannel.equals("")) {
        	//选择栏目查询
            InformationChannelPO po = channelBD.loadChannel(searchChannel);
        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
        		whereSQL = whereSQL + channelBD.getChannelById(searchChannel);
        	}else{//不包含子栏目则只查该栏目中的信息
        		whereSQL = whereSQL + " and (bbb.channelId = :searchChannel or aaa.otherChannel like :searchChannel2) ";
        		varMap.put("searchChannel", searchChannel);
        		varMap.put("searchChannel2", "%,"+searchChannel+",%");
        	}
        }
        if (title != null && !title.equals("")) {
            //标题
            whereSQL = whereSQL + " and aaa.informationTitle like :title " ;
            varMap.put("title", "%"+title+"%");
        }
        if (searchIssuerName!=null && !searchIssuerName.equals("")) {
        	//发布人
            whereSQL = whereSQL + " and aaa.informationIssuer like :searchIssuerName ";
            varMap.put("searchIssuerName", "%"+searchIssuerName+"%");
        }
        //办理状态
        String queryStatus = request.getParameter("queryStatus");
        if(queryStatus!=null && !queryStatus.equals("none")){
            whereSQL = whereSQL + " and bpm.poolWorkStatus = :queryStatus";//  request.getParameter("queryStatus");
            varMap.put("queryStatus", queryStatus);
        }
        if(startDate != null && !startDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime >= :startDate";
        	varMap.put("startDate", sdf.parse(startDate+" 00:00:00"));
        }
        if(endDate != null && !endDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime <= :endDate";
        	varMap.put("endDate", sdf.parse(endDate+" 23:59:59"));
        }
        
        whereSQL += " and bpm.poolModuleId = '4' and bpm.poolRecordId = aaa.informationId ";
        
        String orderSQL = " order by aaa.orderCode desc, aaa.informationIssueTime desc, aaa.informationModifyTime desc" ;
		String orderByFieldName = request.getParameter("orderByFieldName") != null
				? request.getParameter("orderByFieldName") : "";
		String orderByType = request.getParameter("orderByType") != null 
				? request.getParameter("orderByType") : "";
		if(orderByFieldName!=null && !"".equals(orderByFieldName)){
			orderSQL = " order by aaa."+orderByFieldName+" "+orderByType;
		}
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, orderSQL);
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        page.setVarMap(varMap);
        List reslutList = page.getResultList();
        /*办理状态是办理中的，显示当前活动名称
        com.whir.ezoffice.workflow.common.util.NewProc newProc = new com.whir.ezoffice.workflow.common.util.NewProc();
        for(int i=0;i<reslutList.size();i++){
        	Object[] obj = (Object[]) reslutList.get(i);
        	if(obj[15].toString().equals("1")){
        		Object[] procObj = (Object[]) newProc.getDocInfo("4",obj[0].toString());
        		String  blzt="";
				if("null".equals(""+procObj[0])||"NULL".equals(""+procObj[0])){
					blzt="";
				}else{
				    blzt=procObj[0].toString();										 
				}
				obj[15] = blzt;
        	}
        }
         办理状态是办理中的，显示当前活动名称*/
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, reslutList);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		logger.debug("信息办理查阅列表数据:\n"+json);
		printResult(G_SUCCESS,json);
		return null;
	}
	
	/**
	 * 取某信息关联的信息列表
	 * @return
	 */
	public String getRelationList(){
		request.setAttribute("relation","1");
		request.setAttribute("relationModule","information");
		return "allList";
	}
	
	@SuppressWarnings("unchecked")
	public String relationList(){
		String informationId = request.getParameter("informationId");
		String moduleType = request.getParameter("moduleType");
		String relationModule = request.getParameter("relationModule");
		String viewSQL =
            " aaa.informationId, aaa.informationTitle, aaa.informationKits, " +
            " aaa.informationIssuer, aaa.informationVersion, " +
            " aaa.informationIssueTime,aaa.informationSummary,aaa.informationHead," +
            " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId," +
            " aaa.titleColor,aaa.isConf,aaa.documentNo,aaa.transmitToEzsite," +
            " aaa.informationModifyTime,aaa.orderCode,aaa.informationIssueOrg,aaa.informationIsCommend,bbb.newInfoDeadLine";
		String fromSQL =
            " com.whir.ezoffice.information.infomanager.po.InformationPO aaa " +
            " join aaa.informationChannel bbb , com.whir.ezoffice.relation.po.RelationDataPO rrr ";
		String whereSQL = "where rrr.moduleType= '"+moduleType+"' and rrr.infoId="+informationId+" " +
			"and aaa.informationId=rrr.relationInfoId and rrr.relationObjectType='"+relationModule+"' ";
		String orderSQL = " order by aaa.orderCode desc, aaa.informationIssueTime desc, aaa.informationModifyTime desc" ;
		String orderByFieldName = request.getParameter("orderByFieldName") != null ? request.getParameter("orderByFieldName"): "";
		String orderByType = request.getParameter("orderByType") != null ? request.getParameter("orderByType"): "";
		if (orderByFieldName != null && !"".equals(orderByFieldName)) {
			orderSQL = " order by aaa." + orderByFieldName + " " + orderByType;
		}
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, orderSQL);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		page.setVarMap(null);
		List reslutList = page.getResultList();
		int pageCount = page.getPageCount();
		int recordCount = page.getRecordCount();
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, reslutList);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount + "},data:" + json + "}";
		logger.debug("信息办理查阅列表数据:\n" + json);
		printResult(G_SUCCESS, json);
		return null;
	}
	
	/**
	 * 全文检索页面
	 * @return
	 * @throws ParseException 
	 */
	public String retrievalList(){
		return "retrieval";
	}
	
	/**
	 * 全文检索数据
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String retrieval() throws ParseException{
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString(); //用户组织
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        ChannelBD channelBD = new ChannelBD();
        InformationBD informationBD = new InformationBD();
        ManagerBD managerBD = new ManagerBD();
        /* 判断当前用户是否有信息维护－排序的权限（信息维护的权限并且权限范围为全部）*/
        boolean hasRight = false;
        String rightCode = "01*03*03";//信息管理维护权限
        if (request.getParameter("checkdepart") != null && "1".equals(request.getParameter("checkdepart"))) {
            rightCode = "01*01*02"; //单位主页维护权限
        }
        List rightScopeList = new com.whir.org.manager.bd.ManagerBD().getRightScope(userId, rightCode);
        if (rightScopeList!=null && rightScopeList.size()>0 && rightScopeList.get(0)!=null) {
            Object[] obj = (Object[]) rightScopeList.get(0);
            if ("0".equals(obj[0].toString())) {
                hasRight = true;
            }
        }
        /* 判断当前用户是否有信息维护－排序的权限（信息维护的权限并且权限范围为全部）*/

        String viewSQL = " aaa.informationId, aaa.informationTitle, aaa.informationKits, aaa.informationIssuer, " +
            " aaa.informationVersion, aaa.informationIssueTime, aaa.informationSummary, aaa.informationHead," +
            " aaa.informationType, aaa.informationCommonNum, bbb.channelName, bbb.channelId, aaa.titleColor," +
            " aaa.isConf, aaa.documentNo, aaa.transmitToEzsite, aaa.informationModifyTime, aaa.orderCode," +
            " aaa.informationIssueOrg, aaa.informationIsCommend, bbb.channelType, aaa.informationIssuerId," +
            " bbb.channelNeedCheckup,aaa.dossierStatus,bbb.channelNeedCheckupForModi,aaa.informationValidType,"+
            " aaa.validBeginTime,aaa.validEndTime,bbb.newInfoDeadLine ";
        if(request.getParameter("retrievalAction")!=null && request.getParameter("retrievalAction").toString().equals("1")){
            viewSQL+=", aaa.informationContent ";
        }
        String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO aaa join aaa.informationChannel bbb ";
        String whereSQL = "";
        String mysqlnowString = new SimpleDateFormat("yyyy-MM-dd ").format(Calendar.getInstance().getTime());
            
        whereSQL = " where aaa.domainId=" + domainId + " and aaa.informationStatus=0 and (bbb.afficheChannelStatus is null or bbb.afficheChannelStatus='0')";
        
        /*if ("-1".equals(channelType)) {//单位主页所有信息
            whereSQL += " and bbb.channelType >0 and bbb.userDefine=0 ";
        } else {//信息管理或自定义信息频道
            whereSQL += " and bbb.channelType=" + channelType;
        }*/
        Map varMap = new HashMap();
        if(channelId!=null && !"".equals(channelId)){
        	//选择栏目查询或者点击具体栏目信息列表
            InformationChannelPO po = channelBD.loadChannel(channelId);
        	if(po.getIncludeChild()==1){//栏目设置中 设置了包含自栏目，查询时查出其子栏目中的信息
        		whereSQL = whereSQL + channelBD.getChannelById(channelId);
        	}else{//不包含子栏目则只查该栏目中的信息
        		whereSQL = whereSQL + " and (bbb.channelId = :channelId or aaa.otherChannel like :channelId2) ";
        		varMap.put("channelId", channelId);
        		varMap.put("channelId2", "%,"+channelId+",%");
        	}
        	/*没有信息维护的权限并且权限范围为全部的用户看不到过期的信息*/
            if(!hasRight){
            	whereSQL += " and ( aaa.informationValidType = 0 " +
            	"or EZOFFICE.FN_STRTODATE('" + mysqlnowString + "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
            }
            /*没有信息维护的权限并且权限范围为全部的用户看不到过期的信息*/
        } //else {
        	//所有栏目
        	whereSQL += " and ( aaa.informationValidType = 0 " +
        	"or EZOFFICE.FN_STRTODATE('" + mysqlnowString + "','S') between aaa.validBeginTime and aaa.validEndTime ) ";
	        //用户是否是信息的新建人
	        String rightWhere = managerBD.getRightFinalWhere(userId, orgId, "01*03*03", "aaa.informationIssueOrgId",
	            			"aaa.informationIssuerId");
	        //可维护的所有栏目
	        String canReadAllInfoChannel = informationBD.getAllInfoChannel(userId, orgId, channelType, userDefine);
	        //栏目维护权限
	        String scopeWhere = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, "bbb.channelManager",
	        				"bbb.channelManagerOrg", "bbb.channelManagerGroup");
	        //栏目查看权限
	        String  viewscopeWhere = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, "bbb.channelReader",
	        				"bbb.channelReaderOrg", "bbb.channelReaderGroup");
	        
	        String ttemp = " ";
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
	        if (request.getParameter("checkdepart") == null || request.getParameter("checkdepart").equals("") ||
	                request.getParameter("checkdepart").equals("null")) {
//	            if (!hasRight) {
	                if (hsqlList != null && hsqlList.size() != 0) {
	                    whereTmp = whereTmp + " and (( bbb.channelId in (" + hSql + ") ";
	                    whereTmp += " and bbb.channelType = " + channelType + ")or ";
	                    //同时发布到的栏目
	                    if (hsqlList.size() > 1000) {
	                        whereTmp += "( aaa.otherChannel in('-1', ";
	                        for (int i = 0; i < hsqlList.size(); i++) {
	                            if (i % 998 == 1) {
	                                whereTmp += "'-1') or aaa.otherChannel in( '-1'," + hsqlList.get(i) + ",";
	                            } else {
	                                whereTmp += hsqlList.get(i) + ",";
	                            }
	                        }
	                        whereTmp += "'-1')))";
	                    } else {
	                        whereTmp += " aaa.otherChannel in (";
	                        for (int i = 0; i < hsqlList.size() - 1; i++) {
	                            whereTmp += "'" + hsqlList.get(i) + "',";
	                        }
	                        whereTmp += "'" + hsqlList.get(hsqlList.size() - 1) + "'))";
	                    }
	                } else {
	                    whereTmp = whereTmp + " and bbb.channelId in (" + hSql + ") ";
	                }
	
//	                if (readerWhere != null && !readerWhere.equals("")) {
//	                    whereTmp = whereTmp + " and (" + readerWhere + ") ";
//	                }
//	            } else {
//	            	if(!channelType.equals("-1")){
//	            		whereTmp += " and bbb.channelType = " + channelType;
//	            	}
//	            }
	        } else {
	            if (hsqlList != null && hsqlList.size() != 0) {
	                whereTmp = whereTmp + " and (( bbb.channelId in (" + hSql + ") ";
	                if (!request.getParameter("checkdepart").equals("1")) {
	                    whereTmp += " and bbb.channelType = " + channelType;
	                }
	                whereTmp += ")or "; 
	                if (hsqlList.size() > 1000) {
	                    whereTmp += "( aaa.otherChannel in('-1', ";
	                    for (int i = 0; i < hsqlList.size(); i++) {
	                        if (i % 998 == 1) {
	                            whereTmp += "'-1') or aaa.otherChannel in( '-1'," + hsqlList.get(i) + ",";
	                        } else {
	                            whereTmp += hsqlList.get(i) + ",";
	                        }
	                    }
	                    whereTmp += "'-1')))";
	                } else {
	                    whereTmp += " aaa.otherChannel in (";
	                    for (int i = 0; i < hsqlList.size() - 1; i++) {
	                        whereTmp += "'" + hsqlList.get(i) + "',";
	                    }
	                    whereTmp += "'" + hsqlList.get(hsqlList.size() - 1) + "'))";
	                }
	            } else {
	                whereTmp = whereTmp + " and bbb.channelId in (" + hSql + ") ";
	                if (!request.getParameter("checkdepart").equals("1")) {
	                    whereTmp += " and bbb.channelType = " + channelType;
	                }
	            }
	
//	            if (!hasRight) {
//	                if (readerWhere != null && !readerWhere.equals("")) {
//	                    whereTmp = whereTmp + " and (" + readerWhere + ") ";
//	                }
//	            }
	        }
	        whereSQL += whereTmp + ")";
        //}
        //判断是否精华信息
        if (request.getParameter("type").equals("good")) {
            whereSQL += " and aaa.informationIsCommend=1 ";
        }
        
        if(startDate != null && !startDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime >= :startDate";
        	varMap.put("startDate", sdf.parse(startDate+" 00:00:00"));
        }
        if(endDate != null && !endDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and aaa.informationIssueTime <= :endDate";
        	varMap.put("endDate", sdf.parse(endDate+" 23:59:59"));
        }
        if(retrievalKey!=null && !retrievalKey.equals("")){
        	//全文检索
        	LuceneClient  luceneClient=new LuceneClient();
            String lids= luceneClient.getIdByLucenceRestult("", "information",retrievalKey);
            if (lids != null && !lids.equals("null") && lids.length() > 1) {
            	//当lids>1000时，sql报错，所以将其拆成多个or
            	String[] lidsArr = lids.split(",");
            	int len = lidsArr.length;
            	if(len < 1000){
            		whereSQL += " and aaa.informationId in(" + lids + ") ";
            	}else{
            		String sql = " and (aaa.informationId in(-1";
                    for(int i=0;i<lidsArr.length;i++){
                        //按照 999 条分割
                        if(i%998==1){
                            //第一次进来不算
                            if(i==1){
                            	sql+=","+lidsArr[i];
                            }else{
                               //到了 1000  加or
                            	sql+=") or aaa.informationId in("+lidsArr[i];
                            }
                        }else{
                        	sql+=","+lidsArr[i];
                        }
                    }
                    sql+=")) ";
            		whereSQL += sql;
            	}
            } else {
                whereSQL += " and (1>2) ";
            }
        }
        String orderSQL = " order by aaa.orderCode desc, aaa.informationIssueTime desc, aaa.informationModifyTime desc" ;
        String orderByFieldName = request.getParameter("orderByFieldName") != null
        		? request.getParameter("orderByFieldName") : "";
        String orderByType = request.getParameter("orderByType") != null 
        		? request.getParameter("orderByType") : "";
		if(orderByFieldName!=null && !"".equals(orderByFieldName)){
			orderSQL = " order by aaa."+orderByFieldName+" "+orderByType;
        }
		String listType = request.getParameter("listType");
		int pageSize = CommonUtils.getUserPageSize(request);
		if(listType!=null && listType.equals("1")){
			pageSize = 30;
		}
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, orderSQL);
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        page.setVarMap(varMap);
        List reslutList = page.getResultList();
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, reslutList);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		logger.debug("信息列表数据:\n"+json);
		printResult(G_SUCCESS,json);
		logger.debug("查询信息列表结束");
		return null;
	}
	
	/**
	 * 全文检索数据_NEW 
	 * @return
	 * @throws ParseException 
	 */
	@SuppressWarnings("unchecked")
	public String retrieval_new() throws ParseException{
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString(); //用户组织
        String orgIdString = session.getAttribute("orgIdString").toString();
        String domainId = CommonUtils.getSessionDomainId(request).toString();
        ChannelBD channelBD = new ChannelBD();
        InformationBD informationBD = new InformationBD();
        ManagerBD managerBD = new ManagerBD();
        StringBuffer buffer = new StringBuffer();
        StringBuffer frombuffer1 = new StringBuffer("");
        StringBuffer frombuffer2 = new StringBuffer("");
        StringBuffer frombuffer3 = new StringBuffer("");
        StringBuffer wherebuffer1 = new StringBuffer("");
        StringBuffer wherebuffer2 = new StringBuffer("");
        StringBuffer wherebuffer3 = new StringBuffer("");
        //String whereSql = "";

        String viewSQL = " inf.information_id, inf.informationTitle, inf.informationKits, inf.informationIssuer, " +
            " inf.informationVersion, inf.informationIssueTime, inf.informationSummary, inf.informationHead," +
            " inf.informationType, inf.informationCommonNum, ch.channelName, ch.channel_id, inf.titleColor," +
            " inf.isConf, inf.documentNo, inf.transmitToEzsite, inf.informationModifyTime, inf.orderCode," +
            " inf.informationIssueOrg, inf.informationIsCommend, ch.channelType, inf.informationIssuerId," +
            " ch.channelNeedCheckup,inf.dossierStatus,ch.channelNeedCheckupForModi,inf.informationValidType,"+
            " inf.validBeginTime,inf.validEndTime,ch.newInfoDeadLine ";
        if(request.getParameter("retrievalAction")!=null && request.getParameter("retrievalAction").toString().equals("1")){
            viewSQL+=", inf.informationContent ";
        }
        String fromSQL = " com.whir.ezoffice.information.infomanager.po.InformationPO inf join inf.informationChannel ch ";
        String whereSQL = "";
        String mysqlnowString = new SimpleDateFormat("yyyy-MM-dd ").format(Calendar.getInstance().getTime());
            
        whereSQL = " where inf.domain_id=" + domainId + " and inf.informationStatus=0 and (ch.afficheChannelStatus is null or ch.afficheChannelStatus='0')";
        /***----------------------------------------------------------------------------------------***/

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
       // 1.信息最新更新 取有权限看到的最新的15条 取信息分类(完整的分类如:"新闻.国内新闻"),信息标题,最后更新日期,阅读次数
       StringBuffer sqlBuf = new StringBuffer();
       String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();

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
       //System.out.println("========查询全部数据，不包含具体查询条件========");
       //System.out.println("========viewSQL:" + viewSQL);
       //System.out.println("========fromSQL:" + fromSQL);
       //System.out.println("========whereSQL:" + whereSQL);
       
       /***----------------------------------------------------------------------------------------***/
        Map varMap = new HashMap();
        
        if(startDate != null && !startDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and inf.informationIssueTime >= :startDate";
        	varMap.put("startDate", sdf.parse(startDate+" 00:00:00"));
        }
        if(endDate != null && !endDate.equals("")){
        	//发布日期
        	whereSQL = whereSQL + " and inf.informationIssueTime <= :endDate";
        	varMap.put("endDate", sdf.parse(endDate+" 23:59:59"));
        }
        String orderIds = "";
        if(retrievalKey!=null && !retrievalKey.equals("")){
        	//全文检索
        	LuceneClient  luceneClient=new LuceneClient();
            String lids= luceneClient.getIdByLucenceRestult("", "information",retrievalKey);
            if (lids != null && !lids.equals("null") && lids.length() > 1) {
            	//20151215 -by jqq 增加排序，按照给定的id串顺序来排序
            	orderIds = lids;
            	//当lids>1000时，sql报错，所以将其拆成多个or
            	String[] lidsArr = lids.split(",");
            	int len = lidsArr.length;
            	if(len < 1000){
            		whereSQL += " and inf.information_id in(" + lids + ") ";
            	}else{
            		String sql = " and (inf.information_id in(-1";
                    for(int i=0;i<lidsArr.length;i++){
                        //按照 999 条分割
                        if(i%998==1){
                            //第一次进来不算
                            if(i==1){
                            	sql+=","+lidsArr[i];
                            }else{
                               //到了 1000  加or
                            	sql+=") or inf.information_id in("+lidsArr[i];
                            }
                        }else{
                        	sql+=","+lidsArr[i];
                        }
                    }
                    sql+=")) ";
            		whereSQL += sql;
            	}
            } else {
                whereSQL += " and (1>2) ";
            }
        }
        String orderSQL = " order by EZOFFICE.FN_INDEX_NUM(:ORDERIDS,information_id) asc, orderCode desc, informationIssueTime desc, informationModifyTime desc" ;
        String orderByFieldName = request.getParameter("orderByFieldName") != null
        		? request.getParameter("orderByFieldName") : "";
        String orderByType = request.getParameter("orderByType") != null 
        		? request.getParameter("orderByType") : "";
		if(orderByFieldName!=null && !"".equals(orderByFieldName)){
			orderSQL = " order by EZOFFICE.FN_INDEX_NUM(:ORDERIDS,information_id) asc, "+orderByFieldName+" "+orderByType;
        }
		/**截取返回的id串，前N条id记录**/
		String sub_orderIds = "";
		int i = 0;
	    int index = 0;
	    boolean outflag = false;
	    //截取数量太长可能导致报错，该数值500是测试后估计出来的
	    while (i++ < 500) {
	    	index = orderIds.indexOf(",", index + 1);
	    	if (index == -1) {
	    		outflag = true;
	    		break;
	    	}
	    }
	    if(!outflag) {
	    	sub_orderIds = orderIds.substring(0, index);
	    }else{
	    	sub_orderIds = orderIds;
	    }
		
		logger.debug("========截取500个sub_orderIds:" + sub_orderIds);
		/**截取返回的id串，前N条id记录**/
		varMap.put("ORDERIDS", sub_orderIds);
		String listType = request.getParameter("listType");
		int pageSize = CommonUtils.getUserPageSize(request);
		if(listType!=null && listType.equals("1")){
			pageSize = 30;
		}
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        //System.out.println("========加入全文检索查询条件，开始查询========");
        Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL, orderSQL);
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        page.setVarMap(varMap);
        List reslutList = page.getResultList();
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, reslutList);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		logger.debug("信息列表数据:\n"+json);
		printResult(G_SUCCESS,json);
		logger.debug("查询信息列表结束");
		return null;
	}
}
