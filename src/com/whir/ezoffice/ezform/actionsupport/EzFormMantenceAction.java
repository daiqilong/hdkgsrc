package com.whir.ezoffice.ezform.actionsupport;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.ezoffice.ezform.bd.EzFormBD;
import com.whir.ezoffice.ezform.bd.FormDataBD;
import com.whir.ezoffice.ezform.po.FormPO;
import com.whir.ezoffice.ezform.ui.UIBD;
import com.whir.ezoffice.ezform.util.FormHelper;
import com.whir.ezoffice.formhandler.runtime.RuntimeProcessFactory;

/**
 * 新自定义表单模块新增、修改
 * 
 * @author wanghx
 * 
 */
public class EzFormMantenceAction extends BaseActionSupport {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(EzFormMantenceAction.class
            .getName());
    
    private RuntimeProcessFactory factory = RuntimeProcessFactory.getInstance();

    public static final String MODULE_CODE = "EzFormMantenceAction";

    private String menuId;
    private String formId;
    private String operate;// add-新增 modify-修改
    private String recordId;
    private String moduleType;// customizeAdd,customizeModi
    
    /**
     * 根据formCode获取formId
     * 
     * @return formId
     */
    public String getFormIdByFormCode(){
        String result = null;
        
        String domainId = CommonUtils.getSessionDomainId(request) + "";
        
        String flag = request.getParameter("flag");
        String formCode = request.getParameter("formCode");//表单code
        FormPO formPO = null;
        
        if(!CommonUtils.isEmpty(formCode)){
            EzFormBD fbd = new EzFormBD();
            if("print".equals(flag)){//打印表单
                formPO = fbd.getPrintFormPOByFormCode(formCode, domainId);
            }else{
                formPO = fbd.getFormPOByFormCode(formCode, domainId);
            }
        }
        
        if(formPO != null){
            result = formPO.getFormId() + "";
        }
        
        return result;
    }

    /**
     * 打开自定义模块新增页面
     * 
     * @return
     * @throws Exception
     */
    public String addCustomMantence() throws Exception {
        
        if(CommonUtils.isEmpty(formId)){
            formId = getFormIdByFormCode();
        }
        
        if(CommonUtils.isEmpty(formId, true)){
            return this.NO_RIGHTS;
        }
        
        FormPO fpo = new EzFormBD().loadFormPO(new Long(formId));        
        String formCode = fpo.getFormCode();
        
        request.setAttribute("formCode", formCode);
        
        setWfModuleId(fpo.getModulePO().getWfModuleId()+"");
        
        request.setAttribute("p_wf_openType", "startOpen");

        // request.setAttribute("p_wf_concealField", p_wf_concealField);
        // request.setAttribute("p_wf_cur_ModifyField", p_wf_cur_ModifyField);
        // request.setAttribute("p_wf_processInstanceId",
        // p_wf_processInstanceId);
        
        request.setAttribute("_mantenctType", "add");
        
        operate = "add";

        return "addCustomMantence";
    }

    /**
     * 打开自定义模块修改页面
     * 
     * @return
     * @throws Exception
     */
    public String loadCustomMantence() throws Exception {
        
        if(CommonUtils.isEmpty(formId)){
            formId = getFormIdByFormCode();
        }
        
        if(CommonUtils.isEmpty(formId, true)){
            return this.NO_RIGHTS;
        }
        
        if(CommonUtils.isEmpty(recordId, true)){
            return this.NO_RIGHTS;
        }
        
        FormPO fpo = new EzFormBD().loadFormPO(new Long(formId));        
        String formCode = fpo.getFormCode();
        
        request.setAttribute("formCode", formCode);
        
        setWfModuleId(fpo.getModulePO().getWfModuleId()+"");

        request.setAttribute("infoId", recordId);
        request.setAttribute("p_wf_recordId", recordId);
        request.setAttribute("p_wf_openType", "waitingDeal");

        // request.setAttribute("p_wf_concealField", p_wf_concealField);
        // request.setAttribute("p_wf_cur_ModifyField", p_wf_cur_ModifyField);
        // request.setAttribute("p_wf_processInstanceId",
        // p_wf_processInstanceId);
        
        request.setAttribute("_mantenctType", "modify");

        operate = "modify";

        return "loadCustomMantence";
    }
    
