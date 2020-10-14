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
 * @author :hexd
 * 2013-9-16
 *ordercenter
 */
public class ReturnGoodsService  extends RootFunc
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
	
	/**
	 * 获取可用退货的商品数量
	 * @param order_code
	 * @param sku_code
	 * @return
	 */
	public int getAchangeNum(String order_code,String sku_code) {
		int count = Integer.valueOf(DbUp.upTable("oc_order_achange").oneWhere("IFNULL(sum(oac_num),0) num", "", "", "order_code",order_code,"sku_code",sku_code,"available","0","oac_type","4497477800030001").get("num"));
		int count_all=Integer.valueOf(DbUp.upTable("oc_orderdetail").one("order_code",order_code,"sku_code",sku_code).get("sku_num"));
		return count_all-count;
	}
	
	/**
	 * 获取可用换货的商品数量
	 * @param order_code
	 * @param sku_code
	 * @return
	 */
	public int getAchangeNumC(String order_code,String sku_code) {
		int count = Integer.valueOf(DbUp.upTable("oc_order_achange").oneWhere("IFNULL(sum(oac_num),0) num", "", "", "order_code",order_code,"sku_code",sku_code,"available","0","oac_type","4497477800030001").get("num"));
		int count1 = Integer.valueOf(DbUp.upTable("oc_order_achange").oneWhere("IFNULL(sum(oac_num),0) num", "", "", "order_code",order_code,"sku_code",sku_code,"oac_status","4497477800040001","oac_type","4497477800030003").get("num"));
		int count_all=Integer.valueOf(DbUp.upTable("oc_orderdetail").one("order_code",order_code,"sku_code",sku_code).get("sku_num"));
		return count_all-count-count1;
	}
}
