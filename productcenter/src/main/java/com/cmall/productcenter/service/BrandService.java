package com.cmall.productcenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.util.PinYin;
import com.cmall.systemcenter.support.TermChangeSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;

public class BrandService {
	public boolean addBrand(MDataMap mDataMap) {
		try {
			mDataMap.put("brand_code", WebHelper.upCode("44971602"));// 获取以LC开头的信息编号
			mDataMap.put("parent_code", "44971602");
			
			DbUp.upTable("pc_brandinfo").dataInsert(mDataMap);
			
			String term = mDataMap.get("brand_name");
			if(DbUp.upTable("sc_word_term").count("term", term)== 0) {
				String pinyin = PinYin.getFullSpell(term);
				
				// 保存到词库表
				MDataMap insertMap = new MDataMap();
				insertMap.put("term", term);
				insertMap.put("pinyin", pinyin);
				insertMap.put("create_time", FormatHelper.upDateTime());
				insertMap.put("create_user", UserFactory.INSTANCE.create().getLoginName());
				DbUp.upTable("sc_word_term").dataInsert(insertMap);
				
				// 记录日志表
				MDataMap logInsertMap = new MDataMap();
				logInsertMap.put("term", term);
				logInsertMap.put("oper_type", TermChangeSupport.TYPE_ADD+"");
				logInsertMap.put("create_time", insertMap.get("create_time"));
				logInsertMap.put("create_user", insertMap.get("create_user"));
				DbUp.upTable("lc_word_term_log").dataInsert(logInsertMap);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/** 
	* @Description:查询品牌关联的商品数
	* @param brandCode 品牌号
	* @author 张海生
	* @date 2015-3-16 下午3:15:43
	* @return int 
	* @throws 
	*/
	public int productNumOfBrand(String brandCode,String sellerCode){
		if (StringUtils.isNotBlank(sellerCode)) {
			return DbUp.upTable("pc_productinfo").count("brand_code",brandCode,"seller_code",sellerCode);
		}
		return DbUp.upTable("pc_productinfo").count("brand_code",brandCode);
	}
}
