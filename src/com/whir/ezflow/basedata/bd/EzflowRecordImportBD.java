package com.whir.ezflow.basedata.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bsh.ParseException;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;
import com.whir.common.util.JsonParse;
import com.whir.common.util.SecurityFlightJsonBean;
import com.whir.common.util.TowerSecurityFlightJsonBean;
import com.whir.common.util.WeatherJsonBean;

public class EzflowRecordImportBD extends HibernateBase {
	
	/*
	 * 保存气象工作质量统计流程
	 */
	public boolean saveRecordImp(List list, String userId, String orgId,
			String domainId, String userName) throws SQLException {
		boolean flag = false;
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pst = null;
		Statement stmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String str = "DELETE FROM whir$qxgztjlsb WHERE whir$qxgztjlsb_f4067='"
					+ userId + "'";
			stmt.executeQuery(str);

			pst = conn
					.prepareStatement("INSERT INTO whir$qxgztjlsb (whir$qxgztjlsb_id,whir$qxgztjlsb_f4029,whir$qxgztjlsb_f4030,"
							+ "whir$qxgztjlsb_f4031,whir$qxgztjlsb_f4032,"
							+ "whir$qxgztjlsb_f4033,whir$qxgztjlsb_f4034,whir$qxgztjlsb_f4035,"
							+ "whir$qxgztjlsb_f4036,whir$qxgztjlsb_f4037,whir$qxgztjlsb_f4067) "
							+ "values (seq_whir$qxgztjlsb.nextval,?,?,?,?,?,?,?,?,?,?)");
			for (int i = 0; i < list.size(); i++) {
				String[] valArr = (String[]) list.get(i);
				if (!"".equals(valArr[0].trim()) && valArr[0].trim() != null) {
					pst.setString(1, valArr[0].trim());
				}
				if (!"".equals(valArr[1].trim()) && valArr[1].trim() != null) {
					pst.setString(2, valArr[1].trim());
				} else {
					pst.setString(2, "");
				}
				if (!"".equals(valArr[2].trim()) && valArr[2].trim() != null) {
					pst.setString(3, valArr[2].trim());
				} else {
					pst.setString(3, "");
				}
				if (!"".equals(valArr[3].trim()) && valArr[3].trim() != null) {
					pst.setString(4, valArr[3].trim());
				} else {
					pst.setString(4, "");
				}
				if (!"".equals(valArr[4].trim()) && valArr[4].trim() != null) {
					pst.setString(5, valArr[4].trim());
				} else {
					pst.setString(5, "");
				}
				if (!"".equals(valArr[5].trim()) && valArr[5].trim() != null) {
					pst.setString(6, valArr[5].trim());
				} else {
					pst.setString(6, "");
				}
				if (!"".equals(valArr[6].trim()) && valArr[6].trim() != null) {
					pst.setString(7, valArr[6].trim());
				} else {
					pst.setString(7, "");
				}
				if (!"".equals(valArr[7].trim()) && valArr[7].trim() != null) {
					pst.setString(8, valArr[7].trim());
				} else {
					pst.setString(8, "");
				}
				if (!"".equals(valArr[8].trim()) && valArr[8].trim() != null) {
					pst.setString(9, valArr[8].trim());
				} else {
					pst.setString(9, "");
				}
				pst.setString(10, userId);
				pst.addBatch();
			}
			// 执行批量更新
			pst.executeBatch();
			// 语句执行完毕，提交本事务
			conn.commit();
			flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
		return flag;
	}
	/*
	 * 保存进近(区域)保障架次统计流程
	 */
	public boolean saveSecurityFlightImp(List list, String userId,
			String orgId, String domainId, String userName) throws SQLException {
		boolean flag = false;
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pst = null;
		Statement stmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String str = "DELETE FROM whir$jjbzjctlsb WHERE whir$jjbzjctlsb_f4065='"
					+ userId + "'";
			stmt.executeQuery(str);

			pst = conn
					.prepareStatement("INSERT INTO whir$jjbzjctlsb (whir$jjbzjctlsb_id,whir$jjbzjctlsb_f4055,whir$jjbzjctlsb_f4056,"
							+ "whir$jjbzjctlsb_f4057,whir$jjbzjctlsb_f4058,"
							+ "whir$jjbzjctlsb_f4059,whir$jjbzjctlsb_f4060,whir$jjbzjctlsb_f4061,"
							+ "whir$jjbzjctlsb_f4062,whir$jjbzjctlsb_f4063,whir$jjbzjctlsb_f4064,whir$jjbzjctlsb_f4065) "
							+ "values (hibernate_sequence.nextval,?,?,?,?,?,?,?,?,?,?,?)");
			for (int i = 0; i < list.size(); i++) {
				int total1 = 0;
				int total2 = 0;
				int total12 = 0;
				String[] valArr = (String[]) list.get(i);
				if (!"".equals(valArr[0].trim()) && valArr[0].trim() != null) {
					pst.setString(1, valArr[0].trim());
				}
				if (!"".equals(valArr[1].trim()) && valArr[1].trim() != null) {
					total1 += Integer.parseInt(valArr[1].trim());
					pst.setString(2, valArr[1].trim());
				} else {
					pst.setString(2, "");
				}
				if (!"".equals(valArr[2].trim()) && valArr[2].trim() != null) {
					total1 += Integer.parseInt(valArr[2].trim());
					pst.setString(3, valArr[2].trim());
				} else {
					pst.setString(3, "");
				}
				if (!"".equals(valArr[3].trim()) && valArr[3].trim() != null) {
					pst.setString(4, valArr[3].trim());
				} else {
					pst.setString(4, "");
				}
				pst.setString(5, total1+"");
//				if (!"".equals(valArr[4].trim()) && valArr[4].trim() != null) {
//					pst.setString(5, valArr[4].trim());
//				} else {
//					pst.setString(5, "");
//				}
				if (!"".equals(valArr[5].trim()) && valArr[5].trim() != null) {
					total2 += Integer.parseInt(valArr[5].trim());
					pst.setString(6, valArr[5].trim());
				} else {
					pst.setString(6, "");
				}
				if (!"".equals(valArr[6].trim()) && valArr[6].trim() != null) {
					total2 += Integer.parseInt(valArr[6].trim());
					pst.setString(7, valArr[6].trim());
				} else {
					pst.setString(7, "");
				}
				if (!"".equals(valArr[7].trim()) && valArr[7].trim() != null) {
					pst.setString(8, valArr[7].trim());
				} else {
					pst.setString(8, "");
				}
				pst.setString(9, total2+"");
//				if (!"".equals(valArr[8].trim()) && valArr[8].trim() != null) {
//					pst.setString(9, valArr[8].trim());
//				} else {
//					pst.setString(9, "");
//				}
				total12 = total1+total2;
				pst.setString(10, total12+"");
//				if (!"".equals(valArr[9].trim()) && valArr[9].trim() != null) {
//					pst.setString(10, valArr[9].trim());
//				} else {
//					pst.setString(10, "");
//				}
				pst.setString(11, userId);
				pst.addBatch();
			}
			// 执行批量更新
			pst.executeBatch();
			// 语句执行完毕，提交本事务
			conn.commit();
			flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
		return flag;
	}

