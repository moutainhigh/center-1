package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.beauty.model.CosmeticBag;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 发布小时代姐妹圈
 * @author houwen
 *  2015-2-5
 */
public class FuncAddForXsdPosts extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		// 定义组件判断标记
		boolean bFlagComponent = false;
		boolean flag = false ;
		boolean flag2 = false;
		boolean flag3 = false;
	
		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {
				
				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}
				
				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {
					
					String sValue = mAddMaps.get(mField.getFieldName());
					
					mInsertMap.put(mField.getColumnName(), sValue);
					
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
					
					if(flag && flag2){
						mResult.inErrorMessage(934205110);
						return mResult;
					}
					
					if(mField.getColumnName().equals("sort")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "9999");
						}
						
					}
					if(mField.getColumnName().equals("issessence")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}/*else{
							mInsertMap.put(mField.getColumnName(), "0");
						}*/
						
					}
					if(mField.getColumnName().equals("isofficial")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}/*else{
							mInsertMap.put(mField.getColumnName(), "0");
						}*/
						
					}
					if(mField.getColumnName().equals("post_browse")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "0");
						}
						
					}
					if(mField.getColumnName().equals("post_praise")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205107);
								return mResult;
							}
						}else{
							mInsertMap.put(mField.getColumnName(), "0");
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
					
				}
			}
			
		}
		
		//主帖ID
		String mainPostCode = WebHelper.upCode("HML");
		//APP
		String appCode = UserFactory.INSTANCE.create().getManageCode();
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		String publishTime = df.format(new Date());// new Date()为获取当前系统时间
		
		mInsertMap.put("publish_time", publishTime);   
		
		mInsertMap.put("post_type","449746780001"); // 是否主/追帖 0：主帖，1：追帖
		
		mInsertMap.put("post_catagory","4497465000020001"); // 栏目ID
		
		mInsertMap.put("app_code",appCode); // APP
		
		mInsertMap.put("post_code",mainPostCode); // 帖子ID
		
		mInsertMap.put("type", "449747130001");//默认是普通帖子

		if (mResult.upFlagTrue()) {
			
			//主贴
			DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			
			if (bFlagComponent) {

				for (MWebField mField : mPage.getPageFields()) {
					
					if (mField.getFieldTypeAid().equals("104005003")) {

						WebUp.upComponent(mField.getSourceCode()).inAdd(mField,
								mDataMap);
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
