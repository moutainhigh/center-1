package com.cmall.productcenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;

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
 * 修改栏目类型
 * 
 * @author 李国杰
 * 
 */
public class FuncEditForColumnType extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		// 定义组件判断标记
		boolean bFlagComponent = false;

		if (mResult.upFlagTrue()) {

			// 循环所有结构
			for (MWebField mField : mPage.getPageFields()) {
				
				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}
				if (mAddMaps.containsKey(mField.getColumnName())) {
					String sValue = mAddMaps.get(mField.getColumnName());
					mInsertMap.put(mField.getColumnName(), sValue);
				} else if (mField.getFieldTypeAid().equals("104005103")) {
					//特殊判断修改时如果没有传值 则自动赋空
					mInsertMap.put(mField.getColumnName(), "");
				}
			}
			
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
			//更新时间为当年系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			
			mInsertMap.put("update_time", df.format(new Date()));   // new Date()为获取当前系统时间
			
			//先判断登录是否有效
			if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
				mResult.inErrorMessage(941901073);
			}else{
				mInsertMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
			}
			
			//获取栏目类型的”栏目类型名称“，“栏目类型uid”
			String columnName=mAddMaps.get("column_name");
			String uid=mAddMaps.get("uid");
			//根据columnName与uid校验在数据库中是否有相同品牌名称存在,如果存在则返回提示信息
			if(checkInfo(columnName.trim(),uid)){
				//返回提示信息
				mResult.setResultCode(941901077);
				mResult.setResultMessage(bInfo(941901077));
				return mResult;
			}else{
				DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");
			}
		}

		if (mResult.upFlagTrue()) {
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;
	}
	/**
	 * 校验是否与数据库里有重复
	 * @param columnName，uid
	 * @return 
	 */
	private boolean checkInfo(String columnName,String uid) {
		int atCount = DbUp.upTable("nc_column_type").count("column_name", columnName);		//判断数据库中是否存在品牌名称
		
		if(atCount >= 1){
			MDataMap columnTypeData = DbUp.upTable("nc_column_type").one("uid", uid);		//得到数据库中此品牌的信息
			String columnNameData = columnTypeData.get("column_name");						//得到数据库中此品牌的column_name
			//如果两个栏目类型名称相同则说明没有对栏目类型名称做修改操作，可以进行下一步操作，返回false
			if (columnNameData.equals(columnName)) {				
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
}
