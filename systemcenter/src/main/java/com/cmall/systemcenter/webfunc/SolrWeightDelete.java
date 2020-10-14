package com.cmall.systemcenter.webfunc;


import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 删除搜索权重数据
 * @author wangqingxia
 *
 */
public class SolrWeightDelete extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")) {
				MDataMap mg = DbUp.upTable(mPage.getPageTable()).one("uid",mDelMaps.get("uid"));
				if(!mg.isEmpty()){
					int count = DbUp.upTable("pc_solr_weight").count("seller_code",mg.get("seller_code"));
					if(count>0){
						mResult.inErrorMessage(959701035);
					}else{
						DbUp.upTable(mPage.getPageTable()).delete("uid",mDelMaps.get("uid"));						
					}
					
				}
			}
		}
		
		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		
		return mResult;
	}

}
