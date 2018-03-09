package com.whir.ezflow.actionsupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.log4j.Logger;

import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;

public class EzFlowExportToExcelAction extends BaseActionSupport {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(EzFlowExportToExcelAction.class.getName());
	public static int charTitle = 12;// 标题字体大小
	public static int charNormal = 10;// 标题字体大小
	public static WritableFont titleFont = new WritableFont(WritableFont.createFont("宋体"), charTitle, WritableFont.BOLD);
	public static WritableFont normalFont = new WritableFont(WritableFont.createFont("宋体"), charNormal);
	public static WritableFont nf = new WritableFont(WritableFont.createFont("Times New Roman"), charNormal);
	public static WritableCellFormat titleFormat;//Excel表头样式
	public static WritableCellFormat normalFormat;//Excel正文样式
	public static WritableCellFormat numberFormat;
	
	
	// =============================打印Excel表格样式设置============================================
	static {
		try {
			/* ===========================打印Excel表格样式设置=============================== */
			// 添加带有字型Formatting的对象
			// 用于标题
			titleFormat = new jxl.write.WritableCellFormat(titleFont);
			titleFormat.setBorder(jxl.write.Border.ALL, BorderLineStyle.THIN); // 线条
			titleFormat.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
			titleFormat.setAlignment(Alignment.CENTRE); // 水平对齐
			titleFormat.setWrap(true); // 是否换行
			titleFormat.setBackground(Colour.GRAY_25);// 背景色暗灰-25%
			// 用于正文
			normalFormat = new jxl.write.WritableCellFormat(normalFont);
			normalFormat.setBorder(jxl.write.Border.ALL, BorderLineStyle.THIN); // 线条
			normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
			normalFormat.setAlignment(Alignment.CENTRE);// 水平对齐
			normalFormat.setWrap(true); // 是否换行

			// 用于带有formatting的Number对象
			// jxl.write.NumberFormat nf = new jxl.write.NumberFormat("#.##");
			numberFormat = new WritableCellFormat(nf);
			numberFormat.setBorder(jxl.write.Border.ALL, BorderLineStyle.THIN); // 线条
			numberFormat.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
			numberFormat.setAlignment(Alignment.CENTRE);// 水平对齐
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	public String toExcel3() throws Exception {
	    //response.reset();
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	    
	   String selectIds = request.getParameter("selectIds");
	   String[] processInstanceArray = selectIds.split(",");
	   
	   InputStream in = null;
	   OutputStream out = null;
	   OutputStream os = null;
	   File tempFile = null;
	    
	   Date date = new Date();
	   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
	   String strDate = format.format(date);
		// 输出到EXCEL
		WritableWorkbook wwb = null;
		try {
		    os = response.getOutputStream();
			String sheetName = request.getParameter("exportTitle");
			sheetName = sheetName.replaceAll(":", "").replaceAll("[)]", "")
			.replaceAll("[(]", "");
			// 这里解释一下
			// attachment; 这个代表要下载的，如果去掉就编程直接打开了
			// filename是文件名，另存为或者下载时，为默认的文件名
			response.setHeader("Content-Disposition", "attachment; filename="
			+ new String(sheetName.getBytes("GBK"), "ISO-8859-1")+strDate
			+ ".xls");
			String tempDir = System.getProperty("java.io.tmpdir");

	        WorkbookSettings wbSetting = new WorkbookSettings();
	        wbSetting.setGCDisabled(true);
	        wbSetting.setUseTemporaryFileDuringWrite(true);
	        wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempDir));

	        String tempfilename = System.currentTimeMillis() + "_export.xls";

	        tempFile = new File(tempDir + "/" + tempfilename);
			// 创建工作薄(Workbook)对象
			wwb = Workbook.createWorkbook(tempFile, wbSetting);
			// 创建一个可写入的工作表
			WritableSheet ws = wwb.createSheet(sheetName, 0);
			Label lab = null; 
			WritableFont   wf2   =   new   WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
	        WritableCellFormat wcfTitle = new WritableCellFormat(wf2);
	        wcfTitle.setBackground(jxl.format.Colour.IVORY);  //象牙白
	        wcfTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
	        wcfTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐
	        CellView navCellView = new CellView();  
	        navCellView.setAutosize(true); //设置自动大小
	        navCellView.setSize(18);
	        /*===========================表头设置===============================*/
	        lab = new Label(0, 0, "填报单位",wcfTitle); 
			ws.addCell(lab);
			ws.mergeCells(1,0,3,0);
			
			lab = new Label(0, 1, "填报人",wcfTitle); 
			ws.addCell(lab);
			ws.mergeCells(1,1,3,1);
			lab = new Label(19, 0, "填报时间",wcfTitle); 
			ws.mergeCells(19,0,20,0);
			ws.mergeCells(4,0,18,1);
			ws.mergeCells(21,0,24,0);
			ws.addCell(lab);
			lab = new Label(19, 1, "复核人",wcfTitle); 
			ws.mergeCells(19,1,20,1);
			ws.mergeCells(21,1,24,1);
			ws.addCell(lab);
			
			lab = new Label(0, 2, "塔台保障架次统计表",wcfTitle); 
			ws.mergeCells(0,2,24,2);
			ws.addCell(lab);
			
			lab = new Label(0, 4, "管制单位",wcfTitle); 
			ws.mergeCells(0,4,0,5);
			ws.addCell(lab);
			
			lab = new Label(1, 4, "起降架次",wcfTitle); 
			ws.mergeCells(1,4,14,4);
			ws.addCell(lab);
			
			lab = new Label(15, 4, "塔台飞越架次",wcfTitle); 
			ws.mergeCells(15,4,18,4);
			ws.addCell(lab);
			
			lab = new Label(19, 4, "总计",wcfTitle); 
			ws.mergeCells(19,4,19,5);
			ws.addCell(lab);
			
			lab = new Label(20, 4, "日高峰起降架次",wcfTitle); 
			ws.mergeCells(20,4,20,5);
			ws.addCell(lab);
			
			lab = new Label(21, 4, "高峰日期",wcfTitle); 
			ws.mergeCells(21,4,21,5);
			ws.addCell(lab);
			
			Label label111 = new Label(22, 4, "小时高峰起降架次",wcfTitle); 
			ws.mergeCells(22,4,22,5);
			ws.addCell(label111);
			
			lab = new Label(23, 4, "高峰时段",wcfTitle); 
			ws.mergeCells(23,4,23,5);
			ws.addCell(lab);
			
			lab = new Label(24, 4, "备注",wcfTitle); 
			ws.mergeCells(24,4,24,5);
			ws.addCell(lab);
			
			lab = new Label(1, 5, "班机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(2, 5, "专机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(3, 5, "加班",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(4, 5, "包机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(5, 5, "公司训练",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(6, 5, "航校训练",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(7, 5, "调机",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(8, 5, "公务",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(9, 5, "救灾急救",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(10, 5, "军航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(11, 5, "外航",wcfTitle); 
			ws.mergeCells(24,4,24,5);
			ws.addCell(lab);
			lab = new Label(12, 5, "其他",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(13, 5, "合计",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(14, 5, "比上年同期%",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(15, 5, "民航",wcfTitle); 
			ws.mergeCells(24,4,24,5);
			ws.addCell(lab);
			lab = new Label(16, 5, "军航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(17, 5, "外航",wcfTitle); 
			ws.addCell(lab);
			lab = new Label(18, 5, "合计",wcfTitle); 
			ws.addCell(lab);
			Map<String,String> map = getHeadData3(processInstanceArray[0]);
			for (Entry<String, String> entry : map.entrySet()) {
				if("sqr".equals(entry.getKey())){
					lab = new Label(1, 1, entry.getValue()); 
					ws.addCell(lab);
				}
				if("bm".equals(entry.getKey())){
					lab = new Label(1, 0, entry.getValue()); 
					ws.addCell(lab);
				}
				if("tbrq".equals(entry.getKey())){
					lab = new Label(21, 0, entry.getValue()); 
					ws.addCell(lab);
				}
				if("shr".equals(entry.getKey())){
					lab = new Label(21, 1, entry.getValue()); 
					ws.addCell(lab);
				}
			}
			int num = 0;
			for(int i=0;i<processInstanceArray.length;i++){
				List<Map<String, String>> list = getBodyData3(processInstanceArray[i]);
				for(int j=num;j<list.size()+num;j++){
					Map<String, String> bodymap = list.get(j-num);
		    	    lab = new Label(0,j+6,bodymap.get("gzdw"));
		    	    ws.addCell(lab);
		    	    lab = new Label(1,j+6,bodymap.get("banj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(2,j+6,bodymap.get("zhj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(3,j+6,bodymap.get("jb"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(4,j+6,bodymap.get("bj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(5,j+6,bodymap.get("gsxl"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(6,j+6,bodymap.get("hxxl"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(7,j+6,bodymap.get("dj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(8,j+6,bodymap.get("gw"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(9,j+6,bodymap.get("jzjj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(10,j+6,bodymap.get("jh"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(11,j+6,bodymap.get("wh"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(12,j+6,bodymap.get("qt"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(13,j+6,bodymap.get("hj"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(14,j+6,bodymap.get("bsntq"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(15,j+6,bodymap.get("mh"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(16,j+6,bodymap.get("jh2"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(17,j+6,bodymap.get("wh2"));
		    	    ws.addCell(lab);  
		    	    lab = new Label(18,j+6,bodymap.get("hj2"));
		    	    ws.addCell(lab);
		    	    lab = new Label(19,j+6,bodymap.get("zj"));
		    	    ws.addCell(lab); 
		    	    lab = new Label(20,j+6,bodymap.get("rgfqjjc"));
		    	    ws.addCell(lab); 
		    	    lab = new Label(21,j+6,bodymap.get("gfrq"));
		    	    ws.addCell(lab); 
		    	    lab = new Label(22,j+6,bodymap.get("xsgfqjjc"));
		    	    ws.addCell(lab); 
		    	    lab = new Label(23,j+6,bodymap.get("gfsd"));
		    	    ws.addCell(lab); 
		    	    lab = new Label(24,j+6,bodymap.get("bz"));
		    	    ws.addCell(lab); 
		        }
				num = list.size();
			}
			// 从内存中写入文件中
			wwb.write();
			// 关闭资源，释放内存
			wwb.close();
			
			in = new BufferedInputStream(new FileInputStream(tempDir + "/" + tempfilename));
	        out = new BufferedOutputStream(os);
            byte[] buf = new byte[8192];
	        int n = -1;
	        while (-1 != (n = in.read(buf, 0, buf.length))) {
		        out.write(buf, 0, n);
	        }
	        out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}finally {
	      try {
	          if (out != null)
	            out.close();
	        }
	        catch (Exception e2)
	        {
	        	e2.printStackTrace();
	        }
	        try {
	          if (in != null)
	            in.close();
	        }
	        catch (Exception e1)
	        {
	        	e1.printStackTrace();
	        }
	        if ((tempFile != null) && 
	            (tempFile.exists())) {
	            tempFile.delete();
            }
       }
		return null;
	}

	public Map<String,String> getHeadData3(String id){
	    DataSourceBase dsb = new DataSourceBase();
	    Map<String,String> map = new HashMap<String,String>();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select whir$ttbzjctjd_f3943,whir$ttbzjctjd_f3944,whir$ttbzjctjd_f3945,whir$ttbzjctjd_f3946 from (select distinct a.WORKRECORD_ID from WF_WORK a where a.EZFLOWPROCESSINSTANCEID='"+id+"') b, whir$ttbzjctjd c where b.WORKRECORD_ID=c.whir$ttbzjctjd_id";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	        	map.put("sqr", rs.getString(1));
	        	map.put("bm", rs.getString(2));
	        	map.put("tbrq", rs.getString(3));
	        	map.put("shr", rs.getString(4));
	      }
          rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null) {
		          conn.close();
		        }
		      }
		      catch (SQLException localSQLException1)
		      {
		      }

		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2)
		      {
		      }
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return map;
	}
	
	
	public List<Map<String, String>> getBodyData3(String id) throws SQLException{
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    StringBuffer sql = new StringBuffer();
	    ResultSet rs = null;
	    Statement stmt = null;
	    
	    try {  
	    	conn = dsb.getDataSource().getConnection();
		    stmt = conn.createStatement();
	        sql.append("select 	whir$ttbzjctjb_f3616,whir$ttbzjctjb_f3617,whir$ttbzjctjb_f3618,whir$ttbzjctjb_f3619," +
	        		"whir$ttbzjctjb_f3620,whir$ttbzjctjb_f3621,whir$ttbzjctjb_f3622,whir$ttbzjctjb_f3623,whir$ttbzjctjb_f3624," +
	        		"whir$ttbzjctjb_f3625,whir$ttbzjctjb_f3626,whir$ttbzjctjb_f3627,whir$ttbzjctjb_f3628,whir$ttbzjctjb_f3629," +
	        		"whir$ttbzjctjb_f3630,whir$ttbzjctjb_f3631,whir$ttbzjctjb_f3632,whir$ttbzjctjb_f3633," +
	        		"whir$ttbzjctjb_f3634,whir$ttbzjctjb_f3635,whir$ttbzjctjb_f3636,whir$ttbzjctjb_f3637," +
	        		"whir$ttbzjctjb_f3638,whir$ttbzjctjb_f3639,whir$ttbzjctjb_f3640 from (select distinct " +
	        		"a.WORKRECORD_ID from WF_WORK a where a.EZFLOWPROCESSINSTANCEID='"+id+"') b, whir$ttbzjctjb c " +
	        		"where b.WORKRECORD_ID=c.whir$ttbzjctjb_foreignkey");
	        rs = stmt.executeQuery(sql.toString());
	        while(rs.next()){
	           Map<String,String> map = new HashMap<String,String>();
	           //管制单位
	           map.put("gzdw",rs.getString(1));
	      	  //班机
	           map.put("banj",rs.getString(2));
	      	  //专机
	           map.put("zhj",rs.getString(3));
	      	  //加班
	           map.put("jb",rs.getString(4));
	      	  //包机
	           map.put("bj",rs.getString(5));
	      	  //公司训练
	           map.put("gsxl",rs.getString(6));
	      	  //航校训练
	           map.put("hxxl",rs.getString(7));
	      	  //调机
	           map.put("dj",rs.getString(8));
	      	  //公务
	           map.put("gw",rs.getString(9));
	      	  //救灾急救
	           map.put("jzjj",rs.getString(10));
	      	  //军航
	           map.put("jh",rs.getString(11));
	      	  //外航
	           map.put("wh",rs.getString(12));
	      	  //其他
	           map.put("qt",rs.getString(13));
	      	  //合计
	           map.put("hj",rs.getString(14));
	      	  //比上年同期
	           map.put("bsntq",rs.getString(15));
	      	  //民航
	           map.put("mh",rs.getString(16));
	      	  //军航2	
	           map.put("jh2",rs.getString(17));
	      	  //外航2
	           map.put("wh2",rs.getString(18));
	      	  //合计2
	           map.put("hj2",rs.getString(19));
	      	  //总计
	           map.put("zj",rs.getString(20));
	      	  //日高峰起降架次
	           map.put("rgfqjjc",rs.getString(21));
	      	  //高峰日期
	           map.put("gfrq",rs.getString(22));
	      	  //小时高峰起降架次
	           map.put("xsgfqjjc",rs.getString(23));
	      	  //高峰时段
	           map.put("gfsd",rs.getString(24));
	      	  //备注
      	 	  map.put("bz",rs.getString(25));
	          list.add(map);
	        }
	    } catch (Exception e) {
	      conn.rollback();
	      System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	//log
	    	}
	    	}
	    	if(stmt != null){
	    	try
	    	{
	    		stmt.close();
	    	}catch(Exception e)
	    	{
	    	}
	    	}
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
	    return list;
	}
	
	
	public String toExcel2() throws Exception {
		   //response.reset();
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		    
		   String selectIds = request.getParameter("selectIds");
		   String[] processInstanceArray = selectIds.split(",");
		   
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = request.getParameter("exportTitle");
				sheetName = sheetName.replaceAll(":", "").replaceAll("[)]", "")
				.replaceAll("[(]", "");
				// 这里解释一下
				// attachment; 这个代表要下载的，如果去掉就编程直接打开了
				// filename是文件名，另存为或者下载时，为默认的文件名
				response.setHeader("Content-Disposition", "attachment; filename="
				+ new String(sheetName.getBytes("GBK"), "ISO-8859-1")+strDate
				+ ".xls");
				String tempDir = System.getProperty("java.io.tmpdir");

		        WorkbookSettings wbSetting = new WorkbookSettings();
		        wbSetting.setGCDisabled(true);
		        wbSetting.setUseTemporaryFileDuringWrite(true);
		        wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempDir));

		        String tempfilename = System.currentTimeMillis() + "_export.xls";

		        tempFile = new File(tempDir + "/" + tempfilename);
				// 创建工作薄(Workbook)对象
				wwb = Workbook.createWorkbook(tempFile, wbSetting);
				// 创建一个可写入的工作表
				WritableSheet ws = wwb.createSheet(sheetName, 0);
				WritableFont   wf2   =   new   WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
		        WritableCellFormat wcfTitle = new WritableCellFormat(wf2);
		        wcfTitle.setBackground(jxl.format.Colour.IVORY);  //象牙白
		        wcfTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
		        wcfTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐
		        CellView navCellView = new CellView();  
		        navCellView.setAutosize(true); //设置自动大小
		        navCellView.setSize(18);
		        
		        Label lab = null;
		        lab = new Label(0, 0, "填报单位",wcfTitle); 
				ws.addCell(lab);
				ws.mergeCells(1,0,3,0);
				lab = new Label(0, 1, "填报人",wcfTitle); 
				ws.addCell(lab);
				ws.mergeCells(1,1,3,1);
				lab = new Label(6, 0, "填报时间",wcfTitle); 
				ws.mergeCells(6,0,7,0);
				ws.mergeCells(4,0,5,1);
				ws.mergeCells(8,0,9,0);
				ws.addCell(lab);
				lab = new Label(6, 1, "复核人",wcfTitle); 
				ws.mergeCells(6,1,7,1);
				ws.mergeCells(8,1,9,1);
				ws.addCell(lab);
				lab = new Label(0, 2, "进近（终端）、区域保障架次统计表",wcfTitle); 
				ws.mergeCells(0,2,9,2);
				ws.addCell(lab);
				lab = new Label(0, 3, "管制单位",wcfTitle); 
				ws.mergeCells(0,3,0,4);
				ws.addCell(lab);
				lab = new Label(1, 3, "进近（终端）保障架次",wcfTitle); 
				ws.mergeCells(1,3,4,3);
				ws.addCell(lab);
				lab = new Label(5, 3, "区域保障架次",wcfTitle); 
				ws.mergeCells(5,3,8,3);
				ws.addCell(lab);
				lab = new Label(9, 3, "",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(1, 4, "民航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 4, "军航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(3, 4, "日高峰",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(4, 4, "小计",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(5, 4, "民航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(6, 4, "军航",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(7, 4, "日高峰",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(8, 4, "小计",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(9, 4, "总计",wcfTitle); 
				ws.addCell(lab);
				/*===========================表头设置===============================*/
				Map<String,String> map = getHeadData2(processInstanceArray[0]);
				for (Entry<String, String> entry : map.entrySet()) {
					if("sqr".equals(entry.getKey())){
						lab = new Label(1, 1, entry.getValue()); 
						ws.addCell(lab);
					}
					if("bm".equals(entry.getKey())){
						lab = new Label(1, 0, entry.getValue()); 
						ws.addCell(lab);
					}
					if("tbrq".equals(entry.getKey())){
						lab = new Label(8, 0, entry.getValue()); 
						ws.addCell(lab);
					}
					if("shr".equals(entry.getKey())){
						lab = new Label(8, 1, entry.getValue()); 
						ws.addCell(lab);
					}
				}
				int num=0;
				for(int i=0;i<processInstanceArray.length;i++){
					List<Map<String, String>> list = getBodyData2(processInstanceArray[i]);
					for(int j=num;j<list.size()+num;j++){
						Map<String, String> bodymap = list.get(j-num);
			    	    lab = new Label(0,j+5,bodymap.get("gzdw"));
			    	    ws.addCell(lab);
			    	    lab = new Label(1,j+5,bodymap.get("jjmh"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(2,j+5,bodymap.get("jjjh"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(3,j+5,bodymap.get("jjrgf"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(4,j+5,bodymap.get("jjxj"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(5,j+5,bodymap.get("qymh"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(6,j+5,bodymap.get("qyjh"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(7,j+5,bodymap.get("qyrgf"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(8,j+5,bodymap.get("qyxj"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(9,j+5,bodymap.get("zj"));
			    	    ws.addCell(lab);  
			        }
					num = list.size();
				}
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
				in = new BufferedInputStream(new FileInputStream(tempDir + "/" + tempfilename));
		        out = new BufferedOutputStream(os);

	            byte[] buf = new byte[8192];
		        int n = -1;
		        while (-1 != (n = in.read(buf, 0, buf.length))) {
			        out.write(buf, 0, n);
		        }
		        out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}finally {
		      try {
		          if (out != null)
		            out.close();
		        }
		        catch (Exception localException3)
		        {
		        }
		        try {
		          if (in != null)
		            in.close();
		        }
		        catch (Exception localException4)
		        {
		        }
		        if ((tempFile != null) && 
		          (tempFile.exists())) {
		          tempFile.delete();
		        }

	       }
			return null;
		}
	
	
	public Map<String,String> getHeadData2(String id){
	    DataSourceBase dsb = new DataSourceBase();
	    Map<String,String> map = new HashMap<String,String>();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select whir$jjbzjctjb_f4038,whir$jjbzjctjb_f4039,whir$jjbzjctjb_f4040,whir$jjbzjctjb_f4041 from (select distinct a.WORKRECORD_ID from WF_WORK a where a.EZFLOWPROCESSINSTANCEID='"+id+"') b, whir$jjbzjctjb c where b.WORKRECORD_ID=c.whir$jjbzjctjb_id";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	        	map.put("bm", rs.getString(1));
	        	map.put("tbrq", rs.getString(2));
	        	map.put("sqr", rs.getString(3));
	        	map.put("shr", rs.getString(4));
	      }
          rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null) {
		          conn.close();
		        }
		      }
		      catch (SQLException localSQLException1)
		      {
		      }

		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2)
		      {
		      }
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return map;
	}
	
	
	public List<Map<String, String>> getBodyData2(String id) throws SQLException{
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    StringBuffer sql = new StringBuffer();
	    ResultSet rs = null;
	    Statement stmt = null;
	    
	    try {  
	    	conn = dsb.getDataSource().getConnection();
		    stmt = conn.createStatement();
	        sql.append("select 	whir$jjbzjctjzb_f4045,whir$jjbzjctjzb_f4046,whir$jjbzjctjzb_f4047,whir$jjbzjctjzb_f4048," +
	        		"whir$jjbzjctjzb_f4049,whir$jjbzjctjzb_f4050,whir$jjbzjctjzb_f4051,whir$jjbzjctjzb_f4052,whir$jjbzjctjzb_f4053," +
	        		"whir$jjbzjctjzb_f4054  from (select distinct " +
	        		"a.WORKRECORD_ID from WF_WORK a where a.EZFLOWPROCESSINSTANCEID='"+id+"') b, whir$jjbzjctjzb c " +
	        		"where b.WORKRECORD_ID=c.whir$jjbzjctjzb_foreignkey");
	        rs = stmt.executeQuery(sql.toString());
	        while(rs.next()){
	           Map<String,String> map = new HashMap<String,String>();
	           //管制单位
	           map.put("gzdw",rs.getString(1));
	         //进近民航
	           map.put("jjmh",rs.getString(2));
	           //进近军航
	           map.put("jjjh",rs.getString(3));
	         //进近日高峰
	           map.put("jjrgf",rs.getString(4));
	         //进近小计
	           map.put("jjxj",rs.getString(5));
	         //区域民航
	           map.put("qymh",rs.getString(6));
	         //区域军航
	           map.put("qyjh",rs.getString(7));
	         //区域日高峰	
	           map.put("qyrgf",rs.getString(8));
	         //区域小计
	           map.put("qyxj",rs.getString(9));
	          //总计
	           map.put("zj",rs.getString(10));
	           list.add(map);
	        }
	    } catch (Exception e) {
	      conn.rollback();
	      System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	}
	    	}
	    	if(stmt != null){
	    	try
	    	{
	    		stmt.close();
	    	}catch(Exception e)
	    	{
	    	}
	    	}
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
	    return list;
	}
	
	
	public String toExcel1() throws Exception {
		   //response.reset();
		   response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		    
		   String selectIds = request.getParameter("selectIds");
		   String[] processInstanceArray = selectIds.split(",");
		   
		   InputStream in = null;
		   OutputStream out = null;
		   OutputStream os = null;
		   File tempFile = null;
		   
		   Date date = new Date();
		   DateFormat format = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
		   String strDate = format.format(date);
			// 输出到EXCEL
			WritableWorkbook wwb = null;
			try {
				os = response.getOutputStream();
				String sheetName = request.getParameter("exportTitle");
				sheetName = sheetName.replaceAll(":", "").replaceAll("[)]", "")
				.replaceAll("[(]", "");
				// 这里解释一下
				// attachment; 这个代表要下载的，如果去掉就编程直接打开了
				// filename是文件名，另存为或者下载时，为默认的文件名
				response.setHeader("Content-Disposition", "attachment; filename="
				+ new String(sheetName.getBytes("GBK"), "ISO-8859-1")+strDate
				+ ".xls");
				String tempDir = System.getProperty("java.io.tmpdir");

		        WorkbookSettings wbSetting = new WorkbookSettings();
		        wbSetting.setGCDisabled(true);
		        wbSetting.setUseTemporaryFileDuringWrite(true);
		        wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempDir));

		        String tempfilename = System.currentTimeMillis() + "_export.xls";

		        tempFile = new File(tempDir + "/" + tempfilename);
				// 创建工作薄(Workbook)对象
				wwb = Workbook.createWorkbook(tempFile, wbSetting);
				// 创建一个可写入的工作表
				WritableSheet ws = wwb.createSheet(sheetName, 0);
				WritableFont   wf2   =   new   WritableFont(WritableFont.ARIAL,14,WritableFont.BOLD,false,UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.BLACK); // 定义格式 字体 下划线 斜体 粗体 颜色
		        WritableCellFormat wcfTitle = new WritableCellFormat(wf2);
		        wcfTitle.setBackground(jxl.format.Colour.IVORY);  //象牙白
		        wcfTitle.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN,jxl.format.Colour.BLACK); //BorderLineStyle边框
		        wcfTitle.setAlignment(Alignment.CENTRE); //设置垂直对齐
		        CellView navCellView = new CellView();  
		        navCellView.setAutosize(true); //设置自动大小
		        navCellView.setSize(18);
		        
		        Label lab = null; 
		        lab = new Label(0, 0, "地区空管局",wcfTitle); 
				ws.mergeCells(1,0,2,0);
				ws.addCell(lab);
				
				lab = new Label(0, 1, "机场",wcfTitle); 
				ws.mergeCells(1,1,2,1);
				ws.addCell(lab);
				
				lab = new Label(0, 2, "填报时间",wcfTitle); 
				ws.mergeCells(1,2,2,2);
				ws.addCell(lab);
				
				lab = new Label(0, 3, "填报人",wcfTitle); 
				ws.mergeCells(1,3,2,3);
				ws.addCell(lab);
				
				lab = new Label(0, 4, "复核人",wcfTitle); 
				ws.mergeCells(1,4,2,4);
				ws.addCell(lab);
				
				lab = new Label(0, 5, "华东地区民航气象工作质量统计表",wcfTitle); 
				ws.mergeCells(0,5,8,5);
				ws.addCell(lab);
				
				lab = new Label(0, 6, "气象台名称",wcfTitle); 
				ws.mergeCells(0,6,0,7);
				ws.addCell(lab);
				
				lab = new Label(1, 6, "全要素准确率（%）",wcfTitle); 
				ws.mergeCells(1,6,2,6);
				ws.addCell(lab);
				
				lab = new Label(3, 6, "重要天气准确率（ / ）",wcfTitle); 
				ws.mergeCells(3,6,6,6);
				ws.addCell(lab);
				
				lab = new Label(7, 6, "重要天气准确率（%）",wcfTitle); 
				ws.mergeCells(7,6,7,7);
				ws.addCell(lab);
				
				lab = new Label(8, 6, "观测错情率（‰0）",wcfTitle); 
				ws.mergeCells(8,6,8,7);
				ws.addCell(lab);
				
				
				lab = new Label(1, 7, "FC",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(2, 7, "FT",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(3, 7, "命中率",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(4, 7, "虚警率",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(5, 7, "漏报率",wcfTitle); 
				ws.addCell(lab);
				lab = new Label(6, 7, "临界成功指数",wcfTitle); 
				ws.addCell(lab);
				/*===========================表头设置===============================*/
				Map<String,String> map = getHeadData1(processInstanceArray[0]);

				for (Entry<String, String> entry : map.entrySet()) {
					if("sqr".equals(entry.getKey())){
						lab = new Label(1, 3, entry.getValue()); 
						ws.addCell(lab);
					}
					if("bm".equals(entry.getKey())){
						lab = new Label(1, 0, entry.getValue()); 
						ws.addCell(lab);
					}
					if("tbrq".equals(entry.getKey())){
						lab = new Label(1, 2, entry.getValue()); 
						ws.addCell(lab);
					}
					if("shr".equals(entry.getKey())){
						lab = new Label(1, 4, entry.getValue()); 
						ws.addCell(lab);
					}
				}
				int num = 0;
				for(int i=0;i<processInstanceArray.length;i++){
					List<Map<String, String>> list = getBodyData1(processInstanceArray[i]);
					for(int j=num;j<list.size()+num;j++){
						Map<String, String> bodymap = list.get(j-num);
			    	    lab = new Label(0,j+8,bodymap.get("qxtmc"));
			    	    ws.addCell(lab);
			    	    lab = new Label(1,j+8,bodymap.get("qysfc"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(2,j+8,bodymap.get("qysft"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(3,j+8,bodymap.get("zytqmzl"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(4,j+8,bodymap.get("zytqxjl"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(5,j+8,bodymap.get("zytqlbl"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(6,j+8,bodymap.get("zytqljcgzs"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(7,j+8,bodymap.get("zytqzql"));
			    	    ws.addCell(lab);  
			    	    lab = new Label(8,j+8,bodymap.get("gccql"));
			    	    ws.addCell(lab);  
			        }
					num = list.size();
				}
				// 从内存中写入文件中
				wwb.write();
				// 关闭资源，释放内存
				wwb.close();
				in = new BufferedInputStream(new FileInputStream(tempDir + "/" + tempfilename));
		        out = new BufferedOutputStream(os);

	            byte[] buf = new byte[8192];
		        int n = -1;
		        while (-1 != (n = in.read(buf, 0, buf.length))) {
			        out.write(buf, 0, n);
		        }
		        out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}finally {
		      try {
		          if (out != null)
		            out.close();
		        }
		        catch (Exception localException3)
		        {
		        }
		        try {
		          if (in != null)
		            in.close();
		        }
		        catch (Exception localException4)
		        {
		        }
		        if ((tempFile != null) && 
		          (tempFile.exists())) {
		          tempFile.delete();
		        }

	       }
			return null;
		}
	
	
	public Map<String,String> getHeadData1(String id){
	    DataSourceBase dsb = new DataSourceBase();
	    Map<String,String> map = new HashMap<String,String>();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select whir$qxgzzltjb_f4012,whir$qxgzzltjb_f4013,whir$qxgzzltjb_f4014,whir$qxgzzltjb_f4015 from (select distinct a.WORKRECORD_ID from WF_WORK a where a.EZFLOWPROCESSINSTANCEID='"+id+"') b, whir$qxgzzltjb c where b.WORKRECORD_ID=c.whir$qxgzzltjb_id";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	        	map.put("bm", rs.getString(1));
	        	map.put("tbrq", rs.getString(2));
	        	map.put("sqr", rs.getString(3));
	        	map.put("shr", rs.getString(4));
	      }
          rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null) {
		          conn.close();
		        }
		      }
		      catch (SQLException localSQLException1)
		      {
		      }

		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2)
		      {
		      }
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return map;
	}
	
	
	public List<Map<String, String>> getBodyData1(String id) throws SQLException{
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    StringBuffer sql = new StringBuffer();
	    ResultSet rs = null;
	    Statement stmt = null;
	    
	    try {  
	    	conn = dsb.getDataSource().getConnection();
		    stmt = conn.createStatement();
	        sql.append("select 	whir$qxgzzltjzb_f4018,whir$qxgzzltjzb_f4019,whir$qxgzzltjzb_f4020,whir$qxgzzltjzb_f4021," +
	        		"whir$qxgzzltjzb_f4022,whir$qxgzzltjzb_f4023,whir$qxgzzltjzb_f4024,whir$qxgzzltjzb_f4025,whir$qxgzzltjzb_f4026" +
	        		" from (select distinct " +
	        		"a.WORKRECORD_ID from WF_WORK a where a.EZFLOWPROCESSINSTANCEID='"+id+"') b, whir$qxgzzltjzb c " +
	        		"where b.WORKRECORD_ID=c.whir$qxgzzltjzb_foreignkey");
	        rs = stmt.executeQuery(sql.toString());
	        while(rs.next()){
	           Map<String,String> map = new HashMap<String,String>();
	         //气象台名称
	           map.put("qxtmc",rs.getString(1));
	         //全要素FC
	           map.put("qysfc",rs.getString(2));
	         //全要素FT
	           map.put("qysft",rs.getString(3));
	         //重要天气命中率
	           map.put("zytqmzl",rs.getString(4));
	         //重要天气虚警率
	           map.put("zytqxjl",rs.getString(5));
	         //重要天气漏报率;
	           map.put("zytqlbl",rs.getString(6));
	         //重要天气临界成功指数
	           map.put("zytqljcgzs",rs.getString(7));
	         //重要天气准确率
	           map.put("zytqzql",rs.getString(8));
	         //观测错情率
	           map.put("gccql",rs.getString(9));
	           list.add(map);
	        }
	    } catch (Exception e) {
	      conn.rollback();
	      System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	//log
	    	}
	    	}
	    	if(stmt != null){
	    	try
	    	{
	    		stmt.close();
	    	}catch(Exception e)
	    	{
	    	}
	    	}
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
	    return list;
	}
}
