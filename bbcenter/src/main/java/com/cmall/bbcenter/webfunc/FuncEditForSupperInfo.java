package com.cmall.bbcenter.webfunc;

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

/***
 * 编辑供应商
 * @author jl
 *
 */
public class FuncEditForSupperInfo  extends RootFunc {

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
				
				if (mAddMaps.containsKey(mField.getFieldName()) && StringUtils.isNotEmpty(mField.getColumnName())) {
					String sValue = mAddMaps.get(mField.getFieldName());
					mInsertMap.put(mField.getColumnName(), sValue);
				}
			}
		}
		
		String company_name=mInsertMap.get("company_name");
		String company_name_old=mAddMaps.get("company_name_old");
		
		if (mResult.upFlagTrue()) {
			if(checkName(company_name,company_name_old)){
				//返回提示信息
				mResult.setResultCode(909401005);
				mResult.setResultMessage(bInfo(909401005,company_name));
			}else{
				//////该地方虽然操作两张表，但事务性不强，所以不做事务
				DbUp.upTable(mPage.getPageTable()).dataUpdate(mInsertMap, "", "uid");
				DbUp.upTable("bc_supplier_log").dataInsert(new MDataMap("supplier_code",mInsertMap.get("supplier_code"),"action_",mAddMaps.get("action_"),"update_user",mInsertMap.get("update_user"),"update_time",mInsertMap.get("update_time")));
				mResult.setResultMessage(bInfo(969909001));
			}
		}
		return mResult;
	}

	private boolean checkName(String company_name,String company_name_old){
		if(company_name.equals(company_name_old)){
			return false;
		}
		
		String sql="SELECT company_name from bc_supplier_info where company_name=:company_name ";
		List<Map<String, Object>> list=DbUp.upTable("bc_supplier_info").dataSqlList(sql, new MDataMap("company_name",company_name));
		if(list==null||list.size()<1){//不存在的情形
			return false;
		}
		return true;
	}
	
}
