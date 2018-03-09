package com.whir.portal.service;

import java.util.ArrayList;
import java.util.List;

public class PortalInfService {
	/**
	 * 
	 * @param queryStr
	 *            String[] 0 用户ID
	 * @return List [0]-数据列表list [1]-参数list 0 pager.offset，1 recordCount
	 */
	public List getAttentionMatterList(String queryStr[]) {
		List resultList = new ArrayList();
		List list = new ArrayList();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		List data = new ArrayList();

		if (list != null && list.size() > 0) {
			for (int jj = 0; jj < list.size(); jj++) {
				Object[] objjj = (Object[]) list.get(jj);
				List listi = new ArrayList();
				listi = java.util.Arrays.asList(objjj);
				data.add(listi);
			}
		}

		// 结果集
		resultList.add(data);// [0]-数据列表list

		return resultList;
	}
}
