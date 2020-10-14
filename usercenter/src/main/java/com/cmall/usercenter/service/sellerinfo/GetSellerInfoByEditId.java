package com.cmall.usercenter.service.sellerinfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 根据编辑人员ID 获取店铺信息
 * 
 * @author hxd
 */

public class GetSellerInfoByEditId extends RootApi<SellerInfoList, Inputparam>
{
	public SellerInfoList Process(Inputparam inputParam, MDataMap mRequestMap)
	{
		SellerInfoList result = new SellerInfoList();
		if(StringUtils.isBlank(inputParam.getEditId()))
		{
			result.setResultCode(959701032);
			result.setResultMessage(bInfo(959701032));
			return result;
		}
		List<SellerInfo> infos = new ArrayList<SellerInfo>();
		SellerInfo info = new SellerInfo();
        MDataMap map = new MDataMap();
        map.put("editId", inputParam.getEditId());
		List<MDataMap> list = DbUp.upTable("uc_sellerinfo").queryByWhere("editId",inputParam.getEditId());
		for (MDataMap mp1 : list)
		{
			info = new SerializeSupport<SellerInfo>().serialize(mp1,
					new SellerInfo());
			infos.add(info);
		}
		result.setList(infos);
		return result;
	}
}
