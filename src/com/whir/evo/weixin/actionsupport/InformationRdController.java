package com.whir.evo.weixin.actionsupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.whir.ezoffice.customdb.common.util.DbOpt;


@Controller
@RequestMapping("/infomationRd")
public class InformationRdController {
	@RequestMapping(value = "/menu")
	public String menu(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String menu = "/rd/informationMenu";
		String userId = session.getAttribute("userId") == null ? "0" : session.getAttribute("userId").toString();
		String sql = "select whir$set_notice_module from whir$set_notice " +
				"where whir$set_notice_userid =?  order by whir$set_notice_sort asc";
		Map<String,String[]> map = new HashMap<String, String[]>();//初始化记录下信息栏目的设置
		List<String> list = getListBySql(sql,new String[]{userId});
		map.put("1", new String[]{"OA通知",
				"<div class=\"tips green\"><i class=\"fa fa-pencil-square-o\"></i></div>",
				"/defaultroot/notification/oaNotificationPage.controller"
				}); // 分别代表的是栏目的名称 样式 及url
		map.put("2", new String[]{"门户通知",
				"<div class=\"tips orange\"><i class=\"fa fa-share-alt\"></i></div>",
				"/defaultroot/information/channelList.controller?channelId=6695&channelName=通知通告"
		});
		map.put("3", new String[]{"安管通知",
				"<div class=\"tips red\"><i class=\"fa fa-th-large\"></i></div>",
				"/defaultroot/notification/agNotificationPage.controller"
		});
		map.put("4", new String[]{"运维通知",
				"<div class=\"tips skyblue\"><i class=\"fa fa-pie-chart\"></i></div>",
				"/defaultroot/notification/ywNotificationPage.controller"
		});
		map.put("5", new String[]{"协会公告",
				"<div class=\"tips blue\"><i class=\"fa fa-under-log\"></i></div>",
				"/defaultroot/information/channelList.controller?channelId=8821&channelName=协会公告"
		});
		map.put("6", new String[]{"培训动态",
				"<div class=\"tips pureblue\"><i class=\"fa fa-meeting\"></i></div>",
				"/defaultroot/notification/pxNotificationPage.controller"
		});
		
//		map.put("5", new String[]{"合同通知",
//				"<div class=\"tips yellow\"><i class=\"fa fa-push\"></i></div>",
//				"/defaultroot/notification/htNotificationPage.controller"
//		});
		String moduleLiStr = ""; //ul 标签下的li 标签拼成的字符串		
		if(false&&list!=null&&list.size()>0){//查看了一下正式环境和测试环境的区别 栏目对应不上 配置的功能暂时取消
			for(int i=0;i<list.size();i++){
				String[] str = map.get(list.get(i));
				if(str!=null){
					moduleLiStr += "<li class=\"noticeli\">"+
	                  "<a href=\""+str[2]+"\" class=\"swipeout-content item-content external\">"+
	                    "<div>"+
	                    str[1]+
	                      "<span>"+str[0]+"</span>"+
	                    "</div>"+
	                    "<div class=\"item-after\"><i class=\"fa fa-angle-right\"></i></div>"+
	                  "</a>"+
	                "</li>";
                }
			}
		}else{//若没有记录显示默认的
			for(String key:map.keySet()){
				String[] str = map.get(key);
				if(str!=null){
					moduleLiStr += "<li class=\"noticeli\">"+
	                  "<a href=\""+str[2]+"\" class=\"swipeout-content item-content external\">"+
	                    "<div>"+
	                    str[1]+
	                      "<span>"+str[0]+"</span>"+
	                    "</div>"+
	                    "<div class=\"item-after\"><i class=\"fa fa-angle-right\"></i></div>"+
	                  "</a>"+
	                "</li>";
                }
			}
		}
		model.addAttribute("modultType",moduleLiStr);
		return menu;
	}
	
	
	/**
	 * 根据sql 获取list的结果
	 * @param sql
	 * @param sqlStr
	 * @return
	 */
	private List<String> getListBySql(String sql,String[] sqlStr){
		DbOpt dbopt = new DbOpt();
		List<String> list = new ArrayList<String>();
		ResultSet rs = null;
		try {
			rs = dbopt.executePreparedQuery(sql, sqlStr);
			while(rs.next()){
				list.add(rs.getString(1)==null?"ss":rs.getString(1)) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				dbopt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
}
