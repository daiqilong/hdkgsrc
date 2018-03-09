package com.whir.portal.servlet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.whir.portal.service.StrBinaryTurn;

public class CopyOfServerPortListener implements ServletContextListener {
	
	private MyThread myThread;

	public void contextInitialized(ServletContextEvent sce)
	  {
	    System.out.println("==============================容器装载");
	    String str = null;
		if (str == null && myThread == null) {
			myThread = new MyThread();
			new Thread(myThread).start();
		}
	  }
	  /**
	   * 自定义一个 Class 线程类继承自线程类，重写 run() 方法，用于从后台获取并处理数据
	   * 
	   * @author Champion.Wong
	   * 
	   */
	  class MyThread implements Runnable {
		  byte[] buf = new byte[1024];
		    private int UDP_PORT=4001;
			public void run() {
				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket(UDP_PORT);
				} catch (BindException e) {
					System.out.println("UDP端口使用中...请重关闭程序启服务器");
				} catch (SocketException e) {
					e.printStackTrace();
				}
				while (ds != null) {
					DatagramPacket dp = new DatagramPacket(buf, buf.length);
					try {
						ds.receive(dp);
						System.out.println("2222222222222222222");
						byte[] contentInBytes = dp.getData();
						String str = StrBinaryTurn.byte2hex(contentInBytes);
						System.out.println("111111111111111111111"+str);
						//该包的数据长度
						int len = 0;
						String jiequhou = "";
						//fs是16进制数值
						String fs = "";
						//fsBinary是二进制数值
						String fsBinary ="";
						//sjybsw是数据源标识位
						String sjybsw ="";
						//mbbgms是“目标报告描述”
						String mbbgms ="";
						//hjph是“飞机呼号/批号”
						String hjph ="";
						//hjh是“航迹号”
						String hjh ="";
						//jwd是“用直角坐标计算的目标位置(统一转成经纬度)”
						String jwd ="";
						//cmsgd是“用二进制数表示的C模式高度”
						String cmsgd ="";
						//gdgd是“用二进制数表示的计划高度”
						String gdgd ="";
						//zdgd是“用二进制数表示的指定高度”
						String zdgd ="";
						//hjzt是“航迹状态”
						String hjzt ="";
						//jdhjsd是“用极坐标计算的航迹速度”
						String jdhjsd ="";
						//amsdm是“用八进制数表示的3/A模式代码”
						String amsdm ="";
						//hjzl是“航迹质量”
						String hjzl ="";
						//fxybggd是“用二进制数表示的飞行员报告高度”
						String fxybggd ="";
						//rsj是“日时间”
						String rsj ="";
						//spxjsl是“计算的爬升/下降速率”
						String spxjsl ="";
						
						//003表示该包是综合雷达数据
						if("000003".equals(str.substring(0, 6))){
							len = Integer.valueOf(str.substring(6, 10),16);
							/*flag = 1表示FS位(FS位至少一个字节长度，
							每一位按传输先后分别为F1,F2,...Fx。
							Fi=0对应的数据段不存在，Fi=0对应的数据段存在，
							Fx=0扩展结束，FX=1表示向后扩展一位)*/
							int flag = 0;
							String str1 = str.substring(10, str.length());
							Map<String,String> map1 = null;
							//FS位
							if(flag == 0){
								map1 = new HashMap<String,String>();
								map1 = kuozhanzijie(str1);
								fs = map1.get("jiequ");
								jiequhou = map1.get("jiequhou");
								flag++;
							}
							System.out.println("fs位:"+fs);
							//fsBinary是二进制数值位确定对应的数据段是否存在
							fsBinary = StrBinaryTurn.hexString2binaryString(fs);
							
							System.out.println("fs二进制:"+fsBinary);
							//数据源标识位(固定二个字节长度数据项)
							if(flag == 1 && !jiequhou.isEmpty()){
								//1表示"数据源标识位"数据段存在且是固定二个字节长度
								if("1".equals(fsBinary.substring(flag-1, flag))){
									sjybsw=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								System.out.println("数据源标识位:"+sjybsw);
								flag++;
							}
							//目标报告描述(可变长度数据项)
							if(flag == 2 && !jiequhou.isEmpty()){
								//1表示"目标报告描述"数据段存在且是长度不固定可以扩展
								if("1".equals(fsBinary.substring(flag-1, flag))){
									map1 = new HashMap<String,String>();
									map1 = kuozhanzijie(jiequhou);
									mbbgms = map1.get("jiequ");
									jiequhou = map1.get("jiequhou");
								}
								flag++;
							}
							System.out.println("目标报告描述:"+mbbgms);
							//飞机呼号/批号(可变长度数据项)
							if(flag == 3 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									map1 = new HashMap<String,String>();
									map1 = kuozhanzijie(jiequhou);
									hjph = map1.get("jiequ");
									jiequhou = map1.get("jiequhou");
								}
								flag++;
							}
							System.out.println("飞机呼号/批号:"+hjph);
							//航迹号(固定二个字节长度数据项)
							if(flag == 4 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									hjh=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								flag++;
							}
							System.out.println("航迹号:"+hjh);
							
							//用直角坐标计算的目标位置(统一转成经纬度)(固定四个字节长度数据项)
							if(flag == 5 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									jwd=jiequhou.substring(0, jiequhou.length()<8 ? jiequhou.length() : 8);
									jiequhou=jiequhou.substring(jiequhou.length()<8 ? jiequhou.length() : 8);
								}
								flag++;
							}
							System.out.println("16进制经纬度:"+jwd);
							
							//用二进制数表示的C模式高度(固定二个字节长度数据项)
							if(flag == 6 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									cmsgd=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								flag++;
							}
							System.out.println("用二进制数表示的C模式高度:"+cmsgd);
							
							
							//用二进制数表示的计划高度(固定二个字节长度数据项)
							if(flag == 7 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									gdgd=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								flag++;
							}
							System.out.println("用二进制数表示的计划高度:"+gdgd);
							
							//扩展指示符(-)
							if(flag == 8){
								flag++;
							}
							//用二进制数表示的指定高度(固定二个字节长度数据项)
							if(flag == 9 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									zdgd=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								flag++;
							}
							System.out.println("用二进制数表示的指定高度:"+zdgd);
							
							//航迹状态(可变长度数据项)
							if(flag == 10 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									map1 = new HashMap<String,String>();
									map1 = kuozhanzijie(jiequhou);
									hjzt = map1.get("jiequ");
									jiequhou = map1.get("jiequhou");
								}
								flag++;
							}
							System.out.println("航迹状态:"+hjzt);
							
							//用极坐标计算的航迹速度(固定四个字节长度数据项)
							if(flag == 11 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									jdhjsd=jiequhou.substring(0, jiequhou.length()<8 ? jiequhou.length() : 8);
									jiequhou=jiequhou.substring(jiequhou.length()<8 ? jiequhou.length() : 8);
								}
								flag++;
							}
							System.out.println("用极坐标计算的航迹速度:"+jdhjsd);
							
