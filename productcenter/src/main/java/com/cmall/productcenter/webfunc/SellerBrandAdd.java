package com.cmall.productcenter.webfunc;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;


public class SellerBrandAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap _mDataMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
	
		if (mResult.upFlagTrue()) {
			String [] brandCodes = _mDataMap.get("brand_code").split(",");
			String bDate = _mDataMap.get("begin_auth_date");
			String eDate = _mDataMap.get("end_auth_date");
			
				for(String _brandCodes : brandCodes){
				
					//System.out.println(" _brandCodes  :"+_brandCodes);
					MDataMap map = new MDataMap();
					map.put("brand_code", _brandCodes);
					map.put("seller_code", _mDataMap.get("seller_code"));
					
					
					//重复绑定的问题？？x
					
					List<MDataMap> list =	DbUp.upTable(mPage.getPageTable()).queryAll("", "", "", map);
					//List<MDataMap> list = DbUp.upTable(mPage.getPageTable()).query("", "","seller_code=:seller_code",map, -1, -1);
					
					if(list.isEmpty()){
						//System.out.println(_brandCodes +" : "+map.get("seller_code"));
						
						map.put("begin_auth_date", bDate);
						map.put("end_auth_date", eDate);
						
						DbUp.upTable(mPage.getPageTable()).dataInsert(map);
					}
//					else{
//						System.out.println(" update ......... ");
//						System.out.println(_brandCodes +" ::: "+map.get("seller_code"));
//						//不应该调用这个方法
//						DbUp.upTable(mPage.getPageTable()).da.dataUpdate(map, "brand_code","seller_code");
//						System.out.println(" &&&&&&&&&&&&&&&&&&&&&&&& ");
//					}
				
				}
			}
			
	
		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
}

