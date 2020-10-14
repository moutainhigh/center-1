package com.cmall.ordercenter.webfunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 删除闪购活动
 * @author jl
 *
 */
public class FuncDeleteForFlashsalesService extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		MWebResult mResult = new MWebResult();
		MWebOperate mOperate = WebUp.upOperate(sOperateUid);
		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());
		MDataMap mDelMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		if (mResult.upFlagTrue()) {
			if (mDelMaps.containsKey("uid")) {
				
				List<Map<String, Object>> list=DbUp.upTable(mPage.getPageTable()).dataQuery("start_time,activity_code", "", "uid=:uid", new MDataMap("uid",mDelMaps.get("uid")), 0, 0);
				if(list!=null&&list.size()>0){
					Map<String, Object> map=list.get(0);
					String start_time=(String)map.get("start_time");
					String activity_code=(String)map.get("activity_code");
					
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						//当前时间<开始时间时，系统允许参与者删除闪购信息
						Calendar c1=DateUtil.getCalendar(dateFormat.parse(start_time).getTime());
						Calendar c2=DateUtil.getCalendar(new Date().getTime());
						if(c2.compareTo(c1)>=0){
							//不能删除
							mResult.setResultCode(939301101);
							mResult.setResultMessage(bInfo(939301101));
							return mResult;
						}
					} catch (ParseException e) {
					}
					DbUp.upTable(mPage.getPageTable()).delete("uid",mDelMaps.get("uid"));
					DbUp.upTable("oc_flashsales_skuInfo").delete("activity_code",activity_code);//同时删除关联关系
					
					mResult.setResultMessage(bInfo(969909001));
				}
			}
		}
		
		return mResult;
	}

}
