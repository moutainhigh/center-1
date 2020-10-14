package com.cmall.ordercenter.webfunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

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
 * 添加闪购信息
 * @author jl
 *
 */
public class FuncAddForFlashsalescmService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			//判断登陆用户是否为空
			String loginname=UserFactory.INSTANCE.create().getLoginName();
			if(loginname==null||"".equals(loginname)){
				mResult.inErrorMessage(941901073);
				return mResult;
			}
			
			//创建时间为当年系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			String now=df.format(new Date());
			
			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {
				
				if("update_time".equals(mField.getColumnName())){
					mInsertMap.put("update_time", now);   // 更新时间
				}
				
				if("create_time".equals(mField.getColumnName())){
					mInsertMap.put("create_time", now);   // 添加时间
				}
				
				if("create_user".equals(mField.getColumnName())){
					mInsertMap.put("create_user", loginname);   // 添加的用户
				}
				
				if("update_user".equals(mField.getColumnName())){
					mInsertMap.put("update_user", loginname);   // 更新的用户
				}
				
				if("activity_code".equals(mField.getColumnName())){
					mInsertMap.put("activity_code", WebHelper.upCode("SG"));   // 更新的用户
				}
				
				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getFieldName());

					mInsertMap.put(mField.getColumnName(), sValue);
					
				}
				
			}
		}
		
		mInsertMap.put("status", "449746740001");
		
		String start_time=mInsertMap.get("start_time");
		String end_time=mInsertMap.get("end_time");
		String app_code=mInsertMap.get("app_code");
		
		//判断开始时间必须小于结束时间
		if(Long.valueOf(start_time.replace("-", "").replace(" ", "").replace(":", ""))>=Long.valueOf(end_time.replace("-", "").replace(" ", "").replace(":", ""))){
			mResult.setResultMessage(bInfo(939301111));
			mResult.setResultCode(939301111);
		}
		
		String now=DateUtil.getSysDateTimeString();
		//判断开始时间必须小于结束时间
		if(Long.valueOf(now.replace("-", "").replace(" ", "").replace(":", ""))>=Long.valueOf(end_time.replace("-", "").replace(" ", "").replace(":", ""))){
			mResult.setResultMessage(bInfo(939301114));
			mResult.setResultCode(939301114);
		}
		
		
		////////////////////////////////已经改为同一时间内可以并存多个活动
		//闪购活动不再允许重合 update by jlin  2015-05-22 15:28:00
		if (mResult.upFlagTrue()) {
			//查看是否有时间重合的闪购，若有，则添加应该失败
			//查询不重合的情况
			Map<String, Object> map=DbUp.upTable("oc_activity_flashsales").dataSqlOne("SELECT activity_code from oc_activity_flashsales WHERE !(start_time>:end_time OR end_time<:start_time) and app_code=:app_code", new MDataMap("start_time",start_time,"end_time",end_time,"app_code",app_code));
			if(map!=null&&map.size()>0){
				mResult.setResultMessage(bInfo(939301105));
				mResult.setResultCode(939301105);
			}
		}
		
		if (mResult.upFlagTrue()) {
			DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
	
}
