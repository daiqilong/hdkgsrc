package com.whir.common.util.parse;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * 数据解析
 * @author daiql
 * @date 2016-4-30
 */
public interface IDataParse {
	/**
	 * 解析接收到的xml格式数据
	 * @param data 数据(返回的结果xml)
	 * @return
	 */
	public List<List<Map<String,Object>>> parseXmlData(String data) throws Exception;
	
	/**
	 * 解析接收到的xml格式数据
	 * @param data 数据(返回的结果xml)
	 * @return
	 */
	public List<Map<String,Object>> parseXmlData(File file) throws Exception;
	/**
	 * 解析接收到的xml格式数据返回record
	 * @param data 数据(返回的结果xml)
	 * @return
	 */
	public String parseXmlRecord(String data) throws Exception;
}
