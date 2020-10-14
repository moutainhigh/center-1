package com.cmall.groupcenter.job.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.baidupush.core.httpclient.YunHttpClient;
import com.cmall.groupcenter.util.HttpUtil;
import com.srnpr.xmassystem.support.PlusSupportLD;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 预计每天执行一次
 * @author Angel Joy
 *
 */
public class JobForGetLdAsaleStatusByTime extends RootJob{
	
	 private static Logger logger = Logger.getLogger(YunHttpClient.class.getName());

	@Override
	public void doExecute(JobExecutionContext context) {
		PlusSupportLD ld = new PlusSupportLD();
		String isSyncLd = ld.upSyncLdOrder();
		if("N".equals(isSyncLd)){//添加开关
			logger.info("LD系统调用开关关闭，JobForGetLdAsaleStatusByTime定时执行失败,时间："+DateUtil.getSysDateTimeString());
			return;
		}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("doType", "01");//创建
		params.put("beginTime", DateUtil.addMinute(-1440));//一天前的数据
		params.put("endTime", DateUtil.getSysDateTimeString());//当前时间
		String url = bConfig("groupcenter.rsync_homehas_url")+"getAfterServiceListTo";
		String resultApply = HttpUtil.post(url, JSONObject.toJSONString(params), "UTF-8");
		params.put("doType", "02");//退货完成
		String resultComplate = HttpUtil.post(url, JSONObject.toJSONString(params), "UTF-8");
		params.put("doType", "03");//取消退货
		String resultCancel = HttpUtil.post(url, JSONObject.toJSONString(params), "UTF-8");
		if(StringUtils.isEmpty(resultApply)){
			return;
		}
		List<Map<String,String>> applyAsaleCode = this.getCodeListForAsale(resultApply);
		for(Map<String,String> asleCode : applyAsaleCode) {
			int count = DbUp.upTable("za_exectimer").count("exec_type","449746990025","exec_info",asleCode.get("asaleCode"));
			boolean flag = this.checkFlag(asleCode.get("outOrderCode"));
			if(count <= 0&&flag) {
				JobExecHelper.createExecInfo("449746990025", asleCode.get("asaleCode"), DateUtil.addMinute(1));
			}
		}
		List<Map<String,String>> cancelAsaleCode = this.getCodeListForAsale(resultCancel);
		for(Map<String,String> asleCode : cancelAsaleCode) {
			int count = DbUp.upTable("za_exectimer").count("exec_type","449746990026","exec_info",asleCode.get("asaleCode"));
			boolean flag = this.checkFlag(asleCode.get("outOrderCode"));
			if(count <= 0&&flag) {
				JobExecHelper.createExecInfo("449746990026", asleCode.get("asaleCode"), DateUtil.addMinute(1));
			}
		}
		List<Map<String,String>> complateAsaleCode = this.getCodeListForAsale(resultComplate);
		for(Map<String,String> asleCode : complateAsaleCode) {
			int count = DbUp.upTable("za_exectimer").count("exec_type","449746990027","exec_info",asleCode.get("asaleCode"));
			boolean flag = this.checkFlag(asleCode.get("outOrderCode"));
			if(count <= 0&&flag) {
				JobExecHelper.createExecInfo("449746990027", asleCode.get("asaleCode"), DateUtil.addMinute(1));
			}
		}
		
	}
	
	/**
	 * 校验是否是分销单
	 * @param string
	 * @return
	 */
	private boolean checkFlag(String out_order_code) {
		MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("out_order_code",out_order_code, "small_seller_code", "SI2003");
		
		// 换货单用原单查询
		if(orderInfo != null && StringUtils.isNotBlank(orderInfo.get("org_ord_id"))) {
			orderInfo = DbUp.upTable("oc_orderinfo").one("out_order_code",orderInfo.get("org_ord_id"), "small_seller_code", "SI2003");
		}
		
		if(orderInfo == null || orderInfo.isEmpty()) {
			return false;
		}
		
		String order_code = orderInfo.get("order_code");
		Integer count = DbUp.upTable("fh_agent_order_detail").count("order_code",order_code);
		if(count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 获取售后单号以及LD订单号
	 * @param result
	 * @return
	 */
	private List<Map<String,String>> getCodeListForAsale(String result) {
		List<Map<String,String>> returnCodeList = new ArrayList<Map<String,String>>();
		JSONObject jo = JSONObject.parseObject(result);
		String codeStr = jo.getString("code");
		if(StringUtils.isEmpty(codeStr)){
			return null;
		}
		if(jo.getInteger("code")!= 0){
			return null;
		}
		String jsonArrayStr = jo.getString("result");
		JSONArray ja = JSONArray.parseArray(jsonArrayStr);
		Iterator it = ja.iterator();
		while(it.hasNext()){
			JSONObject oo = (JSONObject) it.next();
			String as_type = StringUtils.isEmpty(oo.getString("AFTER_SALE_TYPE")) ? "T" : oo.getString("AFTER_SALE_TYPE");//售后工单类型 T：退货 H:换货
			if("T".equals(as_type)) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("asaleCode", oo.getString("AFTER_SALE_CODE_LD"));
				map.put("outOrderCode", oo.getString("ORD_ID"));
				returnCodeList.add(map);
			}								
			
		}
		return returnCodeList;
	}

}
