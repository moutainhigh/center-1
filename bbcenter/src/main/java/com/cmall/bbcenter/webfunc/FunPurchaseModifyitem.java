package com.cmall.bbcenter.webfunc;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 修改采购单金额项
 * @author GaoYang
 *
 */
public class FunPurchaseModifyitem  extends RootFunc{

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		MWebResult mResult = new MWebResult();
		
		try{
			//计算后的金额项总金额
			double totalMoneyPrice = 0;
			
			//获取采购商编码
			String supplierCode = mDataMap.get("zw_f_supplier_code");
			//获取采购单编码
			String cpurchaseCode = mDataMap.get("zw_f_purchaseorder_code");
			//获取金额项名称
			String cpurchaseMoneycode = mDataMap.get("zw_f_cpurchase_moneycode");
			//获取金额项说明
			String cpurchaseMoneynote = mDataMap.get("zw_f_cpurchase_moneynote");
			//获取金额
			String cpurchaseMoneyprice = mDataMap.get("zw_f_cpurchase_moneyprice");
			//采购员
			String createUser = UserFactory.INSTANCE.create().getUserCode();
			//采购员姓名
			String loginName = UserFactory.INSTANCE.create().getLoginName();
			//采购员真是姓名
			String userRealName = UserFactory.INSTANCE.create().getRealName();
			if(StringUtils.isNotEmpty(userRealName)){
				loginName = userRealName;
			}
			
			//获取采购单状态，只有状态为“审批中:449746420002”，“待结算:449746420005”时，才可以修改金额项
			String sFields = "order_status,purchase_money";
			MDataMap oneMap = DbUp.upTable("bc_purchase_order").oneWhere(sFields, "", "", "purchaseorder_code", cpurchaseCode);
			String orderStatus= oneMap.get("order_status");
			
			if("449746420002".equals(orderStatus) || "449746420005".equals(orderStatus)){
				//重新计算采购单的采购金额
				String money = oneMap.get("purchase_money");
				totalMoneyPrice = addMoney(cpurchaseMoneyprice,money);
				
				//更新采购表里的采购金额
				updatePurchaseOrder(cpurchaseCode,totalMoneyPrice);
				
				//把修改的数据插入的采购明细表中
				insertPurchaseDetail(cpurchaseCode,supplierCode,cpurchaseMoneycode,cpurchaseMoneynote,cpurchaseMoneyprice,"1","449746440002",loginName);
				
			}else{
				mResult.setResultMessage(bInfo(909401002,orderStatus));
				mResult.setResultCode(909401002);
			}
			
		}catch(Exception e){
			mResult.setResultMessage(bInfo(909401001));
			mResult.setResultCode(909401001);
		}
		return mResult;
	}
	
	/**
	 * 更新采购表里的采购金额
	 * @param cpurchaseCode
	 * @param totalMoneyPrice
	 */
	private void updatePurchaseOrder(String cpurchaseCode,
			double totalMoneyPrice) {
		
		MDataMap updDatamap = new MDataMap();
		//以"换货单号"为单位更新
		updDatamap.put("purchaseorder_code", cpurchaseCode);
		updDatamap.put("purchase_money", String.valueOf(totalMoneyPrice));
		DbUp.upTable("bc_purchase_order").dataUpdate(updDatamap, "purchase_money", "purchaseorder_code");
		
	}

	/**
	 * 相加计算
	 * @param d1
	 * @param d2
	 * @return
	 */
	private double addMoney(String d1, String d2) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return b1.add(b2).doubleValue();
	}

	/**
	 * 向采购单明细表里录入数据
	 * @param cpurchaseCode
	 * @param supplierCode
	 * @param cpurchaseMoneycode
	 * @param cpurchaseMoneynote
	 * @param cpurchaseMoneyprice
	 * @param moneyItemNumber
	 * @param moneyItemType
	 * @param createUser
	 */
	private void insertPurchaseDetail(String cpurchaseCode,
			String supplierCode, String cpurchaseMoneycode,
			String cpurchaseMoneynote, String cpurchaseMoneyprice,
			String moneyItemNumber, String moneyItemType, String createUser) {
		MDataMap mInsertMap = new MDataMap();
		UUID uuid = UUID.randomUUID();
		mInsertMap.put("uid", uuid.toString().replace("-", ""));
		mInsertMap.put("purchaseorder_code", cpurchaseCode);
		mInsertMap.put("supplier_code", supplierCode);
		mInsertMap.put("goods_code", cpurchaseMoneycode);
		mInsertMap.put("goods_name", cpurchaseMoneynote);
		mInsertMap.put("goods_price", cpurchaseMoneyprice);
		mInsertMap.put("goods_number", moneyItemNumber);
		mInsertMap.put("goods_type", moneyItemType);
		mInsertMap.put("create_user", createUser);
		mInsertMap.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("bc_purchase_detail").dataInsert(mInsertMap);
	}

}