	/*
	 * 保存进近(区域)保障架次统计流程
	 */
	public boolean saveTowerSecurityFlightImp(List list, String userId,
			String orgId, String domainId, String userName) throws SQLException, java.text.ParseException, NumberFormatException, ParseException {
		boolean flag = false;
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pst = null;
		Statement stmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String str = "DELETE FROM whir$ttbzjctjb WHERE whir$ttbzjctjb_userid='"
					+ userId + "'";
			stmt.executeQuery(str);

			pst = conn.prepareStatement("INSERT INTO whir$ttbzjctjb (whir$ttbzjctjb_id,whir$ttbzjctjb_f3616,whir$ttbzjctjb_f3617,"
							+ "whir$ttbzjctjb_f3618,whir$ttbzjctjb_f3619,whir$ttbzjctjb_f3620,whir$ttbzjctjb_f3621,	whir$ttbzjctjb_f3622,"
							+ "whir$ttbzjctjb_f3623,whir$ttbzjctjb_f3624,whir$ttbzjctjb_f3625,whir$ttbzjctjb_f3626,	whir$ttbzjctjb_f3627,"
							+ "whir$ttbzjctjb_f3628,whir$ttbzjctjb_f3629,whir$ttbzjctjb_f3630,whir$ttbzjctjb_f3631,	whir$ttbzjctjb_f3632,"
							+ "whir$ttbzjctjb_f3633,whir$ttbzjctjb_f3634,whir$ttbzjctjb_f3635,whir$ttbzjctjb_f3636,whir$ttbzjctjb_f3637,"
							+ "whir$ttbzjctjb_f3638,whir$ttbzjctjb_f3639,whir$ttbzjctjb_f3640,whir$ttbzjctjb_userid) "
							+ "values (hibernate_sequence.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			for (int i = 0; i < list.size(); i++) {
				int total1 = 0;
				int total2 = 0;
				int total12 = 0;
				String[] valArr = (String[]) list.get(i);
				if (!"".equals(valArr[0].trim()) && valArr[0].trim() != null) {
					pst.setString(1, valArr[0].trim());
				}
				if (!"".equals(valArr[1].trim()) && valArr[1].trim() != null) {
					total1 += Integer.parseInt(valArr[1].trim()); 
					pst.setString(2, valArr[1].trim());
				} else {
					pst.setString(2, "");
				}
				if (!"".equals(valArr[2].trim()) && valArr[2].trim() != null) {
					total1 += Integer.parseInt(valArr[2].trim()); 
					pst.setString(3, valArr[2].trim());
				} else {
					pst.setString(3, "");
				}
				if (!"".equals(valArr[3].trim()) && valArr[3].trim() != null) {
					total1 += Integer.parseInt(valArr[3].trim()); 
					pst.setString(4, valArr[3].trim());
				} else {
					pst.setString(4, "");
				}
				if (!"".equals(valArr[4].trim()) && valArr[4].trim() != null) {
					total1 += Integer.parseInt(valArr[4].trim()); 
					pst.setString(5, valArr[4].trim());
				} else {
					pst.setString(5, "");
				}
				if (!"".equals(valArr[5].trim()) && valArr[5].trim() != null) {
					total1 += Integer.parseInt(valArr[5].trim()); 
					pst.setString(6, valArr[5].trim());
				} else {
					pst.setString(6, "");
				}
				if (!"".equals(valArr[6].trim()) && valArr[6].trim() != null) {
					total1 += Integer.parseInt(valArr[6].trim()); 
					pst.setString(7, valArr[6].trim());
				} else {
					pst.setString(7, "");
				}
				if (!"".equals(valArr[7].trim()) && valArr[7].trim() != null) {
					total1 += Integer.parseInt(valArr[7].trim()); 
					pst.setString(8, valArr[7].trim());
				} else {
					pst.setString(8, "");
				}
				if (!"".equals(valArr[8].trim()) && valArr[8].trim() != null) {
					total1 += Integer.parseInt(valArr[8].trim()); 
					pst.setString(9, valArr[8].trim());
				} else {
					pst.setString(9, "");
				}
				if (!"".equals(valArr[9].trim()) && valArr[9].trim() != null) {
					total1 += Integer.parseInt(valArr[9].trim()); 
					pst.setString(10, valArr[9].trim());
				} else {
					pst.setString(10, "");
				}
				if (!"".equals(valArr[10].trim()) && valArr[10].trim() != null) {
					total1 += Integer.parseInt(valArr[10].trim()); 
					pst.setString(11, valArr[10].trim());
				} else {
					pst.setString(11, "");
				}
				if (!"".equals(valArr[11].trim()) && valArr[11].trim() != null) {
					total1 += Integer.parseInt(valArr[11].trim()); 
					pst.setString(12, valArr[11].trim());
				} else {
					pst.setString(12, "");
				}
				if (!"".equals(valArr[12].trim()) && valArr[12].trim() != null) {
					total1 += Integer.parseInt(valArr[12].trim()); 
					pst.setString(13, valArr[12].trim());
				} else {
					pst.setString(13, "");
				}
				pst.setString(14, total1+"");
//				if (!"".equals(valArr[13].trim()) && valArr[13].trim() != null) {
//					pst.setString(14, valArr[13].trim());
//				} else {
//					pst.setString(14, "");
//				}
				if (!"".equals(valArr[14].trim()) && valArr[14].trim() != null) {
					pst.setString(15, valArr[14].trim());
				} else {
					pst.setString(15, "");
				}
				if (!"".equals(valArr[15].trim()) && valArr[15].trim() != null) {
					total2 += Integer.parseInt(valArr[15].trim()); 
					pst.setString(16, valArr[15].trim());
				} else {
					pst.setString(16, "");
				}
				if (!"".equals(valArr[16].trim()) && valArr[16].trim() != null) {
					total2 += Integer.parseInt(valArr[16].trim()); 
					pst.setString(17, valArr[16].trim());
				} else {
					pst.setString(17, "");
				}
				if (!"".equals(valArr[17].trim()) && valArr[17].trim() != null) {
					total2 += Integer.parseInt(valArr[17].trim()); 
					pst.setString(18, valArr[17].trim());
				} else {
					pst.setString(18, "");
				}
				pst.setString(19, total2+"");
//				if (!"".equals(valArr[18].trim()) && valArr[18].trim() != null) {
//					pst.setString(19, valArr[18].trim());
//				} else {
//					pst.setString(19, "");
//				}
//				if (!"".equals(valArr[19].trim()) && valArr[19].trim() != null) {
//					pst.setString(20, valArr[19].trim());
//				} else {
//					pst.setString(20, "");
//				}
				total12 = total1+total2;
				pst.setString(20, total12+"");
				if (!"".equals(valArr[20].trim()) && valArr[20].trim() != null) {
					pst.setString(21, valArr[20].trim());
				} else {
					pst.setString(21, "");
				}
				if (!"".equals(valArr[21].trim()) && valArr[21].trim() != null) {
//					 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 日期格式
//					 SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM月dd日"); // 日期格式
//					 Date date = dateFormat.parse("1900-01-01"); // 指定日期
//					 Date newDate = addDate(date, Integer.parseInt(valArr[21].trim())-2); // 指定日期加上20天
//					 pst.setString(22, dateFormat1.format(newDate));
					 pst.setString(22, valArr[21].trim());
				} else {
					pst.setString(22, "");
				}
				if (!"".equals(valArr[22].trim()) && valArr[22].trim() != null) {
					pst.setString(23, valArr[22].trim());
				} else {
					pst.setString(23, "");
				}
				if (!"".equals(valArr[23].trim()) && valArr[23].trim() != null) {
					pst.setString(24, valArr[23].trim());
				} else {
					pst.setString(24, "");
				}
				if (!"".equals(valArr[24].trim()) && valArr[24].trim() != null) {
					pst.setString(25, valArr[24].trim());
				} else {
					pst.setString(25, "");
				}
				pst.setString(26, userId);
				pst.addBatch();
			}
			// 执行批量更新
			pst.executeBatch();
			// 语句执行完毕，提交本事务
			conn.commit();
			flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
		return flag;
	}
	
