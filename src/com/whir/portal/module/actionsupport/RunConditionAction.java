package com.whir.portal.module.actionsupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;

public class RunConditionAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 930014367691300648L;

	/**
     * 多文件下载


     */
    public void batchPack(){
    	String datetime = request.getParameter("datetime");
        try {
        	String customform = this.request.getSession().getServletContext()
             .getRealPath("/upload/customform/");
        	String filePath = customform+File.separator+"昨日运行情况打包"+File.separator;
        	File zipFile = new File(filePath);
        	System.out.println("111111111111"+filePath);
        	 if (!zipFile.exists()) {
        		 zipFile.mkdirs();
             }
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile+File.separator+datetime+".rar"));
            System.out.println("2222222222"+zipFile+File.separator+datetime+".rar");
            StringBuffer sb = new StringBuffer();
            String[] dateArray = datetime.split("-");
            sb.append(dateArray[0]).append(dateArray[1]);
            List<String> fileList = getFileName(datetime);
            if(fileList != null && fileList.size()>0){
                for(String file : fileList){
                	InputStream is= new FileInputStream(new File(customform+"/"+sb.toString()+"/"+file));
                    out.putNextEntry(new ZipEntry(new String(file.getBytes(),"gb2312")));	
                    byte[] buf=new byte[1048576];
                    int len=0;
                    while((len = is.read(buf)) != -1){
                        out.write(buf,0,len);
                    }
                    is.close();
                    out.closeEntry();
                }
                out.close();
            }
//            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(zipFile));
//            download(fis,"昨日运行情况.zip",zipFile.length()+"");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    
    public void batchDownload() throws FileNotFoundException{
    	String folder = request.getParameter("folder");
    	String fileName = "昨日运行情况.rar";
    	String customform = this.request.getSession().getServletContext()
        .getRealPath("/upload/customform/");
    	String filePath = customform+File.separator+folder+File.separator;
    	File file = new File(filePath+File.separator+fileName);
    	BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
    	System.out.println("33333333"+file.toString());
    	try {
	    	response.reset();
	        response.setHeader("Content-Disposition","attachment; filename=" + new String(fileName.getBytes("gbk"),"ISO8859_1"));
	        response.addHeader("Content-Length",file.length()+"");
	        
	        byte[] buf=new byte[1048576];
	        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
	        response.setContentType("application/x-msdownload");  
	        try {
	            int len=0;
	            while((len = fis.read(buf)) != -1){
	                toClient.write(buf,0,len);
	            }
	            fis.close();
	            toClient.flush();
	            
	        } catch (Exception e) {
	        	e.printStackTrace();
	        } finally {
	            toClient.close();
	        }
    	} catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    private void download(BufferedInputStream is,String fileName,String fileLength){
    	try {
	    	response.reset();
	        response.setHeader("Content-Disposition","attachment; filename=" + new String(fileName.getBytes("gbk"),"ISO8859_1"));
	        response.addHeader("Content-Length",fileLength);
	        
	        byte[] buf=new byte[1048576];
	        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
	        response.setContentType("application/x-msdownload");  
	        try {
	            int len=0;
	            while((len = is.read(buf)) != -1){
	            	System.out.println("2222");
	                toClient.write(buf,0,len);
	            }
	            is.close();
	            toClient.flush();
	            
	        } catch (Exception e) {
	        	e.printStackTrace();
	        } finally {
	            toClient.close();
	        }
    	} catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    
	public List<String> getFileName(String nowDate) throws SQLException{
		List<String> fileNameList = new ArrayList<String>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        String sql = "select whir$yesRunInfo_uploadAcce from whir$yesruninfo where  whir$yesRunInfo_uploadDate='"+nowDate+"'";
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        while(rs.next()){
	        	String fileNameStr = rs.getString(1);
	        	if(!"".equals(fileNameStr) && !fileNameStr.isEmpty()){
	        		String[] fileNameArray = fileNameStr.split(";");
	        		String[] fileNumNameArray = fileNameArray[0].split(",");
	        		for(String fileName:fileNumNameArray){
	        			fileNameList.add(fileName);
	        		}
	        	}
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
	    	if(pstmt != null){
	    	try
	    	{
	    	pstmt.close();
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
	    return fileNameList;
	}
}