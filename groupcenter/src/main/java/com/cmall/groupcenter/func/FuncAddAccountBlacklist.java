package com.cmall.groupcenter.func;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 增加微公社黑名单手机号(与账户无关)
 * @author GaoYang
 * @CreateDate 2015年4月20日下午2:33:04
 *
 */
public class FuncAddAccountBlacklist extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mWebResult=new MWebResult();
		
		String mobiles = mDataMap.get("zw_f_mobile_no");
		
		if(mWebResult.upFlagTrue()){
			if(StringUtils.isNotBlank(mobiles)){
				//按照换行符截取手机号,不要求校验是否为正确的手机号格式
				String[] mobAry = mobiles.split("\n");
				
				if(mobAry.length >0){
					for(int i=0;i<mobAry.length;i++){
						MDataMap mInsertMap = new MDataMap();
						//手机号为空不录入
						String mob = mobAry[i].toString().trim();
						if(StringUtils.isNotBlank(mob)){
							mInsertMap.put("mobile_no", mob);
							mInsertMap.put("create_time", FormatHelper.upDateTime());
							DbUp.upTable("gc_account_blacklist").dataInsert(mInsertMap);
						}
					}
				}
			}else{
				mWebResult.setResultMessage(bInfo(918505178));
			}

		}
		return mWebResult;
	}

}
