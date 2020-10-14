package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
 * 修改app栏目
 * @author jl
 *
 */
public class FuncEditForAppColumnService extends RootFunc {

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
				
				if("update_user".equals(mField.getColumnName())){
					mInsertMap.put("update_user", loginname);   // 更新的用户
				}
				
				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {

					String sValue = mAddMaps.get(mField.getFieldName());

					mInsertMap.put(mField.getColumnName(), sValue);
					
				}
				
			}
		}
		
		String app_code=mDataMap.get("zw_f_app_code").trim();
		String column_name=mDataMap.get("zw_f_column_name").trim();
		String app_code_old=mDataMap.get("zw_f_app_code_old").trim();
		String column_name_old=mDataMap.get("zw_f_column_name_old").trim();
		mInsertMap.put("app_code", app_code);
		mInsertMap.put("column_name", column_name);
		
		if (mResult.upFlagTrue()) {
			
			if(checkName(app_code, column_name, app_code_old, column_name_old)){
				//返回提示信息
				mResult.setResultCode(909101013);
				mResult.setResultMessage(bInfo(909101013));
			}else{
				DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");
				mResult.setResultMessage(bInfo(969909001));
			}
		}
		return mResult;

	}
	
	private boolean checkName(String app_code,String column_name,String app_code_old,String column_name_old){
		if(app_code.equals(app_code_old)&&column_name.equals(column_name_old)){
			return false;
		}
		String sql="SELECT c.uid from nc_app_column c LEFT JOIN  nc_app_page p on c.page_code=p.page_code where c.column_name=:column_name  and p.app_code=:app_code ";
		List<Map<String, Object>> list=DbUp.upTable("nc_app_column").dataSqlList(sql, new MDataMap("app_code",app_code,"column_name",column_name));
		if(list==null||list.size()<1){//不存在的情形
			return false;
		}
		return true;
	}
	
}
