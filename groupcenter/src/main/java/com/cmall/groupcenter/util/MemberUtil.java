package com.cmall.groupcenter.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class MemberUtil {

	public String getMobile(String memberCode) {
		String mobile_phone = "";
		MDataMap map = DbUp.upTable("mc_extend_info_groupcenter").one("member_code", memberCode);
		if (map != null) {
			mobile_phone = map.get("mobile");
		}
		return mobile_phone;
	}

	public String getNikeName(String memberCode) {
		String nikeName = "";
		MDataMap map = DbUp.upTable("mc_extend_info_groupcenter").one("member_code", memberCode);
		if (map != null) {
			nikeName = map.get("nickname");
		}
		return nikeName;
	}
	
	public String getPic(String memberCode) {
		String pic = "";
		MDataMap map = DbUp.upTable("mc_extend_info_groupcenter").one("member_code", memberCode);
		if (map != null) {
			pic = map.get("head_icon_url");
		}
		return pic;
	}
	
	public String getLoginName(String memberCode) {
		String loginName = "";
		MDataMap map = DbUp.upTable("mc_login_info").one("member_code", memberCode);
		if (map != null) {
			loginName = map.get("login_name");
		}
		return loginName;
	}
	
	
	public MDataMap getPublisherInfo(String memberCode) {
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("member_code", memberCode);
		List<MDataMap> list = DbUp.upTable("mc_extend_info_groupcenter").queryAll("", "", "", mDataMap);
		if(list==null || list.size()==0) {
			MDataMap map = new MDataMap();
			map.put("mobile", this.getLoginName(memberCode));
			map.put("nickname", "");
			map.put("head_icon_url", "");
			return map;
		} else {
			list.get(0).put("mobile", this.getLoginName(memberCode));
			return list.get(0);
		}
	}
}