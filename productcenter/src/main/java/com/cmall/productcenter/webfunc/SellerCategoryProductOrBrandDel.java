package com.cmall.productcenter.webfunc;

import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * ClassName:店铺私有类目新增与商品的关系 <br/>
 * Date:     2013-10-26 下午4:03:19 <br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryProductOrBrandDel extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")) {
				
				
				MDataMap mThisMap=null;
				

				// 循环所有结构
				for (MWebField mField : mPage.getPageFields()) {

					if (mField.getFieldTypeAid().equals("104005003")) {
						
						if(mThisMap==null)
						{
							mThisMap=DbUp.upTable(mPage.getPageTable()).one("uid",mDelMaps.get("uid"));
						}
						
						
						WebUp.upComponent(mField.getSourceCode()).inDelete(mField,
								mThisMap);
						
					}
				}
				
				//如果是删除  商品分类 或 商品品牌表，需要更新solr索引库,需要更新商品分类 或 品牌中间表
				if("uc_sellercategory_brand_relation".equals(mPage.getPageTable())||"uc_sellercategory_product_relation".equals(mPage.getPageTable())) {
					if("uc_sellercategory_brand_relation".equals(mPage.getPageTable())) {

					}else {
						//先根据uid查出product_code
						String pageTable = mPage.getPageTable();
						MDataMap chargeNameMap = DbUp.upTable(pageTable).one("uid", mDelMaps.get("uid"));
						String productCode = chargeNameMap.get("product_code").toString();
						//更新 索引库 
						PlusHelperNotice.onChangeProductInfo(productCode);
						//触发消息队列
						new ProductJmsSupport().onChangeForProductChangeAll(productCode);
						//更新分类商品数量表
						XmasKv.upFactory(EKvSchema.IsUpdateCategoryProductCount).set("isUpdateCateProd","update");
					}
				}

				
				DbUp.upTable(mPage.getPageTable()).delete("uid",
						mDelMaps.get("uid"));
				
			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}

		return mResult;
	}
}

