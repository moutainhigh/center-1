package com.cmall.groupcenter.active;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ClassUtils;

import com.cmall.groupcenter.active.product.SkuInfoForRet;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商品类下活动调度器
 * @author jlin
 *
 */
public class ActiveControllerForProduct {

	
	private String appCode = "";//所属 appcode
	
	/**
	 * 重写构造方法，设置appcode，默认appcode为sku所属的app
	 * @param appCode
	 */
	public ActiveControllerForProduct(String appCode) {
		this.appCode=appCode;
	}
	
	/**
	 * 商品类活动管道<br>
	 * 如果出现错误，立即终止
	 * @param buyerCode
	 * @param skuNumMap
	 * @param retResult
	 * @return
	 */
	public List<SkuInfoForRet> activeGallery (Map<String, Integer> skuNumMap,MDataMap paramsExt,RootResultWeb retResult){
		
		return activeGallery(skuNumMap, paramsExt, retResult, true);
	}
	
	
	/**
	 * 商品类活动管道
	 * @param skuNumMap key：skucode value:数量
	 * @param isErrorStop 出现错误是否终止，若不中止，请传入false
	 * @param buyerCode 买家编号
	 * @param paramsExt 参数扩展
	 * @return
	 */
	public List<SkuInfoForRet> activeGallery (Map<String, Integer> skuNumMap,MDataMap paramsExt,RootResultWeb retResult,boolean isErrorStop) {
		
		if(paramsExt==null){
			paramsExt=new MDataMap();
		}
		
		List<SkuInfoForRet> retList= new ArrayList<SkuInfoForRet>();//返回的sku信息
		
		//获取活动列表
		List<Map<String, Object>> activeTypeList=new LinkedList<Map<String, Object>>();
		initActiveType(activeTypeList);
		
		for (Map.Entry<String, Integer> map : skuNumMap.entrySet()) {
			
			String skuCode = map.getKey();
			int skuNum=map.getValue();
			
			
			Set<String> mutexs = new HashSet<String>();//存储所有互斥的类型编号
			//查询sku的基本信息
			String sql="SELECT " +
					"s.zid,s.uid,s.sku_code_old,s.sku_code,s.product_code,s.sell_price,p.market_price,s.stock_num,s.sku_key,s.sku_keyvalue,s.sku_picurl,s.sku_name,s.sku_adv,s.sell_productcode,s.seller_code,s.security_stock_num,s.product_code_old,s.qrcode_link,s.sell_count,s.sale_yn " +
					" from pc_skuinfo s LEFT JOIN pc_productinfo p on s.product_code=p.product_code where sku_code=:sku_code";
			Map<String,Object> skuInfoMap=DbUp.upTable("pc_skuinfo").dataSqlOne(sql, new MDataMap("sku_code",skuCode));
			SerializeSupport<ProductSkuInfo> ss = new SerializeSupport<ProductSkuInfo>();
			ProductSkuInfo pcSkuInfo=new ProductSkuInfo();
			ss.serialize(new MDataMap(skuInfoMap), pcSkuInfo);
			
			//设置返回的sku信息
			SkuInfoForRet skuInfo = new SkuInfoForRet();
			skuInfo.setSkuCode(pcSkuInfo.getSkuCode());
			skuInfo.setSkuName(pcSkuInfo.getSkuName());
			skuInfo.setSellPrice(pcSkuInfo.getSellPrice());
			skuInfo.setTransactionPrice(pcSkuInfo.getSellPrice());
			skuInfo.setSkuKey(pcSkuInfo.getSkuKey());
			skuInfo.setSkuValue(pcSkuInfo.getSkuKeyvalue());
			skuInfo.setProductCode(pcSkuInfo.getProductCode());
			skuInfo.setSkuAdv(pcSkuInfo.getSkuAdv());
			skuInfo.setSkuPicurl(pcSkuInfo.getSkuPicUrl());
			skuInfo.setMarketPrice(pcSkuInfo.getMarketPrice());
			
			
			retList.add(skuInfo);
			
			for (Map<String, Object> mDataMap : activeTypeList) {
				String type_code = (String)mDataMap.get("type_code");
//				String type_name = mDataMap.get("type_name");
				String mutex_type_codes [] = (String[])mDataMap.get("mutex_type_codes");
				IActiveForProduct active = (IActiveForProduct)mDataMap.get("active");
				
				
				//判断活动是否已经被互斥了
				if(mutexs.contains(type_code)){
					continue;
				}else{
					//把互斥的活动类型code添加到set中
					mutexs.addAll(Arrays.asList(mutex_type_codes));
				}
				
				
				if(retResult==null){
					retResult=new RootResultWeb();
				}
				
				
				//来一个活动类型信息，共子类使用
				ActiveType activeType = new ActiveType();
				activeType.setHandle_class((String)mDataMap.get("handle_class"));
				activeType.setMutex_type_code((String)mDataMap.get("mutex_type_code"));
				activeType.setPri_sort((String)mDataMap.get("pri_sort"));
				activeType.setRemark((String)mDataMap.get("remark"));
				activeType.setType_code((String)mDataMap.get("type_code"));
				activeType.setType_name((String)mDataMap.get("type_name"));
				
				
				//若果商品参与活动
				BaseActive baseActive = active.doProcess(activeType,pcSkuInfo, skuNum,appCode,paramsExt, retResult);
				
				if(paramsExt.get("purchase_limit_order_num")!=null){//此段代码供购物车使用，与本活动无关
					skuInfo.setPurchase_limit_order_num(Integer.valueOf(paramsExt.get("purchase_limit_order_num")));
				}
				
				//查看返回结果，一旦出现返回结果为失败的情况，立即终止活动管道
				if(retResult.getResultCode()!=1){
					if(isErrorStop){
						break;
					}else{
						continue;
					}
					
				}
				
				
				if(baseActive!=null){
					skuInfo.getActiveList().add(baseActive);
					skuInfo.setTransactionPrice(baseActive.getActivePrice());
					break;
				}
				
			}
		}
		
		return retList;
	}
	
	
	/**
	 * 查询出所有的可用活动类型
	 * @param activeTypeList
	 */
	private void initActiveType (List<Map<String, Object>> activeTypeList){
		List<MDataMap> list=DbUp.upTable("gc_activity_type").queryAll("type_code,type_name,pri_sort,mutex_type_code,handle_class,remark", "pri_sort", "", null);
		if(list!=null&&list.size()>0){
			for (MDataMap mDataMap : list) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				String handle_class = mDataMap.get("handle_class");
				//实例化活动
				IActiveForProduct active = null ;
				try {
					Class<?> cClass = ClassUtils.getClass(handle_class);
					if (cClass != null && cClass.getDeclaredMethods() != null) {
						active =(IActiveForProduct)cClass.newInstance();
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
				map.put("mutex_type_codes", ((String)mDataMap.get("mutex_type_code")).split(","));
				map.put("active", active);
				
				map.putAll(mDataMap);
				activeTypeList.add(map);
			}
		}
	}
	
}
