package com.cmall.newscenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 商品渠道新增
 * @author dyc
 * */
public class FuncAddProChannel  extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		MDataMap map = new MDataMap();
		
		if (mResult.upFlagTrue()) {
			//查询该APP下有相同的渠道名称没有
			int count = DbUp.upTable("nc_commodity_channel").count("channel_appcode",mAddMaps.get("channel_appcode"),"channel_code", mAddMaps.get("channel_code"));
			if(count==0){
				/*系统当前时间*/
				String create_time = com.cmall.newscenter.util.DateUtil.getNowTime();
				map.put("channel_time", create_time);
				map.put("channel_appcode", mAddMaps.get("channel_appcode"));
				map.put("channel_code", mAddMaps.get("channel_code"));
				map.put("channel_remarks", mAddMaps.get("channel_remarks"));
				map.put("channel_pic", mAddMaps.get("channel_pic"));
				map.put("channel_stats", mAddMaps.get("channel_stats"));
//				map.put("channel_stats", "4497465000060001");//默认可用
				/**将商品渠道信息插入nc_commodity_channel表中*/
				DbUp.upTable("nc_commodity_channel").dataInsert(map);
			}else{
				mResult.inErrorMessage(934205111);
			}
		}
		return mResult;
	}

}
