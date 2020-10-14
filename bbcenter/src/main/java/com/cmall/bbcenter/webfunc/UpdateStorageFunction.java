package com.cmall.bbcenter.webfunc;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 更新入库数据
 * 
 * @author hxd
 * 
 */
public class UpdateStorageFunction extends RootFunc
{
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap)
	{
		MWebResult mResult = new MWebResult();
		if (validateStatus(mDataMap.get("udd")))
		{
			mResult.setResultMessage("已完全入库，入库数量不能修改！");
			return mResult;
		} else
		{
			MDataMap mp = new MDataMap();
			mp.put("zid", mDataMap.get("udd"));
			mp.put("storage_number", mDataMap.get("sData"));
			mp.put("in_number", String.valueOf(String.valueOf(Integer
					.valueOf(mDataMap.get("sData"))
					- currentCount(mDataMap.get("udd")))));
			DbUp.upTable("bc_purchase_detail").dataUpdate(mp,
					"storage_number,in_number", "zid");
			return mResult;
		}

	}

	private boolean validateStatus(String zid)
	{
		MDataMap mp = new MDataMap();
		mp.put("sn", zid);
		mp.put("status", "完全入库");
		int ct = DbUp.upTable("bc_purchaseinfo_in_storage").dataCount(
				"status=:status  AND sn='"+zid+"'", mp);
		if (ct > 0)
			return true;
		else
			return false;
	}

	private int currentCount(String zid)
	{
		MDataMap mp = DbUp.upTable("bc_purchaseinfo_in_storage").one("sn", zid);
		if(null == mp)
			return 0;
		else
			return Integer.valueOf(mp.get("in_storage_count"));
	}
}
