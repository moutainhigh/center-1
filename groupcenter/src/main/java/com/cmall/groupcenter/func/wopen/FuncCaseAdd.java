package com.cmall.groupcenter.func.wopen;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class FuncCaseAdd  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		
		MDataMap mWhereMap  = new MDataMap();
		String casename=mAddMaps.get("case_name");
		casename=casename.trim();
		String caseimg=mAddMaps.get("case_img");
		String[] imgEnd={"jpg","bmp","gif","png","jpeg","exif","tga","tiff","svg"};
		
		if(null==casename||casename.equals("")){
//			mResult.setResultCode(0);
//			mResult.setResultMessage("合作案例名称不能为空");
			mResult.inErrorMessage(918547006);
		}else if(null==caseimg||caseimg.equals("")){
//			mResult.setResultCode(0);
//			mResult.setResultMessage("合作案例图标不能为空");
			mResult.inErrorMessage(918547007);
			
		}else if(!mAddMaps.get("case_url").startsWith("http://")){
//			mResult.setResultCode(0);
//			mResult.setResultMessage("合作案例url必须以http://开头");
			mResult.inErrorMessage(918547008);
			
			
		}else{
			if(casename.length()<=30){
				
				
			    mWhereMap.put("case_name",casename);
			
			
			    Object value = DbUp.upTable("gc_cooperative_case").dataGet("uid", null, mWhereMap);
			    boolean hasImg=false;
				if(value==null){
					if(null!=caseimg&&!caseimg.equals("")){
						for(int i=0;i<imgEnd.length;i++){
							if(caseimg.toLowerCase().endsWith(imgEnd[i])){
								hasImg=true;
							}	
						}
						if(!hasImg){
//							mResult.setResultCode(0);
//							mResult.setResultMessage("合作案例图标必须是图片格式");
							mResult.inErrorMessage(918547009);
						}else{
							String UserCode = UserFactory.INSTANCE.create().getUserCode();
							mWhereMap.put("create_member_code", UserCode);
							mWhereMap.put("create_time", DateUtil.getSysDateTimeString());
							mWhereMap.put("case_img", mAddMaps.get("case_img"));
							mWhereMap.put("flag_able", mAddMaps.get("flag_able"));
							mWhereMap.put("case_url", mAddMaps.get("case_url"));
							
							DbUp.upTable("gc_cooperative_case").dataInsert(mWhereMap);
							mResult.setResultCode(1);
							mResult.setResultMessage("保存成功");
						}
						
					}

				}else {
//					mResult.setResultCode(0);
//					mResult.setResultMessage("您添加的合作案例名称已存在");
					mResult.inErrorMessage(918547010);
					
				}
			}else{
//				mResult.setResultCode(0);
//				mResult.setResultMessage("合作案列名称长度在30个字符之内");
				mResult.inErrorMessage(918547011);
			}
		}
		
		return mResult;
	}
}
