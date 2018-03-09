package com.whir.evo.weixin.util;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.whir.evo.weixin.bd.WeiXinBD;
import com.whir.evo.weixin.bo.HandleOrgResultBO;
import com.whir.service.api.person.PersonService;
import com.whir.service.api.system.UserService;

public class WeiXinUtils {
	private static final Logger logger = Logger.getLogger(WeiXinUtils.class);
	private static Token token = null;
	private static Token jstoken = null;
	
	// 创建定长线程池
	private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

	public static String getTokenNew(String secret) {
		logger.debug("getTokenNew---------->>begin"+secret);
		WeiXinBD bd = new WeiXinBD();
		Map<String, String> map = bd.loadWeixinTokenPO(secret);
		String wxToken = "";
		if(map.get("wxToken")!=null && !"".equals(map.get("wxToken")) && !"null".equals(map.get("wxToken"))){
			if ((Long.parseLong(map.get("tokenTimeStamp")) <= System.currentTimeMillis() - 7200*1000)){
				logger.debug("token-->>超时重新获取token");
				try {
					wxToken = getWxToken(secret);
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
				return wxToken;
			}else {
				logger.debug("token--未超时直接读取token");
				wxToken = map.get("wxToken");
				return wxToken;
			}
		}else{
			logger.debug("token--首次生成token");
			try {
				wxToken = getWxToken(secret);
				return wxToken;
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		logger.debug("getTokenNew---------->>end"+wxToken);
		return null;
	}
	
	public static String getToken() {
		if ((token == null) || (token.getEmpires_time().getTime() <= System.currentTimeMillis() + 10L))
			try {
				token = generateToken();
				return token.getAccess_token();
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		else {
			return token.getAccess_token();
		}
		return null;
	}

	/**
	 * 获取访问令牌
	 * 
	 * @return
	 */
	public static String getJSToken() {
		synchronized (WeiXinUtils.class) {
			if (jstoken == null || jstoken.getEmpires_time().getTime() <= System.currentTimeMillis() + 10) {
				try {
					jstoken = generateJSToken();
					return jstoken.getAccess_token();
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}
			} else {
				return jstoken.getAccess_token();
			}
		}
		return null;

	}

	/**
	 * 生成新的访问令牌
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Token generateJSToken() throws Exception {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + getToken();

		String result = com.whir.evo.weixin.util.HttpUtils.get(url);
		org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
		JsonNode node = mapper.readTree(result);
		String access_token = node.get("ticket").getTextValue();
		long expires_in = node.get("expires_in").getLongValue();
		Token token = new Token();
		token.setAccess_token(access_token);
		Date date = new Date();
		date.setTime(date.getTime() + expires_in);
		token.setEmpires_time(date);

		return token;

	}

	@Deprecated
	public static String createOrganization(Organization org,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=" + getTokenNew(secret);

		JSONObject jo = JSONObject.fromObject(org);

		if (org.getId() == null) {
			jo.remove("id");
		}
		if (org.getOrder() == null) {
			jo.remove("order");
		}

		String data = jo.toString();

		logger.debug(data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			if (errcode == 0) {
				String id = String.valueOf(node.get("id").getIntValue());
				return id;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return null;
	}

	/**
	 * 创建企业号组织，返回字符串集合
	 * 
	 * @param org
	 * @return ResultBO
	 */
	public static HandleOrgResultBO createOrg(Organization org,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=" + getTokenNew(secret);
		JSONObject jo = JSONObject.fromObject(org);
		if (org.getId() == null) {
			jo.remove("id");
		}
		if (org.getOrder() == null) {
			jo.remove("order");
		}
		String data = jo.toString();
		logger.debug("创建组织请求包结构体：" + data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			HandleOrgResultBO resultBO = mapper.readValue(result, HandleOrgResultBO.class);
			logger.debug("创建组织接口返回结果：" + result);
			return resultBO;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

	@Deprecated
	public static String updateOrganization(Organization org,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=" + getTokenNew(secret);

		JSONObject jo = JSONObject.fromObject(org);
		if (org.getParentid() == null) {
			jo.remove("id");
		}
		if (org.getOrder() == null) {
			jo.remove("order");
		}
		String data = jo.toString();
		
		logger.debug(data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();
			if (errcode == 0) {
				return org.getId();
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return null;
	}

	/**
	 * 更新组织 返回接口返回的对象
	 * 
	 * @param org
	 * @return
	 */
	public static HandleOrgResultBO updateOrg(Organization org,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=" + getTokenNew(secret);
		JSONObject jo = JSONObject.fromObject(org);
		String data = jo.toString();
		logger.debug("更新组织请求包结构体：" + data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			HandleOrgResultBO resultBO = mapper.readValue(result, HandleOrgResultBO.class);
			logger.debug("更新组织接口返回结果：" + result);
			return resultBO;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

	/**
	 * 根据父id获取全部组织列表
	 * 
	 * @param parentId
	 * @return
	 */
	public static String getAllOrgList(String parentId) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=" + getToken() + "&id="
				+ parentId;
		String result = null;
		try {
			result = HttpUtils.get(url);
			logger.debug("获取组织列表返回结果：" + result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return result;
	}

	/**
	 * 批量删除微信企业号组织
	 * 
	 * @param idList
	 */
	public static void deleteWxOrg(List<String> idList) {
		String url = "";
		if (idList == null) {
			return;
		}
		for (int i = 0, size = idList.size(); i < size; i++) {
			url = "https://qyapi.weixin.qq.com/cgi-bin/department/delete" + getToken() + "&id=" + idList.get(i);
			try {
				HttpUtils.get(url);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean getUser(String userid) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=" + getToken() + "&userid=" + userid;
		String result = null;
		try {
			result = HttpUtils.get(url);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return false;
	}

	@Deprecated
	public static boolean createUser(User user,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=" + getTokenNew(secret);
		JSONObject jo = JSONObject.fromObject(user);
		if(user.getGender() == -1){
			jo.remove("gender");
		}
		String data = jo.toString();
		logger.debug(data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();
			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return false;
	}

	/**
	 * 创建企业号用户并返回响应数据对象
	 * 
	 * @param user
	 * @return
	 */
	public static HandleOrgResultBO createEmp(User user,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=" + getTokenNew(secret);
		JSONObject jo = JSONObject.fromObject(user);
		if(user.getGender() == -1){
			jo.remove("gender");
		}
		String data = jo.toString();
		logger.debug("创建用户请求数据：" + data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			HandleOrgResultBO resultBO = mapper.readValue(result, HandleOrgResultBO.class);
			logger.debug("创建用户接口返回结果：" + result);
			return resultBO;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

	@Deprecated
	public static boolean updateUser(User user,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=" + getTokenNew(secret);
		JSONObject jo = JSONObject.fromObject(user);
		String data = jo.toString();
		logger.debug(data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug("updateUser------->>result:"+result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();
			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return false;
	}

	/**
	 * 更新企业号用户并返回响应数据对象
	 * 
	 * @param user
	 * @return HandleOrgResultBO
	 */
	public static HandleOrgResultBO updateEmp(User user,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=" + getTokenNew(secret);
		JSONObject jo = JSONObject.fromObject(user);
		String data = jo.toString();
		logger.debug("响应数据：" + data);
		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			HandleOrgResultBO resultBO = mapper.readValue(result, HandleOrgResultBO.class);
			logger.debug("更新用户接口返回结果：" + result);
			return resultBO;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新企业号用户异常......................");
		}
		return null;
	}

	public static boolean deleteUser(String userid,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=" + getTokenNew(secret) + "&userid=" + userid;
		String result = null;
		try {
			result = HttpUtils.get(url);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return true;
	}

	public static boolean deleteOrgnization(String orgid,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=" + getTokenNew(secret) + "&id="
				+ orgid;
		String result = null;
		try {
			result = HttpUtils.get(url);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return true;
	}

	public static boolean removeMenu(String agentid,String secret) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/delete?access_token=" + getTokenNew(secret) + "&agentid="
				+ agentid;
		String result = null;
		try {
			result = HttpUtils.get(url);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return true;
	}

	public static boolean createMenu(String agentid, String data,String secret) {
		if ((agentid == null) || ("".equals(agentid))) {
			return true;
		}
		String url = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=" + getTokenNew(secret) + "&agentid="
				+ agentid;

		String result = null;
		try {
			result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return false;
	}

	public static String getUserId(String code, String agentid, String token) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + token + "&code=" + code
				+ "&agentid=" + agentid;
		try {
			String result = HttpUtils.get(url);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			String userId = node.get("UserId").getTextValue();
			return userId;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

	public void test() {
		UserService us = new UserService();

		Map m = new HashMap();

		List list = us.getOrgByUserIdWithRange("", "0", "0", "0", "*0*", 10000, 0);

		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}

		List list2 = us.getUserList("", "", "", "", "", "0", "", "", 0, "*0*", 10000, "0");
		for (int i = 0; i < list.size(); i++) {
			System.out.println("==" + list.get(i));
		}
		PersonService ps = new PersonService();
		try {
			ps.getInnerPersonList("28", "62", "", "0", "100", "1", "", "0", "", "");
			Map m2 = ps.getInnerPersonList("28", "62", "*62*", "0", "100", "1", "", "1", "", "");
			Iterator it = m2.keySet().iterator();
			while (it.hasNext())
			System.out.println(m2.get(it.next()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean sendTextMsg(String text, String agentid, String touser, String topart, String totag) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + getToken();
		TxtMsg msg = new TxtMsg();
		msg.setAgentid(agentid);

		msg.setTouser(touser);
		msg.getText().setContent(text);
		JSONObject jo = JSONObject.fromObject(msg);

		String data = jo.toString();

		logger.debug(data);
		try {
			String result = HttpUtils.post(url, data);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return false;
	}

	public static boolean sendNewsMsg(WeiXinUtils.TxtMsg.Article[] articles, String agentid, String touser,
			String topart, String totag) {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + getToken();
		if ((agentid == null) || ("".equals(agentid))) {
			return false;
		}
		TxtMsg msg = new TxtMsg();
		msg.setAgentid(agentid);

		msg.setMsgtype("news");
		msg.setTouser(touser);

		msg.getNews().setArticles(articles);

		JSONObject jo = JSONObject.fromObject(msg);

		String data = jo.toString();

		logger.debug(data);
		try {
			String result = HttpUtils.post(url, data);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return false;
	}

	public static boolean sendNewsMsg(String title, String description, String linkurl, String picurl, String agentid,
			String touser, String topart, String totag) {
		System.out.println("sendNewsMsg");
		String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + getToken();
		if ((agentid == null) || ("".equals(agentid))) {
			return false;
		}
		TxtMsg msg = new TxtMsg();
		msg.setAgentid(agentid);

		msg.setMsgtype("news");
		msg.setTouser(touser);

		WeiXinUtils.TxtMsg.Article article = new WeiXinUtils.TxtMsg.Article();
		article.setTitle(title);
		article.setUrl(linkurl);
		article.setPicurl(picurl);
		article.setDescription(description);

		msg.getNews().setArticles(new WeiXinUtils.TxtMsg.Article[] { article });

		JSONObject jo = JSONObject.fromObject(msg);
		if (linkurl == null) {
			jo.remove("linkurl");
		}
		if (picurl == null) {
			jo.remove("picurl");
		}
		if (description == null) {
			jo.remove("description");
		}

		String data = jo.toString();
		System.out.println(data);
		logger.debug(data);
		try {
			String result = HttpUtils.post(url, data);
			System.out.println(result);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(result);
			logger.debug(result);
			int errcode = node.get("errcode").getIntValue();
			String errmsg = node.get("errmsg").getTextValue();

			return errcode == 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}

		return false;
	}

	/**
	 * 异步处理发送消息
	 * 
	 * @param title
	 * @param description
	 * @param linkurl
	 * @param picurl
	 * @param agentid
	 * @param touser
	 * @param topart
	 * @param totag
	 * @return
	 */
	public static void asynSendNewsMsg(final String title, final String description, final String linkurl,
			final String picurl, final String agentid, final String touser, final String topart, final String totag) {
		fixedThreadPool.execute(new Runnable() {
			public void run() {
				try {
					String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + getToken();
					TxtMsg msg = new TxtMsg();
					msg.setAgentid(agentid);
					msg.setMsgtype("news");
					msg.setTouser(touser);
					WeiXinUtils.TxtMsg.Article article = new WeiXinUtils.TxtMsg.Article();
					article.setTitle(title);
					article.setUrl(linkurl);
					article.setPicurl(picurl);
					article.setDescription(description);
					msg.getNews().setArticles(new WeiXinUtils.TxtMsg.Article[] { article });
					JSONObject jo = JSONObject.fromObject(msg);
					if (linkurl == null) {
						jo.remove("linkurl");
					}
					if (picurl == null) {
						jo.remove("picurl");
					}
					if (description == null) {
						jo.remove("description");
					}
					String data = jo.toString();
					System.out.println("data::::::"+data);
					logger.debug(data);
					String result = HttpUtils.post(url, data);
					ObjectMapper mapper = new ObjectMapper();
					JsonNode node = mapper.readTree(result);
					logger.debug(result);
					int errcode = node.get("errcode").getIntValue();
					String errmsg = node.get("errmsg").getTextValue();
					logger.error(Thread.currentThread().getName() + "---线程发送消息errCode：" + errcode);
					logger.error(Thread.currentThread().getName() + "---线程发送消息errMsg：" + errmsg);
				} catch (Exception e) {
					logger.error(Thread.currentThread().getName() + ":--------线程发送消息异常--------");
					logger.error(e);
				}
			}
		});
	}

	private static Token generateToken() throws Exception {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + Constants.corpid + "&corpsecret="
				+ Constants.corpsecret;

		String result = HttpUtils.get(url);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(result);
		String access_token = node.get("access_token").getTextValue();
		long expires_in = node.get("expires_in").getLongValue();
		Token token = new Token();
		token.setAccess_token(access_token);
		Date date = new Date();
		date.setTime(date.getTime() + expires_in);
		token.setEmpires_time(date);

		return token;
	}
	private static String getWxToken(String corpsecret) throws Exception {
		if(corpsecret == null || "".equals(corpsecret) || "null".equals(corpsecret)){
			corpsecret = Constants.corpsecret;
		}
		String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + Constants.corpid + "&corpsecret="+corpsecret;
		logger.debug("getWxToken---url--------->"+url);
		String result = HttpUtils.get(url);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(result);
//		int errcode = node.get("errcode").getIntValue();
//		if(errcode == 0){
			String access_token = node.get("access_token").getTextValue();
			long expires_in = node.get("expires_in").getLongValue();
			WeiXinBD bd = new WeiXinBD();
			bd.saveWeixinToken(access_token, new Date().getTime()+"", corpsecret);
//		}
		logger.debug("getWxToken---access_token--------->"+access_token);	
		return access_token;
	}

	public static final class Organization {
		private String name;
		private String parentid;
		private String order;
		private String id;

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getParentid() {
			return this.parentid;
		}

		public void setParentid(String parentid) {
			this.parentid = parentid;
		}

		public String getOrder() {
			return this.order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public String getId() {
			return this.id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	static final class Token {
		private String access_token;
		private Date empires_time;

		public String getAccess_token() {
			return this.access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public Date getEmpires_time() {
			return this.empires_time;
		}

		public void setEmpires_time(Date empires_time) {
			this.empires_time = empires_time;
		}
	}

	public static final class TxtMsg {
		private String touser;
		private String toparty;
		private String totag;
		private String msgtype = "text";
		private String agentid;
		private Text text = new Text();
		private String safe = "0";
		private News news = new News();

		public News getNews() {
			return this.news;
		}

		public void setNews(News news) {
			this.news = news;
		}

		public String getTouser() {
			return this.touser;
		}

		public void setTouser(String touser) {
			this.touser = touser;
		}

		public String getToparty() {
			return this.toparty;
		}

		public void setToparty(String toparty) {
			this.toparty = toparty;
		}

		public String getTotag() {
			return this.totag;
		}

		public void setTotag(String totag) {
			this.totag = totag;
		}

		public String getMsgtype() {
			return this.msgtype;
		}

		public void setMsgtype(String msgtype) {
			this.msgtype = msgtype;
		}

		public String getAgentid() {
			return this.agentid;
		}

		public void setAgentid(String agentid) {
			this.agentid = agentid;
		}

		public Text getText() {
			return this.text;
		}

		public void setText(Text text) {
			this.text = text;
		}

		public String getSafe() {
			return this.safe;
		}

		public void setSafe(String safe) {
			this.safe = safe;
		}

		public static final class Article {
			private String title;
			private String description;
			private String url;
			private String picurl;

			public String getTitle() {
				return this.title;
			}

			public void setTitle(String title) {
				this.title = title;
			}

			public String getDescription() {
				return this.description;
			}

			public void setDescription(String description) {
				this.description = description;
			}

			public String getUrl() {
				return this.url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getPicurl() {
				return this.picurl;
			}

			public void setPicurl(String picurl) {
				this.picurl = picurl;
			}
		}

		public class News {
			private WeiXinUtils.TxtMsg.Article[] articles;

			public News() {
			}

			public WeiXinUtils.TxtMsg.Article[] getArticles() {
				return this.articles;
			}

			public void setArticles(WeiXinUtils.TxtMsg.Article[] articles) {
				this.articles = articles;
			}
		}

		public class Text {
			private String content;

			public Text() {
			}

			public String getContent() {
				return this.content;
			}

			public void setContent(String content) {
				this.content = content;
			}
		}
	}

	public static final class User {
		private String userid;
		private String name;
		private int[] department;
		private String position;
		private String mobile;
		private int gender;
		private String tel;
		private String email;
		private String weixinid;

		public String getUserid() {
			return this.userid;
		}

		public void setUserid(String userid) {
			this.userid = userid;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int[] getDepartment() {
			return this.department;
		}

		public void setDepartment(int[] department) {
			this.department = department;
		}

		public String getPosition() {
			return this.position;
		}

		public void setPosition(String position) {
			this.position = position;
		}

		public String getMobile() {
			return this.mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public int getGender() {
			return this.gender;
		}

		public void setGender(int gender) {
			this.gender = gender;
		}

		public String getTel() {
			return this.tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getEmail() {
			return this.email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getWeixinid() {
			return this.weixinid;
		}

		public void setWeixinid(String weixinid) {
			this.weixinid = weixinid;
		}
	}
}