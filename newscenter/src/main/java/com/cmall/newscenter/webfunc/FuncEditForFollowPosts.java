package com.cmall.newscenter.webfunc;


import org.apache.commons.lang.StringUtils;

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
 * 惠美丽姐妹圈修改追帖
 * @author houwen
 * 
 */
public class FuncEditForFollowPosts extends RootFunc {

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
		boolean flag3 = false;
		//正文
		String postConcent="";
		//妆品数据
		String[] cosmeticCodes = null;
		
		if (mResult.upFlagTrue()) {

			// 循环所有结构
			for (MWebField mField : mPage.getPageFields()) {

				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}

				if (mAddMaps.containsKey(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getColumnName());

					mInsertMap.put(mField.getColumnName(), sValue);
					
					if(mField.getColumnName().equals("post_content")){
						if(sValue.trim()!=null && !sValue.trim().equals("")){
							postConcent = sValue;//获取正文内容
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
					
					if(mField.getColumnName().equals("cosmetic_code")){
						if(sValue!=null && !sValue.equals("")){
							flag3=true;
							//获取妆品code
							cosmeticCodes = sValue.split(",");
							if(cosmeticCodes.length > 1){
								mResult.setResultCode(934205136);
								mResult.setResultMessage(bInfo(934205136, "1"));
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
		
		//图片,商品和妆品只能选择其中一项
		if((flag && flag2) || (flag && flag3) || (flag2 && flag3) || (flag && flag2 && flag3)){
			mResult.inErrorMessage(934205134);
			return mResult;
		}
		
		//如果商品,图片,妆品都没有选择，正文不能为空
		if(!flag2 && !flag && !flag3){
			if(StringUtils.isEmpty(postConcent)){
				mResult.inErrorMessage(934205135);
				return mResult;
			}
		}
		
		//如果选择了商品或是图片，正文不能为空
		if((flag2 || flag) && !flag3){
			if(StringUtils.isEmpty(postConcent)){
				mResult.inErrorMessage(934205135);
				return mResult;
			}
		}

//		if(flag && flag2){
//			mResult.inErrorMessage(934205110);
//		}
		
		//如果选择了商品或是图片，正文不能为空
		if((flag2 || flag) && StringUtils.isEmpty(postConcent)){
			mResult.inErrorMessage(934205135);
			return mResult;
		}
		
		//获取妆品图片
		if(cosmeticCodes != null && cosmeticCodes.length > 0){
			String cosmeticCode = cosmeticCodes[0];
			if(!StringUtils.isEmpty(cosmeticCode)){
				MDataMap map = DbUp.upTable("nc_cosmetic_bag").one("cosmetic_code",cosmeticCode);
				String img[] = map.get("photo").split(",");
				mInsertMap.put("post_img", img[0]);
			}
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
