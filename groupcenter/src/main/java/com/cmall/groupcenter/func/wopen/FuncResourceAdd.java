package com.cmall.groupcenter.func.wopen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.util.WebGetImgSizeUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncResourceAdd  extends RootFunc{

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
        String resourceName=mAddMaps.get("resource_name");
        
       if(mAddMaps.get("resource_name").length()>30){
//			mResult.setResultMessage("菜单名称长度不能超过30字符");			
			mResult.inErrorMessage(918547004);

			
		}else if(!hasImg){
//			mResult.setResultMessage("菜单图标必须是图片格式");
			mResult.inErrorMessage(918547005);
		}else{
			String sParentValue=mAddMaps.get("parent_code");
			MDataMap  appNameMap =new MDataMap();
			appNameMap.put("parent_code",sParentValue);
			appNameMap.put("resource_name",resourceName);
			appNameMap.put("flag_able","4497473700040001"); //菜单可用状态
			List<Map<String,Object>> appNameMaplist=new ArrayList<Map<String,Object>>();
			appNameMaplist=DbUp.upTable("gc_wopen_resource_center").dataQuery("", "","parent_code=:parent_code and resource_name=:resource_name and flag_able=:flag_able", appNameMap, 0, 1);
			
			if(appNameMaplist!=null&&!appNameMaplist.isEmpty()){
				mResult.inErrorMessage(918547012);
				return mResult;  	
		     }else{
				if(hasImg){
					if(imgUrl!=null&&!imgUrl.equals("")){
						int imgSize=WebGetImgSizeUtil.getImageSize(imgUrl);
	
						if(imgSize>102400){
						//	mResult.setResultMessage("菜单图标大小不能大于100k");
							mResult.inErrorMessage(918547003);
							return mResult;
						}
					}
				}
				
				MDataMap mTopDataMap = DbUp.upTable(
						"gc_wopen_resource_center").oneWhere(
						"resource_code",
						"-resource_code", "", "parent_code",
						sParentValue);
				String sMaxString ="";
				long lMax =1;
				if(null==mTopDataMap){
					lMax =1;
				}else{
				    sMaxString = mTopDataMap.get("resource_code");
				    lMax = Long.parseLong(StringUtils.right(sMaxString, 4)) + 1;
				}
				
			    mAddMaps.put("resource_code",sParentValue+ StringUtils.leftPad(String.valueOf(lMax), 4, "0"));
				DbUp.upTable("gc_wopen_resource_center").dataInsert(mAddMaps);
				mResult.setResultCode(1);
				mResult.setResultMessage("保存成功"); 
			}

		}
		
		return mResult;
	}
	

}
