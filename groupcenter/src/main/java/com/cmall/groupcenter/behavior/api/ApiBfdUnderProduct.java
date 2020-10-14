package com.cmall.groupcenter.behavior.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.behavior.common.StatusEnum;
import com.cmall.groupcenter.behavior.config.BfdUnderProductConfig;
import com.cmall.groupcenter.behavior.model.BfdUnderProductInfo;
import com.cmall.groupcenter.behavior.response.BfdUnderProductResponse;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 通知百分点商品下架
 * @author pang_jhui
 *
 */
public class ApiBfdUnderProduct {
	
	public static void main(String[] args) {
          new ApiBfdUnderProduct().doProcess();
	}
	
	
	public void doProcess(){
		//获取更新商品
		List<BfdUnderProductInfo> productInfos=new ArrayList<BfdUnderProductInfo>();
		MDataMap mWhereMap=new MDataMap();
		//MDataMap mDataMap = DbUp.upTable("lc_request_bfd_productoffline_log").oneWhere("product_code,productUpdateTime", "update_time desc", "", "");
		Map<String, Object> mDataMap = DbUp.upTable("lc_request_bfd_productoffline_log").dataSqlOne("SELECT productCode,productUpdateTime FROM lc_request_bfd_productoffline_log ORDER BY productUpdateTime DESC LIMIT 1", null);
		mWhereMap.put("lastUpateTime",String.valueOf(mDataMap.get("productUpdateTime")));
		//List<MDataMap> lpList = DbUp.upTable("pc_productinfo").queryAll("product_code,update_time", "update_time desc", "product_status in('4497153900060003','4497153900060004') and update_time>=:update_time", selectParam);
		List<Map<String, Object>>  lpList= DbUp.upTable("sc_flow_bussiness_history").dataSqlList("SELECT    p.`product_code`,   h.`create_time`  FROM   systemcenter.`sc_flow_bussiness_history` h ,  productcenter.`pc_productinfo` p       WHERE   p.`update_time`>=:lastUpateTime  AND h.`create_time` >=:lastUpateTime  AND h.`flow_code` = p.`uid`  AND h.`current_status` IN (     '4497153900060003',     '4497153900060004'   )    AND p.product_status IN (     '4497153900060003',     '4497153900060004'   )  ORDER BY h.`create_time` DESC LIMIT 1000;", mWhereMap);
		if(lpList!=null && lpList.size()>0){
	    	for (Map<String, Object> lp : lpList) {
		    	productInfos.add(new BfdUnderProductInfo(lp.get("product_code").toString()));
		    }
			
			BfdUnderProductResponse process = process(productInfos);
			//更新最后一条记录
			if(process.getResultCode()==1){
				MDataMap insertParam=new MDataMap();
				insertParam.put("productCode", String.valueOf(lpList.get(0).get("product_code")));
				insertParam.put("productUpdateTime", String.valueOf(lpList.get(0).get("create_time")));
				insertParam.put("ctime", DateUtil.getSysDateTimeString());
				DbUp.upTable("lc_request_bfd_productoffline_log").dataInsert(insertParam);
			}else{	
				MDataMap insertParam=new MDataMap();
				String resutlMessage=StringUtils.trimToEmpty(process.getResultMessage());
				resutlMessage= resutlMessage.length()>50?resutlMessage.substring(0, 50):resutlMessage;
				insertParam.put("remarks", "同步错误!"+resutlMessage);
				insertParam.put("ctime", DateUtil.getSysDateTimeString());
				DbUp.upTable("lc_request_bfd_productoffline_log").dataInsert(insertParam);
			}
	    }else{
	    	MDataMap insertParam=new MDataMap();
	    	insertParam.put("remarks", "木有数据可以同步!");
			insertParam.put("ctime", DateUtil.getSysDateTimeString());
			DbUp.upTable("lc_request_bfd_productoffline_log").dataInsert(insertParam);
	    }	
		
		
	}
	
	
	public BfdUnderProductResponse process(List<BfdUnderProductInfo> productInfos){
		
		BfdUnderProductResponse response = new BfdUnderProductResponse();
		
		String productJson = "";
		
		/*百分点商品下架配置信息*/
		BfdUnderProductConfig config = new BfdUnderProductConfig();
		
		if(productInfos != null){
			
			JsonHelper<List<BfdUnderProductInfo>> helper = new JsonHelper<List<BfdUnderProductInfo>>();
			
			productJson = helper.ObjToString(productInfos);
			
		}
		
		/*获取登录信息*/
		String ssk = new ApiBfdLoginInfo().process().getSessionKey();
		
		String sUrl = config.getRequestPath();
		
		MDataMap mDataMap = new MDataMap();
		
		mDataMap.put("ssk", ssk);
		
		mDataMap.put("data", productJson);
		
		try {
			
			String returnMsg = WebClientSupport.upPost(sUrl, mDataMap);
			
			JsonHelper<List<Object>> returnHelper = new JsonHelper<List<Object>>();
			
			List<Object> returnList = new ArrayList<Object>();
			
			returnList = returnHelper.StringToObj(returnMsg, returnList);
			
			int resultCode =  (Integer) returnList.get(0);
			
			if(!StringUtils.equals(Integer.toString(resultCode), StatusEnum.SUCCESS.getCode())){
				
				response.setResultMessage((String)returnList.get(1));
				
				response.setResultCode(StatusEnum.FAILURE.getResultCode());
				
			}
			
			
		} catch (Exception e) {
			
			response.setResultCode(StatusEnum.FAILURE.getResultCode());
			
			response.setResultMessage(e.getMessage());
			
		}
		
		return response;
		
		
		
	}
	
	

}