	 public static Date addDate(Date date,long day) throws ParseException {
		 long time = date.getTime(); // 得到指定日期的毫秒数
		 day = day*24*60*60*1000; // 要加上的天数转换成毫秒数
		 time+=day; // 相加得到新的毫秒数
		 return new Date(time); // 将毫秒数转换成日期
	}
	 
	public String getWeatherWork(String userId, String domainId) {
		String sql = " select whir$qxgztjlsb_f4029,whir$qxgztjlsb_f4030,whir$qxgztjlsb_f4031,whir$qxgztjlsb_f4032,"
				+ "whir$qxgztjlsb_f4033,whir$qxgztjlsb_f4034,whir$qxgztjlsb_f4035,whir$qxgztjlsb_f4036,whir$qxgztjlsb_f4037 "
				+ "from whir$qxgztjlsb where whir$qxgztjlsb_f4067='"
				+ userId
				+ "'";
		List leaderList = null;
		try {
			leaderList = getListBySQL(sql);
		} catch (Exception localException) {
		}
		List jsonBeanList = toJsonBeanList(leaderList);
		JsonParse parse = new JsonParse();
		String jsonString = parse.writeListJSON(jsonBeanList);
		return jsonString;
	}

	public String getSecurityFlight(String userId, String domainId) {
		String sql = " select whir$jjbzjctlsb_f4055,whir$jjbzjctlsb_f4056,whir$jjbzjctlsb_f4057,whir$jjbzjctlsb_f4058,"
				+ "whir$jjbzjctlsb_f4059,whir$jjbzjctlsb_f4060,whir$jjbzjctlsb_f4061,whir$jjbzjctlsb_f4062,whir$jjbzjctlsb_f4063,whir$jjbzjctlsb_f4064 "
				+ "from whir$jjbzjctlsb where whir$jjbzjctlsb_f4065='"
				+ userId
				+ "'";
		List leaderList = null;
		try {
			leaderList = getListBySQL(sql);
		} catch (Exception localException) {
		}
		List jsonBeanList = toJsonBeanList1(leaderList);
		JsonParse parse = new JsonParse();
		String jsonString = parse.writeListJSON(jsonBeanList);
		return jsonString;
	}
	
