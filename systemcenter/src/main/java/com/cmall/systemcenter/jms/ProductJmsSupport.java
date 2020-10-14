package com.cmall.systemcenter.jms;

import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapzero.enumer.EJmsMessageType;
import com.srnpr.zapzero.support.JmsSupport;

public class ProductJmsSupport {
	
	/**
	 * 改变商品的
	 */
	public final static String ProductJmsTypeName = "ProductChange";
	/**
	 * 改变sku库存的时候，生成静态页面
	 */
	public final static String SkuJmsTypeName = "SkuChange";
	/**
	 * 输入的时候，替换非法字符
	 */
	public final static String IllegalWordsJmsTypeName = "IllegalWords";
	
	/**
	 * 生成静态页面
	 */
	public final static String CmallCache = "CmallCache";
	
	public void onChangeForProductChangePrice(String productCode){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.ProductJmsTypeName, productCode, null, EJmsMessageType.Toplic);
		//JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductChange, productCode, new MDataMap());
		updateSolrData(productCode);
	}

	public void onChangeForProductChangeStock(String productCode){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.ProductJmsTypeName, productCode, null, EJmsMessageType.Toplic);
		//JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductChange, productCode, new MDataMap());
		updateSolrData(productCode);
	}
	
	public void onChangeForProductChangeAll(String productCode){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.ProductJmsTypeName, productCode, null, EJmsMessageType.Toplic);
		//JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductChange, productCode, new MDataMap());
		updateSolrData(productCode);
		//JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductStatusChange, productCode, new MDataMap());
		
	}
	
	public void onChangeForSkuChangePrice(String productCode){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.SkuJmsTypeName, skuCode, null, EJmsMessageType.Toplic);
		//JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductChange, productCode, new MDataMap());
		updateSolrData(productCode);
	}

	public void onChangeForSkuChangeStock(String productCode){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.SkuJmsTypeName, skuCode, null, EJmsMessageType.Toplic);
		//msNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductChange, productCode, new MDataMap());
		updateSolrData(productCode);
	}
	
	public void onChangeForSkuChangeAll(String productCode){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.SkuJmsTypeName, skuCode, null, EJmsMessageType.Toplic);
		//JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductChange, productCode, new MDataMap());
		updateSolrData(productCode);
	}
	
	public void onChangeProductText(String productCode){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.IllegalWordsJmsTypeName, productCode, null, EJmsMessageType.Toplic);
		//JmsNoticeSupport.INSTANCE.sendToplic(JmsNameEnumer.OnProductChange, productCode, new MDataMap());
		updateSolrData(productCode);
	}
	
	public void OnChangeSku(String jsonData){
//		JmsSupport.getInstance().sendMessage(ProductJmsSupport.CmallCache, jsonData, null, EJmsMessageType.Toplic);
	}
	
	public void updateSolrData(String productCode){
		MDataMap dataMap = new MDataMap();
		dataMap.put("productCode", productCode);
		try {
			WebClientSupport.upPost(TopUp.upConfig("productcenter.webclienturladdone"), dataMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