							//用八进制数表示的3/A模式代码(固定二个字节长度数据项)
							if(flag == 12 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									amsdm=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								flag++;
							}
							System.out.println("用八进制数表示的3/A模式代码:"+amsdm);
							
							//航迹质量(可变长度数据项)
							if(flag == 13 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									map1 = new HashMap<String,String>();
									map1 = kuozhanzijie(jiequhou);
									hjzl = map1.get("jiequ");
									jiequhou = map1.get("jiequhou");
								}
								flag++;
							}
							System.out.println("航迹质量:"+hjzl);
							
							//用二进制数表示的飞行员报告高度(固定二个字节长度数据项)
							if(flag == 14 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									fxybggd=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								flag++;
							}
							System.out.println("用二进制数表示的飞行员报告高度:"+fxybggd);
							
							//日时间(固定三个字节长度数据项)
							if(flag == 15 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									rsj=jiequhou.substring(0, jiequhou.length()<6 ? jiequhou.length() : 6);
									jiequhou=jiequhou.substring(jiequhou.length()<6 ? jiequhou.length() : 6);
								}
								flag++;
							}
							System.out.println("日时间:"+rsj);
							
							//扩展指示符(-)
							if(flag == 16){
								flag++;
							}
							
							//计算的爬升/下降速率(固定二个字节长度数据项)
							if(flag == 17 && !jiequhou.isEmpty()){
								if("1".equals(fsBinary.substring(flag-1, flag))){
									spxjsl=jiequhou.substring(0, jiequhou.length()<4 ? jiequhou.length() : 4);
									jiequhou=jiequhou.substring(jiequhou.length()<4 ? jiequhou.length() : 4);
								}
								flag++;
							}
							System.out.println("计算的爬升/下降速率:"+spxjsl);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
	  }
	  
	  public void contextDestroyed(ServletContextEvent sce)
	  {
	    System.out.println("==============================容器销毁");
	    if (myThread != null && new Thread(myThread).isInterrupted()) {
	    	new Thread(myThread).interrupt();
		}
	  }
	  
	  public Map<String,String> subStr(String str, int subSLength) throws UnsupportedEncodingException{
			Map<String,String> map = new HashMap<String,String>();
	 		if (str == null)  
			    return null;  
				else{ 
				    int tempSubLength = subSLength;//截取字节数
				    String subStr = str.substring(0, str.length()<subSLength ? str.length() : subSLength);//截取的子串  
				    int subStrByetsL = subStr.getBytes("GBK").length;//截取子串的字节长度 
				    // 说明截取的字符串中包含有汉字  
				    while (subStrByetsL > tempSubLength){  
				 	   int subSLengthTemp = --subSLength;
				        subStr = str.substring(0, subSLengthTemp>str.length() ? str.length() : subSLengthTemp);  
				        subStrByetsL = subStr.getBytes("GBK").length;
				    }
				    map.put("jiequ", subStr);
				    map.put("jiequhou", str.substring(2, str.length()));
				    return map; 
				}
			}
		
		public Map<String,String> kuozhanzijie(String str1) throws UnsupportedEncodingException{
			boolean flag = true;
			Map<String,String> returnMap = new HashMap<String,String>();
			Map<String,String> map = null;
			StringBuffer jiequSb = new StringBuffer();
			String jiequhouSb = "";
			while(flag){
				map = new HashMap<String,String>();
				map = subStr(str1,2);
				String erjinzhi = StrBinaryTurn.hexString2binaryString(map.get("jiequ"));
				if("1".equals(erjinzhi.substring(erjinzhi.length()-1))){
					jiequSb.append(map.get("jiequ"));
					jiequhouSb=map.get("jiequhou");
					str1 = map.get("jiequhou");
				}  else {
					jiequSb.append(map.get("jiequ"));
					jiequhouSb = map.get("jiequhou");
					flag=false;
				}
			}
			returnMap.put("jiequ", jiequSb.toString());
			returnMap.put("jiequhou", jiequhouSb.toString());
			return returnMap;
		}
}

