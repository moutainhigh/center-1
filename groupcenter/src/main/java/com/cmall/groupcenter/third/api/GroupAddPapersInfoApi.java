package com.cmall.groupcenter.third.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.GroupConstant.PapersEnum;
import com.cmall.groupcenter.account.model.AddPapersInput;
import com.cmall.groupcenter.support.GroupAccountSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 添加证件信息
 * 
 * @author chenxk
 * 
 */
public class GroupAddPapersInfoApi extends RootApiForToken<RootResultWeb, AddPapersInput> {

	public RootResultWeb Process(AddPapersInput inputParam, MDataMap mRequestMap) {

		RootResultWeb rootResultWeb = new RootResultWeb();

		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		// 锁定账户编号
		String sLockCode = WebHelper.addLock(100, sAccountCode);
				
		if (StringUtils.isNotEmpty(sLockCode)) {
			GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
			groupAccountSupport.checkAndCreateGroupAccount(sAccountCode);
			
			//获取证件类型
			inputParam.setPapersType(PapersEnum.getCardTypeByCno(Integer.valueOf(inputParam.getPapersType())));
			int num = DbUp.upTable("gc_member_papers_info").count("account_code", sAccountCode);
			if(num>0){
				//更新数据
				MDataMap param = new MDataMap();
				//param.put("member_code", getUserCode());
				param.put("account_code", sAccountCode);
				param.put("user_name",inputParam.getUserName());
				param.put("papers_type",inputParam.getPapersType());
				param.put("papers_code",inputParam.getPapersCode());
				param.put("create_time",FormatHelper.upDateTime());
				DbUp.upTable("gc_member_papers_info").dataUpdate(param, "user_name,papers_type,papers_code,create_time", "account_code");
			}else{
				// 开始插入数据
				DbUp.upTable("gc_member_papers_info").insert("uid",
						WebHelper.upUuid(), 
						//"member_code", getUserCode(),
						"account_code", sAccountCode, "user_name",
						inputParam.getUserName(),"papers_type",
						inputParam.getPapersType(), "papers_code",
						inputParam.getPapersCode(), "create_time",
						FormatHelper.upDateTime());
			}
			// 解鎖
			WebHelper.unLock(sLockCode);
		}		
				
		return rootResultWeb;
	}
}
