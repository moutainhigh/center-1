package com.cmall.usercenter.txservice;

import com.cmall.dborm.txmapper.UcSellercategoryProductRelationMapper;
import com.cmall.dborm.txmodel.UcSellercategoryProductRelation;
import com.cmall.dborm.txmodel.UcSellercategoryProductRelationExample;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapdata.dbface.ITxService;
import com.srnpr.zapweb.helper.WebHelper;

public class TxSellercategoryService extends BaseClass implements ITxService {

	public void updateSellercategoryProductRelation(String product_code,String[] category_codes,String seller_code){
		UcSellercategoryProductRelationMapper sellercategoryProductRelationMapper = BeansHelper.upBean("bean_com_cmall_dborm_txmapper_UcSellercategoryProductRelationMapper");
		//删除掉原来的映射关系
		UcSellercategoryProductRelationExample example = new UcSellercategoryProductRelationExample();
		example.createCriteria().andProductCodeEqualTo(product_code);
		sellercategoryProductRelationMapper.deleteByExample(example);
		
		UcSellercategoryProductRelation sellercategoryProductRelation =new UcSellercategoryProductRelation();
		sellercategoryProductRelation.setProductCode(product_code);
		sellercategoryProductRelation.setSellerCode(seller_code);
		
		//插入信息的映射关系
		for (String category_code : category_codes) {
			sellercategoryProductRelation.setUid(WebHelper.upUuid());
			sellercategoryProductRelation.setCategoryCode(category_code);
			sellercategoryProductRelationMapper.insertSelective(sellercategoryProductRelation);
		}
		PlusHelperNotice.onChangeProductInfo(product_code);
		ProductJmsSupport pjs = new ProductJmsSupport();		//触发消息队列
		pjs.onChangeForProductChangeAll(product_code);
		
	}
}
