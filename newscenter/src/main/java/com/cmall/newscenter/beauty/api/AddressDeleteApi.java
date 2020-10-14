package com.cmall.newscenter.beauty.api;

import java.util.List;

import com.cmall.newscenter.beauty.model.AddressDeleteResult;
import com.cmall.newscenter.beauty.model.AddressDeleteInput;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 删除地址信息（删除默认地址  将最新修改的置为默认地址）
 * 
 * @author yangrong date 2014-9-10
 * @version 1.0
 */
public class AddressDeleteApi extends
		RootApiForToken<AddressDeleteResult, AddressDeleteInput> {

	public AddressDeleteResult Process(AddressDeleteInput inputParam,
			MDataMap mRequestMap) {

		AddressDeleteResult result = new AddressDeleteResult();

		if (result.upFlagTrue()) {

			MDataMap map = DbUp.upTable("nc_address").one("address_id",inputParam.getAddress(),"app_code",getManageCode());
			//fix bug .zht 2016/11/23
			if(null == map || map.size() == 0 ) {
				result.inErrorMessage(934205180);
				return result;
			}
			
			//删除的是非默认地址  直接删除不做操作
			if (map.get("address_default").equals("0")) {
				
				DbUp.upTable("nc_address").delete("address_id",inputParam.getAddress(), "address_code", getUserCode(),"app_code",getManageCode());
				
			} else {//删除的是默认地址  将最新修改的置为默认地址
				
				int count = DbUp.upTable("nc_address").delete("address_id",inputParam.getAddress(), "address_code", getUserCode(),"app_code",getManageCode());

				if (count == 1) {
					
					// 查询地址信息
					MDataMap mDataMap = new MDataMap();
					mDataMap.put("app_code", getManageCode());
					mDataMap.put("address_code", getUserCode());
					
					List<MDataMap> list = DbUp.upTable("nc_address").queryAll("", "-update_time", "", mDataMap);

					if (list != null && list.size() != 0) {

						MDataMap mAddressMap = list.get(0);
						mAddressMap.put("address_default", "1");
						mAddressMap.put("update_time",DateUtil.getSysDateTimeString());

						DbUp.upTable("nc_address").update(mAddressMap);
					}
				}
			}

		}

		return result;
	}

}