    /**
     * 打开自定义模块查看页面
     * 
     * @return
     * @throws Exception
     */
    public String loadCustomMantenceV() throws Exception {
        if(CommonUtils.isEmpty(formId)){
            formId = getFormIdByFormCode();
        }
        
        //
        loadCustomMantence();
        
        request.setAttribute("p_wf_openType", "modifyView");//设置页面查看，字段只读
        
        request.setAttribute("_mantenctType", "view");

        return "loadCustomMantence";
    }

    /**
     * 新增自定义模块数据
     * 
     * @return
     * @throws Exception
     */
    public String saveCustomMantence() throws Exception {

        HttpSession session = request.getSession(true);
        
        java.util.Date date = new java.util.Date();
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String userOrgName = session.getAttribute("orgName").toString();
        String domainId = session.getAttribute("domainId").toString();
        String userIP = session.getAttribute("userIP").toString();
        String result = "";

        boolean isLoged = false;//记录日志
        if(!CommonUtils.isEmpty(menuId) && !"-1".equals(menuId)){
            com.whir.ezoffice.customize.customermenu.bd.CustMenuWithOriginalBD _cmbd = new com.whir.ezoffice.customize.customermenu.bd.CustMenuWithOriginalBD();
            com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO _customerMenuConfigerPO = (com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO) _cmbd
                    .loadMenuSetById(menuId, domainId);
            result = _customerMenuConfigerPO.getMenuName();
            isLoged = true;//来自自定义模块
        }

        EzFormBD formBD = new EzFormBD();
        FormPO formPO = formBD.loadFormPO(new Long(formId));

        String formContent = FormHelper.isEmpty(formPO.getFormContent()) ? ""
                : formPO.getFormContent();

        com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
        if (!CommonUtils.isEmpty(formContent)) {
            if ("add".equals(operate)) {

                String infoId = new FormDataBD()
                        .save(request, new Integer(100));
                if (!CommonUtils.isEmpty(infoId)) {
                    factory.invokeRuntimeProcess("save", infoId,
                            formId,
                            request, new Integer(100));
                    
                    if(isLoged){
                        java.util.Date endDate = new java.util.Date();
    
                        logBD.log(userId, userName, userOrgName,
                                "oa_customize_data", "自定义模块-数据", date, endDate,
                                "1", result, userIP, domainId);
                    }
                    
                    printResult(G_SUCCESS);
                }

            } else if ("modify".equals(operate)) {// 修改自定义模块数据
                boolean ret = new FormDataBD()
                        .update(request, new Integer(100));
                if (ret) {
                    String infoId = request.getParameter("infoId");
                    factory.invokeRuntimeProcess("update", infoId,
                            formId,
                            request, new Integer(100));
                    
                    if(isLoged){
                        java.util.Date endDate = new java.util.Date();
                        logBD.log(userId, userName, userOrgName,
                                "oa_customize_data", "自定义模块-数据", date, endDate,
                                "2", result, userIP, domainId);
                    }
                    
                    printResult(G_SUCCESS);
                }
            }
        }

        return null;
    }

    /**
     * 打开自定义模块新增页面（字段可写）
     * 
     * @return
     * @throws Exception
     */
    public String addCustomMantenceW() throws Exception {

        if(CommonUtils.isEmpty(formId)){
            formId = getFormIdByFormCode();
        }
        
        if(CommonUtils.isEmpty(formId, true)){
            return this.NO_RIGHTS;
        }
        
        FormPO fpo = new EzFormBD().loadFormPO(new Long(formId));
        String formCode = fpo.getFormCode();
        
        request.setAttribute("formCode", formCode);
        
        setWfModuleId(fpo.getModulePO().getWfModuleId()+"");
        
        request.setAttribute("p_wf_openType", "startOpen");
        
        //写字段
        request.setAttribute("p_wf_cur_ModifyField", getWriteFields());

        // request.setAttribute("p_wf_concealField", p_wf_concealField);
        // request.setAttribute("p_wf_cur_ModifyField", p_wf_cur_ModifyField);
        // request.setAttribute("p_wf_processInstanceId",
        // p_wf_processInstanceId);
        
        request.setAttribute("_mantenctType", "add");
        
        operate = "add";

        return "addCustomMantence";
    }

