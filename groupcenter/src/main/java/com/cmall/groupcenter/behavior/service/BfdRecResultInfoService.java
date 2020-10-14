package com.cmall.groupcenter.behavior.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.behavior.api.ApiBfdLoginInfo;
import com.cmall.groupcenter.behavior.common.StatusEnum;
import com.cmall.groupcenter.behavior.config.BfdRecResultConfig;
import com.cmall.groupcenter.behavior.model.BfdRecResultInfo;
import com.cmall.groupcenter.behavior.model.BfdRecommendIdInfo;
import com.cmall.groupcenter.behavior.request.BfdRecResultRequest;
import com.cmall.groupcenter.behavior.response.BfdLoginResponse;
import com.cmall.groupcenter.behavior.response.BfdRecResultResponse;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.rootclass.CacheDefine;
import com.srnpr.zapcom.rootclass.RootCustomCache;
import com.srnpr.zapcom.topdo.TopBase;

/**
 * 百分点业务信息处理
 * @author pang_jhui
 *
 */
public class BfdRecResultInfoService {
	
	private static BfdLoginCache sessKeyCache = new BfdLoginCache();
	
	/**
	 * 初始化百分点推荐信息
	 * @param uid
	 * 		用户唯一标识
	 * @param userName
	 * 		用户名称
	 * @return 百分点结果请求信息
	 */
	public BfdRecResultRequest initBfdRecResultRquest(String uid, String iid,String operFlag, String appkey,BfdRecResultConfig config){
		
		BfdRecResultRequest bfdRecResultRequest = new BfdRecResultRequest();
		
		
		ApiBfdLoginInfo apiBfdLoginInfo = new ApiBfdLoginInfo();
		
		BfdLoginResponse loginResponse = new BfdLoginResponse();
		
		if(sessKeyCache.upValue("SessionKey") == null){
			loginResponse = apiBfdLoginInfo.process();
			if(loginResponse.upFlagTrue() && StringUtils.isNotBlank(loginResponse.getSessionKey())){
				sessKeyCache.inElement("SessionKey", loginResponse.getSessionKey());
			}
		}else{
			loginResponse.setSessionKey(sessKeyCache.upValue("SessionKey"));
		}
		
		bfdRecResultRequest.setUid(uid);
		
		bfdRecResultRequest.setBidlst(config.getRecId(operFlag));
		
		bfdRecResultRequest.setReq(config.getReqRecId(operFlag));
		
		String gid = config.getUserName()+"_"+bfdRecResultRequest.getUid();
		
		bfdRecResultRequest.setGid(gid);
		
		bfdRecResultRequest.setFmt(config.getFmt());
		
		bfdRecResultRequest.setAppkey(appkey);
		
		bfdRecResultRequest.setIid(iid);		
		if(loginResponse.upFlagTrue()){
			
			bfdRecResultRequest.setSsk(loginResponse.getSessionKey());
			
		}
		
		return bfdRecResultRequest;
		
	}
	
	/**
	 * 初始化百分点推荐结果响应信息
	 * @param response
	 * 		响应信息
	 * @param list
	 * 		返回结果集
	 * @return 百分点推荐结果响应信息
	 */
	@SuppressWarnings("unchecked")
	public BfdRecResultResponse initBfdRecResultResponse(BfdRecResultResponse response,List<Object> list){
		
		if(list.size() > 0){
			
			String resultCode = String.valueOf(list.get(0));
			
			// 解决GSON把0转成0.0造成对比不相等的问题
			if(new BigDecimal(resultCode).setScale(0, BigDecimal.ROUND_HALF_UP).equals(new BigDecimal(StatusEnum.SUCCESS.getCode()))){
				
				if(list.size() == 3){
					
					response.setResultCode(StatusEnum.SUCCESS.getResultCode());
					
					List<List<Object>> resultInfos = (List<List<Object>>) list.get(2);
					
					response.setRecResultInfos(initBfdRecResultInfoList(resultInfos));
					
//					/*******begin wangqx*******/
//					
//					BfdRecommendIdInfo bfdRecommendIdInfo = initBfdRecResultInfoList1(resultInfos);
//					response.setRecResultInfos(bfdRecommendIdInfo.getListRecProduct());
//					response.setRecommendId(bfdRecommendIdInfo.getRecommendId());
//					
//					/*******end***********/
					
				}					
				
			}else{
				
				if(list.size() == 2){
					
					response.setResultCode(StatusEnum.FAILURE.getResultCode());
					
					response.setResultMessage(String.valueOf(list.get(1)));
					
				}
				
			}
			
		}
		
		return response;		
		
	
		
	}
	
