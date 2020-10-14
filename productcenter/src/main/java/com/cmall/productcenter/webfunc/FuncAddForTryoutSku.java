package com.cmall.productcenter.webfunc;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebField;
import com.srnpr.zapweb.webmodel.MWebOperate;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加试用商品
 * 
 * @author 李国杰
 * 
 */
public class FuncAddForTryoutSku extends RootFunc {
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MWebOperate mOperate = WebUp.upOperate(sOperateUid);

		MWebPage mPage = WebUp.upPage(mOperate.getPageCode());

		MDataMap mAddMaps = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		// 定义插入数据库
		MDataMap mInsertMap = new MDataMap();
		// 定义组件判断标记
		boolean bFlagComponent = false;

		recheckMapField(mResult, mPage, mAddMaps);

		if (mResult.upFlagTrue()) {

			// 循环所有结构 初始化插入map
			for (MWebField mField : mPage.getPageFields()) {
				if (mField.getFieldTypeAid().equals("104005003")) {
					bFlagComponent = true;
				}
				if (mAddMaps.containsKey(mField.getFieldName())
						&& StringUtils.isNotEmpty(mField.getColumnName())) {
					
					String sValue = mAddMaps.get(mField.getFieldName());
					mInsertMap.put(mField.getColumnName(), sValue);
				}
			}
			if (bFlagComponent) {
				for (MWebField mField : mPage.getPageFields()) {
					if (mField.getFieldTypeAid().equals("104005003")) {
						WebUp.upComponent(mField.getSourceCode()).inAdd(mField,mDataMap);
					}
				}
			}
		}

		if (mResult.upFlagTrue()) {

			mInsertMap.put("create_time", DateUtil.getSysDateTimeString());   // 获取当前系统时间
			mInsertMap.put("update_time", DateUtil.getSysDateTimeString());   // 获取当前系统时间

			//先判断登录是否有效
			if(UserFactory.INSTANCE.create().getLoginName() == null || UserFactory.INSTANCE.create().getLoginName().equals("")){
				mResult.inErrorMessage(941901073);
			}else{
				mInsertMap.put("create_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
				mInsertMap.put("update_user", UserFactory.INSTANCE.create().getLoginName());  //获取当前登录名
			}
			
			//获取活动编号以及skuCode、试用库存数
			String appCode=mAddMaps.get("app_code");
			String skuCode=mAddMaps.get("sku_code");
			String initInventory=mAddMaps.get("init_inventory");			//初始库存，只能添加不能修改
			
			String startTime=mAddMaps.get("start_time");
			String endTime=mAddMaps.get("end_time");
			
			mInsertMap.put("tryout_inventory", initInventory);				//试用库存
				//检查库存
			if (null != initInventory && !"".equals(initInventory)) {
				
				//获得商品的实际库存数量
				MDataMap resultMap =  DbUp.upTable("v_pc_skuinfo_stockNum").one("sku_code",skuCode);
				String stockNum = (null == resultMap || resultMap.isEmpty())  ? "0" : resultMap.get("stock_num");

				int tNum = Integer.parseInt(initInventory == null ? "0" : initInventory);				//初始库存
				int sNum = Integer.parseInt(stockNum == null ? "0" : stockNum);								//实际库存
				
				if (sNum < tNum) {				//当实际库存小于初始试用库存时
					//返回提示信息“商品现有库存数为｛0｝，试用库存数不能大于现有库存！”
					mResult.setResultCode(941901086);
					mResult.setResultMessage(bInfo(941901086,sNum));
					return mResult;
				}
			}
			
			//检查商品重复
			if (checkInfo(skuCode,appCode,startTime,endTime)) {					
				//返回提示信息
				mResult.setResultCode(941901085);
				mResult.setResultMessage(bInfo(941901085));
				return mResult;
			}else{
				DbUp.upTable(mPage.getPageTable()).dataInsert(mInsertMap);
			}
		}

		if (mResult.upFlagTrue()) {
			
			mResult.setResultMessage(bInfo(969909001));
		}
		return mResult;

	}
	/**
	 * 校验在数据库中相同APP下的同一时间段内是否存在相同sku
	 * @param skuCode,appCode,startTime,endTime
	 * @return
	 */
	private boolean checkInfo(String skuCode,String appCode,String startTime,String endTime) {
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("start_time",startTime);
		mDataMap.put("end_time",endTime);
		mDataMap.put("app_code",appCode);
		mDataMap.put("sku_code",skuCode);
		 
		Map<String, Object> map=DbUp.upTable("oc_tryout_products").dataSqlOne("SELECT sku_code FROM oc_tryout_products WHERE !(start_time>:end_time OR end_time<:start_time) and app_code=:app_code and sku_code=:sku_code ",mDataMap);
		if(null != map){
			return true;
		}
		return false;
	}
}
