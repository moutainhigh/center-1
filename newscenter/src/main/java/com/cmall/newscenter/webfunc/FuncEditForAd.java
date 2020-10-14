package com.cmall.newscenter.webfunc;



import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改广告
 * 
 * @author shiyz
 * 
 * date 2014-8-19
 * 
 * @version 1.0
 */
public class FuncEditForAd extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		mAddMaps.put("ad_code", WebHelper.upCode("Ad"));
		
		String app_code = bConfig("newscenter.app_code");
		
		mAddMaps.put("app_code", app_code);
		
        /*获取当前登录人*/
		
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		
       String urlType = mDataMap.get("urlType");
		
	   String adImgUrl = mAddMaps.get("adImg_url");
	   
	 //获取”广告位编号“,"广告名称","uid","开始时间","结束时间","权值"
		String placeCode=mAddMaps.get("place_code");
		
		String adName=mAddMaps.get("ad_name");
		
		String uid = mAddMaps.get("uid");
		
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
		
       //检验广告位中是否以存在相同名称的广告
			if (checkInfo(placeCode,adName,uid)) {
				//返回提示信息"该广告位下已存在相同名称的广告，添加失败！"
				mResult.setResultCode(941901081);
				mResult.setResultMessage(bInfo(941901081));
				return mResult;
			}
		
		mAddMaps.put("create_user", create_user);
		
		mAddMaps.put("adImg_url", (urlType+"@@"+adImgUrl));
		
		DbUp.upTable("nc_advertise").update(mAddMaps);
		
		
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
	
	/**
	 * 校验此广告位中是否已经存在相同名称的广告
	 * @param placeCode,adName
	 * @return 
	 */
	private boolean checkInfo(String placeCode,String adName,String uid) {
		StringBuffer strBuffer = new StringBuffer();						//拼接where条件
		strBuffer.append(" place_code = '"+ placeCode);
		strBuffer.append("' and ad_name = '"+adName);
		strBuffer.append("' and uid != '"+uid+"'");
		
		//判断数据库中是否存在相同记录
		 List<MDataMap> list = DbUp.upTable("nc_advertise").queryAll("", "",strBuffer.toString(),null);		
		 if (list.size() > 0) {
				return true;
			}
			return false;
	}
}
