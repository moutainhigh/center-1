package com.cmall.newscenter.beauty.api;

import com.cmall.newscenter.beauty.model.AddressSelectInput;
import com.cmall.newscenter.beauty.model.AddressSelectResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 修改默认地址
 * 
 * @author yangrong date 2014-9-10
 * @version 1.0
 */
public class AddressSelectApi extends RootApiForToken<AddressSelectResult, AddressSelectInput> {

	public AddressSelectResult Process(AddressSelectInput inputParam,MDataMap mRequestMap) {

		AddressSelectResult result = new AddressSelectResult();

		if (result.upFlagTrue()) {

			MDataMap mDataMap = DbUp.upTable("nc_address").one("address_id",inputParam.getAddress());

			if (mDataMap != null) {

				/* 是否默认 */
				mDataMap.put("address_default", "1");

				MDataMap wmDataMap = new MDataMap();

				wmDataMap.put("address_code", getUserCode());

				wmDataMap.put("address_default", "0");

				/* 更新用户默认地址为空 */
				DbUp.upTable("nc_address").dataUpdate(wmDataMap, "","address_code");

				/* 更新为默认地址 */
				DbUp.upTable("nc_address").update(mDataMap);

			}
		}

		return result;
	}

}
