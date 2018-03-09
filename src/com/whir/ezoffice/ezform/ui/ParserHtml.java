package com.whir.ezoffice.ezform.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.ezoffice.customdb.common.util.DbOpt;
import com.whir.ezoffice.ezform.bd.EzFormBD;
import com.whir.ezoffice.ezform.bd.EzFormSettingBD;
import com.whir.ezoffice.ezform.po.FormPO;
import com.whir.ezoffice.ezform.po.SettingPO;
import com.whir.ezoffice.ezform.po.TrigPO;
import com.whir.ezoffice.ezform.service.FormService;
import com.whir.ezoffice.ezform.util.FormContants;
import com.whir.ezoffice.ezform.util.FormHelper;

/**
 * 表单解析
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author w
 * @version 10.3.0.0
 *          ...
 *          11.1.0.0
 */
public class ParserHtml {
    private static Logger logger = Logger.getLogger(ParserHtml.class
            .getName());

    //表单操作类型
    private int operateType;

    //是否启用扩展属性
    private boolean enabledExtAttr = true;

    //联动id
    private String relaTrigId = null;

    public void setEnabledExtAttr(boolean enabledExtAttr) {
        this.enabledExtAttr = enabledExtAttr;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public void setRelaTrigId(String relaTrigId) {
        this.relaTrigId = relaTrigId;
    }

    public boolean isEnabledExtAttr() {
        return enabledExtAttr;
    }

    public int getOperateType() {
        return operateType;
    }

    public String getRelaTrigId() {
        return relaTrigId;
    }

    public ParserHtml() {
    }
    
    /**
     * 根据formCode解析表单模板
     * @param request
     * @param formCode
     * @return
     * @throws Exception
     */
    public String parseHtmlByFormCode(HttpServletRequest request,
            String formCode) throws Exception {
        
        logger.debug("[ParserHtml]:formCode:"+formCode);

        String domainId = CommonUtils.getSessionDomainId(request) + "";

        String flag = request.getParameter("flag");
        EzFormBD fbd = new EzFormBD();
        FormPO formPO = null;
        if ("print".equals(flag)) {
            formPO = fbd.getPrintFormPOByFormCode(formCode, domainId);
        } else {
            formPO = fbd.getFormPOByFormCode(formCode, domainId);
        }

        if (formPO != null) {
            String formId = formPO.getFormId() + "";

            return parseHtml(request, formId);
        }

        return null;
    }

    /**
     * 解析表单
     * @param request HttpServletRequest
     * @param formId String
     * @throws Exception
     * @return String
     */
    public String parseHtml(HttpServletRequest request, String formId) throws
        Exception {
        long startTime = System.currentTimeMillis();
        logger.debug("[ParserHtml]:start--:" + startTime);
        
        logger.debug("[ParserHtml]:formId:"+formId);
        
        String flag = request.getParameter("flag");
        String infoId = request.getParameter("p_wf_recordId");
        if(CommonUtils.isEmpty(infoId)){
            infoId = request.getParameter("infoId");
        }
        
        String parentRecordId = request.getParameter("p_wf_pareRecordId");
        
        logger.debug("request.getParameter p_wf_openType:" + request.getParameter("p_wf_openType"));
        logger.debug("request.getAttribute p_wf_openType:" + request.getAttribute("p_wf_openType"));
        
        //业务模块ID
        String wfModuleId = request.getAttribute("p_wf_moduleId") != null ? request.getAttribute("p_wf_moduleId").toString() :"1";//1-自定义表单，其它-业务自定义表单
        logger.debug("request.getAttribute wfModuleId:" + wfModuleId);
        if(request.getAttribute("p_wf_moduleId") == null){
            wfModuleId = request.getParameter("p_wf_moduleId") != null ? request.getParameter("p_wf_moduleId").toString() :"1";
        }
        
        logger.debug("request.getAttribute wfModuleId:" + wfModuleId);
        
        //表单预览
        if("preview".equals(flag)){
            //防止预览页面解析ftl模板不成功
            request.setAttribute("p_wf_cur_ModifyField", "");
        }
        
        String p_wf_openType = request.getAttribute("p_wf_openType") != null ? request.getAttribute("p_wf_openType").toString() :"";
//      p_wf_openType 值 说明：
//      startOpen :   发起
//      modifyView:   办理查阅打开
//      reStart   :   重新发起
//      startAgain:   再次发起
//    
//      waitingDeal:  待办打开
//      waitingRead:  待阅打开
//      readed     :  已阅打开
//      dealed     :  已办打开
//      myTask     :  我的文件打开
        
        //HttpSession session = request.getSession(true);
        String domainId = CommonUtils.getSessionDomainId(request) + "";

        //----------------------------------------------------------------------
        //表单隐藏字段  ,fieldid,fieldid,……
        //$fieldName1$$fieldName2$
        String whir_nodeHiddenField = (String)request.getAttribute("p_wf_concealField");
        if(FormHelper.isEmpty(whir_nodeHiddenField))whir_nodeHiddenField = "";
        
        if("modifyView".equals(p_wf_openType)){//办理查阅-显示隐藏字段
            whir_nodeHiddenField = "";
        }

        //表单写字段，格式：$fieldName1$$fieldName2$
        String whir_nodeWriteField = (String)request.getAttribute("p_wf_cur_ModifyField");
        if(FormHelper.isEmpty(whir_nodeWriteField))whir_nodeWriteField = "";
        //----------------------------------------------------------------------

        UIBD uibd = new UIBD();
        
        //-----------------------------------------------------------------------
        //特殊需求：wfModuleId=16为会议转批，转换为wfModuleId=15为会议申请（OMG！）
        //-----------------------------------------------------------------------        
        FormService formService = new FormService();
        infoId = formService.convertIdByWfModuleId(infoId, wfModuleId);        
        wfModuleId = formService.convertWfModuleId(wfModuleId);
        if(!"1".equals(wfModuleId)){//业务系统自定义表单
            String canModify = request.getParameter("canModify");
            if("1".equals(canModify)){
                String[][] fieldArr = uibd.getFormFields(formId); //读取表单那关联的所有主表字段信息
                if(fieldArr != null && fieldArr.length > 0) {
                    whir_nodeWriteField = "";
                    for(int i = 0, len=fieldArr.length; i < len; i++) {
                        whir_nodeWriteField += "$" + fieldArr[i][3] + "$";
                    }
                    request.setAttribute("p_wf_cur_ModifyField", whir_nodeWriteField);
                }
            }
        }
        //-----------------------------------------------------------------------
        
        //------------------------------------------------------------
        //非工作流程 读写字段、隐藏字段控制(通过自定义模块设置)-start
        //------------------------------------------------------------
        
        //p_wf_processInstanceId 发起时没有，办理环节有该参数
        String processId = (String)request.getAttribute("p_wf_processInstanceId");//request.getParameter(FormContants.WF_PROCESS_ID);
        logger.debug("p_wf_processInstanceId:"+processId);
        
        String _menuId_ = "";
        if(CommonUtils.isEmpty(processId)) {
            _menuId_ = request.getParameter("menuId");
            logger.debug("[ParserHtml]:menuId:"+_menuId_);
            if(!CommonUtils.isEmpty(_menuId_)) {
                whir_nodeWriteField = "";
                try {
                    com.whir.ezoffice.customize.customermenu.bd.CustomerMenuDB
                        _cmbd = new com.whir.ezoffice.customize.customermenu.bd.
                        CustomerMenuDB();
                    List _readfieldList = _cmbd.getFieldControls(_menuId_, "1"); //字段控制类型：0:源字段 1：读字段 2：写字段 3：隐藏字段
                    List _writefieldList = _cmbd.getFieldControls(_menuId_, "2");
                    List _hidenfieldList = _cmbd.getFieldControls(_menuId_, "3");
                    
                    String _readFields = "";
                    String _writeFields = "";
                    if(_readfieldList != null && _readfieldList.size() > 0) {
                        //hide_Field = "";
                        //forHideField = "";
                        for(int i0 = 0, len=_readfieldList.size(); i0 < len; i0++) {
                            List _ff = (List)_readfieldList.get(i0);
                            //hide_Field += "$" + _ff.get(1) + "$"; //只读字段
                            //forHideField += "$" + _ff.get(1) + "$"; //只读字段
                            
                            _readFields += "$" + _ff.get(1) + "$";
                        }
                    }
                    
                    if(_writefieldList != null && _writefieldList.size() > 0) {
                        for(int i0 = 0, len=_writefieldList.size(); i0 < len; i0++) {
                            List _ff = (List)_writefieldList.get(i0);
                            whir_nodeWriteField += "$" + _ff.get(1) + "$"; //可写字段
                            
                            _writeFields += "$" + _ff.get(1) + "$";
                        }
                    }
                    
                    if(_hidenfieldList != null && _hidenfieldList.size() > 0) {
                        whir_nodeHiddenField = "";
                        for(int i0 = 0, len=_hidenfieldList.size(); i0 < len; i0++) {
                            List _ff = (List)_hidenfieldList.get(i0);
                            whir_nodeHiddenField += "$" + _ff.get(1) + "$"; //隐藏字段
                        }
                    }
                    
                    String[][] _mainFields = uibd.getFormMainFields(formId);
                    String[][] _subFields = uibd.getForeignFields(formId);
                    
                    if(CommonUtils.isEmpty(_readFields) && CommonUtils.isEmpty(_writeFields)){//如果没有设置只读和可写，则默认为可写
                        if(_mainFields != null && _mainFields.length>0){
                            for(int i0=0, len=_mainFields.length; i0<len; i0++){
                                whir_nodeWriteField += "$" + _mainFields[i0][3] + "$";
                            }
                        }
                        if(_subFields != null && _subFields.length>0){
                            for(int i0=0, len=_subFields.length; i0<len; i0++){
                                whir_nodeWriteField += "$" + _subFields[i0][3] + "$";
                            }
                        }
                    } else if(!CommonUtils.isEmpty(_readFields) && CommonUtils.isEmpty(_writeFields)){//如果仅设置只读，没有设置可写，则去除只读字段后，其它均可写
                        if(_mainFields != null && _mainFields.length>0){
                            for(int i0=0, len=_mainFields.length; i0<len; i0++){
                                if(_readFields.indexOf("$"+_mainFields[i0][3]+"$")==-1){
                                    whir_nodeWriteField += "$" + _mainFields[i0][3] + "$";
                                }
                            }
                        }
                        if(_subFields != null && _subFields.length>0){
                            for(int i0=0, len=_subFields.length; i0<len; i0++){
                                if(_readFields.indexOf("$"+_subFields[i0][3]+"$")==-1){
                                    whir_nodeWriteField += "$" + _subFields[i0][3] + "$";
                                }
                            }
                        }
                    }
                    
                } catch(Exception e) {
                    e.printStackTrace();
                }
                
                if(!CommonUtils.isEmpty(infoId)){//修改
                    operateType = FormContants.OP_UPDATE;
                }
            }
        }
        //------------------------------------------------------------
        //非工作流程 读写字段、隐藏字段控制(通过自定义模块设置)-end
        //------------------------------------------------------------

        EzFormBD formBD = new EzFormBD();
        FormPO formPO = formBD.loadFormPO(new Long(formId));

        //读取表单设计模板
        final String formContent = FormHelper.isEmpty(formPO.getFormContent())?"":formPO.getFormContent();
        //输出html
        String output = formContent;
        
//        String openType = request.getParameter("openType") == null ? "" :
//            request.getParameter("openType");        
        
        //openType值含义：
        //待办文件 waitingDeal
        //待阅文件 waitingRead
        //已办文件 dealed
        //已阅文件 readed
        //我的文件 myTask
        //我退回的文件 ibacked
        //下属待办文件 myUnderWaitingDeal
        //转交 tran
        //发起流程 startOpen
        //重新发起 resubmit
        //再次发起 submitAgain
        //其它 other

        //设置表单操作类型
        if("view".equals(flag)) {
            operateType = FormContants.OP_VIEW; //2-查看
        } else if("print".equals(flag)) {
            operateType = FormContants.OP_PRINT; //4-打印
        } else if("preview".equals(flag)){
            operateType = FormContants.OP_PREVIEW; //5-预览
        } else {
            if(FormHelper.isEmpty(infoId)) {
                operateType = FormContants.OP_NEW; //0-新增
            } else {
                operateType = FormContants.OP_UPDATE; //1-修改
            }
        }

        boolean isStartOpen = false;//true-新增发起，infoId不空则带数据
        if("waitingDeal".equals(p_wf_openType) || "fromDraft".equals(p_wf_openType)) {//待办、草稿
            operateType = FormContants.OP_UPDATE; //1-修改
        } else if("startOpen".equals(p_wf_openType)) {
            operateType = FormContants.OP_NEW; //0-新增
            isStartOpen = true;
        }else if("waitingRead".equals(p_wf_openType)){
            operateType = FormContants.OP_VIEW;
        }else if("readed".equals(p_wf_openType)){
            operateType = FormContants.OP_VIEW;
        }else if("dealed".equals(p_wf_openType)){
            operateType = FormContants.OP_VIEW;
        }else if("myTask".equals(p_wf_openType)){
            operateType = FormContants.OP_VIEW;
        }else if("modifyView".equals(p_wf_openType)){
            operateType = FormContants.OP_VIEW;
        }else if("relation".equals(p_wf_openType) || "ibacked".equals(p_wf_openType)){//退回、关联流程打开
            operateType = FormContants.OP_VIEW;
        }else if("myUnderWaitingDeal".equals(p_wf_openType)){//下属待办文件打开
            operateType = FormContants.OP_VIEW;
        }

        //引用
        if(!CommonUtils.isEmpty(parentRecordId)){
            operateType = FormContants.OP_UPDATE; //1-修改
        }

        //赋值
        this.setOperateType(operateType);
        
        //----------------------------------------------------------------------
        GenerateHtml gHtml = new GenerateHtml();
        Form form = new Form();
        form.setRequest(request);
        form.setDomainId(Long.parseLong(domainId));
        form.setFormId(formId);
        form.setInfoId(infoId);
        form.setMobile(FormHelper.checkMobileAccess(request)); //是否移动办公
        form.setPad(CommonUtils.isPad(request));
        form.setClientIP(request.getRemoteAddr());
        form.setProcessId(processId);
        form.setWritableFields(whir_nodeWriteField);
        form.setHiddenFields(whir_nodeHiddenField);
        form.setOperateType("reStart".equals(p_wf_openType)?FormContants.OP_RESTART:operateType);
        form.setEzForm(true);
        form.setUserAgent(request.getHeader("User-Agent"));
        
        // 从草稿打开  重新发起
        //String ropenType = request.getAttribute("ropenType") == null? "" : request.getAttribute("ropenType")+"";
        //if("submitAgain".equals(ropenType)){
        if("reStart".equals(p_wf_openType) || "startAgain".equals(p_wf_openType)){
            form.setAgainSubmit(FormContants.OP_AGAINSUBMIT);
        }
        //表单打开方式
        form.setOpenType(p_wf_openType);
        
        //form
        gHtml.setForm(form);
        //formuser
        gHtml.setFormUser(FormHelper.createFormUserWithDB(request));
        gHtml.setHiddenFields(whir_nodeHiddenField);
        //----------------------------------------------------------------------

        EzFormSettingBD sbd = new EzFormSettingBD();
        SettingPO settingPO = null;
        if(FormHelper.isNotEmpty(getRelaTrigId())) {
            settingPO = sbd.loadSettingPO(new Long(getRelaTrigId()), true);
            if(settingPO!=null){
                Set trigSet = settingPO.getTrigSet();
                if(trigSet != null && trigSet.size() > 0) {
                    Map trigMap = new HashMap();
                    Iterator itor = trigSet.iterator();
                    while(itor.hasNext()) {
                        TrigPO tpo = (TrigPO)itor.next();
                        String trigFieldId = String.valueOf(tpo.getTrigFieldId());
                        trigMap.put(trigFieldId, trigFieldId);
                    }
                    gHtml.setRelaTrigMap(trigMap);
                }
                request.setAttribute("settingPO", settingPO);
            }
        }

        //----------------------------------------------------------------------
        //处理字段扩展属性
        //----------------------------------------------------------------------
        if(enabledExtAttr) {
            Map fieldsExtAttrMap = FormHelper.parseFormSettingsXml(formPO.getFormXML());
            gHtml.setFieldsExtAttrMap(fieldsExtAttrMap);
        }
        //----------------------------------------------------------------------

        gHtml.setWfModuleId(wfModuleId);
        gHtml.init(); //初始化

        StringBuffer hideTableHTML = new StringBuffer("");
        StringBuffer showTableHTML = new StringBuffer("");

        StringBuffer js_addTotalRow = new StringBuffer("");
        StringBuffer js_trHidden = new StringBuffer("");

        DbOpt dbopt = null;
        try{
            dbopt = new DbOpt();
            
            //----------------------------------------------------------------------
            //判断当前表单是否新增
            //----------------------------------------------------------------------
            //新增表单
            if(FormHelper.isEmpty(infoId)) { //初始化显示界面(新增)
                logger.debug("打开新表单");
                //如果是发起子流程则如果子流程中的表单与主表单相同则将数据继承
                String parentFormId = request.getParameter("p_wf_pareTableId");//request.getParameter("parentFormId");
                logger.debug("[ParserHtml]:p_wf_pareTableId:"+parentFormId);
                
                
                List list = (List)request.getAttribute("whir_callFieldJICHENList");//子表继承主表数据对应字段关系集合
                //不是相同数据表之间的继承
                if(list!=null && list.size()>0){
                	Map fieldMap = new HashMap();//主子表对应字段关系
                	for(int j=0;j<list.size();j++){
                		Map buttonMap = (Map)list.get(j);
                		String mainFieldname = ""+buttonMap.get("callmainFieldName");//主表字段
                		String subFieldname = ""+buttonMap.get("callsubFieldName");//子表字段
                		fieldMap.put(subFieldname, mainFieldname);
                	}
                	
                	logger.debug("不是相同数据表继承主流程数据");
                    if(operateType != 0 && operateType != 1)whir_nodeWriteField = "NONE";    
                    gHtml.setNewRelation(true);
    
                    //所有字段不可编辑，该种情况表示在自定义模块中点击链接查看信息      formId继承表的id
                    logger.debug("打开继承流程的表单id----------------："+formId);
                    String[][] fieldArr = uibd.getFormFields(formId); //读取子流程的表单id的所有字段信息
                    String[][] mainArr = uibd.getFormFields(parentFormId);
                    String mainTableName = mainArr[0][23];//获取主表名称
                    logger.debug("被继承主表名---------------："+mainTableName);
                    
                    if(fieldArr != null && fieldArr.length > 0) {
                    	logger.debug("继承子表名---------------："+fieldArr[0][23]);
                    	String[][] fieldArr1 = new String[fieldArr.length][4];
                    	for(int i = 0, len=fieldArr.length; i < len; i++) {
                    		if(fieldMap.containsKey(fieldArr[i][3])){
                    			logger.debug("fieldArr[i][3]============"+fieldMap.get(fieldArr[i][3]));
                    			fieldArr1[i][3] = ""+fieldMap.get(fieldArr[i][3]);
                    		}
                    	}
                    	
                        Map dataMap = uibd.getMainDataById(parentRecordId,
                        		mainTableName, fieldArr1, wfModuleId);//将对应关系字段的数据查询出来以map形式保存
                        
                        for(int i = 0, len=fieldArr.length; i < len; i++) { //遍历所有继承子表字段
                        	String fieldDivHTML = "";
                        	if(fieldMap.containsKey(fieldArr[i][3])){
                        		fieldDivHTML = gHtml.generateHtml(parentRecordId,
                                        fieldArr[i],
                                        (String)dataMap.get(fieldArr1[i][3].toLowerCase()),
                                        whir_nodeWriteField,
                                        operateType, 0, dbopt, false); 
                        		logger.debug("value--------------------"+(String)dataMap.get(fieldArr1[i][3].toLowerCase()));
                        		logger.debug("fieldDivHTML--------------:"+fieldDivHTML);
                        	}else{
	                             fieldDivHTML = gHtml.generateHtml(null,
		                                fieldArr[i],
		                                "",
		                                whir_nodeWriteField,
		                                operateType, 0, dbopt, false); //修改-1
                        	}
                            String fieldName = fieldArr[i][3];//字段名
                            boolean isHidden = whir_nodeHiddenField.indexOf("$" +
                                fieldName + "$") >= 0;
                            
                            //output表单设计模板
                            output = FormHelper.parserContent(output,
                                                   fieldArr[i][23] + "-" +
                                                   fieldName,
                                                   fieldDivHTML,
                                                   isHidden, fieldArr[i][15], operateType);
                        }
                    }

                    //获得继承子表单那关联的所有子表
                    String[] tables = uibd.getForeignTables(formId);
                    //获得被继承主表单那关联的所有子表
                    String[] oldTables = uibd.getForeignTables(parentFormId);
                    if(tables != null && tables.length > 0) {
                        for(int i = 0, len=tables.length; i < len; i++) { //遍历所有子表
                            String tableName = tables[i];
                            if(tableName == null || tableName.trim().length() < 1)continue;
                            
                            //提取字表关联的所有字段
                            String[][] forFields = uibd.getForeignFields(formId,
                                tableName, domainId);
                            logger.debug("forFields----------------:"+forFields);
                            boolean hideTable = true; //子表是否有可写字段
                            //拼装SQL
                            String sql = ""; //查询子表关联到表单中的所有字段值的SQL语句
                            int fieldNum=0;
                            List fieldNameFlag = new ArrayList();
                            if(forFields != null && forFields.length > 0) {
                                //boolean hideFlag = false; //子表有不可写字段则不能增加行
                                for(int k = 0, slen=forFields.length; k < slen; k++) { //遍历所有字段
                                    String fieldName = forFields[k][3];
                                    if(fieldMap.containsKey(fieldName)){
                                    	fieldNameFlag.add(fieldName);
                                    	sql += fieldMap.get(fieldName) + ","; //将字段添加到数据查询的SQL中
                                    	fieldNum++;
                                    }
                                    //if(hide_Field.indexOf(forFields[k][0]) < 0) {
                                    logger.debug("whir_nodeWriteField=============="+whir_nodeWriteField);
                                    if(whir_nodeWriteField.indexOf("$" + fieldName + "$") != -1) { //forFields[k][1] - field_id
                                        //如果该子表中有可编辑字段，则该表可编辑
                                        hideTable = false;
                                    }
                                }
    
                                //生成子表是否可编辑的HTML元素，用于数据更新、增加删除行的控制
                                if(hideTable) {
                                    hideTableHTML.append(
                                        "<input type='hidden' name='hideTable' id='hideTable' value='" +
                                        tableName + "'>");
                                } else {
                                    showTableHTML.append(
                                        "<input type='hidden' name='showTable' id='showTable' value='" +
                                        tableName + "'>");
                                }
                                if(sql.length() > 1){
                                	sql = sql.substring(0, sql.length() - 1);
                                }
                            }
    
                            //提取子表数据
                            String[][] data = null;
                            if(sql.length() > 1) {
                            	
                            	String newTableName = "";
                            	String[] sqlField = sql.split(",");
                            	String fieldTableName = sqlField[0].substring(0, sqlField[0].indexOf("_"));
                            	for(int j = 0; j < oldTables.length; j++) { 
                                	String oldTableName = oldTables[j];
                                	if(fieldTableName.equals(oldTableName)){
                                		newTableName = oldTableName;
                                	}
                                }
                            	if("".equals(newTableName)){
                            		continue;
                            	}
                                sql = "select " + sql + "," + newTableName +
                                    "_id from " + newTableName + " where " + newTableName +
                                    "_FOREIGNKEY=" + parentRecordId +
                                    " order by " + newTableName + "_id";
                                data = uibd.loadDataBySQL(sql,
                                    String.valueOf(fieldNum + 1));
                            } 
    
                            //设置页面子表行数
                            if(data != null && data.length > 0) {                            
                                js_addTotalRow.append(
                                        "currentRow = document.getElementById('" +
                                        tableName + "TR');");
    
                                String[] subTablePosition = FormHelper.getTrPosition(output,
                                    tableName + "TR");
                                StringBuffer subTableInnerHtml = new StringBuffer("");    
                                for(int n = 0, dlen=data.length; n < dlen; n++) { //遍历数据，解析数据在表单上的展示
                                    String parserSubTableContent = subTablePosition[2];
                                    //解析数据
                                    for(int m = 0, slen=forFields.length; m < slen; m++) { //遍历字段，生成该字段及其值展示在表单上的脚本
                                        String subDataId = null;
                                        String subDataValue = "";
                                        if(fieldMap.containsKey(forFields[m][3])){
                                        	subDataId = data[n][data[n].length-1];
                                        	int position = fieldNameFlag.indexOf(forFields[m][3]);
                                        	subDataValue = data[n][position];
                                        }
                                        String gSubHtml = gHtml.generateHtml(subDataId, forFields[m],
                                                subDataValue,
                                            whir_nodeWriteField,
                                            operateType, n, dbopt, true);
                                        
                                        
                                        if(m == 0) {
                                            gSubHtml += "<input type='hidden' id='" +
                                                tableName + "id' name='" + tableName +
                                                "id' value='" +
                                                subDataId +
                                                "'>";
                                        }
                                        String fieldName = forFields[m][3];
                                        boolean isHidden = whir_nodeHiddenField.
                                            indexOf(
                                            "$" +
                                            fieldName + "$") >= 0;
                                        logger.debug("子表待替换字段"+forFields[m][23] + "-" + fieldName);
                                        //此处判断调用过程中设置的对应字段关系forFields[m][23] + "-" + fieldName为替换字段
                                        parserSubTableContent =
                                            FormHelper.parserContent(parserSubTableContent,
                                            forFields[m][23] + "-" + fieldName,
                                            gSubHtml,
                                            isHidden, forFields[m][15], operateType); //修改-1
                                    }
                                    subTableInnerHtml.append(parserSubTableContent);
                                }
                                output = FormHelper.mergeHtml(output,
                                                   subTablePosition[0],
                                                   subTablePosition[1],
                                                   subTableInnerHtml.toString());
    
                            } else { //该子表没有数据，隐藏模板中初始化的第一个数据行
                                for(int m = 0, slen=forFields.length; m < slen; m++) {
                                    String fieldDivHTML = gHtml.generateHtml(null,
                                        forFields[m],
                                        "", whir_nodeWriteField,
                                        operateType, 0, dbopt, true); //新增-0
                                    String fieldName = forFields[m][3];
                                    boolean isHidden = whir_nodeHiddenField.indexOf(
                                        "$" +
                                        fieldName +
                                        "$") >= 0;
                                    output = FormHelper.parserContent(output,
                                        forFields[m][23] + "-" + fieldName,
                                        fieldDivHTML,
                                        isHidden, forFields[m][23], operateType);
                                }
                                logger.debug("hideTable================"+hideTable);
                                //调用过程继承主表时  子表无数据  页面默认显示一条空记录  此时下面if注释掉
                                /*if(hideTable){
                                    js_trHidden.append(
                                        "document.getElementById('" +
                                        tableName +
                                        "TR').style.display='none';");
                                }*/
                            }
    
                            //设置统计字段
                            js_addTotalRow.append(getTotalFieldHtml(tableName,
                                domainId));
                        }
                    }
                	
                //相同数据表继承
	            }else{
	                
	                boolean isNewForm = true;
	                if(FormHelper.isNotEmpty(parentFormId)) {
	                    //取得父表单ID则比较父表单ID是否与当前表单ID相同，如果相同的话，则继承主表单数据
	                    isNewForm = uibd.isNewForm(parentFormId, formId);
	                }
	    
	                if(CommonUtils.isEmpty(parentRecordId) && isNewForm) { //新表单
	                    //取得表单关联的所有字段信息的数据
	                    String[][] fieldArr = uibd.getFormFields(formId);
	                    if(fieldArr != null) {
	                        //遍历所有字段，获得字段的页面显示脚本
	                        for(int i = 0, len=fieldArr.length; i < len; i++) {
	                            String fieldDivHTML = gHtml.generateHtml(null,
	                                fieldArr[i], "", whir_nodeWriteField, operateType,
	                                0, dbopt, false); //新增-0
	                            String fieldName = fieldArr[i][3];
	                            boolean isHidden = whir_nodeHiddenField.indexOf("$" +
	                                fieldName + "$") >= 0;
	    
	                            output = FormHelper.parserContent(output,
	                                                   fieldArr[i][23] + "-" +
	                                                   fieldName,
	                                                   fieldDivHTML,
	                                                   isHidden, fieldArr[i][15], operateType);
	                        }
	                    }
	    
	                    //获得表单关联的所有子表字段
	                    String[][] forFieldArr = uibd.getForeignFields(formId);
	                    if(forFieldArr != null) {
	                        //遍历所有字段，获得字段的页面显示脚本
	                        for(int i = 0, slen=forFieldArr.length; i < slen; i++) {
	                            String fieldDivHTML = gHtml.generateHtml(null,
	                                forFieldArr[i], "", whir_nodeWriteField,
	                                operateType, 0, dbopt, true); //新增-0
	                            String fieldName = forFieldArr[i][3];
	                            boolean isHidden = whir_nodeHiddenField.indexOf("$" +
	                                fieldName + "$") >= 0;
	    
	                            output = FormHelper.parserContent(output,
	                                                   forFieldArr[i][23] + "-" +
	                                                   fieldName,
	                                                   fieldDivHTML,
	                                                   isHidden, forFieldArr[i][15], operateType);
	                        }
	                    }
	    
	                    //获得表单关联的所有子表数组
	                    String[] tables = uibd.getForeignTables(formId);
	                    if(tables != null && tables.length > 0) {
	                        //遍历所有子表
	                        for(int i = 0, len=tables.length; i < len; i++) {
	                            String tableName = tables[i];
	                            if(tableName == null || tableName.trim().length() < 1)continue;
	                            //---------------------------------------------------------------
	                            //提取字表关联的所有字段
	                            String[][] forFields = uibd.getForeignFields(formId,
	                                tableName, domainId);
	                            if(forFields != null && forFields.length > 0) {
	                                boolean hideTable = true; //子表是否有可写字段
	                                for(int k = 0, slen=forFields.length; k < slen; k++) {
	                                    String fieldName = forFields[k][3];
	                                    //遍历所有字段
	                                    if(whir_nodeWriteField.indexOf("$" + fieldName + "$") != -1) {
	                                        //如果该子表中有可编辑字段，则该表可编辑
	                                        hideTable = false;
	                                        break;
	                                    }
	                                }
	    
	                                //生成子表是否可编辑的HTML元素，用于数据更新、增加删除行的控制
	                                if(hideTable) {
	                                    hideTableHTML.append(
	                                        "<input type='hidden' name='hideTable' id='hideTable' value='" +
	                                        tableName + "'>");
	                                } else {
	                                    showTableHTML.append(
	                                        "<input type='hidden' name='showTable' id='showTable' value='" +
	                                        tableName + "'>");
	                                }
	                            }
	                            //---------------------------------------------------------------
	    
	                            //设置统计字段
	                            js_addTotalRow.append(getTotalFieldHtml(tableName,
	                                domainId));
	                        }
	                    }
	                } else { //子流程表单与主流程表单用的是同一张数据表，取主流程中的数据填充表单
	                    //当前表单为编辑表单（表单已有对应的数据）
	                    //新增子流程的继承主流程数据
	                    //String forHideField = "";//字表不可编辑字段
	                    logger.debug("新增子流程的继承主流程数据");
	    
	                    if(operateType != 0 && operateType != 1)whir_nodeWriteField = "NONE";    
	                    
	                    logger.debug("[ParserHtml]:p_wf_pareRecordId:"+parentRecordId);//父数据表中数据唯一标识id
	                    
	                    gHtml.setNewRelation(true);
	    
	                    //所有字段不可编辑，该种情况表示在自定义模块中点击链接查看信息      formId继承表的id
	                    logger.debug("自定义表单id----------------："+formId);
	                    String[][] fieldArr = uibd.getFormFields(formId); //读取继承表那关联的所有字段信息
	                    if(fieldArr != null && fieldArr.length > 0) {
	                    	logger.debug("表名---------------："+fieldArr[0][23]);
	                    	//此处将有对应关系的数据查询出来添加进dataMap对于没有对应关系的数据则添加空字符
	                        Map dataMap = uibd.getMainDataById(parentRecordId,
	                            fieldArr[0][23], fieldArr, wfModuleId);//将继承表中每个字段的数据查询出来以map形式保存
	                        
	                        for(int i = 0, len=fieldArr.length; i < len; i++) { //遍历所有继承表字段
	                            String fieldDivHTML = gHtml.generateHtml(parentRecordId,
	                                fieldArr[i],
	                                (String)dataMap.get(fieldArr[i][3].toLowerCase()),
	                                whir_nodeWriteField,
	                                operateType, 0, dbopt, false); //修改-1
	                            String fieldName = fieldArr[i][3];//字段名
	                            boolean isHidden = whir_nodeHiddenField.indexOf("$" +
	                                fieldName + "$") >= 0;
	                            logger.debug("fieldDivHTML--------------:"+fieldDivHTML);
	                            logger.debug("待替换字段"+fieldArr[i][23] + "-" +fieldName);//div的id
	                            //output表单设计模板
	                            output = FormHelper.parserContent(output,
	                                                   fieldArr[i][23] + "-" +
	                                                   fieldName,
	                                                   fieldDivHTML,
	                                                   isHidden, fieldArr[i][15], operateType);
	                        }
	                    }
	                    
	                    //获得表单那关联的所有子表
	                    String[] tables = uibd.getForeignTables(formId);
	                    if(tables != null && tables.length > 0) {
	                        for(int i = 0, len=tables.length; i < len; i++) { //遍历所有子表
	                            String tableName = tables[i];
	                            if(tableName == null || tableName.trim().length() < 1)continue;
	    
	                            //提取字表关联的所有字段
	                            String[][] forFields = uibd.getForeignFields(formId,
	                                tableName, domainId);
	                            logger.debug("forFields----------------:"+forFields);
	                            boolean hideTable = true; //子表是否有可写字段
	                            //拼装SQL
	                            String sql = ""; //查询子表关联到表单中的所有字段值的SQL语句
	                            if(forFields != null && forFields.length > 0) {
	                                //boolean hideFlag = false; //子表有不可写字段则不能增加行
	                                for(int k = 0, slen=forFields.length; k < slen; k++) { //遍历所有字段
	                                    String fieldName = forFields[k][3];
	                                    sql += fieldName + ","; //将字段添加到数据查询的SQL中
	                                    //if(hide_Field.indexOf(forFields[k][0]) < 0) {
	                                    logger.debug("whir_nodeWriteField=============="+whir_nodeWriteField);
	                                    if(whir_nodeWriteField.indexOf("$" + fieldName + "$") != -1) { //forFields[k][1] - field_id
	                                        //如果该子表中有可编辑字段，则该表可编辑
	                                        hideTable = false;
	                                    }
	                                }
	    
	                                //生成子表是否可编辑的HTML元素，用于数据更新、增加删除行的控制
	                                if(hideTable) {
	                                    hideTableHTML.append(
	                                        "<input type='hidden' name='hideTable' id='hideTable' value='" +
	                                        tableName + "'>");
	                                } else {
	                                    showTableHTML.append(
	                                        "<input type='hidden' name='showTable' id='showTable' value='" +
	                                        tableName + "'>");
	                                }
	                                sql = sql.substring(0, sql.length() - 1);
	                            }
	    
	                            //提取子表数据
	                            String[][] data = null;
	                            if(sql.length() > 1) {
	                                sql = "select " + sql + "," + tableName +
	                                    "_id from " + tableName + " where " + tableName +
	                                    "_FOREIGNKEY=" + parentRecordId +
	                                    " order by " + tableName + "_id";
	                                data = uibd.loadDataBySQL(sql,
	                                    String.valueOf(forFields.length + 1));
	                            } else {
	                                continue;
	                            }
	    
	                            //设置页面子表行数
	                            if(data != null && data.length > 0) {                            
	                                js_addTotalRow.append(
	                                        "currentRow = document.getElementById('" +
	                                        tableName + "TR');");
	    
	                                String[] subTablePosition = FormHelper.getTrPosition(output,
	                                    tableName + "TR");
	                                StringBuffer subTableInnerHtml = new StringBuffer("");    
	                                for(int n = 0, dlen=data.length; n < dlen; n++) { //遍历数据，解析数据在表单上的展示
	                                    String parserSubTableContent = subTablePosition[
	                                        2];
	                                    //解析数据
	                                    for(int m = 0, slen=forFields.length; m < slen; m++) { //遍历字段，生成该字段及其值展示在表单上的脚本
	                                        String subDataId = data[n][slen];
	                                        String subDataValue = data[n][m];
	                                        String gSubHtml = gHtml.generateHtml(subDataId, forFields[m],
	                                                subDataValue,
	                                            whir_nodeWriteField,
	                                            operateType, n, dbopt, true);
	                                        if(m == 0) {
	                                            gSubHtml += "<input type='hidden' id='" +
	                                                tableName + "id' name='" + tableName +
	                                                "id' value='" +
	                                                subDataId +
	                                                "'>";
	                                        }
	                                        String fieldName = forFields[m][3];
	                                        boolean isHidden = whir_nodeHiddenField.
	                                            indexOf(
	                                            "$" +
	                                            fieldName + "$") >= 0;
	                                        logger.debug("子表待替换字段"+forFields[m][23] + "-" + fieldName);
	                                        //此处判断调用过程中设置的对应字段关系forFields[m][23] + "-" + fieldName为替换字段
	                                        parserSubTableContent =
	                                            FormHelper.parserContent(parserSubTableContent,
	                                            forFields[m][23] + "-" + fieldName,
	                                            gSubHtml,
	                                            isHidden, forFields[m][15], operateType); //修改-1
	                                    }
	                                    subTableInnerHtml.append(parserSubTableContent);
	                                }
	                                output = FormHelper.mergeHtml(output,
	                                                   subTablePosition[0],
	                                                   subTablePosition[1],
	                                                   subTableInnerHtml.toString());
	    
	                            } else { //该子表没有数据，隐藏模板中初始化的第一个数据行
	                                for(int m = 0, slen=forFields.length; m < slen; m++) {
	                                    String fieldDivHTML = gHtml.generateHtml(null,
	                                        forFields[m],
	                                        "", whir_nodeWriteField,
	                                        operateType, 0, dbopt, true); //新增-0
	                                    String fieldName = forFields[m][3];
	                                    boolean isHidden = whir_nodeHiddenField.indexOf(
	                                        "$" +
	                                        fieldName +
	                                        "$") >= 0;
	                                    output = FormHelper.parserContent(output,
	                                        forFields[m][23] + "-" + fieldName,
	                                        fieldDivHTML,
	                                        isHidden, forFields[m][23], operateType);
	                                }
	                                if(hideTable){
	                                    js_trHidden.append(
	                                        "document.getElementById('" +
	                                        tableName +
	                                        "TR').style.display='none';");
	                                }
	                            }
	    
	                            //设置统计字段
	                            js_addTotalRow.append(getTotalFieldHtml(tableName,
	                                domainId));
	                        }
	                    }
	                }
	            }
                //--------------------------------------------------------------------------
                //当前表单为编辑表单
                //--------------------------------------------------------------------------
            } else {
                //当前表单为编辑表单（表单已有对应的数据）
                logger.debug("打开编辑表单");
                //编辑表单
                if(operateType != 0 && operateType != 1)whir_nodeWriteField = "NONE";
                
                if("print".equals(flag))whir_nodeWriteField = "NONE";
                
                if(isStartOpen) gHtml.setNewRelation(true);
    
                String _infoId = infoId;
    
                //所有字段不可编辑，该种情况表示在自定义模块中点击链接查看信息
                String[][] fieldArr = uibd.getFormFields(formId); //读取表单那关联的所有主表字段信息
                if(fieldArr != null && fieldArr.length > 0) {                    
                    Map dataMap = uibd.getMainDataById(_infoId, fieldArr[0][23],
                        fieldArr, wfModuleId);
                    for(int i = 0, len=fieldArr.length; i < len; i++) { //遍历所有主表字段
                        String fieldDivHTML = gHtml.
                            generateHtml(_infoId, fieldArr[i],
                                         (String)
                                         dataMap.get(fieldArr[i][3].
                                                     toLowerCase()),
                                         whir_nodeWriteField,
                                         operateType, 0, dbopt, false); //修改-1
                        String fieldName = fieldArr[i][3];
                        boolean isHidden = whir_nodeHiddenField.indexOf("$" +
                            fieldName + "$") >= 0;
    
                        output = FormHelper.parserContent(output,
                                               fieldArr[i][23] + "-" + fieldName,
                                               fieldDivHTML,
                                               isHidden, fieldArr[i][15], operateType);
                    }
                }
    
                //获得表单那关联的所有子表
                String[] tables = uibd.getForeignTables(formId);
                if(tables != null && tables.length > 0) {
                    for(int i = 0, len=tables.length; i < len; i++) { //遍历所有子表
                        String tableName = tables[i];
                        if(tableName == null || tableName.trim().length() < 1)continue;
    
                        //提取子表关联的所有字段
                        String[][] forFields = uibd.getForeignFields(formId,
                            tableName, domainId);
                        boolean hideTable = true; //子表是否有可写字段
                        //拼装SQL
                        String sql = ""; //查询子表关联到表单中的所有字段值的SQL语句
                        if(forFields != null && forFields.length > 0) {
                            //boolean hideFlag = false; //子表有不可写字段则不能增加行
                            for(int k = 0, slen=forFields.length; k < slen; k++) { //遍历所有字段
                                String fieldName = forFields[k][3];
                                sql += fieldName + ","; //将字段添加到数据查询的SQL中
                                if(whir_nodeWriteField.indexOf("$" + fieldName + "$") != -1) {
                                    //如果该子表中有可编辑字段，则该表可编辑
                                    hideTable = false;
                                }
                            }
    
                            //生成子表是否可编辑的HTML元素，用于数据更新、增加删除行的控制
                            if(hideTable) {
                                hideTableHTML.append(
                                    "<input type='hidden' name='hideTable' id='hideTable' value='" +
                                    tableName + "'>");
                            } else {
                                showTableHTML.append(
                                    "<input type='hidden' name='showTable' id='showTable' value='" +
                                    tableName + "'>");
                            }
                            sql = sql.substring(0, sql.length() - 1);
                        }
    
                        //提取子表数据
                        String[][] data = null;
                        if(sql.length() > 1) {
                            sql = "select " + sql + "," + tableName + "_id from " +
                                tableName + " where " + tableName + "_FOREIGNKEY=" +
                                _infoId + " order by " + tableName + "_id";
                            data = uibd.loadDataBySQL(sql,
                                                      String.valueOf(forFields.
                                length +
                                1));
                        } else {
                            continue;
                        }

                        long stime = System.currentTimeMillis();
                        logger.debug("计算子表开始："+stime);
                        //设置页面子表行数
                        if(data != null && data.length > 0) {
                            js_addTotalRow.append(
                                    "currentRow = document.getElementById('" +
                                    tableName + "TR');");
    
                            String[] subTablePosition = FormHelper.getTrPosition(output,
                                tableName + "TR");
                            StringBuffer subTableInnerHtml = new StringBuffer("");
                            for(int n = 0, dlen=data.length; n < dlen; n++) { //遍历数据，解析数据在表单上的展示
                                String parserSubTableContent = subTablePosition[2];
                                //解析数据
                                for(int m = 0, slen=forFields.length; m < slen; m++) { //遍历字段，生成该字段及其值展示在表单上的脚本
                                    String subDataId = data[n][slen];
                                    String subDataValue = data[n][m];
                                    String gSubHtml = gHtml.generateHtml(subDataId, forFields[m], subDataValue,
                                        whir_nodeWriteField,
                                        operateType, n, dbopt, true);
                                    if(m == 0) {
                                        gSubHtml += "<input type='hidden' id='" +
                                            tableName + "id' name='" + tableName +
                                            "id' value='" + subDataId +
                                            "'>";
                                    }
                                    String fieldName = forFields[m][3];
                                    boolean isHidden = whir_nodeHiddenField.indexOf(
                                        "$" +
                                        fieldName + "$") >= 0;
                                    parserSubTableContent =
                                        FormHelper.parserContent(parserSubTableContent,
                                                      forFields[m][23] + "-" +
                                                      fieldName, gSubHtml,
                                                      isHidden, forFields[m][15], operateType); //修改-1
                                }
                                subTableInnerHtml.append(parserSubTableContent);
                            }
    
                            output = FormHelper.mergeHtml(output, subTablePosition[0],
                                               subTablePosition[1],
                                               subTableInnerHtml.toString());
                            
                            logger.debug("计算子表结束："+ (System.currentTimeMillis() - stime));
    
                        } else { //该子表没有数据，隐藏模板中初始化的第一个数据行
                            for(int m = 0, slen=forFields.length; m < slen; m++) {
                                String fieldDivHTML = gHtml.generateHtml(null,
                                    forFields[m], "", whir_nodeWriteField,
                                    operateType, 0, dbopt, true); //新增-0
                                String fieldName = forFields[m][3];
                                boolean isHidden = whir_nodeHiddenField.indexOf("$" +
                                    fieldName +
                                    "$") >= 0;
                                output = FormHelper.parserContent(output,
                                    forFields[m][23] + "-" + fieldName,
                                    fieldDivHTML,
                                    isHidden, forFields[m][15], operateType);
                            }
                          
                            if(hideTable){
                                js_trHidden.append("document.getElementById('" +
                                                   tableName +
                                                   "TR').style.display='none';");
                            }
                        }
    
                        //设置统计字段
                        js_addTotalRow.append(getTotalFieldHtml(tableName, domainId));
                    }
                }
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt != null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        output += "\n";
        output += hideTableHTML.toString();
        output += showTableHTML.toString();

        if(!"".equals(js_trHidden.toString().trim()) ||
           !"".equals(js_addTotalRow.toString().trim())) {
            output += "\n";
            output += "<script language='javascript'>";
            output += "\n";
            output += js_trHidden.toString();
            output += "\n";
            output += js_addTotalRow.toString();
            output += "\n";
            output += "</script>";
        }
        
        if(!CommonUtils.isEmpty(wfModuleId)){
            output += "\n<script src=\""+request.getContextPath()+"/modulesext/js/"+wfModuleId+"/custom_"+wfModuleId+".js\" type=\"text/javascript\"></script>";
        }

        long endTime = System.currentTimeMillis();
        logger.debug("[ParserHtml]:end--:" + (endTime - startTime));

        return output;
    }

    /**
     *
     * @param tableName String
     * @param domainId String
     * @return String
     */
    private String getTotalFieldHtml(String tableName, String domainId) {
        StringBuffer result = new StringBuffer();
        UIBD uibd = new UIBD();
        //设置统计字段
        String[][] tlFlds = uibd.getTotalFieldByTableName(tableName,
            domainId);
        if(tlFlds != null && tlFlds.length > 0) { //增加统计字段显示行
            String tlfld = "";
            String totaltd = "";
            for(int p = 0, len=tlFlds.length; p < len; p++) { //遍历统计字段，设置对应的统计字段表单元素
                tlfld += tlFlds[p][0] + ",";
                totaltd += tlFlds[p][1] + ":<label id=" +
                    tlFlds[p][0] + "totallabel></label>";
            }
            
            result.append("addTotalRow('" + tableName + "','" +
                          totaltd +
                          "<input type=hidden id=totalField name=totalField value=" +
                          tlfld + ">',true);");
        }
        
        return result.toString();
    }
}
