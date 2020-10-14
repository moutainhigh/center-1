package com.cmall.bbcenter.webfunc;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 添加采购单数据
 * @author GaoYang
 *
 */
public class FunPurchaseOrderAdd extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		
		
		MWebResult mResult = new MWebResult();
		try{
			if (mResult.upFlagTrue()) {
				
				//采购单总体的采购金额
				double purchaseMoney = 0;
				//实物商品的总采购金额
				double totalSkuPrice = 0;
				//金额项的总金额
				double totalMoneyPrice = 0;
				
				//采购员
				String createUser = UserFactory.INSTANCE.create().getUserCode();
				//String createUser = mDataMap.get("zw_f_create_user");
				//采购员姓名
				String loginName = UserFactory.INSTANCE.create().getLoginName();
				//采购员真是姓名
				String userRealName = UserFactory.INSTANCE.create().getRealName();
				if(StringUtils.isNotEmpty(userRealName)){
					loginName = userRealName;
				}
				
				//供应商编码
				String supplierCode = mDataMap.get("zw_f_supplier_code");
				//结算方式
				String balanceMode = mDataMap.get("zw_f_balance_mode");
				//添加的商品项
				String productItems = mDataMap.get("zw_f_productitems");
				//添加的金额项
				String moneyItems = mDataMap.get("zw_f_moneyitems");
				
				//生成采购单号
				String purchaseorderCode = WebHelper.upCode("BB");
				
				//计算商品项的采购金额并且向采购单明细表里录入商品项数据
				if(!productItems.trim().equals("")){
					String[] pary = productItems.split(";");
					if(pary.length >0){
						for(int i=0;i<pary.length;i++){
							if(!pary[i].trim().equals("")){
								String[] items = pary[i].split("##");
								if(items.length != 4){
									mResult.setResultMessage(bInfo(909401000));
									mResult.setResultCode(909401000);
									return mResult;
								}else{
									//产品编号
									String skuCode = items[0];
									//产品编号
									String skuName = items[1];
									//进价
									String skuPrice = items[2];
									//采购数量
									String skuNumber = items[3];
									
									//商品的采购金额
									totalSkuPrice = addMoney(totalSkuPrice,mulMoney(Double.valueOf(skuPrice), Double.valueOf(skuNumber)));
									
									//向采购单明细表里录入商品项数据
									insertPurchaseDetail(purchaseorderCode,supplierCode,skuCode,skuName,skuPrice,skuNumber,"449746440001",loginName);
									
								}
							}
						}
					}
				}
				
				//计算金额项的采购金额并且向采购单明细表里录入金额项数据
				if(!moneyItems.trim().equals("")){
					String[] mary = moneyItems.split(";");
					if(mary.length >0){
						for(int i=0;i<mary.length;i++){
							if(!mary[i].trim().equals("")){
								String[] items = mary[i].split("##");
								if(items.length != 3){
									mResult.setResultMessage(bInfo(909401000));
									mResult.setResultCode(909401000);
									return mResult;
								}else{
									//金额项名称(code)
									String moneyCode = items[0];
									//金额项说明
									String moneyNote = items[1];
									//金额项的金额
									String moneyPrice = items[2];
									
									//金额项的总体金额							
									totalMoneyPrice = addMoney(totalMoneyPrice,Double.valueOf(items[2]));
									
									//向采购单明细表里录入金额项数据
									insertPurchaseDetail(purchaseorderCode,supplierCode,moneyCode,moneyNote,moneyPrice,"1","449746440002",loginName);
								}
							}
						}
					}
				}
				
				//计算完毕后的采购单总体采购金额
				purchaseMoney = addMoney(totalSkuPrice,totalMoneyPrice);
				//录入采购单主表数据
				MDataMap mInsertMap = new MDataMap();
				UUID uuid = UUID.randomUUID();
				mInsertMap.put("uid", uuid.toString().replace("-", ""));
				mInsertMap.put("purchaseorder_code", purchaseorderCode);
				mInsertMap.put("create_user", createUser);
				mInsertMap.put("supplier_code", supplierCode);
				//采购单状态默认为“审批中”
				mInsertMap.put("order_status", "449746420002");
				//采购单类型默认为“采购单”类型
				mInsertMap.put("order_type", "449746410001");
				mInsertMap.put("purchase_money", String.valueOf(purchaseMoney));
				mInsertMap.put("purchase_time", DateUtil.getSysDateTimeString());
				mInsertMap.put("balance_mode", balanceMode);
				mInsertMap.put("create_user_realname", loginName);
				
				DbUp.upTable("bc_purchase_order").dataInsert(mInsertMap);
				
				
				//记录采购单操作日志
				MDataMap logInsertMap = new MDataMap();
				UUID logUid = UUID.randomUUID();
				logInsertMap.put("uid", logUid.toString().replace("-", ""));
				logInsertMap.put("purchaseorder_code", purchaseorderCode);
				logInsertMap.put("log_info", "创建采购单");
				logInsertMap.put("create_time", DateUtil.getSysDateTimeString());
				logInsertMap.put("create_user", loginName);
				DbUp.upTable("bc_purchase_log").dataInsert(logInsertMap);
			}
		}catch(Exception e){
			mResult.setResultMessage(bInfo(909401000));
			mResult.setResultCode(909401000);
		}
		
		return mResult;
	}

	/**
	 * 向采购单明细表里录入数据
	 * @param purchaseorderCode
	 * @param supplierCode
	 * @param goodsCode
	 * @param goodsName
	 * @param goodsPrice
	 * @param goodsNumber
	 * @param goodsType
	 * @param createUser
	 */
	private void insertPurchaseDetail(String purchaseorderCode,
			String supplierCode, String goodsCode, String goodsName,
			String goodsPrice, String goodsNumber, String goodsType, String createUser) {
		
		MDataMap mInsertMap = new MDataMap();
		UUID uuid = UUID.randomUUID();
		mInsertMap.put("uid", uuid.toString().replace("-", ""));
		mInsertMap.put("purchaseorder_code", purchaseorderCode);
		mInsertMap.put("supplier_code", supplierCode);
		mInsertMap.put("goods_code", goodsCode);
		mInsertMap.put("goods_name", goodsName);
		mInsertMap.put("goods_price", goodsPrice);
		mInsertMap.put("goods_number", goodsNumber);
		mInsertMap.put("goods_type", goodsType);
		mInsertMap.put("create_user", createUser);
		mInsertMap.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("bc_purchase_detail").dataInsert(mInsertMap);
	}

	/**
	 * 相乘计算
	 * @param skuPrice
	 * @param skuNumber
	 * @return
	 */
	private double mulMoney(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
       return b1.multiply(b2).doubleValue();
	}


	/**
	 * 相加计算
	 * @param totalSkuPrice
	 * @param d
	 */
	private double addMoney(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(d1);
		BigDecimal b2 = new BigDecimal(d2);
		return b1.add(b2).doubleValue();
	}

}
