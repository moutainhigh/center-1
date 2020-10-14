package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.util.DataPaging;
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
 * 添加标签
 * @author houwen
 *
 */
public class FuncAddForLabel extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		// 定义组件判断标记
		boolean bFlagComponent = false;

		recheckMapField(mResult, mPage, mAddMaps);
		
		
		MDataMap map = new MDataMap();
		MPageData mPageData = new MPageData();
		mPageData = DataPaging.upPageData("nc_label_manager", "", "", map, new PageOption());

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
					
					if(mField.getColumnName().equals("label_name")){
						if(sValue!=null && !sValue.equals("")){
							if(!isNumber(sValue)){
								mResult.inErrorMessage(934205108);
								return mResult;
							}
							if(isRepeat(mPageData, sValue)){
								mResult.inErrorMessage(93405113);
								return mResult;
							}
						}
					}
				}
			

			}
		}
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		
		mInsertMap.put("create_time", df.format(new Date()));   // new Date()为获取当前系统时间
		
		mInsertMap.put("app_code", UserFactory.INSTANCE.create().getManageCode());
		if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
			mResult.inErrorMessage(941901073);
		}else{
			mInsertMap.put("creater", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
		}
		
		if (mResult.upFlagTrue()) {
			
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
	
	public static boolean isNumber(String str) {//判断是否为2-4个汉字   ^[\u4e00-\u9fa5]{2,4}
		   return str.matches("^[\u4e00-\u9fa5]{2,4}");
		   
		}
	
	public static boolean isRepeat(MPageData mPageData,String value) {//判断标签名称是否重复
		   
		   for(int i = 0;i<mPageData.getListData().size();i++){
			   if(value.equals(mPageData.getListData().get(i).get("label_name"))){
				   return true;
			   }
		   }
		   return false;
		}
	
}
