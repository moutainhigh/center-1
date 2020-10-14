package com.cmall.bbcenter.webfunc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.bbcenter.model.PurchaseinfoInStorage;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 入库
 * @author hxd
 * 
 */
public class Instorage extends RootFunc
{
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap)
	{
		MWebResult mResult = new MWebResult();
		String sData = mDataMap.get("sData");
		String[] aa = sData.split("#");
		List<PurchaseinfoInStorage> list = new ArrayList<PurchaseinfoInStorage>();
		for (int i = 0; i < aa.length; i++)
		{
			PurchaseinfoInStorage inStorage = new PurchaseinfoInStorage();
			String[] bb = aa[i].split(",");
			inStorage.setZidd(bb[0]);
			inStorage.setPurchase_no(bb[1]);
			inStorage.setProduct_name(bb[2]);
			inStorage.setPurchase_count(Integer.valueOf(bb[3]));
			inStorage.setIn_storage_count(Integer.valueOf(bb[4]));
			inStorage.setSupplier_name(bb[5]);
			list.add(inStorage);
		}

		// 先行判断是否有完全入库的数据，如果有直接退出
		boolean flag = false;
		//入库数量标识
		boolean flag1 = false;
		//全为部分入库
		boolean flag2 = false;
		for (int k = 0; k < list.size(); k++)
		{
			// 根据采购单号 判断是否已经完全入库，有的话直接跳出
			if (validateInstorStatus(list.get(k).getZidd()))
				flag = true;
		}
		for (int k = 0; k < list.size(); k++)
		{
			// 根据采购单号 判断是否已经完全入库，有的话直接跳出
			if(list.get(k).getIn_storage_count() > list.get(k).getPurchase_count())
				flag = true;
		}
		
		for (int k = 0; k < list.size(); k++)
		{
			// 部分入库
			if (validatePartInstorStatus(list.get(k).getZidd()))
			flag2 = true;
		}
		
		if (flag == true)
		{// 根据采购单号 判断是否已经完全入库，有的话直接跳出
			mResult.setResultObject("returnMsg('"+ 969912001+ "')");
			mResult.setResultType("116018010");
			return mResult;

		}
		else if(flag == false)
		{
			// 首次入库 入库状态均为""
			insertInstorageMess(list);
			// 部分入库
		}
		else if (flag1 == true)
		{
			//入库数量错误
			mResult.setResultObject("returnMsg('"+ 969912002+ "')");
			mResult.setResultType("116018010");
			return mResult;

		}
		 else if (flag2 == false)
		{
			//数据不一致！
			mResult.setResultObject("returnMsg('"+ 969912003+ "')");
			mResult.setResultType("116018010");
			return mResult;

		}
		else if (flag2 == true)
		{
			//全为部分入库，根据sn更新入库数据
			 updatePartStorage(list);
			return mResult;

		}
		return mResult;
	}

	/**
	 * 采购单入库
	 */
	private void insertInstorageMess(List<PurchaseinfoInStorage> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			MDataMap logInsertMap = new MDataMap();
			MDataMap map = new MDataMap();
			map.put("product_no", list.get(i).getProduct_no());
			map.put("product_name", list.get(i).getProduct_name());
			map.put("supplier_name", list.get(i).getSupplier_name());
			map.put("in_storage_count",
					String.valueOf(list.get(i).getIn_storage_count()));
			map.put("purchase_count",
					String.valueOf(list.get(i).getPurchase_count()));
			map.put("purchase_no", list.get(i).getPurchase_no());
			map.put("ceate_time", DateUtil.getNowTime());
			map.put("sn", list.get(i).getZidd());
			if (StringUtils.isNotBlank(String.valueOf(list.get(i)
					.getIn_storage_count()))
					&& (list.get(i).getIn_storage_count() == list.get(i)
							.getPurchase_count()))
			{
				map.put("status", "完全入库");
			} else
				map.put("status", "部分入库");
			
			UUID logUid = UUID.randomUUID();
			logInsertMap.put("uid", logUid.toString().replace("-", ""));
			logInsertMap.put("purchaseorder_code", list.get(i).getPurchase_no());
			logInsertMap.put("log_info", map.get("status"));
			logInsertMap.put("create_time", DateUtil.getSysDateTimeString());
			logInsertMap.put("create_user", UserFactory.INSTANCE.create().getLoginName());
			DbUp.upTable("bc_purchase_log").dataInsert(logInsertMap);

			DbUp.upTable("bc_purchaseinfo_in_storage").dataInsert(map);
		}
	}

	/**
	 * 根据采购单号判断是否完全入库
	 */
	private boolean validateInstorStatus(String orderCode)
	{
		int tmp = DbUp.upTable("bc_purchaseinfo_in_storage").dataCount(
				"sn='"
				+ orderCode
				+ "' and status='"
				+ "完全入库'", new MDataMap());
		if (tmp > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * 根据采购单号判断是否部分入库
	 */
	private boolean validatePartInstorStatus(String orderCode)
	{
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("sn", orderCode);
		mWhereMap.put("status", "部分入库");
		int tmp = DbUp.upTable("bc_purchaseinfo_in_storage").dataCount(
				"sn", mWhereMap);
		if (tmp > 0)
			return true;
		else
			return false;
	}
	
	/**
	 * 部分入库数据，进行入库操作
	 */
	private void updatePartStorage(List<PurchaseinfoInStorage> list)
	{
		for (int i = 0; i < list.size(); i++)
		{
			MDataMap mWhereMap = new MDataMap();
			mWhereMap.put("sn", list.get(i).getZidd());
			mWhereMap.put("in_storage_count", String.valueOf(list.get(i).getIn_storage_count()));
			DbUp.upTable("bc_purchaseinfo_in_storage").dataUpdate(mWhereMap, "in_storage_count", "sn");
		}
	}

}
