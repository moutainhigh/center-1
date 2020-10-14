package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.cmall.systemcenter.common.DateUtil;
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
 * 发布 官方活动
 * @author houwen
 *
 */
public class FuncAddForOfficialActivity extends RootFunc {
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
		}
		
		/*//编码自动生成
		mInsertMap.put("configuration_id", WebHelper.upCode(""));*/
		
		if (mResult.upFlagTrue()) {
		//创建时间为当年系统时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		
		mInsertMap.put("create_time", df.format(new Date()));   // new Date()为获取当前系统时间
		mInsertMap.put("info_category","4497465000030001");
		mInsertMap.put("manage_code",UserFactory.INSTANCE.create().getManageCode());
		mInsertMap.put("info_code",WebHelper.upCode("HML")); 
		if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
			mResult.inErrorMessage(941901073);
		}else{
			mInsertMap.put("create_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
		}
		
		String start_time=mAddMaps.get("begin_time");
		String end_time=mAddMaps.get("end_time");
		
		//判断开始时间必须小于结束时间
		if(start_time!=null && !start_time.equals("")){
		if(Long.valueOf(start_time.replace("-", "").replace(" ", "").replace(":", ""))>=Long.valueOf(end_time.replace("-", "").replace(" ", "").replace(":", ""))){
			mResult.setResultMessage(bInfo(939301111));
			mResult.setResultCode(939301111);
			return mResult;
		}}
		
		String now=DateUtil.getSysDateTimeString();
		//判断开始时间必须小于结束时间
		if(end_time!=null && !end_time.equals("")){
		if(Long.valueOf(now.replace("-", "").replace(" ", "").replace(":", ""))>=Long.valueOf(end_time.replace("-", "").replace(" ", "").replace(":", ""))){
			mResult.setResultMessage(bInfo(939301114));
			mResult.setResultCode(939301114);
			return mResult;
		}
		}
		
		String online_time=mAddMaps.get("online_time");
		String offline_time=mAddMaps.get("offline_time");
		
		//判断开始时间必须小于结束时间
		if(online_time!=null && !online_time.equals("")){
		if(Long.valueOf(online_time.replace("-", "").replace(" ", "").replace(":", ""))>=Long.valueOf(offline_time.replace("-", "").replace(" ", "").replace(":", ""))){
			mResult.setResultMessage(bInfo(939301117));
			mResult.setResultCode(939301117);
			return mResult;
		}
		}
		//String now=DateUtil.getSysDateTimeString();
		//判断开始时间必须小于结束时间
		if(offline_time!=null && !offline_time.equals("")){
		if(Long.valueOf(now.replace("-", "").replace(" ", "").replace(":", ""))>=Long.valueOf(offline_time.replace("-", "").replace(" ", "").replace(":", ""))){
			mResult.setResultMessage(bInfo(939301118));
			mResult.setResultCode(939301118);
			return mResult;
		}
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
		}
		if (mResult.upFlagTrue()) {
			
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}

}