	public String getTowerSecurityFlight(String userId, String domainId) {
		String sql = " select whir$ttbzjctjb_f3616,whir$ttbzjctjb_f3617,"
			+ "whir$ttbzjctjb_f3618,whir$ttbzjctjb_f3619,whir$ttbzjctjb_f3620,whir$ttbzjctjb_f3621,	whir$ttbzjctjb_f3622,"
			+ "whir$ttbzjctjb_f3623,whir$ttbzjctjb_f3624,whir$ttbzjctjb_f3625,whir$ttbzjctjb_f3626,	whir$ttbzjctjb_f3627,"
			+ "whir$ttbzjctjb_f3628,whir$ttbzjctjb_f3629,whir$ttbzjctjb_f3630,whir$ttbzjctjb_f3631,	whir$ttbzjctjb_f3632,"
			+ "whir$ttbzjctjb_f3633,whir$ttbzjctjb_f3634,whir$ttbzjctjb_f3635,whir$ttbzjctjb_f3636,whir$ttbzjctjb_f3637,"
			+ "whir$ttbzjctjb_f3638,whir$ttbzjctjb_f3639,whir$ttbzjctjb_f3640 "
			+ "from whir$ttbzjctjb where whir$ttbzjctjb_userid='"+ userId+ "'";
		List leaderList = null;
		try {
			leaderList = getListBySQL(sql);
		} catch (Exception localException) {
		}
		List jsonBeanList = toJsonBeanList2(leaderList);
		JsonParse parse = new JsonParse();
		String jsonString = parse.writeListJSON(jsonBeanList);
		return jsonString;
	}
	
