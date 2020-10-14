package com.cmall.ordercenter.service.goods;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/**
 * 修改退货单
 * @author :shyz
 * 2014-07-03
 *   
 */
public class PlatformReturnGoodsService  extends RootFunc
{
	public static final String UPDATE_RETURNGOODS_LIST = "oc_return_goods";
	public static final String INSERT_RETURNGOODS_LOG = "lc_return_goods_status";
	/**
	 * 更新退货单
	 * @param mDataMap
	 * @return
	 */
	public boolean updateGoodsInfo(MDataMap mDataMap)
	{
		boolean flag = true;
		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);
		try {
			DbUp.upTable(UPDATE_RETURNGOODS_LIST).update(mAddMaps);
			insertRetunGoodsLog(mAddMaps);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 插入日志记录
	 * @param mDataMap
	 * @return
	 */
	public boolean insertRetunGoodsLog(MDataMap mDataMap)
	{
		
		boolean flag = true;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowTime = df.format(new Date());
			MDataMap dataMap = new MDataMap();
			dataMap.put("return_no", mDataMap.get("return_code"));
			dataMap.put("status", mDataMap.get("status"));
			dataMap.put("create_time", nowTime);
			DbUp.upTable(INSERT_RETURNGOODS_LOG).dataInsert(dataMap);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
    /**
     * 执行默认方法
     */
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap)
	{
		MWebResult mResult = new MWebResult();
		try {
			if (mResult.upFlagTrue() == true) {
				boolean flag = updateGoodsInfo(mDataMap);
				if (flag == false) {
					mResult.inErrorMessage(909701004);
				}
			}
			mResult.setResultMessage(bInfo(969909001));
		} catch (Exception e) {
			mResult.inErrorMessage(909701004);
		}
		return mResult;
	}
}
