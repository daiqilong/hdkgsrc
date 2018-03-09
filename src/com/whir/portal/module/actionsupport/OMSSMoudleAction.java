package com.whir.portal.module.actionsupport;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.rd.util.WatchArrangeUtils;

public class OMSSMoudleAction extends BaseActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957611521728273579L;
	private static Logger logger = Logger.getLogger(OMSSMoudleAction.class.getName());
	//实时流量查询日期
	private String flowSearchDate;
	
	
	public String getFlowSearchDate() {
		return flowSearchDate;
	}
	public void setFlowSearchDate(String flowSearchDate) {
		this.flowSearchDate = flowSearchDate;
	}

	/**
	 * 实时流量通报表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String realTimeFlow()
	  {
	    return "realTimeFlow";
	  }
	
	/**
	 * 查找实时流量通报表数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String realTimeFlowData() {

		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        String viewSQL = "rrr.releaseunit,rrr.receiveunit,rrr.releasetime,rrr.content,rrr.effecttime,rrr.endtime,rrr.reason";
        String fromSQL = "REALTIMEFLOW rrr ";
        StringBuffer whereSQL = new StringBuffer();
        if(flowSearchDate!=null && !"".equals(flowSearchDate)){
        	whereSQL.append(" where rrr.datetime='"+flowSearchDate+"' order by rrr.datetime desc");
        } else {
        	whereSQL.append(" Where 1=1 order by rrr.datetime desc");
        }
		Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL.toString(), "");
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        List list = page.getResultList();
        
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, list);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		printResult(G_SUCCESS,json);
		
	    return null;
	}
	
	/**
	 * 每日0900统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String everyRunSituationOne()
	  {
	    return "everyRunSituationOne";
	  }
	
	public Map<String,Object> everyRunSituationOneData(String queryTime) throws IOException{
		
		String sql = "select onWatch,yAirdromeName,yAirdromePlan,yairdromeFact,yAirdromeRushHour,yAirdromeRush,yEnsureMsg,yEvent,yWeatherException,yReceiveFlux,yReleaseFlux,yEqu,event,weather,weatherExtHour,"+
				"weatherExtArea,weatherExtDifAirdromeName,weatherExtDifAirdromeHour,weatherExtDifAirdromeBack,weatherExtDifAirdromeBy,weatherExtDifairdromeCal,weatherExtShutAirdromeName,"+
				"weatherExtShutAirdromeHour,weatherExtShutAirdromeBack,weatherExtShutAirdromeBy,weatherExtShutAirdromeCal,weatherExtChange,receiveFlux,flightLateAirdromeName,flightLateReason,"+
				"flightLate,flightLateGeTwohour,futureFluxReason,futureFluxHour,futureFluxMeasures,airportAlternateMsg,equ,tempAirRoute"+
				" from EVERYDAY_RUN_SITUATION_ONE where datetime='"+queryTime+"'";
		String keyString = "onWatch,yAirdromeName,yAirdromePlan,yairdromeFact,yAirdromeRushHour,yAirdromeRush,yEnsureMsg,yEvent,yWeatherException,yReceiveFlux,yReleaseFlux,yEqu,event,weather,weatherExtHour,"+
					"weatherExtArea,weatherExtDifAirdromeName,weatherExtDifAirdromeHour,weatherExtDifAirdromeBack,weatherExtDifAirdromeBy,weatherExtDifairdromeCal,weatherExtShutAirdromeName,"+
					"weatherExtShutAirdromeHour,weatherExtShutAirdromeBack,weatherExtShutAirdromeBy,weatherExtShutAirdromeCal,weatherExtChange,receiveFlux,flightLateAirdromeName,flightLateReason,"+
					"flightLate,flightLateGeTwohour,futureFluxReason,futureFluxHour,futureFluxMeasures,airportAlternateMsg,equ,tempAirRoute";
		String[] keys = keyString.split(","); 
		Object[] obj1 = new WatchArrangeUtils().getArray(sql);
		Map<String,Object> map1 = new HashMap<String,Object>();
		try{
			if(obj1 !=null ){
				for(int i=0;i<obj1.length;i++){
					map1.put(keys[i], obj1[i]);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		map1.put("keys",keys);
	    return map1;
	}
	/**
	 *  每日1330统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String everyRunSituationTwo()
	  {
	    return "everyRunSituationTwo";
	  }
	public  Map<String,Object> everyRunSituationTwoData(String queryTime) throws IOException{
		String sql = "select event,weatherExtHour,weatherExtArea,weatherExtDifAirdromeName,weatherExtDifAirdromeHour,weatherExtDifAirdromeBack,weatherExtDifAirdromeBy,weatherExtDifairdromeCal,weatherExtShutAirdromeName"+
				",weatherExtShutAirdromeHour,weatherExtShutAirdromeBack,weatherExtShutAirdromeBy,weatherExtShutAirdromeCal,weatherExtChange,flightLateAirdromeName,flightLateReason,flightLate,flightLateGeTwohour"+
				",geTwohourWeatherReason,geTwohourMilitaryReason,geTwohourFluxReason,geTwohourCompanyReason,geTwohourOtherReason,geTwohourMeasures,receiveFlux,releaseFlux,aircraftErrorMsg,airportAlternateMsg"+
				",equ,toCoordinateMatters,tempAirRoute from EVERYDAY_RUN_SITUATION_TWO where datetime='"+queryTime+"'";
		String keyString = "event,weatherExtHour,weatherExtArea,weatherExtDifAirdromeName,weatherExtDifAirdromeHour,weatherExtDifAirdromeBack,weatherExtDifAirdromeBy,weatherExtDifairdromeCal,weatherExtShutAirdromeName"+
					",weatherExtShutAirdromeHour,weatherExtShutAirdromeBack,weatherExtShutAirdromeBy,weatherExtShutAirdromeCal,weatherExtChange,flightLateAirdromeName,flightLateReason,flightLate,flightLateGeTwohour"+
					",geTwohourWeatherReason,geTwohourMilitaryReason,geTwohourFluxReason,geTwohourCompanyReason,geTwohourOtherReason,geTwohourMeasures,receiveFlux,releaseFlux,aircraftErrorMsg,airportAlternateMsg"+
					",equ,toCoordinateMatters,tempAirRoute";
		String[] keys = keyString.split(","); 
		Object[] obj2 = new WatchArrangeUtils().getArray(sql);
		Map<String,Object> map2 = new HashMap<String,Object>();
		try{
			if(obj2 !=null ){
				for(int i=0;i<obj2.length;i++){
					map2.put(keys[i], obj2[i]);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		map2.put("keys",keys);
	    return map2;
		
	}
	/**
	 *  每日1630统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String everyRunSituationThree(){
		
		return "everyRunSituationThree";
	}
	public Map<String,Object> everyRunSituationThreeData(String queryTime) throws IOException{
		String sql = "select event,weatherExtHour,weatherExtArea,weatherExtDifAirdromeName,weatherExtDifAirdromeHour,weatherExtDifAirdromeBack,weatherExtDifAirdromeBy,weatherExtDifairdromeCal,weatherExtShutAirdromeName"+
				",weatherExtShutAirdromeHour,weatherExtShutAirdromeBack,weatherExtShutAirdromeBy,weatherExtShutAirdromeCal,weatherExtChange,flightLateAirdromeName,flightLateReason,flightLate,flightLateGeTwohour"+
				",geTwohourWeatherReason,geTwohourMilitaryReason,geTwohourFluxReason,geTwohourCompanyReason,geTwohourOtherReason,geTwohourMeasures,receiveFlux,releaseFlux,aircraftErrorMsg,airportAlternateMsg"+
				",equ,toCoordinateMatters,tempAirRoute from EVERYDAY_RUN_SITUATION_TWO where datetime='"+queryTime+"'";
		String keyString = "event,weatherExtHour,weatherExtArea,weatherExtDifAirdromeName,weatherExtDifAirdromeHour,weatherExtDifAirdromeBack,weatherExtDifAirdromeBy,weatherExtDifairdromeCal,weatherExtShutAirdromeName"+
					",weatherExtShutAirdromeHour,weatherExtShutAirdromeBack,weatherExtShutAirdromeBy,weatherExtShutAirdromeCal,weatherExtChange,flightLateAirdromeName,flightLateReason,flightLate,flightLateGeTwohour"+
					",geTwohourWeatherReason,geTwohourMilitaryReason,geTwohourFluxReason,geTwohourCompanyReason,geTwohourOtherReason,geTwohourMeasures,receiveFlux,releaseFlux,aircraftErrorMsg,airportAlternateMsg"+
					",equ,toCoordinateMatters,tempAirRoute";
		String[] keys = keyString.split(","); 
		Object[] obj3 = new WatchArrangeUtils().getArray(sql);
		Map<String,Object> map3 = new HashMap<String,Object>();
		try{
			if(obj3 !=null ){
				for(int i=0;i<obj3.length;i++){
					map3.put(keys[i], obj3[i]);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		map3.put("keys",keys);
	    return map3;
	  }
	
	/**
	 *  每日2030统计表
	 *  @param request
	 *            HttpServletRequest
	 */
	public String everyRunSituationFour()
	  {
	    return "everyRunSituationFour";
	  }
	public Map<String,Object> everyRunSituationFourData(String queryTime) throws IOException{
		
		String sql = "select event,public1,public2,equ,public4,public6,receiveFlux,releaseFlux,tempAirRoute from EVERYDAY_RUN_SITUATION_THREE where datetime='"+queryTime+"'";
		String keyString = "event,public1,public2,equ,public4,public6,receiveFlux,releaseFlux,tempAirRoute";
		String[] keys = keyString.split(","); 
		Object[] obj4 = new WatchArrangeUtils().getArray(sql);
		Map<String,Object> map4 = new HashMap<String,Object>();
		try{
			if(obj4 !=null ){
				for(int i=0;i<obj4.length;i++){
					map4.put(keys[i], obj4[i]);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		map4.put("keys",keys);
		return map4;
	}
	
	/**
	 *  运行总主任汇报材料(0800-1000)
	 *  @param request
	 *            HttpServletRequest
	 */
	public String videoMeetingOne()
	  {
	    return "videoMeetingOne";
	  }
	
	/**
	 *  运行总主任汇报材料(0800-1400)
	 *  @param request
	 *            HttpServletRequest
	 */
	public String videoMeetingTwo()
	  {
	    return "videoMeetingTwo";
	  }
	
	/**
	 *  运行总主任汇报材料(0800-1730)
	 *  @param request
	 *            HttpServletRequest
	 */
	public String videoMeetingThree()
	  {
	    return "videoMeetingThree";
	  }
	/*
	 * 获取视频会议主表数据
	 */
	public Map<String,Object> videoMeetingMainData(String tableTime) throws IOException, ParseException{
		String sql = "select runCondition,facility,weather from VIDEO_MEETING where datetime='"+tableTime+"'";
		String keyString = "runCondition,facility,weather";
		String[] keys = keyString.split(","); 
		Object[] obj1 = new WatchArrangeUtils().getArray(sql);
		Map<String,Object> map1 = new HashMap<String,Object>();
		try{
			if(obj1 !=null ){
				for(int i=0;i<obj1.length;i++){
					map1.put(keys[i], obj1[i]);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		map1.put("keys",keys);
		return map1;
	}
	/*
	 * 获取视频会议子表数据
	 */
	public Map<String,Object> videoMeetingFollowData(String tableTime) throws ParseException{
		String sql = "select releaseUnit,receiveUnit,limitPoint,limitTime,content,renson from SUB_VIDEO_MEETING where datetime='"+tableTime+"' and type='发布的限制'";
		List list1 = new WatchArrangeUtils().getList(sql);
		sql = "select releaseUnit,receiveUnit,limitPoint,limitTime,content,renson from SUB_VIDEO_MEETING where datetime='"+tableTime+"' and type='收到的限制'";
		List list2 = new WatchArrangeUtils().getList(sql);
		Map<String,Object> map2 = new HashMap<String,Object>();
		map2.put("发布的限制", list1);
		map2.put("收到的限制", list2);
		return map2;
	}
	
}
