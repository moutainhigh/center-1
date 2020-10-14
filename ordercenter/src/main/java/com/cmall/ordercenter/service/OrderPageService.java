package com.cmall.ordercenter.service;

import java.util.HashMap;
import java.util.Map;
/**
 * 订单分页
 * @author wz
 *
 */
public class OrderPageService {
	/**
	 * 传页数返回跳书
	 * @param page  页数
	 * @return 
	 */
	public Map<String,Integer> pageNumber(int page){
		int startNumber = (page-1) * 10;
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("startNumber", startNumber);
		map.put("endNumber", 10);
		return map;
	}
	
	/**
	 * 
	 * @param page   页数 
	 * @param num	条数
	 * @return
	 */
	public Map<String,Integer> pageNumber(int page,int num){
		int startNumber = 0;
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		if(num!=0){
			startNumber = (page-1) * num;
			map.put("endNumber", num);
		}else {
			startNumber = (page-1) * 10;
			map.put("endNumber", 10);
		}
		map.put("startNumber", startNumber);
		return map;
	}
}
