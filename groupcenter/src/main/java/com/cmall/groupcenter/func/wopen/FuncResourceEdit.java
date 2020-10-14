package com.cmall.groupcenter.func.wopen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.util.WebGetImgSizeUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncResourceEdit  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
        String imgUrl=mAddMaps.get("img_url");
        String[] imgEnd={"jpg","png"};
        boolean hasImg=false;
        if(imgUrl!=null&&!imgUrl.equals("")){
    		for(int i=0;i<imgEnd.length;i++){
    			if(imgUrl.toLowerCase().endsWith(imgEnd[i])){
    				hasImg=true;
    			}	
    		}
        }else{
        	hasImg=true;
        }

		if(mAddMaps.get("resource_name").length()>30){
//			mResult.setResultCode(200);
//			mResult.setResultMessage("菜单名称长度不能超过30字符");
			mResult.inErrorMessage(918547004);
			
		}else if(!hasImg){
//			mResult.setResultCode(300);
//			mResult.setResultMessage("菜单图标必须是图片格式");
			mResult.inErrorMessage(918547005);
		}else{
			String sParentValue=mAddMaps.get("parent_code");
			String uid=mAddMaps.get("uid");
			MDataMap  appNameMap =new MDataMap();
			appNameMap.put("uid",uid);
			appNameMap.put("parent_code",sParentValue);
			appNameMap.put("resource_name",mAddMaps.get("resource_name"));
			appNameMap.put("flag_able","4497473700040001");//菜单可用状态
			List<Map<String,Object>> appNameMaplist=new ArrayList<Map<String,Object>>();
			appNameMaplist=DbUp.upTable("gc_wopen_resource_center").dataQuery("", "","parent_code=:parent_code and resource_name=:resource_name and flag_able=:flag_able and uid!=:uid", appNameMap, 0, 1);
			if(appNameMaplist!=null&&!appNameMaplist.isEmpty()){
				mResult.inErrorMessage(918547012);
				return mResult;  	
		     }else{
		    	 if(hasImg){
					if(imgUrl!=null&&!imgUrl.equals("")){
						int imgSize=WebGetImgSizeUtil.getImageSize(imgUrl);
						if(imgSize>102400){
						//	mResult.setResultCode(400);
						//	mResult.setResultMessage("菜单图标大小不能大于100k");
							mResult.inErrorMessage(918547003);
							return mResult;
						}
					}
	
				}
	
				DbUp.upTable("gc_wopen_resource_center").dataUpdate(mAddMaps,"","uid");
				mResult.setResultCode(1);
				mResult.setResultMessage("修改成功");
		     }
		}
		
		return mResult;
	}
	
}
