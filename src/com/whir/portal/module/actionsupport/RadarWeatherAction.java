package com.whir.portal.module.actionsupport;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.PropertiesUtils;

public class RadarWeatherAction extends BaseActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7997456459475574484L;
	// action配置文件路径  
	private static final String ACTIONPATH = "ftppath.properties";  
	private static Properties props;
	/**
	 * 获取气象云图
	 * @author daiql
	 * @throws IOException 
	 * @since 1.2 2016-05-10
	 */
	public void getCardPhotoFile() throws IOException{
//		initializeDate();
//		Properties properties = props;
		Properties properties = PropertiesUtils.getProperties(ACTIONPATH);
		String qxzxftp_tq = properties.getProperty("qxzxftp_gms");
		System.out.println(qxzxftp_tq);
	    String fileName = getFiles(qxzxftp_tq);
	    String imgsrc = qxzxftp_tq+"\\"+fileName; 
	    String imgdist = qxzxftp_tq+"\\reduceImg\\"+fileName; 
	    System.out.println("imgsrc"+imgsrc);
	    System.out.println("imgdist"+imgdist);
	    boolean result = reduceImg(imgsrc,imgdist,450,360);
	    if(result){
	    	File photoFile = new File(imgdist);
			String contentType = "text/html; charset=UTF-8";
			String roadType = "inline";
			response.reset();
			response.setContentType(contentType);
			try {
				response.setHeader("Content-Disposition",roadType+ ";filename="+ new String(fileName.getBytes("gbk"), "ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			InputStream is = null;
			try {
				is = new FileInputStream(photoFile);
				BufferedInputStream br = new BufferedInputStream(is);
				byte[] buf = new byte[1024];
				int len = 0;
				OutputStream out = null;
				out = response.getOutputStream();
				while ((len = br.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				br.close();
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	public String getFiles(String filePath){
		StringBuffer str = new StringBuffer();
		String fileName = "1900_01_01_00_00";
		  File root = new File(filePath);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(!file.isDirectory()){
			    	 fileName  = compare_date(fileName,file.getName().substring(5, 20));
			     }     
		    }
		    str.append("FY2G_"+fileName+"_L_PJ1_Pic.GIF");
		    return str.toString();
	}
	
	public String compare_date(String DATE1, String DATE2) {
        String date = "";
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        Date date1= new Date();
        try {
        	Date dt1 = format1.parse(DATE1);
  		  	Date dt2 = format1.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
            	date1 = dt1;
            } else if (dt1.getTime() < dt2.getTime()) {
            	date1 = dt2;
            } else {
            	date1 = dt2;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
		date = format1.format(date1); 
        return date;
    }
	
	
	public Boolean reduceImg(String imgsrc, String imgdist, int widthdist,   
	        int heightdist) throws java.io.IOException {   
	    File srcfile = new File(imgsrc);   
		if (!srcfile.exists()) {   
		    return false;   
		}   
		Image src = javax.imageio.ImageIO.read(srcfile);   
  
		BufferedImage tag= new BufferedImage((int) widthdist, (int) heightdist,   
		        BufferedImage.TYPE_INT_RGB);   
		tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist,  Image.SCALE_SMOOTH), 0, 0,  null);   
		File file =new File(imgdist);    
		if(!file.exists()) {    
		   try {    
		       file.createNewFile();    
		   } catch (IOException e) {    
		       e.printStackTrace();    
		   }    
		}    
		FileOutputStream out = new FileOutputStream(imgdist);   
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);   
		encoder.encode(tag);   
		out.close();
		return true;  
	}  
	
	/**
	 * 下载航迹图查看器插件
	 * @author daiql
	 * @throws IOException 
	 * @since 1.2 2016-05-10
	 */
	public void getPluginDown() throws IOException{
		String filename = "D:\\jboss\\jboss-as\\server\\oa\\deploy\\defaultroot.war\\ftpfile\\TE.rar"; 
    	response.setContentType( "application/x-msdownload "); 
    	response.addHeader( "Content-Disposition ", 
    	"attachment; filename=\" " + java.net.URLEncoder.encode(filename, "UTF-8 ") + "\" "); 
    	try { 
    	java.io.OutputStream os = response.getOutputStream(); 
    	java.io.FileInputStream fis = new java.io.FileInputStream(filename); 

    	byte[] b = new byte[1024]; 
    	int i = 0; 
    	while ( (i = fis.read(b)) > 0) { 
    	os.write(b, 0, i); 
    	} 

    	fis.close(); 
    	os.flush(); 
    	os.close(); 
    	} 
    	catch (Exception e) { 
    	e.printStackTrace(); 
    	} 
	}
}
