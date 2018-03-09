package com.whir.service.parse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.ezoffice.information.channelmanager.bd.ChannelBD;
import com.whir.ezoffice.information.channelmanager.bd.NewChannelBD;
import com.whir.ezoffice.information.channelmanager.po.InformationChannelPO;
import com.whir.ezoffice.information.channelmanager.po.MyDisplayChannelPO;
import com.whir.ezoffice.information.infomanager.bd.InformationAccessoryBD;
import com.whir.ezoffice.information.infomanager.bd.InformationBD;
import com.whir.ezoffice.information.infomanager.bd.NewInformationBD;
import com.whir.ezoffice.information.infomanager.po.InformationPO;
import com.whir.ezoffice.microblog.util.CutPhotoSize;
import com.whir.ezoffice.portal.bd.PortletBD;
import com.whir.govezoffice.documentmanager.bd.SenddocumentBD;
import com.whir.service.api.information.InformationService;
import com.whir.service.common.AbstractParse;

/**
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: WanHu Internet Resource(Hefei) C0. Ltd</p>
 * @author not attributable
 * @version 1.0
 */
public class InformationParse  extends AbstractParse {
    public InformationParse(Document doc) {
       super(doc);
    }
    
    /**
     * 取所有栏目
     * @return
     */
    public String getChannel(){
    	String result="";
        Element rootElement = doc.getRootElement();
        Element domainElement = rootElement.getChild("domain");
        Element pagerOffsetElement = rootElement.getChild("pager_offset");
        Element searchChannelNameElement = rootElement.getChild("searchChannelName");

        String domainId = domainElement.getValue();
        String pager_offset=pagerOffsetElement.getValue();
        String  searchChannelName=searchChannelNameElement==null?"":searchChannelNameElement.getValue();

        NewChannelBD bd=new NewChannelBD();
        List list=new ArrayList();
        List resultList = new ArrayList();
        Map map = null;
        try {
        	map = bd.getChannel(domainId, pager_offset, searchChannelName);
        	list = (List)map.get("list");
        	javax.sql.DataSource ds = null;
            java.sql.Connection conn = null;
            java.sql.Statement stmt = null;
            java.sql.ResultSet rs = null;
            ds = new com.whir.common.util.DataSourceBase().getDataSource();
            conn = ds.getConnection();
            stmt = conn.createStatement();
            
            for(int i=0;i<list.size();i++){
            	Object[] obj = (Object[]) list.get(i);
            	if(obj[3].toString().equals("0")){//信息管理
            		obj[2] = "信息管理." + obj[2].toString();
            	}else if(obj[4] != null && "1".equals(obj[4].toString())) {//自定义信息频道
                    rs = stmt.executeQuery("select userChannelName from OA_USERCHANNEL where USERCHANNEL_ID=" + obj[3]);
                    if(rs.next()) {
                    	obj[2] = rs.getString(1) + "." + obj[2];
                    } else {
                        continue;
                    }
                } else {//单位主页
                	rs = stmt.executeQuery("select ORGNAMESTRING from ORG_ORGANIZATION where ORG_ID=" + obj[3]);
                    if(rs.next()) {
                    	obj[2] = rs.getString(1) + "." + obj[2];
                    } else {
                        continue;
                    }
                }
            	resultList.add(obj);
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        if(resultList!=null&&resultList.size()>0){
            this.setMessage("1", "正常");
            result += "<informationChannelList>";
	         for (int j = 0; j < resultList.size(); j++) {
	             Object[] obj = (Object[]) resultList.get(j);
	             result += "<informationChannel>";
	             result += "<channelId>" +obj[0] + "</channelId>";
	             result += "<channelName>" +strFormat(obj[1]) + "</channelName>";
	             result += "<channelNameString>"+strFormat(obj[2]) + "</channelNameString>";
	             result += "<channelType>"+strFormat(obj[3]) + "</channelType>";
	             result += "<userDefine>"+strFormat(obj[4]) + "</userDefine>";
	             result += "</informationChannel>";
	         }
	         result += "</informationChannelList><recordCount>"+map.get("pageCount")+"</recordCount>"+"<currentPage>"+map.get("currentPage")+"</currentPage>";
        }else{
        	this.setMessage("0", "没取到数据");
        }
        return result;
    }
    
    /**
     * 取能查看的栏目
     * @return String
     */
    public String getAllInformationChannel() {
        String result = "";
        Element rootElement = doc.getRootElement();
        Element domainElement = rootElement.getChild("domain");
        Element userIdElement = rootElement.getChild("userId");
        Element orgIdElement = rootElement.getChild("orgId");
        Element channelTypeElement = rootElement.getChild("channelType");
        Element userDefineElement = rootElement.getChild("userDefine");
        Element pagerOffsetElement = rootElement.getChild("pager_offset");
        Element searchChannelNameElement = rootElement.getChild("searchChannelName");
        Element orgIdStringElement = rootElement.getChild("orgIdString");
        Element pageSizeElement = rootElement.getChild("pageSize");

        String domainId = domainElement.getValue();
        String userId = userIdElement.getValue();
        String orgId = orgIdElement.getValue();
        String channelType = channelTypeElement.getValue();
        String userDefine = userDefineElement.getValue();
        String pager_offset = pagerOffsetElement.getValue();
        String searchChannelName = searchChannelNameElement == null ? "" : searchChannelNameElement.getValue();
        String orgIdString = orgIdStringElement == null ? "" : orgIdStringElement.getValue();
        String pageSize = pageSizeElement == null ? "" : pageSizeElement.getValue();

        NewChannelBD bd = new NewChannelBD();
        List list = new ArrayList();
        Map map = null;
        try {
            map = bd.getAllViewChannel_onlyInformation2(userId, orgId,
                channelType, userDefine, domainId,
                pager_offset, searchChannelName, orgIdString, pageSize);
            list = (List) map.get("list");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
       if(list!=null&&list.size()>0){
            this.setMessage("1", "正常");
             //aaa.channelId,aaa.channelName,aaa.channelLevel,aaa.channelIdString,aaa.afficheChannelStatus
             for (int jj = 0; jj < list.size(); jj++) {
                     Object[] oobj = (Object[]) list.get(jj);
                     result += "<infomationChannel>";
                     result += "<channelId>" +oobj[0] + "</channelId>";
                     result += "<channelName>" +strFormat(oobj[1]) + "</channelName>";
                     result += "<channelLevel>"+oobj[2] + "</channelLevel>";
                     result += "<channelIdString>"+oobj[3] + "</channelIdString>";
                     result += "<channelNameString>"+strFormat(oobj[5]) + "</channelNameString>";
                     result += "<channelNeedCheckup>"+oobj[6] + "</channelNeedCheckup>";
                     result += "<isCanAdd>"+oobj[7] + "</isCanAdd>";
                     result += "</infomationChannel>";
             }
             result += "<recordCount>"+map.get("pageCount")+"</recordCount>";
       }else{
          this.setMessage("0", "没取到数据");

       }

       return result;
    }
    /**
     * 取我的手机桌面显示的栏目
     * @return String
     */
    public String getMyDisplayChannelList(){
      String result="";
      Element rootElement = doc.getRootElement();
      Element userIdElement = rootElement.getChild("userId");
      String userId=userIdElement.getValue();
      ChannelBD bd=new ChannelBD();
      List list=bd.getMyDisplayChannelPOList(userId);
      if(list!=null&&list.size()>0){
             this.setMessage("1", "正常");
              result = "<output>";
              //aaa.channelId,aaa.channelName,aaa.channelLevel,aaa.channelIdString,aaa.afficheChannelStatus
              for (int jj = 0; jj < list.size(); jj++) {
                      MyDisplayChannelPO  po = (MyDisplayChannelPO) list.get(jj);
                      result += "<myInfomationChannel>";
                      result += "<channelId>" +po.getMyChannelId() + "</channelId>";
                      result += "<channelName>" +strFormat(po.getMyChannelName()) + "</channelName>";
                      result += "</myInfomationChannel>";
              }
              result += "</output>";
      }else{
        this.setMessage("0", "没取到数据");

      }
      return result;

    }
    /**
     * 保存我的手机桌面显示的栏目
     * @return String
     */
    public String saveMyDisplayChannel(){
        String  result="";
        Element rootElement = doc.getRootElement();
        Element userIdElement = rootElement.getChild("userId");
        String userId=userIdElement.getValue();

        List list = rootElement.getChildren("myDisplayChannel");
        if(list!=null&&list.size()>0){
            List poList=new ArrayList();
            for(int i=0;i<list.size();i++){
               Element channelElement = (Element) list.get(i);
               Element channelIdElement = channelElement.getChild("channelId");
               Element channelNameElement = channelElement.getChild("channelName");
               Element channelIdStringElement = channelElement.getChild("channelIdString");
               MyDisplayChannelPO  po=new MyDisplayChannelPO();
               po.setUserId(new Long(userId));
               po.setMyChannelId(new Long(channelIdElement.getValue()));
               po.setMyChannelName(channelNameElement.getValue());
               po.setChannelIdString(channelIdStringElement.getValue());
               poList.add(po);
            }
             ChannelBD bd=new ChannelBD();
             String saveresult= bd.saveMydisplayChannel(poList,userId);
             //保存成功
             if(saveresult.equals("1")){
                  this.setMessage("1", "正常");
             }else{
                  this.setMessage("0", "没取到数据");
             }
        }else{
        	ChannelBD bd=new ChannelBD();
            String saveresult= bd.deleteMydisplayChannel(userId);
            //保存成功
            if(saveresult.equals("1")){
                 this.setMessage("1", "正常");
            }else{
                 this.setMessage("0", "没取到数据");
            }
        }


        return result;

    }


    /**
    * 取信息列表
    * @param queryStr String[]
    *  <br> String domainId = queryStr[0]           域标识 =====*默认为：0*
       <br> String userId = queryStr[1]             用户id
       <br> String orgId = queryStr[2]              组织id
       <br> String orgIdString = queryStr[3]        组织的orgIdString
       <br> String type = queryStr[4]               当为all时取所有  此处暂固定为all
       <br> String userDefine = queryStr[5]
　　　　<br>　　　　　　　 channelType  =0  时  信息管理
　　　　 <br>　　　　　　　 当 channelType 不为0时 {
　　　　 <br>　　　　　　　　userDefine=1    字定义频道     channelType为自定频道的id
　　　　<br>　　　　　　　　userDefine！=1时   单位主页     channelType为orgId
　　　　 <br>　　　　　　　}      =====*默认为：0*
       <br> String informationId = queryStr[6]      信息的id =====*默认为空*
       <br> String search = queryStr[7]             是否是查找   1：是查找
       <br> String userChannelName = queryStr[8]    频道名 =====*默认为：信息管理*
       <br> String channelType = queryStr[9]        栏目的类型 =====*默认为：0*
       <br> String depart = queryStr[10]            是否是单位主页
       <br> String searchDate = queryStr[11]        是否查询日期  1：是
       <br> String searchBeginDate = queryStr[12]   查询开始日期
       <br> String searchEndDate = queryStr[13]     查询结束日期
       <br> String searchIssuerName = queryStr[14]  查询的发布人
       <br> String searchKeywordType =queryStr[15]  查询的关键字类型
       <br> String searchKeyword = queryStr[16]     查询的关键字内容
       <br> String title = queryStr[17]             标题
       <br> String key = queryStr[18]               关键字
       <br> String subtitle = queryStr[19]          副标题
       <br> String append = queryStr[20]            附件名
       <br> String searchChannel =queryStr[21]      查询的栏目id =====*默认为：0*
       <br> String pager_offset=queryStr[22]        翻页 的 第几页  =====*默认为：0*
    * @return List
    *   返回的List
    * 　第一层存两个List0 ,List1<br>
    *   List0 存n条 List00至List0n       每一条List0n存的是每一条信息的具体内容<br>
    *              List0n里存的先后顺序是：
    * informationId 信息id <br>
    * informationTitle 信息标题<br>
    * informationKits 点击次数<br>
    * informationIssuer 发布人<br>
    * informationVersion 版本信息<br>
    * informationIssueTime发布日期 <br>
    * informationSummary 摘要 <br>
    * informationHead红头 <br>
    * informationType信息类型<br>
    * informationCommonNum 评论数 <br>
    * channelName 栏目名称    <br>
    * channelId 栏目id <br>
    * titleColor 标题颜色 <br>
    * isConf <br>
    * documentNo  编号 <br>
    * transmitToEzsite 是否发送到网站 <br>
    * informationModifyTime最后修改时间 <br>
    * orderCode  排序码 <br>
    * informationIssueOrg 发布人组织 <br>
    * informationIsCommend  是否推荐 <br>
    * channelType  栏目的类型  <br>
    *   List1 存1条 List10               List10的 List10.get(0):pager_offset 翻页的页数      List10.get(1):recordCount 总格记录条数<br>
    *
    */
   public String information_getMobileList() {
       String result = "";
       Element rootElement = doc.getRootElement();
       Object[] inobj = new Object[25];
       inobj[0] = rootElement.getChild("domain").getValue();
       inobj[1] = rootElement.getChild("userId").getValue();
       inobj[2] = rootElement.getChild("orgId").getValue();
       inobj[3] = rootElement.getChild("orgIdString").getValue();
       inobj[4] = rootElement.getChild("type").getValue();
       inobj[5] = rootElement.getChild("userDefine").getValue();
       inobj[6] = rootElement.getChild("informationId").getValue();
       inobj[7] = rootElement.getChild("search").getValue();
       inobj[8] = rootElement.getChild("userChannelName").getValue();
       inobj[9] = rootElement.getChild("channelType").getValue();
       inobj[10] = rootElement.getChild("depart").getValue();
       inobj[11] = rootElement.getChild("searchDate").getValue();
       inobj[12] = rootElement.getChild("searchBeginDate").getValue();
       inobj[13] = rootElement.getChild("searchEndDate").getValue();
       inobj[14] = rootElement.getChild("searchIssuerName").getValue();
       inobj[15] ="";
       inobj[16] = "";
       inobj[17] = rootElement.getChild("title").getValue();
       inobj[18] = rootElement.getChild("informationKey").getValue();
       inobj[19] = rootElement.getChild("subtitle").getValue();
       inobj[20] = rootElement.getChild("append").getValue();
       inobj[21] = rootElement.getChild("searchChannel").getValue();
       inobj[22] = rootElement.getChild("pager_offset").getValue();
       inobj[23] = rootElement.getChild("pageSize").getValue();
       //20151112 -by jqq 增加自定义频道的id 用以查询各自定义频道下的信息
       if(rootElement.getChild("userChannelId") != null && rootElement.getChild("userChannelId").getValue() != null){
    	   inobj[24] = rootElement.getChild("userChannelId").getValue();
       }else{
    	   inobj[24] = "0";
       }

       InformationService informationService = new InformationService();
       List list = informationService.getInformationListMobile(inobj);
       if (list != null && list.size() > 0) {
           this.setMessage("1", "正常");
           List list0=(List)list.get(0);
           List list1=(List)list.get(1);
           for(int ii=0;ii<list0.size();ii++){
               List list0ii=(List)list0.get(ii);
               result += "<infoList>";
               int index=0;
               result+="<informationId>"+list0ii.get(index++)+"</informationId>";
               result+="<informationTitle><![CDATA["+list0ii.get(index++)+"]]></informationTitle>";
               result+="<informationKits>"+list0ii.get(index++)+"</informationKits>";
               result+="<informationIssuer>"+list0ii.get(index++)+"</informationIssuer>";
               result+="<informationVersion>"+list0ii.get(index++)+"</informationVersion>";
               result+="<informationIssueTime>"+list0ii.get(index++)+"</informationIssueTime>";
               result+="<informationSummary><![CDATA["+list0ii.get(index++)+"]]></informationSummary>";
               result+="<informationHead>"+list0ii.get(index++)+"</informationHead>";
               result+="<informationType>"+list0ii.get(index++)+"</informationType>";
               result+="<informationCommonNum>"+list0ii.get(index++)+"</informationCommonNum>";
               result+="<channelName>"+strFormat(list0ii.get(index++))+"</channelName>";
               result+="<channelId>"+list0ii.get(index++)+"</channelId>";
               result+="<titleColor>"+list0ii.get(index++)+"</titleColor>";
               result+="<isConf>"+list0ii.get(index++)+"</isConf>";
               result+="<documentNo>"+list0ii.get(index++)+"</documentNo>";
               result+="<transmitToEzsite><![CDATA["+list0ii.get(index++)+"]]></transmitToEzsite>";
               result+="<informationModifyTime>"+list0ii.get(index++)+"</informationModifyTime>";
               result+="<orderCode>"+list0ii.get(index++)+"</orderCode>";
               result+="<informationIssueOrg><![CDATA["+list0ii.get(index++)+"]]></informationIssueOrg>";
               result+="<informationIsCommend>"+list0ii.get(index++)+"</informationIsCommend>";
               result+="<channelType>"+list0ii.get(index++)+"</channelType>";
               result+="<pageNum>"+ii+"</pageNum>";
               result += "</infoList>";
           }
           List list10=(List)list1.get(0);
           result += "<pager_offset>"+list10.get(0)+"</pager_offset>";
           result += "<recordCount>"+list10.get(1)+"</recordCount>";
       } else {
           this.setMessage("0", "没取到数据");
       }
       return result;
   }

   /**
   * 根据信息id取此条信息的内容
   * @param setObj String[]<br>
   *          <br> String informationId = setObj[0]        信息ID
              <br> String userId = setObj[1]               用户的id
              <br> String orgId = setObj[2]                组织的Id
              <br> String orgIdString = setObj[3]      组织orgIdString
              <br> String domainId = setObj[4]             域id
              <br> String userName = setObj[5]             用户名
              <br> String orgName = setObj[6]              组织名
              <br> String userChannelName = setObj[7]      频道名
              <br> return_informationType = setObj[8]      信息的类型 0 :  普通  1 :  html  2 :  地址连接  3 :  文件连接  4 :   word  5 :   excel  6：ppt
              <br> String channelType = setObj[9]          频道的类型
   * @return List<br>
   * 返回的List 第一层存8条List List0,List1,Lst2,List3,List4,List5,List6,List7<br>
   * List0 存1条List　List00每一项依次存的是此条信息的详细信息<br>
   * List1 存n条List　List10 至List1n　　每条List1n 的第一项存的是图片附件名  第二项 存的是图片附件存储名<br>
   * List2 存n条List　List20 至List2n　　每条List2n 的第一项存的是附件名  第二项 存的是附件存储名<br>
   * List3 存1条List　List30只有一项　存的是此信息的评论条数<br>
   * List4 存1条List　List40只有一项　存的是此信息的类型<br>
   * List5 存1条List　List50只有一项　存的是此信息所属频道名<br>
   * List6 存1条List　List60只有一项　存的是此信息所属栏目名<br>
   * List7 存1条List　List70只有一项　存的是地址<br>
   */
   public String information_loadMobileInformation() {
      String result = "";
      Element rootElement = doc.getRootElement();
      String setObj[]=new String [10];
      setObj[0]=rootElement.getChild("informationId").getValue();
      setObj[1]=rootElement.getChild("userId").getValue();
      setObj[2]=rootElement.getChild("orgId").getValue();
      setObj[3]=rootElement.getChild("orgIdString").getValue();
      setObj[4]=rootElement.getChild("domain").getValue();
      setObj[5]=rootElement.getChild("userName").getValue();
      //20160504 -by jqq 必须字段
      setObj[6]=rootElement.getChild("orgName")!=null ? rootElement.getChild("orgName").getValue() : "";
      setObj[7]=rootElement.getChild("userChannelName").getValue();
      setObj[8]=rootElement.getChild("informationType").getValue();
      setObj[9]=rootElement.getChild("channelType").getValue();

       InformationService informationService = new InformationService();
       List list = informationService.loadInformation_Mobile(setObj);
       if(list.size()>0){
           this.setMessage("1", "正常");
       }else{
           this.setMessage("0", "取不到数据");
       }
       List list0=(List)list.get(0);
       List list00=(List)list0.get(0);
       int index=0;

       result+="<informationTitle><![CDATA["+list00.get(index++)+"]]></informationTitle>";
       result+="<informationSubTitle><![CDATA["+list00.get(index++)+"]]></informationSubTitle>";
       result+="<informationContent><![CDATA["+list00.get(index++)+"]]></informationContent>";
       result+="<informationIssuer>"+list00.get(index++)+"</informationIssuer>";
       result+="<informationIssueOrg>"+list00.get(index++)+"</informationIssueOrg>";
       result+="<informationIssueTime>"+list00.get(index++)+"</informationIssueTime>";
       result+="<informationModifyTime>"+list00.get(index++)+"</informationModifyTime>";
       result+="<informationVersion>"+list00.get(index++)+"</informationVersion>";
       result+="<informationAuthor>"+list00.get(index++)+"</informationAuthor>";
       result+="<informationSummary><![CDATA["+list00.get(index++)+"]]></informationSummary>";
       result+="<informationKey><![CDATA["+list00.get(index++)+"]]></informationKey>";
       result+="<informationKits>"+list00.get(index++)+"</informationKits>";

       result += "<documentNo>" + list00.get(index++) + "</documentNo>";
       result += "<comeFrom>" + list00.get(index++) + "</comeFrom>";
       result += "<documentEditor>" + list00.get(index++) +
               "</documentEditor>";
       result += "<documentType>" + list00.get(index++) +
               "</documentType>";
       result += "<Can_readers><![CDATA[" + list00.get(index++) + "]]></Can_readers>";
       result += "<forbidCopy>" + list00.get(index++) + "</forbidCopy>";
       result += "<informationCanRemark>" + list00.get(index++) + "</informationCanRemark>";
       //20160302 -by jqq evo改造接口返回增加字段displaytitle
       result += "<displayTitle>" + list00.get(index++) + "</displayTitle>";
       //20160310 -by jqq evo改造接口返回增加字段displayimage
       result += "<displayImage>" + list00.get(index++) + "</displayImage>";
       //20151230 -by jqq 接口返回的该字段重复 去掉一个
       //result += "<informationType>" +setObj[8] + "</informationType>";

       List list3=(List)list.get(3);
        List list30=(List)list3.get(0);
       List list4=(List)list.get(4);
        List list40=(List)list4.get(0);
       List list5=(List)list.get(5);
        List list50=(List)list5.get(0);
       List list6=(List)list.get(6);
        List list60=(List)list6.get(0);
       List list7=(List)list.get(7);
        List list70=(List)list7.get(0);

       result += "<commentNum>" + list30.get(0) + "</commentNum>";
       result += "<informationType>" + list40.get(0) + "</informationType>";
       result += "<userChannelName>" + strFormat(list50.get(0)) + "</userChannelName>";
       result += "<channelName>" + strFormat(list60.get(0)) + "</channelName>";
       result += "<address>" + strFormat(list70.get(0)) + "</address>";

       //金格id
       String jingeId = "";
       //信息的类型
       String informationType = setObj[8];
       String workType="";
       //金格类型的 信息
       if (informationType.equals("4")||informationType.equals("5")||informationType.equals("6")) {
           jingeId = "" + list00.get(2);
           //word
           if(informationType.equals("4")){
               workType=".doc";
               SenddocumentBD senddocumentBD = new SenddocumentBD();
               java.util.Map docMap = senddocumentBD.getGovDocumentExt(jingeId);
               if (null != docMap && null != docMap.get(jingeId)) {
                   workType = ".pdf";
               }
           }
           //excel
           if(informationType.equals("5")){
                workType=".xls";
           }
           //ppt
           if(informationType.equals("6")){
                workType=".ppt";
           }
            result += "<workType>" + workType+ "</workType>";
       }


       List list1=(List)list.get(1);
       List list2=(List)list.get(2);
       if(list1!=null&&list1.size()>0){
           result += "<picList>";
           for (int i = 0; i < list1.size(); i++) {
                List tempList=(List)list1.get(i);
                result += "<picName>" + tempList.get(1) + "</picName>";
                result += "<picSaveName>" + tempList.get(2) + "</picSaveName>";
           }
           result += "</picList>";
       }
       if(list2!=null&&list2.size()>0){
          result += "<appList>";
          for (int i = 0; i < list2.size(); i++) {
               List tempList=(List)list2.get(i);
               result += "<appName>" + tempList.get(1) + "</appName>";
               result += "<appSaveName>" + tempList.get(2) + "</appSaveName>";
          }
          result += "</appList>";
      }
      return  result;
   }

   /**
    *
    * @return String
    */
   public String  information_loadMobileByWorkId( ) {
        String result = "";
        Element rootElement = doc.getRootElement();
        String setObj[] = new String[1];
        setObj[0]=rootElement.getChild("workId").getValue();
        InformationService informationService = new InformationService();
        List list=informationService.loadInformation_MobileByWorkId(setObj);
        if(list.size()>0){
            this.setMessage("1", "正常");
        }else{
            this.setMessage("0", "取不到数据");
        }
        List list0=(List)list.get(0);
        List list00=(List)list0.get(0);
        int index=0;
        result+="<informationTitle><![CDATA["+list00.get(index++)+"]]></informationTitle>";
        result+="<userChannelName><![CDATA["+list00.get(index++)+"]]></userChannelName>";
        result+="<informationSubTitle><![CDATA["+list00.get(index++)+"]]></informationSubTitle>";
        result+="<informationContent><![CDATA["+list00.get(index++)+"]]></informationContent>";
        result+="<informationIssuer>"+list00.get(index++)+"</informationIssuer>";
        result+="<informationIssueOrg>"+list00.get(index++)+"</informationIssueOrg>";
        result+="<informationIssueTime>"+list00.get(index++)+"</informationIssueTime>";
        result+="<informationModifyTime>"+list00.get(index++)+"</informationModifyTime>";
        result+="<informationVersion>"+list00.get(index++)+"</informationVersion>";
        result+="<informationAuthor>"+list00.get(index++)+"</informationAuthor>";
        result+="<informationSummary><![CDATA["+list00.get(index++)+"]]></informationSummary>";
        result+="<informationKey><![CDATA["+list00.get(index++)+"]]></informationKey>";
        result+="<informationKits>"+list00.get(index++)+"</informationKits>";

        result += "<documentNo>" + list00.get(index++) + "</documentNo>";
        result += "<comeFrom>" + list00.get(index++) + "</comeFrom>";
        result += "<documentEditor>" + list00.get(index++) + "</documentEditor>";
        result += "<documentType>" + list00.get(index++) +  "</documentType>";
        result += "<Can_readers><![CDATA[" + list00.get(index++) + "]]></Can_readers>";
        result += "<forbidCopy>" + list00.get(index++) + "</forbidCopy>";
        result += "<informationType>" + list00.get(index++) + "</informationType>";
        result += "<otherChannel>" + list00.get(index++) + "</otherChannel>";
        //金格id
     String jingeId = "";
     //信息的类型
     String informationType = ""+list00.get(19);
     String workType="";
     //金格类型的 信息
     if (informationType.equals("4")||informationType.equals("5")||informationType.equals("6")) {
         jingeId = "" + list00.get(2);
         //word
         if(informationType.equals("4")){
             workType=".doc";
             SenddocumentBD senddocumentBD = new SenddocumentBD();
             java.util.Map docMap = senddocumentBD.getGovDocumentExt(jingeId);
             if (null != docMap && null != docMap.get(jingeId)) {
                 workType = ".pdf";
             }
         }
         //excel
         if(informationType.equals("5")){
              workType=".xls";
         }
         //ppt
         if(informationType.equals("6")){
              workType=".ppt";
         }
          result += "<workType>" + workType+ "</workType>";
     }

        List list1=(List)list.get(1);
        List list2=(List)list.get(2);
        if(list1!=null&&list1.size()>0){
          result += "<picList>";
          for (int i = 0; i < list1.size(); i++) {
               List tempList=(List)list1.get(i);
               result += "<picName>" + tempList.get(1) + "</picName>";
               result += "<picSaveName>" + tempList.get(2) + "</picSaveName>";
          }
          result += "</picList>";
        }
        if(list2!=null&&list2.size()>0){
          result += "<appList>";
          for (int i = 0; i < list2.size(); i++) {
              List tempList=(List)list2.get(i);
              result += "<appName>" + tempList.get(1) + "</appName>";
              result += "<appSaveName>" + tempList.get(2) + "</appSaveName>";
          }
         result += "</appList>";
        }



        //批示意见
        List list3=(List)list.get(3);
        if(list3!=null&&list3.size()>0){
           result += "<commonList>";
           for(int i=0;i<list3.size();i++){
               List tempList=(List)list3.get(i);
               //不是随机流程
               if(tempList.size()>3){
                  int tempindex=0;
                  result += "<activity_id>" + tempList.get(tempindex++) + "</activity_id>";
                  result += "<activityname><![CDATA[" + tempList.get(tempindex++) + "]]></activityname>";
                  result += "<empname>" + tempList.get(tempindex++) + "</empname>";
                  result += "<dealwithemployeecomment><![CDATA[" + tempList.get(tempindex++) + "]]></dealwithemployeecomment>";
                  result += "<dealwithtime>" + tempList.get(tempindex++) + "</dealwithtime>";
                  result += "<curstepcount>" + tempList.get(tempindex++) + "</curstepcount>";
                  result += "<standForUserName>" + tempList.get(tempindex++) + "</standForUserName>";
                  result += "<commentrange>" + tempList.get(tempindex++) + "</commentrange>";
                  result += "<commentFieldId>" + tempList.get(tempindex++) + "</commentFieldId>";
                  result += "<actiCommFieldType>" + tempList.get(tempindex++) + "</actiCommFieldType>";
                  result += "<passRoundCommFieldType>" + tempList.get(tempindex++) + "</passRoundCommFieldType>";
                  result += "<commtype>" + tempList.get(tempindex++) + "</commtype>";
                  result += "<emp_id>" + tempList.get(tempindex++) + "</emp_id>";


               }else{
                   //是随机流程
                   int tempindex=0;
                   result += "<empname>" + tempList.get(tempindex++) + "</empname>";
                   result += "<RandFlowEmployeeComment>" + tempList.get(tempindex++) + "</RandFlowEmployeeComment>";
                   result += "<RandFlowTime>" + tempList.get(tempindex++) + "</RandFlowTime>";
                   result += "<emp_id>" + tempList.get(tempindex++) + "</emp_id>";
               }
           }
           result += "</commonList>";
        }


        List list5=(List )list.get(5);
        List list50=(List)list5.get(0);
        result += "<workInfo>";

        if(list50!=null&&list50.size()>0){
            int pindex=0;
            result += "<creatorcancellink><![CDATA[" + list50.get(pindex++) + "]]></creatorcancellink>";
            result += "<workstepcount>" + list50.get(pindex++) + "</workstepcount>";
            result += "<processdeadlinedate>" + list50.get(pindex++) + "</processdeadlinedate>";
            result += "<workallowcancel>" + list50.get(pindex++) + "</workallowcancel>";
            result += "<workdonewithdate>" + list50.get(pindex++) + "</workdonewithdate>";
            result += "<workcreatedate>" + list50.get(pindex++) + "</workcreatedate>";
            result += "<wf_curemployee_id>" + list50.get(pindex++) + "</wf_curemployee_id>";
            result += "<initactivity>" + list50.get(pindex++) + "</initactivity>";

            result += "<tranfrompersonid>" + list50.get(pindex++) + "</tranfrompersonid>";
            result += "<workcurstep>" + list50.get(pindex++) + "</workcurstep>";
            result += "<workmainlinkfile><![CDATA[" + list50.get(pindex++) + "]]></workmainlinkfile>";
            result += "<worksubmitperson>" + list50.get(pindex++) + "</worksubmitperson>";
            result += "<initactivityname>" + list50.get(pindex++) + "</initactivityname>";
            result += "<worktable_id>" + list50.get(pindex++) + "</worktable_id>";


            result += "<standforuserid>" + list50.get(pindex++) + "</standforuserid>";
            result += "<wf_work_id>" + list50.get(pindex++) + "</wf_work_id>";
            result += "<worksubmittime>" + list50.get(pindex++) + "</worksubmittime>";
            result += "<wf_submitemployee_id>" + list50.get(pindex++) + "</wf_submitemployee_id>";
            result += "<worktype>" + list50.get(pindex++) + "</worktype>";


            result += "<workrecord_id>" + list50.get(pindex++) + "</workrecord_id>";
            result += "<workprocess_id>" + list50.get(pindex++) + "</workprocess_id>";
            result += "<workactivity>" + list50.get(pindex++) + "</workactivity>";
            result += "<submitorg>" + list50.get(pindex++) + "</submitorg>";
            result += "<isstandforwork>" + list50.get(pindex++) + "</isstandforwork>";

            result += "<standforusername>" + list50.get(pindex++) + "</standforusername>";
            result += "<workdeadline>" + list50.get(pindex++) + "</workdeadline>";
            result += "<trantype>" + list50.get(pindex++) + "</trantype>";
            result += "<emergence>" + list50.get(pindex++) + "</emergence>";
            result += "<workfiletype>" + list50.get(pindex++) + "</workfiletype>";

            result += "<worktitle>" + list50.get(pindex++) + "</worktitle>";
//            result += "<activityClass>" + list50.get(pindex++) + "</activityClass>";

        }

        result += "</workInfo>";
        return result;
    }


   /**
    * 保存信息
    * @return String 返回xml格式
    */
   public String saveInformation() {
       Element rootElement = doc.getRootElement();
       Element domainElement = rootElement.getChild("domain");
       String domainId = domainElement==null?"0":domainElement.getValue();
       String userId = rootElement.getChild("userId") ==null?"":rootElement.getChild("userId").getValue().trim();
       String orgId = rootElement.getChild("orgId")==null?"":rootElement.getChild("orgId").getValue().trim();
       String username = rootElement.getChild("username")==null?"":rootElement.getChild("username").getValue().trim();
       String orgName = rootElement.getChild("orgName")==null?"":rootElement.getChild("orgName").getValue().trim();
       String orgIdString = rootElement.getChild("orgIdString")==null?"":rootElement.getChild("orgIdString").getValue().trim();
       String infoTitle = rootElement.getChild("infoTitle")==null?"":rootElement.getChild("infoTitle").getValue().trim();
       String infoContent = rootElement.getChild("infoContent")==null?"":rootElement.getChild("infoContent").getValue().trim();
       String channelId = rootElement.getChild("channelId")==null?"":rootElement.getChild("channelId").getValue().trim();
       
       String displayTitle = rootElement.getChild("displayTitle")==null?"":rootElement.getChild("displayTitle").getValue().trim();
       String titleColor = rootElement.getChild("titleColor")==null?"":rootElement.getChild("titleColor").getValue().trim();
       
       ChannelBD channelBD = new ChannelBD();
       InformationChannelPO channelPO = channelBD.getChannelReader(channelId);
       
       //图片附件
       String[] infoPicName = null;
       String[] infoPicSaveName = null;
       //处理图片
       List picList = getAttachList(rootElement, "pics");
       String[][] picArr = saveAttachs(picList, "information");
       if (picArr != null && picArr.length > 0) {
           infoPicName = new String[picArr.length];
           infoPicSaveName = new String[picArr.length];
           for (int i = 0; i < picArr.length; i++) {
               infoPicName[i] = picArr[i][0];
               infoPicSaveName[i] = picArr[i][1];
           }
       }
       //信息附件
       String[] infoAppendName = null;
       String[] infoAppendSaveName = null;
       //处理附件
       List accList = getAttachList(rootElement, "attach");
       String[][] accArr = saveAttachs(accList, "information");
       if (accArr != null && accArr.length > 0) {
           infoAppendName = new String[accArr.length];
           infoAppendSaveName = new String[accArr.length];
           for (int i = 0; i < accArr.length; i++) {
               infoAppendName[i] = accArr[i][0];
               infoAppendSaveName[i] = accArr[i][1];
           }
       }

       String result = "";
       int code = 1;
       String message = "保存成功";
       if (!"".equals(domainId) && !domainId.matches("\\d*")) {
           code = -1;
           message = "参数domainId类型不正确";
       } else if (!"".equals(userId) && !userId.matches("\\d*")) {
           code = -1;
           message = "参数userId类型不正确";
       } else if (!"".equals(orgId) && !orgId.matches("\\d*")) {
           code = -1;
           message = "参数orgId类型不正确";
       }

       if (code > 0) {
           InformationPO po = new InformationPO();
           po.setInformationTitle(infoTitle);//信息标题
           po.setInformationContent(infoContent);//信息内容
           po.setInformationIssuerId(new Long(userId));//创建人Id
           po.setInformationIssuer(username);//创建人姓名
           po.setInformationIssueOrgId(orgId);
           po.setInformationIssueOrg(orgName);
           po.setIssueOrgIdString(orgIdString);
           po.setDomainId(Long.valueOf(domainId));
           po.setInformationReader(channelPO.getChannelReader());
           po.setInformationReaderName(channelPO.getChannelReaderName());
           po.setInformationReaderOrg(channelPO.getChannelReaderOrg());
           po.setInformationReaderGroup(channelPO.getChannelReaderGroup());
           if (!"".equals(displayTitle)) {
               po.setDisplayTitle(Integer.parseInt(displayTitle));
           }
           if (!"".equals(titleColor)) {
               po.setTitleColor(Integer.parseInt(titleColor));
           }

           InformationService infoService = new InformationService();
           String id = infoService.saveInformation(
               po,
               channelId,
               infoPicName,
               infoPicSaveName,
               infoAppendName,
               infoAppendSaveName);
       }
       //结果
       this.setMessage(code + "", message);

       return result;
   }
    /**
     *
     * @param obj Object
     * @return String
     */
    public String strFormat(Object obj){
        if(obj!=null){
            obj=CDATA_OPEN+obj+CDATA_CLOSE;
        }
        return obj.toString();
    }


    /**
     *取能发送的栏目
     * @return String
     */
    public String  Information_getCanIssueChannel(){
         String result="";
         Element rootElement = doc.getRootElement();
         Element domainElement = rootElement.getChild("domain");
         Element userIdElement = rootElement.getChild("userId");
         Element orgIdElement = rootElement.getChild("orgId");
         Element orgIdStringElement = rootElement.getChild("orgIdString");
         Element containFlowElement = rootElement.getChild("containFlow");


         Element searchChannelNameElement = rootElement.getChild("searchChannelName");
         String  searchChannelName=searchChannelNameElement==null?"":searchChannelNameElement.getValue();


         String domainId = domainElement.getValue();
         String userId = userIdElement.getValue();
         String orgId = orgIdElement.getValue();
         String orgIdString =orgIdStringElement.getValue();
         String containFlow = "0";
          containFlow=containFlowElement.getValue();

        InformationService   inforService=new InformationService();
        List list=new ArrayList();
        list=inforService.getCanIssueChannel( userId,  orgId,  orgIdString,   domainId,   containFlow,searchChannelName );

        if(list!=null&&list.size()>0){
               this.setMessage("1", "正常");
              // aaa.channelId,aaa.channelName,aaa.channelIdString,aaa.channelType,aaa.userDefine
              //aaa.channelId,aaa.channelName,aaa.channelLevel,aaa.channelIdString,aaa.afficheChannelStatus
              for (int jj = 0; jj < list.size(); jj++) {
                      Object[] oobj = (Object[]) list.get(jj);
                      result += "<infomationChannel>";
                      result += "<channelId>" +oobj[0] + "</channelId>";
                      result += "<channelName>" +strFormat(oobj[1]) + "</channelName>";
                      result += "<channelIdString><![CDATA[" +oobj[2] + "]]></channelIdString>";
                      result += "</infomationChannel>";
              }
        }else{
           this.setMessage("0", "没取到数据");
        }

        return result;
     }

    /**
     * 取知识标签,结果是以"|"分割的字符串
     * @return
     */
    public String getInformationTag(){
    	String result = "";
    	InformationService inforService = new InformationService();
    	String tags = inforService.getInformationTag();
    	if (tags.length() > 0) {
    		this.setMessage("1", "正常");
    		result += "<informationTag>"+strFormat(tags)+"</informationTag>";
    	} else {
    		this.setMessage("0", "没有数据");
    	}
    	return result;
    }

    /**
     * 取知识标签检索列表
     * @return
     */
    public String getInformationTagList(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	Object[] inobj = new Object[25];
        inobj[0] = rootElement.getChild("domain").getValue();
        inobj[1] = rootElement.getChild("userId").getValue();
        inobj[2] = rootElement.getChild("orgId").getValue();
        inobj[3] = rootElement.getChild("orgIdString").getValue();
        inobj[4] = rootElement.getChild("type").getValue();
        inobj[5] = "";
        inobj[6] = "";
        inobj[7] = "";
        inobj[8] = rootElement.getChild("userChannelName").getValue();
        inobj[9] = rootElement.getChild("channelType").getValue();
        inobj[10] = "";
        inobj[11] = "";
        inobj[12] = "";
        inobj[13] = "";
        inobj[14] = "";
        inobj[15] = "";
        inobj[16] = "";
        inobj[17] = "";
        inobj[18] = "";
        inobj[19] = "";
        inobj[20] = "";
        inobj[21] = "";
        inobj[22] = rootElement.getChild("pager_offset").getValue();
        inobj[23] = rootElement.getChild("retrievalKey").getValue();
        inobj[24] = rootElement.getChild("pageSize").getValue();
        InformationService informationService = new InformationService();
        List list = informationService.getInformationList_Mobile(inobj);
        if (list != null && list.size() > 0) {
            this.setMessage("1", "正常");
            List list0=(List)list.get(0);
            List list1=(List)list.get(1);
            for(int ii=0;ii<list0.size();ii++){
                List list0ii=(List)list0.get(ii);
                result += "<infoList>";
                int index=0;
                result+="<informationId>"+list0ii.get(index++)+"</informationId>";
                result+="<informationTitle><![CDATA["+list0ii.get(index++)+"]]></informationTitle>";
                result+="<informationKits>"+list0ii.get(index++)+"</informationKits>";
                result+="<informationIssuer>"+list0ii.get(index++)+"</informationIssuer>";
                result+="<informationVersion>"+list0ii.get(index++)+"</informationVersion>";
                result+="<informationIssueTime>"+list0ii.get(index++)+"</informationIssueTime>";
                result+="<informationSummary><![CDATA["+list0ii.get(index++)+"]]></informationSummary>";
                result+="<informationHead>"+list0ii.get(index++)+"</informationHead>";
                result+="<informationType>"+list0ii.get(index++)+"</informationType>";
                result+="<informationCommonNum>"+list0ii.get(index++)+"</informationCommonNum>";
                result+="<channelName>"+strFormat(list0ii.get(index++))+"</channelName>";
                result+="<channelId>"+list0ii.get(index++)+"</channelId>";
                result+="<titleColor>"+list0ii.get(index++)+"</titleColor>";
                result+="<isConf>"+list0ii.get(index++)+"</isConf>";
                result+="<documentNo>"+list0ii.get(index++)+"</documentNo>";
                result+="<transmitToEzsite>"+list0ii.get(index++)+"</transmitToEzsite>";
                result+="<informationModifyTime>"+list0ii.get(index++)+"</informationModifyTime>";
                result+="<orderCode>"+list0ii.get(index++)+"</orderCode>";
                result+="<informationIssueOrg>"+list0ii.get(index++)+"</informationIssueOrg>";
                result+="<informationIsCommend>"+list0ii.get(index++)+"</informationIsCommend>";
                result+="<channelType>"+list0ii.get(index++)+"</channelType>";
                result += "</infoList>";
            }
            List list10=(List)list1.get(0);
            result += "<pager_offset>"+list10.get(0)+"</pager_offset>";
            result += "<recordCount>"+list10.get(1)+"</recordCount>";
        } else {
            this.setMessage("0", "没取到数据");
        }
    	return result;
    }

    /**
     * 信息积分榜
     * @return
     */
    public String viewUserStat(){
    	String result = "";
    	InformationService informationService = new InformationService();
    	List list = informationService.viewUserStat();
    	if (list!=null && list.size()>0){
    		this.setMessage("1", "正常");
    		Object[] obj = null;
    		for(int i=0;i<list.size();i++) {
    			if(list.size()>6&&i==6){
    				break;
    			}
    			obj = (Object[]) list.get(i);
    			result += "<userStat>";
    			result += "<userName>"+strFormat(obj[1])+"</userName>";
    			result += "<totalPoints>"+obj[8]+"</totalPoints>";
    			result += "<newPoints>"+obj[3]+"</newPoints>";
    			result += "</userStat>";
    		}
    	} else {
    		this.setMessage("0", "没取到数据");
    	}
    	return result;
    }

    /**
     * 取最近更新的有图片附件的信息（只取4条）
     * @return
     */
    public String getImageInfo(){
    	String result = "";
    	InformationService informationService = new InformationService();
    	List list = informationService.getImageInfo();
    	if (list!=null && list.size()>0){
    		this.setMessage("1", "正常");
    		Object[] obj = null;
    		for(int i=0;i<list.size();i++) {
    			obj = (Object[]) list.get(i);
    			result += "<imageInfo>";
    			result += "<informationId><![CDATA["+obj[0]+"]]></informationId>";
    			result += "<informationtype><![CDATA["+obj[3]+"]]></informationtype>";
    			result += "<channleId><![CDATA["+obj[2]+"]]></channleId>";
    			result += "<channleType><![CDATA["+obj[4]+"]]></channleType>";
    			result += "</imageInfo>";
    		}
    	} else {
    		this.setMessage("0", "没取到数据");
    	}
    	return result;
    }

    /**
     * 根据信息ID获取该信息中上传的第一张图片
     * @return
     */
    public String getImage(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String informationId = rootElement.getChild("informationId").getValue();
    	InformationService informationService = new InformationService();
    	String image = informationService.getHeadInfoImage(informationId);
    	if(image!=null && image.length()>0){
    		this.setMessage("1", "正常");
    		result += "<image>";
    		result += "<imageName><![CDATA["+image.substring(0,image.indexOf("|"))+"]]></imageName>";
    		result += "<imageSaveName><![CDATA["+image.substring(image.indexOf("|")+1)+"]]></imageSaveName>";
    		result += "</image>";
    	} else {
    		this.setMessage("0", "没取到数据");
    	}
    	return result;
    }

    /**
     * 信息列表（取信息内容）
     * @return
     */
    public String getInformationList(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	Object[] inobj = new Object[24];
        inobj[0] = rootElement.getChild("domain").getValue();
        inobj[1] = rootElement.getChild("userId").getValue();
        inobj[2] = rootElement.getChild("orgId").getValue();
        inobj[3] = rootElement.getChild("orgIdString").getValue();
        inobj[4] = rootElement.getChild("type").getValue();
        inobj[5] = "";
        inobj[6] = "";
        inobj[7] = "";
        inobj[8] = rootElement.getChild("userChannelName").getValue();
        inobj[9] = rootElement.getChild("channelType").getValue();
        inobj[10] = "";
        inobj[11] = "";
        inobj[12] = "";
        inobj[13] = "";
        inobj[14] = "";
        inobj[15] = "";
        inobj[16] = "";
        inobj[17] = "";
        inobj[18] = "";
        inobj[19] = "";
        inobj[20] = "";
        inobj[21] = "";
        inobj[22] = rootElement.getChild("pager_offset").getValue();
        inobj[23] = rootElement.getChild("channelId").getValue();

        InformationService informationService = new InformationService();
        List list = informationService.getLastUpdateInfo(inobj);
        if (list != null && list.size() > 0) {
            this.setMessage("1", "正常");
            List list0=(List)list.get(0);
            List list1=(List)list.get(1);
            for(int ii=0;ii<list0.size();ii++){
                List list0ii=(List)list0.get(ii);
                result += "<infoList>";
                int index=0;
                result+="<informationId>"+list0ii.get(index++)+"</informationId>";
                result+="<informationTitle><![CDATA["+list0ii.get(index++)+"]]></informationTitle>";
                result+="<informationKits>"+list0ii.get(index++)+"</informationKits>";
                result+="<informationIssuer>"+list0ii.get(index++)+"</informationIssuer>";
                result+="<informationVersion>"+list0ii.get(index++)+"</informationVersion>";
                result+="<informationContent><![CDATA["+list0ii.get(index++)+"]]></informationContent>";
                result+="<informationIssueTime>"+list0ii.get(index++)+"</informationIssueTime>";
                result+="<informationSummary><![CDATA["+list0ii.get(index++)+"]]></informationSummary>";
                result+="<informationHead>"+list0ii.get(index++)+"</informationHead>";
                result+="<informationType>"+list0ii.get(index++)+"</informationType>";
                result+="<informationCommonNum>"+list0ii.get(index++)+"</informationCommonNum>";
                result+="<channelName>"+strFormat(list0ii.get(index++))+"</channelName>";
                result+="<channelId>"+list0ii.get(index++)+"</channelId>";
                result+="<titleColor>"+list0ii.get(index++)+"</titleColor>";
                result+="<isConf>"+list0ii.get(index++)+"</isConf>";
                result+="<documentNo>"+list0ii.get(index++)+"</documentNo>";
                result+="<transmitToEzsite>"+list0ii.get(index++)+"</transmitToEzsite>";
                result+="<informationModifyTime>"+list0ii.get(index++)+"</informationModifyTime>";
                result+="<orderCode>"+list0ii.get(index++)+"</orderCode>";
                result+="<informationIssueOrg>"+list0ii.get(index++)+"</informationIssueOrg>";
                result+="<informationIsCommend>"+list0ii.get(index++)+"</informationIsCommend>";
                result+="<channelType>"+list0ii.get(index++)+"</channelType>";
                result += "</infoList>";
            }
            List list10=(List)list1.get(0);
            result += "<pager_offset>"+list10.get(0)+"</pager_offset>";
            result += "<recordCount>"+list10.get(1)+"</recordCount>";
        } else {
            this.setMessage("0", "没取到数据");
        }
    	return result;
    }

    /**
     * 将信息内容从html代码转换成纯文本
     * @return
     */
    public String htmlToText(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String html = rootElement.getChild("informationContent").getValue();
    	InformationService informationService = new InformationService();
        String text = informationService.HtmlToText(html);
        if(text!=null && text.length()>0){
        	this.setMessage("1", "正常");
        	result += "<informationContent><![CDATA["+text+"]]></informationContent>";
        }else {
        	this.setMessage("0", "没取到数据");
        }
    	return result;
    }
    
    /**
     * 取某信息栏目
     * @return
     */
    public String getInformationChannelByChannelId() {
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String channelId = rootElement.getChild("channelId").getValue();
    	PortletBD portletBD = new PortletBD();
    	try {
			Object[] obj = portletBD.getInformationChannelByChannelId(channelId);
			String channel = "";
			for(int i=0;i<obj.length;i++){
				channel += obj[i] + ",";
			}
			if(channel!=null && channel.length()>0){
				this.setMessage("1", "正常");
				result += "<informationChannel><![CDATA["+channel.substring(0, channel.lastIndexOf(","))+"]]></informationChannel>";
			} else {
				this.setMessage("0", "没取到数据");
			}
		} catch (HibernateException e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    /**
     * 获取某栏目下的信息
     * @return
     */
    public String listInformation(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String domainId = rootElement.getChild("domain").getValue();
    	String userId = rootElement.getChild("userId").getValue();
    	String orgId = rootElement.getChild("orgId").getValue();
    	String orgIdString = rootElement.getChild("orgIdString").getValue();
    	String channelId = rootElement.getChild("channelId").getValue();
    	PortletBD portletBD = new PortletBD();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
    	try {
			List list = portletBD.listInformation(channelId, userId, orgId, orgIdString, domainId, 15);
			if(list!=null && list.size()>0){
				this.setMessage("1", "正常");
				result += "<informationList>";
				for(int i=0;i<list.size();i++){
					Object[] obj = (Object[]) list.get(i);
		            result += "<information>";
		            result += "<channelName>" + strFormat(obj[0]) + "</channelName>";
		            result += "<informationId>" + obj[1] + "</informationId>";
		            result += "<informationTitle>" + strFormat(obj[2]) + "</informationTitle>";
		            result += "<informationKits>" + obj[3] + "</informationKits>";
		            result += "<informationIssueTime>" + sdf.format((Date)obj[4]) + "</informationIssueTime>";
		            result += "<informationHead>" + obj[5] + "</informationHead>";
		            result += "<informationType>" + obj[6] + "</informationType>";
		            result += "<channelType>" + obj[7] + "</channelType>";
		            result += "<channelId>" + obj[8] + "</channelId>";
		            result += "<channelShowType>" + obj[9] + "</channelShowType>";
		            result += "<titleColor>" + obj[10] + "</titleColor>";
		            result += "<userDefine>" + obj[11] + "</userDefine>";
		            result += "<isConf>" + obj[12] + "</isConf>";
		            result += "<informationContent>" + strFormat(obj[13]) + "</informationContent>";
		            result += "</information>";
				}
				result += "</informationList>";
			}else {
				this.setMessage("0", "没取到数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    /**
     * 取某信息中包含的附件和图片
     * @return
     */
    public String getAccessory(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String informationId = rootElement.getChild("informationId").getValue();
    	InformationAccessoryBD accBD = new InformationAccessoryBD();
    	try {
    		List accList = accBD.getAccessory(informationId);
    		if(accList!=null && accList.size()>0){
    			this.setMessage("1", "正常");
				result += "<accessoryList>";
				for(int i=0;i<accList.size();i++){
					Object[] obj = (Object[]) accList.get(i);
					result += "<accessory>";
					result += "<accessoryId>" + obj[0] + "</accessoryId>";
					result += "<accessoryName>" + obj[1] + "</accessoryName>";
					result += "<accessorySaveName>" + obj[2] + "</accessorySaveName>";
					result += "<accessoryType>" + obj[3] + "</accessoryType>";
					result += "<accessoryIsImage>" + obj[4] + "</accessoryIsImage>";
					result += "</accessory>";
				}
				result += "</accessoryList>";
    		} else {
    			this.setMessage("0", "没取到数据");
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    /**
     * 根据传入的参数栏目ID取其子栏目，若栏目ID为0，则取所有一级栏目
     * @return
     */
    public String getChannelByParentChannel(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String domainId = rootElement.getChild("domain").getValue();
    	String userId = rootElement.getChild("userId").getValue();
    	String orgId = rootElement.getChild("orgId").getValue();
    	String orgIdString = rootElement.getChild("orgIdString").getValue();
    	String channelId = rootElement.getChild("channelId").getValue();
    	String channelType = rootElement.getChild("channelType").getValue();
    	//PortletBD bd = new PortletBD();
    	//20160328 -by jqq 改用新的查询方法，查询所有父栏目下1级子栏目
    	NewChannelBD bd = new NewChannelBD();
    	List list = bd.getChannelByParentId(userId, orgId, orgIdString, domainId, channelId,channelType);
    	if(list!=null && list.size()>0) {
    		this.setMessage("1", "正常");
        	if(list!=null && list.size()>0){
        		//result += "<informationChannelList>";
        		for(int i=0;i<list.size();i++){
        			Object[] obj = (Object[]) list.get(i);
        			result += "<informationChannel>";
        			result += "<channelId>" + obj[0].toString() + "</channelId>";
        			result += "<channelName><![CDATA["+obj[1].toString()+"]]></channelName>";
        			result += "<channelParentId>" + obj[2].toString() + "</channelParentId>";
        			result += "<channelIdString>" + obj[3].toString() + "</channelIdString>";
        			result += "<channelNameString><![CDATA[" + obj[4].toString() + "]]></channelNameString>";
        			result += "<channelType>" + obj[5].toString() + "</channelType>";
        			result += "<userDefine>" + obj[6].toString() + "</userDefine>";
        			result += "<hasInfomation>" + obj[7].toString() + "</hasInfomation>";
        			result += "<hasChildChannl>" + obj[8].toString() + "</hasChildChannl>";
        			result += "<channelNeedCheckup>" + obj[9].toString() + "</channelNeedCheckup>";
        			//20160328 -by jqq 增加是否能新增栏目信息标识isCanAdd字段
        			result += "<isCanAdd>" + obj[10].toString() + "</isCanAdd>";
        			//20160614 -by jqq 增加是否能查看栏目信息标识isView字段
                    result += "<isView>" + obj[11].toString() + "</isView>";
        			result += "</informationChannel>";
        		}
        		//result += "</informationChannelList>";
        	}
    	} else {
    		this.setMessage("0", "没取到数据");
    	}
    	return result;
    }
    
    /**
     * 根据传入的参数栏目ID取其子栏目，若栏目ID为0，则取所有一级栏目，分页列表
     * @return
     */
    public String getChannelPageList(){
    	String result = "";
    	Object[] inobj = new Object[8];
    	Element rootElement = doc.getRootElement();
    	inobj[0] = rootElement.getChild("domain").getValue();
    	inobj[1] = rootElement.getChild("userId").getValue();
    	inobj[2] = rootElement.getChild("orgId").getValue();
    	inobj[3] = rootElement.getChild("orgIdString").getValue();
    	inobj[4] = rootElement.getChild("channelId").getValue();
    	inobj[5] = rootElement.getChild("channelType").getValue();
    	inobj[6] = rootElement.getChild("pager_offset").getValue();
    	inobj[7] = rootElement.getChild("pageSize").getValue();
        InformationService informationService = new InformationService();
        List list = informationService.getChannelPageList(inobj);
        if(list!=null && list.size()>0){
        	if (list != null && list.size() > 0) {
                this.setMessage("1", "正常");
                List list0 = (List)list.get(0);
                List list1 = (List)list.get(1);
                PortletBD bd = new PortletBD();
                for(int ii=0;ii<list0.size();ii++){
                    List list0ii = (List)list0.get(ii);
                    result += "<informationChannel>";
                    int index = 0;
                    result += "<channelId>"+list0ii.get(index++)+"</channelId>";
                    result += "<channelName><![CDATA["+list0ii.get(index++)+"]]></channelName>";
                    result += "<channelParentId>"+list0ii.get(index++)+"</channelParentId>";
                    result += "<channelIdString>"+list0ii.get(index++)+"</channelIdString>";
                    result += "<channelNameString><![CDATA["+list0ii.get(index++)+"]]></channelNameString>";
                    result += "<channelType>"+list0ii.get(index++)+"</channelType>";
                    result += "<userDefine>"+list0ii.get(index++)+"</userDefine>";
                    result += "<channelNeedCheckup>"+list0ii.get(index++)+"</channelNeedCheckup>";
                    //子栏目数量
                    List children = bd.getChannelByParentId(inobj[1].toString(), inobj[2].toString(), inobj[3].toString(), 
                    		inobj[0].toString(), list0ii.get(0).toString(), inobj[5].toString());
                    result += "<children>"+(children.size()>0?children.size():"0")+"</children>";
                    result += "</informationChannel>";
                }
                List list10 = (List)list1.get(0);
                result += "<pager_offset>"+list10.get(0)+"</pager_offset>";
                result += "<recordCount>"+list10.get(1)+"</recordCount>";
            } else {
                this.setMessage("0", "没取到数据");
            }
        }
    	return result;
    }
    
    /**
     * 取某信息的评论列表
     * @return
     * @param informationId
     */
    public String getInformationComment(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String informationId = rootElement.getChild("informationId").getValue();
    	InformationBD bd = new InformationBD();
    	List commentList = bd.getComment(informationId);
    	if(commentList!=null && commentList.size()>0){
    		this.setMessage("1", "正常");
    		for(int i=0;i<commentList.size();i++){
    			Object[] obj = (Object[]) commentList.get(i);
    			result += "<informationComment>";
    			result += "<commentId>"+obj[4].toString()+"</commentId>";
    			result += "<commentIssuerOrg>"+obj[0].toString()+"</commentIssuerOrg>";
    			result += "<commentIssuerName>"+obj[1].toString()+"</commentIssuerName>";
    			result += "<commentIssueTime>"+obj[2].toString()+"</commentIssueTime>";
    			result += "<commentContent>"+obj[3].toString()+"</commentContent>";
    			result += "<commentIssuerId>"+obj[5].toString()+"</commentIssuerId>";
    			result += "<empLivingPhoto>"+( (obj[6]==null || "".equals(obj[6])) ? "" : CutPhotoSize.getFilePathOrNameSmall(obj[6].toString()) )+"</empLivingPhoto>";
    			result += "</informationComment>";
    		}
    	} else {
    		this.setMessage("0", "没取到数据");
    	}
    	return result;
    }
    
    /**
     * 添加评论
     * @return
     * @param informationId, channelId, userId, userName, orgId, orgName, content, domain, commentId(非必需)
     */
    public String setInformationComment(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String informationId = rootElement.getChild("informationId").getValue();
    	String channelId = rootElement.getChild("channelId").getValue();
    	String userId = rootElement.getChild("userId").getValue();
    	String userName = rootElement.getChild("userName").getValue();
    	String orgId = rootElement.getChild("orgId").getValue();
    	String orgName = rootElement.getChild("orgName").getValue();
    	String content = rootElement.getChild("content").getValue();
    	String domain = rootElement.getChild("domain").getValue();
    	String commentId = rootElement.getChild("commentId").getValue();
    	InformationBD bd = new InformationBD();
    	boolean flag = false;
    	if(commentId!=null && !"".equals(commentId)){
    		//修改评论
    		flag = bd.updateComment(content,commentId);
    		if(flag){
                this.setMessage("1", "正常");
            } else {
                this.setMessage("0", "没取到数据");
            }
    	} else {
    		//新增评论
    		flag = bd.setComment(userId,userName,orgName,content,informationId);
    		//保存信息统计
    		new NewInformationBD().saveInfostat(new Long(informationId),new Long(channelId),"comment",
    				new Long(orgId),orgName,new Long(userId),userName,new Long(domain));
    		if(flag){
                this.setMessage("1", "正常");
            } else {
                this.setMessage("0", "没取到数据");
            }
    	}
    	return result;
    }
    
    /**
     * 根据用户信息和栏目Id判断用户是否可以新建信息
     * @return
     */
    public String canIssue(){
    	String result = "";
    	Element rootElement = doc.getRootElement();
    	String channelId = rootElement.getChild("channelId").getValue();
    	String userId = rootElement.getChild("userId").getValue();
    	String orgId = rootElement.getChild("orgId").getValue();
    	String orgIdString = rootElement.getChild("orgIdString").getValue();
    	String domain = rootElement.getChild("domain").getValue();
    	NewChannelBD bd = new NewChannelBD();
    	boolean canIssue;
		try {
			canIssue = bd.canIssue(userId, orgId, orgIdString, channelId, domain);
			if(canIssue){
	    		this.setMessage("1", "正常");
	    		result += "<canIssue>1</canIssue>";
	    	} else {
                this.setMessage("0", "没取到数据");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    /**
     * 取所有栏目(交换平台)
     * @return
     */
    public String getTransChannel(){
    	String result="";
        Element rootElement = doc.getRootElement();
        Element domainElement = rootElement.getChild("domain");
        Element pagerOffsetElement = rootElement.getChild("pager_offset");
        Element searchChannelNameElement = rootElement.getChild("searchChannelName");
        String domainId = domainElement.getValue();
        String pager_offset = pagerOffsetElement.getValue();
        String searchChannelName = searchChannelNameElement==null?"":searchChannelNameElement.getValue();
        NewChannelBD bd = new NewChannelBD();
        List list = new ArrayList();
        List resultList = new ArrayList();
        Map map = null;
        try {
        	map = bd.getTransChannel(domainId, pager_offset, searchChannelName);
        	list = (List)map.get("list");
        	javax.sql.DataSource ds = null;
            java.sql.Connection conn = null;
            java.sql.Statement stmt = null;
            java.sql.ResultSet rs = null;
            ds = new com.whir.common.util.DataSourceBase().getDataSource();
            conn = ds.getConnection();
            stmt = conn.createStatement();
            
            for(int i=0;i<list.size();i++){
            	Object[] obj = (Object[]) list.get(i);
            	if(obj[3].toString().equals("0")){//信息管理
            		obj[2] = "信息管理." + obj[2].toString();
            	}else if(obj[4] != null && "1".equals(obj[4].toString())) {//自定义信息频道
                    rs = stmt.executeQuery("select userChannelName from OA_USERCHANNEL where USERCHANNEL_ID=" + obj[3]);
                    if(rs.next()) {
                    	obj[2] = rs.getString(1) + "." + obj[2];
                    } else {
                        continue;
                    }
                } else {//单位主页
                	rs = stmt.executeQuery("select ORGNAMESTRING from ORG_ORGANIZATION where ORG_ID=" + obj[3]);
                    if(rs.next()) {
                    	obj[2] = rs.getString(1) + "." + obj[2];
                    } else {
                        continue;
                    }
                }
            	resultList.add(obj);
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }

        if(resultList!=null&&resultList.size()>0){
            this.setMessage("1", "正常");
            result += "<informationChannelList>";
	         for (int j = 0; j < resultList.size(); j++) {
	             Object[] obj = (Object[]) resultList.get(j);
	             result += "<informationChannel>";
	             result += "<channelId>" +obj[0] + "</channelId>";
	             result += "<channelName>" +strFormat(obj[1]) + "</channelName>";
	             result += "<channelNameString>"+strFormat(obj[2]) + "</channelNameString>";
	             result += "<channelType>"+strFormat(obj[3]) + "</channelType>";
	             result += "<userDefine>"+strFormat(obj[4]) + "</userDefine>";
	             result += "</informationChannel>";
	         }
	         result += "</informationChannelList><pageCount>"+map.get("pageCount")+"</pageCount>"+"<recordCount>"+map.get("recordCount")+"</recordCount>";
        }else{
        	this.setMessage("0", "没取到数据");
        }
        return result;
    }
    
    /**
     * 取图片新闻列表
     * @return
     */
    public String getImageInfoList(){
    	String result = "";
        Element rootElement = doc.getRootElement();
        Object[] inobj = new Object[25];
        inobj[0] = rootElement.getChild("domain")!=null?rootElement.getChild("domain").getValue():"";
        inobj[1] = rootElement.getChild("userId")!=null?rootElement.getChild("userId").getValue():"";
        inobj[2] = rootElement.getChild("orgId")!=null?rootElement.getChild("orgId").getValue():"";
        inobj[3] = rootElement.getChild("orgIdString")!=null?rootElement.getChild("orgIdString").getValue():"";
        inobj[4] = rootElement.getChild("type")!=null?rootElement.getChild("type").getValue():"";
        inobj[5] = rootElement.getChild("userDefine")!=null?rootElement.getChild("userDefine").getValue():"";
        inobj[6] = rootElement.getChild("informationId")!=null?rootElement.getChild("informationId").getValue():"";
        inobj[7] = rootElement.getChild("search")!=null?rootElement.getChild("search").getValue():"";
        inobj[8] = rootElement.getChild("userChannelName")!=null?rootElement.getChild("userChannelName").getValue():"";
        inobj[9] = rootElement.getChild("channelType")!=null?rootElement.getChild("channelType").getValue():"";
        inobj[10] = rootElement.getChild("depart")!=null?rootElement.getChild("depart").getValue():"";
        inobj[11] = rootElement.getChild("searchDate")!=null?rootElement.getChild("searchDate").getValue():"";
        inobj[12] = rootElement.getChild("searchBeginDate")!=null?rootElement.getChild("searchBeginDate").getValue():"";
        inobj[13] = rootElement.getChild("searchEndDate")!=null?rootElement.getChild("searchEndDate").getValue():"";
        inobj[14] = rootElement.getChild("searchIssuerName")!=null?rootElement.getChild("searchIssuerName").getValue():"";
        inobj[15] = "";
        inobj[16] = "";
        inobj[17] = rootElement.getChild("title")!=null?rootElement.getChild("title").getValue():"";
        inobj[18] = rootElement.getChild("informationKey")!=null?rootElement.getChild("informationKey").getValue():"";
        inobj[19] = rootElement.getChild("subtitle")!=null?rootElement.getChild("subtitle").getValue():"";
        inobj[20] = rootElement.getChild("append")!=null?rootElement.getChild("append").getValue():"";
        inobj[21] = rootElement.getChild("searchChannel")!=null?rootElement.getChild("searchChannel").getValue():"";
        inobj[22] = rootElement.getChild("pager_offset")!=null?rootElement.getChild("pager_offset").getValue():"";
        inobj[23] = rootElement.getChild("retrievalKey")!=null?rootElement.getChild("retrievalKey").getValue():"";
        inobj[24] = rootElement.getChild("pageSize")!=null?rootElement.getChild("pageSize").getValue():"";

        InformationService informationService = new InformationService();
        List list = informationService.getImageInformationListMobile(inobj);
        if (list != null && list.size() > 0) {
            this.setMessage("1", "正常");
            List list0=(List)list.get(0);
            List list1=(List)list.get(1);
            for(int ii=0;ii<list0.size();ii++){
                List list0ii=(List)list0.get(ii);
                result += "<infoList>";
                int index=0;
                result+="<informationId>"+list0ii.get(index++)+"</informationId>";
                result+="<informationTitle><![CDATA["+list0ii.get(index++)+"]]></informationTitle>";
                result+="<informationKits>"+list0ii.get(index++)+"</informationKits>";
                result+="<informationIssuer>"+list0ii.get(index++)+"</informationIssuer>";
                result+="<informationVersion>"+list0ii.get(index++)+"</informationVersion>";
                result+="<informationIssueTime>"+list0ii.get(index++)+"</informationIssueTime>";
                result+="<informationSummary><![CDATA["+list0ii.get(index++)+"]]></informationSummary>";
                result+="<informationHead>"+list0ii.get(index++)+"</informationHead>";
                result+="<informationType>"+list0ii.get(index++)+"</informationType>";
                result+="<informationCommonNum>"+list0ii.get(index++)+"</informationCommonNum>";
                result+="<channelName>"+strFormat(list0ii.get(index++))+"</channelName>";
                result+="<channelId>"+list0ii.get(index++)+"</channelId>";
                result+="<titleColor>"+list0ii.get(index++)+"</titleColor>";
                result+="<isConf>"+list0ii.get(index++)+"</isConf>";
                result+="<documentNo>"+list0ii.get(index++)+"</documentNo>";
                result+="<transmitToEzsite><![CDATA["+list0ii.get(index++)+"]]></transmitToEzsite>";
                result+="<informationModifyTime>"+list0ii.get(index++)+"</informationModifyTime>";
                result+="<orderCode>"+list0ii.get(index++)+"</orderCode>";
                result+="<informationIssueOrg><![CDATA["+list0ii.get(index++)+"]]></informationIssueOrg>";
                result+="<informationIsCommend>"+list0ii.get(index++)+"</informationIsCommend>";
                result+="<channelType>"+list0ii.get(index++)+"</channelType>";
                result += "</infoList>";
            }
            List list10=(List)list1.get(0);
            result += "<pager_offset>"+list10.get(0)+"</pager_offset>";
            result += "<recordCount>"+list10.get(1)+"</recordCount>";
        } else {
            this.setMessage("0", "没取到数据");
        }
        return result;
    }
    
    /**
     * 取能查看的所有自定义信息频道
     * @return String
     */
    public String  getAllUserChannel(){
        String result="";
        Element rootElement = doc.getRootElement();
        
        Element domainElement = rootElement.getChild("domain");
        Element userIdElement = rootElement.getChild("userId");
        Element orgIdElement = rootElement.getChild("orgId");
        Element orgIdStringElement = rootElement.getChild("orgIdString");
        
        String domainId = domainElement.getValue();
        String userId=userIdElement.getValue();
        String orgId=orgIdElement.getValue();
        String orgIdString = orgIdStringElement.getValue();

        NewChannelBD bd=new NewChannelBD();
        List list=new ArrayList();
        Map map = null;
        try {
        	map = bd.getAllUserChannel(domainId, userId, orgId, orgIdString);
        	list = (List)map.get("list");
       } catch (Exception ex) {
           	ex.printStackTrace();
       }

       if(list!=null&&list.size()>0){
            this.setMessage("1", "正常");
             //aaa.userChannelId, aaa.userChannelName, aaa.userChannelOrder,aaa.channelReadName
             for (int jj = 0; jj < list.size(); jj++) {
                     Object[] oobj = (Object[]) list.get(jj);
                     result += "<userChannel>";
                     result += "<userChannelId>" + oobj[0] + "</userChannelId>";
                     result += "<userChannelName>" + strFormat(oobj[1]) + "</userChannelName>";
                     result += "<userChannelOrder>"+ oobj[2] + "</userChannelOrder>";
                     result += "<channelReadName>"+ oobj[3] + "</channelReadName>";
                     result += "</userChannel>";
             }
             result += "<recordCount>"+map.get("recordCount")+"</recordCount>";
       }else{
          this.setMessage("0", "没取到数据");
       }
       return result;
    }
    
    /**
     *取所有能新增的且不走流程的栏目(11.4 -by jqq)
     * @return String
     * @throws Exception 
     */
    public String  getCanIssueChannel_noProcess(){
         String result="";
         Element rootElement = doc.getRootElement();
         Element domainElement = rootElement.getChild("domain");
         Element userIdElement = rootElement.getChild("userId");
         Element orgIdElement = rootElement.getChild("orgId");
         Element orgIdStringElement = rootElement.getChild("orgIdString");
         Element channelTypeElement = rootElement.getChild("channelType");
         Element userDefineElement = rootElement.getChild("userDefine");
         Element searchChannelNameElement = rootElement.getChild("searchChannelName");
         
         String domainId = domainElement.getValue();
         String userId = userIdElement.getValue();
         String orgId = orgIdElement.getValue();
         String orgIdString =orgIdStringElement.getValue();
         String channelType = channelTypeElement.getValue();
         String userDefine = userDefineElement.getValue() != null ? userDefineElement.getValue() : "";
         String searchChannelName=searchChannelNameElement==null?"":searchChannelNameElement.getValue();

         InformationService inforService = new InformationService();
         List list= new ArrayList();
         list = inforService.getCanIssueChannel_noProcess(userId, orgId, orgIdString, domainId, searchChannelName);
         
         if(list!=null&&list.size()>0){
               this.setMessage("1", "正常");
               //aaa.channelId,aaa.channelName,aaa.channelLevel,aaa.channelIdString,
               //aaa.channelType,aaa.userDefine,aaa.isYiBoChannel
               for (int jj = 0; jj < list.size(); jj++) {
                      Object[] oobj = (Object[]) list.get(jj);
                      result += "<infomationChannel>";
                      result += "<channelId>" +oobj[0] + "</channelId>";
                      result += "<channelName>" +strFormat(oobj[1]) + "</channelName>";
                      result += "<channelLevel>"+oobj[2] + "</channelLevel>";
                      result += "<channelIdString>" +oobj[3] + "</channelIdString>";
                      result += "</infomationChannel>";
               }
         } else {
        	 this.setMessage("0", "没取到数据");
         }
         return result;
    }
    
    /**
     *取所有能新增的栏目(包含是否走流程审核标识)(11.4 -by jqq)
     * @return String
     * @throws Exception 
     */
    public String  getCanIssueChannelAll(){
         String result="";
         Element rootElement = doc.getRootElement();
         Element domainElement = rootElement.getChild("domain");
         Element userIdElement = rootElement.getChild("userId");
         Element orgIdElement = rootElement.getChild("orgId");
         Element orgIdStringElement = rootElement.getChild("orgIdString");
         Element channelTypeElement = rootElement.getChild("channelType");
         Element userDefineElement = rootElement.getChild("userDefine");
         Element searchChannelNameElement = rootElement.getChild("searchChannelName");
         
         String domainId = domainElement.getValue();
         String userId = userIdElement.getValue();
         String orgId = orgIdElement.getValue();
         String orgIdString =orgIdStringElement.getValue();
         String channelType = channelTypeElement.getValue();
         String userDefine = userDefineElement.getValue() != null ? userDefineElement.getValue() : "";
         String searchChannelName=searchChannelNameElement==null?"":searchChannelNameElement.getValue();

         InformationService inforService = new InformationService();
         List list= new ArrayList();
         list = inforService.getCanIssueChannel_All(userId, orgId, orgIdString, domainId, searchChannelName);
         
         if(list!=null&&list.size()>0){
               this.setMessage("1", "正常");
               //aaa.channelId,aaa.channelName,aaa.channelLevel,aaa.channelIdString,
               //aaa.channelType,aaa.userDefine,aaa.isYiBoChannel,aaa.channelNeedCheckup
               for (int jj = 0; jj < list.size(); jj++) {
                      Object[] oobj = (Object[]) list.get(jj);
                      result += "<infomationChannel>";
                      result += "<channelId>" + oobj[0] + "</channelId>";
                      result += "<channelName>" + strFormat(oobj[1]) + "</channelName>";
                      result += "<channelLevel>"+ oobj[2] + "</channelLevel>";
                      result += "<channelIdString>" + oobj[3] + "</channelIdString>";
                      result += "<channelNeedCheckup>" + oobj[7] + "</channelNeedCheckup>";
                      result += "</infomationChannel>";
               }
         } else {
             this.setMessage("0", "没取到数据");
         }
         return result;
    }
    
    /**
     * 取所有一级栏目(包含是否可以新增栏目下信息的标识)
     * @return
     */
    public String getLevelOneChannel(){
        String result = "";
        Element rootElement = doc.getRootElement();
        String domainId = rootElement.getChild("domain").getValue();
        String userId = rootElement.getChild("userId").getValue();
        String orgId = rootElement.getChild("orgId").getValue();
        String orgIdString = rootElement.getChild("orgIdString").getValue();
        String channelType = rootElement.getChild("channelType").getValue();
        NewChannelBD bd = new NewChannelBD();
        List list = bd.getLevelOneChannel(userId, orgId, orgIdString, domainId, channelType);
        if(list!=null && list.size()>0) {
            this.setMessage("1", "正常");
            if(list!=null && list.size()>0){
                for(int i=0;i<list.size();i++){
                    Object[] obj = (Object[]) list.get(i);
                    result += "<informationChannel>";
                    result += "<channelId>" + obj[0].toString() + "</channelId>";
                    result += "<channelName><![CDATA["+obj[1].toString()+"]]></channelName>";
                    result += "<channelParentId>" + obj[2].toString() + "</channelParentId>";
                    result += "<channelIdString>" + obj[3].toString() + "</channelIdString>";
                    result += "<channelNameString><![CDATA[" + obj[4].toString() + "]]></channelNameString>";
                    result += "<channelType>" + obj[5].toString() + "</channelType>";
                    result += "<userDefine>" + obj[6].toString() + "</userDefine>";
                    result += "<hasInfomation>" + obj[7].toString() + "</hasInfomation>";
                    result += "<hasChildChannl>" + obj[8].toString() + "</hasChildChannl>";
                    result += "<channelNeedCheckup>" + obj[9].toString() + "</channelNeedCheckup>";
                    result += "<isCanAdd>" + obj[10].toString() + "</isCanAdd>";
                    result += "</informationChannel>";
                }
            }
        } else {
            this.setMessage("0", "没取到数据");
        }
        return result;
    }
    
    /**
     * 取所有栏目信息(包含是否可以新增栏目下信息的标识)
     * @return
     */
    public String getAllChannelWithAddFlag(){
        String result = "";
        Element rootElement = doc.getRootElement();
        String domainId = rootElement.getChild("domain").getValue();
        String userId = rootElement.getChild("userId").getValue();
        String orgId = rootElement.getChild("orgId").getValue();
        String orgIdString = rootElement.getChild("orgIdString").getValue();
        String channelType = rootElement.getChild("channelType").getValue();
        NewChannelBD bd = new NewChannelBD();
        List list = bd.getAllChannelWithAddFlag(userId, orgId, orgIdString, domainId, channelType);
        if(list!=null && list.size()>0) {
            this.setMessage("1", "正常");
            if(list!=null && list.size()>0){
                for(int i=0;i<list.size();i++){
                    Object[] obj = (Object[]) list.get(i);
                    result += "<informationChannel>";
                    result += "<channelId>" + obj[0].toString() + "</channelId>";
                    result += "<channelName><![CDATA["+obj[1].toString()+"]]></channelName>";
                    result += "<channelLevel>" + obj[2].toString() + "</channelLevel>";
                    result += "<channelIdString>" + obj[3].toString() + "</channelIdString>";
                    result += "<channelNeedCheckup>" + obj[4].toString() + "</channelNeedCheckup>";
                    result += "<isCanAdd>" + obj[6].toString() + "</isCanAdd>";
                    result += "</informationChannel>";
                }
                result += "<recordCount>"+ list.size() +"</recordCount>";
            }
        } else {
            this.setMessage("0", "没取到数据");
        }
        return result;
    }
    /**
     * 查询所有1级栏目详细信息（分页与分类查询）
     * @return
     */
    public String getAllInformationChannelwithFlag() {
        String result = "";
        Element rootElement = doc.getRootElement();
        String domainId = rootElement.getChild("domain").getValue();
        String userId = rootElement.getChild("userId").getValue();
        String orgId = rootElement.getChild("orgId").getValue();
        String orgIdString = rootElement.getChild("orgIdString").getValue();
        String channelType = rootElement.getChild("channelType")!=null ? rootElement.getChild("channelType").getValue() : "0";
        //20160525 -by jqq 接口增加栏目名称模块查询
        Element searchChannelNameElement = rootElement.getChild("searchChannelName");
        String  searchChannelName = searchChannelNameElement==null ? "" : searchChannelNameElement.getValue();
        //20160614 -by jqq 接口改造分页查询/分类查询栏目
        Element pageSizeElement = rootElement.getChild("pageSize");
        Element pager_offsetElement = rootElement.getChild("pager_offset");
        Element channelKindElement = rootElement.getChild("channelKind");
        //默认第一页，每页15条数据，默认分类0-信息栏目
        String  pageSize = pageSizeElement==null ? "15" : pageSizeElement.getValue();
        String  pager_offset = pager_offsetElement==null ? "0" : pager_offsetElement.getValue();
        //栏目的分类：0-信息栏目；1-单位主页；2-自定义频道
        String  channelKind = channelKindElement==null ? "0" : channelKindElement.getValue();
        
        NewChannelBD bd = new NewChannelBD();
        List list = bd.getAllInformationChannelwithFlag(
            userId,
            orgId,
            orgIdString,
            domainId,
            channelType,
            searchChannelName,
            pageSize,
            pager_offset,
            channelKind);
        if(list!=null && list.size()>0) {
            this.setMessage("1", "正常");
            if(list!=null && list.size()>0){
                for(int i=0;i<list.size()-1;i++){
                    Object[] obj = (Object[]) list.get(i);
                    result += "<informationChannel>";
                    result += "<channelId>" + obj[0].toString() + "</channelId>";
                    result += "<channelName><![CDATA["+obj[1].toString()+"]]></channelName>";
                    result += "<channelLevel>" + obj[2].toString() + "</channelLevel>";
                    result += "<channelType>" + obj[3].toString() + "</channelType>";
                    result += "<channelNeedCheckup>" + obj[4].toString() + "</channelNeedCheckup>";
                    result += "<isCanAdd>" + obj[6].toString() + "</isCanAdd>";
                    result += "<isView>" + obj[7].toString() + "</isView>";
                    result += "</informationChannel>";
                }
                result += "<recordCount>"+ list.get(list.size()-1).toString() +"</recordCount>";
            }
        } else {
            this.setMessage("0", "没取到数据");
        }
        return result;
    }
}