	public List toJsonBeanList2(List list) {
		List jsonBeanList = new ArrayList();
		if ((list != null) && (list.size() > 0)) {
			for (int i = 0; i < list.size(); i++) {
				Object[] arr = (Object[]) list.get(i);
				TowerSecurityFlightJsonBean towerSecurityFlightJsonBean = new TowerSecurityFlightJsonBean();
				towerSecurityFlightJsonBean.setGzdw(arr[0] != null ? arr[0]
						.toString() : "");
				towerSecurityFlightJsonBean.setBanj(arr[1] != null ? arr[1]
						.toString() : "");
				towerSecurityFlightJsonBean.setZhj(arr[2] != null ? arr[2]
						.toString() : "");
				towerSecurityFlightJsonBean.setJb(arr[3] != null ? arr[3]
						.toString() : "");
				towerSecurityFlightJsonBean.setBj(arr[4] != null ? arr[4]
						.toString() : "");
				towerSecurityFlightJsonBean.setGsxl(arr[5] != null ? arr[5]
						.toString() : "");
				towerSecurityFlightJsonBean.setHxxl(arr[6] != null ? arr[6]
						.toString() : "");
				towerSecurityFlightJsonBean.setDj(arr[7] != null ? arr[7]
						.toString() : "");
				towerSecurityFlightJsonBean.setGw(arr[8] != null ? arr[8]
						.toString() : "");
				towerSecurityFlightJsonBean.setJzjj(arr[9] != null ? arr[9].toString()
						: "");
				towerSecurityFlightJsonBean.setJh(arr[10] != null ? arr[10].toString()
						: "");
				towerSecurityFlightJsonBean.setWh(arr[11] != null ? arr[11].toString()
						: "");
				towerSecurityFlightJsonBean.setQt(arr[12] != null ? arr[12].toString()
						: "");
				towerSecurityFlightJsonBean.setHj(arr[13] != null ? arr[13].toString()
						: "");
				towerSecurityFlightJsonBean.setBsntq(arr[14] != null ? arr[14].toString()
						: "");
				towerSecurityFlightJsonBean.setMh(arr[15] != null ? arr[15].toString()
						: "");
				towerSecurityFlightJsonBean.setJh2(arr[16] != null ? arr[16].toString()
						: "");
				towerSecurityFlightJsonBean.setWh2(arr[17] != null ? arr[17].toString()
						: "");
				towerSecurityFlightJsonBean.setHj2(arr[18] != null ? arr[18].toString()
						: "");
				towerSecurityFlightJsonBean.setZj(arr[19] != null ? arr[19].toString()
						: "");
				towerSecurityFlightJsonBean.setRgfqjjc(arr[20] != null ? arr[20].toString()
						: "");
				towerSecurityFlightJsonBean.setGfrq(arr[21] != null ? arr[21].toString()
						: "");
				towerSecurityFlightJsonBean.setXsgfqjjc(arr[22] != null ? arr[22].toString()
						: "");
				towerSecurityFlightJsonBean.setGfsd(arr[23] != null ? arr[23].toString()
						: "");
				towerSecurityFlightJsonBean.setBz(arr[24] != null ? arr[24].toString()
						: "");
				jsonBeanList.add(towerSecurityFlightJsonBean);
			}
		}

		return jsonBeanList;
	}
	
