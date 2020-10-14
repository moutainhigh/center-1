package com.cmall.newscenter.webfunc;


import java.util.List;
import java.util.Map;

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
 * 小时代帖子修改 区分惠美丽的是：没有化妆品
 * @author houwen
 * 2015-2-5
 *
 */
public class FuncEditForXsdPosts extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		// 定义组件判断标记
		boolean bFlagComponent = false;
		boolean flag = false ;
		boolean flag2 = false;
		
		String tplUid = mDataMap.get("zw_f_uid");
		MDataMap dataMap = new MDataMap();
		MDataMap dataMap2 = new MDataMap();
		dataMap.put("uid", tplUid);
		List<Map<String, Object>> maps = DbUp.upTable("nc_posts").dataQuery("", "", "", dataMap, -1, -1);
		if (mResult.upFlagTrue()) {

			// 循环所有结构
			for (MWebField mField : mPage.getPageFields()) {

				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}

				if (mAddMaps.containsKey(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getColumnName());

					mInsertMap.put(mField.getColumnName(), sValue);
					
					if(mField.getColumnName().equals("isofficial")){
						if(sValue.equals("449746760001")){
							
							dataMap2.put("member_code", maps.get(0).get("publisher_code").toString());
							
							dataMap2.put("flag", "449746950001");
							List<Map<String, Object>> mapss = DbUp.upTable("mc_extend_info_star").dataQuery("", "", "", dataMap2, -1, -1);
							if(mapss.size()==0){
								mResult.inErrorMessage(934205115);
								return mResult;
							}
							
						}
						
					}
					
					if(mField.getColumnName().equals("post_praise")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "0");
						}
					}
					
					
					if(mField.getColumnName().equals("post_browse")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "0");
						}
					}
					
					if(mField.getColumnName().equals("sort")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "9999");
						}
					}
					
					if(mField.getColumnName().equals("post_img")){
						if(sValue!=null && !sValue.equals("")){
							flag=true;
						}
					}
					
					if(mField.getColumnName().equals("product_code")){
						if(sValue!=null && !sValue.equals("")){
							flag2=true;
						}
					}
					
					if(mField.getColumnName().equals("post_title")){
						if(sValue.trim()!=null && !sValue.trim().equals("")){
							if(sValue.trim().length()<3 || sValue.trim().length()>30){
								mResult.inErrorMessage(934205113);
								return mResult;
							}
						}
					}
					
					if(mField.getColumnName().equals("post_content")){
						if(sValue.trim()!=null && !sValue.trim().equals("")){
							if(sValue.trim().length()<10 || sValue.trim().length()>1000){
								mResult.inErrorMessage(934205114);
								return mResult;
							}
						}
					}
					
					
				} else if (mField.getFieldTypeAid().equals("104005103")) {
					//特殊判断修改时如果没有传值 则自动赋空
					mInsertMap.put(mField.getColumnName(), "");
				}

			}
		}

		//商品和图片只能选 其中一个
		if(flag && flag2){
			mResult.inErrorMessage(934205110);
		}
		
		if (mResult.upFlagTrue()) {
			DbUp.upTable(mPage.getPageTable())
					.dataUpdate(mInsertMap, "", "uid");

			if (bFlagComponent) {

				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {

						WebUp.upComponent(mField.getSourceCode()).inEdit(
								mField, mDataMap);

					}
				}

			}

		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
	
	
	public static boolean isNumber(String str) {//判断整型
		   return str.matches("[0-9]*");
		}

}