	/**
	 * 百分点推荐结果信息列表(后续可能存在问题，有时间优化)
	 * @param resultStr
	 * 		返回结果字符串
	 * @return 百分点推荐结果信息列表
	 */
	public List<BfdRecResultInfo> initBfdRecResultInfoList(List<List<Object>> resultInfos){
		
		List<BfdRecResultInfo> resultInfoList = new ArrayList<BfdRecResultInfo>();		
		
		if(resultInfos != null){
			
			for (List<Object> list : resultInfos) {
				
				BfdRecResultInfo bfdRecResultInfo = new BfdRecResultInfo();
				
				// 解决GSON把0转成0.0造成对比不相等的问题
				String resultCode = new BigDecimal(String.valueOf(list.get(0))).intValue()+"";
				
				if(list.size() == 2 && !StringUtils.equals(resultCode, StatusEnum.SUCCESS.getCode())){
					
					bfdRecResultInfo.setResultCode(StatusEnum.FAILURE.getResultCode());
					
					bfdRecResultInfo.setResultMessage(String.valueOf(list.get(1)));
					
				}
				
				if(list.size() == 4 && StringUtils.equals(resultCode, StatusEnum.SUCCESS.getCode())){
					
					bfdRecResultInfo.setResultCode(StatusEnum.SUCCESS.getResultCode());
					
					if(list.get(3) != null){			
						
						JsonHelper<BfdRecResultInfo> recProductHelper = new JsonHelper<BfdRecResultInfo>();
						
						//String jsonStr = StringUtils.replace(String.valueOf(list.get(3)), "{", "{\"");
						
						//jsonStr = StringUtils.replace(jsonStr, "=", "\":\"" );
						
						//jsonStr = "{\""+"recProductInfoList\":"+StringUtils.replace(jsonStr, "}", "\"}")+"}";
						
						JSONObject obj = new JSONObject();
						obj.put("recProductInfoList", list.get(3));
						
						bfdRecResultInfo = recProductHelper.StringToObj(obj.toJSONString(), bfdRecResultInfo);
					}
					
					bfdRecResultInfo.setOpenDS(String.valueOf(list.get(2)).split(":")[1]);
					
				}
				
				resultInfoList.add(bfdRecResultInfo);
				
			}
			
		}
		
		return resultInfoList;
		
	}
	
	/**
	 * 百分点推荐结果信息列表,为了返回推荐结果唯一标识id，改进的方法 @author wangqx
	 * 
	 * @param resultInfos
	 *        返回结果字符串
	 * @return 百分点推荐结果信息
	 */
	public BfdRecommendIdInfo initBfdRecResultInfoList1(List<List<Object>> resultInfos){
		BfdRecommendIdInfo bfdRecommendIdInfo = new BfdRecommendIdInfo();
		
		List<BfdRecResultInfo> resultInfoList = new ArrayList<BfdRecResultInfo>();		
		
		if(resultInfos != null){
			
			BfdRecResultInfo bfdRecResultInfo = null;
			
			for (List<Object> list : resultInfos) {
				bfdRecResultInfo = new BfdRecResultInfo();
						
				// 解决GSON把0转成0.0造成对比不相等的问题
				String resultCode = new BigDecimal(String.valueOf(list.get(0))).intValue()+"";
				
				if(list.size() == 2 && !StringUtils.equals(resultCode, StatusEnum.SUCCESS.getCode())){
					
					bfdRecResultInfo.setResultCode(StatusEnum.FAILURE.getResultCode());
					
					bfdRecResultInfo.setResultMessage(String.valueOf(list.get(1)));
					
				}
				
				if(list.size() == 4 && StringUtils.equals(resultCode, StatusEnum.SUCCESS.getCode())){
					
					bfdRecResultInfo.setResultCode(StatusEnum.SUCCESS.getResultCode());
					
					if(list.get(3) != null){			
						
						JsonHelper<BfdRecResultInfo> recProductHelper = new JsonHelper<BfdRecResultInfo>();
						
						//String jsonStr = StringUtils.replace(String.valueOf(list.get(3)), "{", "{\"");
						
						//jsonStr = StringUtils.replace(jsonStr, "=", "\":\"" );
						
						//jsonStr = "{\""+"recProductInfoList\":"+StringUtils.replace(jsonStr, "}", "\"}")+"}";
						
						JSONObject obj = new JSONObject();
						obj.put("recProductInfoList", list.get(3));
						
						bfdRecResultInfo = recProductHelper.StringToObj(obj.toJSONString(), bfdRecResultInfo);
					}
					
					bfdRecResultInfo.setOpenDS(String.valueOf(list.get(2)).split(":")[1]);
					
				}
				
				resultInfoList.add(bfdRecResultInfo);
				
			}
			
//			bfdRecommendIdInfo.setListRecProduct(resultInfoList);
//			bfdRecommendIdInfo.setRecommendId(bfdRecResultInfo.getOpenDS());
			
		}
		
		return bfdRecommendIdInfo;
		
	}

	static class BfdLoginCache extends TopBase{
		private Cache cache;
		public BfdLoginCache() {
			CacheDefine cDefine = new CacheDefine();
			String sCacheName = this.getClass().getName();
			CacheConfiguration cacheConfiguration = new CacheConfiguration();

			cacheConfiguration.setName(sCacheName);

			// 设置最大数量
			cacheConfiguration.setMaxEntriesLocalHeap(9999999);
			// 设置最长存活时间
			cacheConfiguration.setTimeToIdleSeconds(300);
			cacheConfiguration.setTimeToLiveSeconds(3600);

			cacheConfiguration.setMemoryStoreEvictionPolicy("FIFO");

			cache = cDefine.inCustomCache(sCacheName, cacheConfiguration);
		}

		public void inElement(String k, String v) {

			cache.put(new Element(k, v));
		}

		
		public void inElement(Element element)
		{
			cache.put(element);
		}
		
		public String upValue(String k) {
			Object oReturnObject = null;
			if (cache.isKeyInCache(k)) {
				Element eCachElement = cache.get(k);
				if (eCachElement != null) {
					oReturnObject = eCachElement.getObjectValue();
				}

			}

			return (String)oReturnObject;
		}
	}

}
