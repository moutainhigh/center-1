package com.cmall.systemcenter.webfunc;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加新的推荐商品行业
 * @author GaoYang
 *
 */
public class FuncCreateTrade  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()){
			try{
				//获取行业名称
				String tradeName=StringUtils.trim(mSubMap.get("trade_name"));
				//校验是否是相同行业
				if(checkTrade(tradeName)){
					//插入新行业
					String tradeCode = WebHelper.upCode("CTN");
					insertTrade(tradeName,tradeCode);
				}else{
					//返回提示信息
					mResult.setResultCode(949701039);
					mResult.setResultMessage(bInfo(949701039));
					return mResult;
				}
			}catch(Exception e){
				e.printStackTrace();
				mResult.setResultCode(949701038);
				mResult.setResultMessage(bInfo(949701038));
				return mResult;
			}
		}
		return mResult;
	}

	/**
	 * 校验是否是相同行业
	 * @param tradeName
	 */
	private boolean checkTrade(String tradeName) {
		int atCount = DbUp.upTable("agent_trade").count("trade_name", tradeName);
		if(atCount <= 0){
			return true;
		}
		return false;
	}

	/**
	 * 插入新行业
	 * @param tradeName
	 * @param tradeCode
	 */
	private void insertTrade(String tradeName, String tradeCode) {
		MDataMap insMap = new MDataMap();
		UUID uuid = UUID.randomUUID();
		insMap.put("uid", uuid.toString().replace("-", ""));
		insMap.put("trade_code", tradeCode);
		insMap.put("trade_name", tradeName);
		DbUp.upTable("agent_trade").dataInsert(insMap);
	}
	
}
