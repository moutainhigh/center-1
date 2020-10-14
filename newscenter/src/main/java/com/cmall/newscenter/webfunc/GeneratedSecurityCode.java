package com.cmall.newscenter.webfunc;

import java.util.UUID;

import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 生成防伪码
 * @author shiyz	
 * date 2014-09-20
 * @version 1.0
 */
public class GeneratedSecurityCode extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		MDataMap batchMap = new MDataMap();
		
		/*防伪批次*/
		String security_batch = FormatHelper.upDateTime().replace("-", "").replace(":", "").trim().replace(" ", "")+Math.round(Math.random()*9000+1000);
		
		/*系统当前时间*/
		String create_time = com.cmall.newscenter.util.DateUtil.getNowTime();
		
		MDataMap map = DbUp.upTable("nc_commodity_channel").one("channel_appcode",mAddMaps.get("security_appcode"),"channel_code",mAddMaps.get("security_source"));
		
		
		batchMap.put("security_generationtime", create_time);
		
		batchMap.put("security_batch", security_batch);
		
		batchMap.put("security_appcode", mAddMaps.get("security_appcode"));
		
		batchMap.put("security_itemname", mAddMaps.get("sku_name"));
		
		batchMap.put("security_num", mAddMaps.get("security_num"));
		
		if(map!=null){
			
			batchMap.put("security_source", map.get("uid"));
		}
		
		batchMap.put("security_productiontime", mAddMaps.get("security_productiontime"));
		
		batchMap.put("security_itemnumber", mAddMaps.get("security_itemnumber"));
		
		/**将防伪码信息插入nc_security_code表中*/
		DbUp.upTable("nc_security_code").dataInsert(batchMap);	
		
		
		MDataMap securMap = new MDataMap();
		
		securMap.put("security_batch", batchMap.get("security_batch"));
		
		securMap.put("security_app", batchMap.get("security_appcode"));
		
		securMap.put("security_itemnumber", batchMap.get("security_itemnumber"));
		
        if(map!=null){
			
        	securMap.put("security_source", batchMap.get("security_source"));
		}
		
		
		securMap.put("security_itemname", mAddMaps.get("sku_name"));
		
		int num =0;
		
		
		try{
			if (mResult.upFlagTrue()) {
				
				int security_num = Integer.valueOf(mAddMaps.get("security_num"));
				
				if(security_num!=0){
					
					
					for(int i=0;i<security_num;i++){
						
						
						/*批次内序号*/
						 num = Integer.valueOf(bConfig("newscenter.security_num"));
						
						 securMap.put("security_batchnum", String.valueOf(num+i));
						
						String security_code = ""+mAddMaps.get("link_address")+"?code="+batchMap.get("security_appcode")+"-"+batchMap.get("security_itemnumber")+"-"+UUID.randomUUID()+"&app=liujialing&type=check";
						
						/*防伪码*/
						securMap.put("security_code", security_code);
						
						DbUp.upTable("nc_securitycode_details").dataInsert(securMap);	
						
					}
					
				}
				
				
			}
		}catch (Exception e) {
			mResult.inErrorMessage(959701033);
		}
	
	return mResult;
	}
}