    /**
     * 打开自定义模块修改页面（字段可写）
     * 
     * @return
     * @throws Exception
     */
    public String loadCustomMantenceW() throws Exception {
        
        if(CommonUtils.isEmpty(formId)){
            formId = getFormIdByFormCode();
        }
        
        if(CommonUtils.isEmpty(formId, true)){
            return this.NO_RIGHTS;
        }
        
        if(CommonUtils.isEmpty(recordId, true)){
            return this.NO_RIGHTS;
        }
        
        FormPO fpo = new EzFormBD().loadFormPO(new Long(formId));        
        String formCode = fpo.getFormCode();
        
        request.setAttribute("formCode", formCode);
        
        setWfModuleId(fpo.getModulePO().getWfModuleId()+"");

        request.setAttribute("infoId", recordId);
        request.setAttribute("p_wf_recordId", recordId);
        request.setAttribute("p_wf_openType", "waitingDeal");
        
        //写字段
        request.setAttribute("p_wf_cur_ModifyField", getWriteFields());

        // request.setAttribute("p_wf_concealField", p_wf_concealField);
        // request.setAttribute("p_wf_cur_ModifyField", p_wf_cur_ModifyField);
        // request.setAttribute("p_wf_processInstanceId",
        // p_wf_processInstanceId);
        
        request.setAttribute("_mantenctType", "modify");
        
        menuId = "-1";

        operate = "modify";

        return "loadCustomMantence";
    }

    /**
     * 打开自定义模块查看页面（字段不可写）
     * 
     * @return
     * @throws Exception
     */
    public String loadCustomMantenceR() throws Exception {
        
        if(CommonUtils.isEmpty(formId)){
            formId = getFormIdByFormCode();
        }
        
        if(CommonUtils.isEmpty(formId, true)){
            return this.NO_RIGHTS;
        }
        
        if(CommonUtils.isEmpty(recordId, true)){
            return this.NO_RIGHTS;
        }
        
        FormPO fpo = new EzFormBD().loadFormPO(new Long(formId));        
        String formCode = fpo.getFormCode();
        
        request.setAttribute("formCode", formCode);

        request.setAttribute("infoId", recordId);
        request.setAttribute("p_wf_recordId", recordId);
        request.setAttribute("p_wf_openType", "waitingDeal");
        
        //写字段
        //request.setAttribute("p_wf_cur_ModifyField", getWriteFields());

        // request.setAttribute("p_wf_concealField", p_wf_concealField);
        // request.setAttribute("p_wf_cur_ModifyField", p_wf_cur_ModifyField);
        // request.setAttribute("p_wf_processInstanceId",
        // p_wf_processInstanceId);
        
        request.setAttribute("_mantenctType", "view");
        
        setWfModuleId(fpo.getModulePO().getWfModuleId()+"");
        
        menuId = "-1";

        operate = "modify";

        return "loadCustomMantence";
    }

    /**
     * 获取表单可写字段
     * 
     * @return String 格式：$aaa$$bbb$
     */
    public String getWriteFields() {
        UIBD uibd = new UIBD();

        //根据表单ID获取主表字段
        String[][] _mainFields = uibd.getFormMainFields(formId);
        //根据表单ID获取子表单字段
        String[][] _subFields = uibd.getForeignFields(formId);

        String whir_nodeWriteField = "";
        if (_mainFields != null && _mainFields.length > 0) {
            for (int i0 = 0, len = _mainFields.length; i0 < len; i0++) {
                whir_nodeWriteField += "$" + _mainFields[i0][3] + "$";
            }
        }
        if (_subFields != null && _subFields.length > 0) {
            for (int i0 = 0, len = _subFields.length; i0 < len; i0++) {
                whir_nodeWriteField += "$" + _subFields[i0][3] + "$";
            }
        }

        return whir_nodeWriteField;
    }
    
    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }
    
    private void setWfModuleId(String wfModuleId) {
        if(!CommonUtils.isEmpty(wfModuleId)){
            request.setAttribute("p_wf_moduleId", wfModuleId);
        }
    }
}
