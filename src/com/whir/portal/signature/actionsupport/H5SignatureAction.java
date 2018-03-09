package com.whir.portal.signature.actionsupport;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;

public class H5SignatureAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(H5SignatureAction.class.getName());
	private byte[] SignatureBody;
	private int mSignatureSize;
	/**
	 * 保存签章数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 */
	public void saveSignature() throws SQLException {
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    Statement stmt1=null;
	    conn = dsb.getDataSource().getConnection();
	    stmt = conn.createStatement();
	    
		String documentId = request.getParameter("documentId");
		String signatureId = request.getParameter("signatureId");
		String signatureData = request.getParameter("signatureData");
		SignatureBody = signatureData.getBytes();
		mSignatureSize = SignatureBody.length;
		String strSql="SELECT * from HTMLSignature Where SignatureID='"+signatureId+"' and DocumentID='"+documentId+"'";
		try{
		   ResultSet result = stmt.executeQuery(strSql);
		   if (result.next()) {
			 strSql = "update HTMLSignature set DocumentID=?,SIGNATUREID=?,SignatureSize=?,Signature=EMPTY_BLOB() Where SignatureID='"+signatureId+"' and DocumentID='"+documentId+"'";
		   }
		   else {
			 strSql="insert into HTMLSignature (DocumentID,SignatureID,SignatureSize,Signature) values (?,?,?,EMPTY_BLOB()) ";
		   }
		   result.close();
		   java.sql.PreparedStatement prestmt=null;
		   System.out.println("strSql:::"+strSql);
		   try {
			   prestmt =conn.prepareStatement(strSql);
			   prestmt.setString(1, documentId);
			   prestmt.setString(2, signatureId);
			   prestmt.setInt(3,mSignatureSize);
			   conn.setAutoCommit(false) ;
			   prestmt.execute();
			   conn.commit();
			   prestmt.close();
			   conn.setAutoCommit(false) ;
			   stmt1 = conn.createStatement();
			   ResultSet update=stmt1.executeQuery("select Signature from HTMLSignature Where SignatureID='"+signatureId+"' and DocumentID='"+documentId+"'" + " for update");
			   if (update.next()){
				 try
				 {
				   PutAtBlob_new(update.getBlob("Signature"),mSignatureSize);
				 }
				 catch (IOException e) {
					System.out.println(e.toString());
				 }
			   }
			   update.close();
			   stmt1.close();
			   conn.commit();
			   SignatureBody=null;
		   }
		 catch (SQLException e) {
		   System.out.println(e.toString());
		 }
			 }
			 finally {
				 try
			      {
			        if (stmt != null) {
			          stmt.close();
			        }
			        if (stmt1 != null) {
				          stmt1.close();
				        }
			        if (conn != null)
			          conn.close();
			      }
			      catch (SQLException localSQLException3) {
			      }
			 }
	}
	
	
	private void PutAtBlob_new(Object vField, int vSize) throws IOException {
        Class clazz = vField.getClass();
        try {
            Method method = clazz.getMethod("getBinaryOutputStream", new Class[] {});
            OutputStream os = (OutputStream) method.invoke(vField, new Object[] {});
            BufferedOutputStream outstream = new BufferedOutputStream(os);
            // outstream.write(define.getBytes("UTF-8"));
            outstream.write(SignatureBody, 0, vSize);
            outstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	
	/**
	 * 判断设定的通知公告是否重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws IOException 
	 */
	public String getSignatures() throws IOException {
		String documentId = request.getParameter("documentId");
		
		List<Map<String, String>> result = getSignatures(documentId);
		System.out.println("签章个数::"+result.size());
		String jsonStr = JSONArray.fromObject(result).toString();
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(jsonStr);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	public List<Map<String,String>> getSignatures(String documentId) throws IOException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      String strSql="SELECT SignatureID,Signature,SignatureSize from HTMLSignature Where DocumentID='"+documentId+"'";
	      rs = stmt.executeQuery(strSql);
	        while (rs.next())
	        {
	        	Map<String, String> map = new HashMap<String, String>();
	        	String signatureid = rs.getString("SignatureID");
	        	mSignatureSize = rs.getInt("SignatureSize");
	        	GetAtBlob_new(rs.getBlob("Signature"),mSignatureSize);
	        	String mSignature=new String(SignatureBody);
	        	map.put("signatureid", signatureid);
	        	map.put("signatureData", mSignature);
	        	list.add(map);
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
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
	   return list;
	}
	
	private void GetAtBlob_new(Object vField, int vSize) throws IOException {
        Class clazz = vField.getClass();
        try {
            Method method = clazz.getMethod("getBinaryStream", new Class[] {});
            SignatureBody = new byte[vSize];
            InputStream instream = (InputStream) method.invoke(vField,
                    new Object[] {});
            instream.read(SignatureBody, 0, vSize);
            instream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
