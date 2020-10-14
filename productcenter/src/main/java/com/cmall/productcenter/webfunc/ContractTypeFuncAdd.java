package com.cmall.productcenter.webfunc;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.FuncAdd;
import com.srnpr.zapweb.webmodel.MWebResult;

/** 
* @ClassName: contractTypeFuncAdd 
* @Description: 添加合同类型
* @author 张海生
* @date 2015-9-10 下午4:14:12 
*  
*/
public class ContractTypeFuncAdd extends FuncAdd {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		String create_time = DateUtil.getSysDateTimeString();// 系统当前时间
		/* 获取当前登录人 */
		String create_user = UserFactory.INSTANCE.create().getLoginName();
		
		mDataMap.put("zw_f_create_time", create_time);
		mDataMap.put("zw_f_create_user", create_user);
		mDataMap.put("zw_f_update_time", create_time);
		mDataMap.put("zw_f_update_user", create_user);
		try {
			String typeName = mDataMap.get("zw_f_contract_type_name");
			if(StringUtils.isNotEmpty(typeName)){
				String typeNameArray[] = typeName.split(",");
				for (int i = 0; i < typeNameArray.length; i++) {
					int count = DbUp.upTable("fh_contract_type").count("contract_type_name",typeNameArray[i]);
					if(count > 0){
						continue;
					}
					mDataMap.put("zw_f_contract_type_code", WebHelper.upCode("CTC"));
					mDataMap.put("zw_f_contract_type_name", typeNameArray[i]);
					mResult = super.funcDo(sOperateUid, mDataMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(959701033);
		}
		return mResult;
	}

}
