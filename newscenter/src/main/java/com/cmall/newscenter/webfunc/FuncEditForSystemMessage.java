package com.cmall.newscenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改系统消息
 * 
 * @author shiyz
 * 
 */
public class FuncEditForSystemMessage extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		
		if (mResult.upFlagTrue()) {
			
			DbUp.upTable(mPage.getPageTable()).update(mAddMaps);
			
			//创建时间为当年系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
			
			String create_time = df.format(new Date());
			
			/**如果是系统消息*/
			if(mAddMaps.get("message_type").equals("449746640001")){
				
			
			if(mAddMaps.get("member_send").equals("")){
			
			
			MDataMap map = new MDataMap();
			
			map.put("app_code", mAddMaps.get("manage_code"));
			
			List<MDataMap> list = DbUp.upTable("mc_extend_info_star").query("member_code", "", "app_code=:app_code",map , 0, 0);
			
			if(list.size()!=0){
				
				for(int i=0;i<list.size();i++){
					
					MDataMap mapList = list.get(i);
					
					MDataMap messDataMap = new MDataMap();
					
					messDataMap.put("message_code", mAddMaps.get("message_code"));
					
					messDataMap.put("message_info", mAddMaps.get("message_info"));
					
					messDataMap.put("create_time", create_time);
					
					messDataMap.put("member_send", mapList.get("member_code"));
					
					messDataMap.put("manage_code", mAddMaps.get("manage_code"));
					
					messDataMap.put("message_type", mAddMaps.get("message_type"));
					
					messDataMap.put("url", mAddMaps.get("url"));
					
					messDataMap.put("send_time", mAddMaps.get("send_time"));
					
					
					DbUp.upTable("nc_system_message").dataUpdate(messDataMap, "message_info,create_time,member_send,manage_code,url,message_type,send_time", "message_code");
					
					
				}
			}
			
			}else{
			
				MDataMap map = new MDataMap();
				
				map.put("app_code", mAddMaps.get("manage_code"));
				
				map.put("member_code", mAddMaps.get("member_send"));
				
				List<MDataMap> list = DbUp.upTable("mc_extend_info_star").query("member_code", "", "app_code=:app_code and member_code=:member_code",map , 0, 0);
				
				if(list.size()!=0){
					
						MDataMap mapList = list.get(0);
						
						MDataMap messDataMap = new MDataMap();
						
						messDataMap.put("message_code", mAddMaps.get("message_code"));
						
						messDataMap.put("message_info", mAddMaps.get("message_info"));
						
						messDataMap.put("create_time", create_time);
						
						messDataMap.put("member_send", mapList.get("member_code"));
						
						messDataMap.put("manage_code", mAddMaps.get("manage_code"));
						
						messDataMap.put("message_type", mAddMaps.get("message_type"));
						
						messDataMap.put("url", mAddMaps.get("url"));
						
						messDataMap.put("send_time", mAddMaps.get("send_time"));
						
						DbUp.upTable("nc_system_message").dataUpdate(messDataMap, "message_info,create_time,member_send,manage_code,url,message_type,send_time", "message_code");
						
						
				}else {
					
					mResult.setResultMessage(bInfo(969905017));
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
