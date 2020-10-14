package com.cmall.productcenter.webfunc;

import java.util.List;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * ClassName: 删除店铺私有类目（以及类目与对应商品的关系）<br/>
 * @author   jack
 * @version  1.0
 */
public class SellerCategoryDelete extends RootFunc {

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
				List<MDataMap> codes = DbUp.upTable(mPage.getPageTable()).query("seller_code,category_code", "", "", mDelMaps,0,0);
				DbUp.upTable(mPage.getPageTable()).delete("uid",mDelMaps.get("uid"));
				for(int i=0;i<codes.size();i++){
					DbUp.upTable("uc_sellercategory_product_relation").delete("category_code",codes.get(i).get("category_code"),"seller_code",codes.get(i).get("seller_code"));
				}
			}
		}
		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}

}