	public List toJsonBeanList1(List list) {
		List jsonBeanList = new ArrayList();
		if ((list != null) && (list.size() > 0)) {
			for (int i = 0; i < list.size(); i++) {
				Object[] arr = (Object[]) list.get(i);
				SecurityFlightJsonBean securityFlightJsonBean = new SecurityFlightJsonBean();
				securityFlightJsonBean.setGzdw(arr[0] != null ? arr[0]
						.toString() : "");
				securityFlightJsonBean.setJjmh(arr[1] != null ? arr[1]
						.toString() : "");
				securityFlightJsonBean.setJjjh(arr[2] != null ? arr[2]
						.toString() : "");
				securityFlightJsonBean.setJjrgf(arr[3] != null ? arr[3]
						.toString() : "");
				securityFlightJsonBean.setJjxj(arr[4] != null ? arr[4]
						.toString() : "");
				securityFlightJsonBean.setQymh(arr[5] != null ? arr[5]
						.toString() : "");
				securityFlightJsonBean.setQyjh(arr[6] != null ? arr[6]
						.toString() : "");
				securityFlightJsonBean.setQyrgf(arr[7] != null ? arr[7]
						.toString() : "");
				securityFlightJsonBean.setQyxj(arr[8] != null ? arr[8]
						.toString() : "");
				securityFlightJsonBean.setZj(arr[9] != null ? arr[9].toString()
						: "");
				jsonBeanList.add(securityFlightJsonBean);
			}
		}

		return jsonBeanList;
	}

	public List getListBySQL(String sql) throws Exception {
		List result = new ArrayList();
		begin();
		Connection conn = this.session.connection();
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				Object[] obj = new Object[columnCount];
				for (int i = 1; i <= columnCount; i++) {
					if (rs.getString(rsmd.getColumnName(i)) != null)
						obj[(i - 1)] = rs.getString(rsmd.getColumnName(i));
					else {
						obj[(i - 1)] = "";
					}
				}
				result.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
			conn = null;
			this.session.close();
			this.session = null;
			this.transaction = null;
		}
		return result;
	}

	public List toJsonBeanList(List list) {
		List jsonBeanList = new ArrayList();
		if ((list != null) && (list.size() > 0)) {
			for (int i = 0; i < list.size(); i++) {
				Object[] arr = (Object[]) list.get(i);
				WeatherJsonBean weatherJsonBean = new WeatherJsonBean();
				weatherJsonBean.setQxtmc(arr[0] != null ? arr[0].toString()
						: "");
				weatherJsonBean.setQysfc(arr[1] != null ? arr[1].toString()
						: "");
				weatherJsonBean.setQysft(arr[2] != null ? arr[2].toString()
						: "");
				weatherJsonBean.setZytqmzl(arr[3] != null ? arr[3].toString()
						: "");
				weatherJsonBean.setZytqxjl(arr[4] != null ? arr[4].toString()
						: "");
				weatherJsonBean.setZytqlbl(arr[5] != null ? arr[5].toString()
						: "");
				weatherJsonBean.setZytqljcgzs(arr[6] != null ? arr[6]
						.toString() : "");
				weatherJsonBean.setZytqzql(arr[7] != null ? arr[7].toString()
						: "");
				weatherJsonBean.setGccql(arr[8] != null ? arr[8].toString()
						: "");
				jsonBeanList.add(weatherJsonBean);
			}
		}

		return jsonBeanList;
	}
	
	
	public void delectWeatherWork(String userId, String domainId) {
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pst = null;
		Statement stmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String str = "DELETE FROM whir$qxgztjlsb WHERE whir$qxgztjlsb_f4067='"
					+ userId + "'";
			stmt.executeQuery(str);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	
	public void delectSecurityFlight(String userId, String domainId) {
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pst = null;
		Statement stmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String str = "DELETE FROM whir$jjbzjctlsb WHERE whir$jjbzjctlsb_f4065='"
					+ userId + "'";
			stmt.executeQuery(str);

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
	}
	
	
	public void delectTowerSecurityFlight(String userId, String domainId) {
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pst = null;
		Statement stmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String str = "DELETE FROM whir$ttbzjctjb WHERE whir$ttbzjctjb_userid='"
					+ userId + "'";
			stmt.executeQuery(str);

			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
	}
}