/*package yhb;
import java.io.IOException;
import java.net.*;
不知道是客户端还是服务器端所以在MAIN方法里只是起了一个处理UDP线程
 * 
public class CSDNudp {
	public static void main(String[] args)
	{
		new Thread(new UDPThread()).start();
	}

}

class UDPThread implements Runnable {

	byte[] buf = new byte[1024];
    private int UDP_PORT=9999;
	public void run() {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(UDP_PORT);
		} catch (BindException e) {
			System.out.println("UDP端口使用中...请重关闭程序启服务器");
		} catch (SocketException e) {
			e.printStackTrace();
		}
用这个来处理循环接收数据包比较合理，服务器接收数据包一般都是转发至客户端，而客户端接收数据包一般处理后再发送到服务器，很复杂。不会
 close()掉，下面只是简单的System.out.println("收到数据包!")
		while (ds != null) {
			DatagramPacket dp = new DatagramPacket(buf, buf.length);

			try {
				ds.receive(dp);
				System.out.println("收到数据包!");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
*/
/*public void run() {
	while (!this.isInterrupted()) {// 线程未中断执行循环
		try {
			Thread.sleep(2000); //每隔2000ms执行一次
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		 ------------------ 开始执行 ---------------------------
		System.out.println("____FUCK TIME:" + System.currentTimeMillis());
		try {
	    	ServerSocket serverSocket = new ServerSocket(3000);
	    	while (true) {
	    	     Socket socket = null;
	    	     socket = serverSocket.accept(); // 等待客户连接
	    	     DatagramSocket ds = new DatagramSocket(socket.getPort());
	    	     byte[] buf = new byte[1024];
	   	      	 DatagramPacket dp = new DatagramPacket(buf, 1024);
	   	      	 ds.receive(dp);
	   	      	 String strInfo = new String(dp.getData(), 0, dp.getLength()) + 
	   	         " from " + dp.getAddress().getHostAddress() + ":" + dp.getPort();
	   	      	 System.out.println(strInfo);
	   	      	 ds.close();
  	    }
	    } catch (SocketException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	package com.whir.portal.servlet;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.whir.common.util.DataSourceBase;
import com.whir.portal.service.StrBinaryTurn;

public class ServerPortListener implements ServletContextListener {
	
	private MyThread myThread;
	private StringBuffer sb = new StringBuffer();
	private List<String> list = new ArrayList<String>();

	public void contextInitialized(ServletContextEvent sce)
	  {
	    System.out.println("==============================容器装载");
	    String str = null;
		if (str == null && myThread == null) {
			myThread = new MyThread();
			new Thread(myThread).start();
		}
	  }
	  /**
	   * 自定义一个 Class 线程类继承自线程类，重写 run() 方法，用于从后台获取并处理数据
	   * 
	   * @author Champion.Wong
	   * 
	   */
	  class MyThread implements Runnable {
		    byte[] buf = new byte[1024];
		    private int UDP_PORT=4001;
			public void run() {
				DatagramSocket ds = null;
				try {
					ds = new DatagramSocket(UDP_PORT);
				} catch (BindException e) {
					System.out.println("UDP端口使用中...请重关闭程序启服务器");
				} catch (SocketException e) {
					e.printStackTrace();
				}
				while (ds != null) {
					DatagramPacket dp = new DatagramPacket(buf, buf.length);
					try {
						ds.receive(dp);
						byte[] contentInBytes = dp.getData();
						String str = StrBinaryTurn.byte2hex(contentInBytes);
//						String content = StrBinaryTurn.hex2bin(str.substring(0, 2));
						sb.append(StrBinaryTurn.hex2bin(str.substring(0, 2)));
						int startIndex = sb.indexOf("ZCZC");
				        int endIndex = sb.indexOf("NNNN");
				        
				        if (startIndex != -1 && endIndex != -1)//存在一次完整的xml数据
				        {
				            String xmlData = sb.substring(startIndex + 4, endIndex - startIndex);
				            list.add(xmlData);
				            sb.delete(startIndex, endIndex + 4);
				            if(list.size()==10){
				            	if(PraseXMLData(list)){
				            		list=list.subList(10, list.size());
				            	}
				            }
				        }
				        else if (endIndex != -1)//只存在结束标识符，只接受到一般数据，无用
				        {
				        	sb.delete(0, endIndex + 4);
				        }
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JDOMException e) {
						e.printStackTrace();
					}

				}

			}
	  }
	  
	  public void contextDestroyed(ServletContextEvent sce)
	  {
	    System.out.println("==============================容器销毁");
	    if (myThread != null && new Thread(myThread).isInterrupted()) {
	    	new Thread(myThread).interrupt();
		}
	  }
	  
	  public boolean PraseXMLData(List<String> xmlList) throws JDOMException, IOException
      {
	      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	      Date date= new Date();
	      String dateStr = format.format(date);
		  boolean flag=false;
		  List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		  for(String xmlData:xmlList){
			  System.out.println("==="+xmlData);
			  Map<String,String> map= new HashMap<String,String>();
			  StringReader read = new StringReader(xmlData);
			  InputSource source = new InputSource(read);
			  SAXBuilder sb = new SAXBuilder();
			  Document doc = sb.build(source);
			  Element rootElement = doc.getRootElement(); // 获取根节点
			  List<Element> metaList = rootElement.getChildren("META");
              //获取传输标签
        	  for (Element element : metaList) {
                  //获取name属性值
        		  map.put("time", element.getChildText("TIME"));
        		  map.put("qdm", element.getChildText("QDM"));
        		  map.put("qrc", element.getChildText("QRC"));
        		  map.put("ddm", element.getChildText("DDM"));
        		  map.put("dst", element.getChildText("DST"));
        		  map.put("msgType", element.getChildText("MSGTYPE"));
              }
        	  List<Element> bizList = rootElement.getChildren("BIZ");
              //获取传输标签
        	  for (Element element : bizList) {
                  //获取name属性值
        		  map.put("key", element.getChildText("KEY"));
        		  map.put("retc", element.getChildText("RETC"));
        		  map.put("rets", element.getChildText("RETS"));
        		  map.put("rid", element.getChildText("RID"));
        		  map.put("mid", element.getChildText("MID"));
              }
        	  List<Element> datList = rootElement.getChildren("DAT");
        	  for (Element element : datList) {
        		  map.put("sqcnum", element.getChildText("SQCNUM"));
        		  
        		  List<Element> flightIdentityList = element.getChildren("FlightIdentity");
        		  List<Element> stateTimeList = element.getChildren("StateTime");
        		  List<Element> flightInfoList = element.getChildren("FlightInfo");
                  //获取name属性值
        		  for (Element flightIdentityElement : flightIdentityList) {
                      //获取name属性值
        			  map.put("fuid", element.getChildText("FUID"));
        			  map.put("flno", element.getChildText("FLNO"));
        			  map.put("carrier", element.getChildText("Carrier"));
        			  map.put("adep", element.getChildText("ADEP"));
        			  map.put("ades", element.getChildText("ADES"));
        			  map.put("stod", element.getChildText("STOD"));
        			  map.put("stoa", element.getChildText("STOA"));
                  }
        		  for (Element stateTimeElement : stateTimeList) {
                      //获取name属性值
        			  map.put("type", element.getChildText("TYPE"));
        			  map.put("seat", element.getChildText("SEAT"));
        			  map.put("state", element.getChildText("STATE"));
        			  map.put("stateTime", element.getChildText("TIME"));
                  }
        		  for (Element flightInfoElement : flightInfoList) {
                      //获取name属性值
        			  map.put("rwy", element.getChildText("RWY"));
        			  map.put("pbay", element.getChildText("PBAY"));
        			  map.put("act", element.getChildText("ACT"));
        			  map.put("atot", element.getChildText("ATOT"));
        			  map.put("aldt", element.getChildText("ALDT"));
                  }
        	  }
			  listMap.add(map); 
		 	}
		  	StringBuffer flnoSb = new StringBuffer(); 
		  	for (Map<String,String> map:listMap) {
		  		flnoSb.append("'").append(map.get("flno")).append("',");
		  	}
		  	DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    PreparedStatement pst = null;
		    Statement stmt = null;
		    try {
				conn = dsb.getDataSource().getConnection();
			    conn.setAutoCommit(false);
			    stmt = conn.createStatement();
			    String str = "DELETE FROM CDMINFOR WHERE FLNO IN ("+flnoSb.substring(0, flnoSb.toString().length()-1)+") AND SENDTIME LIKE '%"+dateStr+"%'";
			    System.out.println("-------str------"+str);
			    stmt.executeQuery(str);
			    
				pst = conn.prepareStatement("INSERT INTO CDMINFOR (ID,SENDTIME,QDM,QRC,DDM,DST,MSGTYPE,INFORKEY,RETC,RETS,RID,MID,SQCNUM,FUID,FLNO" +
  	    				",Carrier,ADEP,ADES,STOD,STOA,STATETYPE,SEAT,STATE,STSTETIME,RWY,PBAY,ACT,ATOT,ALDT) values (SEQ_CDMINFOR.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for (Map<String,String> map:listMap) {
				        if(map.get("time")!=null && !"".equals(map.get("time"))){
				        	pst.setString(1, map.get("time").toString());   
				        } else {
				        	pst.setString(1, "");   
				        }
				        if(map.get("qdm")!=null && !"".equals(map.get("qdm"))){
				        	pst.setString(2, map.get("qdm").toString());   
				        } else {
				        	pst.setString(2, "");   
				        }
				        if(map.get("qrc")!=null && !"".equals(map.get("qrc"))){
				        	pst.setString(3, map.get("qrc").toString());   
				        } else {
				        	pst.setString(3, "");   
				        }
				        if(map.get("ddm")!=null && !"".equals(map.get("ddm"))){
				        	pst.setString(4, map.get("ddm").toString());   
				        } else {
				        	pst.setString(4, "");   
				        }
				        if(map.get("dst")!=null && !"".equals(map.get("dst"))){
				        	pst.setString(5, map.get("dst").toString());   
				        } else {
				        	pst.setString(5, "");   
				        }
				        if(map.get("msgType")!=null && !"".equals(map.get("msgType"))){
				        	pst.setString(6, map.get("msgType").toString());   
				        } else {
				        	pst.setString(6, "");   
				        }
				        if(map.get("key")!=null && !"".equals(map.get("key"))){
				        	pst.setString(7, map.get("key").toString());   
				        } else {
				        	pst.setString(7, "");   
				        }
				        if(map.get("retc")!=null && !"".equals(map.get("retc"))){
				        	pst.setString(8, map.get("retc").toString());   
				        } else {
				        	pst.setString(8, "");   
				        }
				        if(map.get("rets")!=null && !"".equals(map.get("rets"))){
				        	pst.setString(9, map.get("rets").toString());   
				        } else {
				        	pst.setString(9, "");   
				        }
				        if(map.get("rid")!=null && !"".equals(map.get("rid"))){
				        	pst.setString(10, map.get("rid").toString());   
				        } else {
				        	pst.setString(10, "");   
				        }
				        if(map.get("mid")!=null && !"".equals(map.get("mid"))){
				        	pst.setString(11, map.get("mid").toString());   
				        } else {
				        	pst.setString(11, "");   
				        }
				        if(map.get("sqcnum")!=null && !"".equals(map.get("sqcnum"))){
				        	pst.setString(12, map.get("sqcnum").toString());   
				        } else {
				        	pst.setString(12, "");   
				        }
				        if(map.get("fuid")!=null && !"".equals(map.get("fuid"))){
				        	pst.setString(13, map.get("fuid").toString());   
				        } else {
				        	pst.setString(13, "");   
				        }
				        if(map.get("flno")!=null && !"".equals(map.get("flno"))){
				        	pst.setString(14, map.get("flno").toString());   
				        } else {
				        	pst.setString(14, "");   
				        }
				        if(map.get("carrier")!=null && !"".equals(map.get("carrier"))){
				        	pst.setString(15, map.get("carrier").toString());   
				        } else {
				        	pst.setString(15, "");   
				        }
				        if(map.get("adep")!=null && !"".equals(map.get("adep"))){
				        	pst.setString(16, map.get("adep").toString());   
				        } else {
				        	pst.setString(16, "");   
				        }
				        if(map.get("ades")!=null && !"".equals(map.get("ades"))){
				        	pst.setString(17, map.get("ades").toString());   
				        } else {
				        	pst.setString(17, "");   
				        }
				        if(map.get("stod")!=null && !"".equals(map.get("stod"))){
				        	pst.setString(18, map.get("stod").toString());   
				        } else {
				        	pst.setString(18, "");   
				        }
				        if(map.get("stoa")!=null && !"".equals(map.get("stoa"))){
				        	pst.setString(19, map.get("stoa").toString());   
				        } else {
				        	pst.setString(19, "");   
				        }
				        if(map.get("type")!=null && !"".equals(map.get("type"))){
				        	pst.setString(20, map.get("type").toString());   
				        } else {
				        	pst.setString(20, "");   
				        }
				        if(map.get("seat")!=null && !"".equals(map.get("seat"))){
				        	pst.setString(21, map.get("seat").toString());   
				        } else {
				        	pst.setString(21, "");   
				        }
				        if(map.get("state")!=null && !"".equals(map.get("state"))){
				        	pst.setString(22, map.get("state").toString());   
				        } else {
				        	pst.setString(22, "");   
				        }
				        if(map.get("stateTime")!=null && !"".equals(map.get("stateTime"))){
				        	pst.setString(23, map.get("stateTime").toString());   
				        } else {
				        	pst.setString(23, "");   
				        }
				        if(map.get("rwy")!=null && !"".equals(map.get("rwy"))){
				        	pst.setString(24, map.get("rwy").toString());   
				        } else {
				        	pst.setString(24, "");   
				        }
				        if(map.get("pbay")!=null && !"".equals(map.get("pbay"))){
				        	pst.setString(25, map.get("pbay").toString());   
				        } else {
				        	pst.setString(25, "");   
				        }
				        if(map.get("act")!=null && !"".equals(map.get("act"))){
				        	pst.setString(26, map.get("act").toString());   
				        } else {
				        	pst.setString(26, "");   
				        }
				        if(map.get("atot")!=null && !"".equals(map.get("atot"))){
				        	pst.setString(27, map.get("atot").toString());   
				        } else {
				        	pst.setString(27, "");   
				        }
				        if(map.get("aldt")!=null && !"".equals(map.get("aldt"))){
				        	pst.setString(28, map.get("aldt").toString());   
				        } else {
				        	pst.setString(28, "");   
				        }
				        // 把一个SQL命令加入命令列表   
				        pst.addBatch();   
				 }  
			    // 执行批量更新   
				 pst.executeBatch();   
			    // 语句执行完毕，提交本事务   
			    conn.commit(); 
			    flag=true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
		      	if(pst != null){
		      	try
		      	{
		      		pst.close();
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
		  return flag;
      }
	  
	  public String getFieldId(String time,String flno){
		    DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    Statement stmt = null;
		    String id="";
		    try {
		      conn = dsb.getDataSource().getConnection();
		      stmt = conn.createStatement();
		      ResultSet rs = null;
		      String sql ="SELECT T.ID FROM CDMINFOR T WHERE T.SENDTIME='"+time.substring(0, 10)+"' AND T.FLNO='"+flno+"'";
		      rs = stmt.executeQuery(sql);
		        while (rs.next())
		        {
		        	id = rs.getString(1);
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
		   return id;
		}
	  
	  public void TextToFile(String content){
		  String fileName = "D:/file.xml";    
		  try {    
		        // 打开一个随机访问文件流，按读写方式    
		        RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");    
		        // 文件长度，字节数    
		        long fileLength = randomFile.length();    
		        //将写文件指针移到文件尾。    
		        randomFile.seek(fileLength);    
		        randomFile.writeBytes(content);    
		        randomFile.close();    
	       } catch (IOException e){    
	        e.printStackTrace();    
	       } 
	  }
}

/*package yhb;
import java.io.IOException;
import java.net.*;
不知道是客户端还是服务器端所以在MAIN方法里只是起了一个处理UDP线程
 * 
public class CSDNudp {
	public static void main(String[] args)
	{
		new Thread(new UDPThread()).start();
	}

}

class UDPThread implements Runnable {

	byte[] buf = new byte[1024];
    private int UDP_PORT=9999;
	public void run() {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(UDP_PORT);
		} catch (BindException e) {
			System.out.println("UDP端口使用中...请重关闭程序启服务器");
		} catch (SocketException e) {
			e.printStackTrace();
		}
用这个来处理循环接收数据包比较合理，服务器接收数据包一般都是转发至客户端，而客户端接收数据包一般处理后再发送到服务器，很复杂。不会
 close()掉，下面只是简单的System.out.println("收到数据包!")
		while (ds != null) {
			DatagramPacket dp = new DatagramPacket(buf, buf.length);

			try {
				ds.receive(dp);
				System.out.println("收到数据包!");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
*/
/*public void run() {
	while (!this.isInterrupted()) {// 线程未中断执行循环
		try {
			Thread.sleep(2000); //每隔2000ms执行一次
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		 ------------------ 开始执行 ---------------------------
		System.out.println("____FUCK TIME:" + System.currentTimeMillis());
		try {
	    	ServerSocket serverSocket = new ServerSocket(3000);
	    	while (true) {
	    	     Socket socket = null;
	    	     socket = serverSocket.accept(); // 等待客户连接
	    	     DatagramSocket ds = new DatagramSocket(socket.getPort());
	    	     byte[] buf = new byte[1024];
	   	      	 DatagramPacket dp = new DatagramPacket(buf, 1024);
	   	      	 ds.receive(dp);
	   	      	 String strInfo = new String(dp.getData(), 0, dp.getLength()) + 
	   	         " from " + dp.getAddress().getHostAddress() + ":" + dp.getPort();
	   	      	 System.out.println(strInfo);
	   	      	 ds.close();
  	    }
	    } catch (SocketException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	}
	//    	  String time = "";String qdm = "";
//    	  String qrc = "";String ddm = "";
//    	  String dst = "";String msgType = "";
//    	  String key = "";String retc = "";
//    	  String rets = "";String rid = "";
//    	  String mid = "";String sqcnum = "";
//    	  String fuid = "";String flno = "";
//    	  String carrier = "";String adep = "";
//    	  String ades = "";String stod = "";
//    	  String stoa = "";String type = "";
//    	  String seat = "";String state = "";
//    	  String stateTime = "";String rwy = "";
//    	  String pbay = "";String act = "";
//    	  String atot = "";String aldt = "";
//          try {
//        	DataSourceBase dsb = new DataSourceBase();
//      	    Connection conn = null;
//      	    ResultSet rs = null;
//      	    PreparedStatement pstmt = null;
//      	    try { 
//      	    	String sql = "";
//      	    	conn = dsb.getDataSource().getConnection();
//      	        conn.setAutoCommit(false);
//      	        String id = getFieldId(time,flno);
//      	      if(null!=id && !"".equals(id)){
//  	    		sql= "UPDATE CDMINFOR T SET T.SENDTIME=?,T.QDM=?,T.QRC=?,T.DDM=?,T.DST=?,T.MSGTYPE=?,T.INFORKEY=?" +
//  	    				",T.RETC=?,T.RETS=?,T.RID=?,T.MID=?,T.SQCNUM=?,T.FUID=?,T.FLNO=?,T.Carrier=?,T.ADEP=?,T.ADES=?" +
//  	    				",T.STOD=?,T.STOA=?,T.STATETYPE=?,T.SEAT=?,T.STATE=?,T.STSTETIME=?,T.RWY=?,T.PBAY=?,T.ACT=?,T.ATOT=?" +
//  	    				",T.ALDT=? WHERE T.ID='"+id+"'";
//      	      } else {
//  	    		sql = "INSERT INTO CDMINFOR (ID,SENDTIME,QDM,QRC,DDM,DST,MSGTYPE,INFORKEY,RETC,RETS,RID,MID,SQCNUM,FUID,FLNO" +
//  	    				",Carrier,ADEP,ADES,STOD,STOA,STATETYPE,SEAT,STATE,STSTETIME,RWY,PBAY,ACT,ATOT,ALDT) values (SEQ_CDMINFOR.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//      	      }
//      	      conn = dsb.getDataSource().getConnection();
//      	      conn.setAutoCommit(false);
//      	      pstmt = conn.prepareStatement(sql);
//      	      pstmt.setString(1, time);  
//      	      pstmt.setString(2, qdm);   
//      	      pstmt.setString(3, qrc); 
//      	      pstmt.setString(4, ddm);
//      	      pstmt.setString(5, dst);
//      	      pstmt.setString(6, msgType);
//      	      pstmt.setString(7, key);
//      	      pstmt.setString(8, retc);
//      	      pstmt.setString(9, rets);
//      	      pstmt.setString(10, rid);
//      	      pstmt.setString(11, mid);
//      	      pstmt.setString(12, sqcnum);
//      	      pstmt.setString(13, fuid);
//      	      pstmt.setString(14, flno);
//      	      pstmt.setString(15, carrier);
//      	      pstmt.setString(16, adep);
//      	      pstmt.setString(17, ades);
//      	      pstmt.setString(18, stod);
//      	      pstmt.setString(19, stoa);
//      	      pstmt.setString(20, type);
//      	      pstmt.setString(21, seat);
//      	      pstmt.setString(22, state);
//      	      pstmt.setString(23, stateTime);
//      	      pstmt.setString(24, rwy);
//      	      pstmt.setString(25, pbay);
//      	      pstmt.setString(26, act);
//      	      pstmt.setString(27, atot);
//      	      pstmt.setString(28, aldt);
//      	      // 执行批量更新   
//      	      pstmt.executeQuery();
//      	      // 语句执行完毕，提交本事务   
//      	      conn.commit(); 
//      	    } catch (Exception e) {
//      	    	conn.rollback();
//      	        System.out.println("error message:" + e.getMessage());
//      	    }finally{
//      	    	if(rs != null){
//      	    	try{
//      	    	rs.close();
//      	    	}catch(Exception e){
//      	    	//log
//      	    	}
//      	    	}
//      	    	if(pstmt != null){
//      	    	try
//      	    	{
//      	    	pstmt.close();
//      	    	}catch(Exception e)
//      	    	{
//      	    	}
//      	    	}
//      	    	if(conn != null){
//      	    	try
//      	    	{
//      	    	conn.close();
//      	    	}catch(Exception e){
//      	    		
//      	    	}
//      	    	}
//      	    }
//          } catch (Exception ee)  {
//        	  ee.getStackTrace();
//          }
}*/
}*/