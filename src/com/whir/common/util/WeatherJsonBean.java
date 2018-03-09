package com.whir.common.util;

public class WeatherJsonBean {
	  //气象台名称
	  private String qxtmc;
	  //全要素FC
	  private String qysfc;
	  //全要素FT
	  private String qysft;
	  //重要天气命中率
	  private String zytqmzl;
	  //重要天气虚警率
	  private String zytqxjl;
	  //重要天气漏报率;
	  private String zytqlbl;
	  //重要天气临界成功指数
	  private String zytqljcgzs;
	  //重要天气准确率
	  private String zytqzql;
	  //观测错情率
	  private String gccql;
	public String getQxtmc() {
		return qxtmc;
	}
	public void setQxtmc(String qxtmc) {
		this.qxtmc = qxtmc;
	}
	public String getQysfc() {
		return qysfc;
	}
	public void setQysfc(String qysfc) {
		this.qysfc = qysfc;
	}
	public String getQysft() {
		return qysft;
	}
	public void setQysft(String qysft) {
		this.qysft = qysft;
	}
	public String getZytqmzl() {
		return zytqmzl;
	}
	public void setZytqmzl(String zytqmzl) {
		this.zytqmzl = zytqmzl;
	}
	public String getZytqxjl() {
		return zytqxjl;
	}
	public void setZytqxjl(String zytqxjl) {
		this.zytqxjl = zytqxjl;
	}
	public String getZytqlbl() {
		return zytqlbl;
	}
	public void setZytqlbl(String zytqlbl) {
		this.zytqlbl = zytqlbl;
	}
	public String getZytqljcgzs() {
		return zytqljcgzs;
	}
	public void setZytqljcgzs(String zytqljcgzs) {
		this.zytqljcgzs = zytqljcgzs;
	}
	public String getZytqzql() {
		return zytqzql;
	}
	public void setZytqzql(String zytqzql) {
		this.zytqzql = zytqzql;
	}
	public String getGccql() {
		return gccql;
	}
	public void setGccql(String gccql) {
		this.gccql = gccql;
	}
}
