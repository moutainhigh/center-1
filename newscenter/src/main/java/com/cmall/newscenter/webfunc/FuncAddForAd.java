package com.cmall.newscenter.webfunc;



import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加广告
 * 
 * @author shiyz
 * 
 * date 2014-8-19
 * 
 * @version 1.0
 */
public class FuncAddForAd extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		mAddMaps.put("ad_code", WebHelper.upCode("Ad"));
		
		String app_code = bConfig("newscenter.app_code");
		
		mAddMaps.put("app_code", app_code);
		
        /*获取当前登录人*/
		
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		
		mAddMaps.put("create_user", create_user);
		
		String urlType = mDataMap.get("urlType");
		
		String adImgUrl = mAddMaps.get("adImg_url");
		
		if("code".equals(urlType)){
			
			if (!checkIsExistProduct(adImgUrl)&&!checkIsExistSkuCode(adImgUrl)) {
				//返回提示信息"不存在此编号的商品，请重新填写！"
				mResult.setResultCode(941901084);
				mResult.setResultMessage(bInfo(941901084)); 
				return mResult;
			}
			
		}
		
		
        if("informationCode".equals(urlType)){
			
			if (!checkIsExistInformation(adImgUrl)) {
				//返回提示信息"不存在此编号的资讯，请重新填写！"
				mResult.setResultCode(941901100);
				mResult.setResultMessage(bInfo(941901100)); 
				return mResult;
			}
			
		}
        
         if("columnCode".equals(urlType)){
			
			if (!checkIsExistColumn(adImgUrl)) {
				//返回提示信息"不存在此编号的栏目，请重新填写！"
				mResult.setResultCode(941901101);
				mResult.setResultMessage(bInfo(941901101)); 
				return mResult;
			}
			
		}
		
		mAddMaps.put("adImg_url", (urlType+"@@"+adImgUrl));
		
		
		DbUp.upTable("nc_advertise").dataInsert(mAddMaps);
		
		
		return mResult;

	    
	}
	
	
	/**
	 * 检验商品ID是否有对应的商品
	 * @param adImgUrl
	 * @return 
	 */
	private boolean checkIsExistProduct(String adImgUrl) {
		
		//判断数据库中是否存在相同记录
		int count= DbUp.upTable("pc_productinfo").count("product_code", adImgUrl);		
		 if (count >  0) {
				return true;
			}
			return false;
	}
	
	/**
	 * 检验商品ID是否有对应的sku商品
	 * @param adImgUrl
	 * @return 
	 */
	private boolean checkIsExistSkuCode(String adImgUrl) {
		
		//判断数据库中是否存在相同记录
		int count= DbUp.upTable("pc_skuinfo").count("sku_code", adImgUrl);		
		 if (count >  0) {
				return true;
			}
			return false;
	}
	
	/**
	 * 检验资讯ID是否有对应的资讯信息
	 * @param adImgUrl
	 * @return 
	 */
	private boolean checkIsExistInformation(String adImgUrl) {
		
		//判断数据库中是否存在相同记录
		int count= DbUp.upTable("nc_info").count("info_code", adImgUrl);		
		 if (count >  0) {
				return true;
			}
			return false;
	}
	
	/**
	 * 检验栏目ID是否有对应的栏目
	 * @param adImgUrl
	 * @return 
	 */
	private boolean checkIsExistColumn(String adImgUrl) {
		
		//判断数据库中是否存在相同记录
		int count= DbUp.upTable("nc_category").count("category_code", adImgUrl);		
		 if (count >  0) {
				return true;
			}
			return false;
	}
	
	
}
