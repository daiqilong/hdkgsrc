package com.whir.ezoffice.personalwork.setup.actionsupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whir.common.util.EJBProxy;
import com.whir.common.util.ParameterGenerator;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.ezoffice.personalwork.common.util.PersonalWorkEJBProxy;
import com.whir.ezoffice.personalwork.setup.bd.MyInfoBD;
import com.whir.ezoffice.personalwork.setup.ejb.MyInfoEJBHome;
import com.whir.ezoffice.personalwork.setup.po.OaEmployeeStatusPO;
import com.whir.ezoffice.personalwork.setup.po.OaStatusDetailPO;
import com.whir.org.manager.bd.ManagerBD;

public class StatusAction extends BaseActionSupport {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(StatusAction.class.getName());

	/** 权限 [BEGIN] */
    // 状态查询权限
    private static final String STATUS_SEARCH_RIGHT = "grzt*01*01";
	/** 权限 [END] */

    private OaStatusDetailPO status;
    
    private String detailId;
    
    private String statusClassId;
    
    /** 查询项部分 [BEGIN] */
    private String menuType;
    /* "当前状态" [BEGIN] */ 
    private String queryStatusClassId;
    /* "当前状态" [END] */
    
    /* "状态查询" [BEGIN] */
    private String queryEmpName;
    private String queryOrgName;
    private String queryStatusName;
    
    private String queryByTimeFromS = ""; // 开始时间
    private String queryByTimeToS = "";   // 结束时间
    
    private String queryByTimeFromE = ""; // 开始时间
    private String queryByTimeToE = "";   // 结束时间
    /* "状态查询" [END] */
    /** 查询项部分 [END] */

    /** 新增页部分 [BEGIN] */
    private String formStatusStartDateTime;
    private String formStatusEndDateTime;
    /** 新增页部分 [END] */

    public OaStatusDetailPO getStatus() {
		return status;
	}

