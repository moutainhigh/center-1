package com.cmall.productcenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
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
 * 添加广告位
 * 
 * @author 李国杰
 * 
 */
public class FuncAddForAdPlace extends RootFunc {
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
				}
			}
			if (bFlagComponent) {
				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {
						WebUp.upComponent(mField.getSourceCode()).inAdd(mField,mDataMap);
					}
				}
			}
		}

		if (mResult.upFlagTrue()) {
			//编码自动生成
			mInsertMap.put("place_code", WebHelper.upCode("AdP"));
			//创建时间为当年系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式

			mInsertMap.put("create_time", df.format(new Date()));   // new Date()为获取当前系统时间
			mInsertMap.put("update_time", df.format(new Date()));   // new Date()为获取当前系统时间
			
			//先判断登录是否有效
			if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
				mResult.inErrorMessage(941901073);
				return mResult;
			}else{
				mInsertMap.put("create_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
				mInsertMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
			}
			String appCode=mAddMaps.get("app_code");
			if((!"SI2009".equals(appCode))&&(!"SI2011".equals(appCode))){//应用为家有汇或微公社的时候,不对栏目做判断
				//查询出栏目所属页面插入到数据库里
				MDataMap AdPParam = new MDataMap();
				AdPParam.put("column_code", mAddMaps.get("column_code"));
				AdPParam.put("app_code", appCode);
				List<MDataMap> list = DbUp.upTable("nc_app_column").queryAll("", "","",AdPParam);
				if (null != list && list.size() > 0) {
					mInsertMap.put("page_code", list.get(0).get("page_code"));			//获取page_code
				}else{
					//返回提示信息
					mResult.setResultCode(941901078);
					mResult.setResultMessage(bInfo(941901078,"该APP下没有对应栏目！"));
					return mResult;
				}
			}
			//获取”广告位名称“，“所属栏目”
			String columnCode=mAddMaps.get("column_code");
			String placeName=mAddMaps.get("place_name");
			if (checkInfo(appCode,	placeName.trim(),columnCode)) {
				//返回提示信息
				mResult.setResultCode(941901077);
				mResult.setResultMessage(bInfo(941901077));
				return mResult;
			}else{
				DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
	/**
	 * 校验是否与数据库里有重复
	 * @param appCode, placeName, columnCode
	 * @return 
	 */
	private boolean checkInfo(String appCode,String placeName,String columnCode) {
		
		MDataMap mDataParam = new MDataMap();
		mDataParam.put("place_name", placeName);
		mDataParam.put("app_code", appCode);
		mDataParam.put("column_code", columnCode==null?"":columnCode);
		
		//判断数据库中是否存在相同记录
		 List<MDataMap> list = DbUp.upTable("nc_advertise_place").queryAll("", "","",mDataParam);		
		 if (list.size() > 0) {
				return true;
			}
			return false;
	}
}
