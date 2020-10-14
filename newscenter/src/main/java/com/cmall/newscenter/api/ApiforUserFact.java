package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.cmall.newscenter.model.UserFact;
import com.cmall.newscenter.model.UserFactInput;
import com.cmall.newscenter.model.UserFactResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 用户验证真伪记录
 * @author guz
 * date 2014-11-02
 * @version 1.0
 */
public class ApiforUserFact extends RootApiForToken<UserFactResult, UserFactInput> {

	public UserFactResult Process(UserFactInput inputParam, MDataMap mRequestMap) {
		String user = getUserCode(); 
		UserFactResult result = new UserFactResult();
		if (result.upFlagTrue()) {
			MDataMap securitymap = DbUp.upTable("nc_security_user").one("security_user",getUserCode());
			if(securitymap != null){
				String security_code = securitymap.get("security_code");
				if(!StringUtils.isEmpty(security_code)){
					List<MDataMap> mDataMap = new ArrayList<MDataMap>();
					mDataMap =  DbUp.upTable("nc_securitycode_details").queryByWhere("security_code",security_code);
					for (MDataMap detailMap : mDataMap) {
						if(detailMap !=null){
							UserFact userFact = new UserFact();
							userFact.setProduct_id(detailMap.get("security_itemnumber"));
							userFact.setProduct_name(detailMap.get("security_itemname"));
							userFact.setSecurity_time(securitymap.get("security_time"));
							userFact.setBuy_time("");
							result.getUserFact().add(userFact);
						}
					}
				}
			}
		}
		return result;
	}

}