	public void setStatus(OaStatusDetailPO status) {
		this.status = status;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public void setStatusClassId(String statusClassId) {
		this.statusClassId = statusClassId;
	}

	public String getStatusClassId() {
		return statusClassId;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public String getQueryStatusClassId() {
		return queryStatusClassId;
	}

	public void setQueryStatusClassId(String queryStatusClassId) {
		this.queryStatusClassId = queryStatusClassId;
	}

	public String getQueryEmpName() {
		return queryEmpName;
	}

	public void setQueryEmpName(String queryEmpName) {
		this.queryEmpName = queryEmpName;
	}

	public String getQueryOrgName() {
		return queryOrgName;
	}

	public void setQueryOrgName(String queryOrgName) {
		this.queryOrgName = queryOrgName;
	}

	public String getQueryStatusName() {
		return queryStatusName;
	}

	public void setQueryStatusName(String queryStatusName) {
		this.queryStatusName = queryStatusName;
	}

	public String getQueryByTimeFromS() {
		return queryByTimeFromS;
	}

	public void setQueryByTimeFromS(String queryByTimeFromS) {
		this.queryByTimeFromS = queryByTimeFromS;
	}

	public String getQueryByTimeToS() {
		return queryByTimeToS;
	}

	public void setQueryByTimeToS(String queryByTimeToS) {
		this.queryByTimeToS = queryByTimeToS;
	}

	public String getQueryByTimeFromE() {
		return queryByTimeFromE;
	}

	public void setQueryByTimeFromE(String queryByTimeFromE) {
		this.queryByTimeFromE = queryByTimeFromE;
	}

	public String getQueryByTimeToE() {
		return queryByTimeToE;
	}

	public void setQueryByTimeToE(String queryByTimeToE) {
		this.queryByTimeToE = queryByTimeToE;
	}

	public String getFormStatusStartDateTime() {
		return formStatusStartDateTime;
	}

	public void setFormStatusStartDateTime(String formStatusStartDateTime) {
		this.formStatusStartDateTime = formStatusStartDateTime;
	}

	public String getFormStatusEndDateTime() {
		return formStatusEndDateTime;
	}

	public void setFormStatusEndDateTime(String formStatusEndDateTime) {
		this.formStatusEndDateTime = formStatusEndDateTime;
	}

	/**
     * 获取"当前状态"列表信息
     * 
     * @return
     * @throws Exception
     */
    public String list() throws Exception {
        logger.debug("查询 当前状态 列表开始");

        Long userId = com.whir.common.util.CommonUtils
                .getSessionUserId(request);
        Long orgId = com.whir.common.util.CommonUtils
        .getSessionOrgId(request);
        /** 列表项部分 [BEGIN] */
        StringBuffer sbViewSQL = new StringBuffer();
        sbViewSQL.append("po.detailId, po.createemp, po.createempid, po.createorg, po.createorgid, ");
        sbViewSQL.append("po.startdatetime, po.enddatetime, po.createdate, po.statusId, poo.statusName ");
        sbViewSQL.append("");
        sbViewSQL.append("");
        /** 列表项部分 [END] */

        StringBuffer sbFromSQL = new StringBuffer("com.whir.ezoffice.personalwork.setup.po.OaStatusDetailPO po ");
        sbFromSQL.append(", com.whir.ezoffice.personalwork.setup.po.OaEmployeeStatusPO poo");

        StringBuffer sbWhereSQL = new StringBuffer();
        Map varMap = new HashMap();

        /** 默认条件部分 [BEGIN] */
        sbWhereSQL.append("where po.statusId=poo.statusId");
        sbWhereSQL.append("");
        /** 默认条件部分 [END] */
        
        /** 由不同的菜单进入不同的页面，搜索条件会不一样 [BEGIN] */
		if (menuType != null && "mine".equals(menuType)) {
			// 我的"当前状态"
			sbWhereSQL.append("  and po.createempid=").append(userId);

			// 搜索项--状态类别ID
			if (queryStatusClassId != null && !"".equals(queryStatusClassId)) {
				sbWhereSQL.append(" and po.statusId =:queryStatusClassId ");
				varMap.put("queryStatusClassId", queryStatusClassId);
			}

		} else if (menuType != null && "search".equals(menuType)) {
			// "状态查询"
		    ManagerBD managerBD = new ManagerBD();
            sbWhereSQL.append(" and ("
                    + managerBD.getRightFinalWhere(userId.toString(), orgId
                            .toString(),"grzt*01*01",
                            "po.createorgid", "po.createempid")
                    + ")");
	        // 搜索项--人员姓名
	        if (queryEmpName != null && !"".equals(queryEmpName)) {
	            sbWhereSQL.append(" and po.createemp like :queryEmpName");
	            varMap.put("queryEmpName", "%" + queryEmpName + "%");
	        }
			
	        // 搜索项--组织名称
	        if (queryOrgName != null && !"".equals(queryOrgName)) {
	            sbWhereSQL.append(" and po.createorg like :queryOrgName");
	            varMap.put("queryOrgName", "%" + queryOrgName + "%");
	        }
			
	        // 搜索项--状态名称
	        if (queryStatusName != null && !"".equals(queryStatusName)) {
	            sbWhereSQL.append(" and poo.statusName like :queryStatusName");
	            varMap.put("queryStatusName", "%" + queryStatusName + "%");
	        }
	        
			// 时间段--"当前状态"开始时间
			if (queryByTimeFromS != null && !"".equals(queryByTimeFromS)) {
				Calendar cFrom = Calendar.getInstance();
				String[] arrF = queryByTimeFromS.split(" ");
				if (arrF.length == 2) {
					String[] dateArr = arrF[0].split("-");
					String[] timeArr = arrF[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cFrom.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
				sbWhereSQL
						.append(" and (po.startdatetime >=:queryByTimeFromS ) ");
				varMap.put("queryByTimeFromS", cFrom.getTime());
			}
			if (queryByTimeToS != null && !"".equals(queryByTimeToS)) {
				Calendar cTo = Calendar.getInstance();
				String[] arrT = queryByTimeToS.split(" ");
				if (arrT.length == 2) {
					String[] dateArr = arrT[0].split("-");
					String[] timeArr = arrT[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cTo.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
				sbWhereSQL
						.append(" and (po.startdatetime <=:queryByTimeToS ) ");
				varMap.put("queryByTimeToS", cTo.getTime());
			}

			// 时间段--"当前状态"结束时间
			if (queryByTimeFromE != null && !"".equals(queryByTimeFromE)) {
				Calendar cFrom = Calendar.getInstance();
				String[] arrF = queryByTimeFromE.split(" ");
				if (arrF.length == 2) {
					String[] dateArr = arrF[0].split("-");
					String[] timeArr = arrF[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cFrom.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
				sbWhereSQL
						.append(" and (po.enddatetime >=:queryByTimeFromE ) ");
				varMap.put("queryByTimeFromE", cFrom.getTime());
			}
			if (queryByTimeToE != null && !"".equals(queryByTimeToE)) {
				Calendar cTo = Calendar.getInstance();
				String[] arrT = queryByTimeToE.split(" ");
				if (arrT.length == 2) {
					String[] dateArr = arrT[0].split("-");
					String[] timeArr = arrT[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cTo.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
				sbWhereSQL.append(" and (po.enddatetime <=:queryByTimeToE) ");
				varMap.put("queryByTimeToE", cTo.getTime());
			}
		} else {
			// 由未知菜单项进入
	        sbWhereSQL.append(" and 1=2 ");
		}
		//System.out.println("=====@StatusAction--[list()]--[sbWhereSQL]=[" + sbWhereSQL.toString() + "]");
		
        /** 由不同的菜单进入不同的页面，搜索条件会不一样 [END] */

        /** 排序项部分 [BEGIN] */
        String orderByFieldName = request.getParameter("orderByFieldName") != null ? request
                .getParameter("orderByFieldName")
                : "";
        String orderByType = request.getParameter("orderByType") != null ? request
                .getParameter("orderByType")
                : "";
        StringBuffer sbOrderSQL = new StringBuffer();
        sbOrderSQL.append(" order by ");
        if (orderByFieldName != null && !"".equals(orderByFieldName)) {
            sbOrderSQL.append(" po.").append(orderByFieldName).append(" ").append(orderByType);
        }else{
            sbOrderSQL.append(" po.detailId desc ");
        }
        /** 排序项部分 [END] */

        int pageSize = com.whir.common.util.CommonUtils
                .getUserPageSize(request);

        int currentPage = 0;
        if (request.getParameter("startPage") != null) {
            currentPage = Integer.parseInt(request.getParameter("startPage"));
        }

        Page page = PageFactory.getHibernatePage(sbViewSQL.toString(), sbFromSQL
                .toString(), sbWhereSQL.toString(), sbOrderSQL.toString());
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        page.setVarMap(varMap);
        List list = page.getResultList();
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();

        StringBuffer sbViewJsonArr = new StringBuffer();
        sbViewJsonArr.append("po.id, po.createemp, po.createempid, po.createorg, po.createorgid, ");
        sbViewJsonArr.append("po.startdatetime, po.enddatetime, po.createdate, po.statusId, poo.statusName ");
        sbViewJsonArr.append("");

        JacksonUtil util = new JacksonUtil();
        String json = util.writeArrayJSON(sbViewJsonArr.toString(), list);
        json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
                + "},data:" + json + "}";
        //System.out.println("=====@StatusAction.java--[list()]--[json]=[" + json + "]");
        printResult(G_SUCCESS, json);

        logger.debug("查询 当前状态 列表结束");
        return null;
    }

    /**
     * 进入"当前状态"列表页面
     * @return
     */
	public String statusList() {
		Long domainId = com.whir.common.util.CommonUtils
				.getSessionDomainId(request);
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (domainId != null && !"".equals(domainId) && userId != null
				&& !"".equals(userId)) {
			// 获取状态类别LIST
			MyInfoBD bd = new MyInfoBD();
			List statusClassList = bd.getStatusListByUserId(userId.toString());
			request.setAttribute("statusClassList", statusClassList);

			return "statusList";
		}
		return null;
	}

    /**
     * 进入"状态查询"列表页面
     * @return
     */
	public String statusSearchList() {
		/**
		 * 检验 验证码/权限
		 */
		if (!this.judgeCallRight("", STATUS_SEARCH_RIGHT)) {
			return BaseActionSupport.NO_RIGHTS;
		}
		
		Long domainId = com.whir.common.util.CommonUtils
				.getSessionDomainId(request);
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (domainId != null && !"".equals(domainId) && userId != null
				&& !"".equals(userId)) {

			return "statusSearchList";
		}
		return null;
	}
    

    /**
     * 进入"当前状态"新增页面
     * @return
     */
	public String addStatus() {
		Long domainId = com.whir.common.util.CommonUtils
				.getSessionDomainId(request);
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (domainId != null && !"".equals(domainId) && userId != null
				&& !"".equals(userId)) {

			// 获取状态类别LIST
			MyInfoBD bd = new MyInfoBD();
			Calendar cI = Calendar.getInstance();

			//System.out.println("=====@StatusAction--[addStatus()]--[statusClassId]=[" + statusClassId + "]");
//			if (statusClassId != null && "0".equals(statusClassId)) {
//				/* Modified by Qian Min for bug 7074 at 2013-08-23 [BEGIN] */
//				/*
//				boolean bConflict = bd.hasConflictRecords(userId, cI.getTime(),
//						cI.getTime(), "");
//				//System.out.println("=====@StatusAction--[addStatus()]--[bConflict]=[" + bConflict + "]");
//				if (bConflict) {
//					String errorTip = "当前状态起止时间段与其它记录存在冲突，保存失败。";
//					request.setAttribute("errorTip", errorTip);
//
//					return "addStatusError";
//				}
//				*/
//				String errorTip = bd.getConflictRecordsTip(userId,
//						cI.getTime(), cI.getTime(), "");
//				//System.out.println("=====@StatusAction--[addStatus()]--[errorTip]=[" + errorTip + "]");
//				if (!"".equals(errorTip)) {
//					request.setAttribute("errorTip", errorTip);
//
//					return "addStatusError";
//				}
//				/* Modified by Qian Min for bug 7074 at 2013-08-23 [END] */
//				
//			}

			List statusClassList = bd.getStatusListByUserId(userId.toString());
			request.setAttribute("statusClassList", statusClassList);
			
			//request.setAttribute("statusId", "102");
			// 初始化新增状态的开始时间和结束时间
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			formStatusStartDateTime = sdf.format(cI.getTime());
			formStatusEndDateTime = sdf.format(cI.getTime());

			return "addStatus";

		}
		return null;
	}
    
    /**
     * 进入"当前状态"修改页面
     * @return
     */
	public String modiStatus() {
		Long domainId = com.whir.common.util.CommonUtils
				.getSessionDomainId(request);
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (domainId != null && !"".equals(domainId) && userId != null
				&& !"".equals(userId)) {

			if (detailId != null && !"".equals(detailId)) {
				Long id = new Long(detailId);
				OaStatusDetailPO po = null;
				MyInfoBD bd = new MyInfoBD();
				po = bd.loadStatus(id);
				this.setStatus(po);

				// 获取状态类别LIST
				List statusClassList = bd.getStatusListByUserId(userId
						.toString());
				request.setAttribute("statusClassList", statusClassList);

				// 获取该状态的开始时间和结束时间
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				formStatusStartDateTime = sdf.format(po.getStartdatetime());
				formStatusEndDateTime = sdf.format(po.getEnddatetime());
			}
			return "modiStatus";
		}
		return null;
	}
    
    /**
     * 进入"当前状态"查看页面
     * @return
     */
	public String viewStatus() {
		Long domainId = com.whir.common.util.CommonUtils
				.getSessionDomainId(request);
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (domainId != null && !"".equals(domainId) && userId != null
				&& !"".equals(userId)) {

			if (detailId != null && !"".equals(detailId)) {
				Long id = new Long(detailId);
				OaStatusDetailPO po = null;
				MyInfoBD bd = new MyInfoBD();
				po = bd.loadStatus(id);
				this.setStatus(po);

				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				formStatusStartDateTime = sdf.format(po.getStartdatetime());
				formStatusEndDateTime = sdf.format(po.getEnddatetime());

				// 获取状态类别LIST
				List statusClassList = bd.getStatusListByUserId(userId
						.toString());
				request.setAttribute("statusClassList", statusClassList);
			}
			return "viewStatus";
		}
		return null;
	}

    
    /**
     * 保存"当前状态"信息
     * @return
     */
	public String saveStatus() {
		String result = "false";
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (userId != null && !"".equals(userId)) {

			// 开始时间
			Calendar cFrom = Calendar.getInstance();
			if (formStatusStartDateTime != null
					&& !"".equals(formStatusStartDateTime)) {
				String[] arr = formStatusStartDateTime.split(" ");
				if (arr.length == 2) {
					String[] dateArr = arr[0].split("-");
					String[] timeArr = arr[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cFrom.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
			}
			// 结束时间
			Calendar cTo = Calendar.getInstance();
			if (formStatusEndDateTime != null
					&& !"".equals(formStatusEndDateTime)) {
				String[] arr = formStatusEndDateTime.split(" ");
				if (arr.length == 2) {
					String[] dateArr = arr[0].split("-");
					String[] timeArr = arr[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cTo.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
			}

			MyInfoBD bd = new MyInfoBD();

			/* Modified by Qian Min for bug 7074 at 2013-08-23 [BEGIN] */
			String detailIds = getConflictRecordsTip(userId,
					cFrom.getTime(), cTo.getTime(), "");
			System.out.println("detailIds::::"+detailIds);
			
			//System.out.println("=====@StatusAction--[saveStatus()]--[errorTip]=[" + errorTip + "]");
			if (detailIds != null && !"".equals(detailIds)) {
				if (detailIds.endsWith(",")) {
					detailIds = detailIds.substring(0, detailIds.length() - 1);
				}
				String delResult = bd.batchDeleteStatus(detailIds);
				System.out.println("delResult::::"+delResult);
				
				if ("true".equals(delResult)) {
					Long orgId = com.whir.common.util.CommonUtils
					.getSessionOrgId(request);
					String userName = com.whir.common.util.CommonUtils
							.getSessionUserName(request);
					String orgName = com.whir.common.util.CommonUtils
							.getSessionOrgName(request);
		
					/** 基本信息 [BEGIN] */
					status.setCreateempid(userId.toString());
					status.setCreateemp(userName);
					status.setCreateorgid(orgId.toString());
					status.setCreateorg(orgName);
					status.setCreatedate(new Date());
					/** 基本信息 [END] */
		
					status.setStartdatetime(cFrom.getTime());
					status.setEnddatetime(cTo.getTime());
		
					result = bd.saveStatus(status);
				}
			} else {
			/* Modified by Qian Min for bug 7074 at 2013-08-23 [END] */
				Long orgId = com.whir.common.util.CommonUtils
						.getSessionOrgId(request);
				String userName = com.whir.common.util.CommonUtils
						.getSessionUserName(request);
				String orgName = com.whir.common.util.CommonUtils
						.getSessionOrgName(request);

				/** 基本信息 [BEGIN] */
				status.setCreateempid(userId.toString());
				status.setCreateemp(userName);
				status.setCreateorgid(orgId.toString());
				status.setCreateorg(orgName);
				status.setCreatedate(new Date());
				/** 基本信息 [END] */

				status.setStartdatetime(cFrom.getTime());
				status.setEnddatetime(cTo.getTime());

				result = bd.saveStatus(status);
			}
		}
		if ("true".equals(result)) {
			printResult("success");
		} else {
			printResult(result);
		}
		return null;
	}
    
	
	public String getConflictRecordsTip(Long userId, Date startDateTime, Date endDateTime, String curRecordId)
	  {
	    String ret = "";
	    List list = getUserStatusListByStartDateTimeAndEndDateTime(userId, 
	      startDateTime, endDateTime, curRecordId);
	    if ((list != null) && (list.size() > 0)) {
	      int listSize = list.size();
	      Object[] obj = (Object[])null;
	      OaStatusDetailPO po = null;
	      OaEmployeeStatusPO poo = null;
	      DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	      for (int i = 0; i < listSize; i++) {
	        obj = (Object[])list.get(i);
	        po = (OaStatusDetailPO)obj[0];
	        ret = Long.toString(po.getDetailId());
	      }
	    }
	    return ret;
	  }
	
	
	public List getUserStatusListByStartDateTimeAndEndDateTime(Long userId, Date startDateTime, Date endDateTime, String curRecordId)
	  {
	    List retList = null;

	    ParameterGenerator pg = new ParameterGenerator(4);
	    try {
	      pg.put(userId, "Long");
	      pg.put(startDateTime, Date.class);
	      pg.put(endDateTime, Date.class);
	      pg.put(curRecordId, "String");

	      EJBProxy ejbProxy = new PersonalWorkEJBProxy("MyInfoEJB", 
	        "MyInfoEJBLocal", MyInfoEJBHome.class);

	      retList = (List)ejbProxy.invoke(
	        "getUserStatusListByStartDateTimeAndEndDateTime", pg
	        .getParameters());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return retList;
	  }
    /**
     * 修改"当前状态"信息
     * @return
     */
	public String updateStatus() {
		String result = "false";
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (userId != null && !"".equals(userId)) {
			// 开始时间
			Calendar cFrom = Calendar.getInstance();
			if (formStatusStartDateTime != null
					&& !"".equals(formStatusStartDateTime)) {
				String[] arr = formStatusStartDateTime.split(" ");
				if (arr.length == 2) {
					String[] dateArr = arr[0].split("-");
					String[] timeArr = arr[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cFrom.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
			}
			// 结束时间
			Calendar cTo = Calendar.getInstance();
			if (formStatusEndDateTime != null
					&& !"".equals(formStatusEndDateTime)) {
				String[] arr = formStatusEndDateTime.split(" ");
				if (arr.length == 2) {
					String[] dateArr = arr[0].split("-");
					String[] timeArr = arr[1].split(":");
					if (dateArr.length == 3 && timeArr.length == 2) {
						cTo.set(Integer.parseInt(dateArr[0]), Integer
								.parseInt(dateArr[1]) - 1, Integer
								.parseInt(dateArr[2]), Integer
								.parseInt(timeArr[0]), Integer
								.parseInt(timeArr[1]), 0);
					}
				}
			}

			MyInfoBD bd = new MyInfoBD();
			/* Modified by Qian Min for bug 7074 at 2013-08-23 [BEGIN] */
			String errorTip = bd.getConflictRecordsTip(userId,
					cFrom.getTime(), cTo.getTime(), status.getDetailId().toString());
			//System.out.println("=====@StatusAction--[updateStatus()]--[errorTip]=[" + errorTip + "]");
			if (!"".equals(errorTip)) {
				result = errorTip;
			} else {
			/* Modified by Qian Min for bug 7074 at 2013-08-23 [END] */
				status.setStartdatetime(cFrom.getTime());
				status.setEnddatetime(cTo.getTime());

				result = bd.updateStatus(status);
			}
		}

		if ("true".equals(result)) {
			printResult("success");
		} else {
			printResult(result);
		}
		return null;
	}
    
    /**
     * 删除某个"当前状态"信息
     * @return
     */
	public String deleteStatus() {
		String result = "false";
		Long domainId = com.whir.common.util.CommonUtils
				.getSessionDomainId(request);
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (domainId != null && !"".equals(domainId) && userId != null
				&& !"".equals(userId)) {

			if (detailId != null && !"".equals(detailId)) {
				Long id = new Long(detailId);
				MyInfoBD bd = new MyInfoBD();
				result = bd.deleteStatus(id);
			}
		}
		if ("true".equals(result)) {
			printResult("success");
		}
		return null;
	}
    
    /**
     * 批量删除"当前状态"信息
     * @return
     */
	public String batchDeleteStatus() {
		String result = "false";
		Long domainId = com.whir.common.util.CommonUtils
				.getSessionDomainId(request);
		Long userId = com.whir.common.util.CommonUtils
				.getSessionUserId(request);
		if (domainId != null && !"".equals(domainId) && userId != null
				&& !"".equals(userId)) {
			String ids = request.getParameter("id") == null ? "" : request
					.getParameter("id").toString();
			if (ids != null && !"".equals(ids)) {
				if (ids.endsWith(",")) {
					ids = ids.substring(0, ids.length() - 1);
				}
				MyInfoBD bd = new MyInfoBD();
				result = bd.batchDeleteStatus(ids);
			}
		}
		if ("true".equals(result)) {
			printResult("success");
		}
		return null;
	}
    
    /**
     * 未完成功能
     * @return
     */
    public String undone() {
        return "undone";
    }

}
